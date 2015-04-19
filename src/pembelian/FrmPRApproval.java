/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * FrmPRApproval.java
 *
 * Created on Jul 29, 2010, 11:51:31 AM
 */

package pembelian;

import main.MainForm;
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
import java.io.ByteArrayInputStream;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JCheckBox;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumnModel;
import javax.swing.text.MaskFormatter;
import main.GeneralFunction;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.util.JRLoader;
import net.sf.jasperreports.view.JasperViewer;
import org.jdesktop.swingx.JXTable;
import tableRender.ColumnGroup;
import tableRender.GroupableTableHeader;

/**
 *
 * @author ustadho
 */
public class FrmPRApproval extends javax.swing.JInternalFrame {
    private Connection conn;
    private boolean bAcc1= false, bAcc2=false;
    private String  sAcc1= "", sAcc2="";
    private GeneralFunction fn;
    boolean cumaSingDurungApprove=true;
    private JFormattedTextField jFDate1;
    MyKeyListener kListener=new MyKeyListener();
    private Timer timer;
    private int i;
    private Long waktuRefresh;

    /** Creates new form FrmPRApproval */
    public FrmPRApproval(Connection con, boolean b) {
        initComponents();
        this.conn=con;
        this.cumaSingDurungApprove=b;
        fn=new GeneralFunction();
        
        fn.addKeyListenerInContainer(jPanel1, kListener, txtFocusListener);
        tblHeader.addKeyListener(kListener);
        tblDetail.addKeyListener(kListener);

        MaskFormatter fmttgl = null;
        try {
            fmttgl = new MaskFormatter("##/##/####");
        } catch (java.text.ParseException e) {}

        jFDate1 = new JFormattedTextField(fmttgl);
        jFTgl.setFormatterFactory(jFDate1.getFormatterFactory());
        jFTglAkhir.setFormatterFactory(jFDate1.getFormatterFactory());

        tblHeader.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            public void valueChanged(ListSelectionEvent e) {
                if(conn!=null && tblHeader.getSelectedRow()<0) return;
                udfLoadItemDetail();
            }
        });

        udfInitForm();
    }

    class DoTick extends TimerTask {
        public void run() {
            jLTest.setText(String.valueOf(i));
            i++;

            udfLoadPR();
        }
    }

    private void udfLoadItemDetail(){
        try{
             DefaultTableModel myModelDetPR=(DefaultTableModel)tblDetail.getModel();
            tblDetail.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
            String sNoPr=tblHeader.getValueAt(tblHeader.getSelectedRow(), tblHeader.getColumnModel().getColumnIndex("PRNo")).toString();
           String sQry1="select * from fn_pr_detail_item('"+sNoPr+"') as (kode_barang varchar, nama_barang varchar, qty numeric, uom_alt varchar, \n" +
                        "price double precision, disc double precision, vat double precision, kode_supp varchar, nama_supplier varchar, no_po text, \n" +
                        "amount double precision)";
           
            System.out.println(sQry1);
            myModelDetPR.setRowCount(0);
            ResultSet rs1=conn.createStatement().executeQuery(sQry1);
            double TotAmount=0;
            while (rs1.next()){
                myModelDetPR.addRow(new Object[]{
                    rs1.getString("kode_barang"),
                    rs1.getString("nama_barang"),
                    rs1.getString("uom_alt"),
                    rs1.getDouble("qty"),
                    rs1.getString("kode_supp"),
                    rs1.getString("nama_supplier"),
                    rs1.getDouble("amount"),
                    rs1.getString("no_po")
                    });
                TotAmount+=rs1.getDouble("amount");
            }

            jXTitledPanel1.setTitle("Item PR Detail --- (Perkiraan Total Amount : "+new DecimalFormat("#,##0.00").format(TotAmount)+")");

            tblDetail.setModel((DefaultTableModel) fn.autoResizeColWidth(tblDetail, (DefaultTableModel)tblDetail.getModel()).getModel());
            rs1.close();

        }catch(SQLException se){}
    }

    public void setCumaSingDurungApprove(boolean b){
        cumaSingDurungApprove=b;
        btnSave.setEnabled(b);
        
    }

    public void setConn(Connection con){
        this.conn=con;
    }

    private void udfInitForm(){
        fn=new GeneralFunction(conn);

        try {
            ResultSet rs = conn.createStatement().executeQuery("select '01/'||to_char(current_date, 'MM/yyyy') as tgl1,  to_char(current_date, 'dd/MM/yyyy') as tgl2 " +
                    "");
            
            if(rs.next()){
                jFTgl.setText(cumaSingDurungApprove? rs.getString(2): rs.getString(1));
                jFTgl.setValue(cumaSingDurungApprove? rs.getString(2): rs.getString(1));
                jFTglAkhir.setText(rs.getString(2));
                jFTglAkhir.setValue(rs.getString(2));
            }
            
            rs.close();
            rs=conn.createStatement().executeQuery("select * from m_user_acc where user_name='"+MainForm.sUserName+"' " +
                    "and acc_modul='PR'");
            while(rs.next()){
                if(rs.getInt("level")==1) bAcc1=true;
                if(rs.getInt("level")==2) bAcc2=true;
            }
            rs.close();
            rs=conn.createStatement().executeQuery("select distinct acc.level, coalesce(jb.singkatan,'') " +
                    "from m_user_acc acc  " +
                    "inner join m_user d on d.username=acc.user_name " +
                    "inner join m_jabatan jb on jb.kode_jabatan=d.kode_jabatan " +
                    "where acc_modul='PR' and coalesce(priority,0)=0 ");

            while(rs.next()){
                if(rs.getInt("level")==1) sAcc1=rs.getString(2);
                if(rs.getInt("level")==2) sAcc2=rs.getString(2);
            }
            bAcc2=(cumaSingDurungApprove && bAcc2);
            bAcc1=(cumaSingDurungApprove && bAcc1);

            tblHeader.setModel(new javax.swing.table.DefaultTableModel(
                new Object [][] {},
                new String [] {"PRNo", "Site ID", "Site Name", "Release Date", "Request By", "Need Date", sAcc2, sAcc1, "Cito", "AccName2", "Tambahan"}
            ) {
                Class[] types = new Class [] {
                    java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, 
                    java.lang.Object.class, java.lang.Object.class, java.lang.Boolean.class, java.lang.Boolean.class,
                    java.lang.Boolean.class, java.lang.Object.class, java.lang.Boolean.class
                };
                boolean[] canEdit = new boolean [] {
                    false, false, false, false, false, false,
                    bAcc2, bAcc1, false, false, false
                };

                public Class getColumnClass(int columnIndex) {
                    return types [columnIndex];
                }

                public boolean isCellEditable(int rowIndex, int columnIndex) {
                    return canEdit [columnIndex];
                }
            });


            tblHeader.getTableHeader().setReorderingAllowed(false);
            
            UIManager.put(GroupableTableHeader.uiClassID, "tableRender.GroupableTableHeaderUI");
            GroupableTableHeader header = new GroupableTableHeader(tblHeader.getColumnModel());
            TableColumnModel columns = tblHeader.getColumnModel();
            ColumnGroup acc = new ColumnGroup("Approve By");
            acc.add(columns.getColumn(6));
            acc.add(columns.getColumn(7));
            header.addGroup(acc);
            
            tblHeader.setTableHeader(header);

            for(int i=0; i< tblHeader.getColumnCount(); i++){
                tblHeader.getColumnModel().getColumn(i).setCellRenderer(new MyRowRenderer());
            }

            tblDetail.setRowHeight(22);
            tblHeader.getColumn("PRNo").setPreferredWidth(110);
            tblHeader.getColumn("Site Name").setPreferredWidth(130);
            tblHeader.getColumn("Release Date").setPreferredWidth(110); tblHeader.getColumn("Need Date").setPreferredWidth(110);
            tblHeader.getColumn(sAcc1).setPreferredWidth(110);
            tblHeader.getColumn(sAcc2).setPreferredWidth(110);
            //tblHeader.getColumn("Cito").setPreferredWidth(0);tblHeader.getColumn("Cito").setMinWidth(0);tblHeader.getColumn("Cito").setMaxWidth(0);
            tblHeader.getColumn("AccName2").setPreferredWidth(0);tblHeader.getColumn("AccName2").setMinWidth(0);tblHeader.getColumn("AccName2").setMaxWidth(0);

            //udfLoadPR();
            
        } catch (SQLException ex) {
            Logger.getLogger(FrmPRApproval.class.getName()).log(Level.SEVERE, null, ex);
        }

        jLabel16.setText(getTitle());
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                tblHeader.requestFocus();
            }
        });
        if(!cumaSingDurungApprove) {
            jSlider1.setVisible(false); jLDetik.setVisible(false);
            jLabel11.setVisible(false); jLTest.setVisible(false); jBSet.setVisible(false);
            udfLoadPR();
            return;
        }
        if ((jSlider1.getValue()/60)==0.0){
            jLDetik.setText(String.valueOf(jSlider1.getValue()%60)+" Detik");
        }else{
            jLDetik.setText(String.valueOf(jSlider1.getValue()/60)+" Menit "+String.valueOf(jSlider1.getValue()%60)+" Detik");
        }

        jLDetik.setHorizontalAlignment(JLabel.CENTER);
        jLTest.setHorizontalAlignment(JLabel.RIGHT);
        waktuRefresh=Long.valueOf(String.valueOf(jSlider1.getValue()))*1000;
        timer = new Timer();
        timer.schedule(new DoTick(), 0, waktuRefresh);



    }

    private void udfLoadPR(){
        try{
            SimpleDateFormat dmy = new SimpleDateFormat("dd/MM/yyyy");
            SimpleDateFormat ymd = new SimpleDateFormat("yyyy-MM-dd");
            String sQry = "select distinct pr.no_pr, coalesce(pr.site_id, '') as site_id, coalesce(g.deskripsi, '') as site_name," + 
                    "release_date, coalesce(requested_by,'') as requested_by, need_date, " + 
                    "trim(coalesce(pr.acc_level_1, ''))<>'' as acc_level_1, trim(coalesce(pr.acc_level_2, ''))<>'' as acc_level_2," +
                    "coalesce(pr.cito, false) as cito, coalesce(pr.acc_level_2, '') as acc_name2, coalesce(pr.flag_tambahan, false) as flag_tambah " +
                    "from phar_pr pr "
                    + "inner join phar_pr_detail d on d.no_pr=pr.no_pr "
                    + "inner join barang i on i.item_code=d.kode_barang " +
                    "left join gudang g on g.kode_gudang=pr.site_id " +
                    "where pr.flag_tr='T' " + 
                    (cumaSingDurungApprove? "and (trim(coalesce(pr.acc_level_1, ''))='' or trim(coalesce(pr.acc_level_2, ''))='') ": "") +
                    "and to_char(release_date, 'yyyy-MM-dd')>='" + ymd.format(dmy.parse(jFTgl.getText())) + "' " + 
                    "and to_char(release_date, 'yyyy-MM-dd')<='" + ymd.format(dmy.parse(jFTglAkhir.getText())) + "' "
                    + "and d.kode_barang||coalesce(i.nama_paten,'')||coalesce(i.item_name,'') ilike '%"+txtItem.getText()+"%' " + 
                    " order by release_date desc ";
            setCursor(new Cursor(Cursor.WAIT_CURSOR));
            try {
                ResultSet rs = conn.createStatement().executeQuery(sQry);
                ((DefaultTableModel) tblHeader.getModel()).setNumRows(0);
                ((DefaultTableModel) tblDetail.getModel()).setNumRows(0);
                while (rs.next()) {
                    ((DefaultTableModel) tblHeader.getModel()).addRow(new Object[]{
                        rs.getString("no_pr"),
                        rs.getString("site_id"),
                        rs.getString("site_name"),
                        rs.getTimestamp("release_date"),
                        rs.getString("requested_by"), 
                        rs.getTimestamp("need_date"),
                        rs.getBoolean("acc_level_2"),
                        rs.getBoolean("acc_level_1"),
                        rs.getBoolean("cito"),
                        rs.getString("acc_name2"),
                        rs.getBoolean("flag_tambah")
                    });
                }
                if(tblHeader.getRowCount()>0)
                    tblHeader.setRowSelectionInterval(0, 0);
                setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
                //tblHeader.setModel((DefaultTableModel) fn.autoResizeColWidth(tblHeader, (DefaultTableModel)tblHeader.getModel()).getModel());
            } catch (SQLException se) {
                setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
                JOptionPane.showMessageDialog(this, se.getMessage());
            }
        }catch(ParseException ex){
            Logger.getLogger(FrmPRApproval.class.getName()).log(Level.SEVERE, null,ex);
        }
    }

    SimpleDateFormat dmyFmt=new SimpleDateFormat("dd/MM/yyyy");
    SimpleDateFormat dmyFmt_hhmm=new SimpleDateFormat("dd/MM/yyyy HH:mm");

    private void udfSave() {
        String sUpdate="";

        for(int i=0; i<tblHeader.getRowCount(); i++){
            sUpdate+="Update phar_pr set "+
                    (bAcc1? "acc_level_1='"+ ((Boolean)tblHeader.getValueAt(i, 7)==true?MainForm.sUserName: "" )+"', acc_time1= now() ":" ")+
                    (bAcc1 && bAcc2? "," :" ")+
                    (bAcc2? "acc_level_2='"+ ((Boolean)tblHeader.getValueAt(i, 6)==true?MainForm.sUserName: "" )+"', acc_time2=now() ":" ")+
                    "where no_pr='"+tblHeader.getValueAt(i, 0).toString()+"'; " ;
        }

        //System.out.println(sUpdate);

        try{
            conn.setAutoCommit(false);
            int i=conn.createStatement().executeUpdate(sUpdate);
            conn.setAutoCommit(true);
            if(i>0){
                JOptionPane.showMessageDialog(this, "Simpan PR approval Sukses!");
            }
        }catch(SQLException se){
            try {
                conn.rollback();
                JOptionPane.showMessageDialog(this, se.getMessage());
            } catch (SQLException ex) {
                Logger.getLogger(FrmPRApproval.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    private void udfUpdatePR(KeyEvent evt) {
//        if((!cumaSingDurungApprove ) ||(tblHeader.getSelectedRow()>0 &&
//                !MainForm.sUserName.equalsIgnoreCase(tblHeader.getValueAt(tblHeader.getSelectedRow(), tblHeader.getColumnModel().getColumnIndex("AccName2")).toString()) &&
//                (Boolean)tblHeader.getValueAt(tblHeader.getSelectedRow(), 6)==true &&
//                ((Boolean)tblHeader.getValueAt(tblHeader.getSelectedRow(), 7)==true || (Boolean)tblHeader.getValueAt(tblHeader.getSelectedRow(), 8)==true)
//                )) return;

//        if(MainForm.menuPRHistory !=null && !MainForm.menuPRHistory.canUpdate() || (!cumaSingDurungApprove && tblHeader.getSelectedRow()>0 &&
//                !MainForm.sUserName.equalsIgnoreCase(tblHeader.getValueAt(tblHeader.getSelectedRow(), tblHeader.getColumnModel().getColumnIndex("AccName2")).toString()) &&
//                (Boolean)tblHeader.getValueAt(tblHeader.getSelectedRow(), 6)==true &&
//                ((Boolean)tblHeader.getValueAt(tblHeader.getSelectedRow(), 7)==true || (Boolean)tblHeader.getValueAt(tblHeader.getSelectedRow(), 8)==true)
//                )) 
//            return;
        int iRow=tblDetail.getSelectedRow();

        if(iRow<0) return;
        String sKodeBarang=tblDetail.getValueAt(iRow, 0).toString();
        switch(evt.getKeyCode()){
            case KeyEvent.VK_F3:{
//                if(!MainForm.menuPRApproval.canUpdate())
//                    return;
                
                String sNoPR=tblHeader.getValueAt(tblHeader.getSelectedRow(), 0).toString();
                    if(iRow>=0){
                        double convertion=1;
                        String satuanKecil="";
                        TableColumnModel col=tblDetail.getColumnModel();
                        try{
                            ResultSet rs=conn.createStatement().executeQuery("select * from phar_po " +
                                    "inner join phar_po_detail using(no_po) " +
                                    "where flag_trx='T' and kode_barang='"+sKodeBarang+"' " +
                                    "and no_pr='"+sNoPR+"' " +
                                    "");
                            if(rs.next()){
                                JOptionPane.showMessageDialog(FrmPRApproval.this, "Qty PR tidak bisa diupdate karena telah dibuatkan PO!");
                                tblDetail.requestFocusInWindow();
                                return;
                            }

                            String sSupplier=(tblDetail.getValueAt(iRow, col.getColumnIndex("Kode Supp."))==null? "" : tblDetail.getValueAt(iRow, col.getColumnIndex("Kode Supp.")).toString());

                            String sQry="select coalesce(sb.uom_alt,'') as uom_alt, coalesce(sb.convertion,0) as convertion, " +
                                    "coalesce(i.satuan_kecil,'') as uom_kecil, priority as priority, " +
                                    "coalesce(i.stock,0) as stock_on_hand, coalesce(on_order,0) as stock_on_order," +
                                    "coalesce(min,0) as min, coalesce(max,0) as max " +
                                    "from barang i " +
                                    "left join supplier_barang sb on sb.kode_barang=i.item_code and priority=1 " +
                                    "where i.item_code='"+sKodeBarang+"' " +
                                    "and sb.kode_supplier='"+sSupplier+"' " +
                                    "limit 1";

                            rs=conn.createStatement().executeQuery(sQry);
                            if(rs.next()){
                                convertion=rs.getInt("convertion");
                                satuanKecil=rs.getString("uom_kecil");
                            }
                            rs.close();
                        }catch(SQLException se){
                            JOptionPane.showMessageDialog(FrmPRApproval.this, se.getMessage());
                        }

                        DlgPRUpdateQty d1=new DlgPRUpdateQty(JOptionPane.getFrameForComponent(FrmPRApproval.this), true);
                        d1.setSrcForm(FrmPRApproval.this, iRow);
                        d1.setConn(conn);
                        d1.setPR(sNoPR, sKodeBarang,
                                tblDetail.getValueAt(iRow, col.getColumnIndex("Nama Barang")).toString(),
                                fn.udfGetDouble(tblDetail.getValueAt(iRow, col.getColumnIndex("Qty")))*convertion,
                                satuanKecil);
                        d1.setVisible(true);
                        if(d1.getUpdated()>0)
                            udfLoadItemDetail();
                    }
                    break;
            }
            case KeyEvent.VK_DELETE:{
                try{
//                    if(!MainForm.menuPOApproval.canDelete()) 
//                        return;
                    
                    String sNoPR=tblHeader.getValueAt(tblHeader.getSelectedRow(), 0).toString();
                    ResultSet rs=conn.createStatement().executeQuery("select * from phar_po_detail pod "
                                + "inner join phar_po po on po.no_po=pod.no_po "
                                + "where po.flag_trx='T' "
                                + "and kode_barang='"+sKodeBarang+"' and pod.no_pr='"+sNoPR+"'");
                    if(rs.next()){
                        JOptionPane.showMessageDialog(this, "Item tersebut tidak bisa dihapus karena sudah dibuatkan PO");
                        rs.close();
                        return;
                    }
                    rs.close();

                    if(JOptionPane.showConfirmDialog(this, "Anda yakin untuk menghapus item '"+sKodeBarang+"'?", "Confirm", JOptionPane.YES_NO_OPTION)==JOptionPane.YES_OPTION){
                        conn.setAutoCommit(false);
                        int i=conn.createStatement().executeUpdate("delete from phar_pr_detail where " +
                                "kode_barang='"+sKodeBarang+"' and no_pr='"+sNoPR+"'; \n"
                                + "update phar_pr_detail_del set user_del='"+MainForm.sUserName+"' "
                                + "where kode_barang='"+sKodeBarang+"' and no_pr='"+sNoPR+"';");

                        conn.setAutoCommit(true);

                        if(i>0)
                            ((DefaultTableModel)tblDetail.getModel()).removeRow(iRow);

                        udfLoadItemDetail();
                        if(tblDetail.getRowCount() > 0)
                            tblDetail.setRowSelectionInterval(iRow-1, iRow-1);
                    }
                }catch(SQLException se)        {
                try {
                    JOptionPane.showMessageDialog(this, se.getMessage());
                    conn.rollback();
                    conn.setAutoCommit(true);
                    
                } catch (SQLException ex) {
                    JOptionPane.showMessageDialog(this, ex.getMessage());
                }

                }
                break;
            }

        }
    }
    public class MyRowRenderer extends DefaultTableCellRenderer implements TableCellRenderer{
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
                boolean hasFocus, int row, int column) {
            setFont(table.getFont());
            if(isSelected){
                setBackground(table.getSelectionBackground());
                setForeground(table.getSelectionForeground());
            }else{
                if((Boolean)table.getValueAt(row, table.getColumnModel().getColumnIndex("Cito"))==true){
                    setBackground(lblPRCito.getBackground());
                    setForeground(table.getForeground());
                }else if((Boolean)table.getValueAt(row, table.getColumnModel().getColumnIndex("Tambahan"))==true){
                    setBackground(lblPRTambahan.getBackground());
                    setForeground(table.getForeground());
                }else{
                    setBackground(table.getBackground());
                    setForeground(table.getForeground());
                }
            }
            JCheckBox checkBox = new JCheckBox();
            if(value instanceof Date ){
                value=dmyFmt_hhmm.format(value);
            }if(value instanceof Timestamp ){
                value=dmyFmt_hhmm.format(value);
            }else if(value instanceof Double ||value instanceof Integer ||value instanceof Float  ){
                setHorizontalAlignment(SwingConstants.RIGHT);
                value=fn.dFmt.format(value);
            }else if (value instanceof Boolean) { // Boolean
                checkBox.setSelected(((Boolean) value).booleanValue());
                checkBox.setHorizontalAlignment(lblPRCito.CENTER);
                checkBox.setBackground(getBackground());
                checkBox.setForeground(getForeground());


                checkBox.setSelected(((Boolean) value).booleanValue());
                checkBox.setHorizontalAlignment(JLabel.CENTER);

                if(isSelected){
                    checkBox.setBackground(table.getSelectionBackground());
                    checkBox.setForeground(table.getSelectionForeground());
                }else{
                   if( row>=0 && (Boolean)table.getValueAt(row, table.getColumnModel().getColumnIndex("Cito"))==true){
                        checkBox.setBackground(lblPRCito.getBackground());
                        checkBox.setForeground(table.getForeground());
                    }else{
                       if((Boolean)table.getValueAt(row, table.getColumnModel().getColumnIndex("Tambahan"))==true){
                            checkBox.setBackground(lblPRTambahan.getBackground());
                            checkBox.setForeground(table.getForeground());
                       }else{
                            checkBox.setBackground(table.getBackground());
                            checkBox.setForeground(table.getForeground());
                       }
                    }
                }
                   return checkBox;
            }
            

            setValue(value);
            return this;
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

        jPanel1 = new javax.swing.JPanel();
        btnClose = new javax.swing.JButton();
        btnSave = new javax.swing.JButton();
        btnCtk = new javax.swing.JButton();
        jLabel16 = new javax.swing.JLabel();
        jLabel1 = new javax.swing.JLabel();
        jFTgl = new javax.swing.JFormattedTextField();
        jLabel2 = new javax.swing.JLabel();
        jFTglAkhir = new javax.swing.JFormattedTextField();
        btnFilter1 = new javax.swing.JButton();
        txtItem = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        jXTitledPanel1 = new org.jdesktop.swingx.JXTitledPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        tblDetail = new org.jdesktop.swingx.JXTable();
        jScrollPane1 = new javax.swing.JScrollPane();
        tblHeader = new javax.swing.JTable();
        jPanel2 = new javax.swing.JPanel();
        lblPRCito = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jSlider1 = new javax.swing.JSlider();
        jLDetik = new javax.swing.JLabel();
        jLabel11 = new javax.swing.JLabel();
        jLTest = new javax.swing.JLabel();
        jBSet = new javax.swing.JButton();
        jLabel7 = new javax.swing.JLabel();
        lblPRTambahan = new javax.swing.JLabel();

        setClosable(true);
        setIconifiable(true);
        setMaximizable(true);
        setResizable(true);
        setTitle("Purchase Requisition  Approval ");
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

        btnClose.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/Icon/32/close.png"))); // NOI18N
        btnClose.setText("Close");
        btnClose.setToolTipText("New     (F12)");
        btnClose.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnClose.setMargin(new java.awt.Insets(2, 2, 2, 2));
        btnClose.setMaximumSize(new java.awt.Dimension(40, 40));
        btnClose.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnClose.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCloseActionPerformed(evt);
            }
        });
        jPanel1.add(btnClose, new org.netbeans.lib.awtextra.AbsoluteConstraints(110, 10, 50, 60));

        btnSave.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/Icon/32/cd.png"))); // NOI18N
        btnSave.setText("Save");
        btnSave.setToolTipText("New     (F12)");
        btnSave.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnSave.setMargin(new java.awt.Insets(2, 2, 2, 2));
        btnSave.setMaximumSize(new java.awt.Dimension(40, 40));
        btnSave.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnSave.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSaveActionPerformed(evt);
            }
        });
        jPanel1.add(btnSave, new org.netbeans.lib.awtextra.AbsoluteConstraints(60, 10, 50, 60));

        btnCtk.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/Icon/buttons/print-32.png"))); // NOI18N
        btnCtk.setText("Print");
        btnCtk.setToolTipText("New     (F12)");
        btnCtk.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnCtk.setMargin(new java.awt.Insets(2, 2, 2, 2));
        btnCtk.setMaximumSize(new java.awt.Dimension(40, 40));
        btnCtk.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnCtk.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCtkActionPerformed(evt);
            }
        });
        jPanel1.add(btnCtk, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 10, 50, 60));

        jLabel16.setFont(new java.awt.Font("Tahoma", 0, 24)); // NOI18N
        jLabel16.setForeground(new java.awt.Color(0, 0, 153));
        jLabel16.setText(" Purchase Requisition Approval ");
        jPanel1.add(jLabel16, new org.netbeans.lib.awtextra.AbsoluteConstraints(340, 0, 465, 40));

        jLabel1.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel1.setText("Dari : ");
        jPanel1.add(jLabel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(170, 50, 65, 20));

        jFTgl.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 51, 153), 1, true));
        jFTgl.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jPanel1.add(jFTgl, new org.netbeans.lib.awtextra.AbsoluteConstraints(237, 50, 100, 22));

        jLabel2.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jLabel2.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel2.setText("Item : ");
        jPanel1.add(jLabel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(515, 50, 65, 20));

        jFTglAkhir.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 51, 153), 1, true));
        jFTglAkhir.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jPanel1.add(jFTglAkhir, new org.netbeans.lib.awtextra.AbsoluteConstraints(410, 50, 96, 22));

        btnFilter1.setText("Tampilkan");
        btnFilter1.setToolTipText("Fiter");
        btnFilter1.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        btnFilter1.setMaximumSize(new java.awt.Dimension(40, 40));
        btnFilter1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnFilter1ActionPerformed(evt);
            }
        });
        jPanel1.add(btnFilter1, new org.netbeans.lib.awtextra.AbsoluteConstraints(710, 50, 85, 25));
        jPanel1.add(txtItem, new org.netbeans.lib.awtextra.AbsoluteConstraints(581, 50, 115, -1));

        jLabel3.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jLabel3.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel3.setText("Sampai :");
        jPanel1.add(jLabel3, new org.netbeans.lib.awtextra.AbsoluteConstraints(339, 50, 65, 20));

        jXTitledPanel1.setTitle("Item Detail");
        jXTitledPanel1.setTitleFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jXTitledPanel1.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jXTitledPanel1.getContentContainer().setLayout(new javax.swing.BoxLayout(jXTitledPanel1.getContentContainer(), javax.swing.BoxLayout.LINE_AXIS));

        tblDetail.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Kode Barang", "Nama Barang", "UOM", "Qty", "Kode Supp.", "Nama Supplier", "Amount", "PO#"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Double.class, java.lang.Object.class, java.lang.Object.class, java.lang.Double.class, java.lang.Object.class
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
        tblDetail.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_OFF);
        tblDetail.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                tblDetailKeyPressed(evt);
            }
        });
        jScrollPane2.setViewportView(tblDetail);

        jXTitledPanel1.getContentContainer().add(jScrollPane2);

        tblHeader.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        tblHeader.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "PRNo", "Site ID", "Site Name", "Release Date", "Request By", "Need Date", "Acc2", "Acc1", "Cito", "AccName2", "Tambahan"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Boolean.class, java.lang.Boolean.class, java.lang.Object.class, java.lang.Object.class, java.lang.Boolean.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false, true, true, false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        tblHeader.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_OFF);
        tblHeader.getTableHeader().setReorderingAllowed(false);
        jScrollPane1.setViewportView(tblHeader);

        jPanel2.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        lblPRCito.setBackground(new java.awt.Color(255, 153, 153));
        lblPRCito.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        lblPRCito.setOpaque(true);
        jPanel2.add(lblPRCito, new org.netbeans.lib.awtextra.AbsoluteConstraints(50, 10, 20, 14));

        jLabel4.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel4.setText("Legend :");
        jPanel2.add(jLabel4, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 10, 65, -1));

        jLabel5.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel5.setForeground(new java.awt.Color(0, 0, 153));
        jLabel5.setText(" PR Cito");
        jPanel2.add(jLabel5, new org.netbeans.lib.awtextra.AbsoluteConstraints(80, 10, 50, -1));

        jLabel6.setForeground(new java.awt.Color(0, 0, 153));
        jLabel6.setText("<html>\n<b>F3  :</b> &nbsp Edit Item PR <br>\n<b>Del :</b> &nbsp Delete Item PR\n</html>");
        jPanel2.add(jLabel6, new org.netbeans.lib.awtextra.AbsoluteConstraints(290, 0, 150, 30));

        jSlider1.setMaximum(300);
        jSlider1.setValue(120);
        jSlider1.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                jSlider1StateChanged(evt);
            }
        });
        jPanel2.add(jSlider1, new org.netbeans.lib.awtextra.AbsoluteConstraints(440, 0, 132, 40));

        jLDetik.setBackground(new java.awt.Color(255, 255, 204));
        jLDetik.setFont(new java.awt.Font("Dialog", 1, 10)); // NOI18N
        jLDetik.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        jLDetik.setOpaque(true);
        jPanel2.add(jLDetik, new org.netbeans.lib.awtextra.AbsoluteConstraints(570, 0, 58, 20));

        jLabel11.setText("Count");
        jPanel2.add(jLabel11, new org.netbeans.lib.awtextra.AbsoluteConstraints(640, 0, 40, 20));

        jLTest.setBackground(new java.awt.Color(250, 163, 120));
        jLTest.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 51, 204)));
        jLTest.setOpaque(true);
        jPanel2.add(jLTest, new org.netbeans.lib.awtextra.AbsoluteConstraints(680, 0, 50, 20));

        jBSet.setText("Aplly");
        jBSet.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jBSetActionPerformed(evt);
            }
        });
        jPanel2.add(jBSet, new org.netbeans.lib.awtextra.AbsoluteConstraints(740, 0, 70, -1));

        jLabel7.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel7.setForeground(new java.awt.Color(0, 0, 153));
        jLabel7.setText(" PR Tambahan");
        jPanel2.add(jLabel7, new org.netbeans.lib.awtextra.AbsoluteConstraints(170, 10, 100, -1));

        lblPRTambahan.setBackground(new java.awt.Color(255, 255, 0));
        lblPRTambahan.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        lblPRTambahan.setOpaque(true);
        jPanel2.add(lblPRTambahan, new org.netbeans.lib.awtextra.AbsoluteConstraints(150, 10, 20, 14));

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(10, 10, 10)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGap(12, 12, 12))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jScrollPane1)
                        .addGap(12, 12, 12))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGap(10, 10, 10))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jXTitledPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGap(12, 12, 12))))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(11, 11, 11)
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 278, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jXTitledPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, 198, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(23, 23, 23))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnCloseActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCloseActionPerformed
        dispose();
}//GEN-LAST:event_btnCloseActionPerformed

    private void btnSaveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSaveActionPerformed
        udfSave();
}//GEN-LAST:event_btnSaveActionPerformed

    private void btnCtkActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCtkActionPerformed
        if (!tblHeader.getValueAt(tblHeader.getSelectedRow(),0).toString().trim().isEmpty()){
            if (okCtk(tblHeader.getValueAt(tblHeader.getSelectedRow(),0).toString().trim()))
                    printPR(tblHeader.getValueAt(tblHeader.getSelectedRow(),0).toString().trim());
            else
                JOptionPane.showMessageDialog(this,"Acc PR belum lengkap, lengkapi Acc PR terlebih dahulu...!!");
        }
}//GEN-LAST:event_btnCtkActionPerformed

    private void btnFilter1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnFilter1ActionPerformed
        udfLoadPR();
}//GEN-LAST:event_btnFilter1ActionPerformed

    private void formInternalFrameOpened(javax.swing.event.InternalFrameEvent evt) {//GEN-FIRST:event_formInternalFrameOpened
        jLabel16.setText(getTitle());
    }//GEN-LAST:event_formInternalFrameOpened

    private void tblDetailKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_tblDetailKeyPressed
        udfUpdatePR(evt);
    }//GEN-LAST:event_tblDetailKeyPressed

    private void jSlider1StateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_jSlider1StateChanged

        if ((jSlider1.getValue()/60)==0.0){
            jLDetik.setText(String.valueOf(jSlider1.getValue()%60)+" Detik");
        }else{
            jLDetik.setText(String.valueOf(jSlider1.getValue()/60)+" Menit "+String.valueOf(jSlider1.getValue()%60)+" Detik");
        }
        waktuRefresh=Long.valueOf(String.valueOf(jSlider1.getValue()))*1000;
}//GEN-LAST:event_jSlider1StateChanged

    private void jBSetActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jBSetActionPerformed
        //i=0;
        timer.cancel();
        timer = new Timer();
        timer.schedule(new DoTick(), 0, waktuRefresh);
}//GEN-LAST:event_jBSetActionPerformed

    private Boolean okCtk(String sNo_pr){
        boolean ok=false;
        try{
            Statement statt=conn.createStatement();
            ResultSet rss=statt.executeQuery("select (coalesce(acc_level_1,'')<>'' and coalesce(acc_level_2,'')<>'') " +
                    "from phar_pr where no_pr='"+sNo_pr.trim()+"'");
            if (rss.next()) ok=rss.getBoolean(1);
            rss.close();
            statt.close();
        }catch(SQLException se){}
        return ok;
    }
    
    private void printPR(String sNo_PR){
        String usr[][]= new String[2][2];
        byte[] pic1=null,pic2=null;
        String sUser1="", sUser2="";
        setCursor(new Cursor(Cursor.WAIT_CURSOR));
        try{
            int ii=0;
            String sQry="select coalesce(acc_level_1,'') as user1, " +
                    "coalesce(acc_level_2,'') as user2 from phar_pr where no_pr='"+sNo_PR+"'";
            System.out.println(sQry);

            Statement statt=conn.createStatement();
            ResultSet rss=conn.createStatement().executeQuery(sQry);
            if(rss.next()){
                sUser1=rss.getString("user1");
                sUser2=rss.getString("user2");
            }
            rss.close();

            sQry="select ud.username, ac.level, coalesce(ud.complete_name,'') as complete_name,  j.jabatan, "
                    + "ud.ttd_electronic " +
                    "from m_user ud " +
                    "left join m_user_acc ac on ac.user_name=ud.username " +
                    "left join pejabat p on p.kode_jabatan=ud.kode_jabatan " +
                    "where ud.user_name in('"+sUser1+"', '"+sUser2+"') " +
                    "order by level limit 2";
            rss=statt.executeQuery(sQry);

             System.out.println(sQry);

            while (rss.next()) {
                usr[ii]=new String[]{rss.getString("jabatan"),rss.getString("complete_name")};
                if (ii==0)
                    pic1=rss.getBytes("ttd_electronic");
                else pic2=rss.getBytes("ttd_electronic");
                ii++;
            }
            rss.close();
            statt.close();


        }catch(SQLException se){
            setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
            JOptionPane.showMessageDialog(this, se.getMessage());
            return;
        }
      try{
            JasperReport jasperReportmkel = (JasperReport) JRLoader.loadObject(getClass().getResourceAsStream("Reports/PR.jasper"));
            HashMap parameter = new HashMap();
            parameter.put("no_pr",sNo_PR);
            parameter.put("acc_1",usr[0][0]);
            parameter.put("acc_2",usr[1][0]);
            parameter.put("acc_name1",usr[0][1]);
            parameter.put("acc_name2",usr[1][1]);
            parameter.put("img_acc1",pic1==null? null: new ByteArrayInputStream((byte[])pic1));
            parameter.put("img_acc2",pic2==null? null:  new ByteArrayInputStream((byte[])pic2));

            JasperPrint jasperPrintmkel = JasperFillManager.fillReport(jasperReportmkel, parameter, conn);
            jasperPrintmkel.setOrientation(jasperPrintmkel.getOrientationValue());
            JasperViewer.viewReport(jasperPrintmkel, false);
      } catch(JRException je){
          setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
          System.out.println(je.getMessage());
      }
  }


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnClose;
    private javax.swing.JButton btnCtk;
    private javax.swing.JButton btnFilter1;
    private javax.swing.JButton btnSave;
    private javax.swing.JButton jBSet;
    private javax.swing.JFormattedTextField jFTgl;
    private javax.swing.JFormattedTextField jFTglAkhir;
    private javax.swing.JLabel jLDetik;
    private javax.swing.JLabel jLTest;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JSlider jSlider1;
    private org.jdesktop.swingx.JXTitledPanel jXTitledPanel1;
    private javax.swing.JLabel lblPRCito;
    private javax.swing.JLabel lblPRTambahan;
    private org.jdesktop.swingx.JXTable tblDetail;
    private javax.swing.JTable tblHeader;
    private javax.swing.JTextField txtItem;
    // End of variables declaration//GEN-END:variables

    public class MyKeyListener extends KeyAdapter {
        @Override
        public void keyPressed(KeyEvent evt) {
            Component ct = KeyboardFocusManager.getCurrentKeyboardFocusManager().getPermanentFocusOwner();
            int keyKode = evt.getKeyCode();
            switch(keyKode){
               case KeyEvent.VK_ENTER : {
                    if(!(ct instanceof JTable ||ct instanceof JXTable))                    {
                        if (!fn.isListVisible()){
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
                    if(!(ct instanceof JTable ||ct instanceof JXTable))
                        {
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
                    if(!(ct instanceof JTable ||ct instanceof JXTable))
                    {
                        Component c = findPrevFocus();
                        c.requestFocus();
                    }
                    break;
                }
                case KeyEvent.VK_ESCAPE:{
                    if(JOptionPane.showConfirmDialog(null,"Anda Yakin Untuk Keluar?",
                            "SHS Pharmacy",JOptionPane.YES_NO_OPTION)==JOptionPane.YES_OPTION){
                        dispose();
                    }
                    break;
                }
                case KeyEvent.VK_F2:{
                    //udfSave();
                    break;
                }
                case KeyEvent.VK_DELETE:{
                    int iRow=tblHeader.getSelectedRow();
                    if(evt.getSource()!=tblHeader || iRow<0) return;
                    String sNoPR=tblHeader.getValueAt(iRow, 0).toString();
                    if((Boolean)tblHeader.getValueAt(iRow, 6)==false && (Boolean)tblHeader.getValueAt(iRow, 7)==false){
                        if(JOptionPane.showConfirmDialog(null,"Anda Yakin Untuk Membatalkan Semua item " +
                            "dalam PR No. '"+sNoPR+"' ini?",
                            "SHS Pharmacy",JOptionPane.YES_NO_OPTION)==JOptionPane.YES_OPTION){

                            try{
                                conn.setAutoCommit(false);
                                int i=conn.createStatement().executeUpdate("update phar_pr set flag_tr='K' where no_pr='"+sNoPR+"'");
                                conn.setAutoCommit(true);

                                udfLoadPR();
                            }catch(SQLException se){
                                try{
                                    conn.rollback();
                                    conn.setAutoCommit(true);
                                JOptionPane.showMessageDialog(FrmPRApproval.this, se.getMessage());
                                }catch(SQLException s){
                                }
                            }

                        }
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

    private FocusListener txtFocusListener=new FocusListener() {
        public void focusGained(FocusEvent e) {
            if(e.getSource() instanceof JTextField || e.getSource() instanceof JFormattedTextField){
                ((JTextField)e.getSource()).setBackground(Color.YELLOW);

            }
        }


        public void focusLost(FocusEvent e) {
            if(e.getSource().getClass().getSimpleName().equalsIgnoreCase("JTextField")||
                    e.getSource().getClass().getSimpleName().equalsIgnoreCase("JFormattedTextField")){
                ((JTextField)e.getSource()).setBackground(Color.WHITE);


           }
        }
    } ;
}
