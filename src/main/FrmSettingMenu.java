/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * FrmSettmeningMenuIPD.java
 *
 * Created on Aug 25, 2009, 9:13:36 AM
 */

package main;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.DefaultListModel;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author Administrator
 */
public class FrmSettingMenu extends javax.swing.JInternalFrame {
    private Connection conn;
    GeneralFunction fn=new GeneralFunction();
    private MainForm mainForm;

    /** Creates new form FrmSettingMenuIPD */
    public FrmSettingMenu() {
        initComponents();
        jTable1.getColumn("Menu").setPreferredWidth(150);
        jTable1.getColumn("Read").setPreferredWidth(60);
        jTable1.getColumn("Insert").setPreferredWidth(60);
        jTable1.getColumn("Update").setPreferredWidth(60);
        jTable1.getColumn("Delete").setPreferredWidth(60);
        jTable1.getColumn("Print").setPreferredWidth(60);
        jTable1.getColumn("Correction").setPreferredWidth(70);

        jList1.getSelectionModel().addListSelectionListener(new ListSelectionListener() {

            public void valueChanged(ListSelectionEvent e) {
                if(conn==null) return;
                udfLoadMenuUserAuth();
            }


        });

        jTable1.getModel().addTableModelListener(new MyTableModelListener(jTable1));

//        jTable1.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
//            public void valueChanged(ListSelectionEvent e) {
//                int iRow=jTable1.getSelectedRow();
//                if(jTable1.getSelectedColumn()==1){
//                    if((Boolean)jTable1.getValueAt(iRow, jTable1.getSelectedColumn())==true){
//                        for (int i=2; i<jTable1.getColumnCount()-1; i++){
//                            jTable1.setValueAt(true, iRow, i);
//                        }
//                    }
//                }else{
//                    if((Boolean)jTable1.getValueAt(iRow, jTable1.getSelectedColumn())==false){
//                        jTable1.setValueAt(false, iRow, 1);
//                    }
//                }
//            }
//        });
        


    }

    private void udfLoadMenuUserAuth() {
        chkAll.setSelected(false);

        String sQry="select list.id, menu_description ,coalesce(can_insert, false) as can_insert, coalesce(can_update, false) as can_update, " +
                "coalesce(can_delete, false) as can_delete, coalesce(can_read, false) as can_read, coalesce(can_print, false) as can_print, " +
                "coalesce(can_correction, false) as can_correction " +
                "from m_menu_list list  " +
                (cmbGroup.getSelectedIndex()==0?  "" : "inner join m_menu_group_list g on g.menu_id=list.id and g.group_id='"+cmbGroup.getSelectedItem().toString()+"'") +
                "left join m_menu_authorization auth on auth.menu_id=list.id and user_name='"+jList1.getSelectedValue().toString()+"' " +
                "where module_name='APOTEK' " +
                "order by menu_description";
        try{
            ResultSet rs=conn.createStatement().executeQuery(sQry);
            ((DefaultTableModel)jTable1.getModel()).setNumRows(0);

            while(rs.next()){
                ((DefaultTableModel)jTable1.getModel()).addRow(new Object[]{
                    rs.getString("menu_description"),
                    (rs.getBoolean("can_read") && rs.getBoolean("can_insert") && rs.getBoolean("can_update") && rs.getBoolean("can_delete") && rs.getBoolean("can_print")),
                    rs.getBoolean("can_read"),
                    rs.getBoolean("can_insert"),
                    rs.getBoolean("can_update"),
                    rs.getBoolean("can_delete"),
                    rs.getBoolean("can_print"),
                    rs.getBoolean("can_correction"),
                    rs.getInt("id")
                });
            }
            if(jTable1.getRowCount()>0)
                jTable1.setRowSelectionInterval(0, 0);

            jTable1.setModel((DefaultTableModel)fn.autoResizeColWidth(jTable1, (DefaultTableModel)jTable1.getModel()).getModel());
        }catch(SQLException se){
            JOptionPane.showMessageDialog(null, se.getMessage());
        }
    }

