/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * Trx2.java
 *
 * Created on Dec 28, 2010, 10:36:17 AM
 */

package apotek;

import main.MainForm;
import penjualan.FrmReturPenjualanHistory;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.FocusTraversalPolicy;
import java.awt.KeyboardFocusManager;
import java.awt.Toolkit;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.print.PrinterJob;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.print.DocFlavor;
import javax.print.PrintService;
import javax.print.PrintServiceLookup;
import javax.print.attribute.HashPrintRequestAttributeSet;
import javax.print.attribute.HashPrintServiceAttributeSet;
import javax.print.attribute.PrintRequestAttributeSet;
import javax.print.attribute.PrintServiceAttributeSet;
import javax.swing.AbstractCellEditor;
import javax.swing.BorderFactory;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TableColumnModelEvent;
import javax.swing.event.TableColumnModelListener;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.plaf.BorderUIResource;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableModel;
import javax.swing.text.MaskFormatter;
import main.GeneralFunction;
import main.SysConfig;
import org.jdesktop.swingx.autocomplete.AutoCompleteDecorator;
import org.jdesktop.swingx.autocomplete.ComboBoxCellEditor;

/**
 *
 * @author ustadho
 */
public class TrxReturPenjualan extends javax.swing.JFrame {
    private DefaultTableModel tableModel;
    private Connection conn;
    private JComboBox cmbSatuan=new JComboBox();
    private Component aThis;
    private MyKeyListener kListener=new MyKeyListener();
    private GeneralFunction fn=new GeneralFunction();
    private DlgLookupItemJual lookupItem =new DlgLookupItemJual(this, true);
    ArrayList lstGudang=new ArrayList();
    MyTableCellEditor cEditor=new MyTableCellEditor();
    private boolean isKoreksi=false;
    private String sNoRetur;
    private String tglSkg;
    private Object ObjForm;

    /** Creates new form Trx2 */
    public TrxReturPenjualan() {
        initComponents();
        //this.setExtendedState(MAXIMIZED_BOTH);
        setIconImage(MainForm.imageIcon);
        //initConn();
        tblItem.getTableHeader().setFont(tblItem.getFont());
        tblItem.setRowHeight(22);
        tblItem.addKeyListener(kListener);
        fn.addKeyListenerInContainer(jToolBar1, kListener, txtFocusListener);
        fn.addKeyListenerInContainer(jPanel1, kListener, txtFocusListener);
        AutoCompleteDecorator.decorate(cmbSatuan);
        tblItem.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).
        put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0),"selectNextColumnCell");

        tableModel=((DefaultTableModel)tblItem.getModel());
        tableModel.addTableModelListener(new InteractiveTableModelListener());

        tblItem.setSurrendersFocusOnKeystroke(true);
        if (!hasEmptyRow()) {
             addEmptyRow();
        }
        tblItem.getColumn("ProductID").setPreferredWidth(130);
        tblItem.getColumn("Nama Barang").setPreferredWidth(300);
        tblItem.getColumn("Sub Total").setPreferredWidth(120);
        tblItem.getColumn("Sub Total").setCellRenderer(new InteractiveRenderer(5));
        tblItem.getColumn("Konv").setCellRenderer(new InteractiveRenderer(6));

        tblItem.getColumn("Satuan").setCellEditor(new ComboBoxCellEditor(cmbSatuan));
        
        tblItem.getColumn("ProductID").setCellEditor(cEditor);
        tblItem.getColumn("Qty").setCellEditor(cEditor);
        tblItem.getColumn("Harga").setCellEditor(cEditor);

        tblItem.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            public void valueChanged(ListSelectionEvent e) {
                int iRow=tblItem.getSelectedRow();
                udfLoadComboKonv(iRow);
            }
        });
        aThis=this;
        cmbSatuan.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                if(cmbSatuan.getSelectedIndex()>0 && conn!=null)
                    udfLoadKonversi(cmbSatuan.getSelectedItem().toString());

            }
        });

        cmbSatuan.setFont(tblItem.getFont());
