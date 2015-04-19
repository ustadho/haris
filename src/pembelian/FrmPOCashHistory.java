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
import java.awt.print.PrinterJob;
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
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.print.DocFlavor;
import javax.print.PrintService;
import javax.print.PrintServiceLookup;
import javax.print.attribute.HashPrintRequestAttributeSet;
import javax.print.attribute.HashPrintServiceAttributeSet;
import javax.print.attribute.PrintRequestAttributeSet;
import javax.print.attribute.PrintServiceAttributeSet;
import javax.swing.JCheckBox;
import javax.swing.JFormattedTextField;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
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
 * @author ustadho
 */
public class FrmPOCashHistory extends javax.swing.JInternalFrame {
    private Connection conn;
    private boolean bAcc1= false, bAcc2=false, bAcc3=false;
    private String  sAcc1= "", sAcc2="" , sAcc3="";
    private GeneralFunction fn;
    boolean cumaSingDurungApprove=true;
    private JFormattedTextField jFDate1;
    MyKeyListener kListener=new MyKeyListener();

    /** Creates new form FrmPRApproval */
    public FrmPOCashHistory(Connection con, boolean b) {
        initComponents();
        this.conn=con;
        this.cumaSingDurungApprove=b;
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
                if(conn!=null && tblHeader.getSelectedRow()<0) return;
                ((DefaultTableModel)tblDetail.getModel()).setNumRows(0);
                udfLoadGrDetail();
                
            }
        });

        tblDetail.getModel().addTableModelListener(new TableModelListener() {

            public void tableChanged(TableModelEvent e) {
                double qty=0, disc=0, price=0;
                double totAmount=0, totDisc=0, totNetto=0;
                TableColumnModel col=tblDetail.getColumnModel();

                for(int i=0; i<tblDetail.getRowCount(); i++){
                    price=fn.udfGetDouble(tblDetail.getValueAt(i, col.getColumnIndex("Qty")))*
                            (tblDetail.getValueAt(i, col.getColumnIndex("Unit Price"))==null?0: fn.udfGetDouble(tblDetail.getValueAt(i, col.getColumnIndex("Unit Price"))));
                    disc=price/100* (tblDetail.getValueAt(i, tblDetail.getColumnModel().getColumnIndex("Disc %"))==null? 0: fn.udfGetDouble(tblDetail.getValueAt(i, tblDetail.getColumnModel().getColumnIndex("Disc %"))));
                    totAmount+=price;
                    totDisc+=disc;
                    totNetto+=(price-disc);
                }
                txtTotAmount.setText(fn.dFmt.format(totAmount));
                txtTotDisc.setText(fn.dFmt.format(totDisc));
                txtTotNetto.setText(fn.dFmt.format(totNetto));
            }
        });
        udfInitForm();
        tblDetail.setRowHeight(25);
        
    }

    private void udfLoadGrDetail(){
        int iRow=tblHeader.getSelectedRow();
        String sNoGR=tblHeader.getValueAt(iRow, tblHeader.getColumnModel().getColumnIndex("PO Cash#")).toString();

        String sSql="select * from fn_phar_po_cash_print('"+sNoGR+"') as (no_po_cash varchar, tanggal text, " +
                "kode_supplier varchar, nama_supplier varchar, site_id varchar, site_name varchar, " +
                "location_id varchar, location_name varchar, buyer varchar, remark varchar, " +
                "kode_barang varchar, nama_barang varchar, " +
                "qty numeric, uom varchar, harga numeric, disc double precision, amount double precision)";

        //System.out.println(s);
        try{
            ((DefaultTableModel)tblDetail.getModel()).setNumRows(0);

            ResultSet rs=conn.createStatement().executeQuery(sSql);
            while(rs.next()){
                ((DefaultTableModel)tblDetail.getModel()).addRow(new Object[]{
                    ((DefaultTableModel)tblDetail.getModel()).getRowCount()+1,
                    rs.getString("kode_barang"),
                    rs.getString("nama_barang"),
                    rs.getString("uom"),
                    rs.getInt("qty"),
                    rs.getDouble("harga"),
                    rs.getDouble("disc"),
                    Math.floor(rs.getDouble("amount"))
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
        
    }

    public void setConn(Connection con){
        this.conn=con;
    }

    private void udfInitForm(){
        fn=new GeneralFunction(conn);
        try{
        ResultSet rs = conn.createStatement().executeQuery("select '01/'||to_char(current_date, 'MM/yyyy') as tgl1,  to_char(current_date, 'dd/MM/yyyy') as tgl2 " +
                    "");

            if(rs.next()){
                jFTgl.setText(cumaSingDurungApprove? rs.getString(2): rs.getString(1));
                jFTgl.setValue(cumaSingDurungApprove? rs.getString(2): rs.getString(1));
                jFTglAkhir.setText(rs.getString(2));
                jFTglAkhir.setValue(rs.getString(2));
            }

            rs.close();
        }catch(SQLException se){

        }
        tblHeader.getTableHeader().setReorderingAllowed(false);

        for(int i=0; i< tblHeader.getColumnCount(); i++){
            tblHeader.getColumnModel().getColumn(i).setCellRenderer(new MyRowRenderer());
        }

        tblHeader.setRowHeight(22);
        tblDetail.setRowHeight(22);
        tblHeader.getColumn("PO Cash#").setPreferredWidth(100);
        tblHeader.getColumn("Nama Supplier").setPreferredWidth(130);
        tblHeader.getColumn("Tanggal").setPreferredWidth(110);
        udfLoadPO();
            
        jLabel16.setText(getTitle());
        
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                tblHeader.requestFocus();
            }
      });


    }

   private void printKwitansi(){
       int iRow=tblHeader.getSelectedRow();
        if(iRow<0) return;

        String sNoGR=tblHeader.getValueAt(iRow, 0).toString();
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
//        if (JOptionPane.showConfirmDialog(null,"Printer Siap!","SHS Go Open Source",JOptionPane.YES_NO_OPTION,JOptionPane.QUESTION_MESSAGE)==JOptionPane.YES_OPTION) {
//            PrintPOCash pn = new PrintPOCash(conn, sNoGR, false, services[i]);
//        }
    }

    private void udfLoadPO(){
        try{
            SimpleDateFormat dmy = new SimpleDateFormat("dd/MM/yyyy");
            SimpleDateFormat ymd = new SimpleDateFormat("yyyy-MM-dd");
            String sQry="select distinct h.no_po_cash, h.tanggal, " +
                    "coalesce(h.kode_supplier,'') as kode_supp, coalesce(sp.nama_supplier,'') as nama_supplier, " +
                    "coalesce(h.site_id,'') as site_id, coalesce(g.deskripsi,'') as site_name,  " +
                    "coalesce(h.remark,'') as remark " +
                    "from phar_po_cash h " +
                    "inner join phar_po_cash_detail d on d.no_po_cash=h.no_po_cash " +
                    "inner join barang i on i.item_code=d.kode_barang " +
                    "left join phar_supplier sp on sp.kode_supplier=h.kode_supplier " +
                    "left join gudang g on g.kode_gudang=h.site_id " +
                    "where h.flag_trx='T' " +
                    "and d.kode_barang||coalesce(i.nama_paten,'') ilike '%"+txtItem.getText()+"%' " +
                    "and h.kode_supplier||coalesce(sp.nama_supplier,'') ilike '%"+txtSupplier.getText()+"%' " +
                    "and to_char(h.tanggal, 'yyyy-MM-dd')>='" + ymd.format(dmy.parse(jFTgl.getText())) + "' " +
                    "and to_char(h.tanggal, 'yyyy-MM-dd')<='" + ymd.format(dmy.parse(jFTglAkhir.getText())) + "' " +
                    " order by h.tanggal desc";

            try {
                ResultSet rs = conn.createStatement().executeQuery(sQry);
                ((DefaultTableModel) tblHeader.getModel()).setNumRows(0);
                while (rs.next()) {
                    ((DefaultTableModel) tblHeader.getModel()).addRow(new Object[]{
                        rs.getString("no_po_cash"),
                        rs.getTimestamp("tanggal"),
                        rs.getString("kode_supp"),
                        rs.getString("nama_supplier"),
                        rs.getString("remark"),
                        rs.getString("site_id")
                    });
                }
                if(tblHeader.getRowCount()>0)
                    tblHeader.setRowSelectionInterval(0, 0);
                
                tblHeader.setModel((DefaultTableModel) fn.autoResizeColWidth(tblHeader, (DefaultTableModel)tblHeader.getModel()).getModel());
            } catch (SQLException se) {
                JOptionPane.showMessageDialog(this, se.getMessage());
            }
        }catch(ParseException ex){
            Logger.getLogger(FrmPOCashHistory.class.getName()).log(Level.SEVERE, null,ex);
        }
    }

    SimpleDateFormat dmyFmt=new SimpleDateFormat("dd/MM/yyyy");
    SimpleDateFormat dmyFmt_hhmm=new SimpleDateFormat("dd/MM/yyyy HH:mm");

    private void previewPOCash() {
        try{
            int iRow=tblHeader.getSelectedRow();
            String sPoCash=tblHeader.getValueAt(iRow, 0).toString();
            setCursor(new Cursor(Cursor.WAIT_CURSOR));
            JasperReport jasperReportmkel = (JasperReport) JRLoader.loadObject(getClass().getResourceAsStream("/Reports/PO_Cash.jasper"));
            HashMap parameter = new HashMap();
            parameter.put("corporate", MainForm.sNamaUsaha);
            parameter.put("alamat", MainForm.sAlamat);
            parameter.put("telp", "");
            parameter.put("no_po_cash",sPoCash);
            JasperPrint jasperPrintmkel = JasperFillManager.fillReport(jasperReportmkel, parameter, conn);

            setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
            jasperPrintmkel.setOrientation(jasperPrintmkel.getOrientationValue());
            JasperViewer.viewReport(jasperPrintmkel, false);
            if(!jasperPrintmkel.getPages().isEmpty()){
                
            }

      }catch(JRException je){
            System.out.println(je.getMessage());
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
                setBackground(table.getBackground());
                setForeground(table.getForeground());
                
            }
            JCheckBox checkBox = new JCheckBox();
            if(value instanceof Date ){
                value=dmyFmt_hhmm.format(value);
            }if(value instanceof Timestamp ){
                value=dmyFmt_hhmm.format(value);
            }else if(value instanceof Double ||value instanceof Integer ||value instanceof Float  ){
                setHorizontalAlignment(SwingConstants.RIGHT);
                value=fn.dFmt.format(value);
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
        btnPreview = new javax.swing.JButton();
        jLabel16 = new javax.swing.JLabel();
        btnFilter1 = new javax.swing.JButton();
        jFTgl = new javax.swing.JFormattedTextField();
        jLabel2 = new javax.swing.JLabel();
        jFTglAkhir = new javax.swing.JFormattedTextField();
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
        lblInfo = new javax.swing.JLabel();
        jPanel2 = new javax.swing.JPanel();
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

        setClosable(true);
        setIconifiable(true);
        setMaximizable(true);
        setResizable(true);
        setTitle("PO Cash History");
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
        btnClose.setMaximumSize(new java.awt.Dimension(40, 40));
        btnClose.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnClose.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCloseActionPerformed(evt);
            }
        });
        jPanel1.add(btnClose, new org.netbeans.lib.awtextra.AbsoluteConstraints(70, 10, 70, 60));

        btnPreview.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/Icon/buttons/print-32.png"))); // NOI18N
        btnPreview.setText("Print");
        btnPreview.setToolTipText("New     (F12)");
        btnPreview.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnPreview.setMaximumSize(new java.awt.Dimension(40, 40));
        btnPreview.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnPreview.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnPreviewActionPerformed(evt);
            }
        });
        jPanel1.add(btnPreview, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 10, 60, 60));

        jLabel16.setFont(new java.awt.Font("Tahoma", 0, 24)); // NOI18N
        jLabel16.setForeground(new java.awt.Color(0, 0, 153));
        jLabel16.setText("PO Cash History");
        jPanel1.add(jLabel16, new org.netbeans.lib.awtextra.AbsoluteConstraints(455, 0, 350, 30));

        btnFilter1.setText("Tampilkan");
        btnFilter1.setToolTipText("Fiter");
        btnFilter1.setMaximumSize(new java.awt.Dimension(40, 40));
        btnFilter1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnFilter1ActionPerformed(evt);
            }
        });
        jPanel1.add(btnFilter1, new org.netbeans.lib.awtextra.AbsoluteConstraints(555, 45, 95, 25));

        jFTgl.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 51, 153), 1, true));
        jFTgl.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jPanel1.add(jFTgl, new org.netbeans.lib.awtextra.AbsoluteConstraints(190, 50, 80, 20));

        jLabel2.setBackground(new java.awt.Color(204, 204, 255));
        jLabel2.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jLabel2.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel2.setText("Item");
        jLabel2.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        jLabel2.setOpaque(true);
        jPanel1.add(jLabel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(350, 35, 70, -1));

        jFTglAkhir.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 51, 153), 1, true));
        jFTglAkhir.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jPanel1.add(jFTglAkhir, new org.netbeans.lib.awtextra.AbsoluteConstraints(270, 50, 80, 20));

        jLabel8.setBackground(new java.awt.Color(204, 204, 255));
        jLabel8.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jLabel8.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel8.setText("From");
        jLabel8.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        jLabel8.setOpaque(true);
        jPanel1.add(jLabel8, new org.netbeans.lib.awtextra.AbsoluteConstraints(190, 35, 80, -1));

        jLabel9.setBackground(new java.awt.Color(204, 204, 255));
        jLabel9.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jLabel9.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel9.setText("To");
        jLabel9.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        jLabel9.setOpaque(true);
        jPanel1.add(jLabel9, new org.netbeans.lib.awtextra.AbsoluteConstraints(270, 35, 80, -1));

        txtItem.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        txtItem.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        jPanel1.add(txtItem, new org.netbeans.lib.awtextra.AbsoluteConstraints(350, 50, 70, 20));

        jLabel10.setBackground(new java.awt.Color(204, 204, 255));
        jLabel10.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jLabel10.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel10.setText("Supplier");
        jLabel10.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        jLabel10.setOpaque(true);
        jPanel1.add(jLabel10, new org.netbeans.lib.awtextra.AbsoluteConstraints(420, 35, 120, -1));

        txtSupplier.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        txtSupplier.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        jPanel1.add(txtSupplier, new org.netbeans.lib.awtextra.AbsoluteConstraints(420, 50, 120, 20));

        jXTitledPanel1.setTitle("Item Detail");
        jXTitledPanel1.setTitleFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jXTitledPanel1.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jXTitledPanel1.getContentContainer().setLayout(new javax.swing.BoxLayout(jXTitledPanel1.getContentContainer(), javax.swing.BoxLayout.LINE_AXIS));

        tblDetail.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "No.", "Product ID", "Keterangan", "UOM", "Qty", "Unit Price", "Disc %", "Ext. Price"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Double.class, java.lang.Double.class, java.lang.Double.class, java.lang.Double.class
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
        tblDetail.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_OFF);
        tblDetail.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jScrollPane3.setViewportView(tblDetail);

        jXTitledPanel1.getContentContainer().add(jScrollPane3);

        tblHeader.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        tblHeader.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "PO Cash#", "Tanggal", "SupplierID", "Nama Supplier", "Remark", "SiteID"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false
            };

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

        lblInfo.setForeground(new java.awt.Color(0, 0, 153));

        jPanel2.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        jPanel2.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

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

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(10, 10, 10)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(286, 286, 286)
                        .addComponent(lblInfo, javax.swing.GroupLayout.PREFERRED_SIZE, 522, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, 808, Short.MAX_VALUE)
                            .addComponent(jScrollPane1)
                            .addComponent(jXTitledPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addGap(2, 2, 2)))
                .addGap(10, 10, 10))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(11, 11, 11)
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(6, 6, 6)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 270, Short.MAX_VALUE)
                .addGap(3, 3, 3)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(50, 50, 50)
                        .addComponent(lblInfo, javax.swing.GroupLayout.PREFERRED_SIZE, 8, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jXTitledPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, 140, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(5, 5, 5))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnCloseActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCloseActionPerformed
        dispose();
}//GEN-LAST:event_btnCloseActionPerformed

    private void btnPreviewActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnPreviewActionPerformed
        if (!tblHeader.getValueAt(tblHeader.getSelectedRow(),0).toString().trim().isEmpty()){
            //printKwitansi();
            previewPOCash();
        }
}//GEN-LAST:event_btnPreviewActionPerformed

    private void btnFilter1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnFilter1ActionPerformed
        udfLoadPO();
}//GEN-LAST:event_btnFilter1ActionPerformed

    private void formInternalFrameOpened(javax.swing.event.InternalFrameEvent evt) {//GEN-FIRST:event_formInternalFrameOpened
       //udfInitForm();
        jLabel16.setText(getTitle());
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

    private void txtTotAmountPropertyChange(java.beans.PropertyChangeEvent evt) {//GEN-FIRST:event_txtTotAmountPropertyChange
        // TODO add your handling code here:
}//GEN-LAST:event_txtTotAmountPropertyChange

    private void txtTotDiscPropertyChange(java.beans.PropertyChangeEvent evt) {//GEN-FIRST:event_txtTotDiscPropertyChange
        // TODO add your handling code here:
}//GEN-LAST:event_txtTotDiscPropertyChange

    private void txtTotNettoPropertyChange(java.beans.PropertyChangeEvent evt) {//GEN-FIRST:event_txtTotNettoPropertyChange
        // TODO add your handling code here:
}//GEN-LAST:event_txtTotNettoPropertyChange

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
            ResultSet rss=statt.executeQuery("select user_name,coalesce(ud.complete_user_name,'') as complete_name, coalesce(level,0) as level,jabatan,singkatan,ttd_electronic " +
                    "from user_acc ua " +
                    "inner join user_detail ud on ud.user_id=ua.user_id " +
                    "inner join pejabat p on p.kode_jabatan=ud.kode_jabatan " +
                    "where acc_modul='phar_po' order by level limit 3");
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
            JasperReport jasperReportmkel = (JasperReport) JRLoader.loadObject(getClass().getResourceAsStream("Reports/PO_v1.jasper"));
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
            parameter.put("stempel", getClass().getResource("/pharpurchase/image/StempelPurchasing.gif").toString());
            JasperPrint jasperPrintmkel = JasperFillManager.fillReport(jasperReportmkel, parameter, conn);
            setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
            jasperPrintmkel.setOrientation(jasperPrintmkel.getOrientationValue());
            JasperViewer.viewReport(jasperPrintmkel, false);
      } catch(JRException je){System.out.println(je.getMessage());}
  }


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnClose;
    private javax.swing.JButton btnFilter1;
    private javax.swing.JButton btnPreview;
    private javax.swing.JFormattedTextField jFTgl;
    private javax.swing.JFormattedTextField jFTglAkhir;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel27;
    private javax.swing.JLabel jLabel28;
    private javax.swing.JLabel jLabel29;
    private javax.swing.JLabel jLabel30;
    private javax.swing.JLabel jLabel33;
    private javax.swing.JLabel jLabel34;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JSeparator jSeparator2;
    private org.jdesktop.swingx.JXTitledPanel jXTitledPanel1;
    private javax.swing.JLabel lblInfo;
    private org.jdesktop.swingx.JXTable tblDetail;
    private javax.swing.JTable tblHeader;
    private javax.swing.JTextField txtItem;
    private javax.swing.JTextField txtSupplier;
    private javax.swing.JLabel txtTotAmount;
    private javax.swing.JLabel txtTotDisc;
    private javax.swing.JLabel txtTotNetto;
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
