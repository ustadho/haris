/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * FrmSettingHargaJual.java
 *
 * Created on Jun 7, 2010, 5:45:25 AM
 */

package apotek;

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Cursor;
import java.awt.FocusTraversalPolicy;
import java.awt.KeyboardFocusManager;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.beans.PropertyVetoException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import javax.swing.JFormattedTextField;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumnModel;
import main.GeneralFunction;
import org.jdesktop.swingx.JXTable;

/**
 *
 * @author cak-ust
 */
public class FrmSettingHargaJual extends javax.swing.JInternalFrame {
    private Connection conn;
    private Component aThis;
    private GeneralFunction fn=new GeneralFunction();
    MyKeyListener kListener=new MyKeyListener();
    ArrayList lstKov=new ArrayList();
    private JDesktopImage desktop;

    /** Creates new form FrmSettingHargaJual */
    public FrmSettingHargaJual() {
        initComponents();
        aThis=this;
        masterTable.getSelectionModel().addListSelectionListener(new ListSelectionListener() {

            public void valueChanged(ListSelectionEvent e) {
                int iRow=masterTable.getSelectedRow();
                txtHarga2R.setEnabled(false);
                txtHarga2NR.setEnabled(false);
                hypEdit.setEnabled(iRow>=0);     hypDelete.setEnabled(iRow>=0);
                lblKonv2R.setText("1");     lblKonv2NR.setText("1");
                txtHarga1R.setText("0");    txtHarga2R.setText("0");
                txtHarga2NR.setText("0");

                if(iRow>=0){
                    try{
                        String sItem=masterTable.getValueAt(iRow, masterTable.getColumnModel().getColumnIndex("Kode")).toString();
                        String sQry="select coalesce(satuan_kecil,'') as unit1," +
                                "coalesce(satuan_besar,'') as unit2," +
                                "coalesce(konversi,1) as konv," +
                                "coalesce(harga_kecil_resep,0) as harga_kr," +
                                "coalesce(harga_kecil_non_resep,0) as harga_knr, " +
                                "coalesce(harga_besar_resep,0) as harga_br," +
                                "coalesce(harga_besar_non_resep,0) as harga_bnr, " +
                                "coalesce(i.hpp,0) as hpp " +
                                "from barang i " +
                                "where i.item_code='"+sItem+"' ";

                        //System.out.println(sQry);
                        ResultSet rs=conn.createStatement().executeQuery(sQry                                );


                        if(rs.next()){
                            lstKov.clear(); 
                            lstKov.add(1);
                            lblSat1R.setText(rs.getString("unit1"));    lblSat1NR.setText(rs.getString("unit1"));
                            lblSat2R.setText(rs.getString("unit2"));    lblSat2NR.setText(rs.getString("unit2"));

                            lblKonv2R.setText(rs.getString("konv")+ " " +rs.getString("unit1"));   lblKonv2NR.setText(rs.getString("konv")+" " +rs.getString("unit1"));
                            txtHarga1R.setText(fn.intFmt.format(rs.getDouble("harga_kr")));
                            txtHarga2R.setText(fn.intFmt.format(rs.getDouble("harga_br")));
                            txtHarga1NR.setText(fn.intFmt.format(rs.getDouble("harga_knr")));
                            txtHarga2NR.setText(fn.intFmt.format(rs.getDouble("harga_bnr")));
                            
                            txtHarga2R.setEnabled(rs.getString("unit2").length()>0);
                            txtHarga2NR.setEnabled(rs.getString("unit2").length()>0);

                            txtHargaPokok.setText(fn.intFmt.format(rs.getDouble("hpp")));

                            rs=conn.createStatement().executeQuery("select * from kartu_stock where item_code='"+sItem+"' limit 1");
                            txtHargaPokok.setEnabled(!rs.next());
                            
                        }else{
                            udfClear();
                        }
                        rs.close();
                    }catch(SQLException se){
                        JOptionPane.showMessageDialog(aThis, se.getMessage());
                    }
                }
            }
        });

        masterTable.getModel().addTableModelListener(new TableModelListener() {
            public void tableChanged(TableModelEvent e) {
                lblItemCount.setText(masterTable.getRowCount()+" Item(s)");
            }
        });
        fn.addKeyListenerInContainer(jPanel1, kListener, txtFocusListener);
        fn.addKeyListenerInContainer(jPanel3, kListener, txtFocusListener);
        fn.addKeyListenerInContainer(jPanel4, kListener, txtFocusListener);
        fn.addKeyListenerInContainer(jPanel5, kListener, txtFocusListener);
        masterTable.addKeyListener(kListener);

    }

