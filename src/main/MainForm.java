/*
 * NewJFrame.java
 *
 * Created on April 13, 2008, 10:21 PM
 */
package main;

import apotek.About;
import apotek.Colors;
import apotek.DlgLookupItemBeli;
import apotek.DlgLookupItemJual;
import apotek.FrmAP;
import apotek.FrmAPJtTempo;
import apotek.FrmAR;
import apotek.FrmItem;
import apotek.FrmCustomer;
import apotek.FrmDokterList;
import apotek.FrmRptInventory;
import apotek.FrmRptKasir;
import apotek.FrmRptKeuangan;
import apotek.FrmRptPenjualan;
import apotek.FrmSettingHargaJual;
import apotek.FrmSupplierPriceByItem;
import apotek.FrmTransferHistory;
import apotek.FrmTransferRuang;
import apotek.JDesktopImage;
import apotek.StockOpname;
import apotek.TrxReturPenjualan;
import apotek.dao.SettingDao;
import apotek.inventori.FrmExpiredDate;
import apotek.inventori.FrmUnplanned;
import penjualan.FrmPenjualanHistory;
import penjualan.FrmReturPenjualanHistory;
import penjualan.FrmPenjualan2;
import apotek.master.FrmSupplier;
import apotek.master.FrmUOM;
import apotek.master.FrmBentuk;
import apotek.master.FrmDiskonEvent;
import apotek.master.FrmGroup;
import apotek.master.FrmGudang;
import apotek.master.FrmItemDummyList;
import apotek.master.FrmJenisBarang;
import apotek.master.FrmJenisSupplier;
import apotek.master.FrmManufaktur;
import apotek.pettycash.FrmPettyCash;
import apotek.pettycash.FrmPettyCashList;
import com.klinik.dokter.FrmRegList;
import com.klinik.rm.DlgLookupPasien;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.Frame;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.beans.PropertyVetoException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Timer;
import java.util.TimerTask;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ImageIcon;
import javax.swing.JInternalFrame;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.UIManager;
import javax.swing.plaf.FontUIResource;
import pembelian.FrmGRApproval;
import pembelian.FrmGoodReceipt;
import pembelian.FrmPO;
import pembelian.FrmPOApproval;
import pembelian.FrmPOCash;
import pembelian.FrmPOCashHistory;
import pembelian.FrmPR;
import pembelian.FrmPRApproval;
import pembelian.FrmPRMaintenance;
import com.klinik.rm.DlgPasien;
import com.klinik.rm.FrmReservasi;
import com.klinik.rm.DlgPasien;

/**E
 * load
 *
 * @author oestadho
 */
public class MainForm extends javax.swing.JFrame {
    public static String sUserName = "", sUserID = "", sShift = "P";
    public static int iUserProfile = 0;
    public static Connection conn;
    static int iLeft, iTop;
    public static String sKodeGudang = "", sNamaGudang = "";
    public static String formatTgl="dd-MM-YYYY";
    SysConfig sc = new SysConfig();
    private Timer timer;
    public static Image imageIcon;
    public static Setting setting=new Setting();
    private SettingDao settingDao=new SettingDao();
    public static String sKodeDokter="";
    public static String sNamaDokter="";
    public static String sCatatanKwt="";
    /**
     * Creates new form NewJFrame
     */
    public MainForm() {
        initComponents();

        mnuPRApproval.setVisible(false);
        mnuPOApproval.setVisible(false);
        mnuGRApproval.setVisible(false);
        changeUIdefaults();
        setIconImage(new ImageIcon(getClass().getResource("/resources/uTorrent.gif")).getImage());
        imageIcon = new ImageIcon(getClass().getResource("/resources/uTorrent.gif")).getImage();

//
        //Dimension dm=Toolkit.getDefaultToolkit().getScreenSize();
        //setBounds((dm.width-1024)/2, (dm.height-768)/2, 1024, 768);
        //setBounds(0, 0, dm.width, dm.height);

    }

    public void setServerLocation(String s) {
        lblServer.setText(s);
    }

    public void setUserProfile(int aInt) {
        iUserProfile = aInt;
    }

    private void udfAddActionInventory() {
        taskpane_inventori.removeAll();
//        if (menuItem.canRead()) {
            taskpane_inventori.add(new AbstractAction() {
                {
                    putValue(Action.NAME, "Item");
                    putValue(Action.SHORT_DESCRIPTION, "Item/ Barang");
                    putValue(Action.SMALL_ICON, Images.Diagram.getIcon(12, 12));
                }

                public void actionPerformed(ActionEvent e) {
                    mnuMasterBarangActionPerformed(null);
                }
            });
//        }

        taskpane_inventori.add(new AbstractAction() {
            {
                putValue(Action.NAME, "Stock opname");
                putValue(Action.SHORT_DESCRIPTION, "Penyesuaian Persediaan");
                putValue(Action.SMALL_ICON, Images.Diagram.getIcon(12, 12));
            }

            public void actionPerformed(ActionEvent e) {
                udfLoadSO();
            }
        });

        taskpane_inventori.add(new AbstractAction() {
            {
                putValue(Action.NAME, "Transfer Barang");
                putValue(Action.SHORT_DESCRIPTION, "Transfer Item/ Barang");
                putValue(Action.SMALL_ICON, Images.Diagram.getIcon(12, 12));
            }

            public void actionPerformed(ActionEvent e) {
                udfLoadTransfer();
            }
        });
        taskpane_inventori.add(new AbstractAction() {
            {
                putValue(Action.NAME, "History Expired");
                putValue(Action.SHORT_DESCRIPTION, "Histori Expired dari Pembelian");
                putValue(Action.SMALL_ICON, Images.Diagram.getIcon(12, 12));
            }

            public void actionPerformed(ActionEvent e) {
                mnuHistoriExpiredDatejMenuItem1ActionPerformed(e);
            }
        });
//      taskpane_inventori.add(new AbstractAction() {
//        {
//          putValue(Action.NAME, "Grouping");
//          putValue(Action.SHORT_DESCRIPTION, "Item Group");
//          putValue(Action.SMALL_ICON, Images.Order.getIcon(20, 20));
//        }
//        public void actionPerformed(ActionEvent e) {
//            udfLoadItemGrouping();
//        }
//      });
//      taskpane_inventori.add(new AbstractAction() {
//        {
//          putValue(Action.NAME, "Gudang");
//          putValue(Action.SHORT_DESCRIPTION, "Gudang");
//          putValue(Action.SMALL_ICON, Images.Molecule.getIcon(20, 20));
//        }
//        public void actionPerformed(ActionEvent e) {
//
//        }
//      });

    }

    private void udfAddActionDaftar() {
        taskpane_daftar.add(new AbstractAction() {
            {
                putValue(Action.NAME, "Pelanggan/ Pasien");
                putValue(Action.SHORT_DESCRIPTION, "Daftar pelanggan/ pasien");
                putValue(Action.SMALL_ICON, Images.Diagram.getIcon(12, 12));
            }

            public void actionPerformed(ActionEvent e) {
                udfLoadListCustomer();
            }
        });
        taskpane_daftar.add(new AbstractAction() {
            {
                putValue(Action.NAME, "Dokter");
                putValue(Action.SHORT_DESCRIPTION, "Daftar dokter");
                putValue(Action.SMALL_ICON, Images.Diagram.getIcon(12, 12));
            }

            public void actionPerformed(ActionEvent e) {
                udfLoadListDokter();
            }
        });
        //if(menuSupplier.canRead()){
        taskpane_daftar.add(new AbstractAction() {
            {
                putValue(Action.NAME, "Supplier");
                putValue(Action.SHORT_DESCRIPTION, "Supplier (Pemasok)");
                putValue(Action.SMALL_ICON, Images.Diagram.getIcon(12, 12));
            }

            public void actionPerformed(ActionEvent e) {
                udfLoadSupplier();
            }
        });
        //}
        taskpane_daftar.add(new AbstractAction() {
            {
                putValue(Action.NAME, "Penjualan");
                putValue(Action.SHORT_DESCRIPTION, "Penjualan barang");
                putValue(Action.SMALL_ICON, Images.Diagram.getIcon(12, 12));
            }

            public void actionPerformed(ActionEvent e) {
                mnuListPenjualanActionPerformed(e);
            }
        });
        taskpane_daftar.add(new AbstractAction() {
            {
                putValue(Action.NAME, "Pembelian");
                putValue(Action.SHORT_DESCRIPTION, "Pembelian Barang");
                putValue(Action.SMALL_ICON, Images.Diagram.getIcon(12, 12));
            }

            public void actionPerformed(ActionEvent e) {
                mnuGRHistoryActionPerformed(e);
            }
        });
    }

    private void udfAddActionReport() {
        taskpane_report.add(new AbstractAction() {
            {
                putValue(Action.NAME, "Persediaan");
                putValue(Action.SHORT_DESCRIPTION, "Persediaan barang");
                putValue(Action.SMALL_ICON, Images.Diagram.getIcon(12, 12));
            }

            public void actionPerformed(ActionEvent e) {
                mnuRptPersediaanjMenuItem1ActionPerformed(e);
            }
        });
        taskpane_report.add(new AbstractAction() {
            {
                putValue(Action.NAME, "Penjualan");
                putValue(Action.SHORT_DESCRIPTION, "Laporan Penjualan barang");
                putValue(Action.SMALL_ICON, Images.Diagram.getIcon(12, 12));
            }

            public void actionPerformed(ActionEvent e) {
                mnuRptPenjualanjMenuItem1ActionPerformed(e);
            }
        });

        taskpane_report.add(new AbstractAction() {
            {
                putValue(Action.NAME, "Pembelian");
                putValue(Action.SHORT_DESCRIPTION, "Laporan pembelian barang");
                putValue(Action.SMALL_ICON, Images.Diagram.getIcon(12, 12));
            }

            public void actionPerformed(ActionEvent e) {
                mnuRptPembelianjMenuItem1ActionPerformed(e);
            }
        });


    }

    private void udfAddActionTransaksi() {
        taskPane_trx.add(new AbstractAction() {
            {
                putValue(Action.NAME, "Penjualan");
                putValue(Action.SHORT_DESCRIPTION, "Penjualan");
                putValue(Action.SMALL_ICON, Images.Diagram.getIcon(12, 12));
            }

            public void actionPerformed(ActionEvent e) {
                mnuTrxPenjualanjMenuItem1ActionPerformed(e);
            }
        });
        taskPane_trx.add(new JPopupMenu.Separator());
        taskPane_trx.add(new AbstractAction() {
            {
                putValue(Action.NAME, "Purchase Requistion");
                putValue(Action.SHORT_DESCRIPTION, "Permintaan Pembelian");
                putValue(Action.SMALL_ICON, Images.Diagram.getIcon(12, 12));
            }

            public void actionPerformed(ActionEvent e) {
                mnuPRActionPerformed(e);
            }
        });
        taskPane_trx.add(new AbstractAction() {
            {
                putValue(Action.NAME, "Purchase Order");
                putValue(Action.SHORT_DESCRIPTION, "Pesanan Pembelian");
                putValue(Action.SMALL_ICON, Images.Diagram.getIcon(12, 12));
            }

            public void actionPerformed(ActionEvent e) {
                mnuPOActionPerformed(e);
            }
        });
        taskPane_trx.add(new AbstractAction() {
            {
                putValue(Action.NAME, "Penerimaan Barang");
                putValue(Action.SHORT_DESCRIPTION, "Penerimaan Barang");
                putValue(Action.SMALL_ICON, Images.Diagram.getIcon(12, 12));
            }

            public void actionPerformed(ActionEvent e) {
                mnuGRActionPerformed(e);
            }
        });
        taskPane_trx.add(new AbstractAction() {
            {
                putValue(Action.NAME, "Pembelian Cash");
                putValue(Action.SHORT_DESCRIPTION, "PO Cash");
                putValue(Action.SMALL_ICON, Images.Diagram.getIcon(12, 12));
            }

            public void actionPerformed(ActionEvent e) {
                mnuPOCashActionPerformed(e);
            }
        });
    }

    private void changeUIdefaults() {
        // JXTaskPaneContainer settings (developer defaults)
      /* These are all the properties that can be set (may change with new version of SwingX)
         "TaskPaneContainer.useGradient",
         "TaskPaneContainer.background",
         "TaskPaneContainer.backgroundGradientStart",
         "TaskPaneContainer.backgroundGradientEnd",
         etc.
         */

        // setting taskpanecontainer defaults
        UIManager.put("TaskPaneContainer.useGradient", Boolean.FALSE);
        UIManager.put("TaskPaneContainer.background", Colors.LightGray.color(0.5f));

        // setting taskpane defaults
        UIManager.put("TaskPane.font", new FontUIResource(new Font("Verdana", Font.PLAIN, 16)));
        UIManager.put("TaskPane.titleBackgroundGradientStart", Colors.White.color());
        UIManager.put("TaskPane.titleBackgroundGradientEnd", Colors.LightBlue.color());


    }

    public void setConn(Connection con) {
        conn = con;
        settingDao.setConn(con);
    }

    public void setUserName(String s) {
        sUserName = s;
        
    }

    public boolean udfExistForm(JInternalFrame obj) {
        JInternalFrame ji[] = jDesktopPane1.getAllFrames();
        for (int i = 0; i < ji.length; i++) {
            //System.out.println(ji[i].getTitle());

            if (ji[i].getClass().equals(obj.getClass())) {
                try {
                    ji[i].setSelected(true);
                    return true;
                } catch (PropertyVetoException PO) {
                }
                break;
            }
        }

        return false;
    }

    private boolean udfExistForm(JInternalFrame obj, String sTitle) {
        JInternalFrame ji[] = jDesktopPane1.getAllFrames();
        for (int i = 0; i < ji.length; i++) {
            if (ji[i].getClass().equals(obj.getClass()) && ji[i].getTitle().equalsIgnoreCase(sTitle)) {
                try {
                    ji[i].setSelected(true);
                    return true;
                } catch (PropertyVetoException PO) {
                }
                break;
            }
        }

        return false;
    }

