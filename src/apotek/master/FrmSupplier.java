/*
 * FrmSupplier.java
 *
 * Created on December 2, 2006, 10:23 AM
 */
package apotek.master;

/**
 *
 * @author root
 */
import apotek.SupplierBean;
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
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Vector;
import javax.swing.JCheckBox;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
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
import javax.swing.text.DefaultFormatter;
import javax.swing.text.DefaultFormatterFactory;
import javax.swing.text.MaskFormatter;
import javax.swing.text.NumberFormatter;
import main.GeneralFunction;
import main.ListRsbm;

public class FrmSupplier extends javax.swing.JInternalFrame {

    private String idUnit;
    private String nmUnit;
    private String singkatan;
    private String supervisor;
    DefaultTableModel modelSupp;
    private String sClose = "close";
    private String sQry = "select * from rm_unit order by 1";
    static String sID = "";
    static String sTgl = "";
    private Connection conn;
    private boolean bAsc;
    private Object srcForm;

    /**
     * Creates new form FrmSupplier
     */
    public FrmSupplier(Connection nCon) {
        initComponents();
        conn = nCon;
        setMaximizable(true);
        SelectionListener listener = new SelectionListener(tblSupp);
        tblSupp.getSelectionModel().addListSelectionListener(listener);
        tblSupp.getColumnModel().getSelectionModel().addListSelectionListener(listener);
        tblSupp.setRequestFocusEnabled(true);
    }

    public Connection getConn() {
        return conn;
    }

    public void setConn(Connection conn) {
        this.conn = conn;
    }

    public void initJDBC() {
        
            SelectionListener listener = new SelectionListener(tblSupp);
            tblSupp.getSelectionModel().addListSelectionListener(listener);
            tblSupp.getColumnModel().getSelectionModel().addListSelectionListener(listener);
            tblSupp.setRequestFocusEnabled(true);

            if (modelSupp.getRowCount() > 0) {
                tblSupp.setRowSelectionInterval(0, 0);
            }

            setBEdit(false);
            setBNew(false);

            TableLook();

    }

