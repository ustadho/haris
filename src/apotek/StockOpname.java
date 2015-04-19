/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * StockOpname.java
 *
 * Created on Mar 16, 2009, 8:42:49 PM
 */
package apotek;

import main.MainForm;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.FocusTraversalPolicy;
import java.awt.KeyboardFocusManager;
import java.awt.event.ActionEvent;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.sql.Connection;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumnModel;
import main.GeneralFunction;
import main.ListRsbm;
import org.jdesktop.swingx.decorator.HighlighterFactory;

/**
 *
 * @author ustadho
 */
public class StockOpname extends javax.swing.JFrame {
    private Connection conn;
    private DefaultTableModel myModel;
    private ArrayList lstGudang = new ArrayList();
    private ArrayList lstSatuan = new ArrayList();
    private ArrayList lstHarga = new ArrayList();
    private ArrayList lstKonversi = new ArrayList();
    private NumberFormat dFmt = new DecimalFormat("#,##0.00");
    private NumberFormat curFmt = new DecimalFormat("#,##0.00");

    ;
    private boolean isKoreksi = false;
    private float fCurrentQty = 0;
    private MyKeyListener kListener = new MyKeyListener();
    private GeneralFunction fn = new GeneralFunction();
    private Date tglSkg;
    private Component aThis;

    /** Creates new form StockOpname */
    public StockOpname() {
        initComponents();
        aThis = this;
        myModel = (DefaultTableModel) opnameTable.getModel();
        opnameTable.setHighlighters(HighlighterFactory.createSimpleStriping(HighlighterFactory.QUICKSILVER));

        for (int i = 0; i < panelAddItem.getComponentCount(); i++) {
            Component c = panelAddItem.getComponent(i);
            if (c.getClass().getSimpleName().equalsIgnoreCase("JTEXTFIELD") || c.getClass().getSimpleName().equalsIgnoreCase("JFormattedTextField")
                    || c.getClass().getSimpleName().equalsIgnoreCase("JTextArea") || c.getClass().getSimpleName().equalsIgnoreCase("JComboBox")
                    || c.getClass().getSimpleName().equalsIgnoreCase("JCheckBox") || c.getClass().getSimpleName().equalsIgnoreCase("JButton")) {
                c.addKeyListener(kListener);
                c.addFocusListener(txtFocusListener);

            }
        }
        jXDatePicker1.setEnabled(false);

    }

    public void setConn(Connection con) {
        this.conn = con;
        fn.setConn(conn);
    }

    public void setKoreksi(boolean b) {
        isKoreksi = b;
        txtNoOpname.setEditable(b);
    }

    private void udfClear() {
        txtNoOpname.setText("");
        txtKeterangan.setText("");
        cmbGudang.setSelectedIndex(-1);
        cmbGudang.setEnabled(true);
        myModel.setNumRows(0);
        opnameTable.setModel(myModel);
    }