    private void udfLoadListItem() {
        if (udfExistForm(new FrmItem(conn))) {
            return;
        }
        this.setCursor(new Cursor(Cursor.WAIT_CURSOR));
        FrmItem f1 = new FrmItem(conn);
        f1.setVisible(true);
        f1.setBounds(0, 0, f1.getWidth(), f1.getHeight());
        jDesktopPane1.add(f1, javax.swing.JLayeredPane.DEFAULT_LAYER);

        try {
            f1.setMaximum(true);
            f1.setSelected(true);
            this.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
        } catch (PropertyVetoException PO) {
        }

    }

    private void udfLoadTransfer() {
        if (udfExistForm(new FrmTransferRuang())) {
            return;
        }
        this.setCursor(new Cursor(Cursor.WAIT_CURSOR));
        FrmTransferRuang f1 = new FrmTransferRuang();
        f1.setConn(conn);
        f1.setVisible(true);
        f1.setBounds(0, 0, f1.getWidth(), f1.getHeight());
        jDesktopPane1.add(f1, javax.swing.JLayeredPane.DEFAULT_LAYER);

        try {
            f1.setMaximum(true);
            f1.setSelected(true);
            this.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
        } catch (PropertyVetoException PO) {
        }

    }

    private void udfLoadTransferHistory() {
        if (udfExistForm(new FrmTransferHistory(conn))) {
            return;
        }
        this.setCursor(new Cursor(Cursor.WAIT_CURSOR));
        FrmTransferHistory f1 = new FrmTransferHistory(conn);
        f1.setVisible(true);
        f1.setBounds(0, 0, f1.getWidth(), f1.getHeight());
        jDesktopPane1.add(f1, javax.swing.JLayeredPane.DEFAULT_LAYER);

        try {
            f1.setMaximum(true);
            f1.setSelected(true);
            this.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
        } catch (PropertyVetoException PO) {
        }

    }

    private void udfLoadListCustomer() {
        if (udfExistForm(new FrmCustomer())) {
            return;
        }

        this.setCursor(new Cursor(Cursor.WAIT_CURSOR));
        FrmCustomer f1 = new FrmCustomer();
        f1.setConn(conn);
        f1.setVisible(true);
        f1.setDesktopPane(jDesktopPane1);
        f1.setBounds(0, 0, f1.getWidth(), f1.getHeight());
        jDesktopPane1.add(f1, javax.swing.JLayeredPane.DEFAULT_LAYER);

        try {
            f1.setMaximum(true);
            f1.setSelected(true);
            this.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
        } catch (PropertyVetoException PO) {
        }

    }
    private void udfLoadListDokter() {
        if (udfExistForm(new FrmDokterList())) {
            return;
        }

        this.setCursor(new Cursor(Cursor.WAIT_CURSOR));
        FrmDokterList f1 = new FrmDokterList();
        f1.setConn(conn);
        f1.setVisible(true);
        f1.setDesktopPane(jDesktopPane1);
        f1.setBounds(0, 0, f1.getWidth(), f1.getHeight());
        jDesktopPane1.add(f1, javax.swing.JLayeredPane.DEFAULT_LAYER);

        try {
            f1.setMaximum(true);
            f1.setSelected(true);
            this.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
        } catch (PropertyVetoException PO) {
        }

    }

    private void udfLoadPenjualanList() {
        if (udfExistForm(new FrmPenjualanHistory())) {
            return;
        }
        this.setCursor(new Cursor(Cursor.WAIT_CURSOR));
        FrmPenjualanHistory f1 = new FrmPenjualanHistory();
        f1.setConn(conn);
        //f1.setUserName(sUserName);
        f1.setVisible(true);
        f1.setBounds(0, 0, f1.getWidth(), f1.getHeight());
        jDesktopPane1.add(f1, javax.swing.JLayeredPane.DEFAULT_LAYER);

        try {
            f1.setMaximum(true);
            f1.setSelected(true);
            this.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
        } catch (PropertyVetoException PO) {
        }

    }

    private void udfLoadSupplier() {
        if (udfExistForm(new FrmSupplier(conn))) {
            return;
        }
        this.setCursor(new Cursor(Cursor.WAIT_CURSOR));
        FrmSupplier f1 = new FrmSupplier(conn);
        f1.setVisible(true);
        f1.setBounds(0, 0, f1.getWidth(), f1.getHeight());
        jDesktopPane1.add(f1, javax.swing.JLayeredPane.DEFAULT_LAYER);

        try {
            f1.setMaximum(true);
            f1.setSelected(true);
            this.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
        } catch (PropertyVetoException PO) {
        }
    }

    private void udfLoadReturnPembelian() {
        //TrxReturPembelian//
//        if(udfExistForm(new FrmGRReturn())) return;
//        this.setCursor(new Cursor(Cursor.WAIT_CURSOR));
//        FrmGRReturn f1=new FrmGRReturn();
//        f1.setConn(conn);
//        //f1.setUserName(sUserName);
//        f1.setVisible(true);
//        f1.setKoreksi(false);
//        f1.setBounds(0, 0, f1.getWidth(), f1.getHeight());
//        jDesktopPane1.add(f1, javax.swing.JLayeredPane.DEFAULT_LAYER);
//
//        try{
//            f1.setMaximum(true);
//            f1.setSelected(true);
//            this.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
//        } catch(PropertyVetoException PO){
//
//        }
    }

    private void udfLoadReport(String s) {
        this.setCursor(new Cursor(Cursor.WAIT_CURSOR));
        FrmRptInventory f1 = new FrmRptInventory();
        f1.setConn(conn);
        f1.udfSetFlagReport(s);
        f1.setVisible(true);
        //f1.setDesktopPane(jDesktopPane1);
        f1.setBounds(0, 0, f1.getWidth(), f1.getHeight());
        jDesktopPane1.add(f1, javax.swing.JLayeredPane.DEFAULT_LAYER);

        try {
            f1.setSelected(true);
            this.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
        } catch (PropertyVetoException PO) {
        }
    }

    private void udfLoadReportSales() {
        this.setCursor(new Cursor(Cursor.WAIT_CURSOR));
        FrmRptPenjualan f1 = new FrmRptPenjualan();
        f1.setConn(conn);
        f1.setVisible(true);
        //f1.setDesktopPane(jDesktopPane1);
        f1.setBounds(0, 0, f1.getWidth(), f1.getHeight());
        jDesktopPane1.add(f1, javax.swing.JLayeredPane.DEFAULT_LAYER);

        try {
            f1.setSelected(true);
            this.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
        } catch (PropertyVetoException PO) {
        }
    }

    private void udfLoadReportPembelian() {
        this.setCursor(new Cursor(Cursor.WAIT_CURSOR));
        pembelian.FrmRptPembelian f1 = new pembelian.FrmRptPembelian();
        f1.setConn(conn);
        f1.setVisible(true);
        //f1.setDesktopPane(jDesktopPane1);
        f1.setBounds(0, 0, f1.getWidth(), f1.getHeight());
        jDesktopPane1.add(f1, javax.swing.JLayeredPane.DEFAULT_LAYER);

        try {
            f1.setSelected(true);
            this.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
        } catch (PropertyVetoException PO) {
        }
    }

