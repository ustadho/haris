/*
 * TextEditorOk.java
 *
 * Created on December 20, 2005, 11:30 AM
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.
 */

package pembelian;

import javax.swing.JTextField;
import javax.swing.JTable;
import javax.swing.JLabel;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.DefaultCellEditor;
import java.sql.Connection;
import java.sql.SQLException;
import java.awt.Component;
import java.awt.BorderLayout;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;



/**
 *
 * @author Administrator
 */
public class TextEditorOk extends DefaultCellEditor{
JTextField txt; 
Integer kk;
boolean okAdd,okEdit;
Connection con;    
private MyListView lst;
private JLabel lbl;
private String sFieldShow,sTable,sKdFind,sSql,sExFil;
private Integer posx,posy,tblX,tblY;
    /** Creates a new instance of TextEditorOk */
    public TextEditorOk(Connection newCon,String sFieldToBeShown,String sNameTable,Integer xx,Integer yy) {
         super(new JTextField());
         kk=0;
         lst = new MyListView();
         lst.setVisible(false);
         lst.setSize(200,400);
         lst.con = newCon;
         sFieldShow=sFieldToBeShown;
         sTable=sNameTable;
         posx=xx;posy=yy;
         con=newCon;
         sExFil="";
         
         txt = (JTextField)getComponent();
         txt.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtKeyReleased(evt);
            }
        });
        txt.requestFocus();
    }
    
    public TextEditorOk(Connection newCon,String sFieldToBeShown,String sNameTable,String sExpFilter,Integer xx,Integer yy) {
         super(new JTextField());
         kk=0;
         lst = new MyListView();
         lst.setVisible(false);
         lst.setSize(200,400);
         lst.con = newCon;
         sFieldShow=sFieldToBeShown;
         sTable=sNameTable;
         posx=xx;posy=yy;
         con=newCon;
         sExFil=sExpFilter;
         
         txt = (JTextField)getComponent();
         txt.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtKeyReleased(evt);
            }
        });
        txt.requestFocus();
    }
    
    public Component getTableCellEditorComponent(JTable table,
            Object value, boolean isSelected,
            int row, int column) {
        JTextField txt =
            (JTextField)super.getTableCellEditorComponent(
                table, value, isSelected, row, column);
        if (value==null) 
            value="";
        txt.setText(value.toString().trim());
        //int tem=0;
       // for (int zz=0;zz<column;zz++){
       //     tem=tem+table.getColumnModel().getColumn(zz).getPreferredWidth();
       // }
       tblY=table.getY();
        //tblY=table.getY()+((row+1)*table.Height());
        return txt;
    }
    
    public Object getCellEditorValue() {
        JTextField txt = (JTextField)getComponent();
        Object o = txt.getText().trim();
        return o;
    }

    public boolean stopCellEditing() {
        JTextField txt = (JTextField)getComponent();
        Object o = txt.getText();
    
        try{
            sSql="select "+sFieldShow+" from "+sTable+" where "+sFieldShow.substring(0, sFieldShow.indexOf(","))+"='"+o.toString()+"'";
            Statement stmtfind=con.createStatement();
            ResultSet rsfind=stmtfind.executeQuery(sSql);
            if (!rsfind.next()){
                return false;
            }
            stmtfind.close();
            rsfind.close();
        }catch(SQLException se){}
        lst.dispose();
        return super.stopCellEditing();
    }
    
public String getLabel(){
    return lbl.toString().trim();
}

public String getKode(){
    return txt.getText().trim();
}
    
private void txtKeyReleased(java.awt.event.KeyEvent evt) {                                   
    try
    {
    String sCari = txt.getText();                        
           switch(evt.getKeyCode()) {           
               case java.awt.event.KeyEvent.VK_ENTER :{
                   if (lst.isVisible()){
                        Object[] obj = lst.getOResult();
                        if (obj.length > 0) {                            
                            txt.setText(obj[0].toString());
                            lst.setVisible(false);
                        }               
                   }
                   break;
               }
               case java.awt.event.KeyEvent.VK_DOWN: {               
                   if (lst.isVisible()){                 
                       lst.setFocusableWindowState(true);                       
                       lst.setVisible(true);
                       lst.requestFocus();
                   }   
                   break;
               }
               default : {
                   String sText=sFieldShow;
                   String sWhere="";
                   while (sText.contains(",")){
                       sWhere=sWhere+sText.substring(0, sText.indexOf(","))+" like '%"+sCari.trim().toUpperCase()+"%' OR ";
                       sText=sText.substring(sText.indexOf(",")+1, sText.length());
                   }
                   sWhere=sWhere+sText+" like '%"+sCari.trim().toUpperCase()+"%'";
                   if (!sExFil.trim().equals("")){
                   sWhere="("+sWhere+") and "+sExFil;}
              /*      sSql="select "+sFieldShow+" from "+sTable+" where "+sFieldShow.substring(0, sFieldShow.indexOf(","))+
                            " like '%"+sCari.trim().toUpperCase()+"%' OR "+sFieldShow.substring(sFieldShow.indexOf(",")+1, sFieldShow.length())+
                            " like '%"+sCari.trim().toUpperCase()+"%' order by "+sFieldShow.substring(0, sFieldShow.indexOf(","));
                */
                    sSql="select "+sFieldShow+" from "+sTable+" where "+sWhere+" order by "+sFieldShow.substring(0, sFieldShow.indexOf(","));
                    lst.setSQuery(sSql);
                    Integer txtY=txt.getY()+tblY;
                    lst.setBounds(posx+txt.getX(), posy+txtY+txt.getHeight()+50, 200,200);
                    lst.setFocusableWindowState(false);
                    lst.setTxtCari(txt);
                  //  lst.setLblDes(new javax.swing.JLabel[]{lbl});
                    //lst.setLblDes(lbl);
                    lst.setColWidth(0, 75);
                    lst.setColWidth(1, 200);
                    lst.setVisible(true); 
                    break;
                }
            }
    }catch(SQLException se){}
}    
}