    private void TableLook() {
//        tblSupp.getColumnModel().getColumn(0).setMaxWidth(50);     //kode
//        tblSupp.getColumnModel().getColumn(0).setPreferredWidth(50);
//        tblSupp.getColumnModel().getColumn(1).setMaxWidth(150);    //Nama Supplier
//        tblSupp.getColumnModel().getColumn(1).setPreferredWidth(150);
//        tblSupp.getColumnModel().getColumn(2).setMaxWidth(200);    //Alamat
//        tblSupp.getColumnModel().getColumn(2).setPreferredWidth(200);
//        tblSupp.getColumnModel().getColumn(3).setMaxWidth(100);     //Kota
//        tblSupp.getColumnModel().getColumn(3).setPreferredWidth(100);
//        tblSupp.getColumnModel().getColumn(4).setMaxWidth(80);     //Telepon
//        tblSupp.getColumnModel().getColumn(4).setPreferredWidth(80);
//        tblSupp.getColumnModel().getColumn(5).setMaxWidth(130);       //Contact person
//        tblSupp.getColumnModel().getColumn(5).setPreferredWidth(130);
//        tblSupp.getColumnModel().getColumn(6).setMaxWidth(80);         //HP
//        tblSupp.getColumnModel().getColumn(6).setPreferredWidth(80);
//        tblSupp.getColumnModel().getColumn(7).setMaxWidth(200);        //Keterangan
//        tblSupp.getColumnModel().getColumn(7).setPreferredWidth(200);
//        tblSupp.getColumnModel().getColumn(8).setMaxWidth(50);         //Top
//        tblSupp.getColumnModel().getColumn(8).setPreferredWidth(50);
//        tblSupp.getColumnModel().getColumn(9).setMaxWidth(100);         //Top
//        tblSupp.getColumnModel().getColumn(9).setPreferredWidth(100);

        tblSupp.setRowHeight(20);
        for (int i = 0; i < tblSupp.getColumnCount(); i++) {
            tblSupp.getColumnModel().getColumn(i).setCellRenderer(new MyRowRenderer());
        }

        if (modelSupp.getRowCount() > 0) {
            tblSupp.changeSelection(0, 0, false, false);
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

    public void setObjForm(Object aThis) {
        this.srcForm = aThis;
    }

    private void setLocationRelativeTo(Object object) {
    }

    private void udfFilter() {
        String sQry = "select * from fn_supp_list (0, 'all', 'ilike', '"+txtSearch.getText()+"') as (kode_supplier varchar, "
                + "nama_supplier varchar, alamat varchar, nama_kota varchar, telp varchar, "
                + "contact_person varchar, hp varchar, keterangan varchar, top int, jenis_supplier varchar, active boolean)";

        System.out.println(sQry);

        try {
            ResultSet rs = conn.createStatement().executeQuery(sQry);

            modelSupp.setNumRows(0);
            while (rs.next()) {
                modelSupp.addRow(new Object[]{rs.getString("kode_supplier"),
                    rs.getString("nama_supplier"),
                    rs.getString("alamat"),
                    rs.getString("nama_kota"),
                    rs.getString("telp"),
                    rs.getString("contact_person"),
                    rs.getString("hp"),
                    rs.getString("keterangan"),
                    rs.getString("top"),
                    rs.getString("jenis_supplier"),
                    rs.getBoolean("active")});
            }
            if(tblSupp.getRowCount() >0){
                tblSupp.setRowSelectionInterval(0, 0);
                fn.setAutoResizeColWidth(tblSupp);
            }
        }catch(SQLException se){
            JOptionPane.showMessageDialog(this, se.getMessage());
        }
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
            sortAllRowsBy(modelSupp, vColIndex, bSt);
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
            JCheckBox checkBox = new JCheckBox();

            if ((column == 0) || (column == 1) || (column == 2) || (column == 3) || (column == 5) || (column == 6) || (column == 7) || (column == 9)) {
                JTextField jt = new JTextField();
                setHorizontalAlignment(jt.LEFT);
            }

            if (column == 6) {
                DefaultFormatter fmt = new NumberFormatter(new DecimalFormat("#,###,###"));
                DefaultFormatterFactory fmtFactory = new DefaultFormatterFactory(fmt, fmt, fmt);
                JFormattedTextField ft = new JFormattedTextField();
                ft.setFormatterFactory(fmtFactory);
                //ft.setValue(value);
                //setValue(ft.getText());
                setValue(value);
                setHorizontalAlignment(ft.RIGHT);
            } else {
                setValue(value);
            }

            Color g1 = new Color(230, 243, 255);//[251,251,235]
            Color g2 = new Color(219, 238, 255);//[247,247,218]

            Color w1 = new Color(255, 255, 255);
            Color w2 = new Color(250, 250, 250);

            Color h1 = new Color(255, 240, 240);
            Color h2 = new Color(250, 230, 230);

            Color g;
            Color w;
            Color h;

//            if(column%2==0){
            g = g1;
            w = w1;
            h = h1;
//            }else{
//                g = g2;
//                w = w2;
//                h = h2;
//            }

            if (value instanceof Boolean) { // Boolean
                checkBox.setSelected(((Boolean) value).booleanValue());
                checkBox.setHorizontalAlignment(JLabel.CENTER);
                if (row % 2 == 0) {
                    checkBox.setBackground(w);
                } else {
                    checkBox.setBackground(g);
                }
//                  if(isSelected && column==10){
//                        //setBackground(new Color(248,255,167));//[174,212,254]
//                        checkBox.setBackground(new Color(69,167,14));
//                        checkBox.setForeground(new Color(51,102,255));
//                  }
                //else 
                if (isSelected) {
                    checkBox.setBackground(new Color(248, 255, 167));//51,102,255));
                    //checkBox.setForeground(new Color(255,255,255));
                }

                return checkBox;
            }

            if (row % 2 == 0) {
                setBackground(w);
            } else {
                setBackground(g);
            }

            if (isSelected) {
                setBackground(new Color(248, 255, 167));//[174,212,254]

            }
            setFont(new java.awt.Font("Dialog", 0, 12));

            return this;
        }
    }

//    public class MyRowRenderer extends DefaultTableCellRenderer implements TableCellRenderer{
//        JCheckBox checkBox = new JCheckBox();
//        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
//            //Component comp = getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
//            
//            Color g1 = new Color(239,234,240);//-->>(251,236,177);// Kuning         [251,251,235]
//            Color g2 = new Color(239,234,240);//-->>(241,226,167);// Kuning         [247,247,218]
//            
//            Color w1 = new Color(255,255,255);// Putih
//            Color w2 = new Color(250,250,250);// Putih Juga
//            
//            Color h1 = new Color(255,240,240);// Merah muda
//            Color h2 = new Color(250,230,230);// Merah Muda
//            
//            Color g;
//            Color w;
//            Color h;
//            
//            if(column%2==0){
//                g = g1;
//                w = w1;
//                h = h1;
//            }else{
//                g = g2;
//                w = w2;
//                h = h2;
//            }
//            
//            
//            if(column==4){
//                JTextField tx=new JTextField();
//                setHorizontalAlignment(tx.RIGHT);
//                //value=ft.format(value);
//            }
//            
////            if(column>=5){
////                JCheckBox tx=new JCheckBox();
////                setHorizontalAlignment(tx.CENTER);
////            }
//            
//            
//            setForeground(new Color(0,0,0));
//            if (row%2==0){
//                setBackground(w);
//            }else{
//                setBackground(g);
//            }
//            if(isSelected){
//                //setBackground(new Color(248,255,167));//[174,212,254]
//                setBackground(new Color(51,102,255));
//                setForeground(new Color(255,255,255));
//            }
//            
//            if (value instanceof Boolean) { // Boolean
//                  checkBox.setSelected(((Boolean) value).booleanValue());
//                  checkBox.setHorizontalAlignment(JLabel.CENTER);
//                  if (row%2==0){
//                     checkBox.setBackground(w);
//                  }else{
//                     checkBox.setBackground(g);
//                  }
//                  if(isSelected && column==level+4){
//                        //setBackground(new Color(248,255,167));//[174,212,254]
//                        checkBox.setBackground(new Color(69,167,14));
//                        checkBox.setForeground(new Color(51,102,255));
//                  }else if (isSelected){
//                            checkBox.setBackground(new Color(51,102,255));
//                            checkBox.setForeground(new Color(255,255,255));
//                        }
//                  
//                  return checkBox;
//            }
//            
//            setValue(value);
//            return this;
//        }
//    }
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


                case KeyEvent.VK_F12: {  //Delete
                    if (!getBEdit() && tblSupp.getRowCount() > 0) {
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
                            fn.lstRequestFocus();
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

            tblSupp.setEnabled(false);
            txtKode.setEditable(true);
            txtNama.setEditable(true);
            txtAlamat.setEditable(true);
            txtKodeKota.setEditable(true);
            txtTelp.setEditable(true);
            txtCP.setEditable(true);
            txtHP.setEditable(true);
            txtTop.setEditable(true);
            txtKet.setEditable(true);
            txtKodeJenis.setEditable(true);
            lblJenis.setEnabled(true);
            chkActive.setEnabled(true);
            tblSupp.requestFocus();

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

            tblSupp.setEnabled(true);
            txtKode.setEditable(false);
            txtNama.setEditable(false);
            txtAlamat.setEditable(false);
            txtKodeKota.setEditable(false);
            txtTelp.setEditable(false);
            txtCP.setEditable(false);
            txtHP.setEditable(false);
            txtKet.setEditable(false);
            txtTop.setEditable(false);
            txtKodeJenis.setEditable(false);
            chkActive.setEnabled(false);
            txtNama.requestFocus();
        }
    }

    private void udfLoadSupplier() {
        if(tblSupp.getSelectedRow() <0) return;
        System.out.println(tblSupp.getValueAt(tblSupp.getSelectedRow(), 0));
        String sKode = tblSupp.getValueAt(tblSupp.getSelectedRow(), 0).toString();
        String sQry = "select kode_supplier, coalesce(active,false) as active,"
                + "coalesce(nama_supplier,'') as nama_supplier, "
                + "coalesce(alamat,'') as alamat,"
                + "coalesce(kode_kota,'') as kode_kota, coalesce(nama_kota,'')as nama_kota,"
                + "coalesce(telp,'') as telepon,"
                + "coalesce(contact_person,'') as contact_person,"
                + "coalesce(hp,'') as hp,"
                + "coalesce(keterangan,'') as keterangan,"
                + "coalesce(s.kode_jenis_supp,'') as kode_jenis_supp,"
                + "coalesce(jenis_supplier,'') as jenis_supplier,"
                + "coalesce(top,0) as top "
                + "from phar_supplier s "
                + "left join kota using(kode_kota) "
                + "left join jenis_supplier js on js.kode_jenis_supp=s.kode_jenis_supp "
                + "where kode_supplier='" + sKode + "'";
        try {
            Statement st = conn.createStatement();
            ResultSet rs = st.executeQuery(sQry);

            if (rs.next()) {
                txtKode.setText(sKode);
                chkActive.setSelected(rs.getBoolean("active"));
                txtNama.setText(rs.getString("nama_supplier"));
                txtAlamat.setText(rs.getString("alamat"));
                txtKodeKota.setText(rs.getString("kode_kota"));
                lblKota.setText(rs.getString("nama_kota"));
                txtTelp.setText(rs.getString("telepon"));
                txtCP.setText(rs.getString("contact_person"));
                txtHP.setText(rs.getString("hp"));
                txtKet.setText(rs.getString("keterangan"));
                txtKodeJenis.setText(rs.getString("kode_jenis_supp"));
                lblJenis.setText(rs.getString("jenis_supplier"));
                txtTop.setText(rs.getString("top"));

            } else {
                udfBlank();
            }

        } catch (SQLException ex) {
            ex.printStackTrace();
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
        SupplierBean sBean = new SupplierBean();
        sBean.setConn(conn);
        sBean.setKodeSupp(txtKode.getText());
        sBean.setNamaSup(txtNama.getText());
        sBean.setAlamat(txtAlamat.getText());
        sBean.setKodeKota(txtKodeKota.getText());
        sBean.setTelepon(txtKode.getText());
        sBean.setContactPerson(txtNama.getText());
        sBean.setHp(txtAlamat.getText());
        //sBean.setTop((integer) txtKodeKota.getText().compareTo());

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
                udfLoadSupplier();
            }
            if (table.getRowCount() == 0) {
                udfBlank();
            }

        }
    }