    private void udfLoadLookupGR() {
//        this.setCursor(new Cursor(Cursor.WAIT_CURSOR));
//        DlgLookupPenerimaan f1=new DlgLookupPenerimaan(this, false);
//        f1.setConn(conn);
//        f1.setIsLookup(false);
//        f1.setVisible(true);
//
//       this.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jSplitPane1 = new javax.swing.JSplitPane();
        jScrollKiri = new javax.swing.JScrollPane();
        jXTaskPaneContainer1 = new org.jdesktop.swingx.JXTaskPaneContainer();
        taskPane_trx = new org.jdesktop.swingx.JXTaskPane();
        taskpane_inventori = new org.jdesktop.swingx.JXTaskPane();
        taskpane_daftar = new org.jdesktop.swingx.JXTaskPane();
        taskpane_report = new org.jdesktop.swingx.JXTaskPane();
        jScrollDesktop = new javax.swing.JScrollPane();
        jDesktopPane1 = new apotek.JDesktopImage();
        jXStatusBar1 = new org.jdesktop.swingx.JXStatusBar();
        jXPanel1 = new org.jdesktop.swingx.JXPanel();
        lblTanggal2 = new javax.swing.JLabel();
        jXPanel2 = new org.jdesktop.swingx.JXPanel();
        lblUserName = new javax.swing.JLabel();
        jXPanel3 = new org.jdesktop.swingx.JXPanel();
        jXPanel4 = new org.jdesktop.swingx.JXPanel();
        lblTanggal = new javax.swing.JLabel();
        jXPanel5 = new org.jdesktop.swingx.JXPanel();
        lblJam = new javax.swing.JLabel();
        lblServer = new javax.swing.JLabel();
        jMenuBar2 = new javax.swing.JMenuBar();
        mnuFile = new javax.swing.JMenu();
        mnuFileUserManagement = new javax.swing.JMenuItem();
        mnuFileMenuGrouping = new javax.swing.JMenuItem();
        mnuFileMenuAuth = new javax.swing.JMenuItem();
        mnuFileDiskonPromo = new javax.swing.JMenuItem();
        jSeparator1 = new javax.swing.JSeparator();
        jMenuItemAnggota = new javax.swing.JMenuItem();
        mnuUbahPassword = new javax.swing.JMenuItem();
        jSeparator14 = new javax.swing.JPopupMenu.Separator();
        jMnItemExit = new javax.swing.JMenuItem();
        mnuMaster = new javax.swing.JMenu();
        mnuMasterBarang = new javax.swing.JMenuItem();
        mnuMasterHargaSupplier = new javax.swing.JMenuItem();
        mnuMasterGroupBarang = new javax.swing.JMenuItem();
        mnuMasterJenisBarang = new javax.swing.JMenuItem();
        mnuMasterBentuk = new javax.swing.JMenuItem();
        mnuMasterUom = new javax.swing.JMenuItem();
        jSeparator11 = new javax.swing.JPopupMenu.Separator();
        jMnuGudang = new javax.swing.JMenuItem();
        mnuLokasi = new javax.swing.JMenuItem();
        jSeparator2 = new javax.swing.JPopupMenu.Separator();
        mnuListCustomer = new javax.swing.JMenuItem();
        jSeparator8 = new javax.swing.JPopupMenu.Separator();
        mnuListSupplier = new javax.swing.JMenuItem();
        jMnuJenisSupplier = new javax.swing.JMenuItem();
        jSeparator16 = new javax.swing.JPopupMenu.Separator();
        mnuManufaktur = new javax.swing.JMenuItem();
        mnuMasterBarang1 = new javax.swing.JMenuItem();
        mnuKlinik = new javax.swing.JMenu();
        mnuKlinikReservasi = new javax.swing.JMenuItem();
        jMenuItem4 = new javax.swing.JMenuItem();
        mnuKlinikRegistrasiPasienLookup = new javax.swing.JMenuItem();
        mnuPenjualan = new javax.swing.JMenu();
        mnuTrxPenjualan = new javax.swing.JMenuItem();
        jSeparator13 = new javax.swing.JPopupMenu.Separator();
        mnuAR1 = new javax.swing.JMenuItem();
        mnuAR = new javax.swing.JMenuItem();
        jMenu3 = new javax.swing.JMenu();
        mnuJualKoreksiTrx = new javax.swing.JMenuItem();
        mnuListPenjualan = new javax.swing.JMenuItem();
        mnuListPenjualanRetur = new javax.swing.JMenuItem();
        mnuPembelian = new javax.swing.JMenu();
        mnuPR = new javax.swing.JMenuItem();
        mnuPRApproval = new javax.swing.JMenuItem();
        mnuPRMaintenance = new javax.swing.JMenuItem();
        mnuPRHistory = new javax.swing.JMenuItem();
        jSeparator12 = new javax.swing.JPopupMenu.Separator();
        mnuPO = new javax.swing.JMenuItem();
        mnuPOApproval = new javax.swing.JMenuItem();
        mnuPORevisi = new javax.swing.JMenuItem();
        mnuPOHistory = new javax.swing.JMenuItem();
        jSeparator5 = new javax.swing.JPopupMenu.Separator();
        mnuPOCash = new javax.swing.JMenuItem();
        mnuPOCashHIstory = new javax.swing.JMenuItem();
        jSeparator15 = new javax.swing.JPopupMenu.Separator();
        mnuGR = new javax.swing.JMenuItem();
        mnuGRApproval = new javax.swing.JMenuItem();
        mnuGRHistory = new javax.swing.JMenuItem();
        mnuHistoriExpiredDate = new javax.swing.JMenuItem();
        jSeparator3 = new javax.swing.JPopupMenu.Separator();
        mnuTrxBayarSupplier = new javax.swing.JMenuItem();
        mnuAPJatuhTempo = new javax.swing.JMenuItem();
        mnuPersediaan = new javax.swing.JMenu();
        jSeparator7 = new javax.swing.JSeparator();
        mnuInvSO = new javax.swing.JMenuItem();
        jSeparator9 = new javax.swing.JPopupMenu.Separator();
        mnuInvTransfer = new javax.swing.JMenuItem();
        mnuInvTransferHistory = new javax.swing.JMenuItem();
        jSeparator10 = new javax.swing.JPopupMenu.Separator();
        mnuInvReceiptUnplanned = new javax.swing.JMenuItem();
        mnuInvIssueUnplanned = new javax.swing.JMenuItem();
        mnuKeuPC = new javax.swing.JMenu();
        mnuKeuPCKeluar = new javax.swing.JMenuItem();
        mnuKeuPCMasuk = new javax.swing.JMenuItem();
        mnuKeuPCList = new javax.swing.JMenuItem();
        mnuRpt = new javax.swing.JMenu();
        mnuRptKasir = new javax.swing.JMenuItem();
        jSeparator6 = new javax.swing.JSeparator();
        mnuRptPersediaan = new javax.swing.JMenuItem();
        mnuRptPenjualan = new javax.swing.JMenuItem();
        mnuRptPembelian = new javax.swing.JMenuItem();
        mnuRptKeuangan = new javax.swing.JMenuItem();
        mnuTool = new javax.swing.JMenu();
        mnuToolsLookupItemJual = new javax.swing.JMenuItem();
        mnuToolsLookupItemBeli = new javax.swing.JMenuItem();
        mnuKalkulator = new javax.swing.JMenuItem();
        mnuHelp = new javax.swing.JMenu();
        jMenuHelpAbout = new javax.swing.JMenuItem();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Sistem Informasi Klinik | ?\n"); // NOI18N
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowOpened(java.awt.event.WindowEvent evt) {
                formWindowOpened(evt);
            }
        });

        jSplitPane1.setOneTouchExpandable(true);
        jSplitPane1.addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentResized(java.awt.event.ComponentEvent evt) {
                jSplitPane1ComponentResized(evt);
            }
        });
        jSplitPane1.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                jSplitPane1PropertyChange(evt);
            }
        });
        jSplitPane1.addAncestorListener(new javax.swing.event.AncestorListener() {
            public void ancestorMoved(javax.swing.event.AncestorEvent evt) {
                jSplitPane1AncestorMoved(evt);
            }
            public void ancestorAdded(javax.swing.event.AncestorEvent evt) {
                jSplitPane1AncestorAdded(evt);
            }
            public void ancestorRemoved(javax.swing.event.AncestorEvent evt) {
            }
        });

        taskPane_trx.setTitle("Transaksi");
        jXTaskPaneContainer1.add(taskPane_trx);

        taskpane_inventori.setScrollOnExpand(true);
        taskpane_inventori.setTitle("Inventory");
        jXTaskPaneContainer1.add(taskpane_inventori);

        taskpane_daftar.setTitle("Daftar");
        jXTaskPaneContainer1.add(taskpane_daftar);

        taskpane_report.setTitle("Laporan");
        jXTaskPaneContainer1.add(taskpane_report);

        jScrollKiri.setViewportView(jXTaskPaneContainer1);

        jSplitPane1.setLeftComponent(jScrollKiri);

        jScrollDesktop.setViewportView(jDesktopPane1);

        jSplitPane1.setRightComponent(jScrollDesktop);

        lblTanggal2.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lblTanggal2.setText("User login :");

        javax.swing.GroupLayout jXPanel1Layout = new javax.swing.GroupLayout(jXPanel1);
        jXPanel1.setLayout(jXPanel1Layout);
        jXPanel1Layout.setHorizontalGroup(
            jXPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 100, Short.MAX_VALUE)
            .addGroup(jXPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addComponent(lblTanggal2, javax.swing.GroupLayout.DEFAULT_SIZE, 100, Short.MAX_VALUE))
        );
        jXPanel1Layout.setVerticalGroup(
            jXPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 20, Short.MAX_VALUE)
            .addGroup(jXPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addComponent(lblTanggal2, javax.swing.GroupLayout.DEFAULT_SIZE, 20, Short.MAX_VALUE))
        );

        jXStatusBar1.add(jXPanel1);

        jXPanel2.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        lblUserName.setText("XXXXXXXXXXXXXXXXXXXXXXXX");

        javax.swing.GroupLayout jXPanel2Layout = new javax.swing.GroupLayout(jXPanel2);
        jXPanel2.setLayout(jXPanel2Layout);
        jXPanel2Layout.setHorizontalGroup(
            jXPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 278, Short.MAX_VALUE)
            .addGroup(jXPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(jXPanel2Layout.createSequentialGroup()
                    .addGap(0, 67, Short.MAX_VALUE)
                    .addComponent(lblUserName)
                    .addGap(0, 67, Short.MAX_VALUE)))
        );
        jXPanel2Layout.setVerticalGroup(
            jXPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 16, Short.MAX_VALUE)
            .addGroup(jXPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(jXPanel2Layout.createSequentialGroup()
                    .addGap(0, 1, Short.MAX_VALUE)
                    .addComponent(lblUserName)
                    .addGap(0, 0, Short.MAX_VALUE)))
        );

        jXStatusBar1.add(jXPanel2);

        javax.swing.GroupLayout jXPanel3Layout = new javax.swing.GroupLayout(jXPanel3);
        jXPanel3.setLayout(jXPanel3Layout);
        jXPanel3Layout.setHorizontalGroup(
            jXPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 100, Short.MAX_VALUE)
        );
        jXPanel3Layout.setVerticalGroup(
            jXPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 20, Short.MAX_VALUE)
        );

        jXStatusBar1.add(jXPanel3);

        jXPanel4.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        lblTanggal.setText("20/01/2011");

        javax.swing.GroupLayout jXPanel4Layout = new javax.swing.GroupLayout(jXPanel4);
        jXPanel4.setLayout(jXPanel4Layout);
        jXPanel4Layout.setHorizontalGroup(
            jXPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 89, Short.MAX_VALUE)
            .addGroup(jXPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addComponent(lblTanggal, javax.swing.GroupLayout.DEFAULT_SIZE, 89, Short.MAX_VALUE))
        );
        jXPanel4Layout.setVerticalGroup(
            jXPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 27, Short.MAX_VALUE)
            .addGroup(jXPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jXPanel4Layout.createSequentialGroup()
                    .addComponent(lblTanggal, javax.swing.GroupLayout.PREFERRED_SIZE, 16, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
        );

        jXStatusBar1.add(jXPanel4);

        jXPanel5.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        lblJam.setText("11:09:01");

        javax.swing.GroupLayout jXPanel5Layout = new javax.swing.GroupLayout(jXPanel5);
        jXPanel5.setLayout(jXPanel5Layout);
        jXPanel5Layout.setHorizontalGroup(
            jXPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 100, Short.MAX_VALUE)
            .addGroup(jXPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addComponent(lblJam, javax.swing.GroupLayout.DEFAULT_SIZE, 100, Short.MAX_VALUE))
        );
        jXPanel5Layout.setVerticalGroup(
            jXPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 27, Short.MAX_VALUE)
            .addGroup(jXPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jXPanel5Layout.createSequentialGroup()
                    .addComponent(lblJam, javax.swing.GroupLayout.PREFERRED_SIZE, 16, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
        );

        jXStatusBar1.add(jXPanel5);

        lblServer.setText("11:09:01");
        jXStatusBar1.add(lblServer);

        mnuFile.setMnemonic('1');
        mnuFile.setText("1. File");

        mnuFileUserManagement.setText("Pengguna");
        mnuFileUserManagement.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuFileUserManagementActionPerformed(evt);
            }
        });
        mnuFile.add(mnuFileUserManagement);

        mnuFileMenuGrouping.setText("Menu Grouping");
        mnuFileMenuGrouping.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuFileMenuGroupingActionPerformed(evt);
            }
        });
        mnuFile.add(mnuFileMenuGrouping);

        mnuFileMenuAuth.setText("Otorisasi Menu");
        mnuFileMenuAuth.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuFileMenuAuthActionPerformed(evt);
            }
        });
        mnuFile.add(mnuFileMenuAuth);

        mnuFileDiskonPromo.setText("Diskon & Promo");
        mnuFileDiskonPromo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuFileDiskonPromoActionPerformed(evt);
            }
        });
        mnuFile.add(mnuFileDiskonPromo);
        mnuFile.add(jSeparator1);

        jMenuItemAnggota.setText("Login");
        jMenuItemAnggota.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemAnggotaActionPerformed(evt);
            }
        });
        mnuFile.add(jMenuItemAnggota);

        mnuUbahPassword.setText("Ubah Password");
        mnuUbahPassword.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuUbahPasswordActionPerformed(evt);
            }
        });
        mnuFile.add(mnuUbahPassword);
        mnuFile.add(jSeparator14);

        jMnItemExit.setText("Keluar");
        jMnItemExit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMnItemExitActionPerformed(evt);
            }
        });
        mnuFile.add(jMnItemExit);

        jMenuBar2.add(mnuFile);

        mnuMaster.setMnemonic('2');
        mnuMaster.setText("2. Master");
        mnuMaster.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuMasterActionPerformed(evt);
            }
        });

        mnuMasterBarang.setMnemonic('b');
        mnuMasterBarang.setText("Item/ Barang");
        mnuMasterBarang.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuMasterBarangActionPerformed(evt);
            }
        });
        mnuMaster.add(mnuMasterBarang);

        mnuMasterHargaSupplier.setText("Master Harga Supplier");
        mnuMasterHargaSupplier.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuMasterHargaSupplierActionPerformed(evt);
            }
        });
        mnuMaster.add(mnuMasterHargaSupplier);

        mnuMasterGroupBarang.setText("Group Barang");
        mnuMasterGroupBarang.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuMasterGroupBarangActionPerformed(evt);
            }
        });
        mnuMaster.add(mnuMasterGroupBarang);

        mnuMasterJenisBarang.setMnemonic('n');
        mnuMasterJenisBarang.setText("Jenis Barang");
        mnuMasterJenisBarang.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuMasterJenisBarangActionPerformed(evt);
            }
        });
        mnuMaster.add(mnuMasterJenisBarang);

        mnuMasterBentuk.setText("Bentuk Barang");
        mnuMasterBentuk.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuMasterBentukActionPerformed(evt);
            }
        });
        mnuMaster.add(mnuMasterBentuk);

        mnuMasterUom.setMnemonic('o');
        mnuMasterUom.setText("Satuan");
        mnuMasterUom.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuMasterUomActionPerformed(evt);
            }
        });
        mnuMaster.add(mnuMasterUom);
        mnuMaster.add(jSeparator11);

        jMnuGudang.setMnemonic('g');
        jMnuGudang.setText("Gudang");
        jMnuGudang.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMnuGudangActionPerformed(evt);
            }
        });
        mnuMaster.add(jMnuGudang);

        mnuLokasi.setText("Lokasi Barang");
        mnuLokasi.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuLokasiActionPerformed(evt);
            }
        });
        mnuMaster.add(mnuLokasi);
        mnuMaster.add(jSeparator2);

        mnuListCustomer.setText("Pasien");
        mnuListCustomer.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuListCustomerActionPerformed(evt);
            }
        });
        mnuMaster.add(mnuListCustomer);
        mnuMaster.add(jSeparator8);

        mnuListSupplier.setText("Supplier");
        mnuListSupplier.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuListSupplierActionPerformed(evt);
            }
        });
        mnuMaster.add(mnuListSupplier);

        jMnuJenisSupplier.setMnemonic('j');
        jMnuJenisSupplier.setText("Jenis Supplier");
        jMnuJenisSupplier.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMnuJenisSupplierActionPerformed(evt);
            }
        });
        mnuMaster.add(jMnuJenisSupplier);
        mnuMaster.add(jSeparator16);

        mnuManufaktur.setMnemonic('j');
        mnuManufaktur.setText("Manufaktur");
        mnuManufaktur.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuManufakturActionPerformed(evt);
            }
        });
        mnuMaster.add(mnuManufaktur);

        mnuMasterBarang1.setMnemonic('b');
        mnuMasterBarang1.setText("Dokter");
        mnuMasterBarang1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuMasterBarang1ActionPerformed(evt);
            }
        });
        mnuMaster.add(mnuMasterBarang1);

        jMenuBar2.add(mnuMaster);

        mnuKlinik.setMnemonic('3');
        mnuKlinik.setText("3. Klinik");

        mnuKlinikReservasi.setText("Reservasi");
        mnuKlinikReservasi.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuKlinikReservasiActionPerformed(evt);
            }
        });
        mnuKlinik.add(mnuKlinikReservasi);

        jMenuItem4.setText("Daftar Registrasi Pasien");
        jMenuItem4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem4ActionPerformed(evt);
            }
        });
        mnuKlinik.add(jMenuItem4);

        mnuKlinikRegistrasiPasienLookup.setText("Pencarian Pasien");
        mnuKlinikRegistrasiPasienLookup.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuKlinikRegistrasiPasienLookupActionPerformed(evt);
            }
        });
        mnuKlinik.add(mnuKlinikRegistrasiPasienLookup);

        jMenuBar2.add(mnuKlinik);

        mnuPenjualan.setMnemonic('4');
        mnuPenjualan.setText("4. Penjualan");

        mnuTrxPenjualan.setText("Transaksi Penjualan");
        mnuTrxPenjualan.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuTrxPenjualanjMenuItem1ActionPerformed(evt);
            }
        });
        mnuPenjualan.add(mnuTrxPenjualan);
        mnuPenjualan.add(jSeparator13);

        mnuAR1.setText("Retur Penjualan");
        mnuAR1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuAR1ActionPerformed1(evt);
            }
        });
        mnuPenjualan.add(mnuAR1);

        mnuAR.setText("Pembayaran Piutang");
        mnuAR.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuARActionPerformed1(evt);
            }
        });
        mnuPenjualan.add(mnuAR);

        jMenu3.setText("Koreksi");

        mnuJualKoreksiTrx.setText("Koreksi Penjualan");
        mnuJualKoreksiTrx.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuJualKoreksiTrxActionPerformed(evt);
            }
        });
        jMenu3.add(mnuJualKoreksiTrx);

        mnuPenjualan.add(jMenu3);

        mnuListPenjualan.setText("Histori Penjualan");
        mnuListPenjualan.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuListPenjualanActionPerformed(evt);
            }
        });
        mnuPenjualan.add(mnuListPenjualan);

        mnuListPenjualanRetur.setText("Histori Retur Penjualan");
        mnuListPenjualanRetur.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuListPenjualanReturActionPerformed(evt);
            }
        });
        mnuPenjualan.add(mnuListPenjualanRetur);

        jMenuBar2.add(mnuPenjualan);

        mnuPembelian.setMnemonic('5');
        mnuPembelian.setText("5. Pembelian");

        mnuPR.setText("Purchase Requistion (PR)");
        mnuPR.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuPRActionPerformed(evt);
            }
        });
        mnuPembelian.add(mnuPR);

        mnuPRApproval.setText("PR Approval");
        mnuPRApproval.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuPRApprovalActionPerformed(evt);
            }
        });
        mnuPembelian.add(mnuPRApproval);

        mnuPRMaintenance.setText("PR Maintenance");
        mnuPRMaintenance.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuPRMaintenanceActionPerformed(evt);
            }
        });
        mnuPembelian.add(mnuPRMaintenance);

        mnuPRHistory.setText("PR History");
        mnuPRHistory.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuPRHistoryActionPerformed(evt);
            }
        });
        mnuPembelian.add(mnuPRHistory);
        mnuPembelian.add(jSeparator12);

        mnuPO.setText("Purchase Order - PO");
        mnuPO.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuPOActionPerformed(evt);
            }
        });
        mnuPembelian.add(mnuPO);

        mnuPOApproval.setText("PO Approval");
        mnuPOApproval.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuPOApprovalActionPerformed(evt);
            }
        });
        mnuPembelian.add(mnuPOApproval);

        mnuPORevisi.setText("Revisi PO");
        mnuPORevisi.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuPORevisiActionPerformed(evt);
            }
        });
        mnuPembelian.add(mnuPORevisi);

        mnuPOHistory.setText("PO History");
        mnuPOHistory.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuPOHistoryActionPerformed(evt);
            }
        });
        mnuPembelian.add(mnuPOHistory);
        mnuPembelian.add(jSeparator5);

        mnuPOCash.setText("PO Cash");
        mnuPOCash.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuPOCashActionPerformed(evt);
            }
        });
        mnuPembelian.add(mnuPOCash);

        mnuPOCashHIstory.setText("PO Cash History");
        mnuPOCashHIstory.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuPOCashHIstoryActionPerformed(evt);
            }
        });
        mnuPembelian.add(mnuPOCashHIstory);
        mnuPembelian.add(jSeparator15);

        mnuGR.setText("Penerimaan Harang");
        mnuGR.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuGRActionPerformed(evt);
            }
        });
        mnuPembelian.add(mnuGR);

        mnuGRApproval.setText("Persetujuan Penerimaan Barang");
        mnuGRApproval.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuGRApprovalActionPerformed(evt);
            }
        });
        mnuPembelian.add(mnuGRApproval);

        mnuGRHistory.setText("Histori Penerimaan Barang");
        mnuGRHistory.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuGRHistoryActionPerformed(evt);
            }
        });
        mnuPembelian.add(mnuGRHistory);

        mnuHistoriExpiredDate.setText("Histori Expired Date - Good Receipt");
        mnuHistoriExpiredDate.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuHistoriExpiredDatejMenuItem1ActionPerformed(evt);
            }
        });
        mnuPembelian.add(mnuHistoriExpiredDate);
        mnuPembelian.add(jSeparator3);

        mnuTrxBayarSupplier.setText("Pembayaran Hutang ke Supplier");
        mnuTrxBayarSupplier.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuTrxBayarSupplierjMenuItem1ActionPerformed(evt);
            }
        });
        mnuPembelian.add(mnuTrxBayarSupplier);

        mnuAPJatuhTempo.setText("Hutang Jatuh Tempo");
        mnuAPJatuhTempo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuAPJatuhTempojMenuItem1ActionPerformed(evt);
            }
        });
        mnuPembelian.add(mnuAPJatuhTempo);

        jMenuBar2.add(mnuPembelian);

        mnuPersediaan.setMnemonic('6');
        mnuPersediaan.setText("6. Persediaan");
        mnuPersediaan.add(jSeparator7);

        mnuInvSO.setText("Stok Opname");
        mnuInvSO.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuInvSOActionPerformed1(evt);
            }
        });
        mnuPersediaan.add(mnuInvSO);
        mnuPersediaan.add(jSeparator9);

        mnuInvTransfer.setText("Transfer Barang");
        mnuInvTransfer.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuInvTransferActionPerformed1(evt);
            }
        });
        mnuPersediaan.add(mnuInvTransfer);

        mnuInvTransferHistory.setText("Histori Transfer Barang");
        mnuInvTransferHistory.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuInvTransferHistoryActionPerformed1(evt);
            }
        });
        mnuPersediaan.add(mnuInvTransferHistory);
        mnuPersediaan.add(jSeparator10);

        mnuInvReceiptUnplanned.setText("Penerimaan Barang - Lain");
        mnuInvReceiptUnplanned.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuInvReceiptUnplannedActionPerformed(evt);
            }
        });
        mnuPersediaan.add(mnuInvReceiptUnplanned);

        mnuInvIssueUnplanned.setText("Pengeluaran Barang - Lain");
        mnuInvIssueUnplanned.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuInvIssueUnplannedActionPerformed(evt);
            }
        });
        mnuPersediaan.add(mnuInvIssueUnplanned);

        jMenuBar2.add(mnuPersediaan);

        mnuKeuPC.setMnemonic('7');
        mnuKeuPC.setText("7. Petty Cash");

        mnuKeuPCKeluar.setText("Kas Keluar");
        mnuKeuPCKeluar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuKeuPCKeluarActionPerformed(evt);
            }
        });
        mnuKeuPC.add(mnuKeuPCKeluar);

        mnuKeuPCMasuk.setText("Kas Masuk");
        mnuKeuPCMasuk.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuKeuPCMasukActionPerformed(evt);
            }
        });
        mnuKeuPC.add(mnuKeuPCMasuk);

        mnuKeuPCList.setText("Daftar Petty Cash");
        mnuKeuPCList.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuKeuPCListActionPerformed(evt);
            }
        });
        mnuKeuPC.add(mnuKeuPCList);

        jMenuBar2.add(mnuKeuPC);

        mnuRpt.setMnemonic('8');
        mnuRpt.setText("8. Laporan");

        mnuRptKasir.setText("Kasir");
        mnuRptKasir.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuRptKasirjMenuItem1ActionPerformed(evt);
            }
        });
        mnuRpt.add(mnuRptKasir);
        mnuRpt.add(jSeparator6);

        mnuRptPersediaan.setText("Persediaan");
        mnuRptPersediaan.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuRptPersediaanjMenuItem1ActionPerformed(evt);
            }
        });
        mnuRpt.add(mnuRptPersediaan);

        mnuRptPenjualan.setText("Laporan Penjualan");
        mnuRptPenjualan.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuRptPenjualanjMenuItem1ActionPerformed(evt);
            }
        });
        mnuRpt.add(mnuRptPenjualan);

        mnuRptPembelian.setText("Laporan Pembelian");
        mnuRptPembelian.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuRptPembelianjMenuItem1ActionPerformed(evt);
            }
        });
        mnuRpt.add(mnuRptPembelian);

        mnuRptKeuangan.setText("Laporan Keuangan");
        mnuRptKeuangan.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuRptKeuanganjMenuItem1ActionPerformed(evt);
            }
        });
        mnuRpt.add(mnuRptKeuangan);

        jMenuBar2.add(mnuRpt);

        mnuTool.setMnemonic('9');
        mnuTool.setText("9. Alat");
        mnuTool.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuToolActionPerformed(evt);
            }
        });

        mnuToolsLookupItemJual.setText("Lookup Barang by Harga Jual");
        mnuToolsLookupItemJual.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuToolsLookupItemJualjMenuItem1ActionPerformed(evt);
            }
        });
        mnuTool.add(mnuToolsLookupItemJual);

        mnuToolsLookupItemBeli.setText("Lookup Barang by Beli Supplier");
        mnuToolsLookupItemBeli.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuToolsLookupItemBelijMenuItem1ActionPerformed(evt);
            }
        });
        mnuTool.add(mnuToolsLookupItemBeli);

        mnuKalkulator.setText("Kalkulator");
        mnuKalkulator.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuKalkulatorActionPerformed(evt);
            }
        });
        mnuTool.add(mnuKalkulator);

        jMenuBar2.add(mnuTool);

        mnuHelp.setMnemonic('?');
        mnuHelp.setText("Help ?");

        jMenuHelpAbout.setMnemonic('A');
        jMenuHelpAbout.setText("About");
        jMenuHelpAbout.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuHelpAboutjMenuItem1ActionPerformed(evt);
            }
        });
        mnuHelp.add(jMenuHelpAbout);

        jMenuBar2.add(mnuHelp);

        setJMenuBar(jMenuBar2);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jXStatusBar1, javax.swing.GroupLayout.DEFAULT_SIZE, 1000, Short.MAX_VALUE)
            .addComponent(jSplitPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 1006, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addComponent(jSplitPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 369, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jXStatusBar1, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        setSize(new java.awt.Dimension(1016, 465));
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void formWindowOpened(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowOpened
        this.setExtendedState(MAXIMIZED_BOTH);
        
        sKodeGudang = sc.getSite_Id();

        try {
            ResultSet rs = conn.createStatement().executeQuery("select coalesce(deskripsi,'') as nama_gudang, now() from gudang where kode_gudang='" + sc.getSite_Id() + "'");
            c = Calendar.getInstance();
            if (rs.next()) {
                sNamaGudang = rs.getString("nama_gudang");
                c.setTime(rs.getTimestamp(2));
            }
            rs.close();
            rs=conn.createStatement().executeQuery("select nama_klinik,\n" +
                    "  alamat,\n" +
                    "  telepon,\n" +
                    "  apoteker,\n" +
                    "  coalesce(catatan_kwt,'') as catatan_kwt,\n" +
                    "  sip_apoteker from m_setting");
            if(rs.next()){
                sNamaUsaha=rs.getString("nama_klinik");
                sAlamat=rs.getString("alamat");
                sTelp=rs.getString("telepon");
                sApoteker = rs.getString("apoteker");
                sSipApoteker = rs.getString("sip_apoteker");
                sCatatanKwt = rs.getString("catatan_kwt");
            }
            rs.close();
            setting=settingDao.getSetting();
            lblUserName.setText(sUserName+" - "+sNamaDokter);
            
        } catch (SQLException se) {
            JOptionPane.showMessageDialog(this, se.getMessage());
        }
        setTitle("Sistem Informasi Klinik - "+setting.getNamaKlinik());
        timer = new Timer();
        timer.schedule(new DoTick(), 0, 1000);
    }//GEN-LAST:event_formWindowOpened
    private Calendar c;
    private SimpleDateFormat dmy = new SimpleDateFormat("dd/MM/yyyy");
    private SimpleDateFormat hms = new SimpleDateFormat("hh:mm:ss");

    private void udfLoadSO() {
        StockOpname s1 = new StockOpname();
        s1.setKoreksi(false);
        s1.setConn(conn);
        s1.setVisible(true);
    }

    public void setShift(String string) {
        this.sShift = string;
    }

    public void setUserID(String string) {
        this.sUserID = string;
    }

    private void udfLoadBentuk() {
        this.setCursor(new Cursor(Cursor.WAIT_CURSOR));
        FrmBentuk f1 = new FrmBentuk();
        f1.setConn(conn);
        f1.setVisible(true);
        //f1.setDesktopPane(jDesktopPane1);
        f1.setBounds(0, 0, f1.getWidth(), f1.getHeight());
        jDesktopPane1.add(f1, javax.swing.JLayeredPane.DEFAULT_LAYER);

        try {
            f1.setSelected(true);
            this.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
        } catch (PropertyVetoException PO) {
        }
    }

    private void udfLoadGroup() {
        this.setCursor(new Cursor(Cursor.WAIT_CURSOR));
        FrmGroup f1 = new FrmGroup();
        f1.setConn(conn);
        f1.setVisible(true);
        //f1.setDesktopPane(jDesktopPane1);
        f1.setBounds(0, 0, f1.getWidth(), f1.getHeight());
        jDesktopPane1.add(f1, javax.swing.JLayeredPane.DEFAULT_LAYER);

        try {
            f1.setSelected(true);
            this.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
        } catch (PropertyVetoException PO) {
        }
    }

    class DoTick extends TimerTask {

        @Override
        public void run() {
            //c = Calendar.getInstance();
            c.add(Calendar.SECOND, 1);
            lblTanggal.setText(dmy.format(c.getTime()));
            lblJam.setText(hms.format(c.getTime()));
        }
    }

    private void jSplitPane1AncestorMoved(javax.swing.event.AncestorEvent evt) {//GEN-FIRST:event_jSplitPane1AncestorMoved
    }//GEN-LAST:event_jSplitPane1AncestorMoved

    private void jSplitPane1AncestorAdded(javax.swing.event.AncestorEvent evt) {//GEN-FIRST:event_jSplitPane1AncestorAdded
        // TODO add your handling code here:
    }//GEN-LAST:event_jSplitPane1AncestorAdded

    private void jSplitPane1ComponentResized(java.awt.event.ComponentEvent evt) {//GEN-FIRST:event_jSplitPane1ComponentResized
        udfSetTopLeft();
    }//GEN-LAST:event_jSplitPane1ComponentResized

    private void jSplitPane1PropertyChange(java.beans.PropertyChangeEvent evt) {//GEN-FIRST:event_jSplitPane1PropertyChange
        udfSetTopLeft();
    }//GEN-LAST:event_jSplitPane1PropertyChange

//    private void udfSetTopLeft() {
//        iLeft=this.getX()+ jSplitPane1.getX()+jSplitPane1.getRightComponent().getX()+ jDesktopPane1.getX()-7;
//        iTop=this.getY()+ jSplitPane1.getY()+ jDesktopPane1.getY();
//    }
    private void udfSetTopLeft() {
//        iLeft=this.getX()+jSplitPane1.getX()+jSplitPane1.getLeftComponent().getWidth()+ jDesktopPane1.getX()+5;
//        iTop=this.getY()+ jSplitPane1.getY()+ jDesktopPane1.getY()+31;
    }

    private void mnuRptPenjualanjMenuItem1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuRptPenjualanjMenuItem1ActionPerformed
        udfLoadReportSales();

}//GEN-LAST:event_mnuRptPenjualanjMenuItem1ActionPerformed

    private void jMenuHelpAboutjMenuItem1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuHelpAboutjMenuItem1ActionPerformed
        new About(this, true).setVisible(true);
}//GEN-LAST:event_jMenuHelpAboutjMenuItem1ActionPerformed

    private void mnuRptPersediaanjMenuItem1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuRptPersediaanjMenuItem1ActionPerformed
        udfLoadReport("persediaan");
}//GEN-LAST:event_mnuRptPersediaanjMenuItem1ActionPerformed

    private void mnuRptPembelianjMenuItem1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuRptPembelianjMenuItem1ActionPerformed
        udfLoadReportPembelian();


}//GEN-LAST:event_mnuRptPembelianjMenuItem1ActionPerformed

