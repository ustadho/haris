/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * FrmGRApproval.java
 *
 * Created on Sep 13, 2011, 10:01:24 AM
 */
package pembelian;

import main.MainForm;
import java.awt.Component;
import java.awt.Cursor;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTable;
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
import main.GeneralFunction;

/**
 *
 * @author ustadho
 */
public class FrmGRApproval extends javax.swing.JInternalFrame {
    private Connection conn;
    private GeneralFunction fn=new GeneralFunction();
    private boolean bAcc1=false;
    private int i;
    private Long waktuRefresh;
    private Timer timer;
    private String sAcc1="";
    
    /** Creates new form FrmGRApproval */
    public FrmGRApproval(Connection con) {
        initComponents();
        this.conn=con;
        tblHeader.getSelectionModel().addListSelectionListener(new ListSelectionListener() {

            public void valueChanged(ListSelectionEvent e) {
                if(conn!=null && tblHeader.getSelectedRow()<0) return;
                udfLoadGrDetail();
                
            }
        });
        tblDetail.getModel().addTableModelListener(new TableModelListener() {

            public void tableChanged(TableModelEvent e) {
                int iRow=tblDetail.getSelectedRow();
                int iCol=e.getColumn();
                TableColumnModel col=tblDetail.getColumnModel();

                 if(tblDetail.getRowCount()>0){
                    double totLine=0, totVat=0;
                    double extPrice=0;
                    for(int i=0; i< tblDetail.getRowCount(); i++){
                        //if(e.getType()==TableModelEvent.DELETE) ((DefaultTableModel)tblDetail.getModel()).setValueAt(i+1, i, 0);
                        extPrice=fn.udfGetDouble(tblDetail.getValueAt(i, col.getColumnIndex("Qty")))*
                                fn.udfGetDouble(tblDetail.getValueAt(i, col.getColumnIndex("Unit Price")));
                        extPrice=extPrice-(extPrice/100)* fn.udfGetDouble(tblDetail.getValueAt(i, col.getColumnIndex("Disc %")));

                        totLine+=extPrice;
                        totVat+=extPrice/100*fn.udfGetDouble(tblDetail.getValueAt(i, col.getColumnIndex("Vat")));
                    }

                    txtTotalLine.setText(fn.dFmt.format(Math.floor(totLine)));
                    txtTotVat.setText(fn.dFmt.format(Math.floor(totVat)));
                    txtNetto.setText(fn.dFmt.format(Math.floor(totVat)+Math.floor(totLine)));
                }else{
                    txtTotalLine.setText("0");
                    txtTotVat.setText("0");
                    txtNetto.setText("0");
                }
            }
        });
        
        udfInitForm();
    }
    
