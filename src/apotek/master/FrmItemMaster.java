/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * FrmItemMaster.java
 *
 * Created on May 22, 2012, 10:35:43 AM
 */
package apotek.master;

import apotek.DLgLookup;
import apotek.dao.ItemDao;
import com.klinik.model.BarangPaket;
import main.MainForm;
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
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFormattedTextField;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.TableColumnModel;
import javax.swing.text.html.StyleSheet;
import main.GeneralFunction;

/**
 *
 * @author ustadho
 */
public class FrmItemMaster extends javax.swing.JInternalFrame {
    private Connection conn;
    private GeneralFunction fn=new GeneralFunction();
    private Component aThis;
    private boolean isDummy=false;
    MyKeyListener kListener=new MyKeyListener();
    private String sKodeBarang="";
    private Object srcForm=null;
    List<BarangPaket> listPaket=new ArrayList<BarangPaket>();
    private ItemDao itemDao=new ItemDao();
    
    /** Creates new form FrmItemMaster */
    public FrmItemMaster(boolean b) {
        initComponents();
        isDummy=b;
        fn.addKeyListenerInContainer(jPanel1, kListener, txtFocusListener);
        fn.addKeyListenerInContainer(jPanel2, kListener, txtFocusListener);
        btnSave.addKeyListener(kListener);
        btnClose.addKeyListener(kListener);
        btnDetailPaket.setVisible(false);
    }
    
    public void setSrcForm(Object ob){
        this.srcForm=ob;
    }
    
    public void setConn(Connection con){
        this.conn=con;
        fn.setConn(conn);
    }
    
    
    
