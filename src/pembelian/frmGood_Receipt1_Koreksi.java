/*
 * FrmPharPenjualan.java
 *
 * Created on December 13, 2006, 4:29 PM
 */

package pembelian;

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FocusTraversalPolicy;
import java.awt.Font;
import java.awt.KeyboardFocusManager;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.print.PrinterJob;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.print.DocFlavor;
import javax.print.PrintService;
import javax.print.PrintServiceLookup;
import javax.print.attribute.HashPrintRequestAttributeSet;
import javax.print.attribute.HashPrintServiceAttributeSet;
import javax.print.attribute.PrintRequestAttributeSet;
import javax.print.attribute.PrintServiceAttributeSet;
import javax.swing.JFormattedTextField;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.text.MaskFormatter;
import main.GeneralFunction;
import main.SysConfig;

/**
 *
 * @author  root
 */
public class frmGood_Receipt1_Koreksi extends javax.swing.JInternalFrame {
    SysConfig sc=new SysConfig();
    Connection conn;
    Statement st;
    ResultSet rs;
    String sCariSql;
    private String sekarang="";
    //private ListRsbm lst;
    private String sUserID="";
    private String sUserName="";
    private final static long serialVersionUID = 42;
    private DefaultTableModel modelBarang;
    private static String sClose="close";
    public boolean bAsc=false;
    //final DecimalFormat formatter = new DecimalFormat("###,##0.00");
    private NumberFormat formatter = new DecimalFormat("#,###,###");
    private NumberFormat formatter1 = new DecimalFormat("#,###,###.##");
    private String sQry,shift;
    private Boolean bNew ;
    private Boolean bEdit,editItem=false,okSave=false;
    private SimpleDateFormat fdateformat;
    private String dateNow,dateFormatddmmyy;
    private JFormattedTextField jFDate1;
    private Integer rowSelected;
    private FrmLookupBarang f1=new FrmLookupBarang();  
    private Double total,totalDiscount,totalVat,netto,batasPO=50000000.0;
    private JTextField tx=new JTextField();
    private MyKeyListener kListener=new MyKeyListener();
    private String sTglJthTempo;
    private ResultSet rsHead, rsDet;
    private GeneralFunction fn=new GeneralFunction();
    
    /** Creates new form FrmPharPenjualan */
    public frmGood_Receipt1_Koreksi(Connection newConn) {
        initComponents();
        conn=newConn;
        fn.setConn(conn);
        f1.SetConn(conn);
        
        for(int i=0;i<jPanel1.getComponentCount();i++){
            Component c = jPanel1.getComponent(i);
            if(c.getClass().getSimpleName().equalsIgnoreCase("JTEXTFIELD")    || c.getClass().getSimpleName().equalsIgnoreCase("JFormattedTextField")
            || c.getClass().getSimpleName().equalsIgnoreCase("JTextArea") || c.getClass().getSimpleName().equalsIgnoreCase("JComboBox")
            || c.getClass().getSimpleName().equalsIgnoreCase("JButton") || c.getClass().getSimpleName().equalsIgnoreCase("JCheckBox")  ) {
                c.addKeyListener(kListener);
            }
        }
        for(int i=0;i<jPanel2.getComponentCount();i++){
            Component c = jPanel2.getComponent(i);
            if(c.getClass().getSimpleName().equalsIgnoreCase("JTEXTFIELD")    || c.getClass().getSimpleName().equalsIgnoreCase("JFormattedTextField")
            || c.getClass().getSimpleName().equalsIgnoreCase("JTextArea") || c.getClass().getSimpleName().equalsIgnoreCase("JComboBox")
            || c.getClass().getSimpleName().equalsIgnoreCase("JButton") || c.getClass().getSimpleName().equalsIgnoreCase("JCheckBox")  ) {
                c.addKeyListener(kListener);
            }
        }
        
        for(int i=0;i<jPanel3.getComponentCount();i++){
            Component c = jPanel3.getComponent(i);
            if(c.getClass().getSimpleName().equalsIgnoreCase("JTEXTFIELD")    || c.getClass().getSimpleName().equalsIgnoreCase("JFormattedTextField")
            || c.getClass().getSimpleName().equalsIgnoreCase("JTextArea") || c.getClass().getSimpleName().equalsIgnoreCase("JComboBox")
            || c.getClass().getSimpleName().equalsIgnoreCase("JButton") || c.getClass().getSimpleName().equalsIgnoreCase("JCheckBox")  ) {
                c.addKeyListener(kListener);
            }
        }
        
        jScrollPane1.addKeyListener(kListener);
        tblGR.addKeyListener(kListener);
        this.addKeyListener(kListener);
        
        tblGR.getTableHeader().setReorderingAllowed(false);
    }

    private void udfAddItem() {
        udfAddItem();
        
    }

    private void udfEditItem() {
        if(tblGR.getSelectedRow()>=0){
            //DlgEditItemTransGR f1=new DlgEditItemTransGR(JOptionPane.getFrameForComponent(this), false);
            DlgEditItemTransGR f1=new DlgEditItemTransGR();
            f1.setCon(conn);
            f1.setSrcTable(tblGR);
            f1.setSrcModel(modelBarang);
            f1.setIsNew(false);
            f1.setKodeItem(tblGR.getValueAt(tblGR.getSelectedRow(), 1).toString());
            f1.udfLoadBarang();
            f1.setQtyBesar(fn.udfGetFloat(modelBarang.getValueAt(tblGR.getSelectedRow(), 11)));
            f1.setQtyKecil(fn.udfGetFloat(modelBarang.getValueAt(tblGR.getSelectedRow(), 12)));
            f1.setDiskon(fn.udfGetFloat(modelBarang.getValueAt(tblGR.getSelectedRow(), 5)));
            f1.setPPn(fn.udfGetFloat(modelBarang.getValueAt(tblGR.getSelectedRow(), 6)));
            f1.setHarga(fn.udfGetFloat(modelBarang.getValueAt(tblGR.getSelectedRow(), 13)));
            f1.setExpDate(modelBarang.getValueAt(tblGR.getSelectedRow(), 7).toString());
            f1.setNoBatch(modelBarang.getValueAt(tblGR.getSelectedRow(), 8).toString());
            f1.setVisible(true);
        }
    }

    private void udfSetFocusText(JFormattedTextField txt) {
        txt.setSelectionStart(0);
        txt.setSelectionEnd(txt.getText().length());
    }

//    private void udfLoadItemFromBarcode(){
//        String sQry="select item_code, coalesce(item_name,'') as item_name, coalesce(satuan_kecil,'') as satuan_kecil" +
//                ", coalesce(base_price,0) as base_price " +
//                "from barang where barcode='"+txtBarcode.getText()+"'";
//        Statement st;
//        try {
//            st = conn.createStatement();
//            ResultSet rs=st.executeQuery(sQry);
//        
//           if (rs.next()){
//                txtKodeBarang.setText(rs.getString("item_Code"));
//                lblBarang.setText(rs.getString("item_name"));
//                lblUOM.setText(rs.getString("satuan"));
//                ftPrice.setText(formatter.format(rs.getFloat("base_price")));
//                txtDiscPerItem.setText("0");
//                txtPPNperItem.setText("0");
//                ftQty.setText("1");
//                
//           }else{
//                JOptionPane.showMessageDialog(this, "Barang tidak ditemukan!", "Joss Prima Open Source", JOptionPane.OK_OPTION);
//                txtKodeBarang.setText(rs.getString("item_Code"));
//                lblBarang.setText(rs.getString("item_name"));
//                lblUOM.setText(rs.getString("satuan"));
//                ftPrice.setText("0");
//                txtDiscPerItem.setText("0");
//                txtPPNperItem.setText("0");
//                ftQty.setText("0");
//           }
//        } catch (SQLException ex) {
//            JOptionPane.showMessageDialog(this, ex.getMessage(), "Joss Prima Open Source", JOptionPane.OK_OPTION);
//        }
//       
//    }
    private void udfSetFocusText(JTextField txt) {
        txt.setSelectionStart(0);
        txt.setSelectionEnd(txt.getText().length());
    }
    
    private void udfLookupPO(){
        if(lblSupplier.getText().trim().equalsIgnoreCase("") || txtSupplier.getText().trim().equalsIgnoreCase("")){
            JOptionPane.showMessageDialog(null, "Silakan pilih supplier terlebih dulu!");
            txtSupplier.setText(""); lblSupplier.setText("");
            txtSupplier.requestFocus();
            return;
        }
        
        FrmLookupPO fPo=new FrmLookupPO(JOptionPane.getFrameForComponent(this), true);
        //fPo.setFormAsal(this);
        fPo.setSrcModel((DefaultTableModel)tblGR.getModel());
        fPo.setConn(conn);
        fPo.setKodeSupp(txtSupplier.getText());
        
        fPo.setVisible(true);
        
    }
    
//    private void udfSetTotalLine(){
//        float totalLine=0, qty=0, harga=0, disc=0, ppn=0;
//        qty=fn.udfGetFloat(ftQty.getText().trim());
//        harga=fn.udfGetFloat(ftPrice.getText().trim());
//        disc=fn.udfGetFloat(txtDiscPerItem.getText().trim());
//        ppn=fn.udfGetFloat(txtPPNperItem.getText().trim());
//        
//        if (checkDisc_persen.isSelected()){ //Diskon persen
//            disc=qty*disc*harga/100;
//        }
//        if (checkPPN_persen.isSelected()){ //Diskon persen
//            ppn=qty*ppn*harga/100;
//        }
//        
//        totalLine=(qty*harga)-disc+ppn;
//        txtTotalLine.setText(formatter.format(totalLine));
//        
//    }
    
//    private void udfBlank(){
//        txtSupplier.setText("");
//        lblSupplier.setText("");
//        
//    }
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel3 = new javax.swing.JPanel();
        btnClose = new javax.swing.JButton();
        btnSave = new javax.swing.JButton();
        lblHeader1 = new javax.swing.JLabel();
        lblHeader = new javax.swing.JLabel();
        btnNew = new javax.swing.JButton();
        txtNoPenerimaan = new javax.swing.JTextField();
        jLabel10 = new javax.swing.JLabel();
        jPanel1 = new javax.swing.JPanel();
        jLabel3 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        txtNoDO = new javax.swing.JTextField();
        jLabel6 = new javax.swing.JLabel();
        txtRemark = new javax.swing.JTextField();
        jFDate = new javax.swing.JFormattedTextField();
        jLabel9 = new javax.swing.JLabel();
        txtSupplier = new javax.swing.JTextField();
        lblSupplier = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        txtGudang = new javax.swing.JTextField();
        lblGudang = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        txtPengirim = new javax.swing.JTextField();
        jLabel7 = new javax.swing.JLabel();
        jFJthTempo = new javax.swing.JFormattedTextField();
        jLabel4 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        tblGR = new javax.swing.JTable();
        jPanel2 = new javax.swing.JPanel();
        jLabel22 = new javax.swing.JLabel();
        lblNetto = new javax.swing.JLabel();
        lblTotal = new javax.swing.JLabel();
        jLabel27 = new javax.swing.JLabel();
        lblTotalDiscount = new javax.swing.JLabel();
        jLabel28 = new javax.swing.JLabel();
        lblTotalVat = new javax.swing.JLabel();
        jLabel29 = new javax.swing.JLabel();
        jSeparator2 = new javax.swing.JSeparator();
        btnOk1 = new javax.swing.JButton();
        jLabel11 = new javax.swing.JLabel();
        txtPenerima = new javax.swing.JTextField();
        txtDiscTotal = new javax.swing.JTextField();
        txtPPNTotal = new javax.swing.JTextField();
        jLabel30 = new javax.swing.JLabel();
        jLabel31 = new javax.swing.JLabel();
        btnAdd = new javax.swing.JButton();
        jLabel32 = new javax.swing.JLabel();
        txtBiayaMaterai = new javax.swing.JTextField();
        jLabel33 = new javax.swing.JLabel();
        txtBiayaLain = new javax.swing.JTextField();
        btnEdit = new javax.swing.JButton();
        btnHapus = new javax.swing.JButton();

