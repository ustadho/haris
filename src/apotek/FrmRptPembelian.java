/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * FrmRptPembelian.java
 *
 * Created on Jul 28, 2010, 1:14:42 PM
 */

package apotek;

import main.MainForm;
import java.awt.Cursor;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import javax.swing.JOptionPane;
import main.GeneralFunction;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.util.JRLoader;
import net.sf.jasperreports.view.JasperViewer;

/**
 *
 * @author ustadho
 */
public class FrmRptPembelian extends javax.swing.JInternalFrame {
    private Connection conn;
    GeneralFunction fn;
    List lstJenisBarang=new ArrayList();

    /** Creates new form FrmRptPembelian */
    public FrmRptPembelian() {
        initComponents();
    }

    public void setConn(Connection con){
        this.conn=con;
    }

    private void udfInitForm(){
        fn=new GeneralFunction(conn);
        jXDatePicker1.setFormats(new String[]{"dd/MM/yyyy"});
        jXDatePicker2.setFormats(new String[]{"dd/MM/yyyy"});
        jList1.setSelectedIndex(0);
        txtSite.setText(MainForm.sKodeGudang);
        lblSite.setText(MainForm.sNamaGudang);
        cmbKategori.removeAllItems();
        lstJenisBarang.clear();
        cmbKategori.addItem("All");
        lstJenisBarang.add("");
        try{
            ResultSet rs=conn.createStatement().executeQuery("select kode_jenis, coalesce(jenis_barang,'') as jenis_barang "
                    + "from jenis_barang order by 1");
            while(rs.next()){
                lstJenisBarang.add(rs.getString(1));
                cmbKategori.addItem(rs.getString(2));
            }
            rs.close();
        }catch(SQLException se){
            JOptionPane.showMessageDialog(this, se.getMessage());
        }
    }