private void mnuMasterActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuMasterActionPerformed
}//GEN-LAST:event_mnuMasterActionPerformed

private void jMnItemExitActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMnItemExitActionPerformed
    if (JOptionPane.showConfirmDialog(this, "Anda yakin untuk keluar", "Keluar Program", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
        System.exit(0);
    }
}//GEN-LAST:event_jMnItemExitActionPerformed

private void mnuInvSOActionPerformed1(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuInvSOActionPerformed1
    udfLoadSO();

}//GEN-LAST:event_mnuInvSOActionPerformed1

private void mnuInvTransferActionPerformed1(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuInvTransferActionPerformed1
    udfLoadTransfer();
}//GEN-LAST:event_mnuInvTransferActionPerformed1

private void mnuTrxPenjualanjMenuItem1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuTrxPenjualanjMenuItem1ActionPerformed
    penjualan.FrmPenjualan trx = new penjualan.FrmPenjualan();
    trx.setTitle("Penjualan");
    trx.setIconImage(getIconImage());
    trx.setDesktopPane(jDesktopPane1);
    trx.setConn(conn);
    trx.setState(Frame.MAXIMIZED_BOTH);
    trx.setVisible(true);
}//GEN-LAST:event_mnuTrxPenjualanjMenuItem1ActionPerformed

