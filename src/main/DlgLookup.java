/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * DLgLookup.java
 *
 * Created on Jul 9, 2010, 11:02:51 AM
 */

package main;

import java.awt.Component;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JComponent;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumnModel;

/**
 *
 * @author cak-ust
 */
public class DlgLookup extends javax.swing.JDialog {
    private Connection conn;
    String sQry="";
    private String sFilter;
    GeneralFunction fn=new GeneralFunction();
    private Object srcText;
    private String sKode="";
    
    /** Creates new form DLgLookup */
    public DlgLookup(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
        MyKeyListener kListener=new MyKeyListener();
        txtSearch.addKeyListener(kListener);
        btnSelect.addKeyListener(kListener);
        btnClose.addKeyListener(kListener);
        tblList.addKeyListener(kListener);
        tblList.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).
        put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0),
            "selectNextColumnCell");
    }

    public class MyKeyListener extends KeyAdapter {
//        @Override
//        public void keyReleased(KeyEvent evt) {
//            if(evt.getSource().equals(srcText)){
//                udfFilter();
//            }
////            else if(evt.getSource().equals(tblList) && evt.getKeyCode()==KeyEvent.VK_ENTER)
////                udfSelected();
//        }
        @Override
        public void keyPressed(KeyEvent evt) {
            int keyKode = evt.getKeyCode();
            switch(keyKode){

                case KeyEvent.VK_ENTER : {
                    udfSelected();
                    break;
                }
                case KeyEvent.VK_UP :{
                    if(evt.getSource().equals(tblList)&& tblList.getSelectedRow()==0)
                        txtSearch.requestFocus();
                    break;
                }
                case KeyEvent.VK_DOWN :{
                    if(evt.getSource().equals(txtSearch)&& tblList.getRowCount()>0)
                    tblList.requestFocus();
                    break;
                }
                case KeyEvent.VK_ESCAPE: {
                    dispose();
                    break;
                }   //Jika cancel
                default :{
                    //udfFilter();
                    break;
                }
             }
        }
    }

    public String getKode(){
        return this.sKode;
    }

    public void hideAtas(){
        jLabel1.setVisible(false);
        txtSearch.setVisible(false);
        btnClose.setVisible(false);
        btnSelect.setVisible(false);
    }

    public void udfLoad(Connection con, String sQry, String sFilter, Object src){
        this.conn=con;
        this.sQry=sQry;
        this.sFilter=sFilter;
        this.srcText=src;
        setModel();
    }

     public void setModel()  {
         try{
            ResultSet rs=conn.createStatement().executeQuery(sQry);
            Object colName[] = new Object[rs.getMetaData().getColumnCount()];
            final Class[] types2 = new Class [rs.getMetaData().getColumnCount()];

            for(int i=0;i < rs.getMetaData().getColumnCount();i++) {
                //myModel.addColumn(rs.getMetaData().getColumnName(i));
                colName[i]=rs.getMetaData().getColumnName(i+1);
                if(rs.getMetaData().getColumnType(i+1)== java.sql.Types.DOUBLE ||
                        rs.getMetaData().getColumnType(i+1)== java.sql.Types.FLOAT||
                        rs.getMetaData().getColumnType(i+1)== java.sql.Types.REAL||
                        rs.getMetaData().getColumnType(i+1)== java.sql.Types.NUMERIC)
                    types2[i]=java.lang.Double.class;
                else if(rs.getMetaData().getColumnType(i+1)==java.sql.Types.INTEGER)
                    types2[i]=java.lang.Integer.class;
                else if(rs.getMetaData().getColumnType(i+1)==java.sql.Types.DATE)
                    types2[i]=java.lang.Object.class;
            }

            tblList.setModel(new javax.swing.table.DefaultTableModel(
                null,
                colName
            ) {
                Class[] types = types2;
                @Override
                public boolean isCellEditable(int rowIndex, int columnIndex) {
                    return false;
                }
                public Class getColumnClass(int columnIndex) {
                    return types [columnIndex];
                }
            });
            DefaultTableModel myModel = (DefaultTableModel)tblList.getModel();

            while (rs.next()) {
                Object arObj[] = new Object[rs.getMetaData().getColumnCount()];
                for(int i=1;i <= rs.getMetaData().getColumnCount();i++) {
                    if(rs.getObject(i) != null){
                        if(rs.getMetaData().getColumnType(i)==java.sql.Types.DOUBLE||
                                rs.getMetaData().getColumnType(i)==java.sql.Types.FLOAT||
                                rs.getMetaData().getColumnType(i)==java.sql.Types.REAL||
                                rs.getMetaData().getColumnType(i)==java.sql.Types.NUMERIC)
                            arObj[i-1]=rs.getDouble(i);
                        if(rs.getMetaData().getColumnType(i)==java.sql.Types.INTEGER)
                            arObj[i-1]=rs.getInt(i);
                        if(rs.getMetaData().getColumnType(i)==java.sql.Types.DATE)
                            arObj[i-1]=rs.getDate(i);
                        else
                            arObj[i-1]=rs.getObject(i);
                    } else {

                        arObj[i-1]=new Object();
                    }
                }
                myModel.addRow(arObj);

            }
            if (tblList.getRowCount()>0) {
                tblList.setRowSelectionInterval(0,0) ;
            } else{
                this.setVisible(false);
            }
            tblList.setModel((DefaultTableModel)fn.autoResizeColWidth(tblList, (DefaultTableModel)tblList.getModel()).getModel());
            rs.close();
         }catch(SQLException se){
             JOptionPane.showMessageDialog(this, se.getMessage());

         }
    }


     public JTable getTable(){
         return tblList;
     }

     private void udfFilter(){
        try {
            String s=sQry+" where "+sFilter+" ilike '%"+txtSearch.getText()+"%'";
            //System.out.println(s);
            ResultSet rs = conn.createStatement().executeQuery(s);

            ((DefaultTableModel)tblList.getModel()).setNumRows(0);
            while(rs.next()){
            Object arObj[] = new Object[rs.getMetaData().getColumnCount()];
                for(int i=1;i <= rs.getMetaData().getColumnCount();i++) {
                    if(rs.getObject(i) != null){
                        arObj[i-1]=rs.getObject(i);
                    } else {
                        arObj[i-1]=new Object();
                    }
                }
                ((DefaultTableModel)tblList.getModel()).addRow(arObj);
            }
            if(tblList.getRowCount()>0) tblList.setRowSelectionInterval(0, 0);
            
            tblList.setModel((DefaultTableModel)fn.autoResizeColWidth(tblList, (DefaultTableModel)tblList.getModel()).getModel());
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage());
        }

     }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jScrollPane1 = new javax.swing.JScrollPane();
        tblList = new org.jdesktop.swingx.JXTable();
        jLabel1 = new javax.swing.JLabel();
        txtSearch = new javax.swing.JTextField();
        btnClose = new javax.swing.JButton();
        btnSelect = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        tblList.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Title 1"
            }
        ));
        tblList.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_OFF);
        tblList.setFont(new java.awt.Font("Tahoma", 0, 12));
        tblList.getTableHeader().setReorderingAllowed(false);
        jScrollPane1.setViewportView(tblList);

        jLabel1.setFont(new java.awt.Font("Tahoma", 0, 12));
        jLabel1.setText("Search :");

        txtSearch.setFont(new java.awt.Font("Tahoma", 0, 12));
        txtSearch.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtSearchKeyReleased(evt);
            }
        });

        btnClose.setFont(new java.awt.Font("Tahoma", 0, 12));
        btnClose.setText("Close");
        btnClose.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCloseActionPerformed(evt);
            }
        });

        btnSelect.setFont(new java.awt.Font("Tahoma", 0, 12));
        btnSelect.setText("Select");
        btnSelect.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSelectActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 557, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 75, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtSearch, javax.swing.GroupLayout.DEFAULT_SIZE, 310, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(btnSelect, javax.swing.GroupLayout.PREFERRED_SIZE, 74, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(btnClose, javax.swing.GroupLayout.PREFERRED_SIZE, 74, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 19, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtSearch, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnSelect)
                    .addComponent(btnClose))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 213, Short.MAX_VALUE)
                .addContainerGap())
        );

        java.awt.Dimension screenSize = java.awt.Toolkit.getDefaultToolkit().getScreenSize();
        setBounds((screenSize.width-593)/2, (screenSize.height-302)/2, 593, 302);
    }// </editor-fold>//GEN-END:initComponents

    private void btnCloseActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCloseActionPerformed
        this.dispose();
    }//GEN-LAST:event_btnCloseActionPerformed

    private void txtSearchKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtSearchKeyReleased
        udfFilter();
    }//GEN-LAST:event_txtSearchKeyReleased

    private void btnSelectActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSelectActionPerformed
        udfSelected();
    }//GEN-LAST:event_btnSelectActionPerformed

    private void udfSelected(){
        int iRow=tblList.getSelectedRow();
        if(iRow<0 ) return;
        this.sKode=tblList.getValueAt(iRow, 0).toString();

        if(srcText!=null && srcText instanceof JTextField)
            ((JTextField)srcText).setText(tblList.getValueAt(iRow, 0).toString());
        else if(srcText!=null && srcText instanceof JTable && ((JTable)srcText).getSelectedRow()>=0 && ((JTable)srcText).getSelectedColumn()>=0)
            ((JTable)srcText).setValueAt(tblList.getValueAt(iRow, 0).toString(), ((JTable)srcText).getSelectedRow(), ((JTable)srcText).getSelectedColumn());

        //if(srcText!=null)
            this.dispose();
    }
    /**
    * @param args the command line arguments
    */
    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                DlgLookup dialog = new DlgLookup(new javax.swing.JFrame(), true);
                dialog.addWindowListener(new java.awt.event.WindowAdapter() {
                    public void windowClosing(java.awt.event.WindowEvent e) {
                        System.exit(0);
                    }
                });
                dialog.setVisible(true);
            }
        });
    }

    public class MyRowRenderer extends DefaultTableCellRenderer implements TableCellRenderer{
        SimpleDateFormat dmyFmt=new SimpleDateFormat("dd/MM/yyyy");

        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {

            if(value instanceof Date ){
                value=dmyFmt.format(value);
            }else if(value instanceof Double ||value instanceof Integer ||value instanceof Float  ){
                setHorizontalAlignment(SwingConstants.RIGHT);
                value=fn.dFmt.format(value);
            }
            setFont(table.getFont());
            if(isSelected){
                setBackground(table.getSelectionBackground());
                setForeground(table.getSelectionForeground());
            }else{
                setBackground(table.getBackground());
                setForeground(table.getForeground());
            }

            setValue(value);
            return this;
        }
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnClose;
    private javax.swing.JButton btnSelect;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JScrollPane jScrollPane1;
    private org.jdesktop.swingx.JXTable tblList;
    private javax.swing.JTextField txtSearch;
    // End of variables declaration//GEN-END:variables

}
