// TerrainCooker
//
// Cook input data for consumption by the Terrain Server.
//
// input: heightfield data file of floats [xres][yres][floats]
//        image data file, tile dimensions, number of tiles, size of image texturs (2^n),
//               number of verts in one tile dimension.
// output: data files and image files for each tile, data_X_Y.dat, image_X_Y.jpg
//
// usage:
//   java terrain.TerrainCooker -d mc.data -i mc.image -x 10 -y 10 -s 1024
//

package terrain;

import javax.imageio.ImageIO;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.DataOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.File;
import java.awt.image.BufferedImage;
import java.awt.Color;

class TerrainCooker {

  private String datafile;
  private int dataXres = 0;
  private int dataYres = 0;
  private String imagefile;
  private int imageXres = 0;
  private int imageYres = 0;
  private int xtiles;
  private int ytiles;
  private int image_size;
  private float[][] data;
  private float scale;
  private BufferedImage image;

  public TerrainCooker(String datafile, String imagefile, int xtiles, int ytiles, int image_size, float scale) {
    this.datafile = datafile;
    this.imagefile = imagefile;
    this.xtiles = xtiles;
    this.ytiles = ytiles;
    this.image_size = image_size;
    this.scale = scale;
  } 

  // data input strategies
  public boolean ReadImageAsData() {
    BufferedImage bi = null;
    int cnt = 0;
    int ttl = 0;
    try {
        bi = ImageIO.read(new File(datafile));
        dataXres = bi.getWidth();
        dataYres = bi.getHeight();
        data = new float[dataXres ][dataYres ];
        int minh = 10240;
        for (int j = 0; j < dataYres ; j++) {
          for (int i = 0; i < dataXres ; i++) {
            Integer rgb = bi.getRGB(i, j);
            Color c = new Color(rgb);
            Integer r = c.getRed();
            Integer g = c.getGreen();
            Integer b = c.getBlue();
            Integer sumc = g + r + b;
            if ((i % 77 == 0 ) && (j % 101 == 0))
            System.out.println("RGB: R" + r + " B:" + b + " G:" + g + " S:" + sumc);
            cnt += sumc;
            ttl++;
            if (sumc > 2000) sumc = 2000;
            if (sumc < minh) {
               minh = sumc;
            }
            data[i][j] = sumc.floatValue();
          }
        }
        int avg = cnt / ttl;
        System.out.println("Sum: " + cnt + " cnt: " + ttl + " avg: " + avg);
        for (int j = 0; j < dataYres ; j++) {
          for (int i = 0; i < dataXres ; i++) {
            data[i][j] = (minh - data[i][j]) * scale;
            if ((i % 77 == 0 ) && (j % 101 == 0))
               System.out.println("px: " + data[i][j]);
          }
        }
        System.out.println("Read image file " + datafile + " with resolution [" + dataXres + ", " + dataYres + "].");
        return true;
    } catch (IOException e) {
      System.out.println("Image Data File failure: " + e);
    }
    return false;
  }

  public boolean ReadDataFile() {
    DataInputStream ds = null;
    try {
      try {
        ds = new DataInputStream(new FileInputStream(datafile)); 
        dataXres = ds.readInt();
        dataYres = ds.readInt();
        data = new float[dataXres ][dataYres ];
        for (int j = 0; j < dataYres ; j++) {
          for (int i = 0; i < dataXres ; i++) {
            data[i][j] = ds.readFloat() * scale;
          }
        }
      } finally {
        if (ds != null) ds.close();
      }
      System.out.println("Read data file " + datafile + " with resolution [" + dataXres + ", " + dataYres + "].");
      return true;
    } catch (IOException e) {
      System.out.println("Data File failure: " + e);
    }
    return false;
  }


  public boolean GenerateFunction(String datafile) {
    dataXres = 1000;
    dataYres = 1000;
        data = new float[dataXres][dataYres];
        for (int j = 0; j < dataYres ; j++) {
          for (int i = 0; i < dataXres ; i++) {
            double result = 0.0;
            if (datafile.equals("sin")) {
              double theta = (float) j / (float) dataYres * Math.PI * 4.0;
              double gamma = (float) i / (float) dataXres * Math.PI * 4.0;
              result = Math.cos(theta) * Math.sin(gamma);
              result *= result;
              result *= scale;
              System.out.println("Result: " + result);
            }
            data[i][j] = (float) result;
          }
        }
      System.out.println("Generated data file " + datafile + " with resolution [" + dataXres + ", " + dataYres + "].");
    return true;
  }
  // end strategies

