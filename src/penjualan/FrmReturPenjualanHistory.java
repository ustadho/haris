/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * FrmPRApproval.java
 *
 * Created on Jul 29, 2010, 11:51:31 AM
 */

package penjualan;

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
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JCheckBox;
import javax.swing.JFormattedTextField;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.text.MaskFormatter;
import main.GeneralFunction;
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
public class FrmReturPenjualanHistory extends javax.swing.JInternalFrame {
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
    private Object objMain;

    /** Creates new form FrmPRApproval */
    public FrmReturPenjualanHistory(Connection con) {
        initComponents();
        this.conn=con;
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
        btnPreview.setEnabled(false);
        btnKoreksi.setEnabled(false);

//        fmttgl.install(jFTgl);
//        fmttgl.install(jFTglAkhir);

        tblHeader.getSelectionModel().addListSelectionListener(new ListSelectionListener() {

            public void valueChanged(ListSelectionEvent e) {
                btnPreview.setEnabled(tblHeader.getSelectedRow()>=0 );
                btnKoreksi.setEnabled(tblHeader.getSelectedRow()>=0 );
                btnDelete.setEnabled(tblHeader.getSelectedRow()>=0 );
                ((DefaultTableModel)tblDetail.getModel()).setNumRows(0);
                if(conn!=null && tblHeader.getSelectedRow()<0) return;
                udfLoadReturDetail();
                
            }
        });


//        tblDetail.getModel().addTableModelListener(new TableModelListener() {
//            public void tableChanged(TableModelEvent e) {
//                TableColumnModel col=tblDetail.getColumnModel();
//                if(e.getColumn()==col.getColumnIndex("Qty")){
//                    int iRow=tblDetail.getSelectedRow();
//                    double jmlKecil=fn.udfGetDouble(tblDetail.getValueAt(iRow, col.getColumnIndex("Qty")))*
//                            fn.udfGetDouble(tblDetail.getValueAt(iRow, col.getColumnIndex("Conv")));
//                    double extPrice=fn.udfGetDouble(tblDetail.getValueAt(iRow, col.getColumnIndex("Qty")))*
//                            fn.udfGetDouble(tblDetail.getValueAt(iRow, col.getColumnIndex("Unit Price")));
//                    extPrice=extPrice-(extPrice/100)* fn.udfGetDouble(tblDetail.getValueAt(iRow, col.getColumnIndex("Disc %")));
//
//                    ((DefaultTableModel)tblDetail.getModel()).setValueAt(Math.floor(extPrice),
//                            iRow, col.getColumnIndex("Ext. Price"));
//                    ((DefaultTableModel)tblDetail.getModel()).setValueAt(jmlKecil, iRow, col.getColumnIndex("JmlKecil"));
//
//                }
//                if(tblDetail.getRowCount()>0){
//                    double totLine=0, totVat=0;
//
//                    for(int i=0; i< tblDetail.getRowCount(); i++){
//                        if(e.getType()==TableModelEvent.DELETE)
//                            ((DefaultTableModel)tblDetail.getModel()).setValueAt(i+1, i, col.getColumnIndex("No."));
//
//                        totLine+=fn.udfGetDouble(tblDetail.getValueAt(i, col.getColumnIndex("Ext. Price")));
//                        totVat+=fn.udfGetDouble(tblDetail.getValueAt(i, col.getColumnIndex("Ext. Price")))/100*fn.udfGetDouble(tblDetail.getValueAt(i, col.getColumnIndex("Vat")));
//                    }
//
//                    txtTotalLine.setText(fn.dFmt.format(Math.floor(totLine)));
//                    txtTotVat.setText(fn.dFmt.format(Math.floor(totVat)));
//                    txtNetto.setText(fn.dFmt.format(Math.floor(totLine+totVat-fn.udfGetDouble(txtDiscRp.getText()))));
//
//                }else{
//                    txtTotalLine.setText("0");
//                    txtTotVat.setText("0");
//                    txtDiscRp.setText("0");
//                    txtDiscPersen.setText("0");
//                    txtNetto.setText("0");
//                }
//            }
//        });
        udfInitForm();
        tblDetail.setRowHeight(25);
//        tblDetail.getColumn("Conv").setMinWidth(0); tblDetail.getColumn("Conv").setMaxWidth(0); tblDetail.getColumn("Conv").setPreferredWidth(0);
//        tblDetail.getColumn("UomKecil").setMinWidth(0); tblDetail.getColumn("UomKecil").setMaxWidth(0); tblDetail.getColumn("UomKecil").setPreferredWidth(0);
//        tblDetail.getColumn("JmlKecil").setMinWidth(0); tblDetail.getColumn("JmlKecil").setMaxWidth(0); tblDetail.getColumn("JmlKecil").setPreferredWidth(0);
//        tblDetail.getColumn("SisaPR").setMinWidth(0); tblDetail.getColumn("SisaPR").setMaxWidth(0); tblDetail.getColumn("SisaPR").setPreferredWidth(0);
    }

