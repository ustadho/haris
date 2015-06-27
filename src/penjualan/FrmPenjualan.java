/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * NewJFrame.java
 *
 * Created on Aug 31, 2010, 9:05:58 PM
 */
package penjualan;

import apotek.DLgLookup;
import apotek.DlgDokter;
import apotek.DlgLookupItemJual;
import apotek.JDesktopImage;
import apotek.dao.ItemDao;
import main.MainForm;
import apotek.dao.PelangganDao;
import apotek.dao.PenjualanDao;
import com.klinik.model.Barang;
import apotek.printPenjualan;
import com.klinik.rm.DlgPasien;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Cursor;
import java.awt.FocusTraversalPolicy;
import java.awt.KeyboardFocusManager;
import java.awt.Toolkit;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.print.PrinterJob;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.PatternSyntaxException;
import javax.print.DocFlavor;
import javax.print.PrintService;
import javax.print.PrintServiceLookup;
import javax.print.attribute.HashPrintRequestAttributeSet;
import javax.print.attribute.HashPrintServiceAttributeSet;
import javax.print.attribute.PrintRequestAttributeSet;
import javax.print.attribute.PrintServiceAttributeSet;
import javax.swing.AbstractCellEditor;
import javax.swing.JComponent;
import javax.swing.JFormattedTextField;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.RowFilter;
import javax.swing.SwingUtilities;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;
import javax.swing.text.AbstractDocument;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;
import javax.swing.text.JTextComponent;
import javax.swing.text.MaskFormatter;
import main.GeneralFunction;
import main.SysConfig;

/**
 *
 * @author cak-ust
 */
public class FrmPenjualan extends javax.swing.JFrame {
    private Connection conn;
    DefaultTableModel modelDetail;
    GeneralFunction fn;
    DlgLookupItemJual lookupItem = new DlgLookupItemJual(this, true);
    MyKeyListener kListener = new MyKeyListener();
    final TableRowSorter<TableModel> sorter;
    private String sRLama = "", sRBaru = "";
    MyTableCellEditorHeader cEditorH;
    //private double uangR=150;
    private boolean isKoreksi = false;
    String sSiteID = "01";
    boolean tutupRek = false;
    String sOldTipeHarga = "";
    SysConfig sy = new SysConfig();
    Component aThis;
    private Object srcForm;
    private JDesktopImage desktop;
    ArrayList lstGudang = new ArrayList();
    PelangganDao pelangganDao = new PelangganDao();
    ItemDao itemDao = new ItemDao();
    private String noReg="";

    /**
     * Creates new form NewJFrame
     */
    public FrmPenjualan() {
        initComponents();
//        initConn();
        aThis = this;
        tblHeader.putClientProperty("terminateEditOnFocusLost", Boolean.TRUE);
        tblDetail.putClientProperty("terminateEditOnFocusLost", Boolean.TRUE);

        tblHeader.setRowHeight(22);
        tblDetail.setRowHeight(22);
        tblHeader.getTableHeader().setFont(tblHeader.getFont());
        tblDetail.getTableHeader().setFont(tblDetail.getFont());

        modelDetail = ((DefaultTableModel) tblDetail.getModel());
        table.setModel(modelDetail);

        sorter = new TableRowSorter<TableModel>(tblDetail.getModel());
        tblDetail.setRowSorter(sorter);

        tblHeader.getModel().addTableModelListener(new TableModelListener() {
            TableColumnModel colHeader = tblHeader.getColumnModel();

            public void tableChanged(TableModelEvent e) {
                int iRow = tblHeader.getSelectedRow();

                if (e.getType() == TableModelEvent.DELETE) {
                    for (int i = 1; i <= tblHeader.getRowCount(); i++) {
                        String sOldR = tblHeader.getValueAt(i - 1, 1).toString() + "#" + tblHeader.getValueAt(i - 1, 0).toString();
                        tblHeader.setValueAt(i, i - 1, 0);
                        String sNewR = tblHeader.getValueAt(i - 1, 1).toString() + "#" + tblHeader.getValueAt(i - 1, 0).toString();

                        for (int j = 0; j < modelDetail.getRowCount(); j++) {
                            if (modelDetail.getValueAt(j, tblDetail.getColumnModel().getColumnIndex("NoR")).toString().equalsIgnoreCase(sOldR)) {
                                modelDetail.setValueAt(sNewR, j, tblDetail.getColumnModel().getColumnIndex("NoR"));
                            }
                        }
                    }

                }
//                if(tblHeader.getRowCount()==0){
//                    ((DefaultTableModel)tblHeader.getModel()).setNumRows(1);
//                    tblHeader.setValueAt(1, 0, 0);
//                    tblHeader.setValueAt("T", 0, 1);
//                    tblHeader.setValueAt(1, 0, 2);
//                    tblHeader.setValueAt(0, 0, 3);
//                    
//                    tblHeader.setRowSelectionInterval(0, 0);
//                }

                if (e.getType() == TableModelEvent.UPDATE) {
                    if ((e.getColumn() == colHeader.getColumnIndex("R/") || e.getColumn() == colHeader.getColumnIndex("Qty R"))) {
                        if (tblHeader.getValueAt(iRow, colHeader.getColumnIndex("R/")).toString().equalsIgnoreCase("N")) {
                            ((DefaultTableModel) tblHeader.getModel()).setValueAt(0, iRow, colHeader.getColumnIndex("ES"));
                        } else if (!tblHeader.getValueAt(iRow, colHeader.getColumnIndex("R/")).toString().equalsIgnoreCase("N")) {
                            ((DefaultTableModel) tblHeader.getModel()).setValueAt(
                                    getEmbalaseService(fn.udfGetInt(tblHeader.getValueAt(tblHeader.getSelectedRow(), tblHeader.getColumnModel().getColumnIndex("Qty R")))),
                                    iRow, colHeader.getColumnIndex("ES"));
                        }

                    }
                }
                udfSetTotal();
            }
        });

        tblDetail.getModel().addTableModelListener(new TableModelListener() {
            public void tableChanged(TableModelEvent e) {
                TableColumnModel col = tblDetail.getColumnModel();
                if (e.getType() == TableModelEvent.DELETE || e.getType() == TableModelEvent.INSERT) {
                    if (table.getRowCount() > 0) {
                        //jTable1.setRowSelectionInterval(0, 0);
                        table.setModel((DefaultTableModel) fn.autoResizeColWidth(table, (DefaultTableModel) table.getModel()).getModel());
                    //if(((Boolean)tblDetail.getValueAt(e.getLastRow(), tblDetail.getColumnModel().getColumnIndex("Koreksi"))==false))
                        //if(tblDetail.getSelectedRow()>=0 && tblDetail.getValueAt(tblDetail.getSelectedRow(), 0)!=null)
                        TableModel tm = tblDetail.getModel();

                    }
                }

                if (e.getColumn() == 0) {
                    String sKodeBarang = tblDetail.getValueAt(tblDetail.getSelectedRow(), 0).toString();

                    try {
                        String sQry = "select coalesce(nama_paten,'') as nama_item, "
                                + "coalesce(satuan_kecil,'') as unit, "
                                + "case when '" + cmbTipeTarif.getSelectedItem().toString() + "'='KLINIK' then coalesce(harga_klinik,0) "
                                + "else coalesce(harga_reseller,0) end as harga "
                                + "from barang i "
                                + "where i.item_code='" + sKodeBarang + "'";
                        System.out.println(sQry);
                        ResultSet rs = conn.createStatement().executeQuery(sQry);
                        if (rs.next()) {
                            int iRow = tblDetail.getSelectedRow();
                            tblDetail.setValueAt(rs.getString("nama_item"), iRow, col.getColumnIndex("Nama Barang"));
                            tblDetail.setValueAt(rs.getString("unit"), iRow, col.getColumnIndex("Satuan"));
                            tblDetail.setValueAt(tblHeader.getValueAt(tblHeader.getSelectedRow(), 1).toString() + "#" + tblHeader.getValueAt(tblHeader.getSelectedRow(), 0).toString(), iRow, col.getColumnIndex("NoR"));;

                            tblDetail.setValueAt(rs.getDouble("harga"), iRow, col.getColumnIndex("Harga Satuan"));
                            tblDetail.setValueAt(1, iRow, col.getColumnIndex("Qty Jual"));
                            tblDetail.setValueAt(tblHeader.getValueAt(tblHeader.getSelectedRow(), 1).toString() + "#" + tblHeader.getValueAt(tblHeader.getSelectedRow(), 0).toString(), tblDetail.getRowCount() - 1, tblDetail.getColumnModel().getColumnIndex("NoR"));
                            tblDetail.changeSelection(iRow, col.getColumnIndex("Qty Jual"), false, false);

                        }
                        rs.close();
                    } catch (SQLException se) {
                        System.err.println(se.getMessage());
                    }
                } else if (e.getColumn() == col.getColumnIndex("Qty Jual") || e.getColumn() == col.getColumnIndex("Harga Satuan")
                        || e.getColumn() == col.getColumnIndex("Diskon")) {
                    int iRow = tblDetail.getSelectedRow();
                    if (iRow >= 0) {
                        double jmlKecil = tblDetail.getValueAt(iRow, col.getColumnIndex("Qty Jual")) == null ? 0 : fn.udfGetDouble(tblDetail.getValueAt(iRow, col.getColumnIndex("Qty Jual")));
                        double unitPrice = tblDetail.getValueAt(iRow, col.getColumnIndex("Harga Satuan")) == null ? 0 : fn.udfGetDouble(tblDetail.getValueAt(iRow, col.getColumnIndex("Harga Satuan")));
                        double disc = tblDetail.getValueAt(iRow, col.getColumnIndex("Diskon")) == null ? 0 : fn.udfGetDouble(tblDetail.getValueAt(iRow, col.getColumnIndex("Diskon")));

                        tblDetail.setValueAt(Math.floor(unitPrice * jmlKecil - disc),
                                iRow, col.getColumnIndex("Sub Total"));
                    }

                }

                udfSetTotal();
            }

        });

        tblHeader.getSelectionModel().addListSelectionListener(new ListSelectionListener() {

            public void valueChanged(ListSelectionEvent e) {
                //if(!jCheckBox1.isSelected()) return;
                udfSetFilter();

            }
        });
        tblHeader.addKeyListener(kListener);
        tblDetail.addKeyListener(kListener);
        tblDetail.setRowHeight(22);
        tblHeader.setRowHeight(22);
        tblDetail.getColumn("Nama Barang").setPreferredWidth(200);
        tblDetail.getColumn("Harga Satuan").setPreferredWidth(100);

        cEditorH = new MyTableCellEditorHeader();
        tblHeader.getColumn("ES").setCellEditor(cEditorH);
        tblHeader.getColumn("R/").setCellEditor(cEditorH);
        tblHeader.getColumn("Qty R").setCellEditor(cEditorH);

        MyTableCellEditor cEditor = new MyTableCellEditor();
        tblDetail.getColumn("Qty Jual").setCellEditor(cEditor);
        tblDetail.getColumn("Harga Satuan").setCellEditor(cEditor);
        tblDetail.getColumn("Diskon").setCellEditor(cEditor);

        tblDetail.getColumn("Koreksi").setMinWidth(0);
        tblDetail.getColumn("Koreksi").setMaxWidth(0);
        tblDetail.getColumn("Koreksi").setPreferredWidth(0);

//        tblDetail.getColumn("UR").setMinWidth(0);
//        tblDetail.getColumn("UR").setMaxWidth(0);
//        tblDetail.getColumn("UR").setPreferredWidth(0);
        tblDetail.getColumn("NoR").setMinWidth(0);
        tblDetail.getColumn("NoR").setMaxWidth(0);
        tblDetail.getColumn("NoR").setPreferredWidth(0);

        tblHeader.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0),
                "selectNextColumnCell");
        tblDetail.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0),
                "selectNextColumnCell");

        //java.awt.Toolkit.getDefaultToolkit().setLockingKeyState(KeyEvent.VK_CAPS_LOCK, true);
    }

    public void setSrcForm(Object frm) {
        srcForm = frm;
    }

    public void setKoreksi(boolean b) {
        isKoreksi = b;
    }

    private void udfSetTotal() {
        double dTotal = 0;
        for (int i = 0; i < table.getRowCount(); i++) {
            dTotal += fn.udfGetDouble(table.getValueAt(i, table.getColumnModel().getColumnIndex("Sub Total")));
        }
        for (int i = 0; i < tblHeader.getRowCount(); i++) {
            dTotal += fn.udfGetDouble(tblHeader.getValueAt(i, tblHeader.getColumnModel().getColumnIndex("ES")));
        }
        lblTotal.setText(fn.dFmt.format(dTotal));
        lblNett.setText(fn.dFmt.format(dTotal - fn.udfGetDouble(txtDiskon.getText())));
    }

    private void udfSetFilter() {
        if (tblHeader.getRowCount() > 0 && tblHeader.getSelectedRow() >= 0 && tblHeader.getValueAt(tblHeader.getSelectedRow(), 0) != null && tblHeader.getValueAt(tblHeader.getSelectedRow(), 1) != null) {
            String sFilter = tblHeader.getValueAt(tblHeader.getSelectedRow(), 1).toString() + "#" + tblHeader.getValueAt(tblHeader.getSelectedRow(), 0).toString();
            System.out.println("Filter : " + sFilter);
            try {
                sorter.setRowFilter(
                        RowFilter.regexFilter(sFilter));

                //if(sorter.getModel().getRowCount()>0)
                if (tblDetail.getRowCount() > 0) {
                    tblDetail.setRowSelectionInterval(0, 0);
                }

            } catch (PatternSyntaxException pse) {
                System.err.println("Bad regex pattern");
            }
        } else {
            sorter.setRowFilter(null);
        }
    }

