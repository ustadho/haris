/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * DlgPembayaran.java
 *
 * Created on 07 Jan 11, 20:38:12
 */

package penjualan;

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.FocusTraversalPolicy;
import java.awt.KeyboardFocusManager;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import javax.swing.JFormattedTextField;
import javax.swing.JTable;
import javax.swing.JTextField;
import main.GeneralFunction;

/**
 *
 * @author cak-ust
 */
public class DlgPembayaran extends javax.swing.JDialog {
    private double dBayar=0, dDiskon=0;
    private GeneralFunction fn=new GeneralFunction();
    private boolean isSelected=false;
    private String sNoTrx;
    private Component srcForm;

    /** Creates new form DlgPembayaran */
    public DlgPembayaran(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
        MyKeyListener kListener=new MyKeyListener();
        fn.addKeyListenerInContainer(jPanel1, kListener, txtFocusListener);

    }

    public boolean isSelected(){
        return isSelected;
    }

    public double getBayar(){
        return dBayar;
    }

    public double getDiskon(){
        return dDiskon;
    }

    private void hitung(){
        double total =fn.udfGetDouble(txtGrandTotal.getText())-fn.udfGetDouble(txtDiskon.getText());
        double kredit= total-fn.udfGetDouble(txtTunai.getText())>0 ? total-fn.udfGetDouble(txtTunai.getText()): 0;
        double kembali=fn.udfGetDouble(txtTunai.getText())-total>0 ? fn.udfGetDouble(txtTunai.getText())-total: 0;
        txtKredit.setText(fn.intFmt.format(kredit));
        txtKembali.setText(fn.intFmt.format(Math.abs(kembali)));

    }

    public void setNoTrx(String text) {
        this.sNoTrx =text;
    }

    public void setTotal(double total ){
        txtGrandTotal.setText(fn.intFmt.format(total));
        txtTunai.setText(fn.intFmt.format(total));
    }

    public String getJenisBayar() {
        return fn.udfGetDouble(txtKredit.getText())>0? "2": "1";
    }

    public void setDiskon(double disc) {
        txtDiskon.setText(fn.intFmt.format(disc));
    }

    public void setSrcForm(Component aThis) {
        this.srcForm=aThis;
    }

    public class MyKeyListener extends KeyAdapter {

        @Override
        public void keyReleased(KeyEvent evt){
            if(evt.getSource().equals(txtTunai))
                txtTunai.setText(fn.intFmt.format(fn.udfGetDouble(txtTunai.getText())));
            
            hitung();
        }

        @Override
        public void keyTyped(KeyEvent evt){
            fn.keyTyped(evt);
        }

        @Override
        public void keyPressed(KeyEvent evt) {
            Component ct = KeyboardFocusManager.getCurrentKeyboardFocusManager().getPermanentFocusOwner();
            int keyKode = evt.getKeyCode();
            switch(keyKode){
                case KeyEvent.VK_F2:{
                    udfSelected();
                    break;
                }
                case KeyEvent.VK_F9:{
//                    if(tblDetail.getRowCount()==0) return;
//                    ((DefaultTableModel)tblHeader.getModel()).addRow(new Object[]{
//                        tblHeader.getRowCount()+1, "T", 0
//                    });
//                    tblHeader.requestFocusInWindow();
//                    tblHeader.requestFocus();
//                    tblHeader.setRowSelectionInterval(tblHeader.getRowCount()-1, tblHeader.getRowCount()-1);
//                    tblHeader.changeSelection(tblHeader.getRowCount()-1, 1, false, false);
                    break;
                }
                case KeyEvent.VK_ENTER : {
                    if(!(ct instanceof JTable)){
                        if (!fn.isListVisible()){
//                            if(evt.getSource() instanceof JTextField && ((JTextField)evt.getSource()).getText()!=null
//                               && ((JTextField)evt.getSource()).getName().equalsIgnoreCase("textEditor")){
//                                if(table.getSelectedColumn()==0){
//                                    //table.setValueAt(((JTextField)evt.getSource()).getText(), table.getSelectedRow(), 0);
//                                    //table.changeSelection(table.getSelectedRow(), 2, false, false);
//                                    //table.setColumnSelectionInterval(2, 2);
//                                }
//                            }

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
                    }else{

                    }
                    break;
                }
                case KeyEvent.VK_DOWN: {
                    if(ct instanceof JTable){
                        if(((JTable)ct).getSelectedRow()==0){
                            Component c = findNextFocus();
                            if (c==null) return;
                            if(c.isEnabled())
                                c.requestFocus();
                            else{
                                c = findNextFocus();
                                if (c!=null) c.requestFocus();;
                            }
                        }
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
                                c = findNextFocus();
                                if (c!=null) c.requestFocus();;
                            }
                        }
                    }else{
                        Component c = findPrevFocus();
                        if (c==null) return;
                        if(c.isEnabled())
                            c.requestFocus();
                        else{
                            c = findNextFocus();
                            if (c!=null) c.requestFocus();;
                        }
                    }
                    break;
                }
                case KeyEvent.VK_ESCAPE:{
                    dispose();
                    break;
                }
            }
        }

//        @Override
//        public void keyReleased(KeyEvent evt){
//            if(evt.getSource().equals(txtDisc)||evt.getSource().equals(txtQty)||evt.getSource().equals(txtUnitPrice))
//                GeneralFunction.keyTyped(evt);
//        }

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