    public void setMainForm(MainForm aThis) {
        mainForm=aThis;
    }

    public class MyTableModelListener implements TableModelListener {
        JTable table;

        // It is necessary to keep the table since it is not possible
        // to determine the table from the event's source
        MyTableModelListener(JTable table) {
            this.table = table;
        }

        public void tableChanged(TableModelEvent e) {
            int firstRow = e.getFirstRow();
            int lastRow = e.getLastRow();
            int mColIndex = e.getColumn();

//            if(e.getType()==TableModelEvent.UPDATE || e.getType()==TableModelEvent.INSERT ) {
//                if (firstRow == TableModelEvent.HEADER_ROW) {
//                    if (mColIndex == TableModelEvent.ALL_COLUMNS) {
//                        // A column was added
//                    } else {
//                    }
//                } else {
//                    // The rows in the range [firstRow, lastRow] changed
//                }
//            }

            if(e.getType()==TableModelEvent.UPDATE){
                table.getModel().removeTableModelListener(this);

                int iRow =e.getLastRow();//  jTable1.getSelectedRow();
                if(iRow<0) return;
                if(jTable1.getSelectedColumn()==1){
                    //if((Boolean)((DefaultTableModel)jTable1.getModel()).getValueAt(iRow, jTable1.getSelectedColumn())==true){
                        for (int i=2; i<jTable1.getColumnCount()-1; i++){
                            ((DefaultTableModel)jTable1.getModel()).setValueAt(
                                    (Boolean)((DefaultTableModel)jTable1.getModel()).getValueAt(iRow, jTable1.getSelectedColumn()),
                                    iRow, i);
                        }
                    //}
                }else{
                    if(iRow>=0 && jTable1.getSelectedColumn()>1 && ((DefaultTableModel)jTable1.getModel()).getValueAt(iRow, jTable1.getSelectedColumn())!=null &&
                            (Boolean)((DefaultTableModel)jTable1.getModel()).getValueAt(iRow, jTable1.getSelectedColumn())==false){
                        ((DefaultTableModel)jTable1.getModel()).setValueAt(false, iRow, 1);
                    }
                }

                table.getModel().addTableModelListener(this);

            }
        }
    }

    public void setConn(Connection conn) {
        this.conn=conn;
    }

    private void udfSave(){
        String sIns="";
        try{
            conn.setAutoCommit(false);
            for (int i=0; i<jTable1.getRowCount(); i++){
                sIns+=  "Delete from m_menu_authorization where menu_id="+jTable1.getValueAt(i, jTable1.getColumnModel().getColumnIndex("ID")).toString()+" " +
                        "and user_name='"+jList1.getSelectedValue().toString()+"' ; " +
                        "Insert into m_menu_authorization(menu_id, user_name, can_insert, can_update," +
                        "can_delete, can_read, can_print, can_correction) select " +
                        "'"+jTable1.getValueAt(i, jTable1.getColumnModel().getColumnIndex("ID")).toString()+"'," +
                        "'"+jList1.getSelectedValue().toString()+"', " +
                        (Boolean)jTable1.getValueAt(i, jTable1.getColumnModel().getColumnIndex("Insert")) +", " +
                        (Boolean)jTable1.getValueAt(i, jTable1.getColumnModel().getColumnIndex("Update")) +", " +
                        (Boolean)jTable1.getValueAt(i, jTable1.getColumnModel().getColumnIndex("Delete")) +", " +
                        (Boolean)jTable1.getValueAt(i, jTable1.getColumnModel().getColumnIndex("Read")) +", " +
                        (Boolean)jTable1.getValueAt(i, jTable1.getColumnModel().getColumnIndex("Print")) +", " +
                        (Boolean)jTable1.getValueAt(i, jTable1.getColumnModel().getColumnIndex("Correction")) +" ;";
            }
            System.out.println(sIns);
            
            int iR=conn.createStatement(ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_UPDATABLE).executeUpdate(sIns);
            conn.setAutoCommit(true);
            
            if (iR>0) JOptionPane.showMessageDialog(this, "Update Successful!");

            mainForm.udfSetUserMenu();

        }catch(SQLException se){
            try {
                conn.setAutoCommit(true);
                conn.rollback();
                JOptionPane.showMessageDialog(this, se.getMessage());
            } catch (SQLException ex) {
                Logger.getLogger(FrmSettingMenu.class.getName()).log(Level.SEVERE, null, ex);
            }

        }
    }

