/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * FrmTrxPenjualan.java
 *u
 * Created on 04 Feb 11, 19:39:06
 */

package penjualan;

import apotek.DlgDokter;
import apotek.DlgLookupItemJual;
import apotek.DlgPasien;
import apotek.JDesktopImage;
import main.MainForm;
import apotek.PrintPenjualan2;
import comboBox.AutoCompletion;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.FocusTraversalPolicy;
import java.awt.KeyboardFocusManager;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.print.PrinterJob;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import javax.print.DocFlavor;
import javax.print.PrintService;
import javax.print.PrintServiceLookup;
import javax.print.attribute.HashPrintRequestAttributeSet;
import javax.print.attribute.HashPrintServiceAttributeSet;
import javax.print.attribute.PrintRequestAttributeSet;
import javax.print.attribute.PrintServiceAttributeSet;
import javax.swing.JComponent;
import javax.swing.JFormattedTextField;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableModel;
import javax.swing.text.MaskFormatter;
import main.GeneralFunction;
import main.SysConfig;


/**
 *
 * @author cak-ust
 */
public class FrmPenjualan2 extends javax.swing.JFrame {
    private Connection conn;
    private GeneralFunction fn;
    private Component aThis;
    private DlgLookupItemJual lookupItem =new DlgLookupItemJual(this, true);
    private MyKeyListener kListener=new MyKeyListener();
    private boolean lockHarga=true;
    private boolean isKoreksi=false;
    ArrayList lstGudang=new ArrayList();
    private boolean stItemUpd=false;
    private String sNoTrx;
    private JDesktopImage desktop;
    private Object srcForm;

    /** Creates new form FrmTrxPenjualan */
    public FrmPenjualan2() {
        initComponents();
        setIconImage(MainForm.imageIcon);
        
        //initConn();
       table.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).
        put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0),
            "selectNextColumnCell");

        //AutoCompleteDecorator.decorate(cmbSatuan);

        cmbSatuan.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                if(cmbSatuan.getSelectedIndex()>=0 && conn!=null)
                    udfLoadKonversi(cmbSatuan.getSelectedItem().toString());

            }
        });
        table.getModel().addTableModelListener(new TableModelListener() {

            public void tableChanged(TableModelEvent e) {
                udfSetTotal();
            }
        });
        table.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            public void valueChanged(ListSelectionEvent e) {
                udfClearItem();
            }
        });
        aThis=this;
        //cmbGudang.setEditable(true);
