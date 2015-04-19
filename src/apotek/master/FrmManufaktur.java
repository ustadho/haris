/*
 * FrmJenisBarang.java
 *
 * Created on December 5, 2006, 10:45 AM
 */
package apotek.master;

import apotek.DLgLookup;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.FocusTraversalPolicy;
import java.awt.Font;
import java.awt.KeyboardFocusManager;
import java.awt.Rectangle;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumnModel;
import javax.swing.text.MaskFormatter;
import main.GeneralFunction;
import main.ListRsbm;

/**
 *
 * @author root
 */
public class FrmManufaktur extends javax.swing.JInternalFrame {

    private String idUnit;
    private String nmUnit;
    private String singkatan;
    private String supervisor;
    DefaultTableModel modelJenis;
    private String sClose = "close";
    static String sID = "";
    static String sTgl = "";
    private Connection conn;
    private boolean bAsc;

    /**
     * Creates new form FrmJenisBarang
     */
    public FrmManufaktur() {
        initComponents();
        table.getSelectionModel().addListSelectionListener(new SelectionListener(table));
    }

    public Connection getConn() {
        return conn;
    }

    public void setConn(Connection conn) {
        this.conn = conn;
    }


    private void TableLook() {
        table.getColumnModel().getColumn(0).setMaxWidth(70);     //kode
        table.getColumnModel().getColumn(0).setPreferredWidth(70);

        table.setRowHeight(20);
        for (int i = 0; i < table.getColumnCount(); i++) {
            table.getColumnModel().getColumn(i).setCellRenderer(new MyRowRenderer());
        }

        if (modelJenis.getRowCount() > 0) {
            table.changeSelection(0, 0, false, false);
        }
    }

    public boolean getAsc() {
        if (!bAsc) {
            bAsc = true;
        } else {
            bAsc = false;
        }
        return bAsc;
    }

    private void setLocationRelativeTo(Object object) {
    }

    public class myColHeaderList extends MouseAdapter {

        public void mouseClicked(MouseEvent evt) {
            JTable table = ((JTableHeader) evt.getSource()).getTable();
            TableColumnModel colModel = table.getColumnModel();

            // The index of the column whose header was clicked
            int vColIndex = colModel.getColumnIndexAtX(evt.getX());
            int mColIndex = table.convertColumnIndexToModel(vColIndex);

            // Return if not clicked on any column header
            if (vColIndex == -1) {
                return;
            }
            boolean bSt;
            bSt = getAsc();
            sortAllRowsBy(modelJenis, vColIndex, bSt);
            // Determine if mouse was clicked between column heads
            Rectangle headerRect = table.getTableHeader().getHeaderRect(vColIndex);
            if (vColIndex == 0) {
                headerRect.width -= 3;    // Hard-coded constant
            } else {
                headerRect.grow(-3, 0);   // Hard-coded constant
            }
            if (!headerRect.contains(evt.getX(), evt.getY())) {
                // Mouse was clicked between column heads
                // vColIndex is the column head closest to the click

                // vLeftColIndex is the column head to the left of the click
                int vLeftColIndex = vColIndex;
                if (evt.getX() < headerRect.x) {
                    vLeftColIndex--;
                }
            }
        }
    }

    public class MyRowRenderer extends DefaultTableCellRenderer implements TableCellRenderer {

        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {

            Color g1 = new Color(239, 234, 240);//-->>(251,236,177);// Kuning         [251,251,235]
            Color g2 = new Color(239, 234, 240);//-->>(241,226,167);// Kuning         [247,247,218]


            Color w1 = new Color(255, 255, 255);// Putih
            Color w2 = new Color(250, 250, 250);// Putih Juga

            Color h1 = new Color(255, 240, 240);// Merah muda
            Color h2 = new Color(250, 230, 230);// Merah Muda

            Color g;
            Color w;
            Color h;

            if (column % 2 == 0) {
                g = g1;
                w = w1;
                h = h1;
            } else {
                g = g2;
                w = w2;
                h = h2;
            }
            setForeground(new Color(0, 0, 0));
            if (row % 2 == 0) {
                setBackground(w);
            } else {
                setBackground(g);
            }
            if (isSelected) {
                //setBackground(new Color(51,102,255));
                setBackground(new Color(248, 255, 167));
                //setForeground(new Color(255,255,255));
            }

            setValue(value);
            return this;
        }
    }

