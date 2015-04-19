/*
 * FrmItem.java
 *
 * Created on December 5, 2006, 11:58 AM
 */

package apotek;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.FocusTraversalPolicy;
import java.awt.Font;
import java.awt.KeyboardFocusManager;
import java.awt.Rectangle;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.text.DecimalFormat;
import java.util.Collections;
import java.util.Comparator;
import java.util.Vector;
import javax.swing.DefaultListModel;
import javax.swing.JCheckBox;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumnModel;
import javax.swing.text.DefaultFormatter;
import javax.swing.text.DefaultFormatterFactory;
import javax.swing.text.NumberFormatter;
/**
 *
 * @author  root
 */
public class FrmItem extends javax.swing.JInternalFrame {
    Connection conn;
    Statement st;
    ResultSet rs;
    DefaultTableModel myModel=new DefaultTableModel();
    
    private Color unselectedForeground; 
    private Color unselectedBackground;
    public boolean bAsc=false;
    static String sID;
    static String sNow="";
    
    static boolean isFormItemOn;

    private DefaultListModel cmbModel;
    
    
    public boolean getAsc(){
        if (!bAsc)
            bAsc=true;
        else
            bAsc=false;
        return bAsc;               
    }
    /** Creates new form FrmItem */
    public FrmItem(Connection nCon) {
        initComponents();
        conn=nCon;
        
    }
    
    public class MyRowRenderer extends DefaultTableCellRenderer implements TableCellRenderer{
        JCheckBox checkBox = new JCheckBox();
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            
            if((column==0)||(column==1)||(column==2)||(column==3)||(column==6)||(column==7)||(column==9)){
                JTextField jt= new JTextField();
                setHorizontalAlignment(jt.LEFT);
            }
            
            
            if(value instanceof Float ||value instanceof Double ){
                DefaultFormatter fmt = new NumberFormatter(new DecimalFormat("#,###,###"));
                DefaultFormatterFactory fmtFactory = new DefaultFormatterFactory(fmt, fmt, fmt);
                JFormattedTextField ft =new JFormattedTextField();
                ft.setFormatterFactory(fmtFactory);
                ft.setValue(Double.valueOf(value.toString()));                
                setValue(ft.getText());
                setHorizontalAlignment(ft.RIGHT);
            }else{
                setValue(value);
            }
            
            
            
            Color g1 = new Color(230,243,255);//[251,251,235]
            Color g2 = new Color(219,238,255);//[247,247,218]
            
            Color w1 = new Color(255,255,255);
            Color w2 = new Color(250,250,250);
            
            Color h1 = new Color(255,240,240);
            Color h2 = new Color(250,230,230);
            
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
            
             if (value instanceof Boolean) { // Boolean
                  checkBox.setSelected(((Boolean) value).booleanValue());
                  checkBox.setHorizontalAlignment(JLabel.CENTER);
                  if (row%2==0){
                     checkBox.setBackground(w);
                  }else{
                     checkBox.setBackground(g);
                  }
//                  if(isSelected && column==10){
//                        //setBackground(new Color(248,255,167));//[174,212,254]
//                        checkBox.setBackground(new Color(69,167,14));
//                        checkBox.setForeground(new Color(51,102,255));
//                  }
                  //else 
                      if (isSelected){
                            checkBox.setBackground(new Color(248,255,167));//51,102,255));
                            //checkBox.setForeground(new Color(255,255,255));
                        }
                  
                  return checkBox;
            }
            
            if (row%2==0){
                setBackground(w);
            }else{
                setBackground(g);
            }
            
            if(isSelected){
                setBackground(new Color(248,255,167));//[174,212,254]
            }
            
