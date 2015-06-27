/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*udf
 * FrmGoodReceipt.java
 *
 * Created on Jul 15, 2010, 4:49:38 PM
 */
package pembelian;

import apotek.DlgLookupItemBeli;
import apotek.dao.ItemDao;
import apotek.dao.PembelianDao;
import com.klinik.model.Barang;
import com.klinik.model.SuppBarang;
import main.MainForm;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.FocusTraversalPolicy;
import java.awt.Font;
import java.awt.KeyboardFocusManager;
import java.awt.Toolkit;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.print.PrinterJob;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
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
import javax.swing.SwingUtilities;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableColumnModel;
import javax.swing.text.MaskFormatter;
import main.GeneralFunction;
import main.SysConfig;

/**
 *
 * @author ustadho
 */
public class FrmGoodReceipt extends javax.swing.JInternalFrame {

    GeneralFunction fn;
    private Connection conn;
    TableColumnModel col = null;
    private boolean stMinus = false;
    private ItemDao itemDao = new ItemDao();

    /**
     * Creates new form FrmGoodReceipt
     */
    public FrmGoodReceipt() {
        initComponents();
        tblItem.getTableHeader().setFont(new Font("Tahoma", 0, 12));
        tblItem.getColumn("Keterangan").setPreferredWidth(200);
        tblItem.getColumn("Qty Order").setPreferredWidth(70);
        tblItem.getColumn("Sisa Order").setPreferredWidth(70);
        tblItem.getColumn("Qty Terima").setPreferredWidth(70);
        tblItem.getColumn("UOM").setPreferredWidth(50);
        tblItem.getColumn("Expired").setPreferredWidth(100);
        tblItem.setRowHeight(22);

        tblItem.getColumn("Konv").setMinWidth(0);
        tblItem.getColumn("Konv").setMaxWidth(0);
        tblItem.getColumn("Konv").setPreferredWidth(0);
        tblItem.getColumn("UomKecil").setMinWidth(0);
        tblItem.getColumn("UomKecil").setMaxWidth(0);
        tblItem.getColumn("UomKecil").setPreferredWidth(0);
        tblItem.getColumn("JmlKecil").setMinWidth(0);
        tblItem.getColumn("JmlKecil").setMaxWidth(0);
        tblItem.getColumn("JmlKecil").setPreferredWidth(0);
        tblItem.getColumn("OnHand").setMinWidth(0);
        tblItem.getColumn("OnHand").setMaxWidth(0);
        tblItem.getColumn("OnHand").setPreferredWidth(0);
//        tblPR.getColumn("Harga").setMinWidth(0); tblPR.getColumn("Harga").setMaxWidth(0); tblPR.getColumn("Harga").setPreferredWidth(0);
//        tblPR.getColumn("Disc").setMinWidth(0); tblPR.getColumn("Disc").setMaxWidth(0); tblPR.getColumn("Disc").setPreferredWidth(0);
//        tblPR.getColumn("PPN").setMinWidth(0); tblPR.getColumn("PPN").setMaxWidth(0); tblPR.getColumn("PPN").setPreferredWidth(0);

        col = tblItem.getColumnModel();
        MyTableCellEditor cEditor = new MyTableCellEditor();
        tblItem.getColumnModel().getColumn(col.getColumnIndex("Qty Terima")).setCellEditor(cEditor);
        tblItem.getColumnModel().getColumn(col.getColumnIndex("Expired")).setCellEditor(cEditor);
        tblItem.getColumnModel().getColumn(col.getColumnIndex("Harga")).setCellEditor(cEditor);
        tblItem.getColumnModel().getColumn(col.getColumnIndex("Disc")).setCellEditor(cEditor);
        tblItem.getColumnModel().getColumn(col.getColumnIndex("PPN")).setCellEditor(cEditor);
        tblItem.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            public void valueChanged(ListSelectionEvent e) {
                int iRow = tblItem.getSelectedRow();
                if (iRow >= 0) {
                    txtConv.setText(fn.dFmt.format(fn.udfGetDouble(tblItem.getValueAt(iRow, col.getColumnIndex("Konv")))));
                    lblUomKecil.setText(tblItem.getValueAt(iRow, col.getColumnIndex("UomKecil")).toString());
                    txtStockOnHand.setText(fn.dFmt.format(fn.udfGetDouble(tblItem.getValueAt(iRow, col.getColumnIndex("OnHand")))));
                }
            }
        });

        tblItem.getModel().addTableModelListener(new TableModelListener() {
            public void tableChanged(TableModelEvent e) {
                int iRow = tblItem.getSelectedRow();
                int iCol = e.getColumn();
                TableColumnModel col = tblItem.getColumnModel();

                if (iCol == col.getColumnIndex("Qty Terima") && e.getType() == TableModelEvent.UPDATE) {
                    tblItem.setValueAt(fn.udfGetFloat(tblItem.getValueAt(iRow, col.getColumnIndex("Qty Terima"))) * fn.udfGetFloat(tblItem.getValueAt(iRow, col.getColumnIndex("Konv"))),
                            iRow, col.getColumnIndex("JmlKecil"));
                }
                if (tblItem.getRowCount() > 0) {
                    double totLine = 0, totVat = 0;
                    double extPrice = 0;
                    for (int i = 0; i < tblItem.getRowCount(); i++) {
                        //if(e.getType()==TableModelEvent.DELETE) ((DefaultTableModel)tblItem.getModel()).setValueAt(i+1, i, 0);
                        extPrice = fn.udfGetDouble(tblItem.getValueAt(i, col.getColumnIndex("Qty Terima")))
                                * fn.udfGetDouble(tblItem.getValueAt(i, col.getColumnIndex("Harga")));
                        extPrice = extPrice - (extPrice / 100) * fn.udfGetDouble(tblItem.getValueAt(i, col.getColumnIndex("Disc")));

                        totLine += extPrice;
                        totVat += extPrice / 100 * fn.udfGetDouble(tblItem.getValueAt(i, col.getColumnIndex("PPN")));
                    }

                    txtTotalLine.setText(fn.dFmt.format(Math.floor(totLine)));
                    txtTotVat.setText(fn.dFmt.format(Math.floor(totVat)));
                    txtNetto.setText(fn.dFmt.format(Math.floor(totVat) + Math.floor(totLine)));
                } else {
                    txtTotalLine.setText("0");
                    txtTotVat.setText("0");
                    txtNetto.setText("0");
                }
            }
        });
        tblItem.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).
                put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0),
                        "selectNextColumnCell");
    }

    public void setStatusMinus(boolean b) {
        this.stMinus = b;
    }

    private void setStatusMinus() {
        txtNoGR.setEnabled(stMinus);
        txtNoPO.setEnabled(!stMinus);
        txtNoSJ.setEnabled(!stMinus);
        txtSite.setEnabled(!stMinus);
        txtSupplier.setEnabled(!stMinus);
        jLabel17.setText(stMinus ? "Last TTB#" : "Receipt#");
        jLabel16.setText(stMinus ? "Good Receipt (Minus)" : "Good Receipt");
    }

    public void setConn(Connection con) {
        this.conn = con;
    }

    private void udfSave() {
        if (!udfCekBeforeSave()) {
            return;
        }

        try {
            conn.setAutoCommit(false);
            String sSql = "select fn_phar_get_no_goodreceipt('" + MainForm.sUserID + "') as no_gr";
            String sNoGr = "";
            ResultSet rs = conn.createStatement().executeQuery(sSql);
            if (rs.next()) {
                txtNoGR.setText(rs.getString(1));
                sNoGr = rs.getString(1);
            }
            rs.close();
            sSql = "insert into phar_good_receipt(good_receipt_id, tanggal, no_po, no_inv_do_sj, remarks, site_id, "
                    + "flag_trx, user_ins, kode_supp, source_id, user_terima, user_gudang) values('" + sNoGr + "', "
                    + "now(), " + //
                    "'" + txtNoPO.getText() + "', '" + txtNoSJ.getText() + "', '" + txtRemark.getText() + "', "
                    + "'" + txtSite.getText() + "', 'T', '" + MainForm.sUserName + "', '" + txtSupplier.getText() + "', "
                    + "'" + txtNoGR.getText() + "', "
                    + "'" + txtTTDPenerima.getText() + "', '" + txtTTDGudang.getText() + "'); ";

            String sExpDate = "";
            for (int i = 0; i < tblItem.getRowCount(); i++) {
                sExpDate = (tblItem.getValueAt(i, col.getColumnIndex("Expired")).toString().length() == 0
                        || tblItem.getValueAt(i, col.getColumnIndex("Expired")).toString().equalsIgnoreCase("01/01/00") ? "'1900-01-01'" : "'"
                        + new SimpleDateFormat("yyyy-MM-dd").format(new SimpleDateFormat("dd/MM/yy").parse(tblItem.getValueAt(i, col.getColumnIndex("Expired")).toString())) + "'");

                sSql += "insert into phar_good_receipt_detail(good_receipt_id, kode_barang, jumlah, harga, discount, "
                        + "ppn, expired_date, user_ins, uom_gr, konv, jml_kecil, urut, no_pr"
                        + ") values('" + sNoGr + "',"
                        + "'" + tblItem.getValueAt(i, col.getColumnIndex("Product ID")).toString() + "', "
                        + fn.udfGetDouble(tblItem.getValueAt(i, col.getColumnIndex("Qty Terima"))) + ", "
                        + fn.udfGetDouble(tblItem.getValueAt(i, col.getColumnIndex("Harga"))) + ", "
                        + fn.udfGetDouble(tblItem.getValueAt(i, col.getColumnIndex("Disc"))) + ", "
                        + fn.udfGetDouble(tblItem.getValueAt(i, col.getColumnIndex("PPN"))) + ", "
                        + sExpDate + ","
                        + "'" + MainForm.sUserName + "', '" + tblItem.getValueAt(i, col.getColumnIndex("UOM")).toString() + "',"
                        + "" + fn.udfGetDouble(tblItem.getValueAt(i, col.getColumnIndex("Konv"))) + ","
                        + "" + fn.udfGetDouble(tblItem.getValueAt(i, col.getColumnIndex("Konv"))) * fn.udfGetDouble(tblItem.getValueAt(i, col.getColumnIndex("Qty Terima"))) + ", "
                        + "" + fn.udfGetInt(tblItem.getValueAt(i, col.getColumnIndex("Urut"))) + ","
                        + "'" + tblItem.getValueAt(i, col.getColumnIndex("No. PR")).toString() + "');";
            }

            System.out.println(sSql);

            int i = conn.createStatement().executeUpdate(sSql);
            conn.setAutoCommit(true);
            //JOptionPane.showMessageDialog(this, "Simpan Sukses ");
            printKwitansi(txtNoPO.getText(), false);
            udfNew();

        } catch (ParseException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage());
        } catch (SQLException ex) {

            try {
                conn.rollback();
                conn.setAutoCommit(true);
                JOptionPane.showMessageDialog(this, ex.getMessage());
            } catch (SQLException ex1) {
                Logger.getLogger(FrmGoodReceipt.class.getName()).log(Level.SEVERE, null, ex1);
            }
        } catch (java.lang.ArrayIndexOutOfBoundsException ex) {

        }
    }

    private void printKwitansi(String sNo_PR, Boolean okCpy) {
        try {
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
            if (JOptionPane.showConfirmDialog(null, "Simpan Good Receipt Sukses. Selanjutnya akan di Print!", "SGHS Go Open Source", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE) == JOptionPane.YES_OPTION) {
                PrintGood_receipt pn = new PrintGood_receipt(conn, txtNoGR.getText(), false, MainForm.sUserName, services[i]);
            }
        } catch (java.lang.NullPointerException nu) {
            JOptionPane.showMessageDialog(this, nu.getMessage());
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

        jToolBar1 = new javax.swing.JToolBar();
        btnNew = new javax.swing.JButton();
        btnSave = new javax.swing.JButton();
        btnPrint = new javax.swing.JButton();
        btnCancel = new javax.swing.JButton();
        jLabel16 = new javax.swing.JLabel();
        jPanel1 = new javax.swing.JPanel();
        jLabel18 = new javax.swing.JLabel();
        txtSite = new javax.swing.JTextField();
        jLabel6 = new javax.swing.JLabel();
        txtRemark = new javax.swing.JTextField();
        lblSite = new javax.swing.JLabel();
        jLabel14 = new javax.swing.JLabel();
        lblSupplier = new javax.swing.JLabel();
        txtSupplier = new javax.swing.JTextField();
        jLabel20 = new javax.swing.JLabel();
        jLabel21 = new javax.swing.JLabel();
        jLabel22 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        jLabel23 = new javax.swing.JLabel();
        txtNoSJ = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        txtDate = new javax.swing.JTextField();
        jLabel17 = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        txtReceiptBy = new javax.swing.JTextField();
        jLabel13 = new javax.swing.JLabel();
        jLabel19 = new javax.swing.JLabel();
        txtNoGR = new javax.swing.JTextField();
        jLabel15 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        jLabel27 = new javax.swing.JLabel();
        txtTglPO = new javax.swing.JTextField();
        jLabel10 = new javax.swing.JLabel();
        jLabel28 = new javax.swing.JLabel();
        txtNoPO = new javax.swing.JTextField();
        chkConsignment = new javax.swing.JCheckBox();
        jPanel3 = new javax.swing.JPanel();
        jLabel24 = new javax.swing.JLabel();
        txtStockOnHand = new javax.swing.JLabel();
        jLabel25 = new javax.swing.JLabel();
        txtConv = new javax.swing.JLabel();
        lblUomKecil = new javax.swing.JLabel();
        jSeparator2 = new javax.swing.JSeparator();
        jSeparator3 = new javax.swing.JSeparator();
        jLabel11 = new javax.swing.JLabel();
        txtTTDPenerima = new javax.swing.JTextField();
        jLabel12 = new javax.swing.JLabel();
        txtTTDGudang = new javax.swing.JTextField();
        jLabel1 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        tblItem = new javax.swing.JTable();
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
        setTitle("Good Receipt (Plus)");
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

        btnPrint.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/Icon/buttons/print-32.png"))); // NOI18N
        btnPrint.setText("Print");
        btnPrint.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnPrint.setMargin(new java.awt.Insets(2, 2, 2, 2));
        btnPrint.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnPrint.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnPrintActionPerformed(evt);
            }
        });
        jToolBar1.add(btnPrint);

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
        jLabel16.setText("Good Receipt");

        jPanel1.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        jPanel1.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel18.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jLabel18.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel18.setText("Site ID");
        jPanel1.add(jLabel18, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 85, 90, 20));

        txtSite.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
        txtSite.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 1, true));
        txtSite.setDisabledTextColor(new java.awt.Color(153, 153, 153));
        txtSite.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                txtSiteFocusLost(evt);
            }
        });
        txtSite.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtSiteKeyReleased(evt);
            }
        });
        jPanel1.add(txtSite, new org.netbeans.lib.awtextra.AbsoluteConstraints(110, 85, 60, 20));

        jLabel6.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jLabel6.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel6.setText("Remark");
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

        lblSite.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
        lblSite.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 1, true));
        lblSite.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                lblSitePropertyChange(evt);
            }
        });
        jPanel1.add(lblSite, new org.netbeans.lib.awtextra.AbsoluteConstraints(170, 85, 260, 20));

        jLabel14.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jLabel14.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel14.setText(":");
        jPanel1.add(jLabel14, new org.netbeans.lib.awtextra.AbsoluteConstraints(100, 10, 10, 20));

        lblSupplier.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
        lblSupplier.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 1, true));
        lblSupplier.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                lblSupplierPropertyChange(evt);
            }
        });
        jPanel1.add(lblSupplier, new org.netbeans.lib.awtextra.AbsoluteConstraints(170, 10, 260, 20));

        txtSupplier.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
        txtSupplier.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 1, true));
        txtSupplier.setDisabledTextColor(new java.awt.Color(153, 153, 153));
        txtSupplier.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                txtSupplierFocusLost(evt);
            }
        });
        txtSupplier.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtSupplierKeyReleased(evt);
            }
        });
        jPanel1.add(txtSupplier, new org.netbeans.lib.awtextra.AbsoluteConstraints(110, 10, 60, 20));

        jLabel20.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jLabel20.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel20.setText("Supplier");
        jPanel1.add(jLabel20, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 10, 90, 20));

        jLabel21.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jLabel21.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel21.setText(":");
        jPanel1.add(jLabel21, new org.netbeans.lib.awtextra.AbsoluteConstraints(100, 85, 10, 20));

        jLabel22.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jLabel22.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel22.setText(":");
        jPanel1.add(jLabel22, new org.netbeans.lib.awtextra.AbsoluteConstraints(100, 60, 10, 20));

        jLabel7.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jLabel7.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel7.setText("No. Invoice");
        jPanel1.add(jLabel7, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 35, 90, 20));

        jLabel23.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jLabel23.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel23.setText(":");
        jPanel1.add(jLabel23, new org.netbeans.lib.awtextra.AbsoluteConstraints(100, 35, 10, 20));

        txtNoSJ.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
        txtNoSJ.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 1, true));
        txtNoSJ.setDisabledTextColor(new java.awt.Color(0, 0, 0));
        txtNoSJ.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                txtNoSJFocusLost(evt);
            }
        });
        txtNoSJ.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtNoSJKeyReleased(evt);
            }
        });
        jPanel1.add(txtNoSJ, new org.netbeans.lib.awtextra.AbsoluteConstraints(110, 35, 220, 20));

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
        jLabel17.setText("Receipt #");
        jPanel1.add(jLabel17, new org.netbeans.lib.awtextra.AbsoluteConstraints(530, 10, 80, 20));

        jLabel9.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jLabel9.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel9.setText("Input By");
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

        txtNoGR.setFont(new java.awt.Font("Dialog", 0, 14)); // NOI18N
        txtNoGR.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 1, true));
        txtNoGR.setDisabledTextColor(new java.awt.Color(0, 0, 0));
        txtNoGR.setEnabled(false);
        txtNoGR.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                txtNoGRFocusLost(evt);
            }
        });
        txtNoGR.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                txtNoGRPropertyChange(evt);
            }
        });
        txtNoGR.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtNoGRKeyReleased(evt);
            }
            public void keyTyped(java.awt.event.KeyEvent evt) {
                txtNoGRKeyTyped(evt);
            }
        });
        txtNoGR.addVetoableChangeListener(new java.beans.VetoableChangeListener() {
            public void vetoableChange(java.beans.PropertyChangeEvent evt)throws java.beans.PropertyVetoException {
                txtNoGRVetoableChange(evt);
            }
        });
        jPanel1.add(txtNoGR, new org.netbeans.lib.awtextra.AbsoluteConstraints(620, 10, 120, 20));

        jLabel15.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jLabel15.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel15.setText(":");
        jPanel1.add(jLabel15, new org.netbeans.lib.awtextra.AbsoluteConstraints(610, 10, 10, 20));

        jLabel8.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jLabel8.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel8.setText("Tgl. PO");
        jPanel1.add(jLabel8, new org.netbeans.lib.awtextra.AbsoluteConstraints(530, 90, 80, 20));

        jLabel27.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jLabel27.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel27.setText(":");
        jPanel1.add(jLabel27, new org.netbeans.lib.awtextra.AbsoluteConstraints(610, 90, 10, 20));

        txtTglPO.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
        txtTglPO.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 1, true));
        txtTglPO.setDisabledTextColor(new java.awt.Color(0, 0, 0));
        txtTglPO.setEnabled(false);
        txtTglPO.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                txtTglPOFocusLost(evt);
            }
        });
        txtTglPO.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtTglPOKeyReleased(evt);
            }
        });
        jPanel1.add(txtTglPO, new org.netbeans.lib.awtextra.AbsoluteConstraints(620, 90, 90, 20));

        jLabel10.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jLabel10.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel10.setText("No. PO");
        jPanel1.add(jLabel10, new org.netbeans.lib.awtextra.AbsoluteConstraints(530, 70, 80, 20));

        jLabel28.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jLabel28.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel28.setText(":");
        jPanel1.add(jLabel28, new org.netbeans.lib.awtextra.AbsoluteConstraints(610, 70, 10, 20));

        txtNoPO.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
        txtNoPO.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 1, true));
        txtNoPO.setDisabledTextColor(new java.awt.Color(0, 0, 0));
        txtNoPO.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                txtNoPOFocusLost(evt);
            }
        });
        txtNoPO.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtNoPOKeyReleased(evt);
            }
        });
        jPanel1.add(txtNoPO, new org.netbeans.lib.awtextra.AbsoluteConstraints(620, 70, 120, 20));

        chkConsignment.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        chkConsignment.setText(" Consigment");
        chkConsignment.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                chkConsignmentItemStateChanged(evt);
            }
        });
        chkConsignment.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chkConsignmentActionPerformed(evt);
            }
        });

        jPanel3.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        jPanel3.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel24.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jLabel24.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel24.setText("On Hand :");
        jPanel3.add(jLabel24, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 5, 70, 20));

        txtStockOnHand.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
        txtStockOnHand.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        txtStockOnHand.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 1, true));
        txtStockOnHand.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                txtStockOnHandPropertyChange(evt);
            }
        });
        jPanel3.add(txtStockOnHand, new org.netbeans.lib.awtextra.AbsoluteConstraints(70, 5, 40, 20));

        jLabel25.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jLabel25.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel25.setText(" Conv = ");
        jPanel3.add(jLabel25, new org.netbeans.lib.awtextra.AbsoluteConstraints(120, 5, 50, 20));

        txtConv.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
        txtConv.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        txtConv.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 1, true));
        txtConv.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                txtConvPropertyChange(evt);
            }
        });
        jPanel3.add(txtConv, new org.netbeans.lib.awtextra.AbsoluteConstraints(170, 5, 50, 20));

        lblUomKecil.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        lblUomKecil.setForeground(new java.awt.Color(0, 0, 153));
        lblUomKecil.setText("Uom");
        jPanel3.add(lblUomKecil, new org.netbeans.lib.awtextra.AbsoluteConstraints(220, 5, 40, 20));

        jSeparator2.setOrientation(javax.swing.SwingConstants.VERTICAL);
        jPanel3.add(jSeparator2, new org.netbeans.lib.awtextra.AbsoluteConstraints(118, 0, -1, 30));

        jSeparator3.setOrientation(javax.swing.SwingConstants.VERTICAL);
        jPanel3.add(jSeparator3, new org.netbeans.lib.awtextra.AbsoluteConstraints(260, 0, 10, 30));

        jLabel11.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jLabel11.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel11.setText("Penerima :");
        jPanel3.add(jLabel11, new org.netbeans.lib.awtextra.AbsoluteConstraints(350, 5, 70, 20));

        txtTTDPenerima.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
        txtTTDPenerima.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 1, true));
        txtTTDPenerima.setDisabledTextColor(new java.awt.Color(0, 0, 0));
        txtTTDPenerima.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                txtTTDPenerimaFocusLost(evt);
            }
        });
        txtTTDPenerima.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtTTDPenerimaKeyReleased(evt);
            }
        });
        jPanel3.add(txtTTDPenerima, new org.netbeans.lib.awtextra.AbsoluteConstraints(420, 5, 70, 20));

        jLabel12.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jLabel12.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel12.setText("Gudang :");
        jPanel3.add(jLabel12, new org.netbeans.lib.awtextra.AbsoluteConstraints(520, 5, 60, 20));

        txtTTDGudang.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
        txtTTDGudang.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 1, true));
        txtTTDGudang.setDisabledTextColor(new java.awt.Color(0, 0, 0));
        txtTTDGudang.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                txtTTDGudangFocusLost(evt);
            }
        });
        txtTTDGudang.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtTTDGudangKeyReleased(evt);
            }
        });
        jPanel3.add(txtTTDGudang, new org.netbeans.lib.awtextra.AbsoluteConstraints(580, 5, 80, 20));

        jLabel1.setBackground(new java.awt.Color(204, 255, 255));
        jLabel1.setForeground(new java.awt.Color(0, 0, 153));
        jLabel1.setText("<html>\n &nbsp <b>F5 &nbsp &nbsp &nbsp &nbsp : </b> &nbsp Membuat Good Receipt baru <br> \n &nbsp <b>F2 &nbsp &nbsp &nbsp &nbsp : </b> &nbsp Menyimpan Good Receipt <br>\n &nbsp <b>Ctrl+C : </b> &nbsp Copy Baris Item  &nbsp  &nbsp\n &nbsp <b>Ctrl+V : </b> &nbsp Paste Baris Item <br>\n &nbsp <b>Insert : </b> &nbsp Menambah Item Good Receipt dari PO yang sama<br>\n<hr>\n &nbsp <b>Catatan : </b> &nbsp Format Expired Date adalah 'dd/MM/yy' contoh '31/05/11'<br>\n</html>");
        jLabel1.setVerticalAlignment(javax.swing.SwingConstants.TOP);
        jLabel1.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        jLabel1.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jLabel1.setOpaque(true);
        jLabel1.setVerticalTextPosition(javax.swing.SwingConstants.TOP);

        tblItem.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        tblItem.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Product ID", "Keterangan", "Qty Order", "Sisa Order", "Qty Terima", "UOM", "Expired", "Harga", "Disc", "PPN", "Konv", "UomKecil", "JmlKecil", "OnHand", "Urut", "No. PR"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Object.class, java.lang.Object.class, java.lang.Double.class, java.lang.Double.class, java.lang.Double.class, java.lang.Object.class, java.lang.Object.class, java.lang.Double.class, java.lang.Double.class, java.lang.Double.class, java.lang.Double.class, java.lang.Object.class, java.lang.Double.class, java.lang.Double.class, java.lang.Integer.class, java.lang.Object.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, false, true, false, true, true, true, true, false, false, false, false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        tblItem.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_OFF);
        jScrollPane1.setViewportView(tblItem);

        jPanel2.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        jPanel2.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel31.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jLabel31.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel31.setText("Netto");
        jPanel2.add(jLabel31, new org.netbeans.lib.awtextra.AbsoluteConstraints(180, 50, 80, 20));

        txtTotalLine.setFont(new java.awt.Font("Dialog", 1, 12)); // NOI18N
        txtTotalLine.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        txtTotalLine.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 1, true));
        txtTotalLine.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                txtTotalLinePropertyChange(evt);
            }
        });
        jPanel2.add(txtTotalLine, new org.netbeans.lib.awtextra.AbsoluteConstraints(260, 10, 90, 20));

        jLabel26.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jLabel26.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel26.setText("Line Total :");
        jPanel2.add(jLabel26, new org.netbeans.lib.awtextra.AbsoluteConstraints(180, 10, 80, 20));

        txtTotVat.setFont(new java.awt.Font("Dialog", 1, 12)); // NOI18N
        txtTotVat.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        txtTotVat.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 1, true));
        txtTotVat.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                txtTotVatPropertyChange(evt);
            }
        });
        jPanel2.add(txtTotVat, new org.netbeans.lib.awtextra.AbsoluteConstraints(260, 30, 90, 20));

        txtNetto.setFont(new java.awt.Font("Dialog", 1, 12)); // NOI18N
        txtNetto.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        txtNetto.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 1, true));
        txtNetto.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                txtNettoPropertyChange(evt);
            }
        });
        jPanel2.add(txtNetto, new org.netbeans.lib.awtextra.AbsoluteConstraints(260, 50, 90, 20));

        jLabel30.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jLabel30.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel30.setText("V.A.T");
        jPanel2.add(jLabel30, new org.netbeans.lib.awtextra.AbsoluteConstraints(180, 30, 80, 20));

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(10, 10, 10)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel16, javax.swing.GroupLayout.PREFERRED_SIZE, 400, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(10, 10, 10)
                        .addComponent(chkConsignment, javax.swing.GroupLayout.PREFERRED_SIZE, 110, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(38, 38, 38)
                        .addComponent(jToolBar1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, 749, Short.MAX_VALUE)
                            .addComponent(jScrollPane1)
                            .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 390, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(0, 0, 0)
                                .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                        .addGap(6, 6, 6))))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(10, 10, 10)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel16, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(30, 30, 30)
                        .addComponent(chkConsignment, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jToolBar1, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(3, 3, 3)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 208, Short.MAX_VALUE)
                .addGap(7, 7, 7)
                .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 90, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, 90, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(3, 3, 3))
        );

        setBounds(0, 0, 776, 558);
    }// </editor-fold>//GEN-END:initComponents

    private void btnNewActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnNewActionPerformed
        udfNew();
}//GEN-LAST:event_btnNewActionPerformed

    private void btnSaveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSaveActionPerformed
        udfSave();
}//GEN-LAST:event_btnSaveActionPerformed

    private void btnCancelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCancelActionPerformed
        if (btnCancel.getText().equalsIgnoreCase("cancel")) {
            btnSave.setEnabled(false);
            btnNew.setEnabled(true);
            btnCancel.setText("Exit");
//            btnCancel.setIcon(new javax.swing.ImageIcon(getClass().getResource("/pharpurchase/image/Icon/Exit.png")));
        } else {
            this.dispose();
        }
}//GEN-LAST:event_btnCancelActionPerformed

    private void chkConsignmentItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_chkConsignmentItemStateChanged

}//GEN-LAST:event_chkConsignmentItemStateChanged

    private void txtDateFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtDateFocusLost
        // TODO add your handling code here:
}//GEN-LAST:event_txtDateFocusLost

    private void txtDateKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtDateKeyReleased
        // TODO add your handling code here:
}//GEN-LAST:event_txtDateKeyReleased

    private void txtSiteFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtSiteFocusLost
        // TODO add your handling code here:
}//GEN-LAST:event_txtSiteFocusLost

    private void txtSiteKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtSiteKeyReleased
        fn.lookup(evt, new Object[]{lblSite},
                "select site_id, coalesce(site_name,'') as site_name from phar_site "
                + "where upper(site_id||coalesce(site_name,'')) Like upper('%" + txtSite.getText() + "%') order by 2",
                txtSite.getWidth() + lblSite.getWidth(), 300);
}//GEN-LAST:event_txtSiteKeyReleased

    private void txtRemarkFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtRemarkFocusLost
        // TODO add your handling code here:
}//GEN-LAST:event_txtRemarkFocusLost

    private void txtRemarkKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtRemarkKeyReleased
        // TODO add your handling code here:
}//GEN-LAST:event_txtRemarkKeyReleased

    private void txtReceiptByFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtReceiptByFocusLost
        // TODO add your handling code here:
}//GEN-LAST:event_txtReceiptByFocusLost

    private void txtReceiptByKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtReceiptByKeyReleased
        // TODO add your handling code here:
}//GEN-LAST:event_txtReceiptByKeyReleased

    private void lblSitePropertyChange(java.beans.PropertyChangeEvent evt) {//GEN-FIRST:event_lblSitePropertyChange
        // TODO add your handling code here:
}//GEN-LAST:event_lblSitePropertyChange

    private void txtNoGRFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtNoGRFocusLost
        // TODO add your handling code here:
}//GEN-LAST:event_txtNoGRFocusLost

    private void txtNoGRPropertyChange(java.beans.PropertyChangeEvent evt) {//GEN-FIRST:event_txtNoGRPropertyChange
        btnPrint.setEnabled(txtNoGR.getText().length() > 0);
}//GEN-LAST:event_txtNoGRPropertyChange

    private void txtNoGRKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtNoGRKeyReleased
        // TODO add your handling code here:
}//GEN-LAST:event_txtNoGRKeyReleased

    private void txtNoGRKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtNoGRKeyTyped
        // TODO add your handling code here:
}//GEN-LAST:event_txtNoGRKeyTyped

    private void txtNoGRVetoableChange(java.beans.PropertyChangeEvent evt)throws java.beans.PropertyVetoException {//GEN-FIRST:event_txtNoGRVetoableChange
        // TODO add your handling code here:
}//GEN-LAST:event_txtNoGRVetoableChange

    private void chkConsignmentActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chkConsignmentActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_chkConsignmentActionPerformed

    private void lblSupplierPropertyChange(java.beans.PropertyChangeEvent evt) {//GEN-FIRST:event_lblSupplierPropertyChange
        // TODO add your handling code here:
    }//GEN-LAST:event_lblSupplierPropertyChange

    private void txtSupplierFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtSupplierFocusLost
        // TODO add your handling code here:
    }//GEN-LAST:event_txtSupplierFocusLost

    private void txtSupplierKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtSupplierKeyReleased
