/*
 * NewJDialog.java
 *
 * Created on November 3, 2007, 10:12 PM
 */

package pembelian;

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
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import javax.swing.JFormattedTextField;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;
import javax.swing.text.MaskFormatter;
import main.GeneralFunction;
import main.ListRsbm;

/**
 *
 * @author  oestadho
 */
public class DlgEditItemTransGR extends javax.swing.JFrame {
    DefaultTableModel srcModel;
    JTable srcTable;
    ListRsbm lst;
    Connection conn;
    private String sSupplier;
    private NumberFormat formatter = new DecimalFormat("#,###,###");
    private NumberFormat formatter1 = new DecimalFormat("#,###,###.##");
    private JFormattedTextField jFDate1;
    private String sKode, sItem, sSatuan, sTglExp, sNoBatch;
    private float fQty, fHarga, fDisc, fPPn;
    private int konversi=1;
    private boolean isNew;
    private float fDiskon;
    private Object sNoPO;
    private MyKeyListener kListener=new MyKeyListener();
    private float harga_besar;
    private float harga_kecil;
    private GeneralFunction fn=new GeneralFunction();
    
    /** Creates new form NewJDialog */
    //public DlgEditItemTransGR(java.awt.Frame parent, boolean modal) {
    public DlgEditItemTransGR() {
        //super(parent, modal);
        initComponents();
        setAlwaysOnTop(true);
    }
    

    void setDiskon(float disc) {
        fDiskon=disc;
        txtDiscRupiah.setText(formatter.format(fDiskon));
    }

    void setExpDate(String toString) {
        jFExpired.setText(toString);
        jFExpired.setValue(toString);
    }

    void setHarga(float harga) {
        fHarga=harga;
        txtHarga.setText(formatter.format(fHarga));
        udfSetSubTotal();
    }

    void setIsNew(boolean b) {
        isNew=b;
    }

    void setKodeItem(String toString) {
        txtItem.setText(toString);
    }

    void setNoBatch(String toString) {
        txtNoBatch.setText(toString);
    }

    void setNoPO(String string) {
        sNoPO=string;
    }

    void setPPn(float ppn) {
        fPPn=ppn;
        txtPajakRupiah.setText(formatter.format(fPPn));
    }

    void setQtyBesar(float fBesar) {
        txtQtyBesar.setText(formatter.format(fBesar));
    }

    void setQtyKecil(float fKecil) {
        txtQtyKecil.setText(formatter.format(fKecil));
    }
    
    private void udfSetIsNew(boolean b){
        isNew=b;
    }
    
