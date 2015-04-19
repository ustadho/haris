/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * FrmPOCash.java
 *
 * Created on Jul 19, 2010, 4:14:49 PM
 */

package pembelian;

import main.MainForm;
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
import javax.swing.SwingUtilities;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableColumnModel;
import javax.swing.text.MaskFormatter;
import main.DlgLookup;
import main.GeneralFunction;
import main.SysConfig;

/**
 *
 * @author ustadhtraino
 */
public class FrmPOCash extends javax.swing.JInternalFrame {
    private GeneralFunction fn;
    private Connection conn;
    MyKeyListener kListener=new MyKeyListener();
    private boolean stKoreksi=false;

    /** Creates new form FrmPOCash */
    public FrmPOCash() {
        initComponents();
        tblItem.getColumn("Keterangan").setPreferredWidth(200);
        tblItem.getColumn("Price").setPreferredWidth(100);
        tblItem.getColumn("Disc").setPreferredWidth(50);
        tblItem.getColumn("Amount").setPreferredWidth(110);
        tblItem.getColumn("Expired").setPreferredWidth(100);

        tblItem.getTableHeader().setFont(new Font("Tahoma", 0, 12));
        tblItem.getModel().addTableModelListener(new MyTableModelListener(tblItem));
        tblItem.setRowHeight(22);
        //AutoCompleteDecorator.decorate(jComboBox1);

        TableColumnModel col=tblItem.getColumnModel();
        MyTableCellEditor cEditor=new MyTableCellEditor();

        tblItem.getColumnModel().getColumn(col.getColumnIndex("Qty")).setCellEditor(cEditor);
        tblItem.getColumnModel().getColumn(col.getColumnIndex("Price")).setCellEditor(cEditor);
        tblItem.getColumnModel().getColumn(col.getColumnIndex("Disc")).setCellEditor(cEditor);
        tblItem.getColumnModel().getColumn(col.getColumnIndex("Expired")).setCellEditor(new MyTableCellEditor());
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
        txtNoPO.setEnabled(b);
    }

    private void udfLoadKetBawah() {
        try{
            String sQry="select coalesce(sb.uom_alt,'') as uom_alt, coalesce(sb.convertion,0) as convertion, " +
                    "coalesce(i.satuan_kecil,'') as uom_kecil, max(priority) as priority, " +
                    "coalesce(stock,0) as stock_on_hand, coalesce(on_order,0) as stock_on_order," +
                    "coalesce(min,0) as min, coalesce(max,0) as max " +
                    "from barang i " +
                    "left join supplier_barang sb on sb.kode_barang=i.item_code " +
                    "where i.item_code='"+tblItem.getValueAt(tblItem.getSelectedRow(), 0)+"' " +
                    "and sb.kode_supplier='"+txtSupplier.getText()+"' "
                    + "group by coalesce(sb.uom_alt,'') , coalesce(sb.convertion,0), " +
                    "coalesce(i.satuan_kecil,''), coalesce(stock,0), coalesce(on_order,0)," +
                    "coalesce(min,0), coalesce(max,0)" +
                    "limit 1";
            
            System.out.println(sQry);    
            ResultSet rs=conn.createStatement().executeQuery(sQry);
            if(rs.next()){
                txtConv.setText(fn.dFmt.format(rs.getInt("convertion")));
                lblUomKecil.setText(rs.getString("uom_kecil"));

                txtStockOnHand.setText(fn.dFmt.format(rs.getDouble("stock_on_hand")));

            }else
            {
                txtConv.setText(""); lblUomKecil.setText(""); txtStockOnHand.setText("");
            }

        }catch(SQLException se){
            JOptionPane.showMessageDialog(null, se.getMessage());
        }
    }

    public void setConn(Connection con){
        this.conn=con;
    }

    private void udfNew(){
        txtNoPO.setText("");
        btnNew.setEnabled(false);
        txtSupplier.setText(""); lblSupplier.setText("");
        //txtSite.setText(""); lblSite.setText("");
        txtRemark.setText("");
        ((DefaultTableModel)tblItem.getModel()).setNumRows(0);
        txtSupplier.requestFocus();
    }

    private boolean  udfCekBeforeSave(){
        if(txtSupplier.getText().trim().length()==0){
            JOptionPane.showMessageDialog(this, "Silakan pilih supplier terlebih dulu!");
            txtSupplier.requestFocus();
            return false;
        }
        if(txtSite.getText().trim().length()==0){
            JOptionPane.showMessageDialog(this, "Silakan pilih nama Site terlebih dulu!");
            txtSite.requestFocus();
            return false;
        }
        if(!stKoreksi &&  tblItem.getRowCount()==0){
            JOptionPane.showMessageDialog(this, "Item yang akan dimasukkan masih kosong!");
            tblItem.requestFocus();
            return false;
        }
        for(int i=0; i<tblItem.getRowCount(); i++){
            if(fn.udfGetDouble(tblItem.getValueAt(i, tblItem.getColumnModel().getColumnIndex("Qty")))==0){
                JOptionPane.showMessageDialog(this, "Qty item pada baris ke : "+ (i+1) +" masih Nol!");
                tblItem.changeSelection(i, tblItem.getColumnModel().getColumnIndex("Qty"), false, false);
                return false;
            }
        }

        return true;
    }