    public class ColumnSorter implements Comparator {

        int colIndex;
        boolean ascending;

        ColumnSorter(int colIndex, boolean ascending) {
            this.colIndex = colIndex;
            this.ascending = ascending;
        }

        public int compare(Object a, Object b) {
            Vector v1 = (Vector) a;
            Vector v2 = (Vector) b;
            Object o1 = v1.get(colIndex);
            Object o2 = v2.get(colIndex);

            // Treat empty strains like nulls
            if (o1 instanceof String && ((String) o1).length() == 0) {
                o1 = null;
            }
            if (o2 instanceof String && ((String) o2).length() == 0) {
                o2 = null;
            }

            // Sort nulls so they appear last, regardless
            // of sort order
            if (o1 == null && o2 == null) {
                return 0;
            } else if (o1 == null) {
                return 1;
            } else if (o2 == null) {
                return -1;
            } else if (o1 instanceof Comparable) {
                if (ascending) {
                    return ((Comparable) o1).compareTo(o2);
                } else {
                    return ((Comparable) o2).compareTo(o1);
                }
            } else {
                if (ascending) {
                    return o1.toString().compareTo(o2.toString());
                } else {
                    return o2.toString().compareTo(o1.toString());
                }
            }
        }
    }

    public void sortAllRowsBy(DefaultTableModel model, int colIndex, boolean ascending) {
        Vector data = model.getDataVector();
        Collections.sort(data, new ColumnSorter(colIndex, ascending));
        model.fireTableStructureChanged();
        TableLook();
    }

    private void pesanError(String Err) {
        JOptionPane.showMessageDialog(this, Err, "Message", JOptionPane.ERROR_MESSAGE);
    }

    public class MyKeyListener extends KeyAdapter {

