/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * FrmBaserPriceByItem.java
 *
 * Created on Aug 8, 2010, 2:11:18 PM
 */
package apotek;

import main.MainForm;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.FocusTraversalPolicy;
import java.awt.KeyboardFocusManager;
import java.awt.Toolkit;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
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
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumnModel;
import main.DlgLookup;
import main.GeneralFunction;
import org.jdesktop.swingx.JXTable;
import org.jdesktop.swingx.JXTableBeanInfo;
import org.jdesktop.swingx.autocomplete.ComboBoxCellEditor;

/**
 *
 * @author cak-ust
 */
public class FrmSupplierPriceByItem extends javax.swing.JFrame {

    private Connection conn;
    private GeneralFunction fn;
    MyKeyListener kListener = new MyKeyListener();
    protected DecimalFormat dFmt = new DecimalFormat("#,##0.00");
    private JComboBox cmbSatuan = new JComboBox();
    private Component aThis;

    /**
     * Creates new form FrmBaserPriceByItem
     */
    public FrmSupplierPriceByItem() {
        initComponents();
        tblSupplier.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).
                put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0),
                        "selectNextColumnCell");
        fn = new GeneralFunction(MainForm.conn);
        fn.addKeyListenerInContainer(jToolBar1, kListener, txtFocusListener);
        fn.addKeyListenerInContainer(jPanel1, kListener, txtFocusListener);
        fn.addKeyListenerInContainer(jPanel2, kListener, txtFocusListener);
        tblSupplier.addKeyListener(kListener);
        jScrollPane1.addKeyListener(kListener);

        MyTableCellEditor cEditor = new MyTableCellEditor();
        tblSupplier.getColumnModel().getColumn(tblSupplier.getColumnModel().getColumnIndex("Prioritas")).setCellEditor(cEditor);
        tblSupplier.getColumnModel().getColumn(tblSupplier.getColumnModel().getColumnIndex("Price List")).setCellEditor(cEditor);
        tblSupplier.getColumnModel().getColumn(tblSupplier.getColumnModel().getColumnIndex("Disc (%)")).setCellEditor(cEditor);
        tblSupplier.getColumnModel().getColumn(tblSupplier.getColumnModel().getColumnIndex("Disc (Rp)")).setCellEditor(cEditor);
        tblSupplier.getColumnModel().getColumn(tblSupplier.getColumnModel().getColumnIndex("PPN%")).setCellEditor(cEditor);
        tblSupplier.getColumnModel().getColumn(tblSupplier.getColumnModel().getColumnIndex("Konversi")).setCellEditor(cEditor);
        tblSupplier.getColumn("Satuan2").setCellEditor(new ComboBoxCellEditor(cmbSatuan));
        //tblSupplier.getColumnModel().getColumn(tblSupplier.getColumnModel().getColumnIndex("UOM Alt")).setCellEditor(cEditor);

        tblSupplier.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            public void valueChanged(ListSelectionEvent e) {
                int iRow = tblSupplier.getSelectedRow();
                if (iRow >= 0) {
                    udfLoadKetBawah(iRow);

                } else {
                    lblBasePrice.setText("0");
                    lblHargaRetur.setText("0");
                }
            }
        });

        tblSupplier.getModel().addTableModelListener(new TableModelListener() {
            public void tableChanged(TableModelEvent e) {
                if (e.getType() == TableModelEvent.UPDATE) {
                    TableColumnModel col = tblSupplier.getColumnModel();
                    int iRow = tblSupplier.getSelectedRow();
                    if (iRow >= 0 && (e.getColumn() == col.getColumnIndex("Konversi")
                            || e.getColumn() == col.getColumnIndex("Price List")
                            || e.getColumn() == col.getColumnIndex("Disc (%)")
                            || e.getColumn() == col.getColumnIndex("Disc (Rp)")
                            || e.getColumn() == col.getColumnIndex("PPN%"))) {

                        double hargaRetur = fn.udfGetDouble(tblSupplier.getValueAt(iRow, col.getColumnIndex("Price List")));
                        double diskon = fn.udfGetDouble(tblSupplier.getValueAt(iRow, col.getColumnIndex("Disc (%)"))) > 0
                                ? hargaRetur * fn.udfGetDouble(tblSupplier.getValueAt(iRow, col.getColumnIndex("Disc (%)"))) / 100
                                : fn.udfGetDouble(tblSupplier.getValueAt(iRow, col.getColumnIndex("Disc (Rp)")));
                        System.out.println("Diskon : " + diskon);
                        hargaRetur -= diskon;
                        hargaRetur = hargaRetur * (1 + fn.udfGetDouble(tblSupplier.getValueAt(iRow, col.getColumnIndex("PPN%"))) / 100);
//                        double hargaRetur=fn.udfGetDouble(tblSupplier.getValueAt(iRow, col.getColumnIndex("Price List")))-
//                                     (fn.udfGetDouble(tblSupplier.getValueAt(iRow, col.getColumnIndex("Price List")))/100 * fn.udfGetDouble(tblSupplier.getValueAt(iRow, col.getColumnIndex("Disc (%)"))));
//                        hargaRetur=hargaRetur+(hargaRetur/100 * fn.udfGetDouble(tblSupplier.getValueAt(iRow, col.getColumnIndex("PPN%"))));
                        hargaRetur = hargaRetur / fn.udfGetDouble(tblSupplier.getValueAt(iRow, col.getColumnIndex("Konversi")));
                        tblSupplier.setValueAt(hargaRetur, iRow, col.getColumnIndex("Harga Beli"));
                        udfLoadKetBawah(iRow);
                    } else if (iRow >= 0 && e.getColumn() == col.getColumnIndex("Satuan2")) {
//                        tblSupplier.setValueAt(tblSupplier.getValueAt(iRow, col.getColumnIndex("Satuan2")),
//                                iRow, col.getColumnIndex("Satuan Kcl"));
                    }
                }
            }
        });
