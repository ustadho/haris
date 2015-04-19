/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * FrmPRMaintenance.java
 *
 * Created on Jul 4, 2010, 9:11:41 AM
 */

package pembelian;

import apotek.JDesktopImage;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Cursor;
import java.awt.FocusTraversalPolicy;
import java.awt.KeyboardFocusManager;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.beans.PropertyVetoException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JCheckBox;
import javax.swing.JInternalFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumnModel;
import main.GeneralFunction;

/**
 *
 * @author cak-ust
 */
public class FrmPRMaintenance extends javax.swing.JInternalFrame {
    private Connection conn;
    private GeneralFunction fn=new GeneralFunction();
    private MyKeyListener kListener=new MyKeyListener();
    private JDesktopImage desktopPane;
    TableColumnModel colModel;

    /** Creates new form FrmPRMaintenance */
    public FrmPRMaintenance() {
        initComponents();
        colModel=tblPR.getColumnModel();
        jXDatePicker1.setFormats(new String[]{"dd/MM/yyyy"});
        jXDatePicker2.setFormats(new String[]{"dd/MM/yyyy"});

        tblPR.getColumn("KdGudang").setMinWidth(0); tblPR.getColumn("KdGudang").setMaxWidth(0); tblPR.getColumn("KdGudang").setPreferredWidth(0);
        tblPR.getColumn("Nama Gudang").setMinWidth(0); tblPR.getColumn("Nama Gudang").setMaxWidth(0); tblPR.getColumn("Nama Gudang").setPreferredWidth(0);
//        tblPR.getColumn("LocID").setMinWidth(0); tblPR.getColumn("LocID").setMaxWidth(0); tblPR.getColumn("LocID").setPreferredWidth(0);
//        tblPR.getColumn("LocName").setMinWidth(0); tblPR.getColumn("LocName").setMaxWidth(0); tblPR.getColumn("LocName").setPreferredWidth(0);
        tblPR.getColumn("SuppID").setMinWidth(0); tblPR.getColumn("SuppID").setMaxWidth(0); tblPR.getColumn("SuppID").setPreferredWidth(0);
        tblPR.getColumn("SuppName").setMinWidth(0); tblPR.getColumn("SuppName").setMaxWidth(0); tblPR.getColumn("SuppName").setPreferredWidth(0);

        tblPR.getTableHeader().setFont(new java.awt.Font("Tahoma", 0, 12));
        tblPO.getTableHeader().setFont(new java.awt.Font("Tahoma", 0, 12));
//        tblPR.getColumn("Release Date").setCellRenderer(new MyRowRenderer());
//        tblPR.getColumn("Time Appv.").setCellRenderer(new MyRowRenderer());
//        tblPR.getColumn("Qty On Order").setCellRenderer(new MyRowRenderer());
//        tblPR.getColumn("Qty").setCellRenderer(new MyRowRenderer());

        for(int i=0; i< tblPR.getColumnCount(); i++){
            tblPR.getColumnModel().getColumn(i).setCellRenderer(new MyRowRenderer());
        }
        //tblPO.getColumn("Tanggal").setCellRenderer(new MyRowRenderer());

        //tblPR.setHighlighters(HighlighterFactory.createSimpleStriping(HighlighterFactory.QUICKSILVER));
        
        tblPR.getSelectionModel().addListSelectionListener(new ListSelectionListener() {

            public void valueChanged(ListSelectionEvent e) {
                int iRow=tblPR.getSelectedRow();
                if(iRow<0 || conn==null) return;

                ((DefaultTableModel)tblPO.getModel()).setNumRows(0);
                ((DefaultTableModel)tblSupplier.getModel()).setNumRows(0);
                    
                try{
                    String s="select pod.no_po, po.tanggal, coalesce(s.nama_supplier,'') as supplier, " +
                            "coalesce(pod.jml_kecil,0) as jml_po, coalesce(prd.uom,'') as pr_uom " +
                            "from phar_pr pr " +
                            "inner join phar_pr_detail prd on prd.no_pr=pr.no_pr " +
                            "inner join phar_po_detail pod on pod.no_pr=pr.no_pr and pod.kode_barang=prd.kode_barang " +
                            "inner join phar_po po on po.no_po=pod.no_po " +
                            "left join phar_supplier s on s.kode_supplier=po.kode_supplier " +
                            "where po.flag_trx='T' and pod.no_pr='"+tblPR.getValueAt(iRow, 0).toString()+"' " +
                            "and prd.kode_barang='"+tblPR.getValueAt(iRow, tblPR.getColumnModel().getColumnIndex("Product ID")).toString()+"' ";

                    //System.out.println(s);
                    
                    ResultSet rs=conn.createStatement().executeQuery(s);
                    while(rs.next()){
                        ((DefaultTableModel)tblPO.getModel()).addRow(new Object[]{
                            rs.getString("no_po"),
                            rs.getDate("tanggal"),
                            rs.getString("supplier"),
                            rs.getDouble("jml_po"),
                            rs.getString("pr_uom")
                        });
                    }
                    tblPO.setModel((DefaultTableModel)fn.autoResizeColWidth(tblPO, (DefaultTableModel)tblPO.getModel()).getModel());
                    rs.close();
                    s="select coalesce(sb.priority,0) as rk, coalesce(sb.kode_supplier,'') as kode_supp, " +
                            "coalesce(s.nama_supplier,'') as nama_supplier, coalesce(sb.uom_alt,'') as uom_alt, " +
                            "coalesce(sb.convertion,0) as konv " +
                            "from supplier_barang  sb " +
                            "left join phar_supplier s on s.kode_supplier=sb.kode_supplier " +
                            "where sb.kode_barang='"+tblPR.getValueAt(iRow, tblPR.getColumnModel().getColumnIndex("Product ID")).toString()+"' " +
                            "order by rk ";

                    //System.out.println(s);
                    rs=conn.createStatement().executeQuery(s);
                    while(rs.next()){
                        ((DefaultTableModel)tblSupplier.getModel()).addRow(new Object[]{
                            rs.getInt("rk"),
                            rs.getString("kode_supp"),
                            rs.getString("nama_supplier"),
                            rs.getString("uom_alt"),
                            rs.getDouble("konv")
                        });
                    }
                    if(tblSupplier.getRowCount()>0)
                        tblSupplier.setRowSelectionInterval(0, 0);
                    
                    tblSupplier.setModel((DefaultTableModel)fn.autoResizeColWidth(tblSupplier, (DefaultTableModel)tblSupplier.getModel()).getModel());
                    rs.close();


                    TableColumnModel col=tblPR.getColumnModel();
                    txtKodeSupplier.setText(tblPR.getValueAt(iRow, col.getColumnIndex("SuppID")).toString());
                    txtNamaSupplier.setText(tblPR.getValueAt(iRow, col.getColumnIndex("SuppName")).toString());
                    txtSiteID.setText(tblPR.getValueAt(iRow, col.getColumnIndex("KdGudang")).toString());
                    txtSiteName.setText(tblPR.getValueAt(iRow, col.getColumnIndex("Nama Gudang")).toString());
//                    txtLocationID.setText(tblPR.getValueAt(iRow, col.getColumnIndex("LocID")).toString());
//                    txtLocationName.setText(tblPR.getValueAt(iRow, col.getColumnIndex("LocName")).toString());

                }catch(SQLException se){
                    JOptionPane.showMessageDialog(JOptionPane.getFrameForComponent(FrmPRMaintenance.this), se.getMessage());
                }
            }
        });

        tblSupplier.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            public void valueChanged(ListSelectionEvent e) {
                btnBuatPO.setEnabled(tblSupplier.getSelectedRow()>=0);
                btnBuatPO.setForeground(tblSupplier.getSelectedRow()>=0? Color.black: Color.gray);
                //[240,240,240]
            }
        });
    }

    public void setConn(Connection con){
        this.conn=con;
    }

    public void udfLoadPR(int iRow){
        //txtBarang.setText("");

//        String sHaving="";
//        sHaving+=chkBelumPO.isSelected()? " having sum(coalesce(pod.jumlah,0))< pr.jumlah ": "";
//        sHaving+=chkSudahPO.isSelected() ?  (sHaving.length()>0? " or ": " having ") +"  sum(coalesce(pod.jumlah,0))>= pr.jumlah ": "";

        String sQry="select x.*, acc_time1 " +
                "from(select * from fn_phar_pr_maintenance_list2(" +
                "'"+new SimpleDateFormat("yyyy-MM-dd").format(jXDatePicker1.getDate())+"', " +
                "'"+new SimpleDateFormat("yyyy-MM-dd").format(jXDatePicker2.getDate())+"') as (no_pr varchar, release_date timestamp without time zone, " +
                "kode_barang varchar, nama_barang varchar, jml_pr numeric, jml_po double precision, uom varchar, " +
                "kode_supp varchar, supp varchar, is_po boolean, closed boolean, cito boolean, site_id varchar, site_name varchar," +
                "req_by varchar, flag_tambahan boolean)" +
                ")x " +
                "left join phar_pr pr on x.no_pr=pr.no_pr " +
                "where nama_barang||kode_barang ilike '%"+txtBarang.getText()+"%' order by nama_barang";
        //System.out.println(sQry);

        setCursor(new Cursor(Cursor.WAIT_CURSOR));
        ((DefaultTableModel)tblPR.getModel()).setNumRows(0);
        try{
            ResultSet rs=conn.createStatement().executeQuery(sQry);
            while(rs.next()){
                ((DefaultTableModel)tblPR.getModel()).addRow(new Object[]{
                    rs.getString("no_pr"),
                    rs.getString("kode_barang"),
                    rs.getString("nama_barang"),
                    rs.getDouble("jml_pr"),
                    rs.getDouble("jml_po"),
                    rs.getString("uom"),
                    rs.getBoolean("is_po"),
                    rs.getBoolean("closed"),
                    rs.getBoolean("cito"),
                    rs.getString("kode_supp"),
                    rs.getString("supp"),
                    rs.getString("site_id"),
                    rs.getString("site_name"),
                    rs.getString("req_by"),
                    rs.getTimestamp("release_date"),
                    rs.getBoolean("flag_tambahan"),
                    rs.getTimestamp("acc_time1"),
                    
                });
            }
            if(tblPR.getRowCount()>0)
                tblPR.setRowSelectionInterval(iRow, iRow);
            rs.close();
            setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
        }catch(SQLException se){
            setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
            JOptionPane.showMessageDialog(this, se.getMessage());
        }
        tblPR.setModel((DefaultTableModel)fn.autoResizeColWidth(tblPR, (DefaultTableModel)tblPR.getModel()).getModel());
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
        jXDatePicker1 = new org.jdesktop.swingx.JXDatePicker();
        jLabel2 = new javax.swing.JLabel();
        jXDatePicker2 = new org.jdesktop.swingx.JXDatePicker();
        btnLoad = new javax.swing.JButton();
        jLabel3 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        txtBarang = new javax.swing.JTextField();
        jScrollPane1 = new javax.swing.JScrollPane();
        tblPO = new javax.swing.JTable();
        jPanel3 = new javax.swing.JPanel();
        jPanel5 = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        tblSupplier = new javax.swing.JTable();
        btnBuatPO = new javax.swing.JButton();
        jPanel4 = new javax.swing.JPanel();
        jLabel6 = new javax.swing.JLabel();
        txtKodeSupplier = new javax.swing.JTextField();
        jLabel7 = new javax.swing.JLabel();
        txtSiteID = new javax.swing.JTextField();
        jLabel8 = new javax.swing.JLabel();
        txtNamaSupplier = new javax.swing.JTextField();
        txtLocationID = new javax.swing.JTextField();
        txtLocationName = new javax.swing.JTextField();
        txtSiteName = new javax.swing.JTextField();
        jLabel9 = new javax.swing.JLabel();
        jScrollPane4 = new javax.swing.JScrollPane();
        tblPR = new javax.swing.JTable();
        lblPRCito = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        lblPRTambahan = new javax.swing.JLabel();
        jLabel11 = new javax.swing.JLabel();

        setClosable(true);
        setIconifiable(true);
        setMaximizable(true);
        setResizable(true);
        setTitle("Maintenance Purchase Requisition");
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

        jXDatePicker1.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jXDatePicker1.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jXDatePicker1KeyPressed(evt);
            }
        });
        jPanel1.add(jXDatePicker1, new org.netbeans.lib.awtextra.AbsoluteConstraints(70, 10, 110, -1));

        jLabel2.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jLabel2.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel2.setText("S/D");
        jPanel1.add(jLabel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(180, 10, 60, 20));

        jXDatePicker2.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jPanel1.add(jXDatePicker2, new org.netbeans.lib.awtextra.AbsoluteConstraints(230, 10, 120, -1));

        btnLoad.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        btnLoad.setText("Refresh");
        btnLoad.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnLoadActionPerformed(evt);
            }
        });
        jPanel1.add(btnLoad, new org.netbeans.lib.awtextra.AbsoluteConstraints(360, 10, 100, -1));

        jLabel3.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jLabel3.setText("Tanggal : ");
        jPanel1.add(jLabel3, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 10, 60, 20));

        jLabel5.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jLabel5.setText("Filter Barang : ");
        jPanel1.add(jLabel5, new org.netbeans.lib.awtextra.AbsoluteConstraints(515, 15, 160, 20));

        txtBarang.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        txtBarang.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txtBarangKeyPressed(evt);
            }
        });
        jPanel1.add(txtBarang, new org.netbeans.lib.awtextra.AbsoluteConstraints(670, 15, 265, 20));

        tblPO.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        tblPO.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "PO Number", "Tanggal", "Supplier", "Qty", "UOM"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Double.class, java.lang.Object.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        tblPO.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_OFF);
        jScrollPane1.setViewportView(tblPO);

        jPanel3.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        jPanel3.setLayout(new java.awt.CardLayout());

        tblSupplier.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        tblSupplier.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Rk", "Kd. Supp", "Supplier", "UOM", "Konv"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Double.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        tblSupplier.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_OFF);
        jScrollPane2.setViewportView(tblSupplier);

        btnBuatPO.setForeground(new java.awt.Color(204, 204, 204));
        btnBuatPO.setText("<html> <b><center>Buat PO (F8)</center> </b><br> \n<center>Untuk Supplier</center>\n</html>");
        btnBuatPO.setEnabled(false);
        btnBuatPO.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnBuatPO.setMargin(new java.awt.Insets(2, 2, 2, 2));
        btnBuatPO.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnBuatPOActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel5Layout.createSequentialGroup()
                .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 468, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(btnBuatPO, javax.swing.GroupLayout.PREFERRED_SIZE, 76, javax.swing.GroupLayout.PREFERRED_SIZE))
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel5Layout.createSequentialGroup()
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addComponent(btnBuatPO)
                        .addGap(6, 6, 6))
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE))
                .addGap(0, 0, 0))
        );

        jPanel3.add(jPanel5, "card3");

        jPanel4.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel6.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jLabel6.setText("Suppiler :");
        jPanel4.add(jLabel6, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 10, 60, 20));

        txtKodeSupplier.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        txtKodeSupplier.setDisabledTextColor(new java.awt.Color(0, 0, 0));
        txtKodeSupplier.setEnabled(false);
        jPanel4.add(txtKodeSupplier, new org.netbeans.lib.awtextra.AbsoluteConstraints(70, 10, 20, 20));

        jLabel7.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jLabel7.setText("Site :");
        jPanel4.add(jLabel7, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 30, 60, 20));

        txtSiteID.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        txtSiteID.setDisabledTextColor(new java.awt.Color(0, 0, 0));
        txtSiteID.setEnabled(false);
        jPanel4.add(txtSiteID, new org.netbeans.lib.awtextra.AbsoluteConstraints(70, 30, 20, 20));

        jLabel8.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jLabel8.setText("Location :");
        jPanel4.add(jLabel8, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 50, 60, 20));

        txtNamaSupplier.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        txtNamaSupplier.setDisabledTextColor(new java.awt.Color(0, 0, 0));
        txtNamaSupplier.setEnabled(false);
        jPanel4.add(txtNamaSupplier, new org.netbeans.lib.awtextra.AbsoluteConstraints(90, 10, 20, 20));

        txtLocationID.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        txtLocationID.setDisabledTextColor(new java.awt.Color(0, 0, 0));
        txtLocationID.setEnabled(false);
        jPanel4.add(txtLocationID, new org.netbeans.lib.awtextra.AbsoluteConstraints(70, 50, 20, 20));

        txtLocationName.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        txtLocationName.setDisabledTextColor(new java.awt.Color(0, 0, 0));
        txtLocationName.setEnabled(false);
        jPanel4.add(txtLocationName, new org.netbeans.lib.awtextra.AbsoluteConstraints(90, 50, 20, 20));

        txtSiteName.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        txtSiteName.setDisabledTextColor(new java.awt.Color(0, 0, 0));
        txtSiteName.setEnabled(false);
        jPanel4.add(txtSiteName, new org.netbeans.lib.awtextra.AbsoluteConstraints(90, 30, 20, 20));

        jPanel3.add(jPanel4, "card2");

        jLabel9.setForeground(new java.awt.Color(0, 0, 153));
        jLabel9.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel9.setText("<html> <b>F5 : </b>  &nbsp Refresh ;&nbsp&nbsp  <b>F3 : </b> &nbsp Edit Quantity PR ;&nbsp&nbsp  <b>F8 : </b> &nbsp Lanjutkan ke PO ;&nbsp&nbsp  <b>Del: </b> &nbsp Closed Item PR &nbsp&nbsp  <b>F12: </b> &nbsp Open Item PR</html>");

        tblPR.setAutoCreateRowSorter(true);
        tblPR.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        tblPR.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "No. PR", "Product ID", "Keterangan", "Qty", "Qty On Order", "UOM", "PO", "Cls", "CITO", "SuppID", "SuppName", "KdGudang", "Nama Gudang", "Request By", "Release Date", "Tambahan", "Time Appv."
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Boolean.class, java.lang.Boolean.class, java.lang.Boolean.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Boolean.class, java.lang.Object.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        tblPR.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_OFF);
        jScrollPane4.setViewportView(tblPR);

        lblPRCito.setBackground(new java.awt.Color(255, 153, 153));
        lblPRCito.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        lblPRCito.setOpaque(true);

        jLabel10.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel10.setForeground(new java.awt.Color(0, 0, 153));
        jLabel10.setText(" PR Cito");

        lblPRTambahan.setBackground(new java.awt.Color(255, 255, 51));
        lblPRTambahan.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        lblPRTambahan.setOpaque(true);

        jLabel11.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel11.setForeground(new java.awt.Color(0, 0, 153));
        jLabel11.setText(" PR Tambahan");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(10, 10, 10)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(lblPRCito, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, 0)
                        .addComponent(jLabel10, javax.swing.GroupLayout.PREFERRED_SIZE, 70, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(155, 155, 155)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addGap(143, 143, 143)
                                .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addGap(2, 2, 2))
                            .addComponent(jLabel9, javax.swing.GroupLayout.PREFERRED_SIZE, 705, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(120, 120, 120)
                        .addComponent(jLabel11, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(90, 90, 90)
                        .addComponent(lblPRTambahan, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 392, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jScrollPane4))
                        .addGap(5, 5, 5)))
                .addGap(5, 5, 5))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(12, 12, 12)
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, 0)
                .addComponent(jScrollPane4, javax.swing.GroupLayout.DEFAULT_SIZE, 234, Short.MAX_VALUE)
                .addGap(4, 4, 4)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lblPRCito, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel10, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(24, 24, 24)
                        .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jLabel9, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(1, 1, 1)
                        .addComponent(jLabel11, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(1, 1, 1)
                        .addComponent(lblPRTambahan, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(24, 24, 24)
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(12, 12, 12))
        );

        setBounds(0, 0, 986, 464);
    }// </editor-fold>//GEN-END:initComponents

    private void jXDatePicker1KeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jXDatePicker1KeyPressed
        if(evt.getKeyCode()==KeyEvent.VK_ENTER){
            jXDatePicker2.requestFocus();
        }
    }//GEN-LAST:event_jXDatePicker1KeyPressed

    private void btnLoadActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnLoadActionPerformed
        udfLoadPR(0);
    }//GEN-LAST:event_btnLoadActionPerformed

    private void formInternalFrameOpened(javax.swing.event.InternalFrameEvent evt) {//GEN-FIRST:event_formInternalFrameOpened
        try {
            ResultSet rs = conn.createStatement()
                    .executeQuery("select (to_char(current_date, 'yyyy-MM')||'-01')::date as tgl_awal," +
                    "lastdateofmonth(current_date) as tgl_akhir ");

            rs.next();
            jXDatePicker1.setDate(rs.getDate("tgl_awal"));
            jXDatePicker2.setDate(rs.getDate("tgl_akhir"));

            rs.close();

            fn.addKeyListenerInContainer(jPanel1, kListener, null);
            //fn.addKeyListenerInContainer(jPanel2, kListener, null);
            tblPR.addKeyListener(kListener);

            udfLoadPR(0);
        } catch (SQLException ex) {
            Logger.getLogger(FrmPRMaintenance.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_formInternalFrameOpened

    private void txtBarangKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtBarangKeyPressed
        if(evt.getKeyCode()==KeyEvent.VK_ENTER)
            udfLoadPR(0);
    }//GEN-LAST:event_txtBarangKeyPressed

    private void btnBuatPOActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnBuatPOActionPerformed
        createPO();
    }//GEN-LAST:event_btnBuatPOActionPerformed

    private void createPO() {
        int iRow=tblPR.getSelectedRow();
        if(iRow<0) return;

        FrmPO f1=new FrmPO();
        if(!udfExistOnDesktop(f1)){
            f1.setConn(conn);
            f1.udfCreatePOFromPRMaintenance(this, tblPR.getValueAt(iRow, tblPR.getColumnModel().getColumnIndex("Product ID")).toString(),
                    tblSupplier.getValueAt(tblSupplier.getSelectedRow(), tblSupplier.getColumnModel().getColumnIndex("Kd. Supp")).toString(),
                    fn.udfGetInt(tblSupplier.getValueAt(tblSupplier.getSelectedRow(), tblSupplier.getColumnModel().getColumnIndex("Rk"))),
                    (Boolean)tblPR.getValueAt(iRow, tblPR.getColumnModel().getColumnIndex("CITO"))
                    );
            desktopPane.add(f1);
            f1.setVisible(true);
            f1.setBounds(0, 0, f1.getWidth(), f1.getHeight());
            try{
                f1.setSelected(true);
            } catch(PropertyVetoException PO){
            }
        }else{
            f1.dispose();
        }
    }

    private boolean udfExistOnDesktop(JInternalFrame form){
        JInternalFrame ji[] = desktopPane.getAllFrames();
        for(int i=0;i<ji.length;i++){
            if(ji[i].getClass().getSimpleName().equalsIgnoreCase(form.getClass().getSimpleName())){
                try{
                    if(!ji[i].isShowing()) ji[i].setVisible(true);
                    ji[i].setSelected(true);
                    ji[i].requestFocusInWindow();
                    return true;
                } catch(PropertyVetoException PO){
                }
                break;
            }
        }
        return false;
    }

    public void setDesktopPane(JDesktopImage desktopImage2) {
        this.desktopPane=desktopImage2;
    }

    public class MyRowRenderer extends DefaultTableCellRenderer implements TableCellRenderer{
        SimpleDateFormat dmyFmt=new SimpleDateFormat("dd/MM/yyyy");
        SimpleDateFormat dmyFmtHm=new SimpleDateFormat("dd/MM/yyyy HH:mm");
        JCheckBox checkBox = new JCheckBox();
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {

            if(value instanceof Date || value instanceof Timestamp){
                value=dmyFmtHm.format(value);
            }else if(value instanceof Double ||value instanceof Integer ||value instanceof Float  ){
                setHorizontalAlignment(SwingConstants.RIGHT);
                value=fn.dFmt.format(value);
            }else if(value instanceof Boolean){
//                checkBox.setSelected(((Boolean) value).booleanValue());
//                  checkBox.setHorizontalAlignment(JLabel.CENTER);
//                  if (row%2==0){
//                     checkBox.setBackground(w);
//                  }else{
//                     checkBox.setBackground(g);
//                  }
            }
            setFont(table.getFont());
            if(isSelected){
                setBackground(table.getSelectionBackground());
                setForeground(table.getSelectionForeground());
            }else{
               if( row>=0 && (Boolean)table.getValueAt(row, colModel.getColumnIndex("CITO"))==true){
                    setBackground(lblPRCito.getBackground());
                    setForeground(table.getForeground());
                }else{
                    if((Boolean)table.getValueAt(row, colModel.getColumnIndex("Tambahan"))==true){
                        setBackground(lblPRTambahan.getBackground());
                        setForeground(table.getForeground());
                    }else{
                        setBackground(table.getBackground());
                        setForeground(table.getForeground());
                    }
                }
            }
            if(value instanceof Boolean){
                checkBox.setSelected(((Boolean) value).booleanValue());
                checkBox.setHorizontalAlignment(JLabel.CENTER);
                
                if(isSelected){
                    checkBox.setBackground(table.getSelectionBackground());
                    checkBox.setForeground(table.getSelectionForeground());
                }else{
                   if( row>=0 && (Boolean)table.getValueAt(row, colModel.getColumnIndex("CITO"))==true){
                        checkBox.setBackground(lblPRCito.getBackground());
                        checkBox.setForeground(table.getForeground());
                    }else{
                       if((Boolean)table.getValueAt(row, colModel.getColumnIndex("Tambahan"))==true){
                            checkBox.setBackground(lblPRTambahan.getBackground());
                        checkBox.setForeground(table.getForeground());
                       }else{
                            checkBox.setBackground(table.getBackground());
                            checkBox.setForeground(table.getForeground());
                       }
                    }
                }
                return  checkBox;
            }
            setValue(value);
            return this;
        }
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnBuatPO;
    private javax.swing.JButton btnLoad;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane4;
    private org.jdesktop.swingx.JXDatePicker jXDatePicker1;
    private org.jdesktop.swingx.JXDatePicker jXDatePicker2;
    private javax.swing.JLabel lblPRCito;
    private javax.swing.JLabel lblPRTambahan;
    private javax.swing.JTable tblPO;
    private javax.swing.JTable tblPR;
    private javax.swing.JTable tblSupplier;
    private javax.swing.JTextField txtBarang;
    private javax.swing.JTextField txtKodeSupplier;
    private javax.swing.JTextField txtLocationID;
    private javax.swing.JTextField txtLocationName;
    private javax.swing.JTextField txtNamaSupplier;
    private javax.swing.JTextField txtSiteID;
    private javax.swing.JTextField txtSiteName;
    // End of variables declaration//GEN-END:variables

    public class MyKeyListener extends KeyAdapter {
        @Override
        public void keyPressed(KeyEvent evt) {
            Component ct = KeyboardFocusManager.getCurrentKeyboardFocusManager().getPermanentFocusOwner();
            int keyKode = evt.getKeyCode();
            switch(keyKode){
                case KeyEvent.VK_F3:{
//////                    int iRow=tblPR.getSelectedRow();
//////
//////                    if(iRow>=0){
//////                        TableColumnModel col=tblPR.getColumnModel();
//////
//////                        try{
//////                            ResultSet rs=conn.createStatement().executeQuery("select * from phar_po " +
//////                                    "inner join phar_po_detail using(no_po) " +
//////                                    "where flag_trx='T' and kode_barang='"+tblPR.getValueAt(iRow, col.getColumnIndex("Product ID")).toString()+"' " +
//////                                    "and no_pr='"+tblPR.getValueAt(iRow, col.getColumnIndex("No. PR")).toString()+"' " +
//////                                    "");
//////                            if(rs.next()){
//////                                JOptionPane.showMessageDialog(FrmPRMaintenance.this, "Qty PR tidak bisa diupdate karena telah dibuatkan PO!");
//////                                tblPR.requestFocusInWindow();
//////                                return;
//////                            }
//////                            rs.close();
//////
//////                        }catch(SQLException se){
//////                            JOptionPane.showMessageDialog(FrmPRMaintenance.this, se.getMessage());
//////                        }
//////
//////                        DlgPRUpdateQty d1=new DlgPRUpdateQty(JOptionPane.getFrameForComponent(FrmPRMaintenance.this), true);
//////                        d1.setSrcForm(FrmPRMaintenance.this, iRow);
//////                        d1.setConn(conn);
//////                        d1.setPR(tblPR.getValueAt(iRow, col.getColumnIndex("No. PR")).toString(),
//////                                tblPR.getValueAt(iRow, col.getColumnIndex("Product ID")).toString(),
//////                                tblPR.getValueAt(iRow, col.getColumnIndex("Keterangan")).toString(),
//////                                fn.udfGetDouble(tblPR.getValueAt(iRow, tblPR.getColumnModel().getColumnIndex("Qty")).toString()),
//////                                tblPR.getValueAt(iRow, col.getColumnIndex("UOM")).toString());
//////                        d1.setVisible(true);
//////
//////                    }
                    break;
                }
                case KeyEvent.VK_DELETE:{
                    int iRow=tblPR.getSelectedRow();
                    if(iRow>=0){
                        TableColumnModel col=tblPR.getColumnModel();

                        try{
                            ResultSet rs=conn.createStatement().executeQuery("select * from phar_po " +
                                    "inner join phar_po_detail using(no_po) " +
                                    "where flag_trx='T' and kode_barang='"+tblPR.getValueAt(iRow, col.getColumnIndex("Product ID")).toString()+"' " +
                                    "and no_pr='"+tblPR.getValueAt(iRow, col.getColumnIndex("No. PR")).toString()+"' " +
                                    "");
                            if(rs.next()){
                                JOptionPane.showMessageDialog(FrmPRMaintenance.this, "Item PR tidak bisa di-Close karena telah dibuatkan PO!");
                                tblPR.requestFocusInWindow();
                                return;
                            }

                            String sReason="";

                            do{
                                sReason=JOptionPane.showInputDialog(FrmPRMaintenance.this, "Reason", "");
                            }while(sReason!=null && sReason.trim().length()==0);

                            if(sReason.length()>0){
                                conn.setAutoCommit(false);
                                conn.createStatement().executeUpdate("Update phar_pr_detail set closed=true," +
                                        "closed_Reason='"+sReason+"' " +
                                        "where kode_barang='"+tblPR.getValueAt(iRow, col.getColumnIndex("Product ID")).toString()+"' " +
                                        "and no_pr='"+tblPR.getValueAt(iRow, col.getColumnIndex("No. PR")).toString()+"' ");

                                conn.setAutoCommit(true);
                                udfLoadPR(iRow);
                            }
                        }catch(SQLException se){
                        try {
                            conn.rollback();
                            conn.setAutoCommit(true);
                            JOptionPane.showMessageDialog(FrmPRMaintenance.this, se.getMessage());
                        } catch (SQLException ex) {
                            Logger.getLogger(FrmPRMaintenance.class.getName()).log(Level.SEVERE, null, ex);
                        }
                        }

                    }
                    break;
                }
                case KeyEvent.VK_F8:{
                    createPO();
                    break;
                }
                case KeyEvent.VK_F12:{
                    int iRow=tblPR.getSelectedRow();
                    if(iRow>=0){
                        TableColumnModel col=tblPR.getColumnModel();
                        if((Boolean)tblPR.getValueAt(iRow, col.getColumnIndex("Cls"))==false) return;
                        try{
                            String sReason="";

                            do{
                                sReason=JOptionPane.showInputDialog(FrmPRMaintenance.this, "Reason", "");
                            }while(sReason!=null && sReason.trim().length()==0);

                            if(sReason.length()>0 && sReason.length()>0){
                                conn.setAutoCommit(false);
                                conn.createStatement().executeUpdate("Update phar_pr_detail set closed=false," +
                                        "closed_Reason='"+sReason+"' " +
                                        "where kode_barang='"+tblPR.getValueAt(iRow, col.getColumnIndex("Product ID")).toString()+"' " +
                                        "and no_pr='"+tblPR.getValueAt(iRow, col.getColumnIndex("No. PR")).toString()+"' ");

                                conn.setAutoCommit(true);
                                udfLoadPR(iRow);
                            }
                        }catch(SQLException se){
                        try {
                            conn.rollback();
                            conn.setAutoCommit(true);
                            JOptionPane.showMessageDialog(FrmPRMaintenance.this, se.getMessage());
                        } catch (SQLException ex) {
                            Logger.getLogger(FrmPRMaintenance.class.getName()).log(Level.SEVERE, null, ex);
                        }
                        }

                    }
                    break;
                }
                case KeyEvent.VK_F5:{  //Update supplier
                    udfLoadPR(0);
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
                case KeyEvent.VK_ESCAPE:{
                    if(JOptionPane.showConfirmDialog(null,"Anda Yakin Untuk Keluar?",
                            "SHS Pharmacy",JOptionPane.YES_NO_OPTION)==JOptionPane.YES_OPTION){
                        dispose();
                    }
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

    Color bgCito=new java.awt.Color(255, 153, 153);
}