    private void udfPrint() {
        try{
            HashMap reportParam = new HashMap();
            JasperReport jasperReport=null;
            DateFormat dformat = new SimpleDateFormat("yyyy-MM-dd");
            String tglAwal = dformat.format(jXDatePicker1.getDate());
            String tglAkhir = dformat.format(jXDatePicker2.getDate());
            String sReport="";
            reportParam.put("corporate", MainForm.sNamaUsaha);
            reportParam.put("alamat", MainForm.sAlamat);
            reportParam.put("telp", MainForm.sTelp);
            reportParam.put("tanggal1", tglAwal);
            reportParam.put("tanggal2", tglAkhir);
            reportParam.put("supplier", txtSupp.getText());
            reportParam.put("gudang", txtSite.getText());
            reportParam.put("kategori", cmbKategori.getSelectedIndex()==0? "": cmbKategori.getSelectedItem().toString());
            
            switch (jList1.getSelectedIndex()){
                case 0:{sReport="PO_Rekap";   break;}// PO Rekap
                case 1:{sReport="PO_BySuppDetail";  break;}// PO Deteil
                case 2:{sReport="GR_BySuppRekap";   break;} //sudah
                case 3:{sReport="GR_BpTypeDetail";  break;} //
                case 4:{sReport="Pembelian_10_BesarBySupp";   break;} //
                case 5:{sReport="GR_TotalItemPerSupp"; break;} //
                case 6:{sReport="GR_PurchaseByItem";     break;}
                case 7:{sReport="GR_ItemAmountSum";     break;}
                case 8:{sReport="GR_ItemQtySum";     break;}
                case 9:{sReport="GR_APJtTempo";     break;}
                case 10:{sReport="GR_ReturDet"; break;}
                case 11:{sReport="GR_ReturSum"; break;}

                default:{
                    break;
                }
            }
            if(sReport.length()==0){
                JOptionPane.showMessageDialog(this, "Report tidak ditemukan!");
                return ;
            }
            this.setCursor(new Cursor(Cursor.WAIT_CURSOR));
            //System.out.println(getClass().getResourceAsStream("Reports/"+sReport+".jasper"));
            jasperReport = (JasperReport) JRLoader.loadObject(getClass().getResourceAsStream("Reports/"+sReport+".jasper"));
            JasperPrint print = JasperFillManager.fillReport(jasperReport,reportParam,conn);
            print.setOrientation(jasperReport.getOrientationValue());
            this.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
            if(print.getPages().isEmpty())
                JOptionPane.showMessageDialog(this, "Report tidak ditemukan!");
            else
                JasperViewer.viewReport(print,false);

        }
        catch(JRException je){System.out.println(je.getMessage());}
        //catch(NullPointerException ne){JOptionPane.showMessageDialog(null, ne.getMessage(), MainForm.sMessage, JOptionPane.OK_OPTION);}

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
        jPanel1 = new javax.swing.JPanel();
        jXDatePicker1 = new org.jdesktop.swingx.JXDatePicker();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jXDatePicker2 = new org.jdesktop.swingx.JXDatePicker();
        jLabel17 = new javax.swing.JLabel();
        txtSite = new javax.swing.JTextField();
        lblSite = new javax.swing.JLabel();
        txtSupp = new javax.swing.JTextField();
        lblSupp = new javax.swing.JLabel();
        jLabel19 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        cmbKategori = new javax.swing.JComboBox();
        jButton1 = new javax.swing.JButton();
        jButton2 = new javax.swing.JButton();

        setClosable(true);
        setIconifiable(true);
        setMaximizable(true);
        setResizable(true);
        setTitle("Laporan Pembelian"); // NOI18N
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

        jList1.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jList1.setModel(new javax.swing.AbstractListModel() {
            String[] strings = { "1.  Purhcase Order (Rekap)", "2.  Purchase Order By Supplier (Detail)", "3.  Ringkasan Faktur Pembelian per Supplier", "4.  Rincian Faktur Pembelian per Supplier", "5.  Laporan Pembelian 10 Besar / Supplier", "6.  Total Item Pembelian (TTB) Per Supplier", "7.  Rekap Pembelian per Item/ Barang", "8.  Rekap Pembelian per Nilai Item/ Barang", "9.  Rekap Pembelian per Qty Item/ Barang", "10.Hutang Jatuh Tempo", "11.Rincian Retur Pembelian", "12.Ringkasan Retur Pembelian ", " " };
            public int getSize() { return strings.length; }
            public Object getElementAt(int i) { return strings[i]; }
        });
        jList1.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        jList1.addListSelectionListener(new javax.swing.event.ListSelectionListener() {
            public void valueChanged(javax.swing.event.ListSelectionEvent evt) {
                jList1ValueChanged(evt);
            }
        });
        jScrollPane1.setViewportView(jList1);

        getContentPane().add(jScrollPane1, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 11, 300, 360));

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder("Parameter"));
        jPanel1.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jXDatePicker1.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jPanel1.add(jXDatePicker1, new org.netbeans.lib.awtextra.AbsoluteConstraints(110, 30, 140, -1));

        jLabel1.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jLabel1.setText("Tanggal Awal");
        jPanel1.add(jLabel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 30, 90, 20));

        jLabel2.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jLabel2.setText("Kategori Item");
        jPanel1.add(jLabel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 90, 90, 20));

        jXDatePicker2.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jPanel1.add(jXDatePicker2, new org.netbeans.lib.awtextra.AbsoluteConstraints(110, 60, 140, -1));

        jLabel17.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jLabel17.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel17.setText("Site ID");
        jPanel1.add(jLabel17, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 120, 90, 20));

        txtSite.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
        txtSite.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 1, true));
        txtSite.setDisabledTextColor(new java.awt.Color(153, 153, 153));
        txtSite.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                txtSiteFocusLost(evt);
            }
        });
        txtSite.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtSiteKeyReleased(evt);
            }
        });
        jPanel1.add(txtSite, new org.netbeans.lib.awtextra.AbsoluteConstraints(110, 120, 50, 20));

        lblSite.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
        lblSite.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 1, true));
        lblSite.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                lblSitePropertyChange(evt);
            }
        });
        jPanel1.add(lblSite, new org.netbeans.lib.awtextra.AbsoluteConstraints(160, 120, 190, 20));

        txtSupp.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
        txtSupp.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 1, true));
        txtSupp.setDisabledTextColor(new java.awt.Color(153, 153, 153));
        txtSupp.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                txtSuppFocusLost(evt);
            }
        });
        txtSupp.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtSuppKeyReleased(evt);
            }
        });
        jPanel1.add(txtSupp, new org.netbeans.lib.awtextra.AbsoluteConstraints(110, 145, 50, 20));

        lblSupp.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
        lblSupp.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 1, true));
        lblSupp.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                lblSuppPropertyChange(evt);
            }
        });
        jPanel1.add(lblSupp, new org.netbeans.lib.awtextra.AbsoluteConstraints(160, 145, 190, 20));

        jLabel19.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jLabel19.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel19.setText("Supplier");
        jPanel1.add(jLabel19, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 145, 90, 20));

        jLabel3.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jLabel3.setText("Tanggal Akhir");
        jPanel1.add(jLabel3, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 60, 90, 20));

        cmbKategori.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        jPanel1.add(cmbKategori, new org.netbeans.lib.awtextra.AbsoluteConstraints(110, 90, 240, -1));

        getContentPane().add(jPanel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(315, 10, 370, 360));

        jButton1.setText("Preview");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });
        getContentPane().add(jButton1, new org.netbeans.lib.awtextra.AbsoluteConstraints(500, 380, 90, 30));

        jButton2.setText("Close");
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });
        getContentPane().add(jButton2, new org.netbeans.lib.awtextra.AbsoluteConstraints(590, 380, 90, 30));

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void txtSiteFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtSiteFocusLost
        // TODO add your handling code here:
}//GEN-LAST:event_txtSiteFocusLost

    private void txtSiteKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtSiteKeyReleased
        fn.lookup(evt, new Object[]{lblSite},
                "select kode_gudang, coalesce(nama_gudang,'') as nama_gudang from r_gudang " +
                "where upper(kode_gudang||coalesce(nama_gudang,'')) Like upper('%" + txtSite.getText() +"%') order by 1",
                txtSite.getWidth()+lblSite.getWidth(), 300);
}//GEN-LAST:event_txtSiteKeyReleased

    private void lblSitePropertyChange(java.beans.PropertyChangeEvent evt) {//GEN-FIRST:event_lblSitePropertyChange
        // TODO add your handling code here:
}//GEN-LAST:event_lblSitePropertyChange

    private void txtSuppFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtSuppFocusLost
        // TODO add your handling code here:
    }//GEN-LAST:event_txtSuppFocusLost

    private void txtSuppKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtSuppKeyReleased
        fn.lookup(evt, new Object[]{lblSupp},
                "select kode_supp as kode, coalesce(nama_supp,'') as supplier from r_supplier " +
                "where upper(kode_supp||coalesce(nama_supp,'')) Like upper('%" + txtSupp.getText() +"%') order by 2",
                txtSupp.getWidth()+lblSupp.getWidth()+15, 300);
    }//GEN-LAST:event_txtSuppKeyReleased

    private void lblSuppPropertyChange(java.beans.PropertyChangeEvent evt) {//GEN-FIRST:event_lblSuppPropertyChange
        // TODO add your handling code here:
    }//GEN-LAST:event_lblSuppPropertyChange

    private void formInternalFrameOpened(javax.swing.event.InternalFrameEvent evt) {//GEN-FIRST:event_formInternalFrameOpened
        udfInitForm();
        
    }//GEN-LAST:event_formInternalFrameOpened

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        udfPrint();
}//GEN-LAST:event_jButton1ActionPerformed

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        this.dispose();
    }//GEN-LAST:event_jButton2ActionPerformed

    private void jList1ValueChanged(javax.swing.event.ListSelectionEvent evt) {//GEN-FIRST:event_jList1ValueChanged
//        lblItemType.setVisible(jList1.getSelectedIndex()==9);
//        cmbItemType.setVisible(jList1.getSelectedIndex()==9);
    }//GEN-LAST:event_jList1ValueChanged


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JComboBox cmbKategori;
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel19;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JList jList1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private org.jdesktop.swingx.JXDatePicker jXDatePicker1;
    private org.jdesktop.swingx.JXDatePicker jXDatePicker2;
    private javax.swing.JLabel lblSite;
    private javax.swing.JLabel lblSupp;
    private javax.swing.JTextField txtSite;
    private javax.swing.JTextField txtSupp;
    // End of variables declaration//GEN-END:variables

}
