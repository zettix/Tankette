/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.zettix.tankette.game;

import com.zettix.graphics.gjkj.util.V3;
import javax.json.JsonObject;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author sean
 */
public class TerrainTileTest {
    TerrainTile tt;

    @Before
    public void setUp() {
        double[] data = {0.0, 0.0, 1.0, 1.0};
        tt = new TerrainTile(0.1, 0.2, 0.3, 0.11, 0.12, 2, "www.example.com", "testname", data);
    }
    
    /**
     * Test of copy method, of class TerrainTile.
     */
    @Test
    public void testCopy() {
        System.out.println("copy");
        TerrainTile result = (TerrainTile) TerrainTile.copy(tt);
        JsonObject ttj = tt.toJson();
        JsonObject resultj = result.toJson();
        assertEquals(ttj, resultj);
        assertNotEquals(result, tt);
    }

    /**
     * Test of getNormal method, of class TerrainTile.
     */
    @Test
    public void testGetNormal() {
        System.out.println("getNormal");
        double xi = 0.11;
        double yi = 0.33;
        V3 expResult = new V3(0.0, 0.11914522061843066, -0.9928768384869222);
        V3 result = tt.getNormal(xi, yi);
        for (int i = 0; i < 3; i++) {
            assertEquals(expResult.coords[i], result.coords[i], 0.00001);
        }
    }

    /**
     * Test of toString method, of class TerrainTile.
     */
    @Test
    public void testToString() {
        System.out.println("toString");
        StringBuffer sb = new StringBuffer()
                .append("Tile: testname\n")
                .append("X: 0.1\n")
                .append("Y: 0.2\n")
                .append("Z: 0.3ResX:0.11ResY0.12\n")
                .append("C: 2\n")
                .append("U: www.example.com\n")
                .append("0.00 0.00 \n")
                .append("1.00 1.00 \n");
                //.append("]\n");
        String expResult = sb.toString();
        String result = tt.toString();
        assertEquals(expResult, result);
    }

    /**
     * Test of getHeight method, of class TerrainTile.
     */
    @Test
    public void testGetHeight() {
        System.out.println("getHeight");
        double xi = 0.11;
        double yi = 0.33;
        double expResult = 0.25;
        double result = tt.getHeight(xi, yi);
        assertEquals(expResult, result, 0.001);
    }

    /**
     * Test of toJson method, of class TerrainTile.
     */
    @Test
    public void testToJson() {
        System.out.println("toJson");
        String expJsonStr = "{\"n\":\"testname\",\"x\":0.1,\"y\":0.3,\"z\":0.2," +
                            "\"rx\":0.11,\"ry\":0.12,\"c\":2,\"u\":\"www.example.com\"" +
                            ",\"d\":[0.0,0.0,1.0,1.0]}";
        
        //JsonObject expResult = null;
        JsonObject result = tt.toJson();
        String resultJsonString = result.toString();
        assertEquals(expJsonStr, resultJsonString);
    }
}