//        String sQry="select distinct po.kode_supplier, coalesce(sp.nama_supplier,'') as nama_supplier, coalesce(sp.telp,'') as telp," +
//                "coalesce(sp.top,0) as top " +
//                "from phar_supplier sp " +
//                "inner join phar_po po on po.kode_supplier=sp.kode_supplier and po.closed<>true " +
//                "where po.kode_supplier ||coalesce(sp.nama_supplier,'') ||coalesce(sp.telp,'') ilike '%"+txtSupplier.getText()+"%' order by 2";

        String sQry = "select sp.kode_supplier, coalesce(sp.nama_supplier,'') as nama_supplier, coalesce(sp.telp,'') as telp,"
                + "coalesce(sp.top,0) as top "
                + "from phar_supplier sp "
                + "where sp.kode_supplier ||coalesce(sp.nama_supplier,'') ||coalesce(sp.telp,'') ilike '%" + txtSupplier.getText() + "%' order by 2";

        fn.lookup(evt, new Object[]{lblSupplier},
                sQry,
                txtSupplier.getWidth() + lblSupplier.getWidth(), 200);
    }//GEN-LAST:event_txtSupplierKeyReleased

    private void txtNoSJFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtNoSJFocusLost
        // TODO add your handling code here:
    }//GEN-LAST:event_txtNoSJFocusLost

    private void txtNoSJKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtNoSJKeyReleased
        // TODO add your handling code here:
    }//GEN-LAST:event_txtNoSJKeyReleased

    private void txtStockOnHandPropertyChange(java.beans.PropertyChangeEvent evt) {//GEN-FIRST:event_txtStockOnHandPropertyChange
        // TODO add your handling code here:
}//GEN-LAST:event_txtStockOnHandPropertyChange

    private void txtConvPropertyChange(java.beans.PropertyChangeEvent evt) {//GEN-FIRST:event_txtConvPropertyChange
        // TODO add your handling code here:
}//GEN-LAST:event_txtConvPropertyChange

    private void formInternalFrameOpened(javax.swing.event.InternalFrameEvent evt) {//GEN-FIRST:event_formInternalFrameOpened
        udfInitForm();
    }//GEN-LAST:event_formInternalFrameOpened

    private void txtTglPOFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtTglPOFocusLost
        // TODO add your handling code here:
    }//GEN-LAST:event_txtTglPOFocusLost

    private void txtTglPOKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtTglPOKeyReleased
        // TODO add your handling code here:
    }//GEN-LAST:event_txtTglPOKeyReleased

    private void txtNoPOFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtNoPOFocusLost
        // TODO add your handling code here:
    }//GEN-LAST:event_txtNoPOFocusLost

    private void txtNoPOKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtNoPOKeyReleased
        fn.lookup(evt, new Object[]{null},
                "select * from fn_phar_lookup_no_po_supplier('" + txtSupplier.getText() + "','%" + txtNoPO.getText() + "%') "
                + "as (\"No PO\" varchar)",
                txtNoPO.getWidth(), 150);
    }//GEN-LAST:event_txtNoPOKeyReleased

    private void formInternalFrameClosed(javax.swing.event.InternalFrameEvent evt) {//GEN-FIRST:event_formInternalFrameClosed
        fn.setVisibleList(false);
    }//GEN-LAST:event_formInternalFrameClosed

    private void btnPrintActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnPrintActionPerformed
        printKwitansi(txtNoGR.getText(), false);
}//GEN-LAST:event_btnPrintActionPerformed

    private void txtTotalLinePropertyChange(java.beans.PropertyChangeEvent evt) {//GEN-FIRST:event_txtTotalLinePropertyChange
        // TODO add your handling code here:
}//GEN-LAST:event_txtTotalLinePropertyChange

    private void txtTotVatPropertyChange(java.beans.PropertyChangeEvent evt) {//GEN-FIRST:event_txtTotVatPropertyChange
        // TODO add your handling code here:
}//GEN-LAST:event_txtTotVatPropertyChange

    private void txtNettoPropertyChange(java.beans.PropertyChangeEvent evt) {//GEN-FIRST:event_txtNettoPropertyChange
        // TODO add your handling code here:
}//GEN-LAST:event_txtNettoPropertyChange

    private void txtTTDPenerimaFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtTTDPenerimaFocusLost
        // TODO add your handling code here:
    }//GEN-LAST:event_txtTTDPenerimaFocusLost

    private void txtTTDPenerimaKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtTTDPenerimaKeyReleased
        fn.lookup(evt, null, "select coalesce(user_name,'') as penerima from user_detail "
                + "where user_name ilike '%" + txtTTDPenerima.getText() + "%'", txtTTDPenerima.getWidth() + 18, 100);
    }//GEN-LAST:event_txtTTDPenerimaKeyReleased

    private void txtTTDGudangFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtTTDGudangFocusLost
        // TODO add your handling code here:
    }//GEN-LAST:event_txtTTDGudangFocusLost

    private void txtTTDGudangKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtTTDGudangKeyReleased
        fn.lookup(evt, null, "select coalesce(user_name,'') as penerima from user_detail "
                + "where user_name ilike '%" + txtTTDGudang.getText() + "%'", txtTTDGudang.getWidth() + 18, 100);
    }//GEN-LAST:event_txtTTDGudangKeyReleased

    private void udfNew() {
        txtSupplier.setText("");
        lblSupplier.setText("");
        txtNoPO.setText("");
        txtNoSJ.setText("");
        txtRemark.setText("");
        txtTglPO.setText("");
        txtStockOnHand.setText("");
        txtConv.setText("");
        lblUomKecil.setText("");

        ((DefaultTableModel) tblItem.getModel()).setNumRows(0);

        btnNew.setEnabled(false);
        btnSave.setEnabled(true);
        chkConsignment.setSelected(false);
        btnCancel.setText("Cancel");
//        btnCancel.setIcon(new javax.swing.ImageIcon(getClass().getResource("/pharpurchase/image/Icon/Cancel.png")));
        //txtSite.setText(""); lblSite.setText("");
        //txtSupplier.requestFocus();

    }


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnCancel;
    private javax.swing.JButton btnNew;
    private javax.swing.JButton btnPrint;
    private javax.swing.JButton btnSave;
    private javax.swing.JCheckBox chkConsignment;
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
    private javax.swing.JLabel jLabel20;
    private javax.swing.JLabel jLabel21;
    private javax.swing.JLabel jLabel22;
    private javax.swing.JLabel jLabel23;
    private javax.swing.JLabel jLabel24;
    private javax.swing.JLabel jLabel25;
    private javax.swing.JLabel jLabel26;
    private javax.swing.JLabel jLabel27;
    private javax.swing.JLabel jLabel28;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel30;
    private javax.swing.JLabel jLabel31;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JSeparator jSeparator2;
    private javax.swing.JSeparator jSeparator3;
    private javax.swing.JToolBar jToolBar1;
    private javax.swing.JLabel lblSite;
    private javax.swing.JLabel lblSupplier;
    private javax.swing.JLabel lblUomKecil;
    private javax.swing.JTable tblItem;
    private javax.swing.JLabel txtConv;
    private javax.swing.JTextField txtDate;
    private javax.swing.JLabel txtNetto;
    private javax.swing.JTextField txtNoGR;
    private javax.swing.JTextField txtNoPO;
    private javax.swing.JTextField txtNoSJ;
    private javax.swing.JTextField txtReceiptBy;
    private javax.swing.JTextField txtRemark;
    private javax.swing.JTextField txtSite;
    private javax.swing.JLabel txtStockOnHand;
    private javax.swing.JTextField txtSupplier;
    private javax.swing.JTextField txtTTDGudang;
    private javax.swing.JTextField txtTTDPenerima;
    private javax.swing.JTextField txtTglPO;
    private javax.swing.JLabel txtTotVat;
    private javax.swing.JLabel txtTotalLine;
    // End of variables declaration//GEN-END:variables
    MyKeyListener kListener = new MyKeyListener();

    private void udfClearPO() {
        txtTglPO.setText("");
        ((DefaultTableModel) tblItem.getModel()).setNumRows(0);

    }

    private void udfLoadItemFromPO() {
        if (txtNoPO.getText().trim().length() == 0) {
            udfClearPO();
            return;
        }

        try {
            String s = "select * from fn_phar_gr_item_sisa_po3('" + txtNoPO.getText() + "') as (no_po varchar, kode_barang varchar,"
                    + "nama_barang varchar, qty_order numeric, sisa numeric, on_receipt numeric, uom_po varchar, "
                    + "harga numeric, disc double precision, ppn real, konv real, uom_kecil varchar, "
                    + "jml_kecil double precision, on_hand numeric, urut int, no_pr varchar) "
                    + "";
            ResultSet rs = conn.createStatement().executeQuery(s);
            ((DefaultTableModel) tblItem.getModel()).setNumRows(0);
            while (rs.next()) {
                ((DefaultTableModel) tblItem.getModel()).addRow(new Object[]{
                    rs.getString("kode_barang"),
                    rs.getString("nama_barang"),
                    rs.getDouble("qty_order"),
                    rs.getDouble("sisa"),
                    rs.getDouble("on_receipt"),
                    rs.getString("uom_po"),
                    "",
                    rs.getDouble("harga"),
                    rs.getDouble("disc"),
                    rs.getDouble("ppn"),
                    rs.getDouble("konv"),
                    rs.getString("uom_kecil"),
                    rs.getDouble("jml_kecil"),
                    rs.getDouble("on_hand"),
                    rs.getInt("urut"),
                    rs.getString("no_pr"),});
            }
            tblItem.setModel((DefaultTableModel) fn.autoResizeColWidth(tblItem, (DefaultTableModel) tblItem.getModel()).getModel());
            if (tblItem.getRowCount() > 0) {
                tblItem.changeSelection(0, 4, false, false);
            } else {
                JOptionPane.showMessageDialog(this, "Item Sisa PO tidak ditemukan! Silakan cek No. PO anda!");
                //txtNoPO.requestFocus();
                return;
            }
            rs.close();
        } catch (SQLException se) {
            JOptionPane.showMessageDialog(this, se.getMessage());
        }
    }

    private void udfLoadItemFromSB() {
        DlgLookupItemBeli lookupItem = new DlgLookupItemBeli(JOptionPane.getFrameForComponent(this), true);
        lookupItem.setAlwaysOnTop(true);
        //lookupItem.setSrcTable(table, table.getColumnModel().getColumnIndex("Qty"));
//        lookupItem.setKeyEvent(KeyEvent.VK_INSERT);
        lookupItem.setConn(conn);
        lookupItem.setObjForm(this);
        lookupItem.setVisible(true);
        lookupItem.clearText();
        lookupItem.requestFocusInWindow();

        String kodeBarang = lookupItem.getKodeBarang();
        if (kodeBarang.length() > 0) {
            TableColumnModel col = tblItem.getColumnModel();

            Barang i = itemDao.getBarangByKode(lookupItem.getKodeBarang(), "KLINIK");
            if (i != null) {

                SuppBarang sb = itemDao.getSuppBarang(kodeBarang, txtSupplier.getText());
                if (sb == null) {
                    JOptionPane.showMessageDialog(this, "Silahkan setting master harga supplier terlebih dulu!");
                    return;
                }
                ((DefaultTableModel)tblItem.getModel()).addRow(new Object[]{
                    kodeBarang, 
                    i.getNamaPaten(), 
                    0, 
                    0,
                    1, 
                    sb.getUomAlt(), 
                    "", 
                    sb.getPrice(), 
                    sb.getDisc(), 
                    sb.getVat(), 
                    sb.getConvertion(), 
                    i.getSatuanKecil(), 
                    sb.getConvertion(), 
                    0, 
                    tblItem.getRowCount(), 
                    ""
                });
                
                tblItem.requestFocusInWindow();
                tblItem.changeSelection(tblItem.getRowCount()-1, col.getColumnIndex("Qty Terima"), false, false);
            }
        }
    }

    private boolean udfCekBeforeSave() {
        if (txtSupplier.getText().length() == 0) {
            JOptionPane.showMessageDialog(this, "Silakan masukkan supplier terlebih dulu~");
            txtSupplier.requestFocus();
            return false;
        }
        if (txtSite.getText().length() == 0) {
            JOptionPane.showMessageDialog(this, "Silakan masukkan Site terlebih dulu~");
            txtSite.requestFocus();
            return false;
        }
        if (tblItem.getRowCount() == 0) {
            JOptionPane.showMessageDialog(this, "Item yang diterima masih kosong~");
            tblItem.requestFocus();
            return false;
        }
        if (txtNoSJ.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Masukkan nomor Invoice terlebih dulu");
            txtNoSJ.requestFocus();
            return false;
        }
        if (txtTTDPenerima.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Masukkan user penerima terlebih dulu!");
            txtTTDPenerima.requestFocus();
            return false;
        }
        if (txtTTDGudang.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Masukkan user gudang terlebih dulu!");
            txtTTDGudang.requestFocus();
            return false;
        }
        if (!stMinus) {
            try {
                ResultSet rs = conn.createStatement().executeQuery(
                        "select no_inv_do_sj from phar_good_receipt where no_inv_do_sj='" + txtNoSJ.getText() + "'; ");
                if (rs.next()) {
                    JOptionPane.showMessageDialog(this, "Delivery No. sudah pernah dimasukkan!");
                    txtNoSJ.requestFocus();
                }
                rs.close();

            } catch (SQLException se) {
                JOptionPane.showMessageDialog(this, se.getMessage());
            }
        }
        for (int i = 0; i < tblItem.getRowCount(); i++) {
            if (tblItem.getValueAt(i, col.getColumnIndex("Expired")).toString().equalsIgnoreCase("")) {
                //if(JOptionPane.showConfirmDialog(this, "Expired Date pada baris ke : "+(i+1)+" masih kosong.\nAkan dilanjutkan", "Confirm", JOptionPane.YES_NO_OPTION)==JOptionPane.NO_OPTION){
                JOptionPane.showMessageDialog(this, "Expired Date pada baris ke : " + (i + 1) + " masih kosong!", "Information", JOptionPane.INFORMATION_MESSAGE);
                tblItem.grabFocus();
                tblItem.changeSelection(i, col.getColumnIndex("Expired"), false, false);
                return false;
                //}
            }

        }
        return true;
    }

    private void udfInitForm() {
        if(SysConfig.versi.equalsIgnoreCase("DEMO") && new PembelianDao().jmlRecord()>=40){
            JOptionPane.showMessageDialog(this, "Versi demo hanya dibatasi maksimal 40 baris.\n"
                    + "Jika anda tertarik, silahkan kontak kami untuk pembelian software ini!");
            this.dispose();
            return;
        }
        fn = new GeneralFunction(conn);
        fn.addKeyListenerInContainer(jToolBar1, kListener, txtFocusListener);
        fn.addKeyListenerInContainer(jPanel1, kListener, txtFocusListener);
        fn.addKeyListenerInContainer(jPanel3, kListener, txtFocusListener);

        tblItem.addKeyListener(kListener);
        try {
            ResultSet rs = conn.createStatement().executeQuery("select to_char(current_date, 'dd/MM/yyyy')");
            rs.next();
            txtDate.setText(rs.getString(1));

            rs.close();
        } catch (SQLException se) {
            JOptionPane.showMessageDialog(this, se.getMessage());
        }

        udfNew();
        txtSite.setText(MainForm.sKodeGudang);
        lblSite.setText(MainForm.sNamaGudang);

        setStatusMinus();
        Runnable doRun = new Runnable() {
            public void run() {
                if (!stMinus) {
                    txtSupplier.requestFocusInWindow();
                } else {
                    txtNoGR.requestFocusInWindow();
                }
            }
        };
        SwingUtilities.invokeLater(doRun);
        aThis = this;
    }

    private void udfLoadGR() {
        if (txtNoGR.getText().trim().isEmpty()) {
            udfNew();
            return;
        }

        String s = "select coalesce(no_inv_do_sj,'') as no_sj, gr.kode_supp, coalesce(s.nama_supplier,'') as nama_supplier,"
                + "coalesce(gr.site_id,'') as site_id, coalesce(st.site_name,'') as site_name,"
                + "coalesce(gr.no_po,'') as no_po, to_char(po.tanggal, 'dd/MM/yyyy') as tgl_po "
                + "from phar_good_receipt gr "
                + "left join phar_good_receipt_detail grd on grd.good_receipt_id=gr.good_receipt_id "
                + "left join phar_supplier s on s.kode_supplier=gr.kode_supp "
                + "left join phar_site st on st.site_id=gr.site_id "
                + "left join phar_po po on po.no_po=gr.no_po where "
                + "gr.good_receipt_id='" + txtNoGR.getText() + "' ";

        try {
            ResultSet rs = conn.createStatement().executeQuery(s);
            if (rs.next()) {
                txtSupplier.setText(rs.getString("kode_supp"));
                lblSupplier.setText(rs.getString("nama_supplier"));
                txtSite.setText(rs.getString("site_id"));
                lblSite.setText(rs.getString("site_name"));
                txtNoPO.setText(rs.getString("no_po"));
                txtTglPO.setText(rs.getString("tgl_po"));
                txtNoSJ.setText(rs.getString("no_sj"));

                rs.close();
                s = "select * from fn_phar_gr_item_detail('" + txtNoPO.getText() + "') as "
                        + "(no_po varchar, kode_barang varchar,"
                        + "nama_barang varchar, qty_order numeric, sisa numeric, on_receipt numeric, uom_po varchar, "
                        + "harga numeric, disc double precision, ppn real, konv real, uom_kecil varchar, "
                        + "jml_kecil double precision, on_hand numeric, exp_date text, urut smallint, no_pr varchar)";

                rs = conn.createStatement().executeQuery(s);
                ((DefaultTableModel) tblItem.getModel()).setNumRows(0);
                while (rs.next()) {
                    ((DefaultTableModel) tblItem.getModel()).addRow(new Object[]{
                        rs.getString("kode_barang"),
                        rs.getString("nama_barang"),
                        rs.getDouble("qty_order"),
                        rs.getDouble("sisa"),
                        rs.getDouble("on_receipt"),
                        rs.getString("uom_po"),
                        rs.getString("exp_date"),
                        rs.getDouble("harga"),
                        rs.getDouble("disc"),
                        rs.getDouble("ppn"),
                        rs.getDouble("konv"),
                        rs.getString("uom_kecil"),
                        rs.getDouble("on_receipt") * rs.getDouble("konv"), //rs.getDouble("jml_kecil"),
                        rs.getDouble("on_hand"),
                        rs.getInt("urut"),
                        rs.getString("no_pr")
                    });
                }

                tblItem.setModel((DefaultTableModel) fn.autoResizeColWidth(tblItem, (DefaultTableModel) tblItem.getModel()).getModel());
                if (tblItem.getRowCount() > 0) {
                    tblItem.changeSelection(0, 4, false, false);
                }
            } else {
                JOptionPane.showMessageDialog(this, "No. Good Receipt tidak ditemukan!");
                udfNew();
                //txtNoGR.grabFocus();
            }
            rs.close();
        } catch (SQLException se) {
            JOptionPane.showMessageDialog(this, se.getMessage());
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
            if (e.getSource().getClass().getSimpleName().equalsIgnoreCase("JTextField")
                    || e.getSource().getClass().getSimpleName().equalsIgnoreCase("JFormattedTextField")) {
                ((JTextField) e.getSource()).setBackground(Color.WHITE);

                if (e.getSource().equals(txtNoPO) && !fn.isListVisible()) {
                    udfLoadItemFromPO();
                } else if (e.getSource().equals(txtNoGR)) {
                    udfLoadGR();
                }
            }
        }

    };

    private void copyTable() {
        // Get all the table data
        Vector data = ((DefaultTableModel) tblItem.getModel()).getDataVector();
        // Copy the second row
        tableCopyRow = (Vector) data.elementAt(tblItem.getSelectedRow());
        //row = (Vector) row.clone();
    }

    private void pasteTable() {
        ((DefaultTableModel) tblItem.getModel()).insertRow(tblItem.getSelectedRow(), tableCopyRow);
    }

    public class MyKeyListener extends KeyAdapter {

        @Override
        public void keyTyped(java.awt.event.KeyEvent evt) {
//          if(evt.getSource().equals(tblPR)){
//              if(tblPR.getSelectedColumn()!=tblPR.getColumnModel().getColumnIndex("Qty Terima") && stMinus){
//                  evt.consume();
//                  return;
//              }
//          }
            if (evt.getSource() instanceof JTextField
                    && ((JTextField) evt.getSource()).getName() != null
                    && ((JTextField) evt.getSource()).getName().equalsIgnoreCase("textEditor") && stMinus) {
                ((JTextField) evt.getSource()).setEditable(tblItem.getSelectedColumn() == tblItem.getColumnModel().getColumnIndex("Qty Terima"));
            }

            if (evt.getSource() instanceof JTextField
                    && ((JTextField) evt.getSource()).getName() != null
                    && ((JTextField) evt.getSource()).getName().equalsIgnoreCase("textEditor")
                    && !(tblItem.getSelectedColumn() == tblItem.getColumnModel().getColumnIndex("Expired"))) {

//              if((tblPR.getSelectedColumn()!=tblPR.getColumnModel().getColumnIndex("Qty Terima"))){
//                  if(stMinus){
//                      evt.consume();
//                      return;
//                  }
//              }
//              char c = evt.getKeyChar();
//              if (!((c >= '0' && c <= '9')) &&
//                    (c != KeyEvent.VK_BACK_SPACE) &&
//                    (c != KeyEvent.VK_DELETE) &&
//                    (c != KeyEvent.VK_ENTER) &&
//                    (c != '-') && (c != '.')) {
//                    getToolkit().beep();
//                    evt.consume();
//                    return;
//              }
                fn.keyTyped(evt);
            }

        }

        @Override
        public void keyPressed(KeyEvent evt) {
            Component ct = KeyboardFocusManager.getCurrentKeyboardFocusManager().getPermanentFocusOwner();
            int keyKode = evt.getKeyCode();
            switch (keyKode) {
                case KeyEvent.VK_ENTER: {
                    if (!(ct instanceof JTable)) {
                        if (!fn.isListVisible()) {
                            Component c = findNextFocus();
                            if (c == null) {
                                return;
                            }
                            c.requestFocus();
                        } else {
                            fn.lstRequestFocus();
                        }
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
                            c.requestFocus();
                        } else {
                            fn.lstRequestFocus();
                        }
                        break;
                    }
                }

                case KeyEvent.VK_UP: {
                    if (!(evt.getSource() instanceof JTable)) {
                        Component c = findPrevFocus();
                        c.requestFocus();
                    }
                    break;
                }
                case KeyEvent.VK_ESCAPE: {
                    if (JOptionPane.showConfirmDialog(null, "Anda Yakin Untuk Keluar?",
                            "SHS Pharmacy", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
                        dispose();
                    }
                    break;
                }
                case KeyEvent.VK_F2: {
                    udfSave();
                    break;
                }
                case KeyEvent.VK_DELETE: {
                    if (evt.getSource().equals(tblItem) && tblItem.getSelectedRow() >= 0) {
                        int iRow[] = tblItem.getSelectedRows();
                        int rowPalingAtas = iRow[0];

//                        if(JOptionPane.showConfirmDialog(FrmPO.this,
//                                "Item '"+tblPR.getValueAt(iRow, 3).toString()+"' dihapus dari PO?", "Confirm", JOptionPane.YES_NO_OPTION)==JOptionPane.NO_OPTION)
//                            return;
                        for (int a = 0; a < iRow.length; a++) {
                            ((DefaultTableModel) tblItem.getModel()).removeRow(tblItem.getSelectedRow());
                        }

                        if (tblItem.getRowCount() > 0 && rowPalingAtas < tblItem.getRowCount()) {
                            //if(tblPR.getSelectedRow()>0)
                            tblItem.setRowSelectionInterval(rowPalingAtas, rowPalingAtas);
                        } else {
                            if (tblItem.getRowCount() > 0) {
                                tblItem.setRowSelectionInterval(rowPalingAtas - 1, rowPalingAtas - 1);
                            } else {
                                txtSupplier.requestFocus();
                            }

                        }
                        if (tblItem.getSelectedRow() >= 0) {
                            tblItem.changeSelection(tblItem.getSelectedRow(), 0, false, false);
                        }
                    }
                    break;

                }
                case KeyEvent.VK_INSERT: {
                    tblItem.requestFocusInWindow();
                    if (txtSupplier.getText().trim().equalsIgnoreCase("")) {
                        JOptionPane.showMessageDialog(aThis, "Masukkan nama supplier terlebih dulu!");
                        txtSupplier.requestFocusInWindow();
                        return;
                    }
                    if (txtNoPO.getText().equalsIgnoreCase("")) {
                        udfLoadItemFromSB();
                    } else {
                        udfLoadItemFromPO();
                    }

                    break;
                }

            }
        }

        @Override
        public void keyReleased(KeyEvent evt) {
            if (evt.getSource().equals(tblItem) && tblItem.getSelectedRow() >= 0) {
                if (evt.getKeyCode() == java.awt.event.KeyEvent.VK_C && evt.getModifiersEx() == java.awt.event.KeyEvent.CTRL_DOWN_MASK) {
                    if (fn.udfGetInt(tblItem.getValueAt(tblItem.getSelectedRow(), tblItem.getColumnModel().getColumnIndex("Qty Terima"))) <= 1) {
                        JOptionPane.showMessageDialog(FrmGoodReceipt.this, "Qty on Receipt <= 1 baris tidak bisa dicopy!");
                        tableCopyRow = null;
                        return;
                    }
                    copyTable();
                }
                if (evt.getKeyCode() == java.awt.event.KeyEvent.VK_V && evt.getModifiersEx() == java.awt.event.KeyEvent.CTRL_DOWN_MASK) {
                    if (tableCopyRow == null) {
                        JOptionPane.showMessageDialog(FrmGoodReceipt.this, "Tabel item belum di-copy! Klik pada item kemudian tekan Ctrl+C terlebih dulu!");
                        return;
                    }
                    pasteTable();
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
            Logger.getLogger(FrmGoodReceipt.class.getName()).log(Level.SEVERE, null, ex);
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

    private Component aThis;

    public class MyTableCellEditor extends AbstractCellEditor implements TableCellEditor {

        private Toolkit toolkit;
        JTextField text = ustTextField;

        JFormattedTextField fText = getFormattedText();

        int col, row;

        public Component getTableCellEditorComponent(JTable table, Object value,
                boolean isSelected, int rowIndex, int vColIndex) {
            // 'value' is value contained in the cell located at (rowIndex, vColIndex)
            row = rowIndex;
            col = vColIndex;
            if (vColIndex == tblItem.getColumnModel().getColumnIndex("Expired")) {
                text = fText;
            } else {
                text = ustTextField;
            }

            text.setName("textEditor");

            if (stMinus) {
                text.setEditable(vColIndex == tblItem.getColumnModel().getColumnIndex("Qty Terima"));
            }

            if (vColIndex == tblItem.getColumnModel().getColumnIndex("Qty Terima")
                    || vColIndex == tblItem.getColumnModel().getColumnIndex("Harga")
                    || vColIndex == tblItem.getColumnModel().getColumnIndex("Disc")
                    || vColIndex == tblItem.getColumnModel().getColumnIndex("PPN")) {
                text.addKeyListener(kListener);
            } else {
                text.removeKeyListener(kListener);
            }

            //col=vColIndex;
            text.setBackground(new Color(0, 255, 204));
            text.addFocusListener(txtFocusListener);
            //text.addKeyListener(kListener);
            text.setFont(table.getFont());
           //text.setName("textEditor");

            text.setText(value == null ? "" : value.toString());
            //component.setText(df.format(value));

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
                if (col == tblItem.getColumnModel().getColumnIndex("Expired")) {
                    if (!fn.validateDate(((JTextField) text).getText(), true, "dd/MM/yy")) {
                        JOptionPane.showMessageDialog(JOptionPane.getFrameForComponent(FrmGoodReceipt.this),
                                "Silakan isikan format tanggal dengan 'dd/MM/yy'\n"
                                + "Contoh: 31/12/19");

                        retVal = tblItem.getValueAt(row, col).toString();
                        tblItem.requestFocusInWindow();
                        tblItem.changeSelection(row, tblItem.getColumnModel().getColumnIndex("Expired"), false, false);
                    } else if (((JTextField) text).getText().equalsIgnoreCase("01/01/00")
                            && tblItem.getValueAt(row, tblItem.getColumnModel().getColumnIndex("Jenis")).toString().equalsIgnoreCase("OBAT")) {
                        JOptionPane.showMessageDialog(aThis, "Untuk jenis item Obat masukkan tanggal expired lebih besar dari '01/01/00'!");
                        o = tblItem.getValueAt(row, tblItem.getColumnModel().getColumnIndex("Expired")).toString();
                        tblItem.requestFocusInWindow();
                        tblItem.changeSelection(row, tblItem.getColumnModel().getColumnIndex("Expired"), false, false);
                        return o;
                    } else {
                        retVal = ((JTextField) text).getText();
                    }

                } else if (col == tblItem.getColumnModel().getColumnIndex("Qty Terima") && txtNoPO.getText().length() > 0) {
                    double sisaPR = fn.udfGetDouble(tblItem.getValueAt(row, tblItem.getColumnModel().getColumnIndex("Sisa Order")));

                    if (fn.udfGetDouble(((JTextField) text).getText()) > sisaPR) {
                        JOptionPane.showMessageDialog(FrmGoodReceipt.this, "Jumlah On Receipt melebihi Qty Sisa PO \nQuantity PO adalah : "
                                + fn.dFmt.format(sisaPR));
                        o = fn.udfGetDouble(tblItem.getValueAt(row, tblItem.getColumnModel().getColumnIndex("Qty Terima")));
                        return o;
                    }
                    if (stMinus) {

                        double maxMinus = fn.udfGetDouble(tblItem.getValueAt(row, tblItem.getColumnModel().getColumnIndex("Qty Order"))) - sisaPR;
                        if (fn.udfGetDouble(((JTextField) text).getText()) + maxMinus < 0) {
                            JOptionPane.showMessageDialog(FrmGoodReceipt.this, "Jumlah On Receipt Maksimum adalah : -"
                                    + fn.dFmt.format(maxMinus));
                            o = fn.udfGetDouble(tblItem.getValueAt(row, tblItem.getColumnModel().getColumnIndex("Qty Terima")));
                            return o;
                        }
                    }
                    retVal = fn.udfGetDouble(((JTextField) text).getText());
                } else {
                    retVal = fn.udfGetDouble(((JTextField) text).getText());
                }

                return retVal;
            } catch (Exception e) {
                toolkit.beep();
                retVal = 0;
            }
            return retVal;
        }

    }

    Vector tableCopyRow;
}
