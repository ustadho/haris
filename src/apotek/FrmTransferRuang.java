/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * FrmTransferRuang.java
 *
 * Created on Jul 20, 2010, 11s:08:49 AM
 */

package apotek;

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
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.AbstractCellEditor;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFormattedTextField;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableModel;
import main.GeneralFunction;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.util.JRLoader;
import net.sf.jasperreports.view.JasperViewer;

/**
 *
 * @author ustadho
 */
public class FrmTransferRuang extends javax.swing.JInternalFrame {
    private GeneralFunction fn;
    private Connection conn;
    DlgLookupItemJual lookupItem=new DlgLookupItemJual(JOptionPane.getFrameForComponent(this), true);
    MyKeyListener kListener=new MyKeyListener();
    private boolean stKoreksi=false;
    private Object srcForm;

    /** Creates new form FrmTransferRuang */
    public FrmTransferRuang() {
        initComponents();
        tblItem.getTableHeader().setFont(new Font("Tahoma", 0, 12));
        tblItem.getModel().addTableModelListener(new MyTableModelListener(tblItem));
        tblItem.setRowHeight(22);
        TableColumnModel col=tblItem.getColumnModel();
        MyTableCellEditor cEditor=new MyTableCellEditor();

        tblItem.getColumnModel().getColumn(col.getColumnIndex("Qty")).setCellEditor(cEditor);
        tblItem.getColumn("Keterangan").setPreferredWidth(200);
        
        tblItem.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            public void valueChanged(ListSelectionEvent e) {
                int iRow=tblItem.getSelectedRow();
                if(iRow<0||conn==null ) return;
                if(tblItem.getValueAt(iRow, 0)==null||tblItem.getValueAt(iRow, 0).toString().equalsIgnoreCase("")) return;
                udfShowStock();
            }
        });

        tblItem.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).
        put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), "selectNextColumnCell");
    }

    public void setKoreksi(boolean b){
        this.stKoreksi=b;
    }

    public void setTransferNo(String s){
        txtTransferNo.setText(s);
    }

    private void udfShowStock(){
        try{
            int iRow=tblItem.getSelectedRow();
            String sKodeBarang=tblItem.getValueAt(iRow, 0).toString();

            String sQry="select 'src' as tp, coalesce(sum(coalesce(jumlah,0)),0) as qty from stock where item_code='"+sKodeBarang+"' and kode_gudang='"+txtSiteFrom.getText()+"' " +
                    "union all " +
                    "select 'dest',   coalesce(sum(coalesce(jumlah,0)),0) from stock where item_code='"+sKodeBarang+"' and kode_gudang='"+txtSiteTo.getText()+"'" ;

            //System.out.println(sQry);
            ResultSet rs=conn.createStatement().executeQuery(sQry);
            while(rs.next()){
                if(rs.getString("tp").equalsIgnoreCase("src")) txtQtySrc.setText(fn.dFmt.format(rs.getDouble("qty")));
                if(rs.getString("tp").equalsIgnoreCase("dest")) txtQtyDest.setText(fn.dFmt.format(rs.getDouble("qty")));
            }
            rs.close();
        }catch(SQLException se){
            JOptionPane.showMessageDialog(null, se.getMessage());
        }
    }

    private void udfInitForm(){
        fn=new GeneralFunction(conn);
        lookupItem.setConn(conn);
        fn.addKeyListenerInContainer(jToolBar1, kListener, txtFocusListener);
        fn.addKeyListenerInContainer(jPanel1, kListener, txtFocusListener);
        fn.addKeyListenerInContainer(jPanel2, kListener, txtFocusListener);
        tblItem.addKeyListener(kListener);
        txtTransferNo.setEnabled(stKoreksi);

        try{
            ResultSet rs=conn.createStatement().executeQuery("select to_char(current_date, 'dd/MM/yyyy')");
            rs.next();
            txtDate.setText(rs.getString(1));

            rs.close();
        }catch(SQLException se){
            JOptionPane.showMessageDialog(this, se.getMessage());
        }

        Runnable doRun = new Runnable() {
            public void run() {
                if(!stKoreksi)
                    txtSiteFrom.requestFocusInWindow();
                else
                    txtTransferNo.requestFocusInWindow();
            }
        };
        SwingUtilities.invokeLater(doRun);
        txtReceiptBy.setText(MainForm.sUserName);
        btnNew.setEnabled(false);

        //if(txtSRNo.getText().length()==0){
//        txtSiteFrom.setText(MainForm.sKodeGudang);
//        lblSiteFrom.setText(MainForm.sNamaGudang);
            udfNew();
        //}

    }

    private void udfNew(){
        txtTransferNo.setText("");
        btnNew.setEnabled(false);
        //txtTransferType.setText(""); lblTransferType.setText("");
        //txtSiteFrom.setText(""); lblSiteFrom.setText("");
        txtSiteTo.setText(""); lblSiteTo.setText("");
        txtRemark.setText("");
        //txtSRNo.setText("");
        ((DefaultTableModel)tblItem.getModel()).setNumRows(0);
        txtSiteFrom.requestFocus();
    }

    private void udfLoadTransferRuang(){
        try{
            String sQry="select h.no_mutasi, to_char(h.tanggal, 'dd/MM/yyyy') as tanggal, coalesce(h.type_id,'') as type_id, coalesce(tp.type_name,'') as type_name," +
                    "coalesce(h.site_id_from,'') as site_from_id, coalesce(s1.site_name,'') as site_from_name, " +
                    "coalesce(h.site_id_to,'') as site_to_id, coalesce(s2.site_name,'') as site_to_name, " +
                    "coalesce(h.keterangan,'') as keterangan, flag_trx " +
                    "from phar_mutasi h " +
                    "left join phar_transfer_type tp on tp.type_id=h.type_id " +
                    "left join phar_site s1 on s1.site_id=h.site_id_from " +
                    "left join phar_site s2 on s2.site_id=h.site_id_to " +
                    "where h.no_mutasi='"+txtTransferNo.getText()+"'"; 
            
            ResultSet rs=conn.createStatement().executeQuery(sQry);
            ((DefaultTableModel)tblItem.getModel()).setNumRows(0);

            if(rs.next()){
                if(rs.getString("flag_trx").equalsIgnoreCase("K")){
                    JOptionPane.showMessageDialog(this, "Transaksi ini sudah pernah dikoreksi!");
                    rs.close();
                    udfNew();
                    txtTransferNo.requestFocus();
                    return;
                }

//                txtTransferType.setText(rs.getString("type_id"));
//                lblTransferType.setText(rs.getString("type_name"));
                txtSiteFrom.setText(rs.getString("site_from_id"));
                lblSiteFrom.setText(rs.getString("site_from_name"));
                txtSiteTo.setText(rs.getString("site_to_id"));
                lblSiteTo.setText(rs.getString("site_to_name"));
                txtRemark.setText(rs.getString("keterangan"));

                rs.close();
                sQry="select d.kode_barang, coalesce(i.nama_barang,'') as nama_barang, coalesce(d.jumlah,0) as qty," +
                        "coalesce(i.uom_kecil,'') as uom_kecil, " +
                        "coalesce((select uom_alt from phar_supp_barang where kode_barang =d.kode_barang order by priority limit 1), i.uom_kecil) as uom_alt," +
                        "coalesce((select convertion from phar_supp_barang where kode_barang =d.kode_barang order by priority limit 1), 1) as convertion " +
                        "from phar_mutasi_detail d " +
                        "left join phar_item i on i.kode_barang=d.kode_barang " +
                        "where no_mutasi='"+txtTransferNo.getText()+"' order by 2";

                rs=conn.createStatement().executeQuery(sQry);
                while(rs.next()){
                    ((DefaultTableModel)tblItem.getModel()).addRow(new Object[]{
                        rs.getString("kode_barang"),
                        rs.getString("nama_barang"),
                        rs.getInt("qty"),
                        rs.getString("uom_kecil"),
                        rs.getString("uom_alt"),
                        rs.getInt("convertion"),
                        0
                    });
                }

                if(tblItem.getRowCount()>0)
                    tblItem.setRowSelectionInterval(0, 0);
            }

            rs.close();
        }catch(SQLException se){
            JOptionPane.showMessageDialog(this, se.getMessage());
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

        jToolBar1 = new javax.swing.JToolBar();
        btnNew = new javax.swing.JButton();
        btnSave = new javax.swing.JButton();
        btnCancel = new javax.swing.JButton();
        jLabel16 = new javax.swing.JLabel();
        jPanel1 = new javax.swing.JPanel();
        jLabel18 = new javax.swing.JLabel();
        txtSiteFrom = new javax.swing.JTextField();
        jLabel6 = new javax.swing.JLabel();
        txtRemark = new javax.swing.JTextField();
        lblSiteFrom = new javax.swing.JLabel();
        jLabel21 = new javax.swing.JLabel();
        jLabel22 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        txtDate = new javax.swing.JTextField();
        jLabel17 = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        txtReceiptBy = new javax.swing.JTextField();
        jLabel13 = new javax.swing.JLabel();
        jLabel19 = new javax.swing.JLabel();
        txtTransferNo = new javax.swing.JTextField();
        jLabel15 = new javax.swing.JLabel();
        jLabel23 = new javax.swing.JLabel();
        txtSiteTo = new javax.swing.JTextField();
        lblSiteTo = new javax.swing.JLabel();
        jLabel24 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        tblItem = new javax.swing.JTable();
        jPanel2 = new javax.swing.JPanel();
        jLabel25 = new javax.swing.JLabel();
        txtQtySrc = new javax.swing.JLabel();
        jLabel26 = new javax.swing.JLabel();
        txtQtyDest = new javax.swing.JLabel();
        jSeparator2 = new javax.swing.JSeparator();

        setClosable(true);
        setIconifiable(true);
        setMaximizable(true);
        setResizable(true);
        setTitle("Transfer Antar Ruang");
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

        jToolBar1.setRollover(true);

        btnNew.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/Icon/buttons/add-32.png"))); // NOI18N
        btnNew.setText("New");
        btnNew.setFocusable(false);
        btnNew.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnNew.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnNew.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnNewActionPerformed(evt);
            }
        });
        jToolBar1.add(btnNew);

        btnSave.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/Icon/buttons/Ok-32.png"))); // NOI18N
        btnSave.setText("Save");
        btnSave.setFocusable(false);
        btnSave.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnSave.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnSave.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSaveActionPerformed(evt);
            }
        });
        jToolBar1.add(btnSave);

        btnCancel.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/Icon/buttons/exit-32.png"))); // NOI18N
        btnCancel.setText("Exit");
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
        jLabel16.setText("Transfer Barang");

        jPanel1.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        jPanel1.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel18.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jLabel18.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel18.setText("Gudang Asal");
        jPanel1.add(jLabel18, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 10, 100, 20));

        txtSiteFrom.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
        txtSiteFrom.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 1, true));
        txtSiteFrom.setDisabledTextColor(new java.awt.Color(153, 153, 153));
        txtSiteFrom.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                txtSiteFromFocusLost(evt);
            }
        });
        txtSiteFrom.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtSiteFromKeyReleased(evt);
            }
        });
        jPanel1.add(txtSiteFrom, new org.netbeans.lib.awtextra.AbsoluteConstraints(110, 10, 60, 20));

        jLabel6.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jLabel6.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel6.setText("Catatan");
        jPanel1.add(jLabel6, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 60, 90, 20));

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
        jPanel1.add(txtRemark, new org.netbeans.lib.awtextra.AbsoluteConstraints(110, 60, 390, 20));

        lblSiteFrom.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
        lblSiteFrom.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 1, true));
        lblSiteFrom.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                lblSiteFromPropertyChange(evt);
            }
        });
        jPanel1.add(lblSiteFrom, new org.netbeans.lib.awtextra.AbsoluteConstraints(170, 10, 260, 20));

        jLabel21.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jLabel21.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel21.setText(":");
        jPanel1.add(jLabel21, new org.netbeans.lib.awtextra.AbsoluteConstraints(100, 10, 10, 20));

        jLabel22.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jLabel22.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel22.setText(":");
        jPanel1.add(jLabel22, new org.netbeans.lib.awtextra.AbsoluteConstraints(100, 60, 10, 20));

        jLabel3.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jLabel3.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel3.setText("Date");
        jPanel1.add(jLabel3, new org.netbeans.lib.awtextra.AbsoluteConstraints(530, 30, 70, 20));

        txtDate.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
        txtDate.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 1, true));
        txtDate.setDisabledTextColor(new java.awt.Color(0, 0, 0));
        txtDate.setEnabled(false);
        txtDate.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                txtDateFocusLost(evt);
            }
        });
        txtDate.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtDateKeyReleased(evt);
            }
        });
        jPanel1.add(txtDate, new org.netbeans.lib.awtextra.AbsoluteConstraints(620, 30, 90, 20));

        jLabel17.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jLabel17.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel17.setText("Transfer No.");
        jPanel1.add(jLabel17, new org.netbeans.lib.awtextra.AbsoluteConstraints(530, 10, 90, 20));

        jLabel9.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jLabel9.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel9.setText("User Trx");
        jPanel1.add(jLabel9, new org.netbeans.lib.awtextra.AbsoluteConstraints(530, 50, 70, 20));

        txtReceiptBy.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
        txtReceiptBy.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 1, true));
        txtReceiptBy.setDisabledTextColor(new java.awt.Color(0, 0, 0));
        txtReceiptBy.setEnabled(false);
        txtReceiptBy.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                txtReceiptByFocusLost(evt);
            }
        });
        txtReceiptBy.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtReceiptByKeyReleased(evt);
            }
        });
        jPanel1.add(txtReceiptBy, new org.netbeans.lib.awtextra.AbsoluteConstraints(620, 50, 120, 20));

        jLabel13.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jLabel13.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel13.setText(":");
        jPanel1.add(jLabel13, new org.netbeans.lib.awtextra.AbsoluteConstraints(610, 30, 10, 20));

        jLabel19.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jLabel19.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel19.setText(":");
        jPanel1.add(jLabel19, new org.netbeans.lib.awtextra.AbsoluteConstraints(610, 50, 10, 20));

        txtTransferNo.setFont(new java.awt.Font("Dialog", 0, 14)); // NOI18N
        txtTransferNo.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 1, true));
        txtTransferNo.setDisabledTextColor(new java.awt.Color(0, 0, 0));
        txtTransferNo.setEnabled(false);
        txtTransferNo.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                txtTransferNoFocusLost(evt);
            }
        });
        txtTransferNo.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                txtTransferNoPropertyChange(evt);
            }
        });
        txtTransferNo.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtTransferNoKeyReleased(evt);
            }
            public void keyTyped(java.awt.event.KeyEvent evt) {
                txtTransferNoKeyTyped(evt);
            }
        });
        txtTransferNo.addVetoableChangeListener(new java.beans.VetoableChangeListener() {
            public void vetoableChange(java.beans.PropertyChangeEvent evt)throws java.beans.PropertyVetoException {
                txtTransferNoVetoableChange(evt);
            }
        });
        jPanel1.add(txtTransferNo, new org.netbeans.lib.awtextra.AbsoluteConstraints(620, 10, 120, 20));

        jLabel15.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jLabel15.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel15.setText(":");
        jPanel1.add(jLabel15, new org.netbeans.lib.awtextra.AbsoluteConstraints(610, 10, 10, 20));

        jLabel23.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jLabel23.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel23.setText("Gudang Tujuan");
        jPanel1.add(jLabel23, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 35, 100, 20));

        txtSiteTo.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
        txtSiteTo.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 1, true));
        txtSiteTo.setDisabledTextColor(new java.awt.Color(153, 153, 153));
        txtSiteTo.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                txtSiteToFocusLost(evt);
            }
        });
        txtSiteTo.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtSiteToKeyReleased(evt);
            }
        });
        jPanel1.add(txtSiteTo, new org.netbeans.lib.awtextra.AbsoluteConstraints(110, 35, 60, 20));

        lblSiteTo.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
        lblSiteTo.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 1, true));
        lblSiteTo.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                lblSiteToPropertyChange(evt);
            }
        });
        jPanel1.add(lblSiteTo, new org.netbeans.lib.awtextra.AbsoluteConstraints(170, 35, 260, 20));

        jLabel24.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jLabel24.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel24.setText(":");
        jPanel1.add(jLabel24, new org.netbeans.lib.awtextra.AbsoluteConstraints(100, 35, 10, 20));

        tblItem.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        tblItem.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Product ID", "Keterangan", "Qty", "Sat Kecil"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Object.class, java.lang.Object.class, java.lang.Double.class, java.lang.Object.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, true, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        tblItem.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_OFF);
        tblItem.getTableHeader().setReorderingAllowed(false);
        jScrollPane1.setViewportView(tblItem);

        jPanel2.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        jPanel2.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel25.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jLabel25.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel25.setText("Source Quantity : ");
        jPanel2.add(jLabel25, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 10, 110, 20));

        txtQtySrc.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
        txtQtySrc.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        txtQtySrc.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 1, true));
        txtQtySrc.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                txtQtySrcPropertyChange(evt);
            }
        });
        jPanel2.add(txtQtySrc, new org.netbeans.lib.awtextra.AbsoluteConstraints(120, 10, 80, 20));

        jLabel26.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jLabel26.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel26.setText("Target Quantity :");
        jPanel2.add(jLabel26, new org.netbeans.lib.awtextra.AbsoluteConstraints(310, 10, 140, 20));

        txtQtyDest.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
        txtQtyDest.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        txtQtyDest.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 1, true));
        txtQtyDest.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                txtQtyDestPropertyChange(evt);
            }
        });
        jPanel2.add(txtQtyDest, new org.netbeans.lib.awtextra.AbsoluteConstraints(460, 10, 70, 20));

        jSeparator2.setOrientation(javax.swing.SwingConstants.VERTICAL);
        jPanel2.add(jSeparator2, new org.netbeans.lib.awtextra.AbsoluteConstraints(290, 0, 10, 80));

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(10, 10, 10)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel16, javax.swing.GroupLayout.PREFERRED_SIZE, 520, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(90, 90, 90)
                        .addComponent(jToolBar1, javax.swing.GroupLayout.PREFERRED_SIZE, 140, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, 755, Short.MAX_VALUE)
                            .addComponent(jScrollPane1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 755, Short.MAX_VALUE)
                            .addComponent(jPanel2, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 755, Short.MAX_VALUE))
                        .addContainerGap())))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(11, 11, 11)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel16, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jToolBar1, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, 87, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 293, Short.MAX_VALUE)
                .addGap(7, 7, 7)
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnNewActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnNewActionPerformed
        udfNew();
}//GEN-LAST:event_btnNewActionPerformed

    private void btnSaveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSaveActionPerformed
        udfSave();
}//GEN-LAST:event_btnSaveActionPerformed

    private void btnCancelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCancelActionPerformed
        if(btnCancel.getText().equalsIgnoreCase("cancel")){
            btnSave.setEnabled(false);
            btnNew.setEnabled(true);
            btnCancel.setText("Exit");
            btnCancel.setIcon(new javax.swing.ImageIcon(getClass().getResource("/pharpurchase/image/Icon/Exit.png")));
        }else{
            this.dispose();
        }
}//GEN-LAST:event_btnCancelActionPerformed

    private void txtSiteFromFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtSiteFromFocusLost
        // TODO add your handling code here:
}//GEN-LAST:event_txtSiteFromFocusLost

    private void txtSiteFromKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtSiteFromKeyReleased
        fn.lookup(evt, new Object[]{lblSiteFrom},
                "select kode_gudang, coalesce(deskripsi,'') as nama_gudang from gudang " +
                "where upper(kode_gudang||coalesce(deskripsi,'')) Like upper('%" + txtSiteFrom.getText() +"%') order by 1",
                txtSiteFrom.getWidth()+lblSiteFrom.getWidth()+15, 300);
}//GEN-LAST:event_txtSiteFromKeyReleased

    private void txtRemarkFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtRemarkFocusLost
        // TODO add your handling code here:
}//GEN-LAST:event_txtRemarkFocusLost

    private void txtRemarkKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtRemarkKeyReleased
        // TODO add your handling code here:
}//GEN-LAST:event_txtRemarkKeyReleased

    private void lblSiteFromPropertyChange(java.beans.PropertyChangeEvent evt) {//GEN-FIRST:event_lblSiteFromPropertyChange
        // TODO add your handling code here:
}//GEN-LAST:event_lblSiteFromPropertyChange

    private void txtDateFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtDateFocusLost
        // TODO add your handling code here:
}//GEN-LAST:event_txtDateFocusLost

    private void txtDateKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtDateKeyReleased
        // TODO add your handling code here:
}//GEN-LAST:event_txtDateKeyReleased

    private void txtReceiptByFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtReceiptByFocusLost
        // TODO add your handling code here:
}//GEN-LAST:event_txtReceiptByFocusLost

    private void txtReceiptByKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtReceiptByKeyReleased
        // TODO add your handling code here:
}//GEN-LAST:event_txtReceiptByKeyReleased

    private void txtTransferNoFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtTransferNoFocusLost
        // TODO add your handling code here:
}//GEN-LAST:event_txtTransferNoFocusLost

    private void txtTransferNoPropertyChange(java.beans.PropertyChangeEvent evt) {//GEN-FIRST:event_txtTransferNoPropertyChange
        // TODO add your handling code here:
}//GEN-LAST:event_txtTransferNoPropertyChange

    private void txtTransferNoKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtTransferNoKeyReleased
        // TODO add your handling code here:
}//GEN-LAST:event_txtTransferNoKeyReleased

    private void txtTransferNoKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtTransferNoKeyTyped
        // TODO add your handling code here:
}//GEN-LAST:event_txtTransferNoKeyTyped

    private void txtTransferNoVetoableChange(java.beans.PropertyChangeEvent evt)throws java.beans.PropertyVetoException {//GEN-FIRST:event_txtTransferNoVetoableChange
        // TODO add your handling code here:
}//GEN-LAST:event_txtTransferNoVetoableChange

    private void txtSiteToFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtSiteToFocusLost
        // TODO add your handling code here:
    }//GEN-LAST:event_txtSiteToFocusLost

    private void txtSiteToKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtSiteToKeyReleased
        fn.lookup(evt, new Object[]{lblSiteTo},
                "select kode_gudang, coalesce(deskripsi,'') as nama_gudang from gudang " +
                "where upper(kode_gudang||coalesce(deskripsi,'')) iLike ('%" + txtSiteTo.getText() +"%') order by 2",
                txtSiteTo.getWidth()+lblSiteTo.getWidth()+15, 300);
    }//GEN-LAST:event_txtSiteToKeyReleased

    private void lblSiteToPropertyChange(java.beans.PropertyChangeEvent evt) {//GEN-FIRST:event_lblSiteToPropertyChange
        // TODO add your handling code here:
    }//GEN-LAST:event_lblSiteToPropertyChange

    private void txtQtySrcPropertyChange(java.beans.PropertyChangeEvent evt) {//GEN-FIRST:event_txtQtySrcPropertyChange
        // TODO add your handling code here:
}//GEN-LAST:event_txtQtySrcPropertyChange

    private void txtQtyDestPropertyChange(java.beans.PropertyChangeEvent evt) {//GEN-FIRST:event_txtQtyDestPropertyChange
        // TODO add your handling code here:
}//GEN-LAST:event_txtQtyDestPropertyChange

    private void formInternalFrameOpened(javax.swing.event.InternalFrameEvent evt) {//GEN-FIRST:event_formInternalFrameOpened
        udfInitForm();
    }//GEN-LAST:event_formInternalFrameOpened

    private void formInternalFrameClosed(javax.swing.event.InternalFrameEvent evt) {//GEN-FIRST:event_formInternalFrameClosed
        fn.setVisibleList(false);
    }//GEN-LAST:event_formInternalFrameClosed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnCancel;
    private javax.swing.JButton btnNew;
    private javax.swing.JButton btnSave;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel18;
    private javax.swing.JLabel jLabel19;
    private javax.swing.JLabel jLabel21;
    private javax.swing.JLabel jLabel22;
    private javax.swing.JLabel jLabel23;
    private javax.swing.JLabel jLabel24;
    private javax.swing.JLabel jLabel25;
    private javax.swing.JLabel jLabel26;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JSeparator jSeparator2;
    private javax.swing.JToolBar jToolBar1;
    private javax.swing.JLabel lblSiteFrom;
    private javax.swing.JLabel lblSiteTo;
    private javax.swing.JTable tblItem;
    private javax.swing.JTextField txtDate;
    private javax.swing.JLabel txtQtyDest;
    private javax.swing.JLabel txtQtySrc;
    private javax.swing.JTextField txtReceiptBy;
    private javax.swing.JTextField txtRemark;
    private javax.swing.JTextField txtSiteFrom;
    private javax.swing.JTextField txtSiteTo;
    private javax.swing.JTextField txtTransferNo;
    // End of variables declaration//GEN-END:variables

    public void setConn(Connection conn) {
        this.conn=conn;
    }
    // End of variables declaration

    public class MyKeyListener extends KeyAdapter {
        @Override
        public void keyPressed(KeyEvent evt) {
            Component ct = KeyboardFocusManager.getCurrentKeyboardFocusManager().getPermanentFocusOwner();
            int keyKode = evt.getKeyCode();
            switch(keyKode){
                case KeyEvent.VK_INSERT:{
                        lookupItem.setSrcTable(tblItem, tblItem.getColumnModel().getColumnIndex("Qty"));
                        lookupItem.udfInitForm();
                        lookupItem.setObjForm(this);
                        lookupItem.setKeyEvent(evt);
                        lookupItem.setVisible(true);
                        if(lookupItem.getKodeBarang().length()>0){
                            ((DefaultTableModel)tblItem.getModel()).setNumRows(tblItem.getRowCount()+1);
                            tblItem.setRowSelectionInterval(tblItem.getRowCount()-1, tblItem.getRowCount()-1);
                            tblItem.requestFocusInWindow();
                            tblItem.grabFocus();
                            tblItem.setValueAt(lookupItem.getKodeBarang(), tblItem.getRowCount()-1,0);
                            tblItem.changeSelection(tblItem.getRowCount()-1, 2, false, false);
                              
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
                    if(evt.getSource().equals(tblItem) && tblItem.getSelectedRow()>=0){
                        int iRow[]= tblItem.getSelectedRows();
                        int rowPalingAtas=iRow[0];

                        TableModel tm= tblItem.getModel();

                        while(iRow.length>0) {
                            //JOptionPane.showMessageDialog(null, iRow[0]);
                            ((DefaultTableModel)tm).removeRow(tblItem.convertRowIndexToModel(iRow[0]));
                            iRow = tblItem.getSelectedRows();
                        }
                        tblItem.clearSelection();

                        if(tblItem.getRowCount()>0 && rowPalingAtas<tblItem.getRowCount()){
                            tblItem.setRowSelectionInterval(rowPalingAtas, rowPalingAtas);
                        }else{
                            if(tblItem.getRowCount()>0)
                                tblItem.setRowSelectionInterval(rowPalingAtas-1, rowPalingAtas-1);
                            else
                                tblItem.requestFocus();
                        }
                        if(tblItem.getSelectedRow()>=0)
                            tblItem.changeSelection(tblItem.getSelectedRow(), 0, false, false);
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
    private boolean  udfCekBeforeSave(){
//        if(txtTransferType.getText().trim().length()==0){
//            JOptionPane.showMessageDialog(this, "Transfer type harus diisi!");
//            txtTransferType.requestFocus();
//            return false;
//        }
        if(txtSiteFrom.getText().trim().length()==0){
            JOptionPane.showMessageDialog(this, "Silakan pilih nama Asal Site/ Ruang terlebih dulu!");
            txtSiteFrom.requestFocus();
            return false;
        }
        if(txtSiteTo.getText().trim().length()==0){
            JOptionPane.showMessageDialog(this, "Silakan pilih nama Tujuan Site/ Ruang terlebih dulu!");
            txtSiteTo.requestFocus();
            return false;
        }
        if(txtSiteTo.getText().trim().equalsIgnoreCase(txtSiteFrom.getText().trim())){
            JOptionPane.showMessageDialog(this, "Site asal dan tujuan tidak boleh sama!");
            txtSiteTo.requestFocus();
            return false;
        }
        if(!stKoreksi && tblItem.getRowCount()==0){
            JOptionPane.showMessageDialog(this, "Item yang akan dimasukkan masih kosong!");
            tblItem.requestFocus();
            return false;
        }
        for(int i=0; i<tblItem.getRowCount(); i++){
            if(fn.udfGetDouble(tblItem.getValueAt(i, tblItem.getColumnModel().getColumnIndex("Qty")))==0){
                JOptionPane.showMessageDialog(this, "Qty item pada baris ke : "+ (i+1) +" masih Nol!");
                tblItem.changeSelection(i, tblItem.getColumnModel().getColumnIndex("Qty"), false, false);
                return false;
            }
        }

        return true;
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
            if(mColIndex==0 && table.getSelectedRow()>=0){
                
                try{
                    String sKodeBarang=table.getValueAt(table.getSelectedRow(), 0).toString();
                    String sQry="select i.item_code, coalesce(nama_paten,'') as item_name, coalesce(satuan_kecil,'') as sat_kecil " +
                            "from barang i " +
                            "where i.item_code='"+sKodeBarang+"' " +
                            "";

                    ResultSet rs=conn.createStatement().executeQuery(sQry);
                    if(rs.next()){
                        TableColumnModel col=table.getColumnModel();
                        int iRow=table.getSelectedRow();

                        ((DefaultTableModel)tblItem.getModel()).setValueAt(rs.getString("item_name"), iRow, col.getColumnIndex("Keterangan"));
                        ((DefaultTableModel)tblItem.getModel()).setValueAt(new Double(0), iRow, col.getColumnIndex("Qty"));
                        ((DefaultTableModel)tblItem.getModel()).setValueAt(rs.getString("sat_kecil"), iRow, col.getColumnIndex("Sat Kecil"));
                        udfShowStock();

                    }
                    rs.close();
                }catch(SQLException se){
                    System.err.println(se.getMessage());
                }
            }
        }
    }

    private SimpleDateFormat ymd=new SimpleDateFormat("yyyy-MM-dd");
    private SimpleDateFormat dmy=new SimpleDateFormat("dd/MM/yyyy");

    private void udfSave(){
        if(!udfCekBeforeSave()) return;
        String sNoKoreksi="", sNewNo="";
        ResultSet rs=null;
        String sQry="", sIns="";
        try{
            conn.setAutoCommit(false);
            if(stKoreksi){
                sQry="select fn_phar_mutasi_koreksi('"+txtTransferNo.getText()+"', " +
                        "'"+MainForm.sUserID+"', '"+MainForm.sUserName+"')";
                rs=conn.createStatement().executeQuery(sQry);
                System.out.println(sQry);
                if(rs.next()){
                    sNoKoreksi=rs.getString(1);

                }
                rs.close();
            }

            if(tblItem.getRowCount()>0){
                rs=conn.createStatement().executeQuery("select fn_get_transfer_no('"+ymd.format(dmy.parse(txtDate.getText()))+"') ");

                if(rs.next()){
                    txtTransferNo.setText(rs.getString(1));
                    sNewNo=rs.getString(1);
                }
                rs.close();

                sIns="insert into transfer(kode_transfer, gudang_asal, gudang_tujuan, tanggal, keterangan, user_ins, shift) " +
                        "values('"+txtTransferNo.getText()+"', '"+txtSiteFrom.getText()+"', '"+txtSiteTo.getText()+"', " +
                        "'"+ymd.format(dmy.parse(txtDate.getText()))+"', " +
                        "'"+txtRemark.getText()+"', '"+txtReceiptBy.getText()+"', '"+MainForm.sShift+"'); ";

                TableColumnModel col=tblItem.getColumnModel();

                for(int i=0; i<tblItem.getRowCount(); i++){
                        sIns += "insert into transfer_detail(kode_transfer, item_code, jumlah) " +
                                "values('" + txtTransferNo.getText() + "', '" + tblItem.getValueAt(i, col.getColumnIndex("Product ID")).toString() + "', " +
                                 fn.udfGetInt(tblItem.getValueAt(i, col.getColumnIndex("Qty"))) + "); ";
                }

                

                //JOptionPane.showMessageDialog(this, "Simpan Transfer Antar Ruang Sukses!");
                //udfPrint();
            }
            //conn.setAutoCommit(false);
            int i=conn.createStatement().executeUpdate(sIns);
            conn.setAutoCommit(true);
            JOptionPane.showMessageDialog(this, stKoreksi? "Transfer Dikoreksi dengan nomor '"+sNoKoreksi+"', " +
                    (tblItem.getRowCount()>0? "dan dibenarkan dengan nomor baru '"+sNewNo+"' ": "")
                    : "Simpan Transfer Sukses!");

            //udfPrint(sNoKoreksi, sNewNo);

            udfNew();
        } catch (ParseException ex) {
            Logger.getLogger(FrmTransferRuang.class.getName()).log(Level.SEVERE, null, ex);
        }catch(SQLException se){
            try {
                conn.rollback();
                JOptionPane.showMessageDialog(this, se.getMessage());
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, se.getMessage());
            }
        }
    }

    private void udfPrint(String sNoKoreksi, String sNewPo) {
        try{
            HashMap reportParam = new HashMap();
            JasperReport jasperReport=null;
            if(sNoKoreksi.length()>0){
                reportParam.put("corporate", MainForm.sAlamat);
                reportParam.put("alamat", MainForm.sAlamat);
                reportParam.put("telp", MainForm.sTelp);
                reportParam.put("no_mutasi", sNoKoreksi);

                this.setCursor(new Cursor(Cursor.WAIT_CURSOR));
                System.out.println(getClass().getResourceAsStream("Reports/TransferSiteByNo.jasper"));
                jasperReport = (JasperReport) JRLoader.loadObject(getClass().getResourceAsStream("Reports/TransferSiteByNo.jasper"));
                JasperPrint print = JasperFillManager.fillReport(jasperReport,reportParam,conn);
                print.setOrientation(jasperReport.getOrientationValue());
                this.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
                if(print.getPages().isEmpty())
                    JOptionPane.showMessageDialog(this, "Report tidak ditemukan!");
                else
                    JasperViewer.viewReport(print,false);
            }
            if(sNewPo.length()>0){
                reportParam.put("corporate", MainForm.sNamaUsaha);
                reportParam.put("alamat", MainForm.sAlamat);
                reportParam.put("telp", MainForm.sTelp);
                reportParam.put("no_mutasi", sNewPo);

                this.setCursor(new Cursor(Cursor.WAIT_CURSOR));
                System.out.println(getClass().getResourceAsStream("Reports/TransferSiteByNo.jasper"));
                jasperReport = (JasperReport) JRLoader.loadObject(getClass().getResourceAsStream("Reports/TransferSiteByNo.jasper"));
                JasperPrint print = JasperFillManager.fillReport(jasperReport,reportParam,conn);
                print.setOrientation(jasperReport.getOrientationValue());
                this.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
                if(print.getPages().isEmpty())
                    JOptionPane.showMessageDialog(this, "Report tidak ditemukan!");
                else
                    JasperViewer.viewReport(print,false);
            }

        }
        catch(JRException je){System.out.println(je.getMessage());}
        //catch(NullPointerException ne){JOptionPane.showMessageDialog(null, ne.getMessage(), MainForm.sMessage, JOptionPane.OK_OPTION);}

    }

    public class MyTableCellEditor extends AbstractCellEditor implements TableCellEditor {
        private Toolkit toolkit;
        JTextField text= new JTextField() {
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

        int col, row;

        public Component getTableCellEditorComponent(JTable table, Object value,
            boolean isSelected, int rowIndex, int vColIndex) {
            // 'value' is value contained in the cell located at (rowIndex, vColIndex)
            row=rowIndex;
            col=vColIndex;

            text.setName("textEditor");

            if(vColIndex==tblItem.getColumnModel().getColumnIndex("Qty")){
               text.addKeyListener(kListener);
            }else{
               text.removeKeyListener(kListener);
            }

           //col=vColIndex;
           text.setBackground(new Color(0,255,204));
           text.addFocusListener(txtFocusListener);
           //text.addKeyListener(kListener);
           text.setFont(table.getFont());
           //text.setName("textEditor");


            text.setText(value==null? "": value.toString());
            //component.setText(df.format(value));

            if(value instanceof Double || value instanceof Float|| value instanceof Integer){
                text.setText(fn.dFmt.format(value));
                
            }else
                text.setText(value==null? "":value.toString());
           return text;
        }

        public Object getCellEditorValue() {
            //Object o="";//=component.getText();
            Object retVal = 0;
            try {
                retVal = fn.udfGetInt(((JTextField)text).getText());
                if(col==tblItem.getColumnModel().getColumnIndex("Qty")){
                   if(fn.udfGetDouble(((JTextField)text).getText())> fn.udfGetDouble(txtQtySrc.getText()) ){
                        JOptionPane.showMessageDialog(FrmTransferRuang.this, "Jumlah transfer lebih besar dari sisa stok!");
                        retVal=fn.udfGetInt(tblItem.getValueAt(row, tblItem.getColumnModel().getColumnIndex("Qty")));
                    }else
                        retVal = fn.udfGetInt(((JTextField)text).getText());

                    return retVal;
                }
            }catch (Exception e) {
                toolkit.beep();
                retVal=0;
            }
            return retVal;
        }

    }

    private FocusListener txtFocusListener=new FocusListener() {
        public void focusGained(FocusEvent e) {
            if(e.getSource() instanceof JTextField || e.getSource() instanceof JFormattedTextField || e.getSource() instanceof JComboBox){
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
            if(e.getSource() instanceof JTextField || e.getSource() instanceof JFormattedTextField || e.getSource() instanceof JComboBox){
                ((JTextField)e.getSource()).setBackground(Color.WHITE);
//                if(e.getSource().equals(txtQty)||e.getSource().equals(txtUnitPrice)||e.getSource().equals(txtDisc)){
//                    ((JTextField)e.getSource()).setText(GeneralFunction.dFmt.format(GeneralFunction.udfGetDouble(((JTextField)e.getSource()).getText())));
//                    if(e.getSource().equals(txtQty) && txtQty.getText().equalsIgnoreCase("0")) txtQty.setText("1");
//                    setSubTotal();
//                }else
                if(e.getSource().equals(txtTransferNo)){
                    udfLoadTransferRuang();
                }
            }
        }
    } ;
}
