package main;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */



/**
 *
 * @author Administrator
 */
/*
 * KasirCon.java
 *
 * Created on July 6, 2005, 7:42 PM
 */

/**
 *
 * @author  Administrator
 */
import java.awt.Component;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.MissingResourceException;
import java.util.Properties;
import java.util.PropertyResourceBundle;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
public class DBConnection{
    /** Creates a new instance of KasirCon */
    private Connection con;
    boolean bError;
    private Properties prop;
    private StringBuilder url;
    private static PropertyResourceBundle resources;

    static {
        try {
            String sDir=System.getProperties().getProperty("user.dir");
            resources = new PropertyResourceBundle(new FileInputStream(new File(sDir+"/setting.properties")));
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(null, ex.getMessage());
        } catch (MissingResourceException mre) {
            System.err.println("setting.properties not found");
            System.exit(1);
        }
    }

    public DBConnection(String user,String pass, Component fThis) {
        {
            try {
                url = new StringBuilder();
                url.append("jdbc:postgresql://");
                url.append(resources.getString("server"));
                url.append(":");
                url.append("5432");
                url.append("/");
                url.append(resources.getString("database"));

                System.out.println(url);
                Class.forName("org.postgresql.Driver");
                con = DriverManager.getConnection(url.toString(), user, pass);

                bError = true;
            } catch (java.sql.SQLException se) {
                JOptionPane jfo = new JOptionPane(se.getMessage(), JOptionPane.INFORMATION_MESSAGE);
                JDialog dialog = jfo.createDialog(fThis, "Message");
                dialog.setModal(true);
                dialog.setVisible(true);
                bError = false;
                //System.exit(1);
            } catch (ClassNotFoundException ce) {
                JOptionPane jfo = new JOptionPane(ce.getMessage(), JOptionPane.INFORMATION_MESSAGE);
                JDialog dialog = jfo.createDialog(fThis, "Message");
                dialog.setModal(true);
                dialog.setVisible(true);
                bError = false;
                //System.exit(1);
            } finally {
//                try {
//                    in.close();
//                } catch (IOException ex) {
//                    Logger.getLogger(DBConnection.class.getName()).log(Level.SEVERE, null, ex);
//                }
            }
        }

    }

    public Connection getCon(){
        return con;
    }

     public boolean gettErrLog(){
        return bError;
    }
}

