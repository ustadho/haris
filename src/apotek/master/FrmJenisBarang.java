/*
 * FrmJenisBarang.java
 *
 * Created on December 5, 2006, 10:45 AM
 */
package apotek.master;

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.FocusTraversalPolicy;
import java.awt.KeyboardFocusManager;
import java.awt.Rectangle;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Vector;
import javax.swing.JFormattedTextField;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.JTextField;
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
public class FrmJenisBarang extends javax.swing.JInternalFrame {

    private String idUnit;
    private String nmUnit;
    private String singkatan;
    private String supervisor;
    DefaultTableModel modelJenis;
    private String sClose = "close";
    private ListRsbm lst;
    static String sID = "";
    static String sTgl = "";
    private Connection conn;
    private boolean bAsc;
    private GeneralFunction fn = new GeneralFunction();

    /**
     * Creates new form FrmJenisBarang
     */
    public FrmJenisBarang(Connection nCon) {
        initComponents();
        conn = nCon;
    }

    public Connection getConn() {
        return conn;
    }

    public void setConn(Connection conn) {
        this.conn = conn;
    }

    public void initJDBC() {
            
            SelectionListener listener = new SelectionListener(tblJenisBarang);
            tblJenisBarang.getSelectionModel().addListSelectionListener(listener);
            tblJenisBarang.getColumnModel().getSelectionModel().addListSelectionListener(listener);
            tblJenisBarang.setRequestFocusEnabled(true);

            if (modelJenis.getRowCount() > 0) {
                tblJenisBarang.setRowSelectionInterval(0, 0);
            }

            setBEdit(false);
            setBNew(false);

            TableLook();
            udfFilter();

    }

