/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package apotek.inventori;

import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import main.GeneralFunction;

/**
 *
 * @author faheem
 */
public class FrmExpiredDate extends javax.swing.JInternalFrame {
    private Connection conn;
    private GeneralFunction fn=new GeneralFunction();
    
    /**
     * Creates new form FrmExpiredDate
     */
    public FrmExpiredDate() {
        initComponents();
        for(int i=0; i< jTable1.getColumnCount(); i++){
            jTable1.getColumnModel().getColumn(i).setCellRenderer(new MyRowRenderer());
        }
    }

    public void setConn(Connection c){
        this.conn=c;
    }
    
    private void tampilkan(){
        
        String bulan1=jYearAwal.getYear()+"-"+new DecimalFormat("00").format(jMonthAwal.getMonth()+1);
        String bulan2=jYearAkhir.getYear()+"-"+new DecimalFormat("00").format(jMonthAkhir.getMonth()+1);
        String sQry="select distinct d.kode_barang, i.item_name, expired_date, coalesce(s.nama_supplier,'') as supplier\n" +
                    "from phar_good_receipt g \n" +
                    "inner join phar_good_receipt_detail d on d.good_receipt_id=g.good_receipt_id\n" +
                    "inner join barang i on i.item_code=d.kode_barang\n" +
                    "left join phar_supplier s on s.kode_supplier=g.kode_supp\n"
                    + "where to_char(d.expired_date, 'yyyy-MM') >='"+bulan1+"' "
                    + "and to_char(d.expired_date, 'yyyy-MM')<='"+bulan2+"' " +
                    "order by expired_date, i.item_name";
//        System.out.println(sQry);
        setCursor(new Cursor(Cursor.WAIT_CURSOR));
        ((DefaultTableModel)jTable1.getModel()).setNumRows(0);
        try {
            ResultSet rs=conn.createStatement().executeQuery(sQry);
            while(rs.next()){
                ((DefaultTableModel)jTable1.getModel()).addRow(new Object[]{
                    rs.getString("kode_barang"),
                    rs.getString("item_name"),
                    rs.getDate("expired_date"),
                    rs.getString("supplier")
                });
            }
            
            if(jTable1.getRowCount() > 0){
                fn.setAutoResizeColWidth(jTable1);
                jTable1.setRowSelectionInterval(0, 0);
            }
            setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
        } catch (SQLException ex) {
            setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
            Logger.getLogger(FrmExpiredDate.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }
    /**
     * 
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jMonthAkhir = new com.toedter.calendar.JMonthChooser();
        jMonthAwal = new com.toedter.calendar.JMonthChooser();
        jYearAwal = new com.toedter.calendar.JYearChooser();
        jYearAkhir = new com.toedter.calendar.JYearChooser();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jButton1 = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();

        setClosable(true);
        setIconifiable(true);
        setMaximizable(true);
        setResizable(true);
        setTitle("Histori Expired Date");

        jPanel1.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        jPanel1.setLayout(null);
        jPanel1.add(jMonthAkhir);
        jMonthAkhir.setBounds(340, 20, 134, 27);
        jPanel1.add(jMonthAwal);
        jMonthAwal.setBounds(70, 20, 120, 27);
        jPanel1.add(jYearAwal);
        jYearAwal.setBounds(190, 20, 60, 27);
        jPanel1.add(jYearAkhir);
        jYearAkhir.setBounds(460, 20, 65, 27);

        jLabel1.setText("Bulan :");
        jPanel1.add(jLabel1);
        jLabel1.setBounds(5, 20, 65, 25);

        jLabel2.setText("Sampai : ");
        jPanel1.add(jLabel2);
        jLabel2.setBounds(260, 20, 80, 25);

        jButton1.setText("Tampilkan");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });
        jPanel1.add(jButton1);
        jButton1.setBounds(525, 20, 105, 29);

        jTable1.setAutoCreateRowSorter(true);
        jTable1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Kode", "Nama Barang", "Expired", "Supplier"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jTable1.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_OFF);
        jScrollPane1.setViewportView(jTable1);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(12, 12, 12)
                .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGap(8, 8, 8))
            .addGroup(layout.createSequentialGroup()
                .addGap(15, 15, 15)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 645, Short.MAX_VALUE)
                .addGap(10, 10, 10))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(10, 10, 10)
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, 0)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 265, Short.MAX_VALUE)
                .addGap(10, 10, 10))
        );

        setBounds(0, 0, 681, 372);
    }// </editor-fold>//GEN-END:initComponents

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        tampilkan();
    }//GEN-LAST:event_jButton1ActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private com.toedter.calendar.JMonthChooser jMonthAkhir;
    private com.toedter.calendar.JMonthChooser jMonthAwal;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable jTable1;
    private com.toedter.calendar.JYearChooser jYearAkhir;
    private com.toedter.calendar.JYearChooser jYearAwal;
    // End of variables declaration//GEN-END:variables

    public class MyRowRenderer extends DefaultTableCellRenderer implements TableCellRenderer{
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {


            if(value instanceof Date ){
                value=fn.ddMMyy_format.format(value);
            }else if(value instanceof Double ||value instanceof Float  ){
                setHorizontalAlignment(SwingConstants.RIGHT);
                value=fn.intFmt.format(value);
            }else if(value instanceof Integer ){
                setHorizontalAlignment(SwingConstants.RIGHT);
                value=fn.intFmt.format(value);

            }
            setFont(table.getFont());
            if(isSelected){
                setBackground(table.getSelectionBackground());
                setForeground(table.getSelectionForeground());
            }else{
                setBackground(table.getBackground());
                setForeground(table.getForeground());
            }

            if (hasFocus) {
                setBorder( UIManager.getBorder("Table.focusCellHighlightBorder") );
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
}