    private void udfSave(){
        if(!udfCekBeforeSave()) return;
        
        String sNoKoreksi="", sNewPO="";
        try{
            ResultSet rs=null;
            conn.setAutoCommit(false);
            if(stKoreksi){
                rs=conn.createStatement().executeQuery("select fn_phar_po_cash_koreksi('"+txtNoPO.getText()+"', " +
                        "'"+MainForm.sUserID+"', '"+MainForm.sUserName+"')");
                if(rs.next()){
                    sNoKoreksi=rs.getString(1);
                }
                rs.close();
            }

            if(tblItem.getRowCount()>0){
                rs=conn.createStatement().executeQuery("select fn_phar_get_no_po_cash('"+MainForm.sUserID+"'," +
                        " '"+new SimpleDateFormat("yyyy-MM-dd").format(new SimpleDateFormat("dd/MM/yyyy").parse(txtDate.getText()))+"'::date) ");

                if(rs.next()){
                    txtNoPO.setText(rs.getString(1));
                    sNewPO=rs.getString(1);
                }
                rs.close();

                String sIns="insert into phar_po_cash(no_po_cash, tanggal, kode_supplier, discount, ppn, top, remark, user_ins, consigment, cara_bayar, site_id) " +
                        "values('"+txtNoPO.getText()+"', now(), " + //'"+new SimpleDateFormat("dd/MM/yyyy").parse(txtDate.getText())+"'
                        "'"+txtSupplier.getText()+"', 0, 0, "+fn.udfGetInt(txtTOP.getText())+", " +
                        "'"+txtRemark.getText()+"', '"+txtReceiptBy.getText()+"', "+chkConsigment.isSelected()+", '"+jComboBox1.getSelectedIndex()+"', " +
                        "'"+txtSite.getText()+"'); ";

                TableColumnModel col=tblItem.getColumnModel();

                SimpleDateFormat ymd=new SimpleDateFormat("yyyy-MM-dd");
                for(int i=0; i<tblItem.getRowCount(); i++){
                    try {
                        sIns += "insert into phar_po_cash_detail(no_po_cash, kode_barang, jumlah, expired_date, harga, discount, ppn, user_ins) " +
                                "values('" + txtNoPO.getText() + "', '" + tblItem.getValueAt(i, col.getColumnIndex("Product ID")).toString() + "', " +
                                 fn.udfGetInt(tblItem.getValueAt(i, col.getColumnIndex("Qty"))) + ", " +
                                 (tblItem.getValueAt(i, col.getColumnIndex("Expired")).toString().length() == 0 ? "'1900-01-01'" : "'" + ymd.format(new SimpleDateFormat("dd/MM/yy").parse(tblItem.getValueAt(i, col.getColumnIndex("Expired")).toString())) + "'") + "," +
                                 fn.udfGetDouble(tblItem.getValueAt(i, col.getColumnIndex("Price")))+ ", " +
                                 fn.udfGetDouble(tblItem.getValueAt(i, col.getColumnIndex("Disc")))+ ", 0, " +
                                 "'"+txtReceiptBy.getText()+"'); ";
                    } catch (ParseException ex) {
                        Logger.getLogger(FrmPOCash.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }

                System.out.println(sIns);

                int i=conn.createStatement().executeUpdate(sIns);
                conn.setAutoCommit(true);
            }

            JOptionPane.showMessageDialog(this, stKoreksi? "PO Cash Dikoreksi dengan nomor '"+sNoKoreksi+"', " +
                    (tblItem.getRowCount()>0? "dan dibenarkan dengan nomor PO baru '"+txtNoPO.getText()+"' ": "")
                    : "Simpan PO Cash Sukses!");
            printKwitansi(sNoKoreksi, sNewPO);

            udfNew();
        } catch (ParseException ex) {
            Logger.getLogger(FrmPOCash.class.getName()).log(Level.SEVERE, null, ex);
        }catch(SQLException se){
            try {
                conn.rollback();
                JOptionPane.showMessageDialog(this, se.getMessage());
            } catch (SQLException ex) {
                Logger.getLogger(FrmPOCash.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    private void printKwitansi(String sNoKoreksi, String sNewPo){
//        PrinterJob job = PrinterJob.getPrinterJob();
//        SysConfig sy=new SysConfig();
//
//        DocFlavor flavor = DocFlavor.SERVICE_FORMATTED.PRINTABLE;
//        PrintRequestAttributeSet aset = new HashPrintRequestAttributeSet();
//        PrintServiceAttributeSet pset = new HashPrintServiceAttributeSet();
//        PrintService[] services = PrintServiceLookup.lookupPrintServices(flavor, null);
//        int i=0;
//        for(i=0;i<services.length;i++){
//            if(services[i].getName().equalsIgnoreCase(sy.getPrintKwtName())){
//                break;
//            }
//        }
//        if (JOptionPane.showConfirmDialog(null,"Printer Siap!","SHS Go Open Source",JOptionPane.YES_NO_OPTION,JOptionPane.QUESTION_MESSAGE)==JOptionPane.YES_OPTION) {
//            PrintPOCash pn ;
//            if(sNoKoreksi.length()>0)
//                pn= new PrintPOCash(conn, sNoKoreksi, false, services[i]);
//
//            if(sNewPo.length()>0)
//                pn= new PrintPOCash(conn, sNewPo, false, services[i]);
//
//        }
    }


//    private void printKwitansi(String sNo_PR, Boolean okCpy){
//        PrinterJob job = PrinterJob.getPrinterJob();
//        SysConfig sy=new SysConfig();
//
//        DocFlavor flavor = DocFlavor.SERVICE_FORMATTED.PRINTABLE;
//        PrintRequestAttributeSet aset = new HashPrintRequestAttributeSet();
//        PrintServiceAttributeSet pset = new HashPrintServiceAttributeSet();
//        PrintService[] services = PrintServiceLookup.lookupPrintServices(flavor, null);
//        int i=0;
//        for(i=0;i<services.length;i++){
//            if(services[i].getName().equalsIgnoreCase(sy.getPrintKwtName())){
//                break;
//            }
//        }
//        if (JOptionPane.showConfirmDialog(null,"Simpan Good Receipt Sukses. Selanjutnya akan di Print!","SGHS Go Open Source",JOptionPane.YES_NO_OPTION,JOptionPane.QUESTION_MESSAGE)==JOptionPane.YES_OPTION) {
//            PrintPOCash pn = new PrintPOCash(conn, txtNoPO.getText(), false, services[i]);
//        }
//    }

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
        lblSupplier = new javax.swing.JLabel();
        txtSupplier = new javax.swing.JTextField();
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
        txtNoPO = new javax.swing.JTextField();
        jLabel15 = new javax.swing.JLabel();
        jLabel23 = new javax.swing.JLabel();
        jLabel24 = new javax.swing.JLabel();
        jComboBox1 = new javax.swing.JComboBox();
        jLabel7 = new javax.swing.JLabel();
        txtTOP = new javax.swing.JTextField();
        jLabel31 = new javax.swing.JLabel();
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
        jLabel26 = new javax.swing.JLabel();
        txtConv = new javax.swing.JLabel();
        lblUomKecil = new javax.swing.JLabel();
        jSeparator2 = new javax.swing.JSeparator();
        jLabel27 = new javax.swing.JLabel();
        txtTotAmount = new javax.swing.JLabel();
        jLabel28 = new javax.swing.JLabel();
        jLabel29 = new javax.swing.JLabel();
        jLabel30 = new javax.swing.JLabel();
        txtTotDisc = new javax.swing.JLabel();
        jLabel33 = new javax.swing.JLabel();
        jLabel34 = new javax.swing.JLabel();
        txtTotNetto = new javax.swing.JLabel();
        chkConsigment = new javax.swing.JCheckBox();

        setClosable(true);
        setIconifiable(true);
        setMaximizable(true);
        setResizable(true);
        setTitle("PO Cash");
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
        getContentPane().setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

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
        jLabel6.setText("Remark");
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
        jPanel1.add(txtRemark, new org.netbeans.lib.awtextra.AbsoluteConstraints(110, 60, 390, 20));

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

        lblSupplier.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
        lblSupplier.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 1, true));
        lblSupplier.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                lblSupplierPropertyChange(evt);
            }
        });
        jPanel1.add(lblSupplier, new org.netbeans.lib.awtextra.AbsoluteConstraints(170, 10, 260, 20));

        txtSupplier.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
        txtSupplier.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 1, true));
        txtSupplier.setDisabledTextColor(new java.awt.Color(153, 153, 153));
        txtSupplier.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                txtSupplierFocusLost(evt);
            }
        });
        txtSupplier.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtSupplierKeyReleased(evt);
            }
        });
        jPanel1.add(txtSupplier, new org.netbeans.lib.awtextra.AbsoluteConstraints(110, 10, 60, 20));

        jLabel20.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jLabel20.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel20.setText("Supplier");
        jPanel1.add(jLabel20, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 10, 90, 20));

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
        jLabel3.setText("Date");
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
        jLabel17.setText("PO Cash #");
        jPanel1.add(jLabel17, new org.netbeans.lib.awtextra.AbsoluteConstraints(530, 10, 80, 20));

        jLabel9.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jLabel9.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel9.setText("Receipt By");
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

        txtNoPO.setFont(new java.awt.Font("Dialog", 0, 14)); // NOI18N
        txtNoPO.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 1, true));
        txtNoPO.setDisabledTextColor(new java.awt.Color(0, 0, 0));
        txtNoPO.setEnabled(false);
        txtNoPO.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                txtNoPOFocusLost(evt);
            }
        });
        txtNoPO.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                txtNoPOPropertyChange(evt);
            }
        });
        txtNoPO.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtNoPOKeyReleased(evt);
            }
            public void keyTyped(java.awt.event.KeyEvent evt) {
                txtNoPOKeyTyped(evt);
            }
        });
        txtNoPO.addVetoableChangeListener(new java.beans.VetoableChangeListener() {
            public void vetoableChange(java.beans.PropertyChangeEvent evt)throws java.beans.PropertyVetoException {
                txtNoPOVetoableChange(evt);
            }
        });
        jPanel1.add(txtNoPO, new org.netbeans.lib.awtextra.AbsoluteConstraints(620, 10, 120, 20));

        jLabel15.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jLabel15.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel15.setText(":");
        jPanel1.add(jLabel15, new org.netbeans.lib.awtextra.AbsoluteConstraints(610, 10, 10, 20));

        jLabel23.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jLabel23.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel23.setText("Cara Bayar");
        jPanel1.add(jLabel23, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 85, 90, 20));

        jLabel24.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jLabel24.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel24.setText(":");
        jPanel1.add(jLabel24, new org.netbeans.lib.awtextra.AbsoluteConstraints(100, 85, 10, 20));

        jComboBox1.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "KREDIT", "TUNAI" }));
        jPanel1.add(jComboBox1, new org.netbeans.lib.awtextra.AbsoluteConstraints(110, 85, 200, -1));

        jLabel7.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jLabel7.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel7.setText("TOP");
        jPanel1.add(jLabel7, new org.netbeans.lib.awtextra.AbsoluteConstraints(330, 85, 70, 20));

        txtTOP.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
        txtTOP.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 1, true));
        txtTOP.setDisabledTextColor(new java.awt.Color(0, 0, 0));
        jPanel1.add(txtTOP, new org.netbeans.lib.awtextra.AbsoluteConstraints(410, 85, 70, 20));

        jLabel31.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jLabel31.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel31.setText(":");
        jPanel1.add(jLabel31, new org.netbeans.lib.awtextra.AbsoluteConstraints(400, 85, 10, 20));

        getContentPane().add(jPanel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 70, 750, 120));

        jLabel16.setFont(new java.awt.Font("Tahoma", 0, 36)); // NOI18N
        jLabel16.setForeground(new java.awt.Color(0, 0, 153));
        jLabel16.setText("PO Cash");
        getContentPane().add(jLabel16, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 10, 400, 60));

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

        getContentPane().add(jToolBar1, new org.netbeans.lib.awtextra.AbsoluteConstraints(620, 10, 140, 60));

        tblItem.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        tblItem.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Product ID", "Keterangan", "Satuan", "Qty", "Price", "Disc", "Amount", "Expired"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Double.class, java.lang.Double.class, java.lang.Double.class, java.lang.Double.class, java.lang.Object.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, true, true, true, false, true
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

        getContentPane().add(jScrollPane1, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 193, 750, 170));

        jPanel2.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        jPanel2.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel25.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jLabel25.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel25.setText("Stock :");
        jPanel2.add(jLabel25, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 5, 60, 20));

        txtStockOnHand.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
        txtStockOnHand.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        txtStockOnHand.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 1, true));
        txtStockOnHand.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                txtStockOnHandPropertyChange(evt);
            }
        });
        jPanel2.add(txtStockOnHand, new org.netbeans.lib.awtextra.AbsoluteConstraints(70, 5, 80, 20));

        jLabel26.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jLabel26.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel26.setText("Conv = ");
        jPanel2.add(jLabel26, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 30, 50, 20));

        txtConv.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
        txtConv.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        txtConv.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 1, true));
        txtConv.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                txtConvPropertyChange(evt);
            }
        });
        jPanel2.add(txtConv, new org.netbeans.lib.awtextra.AbsoluteConstraints(70, 30, 70, 20));

        lblUomKecil.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        lblUomKecil.setForeground(new java.awt.Color(0, 0, 153));
        lblUomKecil.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblUomKecil.setText("Uom");
        jPanel2.add(lblUomKecil, new org.netbeans.lib.awtextra.AbsoluteConstraints(140, 30, 50, 20));

        jSeparator2.setOrientation(javax.swing.SwingConstants.VERTICAL);
        jPanel2.add(jSeparator2, new org.netbeans.lib.awtextra.AbsoluteConstraints(490, 0, 10, 80));

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

        jLabel29.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jLabel29.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel29.setText("Disc");
        jPanel2.add(jLabel29, new org.netbeans.lib.awtextra.AbsoluteConstraints(520, 30, 90, 20));

        jLabel30.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jLabel30.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel30.setText(":");
        jPanel2.add(jLabel30, new org.netbeans.lib.awtextra.AbsoluteConstraints(610, 30, 10, 20));

        txtTotDisc.setFont(new java.awt.Font("Dialog", 1, 12)); // NOI18N
        txtTotDisc.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        txtTotDisc.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 1, true));
        txtTotDisc.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                txtTotDiscPropertyChange(evt);
            }
        });
        jPanel2.add(txtTotDisc, new org.netbeans.lib.awtextra.AbsoluteConstraints(620, 30, 120, 20));

        jLabel33.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jLabel33.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel33.setText("Netto");
        jPanel2.add(jLabel33, new org.netbeans.lib.awtextra.AbsoluteConstraints(520, 50, 90, 20));

        jLabel34.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jLabel34.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel34.setText(":");
        jPanel2.add(jLabel34, new org.netbeans.lib.awtextra.AbsoluteConstraints(610, 50, 10, 20));

        txtTotNetto.setFont(new java.awt.Font("Dialog", 1, 12)); // NOI18N
        txtTotNetto.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        txtTotNetto.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 1, true));
        txtTotNetto.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                txtTotNettoPropertyChange(evt);
            }
        });
        jPanel2.add(txtTotNetto, new org.netbeans.lib.awtextra.AbsoluteConstraints(620, 50, 120, 20));

        getContentPane().add(jPanel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 370, 750, 80));

        chkConsigment.setText("Consigment");
        getContentPane().add(chkConsigment, new org.netbeans.lib.awtextra.AbsoluteConstraints(410, 40, 180, -1));

        setBounds(0, 0, 779, 492);
    }// </editor-fold>//GEN-END:initComponents

    private void txtSiteFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtSiteFocusLost
        // TODO add your handling code here:
}//GEN-LAST:event_txtSiteFocusLost

    private void txtSiteKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtSiteKeyReleased
        fn.lookup(evt, new Object[]{lblSite},
                "select kode_gudang, coalesce(deskripsi,'') as nama_gudang from gudang " +
                "where upper(kode_gudang||coalesce(deskripsi,'')) Like upper('%" + txtSite.getText() +"%') order by 2",
                txtSite.getWidth()+lblSite.getWidth(), 300);
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

    private void lblSupplierPropertyChange(java.beans.PropertyChangeEvent evt) {//GEN-FIRST:event_lblSupplierPropertyChange
        // TODO add your handling code here:
}//GEN-LAST:event_lblSupplierPropertyChange

    private void txtSupplierFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtSupplierFocusLost
        // TODO add your handling code here:
}//GEN-LAST:event_txtSupplierFocusLost

    private void txtSupplierKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtSupplierKeyReleased
        fn.lookup(evt, new Object[]{lblSupplier}, "select kode_supplier, coalesce(nama_supplier,'') as nama_supplier, coalesce(telp,'') as telp," +
                "coalesce(top,0) as top " +
                "from phar_supplier where kode_supplier ||coalesce(nama_supplier,'') ||coalesce(telp,'') ilike '%"+txtSupplier.getText()+"%' order by 2",
                txtSupplier.getWidth()+lblSupplier.getWidth(), 200);
}//GEN-LAST:event_txtSupplierKeyReleased

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

    private void txtNoPOFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtNoPOFocusLost
        // TODO add your handling code here:
}//GEN-LAST:event_txtNoPOFocusLost

    private void txtNoPOPropertyChange(java.beans.PropertyChangeEvent evt) {//GEN-FIRST:event_txtNoPOPropertyChange
        // TODO add your handling code here:
}//GEN-LAST:event_txtNoPOPropertyChange

    private void txtNoPOKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtNoPOKeyReleased
        // TODO add your handling code here:
}//GEN-LAST:event_txtNoPOKeyReleased

    private void txtNoPOKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtNoPOKeyTyped
        // TODO add your handling code here:
}//GEN-LAST:event_txtNoPOKeyTyped

    private void txtNoPOVetoableChange(java.beans.PropertyChangeEvent evt)throws java.beans.PropertyVetoException {//GEN-FIRST:event_txtNoPOVetoableChange
        // TODO add your handling code here:
}//GEN-LAST:event_txtNoPOVetoableChange

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

    private void txtConvPropertyChange(java.beans.PropertyChangeEvent evt) {//GEN-FIRST:event_txtConvPropertyChange
        // TODO add your handling code here:
}//GEN-LAST:event_txtConvPropertyChange

    private void txtTotAmountPropertyChange(java.beans.PropertyChangeEvent evt) {//GEN-FIRST:event_txtTotAmountPropertyChange
        // TODO add your handling code here:
    }//GEN-LAST:event_txtTotAmountPropertyChange

    private void txtTotDiscPropertyChange(java.beans.PropertyChangeEvent evt) {//GEN-FIRST:event_txtTotDiscPropertyChange
        // TODO add your handling code here:
    }//GEN-LAST:event_txtTotDiscPropertyChange

    private void txtTotNettoPropertyChange(java.beans.PropertyChangeEvent evt) {//GEN-FIRST:event_txtTotNettoPropertyChange
        // TODO add your handling code here:
    }//GEN-LAST:event_txtTotNettoPropertyChange

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
    private javax.swing.JCheckBox chkConsigment;
    private javax.swing.JComboBox jComboBox1;
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
    private javax.swing.JLabel jLabel23;
    private javax.swing.JLabel jLabel24;
    private javax.swing.JLabel jLabel25;
    private javax.swing.JLabel jLabel26;
    private javax.swing.JLabel jLabel27;
    private javax.swing.JLabel jLabel28;
    private javax.swing.JLabel jLabel29;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel30;
    private javax.swing.JLabel jLabel31;
    private javax.swing.JLabel jLabel33;
    private javax.swing.JLabel jLabel34;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JSeparator jSeparator2;
    private javax.swing.JToolBar jToolBar1;
    private javax.swing.JLabel lblSite;
    private javax.swing.JLabel lblSupplier;
    private javax.swing.JLabel lblUomKecil;
    private javax.swing.JTable tblItem;
    private javax.swing.JLabel txtConv;
    private javax.swing.JTextField txtDate;
    private javax.swing.JTextField txtNoPO;
    private javax.swing.JTextField txtReceiptBy;
    private javax.swing.JTextField txtRemark;
    private javax.swing.JTextField txtSite;
    private javax.swing.JLabel txtStockOnHand;
    private javax.swing.JTextField txtSupplier;
    private javax.swing.JTextField txtTOP;
    private javax.swing.JLabel txtTotAmount;
    private javax.swing.JLabel txtTotDisc;
    private javax.swing.JLabel txtTotNetto;
    // End of variables declaration//GEN-END:variables

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
            Logger.getLogger(FrmPOCash.class.getName()).log(Level.SEVERE, null, ex);
        }

        return fText;
    }

    JTextField ustTextField= new JTextField() {
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

        int col, row;
        JFormattedTextField fText=getFormattedText();
        
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

            if(vColIndex==tblItem.getColumnModel().getColumnIndex("Qty")||
                    vColIndex==tblItem.getColumnModel().getColumnIndex("Price")||
                    vColIndex==tblItem.getColumnModel().getColumnIndex("Disc")){
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
                        JOptionPane.showMessageDialog(JOptionPane.getFrameForComponent(FrmPOCash.this),
                                "Silakan isikan format tanggal dengan 'dd/MM/yyyy'\n" +
                                "Contoh: 31/12/19");

                    }else{
                        retVal = ((JTextField)text).getText();
                    }
                }else
                    retVal = fn.udfGetDouble(((JTextField)text).getText());

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
                    String sQry="select item_code as kode_barang, coalesce(nama_paten,'') as nama_barang " +
                            "from barang pi " +
                            "where pi.item_code='"+sKodeBarang+"' " ;
                            

                    ResultSet rs=conn.createStatement().executeQuery(sQry);
                    if(rs.next()){
                        TableColumnModel col=table.getColumnModel();
                        int iRow=table.getSelectedRow();

                        ((DefaultTableModel)tblItem.getModel()).setValueAt(rs.getString("nama_barang"), iRow, col.getColumnIndex("Keterangan"));
                        ((DefaultTableModel)tblItem.getModel()).setValueAt(new Double(0), iRow, col.getColumnIndex("Qty"));
                        ((DefaultTableModel)tblItem.getModel()).setValueAt(new Double(0), iRow, col.getColumnIndex("Price"));
                        ((DefaultTableModel)tblItem.getModel()).setValueAt(new Double(0), iRow, col.getColumnIndex("Disc"));
                        ((DefaultTableModel)tblItem.getModel()).setValueAt(new Double(0), iRow, col.getColumnIndex("Amount"));
                        ((DefaultTableModel)tblItem.getModel()).setValueAt("", iRow, col.getColumnIndex("Expired"));
                        //udfLoadKetBawah();

                    }
                    rs.close();
                }catch(SQLException se){
                    System.err.println(se.getMessage());
                }
            }else if((mColIndex==table.getColumnModel().getColumnIndex("Qty") && tblItem.getSelectedColumn()==table.getColumnModel().getColumnIndex("Qty"))||
                     (mColIndex==table.getColumnModel().getColumnIndex("Price") && tblItem.getSelectedColumn()==table.getColumnModel().getColumnIndex("Price"))||
                     (mColIndex==table.getColumnModel().getColumnIndex("Disc") && tblItem.getSelectedColumn()==table.getColumnModel().getColumnIndex("Disc"))){
                if(tblItem.getSelectedRow()<0) return;
                double qty=0, disc=0, price=0;

                if(tblItem.getValueAt(tblItem.getSelectedRow(), tblItem.getColumnModel().getColumnIndex("Qty"))!=null)
                    qty=fn.udfGetDouble(tblItem.getValueAt(tblItem.getSelectedRow(), tblItem.getColumnModel().getColumnIndex("Qty")));
                if(tblItem.getValueAt(tblItem.getSelectedRow(), tblItem.getColumnModel().getColumnIndex("Price"))!=null)
                    price=fn.udfGetDouble(tblItem.getValueAt(tblItem.getSelectedRow(), tblItem.getColumnModel().getColumnIndex("Price")));
                if(tblItem.getValueAt(tblItem.getSelectedRow(), tblItem.getColumnModel().getColumnIndex("Disc"))!=null)
                    disc=fn.udfGetDouble(tblItem.getValueAt(tblItem.getSelectedRow(), tblItem.getColumnModel().getColumnIndex("Disc")));

                tblItem.setValueAt((qty*price) , tblItem.getSelectedRow(), tblItem.getColumnModel().getColumnIndex("Amount"));
                double totAmount=0, totDisc=0, totNetto=0;
                
                TableColumnModel col=tblItem.getColumnModel();
                
                for(int i=0; i<tblItem.getRowCount(); i++){
                    price=fn.udfGetDouble(tblItem.getValueAt(i, col.getColumnIndex("Qty")))*
                            (tblItem.getValueAt(i, col.getColumnIndex("Price"))==null?0: fn.udfGetDouble(tblItem.getValueAt(i, col.getColumnIndex("Price"))));
                    disc=price/100* (tblItem.getValueAt(i, tblItem.getColumnModel().getColumnIndex("Disc"))==null? 0: fn.udfGetDouble(tblItem.getValueAt(i, tblItem.getColumnModel().getColumnIndex("Disc"))));
                    totAmount+=price;
                    totDisc+=disc;
                    totNetto+=(price-disc);
                }
                txtTotAmount.setText(fn.dFmt.format(totAmount));
                txtTotDisc.setText(fn.dFmt.format(totDisc));
                txtTotNetto.setText(fn.dFmt.format(totNetto));

            }
            
        }
    }

    private void udfLoadPOCash(){
        if(txtNoPO.getText().length()==0){
//            JOptionPane.showMessageDialog(this, "Silakan iso No. PO Cash yang akan dikoreksi terlebih dulu!");
//            txtNoPO.requestFocus();
            return;
        }

        String s="select po.flag_trx, po.kode_supplier, coalesce(sp.nama_supplier,'') as nama_supplier," +
                "po.site_id, coalesce(g.deskripsi,'') as site_name, coalesce(po.remark,'') as remark," +
                "coalesce(cara_bayar,'0')::int as cara_bayar, coalesce(po.top,0) as top, " +
                "to_char(po.tanggal, 'dd/MM/yyyy') as tgl_po, coalesce(po.user_ins,'') as user_ins, " +
                "po.flag_trx " +
                "from phar_po_cash po " +
                "left join phar_supplier sp on sp.kode_supplier=po.kode_supplier " +
                "left join gudang g on g.kode_gudang=po.site_id " +
                "where no_po_cash='"+txtNoPO.getText()+"'";

        try{
            ResultSet rs=conn.createStatement().executeQuery(s);
            if(rs.next()){
                if(rs.getString("flag_trx").equalsIgnoreCase("K")){
                    JOptionPane.showMessageDialog(this, "PO Cash tersebut sudah pernah dikoreksi.\nSilakan pilih nomor PO yang lain!");
                    udfNew();
                    txtNoPO.requestFocus();
                    rs.close();
                    return;
                }
                txtDate.setText(rs.getString("tgl_po"));
                txtSite.setText(rs.getString("site_id"));
                lblSite.setText(rs.getString("site_name"));
                txtSupplier.setText(rs.getString("kode_supplier"));
                lblSupplier.setText(rs.getString("nama_supplier"));
                txtRemark.setText(rs.getString("remark"));
                txtTOP.setText(rs.getString("top"));
                jComboBox1.setSelectedIndex(rs.getInt("cara_bayar"));
                txtReceiptBy.setText(rs.getString("user_ins"));
                rs.close();

                s=  "select d.kode_barang, coalesce(i.nama_paten,'') as nama_barang, coalesce(jumlah,0) as qty," +
                    "coalesce(d.harga,0) as harga, coalesce(d.discount,0) as disc, " +
                    "coalesce(d.jumlah,0)*coalesce(d.harga,0) - (coalesce(d.jumlah,0)*coalesce(d.harga,0)/100*coalesce(d.discount,0)) as amount," +
                    "to_char(expired_date, 'dd/MM/yy') as exp_date " +
                    "from phar_po_cash_detail d " +
                    "left join barang i on i.item_code=d.kode_barang " +
                    "where d.no_po_cash='"+txtNoPO.getText()+"'";

                ((DefaultTableModel)tblItem.getModel()).setNumRows(0);
                rs=conn.createStatement().executeQuery(s);
                while(rs.next()){
                    ((DefaultTableModel)tblItem.getModel()).addRow(new Object[]{
                        rs.getString("kode_barang"),
                        rs.getString("nama_barang"),
                        rs.getDouble("qty"),
                        rs.getDouble("harga"),
                        rs.getDouble("disc"),
                        rs.getDouble("amount"),
                        rs.getString("exp_date")
                    });
                }

                if(tblItem.getRowCount()>0){
                    tblItem.setRowSelectionInterval(0, 0);
                    udfLoadKetBawah();
                }
                rs.close();
            }
        }catch(SQLException se){
            JOptionPane.showMessageDialog(this, se.getMessage());
        }
    }
    private void udfInitForm(){
        fn=new GeneralFunction(conn);
        fn.addKeyListenerInContainer(jToolBar1, kListener, txtFocusListener);
        fn.addKeyListenerInContainer(jPanel1, kListener, txtFocusListener);
        fn.addKeyListenerInContainer(jPanel2, kListener, txtFocusListener);
        tblItem.addKeyListener(kListener);
        try{
            ResultSet rs=conn.createStatement().executeQuery("select to_char(current_date, 'dd/MM/yyyy')");
            rs.next();
            txtDate.setText(rs.getString(1));
            
            rs.close();
        }catch(SQLException se){
            JOptionPane.showMessageDialog(this, se.getMessage());
        }

        txtSite.setText(MainForm.sKodeGudang);
        lblSite.setText(MainForm.sNamaGudang);

        Runnable doRun = new Runnable() {
            public void run() {
                if(!stKoreksi)
                    txtSupplier.requestFocusInWindow();
                else
                    txtNoPO.requestFocusInWindow();

            }
        };
        SwingUtilities.invokeLater(doRun);
        txtReceiptBy.setText(MainForm.sUserName);

        jLabel16.setText(getTitle());
        udfNew();
    }

    public class MyKeyListener extends KeyAdapter {
        @Override
        public void keyPressed(KeyEvent evt) {
            Component ct = KeyboardFocusManager.getCurrentKeyboardFocusManager().getPermanentFocusOwner();
            int keyKode = evt.getKeyCode();
            switch(keyKode){
                case KeyEvent.VK_INSERT:{
                    DlgLookup d1=new DlgLookup(JOptionPane.getFrameForComponent(FrmPOCash.this), true);
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
                            tbl.getValueAt(iRow, col.getColumnIndex("satuan_kecil")).toString(),
                            0,  //Qty
                            0,  //Harga
                            0,  //Disc
                            0,  //Sub Total
                            ""  //Exp
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

        @Override
        public void keyTyped(KeyEvent evt){
            if(evt.getSource().equals(txtTOP)||(evt.getSource() instanceof JTextField && ((JTextField)evt.getSource()).getName()!=null && ((JTextField)evt.getSource()).getName().equalsIgnoreCase("textEditor") ))
                fn.keyTyped(evt);
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

    private FocusListener txtFocusListener=new FocusListener() {
        public void focusGained(FocusEvent e) {
            if(e.getSource() instanceof JTextField || e.getSource() instanceof JFormattedTextField){
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
            if(e.getSource() instanceof JTextField || e.getSource() instanceof JFormattedTextField || e.getSource() instanceof JComboBox){
                ((JTextField)e.getSource()).setBackground(Color.WHITE);
                if(e.getSource().equals(txtNoPO)){
                    udfLoadPOCash();
                }
            }
        }
    } ;
}
