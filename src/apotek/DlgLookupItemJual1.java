/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * DlgLookupItem2.java
 *
 * Created on 28 Des 10, 6:36:14
 */

package apotek;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.swing.JComponent;
import javax.swing.JFormattedTextField;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;
import main.GeneralFunction;

/**
 *
 * @author cak-ust
 */
public class DlgLookupItemJual1 extends javax.swing.JDialog {
    private Connection conn;
    GeneralFunction fn=new GeneralFunction();
    private String sCustType="R";
    private int columnIndex;
    private JTable srcTable;
    private KeyEvent keyEvent;
    private Object objForm;
    private String sKodeBarang="";

    /** Creates new form DlgLookupItem2 */
    public DlgLookupItemJual1(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
        tblItem.getTableHeader().setFont(tblItem.getFont());
        tblStok.getTableHeader().setFont(tblItem.getFont());
        tblStok.setFont(tblItem.getFont());

        txtCari.addFocusListener(txtFocusListener);
        tblItem.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0),"selectNextColumnCell");
        tblItem.getColumn("Kode").setPreferredWidth(120);
        tblItem.getColumn("Nama Barang").setPreferredWidth(250);
        tblItem.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            public void valueChanged(ListSelectionEvent e) {
                if(tblItem.getSelectedRow()>=0){
                    udfLoadStok();
                    udfLoadHarga();
                }
            }
        });
    }

    public void setCustType(String s){
        this.sCustType=s;
    }

    public void setObjForm(Object obj){
        objForm=obj;
    }

    private void setSelected(){
        int iRow=tblItem.getSelectedRow();
        if(iRow>=0)
            sKodeBarang=tblItem.getValueAt(iRow, 0).toString();
        if(objForm !=null)
            this.dispose();
    }

    private void udfLoadStok(){
        String sItem=tblItem.getValueAt(tblItem.getSelectedRow(), 0).toString();
        try {
            ((DefaultTableModel)tblStok.getModel()).setNumRows(0);

            ResultSet rs = conn.createStatement().executeQuery(
                    "select s.kode_gudang, coalesce(g.deskripsi,'') as gudang, sum(coalesce(jumlah,0)) as saldo " +
                    "from stock s " +
                    "left join gudang g on g.kode_gudang=s.kode_gudang " +
                    "where item_code='"+sItem+"' " +
                    "group by s.kode_gudang, coalesce(g.deskripsi,'') order by s.kode_gudang");
            while(rs.next())
                ((DefaultTableModel)tblStok.getModel()).addRow(new Object[]{
                    rs.getString("kode_gudang"),
                    rs.getString("gudang"),
                    rs.getDouble("saldo")
                });
            rs.close();
            if(tblStok.getRowCount()>0)
                tblStok.setRowSelectionInterval(0, 0);

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage());
        }
    }

    private void udfLoadHarga(){
        int iRow=tblItem.getSelectedRow();
        try{
            String sQry = "select coalesce(b.diskon_box,0) as diskon_box, " +
                        "coalesce(b.base_price,0)*coalesce(b.margin,0)/100 as harga_jual," +
                        "coalesce(b.kode_jenis,'') as kode_jenis, coalesce(j.jenis_barang,'') as jenis_barang, " +
                        "coalesce(b.group_id,'') as group_id, coalesce(g.group_name,'') as group_name, " +
                        "coalesce(b.bentuk_id, '') as bentuk_id, coalesce(bt.bentuk_name,'') as bentuk_name, " +
                        "coalesce(b.manufaktur_id, '') as manufaktur_id, coalesce(mn.nama_manufaktur,'') as nama_manufaktur," +
                        "coalesce(b.keterangan,'') as keterangan, " +
                        "coalesce(b.discontinued, false) as discontinued, coalesce(b.indikasi,'') as indikasi " +
                        "from barang b " +
                        "left join jenis_barang j on j.kode_jenis=b.kode_jenis " +
                        "left join item_group g on g.group_id=b.group_id " +
                        "left join item_bentuk bt on bt.bentuk_id=b.bentuk_id " +
                        "left join item_manufaktur mn on mn.id=b.manufaktur_id " +
                        "where b.item_code='"+tblItem.getValueAt(iRow, tblItem.getColumnModel().getColumnIndex("Kode")).toString()+"' ";
            ResultSet rs=conn.createStatement().executeQuery(sQry);

            if(rs.next()){
                txtHargaJual.setText(fn.intFmt.format(rs.getDouble("harga_jual")));
                txtDiskonBox.setText(fn.intFmt.format(rs.getDouble("diskon_box")));
                txtJenis.setText(rs.getString("jenis_barang"));
                txtGroup.setText(rs.getString("group_name"));
                txtBentuk.setText(rs.getString("bentuk_name"));
                txtManufaktur.setText(rs.getString("nama_manufaktur"));
                txtIndikasi.setText(rs.getString("indikasi"));
                txtKeterangan.setText(rs.getString("keterangan"));

            }else{
                udfClear();
            }
            rs.close();
        }catch(SQLException se){
            JOptionPane.showMessageDialog(this, se.getMessage());
        }
    }

    private void udfClear() {
        txtDiskonBox.setText("");
        txtGroup.setText("");
        txtJenis.setText("");
        txtGroup.setText("");
        txtBentuk.setText("");
        txtManufaktur.setText("");
        txtIndikasi.setText("");
        txtKeterangan.setText("");
    }

    public void setConn(Connection con){
        this.conn=con;
    }

    private void udfFilter(){
        try{
            setCursor(new Cursor(Cursor.WAIT_CURSOR));
            ((DefaultTableModel)tblItem.getModel()).setNumRows(0);
            String sQry="select i.item_code, coalesce(i.item_name,'') as item_name, coalesce(j.jenis_barang,'') as jenis_barang,"
                    + "coalesce(i.satuan_kecil,'') as satuan_kecil, base_price*margin/100 as harga_jual " +
                    "from barang  i " +
                    "left join jenis_barang j on j.kode_jenis=i.kode_jenis " +
                    "where not discontinued " +
                    "and item_code||coalesce(i.item_name,'')||coalesce(j.jenis_barang,'') ilike '%"+txtCari.getText()+"%' " +
                    "order by 2";
            //System.out.println(sQry);
            ResultSet rs=conn.createStatement().executeQuery(sQry);
            
            while(rs.next()){
                ((DefaultTableModel)tblItem.getModel()).addRow(new Object[]{
                    rs.getString("item_code"),
                    rs.getString("item_name"),
                    rs.getString("jenis_barang"),
                    rs.getString("satuan_kecil"),
                    rs.getDouble("harga_jual")
                });
            }
            rs.close();
            if(tblItem.getRowCount()>0)
                tblItem.setRowSelectionInterval(0, 0);

            setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
        }catch(SQLException se){
            setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
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

        jLabel1 = new javax.swing.JLabel();
        txtCari = new javax.swing.JTextField();
        jScrollPane1 = new javax.swing.JScrollPane();
        tblItem = new org.jdesktop.swingx.JXTable();
        jPanel1 = new javax.swing.JPanel();
        jPanel4 = new javax.swing.JPanel();
        jLabel3 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        txtDiskonBox = new javax.swing.JTextField();
        txtGroup = new javax.swing.JTextField();
        jLabel7 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        txtManufaktur = new javax.swing.JTextField();
        jLabel9 = new javax.swing.JLabel();
        txtIndikasi = new javax.swing.JTextField();
        jLabel10 = new javax.swing.JLabel();
        txtHargaJual = new javax.swing.JTextField();
        txtJenis = new javax.swing.JTextField();
        jLabel11 = new javax.swing.JLabel();
        txtKeterangan = new javax.swing.JTextField();
        txtBentuk = new javax.swing.JTextField();
        jLabel12 = new javax.swing.JLabel();
        jPanel2 = new javax.swing.JPanel();
        jSeparator2 = new javax.swing.JSeparator();
        jLabel6 = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        tblStok = new javax.swing.JTable();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Lookup Item by Harga Jual");
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosed(java.awt.event.WindowEvent evt) {
                formWindowClosed(evt);
            }
            public void windowOpened(java.awt.event.WindowEvent evt) {
                formWindowOpened(evt);
            }
        });
        getContentPane().setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel1.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel1.setText("Pencarian");
        jLabel1.setName("jLabel1"); // NOI18N
        getContentPane().add(jLabel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 10, 100, 20));

        txtCari.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        txtCari.setName("txtCari"); // NOI18N
        txtCari.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txtCariKeyPressed(evt);
            }
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtCariKeyReleased(evt);
            }
        });
        getContentPane().add(txtCari, new org.netbeans.lib.awtextra.AbsoluteConstraints(120, 10, 430, -1));

        jScrollPane1.setName("jScrollPane1"); // NOI18N

        tblItem.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Kode", "Nama Barang", "Kategori", "Satuan", "Harga"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        tblItem.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_OFF);
        tblItem.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        tblItem.setName("tblItem"); // NOI18N
        tblItem.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tblItemMouseClicked(evt);
            }
        });
        tblItem.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                tblItemKeyPressed(evt);
            }
        });
        jScrollPane1.setViewportView(tblItem);

        getContentPane().add(jScrollPane1, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 40, 540, 440));

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder("Informasi Item"));
        jPanel1.setName("jPanel1"); // NOI18N
        jPanel1.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jPanel4.setName("jPanel4"); // NOI18N
        jPanel4.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel3.setBackground(new java.awt.Color(153, 153, 255));
        jLabel3.setText("Group");
        jLabel3.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        jLabel3.setName("jLabel3"); // NOI18N
        jLabel3.setOpaque(true);
        jPanel4.add(jLabel3, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 75, 80, 20));

        jLabel5.setBackground(new java.awt.Color(153, 153, 255));
        jLabel5.setText("Diskon Box");
        jLabel5.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        jLabel5.setName("jLabel5"); // NOI18N
        jLabel5.setOpaque(true);
        jPanel4.add(jLabel5, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 25, 80, 20));

        txtDiskonBox.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        txtDiskonBox.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtDiskonBox.setText("0");
        txtDiskonBox.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        txtDiskonBox.setDisabledTextColor(new java.awt.Color(0, 0, 0));
        txtDiskonBox.setEnabled(false);
        txtDiskonBox.setName("txtDiskonBox"); // NOI18N
        txtDiskonBox.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                txtDiskonBoxKeyTyped(evt);
            }
        });
        jPanel4.add(txtDiskonBox, new org.netbeans.lib.awtextra.AbsoluteConstraints(80, 25, 120, 20));

        txtGroup.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        txtGroup.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        txtGroup.setDisabledTextColor(new java.awt.Color(0, 0, 0));
        txtGroup.setEnabled(false);
        txtGroup.setName("txtGroup"); // NOI18N
        txtGroup.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                txtGroupKeyTyped(evt);
            }
        });
        jPanel4.add(txtGroup, new org.netbeans.lib.awtextra.AbsoluteConstraints(80, 75, 170, 20));

        jLabel7.setBackground(new java.awt.Color(153, 153, 255));
        jLabel7.setText("Jenis");
        jLabel7.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        jLabel7.setName("jLabel7"); // NOI18N
        jLabel7.setOpaque(true);
        jPanel4.add(jLabel7, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 50, 80, 20));

        jLabel8.setBackground(new java.awt.Color(153, 153, 255));
        jLabel8.setText("Manufaktur");
        jLabel8.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        jLabel8.setName("jLabel8"); // NOI18N
        jLabel8.setOpaque(true);
        jPanel4.add(jLabel8, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 100, 80, 20));

        txtManufaktur.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        txtManufaktur.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        txtManufaktur.setDisabledTextColor(new java.awt.Color(0, 0, 0));
        txtManufaktur.setEnabled(false);
        txtManufaktur.setName("txtManufaktur"); // NOI18N
        txtManufaktur.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                txtManufakturKeyTyped(evt);
            }
        });
        jPanel4.add(txtManufaktur, new org.netbeans.lib.awtextra.AbsoluteConstraints(80, 100, 170, 20));

        jLabel9.setBackground(new java.awt.Color(153, 153, 255));
        jLabel9.setText("Keterangan");
        jLabel9.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        jLabel9.setName("jLabel9"); // NOI18N
        jLabel9.setOpaque(true);
        jPanel4.add(jLabel9, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 175, 80, 20));

        txtIndikasi.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        txtIndikasi.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        txtIndikasi.setDisabledTextColor(new java.awt.Color(0, 0, 0));
        txtIndikasi.setEnabled(false);
        txtIndikasi.setName("txtIndikasi"); // NOI18N
        txtIndikasi.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                txtIndikasiKeyTyped(evt);
            }
        });
        jPanel4.add(txtIndikasi, new org.netbeans.lib.awtextra.AbsoluteConstraints(80, 150, 170, 20));

        jLabel10.setBackground(new java.awt.Color(153, 153, 255));
        jLabel10.setText("Harga");
        jLabel10.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        jLabel10.setName("jLabel10"); // NOI18N
        jLabel10.setOpaque(true);
        jPanel4.add(jLabel10, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 80, 20));

        txtHargaJual.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        txtHargaJual.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtHargaJual.setText("0");
        txtHargaJual.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        txtHargaJual.setDisabledTextColor(new java.awt.Color(0, 0, 0));
        txtHargaJual.setEnabled(false);
        txtHargaJual.setName("txtHargaJual"); // NOI18N
        txtHargaJual.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                txtHargaJualKeyTyped(evt);
            }
        });
        jPanel4.add(txtHargaJual, new org.netbeans.lib.awtextra.AbsoluteConstraints(80, 0, 120, 20));

        txtJenis.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        txtJenis.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        txtJenis.setDisabledTextColor(new java.awt.Color(0, 0, 0));
        txtJenis.setEnabled(false);
        txtJenis.setName("txtJenis"); // NOI18N
        txtJenis.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                txtJenisKeyTyped(evt);
            }
        });
        jPanel4.add(txtJenis, new org.netbeans.lib.awtextra.AbsoluteConstraints(80, 50, 170, 20));

        jLabel11.setBackground(new java.awt.Color(153, 153, 255));
        jLabel11.setText("Indikasi");
        jLabel11.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        jLabel11.setName("jLabel11"); // NOI18N
        jLabel11.setOpaque(true);
        jPanel4.add(jLabel11, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 150, 80, 20));

        txtKeterangan.setEditable(false);
        txtKeterangan.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        txtKeterangan.setName("txtKeterangan"); // NOI18N
        jPanel4.add(txtKeterangan, new org.netbeans.lib.awtextra.AbsoluteConstraints(80, 175, 170, 20));

        txtBentuk.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        txtBentuk.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        txtBentuk.setDisabledTextColor(new java.awt.Color(0, 0, 0));
        txtBentuk.setEnabled(false);
        txtBentuk.setName("txtBentuk"); // NOI18N
        txtBentuk.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                txtBentukKeyTyped(evt);
            }
        });
        jPanel4.add(txtBentuk, new org.netbeans.lib.awtextra.AbsoluteConstraints(80, 125, 170, 20));

        jLabel12.setBackground(new java.awt.Color(153, 153, 255));
        jLabel12.setText("Bentuk");
        jLabel12.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        jLabel12.setName("jLabel12"); // NOI18N
        jLabel12.setOpaque(true);
        jPanel4.add(jLabel12, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 125, 80, 20));

        jPanel1.add(jPanel4, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 30, 250, 200));

        getContentPane().add(jPanel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(560, 220, 270, 260));

        jPanel2.setName("jPanel2"); // NOI18N
        jPanel2.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jSeparator2.setName("jSeparator2"); // NOI18N
        jPanel2.add(jSeparator2, new org.netbeans.lib.awtextra.AbsoluteConstraints(100, 20, 160, -1));

        jLabel6.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel6.setText("Informasi Stok");
        jLabel6.setName("jLabel6"); // NOI18N
        jPanel2.add(jLabel6, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 10, 90, 20));

        jScrollPane2.setName("jScrollPane2"); // NOI18N

        tblStok.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Kode", "Gudang", "Stok"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Object.class, java.lang.Object.class, java.lang.Double.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        tblStok.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_OFF);
        tblStok.setName("tblStok"); // NOI18N
        tblStok.getTableHeader().setReorderingAllowed(false);
        tblStok.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                tblStokKeyPressed(evt);
            }
        });
        jScrollPane2.setViewportView(tblStok);

        jPanel2.add(jScrollPane2, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 30, 250, 130));

        getContentPane().add(jPanel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(560, 40, 270, 170));

        setSize(new java.awt.Dimension(855, 535));
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void txtDiskonBoxKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtDiskonBoxKeyTyped
        
}//GEN-LAST:event_txtDiskonBoxKeyTyped

    private void txtGroupKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtGroupKeyTyped
        
}//GEN-LAST:event_txtGroupKeyTyped

    private void txtCariKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtCariKeyReleased
        udfFilter();
    }//GEN-LAST:event_txtCariKeyReleased

    private void formWindowOpened(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowOpened
        udfInitForm();
    }//GEN-LAST:event_formWindowOpened

    public void udfInitForm(){
        sKodeBarang="";
        txtCari.setText("");
        udfFilter();
    }

    private void txtCariKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtCariKeyPressed
        switch(evt.getKeyCode()){
            case KeyEvent.VK_DOWN:{
                tblItem.requestFocus();
                break;
            }
            case KeyEvent.VK_ENTER:{
                setSelected();
                break;
            }
            case KeyEvent.VK_ESCAPE:{
                this.sKodeBarang="";
                this.dispose();
            }
        }
    }//GEN-LAST:event_txtCariKeyPressed

    private void tblItemKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_tblItemKeyPressed
        switch(evt.getKeyCode()){
            case KeyEvent.VK_UP:{
                if(tblItem.getSelectedRow()==0)
                    txtCari.requestFocus();
                break;
            }
            case KeyEvent.VK_ENTER:{
                setSelected();
                break;
            }
            case KeyEvent.VK_ESCAPE:{
                this.sKodeBarang="";
                this.dispose();
            }  
        }
    }//GEN-LAST:event_tblItemKeyPressed

    private void tblItemMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tblItemMouseClicked
        if(evt.getClickCount()==2)
            setSelected();
            
    }//GEN-LAST:event_tblItemMouseClicked

    private void formWindowClosed(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosed
        // TODO add your handling code here:
    }//GEN-LAST:event_formWindowClosed

    private void tblStokKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_tblStokKeyPressed
        switch(evt.getKeyCode()){
            case KeyEvent.VK_ESCAPE:{
                this.sKodeBarang="";
                this.dispose();
            }  
        }
    }//GEN-LAST:event_tblStokKeyPressed

    private void txtManufakturKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtManufakturKeyTyped
        // TODO add your handling code here:
    }//GEN-LAST:event_txtManufakturKeyTyped

    private void txtIndikasiKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtIndikasiKeyTyped
        // TODO add your handling code here:
    }//GEN-LAST:event_txtIndikasiKeyTyped

    private void txtHargaJualKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtHargaJualKeyTyped
        // TODO add your handling code here:
    }//GEN-LAST:event_txtHargaJualKeyTyped

    private void txtJenisKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtJenisKeyTyped
        // TODO add your handling code here:
    }//GEN-LAST:event_txtJenisKeyTyped

    private void txtBentukKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtBentukKeyTyped
        // TODO add your handling code here:
    }//GEN-LAST:event_txtBentukKeyTyped

    /**
    * @param args the command line arguments
    */
    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                DlgLookupItemJual1 dialog = new DlgLookupItemJual1(new javax.swing.JFrame(), true);
                dialog.addWindowListener(new java.awt.event.WindowAdapter() {
                    public void windowClosing(java.awt.event.WindowEvent e) {
                        System.exit(0);
                    }
                });
                dialog.setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JSeparator jSeparator2;
    private org.jdesktop.swingx.JXTable tblItem;
    private javax.swing.JTable tblStok;
    private javax.swing.JTextField txtBentuk;
    private javax.swing.JTextField txtCari;
    private javax.swing.JTextField txtDiskonBox;
    private javax.swing.JTextField txtGroup;
    private javax.swing.JTextField txtHargaJual;
    private javax.swing.JTextField txtIndikasi;
    private javax.swing.JTextField txtJenis;
    private javax.swing.JTextField txtKeterangan;
    private javax.swing.JTextField txtManufaktur;
    // End of variables declaration//GEN-END:variables

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


           }
        }


    } ;

    public void setKeyEvent(KeyEvent evt) {
        this.keyEvent=evt;
    }

    public void setSrcTable(JTable table, int columnIndex) {
        this.srcTable=table;
        this.columnIndex=columnIndex;
    }

    public String getKodeBarang() {
        return sKodeBarang;
    }

    public void clearText() {
        txtCari.setText("");
    }

}
