/*
 * FrmLookupPO.java
 *
 * Created on September 30, 2007, 7:35 AM
 */

package pembelian;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.event.KeyEvent;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;

/**
 *
 * @author  Lab. GIS
 */
public class FrmLookupPO extends javax.swing.JDialog {
    
    private String sKodeSupplier="";
    private Connection conn;
    private DefaultTableModel hModel, dModel, srcModel;

    private frmGood_Receipt1 fAsal;
    int i=0;
    
    /** Creates new form FrmLookupPO */
    public FrmLookupPO(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
    }
    
    private Boolean anyPO(String poIns){
        int ii=0;boolean find=false;
        while(!find && ii<srcModel.getRowCount()){
            if (poIns.trim().toLowerCase().equalsIgnoreCase(srcModel.getValueAt(ii,11).toString().trim().toLowerCase())){
                find=true;
                break; //--> by hengki
            }
            ii++;
        }
        return find;
    }
    
    public void setConn(Connection con){
        conn=con;
    }
    
    public void setKodeSupp(String sKode){
        sKodeSupplier=sKode;
    }
    
    private void udfLoadSupplier(){
        try {
            Statement st=conn.createStatement(); 
            String sSupp="select coalesce(nama_supplier,'') as nama," +
                        "       coalesce(alamat,'')  as alamat," +
                        "       coalesce(telp,'') as telp " +
                        "from phar_supplier where kode_supplier='"+sKodeSupplier+"' ";
            
            ResultSet rs=st.executeQuery(sSupp);
            if(rs.next()){
                lblKode.setText(sKodeSupplier);
                lblNama.setText(rs.getString(1));
                lblAlamat.setText(rs.getString(2));
                lblTelp.setText(rs.getString(3));
            }else{
                lblKode.setText("");
                lblNama.setText("");
                lblAlamat.setText("");
                lblTelp.setText("");
            }
            
            rs.close();
            st.close();
        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
        } 
    }
    
    private void udfLoadTablePO(){
        try {
            String sPO="select po_code, to_char(tanggal,'dd-MM-yyyy') as tanggal, coalesce(jatuh_tempo,1) as top, " +
                    "to_char(coalesce(due_date, tanggal), 'dd-MM-yyyy') as jatuh_tempo, coalesce(keterangan,'') as keterangan, " +
                    "coalesce(discount, 0) as discount," +
                    "coalesce(tax,0) as ppn " +
                    "from purchase_order where closed=false and supplier_code='"+sKodeSupplier+"'";
            
            Statement st=conn.createStatement();
            ResultSet rs=st.executeQuery(sPO);
            i=1;
            while (rs.next()){
                hModel.addRow(new Object[]{i,
                    rs.getString("po_code"),
                    rs.getString("tanggal"),
                    rs.getInt("top"),
                    rs.getString("jatuh_tempo"),
                    rs.getString("keterangan"),
                    rs.getFloat("discount"),
                    rs.getFloat("ppn")
                });
                i++;
            }
            
            
        } catch (SQLException ex) {
            System.err.println(ex.getMessage());
        }
    }
    
    public class MyRowRenderer extends DefaultTableCellRenderer implements TableCellRenderer{
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            
            Color g1 = new Color(239,234,240);//-->>(251,236,177);// Kuning         [251,251,235]
            Color g2 = new Color(239,234,240);//-->>(241,226,167);// Kuning         [247,247,218]
            
             
            Color w1 = new Color(255,255,255);// Putih
            Color w2 = new Color(250,250,250);// Putih Juga
            
            Color h1 = new Color(255,240,240);// Merah muda
            Color h2 = new Color(250,230,230);// Merah Muda
            
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
            if(column==0 || (column==3 && table.equals(tblPO)) ){
                JTextField tx=new JTextField();
                setHorizontalAlignment(tx.CENTER);
              //  value=formatter.format(value);
            }
            
            if((column==6 ||column==7 && table.equals(tblPO)) || //||
               (column==6||column==8 && table.equals(tblPODet)) ){
                JTextField tx=new JTextField();
                setHorizontalAlignment(tx.RIGHT);
                value=formatter1.format(value);
            }
            
            
            setForeground(new Color(0,0,0));
            if (row%2==0){
                setBackground(w);
            }else{
                setBackground(g);
            }
            if(isSelected){
                //setBackground(new Color(51,102,255));
                setBackground(new Color(248,255,167));
                //setForeground(new Color(255,255,255));
            }
            
