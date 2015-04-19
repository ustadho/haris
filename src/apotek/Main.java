/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package apotek;

import java.awt.Color;
import java.sql.Connection;
import javax.swing.BorderFactory;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.plaf.BorderUIResource;
import main.LoginUser;
import main.systemSkin;

/**
 *
 * @author cak-ust
 */
public class Main {
    public static Connection conn;

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
//        new systemSkin("silverLunaXPthemepack.zip");
        try {
//            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
//                UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            BorderUIResource borderUIResource= new BorderUIResource(BorderFactory.createLineBorder(Color.yellow, 2));
            UIManager.put("Table.focusCellHighlightBorder", borderUIResource);
        } catch (Exception e) {
            System.out.println("error setting l &f " + e);
        }

//        SwingUtilities.invokeLater(new Runnable() {
//
//            public void run() {
                new LoginUser().setVisible(true);
//            }
//        });
    }

    public static String sU="ust-apot3k";
    public static String sP="Allahu-Ahad";
}
