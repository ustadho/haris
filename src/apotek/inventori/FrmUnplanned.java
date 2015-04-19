/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * FrmPOCash.java
 *
 * Created on Jul 19, 2010, 4:14:49 PM
 */

package apotek.inventori;

import apotek.dao.ItemDao;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.FocusTraversalPolicy;
import java.awt.Font;
import java.awt.KeyboardFocusManager;
import java.awt.Toolkit;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.print.PrinterJob;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.print.DocFlavor;
import javax.print.PrintService;
import javax.print.PrintServiceLookup;
import javax.print.attribute.HashPrintRequestAttributeSet;
import javax.print.attribute.HashPrintServiceAttributeSet;
import javax.print.attribute.PrintRequestAttributeSet;
import javax.print.attribute.PrintServiceAttributeSet;
import javax.swing.AbstractCellEditor;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFormattedTextField;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumnModel;
import javax.swing.text.MaskFormatter;
import main.DlgLookup;
import main.GeneralFunction;
import main.MainForm;
import main.SysConfig;
import pembelian.FrmGoodReceipt;
import pembelian.FrmPOCash;

/**
 *
 * @author ustadhtraino
 */
public class FrmUnplanned extends javax.swing.JInternalFrame {
    private GeneralFunction fn;
    private Connection conn;
    //FrmLookupBarang lookupItem=new FrmLookupBarang();
    MyKeyListener kListener=new MyKeyListener();
    private boolean stKoreksi=false;
    private boolean stIn = false;
    private Component aThis;
    ItemDao itemDao=new ItemDao();
    
