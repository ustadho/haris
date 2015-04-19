/*
 * DlgSuppBarang.java
 *
 * Created on December 6, 2006, 5:12 PM
 */

package apotek;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FocusTraversalPolicy;
import java.awt.KeyboardFocusManager;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DecimalFormat;
import javax.swing.InputVerifier;
import javax.swing.JComponent;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;
import javax.swing.text.DefaultFormatter;
import javax.swing.text.DefaultFormatterFactory;
import javax.swing.text.NumberFormatter;


/**
 *
 * @author  root
 */
public class DlgSuppBarang extends javax.swing.JDialog {
    private String kode_barang;
    private String nama_barang;
    private String kode_supp;
    private String nama_supp;
    private String uom_alt;
    private float convertion=0;
    private float price=0;
    private float disc=0;
    private float bonus=0;
    private float vat=0;
    
    private boolean bNew;
    private String oldSupp;
    private DlgList lst;
    
    private Connection conn;
    private javax.swing.table.DefaultTableModel tblModel;
    private JTable tblItem;
    
    private int rowPos;
    /** Creates new form DlgSuppBarang */
    public DlgSuppBarang(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
        pack();
        Rectangle parentBounds = parent.getBounds();
        Dimension size = getSize();
        // Center in the parent
        int x = Math.max(0, parentBounds.x + (parentBounds.width - size.width) / 2);
        int y = Math.max(0, parentBounds.y + (parentBounds.height - size.height) / 2);
        setLocation(new Point(x, y));
    }
    
    public void udfSetKodeBarang(String sKode){
        this.kode_barang=sKode;
    }
    public void udfSetNamaBarang(String sNama){
        this.nama_barang=sNama;
    }
    public void udfSetKodeSupp(String sKodeSupp){
        this.kode_supp=sKodeSupp;
    }
    public void udfSetEnabledKodeBarang(boolean bSt){
        txtKdBrg.setEnabled(bSt);
    }
    
    public void udfSetConvertion(float fConv){
        this.convertion=fConv;
    }
    
    public void udfSetPrice(float fPrice){
        this.price=fPrice;
    }
    
    public void udfSetDisc(float fDisc){
        this.disc=fDisc;
    }
    
    public void udfSetBonus(float fBonus){
        this.bonus=fBonus;
    }
    public void udfSetVat(float fVat){
        this.vat=fVat;
    }
    
    public class MyKeyListener extends KeyAdapter {
        public void keyPressed(KeyEvent evt) {
            int keyKode = evt.getKeyCode();
            switch(keyKode){
                case KeyEvent.VK_ENTER : {
                    Component ct = KeyboardFocusManager.getCurrentKeyboardFocusManager().getPermanentFocusOwner();
                    if(!(ct.getClass().getSimpleName().equalsIgnoreCase("JTABLE")))
                        {
                        if (!lst.isVisible()){
                            Component c = findNextFocus();
                            c.requestFocus();
                        }else{
                            lst.requestFocus();
                        }
                    }
                    break;
                }
                case KeyEvent.VK_DOWN: {
                    Component ct = KeyboardFocusManager.getCurrentKeyboardFocusManager().getPermanentFocusOwner();
                    if(!(ct.getClass().getSimpleName().equalsIgnoreCase("JTABLE")))
                        {                        
                            if (!lst.isVisible()){
			    Component c = findNextFocus();
			    c.requestFocus();
                            }else
                                lst.requestFocus();
                            
                            break;
                    }
                }
                case KeyEvent.VK_UP: {
                    Component ct = KeyboardFocusManager.getCurrentKeyboardFocusManager().getPermanentFocusOwner();
                    if(!(ct.getClass().getSimpleName().equalsIgnoreCase("JTABLE")))
                    {    
                        Component c = findPrevFocus();
                        c.requestFocus();
                    }
                    break;
                }
                
                case KeyEvent.VK_ESCAPE: {
                    if(JOptionPane.showConfirmDialog(null,"Anda Yakin Untuk Keluar?","SHS go Open Sources",JOptionPane.YES_NO_OPTION)==JOptionPane.YES_OPTION){
                        dispose();
                    }
                }
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
            if (lst.isVisible())
                lst.setVisible(false);
            
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
            if (lst.isVisible()) lst.setVisible(false);
            
            return prevFocus;
        }
        return null;
    }
    public void setConn(Connection nConn){
        conn=nConn;
    }
    class IntegerVerifier extends InputVerifier {
    
