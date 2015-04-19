/*
 * FrmItemMaster.java
 *
 * Created on December 6, 2006, 5:04 PM
 */

package apotek;

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.FocusTraversalPolicy;
import java.awt.KeyboardFocusManager;
import java.awt.Point;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DecimalFormat;
import javax.swing.InputVerifier;
import javax.swing.JComponent;
import javax.swing.JFormattedTextField;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.text.DefaultFormatter;
import javax.swing.text.DefaultFormatterFactory;
import javax.swing.text.NumberFormatter;
import main.ListRsbm;

/**
 *
 * @author  root
 */
public class FrmItemMaster extends javax.swing.JFrame {
    private boolean bNew, bSaved;
    private boolean bEdit;
    private String sClose="close";
    private ListRsbm lst;
    private Connection conn;
    private String kode_barang;
    private boolean isNew;
    private String namaDepan;
    
    private JTable tblItem;
    private javax.swing.table.DefaultTableModel tblModel;
    private int rowPos;
    
    /** Creates new form FrmItemMaster */
    
    FocusListener fListener=new FocusListener() {
        Color clr1=new Color(204,255,255);
        Color clr0=new Color(255,255,255);
        
        public void focusGained(FocusEvent e) {
            Component c=(Component) e.getSource();
            c.setBackground(clr1);
        }
        public void focusLost(FocusEvent e) {
            Component c=(Component) e.getSource();
            c.setBackground(clr0);
        }
    };
    private Object srcForm;
    
    
   public FrmItemMaster() {
        initComponents();
//        int a=(getWidth()-this.getWidth())/2;
//        int b=(getHeight()-this.getHeight())/2;
////        int x = Math.max(0, parentBounds.x + (parentBounds.width - size.width) / 2);
////        int y = Math.max(0, parentBounds.y + (parentBounds.height - size.height) / 2);
//        setLocation(new Point(a, b));
    

        Component c;
        for(int i=0; i<jPanel1.getComponentCount();i++){
            c = jPanel1.getComponent(i);
            if(c.getClass().getSimpleName().equalsIgnoreCase("JTEXTFIELD") || c.getClass().getSimpleName().equalsIgnoreCase("JFormattedTextField")
            || c.getClass().getSimpleName().equalsIgnoreCase("JTextArea")  || c.getClass().getSimpleName().equalsIgnoreCase("JRadioButton") ) 
                //System.out.println(c.getClass().getSimpleName());
                c.addFocusListener(fListener);
                
            
        }
        
        for(int i=0; i<jPanel2.getComponentCount();i++){
            c = jPanel2.getComponent(i);
            if(c.getClass().getSimpleName().equalsIgnoreCase("JTEXTFIELD") || c.getClass().getSimpleName().equalsIgnoreCase("JFormattedTextField")
            || c.getClass().getSimpleName().equalsIgnoreCase("JTextArea")  || c.getClass().getSimpleName().equalsIgnoreCase("JRadioButton") ) 
                //System.out.println(c.getClass().getSimpleName());
                c.addFocusListener(fListener);
                
            
        }
        for(int i=0; i<jPanel3.getComponentCount();i++){
            c = jPanel3.getComponent(i);
            if(c.getClass().getSimpleName().equalsIgnoreCase("JTEXTFIELD") || c.getClass().getSimpleName().equalsIgnoreCase("JFormattedTextField")
            || c.getClass().getSimpleName().equalsIgnoreCase("JTextArea")  || c.getClass().getSimpleName().equalsIgnoreCase("JRadioButton") ) 
                //System.out.println(c.getClass().getSimpleName());
                c.addFocusListener(fListener);
                
            
        }
        
        
    }
    
   public void setBSaved(boolean b){
        bSaved=b;
   }
   
    public void setConnection(Connection nCon){
        this.conn=nCon;
    }
    
    public void setNew(boolean bSt){
        this.isNew=bSt;
    }
    
    public void udfSetNamaDepan(String sDepan){
        this.namaDepan=sDepan;
    }
    
    public void udfSetRowPos(int rPos){
        this.rowPos=rPos;
    }
    
    public void udfSetTable(JTable tbl){
        tblItem=tbl;
    }
    
    public void udfSetModel(javax.swing.table.DefaultTableModel model){
        tblModel=model;
    }
    
    public void setKodeBarang(String sKode){
        this.kode_barang=sKode;
        this.sItemCode=sKode;
    }
    
    public void setEnabledKode(boolean bSt){
        this.txtKdBrg.setEnabled(bSt);
        
    }
    
    public void setBNew(boolean bNew){
        this.bNew=bNew;
    }
    
    public Boolean getBNew() {
        return bNew;
    }

    public void setBEdit(Boolean lEdit) {
        this.bEdit = lEdit;
    }
    
    public Boolean getBEdit() {
        return bEdit;
    }

    void setObjForm(Object aThis) {
        srcForm=aThis;
    }
    
    public class MyKeyListener extends KeyAdapter {
        public void keyPressed(KeyEvent evt) {
            int keyKode = evt.getKeyCode();
            switch(keyKode){
                
                case KeyEvent.VK_F2: {  //Save
                    if (getBEdit())
                        udfSave();
                    
                    break;
                }
                
                case KeyEvent.VK_F3: {  //Search
//                    udfFilter();
                    
                    break;
                }
                
                case KeyEvent.VK_F4: {  //Edit
                    if(tblSupp.getValueAt(tblSupp.getSelectedRow(),0)!=null){
                        udfEdititemSupplier();
                    }
                    break;
                }
                
                case KeyEvent.VK_F5: {  //New -- Add
                    udfAdd();
                    break;
                }
                
                case KeyEvent.VK_F6: {  //Filter
//                    onOpen(cmbFilter.getSelectedItem().toString(),true);
                    break;
                }
                
                case KeyEvent.VK_F12: {  //Delete
                    if (tblSupp.getRowCount()>0)
                        //if (!getBEdit() && tblSupp.getRowCount()>0)
                        udfDeleteSupplier();
                    
                    break;
                }
                
                case KeyEvent.VK_F10: {
                    udfSetDefaultBasePrice();
                    break;
                }
                
                case KeyEvent.VK_ENTER : {
                    Component ct = KeyboardFocusManager.getCurrentKeyboardFocusManager().getPermanentFocusOwner();
                    if(!(ct.getClass().getSimpleName().equalsIgnoreCase("JTABLE"))){
                        if (!lst.isVisible()){
                            Component c = findNextFocus();
                            c.requestFocus();
                        }else{
                            lst.requestFocus();
                        }
                    }else
                        if(tblSupp.getValueAt(tblSupp.getSelectedRow(),0)!=null)
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
                
                //lempar aja ke udfCancel
                case KeyEvent.VK_ESCAPE: {
                    if(!lst.isVisible()){
                        //Jika status button adalah Close
                        if(sClose.equalsIgnoreCase("close") && !lst.isVisible()){
                            if(!getBEdit()){
                                if(JOptionPane.showConfirmDialog(null,"Anda Yakin Untuk Keluar?","SHS go Open Sources",JOptionPane.YES_NO_OPTION)==JOptionPane.YES_OPTION){
                                    dispose();
                                }
                            }
                            else
                                if(JOptionPane.showConfirmDialog(null,"Apakah data disimpan sebelum anda keluar?","SHS go Open Sources",JOptionPane.YES_NO_OPTION)==JOptionPane.YES_OPTION){
                                    udfSave();
                                }
                                else
                                    dispose();

                                break;
                        }   //Jika cancel
                    }
                        else{
                            lst.setVisible(false);
                            
                        }
                }
            }
        }
    }
    
