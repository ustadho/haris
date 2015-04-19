/*
 * FrmLookupBarang.java
 *
 * Created on December 14, 2006, 10:38 AM
 */

package pembelian;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.KeyboardFocusManager;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DecimalFormat;
import java.util.Collections;
import java.util.Comparator;
import java.util.Vector;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
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
import javax.swing.text.NumberFormatter;
/**
 *
 * @author  root
 */
public class FrmLookupBarang extends javax.swing.JFrame {
    static Connection conn;
    private int widthAsli=0;
    private int widthDetail=0;
    private int heightAsli=0;
    
    private String kode_barang="";
    private String nama_barang="";
    private float stock=0;
    private float in_order=0;
    private float min_stock=0;
    private String uom="";
    private String keterangan="";
    private float harga;
    private String kelas="";
    private JTextField textFocus;
    
    private boolean bAsc;
    private boolean isSelected;
    
    /** Creates new form FrmLookupBarang */
    public FrmLookupBarang() {
        initComponents();
        
        //conn=nCon;
        
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        Dimension frameSize = getSize();
        setLocation(new Point((screenSize.width - frameSize.width) / 2,
                              (screenSize.height - frameSize.width) / 2));
        
        widthDetail=this.getWidth();
        widthAsli=jPanel1.getWidth()+3*jPanel1.getX();
        heightAsli=this.getHeight();
        
        this.setSize(widthAsli, this.getHeight());
        
    }
    
    public void SetConn(Connection nCon){
        this.conn=nCon;
    }
    
    private void Setselected(boolean bSelect){
        this.isSelected=bSelect;
    }
    
    public boolean GetSelected(){
        return isSelected;
    }
    
    public String getKeterangan() {
        return keterangan;
    }

    public void setTextFocus(JTextField textFocus) {
        this.textFocus = textFocus;
    }

    public JTextField getTextFocus() {
        return textFocus;
    }
    
    public String getKode_barang() {
        return txtKodeBrgDet.getText();
    }

    private void SetKodeBarang(String kode){
        this.kode_barang=kode;
    }
    
    public String GetKodeBarang(){
        return kode_barang;
    }
    
    private void SetNamaBarang(String nama){
        this.nama_barang=nama;
    }

    public void setKelas(String kelas) {
        this.kelas = kelas;
    }

    public String getKelas() {
        return kelas;
    }

    public String GetNamaBarang(){
        //return nama_barang;
        return txtNamaBrgDet.getText().trim();
    }
    
    private void SetStock(float stock){
        this.stock=stock;
    }
    
    public float GetStock(){
        return stock;
    }
    
    private void SetInOrder(float inOrder){
        this.in_order=inOrder;
    }
    
    public float GetInOrder(){
        return in_order;
    }
    
    private void SetMinStock(float fMin){
        this.min_stock=fMin;
    }
    
    public float GetMinStock(){
        return min_stock;
    }
    
    private void SetUom(String sUom){
        this.uom=sUom;
    }
    
    public String GetUom(){
        return uom;
    }

    public float getHarga() {
        return harga;
    }

    public void setHarga(float harga) {
        this.harga = harga;
    }

    private void SetKeterangan(String sKet){
        this.keterangan=sKet;
    }
    
    public String GetKeterangan(){
        return keterangan;
    }
    
    public class MyRowRenderer extends DefaultTableCellRenderer implements TableCellRenderer{
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            
            if((column==0)||(column==1)||(table.getClass().getName().equals(tblStockDet) && column==0)){
                JTextField jt= new JTextField();
                setHorizontalAlignment(jt.LEFT);
            }
            
            
            if(column==2||column==3 ||(table.getName()=="tblStockDet" && column==1) ){
                DefaultFormatter fmt = new NumberFormatter(new DecimalFormat("#,###,###"));
                DefaultFormatterFactory fmtFactory = new DefaultFormatterFactory(fmt, fmt, fmt);
                JFormattedTextField ft =new JFormattedTextField();
                ft.setFormatterFactory(fmtFactory);
                ft.setValue(value);
                setValue(ft.getText());
                setHorizontalAlignment(ft.RIGHT);
            }else{
                setValue(value);
            }
            
            Color g1 = new Color(230,243,255);//[251,251,235]
            Color g2 = new Color(219,238,255);//[247,247,218]
            
            Color w1 = new Color(255,255,255);
            Color w2 = new Color(250,250,250);
            
            Color h1 = new Color(255,240,240);
            Color h2 = new Color(250,230,230);
            
            Color g;
            Color w;
            Color h;
            
            if(column%2==0){
                g = g1;
                w = w1;
                h = h1;
            }else{
                g = g2;
                w = w2;
                h = h2;
            }
            
            if (row%2==0){
                setBackground(w);
            }else{
                setBackground(g);
            }
            
