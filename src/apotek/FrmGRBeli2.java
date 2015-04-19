/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * FrmGRBeli.java
 *
 * Created on 05 Feb 11, 21:04:03
 */

package apotek;

import main.MainForm;
import apotek.master.FrmSupplier;
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
import java.beans.PropertyVetoException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Vector;
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
import main.GeneralFunction;
import org.jdesktop.swingx.autocomplete.ComboBoxCellEditor;

/**
 *
 * @author cak-ust
 */
public class FrmGRBeli2 extends javax.swing.JInternalFrame {
    private Connection conn;
    private GeneralFunction fn;
    private Component aThis;
    private DlgLookupItemBeli lookupItem =new DlgLookupItemBeli(JOptionPane.getFrameForComponent(this), true);
    private MyKeyListener kListener=new MyKeyListener();
    ArrayList lstGudang=new ArrayList();
    private boolean stItemUpd=false;
    private String sNoTrx;
    SimpleDateFormat dmy = new SimpleDateFormat("dd/MM/yyyy");
    SimpleDateFormat ymd = new SimpleDateFormat("yyyy-MM-dd");
    private boolean stMinus=false;
    private boolean withPO=true;
    TableColumnModel col;
    private boolean isKoreksi=false;
    private JComboBox cmbSatuan=new JComboBox();

    /** Creates new form FrmGRBeli */
    public FrmGRBeli2() {
        initComponents();
        
        aThis=this;
        
//        table.getColumn("Konv").setMinWidth(0); table.getColumn("Konv").setMaxWidth(0); table.getColumn("Konv").setPreferredWidth(0);
//        table.getColumn("UomKecil").setMinWidth(0); table.getColumn("UomKecil").setMaxWidth(0); table.getColumn("UomKecil").setPreferredWidth(0);
//        table.getColumn("JmlKecil").setMinWidth(0); table.getColumn("JmlKecil").setMaxWidth(0); table.getColumn("JmlKecil").setPreferredWidth(0);
//        table.getColumn("OnHand").setMinWidth(0); table.getColumn("OnHand").setMaxWidth(0); table.getColumn("OnHand").setPreferredWidth(0);
//        tblPR.getColumn("Harga").setMinWidth(0); tblPR.getColumn("Harga").setMaxWidth(0); tblPR.getColumn("Harga").setPreferredWidth(0);
//        table.getColumn("Disc%").setMinWidth(0); table.getColumn("Disc%").setMaxWidth(0); table.getColumn("Disc%").setPreferredWidth(0);
//        table.getColumn("PPn%").setMinWidth(0); table.getColumn("PPn%").setMaxWidth(0); table.getColumn("PPn%").setPreferredWidth(0);

        col=table.getColumnModel();

        table.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            public void valueChanged(ListSelectionEvent e) {
                int iRow=table.getSelectedRow();
                if(iRow>=0){
//                    txtConv.setText(fn.dFmt.format(fn.udfGetDouble(table.getValueAt(iRow, col.getColumnIndex("Konv")))));
//                    lblUomKecil.setText(table.getValueAt(iRow, col.getColumnIndex("UomKecil")).toString());
//                    txtStockOnHand.setText(fn.dFmt.format(fn.udfGetDouble(table.getValueAt(iRow, col.getColumnIndex("OnHand")))));
                }
            }
        });

        table.getModel().addTableModelListener(new TableModelListener() {
            public void tableChanged(TableModelEvent e) {
                int iRow=table.getSelectedRow();
                int iCol=e.getColumn();
                TableColumnModel col=table.getColumnModel();

                if(iRow>=0 && iCol==col.getColumnIndex("Product ID") && e.getType()==TableModelEvent.UPDATE ){
                    udfLoadItem(table.getValueAt(iRow, iCol).toString());
                }else if(iRow>=0  && e.getType()==TableModelEvent.UPDATE && (
                        iCol==col.getColumnIndex("Qty")||iCol==col.getColumnIndex("Harga")||iCol==col.getColumnIndex("PPn%")
                        )){
                    udfSetSubTotalItem();
                }else if(e.getType()==TableModelEvent.UPDATE && iCol==col.getColumnIndex("Disc%") && fn.udfGetDouble(table.getValueAt(iRow, col.getColumnIndex("Disc%"))) >0){
                    table.setValueAt(0, iRow, col.getColumnIndex("Disc(Rp)"));
                    udfSetSubTotalItem();
                }else if(e.getType()==TableModelEvent.UPDATE && iCol==col.getColumnIndex("Disc(Rp)") && fn.udfGetDouble(table.getValueAt(iRow, col.getColumnIndex("Disc(Rp)"))) >0){
                    table.setValueAt(0, iRow, col.getColumnIndex("Disc%"));
                    udfSetSubTotalItem();
                }
                

                
                txtTotalLine.setText("0");
                txtTotVat.setText("0");
                txtNetto.setText("0");

                 if(table.getRowCount()>0){
                    double totLine=0, totVat=0;
                    double extPrice=0;
                    for(int i=0; i< table.getRowCount(); i++){
                        //if(e.getType()==TableModelEvent.DELETE) ((DefaultTableModel)table.getModel()).setValueAt(i+1, i, 0);
                        extPrice=fn.udfGetDouble(table.getValueAt(i, col.getColumnIndex("Sub Total")));
                        totLine+=extPrice;
                        //totVat+=extPrice/100*fn.udfGetDouble(table.getValueAt(i, col.getColumnIndex("PPN")));
                        totVat+= extPrice/100*fn.udfGetDouble(table.getValueAt(i, col.getColumnIndex("PPn%")));
                    }

                    txtTotalLine.setText(fn.dFmt.format(Math.floor(totLine)));
                    txtTotVat.setText(fn.dFmt.format(Math.floor(totVat)));
                    txtNetto.setText(fn.dFmt.format(Math.floor(totVat)+Math.floor(totLine)));
                }else{
                    txtTotalLine.setText("0");
                    txtTotVat.setText("0");
                    txtNetto.setText("0");
                }
            }
        });
        cmbSatuan.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                if(cmbSatuan.getSelectedIndex()>=0 && conn!=null)
                    udfLoadKonversi(cmbSatuan.getSelectedItem().toString());

            }
        });
        
        MyTableCellEditor cEditor=new MyTableCellEditor();
        table.getColumn("Expired").setCellEditor(cEditor);
        table.getColumn("Qty").setCellEditor(cEditor);
        table.getColumn("Harga").setCellEditor(cEditor);
        table.getColumn("Disc%").setCellEditor(cEditor);
        table.getColumn("Disc(Rp)").setCellEditor(cEditor);
        table.getColumn("PPn%").setCellEditor(cEditor);
        table.getColumn("Satuan").setCellEditor(new ComboBoxCellEditor(cmbSatuan));
        
        table.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).
        put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0),
            "selectNextColumnCell");

        //txtSite.setEnabled(false);
    }

    public void udfSetNewSupplier(String sKode, String sNama){
        txtSupplier.setText(sKode);
        lblSupplier.setText(sNama);
        if(!txtSupplier.isFocusOwner())
            txtSupplier.requestFocusInWindow();
    }

    private void udfLoadKonversi(String sUnit) {
        int row=table.getSelectedRow();
        if(cmbSatuan.getSelectedIndex()<0 || row<0 || table.getValueAt(row, table.getColumnModel().getColumnIndex("Product ID"))==null ) return;
        try {
            String sQry = "select case  when '" + sUnit + "'=satuan_besar then coalesce(konversi,1) " +
                          "             else 1 end as konv  " +
                          "from barang i " +
                          "where i.item_code='" + table.getValueAt(row, table.getColumnModel().getColumnIndex("Product ID")).toString() + "'";

            //System.out.println(sQry);
            ResultSet rs = conn.createStatement().executeQuery(sQry);
            if (rs.next()) {
                table.setValueAt(rs.getDouble("konv"), row, table.getColumnModel().getColumnIndex("Konv"));                    
            } else {
                table.setValueAt(1, row, table.getColumnModel().getColumnIndex("Konv"));                    
            }
            rs.close();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage());
        }
    }

    private void initConn(){
        String url = "jdbc:postgresql://localhost/NABILA";
        try{
            Class.forName("org.postgresql.Driver");
            conn = DriverManager.getConnection(url,"tadho","ustasoft");

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage());
        } catch(ClassNotFoundException ce) {
            System.out.println(ce.getMessage());
        }
    }

    private Date getDueDate(Date d, int i){
        Calendar c = Calendar.getInstance();
        c.setTime(d);
        c.add(Calendar.DAY_OF_MONTH, i);

        return c.getTime();
    }

    private void setDueDate(){
        try {
            jFJtTempo.setText(new SimpleDateFormat("dd/MM/yyyy").format(
                    getDueDate(new SimpleDateFormat("dd/MM/yyyy").parse(txtDate.getText()),
                    fn.udfGetInt(txtTop.getText()))));
        } catch (ParseException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage());
        }
    }

    private boolean udfCekBeforeSave(){
        if(txtSupplier.getText().length()==0){
            JOptionPane.showMessageDialog(this, "Silakan masukkan supplier terlebih dulu~");
            txtSupplier.requestFocus();
            return false;
        }
        if(txtSite.getText().length()==0){
            JOptionPane.showMessageDialog(this, "Silakan masukkan Site terlebih dulu~");
            txtSite.requestFocus();
            return false;
        }
        if(table.getRowCount()==0){
            JOptionPane.showMessageDialog(this, "Item yang diterima masih kosong~");
            table.requestFocus();
            return false;
        }
        if(txtNoInvoice.getText().trim().isEmpty()){
            JOptionPane.showMessageDialog(this, "Masukkan nomor Invoice terlebih dulu");
            txtNoInvoice.requestFocus();
            return false;
        }
        if(fn.udfGetDouble(txtBayar.getText())>fn.udfGetDouble(txtNetto.getText())||fn.udfGetDouble(txtBayar.getText())<0){
            JOptionPane.showMessageDialog(this, "Jumlah pembayaran harus lebih kecil atau sama dengan total pembelian ");
            txtNoInvoice.requestFocus();
            return false;
        }
        if(fn.udfGetDouble(txtBayar.getText())==0){
            if(JOptionPane.showConfirmDialog(this, "Anda akan menyimpan pembelian dengan tanpa pembayaran?", "Pembelian Kredit", JOptionPane.YES_NO_OPTION)==JOptionPane.NO_OPTION){
                if(!txtBayar.isFocusOwner())
                    txtBayar.requestFocus();
                return false;
            }
        }
        if(!stMinus && !isKoreksi){
            try{
                ResultSet rs=conn.createStatement().executeQuery(
                        "select no_sj_supplier from good_receipt where no_sj_supplier='"+txtNoInvoice.getText()+"'; ");
                if(rs.next()){
                    JOptionPane.showMessageDialog(this, "No. Invoice sudah pernah dimasukkan!");
                    txtNoInvoice.requestFocus();
                    rs.close();
                    return false;
                }
                rs.close();

            }catch(SQLException se){
                JOptionPane.showMessageDialog(this, se.getMessage());
            }
        }
//        for(int i=0; i<table.getRowCount(); i++){
//            if(table.getValueAt(i, col.getColumnIndex("Expired")).toString().equalsIgnoreCase("")){
//                if(JOptionPane.showConfirmDialog(this, "Expired Date pada baris ke : "+(i+1)+" masih kosong.\nAkan dilanjutkan", "Confirm", JOptionPane.YES_NO_OPTION)==JOptionPane.NO_OPTION){
//                    table.grabFocus();
//                    table.changeSelection(i, col.getColumnIndex("Expired"), false, false);
//                    return false;
//                }
//            }
//        }
        return true;
    }


    private void udfSave(){
        if(!udfCekBeforeSave()) return;
        TableColumnModel col=table.getColumnModel();
        try{
            conn.setAutoCommit(false);
            ResultSet rs=null;
            String sNoKoreksi="";
            if(isKoreksi){
                rs=conn.createStatement().executeQuery("select fn_koreksi_gr('"+txtNoGR.getText()+"')");
                if(rs.next())
                    sNoKoreksi=rs.getString(1);

                rs.close();
                if(table.getRowCount()==0){
                    JOptionPane.showMessageDialog(this, "Transaksi dibatalkan dengan nomor '"+sNoKoreksi+"'");
                    dispose();
                    return;
                }
            }
            String sSql="select fn_get_kode_gr('"+ymd.format(dmy.parse(txtDate.getText()))+"'::date) as no_gr";
            String sNoGr="";
            rs=conn.createStatement().executeQuery(sSql);
            if(rs.next()){
                txtNoGR.setText(rs.getString(1));
                sNoGr=rs.getString(1);
            }
            rs.close();
            sSql="INSERT INTO good_receipt(no_penerimaan, kode_supplier, tanggal, no_sj_supplier, " +
                    "user_terima, keterangan, user_trx, tanggal_ins, " +
                    "kode_gudang, tgl_jatuh_tempo, biaya_lain, is_lunas, " +
                    "no_po, bayar) " +
                    "values('"+sNoGr+"', '"+txtSupplier.getText()+"', now(), '"+txtNoInvoice.getText()+"', " +
                    "'"+txtReceiptBy.getText()+"', '"+txtRemark.getText()+"', '"+MainForm.sUserName+"', now(), " +
                    "'"+txtSite.getText()+"', '"+ymd.format(dmy.parse(txtDate.getText()))+"'::date, "+fn.udfGetDouble(txtBiayaLain.getText())+", " +
                    (fn.udfGetDouble(txtBayar.getText())>=fn.udfGetDouble(txtNetto.getText()))+","+
                    "'"+txtNoPO.getText()+"', " +
                    fn.udfGetDouble(txtBayar.getText())+ ") ; ";

            String sExpDate="";
            for(int i=0; i<table.getRowCount(); i++){
                sExpDate=(table.getValueAt(i, col.getColumnIndex("Expired")).toString().length()==0? "null" : "'"+new SimpleDateFormat("dd/MM/yy").parse(table.getValueAt(i, col.getColumnIndex("Expired")).toString())+"'");
                sSql+=  "INSERT INTO good_receipt_detail(" +
                        "no_penerimaan, no_po, item_code, jumlah, harga, discount, tax, " +
                        "user_ins, exp_date, " +
                        "qty_beli, harga_beli, disc, ppn, is_disc_rp, is_ppn_rp, uom_beli, " +
                        "konv) values('"+sNoGr+"', '"+txtNoPO.getText()+"',  " +
                        "'"+table.getValueAt(i, col.getColumnIndex("Product ID")).toString()+"', " +
                        fn.udfGetDouble(table.getValueAt(i, col.getColumnIndex("Qty")))*fn.udfGetDouble(table.getValueAt(i, col.getColumnIndex("Konv")))+"," +
                        fn.udfGetDouble(table.getValueAt(i, col.getColumnIndex("Harga")))/fn.udfGetDouble(table.getValueAt(i, col.getColumnIndex("Konv")))+ ", " +
                        fn.udfGetDouble(table.getValueAt(i, col.getColumnIndex("Disc")))+ ", " +
                        fn.udfGetDouble(table.getValueAt(i, col.getColumnIndex("PPN")))+ ", " +
                        "'"+MainForm.sUserName+"',"+ sExpDate+"," +
                        fn.udfGetDouble(table.getValueAt(i, col.getColumnIndex("Qty")))+","+
                        fn.udfGetDouble(table.getValueAt(i, col.getColumnIndex("Harga")))+","+
                        fn.udfGetDouble(table.getValueAt(i, col.getColumnIndex("Disc")))+","+
                        fn.udfGetDouble(table.getValueAt(i, col.getColumnIndex("PPN")))+","+
                        (Boolean)table.getValueAt(i, col.getColumnIndex("Disc%"))+"," +
                        (Boolean)table.getValueAt(i, col.getColumnIndex("PPn%"))+","+
                        "'"+table.getValueAt(i, col.getColumnIndex("Satuan")).toString()+"'," +
                        ""+fn.udfGetDouble(table.getValueAt(i, col.getColumnIndex("Konv")))+");";
            }

            System.out.println(sSql);
            
            int i=conn.createStatement().executeUpdate(sSql);
            conn.setAutoCommit(true);
            JOptionPane.showMessageDialog(this, "Simpan pembelian Sukses!");
            //printKwitansi(txtNoPO.getText(), false);
            udfNew();

        } catch (ParseException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage());
        }catch(SQLException ex){

            try {
                conn.setAutoCommit(true);
                conn.rollback();
                JOptionPane.showMessageDialog(this, ex.getMessage());
            } catch (SQLException ex1) {
                JOptionPane.showMessageDialog(aThis, ex1.getMessage());
            }
        }catch(java.lang.ArrayIndexOutOfBoundsException ex){

        }
    }

    private void udfNew(){
        txtSupplier.setText(""); lblSupplier.setText("");
        txtNoPO.setText("");
        txtNoInvoice.setText("");
        txtRemark.setText("");
        txtTglPO.setText("");
        txtTop.setText(""); jFJtTempo.setText("");
        txtBayar.setText("0");

        ((DefaultTableModel)table.getModel()).setNumRows(0);
        txtNetto.setText("0");
        txtBiayaLain.setText("0");
        txtTotVat.setText("0");
        txtTotalLine.setText("0");

        btnNew.setEnabled(false);
        btnSave.setEnabled(true);
        btnCancel.setText("Cancel");
        btnCancel.setIcon(new javax.swing.ImageIcon(getClass().getResource("/image/Icon/Cancel.png")));
        //txtSite.setText(""); lblSite.setText("");
        //txtSupplier.requestFocus();
        //udfClearItem();

        if(!txtSupplier.isFocusOwner())
            txtSupplier.requestFocusInWindow();
        
    }