    private void udfLoadReturDetail(){
        int iRow=tblHeader.getSelectedRow();
        String sNoRetur=tblHeader.getValueAt(iRow, tblHeader.getColumnModel().getColumnIndex("Retur #")).toString();

        String s ="select d.kode_item, coalesce(i.nama_paten,'') as nama_item, " +
                "coalesce(d.qty,0) as qty, coalesce(i.satuan_kecil,'') as unit, coalesce(d.unit_Price,0) as unit_price," +
                "coalesce(d.unit_Price,0) * coalesce(d.qty,0) as sub_total " +
                "from retur_jual_Detail d " +
                "inner join barang i on i.item_code=d.kode_item " +
                "where d.no_retur='"+sNoRetur+"'";

        //System.out.println(s);
        try{
            ((DefaultTableModel)tblDetail.getModel()).setNumRows(0);

            ResultSet rs=conn.createStatement().executeQuery(s);
            while(rs.next()){
                ((DefaultTableModel)tblDetail.getModel()).addRow(new Object[]{
                    ((DefaultTableModel)tblDetail.getModel()).getRowCount()+1,
                    rs.getString("kode_item"),
                    rs.getString("nama_item"),
                    rs.getString("unit"),
                    rs.getDouble("qty"),
                    rs.getDouble("unit_price"),
                    rs.getDouble("sub_total")
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

    public void setCumaSingOutstanding(boolean b){
        cumaSingOutstanding=b;
        btnPreview.setVisible(!b);
        
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
            tblHeader.getTableHeader().setReorderingAllowed(false);

            for(int i=0; i< tblHeader.getColumnCount(); i++){
                tblHeader.getColumnModel().getColumn(i).setCellRenderer(new MyRowRenderer());
            }

            tblHeader.setRowHeight(22);
            tblDetail.setRowHeight(22);
            tblHeader.getColumn("Retur #").setPreferredWidth(100);
            tblHeader.getColumn("Nama Pelanggan").setPreferredWidth(130);
            tblHeader.getColumn("Tanggal").setPreferredWidth(110);
            
        } catch (SQLException ex) {
            Logger.getLogger(FrmReturPenjualanHistory.class.getName()).log(Level.SEVERE, null, ex);
        }

        //jLabel16.setText(getTitle());
        udfLoadRetur();
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                jFTgl.requestFocus();
            }
        });
    }

    public void udfLoadRetur(){
        try{
            SimpleDateFormat dmy = new SimpleDateFormat("dd/MM/yyyy");
            SimpleDateFormat ymd = new SimpleDateFormat("yyyy-MM-dd");
            String sQry="select distinct r.no_retur, r.kode_cust, coalesce(c.nama_pelanggan, '') as nama_cust, r.tanggal, " +
                    "r.date_ins , coalesce(r.keterangan,'') as remark " +
                    "from retur_jual r " +
                    "inner join retur_jual_detail d on d.no_retur=r.no_retur " +
                    "left join pelanggan c on c.kode_pelanggan=r.kode_cust " +
                    "left join barang i on i.item_code=d.kode_item " +
                    "where r.flag_Trx='T' " +
                    "and coalesce(i.item_name,'')||d.kode_item ilike '%"+txtItem.getText()+"%' " +
                    "and coalesce(c.nama_pelanggan,'')||r.kode_cust||r.no_retur ilike '%"+txtSupplier.getText()+"%' " +
                    "and to_char(r.tanggal, 'yyyy-MM-dd')>='" + ymd.format(dmy.parse(jFTgl.getText())) + "' " +
                    "and to_char(r.tanggal, 'yyyy-MM-dd')<='" + ymd.format(dmy.parse(jFTglAkhir.getText())) + "' " +
                    "order by r.tanggal desc";

            System.out.println(sQry);
            
            setCursor(new Cursor(Cursor.WAIT_CURSOR));
            try {
                ResultSet rs = conn.createStatement().executeQuery(sQry);
                ((DefaultTableModel) tblHeader.getModel()).setNumRows(0);
                while (rs.next()) {
                    ((DefaultTableModel) tblHeader.getModel()).addRow(new Object[]{
                        rs.getString("no_retur"),
                        rs.getTimestamp("date_ins"),
                        rs.getString("kode_cust"),
                        rs.getString("nama_cust"),
                        rs.getString("remark"),//rs.getBoolean("acc_level3"),
                        
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
            Logger.getLogger(FrmReturPenjualanHistory.class.getName()).log(Level.SEVERE, null,ex);
        }
    }

    SimpleDateFormat dmyFmt=new SimpleDateFormat("dd/MM/yyyy");
    SimpleDateFormat dmyFmt_hhmm=new SimpleDateFormat("dd/MM/yyyy hh:mm");

    private void udfSave() {
        String sUpdate="";
        String sUpd1="", sUpd2="", sUpd3="";

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
                Logger.getLogger(FrmReturPenjualanHistory.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    public void setMainForm(Object aThis) {
        this.objMain=aThis;
    }

    private void udfBatalRetur() {
        int i=tblHeader.getSelectedRow();
        if(i<0) return;
        if(JOptionPane.showConfirmDialog(this, "Anda yakin untuk membatalkan retur ini?", "Batal Retur", JOptionPane.YES_NO_OPTION)==JOptionPane.NO_OPTION) return;
        try{
            conn.setAutoCommit(false);
            String sNoRetur=tblHeader.getValueAt(i, tblHeader.getColumnModel().getColumnIndex("Retur #")).toString();
            ResultSet rs=conn.createStatement().executeQuery("select fn_retur_jual_koreksi('"+sNoRetur+"', '"+MainForm.sUserName+"')");
            if(rs.next()){
                JOptionPane.showMessageDialog(this, "Transaksi dikoreksi dengan nomor '"+rs.getString(1)+"'");
                udfLoadRetur();
            }
            rs.close();
            conn.setAutoCommit(true);
        }catch(SQLException se){
            try {
                conn.rollback();
                JOptionPane.showMessageDialog(this, se.getMessage(), "udfBatalRetur", JOptionPane.ERROR_MESSAGE);
            } catch (SQLException ex) {
                Logger.getLogger(FrmReturPenjualanHistory.class.getName()).log(Level.SEVERE, null, ex);
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
//                if((Boolean)table.getValueAt(row, 8)==true){
//                    setBackground(lblPRCito.getBackground());
//                    setForeground(table.getForeground());
//                }else if(table.getValueAt(row, tblHeader.getColumnModel().getColumnIndex("Last Print"))!=null){
//                    setBackground(lblPRPrinted.getBackground());
//                    setForeground(table.getForeground());
//                } else{
                    setBackground(table.getBackground());
                    setForeground(table.getForeground());
//                }
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
                //checkBox.setHorizontalAlignment(lblPRCito.CENTER);
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
        txtSupplier = new javax.swing.JTextField();
        btnKoreksi = new javax.swing.JButton();
        btnDelete = new javax.swing.JButton();
        jXTitledPanel1 = new org.jdesktop.swingx.JXTitledPanel();
        jScrollPane3 = new javax.swing.JScrollPane();
        tblDetail = new org.jdesktop.swingx.JXTable();
        jScrollPane1 = new javax.swing.JScrollPane();
        tblHeader = new javax.swing.JTable();
        jLabel1 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();

        setClosable(true);
        setIconifiable(true);
        setMaximizable(true);
        setResizable(true);
        setTitle("Sales Return History"); // NOI18N
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
        btnClose.setText("Tutup");
        btnClose.setToolTipText("Tutup");
        btnClose.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnClose.setMargin(new java.awt.Insets(1, 1, 1, 1));
        btnClose.setMaximumSize(new java.awt.Dimension(40, 40));
        btnClose.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnClose.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCloseActionPerformed(evt);
            }
        });
        jPanel1.add(btnClose, new org.netbeans.lib.awtextra.AbsoluteConstraints(790, 10, 60, 60));

        btnPreview.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/Icon/buttons/print-32.png"))); // NOI18N
        btnPreview.setText("Cetak");
        btnPreview.setToolTipText("Cetak Retur");
        btnPreview.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnPreview.setMargin(new java.awt.Insets(1, 1, 1, 1));
        btnPreview.setMaximumSize(new java.awt.Dimension(40, 40));
        btnPreview.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnPreview.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnPreviewActionPerformed(evt);
            }
        });
        jPanel1.add(btnPreview, new org.netbeans.lib.awtextra.AbsoluteConstraints(610, 10, 60, 60));

        jLabel16.setFont(new java.awt.Font("Tahoma", 0, 24)); // NOI18N
        jLabel16.setForeground(new java.awt.Color(0, 0, 153));
        jLabel16.setText(" Histori Retur Penjualan");
        jPanel1.add(jLabel16, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 330, 30));

        jFTgl.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 51, 153), 1, true));
        jFTgl.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jPanel1.add(jFTgl, new org.netbeans.lib.awtextra.AbsoluteConstraints(170, 60, 80, 20));