//        new AutoCompletion(cmbGudang) ;
        
        udfNew();
    }

    public void setSrcForm(Object frm){
        srcForm=frm;
    }

    private void udfSetTotal(){
        lblTotal.setText("0.00");
        double dTotal=0;
        for(int i=0; i<table.getRowCount(); i++){
            dTotal+=fn.udfGetDouble(table.getValueAt(i, table.getColumnModel().getColumnIndex("Sub Total")));
        }
        lblTotal.setText(fn.dFmt.format(dTotal));
    }

    public void setLockHarga(boolean b){
        this.lockHarga=b;
    }
    
    public void setConn(Connection con){
        this.conn=con;
    }

    public void setCustomer(String sKode, String sNama){
        txtCustomer.setText(sKode);
        txtNamaCustomer.setText(sNama);
    }

    public void setDokter(String sKode, String sNama){
        txtDokter.setText(sKode);
        txtNamaDokter.setText(sNama);
    }
    private void udfInitForm(){
        table.getColumn("ProductID").setPreferredWidth(txtKode.getWidth());
        table.getColumn("Nama Barang").setPreferredWidth(lblItem.getWidth());
        table.getColumn("Satuan").setPreferredWidth(cmbSatuan.getWidth());
        table.getColumn("Qty").setPreferredWidth(txtQty.getWidth());
        table.getColumn("Harga").setPreferredWidth(txtHarga.getWidth());
        table.getColumn("Disc").setPreferredWidth(txtDisc.getWidth());
        table.getColumn("PPn").setPreferredWidth(txtPPn.getWidth());
        table.getColumn("Sub Total").setPreferredWidth(lblSubTotal.getWidth());
        table.getColumn("Konv").setPreferredWidth(lblKonv.getWidth());
        table.getColumn("Disc%").setMinWidth(0);table.getColumn("Disc%").setMaxWidth(0);table.getColumn("Disc%").setPreferredWidth(0);
        table.getColumn("PPn%").setMinWidth(0);table.getColumn("PPn%").setMaxWidth(0);table.getColumn("PPn%").setPreferredWidth(0);


        table.getTableHeader().setResizingAllowed(false);

        table.setRowHeight(22);
        fn=new GeneralFunction(conn);
        fn.addKeyListenerInContainer(jPanel1, kListener, txtFocusListener);
        fn.addKeyListenerInContainer(jPanel2, kListener, txtFocusListener);
        fn.addKeyListenerInContainer(jToolBar1, kListener, txtFocusListener);
//        fn.removeComboUpDown(cmbGudang);
        fn.removeComboUpDown(cmbCustPembayaran);
        fn.removeComboUpDown(cmbSatuan);
        table.addKeyListener(kListener);
        txtHarga.setEnabled(!lockHarga);

        lookupItem.setConn(conn);
        fn.setConn(conn);
//        cmbGudang.setSelectedItem(MainForm.sNamaGudang);
        MaskFormatter fmttgl = null;
        try {
            fmttgl = new MaskFormatter("##/##/####");
        } catch (java.text.ParseException e) {}

        JFormattedTextField jFDate1 = new JFormattedTextField(fmttgl);
        jFJtTempo.setFormatterFactory(jFDate1.getFormatterFactory());

        try{
            ResultSet rs=conn.createStatement().executeQuery(
                    "select kode_gudang, coalesce(deskripsi,'') as nama_gudang, " +
                    "to_char(current_date, 'dd/MM/yyyy') as tgl " +
                    "from gudang order by 1");
//            lstGudang.clear();
//            cmbGudang.removeAllItems();

            while(rs.next()){
//                lstGudang.add(rs.getString(1));
//                cmbGudang.addItem(rs.getString(2));
                lblTgl.setText(rs.getString("tgl"));
                jFJtTempo.setText(rs.getString("tgl"));
                jFJtTempo.setValue(rs.getString("tgl"));
            }
            rs.close();

        }catch(SQLException se){
            JOptionPane.showMessageDialog(this, se.getMessage());
        }
        cmbCustPembayaranItemStateChanged(null);
        if(isKoreksi && txtNoTrx.getText().trim().length()>0)
            udfLoadKoreksiJual();
        else
            udfNew();

        txtNoTrx.setEnabled(isKoreksi);
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                if(isKoreksi)
                    txtNoTrx.requestFocusInWindow();
                else
                    txtKode.requestFocusInWindow();
            }
        });

    }

    private void setDueDate(){
        try {
            jFJtTempo.setText(new SimpleDateFormat("dd/MM/yyyy").format(
                    getDueDate(new SimpleDateFormat("dd/MM/yyyy").parse(lblTgl.getText()),
                    fn.udfGetInt(txtTop.getText()))));
        } catch (ParseException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage());
        }
    }

    private Date getDueDate(Date d, int i){
        Calendar c = Calendar.getInstance();
        c.setTime(d);
        c.add(Calendar.DAY_OF_MONTH, i);

        return c.getTime();
    }

    private void udfLoadKoreksiJual(){
        String sQry="select h.no_penjualan as sales_no, to_char(h.tanggal, 'dd/MM/yyyy') as tgl_trx, " +
                "coalesce(h.kode_customers,'') as kode_cust, coalesce(c.nama_pasien, h.nama_pasien) as nama_cust, " +
                "coalesce(h.keterangan,'') as catatan, " +
                "case when h.kode_jenis='2' then 'KREDIT' else 'TUNAI' end as jenis, coalesce(h.koreksi, false) as koreksi " +
                "from penjualan h " +
                "left join customers c on c.kode_customers=h.kode_customers " +
                "where h.no_penjualan='"+txtNoTrx.getText()+"'";
        
        //System.out.println(sQry);
        try{
            ResultSet rs=conn.createStatement().executeQuery(sQry);
            if(rs.next()){
                if(rs.getBoolean("koreksi")==true){
                    JOptionPane.showMessageDialog(this, "Transaksi penjualan sudah pernah dikoreksi!");
                    udfNew();
                    if(!txtNoTrx.isFocusOwner())
                        txtNoTrx.requestFocus();
                    
                    return;
                }
                txtNoTrx.setText(rs.getString("sales_no"));
                txtCustomer.setText(rs.getString("kode_cust"));
                txtNamaCustomer.setText(rs.getString("nama_cust"));
                lblTgl.setText(rs.getString("tgl_trx"));
                //cmbGudang.setSelectedItem(rs.getString("nama_gudang"));
                txtCatatan.setText(rs.getString("catatan"));
                cmbCustPembayaran.setSelectedItem(rs.getString("jenis"));

                rs.close();
                sQry="select d.item_code, coalesce(i.item_name,'') as nama_item, coalesce(d.qty_jual,0) as qty, " +
                        "coalesce(d.uom_jual,'') as unit_jual, coalesce(d.harga_jual,0) as unit_price, " +
                        "coalesce(d.discount,0) as discount, coalesce(d.tax,0) as tax, " +
                        "coalesce(d.qty_jual,0) * coalesce(d.harga_jual,0) as sub_Total, " +
                        "case when d.uom_jual=i.satuan_besar then coalesce(konversi,1) " +
                        "else 1 end as konv, " +
                        "coalesce(d.is_disc_rp, false) as is_disc_rp, coalesce(d.is_tax_rp, false) as is_tax_rp,"
                        + "coalesce(d.kode_gudang,'') as kode_gudang, coalesce(g.deskripsi,'') as nama_gudang  " +
                        "from penjualan_detail d " +
                        "inner join barang i on i.item_code=d.item_code  "
                        + "left join gudang g on g.kode_gudang=d.kode_gudang " +
                        "where d.no_penjualan='"+txtNoTrx.getText()+"'";
               // System.out.println(sQry);
                ((DefaultTableModel)table.getModel()).setNumRows(0);
                rs=conn.createStatement().executeQuery(sQry);
                while(rs.next()){
                    ((DefaultTableModel)table.getModel()).addRow(new Object[]{
                        rs.getString("item_code"),
                        rs.getString("nama_item"),
                        rs.getString("unit_jual"),
                        rs.getDouble("qty"),
                        rs.getDouble("unit_price"),
                        rs.getDouble("discount"),
                        rs.getDouble("tax"),
                        rs.getDouble("sub_Total"),
                        rs.getDouble("konv"),
                        rs.getBoolean("is_disc_rp"),
                        rs.getBoolean("is_tax_rp"),
                        rs.getString("kode_gudang"),
                        rs.getString("nama_gudang")
                    });
                }
                if(table.getRowCount()>0)
                    table.setRowSelectionInterval(0, 0);

            }else{
                JOptionPane.showMessageDialog(this, "No. Penjualan tidak ditemukan!");
                udfNew();
                txtNoTrx.requestFocus();
            }
            rs.close();
        }catch(SQLException se){
            JOptionPane.showMessageDialog(this, se.getMessage());
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

    private void udfLoadKonversi(String sUnit) {
        if(cmbSatuan.getSelectedIndex()<0) return;
        //if(cmbSatuan.getSelectedIndex()==0) {lblKonv.setText("1"); return;}

        try {
            String sCustType="";
            String sQry = "select case  when '" + sUnit + "'=satuan_kecil then 1 " +
                          "             when '" + sUnit + "'=satuan_besar then coalesce(konversi,1) " +
                          "             else 1 end as konv, " +
                          "case  when '" + sUnit + "'=coalesce(satuan_kecil,'') then harga_kecil_non_resep " +
                          "      when '" + sUnit + "'=coalesce(satuan_besar,'') then harga_besar_non_resep " +
                          "      else 0 end as harga " +
                          "from barang i " +
                          "where i.item_code='" + txtKode.getText() + "'";

            //System.out.println(sQry);
            ResultSet rs = conn.createStatement().executeQuery(sQry);
            if (rs.next()) {
                txtHarga.setText(fn.dFmt.format(rs.getDouble("harga")));
                lblSubTotal.setText(fn.dFmt.format(rs.getDouble("harga")*fn.udfGetDouble(txtQty.getText())));
                lblKonv.setText(fn.intFmt.format(rs.getInt("konv")));
            } else {
                txtHarga.setText("0");
                lblSubTotal.setText("0");
                lblKonv.setText("1");
            }
            rs.close();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage());
        }
    }

    private void udfChangePriceAll(){
        if(table.getRowCount()<=0) return;
        try{
            ResultSet rs=null;
            TableColumnModel col=table.getColumnModel();
            //String sCustType=cmbCustType.getSelectedIndex()==0? "G": "R";
            String sCustType="";
            String sQry="", sUnit;
            for (int i=0; i<=table.getRowCount(); i++){
                sUnit=table.getValueAt(i, col.getColumnIndex("Satuan")).toString();
                if(i==table.getRowCount() && txtKode.getText().length()>0){
                    sQry="select coalesce(nama_item,'') as nama_item, " +
                         "coalesce(unit,'') as unit, " +
                         "coalesce(unit2,'') as unit2, coalesce(konv2,1) as konv2, " +
                         "coalesce(unit3,'') as unit3, coalesce(konv3,1) as konv3," +
                         "coalesce(unit_jual,'') as unit_jual, coalesce(konv_jual,1) as konv_jual, " +
                         "case  when '"+sCustType+"'='G' and '"+sUnit+"'=coalesce(unit,'') then harga_g_1 " +
                         "      when '"+sCustType+"'='G' and '"+sUnit+"'=coalesce(unit2,'') then harga_g_2 " +
                         "      when '"+sCustType+"'='G' and '"+sUnit+"'=coalesce(unit3,'') then harga_g_3 " +
                         "      when '"+sCustType+"'='E' and '"+sUnit+"'=coalesce(unit,'') then harga_r_1 " +
                         "      when '"+sCustType+"'='E' and '"+sUnit+"'=coalesce(unit2,'') then harga_r_2 " +
                         "      when '"+sCustType+"'='E' and '"+sUnit+"'=coalesce(unit3,'') then harga_r_3 " +
                         "else (case when '"+sCustType+"'='G' then harga_g_1 else harga_r_1 end) " +
                         "end as harga " +
                         "from r_item i " +
                         "left join r_item_harga_jual h on h.kode_item=i.kode_item  " +
                         "where i.kode_item='"+txtKode.getText()+"'";
                    rs=conn.createStatement().executeQuery(sQry);
                    if(rs.next()){
                        txtHarga.setText(fn.dFmt.format(rs.getDouble("harga")));
                        lblSubTotal.setText(fn.dFmt.format(rs.getDouble("harga")*fn.udfGetDouble(txtQty.getText())));
                    }
                    rs.close();
                }else{
                    sQry="select coalesce(nama_item,'') as nama_item, " +
                         "coalesce(unit,'') as unit, " +
                         "coalesce(unit2,'') as unit2, coalesce(konv2,1) as konv2, " +
                         "coalesce(unit3,'') as unit3, coalesce(konv3,1) as konv3," +
                         "coalesce(unit_jual,'') as unit_jual, coalesce(konv_jual,1) as konv_jual, " +
                         "case  when '"+sCustType+"'='G' and '"+sUnit+"'=coalesce(unit,'') then harga_g_1 " +
                         "      when '"+sCustType+"'='G' and '"+sUnit+"'=coalesce(unit2,'') then harga_g_2 " +
                         "      when '"+sCustType+"'='G' and '"+sUnit+"'=coalesce(unit3,'') then harga_g_3 " +
                         "      when '"+sCustType+"'='E' and '"+sUnit+"'=coalesce(unit,'') then harga_r_1 " +
                         "      when '"+sCustType+"'='E' and '"+sUnit+"'=coalesce(unit2,'') then harga_r_2 " +
                         "      when '"+sCustType+"'='E' and '"+sUnit+"'=coalesce(unit3,'') then harga_r_3 " +
                         "else (case when '"+sCustType+"'='G' then harga_g_1 else harga_r_1 end) " +
                         "end as harga " +
                         "from r_item i " +
                         "left join r_item_harga_jual h on h.kode_item=i.kode_item  " +
                         "where i.kode_item='"+table.getValueAt(i, 0).toString()+"'";
                    rs=conn.createStatement().executeQuery(sQry);
                    if(rs.next()){
                        table.setValueAt(rs.getDouble("harga"), i, col.getColumnIndex("Harga"));
                        table.setValueAt(rs.getDouble("harga")*fn.udfGetDouble(table.getValueAt(i, col.getColumnIndex("Qty"))),
                                i, col.getColumnIndex("Sub Total"));
                    }
                    rs.close();
                }
            }


        }catch(SQLException se){
            JOptionPane.showMessageDialog(this, se.getMessage());
        }
    }

    private String udfLoadItem(){
        String sMsg="";
        try{
            if(txtKode.getText().length()>0){
                String sQry="select coalesce(item_name,'') as nama_item, " +
                         "coalesce(satuan_kecil,'') as unit, " +
                         "coalesce(satuan_besar,'') as unit2, coalesce(konversi,1) as konv, " +
                         "coalesce(harga_kecil_non_resep,0) as harga " +
                         "from barang i " +
                         "where i.item_code='"+txtKode.getText()+"' ";

//                System.out.println(sQry);
                 ResultSet rs=conn.createStatement().executeQuery(sQry);
                 cmbSatuan.removeAllItems();
                 if(rs.next()){
                     lblItem.setText(rs.getString("nama_item"));
                     if(rs.getString("unit").length()>0) cmbSatuan.addItem(rs.getString("unit"));
                     if(rs.getString("unit2").length()>0) cmbSatuan.addItem(rs.getString("unit2"));
                     lblKonv.setText("1");
                     txtHarga.setText(fn.dFmt.format(rs.getDouble("harga")));
                     txtQty.setText("1");
                     lblSubTotal.setText(txtHarga.getText());

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

    private String udfLoadComboKonversi(){
        String sMsg="";
        try{
            if(txtKode.getText().length()>0){
                String sQry="select coalesce(item_name,'') as nama_item, " +
                         "coalesce(satuan_kecil,'') as unit, " +
                         "coalesce(satuan_besar,'') as unit2 " +
                         "from barang i " +
                         "where i.item_code='"+txtKode.getText()+"'";

//                System.out.println(sQry);
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

    public void setFlagKoreksi(boolean b) {
        this.isKoreksi=b;
    }

    public void setNoTrx(String s) {
        this.sNoTrx = s;
        txtNoTrx.setText(s);
    }

    public void setDesktopPane(JDesktopImage jDesktopPane1) {
        this.desktop=jDesktopPane1;
    }

    private void udfNewDokter() {
        DlgDokter fMaster=new DlgDokter(this, true);
        fMaster.setTitle("Data dokter baru");
        fMaster.setConn(conn);
        fMaster.setSrcForm(this);
        fMaster.setVisible(true);
    }

    public class MyKeyListener extends KeyAdapter {
        @Override
        public void keyReleased(KeyEvent evt){
            if(evt.getSource().equals(txtQty)||evt.getSource().equals(txtHarga)||evt.getSource().equals(txtPPn)||evt.getSource().equals(txtDisc))
                //lblSubTotal.setText(fn.dFmt.format(fn.udfGetDouble(txtQty.getText())*fn.udfGetDouble(txtHarga.getText())));
                udfSetSubTotalItem();
            else if(evt.getSource().equals(txtKode) && txtKode.getText().trim().length()==0)
                udfClearItem();
            else if(evt.getSource().equals(txtTop))
                setDueDate();
        }

        @Override
        public void keyTyped(KeyEvent evt){
            if(evt.getSource().equals(txtHarga) || evt.getSource().equals(txtQty)||evt.getSource().equals(txtTop))
                fn.keyTyped(evt);
        }

        @Override
        public void keyPressed(KeyEvent evt) {
            Component ct = KeyboardFocusManager.getCurrentKeyboardFocusManager().getPermanentFocusOwner();
            int keyKode = evt.getKeyCode();
            switch(keyKode){
                case KeyEvent.VK_INSERT:{
                    //if(table.getCellEditor()!=null && evt.getSource().equals(table))
                    if(!txtKode.isFocusOwner())    
                        txtKode.requestFocus();
                    
                    if(table.getCellEditor()!=null && evt.getSource().equals(table))
                        table.getCellEditor().stopCellEditing();
                    lookupItem.setAlwaysOnTop(true);
                    lookupItem.setSrcTable(table, table.getColumnModel().getColumnIndex("Qty"));
                    lookupItem.setKeyEvent(evt);
                    lookupItem.setObjForm(this);
                    lookupItem.setVisible(true);
                    lookupItem.clearText();
                    lookupItem.requestFocusInWindow();
                    if(lookupItem.getKodeBarang().length()>0){
                        txtKode.setText(lookupItem.getKodeBarang());
                        String sMsg=udfLoadItem();
                        if(sMsg.length()>0){
                            JOptionPane.showMessageDialog(aThis, sMsg);
                            //if(!txtKode.isFocusOwner())
                                txtKode.requestFocus();
                            return;
                        }
                        txtQty.requestFocus();
                        //cmbSatuan.requestFocus();
                    }
                    break;
                }
                case KeyEvent.VK_F4:{
                    udfNew();
                    break;
                }
                case KeyEvent.VK_F2:{
                    udfSave();
                    break;
                }
                case KeyEvent.VK_F3:{
                    int iRow=table.getSelectedRow();
                    if(iRow < 0) return;
                    stItemUpd=true;
                    TableColumnModel col=table.getColumnModel();
                    txtKode.setText(table.getValueAt(iRow, col.getColumnIndex("ProductID")).toString());
                    udfLoadComboKonversi();
                    lblItem.setText(table.getValueAt(iRow, col.getColumnIndex("Nama Barang")).toString());
                    cmbSatuan.setSelectedItem(table.getValueAt(iRow, col.getColumnIndex("Satuan")).toString());
                    txtQty.setText(fn.intFmt.format(fn.udfGetInt(table.getValueAt(iRow, col.getColumnIndex("Qty")))));
                    txtHarga.setText(fn.dFmt.format(fn.udfGetDouble(table.getValueAt(iRow, col.getColumnIndex("Harga")))));
                    lblSubTotal.setText(fn.dFmt.format(fn.udfGetDouble(table.getValueAt(iRow, col.getColumnIndex("Sub Total")))));
                    txtGudang.setText(table.getValueAt(iRow, col.getColumnIndex("Kd. Gdg")).toString());
                    lblGudang.setText(table.getValueAt(iRow, col.getColumnIndex("Gudang")).toString());
                    txtQty.requestFocusInWindow();
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
//                        if(txtKode.isFocusOwner()){
//                            txtQty.requestFocusInWindow();
//                            return;
                        //}else
                        //if((lockHarga && txtQty.isFocusOwner()) || txtHarga.isFocusOwner() ||txtPPn.isFocusOwner() ){
//                        if(cmbGudang.isFocusOwner())
//                            udfAddItemToTable();
//                            return;
//                            cm
//                        }
                        if(txtGudang.isFocusOwner() && !fn.isListVisible()){
                            udfAddItemToTable();
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
                    }else{
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
                case KeyEvent.VK_LEFT:{
                    if(table.getSelectedColumn()==2)
                        table.setColumnSelectionInterval(0, 0);
                    break;
                }
                case KeyEvent.VK_DELETE:{
                    if(evt.getSource().equals(table) && table.getSelectedRow()>=0){
                        if(table.getCellEditor()!=null)
                            table.getCellEditor().stopCellEditing();

                        int iRow[]= table.getSelectedRows();
                        int rowPalingAtas=iRow[0];

                        TableModel tm= table.getModel();

                        while(iRow.length>0) {
                            //JOptionPane.showMessageDialog(null, iRow[0]);
                            ((DefaultTableModel)tm).removeRow(table.convertRowIndexToModel(iRow[0]));
                            iRow = table.getSelectedRows();
                        }
                        table.clearSelection();

                        if(table.getRowCount()>0 && rowPalingAtas<table.getRowCount()){
                            table.setRowSelectionInterval(rowPalingAtas, rowPalingAtas);
                        }else{
                            if(table.getRowCount()>0)
                                table.setRowSelectionInterval(rowPalingAtas-1, rowPalingAtas-1);
                            else
                                table.requestFocus();
                        }
                        if(table.getSelectedRow()>=0){
                            table.changeSelection(table.getSelectedRow(), 0, false, false);
                            //cEditor.setValue(table.getValueAt(table.getSelectedRow(), 0).toString());
                        }
                    }
                    break;
                }
                case KeyEvent.VK_ESCAPE:{
                    if(evt.getSource().equals(txtKode)){
                        udfClearItem();
                        return;
                    }
                    if(fn.isListVisible()){
                        return;
                    }
                    if(table.getRowCount()>0 && JOptionPane.showConfirmDialog(null,"Anda Yakin Untuk Keluar?",
                            "Ustasoft",JOptionPane.YES_NO_OPTION)==JOptionPane.YES_OPTION){
                        dispose();
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

    private void udfAddItemToTable(){
        if(lblItem.getText().trim().length()==0){
            txtKode.requestFocus();
            JOptionPane.showMessageDialog(this, "Silakan masukkan item terlebih dulu!");
            return;
        }
        if(fn.udfGetDouble(txtQty.getText())==0){
            txtQty.requestFocus();
            JOptionPane.showMessageDialog(this, "Masukkan Qty lebih dari 0!");
            return;
        }
        if(fn.udfGetDouble(txtHarga.getText())==0){
            txtKode.requestFocus();
            JOptionPane.showMessageDialog(this, "Harga jual belum diset!");
            
            return;
        }
        if(txtGudang.getText().length()==0){
            txtGudang.requestFocus();
            JOptionPane.showMessageDialog(this, "Silakan masukkan Gudang terlebih dulu!");
            
            return;
        }
        
        TableColumnModel col=table.getColumnModel();
        if(stItemUpd){
            int iRow=table.getSelectedRow();
            if(iRow<0) return;
            table.setValueAt(txtKode.getText(), iRow, col.getColumnIndex("ProductID"));
            table.setValueAt(lblItem.getText(), iRow, col.getColumnIndex("Nama Barang"));
            table.setValueAt(cmbSatuan.getSelectedItem().toString(), iRow, col.getColumnIndex("Satuan"));
            table.setValueAt(fn.udfGetInt(txtQty.getText()), iRow, col.getColumnIndex("Qty"));
            table.setValueAt(fn.udfGetDouble(txtHarga.getText()), iRow, col.getColumnIndex("Harga"));
            table.setValueAt(fn.udfGetDouble(lblSubTotal.getText()), iRow, col.getColumnIndex("Sub Total"));
            table.setValueAt(fn.udfGetInt(lblKonv.getText()), iRow, col.getColumnIndex("Konv"));
            table.setValueAt(fn.udfGetInt(txtDisc.getText()), iRow, col.getColumnIndex("Disc"));
            table.setValueAt(fn.udfGetInt(txtPPn.getText()), iRow, col.getColumnIndex("PPn"));
            table.setValueAt(chkDiscRp.isSelected(), iRow, col.getColumnIndex("Disc%"));
            table.setValueAt(chkPPnRp.isSelected(), iRow, col.getColumnIndex("PPn%"));
            table.setValueAt(txtGudang.getText(), iRow, col.getColumnIndex("Kd. Gdg"));
            table.setValueAt(lblGudang.getText(), iRow, col.getColumnIndex("Gudang"));

            table.changeSelection(iRow, iRow, false, false);
        }else{
            String sUnit="";
            for(int i=0; i<table.getRowCount(); i++){
                sUnit=cmbSatuan.getSelectedIndex()<0? "": cmbSatuan.getSelectedItem().toString();
                if(table.getValueAt(i, 0).toString().equalsIgnoreCase(txtKode.getText()) && table.getValueAt(i, table.getColumnModel().getColumnIndex("Satuan")).toString().equalsIgnoreCase(sUnit)){
                    if(JOptionPane.showConfirmDialog(this, ("Item tersebut sudah dimasukkan pada baris ke "+(i+1)+".\nQty Item akan ditambahkan?"), "Item exists", JOptionPane.YES_NO_OPTION)==JOptionPane.NO_OPTION)
                        return;
                    else{
                        double total=fn.udfGetInt(txtQty.getText())+fn.udfGetDouble(table.getValueAt(i, col.getColumnIndex("Qty")));
                        table.setValueAt(total, i, col.getColumnIndex("Qty"));
                        table.setValueAt(total*fn.udfGetDouble(lblSubTotal.getText()), i, col.getColumnIndex("Sub Total"));
                        udfClearItem();
                        txtKode.requestFocusInWindow();
                        return;
                    }
                }
            }
            ((DefaultTableModel)table.getModel()).addRow(new Object[]{
                txtKode.getText(),
                lblItem.getText(),
                (cmbSatuan.getSelectedItem()==null? "": cmbSatuan.getSelectedItem().toString()) ,
                fn.udfGetDouble(txtQty.getText()),
                fn.udfGetDouble(txtHarga.getText()),
                fn.udfGetDouble(txtDisc.getText()),
                fn.udfGetDouble(txtPPn.getText()),
                fn.udfGetDouble(lblSubTotal.getText()),
                fn.udfGetInt(lblKonv.getText()),
                chkDiscRp.isSelected(),
                chkPPnRp.isSelected(),
                txtGudang.getText(),
                lblGudang.getText()
            });
        table.setRowSelectionInterval(((DefaultTableModel)table.getModel()).getRowCount()-1, ((DefaultTableModel)table.getModel()).getRowCount()-1);
        table.changeSelection(((DefaultTableModel)table.getModel()).getRowCount()-1, ((DefaultTableModel)table.getModel()).getRowCount()-1, false, false);
        }

        udfClearItem();
        txtKode.requestFocus();
        txtKode.requestFocusInWindow();
    }

    private void udfClearItem(){
        txtKode.setText("");
        lblItem.setText("");
        cmbSatuan.removeAllItems();
        txtQty.setText("1");
        txtHarga.setText("0");
        lblSubTotal.setText("0");
        lblKonv.setText("1");
        txtDisc.setText("");
        txtPPn.setText("");
        txtGudang.setText("01"); lblGudang.setText("DEPAN");
        
        stItemUpd=false;
    }

    private void udfNew() {
        cmbCustPembayaran.setSelectedIndex(0);
        jLabel3.setVisible(false); jFJtTempo.setVisible(false);
        txtNoTrx.setText("");
        txtDokter.setText("0000");   txtNamaDokter.setText("Atas Permintaan Sendiri");   
        txtNoTrx.setEnabled(isKoreksi);
        txtCustomer.setText(""); txtCustomer.setText("");
        ((DefaultTableModel)table.getModel()).setNumRows(0);
        lblTotal.setText("0");
        btnCancel.setIcon(new javax.swing.ImageIcon(getClass().getResource("/image/Icon/Cancel.png"))); // NOI18N
        btnCancel.setText("Cancel");
        btnNew.setEnabled(false);   btnPrint.setEnabled(false);
        btnSave.setEnabled(true);
        udfClearItem();
        chkDiscRp.setSelected(false);
        chkPPnRp.setSelected(false);
        txtKode.requestFocusInWindow();
    }

    private boolean udfCekBeforeSave(){
        boolean b=true;
        if(!isKoreksi && table.getRowCount()==0){
            JOptionPane.showMessageDialog(this, "Tidak ada item yang ditransaksikan!\nTekan 'Insert' untuk menambahkan item penjualan");
            txtKode.requestFocus();
            return false;
        }
        if(!isKoreksi && cmbCustPembayaran.getSelectedItem().toString().equalsIgnoreCase("KREDIT") && txtCustomer.getText().trim().isEmpty()){
            JOptionPane.showMessageDialog(this, "Isikan nama customer untuk penjualan kredit");
            txtCustomer.requestFocus();
            return false;
        }
        return b;
    }

    private void udfSave(){
        if(!udfCekBeforeSave()) return;
        String sNoKoreksi="";
        try{
            DlgPembayaran d1=new DlgPembayaran(this, true);
            if(!isKoreksi || fn.udfGetDouble(lblTotal.getText())>0){
                d1.setNoTrx(txtNoTrx.getText());
                d1.setTotal(fn.udfGetDouble(lblTotal.getText()));
                d1.setVisible(true);
                if(!d1.isSelected()) return;
                if(d1.getBayar()<fn.udfGetDouble(lblTotal.getText()) &&
                        (txtCustomer.getText().trim().equalsIgnoreCase("")||txtCustomer.getText().trim().equalsIgnoreCase("CASH"))){
                    JOptionPane.showMessageDialog(this, "Untuk transaksi kredit silakan masukkan nama pelanggan terlebih dulu!");
                    txtCustomer.requestFocusInWindow();
                    return;
                }
            }
            conn.setAutoCommit(false);
            ResultSet rs=null;

            if(isKoreksi){
                if(table.getRowCount()==0 && JOptionPane.showConfirmDialog(this, "Anda yakin untuk membatalkan penjualan ini?")!=JOptionPane.YES_OPTION){
                    return;
                }
                rs=conn.createStatement().executeQuery("select fn_penjualan_koreksi('"+txtNoTrx.getText()+"')");
                if(rs.next())
                    sNoKoreksi=rs.getString(1);

                rs.close();
                if(table.getRowCount()==0){
                    this.dispose();
                }
            }

            rs=conn.createStatement().executeQuery("select fn_get_kode_jual("+
                    (isKoreksi?"(select tanggal::date from penjualan where no_penjualan='"+txtNoTrx.getText()+"')": "current_date")+" )");
            if(rs.next()){
                txtNoTrx.setText(rs.getString(1));
            }
            rs.close();


            String sQryH="INSERT INTO penjualan(" +
                    "no_penjualan, nama_pasien, alamat, telp, " +
                    "keterangan, dokter, user_trx, discount, tax, resep, " +
                    "tanggal, koreksi, kode_jenis, ket_jenis_bayar, jth_tempo, kode_customers, " +
                    "st_lunas, multi_satuan)" +
                    "VALUES (" +
                    "'"+txtNoTrx.getText()+"', '"+txtNamaCustomer.getText()+"', '', '', " +
                    "'"+txtCatatan.getText()+"', '"+txtDokter.getText()+"', '"+MainForm.sUserName+"', 0, 0, 'N', " +
                    "now(), false, '"+d1.getJenisBayar()+"', '', "+fn.udfGetInt(txtTop.getText())+", '"+txtCustomer.getText()+"', " +
                    (fn.udfGetDouble(lblTotal.getText())<=d1.getBayar())+ ", " +
                    "true);";

            String sQry="";
            TableColumnModel col=table.getColumnModel();
            double harga=0, qty_jual=0;
            for(int iRow=0; iRow<table.getRowCount(); iRow++){
                if(table.getValueAt(iRow, col.getColumnIndex("ProductID"))!=null &&
                   table.getValueAt(iRow, col.getColumnIndex("ProductID")).toString().length()>0){

                    qty_jual=fn.udfGetDouble(table.getValueAt(iRow, col.getColumnIndex("Qty")))*fn.udfGetDouble(table.getValueAt(iRow, col.getColumnIndex("Konv")));
                    harga=fn.udfGetDouble(table.getValueAt(iRow, col.getColumnIndex("Harga")))/fn.udfGetDouble(table.getValueAt(iRow, col.getColumnIndex("Konv")));

                    sQry+= //(sQry.length()>0? " union all ": "")+
                           "INSERT INTO penjualan_detail(" +
                           "no_penjualan, no_racikan, item_code, jumlah, harga, discount, " +
                           "tax, jumlah_jual, kode_gudang, shift, " +
                           "uom_jual, qty_jual, harga_jual, konv) VALUES (" +
                           "'"+txtNoTrx.getText()+"', '', " +
                           "'"+table.getValueAt(iRow, col.getColumnIndex("ProductID")).toString()+"', " +
                           qty_jual+", "+harga+", 0, 0,"+qty_jual+", '"+table.getValueAt(iRow, col.getColumnIndex("Kd. Gdg")).toString()+"', " +
                           "'"+MainForm.sShift+"', " +
                           "'"+table.getValueAt(iRow, col.getColumnIndex("Satuan")).toString()+"', " +
                           fn.udfGetDouble(table.getValueAt(iRow, col.getColumnIndex("Qty")))+", " +
                           fn.udfGetDouble(table.getValueAt(iRow, col.getColumnIndex("Harga")))+", " +
                           fn.udfGetDouble(table.getValueAt(iRow, col.getColumnIndex("Konv")))+");";
                }
            }
            String sInsBayar="insert into penjualan_bayar(no_penjualan,sub_total,discount,ppn,total,bayar)" +
                             "values('"+ txtNoTrx.getText() +"',0,0,0,"+ fn.udfGetDouble(lblTotal.getText()) +","+
                             d1.getBayar() +");";

            System.out.println(sQryH+sQry);

            int i=conn.createStatement().executeUpdate(sQryH+sInsBayar+sQry);

            conn.setAutoCommit(true);
            if(JOptionPane.showConfirmDialog(this, "Input data sukses, Klik ok untuk cetak invoice", "Message", JOptionPane.YES_NO_OPTION)==JOptionPane.YES_OPTION){
                printKwitansi();
                if(fn.udfGetDouble(lblTotal.getText())>d1.getBayar())
                    printKwitansi();
                
            }
            if(isKoreksi && srcForm!=null){
                if(srcForm instanceof FrmPenjualanHistory){
                    ((FrmPenjualanHistory)srcForm).udfFilter();
                    this.dispose();
                    return;
                }
            }
            udfNew();
            rs.close();
        }catch(SQLException se){
            try {
                conn.setAutoCommit(true);
                conn.rollback();
                JOptionPane.showMessageDialog(this, se.getMessage());

            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage());
            }
        }


    }

    private void printKwitansi(){
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
        //if (JOptionPane.showConfirmDialog(null,"Cetak Invoice?","Message",JOptionPane.YES_NO_OPTION,JOptionPane.QUESTION_MESSAGE)==JOptionPane.YES_OPTION) {
        try{
            PrintPenjualan2 pn = new PrintPenjualan2(conn, txtNoTrx.getText(), MainForm.sUserName,services[i]);

        }catch(java.lang.ArrayIndexOutOfBoundsException ie){
            JOptionPane.showMessageDialog(this, "Printer tidak ditemukan!");
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

        jScrollPane1 = new javax.swing.JScrollPane();
        table = new javax.swing.JTable();
        jPanel1 = new javax.swing.JPanel();
        txtQty = new javax.swing.JTextField();
        lblItem = new javax.swing.JLabel();
        cmbSatuan = new javax.swing.JComboBox();
        txtKode = new javax.swing.JTextField();
        txtHarga = new javax.swing.JTextField();
        lblKonv = new javax.swing.JLabel();
        lblSubTotal = new javax.swing.JLabel();
        txtDisc = new javax.swing.JTextField();
        txtPPn = new javax.swing.JTextField();
        chkPPnRp = new javax.swing.JCheckBox();
        chkDiscRp = new javax.swing.JCheckBox();
        txtGudang = new javax.swing.JTextField();
        lblGudang = new javax.swing.JTextField();
        jToolBar1 = new javax.swing.JToolBar();
        btnNew = new javax.swing.JButton();
        btnSave = new javax.swing.JButton();
        btnPrint = new javax.swing.JButton();
        btnCancel = new javax.swing.JButton();
        lblTotal = new javax.swing.JLabel();
        jPanel2 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        txtNamaCustomer = new javax.swing.JTextField();
        txtCustomer = new javax.swing.JTextField();
        lblTgl = new javax.swing.JLabel();
        txtNoTrx = new javax.swing.JTextField();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        btnAccCust = new javax.swing.JButton();
        jLabel3 = new javax.swing.JLabel();
        cmbCustPembayaran = new javax.swing.JComboBox();
        jLabel9 = new javax.swing.JLabel();
        txtCatatan = new javax.swing.JTextField();
        jLabel10 = new javax.swing.JLabel();
        jFJtTempo = new javax.swing.JFormattedTextField();
        jLabel2 = new javax.swing.JLabel();
        txtDokter = new javax.swing.JTextField();
        txtNamaDokter = new javax.swing.JTextField();
        btnAddDokter = new javax.swing.JButton();
        txtTop = new javax.swing.JTextField();
        jLabel4 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Penjualan NON RESEP (Multi Satuan - Multi Gudang)");
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowOpened(java.awt.event.WindowEvent evt) {
                formWindowOpened(evt);
            }
        });

        jScrollPane1.setName("jScrollPane1"); // NOI18N

        table.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        table.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "ProductID", "Nama Barang", "Satuan", "Qty", "Harga", "Disc", "PPn", "Sub Total", "Konv", "Disc%", "PPn%", "Kd. Gdg", "Gudang"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Double.class, java.lang.Double.class, java.lang.Double.class, java.lang.Double.class, java.lang.Double.class, java.lang.Object.class, java.lang.Boolean.class, java.lang.Boolean.class, java.lang.String.class, java.lang.String.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false, false, false, false, false, false, false, false
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
        table.getTableHeader().setReorderingAllowed(false);
        jScrollPane1.setViewportView(table);

        jPanel1.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        jPanel1.setName("jPanel1"); // NOI18N
        jPanel1.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        txtQty.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        txtQty.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        txtQty.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        txtQty.setDisabledTextColor(new java.awt.Color(0, 0, 0));
        txtQty.setName("txtQty"); // NOI18N
        jPanel1.add(txtQty, new org.netbeans.lib.awtextra.AbsoluteConstraints(410, 20, 40, 20));

        lblItem.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        lblItem.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        lblItem.setName("lblItem"); // NOI18N
        jPanel1.add(lblItem, new org.netbeans.lib.awtextra.AbsoluteConstraints(60, 20, 270, 20));

        cmbSatuan.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        cmbSatuan.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        cmbSatuan.setName("cmbSatuan"); // NOI18N
        jPanel1.add(cmbSatuan, new org.netbeans.lib.awtextra.AbsoluteConstraints(330, 20, 80, 20));

        txtKode.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        txtKode.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        txtKode.setDisabledTextColor(new java.awt.Color(0, 0, 0));
        txtKode.setName("txtKode"); // NOI18N
        jPanel1.add(txtKode, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 20, 60, 20));

        txtHarga.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        txtHarga.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtHarga.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        txtHarga.setDisabledTextColor(new java.awt.Color(0, 0, 0));
        txtHarga.setName("txtHarga"); // NOI18N
        jPanel1.add(txtHarga, new org.netbeans.lib.awtextra.AbsoluteConstraints(450, 20, 80, 20));

        lblKonv.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        lblKonv.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        lblKonv.setName("lblKonv"); // NOI18N
        jPanel1.add(lblKonv, new org.netbeans.lib.awtextra.AbsoluteConstraints(720, 20, 40, 20));

        lblSubTotal.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        lblSubTotal.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lblSubTotal.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        lblSubTotal.setName("lblSubTotal"); // NOI18N
        jPanel1.add(lblSubTotal, new org.netbeans.lib.awtextra.AbsoluteConstraints(630, 20, 90, 20));

        txtDisc.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        txtDisc.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        txtDisc.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        txtDisc.setDisabledTextColor(new java.awt.Color(0, 0, 0));
        txtDisc.setName("txtDisc"); // NOI18N
        jPanel1.add(txtDisc, new org.netbeans.lib.awtextra.AbsoluteConstraints(530, 20, 50, 20));

        txtPPn.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        txtPPn.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        txtPPn.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        txtPPn.setDisabledTextColor(new java.awt.Color(0, 0, 0));
        txtPPn.setName("txtPPn"); // NOI18N
        txtPPn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtPPnActionPerformed(evt);
            }
        });
        jPanel1.add(txtPPn, new org.netbeans.lib.awtextra.AbsoluteConstraints(580, 20, 50, 20));

        chkPPnRp.setText("(Rp.)");
        chkPPnRp.setName("chkPPnRp"); // NOI18N
        chkPPnRp.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                chkPPnRpItemStateChanged(evt);
            }
        });
        chkPPnRp.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chkPPnRpActionPerformed(evt);
            }
        });
        jPanel1.add(chkPPnRp, new org.netbeans.lib.awtextra.AbsoluteConstraints(580, 1, 60, 18));

        chkDiscRp.setText("(Rp.)");
        chkDiscRp.setName("chkDiscRp"); // NOI18N
        chkDiscRp.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                chkDiscRpItemStateChanged(evt);
            }
        });
        jPanel1.add(chkDiscRp, new org.netbeans.lib.awtextra.AbsoluteConstraints(530, 2, 60, 18));

        txtGudang.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        txtGudang.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        txtGudang.setName("txtGudang"); // NOI18N
        txtGudang.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtGudangKeyReleased(evt);
            }
        });
        jPanel1.add(txtGudang, new org.netbeans.lib.awtextra.AbsoluteConstraints(760, 20, 40, 20));

        lblGudang.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        lblGudang.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        lblGudang.setDisabledTextColor(new java.awt.Color(0, 0, 0));
        lblGudang.setEnabled(false);
        lblGudang.setName("lblGudang"); // NOI18N
        jPanel1.add(lblGudang, new org.netbeans.lib.awtextra.AbsoluteConstraints(800, 20, 120, 20));

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

        lblTotal.setBackground(new java.awt.Color(0, 0, 102));
        lblTotal.setFont(new java.awt.Font("Tahoma", 0, 34)); // NOI18N
        lblTotal.setForeground(new java.awt.Color(255, 255, 255));
        lblTotal.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lblTotal.setText("0,00");
        lblTotal.setBorder(new org.jdesktop.swingx.border.DropShadowBorder());
        lblTotal.setName("lblTotal"); // NOI18N
        lblTotal.setOpaque(true);

        jPanel2.setBackground(new java.awt.Color(204, 204, 255));
        jPanel2.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        jPanel2.setName("jPanel2"); // NOI18N
        jPanel2.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel1.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jLabel1.setText("Pasien");
        jLabel1.setName("jLabel1"); // NOI18N
        jPanel2.add(jLabel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 35, 90, 20));

        txtNamaCustomer.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        txtNamaCustomer.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        txtNamaCustomer.setDisabledTextColor(new java.awt.Color(0, 0, 0));
        txtNamaCustomer.setEnabled(false);
        txtNamaCustomer.setName("txtNamaCustomer"); // NOI18N
        jPanel2.add(txtNamaCustomer, new org.netbeans.lib.awtextra.AbsoluteConstraints(180, 35, 310, 20));

        txtCustomer.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        txtCustomer.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        txtCustomer.setName("txtCustomer"); // NOI18N
        txtCustomer.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtCustomerKeyReleased(evt);
            }
        });
        jPanel2.add(txtCustomer, new org.netbeans.lib.awtextra.AbsoluteConstraints(100, 35, 80, 20));

        lblTgl.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        lblTgl.setText("20/12/2010");
        lblTgl.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        lblTgl.setName("lblTgl"); // NOI18N
        jPanel2.add(lblTgl, new org.netbeans.lib.awtextra.AbsoluteConstraints(300, 10, 90, 20));

        txtNoTrx.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        txtNoTrx.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        txtNoTrx.setDisabledTextColor(new java.awt.Color(0, 0, 0));
        txtNoTrx.setEnabled(false);
        txtNoTrx.setName("txtNoTrx"); // NOI18N
        jPanel2.add(txtNoTrx, new org.netbeans.lib.awtextra.AbsoluteConstraints(100, 10, 130, 20));

        jLabel5.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jLabel5.setText("No. Trans.");
        jLabel5.setName("jLabel5"); // NOI18N
        jPanel2.add(jLabel5, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 10, 90, 20));

        jLabel6.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jLabel6.setText("Tgl.");
        jLabel6.setName("jLabel6"); // NOI18N
        jPanel2.add(jLabel6, new org.netbeans.lib.awtextra.AbsoluteConstraints(250, 10, 50, 20));

        btnAccCust.setText("+");
        btnAccCust.setToolTipText("Tambah data pasien/ pelanggan");
        btnAccCust.setMargin(new java.awt.Insets(2, 2, 2, 2));
        btnAccCust.setName("btnAccCust"); // NOI18N
        btnAccCust.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                btnAccCustMouseClicked(evt);
            }
        });
        btnAccCust.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAccCustActionPerformed(evt);
            }
        });
        jPanel2.add(btnAccCust, new org.netbeans.lib.awtextra.AbsoluteConstraints(490, 35, 30, 22));

        jLabel3.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jLabel3.setText("Jatuh Tempo");
        jLabel3.setName("jLabel3"); // NOI18N
        jPanel2.add(jLabel3, new org.netbeans.lib.awtextra.AbsoluteConstraints(550, 35, 90, 20));

        cmbCustPembayaran.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        cmbCustPembayaran.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "TUNAI", "KREDIT" }));
        cmbCustPembayaran.setName("cmbCustPembayaran"); // NOI18N
        cmbCustPembayaran.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                cmbCustPembayaranItemStateChanged(evt);
            }
        });
        jPanel2.add(cmbCustPembayaran, new org.netbeans.lib.awtextra.AbsoluteConstraints(640, 10, 150, -1));

        jLabel9.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jLabel9.setText("Catatan");
        jLabel9.setName("jLabel9"); // NOI18N
        jPanel2.add(jLabel9, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 90, 90, 20));

        txtCatatan.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        txtCatatan.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        txtCatatan.setName("txtCatatan"); // NOI18N
        txtCatatan.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtCatatanKeyReleased(evt);
            }
        });
        jPanel2.add(txtCatatan, new org.netbeans.lib.awtextra.AbsoluteConstraints(100, 90, 700, 20));

        jLabel10.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jLabel10.setText("Pembayaran");
        jLabel10.setName("jLabel10"); // NOI18N
        jPanel2.add(jLabel10, new org.netbeans.lib.awtextra.AbsoluteConstraints(550, 10, 90, 20));

        jFJtTempo.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 1, true));
        jFJtTempo.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jFJtTempo.setName("jFJtTempo"); // NOI18N
        jPanel2.add(jFJtTempo, new org.netbeans.lib.awtextra.AbsoluteConstraints(720, 35, 90, 20));

        jLabel2.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jLabel2.setText("Dokter");
        jLabel2.setName("jLabel2"); // NOI18N
        jPanel2.add(jLabel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 60, 90, 20));

        txtDokter.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        txtDokter.setText("0000");
        txtDokter.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        txtDokter.setName("txtDokter"); // NOI18N
        txtDokter.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtDokterKeyReleased(evt);
            }
        });
        jPanel2.add(txtDokter, new org.netbeans.lib.awtextra.AbsoluteConstraints(100, 60, 80, 20));

        txtNamaDokter.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        txtNamaDokter.setText("Atas Permintaan Sendiri");
        txtNamaDokter.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        txtNamaDokter.setDisabledTextColor(new java.awt.Color(0, 0, 0));
        txtNamaDokter.setEnabled(false);
        txtNamaDokter.setName("txtNamaDokter"); // NOI18N
        jPanel2.add(txtNamaDokter, new org.netbeans.lib.awtextra.AbsoluteConstraints(180, 60, 310, 20));

        btnAddDokter.setText("+");
        btnAddDokter.setToolTipText("Tambah data dokter");
        btnAddDokter.setMargin(new java.awt.Insets(2, 2, 2, 2));
        btnAddDokter.setName("btnAddDokter"); // NOI18N
        btnAddDokter.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                btnAddDokterMouseClicked(evt);
            }
        });
        btnAddDokter.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAddDokterActionPerformed(evt);
            }
        });
        jPanel2.add(btnAddDokter, new org.netbeans.lib.awtextra.AbsoluteConstraints(490, 60, 30, 22));

        txtTop.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        txtTop.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        txtTop.setDisabledTextColor(new java.awt.Color(0, 0, 0));
        txtTop.setName("txtTop"); // NOI18N
        jPanel2.add(txtTop, new org.netbeans.lib.awtextra.AbsoluteConstraints(640, 35, 40, 20));

        jLabel4.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jLabel4.setText(" Hr.");
        jLabel4.setName("jLabel4"); // NOI18N
        jPanel2.add(jLabel4, new org.netbeans.lib.awtextra.AbsoluteConstraints(680, 35, 30, 20));

        jLabel8.setBackground(new java.awt.Color(204, 204, 255));
        jLabel8.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jLabel8.setText("<html>\n<b>F4</b> &nbsp : Membuat transaksi baru  &nbsp  &nbsp |  &nbsp  &nbsp \n<b>Insert</b> &nbsp : Menambah item barang  &nbsp  &nbsp |  &nbsp  &nbsp \n<b>Del</b> &nbsp&nbsp &nbsp &nbsp : Menghapus item barang |  &nbsp  &nbsp <br>\n<b>F2</b> &nbsp&nbsp : Menyimpan Transaksi &nbsp  &nbsp |  &nbsp  &nbsp\n<b>F3</b> &nbsp : Mengubah item transaksi  &nbsp  &nbsp |  &nbsp  &nbsp\n</html>"); // NOI18N
        jLabel8.setVerticalAlignment(javax.swing.SwingConstants.TOP);
        jLabel8.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        jLabel8.setName("jLabel8"); // NOI18N
        jLabel8.setOpaque(true);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jToolBar1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(429, 429, 429)
                .addComponent(lblTotal, javax.swing.GroupLayout.PREFERRED_SIZE, 225, javax.swing.GroupLayout.PREFERRED_SIZE))
            .addGroup(layout.createSequentialGroup()
                .addGap(10, 10, 10)
                .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, 934, Short.MAX_VALUE)
                .addGap(6, 6, 6))
            .addGroup(layout.createSequentialGroup()
                .addGap(10, 10, 10)
                .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, 934, Short.MAX_VALUE)
                .addGap(6, 6, 6))
            .addGroup(layout.createSequentialGroup()
                .addGap(10, 10, 10)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 934, Short.MAX_VALUE)
                .addGap(6, 6, 6))
            .addGroup(layout.createSequentialGroup()
                .addGap(10, 10, 10)
                .addComponent(jLabel8, javax.swing.GroupLayout.DEFAULT_SIZE, 934, Short.MAX_VALUE)
                .addGap(6, 6, 6))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jToolBar1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(20, 20, 20)
                        .addComponent(lblTotal)))
                .addGap(9, 9, 9)
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(3, 3, 3)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 210, Short.MAX_VALUE)
                .addGap(7, 7, 7)
                .addComponent(jLabel8, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(9, 9, 9))
        );

        setSize(new java.awt.Dimension(966, 557));
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void formWindowOpened(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowOpened
        udfInitForm();
    }//GEN-LAST:event_formWindowOpened

    private void btnNewActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnNewActionPerformed
        udfNew();
}//GEN-LAST:event_btnNewActionPerformed

    private void btnSaveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSaveActionPerformed
        udfSave();
}//GEN-LAST:event_btnSaveActionPerformed

    private void btnPrintActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnPrintActionPerformed
        printKwitansi();
}//GEN-LAST:event_btnPrintActionPerformed

    private void btnCancelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCancelActionPerformed
        if(btnCancel.getText().equalsIgnoreCase("cancel")){
            if(getTitle().indexOf("Revision")>0) dispose();
            btnSave.setEnabled(false);
            btnNew.setEnabled(true);
            btnCancel.setText("Exit");
            btnCancel.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/Icon/buttons/exit-32.png"))); // NOI18N
        }else{
            this.dispose();
        }
}//GEN-LAST:event_btnCancelActionPerformed

    private void txtCustomerKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtCustomerKeyReleased
        fn.lookup(evt, new Object[]{txtNamaCustomer}, 
                "select kode_customers, coalesce(nama_pasien,'') as nama_customer from customers " +
                "where kode_customers||coalesce(nama_pasien,'') ilike '%"+txtCustomer.getText()+"%'", 500, 200);
}//GEN-LAST:event_txtCustomerKeyReleased

    private void cmbCustPembayaranItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_cmbCustPembayaranItemStateChanged
        jLabel3.setVisible(cmbCustPembayaran.getSelectedIndex()==1);
        jFJtTempo.setVisible(cmbCustPembayaran.getSelectedIndex()==1);
        txtTop.setVisible(cmbCustPembayaran.getSelectedIndex()==1);
        jLabel4.setVisible(cmbCustPembayaran.getSelectedIndex()==1);
}//GEN-LAST:event_cmbCustPembayaranItemStateChanged

    private void txtCatatanKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtCatatanKeyReleased
        // TODO add your handling code here:
}//GEN-LAST:event_txtCatatanKeyReleased

    private void btnAccCustActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAccCustActionPerformed
        
    }//GEN-LAST:event_btnAccCustActionPerformed

    private void txtDokterKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtDokterKeyReleased
        fn.lookup(evt, new Object[]{txtNamaDokter}, "select kode_dokter, coalesce(nama,'') as nama from dokter "
                + "where kode_dokter||coalesce(nama,'') ilike '%"+txtDokter.getText()+"%' order by nama", txtDokter.getWidth()+txtNamaDokter.getWidth()+18, 150);
    }//GEN-LAST:event_txtDokterKeyReleased

    private void btnAddDokterActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAddDokterActionPerformed
        
    }//GEN-LAST:event_btnAddDokterActionPerformed

    private void txtPPnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtPPnActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtPPnActionPerformed

    private void chkDiscRpItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_chkDiscRpItemStateChanged
        udfSetSubTotalItem();
}//GEN-LAST:event_chkDiscRpItemStateChanged

    private void chkPPnRpActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chkPPnRpActionPerformed
        // TODO add your handling code here:
}//GEN-LAST:event_chkPPnRpActionPerformed

    private void chkPPnRpItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_chkPPnRpItemStateChanged
        udfSetSubTotalItem();
}//GEN-LAST:event_chkPPnRpItemStateChanged

    private void btnAccCustMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnAccCustMouseClicked
        udfNewCustomer();
    }//GEN-LAST:event_btnAccCustMouseClicked

    private void btnAddDokterMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnAddDokterMouseClicked
        udfNewDokter();
    }//GEN-LAST:event_btnAddDokterMouseClicked

    private void txtGudangKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtGudangKeyReleased
        fn.lookup(evt, new Object[]{lblGudang}, "select kode_gudang, coalesce(deskripsi,'') as gudang "
                + "from gudang where kode_gudang||coalesce(deskripsi,'') ilike '%"+txtGudang.getText()+"%'", txtGudang.getWidth()+lblGudang.getWidth()+18, 100);
    }//GEN-LAST:event_txtGudangKeyReleased

    private void udfSetSubTotalItem(){
        double subTotal=fn.udfGetDouble(txtQty.getText())*fn.udfGetDouble(txtHarga.getText());
        subTotal=chkDiscRp.isSelected()? subTotal-fn.udfGetDouble(txtDisc.getText()): subTotal*(1-fn.udfGetDouble(txtDisc.getText())/100);
        subTotal=chkPPnRp.isSelected()? subTotal+fn.udfGetDouble(txtPPn.getText()): subTotal*(1+fn.udfGetDouble(txtPPn.getText())/100);
        lblSubTotal.setText(fn.dFmt.format(subTotal));
    }

    public void udfSetNewCustomer(String sKode, String sNama){
        txtCustomer.setText(sKode);
        txtNamaCustomer.setText(sNama);
        if(!txtCustomer.isFocusOwner())
            txtKode.requestFocusInWindow();
    }

    private void udfNewCustomer() {
        DlgPasien fMaster=new DlgPasien(this, true);
        fMaster.setTitle("Pasien/ Customer baru");
        fMaster.setConn(conn);
        fMaster.setSrcForm(this);
        fMaster.setVisible(true);
        
    }
    /**
    * @param args the command line arguments
    */
    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new FrmPenjualan2().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnAccCust;
    private javax.swing.JButton btnAddDokter;
    private javax.swing.JButton btnCancel;
    private javax.swing.JButton btnNew;
    private javax.swing.JButton btnPrint;
    private javax.swing.JButton btnSave;
    private javax.swing.JCheckBox chkDiscRp;
    private javax.swing.JCheckBox chkPPnRp;
    private javax.swing.JComboBox cmbCustPembayaran;
    private javax.swing.JComboBox cmbSatuan;
    private javax.swing.JFormattedTextField jFJtTempo;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JToolBar jToolBar1;
    private javax.swing.JTextField lblGudang;
    private javax.swing.JLabel lblItem;
    private javax.swing.JLabel lblKonv;
    private javax.swing.JLabel lblSubTotal;
    private javax.swing.JLabel lblTgl;
    private javax.swing.JLabel lblTotal;
    private javax.swing.JTable table;
    private javax.swing.JTextField txtCatatan;
    private javax.swing.JTextField txtCustomer;
    private javax.swing.JTextField txtDisc;
    private javax.swing.JTextField txtDokter;
    private javax.swing.JTextField txtGudang;
    private javax.swing.JTextField txtHarga;
    private javax.swing.JTextField txtKode;
    private javax.swing.JTextField txtNamaCustomer;
    private javax.swing.JTextField txtNamaDokter;
    private javax.swing.JTextField txtNoTrx;
    private javax.swing.JTextField txtPPn;
    private javax.swing.JTextField txtQty;
    private javax.swing.JTextField txtTop;
    // End of variables declaration//GEN-END:variables

    private FocusListener txtFocusListener=new FocusListener() {
        public void focusGained(FocusEvent e) {
            if(e.getSource() instanceof JTextField || e.getSource() instanceof JFormattedTextField){
                ((JTextField)e.getSource()).setBackground(Color.YELLOW);
                if((e.getSource() instanceof JTextField)){
                    ((JTextField)e.getSource()).setSelectionStart(0);
                    ((JTextField)e.getSource()).setSelectionEnd(((JTextField)e.getSource()).getText().length());
                }
//                else if(e.getSource().equals(txtKelas) && !fn.isListVisible()){
//                    sOldKelas=txtKelas.getText();
//                }
            }
        }

        public void focusLost(FocusEvent e) {
            if(e.getSource() instanceof  JTextField  || e.getSource() instanceof  JFormattedTextField){
                ((JTextField)e.getSource()).setBackground(Color.WHITE);
                if(e.getSource().equals(txtCustomer))
                    setTitle("Penjualan NON RESEP (Multi Satuan)- "+txtNamaCustomer.getText());
                else if(e.getSource().equals(txtNoTrx) && aThis.isShowing() && aThis.isFocusable() && 
                        txtNoTrx.getText().trim().length()>0)
                    udfLoadKoreksiJual();
                else if(e.getSource().equals(txtKode) && !e.isTemporary()){
                    if(txtKode.getText().isEmpty()){
                        udfClearItem();

                    }else{
                        String sMessage=udfLoadItem();
                        if(sMessage.length()>0){
                            if(!e.isTemporary())
                                txtKode.requestFocusInWindow();

                            JOptionPane.showMessageDialog(aThis, sMessage);
                            return;
                        }
                        txtQty.requestFocusInWindow();
                    }

                    
                }
                    
           }
        }
    } ;

}