    public boolean verify(JComponent input) {
      JTextField text = (JTextField)input;
      try{
        if (Integer.parseInt(text.getText()) >= 0) {
          return true;
        }
        else{
            text.setText("");  
            return false;
              
            }
          }
          catch(NumberFormatException e){
              text.setText("");  
              return false;
               
          }
        }
    }
    class DoubleVerifier extends InputVerifier {
    
    public boolean verify(JComponent input) {
        JTextField text = (JTextField)input;
          try{
                if (Double.parseDouble(text.getText()) >= 0) {
                return true;
            }
            else{
                text.setText("");
                return false;
            }
          }
          catch(NumberFormatException e){
                text.setText("");
                return false;
          }
        }
    }
    
    public void udfSetOldSupplier(String sSup){
        this.oldSupp=sSup;
    }
    
    public void udfSetTable(JTable nTbl){
        tblItem = nTbl;
        tblModel =(DefaultTableModel) nTbl.getModel();
    }
    
    public void udfSetRowPos(int newRow){
        rowPos = newRow;
    }
    
    private void udfSave(){
        if (udfCekBeforeSave()){
            ItemBean iB=new ItemBean();
            iB.setConn(conn);
            iB.setKode(txtKdBrg.getText().trim());
            iB.setSupplier(txtKdSupp.getText().trim());
            iB.setUom(txtUom.getText().trim());
            iB.setKonversi(Float.parseFloat(txtConvertion.getText().trim().replace(",","")));
            iB.setPrice(Float.parseFloat(txtPrice.getText().replace(",","")));
            iB.setDisc(Float.parseFloat(txtDisc.getText().trim().replace(",","")));
            iB.setBonus(Float.parseFloat(txtBonus.getText().trim().replace(",","")));
            iB.setVat(Float.parseFloat(txtVat.getText().trim().replace(",","")));
            iB.setOldSupplier(oldSupp);
            Object obj;
            try{
                    conn.setAutoCommit(false);

                    if (bNew) {        //Add ------------------ new
                        boolean hsl=iB.addItemSupp();

                        if (!hsl){
                            pesanError("Simpan data gagal!");
                            System.out.println(hsl);   
                        }
                        else
                        {
                            udfSetBNew(false);
                            tblModel.addRow(new Object[]{txtKdSupp.getText().trim(),
                                                lblNmSupp.getText().trim(),
                                                txtUom.getText().trim(),
                                                Float.parseFloat(txtConvertion.getText().trim().replace(",","")),
                                                Float.parseFloat(txtPrice.getText().trim().replace(",","")),
                                                Float.parseFloat(txtDisc.getText().trim().replace(",","")),
                                                Float.parseFloat(txtBonus.getText().trim().replace(",","")),
                                                Float.parseFloat(txtVat.getText().trim().replace(",",""))});

                            tblItem.changeSelection(tblItem.getRowCount()-1,0,false,false);
                            tblItem.requestFocusInWindow();
                            this.dispose();
                        }

                    } else {
                        System.out.println("OK EDIT");
                        iB.setOldSupplier(oldSupp);
                        int i=iB.EditItemSupp();

                        if (i==0){
                            pesanError("Gagal Update!");
                            System.out.println(i);   
                        }else{
                            //Kode Supplier |Nama Supplier | Uom | Convertion | Price | Discount | Bonus | Vat
                             
                            tblModel.setValueAt(txtKdSupp.getText().trim(), rowPos,0);
                            tblModel.setValueAt(lblNmSupp.getText().trim(),rowPos,1);
                            tblModel.setValueAt(txtUom.getText().trim(),rowPos,2);
                            tblModel.setValueAt(Float.parseFloat(txtConvertion.getText().trim().replace(",","")),rowPos,3);
                            tblModel.setValueAt(Float.parseFloat(txtPrice.getText().replace(",","")),rowPos,4);
                            tblModel.setValueAt(Float.parseFloat(txtDisc.getText().replace(",","")),rowPos,5);
                            tblModel.setValueAt(Float.parseFloat(txtBonus.getText().trim().replace(",","")),rowPos,6);
                            tblModel.setValueAt(Float.parseFloat(txtVat.getText().trim().replace(",","")),rowPos,7);
                            tblItem.requestFocusInWindow();

                            this.dispose();
                        }
                    }
                    conn.commit();
                    conn.setAutoCommit(true);
               }catch (SQLException e) {
                    try{
                        conn.rollback();
                        conn.setAutoCommit(true);
                    }catch(SQLException s){}
                    System.out.println(e.getMessage());
                    pesanError(e.getMessage());  
            }
        }
    }
    