    private void udfClear() {
        lblSat1R.setText("");    lblSat1NR.setText("");
        lblSat2R.setText("");    lblSat2NR.setText("");
        lblKonv2R.setText("");   lblKonv2R.setText("");
        txtHarga1R.setText("");
        txtHarga2R.setText("");
        txtHarga1NR.setText("");
        txtHarga2NR.setText("");
        txtHargaPokok.setText("0");
}

    public void setConn(Connection con){
        this.conn=con;
    }

    public void udfFilter(String sKode){
        int iPos=-1;
        this.setCursor(new Cursor(Cursor.WAIT_CURSOR));
        try{
            final ResultSet rs=conn.createStatement().executeQuery(
                    "select i.item_code, coalesce(i.item_name,'') as item_name, " +
                    "coalesce(j.jenis_barang,'') as jenis " +
                    "from barang i " +
                    "left join jenis_barang j on j.kode_jenis=i.kode_jenis " +
                    "where i.item_code||coalesce(item_name,'')||coalesce(j.jenis_barang,'') ilike '%"+txtCari.getText()+"%' " +
                    "order by coalesce(i.item_name,'')");

            ((DefaultTableModel)masterTable.getModel()).setNumRows(0);
            while(rs.next()){
                ((DefaultTableModel)masterTable.getModel()).addRow(new Object[]{
                    rs.getString("item_code"),
                    rs.getString("item_name"),
                    rs.getString("jenis")
                });
                iPos=sKode.equalsIgnoreCase(rs.getString("item_code"))? ((DefaultTableModel)masterTable.getModel()).getRowCount()-1: iPos;
            }
            if(masterTable.getRowCount()>0){
                iPos=iPos<0? 0: iPos;
                //masterTable.setRowSelectionInterval(iPos, iPos);
                //masterTable.changeSelection(iPos, 0, false, false);
                masterTable.setModel((DefaultTableModel)fn.autoResizeColWidth(masterTable, (DefaultTableModel)masterTable.getModel()).getModel());
            }
            this.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
            rs.close();
            finalize();
        } catch (Throwable ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage());
        }
//        catch(SQLException se){
//            this.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
//            JOptionPane.showMessageDialog(this, se.getMessage());
//        }
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jSeparator1 = new javax.swing.JSeparator();
        jLabel9 = new javax.swing.JLabel();
        jSeparator3 = new javax.swing.JSeparator();
        jPanel3 = new javax.swing.JPanel();
        jLabel10 = new javax.swing.JLabel();
        jLabel11 = new javax.swing.JLabel();
        jLabel12 = new javax.swing.JLabel();
        lblKonv1NR = new javax.swing.JLabel();
        lblSat1NR = new javax.swing.JLabel();
        lblKonv2NR = new javax.swing.JLabel();
        lblSat2NR = new javax.swing.JLabel();
        txtHarga1NR = new javax.swing.JTextField();
        txtHarga2NR = new javax.swing.JTextField();
        jPanel4 = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        lblKonv1R = new javax.swing.JLabel();
        lblSat1R = new javax.swing.JLabel();
        lblKonv2R = new javax.swing.JLabel();
        lblSat2R = new javax.swing.JLabel();
        txtHarga1R = new javax.swing.JTextField();
        txtHarga2R = new javax.swing.JTextField();
        btnUpdate = new javax.swing.JButton();
        lblSat3E1 = new javax.swing.JLabel();
        txtHargaPokok = new javax.swing.JTextField();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        masterTable = new org.jdesktop.swingx.JXTable();
        jPanel5 = new javax.swing.JPanel();
        jLabel13 = new javax.swing.JLabel();
        txtCari = new javax.swing.JTextField();
        hypDelete = new org.jdesktop.swingx.JXHyperlink();
        hypNew = new org.jdesktop.swingx.JXHyperlink();
        hypEdit = new org.jdesktop.swingx.JXHyperlink();
        lblItemCount = new javax.swing.JLabel();