            if(isSelected){
                setBackground(new Color(248,255,167));//[174,212,254]
            }
            return this;
        }
    }
    
    public class MyRowRenderer1 extends DefaultTableCellRenderer implements TableCellRenderer{
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            
            if(column==0){
                JTextField jt= new JTextField();
                setHorizontalAlignment(jt.LEFT);
            }
            
            
            if(column==1){
                DefaultFormatter fmt = new NumberFormatter(new DecimalFormat("#,###,###"));
                DefaultFormatterFactory fmtFactory = new DefaultFormatterFactory(fmt, fmt, fmt);
                JFormattedTextField ft =new JFormattedTextField();
                ft.setFormatterFactory(fmtFactory);
                ft.setValue(value);
                setValue(ft.getText());
                setHorizontalAlignment(ft.RIGHT);
            }else{
                setValue(value);
            }
            
            Color g1 = new Color(230,243,255);//[251,251,235]
            Color g2 = new Color(219,238,255);//[247,247,218]
            
            Color w1 = new Color(255,255,255);
            Color w2 = new Color(250,250,250);
            
            Color h1 = new Color(255,240,240);
            Color h2 = new Color(250,230,230);
            
            Color g;
            Color w;
            Color h;
            
            if(column%2==0){
                g = g1;
                w = w1;
                h = h1;
            }else{
                g = g2;
                w = w2;
                h = h2;
            }
            
            if (row%2==0){
                setBackground(w);
            }else{
                setBackground(g);
            }
            
            if(isSelected){
                setBackground(new Color(248,255,167));//[174,212,254]
            }
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
            Vector v1 = (Vector)a;
            Vector v2 = (Vector)b;
            Object o1 = v1.get(colIndex);
            Object o2 = v2.get(colIndex);
    
            // Treat empty strains like nulls
            if (o1 instanceof String && ((String)o1).length() == 0) {
                o1 = null;
            }
            if (o2 instanceof String && ((String)o2).length() == 0) {
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
                    return ((Comparable)o1).compareTo(o2);
                } else {
                    return ((Comparable)o2).compareTo(o1);
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
    
    private void pesanError(String Err){
        JOptionPane.showMessageDialog(this,Err,"Message",JOptionPane.ERROR_MESSAGE);
    }
    
    private void LoadStock(String sQry){
        
        System.out.println(sQry);
        
        try {
            st = conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE,ResultSet.CONCUR_UPDATABLE);
            rs = st.executeQuery(sQry);
            
            
            modelStock.setNumRows(0);
            TableLook();    
           
            while (rs.next()) {
                modelStock.addRow(new Object[]{rs.getString("kode"),
                                            rs.getString("nama"),
                                            rs.getFloat("jumlah"),
                                            rs.getFloat("in_order")
                                            });
                    }

            SelectionListener listener = new SelectionListener(tblStock);
            tblStock.getSelectionModel().addListSelectionListener(listener);
            tblStock.getColumnModel().getSelectionModel().addListSelectionListener(listener);
            tblStock.setRequestFocusEnabled(true);

            if (modelStock.getRowCount() > 0) {
                tblStock.setRowSelectionInterval(0, 0);
            }else
            {
                txtKodeBrgDet.setText("");
                txtNamaBrgDet.setText("");
                txtStockDet.setText("");
                txtInOrderDet.setText("");
            }
            
        } catch(SQLException se) {
            System.out.println(se.getMessage());
        }
    }
    
    private void LoadDetail(){
       
        String sQry="select site_id, stock, stock_in_order from phar_stock where " +
                    "kode_barang='"+tblStock.getValueAt(tblStock.getSelectedRow(),0).toString().trim()+"'";
        
        System.out.println(sQry);
        
        try {
            modelDetail.setNumRows(0);
            modelHarga.setNumRows(0);
            
            st = conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE,ResultSet.CONCUR_UPDATABLE);
            rs = st.executeQuery(sQry);
            
            while (rs.next()) {
                modelDetail.addRow(new Object[]{rs.getString("site_id"),
                                            rs.getFloat("stock") });
                    }

            if (modelDetail.getRowCount() > 0) {
                tblStockDet.setRowSelectionInterval(0, 0);
            }
            
            tblStockDet.getColumnModel().getColumn(0).setMaxWidth(50);    //Site ID
            tblStockDet.getColumnModel().getColumn(0).setPreferredWidth(50);
            tblStockDet.getColumnModel().getColumn(1).setMaxWidth(100);    //Stock
            tblStockDet.getColumnModel().getColumn(1).setPreferredWidth(100);
            
            tblStockDet.setRowHeight(20);
            
            if (modelHeader.getRowCount() > 0) {
                tblStockDet.changeSelection(0, 0,false,false);                
            } 
            
            String sQry1="Select kelas_tarif, coalesce(tarif,0) as tarif From phar_tarif_per_kelas where " +
                         "kode_barang='"+tblStock.getValueAt(tblStock.getSelectedRow(),0).toString().trim()+"' and " +
                         "kelas_tarif like '%"+kelas+"%'";
            
            System.out.println(sQry1);
            Statement st1=conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY);
            ResultSet rs1=st1.executeQuery(sQry1);
            
            
            while (rs1.next()) {
                modelHarga.addRow(new Object[]{ rs1.getString("kelas_tarif"),
                                                rs1.getFloat("tarif")});
                    }

            if (modelHarga.getRowCount() > 0) {
                tblHarga.setRowSelectionInterval(0, 0);
            }
            tblHarga.getColumnModel().getColumn(0).setMaxWidth(60);    //Site ID
            tblHarga.getColumnModel().getColumn(0).setPreferredWidth(60);
            tblHarga.getColumnModel().getColumn(1).setMaxWidth(90);    //Stock
            tblHarga.getColumnModel().getColumn(1).setPreferredWidth(90);
            
            tblHarga.setRowHeight(20);
            
            if (modelHarga.getRowCount() > 0) {
                tblHarga.changeSelection(0, 0,false,false);                
            }
        } catch(SQLException se) {
            System.out.println(se.getMessage());
        }
    }
    
    private void LoadText(){
        String sQry= "select i.kode_barang, coalesce(nama_barang,'''') as nama_barang, coalesce(sum(stock),0) as stock, " +
                    "coalesce(sum(stock_in_order),0) as in_order ,min, uom_kecil, keterangan " +
                    "from phar_item i " +
                    "left join phar_stock s on i.kode_barang=s.kode_barang " +
                    "where i.kode_barang='"+tblStock.getValueAt(tblStock.getSelectedRow(),0).toString()+"'" +
                    "group by i.kode_barang, nama_barang,min, uom_kecil,keterangan  order by 1";
        
        System.out.println("LoadText"+sQry);
        
        try {
            st = conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE,ResultSet.CONCUR_UPDATABLE);
            rs = st.executeQuery(sQry);
            
            if (rs.next()) {
                txtKodeBrgDet.setText(rs.getString("kode_barang"));
                txtNamaBrgDet.setText(rs.getString("nama_barang"));
                txtStockDet.setText(df1.format(Double.parseDouble(rs.getString("stock"))));
                txtInOrderDet.setText(df1.format(Double.parseDouble(rs.getString("in_order"))));
                txtMin.setText(df1.format(rs.getFloat("min")));
                txtUom.setText(rs.getString("uom_kecil"));
                txtKet.setText(rs.getString("keterangan"));
            }else{
                txtKodeBrgDet.setText("");
                txtNamaBrgDet.setText("");
                txtStockDet.setText("");
                txtInOrderDet.setText("");
                txtMin.setText("");
                txtUom.setText("");
                txtKet.setText("");
            }
                
        }catch(SQLException se){System.out.println(se.getMessage());}
    }
    
    private void Oke(){
        if (modelStock.getRowCount()>0){
            Setselected(true);
            SetKodeBarang(txtKodeBrgDet.getText());
            SetNamaBarang(txtNamaBrgDet.getText());
            SetStock(Float.parseFloat(txtStockDet.getText().equalsIgnoreCase("")? "0": txtStockDet.getText() ) );
            SetInOrder(Float.parseFloat(txtInOrderDet.getText().equalsIgnoreCase("")? "0": txtInOrderDet.getText() ));
            SetUom(txtUom.getText());
            SetMinStock(Float.parseFloat(txtMin.getText()));
            SetKeterangan(txtKet.getText());
//            String f=tblHarga.getValueAt(tblHarga.getSelectedRow(),0).toString();
//            
            if (modelHarga.getRowCount()>0)
                setHarga(Float.parseFloat(tblHarga.getValueAt(tblHarga.getSelectedRow(),1).toString() ));
            else
                setHarga(0);
            
            this.dispose();
    
        }
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
            
            if  (rowPos >=0 && rowPos < table.getRowCount()) {
                LoadText();
                btnShowDetail.setEnabled(true);
                LoadDetail();
                
            }
            else{    
                txtKodeBrgDet.setText("");
                txtNamaBrgDet.setText("");
                txtStockDet.setText("");
                txtInOrderDet.setText("");
                txtKet.setText("");
                txtUom.setText("");
                txtInOrderDet.setText("");
                
                btnShowDetail.setEnabled(false);
                modelDetail.setNumRows(0);
                modelHarga.setNumRows(0);
            }
             
            System.out.println(rowPos);            
        }
    }
    
    public boolean getAsc(){
        if (!bAsc)
            bAsc=true;
        else
            bAsc=false;
        return bAsc;               
    }
    
    public class myColHeaderList extends MouseAdapter{
            public void mouseClicked(MouseEvent evt) {
            JTable table = ((JTableHeader)evt.getSource()).getTable();
            TableColumnModel colModel = table.getColumnModel();
    
            // The index of the column whose header was clicked
            int vColIndex = colModel.getColumnIndexAtX(evt.getX());
            int mColIndex = table.convertColumnIndexToModel(vColIndex);
    
            // Return if not clicked on any column header
            if (vColIndex == -1) {
                return;
            }
            boolean bSt;
            bSt=getAsc();
            sortAllRowsBy(modelStock, vColIndex, bSt);
            
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
            tblStock.setRowSelectionInterval(0,0);
        }
    }
        
    private void TableLook(){
        tblStock.getColumnModel().getColumn(0).setMaxWidth(110);     //Kode barang
        tblStock.getColumnModel().getColumn(0).setPreferredWidth(110);
        tblStock.getColumnModel().getColumn(1).setMaxWidth(270);    //Nama Barang
        tblStock.getColumnModel().getColumn(1).setPreferredWidth(270);
        tblStock.getColumnModel().getColumn(2).setMaxWidth(65);    //Jumlah Stock
        tblStock.getColumnModel().getColumn(2).setPreferredWidth(65);
        tblStock.getColumnModel().getColumn(3).setMaxWidth(65);    //Jumlah Stock
        tblStock.getColumnModel().getColumn(3).setPreferredWidth(65);

        tblStock.setRowHeight(20);
        for (int i=0;i<tblStock.getColumnCount();i++){
            tblStock.getColumnModel().getColumn(i).setCellRenderer(new MyRowRenderer());
        }
        
        tblStockDet.getColumnModel().getColumn(0).setMaxWidth(60);     //Kode barang
        tblStockDet.getColumnModel().getColumn(0).setPreferredWidth(60);
        tblStockDet.getColumnModel().getColumn(1).setMaxWidth(200);    //Nama Barang
        tblStockDet.getColumnModel().getColumn(1).setPreferredWidth(200);
        
        if (modelHeader.getRowCount() > 0) {
            tblStock.changeSelection(0, 0,false,false);                
        }
        
        for (int i=0;i<tblStockDet.getColumnCount();i++){
            tblStockDet.getColumnModel().getColumn(i).setCellRenderer(new MyRowRenderer1());
        }
        for (int i=0;i<tblHarga.getColumnCount();i++){
            tblHarga.getColumnModel().getColumn(i).setCellRenderer(new MyRowRenderer1());
        }
     }
    
    public class MyKeyListener extends KeyAdapter {
        public void keyPressed(KeyEvent evt) {
            int keyKode = evt.getKeyCode();
            switch(keyKode){
                
                case KeyEvent.VK_ENTER : {
//                    Component ct = KeyboardFocusManager.getCurrentKeyboardFocusManager().getPermanentFocusOwner();
//                    if(!(ct.getClass().getSimpleName().equalsIgnoreCase("JTABLE")))
//                        {
//                    if (!lst.isVisible()){
//                        Component c = findNextFocus();
//                        c.requestFocus();
//                    }else{
//                        lst.requestFocus();
//                    }
                    Oke();
                    break;
                }
//                case KeyEvent.VK_DOWN: {
//                    Component ct = KeyboardFocusManager.getCurrentKeyboardFocusManager().getPermanentFocusOwner();
//                    if(!(ct.getClass().getSimpleName().equalsIgnoreCase("JTABLE")))
//                        {                        
//                            if (!lst.isVisible()){
//			    Component c = findNextFocus();
//			    c.requestFocus();
//                            }else
//                                lst.requestFocus();
//                            
//                            break;
//                    }
//                }
//                
//                case KeyEvent.VK_UP: {
//                    Component ct = KeyboardFocusManager.getCurrentKeyboardFocusManager().getPermanentFocusOwner();
//                    if(!(ct.getClass().getSimpleName().equalsIgnoreCase("JTABLE")))
//                    {    
//                        Component c = findPrevFocus();
//                        c.requestFocus();
//                    }
//                    break;
//                }
                
                //lempar aja ke udfCancel
                case KeyEvent.VK_ESCAPE: {
                        dispose();
                        break;
                    }   //Jika cancel
                    
                //default ;
                
             }
        }
    }
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel4 = new javax.swing.JLabel();
        jPanel1 = new javax.swing.JPanel();
        txtNama = new javax.swing.JTextField();
        jScrollPane10 = new javax.swing.JScrollPane();
        tblStock = new javax.swing.JTable();
        jButton7 = new javax.swing.JButton();
        jButton8 = new javax.swing.JButton();
        jPanel3 = new javax.swing.JPanel();
        jLabel27 = new javax.swing.JLabel();
        jLabel28 = new javax.swing.JLabel();
        jLabel29 = new javax.swing.JLabel();
        jLabel30 = new javax.swing.JLabel();
        txtKodeBrgDet = new javax.swing.JTextField();
        txtNamaBrgDet = new javax.swing.JTextField();
        btnShowDetail = new javax.swing.JToggleButton();
        txtStockDet = new javax.swing.JFormattedTextField();
        txtInOrderDet = new javax.swing.JFormattedTextField();
        jLabel31 = new javax.swing.JLabel();
        txtMin = new javax.swing.JFormattedTextField();
        txtUom = new javax.swing.JTextField();
        jLabel32 = new javax.swing.JLabel();
        jLabel33 = new javax.swing.JLabel();
        jScrollPane11 = new javax.swing.JScrollPane();
        txtKet = new javax.swing.JTextArea();
        jPanel2 = new javax.swing.JPanel();
        jScrollPane12 = new javax.swing.JScrollPane();
        tblHarga = new javax.swing.JTable();
        lblStockLocation3 = new javax.swing.JLabel();
        lblStockLocation4 = new javax.swing.JLabel();
        jScrollPane13 = new javax.swing.JScrollPane();
        tblStockDet = new javax.swing.JTable();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Lookup Barang");
        setBackground(new java.awt.Color(0, 153, 204));
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowActivated(java.awt.event.WindowEvent evt) {
                formWindowActivated(evt);
            }
            public void windowClosed(java.awt.event.WindowEvent evt) {
                formWindowClosed(evt);
            }
            public void windowOpened(java.awt.event.WindowEvent evt) {
                formWindowOpened(evt);
            }
        });
        getContentPane().setLayout(null);

        jLabel4.setBackground(new java.awt.Color(159, 120, 2));
        jLabel4.setFont(new java.awt.Font("Dialog", 1, 14)); // NOI18N
        jLabel4.setForeground(new java.awt.Color(255, 255, 51));
        jLabel4.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel4.setText("Lookup Barang");
        jLabel4.setOpaque(true);
        getContentPane().add(jLabel4);
        jLabel4.setBounds(0, 0, 723, 25);

        jPanel1.setBackground(new java.awt.Color(255, 204, 102));
        jPanel1.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        txtNama.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txtNamaFocusGained(evt);
            }
        });
        txtNama.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txtNamaKeyPressed(evt);
            }
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtNamaKeyReleased(evt);
            }
            public void keyTyped(java.awt.event.KeyEvent evt) {
                txtNamaKeyTyped(evt);
            }
        });
        jPanel1.add(txtNama, new org.netbeans.lib.awtextra.AbsoluteConstraints(6, 5, 369, 24));

        jScrollPane10.setBackground(new java.awt.Color(255, 255, 255));

        tblStock.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null}
            },
            new String [] {
                "Kode Item", "Nama Item Barang", "Jml.Stok", "In Order"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class, java.lang.Float.class, java.lang.Float.class
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
        tblStock.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_OFF);
        tblStock.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tblStockMouseClicked(evt);
            }
        });
        tblStock.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                tblStockKeyPressed(evt);
            }
        });
        jScrollPane10.setViewportView(tblStock);

        jPanel1.add(jScrollPane10, new org.netbeans.lib.awtextra.AbsoluteConstraints(5, 35, 525, 140));

        jButton7.setText("OK");
        jButton7.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton7ActionPerformed(evt);
            }
        });
        jPanel1.add(jButton7, new org.netbeans.lib.awtextra.AbsoluteConstraints(378, 5, 70, -1));

        jButton8.setText("Cancel");
        jButton8.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton8ActionPerformed(evt);
            }
        });
        jPanel1.add(jButton8, new org.netbeans.lib.awtextra.AbsoluteConstraints(455, 5, -1, -1));

        getContentPane().add(jPanel1);
        jPanel1.setBounds(5, 25, 535, 180);

        jPanel3.setBackground(new java.awt.Color(159, 120, 2));
        jPanel3.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel27.setText("Kode Barang");
        jPanel3.add(jLabel27, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 10, -1, -1));

        jLabel28.setText("Nama Barang");
        jPanel3.add(jLabel28, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 40, -1, -1));

        jLabel29.setText("Stock");
        jPanel3.add(jLabel29, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 60, -1, 20));

        jLabel30.setText("Stock In Order");
        jPanel3.add(jLabel30, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 110, -1, 20));

        txtKodeBrgDet.setEditable(false);
        txtKodeBrgDet.setFont(new java.awt.Font("Dialog", 1, 12)); // NOI18N
        txtKodeBrgDet.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        jPanel3.add(txtKodeBrgDet, new org.netbeans.lib.awtextra.AbsoluteConstraints(100, 10, 110, 24));

        txtNamaBrgDet.setEditable(false);
        txtNamaBrgDet.setFont(new java.awt.Font("Dialog", 1, 12)); // NOI18N
        txtNamaBrgDet.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        jPanel3.add(txtNamaBrgDet, new org.netbeans.lib.awtextra.AbsoluteConstraints(100, 35, 430, 24));

        btnShowDetail.setText("Show Detail");
        btnShowDetail.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnShowDetailActionPerformed(evt);
            }
        });
        jPanel3.add(btnShowDetail, new org.netbeans.lib.awtextra.AbsoluteConstraints(400, 5, 130, -1));

        txtStockDet.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        txtStockDet.setEditable(false);
        txtStockDet.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtStockDet.setFont(new java.awt.Font("Dialog", 1, 12)); // NOI18N
        jPanel3.add(txtStockDet, new org.netbeans.lib.awtextra.AbsoluteConstraints(100, 60, 110, 24));

        txtInOrderDet.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        txtInOrderDet.setEditable(false);
        txtInOrderDet.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtInOrderDet.setFont(new java.awt.Font("Dialog", 1, 12)); // NOI18N
        jPanel3.add(txtInOrderDet, new org.netbeans.lib.awtextra.AbsoluteConstraints(100, 110, 110, 24));

        jLabel31.setText("Min. Stock");
        jPanel3.add(jLabel31, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 85, -1, 20));

        txtMin.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        txtMin.setEditable(false);
        txtMin.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtMin.setFont(new java.awt.Font("Dialog", 1, 12)); // NOI18N
        jPanel3.add(txtMin, new org.netbeans.lib.awtextra.AbsoluteConstraints(100, 85, 110, 24));

        txtUom.setEditable(false);
        txtUom.setFont(new java.awt.Font("Dialog", 1, 12)); // NOI18N
        txtUom.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        jPanel3.add(txtUom, new org.netbeans.lib.awtextra.AbsoluteConstraints(310, 60, 110, 24));

        jLabel32.setText("Uom Kecil");
        jPanel3.add(jLabel32, new org.netbeans.lib.awtextra.AbsoluteConstraints(245, 65, -1, -1));

        jLabel33.setText("Keterangan");
        jPanel3.add(jLabel33, new org.netbeans.lib.awtextra.AbsoluteConstraints(235, 90, -1, 20));

        txtKet.setColumns(20);
        txtKet.setEditable(false);
        txtKet.setRows(5);
        txtKet.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        jScrollPane11.setViewportView(txtKet);

        jPanel3.add(jScrollPane11, new org.netbeans.lib.awtextra.AbsoluteConstraints(310, 85, 220, 60));

        getContentPane().add(jPanel3);
        jPanel3.setBounds(5, 210, 535, 150);

        jPanel2.setBackground(new java.awt.Color(255, 204, 102));
        jPanel2.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jScrollPane12.setBackground(new java.awt.Color(255, 255, 255));

        tblHarga.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null}
            },
            new String [] {
                "Kelas", "Harga"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.Float.class
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
        tblHarga.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_OFF);
        tblHarga.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tblHargaMouseClicked(evt);
            }
        });
        jScrollPane12.setViewportView(tblHarga);

        jPanel2.add(jScrollPane12, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 207, 170, 130));

        lblStockLocation3.setBackground(new java.awt.Color(159, 120, 2));
        lblStockLocation3.setFont(new java.awt.Font("Dialog", 1, 14)); // NOI18N
        lblStockLocation3.setForeground(new java.awt.Color(255, 255, 0));
        lblStockLocation3.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblStockLocation3.setText("SITE");
        lblStockLocation3.setOpaque(true);
        jPanel2.add(lblStockLocation3, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 15, 170, 20));

        lblStockLocation4.setBackground(new java.awt.Color(159, 120, 2));
        lblStockLocation4.setFont(new java.awt.Font("Dialog", 1, 14)); // NOI18N
        lblStockLocation4.setForeground(new java.awt.Color(255, 255, 0));
        lblStockLocation4.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblStockLocation4.setText("HARGA");
        lblStockLocation4.setOpaque(true);
        jPanel2.add(lblStockLocation4, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 185, 170, 20));

        jScrollPane13.setBackground(new java.awt.Color(255, 255, 255));

        tblStockDet.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null}
            },
            new String [] {
                "Site", "Jumlah Stok"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.Float.class
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
        tblStockDet.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_OFF);
        tblStockDet.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tblStockDetMouseClicked(evt);
            }
        });
        jScrollPane13.setViewportView(tblStockDet);

        jPanel2.add(jScrollPane13, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 35, 170, 140));

        getContentPane().add(jPanel2);
        jPanel2.setBounds(545, 25, 170, 335);

        java.awt.Dimension screenSize = java.awt.Toolkit.getDefaultToolkit().getScreenSize();
        setBounds((screenSize.width-729)/2, (screenSize.height-391)/2, 729, 391);
    }// </editor-fold>//GEN-END:initComponents

    private void formWindowActivated(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowActivated
//        txtkode.setText(textFocus.getText());
    }//GEN-LAST:event_formWindowActivated

    private void formWindowClosed(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosed
        textFocus.requestFocus();
    }//GEN-LAST:event_formWindowClosed

    private void txtNamaKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtNamaKeyPressed
        switch(evt.getKeyCode()){
            case KeyEvent.VK_DOWN:
                tblStock.requestFocus();
                break;
                
//            case KeyEvent.VK_LEFT:
//                txtkode.requestFocus();
//                break;
        }
    }//GEN-LAST:event_txtNamaKeyPressed

    private void tblStockDetMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tblStockDetMouseClicked