private void mnuListSupplierActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuListSupplierActionPerformed
    udfLoadSupplier();
}//GEN-LAST:event_mnuListSupplierActionPerformed

private void mnuListPenjualanActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuListPenjualanActionPerformed
    udfLoadPenjualanList();
}//GEN-LAST:event_mnuListPenjualanActionPerformed

private void mnuToolsLookupItemJualjMenuItem1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuToolsLookupItemJualjMenuItem1ActionPerformed
    DlgLookupItemJual dlgLookupItem = new DlgLookupItemJual(this, true);
    dlgLookupItem.setConn(conn);
    dlgLookupItem.setVisible(true);
}//GEN-LAST:event_mnuToolsLookupItemJualjMenuItem1ActionPerformed

private void mnuMasterHargaSupplierActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuMasterHargaSupplierActionPerformed
    udfLoadMasterPriceByProduct();
}//GEN-LAST:event_mnuMasterHargaSupplierActionPerformed

private void mnuListCustomerActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuListCustomerActionPerformed
    udfLoadListCustomer();
}//GEN-LAST:event_mnuListCustomerActionPerformed

private void mnuARActionPerformed1(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuARActionPerformed1
    udfLoadAR();
}//GEN-LAST:event_mnuARActionPerformed1

private void mnuPOjMenuItem1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuPOjMenuItem1ActionPerformed
    udfLoadPO();
}//GEN-LAST:event_mnuPOjMenuItem1ActionPerformed

private void mnuAR1ActionPerformed1(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuAR1ActionPerformed1
    TrxReturPenjualan trx = new TrxReturPenjualan();
    trx.setConn(conn);
    //trx.setState(Frame.MAXIMIZED_BOTH);
    trx.setVisible(true);
}//GEN-LAST:event_mnuAR1ActionPerformed1

private void mnuJualKoreksiTrxActionPerformed1(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuJualKoreksiTrxActionPerformed1
    FrmPenjualan2 frm = new FrmPenjualan2();
    frm.setConn(conn);
    frm.setFlagKoreksi(true);
    //frm.setNoTrx(jXTable1.getValueAt(iRow, 0).toString());
    frm.setVisible(true);
}//GEN-LAST:event_mnuJualKoreksiTrxActionPerformed1

private void mnuToolsLookupItemBelijMenuItem1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuToolsLookupItemBelijMenuItem1ActionPerformed
    DlgLookupItemBeli dlgLookupItem = new DlgLookupItemBeli(this, true);
    dlgLookupItem.setConn(conn);
    dlgLookupItem.setVisible(true);
}//GEN-LAST:event_mnuToolsLookupItemBelijMenuItem1ActionPerformed

private void mnuListPenjualanReturActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuListPenjualanReturActionPerformed
    FrmReturPenjualanHistory f1 = new FrmReturPenjualanHistory(conn);
    if (udfExistForm(f1)) {
        f1.dispose();
        return;
    }
    //f1.setConn(conn);
    f1.setBounds(0, 0, f1.getWidth(), f1.getHeight());
    jDesktopPane1.add(f1, javax.swing.JLayeredPane.DEFAULT_LAYER);
    f1.setVisible(true);
    f1.setMainForm(this);
    try {
        f1.setSelected(true);
        this.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
    } catch (PropertyVetoException PO) {
    }
}//GEN-LAST:event_mnuListPenjualanReturActionPerformed

private void mnuKalkulatorActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuKalkulatorActionPerformed
    this.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
    Runtime rt = Runtime.getRuntime();
    try {

        rt.exec(new String[]{"cmd", "/c", "start calc"});
    } catch (Exception e) {
        JOptionPane.showMessageDialog(this, e.getMessage());
        e.printStackTrace();
    }
}//GEN-LAST:event_mnuKalkulatorActionPerformed

private void mnuLokasiActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuLokasiActionPerformed
//    FrmLokasi f1=new FrmLokasi();
//    if(udfExistForm(f1)){
//        f1.dispose();
//        return;
//    }
//    f1.setConn(conn);
//    f1.setBounds(0, 0, f1.getWidth(), f1.getHeight());
//    jDesktopPane1.add(f1, javax.swing.JLayeredPane.DEFAULT_LAYER);
//    f1.setVisible(true);
//    //f1.setMainForm(this);
//    try{
//        f1.setSelected(true);
//        this.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
//    } catch(PropertyVetoException PO){
//
//    }
}//GEN-LAST:event_mnuLokasiActionPerformed

private void mnuTrxBayarSupplierjMenuItem1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuTrxBayarSupplierjMenuItem1ActionPerformed
    udfLoadAP();
}//GEN-LAST:event_mnuTrxBayarSupplierjMenuItem1ActionPerformed

private void mnuAPJatuhTempojMenuItem1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuAPJatuhTempojMenuItem1ActionPerformed
    udfLoadAPJtTempo();
}//GEN-LAST:event_mnuAPJatuhTempojMenuItem1ActionPerformed

private void mnuInvTransferHistoryActionPerformed1(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuInvTransferHistoryActionPerformed1
    udfLoadTransferHistory();
}//GEN-LAST:event_mnuInvTransferHistoryActionPerformed1

private void mnuMasterBarangActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuMasterBarangActionPerformed
    FrmItemDummyList fList = new FrmItemDummyList();
    if (udfExistForm(fList)) {
        fList.dispose();
        return;
    }
    fList.setConn(conn);
    fList.setMainForm(this);
    fList.setVisible(true);
    fList.setBounds(0, 0, fList.getWidth(), fList.getHeight());
    jDesktopPane1.add(fList, javax.swing.JLayeredPane.DEFAULT_LAYER);

    try {
        fList.setSelected(true);
    } catch (PropertyVetoException PO) {
    }

}//GEN-LAST:event_mnuMasterBarangActionPerformed

private void jMnuGudangActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMnuGudangActionPerformed
    if (udfExistForm(new FrmGudang())) {
        return;
    }
    FrmGudang f1 = new FrmGudang();
    f1.setConn(conn);
    f1.setBounds(0, 0, f1.getWidth(), f1.getHeight());
    f1.setVisible(true);
    jDesktopPane1.add(f1, javax.swing.JLayeredPane.DEFAULT_LAYER);
    try {
        f1.setMaximizable(true);
        f1.setSelected(true);
    } catch (PropertyVetoException PO) {
    }

}//GEN-LAST:event_jMnuGudangActionPerformed

private void jMnuJenisSupplierActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMnuJenisSupplierActionPerformed
    if (udfExistForm(new FrmJenisSupplier(conn))) {
        return;
    }
    FrmJenisSupplier f1 = new FrmJenisSupplier(conn);
    f1.setBounds(0, 0, f1.getWidth(), f1.getHeight());
    f1.setVisible(true);
    jDesktopPane1.add(f1, javax.swing.JLayeredPane.DEFAULT_LAYER);
    try {
        f1.setMaximizable(true);
        f1.setSelected(true);
    } catch (PropertyVetoException PO) {
    }

}//GEN-LAST:event_jMnuJenisSupplierActionPerformed

private void mnuMasterJenisBarangActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuMasterJenisBarangActionPerformed
    if (udfExistForm(new FrmJenisBarang(conn))) {
        return;
    }
    FrmJenisBarang f1 = new FrmJenisBarang(conn);
    f1.setBounds(0, 0, f1.getWidth(), f1.getHeight());
    f1.setVisible(true);
    jDesktopPane1.add(f1, javax.swing.JLayeredPane.DEFAULT_LAYER);
    try {
        f1.setMaximizable(true);
        f1.setSelected(true);
    } catch (PropertyVetoException PO) {
    }

}//GEN-LAST:event_mnuMasterJenisBarangActionPerformed

private void mnuMasterUomActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuMasterUomActionPerformed
    if (udfExistForm(new FrmUOM(conn))) {
        return;
    }
    FrmUOM f1 = new FrmUOM(conn);
    f1.setBounds(0, 0, f1.getWidth(), f1.getHeight());
    f1.setVisible(true);
    jDesktopPane1.add(f1, javax.swing.JLayeredPane.DEFAULT_LAYER);
    try {
        f1.setMaximizable(true);
        f1.setSelected(true);
    } catch (PropertyVetoException PO) {
    }

}//GEN-LAST:event_mnuMasterUomActionPerformed

private void mnuRptKasirjMenuItem1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuRptKasirjMenuItem1ActionPerformed
    udfLoadReportKasir();
}//GEN-LAST:event_mnuRptKasirjMenuItem1ActionPerformed