        setClosable(true);
        setIconifiable(true);
        setMaximizable(true);
        setResizable(true);
        setTitle("Setting Harga Jual");
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

        jPanel1.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        jPanel1.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel1.setFont(new java.awt.Font("Tahoma", 1, 11));
        jLabel1.setText("Harga Resep");
        jPanel1.add(jLabel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 10, 90, 20));
        jPanel1.add(jSeparator1, new org.netbeans.lib.awtextra.AbsoluteConstraints(90, 18, 170, -1));

        jLabel9.setFont(new java.awt.Font("Tahoma", 1, 11));
        jLabel9.setText("Harga Non Resep");
        jPanel1.add(jLabel9, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 140, 110, 20));
        jPanel1.add(jSeparator3, new org.netbeans.lib.awtextra.AbsoluteConstraints(90, 150, 170, -1));

        jPanel3.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel10.setBackground(new java.awt.Color(153, 153, 255));
        jLabel10.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel10.setText("Harga");
        jLabel10.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        jLabel10.setOpaque(true);
        jPanel3.add(jLabel10, new org.netbeans.lib.awtextra.AbsoluteConstraints(80, 0, 90, 20));

        jLabel11.setBackground(new java.awt.Color(153, 153, 255));
        jLabel11.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel11.setText("Konv");
        jLabel11.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        jLabel11.setOpaque(true);
        jPanel3.add(jLabel11, new org.netbeans.lib.awtextra.AbsoluteConstraints(170, 0, 80, 20));

        jLabel12.setBackground(new java.awt.Color(153, 153, 255));
        jLabel12.setText("Satuan");
        jLabel12.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        jLabel12.setOpaque(true);
        jPanel3.add(jLabel12, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 80, 20));

        lblKonv1NR.setBackground(new java.awt.Color(153, 255, 255));
        lblKonv1NR.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lblKonv1NR.setText("1");
        lblKonv1NR.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        lblKonv1NR.setOpaque(true);
        jPanel3.add(lblKonv1NR, new org.netbeans.lib.awtextra.AbsoluteConstraints(170, 20, 80, 20));

        lblSat1NR.setBackground(new java.awt.Color(153, 255, 255));
        lblSat1NR.setText("PCS");
        lblSat1NR.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        lblSat1NR.setOpaque(true);
        jPanel3.add(lblSat1NR, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 20, 80, 20));

        lblKonv2NR.setBackground(new java.awt.Color(153, 255, 255));
        lblKonv2NR.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lblKonv2NR.setText("10");
        lblKonv2NR.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        lblKonv2NR.setOpaque(true);
        jPanel3.add(lblKonv2NR, new org.netbeans.lib.awtextra.AbsoluteConstraints(170, 40, 80, 20));

        lblSat2NR.setBackground(new java.awt.Color(153, 255, 255));
        lblSat2NR.setText("BOX");
        lblSat2NR.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        lblSat2NR.setOpaque(true);
        jPanel3.add(lblSat2NR, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 40, 80, 20));

        txtHarga1NR.setFont(new java.awt.Font("Tahoma", 0, 12));
        txtHarga1NR.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtHarga1NR.setText("0");
        txtHarga1NR.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        txtHarga1NR.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                txtHarga1NRKeyTyped(evt);
            }
        });
        jPanel3.add(txtHarga1NR, new org.netbeans.lib.awtextra.AbsoluteConstraints(80, 20, 90, 20));

        txtHarga2NR.setFont(new java.awt.Font("Tahoma", 0, 12));
        txtHarga2NR.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtHarga2NR.setText("0");
        txtHarga2NR.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        txtHarga2NR.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                txtHarga2NRKeyTyped(evt);
            }
        });
        jPanel3.add(txtHarga2NR, new org.netbeans.lib.awtextra.AbsoluteConstraints(80, 40, 90, 20));

        jPanel1.add(jPanel3, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 160, 250, 90));

        jPanel4.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel2.setBackground(new java.awt.Color(153, 153, 255));
        jLabel2.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel2.setText("Harga");
        jLabel2.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        jLabel2.setOpaque(true);
        jPanel4.add(jLabel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(80, 0, 90, 20));

        jLabel3.setBackground(new java.awt.Color(153, 153, 255));
        jLabel3.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel3.setText("Konv");
        jLabel3.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        jLabel3.setOpaque(true);
        jPanel4.add(jLabel3, new org.netbeans.lib.awtextra.AbsoluteConstraints(170, 0, 80, 20));

        jLabel4.setBackground(new java.awt.Color(153, 153, 255));
        jLabel4.setText("Satuan");
        jLabel4.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        jLabel4.setOpaque(true);
        jPanel4.add(jLabel4, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 80, 20));

        lblKonv1R.setBackground(new java.awt.Color(153, 255, 255));
        lblKonv1R.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lblKonv1R.setText("1");
        lblKonv1R.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        lblKonv1R.setOpaque(true);
        jPanel4.add(lblKonv1R, new org.netbeans.lib.awtextra.AbsoluteConstraints(170, 20, 80, 20));

        lblSat1R.setBackground(new java.awt.Color(153, 255, 255));
        lblSat1R.setText("PCS");
        lblSat1R.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        lblSat1R.setOpaque(true);
        jPanel4.add(lblSat1R, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 20, 80, 20));

        lblKonv2R.setBackground(new java.awt.Color(153, 255, 255));
        lblKonv2R.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lblKonv2R.setText("10");
        lblKonv2R.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        lblKonv2R.setOpaque(true);
        jPanel4.add(lblKonv2R, new org.netbeans.lib.awtextra.AbsoluteConstraints(170, 40, 80, 20));

        lblSat2R.setBackground(new java.awt.Color(153, 255, 255));
        lblSat2R.setText("BOX");
        lblSat2R.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        lblSat2R.setOpaque(true);
        jPanel4.add(lblSat2R, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 40, 80, 20));

        txtHarga1R.setFont(new java.awt.Font("Tahoma", 0, 12));
        txtHarga1R.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtHarga1R.setText("0");
        txtHarga1R.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        txtHarga1R.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                txtHarga1RKeyTyped(evt);
            }
        });
        jPanel4.add(txtHarga1R, new org.netbeans.lib.awtextra.AbsoluteConstraints(80, 20, 90, 20));

        txtHarga2R.setFont(new java.awt.Font("Tahoma", 0, 12));
        txtHarga2R.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtHarga2R.setText("0");
        txtHarga2R.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        txtHarga2R.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                txtHarga2RKeyTyped(evt);
            }
        });
        jPanel4.add(txtHarga2R, new org.netbeans.lib.awtextra.AbsoluteConstraints(80, 40, 90, 20));

        jPanel1.add(jPanel4, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 30, 250, 90));

        btnUpdate.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/Icon/buttons/Ok-32.png"))); // NOI18N
        btnUpdate.setText("Update");
        btnUpdate.setMargin(new java.awt.Insets(2, 2, 2, 2));
        btnUpdate.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnUpdateActionPerformed(evt);
            }
        });
        jPanel1.add(btnUpdate, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 340, 250, -1));

        lblSat3E1.setBackground(new java.awt.Color(153, 255, 255));
        lblSat3E1.setText("Harga Pokok");
        lblSat3E1.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        lblSat3E1.setOpaque(true);
        jPanel1.add(lblSat3E1, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 310, 80, 20));

        txtHargaPokok.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        txtHargaPokok.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtHargaPokok.setText("0");
        txtHargaPokok.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        txtHargaPokok.setDisabledTextColor(new java.awt.Color(0, 0, 0));
        txtHargaPokok.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                txtHargaPokokKeyTyped(evt);
            }
        });
        jPanel1.add(txtHargaPokok, new org.netbeans.lib.awtextra.AbsoluteConstraints(90, 310, 90, 20));

        jLabel5.setText(" (Harga Sat. Kecil)");
        jPanel1.add(jLabel5, new org.netbeans.lib.awtextra.AbsoluteConstraints(180, 310, 100, 20));

        jLabel6.setForeground(new java.awt.Color(0, 0, 153));
        jLabel6.setText("<html> <b>F5 &nbsp:</b> Simpan Harga Penjualan<br> <b>F12:</b> Focus ke Harga Penjualan <br> </html>"); // NOI18N
        jLabel6.setVerticalAlignment(javax.swing.SwingConstants.TOP);
        jPanel1.add(jLabel6, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 400, 170, 50));

        masterTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Kode", "Nama Barang", "Kategori", "Lokasi", "Tipe"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        masterTable.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_OFF);
        masterTable.getTableHeader().setReorderingAllowed(false);
        masterTable.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                masterTableMouseClicked(evt);
            }
        });
        masterTable.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                masterTableKeyPressed(evt);
            }
        });
        jScrollPane1.setViewportView(masterTable);

        jPanel5.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel13.setText("Pencarian :");
        jPanel5.add(jLabel13, new org.netbeans.lib.awtextra.AbsoluteConstraints(190, 10, 80, 20));

        txtCari.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        jPanel5.add(txtCari, new org.netbeans.lib.awtextra.AbsoluteConstraints(270, 10, 160, 20));

        hypDelete.setMnemonic('D');
        hypDelete.setText("Hapus");
        hypDelete.setFont(new java.awt.Font("Tahoma", 1, 12));
        hypDelete.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                hypDeleteActionPerformed(evt);
            }
        });
        jPanel5.add(hypDelete, new org.netbeans.lib.awtextra.AbsoluteConstraints(110, 10, 50, -1));

        hypNew.setMnemonic('B');
        hypNew.setText("Baru");
        hypNew.setFont(new java.awt.Font("Tahoma", 1, 12));
        hypNew.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                hypNewActionPerformed(evt);
            }
        });
        jPanel5.add(hypNew, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 10, 50, -1));

        hypEdit.setMnemonic('U');
        hypEdit.setText("Ubah");
        hypEdit.setFont(new java.awt.Font("Tahoma", 1, 12));
        hypEdit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                hypEditActionPerformed(evt);
            }
        });
        jPanel5.add(hypEdit, new org.netbeans.lib.awtextra.AbsoluteConstraints(60, 10, 50, -1));

        lblItemCount.setForeground(new java.awt.Color(0, 0, 102));
        lblItemCount.setText("1000 Items");
        jPanel5.add(lblItemCount, new org.netbeans.lib.awtextra.AbsoluteConstraints(440, 10, 90, 20));

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(10, 10, 10)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel5, javax.swing.GroupLayout.PREFERRED_SIZE, 540, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 540, Short.MAX_VALUE))
                .addGap(10, 10, 10)
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, 270, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(10, 10, 10))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(10, 10, 10)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jPanel5, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 454, Short.MAX_VALUE))
                    .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, 494, Short.MAX_VALUE))
                .addGap(11, 11, 11))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void formInternalFrameOpened(javax.swing.event.InternalFrameEvent evt) {//GEN-FIRST:event_formInternalFrameOpened
        SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    txtCari.requestFocus();
                }
          });
        udfFilter("");
    }//GEN-LAST:event_formInternalFrameOpened

    private void btnUpdateActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnUpdateActionPerformed
        udfSave();
    }//GEN-LAST:event_btnUpdateActionPerformed

    private void txtHarga1RKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtHarga1RKeyTyped
        fn.keyTyped(evt);
    }//GEN-LAST:event_txtHarga1RKeyTyped

    private void txtHarga2RKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtHarga2RKeyTyped
        fn.keyTyped(evt);
    }//GEN-LAST:event_txtHarga2RKeyTyped

    private void txtHarga1NRKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtHarga1NRKeyTyped
        fn.keyTyped(evt);
    }//GEN-LAST:event_txtHarga1NRKeyTyped

    private void txtHarga2NRKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtHarga2NRKeyTyped
        fn.keyTyped(evt);
    }//GEN-LAST:event_txtHarga2NRKeyTyped

    private void hypNewActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_hypNewActionPerformed
        udfNew();
    }//GEN-LAST:event_hypNewActionPerformed

    private void hypDeleteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_hypDeleteActionPerformed
        udfDelete();
    }//GEN-LAST:event_hypDeleteActionPerformed

    private void hypEditActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_hypEditActionPerformed
        udfUpdate();
    }//GEN-LAST:event_hypEditActionPerformed

    private void masterTableMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_masterTableMouseClicked
        if(evt.getClickCount()==2)
            udfUpdate();
    }//GEN-LAST:event_masterTableMouseClicked

    private void txtHargaPokokKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtHargaPokokKeyTyped
        // TODO add your handling code here:
    }//GEN-LAST:event_txtHargaPokokKeyTyped

    private void masterTableKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_masterTableKeyPressed