  public boolean WriteImageFiles() {
    // we don't care, just interpolate.  
    int tileXres = image_size;
    int tileYres = image_size;
    float mmmy = ((float) imageYres / (float) ytiles);  // 80 * 10 * 10 = 8000 or 1/8000
    float mmmx = ((float) imageXres / (float) xtiles);  // 40 * 10 * 10 = 4000 or 1/4000
    System.out.println("tilexysize :" + image_size);
    System.out.println("imageXres :" + imageXres);
    System.out.println("imageYres :" + imageYres);
    System.out.println("xtiles :" + xtiles);
    System.out.println("ytiles :" + ytiles);
    System.out.println("mmmx :" + mmmx + "mmmy: " + mmmy);
    for (int tY = 0; tY < ytiles; tY++) {
      for (int tX = 0; tX < xtiles; tX++) {
        // String imageout = String.format(TerrainConstants.imageFormat, tY, tX);  // to look nice in nautulis.
        String imageout = String.format(TerrainConstants.imageFormat, tX, tY);
        BufferedImage bi = new BufferedImage(image_size, image_size, BufferedImage.TYPE_INT_RGB);
        FileOutputStream ds = null;
        try {
          try {
            ds = new FileOutputStream(imageout); 
            for (int mY = 0; mY < tileYres; mY++) {
              float yPos = (float) tY * (float) imageYres / (float) ytiles + (float) mY * mmmy / (float) tileYres;
              for (int mX = 0; mX < tileXres; mX++) {
                float xPos = (float) tX * (float) imageXres / (float) xtiles + (float) mX * mmmx / (float) tileXres;
                //System.out.println("probing " + (int) xPos + " . " + (int) yPos + "    setting: " + mX + ", " + mY);
                //System.out.println("probing " + image.getRGB( (int)xPos, (int)yPos));
                //ds.writeFloat(image[(int) xPos][(int) yPos]);
                bi.setRGB(mX, mY, image.getRGB( (int)xPos, (int)yPos));
              }
            }
            ImageIO.write(bi, "jpeg", ds);
          } finally {
            if (ds != null) ds.close();
          }
          // System.out.println("Wrote image file " + imageout + " with resolution [" + tileXres + ", " + tileYres + "].");
        } catch (IOException e) {
          System.out.println("Data File failure: " + e);
        }
      }
    }
    return true;
  }

  public boolean WriteDataFiles() {
    int tileXres = image_size;
    int tileYres = image_size;
    // we don't care, just interpolate.  
    float mmmy = ((float) dataYres / (float) ytiles);  // 80 * 10 * 10 = 8000 or 1/8000
    float mmmx = ((float) dataXres / (float) xtiles);  // 40 * 10 * 10 = 4000 or 1/4000
    System.out.println("mmmx :" + mmmx + "mmmy: " + mmmy);
    for (int tY = 0; tY < ytiles; tY++) {
      for (int tX = 0; tX < xtiles; tX++) {
        System.out.println("Writing tile " + tX + " " + tY + " //");
        System.out.println("Writing resx " + tileXres + " y " + tileYres + " //");
        System.out.println("Writing dataxr " + dataXres + " y " + dataYres + " //");
        String dataout = String.format(TerrainConstants.dataFormat, tX, tY);
        DataOutputStream ds = null;
        try {
          try {
            ds = new DataOutputStream(new FileOutputStream(dataout)); 
            ds.writeInt(tileXres);
            ds.writeInt(tileYres);
            for (int mY = 0; mY < tileYres; mY++) {
                float yPos = (float) tY * (float) dataYres / (float) ytiles + (float) mY * mmmy / (float) (tileYres - 1);
              for (int mX = 0; mX < tileXres; mX++) {
                float xPos = (float) tX * (float) dataXres / (float) xtiles + (float) mX * mmmx / (float) (tileXres - 1);
                  //System.out.println("probing " + (int) xPos + " . " + (int) yPos + " -> ");
                if (xPos >= dataXres) {
                 xPos = dataXres - 1.0f;
                }
                if (yPos >= dataYres) {
                 yPos = dataYres - 1.0f;
                }
                float da = data[(int) xPos][(int) yPos];
                if (mX == 2 && mY == 2) {
                  System.out.println("probing " + (int) xPos + " . " + (int) yPos + " -> " + da);
                }
                //ds.writeFloat(data[(int) xPos][(int) yPos]);
                // // Important change.  Database is now int16s.
                Float value = data[(int) xPos][(int) yPos];
                short stuff = (short) value.intValue();
                ds.writeShort(stuff);
              }
            }
          } finally {
            if (ds != null) ds.close();
          }
          // System.out.println("Wrote data file " + dataout + " with resolution [" + tileXres + ", " + tileYres + "].");
        } catch (IOException e) {
          System.out.println("Data File failure: " + e);
        }
      }
    }
    return true;
  }