    private void udfLoadNBarangSupp(){
        if(!bNew){
            String sQry="select sb.kode_barang, item_name as nama_barang, sb.kode_supplier, nama_supplier, coalesce(uom_alt,'') as uom_alt, " +
                        "sb.konversi as convertion, coalesce(price,0) as price, coalesce(disc,0) as disc,  " +
                        "coalesce(bonus,0) as bonus,  coalesce(sb.vat,0) as var " +
                        "from supplier_barang sb " +
                        "left join phar_supplier s on s.kode_supplier=sb.kode_supplier " +
                        "left join barang i on i.item_code=sb.kode_barang where sb.kode_barang='"+kode_barang+"' " +
                        "and sb.kode_supplier='"+kode_supp+"'";
            
            System.out.println(sQry);
            try{
                Statement st=conn.createStatement();
                ResultSet rs=st.executeQuery(sQry);

                if(rs.next()){
                    txtKdBrg.setText(rs.getString("kode_barang"));
                    lblNamaBarang.setText(rs.getString("nama_barang"));
                    txtKdSupp.setText(rs.getString("kode_supplier"));
                    lblNmSupp.setText(rs.getString("nama_supplier"));
                    txtUom.setText(rs.getString("uom_alt"));
                    txtConvertion.setText(df1.format(rs.getFloat("convertion")));
                    txtPrice.setText(df1.format(rs.getFloat("price")));
                    txtDisc.setText(df1.format(rs.getFloat("disc")));
                    txtBonus.setText(df1.format(rs.getFloat("bonus")));
                    txtVat.setText(df1.format(rs.getFloat("vat")));
                    txtKdBrg.setEnabled(false);
                }

            }catch(SQLException se){System.out.println(se.getMessage());}
        }
}
    
    private void pesanError(String Err){
        JOptionPane.showMessageDialog(this,Err,"Message",JOptionPane.ERROR_MESSAGE);
    }
    
    public void udfSetBNew(boolean bSt){
        this.bNew=bSt;
    }
    
