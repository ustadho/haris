/*
 * SysConfig.java
 *
 * Created on April 20, 2006, 8:24 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package main;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import javax.swing.JOptionPane;

/**
 *
 * @author hengki
 * @updated 21-04-2006 20:01
 */

public class SysConfig {
    private String ServerLoc;
    private String PrintTextName;
    private String PrintKwtName;
    private String PrintGraphName;    
    private String Site_Id;
    private String DBName;
    private String sUser, sPass;
    
    /** Creates a new instance of SysConfig */
    public SysConfig() {
        try {
        File dir = new File(System.getProperty("user.dir"));
        URL url = dir.toURI().toURL();        // file:/c:/almanac1.4/examples/
        System.out.println("Direktory :"+url.getPath());

             
        //BufferedReader in = new BufferedReader(new FileReader(url.getPath()+File.separator+"dist"+File.separator+"sn.ini"));
        BufferedReader in = new BufferedReader(new FileReader(url.getPath()+File.separator+"sn.ini"));
        String str;
        while ((str = in.readLine()) != null) {
            System.out.println(str);
            if(str.toUpperCase().indexOf("SERVER")>=0){
                ServerLoc=str.substring(str.indexOf("=")+1,str.length());
                System.out.println("Server = "+ServerLoc);
            }

            if(str.toUpperCase().indexOf("DB")>=0){
                DBName=str.substring(str.indexOf("=")+1,str.length());
                System.out.println("DB = "+DBName);
            }

            if(str.toUpperCase().indexOf("PRINT_LABEL")>=0){
                PrintTextName=str.substring(str.indexOf("=")+1,str.length());
                System.out.println("print_label = "+PrintTextName);
            }

            if(str.toUpperCase().indexOf("PRINT_KWITANSI")>=0){
                PrintKwtName=str.substring(str.indexOf("=")+1,str.length()).trim();
                System.out.println("print_kwitansi = "+PrintKwtName);
            }
            
            
            if(str.toUpperCase().indexOf("PRINT_REPORT")>=0){
                PrintGraphName=str.substring(str.indexOf("=")+1,str.length());
                System.out.println("print_report = "+PrintGraphName);
            }
            
            if(str.toUpperCase().indexOf("SITE_ID")>=0){
                Site_Id=str.substring(str.indexOf("=")+1,str.length());
                System.out.println("Site_Id = "+Site_Id);
            }
            
            if(str.toUpperCase().indexOf("USR")>=0){
                sUser=str.substring(str.indexOf("=")+1,str.length());
                //System.out.println("Usr = "+sUser);
            }
            
            if(str.toUpperCase().indexOf("PAS")>=0){
                sPass=str.substring(str.indexOf("=")+1,str.length());
                //System.out.println("Pas = "+sPass);
            }
            
        }
        in.close();
    } catch (IOException e) {
        JOptionPane.showMessageDialog(null,e.getMessage(),"SHS Go Open Source Err",JOptionPane.ERROR_MESSAGE);  
    }
    }
    public String getServerLoc() {
        return ServerLoc;
    }

    public String getPrintTextName() {
        return PrintTextName;
    }
    
    public String getPrintKwtName() {
        return PrintKwtName;
    }    

    public String getPrintGraphtName() {
        return PrintGraphName;
    }    
    
    public String getDBName() {
        return DBName;
    }    
    
    public String getSite_Id() {
        return Site_Id;
    }

    public String getUser(){
        return sUser;
    }
    
    public String getPass(){
        return sPass;
    }
    
    public static void main(String args[]) {
        SysConfig sc = new SysConfig();
    }
    
//    public static String versi="DEMO";
    public static String versi="ASLI";
}
   