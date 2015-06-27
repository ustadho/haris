/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * DiagnosaRmo.java
 *
 * Created on Apr 28, 2010, 1:44:25 PM
 */

package com.klinik.dokter;

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.FocusTraversalPolicy;
import java.awt.KeyboardFocusManager;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JComponent;
import javax.swing.JFormattedTextField;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import main.GeneralFunction;
import main.MainForm;

/**
 *
 * @author ustadho
 */
public class DiagnosaEdit extends javax.swing.JFrame {
    private Connection conn;
    private String sNoReg="";
    GeneralFunction fn;
    Calendar c=Calendar.getInstance();
    private Timer timer;
    private String sOldDiagnosa="";
    private String sFlag;
    private DiagnosaHistory frmHistory;
    private Object srcForm;

    /** Creates new form DiagnosaRmo */
    public DiagnosaEdit() {
        initComponents();
    }

    public void setConn(Connection con){
        this.conn=con;
        fn=new GeneralFunction(con);
    }

    public void setNoReg(String s){
        this.sNoReg =s;
    }

    public void setIdDiagnosa(String s){
        sOldDiagnosa=s.substring(s.indexOf("-")+1, s.length());
        sFlag=s.substring(0, s.indexOf("-"));

        System.out.println("ID diagnosa --> "+sOldDiagnosa+"\nsFlag -->"+sFlag);
    }

    private void udfSaveDiagnosa() {
        if(txtKodeDiag.getText().trim().length()==0){
            JOptionPane.showMessageDialog(this, "Diagnosa utama belum dimasukkan!");
            if(!txtKodeDiag.isFocusOwner())
                txtKodeDiag.requestFocus();
            return;
        }
        try {
            conn.setAutoCommit(false);
            ResultSet rs = conn.createStatement(ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_UPDATABLE)
                    .executeQuery("select * from rm_reg_diagnosa where no_reg="
                    + "'"+lblNoReg.getText()+"'");
            boolean isNew=false;
            if(!rs.next()){
                rs.moveToInsertRow();
                isNew=true;
            }
            rs.updateString("no_reg", lblNoReg.getText());
            rs.updateString("kode_diagnosa", txtKodeDiag.getText());
            rs.updateString("ket_diagnosa", txtKetDiag.getText());
            rs.updateString("s_ket", txtS.getText());
            rs.updateString("o_ket", txtO.getText());
            rs.updateString("a_ket", txtA.getText());
            rs.updateString("p_ket", txtP.getText());
            rs.updateString("catatan_lain", txtKetLain.getText());
            if(isNew){
                rs.updateString("user_ins", MainForm.sUserName);
                rs.insertRow();
            }else{
                rs.updateString("user_upd", MainForm.sUserName);
                rs.updateTimestamp("time_upd", new java.sql.Timestamp(new Date().getTime()));
                rs.updateRow();
            }
            conn.setAutoCommit(true);
            String sMessage=sOldDiagnosa.length()>0 ?"Update diagnosa sukses!": "Simpan diagnosa sukses!";

            //if(iUpd>0){
            if(frmHistory!=null) frmHistory.udfLoadDiagnosa();
//            if(srcForm!=null && srcForm.getClass().getSimpleName().equalsIgnoreCase("FrmIPDRegistered"))
//                ((FrmRegList)srcForm).udfLoadDiagnosaPasien();
            
            JOptionPane.showMessageDialog(this, sMessage);
            this.dispose();
            //}
        } catch (SQLException ex) {
            try {
                conn.setAutoCommit(true);
                conn.rollback();
                Logger.getLogger(DiagnosaEdit.class.getName()).log(Level.SEVERE, null, ex);
            } catch (SQLException ex1) {
                Logger.getLogger(DiagnosaEdit.class.getName()).log(Level.SEVERE, null, ex1);
            }
        }
    }