    private void udfSelected() {
        this.isSelected=true;
        dBayar=fn.udfGetDouble(txtTunai.getText());
        dDiskon=fn.udfGetDouble(txtDiskon.getText());
        this.dispose();
    }

    private FocusListener txtFocusListener=new FocusListener() {
        public void focusGained(FocusEvent e) {
            if(e.getSource() instanceof JTextField || e.getSource() instanceof JFormattedTextField){
                ((JTextField)e.getSource()).setBackground(Color.YELLOW);
                ((JTextField)e.getSource()).setSelectionStart(0);
                    ((JTextField)e.getSource()).setSelectionEnd(((JTextField)e.getSource()).getText().length());
            }
        }

        public void focusLost(FocusEvent e) {
            if(e.getSource() instanceof  JTextField  || e.getSource() instanceof  JFormattedTextField){
                ((JTextField)e.getSource()).setBackground(Color.WHITE);
                ((JTextField)e.getSource()).setText(fn.intFmt.format(
                        fn.udfGetDouble(((JTextField)e.getSource()).getText())
                        ));
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

        jPanel1 = new javax.swing.JPanel();
        txtKembali = new javax.swing.JLabel();
        txtKredit = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        jLabel11 = new javax.swing.JLabel();
        jLabel12 = new javax.swing.JLabel();
        txtTunai = new javax.swing.JTextField();
        jSeparator1 = new javax.swing.JSeparator();
        jLabel5 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        txtGrandTotal = new javax.swing.JTextField();
        jLabel8 = new javax.swing.JLabel();
        txtDiskon = new javax.swing.JTextField();
        jLabel1 = new javax.swing.JLabel();
        btnSimpan = new javax.swing.JButton();
        jLabel6 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setUndecorated(true);
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowOpened(java.awt.event.WindowEvent evt) {
                formWindowOpened(evt);
            }
        });
        getContentPane().setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jPanel1.setBackground(new java.awt.Color(0, 0, 204));
        jPanel1.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        jPanel1.setName("jPanel1"); // NOI18N
        jPanel1.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        txtKembali.setBackground(new java.awt.Color(0, 0, 102));
        txtKembali.setFont(new java.awt.Font("Tahoma", 0, 22)); // NOI18N
        txtKembali.setForeground(new java.awt.Color(255, 255, 255));
        txtKembali.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        txtKembali.setText("0");
        txtKembali.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        txtKembali.setName("txtKembali"); // NOI18N
        txtKembali.setOpaque(true);
        jPanel1.add(txtKembali, new org.netbeans.lib.awtextra.AbsoluteConstraints(130, 190, 250, 30));

        txtKredit.setBackground(new java.awt.Color(0, 255, 255));
        txtKredit.setFont(new java.awt.Font("Tahoma", 0, 22)); // NOI18N
        txtKredit.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        txtKredit.setText("0");
        txtKredit.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        txtKredit.setName("txtKredit"); // NOI18N
        txtKredit.setOpaque(true);
        jPanel1.add(txtKredit, new org.netbeans.lib.awtextra.AbsoluteConstraints(130, 155, 250, 30));

        jLabel10.setFont(new java.awt.Font("Tahoma", 0, 22)); // NOI18N
        jLabel10.setForeground(new java.awt.Color(255, 255, 255));
        jLabel10.setText("Kredit");
        jLabel10.setName("jLabel10"); // NOI18N
        jPanel1.add(jLabel10, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 155, 90, 30));

