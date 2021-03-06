/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package apotek.master;

import apotek.dao.ItemDao;
import com.klinik.dao.DiskonEventDao;
import com.klinik.model.Barang;
import com.klinik.model.DiskonEvent;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JOptionPane;
import main.GeneralFunction;
import org.jdesktop.swingx.autocomplete.AutoCompleteDecorator;

/**
 *
 * @author cak-ust
 */
public class DlgDiskonEvent extends javax.swing.JDialog {
    List<Barang> listBarang=null;
    ItemDao itemDao=new ItemDao();
    GeneralFunction fn=new GeneralFunction();
    /**
     * Creates new form DlgDiskonEvent
     */
    public DlgDiskonEvent(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
        AutoCompleteDecorator.decorate(cmbItem);
        udfInitForm();
        
        cmbTipeDiskonItemStateChanged(null);
        jXDatePicker1.setFormats("dd/MM/yyyy");
        jXDatePicker2.setFormats("dd/MM/yyyy");
    }

    private void udfInitForm(){
        listBarang=itemDao.listItemAktif();
        cmbItem.removeAllItems();
        for(Barang x: listBarang){
            cmbItem.addItem(x.getNamaPaten());
        }
    }
    
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        cmbTipeDiskon = new javax.swing.JComboBox();
        jLabel4 = new javax.swing.JLabel();
        txtDiskonPersen = new javax.swing.JTextField();
        jLabel5 = new javax.swing.JLabel();
        txtNilaiBilling = new javax.swing.JTextField();
        jLabel6 = new javax.swing.JLabel();
        txtDiskonRp = new javax.swing.JTextField();
        lblID = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        cmbTipeTarif = new javax.swing.JComboBox();
        jLabel8 = new javax.swing.JLabel();
        txtKeterangan = new javax.swing.JTextField();
        chkAktif = new javax.swing.JCheckBox();
        cmbItem = new javax.swing.JComboBox();
        lblHarga = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        jXDatePicker1 = new org.jdesktop.swingx.JXDatePicker();
        jLabel10 = new javax.swing.JLabel();
        jXDatePicker2 = new org.jdesktop.swingx.JXDatePicker();
        btnSimpan = new javax.swing.JButton();
        btnTutup = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        getContentPane().setLayout(null);

        jPanel1.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        jPanel1.setLayout(null);

        jLabel1.setText("Diskon (%)");
        jPanel1.add(jLabel1);
        jLabel1.setBounds(10, 100, 70, 20);

        jLabel2.setText("ID");
        jPanel1.add(jLabel2);
        jLabel2.setBounds(335, 20, 45, 20);

        jLabel3.setText("Nilai Billing : ");
        jPanel1.add(jLabel3);
        jLabel3.setBounds(250, 45, 80, 20);

