/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * FrmPR.java
 *
 * Created on Jul 1, 2010, 6:20:27 PM
 */

package pembelian;

import apotek.DLgLookup;
import main.MainForm;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Cursor;
import java.awt.FocusTraversalPolicy;
import java.awt.Font;
import java.awt.KeyboardFocusManager;
import java.awt.Toolkit;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.PatternSyntaxException;
import javax.swing.AbstractCellEditor;
import javax.swing.JComponent;
import javax.swing.JFormattedTextField;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.RowFilter;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TableColumnModelEvent;
import javax.swing.event.TableColumnModelListener;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;
import javax.swing.text.MaskFormatter;
import main.DlgLookup;
import main.GeneralFunction;
/**
 *
 * @author cak-ust
 */
public class FrmPR extends javax.swing.JInternalFrame {
    private Connection conn;
    GeneralFunction fn;
    MyKeyListener kListener=new MyKeyListener();
    //FrmLookupBarang lookupItem=new FrmLookupBarang();
//    FrmLookupItemMaster lookupItem=new FrmLookupItemMaster();
    DLgLookup lookupItem=new DLgLookup(JOptionPane.getFrameForComponent(this), true);
    private boolean isKoreksi=false, sudahAdaPO=false;
    TableRowSorter<TableModel> sorter;


    /** Creates new form FrmPR */
    public FrmPR() {
        initComponents();
        MyTableCellEditor cEditor=new MyTableCellEditor();
        tblPR.getColumnModel().getColumn(0).setCellEditor(cEditor);
        tblPR.getColumnModel().getColumn(3).setCellEditor(cEditor);
        
         tblPR.getModel().addTableModelListener(new MyTableModelListener(tblPR));
         tblPR.getTableHeader().setFont(new Font("Tahoma", 0, 12));
         tblPR.setFont(new Font("Tahoma", 0, 12));
         tblPR.setRowHeight(22);

         tblPR.getColumn("Description").setPreferredWidth(200);
         tblPR.getColumn("Nama Supplier").setPreferredWidth(200);
         tblPR.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            public void valueChanged(ListSelectionEvent e) {
                if(tblPR.getSelectedRow()>=0 && tblPR.getValueAt(tblPR.getSelectedRow(), 0)!=null){
                    udfLoadKetBawah();
                }
            }
        });

        tblPR.getColumnModel().addColumnModelListener(new TableColumnModelListener() {

            public void columnAdded(TableColumnModelEvent e) {
            }

            public void columnRemoved(TableColumnModelEvent e) {
            }

            public void columnMoved(TableColumnModelEvent e) {
            }

            public void columnMarginChanged(ChangeEvent e) {
            }

            public void columnSelectionChanged(ListSelectionEvent e) {
//                if(tblPR.getSelectedRow()>=0 && (tblPR.getSelectedColumn()==tblPR.getColumnModel().getColumnIndex("Kode Supp.") || tblPR.getSelectedColumn()==tblPR.getColumnModel().getColumnIndex("Supplier"))){
//                    lblInfoLookupSupp.setText("<html> " +
//                            "<b>F9 : </b> Ubah Supplier" +
//                            "</html>");
//
//                }else
                    lblInfoLookupSupp.setText("");
            }
        });