    /** Creates new form FrmPOCash */
    public FrmUnplanned(boolean b) {
        initComponents();
        this.stIn=b;
        //lblReffNo.setVisible(false); jLabel23.setVisible(false); txtReffNo.setVisible(false);
        tblItem.getColumn("Keterangan").setPreferredWidth(200);
        tblItem.getColumn("Harga").setPreferredWidth(100);
        tblItem.getColumn("Sub Total").setPreferredWidth(110);
        tblItem.getColumn("Expired").setPreferredWidth(100);
        
        tblItem.getColumn("Harga").setCellRenderer(new MyRowRenderer());
        tblItem.getColumn("Sub Total").setCellRenderer(new MyRowRenderer());

        tblItem.getTableHeader().setFont(new Font("Tahoma", 0, 12));
        tblItem.getModel().addTableModelListener(new MyTableModelListener(tblItem));
        tblItem.setRowHeight(22);

        TableColumnModel col=tblItem.getColumnModel();
        MyTableCellEditor cEditor=new MyTableCellEditor();

        tblItem.getColumnModel().getColumn(col.getColumnIndex("Qty")).setCellEditor(cEditor);
        tblItem.getColumnModel().getColumn(col.getColumnIndex("Harga")).setCellEditor(cEditor);
        tblItem.getColumnModel().getColumn(col.getColumnIndex("Expired")).setCellEditor(cEditor);
        tblItem.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            public void valueChanged(ListSelectionEvent e) {
                if(tblItem.getSelectedRow()>=0 && conn!=null){
                    udfLoadKetBawah();
                }
            }
        });
        tblItem.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).
        put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0),
            "selectNextColumnCell");

       
    }

    public void setKoreksi(boolean b){
        this.stKoreksi=b;
        txtReceiptNo.setEnabled(b);
    }

    private void udfLoadKetBawah() {
        if((tblItem.getSelectedRow()<0 || tblItem.getValueAt(tblItem.getSelectedRow(), 0)==null) ) return;
        try{
            String sQry="select sum(coalesce(jumlah,0)) as stock_on_hand from stock s " +
                    "where s.item_code='"+tblItem.getValueAt(tblItem.getSelectedRow(), 0).toString()+"' " +
                    "and s.kode_gudang='"+txtSite.getText()+"' " +
                    "";

            ResultSet rs=conn.createStatement().executeQuery(sQry);
            if(rs.next()){
                txtStockOnHand.setText(fn.dFmt.format(rs.getDouble("stock_on_hand")));

            }else
            {
                txtStockOnHand.setText("0");
            }

        }catch(SQLException se){
            JOptionPane.showMessageDialog(null, se.getMessage());
        }
    }

    public void setConn(Connection con){
        this.conn=con;
    }

    private void udfNew(){
        txtReceiptNo.setText("");
        btnNew.setEnabled(false);
        txtReceiptType.setText(""); lblReceiptType.setText("");
        //txtSite.setText(""); lblSite.setText("");
        txtRemark.setText("");
        ((DefaultTableModel)tblItem.getModel()).setNumRows(0);
        txtStockOnHand.setText("0");
        txtTotAmount.setText("0");
        txtReceiptType.requestFocus();
    }

    private boolean  udfCekBeforeSave(){
        if(txtReceiptType.getText().trim().length()==0){
            JOptionPane.showMessageDialog(this, "Silakan pilih Receipt Type terlebih dulu!");
            txtReceiptType.requestFocus();
            return false;
        }
        if(txtSite.getText().trim().length()==0){
            JOptionPane.showMessageDialog(this, "Silakan pilih nama Site terlebih dulu!");
            txtSite.requestFocus();
            return false;
        }
        if(tblItem.getRowCount()==0){
            JOptionPane.showMessageDialog(this, "Item yang akan dimasukkan masih kosong!");
            tblItem.requestFocus();
            return false;
        }
        
        TableColumnModel col=tblItem.getColumnModel();
        for(int i=0; i<tblItem.getRowCount(); i++){
            if(fn.udfGetDouble(tblItem.getValueAt(i, tblItem.getColumnModel().getColumnIndex("Qty")))==0){
                JOptionPane.showMessageDialog(this, "Qty item pada baris ke : "+ (i+1) +" masih Nol!");
                tblItem.changeSelection(i, tblItem.getColumnModel().getColumnIndex("Qty"), false, false);
                return false;
            }
//            if(stIn==true && tblItem.getValueAt(i, col.getColumnIndex("Expired")).toString().equalsIgnoreCase("")){
//                JOptionPane.showMessageDialog(this, "Expired Date pada baris ke : "+(i+1)+" masih kosong!", "Information", JOptionPane.INFORMATION_MESSAGE);
//                tblItem.grabFocus();
//                tblItem.changeSelection(i, col.getColumnIndex("Expired"), false, false);
//                return false;
//            }
        }

        return true;
    }

    private void udfSave(){
        if(!udfCekBeforeSave()) return;

        try{
            ResultSet rs=conn.createStatement().executeQuery("select fn_phar_get_no_unplanned('"+ MainForm.sUserID+"') ");

            if(rs.next())
                txtReceiptNo.setText(rs.getString(1));

            rs.close();

            String sIns="insert into phar_unplanned(unplanned_no, site_id, trx_type_id, remark, receipt_by, user_ins) " +
                    "values('"+txtReceiptNo.getText()+"', '"+txtSite.getText()+"', '"+txtReceiptType.getText()+"', " +
                    "'"+txtRemark.getText()+"', '"+txtReceiptBy.getText()+"', '"+txtReceiptBy.getText()+"'); ";

            TableColumnModel col=tblItem.getColumnModel();
            String sExpDate="";
            for(int i=0; i<tblItem.getRowCount(); i++){
                
                try {
                    sExpDate=(tblItem.getValueAt(i, col.getColumnIndex("Expired")).toString().length() == 0 || 
                              tblItem.getValueAt(i, col.getColumnIndex("Expired")).toString().equalsIgnoreCase("01/01/00") ? "'1900-01-01'" : "'" + new SimpleDateFormat("dd/MM/yy").parse(tblItem.getValueAt(i, col.getColumnIndex("Expired")).toString()) + "'");
                    
                    sIns += "insert into phar_unplanned_detail(unplanned_no, kode_barang, qty, expired_date, unit_price, user_ins) " +
                            "values('" + txtReceiptNo.getText() + "', " +
                            "'" + tblItem.getValueAt(i, col.getColumnIndex("Kode")).toString() + "', " +
                             fn.udfGetInt(tblItem.getValueAt(i, col.getColumnIndex("Qty"))) + ", " +
                             sExpDate + "," +
                             fn.udfGetDouble(tblItem.getValueAt(i, col.getColumnIndex("Harga")))+ ", " +
                             "'"+txtReceiptBy.getText()+"'); ";
                } catch (ParseException ex) {
                    Logger.getLogger(FrmUnplanned.class.getName()).log(Level.SEVERE, null, ex);
                }
            }

            System.out.println(sIns);
            
            conn.setAutoCommit(false);
            int i=conn.createStatement().executeUpdate(sIns);
            conn.setAutoCommit(true);

            //JOptionPane.showMessageDialog(this, "Simpan Receipt Unplaned Sukses!");
            printKwitansi(txtReceiptNo.getText(), false);

            udfNew();
        }catch(SQLException se){
            try {
                conn.rollback();
                JOptionPane.showMessageDialog(this, se.getMessage());
            } catch (SQLException ex) {
                Logger.getLogger(FrmUnplanned.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    private void printKwitansi(String sUnplannedNo, Boolean okCpy){
        PrinterJob job = PrinterJob.getPrinterJob();
        SysConfig sy=new SysConfig();

        DocFlavor flavor = DocFlavor.SERVICE_FORMATTED.PRINTABLE;
        PrintRequestAttributeSet aset = new HashPrintRequestAttributeSet();
        PrintServiceAttributeSet pset = new HashPrintServiceAttributeSet();
        PrintService[] services = PrintServiceLookup.lookupPrintServices(flavor, null);
        int i=0;
        for(i=0;i<services.length;i++){
            if(services[i].getName().equalsIgnoreCase(sy.getPrintKwtName())){
                break;
            }
        }
        if (JOptionPane.showConfirmDialog(null,"Siapkan Printer!","SHS Go Open Source",JOptionPane.YES_NO_OPTION,JOptionPane.QUESTION_MESSAGE)==JOptionPane.YES_OPTION) {
//            PrintReceiptUnplanned pn = new PrintReceiptUnplanned(conn,sUnplannedNo, okCpy, PHARMainMenu.sUserName, services[i]);
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
        jLabel18 = new javax.swing.JLabel();
        txtSite = new javax.swing.JTextField();
        jLabel6 = new javax.swing.JLabel();
        txtRemark = new javax.swing.JTextField();
        lblSite = new javax.swing.JLabel();
        jLabel14 = new javax.swing.JLabel();
        lblReceiptType = new javax.swing.JLabel();
        txtReceiptType = new javax.swing.JTextField();
        jLabel20 = new javax.swing.JLabel();
        jLabel21 = new javax.swing.JLabel();
        jLabel22 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        txtDate = new javax.swing.JTextField();
        jLabel17 = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        txtReceiptBy = new javax.swing.JTextField();
        jLabel13 = new javax.swing.JLabel();
        jLabel19 = new javax.swing.JLabel();
        txtReceiptNo = new javax.swing.JTextField();
        jLabel15 = new javax.swing.JLabel();
        jLabel16 = new javax.swing.JLabel();
        jToolBar1 = new javax.swing.JToolBar();
        btnNew = new javax.swing.JButton();
        btnSave = new javax.swing.JButton();
        btnCancel = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        tblItem = new javax.swing.JTable();
        jPanel2 = new javax.swing.JPanel();
        jLabel25 = new javax.swing.JLabel();
        txtStockOnHand = new javax.swing.JLabel();
        jSeparator2 = new javax.swing.JSeparator();
        jLabel27 = new javax.swing.JLabel();
        txtTotAmount = new javax.swing.JLabel();
        jLabel28 = new javax.swing.JLabel();

        setClosable(true);
        setIconifiable(true);
        setMaximizable(true);
        setResizable(true);
        setTitle("Receipt Unplanned");
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
            }
            public void internalFrameOpened(javax.swing.event.InternalFrameEvent evt) {
                formInternalFrameOpened(evt);
            }
        });

        jPanel1.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        jPanel1.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel18.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jLabel18.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel18.setText("Gudang");
        jPanel1.add(jLabel18, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 35, 90, 20));

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
        jPanel1.add(txtSite, new org.netbeans.lib.awtextra.AbsoluteConstraints(110, 35, 60, 20));

        jLabel6.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jLabel6.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel6.setText("Keterangan");
        jPanel1.add(jLabel6, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 60, 90, 20));

        txtRemark.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
        txtRemark.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 1, true));
        txtRemark.setDisabledTextColor(new java.awt.Color(0, 0, 0));
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
        jPanel1.add(txtRemark, new org.netbeans.lib.awtextra.AbsoluteConstraints(110, 60, 400, 20));

        lblSite.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
        lblSite.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 1, true));
        lblSite.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                lblSitePropertyChange(evt);
            }
        });
        jPanel1.add(lblSite, new org.netbeans.lib.awtextra.AbsoluteConstraints(170, 35, 260, 20));

        jLabel14.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jLabel14.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel14.setText(":");
        jPanel1.add(jLabel14, new org.netbeans.lib.awtextra.AbsoluteConstraints(100, 10, 10, 20));

        lblReceiptType.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
        lblReceiptType.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 1, true));
        lblReceiptType.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                lblReceiptTypePropertyChange(evt);
            }
        });
        jPanel1.add(lblReceiptType, new org.netbeans.lib.awtextra.AbsoluteConstraints(170, 10, 260, 20));

        txtReceiptType.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
        txtReceiptType.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 1, true));
        txtReceiptType.setDisabledTextColor(new java.awt.Color(153, 153, 153));
        txtReceiptType.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                txtReceiptTypeFocusLost(evt);
            }
        });
        txtReceiptType.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtReceiptTypeKeyReleased(evt);
            }
        });
        jPanel1.add(txtReceiptType, new org.netbeans.lib.awtextra.AbsoluteConstraints(110, 10, 60, 20));

        jLabel20.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jLabel20.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel20.setText("Tipe Transaksi");
        jPanel1.add(jLabel20, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 10, 100, 20));

        jLabel21.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jLabel21.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel21.setText(":");
        jPanel1.add(jLabel21, new org.netbeans.lib.awtextra.AbsoluteConstraints(100, 35, 10, 20));

        jLabel22.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jLabel22.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel22.setText(":");
        jPanel1.add(jLabel22, new org.netbeans.lib.awtextra.AbsoluteConstraints(100, 60, 10, 20));

        jLabel3.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jLabel3.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel3.setText("Tanggal");
        jPanel1.add(jLabel3, new org.netbeans.lib.awtextra.AbsoluteConstraints(530, 30, 70, 20));

        txtDate.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
        txtDate.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 1, true));
        txtDate.setDisabledTextColor(new java.awt.Color(0, 0, 0));
        txtDate.setEnabled(false);
        txtDate.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                txtDateFocusLost(evt);
            }
        });
        txtDate.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtDateKeyReleased(evt);
            }
        });
        jPanel1.add(txtDate, new org.netbeans.lib.awtextra.AbsoluteConstraints(620, 30, 110, 20));

        jLabel17.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jLabel17.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel17.setText("No. Trans");
        jPanel1.add(jLabel17, new org.netbeans.lib.awtextra.AbsoluteConstraints(530, 10, 80, 20));

        jLabel9.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jLabel9.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel9.setText("User :");
        jPanel1.add(jLabel9, new org.netbeans.lib.awtextra.AbsoluteConstraints(530, 50, 70, 20));

        txtReceiptBy.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
        txtReceiptBy.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 1, true));
        txtReceiptBy.setDisabledTextColor(new java.awt.Color(0, 0, 0));
        txtReceiptBy.setEnabled(false);
        txtReceiptBy.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                txtReceiptByFocusLost(evt);
            }
        });
        txtReceiptBy.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtReceiptByKeyReleased(evt);
            }
        });
        jPanel1.add(txtReceiptBy, new org.netbeans.lib.awtextra.AbsoluteConstraints(620, 50, 120, 20));

        jLabel13.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jLabel13.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel13.setText(":");
        jPanel1.add(jLabel13, new org.netbeans.lib.awtextra.AbsoluteConstraints(610, 30, 10, 20));

        jLabel19.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jLabel19.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel19.setText(":");
        jPanel1.add(jLabel19, new org.netbeans.lib.awtextra.AbsoluteConstraints(610, 50, 10, 20));

        txtReceiptNo.setFont(new java.awt.Font("Dialog", 0, 14)); // NOI18N
        txtReceiptNo.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 1, true));
        txtReceiptNo.setDisabledTextColor(new java.awt.Color(0, 0, 0));
        txtReceiptNo.setEnabled(false);
        txtReceiptNo.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                txtReceiptNoFocusLost(evt);
            }
        });
        txtReceiptNo.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                txtReceiptNoPropertyChange(evt);
            }
        });
        txtReceiptNo.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtReceiptNoKeyReleased(evt);
            }
            public void keyTyped(java.awt.event.KeyEvent evt) {
                txtReceiptNoKeyTyped(evt);
            }
        });
        txtReceiptNo.addVetoableChangeListener(new java.beans.VetoableChangeListener() {
            public void vetoableChange(java.beans.PropertyChangeEvent evt)throws java.beans.PropertyVetoException {
                txtReceiptNoVetoableChange(evt);
            }
        });
        jPanel1.add(txtReceiptNo, new org.netbeans.lib.awtextra.AbsoluteConstraints(620, 10, 120, 20));

        jLabel15.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jLabel15.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel15.setText(":");
        jPanel1.add(jLabel15, new org.netbeans.lib.awtextra.AbsoluteConstraints(610, 10, 10, 20));

        jLabel16.setFont(new java.awt.Font("Tahoma", 0, 36)); // NOI18N
        jLabel16.setForeground(new java.awt.Color(0, 0, 153));
        jLabel16.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel16.setText("Receipt Unplanned");

        jToolBar1.setRollover(true);

        btnNew.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/Icon/buttons/add-32.png"))); // NOI18N
        btnNew.setText("New");
        btnNew.setFocusable(false);
        btnNew.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnNew.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnNew.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnNewActionPerformed(evt);
            }
        });
        jToolBar1.add(btnNew);

        btnSave.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/Icon/buttons/Ok-32.png"))); // NOI18N
        btnSave.setText("Save");
        btnSave.setFocusable(false);
        btnSave.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnSave.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnSave.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSaveActionPerformed(evt);
            }
        });
        jToolBar1.add(btnSave);

        btnCancel.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/Icon/buttons/exit-32.png"))); // NOI18N
        btnCancel.setText("Exit");
        btnCancel.setFocusable(false);
        btnCancel.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnCancel.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnCancel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCancelActionPerformed(evt);
            }
        });
        jToolBar1.add(btnCancel);

        tblItem.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        tblItem.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Kode", "Keterangan", "Qty", "Expired", "Satuan", "Harga", "Sub Total"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Object.class, java.lang.Object.class, java.lang.Double.class, java.lang.Object.class, java.lang.Object.class, java.lang.Double.class, java.lang.Double.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, true, true, false, true, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        tblItem.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_OFF);
        jScrollPane1.setViewportView(tblItem);

        jPanel2.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        jPanel2.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel25.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jLabel25.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel25.setText("Quantity Gudang:");
        jPanel2.add(jLabel25, new org.netbeans.lib.awtextra.AbsoluteConstraints(280, 10, 130, 20));

        txtStockOnHand.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
        txtStockOnHand.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        txtStockOnHand.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 1, true));
        txtStockOnHand.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                txtStockOnHandPropertyChange(evt);
            }
        });
        jPanel2.add(txtStockOnHand, new org.netbeans.lib.awtextra.AbsoluteConstraints(410, 10, 80, 20));

        jSeparator2.setOrientation(javax.swing.SwingConstants.VERTICAL);
        jPanel2.add(jSeparator2, new org.netbeans.lib.awtextra.AbsoluteConstraints(510, 0, 10, 80));

        jLabel27.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jLabel27.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel27.setText("Total");
        jPanel2.add(jLabel27, new org.netbeans.lib.awtextra.AbsoluteConstraints(520, 10, 90, 20));

        txtTotAmount.setFont(new java.awt.Font("Dialog", 1, 12)); // NOI18N
        txtTotAmount.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        txtTotAmount.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 1, true));
        txtTotAmount.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                txtTotAmountPropertyChange(evt);
            }
        });
        jPanel2.add(txtTotAmount, new org.netbeans.lib.awtextra.AbsoluteConstraints(620, 10, 120, 20));

        jLabel28.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jLabel28.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel28.setText(":");
        jPanel2.add(jLabel28, new org.netbeans.lib.awtextra.AbsoluteConstraints(610, 10, 10, 20));

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(10, 10, 10)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jToolBar1, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel16, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jScrollPane1)
                    .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, 750, Short.MAX_VALUE))
                .addGap(3, 3, 3))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(10, 10, 10)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jToolBar1, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel16, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, 90, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(3, 3, 3)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 200, Short.MAX_VALUE)
                .addGap(7, 7, 7)
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(8, 8, 8))
        );

        setBounds(0, 0, 779, 457);
    }// </editor-fold>//GEN-END:initComponents

    private void txtSiteFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtSiteFocusLost
        // TODO add your handling code here:
}//GEN-LAST:event_txtSiteFocusLost

    private void txtSiteKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtSiteKeyReleased
        fn.lookup(evt, new Object[]{lblSite},
                "select kode_gudang, coalesce(deskripsi,'') as nama_gudang from gudang " +
                "where upper(kode_gudang||coalesce(deskripsi,'')) Like upper('%" + txtSite.getText() +"%') order by 2",
                txtSite.getWidth()+lblSite.getWidth(), 150);
}//GEN-LAST:event_txtSiteKeyReleased

    private void txtRemarkFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtRemarkFocusLost
        // TODO add your handling code here:
}//GEN-LAST:event_txtRemarkFocusLost

    private void txtRemarkKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtRemarkKeyReleased
        // TODO add your handling code here:
}//GEN-LAST:event_txtRemarkKeyReleased

    private void lblSitePropertyChange(java.beans.PropertyChangeEvent evt) {//GEN-FIRST:event_lblSitePropertyChange
        // TODO add your handling code here:
}//GEN-LAST:event_lblSitePropertyChange

    private void lblReceiptTypePropertyChange(java.beans.PropertyChangeEvent evt) {//GEN-FIRST:event_lblReceiptTypePropertyChange
        // TODO add your handling code here:
}//GEN-LAST:event_lblReceiptTypePropertyChange

    private void txtReceiptTypeFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtReceiptTypeFocusLost
        // TODO add your handling code here:
}//GEN-LAST:event_txtReceiptTypeFocusLost

    private void txtReceiptTypeKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtReceiptTypeKeyReleased
        fn.lookup(evt, new Object[]{lblReceiptType}, "select trx_type_id, coalesce(trx_type_name,'') as type_name " +
                "from phar_trx_type where coalesce(in_out, false)="+stIn+" and trx_type_id ||coalesce(trx_type_name,'') ilike '%"+txtReceiptType.getText()+"%' order by 2",
                txtReceiptType.getWidth()+lblReceiptType.getWidth(), 200);
}//GEN-LAST:event_txtReceiptTypeKeyReleased

    private void txtDateFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtDateFocusLost
        // TODO add your handling code here:
}//GEN-LAST:event_txtDateFocusLost

    private void txtDateKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtDateKeyReleased
        // TODO add your handling code here:
}//GEN-LAST:event_txtDateKeyReleased

    private void txtReceiptByFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtReceiptByFocusLost
        // TODO add your handling code here:
}//GEN-LAST:event_txtReceiptByFocusLost

    private void txtReceiptByKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtReceiptByKeyReleased
        // TODO add your handling code here:
}//GEN-LAST:event_txtReceiptByKeyReleased

    private void txtReceiptNoFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtReceiptNoFocusLost
        // TODO add your handling code here:
}//GEN-LAST:event_txtReceiptNoFocusLost

    private void txtReceiptNoPropertyChange(java.beans.PropertyChangeEvent evt) {//GEN-FIRST:event_txtReceiptNoPropertyChange
        // TODO add your handling code here:
}//GEN-LAST:event_txtReceiptNoPropertyChange

    private void txtReceiptNoKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtReceiptNoKeyReleased
        // TODO add your handling code here:
}//GEN-LAST:event_txtReceiptNoKeyReleased

    private void txtReceiptNoKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtReceiptNoKeyTyped
        // TODO add your handling code here:
}//GEN-LAST:event_txtReceiptNoKeyTyped

    private void txtReceiptNoVetoableChange(java.beans.PropertyChangeEvent evt)throws java.beans.PropertyVetoException {//GEN-FIRST:event_txtReceiptNoVetoableChange
        // TODO add your handling code here:
}//GEN-LAST:event_txtReceiptNoVetoableChange

    private void btnNewActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnNewActionPerformed
        udfNew();
}//GEN-LAST:event_btnNewActionPerformed

    private void btnSaveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSaveActionPerformed
        udfSave();
}//GEN-LAST:event_btnSaveActionPerformed

    private void btnCancelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCancelActionPerformed
        if(btnCancel.getText().equalsIgnoreCase("cancel")){
            btnSave.setEnabled(false);
            btnNew.setEnabled(true);
            btnCancel.setText("Exit");
            btnCancel.setIcon(new javax.swing.ImageIcon(getClass().getResource("/pharpurchase/image/Icon/Exit.png")));
        }else{
            this.dispose();
        }
}//GEN-LAST:event_btnCancelActionPerformed

    private void txtStockOnHandPropertyChange(java.beans.PropertyChangeEvent evt) {//GEN-FIRST:event_txtStockOnHandPropertyChange
        // TODO add your handling code here:
}//GEN-LAST:event_txtStockOnHandPropertyChange

    private void txtTotAmountPropertyChange(java.beans.PropertyChangeEvent evt) {//GEN-FIRST:event_txtTotAmountPropertyChange
        // TODO add your handling code here:
    }//GEN-LAST:event_txtTotAmountPropertyChange

    private void formInternalFrameOpened(javax.swing.event.InternalFrameEvent evt) {//GEN-FIRST:event_formInternalFrameOpened
        udfInitForm();
    }//GEN-LAST:event_formInternalFrameOpened

    private void formInternalFrameClosed(javax.swing.event.InternalFrameEvent evt) {//GEN-FIRST:event_formInternalFrameClosed
        fn.setVisibleList(false);
    }//GEN-LAST:event_formInternalFrameClosed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnCancel;
    private javax.swing.JButton btnNew;
    private javax.swing.JButton btnSave;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel18;
    private javax.swing.JLabel jLabel19;
    private javax.swing.JLabel jLabel20;
    private javax.swing.JLabel jLabel21;
    private javax.swing.JLabel jLabel22;
    private javax.swing.JLabel jLabel25;
    private javax.swing.JLabel jLabel27;
    private javax.swing.JLabel jLabel28;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JSeparator jSeparator2;
    private javax.swing.JToolBar jToolBar1;
    private javax.swing.JLabel lblReceiptType;
    private javax.swing.JLabel lblSite;
    private javax.swing.JTable tblItem;
    private javax.swing.JTextField txtDate;
    private javax.swing.JTextField txtReceiptBy;
    private javax.swing.JTextField txtReceiptNo;
    private javax.swing.JTextField txtReceiptType;
    private javax.swing.JTextField txtRemark;
    private javax.swing.JTextField txtSite;
    private javax.swing.JLabel txtStockOnHand;
    private javax.swing.JLabel txtTotAmount;
    // End of variables declaration//GEN-END:variables

    JTextField ustTextField = new JTextField() {
        protected boolean processKeyBinding(final KeyStroke ks, final KeyEvent e, final int condition, final boolean pressed) {
            if (hasFocus()) {
                return super.processKeyBinding(ks, e, condition, pressed);
            } else {
                this.requestFocus();
                SwingUtilities.invokeLater(new Runnable() {
                    public void run() {
                        processKeyBinding(ks, e, condition, pressed);
                    }
              });
                return true;
            }
        }
    };

    public class MyTableCellEditor extends AbstractCellEditor implements TableCellEditor {
        private Toolkit toolkit;
        JTextField text= ustTextField;

        JFormattedTextField fText=getFormattedText();

        int col, row;


        public Component getTableCellEditorComponent(JTable table, Object value,
            boolean isSelected, int rowIndex, int vColIndex) {
            // 'value' is value contained in the cell located at (rowIndex, vColIndex)
            row=rowIndex;
            col=vColIndex;
            if(vColIndex==tblItem.getColumnModel().getColumnIndex("Expired"))
                text=fText;
            else
                text=ustTextField;

            text.setName("textEditor");

//            if(stMinus)
//                text.setEditable(vColIndex==tblItem.getColumnModel().getColumnIndex("On Receipt"));

            if(vColIndex==tblItem.getColumnModel().getColumnIndex("Harga")||
                    vColIndex==tblItem.getColumnModel().getColumnIndex("Qty")){
               text.addKeyListener(kListener);
            }else{
               text.removeKeyListener(kListener);
            }

           //col=vColIndex;
           text.setBackground(new Color(0,255,204));
           text.addFocusListener(txtFocusListener);
           //text.addKeyListener(kListener);
           text.setFont(table.getFont());
           //text.setName("textEditor");


            text.setText(value==null? "": value.toString());
            //component.setText(df.format(value));

            if(value instanceof Double || value instanceof Float|| value instanceof Integer){
               text.setText(fn.dFmt.format(value));

            }else
                text.setText(value==null? "":value.toString());
           return text;
        }

        public Object getCellEditorValue() {
            Object o="";//=component.getText();
            Object retVal = 0;
            try {
                if(col==tblItem.getColumnModel().getColumnIndex("Expired")){
                    if(!fn.validateDate(((JTextField)text).getText(), true, "dd/MM/yy")){
                        JOptionPane.showMessageDialog(JOptionPane.getFrameForComponent(FrmUnplanned.this),
                                "Silakan isikan format tanggal dengan 'dd/MM/yy'\n" +
                                "Contoh: 31/12/19");

                        retVal=tblItem.getValueAt(row, col).toString();
                        tblItem.requestFocusInWindow();
                        tblItem.changeSelection(row, tblItem.getColumnModel().getColumnIndex("Expired"), false, false);
//                    }else if(((JTextField)text).getText().equalsIgnoreCase("01/01/00") && 
//                            tblItem.getValueAt(row, tblItem.getColumnModel().getColumnIndex("Jenis")).toString().equalsIgnoreCase("OBAT")){
//                        JOptionPane.showMessageDialog(aThis, "Untuk jenis item Obat masukkan tanggal expired lebih besar dari '01/01/00'!");
//                        o=tblItem.getValueAt(row, tblItem.getColumnModel().getColumnIndex("Expired")).toString();
//                        tblItem.requestFocusInWindow();
//                        tblItem.changeSelection(row, tblItem.getColumnModel().getColumnIndex("Expired"), false, false);
//                        return o;
                    }else
                        retVal = ((JTextField)text).getText();

                }else if(col==tblItem.getColumnModel().getColumnIndex("Qty")){
                    retVal = fn.udfGetDouble(((JTextField)text).getText());
                        if(stIn==false && fn.udfGetDouble(((JTextField)text).getText())> fn.udfGetDouble(txtStockOnHand.getText()) ){
                            JOptionPane.showMessageDialog(aThis, "Qty lebih besar dari sisa stok!");
                            retVal=fn.udfGetDouble(tblItem.getValueAt(row, tblItem.getColumnModel().getColumnIndex("Qty")));
                        }else
                            retVal = fn.udfGetDouble(((JTextField)text).getText());

                        //return retVal;
                    
                }else if(col==tblItem.getColumnModel().getColumnIndex("Harga")){
                    retVal = fn.udfGetDouble(((JTextField)text).getText());
                }

                return retVal;
            } catch (Exception e) {
                toolkit.beep();
                retVal=0;
            }
            return retVal;
        }

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

            if(conn==null ) return;
            if(mColIndex==0){
                String sKodeBarang=table.getValueAt(table.getSelectedRow(), 0).toString();

                try{
                    String sQry="select  i.kode_barang, coalesce(i.nama_barang,'') as nama, coalesce(i.base_price,0) as b_price, " +
                            "coalesce(i.uom_kecil,'') as uom, coalesce((select hpp from phar_kartu_stock_all where kode_barang='"+sKodeBarang+"' and harga>0 " +
                            "order by id_kartu_stock desc limit 1),0) as hpp, coalesce(j.kategori,'OBAT') as jenis " +
                            "from phar_item i "
                            + "left join phar_jenis_barang j on j.jenis_id=i.jenis_id " +
                            "where kode_barang='"+sKodeBarang+"' " ;
                            
                    ResultSet rs=conn.createStatement().executeQuery(sQry);
                    if(rs.next()){
                        TableColumnModel col=table.getColumnModel();
                        int iRow=table.getSelectedRow();
                        double stockPrice = rs.getDouble("hpp");
                        stockPrice=stockPrice==0? rs.getDouble("b_price"): rs.getDouble("hpp");

                        ((DefaultTableModel)tblItem.getModel()).setValueAt(rs.getString("nama"), iRow, col.getColumnIndex("Keterangan"));
                        ((DefaultTableModel)tblItem.getModel()).setValueAt(new Double(0), iRow, col.getColumnIndex("Qty"));
                        ((DefaultTableModel)tblItem.getModel()).setValueAt(rs.getString("uom"), iRow, col.getColumnIndex("Satuan"));
                        ((DefaultTableModel)tblItem.getModel()).setValueAt(stockPrice, iRow, col.getColumnIndex("Harga"));
                        //((DefaultTableModel)tblItem.getModel()).setValueAt(new Double(0), iRow, col.getColumnIndex("Amount"));
                        ((DefaultTableModel)tblItem.getModel()).setValueAt("", iRow, col.getColumnIndex("Expired"));
                        ((DefaultTableModel)tblItem.getModel()).setValueAt(rs.getString("jenis"), iRow, col.getColumnIndex("Jenis"));
                        
                        udfLoadKetBawah();

                    }
                    rs.close();
                }catch(SQLException se){
                    JOptionPane.showMessageDialog(FrmUnplanned.this, se.getMessage());
                }
            }else if((mColIndex==table.getColumnModel().getColumnIndex("Qty") && tblItem.getSelectedColumn()==table.getColumnModel().getColumnIndex("Qty"))||
                     (mColIndex==table.getColumnModel().getColumnIndex("Harga") && tblItem.getSelectedColumn()==table.getColumnModel().getColumnIndex("Harga"))
                     ){
                if(tblItem.getSelectedRow()<0) return;
                double qty=0, disc=0, price=0;

                if(tblItem.getValueAt(tblItem.getSelectedRow(), tblItem.getColumnModel().getColumnIndex("Qty"))!=null)
                    qty=fn.udfGetDouble(tblItem.getValueAt(tblItem.getSelectedRow(), tblItem.getColumnModel().getColumnIndex("Qty")));
                if(tblItem.getValueAt(tblItem.getSelectedRow(), tblItem.getColumnModel().getColumnIndex("Harga"))!=null)
                    price=fn.udfGetDouble(tblItem.getValueAt(tblItem.getSelectedRow(), tblItem.getColumnModel().getColumnIndex("Harga")));
                
                tblItem.setValueAt((qty*price), tblItem.getSelectedRow(), tblItem.getColumnModel().getColumnIndex("Sub Total"));
                double totAmount=0, totDisc=0, totNetto=0;
                
                TableColumnModel col=tblItem.getColumnModel();
                
                for(int i=0; i<tblItem.getRowCount(); i++){
                    price=fn.udfGetDouble(tblItem.getValueAt(i, col.getColumnIndex("Qty")))*fn.udfGetDouble(tblItem.getValueAt(i, col.getColumnIndex("Harga")));
                    totAmount+=price;
                    
                }
                txtTotAmount.setText(numFmt.format(totAmount));

            }
        }
    }

    NumberFormat numFmt=new DecimalFormat("#,##0.00");
    NumberFormat nFmt=new DecimalFormat("#,##0");

    public class MyRowRenderer extends DefaultTableCellRenderer implements TableCellRenderer{
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {


            if(value instanceof Double ||value instanceof Float  ){
                setHorizontalAlignment(SwingConstants.RIGHT);
                value=numFmt.format(value);
            }else if(value instanceof Integer ){
                setHorizontalAlignment(SwingConstants.RIGHT);
                value=nFmt.format(value);

            }
            setFont(table.getFont());
            if(isSelected){
                setBackground(table.getSelectionBackground());
                setForeground(table.getSelectionForeground());
            }else{
                setBackground(table.getBackground());
                setForeground(table.getForeground());
            }

            if (hasFocus) {
                setBorder( UIManager.getBorder("Table.focusCellHighlightBorder") );
                if (!isSelected && table.isCellEditable(row, column)) {
                    Color col;
                    col = UIManager.getColor("Table.focusCellForeground");
                    if (col != null) {
                        super.setForeground(col);
                    }
                    col = UIManager.getColor("Table.focusCellBackground");
                    if (col != null) {
                        super.setBackground(col);
                    }
                }
            } else {
                setBorder(noFocusBorder);
            }


            setValue(value);
            return this;
        }
    }

    private void udfInitForm(){
        txtSite.setText(MainForm.sKodeGudang); lblSite.setText(MainForm.sNamaGudang);
        fn=new GeneralFunction(conn);
        fn.addKeyListenerInContainer(jToolBar1, kListener, txtFocusListener);
        fn.addKeyListenerInContainer(jPanel1, kListener, txtFocusListener);
        fn.addKeyListenerInContainer(jPanel2, kListener, txtFocusListener);
        tblItem.addKeyListener(kListener);
        aThis=this;
        try{
            ResultSet rs=conn.createStatement().executeQuery("select to_char(current_date, 'dd/MM/yyyy')");
            rs.next();
            txtDate.setText(rs.getString(1));
            
            rs.close();
        }catch(SQLException se){
            JOptionPane.showMessageDialog(this, se.getMessage());
        }
        
        Runnable doRun = new Runnable() {
            public void run() {
                txtReceiptType.requestFocusInWindow();
            }
        };
        SwingUtilities.invokeLater(doRun);
        txtReceiptBy.setText(MainForm.sUserName);
        if(stIn==false){
            tblItem.getColumn("Expired").setMinWidth(0);
            tblItem.getColumn("Expired").setPreferredWidth(0);
            tblItem.getColumn("Expired").setMaxWidth(0);
        }
        udfNew();
        jLabel16.setText(getTitle());
    }

    public class MyKeyListener extends KeyAdapter {
        @Override
        public void keyPressed(KeyEvent evt) {
            Component ct = KeyboardFocusManager.getCurrentKeyboardFocusManager().getPermanentFocusOwner();
            int keyKode = evt.getKeyCode();
            switch(keyKode){
                case KeyEvent.VK_INSERT:{
                    DlgLookup d1=new DlgLookup(JOptionPane.getFrameForComponent(FrmUnplanned.this), true);
                    String sItem="";
                    for(int i=0; i< tblItem.getRowCount(); i++){
                        sItem+=(sItem.length()==0? "" : ",") +"'"+tblItem.getValueAt(i, 0).toString()+"'";
                    }

                    String s="select * from (" +
                            "select item_code as kode_barang, coalesce(nama_paten,'') as nama_barang, "
                            + "coalesce(satuan_kecil,'') as satuan_kecil from " +
                            "barang b "+
                            (sItem.length()>0? "where item_code not in("+sItem+")":"")+" order by 2) x ";

                    //System.out.println(s);
//                    ((DefaultTableModel)tblPR.getModel()).setNumRows(tblPR.getRowCount()+1);
//                    tblPR.setRowSelectionInterval(tblPR.getRowCount()-1, tblPR.getRowCount()-1);
                    d1.setTitle("Lookup Barang");
                    d1.udfLoad(conn, s, "(kode_barang||nama_barang)", null);

                    d1.setVisible(true);

                    //System.out.println("Kode yang dipilih" +d1.getKode());
                    if(d1.getKode().length()>0){
                        TableColumnModel col=d1.getTable().getColumnModel();
                        JTable tbl=d1.getTable();
                        int iRow = tbl.getSelectedRow();

                        ((DefaultTableModel)tblItem.getModel()).addRow(new Object[]{
                            tbl.getValueAt(iRow, col.getColumnIndex("kode_barang")).toString(),
                            tbl.getValueAt(iRow, col.getColumnIndex("nama_barang")).toString(),
                            0, //QtyR
                            "", //Expired
                            tbl.getValueAt(iRow, col.getColumnIndex("satuan_kecil")).toString(), //Uom
                            itemDao.getHpp(d1.getKode()),  //Harga
                            0,  //Sub Total
                        });

                        tblItem.setRowSelectionInterval(tblItem.getRowCount()-1, tblItem.getRowCount()-1);
                        tblItem.changeSelection(tblItem.getRowCount()-1, tblItem.getColumnModel().getColumnIndex("Qty"), false, false);
                        tblItem.requestFocusInWindow();
                    }
                    break;
                }
               case KeyEvent.VK_ENTER : {
                    if(!(ct instanceof JTable))                    {
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
                                if (c==null) return;
                                c.requestFocus();
                            }else{
                                fn.lstRequestFocus();
                            }
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
                case KeyEvent.VK_DELETE:{
                    if(evt.getSource().equals(tblItem) && tblItem.getSelectedRow()>=0){
                        ((DefaultTableModel)tblItem.getModel()).removeRow(tblItem.getSelectedRow());
                    }
                    break;
                }
                case KeyEvent.VK_ESCAPE:{
                    if(JOptionPane.showConfirmDialog(null,"Anda Yakin Untuk Keluar?",
                            "SHS Pharmacy",JOptionPane.YES_NO_OPTION)==JOptionPane.YES_OPTION){
                        dispose();
                    }
                    break;
                }
                case KeyEvent.VK_F2:{
                    udfSave();
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

    public JFormattedTextField getFormattedText(){
        JFormattedTextField fText=null;
        try {
            fText = new JFormattedTextField(new MaskFormatter("##/##/##")){
                protected boolean processKeyBinding(final KeyStroke ks, final KeyEvent e, final int condition, final boolean pressed) {
                    if (hasFocus()) {
                        return super.processKeyBinding(ks, e, condition, pressed);
                    } else {
                        this.requestFocus();
                        SwingUtilities.invokeLater(new Runnable() {
                            public void run() {
                                processKeyBinding(ks, e, condition, pressed);
                            }
                      });
                        return true;
                    }
                }
            };
        } catch (ParseException ex) {
            Logger.getLogger(FrmGoodReceipt.class.getName()).log(Level.SEVERE, null, ex);
        }

        return fText;
    }

    private FocusListener txtFocusListener=new FocusListener() {
        public void focusGained(FocusEvent e) {
            if(e.getSource() instanceof JTextField || e.getSource() instanceof JFormattedTextField || e.getSource() instanceof JComboBox){
                ((JTextField)e.getSource()).setBackground(Color.YELLOW);
////
//                if(e.getSource().equals(ustTextField)||e.getSource().equals(txtUnitPrice)||e.getSource().equals(txtDisc)){
                    ((JTextField)e.getSource()).setSelectionStart(0);
                    ((JTextField)e.getSource()).setSelectionEnd(((JTextField)e.getSource()).getText().length());
//                }
//                else if(e.getSource().equals(txtDokter)){
//////                    txtDokter.setEnabled(isJasaMedis);
////                }
            }
        }

        public void focusLost(FocusEvent e) {
            boolean b=false;
            if(e.getSource() instanceof JTextField || e.getSource() instanceof JFormattedTextField || e.getSource() instanceof JComboBox){
                ((JTextField)e.getSource()).setBackground(Color.WHITE);
                    if(e.getSource().equals(txtReceiptType)){
                    //lblReffNo.setVisible(false); jLabel23.setVisible(false); txtReffNo.setVisible(false);
                    if(!fn.isListVisible() && txtReceiptType.getText().length()>0){
                        b = (getSourceType().equalsIgnoreCase("RETUR SUPP"));
                        //lblReffNo.setVisible(b); jLabel23.setVisible(b); txtReffNo.setVisible(b);
                    }
                }
            }
        }
    } ;

    private String getSourceType(){
        String s="";
        try{
            ResultSet rs=conn.createStatement().executeQuery(
                    "select coalesce(source_type,'') from phar_trx_type where trx_type_id='"+txtReceiptType.getText()+"'  ");

            if(rs.next())
                s=rs.getString(1);

            rs.close();


        }catch(SQLException se){

        }
        return s;
    }

}