    private void udfLoadItem() {
        String sQry = "select b.item_code as kode_barang, coalesce(b.barcode,'') as barcode, " +
                "coalesce(b.item_name,'') as nama_barang, coalesce(b.nama_paten,'') as nama_paten, " +
                "coalesce(b.satuan_kecil,'') as uom_kecil, coalesce(b.dosis,1) as dosis," +
                "coalesce(b.min,0) as min, coalesce(b.max,0) as max, coalesce(b.diskon_box,0) as diskon_box, " +
                "coalesce(b.base_price,0) as base_price, coalesce(b.margin,0) as margin," +
                "coalesce(b.kode_jenis,'') as kode_jenis, coalesce(j.jenis_barang,'') as jenis_barang, " +
                "coalesce(b.group_id,'') as group_id, coalesce(g.group_name,'') as group_name, " +
                "coalesce(b.bentuk_id, '') as bentuk_id, coalesce(bt.bentuk_name,'') as bentuk_name, " +
                "coalesce(b.manufaktur_id, '') as manufaktur_id, coalesce(mn.nama_manufaktur,'') as nama_manufaktur," +
                "coalesce(b.keterangan,'') as keterangan, " +
                "coalesce(b.pr_automatic, true) as pr_automatic, coalesce(b.consignment, false) as consignment, " +
                "coalesce(b.discontinued, false) as discontinued, coalesce(b.indikasi,'') as indikasi,"
                + "case when b.kategori='J' then 'JASA' when b.kategori='N' then 'NON INVENTORI' else 'INVENTORI' end as kategori, "
                + "coalesce(b.paket, false) as paket " +
                "from barang b " +
                "left join jenis_barang j on j.kode_jenis=b.kode_jenis " +
                "left join item_group g on g.group_id=b.group_id " +
                "left join item_bentuk bt on bt.bentuk_id=b.bentuk_id " +
                "left join item_manufaktur mn on mn.id=b.manufaktur_id " +
                "where b.item_code='"+sKodeBarang+"'";

        try {
            Statement st = conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY);
            ResultSet rs = st.executeQuery(sQry);
            System.out.println(sQry);

            if (rs.next()) {
                txtProductID.setText(rs.getString("kode_barang"));
                txtBarcode.setText(rs.getString("barcode"));
                txtNamaBarang.setText(rs.getString("nama_barang"));
                txtNamaPatent.setText(rs.getString("nama_paten"));
                cmbSatuan.setSelectedItem(rs.getString("uom_kecil"));
//                txtDosis.setText(fn.intFmt.format(rs.getFloat("dosis")));
                txtMin.setText(fn.intFmt.format(rs.getFloat("min")));
                txtMax.setText(fn.intFmt.format(rs.getFloat("max")));
//                txtDiscBox.setText(fn.intFmt.format(rs.getFloat("diskon_box")));
                txtGroup.setText(rs.getString("group_id"));
                lblGroup.setText(rs.getString("group_name"));
                txtJenis.setText(rs.getString("kode_jenis"));
                lblJenis.setText(rs.getString("jenis_barang"));
                txtBentuk.setText(rs.getString("bentuk_id"));
                lblBentuk.setText(rs.getString("bentuk_name"));
                txtManufaktur.setText(rs.getString("manufaktur_id"));
                lblManufaktur.setText(rs.getString("nama_manufaktur"));
                txtKeterangan.setText(rs.getString("keterangan"));
                txtIndikasi.setText(rs.getString("indikasi"));
                cmbKategori.setSelectedItem(rs.getString("kategori"));
                txtBasePrice.setText(fn.intFmt.format(rs.getFloat("base_price")));
                txtMargin.setText(fn.dFmt.format(rs.getFloat("margin")));

                chkDiscontinued.setSelected(rs.getBoolean("discontinued"));
                chkAutomatic.setSelected(rs.getBoolean("pr_automatic"));
                chkConsignment.setSelected(rs.getBoolean("consignment"));
                chkPaket.setSelected(rs.getBoolean("paket"));
                btnDetailPaket.setVisible(rs.getBoolean("paket"));
                setHargaJual();
                listPaket=itemDao.getListPaket(txtProductID.getText());
            }

            rs.close();
            st.close();

        } catch (SQLException se) {
            System.out.println(se.getMessage());
        }
    }
    
    private void udfLookupItem(){
        DLgLookup d1=new DLgLookup(JOptionPane.getFrameForComponent(aThis), true);
        String sSupplier="";
        
        String s="select * from (" +
                "select i.item_code as kode_barang, coalesce(item_name,'') as nama_barang, "
                + "coalesce(j.jenis_barang,'') as jenis_barang, "
                + "coalesce(g.group_name,'') as group_name from " +
                "barang i "
                + "left join jenis_barang j on j.kode_jenis=i.kode_jenis "
                + "left join item_group g on g.group_id=i.group_id "+
                "order by 2) x ";

        //System.out.println(s);
//                    ((DefaultTableModel)tblSupplier.getModel()).setNumRows(tblSupplier.getRowCount()+1);
//                    tblSupplier.setRowSelectionInterval(tblSupplier.getRowCount()-1, tblSupplier.getRowCount()-1);
        d1.setTitle("Lookup item");
        d1.udfLoad(conn, s, "(kode_barang||nama_barang)", null);

        d1.setVisible(true);

        //System.out.println("Kode yang dipilih" +d1.getKode());
        if(d1.getKode().length()>0){
            TableColumnModel col=d1.getTable().getColumnModel();
            JTable tbl=d1.getTable();
            int iRow = tbl.getSelectedRow();

            sKodeBarang= tbl.getValueAt(iRow, col.getColumnIndex("kode_barang")).toString();
            udfLoadItem();    
        }
    }

    private void setHargaJual(){
        double harga=fn.udfGetDouble(txtBasePrice.getText())*(1+fn.udfGetDouble(txtMargin.getText())/100);
        harga=fn.roundUp(harga, 50D);
        lblHargaJual.setText(fn.intFmt.format(harga));
    }
    private boolean cekBeforeSave() {
        boolean st = true;
//        if (txtProductID.getText().trim().equalsIgnoreCase("")) {
//            JOptionPane.showMessageDialog(this, "Silakan isi Nama Barang terlebih dulu!");
//            if(!txtProductID.isFocusOwner())
//                txtProductID.requestFocus();
//            return false;
//        }
        if (txtNamaBarang.getText().trim().equalsIgnoreCase("")) {
            JOptionPane.showMessageDialog(this, "Silakan isi Nama Barang terlebih dulu!");
            if(!txtNamaBarang.isFocusOwner())
                txtNamaBarang.requestFocus();
            return false;
        }

        if (cmbSatuan.getSelectedItem().toString().trim().equalsIgnoreCase("")) {
            JOptionPane.showMessageDialog(this, "Silakan isi Uom Kecil terlebih dulu!");
            if(!cmbSatuan.isFocusOwner())
                cmbSatuan.requestFocus();
            return false;
        }

        if (txtGroup.getText().trim().equalsIgnoreCase("")) {
            JOptionPane.showMessageDialog(this, "Silakan isi Group terlebih dulu!");
            if(!txtGroup.isFocusOwner())
                txtGroup.requestFocus();
            return false;
        }

        if (txtJenis.getText().trim().equalsIgnoreCase("")) {
            JOptionPane.showMessageDialog(this, "Silakan isi Jenis item terlebih dulu!");
            txtJenis.requestFocus();
            st = false;
            return st;
        }

        if (txtBentuk.getText().trim().equalsIgnoreCase("")) {
            JOptionPane.showMessageDialog(this, "Silakan isi Bentuk terlebih dulu!");
            txtBentuk.requestFocus();
            st = false;
            return st;
        }
        if(chkPaket.isSelected() && listPaket.size()==0){
            JOptionPane.showMessageDialog(this, "Item paket masih kosong!");
            btnDetailPaketActionPerformed(null);
            st = false;
            return st;
        }
        
//        if (fn.udfGetInt(txtDosis.getText())<=0) {
//            JOptionPane.showMessageDialog(this, "Isikan dosis dengan angka >=1!");
//            if(!txtDosis.isFocusOwner())
//                txtDosis.requestFocus();
//            st = false;
//            return st;
//        }
        return st;
    }
    
    private void udfSave() {
        try {
            boolean isNew=false;
            if(!btnSave.isEnabled() || !cekBeforeSave())
                return;
            ResultSet rs=conn.createStatement().executeQuery("select now()");
            rs.next();
            String sNow=rs.getString(1);
            rs.close();

            rs=conn.createStatement(ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_UPDATABLE)
                    .executeQuery("select * from barang where item_code='"+sKodeBarang+"'");
            if(!rs.next()){
                isNew=true;
                txtProductID.setText(getNewKodeBarang());
                rs.moveToInsertRow();
            }
            rs.updateString("item_code", txtProductID.getText());
            rs.updateString("item_name", txtNamaBarang.getText());
            rs.updateString("nama_paten", txtNamaPatent.getText());
            rs.updateString("satuan_kecil", cmbSatuan.getSelectedItem().toString());
            rs.updateString("keterangan", txtKeterangan.getText());
            rs.updateDouble("base_price", fn.udfGetDouble(txtBasePrice.getText()));
            rs.updateDouble("min", fn.udfGetDouble(txtMin.getText()));
            rs.updateDouble("max", fn.udfGetDouble(txtMax.getText()));
            rs.updateDouble("diskon_box", 0);
            rs.updateDouble("margin", fn.udfGetDouble(txtMargin.getText()));
            rs.updateBoolean("discontinued", chkDiscontinued.isSelected());
            rs.updateString("barcode", txtBarcode.getText());
            rs.updateBoolean("pr_automatic", chkAutomatic.isSelected());
            rs.updateBoolean("consignment", chkConsignment.isSelected());
            rs.updateString("group_id", txtGroup.getText());
            rs.updateString("kode_jenis", txtJenis.getText());
            rs.updateString("bentuk_id", txtBentuk.getText());
            rs.updateString("manufaktur_id", txtManufaktur.getText());
            rs.updateString("indikasi", txtIndikasi.getText());
            rs.updateDouble("dosis", 1);
            rs.updateBoolean("cetak_di_faktur", chkCetakInvoice.isSelected());
            rs.updateString("kategori", cmbKategori.getSelectedItem().toString().substring(0, 1));
            rs.updateBoolean("paket", chkPaket.isSelected());
            //factory_id character varying(4),

          if(isNew){
              rs.updateString("user_ins", MainForm.sUserName);
              rs.insertRow();
          }else{
              rs.updateString("user_upd", MainForm.sUserName);
              rs.updateTimestamp("time_upd", new java.sql.Timestamp(new java.util.Date().getTime()));
              rs.updateRow();
          }
        itemDao.clearItemPaket(txtProductID.getText());
        if(chkPaket.isSelected()){
            for(BarangPaket x: listPaket){
                x.setKodePaket(txtProductID.getText());
            }
            itemDao.simpanItemPaket(listPaket);
        }    
          JOptionPane.showMessageDialog(this, "Insert/update master item sukses!");
          if(srcForm!=null && srcForm instanceof FrmItemDummyList){
              ((FrmItemDummyList)srcForm).udfLoadItem(txtProductID.getText());
              this.dispose();
          }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage());
        }
        
    }

    public void setKodeBarang(String sKode) {
        this.sKodeBarang=sKode;
    }

    private void setLocationRelativeTo(Object object) {
        
    }
    
    private String getNewKodeBarang(){
        String newKode="";
        try {
            ResultSet rs=conn.createStatement().executeQuery("select fn_get_new_kode_barang('"+txtNamaBarang.getText()+"')");
            if(rs.next()){
                newKode=rs.getString(1);
            }
            
        } catch (SQLException ex) {
            Logger.getLogger(FrmItemMaster.class.getName()).log(Level.SEVERE, null, ex);
        }
        return newKode;
    }
    
    public class MyKeyListener extends KeyAdapter {
        @Override
        public void keyTyped(java.awt.event.KeyEvent evt) {
//          if(evt.getSource().equals(tblPR)){
//              if(tblPR.getSelectedColumn()!=tblPR.getColumnModel().getColumnIndex("On Receipt") && stMinus){
//                  evt.consume();
//                  return;
//              }
//          }
            
          if(evt.getSource().equals(txtMin)||evt.getSource().equals(txtMax)||evt.getSource().equals(txtBasePrice)||evt.getSource().equals(txtMargin))
              fn.keyTyped(evt);
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
                            "SHS Pharmacy",JOptionPane.YES_NO_OPTION)==JOptionPane.YES_OPTION){
                        dispose();
                    }
                    break;
                }
                case KeyEvent.VK_F2:{
                    udfSave();
                    break;
                }
                case KeyEvent.VK_F9:{
                    udfLookupItem();
                    break;
                }
                
                case KeyEvent.VK_INSERT:{
                    DLgLookup d1=new DLgLookup(JOptionPane.getFrameForComponent(aThis), true);
                    
                    String s="select * from (" +
                            "select kode_barang, coalesce(nama_barang,'') as nama_barang from phar_item i "
                            + " " +
                            (isDummy? " where  i.group_id not in('999') " : "")+
                            "order by nama_barang )x ";

                    //System.out.println(s);

                    d1.setTitle("Lookup Item");
                    d1.udfLoad(conn, s, "(kode_barang||nama_barang)", null);

                    d1.setVisible(true);

                    //System.out.println("Kode yang dipilih" +d1.getKode());
                    if(d1.getKode().length()>0){
                        TableColumnModel col=d1.getTable().getColumnModel();
                        JTable tbl=d1.getTable();
                        int iRow = tbl.getSelectedRow();
                        
                        txtProductID.setText(tbl.getValueAt(iRow, col.getColumnIndex("kode_barang")).toString());
                        
                    }

                    break;
                }

            }
        }

        @Override
        public void keyReleased(KeyEvent evt){
            String sQry="";
            if(evt.getSource().equals(txtGroup)){
                 sQry="select group_id, coalesce(group_name,'') as group_name "
                        + "from item_group "
                        + "where group_id||coalesce(group_name,'') ilike '%"+txtGroup.getText()+"%' order by 2";

                fn.lookup(evt, new Object[]{lblGroup}, sQry, txtGroup.getWidth()+lblGroup.getWidth()+18, 150);
            
            }else if(evt.getSource().equals(txtJenis)){
                sQry="select kode_jenis, coalesce(jenis_barang,'') as jenis_barang "
                        + "from jenis_barang "
                        + "where kode_jenis||coalesce(jenis_barang,'') ilike '%"+txtJenis.getText()+"%' order by 2";

                fn.lookup(evt, new Object[]{lblJenis}, sQry, txtJenis.getWidth()+lblJenis.getWidth()+18, 150);
            
            }else if(evt.getSource().equals(txtBentuk)){
                sQry="select bentuk_id, coalesce(bentuk_name,'') as bentuk "
                        + "from item_bentuk "
                        + "where bentuk_id||coalesce(bentuk_name,'') ilike '%"+txtBentuk.getText()+"%' order by 2";

                fn.lookup(evt, new Object[]{lblBentuk}, sQry, txtBentuk.getWidth()+lblBentuk.getWidth()+18, 150);
           
            }else if(evt.getSource().equals(txtManufaktur)){
                sQry="select id, coalesce(nama_manufaktur,'') as nama_manufaktur "
                        + "from item_manufaktur "
                        + "where id||coalesce(nama_manufaktur,'') ilike '%"+txtManufaktur.getText()+"%' order by 2";

                fn.lookup(evt, new Object[]{lblManufaktur}, sQry, txtManufaktur.getWidth()+lblManufaktur.getWidth()+18, 150);
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
    
    private FocusListener txtFocusListener=new FocusListener() {
        public void focusGained(FocusEvent e) {
            if(e.getSource() instanceof JTextField || e.getSource() instanceof JFormattedTextField){
                ((JTextField)e.getSource()).setBackground(Color.YELLOW);
                if((e.getSource() instanceof JTextField && ((JTextField)e.getSource()).getName()!=null && ((JTextField)e.getSource()).getName().equalsIgnoreCase("textEditor"))){
                    ((JTextField)e.getSource()).setSelectionStart(0);
                    ((JTextField)e.getSource()).setSelectionEnd(((JTextField)e.getSource()).getText().length());
                }
            }
        }


        public void focusLost(FocusEvent e) {
            if(e.getSource().getClass().getSimpleName().equalsIgnoreCase("JTextField")||
                    e.getSource().getClass().getSimpleName().equalsIgnoreCase("JFormattedTextField")){
                ((JTextField)e.getSource()).setBackground(Color.WHITE);

                if(e.getSource().equals(txtBasePrice) || e.getSource().equals(txtMargin)){
                    setHargaJual();
                }
//                if(e.getSource().equals(txtNoPO) && !fn.isListVisible())
//                    udfLoadItemFromPO();
//                else if(e.getSource().equals(txtNoGR))
//                    udfLoadGR();
           }
        }


    } ;
    
    private void udfInitForm(){
        aThis=this;
//        txtGroup.setEnabled(!isDummy);
//        if(isDummy){
            try{
                cmbSatuan.removeAllItems();
                ResultSet rs=conn.createStatement()
                        .executeQuery("select uom from uom order by 1");
                while(rs.next()){
                    cmbSatuan.addItem(rs.getString(1));
                }
                rs.close();
            }catch(SQLException se){
                JOptionPane.showMessageDialog(this, se.getMessage());
            }
//        }
        
        if(sKodeBarang.length()>0)
            udfLoadItem();
        
        java.awt.EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                txtNamaBarang.requestFocusInWindow();
            }
        });
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
        txtProductID = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        txtNamaBarang = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        txtNamaPatent = new javax.swing.JTextField();
        jLabel5 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        txtKeterangan = new javax.swing.JTextField();
        jLabel8 = new javax.swing.JLabel();
        txtMin = new javax.swing.JTextField();
        jLabel9 = new javax.swing.JLabel();
        txtMax = new javax.swing.JTextField();
        jXPanel1 = new org.jdesktop.swingx.JXPanel();
        chkDiscontinued = new javax.swing.JCheckBox();
        chkAutomatic = new javax.swing.JCheckBox();
        chkConsignment = new javax.swing.JCheckBox();
        chkCetakInvoice = new javax.swing.JCheckBox();
        jLabel10 = new javax.swing.JLabel();
        txtBarcode = new javax.swing.JTextField();
        jLabel11 = new javax.swing.JLabel();
        jPanel2 = new javax.swing.JPanel();
        jLabel15 = new javax.swing.JLabel();
        jTextField11 = new javax.swing.JTextField();
        jLabel16 = new javax.swing.JLabel();
        jTextField12 = new javax.swing.JTextField();
        jLabel17 = new javax.swing.JLabel();
        jTextField13 = new javax.swing.JTextField();
        jLabel18 = new javax.swing.JLabel();
        jTextField14 = new javax.swing.JTextField();
        jLabel19 = new javax.swing.JLabel();
        jLabel20 = new javax.swing.JLabel();
        jTextField15 = new javax.swing.JTextField();
        jLabel21 = new javax.swing.JLabel();
        jTextField16 = new javax.swing.JTextField();
        jLabel22 = new javax.swing.JLabel();
        jTextField17 = new javax.swing.JTextField();
        jLabel23 = new javax.swing.JLabel();
        jTextField18 = new javax.swing.JTextField();
        jXPanel2 = new org.jdesktop.swingx.JXPanel();
        chkDiscontinued1 = new javax.swing.JCheckBox();
        chkAutomatic1 = new javax.swing.JCheckBox();
        chkConsignment1 = new javax.swing.JCheckBox();
        chkShow1 = new javax.swing.JCheckBox();
        chkFormularium1 = new javax.swing.JCheckBox();
        jLabel24 = new javax.swing.JLabel();
        jTextField19 = new javax.swing.JTextField();
        jLabel25 = new javax.swing.JLabel();
        jLabel26 = new javax.swing.JLabel();
        jComboBox2 = new javax.swing.JComboBox();
        jLabel27 = new javax.swing.JLabel();
        jLabel28 = new javax.swing.JLabel();
        jTextField20 = new javax.swing.JTextField();
        txtGroup = new javax.swing.JTextField();
        lblGroup = new javax.swing.JLabel();
        jLabel30 = new javax.swing.JLabel();
        jLabel31 = new javax.swing.JLabel();
        txtJenis = new javax.swing.JTextField();
        lblJenis = new javax.swing.JLabel();
        jLabel33 = new javax.swing.JLabel();
        txtBentuk = new javax.swing.JTextField();
        lblBentuk = new javax.swing.JLabel();
        jButton1 = new javax.swing.JButton();
        jLabel4 = new javax.swing.JLabel();
        txtMargin = new javax.swing.JTextField();
        lblHargaJual = new javax.swing.JLabel();
        jLabel34 = new javax.swing.JLabel();
        txtManufaktur = new javax.swing.JTextField();
        lblManufaktur = new javax.swing.JLabel();
        jLabel13 = new javax.swing.JLabel();
        txtIndikasi = new javax.swing.JTextField();
        txtBasePrice = new javax.swing.JTextField();
        cmbSatuan = new javax.swing.JComboBox();
        jLabel12 = new javax.swing.JLabel();
        cmbKategori = new javax.swing.JComboBox();
        jLabel6 = new javax.swing.JLabel();
        btnSave = new javax.swing.JButton();
        btnClose = new javax.swing.JButton();
        chkPaket = new javax.swing.JCheckBox();
        btnDetailPaket = new javax.swing.JButton();

        setClosable(true);
        setTitle("Master Item");
        addInternalFrameListener(new javax.swing.event.InternalFrameListener() {
            public void internalFrameActivated(javax.swing.event.InternalFrameEvent evt) {
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

        jPanel1.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        jPanel1.setLayout(null);

        jLabel1.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jLabel1.setText("Kode Barang");
        jPanel1.add(jLabel1);
        jLabel1.setBounds(20, 20, 80, 20);

        txtProductID.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        txtProductID.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        txtProductID.setEnabled(false);
        jPanel1.add(txtProductID);
        txtProductID.setBounds(100, 20, 140, 20);

        jLabel2.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jLabel2.setText("Nama Item");
        jPanel1.add(jLabel2);
        jLabel2.setBounds(20, 45, 80, 20);

        txtNamaBarang.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        txtNamaBarang.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        jPanel1.add(txtNamaBarang);
        txtNamaBarang.setBounds(100, 45, 440, 20);

        jLabel3.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jLabel3.setText("Nama Paten");
        jPanel1.add(jLabel3);
        jLabel3.setBounds(20, 70, 80, 20);

        txtNamaPatent.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        txtNamaPatent.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        jPanel1.add(txtNamaPatent);
        txtNamaPatent.setBounds(100, 70, 440, 20);

        jLabel5.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jLabel5.setText("Satuan Kecil");
        jPanel1.add(jLabel5);
        jLabel5.setBounds(20, 95, 80, 20);

        jLabel7.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jLabel7.setText("Keterangan");
        jPanel1.add(jLabel7);
        jLabel7.setBounds(20, 320, 80, 20);

        txtKeterangan.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        txtKeterangan.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        jPanel1.add(txtKeterangan);
        txtKeterangan.setBounds(100, 320, 440, 20);

        jLabel8.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jLabel8.setText("Min");
        jPanel1.add(jLabel8);
        jLabel8.setBounds(20, 120, 80, 20);

        txtMin.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        txtMin.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtMin.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        jPanel1.add(txtMin);
        txtMin.setBounds(100, 120, 70, 20);

        jLabel9.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jLabel9.setText("Max");
        jPanel1.add(jLabel9);
        jLabel9.setBounds(190, 120, 80, 20);

        txtMax.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        txtMax.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtMax.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        jPanel1.add(txtMax);
        txtMax.setBounds(270, 120, 70, 20);

        jXPanel1.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        chkDiscontinued.setText("Discontinued");
        chkDiscontinued.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        jXPanel1.add(chkDiscontinued);

        chkAutomatic.setText("Otomatis PR");
        chkAutomatic.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        jXPanel1.add(chkAutomatic);

        chkConsignment.setText("Konsinyasi");
        chkConsignment.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        jXPanel1.add(chkConsignment);

        chkCetakInvoice.setSelected(true);
        chkCetakInvoice.setText("Cetak di Kwt Penjualan");
        chkCetakInvoice.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        jXPanel1.add(chkCetakInvoice);

        jPanel1.add(jXPanel1);
        jXPanel1.setBounds(20, 350, 520, 30);

        jLabel10.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jLabel10.setText("Base Price");
        jPanel1.add(jLabel10);
        jLabel10.setBounds(20, 145, 80, 20);

        txtBarcode.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        txtBarcode.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        jPanel1.add(txtBarcode);
        txtBarcode.setBounds(385, 20, 150, 20);

        jLabel11.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jLabel11.setText("Barcode");
        jPanel1.add(jLabel11);
        jLabel11.setBounds(305, 20, 80, 20);

        jPanel2.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        jPanel2.setLayout(null);

        jLabel15.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jLabel15.setText("ProductID");
        jPanel2.add(jLabel15);
        jLabel15.setBounds(20, 20, 80, 20);

        jTextField11.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jTextField11.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        jPanel2.add(jTextField11);
        jTextField11.setBounds(100, 20, 140, 20);

        jLabel16.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jLabel16.setText("Nama Item");
        jPanel2.add(jLabel16);
        jLabel16.setBounds(20, 45, 80, 20);

        jTextField12.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jTextField12.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        jPanel2.add(jTextField12);
        jTextField12.setBounds(100, 45, 440, 20);

        jLabel17.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jLabel17.setText("Nama Paten");
        jPanel2.add(jLabel17);
        jLabel17.setBounds(20, 70, 80, 20);

        jTextField13.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jTextField13.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        jPanel2.add(jTextField13);
        jTextField13.setBounds(100, 70, 440, 20);

        jLabel18.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jLabel18.setText("0");
        jLabel18.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        jPanel2.add(jLabel18);
        jLabel18.setBounds(350, 170, 100, 20);

        jTextField14.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jTextField14.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        jPanel2.add(jTextField14);
        jTextField14.setBounds(100, 95, 70, 20);

        jLabel19.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jLabel19.setText("UoM Jual");
        jPanel2.add(jLabel19);
        jLabel19.setBounds(20, 95, 80, 20);

        jLabel20.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jLabel20.setText("CGP Code");
        jPanel2.add(jLabel20);
        jLabel20.setBounds(340, 20, 80, 20);

        jTextField15.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jTextField15.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        jPanel2.add(jTextField15);
        jTextField15.setBounds(420, 20, 120, 20);

        jLabel21.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jLabel21.setText("Keterangan");
        jPanel2.add(jLabel21);
        jLabel21.setBounds(20, 340, 80, 20);

        jTextField16.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jTextField16.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        jPanel2.add(jTextField16);
        jTextField16.setBounds(100, 340, 440, 20);

        jLabel22.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jLabel22.setText("Min");
        jPanel2.add(jLabel22);
        jLabel22.setBounds(20, 120, 80, 20);

        jTextField17.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jTextField17.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        jPanel2.add(jTextField17);
        jTextField17.setBounds(100, 120, 70, 20);

        jLabel23.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jLabel23.setText("Max");
        jPanel2.add(jLabel23);
        jLabel23.setBounds(190, 120, 80, 20);

        jTextField18.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jTextField18.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        jPanel2.add(jTextField18);
        jTextField18.setBounds(270, 120, 70, 20);

        jXPanel2.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        chkDiscontinued1.setText("Discontinued");
        chkDiscontinued1.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        jXPanel2.add(chkDiscontinued1);

        chkAutomatic1.setText("Automatic");
        chkAutomatic1.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        jXPanel2.add(chkAutomatic1);

        chkConsignment1.setText("Consignment");
        chkConsignment1.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        jXPanel2.add(chkConsignment1);

        chkShow1.setSelected(true);
        chkShow1.setText("Show in Invoice");
        chkShow1.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        jXPanel2.add(chkShow1);

        chkFormularium1.setText("Formularium");
        chkFormularium1.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        jXPanel2.add(chkFormularium1);

        jPanel2.add(jXPanel2);
        jXPanel2.setBounds(20, 370, 570, 30);

        jLabel24.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jLabel24.setText("Base Price");
        jPanel2.add(jLabel24);
        jLabel24.setBounds(270, 170, 80, 20);

        jTextField19.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jTextField19.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        jPanel2.add(jTextField19);
        jTextField19.setBounds(100, 145, 150, 20);

        jLabel25.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jLabel25.setText("Barcode");
        jPanel2.add(jLabel25);
        jLabel25.setBounds(20, 145, 80, 20);

        jLabel26.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jLabel26.setText("Nama Paten");
        jLabel26.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        jPanel2.add(jLabel26);
        jLabel26.setBounds(170, 95, 180, 20);

        jComboBox2.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jComboBox2.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Medical", "Supplies" }));
        jPanel2.add(jComboBox2);
        jComboBox2.setBounds(100, 170, 150, 21);

        jLabel27.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jLabel27.setText("Base Price");
        jPanel2.add(jLabel27);
        jLabel27.setBounds(20, 170, 80, 20);

        jLabel28.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jLabel28.setText("Barcode");
        jPanel2.add(jLabel28);
        jLabel28.setBounds(20, 200, 80, 20);

        jTextField20.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jTextField20.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        jPanel2.add(jTextField20);
        jTextField20.setBounds(100, 200, 150, 20);

        jPanel1.add(jPanel2);
        jPanel2.setBounds(0, 0, 0, 0);

        txtGroup.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        txtGroup.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        jPanel1.add(txtGroup);
        txtGroup.setBounds(100, 175, 70, 20);

        lblGroup.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        lblGroup.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        jPanel1.add(lblGroup);
        lblGroup.setBounds(170, 175, 260, 20);

        jLabel30.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jLabel30.setText("Group");
        jPanel1.add(jLabel30);
        jLabel30.setBounds(20, 175, 80, 20);

        jLabel31.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jLabel31.setText("Jenis");
        jPanel1.add(jLabel31);
        jLabel31.setBounds(20, 200, 80, 20);

        txtJenis.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        txtJenis.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        jPanel1.add(txtJenis);
        txtJenis.setBounds(100, 200, 70, 20);

        lblJenis.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        lblJenis.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        jPanel1.add(lblJenis);
        lblJenis.setBounds(170, 200, 260, 20);

        jLabel33.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jLabel33.setText("Bentuk");
        jPanel1.add(jLabel33);
        jLabel33.setBounds(20, 225, 80, 20);

        txtBentuk.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        txtBentuk.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        jPanel1.add(txtBentuk);
        txtBentuk.setBounds(100, 225, 70, 20);

        lblBentuk.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        lblBentuk.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        jPanel1.add(lblBentuk);
        lblBentuk.setBounds(170, 225, 260, 20);

        jButton1.setText("...");
        jButton1.setToolTipText("Lookup item");
        jButton1.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jButton1MouseClicked(evt);
            }
        });
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });
        jPanel1.add(jButton1);
        jButton1.setBounds(240, 17, 45, 23);

        jLabel4.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jLabel4.setText("Magin Harga : ");
        jPanel1.add(jLabel4);
        jLabel4.setBounds(205, 145, 110, 20);

        txtMargin.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtMargin.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        txtMargin.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtMarginKeyReleased(evt);
            }
        });
        jPanel1.add(txtMargin);
        txtMargin.setBounds(315, 145, 40, 22);

        lblHargaJual.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        lblHargaJual.setText("0");
        jPanel1.add(lblHargaJual);
        lblHargaJual.setBounds(410, 145, 130, 20);

        jLabel34.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jLabel34.setText("Manufaktur");
        jPanel1.add(jLabel34);
        jLabel34.setBounds(20, 250, 80, 20);

        txtManufaktur.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        txtManufaktur.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        jPanel1.add(txtManufaktur);
        txtManufaktur.setBounds(100, 250, 70, 20);

        lblManufaktur.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        lblManufaktur.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        jPanel1.add(lblManufaktur);
        lblManufaktur.setBounds(170, 250, 370, 20);

        jLabel13.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jLabel13.setText("<html>Kegunaan / <br>Indikasi</html>");
        jPanel1.add(jLabel13);
        jLabel13.setBounds(20, 290, 80, 30);

        txtIndikasi.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        txtIndikasi.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        jPanel1.add(txtIndikasi);
        txtIndikasi.setBounds(100, 290, 440, 20);

        txtBasePrice.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        txtBasePrice.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtBasePrice.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        txtBasePrice.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtBasePriceKeyReleased(evt);
            }
        });
        jPanel1.add(txtBasePrice);
        txtBasePrice.setBounds(100, 145, 95, 22);

        cmbSatuan.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        jPanel1.add(cmbSatuan);
        cmbSatuan.setBounds(100, 95, 170, 20);

        jLabel12.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jLabel12.setText("Kategori : ");
        jPanel1.add(jLabel12);
        jLabel12.setBounds(290, 95, 80, 20);

        cmbKategori.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "INVENTORI", "NON INVENTORI", "JASA" }));
        jPanel1.add(cmbKategori);
        cmbKategori.setBounds(370, 95, 170, 20);

        jLabel6.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jLabel6.setText("%");
        jPanel1.add(jLabel6);
        jLabel6.setBounds(360, 145, 25, 20);

        getContentPane().add(jPanel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 10, 550, 390));

        btnSave.setText("Save (F2)");
        btnSave.setMargin(new java.awt.Insets(2, 2, 2, 2));
        btnSave.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSaveActionPerformed(evt);
            }
        });
        getContentPane().add(btnSave, new org.netbeans.lib.awtextra.AbsoluteConstraints(370, 405, 90, 30));

        btnClose.setText("Close (Esc)");
        btnClose.setMargin(new java.awt.Insets(2, 2, 2, 2));
        btnClose.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCloseActionPerformed(evt);
            }
        });
        getContentPane().add(btnClose, new org.netbeans.lib.awtextra.AbsoluteConstraints(460, 405, 100, 30));

        chkPaket.setText("Paket");
        chkPaket.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                chkPaketItemStateChanged(evt);
            }
        });
        getContentPane().add(chkPaket, new org.netbeans.lib.awtextra.AbsoluteConstraints(105, 410, 90, -1));

        btnDetailPaket.setText("Detail Paket");
        btnDetailPaket.setMargin(new java.awt.Insets(2, 2, 2, 2));
        btnDetailPaket.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnDetailPaketActionPerformed(evt);
            }
        });
        getContentPane().add(btnDetailPaket, new org.netbeans.lib.awtextra.AbsoluteConstraints(195, 410, 105, -1));

        setBounds(0, 0, 579, 467);
    }// </editor-fold>//GEN-END:initComponents

