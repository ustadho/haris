/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * FrmSalesHistory.java
 *
 * Created on 09 Jan 11, 6:34:37
 */

package penjualan;

import main.MainForm;
import apotek.PrintPenjualan2;
import apotek.printPenjualan;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.FocusTraversalPolicy;
import java.awt.KeyboardFocusManager;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.print.PrinterJob;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
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
import main.SysConfig;
import org.jdesktop.swingx.JXTable;
import org.jdesktop.swingx.decorator.HighlighterFactory;

/**
 *
 * @author cak-ust
 */
public class FrmPenjualanHistory extends javax.swing.JInternalFrame {
    private Connection conn;
    SimpleDateFormat dmy = new SimpleDateFormat("dd/MM/yyyy");
    SimpleDateFormat ymd = new SimpleDateFormat("yyyy-MM-dd");
    private GeneralFunction fn;
    MyKeyListener kListener=new MyKeyListener();
    private JFormattedTextField jFDate1;
    private Component aThis;

    /** Creates new form FrmSalesHistory */
    public FrmPenjualanHistory() {
        initComponents();
        aThis=this;
        jXTable1.getSelectionModel().addListSelectionListener(new ListSelectionListener() {

            public void valueChanged(ListSelectionEvent e) {
                int i=jXTable1.getSelectedRow();
                btnPrintUlang.setEnabled(i>=0);
                btnKoreksi.setEnabled(i>=0);

                lblTotal.setText("0");
                if(i<0 ||conn==null) return;
                try{
                   ResultSet rs=conn.createStatement().executeQuery(
                           "select * from fn_penjualan_show_detail('"+jXTable1.getValueAt(i, 0).toString()+"') as "
                           + "(item_Code varchar, item_name varchar, qty double precision, unit_jual varchar, \n" +
                            "harga double precision, sub_total double precision, koreksi boolean)");
                    ((DefaultTableModel)tblItem.getModel()).setNumRows(0);
                    double total=0;
                    while(rs.next()){
                        btnKoreksi.setEnabled(!rs.getBoolean("koreksi"));
                        ((DefaultTableModel)tblItem.getModel()).addRow(new Object[]{
                            rs.getString("item_code"),
                            rs.getString("item_name"),
                            rs.getDouble("qty"),
                            rs.getString("unit_jual"),
                            rs.getDouble("harga"),
                            rs.getDouble("sub_total")
                        });
                        total+=rs.getDouble("sub_total");
                    }
                    lblTotal.setText(fn.intFmt.format(total));

                    if(tblItem.getRowCount()>0){
                        tblItem.setRowSelectionInterval(0, 0);
                        tblItem.setModel((DefaultTableModel)fn.autoResizeColWidth(tblItem, (DefaultTableModel)tblItem.getModel()).getModel());
                    }

                }catch(SQLException se){
                    JOptionPane.showMessageDialog(aThis, se.getMessage());
                }
            }
        });
    }

    public void setConn(Connection con){
        this.conn=con;
    }