//        cmbSatuan.addItemListener(new java.awt.event.ItemListener() {
//            public void itemStateChanged(java.awt.event.ItemEvent evt) {
//                if(cmbSatuan.getSelectedIndex()>0 && conn!=null)
//                    udfLoadKonversi(cmbSatuan.getSelectedItem().toString());
//
//            }
//        });
        tblSupplier.putClientProperty("terminateEditOnFocusLost", Boolean.TRUE);
    }

    private void udfLoadKonversi(String sUnit) {
        int row = tblSupplier.getSelectedRow();
        if (row < 0) {
            return;
        }
        try {
            String sQry = "select case  when '" + sUnit + "'=satuan_kecil then 1 "
                    + "             when '" + sUnit + "'=satuan_besar then coalesce(konversi,1) "
                    + "             else 1 end as konv,"
                    + "coalesce(satuan_kecil,'') as satuan_kecil "
                    + "from barang i "
                    + "where i.item_code='" + txtItem.getText() + "'";

            System.out.println(sQry);
            ResultSet rs = conn.createStatement().executeQuery(sQry);
            if (rs.next()) {
                tblSupplier.setValueAt(rs.getInt("konv"), row, tblSupplier.getColumnModel().getColumnIndex("Konversi"));
                tblSupplier.setValueAt(rs.getString("satuan_kecil"),
                        row, tblSupplier.getColumnModel().getColumnIndex("Satuan Kcl"));
            } else {
                tblSupplier.setValueAt(1, row, tblSupplier.getColumnModel().getColumnIndex("Konversi"));
            }
            rs.close();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage());
        }
    }

    private void udfLoadKetBawah(int iRow) {
        TableColumnModel col = tblSupplier.getColumnModel();
        if (tblSupplier.getValueAt(iRow, 0) == null || tblSupplier.getValueAt(iRow, col.getColumnIndex("Price List")) == null) {
            return;
        }

        double basePrice = fn.udfGetDouble(tblSupplier.getValueAt(iRow, col.getColumnIndex("Price List")))
                + (fn.udfGetDouble(tblSupplier.getValueAt(iRow, col.getColumnIndex("Price List"))) / 100 * fn.udfGetDouble(tblSupplier.getValueAt(iRow, col.getColumnIndex("PPN%"))));
        basePrice = basePrice / fn.udfGetDouble(tblSupplier.getValueAt(iRow, col.getColumnIndex("Konversi")));

//        double hargaRetur=fn.udfGetDouble(tblSupplier.getValueAt(iRow, col.getColumnIndex("Price List")))*
//                         (1 - fn.udfGetDouble(tblSupplier.getValueAt(iRow, col.getColumnIndex("Disc (%)")))/100);
        double hargaRetur = fn.udfGetDouble(tblSupplier.getValueAt(iRow, col.getColumnIndex("Harga Beli")));
//        hargaRetur=hargaRetur *(1 + fn.udfGetDouble(tblSupplier.getValueAt(iRow, col.getColumnIndex("PPN%")))/100);
//        hargaRetur=hargaRetur/fn.udfGetDouble(tblSupplier.getValueAt(iRow, col.getColumnIndex("Konversi")));

        lblBasePrice.setText(dFmt.format(basePrice));
        lblHargaRetur.setText(dFmt.format(hargaRetur));
    }

    public void setConn(Connection con) {
        this.conn = con;

    }

    private void udfInitForm() {
        aThis=this;
        
        tblSupplier.setRowHeight(22);
        tblSupplier.getColumn("Harga Beli").setCellRenderer(new MyRowRenderer());
        tblSupplier.getColumn("Price List").setCellRenderer(new MyRowRenderer());
        tblSupplier.getColumn("Disc (%)").setCellRenderer(new MyRowRenderer());
        tblSupplier.getColumn("PPN%").setCellRenderer(new MyRowRenderer());
        tblSupplier.getColumn("Konversi").setCellRenderer(new MyRowRenderer());

        try {
            cmbSatuan.removeAllItems();
            ResultSet rs = conn.createStatement().executeQuery("select uom, coalesce(uom_desc,'') as uom_desc from uom order by uom");
            while (rs.next()) {
                cmbSatuan.addItem(rs.getString("uom"));
            }
            rs.close();

        } catch (SQLException se) {
            System.out.println("Error init form: " + se.getMessage());
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

                if (e.getSource().equals(txtItem) && !fn.isListVisible() && aThis.isVisible()) {
                    udfLoadSupplier();
                }

            }
        }

    };

    private void udfLoadSupplier() {
        String s = "select sb.kode_supplier, coalesce(s.nama_supplier,'') as nama, coalesce(sb.priority,0) as rk,"
                + "(coalesce(sb.price,0)-(case when coalesce(sb.disc,0) > 0 then "
                + " coalesce(sb.price,0)*coalesce(sb.disc,0)/100 else coalesce(sb.disc_rp,0) end))*(1+coalesce(sb.vat,0)/100)as harga_beli, "
                + "coalesce(sb.price,0) as price_list1, coalesce(sb.disc,0) as disc, coalesce(sb.disc_rp,0) as disc_rp, coalesce(sb.vat,0) as ppn, "
                + "coalesce(sb.uom_alt,'') as uom_alt, coalesce(convertion,1) as konv "
                + ""
                + "from supplier_barang sb "
                + "left join phar_supplier s on s.kode_supplier=sb.kode_supplier "
                + "where sb.kode_barang='" + txtItem.getText() + "' order by priority ";
        System.out.println(s);
        try {
            ResultSet rs = null;//conn.createStatement().executeQuery("select coalesce(satuan_kecil,'') as unit, " +
//                    "coalesce(konversi,1) as konv " +
//                    "from barang where item_code='"+txtItem.getText()+"'");
//            cmbSatuan.removeAllItems();
//            if(rs.next()){
//                cmbSatuan.addItem(rs.getString("unit"));
//                
//            }
//            rs.close();
            rs = conn.createStatement().executeQuery(s);
            ((DefaultTableModel) tblSupplier.getModel()).setNumRows(0);
            while (rs.next()) {
                ((DefaultTableModel) tblSupplier.getModel()).addRow(new Object[]{
                    rs.getString("kode_supplier"),
                    rs.getString("nama"),
                    rs.getInt("rk"),
                    rs.getDouble("harga_beli") / rs.getDouble("konv"),
                    rs.getDouble("price_list1"),
                    rs.getDouble("disc"),
                    rs.getDouble("disc_rp"),
                    rs.getDouble("ppn"),
                    rs.getString("uom_alt"),
                    rs.getDouble("konv")
                });
            }
            tblSupplier.setModel((DefaultTableModel) fn.autoResizeColWidth(tblSupplier, (DefaultTableModel) tblSupplier.getModel()).getModel());
            if (tblSupplier.getRowCount() > 0) {
                tblSupplier.setRowSelectionInterval(0, 0);
                tblSupplier.changeSelection(0, 4, false, false);
            }

            rs.close();
            tblSupplier.requestFocusInWindow();

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

        jLabel1 = new javax.swing.JLabel();
        jPanel1 = new javax.swing.JPanel();
        jLabel20 = new javax.swing.JLabel();
        jLabel14 = new javax.swing.JLabel();
        txtItem = new javax.swing.JTextField();
        lblItemName = new javax.swing.JLabel();
        jLabel17 = new javax.swing.JLabel();
        jLabel15 = new javax.swing.JLabel();
        txtSatKecil = new javax.swing.JTextField();
        jLabel18 = new javax.swing.JLabel();
        lblBasepriceSkg = new javax.swing.JTextField();
        jScrollPane1 = new javax.swing.JScrollPane();
        tblSupplier = new javax.swing.JTable();
        jPanel2 = new javax.swing.JPanel();
        lblBasePrice = new javax.swing.JLabel();
        jLabel26 = new javax.swing.JLabel();
        lblHargaRetur = new javax.swing.JLabel();
        jLabel30 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jButton1 = new javax.swing.JButton();
        jToolBar1 = new javax.swing.JToolBar();
        btnNew = new javax.swing.JButton();
        btnSave = new javax.swing.JButton();
        btnCancel = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Master Price By Product");
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowOpened(java.awt.event.WindowEvent evt) {
                formWindowOpened(evt);
            }
        });
        getContentPane().setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel1.setFont(new java.awt.Font("Tahoma", 0, 24)); // NOI18N
        jLabel1.setText("Master Harga Supplier");
        jLabel1.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        jLabel1.setOpaque(true);
        getContentPane().add(jLabel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 10, 720, 60));

        jPanel1.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        jPanel1.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel20.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jLabel20.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel20.setText("Product ID");
        jPanel1.add(jLabel20, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 10, 90, 20));

        jLabel14.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jLabel14.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel14.setText(":");
        jPanel1.add(jLabel14, new org.netbeans.lib.awtextra.AbsoluteConstraints(100, 10, 10, 20));

        txtItem.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 1, true));
        txtItem.setDisabledTextColor(new java.awt.Color(153, 153, 153));
        txtItem.setName("txtItem"); // NOI18N
        jPanel1.add(txtItem, new org.netbeans.lib.awtextra.AbsoluteConstraints(110, 10, 120, 20));

        lblItemName.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
        lblItemName.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 1, true));
        lblItemName.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                lblItemNamePropertyChange(evt);
            }
        });
        jPanel1.add(lblItemName, new org.netbeans.lib.awtextra.AbsoluteConstraints(230, 10, 430, 20));

        jLabel17.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jLabel17.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel17.setText("Satuan Kecil");
        jPanel1.add(jLabel17, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 35, 90, 20));

        jLabel15.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jLabel15.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel15.setText(":");
        jPanel1.add(jLabel15, new org.netbeans.lib.awtextra.AbsoluteConstraints(100, 35, 10, 20));

        txtSatKecil.setFont(new java.awt.Font("Dialog", 0, 14)); // NOI18N
        txtSatKecil.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 1, true));
        txtSatKecil.setDisabledTextColor(new java.awt.Color(0, 0, 0));
        txtSatKecil.setEnabled(false);
        txtSatKecil.setName("txtSatuanKecil"); // NOI18N
        txtSatKecil.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtSatKecilKeyReleased(evt);
            }
            public void keyTyped(java.awt.event.KeyEvent evt) {
                txtSatKecilKeyTyped(evt);
            }
        });
        jPanel1.add(txtSatKecil, new org.netbeans.lib.awtextra.AbsoluteConstraints(110, 35, 120, 20));

        jLabel18.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jLabel18.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel18.setText("Baseprice Saat ini :");
        jPanel1.add(jLabel18, new org.netbeans.lib.awtextra.AbsoluteConstraints(320, 35, 130, 20));

        lblBasepriceSkg.setFont(new java.awt.Font("Dialog", 0, 14)); // NOI18N
        lblBasepriceSkg.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 1, true));
        lblBasepriceSkg.setDisabledTextColor(new java.awt.Color(0, 0, 0));
        lblBasepriceSkg.setEnabled(false);
        lblBasepriceSkg.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                lblBasepriceSkgKeyReleased(evt);
            }
            public void keyTyped(java.awt.event.KeyEvent evt) {
                lblBasepriceSkgKeyTyped(evt);
            }
        });
        jPanel1.add(lblBasepriceSkg, new org.netbeans.lib.awtextra.AbsoluteConstraints(460, 35, 120, 20));

        getContentPane().add(jPanel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 80, 875, 60));

        tblSupplier.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        tblSupplier.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "SupplierID", "Nama Supplier", "Prioritas", "Harga Beli", "Price List", "Disc (%)", "Disc (Rp)", "PPN%", "Satuan2", "Konversi"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Object.class, java.lang.Object.class, java.lang.Integer.class, java.lang.Double.class, java.lang.Double.class, java.lang.Double.class, java.lang.Double.class, java.lang.Double.class, java.lang.Object.class, java.lang.Double.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, true, false, true, true, true, true, true, true
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        tblSupplier.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_OFF);
        tblSupplier.setName("tblSupplier"); // NOI18N
        tblSupplier.setSurrendersFocusOnKeystroke(true);
        tblSupplier.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                tblSupplierKeyPressed(evt);
            }
            public void keyReleased(java.awt.event.KeyEvent evt) {
                tblSupplierKeyReleased(evt);
            }
            public void keyTyped(java.awt.event.KeyEvent evt) {
                tblSupplierKeyTyped(evt);
            }
        });
        jScrollPane1.setViewportView(tblSupplier);

        getContentPane().add(jScrollPane1, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 140, 875, 160));

        jPanel2.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        jPanel2.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        lblBasePrice.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lblBasePrice.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 1, true));
        lblBasePrice.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                lblBasePricePropertyChange(evt);
            }
        });
        jPanel2.add(lblBasePrice, new org.netbeans.lib.awtextra.AbsoluteConstraints(100, 10, 100, 20));

        jLabel26.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jLabel26.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel26.setText("Base Price :");
        jPanel2.add(jLabel26, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 10, 90, 20));

        lblHargaRetur.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lblHargaRetur.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 1, true));
        lblHargaRetur.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                lblHargaReturPropertyChange(evt);
            }
        });
        jPanel2.add(lblHargaRetur, new org.netbeans.lib.awtextra.AbsoluteConstraints(660, 10, 110, 20));

        jLabel30.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jLabel30.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel30.setText("Harga Retur : ");
        jPanel2.add(jLabel30, new org.netbeans.lib.awtextra.AbsoluteConstraints(570, 10, 90, 20));

        jLabel2.setBackground(new java.awt.Color(204, 255, 255));
        jLabel2.setForeground(new java.awt.Color(0, 0, 153));
        jLabel2.setText("<html>\n &nbsp <b>F4  &nbsp &nbsp    : </b> Clear <br> \n &nbsp <b>F5 &nbsp &nbsp : </b>  Simpan Master Price <br>\n &nbsp <b>Insert : </b> Menambah Supplier\n</html>"); // NOI18N
        jLabel2.setVerticalAlignment(javax.swing.SwingConstants.TOP);
        jLabel2.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        jLabel2.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jLabel2.setOpaque(true);
        jLabel2.setVerticalTextPosition(javax.swing.SwingConstants.TOP);
        jPanel2.add(jLabel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(220, 10, 290, 50));

        jButton1.setText("Update Baseprice");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });
        jPanel2.add(jButton1, new org.netbeans.lib.awtextra.AbsoluteConstraints(65, 35, 145, -1));

        getContentPane().add(jPanel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 300, 875, 70));

        jToolBar1.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        jToolBar1.setRollover(true);

        btnNew.setIcon(new javax.swing.ImageIcon(getClass().getResource("/image/Icon/New.png"))); // NOI18N
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

        btnSave.setIcon(new javax.swing.ImageIcon(getClass().getResource("/image/Icon/Save.png"))); // NOI18N
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

        btnCancel.setIcon(new javax.swing.ImageIcon(getClass().getResource("/image/Icon/Exit.png"))); // NOI18N
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

        getContentPane().add(jToolBar1, new org.netbeans.lib.awtextra.AbsoluteConstraints(730, 10, 150, 60));

        setSize(new java.awt.Dimension(912, 417));
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void lblItemNamePropertyChange(java.beans.PropertyChangeEvent evt) {//GEN-FIRST:event_lblItemNamePropertyChange
        // TODO add your handling code here:
}//GEN-LAST:event_lblItemNamePropertyChange

    private void txtSatKecilKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtSatKecilKeyReleased
        // TODO add your handling code here:
}//GEN-LAST:event_txtSatKecilKeyReleased

    private void txtSatKecilKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtSatKecilKeyTyped
        // TODO add your handling code here:
}//GEN-LAST:event_txtSatKecilKeyTyped

    private void lblBasePricePropertyChange(java.beans.PropertyChangeEvent evt) {//GEN-FIRST:event_lblBasePricePropertyChange
        // TODO add your handling code here:
}//GEN-LAST:event_lblBasePricePropertyChange

    private void lblHargaReturPropertyChange(java.beans.PropertyChangeEvent evt) {//GEN-FIRST:event_lblHargaReturPropertyChange
        // TODO add your handling code here:
}//GEN-LAST:event_lblHargaReturPropertyChange

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
            btnCancel.setIcon(new javax.swing.ImageIcon(getClass().getResource("/image/Icon/Exit.png")));
        } else {
            this.dispose();
        }
}//GEN-LAST:event_btnCancelActionPerformed

    private void formWindowOpened(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowOpened
        udfInitForm();
    }//GEN-LAST:event_formWindowOpened

    private void tblSupplierKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_tblSupplierKeyReleased

    }//GEN-LAST:event_tblSupplierKeyReleased

    private void tblSupplierKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_tblSupplierKeyTyped

    }//GEN-LAST:event_tblSupplierKeyTyped

    private void tblSupplierKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_tblSupplierKeyPressed
