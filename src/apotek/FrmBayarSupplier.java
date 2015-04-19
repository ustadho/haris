/*
 * FrmBayarSupplier.java
 *
 * Created on October 7, 2007, 7:00 AM
 */

package apotek;

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
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
import java.sql.Statement;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
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
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import javax.swing.text.MaskFormatter;
import main.ListRsbm;
import main.SysConfig;

/**
 *
 * @author  Lab. GIS
 */
public class FrmBayarSupplier extends javax.swing.JInternalFrame {
    Connection conn;
    String sUserID, sUserName;
    private boolean isNew;
    private String sShift;
    private String sTglSkg;
    private DefaultTableModel myModel;
    private SimpleDateFormat fdateformat;
    private JFormattedTextField jFDate1;
    private ListRsbm lst;
    private NumberFormat intFormat = new DecimalFormat("#,###,###");
    private NumberFormat decFormat = new DecimalFormat("#,###,###.##");
    MyKeyListener kListener;
    private float totHutang=0, totTerutang=0;
    SysConfig sc=new SysConfig();
    MyTableCellEditor cEditor;
    JTextField tx=new JTextField();
                
    /** Creates new form FrmBayarSupplier */
    public FrmBayarSupplier() {
        initComponents();
        
        kListener=new MyKeyListener();
        
        for(int i=0;i<jPanel1.getComponentCount();i++){
            Component c = jPanel1.getComponent(i);
            if(c.getClass().getSimpleName().equalsIgnoreCase("JTEXTFIELD")    || c.getClass().getSimpleName().equalsIgnoreCase("JFormattedTextField")
            || c.getClass().getSimpleName().equalsIgnoreCase("JTextArea") || c.getClass().getSimpleName().equalsIgnoreCase("JComboBox")
            || c.getClass().getSimpleName().equalsIgnoreCase("JButton") || c.getClass().getSimpleName().equalsIgnoreCase("JCheckBox")
            ||c.getClass().getSimpleName().equalsIgnoreCase("JRadioButton")) {
                c.addKeyListener(kListener);
                c.addFocusListener(txtFoculListener);
            }
        }
        
        jScrollPane1.addKeyListener(kListener);
        tblHutang.addKeyListener(kListener);
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

    private void udfLoadSupplier() {
        if(!txtSupplier.getText().trim().equalsIgnoreCase("") && !lst.isVisible()){
            try {
                String sQry =   "select kode_supplier, coalesce(nama_supplier,'') as nama " + 
                                "from phar_supplier " + "where kode_supplier ='" + txtSupplier.getText() + "'";

                Statement st = conn.createStatement();
                ResultSet rs = st.executeQuery(sQry);

                if(!rs.next()){
                    JOptionPane.showMessageDialog(this, "Kode supplier tersebut tidak ditemukan");
                    txtSupplier.requestFocus();;
                    st.close();
                    rs.close();
                    return;
                }else{
                    rs.close();
                    rs=st.executeQuery("select * from fn_show_hutang_supp('"+txtSupplier.getText()+"') as " +
                            "(no_penerimaan varchar, tanggal text, tgl_jatuh_tempo text, jumlah float8, terutang float8)");

                    int i=1;
                    myModel.setNumRows(0);
                    
                    totHutang  =0;
                    totTerutang=0;
                    
                    while(rs.next()){
                        myModel.addRow(new Object[]{i,
                            rs.getString("no_penerimaan"),
                            rs.getString("tanggal"),
                            rs.getString("tgl_jatuh_Tempo"),
                            rs.getFloat("jumlah"),
                            rs.getFloat("terutang"),
                            0.0,
                            0.0,
                            0.0
                        });
                       i++; 

                       totHutang=totHutang+rs.getFloat("jumlah");
                       totTerutang=totTerutang+rs.getFloat("terutang");

                    }
                    txtTotHutang.setText(intFormat.format(totHutang));
                    txtTotTerutang.setText(intFormat.format(totTerutang));

                }


            } catch (SQLException ex) {
                Logger.getLogger(FrmBayarSupplier.class.getName()).log(Level.SEVERE, null, ex);
            }
        }else{
            myModel.setNumRows(0);
            
        }
    }
    
    public class MyTableCellEditor extends AbstractCellEditor implements TableCellEditor {
        private Toolkit toolkit;
        JTextField component=new JTextField(""); 
        JLabel label;// =new JLabel("");
        
        int col, row;
        
        public void setComponent(JTextField com){
            component=com;
            
            component.addKeyListener(new java.awt.event.KeyAdapter() {
                public void keyPressed(java.awt.event.KeyEvent evt) {
                    componentKeyPressed(evt);
                }
                
                public void keyReleased(java.awt.event.KeyEvent evt) {
                    //if(col==4)
                        //componentKeyReleased(evt);
                }
            }); 
            
            component.addFocusListener(new java.awt.event.FocusAdapter() {
                 public void focusGained(java.awt.event.FocusEvent evt) {
                      componentFocusGained(evt);
                      
                 }
                 public void focusLost(java.awt.event.FocusEvent evt) {
                      componentFocusLost(evt);
                 }
            });                    
          
        }
        
        public void setCellLabel(JLabel lbl){
            label=lbl;
        }
        
        public void setComponentFocus(){
            component.setSelectionStart(0);
            component.setSelectionEnd(component.getText().length());
            component.requestFocusInWindow();
            component.requestFocus();
        }
        
//        Font ft=new Font("Tahoma",Font.BOLD,14);
//        component.setFont(new Font("Tahoma",Font.BOLD,14));
        
        private NumberFormat  nf=NumberFormat.getInstance();
         
        public Component getTableCellEditorComponent(JTable table, Object value,
            boolean isSelected, int rowIndex, int vColIndex) {
            // 'value' is value contained in the cell located at (rowIndex, vColIndex)
            
           col=vColIndex;
           row=rowIndex;
           component.setBackground(new Color(248,255,167));
           if (isSelected) {
               
            
           }       
           //System.out.println("Value dari editor :"+value);
            component.setText(value==null? "": value.toString());                                    
            //component.setText(df.format(value));                                    
                        
            if(value instanceof Double || value instanceof Float|| value instanceof Integer){
                try {
                    //Double dVal=Double.parseDouble(value.toString().replace(",",""));
                    Number dVal = nf.parse(value.toString());
                    component.setText(nf.format(dVal));
                } catch (ParseException ex) {
                    Logger.getLogger(FrmBayarSupplier.class.getName()).log(Level.SEVERE, null, ex);
                }
            }else
                component.setText(value==null? "":value.toString());
           return component;
        }
         
        private void componentFocusLost(java.awt.event.FocusEvent evt) {
//            if (component.isVisible()==false){
//                //System.out.println("Component Focus Lost"+"-"+tblJadwal.getSelectedRow()+"-"+tblJadwal.getSelectedColumn()); 
//                lst.setVisible(false);
//            }
//            if(lst.isVisible())
//                lst.setVisible(false)
        }  
        
        private void componentFocusGained(java.awt.event.FocusEvent evt) {
            //if(col>=6)
                component.setSelectionStart(0);
                component.setSelectionEnd(component.getText().length());
            //System.out.println("Component Focus Gained");               
        }  

        public void componentKeyPressed(java.awt.event.KeyEvent evt) {                                  
           //System.out.println(evt.getKeyChar());
           if (evt.getKeyChar() == evt.CHAR_UNDEFINED){
               
           }else{                
            
           }
            
           //----------------------------------
          char c = evt.getKeyChar();
          if (!((c >= '0') && (c <= '9') ||
             (c != KeyEvent.VK_BACK_SPACE) ||
             (c != KeyEvent.VK_DELETE) ||
             (c != KeyEvent.VK_ENTER))) {
                getToolkit().beep();
                evt.consume();
                return;
          }
           
           //-----------------------------------
           switch(evt.getKeyCode()) {
               case KeyEvent.VK_ENTER:{
                        //tblResep.changeSelection(tblResep.getSelectedRow()-1,tblResep.getSelectedColumn()+1, false, false);
                        if(component.isVisible()){
                            
                            tblHutang.setRowSelectionInterval(tblHutang.getSelectedRow(), tblHutang.getSelectedRow());
                            component.setSelectionStart(0);
                            component.setSelectionEnd(component.getText().length());
//                            component.setVisible(false);
                        }else{
                            component.setVisible(true);
                            component.setFocusable(true);
                            component.setSelectionStart(0);
                            component.setSelectionEnd(component.getText().length());
                            }
                        
                        if(col >=6){
                            //tblResep.changeSelection(row, col, false, false);
                            //tblResep.setRowSelectionInterval(row-1, row-1);
                            tblHutang.setColumnSelectionInterval(col, col);
                            
                            if(tblHutang.getSelectedRow()>0)
                                tblHutang.setRowSelectionInterval(row-1, row-1);
                            
                        }else
                        {
                            tblHutang.changeSelection(row, col, false, false);
                            tblHutang.setColumnSelectionInterval(row, 3);
                        }
                        }
                        break;
                    
                    case java.awt.event.KeyEvent.VK_DOWN:{
                        //System.out.println("ok Key down");
                        int row=tblHutang.getSelectedRow();
                        System.out.println(row);
                        
                        }
                        break;
                        
                    case java.awt.event.KeyEvent.VK_UP:{
                        //System.out.println("ok Key Up");
                        if(tblHutang.getSelectedRow()>0)
                        
                            tblHutang.setRowSelectionInterval(tblHutang.getSelectedRow(), tblHutang.getSelectedRow());
                        else
                            tblHutang.setRowSelectionInterval(0,0);
                        }
                    
                    default:
                        {
                            component.setSelectionEnd(component.getText().length());
                        }                        
                        break;
                }
        }
                    
        public Object getCellEditorValue() {
//            //System.out.println(lf.getResCode()+"  "+lf.getResDes());
            Object o="";//=component.getText();
//            if((Double)Double.parseDouble(component.getText().replace(",","")) instanceof Number)
//            {
////            if ((o!=null) && o instanceof Double)
//                //return ((JTextField)component).getText();
//                o=((JTextField)component).getText();
//                //o=doubleFormatter.format(((JTextField)component).getText());
//            }
//            else
//                o=0;
            
            //====================================================================
            Object retVal = 0;
		if(col>=6){
                    try {
                        retVal = Integer.parseInt(((JTextField)component).getText().replace(",",""));
                        
//                        if(Integer.parseInt(retVal.toString() > Integer.parseInt(tblHutang.getValueAt(tblHutang.getSelectedRow(), 5).toString())){
//                            JOptionPane.showMessageDialog(null, "Jumlah pembayaran melebihi jumlah terhutang!");
//                            toolkit.beep();
//                            retVal=0;
//                        }
                        o=nf.format(retVal);
                        
                        
                        return o;
                    } catch (Exception e) {
                        toolkit.beep();
                        retVal=0;
                    }
                }else
                    retVal=(Object)component.getText();
		return retVal;
                
            //return o;                                    
        }
        
        public int getValue() {
		int retVal = 0;
		try {
                    retVal = Integer.parseInt(((JTextField)component).getText());
		} catch (Exception e) {
                    toolkit.beep();
		}
		return retVal;
	}
    }
    
    public class MyKeyListener extends KeyAdapter {
        public void keyPressed(KeyEvent evt) {
            
            if(evt.getSource().equals(tblHutang)){
                return;
            }
            int keyKode = evt.getKeyCode();
            switch(keyKode){
                
                
                case KeyEvent.VK_ENTER : {
		    if (!lst.isVisible()){
			Component c = findNextFocus();
			if (c!=null) c.requestFocus();
		    }else{
			lst.requestFocus();
		    }
		    break;
		}
                case KeyEvent.VK_UP : {
		    if (!lst.isVisible()){
			Component c = findPrevFocus();
			if (c!=null) c.requestFocus();
		    }else{
			lst.requestFocus();
		    }
		    break;
		}
                case KeyEvent.VK_DOWN : {
		    if (!lst.isVisible()){
			Component c = findNextFocus();
			if (c!=null) c.requestFocus();
		    }else{
			lst.requestFocus();
		    }
		    break;
		}
                case KeyEvent.VK_F2: {  //Save
                    if (isNew)
                       udfSave();
                    break;
                }
               case KeyEvent.VK_F5: {  //New -- Add
                    
                    udfNew();
                    break;
                }
                case KeyEvent.VK_ESCAPE: {
                    //Jika status button adalah Close
                    if(lst.isVisible()){
                        lst.setVisible(false);
                        return;
                        
                    }
                    
                    if(btnClose.getToolTipText().equalsIgnoreCase("Batal (Esc)")){
                        if(!isNew){
                            if(JOptionPane.showConfirmDialog(null,"Anda Yakin Untuk Keluar?","Joss Prima go Open Sources",JOptionPane.YES_NO_OPTION)==JOptionPane.YES_OPTION){
                                if(lst.isVisible()){lst.dispose();}
                                dispose();
                            }
                        }
                        else
                            if(JOptionPane.showConfirmDialog(null,"Apakah data disimpan sebelum anda keluar?","Joss Prima go Open Sources",JOptionPane.YES_NO_OPTION)==JOptionPane.YES_OPTION){
                                if(lst.isVisible()){lst.dispose();}
                                //udfUpdateData();
                            }
                            else{
                                if(lst.isVisible()){lst.dispose();}
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

        private Component findNextFocus() {
            throw new UnsupportedOperationException("Not yet implemented");
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
//            int firstRow = e.getFirstRow();
//            int lastRow = e.getLastRow();
//            int mColIndex = e.getColumn();
//                        
//            total = new Double(0);
//            totalDiscount=new Double(0);
//            totalVat=new Double(0);
//            netto=new Double(0);
//            Double ttl=new Double(0);
//            Double ttlDisc=new Double(0);
//            Double ttlVat=new Double(0);
//            for(int i=0;i<table.getRowCount();i++){
//                ttl=0.0;
//                ttlDisc=0.0;
//                ttlVat=0.0;
//                ttl=Double.valueOf(table.getValueAt(i,4).toString().replace(",", ""))*Double.valueOf(table.getValueAt(i,5).toString().replace(",", ""));
//                ttlDisc = ttl*(Double.valueOf(table.getValueAt(i,6).toString().replace(",", ""))/100);
//                ttlVat=((ttl-ttlDisc)*(Double.valueOf(table.getValueAt(i,7).toString().replace(",", ""))/100));
//                total = total + ttl;
//                totalDiscount = totalDiscount + ttlDisc;
//                totalVat = totalVat + ttlVat;
//                //table.setValueAt((ttl-ttlDisc)+ttlVat,i,9);
//            }
//            netto=(total-totalDiscount+totalVat);
//            lblTotal.setText(formatter.format(total));
//            lblTotalDiscount.setText(formatter.format(totalDiscount));
//            lblTotalVat.setText(formatter.format(totalVat));
//            lblNetto.setText(formatter.format(netto));
//            if (netto>=batasPO){
//                writeMsg(" Nilai PO sudah lebih dari '"+batasPO+"'");
//            }
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
    
    public void setUserID(String s){
        sUserID=s;
    }
    
    public void setUserName(String s){
        sUserName=s;
    }
    
    private void udfSave(){
        if(txtSupplier.getText().equalsIgnoreCase("")){
            JOptionPane.showMessageDialog(this, "Silakan isi supplier terlebih dulu!");
            txtSupplier.requestFocus();
            return;
        }
        if(txtJenisBayar.getText().equalsIgnoreCase("")){
            JOptionPane.showMessageDialog(this, "Silakan isi jenis pembayaran terlebih dulu!");
            txtJenisBayar.requestFocus();
            return;
        }
        if(tblHutang.getRowCount()==0){
            JOptionPane.showMessageDialog(this, "Hutang yang harus dibayar tidak ada!");
            txtSupplier.requestFocus();
            return;
        }
        if(udfGetFloat(txtTotBayar.getText())==0){
            JOptionPane.showMessageDialog(this, "Jumlah pembayaran masih nol!");
            tblHutang.requestFocus();
            tblHutang.changeSelection(0, 6, false, false);
            return;
        }
        
        try {
            String sTglBayar=jFBayar.getText().substring(6,10).trim()+"-"+jFBayar.getText().substring(3,5).trim()+"-"+jFBayar.getText().substring(0,2).trim();
            String sTglCek  =jFTglCek.getText().substring(6,10).trim()+"-"+jFTglCek.getText().substring(3,5).trim()+"-"+jFTglCek.getText().substring(0,2).trim();
            
            
            Statement stH=conn.createStatement();
            ResultSet rsH=stH.executeQuery("select * from ap limit 1");
            
            Statement stD=conn.createStatement();
            ResultSet rsD=stD.executeQuery("select * from ap_detail limit 1");
            
            conn.setAutoCommit(false);
            String sNoAP=getNoAP();
            
            rsH.moveToInsertRow();
            rsH.updateString("kode_ap", sNoAP);
            rsH.updateDate("tanggal", java.sql.Date.valueOf(sTglBayar));
            rsH.updateString("kode_supplier", txtSupplier.getText());
            rsH.updateString("kode_pembayaran", txtJenisBayar.getText());
//            rsH.updateString("kurs", getNoAP());
            rsH.updateString("no_cek", txtNoCek.getText());
            rsH.updateDate("tgl_cek", java.sql.Date.valueOf(sTglCek));
            rsH.updateString("catatan", txtCatatan.getText());
            rsH.updateString("no_bukti", txtNoBukti.getText());
            rsH.insertRow();
            
            for(int i=0; i<tblHutang.getRowCount(); i++){
                if(udfGetFloat(tblHutang.getValueAt(i, 6).toString())>0 ||
                   udfGetFloat(tblHutang.getValueAt(i, 7).toString())>0 ||
                   udfGetFloat(tblHutang.getValueAt(i, 8).toString())>0 ){
                    
                    rsD.moveToInsertRow();
                    rsD.updateString("kode_ap", sNoAP);
                    rsD.updateString("no_faktur", tblHutang.getValueAt(i, 1).toString());
                    rsD.updateFloat("terutang", udfGetFloat(tblHutang.getValueAt(i, 5).toString()));
                    rsD.updateFloat("jumlah", udfGetFloat(tblHutang.getValueAt(i, 6).toString()));
                    rsD.updateFloat("diskon", udfGetFloat(tblHutang.getValueAt(i, 7).toString()));
                    rsD.updateFloat("denda", udfGetFloat(tblHutang.getValueAt(i, 8).toString()));
                    rsD.insertRow();
                }
            }
            
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
            
            JOptionPane.showMessageDialog(this, "Data sudah tersimpan!!");
//            if (JOptionPane.showConfirmDialog(null,"Siapkan Printer!","Joss Prima Open Source",JOptionPane.YES_NO_OPTION,JOptionPane.QUESTION_MESSAGE)==JOptionPane.YES_OPTION) {            
//                //PrintBayarPemasok pn = new PrintBayarPemasok(conn, sNoAP, false, services[i]);
//                JOptionPane.showMessageDialog(this, "Print durung mari bos!!");
//            }
            
            
            conn.setAutoCommit(true);
            
            isNew=false;
            setUpBtn();
            udfBlank();
            
            
            } catch (SQLException ex) {
                try {
                    JOptionPane.showMessageDialog(this, "Gagal insert. \n"+ex.getMessage());
                    conn.rollback();
                    conn.setAutoCommit(true);
                    
                } catch (SQLException ex1) {
                    JOptionPane.showMessageDialog(this, ex1.getMessage());
                    //conn.setAutoCommit(true);
                }
            }catch(IllegalArgumentException il){
                JOptionPane.showMessageDialog(this, "Gagal insert. \n"+il.getMessage());
            }
    }
    
    private void udfSetEnabledComponent(){
        txtSupplier.setEnabled(isNew);
        lblSupplier.setEnabled(isNew);
        txtPenerima.setEnabled(isNew);
        txtJenisBayar.setEnabled(isNew);
        lblJenisBayar.setEnabled(isNew);
        jFBayar.setEnabled(isNew);
        txtNoBukti.setEnabled(isNew);
        txtNoCek.setEnabled(isNew);
        jFTglCek.setEnabled(isNew);
        txtSupplier.setEnabled(isNew);
        txtTotBayar.setEnabled(isNew);
        txtTotDenda.setEnabled(isNew);
        txtTotDiskon.setEnabled(isNew);
        lblGrandTotal.setEnabled(isNew);
    }
    
    private String getNoAP(){
        String sKode = "";
        try {
            
            Statement st = conn.createStatement();
            ResultSet rs = st.executeQuery("select fn_get_kode_bayar_supplier()");

            if(rs.next())
                sKode=rs.getString(1);
            else
                sKode="";
            
            rs.close();
            st.close();
            
        } catch (SQLException ex) {
            Logger.getLogger(FrmBayarSupplier.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return sKode;
    }
    
    public void setUpBtn(){
        btnSave.setEnabled(isNew? true: false); 
        if(isNew){
            btnClose.setIcon(new javax.swing.ImageIcon(getClass().getResource("/image/Icon/Cancel.png")));
            btnClose.setToolTipText("Batal (Esc)");
            
        }else{
            btnClose.setIcon(new javax.swing.ImageIcon(getClass().getResource("/image/Icon/Exit.png")));
            btnClose.setToolTipText("Keluar (Esc)");
            
        }
        udfSetEnabledComponent();
        
        btnSave.setEnabled(isNew);
        btnNew.setEnabled(!isNew);
    }

    private void udfBlank(){
        txtSupplier.setText("");
        lblSupplier.setText("");
        txtPenerima.setText("");
        txtJenisBayar.setText("");
        lblJenisBayar.setText("");
        jFBayar.setText(sTglSkg);
        txtNoBukti.setText("");
        txtNoCek.setText("");
        jFTglCek.setText(sTglSkg);
        txtSupplier.requestFocus();
        txtTotBayar.setText("0");
        txtTotDenda.setText("0");
        txtTotDiskon.setText("0");
        lblGrandTotal.setText("0");
        
        myModel.setNumRows(0);
    }
    
    void setConn(Connection conn) {
        this.conn=conn;
    }

    void setShift(String sShift) {
        this.sShift=sShift;
    }

    void setUserId(String sID) {
        sUserID=sID;
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
                //tblHutang.getModel().addTableModelListener(new MyTableModelListener(tblGR));
                rowPos = table.getSelectedRow();           
                
                if (rowPos >=0 && rowPos < table.getRowCount() && table.getValueAt(rowPos,0)!=null) { 
                    udfSetTotal();
                    
                }else{
                    txtTotBayar.setText("0");
                    txtTotDenda.setText("0");
                    txtTotDiskon.setText("0");
                }
            }
        }

        private void udfSetTotal() {
            float totBayar=0, totDenda=0, totDisc=0;
            for(int i=0; i<table.getRowCount();i++){
                totBayar=totBayar+udfGetFloat(table.getValueAt(i, 6).toString());
                totDisc=totDisc+udfGetFloat(table.getValueAt(i, 7).toString());
                totDenda=totDenda+udfGetFloat(table.getValueAt(i, 8).toString());
            }

            txtTotBayar.setText(intFormat.format(totBayar));
            txtTotDiskon.setText(intFormat.format(totDisc));
            txtTotDenda.setText(intFormat.format(totDenda));
            
            lblGrandTotal.setText(intFormat.format(totBayar - totDisc + totDenda));
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
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jScrollPane1 = new javax.swing.JScrollPane();
        tblHutang = new javax.swing.JTable();
        jLabel4 = new javax.swing.JLabel();
        lblHeader1 = new javax.swing.JLabel();
        lblHeader2 = new javax.swing.JLabel();
        jPanel1 = new javax.swing.JPanel();
        txtSupplier = new javax.swing.JTextField();
        jLabel9 = new javax.swing.JLabel();
        lblSupplier = new javax.swing.JLabel();
        txtPenerima = new javax.swing.JTextField();
        jLabel10 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        jLabel11 = new javax.swing.JLabel();
        txtJenisBayar = new javax.swing.JTextField();
        lblJenisBayar = new javax.swing.JLabel();
        txtCatatan = new javax.swing.JTextField();
        jLabel12 = new javax.swing.JLabel();
        txtNoBukti = new javax.swing.JTextField();
        jLabel13 = new javax.swing.JLabel();
        jLabel15 = new javax.swing.JLabel();
        jFBayar = new javax.swing.JFormattedTextField();
        txtNoCek = new javax.swing.JTextField();
        jLabel14 = new javax.swing.JLabel();
        jFTglCek = new javax.swing.JFormattedTextField();
        jPanel2 = new javax.swing.JPanel();
        txtTotBayar = new javax.swing.JTextField();
        txtTotDiskon = new javax.swing.JTextField();
        jLabel8 = new javax.swing.JLabel();
        jLabel20 = new javax.swing.JLabel();
        lblGrandTotal = new javax.swing.JLabel();
        jLabel17 = new javax.swing.JLabel();
        txtTotDenda = new javax.swing.JTextField();
        txtTotTerutang = new javax.swing.JTextField();
        txtTotHutang = new javax.swing.JTextField();
        jLabel18 = new javax.swing.JLabel();
        jLabel16 = new javax.swing.JLabel();
        btnNew = new javax.swing.JButton();
        btnSave = new javax.swing.JButton();
        btnClose = new javax.swing.JButton();

        setClosable(true);
        setIconifiable(true);
        setMaximizable(true);
        setResizable(true);
        setTitle("Pembayaran Hutang");
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

        tblHutang.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, null, null, null, null}
            },
            new String [] {
                "No.", "No. Faktur", "Tanggal", "Jatuh Tempo", "Jumlah", "Terutang", "Jml. Pembayaran", "Diskon", "Denda"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Object.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.Double.class, java.lang.Double.class, java.lang.Double.class, java.lang.Double.class, java.lang.Double.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false, true, true, true
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        tblHutang.setCellSelectionEnabled(true);
        tblHutang.getTableHeader().setReorderingAllowed(false);
        tblHutang.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                tblHutangFocusGained(evt);
            }
        });
        tblHutang.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                tblHutangKeyTyped(evt);
            }
        });
        jScrollPane1.setViewportView(tblHutang);

        getContentPane().add(jScrollPane1, new org.netbeans.lib.awtextra.AbsoluteConstraints(6, 219, 960, 210));

        jLabel4.setBackground(new java.awt.Color(255, 204, 204));
        jLabel4.setFont(new java.awt.Font("Bitstream Vera Sans", 1, 14));
        jLabel4.setForeground(new java.awt.Color(51, 51, 51));
        jLabel4.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel4.setText("HUTANG BELUM LUNAS");
        jLabel4.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));
        jLabel4.setOpaque(true);
        getContentPane().add(jLabel4, new org.netbeans.lib.awtextra.AbsoluteConstraints(4, 192, 960, -1));

        lblHeader1.setFont(new java.awt.Font("Bookman Old Style", 1, 24));
        lblHeader1.setForeground(new java.awt.Color(153, 51, 0));
        lblHeader1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblHeader1.setText("Pembayaran Hutang Supplier");
        lblHeader1.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        getContentPane().add(lblHeader1, new org.netbeans.lib.awtextra.AbsoluteConstraints(549, 9, 410, 30));

        lblHeader2.setFont(new java.awt.Font("Bookman Old Style", 1, 24));
        lblHeader2.setForeground(new java.awt.Color(153, 153, 153));
        lblHeader2.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblHeader2.setText("Pembayaran Hutang Supplier");
        lblHeader2.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        getContentPane().add(lblHeader2, new org.netbeans.lib.awtextra.AbsoluteConstraints(547, 9, 410, 30));

        jPanel1.setBackground(new java.awt.Color(51, 102, 255));
        jPanel1.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        jPanel1.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        txtSupplier.setFont(new java.awt.Font("Dialog", 1, 12));
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
        jPanel1.add(txtSupplier, new org.netbeans.lib.awtextra.AbsoluteConstraints(110, 16, 102, 24));

        jLabel9.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel9.setText("Supplier");
        jPanel1.add(jLabel9, new org.netbeans.lib.awtextra.AbsoluteConstraints(12, 19, 90, -1));

        lblSupplier.setFont(new java.awt.Font("Dialog", 3, 12));
        lblSupplier.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 1, true));
        jPanel1.add(lblSupplier, new org.netbeans.lib.awtextra.AbsoluteConstraints(215, 16, 264, 24));

        txtPenerima.setFont(new java.awt.Font("Dialog", 1, 12));
        txtPenerima.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 1, true));
        txtPenerima.setDisabledTextColor(new java.awt.Color(153, 153, 153));
        txtPenerima.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                txtPenerimaFocusLost(evt);
            }
        });
        txtPenerima.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txtPenerimaKeyPressed(evt);
            }
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtPenerimaKeyReleased(evt);
            }
        });
        jPanel1.add(txtPenerima, new org.netbeans.lib.awtextra.AbsoluteConstraints(109, 43, 370, 24));

        jLabel10.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel10.setText("Penerima");
        jPanel1.add(jLabel10, new org.netbeans.lib.awtextra.AbsoluteConstraints(9, 47, 90, -1));

        jLabel7.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel7.setText("No. Bukti");
        jPanel1.add(jLabel7, new org.netbeans.lib.awtextra.AbsoluteConstraints(579, 45, 140, 20));

        jLabel11.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel11.setText("Jenis Pembayaran");
        jPanel1.add(jLabel11, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 74, 90, -1));

        txtJenisBayar.setFont(new java.awt.Font("Dialog", 1, 12));
        txtJenisBayar.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 1, true));
        txtJenisBayar.setDisabledTextColor(new java.awt.Color(153, 153, 153));
        txtJenisBayar.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                txtJenisBayarFocusLost(evt);
            }
        });
        txtJenisBayar.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtJenisBayarKeyReleased(evt);
            }
        });
        jPanel1.add(txtJenisBayar, new org.netbeans.lib.awtextra.AbsoluteConstraints(108, 71, 50, 24));

        lblJenisBayar.setFont(new java.awt.Font("Dialog", 3, 12));
        lblJenisBayar.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 1, true));
        jPanel1.add(lblJenisBayar, new org.netbeans.lib.awtextra.AbsoluteConstraints(158, 71, 430, 24));

        txtCatatan.setFont(new java.awt.Font("Dialog", 1, 12));
        txtCatatan.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 1, true));
        txtCatatan.setDisabledTextColor(new java.awt.Color(153, 153, 153));
        jPanel1.add(txtCatatan, new org.netbeans.lib.awtextra.AbsoluteConstraints(108, 99, 480, 24));

        jLabel12.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel12.setText("Catatan");
        jPanel1.add(jLabel12, new org.netbeans.lib.awtextra.AbsoluteConstraints(11, 103, 90, -1));

        txtNoBukti.setDisabledTextColor(new java.awt.Color(153, 153, 153));
        jPanel1.add(txtNoBukti, new org.netbeans.lib.awtextra.AbsoluteConstraints(722, 41, 220, 24));

        jLabel13.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel13.setText("No. Cek");
        jPanel1.add(jLabel13, new org.netbeans.lib.awtextra.AbsoluteConstraints(625, 72, 90, -1));

        jLabel15.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel15.setText("Tgl. Cek");
        jPanel1.add(jLabel15, new org.netbeans.lib.awtextra.AbsoluteConstraints(630, 103, 90, -1));

        jFBayar.setFont(new java.awt.Font("Dialog", 1, 12));
        jFBayar.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                jFBayarFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                jFBayarFocusLost(evt);
            }
        });
        jPanel1.add(jFBayar, new org.netbeans.lib.awtextra.AbsoluteConstraints(721, 15, 110, 22));

        txtNoCek.setFont(new java.awt.Font("Tahoma", 1, 11));
        txtNoCek.setText("4");
        jPanel1.add(txtNoCek, new org.netbeans.lib.awtextra.AbsoluteConstraints(723, 69, 210, 24));

        jLabel14.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel14.setText("Tgl. Pembayaran");
        jPanel1.add(jLabel14, new org.netbeans.lib.awtextra.AbsoluteConstraints(578, 15, 140, 20));

        jFTglCek.setFont(new java.awt.Font("Dialog", 1, 12));
        jFTglCek.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                jFTglCekFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                jFTglCekFocusLost(evt);
            }
        });
        jPanel1.add(jFTglCek, new org.netbeans.lib.awtextra.AbsoluteConstraints(723, 97, 110, 22));

        getContentPane().add(jPanel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(4, 58, 960, 130));

        jPanel2.setBackground(new java.awt.Color(51, 102, 255));
        jPanel2.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        jPanel2.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        txtTotBayar.setEditable(false);
        txtTotBayar.setFont(new java.awt.Font("Dialog", 1, 12));
        txtTotBayar.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtTotBayar.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 1, true));
        txtTotBayar.setDisabledTextColor(new java.awt.Color(153, 153, 153));
        txtTotBayar.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                txtTotBayarFocusLost(evt);
            }
        });
        txtTotBayar.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txtTotBayarKeyPressed(evt);
            }
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtTotBayarKeyReleased(evt);
            }
        });
        jPanel2.add(txtTotBayar, new org.netbeans.lib.awtextra.AbsoluteConstraints(819, 10, 130, 24));

        txtTotDiskon.setEditable(false);
        txtTotDiskon.setFont(new java.awt.Font("Dialog", 1, 12));
        txtTotDiskon.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtTotDiskon.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 1, true));
        txtTotDiskon.setDisabledTextColor(new java.awt.Color(153, 153, 153));
        txtTotDiskon.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                txtTotDiskonFocusLost(evt);
            }
        });
        txtTotDiskon.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txtTotDiskonKeyPressed(evt);
            }
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtTotDiskonKeyReleased(evt);
            }
        });
        jPanel2.add(txtTotDiskon, new org.netbeans.lib.awtextra.AbsoluteConstraints(819, 39, 130, 24));

        jLabel8.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel8.setText("Diskon");
        jPanel2.add(jLabel8, new org.netbeans.lib.awtextra.AbsoluteConstraints(673, 41, 140, 20));

        jLabel20.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel20.setText("Denda");
        jPanel2.add(jLabel20, new org.netbeans.lib.awtextra.AbsoluteConstraints(724, 70, 90, -1));

        lblGrandTotal.setFont(new java.awt.Font("Tahoma", 0, 48));
        lblGrandTotal.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lblGrandTotal.setText("0");
        lblGrandTotal.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        jPanel2.add(lblGrandTotal, new org.netbeans.lib.awtextra.AbsoluteConstraints(21, 19, 320, 60));

        jLabel17.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel17.setText("Jumlah Bayar");
        jPanel2.add(jLabel17, new org.netbeans.lib.awtextra.AbsoluteConstraints(673, 13, 140, 20));

        txtTotDenda.setEditable(false);
        txtTotDenda.setFont(new java.awt.Font("Dialog", 1, 12));
        txtTotDenda.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtTotDenda.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 1, true));
        txtTotDenda.setDisabledTextColor(new java.awt.Color(153, 153, 153));
        txtTotDenda.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                txtTotDendaFocusLost(evt);
            }
        });
        txtTotDenda.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txtTotDendaKeyPressed(evt);
            }
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtTotDendaKeyReleased(evt);
            }
        });
        jPanel2.add(txtTotDenda, new org.netbeans.lib.awtextra.AbsoluteConstraints(820, 67, 130, 24));

        txtTotTerutang.setEditable(false);
        txtTotTerutang.setFont(new java.awt.Font("Dialog", 1, 12));
        txtTotTerutang.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtTotTerutang.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 1, true));
        txtTotTerutang.setDisabledTextColor(new java.awt.Color(153, 153, 153));
        txtTotTerutang.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                txtTotTerutangFocusLost(evt);
            }
        });
        txtTotTerutang.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txtTotTerutangKeyPressed(evt);
            }
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtTotTerutangKeyReleased(evt);
            }
        });
        jPanel2.add(txtTotTerutang, new org.netbeans.lib.awtextra.AbsoluteConstraints(539, 40, 130, 24));

        txtTotHutang.setEditable(false);
        txtTotHutang.setFont(new java.awt.Font("Dialog", 1, 12));
        txtTotHutang.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtTotHutang.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 1, true));
        txtTotHutang.setDisabledTextColor(new java.awt.Color(153, 153, 153));
        txtTotHutang.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                txtTotHutangFocusLost(evt);
            }
        });
        txtTotHutang.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txtTotHutangKeyPressed(evt);
            }
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtTotHutangKeyReleased(evt);
            }
        });
        jPanel2.add(txtTotHutang, new org.netbeans.lib.awtextra.AbsoluteConstraints(539, 11, 130, 24));

        jLabel18.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel18.setText("Jumlah Hutang");
        jPanel2.add(jLabel18, new org.netbeans.lib.awtextra.AbsoluteConstraints(393, 14, 140, 20));

        jLabel16.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel16.setText("Terutang");
        jPanel2.add(jLabel16, new org.netbeans.lib.awtextra.AbsoluteConstraints(393, 42, 140, 20));

        getContentPane().add(jPanel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(6, 435, 960, 100));

        btnNew.setIcon(new javax.swing.ImageIcon(getClass().getResource("/image/Icon/New.png"))); // NOI18N
        btnNew.setToolTipText("Baru & Bersihkan");
        btnNew.setMaximumSize(new java.awt.Dimension(40, 40));
        btnNew.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnNewActionPerformed(evt);
            }
        });
        getContentPane().add(btnNew, new org.netbeans.lib.awtextra.AbsoluteConstraints(4, 5, 50, 50));

        btnSave.setIcon(new javax.swing.ImageIcon(getClass().getResource("/image/Icon/Save.png"))); // NOI18N
        btnSave.setToolTipText("Simpan");
        btnSave.setMaximumSize(new java.awt.Dimension(40, 40));
        btnSave.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSaveActionPerformed(evt);
            }
        });
        getContentPane().add(btnSave, new org.netbeans.lib.awtextra.AbsoluteConstraints(55, 6, 50, 50));

        btnClose.setIcon(new javax.swing.ImageIcon(getClass().getResource("/image/Icon/Exit.png"))); // NOI18N
        btnClose.setToolTipText("New     (F12)");
        btnClose.setMaximumSize(new java.awt.Dimension(40, 40));
        btnClose.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCloseActionPerformed(evt);
            }
        });
        getContentPane().add(btnClose, new org.netbeans.lib.awtextra.AbsoluteConstraints(112, 7, 50, 50));

        java.awt.Dimension screenSize = java.awt.Toolkit.getDefaultToolkit().getScreenSize();
        setBounds((screenSize.width-980)/2, (screenSize.height-576)/2, 980, 576);
    }// </editor-fold>//GEN-END:initComponents

   
    private void txtJenisBayarFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtJenisBayarFocusLost
// TODO add your handling code here:
}//GEN-LAST:event_txtJenisBayarFocusLost

    private void txtSupplierFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtSupplierFocusLost
        udfLoadSupplier();
    }//GEN-LAST:event_txtSupplierFocusLost

    private void txtSupplierKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtSupplierKeyPressed
// TODO add your handling code here:
        if(evt.getKeyCode()==KeyEvent.VK_ENTER){
            udfLoadSupplier();
        }
    }//GEN-LAST:event_txtSupplierKeyPressed

    private void txtSupplierKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtSupplierKeyReleased
// TODO add your handling code here:
        try {
            String sCari = txtSupplier.getText();
            switch(evt.getKeyCode()) {
                
                case java.awt.event.KeyEvent.VK_ENTER : {
                    if (lst.isVisible()){
                        Object[] obj = lst.getOResult();
                        if (obj.length > 0) {
                            txtSupplier.setText(obj[0].toString());
                            lblSupplier.setText(obj[1].toString());
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
                    txtSupplier.setText("");
                    lblSupplier.setText("");
                    txtSupplier.requestFocus();
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
                        //String sQry="select kode_supplier,nama_supplier from phar_supplier where upper(kode_supplier||nama_supplier) like upper('%"+sCari+"%') order by 1";
                        String sQry="select kode_supplier, coalesce(nama_supplier,'') as nama, " +
                                    "coalesce(alamat,'')||' - '||coalesce(nama_kota,'')   as alamat " +
                                    "from phar_supplier " +
                                    "left join kota k using(kode_kota) " +
                                    "where (kode_supplier||coalesce(nama_supplier,'')||coalesce(alamat,'')) iLike '%"+sCari+"%'";
                        
                        //System.out.println(sQry);
                        lst.setSQuery(sQry);
                        
                        lst.setBounds(this.getX()+this.jPanel1.getX() + this.txtSupplier.getX()+5, this.getY()+this.jPanel1.getY()+this.txtSupplier.getY() + txtSupplier.getHeight()+76, 350,150);
                        lst.setFocusableWindowState(false);
                        lst.setTxtCari(txtSupplier);
                        lst.setLblDes(new javax.swing.JLabel[]{lblSupplier});
                        lst.setColWidth(0, txtSupplier.getWidth()-1);
                        lst.setColWidth(1, 250);
                        if(lst.getIRowCount()>0){
                            lst.setVisible(true);
                            txtSupplier.requestFocus();
                        } else{
                            lst.setVisible(false);
                            txtSupplier.setText("");
                            lblSupplier.setText("");
                            txtSupplier.requestFocus();
                        }
                    }
                    break;
                }
            }
        } catch (SQLException se) {System.out.println(se.getMessage());}
    }//GEN-LAST:event_txtSupplierKeyReleased

    private void txtPenerimaFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtPenerimaFocusLost
// TODO add your handling code here:
    }//GEN-LAST:event_txtPenerimaFocusLost

    private void txtPenerimaKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtPenerimaKeyPressed
// TODO add your handling code here:
    }//GEN-LAST:event_txtPenerimaKeyPressed

    private void txtPenerimaKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtPenerimaKeyReleased
// TODO add your handling code here:
    }//GEN-LAST:event_txtPenerimaKeyReleased

    private void tblHutangFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_tblHutangFocusGained

    }//GEN-LAST:event_tblHutangFocusGained

    private void jFBayarFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jFBayarFocusGained
        // TODO add your handling code here:
}//GEN-LAST:event_jFBayarFocusGained

    private void jFBayarFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jFBayarFocusLost
        if(!jFBayar.getText().trim().equalsIgnoreCase("/  /")) {
            if(!validateDate(jFBayar.getText(),true,"dd/MM/yyyy")){
                JOptionPane.showMessageDialog(null, "Silakan masukkan tanggal dengan format 'dd/MM/yyyy' !");
                jFBayar.setText("");
                jFBayar.requestFocus();
                return;
                
            }
        }else{
            jFBayar.setText(sTglSkg);
        }
}//GEN-LAST:event_jFBayarFocusLost

    private void txtTotDiskonFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtTotDiskonFocusLost
        // TODO add your handling code here:
}//GEN-LAST:event_txtTotDiskonFocusLost

    private void txtTotDiskonKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtTotDiskonKeyPressed
        // TODO add your handling code here:
}//GEN-LAST:event_txtTotDiskonKeyPressed

    private void txtTotDiskonKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtTotDiskonKeyReleased
        // TODO add your handling code here:
}//GEN-LAST:event_txtTotDiskonKeyReleased

    private void txtTotBayarFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtTotBayarFocusLost
        // TODO add your handling code here:
}//GEN-LAST:event_txtTotBayarFocusLost

    private void txtTotBayarKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtTotBayarKeyPressed
        // TODO add your handling code here:
}//GEN-LAST:event_txtTotBayarKeyPressed

    private void txtTotBayarKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtTotBayarKeyReleased
        // TODO add your handling code here:
}//GEN-LAST:event_txtTotBayarKeyReleased

    private void txtTotDendaFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtTotDendaFocusLost
        // TODO add your handling code here:
}//GEN-LAST:event_txtTotDendaFocusLost

    private void txtTotDendaKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtTotDendaKeyPressed
        // TODO add your handling code here:
}//GEN-LAST:event_txtTotDendaKeyPressed

    private void txtTotDendaKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtTotDendaKeyReleased
        // TODO add your handling code here:
}//GEN-LAST:event_txtTotDendaKeyReleased

    private void btnNewActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnNewActionPerformed
        udfNew();
    }//GEN-LAST:event_btnNewActionPerformed

    private void btnSaveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSaveActionPerformed
        udfSave();
    }//GEN-LAST:event_btnSaveActionPerformed

    private void btnCloseActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCloseActionPerformed
        udfCancel();
        //this.dispose();
    }//GEN-LAST:event_btnCloseActionPerformed

    private void formInternalFrameOpened(javax.swing.event.InternalFrameEvent evt) {//GEN-FIRST:event_formInternalFrameOpened
        lst = new ListRsbm();
	lst.setVisible(false);
	lst.setSize(500,200);
	lst.con = conn;
        
        
        TableLook();
        
        MaskFormatter fmttgl = null;
            try {
                fmttgl = new MaskFormatter("##/##/####");
            } catch (java.text.ParseException e) {
            }

            try {
                Statement stTgl = conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY);
                ResultSet rtgl = stTgl.executeQuery("select cast(current_date as varchar) as tanggal");
                if (rtgl.next()) {
                    fdateformat = new SimpleDateFormat("yyyy/MM/dd");
                    //dateFormatddmmyy = fdateformat.format(rtgl.getDate(1));
                    fdateformat = new SimpleDateFormat("dd/MM/yyyy");
                    sTglSkg = fdateformat.format(rtgl.getDate(1));
                }
                stTgl.close();
                rtgl.close();
            } catch (SQLException se) {
            }
            SelectionListener listener = new SelectionListener(tblHutang);
            tblHutang.getSelectionModel().addListSelectionListener(listener);
            tblHutang.getColumnModel().getSelectionModel().addListSelectionListener(listener);
            
            jFDate1 = new JFormattedTextField(fmttgl);
            jFBayar.setFormatterFactory(jFDate1.getFormatterFactory());
            jFBayar.setText(sTglSkg);
            
            jFTglCek.setFormatterFactory(jFDate1.getFormatterFactory());
            jFTglCek.setText(sTglSkg);
            
            cEditor=new MyTableCellEditor();
        
            tblHutang.getColumnModel().getColumn(6).setCellEditor(cEditor);
            tblHutang.getColumnModel().getColumn(7).setCellEditor(cEditor);
            tblHutang.getColumnModel().getColumn(8).setCellEditor(cEditor);
            isNew=true;
            udfNew();
    }//GEN-LAST:event_formInternalFrameOpened

    private void formInternalFrameClosed(javax.swing.event.InternalFrameEvent evt) {//GEN-FIRST:event_formInternalFrameClosed
        
    }//GEN-LAST:event_formInternalFrameClosed

    private void txtJenisBayarKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtJenisBayarKeyReleased
        try {
            String sCari = txtJenisBayar.getText();
            switch(evt.getKeyCode()) {
                
                case java.awt.event.KeyEvent.VK_ENTER : {
                    if (lst.isVisible()){
                        Object[] obj = lst.getOResult();
                        if (obj.length > 0) {
                            txtJenisBayar.setText(obj[0].toString());
                            lblJenisBayar.setText(obj[1].toString());
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
                    txtJenisBayar.setText("");
                    lblJenisBayar.setText("");
                    txtJenisBayar.requestFocus();
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
                        //String sQry="select kode_supplier,nama_supplier from phar_supplier where upper(kode_supplier||nama_supplier) like upper('%"+sCari+"%') order by 1";
                        String sQry="select kode_jenis, coalesce(jenis_pembayaran,'') as jenis_pembayaran " +
                                    "from jenis_pembayaran " +
                                    "where (kode_jenis||coalesce(jenis_pembayaran,'')) iLike '%"+sCari+"%'";
                        
                        //System.out.println(sQry);
                        lst.setSQuery(sQry);
                        
                        lst.setBounds(this.getX()+this.jPanel1.getX() + this.txtJenisBayar.getX()+5, 
                                this.getY()+this.jPanel1.getY()+this.txtJenisBayar.getY() + txtJenisBayar.getHeight()+76, 350,150);
                        lst.setFocusableWindowState(false);
                        lst.setTxtCari(txtJenisBayar);
                        lst.setLblDes(new javax.swing.JLabel[]{lblJenisBayar});
                        lst.setColWidth(0, txtJenisBayar.getWidth()-1);
                        lst.setColWidth(1, 250);
                        if(lst.getIRowCount()>0){
                            lst.setVisible(true);
                            txtJenisBayar.requestFocus();
                        } else{
                            lst.setVisible(false);
                            txtJenisBayar.setText("");
                            lblJenisBayar.setText("");
                            txtJenisBayar.requestFocus();
                        }
                    }
                    break;
                }
            }
        } catch (SQLException se) {System.out.println(se.getMessage());}
    }//GEN-LAST:event_txtJenisBayarKeyReleased

    private void txtTotHutangFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtTotHutangFocusLost
        // TODO add your handling code here:
}//GEN-LAST:event_txtTotHutangFocusLost

    private void txtTotHutangKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtTotHutangKeyPressed
        // TODO add your handling code here:
}//GEN-LAST:event_txtTotHutangKeyPressed

    private void txtTotHutangKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtTotHutangKeyReleased
        // TODO add your handling code here:
}//GEN-LAST:event_txtTotHutangKeyReleased

    private void txtTotTerutangFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtTotTerutangFocusLost
        // TODO add your handling code here:
}//GEN-LAST:event_txtTotTerutangFocusLost

    private void txtTotTerutangKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtTotTerutangKeyPressed
        // TODO add your handling code here:
}//GEN-LAST:event_txtTotTerutangKeyPressed

    private void txtTotTerutangKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtTotTerutangKeyReleased
        // TODO add your handling code here:
}//GEN-LAST:event_txtTotTerutangKeyReleased

    private void jFTglCekFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jFTglCekFocusGained
        // TODO add your handling code here:
}//GEN-LAST:event_jFTglCekFocusGained

    private void jFTglCekFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jFTglCekFocusLost
        if(!jFTglCek.getText().trim().equalsIgnoreCase("/  /")) {
            if(!validateDate(jFTglCek.getText(),true,"dd/MM/yyyy")){
                JOptionPane.showMessageDialog(null, "Silakan masukkan tanggal dengan format 'dd/MM/yyyy' !");
                jFTglCek.setText("");
                jFTglCek.requestFocus();
                return;
                
            }
        }else{
            jFTglCek.setText(sTglSkg);
        }
}//GEN-LAST:event_jFTglCekFocusLost

    private void tblHutangKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_tblHutangKeyTyped
        cEditor.setComponentFocus();
    }//GEN-LAST:event_tblHutangKeyTyped
    
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
    
    private void TableLook(){
        
        myModel=(DefaultTableModel)tblHutang.getModel();
        myModel.setNumRows(0);
        tblHutang.setModel(myModel);
        
        tblHutang.getColumnModel().getColumn(0).setPreferredWidth(25);  //No
        tblHutang.getColumnModel().getColumn(1).setPreferredWidth(75);  //No Faktur
        tblHutang.getColumnModel().getColumn(2).setPreferredWidth(75); //Tanggal
        tblHutang.getColumnModel().getColumn(3).setPreferredWidth(75);  //Jatuh Tempo
        tblHutang.getColumnModel().getColumn(4).setPreferredWidth(80);  //jumlah
        tblHutang.getColumnModel().getColumn(5).setPreferredWidth(80);  //Terutang
        tblHutang.getColumnModel().getColumn(6).setPreferredWidth(80);  //Bayar
        tblHutang.getColumnModel().getColumn(7).setPreferredWidth(80);  //Diskon
        tblHutang.getColumnModel().getColumn(8).setPreferredWidth(80);  //Denda
        
        tblHutang.setRowHeight(25);
        for (int i=0;i<tblHutang.getColumnCount();i++){
            tblHutang.getColumnModel().getColumn(i).setCellRenderer(new MyRowRenderer());
        }
        
        //----------------------
        tblHutang.setShowVerticalLines(true);
        tblHutang.getTableHeader().setPreferredSize(new Dimension(0,30));
        tblHutang.getTableHeader().setBackground(new Color(255,204,204));
        
        tblHutang.setRowHeight(25);
        //tblHutang.getTableHeader().setForeground(new Color(255,255,0));
        //--------------------
//        if (tblHutang.getRowCount() > 0) {
//            tblHutang.changeSelection(0, 0,false,false);                
//        } 
        tblHutang.setAutoscrolls(true);
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
            
            if(value instanceof Float || value instanceof Double){
                setHorizontalAlignment(tx.RIGHT);
                value=intFormat.format(value);
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
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnClose;
    private javax.swing.JButton btnNew;
    private javax.swing.JButton btnSave;
    private javax.swing.JFormattedTextField jFBayar;
    private javax.swing.JFormattedTextField jFTglCek;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel18;
    private javax.swing.JLabel jLabel20;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JLabel lblGrandTotal;
    private javax.swing.JLabel lblHeader1;
    private javax.swing.JLabel lblHeader2;
    private javax.swing.JLabel lblJenisBayar;
    private javax.swing.JLabel lblSupplier;
    private javax.swing.JTable tblHutang;
    private javax.swing.JTextField txtCatatan;
    private javax.swing.JTextField txtJenisBayar;
    private javax.swing.JTextField txtNoBukti;
    private javax.swing.JTextField txtNoCek;
    private javax.swing.JTextField txtPenerima;
    private javax.swing.JTextField txtSupplier;
    private javax.swing.JTextField txtTotBayar;
    private javax.swing.JTextField txtTotDenda;
    private javax.swing.JTextField txtTotDiskon;
    private javax.swing.JTextField txtTotHutang;
    private javax.swing.JTextField txtTotTerutang;
    // End of variables declaration//GEN-END:variables

    private void udfCancel() {
        isNew=isNew? false: true;
        if(isNew)
            this.dispose();
        else
            setUpBtn();
    }
    // End of variables declaration

    private void udfNew() {
        isNew=true;
        setUpBtn();
        udfBlank();
                
    }
    
    Color g1 = new Color(153,255,255);
    Color g2 = new Color(255,255,255); 
    
    Color fHitam = new Color(0,0,0);
    Color fPutih = new Color(255,255,255); 
    
    Color crtHitam =new java.awt.Color(0, 0, 0);
    Color crtPutih = new java.awt.Color(255, 255, 255); 
}