    private void udfInitForm(){
        fn=new GeneralFunction(conn);
        fn.addKeyListenerInContainer(jPanel3, kListener, txtFocusListener);
        jXTable1.getColumnModel().getColumn(1).setCellRenderer(new MyRowRenderer());
        jXTable1.setHighlighters(HighlighterFactory.createSimpleStriping(HighlighterFactory.QUICKSILVER));

        MaskFormatter fmttgl = null;
        try {
            fmttgl = new MaskFormatter("##/##/####");
        } catch (java.text.ParseException e) {}

        jFDate1 = new JFormattedTextField(fmttgl);
        jFTgl.setFormatterFactory(jFDate1.getFormatterFactory());
        jFTglAkhir.setFormatterFactory(jFDate1.getFormatterFactory());

        try{
            ResultSet rs = conn.createStatement().executeQuery("select '01/'||to_char(current_date, 'MM/yyyy') as tgl1,  to_char(current_date, 'dd/MM/yyyy') as tgl2 ");
            if(rs.next()){
                jFTgl.setText(rs.getString(2));
                jFTgl.setValue(rs.getString(2));
                jFTglAkhir.setText(rs.getString(2));
                jFTglAkhir.setValue(rs.getString(2));
            }

            rs.close();
        }catch(SQLException se){
            JOptionPane.showMessageDialog(this, se.getMessage());
        }
        udfFilter();

        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                jFTgl.requestFocus();
            }
        });
    }

    public void udfFilter(){
        try{
            ResultSet rs=conn.createStatement().executeQuery("select s.no_penjualan, s.tanggal, " +
                    "coalesce(c.nama_pelanggan, s.nama_pelanggan) as nama_cust, " +
                    "coalesce(s.total, 0) as sub_total, " +
                    "case when s.koreksi=true then 'K' else '' end as flag_koreksi,"
                    + "coalesce(s.multi_satuan,false) as multi  " +
                    "from penjualan s " +
                    "inner join penjualan_detail d on d.no_penjualan=s.no_penjualan " +
                    "inner join barang i on i.item_code=d.item_code " +
                    "left join pelanggan c on c.kode_pelanggan=s.kode_pelanggan " +
                    "where " + //s.is_koreksi<>true and
                    "to_char(s.tanggal, 'yyyy-MM-dd')>='" + ymd.format(dmy.parse(jFTgl.getText())) + "' " +
                    "and to_char(s.tanggal, 'yyyy-MM-dd')<='" + ymd.format(dmy.parse(jFTglAkhir.getText())) + "' " +
                    "and (coalesce(c.nama_pelanggan, s.nama_pelanggan)||s.no_penjualan ilike '%"+txtItem.getText()+"%' " +
                    "or d.item_code||coalesce(i.item_name,'') ilike '%"+txtItem.getText()+"%') " +
                    "group by s.no_penjualan, s.tanggal, coalesce(c.nama_pelanggan, s.nama_pelanggan) , flag_koreksi, multi " +
                    "order by s.tanggal");

            ((DefaultTableModel)jXTable1.getModel()).setNumRows(0);
            ((DefaultTableModel)tblItem.getModel()).setNumRows(0);
            while(rs.next()){
                ((DefaultTableModel)jXTable1.getModel()).addRow(new Object[]{
                    rs.getString("no_penjualan"),
                    rs.getDate("tanggal"),
                    rs.getString("nama_cust"),
                    rs.getDouble("sub_total"),
                    rs.getString("flag_koreksi"),
                    rs.getBoolean("multi")

                });
            }
            if(jXTable1.getRowCount()>0){
                jXTable1.setRowSelectionInterval(0, 0);
                jXTable1.setModel((DefaultTableModel)fn.autoResizeColWidth(jXTable1, (DefaultTableModel)jXTable1.getModel()).getModel());

            }

        }catch(SQLException se){
            JOptionPane.showMessageDialog(this, se.getMessage());
        }catch(ParseException pe){
            JOptionPane.showMessageDialog(this, pe.getMessage());
        }
    }

    private void printKwitansi(){
        int iRow=jXTable1.getSelectedRow();
        if(iRow < 0) return;
        String sNo=jXTable1.getValueAt(iRow, 0).toString();
        printPenjualan pn;
        PrintPenjualan2 pn2;
        
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
        //if (JOptionPane.showConfirmDialog(null,"Cetak Invoice?","Message",JOptionPane.YES_NO_OPTION,JOptionPane.QUESTION_MESSAGE)==JOptionPane.YES_OPTION) {
        try{
            if(((Boolean)jXTable1.getValueAt(iRow, jXTable1.getColumnModel().getColumnIndex("Multi")))==true){
                pn2 = new PrintPenjualan2(conn, sNo, MainForm.sUserName,services[i]);
            }else
                pn = new printPenjualan(conn, sNo, MainForm.sUserName,services[i]);
        }catch(java.lang.ArrayIndexOutOfBoundsException ie){
            JOptionPane.showMessageDialog(this, "Printer '"+sy.getPrintKwtName()+"' tidak ditemukan!");
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

        jPanel3 = new javax.swing.JPanel();
        btnFilter1 = new javax.swing.JButton();
        jFTgl = new javax.swing.JFormattedTextField();
        txtItem = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        jFTglAkhir = new javax.swing.JFormattedTextField();
        jLabel16 = new javax.swing.JLabel();
        jLabel20 = new javax.swing.JLabel();
        jScrollPane3 = new javax.swing.JScrollPane();
        jXTable1 = new org.jdesktop.swingx.JXTable();
        lblTotal2 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        tblItem = new javax.swing.JTable();
        jPanel4 = new javax.swing.JPanel();
        btnPrintUlang = new javax.swing.JButton();
        btnKoreksi = new javax.swing.JButton();
        lblTotal = new javax.swing.JLabel();

        setClosable(true);
        setIconifiable(true);
        setMaximizable(true);
        setResizable(true);
        setTitle("History Penjualan");
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

        jPanel3.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        jPanel3.setName("jPanel3"); // NOI18N
        jPanel3.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        btnFilter1.setText("Cari");
        btnFilter1.setToolTipText("Fiter");
        btnFilter1.setMaximumSize(new java.awt.Dimension(40, 40));
        btnFilter1.setName("btnFilter1"); // NOI18N
        btnFilter1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnFilter1ActionPerformed(evt);
            }
        });
        jPanel3.add(btnFilter1, new org.netbeans.lib.awtextra.AbsoluteConstraints(290, 20, 70, 25));

        jFTgl.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 51, 153), 1, true));
        jFTgl.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jFTgl.setName("jFTgl"); // NOI18N
        jPanel3.add(jFTgl, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 25, 80, 20));

        txtItem.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        txtItem.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 204)));
        txtItem.setName("txtItem"); // NOI18N
        jPanel3.add(txtItem, new org.netbeans.lib.awtextra.AbsoluteConstraints(160, 25, 130, 20));

        jLabel2.setBackground(new java.awt.Color(204, 204, 255));
        jLabel2.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jLabel2.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel2.setText("Pencarian");
        jLabel2.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        jLabel2.setName("jLabel2"); // NOI18N
        jLabel2.setOpaque(true);
        jPanel3.add(jLabel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(160, 10, 130, -1));

        jFTglAkhir.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 51, 153), 1, true));
        jFTglAkhir.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jFTglAkhir.setName("jFTglAkhir"); // NOI18N
        jPanel3.add(jFTglAkhir, new org.netbeans.lib.awtextra.AbsoluteConstraints(80, 25, 80, 20));

        jLabel16.setBackground(new java.awt.Color(204, 204, 255));
        jLabel16.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jLabel16.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel16.setText("Dari");
        jLabel16.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        jLabel16.setName("jLabel16"); // NOI18N
        jLabel16.setOpaque(true);
        jPanel3.add(jLabel16, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 10, 80, -1));

        jLabel20.setBackground(new java.awt.Color(204, 204, 255));
        jLabel20.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jLabel20.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel20.setText("Sampai");
        jLabel20.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        jLabel20.setName("jLabel20"); // NOI18N
        jLabel20.setOpaque(true);
        jPanel3.add(jLabel20, new org.netbeans.lib.awtextra.AbsoluteConstraints(80, 10, 80, -1));

        jScrollPane3.setName("jScrollPane3"); // NOI18N

        jXTable1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "No. Trans.", "Tanggal", "Customer", "Total", "Flag", "Multi"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Double.class, java.lang.Object.class, java.lang.Boolean.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jXTable1.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_OFF);
        jXTable1.setName("jXTable1"); // NOI18N
        jXTable1.getTableHeader().setReorderingAllowed(false);
        jScrollPane3.setViewportView(jXTable1);

        lblTotal2.setBackground(new java.awt.Color(0, 0, 102));
        lblTotal2.setFont(new java.awt.Font("Tahoma", 1, 24)); // NOI18N
        lblTotal2.setForeground(new java.awt.Color(255, 255, 255));
        lblTotal2.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblTotal2.setText("Detail Barang");
        lblTotal2.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        lblTotal2.setName("lblTotal2"); // NOI18N
        lblTotal2.setOpaque(true);

        jScrollPane1.setName("jScrollPane1"); // NOI18N

        tblItem.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        tblItem.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "ProductID", "Nama Barang", "Qty", "Satuan", "Harga", "Sub Total"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Object.class, java.lang.Object.class, java.lang.Double.class, java.lang.Object.class, java.lang.Double.class, java.lang.Double.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        tblItem.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_OFF);
        tblItem.setName("tblItem"); // NOI18N
        tblItem.getTableHeader().setReorderingAllowed(false);
        jScrollPane1.setViewportView(tblItem);

        jPanel4.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        jPanel4.setName("jPanel4"); // NOI18N
        jPanel4.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        btnPrintUlang.setText("Print Ulang");
        btnPrintUlang.setMargin(new java.awt.Insets(2, 2, 2, 2));
        btnPrintUlang.setName("btnPrintUlang"); // NOI18N
        btnPrintUlang.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnPrintUlangActionPerformed(evt);
            }
        });
        jPanel4.add(btnPrintUlang, new org.netbeans.lib.awtextra.AbsoluteConstraints(80, 5, 100, -1));

        btnKoreksi.setText("Koreksi");
        btnKoreksi.setMargin(new java.awt.Insets(2, 2, 2, 2));
        btnKoreksi.setName("btnKoreksi"); // NOI18N
        btnKoreksi.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnKoreksiActionPerformed(evt);
            }
        });
        jPanel4.add(btnKoreksi, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 5, 70, -1));

        lblTotal.setBackground(new java.awt.Color(0, 0, 102));
        lblTotal.setFont(new java.awt.Font("Tahoma", 1, 24)); // NOI18N
        lblTotal.setForeground(new java.awt.Color(255, 255, 255));
        lblTotal.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lblTotal.setText("0.00");
        lblTotal.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        lblTotal.setName("lblTotal"); // NOI18N
        lblTotal.setOpaque(true);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(10, 10, 10)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, 370, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(10, 10, 10)
                        .addComponent(lblTotal2, javax.swing.GroupLayout.DEFAULT_SIZE, 490, Short.MAX_VALUE)
                        .addGap(10, 10, 10))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 370, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(8, 8, 8)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 490, Short.MAX_VALUE)
                            .addGroup(layout.createSequentialGroup()
                                .addGap(2, 2, 2)
                                .addComponent(jPanel4, javax.swing.GroupLayout.DEFAULT_SIZE, 275, Short.MAX_VALUE)
                                .addGap(10, 10, 10)
                                .addComponent(lblTotal, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(3, 3, 3)))
                        .addGap(12, 12, 12))))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(10, 10, 10)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblTotal2, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane3, javax.swing.GroupLayout.DEFAULT_SIZE, 454, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(3, 3, 3)
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 398, Short.MAX_VALUE)
                        .addGap(7, 7, 7)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(lblTotal, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jPanel4, javax.swing.GroupLayout.DEFAULT_SIZE, 40, Short.MAX_VALUE))))
                .addGap(11, 11, 11))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnFilter1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnFilter1ActionPerformed
        udfFilter();
}//GEN-LAST:event_btnFilter1ActionPerformed

    private void formInternalFrameOpened(javax.swing.event.InternalFrameEvent evt) {//GEN-FIRST:event_formInternalFrameOpened
        udfInitForm();
    }//GEN-LAST:event_formInternalFrameOpened

    private void btnPrintUlangActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnPrintUlangActionPerformed
        printKwitansi();
    }//GEN-LAST:event_btnPrintUlangActionPerformed

    private boolean cekKoreksi(){
        boolean st=true;
        String sNoPenjualan=jXTable1.getValueAt(jXTable1.getSelectedRow(), 
                jXTable1.getColumnModel().getColumnIndex("No. Trans.")).toString();
        try {
            ResultSet rs=conn.createStatement().executeQuery("select gabung(no_retur) from retur_jual where sales_no='"+sNoPenjualan+"' "
                    + "and flag_trx<>'K'");
            if(rs.next() && rs.getString(1).length() > 0){
                JOptionPane.showMessageDialog(aThis, "Resep tidak bisa dikoreksi karena sudah diretur dengan nomor : '"+rs.getString(1)+"'");
                rs.close();
                return false;
            }
        } catch (SQLException ex) {
            Logger.getLogger(FrmPenjualanHistory.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return st;
    }
    
    private void btnKoreksiActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnKoreksiActionPerformed
        int iRow=jXTable1.getSelectedRow();
        if(iRow>=0){
            if(jXTable1.getValueAt(iRow, jXTable1.getColumnModel().getColumnIndex("Flag")).toString().equalsIgnoreCase("K")){
                JOptionPane.showMessageDialog(this, "Transaksi tidak bisa dikoreksi karena sudah pernah dilakukan!\n" +
                        "Silakan cari hasil dari koreksi");
                jXTable1.requestFocus();
                return;
            }
            if(!cekKoreksi()){
                return;
            }
            if(((Boolean)jXTable1.getValueAt(iRow, jXTable1.getColumnModel().getColumnIndex("Multi")))==true){
                FrmPenjualan2 frm=new FrmPenjualan2();
                frm.setConn(conn);
                frm.setFlagKoreksi(true);
                frm.setNoTrx(jXTable1.getValueAt(iRow, 0).toString());
                frm.setVisible(true);
            }else{
                FrmPenjualan frm=new FrmPenjualan();
                frm.setConn(conn);
                frm.setKoreksi(true);
                frm.setNoResep(jXTable1.getValueAt(iRow, 0).toString());
                frm.setVisible(true);
            }
        }
    }//GEN-LAST:event_btnKoreksiActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnFilter1;
    private javax.swing.JButton btnKoreksi;
    private javax.swing.JButton btnPrintUlang;
    private javax.swing.JFormattedTextField jFTgl;
    private javax.swing.JFormattedTextField jFTglAkhir;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel20;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane3;
    private org.jdesktop.swingx.JXTable jXTable1;
    private javax.swing.JLabel lblTotal;
    private javax.swing.JLabel lblTotal2;
    private javax.swing.JTable tblItem;
    private javax.swing.JTextField txtItem;
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
                value=dmy.format(value);
            }if(value instanceof Timestamp ){
                value=dmy.format(value);
            }else if(value instanceof Double ||value instanceof Integer ||value instanceof Float  ){
                setHorizontalAlignment(SwingConstants.RIGHT);
                value=fn.dFmt.format(value);
            }

            setValue(value);
            return this;
        }
    }

}