//        jTable1.getColumn("Qty").setCellRenderer(new MyRowRenderer());

        sorter=new TableRowSorter<TableModel>(tblPR.getModel());
        tblPR.setRowSorter(sorter);

        
        tblPR.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).
        put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0),
            "selectNextColumnCell");
    }

    public void setKoreksi(boolean b){
        this.isKoreksi=b;
        txtPRNo.setEnabled(b);
    }

    private void udfLoadPRKoreksi(){
        if(txtPRNo.getText().trim().length()==0) return;
        String sQry="select pr.no_pr,  coalesce(automatic,false) as automatic, coalesce(pr.site_id,'') as site_id, coalesce(st.site_name,'')  as site_name, coalesce(pr.keterangan,'') as keterangan," +
                "coalesce(pr.requested_by,'') as req_by, to_char(release_date, 'dd/MM/yyyy hh24:MI') as req_date, " +
                "to_char(pr.need_date, 'dd/MM/yyyy') as need_date, to_char(pr.need_date, 'hh24:MI') as need_time, coalesce(po.no_po,'') as no_po " +
                "from phar_pr pr " +
                "left join phar_site st on st.site_id=pr.site_id " +
                "left join phar_po_detail pod on pod.no_pr=pr.no_pr " +
                "left join phar_po po on po.no_po=pod.no_po and flag_trx='T' " +
                "where pr.no_pr='"+txtPRNo.getText()+"'";

        try{
            ResultSet rs=conn.createStatement().executeQuery(sQry);

            String sNoPo="";

            if(rs.next()){
                sNoPo=rs.getString("no_po");
                sudahAdaPO=sNoPo.length()>0;
                
                if(sNoPo.length()>0){
                    if(JOptionPane.showConfirmDialog(this, "PR sudah dibuatkan PO, apakah PR tetap akan di koreksi?\n" +
                            "Jika ya, PO akan menyesuaikan dengan nomor PR yang baru!", "Konfirmasi Koreksi", JOptionPane.YES_NO_OPTION)==JOptionPane.NO_OPTION){
                        rs.close();
                        return;
                    }
                }

                txtPRNo.setText(rs.getString("no_pr"));
                chkOtomatis.setSelected(rs.getBoolean("automatic"));
                txtSite.setText(rs.getString("site_id"));
                lblSite.setText(rs.getString("site_name"));
                txtReqBy.setText(rs.getString("req_by"));
                lblReqDate.setText(rs.getString("req_date"));
                jFDateNeed.setText(rs.getString("need_date"));
                jFTimelNeed.setText(rs.getString("need_time"));
                rs.close();

                sQry="select prd.kode_barang, coalesce(i.nama_barang,'') as nama_barang, "
                        + "coalesce(prd.uom,'') as uom, coalesce(prd.jumlah,0) as qty," +
                        "coalesce(prd.kode_supp,'') as kode_supp, coalesce(s.nama_supplier,'') as nama_supplier, "
                        + "coalesce(prd.location_id,'') as location_id " +
                        "from phar_pr_detail prd " +
                        "left join phar_item i on i.kode_barang=prd.kode_barang " +
                        "left join phar_supplier s on s.kode_supplier=prd.kode_supp " +
                        "where prd.no_pr='"+txtPRNo.getText()+"' order by 2" ;

                rs=conn.createStatement().executeQuery(sQry);
                ((DefaultTableModel)tblPR.getModel()).setNumRows(0);
                while(rs.next()){
                    ((DefaultTableModel)tblPR.getModel()).addRow(new Object[]{
                        rs.getString("kode_barang"),
                        rs.getString("nama_barang"),
                        rs.getString("uom"),
                        rs.getDouble("qty"),
                        rs.getString("kode_supp"),
                        rs.getString("nama_suplier"),
                        rs.getString("location_id")
                    });
                            //txtPRNo.requestFocusInWindow();
                return;
                }
            }
        }catch(SQLException se){
            JOptionPane.showMessageDialog(this, se.getMessage());
        }
    }

    private void udfLoadKetBawah() {
        try{
//            String sQry="select coalesce(sb.uom_alt,'') as uom_alt, coalesce(sb.convertion,0) as convertion, " +
//                    "coalesce(i.uom_kecil,'') as uom_kecil, max(priority) as priority, " +
//                    "sum(coalesce(stock,0)) as stock_on_hand, sum(coalesce(stock_in_order,0)) as stock_on_order," +
//                    "coalesce(i.min,0) as min, coalesce(i.max,0) as max " +
//                    "from phar_item i " +
//                    "left join phar_supp_barang sb on sb.kode_barang=i.kode_barang " +
//                    "left join phar_stock s on s.kode_barang=i.kode_barang " +
//                    "where i.kode_barang='"+tblPR.getValueAt(tblPR.getSelectedRow(), 0)+"' " +
//                    "and sb.kode_supplier='"+(tblPR.getValueAt(tblPR.getSelectedRow(), 4)==null? "" : tblPR.getValueAt(tblPR.getSelectedRow(), 4).toString())+"' " +
//                    "group by uom_alt, convertion, uom_kecil, min, max " +
//                    "limit 1";

            String sQry="select * from fn_phar_ket_item_per_site('"+tblPR.getValueAt(tblPR.getSelectedRow(), 0)+"', " +
                    "'') as (uom_alt varchar, convertion integer, uom_kecil varchar, " +
                    "on_hand numeric, on_order numeric, min int, max int)";

            ResultSet rs=conn.createStatement().executeQuery(sQry);
            if(rs.next()){
                lblUomAlt1.setText(rs.getString("uom_alt"));
                lblUomAlt2.setText(rs.getString("uom_alt"));
                lblConv.setText(fn.intFmt.format(rs.getObject("convertion")));
                lblUomKecil.setText(rs.getString("uom_kecil"));
                if(tblPR.getValueAt(tblPR.getSelectedRow(), tblPR.getColumnModel().getColumnIndex("Qty"))!=null)
                    lblConvAlt.setText(fn.dFmt.format(fn.udfGetDouble(tblPR.getValueAt(tblPR.getSelectedRow(), tblPR.getColumnModel().getColumnIndex("Qty"))) /
                        fn.udfGetDouble(lblConv.getText())));

                lblStockOnHand.setText(fn.dFmt.format(rs.getDouble("on_hand")));
                lblQtyOnOrder.setText(fn.dFmt.format(rs.getDouble("on_order")));
                lblMin.setText(fn.dFmt.format(rs.getDouble("min")));
                lblMax.setText(fn.dFmt.format(rs.getDouble("max")));

                double recomendedOrder=fn.udfGetDouble(lblMax.getText())-
                                        fn.udfGetDouble(lblStockOnHand.getText());
                lblrecomendedOrder.setText(fn.dFmt.format((recomendedOrder<0? 0: recomendedOrder)));

            }else
                udfClearKeterangan();

        }catch(SQLException se){
            JOptionPane.showMessageDialog(null, se.getMessage());
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
            int firstRow = e.getFirstRow();
            int lastRow = e.getLastRow();
            int mColIndex = e.getColumn();

            if(conn==null ) return;
            if(mColIndex==0){
                String sKodeBarang=table.getValueAt(table.getSelectedRow(), 0).toString();

                try{
                    String sQry="select pi.kode_barang,nama_barang,uom_kecil, coalesce(ps.kode_supplier,'') as kd_supp, " +
                            "coalesce(uom_kecil,'') as uom_kecil, " +
                            "coalesce(nama_supplier,'') as nama_supplier,price,min,max,coalesce(uom_alt,'') as uom_alt,coalesce(convertion,0) as convertion," +
                            "coalesce(pi.location_id,'') as location_id,location,priority,coalesce(minimum_order,0) as minimum_order," +
                            "case when pi.base_price_type='M' then 'Medical' else 'Suplies' end as item_type " +
                            "from phar_item pi " +
                            "left join phar_supp_barang psb on pi.kode_barang=psb.kode_barang " +
                            "left join phar_supplier ps on ps.kode_supplier=psb.kode_supplier " +
                            "left join phar_location pl on pl.location_id=pi.location_id " +
                            "where pi.kode_barang='"+sKodeBarang+"' " +
                            "order by priority limit 1";

                    ResultSet rs=conn.createStatement().executeQuery(sQry);
                    if(rs.next()){
                        TableColumnModel col=table.getColumnModel();
                        int iRow=table.getSelectedRow();

                        table.setValueAt(rs.getString("nama_barang"), iRow, col.getColumnIndex("Description"));
                        table.setValueAt(rs.getString("uom_kecil"), iRow, col.getColumnIndex("UOM"));
                        table.setValueAt(rs.getDouble("convertion"), iRow, col.getColumnIndex("Qty"));
                        table.setValueAt(rs.getString("kd_supp"), iRow, col.getColumnIndex("Kode Supp."));
                        table.setValueAt(rs.getString("nama_supplier"), iRow, col.getColumnIndex("Supplier"));
                        table.setValueAt(rs.getString("location_id"), iRow, col.getColumnIndex("LocationID"));
                        table.setValueAt(rs.getString("item_type"), iRow, col.getColumnIndex("Item Type"));
                        udfLoadKetBawah();
                        
                    }
                    rs.close();
                }catch(SQLException se){
                    System.err.println(se.getMessage());
                }
            }
//            else if(tblPR.getSelectedColumn()==table.getColumnModel().getColumnIndex("Kode Supp.") &&
//                    mColIndex==table.getColumnModel().getColumnIndex("Kode Supp.")){
//                TableColumnModel col=table.getColumnModel();
//                int iRow=table.getSelectedRow();
//                try{
//                    ResultSet rs=conn.createStatement().executeQuery(
//                            "select coalesce(nama_supplier,'')  from phar_supplier s " +
//                            "where kode_supplier='"+table.getValueAt(table.getSelectedRow(), col.getColumnIndex("Kode Supp."))+"'");
//                    
//                    if(rs.next()){
//                        table.setValueAt(rs.getString(1), iRow, col.getColumnIndex("Supplier"));
//                    }
//                    rs.close();
//                }catch(SQLException se){
//                    JOptionPane.showMessageDialog(JOptionPane.getFrameForComponent(FrmPR.this), se.getMessage());
//                }
//            }
            else if(mColIndex==table.getColumnModel().getColumnIndex("Qty") &&
                    tblPR.getSelectedColumn()==table.getColumnModel().getColumnIndex("Qty")){
                if(tblPR.getSelectedRow()<0) return;
                if(tblPR.getValueAt(tblPR.getSelectedRow(), tblPR.getColumnModel().getColumnIndex("Qty"))!=null)
//                    lblConvAlt.setText(fn.dFmt.format(fn.udfGetDouble(jTable1.getValueAt(jTable1.getSelectedRow(), jTable1.getColumnModel().getColumnIndex("Qty"))) /
//                        fn.udfGetDouble(lblConv.getText())));
                    udfLoadKetBawah();
                
            }
        }
    }

    public void setConn(Connection con){
        this.conn=con;
        fn=new GeneralFunction(conn);

        fn.addKeyListenerInContainer(jPanel1, kListener, txtFocusListener);
        fn.addKeyListenerInContainer(jPanel2, kListener, txtFocusListener);
        //jScrollPane1.addKeyListener(kListener);
        tblPR.addKeyListener(kListener);

    }

    public class MyRowRenderer extends DefaultTableCellRenderer implements TableCellRenderer{
        SimpleDateFormat dmyFmt=new SimpleDateFormat("dd/MM/yyyy");

        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {

            if(value instanceof Date ){
                value=dmyFmt.format(value);
            }else if(value instanceof Double ||value instanceof Integer ||value instanceof Float  ){
                setHorizontalAlignment(SwingConstants.RIGHT);
                value=fn.dFmt.format(value);
            }
            setFont(table.getFont());
            if(isSelected){
                setBackground(table.getSelectionBackground());
                setForeground(table.getSelectionForeground());
                //set
            }else{
                setBackground(table.getBackground());
                setForeground(table.getForeground());
            }

            setValue(value);
            return this;
        }
    }

    private void udfNew(){
        txtPRNo.setText("");
        //txtSite.setText(""); lblSite.setText("");
        txtKeterangan.setText("");
        ((DefaultTableModel)tblPR.getModel()).setNumRows(0);
        btnSave.setEnabled(true);
        btnNew.setEnabled(false);
        btnCancel.setText("Cancel");
        udfClearKeterangan();
        requestFocusInWindow();
        txtSite.requestFocus();
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
        jLabel3 = new javax.swing.JLabel();
        txtReqBy = new javax.swing.JTextField();
        lblReqDate = new javax.swing.JLabel();
        chkOtomatis = new javax.swing.JCheckBox();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        txtSite = new javax.swing.JTextField();
        jLabel7 = new javax.swing.JLabel();
        lblSite = new javax.swing.JLabel();
        jLabel13 = new javax.swing.JLabel();
        jFTimelNeed = new javax.swing.JFormattedTextField();
        jLabel17 = new javax.swing.JLabel();
        txtKeterangan = new javax.swing.JTextField();
        jFDateNeed = new javax.swing.JFormattedTextField();
        chkCito = new javax.swing.JCheckBox();
        cmbItemType = new javax.swing.JComboBox();
        jLabel15 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        tblPR = new javax.swing.JTable();
        jPanel2 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        lblUomAlt1 = new javax.swing.JLabel();
        lblConvAlt = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        lblUomKecil = new javax.swing.JLabel();
        lblUomAlt2 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        lblConv = new javax.swing.JLabel();
        jSeparator1 = new javax.swing.JSeparator();
        jLabel9 = new javax.swing.JLabel();
        lblStockOnHand = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        lblMin = new javax.swing.JLabel();
        jLabel11 = new javax.swing.JLabel();
        lblQtyOnOrder = new javax.swing.JLabel();
        jLabel12 = new javax.swing.JLabel();
        lblrecomendedOrder = new javax.swing.JLabel();
        lblMax = new javax.swing.JLabel();
        jLabel14 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        txtPRNo = new javax.swing.JTextField();
        lblInfoLookupSupp = new javax.swing.JLabel();
        jToolBar1 = new javax.swing.JToolBar();
        btnNew = new javax.swing.JButton();
        btnSave = new javax.swing.JButton();
        btnCancel = new javax.swing.JButton();
        jLabel16 = new javax.swing.JLabel();
        jLabel18 = new javax.swing.JLabel();

        setClosable(true);
        setIconifiable(true);
        setMaximizable(true);
        setResizable(true);
        setTitle("Purchase Requisition");
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
        addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                formFocusGained(evt);
            }
        });

        jPanel1.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        jPanel1.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel3.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jLabel3.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel3.setText("Request By");
        jPanel1.add(jLabel3, new org.netbeans.lib.awtextra.AbsoluteConstraints(460, 40, 80, 20));

        txtReqBy.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
        txtReqBy.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 1, true));
        txtReqBy.setDisabledTextColor(new java.awt.Color(0, 0, 0));
        txtReqBy.setEnabled(false);
        txtReqBy.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                txtReqByFocusLost(evt);
            }
        });
        txtReqBy.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtReqByKeyReleased(evt);
            }
        });
        jPanel1.add(txtReqBy, new org.netbeans.lib.awtextra.AbsoluteConstraints(540, 40, 120, 20));

        lblReqDate.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
        lblReqDate.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 1, true));
        lblReqDate.setEnabled(false);
        lblReqDate.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                lblReqDatePropertyChange(evt);
            }
        });
        jPanel1.add(lblReqDate, new org.netbeans.lib.awtextra.AbsoluteConstraints(100, 70, 80, 20));

        chkOtomatis.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        chkOtomatis.setText("Otomatis");
        chkOtomatis.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                chkOtomatisItemStateChanged(evt);
            }
        });
        jPanel1.add(chkOtomatis, new org.netbeans.lib.awtextra.AbsoluteConstraints(540, 20, 90, 20));

        jLabel4.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jLabel4.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel4.setText("Keterangan");
        jPanel1.add(jLabel4, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 45, 90, 20));

        jLabel5.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jLabel5.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel5.setText("Request Date");
        jPanel1.add(jLabel5, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 70, 90, 20));

        txtSite.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
        txtSite.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 1, true));
        txtSite.setDisabledTextColor(new java.awt.Color(153, 153, 153));
        txtSite.setEnabled(false);
        txtSite.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtSiteKeyReleased(evt);
            }
        });
        jPanel1.add(txtSite, new org.netbeans.lib.awtextra.AbsoluteConstraints(100, 20, 50, 20));

        jLabel7.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jLabel7.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel7.setText("Need Date : ");
        jPanel1.add(jLabel7, new org.netbeans.lib.awtextra.AbsoluteConstraints(180, 70, 90, 20));

        lblSite.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
        lblSite.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 1, true));
        lblSite.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                lblSitePropertyChange(evt);
            }
        });
        jPanel1.add(lblSite, new org.netbeans.lib.awtextra.AbsoluteConstraints(150, 20, 240, 20));

        jLabel13.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jLabel13.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel13.setText("Filter By : ");
        jPanel1.add(jLabel13, new org.netbeans.lib.awtextra.AbsoluteConstraints(550, 70, 75, 20));

        jFTimelNeed.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(51, 51, 51), 1, true));
        jFTimelNeed.setDisabledTextColor(new java.awt.Color(153, 153, 153));
        jFTimelNeed.setFont(new java.awt.Font("Dialog", 1, 12)); // NOI18N
        jFTimelNeed.setSelectedTextColor(new java.awt.Color(255, 255, 0));
        jPanel1.add(jFTimelNeed, new org.netbeans.lib.awtextra.AbsoluteConstraints(460, 70, 60, 20));

        jLabel17.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jLabel17.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel17.setText("Site ID");
        jPanel1.add(jLabel17, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 20, 90, 20));

        txtKeterangan.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        jPanel1.add(txtKeterangan, new org.netbeans.lib.awtextra.AbsoluteConstraints(100, 45, 340, 20));

        jFDateNeed.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(51, 51, 51), 1, true));
        jFDateNeed.setDisabledTextColor(new java.awt.Color(153, 153, 153));
        jFDateNeed.setFont(new java.awt.Font("Dialog", 1, 12)); // NOI18N
        jFDateNeed.setSelectedTextColor(new java.awt.Color(255, 255, 0));
        jPanel1.add(jFDateNeed, new org.netbeans.lib.awtextra.AbsoluteConstraints(270, 70, 90, 20));

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
        jPanel1.add(chkCito, new org.netbeans.lib.awtextra.AbsoluteConstraints(400, 10, 130, 20));

        cmbItemType.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        cmbItemType.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "All", "Medical", "Supplies" }));
        cmbItemType.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmbItemTypeActionPerformed(evt);
            }
        });
        jPanel1.add(cmbItemType, new org.netbeans.lib.awtextra.AbsoluteConstraints(626, 70, 120, -1));

        jLabel15.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jLabel15.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel15.setText("Need Time : ");
        jPanel1.add(jLabel15, new org.netbeans.lib.awtextra.AbsoluteConstraints(365, 70, 90, 20));

        tblPR.setAutoCreateRowSorter(true);
        tblPR.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Product ID", "Description", "UOM", "Qty", "Kd Supp", "Nama Supplier"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Double.class, java.lang.Object.class, java.lang.Object.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, true, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        tblPR.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_OFF);
        tblPR.setSurrendersFocusOnKeystroke(true);
        tblPR.getTableHeader().setReorderingAllowed(false);
        jScrollPane1.setViewportView(tblPR);

        jPanel2.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        jPanel2.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel1.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
        jLabel1.setText("Uom Alt :");
        jPanel2.add(jLabel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(230, 10, 60, 20));

        lblUomAlt1.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
        lblUomAlt1.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 1, true));
        lblUomAlt1.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                lblUomAlt1PropertyChange(evt);
            }
        });
        jPanel2.add(lblUomAlt1, new org.netbeans.lib.awtextra.AbsoluteConstraints(330, 10, 80, 20));

        lblConvAlt.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
        lblConvAlt.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblConvAlt.setText("1");
        lblConvAlt.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 1, true));
        lblConvAlt.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                lblConvAltPropertyChange(evt);
            }
        });
        jPanel2.add(lblConvAlt, new org.netbeans.lib.awtextra.AbsoluteConstraints(290, 10, 40, 20));

        jLabel2.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
        jLabel2.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel2.setText("=");
        jPanel2.add(jLabel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(590, 10, 30, 20));

        lblUomKecil.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
        lblUomKecil.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 1, true));
        lblUomKecil.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                lblUomKecilPropertyChange(evt);
            }
        });
        jPanel2.add(lblUomKecil, new org.netbeans.lib.awtextra.AbsoluteConstraints(680, 10, 60, 20));

        lblUomAlt2.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
        lblUomAlt2.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 1, true));
        lblUomAlt2.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                lblUomAlt2PropertyChange(evt);
            }
        });
        jPanel2.add(lblUomAlt2, new org.netbeans.lib.awtextra.AbsoluteConstraints(530, 10, 60, 20));

        jLabel8.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
        jLabel8.setText("Convertion :");
        jPanel2.add(jLabel8, new org.netbeans.lib.awtextra.AbsoluteConstraints(440, 10, 90, 20));

        lblConv.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
        lblConv.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 1, true));
        lblConv.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                lblConvPropertyChange(evt);
            }
        });
        jPanel2.add(lblConv, new org.netbeans.lib.awtextra.AbsoluteConstraints(620, 10, 60, 20));
        jPanel2.add(jSeparator1, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 40, 730, -1));

        jLabel9.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
        jLabel9.setText("Stock On Hand :");
        jPanel2.add(jLabel9, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 50, 100, 20));

        lblStockOnHand.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
        lblStockOnHand.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lblStockOnHand.setText("0");
        lblStockOnHand.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 1, true));
        lblStockOnHand.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                lblStockOnHandPropertyChange(evt);
            }
        });
        jPanel2.add(lblStockOnHand, new org.netbeans.lib.awtextra.AbsoluteConstraints(110, 50, 120, 20));

        jLabel10.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
        jLabel10.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel10.setText("/");
        jPanel2.add(jLabel10, new org.netbeans.lib.awtextra.AbsoluteConstraints(180, 70, 30, 20));

        lblMin.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
        lblMin.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lblMin.setText("0");
        lblMin.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 1, true));
        lblMin.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                lblMinPropertyChange(evt);
            }
        });
        jPanel2.add(lblMin, new org.netbeans.lib.awtextra.AbsoluteConstraints(110, 70, 70, 20));

        jLabel11.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
        jLabel11.setText("Quantity On Order :");
        jPanel2.add(jLabel11, new org.netbeans.lib.awtextra.AbsoluteConstraints(450, 50, 140, 20));

        lblQtyOnOrder.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
        lblQtyOnOrder.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lblQtyOnOrder.setText("0");
        lblQtyOnOrder.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 1, true));
        lblQtyOnOrder.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                lblQtyOnOrderPropertyChange(evt);
            }
        });
        jPanel2.add(lblQtyOnOrder, new org.netbeans.lib.awtextra.AbsoluteConstraints(590, 50, 120, 20));

        jLabel12.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
        jLabel12.setText("Recomended Order :");
        jPanel2.add(jLabel12, new org.netbeans.lib.awtextra.AbsoluteConstraints(450, 70, 140, 20));

        lblrecomendedOrder.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
        lblrecomendedOrder.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lblrecomendedOrder.setText("0");
        lblrecomendedOrder.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 1, true));
        lblrecomendedOrder.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                lblrecomendedOrderPropertyChange(evt);
            }
        });
        jPanel2.add(lblrecomendedOrder, new org.netbeans.lib.awtextra.AbsoluteConstraints(590, 70, 120, 20));

        lblMax.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
        lblMax.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lblMax.setText("0");
        lblMax.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 1, true));
        lblMax.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                lblMaxPropertyChange(evt);
            }
        });
        jPanel2.add(lblMax, new org.netbeans.lib.awtextra.AbsoluteConstraints(210, 70, 120, 20));

        jLabel14.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
        jLabel14.setText("Stock Min / Max:");
        jPanel2.add(jLabel14, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 70, 100, 20));

        jLabel6.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jLabel6.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel6.setText("PR. No : ");
        jPanel2.add(jLabel6, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 10, 60, 20));

        txtPRNo.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        txtPRNo.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 1, true));
        txtPRNo.setDisabledTextColor(new java.awt.Color(0, 0, 0));
        txtPRNo.setEnabled(false);
        jPanel2.add(txtPRNo, new org.netbeans.lib.awtextra.AbsoluteConstraints(70, 10, 120, 20));

        lblInfoLookupSupp.setForeground(new java.awt.Color(0, 0, 204));

        jToolBar1.setRollover(true);

        btnNew.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/Icon/32/add.png"))); // NOI18N
        btnNew.setText("New");
        btnNew.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        btnNew.setFocusable(false);
        btnNew.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnNew.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnNew.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnNewActionPerformed(evt);
            }
        });
        jToolBar1.add(btnNew);

        btnSave.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/Icon/32/cd package.png"))); // NOI18N
        btnSave.setText("Save");
        btnSave.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        btnSave.setFocusable(false);
        btnSave.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnSave.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnSave.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSaveActionPerformed(evt);
            }
        });
        jToolBar1.add(btnSave);

        btnCancel.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/Icon/32/close.png"))); // NOI18N
        btnCancel.setText("Cancel");
        btnCancel.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        btnCancel.setFocusable(false);
        btnCancel.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnCancel.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnCancel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCancelActionPerformed(evt);
            }
        });
        jToolBar1.add(btnCancel);

        jLabel16.setFont(new java.awt.Font("Tahoma", 0, 36)); // NOI18N
        jLabel16.setForeground(new java.awt.Color(0, 0, 153));
        jLabel16.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel16.setText("Purchase Requisition");

        jLabel18.setForeground(new java.awt.Color(0, 0, 204));
        jLabel18.setText("<html>\n<b>Insert : </b>  &nbsp Tambah Item &nbsp&nbsp \n<b>F3 : </b> &nbsp Edit Item PR &nbsp&nbsp \n<b>F2 : </b> &nbsp Simpan PR\n</html>");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(10, 10, 10)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel18, javax.swing.GroupLayout.PREFERRED_SIZE, 545, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(10, 10, 10)
                        .addComponent(lblInfoLookupSupp, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jScrollPane1)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jToolBar1, javax.swing.GroupLayout.PREFERRED_SIZE, 165, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(145, 145, 145)
                                .addComponent(jLabel16, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, 755, Short.MAX_VALUE))
                        .addGap(1, 1, 1)))
                .addGap(7, 7, 7))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(10, 10, 10)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jToolBar1, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel16, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, 0)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 242, Short.MAX_VALUE)
                .addGap(0, 0, 0)
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, 110, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel18, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblInfoLookupSupp, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(4, 4, 4))
        );

        setBounds(0, 0, 784, 573);
    }// </editor-fold>//GEN-END:initComponents

    private void txtReqByFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtReqByFocusLost
        // TODO add your handling code here:
    }//GEN-LAST:event_txtReqByFocusLost

    private void txtReqByKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtReqByKeyReleased
        // TODO add your handling code here:
    }//GEN-LAST:event_txtReqByKeyReleased

    private void lblReqDatePropertyChange(java.beans.PropertyChangeEvent evt) {//GEN-FIRST:event_lblReqDatePropertyChange
        // TODO add your handling code here:
    }//GEN-LAST:event_lblReqDatePropertyChange

    private void txtSiteKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtSiteKeyReleased
        fn.lookup(evt, new JComponent[]{lblSite},
        "select site_id, coalesce(site_name,'') as site_name from phar_site " +
                "where upper(site_id||coalesce(site_name,'')) Like upper('%" + txtSite.getText() +"%') order by 2",
                txtSite.getWidth()+lblSite.getWidth(), 300);
    }//GEN-LAST:event_txtSiteKeyReleased

    private void lblSitePropertyChange(java.beans.PropertyChangeEvent evt) {//GEN-FIRST:event_lblSitePropertyChange
        // TODO add your handling code here:
    }//GEN-LAST:event_lblSitePropertyChange

    private void formFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_formFocusGained