    private void udfLoadGrDetail(){
        int iRow=tblHeader.getSelectedRow();
        String sNoGR=tblHeader.getValueAt(iRow, tblHeader.getColumnModel().getColumnIndex("Good Receipt#")).toString();

        String s ="select * from fn_phar_show_gr_print('"+sNoGR+"') " +
                "as(delivery_no varchar, tanggal text,jam text,kode_supplier varchar,nama_supplier varchar,alamat varchar,kota varchar, " +
                "remarks varchar,gr_id varchar,user_receipt varchar,no_po varchar,kode_barang varchar,nama_barang varchar,uom varchar, " +
                "qty numeric,price numeric,discount float8,vat float4,total numeric)";

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
                    rs.getDouble("discount"),
                    rs.getDouble("vat"),
                    Math.floor(rs.getDouble("total"))
                });
            }

            if(tblDetail.getRowCount()>0)
                tblDetail.setRowSelectionInterval(0, 0);

            tblDetail.setModel((DefaultTableModel)fn.autoResizeColWidth(tblDetail, (DefaultTableModel)tblDetail.getModel()).getModel());
        }catch(SQLException se){
            JOptionPane.showMessageDialog(this, se.getMessage());
        }

    }
    
    private void udfInitForm(){
        fn=new GeneralFunction(conn);
        
        try {
            ResultSet rs = conn.createStatement().executeQuery("select '01/'||to_char(current_date, 'MM/yyyy') as tgl1,  to_char(current_date, 'dd/MM/yyyy') as tgl2 " +
                    "");
            
            if(rs.next()){
                jFTgl.setText(rs.getString(1));
                jFTgl.setValue(rs.getString(1));
                jFTglAkhir.setText(rs.getString(2));
                jFTglAkhir.setValue(rs.getString(2));
            }
            
            rs.close();
            rs=conn.createStatement().executeQuery("select * from m_user_acc where user_name='"+ MainForm.sUserName+"' " +
                    "and acc_modul='GR'");
            while(rs.next()){
                if(rs.getInt("level")==1) bAcc1=true;
            }
            rs.close();
            rs=conn.createStatement().executeQuery("select distinct acc.level, coalesce(singkatan,'') " +
                    "from m_user_acc acc  " +
                    "inner join m_user u on u.username=acc.user_name " +
                    "inner join m_jabatan jb on jb.kode_jabatan=u.kode_jabatan " +
                    "where acc_modul='GR'  " +
                    "and coalesce(acc.priority,0)=0 order by acc.level");


            while(rs.next()){
                if(rs.getInt("level")==1) sAcc1=rs.getString(2);
                
            }
            
            tblHeader.setModel(new javax.swing.table.DefaultTableModel(
                new Object [][] {},
                new String [] {"Good Receipt#", "Tanggal", "SupplierID", "Nama Supplier", "Delivery No.", "No. PO", 
                                sAcc1, "Remark", "SiteID", "Diterima", "Gudang"}
            ) {
                Class[] types = new Class [] {
                    java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class,
                    java.lang.Object.class, java.lang.Object.class, 
                    java.lang.Boolean.class, java.lang.Object.class, java.lang.Object.class, 
                    java.lang.Object.class, java.lang.Object.class 
                };
                boolean[] canEdit = new boolean [] {
                    false, false, false, false, false, false, bAcc1, false, false, false, false
                };
                
                public Class getColumnClass(int columnIndex) {
                    return types [columnIndex];
                }

                public boolean isCellEditable(int rowIndex, int columnIndex) {
                    return canEdit [columnIndex];
                }
            });


            tblHeader.getTableHeader().setReorderingAllowed(false);
            
//            UIManager.put(GroupableTableHeader.uiClassID, "tableRender.GroupableTableHeaderUI");
//            GroupableTableHeader header = new GroupableTableHeader(tblHeader.getColumnModel());
//            TableColumnModel columns = tblHeader.getColumnModel();
//            ColumnGroup acc = new ColumnGroup("Approve By");
//            acc.add(columns.getColumn(6));
//            //acc.add(columns.getColumn(8));
//            
//            header.addGroup(acc);
//            
//            tblHeader.setTableHeader(header);

//            tblHeader.getColumn("Release Date").setCellRenderer(new MyRowRenderer());
//            tblHeader.getColumn("Need Date").setCellRenderer(new MyRowRenderer());
            for(int i=0; i< tblHeader.getColumnCount(); i++){
                tblHeader.getColumnModel().getColumn(i).setCellRenderer(new MyRowRenderer());
            }

            tblHeader.setRowHeight(22);
            tblDetail.setRowHeight(22);
            tblHeader.setRowHeight(22);
            tblDetail.setRowHeight(22);
            tblHeader.getColumn("Good Receipt#").setPreferredWidth(100);
            tblHeader.getColumn("Nama Supplier").setPreferredWidth(130);
            tblHeader.getColumn("Tanggal").setPreferredWidth(110);
            
        } catch (SQLException ex) {
            Logger.getLogger(FrmPOApproval.class.getName()).log(Level.SEVERE, null, ex);
        }

        jLabel16.setText(getTitle());
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                tblHeader.requestFocus();
            }
        });
        chkAutomatic.setSelected(false);
        jSlider1.setVisible(false); jLDetik.setVisible(false);
        jLabel11.setVisible(false); jLTest.setVisible(false); jBSet.setVisible(false);
        udfLoadGoodReceipt();
        
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
            if(chkAutomatic.isSelected())
                udfLoadGoodReceipt();
        }
    }
    
    private void udfLoadGoodReceipt(){
        try{
            SimpleDateFormat dmy = new SimpleDateFormat("dd/MM/yyyy");
            SimpleDateFormat ymd = new SimpleDateFormat("yyyy-MM-dd");
            String sQry="select distinct g.good_Receipt_id, g.tanggal, coalesce(g.kode_supp,'') as kode_supp, coalesce(sp.nama_supplier,'') as nama_supplier," +
                    "coalesce(g.site_id,'') as site_id, coalesce(gudang.deskripsi,'') as site_name, " +
                    "coalesce(no_inv_do_sj,'') as delivery_no, coalesce(no_po,'') as no_po, coalesce(g.remarks,'') as remark, "
                    + "g.kontroller is not null as controlled,"
                    + "coalesce(g.user_terima,'') as user_terima, "
                    + "coalesce(g.user_gudang,'') as user_gudang " +
                    "from phar_good_receipt g " +
                    "inner join phar_good_receipt_detail gd on gd.good_receipt_id=g.good_receipt_id " +
                    "inner join barang i on i.item_code=gd.kode_barang  " +
                    "left join phar_supplier sp on sp.kode_supplier=g.kode_supp " +
                    "left join gudang on gudang.kode_gudang=g.site_id " +
                    "where g.flag_trx='T' and g.kontroller is null " +
                    "and gd.kode_barang||coalesce(i.item_name,'')||coalesce(i.nama_paten,'') ilike '%"+txtItem.getText()+"%' " +
                    "and g.kode_supp||coalesce(sp.nama_supplier,'')||coalesce(g.no_inv_do_sj,'') ilike '%"+txtSupplier.getText()+"%' " +
                    "and to_char(g.tanggal, 'yyyy-MM-dd')>='" + ymd.format(dmy.parse(jFTgl.getText())) + "' " +
                    "and to_char(g.tanggal, 'yyyy-MM-dd')<='" + ymd.format(dmy.parse(jFTglAkhir.getText())) + "' " +
                    " order by g.tanggal desc";

            setCursor(new Cursor(Cursor.WAIT_CURSOR));
            try {
                ResultSet rs = conn.createStatement().executeQuery(sQry);
                ((DefaultTableModel) tblHeader.getModel()).setNumRows(0);
                ((DefaultTableModel) tblDetail.getModel()).setNumRows(0);
                while (rs.next()) {
                    ((DefaultTableModel) tblHeader.getModel()).addRow(new Object[]{
                        rs.getString("good_Receipt_id"),
                        rs.getTimestamp("tanggal"),
                        rs.getString("kode_supp"),
                        rs.getString("nama_supplier"),
                        rs.getString("delivery_no"),
                        rs.getString("no_po"),//rs.getBoolean("acc_level3"),
                        rs.getBoolean("controlled"),
                        rs.getString("remark"),
                        rs.getString("site_id"),
                        rs.getString("user_terima"),
                        rs.getString("user_gudang"),
                    });
                }
                if(tblHeader.getRowCount()>0)
                    tblHeader.setRowSelectionInterval(0, 0);

                setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
                tblHeader.setModel((DefaultTableModel) fn.autoResizeColWidth(tblHeader, (DefaultTableModel)tblHeader.getModel()).getModel());
            } catch (SQLException se) {
                setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
                JOptionPane.showMessageDialog(this, se.getMessage());
            }
        }catch(ParseException ex){
            Logger.getLogger(FrmGRHistory.class.getName()).log(Level.SEVERE, null,ex);
        }
    }
    SimpleDateFormat dmyFmt=new SimpleDateFormat("dd/MM/yyyy");
    SimpleDateFormat dmyFmt_hhmm=new SimpleDateFormat("dd/MM/yyyy HH:mm");

    private void udfSave() {
        String sUpdate="";
        
        for(int i=0; i<tblHeader.getRowCount(); i++){
            if((Boolean)tblHeader.getValueAt(i, tblHeader.getColumnModel().getColumnIndex(sAcc1))==true){
                sUpdate+= "Update phar_good_receipt set kontroller='"+MainForm.sUserName+"', "
                      + "time_appv =now() "
                      + "where good_receipt_id='"+tblHeader.getValueAt(i, 0).toString()+"'; \n";
            }
        }
        //System.out.println(sUpdate);

        try{
            conn.setAutoCommit(false);
            int i=conn.createStatement().executeUpdate(sUpdate);
            conn.setAutoCommit(true);
            if(i>0){
                JOptionPane.showMessageDialog(this, "Simpan Good Receipt approval Sukses!");
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
                checkBox.setHorizontalAlignment(jLabel10.CENTER);
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
        jScrollPane1 = new javax.swing.JScrollPane();
        tblHeader = new javax.swing.JTable();
        jSlider1 = new javax.swing.JSlider();
        jLDetik = new javax.swing.JLabel();
        jLabel11 = new javax.swing.JLabel();
        jLTest = new javax.swing.JLabel();
        jBSet = new javax.swing.JButton();
        jScrollPane3 = new javax.swing.JScrollPane();
        tblDetail = new org.jdesktop.swingx.JXTable();
        chkAutomatic = new javax.swing.JCheckBox();
        jPanel2 = new javax.swing.JPanel();
        jLabel31 = new javax.swing.JLabel();
        txtTotalLine = new javax.swing.JLabel();
        jLabel26 = new javax.swing.JLabel();
        txtTotVat = new javax.swing.JLabel();
        txtNetto = new javax.swing.JLabel();
        jLabel30 = new javax.swing.JLabel();

        setClosable(true);
        setIconifiable(true);
        setMaximizable(true);
        setResizable(true);
        setTitle("Good Receipt Approval");

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
        jPanel1.add(btnSave, new org.netbeans.lib.awtextra.AbsoluteConstraints(690, 10, 50, 60));

        jLabel16.setFont(new java.awt.Font("Tahoma", 0, 24)); // NOI18N
        jLabel16.setForeground(new java.awt.Color(0, 0, 153));
        jLabel16.setText(" Purchase Order Approval ");
        jPanel1.add(jLabel16, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 330, 30));

        jFTgl.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 51, 153), 1, true));
        jFTgl.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jPanel1.add(jFTgl, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 60, 80, 20));

        jLabel2.setBackground(new java.awt.Color(204, 204, 255));
        jLabel2.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jLabel2.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel2.setText("Item");
        jLabel2.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        jLabel2.setOpaque(true);
        jPanel1.add(jLabel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(170, 43, 70, -1));

        jFTglAkhir.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 51, 153), 1, true));
        jFTglAkhir.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jPanel1.add(jFTglAkhir, new org.netbeans.lib.awtextra.AbsoluteConstraints(90, 60, 80, 20));

        btnFilter1.setText("Load");
        btnFilter1.setToolTipText("Fiter");
        btnFilter1.setMaximumSize(new java.awt.Dimension(40, 40));
        btnFilter1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnFilter1ActionPerformed(evt);
            }
        });
        jPanel1.add(btnFilter1, new org.netbeans.lib.awtextra.AbsoluteConstraints(390, 60, 70, 25));

        jLabel8.setBackground(new java.awt.Color(204, 204, 255));
        jLabel8.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jLabel8.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel8.setText("From");
        jLabel8.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        jLabel8.setOpaque(true);
        jPanel1.add(jLabel8, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 43, 80, -1));

        jLabel9.setBackground(new java.awt.Color(204, 204, 255));
        jLabel9.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jLabel9.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel9.setText("To");
        jLabel9.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        jLabel9.setOpaque(true);
        jPanel1.add(jLabel9, new org.netbeans.lib.awtextra.AbsoluteConstraints(90, 43, 80, -1));

        txtItem.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        txtItem.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        jPanel1.add(txtItem, new org.netbeans.lib.awtextra.AbsoluteConstraints(170, 60, 70, 20));

        jLabel10.setBackground(new java.awt.Color(204, 204, 255));
        jLabel10.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jLabel10.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel10.setText("Supplier/ Receipt No.");
        jLabel10.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        jLabel10.setOpaque(true);
        jPanel1.add(jLabel10, new org.netbeans.lib.awtextra.AbsoluteConstraints(240, 43, 140, -1));

        txtSupplier.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        txtSupplier.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        jPanel1.add(txtSupplier, new org.netbeans.lib.awtextra.AbsoluteConstraints(240, 60, 140, 20));

        tblHeader.setAutoCreateRowSorter(true);
        tblHeader.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        tblHeader.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Good Receipt#", "Tanggal", "SupplierID", "Nama Supplier", "Delivery No.", "No. PO", "Remark", "SiteID", "Approve", "Diterima", "Gudang"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Boolean.class, java.lang.Object.class, java.lang.Object.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false, false, false, true, false, false
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

        jLabel11.setText("Count");

        jLTest.setBackground(new java.awt.Color(250, 163, 120));
        jLTest.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 51, 204)));
        jLTest.setOpaque(true);

        jBSet.setText("Aplly");
        jBSet.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jBSetActionPerformed(evt);
            }
        });

        tblDetail.setAutoCreateRowSorter(true);
        tblDetail.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "No.", "Product ID", "Keterangan", "UOM", "Qty", "Unit Price", "Disc %", "Vat", "Ext. Price"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Double.class, java.lang.Double.class, java.lang.Double.class, java.lang.Double.class, java.lang.Double.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false, false, false, false
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

        chkAutomatic.setText("Automatic");
        chkAutomatic.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                chkAutomaticItemStateChanged(evt);
            }
        });

        jPanel2.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        jPanel2.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel31.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jLabel31.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel31.setText("Netto");
        jPanel2.add(jLabel31, new org.netbeans.lib.awtextra.AbsoluteConstraints(500, 50, 90, 20));

        txtTotalLine.setFont(new java.awt.Font("Dialog", 1, 12)); // NOI18N
        txtTotalLine.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        txtTotalLine.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 1, true));
        txtTotalLine.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                txtTotalLinePropertyChange(evt);
            }
        });
        jPanel2.add(txtTotalLine, new org.netbeans.lib.awtextra.AbsoluteConstraints(590, 10, 120, 20));

        jLabel26.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jLabel26.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel26.setText("Line Total :");
        jPanel2.add(jLabel26, new org.netbeans.lib.awtextra.AbsoluteConstraints(500, 10, 90, 20));

        txtTotVat.setFont(new java.awt.Font("Dialog", 1, 12)); // NOI18N
        txtTotVat.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        txtTotVat.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 1, true));
        txtTotVat.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                txtTotVatPropertyChange(evt);
            }
        });
        jPanel2.add(txtTotVat, new org.netbeans.lib.awtextra.AbsoluteConstraints(590, 30, 120, 20));

        txtNetto.setFont(new java.awt.Font("Dialog", 1, 12)); // NOI18N
        txtNetto.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        txtNetto.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 1, true));
        txtNetto.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                txtNettoPropertyChange(evt);
            }
        });
        jPanel2.add(txtNetto, new org.netbeans.lib.awtextra.AbsoluteConstraints(590, 50, 120, 20));

        jLabel30.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jLabel30.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel30.setText("V.A.T");
        jPanel2.add(jLabel30, new org.netbeans.lib.awtextra.AbsoluteConstraints(500, 30, 90, 20));

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(250, 250, 250)
                .addComponent(chkAutomatic, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(2, 2, 2)
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
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(10, 10, 10)
                        .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, 799, Short.MAX_VALUE))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(10, 10, 10)
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 800, Short.MAX_VALUE))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(10, 10, 10)
                        .addComponent(jScrollPane3, javax.swing.GroupLayout.DEFAULT_SIZE, 800, Short.MAX_VALUE))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(10, 10, 10)
                        .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, 800, Short.MAX_VALUE)))
                .addGap(7, 7, 7))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(10, 10, 10)
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, 95, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(2, 2, 2)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 203, Short.MAX_VALUE)
                .addGap(6, 6, 6)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(2, 2, 2)
                        .addComponent(chkAutomatic))
                    .addComponent(jSlider1, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLDetik, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel11, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLTest, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jBSet, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(6, 6, 6)
                .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 170, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, 0)
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, 90, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(7, 7, 7))
        );

        setBounds(0, 0, 827, 642);
    }// </editor-fold>//GEN-END:initComponents

    private void btnCloseActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCloseActionPerformed
        dispose();
}//GEN-LAST:event_btnCloseActionPerformed

    private void btnSaveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSaveActionPerformed
        udfSave();
}//GEN-LAST:event_btnSaveActionPerformed

    private void btnFilter1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnFilter1ActionPerformed
        udfLoadGoodReceipt();
}//GEN-LAST:event_btnFilter1ActionPerformed

    private void tblHeaderMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tblHeaderMouseClicked
        if(!bAcc1) return;