        public void keyPressed(KeyEvent evt) {
            int keyKode = evt.getKeyCode();
            switch (keyKode) {

                case KeyEvent.VK_F2: {  //Save
                    if (getBEdit()) {
                        udfUpdateData();
                    }

                    break;
                }

                case KeyEvent.VK_F3: {  //Search
//                    udfFilter();

                    break;
                }

                case KeyEvent.VK_F4: {  //Edit
                    udfEdit();
                    break;
                }

                case KeyEvent.VK_F5: {  //New -- Add
                    udfNew();
                    break;
                }

                case KeyEvent.VK_F6: {  //Filter
//                    onOpen(cmbFilter.getSelectedItem().toString(),true);
                    break;
                }

                case KeyEvent.VK_F12: {  //Delete
                    if (!getBEdit() && table.getRowCount() > 0) {
                        udfUpdateData();
                    }

                    break;
                }
                case KeyEvent.VK_ENTER: {
                    Component ct = KeyboardFocusManager.getCurrentKeyboardFocusManager().getPermanentFocusOwner();
                    if (!(ct.getClass().getSimpleName().equalsIgnoreCase("JTABLE"))) {
                        if (!fn.isListVisible()) {
                            Component c = findNextFocus();
                            c.requestFocus();
                        } else {
                            fn.lstRequestFocus();;
                        }
                    }
                    break;
                }
                case KeyEvent.VK_DOWN: {
                    Component ct = KeyboardFocusManager.getCurrentKeyboardFocusManager().getPermanentFocusOwner();
                    if (!(ct.getClass().getSimpleName().equalsIgnoreCase("JTABLE"))) {
                        if (!fn.isListVisible()) {
                            Component c = findNextFocus();
                            c.requestFocus();
                        } else {
                            fn.lstRequestFocus();
                        }

                        break;
                    }
                }
                case KeyEvent.VK_UP: {
                    Component ct = KeyboardFocusManager.getCurrentKeyboardFocusManager().getPermanentFocusOwner();
                    if (!(ct.getClass().getSimpleName().equalsIgnoreCase("JTABLE"))) {
                        Component c = findPrevFocus();
                        c.requestFocus();
                    }
                    break;
                }

                //lempar aja ke udfCancel
                case KeyEvent.VK_ESCAPE: {
                    //Jika status button adalah Close
                    if (sClose.equalsIgnoreCase("close")) {
                        if (!getBEdit()) {
                            if (JOptionPane.showConfirmDialog(null, "Anda Yakin Untuk Keluar?", "SHS go Open Sources", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
                                dispose();
                            }
                        } else if (JOptionPane.showConfirmDialog(null, "Apakah data disimpan sebelum anda keluar?", "SHS go Open Sources", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
                            udfUpdateData();
                        } else {
                            dispose();
                        }

                        break;
                    } //Jika cancel
                    else {
                        udfCancel();
                    }
                }
            }
        }
        
        @Override
        public void keyReleased(KeyEvent evt){
            String sQry="";
            if(evt.getSource().equals(txtKota)){
                 sQry="select kode_kota, coalesce(nama_kota,'') as nama_kota "
                        + "from kota "
                        + "where kode_kota||coalesce(nama_kota,'') ilike '%"+txtKota.getText()+"%' order by 2";

                fn.lookup(evt, new Object[]{lblKota}, sQry, txtKota.getWidth()+lblKota.getWidth()+18, 150);
            
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
//            if (lst.isVisible())
//                lst.setVisible(false);

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
//            if (lst.isVisible()) lst.setVisible(false);

            return prevFocus;
        }
        return null;
    }

    public void setUpBtn() {
        if (getBEdit()) {  //proses edit     

            String fileImageSave = "/image/Icon/Save.png";
            ButtonIcon(fileImageSave, btnDelete);

            String fileImageCancel = "/image/Icon/Cancel.png";
            ButtonIcon(fileImageCancel, btnClose);

            sClose = "cancel";

            btnNew.setEnabled(false);
            btnEdit.setEnabled(false);

            btnDelete.setToolTipText("Save    (F5)");
            btnClose.setToolTipText("Cancel");
            System.out.println(getBEdit());

            table.setEnabled(false);
            txtNama.setEditable(true);

            table.requestFocus();

        } else {   //selain edit & NEW
            String fileImageSave = "/image/Icon/Delete.png";
            ButtonIcon(fileImageSave, btnDelete);

            String fileImageCancel = "/image/Icon/Exit.png";
            ButtonIcon(fileImageCancel, btnClose);

            sClose = "close";


            btnNew.setEnabled(true);
            btnEdit.setEnabled(true);

            btnDelete.setToolTipText("Delete     (F12)");
            btnClose.setToolTipText("Close");

            table.setEnabled(true);
            txtNama.setEditable(false);

        }
    }

    public void setBNew(Boolean lNew) {
        bNew = lNew;
    }

    private void LabelIcon(String aFile, javax.swing.JLabel newlbl) {
        javax.swing.ImageIcon myIcon = new javax.swing.ImageIcon(getClass().getResource(aFile));
        newlbl.setIcon(myIcon);
    }

    private void ButtonIcon(String aFile, javax.swing.JButton newBtn) {
        javax.swing.ImageIcon myIcon = new javax.swing.ImageIcon(getClass().getResource(aFile));
        newBtn.setIcon(myIcon);
    }

    public Boolean getBNew() {
        return bNew;
    }

    public void setBEdit(Boolean lEdit) {
        bEdit = lEdit;
    }

    public Boolean getBEdit() {
        return bEdit;
    }

    public String getTanggal() {
        Calendar c = Calendar.getInstance();
        String sekarang = "";
        try {
            final Statement stTgl = conn.createStatement();
            final ResultSet rtgl = stTgl.executeQuery("select now() as tanggal, current_time as jam");
            if (rtgl.next()) {
                SimpleDateFormat fdateformat = new SimpleDateFormat("dd-MM-yyyy");
                sekarang = fdateformat.format(rtgl.getDate(1));
                c.setTimeInMillis(rtgl.getTime(2).getTime());
                //c.set()
            }
            rtgl.close();
            stTgl.close();
        } catch (SQLException sqtgl) {
            System.out.println(sqtgl.getMessage());
        }

        MaskFormatter fmt = null;
        try {
            fmt = new MaskFormatter("##-##-####");
        } catch (java.text.ParseException e) {
        }
        return sekarang;
    }

    private void saveUnit() {
//        JenisBarangBean sBean=new JenisBarangBean();
//        sBean.setConn(conn);
//        sBean.setKode(txtKode.getText());
//        sBean.setNama(txtNama.getText());
    }

    //Modul untuk RowColChange-nya JTable
    public class SelectionListener implements ListSelectionListener {

        JTable table;
        int rowPos;
        int colPos;

        // It is necessary to keep the table since it is not possible
        // to determine the table from the event's source
        SelectionListener(JTable table) {
            this.table = table;
        }

        public void valueChanged(ListSelectionEvent e) {
            rowPos = table.getSelectedRow();

            if (rowPos >= 0 && rowPos < table.getRowCount()) {
                String sKode=table.getValueAt(rowPos, 0).toString();
                try{
                    ResultSet rs=conn.createStatement().executeQuery(
                            "select m.id, coalesce(m.nama_manufaktur,'') as nama_manufaktur, "
                            + "coalesce(m.alamat,'') as alamat, coalesce(m.kode_kota,'') as kode_kota,  "
                            + "coalesce(k.nama_kota,'') as nama_kota, coalesce(m.telepon,'') as telepon, "
                            + "coalesce(m.hp,'') as hp, coalesce(m.kontak,'') as kontak "
                            + "from item_manufaktur m "
                            + "left join kota k on k.kode_kota=m.kode_kota "
                            + "where m.id = '"+sKode+"' "
                            + "order by 2");
                    if(rs.next()){
                        txtKode.setText(sKode);
                        txtNama.setText(rs.getString("nama_manufaktur"));
                        txtAlamat.setText(rs.getString("alamat"));
                        txtKota.setText(rs.getString("kode_kota"));
                        lblKota.setText(rs.getString("nama_kota"));
                        txtTelepon.setText(rs.getString("telepon"));
                        txtHP.setText(rs.getString("hp"));
                        txtKontak.setText(rs.getString("kontak"));
                    }
                }catch(SQLException se){
                    System.out.println("Error :"+se.getMessage());
                }
            }
            if (table.getRowCount() == 0) {
                udfClear();
            }

        }
    }
    
    private void udfClear(){
        txtKode.setText("");
        txtNama.setText("");
        txtAlamat.setText("");
        txtKota.setText("");
        lblKota.setText("");
        txtKontak.setText("");
        txtHP.setText("");
        txtTelepon.setText("");
        txtNama.requestFocusInWindow();
    }
    
    private String getNewKode(){
        String sKode="";
        try {
            ResultSet rs=conn.createStatement().executeQuery("select fn_get_new_kode_manufaktur()");
            if(rs.next()){
                sKode=rs.getString(1);
                txtKode.setText(sKode);
            }
            rs.close();
        } catch (SQLException ex) {
            Logger.getLogger(FrmManufaktur.class.getName()).log(Level.SEVERE, null, ex);
        }
        return sKode;
    }

    private void udfUpdateData() {
        //JenisBarangBean nB =new JenisBarangBean();
        int hsl = 0;

        if (getBEdit()) {
            try {
                conn.setAutoCommit(false);
                
                if (getBNew()) {        //Add
                    PreparedStatement ps=conn.prepareStatement("INSERT INTO item_manufaktur(\n" +
                                        "            id, nama_manufaktur, alamat, kode_kota, telepon, kontak, hp)\n" +
                                        "    VALUES (?, ?, ?, ?, ?, ?, ?);");
                    ps.setString(1, getNewKode());
                    ps.setString(2, txtNama.getText());
                    ps.setString(3, txtAlamat.getText());
                    ps.setString(4, txtKota.getText());
                    ps.setString(5, txtTelepon.getText());
                    ps.setString(6, txtKontak.getText());
                    ps.setString(7, txtHP.getText());
                    
                    ps.executeUpdate();
                    modelJenis.addRow(new Object[]{
                        txtKode.getText(),
                        txtNama.getText(),
                        txtAlamat.getText(),
                        lblKota.getText(),
                        txtTelepon.getText(),
                    });
                    fn.setAutoResizeColWidth(table);
                    setBEdit(false);
                    table.setRowSelectionInterval(modelJenis.getRowCount() - 1, modelJenis.getRowCount() - 1);
                    setBEdit(false);
                    setBNew(false);
                    setUpBtn();

                    //System.out.println(hsl);
                } else {
                    PreparedStatement ps=conn.prepareStatement("UPDATE item_manufaktur\n" +
                            "   SET nama_manufaktur=?, alamat=?, kode_kota=?, telepon=?, kontak=?, \n" +
                            "       hp=?\n" +
                            " WHERE id=?;");
                    ps.setString(1, txtNama.getText());
                    ps.setString(2, txtAlamat.getText());
                    ps.setString(3, txtKota.getText());
                    ps.setString(4, txtTelepon.getText());
                    ps.setString(5, txtKontak.getText());
                    ps.setString(6, txtHP.getText());
                    ps.setString(7, txtKode.getText());
                    
                    table.setValueAt(txtNama.getText(), table.getSelectedRow(), 1);
                    table.setValueAt(txtAlamat.getText(), table.getSelectedRow(), 2);
                    table.setValueAt(lblKota.getText(), table.getSelectedRow(), 3);
                    table.setValueAt(txtTelepon.getText(), table.getSelectedRow(), 4);
                    System.out.println(hsl);
                    setBEdit(false);
                    setBNew(false);
                    setUpBtn();
                    
                }

                conn.commit();
                conn.setAutoCommit(true);
            } catch (SQLException e) {
                try {
                    conn.rollback();
                    conn.setAutoCommit(true);
                } catch (SQLException s) {
                }
                System.out.println(e.getMessage());
                pesanError(e.getMessage());
            }

        } else {    //DELETE
            try {
                String s = table.getValueAt(table.getSelectedRow(), 1).toString();
                if (JOptionPane.showConfirmDialog(null, "Anda Yakin Untuk mengapus '" + s + "' ?", "Message", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
                    int iPosDel = table.getSelectedRow();

                    Statement st = conn.createStatement();
                    hsl = st.executeUpdate("delete from item_bentuk where bentuk_id='" + txtKode.getText() + "'");
//                    conn.commit();
                        
                    if (hsl > 0) {
                        modelJenis.removeRow(iPosDel);
                        if (iPosDel < modelJenis.getRowCount() && modelJenis.getRowCount() > 0) {
                            table.setRowSelectionInterval(iPosDel, iPosDel);
                            table.requestFocus();
                        }
                    }
                }
            } catch (SQLException se) {
                try {
                    System.out.println(se.getMessage());
                    conn.rollback();
                    conn.setAutoCommit(true);
                } catch (SQLException s) {
                    pesanError(s.getMessage());
                }
            }
            table.requestFocus();
        }
    }

    private void udfEdit() {
        if (table.getRowCount() > 0) {
            setBEdit(true);
            setBNew(false);
            setUpBtn();
            txtNama.requestFocus();
        }
    }

    private void udfNew() {
        setBNew(true);
        setBEdit(true);
        setUpBtn();

        udfClear();
    }

    
    private void udfCancel() {
        if (getBEdit()) {
            setBEdit(false);
            setBNew(false);
            if (table.getRowCount() > 0) {
                int rowPos = table.getSelectedRow();
                txtKode.setText(table.getValueAt(rowPos, 0).toString());
                txtNama.setText(table.getValueAt(rowPos, 1).toString());
            }
            setUpBtn();
            table.setRequestFocusEnabled(true);

        } else {
            this.dispose();
        }
    }

    private void udfFilter() {
        modelJenis = (DefaultTableModel) table.getModel();
        int i = 0;
        try {
            String sQry = "select m.id, coalesce(m.nama_manufaktur,'') as nama_manufaktur, "
                    + "coalesce(m.alamat,'') as alamat, coalesce(k.nama_kota,'') as nama_kota, coalesce(m.telepon,'') as telepon "
                    + "from item_manufaktur m "
                    + "left join kota k on k.kode_kota=m.kode_kota "
                    + "where m.id||coalesce(m.nama_manufaktur,'') ||coalesce(k.nama_kota,'') ilike '%"+txtSearch.getText()+"%' "
                    + "order by 2;";

            while (modelJenis.getRowCount() >= 1) {
                modelJenis.removeRow(0);
            }

            ResultSet rs = conn.createStatement().executeQuery(sQry);

            System.out.println(sQry);
            while (rs.next()) {
                modelJenis.addRow(new Object[]{
                    rs.getString("id"),
                    rs.getString("nama_manufaktur"),
                    rs.getString("alamat"),
                    rs.getString("nama_kota"),
                    rs.getString("telepon"),
                });
            }

            if (modelJenis.getRowCount() > 0) {
                table.setRowSelectionInterval(0, 0);
                fn.setAutoResizeColWidth(table);
            }
            rs.close();
        } catch (SQLException eswl) {
            JOptionPane.showMessageDialog(this, eswl.getMessage());
        }
        if (i > 0) {
            table.requestFocusInWindow();
            table.setRowSelectionInterval(0, 0);
        }
    }
    private GeneralFunction fn = new GeneralFunction();

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jToolBar1 = new javax.swing.JToolBar();
        btnNew = new javax.swing.JButton();
        btnEdit = new javax.swing.JButton();
        btnDelete = new javax.swing.JButton();
        btnClose = new javax.swing.JButton();
        jToolBar2 = new javax.swing.JToolBar();
        jLabel3 = new javax.swing.JLabel();
        txtSearch = new javax.swing.JTextField();
        btnSearch = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        table = new javax.swing.JTable();
        jPanel2 = new javax.swing.JPanel();
        txtKode = new javax.swing.JTextField();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        txtNama = new javax.swing.JTextField();
        txtAlamat = new javax.swing.JTextField();
        jLabel4 = new javax.swing.JLabel();
        txtKota = new javax.swing.JTextField();
        lblKota = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        txtTelepon = new javax.swing.JTextField();
        jLabel7 = new javax.swing.JLabel();
        txtKontak = new javax.swing.JTextField();
        jLabel8 = new javax.swing.JLabel();
        txtHP = new javax.swing.JTextField();

        setClosable(true);
        setIconifiable(true);
        setMaximizable(true);
        setResizable(true);
        setTitle("Master Manufaktur");
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
        getContentPane().setLayout(null);

        jPanel1.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        btnNew.setIcon(new javax.swing.ImageIcon(getClass().getResource("/image/Icon/New.png"))); // NOI18N
        btnNew.setToolTipText("New     (F5)");
        btnNew.setBorder(null);
        btnNew.setMaximumSize(new java.awt.Dimension(40, 40));
        btnNew.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnNewActionPerformed(evt);
            }
        });
        jToolBar1.add(btnNew);

