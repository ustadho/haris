/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * FrmIPDRegistered.java
 *
 * Created on Apr 28, 2010, 8:54:29 AM
 */

package com.klinik.dokter;

import apotek.Main;
import com.klinik.model.BaseModel;
import java.awt.Color;
import java.awt.Component;
import java.awt.event.KeyEvent;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import main.GeneralFunction;
import main.MainForm;
import org.jdesktop.swingx.decorator.AlternateRowHighlighter;
import org.jdesktop.swingx.decorator.Highlighter;
import org.jdesktop.swingx.decorator.RolloverHighlighter;

/**
 *
 * @author ustadho
 */
public class FrmRegList extends javax.swing.JInternalFrame {
    private Connection conn;
    List<BaseModel> listDokter=new ArrayList<BaseModel>();

    /** Creates new form FrmIPDRegistered */
    public FrmRegList() {
        try {
            initComponents();
            jXTable1.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
                
                @Override
                public void valueChanged(ListSelectionEvent e) {
                    if(conn!=null && jXTable1.getSelectedRow()>=0){
                        //udfLoadDiagnosaPasien();
                    }
                }
            });
            
            jXTable1.getColumn("Nama").setPreferredWidth(150);
            jXTable1.getColumn("Alamat").setPreferredWidth(150);
            jXTable1.getColumn("Tgl. Masuk").setCellRenderer(new MyRowRenderer());
            jXTable1.getColumn("Tgl. Lahir").setCellRenderer(new MyRowRenderer());
            
            cmbDokter.removeAllItems();
            ResultSet rs=Main.conn.createStatement().executeQuery("select kode_dokter, nama ||coalesce(', '||gelar_depan,'')||coalesce(', '||gelar_belakang,'') as nama "
                    + "from rm_dokter order by nama");
            while(rs.next()){
                BaseModel b=new BaseModel();
                b.setKode(rs.getString("kode_dokter"));
                b.setNama(rs.getString("nama"));
                listDokter.add(b);
                cmbDokter.addItem(rs.getString("nama"));
            }
            rs.close();
            cmbDokter.setSelectedItem(MainForm.sNamaDokter);
        } catch (SQLException ex) {
            Logger.getLogger(FrmRegList.class.getName()).log(Level.SEVERE, null, ex);
        }
            
    }

