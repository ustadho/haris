/*
 * MyListView.java
 *
 * Created on December 21, 2005, 2:53 PM
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.
 */

package pembelian;
import java.sql.*;
/**
 *
 * @author Administrator
 */
public class MyListView extends javax.swing.JFrame {
    static Connection con;
    private ResultSet rs;
    private String sQuery;
    private Object[] oResult;
    private javax.swing.table.DefaultTableModel myModel;
    private javax.swing.JTextField txtCari;
//    private javax.swing.JLabel[] lblDes;    
    private int iPosRow = 0;
    
    public MyListView(){
        initComponents();
    }
    /** Creates a new instance of MyListView */
    public MyListView(String newQry) throws SQLException {
        initComponents();
        setSQuery(newQry);
        tblList.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_OFF);
    }
    
    public void setRs(ResultSet newRS)  throws SQLException {
        rs = newRS;
        setMyModel(rs);
    }
    
    
    public ResultSet getRs() {
        return rs;
    }
    
    public void setSQuery(String newQry) throws SQLException{
    try{
        sQuery = newQry;        
        Statement st = con.createStatement();
        rs = st.executeQuery(sQuery);
        setMyModel(rs);
        rs.close();
        st.close();
    }catch(SQLException e){System.out.println(e.getMessage());}
    }
    
    public String getSQuery(){
        return sQuery;
    }
    
    public void setTxtCari(javax.swing.JTextField newTxt){
        txtCari = newTxt;
    }
    
   private void initComponents() {
        jScrollPane1 = new javax.swing.JScrollPane();
        tblList = new javax.swing.JTable();

        setAlwaysOnTop(true);
        setResizable(false);
        setState(2);
        setUndecorated(true);
        addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                formFocusGained(evt);
            }
   //         public void focusLost(java.awt.event.FocusEvent evt) {
   //             formFocusLost(evt);
   //        }
        });

        jScrollPane1.setBackground(new java.awt.Color(255, 255, 204));
        jScrollPane1.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
        jScrollPane1.setVerticalScrollBarPolicy(javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
/*        jScrollPane1.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jScrollPane1MouseClicked(evt);
            }
        });
*/
        tblList.setBackground(new java.awt.Color(204, 255, 255));
        tblList.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        tblList.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_OFF);
        tblList.setSelectionBackground(new java.awt.Color(255, 204, 204));
 //       tblList.addFocusListener(new java.awt.event.FocusAdapter() {
 //           public void focusLost(java.awt.event.FocusEvent evt) {
 //               tblListFocusLost(evt);
 //           }
//        });
        tblList.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                tblListKeyPressed(evt);
           }
            public void keyReleased(java.awt.event.KeyEvent evt) {
                tblListKeyReleased(evt);
            }
        });

        tblList.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tblListMouseClicked(evt);
            }
        });

        jScrollPane1.setViewportView(tblList);

        getContentPane().add(jScrollPane1, java.awt.BorderLayout.CENTER);

    }
    
    public void setMyModel(ResultSet newRS)  throws SQLException {
        myModel = new javax.swing.table.DefaultTableModel();
        tblList.setModel(myModel);
            
            for(int i=1;i <= rs.getMetaData().getColumnCount();i++) {
                myModel.addColumn(rs.getMetaData().getColumnName(i));
            }
            
            while (newRS.next()) {
                Object arObj[] = new Object[newRS.getMetaData().getColumnCount()];
                for(int i=1;i <= newRS.getMetaData().getColumnCount();i++) {
                    if(newRS.getObject(i) != null){
                        arObj[i-1]=newRS.getObject(i);
                    } else {                        
                        arObj[i-1]=new Object();
                    }
                }
                myModel.addRow(arObj);
            }            
            if (tblList.getRowCount()>0) tblList.setRowSelectionInterval(0,0);
            newRS.close();    
}
    
    public Object[] getOResult() {
        Object[] oSelected =new Object[tblList.getColumnCount()];
        
        if (tblList.getRowCount()>0) {        
            if (iPosRow == 0) {iPosRow =tblList.getSelectedRow();}
            for(int i=1;i <= tblList.getColumnCount();i++) {
                oSelected[i-1] = tblList.getValueAt(iPosRow, i-1);
            }
        }
        oResult = oSelected;
        return oResult;
    }
    
    private void tblListMouseClicked(java.awt.event.MouseEvent evt) {                                     
        if (evt.getClickCount()==2){
            iPosRow =tblList.getSelectedRow();
            this.setVisible(false);
            txtCari.setText(tblList.getValueAt(iPosRow, 0).toString());                          
            txtCari.requestFocus();
 //           for (int i=0;i<lblDes.length;i++){
 //               lblDes[i].setText(tblList.getValueAt(iPosRow, i+1).toString());}
        }
    }                                    

    private void tblListKeyReleased(java.awt.event.KeyEvent evt) {                                   
    }    
        
    private void tblListKeyPressed(java.awt.event.KeyEvent evt) {                                   
        if (evt.getKeyCode() == java.awt.event.KeyEvent.VK_ENTER) {                       
          //  getOResult();
            
            iPosRow =tblList.getSelectedRow();
            this.setVisible(false);
            txtCari.setText(tblList.getValueAt(tblList.getSelectedRow(), 0).toString());                            
            txtCari.requestFocus();            
/*            for (int i=0;i<lblDes.length;i++){
                lblDes[i].setText(tblList.getValueAt(iPosRow, i+1).toString());
*/            }
        }          
                                        

    private void formFocusGained(java.awt.event.FocusEvent evt) {                                 
        tblList.requestFocus();
    }
    
    public void setColWidth(int ColIndex, int ColWidth) {                        
        tblList.getColumnModel().getColumn(ColIndex).setPreferredWidth(ColWidth);
    }

    // Variables declaration - do not modify                     
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable tblList;
    // End of variables declaration                   

    
}