    private boolean udfCekBeforeSave(){
        boolean st=true;
        if(txtKdSupp.getText().trim().length()==0){
            pesanError("Silakan masukkan Supplier terlebih dulu!");
            txtKdSupp.requestFocus();
            return false;
        }
        if(txtUom.getText().trim().length()==0){
            pesanError("Silakan isi uom alt terlebih dulu!");
            txtUom.requestFocus();
            return false;
        }
        
        if(bNew){
            try{
                Statement stmt=conn.createStatement();
                ResultSet rs=stmt.executeQuery("select * from supplier_barang where item_code='"+txtKdBrg.getText().trim()+"' "+
                                            "and supplier_code='"+txtKdSupp.getText().trim()+"'");

                if(rs.next()){
                    pesanError("Relasi antara item '"+lblNamaBarang.getText()+"' dan supplier '"+lblNmSupp.getText()+"' sudah dibuat sebelumnya");
                    txtKdSupp.requestFocus();
                    st=false;
                    return st;
                }   
            }catch(SQLException se){System.out.println(se.getMessage());}
        }
        
        return st;
    }
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel3 = new javax.swing.JLabel();
        jPanel1 = new javax.swing.JPanel();
        jLabel32 = new javax.swing.JLabel();
        txtKdSupp = new javax.swing.JTextField();
        lblNmSupp = new javax.swing.JLabel();
        jLabel35 = new javax.swing.JLabel();
        jLabel37 = new javax.swing.JLabel();
        jLabel38 = new javax.swing.JLabel();
        jLabel39 = new javax.swing.JLabel();
        jButton1 = new javax.swing.JButton();
        jButton2 = new javax.swing.JButton();
        jLabel40 = new javax.swing.JLabel();
        jLabel41 = new javax.swing.JLabel();
        txtUom = new javax.swing.JFormattedTextField();
        jLabel1 = new javax.swing.JLabel();
        lblNamaBarang = new javax.swing.JLabel();
        txtKdBrg = new javax.swing.JTextField();
        txtConvertion = new javax.swing.JFormattedTextField();
        txtPrice = new javax.swing.JFormattedTextField();
        txtBonus = new javax.swing.JFormattedTextField();
        txtDisc = new javax.swing.JFormattedTextField();
        txtVat = new javax.swing.JFormattedTextField();
        jLabel5 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Supplier - Barang");
        setBackground(new java.awt.Color(51, 0, 153));
        setResizable(false);
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowOpened(java.awt.event.WindowEvent evt) {
                formWindowOpened(evt);
            }
        });
        getContentPane().setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel3.setFont(new java.awt.Font("Bookman Old Style", 0, 24));
        jLabel3.setForeground(new java.awt.Color(255, 255, 255));
        jLabel3.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel3.setText("Supplier-Barang");
        getContentPane().add(jLabel3, new org.netbeans.lib.awtextra.AbsoluteConstraints(4, 2, 460, 30));

        jPanel1.setBackground(new java.awt.Color(0, 102, 0));
        jPanel1.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel32.setForeground(new java.awt.Color(255, 255, 51));
        jLabel32.setText("Supplier");
        jPanel1.add(jLabel32, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 40, -1, 24));

        txtKdSupp.setFont(new java.awt.Font("Dialog", 1, 12));
        txtKdSupp.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        txtKdSupp.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                txtKdSuppFocusLost(evt);
            }
        });
        txtKdSupp.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtKdSuppKeyReleased(evt);
            }
        });
        jPanel1.add(txtKdSupp, new org.netbeans.lib.awtextra.AbsoluteConstraints(80, 40, 70, 25));

        lblNmSupp.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        lblNmSupp.setOpaque(true);
        jPanel1.add(lblNmSupp, new org.netbeans.lib.awtextra.AbsoluteConstraints(150, 40, 300, 24));

        jLabel35.setForeground(new java.awt.Color(255, 255, 51));
        jLabel35.setText("Uom Alt");
        jPanel1.add(jLabel35, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 70, -1, 24));

        jLabel37.setForeground(new java.awt.Color(255, 255, 51));
        jLabel37.setText("Convertion");
        jPanel1.add(jLabel37, new org.netbeans.lib.awtextra.AbsoluteConstraints(250, 70, -1, 24));

        jLabel38.setForeground(new java.awt.Color(255, 255, 51));
        jLabel38.setText("Price");
        jPanel1.add(jLabel38, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 100, -1, 24));

        jLabel39.setForeground(new java.awt.Color(255, 255, 51));
        jLabel39.setText("Disc");
        jPanel1.add(jLabel39, new org.netbeans.lib.awtextra.AbsoluteConstraints(290, 100, -1, 24));

        jButton1.setMnemonic('O');
        jButton1.setText("OK");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });
        jButton1.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jButton1KeyPressed(evt);
            }
        });
        jPanel1.add(jButton1, new org.netbeans.lib.awtextra.AbsoluteConstraints(260, 170, 90, 30));

        jButton2.setMnemonic('C');
        jButton2.setText("Cancel");
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });
        jPanel1.add(jButton2, new org.netbeans.lib.awtextra.AbsoluteConstraints(360, 170, 90, 30));

        jLabel40.setForeground(new java.awt.Color(255, 255, 51));
        jLabel40.setText("Bonus");
        jPanel1.add(jLabel40, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 130, -1, 24));

        jLabel41.setForeground(new java.awt.Color(255, 255, 51));
        jLabel41.setText("Vat");
        jPanel1.add(jLabel41, new org.netbeans.lib.awtextra.AbsoluteConstraints(290, 130, -1, 24));

        txtUom.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        txtUom.setFont(new java.awt.Font("Dialog", 1, 12));
        txtUom.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txtUomFocusGained(evt);
            }
        });
        txtUom.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtUomKeyReleased(evt);
            }
        });
        jPanel1.add(txtUom, new org.netbeans.lib.awtextra.AbsoluteConstraints(80, 70, 130, 24));

        jLabel1.setForeground(new java.awt.Color(255, 255, 51));
        jLabel1.setText("Barang");
        jPanel1.add(jLabel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 10, -1, -1));

        lblNamaBarang.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        jPanel1.add(lblNamaBarang, new org.netbeans.lib.awtextra.AbsoluteConstraints(150, 10, 300, 24));

        txtKdBrg.setFont(new java.awt.Font("Dialog", 1, 12));
        txtKdBrg.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        jPanel1.add(txtKdBrg, new org.netbeans.lib.awtextra.AbsoluteConstraints(80, 10, 70, 25));

        txtConvertion.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        txtConvertion.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtConvertion.setFont(new java.awt.Font("Dialog", 1, 12));
        txtConvertion.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txtConvertionFocusGained(evt);
            }
        });
        jPanel1.add(txtConvertion, new org.netbeans.lib.awtextra.AbsoluteConstraints(330, 70, 120, 24));

        txtPrice.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        txtPrice.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtPrice.setFont(new java.awt.Font("Dialog", 1, 12));
        txtPrice.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txtPriceFocusGained(evt);
            }
        });
        jPanel1.add(txtPrice, new org.netbeans.lib.awtextra.AbsoluteConstraints(80, 100, 130, 24));

        txtBonus.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        txtBonus.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtBonus.setFont(new java.awt.Font("Dialog", 1, 12));
        txtBonus.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txtBonusFocusGained(evt);
            }
        });
        jPanel1.add(txtBonus, new org.netbeans.lib.awtextra.AbsoluteConstraints(80, 130, 130, 24));

        txtDisc.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        txtDisc.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtDisc.setFont(new java.awt.Font("Dialog", 1, 12));
        txtDisc.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txtDiscFocusGained(evt);
            }
        });
        jPanel1.add(txtDisc, new org.netbeans.lib.awtextra.AbsoluteConstraints(330, 100, 120, 24));

        txtVat.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        txtVat.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtVat.setFont(new java.awt.Font("Dialog", 1, 12));
        txtVat.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txtVatFocusGained(evt);
            }
        });
        jPanel1.add(txtVat, new org.netbeans.lib.awtextra.AbsoluteConstraints(330, 130, 120, 24));

        getContentPane().add(jPanel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(3, 44, 460, 220));

        jLabel5.setBackground(new java.awt.Color(0, 0, 153));
        jLabel5.setOpaque(true);
        getContentPane().add(jLabel5, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 480, 40));

        jLabel2.setBackground(new java.awt.Color(204, 255, 204));
        jLabel2.setOpaque(true);
        getContentPane().add(jLabel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 40, 470, 220));

        java.awt.Dimension screenSize = java.awt.Toolkit.getDefaultToolkit().getScreenSize();
        setBounds((screenSize.width-476)/2, (screenSize.height-304)/2, 476, 304);
    }// </editor-fold>//GEN-END:initComponents

    private void txtKdSuppFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtKdSuppFocusLost
        if (txtKdSupp.getText().trim().length()==0){
            lblNamaBarang.setText("");
        }
    }//GEN-LAST:event_txtKdSuppFocusLost

    private void jButton1KeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jButton1KeyPressed
        if(evt.getKeyCode()==evt.VK_ENTER){
            udfSave();
        }
    }//GEN-LAST:event_jButton1KeyPressed

    private void txtVatFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtVatFocusGained
        txtVat.setSelectionStart(0);
        txtVat.setSelectionEnd(txtVat.getText().length());
    }//GEN-LAST:event_txtVatFocusGained

    private void txtBonusFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtBonusFocusGained
        txtBonus.setSelectionStart(0);
        txtBonus.setSelectionEnd(txtBonus.getText().length());
    }//GEN-LAST:event_txtBonusFocusGained

    private void txtDiscFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtDiscFocusGained
        txtDisc.setSelectionStart(0);
        txtDisc.setSelectionEnd(txtDisc.getText().length());
    }//GEN-LAST:event_txtDiscFocusGained

    private void txtPriceFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtPriceFocusGained
        txtPrice.setSelectionStart(0);
        txtPrice.setSelectionEnd(txtPrice.getText().length());
    }//GEN-LAST:event_txtPriceFocusGained

    private void txtConvertionFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtConvertionFocusGained
        txtConvertion.setSelectionStart(0);
        txtConvertion.setSelectionEnd(txtConvertion.getText().length());
    }//GEN-LAST:event_txtConvertionFocusGained

    private void txtUomFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtUomFocusGained
        txtUom.setSelectionStart(0);
        txtUom.setSelectionEnd(txtUom.getText().length());
    }//GEN-LAST:event_txtUomFocusGained

    private void txtUomKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtUomKeyReleased
        try {
            String sCari = txtUom.getText();
            switch(evt.getKeyCode()) {
                
                case java.awt.event.KeyEvent.VK_ENTER : {
                    if (lst.isVisible()){
                        Object[] obj = lst.getOResult();
                        if (obj.length > 0) {
                            txtUom.setText(obj[0].toString());
                            //lblNmSupp.setText(obj[1].toString());
                            lst.setVisible(false);
                        }
                    }
                    break;
                }
                case java.awt.event.KeyEvent.VK_DELETE: {
                    lst.setFocusable(true);
                    lst.requestFocus();
                    
                    break;
                }
                
                case java.awt.event.KeyEvent.VK_DOWN: {
                    if (lst.isVisible()){
                        lst.setFocusableWindowState(true);
                        lst.setVisible(true);
                        lst.requestFocus();
                    }
                    break;
                }
                default : {
                    if(!evt.getKeyText(evt.getKeyCode()).equalsIgnoreCase("Up")){
                        String sQry="select uom as Kode, uom_desc as Uom_description from phar_uom where upper(uom||uom_desc) Like '%" + sCari.toUpperCase() +"%' order by 1";
                        System.out.println(sQry);
                        lst.setSQuery(sQry);
                        
                        int lWidth=txtKdSupp.getWidth()+lblNmSupp.getWidth();
                        
                        lst.setBounds(this.getX()+this.jPanel1.getX() + this.txtUom.getX()+5, this.getY()+this.jPanel1.getY()+this.txtUom.getY() + txtUom.getHeight()+20, lWidth,150);
                        lst.setFocusableWindowState(false);
                        lst.setTxtCari(txtUom);
                        lst.setLblDes(new javax.swing.JLabel[]{});
                        lst.setColWidth(0, txtUom.getWidth()-1);
                        lst.setColWidth(1, lblNmSupp.getWidth()-75);
                        if(lst.getIRowCount()>0){
                            lst.setVisible(true);
                        } else{
                            txtUom.setText("");//lblNmSupp.setText("");
                            lst.setVisible(false);
                        }
                    }
                    break;
                }
            }
        } catch (SQLException se) {System.out.println(se.getMessage());
        
        }
    }//GEN-LAST:event_txtUomKeyReleased

    private void txtKdSuppKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtKdSuppKeyReleased
        try {
            String sCari = txtKdSupp.getText();
            switch(evt.getKeyCode()) {
                
                case java.awt.event.KeyEvent.VK_ENTER : {
                    if (lst.isVisible()){
                        Object[] obj = lst.getOResult();
                        if (obj.length > 0) {
                            txtKdSupp.setText(obj[0].toString());
                            lblNmSupp.setText(obj[1].toString());
                            lst.setVisible(false);
                        }
                    }
                    break;
                }
                case java.awt.event.KeyEvent.VK_DELETE: {
                    lst.setFocusable(true);
                    lst.requestFocus();
                    
                    break;
                }
                
                case java.awt.event.KeyEvent.VK_DOWN: {
                    if (lst.isVisible()){
                        lst.setFocusableWindowState(true);
                        lst.setVisible(true);
                        lst.requestFocus();
                    }
                    break;
                }
                default : {
                    if(!evt.getKeyText(evt.getKeyCode()).equalsIgnoreCase("Up")){
                        String sQry="select kode_supplier as Kode, nama_supplier as Nama from phar_supplier where upper(kode_supplier||nama_supplier) Like '%" + sCari.toUpperCase() +"%' order by 1";
                        System.out.println(sQry);
                        lst.setSQuery(sQry);
                        
                        int lWidth=txtKdSupp.getWidth()+lblNmSupp.getWidth();
                        
                        lst.setBounds(this.getX() +this.jPanel1.getX()+ this.txtKdSupp.getX()+5, this.getY()+this.jPanel1.getY()+this.txtKdSupp.getY() + txtKdSupp.getHeight()+20, lWidth,150);
                        lst.setFocusableWindowState(false);
                        lst.setTxtCari(txtKdSupp);
                        lst.setLblDes(new javax.swing.JLabel[]{lblNmSupp});
                        lst.setColWidth(0, txtKdSupp.getWidth()-1);
                        lst.setColWidth(1, lblNmSupp.getWidth()-75);
                        if(lst.getIRowCount()>0){
                            lst.setVisible(true);
                        } else{
                            txtKdSupp.setText("");lblNmSupp.setText("");
                            lst.setVisible(false);
                        }
                    }
                    break;
                }
            }
        } catch (SQLException se) {System.out.println(se.getMessage());
        
        }
    }//GEN-LAST:event_txtKdSuppKeyReleased

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        udfSave();
    }//GEN-LAST:event_jButton1ActionPerformed

    private void formWindowOpened(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowOpened
        DefaultFormatter fmt = new NumberFormatter(new DecimalFormat("#,###,###"));
        
        DefaultFormatterFactory fmtFactory = new DefaultFormatterFactory(fmt, fmt, fmt);
        txtPrice.setFormatterFactory(fmtFactory);
        txtDisc.setFormatterFactory(fmtFactory);
        txtConvertion.setFormatterFactory(fmtFactory);
        txtBonus.setFormatterFactory(fmtFactory);
        txtVat.setFormatterFactory(fmtFactory);
                
        txtPrice.setText("0");
        txtDisc.setText("0");
        txtConvertion.setText("0");
        txtBonus.setText("0");
        txtVat.setText("0");
                
        lst = new DlgList(JOptionPane.getFrameForComponent(this), true);
	lst.setVisible(false);
	lst.setSize(500,150);
	lst.con = conn;
        
        
        if(!bNew) 
            udfLoadNBarangSupp();
        else
        {//Jika memasukkan data baru    -------------------------------------------------
            txtKdBrg.setText(kode_barang);
            lblNamaBarang.setText(nama_barang);
//            txtConvertion.setText(String.valueOf(convertion));
//            txtPrice.setText(String.valueOf(price));
//            txtDisc.setText(String.valueOf(disc));
//            txtBonus.setText(String.valueOf(disc));
            txtKdBrg.setEnabled(false);
        }
        
        for(int i=0;i<jPanel1.getComponentCount();i++){
            Component c = jPanel1.getComponent(i);
            if(c.getClass().getSimpleName().equalsIgnoreCase("JTEXTFIELD")  ||c.getClass().getSimpleName().equalsIgnoreCase("JFormattedTextField")
            || c.getClass().getSimpleName().equalsIgnoreCase("JTextArea")   ||c.getClass().getSimpleName().equalsIgnoreCase("JComboBox")
            || c.getClass().getSimpleName().equalsIgnoreCase("JButton")     ||c.getClass().getSimpleName().equalsIgnoreCase("JCheckBox")) {
                
                c.addKeyListener(new MyKeyListener());
            }
        }
        
    }//GEN-LAST:event_formWindowOpened

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        dispose();
    }//GEN-LAST:event_jButton2ActionPerformed
    
    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new DlgSuppBarang(new javax.swing.JFrame(), true).setVisible(true);
            }
        });
    }
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel32;
    private javax.swing.JLabel jLabel35;
    private javax.swing.JLabel jLabel37;
    private javax.swing.JLabel jLabel38;
    private javax.swing.JLabel jLabel39;
    private javax.swing.JLabel jLabel40;
    private javax.swing.JLabel jLabel41;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JLabel lblNamaBarang;
    private javax.swing.JLabel lblNmSupp;
    private javax.swing.JFormattedTextField txtBonus;
    private javax.swing.JFormattedTextField txtConvertion;
    private javax.swing.JFormattedTextField txtDisc;
    private javax.swing.JTextField txtKdBrg;
    private javax.swing.JTextField txtKdSupp;
    private javax.swing.JFormattedTextField txtPrice;
    private javax.swing.JFormattedTextField txtUom;
    private javax.swing.JFormattedTextField txtVat;
    // End of variables declaration//GEN-END:variables
    
    DecimalFormat df1=new DecimalFormat("#,##0");
    
}
