/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * FrmPettyCashList.java
 *
 * Created on 13 Mei 11, 21:11:41
 */
package apotek.pettycash;

import apotek.JDesktopImage;
import java.beans.PropertyVetoException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.swing.JOptionPane;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableModel;
import main.GeneralFunction;
import main.MainForm;

/**
 *
 * @author cak-ust
 */
public class FrmPettyCashList extends javax.swing.JInternalFrame {
    private Connection conn;
    private GeneralFunction fn=new GeneralFunction();
    private JDesktopImage desktopPane;
    private MainForm mainForm;
            
    
    /** Creates new form FrmPettyCashList */
    public FrmPettyCashList() {
        initComponents();
        jDateAwal.setFormats(new String[]{"dd/MM/yyyy"});
        jDateAkhir.setFormats(new String[]{"dd/MM/yyyy"});
        //jXTable1.setHighlighters(HighlighterFactory.createAlternateStriping());
        //jXTable1.setHighlighters(org.jdesktop.swingx.decorator.Highlighter);
//        btnEdit.setVisible(false);
        jXTable1.getSelectionModel().addListSelectionListener(new ListSelectionListener() {

            public void valueChanged(ListSelectionEvent e) {
                int iRow=jXTable1.getSelectedRow();
                btnDelete.setEnabled(iRow>=0);
                btnEdit.setEnabled(iRow>=0);
                
            }
        });
        jXTable1.getTableHeader().setReorderingAllowed(false);
        jXTable1.getModel().addTableModelListener(new TableModelListener() {

            public void tableChanged(TableModelEvent e) {
                int colMasuk=jXTable1.getColumnModel().getColumnIndex("Masuk");
                int colKeluar=jXTable1.getColumnModel().getColumnIndex("Keluar");
                double masuk=0, keluar=0;
                for (int i = 0; i < jXTable1.getRowCount(); i++) {
                    masuk+=fn.udfGetDouble(jXTable1.getValueAt(i, colMasuk));
                    keluar+=fn.udfGetDouble(jXTable1.getValueAt(i, colKeluar));
                }
                lblTotMasuk.setText(fn.intFmt.format(masuk));
                lblTotKeluar.setText(fn.intFmt.format(keluar));
            }
        });
    }
    
    
    public void setMainForm(MainForm mf){
        this.mainForm=mf;
    }
    
    public void setConn(Connection con){
        this.conn=con;
        fn.setConn(conn);
    }
    