    private void udfInitForm() {
        jXDatePicker1.setFormats("dd/MM/yyyy");

        try {
            ResultSet rs = conn.createStatement().executeQuery(
                    "select kode_gudang, coalesce(deskripsi,'') as nama_gudang, current_date as skg from gudang order by kode_gudang");

            lstGudang.clear();
            cmbGudang.removeAllItems();
            while (rs.next()) {
                
                lstGudang.add(rs.getString("kode_gudang"));
                cmbGudang.addItem(rs.getString("nama_gudang"));
                tglSkg = rs.getDate("skg");
            }
            rs.close();
            jXDatePicker1.setDate(tglSkg);
            udfLoadOpname();
        } catch (SQLException se) {
            JOptionPane.showMessageDialog(this, se.getMessage());
        }

        opnameTable.getColumn("Kode").setPreferredWidth(txtKode.getWidth());
        opnameTable.getColumn("Nama Barang").setPreferredWidth(txtNamaBarang.getWidth());
    }

//    private void udfInsertItem() {
//        if (txtKode.getText().trim().equalsIgnoreCase("") || txtNamaBarang.getText().trim().equalsIgnoreCase("")) {
//            txtKode.requestFocus();
//            return;
//        }
//        if (txtNoOpname.getText().length() == 0) {
//            try {
//                ResultSet rs = conn.createStatement().executeQuery("select fn_r_ins_opname_header('" + new SimpleDateFormat("yyyy-MM-dd").format(jXDatePicker1.getDate()) + "',"
//                        + "'', '" + lstGudang.get(cmbGudang.getSelectedIndex()).toString() + "', '" + txtKeterangan.getText() + "', '" + MainForm.sUserName + "', "
//                        + "" + chkValueAdjustment.isSelected() + ")");
//
//                if (rs.next()) {
//                    txtNoOpname.setText(rs.getString(1));
//                    cmbGudang.setEnabled(false);
//                    jXDatePicker1.setEnabled(false);
//                }
//                rs.close();
//
//            } catch (SQLException se) {
//                JOptionPane.showMessageDialog(this, se.getMessage());
//            }
//        }
//
//        TableColumnModel column = opnameTable.getColumnModel();
//        for (int i = 0; i < opnameTable.getRowCount(); i++) {
//            if (myModel.getValueAt(i, column.getColumnIndex("Kode")).toString().equalsIgnoreCase(txtKode.getText())
//                    && myModel.getValueAt(i, column.getColumnIndex("Nama Barang")).toString().equalsIgnoreCase(txtNamaBarang.getText())
//                    && myModel.getValueAt(i, column.getColumnIndex("Batch No.")).toString().equalsIgnoreCase(txtBatchNo.getText())
//                    && myModel.getValueAt(i, column.getColumnIndex("Exp. Date")).toString().equalsIgnoreCase(txtExpDate.getText())) {
//
//                JOptionPane.showMessageDialog(this, "Barang tersebut sudah dimasukkan pada baris ke :" + (i + 1));
//                opnameTable.setRowSelectionInterval(i, i);
//                txtKode.requestFocus();
//                return;
//            }
//        }
//
//        if (btnInsertItem.getText().equalsIgnoreCase("ADD")) {
//            double newValue = 0;
//            newValue = (GeneralFunction.udfGetDouble(txtNewValue.getText()) == 0 ? (GeneralFunction.udfGetDouble(txtCurrentValue.getText()) / GeneralFunction.udfGetDouble(txtCurrentQty.getText())) * GeneralFunction.udfGetDouble(txtNewQty.getText())
//                    : GeneralFunction.udfGetDouble(txtNewValue.getText()));
//
//            myModel.addRow(new Object[]{
//                        txtKode.getText(),
//                        txtNamaBarang.getText(),
//                        txtBatchNo.getText(),
//                        txtExpDate.getText(),
//                        cmbUnit.getSelectedItem().toString(),
//                        dFmt.format(GeneralFunction.udfGetDouble(txtCurrentQty.getText())),
//                        dFmt.format(GeneralFunction.udfGetDouble(txtNewQty.getText())),
//                        dFmt.format(GeneralFunction.udfGetDouble(txtCurrentValue.getText())),
//                        newValue,
//                        txtKonv.getText()
//                    });
//            opnameTable.setRowSelectionInterval(myModel.getRowCount() - 1, myModel.getRowCount() - 1);
//        } else if (btnInsertItem.getText().equalsIgnoreCase("Update")) {
//        }
//        cmbGudang.setEnabled(opnameTable.getRowCount() < 0);
//        udfStartNewItem();
//    }

