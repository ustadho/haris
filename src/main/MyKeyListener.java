/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package main;

import java.awt.Component;
import java.awt.Container;
import java.awt.FocusTraversalPolicy;
import java.awt.KeyboardFocusManager;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

/**
 *
 * @author ustadho
 */
public class MyKeyListener extends KeyAdapter {
    ListRsbm lst;

    public void setListRsbm(ListRsbm l){
        this.lst=l;
    }

    public void keyPressed(KeyEvent evt) {
        int keyKode = evt.getKeyCode();
        switch(keyKode){
            case KeyEvent.VK_ENTER : {
                if(evt.getSource().getClass().getName().equals("JTable")){
                    return;
                }
                if (!lst.isVisible()){
                    Component c = findNextFocus();
                    if (c!=null) c.requestFocus();
                }else{
                lst.requestFocus();
                }
                break;
            }
            case KeyEvent.VK_UP : {

                if(evt.getSource().getClass().getName().equals("JTable")){
                    return;
                }
        if (!lst.isVisible()){
        Component c = findPrevFocus();
        if (c!=null) c.requestFocus();
        }else{
        lst.requestFocus();
        }
        break;
    }
            case KeyEvent.VK_DOWN : {
                if(evt.getSource().getClass().getName().equals("JTable")){
                    return;
                }
                if (!lst.isVisible()){
                    Component c = findNextFocus();
                    if (c!=null) c.requestFocus();
                }else{
                    lst.requestFocus();
                }
                break;
            }
            case KeyEvent.VK_INSERT: {  //
//                    if (getBEdit()){
//                        if (tblItem.getRowCount()>=0){
//                            udfInsertDetail();
//                        }
//                    }
                break;
            }
             case KeyEvent.VK_F2 : {  //Add
//                    udfLookupPenerimaan();
                break;
            }
            case KeyEvent.VK_F3: {  //Edit
             //   udfFilter();
                break;
            }
            case KeyEvent.VK_F4: {  //Delete
                //udfDeleteItem();
                break;
            }

            case KeyEvent.VK_ESCAPE: {
                //Fr.dispose();
            }
            //default ;

         }
    }

    public Component findNextFocus() {
	// Find focus owner
	Component c = KeyboardFocusManager.getCurrentKeyboardFocusManager().getFocusOwner();
	Container root = c == null ? null : c.getFocusCycleRootAncestor();

	if (root != null) {
	    FocusTraversalPolicy policy = root.getFocusTraversalPolicy();
	    Component nextFocus = policy.getComponentAfter(root, c);
	    if (nextFocus == null) {
		nextFocus = policy.getDefaultComponent(root);
	    }
	    return nextFocus;
	}
	return null;
    }

    public Component findPrevFocus() {
	// Find focus owner
	Component c = KeyboardFocusManager.getCurrentKeyboardFocusManager().getFocusOwner();
	Container root = c == null ? null : c.getFocusCycleRootAncestor();

	if (root != null) {
	    FocusTraversalPolicy policy = root.getFocusTraversalPolicy();
	    Component prevFocus = policy.getComponentBefore(root, c);
	    if (prevFocus == null) {
		prevFocus = policy.getDefaultComponent(root);
	    }
	    return prevFocus;
	}
	return null;
    }
}