  public boolean ReadImageFile() {
    try {
      FileInputStream imagef = new FileInputStream(imagefile);
      image = ImageIO.read(imagef);
      imageXres = image.getWidth();
      imageYres = image.getHeight();
      System.out.println("Image: " + image);
    } catch (IOException e) {
      System.out.println("Image File failure: " + e);
      return false;
    }
    return true;
  }

  public void Process() {
    boolean skipData = false;

    System.out.println("Processing... tiles [" + xtiles + ", " + ytiles +"] with image size " + image_size);
    boolean success = false;
    // Select data strategy:
    if (datafile.equals("sin")) {
      success = GenerateFunction(datafile);
    } else if (datafile.equals("skip")) {
      success = true;
      skipData = true;
    } else if (datafile.endsWith(".png")) {
      success = ReadImageAsData();
    } else {
      success = ReadDataFile();
    }
    if (! success) {
      Usage();
      return;
    }
    // end data file input.
    success = ReadImageFile();
    if (! success) {
      Usage();
      return;
    }
    if (! skipData) {
      System.out.print("Writing Data Files");
      success = WriteDataFiles();
      if (! success) {
        Usage();
        return;
      }
    }
    System.out.print("Writing Image Files");
    success = WriteImageFiles();
    if (! success) {
      Usage();
      return;
    }
    System.out.println("Loaded image and data: " + datafile + ", " + imagefile);
  }

  public static void Usage() {
    System.out.println("Usage:");
    System.out.println("java terrain.TerrainCooker -d mc.data -i mc.image -x 10 -y 10 -z 1.0 -s 1024");
    System.out.println("Where: z = height scale of data");
    System.out.println("Where: d = data image path, i = texture image path, x,y are tile dimensions, s is image size:");
    System.out.println("Where: d can also be 'sin' for sine waves or an image using (int) TYPE_INT_ARGB, 32 bit ints.");
  }

  public static void main(String... argv) {
    String dataString = null;
    String imageString = null;
    int x = 0;
    int y = 0;
    int size = 0;
    float scale = 1.0f;

    int argvIndex = 0;
    while (argvIndex < argv.length) {
      String a = argv[argvIndex];      
      System.out.println("Arg: " + argvIndex + " is " + a);
      if (a.equals("-d")) {
        dataString = argv[++argvIndex];
      }
      if (a.equals("-i")) {
        imageString = argv[++argvIndex];
      }
      if (a.equals("-x")) {
        x = Integer.parseInt(argv[++argvIndex]);
      }
      if (a.equals("-y")) {
        y = Integer.parseInt(argv[++argvIndex]);
      }
      if (a.equals("-z")) {
        scale = Float.parseFloat(argv[++argvIndex]);
      }
      if (a.equals("-s")) {
        size = Integer.parseInt(argv[++argvIndex]);
      }
      argvIndex++;
   }
   if (dataString == null || imageString == null || x <= 0 || y <= 0 || size <= 0) {
     Usage();
     return;
   } 
   TerrainCooker tc = new TerrainCooker(dataString, imageString, x, y, size, scale);
   tc.Process();
  }
}
