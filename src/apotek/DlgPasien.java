/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * DlgPasien.java
 *
 * Created on 22 Mar 11, 19:18:03
 */

package apotek;

import penjualan.FrmPenjualan2;
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
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFormattedTextField;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.text.MaskFormatter;
import main.GeneralFunction;

/**
 *
 * @author cak-ust
 */
public class DlgPasien extends javax.swing.JDialog {
    private Connection conn;
    private GeneralFunction fn=new GeneralFunction();
    private Object srcForm;
    MyKeyListener kListener=new MyKeyListener();
    private String sKodeCust="";
    private boolean isSelected=false;

    /** Creates new form DlgPasien */
    public DlgPasien(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
        MaskFormatter fmttgl = null;
        try {
            fmttgl = new MaskFormatter("##/##/####");
        } catch (java.text.ParseException e) {}

        JFormattedTextField jFDate1 = new JFormattedTextField(fmttgl);
        txtTglLahir.setFormatterFactory(jFDate1.getFormatterFactory());
        fn.addKeyListenerInContainer(jPanel1, kListener, txtFocusListener);
        btnSimpan.addKeyListener(kListener);
        btnTutup.addKeyListener(kListener);
    }

    public void setConn(Connection con){
        this.conn=con;
        fn.setConn(conn);
    }

    public void setSrcForm(Object obj){
        this.srcForm=obj;
    }