            setFont(new Font("Tahoma", 0, 12));
            setValue(value);
            return this;
        }
    }
    
    private void TableLook(){
            tblPO.getColumnModel().getColumn(0).setPreferredWidth(50);  //No
            tblPO.getColumnModel().getColumn(1).setPreferredWidth(120);  //Kode
            tblPO.getColumnModel().getColumn(2).setPreferredWidth(100);  //Tgl. PO
            tblPO.getColumnModel().getColumn(3).setPreferredWidth(60);  //TOP
            tblPO.getColumnModel().getColumn(4).setPreferredWidth(110);  //Tgl. Jatuh Tempo
            tblPO.getColumnModel().getColumn(5).setPreferredWidth(300);  //Keterangan
            tblPO.getColumnModel().getColumn(6).setPreferredWidth(120);  //Diskon
            tblPO.getColumnModel().getColumn(7).setPreferredWidth(120);  //PPn
            
            tblPODet.getColumnModel().getColumn(0).setPreferredWidth(50);  //No
            tblPODet.getColumnModel().getColumn(1).setPreferredWidth(120);  //Kode Barang
            tblPODet.getColumnModel().getColumn(2).setPreferredWidth(220);  //Deskripsi
            tblPODet.getColumnModel().getColumn(3).setPreferredWidth(60);  //Satuan
            tblPODet.getColumnModel().getColumn(4).setPreferredWidth(110);  //Qty
            tblPODet.getColumnModel().getColumn(5).setPreferredWidth(110);  //Harga Sat
            tblPODet.getColumnModel().getColumn(6).setPreferredWidth(120);  //Diskon
            tblPODet.getColumnModel().getColumn(7).setPreferredWidth(120);  //PPn
            
            tblPO.setRowHeight(20);
            tblPODet.setRowHeight(20);
            
            for (int i=0;i<tblPO.getColumnCount();i++){
                tblPO.getColumnModel().getColumn(i).setCellRenderer(new MyRowRenderer());
            }
            for (int i=0;i<tblPODet.getColumnCount();i++){
                tblPODet.getColumnModel().getColumn(i).setCellRenderer(new MyRowRenderer());
            }
            
            SelectionListener listener = new SelectionListener(tblPO);
            tblPO.getSelectionModel().addListSelectionListener(listener);
            tblPO.getColumnModel().getSelectionModel().addListSelectionListener(listener);
            
            if (hModel.getRowCount() > 0) {
                tblPO.changeSelection(0, 0,false,false);                
            }  
            tblPO.setRequestFocusEnabled(true);
            
     }
    
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
            String sNoPo;
            rowPos = table.getSelectedRow();           
            
            if  (rowPos >=0 && rowPos < table.getRowCount()) {
                sNoPo=tblPO.getValueAt(rowPos, 1).toString();
                
                Statement st;
                try {
                    String sPO= "select * from fn_show_po('"+ sNoPo+"') " +
                                "as(shiping varchar,remark varchar,top integer,kurs numeric,tanggal text,duedate text," +
                                "currency varchar,discount double precision,ppn double precision,kode_supplier varchar, " +
                                "no_po varchar,kode_barang varchar,nama_barang varchar,uom varchar,qty numeric, " +
                                "price numeric,disc numeric,vat numeric,total double precision)";
                    
                    System.out.println(sPO);
                    st = conn.createStatement();
                    ResultSet rs=st.executeQuery(sPO);
                    dModel.setNumRows(0);
                    
                    i=1;
                    while(rs.next()){
                        btnOK.setEnabled(true);
                        dModel.addRow(new Object[]{i,rs.getString("kode_barang"),rs.getString("nama_barang"),rs.getString("uom"),
                            rs.getDouble("qty"),rs.getDouble("price"),rs.getDouble("disc"),rs.getDouble("vat"), 
                            rs.getDouble("total")});
                        i++;
                    }
                    
                    rs.close();
                    st.close();
                    
                } catch (SQLException ex) {
                    System.err.println(ex.getMessage());
                }
                
                
            }
            if (table.getRowCount()==0){
                dModel.setNumRows(0);
                btnOK.setEnabled(false);
            }
            
            
        }
    }
     
     private void tambahItem(){
        if(!anyPO(tblPO.getValueAt(tblPO.getSelectedRow(), 1).toString())){
            int row=tblPO.getSelectedRow();
            if(row>=0 && row<tblPO.getRowCount()){
                fAsal.addPOItem(tblPO.getValueAt(row, 1).toString());
                this.dispose();
            }
        }else{
            JOptionPane.showMessageDialog(this, "PO tersebut sudah pernah dimasukkan. Silakan pilih PO lain");
            tblPODet.requestFocus();
        }
     }
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jScrollPane1 = new javax.swing.JScrollPane();
        tblPO = new javax.swing.JTable();
        jScrollPane2 = new javax.swing.JScrollPane();
        tblPODet = new javax.swing.JTable();
        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        lblKode = new javax.swing.JLabel();
        lblNama = new javax.swing.JLabel();
        lblAlamat = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        lblTelp = new javax.swing.JLabel();
        btnOK = new javax.swing.JButton();
        jButton2 = new javax.swing.JButton();
        lblAlamat1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Lookup Pesanan Pembelian");
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowOpened(java.awt.event.WindowEvent evt) {
                formWindowOpened(evt);
            }
        });
        getContentPane().setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        tblPO.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, null, null, null}
            },
            new String [] {
                "No.", "Kode PO", "Tgl. PO", "T.O.P", "Tgl. Jth Tempo", "Keterangan", "Diskon", "PPn"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.Integer.class, java.lang.String.class, java.lang.String.class, java.lang.Double.class, java.lang.Double.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        tblPO.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tblPOMouseClicked(evt);
            }
        });
        tblPO.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                tblPOFocusGained(evt);
            }
        });
        tblPO.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                tblPOKeyPressed(evt);
            }
        });
        jScrollPane1.setViewportView(tblPO);

        getContentPane().add(jScrollPane1, new org.netbeans.lib.awtextra.AbsoluteConstraints(8, 82, 840, 150));

        tblPODet.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, null, null, null, null}
            },
            new String [] {
                "No.", "Kode Barang", "Deskripsi", "Satuan", "QTY", "Harga Sat", "Diskon", "PPn", "Total Line"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Double.class, java.lang.Double.class, java.lang.Double.class, java.lang.Double.class, java.lang.Double.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false, false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        tblPODet.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                tblPODetFocusGained(evt);
            }
        });
        tblPODet.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                tblPODetKeyPressed(evt);
            }
        });
        jScrollPane2.setViewportView(tblPODet);

        getContentPane().add(jScrollPane2, new org.netbeans.lib.awtextra.AbsoluteConstraints(9, 259, 840, 170));

        jPanel1.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        jPanel1.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel1.setText("Supplier");
        jPanel1.add(jLabel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(16, 10, 100, 20));

        lblKode.setBackground(new java.awt.Color(255, 255, 255));
        lblKode.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        lblKode.setOpaque(true);
        jPanel1.add(lblKode, new org.netbeans.lib.awtextra.AbsoluteConstraints(116, 11, 80, 20));

        lblNama.setBackground(new java.awt.Color(255, 255, 255));
        lblNama.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        lblNama.setOpaque(true);
        jPanel1.add(lblNama, new org.netbeans.lib.awtextra.AbsoluteConstraints(196, 11, 390, 20));

        lblAlamat.setBackground(new java.awt.Color(255, 255, 255));
        lblAlamat.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        lblAlamat.setOpaque(true);
        jPanel1.add(lblAlamat, new org.netbeans.lib.awtextra.AbsoluteConstraints(116, 36, 470, 20));

        jLabel5.setText("Alamat");
        jPanel1.add(jLabel5, new org.netbeans.lib.awtextra.AbsoluteConstraints(14, 36, 100, 20));

        jLabel6.setText("Telp.");
        jPanel1.add(jLabel6, new org.netbeans.lib.awtextra.AbsoluteConstraints(596, 37, 50, 20));

        lblTelp.setBackground(new java.awt.Color(255, 255, 255));
        lblTelp.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        lblTelp.setOpaque(true);
        jPanel1.add(lblTelp, new org.netbeans.lib.awtextra.AbsoluteConstraints(625, 36, 150, 20));

        getContentPane().add(jPanel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(8, 5, 840, 70));

        btnOK.setText("Pilih");
        btnOK.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnOKActionPerformed(evt);
            }
        });
        btnOK.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                btnOKKeyPressed(evt);
            }
        });
        getContentPane().add(btnOK, new org.netbeans.lib.awtextra.AbsoluteConstraints(703, 431, 70, -1));

        jButton2.setText("Batal");
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });
        jButton2.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jButton2KeyPressed(evt);
            }
        });
        getContentPane().add(jButton2, new org.netbeans.lib.awtextra.AbsoluteConstraints(777, 431, 70, -1));

        lblAlamat1.setBackground(new java.awt.Color(204, 255, 255));
        lblAlamat1.setFont(new java.awt.Font("Tahoma", 1, 12));
        lblAlamat1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblAlamat1.setText("DETAIL PESANAN");
        lblAlamat1.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        lblAlamat1.setOpaque(true);
        getContentPane().add(lblAlamat1, new org.netbeans.lib.awtextra.AbsoluteConstraints(8, 239, 840, 20));

        jLabel2.setBackground(new java.awt.Color(204, 255, 204));
        jLabel2.setOpaque(true);
        getContentPane().add(jLabel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(-15, -15, 750, 480));

        java.awt.Dimension screenSize = java.awt.Toolkit.getDefaultToolkit().getScreenSize();
        setBounds((screenSize.width-864)/2, (screenSize.height-498)/2, 864, 498);
    }// </editor-fold>//GEN-END:initComponents

    private void tblPOMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tblPOMouseClicked
        if(evt.getClickCount()>=2)
            tambahItem();
    }//GEN-LAST:event_tblPOMouseClicked

    private void btnOKKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_btnOKKeyPressed
        if(evt.getKeyCode()==KeyEvent.VK_ENTER){
            tambahItem();
        }
    }//GEN-LAST:event_btnOKKeyPressed

    private void btnOKActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnOKActionPerformed
        tambahItem();
    }//GEN-LAST:event_btnOKActionPerformed

    private void jButton2KeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jButton2KeyPressed
        if(evt.getKeyCode()==KeyEvent.VK_ENTER)
            this.dispose();
    }//GEN-LAST:event_jButton2KeyPressed

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        this.dispose();
    }//GEN-LAST:event_jButton2ActionPerformed

    private void formWindowOpened(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowOpened
        hModel=(DefaultTableModel)tblPO.getModel();
        hModel.setNumRows(0);
        dModel=(DefaultTableModel)tblPODet.getModel();
        dModel.setNumRows(0);
        
        udfLoadSupplier();
        udfLoadTablePO();
        TableLook();
        
    }//GEN-LAST:event_formWindowOpened

    private void tblPODetKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_tblPODetKeyPressed