    private boolean udfCekBeforeSave() {
        boolean b = true;
//        if(txtKode.getText().trim().equalsIgnoreCase("")){
//            JOptionPane.showMessageDialog(this, "Silakan isi kode supplier terlebih dulu!","Joss..", JOptionPane.OK_OPTION);
//            txtKode.requestFocus();
//            return false;
//        }
        if (txtNama.getText().trim().equalsIgnoreCase("")) {
            JOptionPane.showMessageDialog(this, "Silakan nama suplier dulu!", "Apotek", JOptionPane.OK_OPTION);
            txtKode.requestFocus();
            return false;
        }
        if (txtKodeJenis.getText().trim().equalsIgnoreCase("")) {
            JOptionPane.showMessageDialog(this, "Silakan Pilih jenis supplier terlebih dulu!", "Apotek", JOptionPane.OK_OPTION);
            txtKode.requestFocus();
            return false;
        }
        return b;
    }

    private int udfGetInt(String sNum) {
        int hsl = 0;
        if (!sNum.trim().equalsIgnoreCase("")) {
            try {
                hsl = Integer.valueOf(sNum.replace(",", ""));
            } catch (NumberFormatException ne) {
                hsl = 0;
            } catch (IllegalArgumentException i) {
                hsl = 0;
            }
        }
        return hsl;
    }