        cmbTipeDiskon.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "ITEM", "TOTAL BILL" }));
        cmbTipeDiskon.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                cmbTipeDiskonItemStateChanged(evt);
            }
        });
        jPanel1.add(cmbTipeDiskon);
        cmbTipeDiskon.setBounds(95, 45, 130, 20);

        jLabel4.setText("Tipe Tarif");
        jPanel1.add(jLabel4);
        jLabel4.setBounds(10, 15, 70, 20);

        txtDiskonPersen.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtDiskonPersen.setText("0");
        txtDiskonPersen.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        jPanel1.add(txtDiskonPersen);
        txtDiskonPersen.setBounds(95, 100, 45, 20);

        jLabel5.setText("Item");
        jPanel1.add(jLabel5);
        jLabel5.setBounds(10, 70, 70, 20);

        txtNilaiBilling.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtNilaiBilling.setText("0");
        txtNilaiBilling.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        jPanel1.add(txtNilaiBilling);
        txtNilaiBilling.setBounds(335, 45, 125, 20);

        jLabel6.setText("Diskon (Rp)");
        jPanel1.add(jLabel6);
        jLabel6.setBounds(210, 105, 70, 15);

        txtDiskonRp.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtDiskonRp.setText("0");
        txtDiskonRp.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        jPanel1.add(txtDiskonRp);
        txtDiskonRp.setBounds(325, 100, 135, 20);

        lblID.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        jPanel1.add(lblID);
        lblID.setBounds(385, 20, 75, 20);

        jLabel7.setText("Tipe Diskon");
        jPanel1.add(jLabel7);
        jLabel7.setBounds(10, 45, 70, 20);

        cmbTipeTarif.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "KLINIK", "RESELLER" }));
        cmbTipeTarif.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                cmbTipeTarifItemStateChanged(evt);
            }
        });
        jPanel1.add(cmbTipeTarif);
        cmbTipeTarif.setBounds(95, 15, 150, 20);

        jLabel8.setText("Sampai");
        jPanel1.add(jLabel8);
        jLabel8.setBounds(225, 150, 70, 20);

        txtKeterangan.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        jPanel1.add(txtKeterangan);
        txtKeterangan.setBounds(95, 125, 365, 20);

        chkAktif.setText("Aktif");
        jPanel1.add(chkAktif);
        chkAktif.setBounds(345, 175, 115, 23);

        cmbItem.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                cmbItemItemStateChanged(evt);
            }
        });
        jPanel1.add(cmbItem);
        cmbItem.setBounds(95, 70, 285, 20);

        lblHarga.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        jPanel1.add(lblHarga);
        lblHarga.setBounds(380, 70, 80, 20);

        jLabel9.setText("Keterangan");
        jPanel1.add(jLabel9);
        jLabel9.setBounds(10, 125, 70, 20);
        jPanel1.add(jXDatePicker1);
        jXDatePicker1.setBounds(95, 150, 120, 22);

        jLabel10.setText("Mulai");
        jPanel1.add(jLabel10);
        jLabel10.setBounds(10, 150, 70, 20);
        jPanel1.add(jXDatePicker2);
        jXDatePicker2.setBounds(290, 150, 125, 22);

        getContentPane().add(jPanel1);
        jPanel1.setBounds(10, 10, 475, 205);

        btnSimpan.setText("Simpan");
        btnSimpan.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSimpanActionPerformed(evt);
            }
        });
        getContentPane().add(btnSimpan);
        btnSimpan.setBounds(310, 225, 85, 25);

        btnTutup.setText("Tutup");
        btnTutup.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnTutupActionPerformed(evt);
            }
        });
        getContentPane().add(btnTutup);
        btnTutup.setBounds(400, 225, 85, 25);

        setBounds(0, 0, 514, 290);
    }// </editor-fold>//GEN-END:initComponents

    private void btnTutupActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnTutupActionPerformed
        this.dispose();
    }//GEN-LAST:event_btnTutupActionPerformed

    private void cmbTipeDiskonItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_cmbTipeDiskonItemStateChanged
        String tipeDiskon=cmbTipeDiskon.getSelectedItem().toString();
        jLabel3.setVisible(tipeDiskon.equalsIgnoreCase("TOTAL BILL"));
        txtNilaiBilling.setVisible(tipeDiskon.equalsIgnoreCase("TOTAL BILL"));
        jLabel5.setVisible(tipeDiskon.equalsIgnoreCase("ITEM"));
        cmbItem.setVisible(tipeDiskon.equalsIgnoreCase("ITEM"));
        
    }//GEN-LAST:event_cmbTipeDiskonItemStateChanged

    private void cmbItemItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_cmbItemItemStateChanged
        int i=cmbItem.getSelectedIndex();
        if(i>=0){
            lblHarga.setText(cmbTipeTarif.getSelectedItem().toString().equalsIgnoreCase("KLINIK")? fn.intFmt.format(listBarang.get(i).getHargaKlinik()): fn.intFmt.format(listBarang.get(i).getHargaReseller()));
        }
    }//GEN-LAST:event_cmbItemItemStateChanged

    private void cmbTipeTarifItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_cmbTipeTarifItemStateChanged
        cmbItemItemStateChanged(null);
    }//GEN-LAST:event_cmbTipeTarifItemStateChanged

    private void btnSimpanActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSimpanActionPerformed
        udfSave();
    }//GEN-LAST:event_btnSimpanActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(DlgDiskonEvent.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(DlgDiskonEvent.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(DlgDiskonEvent.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(DlgDiskonEvent.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the dialog */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                DlgDiskonEvent dialog = new DlgDiskonEvent(new javax.swing.JFrame(), true);
                dialog.addWindowListener(new java.awt.event.WindowAdapter() {
                    @Override
                    public void windowClosing(java.awt.event.WindowEvent e) {
                        System.exit(0);
                    }
                });
                dialog.setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnSimpan;
    private javax.swing.JButton btnTutup;
    private javax.swing.JCheckBox chkAktif;
    private javax.swing.JComboBox cmbItem;
    private javax.swing.JComboBox cmbTipeDiskon;
    private javax.swing.JComboBox cmbTipeTarif;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private org.jdesktop.swingx.JXDatePicker jXDatePicker1;
    private org.jdesktop.swingx.JXDatePicker jXDatePicker2;
    private javax.swing.JLabel lblHarga;
    private javax.swing.JLabel lblID;
    private javax.swing.JTextField txtDiskonPersen;
    private javax.swing.JTextField txtDiskonRp;
    private javax.swing.JTextField txtKeterangan;
    private javax.swing.JTextField txtNilaiBilling;
    // End of variables declaration//GEN-END:variables

    private void udfSave() {
        DiskonEventDao dao=new DiskonEventDao();
        DiskonEvent x=new DiskonEvent();
        x.setAktif(chkAktif.isSelected());
        x.setDiskonPersen(fn.udfGetDouble(txtDiskonPersen.getText()));
        x.setDiskonRp(fn.udfGetDouble(txtDiskonRp.getText()));
        x.setItemCode(cmbTipeDiskon.getSelectedItem().toString().equalsIgnoreCase("ITEM")? listBarang.get(cmbItem.getSelectedIndex()).getItemCode(): "");
        x.setKeterangan(txtKeterangan.getText());
        x.setMinTotBill(fn.udfGetDouble(txtNilaiBilling.getText()));
        x.setTglMulai(jXDatePicker1.getDate());
        x.setTglSampai(jXDatePicker2.getDate());
        x.setTipeDiskon(cmbTipeDiskon.getSelectedItem().toString());
        x.setTipeTarif(cmbTipeTarif.getSelectedItem().toString());
        
        Integer id=dao.simpan(x);
        JOptionPane.showMessageDialog(this, "Diskon disimpan dengan id: "+id);
        this.dispose();
    }
}