    private void udfShowItem(){
        String sQry="select b.item_code, b.item_name, coalesce(b.barcode,'') as barcode, coalesce(b.kode_jenis,'') as kode_jenis," +
                "coalesce(jenis_barang,'') as jenis_barang, coalesce(b.satuan_besar,'') as satuan_besar," +
                "coalesce(b.satuan_kecil,'') as satuan_kecil, coalesce(b.konversi, 1) as konversi, " +
                "coalesce(b.default_brows,1) as default_brows, coalesce(b.min_stock,0) as min ," +
                "coalesce(b.max_stock,0) as max, coalesce(b.base_price,0) as base_price," +
                "coalesce(b.keterangan, '') as keterangan, coalesce(b.discontinued,true) as active," +
                "coalesce(b.harga_besar_resep,0) as harga_besar_resep, coalesce(b.harga_besar_non_resep,0) as harga_besar_non_resep," +
                "coalesce(b.harga_kecil_resep,0) as harga_kecil_resep, coalesce(b.harga_kecil_non_resep,0) as harga_kecil_non_resep," +
                "coalesce(b.kode_lokasi,'') as kode_lokasi, coalesce(nama_lokasi,'') as nama_lokasi, " +
                "coalesce(b.kode_barang_sama,'') as kode_barang_sama, coalesce(b2.item_name,'') as nama_barang_sama," +
                "coalesce(b.is_dispensing, false) as is_dispensing " +
                "from barang b " +
                "left join jenis_barang j on j.kode_jenis=b.kode_jenis " +
                "left join gudang g on b.gudang=kode_lokasi " +
                "left join lokasi l on l.kode_lokasi=b.kode_lokasi " +
                "left join barang b2 on b2.item_code=b.kode_barang_sama " +
                "where b.item_code='"+getItemCode()+"'";
        
        try{
            Statement st=conn.createStatement();
            ResultSet rs=st.executeQuery(sQry);
            System.out.println(sQry);
            
            if(rs.next()){
                txtKdBrg.setText(rs.getString("item_code"));
                txtNama.setText(rs.getString("item_name"));
                txtBarcode.setText(rs.getString("barcode"));
                txtJenis.setText(rs.getString("kode_jenis"));
                lblJenis.setText(rs.getString("jenis_barang"));
                txtSatuanBesar.setText(rs.getString("satuan_besar"));
                txtSatuanKecil.setText(rs.getString("satuan_kecil"));
                txtKonversi.setText(df1.format(rs.getFloat("konversi")));
                if(rs.getInt("default_brows")==1)
                    jRBtnSatuanKecil.setSelected(true);
                else
                    jRBtnSatuanBesar.setSelected(false);
                txtMin.setText(df1.format(rs.getFloat("min")));
                txtMax.setText(df1.format(rs.getFloat("max")));
                txtBasePrice.setText(df1.format(rs.getFloat("base_price")));
                txtKet.setText(rs.getString("keterangan"));
                chkDiscontinued.setSelected(rs.getBoolean("active"));
                txtHargaBesarResep.setText(df1.format(rs.getFloat("harga_besar_resep")));
                txtHargaBesarNonResep.setText(df1.format(rs.getFloat("harga_besar_non_resep")));
                txtHargaKecilResep.setText(df1.format(rs.getFloat("harga_kecil_resep")));
                txtHargaKecilNonResep.setText(df1.format(rs.getFloat("harga_kecil_non_resep")));
                
                txtLokasi.setText(rs.getString("kode_lokasi"));
                lblLokasi.setText(rs.getString("nama_lokasi"));
                
                txtBasePrice.setText(df1.format(rs.getFloat("base_price")));
                
                chkDiscontinued.setSelected(rs.getBoolean("active"));
                txtKodeObatSama.setText(rs.getString("kode_barang_sama"));
                lblObatSama.setText(rs.getString("nama_barang_sama"));
                chkDispensing.setSelected(rs.getBoolean("is_dispensing"));
            }
            
            rs.close();
            st.close();
            
            if(getBEdit()) udfLoadGridSupplier();
            
        }catch(SQLException se){System.out.println(se.getMessage()); } 
    }
    
    private void udfSetupBtn(boolean st){
        btnAdd.setEnabled(st);
        btnDelete.setEnabled(st);
        btnEdit.setEnabled(st);
    }
    
    public class MyRowRenderer extends DefaultTableCellRenderer implements TableCellRenderer{
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            
            if((column==0)||(column==1)||(column==2)){
                JTextField jt= new JTextField();
                setHorizontalAlignment(jt.LEFT);
            }
            
            
            if(column==4 || column==5 || column==6 || column==7||(column==3)){
                DefaultFormatter fmt = new NumberFormatter(new DecimalFormat("#,##0"));
                DefaultFormatterFactory fmtFactory = new DefaultFormatterFactory(fmt, fmt, fmt);
                JFormattedTextField ft =new JFormattedTextField();
                ft.setFormatterFactory(fmtFactory);
                //ft.setValue(Double.valueOf(value.toString()));
                ft.setValue(value);
                setValue(ft.getText());
                setHorizontalAlignment(ft.RIGHT);
            }else{
                setValue(value);
            }
            
            Color g1 = new Color(230,243,255);//[251,251,235]
            Color g2 = new Color(219,238,255);//[247,247,218]
            
            Color w1 = new Color(255,255,255);
            Color w2 = new Color(250,250,250);
            
            Color h1 = new Color(255,240,240);
            Color h2 = new Color(250,230,230);
            
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
            
            if (row%2==0){
                setBackground(w);
            }else{
                setBackground(g);
            }
            
            if(isSelected){
                setBackground(new Color(248,255,167));//[174,212,254]
            }
            
            return this;
        }
    }
    
