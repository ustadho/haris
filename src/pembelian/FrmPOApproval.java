/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * FrmPRApproval.java
 *
 * Created on Jul 29, 2010, 11:51:31 AM
 */

package pembelian;

import main.MainForm;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Cursor;
import java.awt.FocusTraversalPolicy;
import java.awt.KeyboardFocusManager;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.ByteArrayInputStream;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JCheckBox;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumnModel;
import javax.swing.text.MaskFormatter;
import main.GeneralFunction;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.util.JRLoader;
import net.sf.jasperreports.view.JasperViewer;
import org.jdesktop.swingx.JXTable;
import tableRender.ColumnGroup;
import tableRender.GroupableTableHeader;

/**
 *
 * @author ustadho
 */
public class FrmPOApproval extends javax.swing.JInternalFrame {
    private Connection conn;
    private boolean bAcc1= false, bAcc2=false, bAcc3=false;
    private String  sAcc1= "", sAcc2="" , sAcc3="";
    private GeneralFunction fn;
    boolean cumaSingDurungApprove=true, cumaSingOutstanding=false;
    private JFormattedTextField jFDate1;
    MyKeyListener kListener=new MyKeyListener();
    private Timer timer;
    private int i;
    private Long waktuRefresh;

    /** Creates new form FrmPRApproval */
    public FrmPOApproval(Connection con, boolean b, boolean stOpen) {
        initComponents();
        this.conn=con;
        this.cumaSingDurungApprove=b;
        this.cumaSingOutstanding=stOpen;
        fn=new GeneralFunction();
        
        fn.addKeyListenerInContainer(jPanel1, kListener, txtFocusListener);
        tblHeader.addKeyListener(kListener);
        tblDetail.addKeyListener(kListener);

        MaskFormatter fmttgl = null;
        try {
            fmttgl = new MaskFormatter("##/##/####");
        } catch (java.text.ParseException e) {}

        jFDate1 = new JFormattedTextField(fmttgl);
        jFTgl.setFormatterFactory(jFDate1.getFormatterFactory());
        jFTglAkhir.setFormatterFactory(jFDate1.getFormatterFactory());

//        fmttgl.install(jFTgl);
//        fmttgl.install(jFTglAkhir);

        tblHeader.getSelectionModel().addListSelectionListener(new ListSelectionListener() {

            public void valueChanged(ListSelectionEvent e) {
                btnPreview.setEnabled(tblHeader.getSelectedRow()>=0); 
                        //&& (Boolean)tblHeader.getValueAt(tblHeader.getSelectedRow(), tblHeader.getColumnModel().getColumnIndex(sAcc2)));
                if(conn!=null && tblHeader.getSelectedRow()<0) return;
                udfLoadPODetail();
                
            }
        });


        tblDetail.getModel().addTableModelListener(new TableModelListener() {
            public void tableChanged(TableModelEvent e) {
                TableColumnModel col=tblDetail.getColumnModel();
                if(e.getColumn()==col.getColumnIndex("Qty")||e.getColumn()==col.getColumnIndex("Disc %")){
                    int iRow=tblDetail.getSelectedRow();
                    double jmlKecil=fn.udfGetDouble(tblDetail.getValueAt(iRow, col.getColumnIndex("Qty")))*
                            fn.udfGetDouble(tblDetail.getValueAt(iRow, col.getColumnIndex("Conv")));
                    double extPrice=fn.udfGetDouble(tblDetail.getValueAt(iRow, col.getColumnIndex("Qty")))*
                            fn.udfGetDouble(tblDetail.getValueAt(iRow, col.getColumnIndex("Unit Price")));
                    extPrice=extPrice-(extPrice/100)* fn.udfGetDouble(tblDetail.getValueAt(iRow, col.getColumnIndex("Disc %")));

                    ((DefaultTableModel)tblDetail.getModel()).setValueAt(Math.floor(extPrice),
                            iRow, col.getColumnIndex("Ext. Price"));
                    ((DefaultTableModel)tblDetail.getModel()).setValueAt(jmlKecil, iRow, col.getColumnIndex("JmlKecil"));

                }
                if(tblDetail.getRowCount()>0){
                    double totLine=0, totVat=0;

                    for(int i=0; i< tblDetail.getRowCount(); i++){
                        if(e.getType()==TableModelEvent.DELETE)
                            ((DefaultTableModel)tblDetail.getModel()).setValueAt(i+1, i, col.getColumnIndex("No."));

                        totLine+=fn.udfGetDouble(tblDetail.getValueAt(i, col.getColumnIndex("Ext. Price")));
                        totVat+=fn.udfGetDouble(tblDetail.getValueAt(i, col.getColumnIndex("Ext. Price")))/100*fn.udfGetDouble(tblDetail.getValueAt(i, col.getColumnIndex("Vat")));
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
        udfInitForm();
        tblDetail.setRowHeight(25);
        tblDetail.getColumn("Conv").setMinWidth(0); tblDetail.getColumn("Conv").setMaxWidth(0); tblDetail.getColumn("Conv").setPreferredWidth(0);
        tblDetail.getColumn("UomKecil").setMinWidth(0); tblDetail.getColumn("UomKecil").setMaxWidth(0); tblDetail.getColumn("UomKecil").setPreferredWidth(0);
        tblDetail.getColumn("JmlKecil").setMinWidth(0); tblDetail.getColumn("JmlKecil").setMaxWidth(0); tblDetail.getColumn("JmlKecil").setPreferredWidth(0);
        tblDetail.getColumn("SisaPR").setMinWidth(0); tblDetail.getColumn("SisaPR").setMaxWidth(0); tblDetail.getColumn("SisaPR").setPreferredWidth(0);
    }

    private void udfLoadPODetail(){
        int iRow=tblHeader.getSelectedRow();
        String sNoPo=tblHeader.getValueAt(iRow, tblHeader.getColumnModel().getColumnIndex("PO No.")).toString();

        String s ="select * from fn_phar_po_detail_item('"+sNoPo+"') as (consigment boolean, cito boolean, top integer, curr varchar, "
                + "kurs numeric, disc_po double precision, tanggal text, jam text, kode_supplier varchar, nama_supplier varchar, "
                + "alamat varchar, nama_kota varchar, telp_supp varchar, shipping varchar, remark varchar, buyer varchar, no_po varchar, "
                + "kode_barang varchar, nama_barang varchar, uom_alt varchar, qty numeric, price numeric, disc double precision, "
                + "vat real, ext_price double precision, jml_kecil double precision, uom_kecil varchar, konv real, no_pr varchar, "
                + "urut integer, ppn double precision, gr_id text, tgl_gr text, qty_gr numeric, sisa_po numeric)";

        //System.out.println(s);
        try{
            ((DefaultTableModel)tblDetail.getModel()).setNumRows(0);

            ResultSet rs=conn.createStatement().executeQuery(s);
            while(rs.next()){
                ((DefaultTableModel)tblDetail.getModel()).addRow(new Object[]{
                    ((DefaultTableModel)tblDetail.getModel()).getRowCount()+1,
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
                    rs.getDouble("sisa_po"),
                    rs.getString("gr_id"),
                    rs.getString("tgl_gr"),
                    rs.getDouble("qty_gr")
                });
            }

            if(tblDetail.getRowCount()>0)
                tblDetail.setRowSelectionInterval(0, 0);

            tblDetail.setModel((DefaultTableModel)fn.autoResizeColWidth(tblDetail, (DefaultTableModel)tblDetail.getModel()).getModel());
        }catch(SQLException se){
            JOptionPane.showMessageDialog(this, se.getMessage());
        }

    }

    public void setCumaSingDurungApprove(boolean b){
        cumaSingDurungApprove=b;
        btnSave.setEnabled(b);
        
    }

    public void setCumaSingOutstanding(boolean b){
        cumaSingOutstanding=b;
        btnPreview.setVisible(!b);
        btnSave.setVisible(!b);
        jLabel3.setVisible(!b);
    }

    public void setConn(Connection con){
        this.conn=con;
    }

    private void udfInitForm(){
        fn=new GeneralFunction(conn);

        try {
            ResultSet rs = conn.createStatement().executeQuery("select '01/'||to_char(current_date, 'MM/yyyy') as tgl1,  to_char(current_date, 'dd/MM/yyyy') as tgl2 " +
                    "");
            
            if(rs.next()){
                jFTgl.setText(cumaSingDurungApprove? rs.getString(2): rs.getString(1));
                jFTgl.setValue(cumaSingDurungApprove? rs.getString(2): rs.getString(1));
                jFTglAkhir.setText(rs.getString(2));
                jFTglAkhir.setValue(rs.getString(2));
            }
            
            rs.close();
            rs=conn.createStatement().executeQuery("select * from m_user_acc where user_name='"+ MainForm.sUserName+"' " +
                    "and acc_modul='PO'");
            while(rs.next()){
                if(rs.getInt("level")==1) bAcc1=true;
                if(rs.getInt("level")==2) bAcc2=true;
                //if(rs.getInt("level")==3) bAcc3=true;
            }
            rs.close();
            rs=conn.createStatement().executeQuery("select distinct acc.level, coalesce(singkatan,'') " +
                    "from m_user_acc acc  " +
                    "inner join m_user d on d.username=acc.user_name " +
                    "inner join m_jabatan jb on jb.kode_jabatan=d.kode_jabatan " +
                    "where acc_modul='PO'  " +
                    "and coalesce(acc.priority,0)=0 order by acc.level");


            while(rs.next()){
                if(rs.getInt("level")==1) sAcc1=rs.getString(2);
                if(rs.getInt("level")==2) sAcc2=rs.getString(2);
                //if(rs.getInt("level")==3) sAcc3=rs.getString(2);
            }
            //bAcc3=(cumaSingDurungApprove&&bAcc3);
            bAcc2=(cumaSingDurungApprove&&bAcc2);
            bAcc1=(cumaSingDurungApprove&&bAcc1);

            tblHeader.setModel(new javax.swing.table.DefaultTableModel(
                new Object [][] {},
                new String [] {"PO No.", "Tanggal", "SupplierID", "Nama Supplier", "Consigment", "Remark", 
                                sAcc2, sAcc1, "Cito", "Last Print"}
            ) {
                Class[] types = new Class [] {
                    java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class,
                    java.lang.Boolean.class, java.lang.Object.class, java.lang.Boolean.class, java.lang.Boolean.class,
                    java.lang.Object.class, java.lang.Object.class
                };
                boolean[] canEdit = new boolean [] {
                    false, false, false, false, false, false, bAcc2, bAcc1, false, false
                };
                
                public Class getColumnClass(int columnIndex) {
                    return types [columnIndex];
                }

                public boolean isCellEditable(int rowIndex, int columnIndex) {
                    return canEdit [columnIndex];
                }
            });


            tblHeader.getTableHeader().setReorderingAllowed(false);
            
            UIManager.put(GroupableTableHeader.uiClassID, "tableRender.GroupableTableHeaderUI");
            GroupableTableHeader header = new GroupableTableHeader(tblHeader.getColumnModel());
            TableColumnModel columns = tblHeader.getColumnModel();
            ColumnGroup acc = new ColumnGroup("Approve By");
            acc.add(columns.getColumn(6));
            acc.add(columns.getColumn(7));
            //acc.add(columns.getColumn(8));
            
            header.addGroup(acc);
            
            tblHeader.setTableHeader(header);

//            tblHeader.getColumn("Release Date").setCellRenderer(new MyRowRenderer());
//            tblHeader.getColumn("Need Date").setCellRenderer(new MyRowRenderer());
            for(int i=0; i< tblHeader.getColumnCount(); i++){
                tblHeader.getColumnModel().getColumn(i).setCellRenderer(new MyRowRenderer());
            }

            tblHeader.setRowHeight(22);
            tblDetail.setRowHeight(22);
            tblHeader.getColumn("PO No.").setPreferredWidth(100);
            tblHeader.getColumn("Nama Supplier").setPreferredWidth(230);
            tblHeader.getColumn("Tanggal").setPreferredWidth(120);
            tblHeader.getColumn(sAcc1).setPreferredWidth(90);
            tblHeader.getColumn(sAcc2).setPreferredWidth(90);
            //tblHeader.getColumn(sAcc3).setPreferredWidth(90);
            tblHeader.getColumn("Cito").setPreferredWidth(0);tblHeader.getColumn("Cito").setMinWidth(0);tblHeader.getColumn("Cito").setMaxWidth(0);
            //udfLoadPO();
            
        } catch (SQLException ex) {
            Logger.getLogger(FrmPOApproval.class.getName()).log(Level.SEVERE, null, ex);
        }

        jLabel16.setText(getTitle());
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                tblHeader.requestFocus();
            }
        });
        if(!cumaSingDurungApprove) {
            jSlider1.setVisible(false); jLDetik.setVisible(false);
            jLabel11.setVisible(false); jLTest.setVisible(false); jBSet.setVisible(false);
            udfLoadPO();
            return;
        }

        if ((jSlider1.getValue()/60)==0.0){
            jLDetik.setText(String.valueOf(jSlider1.getValue()%60)+" Detik");
        }else{
            jLDetik.setText(String.valueOf(jSlider1.getValue()/60)+" Menit "+String.valueOf(jSlider1.getValue()%60)+" Detik");
        }
        
        jLDetik.setHorizontalAlignment(JLabel.CENTER);
        jLTest.setHorizontalAlignment(JLabel.RIGHT);
        waktuRefresh=Long.valueOf(String.valueOf(jSlider1.getValue()))*1000;
        timer = new Timer();
        timer.schedule(new DoTick(), 0, waktuRefresh);
        
        


    }

    class DoTick extends TimerTask {
        public void run() {
            jLTest.setText(String.valueOf(i));
            i++;

            udfLoadPO();
        }
    }

    private void udfLoadPO(){
        try{
            SimpleDateFormat dmy = new SimpleDateFormat("dd/MM/yyyy");
            SimpleDateFormat ymd = new SimpleDateFormat("yyyy-MM-dd");
            String sQry="select distinct po.no_po, po.tanggal, coalesce(po.kode_supplier,'') as kode_supp, " +
                    "coalesce(sup.nama_supplier,'') as nama_supplier, coalesce(po.consigment, false) as consigment, " +
                    "coalesce(po.remark,'') as remark, trim(coalesce(po.acc_level_3, ''))<>'' as acc_level3, " +
                    "trim(coalesce(po.acc_level_2, ''))<>'' as acc_level2, trim(coalesce(po.acc_level_1, ''))<>'' as acc_level1, " +
                    "coalesce(po.cito, false) as cito, last_print_time " +
                    "from phar_po po " +
                    "inner join phar_po_detail pod on pod.no_po=po.no_po " +
                    "inner join barang i on i.item_code=pod.kode_barang " +
                    "left join phar_supplier sup on sup.kode_supplier=po.kode_supplier " +
                    "where po.flag_Trx='T' " +
                    "and coalesce(i.item_name,'')||coalesce(i.nama_paten,'')||pod.kode_barang ilike '%"+txtItem.getText()+"%' " +
                    "and coalesce(sup.nama_supplier,'')||po.kode_supplier||po.no_po ilike '%"+txtSupplier.getText()+"%' " +
                    (cumaSingDurungApprove? "and(trim(coalesce(po.acc_level_2, ''))='' or trim(coalesce(po.acc_level_1, ''))='') ":
                        (cumaSingOutstanding? "and coalesce(po.closed, false)=false ": "")) +
                    "and to_char(po.tanggal, 'yyyy-MM-dd')>='" + ymd.format(dmy.parse(jFTgl.getText())) + "' " +
                    "and to_char(po.tanggal, 'yyyy-MM-dd')<='" + ymd.format(dmy.parse(jFTglAkhir.getText())) + "' " +
                    " order by po.tanggal desc";

            System.out.println(sQry);
            
            setCursor(new Cursor(Cursor.WAIT_CURSOR));
            try {
                ResultSet rs = conn.createStatement().executeQuery(sQry);
                ((DefaultTableModel) tblHeader.getModel()).setNumRows(0);
                while (rs.next()) {
                    ((DefaultTableModel) tblHeader.getModel()).addRow(new Object[]{
                        rs.getString("no_po"),
                        rs.getTimestamp("tanggal"),
                        rs.getString("kode_supp"),
                        rs.getString("nama_supplier"),
                        rs.getBoolean("consigment"),
                        rs.getString("remark"),//rs.getBoolean("acc_level3"),
                        rs.getBoolean("acc_level2"),
                        rs.getBoolean("acc_level1"),
                        rs.getBoolean("cito"),
                        rs.getTimestamp("last_print_time")
                    });
                }
                if(tblHeader.getRowCount()>0)
                    tblHeader.setRowSelectionInterval(0, 0);

                setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
                //tblHeader.setModel((DefaultTableModel) fn.autoResizeColWidth(tblHeader, (DefaultTableModel)tblHeader.getModel()).getModel());
            } catch (SQLException se) {
                setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
                JOptionPane.showMessageDialog(this, se.getMessage());
            }
        }catch(ParseException ex){
            Logger.getLogger(FrmPOApproval.class.getName()).log(Level.SEVERE, null,ex);
        }
    }

    SimpleDateFormat dmyFmt=new SimpleDateFormat("dd/MM/yyyy");
    SimpleDateFormat dmyFmt_hhmm=new SimpleDateFormat("dd/MM/yyyy HH:mm");

    private void udfSave() {
        String sUpdate="";
        String sUpd1="", sUpd2="", sUpd3="";

        for(int i=0; i<tblHeader.getRowCount(); i++){
            sUpd1=(bAcc1? "acc_level_1='"+ ((Boolean)tblHeader.getValueAt(i, tblHeader.getColumnModel().getColumnIndex(sAcc1))==true?MainForm.sUserName: "" )+"', acc_time1= now() ":"");
            sUpd2=(bAcc2? "acc_level_2='"+ ((Boolean)tblHeader.getValueAt(i, tblHeader.getColumnModel().getColumnIndex(sAcc2))==true?MainForm.sUserName: "" )+"', acc_time2= now() ":"");
            //sUpd3=(bAcc3? "acc_level_3='"+ ((Boolean)tblHeader.getValueAt(i, 6)==true?MainForm.sUserName: "" )+"', acc_time3= now() ":"");

            sUpd1=sUpd1.length()>0? "Update phar_po set "+sUpd1+" where no_po='"+tblHeader.getValueAt(i, 0).toString()+"'; " : "";
            sUpd2=sUpd2.length()>0? "Update phar_po set "+sUpd2+" where no_po='"+tblHeader.getValueAt(i, 0).toString()+"'; " : "";
            //sUpd3=sUpd3.length()>0? "Update phar_po set "+sUpd3+" where no_po='"+tblHeader.getValueAt(i, 0).toString()+"'; " : "";

            sUpdate+=sUpd1+sUpd2; //+sUpd3
        }

        System.out.println(sUpdate);

        try{
            conn.setAutoCommit(false);
            int i=conn.createStatement().executeUpdate(sUpdate);
            conn.setAutoCommit(true);
            if(i>0){
                JOptionPane.showMessageDialog(this, "Simpan PR approval Sukses!");
            }
        }catch(SQLException se){
            try {
                conn.rollback();
                JOptionPane.showMessageDialog(this, se.getMessage());
            } catch (SQLException ex) {
                Logger.getLogger(FrmPOApproval.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
    public class MyRowRenderer extends DefaultTableCellRenderer implements TableCellRenderer{
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
                boolean hasFocus, int row, int column) {
            setFont(table.getFont());
            if(isSelected){
                setBackground(table.getSelectionBackground());
                setForeground(table.getSelectionForeground());
            }else{
                if((Boolean)table.getValueAt(row, 8)==true){
                    setBackground(lblPRCito.getBackground());
                    setForeground(table.getForeground());
                }else if(table.getValueAt(row, tblHeader.getColumnModel().getColumnIndex("Last Print"))!=null){
                    setBackground(lblPRPrinted.getBackground());
                    setForeground(table.getForeground());
                } else{
                    setBackground(table.getBackground());
                    setForeground(table.getForeground());
                }
            }
            JCheckBox checkBox = new JCheckBox();
            if(value instanceof Date ){
                value=dmyFmt_hhmm.format(value);
            }if(value instanceof Timestamp ){
                value=dmyFmt_hhmm.format(value);
            }else if(value instanceof Double ||value instanceof Integer ||value instanceof Float  ){
                setHorizontalAlignment(SwingConstants.RIGHT);
                value=fn.dFmt.format(value);
            }else if (value instanceof Boolean) { // Boolean
                checkBox.setSelected(((Boolean) value).booleanValue());
                checkBox.setHorizontalAlignment(lblPRCito.CENTER);
                checkBox.setBackground(getBackground());
                checkBox.setForeground(getForeground());
                return checkBox;
            }
            

            setValue(value);
            return this;
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
        btnClose = new javax.swing.JButton();
        btnSave = new javax.swing.JButton();
        btnPreview = new javax.swing.JButton();
        jLabel16 = new javax.swing.JLabel();
        jFTgl = new javax.swing.JFormattedTextField();
        jLabel2 = new javax.swing.JLabel();
        jFTglAkhir = new javax.swing.JFormattedTextField();
        btnFilter1 = new javax.swing.JButton();
        jLabel8 = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        txtItem = new javax.swing.JTextField();
        jLabel10 = new javax.swing.JLabel();
        txtSupplier = new javax.swing.JTextField();
        jXTitledPanel1 = new org.jdesktop.swingx.JXTitledPanel();
        jScrollPane3 = new javax.swing.JScrollPane();
        tblDetail = new org.jdesktop.swingx.JXTable();
        jScrollPane1 = new javax.swing.JScrollPane();
        tblHeader = new javax.swing.JTable();
        lblPRCito = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
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
        jLabel3 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        txtRemark = new javax.swing.JTextField();
        lblPRPrinted = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        jLabel11 = new javax.swing.JLabel();
        jSlider1 = new javax.swing.JSlider();
        jLDetik = new javax.swing.JLabel();
        jLTest = new javax.swing.JLabel();
        jBSet = new javax.swing.JButton();

        setClosable(true);
        setIconifiable(true);
        setMaximizable(true);
        setResizable(true);
        setTitle("Purchase Order  Approval ");
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

        jPanel1.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        jPanel1.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        btnClose.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/Icon/buttons/exit-32.png"))); // NOI18N
        btnClose.setText("Close");
        btnClose.setToolTipText("New     (F12)");
        btnClose.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnClose.setMargin(new java.awt.Insets(2, 2, 2, 2));
        btnClose.setMaximumSize(new java.awt.Dimension(40, 40));
        btnClose.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnClose.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCloseActionPerformed(evt);
            }
        });
        jPanel1.add(btnClose, new org.netbeans.lib.awtextra.AbsoluteConstraints(750, 10, 50, 60));

        btnSave.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/Icon/buttons/Ok-32.png"))); // NOI18N
        btnSave.setText("Save");
        btnSave.setToolTipText("New     (F12)");
        btnSave.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnSave.setMargin(new java.awt.Insets(2, 2, 2, 2));
        btnSave.setMaximumSize(new java.awt.Dimension(40, 40));
        btnSave.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnSave.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSaveActionPerformed(evt);
            }
        });
        jPanel1.add(btnSave, new org.netbeans.lib.awtextra.AbsoluteConstraints(700, 10, 50, 60));

        btnPreview.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/Icon/buttons/print-32.png"))); // NOI18N
        btnPreview.setText("Print");
        btnPreview.setToolTipText("New     (F12)");
        btnPreview.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnPreview.setMargin(new java.awt.Insets(2, 2, 2, 2));
        btnPreview.setMaximumSize(new java.awt.Dimension(40, 40));
        btnPreview.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnPreview.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnPreviewActionPerformed(evt);
            }
        });
        jPanel1.add(btnPreview, new org.netbeans.lib.awtextra.AbsoluteConstraints(650, 10, 50, 60));

        jLabel16.setFont(new java.awt.Font("Tahoma", 0, 24)); // NOI18N
        jLabel16.setForeground(new java.awt.Color(0, 0, 153));
        jLabel16.setText(" Purchase Order Approval ");
        jPanel1.add(jLabel16, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 330, 30));

        jFTgl.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 51, 153), 1, true));
        jFTgl.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jPanel1.add(jFTgl, new org.netbeans.lib.awtextra.AbsoluteConstraints(190, 60, 80, 20));

        jLabel2.setBackground(new java.awt.Color(204, 204, 255));
        jLabel2.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jLabel2.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel2.setText("Item");
        jLabel2.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        jLabel2.setOpaque(true);
        jPanel1.add(jLabel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(350, 43, 70, -1));

        jFTglAkhir.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 51, 153), 1, true));
        jFTglAkhir.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jPanel1.add(jFTglAkhir, new org.netbeans.lib.awtextra.AbsoluteConstraints(270, 60, 80, 20));

        btnFilter1.setText("Tampilkan");
        btnFilter1.setToolTipText("Fiter");
        btnFilter1.setMaximumSize(new java.awt.Dimension(40, 40));
        btnFilter1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnFilter1ActionPerformed(evt);
            }
        });
        jPanel1.add(btnFilter1, new org.netbeans.lib.awtextra.AbsoluteConstraints(550, 55, 95, 25));

        jLabel8.setBackground(new java.awt.Color(204, 204, 255));
        jLabel8.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jLabel8.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel8.setText("From");
        jLabel8.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        jLabel8.setOpaque(true);
        jPanel1.add(jLabel8, new org.netbeans.lib.awtextra.AbsoluteConstraints(190, 43, 80, -1));

        jLabel9.setBackground(new java.awt.Color(204, 204, 255));
        jLabel9.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jLabel9.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel9.setText("To");
        jLabel9.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        jLabel9.setOpaque(true);
        jPanel1.add(jLabel9, new org.netbeans.lib.awtextra.AbsoluteConstraints(270, 43, 80, -1));

        txtItem.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        txtItem.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        jPanel1.add(txtItem, new org.netbeans.lib.awtextra.AbsoluteConstraints(350, 60, 70, 20));

        jLabel10.setBackground(new java.awt.Color(204, 204, 255));
        jLabel10.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jLabel10.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel10.setText("Supplier/ No. PO");
        jLabel10.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        jLabel10.setOpaque(true);
        jPanel1.add(jLabel10, new org.netbeans.lib.awtextra.AbsoluteConstraints(420, 43, 120, -1));

        txtSupplier.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        txtSupplier.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        jPanel1.add(txtSupplier, new org.netbeans.lib.awtextra.AbsoluteConstraints(420, 60, 120, 20));

        jXTitledPanel1.setTitle("Item Detail");
        jXTitledPanel1.setTitleFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jXTitledPanel1.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jXTitledPanel1.getContentContainer().setLayout(new javax.swing.BoxLayout(jXTitledPanel1.getContentContainer(), javax.swing.BoxLayout.LINE_AXIS));

        tblDetail.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "No.", "PRNo", "Product ID", "Keterangan", "UOM", "Qty", "Unit Price", "Disc %", "Vat", "Ext. Price", "Conv", "UomKecil", "JmlKecil", "SisaPR", "Good Receipt#", "Receipt. Time", "Qty Receipt"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Double.class, java.lang.Double.class, java.lang.Double.class, java.lang.Double.class, java.lang.Double.class, java.lang.Integer.class, java.lang.Object.class, java.lang.Double.class, java.lang.Double.class, java.lang.Object.class, java.lang.Object.class, java.lang.Double.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, true, false, false, false, false, false, false, false, false, false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        tblDetail.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_OFF);
        tblDetail.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jScrollPane3.setViewportView(tblDetail);

        jXTitledPanel1.getContentContainer().add(jScrollPane3);

        tblHeader.setAutoCreateRowSorter(true);
        tblHeader.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        tblHeader.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "PO No.", "Tanggal", "SupplierID", "Nama Supplier", "Consigment", "Remark", "Acc2", "Acc1", "Cito", "Last Print"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Boolean.class, java.lang.Object.class, java.lang.Boolean.class, java.lang.Boolean.class, java.lang.Object.class, java.lang.Object.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false, true, true, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        tblHeader.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_OFF);
        tblHeader.getTableHeader().setReorderingAllowed(false);
        tblHeader.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tblHeaderMouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(tblHeader);

        lblPRCito.setBackground(new java.awt.Color(255, 153, 153));
        lblPRCito.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        lblPRCito.setOpaque(true);

        jLabel4.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel4.setText("Legend :");

        jLabel5.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel5.setForeground(new java.awt.Color(0, 0, 153));
        jLabel5.setText("PO Cito");

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

        jLabel3.setBackground(new java.awt.Color(204, 255, 255));
        jLabel3.setForeground(new java.awt.Color(0, 0, 153));
        jLabel3.setText("<html>\n &nbsp <b>F5  &nbsp &nbsp    : </b> Membuat PO baru <br> \n &nbsp <b>F2 &nbsp &nbsp : </b>  Menyimpan PO <br>\n &nbsp <b>Insert : </b> Menambah Item PR\n</html>");
        jLabel3.setVerticalAlignment(javax.swing.SwingConstants.TOP);
        jLabel3.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        jLabel3.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jLabel3.setOpaque(true);
        jLabel3.setVerticalTextPosition(javax.swing.SwingConstants.TOP);
        jPanel2.add(jLabel3, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 40, 290, 60));

        jLabel6.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jLabel6.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel6.setText("Remark");
        jPanel2.add(jLabel6, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 10, 90, 20));

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

        lblPRPrinted.setBackground(new java.awt.Color(204, 255, 204));
        lblPRPrinted.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        lblPRPrinted.setOpaque(true);

        jLabel7.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel7.setForeground(new java.awt.Color(0, 0, 153));
        jLabel7.setText("Printed");

        jLabel11.setText("Count");

        jSlider1.setMaximum(300);
        jSlider1.setValue(120);
        jSlider1.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                jSlider1StateChanged(evt);
            }
        });

        jLDetik.setBackground(new java.awt.Color(255, 255, 204));
        jLDetik.setFont(new java.awt.Font("Dialog", 1, 10)); // NOI18N
        jLDetik.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        jLDetik.setOpaque(true);

        jLTest.setBackground(new java.awt.Color(250, 163, 120));
        jLTest.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 51, 204)));
        jLTest.setOpaque(true);

        jBSet.setText("Aplly");
        jBSet.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jBSetActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(10, 10, 10)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGap(12, 12, 12))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jScrollPane1)
                        .addGap(12, 12, 12))
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 62, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(6, 6, 6)
                                .addComponent(lblPRCito, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(82, 82, 82)
                                .addComponent(lblPRPrinted, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(6, 6, 6)
                                .addComponent(jLabel7, javax.swing.GroupLayout.PREFERRED_SIZE, 66, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(102, 102, 102)
                                .addComponent(jSlider1, javax.swing.GroupLayout.PREFERRED_SIZE, 132, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(8, 8, 8)
                                .addComponent(jLDetik, javax.swing.GroupLayout.PREFERRED_SIZE, 58, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(22, 22, 22)
                                .addComponent(jLabel11, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(10, 10, 10)
                                .addComponent(jLTest, javax.swing.GroupLayout.PREFERRED_SIZE, 68, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(12, 12, 12)
                                .addComponent(jBSet, javax.swing.GroupLayout.PREFERRED_SIZE, 70, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(layout.createSequentialGroup()
                                .addGap(107, 107, 107)
                                .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 66, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jXTitledPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                                .addGap(2, 2, 2)))
                        .addGap(10, 10, 10))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGap(12, 12, 12))))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(11, 11, 11)
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, 90, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 295, Short.MAX_VALUE)
                .addGap(4, 4, 4)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(5, 5, 5)
                        .addComponent(jLabel4))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(5, 5, 5)
                        .addComponent(lblPRCito, javax.swing.GroupLayout.PREFERRED_SIZE, 14, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(5, 5, 5)
                        .addComponent(lblPRPrinted, javax.swing.GroupLayout.PREFERRED_SIZE, 14, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(5, 5, 5)
                        .addComponent(jLabel7))
                    .addComponent(jSlider1, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLDetik, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel11, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLTest, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jBSet, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(5, 5, 5)
                        .addComponent(jLabel5))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(25, 25, 25)
                        .addComponent(jXTitledPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, 160, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(6, 6, 6)
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, 110, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnCloseActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCloseActionPerformed
        dispose();
}//GEN-LAST:event_btnCloseActionPerformed

    private void btnSaveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSaveActionPerformed
        udfSave();
}//GEN-LAST:event_btnSaveActionPerformed

    private void btnPreviewActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnPreviewActionPerformed
//        if (!tblHeader.getValueAt(tblHeader.getSelectedRow(),0).toString().trim().isEmpty()){
//            if (okCtk(tblHeader.getValueAt(tblHeader.getSelectedRow(),0).toString().trim()))
//                    printPR(tblHeader.getValueAt(tblHeader.getSelectedRow(),0).toString().trim());
//            else JOptionPane.showMessageDialog(this,"Acc PO belum lengkap, lengkapi Acc PO terlebih dahulu...!!");
//        }
        if (!tblHeader.getValueAt(tblHeader.getSelectedRow(),0).toString().trim().isEmpty()){
//            if (okCtk(tblDetail_ACC.getValueAt(tblDetail_ACC.getSelectedRow(),1).toString().trim()))
                previewPO(tblHeader.getValueAt(tblHeader.getSelectedRow(),0).toString().trim());
//            else JOptionPane.showMessageDialog(this,"Acc PR belum lengkap, lengkapi Acc PR terlebih dahulu...!!");
        }
}//GEN-LAST:event_btnPreviewActionPerformed

    private void btnFilter1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnFilter1ActionPerformed
         
}//GEN-LAST:event_btnFilter1ActionPerformed

    private void formInternalFrameOpened(javax.swing.event.InternalFrameEvent evt) {//GEN-FIRST:event_formInternalFrameOpened
       //udfInitForm();
        jLabel16.setText(getTitle());
        btnFilter1ActionPerformed(null);
    }//GEN-LAST:event_formInternalFrameOpened

    private void tblHeaderMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tblHeaderMouseClicked
        if(!bAcc1) return;
        int col=tblHeader.getSelectedColumn();
        if(col==tblHeader.getColumnModel().getColumnIndex(sAcc1) &&
                (Boolean)tblHeader.getValueAt(tblHeader.getSelectedRow(), tblHeader.getColumnModel().getColumnIndex(sAcc2))==false){

            if(JOptionPane.showConfirmDialog(this, sAcc2+" belum ACC. Anda ingin melanjutkan?", "Konfirmasi approval",
                    JOptionPane.YES_NO_OPTION)==JOptionPane.NO_OPTION){
                tblHeader.setValueAt(false, tblHeader.getSelectedRow(), col);
            }
            
        }
    }//GEN-LAST:event_tblHeaderMouseClicked

    private void txtTotalLinePropertyChange(java.beans.PropertyChangeEvent evt) {//GEN-FIRST:event_txtTotalLinePropertyChange
        // TODO add your handling code here:
}//GEN-LAST:event_txtTotalLinePropertyChange

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

    private void txtRemarkFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtRemarkFocusLost
        // TODO add your handling code here:
}//GEN-LAST:event_txtRemarkFocusLost

    private void txtRemarkKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtRemarkKeyReleased
        // TODO add your handling code here:
}//GEN-LAST:event_txtRemarkKeyReleased

    private void jSlider1StateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_jSlider1StateChanged

        if ((jSlider1.getValue()/60)==0.0){
            jLDetik.setText(String.valueOf(jSlider1.getValue()%60)+" Detik");
        }else{
            jLDetik.setText(String.valueOf(jSlider1.getValue()/60)+" Menit "+String.valueOf(jSlider1.getValue()%60)+" Detik");
        }
        waktuRefresh=Long.valueOf(String.valueOf(jSlider1.getValue()))*1000;
    }//GEN-LAST:event_jSlider1StateChanged

    private void jBSetActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jBSetActionPerformed
        //i=0;
        timer.cancel();
        timer = new Timer();
        timer.schedule(new DoTick(), 0, waktuRefresh);
}//GEN-LAST:event_jBSetActionPerformed

    private Boolean okCtk(String sNo_pr){
        boolean ok=false;
        try{
            Statement statt=conn.createStatement();
            ResultSet rss=statt.executeQuery("select (coalesce(acc_level_1,'')<>'' and coalesce(acc_level_2,'')<>'') from phar_pr where no_pr='"+sNo_pr.trim()+"'");
            if (rss.next()) ok=rss.getBoolean(1);
            rss.close();
            statt.close();
        }catch(SQLException se){}
        return ok;
    }


    private void previewPO(String sNo_PO){
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
                    tblHeader.setValueAt(rs.getTimestamp(1), tblHeader.getSelectedRow(), tblHeader.getColumnModel().getColumnIndex("Last Print"));
                }
                rs.close();
            }

      }catch(JRException je){
            System.out.println(je.getMessage());
      }catch(SQLException se){
            JOptionPane.showMessageDialog(this, se.getMessage());
      }
  }


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnClose;
    private javax.swing.JButton btnFilter1;
    private javax.swing.JButton btnPreview;
    private javax.swing.JButton btnSave;
    private javax.swing.JButton jBSet;
    private javax.swing.JFormattedTextField jFTgl;
    private javax.swing.JFormattedTextField jFTglAkhir;
    private javax.swing.JLabel jLDetik;
    private javax.swing.JLabel jLTest;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel23;
    private javax.swing.JLabel jLabel26;
    private javax.swing.JLabel jLabel27;
    private javax.swing.JLabel jLabel28;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JSlider jSlider1;
    private org.jdesktop.swingx.JXTitledPanel jXTitledPanel1;
    private javax.swing.JLabel lblPRCito;
    private javax.swing.JLabel lblPRPrinted;
    private org.jdesktop.swingx.JXTable tblDetail;
    private javax.swing.JTable tblHeader;
    private javax.swing.JTextField txtDiscPersen;
    private javax.swing.JTextField txtDiscRp;
    private javax.swing.JTextField txtItem;
    private javax.swing.JLabel txtNetto;
    private javax.swing.JTextField txtRemark;
    private javax.swing.JTextField txtSupplier;
    private javax.swing.JLabel txtTotVat;
    private javax.swing.JLabel txtTotalLine;
    // End of variables declaration//GEN-END:variables

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
                    //udfSave();
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

    private FocusListener txtFocusListener=new FocusListener() {
        public void focusGained(FocusEvent e) {
            if(e.getSource() instanceof JTextField || e.getSource() instanceof JFormattedTextField){
                ((JTextField)e.getSource()).setBackground(Color.YELLOW);

            }
        }


        public void focusLost(FocusEvent e) {
            if(e.getSource().getClass().getSimpleName().equalsIgnoreCase("JTextField")||
                    e.getSource().getClass().getSimpleName().equalsIgnoreCase("JFormattedTextField")){
                ((JTextField)e.getSource()).setBackground(Color.WHITE);


           }
        }
    } ;
}