    private void udfUpdateData() {
        SupplierBean nB = new SupplierBean();
        boolean hsl = false;

        if (getBEdit() && udfCekBeforeSave()) {
            try {
                conn.setAutoCommit(false);
                if (getBNew()) {        //Add

                    System.out.println("OK New");

                    nB.setConn(conn);
                    nB.sID = sID;
                    txtKode.setText(nB.getNewCode(txtNama.getText().trim().substring(0, 1)));
                    nB.setKodeSupp(txtKode.getText());
                    nB.setNamaSup(txtNama.getText());
                    nB.setAlamat(txtAlamat.getText().trim());
                    nB.setKodeKota(txtKodeKota.getText().trim());
                    nB.setTelepon(txtTelp.getText().trim());
                    nB.setContactPerson(txtCP.getText().trim());
                    nB.setHp(txtHP.getText().trim());
                    nB.setKeterangan(txtKet.getText().trim());
                    nB.setKodeJenis(txtKodeJenis.getText().trim());
                    nB.setActive(chkActive.isSelected());

                    if (txtTop.getText().trim().equals("")) {
                        nB.setTop(0);
                    } else {
                        nB.setTop(Integer.parseInt(txtTop.getText()));
                    }

                    hsl = nB.Add();

                    if (!hsl) {
                        pesanError("Kode Supplier telah terpakai. Silakan inputkan kode lain!");
                        System.out.println(hsl);

                    } else {
                        if (modelSupp != null) {
                            modelSupp.addRow(new Object[]{txtKode.getText(),
                                txtNama.getText(),
                                txtAlamat.getText(),
                                lblKota.getText(),
                                txtTelp.getText(),
                                txtCP.getText(),
                                txtHP.getText(),
                                txtKet.getText(),
                                udfGetInt(txtTop.getText()),
                                lblJenis.getText(),
                                chkActive.isSelected()});
                        }

                        setBEdit(false);
                        System.out.println(hsl);

                        setBEdit(false);
                        setBNew(false);
                        setUpBtn();
                        tblSupp.setRowSelectionInterval(tblSupp.getRowCount() - 1, tblSupp.getRowCount() - 1);

                    }

                } else {
                    System.out.println("OK EDIT");
//                    System.out.println(rs.getRow());

                    nB.setConn(conn);
                    nB.sID = sID;
                    nB.setKodeSupp(txtKode.getText());
                    nB.setNamaSup(txtNama.getText());
                    nB.setAlamat(txtAlamat.getText().trim());
                    nB.setKodeKota(txtKodeKota.getText().trim());
                    nB.setTelepon(txtTelp.getText().trim());
                    nB.setContactPerson(txtCP.getText().trim());
                    nB.setHp(txtHP.getText().trim());
                    nB.setKeterangan(txtKet.getText().trim());
                    if (txtTop.getText().trim().equalsIgnoreCase("")) {
                        nB.setTop(0);
                    } else {
                        nB.setTop(Integer.parseInt(txtTop.getText().trim()));
                    }

                    nB.setKodeJenis(txtKodeJenis.getText().trim());
                    nB.setActive(chkActive.isSelected());

                    int i = nB.Edit(txtKode.getText());

                    if (i == 0) {
                        pesanError("Gagal Update!");
                        System.out.println(hsl);

                    } else {
                        tblSupp.setValueAt(txtNama.getText(), tblSupp.getSelectedRow(), 1);
                        tblSupp.setValueAt(txtAlamat.getText(), tblSupp.getSelectedRow(), 2);
                        tblSupp.setValueAt(lblKota.getText(), tblSupp.getSelectedRow(), 3);
                        tblSupp.setValueAt(txtTelp.getText(), tblSupp.getSelectedRow(), 4);
                        tblSupp.setValueAt(txtCP.getText(), tblSupp.getSelectedRow(), 5);
                        tblSupp.setValueAt(txtHP.getText(), tblSupp.getSelectedRow(), 6);
                        tblSupp.setValueAt(txtKet.getText(), tblSupp.getSelectedRow(), 7);
                        tblSupp.setValueAt(udfGetInt(txtTop.getText()), tblSupp.getSelectedRow(), 8);
                        tblSupp.setValueAt(lblJenis.getText(), tblSupp.getSelectedRow(), 9);
                        tblSupp.setValueAt(chkActive.isSelected(), tblSupp.getSelectedRow(), 10);


                        System.out.println(hsl);
                        setBEdit(false);
                        setBNew(false);
                        setUpBtn();
                    }
                }

                conn.commit();
                conn.setAutoCommit(true);

                if (srcForm != null) {
                    dispose();
                }
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
                String s = tblSupp.getValueAt(tblSupp.getSelectedRow(), 1).toString();
                if (JOptionPane.showConfirmDialog(null, "Anda Yakin Untuk mengapus '" + s + "' ?", "SHS go Open Sources", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
                    int iPosDel = tblSupp.getSelectedRow();
                    nB.setConn(conn);
                    nB.Delete(txtKode.getText());

                    conn.commit();
                    modelSupp.removeRow(iPosDel);
                    if (iPosDel < modelSupp.getRowCount() && modelSupp.getRowCount() > 0) {
                        tblSupp.setRowSelectionInterval(iPosDel, iPosDel);
                        tblSupp.requestFocus();
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
            tblSupp.requestFocus();
        }
    }

    public String getKodeSupplier() {
        return txtKode.getText();
    }

    private void udfEdit() {
        if (tblSupp.getRowCount() > 0) {
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

//        try{
//            Statement st=conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE,ResultSet.CONCUR_READ_ONLY);
//            ResultSet rs=st.executeQuery("select fn_get_new_phar_supplier()");
//            
//            if (rs.next()) txtKode.setText(rs.getString(1));
//            
//            rs.close();
//            st.close();
//            
//        }catch(SQLException se){System.out.println(se.getMessage());}
        txtKode.setText("");
        txtNama.setText("");
        txtAlamat.setText("");
        txtKodeKota.setText("");
        lblKota.setText("");
        txtTelp.setText("");
        txtCP.setText("");
        txtHP.setText("");
        txtKet.setText("");
        txtTop.setText("");
        txtKodeJenis.setText("");
        lblJenis.setText("");
        txtKode.requestFocus();
        chkActive.setSelected(true);
    }

    private void udfCancel() {
        if (getBEdit()) {
            setBEdit(false);
            setBNew(false);
            if (tblSupp.getRowCount() > 0) {
                int rowPos = tblSupp.getSelectedRow();
                txtKode.setText(tblSupp.getValueAt(rowPos, 0).toString());
                txtNama.setText(tblSupp.getValueAt(rowPos, 1).toString());
                txtAlamat.setText(tblSupp.getValueAt(rowPos, 2).toString());
                lblKota.setText(tblSupp.getValueAt(rowPos, 3).toString());
                txtTelp.setText(tblSupp.getValueAt(rowPos, 4).toString());
                txtCP.setText(tblSupp.getValueAt(rowPos, 5).toString());
                txtHP.setText(tblSupp.getValueAt(rowPos, 6).toString());
                txtKet.setText(tblSupp.getValueAt(rowPos, 7).toString());
                //txtTop.setText(tblSupp.getValueAt(rowPos,8).toString());

            }
            setUpBtn();
            tblSupp.setRequestFocusEnabled(true);

        } else{
            this.dispose();
        }
    }

    private void onOpen() {
        modelSupp = (DefaultTableModel) tblSupp.getModel();
        TableLook();
        fn.setConn(conn);
        int i = 0;
        udfFilter();
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
        jLabel1 = new javax.swing.JLabel();
        txtSearch = new javax.swing.JTextField();
        btnSearch = new javax.swing.JButton();
        jScrollPane2 = new javax.swing.JScrollPane();
        tblSupp = new javax.swing.JTable();
        jPanel2 = new javax.swing.JPanel();
        txtKode = new javax.swing.JTextField();
        jLabel19 = new javax.swing.JLabel();
        jLabel20 = new javax.swing.JLabel();
        jLabel21 = new javax.swing.JLabel();
        txtNama = new javax.swing.JTextField();
        jLabel22 = new javax.swing.JLabel();
        jLabel23 = new javax.swing.JLabel();
        jLabel24 = new javax.swing.JLabel();
        jLabel25 = new javax.swing.JLabel();
        jLabel26 = new javax.swing.JLabel();
        txtAlamat = new javax.swing.JTextField();
        txtKodeKota = new javax.swing.JTextField();
        txtTelp = new javax.swing.JTextField();
        txtCP = new javax.swing.JTextField();
        txtHP = new javax.swing.JTextField();
        txtTop = new javax.swing.JTextField();
        lblKota = new javax.swing.JLabel();
        txtKet = new javax.swing.JTextField();
        jLabel27 = new javax.swing.JLabel();
        jLabel28 = new javax.swing.JLabel();
        jLabel29 = new javax.swing.JLabel();
        txtKodeJenis = new javax.swing.JTextField();
        lblJenis = new javax.swing.JLabel();
        chkActive = new javax.swing.JCheckBox();

        setClosable(true);
        setIconifiable(true);
        setMaximizable(true);
        setResizable(true);
        setTitle("Supplier");
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

        jLabel1.setText("Filter : ");

        txtSearch.setMaximumSize(new java.awt.Dimension(200, 24));
        txtSearch.setMinimumSize(new java.awt.Dimension(4, 4));
        txtSearch.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtSearchKeyReleased(evt);
            }
        });

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

        org.jdesktop.layout.GroupLayout jPanel1Layout = new org.jdesktop.layout.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel1Layout.createSequentialGroup()
                .add(jToolBar1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 204, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .add(36, 36, 36)
                .add(jLabel1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 103, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(txtSearch, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(btnSearch, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .add(276, 276, 276))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jToolBar1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 50, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, jPanel1Layout.createSequentialGroup()
                .add(jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(btnSearch, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                        .add(jLabel1)
                        .add(txtSearch, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );

        jScrollPane2.setBackground(new java.awt.Color(219, 238, 255));
        jScrollPane2.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
        jScrollPane2.setAutoscrolls(true);

        tblSupp.setAutoCreateRowSorter(true);
        tblSupp.setBackground(new java.awt.Color(219, 238, 255));
        tblSupp.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Kode", "Nama Supplier", "Alamat", "Kota", "Telepon", "Contact Person", "HP", "Keterangan", "Top", "Jenis Supplier", "Active"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.Boolean.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false, false, false, false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        tblSupp.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_OFF);
        tblSupp.getTableHeader().setReorderingAllowed(false);
        tblSupp.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                tblSuppFocusGained(evt);
            }
        });
        tblSupp.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                tblSuppKeyPressed(evt);
            }
            public void keyReleased(java.awt.event.KeyEvent evt) {
                tblSuppKeyReleased(evt);
            }
        });
        jScrollPane2.setViewportView(tblSupp);

        jPanel2.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        jPanel2.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        txtKode.setEditable(false);
        txtKode.setFont(new java.awt.Font("Dialog", 1, 12)); // NOI18N
        txtKode.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        txtKode.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtKodeActionPerformed(evt);
            }
        });
        txtKode.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txtKodeFocusGained(evt);
            }
        });
        txtKode.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                txtKodePropertyChange(evt);
            }
        });
        txtKode.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtKodeKeyReleased(evt);
            }
        });
        txtKode.addVetoableChangeListener(new java.beans.VetoableChangeListener() {
            public void vetoableChange(java.beans.PropertyChangeEvent evt)throws java.beans.PropertyVetoException {
                txtKodeVetoableChange(evt);
            }
        });
        jPanel2.add(txtKode, new org.netbeans.lib.awtextra.AbsoluteConstraints(110, 10, 90, 26));

        jLabel19.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel19.setText("Nama");
        jPanel2.add(jLabel19, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 40, 93, 15));

        jLabel20.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel20.setText("Kode");
        jPanel2.add(jLabel20, new org.netbeans.lib.awtextra.AbsoluteConstraints(12, 15, 93, -1));

        jLabel21.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel21.setText("Alamat");
        jPanel2.add(jLabel21, new org.netbeans.lib.awtextra.AbsoluteConstraints(9, 75, 93, -1));

        txtNama.setFont(new java.awt.Font("Dialog", 1, 12)); // NOI18N
        txtNama.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        txtNama.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txtNamaFocusGained(evt);
            }
        });
        jPanel2.add(txtNama, new org.netbeans.lib.awtextra.AbsoluteConstraints(110, 40, 486, 26));

        jLabel22.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel22.setText("Kota");
        jPanel2.add(jLabel22, new org.netbeans.lib.awtextra.AbsoluteConstraints(12, 105, 90, -1));

        jLabel23.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel23.setText("Telepon");
        jPanel2.add(jLabel23, new org.netbeans.lib.awtextra.AbsoluteConstraints(12, 135, 90, -1));

        jLabel24.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel24.setText("Contact Person");
        jPanel2.add(jLabel24, new org.netbeans.lib.awtextra.AbsoluteConstraints(9, 165, -1, -1));

        jLabel25.setText("HP");
        jPanel2.add(jLabel25, new org.netbeans.lib.awtextra.AbsoluteConstraints(418, 164, -1, -1));

        jLabel26.setText("Term of Payment");
        jPanel2.add(jLabel26, new org.netbeans.lib.awtextra.AbsoluteConstraints(609, 192, -1, 20));

        txtAlamat.setFont(new java.awt.Font("Dialog", 1, 12)); // NOI18N
        txtAlamat.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        txtAlamat.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txtAlamatFocusGained(evt);
            }
        });
        jPanel2.add(txtAlamat, new org.netbeans.lib.awtextra.AbsoluteConstraints(110, 70, 486, 26));

        txtKodeKota.setFont(new java.awt.Font("Dialog", 1, 12)); // NOI18N
        txtKodeKota.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        txtKodeKota.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txtKodeKotaFocusGained(evt);
            }
        });
        txtKodeKota.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtKodeKotaKeyReleased(evt);
            }
        });
        jPanel2.add(txtKodeKota, new org.netbeans.lib.awtextra.AbsoluteConstraints(110, 100, 60, 26));

        txtTelp.setFont(new java.awt.Font("Dialog", 1, 12)); // NOI18N
        txtTelp.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        txtTelp.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txtTelpFocusGained(evt);
            }
        });
        jPanel2.add(txtTelp, new org.netbeans.lib.awtextra.AbsoluteConstraints(110, 130, 184, 26));

        txtCP.setFont(new java.awt.Font("Dialog", 1, 12)); // NOI18N
        txtCP.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        txtCP.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txtCPFocusGained(evt);
            }
        });
        jPanel2.add(txtCP, new org.netbeans.lib.awtextra.AbsoluteConstraints(110, 160, 300, 26));

        txtHP.setFont(new java.awt.Font("Dialog", 1, 12)); // NOI18N
        txtHP.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        txtHP.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txtHPFocusGained(evt);
            }
        });
        jPanel2.add(txtHP, new org.netbeans.lib.awtextra.AbsoluteConstraints(436, 160, 160, 26));

        txtTop.setFont(new java.awt.Font("Dialog", 1, 12)); // NOI18N
        txtTop.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        txtTop.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txtTopFocusGained(evt);
            }
        });
        jPanel2.add(txtTop, new org.netbeans.lib.awtextra.AbsoluteConstraints(720, 189, 50, 26));

        lblKota.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        jPanel2.add(lblKota, new org.netbeans.lib.awtextra.AbsoluteConstraints(173, 100, 423, 26));

        txtKet.setFont(new java.awt.Font("Dialog", 1, 12)); // NOI18N
        txtKet.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        txtKet.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txtKetFocusGained(evt);
            }
        });
        jPanel2.add(txtKet, new org.netbeans.lib.awtextra.AbsoluteConstraints(110, 190, 486, 26));

        jLabel27.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel27.setText("Keterangan");
        jPanel2.add(jLabel27, new org.netbeans.lib.awtextra.AbsoluteConstraints(12, 195, 93, -1));

        jLabel28.setText("Hari");
        jPanel2.add(jLabel28, new org.netbeans.lib.awtextra.AbsoluteConstraints(774, 192, 35, 20));

        jLabel29.setText("Jenis Supplier");
        jPanel2.add(jLabel29, new org.netbeans.lib.awtextra.AbsoluteConstraints(465, 10, 105, 20));

        txtKodeJenis.setFont(new java.awt.Font("Dialog", 1, 12)); // NOI18N
        txtKodeJenis.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        txtKodeJenis.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txtKodeJenisFocusGained(evt);
            }
        });
        txtKodeJenis.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtKodeJenisKeyReleased(evt);
            }
        });
        jPanel2.add(txtKodeJenis, new org.netbeans.lib.awtextra.AbsoluteConstraints(570, 10, 60, 24));

        lblJenis.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        jPanel2.add(lblJenis, new org.netbeans.lib.awtextra.AbsoluteConstraints(630, 10, 150, 24));

        chkActive.setText("Active");
        chkActive.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        jPanel2.add(chkActive, new org.netbeans.lib.awtextra.AbsoluteConstraints(240, 9, 150, 25));

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, layout.createSequentialGroup()
                .add(5, 5, 5)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                    .add(jScrollPane2)
                    .add(jPanel2, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .add(15, 15, 15))
            .add(org.jdesktop.layout.GroupLayout.TRAILING, jPanel1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .add(jPanel1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jScrollPane2, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 367, Short.MAX_VALUE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanel2, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 224, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        setBounds(0, 0, 837, 692);
    }// </editor-fold>//GEN-END:initComponents

    private void txtKodeJenisKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtKodeJenisKeyReleased
        String sQry = "select kode_jenis_supp as Kode, jenis_supplier from jenis_supplier where upper(kode_jenis_supp||jenis_supplier) iLike '%" + txtSearch.getText() + "%' order by 1";
        fn.lookup(evt, new Object[]{lblJenis}, sQry, txtKodeJenis.getWidth()+lblJenis.getWidth()+19, 200);
    }//GEN-LAST:event_txtKodeJenisKeyReleased

    private void txtKodeJenisFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtKodeJenisFocusGained
// TODO add your handling code here:
    }//GEN-LAST:event_txtKodeJenisFocusGained

    private void txtKodeKotaKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtKodeKotaKeyReleased
        String sCari = txtKodeKota.getText();
        String sQry = "select kode_kota as Kode, nama_kota as Nama_Kota from kota where upper(kode_kota||nama_kota) Like '%" + sCari.toUpperCase() + "%' order by 1";
        fn.lookup(evt, new Object[]{lblKota}, sQry, txtKodeKota.getWidth()+lblKota.getWidth(), 200);
    }//GEN-LAST:event_txtKodeKotaKeyReleased

    private void formInternalFrameClosed(javax.swing.event.InternalFrameEvent evt) {//GEN-FIRST:event_formInternalFrameClosed
    }//GEN-LAST:event_formInternalFrameClosed

    private void formInternalFrameOpened(javax.swing.event.InternalFrameEvent evt) {//GEN-FIRST:event_formInternalFrameOpened
        
        setBEdit(false);
        setBNew(false);


        requestFocusInWindow(true);
        tblSupp.requestFocusInWindow();
        
        MyKeyListener kListener=new MyKeyListener();
        fn.addKeyListenerInContainer(jToolBar1, kListener, txtFocusListener);
        jScrollPane2.addKeyListener(kListener);
        tblSupp.addKeyListener(kListener);
        fn.addKeyListenerInContainer(jPanel1, kListener, txtFocusListener);
        fn.addKeyListenerInContainer(jPanel2, kListener, txtFocusListener);
        
        setUpBtn();
        onOpen();
    }//GEN-LAST:event_formInternalFrameOpened

    private void txtKetFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtKetFocusGained
// TODO add your handling code here:
    }//GEN-LAST:event_txtKetFocusGained

    private void txtTopFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtTopFocusGained
// TODO add your handling code here:
    }//GEN-LAST:event_txtTopFocusGained

    private void txtHPFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtHPFocusGained
// TODO add your handling code here:
    }//GEN-LAST:event_txtHPFocusGained

    private void txtCPFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtCPFocusGained
// TODO add your handling code here:
    }//GEN-LAST:event_txtCPFocusGained

    private void txtTelpFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtTelpFocusGained
// TODO add your handling code here:
    }//GEN-LAST:event_txtTelpFocusGained

    private void txtKodeKotaFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtKodeKotaFocusGained
// TODO add your handling code here:
    }//GEN-LAST:event_txtKodeKotaFocusGained

    private void txtAlamatFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtAlamatFocusGained
// TODO add your handling code here:
    }//GEN-LAST:event_txtAlamatFocusGained

    private void txtNamaFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtNamaFocusGained
    }//GEN-LAST:event_txtNamaFocusGained

    private void txtKodeVetoableChange(java.beans.PropertyChangeEvent evt)throws java.beans.PropertyVetoException {//GEN-FIRST:event_txtKodeVetoableChange
    }//GEN-LAST:event_txtKodeVetoableChange

    private void txtKodeKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtKodeKeyReleased
    }//GEN-LAST:event_txtKodeKeyReleased

    private void txtKodeFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtKodeFocusGained
    }//GEN-LAST:event_txtKodeFocusGained

    private void txtKodePropertyChange(java.beans.PropertyChangeEvent evt) {//GEN-FIRST:event_txtKodePropertyChange
    }//GEN-LAST:event_txtKodePropertyChange

    private void txtKodeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtKodeActionPerformed
    }//GEN-LAST:event_txtKodeActionPerformed

    private void tblSuppKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_tblSuppKeyReleased