//        if (evt.getKeyCode() == KeyEvent.VK_UP && tblSupplier.getSelectedRow() == 0) {
//            txtItem.requestFocus();
//        }
    }//GEN-LAST:event_tblSupplierKeyPressed

    private void lblBasepriceSkgKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_lblBasepriceSkgKeyReleased
        // TODO add your handling code here:
    }//GEN-LAST:event_lblBasepriceSkgKeyReleased

    private void lblBasepriceSkgKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_lblBasepriceSkgKeyTyped
        // TODO add your handling code here:
    }//GEN-LAST:event_lblBasepriceSkgKeyTyped

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        updateBasePrice();
    }//GEN-LAST:event_jButton1ActionPerformed

    private void updateBasePrice(){
        if (tblSupplier.getSelectedRow() < 0 || txtItem.getText().length() == 0) {
            return;
        }
        if (JOptionPane.showConfirmDialog(aThis, "Anda yakin untuk mengupdate baseprice item ini?", "Update", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
            try {
                int i = conn.createStatement().executeUpdate("update barang set base_price=" + GeneralFunction.udfGetDouble(lblBasePrice.getText()) + " "
                        + "where item_code='" + txtItem.getText() + "'");
                if (i > 0) {
                    JOptionPane.showMessageDialog(this, "Update baseprice sukses!");
                }
            } catch (SQLException ex) {
                Logger.getLogger(FrmSupplierPriceByItem.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new FrmSupplierPriceByItem().setVisible(true);
            }
        });
    }
    SimpleDateFormat dmyFmt_hhmm = new SimpleDateFormat("dd/MM/yyyy hh:mm");
    NumberFormat numFmt = new DecimalFormat("#,##0.00");
    NumberFormat nFmt = new DecimalFormat("#,##0");

    public class MyRowRenderer extends DefaultTableCellRenderer implements TableCellRenderer {
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            if (value instanceof Date) {
                value = fn.ddMMyy_format.format(value);
                if (value instanceof Timestamp) {
                    value = dmyFmt_hhmm.format(value);
                }
            } else if (value instanceof Double || value instanceof Float) {
                setHorizontalAlignment(SwingConstants.RIGHT);
                if(column==5)
                    value = numFmt.format(value);
                else
                    value=nFmt.format(value);
                
            } else if (value instanceof Integer) {
                setHorizontalAlignment(SwingConstants.RIGHT);
                value = nFmt.format(value);

            }
            setFont(table.getFont());
            if (isSelected) {
                setBackground(table.getSelectionBackground());
                setForeground(table.getSelectionForeground());
            } else {
                setBackground(table.getBackground());
                setForeground(table.getForeground());
            }

            if (hasFocus) {
                setBorder(UIManager.getBorder("Table.focusCellHighlightBorder"));
                if (!isSelected && table.isCellEditable(row, column)) {
                    Color col;
                    col = UIManager.getColor("Table.focusCellForeground");
                    if (col != null) {
                        super.setForeground(col);
                    }
                    col = UIManager.getColor("Table.focusCellBackground");
                    if (col != null) {
                        super.setBackground(col);
                    }
                }
            } else {
                setBorder(noFocusBorder);
            }

            setValue(value);
            return this;
        }
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnCancel;
    private javax.swing.JButton btnNew;
    private javax.swing.JButton btnSave;
    private javax.swing.JButton jButton1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel18;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel20;
    private javax.swing.JLabel jLabel26;
    private javax.swing.JLabel jLabel30;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JToolBar jToolBar1;
    private javax.swing.JLabel lblBasePrice;
    private javax.swing.JTextField lblBasepriceSkg;
    private javax.swing.JLabel lblHargaRetur;
    private javax.swing.JLabel lblItemName;
    private javax.swing.JTable tblSupplier;
    private javax.swing.JTextField txtItem;
    private javax.swing.JTextField txtSatKecil;
    // End of variables declaration//GEN-END:variables
    protected static Border noFocusBorder = new EmptyBorder(1, 1, 1, 1);

    private void udfNew() {
        txtItem.setText("");
        lblItemName.setText("");
        txtSatKecil.setText("");
        ((DefaultTableModel) tblSupplier.getModel()).setNumRows(0);
        lblBasePrice.setText("0");
        lblHargaRetur.setText("0");
        txtItem.requestFocus();
    }

    private boolean udfCekBeforeSave() {
        boolean b = true;
        btnSave.requestFocus();
        TableColumnModel col = tblSupplier.getColumnModel();
        if (txtItem.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Silakan isi ProductID terlebih dulu!");
            txtItem.requestFocus();
            return false;
        }
        if (tblSupplier.getRowCount() == 0) {
            JOptionPane.showMessageDialog(this, "Tidak ada supplier yang akan disimpan!");
            tblSupplier.requestFocus();
            return false;
        }
        double rkI, rkJ;
        for (int i = 0; i < tblSupplier.getRowCount(); i++) {
            rkI = fn.udfGetDouble(tblSupplier.getValueAt(i, col.getColumnIndex("Prioritas")));
            for (int j = 0; j < tblSupplier.getRowCount(); j++) {
                rkJ = fn.udfGetDouble(tblSupplier.getValueAt(j, col.getColumnIndex("Prioritas")));
                if (i != j && rkJ == rkI) {
                    JOptionPane.showMessageDialog(this, "Ranking \"" + rkJ + "\" Sama untuk Supplier '" + tblSupplier.getValueAt(j, 2).toString() + "' "
                            + "dengan supplier '" + tblSupplier.getValueAt(i, 2).toString() + "' ");
                    tblSupplier.requestFocus();
                    tblSupplier.changeSelection(j, col.getColumnIndex("Prioritas"), false, false);
                    return false;
                }
            }
        }

        return b;
    }

    private void udfSave() {
        try {
            if (!udfCekBeforeSave()) {
                return;
            }
            String sSupp = "", sIns = "";
            TableColumnModel col = tblSupplier.getColumnModel();

            for (int i = 0; i < tblSupplier.getRowCount(); i++) {
                sSupp += (sSupp.length() == 0 ? "" : ",") + "'" + tblSupplier.getValueAt(i, 0).toString() + "'";
                sIns += (sIns.length() == 0 ? "" : " union all ")
                        + "select fn_supplier_barang_save('" + txtItem.getText() + "',"
                        + "'" + tblSupplier.getValueAt(i, col.getColumnIndex("SupplierID")).toString() + "', "
                        + "'" + tblSupplier.getValueAt(i, col.getColumnIndex("Satuan2")).toString() + "', "
                        + fn.udfGetDouble(tblSupplier.getValueAt(i, col.getColumnIndex("Konversi"))) + ", "
                        + fn.udfGetDouble(tblSupplier.getValueAt(i, col.getColumnIndex("Price List"))) + ","
                        + fn.udfGetDouble(tblSupplier.getValueAt(i, col.getColumnIndex("Disc (%)"))) + ","
                        + fn.udfGetDouble(tblSupplier.getValueAt(i, col.getColumnIndex("Disc (Rp)"))) + ","
                        + fn.udfGetDouble(tblSupplier.getValueAt(i, col.getColumnIndex("PPN%"))) + ","
                        + fn.udfGetInt(tblSupplier.getValueAt(i, col.getColumnIndex("Prioritas"))) + ","
                        + "0::double precision,0::double precision, '" + MainForm.sUserName + "') ";
            }
            String sDelSupp = "delete from supplier_barang where kode_supplier not in(" + sSupp + ") "
                    + "and kode_barang='" + txtItem.getText() + "'; ";

            //System.out.println(sIns+sDelSupp);
            conn.setAutoCommit(false);
            int iUpd = conn.createStatement().executeUpdate(sDelSupp);

            ResultSet rs = conn.createStatement().executeQuery(sIns);
            rs.next();
            rs.close();

//            rs=conn.createStatement().executeQuery("select fn_phar_pr_update_supplier_pertama()");
//            rs.next();
//            rs.close();
            conn.setAutoCommit(true);

            JOptionPane.showMessageDialog(this, "Simpan Master Price Sukses!");

        } catch (SQLException se) {
            try {
                conn.rollback();
                conn.setAutoCommit(true);
                JOptionPane.showMessageDialog(this, se.getMessage());
            } catch (SQLException ex) {
                Logger.getLogger(FrmSupplierPriceByItem.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    public class MyKeyListener extends KeyAdapter {

        @Override
        public void keyReleased(java.awt.event.KeyEvent evt){
            if(evt.getSource().equals(txtItem)){
                fn.lookup(evt, new Object[]{lblItemName, txtSatKecil, lblBasepriceSkg}, "select item_code, coalesce(nama_paten,'') as nama_barang, "
                    + "coalesce(satuan_kecil,'') as satuan, coalesce(base_price, 0) as base_price "
                    + "from barang where discontinued=false "
                    + "and item_code||coalesce(item_name,'')||coalesce(nama_paten,'') ilike '%" + txtItem.getText() + "%' "
                    + "order by 2",
                    txtItem.getWidth() + lblItemName.getWidth(), 200);
            }
        }
        
        @Override
        public void keyTyped(java.awt.event.KeyEvent evt) {
            if (evt.getSource() instanceof JTextField
                    && ((JTextField) evt.getSource()).getName() != null
                    && ((JTextField) evt.getSource()).getName().equalsIgnoreCase("textEditor")
                    && tblSupplier.getSelectedColumn() != tblSupplier.getColumnModel().getColumnIndex("Satuan2")) {

                char c = evt.getKeyChar();
                if (!((c >= '0' && c <= '9'))
                        && (c != KeyEvent.VK_BACK_SPACE)
                        && (c != KeyEvent.VK_DELETE)
                        && (c != KeyEvent.VK_ENTER)
                        && (c != '-')
                        && (c != '.')) {
                    getToolkit().beep();
                    evt.consume();
                    return;
                }
            }

        }

        @Override
        public void keyPressed(KeyEvent evt) {
            Component ct = KeyboardFocusManager.getCurrentKeyboardFocusManager().getPermanentFocusOwner();
            int keyKode = evt.getKeyCode();
            switch (keyKode) {
                case KeyEvent.VK_ENTER: {
                    if (!(ct instanceof JTable || ct instanceof JXTable)) {
                        if (!fn.isListVisible()) {
                            Component c = findNextFocus();
                            if (c == null) {
                                return;
                            }
                            System.out.println("Next Component: "+c.getName());
                            c.requestFocus();
                        } else {
                            fn.lstRequestFocus();
                        }
                    } else {
                        return;
                    }
                    break;
                }
                case KeyEvent.VK_DOWN: {
                    if (!(ct instanceof JTable || ct instanceof JXTable)) {
                        if (!fn.isListVisible()) {
                            Component c = findNextFocus();
                            if (c == null) {
                                return;
                            }
                            System.out.println("Next Component: "+c.getName());
                            c.requestFocus();
                        } else {
                            fn.lstRequestFocus();
                        }
                        
                    }
                    break;
                }

                case KeyEvent.VK_UP: {
                    if (!(evt.getSource() instanceof JTable)) {
                        Component c = findPrevFocus();
                        c.requestFocus();
                        System.out.println("Next Component: "+c.getName());
                    } else if (evt.getSource().equals(tblSupplier) && tblSupplier.getSelectedRow() == 0) {
                        //txtItem.requestFocus();
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
                case KeyEvent.VK_F5: {
                    udfSave();
                    break;
                }
                case KeyEvent.VK_F4: {
                    udfNew();
                    break;
                }
                case KeyEvent.VK_DELETE: {
                    if (evt.getSource().equals(tblSupplier) && tblSupplier.getSelectedRow() >= 0) {
                        int iRow[] = tblSupplier.getSelectedRows();
                        int rowPalingAtas = iRow[0];

//                        if(JOptionPane.showConfirmDialog(FrmPO.this,
//                                "Item '"+tblPR.getValueAt(iRow, 3).toString()+"' dihapus dari PO?", "Confirm", JOptionPane.YES_NO_OPTION)==JOptionPane.NO_OPTION)
//                            return;
                        for (int a = 0; a < iRow.length; a++) {
                            ((DefaultTableModel) tblSupplier.getModel()).removeRow(tblSupplier.getSelectedRow());
                        }

                        if (tblSupplier.getRowCount() > 0 && rowPalingAtas < tblSupplier.getRowCount()) {
                            //if(tblPR.getSelectedRow()>0)
                            tblSupplier.setRowSelectionInterval(rowPalingAtas, rowPalingAtas);
                        } else {
                            if (tblSupplier.getRowCount() > 0) {
                                tblSupplier.setRowSelectionInterval(rowPalingAtas - 1, rowPalingAtas - 1);
                            } else {
                                txtItem.requestFocus();
                            }

                        }
                        if (tblSupplier.getSelectedRow() >= 0) {
                            tblSupplier.changeSelection(tblSupplier.getSelectedRow(), 0, false, false);
                        }
                    }
                    break;

                }
                case KeyEvent.VK_INSERT: {

                    lookupSupplier();
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
        JTextField text = new JTextField() {
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
            row = rowIndex;
            col = vColIndex;

            if (vColIndex != tblSupplier.getColumnModel().getColumnIndex("Satuan2")) {
                text.addKeyListener(kListener);
            } else {
                text.removeKeyListener(kListener);
            }

            text.setName("textEditor");

            //col=vColIndex;
            text.setBackground(new Color(0, 255, 204));
            text.addFocusListener(txtFocusListener);
            //text.addKeyListener(kListener);
            text.setFont(table.getFont());
           //text.setName("textEditor");

            //text.setText(value==null? "": value.toString());
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
//                if(col==tblSupplier.getColumnModel().getColumnIndex("Satuan2")){
//                    retVal = ((JTextField)text).getText();
//                }else
                if (col == tblSupplier.getColumnModel().getColumnIndex("Prioritas")) {
                    retVal = fn.udfGetInt(((JTextField) text).getText());
                } else {
                    retVal = fn.udfGetDouble(((JTextField) text).getText());
                }

                if (col == tblSupplier.getColumnModel().getColumnIndex("Disc (%)")) {
                    if ((Double) retVal > 0) {
                        tblSupplier.setValueAt(0, row, tblSupplier.getColumnModel().getColumnIndex("Disc (Rp)"));
                    }
                } else if (col == tblSupplier.getColumnModel().getColumnIndex("Disc (Rp)")) {
                    if ((Double) retVal > 0) {
                        tblSupplier.setValueAt(0, row, tblSupplier.getColumnModel().getColumnIndex("Disc (%)"));
                    }
                }
                return retVal;
            } catch (Exception e) {
                toolkit.beep();
                retVal = 0;
            }
            return retVal;
        }

    }

    private void lookupSupplier() {
        tblSupplier.requestFocus();
        if(txtItem.getText().equalsIgnoreCase("")){
            JOptionPane.showMessageDialog(this, "Silahkan pilih nama barang terlebih dulu!");
            txtItem.requestFocusInWindow();
            return;
        }
        DlgLookup d1 = new DlgLookup(JOptionPane.getFrameForComponent(FrmSupplierPriceByItem.this), true);
        String sSupplier = "";
        for (int i = 0; i < tblSupplier.getRowCount(); i++) {
            sSupplier += (sSupplier.length() == 0 ? "" : ",") + "'" + tblSupplier.getValueAt(i, 0).toString() + "'";
        }

        String s = "select * from ("
                + "select kode_supplier, coalesce(nama_supplier,'') as nama_supplier from "
                + "phar_supplier sp "
                + (sSupplier.length() > 0 ? "where kode_supplier not in(" + sSupplier + ")" : "") + " order by 2) x ";

        //System.out.println(s);
//                    ((DefaultTableModel)tblSupplier.getModel()).setNumRows(tblSupplier.getRowCount()+1);
//                    tblSupplier.setRowSelectionInterval(tblSupplier.getRowCount()-1, tblSupplier.getRowCount()-1);
        d1.setTitle("Lookup Supplier");
        d1.udfLoad(conn, s, "(kode_supplier||nama_supplier)", null);

        d1.setVisible(true);

        //System.out.println("Kode yang dipilih" +d1.getKode());
        if (d1.getKode().length() > 0) {
            TableColumnModel col = d1.getTable().getColumnModel();
            JTable tbl = d1.getTable();
            int iRow = tbl.getSelectedRow();

            ((DefaultTableModel) tblSupplier.getModel()).addRow(new Object[]{
                tbl.getValueAt(iRow, col.getColumnIndex("kode_supplier")).toString(),
                tbl.getValueAt(iRow, col.getColumnIndex("nama_supplier")).toString(),
                tblSupplier.getRowCount() + 1,
                0,
                0,
                0, //Disc %
                0, //Disc Rp
                10,
                "",
                1
            });

            tblSupplier.setRowSelectionInterval(tblSupplier.getRowCount() - 1, tblSupplier.getRowCount() - 1);
            tblSupplier.changeSelection(tblSupplier.getRowCount() - 1,
                    tblSupplier.getColumnModel().getColumnIndex("Price List"), false, false);
            tblSupplier.requestFocus();
        }

    }
}