//        tblItem.getColumnModel().addColumnModelListener(new TableColumnModelListener() {
//            public void columnAdded(TableColumnModelEvent e) {
//                //throw new UnsupportedOperationException("Not supported yet.");
//            }
//
//            public void columnRemoved(TableColumnModelEvent e) {
//                //throw new UnsupportedOperationException("Not supported yet.");
//            }
//
//            public void columnMoved(TableColumnModelEvent e) {
//                
//            }
//
//            public void columnMarginChanged(ChangeEvent e) {
//                //throw new UnsupportedOperationException("Not supported yet.");
//            }
//
//            public void columnSelectionChanged(ListSelectionEvent e) {
//                
//                if(tblItem.getSelectedColumn()==1)
//                    tblItem.setColumnSelectionInterval(2, 2);
//            }
//        });

    }

    public void setObjForm(Object b){
        this.ObjForm=b;
    }

    private void udfLoadComboKonv(int iRow){
        
        if(iRow<0) return;
        if(tblItem.getValueAt(iRow, 0)==null ||tblItem.getValueAt(iRow, 0).toString().equalsIgnoreCase(""))
            return;

        try{
            cmbSatuan.removeAllItems();
            ResultSet rs=conn.createStatement().executeQuery("select coalesce(item_name,'') as nama_item, " +
                         "coalesce(satuan_kecil,'') as unit, " +
                         "coalesce(satuan_besar,'') as unit2, coalesce(konversi,1) as konv " +
                         "from barang where item_code='"+tblItem.getValueAt(iRow, 0).toString()+"'");
             if(rs.next()){
                 if(rs.getString("unit").length()>0) cmbSatuan.addItem(rs.getString("unit"));
                 if(rs.getString("unit2").length()>0) cmbSatuan.addItem(rs.getString("unit2"));
             }else{

             }
             rs.close();
        }catch(SQLException se){
            JOptionPane.showMessageDialog(aThis, se.getMessage());
        }
    }

    public void setFlagKoreksi(boolean b){
        this.isKoreksi=b;
    }

    private void initConn(){
        String url = "jdbc:postgresql://localhost/KopSiloam";
        try{
            Class.forName("org.postgresql.Driver");
            conn = DriverManager.getConnection(url,"tadho","ustasoft");

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage());
        } catch(ClassNotFoundException ce) {
            System.out.println(ce.getMessage());
        }
    }

    public void setConn(Connection conn) {
        this.conn=conn;
    }

    private void udfInitForm() {
        lookupItem.setConn(conn);
        fn.setConn(conn);
        cmbGudang.setSelectedItem(MainForm.sNamaGudang);
        MaskFormatter fmttgl = null;
        try {
            fmttgl = new MaskFormatter("##/##/####");
        } catch (java.text.ParseException e) {}

        JFormattedTextField jFDate1 = new JFormattedTextField(fmttgl);
//        jFJtTempo.setFormatterFactory(jFDate1.getFormatterFactory());

        try{
            ResultSet rs=conn.createStatement().executeQuery("select kode_gudang, coalesce(deskripsi,'') as nama_gudang, " +
                    "to_char(current_date, 'dd/MM/yyyy') as tgl " +
                    "from gudang order by 1");
            lstGudang.clear();
            cmbGudang.removeAllItems();
            
            while(rs.next()){
                lstGudang.add(rs.getString(1));
                cmbGudang.addItem(rs.getString(2));
//                jFJtTempo.setText(rs.getString("tgl"));
//                jFJtTempo.setValue(rs.getString("tgl"));
                txtDate.setText(rs.getString("tgl"));
                this.tglSkg=rs.getString("tgl");
            }
            rs.close();

        }catch(SQLException se){
            JOptionPane.showMessageDialog(this, se.getMessage());
        }
        if(isKoreksi)
            udfLoadKoreksiRetur();
        else
            udfNew();
        
        txtReturnNo.setEnabled(isKoreksi);
        txtNoTrx.setEnabled(!isKoreksi);
        txtCustomer.setEnabled(!isKoreksi);

        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                if(isKoreksi)
                    txtReturnNo.requestFocusInWindow();
                else
                    txtNoTrx.requestFocusInWindow();
            }
        });
    }

    private void udfNew() {
        cmbJenisRetur.setSelectedIndex(1);
        cmbCaraBayar.setSelectedIndex(1);
        //jLabel3.setVisible(false); jFJtTempo.setVisible(false);
        txtDate.setText(tglSkg);
        txtNoTrx.setText(""); txtReturnNo.setText("");
        txtReturnNo.setEnabled(isKoreksi);
        txtCustomer.setText(""); txtCustomer.setText("");
        ((DefaultTableModel)tblItem.getModel()).setNumRows(0);
        lblTotal.setText("0");
        //btnCancel.setIcon(new javax.swing.ImageIcon(getClass().getResource("/retail/image/Icon/Cancel.png"))); // NOI18N
        btnCancel.setText("Cancel");
        btnNew.setEnabled(false);   btnPrint.setEnabled(false);
        btnSave.setEnabled(true);
        //addEmptyRow();

    }

    private void udfLoadKoreksiRetur(){
        String sQry="select r.no_retur, to_Char(r.tanggal, 'dd/MM/yyyy') as tanggal, coalesce(r.sales_no,'') as sales_no, " +
                "to_char(s.tanggal,'dd/MM/yyyy') as sales_date," +
                "coalesce(r.kode_cust,'') as kode_cust, coalesce(c.nama_pasien,'') as nama_cust, coalesce(c.alamat,'') as alamat ," +
                "coalesce(g.deskripsi,'') as nama_gudang," +
                "coalesce(r.keterangan,'') as keterangan, case when r.tunai_kredit='T' then 'TUNAI' else 'KREDIT' end as tunai_kredit, " +
                "case when r.jenis_retur='P' then 'Potong Tagihan' else 'Tukar Barang' end as jenis_retur, " +
                "coalesce(r.potongan,0) as potongan " +
                "from retur_jual r " +
                "inner join penjualan s on s.no_penjualan=r.sales_no " +
                "left join customers c on c.kode_customers=r.kode_cust " +
                "left join gudang g on g.kode_gudang=r.kode_gudang " +
                "where upper(r.no_retur)='"+txtReturnNo.getText().toUpperCase()+"'";
        try{
            ResultSet rs=conn.createStatement().executeQuery(sQry);
            if(rs.next()){
                txtReturnNo.setText(rs.getString("no_retur"));
                txtDate.setText(rs.getString("tanggal"));
                txtNoTrx.setText(rs.getString("sales_no"));
                txtCustomer.setText(rs.getString("kode_cust"));
                txtNamaCustomer.setText(rs.getString("nama_cust"));
                cmbJenisRetur.setSelectedItem(rs.getString("jenis_retur"));
                lblTglJual.setText(rs.getString("sales_date"));
                cmbGudang.setSelectedItem(rs.getString("nama_gudang"));
                txtCatatan.setText(rs.getString("keterangan"));
                cmbCaraBayar.setSelectedItem(rs.getString("tunai_kredit"));
                txtPot.setText(fn.dFmt.format(rs.getDouble("potongan")));

                rs.close();
                ((DefaultTableModel)tblItem.getModel()).setNumRows(0);
                
                sQry="select d.kode_item, coalesce(i.item_name,'') as nama_item, coalesce(d.qty,0) as qty, " +
                        "coalesce(d.unit,'') as unit, coalesce(d.unit_price,0) as unit_price, " +
                        "coalesce(d.qty,0) * coalesce(d.unit_price,0) as sub_Total, " +
                        "coalesce(konv,0) as konv, coalesce(x.sisa,0) as sisa " +
                        "from retur_jual_detail d " +
                        "inner join barang i on i.item_code=d.kode_item  " +
                        "left join(select * from fn_retur_jual_item_sisa('"+txtNoTrx.getText()+"') as " +
                        "   (kode_item varchar, nama_item varchar, " +
                        "   unit_price double precision, sisa double precision, unit varchar)" +
                        ")x on x.kode_item=d.kode_item " +
                        "where d.no_retur='"+txtReturnNo.getText()+"'";

                ((DefaultTableModel)tblItem.getModel()).setNumRows(0);
                rs=conn.createStatement().executeQuery(sQry);
                while(rs.next()){
                    ((DefaultTableModel)tblItem.getModel()).addRow(new Object[]{
                        rs.getString("kode_item"),
                        rs.getString("nama_item"),
                        rs.getDouble("qty"),
                        rs.getString("unit"),
                        rs.getDouble("unit_price"),
                        rs.getDouble("sub_Total"),
                        rs.getDouble("konv"),
                        rs.getDouble("sisa")+rs.getDouble("qty"),
                    });
                }
                addEmptyRow();
                if(tblItem.getRowCount()>0)
                    tblItem.setRowSelectionInterval(0, 0);

            }else{
                JOptionPane.showMessageDialog(this, "No. Penjualan tidak ditemukan!");
                udfNew();
                txtNoTrx.requestFocus();
            }
            rs.close();
        }catch(SQLException se){
            JOptionPane.showMessageDialog(this, se.getMessage());
        }

    }

    private void udfLoadPenjualan(){
        try{
            tblItem.requestFocusInWindow();
            String s="select distinct s.no_penjualan, coalesce(s.kode_pelanggan,'') as kode_cust, " +
                    "coalesce(c.nama_pelanggan, s.nama_pelanggan) as nama_cust, to_char(s.tanggal, 'dd/MM/yyyy') as tgl_jual," +
                    "coalesce(g.deskripsi,'') as nama_gudang, coalesce(s.koreksi, false) as koreksi " +
                    "from penjualan s " +
                    "inner join penjualan_detail d on d.no_penjualan=s.no_penjualan " +
                    "left join pelanggan c on c.kode_pelanggan=s.kode_pelanggan " +
                    "left join gudang g on g.kode_gudang=d.kode_gudang " +
                    "where s.no_penjualan='"+txtNoTrx.getText()+"'";

            ResultSet rs=conn.createStatement().executeQuery(s);
            if(rs.next()){
                if(rs.getBoolean("koreksi")){
                    JOptionPane.showMessageDialog(this, "Nomor transaksi tersebut tidak bisa diretur karena sudah dikoreksi!");
                    udfNew();
                    txtNoTrx.requestFocusInWindow();
                    return;
                }
                
                txtCustomer.setText(rs.getString("kode_cust"));
                txtNamaCustomer.setText(rs.getString("nama_cust"));
                lblTglJual.setText(rs.getString("tgl_jual"));
                cmbGudang.setSelectedItem(rs.getString("nama_gudang"));
                txtCatatan.setText("");
                
            }else{
                JOptionPane.showMessageDialog(this, "No. Penjualan tidak ditemukan!");
                udfNew();
                if(!txtNoTrx.isFocusOwner())
                    txtNoTrx.requestFocus();
            }

            rs.close();
        }catch(SQLException se){
            JOptionPane.showMessageDialog(this, se.getMessage());
        }
    }

    public void setNoTrx(String toString) {
        txtNoTrx.setText(toString);
    }

    public void setNoRetur(String sNoRetur) {
        this.sNoRetur=sNoRetur;
        this.txtReturnNo.setText(sNoRetur);
    }

    public class InteractiveTableModelListener implements TableModelListener {
         public void tableChanged(TableModelEvent evt) {
             if (evt.getType() == TableModelEvent.UPDATE || evt.getType() == TableModelEvent.INSERT) {
                 int column = evt.getColumn();
                 int row = evt.getFirstRow();
                 System.out.println("row: " + row + " column: " + column);
                 //table.setColumnSelectionInterval(column + (column==0? 2: 1), column + (column==0? 2: 1));

                 if(column<tblItem.getColumnCount()-1)
                    tblItem.setColumnSelectionInterval(column + 1, column + 1);

                 try{
                    if(column==tblItem.getColumnModel().getColumnIndex("ProductID") ){
                        if(tblItem.getValueAt(row, column).toString().length()>0){
                            String sQry="select * from fn_retur_jual_item_sisa('"+txtNoTrx.getText()+"') as " +
                                    "(kode_item varchar, nama_item varchar, " + 
                                    "unit_price double precision, sisa double precision, unit varchar) " +
                                    "where kode_item='"+tblItem.getValueAt(row, column).toString()+"' " +
                                    "";
                             //System.out.println(sQry);
                             ResultSet rs=conn.createStatement().executeQuery(sQry);
                             cmbSatuan.removeAllItems();
                             if(rs.next()){
                                 tblItem.setValueAt(rs.getString("nama_item"), row, tblItem.getColumnModel().getColumnIndex("Nama Barang"));
//                                 if(rs.getString("unit").length()>0) cmbSatuan.addItem(rs.getString("unit"));
//                                 if(rs.getString("unit2").length()>0) cmbSatuan.addItem(rs.getString("unit2"));
                                 tblItem.setValueAt(rs.getString("unit"), row, tblItem.getColumnModel().getColumnIndex("Satuan"));
                                 tblItem.setValueAt(1, row, tblItem.getColumnModel().getColumnIndex("Konv"));
                                 tblItem.setValueAt(rs.getDouble("unit_price"), row, tblItem.getColumnModel().getColumnIndex("Harga"));
                                 tblItem.setValueAt(rs.getDouble("sisa"), row, tblItem.getColumnModel().getColumnIndex("Sisa"));
                                 tblItem.setColumnSelectionInterval(2, 2);
                                 
                                 //udfLoadComboKonv(row);

                             }else{
                                 JOptionPane.showMessageDialog(aThis, "Item tidak ditemukan!");
                                 
                             }
                             rs.close();
                        }else{
                            //tableModel.removeRow(row);
                            udfClearRow(row);
                        }
                    }else if(column==tblItem.getColumnModel().getColumnIndex("Satuan")){
                        //udfLoadKonversi(tblItem.getValueAt(row, column).toString());
                    }else if(column==tblItem.getColumnModel().getColumnIndex("Qty")||column==tblItem.getColumnModel().getColumnIndex("Harga")){
                        tblItem.setValueAt(
                                fn.udfGetDouble(tblItem.getValueAt(row, tblItem.getColumnModel().getColumnIndex("Qty")))*
                                fn.udfGetDouble(tblItem.getValueAt(row, tblItem.getColumnModel().getColumnIndex("Harga"))),
                                row, tblItem.getColumnModel().getColumnIndex("Sub Total"));
                    }
                    udfSetTotalAmount();

                 }catch(SQLException se){
                        JOptionPane.showMessageDialog(null, se.getMessage());
                     }
                 tblItem.setRowSelectionInterval(row, row);
             }
         }


     }
    
    private void udfSetTotalAmount(){
        double dTotal=0;
        for(int i=0; i<tblItem.getRowCount(); i++){
            if(tblItem.getValueAt(i, tblItem.getColumnModel().getColumnIndex("Sub Total"))!=null)
                dTotal+=fn.udfGetDouble(tblItem.getValueAt(i, tblItem.getColumnModel().getColumnIndex("Sub Total")));
        }
        dTotal=chkPersen.isSelected()? dTotal*(1-fn.udfGetDouble(txtPot.getText())/100): 
                dTotal-fn.udfGetDouble(txtPot.getText());

        lblTotal.setText(fn.dFmt.format(dTotal));
    }

    private void udfClearRow(int row){
        tblItem.setValueAt("", row, 0);
        tblItem.setValueAt("", row, 1);
        tblItem.setValueAt("1", row, 2);
        tblItem.setValueAt("", row, 3);
        tblItem.setValueAt(0, row, 4);
        tblItem.setValueAt(0, row, 5);
        tblItem.setValueAt(1, row, 6);

    }

    private void udfLoadKonversi(String sUnit) {
        int row=tblItem.getSelectedRow();
        if(row<0) return;
        try {
            String sCustType=cmbJenisRetur.getSelectedItem().toString().substring(0, 1);
            String sQry = "select case  when '" + sUnit + "'=i.satuan_kecil then 1 " +
                          "             when '" + sUnit + "'=i.satuan_besar then coalesce(konversi,1) " +
                          "             else 1 end as konv, " +
                          "coalesce(x.unit_price,0) as harga " +
                          "from (" +
                          "    select * from fn_retur_jual_item_sisa('"+txtNoTrx.getText()+"') as (kode_item varchar, nama_item varchar, " +
                          "    unit_price double precision, sisa double precision, unit varchar)" +
                          ")x inner join barang i on i.item_code=x.kode_item " +
                          "where i.item_code='" + tblItem.getValueAt(row, 0).toString() + "'";

            //System.out.println(sQry);
            ResultSet rs = conn.createStatement().executeQuery(sQry);
            if (rs.next()) {
                tblItem.setValueAt(rs.getDouble("harga"), row, tblItem.getColumnModel().getColumnIndex("Harga"));
                tblItem.setValueAt(rs.getInt("konv"), row, tblItem.getColumnModel().getColumnIndex("Konv"));
            } else {
                tblItem.setValueAt(0, row, tblItem.getColumnModel().getColumnIndex("Harga"));
                tblItem.setValueAt(1, row, tblItem.getColumnModel().getColumnIndex("Konv"));
            }
            rs.close();
        } catch (SQLException ex) {
            Logger.getLogger(TrxReturPenjualan.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    class InteractiveRenderer extends DefaultTableCellRenderer {
         protected int interactiveColumn;

         public InteractiveRenderer(int interactiveColumn) {
             this.interactiveColumn = interactiveColumn;
         }

         public Component getTableCellRendererComponent(JTable table,
            Object value, boolean isSelected, boolean hasFocus, int row,
            int column){
             Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
             //if (column == interactiveColumn && hasFocus ) {
             //if (column == interactiveColumn && hasFocus ) {
             if (column >=interactiveColumn && hasFocus ) {
                 if ((tableModel.getRowCount() - 1) == row &&
                    !hasEmptyRow()){
                     addEmptyRow();
                 }

                 highlightLastRow(row);
             }
             setHorizontalAlignment(JLabel.RIGHT);
             if(value instanceof Double ||value instanceof Integer ||value instanceof Float  ){
                setHorizontalAlignment(SwingConstants.RIGHT);
                value=fn.dFmt.format(value);
            }

            setValue(value);
             return c;
         }
     }

    public void highlightLastRow(int row) {
         int lastrow = tableModel.getRowCount();
         if (row == lastrow - 1) {
             tblItem.setRowSelectionInterval(lastrow - 1, lastrow - 1);
         } else {
             tblItem.setRowSelectionInterval(row + 1, row + 1);
         }

         tblItem.setColumnSelectionInterval(0, 0);
     }

    public boolean hasEmptyRow() {
         if (tableModel.getRowCount() == 0) return false;
         int row=tableModel.getRowCount()-1;

         if((tableModel.getValueAt(row, 0)==null || tableModel.getValueAt(row, 0).toString().trim().equals("")) &&
            (tableModel.getValueAt(row, 1)==null || tableModel.getValueAt(row, 1).toString().trim().equals(""))
            //(tableModel.getValueAt(row, 3)==null || tableModel.getValueAt(row, 3).toString().trim().equals(""))
            ){
            return true;
         }
         else return false;
     }


    public void addEmptyRow() {
         tableModel.addRow(new Object[]{"", "", 1, "",0, 0, 1});
         tableModel.fireTableRowsInserted(tableModel.getRowCount() - 1,tableModel.getRowCount() - 1);
         tblItem.requestFocusInWindow();
         tblItem.setRowSelectionInterval(tblItem.getRowCount()-1, tblItem.getRowCount()-1);
         tblItem.changeSelection(tableModel.getRowCount()-1, 0, false, false);

     }

    JTextField ustTextField = new JTextField() {
        protected boolean processKeyBinding(final KeyStroke ks, final KeyEvent e, final int condition, final boolean pressed) {
            if (hasFocus()) {
                return super.processKeyBinding(ks, e, condition, pressed);
            } else {
                this.requestFocus();
                SwingUtilities.invokeLater(new Runnable() {
                    public void run() {
                        processKeyBinding(ks, e, condition, pressed);
                    }
              });
                return true;
            }
        }
    };
     public class MyTableCellEditor extends AbstractCellEditor implements TableCellEditor {
        private Toolkit toolkit;
        JTextField text= ustTextField;

        int col, row;
        public Component getTableCellEditorComponent(JTable tblDetail, Object value,
            boolean isSelected, int rowIndex, int vColIndex) {
            // 'value' is value contained in the cell located at (rowIndex, vColIndex)
            row=rowIndex;
            col=vColIndex;
            text=ustTextField;
            text.setName("textEditor");

            text.addKeyListener(kListener);

           text.setBackground(new Color(0,255,204));
           text.addFocusListener(txtFocusListener);
           text.setFont(tblDetail.getFont());
           text.setVisible(!lookupItem.isVisible());
            if(lookupItem.isVisible()){
                return null;
            }
            text.setText(value==null? "": value.toString());

            if(value instanceof Double || value instanceof Float|| value instanceof Integer){
               text.setText(fn.dFmt.format(value));
            }else
                text.setText(value==null? "":value.toString());
           return text;
        }

        public Object getCellEditorValue() {
            Object o="";//=component.getText();
            Object retVal = 0;
            try {
                if(tblItem.getSelectedColumn()==0){
                    retVal = ((JTextField)text).getText();
                }else if(tblItem.getSelectedColumn()==tblItem.getColumnModel().getColumnIndex("Qty")){
                    retVal = fn.udfGetDouble(((JTextField)text).getText());
                    if(fn.udfGetDouble(((JTextField)text).getText())>fn.udfGetDouble(tblItem.getValueAt(row, tblItem.getColumnModel().getColumnIndex("Sisa")))){
                        JOptionPane.showMessageDialog(aThis, "Jumlah yang diretur melebihi Sisa transaksi!\n" +
                                "Sisa transaksi adalah "+fn.udfGetDouble(tblItem.getValueAt(row, tblItem.getColumnModel().getColumnIndex("Sisa"))));
                        retVal = fn.udfGetDouble(tblItem.getValueAt(row, tblItem.getColumnModel().getColumnIndex("Sisa")));
                    }
                }else
                    retVal = fn.udfGetDouble(((JTextField)text).getText());
                return retVal;
            } catch (Exception e) {
                toolkit.beep();
                retVal=0;
            }
            return retVal;
        }

        public boolean isVisible(){
            return text.isVisible();
        }

        private void setValue(String toString) {
            text.setText(toString);
        }
    }

     private FocusListener txtFocusListener=new FocusListener() {
        public void focusGained(FocusEvent e) {
            if(e.getSource() instanceof JTextField || e.getSource() instanceof JFormattedTextField){
                ((JTextField)e.getSource()).setBackground(Color.YELLOW);
                if((e.getSource() instanceof JTextField && ((JTextField)e.getSource()).getName()!=null && ((JTextField)e.getSource()).getName().equalsIgnoreCase("textEditor"))
                        ){
                    ((JTextField)e.getSource()).setSelectionStart(0);
                    ((JTextField)e.getSource()).setSelectionEnd(((JTextField)e.getSource()).getText().length());
                }
//                else if(e.getSource().equals(txtKelas) && !fn.isListVisible()){
//                    sOldKelas=txtKelas.getText();
//                }
            }
        }

        public void focusLost(FocusEvent e) {
            if(e.getSource() instanceof  JTextField  || e.getSource() instanceof  JFormattedTextField){
                ((JTextField)e.getSource()).setBackground(Color.WHITE);
                if(e.getSource().equals(txtReturnNo) && aThis.isShowing() && aThis.isFocusable() && txtNoTrx.getText().trim().length()>0)
                    udfLoadKoreksiRetur();
                else if(e.getSource().equals(txtNoTrx) && aThis.isShowing() && aThis.isFocusable() && txtNoTrx.getText().trim().length()>0)
                    udfLoadPenjualan();
           }
        }
    } ;

    public class MyKeyListener extends KeyAdapter {
        @Override
        public void keyReleased(KeyEvent evt){

        }

        @Override
        public void keyTyped(KeyEvent evt){
//            if(evt.getSource().equals(txtNamaPasien) && txtNoReg.getText().length()>0)
//                evt.consume();
        }

        @Override
        public void keyPressed(KeyEvent evt) {
            Component ct = KeyboardFocusManager.getCurrentKeyboardFocusManager().getPermanentFocusOwner();
            int keyKode = evt.getKeyCode();
            switch(keyKode){
                case KeyEvent.VK_INSERT:{
                    
                    if(cmbJenisRetur.getSelectedIndex()<0){
                        JOptionPane.showMessageDialog(aThis, "Pilih harga penjualan terlebih dulu!");
                        cmbJenisRetur.requestFocus();
                        return;
                    }
                    if(tblItem.getCellEditor()!=null && evt.getSource().equals(tblItem))
                        tblItem.getCellEditor().stopCellEditing();

                    udfLookupItemRetur();
                    break;
                }
                case KeyEvent.VK_F4:{
                    udfNew();
                    break;
                }
                case KeyEvent.VK_F5:{
                    udfSave();
                    break;
                }
                case KeyEvent.VK_F9:{
//                    if(tblDetail.getRowCount()==0) return;
//                    ((DefaultTableModel)tblHeader.getModel()).addRow(new Object[]{
//                        tblHeader.getRowCount()+1, "T", 0
//                    });
//                    tblHeader.requestFocusInWindow();
//                    tblHeader.requestFocus();
//                    tblHeader.setRowSelectionInterval(tblHeader.getRowCount()-1, tblHeader.getRowCount()-1);
//                    tblHeader.changeSelection(tblHeader.getRowCount()-1, 1, false, false);
                    break;
                }
                case KeyEvent.VK_ENTER : {
                    if(!(ct instanceof JTable)){
                        if (!fn.isListVisible()){
//                            if(evt.getSource() instanceof JTextField && ((JTextField)evt.getSource()).getText()!=null
//                               && ((JTextField)evt.getSource()).getName().equalsIgnoreCase("textEditor")){
//                                if(table.getSelectedColumn()==0){
//                                    //table.setValueAt(((JTextField)evt.getSource()).getText(), table.getSelectedRow(), 0);
//                                    //table.changeSelection(table.getSelectedRow(), 2, false, false);
//                                    //table.setColumnSelectionInterval(2, 2);
//                                }
//                            }

                            Component c = findNextFocus();
                            if (c==null) return;
                            if(c.isEnabled())
                                c.requestFocus();
                            else{
                                c = findNextFocus();
                                if (c!=null) c.requestFocus();;
                            }
                        }else{
                            fn.lstRequestFocus();
                        }
                    }else{
                        if(tblItem.getSelectedColumn()==tblItem.getColumnModel().getColumnIndex("Konv") && !hasEmptyRow()){
                            addEmptyRow();
                            tblItem.requestFocusInWindow();

                        }
                    }
                    break;
                }
                case KeyEvent.VK_DOWN: {
                    if(ct instanceof JTable){
//                        if(((JTable)ct).getSelectedRow()==0){
////                            Component c = findNextFocus();
////                            if (c==null) return;
////                            if(c.isEnabled())
////                                c.requestFocus();
////                            else{
////                                c = findNextFocus();
////                                if (c!=null) c.requestFocus();;
////                            }
//                        }
                    }else{
                        if (!fn.isListVisible()){
                            Component c = findNextFocus();
                            if (c==null) return;
                            if(c.isEnabled())
                                c.requestFocus();
                            else{
                                c = findNextFocus();
                                if (c!=null) c.requestFocus();;
                            }
                        }else{
                            fn.lstRequestFocus();
                        }
                        break;
                    }
                }

                case KeyEvent.VK_UP: {
                    if(ct instanceof JTable){
                        if(((JTable)ct).getSelectedRow()==0){
                            Component c = findPrevFocus();
                            if (c==null) return;
                            if(c.isEnabled())
                                c.requestFocus();
                            else{
                                c = findPrevFocus();
                                if (c!=null) c.requestFocus();;
                            }
                        }
                    }else{
                        Component c = findPrevFocus();
                        if (c==null) return;
                        if(c.isEnabled())
                            c.requestFocus();
                        else{
//                            c = findPreFocus();
//                            if (c!=null) c.requestFocus();;
                        }
                    }
                    break;
                }
                case KeyEvent.VK_LEFT:{
                    if(tblItem.getSelectedColumn()==2)
                        tblItem.setColumnSelectionInterval(0, 0);
                    break;
                }
                case KeyEvent.VK_DELETE:{
                    if(evt.getSource().equals(tblItem) && tblItem.getSelectedRow()>=0){
                        if(tblItem.getCellEditor()!=null)
                            tblItem.getCellEditor().stopCellEditing();
                        
                        int iRow[]= tblItem.getSelectedRows();
                        int rowPalingAtas=iRow[0];

                        TableModel tm= tblItem.getModel();

                        while(iRow.length>0) {
                            //JOptionPane.showMessageDialog(null, iRow[0]);
                            ((DefaultTableModel)tm).removeRow(tblItem.convertRowIndexToModel(iRow[0]));
                            iRow = tblItem.getSelectedRows();
                        }
                        tblItem.clearSelection();

                        if(tblItem.getRowCount()>0 && rowPalingAtas<tblItem.getRowCount()){
                            tblItem.setRowSelectionInterval(rowPalingAtas, rowPalingAtas);
                        }else{
                            if(tblItem.getRowCount()>0)
                                tblItem.setRowSelectionInterval(rowPalingAtas-1, rowPalingAtas-1);
                            else
                                tblItem.requestFocus();
                        }
                        if(tblItem.getSelectedRow()>=0){
                            tblItem.changeSelection(tblItem.getSelectedRow(), 0, false, false);
                            cEditor.setValue(tblItem.getValueAt(tblItem.getSelectedRow(), 0).toString());
                        }
                    }
                    break;
                }
                case KeyEvent.VK_ESCAPE:{
                    if(!cEditor.isVisible() && JOptionPane.showConfirmDialog(null,"Anda Yakin Untuk Keluar?",
                            "Ustasoft",JOptionPane.YES_NO_OPTION)==JOptionPane.YES_OPTION){
                        dispose();
                    }
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

    private void udfLookupItemRetur(){
        String sItem="";
        for(int i=0; i< tblItem.getRowCount(); i++){
            sItem+=(sItem.length()==0? "" : ",") +"'"+tblItem.getValueAt(i, 0).toString()+"'";
        }
        String sQry="select * from(select * from fn_retur_jual_item_sisa('"+txtNoTrx.getText()+"') as (kode_item varchar, nama_item varchar, " +
                "unit_price double precision, sisa double precision, unit varchar) " +
                (sItem.length()>0? " where  kode_item not in("+sItem+") " : "")+
                ")x ";
        DLgLookup d1=new DLgLookup(this, true);
        d1.udfLoad(conn, sQry, "(kode_item||nama_item)", tblItem);

        d1.setVisible(true);
        if(d1.getKode().length()>0) {
            tblItem.requestFocusInWindow();
            addEmptyRow();
            tblItem.setRowSelectionInterval(tblItem.getRowCount()-1, tblItem.getRowCount()-1);
            tblItem.setValueAt(d1.getKode(), tblItem.getSelectedRow(), 0);
            tblItem.setValueAt(d1.getTable().getValueAt(d1.getTable().getSelectedRow(), d1.getTable().getColumnModel().getColumnIndex("sisa")), tblItem.getSelectedRow(), tblItem.getColumnModel().getColumnIndex("Sisa"));
            tblItem.changeSelection(tblItem.getSelectedRow(), tblItem.getColumnModel().getColumnIndex("Qty"), false, false);
        }

//        if(lookupItem.getKodeBarang().length()>0){
//            if(tblItem.getRowCount()==0 || (tblItem.getValueAt(tblItem.getRowCount()-1, 0)!=null && !tblItem.getValueAt(tblItem.getRowCount()-1, 0).toString().trim().equalsIgnoreCase("")) )
//                addEmptyRow();
//            tblItem.requestFocusInWindow();
//            tblItem.setValueAt(lookupItem.getKodeBarang(), tblItem.getRowCount()-1, 0);
//            tblItem.changeSelection(tblItem.getRowCount()-1, tblItem.getColumnModel().getColumnIndex("Qty"), false, false);
//            }
    }

    private boolean udfCekBeforeSave(){
        boolean b=true;
        if(tblItem.getRowCount()==0){
            JOptionPane.showMessageDialog(this, "Tidak ada item yang ditransaksikan!\nTekan insert untuk menambahkan item penjualan");
            tblItem.requestFocus();
            return false;
        }
        if(cmbCaraBayar.getSelectedItem().toString().equalsIgnoreCase("KREDIT") && txtCustomer.getText().trim().isEmpty()){
            JOptionPane.showMessageDialog(this, "Isikan nama customer untuk penjualan kredit");
            txtCustomer.requestFocus();
            return false;
        }
        if(isKoreksi && (tblItem.getRowCount()==0 || (tblItem.getRowCount()==1 && (tblItem.getValueAt(0, 0)==null||tblItem.getValueAt(0, 0).toString().equalsIgnoreCase(""))))){
            b=JOptionPane.showConfirmDialog(aThis, "Anda yakin untuk membatalkan transaksi retur ini?", "Batal Retur", JOptionPane.YES_NO_OPTION)==JOptionPane.YES_OPTION;
        }
        return b;
    }

    private void udfSave(){
        if(!udfCekBeforeSave()) return;
         String sSql="", sMsg="";
         ResultSet rs=null;
         try{
             conn.setAutoCommit(false);
             if(isKoreksi){
                 rs=conn.createStatement().executeQuery("select fn_retur_jual_koreksi('"+txtReturnNo.getText()+"', '"+MainForm.sUserName+"')");
                 if(rs.next()){
                     sMsg="Retur dikoreksi dengan nomor '"+rs.getString(1)+"'";
                 }
             }
             if(tblItem.getRowCount()==0) {
                 conn.setAutoCommit(true);
                 JOptionPane.showMessageDialog(aThis, sMsg);
                 return;
             }

             double dPotongan=chkPersen.isSelected()? fn.udfGetDouble(lblTotal.getText())/100*fn.udfGetDouble(txtPot.getText()): fn.udfGetDouble(txtPot.getText());
             
             rs=conn.createStatement().executeQuery("select fn_get_retur_jual_no('"+fn.yyyymmdd_format.format(new SimpleDateFormat("dd/MM/yyyy").parse(lblTglJual.getText()))+"')");
             if(rs.next())
                 txtReturnNo.setText(rs.getString(1));

             sSql="INSERT INTO retur_jual(" +
                  "no_retur, tanggal, kode_cust, sales_no, kode_gudang, " +
                  "date_ins, user_ins, date_upd, user_upd, potongan, tunai_kredit, " +
                  "jenis_retur, keterangan, flag_trx) " +
                  "VALUES ('"+txtReturnNo.getText()+"', "
                     + "'"+ new SimpleDateFormat("yyyy-MM-dd").format(new SimpleDateFormat("dd/MM/yyyy").parse(txtDate.getText()))+"', '"+txtCustomer.getText()+"', '"+txtNoTrx.getText()+"', '"+lstGudang.get(cmbGudang.getSelectedIndex()).toString()+"', " +
                  "now(), '"+MainForm.sUserName+"', "+(isKoreksi? "now()": "null")+", "+(isKoreksi? "'"+MainForm.sUserName+"'": "null")+", "+dPotongan+", " +
                  "'"+cmbCaraBayar.getSelectedItem().toString().substring(0, 1)+"', '"+cmbJenisRetur.getSelectedItem().toString().substring(0, 1)+"', " +
                  "'"+txtCatatan.getText()+"', 'T'); ";

             TableColumnModel col=tblItem.getColumnModel();
             for(int i=0; i< tblItem.getRowCount(); i++){
                 if(tblItem.getValueAt(i, col.getColumnIndex("ProductID"))!=null &&
                   tblItem.getValueAt(i, col.getColumnIndex("ProductID")).toString().length()>0){
                    sSql+="INSERT INTO retur_jual_detail(no_retur, kode_item, qty, unit_price, " +
                            "date_ins, user_ins, unit, konv) values(" +
                            "'"+txtReturnNo.getText()+"', " +
                            "'"+tblItem.getValueAt(i, col.getColumnIndex("ProductID"))+"', " +
                            ""+fn.udfGetDouble(tblItem.getValueAt(i, col.getColumnIndex("Qty")))+", " +
                            ""+fn.udfGetDouble(tblItem.getValueAt(i, col.getColumnIndex("Harga")))+", " +
                            "now(), '"+MainForm.sUserName+"', " +
                            "'"+tblItem.getValueAt(i, col.getColumnIndex("Satuan")).toString()+ "'," +
                            ""+fn.udfGetDouble(tblItem.getValueAt(i, col.getColumnIndex("Konv")))+");";
                 }
             }

             //System.out.println(sSql);

             
             int i=conn.createStatement().executeUpdate(sSql);
             conn.setAutoCommit(true);
             sMsg+=sMsg.length()>0? "\n":""+"Simpan retur penjualan sukses!";
              JOptionPane.showMessageDialog(this, sMsg);
             //udfPreviewRetur();
             if(ObjForm!=null && ObjForm instanceof FrmReturPenjualanHistory)
                 ((FrmReturPenjualanHistory)ObjForm).udfLoadRetur();
             if(isKoreksi)
                 this.dispose();

             printKwitansi();
             udfNew();
             
         } catch (ParseException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage());
        }catch(SQLException se){
            try {
                conn.rollback();
                conn.setAutoCommit(true);
                JOptionPane.showMessageDialog(this, se.getMessage());
            } catch (SQLException se1) {
                JOptionPane.showMessageDialog(this, se1.getMessage());
            }
         }

    }

    private void printKwitansi(){
        PrinterJob job = PrinterJob.getPrinterJob();
        SysConfig sy=new SysConfig();

        DocFlavor flavor = DocFlavor.SERVICE_FORMATTED.PRINTABLE;
        PrintRequestAttributeSet aset = new HashPrintRequestAttributeSet();
        PrintServiceAttributeSet pset = new HashPrintServiceAttributeSet();
        PrintService[] services = PrintServiceLookup.lookupPrintServices(flavor, null);
        int i=0;
        for(i=0;i<services.length;i++){
            if(services[i].getName().equalsIgnoreCase(sy.getPrintKwtName())){
                try{
                    PrintPenjualanRetur pn = new PrintPenjualanRetur(conn, txtNoTrx.getText(), MainForm.sUserName,services[i]);
                }catch(java.lang.ArrayIndexOutOfBoundsException ie){
                    JOptionPane.showMessageDialog(this, "Printer tidak ditemukan!");
                }
                break;
            }
        }
        //if (JOptionPane.showConfirmDialog(null,"Cetak Invoice?","Message",JOptionPane.YES_NO_OPTION,JOptionPane.QUESTION_MESSAGE)==JOptionPane.YES_OPTION) {
        
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
        tblItem = new javax.swing.JTable();
        jPanel1 = new javax.swing.JPanel();
        cmbJenisRetur = new javax.swing.JComboBox();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        txtNamaCustomer = new javax.swing.JTextField();
        txtCustomer = new javax.swing.JTextField();
        lblTglJual = new javax.swing.JLabel();
        txtNoTrx = new javax.swing.JTextField();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        cmbCaraBayar = new javax.swing.JComboBox();
        cmbGudang = new javax.swing.JComboBox();
        jLabel7 = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        txtCatatan = new javax.swing.JTextField();
        jLabel10 = new javax.swing.JLabel();
        jLabel11 = new javax.swing.JLabel();
        txtReturnNo = new javax.swing.JTextField();
        jLabel12 = new javax.swing.JLabel();
        txtPot = new javax.swing.JTextField();
        chkPersen = new javax.swing.JCheckBox();
        jLabel8 = new javax.swing.JLabel();
        lblTotal = new javax.swing.JLabel();
        jToolBar1 = new javax.swing.JToolBar();
        btnNew = new javax.swing.JButton();
        btnSave = new javax.swing.JButton();
        btnPrint = new javax.swing.JButton();
        btnCancel = new javax.swing.JButton();
        jLabel3 = new javax.swing.JLabel();
        jLabel13 = new javax.swing.JLabel();
        txtDate = new javax.swing.JTextField();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Retur Penjualan");
        setBackground(new java.awt.Color(204, 204, 204));
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowOpened(java.awt.event.WindowEvent evt) {
                formWindowOpened(evt);
            }
        });

        tblItem.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        tblItem.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "ProductID", "Nama Barang", "Qty", "Satuan", "Harga", "Sub Total", "Konv", "Sisa"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Object.class, java.lang.Object.class, java.lang.Double.class, java.lang.Object.class, java.lang.Double.class, java.lang.Double.class, java.lang.Object.class, java.lang.Double.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, true, false, false, false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        tblItem.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_OFF);
        tblItem.getTableHeader().setReorderingAllowed(false);
        jScrollPane1.setViewportView(tblItem);

        jPanel1.setBackground(new java.awt.Color(204, 204, 255));
        jPanel1.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        jPanel1.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        cmbJenisRetur.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        cmbJenisRetur.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Potong Tagihan", "Tukar Barang" }));
        cmbJenisRetur.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                cmbJenisReturItemStateChanged(evt);
            }
        });
        jPanel1.add(cmbJenisRetur, new org.netbeans.lib.awtextra.AbsoluteConstraints(650, 10, 150, -1));

        jLabel1.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jLabel1.setText("Pelanggan");
        jPanel1.add(jLabel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 35, 90, 20));

        jLabel2.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jLabel2.setText("Jenis Retur");
        jPanel1.add(jLabel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(560, 10, 90, 20));

        txtNamaCustomer.setEditable(false);
        txtNamaCustomer.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        txtNamaCustomer.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        txtNamaCustomer.setDisabledTextColor(new java.awt.Color(0, 0, 0));
        jPanel1.add(txtNamaCustomer, new org.netbeans.lib.awtextra.AbsoluteConstraints(100, 60, 410, 20));

        txtCustomer.setEditable(false);
        txtCustomer.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        txtCustomer.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        txtCustomer.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtCustomerKeyReleased(evt);
            }
        });
        jPanel1.add(txtCustomer, new org.netbeans.lib.awtextra.AbsoluteConstraints(100, 35, 130, 20));

        lblTglJual.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        lblTglJual.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        jPanel1.add(lblTglJual, new org.netbeans.lib.awtextra.AbsoluteConstraints(340, 35, 105, 20));

        txtNoTrx.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        txtNoTrx.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        txtNoTrx.setDisabledTextColor(new java.awt.Color(0, 0, 0));
        jPanel1.add(txtNoTrx, new org.netbeans.lib.awtextra.AbsoluteConstraints(100, 10, 130, 20));

        jLabel5.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jLabel5.setText("Penjualan #");
        jPanel1.add(jLabel5, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 10, 90, 20));

        jLabel6.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jLabel6.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel6.setText("Tgl. Penjualan ");
        jPanel1.add(jLabel6, new org.netbeans.lib.awtextra.AbsoluteConstraints(230, 35, 110, 20));

        cmbCaraBayar.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        cmbCaraBayar.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "TUNAI", "KREDIT" }));
        cmbCaraBayar.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                cmbCaraBayarItemStateChanged(evt);
            }
        });
        jPanel1.add(cmbCaraBayar, new org.netbeans.lib.awtextra.AbsoluteConstraints(650, 35, 150, -1));

        cmbGudang.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        cmbGudang.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Center" }));
        cmbGudang.setEnabled(false);
        jPanel1.add(cmbGudang, new org.netbeans.lib.awtextra.AbsoluteConstraints(650, 60, 150, -1));

        jLabel7.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jLabel7.setText("Potongan");
        jPanel1.add(jLabel7, new org.netbeans.lib.awtextra.AbsoluteConstraints(560, 85, 90, 20));

        jLabel9.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jLabel9.setText("Catatan");
        jPanel1.add(jLabel9, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 85, 90, 20));

        txtCatatan.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        txtCatatan.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        txtCatatan.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtCatatanKeyReleased(evt);
            }
        });
        jPanel1.add(txtCatatan, new org.netbeans.lib.awtextra.AbsoluteConstraints(100, 85, 410, 20));

        jLabel10.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jLabel10.setText("Pembayaran");
        jPanel1.add(jLabel10, new org.netbeans.lib.awtextra.AbsoluteConstraints(560, 35, 90, 20));

        jLabel11.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jLabel11.setText("Retur #");
        jPanel1.add(jLabel11, new org.netbeans.lib.awtextra.AbsoluteConstraints(250, 10, 90, 20));

        txtReturnNo.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        txtReturnNo.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        txtReturnNo.setDisabledTextColor(new java.awt.Color(0, 0, 0));
        txtReturnNo.setEnabled(false);
        jPanel1.add(txtReturnNo, new org.netbeans.lib.awtextra.AbsoluteConstraints(340, 10, 145, 20));

        jLabel12.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jLabel12.setText("Site");
        jPanel1.add(jLabel12, new org.netbeans.lib.awtextra.AbsoluteConstraints(560, 60, 90, 20));

        txtPot.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        txtPot.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        txtPot.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtPotKeyReleased(evt);
            }
            public void keyTyped(java.awt.event.KeyEvent evt) {
                txtPotKeyTyped(evt);
            }
        });
        jPanel1.add(txtPot, new org.netbeans.lib.awtextra.AbsoluteConstraints(650, 85, 80, 20));

        chkPersen.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        chkPersen.setText("Persen");
        chkPersen.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                chkPersenItemStateChanged(evt);
            }
        });
        jPanel1.add(chkPersen, new org.netbeans.lib.awtextra.AbsoluteConstraints(730, 85, 70, -1));

        jLabel8.setBackground(new java.awt.Color(204, 204, 255));
        jLabel8.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jLabel8.setText("<html>\n<b>F4</b> &nbsp&nbsp : Membuat transaksi baru  &nbsp  &nbsp |  &nbsp  &nbsp \n<b>Insert</b> &nbsp : Menambah item barang  &nbsp  &nbsp |  &nbsp  &nbsp \n<b>Del</b> &nbsp&nbsp &nbsp &nbsp : Menghapus item barang <br>\n<b>F5</b> &nbsp&nbsp : Menyimpan Transaksi <br>\n</html>"); // NOI18N
        jLabel8.setVerticalAlignment(javax.swing.SwingConstants.TOP);
        jLabel8.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        jLabel8.setOpaque(true);

        lblTotal.setBackground(new java.awt.Color(0, 0, 102));
        lblTotal.setFont(new java.awt.Font("Tahoma", 0, 34)); // NOI18N
        lblTotal.setForeground(new java.awt.Color(255, 255, 255));
        lblTotal.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lblTotal.setText("0,00");
        lblTotal.setBorder(new org.jdesktop.swingx.border.DropShadowBorder());
        lblTotal.setOpaque(true);

        jToolBar1.setRollover(true);

        btnNew.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/Icon/buttons/add-32.png"))); // NOI18N
        btnNew.setText("New");
        btnNew.setFocusable(false);
        btnNew.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnNew.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnNew.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnNewActionPerformed(evt);
            }
        });
        jToolBar1.add(btnNew);

        btnSave.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/Icon/buttons/Ok-32.png"))); // NOI18N
        btnSave.setText("Save");
        btnSave.setFocusable(false);
        btnSave.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnSave.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnSave.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSaveActionPerformed(evt);
            }
        });
        jToolBar1.add(btnSave);

        btnPrint.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/Icon/buttons/print-32.png"))); // NOI18N
        btnPrint.setText("Print");
        btnPrint.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnPrint.setMargin(new java.awt.Insets(2, 2, 2, 2));
        btnPrint.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnPrint.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnPrintActionPerformed(evt);
            }
        });
        jToolBar1.add(btnPrint);

        btnCancel.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/Icon/buttons/exit-32.png"))); // NOI18N
        btnCancel.setText("Exit");
        btnCancel.setFocusable(false);
        btnCancel.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnCancel.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnCancel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCancelActionPerformed(evt);
            }
        });
        jToolBar1.add(btnCancel);

        jLabel3.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jLabel3.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel3.setText("Date");

        jLabel13.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jLabel13.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel13.setText(":");

        txtDate.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
        txtDate.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 1, true));
        txtDate.setDisabledTextColor(new java.awt.Color(0, 0, 0));
        txtDate.setEnabled(false);
        txtDate.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                txtDateFocusLost(evt);
            }
        });
        txtDate.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtDateKeyReleased(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jToolBar1, javax.swing.GroupLayout.PREFERRED_SIZE, 173, Short.MAX_VALUE)
                .addGap(143, 143, 143)
                .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 70, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, 0)
                .addComponent(jLabel13, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, 0)
                .addComponent(txtDate, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lblTotal, javax.swing.GroupLayout.PREFERRED_SIZE, 300, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(8, 8, 8))
            .addGroup(layout.createSequentialGroup()
                .addGap(10, 10, 10)
                .addComponent(jLabel8, javax.swing.GroupLayout.DEFAULT_SIZE, 810, Short.MAX_VALUE)
                .addGap(8, 8, 8))
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, 808, Short.MAX_VALUE)
                .addContainerGap())
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 808, Short.MAX_VALUE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jToolBar1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(10, 10, 10)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(jLabel13, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(txtDate, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(lblTotal, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addGap(9, 9, 9)
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, 112, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 185, Short.MAX_VALUE)
                .addGap(7, 7, 7)
                .addComponent(jLabel8, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(7, 7, 7))
        );

        setSize(new java.awt.Dimension(844, 465));
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void formWindowOpened(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowOpened
        udfInitForm();
    }//GEN-LAST:event_formWindowOpened

    private void btnNewActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnNewActionPerformed
        udfNew();
}//GEN-LAST:event_btnNewActionPerformed

    private void btnSaveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSaveActionPerformed
        udfSave();
}//GEN-LAST:event_btnSaveActionPerformed

    private void btnPrintActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnPrintActionPerformed
        //printKwitansi(txtNoPO.getText(), false);
}//GEN-LAST:event_btnPrintActionPerformed

    private void btnCancelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCancelActionPerformed
        if(btnCancel.getText().equalsIgnoreCase("cancel")){
            if(getTitle().indexOf("Revision")>0) dispose();
            btnSave.setEnabled(false);
            btnNew.setEnabled(true);
            btnCancel.setText("Exit");
            //btnCancel.setIcon(new javax.swing.ImageIcon(getClass().getResource("/retail/image/Icon/Exit.png"))); // NOI18N
        }else{
            this.dispose();
        }
}//GEN-LAST:event_btnCancelActionPerformed

    private void txtCustomerKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtCustomerKeyReleased
        fn.lookup(evt, new Object[]{txtNamaCustomer}, "select kode_cust, coalesce(nama,'') as nama_customer from r_customer " +
                "where kode_cust||coalesce(nama,'') ilike '%"+txtCustomer.getText()+"%'", 500, 200);
    }//GEN-LAST:event_txtCustomerKeyReleased

    private void txtCatatanKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtCatatanKeyReleased
        // TODO add your handling code here:
    }//GEN-LAST:event_txtCatatanKeyReleased

    private void cmbCaraBayarItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_cmbCaraBayarItemStateChanged
