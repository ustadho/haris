/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * FrmAR.java
 *
 * Created on 09 Jan 11, 19:57:31
 */

package apotek;

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Cursor;
import java.awt.FocusTraversalPolicy;
import java.awt.KeyboardFocusManager;
import java.awt.Toolkit;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.beans.PropertyVetoException;
import java.security.Timestamp;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.swing.AbstractCellEditor;
import javax.swing.JCheckBox;
import javax.swing.JFormattedTextField;
import javax.swing.JInternalFrame;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumnModel;
import javax.swing.text.MaskFormatter;
import main.GeneralFunction;

/**
 *
 * @author cak-ust
 */
public class FrmAPJtTempo extends javax.swing.JInternalFrame {
    private Connection conn;
    private GeneralFunction fn=new GeneralFunction();
    MyKeyListener kListener=new MyKeyListener();
    MyTableCellEditor cEditor=new MyTableCellEditor();
    private JFormattedTextField jFDate1;
    private boolean isKoreksi=false;
    private Component aThis;
    private JDesktopImage desktop;
    

    /** Creates new form FrmAR */
    public FrmAPJtTempo() {
        initComponents();
        aThis=this;
        jTable1.getColumn("Bayar").setCellEditor(cEditor);
        jTable1.getColumn("Diskon").setCellEditor(cEditor);
        fn.addKeyListenerInContainer(jPanel1, kListener, txtFocusListener);
        jTable1.addKeyListener(kListener);
        jTable1.getTableHeader().setFont(jTable1.getFont());
        jTable1.setRowHeight(20);

        jTable1.getModel().addTableModelListener(new TableModelListener() {
            public void tableChanged(TableModelEvent e) {
                lblTotBayar.setText("0");
                lblTotJual.setText("0");
                lblTotTerbayar.setText("0");
                lblTotSisa.setText("0");
                lblTotDiskon.setText("0");

                double dNilaiJual=0, dTerbayar=0, dSisa=0, dBayar=0, dDiskon=0;
                TableColumnModel col=jTable1.getColumnModel();

                for(int i=0; i< jTable1.getRowCount(); i++){
                    dNilaiJual+=fn.udfGetDouble(jTable1.getValueAt(i, col.getColumnIndex("Nilai Pembelian")));
                    dTerbayar+=fn.udfGetDouble(jTable1.getValueAt(i, col.getColumnIndex("Terbayar")));
                    dSisa+=fn.udfGetDouble(jTable1.getValueAt(i, col.getColumnIndex("Sisa")));
                    dBayar+=fn.udfGetDouble(jTable1.getValueAt(i, col.getColumnIndex("Bayar")));
                    dDiskon+= fn.udfGetDouble(jTable1.getValueAt(i, col.getColumnIndex("Diskon")));
                }
                lblTotJual.setText(fn.intFmt.format(dNilaiJual));
                lblTotTerbayar.setText(fn.intFmt.format(dTerbayar));
                lblTotSisa.setText(fn.intFmt.format(dSisa));
                lblTotBayar.setText(fn.intFmt.format(dBayar));
                lblTotDiskon.setText(fn.intFmt.format(dDiskon));
            }
        });
        jTable1.getColumn("Tanggal").setCellRenderer(new MyRowRenderer());
        jTable1.getColumn("Jt. Tempo").setCellRenderer(new MyRowRenderer());
        
        jTable1.getSelectionModel().addListSelectionListener(new ListSelectionListener() {

            public void valueChanged(ListSelectionEvent e) {
                btnBayar.setEnabled(jTable1.getSelectedRow()>=0);
            }
        });
    }

    public void setConn(Connection con){
        this.conn=con;
        fn.setConn(conn);
    }

    public void setKoreksi(boolean b){
        isKoreksi=b;
    }
    