// TODO add your handling code here:
    }//GEN-LAST:event_tblStockDetMouseClicked

    private void tblStockKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_tblStockKeyPressed
        switch(evt.getKeyCode()){
            case KeyEvent.VK_ENTER:
                int rowSelected;
                rowSelected=tblStock.getSelectedRow();
                if (rowSelected==0){
                    if (tblStock.getRowCount()>0){
                    rowSelected=tblStock.getRowCount()-1;}
                    else {rowSelected=0;}
                }
                else {if (tblStock.getRowCount()>0){rowSelected--;}else {rowSelected=0;}
                }
                tblStock.setRowSelectionInterval(rowSelected,rowSelected);
                Oke();
                break;
                
            case KeyEvent.VK_UP: 
                if(tblStock.getSelectedRow()==0 && modelStock.getRowCount()>0){
                    txtNama.requestFocus();
                    break;
                }
        }
    }//GEN-LAST:event_tblStockKeyPressed

    
    private void formWindowOpened(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowOpened
        DefaultFormatter fmt = new NumberFormatter(new DecimalFormat("#,###,###"));
        DefaultFormatterFactory fmtFactory = new DefaultFormatterFactory(fmt, fmt, fmt);
        txtStockDet.setFormatterFactory(fmtFactory);
        txtInOrderDet.setFormatterFactory(fmtFactory);
        
        modelStock=(DefaultTableModel)tblStock.getModel();
        tblStock.setModel(modelStock);
            
        modelDetail=(DefaultTableModel)tblStockDet.getModel();
        tblStockDet.setModel(modelDetail);
        
        modelHarga=(DefaultTableModel)tblHarga.getModel();
        tblHarga.setModel(modelHarga);
        
        //udfLoadStock("select * from fn_phar_site_list(1,'s.kode_barang','like','') " +
          //          "as(kode varchar, nama varchar,jumlah numeric(12,0),  in_order numeric(12,0))");
        

        String sQry="select * from fn_phar_site_list(1,'nama_barang','like','') "+
                    "as(kode varchar, nama varchar, jumlah numeric(12,0), in_order numeric(12,0))";
        LoadStock(sQry);
        
        JTableHeader header = tblStock.getTableHeader();
        Font fH;
        fH=new Font("Tahoma",Font.BOLD,12);
        header.setFont(fH);

        header.setBackground((new Color(234,243,244)));
        header.addMouseListener(new myColHeaderList());
            
        JTableHeader header1 = tblStockDet.getTableHeader();
        Font fH1;
        fH1=new Font("Tahoma",Font.BOLD,12);
        header.setFont(fH1);

        header1.setBackground((new Color(234,243,244)));
        header1.addMouseListener(new myColHeaderList());  
        
        for(int i=0;i<jPanel1.getComponentCount();i++){
            Component c = jPanel1.getComponent(i);
            if(c.getClass().getSimpleName().equalsIgnoreCase("JTEXTFIELD")    || c.getClass().getSimpleName().equalsIgnoreCase("JFormattedTextField")
            || c.getClass().getSimpleName().equalsIgnoreCase("JTextArea") || c.getClass().getSimpleName().equalsIgnoreCase("JComboBox")
            || c.getClass().getSimpleName().equalsIgnoreCase("JButton") || c.getClass().getSimpleName().equalsIgnoreCase("JCheckBox")) {
                //System.out.println(c.getClass().getSimpleName());
                c.addKeyListener(new MyKeyListener());
            }
        }
        
         for(int i=0;i<jPanel2.getComponentCount();i++){
            Component c = jPanel2.getComponent(i);
            if(c.getClass().getSimpleName().equalsIgnoreCase("JTEXTFIELD")    || c.getClass().getSimpleName().equalsIgnoreCase("JFormattedTextField")
            || c.getClass().getSimpleName().equalsIgnoreCase("JTextArea") || c.getClass().getSimpleName().equalsIgnoreCase("JComboBox")
            || c.getClass().getSimpleName().equalsIgnoreCase("JButton") || c.getClass().getSimpleName().equalsIgnoreCase("JCheckBox")) {
                //System.out.println(c.getClass().getSimpleName());
                c.addKeyListener(new MyKeyListener());
            }
        }
        
        for(int i=0;i<jPanel3.getComponentCount();i++){
            Component c = jPanel3.getComponent(i);
            if(c.getClass().getSimpleName().equalsIgnoreCase("JTEXTFIELD")    || c.getClass().getSimpleName().equalsIgnoreCase("JFormattedTextField")
            || c.getClass().getSimpleName().equalsIgnoreCase("JTextArea") || c.getClass().getSimpleName().equalsIgnoreCase("JComboBox")
            || c.getClass().getSimpleName().equalsIgnoreCase("JButton") || c.getClass().getSimpleName().equalsIgnoreCase("JCheckBox")) {
                //System.out.println(c.getClass().getSimpleName());
                c.addKeyListener(new MyKeyListener());
            }
        }
    }//GEN-LAST:event_formWindowOpened

    private void tblHargaMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tblHargaMouseClicked