    private void udfSave(){
        if(txtNama.getText().trim().length()==0){
            JOptionPane.showMessageDialog(this, "Silakan isi nama terlebih dulu!");
            if(!txtNama.isFocusOwner())
                txtNama.requestFocus();
            return;
        }
        if(txtAlamat.getText().trim().length()==0){
            JOptionPane.showMessageDialog(this, "Silakan isi alamat terlebih dulu!");
            if(!txtAlamat.isFocusOwner())
                txtAlamat.requestFocus();
            return;
        }
        try{
            String sExpDate=(txtTglLahir.getText().replace("/", "").trim().length()==0? "null" : 
                    "'"+ new SimpleDateFormat("yyyy-MM-dd").format(new SimpleDateFormat("dd/MM/yyyy").parse(txtTglLahir.getText()))+"'");
            conn.setAutoCommit(false);
            ResultSet rs=conn.createStatement().executeQuery(
                    "select fn_customer_save('"+txtKode.getText()+"', " +
                    "'"+txtNama.getText()+"', "+sExpDate+", '"+txtAlamat.getText()+"', " +
                    "'"+txtTelp.getText()+"', "+fn.udfGetInt(txtTOP.getText())+")");
            conn.setAutoCommit(true);
            if(rs.next()){
                txtKode.setText(rs.getString(1));
                if(srcForm instanceof FrmCustomer)
                    ((FrmCustomer)srcForm).udfFilter(txtKode.getText());
                
                isSelected=true;
                JOptionPane.showMessageDialog(this, "Simpan pasien/customer sukses!");

            }
            if(srcForm instanceof FrmPenjualan2)
                ((FrmPenjualan2)srcForm).setCustomer(txtKode.getText(), txtNama.getText());
            this.dispose();
        } catch (ParseException ex) {
            Logger.getLogger(DlgPasien.class.getName()).log(Level.SEVERE, null, ex);
        }catch(SQLException se){
            try {
                JOptionPane.showMessageDialog(this, se.getMessage());
                conn.rollback();
            } catch (SQLException ex) {
                Logger.getLogger(DlgPasien.class.getName()).log(Level.SEVERE, null, ex);
            }
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

        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        txtKode = new javax.swing.JTextField();
        txtNama = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        txtAlamat = new javax.swing.JTextField();
        jLabel4 = new javax.swing.JLabel();
        txtTelp = new javax.swing.JTextField();
        txtTglLahir = new javax.swing.JFormattedTextField();
        jLabel5 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        txtTOP = new javax.swing.JTextField();
        jLabel8 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        btnSimpan = new javax.swing.JButton();
        btnTutup = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowOpened(java.awt.event.WindowEvent evt) {
                formWindowOpened(evt);
            }
        });
        getContentPane().setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jPanel1.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        jPanel1.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel1.setText("Kode");
        jPanel1.add(jLabel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 10, 50, 20));

        txtKode.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        txtKode.setDisabledTextColor(new java.awt.Color(0, 0, 0));
        txtKode.setEnabled(false);
        jPanel1.add(txtKode, new org.netbeans.lib.awtextra.AbsoluteConstraints(60, 10, 80, 22));

        txtNama.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        jPanel1.add(txtNama, new org.netbeans.lib.awtextra.AbsoluteConstraints(60, 35, 220, 22));

        jLabel2.setText("Nama");
        jPanel1.add(jLabel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 35, 50, 20));

        jLabel3.setText("Alamat");
        jPanel1.add(jLabel3, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 60, 50, 20));

        txtAlamat.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        jPanel1.add(txtAlamat, new org.netbeans.lib.awtextra.AbsoluteConstraints(60, 60, 380, 22));

        jLabel4.setText("Telp");
        jPanel1.add(jLabel4, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 85, 50, 20));

        txtTelp.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        jPanel1.add(txtTelp, new org.netbeans.lib.awtextra.AbsoluteConstraints(60, 85, 150, 22));

        txtTglLahir.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 1, true));
        txtTglLahir.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jPanel1.add(txtTglLahir, new org.netbeans.lib.awtextra.AbsoluteConstraints(360, 35, 80, 22));

        jLabel5.setText("Tgl. Lahir");
        jPanel1.add(jLabel5, new org.netbeans.lib.awtextra.AbsoluteConstraints(290, 35, 70, 20));

        jLabel7.setText(" Hari");
        jPanel1.add(jLabel7, new org.netbeans.lib.awtextra.AbsoluteConstraints(330, 85, 50, 20));

        txtTOP.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        jPanel1.add(txtTOP, new org.netbeans.lib.awtextra.AbsoluteConstraints(290, 85, 40, 22));

        jLabel8.setText("T.O.P");
        jPanel1.add(jLabel8, new org.netbeans.lib.awtextra.AbsoluteConstraints(240, 85, 50, 20));

        getContentPane().add(jPanel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 60, 450, 120));

        jLabel6.setBackground(new java.awt.Color(0, 0, 153));
        jLabel6.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel6.setForeground(new java.awt.Color(204, 255, 255));
        jLabel6.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel6.setText("PASIEN / PELANGGAN");
        jLabel6.setOpaque(true);
        getContentPane().add(jLabel6, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 10, 450, 40));

        btnSimpan.setText("Simpan");
        btnSimpan.setMargin(new java.awt.Insets(2, 2, 2, 2));
        btnSimpan.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSimpanActionPerformed(evt);
            }
        });
        getContentPane().add(btnSimpan, new org.netbeans.lib.awtextra.AbsoluteConstraints(320, 190, 70, 30));

        btnTutup.setText("Tutup");
        btnTutup.setMargin(new java.awt.Insets(2, 2, 2, 2));
        btnTutup.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnTutupActionPerformed(evt);
            }
        });
        getContentPane().add(btnTutup, new org.netbeans.lib.awtextra.AbsoluteConstraints(390, 190, 70, 30));

        java.awt.Dimension screenSize = java.awt.Toolkit.getDefaultToolkit().getScreenSize();
        setBounds((screenSize.width-483)/2, (screenSize.height-268)/2, 483, 268);
    }// </editor-fold>//GEN-END:initComponents

    private void btnSimpanActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSimpanActionPerformed
        udfSave();
    }//GEN-LAST:event_btnSimpanActionPerformed

    private void btnTutupActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnTutupActionPerformed
        this.dispose();
    }//GEN-LAST:event_btnTutupActionPerformed

    private void formWindowOpened(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowOpened
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                txtNama.requestFocus();
            }
        });
        if(sKodeCust.length()>0){
            try{
                String sQry="SELECT kode_pelanggan, coalesce(nama_pelanggan,'') as nama_pasien, "
                    + "coalesce(alamat,'') as alamat, coalesce(telepon,'') as telp,"
                        + "coalesce(to_char(tgl_lahir, 'dd/MM/yyyy'),'') as tgl_lahir, coalesce(top,0) as top "
                    + "FROM pelanggan "
                    + "where kode_pelanggan= '"+sKodeCust+"' "
                    + "";
                
                ResultSet rs=conn.createStatement().executeQuery(sQry);
                if(rs.next()){
                    txtKode.setText(rs.getString("kode_pelanggan"));
                    txtNama.setText(rs.getString("nama_pasien"));
                    txtAlamat.setText(rs.getString("alamat"));
                    txtTelp.setText(rs.getString("telp"));
                    txtTglLahir.setText(rs.getString("tgl_lahir"));
                    txtTOP.setText(rs.getString("top"));
                }
                rs.close();
                
            }catch(SQLException se){
                JOptionPane.showMessageDialog(this, se.getMessage());
            }
        }
    }//GEN-LAST:event_formWindowOpened

    /**
    * @param args the command line arguments
    */
    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                DlgPasien dialog = new DlgPasien(new javax.swing.JFrame(), true);
                dialog.addWindowListener(new java.awt.event.WindowAdapter() {
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
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JTextField txtAlamat;
    private javax.swing.JTextField txtKode;
    private javax.swing.JTextField txtNama;
    private javax.swing.JTextField txtTOP;
    private javax.swing.JTextField txtTelp;
    private javax.swing.JFormattedTextField txtTglLahir;
    // End of variables declaration//GEN-END:variables

    private FocusListener txtFocusListener=new FocusListener() {
        public void focusGained(FocusEvent e) {
            if(e.getSource() instanceof JTextField || e.getSource() instanceof JFormattedTextField){
                ((JTextField)e.getSource()).setBackground(Color.YELLOW);
                if(e.getSource() instanceof JTextField){
                    ((JTextField)e.getSource()).setSelectionStart(0);
                    ((JTextField)e.getSource()).setSelectionEnd(((JTextField)e.getSource()).getText().length());
                }else if(e.getSource() instanceof JFormattedTextField){
                    ((JFormattedTextField)e.getSource()).setSelectionStart(0);
                    ((JFormattedTextField)e.getSource()).setSelectionEnd(((JFormattedTextField)e.getSource()).getText().length());
                }

            }
        }


        public void focusLost(FocusEvent e) {
            if(e.getSource().getClass().getSimpleName().equalsIgnoreCase("JTextField")||
                    e.getSource().getClass().getSimpleName().equalsIgnoreCase("JFormattedTextField")){
                ((JTextField)e.getSource()).setBackground(Color.WHITE);

           }
        }


    } ;

    void setKodeCust(String toString) {
        this.sKodeCust=toString; 
    }

    public boolean isSelected() {
        return isSelected;
    }

    public String getNamaPelanggan() {
        return txtNama.getText();
    }

    public String getKodePelanggan() {
        return txtKode.getText();
    }

    public String getAlamatPelanggan() {
        return txtAlamat.getText();
    }

    public class MyKeyListener extends KeyAdapter {
        @Override
        public void keyReleased(KeyEvent evt){

        }

        @Override
        public void keyTyped(KeyEvent evt){
            if(evt.getSource().equals(txtTOP))
                fn.keyTyped(evt);
        }

        @Override
        public void keyPressed(KeyEvent evt) {
            Component ct = KeyboardFocusManager.getCurrentKeyboardFocusManager().getPermanentFocusOwner();
            int keyKode = evt.getKeyCode();
            switch(keyKode){
                case KeyEvent.VK_F2:{
                    udfSave();
                    break;
                }

                case KeyEvent.VK_ENTER : {
                    if(!(ct instanceof JTable)){
                        if(btnSimpan.isFocusOwner()){
                            udfSave();
                            return;
                        }
                        if (!fn.isListVisible()){
                            Component c = findNextFocus();
                            if (c==null) return;
                            if(c.isEnabled())
                                c.requestFocus();
                            else{
                                c = findNextFocus();
                                if (c!=null) c.requestFocus();;
                            }
                        }else{
                            fn.lstRequestFocus();
                        }
                    }
                    break;
                }
                case KeyEvent.VK_DOWN: {
                    if(ct instanceof JTable){
//                        if(((JTable)ct).getSelectedRow()==0){
////                            Component c = findNextFocus();
////                            if (c==null) return;
////                            if(c.isEnabled())
////                                c.requestFocus();
////                            else{
////                                c = findNextFocus();
////                                if (c!=null) c.requestFocus();;
////                            }
//                        }
                    }else{
                        if (!fn.isListVisible()){
                            Component c = findNextFocus();
                            if (c==null) return;
                            if(c.isEnabled())
                                c.requestFocus();
                            else{
                                c = findNextFocus();
                                if (c!=null) c.requestFocus();;
                            }
                        }else{
                            fn.lstRequestFocus();
                        }
                        break;
                    }
                }

                case KeyEvent.VK_UP: {
                    if(ct instanceof JTable){
                        if(((JTable)ct).getSelectedRow()==0){
                            Component c = findPrevFocus();
                            if (c==null) return;
                            if(c.isEnabled())
                                c.requestFocus();
                            else{
                                c = findPrevFocus();
                                if (c!=null) c.requestFocus();;
                            }
                        }
                    }
                    else{
                        Component c = findPrevFocus();
                        if (c==null) return;
                        if(c.isEnabled())
                            c.requestFocus();
                        else{
//                            c = findPreFocus();
//                            if (c!=null) c.requestFocus();;
                        }
                    }
                    break;
                }

                case KeyEvent.VK_ESCAPE:{

                    break;
                }
            }
        }

//        @Override
//        public void keyReleased(KeyEvent evt){
//            if(evt.getSource().equals(txtDisc)||evt.getSource().equals(txtQty)||evt.getSource().equals(txtUnitPrice))
//                GeneralFunction.keyTyped(evt);
//        }


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