    private void udfBayarSupp(){
        if(jTable1.getSelectedRow()<0) return;
        FrmAP f1=new FrmAP();
        if(udfExistForm(f1, "Pembayaran Supplier '"+jTable1.getValueAt(jTable1.getSelectedRow(), 0).toString()+"'")){
            f1.dispose();
            return;
        }
        f1.setTitle("Pembayaran Supplier '"+jTable1.getValueAt(jTable1.getSelectedRow(), 0).toString()+"'");
        f1.setConn(conn);
        f1.setKodeSupp(jTable1.getValueAt(jTable1.getSelectedRow(), 0).toString(), jTable1.getValueAt(jTable1.getSelectedRow(), 1).toString());
        f1.setSrcForm(this);
        f1.setBounds(0, 0, f1.getWidth(), f1.getHeight());
        desktop.add(f1, javax.swing.JLayeredPane.DEFAULT_LAYER);
        f1.setVisible(true);
        try{
            f1.setSelected(true);
            this.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
        } catch(PropertyVetoException PO){

        }
    }

    private boolean udfExistForm(JInternalFrame obj, String sTitle){
        JInternalFrame ji[] = desktop.getAllFrames();
        for(int i=0;i<ji.length;i++){
            if(ji[i].getClass().equals(obj.getClass()) && ji[i].getTitle().equalsIgnoreCase(sTitle)){
                try{
                    ji[i].setSelected(true);
                    return true;
                } catch(PropertyVetoException PO){
                }
                break;
            }
        }

        return false;
    }