//        if (evt.getClickCount()==2 && tblItem.getValueAt(tblItem.getSelectedRow(),0).toString().length()>0){
////            EditItem();
//        }
    }//GEN-LAST:event_tblHargaMouseClicked

    private void btnShowDetailActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnShowDetailActionPerformed
        if(tblStock.getSelectedRow()>=0 && tblStock.getRowCount()>0){
            
            if (this.getWidth()!=widthAsli) {   //Lokasi disembunyikan
                this.setSize(widthAsli, heightAsli);
                btnShowDetail.setText("Show Detail");
                jPanel2.setVisible(false);
                jLabel4.setSize(widthAsli,jLabel4.getHeight());
                
            } else{
                this.setSize(widthDetail, heightAsli);
                btnShowDetail.setText("Hide Detail");
                jPanel2.setVisible(true);
                jLabel4.setSize(widthDetail,jLabel4.getHeight());
                
            }
        }
    }//GEN-LAST:event_btnShowDetailActionPerformed

    private void jButton8ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton8ActionPerformed
        Setselected(false);
        this.dispose();
    }//GEN-LAST:event_jButton8ActionPerformed

    private void jButton7ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton7ActionPerformed
        Oke();
    }//GEN-LAST:event_jButton7ActionPerformed

    private void tblStockMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tblStockMouseClicked
        if(modelStock.getRowCount()>0&&evt.getClickCount()==2){
            Oke();
        }
    }//GEN-LAST:event_tblStockMouseClicked

    private void txtNamaKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtNamaKeyTyped
        
    }//GEN-LAST:event_txtNamaKeyTyped

    private void txtNamaKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtNamaKeyReleased
        String sQry="select * from fn_phar_site_list1(1,'i.kode_barang||nama_barang','like','"+txtNama.getText().trim()+"') "+
                "as(kode varchar, nama varchar, jumlah numeric(12,0), in_order numeric(12,0))";
        LoadStock(sQry);
    }//GEN-LAST:event_txtNamaKeyReleased

    private void txtNamaFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtNamaFocusGained
