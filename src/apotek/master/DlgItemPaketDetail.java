/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package apotek.master;

import apotek.DlgLookupItemJual;
import apotek.dao.ItemDao;
import com.klinik.model.Barang;
import com.klinik.model.BarangPaket;
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
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.swing.AbstractCellEditor;
import javax.swing.JFormattedTextField;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableModel;
import main.GeneralFunction;
import main.MainForm;
import org.jdesktop.swingx.JXTable;

/**
 *
 * @author cak-ust
 */
public class DlgItemPaketDetail extends javax.swing.JDialog {
    DlgLookupItemJual lookupItem=new DlgLookupItemJual(JOptionPane.getFrameForComponent(this), true);
    private DlgItemPaketDetail aThis;
    ItemDao itemDao=new ItemDao();
    GeneralFunction fn=new GeneralFunction(MainForm.conn);
    private String sKodePaket;
    private String sNamaPaket;
    MyKeyListener kListener=new MyKeyListener();
    List<BarangPaket> listItem=new ArrayList<BarangPaket>();
    
    /**
     * Creates new form NewJDialog
     */
    public DlgItemPaketDetail(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
        tblDetail.addKeyListener(kListener);
        btnSave.addKeyListener(kListener);
        btnClose.addKeyListener(kListener);
        tblDetail.getColumn("Qty").setCellEditor(new MyTableCellEditor());
        tblDetail.getColumn("Baseprice").setCellEditor(new MyTableCellEditor());
        lookupItem.setConn(MainForm.conn);
        tblDetail.setRowHeight(22);
        tblDetail.getModel().addTableModelListener(new TableModelListener() {
            public void tableChanged(TableModelEvent e) {
                int colSubTotal=tblDetail.getColumnModel().getColumnIndex("Sub Total");
                if(e.getType()==TableModelEvent.UPDATE && e.getColumn()==tblDetail.getColumnModel().getColumnIndex("Qty") || 
                        e.getType()==TableModelEvent.UPDATE && e.getColumn()==tblDetail.getColumnModel().getColumnIndex("Baseprice")){
                    int iRow=tblDetail.getSelectedRow();
                    int colQty=tblDetail.getColumnModel().getColumnIndex("Qty");
                    int colHarga=tblDetail.getColumnModel().getColumnIndex("Baseprice");
                    tblDetail.setValueAt(fn.udfGetInt(tblDetail.getValueAt(iRow, colQty))*fn.udfGetInt(tblDetail.getValueAt(iRow, colHarga)), iRow, colSubTotal);
                }
                double total=0;
                for(int i=0; i<tblDetail.getRowCount(); i++){
                    total+=fn.udfGetDouble(tblDetail.getValueAt(i, colSubTotal));
                }
                lblTotal.setText(fn.intFmt.format(total));
            }
        });
        for (int i = 0; i < tblDetail.getColumnCount(); i++) {
            tblDetail.getColumnModel().getColumn(i).setCellRenderer(new MyRowRenderer());
        }
    }

    public List<BarangPaket> getListItem() {
        return listItem;
    }
    