    private FocusListener txtFoculListener=new FocusListener() {
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

   private boolean udfCekBeforeSave() {
        boolean b=true;
        
        if(txtItem.getText().trim().equalsIgnoreCase("")){
            JOptionPane.showMessageDialog(this, "Silakan isi item terlebih dulu");
            txtItem.requestFocus();
            b=false;
            return b;
        }
        
        
        if(fn.udfGetFloat(lblSubTotal.getText())==0){
            JOptionPane.showMessageDialog(this, "Silakan lengkapi kuantitas atau harga item terlebih dulu");
            txtHarga.requestFocus();
            b=false;
            return b;
        }
        
        return b;
    }
   
   private void udfSave() {
        if(udfCekBeforeSave()){
            int iRow=srcTable.getSelectedRow();
                
            String sSatuan=fn.udfGetFloat(txtQtyBesar.getText())>0? formatter.format(fn.udfGetFloat(txtQtyBesar.getText()))+lblSatuanBesar.getText()+" ":"";
            sSatuan=sSatuan+(fn.udfGetFloat(txtQtyKecil.getText())>0? formatter.format(fn.udfGetFloat(txtQtyKecil.getText()))+lblSatuanKecil.getText():"");
            
            int iQty= fn.udfGetInt(txtQtyBesar.getText())*konversi+
                      fn.udfGetInt(txtQtyKecil.getText());
                 
            //float fHarga= fn.udfGetFloat(txtQtyBesar.getText())==0? fn.udfGetFloat(txtHarga.getText())/konversi: fn.udfGetFloat(txtHarga.getText());
            
            float fHarga= !txtQtyBesar.isEnabled()? fn.udfGetFloat(txtHarga.getText())/konversi: fn.udfGetFloat(txtHarga.getText());
            if (isNew) {
                srcModel.addRow(new Object[]{
                        srcModel.getRowCount() + 1, 
                        txtItem.getText(), 
                        lblItem.getText(), 
                        sSatuan, 
                        fn.udfGetFloat(txtHarga.getText()),
                        fn.udfGetFloat(txtDiscRupiah.getText()), 
                        fn.udfGetFloat(txtPajakRupiah.getText()), 
                        jFExpired.getText().trim().equalsIgnoreCase("/  /")? "":jFExpired.getText(),
                        txtNoBatch.getText(),
                        fn.udfGetFloat(lblSubTotal.getText()),
                        sNoPO,     //No. PO
                        fn.udfGetFloat(txtQtyBesar.getText()),
                        fn.udfGetFloat(txtQtyKecil.getText()),
                        fHarga,
                        konversi,
                        udfGetHargaSat()
//                        fn.udfGetFloat(txtHargaKecil.getText())
                });

                srcTable.setRowSelectionInterval(srcModel.getRowCount()-1, srcModel.getRowCount()-1);
            }else{
                srcModel.setValueAt(txtItem.getText(),iRow, 1);
                srcModel.setValueAt(lblItem.getText(), iRow, 2);
                srcModel.setValueAt(sSatuan, iRow, 3); 
                srcModel.setValueAt(fHarga, iRow, 4);
                srcModel.setValueAt(fn.udfGetFloat(txtDiscRupiah.getText()), iRow, 5); 
                srcModel.setValueAt(fn.udfGetFloat(txtPajakRupiah.getText()), iRow, 6);
                srcModel.setValueAt(jFExpired.getText().trim().equalsIgnoreCase("/  /")? "":jFExpired.getText(), iRow, 7);
                srcModel.setValueAt(txtNoBatch.getText(), iRow, 8);
                srcModel.setValueAt(fn.udfGetFloat(lblSubTotal.getText()), iRow, 9);
                //srcModel.setValueAt(sNoPO, iRow, 10);
                srcModel.setValueAt(fn.udfGetFloat(txtQtyBesar.getText()), iRow, 11);
                srcModel.setValueAt(fn.udfGetFloat(txtQtyKecil.getText()), iRow, 12);
                srcModel.setValueAt(fn.udfGetFloat(txtHarga.getText()), iRow, 13); //Harga
                srcModel.setValueAt(konversi, iRow, 14);
                srcModel.setValueAt(udfGetHargaSat(), iRow, 15);
                //srcModel.setValueAt(udfGetHargaSat(), iRow, 14);
                
            }
            try{
                String sQry="";
                if(fn.udfGetFloat(txtQtyBesar.getText())>0){
                    sQry="Update barang set harga_beli_besar="+fn.udfGetFloat(txtHarga.getText())+" where item_code='"+txtItem.getText()+"' ";
                }else{
                    sQry="Update barang set harga_beli_kecil="+fn.udfGetFloat(txtHarga.getText())+" where item_code='"+txtItem.getText()+"' ";
                }
                
                Statement st=conn.createStatement();
                int i=st.executeUpdate(sQry);
                
                
            }catch(SQLException se){
                JOptionPane.showMessageDialog(this, se.getMessage());
            }
            this.dispose();
            
        }
    }
   
   public void udfLoadBarang(){
        String sQry="select coalesce(item_name,'') as item_name, coalesce(satuan_besar,'') as satuan_besar, coalesce(konversi, 1) as konversi," +
                    "coalesce(satuan_besar,'') as satuan_besar, coalesce(satuan_kecil,'') as satuan_kecil, " +
                    "coalesce(harga_beli_besar,0) as harga_besar, coalesce(harga_beli_kecil,0) as harga_kecil, " +
                    "coalesce(base_price,0) as base_price " +
                    "from barang where item_code='"+txtItem.getText()+"'";
        
        try {
            Statement st;
        
            st = conn.createStatement();
            ResultSet rs=st.executeQuery(sQry);
            if(rs.next()){
                lblItem.setText(rs.getString("item_name"));
                lblSatuanBesar.setText(rs.getString("satuan_besar"));
                lblSatuanKecil.setText(rs.getString("satuan_kecil"));
                txtHarga.setText(formatter.format(rs.getFloat("harga_besar")));
//                txtHargaKecil.setText(formatter.format(rs.getFloat("harga_kecil_non_resep")));
//                
                konversi=rs.getInt("konversi");
                konversi=konversi==0? 1:konversi;
                harga_besar=rs.getFloat("harga_besar");
                harga_kecil=rs.getFloat("harga_besar");
                
            if(lblSatuanBesar.getText().equalsIgnoreCase("")){
//                txtHarga.setEnabled(false);
                txtQtyBesar.setEnabled(false);
                txtQtyBesar.setText("0");
                lblSatuanBesar.setText("");
                txtHarga.setText(formatter.format(harga_kecil));
                //lblKeterangan.setText("");
            }
            else{
                txtQtyBesar.setEnabled(true);
                txtHarga.setEnabled(true);
                txtHarga.setText(formatter.format(harga_besar));
                //lblKeterangan.setText("Isi dari 1 "+lblSatuanBesar.getText()+" adalah "+formatter.format(konversi)+" "+lblSatuanKecil.getText());
            }
            
            udfSetSubTotal();
        }else{
            udfBlank();
            
        }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        
        
   }
   
   private void udfBlank(){
        txtItem.setText("");
        lblItem.setText("");
        lblSatuanBesar.setText("");
        lblSatuanKecil.setText("");
        txtQtyBesar.setEditable(true);
        txtHarga.setText("0");
        //txtHargaKecil.setText("0");
        txtQtyBesar.setText("0");
        txtQtyKecil.setText("0");
        txtDiscPersen.setText("0");
        txtDiscRupiah.setText("0");
        txtPajakPersen.setText("0");
        txtPajakRupiah.setText("0");
        jFExpired.setText("");
        txtNoBatch.setText("");
        
   }
   
   private void setTextFocus(){
        txtItem.addFocusListener(txtFoculListener);
        txtQtyBesar.addFocusListener(txtFoculListener);
        txtQtyKecil.addFocusListener(txtFoculListener);
        txtHarga.addFocusListener(txtFoculListener);
        txtDiscPersen.addFocusListener(txtFoculListener);
        txtDiscRupiah.addFocusListener(txtFoculListener);
        txtPajakPersen.addFocusListener(txtFoculListener);
        txtPajakRupiah.addFocusListener(txtFoculListener);
        jFExpired.addFocusListener(txtFoculListener);
        txtNoBatch.addFocusListener(txtFoculListener);
        
   }
   
    public void setKode(String s){
        sKode=s;
        txtItem.setText(s);
    }
    
    public void setNamaItem(String s){
        sItem=s;
        lblItem.setText(s);
    }
    
    public void setSatuan(String s){
        lblSatuanKecil.setText(s);
        sSatuan=s;
    }
    
    public void setQty(float qty){
        txtQtyKecil.setText(formatter1.format(qty));
        fQty=qty;
    }
    
    public void setSupplier(String s){
        sSupplier=s;
    }
    
    private String getSupp(){
        return sSupplier;
    }
    
    public void setCon(Connection con){
        conn=con;
        fn.setConn(conn);
    }
    
    public void setSrcModel(DefaultTableModel mdl){
        srcModel=mdl;
    }
    
    public void setSrcTable(JTable tbl){
        srcTable=tbl;
    }
    
    private float udfGetHargaSat(){
        float fHarga=0;
        //int konversi=1;
        
        
        //
        if(fn.udfGetFloat(txtQtyBesar.getText())>0){ //HArga yang di pakai adalah besar
            fHarga=fn.udfGetFloat(txtHarga.getText())/(konversi==0?1:konversi);
            jLabel3.setText("Harga Sat Besar");
        }else{
            fHarga=fn.udfGetFloat(txtHarga.getText());
            jLabel3.setText("Harga Sat Kecil");
        }
        return fHarga;
    }
    
    private void udfSetSubTotal(){
//        lblSubTotal.setText(formatter.format(
//                ((fn.udfGetFloat(txtHarga.getText())*fn.udfGetFloat(txtQtyBesar.getText()))+
//                ((fn.udfGetFloat(txtHarga.getText())/konversi)*fn.udfGetFloat(txtQtyKecil.getText())))
//                -fn.udfGetFloat(txtDiscRupiah.getText())
//                +fn.udfGetFloat(txtPajakRupiah.getText())
//                ));
//        
        
        lblSubTotal.setText(formatter.format(
                ((udfGetHargaSat()* konversi* fn.udfGetFloat(txtQtyBesar.getText()))+
                (udfGetHargaSat()*fn.udfGetFloat(txtQtyKecil.getText())))
                -fn.udfGetFloat(txtDiscRupiah.getText())
                +fn.udfGetFloat(txtPajakRupiah.getText())
                ));
        
        //float fHarga= !txtQtyBesar.isEnabled()? fn.udfGetFloat(txtHarga.getText())/konversi: fn.udfGetFloat(txtHarga.getText());
        lblHargaSat.setText(String.valueOf(udfGetHargaSat()));
        lblKonv.setText(String.valueOf(konversi));
        
    }
    
    private void setFocusBlockTxt(JTextField txt){
        txt.setSelectionStart(0);
        txt.setSelectionEnd(txt.getText().length());
    }
    
    private void setFocusBlockTxtFmt(JFormattedTextField txt){
        txt.setSelectionStart(0);
        txt.setSelectionEnd(txt.getText().length());
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
//                case KeyEvent.VK_INSERT: {  //
//                    if (getBEdit()){
//                        if (tblGR.getRowCount()>=0){
//                            udfInsertDetail();
//                        }
//                    }    
//                    break;
//                }
//                 case KeyEvent.VK_F2: {  //Save
//                    if (getBEdit())
//                       udfUpdateData();
//                    break;
//                }
                
//                case KeyEvent.VK_F5: {  //New -- Add
//                    setBEdit(false);
//                    setBNew(false);
//                    udfNew();
//                    break;
//                }
                case KeyEvent.VK_F6: {  //Filter
                //    onOpen(cmbFilter.getSelectedItem().toString(),true);
                    break;
                }
                
                case KeyEvent.VK_ESCAPE: {
                    //Jika status button adalah Close
//                    if(sClose.equalsIgnoreCase("close")){
//                        if(!getBEdit()){
//                            if(JOptionPane.showConfirmDialog(null,"Anda Yakin Untuk Keluar?","SHS go Open Sources",JOptionPane.YES_NO_OPTION)==JOptionPane.YES_OPTION){
//                                if(lst.isVisible()){lst.dispose();}
//                                dispose();
//                            }
//                        }
//                        else
//                            if(JOptionPane.showConfirmDialog(null,"Apakah data disimpan sebelum anda keluar?","SHS go Open Sources",JOptionPane.YES_NO_OPTION)==JOptionPane.YES_OPTION){
//                                if(lst.isVisible()){lst.dispose();}
////                                udfUpdateData();
//                            }
//                            else{
//                                if(lst.isVisible()){lst.dispose();}
//                                dispose();
//                            }
//
//                            break;
//                    }   //Jika cancel
//                    else
//                        udfCancel();
                }
                //default ;
                
             }
        }
    }
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        btnOK = new javax.swing.JButton();
        btnCancel = new javax.swing.JButton();
        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        txtItem = new javax.swing.JTextField();
        txtDiscRupiah = new javax.swing.JTextField();
        lblSatuanBesar = new javax.swing.JLabel();
        txtDiscPersen = new javax.swing.JTextField();
        txtQtyBesar = new javax.swing.JTextField();
        txtPajakRupiah = new javax.swing.JTextField();
        jLabel10 = new javax.swing.JLabel();
        txtHarga = new javax.swing.JTextField();
        jLabel11 = new javax.swing.JLabel();
        txtPajakPersen = new javax.swing.JTextField();
        lblItem = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        lblSubTotal = new javax.swing.JLabel();
        txtNoBatch = new javax.swing.JTextField();
        txtQtyKecil = new javax.swing.JTextField();
        lblSatuanKecil = new javax.swing.JLabel();
        jFExpired = new javax.swing.JFormattedTextField();
        jLabel8 = new javax.swing.JLabel();
        jPanel2 = new javax.swing.JPanel();
        lblHargaSat = new javax.swing.JLabel();
        lblKonv = new javax.swing.JLabel();
        lblHargaSat2 = new javax.swing.JLabel();
        lblHargaSat3 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowOpened(java.awt.event.WindowEvent evt) {
                formWindowOpened(evt);
            }
        });
        getContentPane().setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        btnOK.setText("OK");
        btnOK.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnOKActionPerformed(evt);
            }
        });
        getContentPane().add(btnOK, new org.netbeans.lib.awtextra.AbsoluteConstraints(446, 264, 80, 40));

        btnCancel.setText("Batal");
        btnCancel.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                btnCancelMouseClicked(evt);
            }
        });
        btnCancel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCancelActionPerformed(evt);
            }
        });
        getContentPane().add(btnCancel, new org.netbeans.lib.awtextra.AbsoluteConstraints(530, 264, 90, 40));

        jPanel1.setBackground(new java.awt.Color(102, 153, 255));
        jPanel1.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        jPanel1.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel1.setFont(new java.awt.Font("Tahoma", 1, 11));
        jLabel1.setText("Item");
        jPanel1.add(jLabel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(15, 13, 80, -1));

        jLabel2.setFont(new java.awt.Font("Tahoma", 1, 11));
        jLabel2.setText("Jumlah");
        jPanel1.add(jLabel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(16, 40, 90, -1));

        jLabel3.setFont(new java.awt.Font("Tahoma", 1, 11));
        jLabel3.setText("Harga Sat Besar");
        jPanel1.add(jLabel3, new org.netbeans.lib.awtextra.AbsoluteConstraints(14, 99, 130, -1));

        jLabel4.setFont(new java.awt.Font("Tahoma", 1, 11));
        jLabel4.setText("Diskon");
        jPanel1.add(jLabel4, new org.netbeans.lib.awtextra.AbsoluteConstraints(326, 121, 70, -1));

        jLabel5.setFont(new java.awt.Font("Tahoma", 1, 11));
        jLabel5.setText("Pajak");
        jPanel1.add(jLabel5, new org.netbeans.lib.awtextra.AbsoluteConstraints(327, 156, 70, -1));

        txtItem.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        txtItem.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txtItemFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                txtItemFocusLost(evt);
            }
        });
        txtItem.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtItemKeyReleased(evt);
            }
        });
        jPanel1.add(txtItem, new org.netbeans.lib.awtextra.AbsoluteConstraints(118, 10, 106, 22));

        txtDiscRupiah.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtDiscRupiah.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        txtDiscRupiah.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txtDiscRupiahFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                txtDiscRupiahFocusLost(evt);
            }
        });
        jPanel1.add(txtDiscRupiah, new org.netbeans.lib.awtextra.AbsoluteConstraints(466, 120, 130, 23));

        lblSatuanBesar.setFont(new java.awt.Font("Tahoma", 1, 11));
        lblSatuanBesar.setForeground(new java.awt.Color(204, 255, 255));
        lblSatuanBesar.setText("Satuan Besar");
        jPanel1.add(lblSatuanBesar, new org.netbeans.lib.awtextra.AbsoluteConstraints(226, 37, 192, 23));

        txtDiscPersen.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtDiscPersen.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        txtDiscPersen.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txtDiscPersenFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                txtDiscPersenFocusLost(evt);
            }
        });
        jPanel1.add(txtDiscPersen, new org.netbeans.lib.awtextra.AbsoluteConstraints(403, 119, 37, 24));

        txtQtyBesar.setFont(new java.awt.Font("Tahoma", 1, 12));
        txtQtyBesar.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtQtyBesar.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        txtQtyBesar.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txtQtyBesarFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                txtQtyBesarFocusLost(evt);
            }
        });
        jPanel1.add(txtQtyBesar, new org.netbeans.lib.awtextra.AbsoluteConstraints(119, 36, 104, 23));

        txtPajakRupiah.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtPajakRupiah.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        txtPajakRupiah.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txtPajakRupiahFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                txtPajakRupiahFocusLost(evt);
            }
        });
        jPanel1.add(txtPajakRupiah, new org.netbeans.lib.awtextra.AbsoluteConstraints(466, 149, 130, 23));

        jLabel10.setFont(new java.awt.Font("Tahoma", 1, 11));
        jLabel10.setText("%");
        jPanel1.add(jLabel10, new org.netbeans.lib.awtextra.AbsoluteConstraints(445, 125, 20, -1));

        txtHarga.setFont(new java.awt.Font("Tahoma", 1, 12));
        txtHarga.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtHarga.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        txtHarga.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txtHargaFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                txtHargaFocusLost(evt);
            }
        });
        jPanel1.add(txtHarga, new org.netbeans.lib.awtextra.AbsoluteConstraints(119, 91, 130, 24));

        jLabel11.setFont(new java.awt.Font("Tahoma", 1, 11));
        jLabel11.setText("%");
        jPanel1.add(jLabel11, new org.netbeans.lib.awtextra.AbsoluteConstraints(447, 152, 20, -1));

        txtPajakPersen.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtPajakPersen.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        txtPajakPersen.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txtPajakPersenFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                txtPajakPersenFocusLost(evt);
            }
        });
        jPanel1.add(txtPajakPersen, new org.netbeans.lib.awtextra.AbsoluteConstraints(403, 148, 36, 23));

        lblItem.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        jPanel1.add(lblItem, new org.netbeans.lib.awtextra.AbsoluteConstraints(223, 10, 373, 22));

        jLabel7.setFont(new java.awt.Font("Tahoma", 1, 11));
        jLabel7.setText("Tgl. Exp");
        jPanel1.add(jLabel7, new org.netbeans.lib.awtextra.AbsoluteConstraints(15, 129, 92, -1));

        jLabel6.setFont(new java.awt.Font("Tahoma", 1, 11));
        jLabel6.setText("Sub Total");
        jPanel1.add(jLabel6, new org.netbeans.lib.awtextra.AbsoluteConstraints(348, 200, 90, -1));

        lblSubTotal.setFont(new java.awt.Font("Tahoma", 1, 12));
        lblSubTotal.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lblSubTotal.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        jPanel1.add(lblSubTotal, new org.netbeans.lib.awtextra.AbsoluteConstraints(449, 195, 148, 23));

        txtNoBatch.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtNoBatch.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        txtNoBatch.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txtNoBatchFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                txtNoBatchFocusLost(evt);
            }
        });
        jPanel1.add(txtNoBatch, new org.netbeans.lib.awtextra.AbsoluteConstraints(119, 149, 177, 23));

        txtQtyKecil.setFont(new java.awt.Font("Tahoma", 1, 12));
        txtQtyKecil.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtQtyKecil.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        txtQtyKecil.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txtQtyKecilFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                txtQtyKecilFocusLost(evt);
            }
        });
        jPanel1.add(txtQtyKecil, new org.netbeans.lib.awtextra.AbsoluteConstraints(119, 64, 104, 23));

        lblSatuanKecil.setFont(new java.awt.Font("Tahoma", 1, 11));
        lblSatuanKecil.setForeground(new java.awt.Color(204, 255, 255));
        lblSatuanKecil.setText("Satuan Kecil");
        jPanel1.add(lblSatuanKecil, new org.netbeans.lib.awtextra.AbsoluteConstraints(227, 65, 192, 23));

        jFExpired.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 1, true));
        jFExpired.setFont(new java.awt.Font("Dialog", 0, 12));
        jFExpired.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                jFExpiredFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                jFExpiredFocusLost(evt);
            }
        });
        jPanel1.add(jFExpired, new org.netbeans.lib.awtextra.AbsoluteConstraints(119, 120, 135, 24));

        jLabel8.setFont(new java.awt.Font("Tahoma", 1, 11));
        jLabel8.setText("No. Batch");
        jPanel1.add(jLabel8, new org.netbeans.lib.awtextra.AbsoluteConstraints(14, 157, 92, -1));

        getContentPane().add(jPanel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(4, 5, 620, 240));

        jPanel2.setBackground(new java.awt.Color(255, 255, 204));
        jPanel2.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        lblHargaSat.setText("00");
        jPanel2.add(lblHargaSat, new org.netbeans.lib.awtextra.AbsoluteConstraints(57, 3, 90, 20));

        lblKonv.setFont(new java.awt.Font("Tahoma", 1, 11));
        lblKonv.setText("1");
        jPanel2.add(lblKonv, new org.netbeans.lib.awtextra.AbsoluteConstraints(57, 23, 70, 20));

        lblHargaSat2.setText("Hrg. Sat :");
        jPanel2.add(lblHargaSat2, new org.netbeans.lib.awtextra.AbsoluteConstraints(4, 2, 50, 20));

        lblHargaSat3.setText("Konv      :");
        jPanel2.add(lblHargaSat3, new org.netbeans.lib.awtextra.AbsoluteConstraints(4, 22, 50, 20));

        getContentPane().add(jPanel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(5, 250, 240, 50));

        java.awt.Dimension screenSize = java.awt.Toolkit.getDefaultToolkit().getScreenSize();
        setBounds((screenSize.width-642)/2, (screenSize.height-347)/2, 642, 347);
    }// </editor-fold>//GEN-END:initComponents

    private void txtItemFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtItemFocusLost
        if(!lst.isVisible() && isNew)
            udfLoadBarang();
    }//GEN-LAST:event_txtItemFocusLost

    private void txtQtyKecilFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtQtyKecilFocusLost
// TODO add your handling code here:
        txtQtyKecil.setText(formatter1.format(fn.udfGetFloat(txtQtyKecil.getText())));
        udfSetSubTotal();
        
    }//GEN-LAST:event_txtQtyKecilFocusLost

    private void txtQtyKecilFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtQtyKecilFocusGained
        setFocusBlockTxt(txtQtyKecil);
    }//GEN-LAST:event_txtQtyKecilFocusGained

    private void txtNoBatchFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtNoBatchFocusLost
// TODO add your handling code here:
    }//GEN-LAST:event_txtNoBatchFocusLost

    private void txtNoBatchFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtNoBatchFocusGained
// TODO add your handling code here:
    }//GEN-LAST:event_txtNoBatchFocusGained

    private void txtPajakRupiahFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtPajakRupiahFocusGained
        setFocusBlockTxt(txtPajakRupiah);
    }//GEN-LAST:event_txtPajakRupiahFocusGained

    private void txtPajakPersenFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtPajakPersenFocusGained
        setFocusBlockTxt(txtPajakPersen);
    }//GEN-LAST:event_txtPajakPersenFocusGained

    private void txtDiscRupiahFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtDiscRupiahFocusGained
        setFocusBlockTxt(txtDiscRupiah);
    }//GEN-LAST:event_txtDiscRupiahFocusGained

    private void txtDiscPersenFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtDiscPersenFocusGained
        setFocusBlockTxt(txtDiscPersen);
    }//GEN-LAST:event_txtDiscPersenFocusGained

    private void txtHargaFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtHargaFocusGained
        setFocusBlockTxt(txtHarga);
}//GEN-LAST:event_txtHargaFocusGained

    private void txtQtyBesarFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtQtyBesarFocusGained
        setFocusBlockTxt(txtQtyBesar);
    }//GEN-LAST:event_txtQtyBesarFocusGained

    private void txtItemFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtItemFocusGained
//        setFocusBlockTxt(txtItem);
    }//GEN-LAST:event_txtItemFocusGained

    private void txtPajakRupiahFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtPajakRupiahFocusLost
        txtPajakRupiah.setText(formatter1.format(fn.udfGetFloat(txtPajakRupiah.getText())));
        
        if((fn.udfGetFloat(txtDiscRupiah.getText())*100)/(fn.udfGetFloat(txtHarga.getText())*fn.udfGetFloat(txtQtyBesar.getText())) !=fn.udfGetFloat(txtPajakPersen.getText())){
            txtPajakPersen.setText("");
            //return;
        }
        udfSetSubTotal();
    }//GEN-LAST:event_txtPajakRupiahFocusLost

    private void txtDiscRupiahFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtDiscRupiahFocusLost
        txtDiscRupiah.setText(formatter1.format(fn.udfGetFloat(txtDiscRupiah.getText())));
        
        if((fn.udfGetFloat(txtDiscRupiah.getText())*100)/(fn.udfGetFloat(txtHarga.getText())*fn.udfGetFloat(txtQtyBesar.getText())) !=fn.udfGetFloat(txtDiscPersen.getText())){
            txtDiscPersen.setText("");
            //return;
        }
        udfSetSubTotal();
    }//GEN-LAST:event_txtDiscRupiahFocusLost

    private void txtHargaFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtHargaFocusLost
        txtHarga.setText(formatter1.format(fn.udfGetFloat(txtHarga.getText())));
        udfSetSubTotal();
}//GEN-LAST:event_txtHargaFocusLost

    private void txtQtyBesarFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtQtyBesarFocusLost
        txtQtyBesar.setText(formatter1.format(fn.udfGetFloat(txtQtyBesar.getText())));
        udfSetSubTotal();
        
    }//GEN-LAST:event_txtQtyBesarFocusLost

    private void txtItemKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtItemKeyReleased
        String sQry="select b.item_code as Kode, coalesce(item_name,'') as Keterangan, " +
                                "coalesce(uom_alt, case when default_brows=1 then coalesce(satuan_kecil,'') else coalesce(satuan_besar,'') end) as satuan " +
                                "from barang b " +
                                "left join supplier_barang sb on sb.kode_barang=b.item_code and sb.kode_supplier iLike '%"+getSupp()+"%' " +
                                "where upper(b.item_code||item_name) like upper('%"+txtItem.getText()+"%') order by 2";
        fn.lookup(evt, new Object[]{lblItem}, sQry, txtItem.getWidth()+lblItem.getWidth()+18, 150);
    }//GEN-LAST:event_txtItemKeyReleased

    private void txtPajakPersenFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtPajakPersenFocusLost
        txtPajakPersen.setText(formatter1.format(fn.udfGetFloat(txtPajakPersen.getText())));
        
        if(fn.udfGetFloat(txtPajakPersen.getText())>100){
            JOptionPane.showMessageDialog(null, "Pajak melebihi 100%");
            txtPajakPersen.requestFocus();
            return;
        }else{
            txtPajakRupiah.setText(formatter.format(((udfGetHargaSat()*fn.udfGetFloat(txtQtyKecil.getText()))+(fn.udfGetFloat(txtQtyBesar.getText())*konversi*udfGetHargaSat() ))/100*fn.udfGetFloat(txtPajakPersen.getText())));
        }
        udfSetSubTotal();
    }//GEN-LAST:event_txtPajakPersenFocusLost

    private void txtDiscPersenFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtDiscPersenFocusLost
        txtDiscPersen.setText(formatter1.format(fn.udfGetFloat(txtDiscPersen.getText())));
        
        if(fn.udfGetFloat(txtDiscPersen.getText())>100){
            JOptionPane.showMessageDialog(null, "DIskon melebihi 100%");
            txtDiscPersen.requestFocus();
            return;
        }else{
            txtDiscRupiah.setText(formatter.format(((udfGetHargaSat()*fn.udfGetFloat(txtQtyKecil.getText()))+(fn.udfGetFloat(txtQtyBesar.getText())*konversi*udfGetHargaSat() ))  /100*fn.udfGetFloat(txtDiscPersen.getText())));
        }
        udfSetSubTotal();
    }//GEN-LAST:event_txtDiscPersenFocusLost

    private void formWindowOpened(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowOpened
        lst=new ListRsbm();
        lst.con=conn;
        lst.setAlwaysOnTop(true);
        MaskFormatter fmttgl=null;
        
         try {
            fmttgl = new MaskFormatter("##/##/####");
        } catch (java.text.ParseException e) {}
        
        jFDate1 = new JFormattedTextField(fmttgl);
        jFExpired.setFormatterFactory(jFDate1.getFormatterFactory());
        
        for(int i=0;i<jPanel1.getComponentCount();i++){
            Component c = jPanel1.getComponent(i);
            if(c.getClass().getSimpleName().equalsIgnoreCase("JTEXTFIELD")    || c.getClass().getSimpleName().equalsIgnoreCase("JFormattedTextField")
            || c.getClass().getSimpleName().equalsIgnoreCase("JTextArea") || c.getClass().getSimpleName().equalsIgnoreCase("JComboBox")
            || c.getClass().getSimpleName().equalsIgnoreCase("JButton") || c.getClass().getSimpleName().equalsIgnoreCase("JCheckBox")  ) {
                c.addKeyListener(kListener);
            }
        }
        btnOK.addKeyListener(kListener);
        btnCancel.addKeyListener(kListener);
        this.addKeyListener(kListener);
        
        setTextFocus();
        requestFocusInWindow();
        txtItem.requestFocus();
        
    }//GEN-LAST:event_formWindowOpened

    private void btnCancelMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnCancelMouseClicked
        dispose();
    }//GEN-LAST:event_btnCancelMouseClicked

    private void btnCancelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCancelActionPerformed
        dispose();
    }//GEN-LAST:event_btnCancelActionPerformed

    private void jFExpiredFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jFExpiredFocusGained
        setFocusBlockTxtFmt(jFExpired);
    }//GEN-LAST:event_jFExpiredFocusGained

    private void jFExpiredFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jFExpiredFocusLost
        if(!jFExpired.getText().trim().equalsIgnoreCase("/  /")) {
            if(!validateDate(jFExpired.getText(),true,"dd/MM/yyyy")){
                JOptionPane.showMessageDialog(null, "Silakan masukkan tanggal dengan format 'dd/MM/yyyy' !");
                jFExpired.setText("");
                jFExpired.requestFocus();
                return;
                
            }
        }else{
            jFExpired.setText("");
        }
    }//GEN-LAST:event_jFExpiredFocusLost

    private void btnOKActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnOKActionPerformed
        udfSave();
    }//GEN-LAST:event_btnOKActionPerformed
    
    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                //new DlgEditItemTransGR(new javax.swing.JFrame(), true).setVisible(true);
                new DlgEditItemTransGR().setVisible(true);
            }
        });
    }
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnCancel;
    private javax.swing.JButton btnOK;
    private javax.swing.JFormattedTextField jFExpired;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JLabel lblHargaSat;
    private javax.swing.JLabel lblHargaSat2;
    private javax.swing.JLabel lblHargaSat3;
    private javax.swing.JLabel lblItem;
    private javax.swing.JLabel lblKonv;
    private javax.swing.JLabel lblSatuanBesar;
    private javax.swing.JLabel lblSatuanKecil;
    private javax.swing.JLabel lblSubTotal;
    private javax.swing.JTextField txtDiscPersen;
    private javax.swing.JTextField txtDiscRupiah;
    private javax.swing.JTextField txtHarga;
    private javax.swing.JTextField txtItem;
    private javax.swing.JTextField txtNoBatch;
    private javax.swing.JTextField txtPajakPersen;
    private javax.swing.JTextField txtPajakRupiah;
    private javax.swing.JTextField txtQtyBesar;
    private javax.swing.JTextField txtQtyKecil;
    // End of variables declaration//GEN-END:variables

    Color g1 = new Color(153,255,255);
    Color g2 = new Color(255,255,255); 
    
    Color fHitam = new Color(0,0,0);
    Color fPutih = new Color(255,255,255); 
    
    Color crtHitam =new java.awt.Color(0, 0, 0);
    Color crtPutih = new java.awt.Color(255, 255, 255); 
}