        jLabel2.setBackground(new java.awt.Color(204, 204, 255));
        jLabel2.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jLabel2.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel2.setText("Item");
        jLabel2.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        jLabel2.setOpaque(true);
        jPanel1.add(jLabel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(330, 45, 70, -1));

        jFTglAkhir.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 51, 153), 1, true));
        jFTglAkhir.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jPanel1.add(jFTglAkhir, new org.netbeans.lib.awtextra.AbsoluteConstraints(250, 60, 80, 20));

        btnFilter1.setText("Load");
        btnFilter1.setToolTipText("Fiter");
        btnFilter1.setMargin(new java.awt.Insets(2, 2, 2, 2));
        btnFilter1.setMaximumSize(new java.awt.Dimension(40, 40));
        btnFilter1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnFilter1ActionPerformed(evt);
            }
        });
        jPanel1.add(btnFilter1, new org.netbeans.lib.awtextra.AbsoluteConstraints(530, 60, 70, 25));

        jLabel8.setBackground(new java.awt.Color(204, 204, 255));
        jLabel8.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jLabel8.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel8.setText("From");
        jLabel8.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        jLabel8.setOpaque(true);
        jPanel1.add(jLabel8, new org.netbeans.lib.awtextra.AbsoluteConstraints(170, 45, 80, -1));

        jLabel9.setBackground(new java.awt.Color(204, 204, 255));
        jLabel9.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jLabel9.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel9.setText("To");
        jLabel9.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        jLabel9.setOpaque(true);
        jPanel1.add(jLabel9, new org.netbeans.lib.awtextra.AbsoluteConstraints(250, 45, 80, -1));

        txtItem.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        txtItem.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        jPanel1.add(txtItem, new org.netbeans.lib.awtextra.AbsoluteConstraints(330, 60, 70, 20));

        jLabel10.setBackground(new java.awt.Color(204, 204, 255));
        jLabel10.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jLabel10.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel10.setText("Pelanggan/ Retur No.");
        jLabel10.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        jLabel10.setOpaque(true);
        jPanel1.add(jLabel10, new org.netbeans.lib.awtextra.AbsoluteConstraints(400, 45, 120, -1));

        txtSupplier.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        txtSupplier.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        jPanel1.add(txtSupplier, new org.netbeans.lib.awtextra.AbsoluteConstraints(400, 60, 120, 20));

        btnKoreksi.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/Icon/buttons/edit-32.png"))); // NOI18N
        btnKoreksi.setText("Ubah");
        btnKoreksi.setToolTipText("Koreksi Retur");
        btnKoreksi.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnKoreksi.setMargin(new java.awt.Insets(1, 1, 1, 1));
        btnKoreksi.setMaximumSize(new java.awt.Dimension(40, 40));
        btnKoreksi.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnKoreksi.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnKoreksiActionPerformed(evt);
            }
        });
        jPanel1.add(btnKoreksi, new org.netbeans.lib.awtextra.AbsoluteConstraints(670, 10, 60, 60));

        btnDelete.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/Icon/buttons/delete-32.png"))); // NOI18N
        btnDelete.setText("Hapus");
        btnDelete.setToolTipText("Batalkan Retur");
        btnDelete.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnDelete.setMargin(new java.awt.Insets(1, 1, 1, 1));
        btnDelete.setMaximumSize(new java.awt.Dimension(40, 40));
        btnDelete.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnDelete.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnDeleteActionPerformed(evt);
            }
        });
        jPanel1.add(btnDelete, new org.netbeans.lib.awtextra.AbsoluteConstraints(730, 10, -1, 60));

        jXTitledPanel1.setTitle("Item Detail");
        jXTitledPanel1.setTitleFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jXTitledPanel1.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jXTitledPanel1.getContentContainer().setLayout(new javax.swing.BoxLayout(jXTitledPanel1.getContentContainer(), javax.swing.BoxLayout.LINE_AXIS));

        tblDetail.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "No.", "Product ID", "Keterangan", "Satuan", "Qty", "Harga Sat", "Sub Total"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Double.class, java.lang.Double.class, java.lang.Double.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false, false
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
                "Retur #", "Tanggal", "ID Pelanggan", "Nama Pelanggan", "Remark"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false
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

        jLabel1.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel1.setForeground(new java.awt.Color(0, 0, 102));
        jLabel1.setText("F11  :");

        jLabel3.setForeground(new java.awt.Color(0, 0, 102));
        jLabel3.setText("Koreksi Retur");

        jLabel4.setForeground(new java.awt.Color(0, 0, 102));
        jLabel4.setText("Print Retur");

        jLabel5.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel5.setForeground(new java.awt.Color(0, 0, 102));
        jLabel5.setText("F12  :");

        jLabel6.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel6.setForeground(new java.awt.Color(0, 0, 102));
        jLabel6.setText("Esc  :");

        jLabel7.setForeground(new java.awt.Color(0, 0, 102));
        jLabel7.setText("Tutup");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jXTitledPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, 860, Short.MAX_VALUE))
                    .addGroup(layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 42, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 112, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 42, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 112, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, 42, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel7, javax.swing.GroupLayout.PREFERRED_SIZE, 112, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addGap(10, 10, 10)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jScrollPane1, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 862, Short.MAX_VALUE)
                            .addComponent(jPanel1, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 862, Short.MAX_VALUE))))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, 90, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 231, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jXTitledPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, 216, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 15, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel3)
                    .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 15, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel4)
                    .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, 15, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel7))
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnCloseActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCloseActionPerformed
        dispose();
}//GEN-LAST:event_btnCloseActionPerformed

    private void btnPreviewActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnPreviewActionPerformed
//        if (!tblHeader.getValueAt(tblHeader.getSelectedRow(),0).toString().trim().isEmpty()){
//            if (okCtk(tblHeader.getValueAt(tblHeader.getSelectedRow(),0).toString().trim()))
//                    printPR(tblHeader.getValueAt(tblHeader.getSelectedRow(),0).toString().trim());
//            else JOptionPane.showMessageDialog(this,"Acc PO belum lengkap, lengkapi Acc PO terlebih dahulu...!!");
//        }
        if (!tblHeader.getValueAt(tblHeader.getSelectedRow(),0).toString().trim().isEmpty()){
//            if (okCtk(tblDetail_ACC.getValueAt(tblDetail_ACC.getSelectedRow(),1).toString().trim()))
                previewRetur(tblHeader.getValueAt(tblHeader.getSelectedRow(),0).toString().trim());
//            else JOptionPane.showMessageDialog(this,"Acc PR belum lengkap, lengkapi Acc PR terlebih dahulu...!!");
        }
}//GEN-LAST:event_btnPreviewActionPerformed

    private void btnFilter1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnFilter1ActionPerformed
        udfLoadRetur();
}//GEN-LAST:event_btnFilter1ActionPerformed

    private void formInternalFrameOpened(javax.swing.event.InternalFrameEvent evt) {//GEN-FIRST:event_formInternalFrameOpened
       //udfInitForm();
        //jLabel16.setText(getTitle());
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

    private void btnDeleteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnDeleteActionPerformed
        udfBatalRetur();
    }//GEN-LAST:event_btnDeleteActionPerformed

    private void btnKoreksiActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnKoreksiActionPerformed
        if(objMain!=null && objMain instanceof MainForm && tblHeader.getSelectedRow()>=0){
            ((MainForm)objMain).udfLoadKoreksiReturJual(tblHeader.getValueAt(tblHeader.getSelectedRow(), 0).toString(), this);
        }
}//GEN-LAST:event_btnKoreksiActionPerformed

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