    public class MyRowRenderer extends DefaultTableCellRenderer implements TableCellRenderer{
        SimpleDateFormat dmyFmt=new SimpleDateFormat("dd/MM/yyyy");

        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {

            if(value instanceof Date ){
                value=dmyFmt.format(value);
            }else if(value instanceof Double ||value instanceof Integer ||value instanceof Float  ){
                setHorizontalAlignment(SwingConstants.RIGHT);
                value=fn.dFmt.format(value);
            }
            setFont(table.getFont());
            if(isSelected){
                setBackground(table.getSelectionBackground());
                setForeground(table.getSelectionForeground());
            }else{
                setBackground(table.getBackground());
                setForeground(table.getForeground());
            }

            setValue(value);
            return this;
        }
    }
    
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jScrollPane1 = new javax.swing.JScrollPane();
        tblDetail = new javax.swing.JTable();
        jLabel1 = new javax.swing.JLabel();
        lblTotal = new javax.swing.JLabel();
        btnSave = new javax.swing.JButton();
        btnClose = new javax.swing.JButton();
        jLabel2 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowOpened(java.awt.event.WindowEvent evt) {
                formWindowOpened(evt);
            }
        });
        getContentPane().setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        tblDetail.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "No", "Kode Barang", "Nama Barang", "Satuan", "Qty", "Baseprice", "Sub Total"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Integer.class, java.lang.Double.class, java.lang.Double.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, false, true, true, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        tblDetail.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_OFF);
        jScrollPane1.setViewportView(tblDetail);

        getContentPane().add(jScrollPane1, new org.netbeans.lib.awtextra.AbsoluteConstraints(5, 5, 610, 275));

        jLabel1.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel1.setText("Total");
        getContentPane().add(jLabel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(425, 285, 65, 20));

        lblTotal.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        lblTotal.setText("0");
        getContentPane().add(lblTotal, new org.netbeans.lib.awtextra.AbsoluteConstraints(495, 285, 120, 20));

        btnSave.setText("OK");
        btnSave.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSaveActionPerformed(evt);
            }
        });
        getContentPane().add(btnSave, new org.netbeans.lib.awtextra.AbsoluteConstraints(410, 320, 100, 30));

        btnClose.setText("Tutup");
        btnClose.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCloseActionPerformed(evt);
            }
        });
        getContentPane().add(btnClose, new org.netbeans.lib.awtextra.AbsoluteConstraints(513, 320, 100, 30));

        jLabel2.setText("<html>\n<b>Ins</b> &nbsp; : Tambah Item <br>\n<b>Del</b> &nbsp; : Hapus Item <br>\n</html>");
        getContentPane().add(jLabel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 295, 370, 35));

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnSaveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSaveActionPerformed
        TableColumnModel col=tblDetail.getColumnModel();
        listItem.clear();
        for(int i=0; i<tblDetail.getRowCount(); i++){
            BarangPaket b=new BarangPaket();
            b.setItemCode(tblDetail.getValueAt(i, col.getColumnIndex("Kode Barang")).toString());
            b.setJumlah(fn.udfGetInt(tblDetail.getValueAt(i, col.getColumnIndex("Qty"))));
            b.setKodePaket(sKodePaket);
            listItem.add(b);
        }
        this.dispose();
    }//GEN-LAST:event_btnSaveActionPerformed

    private void btnCloseActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCloseActionPerformed
        this.dispose();
    }//GEN-LAST:event_btnCloseActionPerformed

    private void formWindowOpened(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowOpened
        this.aThis=this;
        ((DefaultTableModel)tblDetail.getModel()).setNumRows(0);
        for(BarangPaket x: listItem){
            Barang b=itemDao.getBarangByKode(x.getItemCode(), "KLINIK");
            ((DefaultTableModel)tblDetail.getModel()).addRow(new Object[]{
                tblDetail.getRowCount()+1,
                x.getItemCode(), 
                b.getNamaPaten(), 
                b.getSatuanKecil(), 
                x.getJumlah(), 
                b.getBasePrice(),
                b.getBasePrice()*x.getJumlah(),
            });
        }
        if(listItem.size()>0){
            fn.autoColWidthTable(tblDetail);
        }
    }//GEN-LAST:event_formWindowOpened

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(DlgItemPaketDetail.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(DlgItemPaketDetail.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(DlgItemPaketDetail.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(DlgItemPaketDetail.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        //</editor-fold>

        /* Create and display the dialog */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                DlgItemPaketDetail dialog = new DlgItemPaketDetail(new javax.swing.JFrame(), true);
                dialog.addWindowListener(new java.awt.event.WindowAdapter() {
                    @Override
                    public void windowClosing(java.awt.event.WindowEvent e) {
                        System.exit(0);
                    }
                });
                dialog.setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnClose;
    private javax.swing.JButton btnSave;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JLabel lblTotal;
    private javax.swing.JTable tblDetail;
    // End of variables declaration//GEN-END:variables

    public void setKodePaket(String kode, String nama) {
        this.sKodePaket=kode;
        this.sNamaPaket=nama;
        setTitle("Detail item paket '"+nama+" - "+kode+"'");
    }

    public void setListItem(List<BarangPaket> listPaket) {
        this.listItem=listPaket;
    }

    public class MyKeyListener extends KeyAdapter {
        @Override
        public void keyReleased(KeyEvent evt){
            
        }

        @Override
        public void keyTyped(KeyEvent evt){
//            if(evt.getSource().equals(txtDiskon)){
//                GeneralFunction.keyTyped(evt);
//            }
            
        }

        @Override        public void keyPressed(KeyEvent evt) {
            Component ct = KeyboardFocusManager.getCurrentKeyboardFocusManager().getPermanentFocusOwner();
            int keyKode = evt.getKeyCode();
            switch(keyKode){
                case KeyEvent.VK_INSERT:{
                        
                        lookupItem.clearText();
//                        lookupItem.setKodeGudang(sSiteID);
//                        lookupItem.setJikaExists(false);
                        lookupItem.setAlwaysOnTop(true);
//                        lookupItem.setColumn0Name("Kode");
                        lookupItem.setObjForm(aThis);
                        lookupItem.setSrcTable(tblDetail, tblDetail.getColumnModel().getColumnIndex("Qty"));
                        lookupItem.setKeyEvent(evt);
//                        lookupItem.setNoR(tblHeader.getValueAt(tblHeader.getSelectedRow(), 1).toString()+"#"+tblHeader.getValueAt(tblHeader.getSelectedRow(), 0).toString());
//                        if(tblHeader.getSelectedRow()>=0){
//                            String sFilter=tblHeader.getValueAt(tblHeader.getSelectedRow(), 1).toString()+"#"+tblHeader.getValueAt(tblHeader.getSelectedRow(), 0).toString();
//                            lookupItem.udfSetFilter(sFilter);
//                        }
                        lookupItem.setVisible(true);
                        if(lookupItem.getKodeBarang().length()>0){
                            insertItem();
                        }
                    //}
                    //lookupItem.requestFocusInWindow();
                    break;
                }
                case KeyEvent.VK_F2:{
                    if(btnSave.isEnabled())
                        udfSave();
                    break;
                }
                case KeyEvent.VK_ENTER : {
                    if(!(ct instanceof JTable))                    {
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
                    }else{
//                        if(jTable1.getSelectedColumn()<jTable1.getColumnCount()-1){
//                            jTable1.changeSelection(jTable1.getSelectedRow(), jTable1.getSelectedColumn()+1, false, false);
//                        }
                    }
                    break;
                }
                case KeyEvent.VK_DOWN: {
                    if(!(ct instanceof JTable || ct instanceof JXTable))
                        {
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
                    if(!(ct instanceof JTable || ct instanceof JXTable))
                    {
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
                case KeyEvent.VK_DELETE:{
                    if(evt.getSource().equals(tblDetail))
                        udfDeleteItemDetail();
                    
                    break;
                }
                case KeyEvent.VK_F11:{
                    tblDetail.requestFocusInWindow();
                    tblDetail.requestFocus();
                    tblDetail.changeSelection(tblDetail.getSelectedRow(), tblDetail.getSelectedColumn(), false, false);
                    break;
                }
                case KeyEvent.VK_F12:{
                    tblDetail.requestFocusInWindow();
                    tblDetail.requestFocus();
                    tblDetail.changeSelection(tblDetail.getSelectedRow(), tblDetail.getSelectedColumn(), false, false);
                    break;
                }
                case KeyEvent.VK_ESCAPE:{
                    if(JOptionPane.showConfirmDialog(null,"Anda Yakin Untuk Keluar?",
                            "SHS Pharmacy",JOptionPane.YES_NO_OPTION)==JOptionPane.YES_OPTION){
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
            if(e.getSource() instanceof  JTextField  || e.getSource() instanceof  JFormattedTextField){
                ((JTextField)e.getSource()).setBackground(Color.WHITE);
           }
        }
    } ;
    
    private void insertItem(){
        String item=lookupItem.getKodeBarang();
//        double saldo=itemDao.getSaldo(item, sSiteID);
        
//        if(saldo >0){
            Barang barang=itemDao.getBarangByKode(item, "KLINIK");
            ((DefaultTableModel)tblDetail.getModel()).addRow(new Object[]{
                tblDetail.getRowCount()+1,
                    item,
                    barang.getNamaPaten(), //Nama Barang
                    barang.getSatuanKecil(), //UOM
                    1,  //Qty
                    barang.getBasePrice(),  //Baseprice
                    barang.getBasePrice(),  //Baseprice
                });
                tblDetail.requestFocus();
                if(tblDetail.getRowCount()>0){
                    tblDetail.setRowSelectionInterval(tblDetail.getRowCount()-1, tblDetail.getRowCount()-1);
                    tblDetail.changeSelection(tblDetail.getSelectedRow(), tblDetail.getColumnModel().getColumnIndex("Qty"), 
                            false, false);

                }
                //tblDetail.setValueAt(lookupItem.getKodeBarang(), tblDetail.getSelectedRow(), 0);
//        }else{
//            JOptionPane.showMessageDialog(this, "Saldo Tidak Cukup!\n"
//                    + "Saldo komputer="+saldo);
//        }
    }
    
    public void udfDeleteItemDetail(){
        if(tblDetail.getSelectedRow()>=0){
            int iRow[]= tblDetail.getSelectedRows();
            int rowPalingAtas=iRow[0];

            TableModel tm= tblDetail.getModel();

            while(iRow.length>0) {
                //JOptionPane.showMessageDialog(null, iRow[0]);
                ((DefaultTableModel)tm).removeRow(tblDetail.convertRowIndexToModel(iRow[0]));
                iRow = tblDetail.getSelectedRows();
            }
            tblDetail.clearSelection();

            if(tblDetail.getRowCount()>0 && rowPalingAtas<tblDetail.getRowCount()){
                tblDetail.setRowSelectionInterval(rowPalingAtas, rowPalingAtas);
            }else{
                if(tblDetail.getRowCount()>0)
                    tblDetail.setRowSelectionInterval(rowPalingAtas-1, rowPalingAtas-1);
                else
                    tblDetail.requestFocus();
            }
            if(tblDetail.getSelectedRow()>=0)
                tblDetail.changeSelection(tblDetail.getSelectedRow(), 0, false, false);
        }
    }
    
    private void udfSave(){
        
    }
    
    public class MyTableCellEditor extends AbstractCellEditor implements TableCellEditor {
        private Toolkit toolkit;
        JTextField text=ustTextField;

        int col, row;

        //private NumberFormat nf=NumberFormat.getNumberInstance(Locale.US);

        public Component getTableCellEditorComponent(JTable table, Object value,
            boolean isSelected, int rowIndex, int vColIndex) {
            // 'value' is value contained in the cell located at (rowIndex, vColIndex)

           //text.addKeyListener(kListener);
           //text.setEditable(canEdit);
           col=vColIndex;
           row=rowIndex;
           text.setBackground(new Color(0,255,204));
           text.addFocusListener(txtFocusListener);
           text.addKeyListener(kListener);
           text.setFont(table.getFont());
           text.setName("textEditor");

           text.addKeyListener(new java.awt.event.KeyAdapter() {
                   public void keyTyped(java.awt.event.KeyEvent evt) {
                       fn.keyTyped(evt);
//                      if (col!=0) {
//                          char c = evt.getKeyChar();
//                          if (!((c >= '0' && c <= '9') || c=='.') &&
//                                (c != KeyEvent.VK_BACK_SPACE) &&
//                                (c != KeyEvent.VK_DELETE) &&
//                                (c != KeyEvent.VK_ENTER)) {
//                                getToolkit().beep();
//                                evt.consume();
//                                return;
//                          }
//                       }
                    }
                });
           if (isSelected) {

           }
           //System.out.println("Value dari editor :"+value);
            //text.setText(value==null? "": value.toString());
            //component.setText(df.format(value));

            if(value instanceof Double || value instanceof Float|| value instanceof Integer){
//                try {
                    //Double dVal=Double.parseDouble(value.toString().replace(",",""));
                    double dVal = fn.udfGetDouble(value.toString());
                    text.setText(fn.dFmt.format(dVal));
//                } catch (java.text.ParseException ex) {
//                    //Logger.getLogger(DlgLookupBarang.class.getName()).log(Level.SEVERE, null, ex);
//                }
            }else
                text.setText(value==null? "":value.toString());
           return text;
        }

        public Object getCellEditorValue() {
            Object o="";//=component.getText();
            Object retVal = 0;
            try {
                retVal = fn.udfGetDouble(((JTextField)text).getText());

                o=retVal;

                return o;
            } catch (Exception e) {
                toolkit.beep();
                retVal=0;
            }
            return retVal;
        }

    }

    final JTextField ustTextField = new JTextField() {
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
    
}
