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
public class DlgLookupItemBeli extends javax.swing.JDialog {
    private Connection conn;
    GeneralFunction fn=new GeneralFunction();
    private String sCustType="R";
    private int columnIndex;
    private JTable srcTable;
    private KeyEvent keyEvent;
    private Object objForm;
    private String sKodeBarang="";

    /** Creates new form DlgLookupItem2 */
    public DlgLookupItemBeli(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
        tblItem.setRowHeight(22);
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
                    udfLoadSupplier();
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
                    "group by s.kode_gudang, gudang order by s.kode_gudang");
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

    private void udfLoadSupplier(){
        String s="select sb.kode_supplier, coalesce(s.nama_supplier,'') as nama, coalesce(sb.priority,0) as rk," +
                "coalesce(sb.price,0)-(coalesce(sb.price,0)/100*coalesce(sb.disc,0))+" +
                "(coalesce(sb.price,0)-(coalesce(sb.price,0)/100*coalesce(sb.disc,0)))/100*coalesce(sb.vat,0) as harga_beli, " +
                "coalesce(sb.price,0) as price_list1, coalesce(sb.disc,0) as disc, coalesce(sb.vat,0) as ppn, " +
                "coalesce(sb.uom_alt,'') as uom_alt, coalesce(convertion,1) as konv, " +
                "coalesce(i.satuan_kecil,'') as unit_kecil " +
                "from supplier_barang sb " +
                "inner join barang i on i.item_code=sb.kode_barang " +
                "left join phar_supplier s on s.kode_supplier=sb.kode_supplier " +
                "where sb.kode_barang='"+tblItem.getValueAt(tblItem.getSelectedRow(), 0).toString()+"' order by priority ";
        try{
            ResultSet rs=null;
            rs=conn.createStatement().executeQuery(s);
            ((DefaultTableModel)tblSupplier.getModel()).setNumRows(0);
            while(rs.next())
                ((DefaultTableModel)tblSupplier.getModel()).addRow(new Object[]{
                    rs.getString("kode_supplier"),
                    rs.getString("nama"),
                    rs.getInt("rk"),
                    rs.getDouble("harga_beli")/ rs.getDouble("konv"),
                    rs.getString("unit_kecil"),
                    rs.getDouble("price_list1"),
                    rs.getDouble("disc"),
                    rs.getDouble("ppn"),
                    rs.getString("uom_alt"),
                    rs.getDouble("konv")
                });
            tblSupplier.setModel((DefaultTableModel)fn.autoResizeColWidth(tblSupplier, (DefaultTableModel)tblSupplier.getModel()).getModel());
            if(tblSupplier.getRowCount()>0)
                tblSupplier.setRowSelectionInterval(0, 0);

            rs.close();
        }catch(SQLException se){
            JOptionPane.showMessageDialog(this, se.getMessage());
        }
    }

    public void setConn(Connection con){
        this.conn=con;
    }

    private void udfFilter(){
        try{
            setCursor(new Cursor(Cursor.WAIT_CURSOR));
            ((DefaultTableModel)tblItem.getModel()).setNumRows(0);
            ResultSet rs=conn.createStatement().executeQuery(
                    "select item_code, coalesce(item_name,'') as item_name, coalesce(j.jenis_barang,'') as jenis, "
                    + "coalesce(i.nama_paten,'') as nama_paten, coalesce(i.indikasi,'') as indikasi " +
                    "from barang i " +
                    "left join jenis_barang j on j.kode_jenis=i.kode_jenis " +
                    "where item_code||coalesce(item_name,'')||coalesce(i.nama_paten,'')||coalesce(j.jenis_barang,'') ilike '%"+txtCari.getText()+"%' " +
                    "order by item_name, jenis");
            while(rs.next()){
                ((DefaultTableModel)tblItem.getModel()).addRow(new Object[]{
                    rs.getString("item_code"),
                    rs.getString("item_name"),
                    rs.getString("nama_paten"),
                    rs.getString("indikasi"),
                    rs.getString("jenis")
                });
            }
            rs.close();
            if(tblItem.getRowCount()>0){
                fn.setAutoResizeColWidth(tblItem);
                tblItem.setRowSelectionInterval(0, 0);
            }
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
        jPanel1 = new javax.swing.JPanel();
        jSeparator3 = new javax.swing.JSeparator();
        jLabel7 = new javax.swing.JLabel();
        jScrollPane3 = new javax.swing.JScrollPane();
        tblSupplier = new javax.swing.JTable();
        jPanel2 = new javax.swing.JPanel();
        jSeparator2 = new javax.swing.JSeparator();
        jLabel6 = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        tblStok = new javax.swing.JTable();
        jScrollPane4 = new javax.swing.JScrollPane();
        tblItem = new javax.swing.JTable();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Lookup Item by Harga Pembelian");
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowOpened(java.awt.event.WindowEvent evt) {
                formWindowOpened(evt);
            }
        });

        jLabel1.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel1.setText("Pencarian");
        jLabel1.setName("jLabel1"); // NOI18N