    private void udfInitForm(){
        try{
            ResultSet rs=conn.createStatement().executeQuery("select username from m_user " +
                    "order by upper(username)");
            DefaultListModel lstModel=new DefaultListModel();

            while(rs.next()){
                lstModel.addElement(rs.getString("username"));
            }
            jList1.setModel(lstModel);
            if(jList1.getModel().getSize()>0) jList1.setSelectedIndex(0);

            rs.close();
            rs=conn.createStatement().executeQuery("select group_id from m_menu_group order by 1");
            cmbGroup.removeAllItems();
            cmbGroup.addItem("< Semua >");
            while(rs.next()){
                cmbGroup.addItem(rs.getString(1));
            }

            rs.close();
        }catch(SQLException se){
            JOptionPane.showMessageDialog(this, se.getMessage());
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
        jList1 = new javax.swing.JList();
        jLabel1 = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();
        jLabel2 = new javax.swing.JLabel();
        jSeparator1 = new javax.swing.JSeparator();
        jButton2 = new javax.swing.JButton();
        jButton1 = new javax.swing.JButton();
        chkAll = new javax.swing.JCheckBox();
        jLabel3 = new javax.swing.JLabel();
        cmbGroup = new javax.swing.JComboBox();

        setClosable(true);
        setTitle("Menu Setting"); // NOI18N
        addInternalFrameListener(new javax.swing.event.InternalFrameListener() {
            public void internalFrameActivated(javax.swing.event.InternalFrameEvent evt) {
            }
            public void internalFrameClosed(javax.swing.event.InternalFrameEvent evt) {
            }
            public void internalFrameClosing(javax.swing.event.InternalFrameEvent evt) {
            }
            public void internalFrameDeactivated(javax.swing.event.InternalFrameEvent evt) {
            }
            public void internalFrameDeiconified(javax.swing.event.InternalFrameEvent evt) {
            }
            public void internalFrameIconified(javax.swing.event.InternalFrameEvent evt) {
            }
            public void internalFrameOpened(javax.swing.event.InternalFrameEvent evt) {
                formInternalFrameOpened(evt);
            }
        });
        getContentPane().setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jList1.setModel(new javax.swing.AbstractListModel() {
            String[] strings = { "Test" };
            public int getSize() { return strings.length; }
            public Object getElementAt(int i) { return strings[i]; }
        });
        jScrollPane1.setViewportView(jList1);

        getContentPane().add(jScrollPane1, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 30, 160, 454));

        jLabel1.setFont(new java.awt.Font("Tahoma", 1, 11));
        jLabel1.setText("User List :");
        getContentPane().add(jLabel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 10, 160, 20));

