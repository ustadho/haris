/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * FrmPasswordChange.java
 *
 * Created on Aug 11, 2010, 11:19:51 AM
 */

package main;

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FocusTraversalPolicy;
import java.awt.KeyboardFocusManager;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFormattedTextField;
import javax.swing.JOptionPane;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

/**
 *
 * @author cak-ust
 */
public class FrmPasswordChange extends javax.swing.JDialog {
    private Connection conn;
    private MainForm mainMenu;

    /** Creates new form FrmPasswordChange */
    public FrmPasswordChange(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
        MyKeyListener kListener=new MyKeyListener();
        txtUsername.addKeyListener(kListener); txtUsername.addFocusListener(txtFocusListener);
        txtOldPassword.addKeyListener(kListener); txtOldPassword.addFocusListener(txtFocusListener);
        txtPassNew.addKeyListener(kListener); txtPassNew.addFocusListener(txtFocusListener);
        txtPassKonfirm.addKeyListener(kListener); txtPassKonfirm.addFocusListener(txtFocusListener);

    }

    public void setMainMenu(MainForm menu){
        this.mainMenu=menu;
    }
    
    public void setUsername(String s){
        txtUsername.setText(s);
        txtUsername.setEnabled(s.length()==0);

    }

    public void setConn(Connection conn){
        this.conn=conn;
    }

    private String getPwdChar(char sText[]){
        String sPwd="";
        for(int i1=0; i1<sText.length; i1++){
            sPwd+=sText[i1];
            sText[i1]='0';
        }
        return sPwd;
    }
    private boolean udfCekBeforeSave(){
        boolean b=true;
        String oldPwd ="", newPwd="", konfrmPwd="";
        oldPwd=getPwdChar(txtOldPassword.getPassword());
        newPwd=getPwdChar(txtPassNew.getPassword());
        konfrmPwd=getPwdChar(txtPassKonfirm.getPassword());

        try{
            String s="select pwd=md5('"+oldPwd+"'), user_id " +
                    "from m_user where username='"+txtUsername.getText()+"'";
            ResultSet rs=conn.createStatement().executeQuery(s);
            if(rs.next()){
                if(rs.getBoolean(1)==false){
                    String sMsg=txtUsername.isEnabled()? "Password tidak sesuai": "Password lama tidak sesuai" ;
                    JOptionPane.showMessageDialog(this, sMsg);
                    rs.close();
                    txtOldPassword.requestFocus();
                    return false;
                }else if(jPanel1.isVisible()){
                    if(!newPwd.equals(konfrmPwd)){
                        JOptionPane.showMessageDialog(this, "Password baru dan konfirmasi password tidak sama!");
                        txtPassKonfirm.requestFocus();
                        return false;
                    }
                }
            }else{
                rs.close();
                JOptionPane.showMessageDialog(this, "Username tidak ada di database!");
                txtOldPassword.requestFocus();
                return false;
            }
            
        }catch(SQLException se){
            JOptionPane.showMessageDialog(this, se.getMessage());
        }

        return b;
    }

    private FocusListener txtFocusListener=new FocusListener() {
        public void focusGained(FocusEvent e) {
            if(e.getSource() instanceof JTextField || e.getSource() instanceof JFormattedTextField 
                    ||e.getSource() instanceof JPasswordField){
                ((JTextField)e.getSource()).setBackground(Color.YELLOW);
                    if(e.getSource() instanceof JTextField){
                        ((JTextField)e.getSource()).setSelectionStart(0);
                        ((JTextField)e.getSource()).setSelectionEnd(((JTextField)e.getSource()).getText().length());
                    }
            }
        }


        public void focusLost(FocusEvent e) {
            if(e.getSource() instanceof JTextField || e.getSource() instanceof JFormattedTextField ||
                    e.getSource() instanceof JPasswordField){
                ((JTextField)e.getSource()).setBackground(Color.WHITE);

           }
        }
    } ;