        txtCari.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        txtCari.setName("txtCari"); // NOI18N
        txtCari.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtCariActionPerformed(evt);
            }
        });
        txtCari.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txtCariKeyPressed(evt);
            }
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtCariKeyReleased(evt);
            }
        });

        jPanel1.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        jPanel1.setName("jPanel1"); // NOI18N

        jSeparator3.setName("jSeparator3"); // NOI18N

        jLabel7.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel7.setText("Harga Supplier");
        jLabel7.setName("jLabel7"); // NOI18N

        jScrollPane3.setName("jScrollPane3"); // NOI18N

        tblSupplier.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        tblSupplier.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "SupplierID", "Nama Supplier", "Rk", "Harga Beli", "Satuan Kcl", "Price List1", "Disc1%", "PPN1%", "Satuan2", "Konversi"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Object.class, java.lang.Object.class, java.lang.Integer.class, java.lang.Double.class, java.lang.Object.class, java.lang.Double.class, java.lang.Double.class, java.lang.Double.class, java.lang.Object.class, java.lang.Double.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false, false, false, false, false
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
        tblSupplier.getTableHeader().setReorderingAllowed(false);
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
        jScrollPane3.setViewportView(tblSupplier);

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(10, 10, 10)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel7, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jSeparator3, javax.swing.GroupLayout.PREFERRED_SIZE, 450, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jScrollPane3)
                        .addGap(8, 8, 8))))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(8, 8, 8)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel7, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(10, 10, 10)
                        .addComponent(jSeparator3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addComponent(jScrollPane3, javax.swing.GroupLayout.DEFAULT_SIZE, 90, Short.MAX_VALUE)
                .addGap(11, 11, 11))
        );

        jPanel2.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        jPanel2.setName("jPanel2"); // NOI18N
        jPanel2.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jSeparator2.setName("jSeparator2"); // NOI18N
        jPanel2.add(jSeparator2, new org.netbeans.lib.awtextra.AbsoluteConstraints(100, 20, 160, -1));

        jLabel6.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel6.setText("Informasi Stok");
        jLabel6.setName("jLabel6"); // NOI18N
        jPanel2.add(jLabel6, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 10, 130, 20));

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
        jScrollPane2.setViewportView(tblStok);

        jPanel2.add(jScrollPane2, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 30, 250, 90));

        jScrollPane4.setName("jScrollPane4"); // NOI18N

        tblItem.setAutoCreateRowSorter(true);
        tblItem.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Kode", "Nama Paten", "Nama Barang", "Indikasi", "Kategori"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.String.class
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
        tblItem.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_OFF);
        tblItem.setName("tblItem"); // NOI18N
        tblItem.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                tblItemKeyPressed(evt);
            }
        });
        jScrollPane4.setViewportView(tblItem);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(10, 10, 10)
                .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(10, 10, 10)
                .addComponent(txtCari, javax.swing.GroupLayout.PREFERRED_SIZE, 726, javax.swing.GroupLayout.PREFERRED_SIZE))
            .addGroup(layout.createSequentialGroup()
                .addGap(15, 15, 15)
                .addComponent(jScrollPane4, javax.swing.GroupLayout.PREFERRED_SIZE, 830, javax.swing.GroupLayout.PREFERRED_SIZE))
            .addGroup(layout.createSequentialGroup()
                .addGap(10, 10, 10)
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, 578, Short.MAX_VALUE)
                .addGap(0, 0, 0)
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, 270, javax.swing.GroupLayout.PREFERRED_SIZE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(10, 10, 10)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtCari, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(3, 3, 3)
                .addComponent(jScrollPane4, javax.swing.GroupLayout.PREFERRED_SIZE, 305, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(10, 10, 10)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE)))
        );

        setSize(new java.awt.Dimension(868, 535));
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void txtCariKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtCariKeyReleased
        udfFilter();
    }//GEN-LAST:event_txtCariKeyReleased

    private void formWindowOpened(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowOpened
        txtCari.setText("");
        udfFilter();
    }//GEN-LAST:event_formWindowOpened

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
        }
    }//GEN-LAST:event_txtCariKeyPressed

    private void tblSupplierKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_tblSupplierKeyPressed
        if(evt.getKeyCode()==KeyEvent.VK_UP && tblSupplier.getSelectedRow()==0){
            tblItem.requestFocus();
        }
}//GEN-LAST:event_tblSupplierKeyPressed

    private void tblSupplierKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_tblSupplierKeyReleased

}//GEN-LAST:event_tblSupplierKeyReleased

    private void tblSupplierKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_tblSupplierKeyTyped

}//GEN-LAST:event_tblSupplierKeyTyped

    private void txtCariActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtCariActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtCariActionPerformed

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

    /**
    * @param args the command line arguments
    */
    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                DlgLookupItemBeli dialog = new DlgLookupItemBeli(new javax.swing.JFrame(), true);
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
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JSeparator jSeparator2;
    private javax.swing.JSeparator jSeparator3;
    private javax.swing.JTable tblItem;
    private javax.swing.JTable tblStok;
    private javax.swing.JTable tblSupplier;
    private javax.swing.JTextField txtCari;
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