//    public void udfLoadDiagnosaPasien(){
//        try{
//            String sNoReg=jXTable1.getValueAt(jXTable1.getSelectedRow(), 0).toString();
//            String sDet="select coalesce(h.catatan,'') as catatan, coalesce(d.kode_icd,'') as kode_icd, coalesce(icd.ket_icd,'') as ket_icd ," +
//                    "coalesce(to_char(datetime_ins, 'dd/MM/yyyy     hh24:MI'),'') as datetime_ins " +
//                    "from rmo_diagnosa h " +
//                    "inner join rmo_diagnosa_detail d on d.id=h.id " +
//                    "left join rm_dtd_icd_final icd on icd.kode_icd=d.kode_icd " +
//                    "where coalesce(is_delete, false)=false and no_reg='"+sNoReg+"' " +
//                    "order by datetime_ins desc, urut";
//
//            ResultSet rs=conn.createStatement().executeQuery(sDet);
//
//            jTextArea1.setText(""); lblDateTime.setText("");
//            String spasi="";
//            System.out.println(sDet);
//            
//            while(rs.next()){
//                spasi= jTextArea1.getText().length()>0? jTextArea1.getText()+"-------------------------------------------------------------------\n" : "";
//                //lblDateTime.setText(rs.getString("datetime_ins"));
//                jTextArea1.setText((spasi+rs.getString("catatan")+" ("+rs.getString("datetime_ins")+")\n"));
//                jTextArea1.setText((jTextArea1.getText()+" * "+rs.getString("ket_icd")+" ("+rs.getString("kode_icd")+")\n"));
//                jTextArea1.setText(jTextArea1.getText()+"\n");
//            }
//            rs.close();
//
//            jTextArea1.setCaretPosition(0);
//        }catch(SQLException s){
//            System.err.println("Error di listSelectionListener\n"+s.getMessage());
//        }
//    }
    private int searchData(int idxAwal, String sText){
        int index=-1;
        int i=-1;
        idxAwal=idxAwal<0? 0: idxAwal+1;
        for(i=idxAwal; i < jXTable1.getRowCount(); i++){
            for(int col=0; col< jXTable1.getColumnCount(); col++){
                if(jXTable1.getValueAt(i, col).toString().toUpperCase().indexOf(sText.toUpperCase())>=0){
                    return i;
                }
            }
        }
        if(i==jXTable1.getRowCount()){
            if(JOptionPane.showConfirmDialog(this, "Data tidak ditemukan. Anda ingin mengulangi dari awal?", "Confirm", JOptionPane.YES_NO_OPTION)==JOptionPane.YES_OPTION){
                index=searchData(-1, sText);
            }

        }

        return index;
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jScrollPane1 = new javax.swing.JScrollPane();
        jXTable1 = new org.jdesktop.swingx.JXTable();
        btnHistory = new javax.swing.JButton();
        jLabel2 = new javax.swing.JLabel();
        txtSearch = new javax.swing.JTextField();
        btnRefresh = new javax.swing.JButton();
        btnBatal = new javax.swing.JButton();
        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        cmbDokter = new javax.swing.JComboBox();
        jButton1 = new javax.swing.JButton();

        setClosable(true);
        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setIconifiable(true);
        setMaximizable(true);
        setResizable(true);
        addInternalFrameListener(new javax.swing.event.InternalFrameListener() {
            public void internalFrameActivated(javax.swing.event.InternalFrameEvent evt) {
            }
            public void internalFrameClosed(javax.swing.event.InternalFrameEvent evt) {
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

        jXTable1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, null, null, null}
            },
            new String [] {
                "No. Reg", "Tgl. Masuk", "Norm", "Nama", "Tgl. Lahir", "Usia", "Alamat", "Jam. Masuk"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.Object.class, java.lang.String.class, java.lang.String.class, java.lang.Object.class, java.lang.String.class, java.lang.String.class, java.lang.Object.class
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
        jXTable1.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_OFF);
        jXTable1.getTableHeader().setReorderingAllowed(false);
        jScrollPane1.setViewportView(jXTable1);

        getContentPane().add(jScrollPane1, new org.netbeans.lib.awtextra.AbsoluteConstraints(15, 40, 760, 400));

        btnHistory.setText("Histori Diagnosa");
        btnHistory.setMargin(new java.awt.Insets(2, 2, 2, 2));
        btnHistory.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnHistoryActionPerformed(evt);
            }
        });
        getContentPane().add(btnHistory, new org.netbeans.lib.awtextra.AbsoluteConstraints(516, 445, 161, -1));

        jLabel2.setText("Search :");
        getContentPane().add(jLabel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(15, 447, 70, 20));

        txtSearch.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txtSearchKeyPressed(evt);
            }
        });
        getContentPane().add(txtSearch, new org.netbeans.lib.awtextra.AbsoluteConstraints(85, 447, 164, -1));

        btnRefresh.setText("Refresh");
        btnRefresh.setMargin(new java.awt.Insets(2, 2, 2, 2));
        btnRefresh.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnRefreshActionPerformed(evt);
            }
        });
        getContentPane().add(btnRefresh, new org.netbeans.lib.awtextra.AbsoluteConstraints(255, 445, 94, -1));

        btnBatal.setText("Close");
        btnBatal.setMargin(new java.awt.Insets(2, 2, 2, 2));
        btnBatal.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnBatalActionPerformed(evt);
            }
        });
        getContentPane().add(btnBatal, new org.netbeans.lib.awtextra.AbsoluteConstraints(683, 445, 90, -1));

        jPanel1.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        jPanel1.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel1.setText("Dokter : ");
        jPanel1.add(jLabel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(15, 4, 60, 20));

        cmbDokter.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        cmbDokter.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                cmbDokterItemStateChanged(evt);
            }
        });
        jPanel1.add(cmbDokter, new org.netbeans.lib.awtextra.AbsoluteConstraints(75, 5, 310, -1));

        jButton1.setText("Refresh");
        jPanel1.add(jButton1, new org.netbeans.lib.awtextra.AbsoluteConstraints(395, 5, 105, -1));

        getContentPane().add(jPanel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 5, 665, 30));

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void formInternalFrameOpened(javax.swing.event.InternalFrameEvent evt) {//GEN-FIRST:event_formInternalFrameOpened
        udfInitForm();
    }//GEN-LAST:event_formInternalFrameOpened

    private void btnRefreshActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnRefreshActionPerformed
        txtSearch.setText("");
        udfLoadGrid();
    }//GEN-LAST:event_btnRefreshActionPerformed

    private void txtSearchKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtSearchKeyPressed
        if(txtSearch.getText().length()>0 && evt.getKeyCode()==KeyEvent.VK_ENTER){
            int iRow=searchData(jXTable1.getSelectedRow(), txtSearch.getText());
            if(iRow>=0){
                jXTable1.setRowSelectionInterval(iRow, iRow);
                jXTable1.changeSelection(iRow, 0, false, false);
                txtSearch.requestFocus();
            }
        }
    }//GEN-LAST:event_txtSearchKeyPressed

    private void btnBatalActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnBatalActionPerformed
        this.dispose();
}//GEN-LAST:event_btnBatalActionPerformed

    private void btnHistoryActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnHistoryActionPerformed
        //DiagnosaHistory d1=new DiagnosaHistory(JOptionPane.getFrameForComponent(this), true);
        DiagnosaHistory d1=new DiagnosaHistory();
        d1.setConn(conn);
        d1.setNorm(jXTable1.getValueAt(jXTable1.getSelectedRow(), 2).toString());
        d1.setNoReg(jXTable1.getValueAt(jXTable1.getSelectedRow(), 0).toString());
        d1.setVisible(true);
    }//GEN-LAST:event_btnHistoryActionPerformed

    private void cmbDokterItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_cmbDokterItemStateChanged
        if(listDokter.size() > 0 && Main.conn!=null){
            udfLoadGrid();
        }
    }//GEN-LAST:event_cmbDokterItemStateChanged

    /**
    * @param args the command line arguments
    */
    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new FrmRegList().setVisible(true);
            }
        });
    }

    private void udfInitForm(){
        jXTable1.setHighlighters(new Highlighter[]{AlternateRowHighlighter. classicLinePrinter});
        jXTable1.addHighlighter(new RolloverHighlighter(Color.ORANGE, Color.MAGENTA));
        jXTable1.setRolloverEnabled(true);
        jXTable1.setColumnControlVisible(true);
        jXTable1.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        jXTable1.setRowHeight(22);
        udfLoadGrid();

    }

    public void setConn(Connection conn) {
        this.conn=conn;
    }
    GeneralFunction fn=new GeneralFunction();
    private void udfLoadGrid(){
        try {
            String sQry="select r.no_reg, r.tanggal, r.norm, ps.nama, ps.tgl_lahir, age(current_date, ps.tgl_lahir) as usia, coalesce(ps.alamat_domisili,'') as alamat,\n" +
                                            "r.time_ins\n" +
                                            "from rm_reg r\n" +
                                            "inner join rm_pasien ps on ps.norm=r.norm\n" +
                                            "where r.tanggal=current_date and r.kode_dokter='"+listDokter.get(cmbDokter.getSelectedIndex()).getKode()+"'\n" +
                                            "order by r.time_ins\n";
//            System.out.println(sQry);
            ResultSet rs = MainForm.conn.createStatement().executeQuery(sQry);

            ((DefaultTableModel)jXTable1.getModel()).setNumRows(0);
            while(rs.next()){
                ((DefaultTableModel)jXTable1.getModel()).addRow(new Object[]{
                    rs.getString("no_reg"),
                    rs.getDate("tanggal"),
                    rs.getString("norm"),
                    rs.getString("nama"),
                    rs.getDate("tgl_lahir"),
                    rs.getString("usia"),
                    rs.getString("alamat"),
                    rs.getTime("time_ins"),
                });
            }
            if(jXTable1.getRowCount()>0){
                jXTable1.setRowSelectionInterval(0, 0);
                fn.autoColWidthTable(jXTable1);
            }
        } catch (SQLException ex) {
            Logger.getLogger(FrmRegList.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnBatal;
    private javax.swing.JButton btnHistory;
    private javax.swing.JButton btnRefresh;
    private javax.swing.JComboBox cmbDokter;
    private javax.swing.JButton jButton1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private org.jdesktop.swingx.JXTable jXTable1;
    private javax.swing.JTextField txtSearch;
    // End of variables declaration//GEN-END:variables

    private void udfUpdateDiagnosa() {
        if(jXTable1.getSelectedRow()>=0){
            String sNoReg=jXTable1.getValueAt(jXTable1.getSelectedRow(), 0).toString();
            DiagnosaEdit d1=new DiagnosaEdit();
            d1.setSrcForm(this);
            d1.setConn(conn);
            d1.setNoReg(sNoReg);
            d1.setVisible(true);
        }
    }
    // End of variables declaration

    class MyRowRenderer extends DefaultTableCellRenderer implements TableCellRenderer{
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            setFont(table.getFont());
            if(value instanceof Date){
                value=new SimpleDateFormat("dd/MM/yyyy").format(value);
            }

            if(isSelected){
                setBackground(table.getSelectionBackground());
                setForeground(table.getSelectionForeground());
            }
            setValue(value);
            return this;
        }
    }

}