//    private void initConn(){
//        SysConfig sc = new SysConfig();
//        conn = (new KasirCon("jdbc:postgresql://" + sc.getServerLoc() + ":5432/" + sc.getDBName(), 
//                "postgres", "ustasoft", this)).getCon();
//
//    }
    public void setConn(Connection con) {
        this.conn = con;
        pelangganDao.setConn(con);
    }

    private void udfNew() {
        udfClear();
        //modelDetail.setNumRows(0);
        btnNew.setEnabled(false);

    }

    private void udfInitForm() {
        try {
//            if (SysConfig.versi.equalsIgnoreCase("DEMO") && new PenjualanDao().jmlRecord() >= 40) {
//                JOptionPane.showMessageDialog(this, "Versi demo hanya dibatasi maksimal 40 record.\n"
//                        + "Jika anda tertarik, silahkan kontak kami untuk pembelian software ini!");
//                this.dispose();
//                return;
//            }

            fn = new GeneralFunction(conn);
            lookupItem.setConn(conn);
            fn.addKeyListenerInContainer(jToolBar1, kListener, txtFocusListener);
            fn.addKeyListenerInContainer(jPanel1, kListener, txtFocusListener);
            fn.addKeyListenerInContainer(jPanel2, kListener, txtFocusListener);
            fn.addKeyListenerInContainer(jPanel3, kListener, txtFocusListener);

            sSiteID = sy.getSite_Id();

            java.awt.EventQueue.invokeLater(new Runnable() {
                @Override
                public void run() {
                    if (!isKoreksi) {
                        if(txtNama.getText().length()==0)
                            btnLookupPasien.requestFocus();
                        else
                            tblDetail.requestFocusInWindow();
                    } else {
                        txtNoTrx.requestFocusInWindow();
                        btnNew.setEnabled(false);
                        btnSave.setEnabled(true);
                    }
                }
            });

            MaskFormatter fmttgl = null;
            try {
                fmttgl = new MaskFormatter("##/##/####");
            } catch (java.text.ParseException e) {
            }

            JFormattedTextField jFDate1 = new JFormattedTextField(fmttgl);
            jFJtTempo.setFormatterFactory(jFDate1.getFormatterFactory());

            ResultSet rs = conn.createStatement().executeQuery(
                    "select "
                    + "to_char(current_date, 'dd/MM/yyyy') as tgl ");
//            lstGudang.clear();
//            cmbGudang.removeAllItems();

            while (rs.next()) {
//                lstGudang.add(rs.getString(1));
//                cmbGudang.addItem(rs.getString(2));
                lblTgl.setText(rs.getString("tgl"));
//                jFJtTempo.setText(rs.getString("tgl"));
//                jFJtTempo.setValue(rs.getString("tgl"));
            }
            rs.close();

            txtNoTrx.setEnabled(isKoreksi);
            btnNew.setEnabled(false);
            if (isKoreksi && txtNoTrx.getText().length() > 0) {
                udfLoadKoreksiJual();
            } else {
                if (this.noReg.length() > 0) {
                    rs = MainForm.conn.createStatement().executeQuery("select r.no_Reg, r.norm, coalesce(ps.nama,'') as nama, coalesce(ps.alamat_domisili,'') as alamat "
                            + "from rm_reg r "
                            + "inner join rm_pasien ps on ps.norm=r.norm "
                            + "where r.no_reg='" + noReg + "'");
                    if (rs.next()) {
                        lblNoReg.setText(rs.getString("no_reg"));
                        txtNorm.setText(rs.getString("norm"));
                        txtNama.setText(rs.getString("nama"));
                        txtAlamat.setText(rs.getString("alamat"));
                    }
                    rs.close();
                } else {
                    udfNew();
                }
            }

//        else if(isKoreksi && txtNoTrx.getText().length()>0)
//            udfLoadRKKoreksi();
            //else if(isKoreksi)
            //udfLoadReg();
            jLabel17.setText(getTitle());
            if (tblHeader.getRowCount() > 0) {
                tblHeader.setRowSelectionInterval(0, 0);
            }
        } catch (SQLException se) {
            JOptionPane.showMessageDialog(this, se.getMessage());
        }
    }

    private void udfClear() {
        tutupRek = false;
        txtNoTrx.setText("");
        txtNorm.setText("");
        txtNama.setText("");
        txtAlamat.setText("");
        txtDokter.setText("");
        lblDokter.setText("-");
        
        sOldTipeHarga = "NON RESEP";
//        cmbTipeHarga.setSelectedItem(sOldTipeHarga);
        lblTotal.setText("0");
        txtDiskon.setText("0");
        lblNett.setText("0");
        setTitle((isKoreksi ? "Koreksi" : "") + "Resep");
        ((DefaultTableModel) tblHeader.getModel()).setNumRows(0);
        ((DefaultTableModel) table.getModel()).setNumRows(0);
        ((DefaultTableModel) tblHeader.getModel()).addRow(new Object[]{
            1, "N", 1, 0
        });
        if (tblHeader.getRowCount() > 0) {
            tblHeader.setRowSelectionInterval(0, 0);
        }

        cmbCustPembayaran.setSelectedIndex(0);
        cmbCustPembayaranItemStateChanged(null);
    }

    private boolean udfCekBeforeSave() {
        boolean st = true;
        tblHeader.requestFocus(false);
        tblDetail.requestFocus(false);

        if (cmbCustPembayaran.getSelectedIndex() == 1 && txtAlamat.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Untuk pembelian KREDIT Pelanggan/ Pasien harus diisi!");
            txtNama.requestFocusInWindow();
            if (!txtNama.isFocusOwner()) {
                txtNama.requestFocus();
            }
            return false;
        }
        if (!isKoreksi && table.getRowCount() == 0) {
            JOptionPane.showMessageDialog(this, "Item transaksi masih belum diisi!");
            tblHeader.requestFocus();
            return false;
        }
        if (!isKoreksi && fn.udfGetDouble(lblNett.getText()) == 0) {
            JOptionPane.showMessageDialog(this, "Total penjualan masih Nol!");
            tblDetail.requestFocusInWindow();
            return false;
        }

        return st;
    }

    private void printKwitansi() {
        PrinterJob job = PrinterJob.getPrinterJob();
        SysConfig sy = new SysConfig();

        DocFlavor flavor = DocFlavor.SERVICE_FORMATTED.PRINTABLE;
        PrintRequestAttributeSet aset = new HashPrintRequestAttributeSet();
        PrintServiceAttributeSet pset = new HashPrintServiceAttributeSet();
        PrintService[] services = PrintServiceLookup.lookupPrintServices(flavor, null);
        int i = 0;
        for (i = 0; i < services.length; i++) {
            if (services[i].getName().equalsIgnoreCase(sy.getPrintKwtName())) {
                break;
            }
        }
        //if (JOptionPane.showConfirmDialog(null,"Cetak Invoice?","Message",JOptionPane.YES_NO_OPTION,JOptionPane.QUESTION_MESSAGE)==JOptionPane.YES_OPTION) {
        try {
            printPenjualan pn = new printPenjualan(conn, txtNoTrx.getText(), MainForm.sUserName, services[i]);

        } catch (java.lang.ArrayIndexOutOfBoundsException ie) {
            JOptionPane.showMessageDialog(this, "Printer tidak ditemukan!");
        }
    }

    private void udfSave() {
        if (!udfCekBeforeSave()) {
            return;
        }
        String sNoKoreksi = "";
        try {
            double bayar = 0;
            if (cmbCustPembayaran.getSelectedIndex() == 0) {
                DlgPembayaran d1 = new DlgPembayaran(this, true);

                if (!isKoreksi || fn.udfGetDouble(lblNett.getText()) > 0) {
                    d1.setNoTrx(txtNoTrx.getText());
                    d1.setTotal(fn.udfGetDouble(lblTotal.getText()));
                    d1.setDiskon(fn.udfGetDouble(txtDiskon.getText()));
                    d1.setSrcForm(aThis);
                    d1.setVisible(true);
                    if (!d1.isSelected()) {
                        return;
                    }
                    if (d1.getBayar() < fn.udfGetDouble(lblNett.getText())
                            && (txtAlamat.getText().trim().equalsIgnoreCase("") || txtAlamat.getText().trim().equalsIgnoreCase("CASH"))) {
                        JOptionPane.showMessageDialog(this, "Untuk transaksi kredit silakan masukkan nama pasien/ pelanggan terlebih dulu!");
                        txtNama.requestFocusInWindow();
                        return;
                    }
                    bayar = d1.getBayar();
                }
            }
            conn.setAutoCommit(false);
            ResultSet rs = null;

            if (isKoreksi) {
                if (table.getRowCount() == 0 && JOptionPane.showConfirmDialog(this, "Anda yakin untuk membatalkan penjualan ini?") != JOptionPane.YES_OPTION) {
                    return;
                }
                String query="select fn_penjualan_koreksi('" + txtNoTrx.getText() + "', "
                        + "'" + MainForm.sUserName + "', '" + MainForm.sShift + "')";
                System.out.println(query);
                rs = conn.createStatement().executeQuery(query);
                if (rs.next()) {
                    sNoKoreksi = rs.getString(1);
                }

                rs.close();
                if (table.getRowCount() == 0) {
                    this.dispose();
                }
            }

            if (txtNama.getText().trim().length() > 0 && txtNorm.getText().trim().length() == 0) {
                txtNorm.setText(pelangganDao.simpanPelanggan("", txtNama.getText(), null, txtAlamat.getText(), ""
                        + "", "", ""));
            }
            if (txtDokter.getText().trim().length() > 0 && lblDokter.getText().trim().length() == 0) {
                udfSimpanDokter();
            }
            rs = conn.createStatement().executeQuery("select fn_get_kode_jual("
                    + (isKoreksi ? "(select tanggal::date from penjualan where no_penjualan='" + txtNoTrx.getText() + "')" : "current_date") + " )");
            if (rs.next()) {
                txtNoTrx.setText(rs.getString(1));
            }
            rs.close();

            PreparedStatement ps = conn.prepareStatement("INSERT INTO penjualan("
                    + "no_penjualan, kode_pelanggan, nama_pelanggan, alamat, "
                    + "keterangan, kode_dokter, user_trx, shift, "
                    + "discount, no_resep, kode_jenis, ket_jenis_bayar, "
                    + "jth_tempo, st_lunas, multi_satuan, total, bayar, tipe_tarif, no_reg) values"
                    + "(?, ?, ?, ?, "
                    + "?, ?, ?, ?, "
                    + "?, ?, ?, ?, "
                    + "?, ?, ?, ?, ?, ?, ?) ");

            ps.setString(1, txtNoTrx.getText());
            ps.setString(2, txtNorm.getText());
            ps.setString(3, txtNama.getText());
            ps.setString(4, txtAlamat.getText());

            ps.setString(5, "");
            ps.setString(6, lblDokter.getText());
            ps.setString(7, MainForm.sUserName);
            ps.setString(8, MainForm.sShift);
            ps.setDouble(9, fn.udfGetDouble(txtDiskon.getText()));
            ps.setString(10, ""); //No. REsep
            ps.setString(11, cmbCustPembayaran.getSelectedItem().toString().equalsIgnoreCase("TUNAI") ? "1" : "0"); //No. REsep d1.getJenisBayar()
            ps.setString(12, ""); //Keterangan jenis bayar
            ps.setInt(13, cmbCustPembayaran.getSelectedIndex() == 0 ? 0 : fn.udfGetInt(txtTop.getText()));
            ps.setBoolean(14, (fn.udfGetDouble(lblNett.getText()) <= bayar)); //St Lunas
            ps.setBoolean(15, false); //Multi satuan
            ps.setDouble(16, fn.udfGetDouble(lblNett.getText()));
            ps.setDouble(17, bayar);
            ps.setString(18, cmbTipeTarif.getSelectedItem().toString());
            ps.setString(19, lblNoReg.getText());
            ps.executeUpdate();

            ps.close();
            ps = null;

            int colR = tblHeader.getColumnModel().getColumnIndex("R/");
            String noR = "";
            for (int i = 0; i < tblHeader.getRowCount(); i++) {
                if (tblHeader.getValueAt(i, colR).toString().equalsIgnoreCase("R")) {
                    if (ps == null || ps.isClosed()) {
                        ps = conn.prepareStatement("INSERT INTO penjualan_es(\n"
                                + "            no_penjualan, no_r, qty_r, es)\n"
                                + "    VALUES (?, ?, ?, ?);");
                    }
                    noR = tblHeader.getValueAt(i, tblHeader.getColumnModel().getColumnIndex("R/")).toString() + "#"
                            + tblHeader.getValueAt(i, tblHeader.getColumnModel().getColumnIndex("No")).toString();
                    ps.setString(1, txtNoTrx.getText());
                    ps.setString(2, noR);
                    ps.setInt(3, fn.udfGetInt(tblHeader.getValueAt(i, tblHeader.getColumnModel().getColumnIndex("Qty R"))));
                    ps.setDouble(4, fn.udfGetDouble(tblHeader.getValueAt(i, tblHeader.getColumnModel().getColumnIndex("ES"))));
                    ps.addBatch();
                }
            }

            if (ps != null) {
                ps.executeBatch();
                ps.close();
            }

            ps = conn.prepareStatement("INSERT INTO penjualan_detail(\n"
                    + "            no_penjualan, no_r, item_code, jumlah, harga, discount, tax, \n"
                    + "            kode_gudang, uom_jual, hpp, is_disc_rp, is_tax_rp)\n"
                    + "    VALUES (?, ?, ?, ?, ?, ?, ?, \n"
                    + "            ?, ?, ?, ?, ?);");

            TableColumnModel col = table.getColumnModel();
            double harga = 0, qty_jual = 0, disc = 0;
            for (int iRow = 0; iRow < table.getRowCount(); iRow++) {
                if (table.getValueAt(iRow, col.getColumnIndex("Kode")) != null
                        && table.getValueAt(iRow, col.getColumnIndex("Kode")).toString().length() > 0) {

                    qty_jual = fn.udfGetDouble(table.getValueAt(iRow, col.getColumnIndex("Qty Jual")));
                    harga = fn.udfGetDouble(table.getValueAt(iRow, col.getColumnIndex("Harga Satuan")));
                    disc = fn.udfGetDouble(table.getValueAt(iRow, col.getColumnIndex("Diskon")));

                    ps.setString(1, txtNoTrx.getText()); //no_penjualan
                    ps.setString(2, table.getValueAt(iRow, col.getColumnIndex("NoR")).toString()); //no_r
                    ps.setString(3, table.getValueAt(iRow, col.getColumnIndex("Kode")).toString()); //item_code
                    ps.setDouble(4, qty_jual); //jumlah
                    ps.setDouble(5, harga); //harga
                    ps.setDouble(6, disc); //discount
                    ps.setDouble(7, 0); //tax
                    ps.setString(8, sSiteID); //kode_gudang
                    ps.setString(9, table.getValueAt(iRow, col.getColumnIndex("Satuan")).toString()); //uom_jual
                    ps.setDouble(10, 0); //hpp
                    ps.setBoolean(11, true); //is_disc_rp
                    ps.setBoolean(12, false); //is_tax_rp
                    ps.addBatch();
                }
            }
            ps.executeBatch();

//            String sInsBayar="insert into penjualan_bayar(no_penjualan,sub_total,discount,ppn,total,bayar)" +
//                             "values('"+ txtNoTrx.getText() +"',0,0,0,"+ fn.udfGetDouble(lblTotal.getText()) +","+
//                             d1.getBayar() +");";
//            int i=conn.createStatement().executeUpdate(sInsBayar);
            conn.setAutoCommit(true);
            if (JOptionPane.showConfirmDialog(this, "Input data sukses, Klik ok untuk cetak invoice", "Message", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
                printKwitansi();
//                if(fn.udfGetDouble(lblTotal.getText())>d1.getBayar())
//                    printKwitansi();
//                
            }
            if (isKoreksi && srcForm != null) {
                if (srcForm instanceof FrmPenjualanHistory) {
                    ((FrmPenjualanHistory) srcForm).udfFilter();
                    this.dispose();
                    return;
                }
            }
            if (this.noReg.length() > 0) {
                this.dispose();
            } else {
                udfNew();
            }
            rs.close();
        } catch (SQLException se) {
            try {
                System.out.println("Error save: " + se.getMessage());
                conn.rollback();
                conn.setAutoCommit(true);
                System.out.println("Error: " + se.getMessage() + "\n" + se.getNextException());
                Logger.getLogger(PelangganDao.class.getName()).log(Level.SEVERE, null, se);

            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage());
            }
        }

    }

    private boolean isValidResepShift(String s) {
        boolean b = false;
        try {
            ResultSet rs = conn.createStatement().executeQuery("select shift from phar_resep where no_resep='" + s + "'");
            if (rs.next()) {
                b = (rs.getString(1).equalsIgnoreCase(MainForm.sShift));
                if (!b) {
                    JOptionPane.showMessageDialog(this, "Shift Resep ('" + rs.getString(1) + "') dengan "
                            + "shift yang dipilih, tidak bersesuaian!");
                }
            }
            rs.close();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage());
        }
        return b;
    }

    private int udfGetIdxHeader(String sNoR) {
        int idx = 0;
        for (int i = 0; i < tblHeader.getRowCount(); i++) {
            if (tblHeader.getValueAt(i, 0).toString().equalsIgnoreCase(sNoR)) {
                idx = i;
            }
        }

        return idx;
    }

    private Timestamp udfGetTanggalKoreksi(String sNoResep) {
        Timestamp time = null;
        try {
            ResultSet rs = conn.createStatement().executeQuery("select tanggal from phar_resep where no_resep='" + sNoResep + "'");
            if (rs.next()) {
                time = rs.getTimestamp("tanggal");
            }

            rs.close();
        } catch (SQLException se) {
            JOptionPane.showMessageDialog(this, se.getMessage());
        }

        return time;
    }

    public void mulaiTransaksi() {
        udfNew();
        this.setFocusable(true);
        java.awt.EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                txtNama.requestFocusInWindow();
            }
        });
    }

    public void setNoResep(String text) {
        txtNoTrx.setText(text);
    }

    private void setDueDate() {
        try {
            jFJtTempo.setText(new SimpleDateFormat("dd/MM/yyyy").format(
                    getDueDate(new SimpleDateFormat("dd/MM/yyyy").parse(lblTgl.getText()),
                            fn.udfGetInt(txtTop.getText()))));
        } catch (ParseException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage());
        }
    }

    private Date getDueDate(Date d, int i) {
        Calendar c = Calendar.getInstance();
        c.setTime(d);
        c.add(Calendar.DAY_OF_MONTH, i);

        return c.getTime();
    }

    public void setDesktopPane(JDesktopImage jDesktopPane1) {
        this.desktop = jDesktopPane1;
    }

    private void sesuaikanHargaJual() {
        int colKode = table.getColumnModel().getColumnIndex("Kode");
        int colHarga = table.getColumnModel().getColumnIndex("Harga Satuan");
        for (int i = 0; i < table.getRowCount(); i++) {
            String item = table.getValueAt(i, colKode).toString();
            Barang barang = itemDao.getBarangByKode(item, cmbTipeTarif.getSelectedItem().toString());
            if (barang != null) {
                table.setValueAt(cmbTipeTarif.getSelectedItem().toString().equalsIgnoreCase("KLINIK")? barang.getHargaKlinik(): barang.getHargaReseller(), i, colHarga);
            }
        }
    }

    public void setNoReg(String noReg) {
        this.noReg = noReg;
    }

    public class MyKeyListener extends KeyAdapter {

        @Override
        public void keyReleased(KeyEvent evt) {
            if (evt.getSource().equals(txtTop)) {
                setDueDate();
            } else if (evt.getSource().equals(txtDiskon)) {
                udfSetTotal();
                txtDiskon.setText(fn.intFmt.format(fn.udfGetDouble(txtDiskon.getText())));
            } else if (evt.getSource().equals(txtDiskonPersen)) {
                if(fn.udfGetInt(txtDiskonPersen.getText())>100){
                    JOptionPane.showMessageDialog(aThis, "Diskon harus kurang dari 100");
                    txtDiskonPersen.setText("0");
                }
                double diskon=fn.udfGetDouble(lblTotal.getText())*fn.udfGetDouble(txtDiskonPersen.getText())/100;
                txtDiskon.setText(fn.intFmt.format(diskon));
                udfSetTotal();
                
            }

        }

        @Override
        public void keyTyped(KeyEvent evt) {
            if (evt.getSource().equals(txtDiskon)) {
                GeneralFunction.keyTyped(evt);
            }

        }

        @Override
        public void keyPressed(KeyEvent evt) {
            Component ct = KeyboardFocusManager.getCurrentKeyboardFocusManager().getPermanentFocusOwner();
            int keyKode = evt.getKeyCode();
            switch (keyKode) {
                case KeyEvent.VK_INSERT: {
                    if (tblHeader.getSelectedRow() < 0) {
                        return;
                    }
                    tblHeader.changeSelection(tblHeader.getSelectedRow(), 0, false, false);

                    udfSetFilter();
                    if (tblDetail.getCellEditor() != null && evt.getSource().equals(tblDetail)) {
                        tblDetail.getCellEditor().stopCellEditing();
                    } else if (tblHeader.getCellEditor() != null && evt.getSource().equals(tblHeader)) {
                        tblHeader.getCellEditor().stopCellEditing();
                    }

                    cEditorH.setVisible(false);
//                    if(!tblHeader.getValueAt(tblHeader.getSelectedRow(), 1).toString().equalsIgnoreCase("T") && tblDetail.getRowCount()==0){
//                        udfAddItemRacikan();
//                    }
//                    else{
                    if (tblDetail.getSelectedRow() >= 0 && tblDetail.getValueAt(tblDetail.getSelectedRow(), 0) == null) {
                    } else {

                    }

                    lookupItem.clearText();
//                        lookupItem.setKodeGudang(sSiteID);
//                        lookupItem.setJikaExists(false);
                    lookupItem.setAlwaysOnTop(true);
//                        lookupItem.setColumn0Name("Kode");
                    lookupItem.setObjForm(aThis);
                    lookupItem.setSrcTable(tblDetail, tblDetail.getColumnModel().getColumnIndex("Qty Jual"));
                    lookupItem.setKeyEvent(evt);
//                        lookupItem.setNoR(tblHeader.getValueAt(tblHeader.getSelectedRow(), 1).toString()+"#"+tblHeader.getValueAt(tblHeader.getSelectedRow(), 0).toString());
//                        if(tblHeader.getSelectedRow()>=0){
//                            String sFilter=tblHeader.getValueAt(tblHeader.getSelectedRow(), 1).toString()+"#"+tblHeader.getValueAt(tblHeader.getSelectedRow(), 0).toString();
//                            lookupItem.udfSetFilter(sFilter);
//                        }
                    lookupItem.setVisible(true);
                    if (lookupItem.getKodeBarang().length() > 0) {
                        insertItem();

                    } else {
                        udfDeleteItemDetail();
                    }
                    //}
                    //lookupItem.requestFocusInWindow();
                    break;
                }
                case KeyEvent.VK_F3: {
                    if (evt.getSource().equals(tblDetail) && tblDetail.getSelectedRow() >= 0 && tblDetail.getValueAt(tblDetail.getSelectedRow(), 0) != null) {
                        tblDetail.changeSelection(tblDetail.getSelectedRow(), 0, false, false);
                        lookupItem.setSrcTable(tblDetail, tblDetail.getColumnModel().getColumnIndex("Qty Jual"));
                        lookupItem.setVisible(true);
                    }
                    break;
                }
                case KeyEvent.VK_F2: {
                    if (btnSave.isEnabled()) {
                        udfSave();
                    }
                    break;
                }
                case KeyEvent.VK_F9: {
                    if (tblDetail.getRowCount() == 0) {
                        return;
                    }
                    ((DefaultTableModel) tblHeader.getModel()).addRow(new Object[]{
                        tblHeader.getRowCount() + 1, "N", 0
                    });
                    tblHeader.requestFocusInWindow();
                    tblHeader.requestFocus();
                    if (tblHeader.getRowCount() > 0) {
                        tblHeader.setRowSelectionInterval(tblHeader.getRowCount() - 1, tblHeader.getRowCount() - 1);
                        tblHeader.changeSelection(tblHeader.getRowCount() - 1, 1, false, false);
                    }
                    break;
                }
                case KeyEvent.VK_ENTER: {
                    if (!(ct instanceof JTable)) {
                        if (!fn.isListVisible()) {
                            Component c = findNextFocus();
                            if (c == null) {
                                return;
                            }
                            if (c.isEnabled()) {
                                c.requestFocus();
                            } else {
                                c = findNextFocus();
                                if (c != null) {
                                    c.requestFocus();
                                };
                            }
                        } else {
                            fn.lstRequestFocus();
                        }
                    } else {
//                        if(jTable1.getSelectedColumn()<jTable1.getColumnCount()-1){
//                            jTable1.changeSelection(jTable1.getSelectedRow(), jTable1.getSelectedColumn()+1, false, false);
//                        }
                    }
                    break;
                }
                case KeyEvent.VK_DOWN: {
                    if (!(ct.getClass().getSimpleName().equalsIgnoreCase("JTABLE"))) {
                        if (!fn.isListVisible()) {
                            Component c = findNextFocus();
                            if (c == null) {
                                return;
                            }
                            if (c.isEnabled()) {
                                c.requestFocus();
                            } else {
                                c = findNextFocus();
                                if (c != null) {
                                    c.requestFocus();
                                };
                            }
                        } else {
                            fn.lstRequestFocus();
                        }
                        break;
                    }
                }

                case KeyEvent.VK_UP: {
                    if (!(ct.getClass().getSimpleName().equalsIgnoreCase("JTABLE"))) {
                        Component c = findPrevFocus();
                        if (c == null) {
                            return;
                        }
                        if (c.isEnabled()) {
                            c.requestFocus();
                        } else {
                            c = findNextFocus();
                            if (c != null) {
                                c.requestFocus();
                            };
                        }
                    }
                    break;
                }
                case KeyEvent.VK_DELETE: {
                    if (evt.getSource().equals(tblDetail)) {
                        udfDeleteItemDetail();
                    }

                    break;
                }
                case KeyEvent.VK_F11: {
                    tblHeader.requestFocusInWindow();
                    tblHeader.requestFocus();
                    tblHeader.changeSelection(tblHeader.getSelectedRow(), tblHeader.getSelectedColumn(), false, false);
                    break;
                }
                case KeyEvent.VK_F12: {
                    tblDetail.requestFocusInWindow();
                    tblDetail.requestFocus();
                    tblDetail.changeSelection(tblDetail.getSelectedRow(), tblDetail.getSelectedColumn(), false, false);
                    break;
                }
                case KeyEvent.VK_ESCAPE: {
                    if (JOptionPane.showConfirmDialog(null, "Anda Yakin Untuk Keluar?",
                            "SHS Pharmacy", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
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

    public void udfDeleteItemDetail() {
        if (tblDetail.getSelectedRow() >= 0) {
            int iRow[] = tblDetail.getSelectedRows();
            int rowPalingAtas = iRow[0];

            TableModel tm = tblDetail.getModel();

            while (iRow.length > 0) {
                //JOptionPane.showMessageDialog(null, iRow[0]);
                ((DefaultTableModel) tm).removeRow(tblDetail.convertRowIndexToModel(iRow[0]));
                iRow = tblDetail.getSelectedRows();
            }
            tblDetail.clearSelection();

            if (tblDetail.getRowCount() > 0 && rowPalingAtas < tblDetail.getRowCount()) {
                tblDetail.setRowSelectionInterval(rowPalingAtas, rowPalingAtas);
            } else {
                if (tblDetail.getRowCount() > 0) {
                    tblDetail.setRowSelectionInterval(rowPalingAtas - 1, rowPalingAtas - 1);
                } else {
                    tblDetail.requestFocus();
                }
            }
            if (tblDetail.getSelectedRow() >= 0) {
                tblDetail.changeSelection(tblDetail.getSelectedRow(), 0, false, false);
            }
        }
    }

    public JFormattedTextField getFormattedText() {
        JFormattedTextField fText = null;
        try {
            fText = new JFormattedTextField(new MaskFormatter("##/##/##")) {
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
        } catch (ParseException ex) {
            JOptionPane.showMessageDialog(aThis, ex.getMessage());
        }

        return fText;
    }

    JTextField ustTextField = new JTextField() {
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

    public class MyTableCellEditor extends AbstractCellEditor implements TableCellEditor {

        private Toolkit toolkit;
        JTextField text = ustTextField;

        JFormattedTextField fText = getFormattedText();

        int col, row;

        public Component getTableCellEditorComponent(JTable tblDetail, Object value,
                boolean isSelected, int rowIndex, int vColIndex) {
            // 'value' is value contained in the cell located at (rowIndex, vColIndex)
            row = rowIndex;
            col = vColIndex;
            text = ustTextField;
            text.setName("textEditor");

            text.addKeyListener(kListener);

            text.setBackground(new Color(0, 255, 204));
            text.addFocusListener(txtFocusListener);
            text.setFont(tblDetail.getFont());
            text.setVisible(!lookupItem.isVisible());
            if (lookupItem.isVisible()) {
                return null;
            }
            text.setText(value == null ? "" : value.toString());

            if (value instanceof Double || value instanceof Float || value instanceof Integer) {
                text.setText(fn.dFmt.format(value));
            } else {
                text.setText(value == null ? "" : value.toString());
            }
            return text;
        }

        public Object getCellEditorValue() {
            Object o = "";//=component.getText();
            Object retVal = 0;
            try {
                double qty = fn.udfGetDouble(((JTextField) text).getText());
//                double saldo=itemDao.getSaldo(tblDetail.getValueAt(tblDetail.getSelectedRow(), 0).toString(), sSiteID);
//                if(qty > saldo){
//                    JOptionPane.showMessageDialog(aThis, "Saldo Tidak Cukup\n"
//                            + "Saldo komputer: "+saldo);
//                    return tblDetail.getValueAt(tblDetail.getSelectedRow(), tblDetail.getSelectedColumn());
//                }
                retVal = qty;
                return retVal;
            } catch (Exception e) {
                toolkit.beep();
                retVal = 0;
            }
            return retVal;
        }
    }

    private FocusListener txtFocusListener = new FocusListener() {
        public void focusGained(FocusEvent e) {
            if (e.getSource() instanceof JTextField || e.getSource() instanceof JFormattedTextField) {
                ((JTextField) e.getSource()).setBackground(Color.YELLOW);
                if ((e.getSource() instanceof JTextField && ((JTextField) e.getSource()).getName() != null && ((JTextField) e.getSource()).getName().equalsIgnoreCase("textEditor"))) {
                    ((JTextField) e.getSource()).setSelectionStart(0);
                    ((JTextField) e.getSource()).setSelectionEnd(((JTextField) e.getSource()).getText().length());
                }
            }
        }

        public void focusLost(FocusEvent e) {
            if (e.getSource() instanceof JTextField || e.getSource() instanceof JFormattedTextField) {
                ((JTextField) e.getSource()).setBackground(Color.WHITE);
                if (e.getSource().equals(txtNoTrx) && isKoreksi && !e.isTemporary()) {
                    if (txtNoTrx.getText().isEmpty()) {
                        udfNew();
                    } else {
                        udfLoadKoreksiJual();
                    }
                }
            }
        }
    };

    private void udfSubTotalItem(int iRow) {
        TableColumnModel col = tblDetail.getColumnModel();
        if (((DefaultTableModel) tblDetail.getModel()).getValueAt(iRow, col.getColumnIndex("Qty Jual")) != null
                && ((DefaultTableModel) tblDetail.getModel()).getValueAt(iRow, col.getColumnIndex("Harga Satuan")) != null) {

            double subTotal = fn.udfGetDouble(((DefaultTableModel) tblDetail.getModel()).getValueAt(iRow, col.getColumnIndex("Qty Jual")))
                    * fn.udfGetDouble(((DefaultTableModel) tblDetail.getModel()).getValueAt(iRow, col.getColumnIndex("Harga Satuan")));

            ((DefaultTableModel) tblDetail.getModel()).setValueAt(subTotal, iRow, col.getColumnIndex("Sub Total"));
        }
    }

    private boolean isExistKitir(String sNo) {
        boolean b = false;
        try {
            ResultSet rs = conn.createStatement().executeQuery("select no_antrian from phar_resep_antrian where no_antrian='" + sNo + "'");
            b = rs.next();
            rs.close();
        } catch (SQLException se) {
            JOptionPane.showMessageDialog(this, se.getMessage());
        }

        return b;
    }

    public class MyTableCellEditorHeader extends AbstractCellEditor implements TableCellEditor {

        private Toolkit toolkit;
        JTextComponent text = new JTextField() {
            protected boolean processKeyBinding(final KeyStroke ks, final KeyEvent e, final int condition, final boolean pressed) {
                if (lookupItem.isVisible()) {
                    return true;
                }

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
            col = vColIndex;
            row = rowIndex;
            if (col == 1) {
                sRLama = value == null ? "" : value.toString();
            }
            int iText = (vColIndex == tblHeader.getColumnModel().getColumnIndex("R/") ? 1 : 30);
            text.setBackground(new Color(0, 255, 204));
            text.addFocusListener(txtFocusListener);
            text.addKeyListener(kListener);
            text.setFont(table.getFont());
            text.setName("textEditor");
            text.removeKeyListener(kListener);
            AbstractDocument doc = (AbstractDocument) text.getDocument();
            doc.setDocumentFilter(null);
            doc.setDocumentFilter(new FixedSizeFilter(iText));

            text.removeKeyListener(kListener);
            text.addKeyListener(kListener);

            text.setVisible(!lookupItem.isVisible());
            if (lookupItem.isVisible()) {
                return null;
            }

            if (isSelected) {
            }

            if (value instanceof Double || value instanceof Float || value instanceof Integer) {
                double dVal = fn.udfGetFloat(value);
                text.setText(fn.dFmt.format(dVal));
            } else {
                text.setText(value == null ? "" : value.toString());
            }
            return text;
        }

        public Object getCellEditorValue() {
            Object retVal = 0;
            try {
                if (col == tblHeader.getColumnModel().getColumnIndex("ES") || col == tblHeader.getColumnModel().getColumnIndex("Qty R")) {
                    retVal = fn.udfGetDouble(((JTextField) text).getText());
                } else {
                    retVal = (((JTextField) text).getText().toUpperCase());
                    retVal = retVal.toString().equalsIgnoreCase("R") ? "R" : "N";
                }
                sRBaru = retVal.toString();
                if (modelDetail.getRowCount() == 0) {
                    return retVal;
                }

                if (col == tblHeader.getColumnModel().getColumnIndex("R/") && !sRBaru.equalsIgnoreCase(sRLama)) {
                    String sOldR = sRLama + "#" + tblHeader.getValueAt(tblHeader.getSelectedRow(), 0).toString();
                    String sNewR = sRBaru + "#" + tblHeader.getValueAt(tblHeader.getSelectedRow(), 0).toString();

                    for (int i = 0; i < modelDetail.getRowCount(); i++) {
                        if (modelDetail.getValueAt(i, tblDetail.getColumnModel().getColumnIndex("NoR")).toString().equalsIgnoreCase(sOldR)) {
                            modelDetail.setValueAt(sNewR, i, tblDetail.getColumnModel().getColumnIndex("NoR"));
                        }
                    }
                }

                return retVal;
            } catch (Exception e) {
                toolkit.beep();
                retVal = 0;
            }
            return retVal;
        }

        public void setVisible(boolean b) {
            text.setVisible(b);
        }

        public boolean isVisible() {
            return text.isVisible();
        }
    }

    class FixedSizeFilter extends DocumentFilter {

        int maxSize;

        // limit is the maximum number of characters allowed.
        public FixedSizeFilter(int limit) {
            maxSize = limit;
        }

        // This method is called when characters are inserted into the document
        public void insertString(DocumentFilter.FilterBypass fb, int offset, String str,
                AttributeSet attr) throws BadLocationException {
            replace(fb, offset, 0, str, attr);
        }

        // This method is called when characters in the document are replace with other characters
        public void replace(DocumentFilter.FilterBypass fb, int offset, int length,
                String str, AttributeSet attrs) throws BadLocationException {
            int newLength = fb.getDocument().getLength() - length + str.length();
            if (newLength <= maxSize) {
                fb.replace(offset, length, str, attrs);
            } else {
                throw new BadLocationException("New characters exceeds max size of document", offset);
            }
        }
    }

//    private void udfAddItemRacikan(){
//        int iRow=tblHeader.getSelectedRow();
//        if(iRow>=0){
//            if(fn.udfGetDouble(tblHeader.getValueAt(iRow, tblHeader.getColumnModel().getColumnIndex("Qty R")))==0){
//                JOptionPane.showMessageDialog(this, "Isikan Qty R terlebih dulu!");
//                tblHeader.requestFocusInWindow();
//                tblHeader.changeSelection(iRow, tblHeader.getColumnModel().getColumnIndex("Qty R"), false, false);
//                return;
//            }
//
//            FrmLookupRacikan frmDet = new FrmLookupRacikan(conn);
//
//            frmDet.setSite_id(sSiteID);
//            frmDet.setManual(true);
//            frmDet.setNoR(tblHeader.getValueAt(iRow, 1).toString()+"#"+tblHeader.getValueAt(iRow, 0).toString());
//            //frmDet.setDataStore(aList);
//            frmDet.setJumlah(fn.udfGetDouble(tblHeader.getValueAt(iRow, tblHeader.getColumnModel().getColumnIndex("Qty R"))));
//            frmDet.setIsKoreksi(false);
//            frmDet.setiRow(iRow);
//            frmDet.setUangR(uangR);
//            frmDet.setKelas(txtKelas.getText().trim());
//            frmDet.setTableCaraBuat(tblHeader);
//            frmDet.setTableFocus(tblDetail);
//            frmDet.setKode_dokter(txtDokter.getText().trim());
//            frmDet.setNoR(tblHeader.getValueAt(tblHeader.getSelectedRow(), 1).toString()+"#"+tblHeader.getValueAt(tblHeader.getSelectedRow(), 0).toString());
//            frmDet.requestFocusInWindow();
//            frmDet.setVisible(true);
//            
//        }
//    }
    private double getEmbalaseService(int jumlah) {
        double dEmbalase = 0;
        try {
            String sSql = "";
            ResultSet rs = null;
            sSql = "select * from es where " + jumlah + ">=min_qty and " + jumlah + "<=max_qty";
            rs = conn.createStatement().executeQuery(sSql);
            if (rs.next()) {
                dEmbalase = rs.getDouble("biaya");
            }
            rs.close();

        } catch (SQLException ex) {
            Logger.getLogger(FrmPenjualan.class.getName()).log(Level.SEVERE, null, ex);
        }
        return dEmbalase;
    }

    private void udfLoadKoreksiJual() {
        String sQry = "select distinct h.no_penjualan as sales_no, to_char(h.tanggal, 'dd/MM/yyyy') as tgl_trx, "
                + "coalesce(p.norm, h.kode_pelanggan) as kode_cust, coalesce(p.nama||coalesce(', '||p.title,''), h.nama_pelanggan) as nama_cust, "
                + "coalesce(p.alamat_domisili,'') as alamat, "
                + "coalesce(d.kode_gudang,'') as kode_gudang, coalesce(h.discount,0) as discount,"
                + "coalesce(g.deskripsi,'') as nama_gudang, coalesce(h.keterangan,'') as catatan, "
                + "case when h.kode_jenis='2' then 'KREDIT' else 'TUNAI' end as jenis, coalesce(h.koreksi, false) as koreksi, "
                + "coalesce(h.kode_dokter,'') as kode_dokter, coalesce(dok.nama||', '||coalesce(dok.gelar_depan,'')||', '||coalesce(dok.gelar_belakang,''), '-') as nama_dokter "
                + "from penjualan h "
                + "inner join penjualan_detail d on d.no_penjualan=h.no_penjualan "
                + "left join rm_pasien p on p.norm=h.kode_pelanggan "
                + "left join gudang g on g.kode_gudang=d.kode_gudang "
                + "left join rm_dokter dok on dok.kode_dokter=h.kode_dokter "
                + "where h.no_penjualan='" + txtNoTrx.getText() + "'";
        System.out.println(sQry);
        try {
            ResultSet rs = conn.createStatement().executeQuery(sQry);
            if (rs.next()) {
                if (rs.getBoolean("koreksi") == true) {
                    JOptionPane.showMessageDialog(this, "Transaksi penjualan sudah pernah dikoreksi!");
                    udfNew();
                    if (!txtNoTrx.isFocusOwner()) {
                        txtNoTrx.requestFocus();
                    }

                    return;
                }
                txtNoTrx.setText(rs.getString("sales_no"));
                txtNama.setText(rs.getString("nama_cust"));
                txtNorm.setText(rs.getString("kode_cust"));
                txtAlamat.setText(rs.getString("alamat"));

                lblTgl.setText(rs.getString("tgl_trx"));
                txtCatatan.setText(rs.getString("catatan"));
                cmbCustPembayaran.setSelectedItem(rs.getString("jenis"));

                rs.close();
                sQry = "select d.item_code, coalesce(i.nama_paten,'') as nama_item, coalesce(i.satuan_kecil,'') as satuan_kecil,"
                        + "coalesce(d.jumlah, 0) as qty_jual, coalesce(d.harga,0) as unit_price, "
                        + "coalesce(d.discount,0) as discount, coalesce(d.tax,0) as tax, "
                        + "(coalesce(d.jumlah,0) * coalesce(d.harga,0))-coalesce(d.discount,0) as sub_Total,  "
                        + "case when coalesce(d.no_r,'')='' then 'T#1' else d.no_r end as no_racikan "
                        + "from penjualan_detail d "
                        + "inner join barang i on i.item_code=d.item_code  "
                        + "where d.no_penjualan='" + txtNoTrx.getText() + "'";

                ((DefaultTableModel) table.getModel()).setNumRows(0);
                rs = conn.createStatement().executeQuery(sQry);
                double diskon=0;
                while (rs.next()) {
                    ((DefaultTableModel) table.getModel()).addRow(new Object[]{
                        rs.getString("item_code"),
                        rs.getString("nama_item"),
                        rs.getString("satuan_kecil"),
                        rs.getDouble("qty_jual"),
                        rs.getDouble("unit_price"),
                        rs.getDouble("discount"),
                        rs.getDouble("sub_Total"),
                        rs.getString("no_racikan")
                    });
                    diskon=rs.getDouble("discount");
                }
                txtDiskon.setText(fn.intFmt.format(diskon));
                udfSetTotal();
                rs.close();
                if (table.getRowCount() > 0) {
                    table.setRowSelectionInterval(0, 0);
                }

            } else {
                JOptionPane.showMessageDialog(this, "No. Penjualan tidak ditemukan!");
                udfNew();
                txtNoTrx.requestFocus();
            }
            rs.close();
        } catch (SQLException se) {
            JOptionPane.showMessageDialog(this, se.getMessage());
        }

    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jScrollPane1 = new javax.swing.JScrollPane();
        tblDetail = new javax.swing.JTable();
        jScrollPane2 = new javax.swing.JScrollPane();
        tblHeader = new javax.swing.JTable();
        jLabel2 = new javax.swing.JLabel();
        jScrollPane3 = new javax.swing.JScrollPane();
        table = new javax.swing.JTable();
        jPanel1 = new javax.swing.JPanel();
        jLabel15 = new javax.swing.JLabel();
        jLabel18 = new javax.swing.JLabel();
        txtNoTrx = new javax.swing.JTextField();
        jPanel2 = new javax.swing.JPanel();
        jLabel11 = new javax.swing.JLabel();
        txtTop = new javax.swing.JTextField();
        jFJtTempo = new javax.swing.JFormattedTextField();
        jLabel16 = new javax.swing.JLabel();
        cmbCustPembayaran = new javax.swing.JComboBox();
        jLabel12 = new javax.swing.JLabel();
        jLabel13 = new javax.swing.JLabel();
        jLabel20 = new javax.swing.JLabel();
        cmbTipeTarif = new javax.swing.JComboBox();
        jLabel19 = new javax.swing.JLabel();
        jLabel1 = new javax.swing.JLabel();
        txtAlamat = new javax.swing.JTextField();
        txtNama = new javax.swing.JTextField();
        jLabel4 = new javax.swing.JLabel();
        txtDokter = new javax.swing.JTextField();
        lblDokter = new javax.swing.JTextField();
        btnAddDokter = new javax.swing.JButton();
        jLabel14 = new javax.swing.JLabel();
        lblTgl = new javax.swing.JLabel();
        jLabel22 = new javax.swing.JLabel();
        txtNorm = new javax.swing.JTextField();
        btnLookupPasien = new javax.swing.JButton();
        jLabel21 = new javax.swing.JLabel();
        lblNoReg = new javax.swing.JLabel();
        jToolBar1 = new javax.swing.JToolBar();
        btnNew = new javax.swing.JButton();
        btnSave = new javax.swing.JButton();
        btnCancel = new javax.swing.JButton();
        jLabel17 = new javax.swing.JLabel();
        lblNett = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        txtCatatan = new javax.swing.JTextField();
        jPanel3 = new javax.swing.JPanel();
        lblTotal = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        txtDiskon = new javax.swing.JTextField();
        txtDiskonPersen = new javax.swing.JTextField();
        jLabel7 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Penjualan Resep");
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowOpened(java.awt.event.WindowEvent evt) {
                formWindowOpened(evt);
            }
        });

        tblDetail.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        tblDetail.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Kode", "Nama Barang", "Satuan", "Qty Jual", "Harga Satuan", "Diskon", "Sub Total", "NoR", "Koreksi"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Double.class, java.lang.Double.class, java.lang.Double.class, java.lang.Double.class, java.lang.Object.class, java.lang.Boolean.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, true, true, true, false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        tblDetail.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_OFF);
        tblDetail.getTableHeader().setReorderingAllowed(false);
        jScrollPane1.setViewportView(tblDetail);

        tblHeader.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        tblHeader.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {"1", "N",  new Double(1.0),  new Double(0.0)}
            },
            new String [] {
                "No", "R/", "Qty R", "ES"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Object.class, java.lang.Object.class, java.lang.Double.class, java.lang.Double.class
            };
            boolean[] canEdit = new boolean [] {
                false, true, true, true
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
        tblHeader.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                tblHeaderFocusLost(evt);
            }
        });
        tblHeader.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                tblHeaderKeyPressed(evt);
            }
        });
        jScrollPane2.setViewportView(tblHeader);

        jLabel2.setBackground(new java.awt.Color(204, 255, 255));
        jLabel2.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel2.setText("<html> \n&nbsp <b>Ins</b> &nbsp;: Tambah Item<br> \n&nbsp <b>F9</b>   &nbsp; &nbsp;: Add Item Header (Menambah <b>R</b> Racikan & <b>N</b> Non Racikan)<br> \n&nbsp <b>F3</b>   &nbsp; &nbsp;: Edit Item Detail<br> &nbsp <b>F11</b> &nbsp: Pindah Focus Ke Header, &nbsp;\n&nbsp <b>F12</b> &nbsp;: Pindah Focus Ke Detail<br> \n&nbsp <b>F2</b> &nbsp; &nbsp: Simpan Transaksi<br> \n</html>");
        jLabel2.setBorder(new javax.swing.border.MatteBorder(null));
        jLabel2.setOpaque(true);

        table.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        table.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_OFF);
        table.setEnabled(false);
        jScrollPane3.setViewportView(table);

        jPanel1.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        jPanel1.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel15.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jLabel15.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel15.setText(":");
        jPanel1.add(jLabel15, new org.netbeans.lib.awtextra.AbsoluteConstraints(90, 10, 10, 20));

        jLabel18.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jLabel18.setText("No. Trans.");
        jPanel1.add(jLabel18, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 10, 105, 20));

        txtNoTrx.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        txtNoTrx.setText("120101000000");
        txtNoTrx.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(153, 153, 153)));
        txtNoTrx.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtNoTrxActionPerformed(evt);
            }
        });
        jPanel1.add(txtNoTrx, new org.netbeans.lib.awtextra.AbsoluteConstraints(100, 10, 110, 22));

        jPanel2.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel11.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jLabel11.setText("Tgl. Jt Tempo");
        jPanel2.add(jLabel11, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 60, 90, 20));

        txtTop.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        txtTop.setText("14");
        txtTop.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        txtTop.setDisabledTextColor(new java.awt.Color(0, 0, 0));
        jPanel2.add(txtTop, new org.netbeans.lib.awtextra.AbsoluteConstraints(100, 35, 40, 20));

        jFJtTempo.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 1, true));
        jFJtTempo.setEnabled(false);
        jFJtTempo.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jPanel2.add(jFJtTempo, new org.netbeans.lib.awtextra.AbsoluteConstraints(100, 60, 90, 20));

        jLabel16.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jLabel16.setText("Pembayaran");
        jPanel2.add(jLabel16, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 10, 90, 20));

        cmbCustPembayaran.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        cmbCustPembayaran.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "TUNAI", "KREDIT" }));
        cmbCustPembayaran.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                cmbCustPembayaranItemStateChanged(evt);
            }
        });
        jPanel2.add(cmbCustPembayaran, new org.netbeans.lib.awtextra.AbsoluteConstraints(100, 10, 110, -1));

        jLabel12.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jLabel12.setText(" Hr.");
        jPanel2.add(jLabel12, new org.netbeans.lib.awtextra.AbsoluteConstraints(140, 35, 30, 20));

        jLabel13.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jLabel13.setText("Jatuh Tempo");
        jPanel2.add(jLabel13, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 35, 90, 20));

        jLabel20.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jLabel20.setText("Tarif");
        jPanel2.add(jLabel20, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 85, 90, 20));

        cmbTipeTarif.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        cmbTipeTarif.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "KLINIK", "RESELLER" }));
        cmbTipeTarif.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                cmbTipeTarifItemStateChanged(evt);
            }
        });
        jPanel2.add(cmbTipeTarif, new org.netbeans.lib.awtextra.AbsoluteConstraints(100, 85, 110, -1));

        jPanel1.add(jPanel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(565, 5, 210, 110));

        jLabel19.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jLabel19.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel19.setText(":");
        jPanel1.add(jLabel19, new org.netbeans.lib.awtextra.AbsoluteConstraints(90, 35, 10, 20));

        jLabel1.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jLabel1.setText("Pasien");
        jPanel1.add(jLabel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 35, 80, 20));

        txtAlamat.setEditable(false);
        txtAlamat.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        txtAlamat.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        txtAlamat.setDisabledTextColor(new java.awt.Color(0, 0, 0));
        jPanel1.add(txtAlamat, new org.netbeans.lib.awtextra.AbsoluteConstraints(100, 55, 390, 20));

        txtNama.setEditable(false);
        txtNama.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        txtNama.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        jPanel1.add(txtNama, new org.netbeans.lib.awtextra.AbsoluteConstraints(100, 35, 320, 20));

        jLabel4.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jLabel4.setText("Dokter");
        jPanel1.add(jLabel4, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 80, 80, 20));

        txtDokter.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        txtDokter.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        txtDokter.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtDokterKeyReleased(evt);
            }
        });
        jPanel1.add(txtDokter, new org.netbeans.lib.awtextra.AbsoluteConstraints(100, 80, 335, 20));

        lblDokter.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        lblDokter.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        lblDokter.setDisabledTextColor(new java.awt.Color(0, 0, 0));
        lblDokter.setEnabled(false);
        jPanel1.add(lblDokter, new org.netbeans.lib.awtextra.AbsoluteConstraints(435, 80, 55, 20));

        btnAddDokter.setText("+");
        btnAddDokter.setToolTipText("Tambah data dokter");
        btnAddDokter.setMargin(new java.awt.Insets(2, 2, 2, 2));
        btnAddDokter.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                btnAddDokterMouseClicked(evt);
            }
        });
        btnAddDokter.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAddDokterActionPerformed(evt);
            }
        });
        jPanel1.add(btnAddDokter, new org.netbeans.lib.awtextra.AbsoluteConstraints(490, 80, 30, 22));

        jLabel14.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jLabel14.setText("Tgl. Transaksi");
        jPanel1.add(jLabel14, new org.netbeans.lib.awtextra.AbsoluteConstraints(215, 10, 90, 20));

        lblTgl.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        lblTgl.setText("20/12/2010");
        lblTgl.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        jPanel1.add(lblTgl, new org.netbeans.lib.awtextra.AbsoluteConstraints(305, 10, 75, 20));

        jLabel22.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jLabel22.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel22.setText(":");
        jPanel1.add(jLabel22, new org.netbeans.lib.awtextra.AbsoluteConstraints(90, 80, 10, 20));

        txtNorm.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        txtNorm.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        txtNorm.setDisabledTextColor(new java.awt.Color(0, 0, 0));
        txtNorm.setEnabled(false);
        jPanel1.add(txtNorm, new org.netbeans.lib.awtextra.AbsoluteConstraints(420, 35, 70, 20));

        btnLookupPasien.setText("...");
        btnLookupPasien.setToolTipText("Tambah data pasien/ pelanggan");
        btnLookupPasien.setMargin(new java.awt.Insets(2, 2, 2, 2));
        btnLookupPasien.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                btnLookupPasienMouseClicked(evt);
            }
        });
        btnLookupPasien.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnLookupPasienActionPerformed(evt);
            }
        });
        jPanel1.add(btnLookupPasien, new org.netbeans.lib.awtextra.AbsoluteConstraints(490, 35, 30, 22));

        jLabel21.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jLabel21.setText("No. Reg :");
        jPanel1.add(jLabel21, new org.netbeans.lib.awtextra.AbsoluteConstraints(385, 10, 60, 20));

        lblNoReg.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        lblNoReg.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        jPanel1.add(lblNoReg, new org.netbeans.lib.awtextra.AbsoluteConstraints(445, 10, 105, 20));

        jToolBar1.setRollover(true);

        btnNew.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/Icon/32/add.png"))); // NOI18N
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
        btnCancel.setText("Cancel");
        btnCancel.setFocusable(false);
        btnCancel.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnCancel.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnCancel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCancelActionPerformed(evt);
            }
        });
        jToolBar1.add(btnCancel);

        jLabel17.setFont(new java.awt.Font("Tahoma", 0, 36)); // NOI18N
        jLabel17.setForeground(new java.awt.Color(0, 0, 153));
        jLabel17.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel17.setText("Transaksi");
        jLabel17.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);

        lblNett.setBackground(new java.awt.Color(0, 0, 102));
        lblNett.setFont(new java.awt.Font("Tahoma", 1, 32)); // NOI18N
        lblNett.setForeground(new java.awt.Color(255, 255, 255));
        lblNett.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lblNett.setText("0.00");
        lblNett.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(255, 255, 255), 2, true));
        lblNett.setOpaque(true);

        jLabel10.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jLabel10.setText("Keterangan :");

        txtCatatan.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        txtCatatan.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        txtCatatan.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtCatatanKeyReleased(evt);
            }
        });

        jPanel3.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        jPanel3.setLayout(null);

        lblTotal.setFont(new java.awt.Font("Arial", 1, 14)); // NOI18N
        lblTotal.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lblTotal.setText("0");
        lblTotal.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        jPanel3.add(lblTotal);
        lblTotal.setBounds(135, 5, 175, 25);

        jLabel5.setText("Total : ");
        jPanel3.add(jLabel5);
        jLabel5.setBounds(15, 5, 110, 25);

        jLabel6.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel6.setText("%");
        jPanel3.add(jLabel6);
        jLabel6.setBounds(110, 30, 20, 25);

        txtDiskon.setFont(new java.awt.Font("Arial", 1, 14)); // NOI18N
        txtDiskon.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtDiskon.setText("0");
        txtDiskon.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        jPanel3.add(txtDiskon);
        txtDiskon.setBounds(135, 30, 175, 25);

        txtDiskonPersen.setFont(new java.awt.Font("Arial", 1, 14)); // NOI18N
        txtDiskonPersen.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtDiskonPersen.setText("0");
        txtDiskonPersen.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        jPanel3.add(txtDiskonPersen);
        txtDiskonPersen.setBounds(80, 30, 25, 25);

        jLabel7.setText("Diskon : ");
        jPanel3.add(jLabel7);
        jLabel7.setBounds(15, 30, 65, 25);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(15, 15, 15)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(95, 95, 95)
                        .addComponent(txtCatatan, javax.swing.GroupLayout.PREFERRED_SIZE, 680, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jLabel10, javax.swing.GroupLayout.PREFERRED_SIZE, 110, javax.swing.GroupLayout.PREFERRED_SIZE)))
            .addGroup(layout.createSequentialGroup()
                .addGap(10, 10, 10)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addGap(150, 150, 150)
                                .addComponent(jLabel17, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                            .addComponent(jToolBar1, javax.swing.GroupLayout.PREFERRED_SIZE, 160, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(10, 10, 10)
                        .addComponent(lblNett, javax.swing.GroupLayout.PREFERRED_SIZE, 264, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, 780, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 490, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(6, 6, 6)
                        .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE))
                    .addComponent(jScrollPane1)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel2)
                        .addGap(5, 5, 5)
                        .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, 320, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(7, 7, 7))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel17)
                    .addComponent(jToolBar1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblNett, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(6, 6, 6)
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 110, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 110, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 137, Short.MAX_VALUE)
                .addGap(7, 7, 7)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(txtCatatan, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel10, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(5, 5, 5)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 85, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, 85, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(1, 1, 1))
        );

        setSize(new java.awt.Dimension(813, 589));
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void formWindowOpened(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowOpened
        udfInitForm();
    }//GEN-LAST:event_formWindowOpened

    private void tblHeaderKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_tblHeaderKeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_DELETE && tblHeader.getSelectedRow() >= 0) {
            int iRow[] = tblHeader.getSelectedRows();
            int rowPalingAtas = iRow[0];

            TableModel tm = tblHeader.getModel();

            while (iRow.length > 0) {
                //JOptionPane.showMessageDialog(null, iRow[0]);

                TableColumnModel colD = tblDetail.getColumnModel();
                int i = 0;
                do {
                    if (modelDetail.getValueAt(i, colD.getColumnIndex("NoR")).toString().equalsIgnoreCase(
                            tblHeader.getValueAt(tblHeader.getSelectedRow(), 1).toString() + "#" + tblHeader.getValueAt(tblHeader.getSelectedRow(), 0).toString())) {
                        modelDetail.removeRow(i);
                    } else {
                        i++;
                    }

                } while (i < modelDetail.getRowCount());
                ((DefaultTableModel) tm).removeRow(tblHeader.convertRowIndexToModel(iRow[0]));
                iRow = tblHeader.getSelectedRows();
            }
            tblHeader.clearSelection();

            if (tblHeader.getRowCount() > 0 && rowPalingAtas < tblHeader.getRowCount()) {
                tblHeader.setRowSelectionInterval(rowPalingAtas, rowPalingAtas);
            } else {
                if (tblHeader.getRowCount() > 0) {
                    tblHeader.setRowSelectionInterval(rowPalingAtas - 1, rowPalingAtas - 1);
                } else {
                    ((DefaultTableModel) tblHeader.getModel()).addRow(new Object[]{
                        1, "N", 1, 0
                    });
                    tblHeader.setRowSelectionInterval(0, 0);
                    tblHeader.requestFocus();
                }
            }
            if (tblHeader.getSelectedRow() >= 0) {
                tblHeader.changeSelection(tblHeader.getSelectedRow(), 0, false, false);
            }
        }
    }//GEN-LAST:event_tblHeaderKeyPressed

    private void removeItemDetail(String sNoR) {
        TableColumnModel colD = tblDetail.getColumnModel();
        for (int i = 0; i < modelDetail.getRowCount(); i++) {
            if (modelDetail.getValueAt(i, colD.getColumnIndex("NoR")).toString().equalsIgnoreCase(
                    sNoR)) {
                modelDetail.removeRow(i);
            }
        }
    }


    private void btnNewActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnNewActionPerformed
        udfClear();
        btnCancel.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/Icon/32/cross.png")));
        btnCancel.setText("Cancel");
        btnSave.setEnabled(true);
        btnNew.setEnabled(false);
        if (!txtNama.isFocusOwner()) {
            txtNama.requestFocus();
        }
}//GEN-LAST:event_btnNewActionPerformed

    private void btnSaveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSaveActionPerformed
        udfSave();
}//GEN-LAST:event_btnSaveActionPerformed

    private void btnCancelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCancelActionPerformed
        if (btnCancel.getText().equalsIgnoreCase("cancel")) {
            if (getTitle().indexOf("Koreksi") > 0) {
                dispose();
            }
            btnSave.setEnabled(false);
            btnNew.setEnabled(true);
            btnCancel.setText("Exit");
            btnCancel.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/Icon/32/cross.png")));
        } else {
            this.dispose();
        }
}//GEN-LAST:event_btnCancelActionPerformed

    private void tblHeaderFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_tblHeaderFocusLost
        //tblHeader.validate();
    }//GEN-LAST:event_tblHeaderFocusLost

    private void txtCatatanKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtCatatanKeyReleased
        // TODO add your handling code here:
}//GEN-LAST:event_txtCatatanKeyReleased

    private void cmbCustPembayaranItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_cmbCustPembayaranItemStateChanged
        jLabel11.setVisible(cmbCustPembayaran.getSelectedIndex() == 1);
        jFJtTempo.setVisible(cmbCustPembayaran.getSelectedIndex() == 1);
        txtTop.setVisible(cmbCustPembayaran.getSelectedIndex() == 1);
        jLabel12.setVisible(cmbCustPembayaran.getSelectedIndex() == 1);
        jLabel13.setVisible(cmbCustPembayaran.getSelectedIndex() == 1);
        setDueDate();
}//GEN-LAST:event_cmbCustPembayaranItemStateChanged

    private void udfNewCustomer() {
        DlgPasien fMaster = new DlgPasien(this, true);
        fMaster.setTitle("Pasien/ Customer baru");
        fMaster.setSrcForm(this);
        fMaster.setVisible(true);
        if (fMaster.isSelected()) {
            txtNama.setText(fMaster.getNama());
            txtNorm.setText(fMaster.getNorm());
            txtAlamat.setText(fMaster.getAlamat());
        }
    }

    private void udfSimpanDokter() {
        try {
            conn.setAutoCommit(false);
            ResultSet rs = conn.createStatement().executeQuery(
                    "select fn_dokter_save('', "
                    + "'" + txtDokter.getText() + "', '', '', "
                    + "'', "
                    + "'', '', '')");
            conn.setAutoCommit(true);
            if (rs.next()) {
                lblDokter.setText(rs.getString(1));
            }
            rs.close();
        } catch (SQLException se) {
            try {
                JOptionPane.showMessageDialog(this, se.getMessage());
                conn.rollback();
            } catch (SQLException ex) {
                Logger.getLogger(DlgDokter.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    private void txtDokterKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtDokterKeyReleased
        fn.lookup(evt, new Object[]{lblDokter},
                "select coalesce(nama,'') as nama_dokter, kode_dokter from dokter "
                + "where kode_dokter||coalesce(nama,'') ilike '%" + txtDokter.getText() + "%'", 500, 200);
}//GEN-LAST:event_txtDokterKeyReleased

    private void udfNewDokter() {
        DlgDokter fMaster = new DlgDokter(this, true);
        fMaster.setTitle("Data dokter baru");
        fMaster.setConn(conn);
        fMaster.setSrcForm(this);
        fMaster.setVisible(true);
        if (fMaster.isSelected()) {
            txtDokter.setText(fMaster.getNamaDokter());
            lblDokter.setText(fMaster.getKodeDokter());
        }
    }

    private void btnAddDokterMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnAddDokterMouseClicked
        udfNewDokter();
}//GEN-LAST:event_btnAddDokterMouseClicked

    private void btnAddDokterActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAddDokterActionPerformed

}//GEN-LAST:event_btnAddDokterActionPerformed

    private void btnLookupPasienMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnLookupPasienMouseClicked
        // TODO add your handling code here:
    }//GEN-LAST:event_btnLookupPasienMouseClicked

    private void btnLookupPasienActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnLookupPasienActionPerformed
        lookupPasien();
    }//GEN-LAST:event_btnLookupPasienActionPerformed

    private void cmbTipeTarifItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_cmbTipeTarifItemStateChanged
        sesuaikanHargaJual();
    }//GEN-LAST:event_cmbTipeTarifItemStateChanged

    private void txtNoTrxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtNoTrxActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtNoTrxActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