//        txtkode.setText("");
    }//GEN-LAST:event_txtNamaFocusGained
    
    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new FrmLookupBarang().setVisible(true);
            }
        });
    }
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JToggleButton btnShowDetail;
    private javax.swing.JButton jButton7;
    private javax.swing.JButton jButton8;
    private javax.swing.JLabel jLabel27;
    private javax.swing.JLabel jLabel28;
    private javax.swing.JLabel jLabel29;
    private javax.swing.JLabel jLabel30;
    private javax.swing.JLabel jLabel31;
    private javax.swing.JLabel jLabel32;
    private javax.swing.JLabel jLabel33;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JScrollPane jScrollPane10;
    private javax.swing.JScrollPane jScrollPane11;
    private javax.swing.JScrollPane jScrollPane12;
    private javax.swing.JScrollPane jScrollPane13;
    private javax.swing.JLabel lblStockLocation3;
    private javax.swing.JLabel lblStockLocation4;
    private javax.swing.JTable tblHarga;
    private javax.swing.JTable tblStock;
    private javax.swing.JTable tblStockDet;
    private javax.swing.JFormattedTextField txtInOrderDet;
    private javax.swing.JTextArea txtKet;
    private javax.swing.JTextField txtKodeBrgDet;
    private javax.swing.JFormattedTextField txtMin;
    private javax.swing.JTextField txtNama;
    private javax.swing.JTextField txtNamaBrgDet;
    private javax.swing.JFormattedTextField txtStockDet;
    private javax.swing.JTextField txtUom;
    // End of variables declaration//GEN-END:variables
    DefaultTableModel modelStock=new DefaultTableModel();
    DefaultTableModel modelHeader=new DefaultTableModel();
    DefaultTableModel modelDetail=new DefaultTableModel();
    DefaultTableModel modelHarga=new DefaultTableModel();
    
    ResultSet rs;
    Statement st;
    DecimalFormat df1 = new DecimalFormat("#,##0");
}
