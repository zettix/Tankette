// files:
package terrain;

import java.io.File;
import java.io.IOException;
import java.io.FileInputStream;
import java.nio.file.Path;
// data structures:
import java.util.List;
import java.util.ArrayList;
import java.util.LinkedList;
// database
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.PreparedStatement;
// regex
import java.util.regex.Pattern;
import java.util.regex.Matcher;

// java, so needy.

/**
  Create an SQLite database and populate it with files from a directory.
*/


class SqlPusher {

  private final String databaseFile;
  private final String datafileDir;
  private final String tableName;
  private final Pattern p = Pattern.compile("data_(\\d+)_(\\d+).dat");
  private int maxX;
  private int maxY;

  private List<File> files = new ArrayList<>();
  private Connection conn = null;

  public SqlPusher(String fileName, String dirName) {
    this(fileName, dirName, dirName );
  }

  public SqlPusher(String fileName, String dirName, String tableName) {
    databaseFile = fileName;
    datafileDir = dirName;
    this.tableName = tableName;
    maxX = 0;
    maxY = 0;
  }

  private boolean Connect() throws SQLException {
    PreparedStatement pstmt = null;
    try {
      System.out.println("Loading JDBC");
      Class.forName("org.sqlite.JDBC");
      System.out.println("Connecting to Database: " + databaseFile);
      conn = DriverManager.getConnection("jdbc:sqlite:" + databaseFile);
      StringBuffer createTable = new StringBuffer("CREATE TABLE IF NOT EXISTS ")
                .append(tableName)
                .append(" (id varchar(40) NOT NULL PRIMARY KEY, data blob);");
      pstmt = conn.prepareStatement(createTable.toString());
      pstmt.executeUpdate();
      return true;
    } catch (ClassNotFoundException e) {
      System.out.println("Jello pudding pops! Databse errors: " + e);
    } finally {
      if (pstmt != null) pstmt.close();
    }
    return false;
  }
  

  private boolean GetFiles() {
    File dirk = new File(datafileDir);
    if (dirk.isDirectory()) {
      for (final File fileEntry : dirk.listFiles()) {
        files.add(fileEntry);
        System.out.println("Collected FILE : " + fileEntry);
      }
    } else {
      System.out.println("Not a directory: " + dirk);
      return false;
    }
    return true;
  }

  private byte[] ReadFile(File target) {
    try {
            List<byte []> contentsList = new LinkedList<>();
            int total = 0;
            FileInputStream inputStream = new FileInputStream(target);
            do {
                int size = inputStream.available();
                if (size == 0) {
                    break;
                }
                byte[] buff = new byte[size];
                size = inputStream.read(buff);
                total += size;
                contentsList.add(buff);
            } while (true);
            if (total == 0) {
              System.out.println("Empty file: " + target);
              return null;
            }
            byte[] totalFile = new byte[total];
            int offset = 0;
            for (byte[] chunk :  contentsList) {
                for (int x = 0; x < chunk.length; x++) {
                    totalFile[x + offset] = chunk[x];
                }
                offset += chunk.length;
            }
            if (offset != total) {
                System.out.println("Sound the alarm! " + offset + " is the offset but the total is: " + total);
            }
            return totalFile;
        } catch (IOException e) {
            System.out.println("Boo hoo! IO Exception." + e);
        }
        return null;
  }

  private boolean PutManifest() throws SQLException {
    for (final File fileEntry : files) {
       Path path = fileEntry.toPath();
       int nameCount = path.getNameCount();
       String name = path.getName(nameCount - 1).toString();
       Matcher m = p.matcher(name);
       System.out.println("Count: " + nameCount + " name:" + name + " match?" + m.matches());
       if (m.matches()) {
         int x = Integer.parseInt(m.group(1));
         int y = Integer.parseInt(m.group(2));
         if (x > maxX) {
            maxX = x;
         }
         if (y > maxY) {
            maxY = y;
         }
       }
    }
    if (maxX > 0 && maxY > 0) {
       System.out.println("Writing manifest file with dims: " + maxX + ", " + maxY);
       PreparedStatement pstmt = null;
       try {
          StringBuffer createTable = new StringBuffer("CREATE TABLE IF NOT EXISTS ")
                .append("manifest")
                .append(" (id varchar(16) NOT NULL PRIMARY KEY, data varchar(32));");
        pstmt = conn.prepareStatement(createTable.toString());
        pstmt.executeUpdate();
        StringBuffer putfile = new StringBuffer("INSERT INTO ")
            .append("manifest")
            .append(" VALUES (?,?);");
        pstmt = this.conn.prepareStatement(putfile.toString());
        pstmt.setString(1, "manifest");
        StringBuffer datum = new StringBuffer();
        datum.append(maxX).append(" ").append(maxY);
        pstmt.setString(2, datum.toString());
        pstmt.executeUpdate();
      } finally {
        if (pstmt != null) pstmt.close();
      }
     } else {
      System.out.println("max x and y not greater than 0");
     }
     return true;
  }


  private boolean PutData(String name, byte[] data) throws SQLException {
    // sql to insert data via conn.
    PreparedStatement pstmt = null;
    try {
      StringBuffer putfile = new StringBuffer("INSERT INTO ")
          .append(tableName)
          .append(" VALUES (?,?);");
      pstmt = this.conn.prepareStatement(putfile.toString());
      pstmt.setString(1, name);
      pstmt.setBytes(2, data);
      pstmt.executeUpdate();
    } finally {
      if (pstmt != null) pstmt.close();
    }
    return true;
  }

  private void PutFiles () throws SQLException {
    // drop tables if they exist.
    // create tables
    boolean putOK;
    for (final File fileEntry : files) {
       Path path = fileEntry.toPath();
       int nameCount = path.getNameCount();
       System.out.println("Count: " + nameCount);
       String name = path.getName(nameCount - 1).toString();
       System.out.println("Putting: " + name);
       byte[] data = ReadFile(fileEntry);
       System.out.println("With a size of " + data.length);
       if (data.length == 0) {
         System.out.println("zero data: " + name);
         Usage();
         return;
       }
       putOK = PutData(name, data);
       if (! putOK) break;
       //
    }
  }
      

  public void Process() {
    try {
    System.out.println("Processing  database " + databaseFile + " with dir " +
                       datafileDir + " creating table " + tableName);
    boolean go = true;
    go = Connect();
    if (!go) {
      System.out.println("Could not connect to database: " + databaseFile);
      Usage();
      return;
    }
    go = GetFiles();
    if (!go) {
      System.out.println("file collection failed." + datafileDir);
      Usage();
    } else {
      PutManifest();
      PutFiles();
    }
    System.out.println("Done inserting files into " + databaseFile);
    this.conn.close();
    } catch (SQLException e) {
      System.out.println("Jello pudding pops! Insert errors: " + e);
    }
  }

  public static void Usage() {
    System.out.println("java SqlPusher -f database -d directory [-t tablename]");
    System.out.println("    tablename defaults to directory.");
  }

  public static void main(String... args) {
    String dbase = null;
    String datadir = null;
    String table = null;
    for (int x = 0; x < args.length; ++x) {
      if (args[x].equals("-f")) {
        dbase = args[++x];
      } else if (args[x].equals("-d")) {
        datadir = args[++x];
      } else if (args[x].equals("-t")) {
        table = args[++x];
      }
    }
    if (dbase == null || datadir == null) {
      SqlPusher.Usage();
      return;
    }
    SqlPusher pushie;
    if (table != null) {
      pushie = new SqlPusher(dbase, datadir, table);
    } else {
      pushie = new SqlPusher(dbase, datadir);
    }
    pushie.Process();
  }
}