            setFont(new Font("Tahoma", 0,12));
             if (value instanceof Boolean) { // Boolean
                  checkBox.setSelected(((Boolean) value).booleanValue());
                  checkBox.setHorizontalAlignment(JLabel.CENTER);
//                  if (row%2==0){
//                     checkBox.setBackground(w);
//                  }else{
//                     checkBox.setBackground(g);
//                  }
//                  if(isSelected){
//                        //setBackground(new Color(248,255,167));//[174,212,254]
//                        checkBox.setBackground(new Color(69,167,14));
//                        checkBox.setForeground(new Color(51,102,255));
//                  }else if (isSelected){
//                            checkBox.setBackground(new Color(51,102,255));
//                            checkBox.setForeground(new Color(255,255,255));
//                        }
                  
                  return checkBox;
            }
            return this;
        }
    }
    
    public class myColHeaderList extends MouseAdapter{
            public void mouseClicked(MouseEvent evt) {
            JTable table = ((JTableHeader)evt.getSource()).getTable();
            TableColumnModel colModel = table.getColumnModel();
    
            // The index of the column whose header was clicked
            int vColIndex = colModel.getColumnIndexAtX(evt.getX());
            int mColIndex = table.convertColumnIndexToModel(vColIndex);
    
            // Return if not clicked on any column header
            if (vColIndex == -1) {
                return;
            }
            boolean bSt;
            bSt=getAsc();
            sortAllRowsBy(myModel, vColIndex, bSt);
            // Determine if mouse was clicked between column heads
            Rectangle headerRect = table.getTableHeader().getHeaderRect(vColIndex);
            if (vColIndex == 0) {
                headerRect.width -= 3;    // Hard-coded constant
            } else {
                headerRect.grow(-3, 0);   // Hard-coded constant
            }
            if (!headerRect.contains(evt.getX(), evt.getY())) {
                // Mouse was clicked between column heads
                // vColIndex is the column head closest to the click
    
                // vLeftColIndex is the column head to the left of the click
                int vLeftColIndex = vColIndex;
                if (evt.getX() < headerRect.x) {
                    vLeftColIndex--;
                }                
            }
        }
    }
    
    public void sortAllRowsBy(DefaultTableModel model, int colIndex, boolean ascending) {
        Vector data = model.getDataVector();
        Collections.sort(data, new ColumnSorter(colIndex, ascending));
        model.fireTableStructureChanged();
        tableLook();
    }
    
    public class ColumnSorter implements Comparator {
        int colIndex;
        boolean ascending;
        ColumnSorter(int colIndex, boolean ascending) {
            this.colIndex = colIndex;
            this.ascending = ascending;
        }
        public int compare(Object a, Object b) {
            Vector v1 = (Vector)a;
            Vector v2 = (Vector)b;
            Object o1 = v1.get(colIndex);
            Object o2 = v2.get(colIndex);
    
            // Treat empty strains like nulls
            if (o1 instanceof String && ((String)o1).length() == 0) {
                o1 = null;
            }
            if (o2 instanceof String && ((String)o2).length() == 0) {
                o2 = null;
            }
    
            // Sort nulls so they appear last, regardless
            // of sort order
            if (o1 == null && o2 == null) {
                return 0;
            } else if (o1 == null) {
                return 1;
            } else if (o2 == null) {
                return -1;
            } else if (o1 instanceof Comparable) {
                if (ascending) {
                    return ((Comparable)o1).compareTo(o2);
                } else {
                    return ((Comparable)o2).compareTo(o1);
                }
            } else {
                if (ascending) {
                    return o1.toString().compareTo(o2.toString());
                } else {
                    return o2.toString().compareTo(o1.toString());
                }
            }
        }
    } 
    
    private void pesanError(String Err){
        JOptionPane.showMessageDialog(this,Err,"Message",JOptionPane.ERROR_MESSAGE);
    }
    
    private void onOpen(String sQry){
        //myModel = (DefaultTableModel) tblItem.getModel();
        int i=0;
        try {
            
            myModel.setNumRows(0);
            
            Statement st = conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY);
            ResultSet rs = st.executeQuery(sQry);
 
            while (rs.next()) {
                    myModel.addRow(new Object[]{rs.getString("item_code"),
                                            rs.getString("item_name"),
                                            rs.getString("jenis_barang"),
                                            rs.getString("keterangan"),
                                            rs.getFloat("min"),
                                            rs.getFloat("max"),
                                            rs.getString("satuan_besar"),
                                            rs.getFloat("harga_besar_resep"),
                                            rs.getFloat("harga_besar_non_resep"),
                                            rs.getString("satuan_kecil"),
                                            rs.getFloat("harga_kecil_resep"),
                                            rs.getFloat("harga_kecil_non_resep"),
                                            rs.getString("nama_lokasi"),
                                            rs.getBoolean("status_pakai"),
                                            });
            }
            
            /*if (rs.)
                tblStatusKamar.setColumnSelectionInterval(ii,ii);
                rect=tblStatusKamar.getCellRect(jj,ii,false);
                tblStatusKamar.scrollRectToVisible(rect);
                tblStatusKamar.revalidate();
                //tblStatusKamar.setRowSelectionInterval(tblStatusKamar.getRowCount()-1,tblStatusKamar.getRowCount()-1);
                tblStatusKamar.requestFocusInWindow();
            */
            if(tblItem.getRowCount()>0){
                tblItem.setRowSelectionInterval(0, 0);
                
            }
            rs.close();
            st.close();
        } catch (SQLException eswl){ System.out.println(eswl.getMessage());}
        if(i>0){
            tblItem.requestFocusInWindow();
            tblItem.setRowSelectionInterval(0,0);
        }
   }
    
    public class MyKeyListener extends KeyAdapter {
        public void keyPressed(KeyEvent evt) {
            int keyKode = evt.getKeyCode();
            switch(keyKode){
                case KeyEvent.VK_F3: {  //Search
                    btnSearch.requestFocus();
                    break;
                }
                
                case KeyEvent.VK_F6: {  //Filter
                    btnFilter.requestFocus();
                    break;
                }
                
                case KeyEvent.VK_ENTER : {
                    
                    break;
                }
                
                case KeyEvent.VK_UP: {
                    Component ct = KeyboardFocusManager.getCurrentKeyboardFocusManager().getPermanentFocusOwner();
                    if(!(ct.getClass().getSimpleName().equalsIgnoreCase("JTABLE")))
                    {    
                        Component c = findPrevFocus();
                        c.requestFocus();
                    }
                    break;
                }
                
                //lempar aja ke udfCancel
                case KeyEvent.VK_ESCAPE: {
                    if(JOptionPane.showConfirmDialog(null,"Anda yakin keluar?","SHS go Open Sources",JOptionPane.YES_NO_OPTION)==JOptionPane.YES_OPTION){
                       dispose();
                    }
                 }
            }
        }
    }
    
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
//            if (lst.isVisible())
//                lst.setVisible(false);
//            
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
    
   private void tableLook(){
        tblItem.getColumnModel().getColumn(0).setMaxWidth(80);   //kode barang
        tblItem.getColumnModel().getColumn(0).setPreferredWidth(80);
        tblItem.getColumnModel().getColumn(1).setMaxWidth(250);      //nama_barang
        tblItem.getColumnModel().getColumn(1).setPreferredWidth(250);
        tblItem.getColumnModel().getColumn(2).setMaxWidth(100);      //jenis_barang
        tblItem.getColumnModel().getColumn(2).setPreferredWidth(100);
        tblItem.getColumnModel().getColumn(3).setMaxWidth(300);     //keterangan
        tblItem.getColumnModel().getColumn(3).setPreferredWidth(300);
        tblItem.getColumnModel().getColumn(4).setMaxWidth(80);      //min
        tblItem.getColumnModel().getColumn(4).setPreferredWidth(80);
        tblItem.getColumnModel().getColumn(5).setMaxWidth(80);      //max
        tblItem.getColumnModel().getColumn(5).setPreferredWidth(80);
        tblItem.getColumnModel().getColumn(6).setMaxWidth(120);      //unit
        tblItem.getColumnModel().getColumn(6).setPreferredWidth(120);
        tblItem.getColumnModel().getColumn(7).setMaxWidth(80);      //uom
        tblItem.getColumnModel().getColumn(7).setPreferredWidth(80);
        tblItem.getColumnModel().getColumn(8).setMaxWidth(100);      //discontinued
        tblItem.getColumnModel().getColumn(8).setPreferredWidth(100);
        tblItem.getColumnModel().getColumn(9).setMaxWidth(110);      //barcode
        tblItem.getColumnModel().getColumn(9).setPreferredWidth(110);
        tblItem.getColumnModel().getColumn(13).setPreferredWidth(50);
        


        tblItem.setRowHeight(20);

        for (int i=0;i<tblItem.getColumnCount();i++){
                tblItem.getColumnModel().getColumn(i).setCellRenderer(new MyRowRenderer());
        }
        
        if(tblItem.getRowCount()>0)
        {
            tblItem.setRowSelectionInterval(0,0 );
            tblItem.changeSelection(0, 0, false, true);
        }   
   }
    
    private void udfEditItem(){
        FrmItemMaster f1=new FrmItemMaster();
        
        if(!isFormItemOn){
            f1.setKodeBarang(tblItem.getValueAt(tblItem.getSelectedRow(),0).toString());
            f1.setConnection(conn);
            f1.setBEdit(true);
            f1.setBSaved(true);
            f1.udfSetModel(myModel);
            f1.udfSetRowPos(tblItem.getSelectedRow());
            f1.udfSetTable(tblItem);
            f1.setBNew(false);
            f1.setEnabledKode(false);
            
            f1.setVisible(true);
            f1.udfSetNamaDepan(tblItem.getValueAt(tblItem.getSelectedRow(),0).toString().substring(0,1));
            isFormItemOn=true;
        }
        f1.setFocusable(true);
    }
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel2 = new javax.swing.JPanel();
        jToolBar2 = new javax.swing.JToolBar();
        jLabel1 = new javax.swing.JLabel();
        cmbFilter = new javax.swing.JComboBox();
        cmbOperator = new javax.swing.JComboBox();
        txtFilter = new javax.swing.JTextField();
        btnFilter = new javax.swing.JButton();
        jLabel2 = new javax.swing.JLabel();
        txtSearch = new javax.swing.JTextField();
        btnSearch = new javax.swing.JButton();
        jToolBar1 = new javax.swing.JToolBar();
        btnNew = new javax.swing.JButton();
        btnEdit = new javax.swing.JButton();
        btnDelete = new javax.swing.JButton();
        btnClose = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        tblItem = new javax.swing.JTable();

        setClosable(true);
        setIconifiable(true);
        setMaximizable(true);
        setResizable(true);
        setTitle("Item List");
        addInternalFrameListener(new javax.swing.event.InternalFrameListener() {
            public void internalFrameActivated(javax.swing.event.InternalFrameEvent evt) {
            }
            public void internalFrameClosed(javax.swing.event.InternalFrameEvent evt) {
                formInternalFrameClosed(evt);
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

        jPanel2.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        jPanel2.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel1.setText("Filter    ");
        jToolBar2.add(jLabel1);

        cmbFilter.setMaximumSize(new java.awt.Dimension(150, 24));
        cmbFilter.setMinimumSize(new java.awt.Dimension(20, 24));
        cmbFilter.setPreferredSize(new java.awt.Dimension(20, 24));
        jToolBar2.add(cmbFilter);

        cmbOperator.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "=", ">", ">=", "<", "<=", "Like" }));
        cmbOperator.setMaximumSize(new java.awt.Dimension(150, 24));
        cmbOperator.setMinimumSize(new java.awt.Dimension(20, 24));
        cmbOperator.setPreferredSize(new java.awt.Dimension(20, 24));
        jToolBar2.add(cmbOperator);

        txtFilter.setMaximumSize(new java.awt.Dimension(200, 24));
        txtFilter.setMinimumSize(new java.awt.Dimension(4, 4));
        txtFilter.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txtFilterFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                txtFilterFocusLost(evt);
            }
        });
        jToolBar2.add(txtFilter);

        btnFilter.setIcon(new javax.swing.ImageIcon(getClass().getResource("/image/Icon/Filter.png"))); // NOI18N
        btnFilter.setToolTipText("Fiter      (F6)");
        btnFilter.setBorder(null);
        btnFilter.setMaximumSize(new java.awt.Dimension(40, 40));
        btnFilter.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnFilterActionPerformed(evt);
            }
        });
        jToolBar2.add(btnFilter);

        jLabel2.setText("     Searching ");
        jToolBar2.add(jLabel2);

        txtSearch.setMaximumSize(new java.awt.Dimension(200, 24));
        txtSearch.setMinimumSize(new java.awt.Dimension(4, 4));
        txtSearch.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txtSearchFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                txtSearchFocusLost(evt);
            }
        });
        txtSearch.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txtSearchKeyPressed(evt);
            }
        });
        jToolBar2.add(txtSearch);

        btnSearch.setIcon(new javax.swing.ImageIcon(getClass().getResource("/image/Icon/Search.png"))); // NOI18N
        btnSearch.setToolTipText("Search     (F3)");
        btnSearch.setBorder(null);
        btnSearch.setMaximumSize(new java.awt.Dimension(40, 40));
        btnSearch.setMinimumSize(new java.awt.Dimension(40, 40));
        btnSearch.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSearchActionPerformed(evt);
            }
        });
        jToolBar2.add(btnSearch);

        jPanel2.add(jToolBar2, new org.netbeans.lib.awtextra.AbsoluteConstraints(217, 0, 600, 50));

        btnNew.setIcon(new javax.swing.ImageIcon(getClass().getResource("/image/Icon/New.png"))); // NOI18N
        btnNew.setToolTipText("New     (F5)");
        btnNew.setBorder(null);
        btnNew.setMaximumSize(new java.awt.Dimension(40, 40));
        btnNew.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnNewActionPerformed(evt);
            }
        });
        jToolBar1.add(btnNew);

        btnEdit.setIcon(new javax.swing.ImageIcon(getClass().getResource("/image/Icon/Edit.png"))); // NOI18N
        btnEdit.setToolTipText("Edit     (F4)");
        btnEdit.setBorder(null);
        btnEdit.setMaximumSize(new java.awt.Dimension(40, 40));
        btnEdit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnEditActionPerformed(evt);
            }
        });
        jToolBar1.add(btnEdit);

        btnDelete.setIcon(new javax.swing.ImageIcon(getClass().getResource("/image/Icon/Delete.png"))); // NOI18N
        btnDelete.setToolTipText("New     (F12)");
        btnDelete.setBorder(null);
        btnDelete.setMaximumSize(new java.awt.Dimension(40, 40));
        btnDelete.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnDeleteActionPerformed(evt);
            }
        });
        jToolBar1.add(btnDelete);

        btnClose.setIcon(new javax.swing.ImageIcon(getClass().getResource("/image/Icon/Exit.png"))); // NOI18N
        btnClose.setToolTipText("New     (F12)");
        btnClose.setBorder(null);
        btnClose.setMaximumSize(new java.awt.Dimension(40, 40));
        btnClose.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCloseActionPerformed(evt);
            }
        });
        jToolBar1.add(btnClose);

        jPanel2.add(jToolBar1, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 213, 50));

        jScrollPane1.setBackground(new java.awt.Color(255, 255, 255));

        tblItem.setAutoCreateRowSorter(true);
        tblItem.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, null, null, null, null, null, null, null, null, null}
            },
            new String [] {
                "Kode Item", "Nama Item Barang", "Jenis Barang", "Keterangan", "Min. Stock", "max. Stock", "Satuan Besar", "Hrg. Bsr Resep", "Hrg. Bsr Non Rsp", "Satuan Kecil", "Hrg. Kcl Resep", "Hrg. Kcl Non Rsp", "Lokasi", "Active"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.Float.class, java.lang.Float.class, java.lang.String.class, java.lang.Float.class, java.lang.Float.class, java.lang.String.class, java.lang.Float.class, java.lang.Float.class, java.lang.String.class, java.lang.Boolean.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false, false, false, false, false, false, false, false, false
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

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, layout.createSequentialGroup()
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                    .add(layout.createSequentialGroup()
                        .add(5, 5, 5)
                        .add(jScrollPane1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 840, Short.MAX_VALUE))
                    .add(jPanel2, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 845, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .add(jPanel2, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 57, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jScrollPane1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 569, Short.MAX_VALUE)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void txtFilterFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtFilterFocusGained
        txtSearch.setText("");
    }//GEN-LAST:event_txtFilterFocusGained

    private void txtSearchFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtSearchFocusGained
        txtFilter.setText("");
    }//GEN-LAST:event_txtSearchFocusGained

    private void txtSearchFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtSearchFocusLost
        
    }//GEN-LAST:event_txtSearchFocusLost

    private void txtFilterFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtFilterFocusLost
        
    }//GEN-LAST:event_txtFilterFocusLost

    private void txtFilterKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtFilterKeyPressed
        if(evt.getKeyCode()==KeyEvent.VK_ENTER)
            udfFilter();
    }//GEN-LAST:event_txtFilterKeyPressed

    private void txtSearchKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtSearchKeyPressed
        if(evt.getKeyCode()==KeyEvent.VK_ENTER)
            udfSearch();
    }//GEN-LAST:event_txtSearchKeyPressed

    private void tblItemKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_tblItemKeyPressed
        if(evt.getKeyCode()==KeyEvent.VK_ENTER){
            udfEditItem();
            tblItem.setRowSelectionInterval(tblItem.getSelectedRow(), tblItem.getSelectedRow());
        }
    }//GEN-LAST:event_tblItemKeyPressed

    private void btnCloseActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCloseActionPerformed
        this.dispose();
    }//GEN-LAST:event_btnCloseActionPerformed

    private void btnDeleteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnDeleteActionPerformed
        int yesNo = JOptionPane.showConfirmDialog(null,"Anda YAKIN untuk MENGHAPUS item ini?","SHS Go Open Source",JOptionPane.YES_NO_OPTION,JOptionPane.QUESTION_MESSAGE);
        if(yesNo == JOptionPane.YES_OPTION){
            try{
                conn.setAutoCommit(false);
                ItemBean iB=new ItemBean();
                iB.setConn(conn);
                int i=iB.DeleteItem(tblItem.getValueAt(tblItem.getSelectedRow(),0).toString().trim());
                
                if (i>0){
                    myModel.removeRow(tblItem.getSelectedRow());
                    conn.commit();
                    conn.setAutoCommit(true);
                }
                
            }catch(SQLException se){
                try{
                    //System.out.println(se.getMessage());
                    pesanError(se.getMessage());
                    conn.rollback();
                    conn.setAutoCommit(true);
                }catch(SQLException se2){
                    //System.out.println(se2.getMessage());
                    pesanError(se2.getMessage());
                }
            }
        }
    }//GEN-LAST:event_btnDeleteActionPerformed

    private void btnEditActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnEditActionPerformed
        if (tblItem.getValueAt(tblItem.getSelectedRow(),0).toString().length()>0)
            udfEditItem();
    }//GEN-LAST:event_btnEditActionPerformed

    private void btnNewActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnNewActionPerformed
        FrmItemMaster fMaster=new FrmItemMaster();
        fMaster.setConnection(conn);
        fMaster.setBNew(true);
        fMaster.setBEdit(false);
        fMaster.udfSetModel(myModel);
        fMaster.udfSetTable(tblItem);
        fMaster.udfSetRowPos(tblItem.getSelectedRow());
        fMaster.setVisible(true);
        
    }//GEN-LAST:event_btnNewActionPerformed

    private void formInternalFrameClosed(javax.swing.event.InternalFrameEvent evt) {//GEN-FIRST:event_formInternalFrameClosed
        
    }//GEN-LAST:event_formInternalFrameClosed

    private void formInternalFrameOpened(javax.swing.event.InternalFrameEvent evt) {//GEN-FIRST:event_formInternalFrameOpened
        udfSetKEyListener();
        
        String sQry="select item_code, item_name, coalesce(discontinued,false) as status_pakai, " +
                "coalesce(b.kode_jenis,'') as kode_jenis ," +
                "coalesce(jenis_barang,'') as jenis_barang, " +
                "coalesce(keterangan,'') as keterangan , coalesce(min_stock,0) as min, coalesce(max_stock,0) as max, " +
                "satuan_besar, harga_besar_resep, harga_besar_non_resep, satuan_kecil, harga_kecil_resep, harga_kecil_non_resep," +
                "coalesce(b.kode_lokasi,'') as kode_lokasi, coalesce(l.nama_lokasi,'') as nama_lokasi " +
                "from barang b " +
                "left join jenis_barang jb on jb.kode_jenis=b.kode_jenis " +
                "left join lokasi l using(kode_lokasi)" +
                "left join gudang g on g.kode_gudang=b.gudang ORDER BY 1";
        
        System.out.println(sQry);
        //this.setBounds(0,0,1000,800);
        try {
            st = conn.createStatement();
            rs = st.executeQuery(sQry);
            System.out.println(sQry);
            
            cmbModel=new DefaultListModel();
            cmbModel.clear();
            
            int jmlKolom=rs.getMetaData().getColumnCount();
            
            cmbFilter.addItem("All");           cmbModel.add(0, "all");
            cmbFilter.addItem("Kode");          cmbModel.add(1, "item_code");
            cmbFilter.addItem("Nama Barang");   cmbModel.add(2, "item_name");
            cmbFilter.addItem("Kode Jenis");    cmbModel.add(3, "b.kode_jenis");
            cmbFilter.addItem("Jenis");         cmbModel.add(4, "jenis_barang");
            
            myModel=(DefaultTableModel)tblItem.getModel();
            tblItem.setModel(myModel);
            
            tableLook();
            myModel.setNumRows(0);
            while (rs.next()) {
                myModel.addRow(new Object[]{rs.getString("item_code"),
                                            rs.getString("item_name"),
                                            rs.getString("jenis_barang"),
                                            rs.getString("keterangan"),
                                            rs.getFloat("min"),
                                            rs.getFloat("max"),
                                            rs.getString("satuan_besar"),
                                            rs.getFloat("harga_besar_resep"),
                                            rs.getFloat("harga_besar_non_resep"),
                                            rs.getString("satuan_kecil"),
                                            rs.getFloat("harga_kecil_resep"),
                                            rs.getFloat("harga_kecil_non_resep"),
                                            rs.getString("nama_lokasi"),
                                            rs.getBoolean("status_pakai"),
                                            });
            }
            
            tblItem.setRequestFocusEnabled(true);
            
            if (myModel.getRowCount() > 0) {
                tblItem.setRowSelectionInterval(0, 0);
            }
            
            rs.close();
            st.close();
            // rs.first();
        
            if (myModel.getRowCount() > 0) {
                JTableHeader header = tblItem.getTableHeader();
                Font fH;
                fH=new Font("Tahoma", 0,12);
                header.setFont(fH);

                header.setBackground((new Color(234,243,244)));
                header.addMouseListener(new myColHeaderList());
            }
            
        } catch(SQLException se) {
            System.out.println(se.getMessage());
        }
    }//GEN-LAST:event_formInternalFrameOpened

    private void btnSearchActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSearchActionPerformed
        udfSearch();
    }//GEN-LAST:event_btnSearchActionPerformed

    private void btnFilterActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnFilterActionPerformed
        udfFilter();
    }//GEN-LAST:event_btnFilterActionPerformed

    private void tblItemMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tblItemMouseClicked
        if (evt.getClickCount()==2 && tblItem.getValueAt(tblItem.getSelectedRow(),0).toString().length()>0){
            udfEditItem();
        }
    }//GEN-LAST:event_tblItemMouseClicked

    
    
    private void udfSetKEyListener() {
        for(int i=0;i<jToolBar2.getComponentCount();i++){
            Component c = jToolBar2.getComponent(i);
            if(c.getClass().getSimpleName().equalsIgnoreCase("JTEXTFIELD")    || c.getClass().getSimpleName().equalsIgnoreCase("JFormattedTextField")
            || c.getClass().getSimpleName().equalsIgnoreCase("JTextArea") || c.getClass().getSimpleName().equalsIgnoreCase("JComboBox")
            || c.getClass().getSimpleName().equalsIgnoreCase("JButton") || c.getClass().getSimpleName().equalsIgnoreCase("JCheckBox")) {
                //System.out.println(c.getClass().getSimpleName());
                c.addKeyListener(new MyKeyListener());
            }
        }
        for(int i=0;i<jToolBar1.getComponentCount();i++){
            Component c = jToolBar1.getComponent(i);
            if(c.getClass().getSimpleName().equalsIgnoreCase("JTEXTFIELD")    || c.getClass().getSimpleName().equalsIgnoreCase("JFormattedTextField")
            || c.getClass().getSimpleName().equalsIgnoreCase("JTextArea") || c.getClass().getSimpleName().equalsIgnoreCase("JComboBox")
            || c.getClass().getSimpleName().equalsIgnoreCase("JButton") || c.getClass().getSimpleName().equalsIgnoreCase("JCheckBox")) {
                //System.out.println(c.getClass().getSimpleName());
                c.addKeyListener(new MyKeyListener());
            }
        }
         
         jScrollPane1.addKeyListener(new MyKeyListener());
         tblItem.addKeyListener(new MyKeyListener());
         
        System.out.println(jPanel2.getComponentCount());
        for(int i=0;i<jPanel2.getComponentCount();i++){
            Component c = jPanel2.getComponent(i);
            if(c.getClass().getSimpleName().equalsIgnoreCase("JTEXTFIELD")    || c.getClass().getSimpleName().equalsIgnoreCase("JFormattedTextField")
            || c.getClass().getSimpleName().equalsIgnoreCase("JTextArea") || c.getClass().getSimpleName().equalsIgnoreCase("JComboBox")
            || c.getClass().getSimpleName().equalsIgnoreCase("JButton")   || c.getClass().getSimpleName().equalsIgnoreCase("JCheckBox")) {
                //System.out.println(c.getClass().getSimpleName());
                c.addKeyListener(new MyKeyListener());
            }
        }
    }

    private void udfSearch() {
//        String sQry="select * from fn_phar_barang_list(0, 'all', '', " +
//            "'"+txtSearch.getText().toUpperCase()+"')as (kode_barang varchar, " +
//            "nama_barang varchar, jenis_barang varchar, keterangan varchar, min float4, max float4, unit varchar, " +
//            "uom varchar, discontinued bool, barcode varchar) ";

        String sQry="select item_code, item_name, coalesce(discontinued,false) as status_pakai, " +
                "coalesce(b.kode_jenis,'') as kode_jenis ," +
                "coalesce(jenis_barang,'') as jenis_barang, " +
                "coalesce(keterangan,'') as keterangan , coalesce(min_stock,0) as min, coalesce(max_stock,0) as max, " +
                "satuan_besar, harga_besar_resep, harga_besar_non_resep, satuan_kecil, harga_kecil_resep, harga_kecil_non_resep," +
                "coalesce(b.kode_lokasi,'') as kode_lokasi, coalesce(l.nama_lokasi,'') as nama_lokasi " +
                "from barang b " +
                "left join jenis_barang jb on jb.kode_jenis=b.kode_jenis " +
                "left join lokasi l using(kode_lokasi)" +
                "left join gudang g on g.kode_gudang=b.gudang " +
                "Where upper(item_Code||coalesce(item_name,'')||coalesce(jenis_barang,'')||coalesce(satuan_besar,'')||coalesce(satuan_kecil,'')) iLike '%"+txtSearch.getText()+"%'" +
                "ORDER BY 1";
        
        
        onOpen(sQry);
        tblItem.setFocusable(true);
        txtSearch.requestFocus();
    }

    private void udfFilter() {
        /*String sQry="select * from fn_phar_barang_list(1, '"+cmbFilter.getSelectedItem().toString()+"', " +
                    "'"+cmbOperator.getSelectedItem().toString()+"', '"+txtFilter.getText().toUpperCase()+"')as (kode_barang varchar, " +
                    "nama_barang varchar, jenis_barang varchar, keterangan varchar, min float4, max float4, unit varchar, " +
                    "uom varchar, discontinued bool, barcode varchar) ";
        */
       String sField="";
       
       switch(cmbFilter.getSelectedIndex()){
           case 0:{
               sField= "Where upper(item_Code||coalesce(item_name,'')||coalesce(jenis_barang,'')) ";
               break;
           }
           case 1:{
               sField= " item_code ";
               break;
           }
           case 2:{
               sField= " item_name ";
               break;
           }
           case 3:{
               sField= " b.kode_jenis ";
               break;
           }
           case 4:{
               sField= " jenis_barang ";
               break;
           }
           }
           
       String sPersen=cmbOperator.getSelectedIndex()==5? "%":"";
       
       String sQry="select item_code, item_name, coalesce(discontinued,false) as status_pakai, " +
                "coalesce(b.kode_jenis,'') as kode_jenis ," +
                "coalesce(jenis_barang,'') as jenis_barang, " +
                "coalesce(keterangan,'') as keterangan , coalesce(min_stock,0) as min, coalesce(max_stock,0) as max, " +
                "satuan_besar, harga_besar_resep, harga_besar_non_resep, satuan_kecil, harga_kecil_resep, harga_kecil_non_resep," +
                "coalesce(b.kode_lokasi,'') as kode_lokasi, coalesce(l.nama_lokasi,'') as nama_lokasi " +
                "from barang b " +
                "left join jenis_barang jb on jb.kode_jenis=b.kode_jenis " +
                "left join lokasi l using(kode_lokasi)" +
                "left join gudang g on g.kode_gudang=b.gudang " +
                "where "+sField+" "+cmbOperator.getSelectedItem().toString()+" '"+sPersen+txtFilter.getText()+sPersen+"'" +
                "ORDER BY 1";
        
         //
        
        onOpen(sQry);
        System.out.println(sQry);
        txtFilter.requestFocus();
    }
    
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnClose;
    private javax.swing.JButton btnDelete;
    private javax.swing.JButton btnEdit;
    private javax.swing.JButton btnFilter;
    private javax.swing.JButton btnNew;
    private javax.swing.JButton btnSearch;
    private javax.swing.JComboBox cmbFilter;
    private javax.swing.JComboBox cmbOperator;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JToolBar jToolBar1;
    private javax.swing.JToolBar jToolBar2;
    private javax.swing.JTable tblItem;
    private javax.swing.JTextField txtFilter;
    private javax.swing.JTextField txtSearch;
    // End of variables declaration//GEN-END:variables
    
}