//    private void printKwitansi(Boolean okCpy){
//        int iRow=tblHeader.getSelectedRow();
//        if(iRow<0) return;
//        String sNoPO=tblHeader.getValueAt(iRow, 0).toString();
//
//        PrinterJob job = PrinterJob.getPrinterJob();
//        SysConfig sy=new SysConfig();
//
//        DocFlavor flavor = DocFlavor.SERVICE_FORMATTED.PRINTABLE;
//        PrintRequestAttributeSet aset = new HashPrintRequestAttributeSet();
//        PrintServiceAttributeSet pset = new HashPrintServiceAttributeSet();
//        PrintService[] services = PrintServiceLookup.lookupPrintServices(flavor, null);
//        int i=0;
//        for(i=0;i<services.length;i++){
//            if(services[i].getName().equalsIgnoreCase(sy.getPrintKwtName())){
//                break;
//            }
//        }
//        if (JOptionPane.showConfirmDialog(null,"Siapkan Printer!","SHS Go Open Source",JOptionPane.YES_NO_OPTION,JOptionPane.QUESTION_MESSAGE)==JOptionPane.YES_OPTION) {
//            PrintPO pn = new PrintPO(conn, sNoPO, okCpy,services[i]);
//        }
//    }

    private void previewRetur(String sNo_PO){
      try{
            setCursor(new Cursor(Cursor.WAIT_CURSOR));
            JasperReport jasperReportmkel = (JasperReport) JRLoader.loadObject(getClass().getResourceAsStream("Reports/ReturBeli.jasper"));
            HashMap parameter = new HashMap();
            parameter.put("corporate", MainForm.sNamaUsaha);
            parameter.put("alamat", MainForm.sAlamat);
            parameter.put("telp", MainForm.sTelp);
            parameter.put("no_retur",sNo_PO);
            JasperPrint jasperPrintmkel = JasperFillManager.fillReport(jasperReportmkel, parameter, conn);

            setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
            JasperViewer.viewReport(jasperPrintmkel, false);
//            if(!jasperPrintmkel.getPages().isEmpty()){
//                ResultSet rs=conn.createStatement().executeQuery(
//                        "select * from fn_phar_po_update_status_print('"+sNo_PO+"', '"+MainForm.sUserName+"') as " +
//                        "(time_print timestamp without time zone, print_ke int)");
//                if(rs.next()){
//                    tblHeader.setValueAt(rs.getTimestamp(1), tblHeader.getSelectedRow(), tblHeader.getColumnModel().getColumnIndex("Last Print"));
//                }
//                rs.close();
//            }

      }catch(JRException je){
            System.out.println(je.getMessage());
      }
  }


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnClose;
    private javax.swing.JButton btnDelete;
    private javax.swing.JButton btnFilter1;
    private javax.swing.JButton btnKoreksi;
    private javax.swing.JButton btnPreview;
    private javax.swing.JFormattedTextField jFTgl;
    private javax.swing.JFormattedTextField jFTglAkhir;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane3;
    private org.jdesktop.swingx.JXTitledPanel jXTitledPanel1;
    private org.jdesktop.swingx.JXTable tblDetail;
    private javax.swing.JTable tblHeader;
    private javax.swing.JTextField txtItem;
    private javax.swing.JTextField txtSupplier;
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
                
                case KeyEvent.VK_F12:{
                    previewRetur(tblHeader.getValueAt(tblHeader.getSelectedRow(), 0).toString());
                    break;
                }

                case KeyEvent.VK_F11:{
                    btnKoreksiActionPerformed(null);
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