    private void TableLook() {
        tblJenisBarang.getColumnModel().getColumn(0).setMaxWidth(70);     //kode
        tblJenisBarang.getColumnModel().getColumn(0).setPreferredWidth(70);

        tblJenisBarang.setRowHeight(20);
        for (int i = 0; i < tblJenisBarang.getColumnCount(); i++) {
            tblJenisBarang.getColumnModel().getColumn(i).setCellRenderer(new MyRowRenderer());
        }

        if (modelJenis.getRowCount() > 0) {
            tblJenisBarang.changeSelection(0, 0, false, false);
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
                    if (!getBEdit() && tblJenisBarang.getRowCount() > 0) {
                        udfUpdateData();
                    }

                    break;
                }
                case KeyEvent.VK_ENTER: {
                    Component ct = KeyboardFocusManager.getCurrentKeyboardFocusManager().getPermanentFocusOwner();
                    if (!(ct.getClass().getSimpleName().equalsIgnoreCase("JTABLE"))) {
                        if (!lst.isVisible()) {
                            Component c = findNextFocus();
                            c.requestFocus();
                        } else {
                            lst.requestFocus();
                        }
                    }
                    break;
                }
                case KeyEvent.VK_DOWN: {
                    Component ct = KeyboardFocusManager.getCurrentKeyboardFocusManager().getPermanentFocusOwner();
                    if (!(ct.getClass().getSimpleName().equalsIgnoreCase("JTABLE"))) {
                        if (!lst.isVisible()) {
                            Component c = findNextFocus();
                            c.requestFocus();
                        } else {
                            lst.requestFocus();
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

            tblJenisBarang.setEnabled(false);
            txtNama.setEditable(true);

            tblJenisBarang.requestFocus();

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

            tblJenisBarang.setEnabled(true);
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
                txtKode.setText(table.getValueAt(rowPos, 0).toString());
                txtNama.setText(table.getValueAt(rowPos, 1).toString());
            }
            if (table.getRowCount() == 0) {
                txtKode.setText("");
                txtNama.setText("");
            }

        }
    }

    private void udfUpdateData() {
        //JenisBarangBean nB =new JenisBarangBean();
        int hsl = 0;

        if (getBEdit()) {
            try {
                conn.setAutoCommit(false);
                if (getBNew()) {        //Add
                    Statement st = conn.createStatement();
                    ResultSet rs = st.executeQuery("select fn_save_jenis('', '" + txtNama.getText() + "')");

                    if (rs.next()) {
                        txtKode.setText(rs.getString(1));
                        modelJenis.addRow(new Object[]{txtKode.getText(),
                            txtNama.getText(),});
                        setBEdit(false);
                        tblJenisBarang.setRowSelectionInterval(modelJenis.getRowCount() - 1, modelJenis.getRowCount() - 1);
                        setBEdit(false);
                        setBNew(false);
                        setUpBtn();
                    } else {
                        pesanError("Insert jenis barang gagal. Silakan coba sekali lagi!");
                        System.out.println(hsl);
                    }
                    rs.close();
                    st.close();

                    //System.out.println(hsl);
                } else {
                    Statement st = conn.createStatement();
                    ResultSet rs = st.executeQuery("select fn_save_jenis('" + txtKode.getText() + "', '" + txtNama.getText() + "')");

                    if (!rs.next()) {
                        pesanError("Gagal Update!");
                        System.out.println(hsl);

                    } else {
                        tblJenisBarang.setValueAt(txtNama.getText(), tblJenisBarang.getSelectedRow(), 1);
                        System.out.println(hsl);
                        setBEdit(false);
                        setBNew(false);
                        setUpBtn();
                    }
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
                String s = tblJenisBarang.getValueAt(tblJenisBarang.getSelectedRow(), 1).toString();
                if (JOptionPane.showConfirmDialog(null, "Anda Yakin Untuk mengapus '" + s + "' ?", "SHS go Open Sources", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
                    int iPosDel = tblJenisBarang.getSelectedRow();

                    Statement st = conn.createStatement();
                    hsl = st.executeUpdate("delete from jenis_barang where kode_jenis='" + txtKode.getText() + "'");

                    conn.commit();
                    modelJenis.removeRow(iPosDel);
                    if (iPosDel < modelJenis.getRowCount() && modelJenis.getRowCount() > 0) {
                        tblJenisBarang.setRowSelectionInterval(iPosDel, iPosDel);
                        tblJenisBarang.requestFocus();
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
            tblJenisBarang.requestFocus();
        }
    }

    private void udfEdit() {
        if (tblJenisBarang.getRowCount() > 0) {
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

        txtKode.setText("");
        txtNama.setText("");
        txtNama.requestFocus();
    }

    private void udfCancel() {
        if (getBEdit()) {
            setBEdit(false);
            setBNew(false);
            if (tblJenisBarang.getRowCount() > 0) {
                int rowPos = tblJenisBarang.getSelectedRow();
                txtKode.setText(tblJenisBarang.getValueAt(rowPos, 0).toString());
                txtNama.setText(tblJenisBarang.getValueAt(rowPos, 1).toString());
            }
            setUpBtn();
            tblJenisBarang.setRequestFocusEnabled(true);

        } else {
            this.dispose();
        }
    }

    private void udfFilter() {
        modelJenis = (DefaultTableModel) tblJenisBarang.getModel();
        int i = 0;
        try {
            String sQry = "select kode_jenis, coalesce(jenis_barang,'') as jenis_barang "
                    + "from jenis_barang "
                    + "where kode_jenis || coalesce(jenis_barang,'') ilike '%" + txtFilter.getText() + "%' "
                    + "order by 1";

            modelJenis.setNumRows(0);
            ResultSet rs = conn.createStatement().executeQuery(sQry);

            while (rs.next()) {
                modelJenis.addRow(new Object[]{rs.getString("kode_jenis"),
                    rs.getString("jenis_barang")
                });
            }

            if (modelJenis.getRowCount() > 0) {
                tblJenisBarang.setRowSelectionInterval(0, 0);
                fn.setAutoResizeColWidth(tblJenisBarang);
            }
            rs.close();
        } catch (SQLException eswl) {
            System.out.println(eswl.getMessage());
        }
        if (i > 0) {
            tblJenisBarang.requestFocusInWindow();
            tblJenisBarang.setRowSelectionInterval(0, 0);
        }
    }

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
        txtFilter = new javax.swing.JTextField();
        jScrollPane1 = new javax.swing.JScrollPane();
        tblJenisBarang = new javax.swing.JTable();
        jPanel2 = new javax.swing.JPanel();
        txtKode = new javax.swing.JTextField();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        txtNama = new javax.swing.JTextField();

        setClosable(true);
        setIconifiable(true);
        setMaximizable(true);
        setResizable(true);
        setTitle("Jenis Barang");
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
        getContentPane().setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

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

        txtFilter.setMaximumSize(new java.awt.Dimension(200, 24));
        txtFilter.setMinimumSize(new java.awt.Dimension(4, 4));
        txtFilter.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtFilterKeyReleased(evt);
            }
        });
        jToolBar2.add(txtFilter);

        jPanel1.add(jToolBar2, new org.netbeans.lib.awtextra.AbsoluteConstraints(180, 0, 555, 50));

        getContentPane().add(jPanel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(4, 0, 741, 50));

        jScrollPane1.setBackground(new java.awt.Color(255, 255, 255));

        tblJenisBarang.setAutoCreateRowSorter(true);
        tblJenisBarang.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null},
                {null, null},
                {null, null},
                {null, null}
            },
            new String [] {
                "Kode", "Jenis Barang"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class
            };
            boolean[] canEdit = new boolean [] {
                false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        tblJenisBarang.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_OFF);
        jScrollPane1.setViewportView(tblJenisBarang);

        getContentPane().add(jScrollPane1, new org.netbeans.lib.awtextra.AbsoluteConstraints(5, 50, 740, 250));

        jPanel2.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        jPanel2.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        txtKode.setFont(new java.awt.Font("Dialog", 1, 12));
        txtKode.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        txtKode.setEnabled(false);
        jPanel2.add(txtKode, new org.netbeans.lib.awtextra.AbsoluteConstraints(96, 9, 110, 24));

        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel1.setText("Kode Jenis");
        jPanel2.add(jLabel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(9, 12, 78, -1));

        jLabel2.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel2.setText("Jenis Barang");
        jPanel2.add(jLabel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(8, 38, -1, -1));

        txtNama.setFont(new java.awt.Font("Dialog", 1, 12));
        txtNama.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        jPanel2.add(txtNama, new org.netbeans.lib.awtextra.AbsoluteConstraints(96, 36, 480, 24));

        getContentPane().add(jPanel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(5, 301, 740, 72));

        java.awt.Dimension screenSize = java.awt.Toolkit.getDefaultToolkit().getScreenSize();
        setBounds((screenSize.width-760)/2, (screenSize.height-417)/2, 760, 417);
    }// </editor-fold>//GEN-END:initComponents

    private void formInternalFrameClosed(javax.swing.event.InternalFrameEvent evt) {//GEN-FIRST:event_formInternalFrameClosed
    }//GEN-LAST:event_formInternalFrameClosed

    private void formInternalFrameOpened(javax.swing.event.InternalFrameEvent evt) {//GEN-FIRST:event_formInternalFrameOpened
        modelJenis = (DefaultTableModel) tblJenisBarang.getModel();
        modelJenis.setNumRows(0);
        tblJenisBarang.setModel(modelJenis);

        initJDBC();
        setBEdit(false);
        setBNew(false);

        lst = new ListRsbm();
        lst.setVisible(false);
        lst.setSize(500, 150);
        lst.con = conn;

        requestFocusInWindow(true);
        tblJenisBarang.requestFocusInWindow();
        MyKeyListener kListener=new MyKeyListener();
        fn.addKeyListenerInContainer(jToolBar1, kListener, txtFocusListener);
        fn.addKeyListenerInContainer(jToolBar2, kListener, txtFocusListener);
        fn.addKeyListenerInContainer(jPanel1, kListener, txtFocusListener);
        fn.addKeyListenerInContainer(jPanel2, kListener, txtFocusListener);
        
        jScrollPane1.addKeyListener(new MyKeyListener());
        tblJenisBarang.addKeyListener(new MyKeyListener());

        System.out.println(jPanel1.getComponentCount());
        setUpBtn();
    }//GEN-LAST:event_formInternalFrameOpened

    private void btnCloseActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCloseActionPerformed
        udfCancel();
    }//GEN-LAST:event_btnCloseActionPerformed