    private void udfInitForm(){
        MaskFormatter fmttgl = null;
        try {
            fmttgl = new MaskFormatter("##/##/####");
        } catch (java.text.ParseException e) {}

        try{
            ResultSet rs = conn.createStatement().executeQuery("select current_date ");
            if(rs.next()){
                jXDatePicker1.setDate(rs.getDate(1));
            }

            rs.close();
        }catch(SQLException se){
            JOptionPane.showMessageDialog(this, se.getMessage());
        }
        udfLoadAPJtTempo();
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                jXDatePicker1.requestFocus();
            }
        });


    }

    public void udfLoadAPJtTempo(){
        try{
            ((DefaultTableModel)jTable1.getModel()).setNumRows(0);
            ResultSet rs=conn.createStatement().executeQuery(
                    "select * from fn_show_ap_jt_tempo('', '"+new SimpleDateFormat("yyyy-MM-dd").format(jXDatePicker1.getDate())+"') as " +
                    "(kode_supp varchar, nama_supp varchar, no_receipt varchar, tanggal date, jt_tempo date, " +
                    "total double precision, bayar double precision)");
            while(rs.next()){
                ((DefaultTableModel)jTable1.getModel()).addRow(new Object[]{
                    rs.getString("kode_supp"),
                    rs.getString("nama_supp"),
                    rs.getString("no_receipt"),
                    rs.getDate("tanggal"),
                    rs.getDate("jt_tempo"),
                    rs.getDouble("total"),
                    rs.getDouble("bayar"),
                    rs.getDouble("total")-rs.getDouble("bayar"),
                    0,
                    0
                });
            }
            if(jTable1.getRowCount()>0){
                jTable1.setRowSelectionInterval(0, 0);
                fn.autoResizeColWidth(jTable1, ((DefaultTableModel)jTable1.getModel()));
            }
        }catch(SQLException se){
            JOptionPane.showMessageDialog(this, se.getMessage());
        }
    }

    public void setDesktopPane(JDesktopImage jDesktopPane1) {
        this.desktop =jDesktopPane1;
    }

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
                case KeyEvent.VK_F4:{
                //    udfNew();
                    break;
                }
                case KeyEvent.VK_F5:{
                    //udfSave();
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

                    }
                    break;
                }
                case KeyEvent.VK_DOWN: {
//                    if(ct instanceof JTable){
//                        //if(((JTable)ct).getSelectedRow()==0){
//                            Component c = findNextFocus();
//                            if (c==null) return;
//                            if(c.isEnabled())
//                                c.requestFocus();
//                            else{
//                                c = findNextFocus();
//                                if (c!=null) c.requestFocus();;
//                            }
//                        //}
//                    }else{
                        if (!(ct instanceof JTable) && !fn.isListVisible()){
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
                    //}
                }

                case KeyEvent.VK_UP: {
                    if(ct instanceof JTable){
                        if(((JTable)ct).getSelectedRow()==0){
                            Component c = findPrevFocus();
                            if (c==null) return;
                            if(c.isEnabled())
                                c.requestFocus();
                            else{
                                c = findNextFocus();
                                if (c!=null) c.requestFocus();;
                            }
                        }
                    }else{
                        Component c = findPrevFocus();
                        if (c==null) return;
                        if(c.isEnabled())
                            c.requestFocus();
                        else{
                            c = findNextFocus();
                            if (c!=null) c.requestFocus();;
                        }
                    }
                    break;
                }
//                case KeyEvent.VK_DELETE:{
//                    if(evt.getSource().equals(table) && table.getSelectedRow()>=0){
//                        int iRow[]= table.getSelectedRows();
//                        int rowPalingAtas=iRow[0];
//
//                        TableModel tm= table.getModel();
//
//                        while(iRow.length>0) {
//                            //JOptionPane.showMessageDialog(null, iRow[0]);
//                            ((DefaultTableModel)tm).removeRow(table.convertRowIndexToModel(iRow[0]));
//                            iRow = table.getSelectedRows();
//                        }
//                        table.clearSelection();
//
//                        if(table.getRowCount()>0 && rowPalingAtas<table.getRowCount()){
//                            table.setRowSelectionInterval(rowPalingAtas, rowPalingAtas);
//                        }else{
//                            if(table.getRowCount()>0)
//                                table.setRowSelectionInterval(rowPalingAtas-1, rowPalingAtas-1);
//                            else
//                                table.requestFocus();
//                        }
//                        if(table.getSelectedRow()>=0)
//                            table.changeSelection(table.getSelectedRow(), 0, false, false);
//
//                        if(table.getCellEditor()!=null)
//                            table.getCellEditor().stopCellEditing();
//                    }
//                    break;
//                }
                case KeyEvent.VK_ESCAPE:{
                    dispose();
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
//           text.setVisible(!lookupItem.isVisible());
//            if(lookupItem.isVisible()){
//                return null;
//            }
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
                if(jTable1.getSelectedColumn()==0){
                    retVal = ((JTextField)text).getText();

                }
                else
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
                


           }
        }


    } ;
    SimpleDateFormat dmy = new SimpleDateFormat("dd/MM/yyyy");
    SimpleDateFormat ymd = new SimpleDateFormat("yyyy-MM-dd");

    public class MyRowRenderer extends DefaultTableCellRenderer implements TableCellRenderer{
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
                boolean hasFocus, int row, int column) {
            setFont(table.getFont());
            if(isSelected){
                setBackground(table.getSelectionBackground());
                setForeground(table.getSelectionForeground());
            }else{
                setBackground(table.getBackground());
                setForeground(table.getForeground());

            }
            JCheckBox checkBox = new JCheckBox();
            if(value instanceof Date ){
                value=dmy.format(value);
            }if(value instanceof Timestamp ){
                value=dmy.format(value);
            }else if(value instanceof Double ||value instanceof Integer ||value instanceof Float  ){
                setHorizontalAlignment(SwingConstants.RIGHT);
                value=fn.dFmt.format(value);
            }

            setValue(value);
            return this;
        }
    }

