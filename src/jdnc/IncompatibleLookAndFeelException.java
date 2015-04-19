/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package jdnc;

/**
 *
 * @author cak-ust
 */
import javax.swing.UIManager;

/**
 *
 * @author Thomas Bierhance
 */
public class IncompatibleLookAndFeelException extends Exception {

    public IncompatibleLookAndFeelException(String message) {
        super(message);
    }

    public String getLookAndFeelName() {
        return UIManager.getLookAndFeel().getName();
    }

}

