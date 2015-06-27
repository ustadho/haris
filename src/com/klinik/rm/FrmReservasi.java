/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.klinik.rm;

import apotek.Main;
import com.klinik.dao.ReservasiDao;
import com.klinik.model.BaseModel;
import com.klinik.model.Pasien;
import com.klinik.model.Reservasi;
import java.awt.Color;
import java.awt.Component;
import java.awt.Frame;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import main.GeneralFunction;
import static main.MainForm.conn;
import static main.MainForm.jDesktopPane1;

/**
 *
 * @author cak-ust
 */
public class FrmReservasi extends javax.swing.JInternalFrame {
    List<BaseModel> listDokter=new ArrayList<BaseModel>();
    ReservasiDao dao=new ReservasiDao();
    private GeneralFunction fn=new GeneralFunction();
    /**
     * Creates new form FrmReservasi
     */
    public FrmReservasi() {
        try {
            initComponents();
            jXTable1.setRowHeight(22);
            cmbDokter.removeAllItems();
            ResultSet rs=Main.conn.createStatement().executeQuery("select kode_dokter, nama ||coalesce(', '||gelar_depan,'')||coalesce(', '||gelar_belakang,'') as nama "
                    + "from rm_dokter order by nama");
            while(rs.next()){
                BaseModel b=new BaseModel();
                b.setKode(rs.getString("kode_dokter"));
                b.setNama(rs.getString("nama"));
                listDokter.add(b);
                cmbDokter.addItem(rs.getString("nama"));
            }
            rs.close();
            
//            jXTable1.setHighlighters(HighlighterFactory.createAlternateStriping());
            
            jXTable1.getSelectionModel().addListSelectionListener(new ListSelectionListener() {

                @Override
                public void valueChanged(ListSelectionEvent e) {
                    btnTransaksi.setEnabled(false);
                    int iRow=jXTable1.getSelectedRow();
                    String status="";
                    if(iRow>=0){
                        Integer id=fn.udfGetInt(jXTable1.getValueAt(iRow, jXTable1.getColumnModel().getColumnIndex("ID")).toString());
                        Reservasi rv=dao.findOne(id);
                        
                        status=rv.getStatus();
                        jXTable1.setValueAt(status, iRow, jXTable1.getColumnModel().getColumnIndex("Status"));
                        String noReg=jXTable1.getValueAt(iRow, jXTable1.getColumnModel().getColumnIndex("No. Reg"))==null?"": jXTable1.getValueAt(iRow, jXTable1.getColumnModel().getColumnIndex("No. Reg")).toString();
                        btnTransaksi.setEnabled(noReg.length()>0);
                    }
                    
                    
                    btnBatal.setEnabled(iRow>=0 && status.equalsIgnoreCase("Reservasi"));
                    btnRegistrasi.setEnabled(iRow>=0 && status.equalsIgnoreCase("Reservasi"));
                    
                }
            });
            
            for(int i=0; i<jXTable1.getColumnCount(); i++){
                jXTable1.getColumnModel().getColumn(i).setCellRenderer(new MyRowRenderer());
            }
        } catch (SQLException ex) {
            Logger.getLogger(FrmReservasi.class.getName()).log(Level.SEVERE, null, ex);
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
        btnLama = new javax.swing.JButton();
        btnBaru = new javax.swing.JButton();
        btnBatal = new javax.swing.JButton();
        btnRegistrasi = new javax.swing.JButton();
        btnTransaksi = new javax.swing.JButton();
        jPanel4 = new javax.swing.JPanel();
        jLabel7 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        cmbDokter = new javax.swing.JComboBox();
        btnRefresh = new javax.swing.JButton();
        jXDatePicker2 = new org.jdesktop.swingx.JXDatePicker();
        jLabel3 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        lblLegendReserved = new javax.swing.JLabel();
        lblLegendRegistered = new javax.swing.JLabel();
        lblLegendCompleted = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        lblLegendCancel = new javax.swing.JLabel();
        btnBatal1 = new javax.swing.JButton();
        jScrollPane2 = new javax.swing.JScrollPane();
        jXTable1 = new javax.swing.JTable();

        setClosable(true);
        setIconifiable(true);
        setMaximizable(true);
        setTitle("Reservasi Pasien");
        setEnabled(false);
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

        jPanel1.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        jPanel1.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        btnLama.setText("Pasien Lama");
        btnLama.setMargin(new java.awt.Insets(2, 2, 2, 2));
        btnLama.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnLamaActionPerformed(evt);
            }
        });
        jPanel1.add(btnLama, new org.netbeans.lib.awtextra.AbsoluteConstraints(150, 10, 135, 25));

        btnBaru.setText("Pasien Baru");
        btnBaru.setMargin(new java.awt.Insets(2, 2, 2, 2));
        btnBaru.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnBaruActionPerformed(evt);
            }
        });
        jPanel1.add(btnBaru, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 10, 135, 25));

        btnBatal.setText("Batalkan");
        btnBatal.setMargin(new java.awt.Insets(2, 2, 2, 2));
        btnBatal.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnBatalActionPerformed(evt);
            }
        });
        jPanel1.add(btnBatal, new org.netbeans.lib.awtextra.AbsoluteConstraints(765, 10, 105, 25));

        btnRegistrasi.setText("Registrasi");
        btnRegistrasi.setEnabled(false);
        btnRegistrasi.setMargin(new java.awt.Insets(2, 2, 2, 2));
        btnRegistrasi.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnRegistrasiActionPerformed(evt);
            }
        });
        jPanel1.add(btnRegistrasi, new org.netbeans.lib.awtextra.AbsoluteConstraints(290, 10, 110, 25));

        btnTransaksi.setText("Transaksi");
        btnTransaksi.setEnabled(false);
        btnTransaksi.setMargin(new java.awt.Insets(2, 2, 2, 2));
        btnTransaksi.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnTransaksiActionPerformed(evt);
            }
        });
        jPanel1.add(btnTransaksi, new org.netbeans.lib.awtextra.AbsoluteConstraints(405, 10, 145, 25));

        jPanel4.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        jPanel4.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel7.setText("Dokter : ");
        jPanel4.add(jLabel7, new org.netbeans.lib.awtextra.AbsoluteConstraints(15, 45, 85, 20));

        jLabel8.setText("Tanggal : ");
        jPanel4.add(jLabel8, new org.netbeans.lib.awtextra.AbsoluteConstraints(12, 12, 85, 20));

        cmbDokter.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        jPanel4.add(cmbDokter, new org.netbeans.lib.awtextra.AbsoluteConstraints(110, 45, 440, -1));

        btnRefresh.setText("Refresh");
        btnRefresh.setMargin(new java.awt.Insets(2, 2, 2, 2));
        btnRefresh.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnRefreshActionPerformed(evt);
            }
        });
        jPanel4.add(btnRefresh, new org.netbeans.lib.awtextra.AbsoluteConstraints(330, 15, 100, 25));

        jXDatePicker2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jXDatePicker2ActionPerformed(evt);
            }
        });
        jPanel4.add(jXDatePicker2, new org.netbeans.lib.awtextra.AbsoluteConstraints(110, 15, 185, -1));

        jLabel3.setText(" Teregistrasi");
        jPanel4.add(jLabel3, new org.netbeans.lib.awtextra.AbsoluteConstraints(585, 25, 100, 20));

        jLabel2.setText(" Reservasi");
        jPanel4.add(jLabel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(585, 5, 100, 20));

        lblLegendReserved.setBackground(new java.awt.Color(255, 255, 204));
        lblLegendReserved.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        lblLegendReserved.setOpaque(true);
        jPanel4.add(lblLegendReserved, new org.netbeans.lib.awtextra.AbsoluteConstraints(565, 5, 20, 20));

        lblLegendRegistered.setBackground(new java.awt.Color(204, 255, 204));
        lblLegendRegistered.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        lblLegendRegistered.setOpaque(true);
        jPanel4.add(lblLegendRegistered, new org.netbeans.lib.awtextra.AbsoluteConstraints(565, 25, 20, 20));

        lblLegendCompleted.setBackground(new java.awt.Color(51, 255, 51));
        lblLegendCompleted.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        lblLegendCompleted.setOpaque(true);
        jPanel4.add(lblLegendCompleted, new org.netbeans.lib.awtextra.AbsoluteConstraints(565, 45, 20, 20));

        jLabel5.setText(" Selesai");
        jPanel4.add(jLabel5, new org.netbeans.lib.awtextra.AbsoluteConstraints(585, 45, 80, 20));

        jLabel6.setText(" Batal");
        jPanel4.add(jLabel6, new org.netbeans.lib.awtextra.AbsoluteConstraints(585, 65, 85, 20));

        lblLegendCancel.setBackground(new java.awt.Color(255, 204, 204));
        lblLegendCancel.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        lblLegendCancel.setOpaque(true);
        jPanel4.add(lblLegendCancel, new org.netbeans.lib.awtextra.AbsoluteConstraints(565, 65, 20, 20));

        btnBatal1.setText("Refresh");
        btnBatal1.setMargin(new java.awt.Insets(2, 2, 2, 2));
        btnBatal1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnBatal1ActionPerformed(evt);
            }
        });
        jPanel4.add(btnBatal1, new org.netbeans.lib.awtextra.AbsoluteConstraints(695, 55, 105, 25));

        jXTable1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "No", "No. Reg", "Norm", "Pasien", "Jenis Kelamin", "Tgl Lahir", "Alamat", "Status", "ID"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jXTable1.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_OFF);
        jScrollPane2.setViewportView(jXTable1);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(10, 10, 10)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel4, javax.swing.GroupLayout.DEFAULT_SIZE, 896, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jScrollPane2)
                        .addGap(1, 1, 1))
                    .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(14, 14, 14))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(10, 10, 10)
                .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, 90, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, 0)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 235, Short.MAX_VALUE)
                .addGap(0, 0, 0)
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(6, 6, 6))
        );

        setBounds(0, 0, 930, 418);
    }// </editor-fold>//GEN-END:initComponents

    private void btnLamaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnLamaActionPerformed
        DlgLookupPasien l=new DlgLookupPasien(JOptionPane.getFrameForComponent(this), true);
        l.setVisibleButtonPilih(true);
        l.setVisible(true);
        Pasien px=l.getSelectedPasien();
        
        if(px!=null){
            String sKodeDokter=listDokter.get(cmbDokter.getSelectedIndex()).getKode();
            if(dao.cekNormExists(sKodeDokter, jXDatePicker2.getDate(), px.getNorm())){
                if(JOptionPane.showConfirmDialog(this, "Reservasi untuk pasien tersebut hari ini sudah ada!\n"
                        + "Anda tetap akan melanjutkan?")==JOptionPane.NO_OPTION){
                    return;
                }
            }
            Reservasi rv=new Reservasi();
            rv.setKodeDokter(sKodeDokter);
            rv.setTanggal(jXDatePicker2.getDate());
            rv.setPasien(px);
            rv.setStatus("1");
            udfFilter(dao.save(rv));
        }
    }//GEN-LAST:event_btnLamaActionPerformed

    private void btnRefreshActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnRefreshActionPerformed
        udfFilter(0);
    }//GEN-LAST:event_btnRefreshActionPerformed

    private void btnBaruActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnBaruActionPerformed
        DlgPasien ps=new DlgPasien(JOptionPane.getFrameForComponent(this), true);
        ps.setSrcForm(this);
        ps.setTglReservasi(jXDatePicker2.getDate());
        ps.setVisible(true);
        Pasien px=ps.getSelectedPasien();
        
        if(px!=null){
            String sKodeDokter=listDokter.get(cmbDokter.getSelectedIndex()).getKode();
            if(dao.cekNormExists(sKodeDokter, jXDatePicker2.getDate(), px.getNorm())){
                if(JOptionPane.showConfirmDialog(this, "Reservasi untuk pasien tersebut hari ini sudah ada!\n"
                        + "Anda tetap akan melanjutkan?")==JOptionPane.NO_OPTION){
                    return;
                }
            }
            Reservasi rv=new Reservasi();
            rv.setKodeDokter(sKodeDokter);
            rv.setTanggal(jXDatePicker2.getDate());
            rv.setPasien(px);
            rv.setStatus("1");
            udfFilter(dao.save(rv));
        }
    }//GEN-LAST:event_btnBaruActionPerformed

    private void formInternalFrameOpened(javax.swing.event.InternalFrameEvent evt) {//GEN-FIRST:event_formInternalFrameOpened
        udfFilter(0);
    }//GEN-LAST:event_formInternalFrameOpened

    private void btnBatalActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnBatalActionPerformed
        batal();
    }//GEN-LAST:event_btnBatalActionPerformed

    private void btnRegistrasiActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnRegistrasiActionPerformed
        udfRegistrasi();
    }//GEN-LAST:event_btnRegistrasiActionPerformed

    private void btnBatal1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnBatal1ActionPerformed
        udfFilter(0);
    }//GEN-LAST:event_btnBatal1ActionPerformed

    private void jXDatePicker2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jXDatePicker2ActionPerformed
        udfFilter(0);
    }//GEN-LAST:event_jXDatePicker2ActionPerformed

    private void btnTransaksiActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnTransaksiActionPerformed
        int iRow=jXTable1.getSelectedRow();
        if(iRow>=0){
            String noReg=jXTable1.getValueAt(iRow, jXTable1.getColumnModel().getColumnIndex("No. Reg"))==null?"": jXTable1.getValueAt(iRow, jXTable1.getColumnModel().getColumnIndex("No. Reg")).toString();
            penjualan.FrmPenjualan trx = new penjualan.FrmPenjualan();
            trx.setTitle("Penjualan");
            trx.setNoReg(noReg);
            trx.setDesktopPane(jDesktopPane1);
            trx.setConn(conn);
            trx.setState(Frame.MAXIMIZED_BOTH);
            trx.setVisible(true);
        }
    }//GEN-LAST:event_btnTransaksiActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnBaru;
    private javax.swing.JButton btnBatal;
    private javax.swing.JButton btnBatal1;
    private javax.swing.JButton btnLama;
    private javax.swing.JButton btnRefresh;
    private javax.swing.JButton btnRegistrasi;
    private javax.swing.JButton btnTransaksi;
    private javax.swing.JComboBox cmbDokter;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JScrollPane jScrollPane2;
    private org.jdesktop.swingx.JXDatePicker jXDatePicker2;
    private javax.swing.JTable jXTable1;
    private javax.swing.JLabel lblLegendCancel;
    private javax.swing.JLabel lblLegendCompleted;
    private javax.swing.JLabel lblLegendRegistered;
    private javax.swing.JLabel lblLegendReserved;
    // End of variables declaration//GEN-END:variables

    public void udfFilter(Integer id) {
        String SQL= "SELECT id, rv.tanggal, coalesce(ps.norm, rv.norm) as norm, coalesce(ps.nama||coalesce(', '||ps.title,''), rv.nama) as pasien, \n" +
                    "case coalesce(ps.jenis_kelamin, rv.jenis_kelamin) when 'L' then 'Laki-laki' else 'Perempuan' end as jenis_kelamin, \n" +
                    "coalesce(ps.tempat_lahir, rv.tempat_lahir) as tempat_lahir, \n" +
                    "coalesce(ps.tgl_lahir, rv.tgl_lahir) as tgl_lahir, \n" +
                    "coalesce(ps.alamat_domisili, rv.alamat_domisili) as alamat_domisili, \n" +
                    "coalesce(ps.telepon, rv.telepon) as telepon, coalesce(ps.hp, rv.hp) as hp, \n" +
                    "coalesce(ps.nama_keluarga, rv.nama_keluarga) as nama_keluarga, \n" +
                    "coalesce(ps.telp_keluarga, rv.telp_keluarga) as telp_keluarga, \n" +
                    "rv.user_ins, rv.time_ins, rv.batal, \n" +
                    "case rv.status when '1' then 'Reservasi'                  when '2' then 'Registrasi'                  when '3' then 'Selesai'                  when '4' then 'Batal' else '' end as status, \n" +
                    "coalesce(reg.no_reg,'') as no_reg \n" +
                    "FROM rm_reservasi rv\n" +
                    "left join rm_reg reg on reg.id_reservasi=rv.id \n" +
                    "left join rm_pasien ps on reg.no_reg=ps.norm \n" +
                    "where rv.tanggal='"+new SimpleDateFormat("yyyy-MM-dd").format(jXDatePicker2.getDate())+"'::date "+
                    "and rv.kode_dokter='"+listDokter.get(cmbDokter.getSelectedIndex()).getKode()+"' \n" +
                    "order by rv.id  ";
        System.out.println(SQL);
        ((DefaultTableModel)jXTable1.getModel()).setNumRows(0);
        int i=1, selectedRow=-1;
        try {
            ResultSet rs=Main.conn.createStatement().executeQuery(SQL);
            while(rs.next()){
                ((DefaultTableModel)jXTable1.getModel()).addRow(new Object[]{
                    i,
                    rs.getString("no_reg"),
                    rs.getString("norm"),
                    rs.getString("pasien"),
                    rs.getString("jenis_kelamin"),
                    rs.getDate("tgl_lahir"),
                    rs.getString("alamat_domisili"),
                    rs.getString("status"),
                    rs.getInt("id")
                });
                if(rs.getInt("id")==id){
                    selectedRow=i-1;
                }
                i++;
            }
            if(i>1){
                fn.autoColWidthTable(jXTable1);
            }
            if(selectedRow >=0){
                jXTable1.setRowSelectionInterval(selectedRow, selectedRow);
            }
            rs.close();
        } catch (SQLException ex) {
            Logger.getLogger(FrmReservasi.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }

    private void batal() {
        int iRow=jXTable1.getSelectedRow();
        if(iRow<0)
            return;
        
        if(JOptionPane.showConfirmDialog(this, "Anda ingin membatalkan reservasi pasien ini?", "Batal Reservasi", JOptionPane.YES_NO_OPTION)==JOptionPane.NO_OPTION){
            return;
        }
        Integer id=fn.udfGetInt(jXTable1.getValueAt(iRow, jXTable1.getColumnModel().getColumnIndex("ID")));
        dao.batal(id);
        udfFilter(0);
    }
    
    Color w1 = new Color(255,255,255);
    Color w2 = new Color(240,240,240);

    private void udfRegistrasi() {
        int iRow=jXTable1.getSelectedRow();
        Integer id=GeneralFunction.udfGetInt(jXTable1.getValueAt(iRow, jXTable1.getColumnModel().getColumnIndex("ID")));
        DlgPasienReg px=new DlgPasienReg(JOptionPane.getFrameForComponent(this), true);
        px.setReservasi(dao.findOne(id));
        px.setSrcForm(this);
        px.setTitle("Registrasi dokter '"+cmbDokter.getSelectedItem().toString()+"'");
        px.setLocationRelativeTo(null);
        px.setVisible(true);
    }
    class MyRowRenderer extends DefaultTableCellRenderer implements TableCellRenderer{
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            setFont(table.getFont());
            Object status=table.getValueAt(row, table.getColumnModel().getColumnIndex("Status"));
            if(status!=null && status.toString().equalsIgnoreCase("Batal"))
                setBackground(lblLegendCancel.getBackground());
            else if(status!=null && status.toString().equalsIgnoreCase("Reservasi"))
                setBackground(lblLegendReserved.getBackground());
            else if(status!=null && status.toString().equalsIgnoreCase("Registrasi"))
                setBackground(lblLegendRegistered.getBackground());
            else if(status!=null && status.toString().equalsIgnoreCase("Selesai"))
                setBackground(lblLegendCompleted.getBackground());
            else
                setBackground(table.getBackground());

            if(value instanceof Date){
                value=new SimpleDateFormat("dd/MM/yyyy").format(value);
            }
            setForeground(new Color(51,51,51));
            //setBackground(w);

            if(isSelected){
                setBackground(new Color(51,255,255));
            }
            setValue(value);
            return this;
        }
    }
    
}
