/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * FrmPO.java
 *
 * Created on Jul 12, 2010, 11:06:18 AM
 */

package pembelian;

import apotek.DLgLookup;
import main.MainForm;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Cursor;
import java.awt.FocusTraversalPolicy;
import java.awt.KeyboardFocusManager;
import java.awt.Toolkit;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.print.PrinterJob;
import java.io.ByteArrayInputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
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
import javax.swing.JComponent;
import javax.swing.JFormattedTextField;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumnModel;
import main.GeneralFunction;
import main.SysConfig;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.util.JRLoader;
import net.sf.jasperreports.view.JasperViewer;
import org.jdesktop.swingx.JXTable;

/**
 *
 * @author cak-ust
 */
public class FrmPO extends javax.swing.JInternalFrame {
    GeneralFunction fn;
    private Connection conn;
    MyKeyListener kListener=new MyKeyListener();
    TableColumnModel col=null;
    private boolean isKoreksi=false;
    private Object srcFrom;
    private int suppLevel=0;
    boolean sudahGR=false;
    private boolean winClosed=false;

    /** Creates new form FrmPO */
    public FrmPO() {
        initComponents();
        col=tblPO.getColumnModel();
        MyTableCellEditor cEditor=new MyTableCellEditor();
        tblPO.getColumnModel().getColumn(col.getColumnIndex("Qty")).setCellEditor(cEditor);
        tblPO.getColumnModel().getColumn(col.getColumnIndex("Disc %")).setCellEditor(cEditor);
        tblPO.getColumnModel().getColumn(col.getColumnIndex("Vat")).setCellEditor(cEditor);
        tblPO.getColumnModel().getColumn(col.getColumnIndex("Unit Price")).setCellEditor(cEditor);
        tblPO.setRowHeight(25);
        tblPO.getColumn("Conv").setMinWidth(0); tblPO.getColumn("Conv").setMaxWidth(0); tblPO.getColumn("Conv").setPreferredWidth(0);
        tblPO.getColumn("UomKecil").setMinWidth(0); tblPO.getColumn("UomKecil").setMaxWidth(0); tblPO.getColumn("UomKecil").setPreferredWidth(0);
        tblPO.getColumn("JmlKecil").setMinWidth(0); tblPO.getColumn("JmlKecil").setMaxWidth(0); tblPO.getColumn("JmlKecil").setPreferredWidth(0);
        tblPO.getColumn("SisaPR").setMinWidth(0); tblPO.getColumn("SisaPR").setMaxWidth(0); tblPO.getColumn("SisaPR").setPreferredWidth(0);
        tblPO.getColumn("Appv. Time").setCellRenderer(new MyRowRenderer());

        tblPO.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            public void valueChanged(ListSelectionEvent e) {
                int iRow=tblPO.getSelectedRow();
                if(iRow<0){
                    txtNoPR.setText("");
                    lblUomKecil.setText("");
                    txtConv.setText("0");
                    txtQtyKecil.setText("0");
                    
                }else{
                    if(iRow>=tblPO.getRowCount()) return;
                    TableColumnModel col=tblPO.getColumnModel();
                    txtNoPR.setText(tblPO.getValueAt(iRow, col.getColumnIndex("PRNo"))==null? "": tblPO.getValueAt(iRow, col.getColumnIndex("PRNo")).toString());
                    lblUomKecil.setText(tblPO.getValueAt(iRow, col.getColumnIndex("UomKecil")).toString());
                    txtConv.setText(tblPO.getValueAt(iRow, col.getColumnIndex("Conv")).toString());
                    txtQtyKecil.setText(tblPO.getValueAt(iRow, col.getColumnIndex("JmlKecil")).toString());
                }
            }
        });

        tblPO.getModel().addTableModelListener(new TableModelListener() {
            public void tableChanged(TableModelEvent e) {
                if( e.getColumn()==col.getColumnIndex("Qty")||e.getColumn()==col.getColumnIndex("Disc %")||
                    e.getColumn()==col.getColumnIndex("Unit Price")||e.getColumn()==col.getColumnIndex("Vat")){
                    int iRow=tblPO.getSelectedRow();
                    double jmlKecil=fn.udfGetDouble(tblPO.getValueAt(iRow, col.getColumnIndex("Qty")))*
                            fn.udfGetDouble(tblPO.getValueAt(iRow, col.getColumnIndex("Conv")));
                    double extPrice=fn.udfGetDouble(tblPO.getValueAt(iRow, col.getColumnIndex("Qty")))*
                            fn.udfGetDouble(tblPO.getValueAt(iRow, col.getColumnIndex("Unit Price")));
                    extPrice=extPrice-(extPrice/100)* fn.udfGetDouble(tblPO.getValueAt(iRow, col.getColumnIndex("Disc %")));
                    
                    ((DefaultTableModel)tblPO.getModel()).setValueAt(Math.floor(extPrice),
                            iRow, col.getColumnIndex("Ext. Price"));
                    ((DefaultTableModel)tblPO.getModel()).setValueAt(jmlKecil, iRow, col.getColumnIndex("JmlKecil"));
                    
                }
                if(tblPO.getRowCount()>0){
                    double totLine=0, totVat=0;
                    TableColumnModel col=tblPO.getColumnModel();

                    for(int i=0; i< tblPO.getRowCount(); i++){
                        if(e.getType()==TableModelEvent.DELETE && !sudahGR)
                            ((DefaultTableModel)tblPO.getModel()).setValueAt(i+1, i, col.getColumnIndex("No."));

                        totLine+=fn.udfGetDouble(tblPO.getValueAt(i, col.getColumnIndex("Ext. Price")));
                        totVat+=fn.udfGetDouble(tblPO.getValueAt(i, col.getColumnIndex("Ext. Price")))/100*fn.udfGetDouble(tblPO.getValueAt(i, col.getColumnIndex("Vat")));
                    }
                    
                    txtTotalLine.setText(fn.dFmt.format(Math.floor(totLine)));
                    txtTotVat.setText(fn.dFmt.format(Math.floor(totVat)));
                    txtNetto.setText(fn.dFmt.format(Math.floor(totLine+totVat-fn.udfGetDouble(txtDiscRp.getText()))));

                }else{
                    txtTotalLine.setText("0");
                    txtTotVat.setText("0");
                    txtDiscRp.setText("0");
                    txtDiscPersen.setText("0");
                    txtNetto.setText("0");
                }
            }
        });

        tblPO.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).
        put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0),
            "selectNextColumnCell");
    }

    public void setConn(Connection con){
        this.conn=con;
    }

    private void udfInitForm(){
        if(isKoreksi) jLabel16.setText("Revisi PO");
        fn=new GeneralFunction(conn);
        fn.addKeyListenerInContainer(jToolBar1, kListener, txtFocusListener);
        fn.addKeyListenerInContainer(jPanel1, kListener, txtFocusListener);
        fn.addKeyListenerInContainer(jPanel2, kListener, txtFocusListener);
        
        tblPO.addKeyListener(kListener);
        try{
            ResultSet rs=conn.createStatement().executeQuery("select to_char(current_date, 'dd/MM/yyyy')");
            rs.next();
            txtDate.setText(rs.getString(1));
            setDueDate();

            rs.close();
        }catch(SQLException se){
            JOptionPane.showMessageDialog(this, se.getMessage());
        }
        if(!isKoreksi)
            udfNew();

        Runnable doRun = new Runnable() {
            public void run() {
                if(!isKoreksi)
                    if(txtSupplier.getText().length()>0)
                        tblPO.requestFocusInWindow();
                    else
                        txtSupplier.requestFocusInWindow();
                else{
                  txtNoPO.requestFocusInWindow();
                  btnNew.setEnabled(false);
                  btnSave.setEnabled(true);
                }
            }
        };
        SwingUtilities.invokeLater(doRun);

    }

    private Date getDueDate(Date d, int i){
        Date dueDate = Calendar.getInstance().getTime();
        Calendar c = Calendar.getInstance();
        c.setTime(d);
        c.add(Calendar.DAY_OF_MONTH, i);

        return c.getTime();
    }

    private void udfNew(){
        btnNew.setEnabled(false);
        btnSave.setEnabled(true);
        chkConsignment.setSelected(false);
        btnCancel.setText("Cancel");
//        btnCancel.setIcon(new javax.swing.ImageIcon(getClass().getResource("/pharpurchase/image/Icon/Cancel.png")));
        //chkCito.setSelected(true);
        if(!isKoreksi) txtNoPO.setText("");
        txtNoPO.setEnabled(isKoreksi);
        txtSupplier.setText(""); lblSupplier.setText(""); lblAlamatSupp.setText("");
        txtShipping.setText("");
        txtRemark.setText("");
        txtTOP.setText("");
        suppLevel=0;
        ((DefaultTableModel)tblPO.getModel()).setNumRows(0);
        txtSupplier.requestFocus();

    }

    public void udfCreatePOFromPRMaintenance(FrmPRMaintenance srcForm, String sItem, String sSuppID, int priority, boolean cito){
        this.srcFrom=srcForm;
        udfInitForm();
        txtSupplier.setText(sSuppID);
        chkCito.setSelected(cito);

        try{
            ResultSet rs=conn.createStatement().executeQuery("select nama_supplier, coalesce(top,0) as top," +
                    "to_char(current_date, 'dd/MM/yyyy') as skg, "
                    + "coalesce(alamat,'') as alamat, coalesce(telp,'') as telp " +
                    "from phar_supplier where kode_supplier='"+sSuppID+"'");
            if(rs.next()){
                txtDate.setText(rs.getString("skg"));
                lblSupplier.setText(rs.getString("nama_supplier"));
                txtTOP.setText(rs.getString("top"));
                lblAlamatSupp.setText(rs.getString("alamat"));
                lblTelepon.setText(rs.getString("telp"));
            }
            setDueDate();
            rs.close();

            suppLevel=priority;

            if(priority==1){
                udfLoadItemFromPR();
                return;
            }
            String s="select fn_phar_po_load_item_from_pr_supp.* " +
                            "from fn_phar_po_load_item_from_pr_supp(" +
                            "'"+txtSupplier.getText()+"', " +
                            ""+chkConsignment.isSelected()+", " +
                            ""+chkCito.isSelected()+" ) as (no_pr varchar, kode_barang varchar, nama_barang varchar, qty double precision, uom_kecil varchar, " +
                            "conv numeric, uom_alt varchar, price real, disc real, vat real, requested_by varchar, time_appv timestamp without time zone) " +
                            "order by bp_type, 2"; // +
                            //"where  kode_barang '"+sItem+"'";
            
            rs=conn.createStatement().executeQuery(s);
            while(rs.next()){
                ((DefaultTableModel)tblPO.getModel()).addRow(new Object[]{
                    tblPO.getRowCount()+1,
                    rs.getString("no_pr"),
                    rs.getString("kode_barang"),
                    rs.getString("nama_barang"),
                    rs.getString("uom_alt"),
                    rs.getDouble("qty")/rs.getDouble("conv"),
                    rs.getDouble("price"),
                    rs.getDouble("disc"),
                    rs.getDouble("vat"),
                    Math.floor((rs.getDouble("qty") / rs.getDouble("conv")*rs.getDouble("price"))-
                              ((rs.getDouble("qty") / rs.getDouble("conv")*rs.getDouble("price")/100*rs.getDouble("disc")))),
                    rs.getDouble("conv"),
                    rs.getString("uom_kecil"),
                    rs.getDouble("qty"),
                    rs.getDouble("qty")/ rs.getDouble("conv"),
                    rs.getString("requested_by"),
                    rs.getTimestamp("time_appv"),
                    rs.getString("bp_type")
                });
            }

            tblPO.setModel((DefaultTableModel)fn.autoResizeColWidth(tblPO, (DefaultTableModel)tblPO.getModel()).getModel());
            rs.close();
        }catch(SQLException se){
            JOptionPane.showMessageDialog(this, se.getMessage());
        }
    }

    private void udfLoadPOKoreksi(){
        if(txtNoPO.getText().isEmpty()) return;
        String s ="select x.*, pr.requested_by, pr.acc_time1 as time_appv  "
                + "from(" +
                "select * from fn_phar_po_print2('"+txtNoPO.getText()+"') as (consigment boolean, cito boolean, " +
                "top int, curr varchar, kurs numeric, disc_po double precision, " +
                "tanggal text, jam text, kode_supplier varchar, nama_supplier varchar , alamat varchar, nama_kota varchar, " +
                "telp_supp varchar, shiping varchar, remark varchar, buyer varchar, no_po varchar, " +
                "kode_barang varchar, nama_barang varchar, " +
                "uom_alt varchar, qty numeric, price numeric, disc double precision, vat real, ext_price double precision, " +
                "jml_kecil double precision, uom_kecil varchar, konv real, no_pr varchar, urut int, ppn double precision) " +
                ") x " +
                "inner join barang i on i.item_code=x.kode_barang " +
                "left join phar_pr pr on pr.no_pr=x.no_pr " +
                "order by x.urut";

        //System.out.println(s);
        try{
            udfNew();
            ResultSet rs=conn.createStatement().executeQuery("select upper(flag_trx) as flag_trx " +
                    "from phar_po where no_po='"+txtNoPO.getText()+"'");
            if(rs.next()){
                if(rs.getString("flag_trx").equalsIgnoreCase("K")){
                    rs.close();
                    JOptionPane.showMessageDialog(this, "Nomor PO Tersebut sudah pernah dikoreksi. Silakan masukkan nomor PO Lain!");
                    udfNew();
                    return;
                }
            }
            rs = conn.createStatement().executeQuery(s);
            boolean found=false;
            while(rs.next()){
                if(((DefaultTableModel)tblPO.getModel()).getRowCount()==0){
                    found=true;
                    chkConsignment.setSelected(rs.getBoolean("consigment"));
                    chkCito.setSelected(rs.getBoolean("cito"));
                    txtSupplier.setText(rs.getString("kode_supplier"));
                    lblSupplier.setText(rs.getString("nama_supplier"));
                    lblAlamatSupp.setText(rs.getString("telp_supp"));
                    txtShipping.setText(rs.getString("shiping"));
                    txtRemark.setText(rs.getString("remark"));
                    txtDate.setText(rs.getString("tanggal"));
                    txtTOP.setText(rs.getString("top"));
                }
                ((DefaultTableModel)tblPO.getModel()).addRow(new Object[]{
                    rs.getInt("urut"),
                    rs.getString("no_pr"),
                    rs.getString("kode_barang"),
                    rs.getString("nama_barang"),
                    rs.getString("uom_alt"),
                    rs.getInt("qty"),
                    rs.getDouble("price"),
                    rs.getDouble("disc"),
                    rs.getDouble("vat"),
                    Math.floor(rs.getDouble("ext_price")),
                    rs.getDouble("konv"),
                    rs.getString("uom_kecil"),
                    rs.getDouble("jml_kecil"),
                    rs.getDouble("qty")/ rs.getDouble("konv"),
                    rs.getString("requested_by"),
                    rs.getTimestamp("time_appv"),
                    //rs.getString("item_type")
                });
            }

            if(!found){
                JOptionPane.showMessageDialog(this, "No. PO tidak ditemukan!");
                txtNoPO.setText("");
                txtNoPO.requestFocus();
                return;
            }else{
                setDueDate();
                if(tblPO.getRowCount()>0)
                    tblPO.setRowSelectionInterval(0, 0);

                rs=conn.createStatement().executeQuery("select * from phar_good_receipt where no_po='"+txtNoPO.getText()+"'");
                sudahGR=rs.next();

                rs.close();
            }
            tblPO.setModel((DefaultTableModel)fn.autoResizeColWidth(tblPO, (DefaultTableModel)tblPO.getModel()).getModel());
        }catch(SQLException se){
            JOptionPane.showMessageDialog(this, se.getMessage());
        }
    }

    private void udfLoadItemFromPR(){
        setDueDate();
        try{
            ((DefaultTableModel)tblPO.getModel()).setNumRows(0);
            if(txtSupplier.getText().trim().length()==0) return;

            String s="select x.* " +
                    "from (select * from fn_phar_po_load_item_from_pr('"+txtSupplier.getText()+"', "+chkCito.isSelected()+") " +
                    "as (no_pr varchar, kode_barang varchar, \n" +
                    "nama_barang varchar, qty double precision, uom_kecil varchar, conv numeric, uom_alt varchar, price double precision, disc double precision, \n" +
                    "vat double precision,requested_by varchar,  time_appv timestamp without time zone)) as x order by 3";
            System.out.println(s);
            ResultSet rs=conn.createStatement().executeQuery(s);
            
            while(rs.next()){
                ((DefaultTableModel)tblPO.getModel()).addRow(new Object[]{
                    tblPO.getRowCount()+1,
                    rs.getString("no_pr"),
                    rs.getString("kode_barang"),
                    rs.getString("nama_barang"),
                    rs.getString("uom_alt"),
                    rs.getDouble("qty")/ rs.getDouble("conv"),
                    rs.getDouble("price"),
                    rs.getDouble("disc"),
                    rs.getDouble("vat"),
                    Math.floor((rs.getDouble("qty")/ rs.getDouble("conv"))*rs.getDouble("price")-
                        ((rs.getDouble("qty")/ rs.getDouble("conv"))*rs.getDouble("price")/100*rs.getDouble("disc"))),
                    rs.getDouble("conv"),
                    rs.getString("uom_kecil"),
                    rs.getDouble("qty"),
                    rs.getDouble("qty")/ rs.getDouble("conv"),
                    rs.getString("requested_by"),
                    rs.getTimestamp("time_appv"),
                });
            }
            if(tblPO.getRowCount()>0)
                tblPO.setRowSelectionInterval(0, 0);
            
            tblPO.setModel((DefaultTableModel)fn.autoResizeColWidth(tblPO, (DefaultTableModel)tblPO.getModel()).getModel());
        }catch(SQLException se){
            JOptionPane.showMessageDialog(this, se.getMessage());
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

        jToolBar1 = new javax.swing.JToolBar();
        btnNew = new javax.swing.JButton();
        btnSave = new javax.swing.JButton();
        btnPrint = new javax.swing.JButton();
        btnCancel = new javax.swing.JButton();
        jPanel1 = new javax.swing.JPanel();
        jLabel3 = new javax.swing.JLabel();
        txtDate = new javax.swing.JTextField();
        jLabel17 = new javax.swing.JLabel();
        jLabel18 = new javax.swing.JLabel();
        txtSupplier = new javax.swing.JTextField();
        lblAlamatSupp = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        txtShipping = new javax.swing.JTextField();
        jLabel7 = new javax.swing.JLabel();
        txtTOP = new javax.swing.JTextField();
        jLabel8 = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        txtDueDate = new javax.swing.JTextField();
        chkCito = new javax.swing.JCheckBox();
        lblSupplier = new javax.swing.JLabel();
        jLabel13 = new javax.swing.JLabel();
        jLabel15 = new javax.swing.JLabel();
        jLabel19 = new javax.swing.JLabel();
        txtNoPO = new javax.swing.JTextField();
        lblTelepon = new javax.swing.JLabel();
        jLabel16 = new javax.swing.JLabel();
        chkConsignment = new javax.swing.JCheckBox();
        jPanel2 = new javax.swing.JPanel();
        txtTotalLine = new javax.swing.JLabel();
        jLabel23 = new javax.swing.JLabel();
        jLabel26 = new javax.swing.JLabel();
        jLabel27 = new javax.swing.JLabel();
        txtTotVat = new javax.swing.JLabel();
        jLabel28 = new javax.swing.JLabel();
        txtNetto = new javax.swing.JLabel();
        txtDiscRp = new javax.swing.JTextField();
        txtDiscPersen = new javax.swing.JTextField();
        jLabel1 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        txtRemark = new javax.swing.JTextField();
        jPanel3 = new javax.swing.JPanel();
        jLabel22 = new javax.swing.JLabel();
        txtNoPR = new javax.swing.JLabel();
        jLabel24 = new javax.swing.JLabel();
        txtConv = new javax.swing.JLabel();
        lblUomKecil = new javax.swing.JLabel();
        txtQtyKecil = new javax.swing.JLabel();
        jLabel25 = new javax.swing.JLabel();
        jSeparator2 = new javax.swing.JSeparator();
        jScrollPane1 = new javax.swing.JScrollPane();
        tblPO = new javax.swing.JTable();

        setClosable(true);
        setIconifiable(true);
        setMaximizable(true);
        setResizable(true);
        setTitle("Purchase Order Manual");
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

        jToolBar1.setRollover(true);

        btnNew.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/Icon/32/add.png"))); // NOI18N
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

        btnPrint.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/Icon/buttons/print-32.png"))); // NOI18N
        btnPrint.setText("Print");
        btnPrint.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnPrint.setMargin(new java.awt.Insets(2, 2, 2, 2));
        btnPrint.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnPrint.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnPrintActionPerformed(evt);
            }
        });
        jToolBar1.add(btnPrint);

        btnCancel.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/Icon/32/close.png"))); // NOI18N
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

        jPanel1.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        jPanel1.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel3.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jLabel3.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel3.setText("Tanggal");
        jPanel1.add(jLabel3, new org.netbeans.lib.awtextra.AbsoluteConstraints(540, 10, 70, 20));

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
        jPanel1.add(txtDate, new org.netbeans.lib.awtextra.AbsoluteConstraints(620, 10, 120, 20));

        jLabel17.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jLabel17.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel17.setText("PO #");
        jPanel1.add(jLabel17, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 10, 90, 20));

        jLabel18.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jLabel18.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel18.setText("Supplier ID");
        jPanel1.add(jLabel18, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 30, 90, 20));

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
        jPanel1.add(txtSupplier, new org.netbeans.lib.awtextra.AbsoluteConstraints(100, 30, 70, 20));

        lblAlamatSupp.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
        lblAlamatSupp.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 1, true));
        lblAlamatSupp.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                lblAlamatSuppPropertyChange(evt);
            }
        });
        jPanel1.add(lblAlamatSupp, new org.netbeans.lib.awtextra.AbsoluteConstraints(100, 50, 260, 20));

        jLabel4.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jLabel4.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel4.setText("Shiping");
        jPanel1.add(jLabel4, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 75, 90, 20));

        txtShipping.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
        txtShipping.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 1, true));
        txtShipping.setDisabledTextColor(new java.awt.Color(0, 0, 0));
        txtShipping.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                txtShippingFocusLost(evt);
            }
        });
        txtShipping.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtShippingKeyReleased(evt);
            }
        });
        jPanel1.add(txtShipping, new org.netbeans.lib.awtextra.AbsoluteConstraints(100, 75, 250, 20));

        jLabel7.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jLabel7.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel7.setText("Days");
        jPanel1.add(jLabel7, new org.netbeans.lib.awtextra.AbsoluteConstraints(670, 50, 50, 20));

        txtTOP.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
        txtTOP.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 1, true));
        txtTOP.setDisabledTextColor(new java.awt.Color(0, 0, 0));
        txtTOP.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                txtTOPFocusLost(evt);
            }
        });
        txtTOP.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                txtTOPPropertyChange(evt);
            }
        });
        txtTOP.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtTOPKeyReleased(evt);
            }
            public void keyTyped(java.awt.event.KeyEvent evt) {
                txtTOPKeyTyped(evt);
            }
        });
        txtTOP.addVetoableChangeListener(new java.beans.VetoableChangeListener() {
            public void vetoableChange(java.beans.PropertyChangeEvent evt)throws java.beans.PropertyVetoException {
                txtTOPVetoableChange(evt);
            }
        });
        jPanel1.add(txtTOP, new org.netbeans.lib.awtextra.AbsoluteConstraints(620, 50, 50, 20));

        jLabel8.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jLabel8.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel8.setText("T.O.P");
        jPanel1.add(jLabel8, new org.netbeans.lib.awtextra.AbsoluteConstraints(540, 50, 70, 20));

        jLabel9.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jLabel9.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel9.setText("Jth. Tempo");
        jPanel1.add(jLabel9, new org.netbeans.lib.awtextra.AbsoluteConstraints(540, 70, 70, 20));

        txtDueDate.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
        txtDueDate.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 1, true));
        txtDueDate.setDisabledTextColor(new java.awt.Color(0, 0, 0));
        txtDueDate.setEnabled(false);
        txtDueDate.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                txtDueDateFocusLost(evt);
            }
        });
        txtDueDate.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtDueDateKeyReleased(evt);
            }
        });
        jPanel1.add(txtDueDate, new org.netbeans.lib.awtextra.AbsoluteConstraints(620, 70, 120, 20));

        chkCito.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        chkCito.setForeground(new java.awt.Color(255, 0, 0));
        chkCito.setText(" C I T O");
        chkCito.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                chkCitoItemStateChanged(evt);
            }
        });
        chkCito.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chkCitoActionPerformed(evt);
            }
        });
        jPanel1.add(chkCito, new org.netbeans.lib.awtextra.AbsoluteConstraints(370, 10, 130, 20));

        lblSupplier.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
        lblSupplier.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 1, true));
        lblSupplier.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                lblSupplierPropertyChange(evt);
            }
        });
        jPanel1.add(lblSupplier, new org.netbeans.lib.awtextra.AbsoluteConstraints(170, 30, 350, 20));

        jLabel13.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jLabel13.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel13.setText(":");
        jPanel1.add(jLabel13, new org.netbeans.lib.awtextra.AbsoluteConstraints(610, 10, 10, 20));

        jLabel15.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jLabel15.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel15.setText(":");
        jPanel1.add(jLabel15, new org.netbeans.lib.awtextra.AbsoluteConstraints(610, 50, 10, 20));

        jLabel19.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jLabel19.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel19.setText(":");
        jPanel1.add(jLabel19, new org.netbeans.lib.awtextra.AbsoluteConstraints(610, 70, 10, 20));

        txtNoPO.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
        txtNoPO.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 1, true));
        txtNoPO.setDisabledTextColor(new java.awt.Color(0, 0, 0));
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
        jPanel1.add(txtNoPO, new org.netbeans.lib.awtextra.AbsoluteConstraints(100, 10, 130, 20));

        lblTelepon.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
        lblTelepon.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 1, true));
        lblTelepon.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                lblTeleponPropertyChange(evt);
            }
        });
        jPanel1.add(lblTelepon, new org.netbeans.lib.awtextra.AbsoluteConstraints(360, 50, 160, 20));

        jLabel16.setFont(new java.awt.Font("Tahoma", 0, 36)); // NOI18N
        jLabel16.setForeground(new java.awt.Color(0, 0, 153));
        jLabel16.setText("Purchase Order ");

        chkConsignment.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        chkConsignment.setText(" PO Consignment");
        chkConsignment.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                chkConsignmentItemStateChanged(evt);
            }
        });

        jPanel2.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        jPanel2.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        txtTotalLine.setFont(new java.awt.Font("Dialog", 1, 12)); // NOI18N
        txtTotalLine.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        txtTotalLine.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 1, true));
        txtTotalLine.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                txtTotalLinePropertyChange(evt);
            }
        });
        jPanel2.add(txtTotalLine, new org.netbeans.lib.awtextra.AbsoluteConstraints(600, 5, 120, 20));

        jLabel23.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jLabel23.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel23.setText("Line Total :");
        jPanel2.add(jLabel23, new org.netbeans.lib.awtextra.AbsoluteConstraints(510, 5, 90, 20));

        jLabel26.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jLabel26.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel26.setText("Discount");
        jPanel2.add(jLabel26, new org.netbeans.lib.awtextra.AbsoluteConstraints(480, 30, 80, 20));

        jLabel27.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jLabel27.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel27.setText("V.A.T");
        jPanel2.add(jLabel27, new org.netbeans.lib.awtextra.AbsoluteConstraints(510, 55, 90, 20));

        txtTotVat.setFont(new java.awt.Font("Dialog", 1, 12)); // NOI18N
        txtTotVat.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        txtTotVat.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 1, true));
        txtTotVat.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                txtTotVatPropertyChange(evt);
            }
        });
        jPanel2.add(txtTotVat, new org.netbeans.lib.awtextra.AbsoluteConstraints(600, 55, 120, 20));

        jLabel28.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jLabel28.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel28.setText("Netto");
        jPanel2.add(jLabel28, new org.netbeans.lib.awtextra.AbsoluteConstraints(510, 80, 90, 20));

        txtNetto.setFont(new java.awt.Font("Dialog", 1, 12)); // NOI18N
        txtNetto.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        txtNetto.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 1, true));
        txtNetto.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                txtNettoPropertyChange(evt);
            }
        });
        jPanel2.add(txtNetto, new org.netbeans.lib.awtextra.AbsoluteConstraints(600, 80, 120, 20));

        txtDiscRp.setFont(new java.awt.Font("Dialog", 1, 12)); // NOI18N
        txtDiscRp.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtDiscRp.setText("0");
        txtDiscRp.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 1, true));
        txtDiscRp.setDisabledTextColor(new java.awt.Color(0, 0, 0));
        txtDiscRp.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                txtDiscRpKeyTyped(evt);
            }
        });
        jPanel2.add(txtDiscRp, new org.netbeans.lib.awtextra.AbsoluteConstraints(600, 30, 120, 20));

        txtDiscPersen.setFont(new java.awt.Font("Dialog", 1, 12)); // NOI18N
        txtDiscPersen.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        txtDiscPersen.setText("0");
        txtDiscPersen.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 1, true));
        txtDiscPersen.setDisabledTextColor(new java.awt.Color(0, 0, 0));
        txtDiscPersen.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                txtDiscPersenKeyTyped(evt);
            }
        });
        jPanel2.add(txtDiscPersen, new org.netbeans.lib.awtextra.AbsoluteConstraints(560, 30, 40, 20));

        jLabel1.setBackground(new java.awt.Color(204, 255, 255));
        jLabel1.setForeground(new java.awt.Color(0, 0, 153));
        jLabel1.setText("<html>\n &nbsp <b>F5  &nbsp &nbsp    : </b> Membuat PO baru <br> \n &nbsp <b>F2 &nbsp &nbsp : </b>  Menyimpan PO <br>\n &nbsp <b>Insert : </b> Menambah Item PR\n</html>");
        jLabel1.setVerticalAlignment(javax.swing.SwingConstants.TOP);
        jLabel1.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        jLabel1.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jLabel1.setOpaque(true);
        jLabel1.setVerticalTextPosition(javax.swing.SwingConstants.TOP);
        jPanel2.add(jLabel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 40, 290, 60));

        jLabel5.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jLabel5.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel5.setText("Remark");
        jPanel2.add(jLabel5, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 10, 90, 20));

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
        jPanel2.add(txtRemark, new org.netbeans.lib.awtextra.AbsoluteConstraints(100, 10, 310, 20));

        jPanel3.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        jPanel3.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel22.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jLabel22.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel22.setText("PR No. :");
        jPanel3.add(jLabel22, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 5, 70, 20));

        txtNoPR.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
        txtNoPR.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 1, true));
        txtNoPR.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                txtNoPRPropertyChange(evt);
            }
        });
        jPanel3.add(txtNoPR, new org.netbeans.lib.awtextra.AbsoluteConstraints(80, 5, 120, 20));

        jLabel24.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jLabel24.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel24.setText("Conv : ");
        jPanel3.add(jLabel24, new org.netbeans.lib.awtextra.AbsoluteConstraints(320, 5, 50, 20));

        txtConv.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
        txtConv.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        txtConv.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 1, true));
        txtConv.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                txtConvPropertyChange(evt);
            }
        });
        jPanel3.add(txtConv, new org.netbeans.lib.awtextra.AbsoluteConstraints(370, 5, 70, 20));

        lblUomKecil.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        lblUomKecil.setForeground(new java.awt.Color(0, 0, 153));
        lblUomKecil.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblUomKecil.setText("Uom");
        jPanel3.add(lblUomKecil, new org.netbeans.lib.awtextra.AbsoluteConstraints(440, 5, 60, 20));

        txtQtyKecil.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
        txtQtyKecil.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        txtQtyKecil.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 1, true));
        txtQtyKecil.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                txtQtyKecilPropertyChange(evt);
            }
        });
        jPanel3.add(txtQtyKecil, new org.netbeans.lib.awtextra.AbsoluteConstraints(600, 5, 90, 20));

        jLabel25.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jLabel25.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel25.setText("Qty :");
        jPanel3.add(jLabel25, new org.netbeans.lib.awtextra.AbsoluteConstraints(540, 5, 60, 20));

        jSeparator2.setOrientation(javax.swing.SwingConstants.VERTICAL);
        jPanel3.add(jSeparator2, new org.netbeans.lib.awtextra.AbsoluteConstraints(300, 0, 10, 30));

        tblPO.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        tblPO.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "No.", "PRNo", "Product ID", "Keterangan", "UOM", "Qty", "Unit Price", "Disc %", "Vat", "Ext. Price", "Conv", "UomKecil", "JmlKecil", "SisaPR", "Request By", "Appv. Time", "Group"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Double.class, java.lang.Double.class, java.lang.Double.class, java.lang.Double.class, java.lang.Double.class, java.lang.Integer.class, java.lang.Object.class, java.lang.Double.class, java.lang.Double.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, true, true, true, true, false, false, false, false, false, false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        tblPO.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_OFF);
        tblPO.getTableHeader().setReorderingAllowed(false);
        jScrollPane1.setViewportView(tblPO);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(10, 10, 10)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel16, javax.swing.GroupLayout.PREFERRED_SIZE, 400, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(10, 10, 10)
                        .addComponent(chkConsignment, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jToolBar1, javax.swing.GroupLayout.PREFERRED_SIZE, 194, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, 765, Short.MAX_VALUE)
                            .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 766, Short.MAX_VALUE)
                            .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, 766, Short.MAX_VALUE)
                            .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, 766, Short.MAX_VALUE))
                        .addGap(9, 9, 9))))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(21, 21, 21)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel16, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(30, 30, 30)
                        .addComponent(chkConsignment, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jToolBar1, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(4, 4, 4)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 192, Short.MAX_VALUE)
                .addGap(2, 2, 2)
                .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(6, 6, 6)
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, 110, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(11, 11, 11))
        );

        setBounds(0, 0, 795, 593);
    }// </editor-fold>//GEN-END:initComponents

    private void btnNewActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnNewActionPerformed
        udfNew();
}//GEN-LAST:event_btnNewActionPerformed

    private void btnSaveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSaveActionPerformed
        udfSave();
}//GEN-LAST:event_btnSaveActionPerformed

    private void btnCancelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCancelActionPerformed
        if(btnCancel.getText().equalsIgnoreCase("cancel")){
            if(getTitle().indexOf("Revision")>0) dispose();
            btnSave.setEnabled(false);
            btnNew.setEnabled(true);
            btnCancel.setText("Exit");
            btnCancel.setIcon(new javax.swing.ImageIcon(getClass().getResource("/pharpurchase/image/Icon/Exit.png")));
        }else{
            this.dispose();
        }
}//GEN-LAST:event_btnCancelActionPerformed

    private void txtDateFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtDateFocusLost
        // TODO add your handling code here:
}//GEN-LAST:event_txtDateFocusLost

    private void txtDateKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtDateKeyReleased
        // TODO add your handling code here:
}//GEN-LAST:event_txtDateKeyReleased

    private void chkConsignmentItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_chkConsignmentItemStateChanged
        
}//GEN-LAST:event_chkConsignmentItemStateChanged

    private void txtSupplierFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtSupplierFocusLost
        // TODO add your handling code here:
}//GEN-LAST:event_txtSupplierFocusLost

    private void txtSupplierKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtSupplierKeyReleased
        fn.lookup(evt, new Object[]{lblSupplier, lblAlamatSupp, lblTelepon, txtTOP}, "select kode_supplier, coalesce(nama_supplier,'') as nama_supplier, " +
                "coalesce(alamat,'') as alamat, coalesce(telp,'') as telp," +
                "coalesce(top,0) as top " +
                "from phar_supplier where active=true and  kode_supplier ||coalesce(nama_supplier,'') ||coalesce(telp,'') ilike '%"+txtSupplier.getText()+"%' order by 2",
                txtSupplier.getWidth()+lblSupplier.getWidth(), 200);
}//GEN-LAST:event_txtSupplierKeyReleased

    private void lblAlamatSuppPropertyChange(java.beans.PropertyChangeEvent evt) {//GEN-FIRST:event_lblAlamatSuppPropertyChange
        // TODO add your handling code here:
}//GEN-LAST:event_lblAlamatSuppPropertyChange

    private void txtShippingFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtShippingFocusLost
        // TODO add your handling code here:
    }//GEN-LAST:event_txtShippingFocusLost

    private void txtShippingKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtShippingKeyReleased
        // TODO add your handling code here:
    }//GEN-LAST:event_txtShippingKeyReleased

    private void txtRemarkFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtRemarkFocusLost
        // TODO add your handling code here:
    }//GEN-LAST:event_txtRemarkFocusLost

    private void txtRemarkKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtRemarkKeyReleased
        // TODO add your handling code here:
    }//GEN-LAST:event_txtRemarkKeyReleased

    private void txtTOPFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtTOPFocusLost
        // TODO add your handling code here:
    }//GEN-LAST:event_txtTOPFocusLost

    private void txtTOPKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtTOPKeyReleased
        // TODO add your handling code here:
    }//GEN-LAST:event_txtTOPKeyReleased

    private void txtDueDateFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtDueDateFocusLost
        // TODO add your handling code here:
    }//GEN-LAST:event_txtDueDateFocusLost

    private void txtDueDateKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtDueDateKeyReleased
        // TODO add your handling code here:
    }//GEN-LAST:event_txtDueDateKeyReleased

    private void chkCitoItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_chkCitoItemStateChanged
        udfLoadItemFromPR();
    }//GEN-LAST:event_chkCitoItemStateChanged

    private void chkCitoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chkCitoActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_chkCitoActionPerformed

    private void lblSupplierPropertyChange(java.beans.PropertyChangeEvent evt) {//GEN-FIRST:event_lblSupplierPropertyChange
        // TODO add your handling code here:
    }//GEN-LAST:event_lblSupplierPropertyChange

    private void formInternalFrameOpened(javax.swing.event.InternalFrameEvent evt) {//GEN-FIRST:event_formInternalFrameOpened
        if(txtSupplier.getText().isEmpty())
            udfInitForm();
    }//GEN-LAST:event_formInternalFrameOpened

    private void txtTOPKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtTOPKeyTyped
        fn.keyTyped(evt);
    }//GEN-LAST:event_txtTOPKeyTyped

    private void txtTotalLinePropertyChange(java.beans.PropertyChangeEvent evt) {//GEN-FIRST:event_txtTotalLinePropertyChange
        // TODO add your handling code here:
    }//GEN-LAST:event_txtTotalLinePropertyChange

    private void txtNoPRPropertyChange(java.beans.PropertyChangeEvent evt) {//GEN-FIRST:event_txtNoPRPropertyChange
        // TODO add your handling code here:
    }//GEN-LAST:event_txtNoPRPropertyChange

    private void txtConvPropertyChange(java.beans.PropertyChangeEvent evt) {//GEN-FIRST:event_txtConvPropertyChange
        // TODO add your handling code here:
    }//GEN-LAST:event_txtConvPropertyChange

    private void txtQtyKecilPropertyChange(java.beans.PropertyChangeEvent evt) {//GEN-FIRST:event_txtQtyKecilPropertyChange
        // TODO add your handling code here:
    }//GEN-LAST:event_txtQtyKecilPropertyChange

    private void txtTotVatPropertyChange(java.beans.PropertyChangeEvent evt) {//GEN-FIRST:event_txtTotVatPropertyChange
        // TODO add your handling code here:
    }//GEN-LAST:event_txtTotVatPropertyChange

    private void txtNettoPropertyChange(java.beans.PropertyChangeEvent evt) {//GEN-FIRST:event_txtNettoPropertyChange
        // TODO add your handling code here:
    }//GEN-LAST:event_txtNettoPropertyChange

    private void txtDiscRpKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtDiscRpKeyTyped
        fn.keyTyped(evt);
    }//GEN-LAST:event_txtDiscRpKeyTyped

    private void txtDiscPersenKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtDiscPersenKeyTyped
        fn.keyTyped(evt);
    }//GEN-LAST:event_txtDiscPersenKeyTyped

    private void txtTOPPropertyChange(java.beans.PropertyChangeEvent evt) {//GEN-FIRST:event_txtTOPPropertyChange
        
    }//GEN-LAST:event_txtTOPPropertyChange

    private void txtTOPVetoableChange(java.beans.PropertyChangeEvent evt)throws java.beans.PropertyVetoException {//GEN-FIRST:event_txtTOPVetoableChange
        if(evt.getPropertyName().equalsIgnoreCase("text"))
            setDueDate();
    }//GEN-LAST:event_txtTOPVetoableChange

    private void txtNoPOFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtNoPOFocusLost
        // TODO add your handling code here:
    }//GEN-LAST:event_txtNoPOFocusLost

    private void txtNoPOPropertyChange(java.beans.PropertyChangeEvent evt) {//GEN-FIRST:event_txtNoPOPropertyChange
        btnPrint.setEnabled(txtNoPO.getText().length()>0);
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

    private void btnPrintActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnPrintActionPerformed
//        printKwitansi(txtNoPO.getText(), false);
    }//GEN-LAST:event_btnPrintActionPerformed

    private void formInternalFrameClosed(javax.swing.event.InternalFrameEvent evt) {//GEN-FIRST:event_formInternalFrameClosed
        this.winClosed=true;
        if (fn.isListVisible()) {
            fn.setVisibleList(false);
        }
        
    }//GEN-LAST:event_formInternalFrameClosed

    private void lblTeleponPropertyChange(java.beans.PropertyChangeEvent evt) {//GEN-FIRST:event_lblTeleponPropertyChange
        // TODO add your handling code here:
    }//GEN-LAST:event_lblTeleponPropertyChange

    private boolean udfCekBeforeSave(){
        if(txtSupplier.getText().trim().length()==0){
            JOptionPane.showMessageDialog(this, "Silakan isi supplier terlebih dulu!");
            txtSupplier.requestFocus();
            return false;
        }
        if(fn.udfGetInt(txtTOP.getText())==0){
            if(JOptionPane.showConfirmDialog(this, "TOP Masih Nol anda tetap melanjutkan?!", "Confirm", JOptionPane.YES_NO_OPTION)==JOptionPane.NO_OPTION){
                txtTOP.requestFocus();
                return false;
            }
        }
        if(!isKoreksi && tblPO.getRowCount()==0){
            JOptionPane.showMessageDialog(this, "Item PO masih kosong!");
            txtSupplier.requestFocus();
            return false;
        }
        if(isKoreksi && tblPO.getRowCount()==0){
            try{
                ResultSet rs=conn.createStatement().executeQuery(
                          "select sum(d.jumlah) as jumlah "
                        + "from phar_good_receipt g "
                        + "inner join phar_good_receipt_Detail d on d.good_receipt_id=g.good_receipt_id "
                        + "where no_po='"+txtNoPO.getText()+"' "
                        + "having sum(d.jumlah) > 0");
                if(rs.next()){
                    JOptionPane.showMessageDialog(this, "PO tidak bisa dibatalkan karena sudah Good Receipt!");
                    txtNoPO.requestFocus();
                    rs.close();
                    return false;
                }
                rs.close();
            }catch(SQLException se){
                JOptionPane.showMessageDialog(this, se.getMessage());
            }
            
        }
        if(!btnSave.isEnabled()) return false;
        return true;
    }

    private void udfSave(){
//'"+new SimpleDateFormat("dd/MM/yyyy").parse(txtDate.getText())+"'
        if(!udfCekBeforeSave()) return;
         String sSql="";
         ResultSet rs=null;
         try{
             conn.setAutoCommit(false);

             if(isKoreksi){
                 if(tblPO.getRowCount()>0){
                     rs=conn.createStatement().executeQuery("select fn_phar_po_koreksi2('"+txtNoPO.getText()+"', '"+MainForm.sUserID+"', '"+MainForm.sUserName+"')");
                     rs.next();
                     rs.close();

                     sSql=  "update phar_po set " +
                            "kode_supplier='"+txtSupplier.getText()+"'," +
                            "discount   ="+fn.udfGetDouble(txtDiscPersen.getText())+", " +
                            "disc_rp    ="+fn.udfGetDouble(txtDiscRp.getText())+", " +
                            "ppn        ="+fn.udfGetDouble(txtTotVat.getText())+"," +
                            "top        ="+txtTOP.getText()+"," +
                            "consigment ="+chkConsignment.isSelected()+"," +
                            "remark     ='"+txtRemark.getText()+"'," +
                            "shiping    ='"+txtShipping.getText()+"'," +
                            "due_date   ='"+new SimpleDateFormat("yyyy-MM-dd").format(new SimpleDateFormat("dd/MM/yyyy").parse(txtDueDate.getText()))+"'," +
                            "cito       ="+chkCito.isSelected()+", " +
                            "user_upd   ='"+MainForm.sUserName+"', " +
                            "time_upd   =now() " +
                            "where no_po='"+txtNoPO.getText()+"';";

                     sSql+="delete from phar_po_detail where no_po='"+txtNoPO.getText()+"';";

                     TableColumnModel col=tblPO.getColumnModel();
                     for(int i=0; i< tblPO.getRowCount(); i++){
                        sSql+="insert into phar_po_detail(no_po, kode_barang, jumlah, uom_po, harga, discount, ppn, user_ins, " +
                                "no_pr, jml_kecil,uom_kecil, konv, urut) values('"+txtNoPO.getText()+"', " +
                                "'"+tblPO.getValueAt(i, col.getColumnIndex("Product ID"))+"', " +
                                ""+fn.udfGetDouble(tblPO.getValueAt(i, col.getColumnIndex("Qty")))+", " +
                                "'"+tblPO.getValueAt(i, col.getColumnIndex("UOM"))+"', " +
                                ""+fn.udfGetDouble(tblPO.getValueAt(i, col.getColumnIndex("Unit Price")))+", " +
                                ""+fn.udfGetDouble(tblPO.getValueAt(i, col.getColumnIndex("Disc %")))+", " +
                                ""+fn.udfGetDouble(tblPO.getValueAt(i, col.getColumnIndex("Vat")))+", " +
                                "'"+MainForm.sUserName+"', '"+tblPO.getValueAt(i, col.getColumnIndex("PRNo")).toString()+"', " +
                                ""+fn.udfGetDouble(tblPO.getValueAt(i, col.getColumnIndex("JmlKecil")))+", " +
                                "'"+tblPO.getValueAt(i, col.getColumnIndex("UomKecil")).toString()+"', " +
                                ""+fn.udfGetDouble(tblPO.getValueAt(i, col.getColumnIndex("Conv")))+", " +
                                ""+fn.udfGetInt(tblPO.getValueAt(i, col.getColumnIndex("No.")))+" " +
                                ");";
                     }

                     //System.out.println(sSql);

                     conn.setAutoCommit(false);
                     int i=conn.createStatement().executeUpdate(sSql);
                     conn.setAutoCommit(true);
                     JOptionPane.showMessageDialog(this, "Revisi PO sukses!");

                     if(srcFrom!=null && srcFrom instanceof FrmPRMaintenance)
                         ((FrmPRMaintenance)srcFrom).udfLoadPR(0);

                     dispose();
                     return;
                 }else{
                     if(JOptionPane.showConfirmDialog(this, "Anda yakin untuk membatalkan PO '"+txtNoPO.getText()+"' ini?", "Confirm", JOptionPane.YES_NO_OPTION)==JOptionPane.NO_OPTION){
                        return;
                     }

                     conn.setAutoCommit(false);
                     int i=conn.createStatement().executeUpdate("Update phar_po set flag_trx='K', " +
                             "user_upd   ='"+MainForm.sUserName+"', " +
                             "time_upd   =now() " +
                             "where no_po='"+txtNoPO.getText()+"'; ");
                     conn.setAutoCommit(true);
                     JOptionPane.showMessageDialog(this, "Pembatalan PO sukses!");

                     if(srcFrom!=null && srcFrom instanceof FrmPRMaintenance)
                         ((FrmPRMaintenance)srcFrom).udfLoadPR(0);

                     dispose();
                     return;
                 }
             }
             
             if(tblPO.getRowCount()>0){
                 sSql="select fn_phar_get_no_po('"+MainForm.sUserID+"') as no_po";
                 rs=conn.createStatement().executeQuery(sSql);
                 if(rs.next())
                     txtNoPO.setText(rs.getString(1));

                 sSql="insert into phar_po(no_po, kode_supplier,discount, disc_rp, ppn,top," +
                    "consigment,remark,shiping, due_date,cito, user_ins) " +
                     "values(?, ?, ?, ?, ?, ?"
                         + ", ?, ?, ?, ?, ?, ?)";
                 PreparedStatement ps=conn.prepareStatement(sSql);
                 ps.setString(1, txtNoPO.getText());
                 ps.setString(2, txtSupplier.getText());
                 ps.setDouble(3, fn.udfGetDouble(txtDiscPersen.getText()));
                 ps.setDouble(4, fn.udfGetDouble(txtDiscRp.getText()));
                 ps.setDouble(5, fn.udfGetDouble(txtTotVat.getText()));
                 ps.setDouble(6, fn.udfGetDouble(txtTOP.getText()));
                 ps.setBoolean(7, chkConsignment.isSelected());
                 ps.setString(8, txtRemark.getText());
                 ps.setString(9, txtShipping.getText());
                 ps.setDate(10, new java.sql.Date(new SimpleDateFormat("dd/MM/yyyy").parse(txtDueDate.getText()).getTime()));
                 ps.setBoolean(11, chkCito.isSelected());
                 ps.setString(12, MainForm.sUserName);
                 ps.executeUpdate();
                 System.out.println(sSql);
                 sSql="";
                 TableColumnModel col=tblPO.getColumnModel();
                 for(int i=0; i< tblPO.getRowCount(); i++){
                    sSql+="insert into phar_po_detail(no_po, kode_barang, jumlah, uom_po, harga, discount, ppn, user_ins, " +
                            "no_pr, jml_kecil,uom_kecil, konv, urut) values('"+txtNoPO.getText()+"', " +
                            "'"+tblPO.getValueAt(i, col.getColumnIndex("Product ID"))+"', " +
                            ""+fn.udfGetDouble(tblPO.getValueAt(i, col.getColumnIndex("Qty")))+", " +
                            "'"+tblPO.getValueAt(i, col.getColumnIndex("UOM"))+"', " +
                            ""+fn.udfGetDouble(tblPO.getValueAt(i, col.getColumnIndex("Unit Price")))+", " +
                            ""+fn.udfGetDouble(tblPO.getValueAt(i, col.getColumnIndex("Disc %")))+", " +
                            ""+fn.udfGetDouble(tblPO.getValueAt(i, col.getColumnIndex("Vat")))+", " +
                            "'"+MainForm.sUserName+"', '"+tblPO.getValueAt(i, col.getColumnIndex("PRNo"))+"', " +
                            ""+fn.udfGetDouble(tblPO.getValueAt(i, col.getColumnIndex("JmlKecil")))+", " +
                            "'"+tblPO.getValueAt(i, col.getColumnIndex("UomKecil"))+"', " +
                            ""+fn.udfGetDouble(tblPO.getValueAt(i, col.getColumnIndex("Conv")))+", " +
                            ""+fn.udfGetInt(tblPO.getValueAt(i, col.getColumnIndex("No.")))+" " +
                            ");";
                 }

                 //System.out.println(sSql);
                 
                 int i=conn.createStatement().executeUpdate(sSql);
                 conn.setAutoCommit(true);
                 JOptionPane.showMessageDialog(this, "Simpan PO sukses!");
                 //printKwitansi(txtNoPO.getText(), false);
                 previewPO(txtNoPO.getText());
                 
                 if(srcFrom!=null && srcFrom instanceof FrmPRMaintenance)
                     ((FrmPRMaintenance)srcFrom).udfLoadPR(0);

                 if(isKoreksi) dispose();
             }
             udfNew();

         } catch (ParseException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage());
        }catch(SQLException se){

            try {
                conn.rollback();
                conn.setAutoCommit(true);
                JOptionPane.showMessageDialog(this, se.getMessage());
            } catch (SQLException ex1) {
                Logger.getLogger(FrmPO.class.getName()).log(Level.SEVERE, null, ex1);
            }
         }

    }

    public void previewPO(String sNo_PO){
        //String usr[][]= new String[2][2];
        String sTtd1="", sTtd2="", sTtd3="";
        String sJbt1="", sJbt2="", sJbt3="";
        byte[] pic1=null,pic2=null, pic3=null;

        try{
            int ii=0;

            Statement statt=conn.createStatement();
            ResultSet rss=statt.executeQuery("select ua.user_name,coalesce(ud.complete_name,'') as complete_name, "
                    + "coalesce(level,0) as level, jb.jabatan, jb.singkatan,ttd_electronic " +
                    "from m_user_acc ua " +
                    "inner join m_user ud on ud.username=ua.user_name " +
                    "inner join m_jabatan jb on jb.kode_jabatan=ud.kode_jabatan " +
                    "where acc_modul='PO' and coalesce(priority,0)=0 order by level limit 3");
            while (rss.next()) {
                if(rss.getInt("level")==1){
                    sJbt1=rss.getString("jabatan"); sTtd1=rss.getString("complete_name"); pic1=rss.getBytes("ttd_electronic");
                }else if(rss.getInt("level")==2){
                    sJbt2=rss.getString("jabatan"); sTtd2=rss.getString("complete_name"); pic2=rss.getBytes("ttd_electronic");
                }else if(rss.getInt("level")==3){
                    sJbt3=rss.getString("jabatan"); sTtd3=rss.getString("complete_name"); pic3=rss.getBytes("ttd_electronic");
                }
           }
            rss.close();
            statt.close();
        }catch(SQLException se){}
      try{
            setCursor(new Cursor(Cursor.WAIT_CURSOR));
            JasperReport jasperReportmkel = (JasperReport) JRLoader.loadObject(getClass().getResourceAsStream("/Reports/PO_v1.jasper"));
            HashMap parameter = new HashMap();
            parameter.put("corporate", MainForm.sNamaUsaha);
            parameter.put("alamat", MainForm.sAlamat);
            parameter.put("telp", "");
            parameter.put("no_po",sNo_PO);
            parameter.put("acc_1", sJbt1); //usr[0][0]
            parameter.put("acc_2", sJbt2);
            parameter.put("acc_3", sJbt3);
            parameter.put("acc_name1",sTtd1);
            parameter.put("acc_name2",sTtd2); //usr[1][1]
            parameter.put("acc_name3",sTtd3); //usr[1][1]
            parameter.put("img_acc1", pic1==null? null: new ByteArrayInputStream((byte[])pic1));
            parameter.put("img_acc2", pic2==null? null: new ByteArrayInputStream((byte[])pic2));
            parameter.put("img_acc3", pic3==null? null: new ByteArrayInputStream((byte[])pic3));
            //parameter.put("stempel", getClass().getResource("/pharpurchase/image/StempelPurchasing.gif").toString());
            parameter.put("stempel", null);
//            parameter.put("SUBREPORT_DIR", getClass().getResource("/pharpurchase/ust/Reports/").toString());
            JasperPrint jasperPrintmkel = JasperFillManager.fillReport(jasperReportmkel, parameter, conn);

            setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
            jasperPrintmkel.setOrientation(jasperPrintmkel.getOrientationValue());
            JasperViewer.viewReport(jasperPrintmkel, false);
            if(!jasperPrintmkel.getPages().isEmpty()){
                ResultSet rs=conn.createStatement().executeQuery(
                        "select * from fn_phar_po_update_status_print('"+sNo_PO+"', '"+MainForm.sUserName+"') as " +
                        "(time_print timestamp without time zone, print_ke int)");
                if(rs.next()){
                    //tblHeader.setValueAt(rs.getTimestamp(1), tblHeader.getSelectedRow(), tblHeader.getColumnModel().getColumnIndex("Last Print"));
                }
                rs.close();
            }

      }catch(JRException je){
            System.out.println(je.getMessage());
      }catch(SQLException se){
            JOptionPane.showMessageDialog(this, se.getMessage());
      }
  }