    public void udfLoadPettyCash(){
        try{
            ResultSet rs=conn.createStatement().executeQuery("select no_trx, tanggal, coalesce(bayar_ke_dari,'') as bayar_ke_dari, "
                    + "coalesce(keterangan,'') as keterangan, coalesce(keluar,0) as keluar, coalesce(masuk,0) as masuk "
                    + "from petty_cash where to_Char(tanggal, 'yyyy-MM-dd')>='"+fn.yyyymmdd_format.format(jDateAwal.getDate()) +"' "
                    + "and to_Char(tanggal, 'yyyy-MM-dd')<='"+fn.yyyymmdd_format.format(jDateAkhir.getDate()) +"' order by time_ins "); 
            ((DefaultTableModel)jXTable1.getModel()).setNumRows(0);
            while(rs.next()){
                ((DefaultTableModel)jXTable1.getModel()).addRow(new Object[]{
                    rs.getString("no_trx"),
                    rs.getDate("tanggal"),
                    rs.getString("bayar_ke_dari"),
                    rs.getString("keterangan"),
                    rs.getDouble("masuk"),
                    rs.getDouble("keluar")
                });
            }
            if(jXTable1.getRowCount()>0){
                jXTable1.setRowSelectionInterval(0, 0);
                jXTable1.setModel((DefaultTableModel)fn.autoResizeColWidth(jXTable1, (DefaultTableModel)jXTable1.getModel()).getModel());
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
        jXTable1 = new org.jdesktop.swingx.JXTable();
        jPanel1 = new javax.swing.JPanel();
        jDateAkhir = new org.jdesktop.swingx.JXDatePicker();
        jDateAwal = new org.jdesktop.swingx.JXDatePicker();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        btnTampil = new javax.swing.JButton();
        jPanel2 = new javax.swing.JPanel();
        btnDelete = new javax.swing.JButton();
        btnMasuk = new javax.swing.JButton();
        btnEdit = new javax.swing.JButton();
        btnKeluar = new javax.swing.JButton();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        lblTotKeluar = new javax.swing.JLabel();
        lblTotMasuk = new javax.swing.JLabel();

        setClosable(true);
        setIconifiable(true);
        setMaximizable(true);
        setResizable(true);
        setTitle("Daftar Petty Cash");
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

        jScrollPane1.setName("jScrollPane1"); // NOI18N

        jXTable1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "No. Trx", "Tanggal", "Terima Dari/ Dibayar Kepada", "Keterangan", "Masuk", "Keluar"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Double.class, java.lang.Double.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jXTable1.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_OFF);
        jXTable1.setName("jXTable1"); // NOI18N
        jScrollPane1.setViewportView(jXTable1);

        jPanel1.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        jPanel1.setName("jPanel1"); // NOI18N
        jPanel1.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jDateAkhir.setName("jDateAkhir"); // NOI18N
        jPanel1.add(jDateAkhir, new org.netbeans.lib.awtextra.AbsoluteConstraints(240, 10, 130, -1));

        jDateAwal.setName("jDateAwal"); // NOI18N
        jPanel1.add(jDateAwal, new org.netbeans.lib.awtextra.AbsoluteConstraints(60, 10, 120, -1));

        jLabel1.setText("Sampai");
        jLabel1.setName("jLabel1"); // NOI18N
        jPanel1.add(jLabel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(190, 10, 40, 20));

        jLabel2.setText("Dari");
        jLabel2.setName("jLabel2"); // NOI18N
        jPanel1.add(jLabel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 10, 50, 20));

        btnTampil.setText("Tampilkan");
        btnTampil.setName("btnTampil"); // NOI18N
        btnTampil.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnTampilActionPerformed(evt);
            }
        });
        jPanel1.add(btnTampil, new org.netbeans.lib.awtextra.AbsoluteConstraints(380, 10, 80, -1));

        jPanel2.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        jPanel2.setName("jPanel2"); // NOI18N
        jPanel2.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        btnDelete.setText("Hapus");
        btnDelete.setEnabled(false);
        btnDelete.setMargin(new java.awt.Insets(2, 2, 2, 2));
        btnDelete.setName("btnDelete"); // NOI18N
        btnDelete.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnDeleteActionPerformed(evt);
            }
        });
        jPanel2.add(btnDelete, new org.netbeans.lib.awtextra.AbsoluteConstraints(325, 10, 90, -1));

        btnMasuk.setText("Kas Masuk");
        btnMasuk.setMargin(new java.awt.Insets(2, 2, 2, 2));
        btnMasuk.setName("btnMasuk"); // NOI18N
        btnMasuk.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnMasukActionPerformed(evt);
            }
        });
        jPanel2.add(btnMasuk, new org.netbeans.lib.awtextra.AbsoluteConstraints(5, 10, 100, -1));

        btnEdit.setText("Ubah");
        btnEdit.setEnabled(false);
        btnEdit.setMargin(new java.awt.Insets(2, 2, 2, 2));
        btnEdit.setName("btnEdit"); // NOI18N
        btnEdit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnEditActionPerformed(evt);
            }
        });
        jPanel2.add(btnEdit, new org.netbeans.lib.awtextra.AbsoluteConstraints(225, 10, 80, -1));

        btnKeluar.setText("Kas Keluar");
        btnKeluar.setMargin(new java.awt.Insets(2, 2, 2, 2));
        btnKeluar.setName("btnKeluar"); // NOI18N
        btnKeluar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnKeluarActionPerformed(evt);
            }
        });
        jPanel2.add(btnKeluar, new org.netbeans.lib.awtextra.AbsoluteConstraints(105, 10, 110, -1));

        jLabel3.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel3.setText("Keluar");
        jLabel3.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        jLabel3.setName("jLabel3"); // NOI18N
        jPanel2.add(jLabel3, new org.netbeans.lib.awtextra.AbsoluteConstraints(640, 5, 90, -1));

        jLabel4.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel4.setText("Masuk");
        jLabel4.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        jLabel4.setName("jLabel4"); // NOI18N
        jPanel2.add(jLabel4, new org.netbeans.lib.awtextra.AbsoluteConstraints(550, 5, 90, -1));

        lblTotKeluar.setBackground(new java.awt.Color(204, 204, 255));
        lblTotKeluar.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lblTotKeluar.setText("0");
        lblTotKeluar.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        lblTotKeluar.setName("lblTotKeluar"); // NOI18N
        lblTotKeluar.setOpaque(true);
        jPanel2.add(lblTotKeluar, new org.netbeans.lib.awtextra.AbsoluteConstraints(640, 20, 90, -1));

        lblTotMasuk.setBackground(new java.awt.Color(204, 204, 255));
        lblTotMasuk.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lblTotMasuk.setText("0");
        lblTotMasuk.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        lblTotMasuk.setName("lblTotMasuk"); // NOI18N
        lblTotMasuk.setOpaque(true);
        jPanel2.add(lblTotMasuk, new org.netbeans.lib.awtextra.AbsoluteConstraints(550, 20, 90, -1));

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jScrollPane1)
                .addGap(3, 3, 3))
            .addGroup(layout.createSequentialGroup()
                .addGap(5, 5, 5)
                .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, 740, Short.MAX_VALUE)
                .addGap(5, 5, 5))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(3, 3, 3)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 195, Short.MAX_VALUE)
                .addGap(7, 7, 7)
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(6, 6, 6))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnTampilActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnTampilActionPerformed
        udfLoadPettyCash();
    }//GEN-LAST:event_btnTampilActionPerformed

    private void formInternalFrameOpened(javax.swing.event.InternalFrameEvent evt) {//GEN-FIRST:event_formInternalFrameOpened
        udfLoadPettyCash();
    }//GEN-LAST:event_formInternalFrameOpened

    private void btnDeleteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnDeleteActionPerformed
        if(JOptionPane.showConfirmDialog(this, "Anda yakin untuk menghapus petty cash ini?", "Hapus Petty Cash", JOptionPane.YES_NO_OPTION)==JOptionPane.YES_OPTION){
            try{
                String sNoTrx=jXTable1.getValueAt(jXTable1.getSelectedRow(), 0).toString();
                int i=conn.createStatement().executeUpdate("delete from petty_cash where no_trx='"+sNoTrx+"'");
                if(i>0){
                    JOptionPane.showMessageDialog(this, "Petty Cash telah terhapus!");
                    udfLoadPettyCash();
                }
            }catch(SQLException se){
                JOptionPane.showMessageDialog(this, se.getMessage());
            }
        }
    }//GEN-LAST:event_btnDeleteActionPerformed

    private void btnEditActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnEditActionPerformed
        String sNoTrx=jXTable1.getValueAt(jXTable1.getSelectedRow(), 0).toString();
        FrmPettyCash f1=new FrmPettyCash();
        f1.setConn(conn);
        f1.udfLoadPettyCash(sNoTrx);
        f1.setVisible(true);
        f1.setSrcForm(this);
        f1.setBounds(0, 0, f1.getWidth(), f1.getHeight());
        desktopPane.add(f1, javax.swing.JLayeredPane.DEFAULT_LAYER);
        try{
            f1.setSelected(true);
        } catch(PropertyVetoException PO){}
    }//GEN-LAST:event_btnEditActionPerformed

    private void btnMasukActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnMasukActionPerformed
//        FrmPettyCash f1=new FrmPettyCash();
//        f1.setConn(conn);
//        f1.setVisible(true);
//        f1.setSrcForm(this);
//        f1.setBounds(0, 0, f1.getWidth(), f1.getHeight());
//        desktopPane.add(f1, javax.swing.JLayeredPane.DEFAULT_LAYER);
//        try{
//            f1.setSelected(true);
//        } catch(PropertyVetoException PO){}
        if(mainForm!=null){
            mainForm.udfLoadPettyCash("M");
        }
    }//GEN-LAST:event_btnMasukActionPerformed

    private void btnKeluarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnKeluarActionPerformed
        if(mainForm!=null){
            mainForm.udfLoadPettyCash("K");
        }
    }//GEN-LAST:event_btnKeluarActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnDelete;
    private javax.swing.JButton btnEdit;
    private javax.swing.JButton btnKeluar;
    private javax.swing.JButton btnMasuk;
    private javax.swing.JButton btnTampil;
    private org.jdesktop.swingx.JXDatePicker jDateAkhir;
    private org.jdesktop.swingx.JXDatePicker jDateAwal;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JScrollPane jScrollPane1;
    private org.jdesktop.swingx.JXTable jXTable1;
    private javax.swing.JLabel lblTotKeluar;
    private javax.swing.JLabel lblTotMasuk;
    // End of variables declaration//GEN-END:variables

    public void setDesktopPane(JDesktopImage jDesktopPane1) {
        this.desktopPane=jDesktopPane1;
    }
}