        setBackground(new java.awt.Color(204, 255, 204));
        setClosable(true);
        setTitle("Koreksi Penerimaan/ Pembelian Barang");
        addInternalFrameListener(new javax.swing.event.InternalFrameListener() {
            public void internalFrameActivated(javax.swing.event.InternalFrameEvent evt) {
            }
            public void internalFrameClosed(javax.swing.event.InternalFrameEvent evt) {
                formInternalFrameClosed(evt);
            }
            public void internalFrameClosing(javax.swing.event.InternalFrameEvent evt) {
            }
            public void internalFrameDeactivated(javax.swing.event.InternalFrameEvent evt) {
            }
            public void internalFrameDeiconified(javax.swing.event.InternalFrameEvent evt) {
            }
            public void internalFrameIconified(javax.swing.event.InternalFrameEvent evt) {
                formInternalFrameIconified(evt);
            }
            public void internalFrameOpened(javax.swing.event.InternalFrameEvent evt) {
                formInternalFrameOpened(evt);
            }
        });
        getContentPane().setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jPanel3.setBackground(new java.awt.Color(102, 153, 255));
        jPanel3.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));
        jPanel3.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        btnClose.setIcon(new javax.swing.ImageIcon(getClass().getResource("/image/Icon/Exit.png"))); // NOI18N
        btnClose.setToolTipText("New     (F12)");
        btnClose.setMaximumSize(new java.awt.Dimension(40, 40));
        btnClose.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCloseActionPerformed(evt);
            }
        });
        jPanel3.add(btnClose, new org.netbeans.lib.awtextra.AbsoluteConstraints(114, 6, 50, 50));

        btnSave.setIcon(new javax.swing.ImageIcon(getClass().getResource("/image/Icon/Save.png"))); // NOI18N
        btnSave.setToolTipText("Save");
        btnSave.setMaximumSize(new java.awt.Dimension(40, 40));
        btnSave.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSaveActionPerformed(evt);
            }
        });
        jPanel3.add(btnSave, new org.netbeans.lib.awtextra.AbsoluteConstraints(63, 7, 50, 50));

        lblHeader1.setFont(new java.awt.Font("Bookman Old Style", 1, 24));
        lblHeader1.setForeground(new java.awt.Color(255, 255, 255));
        lblHeader1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblHeader1.setText("Koreksi Penerimaan/ Pembelian  Barang");
        lblHeader1.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jPanel3.add(lblHeader1, new org.netbeans.lib.awtextra.AbsoluteConstraints(460, 25, 530, 30));

        lblHeader.setFont(new java.awt.Font("Bookman Old Style", 1, 24));
        lblHeader.setForeground(new java.awt.Color(153, 153, 153));
        lblHeader.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblHeader.setText("Koreksi Penerimaan/ Pembelian  Barang");
        lblHeader.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jPanel3.add(lblHeader, new org.netbeans.lib.awtextra.AbsoluteConstraints(462, 23, 530, 30));

        btnNew.setIcon(new javax.swing.ImageIcon(getClass().getResource("/image/Icon/New.png"))); // NOI18N
        btnNew.setToolTipText("Save");
        btnNew.setMaximumSize(new java.awt.Dimension(40, 40));
        btnNew.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnNewActionPerformed(evt);
            }
        });
        jPanel3.add(btnNew, new org.netbeans.lib.awtextra.AbsoluteConstraints(4, 8, 50, 50));

        txtNoPenerimaan.setFont(new java.awt.Font("Dialog", 1, 12)); // NOI18N
        txtNoPenerimaan.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 1, true));
        txtNoPenerimaan.setDisabledTextColor(new java.awt.Color(153, 153, 153));
        txtNoPenerimaan.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                txtNoPenerimaanFocusLost(evt);
            }
        });
        txtNoPenerimaan.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txtNoPenerimaanKeyPressed(evt);
            }
        });
        jPanel3.add(txtNoPenerimaan, new org.netbeans.lib.awtextra.AbsoluteConstraints(224, 34, 190, 24));

        jLabel10.setText("Masukkan No. Penerimaan");
        jPanel3.add(jLabel10, new org.netbeans.lib.awtextra.AbsoluteConstraints(223, 19, 190, -1));

        getContentPane().add(jPanel3, new org.netbeans.lib.awtextra.AbsoluteConstraints(3, 3, 800, 70));

        jPanel1.setBackground(new java.awt.Color(102, 153, 255));
        jPanel1.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));
        jPanel1.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel3.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel3.setText("Tanggal");
        jPanel1.add(jLabel3, new org.netbeans.lib.awtextra.AbsoluteConstraints(440, 10, 60, -1));

        jLabel5.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel5.setText("No Inv./DO/SJ");
        jPanel1.add(jLabel5, new org.netbeans.lib.awtextra.AbsoluteConstraints(23, 37, 90, -1));

        txtNoDO.setFont(new java.awt.Font("Dialog", 1, 12)); // NOI18N
        txtNoDO.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 1, true));
        txtNoDO.setDisabledTextColor(new java.awt.Color(153, 153, 153));
        txtNoDO.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                txtNoDOFocusLost(evt);
            }
        });
        txtNoDO.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtNoDOKeyReleased(evt);
            }
        });
        jPanel1.add(txtNoDO, new org.netbeans.lib.awtextra.AbsoluteConstraints(125, 34, 280, 24));

        jLabel6.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel6.setText("Keterangan");
        jPanel1.add(jLabel6, new org.netbeans.lib.awtextra.AbsoluteConstraints(23, 68, 90, -1));

        txtRemark.setFont(new java.awt.Font("Dialog", 1, 12)); // NOI18N
        txtRemark.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 1, true));
        txtRemark.setDisabledTextColor(new java.awt.Color(153, 153, 153));
        txtRemark.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                txtRemarkFocusLost(evt);
            }
        });
        txtRemark.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtRemarkKeyReleased(evt);
            }
        });
        jPanel1.add(txtRemark, new org.netbeans.lib.awtextra.AbsoluteConstraints(125, 63, 280, 24));

        jFDate.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 1, true));
        jFDate.setFont(new java.awt.Font("Dialog", 1, 12));
        jFDate.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                jFDateFocusLost(evt);
            }
        });
        jPanel1.add(jFDate, new org.netbeans.lib.awtextra.AbsoluteConstraints(510, 10, 80, 24));

        jLabel9.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel9.setText("Supplier");
        jPanel1.add(jLabel9, new org.netbeans.lib.awtextra.AbsoluteConstraints(23, 10, 90, -1));

        txtSupplier.setFont(new java.awt.Font("Dialog", 1, 12)); // NOI18N
        txtSupplier.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 1, true));
        txtSupplier.setDisabledTextColor(new java.awt.Color(153, 153, 153));
        txtSupplier.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                txtSupplierFocusLost(evt);
            }
        });
        txtSupplier.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txtSupplierKeyPressed(evt);
            }
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtSupplierKeyReleased(evt);
            }
        });
        jPanel1.add(txtSupplier, new org.netbeans.lib.awtextra.AbsoluteConstraints(125, 7, 60, 24));

        lblSupplier.setFont(new java.awt.Font("Dialog", 3, 12)); // NOI18N
        lblSupplier.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 1, true));
        jPanel1.add(lblSupplier, new org.netbeans.lib.awtextra.AbsoluteConstraints(184, 7, 220, 24));

        jLabel2.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel2.setText("Gudang");
        jPanel1.add(jLabel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(420, 35, 78, -1));

        txtGudang.setFont(new java.awt.Font("Dialog", 1, 12)); // NOI18N
        txtGudang.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 1, true));
        txtGudang.setDisabledTextColor(new java.awt.Color(153, 153, 153));
        txtGudang.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtGudangKeyReleased(evt);
            }
        });
        jPanel1.add(txtGudang, new org.netbeans.lib.awtextra.AbsoluteConstraints(510, 35, 40, 24));

        lblGudang.setFont(new java.awt.Font("Dialog", 3, 12)); // NOI18N
        lblGudang.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 1, true));
        jPanel1.add(lblGudang, new org.netbeans.lib.awtextra.AbsoluteConstraints(550, 35, 150, 24));

        jLabel8.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel8.setText("Pengirim");
        jPanel1.add(jLabel8, new org.netbeans.lib.awtextra.AbsoluteConstraints(410, 70, 90, -1));

        txtPengirim.setFont(new java.awt.Font("Dialog", 1, 12));
        txtPengirim.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 1, true));
        txtPengirim.setDisabledTextColor(new java.awt.Color(153, 153, 153));
        txtPengirim.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                txtPengirimFocusLost(evt);
            }
        });
        txtPengirim.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtPengirimKeyReleased(evt);
            }
        });
        jPanel1.add(txtPengirim, new org.netbeans.lib.awtextra.AbsoluteConstraints(510, 60, 270, 24));

        jLabel7.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel7.setText("Jt. Tempo");
        jPanel1.add(jLabel7, new org.netbeans.lib.awtextra.AbsoluteConstraints(600, 10, 70, 20));

        jFJthTempo.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 1, true));
        jFJthTempo.setFont(new java.awt.Font("Dialog", 1, 12));
        jFJthTempo.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                jFJthTempoFocusLost(evt);
            }
        });
        jPanel1.add(jFJthTempo, new org.netbeans.lib.awtextra.AbsoluteConstraints(680, 10, 100, 24));

        getContentPane().add(jPanel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(3, 75, 800, 100));

        jLabel4.setBackground(new java.awt.Color(255, 204, 204));
        jLabel4.setFont(new java.awt.Font("Bitstream Vera Sans", 1, 14));
        jLabel4.setForeground(new java.awt.Color(51, 51, 51));
        jLabel4.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel4.setText("DAFTAR PENERIMAAN ITEM");
        jLabel4.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));
        jLabel4.setOpaque(true);
        getContentPane().add(jLabel4, new org.netbeans.lib.awtextra.AbsoluteConstraints(3, 178, 800, -1));

        tblGR.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null}
            },
            new String [] {
                "No", "Kode Barang", "Deskripsi", "Jumlah Satuan", "Harga", "Diskon", "PPn", "Exp. Date", "No. Batch", "Total Line", "No. PO", "JmlBesar", "JmlKecil", "Harga", "KOnversi", "Hrg Ins"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Double.class, java.lang.Double.class, java.lang.Double.class, java.lang.Object.class, java.lang.Object.class, java.lang.Double.class, java.lang.String.class, java.lang.Integer.class, java.lang.Integer.class, java.lang.Object.class, java.lang.Integer.class, java.lang.Double.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false, false, false, false, false, false, true, true, true, true, true
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        tblGR.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_OFF);
        tblGR.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                tblGRFocusGained(evt);
            }
        });
        tblGR.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                tblGRKeyPressed(evt);
            }
        });
        jScrollPane1.setViewportView(tblGR);

        getContentPane().add(jScrollPane1, new org.netbeans.lib.awtextra.AbsoluteConstraints(5, 204, 800, 260));

        jPanel2.setBackground(new java.awt.Color(102, 153, 255));
        jPanel2.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));
        jPanel2.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel22.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel22.setText("Netto");
        jPanel2.add(jLabel22, new org.netbeans.lib.awtextra.AbsoluteConstraints(530, 150, 84, -1));

        lblNetto.setFont(new java.awt.Font("Dialog", 3, 12));
        lblNetto.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lblNetto.setText("0");
        lblNetto.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 1, true));
        jPanel2.add(lblNetto, new org.netbeans.lib.awtextra.AbsoluteConstraints(620, 140, 165, 24));

        lblTotal.setFont(new java.awt.Font("Dialog", 3, 12));
        lblTotal.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lblTotal.setText("0");
        lblTotal.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 1, true));
        jPanel2.add(lblTotal, new org.netbeans.lib.awtextra.AbsoluteConstraints(620, 60, 165, 24));

        jLabel27.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel27.setText("Total");
        jPanel2.add(jLabel27, new org.netbeans.lib.awtextra.AbsoluteConstraints(550, 60, 66, -1));

        lblTotalDiscount.setFont(new java.awt.Font("Dialog", 3, 12));
        lblTotalDiscount.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lblTotalDiscount.setText("0");
        lblTotalDiscount.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 1, true));
        jPanel2.add(lblTotalDiscount, new org.netbeans.lib.awtextra.AbsoluteConstraints(620, 90, 165, 24));

        jLabel28.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel28.setText("Total Discount (-)");
        jPanel2.add(jLabel28, new org.netbeans.lib.awtextra.AbsoluteConstraints(490, 90, 117, -1));

        lblTotalVat.setFont(new java.awt.Font("Dialog", 3, 12));
        lblTotalVat.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lblTotalVat.setText("0");
        lblTotalVat.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 1, true));
        jPanel2.add(lblTotalVat, new org.netbeans.lib.awtextra.AbsoluteConstraints(620, 120, 165, 24));

        jLabel29.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel29.setText("Total PPn (+)");
        jPanel2.add(jLabel29, new org.netbeans.lib.awtextra.AbsoluteConstraints(500, 120, 111, -1));
        jPanel2.add(jSeparator2, new org.netbeans.lib.awtextra.AbsoluteConstraints(23, 39, 969, 10));

        btnOk1.setText("Pilih PO");
        btnOk1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnOk1ActionPerformed(evt);
            }
        });
        btnOk1.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                btnOk1KeyPressed(evt);
            }
        });
        jPanel2.add(btnOk1, new org.netbeans.lib.awtextra.AbsoluteConstraints(680, 10, 110, 24));

        jLabel11.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel11.setText("Penerima");
        jPanel2.add(jLabel11, new org.netbeans.lib.awtextra.AbsoluteConstraints(12, 139, 90, -1));

        txtPenerima.setFont(new java.awt.Font("Dialog", 1, 12));
        txtPenerima.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 1, true));
        txtPenerima.setDisabledTextColor(new java.awt.Color(153, 153, 153));
        txtPenerima.setSelectedTextColor(new java.awt.Color(255, 255, 0));
        txtPenerima.setSelectionColor(new java.awt.Color(255, 255, 0));
        txtPenerima.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txtPenerimaFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                txtPenerimaFocusLost(evt);
            }
        });
        txtPenerima.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtPenerimaKeyReleased(evt);
            }
        });
        jPanel2.add(txtPenerima, new org.netbeans.lib.awtextra.AbsoluteConstraints(110, 140, 170, 24));

        txtDiscTotal.setFont(new java.awt.Font("Dialog", 1, 12));
        txtDiscTotal.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtDiscTotal.setText("0");
        txtDiscTotal.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 1, true));
        txtDiscTotal.setDisabledTextColor(new java.awt.Color(153, 153, 153));
        txtDiscTotal.setSelectedTextColor(new java.awt.Color(255, 255, 0));
        txtDiscTotal.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txtDiscTotalFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                txtDiscTotalFocusLost(evt);
            }
        });
        txtDiscTotal.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtDiscTotalKeyReleased(evt);
            }
        });
        jPanel2.add(txtDiscTotal, new org.netbeans.lib.awtextra.AbsoluteConstraints(410, 90, 110, 24));

        txtPPNTotal.setFont(new java.awt.Font("Dialog", 1, 12));
        txtPPNTotal.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtPPNTotal.setText("0");
        txtPPNTotal.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 1, true));
        txtPPNTotal.setDisabledTextColor(new java.awt.Color(153, 153, 153));
        txtPPNTotal.setSelectedTextColor(new java.awt.Color(255, 255, 0));
        txtPPNTotal.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txtPPNTotalFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                txtPPNTotalFocusLost(evt);
            }
        });
        txtPPNTotal.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtPPNTotalKeyReleased(evt);
            }
        });
        jPanel2.add(txtPPNTotal, new org.netbeans.lib.awtextra.AbsoluteConstraints(410, 120, 110, 24));

        jLabel30.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel30.setText("Diskon");
        jPanel2.add(jLabel30, new org.netbeans.lib.awtextra.AbsoluteConstraints(340, 100, 60, -1));

        jLabel31.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel31.setText("PPn");
        jPanel2.add(jLabel31, new org.netbeans.lib.awtextra.AbsoluteConstraints(340, 130, 60, -1));

        btnAdd.setText("Tambahkan");
        btnAdd.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAddActionPerformed(evt);
            }
        });
        jPanel2.add(btnAdd, new org.netbeans.lib.awtextra.AbsoluteConstraints(8, 9, 111, 27));

        jLabel32.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel32.setText("Biaya Materai");
        jPanel2.add(jLabel32, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 50, 85, 20));

        txtBiayaMaterai.setFont(new java.awt.Font("Dialog", 1, 12));
        txtBiayaMaterai.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtBiayaMaterai.setText("0");
        txtBiayaMaterai.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 1, true));
        txtBiayaMaterai.setDisabledTextColor(new java.awt.Color(153, 153, 153));
        txtBiayaMaterai.setSelectedTextColor(new java.awt.Color(255, 255, 0));
        txtBiayaMaterai.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txtBiayaMateraiFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                txtBiayaMateraiFocusLost(evt);
            }
        });
        txtBiayaMaterai.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtBiayaMateraiKeyReleased(evt);
            }
        });
        jPanel2.add(txtBiayaMaterai, new org.netbeans.lib.awtextra.AbsoluteConstraints(110, 50, 110, 24));

        jLabel33.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel33.setText("Biaya Lain");
        jPanel2.add(jLabel33, new org.netbeans.lib.awtextra.AbsoluteConstraints(230, 54, 60, 20));

        txtBiayaLain.setFont(new java.awt.Font("Dialog", 1, 12));
        txtBiayaLain.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtBiayaLain.setText("0");
        txtBiayaLain.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 1, true));
        txtBiayaLain.setDisabledTextColor(new java.awt.Color(153, 153, 153));
        txtBiayaLain.setSelectedTextColor(new java.awt.Color(255, 255, 0));
        txtBiayaLain.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txtBiayaLainFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                txtBiayaLainFocusLost(evt);
            }
        });
        txtBiayaLain.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtBiayaLainKeyReleased(evt);
            }
        });
        jPanel2.add(txtBiayaLain, new org.netbeans.lib.awtextra.AbsoluteConstraints(290, 50, 110, 24));

        btnEdit.setText("Edit Item");
        btnEdit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnEditActionPerformed(evt);
            }
        });
        jPanel2.add(btnEdit, new org.netbeans.lib.awtextra.AbsoluteConstraints(121, 9, 111, 27));

        btnHapus.setText("Hapus Item");
        btnHapus.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnHapusActionPerformed(evt);
            }
        });
        jPanel2.add(btnHapus, new org.netbeans.lib.awtextra.AbsoluteConstraints(238, 9, 111, 27));

        getContentPane().add(jPanel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(3, 468, 800, 170));

        java.awt.Dimension screenSize = java.awt.Toolkit.getDefaultToolkit().getScreenSize();
        setBounds((screenSize.width-828)/2, (screenSize.height-680)/2, 828, 680);
    }// </editor-fold>//GEN-END:initComponents

    private void txtBiayaLainKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtBiayaLainKeyReleased
// TODO add your handling code here:
    }//GEN-LAST:event_txtBiayaLainKeyReleased

    private void txtBiayaLainFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtBiayaLainFocusLost
// TODO add your handling code here:
        txtBiayaLain.setText(formatter.format(fn.udfGetFloat(txtBiayaLain.getText())));
        udfSetTotal();
    }//GEN-LAST:event_txtBiayaLainFocusLost

    private void txtBiayaLainFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtBiayaLainFocusGained
// TODO add your handling code here:
    }//GEN-LAST:event_txtBiayaLainFocusGained

    private void txtBiayaMateraiKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtBiayaMateraiKeyReleased
// TODO add your handling code here:
    }//GEN-LAST:event_txtBiayaMateraiKeyReleased

    private void txtBiayaMateraiFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtBiayaMateraiFocusLost
        txtBiayaMaterai.setText(formatter.format(fn.udfGetFloat(txtBiayaMaterai.getText())));
        udfSetTotal();
    }//GEN-LAST:event_txtBiayaMateraiFocusLost

    private void txtBiayaMateraiFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtBiayaMateraiFocusGained
// TODO add your handling code here:
    }//GEN-LAST:event_txtBiayaMateraiFocusGained

    private void btnNewActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnNewActionPerformed
        udfClear();
    }//GEN-LAST:event_btnNewActionPerformed

    private void btnAddActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAddActionPerformed
        DlgEditItemTransGR d=new DlgEditItemTransGR();
        d.setCon(conn);
        d.setIsNew(true);
        d.setNoPO("");
        d.setSupplier(txtSupplier.getText());
        d.setTitle("Tambah Item Penjualan");
        d.setSrcModel(modelBarang);
        d.setSrcTable(tblGR);
        d.setVisible(true);
}//GEN-LAST:event_btnAddActionPerformed

    private void txtDiscTotalFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtDiscTotalFocusLost
        txtDiscTotal.setText(formatter.format(fn.udfGetFloat(txtDiscTotal.getText())));
        udfSetTotal();
    }//GEN-LAST:event_txtDiscTotalFocusLost

    private void txtPengirimKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtPengirimKeyReleased
// TODO add your handling code here:
    }//GEN-LAST:event_txtPengirimKeyReleased

    private void txtPengirimFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtPengirimFocusLost
// TODO add your handling code here:
    }//GEN-LAST:event_txtPengirimFocusLost

    private void txtPPNTotalKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtPPNTotalKeyReleased
// TODO add your handling code here:
    }//GEN-LAST:event_txtPPNTotalKeyReleased

    private void txtPPNTotalFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtPPNTotalFocusLost
        txtPPNTotal.setText(formatter.format(fn.udfGetFloat(txtPPNTotal.getText())));
        udfSetTotal();
    }//GEN-LAST:event_txtPPNTotalFocusLost

    private void txtPPNTotalFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtPPNTotalFocusGained
        txtPPNTotal.setSelectionStart(0);
        txtPPNTotal.setSelectionEnd(txtPPNTotal.getText().length());
    }//GEN-LAST:event_txtPPNTotalFocusGained

    private void txtDiscTotalKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtDiscTotalKeyReleased
// TODO add your handling code here:
    }//GEN-LAST:event_txtDiscTotalKeyReleased

    private void txtDiscTotalFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtDiscTotalFocusGained
        txtDiscTotal.setSelectionStart(0);
        txtDiscTotal.setSelectionEnd(txtDiscTotal.getText().length());
    }//GEN-LAST:event_txtDiscTotalFocusGained

    private void btnSaveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSaveActionPerformed
         udfSave();
    }//GEN-LAST:event_btnSaveActionPerformed

    private void txtPenerimaKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtPenerimaKeyReleased
// TODO add your handling code here:
    }//GEN-LAST:event_txtPenerimaKeyReleased

    private void txtPenerimaFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtPenerimaFocusLost
// TODO add your handling code here:
    }//GEN-LAST:event_txtPenerimaFocusLost

    private void txtPenerimaFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtPenerimaFocusGained
// TODO add your handling code here:
    }//GEN-LAST:event_txtPenerimaFocusGained

    private void btnOk1KeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_btnOk1KeyPressed
        udfLookupPO();
    }//GEN-LAST:event_btnOk1KeyPressed

    private void btnOk1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnOk1ActionPerformed
        udfLookupPO();
    }//GEN-LAST:event_btnOk1ActionPerformed

    private void txtSupplierFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtSupplierFocusLost
        
    }//GEN-LAST:event_txtSupplierFocusLost

    private void txtSupplierKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtSupplierKeyPressed
        if (evt.getKeyCode()==evt.VK_ENTER){
            
        }
    }//GEN-LAST:event_txtSupplierKeyPressed

    private void txtSupplierKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtSupplierKeyReleased
        String sQry="select kode_supplier, coalesce(nama_supplier,'') as nama, " +
                                    "coalesce(alamat,'')||' - '||coalesce(nama_kota,'')   as alamat " +
                                    "from phar_supplier " +
                                    "left join kota k using(kode_kota) " +
                                    "where (kode_supplier||coalesce(nama_supplier,'')||coalesce(alamat,'')) "
                + "iLike '%"+txtSupplier.getText()+"%' order by 2";
        fn.lookup(evt, new Object[]{lblSupplier}, sQry, txtSupplier.getWidth()+lblSupplier.getWidth()+18, 200);
        
    }//GEN-LAST:event_txtSupplierKeyReleased

    private void txtGudangKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtGudangKeyReleased
        String sQry="select kode_gudang, deskripsi from gudang where upper(kode_gudang||coalesce(deskripsi,'')) " +
                    "Like upper('%" + txtGudang.getText() +"%') order by 1";
        fn.lookup(evt, new Object[]{lblGudang}, sQry, txtGudang.getWidth()+lblGudang.getWidth()+18, 100);
    }//GEN-LAST:event_txtGudangKeyReleased

    private void tblGRFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_tblGRFocusGained
        //setEnableComp1(false);
    }//GEN-LAST:event_tblGRFocusGained

    public void setUpSportColumn(JTable table,
                                 TableColumn sportColumn) {
        sportColumn.setCellEditor(new TextEditorOk(conn,"kode_barang,nama_baang","phar_item",jScrollPane1.getX()+this.getX(), jScrollPane1.getY()+this.getY()));
    }
    
    private void txtRemarkKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtRemarkKeyReleased
// TODO add your handling code here:
    }//GEN-LAST:event_txtRemarkKeyReleased

    private void txtRemarkFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtRemarkFocusLost
// TODO add your handling code here:
    }//GEN-LAST:event_txtRemarkFocusLost

    private void txtNoDOKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtNoDOKeyReleased
// TODO add your handling code here:
    }//GEN-LAST:event_txtNoDOKeyReleased

    private void txtNoDOFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtNoDOFocusLost
// TODO add your handling code here:
    }//GEN-LAST:event_txtNoDOFocusLost

//    private void isilist(String sss,JTextField txt){
//    try{
//        lst.setBounds(this.getParent().getParent().getX()+this.getParent().getX()+this.getX()+this.jPanel1.getX() + txt.getX()+10,
//                                this.getParent().getParent().getY()+this.getParent().getY()+this.getY()+this.jPanel1.getY()+txt.getY()+txt.getHeight()+75,300,200);
//        lst.setSQuery(sss);
//        lst.setFocusableWindowState(false);
//        lst.setTxtCari(txt);
//        lst.setLblDes(new javax.swing.JLabel[]{lblSite,lblGudang});
//        lst.setColWidth(0, txtLocation.getWidth()-1);
//        lst.setColWidth(1, 75);
//        lst.setColWidth(2, 75);
//        lst.setColWidth(3, 120);
//        if(lst.getIRowCount()>0){
//            lst.setVisible(true);
//        } else{
//            lst.setVisible(false);
//        }
//    }catch(SQLException se){}
//    }
    
//    private void findData(){
//        txtNoPR.requestFocus();
//        String sss="select no_pr,site_id,location_id,to_char(release_date,'yyyy/MM/dd') from phar_pr where coalesce(flag_tr,'')='T' and upper(no_pr||site_id||location_id||to_char(release_date,'yyyy/MM/dd')) Like upper('%" + txtNoPR.getText().trim()+"%') order by 1";
//        isilist(sss,txtNoPR);
//    }
    
    private void delItem(){
      if (tblGR.getSelectedRow()>=0){
           int iDel = tblGR.getSelectedRow();
           DefaultTableModel model = (DefaultTableModel) tblGR.getModel();
           model.removeRow(iDel); 
           if (model.getRowCount()>0){
                tblGR.setRowSelectionInterval(model.getRowCount()-1,model.getRowCount()-1);
                tblGR.requestFocus();
           }
      }
    }
    
//    private void udfInsertDetail(){
//        lblNo.setText(String.valueOf(tblGR.getRowCount()+1));    
//        ftDateExpired.setText(dateNow);
//        editItem=false;
//        setClearComp2();
//        setEnableComp1(true);
//        txtKodeBarang.selectAll();
//        txtKodeBarang.requestFocus();
//    }
    
    private void tblGRKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_tblGRKeyPressed
        switch (evt.getKeyCode()){
            case java.awt.event.KeyEvent.VK_F2:{
                udfUpdateData();
                break;
            }
            case java.awt.event.KeyEvent.VK_INSERT:{
                udfAddItem();
                break;
            }
            case java.awt.event.KeyEvent.VK_DELETE:{
                delItem();
                break;
            }
//            case java.awt.event.KeyEvent.VK_ENTER:{
//               if(tblGR.getRowCount()>=0){
//                    setEnableComp1(true);
//                    txtKodeBarang.setEnabled(false);
//                    rowSelected=tblGR.getSelectedRow();
//                    if (rowSelected==0){
//                        if (tblGR.getRowCount()>0){
//                        rowSelected=tblGR.getRowCount()-1;}
//                        else {rowSelected=0;}
//                    }
//                    else {if (tblGR.getRowCount()>0){rowSelected--;}else {rowSelected=0;}
//                    }
//                    tblGR.setRowSelectionInterval(rowSelected,rowSelected);
//                    ftQty.requestFocus();
//                    editItem=true;
//                    break;
//               } 
//            }
        }
    }//GEN-LAST:event_tblGRKeyPressed

  private void printKwitansi(String sNo_GR,Boolean okCpy){
            
            PrinterJob job = PrinterJob.getPrinterJob();
            DocFlavor flavor = DocFlavor.SERVICE_FORMATTED.PRINTABLE;
            PrintRequestAttributeSet aset = new HashPrintRequestAttributeSet();
            PrintServiceAttributeSet pset = new HashPrintServiceAttributeSet();
            PrintService[] services = PrintServiceLookup.lookupPrintServices(flavor, null);
            int i=0;
            for(i=0;i<services.length;i++){
                if(services[i].getName().equalsIgnoreCase(sc.getPrintKwtName())){
                    break;
                }
            }
            if (JOptionPane.showConfirmDialog(null,"Siapkan Printer!","Joss Prima Open Source",JOptionPane.YES_NO_OPTION,JOptionPane.QUESTION_MESSAGE)==JOptionPane.YES_OPTION) {            
              //  PrintGood_receipt pn = new PrintGood_receipt(conn,sNo_GR,okCpy,services[i]);
                PrintGood_receipt pn = new PrintGood_receipt(conn,sNo_GR,okCpy,sUserName,services[i]);
            }
    }  
  
  private void setOpenClosePO(){
//      try{
//          boolean itsok=false;
//          String sSql="select fn_phar_get_ok_po_close('"+txtNo_PO.getText().trim()+"') as ok_close";
//          conn.setAutoCommit(false);
//          Statement stat=conn.createStatement();
//          ResultSet rs=stat.executeQuery(sSql);
//          if (rs.next()){
//              itsok=rs.getBoolean("ok_close");
//          }
//          if (itsok){
//                stat.executeUpdate("update phar_po set closed=true where no_po='"+txtNo_PO.getText().trim()+"'");
//          }
//          conn.commit();
//          conn.setAutoCommit(true);
//          rs.close();
//          stat.close();
//      }catch(SQLException se){System.out.println(se.getMessage());
//            try{
//                conn.rollback();
//                conn.setAutoCommit(true);
//            }catch(SQLException ser){System.out.println(ser.getMessage());}
//      }
  }
  
  private Boolean okSaveGood(phar_GoodReceiptBean prBean){
      int ii=0;Boolean itsOk=false;
      while(ii<tblGR.getRowCount() && !itsOk){
          if (fn.udfGetDouble(tblGR.getValueAt(ii,4))>0){
              itsOk=true;
          }
          ii++;
      }
      return itsOk;
  }
  
  
  
    private void udfClear(){
        setClearComp();
        //setClearComp2();
        setBEdit(true);
        setBNew(false);
        setUpBtn();
        setBEdit(false);
        setEnableComp(true);
        
        txtSupplier.requestFocus();
    }
  
    private void udfSaveDetail(String sNota) throws SQLException{
        Statement stDet=null;
        ResultSet rsDet=null;
        int iQty=0;
        String sTglEx="";
//        try {
            stDet = conn.createStatement();
            rsDet = stDet.executeQuery("select * from good_receipt_detail limit 0");
            
            for (int i=0; i<tblGR.getRowCount();i++){
                iQty= fn.udfGetInt(modelBarang.getValueAt(i,11).toString().trim())*fn.udfGetInt(modelBarang.getValueAt(i,14).toString().trim())+
                                fn.udfGetInt(modelBarang.getValueAt(i,12).toString().trim());
                  
                if(tblGR.getValueAt(i,8).toString().trim().equalsIgnoreCase(""))
                    sTglEx=null;
                else
                    sTglEx=tblGR.getValueAt(i,8).toString().trim().substring(6,10).trim()+"/"+tblGR.getValueAt(i,8).toString().trim().substring(3,5).trim()+"/"+tblGR.getValueAt(i,8).toString().trim().substring(0,2).trim();
                    
                rsDet.moveToInsertRow();
                rsDet.updateString("no_penerimaan", sNota) ;
                rsDet.updateString("no_po",modelBarang.getValueAt(i,10).toString());
                rsDet.updateString("item_code",tblGR.getValueAt(i,1).toString().trim());
                rsDet.updateInt("jumlah", iQty);
                rsDet.updateFloat("harga",fn.udfGetFloat(modelBarang.getValueAt(i,15)));
                rsDet.updateFloat("discount",fn.udfGetFloat(modelBarang.getValueAt(i,5)));
                rsDet.updateFloat("tax",fn.udfGetFloat(modelBarang.getValueAt(i,6)));
                rsDet.updateString("user_ins",sUserName);
                rsDet.updateString("keterangan_kondisi","");
                rsDet.updateString("no_batch", modelBarang.getValueAt(i,8).toString());
                rsDet.updateDate("exp_date",sTglEx==null? null: java.sql.Date.valueOf(sTglEx));
                rsDet.insertRow();
            }
//        } catch (SQLException ex) {
//            JOptionPane.showMessageDialog(this, ex.getMessage());
//        }
        
    }
    
  private void SaveData(){
      if (tblGR.getRowCount()>=0){
          try{
              String no_gr="";
              phar_GoodReceiptBean prBean=new phar_GoodReceiptBean();
              prBean.setConn(conn);
              String sTgl,sTglEx; 
              if(!prBean.koreksiPenerimaan(txtNoPenerimaan.getText())){
                JOptionPane.showMessageDialog(this, "Koreksi penerimaan gagal!");
                return;
              }
                  
              if(modelBarang.getRowCount()>0){
                  sTgl=jFDate.getText().substring(6,10).trim()+"-"+jFDate.getText().substring(3,5).trim()+"-"+jFDate.getText().substring(0,2).trim();
                  sTglJthTempo=jFJthTempo.getText().substring(6,10).trim()+"-"+jFJthTempo.getText().substring(3,5).trim()+"-"+jFJthTempo.getText().substring(0,2).trim();

                  prBean.setReleaseDate(sTgl);
                  prBean.setUserId(sUserID);
                  prBean.setUserName(sUserName);
                  prBean.setNo_inv_do_sj(txtNoDO.getText().trim());
                  prBean.setRemark(txtRemark.getText().trim());
                  prBean.setKodeGudang(txtGudang.getText().trim());
                  prBean.setShipping(txtPengirim.getText());
                  prBean.setKode_Supplier(txtSupplier.getText().trim());
                  prBean.setUserTerima(txtPenerima.getText());
                  prBean.setDiscount(fn.udfGetFloat(txtDiscTotal.getText().trim().replace(",", "")));
                  prBean.setPPN(fn.udfGetFloat(txtPPNTotal.getText().trim().replace(",", "")));
                  prBean.setTglJthTempo(sTglJthTempo);
                  prBean.setBiayaKirim(fn.udfGetFloat(txtBiayaLain.getText()));
                  prBean.setBiayaMaterai(fn.udfGetFloat(txtBiayaMaterai.getText()));
                  //prBean.setNoPO(txtNo_PO.getText().trim());
                  conn.setAutoCommit(false);
                  if (okSaveGood(prBean)){
                      no_gr=prBean.add_Good_Receipt();
                      Boolean okClosedPO=true;
                      int ii=0;
                      float discount=0,ppn=0;
                      int iQty;
                      Double qty=0.0,price=0.0;

                      udfSaveDetail(no_gr);

                      conn.commit();
                      conn.setAutoCommit(true);
                      //printKwitansi(no_gr,false);
                  }
              }else{
                  JOptionPane.showMessageDialog(this,"Tidak ada Barang yang diterimakan ...\n!");
              }
          }catch(SQLException se){
                JOptionPane.showMessageDialog(this, se.getMessage());
                try{
                    conn.rollback();
                    conn.setAutoCommit(true);
                    return;
                }catch(SQLException ser){System.out.println(ser.getMessage());}
            }
          
            setOpenClosePO();   //set status closed pada po menjadi true        
            setClearComp();
//            setClearComp2();
            setBEdit(true);
            setBNew(false);
            setUpBtn();
            setBEdit(false);
            setEnableComp(true);
            //DefaultTableModel mdl=(DefaultTableModel)tblGR.getModel();
            modelBarang.setRowCount(0);
            
            okSave=true;
           // udfSave();
      }
}
 
   
  private void KoreksiPR(){
      try{
              conn.setAutoCommit(false);  
              String no_pr="";
              phar_PRBean prBean=new phar_PRBean();
              prBean.setConn(conn);
              prBean.setUserId(sUserID);
              prBean.setUserName(sUserName);
              prBean.setReleaseDate(dateFormatddmmyy);
              prBean.setRequestedBy(sUserName);
              String sTgl; 
              sTgl=jFDate.getText().substring(6,10).trim()+"/"+jFDate.getText().substring(3,5).trim()+"/"+jFDate.getText().substring(0,2).trim();
              prBean.setNeededDate(sTgl);
              prBean.setUserId(sUserID);
              prBean.setFlag_Trx("K");
              no_pr=prBean.add_PR();
              int ii=0;
              while(ii<tblGR.getRowCount()){
                  prBean.setKode_Barang(tblGR.getValueAt(ii,0).toString().trim());
                  prBean.setQty(fn.udfGetDouble(tblGR.getValueAt(ii,3)));
                  prBean.setUOM(tblGR.getValueAt(ii,2).toString().trim());
                  prBean.setKode_Supplier(tblGR.getValueAt(ii,4).toString().trim());
                  prBean.add_PR_detail(no_pr);
                  ii++;
              }
              conn.commit();
              conn.setAutoCommit(true);
              printKwitansi(no_pr,false);
          }catch(SQLException se)
                {
                    System.out.println(se.getMessage());
                    try{
                        conn.rollback();
                        conn.setAutoCommit(true);
                    }catch(SQLException ser){System.out.println(ser.getMessage());}
                    }
            setClearComp();
//            setClearComp2();
            setEnableComp(true);
            setBEdit(false);
            setBNew(false);
            setUpBtn();
            DefaultTableModel mdl=(DefaultTableModel)tblGR.getModel();
            for(int ii=0;ii<tblGR.getRowCount();ii++){
                mdl.removeRow(ii);
            }
  }
  
    