        jLabel11.setFont(new java.awt.Font("Tahoma", 0, 22)); // NOI18N
        jLabel11.setForeground(new java.awt.Color(255, 255, 255));
        jLabel11.setText("Kembali");
        jLabel11.setName("jLabel11"); // NOI18N
        jPanel1.add(jLabel11, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 190, 90, 30));

        jLabel12.setFont(new java.awt.Font("Tahoma", 0, 22)); // NOI18N
        jLabel12.setForeground(new java.awt.Color(255, 255, 255));
        jLabel12.setText("Tunai");
        jLabel12.setName("jLabel12"); // NOI18N
        jPanel1.add(jLabel12, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 120, 90, 30));

        txtTunai.setFont(new java.awt.Font("Tahoma", 0, 22)); // NOI18N
        txtTunai.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtTunai.setText("0");
        txtTunai.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        txtTunai.setName("txtTunai"); // NOI18N
        jPanel1.add(txtTunai, new org.netbeans.lib.awtextra.AbsoluteConstraints(130, 120, 250, 30));

        jSeparator1.setName("jSeparator1"); // NOI18N
        jPanel1.add(jSeparator1, new org.netbeans.lib.awtextra.AbsoluteConstraints(130, 110, 250, 10));

        jLabel5.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jLabel5.setForeground(new java.awt.Color(255, 255, 0));
        jLabel5.setText("<html><b>F2 :</b> Simpan Pembayaran, <b>Esc : </b> Batal pembayaran</html>");
        jLabel5.setName("jLabel5"); // NOI18N
        jPanel1.add(jLabel5, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 280, 370, 20));

        jLabel7.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jLabel7.setForeground(new java.awt.Color(255, 255, 255));
        jLabel7.setText("Grand Total");
        jLabel7.setName("jLabel7"); // NOI18N
        jPanel1.add(jLabel7, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 45, 110, 20));

        txtGrandTotal.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        txtGrandTotal.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtGrandTotal.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        txtGrandTotal.setDisabledTextColor(new java.awt.Color(0, 0, 0));
        txtGrandTotal.setEnabled(false);
        txtGrandTotal.setName("txtGrandTotal"); // NOI18N
        jPanel1.add(txtGrandTotal, new org.netbeans.lib.awtextra.AbsoluteConstraints(130, 45, 140, 20));

        jLabel8.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jLabel8.setForeground(new java.awt.Color(255, 255, 255));
        jLabel8.setText("Diskon (Rp.)");
        jLabel8.setName("jLabel8"); // NOI18N
        jPanel1.add(jLabel8, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 70, 110, 20));

        txtDiskon.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        txtDiskon.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtDiskon.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        txtDiskon.setDisabledTextColor(new java.awt.Color(0, 0, 0));
        txtDiskon.setName("txtDiskon"); // NOI18N
        txtDiskon.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtDiskonKeyReleased(evt);
            }
        });
        jPanel1.add(txtDiskon, new org.netbeans.lib.awtextra.AbsoluteConstraints(130, 70, 140, 20));

        jLabel1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/image/Icon/credit-cards.png"))); // NOI18N
        jLabel1.setName("jLabel1"); // NOI18N
        jPanel1.add(jLabel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(270, 0, 130, 120));

        btnSimpan.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        btnSimpan.setIcon(new javax.swing.ImageIcon(getClass().getResource("/image/Icon/Save.png"))); // NOI18N
        btnSimpan.setText("Simpan");
        btnSimpan.setName("btnSimpan"); // NOI18N
        btnSimpan.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSimpanActionPerformed(evt);
            }
        });
        jPanel1.add(btnSimpan, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 230, 360, 40));

        jLabel6.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jLabel6.setForeground(new java.awt.Color(255, 255, 0));
        jLabel6.setText("Pembayaran");
        jLabel6.setName("jLabel6"); // NOI18N
        jPanel1.add(jLabel6, new org.netbeans.lib.awtextra.AbsoluteConstraints(40, 100, 90, 20));

        getContentPane().add(jPanel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 400, 310));

        setSize(new java.awt.Dimension(398, 321));
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void btnSimpanActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSimpanActionPerformed
        udfSelected();
    }//GEN-LAST:event_btnSimpanActionPerformed

    private void formWindowOpened(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowOpened
        hitung();
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                txtTunai.requestFocus();
                txtTunai.setSelectionStart(0);
                txtTunai.setSelectionEnd(txtTunai.getText().length());
            }
        });
        
    }//GEN-LAST:event_formWindowOpened

    private void txtDiskonKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtDiskonKeyReleased
        if(srcForm !=null && srcForm instanceof FrmPenjualan){
            ((FrmPenjualan)srcForm).setDiskon(fn.udfGetDouble(txtDiskon.getText()));
        }
    }//GEN-LAST:event_txtDiskonKeyReleased

    /**
    * @param args the command line arguments
    */
    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                DlgPembayaran dialog = new DlgPembayaran(new javax.swing.JFrame(), true);
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
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JTextField txtDiskon;
    private javax.swing.JTextField txtGrandTotal;
    private javax.swing.JLabel txtKembali;
    private javax.swing.JLabel txtKredit;
    private javax.swing.JTextField txtTunai;
    // End of variables declaration//GEN-END:variables

}