    private void udfLoadReg() {
         try {
                String sQry="select r.no_reg, to_char(r.tanggal, 'dd-MM-yyyy') as tanggal, to_char(r.time_ins, 'hh24:MI') as jam_masuk, \n" +
"                        coalesce(ps.nama||coalesce(', '||ps.title,''), '') as pasien, ps.tgl_lahir, age(ps.tgl_lahir, r.tanggal) as usia, \n" +
"                        coalesce(ps.alamat_domisili,'') as  alamat, coalesce(d.kode_diagnosa,'') as kode_diagnosa, coalesce(d.ket_diagnosa,'') as ket_diagnosa, \n" +
"                        coalesce(d.s_Ket,'') as s_ket, coalesce(d.o_ket,'') as o_ket, coalesce(d.a_ket,'') as a_ket, coalesce(d.p_ket,'') as p_ket, \n" +
"                        coalesce(d.catatan_lain,'') as catatan_lain\n" +
"                        from rm_reg r \n" +
"                        inner join rm_pasien ps on ps.norm=r.norm\n" +
"			left join rm_reg_diagnosa d on d.no_reg=r.no_reg\n" +
"                        where r.no_reg='"+sNoReg+"' ";
                System.out.println(sQry);
                
                ResultSet rs = conn.createStatement().executeQuery(sQry);

                if(rs.next()){
                    lblNoReg.setText(rs.getString("no_reg"));
                    lblPasien.setText(rs.getString("pasien"));
                    txtTanggal.setText(rs.getString("tanggal"));
                    txtJam.setText(rs.getString("jam_masuk"));
                    lblTglLahir.setText(rs.getString("tgl_lahir"));
                    lblUsia.setText(rs.getString("usia"));
                    lblAlamat.setText(rs.getString("alamat"));

                    setTitle(rs.getString("pasien")+" ("+rs.getString("no_reg")+")");
                    
                    txtKodeDiag.setText(rs.getString("kode_diagnosa"));
                    txtKetDiag.setText(rs.getString("ket_diagnosa"));
                    txtS.setText(rs.getString("s_ket"));
                    txtO.setText(rs.getString("o_ket"));
                    txtA.setText(rs.getString("a_ket"));
                    txtP.setText(rs.getString("p_ket"));
                    txtKetLain.setText(rs.getString("catatan_lain"));
                }
                rs.close();
                rs=conn.createStatement().executeQuery("select now()");
                rs.next();
                c.setTime(rs.getTimestamp(1));
                timer = new Timer();
                timer.schedule(new DoTick(), 0, 1000);

            } catch (SQLException ex) {
                Logger.getLogger(DiagnosaEdit.class.getName()).log(Level.SEVERE, null, ex);
            }
    }

    void setHistoryForm(DiagnosaHistory aThis) {
        this.frmHistory=aThis;
    }

    void setSrcForm(Object aThis) {
        srcForm=aThis;
    }

    class DoTick extends TimerTask {
        public void run() {
            c.add(Calendar.SECOND, 1);
            lblTglJam.setText(new SimpleDateFormat("dd-MM-yyyy        hh:mm:ss").format(c.getTime()));
        }
    }

    private void udfInitForm(){
        MyKeyListener kListener=new MyKeyListener();
        fn.addKeyListenerInContainer(jPanel1, kListener, txtFocusListener);
        fn.addKeyListenerInContainer(jPanel2, kListener, txtFocusListener);
        fn.addKeyListenerInContainer(jPanel5, kListener, txtFocusListener);
        
        if(conn!=null && sNoReg.length()>0){
            udfLoadReg();
        }
        
    }