//        try{
//                UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
//                BorderUIResource borderUIResource= new BorderUIResource(BorderFactory.createLineBorder(Color.yellow, 3));
//                UIManager.put("Table.focusCellHighlightBorder", borderUIResource);
//        } catch (Exception e){
//            JOptionPane.showMessageDialog(null, "Couldn't load Windows look and feel " + e);
//        }

        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new FrmPenjualan().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnAddDokter;
    private javax.swing.JButton btnCancel;
    private javax.swing.JButton btnLookupPasien;
    private javax.swing.JButton btnNew;
    private javax.swing.JButton btnSave;
    private javax.swing.JComboBox cmbCustPembayaran;
    private javax.swing.JComboBox cmbTipeTarif;
    private javax.swing.JFormattedTextField jFJtTempo;
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
    private javax.swing.JLabel jLabel19;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel20;
    private javax.swing.JLabel jLabel21;
    private javax.swing.JLabel jLabel22;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JToolBar jToolBar1;
    private javax.swing.JTextField lblDokter;
    private javax.swing.JLabel lblNett;
    private javax.swing.JLabel lblNoReg;
    private javax.swing.JLabel lblTgl;
    private javax.swing.JLabel lblTotal;
    private javax.swing.JTable table;
    private javax.swing.JTable tblDetail;
    private javax.swing.JTable tblHeader;
    private javax.swing.JTextField txtAlamat;
    private javax.swing.JTextField txtCatatan;
    private javax.swing.JTextField txtDiskon;
    private javax.swing.JTextField txtDiskonPersen;
    private javax.swing.JTextField txtDokter;
    private javax.swing.JTextField txtNama;
    private javax.swing.JTextField txtNoTrx;
    private javax.swing.JTextField txtNorm;
    private javax.swing.JTextField txtTop;
    // End of variables declaration//GEN-END:variables

    private void insertItem() {
        String item = lookupItem.getKodeBarang();
//        double saldo=itemDao.getSaldo(item, sSiteID);

//        if(saldo >0){
        Barang barang = itemDao.getBarangByKode(item, cmbTipeTarif.getSelectedItem().toString());
        double harga=cmbTipeTarif.getSelectedItem().toString().equalsIgnoreCase("KLINIK")? barang.getHargaKlinik(): barang.getHargaReseller();
        ((DefaultTableModel) tblDetail.getModel()).addRow(new Object[]{
            item,
            barang.getNamaPaten(), //Nama Barang
            barang.getSatuanKecil(), //UOM
            1, //Qty Jual
            harga, //Harga
            0, //Diskon
            harga, //Sub Total
            tblHeader.getValueAt(tblHeader.getSelectedRow(), 1).toString() + "#" + tblHeader.getValueAt(tblHeader.getSelectedRow(), 0).toString(),
            false
        });
        tblDetail.requestFocus();
        if (tblDetail.getRowCount() > 0) {
            tblDetail.setRowSelectionInterval(tblDetail.getRowCount() - 1, tblDetail.getRowCount() - 1);
            tblDetail.changeSelection(tblDetail.getSelectedRow(), tblDetail.getColumnModel().getColumnIndex("Qty Jual"),
                    false, false);

        }
        //tblDetail.setValueAt(lookupItem.getKodeBarang(), tblDetail.getSelectedRow(), 0);
//        }else{
//            JOptionPane.showMessageDialog(this, "Saldo Tidak Cukup!\n"
//                    + "Saldo komputer="+saldo);
//        }
    }

    public void setDiskon(double diskon) {
        
        txtDiskon.setText(fn.intFmt.format(diskon));
        double discPersen=fn.udfGetDouble(txtDiskon.getText())/fn.udfGetDouble(lblTotal.getText())*100;
        if(discPersen!=fn.udfGetDouble(txtDiskonPersen.getText())){
            txtDiskonPersen.setText("0");
        }
        udfSetTotal();
    }

    private void lookupPasien() {
        DLgLookup d1 = new DLgLookup(JOptionPane.getFrameForComponent(aThis), true);
        String sSupplier = "";

        String s = "select * from ("
                + "select norm as kode, coalesce(nama,'') as nama, coalesce(to_char(tgl_lahir, 'dd/MM/yyyy'), '') as tgl_lahir, coalesce(alamat_domisili,'') as alamat \n"
                + "from rm_pasien order by 2) x ";

        //System.out.println(s);
//                    ((DefaultTableModel)tblSupplier.getModel()).setNumRows(tblSupplier.getRowCount()+1);
//                    tblSupplier.setRowSelectionInterval(tblSupplier.getRowCount()-1, tblSupplier.getRowCount()-1);
        d1.setTitle("Cari Pasien");
        d1.udfLoad(conn, s, "(kode||nama)", null);

        d1.setVisible(true);

        //System.out.println("Kode yang dipilih" +d1.getKode());
        if (d1.getKode().length() > 0) {
            TableColumnModel col = d1.getTable().getColumnModel();
            JTable tbl = d1.getTable();
            int iRow = tbl.getSelectedRow();
            txtNorm.setText(tbl.getValueAt(iRow, col.getColumnIndex("kode")).toString());
            txtNama.setText(tbl.getValueAt(iRow, col.getColumnIndex("nama")).toString());
            txtAlamat.setText(tbl.getValueAt(iRow, col.getColumnIndex("alamat")).toString());
        }
    }
}