    private void btnDeleteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnDeleteActionPerformed
        if (tblJenisBarang.getRowCount() > 0 || getBEdit()) {
            udfUpdateData();
        }
    }//GEN-LAST:event_btnDeleteActionPerformed

    private void btnEditActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnEditActionPerformed
        udfEdit();
    }//GEN-LAST:event_btnEditActionPerformed

    private void btnNewActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnNewActionPerformed
        udfNew();
    }//GEN-LAST:event_btnNewActionPerformed

    private void txtFilterKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtFilterKeyReleased
        udfFilter();
        txtFilter.requestFocus();
}//GEN-LAST:event_txtFilterKeyReleased
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnClose;
    private javax.swing.JButton btnDelete;
    private javax.swing.JButton btnEdit;
    private javax.swing.JButton btnNew;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JToolBar jToolBar1;
    private javax.swing.JToolBar jToolBar2;
    private javax.swing.JTable tblJenisBarang;
    private javax.swing.JTextField txtFilter;
    private javax.swing.JTextField txtKode;
    private javax.swing.JTextField txtNama;
    // End of variables declaration//GEN-END:variables
    private Boolean bNew;
    private Boolean bEdit;
    
    private FocusListener txtFocusListener=new FocusListener() {
        public void focusGained(FocusEvent e) {
            if(e.getSource() instanceof JTextField || e.getSource() instanceof JFormattedTextField){
                ((JTextField)e.getSource()).setBackground(Color.YELLOW);
                if((e.getSource() instanceof JTextField && ((JTextField)e.getSource()).getName()!=null && ((JTextField)e.getSource()).getName().equalsIgnoreCase("textEditor"))){
                    ((JTextField)e.getSource()).setSelectionStart(0);
                    ((JTextField)e.getSource()).setSelectionEnd(((JTextField)e.getSource()).getText().length());
                }
            }
        }

        public void focusLost(FocusEvent e) {
            if(e.getSource() instanceof  JTextField  || e.getSource() instanceof  JFormattedTextField){
                ((JTextField)e.getSource()).setBackground(Color.WHITE);
           }
        }
    } ;
}