private void formInternalFrameOpened(javax.swing.event.InternalFrameEvent evt) {//GEN-FIRST:event_formInternalFrameOpened
    udfInitForm();
}//GEN-LAST:event_formInternalFrameOpened

private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
    
}//GEN-LAST:event_jButton1ActionPerformed

private void jButton1MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jButton1MouseClicked
    udfLookupItem();
}//GEN-LAST:event_jButton1MouseClicked

private void btnSaveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSaveActionPerformed
    udfSave();
}//GEN-LAST:event_btnSaveActionPerformed

private void btnCloseActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCloseActionPerformed
    this.dispose();
}//GEN-LAST:event_btnCloseActionPerformed

    private void txtBasePriceKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtBasePriceKeyReleased
        setHargaJual();
    }//GEN-LAST:event_txtBasePriceKeyReleased

    private void txtMarginKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtMarginKeyReleased
        setHargaJual();
    }//GEN-LAST:event_txtMarginKeyReleased

    private void chkPaketItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_chkPaketItemStateChanged
        btnDetailPaket.setVisible(chkPaket.isSelected());
    }//GEN-LAST:event_chkPaketItemStateChanged

    private void btnDetailPaketActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnDetailPaketActionPerformed
        DlgItemPaketDetail d1=new DlgItemPaketDetail(JOptionPane.getFrameForComponent(aThis), true);
        d1.setKodePaket(txtProductID.getText(), txtNamaPatent.getText());
        d1.setLocationRelativeTo(aThis);
        d1.setListItem(listPaket);
        d1.setVisible(true);
        listPaket=d1.getListItem();
    }//GEN-LAST:event_btnDetailPaketActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnClose;
    private javax.swing.JButton btnDetailPaket;
    private javax.swing.JButton btnSave;
    private javax.swing.JCheckBox chkAutomatic;
    private javax.swing.JCheckBox chkAutomatic1;
    private javax.swing.JCheckBox chkCetakInvoice;
    private javax.swing.JCheckBox chkConsignment;
    private javax.swing.JCheckBox chkConsignment1;
    private javax.swing.JCheckBox chkDiscontinued;
    private javax.swing.JCheckBox chkDiscontinued1;
    private javax.swing.JCheckBox chkFormularium1;
    private javax.swing.JCheckBox chkPaket;
    private javax.swing.JCheckBox chkShow1;
    private javax.swing.JComboBox cmbKategori;
    private javax.swing.JComboBox cmbSatuan;
    private javax.swing.JButton jButton1;
    private javax.swing.JComboBox jComboBox2;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel18;
    private javax.swing.JLabel jLabel19;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel20;
    private javax.swing.JLabel jLabel21;
    private javax.swing.JLabel jLabel22;
    private javax.swing.JLabel jLabel23;
    private javax.swing.JLabel jLabel24;
    private javax.swing.JLabel jLabel25;
    private javax.swing.JLabel jLabel26;
    private javax.swing.JLabel jLabel27;
    private javax.swing.JLabel jLabel28;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel30;
    private javax.swing.JLabel jLabel31;
    private javax.swing.JLabel jLabel33;
    private javax.swing.JLabel jLabel34;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JTextField jTextField11;
    private javax.swing.JTextField jTextField12;
    private javax.swing.JTextField jTextField13;
    private javax.swing.JTextField jTextField14;
    private javax.swing.JTextField jTextField15;
    private javax.swing.JTextField jTextField16;
    private javax.swing.JTextField jTextField17;
    private javax.swing.JTextField jTextField18;
    private javax.swing.JTextField jTextField19;
    private javax.swing.JTextField jTextField20;
    private org.jdesktop.swingx.JXPanel jXPanel1;
    private org.jdesktop.swingx.JXPanel jXPanel2;
    private javax.swing.JLabel lblBentuk;
    private javax.swing.JLabel lblGroup;
    private javax.swing.JLabel lblHargaJual;
    private javax.swing.JLabel lblJenis;
    private javax.swing.JLabel lblManufaktur;
    private javax.swing.JTextField txtBarcode;
    private javax.swing.JTextField txtBasePrice;
    private javax.swing.JTextField txtBentuk;
    private javax.swing.JTextField txtGroup;
    private javax.swing.JTextField txtIndikasi;
    private javax.swing.JTextField txtJenis;
    private javax.swing.JTextField txtKeterangan;
    private javax.swing.JTextField txtManufaktur;
    private javax.swing.JTextField txtMargin;
    private javax.swing.JTextField txtMax;
    private javax.swing.JTextField txtMin;
    private javax.swing.JTextField txtNamaBarang;
    private javax.swing.JTextField txtNamaPatent;
    private javax.swing.JTextField txtProductID;
    // End of variables declaration//GEN-END:variables
}
