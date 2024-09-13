/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.zettix.tankette.game;

// database
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.ResultSet;
import java.sql.Statement;

/**
 * Database handler.
 *
 * @author sean
 */
public class DataBaseHandler {
    private final String databaseFile;
    private Connection conn = null;
    private String manifest;
    
    public DataBaseHandler(String databaseFile) {
        this.databaseFile = databaseFile;
    }
    
    public boolean Connect() {
        try {
            Class.forName("org.sqlite.JDBC");
        } catch  (ClassNotFoundException e) {
            System.out.println("Could load database class! " + e);
            return false;
        }
        try {
            conn = DriverManager.getConnection("jdbc:sqlite:" + databaseFile);
        } catch (SQLException e) {
            System.out.println("Could not connect to database! " + databaseFile + " " + e);
            return false;
        }
        if (conn == null)
            return false;
        return true;
    }
    
    public String getManifest() {
        if (manifest == null) {
            Statement stmt;
            String result = null;
            try {
                String selectCommand = "SELECT data FROM manifest;";
                stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(selectCommand);
                while (rs.next()) {
                    result = rs.getString("data");
                    break;
                }
            } catch (SQLException e) {
                System.out.println("Failed to get manifest! " + e);
            }
            manifest = result;
        }
        return manifest;
    }
    
    public int getX() {
        if (manifest != null) {
            return Integer.parseInt(manifest.substring(0, manifest.indexOf(" ")));
        }
        return 0;
    }
    
    public int getY() {
        if (manifest != null) {
            return Integer.parseInt(manifest.substring(manifest.indexOf(" ") + 1));
        }
        return 0;
    }
    
    public byte[] getBlob(String table, String key) {
        Statement stmt;
        byte[] result = null;
        if (conn == null) {
            System.out.println("Connect to database first!");
            return result;
        }
        try {
            String selectCommand = "SELECT data FROM " + table + 
                                   " WHERE id='" + key + "';";
            stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(selectCommand);
            while (rs.next()) {
                result = rs.getBytes("data");
                break;
            }
        } catch (SQLException e) {
            System.out.println("Failed to get data blob with key: " + key + " " + e);
        }
        return result;
    }   
}