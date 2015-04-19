/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * FrmPRApproval.java
 *
 * Created on Jul 29, 2010, 11:51:31 AM
 */

package apotek.inventori;

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
import main.MainForm;
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
public class FrmUnplannedHistory extends javax.swing.JInternalFrame {
    private Connection conn;
    private boolean bAcc1= false, bAcc2=false, bAcc3=false;
    private String  sAcc1= "", sAcc2="" , sAcc3="";
    private GeneralFunction fn;
    boolean cumaSingDurungApprove=true;
    private JFormattedTextField jFDate1;
    MyKeyListener kListener=new MyKeyListener();

    /** Creates new form FrmPRApproval */
    public FrmUnplannedHistory(Connection con, boolean b) {
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
//                btnPreview.setEnabled(tblHeader.getSelectedRow()>=0 &&
//                        (Boolean)tblHeader.getValueAt(tblHeader.getSelectedRow(), tblHeader.getColumnModel().getColumnIndex(sAcc2)));
                if(conn!=null && tblHeader.getSelectedRow()<0) return;
                udfLoadDetail();
                
            }
        });


        tblDetail.getModel().addTableModelListener(new TableModelListener() {
            public void tableChanged(TableModelEvent e) {
                TableColumnModel col=tblDetail.getColumnModel();

                if(tblDetail.getRowCount()>0){
                    double totLine=0, totVat=0;

                    for(int i=0; i< tblDetail.getRowCount(); i++){
                        if(e.getType()==TableModelEvent.DELETE)
                            ((DefaultTableModel)tblDetail.getModel()).setValueAt(i+1, i, col.getColumnIndex("No."));

                        totLine+=(Double)tblDetail.getValueAt(i, col.getColumnIndex("Sub Total"));
                        
                    }

                    txtTotalLine.setText(fn.dFmt.format(Math.floor(totLine)));
                    
                }else{
                    txtTotalLine.setText("0");
                    
                }
            }
        });
        udfInitForm();
        tblDetail.setRowHeight(25);
        
    }

    private void udfLoadDetail(){
        int iRow=tblHeader.getSelectedRow();
        String sNoReceipt=tblHeader.getValueAt(iRow, tblHeader.getColumnModel().getColumnIndex("Receipt#")).toString();

        String s ="select d.kode_barang, i.nama_barang, coalesce(i.uom_kecil,'') as uom, coalesce(d.qty,0) as qty, " +
                "coalesce(d.unit_price,0) as price, coalesce(d.qty,0)*coalesce(d.unit_price,0) as sub_total, expired_date " +
                "from phar_unplanned_detail d " +
                "inner join phar_item i on i.kode_barang=d.kode_barang " +
                "where d.unplanned_no='"+sNoReceipt+"' " +
                "order by coalesce(i.nama_barang,'')";

        System.out.println(s);
        try{
            ((DefaultTableModel)tblDetail.getModel()).setNumRows(0);

            ResultSet rs=conn.createStatement().executeQuery(s);
            while(rs.next()){
                ((DefaultTableModel)tblDetail.getModel()).addRow(new Object[]{
                    ((DefaultTableModel)tblDetail.getModel()).getRowCount()+1,
                    rs.getString("kode_barang"),
                    rs.getString("nama_barang"),
                    rs.getString("uom"),
                    rs.getInt("qty"),
                    rs.getDouble("price"),
                    Math.floor(rs.getDouble("sub_total")),
                    rs.getString("expired_date")
                    
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

        try {
            ResultSet rs = conn.createStatement().executeQuery("select '01/'||to_char(current_date, 'MM/yyyy') as tgl1,  to_char(current_date, 'dd/MM/yyyy') as tgl2 " +
                    "");

            if(rs.next()){
                jFTgl.setText(cumaSingDurungApprove? rs.getString(2): rs.getString(1));
                jFTgl.setValue(cumaSingDurungApprove? rs.getString(2): rs.getString(1));
                jFTglAkhir.setText(rs.getString(2));
                jFTglAkhir.setValue(rs.getString(2));
            }
//
//            rs.close();
//            rs=conn.createStatement().executeQuery("select * from user_acc where user_id='"+PHARMainMenu.sID+"' " +
//                    "and acc_modul='phar_retur_order'");
//            while(rs.next()){
//                if(rs.getInt("level")==1) bAcc1=true;
//                if(rs.getInt("level")==2) bAcc2=true;
//
//            }
//            rs.close();
//            rs=conn.createStatement().executeQuery("select distinct acc.level, coalesce(singkatan,'') " +
//                    "from user_acc acc  " +
//                    "inner join user_detail d on d.user_id=acc.user_id " +
//                    "inner join pejabat jb on jb.kode_jabatan=d.kode_jabatan " +
//                    "where acc_modul='phar_retur_order'  " +
//                    "and coalesce(acc.priority,0)=0 order by acc.level");
//
//
//            while(rs.next()){
//                if(rs.getInt("level")==1) sAcc1=rs.getString(2);
//                if(rs.getInt("level")==2) sAcc2=rs.getString(2);
//                //if(rs.getInt("level")==3) sAcc3=rs.getString(2);
//            }
//            //bAcc3=(cumaSingDurungApprove&&bAcc3);
//            bAcc2=(cumaSingDurungApprove&&bAcc2);
//            bAcc1=(cumaSingDurungApprove&&bAcc1);
//
//            tblHeader.setModel(new javax.swing.table.DefaultTableModel(
//                new Object [][] {},
//                new String [] {"Return Order#", "Tanggal", "SupplierID", "Nama Supplier", "Remark", sAcc2, sAcc1}
//            ) {
//                Class[] types = new Class [] {
//                    java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class,
//                    java.lang.Object.class, java.lang.Boolean.class, java.lang.Boolean.class
//                };
//                boolean[] canEdit = new boolean [] {
//                    false, false, false, false, false, bAcc2, bAcc1
//                };
//
//                @Override
//                public Class getColumnClass(int columnIndex) {
//                    return types [columnIndex];
//                }
//
//                @Override
//                public boolean isCellEditable(int rowIndex, int columnIndex) {
//                    return canEdit [columnIndex];
//                }
//            });
//
//
//            tblHeader.getTableHeader().setReorderingAllowed(false);
//
//            UIManager.put(GroupableTableHeader.uiClassID, "tableRender.GroupableTableHeaderUI");
//            GroupableTableHeader header = new GroupableTableHeader(tblHeader.getColumnModel());
//            TableColumnModel columns = tblHeader.getColumnModel();
//            ColumnGroup acc = new ColumnGroup("Approve By");
//            acc.add(columns.getColumn(5));
//            acc.add(columns.getColumn(6));
//
//            header.addGroup(acc);
//
//            tblHeader.setTableHeader(header);
//
            for(int i=0; i< tblHeader.getColumnCount(); i++){
                tblHeader.getColumnModel().getColumn(i).setCellRenderer(new MyRowRenderer());
            }
            tblDetail.getColumn("Expired").setCellRenderer(new MyRowRenderer());
            tblHeader.setRowHeight(22);
            tblDetail.setRowHeight(22);
            udfLoadUnplanned();
            
        } catch (SQLException ex) {
            Logger.getLogger(FrmUnplannedHistory.class.getName()).log(Level.SEVERE, null, ex);
        }

        jLabel16.setText(getTitle());
        
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                tblHeader.requestFocus();
            }
      });


    }

    private void udfLoadUnplanned(){
        try{
            SimpleDateFormat dmy = new SimpleDateFormat("dd/MM/yyyy");
            SimpleDateFormat ymd = new SimpleDateFormat("yyyy-MM-dd");
            String sQry="select distinct h.unplanned_no, h.date_unplanned, h.site_id, coalesce(s.site_name,'') as site_name ," +
                    "coalesce(t.trx_type_name,'') as trx_type, coalesce(h.remark,'') as remark " +
                    "from phar_unplanned h " +
                    "inner join phar_unplanned_detail d on d.unplanned_no=d.unplanned_no " +
                    "left join phar_trx_type t on h.trx_type_id=t.trx_type_id " +
                    "left join phar_item i on i.kode_barang=d.kode_barang  " +
                    "left join phar_site s on s.site_id=h.site_id " +
                    "where coalesce(i.nama_barang,'')||d.kode_barang ilike '%"+txtItem.getText()+"%'  " +
                    "and to_char(h.date_unplanned, 'yyyy-MM-dd')>='" + ymd.format(dmy.parse(jFTgl.getText())) + "' " +
                    "and to_char(h.date_unplanned, 'yyyy-MM-dd')<='" + ymd.format(dmy.parse(jFTglAkhir.getText())) + "' " +
                    "and coalesce(h.remark,'') ilike '%"+txtRemark.getText()+"%' " +
                    "order by h.date_unplanned desc";

            try {
                ResultSet rs = conn.createStatement().executeQuery(sQry);
                ((DefaultTableModel) tblHeader.getModel()).setNumRows(0);
                while (rs.next()) {
                    ((DefaultTableModel) tblHeader.getModel()).addRow(new Object[]{
                        rs.getString("unplanned_no"),
                        rs.getTimestamp("date_unplanned"),
                        rs.getString("trx_type"),
                        rs.getString("site_id"),
                        rs.getString("site_name"),
                        rs.getString("remark")
                        
                    });
                }
                if(tblHeader.getRowCount()>0)
                    tblHeader.setRowSelectionInterval(0, 0);
                
                tblHeader.setModel((DefaultTableModel) fn.autoResizeColWidth(tblHeader, (DefaultTableModel)tblHeader.getModel()).getModel());
            } catch (SQLException se) {
                JOptionPane.showMessageDialog(this, se.getMessage());
            }
        }catch(ParseException ex){
            Logger.getLogger(FrmUnplannedHistory.class.getName()).log(Level.SEVERE, null,ex);
        }
    }

    SimpleDateFormat dmyFmt=new SimpleDateFormat("dd/MM/yy");
    SimpleDateFormat dmyFmt_hhmm=new SimpleDateFormat("dd/MM/yyyy HH:mm");

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
                value=dmyFmt.format(value);
            }if(value instanceof Timestamp ){
                value=dmyFmt.format(value);
            }else if(value instanceof Double ||value instanceof Integer ||value instanceof Float  ){
                setHorizontalAlignment(SwingConstants.RIGHT);
                value=fn.dFmt.format(value);
            }else if (value instanceof Boolean) { // Boolean
                checkBox.setSelected(((Boolean) value).booleanValue());
                checkBox.setHorizontalAlignment(lblInfo.CENTER);
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
        txtRemark = new javax.swing.JTextField();
        jXTitledPanel1 = new org.jdesktop.swingx.JXTitledPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        tblDetail = new javax.swing.JTable();
        jScrollPane1 = new javax.swing.JScrollPane();
        tblHeader = new javax.swing.JTable();
        lblInfo = new javax.swing.JLabel();
        jPanel2 = new javax.swing.JPanel();
        txtTotalLine = new javax.swing.JLabel();
        jLabel23 = new javax.swing.JLabel();

        setClosable(true);
        setIconifiable(true);
        setMaximizable(true);
        setResizable(true);
        setTitle("Receipt Unplanned History");
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
        jPanel1.add(btnClose, new org.netbeans.lib.awtextra.AbsoluteConstraints(740, 10, 50, 60));

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
        jPanel1.add(btnPreview, new org.netbeans.lib.awtextra.AbsoluteConstraints(690, 10, 50, 60));

        jLabel16.setFont(new java.awt.Font("Tahoma", 0, 24)); // NOI18N
        jLabel16.setForeground(new java.awt.Color(0, 0, 153));
        jLabel16.setText("Receipt Unplanned History");
        jPanel1.add(jLabel16, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 540, 30));

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

        btnFilter1.setText("Load");
        btnFilter1.setToolTipText("Fiter");
        btnFilter1.setMaximumSize(new java.awt.Dimension(40, 40));
        btnFilter1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnFilter1ActionPerformed(evt);
            }
        });
        jPanel1.add(btnFilter1, new org.netbeans.lib.awtextra.AbsoluteConstraints(550, 55, 60, 25));

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
        jLabel10.setText("Remark");
        jLabel10.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        jLabel10.setOpaque(true);
        jPanel1.add(jLabel10, new org.netbeans.lib.awtextra.AbsoluteConstraints(420, 43, 120, -1));

        txtRemark.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        txtRemark.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        jPanel1.add(txtRemark, new org.netbeans.lib.awtextra.AbsoluteConstraints(420, 60, 120, 20));

        jXTitledPanel1.setTitle("Item Detail");
        jXTitledPanel1.setTitleFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jXTitledPanel1.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jXTitledPanel1.getContentContainer().setLayout(new javax.swing.BoxLayout(jXTitledPanel1.getContentContainer(), javax.swing.BoxLayout.LINE_AXIS));

        tblDetail.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        tblDetail.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "No.", "Product ID", "Keterangan", "UOM", "Qty", "Unit Price", "Sub Total", "Expired"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Integer.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Double.class, java.lang.Double.class, java.lang.Double.class, java.lang.Object.class
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
        jScrollPane2.setViewportView(tblDetail);

        jXTitledPanel1.getContentContainer().add(jScrollPane2);

        tblHeader.setAutoCreateRowSorter(true);
        tblHeader.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        tblHeader.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Receipt#", "Tanggal", "Receipt Type", "SiteID", "Site Name", "Remark"
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

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jXTitledPanel1, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 808, Short.MAX_VALUE)
                    .addComponent(jPanel1, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 808, Short.MAX_VALUE)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 808, Short.MAX_VALUE)
                    .addComponent(lblInfo, javax.swing.GroupLayout.PREFERRED_SIZE, 382, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, 808, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, 89, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 274, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lblInfo)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jXTitledPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, 232, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, 38, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnCloseActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCloseActionPerformed
        dispose();
}//GEN-LAST:event_btnCloseActionPerformed

    private void btnPreviewActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnPreviewActionPerformed
        if (!tblHeader.getValueAt(tblHeader.getSelectedRow(),0).toString().trim().isEmpty()){
            printKwitansi(tblHeader.getValueAt(tblHeader.getSelectedRow(),0).toString(), false);
        }
}//GEN-LAST:event_btnPreviewActionPerformed

    private void btnFilter1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnFilter1ActionPerformed
        udfLoadUnplanned();
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

    private void txtTotalLinePropertyChange(java.beans.PropertyChangeEvent evt) {//GEN-FIRST:event_txtTotalLinePropertyChange
        // TODO add your handling code here:
}//GEN-LAST:event_txtTotalLinePropertyChange

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
                    "where acc_modul='phar_po' and coalesce(priority,0)=0 order by level limit 3");
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

    private void printKwitansi(String sNoRetur, Boolean okCpy){
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
        if (JOptionPane.showConfirmDialog(null,"Siapkan Printer!","SHS Go Open Source",JOptionPane.YES_NO_OPTION,JOptionPane.QUESTION_MESSAGE)==JOptionPane.YES_OPTION) {
//            PrintReceiptUnplanned pn = new PrintReceiptUnplanned(conn,sNoRetur, okCpy, PHARMainMenu.sUserName, services[i]);
        }
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
    private javax.swing.JLabel jLabel23;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private org.jdesktop.swingx.JXTitledPanel jXTitledPanel1;
    private javax.swing.JLabel lblInfo;
    private javax.swing.JTable tblDetail;
    private javax.swing.JTable tblHeader;
    private javax.swing.JTextField txtItem;
    private javax.swing.JTextField txtRemark;
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