//        if(tblPR.isFocusOwner()){
//            tblPR.setValueAt(lookupItem.GetKodeBarang(), tblPR.getSelectedRow(), 0);
//        }
    }//GEN-LAST:event_formFocusGained

    private void lblUomAlt1PropertyChange(java.beans.PropertyChangeEvent evt) {//GEN-FIRST:event_lblUomAlt1PropertyChange
        // TODO add your handling code here:
    }//GEN-LAST:event_lblUomAlt1PropertyChange

    private void lblConvAltPropertyChange(java.beans.PropertyChangeEvent evt) {//GEN-FIRST:event_lblConvAltPropertyChange
        // TODO add your handling code here:
    }//GEN-LAST:event_lblConvAltPropertyChange

    private void lblUomKecilPropertyChange(java.beans.PropertyChangeEvent evt) {//GEN-FIRST:event_lblUomKecilPropertyChange
        // TODO add your handling code here:
    }//GEN-LAST:event_lblUomKecilPropertyChange

    private void lblUomAlt2PropertyChange(java.beans.PropertyChangeEvent evt) {//GEN-FIRST:event_lblUomAlt2PropertyChange
        // TODO add your handling code here:
    }//GEN-LAST:event_lblUomAlt2PropertyChange

    private void lblConvPropertyChange(java.beans.PropertyChangeEvent evt) {//GEN-FIRST:event_lblConvPropertyChange
        // TODO add your handling code here:
    }//GEN-LAST:event_lblConvPropertyChange

    private void lblStockOnHandPropertyChange(java.beans.PropertyChangeEvent evt) {//GEN-FIRST:event_lblStockOnHandPropertyChange
        // TODO add your handling code here:
    }//GEN-LAST:event_lblStockOnHandPropertyChange

    private void lblMinPropertyChange(java.beans.PropertyChangeEvent evt) {//GEN-FIRST:event_lblMinPropertyChange
        // TODO add your handling code here:
    }//GEN-LAST:event_lblMinPropertyChange

    private void lblQtyOnOrderPropertyChange(java.beans.PropertyChangeEvent evt) {//GEN-FIRST:event_lblQtyOnOrderPropertyChange
        // TODO add your handling code here:
    }//GEN-LAST:event_lblQtyOnOrderPropertyChange

    private void lblrecomendedOrderPropertyChange(java.beans.PropertyChangeEvent evt) {//GEN-FIRST:event_lblrecomendedOrderPropertyChange
        // TODO add your handling code here:
    }//GEN-LAST:event_lblrecomendedOrderPropertyChange

    private void lblMaxPropertyChange(java.beans.PropertyChangeEvent evt) {//GEN-FIRST:event_lblMaxPropertyChange
        // TODO add your handling code here:
    }//GEN-LAST:event_lblMaxPropertyChange

    private void chkOtomatisItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_chkOtomatisItemStateChanged
         if (!txtSite.getText().trim().equalsIgnoreCase("")){
            if (chkOtomatis.isSelected()){
                initJDBC();
            }else{
                DefaultTableModel mdl=(DefaultTableModel)tblPR.getModel();
                mdl.setRowCount(0);
            }
        }
    }//GEN-LAST:event_chkOtomatisItemStateChanged

    private void formInternalFrameOpened(javax.swing.event.InternalFrameEvent evt) {//GEN-FIRST:event_formInternalFrameOpened
        chkOtomatis.setVisible(!getTitle().equalsIgnoreCase("Purchase Requisition (Tambahan)"));
        MaskFormatter fmtjam = null, fmtTgl=null;
        try {
            fmtjam = new MaskFormatter("##:##");
            fmtTgl = new MaskFormatter("##/##/####");
        } catch (java.text.ParseException e) {}

        try{
            ResultSet rs=conn.createStatement().executeQuery("select current_date, to_char(now(), 'hh24:MI') as jam ");
            rs.next();

            fmtjam.install(jFTimelNeed);
            fmtTgl.install(jFDateNeed);
            jFTimelNeed.setText(rs.getString(2));
            jFDateNeed.setText(new SimpleDateFormat("dd/MM/yyyy").format(rs.getDate(1)));
            lblReqDate.setText(new SimpleDateFormat("dd/MM/yyyy").format(rs.getDate(1)));
            txtReqBy.setText(MainForm.sUserName);
            udfNew();
        }catch(SQLException se){

        }

        txtSite.setText(MainForm.sKodeGudang);
        lblSite.setText(MainForm.sNamaGudang);
        jLabel16.setText(getTitle());
        Runnable doRun = new Runnable() {
            public void run() {
                if(!isKoreksi)
                  chkCito.requestFocusInWindow();
                else{
                  txtPRNo.requestFocusInWindow();
                  btnNew.setEnabled(false);
                  btnSave.setEnabled(true);
                }
            }
        };
        SwingUtilities.invokeLater(doRun);
    }//GEN-LAST:event_formInternalFrameOpened

    private void btnNewActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnNewActionPerformed
        udfNew();
    }//GEN-LAST:event_btnNewActionPerformed

    private void btnSaveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSaveActionPerformed
        udfSave();
    }//GEN-LAST:event_btnSaveActionPerformed

    private void btnCancelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCancelActionPerformed
        if(btnCancel.getText().equalsIgnoreCase("cancel")){
            if(getTitle().indexOf("Koreksi")>0) this.dispose();
            btnSave.setEnabled(false);
            btnNew.setEnabled(true);
            btnCancel.setText("Exit");
        }else{

            this.dispose();
        }
    }//GEN-LAST:event_btnCancelActionPerformed

    private void chkCitoItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_chkCitoItemStateChanged
        // TODO add your handling code here:
}//GEN-LAST:event_chkCitoItemStateChanged

    private void chkCitoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chkCitoActionPerformed
        // TODO add your handling code here:
}//GEN-LAST:event_chkCitoActionPerformed

    private void cmbItemTypeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmbItemTypeActionPerformed
        if(cmbItemType.getSelectedIndex()<0) return;

        String text= cmbItemType.getSelectedIndex()==0? "": cmbItemType.getSelectedItem().toString();
        if(text.length()==0){
            sorter.setRowFilter(null);
        }else{
            try{
                sorter.setRowFilter(RowFilter.regexFilter(text));
            }catch(PatternSyntaxException pse){
                System.err.println("Bad Regex pattern");
            }
        }
    }//GEN-LAST:event_cmbItemTypeActionPerformed

    private void formInternalFrameClosed(javax.swing.event.InternalFrameEvent evt) {//GEN-FIRST:event_formInternalFrameClosed
        //this.winClosed=true;
        if (fn.isListVisible()) {
            fn.setVisibleList(false);
        }
    }//GEN-LAST:event_formInternalFrameClosed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnCancel;
    private javax.swing.JButton btnNew;
    private javax.swing.JButton btnSave;
    private javax.swing.JCheckBox chkCito;
    private javax.swing.JCheckBox chkOtomatis;
    private javax.swing.JComboBox cmbItemType;
    private javax.swing.JFormattedTextField jFDateNeed;
    private javax.swing.JFormattedTextField jFTimelNeed;
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
    private javax.swing.JLabel jLabel2;
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
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JToolBar jToolBar1;
    private javax.swing.JLabel lblConv;
    private javax.swing.JLabel lblConvAlt;
    private javax.swing.JLabel lblInfoLookupSupp;
    private javax.swing.JLabel lblMax;
    private javax.swing.JLabel lblMin;
    private javax.swing.JLabel lblQtyOnOrder;
    private javax.swing.JLabel lblReqDate;
    private javax.swing.JLabel lblSite;
    private javax.swing.JLabel lblStockOnHand;
    private javax.swing.JLabel lblUomAlt1;
    private javax.swing.JLabel lblUomAlt2;
    private javax.swing.JLabel lblUomKecil;
    private javax.swing.JLabel lblrecomendedOrder;
    private javax.swing.JTable tblPR;
    private javax.swing.JTextField txtKeterangan;
    private javax.swing.JTextField txtPRNo;
    private javax.swing.JTextField txtReqBy;
    private javax.swing.JTextField txtSite;
    // End of variables declaration//GEN-END:variables

    private boolean udfCekBeforeSave(){
        if(txtSite.getText().trim().length()==0){
            JOptionPane.showMessageDialog(this, "Silakan isi Site Id Terlebih Dahulu!");
            txtSite.requestFocus();
            return false;
        }
        if(tblPR.getRowCount()==0){
            JOptionPane.showMessageDialog(this, "Detail item masih kosong!");
            tblPR.requestFocus();
            return false;
        }
        if(isKoreksi && sudahAdaPO){
            if(JOptionPane.showConfirmDialog(this, "PR ini sudah dibuatkan PO. Anda ingin tetap melanjutkan?!", "Konfirmasi Koreksi", JOptionPane.YES_NO_OPTION)==JOptionPane.NO_OPTION){
                txtPRNo.requestFocus();
                return false;
            }

        }
        return true;
    }

    private void udfSave(){
        if(!udfCekBeforeSave()) return;
        String sIns="", sDet="";
        try{
            conn.setAutoCommit(false);
            ResultSet rs=null;
            String sOldPo=txtPRNo.getText();

            if(isKoreksi){
                rs=conn.createStatement().executeQuery("select fn_phar_pr_koreksi('"+sOldPo+"', '"+MainForm.sUserID+"', "
                        + "'"+MainForm.sUserName+"') ");
                if(rs.next()){

                }
            }

            rs=conn.createStatement().executeQuery("select fn_phar_get_no_pr('"+MainForm.sUserID+"') as no_pr");
            if(rs.next()) txtPRNo.setText(rs.getString(1));
            
            
            sIns="insert into phar_pr(no_pr, site_id, requested_by, automatic, need_date, " +
                    "user_ins, keterangan, cito, flag_tambahan)" +
                    "values(?, ?, ?,?,?,"
                    + "?,?,?,?); ";
            PreparedStatement ps=conn.prepareStatement(sIns);
            ps.setString(1, txtPRNo.getText());
            ps.setString(2, txtSite.getText());
            ps.setString(3, txtReqBy.getText());
            ps.setBoolean(4, chkOtomatis.isSelected());
            ps.setDate(5, new java.sql.Date(new SimpleDateFormat("dd/MM/yyyy hh:mm").parse(jFDateNeed.getText()+" "+jFTimelNeed.getText()).getTime()));
            ps.setString(6, MainForm.sUserName);
            ps.setString(7, txtKeterangan.getText());
            ps.setBoolean(8, chkCito.isSelected());
            ps.setBoolean(9, (getTitle().toUpperCase().indexOf("TAMBAHAN")>0));
            ps.executeUpdate();
            ps.close();
            
            TableColumnModel col=tblPR.getColumnModel();
            for(int i=0; i< tblPR.getRowCount(); i++){
                sDet+="insert into phar_pr_detail(no_pr, kode_barang, jumlah, uom) values(" +
                        "'"+txtPRNo.getText()+"', '"+tblPR.getValueAt(i, col.getColumnIndex("Product ID"))+"', " +
                        ""+fn.udfGetDouble(tblPR.getValueAt(i, col.getColumnIndex("Qty")))+", " +
                        "'"+tblPR.getValueAt(i, col.getColumnIndex("UOM")).toString()+"'); ";

            }
            System.out.println(sDet);
            
            int i=conn.createStatement().executeUpdate(sDet);
//            if(isKoreksi) sDet+="update phar_po_detail set no_pr='"+txtPRNo.getText()+"' where no_pr='"+sOldPo+"'";
            conn.setAutoCommit(true);
            if(i>0){
                String sMessage=(isKoreksi? "Koreksi No. PR :'"+sOldPo+"' sukses, dengan No. PR baru '"+txtPRNo.getText()+"'": "Simpan PR Sukses");
                JOptionPane.showMessageDialog(this, sMessage);
                udfNew();
                return;
            }

        } catch (ParseException se) {
            JOptionPane.showMessageDialog(this, se.getMessage());
        }catch(SQLException ex){
            try {
                conn.rollback();
                conn.setAutoCommit(true);
                JOptionPane.showMessageDialog(this, ex.getMessage());
            } catch (SQLException ex1) {
                Logger.getLogger(FrmPR.class.getName()).log(Level.SEVERE, null, ex1);
            }
        }
    }

    public void initJDBC() {
       setCursor(new Cursor(Cursor.WAIT_CURSOR));
        try {
//            String sQry="select * from fn_phar_create_pr("+chkOtomatis.isSelected()+") as(kode_barang varchar,nama_barang varchar,uom_kecil varchar," +
//                 "kode_supplier varchar,nama_supplier varchar,uom_alt varchar,convertion numeric," +
//                 "stock numeric,stock_in_order numeric,min float4,max float4,location_id varchar,location varchar)";
             String sQry ="SELECT kode_barang,nama_barang, uom_kecil, \n" +
                        "qty,kode_supplier,nama_supplier, uom_alt,convertion,stock, \n" +
                        "stock_in_order,min,max\n" +
                        "FROM fn_phar_pr_create() as \n" +
                        "(kode_barang varchar,nama_barang varchar, uom_kecil varchar, \n" +
                        "qty double precision, kode_supplier varchar, nama_supplier varchar, uom_alt varchar, \n" +
                        "convertion integer,stock double precision,stock_in_order numeric,min int,max int)";
            ResultSet rs = conn.createStatement().executeQuery(sQry);
           // System.out.println(sQry);
            int ii=0;
            while (rs.next()) {
                //Double fqty=Math.ceil((rs.getDouble("max")-rs.getDouble("stock")-rs.getDouble("stock_in_order"))/rs.getDouble("convertion"));
                //Double fqty=Math.ceil((rs.getDouble("max")-rs.getDouble("stock"))/rs.getDouble("convertion"));
                //if (fqty>0){
                    ((DefaultTableModel)tblPR.getModel()).addRow(new Object[]{
                        rs.getString("kode_barang"),
                        rs.getString("nama_barang"),
                        rs.getString("uom_kecil"),
                        rs.getDouble("qty"),
                        rs.getString("kode_supplier"),
                        rs.getString("nama_supplier")
                    });
                //}
                ii++;
            }
            rs.close();
            tblPR.setRequestFocusEnabled(true);

            if (ii > 0) {
                tblPR.setRowSelectionInterval(0, 0);
            }else{
                ((DefaultTableModel)tblPR.getModel()).setRowCount(0);
                
            }
            setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
        } catch(SQLException se) {
            setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
            JOptionPane.showMessageDialog(this, se.getMessage());
        }
    }

    public class MyKeyListener extends KeyAdapter {
        @Override
        public void keyReleased(KeyEvent evt){

        }

        @Override
        public void keyPressed(KeyEvent evt) {
            Component ct = KeyboardFocusManager.getCurrentKeyboardFocusManager().getPermanentFocusOwner();
            int keyKode = evt.getKeyCode();
            switch(keyKode){
                case KeyEvent.VK_INSERT:{
                    DlgLookup d1=new DlgLookup(JOptionPane.getFrameForComponent(FrmPR.this), true);
                    String sItem="";
                    for(int i=0; i< tblPR.getRowCount(); i++){
                        sItem+=(sItem.length()==0? "" : ",") +"'"+tblPR.getValueAt(i, 0).toString()+"'";
                    }

                    String s="select * from (" +
                            "select item_code as kode_barang, coalesce(nama_paten,'') as nama_barang, "
                            + "coalesce(satuan_kecil,'') as satuan_kecil, coalesce(sb.kode_supplier,'') as kode_supplier, "
                            + "coalesce(s.nama_supplier,'') as nama_supplier from " +
                            "barang b "
                            + "left join supplier_barang sb on sb.kode_barang=b.item_code and sb.priority=1 "
                            + "left join phar_supplier s on s.kode_supplier=sb.kode_supplier "+
                            (sItem.length()>0? "where item_code not in("+sItem+")":"")+" order by 2) x ";

                    //System.out.println(s);
//                    ((DefaultTableModel)tblPR.getModel()).setNumRows(tblPR.getRowCount()+1);
//                    tblPR.setRowSelectionInterval(tblPR.getRowCount()-1, tblPR.getRowCount()-1);
                    d1.setTitle("Lookup Barang");
                    d1.udfLoad(conn, s, "(kode_barang||nama_barang)", null);

                    d1.setVisible(true);

                    //System.out.println("Kode yang dipilih" +d1.getKode());
                    if(d1.getKode().length()>0){
                        TableColumnModel col=d1.getTable().getColumnModel();
                        JTable tbl=d1.getTable();
                        int iRow = tbl.getSelectedRow();

                        ((DefaultTableModel)tblPR.getModel()).addRow(new Object[]{
                            tbl.getValueAt(iRow, col.getColumnIndex("kode_barang")).toString(),
                            tbl.getValueAt(iRow, col.getColumnIndex("nama_barang")).toString(),
                            tbl.getValueAt(iRow, col.getColumnIndex("satuan_kecil")).toString(),
                            0,
                            tbl.getValueAt(iRow, col.getColumnIndex("kode_supplier")).toString(),
                            tbl.getValueAt(iRow, col.getColumnIndex("nama_supplier")).toString(),
                        });

                        tblPR.setRowSelectionInterval(tblPR.getRowCount()-1, tblPR.getRowCount()-1);
                        tblPR.changeSelection(tblPR.getRowCount()-1, tblPR.getColumnModel().getColumnIndex("Qty"), false, false);
                        tblPR.requestFocusInWindow();
                    break;
                }
                }
//                case KeyEvent.VK_F3:{
//                    if(evt.getSource().equals(tblPR) && tblPR.getSelectedRow()>=0 && tblPR.getValueAt(tblPR.getSelectedRow(), 0)!=null){
//                        lookupItem.setSrcTable(tblPR, tblPR.getColumnModel().getColumnIndex("Qty"));
//                        lookupItem.setVisible(true);
//                    }
//                    break;
//                }
                case KeyEvent.VK_F9:{  //Update supplier
                    if(evt.getSource().equals(tblPR) && (tblPR.getSelectedColumn()==tblPR.getColumnModel().getColumnIndex("Kd Supp") || tblPR.getSelectedColumn()==tblPR.getColumnModel().getColumnIndex("Nama Supplier"))){
                        DLgLookup d1=new DLgLookup(JOptionPane.getFrameForComponent(FrmPR.this), true);
                        d1.setTitle("Lookup Supplier");
                        d1.udfLoad(conn,
                                "select * from(" +
                                "select s.kode_supplier, coalesce(nama_supplier,'') as nama_supplier, " +
                                "coalesce(telp,'') as telp, coalesce(alamat,'') as alamat "
                                + "from phar_supplier s " +
                                "inner join supplier_barang sb on sb.kode_supplier=s.kode_supplier "
                                + "where s.active=true and " +
                                "kode_barang='"+tblPR.getValueAt(tblPR.getSelectedRow(), 0).toString()+"') x ",
                                "(kode_supplier||coalesce(nama_supplier,'')||coalesce(telp,'')||coalesce(alamat,''))",
                                tblPR);

                        d1.setVisible(true);
                        return;
                    }
                    break;
                }
               case KeyEvent.VK_ENTER : {
                    if(!(ct instanceof JTable))                    {
                        if (!fn.isListVisible()){
                            Component c = findNextFocus();
                            if (c==null) return;
                            c.requestFocus();
                        }else{
                            fn.lstRequestFocus();
                        }
                    }else{
//                        if(jTable1.getSelectedColumn()<jTable1.getColumnCount()-1){
//                            jTable1.changeSelection(jTable1.getSelectedRow(), jTable1.getSelectedColumn()+1, false, false);
//                        }
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
                    if(!(ct.getClass().getSimpleName().equalsIgnoreCase("JTABLE")))
                    {
                        Component c = findPrevFocus();
                        c.requestFocus();
                    }
                    break;
                }
                case KeyEvent.VK_DELETE:{
                    if(evt.getSource().equals(tblPR) && tblPR.getSelectedRow()>=0){
                        int iRow[]= tblPR.getSelectedRows();
                        int rowPalingAtas=iRow[0];

//                        for (int a=0; a<iRow.length; a++){
//                            if((a==iRow.length-1? tblPR.getSelectedRow()+1 : tblPR.getSelectedRow())>=0)
//                                //((DefaultTableModel)tblPR.getModel()).removeRow(tblPR.getSelectedRow());
//                                ((DefaultTableModel)tblPR.getModel()).removeRow(
//                                        a==iRow.length-1? tblPR.getSelectedRow()+1 : tblPR.getSelectedRow());
//                        }

                        TableModel tm= tblPR.getModel();

                        while(iRow.length>0) {
                            //JOptionPane.showMessageDialog(null, iRow[0]);
                            ((DefaultTableModel)tm).removeRow(tblPR.convertRowIndexToModel(iRow[0]));
                            iRow = tblPR.getSelectedRows();
                        }
                        tblPR.clearSelection();

                        if(tblPR.getRowCount()>0 && rowPalingAtas<tblPR.getRowCount()){
                            tblPR.setRowSelectionInterval(rowPalingAtas, rowPalingAtas);
                        }else{
                            if(tblPR.getRowCount()>0)
                                tblPR.setRowSelectionInterval(rowPalingAtas-1, rowPalingAtas-1);
                            else
                                tblPR.requestFocus();
                        }
                        if(tblPR.getSelectedRow()>=0)
                            tblPR.changeSelection(tblPR.getSelectedRow(), 0, false, false);
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

           text.addKeyListener(new java.awt.event.KeyAdapter() {
                   public void keyTyped(java.awt.event.KeyEvent evt) {
                      if (col!=0) {
                          char c = evt.getKeyChar();
                          if (!((c >= '0' && c <= '9')) &&
                                (c != KeyEvent.VK_BACK_SPACE) &&
                                (c != KeyEvent.VK_DELETE) &&
                                (c != KeyEvent.VK_ENTER)) {
                                getToolkit().beep();
                                evt.consume();
                                return;
                          }
                       }
                    }
                });
           if (isSelected) {

           }
           //System.out.println("Value dari editor :"+value);
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
                retVal = fn.udfGetDouble(((JTextField)text).getText());

                if(col==tblPR.getColumnModel().getColumnIndex("Qty")){
                    double sisaBagi= fn.udfGetDouble(((JTextField)text).getText())%fn.udfGetDouble(lblConv.getText());

                    if(sisaBagi>0){
                        JOptionPane.showMessageDialog(FrmPR.this, "Qty PR harus bulat sesuai dengan konversi");
                        o=(fn.udfGetDouble(tblPR.getValueAt(row, tblPR.getColumnModel().getColumnIndex("Qty"))));
                        tblPR.changeSelection(row, col, false, false);
                        return o;
                    }
                }
                o=(retVal);

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

     private FocusListener txtFocusListener=new FocusListener() {
        public void focusGained(FocusEvent e) {
            if(e.getSource() instanceof JTextField || e.getSource() instanceof JFormattedTextField){
                ((JTextField)e.getSource()).setBackground(Color.YELLOW);
////
//                if(e.getSource().equals(ustTextField)||e.getSource().equals(txtUnitPrice)||e.getSource().equals(txtDisc)){
                    ((JTextField)e.getSource()).setSelectionStart(0);
                    ((JTextField)e.getSource()).setSelectionEnd(((JTextField)e.getSource()).getText().length());
//                }
//                else if(e.getSource().equals(txtDokter)){
//////                    txtDokter.setEnabled(isJasaMedis);
////                }
            }
        }

        public void focusLost(FocusEvent e) {
            if(e.getSource().getClass().getSimpleName().equalsIgnoreCase("JTextField")||
                    e.getSource().getClass().getSimpleName().equalsIgnoreCase("JFormattedTextField")){
                ((JTextField)e.getSource()).setBackground(Color.WHITE);
                if(e.getSource().equals(txtPRNo) && isKoreksi){
                    udfLoadPRKoreksi();
                }

            }
        }
    } ;

    private void udfClearKeterangan(){
        txtPRNo.setText(""); lblConvAlt.setText(""); lblUomAlt1.setText("");
        lblUomAlt2.setText(""); lblConv.setText(""); lblUomKecil.setText("");
        lblStockOnHand.setText(""); lblMin.setText(""); lblMax.setText("");
        lblQtyOnOrder.setText("");
        lblrecomendedOrder.setText("");
        
    }
}