//    private void setClearComp2(){
//        txtKodeBarang.setText("");
//        lblBarang.setText("");
//        lblUOM.setText("");
//        ftQty.setText("");
//        ftPrice.setText("");
//        txtDiscPerItem.setText("0");
//        txtPPNperItem.setText("0");
//        txtTotalLine.setText("0");
//    }
    
//    private Boolean anyData(){
//        int ii=0;boolean find=false;
//        while(!find && ii<tblGR.getRowCount()){
//            if (txtKodeBarang.getText().trim().toLowerCase().equalsIgnoreCase(tblGR.getValueAt(ii,1).toString().trim().toLowerCase())){
//                find=true;
//            }
//            ii++;
//        }
//        return find;
//    }
    
//    private void filltblFromAdd(){
//        if (!txtKodeBarang.getText().trim().equalsIgnoreCase("") && !ftQty.getText().trim().equalsIgnoreCase("")){
//            float discount=0,ppn=0;
//            Double qty=0.0,price=0.0;
//            discount=0;ppn=0;
//            price=Double.valueOf(ftPrice.getText().trim().replace(",",""));
//            discount=Float.valueOf(txtDiscPerItem.getText().trim().replace(",",""));
//            ppn=Float.valueOf(txtPPNperItem.getText().trim().replace(",",""));
//            qty=Double.valueOf(ftQty.getText().trim().replace(",",""));
//            if (!checkDisc_persen.isSelected()){
//                if (!checkPPN_persen.isSelected()){
//                    //ppn=((qty.floatValue()*price.floatValue())-discount)*(ppn/100);
//                    ppn=(ppn*100)/((qty.floatValue()*price.floatValue())-discount); 
//                }
//                //discount=(qty.floatValue()*price.floatValue())*(discount/100);
//                discount=(discount*100)/(qty.floatValue()*price.floatValue());
//            }else{
//                if (!checkPPN_persen.isSelected()){
//                    //ppn=((qty.floatValue()*price.floatValue())-discount)*(ppn/100);
//                    float dis=0;
//                    dis=(qty.floatValue()*price.floatValue())*(discount/100);
//                    ppn=(ppn*100)/((qty.floatValue()*price.floatValue())-dis); 
//                }
//            }
//            
//            
//            //Double ttl=((Double.valueOf(ftPrice.getText().trim().replace(",",""))*Double.valueOf(ftQty.getText().trim().replace(",","")))*(1-(Double.valueOf(txtDiscPerItem.getText().trim())/100)))*(1+Double.valueOf(txtPPNperItem.getText().trim())/100);
//            //Double ttl=(price*qty)-discount+ppn;
//            Double ttl=((price*qty)*(1-(discount/100)))*(1+Double.valueOf(txtPPNperItem.getText().trim())/100);
//            if (!editItem){
//                if (!anyData()){
//                    if(fn.udfGetFloat(ftQty.getText())==0){
//                        JOptionPane.showMessageDialog(this, "Silakan isi Qty terlebih dulu!");
//                        ftQty.requestFocus();
//                        return;
//                    }
//                    if(fn.udfGetFloat(ftPrice.getText())==0){
//                        JOptionPane.showMessageDialog(this, "Silakan isi Harga Satuan terlebih dulu!");
//                        ftPrice.requestFocus();
//                        return;
//                    }
//                    
//                    Double ExtPrice=Double.valueOf(ftPrice.getText().trim().replace(",",""))*(1-(Double.valueOf(txtDiscPerItem.getText().trim())/100));
////                    DefaultTableModel mdl=(DefaultTableModel) tblGR.getModel();
//                    
////                    mdl.addRow(new Object[]{tblGR.getRowCount()+1,txtKodeBarang.getText(),lblBarang.getText().trim(),lblUOM.getText().trim(),Double.valueOf(ftQty.getText().trim().replace(",","")),
////                    Double.valueOf(ftPrice.getText().trim().replace(",","")),txtDiscPerItem.getText().trim(),txtPPNperItem.getText().trim(),ftDateExpired.getText().trim(),ttl});
//                    modelBarang.addRow(new Object[]{tblGR.getRowCount()+1,
//                                            txtKodeBarang.getText(),
//                                            lblBarang.getText().trim(),
//                                            lblUOM.getText().trim(),
//                                            qty,
//                                            price,
//                                            discount,
//                                            ppn,
//                                            ftDateExpired.getText().trim(),
//                                            txtNoBatch.getText(),     //No. Batch
//                                            fn.udfGetFloat(txtTotalLine.getText()),    //Total LIne
//                                            ""});   //No. PO kosong
//                    setClearComp2();
//                    tblGR.setRowSelectionInterval(tblGR.getRowCount()-1,tblGR.getRowCount()-1);
//                    tblGR.setRowSelectionAllowed(true);
//                    tblGR.requestFocus();
//                }else{
//                        JOptionPane.showMessageDialog(this,"Data dengan kode barang '"+txtKodeBarang.getText().trim()+"' sudah pernah masuk..! \n ");
//                        setClearComp2();
//                        tblGR.setRowSelectionInterval(tblGR.getSelectedRow(),tblGR.getSelectedRow());
//                        tblGR.setRowSelectionAllowed(true);
//                        tblGR.requestFocus();
//                    }
//            }else{
//                    tblGR.setValueAt(qty,tblGR.getSelectedRow(),4);
//                    tblGR.setValueAt(price,tblGR.getSelectedRow(),5);
//                    tblGR.setValueAt(discount,tblGR.getSelectedRow(),6);
//                    tblGR.setValueAt(ppn,tblGR.getSelectedRow(),7);
//                    tblGR.setValueAt(ftDateExpired.getText().trim(),tblGR.getSelectedRow(),8);
//                    tblGR.setValueAt(ttl,tblGR.getSelectedRow(),9);
//                    tblGR.setRowSelectionInterval(tblGR.getSelectedRow(),tblGR.getSelectedRow());
////                tblGR.setValueAt(Double.valueOf(ftQty.getText().trim().replace(",","")),tblGR.getSelectedRow(),4);
////                tblGR.setValueAt(Double.valueOf(ftPrice.getText().trim().replace(",","")),tblGR.getSelectedRow(),5);
////                tblGR.setValueAt(txtDiscPerItem.getText().trim(),tblGR.getSelectedRow(),6);
////                tblGR.setValueAt(txtPPNperItem.getText().trim(),tblGR.getSelectedRow(),7);
////                tblGR.setValueAt(ftDateExpired.getText().trim(),tblGR.getSelectedRow(),8);
////                tblGR.setValueAt(ttl,tblGR.getSelectedRow(),9);
////                tblGR.setRowSelectionInterval(tblGR.getSelectedRow(),tblGR.getSelectedRow());
//            }
//            setEnableComp1(false);
//            tblGR.requestFocus();
//        }
//    }
    
    private void formInternalFrameIconified(javax.swing.event.InternalFrameEvent evt) {//GEN-FIRST:event_formInternalFrameIconified
// TODO add your handling code here:
    }//GEN-LAST:event_formInternalFrameIconified

    private void btnCloseActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCloseActionPerformed
        //udfCancel();
        this.dispose();
    }//GEN-LAST:event_btnCloseActionPerformed

        
    private void formInternalFrameClosed(javax.swing.event.InternalFrameEvent evt) {//GEN-FIRST:event_formInternalFrameClosed
        fn.setVisibleList(false);
    }//GEN-LAST:event_formInternalFrameClosed

    public void setBEdit(Boolean lEdit) {
        bEdit = lEdit;
    }
    
    public Boolean getBEdit() {
        return bEdit;
    }
    
    public void setShift(String Shift){
        this.shift=shift;
    }
    
    public void setBNew(Boolean lNew) {
        bNew = lNew;
    }

    public void setNoTrx(String sNoKoreksi) {
        txtNoPenerimaan.setText(sNoKoreksi);
    }
    
    public class MyRowRenderer extends DefaultTableCellRenderer implements TableCellRenderer{
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            //Component comp = getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            
            Color g1 = new Color(239,234,240);//-->>(251,236,177);// Kuning         [251,251,235]
            Color g2 = new Color(239,234,240);//-->>(241,226,167);// Kuning         [247,247,218]
            
            Color w1 = new Color(255,255,255);// Putih
            Color w2 = new Color(250,250,250);// Putih Juga
            
            Color h1 = new Color(255,240,240);// Merah muda
            Color h2 = new Color(250,230,230);// Merah Muda
            
            Color g;
            Color w;
            Color h;
            
            if(column%2==0){
                g = g1;
                w = w1;
                h = h1;
            }else{
                g = g2;
                w = w2;
                h = h2;
            }
            
            
            if(value instanceof Float || value instanceof Integer ||value instanceof Double ){
                
                setHorizontalAlignment(tx.RIGHT);
                value=formatter.format(value);
            }
            
            setForeground(new Color(0,0,0));
            if (row%2==0){
                setBackground(w);
            }else{
                setBackground(g);
            }
            
            setFont(new Font("Tahoma", 0, 14));
            if(isSelected){
                setBackground(new Color(0,102,255));
                setForeground(new Color(255,255,255));
            }
            
            setValue(value);
            return this;
        }
    }
    
    private void TableLook(int kk){
        if(tblGR.getRowCount()==0){
            modelBarang=(DefaultTableModel)tblGR.getModel();
            modelBarang.setNumRows(0);
            tblGR.setModel(modelBarang);
        }
        
        tblGR.getColumnModel().getColumn(0).setPreferredWidth(25);  //No
        tblGR.getColumnModel().getColumn(1).setPreferredWidth(60);  //Kode barang
        tblGR.getColumnModel().getColumn(2).setPreferredWidth(200); //Deskripsi
        tblGR.getColumnModel().getColumn(3).setPreferredWidth(105);  //Jumlah
        tblGR.getColumnModel().getColumn(4).setPreferredWidth(55);  //Harga
        tblGR.getColumnModel().getColumn(5).setPreferredWidth(75);  //Diskon
        tblGR.getColumnModel().getColumn(6).setPreferredWidth(50);  //PPn
        tblGR.getColumnModel().getColumn(7).setPreferredWidth(75);  //Exp Date
        tblGR.getColumnModel().getColumn(8).setPreferredWidth(50);  //no. Batch
        tblGR.getColumnModel().getColumn(9).setPreferredWidth(90);  //Total Line
        tblGR.getColumnModel().getColumn(10).setPreferredWidth(85); //No. PO
//        tblGR.getColumnModel().removeColumn(tblGR.getColumnModel().getColumn(11));  //JmlBesar
//        tblGR.getColumnModel().removeColumn(tblGR.getColumnModel().getColumn(11));  //Jml Kecil
//        tblGR.getColumnModel().removeColumn(tblGR.getColumnModel().getColumn(11));  //Harga
//        tblGR.getColumnModel().removeColumn(tblGR.getColumnModel().getColumn(11));  //Konversi
        
        tblGR.setRowHeight(25);
        for (int i=0;i<tblGR.getColumnCount();i++){
            tblGR.getColumnModel().getColumn(i).setCellRenderer(new MyRowRenderer());
        }
        
        //----------------------
        tblGR.setShowVerticalLines(true);
        tblGR.getTableHeader().setPreferredSize(new Dimension(0,30));
        tblGR.getTableHeader().setBackground(new Color(255,204,204));
        //tblGR.getTableHeader().setForeground(new Color(255,255,0));
        //--------------------
//        if (tblGR.getRowCount() > 0) {
//            tblGR.changeSelection(0, 0,false,false);                
//        } 
        tblGR.setAutoscrolls(true);
     }
    
    private void writeMsg(String sMsg){
        JOptionPane.showMessageDialog(this,sMsg);
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
                        
            total = new Double(0);
            totalDiscount=new Double(0);
            totalVat=new Double(0);
            netto=new Double(0);
            Double ttl=new Double(0);
            Double ttlDisc=new Double(0);
            Double ttlVat=new Double(0);
            for(int i=0;i<table.getRowCount();i++){
                ttl=0.0;
                ttlDisc=0.0;
                ttlVat=0.0;
                ttl=fn.udfGetDouble(table.getValueAt(i,9)); 
                ttlDisc = ttl*(fn.udfGetDouble(table.getValueAt(i,5))/100);
                ttlVat=((ttl-ttlDisc)*(fn.udfGetDouble(table.getValueAt(i,6))/100));
                total = total + ttl;
                totalDiscount = totalDiscount + ttlDisc;
                totalVat = totalVat + ttlVat;
                //table.setValueAt((ttl-ttlDisc)+ttlVat,i,9);
            }
            netto=(total-totalDiscount+totalVat);
            lblTotal.setText(formatter.format(total));
            lblTotalDiscount.setText(formatter.format(totalDiscount));
            lblTotalVat.setText(formatter.format(totalVat));
            lblNetto.setText(formatter.format(netto));
//            if (netto>=batasPO){
//                writeMsg(" Nilai PO sudah lebih dari '"+batasPO+"'");
//            }
        }
    }
    
   private void udfSetTotal(){
        int i=0;
        float sub_total=0, tot_disc=0, tot_ppn=0, jum=0;
        float qty=0, hrg_sat=0, ppn_item=0, disc_item=0, tot_netto=0;
        
        
        for (i=0; i<tblGR.getModel().getRowCount();i++){
//            qty=fn.udfGetFloat(tblGR.getValueAt(i, 4).toString());
//            hrg_sat=fn.udfGetFloat(tblGR.getValueAt(i, 5).toString());
            
            disc_item=fn.udfGetFloat(tblGR.getValueAt(i, 5).toString());
            ppn_item=fn.udfGetFloat(tblGR.getValueAt(i, 6).toString());
            
            sub_total=sub_total+ fn.udfGetFloat(tblGR.getValueAt(i, 9).toString());
            tot_disc=tot_disc+disc_item;
            tot_ppn=tot_ppn+ppn_item;
        }
        
        lblTotal.setText(formatter.format(sub_total));
        lblTotalDiscount.setText(formatter.format(tot_disc));
        lblTotalVat.setText(formatter.format(tot_ppn));
        
//        lblTotalDiscount.setText(formatter.format(tot_disc+fn.udfGetFloat(txtDiscTotal.getText())));
//        lblTotalVat.setText(formatter.format(tot_ppn+fn.udfGetFloat(txtPPNTotal.getText())));
//        tot_netto=fn.udfGetFloat(lblTotal.getText())-fn.udfGetFloat(lblTotalDiscount.getText())+fn.udfGetFloat(lblTotalVat.getText());
        
        tot_netto=fn.udfGetFloat(lblTotal.getText())-fn.udfGetFloat(txtDiscTotal.getText())+fn.udfGetFloat(txtPPNTotal.getText());
        
        tot_netto=tot_netto+fn.udfGetFloat(txtBiayaLain.getText())+fn.udfGetFloat(txtBiayaMaterai.getText());
        lblNetto.setText(formatter.format(tot_netto));
   }
   
    public class SelectionListener implements ListSelectionListener {
         JTable table;
         int rowPos;
         int colPos;
    
        // It is necessary to keep the table since it is not possible
        // to determine the table from the event's source
        SelectionListener(JTable table) {
            this.table = table;
        }
        public void valueChanged(ListSelectionEvent e) {
            if(table.getSelectedRow()>=0){
                tblGR.getModel().addTableModelListener(new MyTableModelListener(tblGR));
                rowPos = table.getSelectedRow();           
                udfSetTotal();
                if (rowPos >=0 && rowPos < table.getRowCount() && table.getValueAt(rowPos,0)!=null) { 
//                    lblNo.setText(tblGR.getValueAt(tblGR.getSelectedRow(),0).toString().trim());
//                    txtKodeBarang.setText(tblGR.getValueAt(tblGR.getSelectedRow(),1).toString().trim());
//                    lblBarang.setText(tblGR.getValueAt(tblGR.getSelectedRow(),2).toString().trim());
//                    lblUOM.setText(tblGR.getValueAt(tblGR.getSelectedRow(),3).toString().trim());
//                    ftQty.setText(tblGR.getValueAt(tblGR.getSelectedRow(),4).toString().trim());
//                    ftQty.setValue(tblGR.getValueAt(tblGR.getSelectedRow(),4).toString().trim());
//                    ftPrice.setText(tblGR.getValueAt(tblGR.getSelectedRow(),5).toString().trim());
//                    txtDiscPerItem.setText(tblGR.getValueAt(tblGR.getSelectedRow(),6).toString().trim());
//                    txtPPNperItem.setText(tblGR.getValueAt(tblGR.getSelectedRow(),7).toString().trim());
//                    ftDateExpired.setText(tblGR.getValueAt(tblGR.getSelectedRow(),8).toString().trim());
//                    ftDateExpired.setValue(tblGR.getValueAt(tblGR.getSelectedRow(),8).toString().trim());
                }
            }
        }
        
    }
    
    public void addPOItem(String sNo) {
       try {
//            TableLook(0); 
            DefaultTableModel myModel=(DefaultTableModel)tblGR.getModel();
//            tblGR.setAutoResizeMode(tblGR.AUTO_RESIZE_OFF);
//            tblGR.getModel().addTableModelListener(new MyTableModelListener(tblGR));
            String sSql="select * from fn_show_po('"+sNo+"')"+ //+txtNo_PO.getText().trim()+"') " +
                        "as(shiping varchar,remark varchar,top integer,kurs numeric,tanggal text,duedate text," +
                        "currency varchar,discount double precision,ppn double precision,kode_supplier varchar, " +
                        "no_po varchar,kode_barang varchar,nama_barang varchar,uom varchar,qty numeric, " +
                        "price numeric,disc numeric,vat numeric,total double precision)";
            
            System.out.println(sSql);
            Statement stat=conn.createStatement();
            ResultSet rs=stat.executeQuery(sSql);
            int row=tblGR.getRowCount()+1;
            while (rs.next()){
                myModel.addRow(new Object[]{row,rs.getString("kode_barang"),rs.getString("nama_barang"),rs.getString("uom"),
                    rs.getDouble("qty"),rs.getDouble("price"),rs.getDouble("disc"),rs.getDouble("vat"),dateNow, "", 
                    rs.getDouble("total"), sNo});
                row++;
                txtRemark.setText(rs.getString("remark"));
                txtSupplier.setText(rs.getString("kode_supplier"));
                //jFDatePO.setText(rs.getString("tanggal"));
            }
            
            tblGR.setRowSelectionInterval(row-2, row-2);
            
            stat.close();
            rs.close();
            tblGR.setRequestFocusEnabled(true);
            
        } catch(SQLException se) {
            System.out.println(se.getMessage());
        }
    }
    
    public void initJDBC() {
       try {
            TableLook(0); 
            DefaultTableModel myModel=(DefaultTableModel)tblGR.getModel();
            tblGR.setAutoResizeMode(tblGR.AUTO_RESIZE_OFF);
            tblGR.getModel().addTableModelListener(new MyTableModelListener(tblGR));
            String sSql="select * from fn_show_po('')"+ //+txtNo_PO.getText().trim()+"') " +
                        "as(shiping varchar,remark varchar,top integer,kurs numeric,tanggal text,duedate text," +
                        "currency varchar,discount double precision,ppn double precision,kode_supplier varchar, " +
                        "no_po varchar,kode_barang varchar,nama_barang varchar,uom varchar,qty numeric, " +
                        "price numeric,disc numeric,vat numeric,total double precision)";
            
            System.out.println(sSql);
            Statement stat=conn.createStatement();
            ResultSet rs=stat.executeQuery(sSql);
            int ii=1;
            while (rs.next()){
                myModel.addRow(new Object[]{ii,rs.getString("kode_barang"),rs.getString("nama_barang"),rs.getString("uom"),
                    rs.getDouble("qty"),rs.getDouble("price"),rs.getDouble("disc"),rs.getDouble("vat"),dateNow, "", 
                    rs.getDouble("total"), "070929001"});
                ii++;
                txtRemark.setText(rs.getString("remark"));
                txtSupplier.setText(rs.getString("kode_supplier"));
                //jFDatePO.setText(rs.getString("tanggal"));
            }
            stat.close();
            rs.close();
            tblGR.setRequestFocusEnabled(true);
            
            if (ii > 1) {
                tblGR.setRowSelectionInterval(0, 0);
            }else{
                myModel.setRowCount(0);
                JOptionPane.showMessageDialog(this,"no PO tidak ada transaksi...");
                setClearComp();
               // txtNo_PO.requestFocus();
            }
            
            
            SelectionListener listener = new SelectionListener(tblGR);
            tblGR.getSelectionModel().addListSelectionListener(listener);
            tblGR.getColumnModel().getSelectionModel().addListSelectionListener(listener);
            tblGR.setRequestFocusEnabled(true);
        
            JTableHeader header = tblGR.getTableHeader();
            Font fH;
            fH=new Font("Tahoma",Font.BOLD,14);
            header.setFont(fH);
            //header.setBackground((new Color(234,243,244)));
            tblGR.setAutoscrolls(true);
        } catch(SQLException se) {
            System.out.println(se.getMessage());
        }
    }
    
   private void udfCancel(){
        if (fn.isListVisible()){fn.setVisibleList(false);}
        if (getBEdit()) {
            setBEdit(false);
            setBNew(false);
            setUpBtn();
            setClearComp();
            modelBarang.setRowCount(0);
            tblGR.setRequestFocusEnabled(true);
        } else {
            this.dispose();
        }
    }
       
    private void setClearComp(){
        txtSupplier.setText("");
        lblSupplier.setText("");
        txtRemark.setText("");
        txtGudang.setText("");
        lblGudang.setText("");
        txtPenerima.setText("");
        txtNoDO.setText("");
        txtPengirim.setText("");
//        lblSite.setText("");
//        txtLocation.setText("");
    }   
    
       
    private void setTab2Enter(){
        Set forwordKeys= getFocusTraversalKeys(
                KeyboardFocusManager.FORWARD_TRAVERSAL_KEYS);
        Set newForwardKeys = new HashSet(forwordKeys);
        newForwardKeys.add(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER,0));
        setFocusTraversalKeys(KeyboardFocusManager.FORWARD_TRAVERSAL_KEYS,newForwardKeys);
    }   
    
    public class MyKeyListener extends KeyAdapter {
        public void keyPressed(KeyEvent evt) {
            int keyKode = evt.getKeyCode();
            switch(keyKode){
                case KeyEvent.VK_ENTER : {
		    if (!fn.isListVisible()){
			Component c = findNextFocus();
			if (c!=null) c.requestFocus();
		    }else{
			fn.lstRequestFocus();
		    }
		    break;
		}
                case KeyEvent.VK_UP : {
		    if (!fn.isListVisible()){
			Component c = findPrevFocus();
			if (c!=null) c.requestFocus();
		    }else{
			fn.lstRequestFocus();
		    }
		    break;
		}
                case KeyEvent.VK_DOWN : {
		    if (!fn.isListVisible()){
			Component c = findNextFocus();
			if (c!=null) c.requestFocus();
		    }else{
			fn.lstRequestFocus();
		    }
		    break;
		}
                case KeyEvent.VK_INSERT: {  //
//                    if (getBEdit()){
                        udfAddItem();
//                    }    
                    break;
                }
                 case KeyEvent.VK_F2: {  //Save
                    if (getBEdit())
                       udfUpdateData();
                    break;
                }
                case KeyEvent.VK_F3: {  //Search
                 //   udfFilter();
                    break;
                }
                case KeyEvent.VK_F4: {  //Edit
                    udfEdit();
                    break;
                }
                case KeyEvent.VK_F5: {  //New -- Add
                    setBEdit(false);
                    setBNew(false);
                    udfSave();
                    break;
                }
                case KeyEvent.VK_F6: {  //Filter
                //    onOpen(cmbFilter.getSelectedItem().toString(),true);
                    break;
                }
                case KeyEvent.VK_F12: {  //Delete
                    if (!getBEdit() && tblGR.getRowCount()>0)
                        udfUpdateData();
                    
                    break;
                }
                case KeyEvent.VK_ESCAPE: {
                    //Jika status button adalah Close
                    if(sClose.equalsIgnoreCase("close")){
                        if(!getBEdit()){
                            if(JOptionPane.showConfirmDialog(null,"Anda Yakin Untuk Keluar?","SHS go Open Sources",JOptionPane.YES_NO_OPTION)==JOptionPane.YES_OPTION){
                                if(fn.isListVisible()){fn.setVisibleList(false);}
                                dispose();
                            }
                        }
                        else
                            if(JOptionPane.showConfirmDialog(null,"Apakah data disimpan sebelum anda keluar?","SHS go Open Sources",JOptionPane.YES_NO_OPTION)==JOptionPane.YES_OPTION){
                                if(fn.isListVisible()){fn.setVisibleList(false);}
                                udfUpdateData();
                            }
                            else{
                                if(fn.isListVisible()){fn.setVisibleList(false);}
                                dispose();
                            }

                            break;
                    }   //Jika cancel
                    else
                        udfCancel();
                }
                //default ;
                
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
    
    private void onOpen(String sField,  boolean bFilter){
        
   }
    
    public void setUserId(String newUserID){
	sUserID = newUserID;
    }
    
    public void setUserName(String newUserName){
	sUserName = newUserName;
    }
    
    public Boolean getBNew() {
        return bNew;
    }
    
     
    private void ButtonIcon(String aFile,javax.swing.JButton newBtn) {              
       javax.swing.ImageIcon myIcon = new javax.swing.ImageIcon(getClass().getResource(aFile));
       newBtn.setIcon(myIcon);
   }
    
    public void udfLoadPenerimaan(){
        if(!txtNoPenerimaan.getText().equalsIgnoreCase("")){
            TableLook(0);
            try {
                    String sHead="select gr.kode_supplier, coalesce(nama_supplier,'') as nama_supplier, coalesce(no_sj_supplier,'') as no_sj_supplier, coalesce(gr.keterangan,'') as keterangan," +
                    "to_char(gr.tanggal, 'dd-MM-yyyy') as tanggal, coalesce(gr.kode_gudang,'') as kode_gudang, coalesce(gd.deskripsi,'') as nama_gudang, coalesce(shipping,'') as pengirim," +
                    "coalesce(biaya_lain, 0) as biaya_lain, coalesce(biaya_materai,0) as biaya_materai, coalesce(discount, 0) as disc, coalesce(tax, 0) as tax," +
                    "coalesce(shipping,'') as pengirim, koreksi " +
                    "from good_receipt gr " +
                    "left join phar_supplier s on s.kode_supplier=gr.kode_supplier " +
                    "left join gudang gd on gd.kode_gudang=gr.kode_gudang where no_penerimaan='"+txtNoPenerimaan.getText()+"' ";
                rsHead=conn.createStatement().executeQuery(sHead);

                if(rsHead.next()){
                    if(rsHead.getBoolean("koreksi")==true){
                        JOptionPane.showMessageDialog(this, "No. Penerimaan tersebut sudah pernah dikoreksi! \n Silakan masukkan No. Penerimaan lain", "Joss prima", JOptionPane.ERROR_MESSAGE);
                        txtNoPenerimaan.requestFocus();
                        return;
                    }
                    
                    txtSupplier.setText(rsHead.getString("kode_supplier"));
                        lblSupplier.setText(rsHead.getString("nama_supplier"));
                        txtNoDO.setText(rsHead.getString("no_sj_supplier"));
                        txtRemark.setText(rsHead.getString("keterangan"));
                        jFDate.setText(rsHead.getString("tanggal"));
                        txtGudang.setText(rsHead.getString("kode_gudang"));
                        lblGudang.setText(rsHead.getString("nama_gudang"));
                        txtPengirim.setText(rsHead.getString("pengirim"));
                        txtBiayaLain.setText(formatter.format(rsHead.getFloat("biaya_lain")));
                        txtBiayaMaterai.setText(formatter.format(rsHead.getFloat("biaya_materai")));
                        txtDiscTotal.setText(formatter.format(rsHead.getFloat("disc")));
                        txtPPNTotal.setText(formatter.format(rsHead.getFloat("tax")));

                        rsDet=conn.createStatement().executeQuery("select * from fn_load_detail_gr('"+txtNoPenerimaan.getText()+"') as (no_po varchar, " +
                                "kode_barang varchar, nama_barang varchar, " + "qty varchar, price numeric, discount float8, vat float8, total float8," +
                                "exp_date text, no_batch varchar, " + "jml_besar numeric, jml_kecil numeric, konversi numeric, jml_asal numeric, hrg_asal float8)");
                        
                        modelBarang=((DefaultTableModel)tblGR.getModel());
                        modelBarang.setNumRows(0);
                        int i = 1;
                        while (rsDet.next()) {
                            modelBarang.addRow(new Object[]{i, 
                                rsDet.getString("kode_barang"), 
                                rsDet.getString("nama_barang"), 
                                rsDet.getString("qty"), 
                                rsDet.getFloat("price"), 
                                rsDet.getFloat("discount"), 
                                rsDet.getFloat("vat"), 
                                rsDet.getString("exp_date"), 
                                rsDet.getString("no_batch"), 
                                rsDet.getFloat("total"), 
                                rsDet.getString("no_po"), 
                                rsDet.getInt("jml_besar"), 
                                rsDet.getInt("jml_kecil"), 
                                rsDet.getFloat("price"), 
                                rsDet.getInt("konversi"),
                                rsDet.getFloat("hrg_asal")
                            });
                            i++;
                        }
                        if (((DefaultTableModel)tblGR.getModel()).getRowCount() > 0) {
                            tblGR.setRowSelectionInterval(0, 0);
                        }


                }else{
                    JOptionPane.showMessageDialog(this, "No. Penerimaan tidak ditemukan. Silakan periksa kembali No. Penerimaan", "Joss prima", JOptionPane.ERROR_MESSAGE);
                    txtNoPenerimaan.requestFocus();
                }
            } catch (SQLException ex) {
                    Logger.getLogger(frmGood_Receipt1_Koreksi.class.getName()).log(Level.SEVERE, null, ex);
                }
        }
        
    }
    
    public void setUpBtn(){
        Boolean okEdit=getBEdit();
            sClose="cancel";
            btnSave.setToolTipText("Save    (F5)");
            btnClose.setToolTipText("Cancel");
            //System.out.println(getBEdit());
            //if (getBNew()) {txtKode.setEditable(true);ClearText();}
//        } else {   //selain edit
//            String fileImageSave="/phar/image/new.png";
//            ButtonIcon(fileImageSave,btnSave);
//            
//            String fileImageCancel="/phar/image/Exit.png";
//            ButtonIcon(fileImageCancel,btnClose);
//            sClose="close";
//            btnSave.setToolTipText("Delete     (F12)");
//            btnClose.setToolTipText("Close");
//            setBEdit(false);
//            setBNew(false);
//        }        
    }
    
    private void formInternalFrameOpened(javax.swing.event.InternalFrameEvent evt) {//GEN-FIRST:event_formInternalFrameOpened
//        lst = new ListRsbm();
//	lst.setVisible(false);
//	lst.setSize(500,200);
//	lst.con = conn;
              
        MaskFormatter fmttgl = null;
        try {
            fmttgl = new MaskFormatter("##/##/####");
        } catch (java.text.ParseException e) {}
        
        try{
            Statement stTgl = conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY);
            ResultSet rtgl = stTgl.executeQuery("select cast(current_date as varchar) as tanggal");
            if (rtgl.next()){
                fdateformat = new SimpleDateFormat("yyyy/MM/dd");
                dateFormatddmmyy=fdateformat.format(rtgl.getDate(1));
                fdateformat = new SimpleDateFormat("dd/MM/yyyy");
                dateNow=fdateformat.format(rtgl.getDate(1));
            }
            stTgl.close();
            rtgl.close();
        }catch(SQLException se){}
        
        jFDate1 = new JFormattedTextField(fmttgl);
        jFDate.setFormatterFactory(jFDate1.getFormatterFactory());
        jFDate.setText(dateNow);
        jFDate.setValue(dateNow);
        
        jFJthTempo.setFormatterFactory(jFDate1.getFormatterFactory());
        jFJthTempo.setText(dateNow);
        jFJthTempo.setValue(dateNow);
        
        
//        ftDateExpired.setFormatterFactory(jFDate1.getFormatterFactory());
//        ftDateExpired.setText(dateNow);
//        ftDateExpired.setValue(dateNow);
            
       // tblGR.addKeyListener(new MyKeyListener());
        SelectionListener listener = new SelectionListener(tblGR);
	tblGR.getSelectionModel().addListSelectionListener(listener);
	tblGR.getColumnModel().getSelectionModel().addListSelectionListener(listener);
        tblGR.getModel().addTableModelListener(new MyTableModelListener(tblGR));
        
	tblGR.setRequestFocusEnabled(true);
        
        //initJDBC(); 
        TableLook(0);
        //setTab2Enter();
        setBNew(true); 
        setBEdit(true);
        setUpBtn();
        setEnableComp(false);
        setFocus();
        //setMouseList();
        //udfSave();
//        txtSupplier.requestFocus();
//        txtSupplier.requestFocusInWindow();
        
        requestFocusInWindow();
        txtSupplier.requestFocus();
        
        
    }//GEN-LAST:event_formInternalFrameOpened

    private void btnEditActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnEditActionPerformed
        udfEditItem();
        
}//GEN-LAST:event_btnEditActionPerformed

    private void jFDateFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jFDateFocusLost
        if(!validateDate(jFDate.getText(),true,"dd/MM/yyyy")){
                JOptionPane.showMessageDialog(null, "Silakan masukkan tanggal dengan format 'dd/MM/yyyy' !");
                
                jFDate.requestFocus();
                return;
                
            }
    }//GEN-LAST:event_jFDateFocusLost

    private void btnHapusActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnHapusActionPerformed
        int iRow=tblGR.getSelectedRow();
        if(iRow>=0){
            if(JOptionPane.showConfirmDialog(this, "Anda yakin untuk menghapus item ini?", "Joss Prima", JOptionPane.YES_NO_OPTION)==JOptionPane.YES_OPTION){
                modelBarang.removeRow(iRow);
                iRow=iRow<=tblGR.getRowCount()? iRow: iRow-1 ;
            }
        }
}//GEN-LAST:event_btnHapusActionPerformed

    private void jFJthTempoFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jFJthTempoFocusLost
        if(!validateDate(jFJthTempo.getText(),true,"dd/MM/yyyy")){
                JOptionPane.showMessageDialog(null, "Silakan masukkan tanggal dengan format 'dd/MM/yyyy' !");
                
                jFJthTempo.requestFocus();
                return;
                
            }
}//GEN-LAST:event_jFJthTempoFocusLost

    private void txtNoPenerimaanFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtNoPenerimaanFocusLost
        
}//GEN-LAST:event_txtNoPenerimaanFocusLost

    private void txtNoPenerimaanKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtNoPenerimaanKeyPressed
        if(evt.getKeyCode()==KeyEvent.VK_ENTER){
            udfLoadPenerimaan();
            
        }
    }//GEN-LAST:event_txtNoPenerimaanKeyPressed

    
    
    private void setEnableComp(Boolean bEna){
//        txtSupplier.setEnabled(false);
        //jFDate.setEnabled(bEna);
        //jFDate.setEnabled(false);
        //jFDatePO.setEnabled(false);
        txtNoDO.setEnabled(bEna);
        //txtNo_PO.setEnabled(bEna);
        txtGudang.setEnabled(bEna);
        txtRemark.setEnabled(bEna);
    }
    
    private void udfEdit(){
        setBEdit(true);
        setBNew(false);
        setUpBtn();
        lblHeader.setText("Edit Good Receipt");
        setEnableComp(false);
        //txt.requestFocus();
    }
     
    private void ClearText(){
        jFDate.setText(dateNow);
        txtSupplier.setText("");
        DefaultTableModel mdlPR=(DefaultTableModel) tblGR.getModel();
        mdlPR.setRowCount(0);
    }
    
    private void udfSave(){
//       if (!(getBEdit() || getBNew())||okSave){
//            setBNew(true);
//            setBEdit(true);
//            setUpBtn();
//            setEnableComp(true);
//            //txtNo_PO.requestFocus();
//            okSave=false;
//       }else{
           if (tblGR.getRowCount()>0 || getBEdit())
            udfUpdateData();
           if (okSave){
               setBNew(true);
               setBEdit(false);
               setUpBtn();
               setEnableComp(true);
           }
//       }   
       //txtSupplier.requestFocus();
       
    }
    
    private void pesanError(String Err){
        JOptionPane.showMessageDialog(this,Err,"Message",JOptionPane.ERROR_MESSAGE);
    }
    
    private boolean CekBeforeSave(){
        boolean bSt=true;
        if(txtSupplier.getText().trim().equalsIgnoreCase("")){
            pesanError("Silahkan isi Supplier Id Terlebih Dahulu!");
            txtSupplier.requestFocus();
            bSt=false;
            return false;
        }else 
//        if(txtNo_PO.getText().trim().equalsIgnoreCase("")){
//            pesanError("Silahkan isi NO PO. Terlebih Dahulu!");
//            txtNo_PO.requestFocus();
//            bSt=false;
//        }
        if(txtGudang.getText().trim().equalsIgnoreCase("")){
            pesanError("Silahkan isi Gudang Terlebih Dahulu!");
            txtGudang.requestFocus();
            bSt=false;
            return false;
        }
//        else 
//        if(txtNoDO.getText().trim().equalsIgnoreCase("")){
//            pesanError("Silahkan isi NO DO/SJ. Terlebih Dahulu!");
//            txtNoDO.requestFocus();
//            bSt=false;
//            return false;
//        }
        
//        if(modelBarang.getRowCount()==0){
//            pesanError("Silahkan detail item penerimaan terlebih dulu!");
//            btnAdd.requestFocus();
//            bSt=false;
//            return false;
//        }
        
       return bSt;
     }
    
    private void udfUpdateData(){
        Phar_TarifBarangBean iTrBean =new Phar_TarifBarangBean();
        if (getBEdit()) {
            if (CekBeforeSave()){
                if (getBNew()) { 
                    SaveData();
                    okSave=true;
                }else{
                    KoreksiPR();
                    setBEdit(false);
                    setBNew(false);
                    setUpBtn();
                }
            }
        }
    }    
    
     private void setMouseList(){
        //jPanel5.addMouseListener(miceListener);
        //txtNo_PO.addMouseListener(miceListener);
        txtGudang.addMouseListener(miceListener);
        txtNoDO.addMouseListener(miceListener);
        txtRemark.addMouseListener(miceListener);
        
        //jFDatePO.addMouseListener(miceListener);
   }
    
   private void setFocus(){
//        txtGudang.addFocusListener(txtFocusListener);
//        txtRemark.addFocusListener(txtFocusListener);
//        txtSupplier.addFocusListener(txtFocusListener);
//        txtNoDO.addFocusListener(txtFocusListener);
//        txtPengirim.addFocusListener(txtFocusListener);
//        
//        txtDiscTotal.addFocusListener(txtFocusListener);
//        txtPPNTotal.addFocusListener(txtFocusListener);
//        txtBiayaLain.addFocusListener(txtFocusListener);
//        txtBiayaLain.addFocusListener(txtFocusListener);
        
        for(int i=0; i<jPanel1.getComponentCount();i++){
            Component c = jPanel1.getComponent(i);
                if(c.getClass().getSimpleName().equalsIgnoreCase("JTEXTFIELD")    || c.getClass().getSimpleName().equalsIgnoreCase("JFormattedTextField")
                || c.getClass().getSimpleName().equalsIgnoreCase("JTextArea") ) {
                    c.addFocusListener(txtFocusListener);
                }
        }
        for(int i=0; i<jPanel2.getComponentCount();i++){
            Component c = jPanel2.getComponent(i);
                if(c.getClass().getSimpleName().equalsIgnoreCase("JTEXTFIELD")    || c.getClass().getSimpleName().equalsIgnoreCase("JFormattedTextField")
                || c.getClass().getSimpleName().equalsIgnoreCase("JTextArea") ) {
                    c.addFocusListener(txtFocusListener);
                }
        }
   }
    
   public static boolean validateDate( String dateStr, boolean allowPast, String formatStr){
             if (formatStr == null) return false; // or throw some kinda exception, possibly a InvalidArgumentException
		SimpleDateFormat df = new SimpleDateFormat(formatStr);
		Date testDate = null;
		try
		{
			testDate = df.parse(dateStr);
		}
		catch (ParseException e)
		{
			// invalid date format
			return false;
		}
		if (!allowPast)
		{
			// initialise the calendar to midnight to prevent 
			// the current day from being rejected
			Calendar cal = Calendar.getInstance();
			cal.set(Calendar.HOUR_OF_DAY, 0);
			cal.set(Calendar.MINUTE, 0);
			cal.set(Calendar.SECOND, 0);
			cal.set(Calendar.MILLISECOND, 0);
			if (cal.getTime().after(testDate)) return false;
		}
		// now test for legal values of parameters
		if (!df.format(testDate).equals(dateStr)) return false;
		return true;
	}
   
   private MouseListener miceListener=new MouseListener() {
        Color g1 = new Color(255,255,0);
        Color g2 = new Color(255,255,255);  
        public void mouseClicked(MouseEvent e) {
        }
        public void mouseEntered(MouseEvent e) {
            Component c=(Component) e.getSource();
            c.setBackground(g1);
        }
        public void mouseExited(MouseEvent e) {
            Component c=(Component) e.getSource();
            c.setBackground(g2);
        }
        public void mousePressed(MouseEvent e) {
        }
        public void mouseReleased(MouseEvent e) {
        }
   };
        
   private FocusListener txtFocusListener=new FocusListener() {
        public void focusGained(FocusEvent e) {
           Component c=(Component) e.getSource();
           c.setBackground(g1);
           //c.setForeground(fPutih);
           //c.setCaretColor(new java.awt.Color(255, 255, 255));
        }
        public void focusLost(FocusEvent e) {
            Component c=(Component) e.getSource();
            c.setBackground(g2);
            //c.setForeground(fHitam);
        }
   };
    
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnAdd;
    private javax.swing.JButton btnClose;
    private javax.swing.JButton btnEdit;
    private javax.swing.JButton btnHapus;
    private javax.swing.JButton btnNew;
    private javax.swing.JButton btnOk1;
    private javax.swing.JButton btnSave;
    private javax.swing.JFormattedTextField jFDate;
    private javax.swing.JFormattedTextField jFJthTempo;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel22;
    private javax.swing.JLabel jLabel27;
    private javax.swing.JLabel jLabel28;
    private javax.swing.JLabel jLabel29;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel30;
    private javax.swing.JLabel jLabel31;
    private javax.swing.JLabel jLabel32;
    private javax.swing.JLabel jLabel33;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JSeparator jSeparator2;
    private javax.swing.JLabel lblGudang;
    private javax.swing.JLabel lblHeader;
    private javax.swing.JLabel lblHeader1;
    private javax.swing.JLabel lblNetto;
    private javax.swing.JLabel lblSupplier;
    private javax.swing.JLabel lblTotal;
    private javax.swing.JLabel lblTotalDiscount;
    private javax.swing.JLabel lblTotalVat;
    private javax.swing.JTable tblGR;
    private javax.swing.JTextField txtBiayaLain;
    private javax.swing.JTextField txtBiayaMaterai;
    private javax.swing.JTextField txtDiscTotal;
    private javax.swing.JTextField txtGudang;
    private javax.swing.JTextField txtNoDO;
    private javax.swing.JTextField txtNoPenerimaan;
    private javax.swing.JTextField txtPPNTotal;
    private javax.swing.JTextField txtPenerima;
    private javax.swing.JTextField txtPengirim;
    private javax.swing.JTextField txtRemark;
    private javax.swing.JTextField txtSupplier;
    // End of variables declaration//GEN-END:variables
    
    Color g1 = new Color(153,255,255);
    Color g2 = new Color(255,255,255); 
    
    Color fHitam = new Color(0,0,0);
    Color fPutih = new Color(255,255,255); 
    
    Color crtHitam =new java.awt.Color(0, 0, 0);
    Color crtPutih = new java.awt.Color(255, 255, 255); 
    
}