    private void udfInsertItem2() {
        if (txtKode.getText().trim().equalsIgnoreCase("") || txtNamaBarang.getText().trim().equalsIgnoreCase("")) {
            txtKode.requestFocus();
            return;
        } 
        if (txtNoOpname.getText().length() == 0) {
            try {
                ResultSet rs = conn.createStatement().executeQuery(
                        "select fn_ins_opname_header('" + new SimpleDateFormat("yyyy-MM-dd").format(jXDatePicker1.getDate()) + "',"
                        + "'', '" + lstGudang.get(cmbGudang.getSelectedIndex()).toString() + "', '" + txtKeterangan.getText() + "', '" + MainForm.sUserName + "', "
                        + "false)");

                if (rs.next()) {
                    txtNoOpname.setText(rs.getString(1));
                    cmbGudang.setEnabled(false);
                    jXDatePicker1.setEnabled(false);
                }
                rs.close();

            } catch (SQLException se) {
                JOptionPane.showMessageDialog(this, se.getMessage());
            }
        }

        if (btnInsertItem.getText().equalsIgnoreCase("ADD")) {
//            if (isExistsItem(txtKode.getText(), txtExpDate.getText(), txtBatchNo.getText())) {
//                if (JOptionPane.showConfirmDialog(this, "Item tersebut sudah dimasukkan apakah anda ingin mengupdate?", "Item sudah masuk", JOptionPane.YES_NO_OPTION) == JOptionPane.NO_OPTION) {
//                    return;
//                } else {
//                    udfUpdateItem();
//                    udfLoadOpnameDetail();
//                    udfStartNewItem();
//                    return;
//                }
//            }
//            double newValue=(GeneralFunction.udfGetDouble(txtNewValue.getText())==0? (GeneralFunction.udfGetDouble(txtCurrentValue.getText())/GeneralFunction.udfGetDouble(txtCurrentQty.getText()))*GeneralFunction.udfGetDouble(txtNewQty.getText()):
//                GeneralFunction.udfGetDouble(txtNewValue.getText()));
            try {
                String sQry = "INSERT INTO opname_detail(no_opname, kode_barang, " +
                        "expired_date, jumlah_komp, jumlah_real, " +
                        "userins, time_ins, satuan ) "
                        + "VALUES('" + txtNoOpname.getText() + "', '" + txtKode.getText() + "', "
                        + (txtExpDate.getText().trim().length() == 0 ? "null " : "'" + new SimpleDateFormat("yyyy-MM-dd").format(new SimpleDateFormat("dd/MM/yyyy").parse(txtExpDate.getText())) + "'") + ","
                        + fn.udfGetDouble(txtCurrentQty.getText()) + ", "
                        + fn.udfGetDouble(txtNewQty.getText()) + ", " +
                        "'"+MainForm.sUserName+"', now(), "
                        + "'" + txtSatuan.getText() + "')";
                System.out.println(sQry);
                conn.setAutoCommit(false);
                conn.createStatement().executeUpdate(sQry);

                conn.setAutoCommit(true);
                udfLoadOpnameDetail();
                udfStartNewItem();
            } catch (SQLException se) {
                try {
                    conn.setAutoCommit(true);
                    conn.rollback();
                    JOptionPane.showMessageDialog(this, se.getMessage());
                } catch (SQLException ex) {
                    JOptionPane.showMessageDialog(this, ex.getMessage());
                }
            } catch (ParseException fe) {
                JOptionPane.showMessageDialog(this, fe.getMessage());

            }

//            myModel.addRow(new Object[]{
//                txtKode.getText(),
//                txtNamaBarang.getText(),
//                txtBatchNo.getText(),
//                txtExpDate.getText(),
//                cmbUnit.getSelectedItem().toString(),
//                dFmt.format(GeneralFunction.udfGetDouble(txtCurrentQty.getText())),
//                dFmt.format(GeneralFunction.udfGetDouble(txtNewQty.getText())),
//                dFmt.format(GeneralFunction.udfGetDouble(txtCurrentValue.getText())),
//                newValue,
//                txtKonv.getText()
//            });
//            opnameTable.setRowSelectionInterval(myModel.getRowCount()-1, myModel.getRowCount()-1);
        } else if (btnInsertItem.getText().equalsIgnoreCase("Update")) {
            udfUpdateItem();
        }
        cmbGudang.setEnabled(opnameTable.getRowCount() < 0);
        udfStartNewItem();
    }