//        jLabel3.setVisible(cmbCustPembayaran.getSelectedIndex()==1);
//        jFJtTempo.setVisible(cmbCustPembayaran.getSelectedIndex()==1);
    }//GEN-LAST:event_cmbCaraBayarItemStateChanged

    private void txtPotKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtPotKeyTyped
        fn.keyTyped(evt);
        
    }//GEN-LAST:event_txtPotKeyTyped

    private void txtDateFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtDateFocusLost
        // TODO add your handling code here:
}//GEN-LAST:event_txtDateFocusLost

    private void txtDateKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtDateKeyReleased
        // TODO add your handling code here:
}//GEN-LAST:event_txtDateKeyReleased

    private void chkPersenItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_chkPersenItemStateChanged
        udfSetTotalAmount();
    }//GEN-LAST:event_chkPersenItemStateChanged

    private void txtPotKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtPotKeyReleased
        udfSetTotalAmount();
    }//GEN-LAST:event_txtPotKeyReleased

    private void cmbJenisReturItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_cmbJenisReturItemStateChanged
        cmbCaraBayar.setSelectedIndex(cmbJenisRetur.getSelectedIndex());
    }//GEN-LAST:event_cmbJenisReturItemStateChanged

    /**
    * @param args the command line arguments
    */
    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                try{
                    UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
                    BorderUIResource borderUIResource= new BorderUIResource(BorderFactory.createLineBorder(Color.yellow, 2));
                    UIManager.put("Table.focusCellHighlightBorder", borderUIResource);
                } catch (Exception e){
                    JOptionPane.showMessageDialog(null, "Couldn't load Windows look and feel " + e);
                }
                new TrxReturPenjualan().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnCancel;
    private javax.swing.JButton btnNew;
    private javax.swing.JButton btnPrint;
    private javax.swing.JButton btnSave;
    private javax.swing.JCheckBox chkPersen;
    private javax.swing.JComboBox cmbCaraBayar;
    private javax.swing.JComboBox cmbGudang;
    private javax.swing.JComboBox cmbJenisRetur;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JToolBar jToolBar1;
    private javax.swing.JLabel lblTglJual;
    private javax.swing.JLabel lblTotal;
    private javax.swing.JTable tblItem;
    private javax.swing.JTextField txtCatatan;
    private javax.swing.JTextField txtCustomer;
    private javax.swing.JTextField txtDate;
    private javax.swing.JTextField txtNamaCustomer;
    private javax.swing.JTextField txtNoTrx;
    private javax.swing.JTextField txtPot;
    private javax.swing.JTextField txtReturnNo;
    // End of variables declaration//GEN-END:variables

}