    private void udfOK(){
        if(!udfCekBeforeSave()) return;

        if(jPanel1.isVisible()){ //Ubah password baru
            try{
                conn.setAutoCommit(false);
                String s="Update m_user set " +
                        "pwd=md5('"+getPwdChar(txtPassKonfirm.getPassword())+"') " +
                        "where username='"+txtUsername.getText()+"';";

                System.out.println(s);

                int i=conn.createStatement().executeUpdate(s);
                conn.setAutoCommit(true);

                if(i>0){
                    JOptionPane.showMessageDialog(this, "Update password sukses!");
                    mainMenu.sUserName=txtUsername.getText();
                    if(this.mainMenu !=null)
                        mainMenu.udfSetUserMenu();
                    this.dispose();
                }
            }catch(SQLException se){
                try {
                    conn.rollback();
                    conn.setAutoCommit(true);
                    JOptionPane.showMessageDialog(this, se.getMessage());
                } catch (SQLException ex) {
                    Logger.getLogger(FrmPasswordChange.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }else{
            try{
                ResultSet rs=conn.createStatement().executeQuery("select user_id from m_user where username='"+txtUsername.getText()+"'");
                if(rs.next())
                    mainMenu.setUserID(rs.getString(1));

                rs.close();
            }catch(SQLException se){}
            mainMenu.sUserName=txtUsername.getText();
            mainMenu.udfSetUserMenu();
            this.dispose();
        }
    }

    
    public class MyKeyListener extends KeyAdapter {

        @Override
        public void keyPressed(KeyEvent evt) {
            int keyKode = evt.getKeyCode();
            switch (keyKode) {
                case KeyEvent.VK_ENTER: {
                    Component ct = KeyboardFocusManager.getCurrentKeyboardFocusManager().getPermanentFocusOwner();
                    if (!(ct.getClass().getSimpleName().equalsIgnoreCase("JTABLE"))) {
                        Component c = findNextFocus();
                        c.requestFocus();

                    }
                }
                case KeyEvent.VK_DOWN: {
                    Component ct = KeyboardFocusManager.getCurrentKeyboardFocusManager().getPermanentFocusOwner();
                    if (!(ct.getClass().getSimpleName().equalsIgnoreCase("JTABLE"))) {
                        Component c = findNextFocus();
                        c.requestFocus();

                    }
                    break;
                }
                case KeyEvent.VK_UP: {
                    Component ct = KeyboardFocusManager.getCurrentKeyboardFocusManager().getPermanentFocusOwner();
                    if (!(ct.getClass().getSimpleName().equalsIgnoreCase("JTABLE"))) {
                        Component c = findPrevFocus();
                        c.requestFocus();
                    }
                    break;
                }

                //lempar aja ke udfCancel
                case KeyEvent.VK_ESCAPE: {
                        dispose();
                        break;

                }
            }
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

    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel17 = new javax.swing.JLabel();
        txtUsername = new javax.swing.JTextField();
        jLabel18 = new javax.swing.JLabel();
        jPanel1 = new javax.swing.JPanel();
        jLabel19 = new javax.swing.JLabel();
        jLabel20 = new javax.swing.JLabel();
        txtPassNew = new javax.swing.JPasswordField();
        txtPassKonfirm = new javax.swing.JPasswordField();
        btnUpdate = new javax.swing.JButton();
        btnClose = new javax.swing.JButton();
        txtOldPassword = new javax.swing.JPasswordField();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowOpened(java.awt.event.WindowEvent evt) {
                formWindowOpened(evt);
            }
        });
        getContentPane().setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel17.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jLabel17.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel17.setText("Username");
        getContentPane().add(jLabel17, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 30, 90, 20));

        txtUsername.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
        txtUsername.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 1, true));
        txtUsername.setDisabledTextColor(new java.awt.Color(0, 0, 0));
        getContentPane().add(txtUsername, new org.netbeans.lib.awtextra.AbsoluteConstraints(110, 30, 190, 20));

        jLabel18.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jLabel18.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel18.setText("Password");
        getContentPane().add(jLabel18, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 55, 90, 20));

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Password Baru", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 0, 11), new java.awt.Color(0, 0, 204))); // NOI18N
        jPanel1.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel19.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jLabel19.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel19.setText("Password");
        jPanel1.add(jLabel19, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 20, 90, 20));

        jLabel20.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jLabel20.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel20.setText("Konfirmasi");
        jPanel1.add(jLabel20, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 45, 90, 20));

        txtPassNew.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        jPanel1.add(txtPassNew, new org.netbeans.lib.awtextra.AbsoluteConstraints(100, 20, 190, 20));

        txtPassKonfirm.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        jPanel1.add(txtPassKonfirm, new org.netbeans.lib.awtextra.AbsoluteConstraints(100, 45, 190, 20));

        getContentPane().add(jPanel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 80, 310, 80));

        btnUpdate.setText("Update");
        btnUpdate.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnUpdateActionPerformed(evt);
            }
        });
        getContentPane().add(btnUpdate, new org.netbeans.lib.awtextra.AbsoluteConstraints(170, 165, 80, -1));

        btnClose.setText("Close");
        btnClose.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCloseActionPerformed(evt);
            }
        });
        getContentPane().add(btnClose, new org.netbeans.lib.awtextra.AbsoluteConstraints(250, 165, 70, -1));

        txtOldPassword.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        getContentPane().add(txtOldPassword, new org.netbeans.lib.awtextra.AbsoluteConstraints(110, 55, 190, 20));

        java.awt.Dimension screenSize = java.awt.Toolkit.getDefaultToolkit().getScreenSize();
        setBounds((screenSize.width-348)/2, (screenSize.height-232)/2, 348, 232);
    }// </editor-fold>//GEN-END:initComponents

    private void btnUpdateActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnUpdateActionPerformed
        udfOK();
    }//GEN-LAST:event_btnUpdateActionPerformed

    private void btnCloseActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCloseActionPerformed
        this.dispose();
    }//GEN-LAST:event_btnCloseActionPerformed

    private void formWindowOpened(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowOpened
        if(txtUsername.isEnabled()){
            jPanel1.setVisible(false);
            btnClose.setBounds(btnClose.getBounds().x, jPanel1.getY(), btnClose.getWidth(), btnClose.getHeight());
            btnUpdate.setBounds(btnUpdate.getBounds().x, jPanel1.getY(), btnUpdate.getWidth(), btnUpdate.getHeight());
            this.setPreferredSize(new Dimension(this.getWidth(), 150));
            btnUpdate.setText("Login");
        }
    }//GEN-LAST:event_formWindowOpened

    /**
    * @param args the command line arguments
    */
    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                FrmPasswordChange dialog = new FrmPasswordChange(new javax.swing.JFrame(), true);
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
    private javax.swing.JButton btnClose;
    private javax.swing.JButton btnUpdate;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel18;
    private javax.swing.JLabel jLabel19;
    private javax.swing.JLabel jLabel20;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPasswordField txtOldPassword;
    private javax.swing.JPasswordField txtPassKonfirm;
    private javax.swing.JPasswordField txtPassNew;
    private javax.swing.JTextField txtUsername;
    // End of variables declaration//GEN-END:variables

}