//    private void udfClearItem(){
//        txtKode.setText("");
//        lblItem.setText("");
//        jFJtExpDate.setText("");
//        cmbSatuan.removeAllItems();
//        txtOnReceipt.setText("1");
//        txtHarga.setText("0");
//        txtDisc.setText("0");
//        txtPPn.setText("0");
//        lblSubTotal.setText("0");
//        lblKonv.setText("1");
//        stItemUpd=false;
//        
//    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jScrollPane1 = new javax.swing.JScrollPane();
        table = new javax.swing.JTable();
        jToolBar1 = new javax.swing.JToolBar();
        btnNew = new javax.swing.JButton();
        btnSave = new javax.swing.JButton();
        btnPrint = new javax.swing.JButton();
        btnCancel = new javax.swing.JButton();
        jLabel16 = new javax.swing.JLabel();
        jPanel2 = new javax.swing.JPanel();
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
        jLabel7 = new javax.swing.JLabel();
        jLabel23 = new javax.swing.JLabel();
        txtNoInvoice = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        txtDate = new javax.swing.JTextField();
        jLabel17 = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        txtReceiptBy = new javax.swing.JTextField();
        jLabel13 = new javax.swing.JLabel();
        jLabel19 = new javax.swing.JLabel();
        txtNoGR = new javax.swing.JTextField();
        jLabel15 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        jLabel27 = new javax.swing.JLabel();
        txtTglPO = new javax.swing.JTextField();
        jLabel10 = new javax.swing.JLabel();
        jLabel28 = new javax.swing.JLabel();
        txtNoPO = new javax.swing.JTextField();
        jLabel11 = new javax.swing.JLabel();
        txtTop = new javax.swing.JTextField();
        jFJtTempo = new javax.swing.JFormattedTextField();
        jLabel12 = new javax.swing.JLabel();
        jLabel24 = new javax.swing.JLabel();
        jButton1 = new javax.swing.JButton();
        jPanel4 = new javax.swing.JPanel();
        jLabel31 = new javax.swing.JLabel();
        txtTotalLine = new javax.swing.JLabel();
        jLabel26 = new javax.swing.JLabel();
        txtTotVat = new javax.swing.JLabel();
        txtNetto = new javax.swing.JLabel();
        jLabel30 = new javax.swing.JLabel();
        jLabel32 = new javax.swing.JLabel();
        txtBiayaLain = new javax.swing.JTextField();
        txtBayar = new javax.swing.JTextField();
        jLabel29 = new javax.swing.JLabel();
        jLabel1 = new javax.swing.JLabel();

        setClosable(true);
        setIconifiable(true);
        setMaximizable(true);
        setResizable(true);
        setTitle("Pembelian Barang");
        addInternalFrameListener(new javax.swing.event.InternalFrameListener() {
            public void internalFrameActivated(javax.swing.event.InternalFrameEvent evt) {
                formInternalFrameActivated(evt);
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
        getContentPane().setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jScrollPane1.setName("jScrollPane1"); // NOI18N

        table.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        table.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Product ID", "Keterangan", "Expired", "Qty", "Satuan", "Harga", "Disc%", "Disc(Rp)", "PPn%", "Sub Total", "Konv"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Double.class, java.lang.Object.class, java.lang.Double.class, java.lang.Double.class, java.lang.Double.class, java.lang.Double.class, java.lang.Double.class, java.lang.Double.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, true, true, true, true, true, true, true, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        table.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_OFF);
        table.setName("table"); // NOI18N
        jScrollPane1.setViewportView(table);

        getContentPane().add(jScrollPane1, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 198, 830, 260));

        jToolBar1.setRollover(true);
        jToolBar1.setName("jToolBar1"); // NOI18N

        btnNew.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/Icon/buttons/add-32.png"))); // NOI18N
        btnNew.setText("New");
        btnNew.setFocusable(false);
        btnNew.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnNew.setName("btnNew"); // NOI18N
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
        btnSave.setName("btnSave"); // NOI18N
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
        btnPrint.setName("btnPrint"); // NOI18N
        btnPrint.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnPrint.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnPrintActionPerformed(evt);
            }
        });
        jToolBar1.add(btnPrint);

        btnCancel.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/Icon/buttons/exit-32.png"))); // NOI18N
        btnCancel.setText("Exit");
        btnCancel.setFocusable(false);
        btnCancel.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnCancel.setName("btnCancel"); // NOI18N
        btnCancel.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnCancel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCancelActionPerformed(evt);
            }
        });
        jToolBar1.add(btnCancel);

        getContentPane().add(jToolBar1, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 188, -1));

        jLabel16.setFont(new java.awt.Font("Tahoma", 0, 24));
        jLabel16.setForeground(new java.awt.Color(0, 0, 153));
        jLabel16.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel16.setText(" Pembelian Barang");
        jLabel16.setName("jLabel16"); // NOI18N
        getContentPane().add(jLabel16, new org.netbeans.lib.awtextra.AbsoluteConstraints(288, 10, 498, -1));

        jPanel2.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        jPanel2.setName("jPanel2"); // NOI18N
        jPanel2.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel18.setFont(new java.awt.Font("Tahoma", 0, 12));
        jLabel18.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel18.setText("Gudang");
        jLabel18.setName("jLabel18"); // NOI18N
        jPanel2.add(jLabel18, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 85, 90, 20));

        txtSite.setFont(new java.awt.Font("Dialog", 0, 12));
        txtSite.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 1, true));
        txtSite.setDisabledTextColor(new java.awt.Color(153, 153, 153));
        txtSite.setName("txtSite"); // NOI18N
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
        jPanel2.add(txtSite, new org.netbeans.lib.awtextra.AbsoluteConstraints(110, 85, 60, 20));

        jLabel6.setFont(new java.awt.Font("Tahoma", 0, 12));
        jLabel6.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel6.setText("Catatan");
        jLabel6.setName("jLabel6"); // NOI18N
        jPanel2.add(jLabel6, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 60, 90, 20));

        txtRemark.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
        txtRemark.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 1, true));
        txtRemark.setDisabledTextColor(new java.awt.Color(0, 0, 0));
        txtRemark.setName("txtRemark"); // NOI18N
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
        jPanel2.add(txtRemark, new org.netbeans.lib.awtextra.AbsoluteConstraints(110, 60, 360, 20));

        lblSite.setFont(new java.awt.Font("Dialog", 0, 12));
        lblSite.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 1, true));
        lblSite.setName("lblSite"); // NOI18N
        lblSite.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                lblSitePropertyChange(evt);
            }
        });
        jPanel2.add(lblSite, new org.netbeans.lib.awtextra.AbsoluteConstraints(170, 85, 260, 20));

        jLabel14.setFont(new java.awt.Font("Tahoma", 0, 12));
        jLabel14.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel14.setText(":");
        jLabel14.setName("jLabel14"); // NOI18N
        jPanel2.add(jLabel14, new org.netbeans.lib.awtextra.AbsoluteConstraints(100, 10, 10, 20));

        lblSupplier.setFont(new java.awt.Font("Dialog", 0, 12));
        lblSupplier.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 1, true));
        lblSupplier.setName("lblSupplier"); // NOI18N
        lblSupplier.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                lblSupplierPropertyChange(evt);
            }
        });
        jPanel2.add(lblSupplier, new org.netbeans.lib.awtextra.AbsoluteConstraints(170, 10, 260, 20));

        txtSupplier.setFont(new java.awt.Font("Dialog", 0, 12));
        txtSupplier.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 1, true));
        txtSupplier.setDisabledTextColor(new java.awt.Color(153, 153, 153));
        txtSupplier.setName("txtSupplier"); // NOI18N
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
        jPanel2.add(txtSupplier, new org.netbeans.lib.awtextra.AbsoluteConstraints(110, 10, 60, 20));

        jLabel20.setFont(new java.awt.Font("Tahoma", 0, 12));
        jLabel20.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel20.setText("Supplier");
        jLabel20.setName("jLabel20"); // NOI18N
        jPanel2.add(jLabel20, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 10, 90, 20));

        jLabel21.setFont(new java.awt.Font("Tahoma", 0, 12));
        jLabel21.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel21.setText(":");
        jLabel21.setName("jLabel21"); // NOI18N
        jPanel2.add(jLabel21, new org.netbeans.lib.awtextra.AbsoluteConstraints(100, 85, 10, 20));

        jLabel22.setFont(new java.awt.Font("Tahoma", 0, 12));
        jLabel22.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel22.setText(":");
        jLabel22.setName("jLabel22"); // NOI18N
        jPanel2.add(jLabel22, new org.netbeans.lib.awtextra.AbsoluteConstraints(100, 60, 10, 20));

        jLabel7.setFont(new java.awt.Font("Tahoma", 0, 12));
        jLabel7.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel7.setText("No. Invoice");
        jLabel7.setName("jLabel7"); // NOI18N
        jPanel2.add(jLabel7, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 35, 90, 20));

        jLabel23.setFont(new java.awt.Font("Tahoma", 0, 12));
        jLabel23.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel23.setText(":");
        jLabel23.setName("jLabel23"); // NOI18N
        jPanel2.add(jLabel23, new org.netbeans.lib.awtextra.AbsoluteConstraints(100, 35, 10, 20));

        txtNoInvoice.setFont(new java.awt.Font("Dialog", 0, 12));
        txtNoInvoice.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 1, true));
        txtNoInvoice.setDisabledTextColor(new java.awt.Color(0, 0, 0));
        txtNoInvoice.setName("txtNoInvoice"); // NOI18N
        txtNoInvoice.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                txtNoInvoiceFocusLost(evt);
            }
        });
        txtNoInvoice.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtNoInvoiceKeyReleased(evt);
            }
        });
        jPanel2.add(txtNoInvoice, new org.netbeans.lib.awtextra.AbsoluteConstraints(110, 35, 220, 20));

        jLabel3.setFont(new java.awt.Font("Tahoma", 0, 12));
        jLabel3.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel3.setText("Tgl. Terima");
        jLabel3.setName("jLabel3"); // NOI18N
        jPanel2.add(jLabel3, new org.netbeans.lib.awtextra.AbsoluteConstraints(500, 30, 80, 20));

        txtDate.setFont(new java.awt.Font("Dialog", 0, 12));
        txtDate.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 1, true));
        txtDate.setDisabledTextColor(new java.awt.Color(0, 0, 0));
        txtDate.setEnabled(false);
        txtDate.setName("txtDate"); // NOI18N
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
        jPanel2.add(txtDate, new org.netbeans.lib.awtextra.AbsoluteConstraints(590, 30, 100, 20));

        jLabel17.setFont(new java.awt.Font("Tahoma", 0, 12));
        jLabel17.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel17.setText("Transaction #");
        jLabel17.setName("jLabel17"); // NOI18N
        jPanel2.add(jLabel17, new org.netbeans.lib.awtextra.AbsoluteConstraints(500, 10, 110, 20));

        jLabel9.setFont(new java.awt.Font("Tahoma", 0, 12));
        jLabel9.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel9.setText("Diterima Oleh");
        jLabel9.setName("jLabel9"); // NOI18N
        jPanel2.add(jLabel9, new org.netbeans.lib.awtextra.AbsoluteConstraints(500, 50, 80, 20));

        txtReceiptBy.setFont(new java.awt.Font("Dialog", 0, 12));
        txtReceiptBy.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 1, true));
        txtReceiptBy.setDisabledTextColor(new java.awt.Color(0, 0, 0));
        txtReceiptBy.setEnabled(false);
        txtReceiptBy.setName("txtReceiptBy"); // NOI18N
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
        jPanel2.add(txtReceiptBy, new org.netbeans.lib.awtextra.AbsoluteConstraints(590, 50, 120, 20));

        jLabel13.setFont(new java.awt.Font("Tahoma", 0, 12));
        jLabel13.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel13.setText(":");
        jLabel13.setName("jLabel13"); // NOI18N
        jPanel2.add(jLabel13, new org.netbeans.lib.awtextra.AbsoluteConstraints(580, 30, 10, 20));

        jLabel19.setFont(new java.awt.Font("Tahoma", 0, 12));
        jLabel19.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel19.setText(":");
        jLabel19.setName("jLabel19"); // NOI18N
        jPanel2.add(jLabel19, new org.netbeans.lib.awtextra.AbsoluteConstraints(580, 50, 10, 20));

        txtNoGR.setFont(new java.awt.Font("Dialog", 0, 14));
        txtNoGR.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 1, true));
        txtNoGR.setDisabledTextColor(new java.awt.Color(0, 0, 0));
        txtNoGR.setEnabled(false);
        txtNoGR.setName("txtNoGR"); // NOI18N
        txtNoGR.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                txtNoGRFocusLost(evt);
            }
        });
        txtNoGR.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                txtNoGRPropertyChange(evt);
            }
        });
        txtNoGR.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtNoGRKeyReleased(evt);
            }
            public void keyTyped(java.awt.event.KeyEvent evt) {
                txtNoGRKeyTyped(evt);
            }
        });
        txtNoGR.addVetoableChangeListener(new java.beans.VetoableChangeListener() {
            public void vetoableChange(java.beans.PropertyChangeEvent evt)throws java.beans.PropertyVetoException {
                txtNoGRVetoableChange(evt);
            }
        });
        jPanel2.add(txtNoGR, new org.netbeans.lib.awtextra.AbsoluteConstraints(590, 10, 130, 20));

        jLabel15.setFont(new java.awt.Font("Tahoma", 0, 12));
        jLabel15.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel15.setText(":");
        jLabel15.setName("jLabel15"); // NOI18N
        jPanel2.add(jLabel15, new org.netbeans.lib.awtextra.AbsoluteConstraints(580, 10, 10, 20));

        jLabel8.setFont(new java.awt.Font("Tahoma", 0, 12));
        jLabel8.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel8.setText("Tgl. ");
        jLabel8.setName("jLabel8"); // NOI18N
        jPanel2.add(jLabel8, new org.netbeans.lib.awtextra.AbsoluteConstraints(700, 70, 30, 20));

        jLabel27.setFont(new java.awt.Font("Tahoma", 0, 12));
        jLabel27.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel27.setText(":");
        jLabel27.setName("jLabel27"); // NOI18N
        jPanel2.add(jLabel27, new org.netbeans.lib.awtextra.AbsoluteConstraints(580, 90, 10, 20));

        txtTglPO.setFont(new java.awt.Font("Dialog", 0, 12));
        txtTglPO.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 1, true));
        txtTglPO.setDisabledTextColor(new java.awt.Color(0, 0, 0));
        txtTglPO.setEnabled(false);
        txtTglPO.setName("txtTglPO"); // NOI18N
        txtTglPO.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                txtTglPOFocusLost(evt);
            }
        });
        txtTglPO.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtTglPOKeyReleased(evt);
            }
        });
        jPanel2.add(txtTglPO, new org.netbeans.lib.awtextra.AbsoluteConstraints(730, 70, 90, 20));

        jLabel10.setFont(new java.awt.Font("Tahoma", 0, 12));
        jLabel10.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel10.setText("No. PO");
        jLabel10.setName("jLabel10"); // NOI18N
        jPanel2.add(jLabel10, new org.netbeans.lib.awtextra.AbsoluteConstraints(500, 70, 80, 20));

        jLabel28.setFont(new java.awt.Font("Tahoma", 0, 12));
        jLabel28.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel28.setText(":");
        jLabel28.setName("jLabel28"); // NOI18N
        jPanel2.add(jLabel28, new org.netbeans.lib.awtextra.AbsoluteConstraints(580, 70, 10, 20));

        txtNoPO.setFont(new java.awt.Font("Dialog", 0, 12));
        txtNoPO.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 1, true));
        txtNoPO.setDisabledTextColor(new java.awt.Color(0, 0, 0));
        txtNoPO.setName("txtNoPO"); // NOI18N
        jPanel2.add(txtNoPO, new org.netbeans.lib.awtextra.AbsoluteConstraints(590, 70, 110, 20));

        jLabel11.setText("Jth. Tempo  :"); // NOI18N
        jLabel11.setName("jLabel11"); // NOI18N
        jPanel2.add(jLabel11, new org.netbeans.lib.awtextra.AbsoluteConstraints(650, 90, 100, 21));

        txtTop.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        txtTop.setName("txtTop"); // NOI18N
        jPanel2.add(txtTop, new org.netbeans.lib.awtextra.AbsoluteConstraints(590, 90, 50, 20));

        jFJtTempo.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 1, true));
        jFJtTempo.setDisabledTextColor(new java.awt.Color(0, 0, 0));
        jFJtTempo.setEnabled(false);
        jFJtTempo.setFont(new java.awt.Font("Tahoma", 0, 12));
        jFJtTempo.setName("jFJtTempo"); // NOI18N
        jPanel2.add(jFJtTempo, new org.netbeans.lib.awtextra.AbsoluteConstraints(730, 90, 90, 20));

        jLabel12.setText("T.O.P"); // NOI18N
        jLabel12.setName("jLabel12"); // NOI18N
        jPanel2.add(jLabel12, new org.netbeans.lib.awtextra.AbsoluteConstraints(500, 90, 100, 21));

        jLabel24.setText(" hr."); // NOI18N
        jLabel24.setName("jLabel24"); // NOI18N
        jPanel2.add(jLabel24, new org.netbeans.lib.awtextra.AbsoluteConstraints(620, 90, 40, 21));

        jButton1.setText("+");
        jButton1.setMargin(new java.awt.Insets(2, 2, 2, 2));
        jButton1.setName("jButton1"); // NOI18N
        jButton1.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jButton1MouseClicked(evt);
            }
        });
        jPanel2.add(jButton1, new org.netbeans.lib.awtextra.AbsoluteConstraints(430, 10, 30, -1));

        getContentPane().add(jPanel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 70, 830, 120));

        jPanel4.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        jPanel4.setName("jPanel4"); // NOI18N
        jPanel4.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel31.setFont(new java.awt.Font("Tahoma", 0, 12));
        jLabel31.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel31.setText("Netto");
        jLabel31.setName("jLabel31"); // NOI18N
        jPanel4.add(jLabel31, new org.netbeans.lib.awtextra.AbsoluteConstraints(140, 70, 90, 20));

        txtTotalLine.setFont(new java.awt.Font("Dialog", 1, 12));
        txtTotalLine.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        txtTotalLine.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 1, true));
        txtTotalLine.setName("txtTotalLine"); // NOI18N
        txtTotalLine.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                txtTotalLinePropertyChange(evt);
            }
        });
        jPanel4.add(txtTotalLine, new org.netbeans.lib.awtextra.AbsoluteConstraints(230, 10, 110, 20));

        jLabel26.setFont(new java.awt.Font("Tahoma", 0, 12));
        jLabel26.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel26.setText("Line Total :");
        jLabel26.setName("jLabel26"); // NOI18N
        jPanel4.add(jLabel26, new org.netbeans.lib.awtextra.AbsoluteConstraints(140, 10, 90, 20));

        txtTotVat.setFont(new java.awt.Font("Dialog", 1, 12));
        txtTotVat.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        txtTotVat.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 1, true));
        txtTotVat.setName("txtTotVat"); // NOI18N
        txtTotVat.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                txtTotVatPropertyChange(evt);
            }
        });
        jPanel4.add(txtTotVat, new org.netbeans.lib.awtextra.AbsoluteConstraints(230, 30, 110, 20));

        txtNetto.setFont(new java.awt.Font("Dialog", 1, 12));
        txtNetto.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        txtNetto.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 1, true));
        txtNetto.setName("txtNetto"); // NOI18N
        txtNetto.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                txtNettoPropertyChange(evt);
            }
        });
        jPanel4.add(txtNetto, new org.netbeans.lib.awtextra.AbsoluteConstraints(230, 70, 110, 20));

        jLabel30.setFont(new java.awt.Font("Tahoma", 0, 12));
        jLabel30.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel30.setText("V.A.T");
        jLabel30.setName("jLabel30"); // NOI18N
        jPanel4.add(jLabel30, new org.netbeans.lib.awtextra.AbsoluteConstraints(140, 30, 90, 20));

        jLabel32.setFont(new java.awt.Font("Tahoma", 0, 12));
        jLabel32.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel32.setText("Biaya Lain");
        jLabel32.setName("jLabel32"); // NOI18N
        jPanel4.add(jLabel32, new org.netbeans.lib.awtextra.AbsoluteConstraints(140, 50, 80, 20));

        txtBiayaLain.setFont(new java.awt.Font("Dialog", 1, 12));
        txtBiayaLain.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtBiayaLain.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 1, true));
        txtBiayaLain.setDisabledTextColor(new java.awt.Color(153, 153, 153));
        txtBiayaLain.setName("txtBiayaLain"); // NOI18N
        txtBiayaLain.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                txtBiayaLainFocusLost(evt);
            }
        });
        txtBiayaLain.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtBiayaLainKeyReleased(evt);
            }
        });
        jPanel4.add(txtBiayaLain, new org.netbeans.lib.awtextra.AbsoluteConstraints(230, 50, 110, 20));

        getContentPane().add(jPanel4, new org.netbeans.lib.awtextra.AbsoluteConstraints(494, 464, 350, 95));

        txtBayar.setFont(new java.awt.Font("Dialog", 1, 12));
        txtBayar.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtBayar.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 1, true));
        txtBayar.setDisabledTextColor(new java.awt.Color(153, 153, 153));
        txtBayar.setName("txtBayar"); // NOI18N
        txtBayar.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                txtBayarFocusLost(evt);
            }
        });
        txtBayar.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtBayarKeyReleased(evt);
            }
        });
        getContentPane().add(txtBayar, new org.netbeans.lib.awtextra.AbsoluteConstraints(720, 564, 110, 20));

        jLabel29.setFont(new java.awt.Font("Tahoma", 0, 12));
        jLabel29.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel29.setText("BAYAR");
        jLabel29.setName("jLabel29"); // NOI18N
        getContentPane().add(jLabel29, new org.netbeans.lib.awtextra.AbsoluteConstraints(630, 564, 80, 20));

        jLabel1.setBackground(new java.awt.Color(204, 255, 255));
        jLabel1.setForeground(new java.awt.Color(0, 0, 153));
        jLabel1.setText("<html>\n &nbsp <b>F4 &nbsp &nbsp &nbsp &nbsp : </b> &nbsp Membuat Transaksi baru <br> \n &nbsp <b>F5 &nbsp &nbsp &nbsp &nbsp : </b> &nbsp Menyimpan Transaksi <br>\n &nbsp <b>F7 &nbsp &nbsp &nbsp &nbsp : </b> &nbsp Diskon Rupiah <br>\n &nbsp <b>F8 &nbsp &nbsp &nbsp &nbsp : </b> &nbsp PPN Rupiah <br> \n&nbsp <b>Del &nbsp &nbsp &nbsp : </b> &nbsp  Menghapus item pembelian  &nbsp  &nbsp\n &nbsp <b>F3 : </b> &nbsp Edit item pembelian <br>\n &nbsp <b>Insert : </b> &nbsp Menambah ItemTransaski dari PO yang sama<br>\n<hr>\n &nbsp <b>Catatan : </b> &nbsp Format Expired Date adalah 'dd/MM/yy' contoh '31/05/11'<br>\n</html>"); // NOI18N
        jLabel1.setVerticalAlignment(javax.swing.SwingConstants.TOP);
        jLabel1.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        jLabel1.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jLabel1.setName("jLabel1"); // NOI18N
        jLabel1.setOpaque(true);
        jLabel1.setVerticalTextPosition(javax.swing.SwingConstants.TOP);
        getContentPane().add(jLabel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 464, 480, 120));

        java.awt.Dimension screenSize = java.awt.Toolkit.getDefaultToolkit().getScreenSize();
        setBounds((screenSize.width-866)/2, (screenSize.height-624)/2, 866, 624);
    }// </editor-fold>//GEN-END:initComponents

    private void btnNewActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnNewActionPerformed
        udfNew();
}//GEN-LAST:event_btnNewActionPerformed

    private void btnSaveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSaveActionPerformed
        udfSave();
}//GEN-LAST:event_btnSaveActionPerformed

    private void btnPrintActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnPrintActionPerformed
        //printKwitansi(txtNoGR.getText(), false);
}//GEN-LAST:event_btnPrintActionPerformed

    private void btnCancelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCancelActionPerformed
        if(btnCancel.getText().equalsIgnoreCase("cancel")){
            if(isKoreksi)
                this.dispose();
            
            btnSave.setEnabled(false);
            btnNew.setEnabled(true);
            btnCancel.setText("Exit");
            btnCancel.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/Icon/buttons/exit-32.png")));
        }else{
            this.dispose();
        }
}//GEN-LAST:event_btnCancelActionPerformed

    private void txtSiteFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtSiteFocusLost
        // TODO add your handling code here:
}//GEN-LAST:event_txtSiteFocusLost

    private void txtSiteKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtSiteKeyReleased
        fn.lookup(evt, new Object[]{lblSite},
                "select kode_gudang, coalesce(deskripsi,'') as  nama_gudang " +
                "from gudang where kode_gudang||coalesce(deskripsi,'') ilike '%"+txtSite.getText()+"%' order by 1",
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
        String sQry="select kode_supplier, coalesce(nama_supplier,'') as nama_supplier, " +
                "coalesce(credit_term,0) as top, to_char(current_date+coalesce(credit_term,0), 'dd/MM/yyyy') as jt_tempo " +
                "from phar_supplier s " +
                "where kode_supplier||coalesce(nama_supplier,'') ilike '%" + txtSupplier.getText() + "%' " +
                "order by coalesce(nama_supplier,'') ";

        fn.lookup(evt, new Object[]{lblSupplier, txtTop, jFJtTempo}, sQry, txtSupplier.getWidth()+lblSupplier.getWidth()+18, 200);
}//GEN-LAST:event_txtSupplierKeyReleased

    private void txtNoInvoiceFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtNoInvoiceFocusLost
        // TODO add your handling code here:
}//GEN-LAST:event_txtNoInvoiceFocusLost

    private void txtNoInvoiceKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtNoInvoiceKeyReleased
        // TODO add your handling code here:
}//GEN-LAST:event_txtNoInvoiceKeyReleased

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

    private void txtNoGRFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtNoGRFocusLost
        // TODO add your handling code here:
}//GEN-LAST:event_txtNoGRFocusLost

    private void txtNoGRPropertyChange(java.beans.PropertyChangeEvent evt) {//GEN-FIRST:event_txtNoGRPropertyChange
        btnPrint.setEnabled(txtNoGR.getText().length()>0);
}//GEN-LAST:event_txtNoGRPropertyChange

    private void txtNoGRKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtNoGRKeyReleased
        // TODO add your handling code here:
}//GEN-LAST:event_txtNoGRKeyReleased

    private void txtNoGRKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtNoGRKeyTyped
        // TODO add your handling code here:
}//GEN-LAST:event_txtNoGRKeyTyped

    private void txtNoGRVetoableChange(java.beans.PropertyChangeEvent evt)throws java.beans.PropertyVetoException {//GEN-FIRST:event_txtNoGRVetoableChange
        // TODO add your handling code here:
}//GEN-LAST:event_txtNoGRVetoableChange

    private void txtTglPOFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtTglPOFocusLost
        // TODO add your handling code here:
}//GEN-LAST:event_txtTglPOFocusLost

    private void txtTglPOKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtTglPOKeyReleased
        // TODO add your handling code here:
}//GEN-LAST:event_txtTglPOKeyReleased

    private void txtTotalLinePropertyChange(java.beans.PropertyChangeEvent evt) {//GEN-FIRST:event_txtTotalLinePropertyChange
        // TODO add your handling code here:
}//GEN-LAST:event_txtTotalLinePropertyChange

    private void txtTotVatPropertyChange(java.beans.PropertyChangeEvent evt) {//GEN-FIRST:event_txtTotVatPropertyChange
        // TODO add your handling code here:
}//GEN-LAST:event_txtTotVatPropertyChange

    private void txtNettoPropertyChange(java.beans.PropertyChangeEvent evt) {//GEN-FIRST:event_txtNettoPropertyChange
        // TODO add your handling code here:
}//GEN-LAST:event_txtNettoPropertyChange

    private void txtBiayaLainFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtBiayaLainFocusLost
        // TODO add your handling code here:
}//GEN-LAST:event_txtBiayaLainFocusLost

    private void txtBiayaLainKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtBiayaLainKeyReleased
        // TODO add your handling code here:
}//GEN-LAST:event_txtBiayaLainKeyReleased

    private void txtBayarFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtBayarFocusLost
        // TODO add your handling code here:
}//GEN-LAST:event_txtBayarFocusLost

    private void txtBayarKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtBayarKeyReleased
        // TODO add your handling code here:
}//GEN-LAST:event_txtBayarKeyReleased

    private void formInternalFrameOpened(javax.swing.event.InternalFrameEvent evt) {//GEN-FIRST:event_formInternalFrameOpened
        udfInitForm();
    }//GEN-LAST:event_formInternalFrameOpened

    private void jButton1MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jButton1MouseClicked
        udfNewSupplier();
    }//GEN-LAST:event_jButton1MouseClicked

    private void formInternalFrameActivated(javax.swing.event.InternalFrameEvent evt) {//GEN-FIRST:event_formInternalFrameActivated
        jLabel16.setText(getTitle());
        if(isKoreksi && txtNoGR.getText().length()>0)
            udfLoadGRKoreksi();
    }//GEN-LAST:event_formInternalFrameActivated

    private JDesktopImage desktop;

    public void setDesktop(JDesktopImage d){
        this.desktop=d;
    }

    private void udfNewSupplier() {
        FrmSupplier fMaster=new FrmSupplier(conn);
        fMaster.setTitle("Supplier baru");
        fMaster.setConn(conn);
        fMaster.setBounds(0, 0, fMaster.getWidth(), fMaster.getHeight());
        desktop.add(fMaster, javax.swing.JLayeredPane.DEFAULT_LAYER);
        fMaster.setObjForm(this);
        fMaster.setVisible(true);
        try{
            //fMaster.setMaximum(true);
            fMaster.setSelected(true);
            fMaster.requestFocusInWindow();
        } catch(PropertyVetoException PO){

        }
    }


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnCancel;
    private javax.swing.JButton btnNew;
    private javax.swing.JButton btnPrint;
    private javax.swing.JButton btnSave;
    private javax.swing.JButton jButton1;
    private javax.swing.JFormattedTextField jFJtTempo;
    private javax.swing.JLabel jLabel1;
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
    private javax.swing.JLabel jLabel20;
    private javax.swing.JLabel jLabel21;
    private javax.swing.JLabel jLabel22;
    private javax.swing.JLabel jLabel23;
    private javax.swing.JLabel jLabel24;
    private javax.swing.JLabel jLabel26;
    private javax.swing.JLabel jLabel27;
    private javax.swing.JLabel jLabel28;
    private javax.swing.JLabel jLabel29;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel30;
    private javax.swing.JLabel jLabel31;
    private javax.swing.JLabel jLabel32;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JToolBar jToolBar1;
    private javax.swing.JLabel lblSite;
    private javax.swing.JLabel lblSupplier;
    private javax.swing.JTable table;
    private javax.swing.JTextField txtBayar;
    private javax.swing.JTextField txtBiayaLain;
    private javax.swing.JTextField txtDate;
    private javax.swing.JLabel txtNetto;
    private javax.swing.JTextField txtNoGR;
    private javax.swing.JTextField txtNoInvoice;
    private javax.swing.JTextField txtNoPO;
    private javax.swing.JTextField txtReceiptBy;
    private javax.swing.JTextField txtRemark;
    private javax.swing.JTextField txtSite;
    private javax.swing.JTextField txtSupplier;
    private javax.swing.JTextField txtTglPO;
    private javax.swing.JTextField txtTop;
    private javax.swing.JLabel txtTotVat;
    private javax.swing.JLabel txtTotalLine;
    // End of variables declaration//GEN-END:variables

     private void udfClearPO(){
        txtTglPO.setText("");
        ((DefaultTableModel)table.getModel()).setNumRows(0);

    }

    private void udfLoadItemFromPO() {
        if(txtNoPO.getText().trim().length()==0){
            udfClearPO();
            return;
        }

        try{
            ResultSet rs=conn.createStatement().executeQuery("select to_char(tanggal, 'dd/MM/yyyy') as tgl_po, coalesce(top,0) as top, " +
                    "to_char(tanggal+coalesce(top,0), 'dd/MM/yyyy') as tgl_jt_tempo from r_po  " +
                    "where no_po='"+txtNoPO.getText()+"'");
            if(rs.next()){
                txtTglPO.setText(rs.getString("tgl_po"));
                txtTop.setText(rs.getString("top"));
                jFJtTempo.setText(rs.getString("tgl_jt_tempo"));
            }
            rs.close();

            String s="select * from fn_r_gr_item_sisa_po('"+txtNoPO.getText()+"') as (no_po varchar, kode_item varchar," +
                    "nama_item varchar, qty_order numeric, sisa numeric, on_receipt numeric, uom_po varchar, " +
                    "harga double precision, disc double precision, ppn double precision, konv int, uom_kecil varchar, jml_kecil numeric, " +
                    "on_hand numeric, is_disc_rp boolean, is_tax_rp boolean)";

            rs=conn.createStatement().executeQuery(s);
            ((DefaultTableModel)table.getModel()).setNumRows(0);
            while(rs.next()){
                ((DefaultTableModel)table.getModel()).addRow(new Object[]{
                    rs.getString("kode_item"),
                    rs.getString("nama_item"),
                    rs.getString("uom_po"),
                    "",
                    rs.getDouble("qty_order"),
                    rs.getDouble("sisa"),
                    rs.getDouble("on_receipt"),
                    rs.getDouble("harga"),
                    rs.getDouble("disc"),
                    rs.getDouble("ppn"),
                    rs.getDouble("konv"),
                    rs.getBoolean("is_disc_rp"),
                    rs.getBoolean("is_tax_rp"),
                });
            }
            //table.setModel((DefaultTableModel)fn.autoResizeColWidth(table, (DefaultTableModel)table.getModel()).getModel());
            if(table.getRowCount()>0)
                table.changeSelection(0, 4, false, false);

            else{
                JOptionPane.showMessageDialog(this, "Item Sisa PO tidak ditemukan! Silakan cek No. PO anda!");
                //txtNoPO.requestFocus();
                return;
            }
            rs.close();
        }catch(SQLException se){
            JOptionPane.showMessageDialog(this, se.getMessage());
        }
    }

    void setConn(Connection conn) {
        this.conn=conn;
    }

    void setFlagPO(boolean b) {
        this.withPO=b;
    }

    private void udfSetSubTotalItem(){
        int iRow=table.getSelectedRow();
        if(iRow<0) return;
        
        TableColumnModel col=table.getColumnModel();
        
        
        double subTotal=fn.udfGetDouble(table.getValueAt(iRow, col.getColumnIndex("Qty")))*
                        fn.udfGetDouble(table.getValueAt(iRow, col.getColumnIndex("Harga")));
        boolean isDiscRp=fn.udfGetDouble(table.getValueAt(iRow, col.getColumnIndex("Disc(Rp)"))) > 0;
        
        subTotal=isDiscRp? subTotal-fn.udfGetDouble(table.getValueAt(iRow, col.getColumnIndex("Disc(Rp)"))): 
                subTotal*(1-fn.udfGetDouble(table.getValueAt(iRow, col.getColumnIndex("Disc%")))/100);
        //subTotal=chkPPnRp.isSelected()? subTotal*(1+fn.udfGetDouble(txtPPn.getText())/100): subTotal+fn.udfGetDouble(txtPPn.getText());
        table.setValueAt(subTotal, iRow, col.getColumnIndex("Sub Total"));
    }

    public void setKoreksi(boolean b) {
        this.isKoreksi=b;

    }

    public void setNoTrx(String sNoKoreksi) {
        txtNoGR.setText(sNoKoreksi);
    }

    public class MyKeyListener extends KeyAdapter {
        @Override
        public void keyTyped(java.awt.event.KeyEvent evt) {
//          if(evt.getSource().equals(tblPR)){
//              if(tblPR.getSelectedColumn()!=tblPR.getColumnModel().getColumnIndex("Qty") && stMinus){
//                  evt.consume();
//                  return;
//              }
//          }
            if(evt.getSource() instanceof JTextField &&
                    ((JTextField)evt.getSource()).getName()!=null &&
              ((JTextField)evt.getSource()).getName().equalsIgnoreCase("textEditor") && stMinus)
                ((JTextField)evt.getSource()).setEditable(table.getSelectedColumn()==table.getColumnModel().getColumnIndex("Qty"));

          if (evt.getSource() instanceof JTextField &&
              ((JTextField)evt.getSource()).getName()!=null &&
              ((JTextField)evt.getSource()).getName().equalsIgnoreCase("textEditor") &&
              !(table.getSelectedColumn()==table.getColumnModel().getColumnIndex("Expired"))) {

//              if((tblPR.getSelectedColumn()!=tblPR.getColumnModel().getColumnIndex("Qty"))){
//                  if(stMinus){
//                      evt.consume();
//                      return;
//                  }
//              }
              char c = evt.getKeyChar();
              if (!((c >= '0' && c <= '9')) &&
                    (c != KeyEvent.VK_BACK_SPACE) &&
                    (c != KeyEvent.VK_DELETE) &&
                    (c != KeyEvent.VK_ENTER) &&
                    (c != '-')) {
                    getToolkit().beep();
                    evt.consume();
                    return;
              }
           }
          
        }

        @Override
        public void keyPressed(KeyEvent evt) {
            Component ct = KeyboardFocusManager.getCurrentKeyboardFocusManager().getPermanentFocusOwner();
            int keyKode = evt.getKeyCode();
            switch(keyKode){
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
                    if(!(evt.getSource() instanceof JTable))
                    {
                        Component c = findPrevFocus();
                        c.requestFocus();
                    }
                    break;
                }
                case KeyEvent.VK_ESCAPE:{
                    if(JOptionPane.showConfirmDialog(null,"Anda Yakin Untuk Keluar?",
                            MainForm.sNamaUsaha,JOptionPane.YES_NO_OPTION)==JOptionPane.YES_OPTION){
                        dispose();
                    }
                    break;
                }
                case KeyEvent.VK_F5:{
                    udfSave();
                    break;
                }
                case KeyEvent.VK_DELETE:{
                    if(evt.getSource().equals(table) && table.getSelectedRow()>=0){
                        int iRow[]= table.getSelectedRows();
                        int rowPalingAtas=iRow[0];

//                        if(JOptionPane.showConfirmDialog(FrmPO.this,
//                                "Item '"+tblPR.getValueAt(iRow, 3).toString()+"' dihapus dari PO?", "Confirm", JOptionPane.YES_NO_OPTION)==JOptionPane.NO_OPTION)
//                            return;

                        for (int a=0; a<iRow.length; a++){
                            ((DefaultTableModel)table.getModel()).removeRow(table.getSelectedRow());
                        }

                        if(table.getRowCount()>0 && rowPalingAtas<table.getRowCount()){
                            //if(tblPR.getSelectedRow()>0)
                                table.setRowSelectionInterval(rowPalingAtas, rowPalingAtas);
                        }
                        else{
                            if(table.getRowCount()>0)
                                table.setRowSelectionInterval(rowPalingAtas-1, rowPalingAtas-1);
                            else
                                txtSupplier.requestFocus();

                        }
                        if(table.getSelectedRow()>=0)
                            table.changeSelection(table.getSelectedRow(), 0, false, false);
                    }
                    break;
                    
                }
                case KeyEvent.VK_INSERT:{
                    if(withPO)
                        udfLookupItemPO();
                    else
                        udfLookupItemNonPO(evt);

                    break;
                }

            }
        }

        @Override
        public void keyReleased(KeyEvent evt){
            if(evt.getSource().equals(table) && table.getSelectedRow()>=0){
//                if(evt.getKeyCode()==java.awt.event.KeyEvent.VK_C && evt.getModifiersEx()==java.awt.event.KeyEvent.CTRL_DOWN_MASK){
//                    if(fn.udfGetInt(table.getValueAt(table.getSelectedRow(), table.getColumnModel().getColumnIndex("Qty")))<=1){
//                        JOptionPane.showMessageDialog(aThis, "Qty on Receipt <= 1 baris tidak bisa dicopy!");
//                        tableCopyRow=null;
//                        return;
//                    }
//                    copyTable();
//                }
//                if(evt.getKeyCode()==java.awt.event.KeyEvent.VK_V && evt.getModifiersEx()==java.awt.event.KeyEvent.CTRL_DOWN_MASK){
//                    if(tableCopyRow==null){
//                        JOptionPane.showMessageDialog(FrmGR.this, "Tabel item belum di-copy! Klik pada item kemudian tekan Ctrl+C terlebih dulu!");
//                        return;
//                    }
//                    pasteTable();
//                }
            }else if(evt.getSource().equals(txtTop))
                setDueDate();

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
    
    private void udfLookupItemPO(){
        DLgLookup d1=new DLgLookup(JOptionPane.getFrameForComponent(aThis), true);
        String sItem="";
        for(int i=0; i< table.getRowCount(); i++){
            if(table.getValueAt(i, 0)!=null)
            sItem+=(sItem.length()==0? "" : ",") +"'"+table.getValueAt(i, 0).toString()+"'";
        }

        String s="select * from (" +
                "select * from fn_r_gr_item_sisa_po('"+txtNoPO.getText()+"') as (no_po varchar, kode_item varchar," +
                "nama_item varchar, qty_order numeric, sisa numeric, on_receipt numeric, uom_po varchar, " +
                "harga double precision, disc double precision, ppn double precision, konv int, uom_kecil varchar, jml_kecil numeric, on_hand numeric)" +
                (sItem.length()>0? " where  kode_item not in("+sItem+") " : "")+
                "order by nama_item )x ";

        //System.out.println(s);

        d1.setTitle("Lookup Item from PO");
        d1.udfLoad(conn, s, "(kode_item||nama_item)", null);

        d1.setVisible(true);

        //System.out.println("Kode yang dipilih" +d1.getKode());
        if(d1.getKode().length()>0){
            TableColumnModel col=d1.getTable().getColumnModel();
            JTable tbl=d1.getTable();
            int iRow = tbl.getSelectedRow();

            ((DefaultTableModel)table.getModel()).addRow(new Object[]{
                tbl.getValueAt(iRow, col.getColumnIndex("kode_item")).toString(),
                tbl.getValueAt(iRow, col.getColumnIndex("nama_item")).toString(),
                fn.udfGetDouble(tbl.getValueAt(iRow, col.getColumnIndex("qty_order"))),
                fn.udfGetDouble(tbl.getValueAt(iRow, col.getColumnIndex("sisa"))),
                fn.udfGetDouble(tbl.getValueAt(iRow, col.getColumnIndex("on_receipt"))),
                tbl.getValueAt(iRow, col.getColumnIndex("uom_po")).toString(),
                "",
                fn.udfGetDouble(tbl.getValueAt(iRow, col.getColumnIndex("harga"))),
                fn.udfGetDouble(tbl.getValueAt(iRow, col.getColumnIndex("disc"))),
                fn.udfGetDouble(tbl.getValueAt(iRow, col.getColumnIndex("ppn"))),
                fn.udfGetDouble(tbl.getValueAt(iRow, col.getColumnIndex("konv"))),
                tbl.getValueAt(iRow, col.getColumnIndex("uom_kecil")).toString(),
                fn.udfGetDouble(tbl.getValueAt(iRow, col.getColumnIndex("jml_kecil"))),
                fn.udfGetDouble(tbl.getValueAt(iRow, col.getColumnIndex("on_hand")))
            });

            table.setRowSelectionInterval(table.getRowCount()-1, table.getRowCount()-1);
            table.requestFocusInWindow();
            table.changeSelection(table.getRowCount()-1, table.getColumnModel().getColumnIndex("Qty"), false, false);
        }
    }

    

    private void udfLookupItemNonPO(KeyEvent evt){
        if(txtSupplier.getText().trim().length()==0){
            JOptionPane.showMessageDialog(this, "Silakan isi Supplier terlebih dulu!");
            if(!txtSupplier.isFocusOwner())
                txtSupplier.requestFocus();
            return;
        }
        lookupItem.setAlwaysOnTop(true);
        lookupItem.setSrcTable(table, table.getColumnModel().getColumnIndex("Qty"));
        lookupItem.setKeyEvent(evt);
        lookupItem.setObjForm(this);
        lookupItem.setVisible(true);
        lookupItem.clearText();
        lookupItem.requestFocusInWindow();
        if(lookupItem.getKodeBarang().length()>0){
            if(table.getRowCount()==0 || (table.getValueAt(table.getRowCount()-1, 0)!=null && !table.getValueAt(table.getRowCount()-1, 0).toString().trim().equalsIgnoreCase("")) )
                ((DefaultTableModel)table.getModel()).addRow(new Object[]{
                    "", "", "", 1, "", 0, 0, 0, 0, 0, 1
                });
            table.requestFocusInWindow();
            table.setRowSelectionInterval(table.getRowCount()-1, table.getRowCount()-1);
            table.setValueAt(lookupItem.getKodeBarang(), table.getRowCount()-1, 0);
            table.changeSelection(table.getRowCount()-1, table.getColumnModel().getColumnIndex("Expired"), false, false);

        }
    }
    
    private void udfInitForm(){
        table.getTableHeader().setFont(new Font("Tahoma", 0, 12));
        table.getTableHeader().setReorderingAllowed(false);
        table.getTableHeader().setResizingAllowed(false);
        table.getTableHeader().setBackground(Color.yellow);
        table.getColumn("Product ID").setPreferredWidth(80);
        table.getColumn("Keterangan").setPreferredWidth(200);
        table.getColumn("Satuan").setPreferredWidth(50);
        table.getColumn("Expired").setPreferredWidth(60);
        table.getColumn("Qty").setPreferredWidth(50);
        table.getColumn("Harga").setPreferredWidth(60);
        table.getColumn("Disc%").setPreferredWidth(50);
        table.getColumn("PPn%").setPreferredWidth(50);
        table.getColumn("Sub Total").setPreferredWidth(100);
        table.getColumn("Konv").setPreferredWidth(50);
        table.setRowHeight(22);
        fn=new GeneralFunction(conn);
        fn.addKeyListenerInContainer(jToolBar1, kListener, txtFocusListener);
        fn.addKeyListenerInContainer(jPanel2, kListener, txtFocusListener);
        fn.addKeyListenerInContainer(jPanel4, kListener, txtFocusListener);
        fn.removeComboUpDown(cmbSatuan);
        
        txtBayar.addKeyListener(kListener); txtBayar.addFocusListener(txtFocusListener);
        lookupItem.setConn(conn);
        table.addKeyListener(kListener);
        try{
            ResultSet rs=conn.createStatement().executeQuery("select to_char(current_date, 'dd/MM/yyyy')");
            rs.next();
            txtDate.setText(rs.getString(1));

            rs.close();
        }catch(SQLException se){
            JOptionPane.showMessageDialog(this, se.getMessage());
        }

        udfNew();
        txtSite.setText(MainForm.sKodeGudang);
        lblSite.setText(MainForm.sNamaGudang);
        txtReceiptBy.setText(MainForm.sUserName);
        txtNoGR.setEnabled(isKoreksi);

        //setStatusMinus();
        Runnable doRun = new Runnable() {
            public void run() {
                if(!isKoreksi)
                    txtSupplier.requestFocusInWindow();
                else
                    txtNoGR.requestFocusInWindow();
            }
        };
        SwingUtilities.invokeLater(doRun);
        txtNoPO.setVisible(withPO);     jLabel10.setVisible(withPO);    jLabel28.setVisible(withPO);
        txtTglPO.setVisible(withPO);    jLabel8.setVisible(withPO);     jLabel27.setVisible(withPO);

    }

    private void setStatusMinus(){
        //txtNoGR.setEnabled(stMinus);
        txtNoPO.setEnabled(!stMinus);
        txtNoInvoice.setEnabled(!stMinus);
        //txtSite.setEnabled(!stMinus);
        txtSupplier.setEnabled(!stMinus);
        jLabel17.setText(stMinus? "Last TTB#": "Receipt#");
        jLabel16.setText(stMinus? "Pembelian (Minus)": "Pembelian");
    }

    private void udfLoadGRKoreksi(){
        if(txtNoGR.getText().trim().isEmpty()){
            udfNew();
            return;
        }

        String s="select coalesce(h.no_sj_supplier,'') as no_receipt, coalesce(h.kode_supplier,'') as kode_supp, coalesce(s.nama_supplier,'') as nama_supp, " +
                "coalesce(h.kode_gudang,'') as kode_gudang, coalesce(g.deskripsi,'') as nama_gudang, " +
                "to_char(h.tanggal, 'dd/MM/yyyy') as tgl_gr, " +
                "coalesce(h.no_po,'') as no_po, coalesce(to_char(po.tanggal, 'dd/MM/yyyy'), '') as tgl_po ," +
                "coalesce(h.keterangan,'') as remark, coalesce(h.user_trx,'') as user_ins, " +
                "coalesce(h.top,0) as top, to_char(h.tanggal::date+ coalesce(h.top,0), 'dd/MM/yyyy') as jt_tempo,"
                + "coalesce(h.biaya_lain,0) as biaya_lain " +
                "from good_receipt h " +
                "left join phar_supplier s on s.kode_supplier=h.kode_supplier " +
                "left join gudang g on g.kode_gudang=h.kode_gudang " +
                "left join purchase_order po on po.po_code=h.no_po " +
                "where h.no_penerimaan='"+txtNoGR.getText()+"' ";

        try{
            ResultSet rs=conn.createStatement().executeQuery(s);
            if(rs.next()){
                txtSupplier.setText(rs.getString("kode_supp"));
                lblSupplier.setText(rs.getString("nama_supp"));
                txtDate.setText(rs.getString("tgl_gr"));
                txtSite.setText(rs.getString("kode_gudang"));
                lblSite.setText(rs.getString("nama_gudang"));
                txtNoPO.setText(rs.getString("no_po"));
                txtTglPO.setText(rs.getString("tgl_po"));
                txtNoInvoice.setText(rs.getString("no_receipt"));
                txtRemark.setText(rs.getString("remark"));
                txtReceiptBy.setText(rs.getString("user_ins"));
                txtTop.setText(rs.getString("top"));
                jFJtTempo.setText(rs.getString("jt_tempo"));
                txtBiayaLain.setText(fn.dFmt.format(rs.getDouble("biaya_lain")));
                rs.close();
                s="select d.item_code, coalesce(i.item_name,'') as nama_item, coalesce(to_char(d.exp_date, 'dd/MM/yy'),'') as exp_date, " +
                        "coalesce(d.uom_beli,'') as satuan, coalesce(d.qty_beli,0) as qty, coalesce(d.konv,1) as konv, " +
                        "coalesce(d.harga_beli,0) as unit_price, coalesce(d.disc,0) as disc, coalesce(d.ppn,0) as tax, " +
                        "coalesce(d.harga_beli,0)*coalesce(d.qty_beli,0)- case when is_disc_rp=true then coalesce(d.disc,0) else (coalesce(d.harga_beli,0)*coalesce(d.qty_beli,0))/100*coalesce(d.disc,0) end as sub_total," +
                        "coalesce(d.is_disc_rp, false) as is_disc_rp, coalesce(d.is_ppn_rp, false) as is_tax_rp " +
                        "from good_receipt_Detail d " +
                        "inner join barang i on i.item_code=d.item_code " +
                        "where d.no_penerimaan='"+txtNoGR.getText()+"' " +
                        "order by serial_no";

                rs=conn.createStatement().executeQuery(s);
                ((DefaultTableModel)table.getModel()).setNumRows(0);
                while(rs.next()){
                    ((DefaultTableModel)table.getModel()).addRow(new Object[]{
                        rs.getString("item_code"),
                        rs.getString("nama_item"),
                        rs.getString("satuan"),
                        rs.getString("exp_date"),
                        rs.getDouble("qty"),
                        rs.getDouble("unit_price"),
                        rs.getDouble("disc"),
                        rs.getDouble("tax"),
                        rs.getDouble("sub_total"),
                        rs.getDouble("konv"),
                        rs.getBoolean("is_disc_rp"),
                        rs.getBoolean("is_tax_rp")
                    });
                }


                //table.setModel((DefaultTableModel)fn.autoResizeColWidth(table, (DefaultTableModel)table.getModel()).getModel());
                if(table.getRowCount()>0)
                    table.changeSelection(0, 4, false, false);
            }else{
                JOptionPane.showMessageDialog(this, "No. Transaksi tidak ditemukan!");
                udfNew();
                //txtNoGR.grabFocus();
            }
            rs.close();
        }catch(SQLException se){
            JOptionPane.showMessageDialog(this, se.getMessage());
        }
    }

    private String udfLoadComboKonversi(String sKode){
        String sMsg="";
        try{
            if(sKode.length()>0){
                String sQry="select coalesce(item_name,'') as nama_item, " +
                         "coalesce(satuan_kecil,'') as unit, " +
                         "coalesce(satuan_besar,'') as unit2  " +
                         "from barang i " +
                         "where i.item_code||coalesce(i.barcode,'')='"+sKode+"'";

                System.out.println(sQry);
                 ResultSet rs=conn.createStatement().executeQuery(sQry);
                 cmbSatuan.removeAllItems();
                 if(rs.next()){
                     if(rs.getString("unit").length()>0) cmbSatuan.addItem(rs.getString("unit"));
                     if(rs.getString("unit2").length()>0) cmbSatuan.addItem(rs.getString("unit2"));
                 }
                 rs.close();
            }
        }catch(SQLException se){
            JOptionPane.showMessageDialog(this, se.getMessage());
        }
        return sMsg;
    }

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

                if(e.getSource().equals(txtNoPO) && !fn.isListVisible() && !isKoreksi)
                    udfLoadItemFromPO();
                else if(e.getSource().equals(txtNoGR))
                    udfLoadGRKoreksi();
                else if(e.getSource().equals(txtTop))
                    setDueDate();
                else if(e.getSource().equals(txtBayar))
                    txtBayar.setText(fn.dFmt.format(fn.udfGetDouble(txtBayar.getText())));

           }
        }


    } ;

    private String udfLoadItem(String sKode){
        String sMsg="";
        try{
            if(sKode.length()>0){
                String sQry="select coalesce(item_name,'') as nama_item, " +
                         "coalesce(satuan_kecil,'') as unit, " +
                         "coalesce(satuan_besar,'') as unit2, coalesce(konversi,1) as konversi, " +
                         "case  when s.uom_alt=i.satuan_besar then i.satuan_besar " +
                         "      else coalesce(satuan_kecil,'') end as uom_beli, " +
                         "case  when s.uom_alt=i.satuan_besar then coalesce(i.konversi,1) " +
                         "      else 1 end as konv_beli, " +
                         "coalesce(s.price,0) as unit_price, coalesce(s.disc,0) as disc, coalesce(s.vat,0) as vat, " +
                         "coalesce(s.is_disc_rp, false) as is_disc_rp, coalesce(s.is_tax_rp, false) as is_tax_rp " +
                         "from barang i " +
                         "left join supplier_barang s on s.kode_barang=i.item_code and s.kode_supplier='"+txtSupplier.getText()+"' " +
                         "where i.item_code='"+sKode+"' or coalesce(i.barcode,'')='"+sKode+"' " +
                         "order by coalesce(s.convertion,0)  desc limit 1";

                //System.out.println(sQry);
                 ResultSet rs=conn.createStatement().executeQuery(sQry);
                 cmbSatuan.removeAllItems();
                 TableColumnModel col=table.getColumnModel();
                 int iRow=table.getSelectedRow();
                 
                 if(rs.next()){
                     table.setValueAt(rs.getString("nama_item"), iRow, col.getColumnIndex("Keterangan"));
                     if(rs.getString("unit").length()>0) cmbSatuan.addItem(rs.getString("unit"));
                     if(rs.getString("unit2").length()>0) cmbSatuan.addItem(rs.getString("unit2"));
                     cmbSatuan.setSelectedItem(rs.getString("uom_beli"));
                     table.setValueAt(rs.getString("uom_beli"), iRow, col.getColumnIndex("Satuan"));
                     table.setValueAt(rs.getDouble("unit_price"), iRow, col.getColumnIndex("Harga"));
                     if(rs.getBoolean("is_disc_rp")==true)
                         table.setValueAt(rs.getDouble("disc"), iRow, col.getColumnIndex("Disc(Rp)"));
                     else
                         table.setValueAt(rs.getDouble("disc"), iRow, col.getColumnIndex("Disc%"));
                     
                     table.setValueAt(rs.getDouble("vat"), iRow, col.getColumnIndex("PPn%"));
                     table.setValueAt(rs.getDouble("konv_beli"), iRow, col.getColumnIndex("Konv"));
                     
                     udfLoadKonversi(cmbSatuan.getSelectedItem().toString());
                     udfSetSubTotalItem();

                 }else{
                     //JOptionPane.showMessageDialog(aThis, "Item tidak ditemukan!");
                     sMsg="Item tidak ditemukan!";
                 }
                 rs.close();
            }
        }catch(SQLException se){
            JOptionPane.showMessageDialog(this, se.getMessage());
        }
        return sMsg;
    }
    
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
            JOptionPane.showMessageDialog(aThis, ex.getMessage());
        }

        return fText;
    }
    
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
            if(vColIndex==table.getColumnModel().getColumnIndex("Expired"))
                text=fText;
            else
                text=ustTextField;
            
            text.setName("textEditor");

            if(stMinus)
                text.setEditable(vColIndex==table.getColumnModel().getColumnIndex("Qty"));

            if(vColIndex==table.getColumnModel().getColumnIndex("Qty")||
                    vColIndex==table.getColumnModel().getColumnIndex("Harga")||
                    vColIndex==table.getColumnModel().getColumnIndex("Disc%")||
                    vColIndex==table.getColumnModel().getColumnIndex("Disc(Rp)")||
                    vColIndex==table.getColumnModel().getColumnIndex("PPn%")){
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
                if(col==table.getColumnModel().getColumnIndex("Expired")){
                    if(!fn.validateDate(((JTextField)text).getText(), true, "dd/MM/yy")){
                        JOptionPane.showMessageDialog(JOptionPane.getFrameForComponent(aThis),
                                "Silakan isikan format tanggal dengan 'dd/MM/yy'\n" +
                                "Contoh: 31/12/19");
                                
                        retVal=table.getValueAt(row, col).toString();
                        table.requestFocusInWindow();
                        table.changeSelection(row, table.getColumnModel().getColumnIndex("Expired"), false, false);
                    }else
                        retVal = ((JTextField)text).getText();

                }else if(withPO && col==table.getColumnModel().getColumnIndex("On Receipt")){
                    double sisaPR=fn.udfGetDouble(table.getValueAt(row, table.getColumnModel().getColumnIndex("Sisa Order")));

//                    if(withPO && fn.udfGetDouble(((JTextField)text).getText())> sisaPR ){
//                        JOptionPane.showMessageDialog(FrmGR.this, "Jumlah On Receipt melebihi Qty Sisa PO \nQuantity PO adalah : "+
//                                fn.dFmt.format(sisaPR));
//                        o=fn.udfGetDouble(table.getValueAt(row, table.getColumnModel().getColumnIndex("On Receipt")));
//                        return o;
//                    }
                    retVal = fn.udfGetDouble(((JTextField)text).getText());
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

    Vector tableCopyRow;
}
