package main;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author oestadho
 */
/*
 * systemSkin.java
 *
 * Created on 16 Agustus 2007, 13:12
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */


import com.l2fprod.gui.plaf.skin.Skin;
import com.l2fprod.gui.plaf.skin.SkinLookAndFeel;
import javax.swing.JFrame;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

/**
 *
 * @author Prince Fafa
 */
public class systemSkin {

    /** Creates a new instance of systemSkin */
    public systemSkin(String namaSkin) {
        JFrame.setDefaultLookAndFeelDecorated(true);
        Skin theSkinToUse = null;
        try{
            theSkinToUse = SkinLookAndFeel.loadThemePack(getClass().getResource(namaSkin));
        }catch(Exception ex){
            ex.printStackTrace();
        }
        SkinLookAndFeel.setSkin(theSkinToUse);
        try{
            UIManager.setLookAndFeel(new SkinLookAndFeel());
        }catch(UnsupportedLookAndFeelException ex){
            ex.printStackTrace();
        }
    }
}