    private void udfUpdateItem() {
        try {
            conn.setAutoCommit(false);
            conn.createStatement().executeUpdate("UPDATE opname_detail "
                    + "SET  "
                    + "   cur_qty=" + fn.udfGetDouble(txtCurrentQty.getText()) + ", "
                    + "   new_qty=" + fn.udfGetDouble(txtNewQty.getText()) + ", "
                    + "   unit='" + txtSatuan.getText() + "' "
                    + "WHERE no_opname='" + txtNoOpname.getText() + "' and kode_barang='" + txtKode.getText() + "' "
                    + "and coalesce(to_char(expired_date, 'dd/MM/yyyy'),'')='" + txtExpDate.getText() + "' ");

            conn.setAutoCommit(true);

        } catch (SQLException se) {
            try {
                conn.setAutoCommit(true);
                conn.rollback();
                JOptionPane.showMessageDialog(this, se.getMessage());
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage());
            }
        }
    }

    private boolean isExistsItem(String sItem, String sExpDate, String sNoBatch) {
        boolean b = false;
        try {
            ResultSet rs = conn.createStatement().executeQuery("SELECT * from opname_detail where "
                    + "no_opname='" + txtNoOpname.getText() + "' and kode_barang='" + sItem + "' and "
                    + "coalesce(to_char(expired_date, 'dd/MM/yyyy'),'')='" + sExpDate + "' and no_batch='" + sNoBatch + "'");

            b = rs.next();
            rs.close();
        } catch (SQLException se) {
            JOptionPane.showMessageDialog(this, se.getMessage());
        }
        return b;
    }

    private void udfStartNewItem() {
        txtKode.setText("");
        txtNamaBarang.setText("");
        txtExpDate.setText("");
        txtCurrentQty.setText("");
        txtNewQty.setText("");
        txtKode.requestFocus();
        btnInsertItem.setText("Add");
    }

    private void udfSaveOpname() {
        if (myModel.getRowCount() <= 0) {
            JOptionPane.showMessageDialog(this, "Silakan isi barang yang akan diopname terlebih dulu!");
            txtKode.requestFocus();
            return;
        }
        if (cmbGudang.getSelectedIndex() < 0) {
            JOptionPane.showMessageDialog(this, "Silakan pilih gudang terlebih dulu!");
            cmbGudang.requestFocus();
            return;
        }
        try {
            conn.setAutoCommit(false);

            ResultSet rs = conn.createStatement().executeQuery("select fn_r_ins_opname_header('" + new SimpleDateFormat("yyyy-MM-dd").format(jXDatePicker1.getDate()) + "',"
                    + "'', '" + lstGudang.get(cmbGudang.getSelectedIndex()).toString() + "', '" + txtKeterangan.getText() + "', '" + MainForm.sUserName + "', "
                    + "false)");

            if (rs.next()) {
                txtNoOpname.setText(rs.getString(1));

                ResultSet rsDet = conn.createStatement(ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_UPDATABLE).executeQuery("select * from opname_detail limit 0");

                TableColumnModel column = opnameTable.getColumnModel();
                SimpleDateFormat yMd = new SimpleDateFormat("yyyy/MM/dd");
                SimpleDateFormat dMy = new SimpleDateFormat("dd/MM/yyyy");

                double newQty = 0, newValue;

                for (int i = 0; i < myModel.getRowCount(); i++) {
                    newQty = GeneralFunction.udfGetDouble(myModel.getValueAt(i, column.getColumnIndex("Stok Real")).toString()) * GeneralFunction.udfGetDouble(myModel.getValueAt(i, column.getColumnIndex("Konv")).toString());
                    
                    rsDet.moveToInsertRow();
                    rsDet.updateString("no_opname", txtNoOpname.getText());
                    rsDet.updateString("kode_barang", myModel.getValueAt(i, column.getColumnIndex("Kode")).toString());
                    rsDet.updateDate("expired_date", myModel.getValueAt(i, column.getColumnIndex("Exp. Date")).toString().equalsIgnoreCase("") ? null : java.sql.Date.valueOf(yMd.format(dMy.parse(myModel.getValueAt(i, column.getColumnIndex("Exp. Date")).toString()))));
                    rsDet.updateString("no_batch", myModel.getValueAt(i, column.getColumnIndex("Batch No.")).toString());
                    rsDet.updateDouble("jumlah_komp", GeneralFunction.udfGetDouble(myModel.getValueAt(i, column.getColumnIndex("Stok Komp"))));
                    rsDet.updateDouble("jumlah_real", newQty);
                    rsDet.updateString("satuan", myModel.getValueAt(i, column.getColumnIndex("Unit")).toString());
                    rsDet.updateInt("konv", GeneralFunction.udfGetInt(myModel.getValueAt(i, column.getColumnIndex("Konv")).toString()));
                    rsDet.insertRow();
                }
                rsDet.close();

            }

            conn.setAutoCommit(true);
            JOptionPane.showMessageDialog(this, "Opname tersimpan");
        } catch (ParseException ex) {
            Logger.getLogger(StockOpname.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SQLException se) {
            try {
                conn.rollback();
                JOptionPane.showMessageDialog(this, "Simpan opname gagal.\nTransaksi di Rollback\n\n" + se.getMessage());
            } catch (SQLException ex) {
                Logger.getLogger(StockOpname.class.getName()).log(Level.SEVERE, null, ex);
            }

        }
    }

    private void udfLoadOpname() {
        try {
            ResultSet rs = conn.createStatement().executeQuery(
                    "select o.no_opname, o.tanggal, o.kode_gudang, coalesce(g.deskripsi,'') as nama_gudang, "
                    + "coalesce(o.keterangan,'') as catatan "
                    + "from opname o "
                    + "inner join gudang g on g.kode_gudang=o.kode_gudang "
                    + "where to_char(tanggal, 'yyyy-MM-dd')='" + fn.yyyymmdd_format.format(jXDatePicker1.getDate()) + "' ");
            if (rs.next()) {
                txtNoOpname.setText(rs.getString("no_opname"));
                jXDatePicker1.setDate(rs.getDate("tanggal"));
                cmbGudang.setSelectedItem(rs.getString("nama_gudang"));
                txtKeterangan.setText(rs.getString("catatan"));

                rs.close();
                udfLoadOpnameDetail();
            } else {
                txtNoOpname.setText("");
                //jXDatePicker1.setDate(tglSkg);
                cmbGudang.setSelectedItem(MainForm.sNamaGudang);
                txtKeterangan.setText("");

                ((DefaultTableModel) opnameTable.getModel()).setNumRows(0);

            }
            rs.close();
        } catch (SQLException se) {
            JOptionPane.showMessageDialog(this, se.getMessage());
        }
    }

    private void udfLoadOpnameDetail(){
        try{
            int iRow=0;
            ((DefaultTableModel) opnameTable.getModel()).setNumRows(0);
            ResultSet rs=null;
            rs = conn.createStatement().executeQuery("select d.kode_barang, coalesce(i.nama_paten,'') as nama_item, " +
                    "coalesce(d.no_batch,'') as batch_no, "
                    + "coalesce(to_char(expired_date,'dd/MM/yyyy'), '') as exp_date, coalesce(d.satuan,'') as unit, coalesce(d.konv,1) as konv, "
                    + "coalesce(jumlah_komp,0) as cur_qty, coalesce(d.jumlah_real,0) as new_qty "
                    + "from opname_detail d "
                    + "inner join barang i on i.item_code=d.kode_barang "
                    + "where d.no_opname='" + txtNoOpname.getText() + "' " +
                    "and d.kode_barang||coalesce(i.nama_paten,'') ilike '%"+txtCari.getText()+"%' " +
                    "order by d.op_counter desc");
            while (rs.next()) {
                ((DefaultTableModel) opnameTable.getModel()).addRow(new Object[]{
                    rs.getString("kode_barang"),
                    rs.getString("nama_item"),
                    rs.getString("exp_date"),
                    rs.getString("unit"),
                    rs.getDouble("cur_qty"),
                    rs.getDouble("new_qty"),
                });
                if(txtKode.getText().equalsIgnoreCase(rs.getString("kode_barang")) &&
                   txtNamaBarang.getText().equalsIgnoreCase(rs.getString("kode_barang")) )
                    iRow=((DefaultTableModel) opnameTable.getModel()).getRowCount()-1;
            }
            if(opnameTable.getRowCount()>0 && iRow>=0){
                opnameTable.setRowSelectionInterval(iRow, iRow);
                opnameTable.changeSelection(iRow, 0, false, false);
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

        jPanel1 = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        txtNoOpname = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        jXDatePicker1 = new org.jdesktop.swingx.JXDatePicker();
        cmbGudang = new javax.swing.JComboBox();
        jLabel30 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        txtKeterangan = new javax.swing.JTextArea();
        jLabel1 = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        opnameTable = new org.jdesktop.swingx.JXTable();
        jXPanel1 = new org.jdesktop.swingx.JXPanel();
        jLabel5 = new javax.swing.JLabel();
        txtCari = new javax.swing.JTextField();
        panelAddItem = new javax.swing.JPanel();
        jLabel8 = new javax.swing.JLabel();
        txtKode = new javax.swing.JTextField();
        jLabel18 = new javax.swing.JLabel();
        txtNamaBarang = new javax.swing.JTextField();
        jLabel19 = new javax.swing.JLabel();
        txtCurrentQty = new javax.swing.JTextField();
        jLabel20 = new javax.swing.JLabel();
        jLabel21 = new javax.swing.JLabel();
        txtNewQty = new javax.swing.JTextField();
        btnInsertItem = new javax.swing.JButton();
        jLabel27 = new javax.swing.JLabel();
        txtExpDate = new javax.swing.JTextField();
        txtSatuan = new javax.swing.JTextField();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Penyesuaian Persediaan Barang");
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowOpened(java.awt.event.WindowEvent evt) {
                formWindowOpened(evt);
            }
        });
        getContentPane().setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jPanel1.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        jPanel1.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel2.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel2.setText("Tgl. Opname :");
        jPanel1.add(jLabel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(260, 10, 100, 20));
        jPanel1.add(txtNoOpname, new org.netbeans.lib.awtextra.AbsoluteConstraints(110, 10, 140, 23));

        jLabel3.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel3.setText("Keterangan");
        jPanel1.add(jLabel3, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 40, 90, 20));

        jXDatePicker1.setEditable(false);
        jXDatePicker1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jXDatePicker1ActionPerformed(evt);
            }
        });
        jPanel1.add(jXDatePicker1, new org.netbeans.lib.awtextra.AbsoluteConstraints(360, 10, 120, -1));

        cmbGudang.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                cmbGudangItemStateChanged(evt);
            }
        });
        jPanel1.add(cmbGudang, new org.netbeans.lib.awtextra.AbsoluteConstraints(650, 10, 130, -1));

        jLabel30.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel30.setText("Gudang :"); // NOI18N
        jPanel1.add(jLabel30, new org.netbeans.lib.awtextra.AbsoluteConstraints(570, 10, 80, 20));

        jLabel4.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel4.setText("No. Opname");
        jPanel1.add(jLabel4, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 10, 90, 20));

        txtKeterangan.setColumns(20);
        txtKeterangan.setRows(5);
        jScrollPane1.setViewportView(txtKeterangan);

        jPanel1.add(jScrollPane1, new org.netbeans.lib.awtextra.AbsoluteConstraints(110, 40, 670, 50));

        getContentPane().add(jPanel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 32, 818, 102));

        jLabel1.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        jLabel1.setText("Stock Opname");
        getContentPane().add(jLabel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(620, 0, 211, 26));

        opnameTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Kode", "Nama Barang", "Exp. Date", "Unit", "Stok Komp", "Stok Real"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.Double.class, java.lang.Double.class
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
        opnameTable.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_OFF);
        opnameTable.getTableHeader().setReorderingAllowed(false);
        opnameTable.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                opnameTableKeyPressed(evt);
            }
        });
        jScrollPane2.setViewportView(opnameTable);

        getContentPane().add(jScrollPane2, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 189, 988, 419));

        jLabel5.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel5.setText("Pencarian :");

        txtCari.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        javax.swing.GroupLayout jXPanel1Layout = new javax.swing.GroupLayout(jXPanel1);
        jXPanel1.setLayout(jXPanel1Layout);
        jXPanel1Layout.setHorizontalGroup(
            jXPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jXPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 90, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtCari, javax.swing.GroupLayout.PREFERRED_SIZE, 140, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(744, Short.MAX_VALUE))
        );
        jXPanel1Layout.setVerticalGroup(
            jXPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jXPanel1Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jXPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addComponent(txtCari, javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel5, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 20, Short.MAX_VALUE))
                .addGap(3, 3, 3))
        );

        getContentPane().add(jXPanel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 614, -1, -1));

        panelAddItem.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        panelAddItem.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel8.setBackground(new java.awt.Color(255, 255, 204));
        jLabel8.setText("Kode");
        jLabel8.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        jLabel8.setOpaque(true);
        panelAddItem.add(jLabel8, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 2, 120, -1));

        txtKode.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        txtKode.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtKodeKeyReleased(evt);
            }
            public void keyTyped(java.awt.event.KeyEvent evt) {
                txtKodeKeyTyped(evt);
            }
        });
        panelAddItem.add(txtKode, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 20, 120, 21));

        jLabel18.setBackground(new java.awt.Color(255, 255, 204));
        jLabel18.setText("Barang");
        jLabel18.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        jLabel18.setOpaque(true);
        panelAddItem.add(jLabel18, new org.netbeans.lib.awtextra.AbsoluteConstraints(120, 2, 320, -1));

        txtNamaBarang.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        txtNamaBarang.setDisabledTextColor(new java.awt.Color(0, 0, 0));
        txtNamaBarang.setEnabled(false);
        panelAddItem.add(txtNamaBarang, new org.netbeans.lib.awtextra.AbsoluteConstraints(120, 20, 320, 21));

        jLabel19.setBackground(new java.awt.Color(255, 255, 204));
        jLabel19.setText("Satuan");
        jLabel19.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        jLabel19.setOpaque(true);
        panelAddItem.add(jLabel19, new org.netbeans.lib.awtextra.AbsoluteConstraints(520, 2, 60, -1));

        txtCurrentQty.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtCurrentQty.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        txtCurrentQty.setDisabledTextColor(new java.awt.Color(0, 0, 0));
        txtCurrentQty.setEnabled(false);
        panelAddItem.add(txtCurrentQty, new org.netbeans.lib.awtextra.AbsoluteConstraints(580, 20, 80, 21));

        jLabel20.setBackground(new java.awt.Color(255, 255, 204));
        jLabel20.setText("Exp. Date");
        jLabel20.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        jLabel20.setOpaque(true);
        panelAddItem.add(jLabel20, new org.netbeans.lib.awtextra.AbsoluteConstraints(440, 2, 80, -1));

        jLabel21.setBackground(new java.awt.Color(255, 255, 204));
        jLabel21.setText("Qty Real");
        jLabel21.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        jLabel21.setOpaque(true);
        panelAddItem.add(jLabel21, new org.netbeans.lib.awtextra.AbsoluteConstraints(660, 2, 70, -1));

        txtNewQty.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtNewQty.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        panelAddItem.add(txtNewQty, new org.netbeans.lib.awtextra.AbsoluteConstraints(660, 20, 70, 21));

        btnInsertItem.setText("Add");
        btnInsertItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnInsertItemActionPerformed(evt);
            }
        });
        panelAddItem.add(btnInsertItem, new org.netbeans.lib.awtextra.AbsoluteConstraints(729, 19, 80, -1));

        jLabel27.setBackground(new java.awt.Color(255, 255, 204));
        jLabel27.setText("Qty Komp");
        jLabel27.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        jLabel27.setOpaque(true);
        panelAddItem.add(jLabel27, new org.netbeans.lib.awtextra.AbsoluteConstraints(580, 2, 80, -1));

        txtExpDate.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtExpDate.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        txtExpDate.setDisabledTextColor(new java.awt.Color(0, 0, 0));
        txtExpDate.setEnabled(false);
        panelAddItem.add(txtExpDate, new org.netbeans.lib.awtextra.AbsoluteConstraints(440, 20, 80, 21));

        txtSatuan.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtSatuan.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        txtSatuan.setDisabledTextColor(new java.awt.Color(0, 0, 0));
        txtSatuan.setEnabled(false);
        panelAddItem.add(txtSatuan, new org.netbeans.lib.awtextra.AbsoluteConstraints(520, 20, 60, 21));

        getContentPane().add(panelAddItem, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 140, 818, 43));

        setSize(new java.awt.Dimension(1024, 696));
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void formWindowOpened(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowOpened
        udfInitForm();
    }//GEN-LAST:event_formWindowOpened

    private void txtKodeKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtKodeKeyReleased
        String sQry = "select distinct i.item_code, coalesce(nama_paten,'') as nama_barang, coalesce(to_char(tanggal_kadaluwarsa,'dd/MM/yyyy'),'') as exp_date,"
                + "coalesce(stok.jumlah,0) as curr_qty, coalesce(i.satuan_kecil,'') as unit "
                + "from barang i "
                + "left join stock stok on i.item_code=stok.item_code and "
                + "stok.kode_gudang='" + lstGudang.get(cmbGudang.getSelectedIndex()).toString() + "' and jumlah<>0  "
                + "where(i.item_code||coalesce(nama_paten,'')) ilike  '%" + txtKode.getText() + "%'  " +
                "order by coalesce(nama_paten,'') limit 500";
        fn.lookup(evt, new Object[]{txtNamaBarang, txtExpDate, txtCurrentQty, txtSatuan}, sQry, 
                txtKode.getWidth()+txtNamaBarang.getWidth()+txtSatuan.getWidth()+txtCurrentQty.getWidth()+20, 200);
}//GEN-LAST:event_txtKodeKeyReleased

    private void btnInsertItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnInsertItemActionPerformed
        udfInsertItem2();
}//GEN-LAST:event_btnInsertItemActionPerformed

    private void opnameTableKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_opnameTableKeyPressed
    }//GEN-LAST:event_opnameTableKeyPressed

    private void jXDatePicker1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jXDatePicker1ActionPerformed
        udfLoadOpname();
    }//GEN-LAST:event_jXDatePicker1ActionPerformed

    private void cmbGudangItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_cmbGudangItemStateChanged
        if(cmbGudang.getSelectedIndex()>=0)
            udfLoadOpname();
    }//GEN-LAST:event_cmbGudangItemStateChanged

    private void txtKodeKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtKodeKeyTyped
        // TODO add your handling code here:
    }//GEN-LAST:event_txtKodeKeyTyped

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(new Runnable() {

            public void run() {
                new StockOpname().setVisible(true);
            }
        });
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnInsertItem;
    private javax.swing.JComboBox cmbGudang;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel18;
    private javax.swing.JLabel jLabel19;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel20;
    private javax.swing.JLabel jLabel21;
    private javax.swing.JLabel jLabel27;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel30;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private org.jdesktop.swingx.JXDatePicker jXDatePicker1;
    private org.jdesktop.swingx.JXPanel jXPanel1;
    private org.jdesktop.swingx.JXTable opnameTable;
    private javax.swing.JPanel panelAddItem;
    private javax.swing.JTextField txtCari;
    private javax.swing.JTextField txtCurrentQty;
    private javax.swing.JTextField txtExpDate;
    private javax.swing.JTextArea txtKeterangan;
    private javax.swing.JTextField txtKode;
    private javax.swing.JTextField txtNamaBarang;
    private javax.swing.JTextField txtNewQty;
    private javax.swing.JTextField txtNoOpname;
    private javax.swing.JTextField txtSatuan;
    // End of variables declaration//GEN-END:variables

    public class MyKeyListener extends KeyAdapter {

        double sisa = 0;

        public void keyTyped(KeyEvent e) {
            if (e.getSource().equals(txtCurrentQty) || e.getSource().equals(txtNewQty) ) {
                char c = e.getKeyChar();
                if (!((c >= '0') && (c <= '9')
                        || (c == KeyEvent.VK_BACK_SPACE)
                        || (c == KeyEvent.VK_ENTER)
                        || (c == KeyEvent.VK_DELETE))) {
                    getToolkit().beep();
                    e.consume();
                }
                //udfItemSubTotal();
            }
        }

        public void keyReleased(KeyEvent e) {
        }

        public void keyPressed(KeyEvent evt) {
            int keyKode = evt.getKeyCode();
            switch (keyKode) {
                case KeyEvent.VK_ENTER: {
                    if (!(fn.isListVisible())) {
                        Component c = findNextFocus();
                        if (c != null) {
                            c.requestFocus();
                        }
                    } else {
                        fn.lstRequestFocus();
                    }
                    break;
                }

                case KeyEvent.VK_UP: {
                    if (!fn.isListVisible()) {
                        Component c = findPrevFocus();
                        if (c != null) {
                            c.requestFocus();
                        }
                    } else {
                        fn.lstRequestFocus();
                    }
                    break;
                }
                case KeyEvent.VK_DOWN: {
                    if (!fn.isListVisible()) {
                        Component c = findNextFocus();
                        if (c != null) {
                            c.requestFocus();
                        }
                    } else {
                        fn.lstRequestFocus();
                    }
                    break;
                }

                case KeyEvent.VK_F2: {  //Bayar
                    udfStartNewItem();
                    break;
                }
                case KeyEvent.VK_F3: {  //Bayar
                    opnameTableKeyPressed(evt);
                    break;
                }
                case KeyEvent.VK_INSERT: {  //insert item
                    btnInsertItemActionPerformed(new ActionEvent(btnInsertItem, ActionEvent.ACTION_PERFORMED, "Add"));
                    break;
                }


            }
        }

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
    private FocusListener txtFocusListener = new FocusListener() {

        public void focusGained(FocusEvent e) {
            Component c = (Component) e.getSource();
            c.setBackground(g1);

            if (c.equals(txtNewQty) ) {
                ((JTextField) e.getSource()).setSelectionStart(0);
                ((JTextField) e.getSource()).setSelectionEnd(((JTextField) e.getSource()).getText().length());

            } else if (c.equals(txtKode) && cmbGudang.getSelectedIndex() < 0) {
                JOptionPane.showMessageDialog(StockOpname.this, "Silakan pilih gudang terlebih dulu");
                //cmbGudang.requestFocus();
                return;
            }

            //c.setForeground(fPutih);
            //c.setCaretColor(new java.awt.Color(255, 255, 255));
        }

        public void focusLost(FocusEvent e) {
            Component c = (Component) e.getSource();
            c.setBackground(g2);

            if (c.equals(txtKode) && !fn.isListVisible()) {
                //txtCurrentValue.setText(fn.dFmt.format(fn.udfGetDouble(txtCurrentValue)));
                try {
                    ResultSet rs = conn.createStatement().executeQuery(
                            "select i.item_code, coalesce(i.nama_paten,'') as nama_item, " +
                            "coalesce(i.hpp,0) as hpp, coalesce(s.no_batch,'') as batch_no," +
                            " coalesce(s.jumlah,0) as saldo, coalesce(i.satuan_kecil, '') as satuan_kecil "
                            + "from barang i "
                            + "left join stock s on s.item_code=i.item_code and s.kode_gudang='" + lstGudang.get(cmbGudang.getSelectedIndex()).toString() + "' "
                            + "and coalesce(to_char(tanggal_kadaluwarsa, 'dd/MM/yyyy'),'')='" + txtExpDate.getText() + "' "
                            + "where i.item_code='" + txtKode.getText() + "'");
                    if (rs.next()) {
                        txtNamaBarang.setText(rs.getString("nama_item"));
                        txtCurrentQty.setText(fn.dFmt.format(rs.getDouble("saldo")));
                        txtSatuan.setText(rs.getString("satuan_kecil"));
                        //udfSetSatuanBarang();
                    }

                } catch (SQLException se) {
                    JOptionPane.showMessageDialog(aThis, se.getMessage());
                }
                //udfSetSatuanBarang();
            } else if (c.equals(txtExpDate)) {
                if (txtExpDate.getText().trim().equalsIgnoreCase("")) {
                    return;
                }
                if (!txtExpDate.getText().trim().equalsIgnoreCase("/  /") || !txtExpDate.getText().trim().equalsIgnoreCase("")) {
                    if (txtExpDate.getText().length() == 7) {
                        txtExpDate.setText("01/" + txtExpDate.getText());
                    }
                    if (!GeneralFunction.validateDate(txtExpDate.getText(), true, "dd/MM/yyyy")) {
                        JOptionPane.showMessageDialog(null, "Silakan masukkan tanggal dengan format 'dd/MM/yyyy' !");
                        //txtExpDate.setText("");
                        txtExpDate.requestFocus();

                        return;

                    }
                } else {
                    txtExpDate.setText("");
                }
            } else if (e.getSource().equals(txtNewQty)) {
                ((JTextField) e.getSource()).setText(dFmt.format(GeneralFunction.udfGetDouble(((JTextField) e.getSource()).getText())));
            }
            //c.setForeground(fHitam);
        }
    };
    Color g1 = new Color(153, 255, 255);
    Color g2 = new Color(255, 255, 255);
    Color fHitam = new Color(0, 0, 0);
    Color fPutih = new Color(255, 255, 255);
    Color crtHitam = new java.awt.Color(0, 0, 0);
    Color crtPutih = new java.awt.Color(255, 255, 255);
}