////    private void udfSave2(){
//////'"+new SimpleDateFormat("dd/MM/yyyy").parse(txtDate.getText())+"'
////        if(!udfCekBeforeSave()) return;
////         String sSql="";
////         ResultSet rs=null;
////         try{
////
////             if(isKoreksi){
////                 rs=conn.createStatement().executeQuery("select fn_phar_po_koreksi('"+txtNoPO.getText()+"', '"+MainForm.sID+"', '"+MainForm.sUserName+"')");
////                 rs.next();
////                 rs.close();
////
////             }
////             sSql="select fn_phar_get_no_po('"+MainForm.sID+"') as no_po";
////             rs=conn.createStatement().executeQuery(sSql);
////             if(rs.next())
////                 txtNoPO.setText(rs.getString(1));
////
////             if(tblPO.getRowCount()>0){
////                 sSql="insert into phar_po(no_po,tanggal,kode_supplier,discount, disc_rp, ppn,top," +
////                    "consigment,remark,currency,shiping,kurs,due_date,cito, buyer, user_ins, site_id) " +
////                     "values('"+txtNoPO.getText()+"'," +
////                     "now()," +
////                     "'"+txtSupplier.getText()+"',"+fn.udfGetDouble(txtDiscPersen.getText())+"," +
////                     ""+fn.udfGetDouble(txtDiscRp.getText())+", "+fn.udfGetDouble(txtTotVat.getText())+"," +
////                     ""+txtTOP.getText()+"," +
////                     ""+chkConsignment.isSelected()+",'"+txtRemark.getText()+"','"+txtCurr.getText()+"'," +
////                     "'"+txtShipping.getText()+"'," +
////                     ""+fn.udfGetInt(txtKurs.getText())+",'"+new SimpleDateFormat("dd/MM/yyyy").parse(txtDueDate.getText())+"'," +
////                     ""+chkCito.isSelected()+", '"+txtBuyer.getText()+"', '"+MainForm.sUserName+"', '"+txtSite.getText()+"'); ";
////
////                 TableColumnModel col=tblPO.getColumnModel();
////                 for(int i=0; i< tblPO.getRowCount(); i++){
////                    sSql+="insert into phar_po_detail(no_po, kode_barang, jumlah, uom_po, harga, discount, ppn, user_ins, " +
////                            "no_pr, jml_kecil,uom_kecil, konv) values('"+txtNoPO.getText()+"', " +
////                            "'"+tblPO.getValueAt(i, col.getColumnIndex("Product ID"))+"', " +
////                            ""+fn.udfGetDouble(tblPO.getValueAt(i, col.getColumnIndex("Qty")))+", " +
////                            "'"+tblPO.getValueAt(i, col.getColumnIndex("UOM"))+"', " +
////                            ""+fn.udfGetDouble(tblPO.getValueAt(i, col.getColumnIndex("Unit Price")).toString())+", " +
////                            ""+fn.udfGetDouble(tblPO.getValueAt(i, col.getColumnIndex("Disc %")).toString())+", " +
////                            ""+fn.udfGetDouble(tblPO.getValueAt(i, col.getColumnIndex("Vat")).toString())+", " +
////                            "'"+MainForm.sUserName+"', '"+tblPO.getValueAt(i, col.getColumnIndex("PRNo")).toString()+"', " +
////                            ""+fn.udfGetDouble(tblPO.getValueAt(i, col.getColumnIndex("JmlKecil")).toString())+", " +
////                            "'"+tblPO.getValueAt(i, col.getColumnIndex("UomKecil")).toString()+"', " +
////                            ""+fn.udfGetDouble(tblPO.getValueAt(i, col.getColumnIndex("Conv")).toString())+" " +
////                            ");";
////                 }
////
////                 //System.out.println(sSql);
////                 conn.setAutoCommit(false);
////
////                 int i=conn.createStatement().executeUpdate(sSql);
////                 conn.setAutoCommit(true);
////                 JOptionPane.showMessageDialog(this, "Simpan PO sukses!");
////                 //printKwitansi(txtNoPO.getText(), false);
////
////                 if(srcFrom!=null && srcFrom instanceof FrmPRMaintenance)
////                     ((FrmPRMaintenance)srcFrom).udfLoadPR(0);
////
////                 if(isKoreksi) dispose();
////             }
////             udfNew();
////
////         } catch (ParseException ex) {
////            JOptionPane.showMessageDialog(this, ex.getMessage());
////        }catch(SQLException se){
////
////            try {
////                conn.rollback();
////                conn.setAutoCommit(true);
////                JOptionPane.showMessageDialog(this, se.getMessage());
////            } catch (SQLException ex1) {
////                Logger.getLogger(FrmPO.class.getName()).log(Level.SEVERE, null, ex1);
////            }
////         }
////
////    }

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
//        if (JOptionPane.showConfirmDialog(null,"Siapkan Printer!","SGHS Go Open Source",JOptionPane.YES_NO_OPTION,JOptionPane.QUESTION_MESSAGE)==JOptionPane.YES_OPTION) {
//            PrintPO pn = new PrintPO(conn,sNo_PR,okCpy,services[i]);
//        }
//    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnCancel;
    private javax.swing.JButton btnNew;
    private javax.swing.JButton btnPrint;
    private javax.swing.JButton btnSave;
    private javax.swing.JCheckBox chkCito;
    private javax.swing.JCheckBox chkConsignment;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel18;
    private javax.swing.JLabel jLabel19;
    private javax.swing.JLabel jLabel22;
    private javax.swing.JLabel jLabel23;
    private javax.swing.JLabel jLabel24;
    private javax.swing.JLabel jLabel25;
    private javax.swing.JLabel jLabel26;
    private javax.swing.JLabel jLabel27;
    private javax.swing.JLabel jLabel28;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JSeparator jSeparator2;
    private javax.swing.JToolBar jToolBar1;
    private javax.swing.JLabel lblAlamatSupp;
    private javax.swing.JLabel lblSupplier;
    private javax.swing.JLabel lblTelepon;
    private javax.swing.JLabel lblUomKecil;
    private javax.swing.JTable tblPO;
    private javax.swing.JLabel txtConv;
    private javax.swing.JTextField txtDate;
    private javax.swing.JTextField txtDiscPersen;
    private javax.swing.JTextField txtDiscRp;
    private javax.swing.JTextField txtDueDate;
    private javax.swing.JLabel txtNetto;
    private javax.swing.JTextField txtNoPO;
    private javax.swing.JLabel txtNoPR;
    private javax.swing.JLabel txtQtyKecil;
    private javax.swing.JTextField txtRemark;
    private javax.swing.JTextField txtShipping;
    private javax.swing.JTextField txtSupplier;
    private javax.swing.JTextField txtTOP;
    private javax.swing.JLabel txtTotVat;
    private javax.swing.JLabel txtTotalLine;
    // End of variables declaration//GEN-END:variables

    private FocusListener txtFocusListener=new FocusListener() {
        public void focusGained(FocusEvent e) {
            if(e.getSource() instanceof JTextField || e.getSource() instanceof JFormattedTextField){
                ((JTextField)e.getSource()).setBackground(Color.YELLOW);
                if(e.getSource().equals(txtTOP)||e.getSource().equals(txtDiscPersen)||e.getSource().equals(txtDiscRp)||
                        (e.getSource() instanceof JTextField && ((JTextField)e.getSource()).getName()!=null && ((JTextField)e.getSource()).getName().equalsIgnoreCase("textEditor"))){
                    ((JTextField)e.getSource()).setSelectionStart(0);
                    ((JTextField)e.getSource()).setSelectionEnd(((JTextField)e.getSource()).getText().length());
                }
            }
        }


        public void focusLost(FocusEvent e) {
            if(e.getSource().getClass().getSimpleName().equalsIgnoreCase("JTextField")||
                    e.getSource().getClass().getSimpleName().equalsIgnoreCase("JFormattedTextField")){
                ((JTextField)e.getSource()).setBackground(Color.WHITE);

                if(!e.isTemporary() && e.getSource().equals(txtSupplier) && !isKoreksi && !fn.isListVisible() 
                        && suppLevel<=1 && !winClosed)
                    udfLoadItemFromPR();
                else if(e.getSource().equals(txtDiscPersen)){
                    txtDiscRp.setText(fn.dFmt.format(fn.udfGetDouble(txtTotalLine.getText())/100*fn.udfGetDouble(txtDiscPersen.getText()) ));
                    udfTotalBawah();
                }
                else if(e.getSource().equals(txtDiscRp)){
                    txtDiscRp.setText(fn.dFmt.format(fn.udfGetDouble(txtDiscRp.getText())));
                    if(fn.udfGetDouble(txtDiscPersen.getText())!=fn.udfGetDouble(txtDiscRp.getText())*100
                    /fn.udfGetDouble(txtTotalLine.getText())){
                        txtDiscPersen.setText("0");
                    }
                    udfTotalBawah();
                }else if(e.getSource().equals(txtTOP)||(e.getSource().equals(txtSupplier) && txtSupplier.getText().length()>0)){
                    setDueDate();
                }else if(e.getSource().equals(txtNoPO) && isKoreksi )
                    udfLoadPOKoreksi();

           }
        }
    } ;

    private void setDueDate(){
        try {
            txtDueDate.setText(new SimpleDateFormat("dd/MM/yyyy").format(
                    getDueDate(new SimpleDateFormat("dd/MM/yyyy").parse(txtDate.getText()),
                    fn.udfGetInt(txtTOP.getText()))));
        } catch (ParseException ex) {
            Logger.getLogger(FrmPO.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    private void udfTotalBawah(){
        txtNetto.setText(fn.dFmt.format((
                fn.udfGetDouble(txtTotalLine.getText()))+
                fn.udfGetDouble(txtTotVat.getText())-
                fn.udfGetDouble(txtDiscRp.getText()))
                );
    }

    public void setKoreksi(boolean b) {
        this.isKoreksi=b;
        txtNoPO.setEnabled(b);
    }

    SimpleDateFormat dmyFmt_hhmm=new SimpleDateFormat("dd/MM/yyyy HH:mm");

    public class MyRowRenderer extends DefaultTableCellRenderer implements TableCellRenderer{
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            if(value instanceof Date ){
                value=dmyFmt_hhmm.format(value);
            if(value instanceof Timestamp ){
                value=dmyFmt_hhmm.format(value);
            }}else if(value instanceof Double ||value instanceof Integer ||value instanceof Float  ){
                setHorizontalAlignment(SwingConstants.RIGHT);
                value=fn.dFmt.format(value);
            }
            setFont(table.getFont());
            if(isSelected){
                setBackground(table.getSelectionBackground());
                setForeground(table.getSelectionForeground());
            }else{
                setBackground(table.getBackground());
                setForeground(table.getForeground());
            }

            setValue(value);
            return this;
        }
    }

    public class MyKeyListener extends KeyAdapter {
        @Override
        public void keyPressed(KeyEvent evt) {
            Component ct = KeyboardFocusManager.getCurrentKeyboardFocusManager().getPermanentFocusOwner();
            int keyKode = evt.getKeyCode();
            switch(keyKode){
               case KeyEvent.VK_ENTER : {
                    if(!(ct instanceof JTable ||ct instanceof JXTable))                    {
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
                    if(!(ct instanceof JTable ||ct instanceof JXTable))
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
                    if(!(ct instanceof JTable ||ct instanceof JXTable))
                    {
                        Component c = findPrevFocus();
                        c.requestFocus();
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
                case KeyEvent.VK_INSERT:{
                    DLgLookup d1=new DLgLookup(JOptionPane.getFrameForComponent(FrmPO.this), true);
                    String sItem="", sNoPR="", sGabung="";
                    for(int i=0; i< tblPO.getRowCount(); i++){
                        sItem+=(sItem.length()==0? "" : ",") +"'"+tblPO.getValueAt(i, 2).toString()+"'";
                        sNoPR+=(sNoPR.length()==0? "" : ",") +"'"+tblPO.getValueAt(i, 1).toString()+"'";
                        sGabung+=(sGabung.length()==0? "" : ",") +"'"+tblPO.getValueAt(i, 1).toString()+"-"+tblPO.getValueAt(i, 2).toString()+ "'";
                    }

                    String s="select * from( " +
                            "select fn_phar_po_load_item_from_pr_supp.* " +
                            "from fn_phar_po_load_item_from_pr_supp('"+txtSupplier.getText()+"', " +
                            ""+chkConsignment.isSelected()+", " +
                            ""+chkCito.isSelected()+" ) as (no_pr varchar, kode_barang varchar, nama_barang varchar, qty double precision, uom_kecil varchar, " +
                            "conv numeric, uom_alt varchar, price real, disc real, vat real, requested_by varchar, time_appv timestamp without time zone) " +
                            //(sItem.length()>0? " where  kode_barang not in("+sItem+") " : "")+
                            //(sNoPR.length()>0? ((sItem.length()>0? " and ":  " where ")+" no_pr not in("+sNoPR+") ") : "")+
                            (sGabung.length()>0? " where  no_pr||'-'||kode_barang not in("+sGabung+") " : "")+
                            ") x ";

                    d1.setTitle("Lookup Item from PR");
                    d1.udfLoad(conn, s, "(kode_barang||nama_barang)", null);
                    d1.setVisible(true);

                    System.out.println(s);
                    if(d1.getKode().length()>0){
                        TableColumnModel col=d1.getTable().getColumnModel();
                        JTable tbl=d1.getTable();
                        int iRow = tbl.getSelectedRow();

                        ((DefaultTableModel)tblPO.getModel()).addRow(new Object[]{
                            tblPO.getRowCount()+1,
                            tbl.getValueAt(iRow, col.getColumnIndex("no_pr")).toString(),
                            tbl.getValueAt(iRow, col.getColumnIndex("kode_barang")).toString(),
                            tbl.getValueAt(iRow, col.getColumnIndex("nama_barang")).toString(),
                            tbl.getValueAt(iRow, col.getColumnIndex("uom_alt")).toString(),
                            fn.udfGetDouble(tbl.getValueAt(iRow, col.getColumnIndex("qty")))/fn.udfGetDouble(tbl.getValueAt(iRow, col.getColumnIndex("conv"))),
                            fn.udfGetDouble(tbl.getValueAt(iRow, col.getColumnIndex("price"))),
                            fn.udfGetDouble(tbl.getValueAt(iRow, col.getColumnIndex("disc"))),
                            fn.udfGetDouble(tbl.getValueAt(iRow, col.getColumnIndex("vat"))),
                            Math.floor((fn.udfGetDouble(tbl.getValueAt(iRow, col.getColumnIndex("qty")))/ fn.udfGetDouble(tbl.getValueAt(iRow, col.getColumnIndex("conv")))*fn.udfGetDouble(tbl.getValueAt(iRow, col.getColumnIndex("price"))))-
                                      ((fn.udfGetDouble(tbl.getValueAt(iRow, col.getColumnIndex("qty")))/ fn.udfGetDouble(tbl.getValueAt(iRow, col.getColumnIndex("conv")))*fn.udfGetDouble(tbl.getValueAt(iRow, col.getColumnIndex("price")))/100*fn.udfGetDouble(tbl.getValueAt(iRow, col.getColumnIndex("disc"))))) ),
                            fn.udfGetDouble(tbl.getValueAt(iRow, col.getColumnIndex("conv"))),
                            tbl.getValueAt(iRow, col.getColumnIndex("uom_kecil")).toString(),
                            fn.udfGetDouble(tbl.getValueAt(iRow, col.getColumnIndex("qty"))),
                            fn.udfGetDouble(tbl.getValueAt(iRow, col.getColumnIndex("qty")))/ fn.udfGetDouble(tbl.getValueAt(iRow, col.getColumnIndex("conv"))),
                            tbl.getValueAt(iRow, col.getColumnIndex("requested_by")).toString(),
                            tbl.getValueAt(iRow, col.getColumnIndex("time_appv")),
                            tbl.getValueAt(iRow, col.getColumnIndex("bp_type")).toString(),
                            
                        });
                            
                        tblPO.setRowSelectionInterval(tblPO.getRowCount()-1, tblPO.getRowCount()-1);
                        tblPO.requestFocusInWindow();
                        tblPO.setModel((DefaultTableModel)fn.autoResizeColWidth(tblPO, (DefaultTableModel)tblPO.getModel()).getModel());
                        tblPO.changeSelection(tblPO.getRowCount()-1, tblPO.getColumnModel().getColumnIndex("Qty"), false, false);

                    }

                    break;
                }

                case KeyEvent.VK_DELETE:{
                    if(evt.getSource().equals(tblPO) && tblPO.getSelectedRow()>=0){
                        int iRow[]= tblPO.getSelectedRows();
                        int rowPalingAtas=iRow[0];

                        if(isKoreksi){
                            try{
                                ResultSet rs=null;
                                for(int i=0; i<iRow.length; i++){
                                    rs=conn.createStatement().executeQuery(
                                            "select sum(jumlah) as gr "
                                            +"from phar_good_receipt_detail d " +
                                            "inner join phar_good_receipt g on g.good_receipt_id=d.good_receipt_id " +
                                            "where g.no_po='"+txtNoPO.getText()+"' and " +
                                            "d.kode_barang='"+tblPO.getValueAt(iRow[i], tblPO.getColumnModel().getColumnIndex("Product ID")).toString()+"' " +
                                            "and d.no_pr= '" + tblPO.getValueAt(iRow[i], tblPO.getColumnModel().getColumnIndex("PRNo")).toString()+"'");

                                    if(rs.next() && rs.getDouble("gr")>0){
                                        JOptionPane.showMessageDialog(FrmPO.this, "Item '"+tblPO.getValueAt(iRow[i], tblPO.getColumnModel().getColumnIndex("Keterangan")).toString()+"' " +
                                                "pada baris ke "+(iRow[i]+1)+ ", Sudah dilakukan Good Receipt jadi tidak bisa dihapus!");
                                        rs.close();
                                        return;
                                        
                                    }
                                    rs.close();
                                }
                            }catch(SQLException se){
                                JOptionPane.showMessageDialog(FrmPO.this, se.getMessage(), "MyKeyListener VK_DELETE", JOptionPane.ERROR_MESSAGE);
                            }

                        }

                        
                        for (int a=0; a<iRow.length; a++){
                            ((DefaultTableModel)tblPO.getModel()).removeRow(tblPO.getSelectedRow());
                        }

                        if(tblPO.getRowCount()>0 && rowPalingAtas<tblPO.getRowCount()){
                            //if(tblPO.getSelectedRow()>0)
                                tblPO.setRowSelectionInterval(rowPalingAtas, rowPalingAtas);
                        }
                        else{
                            if(tblPO.getRowCount()>0)
                                tblPO.setRowSelectionInterval(rowPalingAtas-1, rowPalingAtas-1);
                            else
                                txtSupplier.requestFocus();
                            
                        }
                        if(tblPO.getSelectedRow()>=0)
                            tblPO.changeSelection(tblPO.getSelectedRow(), 0, false, false);
                    }
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


    public class MyTableCellEditor extends AbstractCellEditor implements TableCellEditor {
        private Toolkit toolkit;
        JTextField text=ustTextField;

        int col, row;

        //private NumberFormat nf=NumberFormat.getNumberInstance(Locale.US);

        public Component getTableCellEditorComponent(JTable table, Object value,
            boolean isSelected, int rowIndex, int vColIndex) {
            // 'value' is value contained in the cell located at (rowIndex, vColIndex)

           //text.addKeyListener(kListener);
           //text.setEditable(canEdit);
           col=vColIndex;
           row=rowIndex;
           text.setBackground(new Color(0,255,204));
           text.addFocusListener(txtFocusListener);
           text.addKeyListener(kListener);
           text.setFont(table.getFont());
           text.setName("textEditor");

           text.addKeyListener(new java.awt.event.KeyAdapter() {
                   public void keyTyped(java.awt.event.KeyEvent evt) {
                       fn.keyTyped(evt);
//                      if (col!=0) {
//                          char c = evt.getKeyChar();
//                          if (!((c >= '0' && c <= '9') || c=='.') &&
//                                (c != KeyEvent.VK_BACK_SPACE) &&
//                                (c != KeyEvent.VK_DELETE) &&
//                                (c != KeyEvent.VK_ENTER)) {
//                                getToolkit().beep();
//                                evt.consume();
//                                return;
//                          }
//                       }
                    }
                });
           if (isSelected) {

           }
           //System.out.println("Value dari editor :"+value);
            //text.setText(value==null? "": value.toString());
            //component.setText(df.format(value));

            if(value instanceof Double || value instanceof Float|| value instanceof Integer){
//                try {
                    //Double dVal=Double.parseDouble(value.toString().replace(",",""));
                    double dVal = fn.udfGetDouble(value.toString());
                    text.setText(fn.dFmt.format(dVal));
//                } catch (java.text.ParseException ex) {
//                    //Logger.getLogger(DlgLookupBarang.class.getName()).log(Level.SEVERE, null, ex);
//                }
            }else
                text.setText(value==null? "":value.toString());
           return text;
        }

        public Object getCellEditorValue() {
            Object o="";//=component.getText();
            Object retVal = 0;
            try {
                retVal = fn.udfGetDouble(((JTextField)text).getText());

                if(col==tblPO.getColumnModel().getColumnIndex("Qty")){
                    double sisaPR=fn.udfGetDouble(tblPO.getValueAt(row, tblPO.getColumnModel().getColumnIndex("SisaPR")));

                    if(!isKoreksi && fn.udfGetDouble(((JTextField)text).getText())> sisaPR ){
                        JOptionPane.showMessageDialog(FrmPO.this, "Jumlah Qty yang dimasukkan melebihi Qty PR\nQuantity PR adalah : "+
                                fn.dFmt.format(sisaPR));
                        o=(fn.udfGetDouble(tblPO.getValueAt(row, tblPO.getColumnModel().getColumnIndex("Qty"))));
                        return o;
                    }else if(isKoreksi){
                        String sQry="select coalesce(sum(jumlah),0) as qty "
                                + "from phar_good_receipt_detail d "
                                + "inner join phar_good_receipt g on g.good_receipt_id=d.good_receipt_id "
                                + "where g.no_po='"+txtNoPO.getText()+"' "
                                + "and d.kode_barang='"+tblPO.getValueAt(row, tblPO.getColumnModel().getColumnIndex("Product ID")).toString() +"' "
                                + "and d.no_pr='"+tblPO.getValueAt(row, tblPO.getColumnModel().getColumnIndex("PRNo")).toString() +"'";
                        
                        ResultSet rs=conn.createStatement().executeQuery(sQry);
                        double qtyGR=0;
                        if(rs.next()){
                            qtyGR=rs.getDouble("qty");
                        }
                        if(qtyGR>0 && fn.udfGetDouble(((JTextField)text).getText())< qtyGR){
                            JOptionPane.showMessageDialog(FrmPO.this, "Jumlah Qty yang dimasukkan kurang dari Qty GR\nQuantity GR adalah : "+
                                fn.dFmt.format(qtyGR));
                            o=(fn.udfGetDouble(tblPO.getValueAt(row, tblPO.getColumnModel().getColumnIndex("Qty"))));
                            return o;
                        }
                    }
                }
                o=retVal;

                return o;
            } catch (Exception e) {
                toolkit.beep();
                retVal=0;
            }
            return retVal;
        }

    }

    final JTextField ustTextField = new JTextField() {
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

}