    private void udfLoadGridSupplier(){
        modelSupp = (DefaultTableModel) tblSupp.getModel();
        int i=0;
        try {
            
            while(modelSupp.getRowCount()>=1){
                modelSupp.removeRow(0);
            }
            String sQry= "select sb.kode_supplier as kode_supplier, nama_supplier, uom_alt, konversi, price, disc, bonus, sb.vat " +
                         "from supplier_barang sb left join phar_supplier s on s.kode_supplier=sb.kode_supplier " +
                         "where kode_barang='"+txtKdBrg.getText().trim()+"'";
            
            Statement st = conn.createStatement();
            ResultSet rs = st.executeQuery(sQry);
                
            System.out.println(sQry);
            while (rs.next()) {
            modelSupp.addRow(new Object[]{rs.getString("kode_supplier"),
                                          rs.getString("nama_supplier"),
                                          rs.getString("uom_alt"),
                                          rs.getFloat("konversi"),
                                          rs.getFloat("price"),
                                          rs.getFloat("disc"),
                                          rs.getFloat("bonus"),
                                          rs.getFloat("vat"),
                                        });
            }
            if(modelSupp.getRowCount()>0) tblSupp.setRowSelectionInterval(0,0);
            rs.close();
            st.close();
        } catch (SQLException eswl){ System.out.println(eswl.getMessage());}
        if(i>0){
            tblSupp.requestFocusInWindow();
            tblSupp.setRowSelectionInterval(0,0);
        }
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
            rowPos = table.getSelectedRow();           
            
            if  (rowPos >=0 && rowPos < table.getRowCount()) {
                btnDefault.setEnabled(true);
                btnDelete.setEnabled(true);
                btnEdit.setEnabled(true);
            }else
            {
                btnDefault.setEnabled(false);
                btnDelete.setEnabled(false);
                btnEdit.setEnabled(false);
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
    
    private void ButtonIcon(String aFile,javax.swing.JButton newBtn) {              
       javax.swing.ImageIcon myIcon = new javax.swing.ImageIcon(getClass().getResource(aFile));
       newBtn.setIcon(myIcon);
    }
    
    public void setUpBtn(){
        if (getBEdit()) {  //proses edit     
            
            String fileImageSave="/image/Icon/Save.png";
            ButtonIcon(fileImageSave,btnDelete);
            
            String fileImageCancel="/image/Icon/Cancel.png";
            //ButtonIcon(fileImageCancel,btnClose);
            
            sClose="cancel";
            
            btnAdd.setEnabled(false);
            btnEdit.setEnabled(false);

            btnDelete.setToolTipText("Save    (F5)");
            //btnClose.setToolTipText("Cancel");
            System.out.println(getBEdit());
            
            tblSupp.setEnabled(false);
            if (getBNew()) txtKdBrg.setEditable(true);
            txtNama.setEditable(true);
            
            tblSupp.requestFocus();

        } else {   //selain edit & NEW
            String fileImageSave="/image/Icon/Delete.png";
            ButtonIcon(fileImageSave,btnDelete);
            
            String fileImageCancel="/image/Icon/Exit.png";
  //          ButtonIcon(fileImageCancel,btnClose);
            
            sClose="close";
            
            btnAdd.setEnabled(true);
            btnEdit.setEnabled(true);
            
            btnDelete.setToolTipText("Delete     (F12)");
//            btnClose.setToolTipText("Close");
            
            tblSupp.setEnabled(true);
            txtKdBrg.setEditable(false);
            txtNama.setEditable(false);
            
        }        
    }
    
    private void pesanError(String Err){
        JOptionPane.showMessageDialog(this,Err,"Message",JOptionPane.ERROR_MESSAGE);
    }
    
    private void pesanInfo(String Err){
        JOptionPane.showMessageDialog(this,Err,"Message",JOptionPane.INFORMATION_MESSAGE);
    }
    
    private boolean cekBeforeSave(){
        boolean st=true;
        if(txtNama.getText().trim().equalsIgnoreCase("")){
            pesanInfo("Silakan isi Nama Barang terlebih dulu!");
            txtNama.requestFocus();
            st=false;
            return st;
        }
        
        if(txtSatuanKecil.getText().trim().equalsIgnoreCase("")){
            pesanInfo("Silakan isi satuan kecil terlebih dulu!");
            txtSatuanKecil.requestFocus();
            st=false;
            return st;
        }
        
        if(txtJenis.getText().trim().equalsIgnoreCase("")){
            pesanInfo("Silakan isi Jenis item terlebih dulu!");
            txtJenis.requestFocus();
            st=false;
            return st;
        }
        
        if(txtLokasi.getText().trim().equalsIgnoreCase("")){
            pesanInfo("Silakan isi lokasi terlebih dulu!");
            txtLokasi.requestFocus();
            st=false;
            return st;
        }
        return st;
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
    
    public void udfAddItemSupp(Object[] obj){
        modelSupp.addRow(obj);
    }
    
    private void udfEdititemSupplier(){
        if(tblSupp.getRowCount()>0){
            if(tblSupp.getValueAt(tblSupp.getSelectedRow(),0)!=null){
                DlgSuppBarang d1=new DlgSuppBarang(this,true);
                d1.setConn(conn);
                d1.udfSetKodeBarang(txtKdBrg.getText());
                d1.udfSetKodeSupp(tblSupp.getValueAt(tblSupp.getSelectedRow(),0).toString());
                d1.udfSetRowPos(tblSupp.getSelectedRow());
                d1.udfSetOldSupplier(tblSupp.getValueAt(tblSupp.getSelectedRow(),0).toString());
        //        d1.udfSetConvertion(Float.parseFloat(tblSupp.getValueAt(tblSupp.getSelectedRow(),3).toString()));
        //        d1.udfSetPrice(Float.parseFloat(tblSupp.getValueAt(tblSupp.getSelectedRow(),4).toString()));
        //        d1.udfSetDisc(Float.parseFloat(tblSupp.getValueAt(tblSupp.getSelectedRow(),5).toString()));
        //        d1.udfSetBonus(Float.parseFloat(tblSupp.getValueAt(tblSupp.getSelectedRow(),6).toString()));
        //        d1.udfSetVat(Float.parseFloat(tblSupp.getValueAt(tblSupp.getSelectedRow(),7).toString()));
                d1.udfSetTable(tblSupp);
                d1.udfSetBNew(false);
                d1.setVisible(true);
                tblSupp.setFocusable(true);
           }
        }
    }
    
    public void udfSetEnabledKode(boolean bSt){
        txtKdBrg.setEnabled(false);
    }
    
//    private float udfGetFloat(String sNum){
//        float hsl=0;
//        if(!sNum.trim().equalsIgnoreCase("")){
//            try{
//                hsl=Float.valueOf(sNum.replace(",", ""));
//            }catch(NumberFormatException ne){
//                hsl=0;
//            }catch(IllegalArgumentException i){
//                hsl=0;
//            }
//        }
//        return hsl;
//    }
    
    private void udfSave(){
        if (cekBeforeSave())
        {   
            //String sKode=iB.udfGetNewKode());
            if (getBNew()) {
                txtKdBrg.setText(iB.udfGetNewKode());
                iB.setKode("");
            }else
            {
                iB.setKodeAsal(txtKdBrg.getText());
            }
            iB.setNama(txtNama.getText());
            iB.setStatusPakai(chkDiscontinued.isSelected());
            iB.setDefaultBrows(jRBtnSatuanBesar.isSelected()? 2 :1);
            iB.setMin(udfGetFloat(txtMin.getText()));
            iB.setMax(udfGetFloat(txtMax.getText()));
            iB.setSatuanBesar(txtSatuanBesar.getText());
            iB.setKonversi(udfGetFloat(txtKonversi.getText()));
            iB.setHarga_besar_resep(udfGetFloat(txtHargaBesarResep.getText()));
            iB.setHarga_besar_non_resep(udfGetFloat(txtHargaBesarNonResep.getText()));
            iB.setSatuan_kecil(txtSatuanKecil.getText());
            iB.setHarga_kecil_resep(udfGetFloat(txtHargaKecilResep.getText()));
            iB.setHarga_kecil_non_resep(udfGetFloat(txtHargaKecilNonResep.getText()));
            iB.setLocation(txtLokasi.getText());
            iB.setBarcode(txtBarcode.getText().toString());
            iB.setKode_sama(txtKodeObatSama.getText().trim());
            iB.setKode_jenis(txtJenis.getText().trim());
            iB.setKeterangan(txtKet.getText());
            iB.setBasePrice(udfGetFloat(txtBasePrice.getText()));
            iB.setIsDispensing(chkDispensing.isSelected());
            iB.setConn(conn);
            
            try{
                conn.setAutoCommit(false);
            
                if (getBNew()) {        //Add
                    boolean hsl=iB.AddItem();
                    txtKdBrg.setText(iB.getKode());
                    if (!hsl){
                        pesanError("Simpan data gagal!");
                        System.out.println(hsl);   
                    }
                    else
                    {
                        //iB.EditItem();
                        setBEdit(true); 
                        setBNew(true);
                        //Kode_Item, Nama_Item, Jenis_Barang, Keterangan, Min. Stock, Max. Stock, Unit, Uom, Discontinued, Barcode
                        //txtKdBrg.setText(iB.udfGetNewKode());
                        
                        tblModel.addRow(new Object[]{txtKdBrg.getText().trim(),
                                            txtNama.getText().trim(),
                                            lblJenis.getText().trim(),
                                            txtKet.getText().trim(),
                                            udfGetFloat(txtMin.getText().trim().trim().replace(",","")),
                                            udfGetFloat(txtMax.getText().trim().trim().replace(",","")),
                                            txtSatuanBesar.getText(),
                                            Float.parseFloat(txtHargaBesarResep.getText().trim().trim().replace(",","")),
                                            Float.parseFloat(txtHargaBesarNonResep.getText().trim().trim().replace(",","")),
                                            txtSatuanKecil.getText(),
                                            udfGetFloat(txtHargaKecilResep.getText().trim().trim().replace(",","")),
                                            udfGetFloat(txtHargaKecilNonResep.getText().trim().trim().replace(",","")),
                                            lblLokasi.getText().trim(),
                                            chkDiscontinued.isSelected()
                                            });
                          
                        udfSetupBtn(true);
                        dispose();
                        tblItem.setRowSelectionInterval(tblModel.getRowCount()-1, tblModel.getRowCount()-1);
                        
                        
                    }
                        
                } else {
                    if (getBEdit()){
                        System.out.println("dari text:"+ txtKdBrg.getText().trim().substring(0,1) +" nama Depan : " +(namaDepan));
                        
                        boolean bSt=txtKdBrg.getText().trim().substring(0,1).equalsIgnoreCase(namaDepan);
                        
                        if(bSt){
                            //iB.setKodeAsal(txtKdBrg.getText());
                            
                            int i=iB.EditItem();
                            System.out.println("OK EDIT");

                            if (i==0){
                                pesanError("Gagal Update!");
                                System.out.println(i);   
                            }else{
                                System.out.println(i);
                                setBEdit(true);
                                setBNew(false);

                                //Kode_Item, Nama_Item, Jenis_Barang, Keterangan, Min. Stock, Max. Stock, Unit, Uom, Discontinued, Barcode
                                tblModel.setValueAt(txtKdBrg.getText().trim(), rowPos,0);
                                tblModel.setValueAt(txtNama.getText().trim(),rowPos,1);
                                tblModel.setValueAt(lblJenis.getText().trim(),rowPos,2);
                                tblModel.setValueAt(txtKet.getText().trim(),rowPos,3);
                                tblModel.setValueAt(udfGetFloat(txtMin.getText().replace(",","")),rowPos,4);
                                tblModel.setValueAt(udfGetFloat(txtMax.getText().replace(",","")),rowPos,5);
                                tblModel.setValueAt(txtSatuanBesar.getText(),rowPos,6);
                                tblModel.setValueAt(udfGetFloat(txtHargaBesarResep.getText().replace(",","")),rowPos,7);
                                tblModel.setValueAt(udfGetFloat(txtHargaBesarNonResep.getText().replace(",","")),rowPos,8);
                                tblModel.setValueAt(txtSatuanKecil.getText(),rowPos,9);
                                tblModel.setValueAt(udfGetFloat(txtHargaKecilResep.getText().replace(",","")),rowPos,10);
                                tblModel.setValueAt(udfGetFloat(txtHargaKecilNonResep.getText().replace(",","")),rowPos,11);
                                tblModel.setValueAt(lblLokasi.getText(),rowPos,12);
                                tblModel.setValueAt(chkDiscontinued.isSelected(),rowPos,13);
                                
                                tblItem.requestFocusInWindow();

                                dispose();
                            }
                        }
                        else
                           pesanInfo("Nama depan Barang harus sama dengan sebelumnya yaitu: '"+namaDepan+"' ");
                    }
                }
                conn.commit();
                conn.setAutoCommit(true);
                
                tblItem.changeSelection(tblItem.getSelectedRow(), tblItem.getSelectedRow(), false, false);
                
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
    
    private float udfGetFloat(String sNum){
    float hsl=0;
    if(!sNum.trim().equalsIgnoreCase("")){
        try{
            hsl=Float.valueOf(sNum.replace(",", ""));
        }catch(NumberFormatException ne){
            hsl=0;
        }catch(IllegalArgumentException i){
            hsl=0;
        }
    }
    return hsl;
  }
    
    private void udfValidateNumber(JFormattedTextField jF, String fmt){
        try{
            if(fmt.equalsIgnoreCase("dec"))
                jF.setText(decFmt.format(udfGetFloat(jF.getText())));
            else
                jF.setText(df1.format(udfGetFloat(jF.getText())));
            
        }catch(NumberFormatException ne){
            System.err.println(ne.getMessage());
            jF.setText("0");
        }
    }
    
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        popMSetDefault = new javax.swing.JPopupMenu();
        JMISetDefault = new javax.swing.JMenuItem();
        buttonGroup1 = new javax.swing.ButtonGroup();
        jPanel2 = new javax.swing.JPanel();
        jLabel24 = new javax.swing.JLabel();
        jLabel25 = new javax.swing.JLabel();
        jLabel27 = new javax.swing.JLabel();
        jLabel28 = new javax.swing.JLabel();
        txtNama = new javax.swing.JTextField();
        txtKdBrg = new javax.swing.JTextField();
        txtSatuanKecil = new javax.swing.JTextField();
        txtKet = new javax.swing.JTextField();
        jLabel30 = new javax.swing.JLabel();
        jLabel31 = new javax.swing.JLabel();
        chkDiscontinued = new javax.swing.JCheckBox();
        txtBarcode = new javax.swing.JTextField();
        jLabel34 = new javax.swing.JLabel();
        jLabel37 = new javax.swing.JLabel();
        txtJenis = new javax.swing.JTextField();
        lblJenis = new javax.swing.JLabel();
        jLabel43 = new javax.swing.JLabel();
        txtLokasi = new javax.swing.JTextField();
        lblLokasi = new javax.swing.JLabel();
        txtMin = new javax.swing.JFormattedTextField();
        txtMax = new javax.swing.JFormattedTextField();
        jLabel44 = new javax.swing.JLabel();
        txtKodeObatSama = new javax.swing.JTextField();
        lblObatSama = new javax.swing.JLabel();
        jLabel46 = new javax.swing.JLabel();
        txtKonversi = new javax.swing.JFormattedTextField();
        txtBasePrice = new javax.swing.JFormattedTextField();
        jLabel33 = new javax.swing.JLabel();
        jButton2 = new javax.swing.JButton();
        jButton1 = new javax.swing.JButton();
        jLabel29 = new javax.swing.JLabel();
        txtSatuanBesar = new javax.swing.JTextField();
        jLabel42 = new javax.swing.JLabel();
        jLabel47 = new javax.swing.JLabel();
        jLabel48 = new javax.swing.JLabel();
        jLabel49 = new javax.swing.JLabel();
        jLabel50 = new javax.swing.JLabel();
        txtHargaBesarResep = new javax.swing.JFormattedTextField();
        txtHargaKecilResep = new javax.swing.JFormattedTextField();
        txtHargaBesarNonResep = new javax.swing.JFormattedTextField();
        txtHargaKecilNonResep = new javax.swing.JFormattedTextField();
        jRBtnSatuanBesar = new javax.swing.JRadioButton();
        jRBtnSatuanKecil = new javax.swing.JRadioButton();
        chkDispensing = new javax.swing.JCheckBox();
        jPanel1 = new javax.swing.JPanel();
        btnAdd = new javax.swing.JButton();
        btnEdit = new javax.swing.JButton();
        btnDelete = new javax.swing.JButton();
        jLabel3 = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        tblSupp = new javax.swing.JTable();
        btnDefault = new javax.swing.JButton();
        jPanel3 = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();

        JMISetDefault.setText("Set Base Default");
        popMSetDefault.add(JMISetDefault);

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Item Master - Supplier");
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosed(java.awt.event.WindowEvent evt) {
                formWindowClosed(evt);
            }
            public void windowOpened(java.awt.event.WindowEvent evt) {
                formWindowOpened(evt);
            }
        });
        getContentPane().setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jPanel2.setBackground(new java.awt.Color(255, 204, 102));
        jPanel2.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel24.setText("Kode Barang");
        jPanel2.add(jLabel24, new org.netbeans.lib.awtextra.AbsoluteConstraints(24, 10, -1, 24));

        jLabel25.setText("Nama barang");
        jPanel2.add(jLabel25, new org.netbeans.lib.awtextra.AbsoluteConstraints(24, 38, -1, 24));

        jLabel27.setText("Satuan Kecil");
        jPanel2.add(jLabel27, new org.netbeans.lib.awtextra.AbsoluteConstraints(19, 120, 80, 24));

        jLabel28.setText("Keterangan");
        jPanel2.add(jLabel28, new org.netbeans.lib.awtextra.AbsoluteConstraints(24, 202, -1, 24));

        txtNama.setFont(new java.awt.Font("Dialog", 1, 12));
        txtNama.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        jPanel2.add(txtNama, new org.netbeans.lib.awtextra.AbsoluteConstraints(110, 38, 370, 25));

        txtKdBrg.setEditable(false);
        txtKdBrg.setFont(new java.awt.Font("Dialog", 1, 12));
        txtKdBrg.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        txtKdBrg.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtKdBrgKeyReleased(evt);
            }
        });
        jPanel2.add(txtKdBrg, new org.netbeans.lib.awtextra.AbsoluteConstraints(110, 11, 150, 25));

        txtSatuanKecil.setFont(new java.awt.Font("Dialog", 1, 12));
        txtSatuanKecil.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        jPanel2.add(txtSatuanKecil, new org.netbeans.lib.awtextra.AbsoluteConstraints(110, 121, 170, 24));

        txtKet.setFont(new java.awt.Font("Dialog", 1, 12));
        txtKet.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        jPanel2.add(txtKet, new org.netbeans.lib.awtextra.AbsoluteConstraints(111, 204, 370, 25));

        jLabel30.setText("Min. Stock");
        jPanel2.add(jLabel30, new org.netbeans.lib.awtextra.AbsoluteConstraints(22, 149, -1, 24));

        jLabel31.setText("Max. Stock");
        jPanel2.add(jLabel31, new org.netbeans.lib.awtextra.AbsoluteConstraints(283, 154, -1, 24));

        chkDiscontinued.setSelected(true);
        chkDiscontinued.setText("Aktif");
        chkDiscontinued.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        chkDiscontinued.setMargin(new java.awt.Insets(0, 0, 0, 0));
        chkDiscontinued.setOpaque(false);
        jPanel2.add(chkDiscontinued, new org.netbeans.lib.awtextra.AbsoluteConstraints(111, 239, 90, -1));

        txtBarcode.setFont(new java.awt.Font("Dialog", 1, 12));
        txtBarcode.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        jPanel2.add(txtBarcode, new org.netbeans.lib.awtextra.AbsoluteConstraints(330, 10, 150, 25));

        jLabel34.setText("Barcode");
        jPanel2.add(jLabel34, new org.netbeans.lib.awtextra.AbsoluteConstraints(280, 10, -1, 24));

        jLabel37.setText("Jenis Barang");
        jPanel2.add(jLabel37, new org.netbeans.lib.awtextra.AbsoluteConstraints(546, 11, 110, 24));

        txtJenis.setFont(new java.awt.Font("Dialog", 1, 12));
        txtJenis.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        txtJenis.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtJenisKeyReleased(evt);
            }
        });
        jPanel2.add(txtJenis, new org.netbeans.lib.awtextra.AbsoluteConstraints(651, 11, 70, 24));

        lblJenis.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        lblJenis.setOpaque(true);
        jPanel2.add(lblJenis, new org.netbeans.lib.awtextra.AbsoluteConstraints(721, 11, 240, 24));

        jLabel43.setText("Lokasi");
        jPanel2.add(jLabel43, new org.netbeans.lib.awtextra.AbsoluteConstraints(518, 147, 130, 24));

        txtLokasi.setFont(new java.awt.Font("Dialog", 1, 12));
        txtLokasi.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        txtLokasi.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtLokasiKeyReleased(evt);
            }
        });
        jPanel2.add(txtLokasi, new org.netbeans.lib.awtextra.AbsoluteConstraints(652, 146, 70, 24));

        lblLokasi.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        lblLokasi.setOpaque(true);
        jPanel2.add(lblLokasi, new org.netbeans.lib.awtextra.AbsoluteConstraints(722, 146, 240, 24));

        txtMin.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        txtMin.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtMin.setText("0");
        txtMin.setFont(new java.awt.Font("Dialog", 1, 12));
        txtMin.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                txtMinFocusLost(evt);
            }
        });
        jPanel2.add(txtMin, new org.netbeans.lib.awtextra.AbsoluteConstraints(110, 148, 120, 24));

        txtMax.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        txtMax.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtMax.setText("0");
        txtMax.setFont(new java.awt.Font("Dialog", 1, 12));
        txtMax.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                txtMaxFocusLost(evt);
            }
        });
        jPanel2.add(txtMax, new org.netbeans.lib.awtextra.AbsoluteConstraints(351, 152, 130, 24));

        jLabel44.setText("Kode Barang Sama");
        jPanel2.add(jLabel44, new org.netbeans.lib.awtextra.AbsoluteConstraints(520, 173, 130, 24));

        txtKodeObatSama.setFont(new java.awt.Font("Dialog", 1, 12));
        txtKodeObatSama.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        txtKodeObatSama.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtKodeObatSamaKeyReleased(evt);
            }
        });
        jPanel2.add(txtKodeObatSama, new org.netbeans.lib.awtextra.AbsoluteConstraints(652, 174, 70, 24));

        lblObatSama.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        lblObatSama.setOpaque(true);
        jPanel2.add(lblObatSama, new org.netbeans.lib.awtextra.AbsoluteConstraints(722, 174, 240, 24));

        jLabel46.setText("Konversi");
        jPanel2.add(jLabel46, new org.netbeans.lib.awtextra.AbsoluteConstraints(22, 94, 90, 24));

        txtKonversi.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        txtKonversi.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtKonversi.setText("1");
        txtKonversi.setFont(new java.awt.Font("Dialog", 1, 12));
        txtKonversi.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                txtKonversiFocusLost(evt);
            }
        });
        jPanel2.add(txtKonversi, new org.netbeans.lib.awtextra.AbsoluteConstraints(110, 94, 94, 24));

        txtBasePrice.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        txtBasePrice.setEditable(false);
        txtBasePrice.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtBasePrice.setText("0");
        txtBasePrice.setFont(new java.awt.Font("Dialog", 1, 12));
        jPanel2.add(txtBasePrice, new org.netbeans.lib.awtextra.AbsoluteConstraints(110, 176, 130, 24));

        jLabel33.setText("Base Price");
        jPanel2.add(jLabel33, new org.netbeans.lib.awtextra.AbsoluteConstraints(21, 176, 70, 24));

        jButton2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/image/Icon/Exit.png"))); // NOI18N
        jButton2.setText("Close (Esc)");
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });
        jPanel2.add(jButton2, new org.netbeans.lib.awtextra.AbsoluteConstraints(802, 216, 160, 40));

        jButton1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/image/Icon/Save.png"))); // NOI18N
        jButton1.setText("Save Item (F2)");
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
        jPanel2.add(jButton1, new org.netbeans.lib.awtextra.AbsoluteConstraints(641, 216, -1, 40));

        jLabel29.setText("Satuan Besar");
        jPanel2.add(jLabel29, new org.netbeans.lib.awtextra.AbsoluteConstraints(24, 67, 80, 24));

        txtSatuanBesar.setFont(new java.awt.Font("Dialog", 1, 12));
        txtSatuanBesar.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        txtSatuanBesar.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                txtSatuanBesarFocusLost(evt);
            }
        });
        txtSatuanBesar.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtSatuanBesarKeyReleased(evt);
            }
        });
        jPanel2.add(txtSatuanBesar, new org.netbeans.lib.awtextra.AbsoluteConstraints(110, 67, 170, 24));

        jLabel42.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel42.setText("HARGA RESEP");
        jLabel42.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        jLabel42.setOpaque(true);
        jPanel2.add(jLabel42, new org.netbeans.lib.awtextra.AbsoluteConstraints(651, 47, 150, 24));

        jLabel47.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel47.setText("HARGA NON RESEP");
        jLabel47.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        jLabel47.setOpaque(true);
        jPanel2.add(jLabel47, new org.netbeans.lib.awtextra.AbsoluteConstraints(801, 47, 160, 24));

        jLabel48.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel48.setText("SATUAN BESAR");
        jLabel48.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        jLabel48.setOpaque(true);
        jPanel2.add(jLabel48, new org.netbeans.lib.awtextra.AbsoluteConstraints(501, 70, 150, 24));

        jLabel49.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel49.setText("SATUAN KECIL");
        jLabel49.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        jLabel49.setOpaque(true);
        jPanel2.add(jLabel49, new org.netbeans.lib.awtextra.AbsoluteConstraints(501, 93, 150, 24));

        jLabel50.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel50.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        jLabel50.setOpaque(true);
        jPanel2.add(jLabel50, new org.netbeans.lib.awtextra.AbsoluteConstraints(501, 47, 150, 24));

        txtHargaBesarResep.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        txtHargaBesarResep.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtHargaBesarResep.setText("0");
        txtHargaBesarResep.setFont(new java.awt.Font("Dialog", 1, 12));
        txtHargaBesarResep.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                txtHargaBesarResepFocusLost(evt);
            }
        });
        jPanel2.add(txtHargaBesarResep, new org.netbeans.lib.awtextra.AbsoluteConstraints(651, 70, 150, 24));

        txtHargaKecilResep.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        txtHargaKecilResep.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtHargaKecilResep.setText("0");
        txtHargaKecilResep.setFont(new java.awt.Font("Dialog", 1, 12));
        txtHargaKecilResep.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                txtHargaKecilResepFocusLost(evt);
            }
        });
        jPanel2.add(txtHargaKecilResep, new org.netbeans.lib.awtextra.AbsoluteConstraints(651, 93, 150, 24));

        txtHargaBesarNonResep.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        txtHargaBesarNonResep.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtHargaBesarNonResep.setText("0");
        txtHargaBesarNonResep.setFont(new java.awt.Font("Dialog", 1, 12));
        txtHargaBesarNonResep.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                txtHargaBesarNonResepFocusLost(evt);
            }
        });
        jPanel2.add(txtHargaBesarNonResep, new org.netbeans.lib.awtextra.AbsoluteConstraints(801, 70, 160, 24));

        txtHargaKecilNonResep.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        txtHargaKecilNonResep.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtHargaKecilNonResep.setText("0");
        txtHargaKecilNonResep.setFont(new java.awt.Font("Dialog", 1, 12));
        txtHargaKecilNonResep.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                txtHargaKecilNonResepFocusLost(evt);
            }
        });
        jPanel2.add(txtHargaKecilNonResep, new org.netbeans.lib.awtextra.AbsoluteConstraints(801, 93, 160, 24));

        buttonGroup1.add(jRBtnSatuanBesar);
        jRBtnSatuanBesar.setSelected(true);
        jRBtnSatuanBesar.setText("Set Satuan Default");
        jRBtnSatuanBesar.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        jRBtnSatuanBesar.setMargin(new java.awt.Insets(0, 0, 0, 0));
        jRBtnSatuanBesar.setOpaque(false);
        jPanel2.add(jRBtnSatuanBesar, new org.netbeans.lib.awtextra.AbsoluteConstraints(289, 73, 160, -1));

        buttonGroup1.add(jRBtnSatuanKecil);
        jRBtnSatuanKecil.setSelected(true);
        jRBtnSatuanKecil.setText("Set Satuan Default");
        jRBtnSatuanKecil.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        jRBtnSatuanKecil.setMargin(new java.awt.Insets(0, 0, 0, 0));
        jRBtnSatuanKecil.setOpaque(false);
        jPanel2.add(jRBtnSatuanKecil, new org.netbeans.lib.awtextra.AbsoluteConstraints(290, 126, 170, -1));

        chkDispensing.setText("Dispensing");
        chkDispensing.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chkDispensingActionPerformed(evt);
            }
        });
        jPanel2.add(chkDispensing, new org.netbeans.lib.awtextra.AbsoluteConstraints(223, 236, 160, -1));

        getContentPane().add(jPanel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(4, 56, 982, 270));

        jPanel1.setBackground(new java.awt.Color(159, 120, 2));
        jPanel1.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        jPanel1.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        btnAdd.setMnemonic('A');
        btnAdd.setText("Add Supplier (F5)");
        btnAdd.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAddActionPerformed(evt);
            }
        });
        btnAdd.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                btnAddKeyPressed(evt);
            }
        });
        jPanel1.add(btnAdd, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 10, -1, 30));

        btnEdit.setMnemonic('E');
        btnEdit.setText("Edit Supplier (F4)");
        btnEdit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnEditActionPerformed(evt);
            }
        });
        btnEdit.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                btnEditKeyPressed(evt);
            }
        });
        jPanel1.add(btnEdit, new org.netbeans.lib.awtextra.AbsoluteConstraints(152, 10, -1, 30));

        btnDelete.setMnemonic('D');
        btnDelete.setText("Delete Supplier (F12)");
        btnDelete.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnDeleteActionPerformed(evt);
            }
        });
        jPanel1.add(btnDelete, new org.netbeans.lib.awtextra.AbsoluteConstraints(296, 10, -1, 30));

        jLabel3.setBackground(new java.awt.Color(255, 153, 0));
        jLabel3.setFont(new java.awt.Font("Dialog", 1, 14));
        jLabel3.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel3.setText("DAFTAR   SUPPLIER");
        jLabel3.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        jLabel3.setOpaque(true);
        jPanel1.add(jLabel3, new org.netbeans.lib.awtextra.AbsoluteConstraints(4, 44, 970, 20));

        tblSupp.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, null, null, null}
            },
            new String [] {
                "Kode Supplier", "Nama Supplier", "UoM", "Convertion", "Price", "Discount", "Bonus", "Vat"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.Float.class, java.lang.Float.class, java.lang.Float.class, java.lang.Float.class, java.lang.Float.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        tblSupp.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_OFF);
        tblSupp.getTableHeader().setReorderingAllowed(false);
        tblSupp.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tblSuppMouseClicked(evt);
            }
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                tblSuppMouseEntered(evt);
            }
            public void mousePressed(java.awt.event.MouseEvent evt) {
                tblSuppMousePressed(evt);
            }
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                tblSuppMouseReleased(evt);
            }
        });
        tblSupp.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                tblSuppKeyPressed(evt);
            }
        });
        jScrollPane2.setViewportView(tblSupp);

        jPanel1.add(jScrollPane2, new org.netbeans.lib.awtextra.AbsoluteConstraints(4, 64, 970, 230));

        btnDefault.setMnemonic('D');
        btnDefault.setText("Set Default Price (F10)");
        btnDefault.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnDefaultActionPerformed(evt);
            }
        });
        btnDefault.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                btnDefaultKeyPressed(evt);
            }
        });
        jPanel1.add(btnDefault, new org.netbeans.lib.awtextra.AbsoluteConstraints(648, 10, -1, 30));

        getContentPane().add(jPanel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(4, 328, 982, 302));

        jPanel3.setBackground(new java.awt.Color(159, 120, 2));
        jPanel3.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel2.setFont(new java.awt.Font("Dialog", 1, 24));
        jLabel2.setForeground(new java.awt.Color(255, 255, 0));
        jLabel2.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel2.setText("MASTER BARANG");
        jPanel3.add(jLabel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(6, 9, 980, 28));

        getContentPane().add(jPanel3, new org.netbeans.lib.awtextra.AbsoluteConstraints(6, 3, 990, 51));

        java.awt.Dimension screenSize = java.awt.Toolkit.getDefaultToolkit().getScreenSize();
        setBounds((screenSize.width-1000)/2, (screenSize.height-665)/2, 1000, 665);
    }// </editor-fold>//GEN-END:initComponents

    private void txtMaxFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtMaxFocusLost
        udfValidateNumber(txtMax, "#,##0");
    }//GEN-LAST:event_txtMaxFocusLost

    private void txtHargaKecilNonResepFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtHargaKecilNonResepFocusLost
        udfValidateNumber(txtHargaKecilNonResep,"#,##0");
    }//GEN-LAST:event_txtHargaKecilNonResepFocusLost

    private void txtHargaKecilResepFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtHargaKecilResepFocusLost
        udfValidateNumber(txtHargaKecilResep,"#,##0");
        
    }//GEN-LAST:event_txtHargaKecilResepFocusLost

    private void txtHargaBesarNonResepFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtHargaBesarNonResepFocusLost
        udfValidateNumber(txtHargaBesarNonResep, "#,##0");
        
    }//GEN-LAST:event_txtHargaBesarNonResepFocusLost

    private void txtHargaBesarResepFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtHargaBesarResepFocusLost
        udfValidateNumber(txtHargaBesarResep, "#,##0");
        
    }//GEN-LAST:event_txtHargaBesarResepFocusLost

    private void txtMinFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtMinFocusLost
        udfValidateNumber(txtMin, "");
    }//GEN-LAST:event_txtMinFocusLost

    private void txtKonversiFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtKonversiFocusLost
        udfValidateNumber(txtKonversi, "");
    }//GEN-LAST:event_txtKonversiFocusLost

    private void txtSatuanBesarFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtSatuanBesarFocusLost
        
    }//GEN-LAST:event_txtSatuanBesarFocusLost

    private void txtSatuanBesarKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtSatuanBesarKeyReleased
// TODO add your handling code here:
    }//GEN-LAST:event_txtSatuanBesarKeyReleased

    private void btnEditKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_btnEditKeyPressed
        if(evt.getKeyCode()==KeyEvent.VK_ENTER)
             udfEdititemSupplier();
    }//GEN-LAST:event_btnEditKeyPressed

    private void btnDefaultKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_btnDefaultKeyPressed
        udfSetDefaultBasePrice();
    }//GEN-LAST:event_btnDefaultKeyPressed

    private void tblSuppKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_tblSuppKeyPressed
        if(evt.getKeyCode()==KeyEvent.VK_ENTER){
            if(tblSupp.getValueAt(tblSupp.getSelectedRow(),0)!=null){
                udfEdititemSupplier();
            }
        }
    }//GEN-LAST:event_tblSuppKeyPressed

    private void btnDefaultActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnDefaultActionPerformed
        udfSetDefaultBasePrice();
        
    }//GEN-LAST:event_btnDefaultActionPerformed

    private void btnAddKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_btnAddKeyPressed
        udfAdd();
    }//GEN-LAST:event_btnAddKeyPressed

    private void tblSuppMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tblSuppMouseReleased
        if(evt.getButton()==KeyEvent.VK_RIGHT){
        
        }
    }//GEN-LAST:event_tblSuppMouseReleased

    private void tblSuppMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tblSuppMouseEntered
        if(evt.getButton()==KeyEvent.VK_RIGHT){
        
        }
    }//GEN-LAST:event_tblSuppMouseEntered

    private void tblSuppMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tblSuppMousePressed
        if(evt.getButton()==KeyEvent.VK_RIGHT){
        
        }
    }//GEN-LAST:event_tblSuppMousePressed

    private void txtKodeObatSamaKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtKodeObatSamaKeyReleased
        try {
            String sCari = txtKodeObatSama.getText();
            switch(evt.getKeyCode()) {
                
                case java.awt.event.KeyEvent.VK_ENTER : {
                    if (lst.isVisible()){
                        Object[] obj = lst.getOResult();
                        if (obj.length > 0) {
                            txtLokasi.setText(obj[0].toString());
                            lblLokasi.setText(obj[1].toString());
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
                
                case java.awt.event.KeyEvent.VK_ESCAPE: {
                    lst.setFocusable(false);
                    txtKodeObatSama.setText("");
                    lblObatSama.setText("");
                    lst.setVisible(false);
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
                        String sQry="select item_code as Kode, coalesce(item_name,'') as Nama from barang " +
                                "where upper(item_code||coalesce(item_name,'')) iLike '%" + sCari.toUpperCase() +"%' order by 1";
                        System.out.println(sQry);
                        lst.setSQuery(sQry);
                        int lWidth=txtKodeObatSama.getWidth()+lblObatSama.getWidth();
                        lst.setBounds(this.getX()+this.jPanel2.getX() + this.txtKodeObatSama.getX()+5, this.getY()+this.jPanel2.getY()+this.txtKodeObatSama.getY() + txtKodeObatSama.getHeight()+30, lWidth,150);
                        lst.setFocusableWindowState(false);
                        lst.setTxtCari(txtKodeObatSama);
                        lst.setLblDes(new javax.swing.JLabel[]{lblObatSama});
                        lst.setColWidth(0, txtKodeObatSama.getWidth()-1);
                        lst.setColWidth(1, lblObatSama.getWidth()-15);
                        if(lst.getIRowCount()>0){
                            lst.setVisible(true);
                        } else{
                            txtKodeObatSama.setText("");lblObatSama.setText("");
                            lst.setVisible(false);
                        }
                    }
                    break;
                }
            }
        } catch (SQLException se) {System.out.println(se.getMessage());
        
        }        
    }//GEN-LAST:event_txtKodeObatSamaKeyReleased

    private void btnDeleteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnDeleteActionPerformed
        udfDeleteSupplier();
    }//GEN-LAST:event_btnDeleteActionPerformed

    private void btnEditActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnEditActionPerformed
        udfEdititemSupplier();
        
    }//GEN-LAST:event_btnEditActionPerformed

    private void btnAddActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAddActionPerformed
        udfAdd();
        
    }//GEN-LAST:event_btnAddActionPerformed

    private void jButton1KeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jButton1KeyPressed
        if(evt.getKeyCode()==evt.VK_ENTER){
            udfSave();
        }
    }//GEN-LAST:event_jButton1KeyPressed

    private void formWindowClosed(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosed
        FrmItem.isFormItemOn=false;
    }//GEN-LAST:event_formWindowClosed

    private void tblSuppMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tblSuppMouseClicked
         if (evt.getClickCount()==2 ){
             udfEdititemSupplier();
         }
    }//GEN-LAST:event_tblSuppMouseClicked

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        this.dispose();
    }//GEN-LAST:event_jButton2ActionPerformed

    private void txtKdBrgKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtKdBrgKeyReleased
        if(txtKdBrg.getText().length()>12) txtKdBrg.setText("");
    }//GEN-LAST:event_txtKdBrgKeyReleased

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        udfSave();
    }//GEN-LAST:event_jButton1ActionPerformed

    private void txtLokasiKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtLokasiKeyReleased
        try {
            String sCari = txtLokasi.getText();
            switch(evt.getKeyCode()) {
                
                case java.awt.event.KeyEvent.VK_ENTER : {
                    if (lst.isVisible()){
                        Object[] obj = lst.getOResult();
                        if (obj.length > 0) {
                            txtLokasi.setText(obj[0].toString());
                            lblLokasi.setText(obj[1].toString());
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
                case java.awt.event.KeyEvent.VK_ESCAPE: {
                    lst.setVisible(false);
                    txtLokasi.setText("");
                    lblLokasi.setText("");
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
                        String sQry="select kode_lokasi as Kode, coalesce(nama_lokasi,'') as lokasi from lokasi " +
                                "where upper(kode_lokasi||coalesce(nama_lokasi,'')) Like '%" + sCari.toUpperCase() +"%' order by 1";
                        System.out.println(sQry);
                        lst.setSQuery(sQry);
                        int lWidth=txtLokasi.getWidth()+lblLokasi.getWidth();
                        lst.setBounds(this.getX()+this.jPanel2.getX() + this.txtLokasi.getX()+5, this.getY()+this.jPanel2.getY()+this.txtLokasi.getY() + txtLokasi.getHeight()+30, lWidth,150);
                        lst.setFocusableWindowState(false);
                        lst.setTxtCari(txtLokasi);
                        lst.setLblDes(new javax.swing.JLabel[]{lblLokasi});
                        lst.setColWidth(0, txtLokasi.getWidth()-1);
                        lst.setColWidth(1, lblLokasi.getWidth()-15);
                        if(lst.getIRowCount()>0){
                            lst.setVisible(true);
                            txtLokasi.requestFocus();
                        } else{
                            txtLokasi.setText("");lblLokasi.setText("");
                            lst.setVisible(false);
                            txtLokasi.requestFocus();
                        }
                    }
                    break;
                }
            }
        } catch (SQLException se) {System.out.println(se.getMessage());
        
        }
}//GEN-LAST:event_txtLokasiKeyReleased

    private void txtJenisKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtJenisKeyReleased
        try {
            String sCari = txtJenis.getText();
            switch(evt.getKeyCode()) {
                
                case java.awt.event.KeyEvent.VK_ENTER : {
                    if (lst.isVisible()){
                        Object[] obj = lst.getOResult();
                        if (obj.length > 0) {
                            txtJenis.setText(obj[0].toString());
                            lblJenis.setText(obj[1].toString());
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
                case java.awt.event.KeyEvent.VK_ESCAPE: {
                    lst.setFocusable(false);
                    txtJenis.setText("");
                    lblJenis.setText("");
                    lst.setVisible(false);
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
                        String sQry="select kode_jenis as kode, coalesce(jenis_barang,'') as jenis " +
                                "from jenis_barang where upper(kode_jenis||jenis_barang) iLike '%" + sCari.toUpperCase() +"%' order by 1";
                        System.out.println(sQry);
                        lst.setSQuery(sQry);
                        int lWidth=txtJenis.getWidth()+lblJenis.getWidth();
                        lst.setBounds(this.getX()+this.jPanel2.getX() + this.txtJenis.getX()+3, this.getY()+this.jPanel2.getY()+this.txtJenis.getY() + txtJenis.getHeight()+30, lWidth,150);
                        lst.setFocusableWindowState(false);
                        lst.setTxtCari(txtJenis);
                        lst.setLblDes(new javax.swing.JLabel[]{lblJenis});
                        lst.setColWidth(0, txtJenis.getWidth()-1);
                        lst.setColWidth(1, lblJenis.getWidth()-15);//lst.getWidth()-txtJenis.getWidth());
                        if(lst.getIRowCount()>0){
                            lst.setVisible(true);
                            this.requestFocusInWindow();
                            txtJenis.requestFocus();
                        } else{
                            txtJenis.setText("");lblJenis.setText("");
                            requestFocusInWindow();
                            lst.setVisible(false);
                            txtJenis.requestFocus();
                        }
                    }
                    break;
                }
            }
        } catch (SQLException se) {System.out.println(se.getMessage());
        
        }
    }//GEN-LAST:event_txtJenisKeyReleased

    private void formWindowOpened(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowOpened
        iB=new ItemBean();
        DefaultFormatter fmt = new NumberFormatter(new DecimalFormat("#,###,#0"));
        DefaultFormatterFactory fmtFactory = new DefaultFormatterFactory(fmt, fmt, fmt);
            
        //DefaultFormatterFactory fmtFactory = new DefaultFormatterFactory(fmt);
        txtMin.setFormatterFactory(fmtFactory);
        txtMax.setFormatterFactory(fmtFactory);
        //txtKonversi.setFormatterFactory(fmtFactory);
        txtBasePrice.setFormatterFactory(fmtFactory);
        
        modelSupp=(DefaultTableModel)tblSupp.getModel();
        
        lst = new ListRsbm();
	lst.setVisible(false);
	lst.setSize(500,150);
	lst.con = conn;
        
        requestFocusInWindow(true);
        tblSupp.requestFocusInWindow();
        
        for(int i=0;i<jPanel1.getComponentCount();i++){
            Component c = jPanel1.getComponent(i);
            if(c.getClass().getSimpleName().equalsIgnoreCase("JTEXTFIELD")    || c.getClass().getSimpleName().equalsIgnoreCase("JFormattedTextField")
            || c.getClass().getSimpleName().equalsIgnoreCase("JTextArea") || c.getClass().getSimpleName().equalsIgnoreCase("JComboBox")
            || c.getClass().getSimpleName().equalsIgnoreCase("JButton") || c.getClass().getSimpleName().equalsIgnoreCase("JCheckBox")
            || c.getClass().getSimpleName().equalsIgnoreCase("JRadioButton")        ) {
                //System.out.println(c.getClass().getSimpleName());
                c.addKeyListener(new MyKeyListener());
            }
        }
        tblSupp.addKeyListener(new MyKeyListener());
        for(int i=0;i<jPanel2.getComponentCount();i++){
            Component c = jPanel2.getComponent(i);
            if(c.getClass().getSimpleName().equalsIgnoreCase("JTEXTFIELD")    || c.getClass().getSimpleName().equalsIgnoreCase("JFormattedTextField")
            || c.getClass().getSimpleName().equalsIgnoreCase("JTextArea") || c.getClass().getSimpleName().equalsIgnoreCase("JComboBox")
            || c.getClass().getSimpleName().equalsIgnoreCase("JButton") || c.getClass().getSimpleName().equalsIgnoreCase("JCheckBox")
            || c.getClass().getSimpleName().equalsIgnoreCase("JRadioButton") ) {
                //System.out.println(c.getClass().getSimpleName());
                c.addKeyListener(new MyKeyListener());
                
            }
        }
        
        lblJenis.setText("");lblLokasi.setText("");
        txtNama.requestFocus();
        
        tblSupp.setModel(modelSupp);
        tblSupp.getColumnModel().getColumn(0).setMaxWidth(110);   //kode supplier
        tblSupp.getColumnModel().getColumn(0).setPreferredWidth(110);
        tblSupp.getColumnModel().getColumn(1).setMaxWidth(280);      //nama supplier
        tblSupp.getColumnModel().getColumn(1).setPreferredWidth(280);
        tblSupp.getColumnModel().getColumn(2).setMaxWidth(80);      //Uom
        tblSupp.getColumnModel().getColumn(2).setPreferredWidth(80);
        tblSupp.getColumnModel().getColumn(3).setMaxWidth(100);     //Convertion
        tblSupp.getColumnModel().getColumn(3).setPreferredWidth(100);
        tblSupp.getColumnModel().getColumn(4).setMaxWidth(100);      //Price
        tblSupp.getColumnModel().getColumn(4).setPreferredWidth(100);
        tblSupp.getColumnModel().getColumn(5).setMaxWidth(100);      //Discount
        tblSupp.getColumnModel().getColumn(5).setPreferredWidth(100);
        tblSupp.getColumnModel().getColumn(6).setMaxWidth(100);      //Bonus
        tblSupp.getColumnModel().getColumn(6).setPreferredWidth(100);
        tblSupp.getColumnModel().getColumn(7).setMaxWidth(100);      //Vat  
        tblSupp.getColumnModel().getColumn(7).setPreferredWidth(100);
            
        tblSupp.setRowHeight(25);
        
        
        if (modelSupp.getRowCount() > 0) {
            tblSupp.changeSelection(0, 0,false,false);                
        }  
        for (int i=0;i<tblSupp.getColumnCount();i++){
            tblSupp.getColumnModel().getColumn(i).setCellRenderer(new MyRowRenderer());
        }

        if (getBEdit()) udfShowItem();
        udfSetupBtn(getBEdit());
       
        if(bSaved) jRBtnSatuanKecil.setSelected(true);
        
        SelectionListener listener = new SelectionListener(tblSupp);
        tblSupp.getSelectionModel().addListSelectionListener(listener);
        tblSupp.getColumnModel().getSelectionModel().addListSelectionListener(listener);
    }//GEN-LAST:event_formWindowOpened

    private void chkDispensingActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chkDispensingActionPerformed
        // TODO add your handling code here:
}//GEN-LAST:event_chkDispensingActionPerformed
    
    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new FrmItemMaster().setVisible(true);
            }
        });
    }

    private void udfAdd() {
        if(bSaved){
            DlgSuppBarang d1=new DlgSuppBarang(this,true);
            d1.setConn(conn);
            d1.udfSetBNew(true);
            d1.udfSetKodeBarang(txtKdBrg.getText());
            d1.udfSetNamaBarang(txtNama.getText());
            d1.udfSetRowPos(tblSupp.getSelectedRow());
            d1.udfSetEnabledKodeBarang(false);
            d1.udfSetTable(tblSupp);
            d1.setVisible(true);
        }else{
            JOptionPane.showMessageDialog(this, "Silakan simpan supplier terlebih dulu!", "Joss Prima", JOptionPane.OK_OPTION);
            jButton1.requestFocus();
            return;
        }
    }

    private void udfDeleteSupplier() {
        int iRow=tblSupp.getSelectedRow();
        if (tblSupp.getRowCount()>0 || getBEdit() && iRow>=0){
            int yesNo = JOptionPane.showConfirmDialog(null,"Anda YAKIN untuk MENGHAPUS Supplier '"+ tblSupp.getValueAt(iRow, 1).toString() +"' untuk barang ini?","SHS Go Open Source",JOptionPane.YES_NO_OPTION,JOptionPane.QUESTION_MESSAGE);
            if(yesNo == JOptionPane.YES_OPTION){
                try{
                conn.setAutoCommit(false);
                ItemBean iB=new ItemBean();
                iB.setConn(conn);
                int i=iB.DeleteItemSupp(txtKdBrg.getText(), tblSupp.getValueAt(tblSupp.getSelectedRow(),0).toString().trim());
                
                if (i>0){
                    modelSupp.removeRow(tblSupp.getSelectedRow());
                    conn.commit();
                    conn.setAutoCommit(true);
                    if(modelSupp.getRowCount()>0){
                        if(iRow<modelSupp.getRowCount())
                            tblSupp.setRowSelectionInterval(iRow, iRow);
                        else
                            tblSupp.setRowSelectionInterval(0, 0);
                    }
                }
                
            }catch(SQLException se){
                try{
                    pesanError(se.getMessage());
                    conn.rollback();
                    conn.setAutoCommit(true);
                }catch(SQLException se2){
                    //System.out.println(se2.getMessage());
                    pesanError(se2.getMessage());
                }
            }
            }
        }
    }
    
    private void udfSetDefaultBasePrice(){
        if(tblSupp.getRowCount()>0){
            if(Double.parseDouble(tblSupp.getValueAt(tblSupp.getSelectedRow(), 4).toString())>0){
                String sMsg="Anda yakin untuk set harga dari supplier '"+ tblSupp.getValueAt(tblSupp.getSelectedRow(), 1).toString() +"' sebagai default base price? \n\n" +
                        "(Harga lama adalah :"+df1.format(Double.parseDouble(txtBasePrice.getText().trim().replace(",",""))) +" dan akan di set dengan "+df1.format(Double.parseDouble(tblSupp.getValueAt(tblSupp.getSelectedRow(), 4).toString())) +")";

                if (JOptionPane.showConfirmDialog(null, sMsg, "Confirm",JOptionPane.OK_OPTION)==JOptionPane.OK_OPTION){
                    try{
                        conn.setAutoCommit(false);

                        ItemBean iBean=new ItemBean();
                        iBean.setConn(conn);

                        iBean.setKode(txtKdBrg.getText().trim());
                        iBean.setBasePrice(Float.parseFloat(tblSupp.getValueAt(tblSupp.getSelectedRow(), 4).toString()));

                        iBean.setDefaultBasePrice();

                        txtBasePrice.setText(df1.format(Double.parseDouble(tblSupp.getValueAt(tblSupp.getSelectedRow(), 4).toString())));
                    }catch(SQLException se){
                    try{
                        pesanError(se.getMessage());
                        conn.rollback();
                        conn.setAutoCommit(true);
                    }catch(SQLException se2){
                        //System.out.println(se2.getMessage());
                        pesanError(se2.getMessage());
                    }
                }
                }

            }else
                JOptionPane.showMessageDialog(null, "Silakan set dulu harga dari supplier bersangkutan!","SHS Message",JOptionPane.OK_OPTION);
        }
    }

    private String getItemCode() {
        return sItemCode;
    }
    
    public void setItemCode(String s){
        this.sItemCode=s;
    }
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JMenuItem JMISetDefault;
    private javax.swing.JButton btnAdd;
    private javax.swing.JButton btnDefault;
    private javax.swing.JButton btnDelete;
    private javax.swing.JButton btnEdit;
    private javax.swing.ButtonGroup buttonGroup1;
    private javax.swing.JCheckBox chkDiscontinued;
    private javax.swing.JCheckBox chkDispensing;
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel24;
    private javax.swing.JLabel jLabel25;
    private javax.swing.JLabel jLabel27;
    private javax.swing.JLabel jLabel28;
    private javax.swing.JLabel jLabel29;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel30;
    private javax.swing.JLabel jLabel31;
    private javax.swing.JLabel jLabel33;
    private javax.swing.JLabel jLabel34;
    private javax.swing.JLabel jLabel37;
    private javax.swing.JLabel jLabel42;
    private javax.swing.JLabel jLabel43;
    private javax.swing.JLabel jLabel44;
    private javax.swing.JLabel jLabel46;
    private javax.swing.JLabel jLabel47;
    private javax.swing.JLabel jLabel48;
    private javax.swing.JLabel jLabel49;
    private javax.swing.JLabel jLabel50;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JRadioButton jRBtnSatuanBesar;
    private javax.swing.JRadioButton jRBtnSatuanKecil;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JLabel lblJenis;
    private javax.swing.JLabel lblLokasi;
    private javax.swing.JLabel lblObatSama;
    private javax.swing.JPopupMenu popMSetDefault;
    private javax.swing.JTable tblSupp;
    private javax.swing.JTextField txtBarcode;
    private javax.swing.JFormattedTextField txtBasePrice;
    private javax.swing.JFormattedTextField txtHargaBesarNonResep;
    private javax.swing.JFormattedTextField txtHargaBesarResep;
    private javax.swing.JFormattedTextField txtHargaKecilNonResep;
    private javax.swing.JFormattedTextField txtHargaKecilResep;
    private javax.swing.JTextField txtJenis;
    private javax.swing.JTextField txtKdBrg;
    private javax.swing.JTextField txtKet;
    private javax.swing.JTextField txtKodeObatSama;
    private javax.swing.JFormattedTextField txtKonversi;
    private javax.swing.JTextField txtLokasi;
    private javax.swing.JFormattedTextField txtMax;
    private javax.swing.JFormattedTextField txtMin;
    private javax.swing.JTextField txtNama;
    private javax.swing.JTextField txtSatuanBesar;
    private javax.swing.JTextField txtSatuanKecil;
    // End of variables declaration//GEN-END:variables
    
    String sItemCode="";
    
    DefaultTableModel modelSupp=new DefaultTableModel();
    DecimalFormat df1 = new DecimalFormat("###,###,###,###");
    DecimalFormat decFmt = new DecimalFormat("###,###,###,###.00");
    ItemBean iB;
    //System.out.println(df1.format(1234.56));
        
}
