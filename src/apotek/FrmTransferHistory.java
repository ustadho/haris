/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * FrmPRApproval.java
 *
 * Created on Jul 29, 2010, 11:51:31 AM
 */

package apotek;

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
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JCheckBox;
import javax.swing.JFormattedTextField;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
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
import net.sf.jasperreports.engine.type.OrientationEnum;
import net.sf.jasperreports.engine.util.JRLoader;
import net.sf.jasperreports.view.JasperViewer;
import org.jdesktop.swingx.JXTable;
import tableRender.ColumnGroup;
import tableRender.GroupableTableHeader;

/**
 *
 * @author ustadho
 */
public class FrmTransferHistory extends javax.swing.JInternalFrame {
    private Connection conn;
    private boolean bAcc1= false, bAcc2=false;
    private String  sAcc1= "", sAcc2="";
    private GeneralFunction fn;
    boolean cumaSingDurungApprove=true;
    private JFormattedTextField jFDate1;
    MyKeyListener kListener=new MyKeyListener();
    JDesktopImage desktop;

    /** Creates new form FrmPRApproval */
    public FrmTransferHistory(Connection con) {
        initComponents();
        this.conn=con;
        
        fn=new GeneralFunction();
        
        fn.addKeyListenerInContainer(jPanel1, kListener, txtFocusListener);
        tblHeader.addKeyListener(kListener);
        tblDetail.addKeyListener(kListener);

        tblHeader.setRowHeight(22);

        MaskFormatter fmttgl = null;
        try {
            fmttgl = new MaskFormatter("##/##/####");
        } catch (java.text.ParseException e) {}

        jFDate1 = new JFormattedTextField(fmttgl);
        jFTgl.setFormatterFactory(jFDate1.getFormatterFactory());
        jFTglAkhir.setFormatterFactory(jFDate1.getFormatterFactory());

        tblHeader.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            public void valueChanged(ListSelectionEvent e) {
                btnCtk.setEnabled(tblHeader.getSelectedRow()>=0);
                if(conn!=null && tblHeader.getSelectedRow()<0) return;
                udfLoadItemDetail();
            }
        });

        //tblDetail.setHighlighters(HighlighterFactory.createSimpleStriping(HighlighterFactory.QUICKSILVER));
        udfInitForm();
    }

    private void udfPrint() {
        int iRow=tblHeader.getSelectedRow();
        if(iRow<0) return;
        try{
            HashMap reportParam = new HashMap();
            JasperReport jasperReport=null;
            reportParam.put("corporate", MainForm.sNamaUsaha);
            reportParam.put("alamat", MainForm.sAlamat);
            reportParam.put("telp", MainForm.sTelp);
            reportParam.put("no_mutasi", tblHeader.getValueAt(iRow, 0).toString());


            this.setCursor(new Cursor(Cursor.WAIT_CURSOR));
            //System.out.println(getClass().getResourceAsStream("Reports/TransferSiteByNo.jasper"));
            jasperReport = (JasperReport) JRLoader.loadObject(getClass().getResourceAsStream("Reports/TransferSiteByNo.jasper"));
            JasperPrint print = JasperFillManager.fillReport(jasperReport,reportParam,conn);
            print.setOrientation(jasperReport.getOrientationValue());
            this.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
            if(print.getPages().isEmpty())
                JOptionPane.showMessageDialog(this, "Report tidak ditemukan!");
            else
                JasperViewer.viewReport(print,false);

        }
        catch(JRException je){System.out.println(je.getMessage());}
        //catch(NullPointerException ne){JOptionPane.showMessageDialog(null, ne.getMessage(), MainForm.sMessage, JOptionPane.OK_OPTION);}

    }

    private void udfLoadItemDetail(){
        try{
            setCursor(new Cursor(Cursor.WAIT_CURSOR));
            DefaultTableModel myModelDetPR=(DefaultTableModel)tblDetail.getModel();

            tblDetail.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
            String sQry1="select d.item_code, coalesce(i.item_name,'') as item_name, " +
                    "coalesce(i.satuan_kecil,'') as uom, coalesce(d.jumlah,0) as qty " +
                    " from transfer_detail d " +
                    " left join barang i on i.item_code=d.item_code " +
                    " where kode_transfer='"+tblHeader.getValueAt(tblHeader.getSelectedRow(), 0).toString()+"' " +
                    "order by i.item_name " ;

           //System.out.println(sQry1);
            myModelDetPR.setRowCount(0);
            ResultSet rs1=conn.createStatement().executeQuery(sQry1);
            while (rs1.next()){
                myModelDetPR.addRow(new Object[]{
                    rs1.getString("item_code"),
                    rs1.getString("item_name"),
                    rs1.getString("uom"),
                    rs1.getDouble("qty")
                    });
                
            }

            
            tblDetail.setModel((DefaultTableModel) fn.autoResizeColWidth(tblDetail, (DefaultTableModel)tblDetail.getModel()).getModel());
            rs1.close();
            setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
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

    public void setDesktopImage(JDesktopImage ds){
        this.desktop=ds;
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
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage());
        }
        tblHeader.getColumn("Tanggal").setMinWidth(150); tblHeader.getColumn("Tanggal").setMaxWidth(150); tblHeader.getColumn("Tanggal").setPreferredWidth(150);
        tblHeader.getColumnModel().getColumn(0).setMinWidth(100); tblHeader.getColumnModel().getColumn(0).setMaxWidth(100); tblHeader.getColumnModel().getColumn(0).setPreferredWidth(100);
        tblHeader.getColumnModel().getColumn(2).setMinWidth(110); tblHeader.getColumnModel().getColumn(2).setMaxWidth(110); tblHeader.getColumnModel().getColumn(2).setPreferredWidth(110);
        tblHeader.getColumnModel().getColumn(3).setMinWidth(110); tblHeader.getColumnModel().getColumn(3).setMaxWidth(110); tblHeader.getColumnModel().getColumn(3).setPreferredWidth(110);
        tblHeader.getColumnModel().getColumn(4).setMinWidth(110); tblHeader.getColumnModel().getColumn(4).setMaxWidth(110); tblHeader.getColumnModel().getColumn(4).setPreferredWidth(110);
        tblHeader.getColumnModel().getColumn(5).setMinWidth(110); tblHeader.getColumnModel().getColumn(5).setMaxWidth(110); tblHeader.getColumnModel().getColumn(5).setPreferredWidth(110);

        tblHeader.getTableHeader().setReorderingAllowed(false);
        UIManager.put(GroupableTableHeader.uiClassID, "tableRender.GroupableTableHeaderUI");
        GroupableTableHeader header = new GroupableTableHeader(tblHeader.getColumnModel());
        TableColumnModel columns = tblHeader.getColumnModel();
        ColumnGroup from = new ColumnGroup("From");
        from.add(columns.getColumn(2));
        from.add(columns.getColumn(3));
        header.addGroup(from);

        ColumnGroup to = new ColumnGroup("To");
        to.add(columns.getColumn(4));
        to.add(columns.getColumn(5));
        header.addGroup(to);

        tblHeader.setTableHeader(header);
        tblDetail.setRowHeight(22);
        udfLoadTransfer();
            
        for(int i=0; i< tblHeader.getColumnCount(); i++){
            tblHeader.getColumnModel().getColumn(i).setCellRenderer(new MyRowRenderer());
        }
        jLabel16.setText(getTitle());
        
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                tblHeader.requestFocus();
            }
      });


    }

    private void udfLoadTransfer(){
        try{
            SimpleDateFormat dmy = new SimpleDateFormat("dd/MM/yyyy");
            SimpleDateFormat ymd = new SimpleDateFormat("yyyy-MM-dd");
            String sQry = "select kode_transfer, tanggal, coalesce(gudang_asal,'') as id_from, coalesce(s.deskripsi,'') as site_from, coalesce(gudang_tujuan,'') as id_to,  " +
                    "coalesce(s2.deskripsi,'') as site_to, coalesce(m.user_ins,'') as user_tr " +
                    "from transfer m " +
                    "left join gudang s on s.kode_gudang=m.gudang_asal " +
                    "left join gudang s2 on s2.kode_gudang=m.gudang_tujuan " +
                    "where " +
                    "to_char(m.tanggal, 'yyyy-MM-dd')>='" + ymd.format(dmy.parse(jFTgl.getText())) + "' and " +
                    "to_char(m.tanggal, 'yyyy-MM-dd')<='" + ymd.format(dmy.parse(jFTglAkhir.getText())) + "' " +
                    "order by tanggal desc";
            try {
                System.out.println(sQry);
                ResultSet rs = conn.createStatement().executeQuery(sQry);
                ((DefaultTableModel) tblHeader.getModel()).setNumRows(0);
                while (rs.next()) {
                    ((DefaultTableModel) tblHeader.getModel()).addRow(new Object[]{
                        rs.getString("kode_transfer"),
                        rs.getTimestamp("tanggal"),
                        rs.getString("id_from"),
                        rs.getString("site_from"),
                        rs.getString("id_to"),
                        rs.getString("site_to"),
                        rs.getString("user_tr")
                    });
                }
                if(tblHeader.getRowCount()>0)
                    tblHeader.setRowSelectionInterval(0, 0);
                
                //tblHeader.setModel((DefaultTableModel) fn.autoResizeColWidth(tblHeader, (DefaultTableModel)tblHeader.getModel()).getModel());
            } catch (SQLException se) {
                JOptionPane.showMessageDialog(this, se.getMessage());
            }
        }catch(ParseException ex){
            Logger.getLogger(FrmTransferHistory.class.getName()).log(Level.SEVERE, null,ex);
        }
    }

    SimpleDateFormat dmyFmt=new SimpleDateFormat("dd/MM/yyyy");
    SimpleDateFormat dmyFmt_hhmm=new SimpleDateFormat("dd/MM/yyyy hh:mm");

    private void udfSave() {
        String sUpdate="";

        for(int i=0; i<tblHeader.getRowCount(); i++){
            sUpdate+="Update phar_sr set "+
                    (bAcc1? "acc_level_1='"+ ((Boolean)tblHeader.getValueAt(i, 7)==true?MainForm.sUserName: "" )+"', acc_time1= now() ":" ")+
                    (bAcc1 && bAcc2? "," :" ")+
                    (bAcc2? "acc_level_2='"+ ((Boolean)tblHeader.getValueAt(i, 6)==true?MainForm.sUserName: "" )+"', acc_time2=now() ":" ")+
                    "where no_sr='"+tblHeader.getValueAt(i, 0).toString()+"'; " ;
        }

        //System.out.println(sUpdate);

        try{
            conn.setAutoCommit(false);
            int i=conn.createStatement().executeUpdate(sUpdate);
            conn.setAutoCommit(true);
            if(i>0){
                JOptionPane.showMessageDialog(this, "Simpan Store Requisition approval Sukses!");
            }
        }catch(SQLException se){
            try {
                conn.rollback();
                JOptionPane.showMessageDialog(this, se.getMessage());
            } catch (SQLException ex) {
                Logger.getLogger(FrmTransferHistory.class.getName()).log(Level.SEVERE, null, ex);
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
            }else if (value instanceof Boolean) { // Boolean
                checkBox.setSelected(((Boolean) value).booleanValue());
                checkBox.setHorizontalAlignment(jLabel1.CENTER);
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
        btnCtk = new javax.swing.JButton();
        jLabel16 = new javax.swing.JLabel();
        jLabel1 = new javax.swing.JLabel();
        jFTgl = new javax.swing.JFormattedTextField();
        jLabel2 = new javax.swing.JLabel();
        jFTglAkhir = new javax.swing.JFormattedTextField();
        btnFilter1 = new javax.swing.JButton();
        jXTitledPanel1 = new org.jdesktop.swingx.JXTitledPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        tblDetail = new org.jdesktop.swingx.JXTable();
        jScrollPane1 = new javax.swing.JScrollPane();
        tblHeader = new javax.swing.JTable();

        setClosable(true);
        setIconifiable(true);
        setMaximizable(true);
        setResizable(true);
        setTitle("Histori Transfer Antar Gudang");
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
        btnClose.setToolTipText("");
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

        btnCtk.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/Icon/buttons/print-32.png"))); // NOI18N
        btnCtk.setText("Print");
        btnCtk.setToolTipText("");
        btnCtk.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnCtk.setMargin(new java.awt.Insets(2, 2, 2, 2));
        btnCtk.setMaximumSize(new java.awt.Dimension(40, 40));
        btnCtk.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnCtk.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCtkActionPerformed(evt);
            }
        });
        jPanel1.add(btnCtk, new org.netbeans.lib.awtextra.AbsoluteConstraints(690, 10, 50, 60));

        jLabel16.setFont(new java.awt.Font("Tahoma", 0, 24)); // NOI18N
        jLabel16.setForeground(new java.awt.Color(0, 0, 153));
        jLabel16.setText("Histori Transfer Barang");
        jPanel1.add(jLabel16, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 520, 40));

        jLabel1.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel1.setText("Dari : ");
        jPanel1.add(jLabel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(170, 50, 110, 20));

        jFTgl.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 51, 153), 1, true));
        jFTgl.setFont(new java.awt.Font("Tahoma", 0, 12));
        jPanel1.add(jFTgl, new org.netbeans.lib.awtextra.AbsoluteConstraints(280, 50, 102, 22));

        jLabel2.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jLabel2.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel2.setText("s/d");
        jPanel1.add(jLabel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(380, 50, 30, 20));

        jFTglAkhir.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 51, 153), 1, true));
        jFTglAkhir.setFont(new java.awt.Font("Tahoma", 0, 12));
        jPanel1.add(jFTglAkhir, new org.netbeans.lib.awtextra.AbsoluteConstraints(410, 50, 96, 22));

        btnFilter1.setText("Tampilkan");
        btnFilter1.setToolTipText("Fiter");
        btnFilter1.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));
        btnFilter1.setMaximumSize(new java.awt.Dimension(40, 40));
        btnFilter1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnFilter1ActionPerformed(evt);
            }
        });
        jPanel1.add(btnFilter1, new org.netbeans.lib.awtextra.AbsoluteConstraints(510, 50, 110, 25));

        jXTitledPanel1.setTitle("Item Detail");
        jXTitledPanel1.setTitleFont(new java.awt.Font("Tahoma", 1, 12));
        jXTitledPanel1.setFont(new java.awt.Font("Tahoma", 1, 12));
        jXTitledPanel1.getContentContainer().setLayout(new javax.swing.BoxLayout(jXTitledPanel1.getContentContainer(), javax.swing.BoxLayout.LINE_AXIS));

        tblDetail.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Kode Barang", "Nama Barang", "UOM", "Qty"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Double.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        tblDetail.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_OFF);
        tblDetail.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                tblDetailKeyPressed(evt);
            }
        });
        jScrollPane2.setViewportView(tblDetail);

        jXTitledPanel1.getContentContainer().add(jScrollPane2);

        tblHeader.setAutoCreateRowSorter(true);
        tblHeader.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        tblHeader.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "No. Mutasi", "Tanggal", "Site ID", "Site Name", "Site ID", "Site Name", "Username"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        tblHeader.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_OFF);
        tblHeader.getTableHeader().setReorderingAllowed(false);
        jScrollPane1.setViewportView(tblHeader);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 808, Short.MAX_VALUE))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(10, 10, 10)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jXTitledPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, 808, Short.MAX_VALUE)
                            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, 808, Short.MAX_VALUE))))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 168, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jXTitledPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, 386, Short.MAX_VALUE)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnCloseActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCloseActionPerformed
        dispose();
}//GEN-LAST:event_btnCloseActionPerformed

    private void btnCtkActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCtkActionPerformed
        udfPrint();
}//GEN-LAST:event_btnCtkActionPerformed

    private void btnFilter1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnFilter1ActionPerformed
        udfLoadTransfer();
}//GEN-LAST:event_btnFilter1ActionPerformed

    private void formInternalFrameOpened(javax.swing.event.InternalFrameEvent evt) {//GEN-FIRST:event_formInternalFrameOpened
        jLabel16.setText(getTitle());
    }//GEN-LAST:event_formInternalFrameOpened

    private void tblDetailKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_tblDetailKeyPressed
        
    }//GEN-LAST:event_tblDetailKeyPressed

    private Boolean okCtk(String sNo_sr){
        boolean ok=false;
        try{
            Statement statt=conn.createStatement();
            ResultSet rss=statt.executeQuery("select (coalesce(acc_level_1,'')<>'' and coalesce(acc_level_2,'')<>'') " +
                    "from phar_sr where no_sr='"+sNo_sr.trim()+"'");
            if (rss.next()) ok=rss.getBoolean(1);
            rss.close();
            statt.close();
        }catch(SQLException se){}
        return ok;
    }
    
    private void printPR(String sNo_PR){
        String usr[][]= new String[2][2];
        byte[] pic1=null,pic2=null;
        String sUser1="", sUser2="";
        setCursor(new Cursor(Cursor.WAIT_CURSOR));
        try{
            int ii=0;
            String sQry="select coalesce(acc_level_1,'') as user1, " +
                    "coalesce(acc_level_2,'') as user2 from phar_sr where no_sr='"+sNo_PR+"'";
            //System.out.println(sQry);

            Statement statt=conn.createStatement();
            ResultSet rss=conn.createStatement().executeQuery(sQry);
            if(rss.next()){
                sUser1=rss.getString("user1");
                sUser2=rss.getString("user2");
            }
            rss.close();

            sQry="select ud.user_name, ac.level, coalesce(ud.complete_user_name,'') as complete_name,  p.jabatan, p.singkatan, ud.ttd_electronic " +
                    "from user_detail ud " +
                    "left join user_acc ac on ac.user_id=ud.user_id " +
                    "left join pejabat p on p.kode_jabatan=ud.kode_jabatan " +
                    "where ud.user_name in('"+sUser1+"', '"+sUser2+"') " +
                    "order by level limit 2";
            rss=statt.executeQuery(sQry);

             //System.out.println(sQry);

            while (rss.next()) {
                usr[ii]=new String[]{rss.getString("jabatan"),rss.getString("complete_name")};
                if (ii==0)
                    pic1=rss.getBytes("ttd_electronic");
                else pic2=rss.getBytes("ttd_electronic");
                ii++;
            }
            rss.close();
            statt.close();


        }catch(SQLException se){
            setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
            JOptionPane.showMessageDialog(this, se.getMessage());
            return;
        }
      try{
            JasperReport jasperReportmkel = (JasperReport) JRLoader.loadObject(getClass().getResourceAsStream("Reports/PR.jasper"));
            HashMap parameter = new HashMap();
            parameter.put("no_pr",sNo_PR);
            parameter.put("acc_1",usr[0][0]);
            parameter.put("acc_2",usr[1][0]);
            parameter.put("acc_name1",usr[0][1]);
            parameter.put("acc_name2",usr[1][1]);
            parameter.put("img_acc1",pic1==null? null: new ByteArrayInputStream((byte[])pic1));
            parameter.put("img_acc2",pic2==null? null:  new ByteArrayInputStream((byte[])pic2));

            JasperPrint jasperPrintmkel = JasperFillManager.fillReport(jasperReportmkel, parameter, conn);
            jasperPrintmkel.setOrientation(OrientationEnum.PORTRAIT);
            JasperViewer.viewReport(jasperPrintmkel, false);
      } catch(JRException je){
          setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
          System.out.println(je.getMessage());
      }
  }


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnClose;
    private javax.swing.JButton btnCtk;
    private javax.swing.JButton btnFilter1;
    private javax.swing.JFormattedTextField jFTgl;
    private javax.swing.JFormattedTextField jFTglAkhir;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private org.jdesktop.swingx.JXTitledPanel jXTitledPanel1;
    private org.jdesktop.swingx.JXTable tblDetail;
    private javax.swing.JTable tblHeader;
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