//        int col=tblHeader.getSelectedColumn();
//        if(col==tblHeader.getColumnModel().getColumnIndex(sAcc1) ){
//            tblHeader.setValueAt(false, tblHeader.getSelectedRow(), col);
//        }
}//GEN-LAST:event_tblHeaderMouseClicked

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

    private void chkAutomaticItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_chkAutomaticItemStateChanged
        jSlider1.setVisible(chkAutomatic.isSelected()); jLDetik.setVisible(chkAutomatic.isSelected());
        jLabel11.setVisible(chkAutomatic.isSelected()); jLTest.setVisible(chkAutomatic.isSelected()); 
        jBSet.setVisible(chkAutomatic.isSelected());
    }//GEN-LAST:event_chkAutomaticItemStateChanged

    private void txtTotalLinePropertyChange(java.beans.PropertyChangeEvent evt) {//GEN-FIRST:event_txtTotalLinePropertyChange
        // TODO add your handling code here:
}//GEN-LAST:event_txtTotalLinePropertyChange

    private void txtTotVatPropertyChange(java.beans.PropertyChangeEvent evt) {//GEN-FIRST:event_txtTotVatPropertyChange
        // TODO add your handling code here:
}//GEN-LAST:event_txtTotVatPropertyChange

    private void txtNettoPropertyChange(java.beans.PropertyChangeEvent evt) {//GEN-FIRST:event_txtNettoPropertyChange
        // TODO add your handling code here:
}//GEN-LAST:event_txtNettoPropertyChange

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnClose;
    private javax.swing.JButton btnFilter1;
    private javax.swing.JButton btnSave;
    private javax.swing.JCheckBox chkAutomatic;
    private javax.swing.JButton jBSet;
    private javax.swing.JFormattedTextField jFTgl;
    private javax.swing.JFormattedTextField jFTglAkhir;
    private javax.swing.JLabel jLDetik;
    private javax.swing.JLabel jLTest;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel26;
    private javax.swing.JLabel jLabel30;
    private javax.swing.JLabel jLabel31;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JSlider jSlider1;
    private org.jdesktop.swingx.JXTable tblDetail;
    private javax.swing.JTable tblHeader;
    private javax.swing.JTextField txtItem;
    private javax.swing.JLabel txtNetto;
    private javax.swing.JTextField txtSupplier;
    private javax.swing.JLabel txtTotVat;
    private javax.swing.JLabel txtTotalLine;
    // End of variables declaration//GEN-END:variables
}