    private FocusListener txtFocusListener=new FocusListener() {
        @Override
        public void focusGained(FocusEvent e) {
            if(e.getSource() instanceof JTextField || e.getSource() instanceof JFormattedTextField){
                ((JTextField)e.getSource()).setBackground(Color.YELLOW);
                if( (e.getSource() instanceof JTextField && ((JTextField)e.getSource()).getName()!=null && ((JTextField)e.getSource()).getName().equalsIgnoreCase("textEditor"))){
                    ((JTextField)e.getSource()).setSelectionStart(0);
                    ((JTextField)e.getSource()).setSelectionEnd(((JTextField)e.getSource()).getText().length());

                }
            }
        }

        @Override
        public void focusLost(FocusEvent e) {
            if(e.getSource().getClass().getSimpleName().equalsIgnoreCase("JTextField")||
                    e.getSource().getClass().getSimpleName().equalsIgnoreCase("JFormattedTextField")){
                ((JTextField)e.getSource()).setBackground(Color.WHITE);


           }
        }

    } ;
    

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel5 = new javax.swing.JPanel();
        jLabel8 = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        jLabel11 = new javax.swing.JLabel();
        lblNoReg = new javax.swing.JLabel();
        lblPasien = new javax.swing.JLabel();
        lblAlamat = new javax.swing.JLabel();
        jLabel12 = new javax.swing.JLabel();
        txtTanggal = new javax.swing.JLabel();
        jLabel13 = new javax.swing.JLabel();
        txtJam = new javax.swing.JLabel();
        jLabel14 = new javax.swing.JLabel();
        lblTglLahir = new javax.swing.JLabel();
        jLabel15 = new javax.swing.JLabel();
        lblUsia = new javax.swing.JLabel();
        jPanel1 = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        txtKodeDiag = new javax.swing.JTextField();
        jLabel10 = new javax.swing.JLabel();
        jLabel16 = new javax.swing.JLabel();
        txtKetDiag = new javax.swing.JTextField();
        jLabel17 = new javax.swing.JLabel();
        jLabel18 = new javax.swing.JLabel();
        jLabel19 = new javax.swing.JLabel();
        jLabel20 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        txtKetLain = new javax.swing.JTextArea();
        jSeparator1 = new javax.swing.JSeparator();
        jScrollPane2 = new javax.swing.JScrollPane();
        txtS = new javax.swing.JTextArea();
        jScrollPane3 = new javax.swing.JScrollPane();
        txtA = new javax.swing.JTextArea();
        jScrollPane4 = new javax.swing.JScrollPane();
        txtO = new javax.swing.JTextArea();
        jScrollPane5 = new javax.swing.JScrollPane();
        txtP = new javax.swing.JTextArea();
        jPanel2 = new javax.swing.JPanel();
        btnSimpan = new javax.swing.JButton();
        btnBatal = new javax.swing.JButton();
        lblTglJam = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosed(java.awt.event.WindowEvent evt) {
                formWindowClosed(evt);
            }
            public void windowOpened(java.awt.event.WindowEvent evt) {
                formWindowOpened(evt);
            }
        });
        getContentPane().setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jPanel5.setBorder(javax.swing.BorderFactory.createTitledBorder("Detail Registrasi"));
        jPanel5.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel8.setText("Pasien");
        jPanel5.add(jLabel8, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 55, 80, 20));

        jLabel9.setText("Alamat");
        jPanel5.add(jLabel9, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 105, 70, 20));

        jLabel11.setText("Tanggal :");
        jPanel5.add(jLabel11, new org.netbeans.lib.awtextra.AbsoluteConstraints(200, 30, 90, 20));

        lblNoReg.setBackground(new java.awt.Color(255, 255, 255));
        lblNoReg.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        lblNoReg.setOpaque(true);
        jPanel5.add(lblNoReg, new org.netbeans.lib.awtextra.AbsoluteConstraints(90, 30, 100, 20));

        lblPasien.setBackground(new java.awt.Color(255, 255, 255));
        lblPasien.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        lblPasien.setOpaque(true);
        jPanel5.add(lblPasien, new org.netbeans.lib.awtextra.AbsoluteConstraints(90, 55, 410, 20));

        lblAlamat.setBackground(new java.awt.Color(255, 255, 255));
        lblAlamat.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        lblAlamat.setOpaque(true);
        jPanel5.add(lblAlamat, new org.netbeans.lib.awtextra.AbsoluteConstraints(90, 105, 410, 20));

        jLabel12.setText("No. Reg");
        jPanel5.add(jLabel12, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 30, 105, 20));

        txtTanggal.setBackground(new java.awt.Color(255, 255, 255));
        txtTanggal.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        txtTanggal.setOpaque(true);
        jPanel5.add(txtTanggal, new org.netbeans.lib.awtextra.AbsoluteConstraints(270, 30, 70, 20));

        jLabel13.setText("Jam Daftar :");
        jPanel5.add(jLabel13, new org.netbeans.lib.awtextra.AbsoluteConstraints(350, 30, 95, 20));

        txtJam.setBackground(new java.awt.Color(255, 255, 255));
        txtJam.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        txtJam.setOpaque(true);
        jPanel5.add(txtJam, new org.netbeans.lib.awtextra.AbsoluteConstraints(430, 30, 70, 20));

        jLabel14.setText("Tgl. Lahir");
        jPanel5.add(jLabel14, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 80, 80, 20));

        lblTglLahir.setBackground(new java.awt.Color(255, 255, 255));
        lblTglLahir.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        lblTglLahir.setOpaque(true);
        jPanel5.add(lblTglLahir, new org.netbeans.lib.awtextra.AbsoluteConstraints(90, 80, 100, 20));

        jLabel15.setText("Usia");
        jPanel5.add(jLabel15, new org.netbeans.lib.awtextra.AbsoluteConstraints(210, 80, 40, 20));

        lblUsia.setBackground(new java.awt.Color(255, 255, 255));
        lblUsia.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        lblUsia.setOpaque(true);
        jPanel5.add(lblUsia, new org.netbeans.lib.awtextra.AbsoluteConstraints(250, 80, 180, 20));

        getContentPane().add(jPanel5, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 10, 530, 140));

        jPanel1.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel2.setText("<html>\nKeterangan Lain\n</html>");
        jPanel1.add(jLabel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 370, 85, 40));

        txtKodeDiag.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        txtKodeDiag.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtKodeDiagKeyReleased(evt);
            }
        });
        jPanel1.add(txtKodeDiag, new org.netbeans.lib.awtextra.AbsoluteConstraints(145, 10, 75, 20));

        jLabel10.setText("Kode Diag. Utama :");
        jPanel1.add(jLabel10, new org.netbeans.lib.awtextra.AbsoluteConstraints(5, 10, 150, 20));

        jLabel16.setText("Ket. Diagnosa :");
        jPanel1.add(jLabel16, new org.netbeans.lib.awtextra.AbsoluteConstraints(5, 30, 115, 20));

        txtKetDiag.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        txtKetDiag.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtKetDiagKeyReleased(evt);
            }
        });
        jPanel1.add(txtKetDiag, new org.netbeans.lib.awtextra.AbsoluteConstraints(145, 30, 375, 20));

        jLabel17.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel17.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel17.setText("S : ");
        jPanel1.add(jLabel17, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 80, 100, 20));

        jLabel18.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel18.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel18.setText("P : ");
        jPanel1.add(jLabel18, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 290, 100, 20));

        jLabel19.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel19.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel19.setText("O : ");
        jPanel1.add(jLabel19, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 150, 100, 20));

        jLabel20.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel20.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel20.setText("A : ");
        jPanel1.add(jLabel20, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 220, 100, 20));

        txtKetLain.setColumns(20);
        txtKetLain.setFont(new java.awt.Font("Tahoma", 0, 11)); // NOI18N
        txtKetLain.setRows(5);
        jScrollPane1.setViewportView(txtKetLain);

        jPanel1.add(jScrollPane1, new org.netbeans.lib.awtextra.AbsoluteConstraints(100, 370, 420, 60));
        jPanel1.add(jSeparator1, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 70, 520, 10));

        txtS.setColumns(20);
        txtS.setFont(new java.awt.Font("Tahoma", 0, 11)); // NOI18N
        txtS.setRows(5);
        jScrollPane2.setViewportView(txtS);

        jPanel1.add(jScrollPane2, new org.netbeans.lib.awtextra.AbsoluteConstraints(100, 80, 420, 60));

        txtA.setColumns(20);
        txtA.setFont(new java.awt.Font("Tahoma", 0, 11)); // NOI18N
        txtA.setRows(5);
        jScrollPane3.setViewportView(txtA);

        jPanel1.add(jScrollPane3, new org.netbeans.lib.awtextra.AbsoluteConstraints(100, 220, 420, 60));

        txtO.setColumns(20);
        txtO.setFont(new java.awt.Font("Tahoma", 0, 11)); // NOI18N
        txtO.setRows(5);
        jScrollPane4.setViewportView(txtO);

        jPanel1.add(jScrollPane4, new org.netbeans.lib.awtextra.AbsoluteConstraints(100, 150, 420, 60));

        txtP.setColumns(20);
        txtP.setFont(new java.awt.Font("Tahoma", 0, 11)); // NOI18N
        txtP.setRows(5);
        jScrollPane5.setViewportView(txtP);

        jPanel1.add(jScrollPane5, new org.netbeans.lib.awtextra.AbsoluteConstraints(100, 290, 420, 60));

        getContentPane().add(jPanel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 160, 530, 440));

        jPanel2.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        btnSimpan.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/small/disk.png"))); // NOI18N
        btnSimpan.setText("Simpan");
        btnSimpan.setMargin(new java.awt.Insets(2, 2, 2, 2));
        btnSimpan.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSimpanActionPerformed(evt);
            }
        });
        jPanel2.add(btnSimpan, new org.netbeans.lib.awtextra.AbsoluteConstraints(360, 10, 90, -1));

        btnBatal.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/small/CANCEL.PNG"))); // NOI18N
        btnBatal.setText("Batal");
        btnBatal.setMargin(new java.awt.Insets(2, 2, 2, 2));
        btnBatal.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnBatalActionPerformed(evt);
            }
        });
        jPanel2.add(btnBatal, new org.netbeans.lib.awtextra.AbsoluteConstraints(450, 10, 80, -1));

        lblTglJam.setForeground(new java.awt.Color(0, 0, 255));
        lblTglJam.setText("10/04/2010 20:58");
        lblTglJam.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        jPanel2.add(lblTglJam, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 10, 160, 20));

        getContentPane().add(jPanel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 600, 530, 40));

        setSize(new java.awt.Dimension(620, 681));
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void btnSimpanActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSimpanActionPerformed
        udfSaveDiagnosa();
    }//GEN-LAST:event_btnSimpanActionPerformed

    private void btnBatalActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnBatalActionPerformed
        this.dispose();
}//GEN-LAST:event_btnBatalActionPerformed

    private void formWindowOpened(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowOpened
        udfInitForm();
    }//GEN-LAST:event_formWindowOpened

    private void formWindowClosed(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosed
        fn.setVisibleList(false);
    }//GEN-LAST:event_formWindowClosed

    private void txtKodeDiagKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtKodeDiagKeyReleased
        fn.setClearIfNotFound(false);
        fn.lookup(evt, new JComponent[]{txtKetDiag},
                "select kode_icd, coalesce(ket_icd,'') as ket_icd " +
                "from rm_dtd_icd_final where kode_icd||coalesce(ket_icd,'') ilike '%"+txtKodeDiag.getText()+"%' " +
                "order by kode_icd", txtKodeDiag.getWidth()+txtKetDiag.getWidth()+18, 120);
}//GEN-LAST:event_txtKodeDiagKeyReleased

    private void txtKetDiagKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtKetDiagKeyReleased
        // TODO add your handling code here:
    }//GEN-LAST:event_txtKetDiagKeyReleased

    /**
    * @param args the command line arguments
    */
    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new DiagnosaEdit().setVisible(true);
            }
        });
    }

    public class MyKeyListener extends KeyAdapter {
        @Override
        public void keyPressed(KeyEvent evt) {
            Component ct = KeyboardFocusManager.getCurrentKeyboardFocusManager().getPermanentFocusOwner();
            int keyKode = evt.getKeyCode();
            switch(keyKode){
               case KeyEvent.VK_ENTER : {
                    if(evt.getSource().equals(btnSimpan)){

                    }
                    else if(!(ct instanceof JTable))                    {
                        if (!fn.isListVisible()){
                            Component c = findNextFocus();
                            if (c==null) return;
                            c.requestFocus();
                        }else{
                            fn.lstRequestFocus();
                        }
                    }
                    break;
                }
                case KeyEvent.VK_DOWN: {
                    if(!(ct.getClass().getSimpleName().equalsIgnoreCase("JTABLE")))
                        {
                            if (!fn.isListVisible()){
			    Component c = findNextFocus();
			    c.requestFocus();
                            }else
                                fn.lstRequestFocus();

                            break;
                    }
                }

                case KeyEvent.VK_UP: {
                    if(!(ct.getClass().getSimpleName().equalsIgnoreCase("JTABLE")))
                    {
                        Component c = findPrevFocus();
                        c.requestFocus();
                    }
                    break;
                }
                case KeyEvent.VK_ESCAPE:{
                    if(JOptionPane.showConfirmDialog(null,"Anda Yakin Untuk Keluar?", MainForm.sNamaUsaha, JOptionPane.YES_NO_OPTION)==JOptionPane.YES_OPTION){
                        dispose();
                    }
                    break;
                }

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

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnBatal;
    private javax.swing.JButton btnSimpan;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel18;
    private javax.swing.JLabel jLabel19;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel20;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JScrollPane jScrollPane5;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JLabel lblAlamat;
    private javax.swing.JLabel lblNoReg;
    private javax.swing.JLabel lblPasien;
    private javax.swing.JLabel lblTglJam;
    private javax.swing.JLabel lblTglLahir;
    private javax.swing.JLabel lblUsia;
    private javax.swing.JTextArea txtA;
    private javax.swing.JLabel txtJam;
    private javax.swing.JTextField txtKetDiag;
    private javax.swing.JTextArea txtKetLain;
    private javax.swing.JTextField txtKodeDiag;
    private javax.swing.JTextArea txtO;
    private javax.swing.JTextArea txtP;
    private javax.swing.JTextArea txtS;
    private javax.swing.JLabel txtTanggal;
    // End of variables declaration//GEN-END:variables

}