// TODO add your handling code here:
    }//GEN-LAST:event_tblPODetKeyPressed

    private void tblPODetFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_tblPODetFocusGained
// TODO add your handling code here:
    }//GEN-LAST:event_tblPODetFocusGained

    private void tblPOKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_tblPOKeyPressed
//        switch (evt.getKeyCode()){
//            case java.awt.event.KeyEvent.VK_F2:{
//                udfUpdateData();
//                break;
//            }
//            case java.awt.event.KeyEvent.VK_INSERT:{
//                udfInsertDetail();
//                break;
//            }
//            case java.awt.event.KeyEvent.VK_DELETE:{
//                delItem();
//                break;
//            }
//            case java.awt.event.KeyEvent.VK_ENTER:{
//                if(tblGR.getRowCount()>=0){
//                    setEnableComp1(true);
//                    txtKodeBarang.setEnabled(false);
//                    rowSelected=tblGR.getSelectedRow();
//                    if (rowSelected==0){
//                        if (tblGR.getRowCount()>0){
//                            rowSelected=tblGR.getRowCount()-1;} else {rowSelected=0;}
//                    } else {if (tblGR.getRowCount()>0){rowSelected--;}else {rowSelected=0;}
//                    }
//                    tblGR.setRowSelectionInterval(rowSelected,rowSelected);
//                    ftQty.requestFocus();
//                    editItem=true;
//                    break;
//                }
//            }
//        }
    }//GEN-LAST:event_tblPOKeyPressed

    private void tblPOFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_tblPOFocusGained
//setEnableComp1(false);
    }//GEN-LAST:event_tblPOFocusGained
    
    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new FrmLookupPO(new javax.swing.JFrame(), true).setVisible(true);
            }
        });
    }

    void setFormAsal(frmGood_Receipt1 frmGood_Receipt) {
        fAsal=frmGood_Receipt;
    }

    void setSrcModel(DefaultTableModel defaultTableModel) {
        srcModel=defaultTableModel;
    }
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnOK;
    private javax.swing.JButton jButton2;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JLabel lblAlamat;
    private javax.swing.JLabel lblAlamat1;
    private javax.swing.JLabel lblKode;
    private javax.swing.JLabel lblNama;
    private javax.swing.JLabel lblTelp;
    private javax.swing.JTable tblPO;
    private javax.swing.JTable tblPODet;
    // End of variables declaration//GEN-END:variables
    
    private NumberFormat formatter = new DecimalFormat("#,###,###");
    private NumberFormat formatter1 = new DecimalFormat("#,###,###.##");
}