        jTable1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Menu", "All", "Read", "Insert", "Update", "Delete", "Print", "Correction", "ID"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.Boolean.class, java.lang.Boolean.class, java.lang.Boolean.class, java.lang.Boolean.class, java.lang.Boolean.class, java.lang.Boolean.class, java.lang.Boolean.class, java.lang.Integer.class
            };
            boolean[] canEdit = new boolean [] {
                false, true, true, true, true, true, true, true, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jTable1.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_OFF);
        jTable1.getTableHeader().setReorderingAllowed(false);
        jScrollPane2.setViewportView(jTable1);

        getContentPane().add(jScrollPane2, new org.netbeans.lib.awtextra.AbsoluteConstraints(180, 30, 540, 424));

        jLabel2.setFont(new java.awt.Font("Tahoma", 1, 11));
        jLabel2.setText("Menu Authorization");
        getContentPane().add(jLabel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(180, 10, 210, 20));
        getContentPane().add(jSeparator1, new org.netbeans.lib.awtextra.AbsoluteConstraints(180, 474, 240, 10));

        jButton2.setText("Close");
        jButton2.setMargin(new java.awt.Insets(2, 2, 2, 2));
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });
        getContentPane().add(jButton2, new org.netbeans.lib.awtextra.AbsoluteConstraints(640, 464, 80, 30));

        jButton1.setText("Update");
        jButton1.setMargin(new java.awt.Insets(2, 2, 2, 2));
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });
        getContentPane().add(jButton1, new org.netbeans.lib.awtextra.AbsoluteConstraints(540, 464, 100, 30));

        chkAll.setText("Check All");
        chkAll.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chkAllActionPerformed(evt);
            }
        });
        getContentPane().add(chkAll, new org.netbeans.lib.awtextra.AbsoluteConstraints(420, 465, 120, -1));

        jLabel3.setText("Group");
        getContentPane().add(jLabel3, new org.netbeans.lib.awtextra.AbsoluteConstraints(390, 5, 70, 20));

        cmbGroup.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        cmbGroup.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                cmbGroupItemStateChanged(evt);
            }
        });
        cmbGroup.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmbGroupActionPerformed(evt);
            }
        });
        getContentPane().add(cmbGroup, new org.netbeans.lib.awtextra.AbsoluteConstraints(470, 5, 250, -1));

        java.awt.Dimension screenSize = java.awt.Toolkit.getDefaultToolkit().getScreenSize();
        setBounds((screenSize.width-746)/2, (screenSize.height-529)/2, 746, 529);
    }// </editor-fold>//GEN-END:initComponents

    private void formInternalFrameOpened(javax.swing.event.InternalFrameEvent evt) {//GEN-FIRST:event_formInternalFrameOpened
        udfInitForm();
    }//GEN-LAST:event_formInternalFrameOpened

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        udfSave();
    }//GEN-LAST:event_jButton1ActionPerformed

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        this.dispose();
    }//GEN-LAST:event_jButton2ActionPerformed

    private void chkAllActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chkAllActionPerformed
        for(int i=0; i<jTable1.getRowCount(); i++){
            ((DefaultTableModel)jTable1.getModel()).setValueAt(chkAll.isSelected(), i, 1);
            ((DefaultTableModel)jTable1.getModel()).setValueAt(chkAll.isSelected(), i, 2);
            ((DefaultTableModel)jTable1.getModel()).setValueAt(chkAll.isSelected(), i, 3);
            ((DefaultTableModel)jTable1.getModel()).setValueAt(chkAll.isSelected(), i, 4);
            ((DefaultTableModel)jTable1.getModel()).setValueAt(chkAll.isSelected(), i, 5);
            ((DefaultTableModel)jTable1.getModel()).setValueAt(chkAll.isSelected(), i, 6);
            ((DefaultTableModel)jTable1.getModel()).setValueAt(chkAll.isSelected(), i, 7);
        }
    }//GEN-LAST:event_chkAllActionPerformed

    private void cmbGroupActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmbGroupActionPerformed
        
    }//GEN-LAST:event_cmbGroupActionPerformed

    private void cmbGroupItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_cmbGroupItemStateChanged
        if(cmbGroup.getSelectedIndex()>=0)
            udfLoadMenuUserAuth();
    }//GEN-LAST:event_cmbGroupItemStateChanged


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JCheckBox chkAll;
    private javax.swing.JComboBox cmbGroup;
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JList jList1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JTable jTable1;
    // End of variables declaration//GEN-END:variables

}