// TODO add your handling code here:
    }//GEN-LAST:event_tblSuppKeyReleased

    private void tblSuppKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_tblSuppKeyPressed
    }//GEN-LAST:event_tblSuppKeyPressed

    private void tblSuppFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_tblSuppFocusGained
    }//GEN-LAST:event_tblSuppFocusGained

    private void btnCloseActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCloseActionPerformed
        udfCancel();
    }//GEN-LAST:event_btnCloseActionPerformed

    private void btnDeleteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnDeleteActionPerformed
        if (tblSupp.getRowCount() > 0 || getBEdit()) {
            udfUpdateData();
        }
    }//GEN-LAST:event_btnDeleteActionPerformed

    private void btnEditActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnEditActionPerformed
        udfEdit();
    }//GEN-LAST:event_btnEditActionPerformed

    private void btnNewActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnNewActionPerformed
        udfNew();
    }//GEN-LAST:event_btnNewActionPerformed

    private void btnSearchActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSearchActionPerformed
        //udfFilter(0,"all","like",txtSearch.getText());

        onOpen();
        txtSearch.requestFocus();
    }//GEN-LAST:event_btnSearchActionPerformed

    private void txtSearchKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtSearchKeyReleased
        udfFilter();
    }//GEN-LAST:event_txtSearchKeyReleased

    private void udfBlank() {
        chkActive.setSelected(true);
        txtNama.setText("");
        txtAlamat.setText("");
        txtKodeKota.setText("");
        lblKota.setText("");
        txtTelp.setText("");
        txtCP.setText("");
        txtHP.setText("");
        txtKet.setText("");
        txtKodeJenis.setText("");
        lblJenis.setText("");
        txtTop.setText("");
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnClose;
    private javax.swing.JButton btnDelete;
    private javax.swing.JButton btnEdit;
    private javax.swing.JButton btnNew;
    private javax.swing.JButton btnSearch;
    private javax.swing.JCheckBox chkActive;
    private javax.swing.JLabel jLabel1;
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
    private javax.swing.JLabel jLabel29;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JToolBar jToolBar1;
    private javax.swing.JLabel lblJenis;
    private javax.swing.JLabel lblKota;
    private javax.swing.JTable tblSupp;
    private javax.swing.JTextField txtAlamat;
    private javax.swing.JTextField txtCP;
    private javax.swing.JTextField txtHP;
    private javax.swing.JTextField txtKet;
    private javax.swing.JTextField txtKode;
    private javax.swing.JTextField txtKodeJenis;
    private javax.swing.JTextField txtKodeKota;
    private javax.swing.JTextField txtNama;
    private javax.swing.JTextField txtSearch;
    private javax.swing.JTextField txtTelp;
    private javax.swing.JTextField txtTop;
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
            if(e.getSource().getClass().getSimpleName().equalsIgnoreCase("JTextField")||
                    e.getSource().getClass().getSimpleName().equalsIgnoreCase("JFormattedTextField")){
                ((JTextField)e.getSource()).setBackground(Color.WHITE);

//                if(e.getSource().equals(txtNoPO) && !fn.isListVisible())
//                    udfLoadItemFromPO();
//                else if(e.getSource().equals(txtNoGR))
//                    udfLoadGR();
           }
        }


    } ;
    
}