private void mnuUbahPasswordActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuUbahPasswordActionPerformed
    FrmPasswordChange f1 = new FrmPasswordChange(this, true);
    f1.setConn(conn);
    f1.setMainMenu(this);
    f1.setUsername(sUserName);
    //f1.setUserID(sUserID);
    f1.setVisible(true);

}//GEN-LAST:event_mnuUbahPasswordActionPerformed

    private void mnuMasterBentukActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuMasterBentukActionPerformed
        udfLoadBentuk();
    }//GEN-LAST:event_mnuMasterBentukActionPerformed

    private void mnuMasterGroupBarangActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuMasterGroupBarangActionPerformed
        udfLoadGroup();
    }//GEN-LAST:event_mnuMasterGroupBarangActionPerformed

    private void mnuManufakturActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuManufakturActionPerformed
        FrmManufaktur f1 = new FrmManufaktur();
        if (udfExistForm(f1)) {
            f1.dispose();
            return;
        }
        f1.setConn(conn);

        f1.setVisible(true);
        f1.setBounds(0, 0, f1.getWidth(), f1.getHeight());
        jDesktopPane1.add(f1, javax.swing.JLayeredPane.DEFAULT_LAYER);

        try {
            f1.setSelected(true);
        } catch (PropertyVetoException PO) {
        }

    }//GEN-LAST:event_mnuManufakturActionPerformed

    private void mnuPRActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuPRActionPerformed
         FrmPR fRpt = new FrmPR();
        if (udfExistForm(fRpt)) {
            fRpt.dispose();
            return;
        }
        fRpt.setConn(conn);

        fRpt.setVisible(true);
        fRpt.setBounds(0, 0, fRpt.getWidth(), fRpt.getHeight());
        jDesktopPane1.add(fRpt, javax.swing.JLayeredPane.DEFAULT_LAYER);

        try {
            fRpt.setSelected(true);
        } catch (PropertyVetoException PO) {
        }
    }//GEN-LAST:event_mnuPRActionPerformed

    private void mnuFileUserManagementActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuFileUserManagementActionPerformed
        FrmUserManagement f1=new FrmUserManagement();
        if(!udfExistForm(f1)){
            f1.setConn(conn);
            jDesktopPane1.add(f1);
            f1.setBounds(0, 0, f1.getWidth(), f1.getHeight());
            f1.setVisible(true);
            try{
                f1.setSelected(true);
            } catch(PropertyVetoException PO){
            }
        }else{
            f1.dispose();
        }
    }//GEN-LAST:event_mnuFileUserManagementActionPerformed

    private void mnuToolActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuToolActionPerformed
        
    }//GEN-LAST:event_mnuToolActionPerformed

    private void mnuPRMaintenanceActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuPRMaintenanceActionPerformed
        FrmPRMaintenance f1=new FrmPRMaintenance();
        if(!udfExistForm(f1)){
            f1.setConn(conn);
            f1.setDesktopPane(jDesktopPane1);
            jDesktopPane1.add(f1);
            f1.setBounds(0, 0, f1.getWidth(), f1.getHeight());
            
            f1.setVisible(true);
            try{
                f1.setSelected(true);
            } catch(PropertyVetoException PO){
            }
        }else{
            f1.dispose();
        }
    }//GEN-LAST:event_mnuPRMaintenanceActionPerformed

    private void mnuPRApprovalActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuPRApprovalActionPerformed
        FrmPRApproval f1=new FrmPRApproval(conn, true);
        if(!udfExistForm(f1, "Purchase Requisition Approval")){
            //f1.setConn(conn);
            jDesktopPane1.add(f1);
            f1.setTitle("Purchase Requisition Approval");
            f1.setCumaSingDurungApprove(true);
            f1.setBounds(0, 0, f1.getWidth(), f1.getHeight());
            f1.setVisible(true);
            try{
                f1.setSelected(true);
            } catch(PropertyVetoException PO){
            }
        }else{
            f1.dispose();
        }
    }//GEN-LAST:event_mnuPRApprovalActionPerformed

    private void mnuPOActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuPOActionPerformed
        FrmPO f1=new FrmPO();
        if(!udfExistForm(f1)){
            f1.setConn(conn);
            jDesktopPane1.add(f1);
            f1.setBounds(0, 0, f1.getWidth(), f1.getHeight());
            f1.setVisible(true);
            try{
                f1.setSelected(true);
            } catch(PropertyVetoException PO){
            }
        }else{
            f1.dispose();
        }
    }//GEN-LAST:event_mnuPOActionPerformed

    private void mnuPOCashActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuPOCashActionPerformed
        FrmPOCash f1=new FrmPOCash();
        if(!udfExistForm(f1)){
            f1.setConn(conn);
            jDesktopPane1.add(f1);
            f1.setBounds(0, 0, f1.getWidth(), f1.getHeight());
            f1.setVisible(true);
            try{
                f1.setSelected(true);
            } catch(PropertyVetoException PO){
            }
        }else{
            f1.dispose();
        }
    }//GEN-LAST:event_mnuPOCashActionPerformed

    private void mnuPOApprovalActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuPOApprovalActionPerformed
        FrmPOApproval f1=new FrmPOApproval(conn, true, false);
        if(!udfExistForm(f1, "Purchase Order Approval")){
            //f1.setConn(conn);
            jDesktopPane1.add(f1);
            f1.setTitle("Purchase Order Approval");
            f1.setBounds(0, 0, f1.getWidth(), f1.getHeight());
            f1.setVisible(true);
            try{
                f1.setSelected(true);
            } catch(PropertyVetoException PO){
            }
        }else{
            f1.dispose();
        }
    }//GEN-LAST:event_mnuPOApprovalActionPerformed

    private void mnuPOHistoryActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuPOHistoryActionPerformed
        FrmPOApproval f1=new FrmPOApproval(conn, false, true);
        if(!udfExistForm(f1, "Purchase Order History")){
            //f1.setConn(conn);
            jDesktopPane1.add(f1);
            f1.setTitle("Purchase Order History");
            f1.setCumaSingDurungApprove(false);
            f1.setCumaSingOutstanding(false);
            f1.setBounds(0, 0, f1.getWidth(), f1.getHeight());
            f1.setVisible(true);
            try{
                f1.setSelected(true);
            } catch(PropertyVetoException PO){
            }
        }else{
            f1.dispose();
        }
    }//GEN-LAST:event_mnuPOHistoryActionPerformed

    private void mnuPOCashHIstoryActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuPOCashHIstoryActionPerformed
        FrmPOCashHistory f1=new FrmPOCashHistory(conn, false);
        if(!udfExistForm(f1)){
//            f1.setConn(conn);
            jDesktopPane1.add(f1);
            f1.setCumaSingDurungApprove(false);
            f1.setBounds(0, 0, f1.getWidth(), f1.getHeight());
            f1.setVisible(true);
            try{
                f1.setSelected(true);
            } catch(PropertyVetoException PO){
            }
        }else{
            f1.dispose();
        }
    }//GEN-LAST:event_mnuPOCashHIstoryActionPerformed

    private void mnuGRActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuGRActionPerformed
        FrmGoodReceipt f1=new FrmGoodReceipt();
        if(!udfExistForm(f1)){
            f1.setConn(conn);
            jDesktopPane1.add(f1);
            f1.setBounds(0, 0, f1.getWidth(), f1.getHeight());
            f1.setVisible(true);
            try{
                f1.setSelected(true);
            } catch(PropertyVetoException PO){
            }
        }else{
            f1.dispose();
        }
    }//GEN-LAST:event_mnuGRActionPerformed

    private void mnuGRApprovalActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuGRApprovalActionPerformed
        FrmGRApproval f1=new FrmGRApproval(conn);
        if(!udfExistForm(f1)){
            jDesktopPane1.add(f1);
            f1.setBounds(0, 0, f1.getWidth(), f1.getHeight());
            f1.setVisible(true);
            try{
                f1.setSelected(true);
            } catch(PropertyVetoException PO){
            }
        }else{
            f1.dispose();
        }
    }//GEN-LAST:event_mnuGRApprovalActionPerformed

    private void mnuGRHistoryActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuGRHistoryActionPerformed
        pembelian.FrmGRHistory f1=new pembelian.FrmGRHistory(conn, false);
        if(!udfExistForm(f1)){
//            f1.setConn(conn);
            jDesktopPane1.add(f1);
            f1.setBounds(0, 0, f1.getWidth(), f1.getHeight());
            f1.setVisible(true);
            try{
                f1.setSelected(true);
            } catch(PropertyVetoException PO){
            }
        }else{
            f1.dispose();
        }
    }//GEN-LAST:event_mnuGRHistoryActionPerformed

    private void mnuPRHistoryActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuPRHistoryActionPerformed
        FrmPRApproval f1=new FrmPRApproval(conn, false);
        if(!udfExistForm(f1, "Purchase Requisition History")){
            //f1.setConn(conn);
            jDesktopPane1.add(f1);
            f1.setTitle("Purchase Requisition History");
            f1.setCumaSingDurungApprove(false);
            f1.setBounds(0, 0, f1.getWidth(), f1.getHeight());
            f1.setVisible(true);
            try{
                f1.setSelected(true);
            } catch(PropertyVetoException PO){
            }
        }else{
            f1.dispose();
        }
    }//GEN-LAST:event_mnuPRHistoryActionPerformed

    private void mnuPORevisiActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuPORevisiActionPerformed
        FrmPO f1=new FrmPO();
        jDesktopPane1.add(f1);
        f1.setConn(conn);
        f1.setTitle("Revisi Purchase Order");
        f1.setKoreksi(true);
        f1.setVisible(true);
        f1.setBounds(0, 0, f1.getWidth(), f1.getHeight());
        try{
            f1.setSelected(true);
        } catch(PropertyVetoException PO){
        }
    }//GEN-LAST:event_mnuPORevisiActionPerformed

    private void mnuJualKoreksiTrxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuJualKoreksiTrxActionPerformed
        penjualan.FrmPenjualan trx = new penjualan.FrmPenjualan();
        trx.setTitle("Koreksi Penjualan");
        trx.setKoreksi(true);
        trx.setIconImage(getIconImage());
        trx.setDesktopPane(jDesktopPane1);
        trx.setConn(conn);
        trx.setState(Frame.MAXIMIZED_BOTH);
        trx.setVisible(true);
    }//GEN-LAST:event_mnuJualKoreksiTrxActionPerformed

    private void mnuHistoriExpiredDatejMenuItem1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuHistoriExpiredDatejMenuItem1ActionPerformed
        FrmExpiredDate f1 = new FrmExpiredDate();
        if (udfExistForm(f1)) {
            f1.dispose();
            return;
        }
        f1.setConn(conn);

        f1.setVisible(true);
        f1.setBounds(0, 0, f1.getWidth(), f1.getHeight());
        jDesktopPane1.add(f1, javax.swing.JLayeredPane.DEFAULT_LAYER);

        try {
            f1.setSelected(true);
        } catch (PropertyVetoException PO) {
        }
    }//GEN-LAST:event_mnuHistoriExpiredDatejMenuItem1ActionPerformed

    private void mnuFileMenuGroupingActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuFileMenuGroupingActionPerformed
        FrmMenuGrouping f1 = new FrmMenuGrouping();
        if (udfExistForm(f1)) {
            f1.dispose();
            return;
        }
        f1.setConn(conn);

        f1.setVisible(true);
        f1.setBounds(0, 0, f1.getWidth(), f1.getHeight());
        jDesktopPane1.add(f1, javax.swing.JLayeredPane.DEFAULT_LAYER);

        try {
            f1.setSelected(true);
        } catch (PropertyVetoException PO) {
        }
    }//GEN-LAST:event_mnuFileMenuGroupingActionPerformed

    private void mnuFileMenuAuthActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuFileMenuAuthActionPerformed
        FrmSettingMenu f1 = new FrmSettingMenu();
        if (udfExistForm(f1)) {
            f1.dispose();
            return;
        }
        f1.setConn(conn);
        f1.setMainForm(this);
        f1.setVisible(true);
        f1.setBounds(0, 0, f1.getWidth(), f1.getHeight());
        jDesktopPane1.add(f1, javax.swing.JLayeredPane.DEFAULT_LAYER);

        try {
            f1.setSelected(true);
        } catch (PropertyVetoException PO) {
        }
    }//GEN-LAST:event_mnuFileMenuAuthActionPerformed

    private void mnuInvReceiptUnplannedActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuInvReceiptUnplannedActionPerformed
        FrmUnplanned f1 = new FrmUnplanned(true);
        if (udfExistForm(f1)) {
            f1.dispose();
            return;
        }
        f1.setConn(conn);
        f1.setTitle("Penerimaan Lain");
        f1.setVisible(true);
        f1.setBounds(0, 0, f1.getWidth(), f1.getHeight());
        jDesktopPane1.add(f1, javax.swing.JLayeredPane.DEFAULT_LAYER);

        try {
            f1.setSelected(true);
        } catch (PropertyVetoException PO) {
        }
    }//GEN-LAST:event_mnuInvReceiptUnplannedActionPerformed

    private void mnuRptKeuanganjMenuItem1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuRptKeuanganjMenuItem1ActionPerformed
        FrmRptKeuangan f1 = new FrmRptKeuangan();
        if (udfExistForm(f1)) {
            f1.dispose();
            return;
        }
        f1.setConn(conn);
        f1.setVisible(true);
        f1.setBounds(0, 0, f1.getWidth(), f1.getHeight());
        jDesktopPane1.add(f1, javax.swing.JLayeredPane.DEFAULT_LAYER);

        try {
            f1.setSelected(true);
        } catch (PropertyVetoException PO) {
        }
    }//GEN-LAST:event_mnuRptKeuanganjMenuItem1ActionPerformed

    private void mnuMasterBarang1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuMasterBarang1ActionPerformed
        FrmDokterList f1 = new FrmDokterList();
        if (udfExistForm(f1)) {
            f1.dispose();
            return;
        }
        f1.setConn(conn);
        f1.setVisible(true);
        f1.setBounds(0, 0, f1.getWidth(), f1.getHeight());
        jDesktopPane1.add(f1, javax.swing.JLayeredPane.DEFAULT_LAYER);

        try {
            f1.setSelected(true);
        } catch (PropertyVetoException PO) {
        }
    }//GEN-LAST:event_mnuMasterBarang1ActionPerformed

    private void mnuKeuPCKeluarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuKeuPCKeluarActionPerformed
        udfLoadPettyCash("K");
    }//GEN-LAST:event_mnuKeuPCKeluarActionPerformed

    private void mnuKeuPCMasukActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuKeuPCMasukActionPerformed
        udfLoadPettyCash("M");
    }//GEN-LAST:event_mnuKeuPCMasukActionPerformed

    private void mnuKeuPCListActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuKeuPCListActionPerformed
        udfLoadPettyCashList();
    }//GEN-LAST:event_mnuKeuPCListActionPerformed

    private void mnuInvIssueUnplannedActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuInvIssueUnplannedActionPerformed
        FrmUnplanned f1 = new FrmUnplanned(false);
        if (udfExistForm(f1)) {
            f1.dispose();
            return;
        }
        f1.setConn(conn);
        
        f1.setTitle("Pengeluaran Barang Lain2");
        f1.setVisible(true);
        f1.setBounds(0, 0, f1.getWidth(), f1.getHeight());
        jDesktopPane1.add(f1, javax.swing.JLayeredPane.DEFAULT_LAYER);

        try {
            f1.setSelected(true);
        } catch (PropertyVetoException PO) {
        }
    }//GEN-LAST:event_mnuInvIssueUnplannedActionPerformed

    private void mnuKlinikRegistrasiPasienLookupActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuKlinikRegistrasiPasienLookupActionPerformed
        DlgLookupPasien d1=new DlgLookupPasien(this, true);
        d1.setVisible(true);
    }//GEN-LAST:event_mnuKlinikRegistrasiPasienLookupActionPerformed

    private void mnuKlinikReservasiActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuKlinikReservasiActionPerformed
        FrmReservasi f1 = new FrmReservasi();
        if (udfExistForm(f1)) {
            f1.dispose();
            return;
        }
        
        f1.setTitle("Reservasi Pasien");
        f1.setVisible(true);
        f1.setBounds(0, 0, f1.getWidth(), f1.getHeight());
        jDesktopPane1.add(f1, javax.swing.JLayeredPane.DEFAULT_LAYER);

        try {
            f1.setSelected(true);
        } catch (PropertyVetoException PO) {
        }
    }//GEN-LAST:event_mnuKlinikReservasiActionPerformed

    private void jMenuItemAnggotaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemAnggotaActionPerformed

    }//GEN-LAST:event_jMenuItemAnggotaActionPerformed

    private void jMenuItem4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem4ActionPerformed
        FrmRegList f1 = new FrmRegList();
        if (udfExistForm(f1)) {
            f1.dispose();
            return;
        }
        f1.setConn(this.conn);
        f1.setTitle("Daftar Registrasi Pasien - "+MainForm.sNamaDokter);
        f1.setVisible(true);
        f1.setBounds(0, 0, f1.getWidth(), f1.getHeight());
        jDesktopPane1.add(f1, javax.swing.JLayeredPane.DEFAULT_LAYER);

        try {
            f1.setSelected(true);
        } catch (PropertyVetoException PO) {
        }
    }//GEN-LAST:event_jMenuItem4ActionPerformed

    private void mnuFileDiskonPromoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuFileDiskonPromoActionPerformed
        FrmDiskonEvent f1 = new FrmDiskonEvent();
        if (udfExistForm(f1)) {
            f1.dispose();
            return;
        }
        f1.setVisible(true);
        f1.setBounds(0, 0, f1.getWidth(), f1.getHeight());
        jDesktopPane1.add(f1, javax.swing.JLayeredPane.DEFAULT_LAYER);

        try {
            f1.setSelected(true);
        } catch (PropertyVetoException PO) {
        }
    }//GEN-LAST:event_mnuFileDiskonPromoActionPerformed

    private void udfLoadReportKasir() {
        FrmRptKasir fRpt = new FrmRptKasir();
        if (udfExistForm(fRpt)) {
            fRpt.dispose();
            return;
        }
        fRpt.setConn(conn);

        fRpt.setVisible(true);
        fRpt.setBounds(0, 0, fRpt.getWidth(), fRpt.getHeight());
        jDesktopPane1.add(fRpt, javax.swing.JLayeredPane.DEFAULT_LAYER);

        try {
            fRpt.setSelected(true);
        } catch (PropertyVetoException PO) {
        }

    }

    private void udfLoadReportGL() {
//    FrmReportAkun fRpt=new FrmReportAkun();
//    if(udfExistForm(fRpt)){
//        fRpt.dispose();
//        return;
//    }
//    fRpt.setConn(conn);
//    fRpt.setVisible(true);
//    fRpt.setBounds(0, 0, fRpt.getWidth(), fRpt.getHeight());
//    jDesktopPane1.add(fRpt, javax.swing.JLayeredPane.DEFAULT_LAYER);
//    try{
//        fRpt.setSelected(true);
//    } catch(PropertyVetoException PO){
//
//    }
    }

    private void udfLoadListBuktiKas(String string) {
//    String sTitle="Bukti Kas/ Bank "+(string.equalsIgnoreCase("M")? "Masuk": "Keluar");
//    FrmHistoriKasBank f1=new FrmHistoriKasBank();
//
//    if(udfExistForm(f1 , sTitle)){
//        f1.dispose();
//        return;
//    }
//    this.setCursor(new Cursor(Cursor.WAIT_CURSOR));
//    f1.setConn(conn);
//    f1.setFlag(string);
//    f1.setVisible(true);
//    f1.setBounds(0, 0, f1.getWidth(), f1.getHeight());
//    jDesktopPane1.add(f1, javax.swing.JLayeredPane.DEFAULT_LAYER);
//    try{
//        f1.setMaximum(true);
//        f1.setSelected(true);
//        this.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
//    } catch(PropertyVetoException PO){
//    }
    }

    private void udfLoadBukuBank() {
//    if(udfExistForm(new FrmBukuBank ())) return;
//    this.setCursor(new Cursor(Cursor.WAIT_CURSOR));
//    FrmBukuBank f1=new FrmBukuBank();
//    f1.setConn(conn);
//    f1.setVisible(true);
//    f1.setBounds(0, 0, f1.getWidth(), f1.getHeight());
//    jDesktopPane1.add(f1, javax.swing.JLayeredPane.DEFAULT_LAYER);
//    try{
//        f1.setMaximum(true);
//        f1.setSelected(true);
//        this.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
//    } catch(PropertyVetoException PO){
//    }
    }

    private void udfLoadJournalEntry() {
//        FrmJournalEntry fJournal=new FrmJournalEntry();
//        fJournal.setConn(conn);
//        //fJournal.setBounds(0, 0, fJournal.getWidth(), fJournal.getHeight());
//        //jDesktopPane1.add(fJournal, javax.swing.JLayeredPane.DEFAULT_LAYER);
//        fJournal.setVisible(true);
////        try{
////            //fMaster.setMaximum(true);
////            fJournal.setSelected(true);
////        } catch(PropertyVetoException PO){
////
////        }
    }

    private void udfLoadRevCostBudget() {
//    if(udfExistForm(new FrmRevCostBudget2())) return;
//
//    this.setCursor(new Cursor(Cursor.WAIT_CURSOR));
//    FrmRevCostBudget2 f1=new FrmRevCostBudget2();
//    f1.setConn(conn);
//    f1.setVisible(true);
//    f1.setBounds(0, 0, f1.getWidth(), f1.getHeight());
//    jDesktopPane1.add(f1, javax.swing.JLayeredPane.DEFAULT_LAYER);
//
//    try{
//        f1.setMaximum(true);
//        f1.setSelected(true);
//        this.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
//    } catch(PropertyVetoException PO){
//
//    }
    }

    private void udfLoadListHistoriBB() {
//    if(udfExistForm(new FrmHistoriBB())) return;
//
//    this.setCursor(new Cursor(Cursor.WAIT_CURSOR));
//    FrmHistoriBB f1=new FrmHistoriBB();
//    f1.setConn(conn);
//    f1.setVisible(true);
//    f1.setDesktopPane(jDesktopPane1);
//    f1.setBounds(0, 0, f1.getWidth(), f1.getHeight());
//    jDesktopPane1.add(f1, javax.swing.JLayeredPane.DEFAULT_LAYER);
//    try{
//        f1.setMaximum(true);
//        f1.setSelected(true);
//        this.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
//    } catch(PropertyVetoException PO){
//
//    }
    }

    private void udfLoadKas(String sFlag) {
//    FrmBuktiKas fBuktiKas=new FrmBuktiKas();
//    fBuktiKas.setConn(conn);
//    fBuktiKas.setFlag(sFlag);
//    //fJournal.setBounds(0, 0, fJournal.getWidth(), fJournal.getHeight());
//    //jDesktopPane1.add(fJournal, javax.swing.JLayeredPane.DEFAULT_LAYER);
//    fBuktiKas.setVisible(true);
//        try{
//            //fMaster.setMaximum(true);
//            fJournal.setSelected(true);
//        } catch(PropertyVetoException PO){
//
//        }
    }

    private void udfLoadListJurnalUst() {
//        if(udfExistForm(new FrmJurnalList())) return;
//
//        this.setCursor(new Cursor(Cursor.WAIT_CURSOR));
//        FrmJurnalList f1=new FrmJurnalList();
//        f1.setConn(conn);
////        f1.setUserName(sUserName);
//        f1.setVisible(true);
//        f1.setDesktopPane(jDesktopPane1);
//        f1.setBounds(0, 0, f1.getWidth(), f1.getHeight());
//        jDesktopPane1.add(f1, javax.swing.JLayeredPane.DEFAULT_LAYER);
//
//        try{
//            f1.setMaximum(true);
//            f1.setSelected(true);
//            this.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
//        } catch(PropertyVetoException PO){
//
//        }
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new MainForm().setVisible(true);
            }
        });
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    public static apotek.JDesktopImage jDesktopPane1;
    private javax.swing.JMenu jMenu3;
    private javax.swing.JMenuBar jMenuBar2;
    private javax.swing.JMenuItem jMenuHelpAbout;
    private javax.swing.JMenuItem jMenuItem4;
    private javax.swing.JMenuItem jMenuItemAnggota;
    private javax.swing.JMenuItem jMnItemExit;
    private javax.swing.JMenuItem jMnuGudang;
    private javax.swing.JMenuItem jMnuJenisSupplier;
    private javax.swing.JScrollPane jScrollDesktop;
    private javax.swing.JScrollPane jScrollKiri;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JPopupMenu.Separator jSeparator10;
    private javax.swing.JPopupMenu.Separator jSeparator11;
    private javax.swing.JPopupMenu.Separator jSeparator12;
    private javax.swing.JPopupMenu.Separator jSeparator13;
    private javax.swing.JPopupMenu.Separator jSeparator14;
    private javax.swing.JPopupMenu.Separator jSeparator15;
    private javax.swing.JPopupMenu.Separator jSeparator16;
    private javax.swing.JPopupMenu.Separator jSeparator2;
    private javax.swing.JPopupMenu.Separator jSeparator3;
    private javax.swing.JPopupMenu.Separator jSeparator5;
    private javax.swing.JSeparator jSeparator6;
    private javax.swing.JSeparator jSeparator7;
    private javax.swing.JPopupMenu.Separator jSeparator8;
    private javax.swing.JPopupMenu.Separator jSeparator9;
    private javax.swing.JSplitPane jSplitPane1;
    private org.jdesktop.swingx.JXPanel jXPanel1;
    private org.jdesktop.swingx.JXPanel jXPanel2;
    private org.jdesktop.swingx.JXPanel jXPanel3;
    private org.jdesktop.swingx.JXPanel jXPanel4;
    private org.jdesktop.swingx.JXPanel jXPanel5;
    private org.jdesktop.swingx.JXStatusBar jXStatusBar1;
    private org.jdesktop.swingx.JXTaskPaneContainer jXTaskPaneContainer1;
    private javax.swing.JLabel lblJam;
    private javax.swing.JLabel lblServer;
    private javax.swing.JLabel lblTanggal;
    private javax.swing.JLabel lblTanggal2;
    private javax.swing.JLabel lblUserName;
    private javax.swing.JMenuItem mnuAPJatuhTempo;
    private javax.swing.JMenuItem mnuAR;
    private javax.swing.JMenuItem mnuAR1;
    private javax.swing.JMenu mnuFile;
    private javax.swing.JMenuItem mnuFileDiskonPromo;
    private javax.swing.JMenuItem mnuFileMenuAuth;
    private javax.swing.JMenuItem mnuFileMenuGrouping;
    private javax.swing.JMenuItem mnuFileUserManagement;
    private javax.swing.JMenuItem mnuGR;
    private javax.swing.JMenuItem mnuGRApproval;
    private javax.swing.JMenuItem mnuGRHistory;
    private javax.swing.JMenu mnuHelp;
    private javax.swing.JMenuItem mnuHistoriExpiredDate;
    private javax.swing.JMenuItem mnuInvIssueUnplanned;
    private javax.swing.JMenuItem mnuInvReceiptUnplanned;
    private javax.swing.JMenuItem mnuInvSO;
    private javax.swing.JMenuItem mnuInvTransfer;
    private javax.swing.JMenuItem mnuInvTransferHistory;
    private javax.swing.JMenuItem mnuJualKoreksiTrx;
    private javax.swing.JMenuItem mnuKalkulator;
    private javax.swing.JMenu mnuKeuPC;
    private javax.swing.JMenuItem mnuKeuPCKeluar;
    private javax.swing.JMenuItem mnuKeuPCList;
    private javax.swing.JMenuItem mnuKeuPCMasuk;
    private javax.swing.JMenu mnuKlinik;
    private javax.swing.JMenuItem mnuKlinikRegistrasiPasienLookup;
    private javax.swing.JMenuItem mnuKlinikReservasi;
    private javax.swing.JMenuItem mnuListCustomer;
    private javax.swing.JMenuItem mnuListPenjualan;
    private javax.swing.JMenuItem mnuListPenjualanRetur;
    private javax.swing.JMenuItem mnuListSupplier;
    private javax.swing.JMenuItem mnuLokasi;
    private javax.swing.JMenuItem mnuManufaktur;
    private javax.swing.JMenu mnuMaster;
    private javax.swing.JMenuItem mnuMasterBarang;
    private javax.swing.JMenuItem mnuMasterBarang1;
    private javax.swing.JMenuItem mnuMasterBentuk;
    private javax.swing.JMenuItem mnuMasterGroupBarang;
    private javax.swing.JMenuItem mnuMasterHargaSupplier;
    private javax.swing.JMenuItem mnuMasterJenisBarang;
    private javax.swing.JMenuItem mnuMasterUom;
    private javax.swing.JMenuItem mnuPO;
    private javax.swing.JMenuItem mnuPOApproval;
    private javax.swing.JMenuItem mnuPOCash;
    private javax.swing.JMenuItem mnuPOCashHIstory;
    private javax.swing.JMenuItem mnuPOHistory;
    private javax.swing.JMenuItem mnuPORevisi;
    private javax.swing.JMenuItem mnuPR;
    private javax.swing.JMenuItem mnuPRApproval;
    private javax.swing.JMenuItem mnuPRHistory;
    private javax.swing.JMenuItem mnuPRMaintenance;
    private javax.swing.JMenu mnuPembelian;
    private javax.swing.JMenu mnuPenjualan;
    private javax.swing.JMenu mnuPersediaan;
    private javax.swing.JMenu mnuRpt;
    private javax.swing.JMenuItem mnuRptKasir;
    private javax.swing.JMenuItem mnuRptKeuangan;
    private javax.swing.JMenuItem mnuRptPembelian;
    private javax.swing.JMenuItem mnuRptPenjualan;
    private javax.swing.JMenuItem mnuRptPersediaan;
    private javax.swing.JMenu mnuTool;
    private javax.swing.JMenuItem mnuToolsLookupItemBeli;
    private javax.swing.JMenuItem mnuToolsLookupItemJual;
    private javax.swing.JMenuItem mnuTrxBayarSupplier;
    private javax.swing.JMenuItem mnuTrxPenjualan;
    private javax.swing.JMenuItem mnuUbahPassword;
    private org.jdesktop.swingx.JXTaskPane taskPane_trx;
    private org.jdesktop.swingx.JXTaskPane taskpane_daftar;
    private org.jdesktop.swingx.JXTaskPane taskpane_inventori;
    private org.jdesktop.swingx.JXTaskPane taskpane_report;
    // End of variables declaration//GEN-END:variables
    public static String sNamaUsaha = "";
    public static String sAlamat = "";
    public static String sApoteker = "";
    public static String sSipApoteker = "";
    public static String sTelp = "";
    public static String sKota="";
    

    private void udfLoadUserManagement() {
//        FrmUserManagement f1=new FrmUserManagement();
//        if(udfExistForm(f1)){
//            f1.dispose();
//            return;
//        }
//        f1.setConn(conn);
//        jDesktopPane1.add(f1, javax.swing.JLayeredPane.DEFAULT_LAYER);
//        f1.setBounds(0, 0, f1.getWidth(), f1.getHeight());
//        f1.setVisible(true);
//        try{
//            f1.setSelected(true);
//            this.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
//        } catch(PropertyVetoException PO){
//
//        }
    }

    private void udfLoadKategori() {
//        FrmItemCategory f1=new FrmItemCategory();
//        if(udfExistForm(f1)){
//            f1.dispose();
//            return;
//        }
//        f1.setConn(conn);
//        f1.setBounds(0, 0, f1.getWidth(), f1.getHeight());
//        jDesktopPane1.add(f1, javax.swing.JLayeredPane.DEFAULT_LAYER);
//        f1.setVisible(true);
//        try{
//            f1.setSelected(true);
//            this.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
//        } catch(PropertyVetoException PO){
//
//        }
    }

    private void udfLoadItemGrouping() {
//        FrmItemGroup f1=new FrmItemGroup();
//        if(udfExistForm(f1)){
//            f1.dispose();
//            return;
//        }
//        f1.setConn(conn);
//        f1.setBounds(0, 0, f1.getWidth(), f1.getHeight());
//        jDesktopPane1.add(f1, javax.swing.JLayeredPane.DEFAULT_LAYER);
//        f1.setVisible(true);
//        try{
//            f1.setSelected(true);
//            this.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
//        } catch(PropertyVetoException PO){
//
//        }
    }

    private void udfLoadMenuAuth() {
//        FrmSettingMenu f1=new FrmSettingMenu();
//        if(udfExistForm(f1)){
//            f1.dispose();
//            return;
//        }
//        f1.setMainForm(this);
//        f1.setConn(conn);
//        f1.setBounds(0, 0, f1.getWidth(), f1.getHeight());
//        jDesktopPane1.add(f1, javax.swing.JLayeredPane.DEFAULT_LAYER);
//        f1.setVisible(true);
//        try{
//            f1.setSelected(true);
//            this.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
//        } catch(PropertyVetoException PO){
//
//        }
    }

    public void udfSetUserMenu() {
//        lblUser.setText("<html>Username : <b>"+sID+" - "+ sUserName+"</b></html>");
//        lblServer.setText(sIPServer);
//        lblShift.setText(shift);
//        lblUser1.setText(sUserName);
//        lblSite.setText("<html>Site :&nbsp <b>"+sSiteID+" - "+sSiteName+"</b></html>");
//        lblDepo.setText("<html>Workstation :&nbsp <b>"+sDepo+"</b></html>");

        try {
            String sQry = "select menu_description, "
                    + "coalesce(can_insert,false) as can_insert, "
                    + "coalesce(can_update, false) as can_update, "
                    + "coalesce(can_delete, false) as can_delete, "
                    + "coalesce(can_read, false) as can_read, "
                    + "coalesce(can_print, false) as can_print, "
                    + "coalesce(can_correction, false) as can_correction "
                    + "from m_menu_authorization auth "
                    + "inner join m_menu_list list on list.id=auth.menu_id "
                    + "where user_name='" + sUserName + "' and module_name='RTL'";

            ResultSet rs = conn.createStatement().executeQuery(sQry);
            logOff();

            while (rs.next()) {
//                if (rs.getString("menu_description").equalsIgnoreCase("Master Item")) {
//                    setMenu(menuItem, mnuListItem, rs.getBoolean("can_read"), rs.getBoolean("can_insert"), rs.getBoolean("can_update"), rs.getBoolean("can_delete"), rs.getBoolean("can_print"), rs.getBoolean("can_correction"));
//
//                }
                if (rs.getString("menu_description").equalsIgnoreCase("Master Supplier")) {
                    setMenu(menuSupplier, mnuListSupplier, rs.getBoolean("can_read"), rs.getBoolean("can_insert"), rs.getBoolean("can_update"), rs.getBoolean("can_delete"), rs.getBoolean("can_print"), rs.getBoolean("can_correction"));
                }


            }
            udfAddActionTransaksi();
            udfAddActionInventory();
            udfAddActionDaftar();
            udfAddActionReport();

            rs.close();
//            rs=conn.createStatement().executeQuery("select photo from m_user where username='"+sUserName+"'");
//            if(rs.next()){
//                byte[] imgBytes = rs.getBytes("photo");
//
//                if(imgBytes!=null){
//                    javax.swing.ImageIcon myIcon = new javax.swing.ImageIcon(imgBytes);
//                    javax.swing.ImageIcon bigImage = new javax.swing.ImageIcon(myIcon.getImage().getScaledInstance
//                                       (lblPhoto.getWidth(), lblPhoto.getHeight(), Image.SCALE_REPLICATE));
//
//                    lblPhoto.setIcon(bigImage);
//                    imgBytes=null;
//                }else{
//                    lblPhoto.setIcon(null);
//                }
//            }

        } catch (SQLException se) {
            System.out.println(se.getMessage());
        }
    }

    public void logOff() {
//        mnuListItem.setVisible(false);
//        mnuListSupplier.setVisible(false);
    }

    public JDesktopImage getDesktopImage() {
        return this.jDesktopPane1;
    }

    private void setMenu(MenuAuth mau, JMenuItem mnuItem, boolean can_read, boolean can_insert, boolean can_update, boolean can_delete,
            boolean can_print, boolean can_correction) {
        mau.setRead(can_read);
        mau.setInsert(can_insert);
        mau.setUpdate(can_update);
        mau.setDelete(can_delete);
        mau.setPrint(can_print);
        mau.setKoreksi(can_correction);
        if (mnuItem != null) {
            mnuItem.setVisible(can_read);
        }
    }
    public static MenuAuth menuItem = new MenuAuth();
    public static MenuAuth menuSupplier = new MenuAuth();
    public static MenuAuth menuSettingKategori = new MenuAuth();

    private void udfLoadTermin() {
//        DlgTermin d1=new DlgTermin(this, true);
//        d1.setConn(conn);
//        d1.setVisible(true);
    }

    private void udfLoadMasterPriceByProduct() {
        FrmSupplierPriceByItem f1 = new FrmSupplierPriceByItem();
        f1.setConn(conn);
        f1.setVisible(true);
    }

    private void udfLoadSettingHargaJual() {
        FrmSettingHargaJual f1 = new FrmSettingHargaJual();
        if (udfExistForm(f1)) {
            f1.dispose();
            return;
        }
        //f1.setMainForm(this);
        f1.setConn(conn);
        f1.setDesktopIcon(jDesktopPane1);
        f1.setBounds(0, 0, f1.getWidth(), f1.getHeight());
        jDesktopPane1.add(f1, javax.swing.JLayeredPane.DEFAULT_LAYER);
        f1.setVisible(true);
        try {
            f1.setSelected(true);
            this.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
        } catch (PropertyVetoException PO) {
        }
    }

    private void udfLoadAR() {
        FrmAR f1 = new FrmAR();
        if (udfExistForm(f1)) {
            f1.dispose();
            return;
        }
        //f1.setMainForm(this);
        f1.setConn(conn);
        f1.setBounds(0, 0, f1.getWidth(), f1.getHeight());
        jDesktopPane1.add(f1, javax.swing.JLayeredPane.DEFAULT_LAYER);
        f1.setVisible(true);
        try {
            f1.setSelected(true);
            this.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
        } catch (PropertyVetoException PO) {
        }
    }

    private void udfLoadAP() {
        FrmAP f1 = new FrmAP();
        if (udfExistForm(f1)) {
            f1.dispose();
            return;
        }
        //f1.setMainForm(this);
        f1.setConn(conn);
        f1.setBounds(0, 0, f1.getWidth(), f1.getHeight());
        jDesktopPane1.add(f1, javax.swing.JLayeredPane.DEFAULT_LAYER);
        f1.setVisible(true);
        try {
            f1.setSelected(true);
            this.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
        } catch (PropertyVetoException PO) {
        }
    }

    private void udfLoadAPJtTempo() {
        FrmAPJtTempo f1 = new FrmAPJtTempo();
        if (udfExistForm(f1)) {
            f1.dispose();
            return;
        }
        //f1.setMainForm(this);
        f1.setConn(conn);
        f1.setDesktopPane(jDesktopPane1);
        f1.setBounds(0, 0, f1.getWidth(), f1.getHeight());
        jDesktopPane1.add(f1, javax.swing.JLayeredPane.DEFAULT_LAYER);
        f1.setVisible(true);
        try {
            f1.setSelected(true);
            this.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
        } catch (PropertyVetoException PO) {
        }
    }

    private void udfLoadSettingHpp() {
//        FrmSettingHpp f1=new FrmSettingHpp();
//        if(udfExistForm(f1)){
//            f1.dispose();
//            return;
//        }
//        //f1.setMainForm(this);
//        f1.setConn(conn);
//        f1.setBounds(0, 0, f1.getWidth(), f1.getHeight());
//        jDesktopPane1.add(f1, javax.swing.JLayeredPane.DEFAULT_LAYER);
//        f1.setVisible(true);
//        try{
//            f1.setSelected(true);
//            this.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
//        } catch(PropertyVetoException PO){
//
//        }
    }

    private void udfLoadListAkun() {
//        FrmAkunList f1=new FrmAkunList();
//        if(udfExistForm(f1)){
//            f1.dispose();
//            return;
//        }
//        f1.setDesktop(jDesktopPane1);
//        f1.setConn(conn);
//        f1.setBounds(0, 0, f1.getWidth(), f1.getHeight());
//        jDesktopPane1.add(f1, javax.swing.JLayeredPane.DEFAULT_LAYER);
//        f1.setVisible(true);
//        try{
//            f1.setSelected(true);
//            this.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
//        } catch(PropertyVetoException PO){
//
//        }
    }

    public void udfLoadPettyCash(String mk) {
        FrmPettyCash pc=new FrmPettyCash();
        String sTitle=mk.equalsIgnoreCase("K")? "Keluar": "Masuk";
        if(udfExistForm(pc, "Petty Kas "+sTitle)){
            pc.dispose();
            return;
        }
        pc.setConn(conn);
        pc.setTitle("Petty Kas "+sTitle);
        pc.setKeluarMasuk(mk);
        pc.setVisible(true);
        pc.setBounds(0, 0, pc.getWidth(), pc.getHeight());
        jDesktopPane1.add(pc, javax.swing.JLayeredPane.DEFAULT_LAYER);
        try{
            pc.setSelected(true);
        } catch(PropertyVetoException PO){}
    }

    private void udfLoadPettyCashList() {
        FrmPettyCashList fRpt=new FrmPettyCashList();
        if(udfExistForm(fRpt)){
            fRpt.dispose();
            return;
        }
        fRpt.setConn(conn);
        fRpt.setMainForm(this);
        fRpt.setDesktopPane(jDesktopPane1);
        fRpt.setVisible(true);
        fRpt.setBounds(0, 0, fRpt.getWidth(), fRpt.getHeight());
        jDesktopPane1.add(fRpt, javax.swing.JLayeredPane.DEFAULT_LAYER);
        try{
            fRpt.setSelected(true);
        } catch(PropertyVetoException PO){}
    }
    
    private void udfLoadPO() {
//        FrmPO f1=new FrmPO();
//        if(udfExistForm(f1)){
//            f1.dispose();
//            return;
//        }
//        //f1.setMainForm(this);
//        f1.setConn(conn);
//        f1.setBounds(0, 0, f1.getWidth(), f1.getHeight());
//        jDesktopPane1.add(f1, javax.swing.JLayeredPane.DEFAULT_LAYER);
//        f1.setVisible(true);
//        try{
//            f1.setSelected(true);
//            this.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
//        } catch(PropertyVetoException PO){
//
//        }
    }

    public void udfLoadPOKoreksi(String sNoPo) {
//        FrmPO f1=new FrmPO();
//        if(udfExistForm(f1, "Koreksi PO - '"+sNoPo+"'")){
//            f1.dispose();
//            return;
//        }
//        f1.setNoPO(sNoPo);
//        f1.setTitle("Koreksi PO - '"+sNoPo+"'");
//        f1.setConn(conn);
//        f1.setFlagKoreksi(true);
//        f1.setBounds(0, 0, f1.getWidth(), f1.getHeight());
//        jDesktopPane1.add(f1, javax.swing.JLayeredPane.DEFAULT_LAYER);
//        f1.setVisible(true);
//        try{
//            f1.setSelected(true);
//            this.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
//        } catch(PropertyVetoException PO){
//
//        }
    }

    public void udfLoadKoreksiReturJual(String sNoRetur, Object objForm) {
//        TrxReturPenjualan f1=new TrxReturPenjualan();
////        if(udfExistForm(f1, "Koreksi Retur Jual - '"+sNoPo+"'")){
////            f1.dispose();
////            return;
////        }
//        f1.setObjForm(objForm);
//        f1.setNoRetur(sNoRetur);
//        f1.setTitle("Koreksi Retur Jual - '"+sNoRetur+"'");
//        f1.setConn(conn);
//        f1.setFlagKoreksi(true);
////        f1.setBounds(0, 0, f1.getWidth(), f1.getHeight());
////        jDesktopPane1.add(f1, javax.swing.JLayeredPane.DEFAULT_LAYER);
//        f1.setVisible(true);
////        try{
////            f1.setSelected(true);
////            this.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
////        } catch(PropertyVetoException PO){
////
////        }
    }
    
    public static MenuAuth menuPRHistory=new MenuAuth();
    public static MenuAuth menuPRApproval=new MenuAuth();
    public static MenuAuth menuPOApproval=new MenuAuth();
}