        btnEdit.setIcon(new javax.swing.ImageIcon(getClass().getResource("/image/Icon/Edit.png"))); // NOI18N
        btnEdit.setToolTipText("Edit     (F4)");
        btnEdit.setBorder(null);
        btnEdit.setMaximumSize(new java.awt.Dimension(40, 40));
        btnEdit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnEditActionPerformed(evt);
            }
        });
        jToolBar1.add(btnEdit);

        btnDelete.setIcon(new javax.swing.ImageIcon(getClass().getResource("/image/Icon/Delete.png"))); // NOI18N
        btnDelete.setToolTipText("New     (F12)");
        btnDelete.setBorder(null);
        btnDelete.setMaximumSize(new java.awt.Dimension(40, 40));
        btnDelete.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnDeleteActionPerformed(evt);
            }
        });
        jToolBar1.add(btnDelete);

        btnClose.setIcon(new javax.swing.ImageIcon(getClass().getResource("/image/Icon/Exit.png"))); // NOI18N
        btnClose.setToolTipText("New     (F12)");
        btnClose.setBorder(null);
        btnClose.setMaximumSize(new java.awt.Dimension(40, 40));
        btnClose.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCloseActionPerformed(evt);
            }
        });
        jToolBar1.add(btnClose);

        jPanel1.add(jToolBar1, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 180, 50));

        jLabel3.setText("Filter : ");
        jToolBar2.add(jLabel3);

        txtSearch.setMaximumSize(new java.awt.Dimension(200, 24));
        txtSearch.setMinimumSize(new java.awt.Dimension(4, 4));
        jToolBar2.add(txtSearch);

        btnSearch.setIcon(new javax.swing.ImageIcon(getClass().getResource("/image/Icon/Search.png"))); // NOI18N
        btnSearch.setToolTipText("Search     (F3)");
        btnSearch.setBorder(null);
        btnSearch.setMaximumSize(new java.awt.Dimension(40, 40));
        btnSearch.setMinimumSize(new java.awt.Dimension(40, 40));
        btnSearch.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSearchActionPerformed(evt);
            }
        });
        jToolBar2.add(btnSearch);

        jPanel1.add(jToolBar2, new org.netbeans.lib.awtextra.AbsoluteConstraints(180, 0, 555, 50));

        getContentPane().add(jPanel1);
        jPanel1.setBounds(4, 0, 740, 50);

        jScrollPane1.setBackground(new java.awt.Color(255, 255, 255));

        table.setAutoCreateRowSorter(true);
        table.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Kode", "Nama Manufaktur", "Alamat", "Kota", "Telp"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class
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
        table.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_OFF);
        table.getTableHeader().setReorderingAllowed(false);
        jScrollPane1.setViewportView(table);

        getContentPane().add(jScrollPane1);
        jScrollPane1.setBounds(5, 56, 739, 210);

        jPanel2.setBackground(new java.awt.Color(204, 204, 204));
        jPanel2.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        jPanel2.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        txtKode.setFont(new java.awt.Font("Dialog", 1, 12)); // NOI18N
        txtKode.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        txtKode.setEnabled(false);
        jPanel2.add(txtKode, new org.netbeans.lib.awtextra.AbsoluteConstraints(160, 10, 80, 24));

        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel1.setText("ID : ");
        jPanel2.add(jLabel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 10, 150, 20));

        jLabel2.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel2.setText("Nama Manufaktur : ");
        jPanel2.add(jLabel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 40, 150, 20));

        txtNama.setFont(new java.awt.Font("Dialog", 1, 12)); // NOI18N
        txtNama.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        jPanel2.add(txtNama, new org.netbeans.lib.awtextra.AbsoluteConstraints(160, 40, 540, 24));

        txtAlamat.setFont(new java.awt.Font("Dialog", 1, 12)); // NOI18N
        txtAlamat.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        jPanel2.add(txtAlamat, new org.netbeans.lib.awtextra.AbsoluteConstraints(160, 70, 540, 24));

        jLabel4.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel4.setText("Kota : ");
        jPanel2.add(jLabel4, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 100, 150, 20));

        txtKota.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        txtKota.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        jPanel2.add(txtKota, new org.netbeans.lib.awtextra.AbsoluteConstraints(160, 100, 50, 24));

        lblKota.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        lblKota.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        jPanel2.add(lblKota, new org.netbeans.lib.awtextra.AbsoluteConstraints(210, 100, 230, 24));

        jLabel5.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel5.setText("Alamat : ");
        jPanel2.add(jLabel5, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 70, 150, 20));

        jLabel6.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel6.setText("Telepon : ");
        jPanel2.add(jLabel6, new org.netbeans.lib.awtextra.AbsoluteConstraints(470, 100, 110, 20));

        txtTelepon.setFont(new java.awt.Font("Dialog", 1, 12)); // NOI18N
        txtTelepon.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        jPanel2.add(txtTelepon, new org.netbeans.lib.awtextra.AbsoluteConstraints(580, 100, 150, 24));

        jLabel7.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel7.setText("Kontak : ");
        jPanel2.add(jLabel7, new org.netbeans.lib.awtextra.AbsoluteConstraints(50, 130, 110, 20));

        txtKontak.setFont(new java.awt.Font("Dialog", 1, 12)); // NOI18N
        txtKontak.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        jPanel2.add(txtKontak, new org.netbeans.lib.awtextra.AbsoluteConstraints(160, 130, 150, 24));

        jLabel8.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel8.setText("HP : ");
        jPanel2.add(jLabel8, new org.netbeans.lib.awtextra.AbsoluteConstraints(470, 130, 110, 20));

        txtHP.setFont(new java.awt.Font("Dialog", 1, 12)); // NOI18N
        txtHP.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        jPanel2.add(txtHP, new org.netbeans.lib.awtextra.AbsoluteConstraints(580, 130, 150, 24));

        getContentPane().add(jPanel2);
        jPanel2.setBounds(5, 268, 739, 170);

        setSize(new java.awt.Dimension(760, 469));
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void formInternalFrameClosed(javax.swing.event.InternalFrameEvent evt) {//GEN-FIRST:event_formInternalFrameClosed
    }//GEN-LAST:event_formInternalFrameClosed

    private void formInternalFrameOpened(javax.swing.event.InternalFrameEvent evt) {//GEN-FIRST:event_formInternalFrameOpened
        modelJenis = (DefaultTableModel) table.getModel();
        modelJenis.setNumRows(0);
        table.setModel(modelJenis);

        udfFilter();
        setBEdit(false);
        setBNew(false);


        requestFocusInWindow(true);
        table.requestFocusInWindow();

        for (int i = 0; i < jToolBar2.getComponentCount(); i++) {
            Component c = jToolBar2.getComponent(i);
            if (c.getClass().getSimpleName().equalsIgnoreCase("JTEXTFIELD") || c.getClass().getSimpleName().equalsIgnoreCase("JFormattedTextField")
                    || c.getClass().getSimpleName().equalsIgnoreCase("JTextArea") || c.getClass().getSimpleName().equalsIgnoreCase("JComboBox")
                    || c.getClass().getSimpleName().equalsIgnoreCase("JButton") || c.getClass().getSimpleName().equalsIgnoreCase("JCheckBox")) {
                //System.out.println(c.getClass().getSimpleName());
                c.addKeyListener(new MyKeyListener());
            }
        }
        for (int i = 0; i < jToolBar1.getComponentCount(); i++) {
            Component c = jToolBar1.getComponent(i);
            if (c.getClass().getSimpleName().equalsIgnoreCase("JTEXTFIELD") || c.getClass().getSimpleName().equalsIgnoreCase("JFormattedTextField")
                    || c.getClass().getSimpleName().equalsIgnoreCase("JTextArea") || c.getClass().getSimpleName().equalsIgnoreCase("JComboBox")
                    || c.getClass().getSimpleName().equalsIgnoreCase("JButton") || c.getClass().getSimpleName().equalsIgnoreCase("JCheckBox")) {
                //System.out.println(c.getClass().getSimpleName());
                c.addKeyListener(new MyKeyListener());
            }
        }

        jScrollPane1.addKeyListener(new MyKeyListener());
        table.addKeyListener(new MyKeyListener());

        System.out.println(jPanel1.getComponentCount());
        for (int i = 0; i < jPanel1.getComponentCount(); i++) {
            Component c = jPanel1.getComponent(i);
            if (c.getClass().getSimpleName().equalsIgnoreCase("JTEXTFIELD") || c.getClass().getSimpleName().equalsIgnoreCase("JFormattedTextField")
                    || c.getClass().getSimpleName().equalsIgnoreCase("JTextArea") || c.getClass().getSimpleName().equalsIgnoreCase("JComboBox")
                    || c.getClass().getSimpleName().equalsIgnoreCase("JButton") || c.getClass().getSimpleName().equalsIgnoreCase("JCheckBox")) {
                //System.out.println(c.getClass().getSimpleName());
                c.addKeyListener(new MyKeyListener());
            }
        }

        System.out.println(jPanel2.getComponentCount());
        for (int i = 0; i < jPanel2.getComponentCount(); i++) {
            Component c = jPanel2.getComponent(i);
            if (c.getClass().getSimpleName().equalsIgnoreCase("JTEXTFIELD") || c.getClass().getSimpleName().equalsIgnoreCase("JFormattedTextField")
                    || c.getClass().getSimpleName().equalsIgnoreCase("JTextArea") || c.getClass().getSimpleName().equalsIgnoreCase("JComboBox")
                    || c.getClass().getSimpleName().equalsIgnoreCase("JButton") || c.getClass().getSimpleName().equalsIgnoreCase("JCheckBox")) {
                //System.out.println(c.getClass().getSimpleName());
                c.addKeyListener(new MyKeyListener());
            }
        }

        setUpBtn();
    }//GEN-LAST:event_formInternalFrameOpened

    private void btnSearchActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSearchActionPerformed
        udfFilter();
        txtSearch.requestFocus();
    }//GEN-LAST:event_btnSearchActionPerformed

    private void btnCloseActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCloseActionPerformed
        udfCancel();
    }//GEN-LAST:event_btnCloseActionPerformed

    private void btnDeleteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnDeleteActionPerformed
        if (table.getRowCount() > 0 || getBEdit()) {
            udfUpdateData();
        }
    }//GEN-LAST:event_btnDeleteActionPerformed

    private void btnEditActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnEditActionPerformed
        udfEdit();
    }//GEN-LAST:event_btnEditActionPerformed

    private void btnNewActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnNewActionPerformed
        udfNew();
    }//GEN-LAST:event_btnNewActionPerformed
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnClose;
    private javax.swing.JButton btnDelete;
    private javax.swing.JButton btnEdit;
    private javax.swing.JButton btnNew;
    private javax.swing.JButton btnSearch;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JToolBar jToolBar1;
    private javax.swing.JToolBar jToolBar2;
    private javax.swing.JLabel lblKota;
    private javax.swing.JTable table;
    private javax.swing.JTextField txtAlamat;
    private javax.swing.JTextField txtHP;
    private javax.swing.JTextField txtKode;
    private javax.swing.JTextField txtKontak;
    private javax.swing.JTextField txtKota;
    private javax.swing.JTextField txtNama;
    private javax.swing.JTextField txtSearch;
    private javax.swing.JTextField txtTelepon;
    // End of variables declaration//GEN-END:variables
    private Boolean bNew;
    private Boolean bEdit;
}