//    private String getMessageBeforeSave(){
//        String sMessage="";
//        if(txtSupplier.getText().trim().length()==0){
//            if(!txtSupplier.isFocusOwner())
//                txtSupplier.requestFocus();
//            return "Silakan isi Supplier terlebih dulu!";
//        }
//        if(jTable1.getRowCount()==0){
//            jTable1.requestFocusInWindow();
//
//            return "Tabel pembayaran supplier masih kosong!";
//        }
//        if(fn.udfGetDouble(lblTotBayar.getText())+fn.udfGetDouble(lblTotDiskon.getText())==0){
//            jTable1.requestFocusInWindow();
//            jTable1.changeSelection(0, 5, false, false);
//            return "Total pembayaran masih kosong!";
//        }
//
//        return sMessage;
//    }
//
//    private void udfSave(){
//        String sMsg=getMessageBeforeSave();
//        if(sMsg.length()>0){
//            JOptionPane.showMessageDialog(this, sMsg);
//            return;
//        }
//        try{
//            setCursor(new Cursor(Cursor.WAIT_CURSOR));
//            ResultSet rs=conn.createStatement().executeQuery("select fn_r_get_ap_no('"+ymd.format(dmy.parse(jFTanggal.getText()))+"')");
//            if(rs.next())
//                txtNoTrx.setText(rs.getString(1));
//            else{
//                rs.close();
//                return;
//            }
//            TableColumnModel col=jTable1.getColumnModel();
//            String sQry="INSERT INTO r_ap(ap_no, tanggal, kode_supp, pay_for, alat_bayar, rate, " +
//                        "memo, date_ins, " +
//                        "user_ins)    VALUES (" +
//                        "'"+txtNoTrx.getText()+"', '"+ymd.format(dmy.parse(jFTanggal.getText()))+"', '"+txtSupplier.getText()+"', '', '1', 1, " +
//                        "'"+txtKeterangan.getText()+"', now()," +
//                        "'"+MainForm.sUserName+"');\n";
//
//            for(int i=0; i<jTable1.getRowCount(); i++){
//                sQry+="INSERT INTO r_ap_detail(ap_no, no_invoice, " +
//                        "bayar, discount, " +
//                        "date_ins) VALUES (" +
//                        "'"+txtNoTrx.getText()+"', '"+jTable1.getValueAt(i, col.getColumnIndex("No. Invoice")).toString()+"', " +
//                        fn.udfGetDouble(jTable1.getValueAt(i, col.getColumnIndex("Bayar")))+", "+fn.udfGetDouble(jTable1.getValueAt(i, col.getColumnIndex("Diskon")))+", " +
//                        "now());\n";
//            }
//            conn.setAutoCommit(false);
//            conn.createStatement().executeUpdate(sQry);
//            conn.setAutoCommit(true);
//            JOptionPane.showMessageDialog(this, "Simpan pembayaran supplier sukses!");
//            udfNew();
//            setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
//        }catch(SQLException se){
//            try {
//                setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
//                conn.rollback();
//                conn.setAutoCommit(true);
//                JOptionPane.showMessageDialog(this, se.getMessage());
//            } catch (SQLException ex) {
//                Logger.getLogger(FrmAPJtTempo.class.getName()).log(Level.SEVERE, null, ex);
//            }
//        }catch(ParseException se){
//            setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
//            JOptionPane.showMessageDialog(this, se.getMessage());
//        }
//    }
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jScrollPane1 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();
        lblTotBayar = new javax.swing.JLabel();
        lblTotal1 = new javax.swing.JLabel();
        lblTotDiskon = new javax.swing.JLabel();
        lblTotSisa = new javax.swing.JLabel();
        lblTotTerbayar = new javax.swing.JLabel();
        lblTotJual = new javax.swing.JLabel();
        lblTotal6 = new javax.swing.JLabel();
        lblTotal7 = new javax.swing.JLabel();
        lblTotal8 = new javax.swing.JLabel();
        lblTotal9 = new javax.swing.JLabel();
        lblTotal10 = new javax.swing.JLabel();
        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jXDatePicker1 = new org.jdesktop.swingx.JXDatePicker();
        btnBayar = new javax.swing.JButton();

        setClosable(true);
        setIconifiable(true);
        setMaximizable(true);
        setResizable(true);
        setTitle("Hutang Jatuh Tempo"); // NOI18N
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

        jScrollPane1.setName("jScrollPane1"); // NOI18N

        jTable1.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jTable1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Kode Supp", "Nama Supplier", "No. Invoice", "Tanggal", "Jt. Tempo", "Nilai Pembelian", "Terbayar", "Sisa", "Bayar", "Diskon", "Keterangan"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Double.class, java.lang.Double.class, java.lang.Double.class, java.lang.Double.class, java.lang.Double.class, java.lang.String.class
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
        jTable1.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_OFF);
        jTable1.setName("jTable1"); // NOI18N
        jScrollPane1.setViewportView(jTable1);

        lblTotBayar.setFont(new java.awt.Font("Tahoma", 1, 12));
        lblTotBayar.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lblTotBayar.setText("0");
        lblTotBayar.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        lblTotBayar.setName("lblTotBayar"); // NOI18N

        lblTotal1.setBackground(new java.awt.Color(153, 204, 255));
        lblTotal1.setFont(new java.awt.Font("Tahoma", 0, 12));
        lblTotal1.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lblTotal1.setText("Diskon");
        lblTotal1.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        lblTotal1.setName("lblTotal1"); // NOI18N
        lblTotal1.setOpaque(true);

        lblTotDiskon.setFont(new java.awt.Font("Tahoma", 1, 12));
        lblTotDiskon.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lblTotDiskon.setText("0");
        lblTotDiskon.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        lblTotDiskon.setName("lblTotDiskon"); // NOI18N

        lblTotSisa.setFont(new java.awt.Font("Tahoma", 1, 12));
        lblTotSisa.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lblTotSisa.setText("0");
        lblTotSisa.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        lblTotSisa.setName("lblTotSisa"); // NOI18N

        lblTotTerbayar.setFont(new java.awt.Font("Tahoma", 1, 12));
        lblTotTerbayar.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lblTotTerbayar.setText("0");
        lblTotTerbayar.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        lblTotTerbayar.setName("lblTotTerbayar"); // NOI18N

        lblTotJual.setFont(new java.awt.Font("Tahoma", 1, 12));
        lblTotJual.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lblTotJual.setText("0");
        lblTotJual.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        lblTotJual.setName("lblTotJual"); // NOI18N

        lblTotal6.setBackground(new java.awt.Color(153, 204, 255));
        lblTotal6.setFont(new java.awt.Font("Tahoma", 1, 14));
        lblTotal6.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lblTotal6.setText("TOTAL  ");
        lblTotal6.setVerticalAlignment(javax.swing.SwingConstants.TOP);
        lblTotal6.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        lblTotal6.setName("lblTotal6"); // NOI18N
        lblTotal6.setOpaque(true);

        lblTotal7.setBackground(new java.awt.Color(153, 204, 255));
        lblTotal7.setFont(new java.awt.Font("Tahoma", 0, 12));
        lblTotal7.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lblTotal7.setText("Terbayar");
        lblTotal7.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        lblTotal7.setName("lblTotal7"); // NOI18N
        lblTotal7.setOpaque(true);

        lblTotal8.setBackground(new java.awt.Color(153, 204, 255));
        lblTotal8.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        lblTotal8.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lblTotal8.setText("Hutang");
        lblTotal8.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        lblTotal8.setName("lblTotal8"); // NOI18N
        lblTotal8.setOpaque(true);

        lblTotal9.setBackground(new java.awt.Color(153, 204, 255));
        lblTotal9.setFont(new java.awt.Font("Tahoma", 0, 12));
        lblTotal9.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lblTotal9.setText("Bayar");
        lblTotal9.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        lblTotal9.setName("lblTotal9"); // NOI18N
        lblTotal9.setOpaque(true);

        lblTotal10.setBackground(new java.awt.Color(153, 204, 255));
        lblTotal10.setFont(new java.awt.Font("Tahoma", 0, 12));
        lblTotal10.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lblTotal10.setText("Nilai Pembelian");
        lblTotal10.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        lblTotal10.setName("lblTotal10"); // NOI18N
        lblTotal10.setOpaque(true);

        jPanel1.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        jPanel1.setName("jPanel1"); // NOI18N
        jPanel1.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel1.setFont(new java.awt.Font("Tahoma", 0, 12));
        jLabel1.setText("Per Tgl.");
        jLabel1.setName("jLabel1"); // NOI18N
        jPanel1.add(jLabel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 10, 80, 20));

        jXDatePicker1.setFont(new java.awt.Font("Tahoma", 0, 12));
        jXDatePicker1.setName("jXDatePicker1"); // NOI18N
        jXDatePicker1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jXDatePicker1ActionPerformed(evt);
            }
        });
        jPanel1.add(jXDatePicker1, new org.netbeans.lib.awtextra.AbsoluteConstraints(70, 10, 160, -1));

        btnBayar.setText("Bayar");
        btnBayar.setName("btnBayar"); // NOI18N
        btnBayar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnBayarActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(10, 10, 10)
                .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, 830, Short.MAX_VALUE)
                .addGap(5, 5, 5))
            .addGroup(layout.createSequentialGroup()
                .addGap(10, 10, 10)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 830, Short.MAX_VALUE)
                .addGap(5, 5, 5))
            .addGroup(layout.createSequentialGroup()
                .addGap(10, 10, 10)
                .addComponent(btnBayar, javax.swing.GroupLayout.DEFAULT_SIZE, 140, Short.MAX_VALUE)
                .addGap(20, 20, 20)
                .addComponent(lblTotal6, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lblTotal10, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblTotJual, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lblTotal7, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblTotTerbayar, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lblTotal8, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblTotSisa, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lblTotal9, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblTotBayar, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lblTotal1, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblTotDiskon, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(75, 75, 75))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(10, 10, 10)
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 250, Short.MAX_VALUE)
                .addGap(10, 10, 10)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(btnBayar, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblTotal6, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(lblTotal10, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(lblTotJual, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(lblTotal7, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(lblTotTerbayar, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(lblTotal8, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(lblTotSisa, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(lblTotal9, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(lblTotBayar, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(lblTotal1, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(lblTotDiskon, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(9, 9, 9))
        );

        java.awt.Dimension screenSize = java.awt.Toolkit.getDefaultToolkit().getScreenSize();
        setBounds((screenSize.width-861)/2, (screenSize.height-398)/2, 861, 398);
    }// </editor-fold>//GEN-END:initComponents

    private void formInternalFrameOpened(javax.swing.event.InternalFrameEvent evt) {//GEN-FIRST:event_formInternalFrameOpened
        udfInitForm();
    }//GEN-LAST:event_formInternalFrameOpened

    private void jXDatePicker1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jXDatePicker1ActionPerformed
        udfLoadAPJtTempo();
    }//GEN-LAST:event_jXDatePicker1ActionPerformed

    private void btnBayarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnBayarActionPerformed
        udfBayarSupp();
    }//GEN-LAST:event_btnBayarActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnBayar;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable jTable1;
    private org.jdesktop.swingx.JXDatePicker jXDatePicker1;
    private javax.swing.JLabel lblTotBayar;
    private javax.swing.JLabel lblTotDiskon;
    private javax.swing.JLabel lblTotJual;
    private javax.swing.JLabel lblTotSisa;
    private javax.swing.JLabel lblTotTerbayar;
    private javax.swing.JLabel lblTotal1;
    private javax.swing.JLabel lblTotal10;
    private javax.swing.JLabel lblTotal6;
    private javax.swing.JLabel lblTotal7;
    private javax.swing.JLabel lblTotal8;
    private javax.swing.JLabel lblTotal9;
    // End of variables declaration//GEN-END:variables

}