//        if(evt.getKeyCode()==KeyEvent)
//        masterTable.setFocusable(false);
//        txtHarga1G.requestFocusInWindow();
//        txtHarga1G.setFocusable(true);
//        txtHarga1G.requestFocus();
    }//GEN-LAST:event_masterTableKeyPressed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnUpdate;
    private org.jdesktop.swingx.JXHyperlink hypDelete;
    private org.jdesktop.swingx.JXHyperlink hypEdit;
    private org.jdesktop.swingx.JXHyperlink hypNew;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JSeparator jSeparator3;
    private javax.swing.JLabel lblItemCount;
    private javax.swing.JLabel lblKonv1NR;
    private javax.swing.JLabel lblKonv1R;
    private javax.swing.JLabel lblKonv2NR;
    private javax.swing.JLabel lblKonv2R;
    private javax.swing.JLabel lblSat1NR;
    private javax.swing.JLabel lblSat1R;
    private javax.swing.JLabel lblSat2NR;
    private javax.swing.JLabel lblSat2R;
    private javax.swing.JLabel lblSat3E1;
    private org.jdesktop.swingx.JXTable masterTable;
    private javax.swing.JTextField txtCari;
    private javax.swing.JTextField txtHarga1NR;
    private javax.swing.JTextField txtHarga1R;
    private javax.swing.JTextField txtHarga2NR;
    private javax.swing.JTextField txtHarga2R;
    private javax.swing.JTextField txtHargaPokok;
    // End of variables declaration//GEN-END:variables

    private void udfSave() {
        int iRow=masterTable.getSelectedRow();
        TableColumnModel col=masterTable.getColumnModel();
        String sItem=masterTable.getValueAt(iRow, col.getColumnIndex("Kode")).toString();
        try{
            conn.createStatement().executeUpdate(
                    "update barang set " +
                    "harga_kecil_resep="+fn.udfGetDouble(txtHarga1R.getText())+", " +
                    "harga_kecil_non_resep="+fn.udfGetDouble(txtHarga1NR.getText())+", " +
                    "harga_besar_resep="+fn.udfGetDouble(txtHarga2R.getText())+", " +
                    "harga_besar_non_resep="+fn.udfGetDouble(txtHarga2NR.getText())+", " +
                    "hpp= "+fn.udfGetDouble(txtHargaPokok.getText()) +" "+
                    "where item_code='"+sItem+"'; ");
                
            JOptionPane.showMessageDialog(this, "Simpan harga penjualan sukses!");
        }catch(SQLException se){
            JOptionPane.showMessageDialog(this, se.getMessage());
        }
    }

    private FocusListener txtFocusListener=new FocusListener() {
        public void focusGained(FocusEvent e) {
            if(e.getSource() instanceof JTextField || e.getSource() instanceof JFormattedTextField){
                ((JTextField)e.getSource()).setBackground(Color.YELLOW);
                if((e.getSource() instanceof JTextField ||(((JTextField)e.getSource()).getName()!=null && ((JTextField)e.getSource()).getName().equalsIgnoreCase("textEditor")))){
                    ((JTextField)e.getSource()).setSelectionStart(0);
                    ((JTextField)e.getSource()).setSelectionEnd(((JTextField)e.getSource()).getText().length());
                }
            }
        }


        public void focusLost(FocusEvent e) {
            if(e.getSource().getClass().getSimpleName().equalsIgnoreCase("JTextField")||
                    e.getSource().getClass().getSimpleName().equalsIgnoreCase("JFormattedTextField")){
                ((JTextField)e.getSource()).setBackground(Color.WHITE);
                if(!e.getSource().equals(txtCari))
                    ((JTextField)e.getSource()).setText(fn.intFmt.format(fn.udfGetDouble(((JTextField)e.getSource()).getText())));


           }
        }


    } ;

    public void setDesktopIcon(JDesktopImage jDesktopPane1) {
        this.desktop=jDesktopPane1;
    }

    public class MyKeyListener extends KeyAdapter {

        @Override
        public void keyReleased(KeyEvent e) {
            if(e.getSource().equals(txtCari))
                udfFilter("");
        }


        @Override
        public void keyTyped(java.awt.event.KeyEvent evt) {
          if (evt.getSource() instanceof JTextField &&
              ((JTextField)evt.getSource()).getName()!=null &&
              ((JTextField)evt.getSource()).getName().equalsIgnoreCase("textEditor")) {

              char c = evt.getKeyChar();
              if (!((c >= '0' && c <= '9')) &&
                    (c != KeyEvent.VK_BACK_SPACE) &&
                    (c != KeyEvent.VK_DELETE) &&
                    (c != KeyEvent.VK_ENTER) &&
                    (c != '-') &&
                    (c != '.')) {
                    getToolkit().beep();
                    evt.consume();
                    return;
              }
           }

        }
        public void keyPressed(KeyEvent evt) {
            Component ct = KeyboardFocusManager.getCurrentKeyboardFocusManager().getPermanentFocusOwner();
            int keyKode = evt.getKeyCode();
            switch(keyKode){
               case KeyEvent.VK_ENTER : {
                    if(!(ct instanceof JTable))                    {
                        if (!fn.isListVisible() && !evt.getSource().equals(txtCari)){
                            Component c = findNextFocus();
                            if (c==null) return;
                            c.requestFocus();
                        }else{
                            fn.lstRequestFocus();
                        }
                    }
                    break;
                }
                case KeyEvent.VK_DOWN: {
                    if(!(evt.getSource() instanceof JXTable)){
                        if(evt.getSource().equals(txtCari)){
                            masterTable.requestFocusInWindow();
                            if(masterTable.getSelectedRow()<0){
                                masterTable.setRowSelectionInterval(0, 0);
                                masterTable.changeSelection(0, 0, false, false);
                            }
                            break;
                        }
                        if (!fn.isListVisible()){
                            Component c = findNextFocus();
                            if (c==null) return;
                            c.requestFocus();
                        }else{
                            fn.lstRequestFocus();
                        }
                        break;
                    }
                }

                case KeyEvent.VK_UP: {
                    if(!(evt.getSource() instanceof JXTable)){
                        Component c = findPrevFocus();
                        c.requestFocus();
                    }
                    break;
                }
                case KeyEvent.VK_ESCAPE:{
                    if(JOptionPane.showConfirmDialog(null,"Anda Yakin Untuk Keluar?",
                            "Message",JOptionPane.YES_NO_OPTION)==JOptionPane.YES_OPTION){
                        dispose();
                    }
                    break;
                }
                case KeyEvent.VK_F2:{
                    udfSave();
                    break;
                }
                case KeyEvent.VK_F12:{
                    masterTable.setFocusable(false);
                    txtHarga1R.requestFocusInWindow();
                    txtHarga1R.setFocusable(true);
                    txtHarga1R.requestFocus();
                    break;
                }
                case KeyEvent.VK_F5:{
                    txtCari.setText("");
                    udfFilter("");
                    break;
                }
                case KeyEvent.VK_DELETE:{

                    break;

                }
            }
        }

//        @Override
//        public void keyReleased(KeyEvent evt){
//            if(evt.getSource().equals(txtDisc)||evt.getSource().equals(txtQty)||evt.getSource().equals(txtUnitPrice))
//                GeneralFunction.keyTyped(evt);
//        }

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
                return prevFocus;
            }
            return null;
        }
    }

    private void udfNew() {
        FrmItemMaster fMaster=new FrmItemMaster();
        fMaster.setTitle("Item baru");
        fMaster.setConnection(conn);
        fMaster.setKodeBarang("");
        fMaster.setBNew(true);
        fMaster.setObjForm(this);
        //fMaster.setSrcTable(masterTable);
//        fMaster.setSrcModel(myModel);
        fMaster.setBounds(0, 0, fMaster.getWidth(), fMaster.getHeight());
        fMaster.setVisible(true);
        
    }
        
    private void udfUpdate() {
        int iRow= masterTable.getSelectedRow();
        if(iRow>=0){
            FrmItemMaster fMaster=new FrmItemMaster();
            fMaster.setTitle("Update Item / Barang");
            //fMaster.settDesktopPane(jDesktop);
            fMaster.setConnection(conn);
            //fMaster.setSrcTable(masterTable);
            fMaster.setObjForm(this);
            fMaster.setBNew(false);
            fMaster.setKodeBarang(masterTable.getValueAt(iRow, masterTable.getColumnModel().getColumnIndex("Kode")).toString());
            fMaster.setBounds(0, 0, fMaster.getWidth(), fMaster.getHeight());
        }
    }

    private void udfDelete() {
        int iRow = masterTable.getSelectedRow();

        if(iRow>=0){
            if(JOptionPane.showConfirmDialog(this, "Anda yakin untuk menghapus data tersebut?", "Confirm", JOptionPane.YES_NO_OPTION)==
                    JOptionPane.YES_OPTION){
                    try {
                        int iDel = conn.createStatement().executeUpdate("Delete from r_item where kode_item='" + masterTable.getValueAt(iRow, 0).toString() + "'");

                        if(iDel>0){
                            JOptionPane.showMessageDialog(this, "Hapus data sukses!!!");
                            ((DefaultTableModel)masterTable.getModel()).removeRow(iRow);

                            if(iRow>=masterTable.getRowCount() && ((DefaultTableModel)masterTable.getModel()).getRowCount()>0){
                                masterTable.setRowSelectionInterval(iRow-1, iRow-1);
    //                        }else if(iRow>myModel.getRowCount()){
    //                            masterTable.setRowSelectionInterval(iRow-1, iRow-1);
                            }else if(((DefaultTableModel)masterTable.getModel()).getRowCount()>0){
                                masterTable.setRowSelectionInterval(iRow, iRow);
                            }
                        }else{
                            JOptionPane.showMessageDialog(this, "Hapus data gagal!!!");
                        }
                    } catch (SQLException ex) {
                        //Logger.getLogger(AgamaList.class.getName()).log(Level.SEVERE, null, ex);
                        JOptionPane.showMessageDialog(this, "Hapus data gagal");
                    }

            }
        }
    }
}
