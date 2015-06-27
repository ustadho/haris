/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package main;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */


import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.FocusTraversalPolicy;
import java.awt.KeyboardFocusManager;
import java.awt.event.ActionEvent;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.print.PrinterJob;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.InetAddress;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.MissingResourceException;
import java.util.PropertyResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.print.DocFlavor;
import javax.print.PrintService;
import javax.print.PrintServiceLookup;
import javax.print.attribute.HashPrintRequestAttributeSet;
import javax.print.attribute.HashPrintServiceAttributeSet;
import javax.print.attribute.PrintRequestAttributeSet;
import javax.print.attribute.PrintServiceAttributeSet;
import javax.swing.AbstractAction;
import javax.swing.ActionMap;
import javax.swing.InputMap;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JToolBar;
import javax.swing.KeyStroke;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableColumnModel;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import org.jdesktop.swingx.JXDatePicker;
import org.jdesktop.swingx.JXTable;
import org.jdesktop.swingx.JXTitledPanel;
import org.jdesktop.swingx.table.ColumnHeaderRenderer;
//import org.jdesktop.swingx.JXTable;
//import org.jdesktop.swingx.table.ColumnHeaderRenderer;

/**
 *
 * @author root
 */
public class GeneralFunction {
//    public static NumberFormat dFmt=NumberFormat.getNumberInstance(Locale.US);
//    public static NumberFormat intFmt=NumberFormat.getIntegerInstance(Locale.US);
//    public static NumberFormat fFmt=NumberFormat.getNumberInstance(Locale.US);


    public static NumberFormat dFmt=new DecimalFormat("#,##0.00");
    public static NumberFormat intFmt=new DecimalFormat("#,##0");
    public static NumberFormat fFmt=new DecimalFormat("#,##0.00");
    private boolean clearIfNotFound=true;
    public static SimpleDateFormat yyyymmdd_format= new SimpleDateFormat("yyyy-MM-dd");
    public static SimpleDateFormat ddMMyy_format= new SimpleDateFormat("dd-MM-yyyy");

    ListRsbm lst=new ListRsbm();
    MyKeyListener kListener=new MyKeyListener();

    public GeneralFunction(){
        
    }

    public GeneralFunction(Connection con){
        lst.con=con;
    }

    public void setClearIfNotFound(boolean b){
        clearIfNotFound=b;
    }
    
    public boolean isListVisible(){
        return lst.isVisible();
    }

    public void lstRequestFocus(){
        lst.requestFocus();
    }

    public void setVisibleList(boolean b){
        lst.setVisible(b);
    }

    public String getComputerName(){
        String computername="";
        try{
            computername=InetAddress.getLocalHost().getHostName();

        }catch (Exception e){
          System.out.println("Exception caught ="+e.getMessage());
        }
        return computername;
    }
    
    public void autoColWidthTable(JTable table){
        table.setModel((DefaultTableModel)autoResizeColWidth(table, (DefaultTableModel)table.getModel()).getModel());
    }
    public void autoColWidthTable(JXTable tbl){
        tbl.setModel((DefaultTableModel)autoResizeColWidth(tbl, (DefaultTableModel)tbl.getModel()).getModel());
    }
    
    public void lookup(KeyEvent evt, Object comp[], String sQry, int iWidth, int iHeight){
        //System.out.println(sQry);
        lst.setCompDes(comp);
        try {
            switch(evt.getKeyCode()) {
                case java.awt.event.KeyEvent.VK_ENTER: {
                    if (lst.isVisible()){
                        Object[] obj = lst.getOResult();
                        if (obj.length > 0) {
                            lst.udfSelected();
                            lst.setVisible(false);
                        }
                    }
                    break;
                }
                case java.awt.event.KeyEvent.VK_ESCAPE: {
                    lst.setVisible(false);
                    ((JTextField)evt.getSource()).setText("");
                    if(comp==null || comp.length==0) return;
                    for(int i=0; i<comp.length; i++){
                        if(comp[i] instanceof JTextArea)
                            ((JTextArea)comp[i]).setText("");
                        if(comp[i] instanceof JTextField)
                            ((JTextField)comp[i]).setText("");
                        if(comp[i] instanceof JFormattedTextField)
                            ((JTextField)comp[i]).setText("");
                        if(comp[i] instanceof JLabel)
                            ((JLabel)comp[i]).setText("");
                        if(comp[i] instanceof JComboBox)
                            ((JComboBox)comp[i]).setSelectedItem("");
                    }
                }
                case java.awt.event.KeyEvent.VK_DELETE: {
                    lst.setFocusable(true);
                    lst.requestFocus();
                    break;
                }

                case java.awt.event.KeyEvent.VK_DOWN: {
                    if (lst.isVisible()){
                        lst.setFocusableWindowState(true);
                        lst.setVisible(true);
                        lst.requestFocus();
                    }
                    break;
                }
                default : {
                    if(evt.getKeyCode()==KeyEvent.VK_INSERT) return;

                    if(!evt.getKeyText(evt.getKeyCode()).equalsIgnoreCase("Up")){
                        lst.setSQuery(sQry);
                        lst.setBounds(((JTextField)evt.getSource()).getLocationOnScreen().x,
                                ((JTextField)evt.getSource()).getLocationOnScreen().y + ((JTextField)evt.getSource()).getHeight(),
                                iWidth, iHeight);
                        lst.setFocusableWindowState(false);
                        lst.setTxtCari(((JTextField)evt.getSource()));
                        lst.setCompDes(comp);
                        lst.setColWidth(0, ((JTextField)evt.getSource()).getWidth());
                        if(comp!=null && comp[0]!=null ) {
                            if(comp[0] instanceof JLabel) lst.setColWidth(1, ((JLabel)comp[0]).getWidth());
                            if(comp[0] instanceof JTextField) lst.setColWidth(1, ((JTextField)comp[0]).getWidth());
                            if(comp[0] instanceof JTextArea) lst.setColWidth(1, ((JTextArea)comp[0]).getWidth());
                            if(comp[0] instanceof JFormattedTextField) lst.setColWidth(1, ((JFormattedTextField)comp[0]).getWidth());
                            if(comp[0] instanceof JComboBox) lst.setColWidth(1, ((JComboBox)comp[0]).getWidth());
                        }

                        if(lst.getIRowCount()>0){
                            lst.setVisible(true);
                        } else{
                            if(clearIfNotFound)
                                ((JTextField)evt.getSource()).setText("");
                            
                            if(comp==null || comp.length==0) return;
                            for(int i=0; i<comp.length; i++){
                                if(comp[i] instanceof JTextArea)
                                    ((JTextArea)comp[i]).setText("");
                                if(comp[i] instanceof JTextField)
                                    ((JTextField)comp[i]).setText("");
                                if(comp[i] instanceof JFormattedTextField)
                                    ((JFormattedTextField)comp[i]).setText("");
                                if(comp[i] instanceof JLabel)
                                    ((JLabel)comp[i]).setText("");
                                if(comp[i] instanceof JComboBox)
                                    ((JComboBox)comp[i]).setSelectedItem("");
                            }
                            lst.setVisible(false);
                        }
                    }
                    break;
                }
            }


        } catch (SQLException se) {System.out.println(se.getMessage());

        }
    }

    public void udfLoadList(String sQry, JTextField txt , int iWidth, int iHeight){
        try {
            lst.setSQuery(sQry);
            lst.setBounds(txt.getLocationOnScreen().x, txt.getLocationOnScreen().y + txt.getHeight(), iWidth, iHeight);
            lst.setFocusableWindowState(false);
            lst.setTxtCari(txt);
            lst.setCompDes(null);
            lst.setColWidth(0, txt.getWidth());
            if (lst.getIRowCount() > 0) {
                lst.setVisible(true);
            }
        } catch (SQLException ex) {
            Logger.getLogger(GeneralFunction.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static double udfGetDouble(String sNum){
        double hsl=0;
        if(!sNum.trim().equalsIgnoreCase("")){
            try{
                hsl=dFmt.parse(sNum).doubleValue();
                //hsl=Double.parseDouble(sNum);
            } catch (java.text.ParseException ex) {
                hsl=0;
                //Logger.getLogger(FrmTrxPinjam.class.getName()).log(Level.SEVERE, null, ex);
            }catch(NumberFormatException ne){
                hsl=0;
            }catch(IllegalArgumentException i){
                hsl=0;
            }
        }
        return hsl;
  }
    public static double udfGetDouble(Object sNum){
        double hsl=0;
        if(sNum!=null && !sNum.toString().trim().equalsIgnoreCase("")){
            try{
                hsl=dFmt.parse(dFmt.format(sNum)).doubleValue();
            } catch (java.text.ParseException ex) {
                hsl=0;
            }catch(NumberFormatException ne){
                hsl=0;
            }catch(IllegalArgumentException i){
                hsl=0;
            }
        }
        return hsl;
  }


    public static float udfGetFloat(String sNum){
        float hsl=0;
        if(!sNum.trim().equalsIgnoreCase("")){
            try{
                hsl=fFmt.parse(sNum).floatValue();
            } catch (java.text.ParseException ex) {
                hsl=0;
            }catch(NumberFormatException ne){
                hsl=0;
            }catch(IllegalArgumentException i){
                hsl=0;
            }
        }
        return hsl;
  }

    public static float udfGetFloat(Object sNum){
        float hsl=0;
        if(sNum!=null && !sNum.toString().trim().equalsIgnoreCase("")){
            try{
                hsl=fFmt.parse(fFmt.format(sNum)).floatValue();
            } catch (java.text.ParseException ex) {
                hsl=0;
            }catch(NumberFormatException ne){
                hsl=0;
            }catch(IllegalArgumentException i){
                hsl=0;
            }
        }
        return hsl;
  }

    public static int udfGetInt(String sNum){
        int hsl=0;
        if(!sNum.trim().equalsIgnoreCase("")){
            try{
                //hsl=Integer.valueOf(sNum);
                hsl=intFmt.parse(sNum).intValue();

            } catch (java.text.ParseException ex) {
                hsl=0;
                //Logger.getLogger(FrmTrxPinjam.class.getName()).log(Level.SEVERE, null, ex);
            }catch(NumberFormatException ne){
                hsl=0;
            }catch(IllegalArgumentException i){
                hsl=0;
            }
        }
        return hsl;
  }

    public static int udfGetInt(Object sNum){
        int hsl=0;
        if(sNum!=null && !sNum.toString().trim().equalsIgnoreCase("")){
            try{
                if(sNum instanceof String)
                    hsl=Integer.parseInt(sNum.toString());
                else
                    hsl=intFmt.parse(intFmt.format(sNum)).intValue();
            } catch (java.text.ParseException ex) {
                hsl=0;
            }catch(NumberFormatException ne){
                hsl=0;
            }catch(IllegalArgumentException i){
                hsl=0;
            }
        }
        return hsl;
  }

    public static BigDecimal udfGetBigDecimal(String sNum){
        BigDecimal hsl=new BigDecimal(BigInteger.ZERO);
        if(sNum!=null && !sNum.trim().equalsIgnoreCase("")){
            try{
                hsl=new BigDecimal(sNum);
            }catch(NumberFormatException ne){
                hsl=new BigDecimal(BigInteger.ZERO);
            }catch(IllegalArgumentException i){
                hsl=new BigDecimal(BigInteger.ZERO);
            }
        }
        return hsl;
  }
    
    public static BigDecimal udfGetBigDecimal(Object sNum){
        BigDecimal hsl=new BigDecimal(BigInteger.ZERO);
        if(sNum!=null && sNum!=null){
            try{
                hsl=(BigDecimal)sNum;
            }catch(NumberFormatException ne){
                hsl=new BigDecimal(BigInteger.ZERO);
            }catch(IllegalArgumentException i){
                hsl=new BigDecimal(BigInteger.ZERO);
            }
        }
        return hsl;
  }
    
    public static boolean validateDate( String dateStr, boolean allowPast, String formatStr){
     if (formatStr == null) return false; // or throw some kinda exception, possibly a InvalidArgumentException
		SimpleDateFormat df = new SimpleDateFormat(formatStr);
		Date testDate = null;
		try
		{
			testDate = df.parse(dateStr);
		}
		catch (java.text.ParseException e)
		{
			// invalid date format
			return false;
		}
		if (!allowPast)
		{
			// initialise the calendar to midnight to prevent
			// the current day from being rejected
			Calendar cal = Calendar.getInstance();
			cal.set(Calendar.HOUR_OF_DAY, 0);
			cal.set(Calendar.MINUTE, 0);
			cal.set(Calendar.SECOND, 0);
			cal.set(Calendar.MILLISECOND, 0);
			if (cal.getTime().after(testDate)) return false;
		}
		// now test for legal values of parameters
		if (!df.format(testDate).equals(dateStr)) return false;
		return true;
	}

    public static void keyTyped(java.awt.event.KeyEvent evt) {
        char c = evt.getKeyChar();
          if (!(Character.isDigit(c) ||
             (c == KeyEvent.VK_BACK_SPACE) ||
             (c == '.') ||(c == ',') ||
             (c == '-') ||
             (c == KeyEvent.VK_DELETE)||
             (c == KeyEvent.VK_ENTER))) {
            //getToolkit().beep();
            evt.consume();
          }
    }

    public void addKeyListenerToPanel(JPanel panel){
        Component c=null;
        for(int i=0;i<panel.getComponentCount();i++){
            c = panel.getComponent(i);
            if(c instanceof JTextField ||c instanceof JFormattedTextField|| c instanceof JTable|| c instanceof JButton) {
                c.addKeyListener(kListener);
                if(c instanceof JTextField ) ((JTextField)c).addFocusListener(txtFocus);
                if(c instanceof JFormattedTextField ) ((JFormattedTextField)c).addFocusListener(txtFocus);

            }
        }
    }

    public void setConn(Connection conn) {
        lst.con=conn;
    }

//    public void setAutoResizeColWidth(JTable table) {
//        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
//    }

    public class MyKeyListener extends KeyAdapter {
        @Override
        public void keyPressed(KeyEvent evt) {
            Component ct = KeyboardFocusManager.getCurrentKeyboardFocusManager().getPermanentFocusOwner();
            int keyKode = evt.getKeyCode();
            switch(keyKode){

                case KeyEvent.VK_F2: {  //Save

                    break;
                }

               case KeyEvent.VK_ENTER : {
                    if(!(ct instanceof JTable))                    {
                        if (!isListVisible()){
                            Component c = findNextFocus();
                            if (c==null) return;
                            c.requestFocus();
                        }else{
                            lstRequestFocus();
                        }
                    }
                    break;
                }
                case KeyEvent.VK_DOWN: {
                    if(!(ct.getClass().getSimpleName().equalsIgnoreCase("JTABLE")))
                        {
                            if (!isListVisible()){
			    Component c = findNextFocus();
			    c.requestFocus();
                            }else
                                lstRequestFocus();

                            break;
                    }
                }

                case KeyEvent.VK_UP: {
                    if(!(ct.getClass().getSimpleName().equalsIgnoreCase("JTABLE")))
                    {
                        Component c = findPrevFocus();
                        c.requestFocus();
                    }
                    break;
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

    public void removeComboUpDown(final JComboBox cmb){
        InputMap _im = new InputMap();
        _im.put(KeyStroke.getKeyStroke(KeyEvent.VK_DOWN, 0), "highlightNext");
        _im.put(KeyStroke.getKeyStroke(KeyEvent.VK_UP, 0), "highlightPrev");
        SwingUtilities.replaceUIInputMap(cmb, JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT, _im);

        ActionMap _am = new ActionMap();
        _am.put("highlightNext", new AbstractAction(){
            public void actionPerformed(ActionEvent e){
                if(cmb.isShowing() && cmb.isPopupVisible()){ highlightNext(); }
            }
        });
        _am.put("highlightPrev", new AbstractAction(){
            public void actionPerformed(ActionEvent e){
                if(cmb.isShowing() && cmb.isPopupVisible()){ highlightPrev(); }
            }
        });
        SwingUtilities.replaceUIActionMap(cmb, _am);
    }

    public void highlightNext(){
        Component c = findNextFocus();
        if (c==null) return;
        if(c.isEnabled())
            c.requestFocus();
        else{
            c = findNextFocus();
            if (c!=null) c.requestFocus();;
        }
    }

    public void highlightPrev(){
        Component c = findPrevFocus();
        if (c==null) return;
        if(c.isEnabled())
            c.requestFocus();
        else{
            c = findPrevFocus();
            if (c!=null) c.requestFocus();;
        }
    }

    public Double roundUp(Double dNum, Double dUp) {
        Double ret = dNum;
        if (dNum == null) {
            dNum = 0.0;
        }
        Double sisa = dNum % dUp;
        if (sisa > 0) {
            ret = (dNum - sisa) + dUp;
        }
        return ret;
    }

    private FocusListener txtFocus=new FocusListener() {
        public void focusGained(FocusEvent e) {
            if(e.getSource().getClass().getSimpleName().equalsIgnoreCase("JTextField")||
                    e.getSource().getClass().getSimpleName().equalsIgnoreCase("JFormattedTextField")){
                ((JTextField)e.getSource()).setBackground(color);

                ((JTextField)e.getSource()).setSelectionStart(0);
                    ((JTextField)e.getSource()).setSelectionEnd(((JTextField)e.getSource()).getText().length());

            }
        }

        public void focusLost(FocusEvent e) {
            if(e.getSource().getClass().getSimpleName().equalsIgnoreCase("JTextField")||
                    e.getSource().getClass().getSimpleName().equalsIgnoreCase("JFormattedTextField")){
                ((JTextField)e.getSource()).setBackground(Color.WHITE);

                ((JTextField)e.getSource()).setSelectionStart(0);
                ((JTextField)e.getSource()).setSelectionEnd(0);
            }
        }
    } ;

    public static PrintService getPrinterKwt(){
        int q = 0;
        PrintService[] services=null;
        PrinterJob job = PrinterJob.getPrinterJob();
        DocFlavor flavor = DocFlavor.SERVICE_FORMATTED.PRINTABLE;
        PrintRequestAttributeSet aset = new HashPrintRequestAttributeSet();
        PrintServiceAttributeSet pset = new HashPrintServiceAttributeSet();

        services = PrintServiceLookup.lookupPrintServices(flavor, null);
        for (q = 0; q < services.length; q++) {
            if (services[q].getName().equalsIgnoreCase(resources.getString("printer_kwt"))) {
                break;
            }
        }
        return services[q];
    }

    public static PrintService getPrinterLabel(){
        int q = 0;
        PrintService[] services=null;
        PrinterJob job = PrinterJob.getPrinterJob();
        DocFlavor flavor = DocFlavor.SERVICE_FORMATTED.PRINTABLE;
        PrintRequestAttributeSet aset = new HashPrintRequestAttributeSet();
        PrintServiceAttributeSet pset = new HashPrintServiceAttributeSet();

        services = PrintServiceLookup.lookupPrintServices(flavor, null);
        for (q = 0; q < services.length; q++) {
            if (services[q].getName().equalsIgnoreCase(resources.getString("printer_label"))) {
                break;
            }
        }
        return services[q];
    }

    public void addKeyListenerInContainer(Object obj, KeyAdapter keyAdapter, FocusListener focus){
        if (obj instanceof JPanel){
            for(int i=0;i<((JPanel)obj).getComponentCount();i++){
                Component c = ((JPanel)obj).getComponent(i);
                if(c instanceof JTextField || c instanceof  JFormattedTextField || c instanceof  JTextArea ||
                c instanceof JComboBox || c instanceof  JButton || c  instanceof  JCheckBox ||c instanceof JXDatePicker 
                        || c instanceof JTable || c instanceof JXTable) {
                    c.addKeyListener(keyAdapter);
                    if(focus!=null){
                        c.addFocusListener(focus);
                    }
                }
            }
        }else if (obj instanceof JToolBar){
            for(int i=0;i<((JToolBar)obj).getComponentCount();i++){
            Component c = ((JToolBar)obj).getComponent(i);
                if(c instanceof JTextField || c instanceof  JFormattedTextField || c instanceof  JTextArea ||
                c instanceof JComboBox || c instanceof  JButton || c  instanceof  JCheckBox ||c instanceof JXDatePicker ) {
                    c.addKeyListener(keyAdapter);
                    if(focus!=null){
                        c.addFocusListener(focus);
                    }
                }
            }
        }else if (obj instanceof JXTitledPanel){
            for(int i=0;i<((JXTitledPanel)obj).getComponentCount();i++){
            Component c = ((JXTitledPanel)obj).getComponent(i);
                if(c instanceof JTextField || c instanceof  JFormattedTextField || c instanceof  JTextArea ||
                c instanceof JComboBox || c instanceof  JButton || c  instanceof  JCheckBox ||c instanceof JXDatePicker 
                        || c instanceof JTable || c instanceof JXTable) {
                    c.addKeyListener(keyAdapter);
                    if(focus!=null){
                        c.addFocusListener(focus);
                    }
                }
            }
        }
    }

    public JTable autoResizeColWidth(JTable table, DefaultTableModel model) {
        table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        table.setModel(model);

        int margin = 5;

        for (int i = 0; i < table.getColumnCount(); i++) {
            int                     vColIndex = i;
            DefaultTableColumnModel colModel  = (DefaultTableColumnModel) table.getColumnModel();
            TableColumn             col       = colModel.getColumn(vColIndex);
            int                     width     = 0;

            // Get width of column header
            TableCellRenderer renderer = col.getHeaderRenderer();

            if (renderer == null) {
                renderer = table.getTableHeader().getDefaultRenderer();
            }

            Component comp = renderer.getTableCellRendererComponent(table, col.getHeaderValue(), false, false, 0, 0);

            width = comp.getPreferredSize().width;

            // Get maximum width of column data
            for (int r = 0; r < table.getRowCount(); r++) {
                renderer = table.getCellRenderer(r, vColIndex);
                comp     = renderer.getTableCellRendererComponent(table, table.getValueAt(r, vColIndex), false, false,
                        r, vColIndex);
                width = Math.max(width, comp.getPreferredSize().width);
            }

            // Add margin
            width += 2 * margin;

            // Set the width
            col.setPreferredWidth(width);
        }

        ((DefaultTableCellRenderer) table.getTableHeader().getDefaultRenderer()).setHorizontalAlignment(
            SwingConstants.LEFT);

        // table.setAutoCreateRowSorter(true);
        table.getTableHeader().setReorderingAllowed(false);

        return table;
    }

    public JXTable autoResizeColWidth(JXTable table, DefaultTableModel model) {
        table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        table.setModel(model);

        int margin = 5;

        for (int i = 0; i < table.getColumnCount(); i++) {
            int                     vColIndex = i;
            DefaultTableColumnModel colModel  = (DefaultTableColumnModel) table.getColumnModel();
            TableColumn             col       = colModel.getColumn(vColIndex);
            int                     width     = 0;

            // Get width of column header
            TableCellRenderer renderer = col.getHeaderRenderer();

            if (renderer == null) {
                renderer = table.getTableHeader().getDefaultRenderer();
            }

            Component comp = renderer.getTableCellRendererComponent(table, col.getHeaderValue(), false, false, 0, 0);

            width = comp.getPreferredSize().width;

            // Get maximum width of column data
            for (int r = 0; r < table.getRowCount(); r++) {
                renderer = table.getCellRenderer(r, vColIndex);
                comp     = renderer.getTableCellRendererComponent(table, table.getValueAt(r, vColIndex), false, false,
                        r, vColIndex);
                width = Math.max(width, comp.getPreferredSize().width);
            }

            // Add margin
            width += 2 * margin;

            // Set the width
            col.setPreferredWidth(width);
        }

//        ((DefaultTableCellRenderer) table.getTableHeader().getDefaultRenderer()).setHorizontalAlignment(
//            SwingConstants.LEFT);
        ((ColumnHeaderRenderer) table.getTableHeader().getDefaultRenderer()).setHorizontalAlignment(
            SwingConstants.LEFT);

        // table.setAutoCreateRowSorter(true);
        table.getTableHeader().setReorderingAllowed(false);

        return table;
    }

    Color color=new Color(0,255,255);
    private static PropertyResourceBundle resources;

    static {
        try {
            String sDir=System.getProperties().getProperty("user.dir");
            resources = new PropertyResourceBundle(new FileInputStream(new File(sDir+"/setting.properties")));
        } catch (IOException ex) {
            
        } catch (MissingResourceException mre) {
            System.err.println("setting.properties not found");
            System.exit(1);
        }
    }
    public static void adjustColumnPreferredWidths(JTable table) {
        // strategy - get max width for cells in column and
        // make that the preferred width
        TableColumnModel columnModel = table.getColumnModel();
        for (int col = 0; col < table.getColumnCount(); col++) {

            int maxwidth = 0;
            for (int row = 0; row < table.getRowCount(); row++) {
                TableCellRenderer rend =
                        table.getCellRenderer(row, col);
                Object value = table.getValueAt(row, col);
                Component comp =
                        rend.getTableCellRendererComponent(table,
                        value,
                        false,
                        false,
                        row,
                        col);
                maxwidth = Math.max(comp.getPreferredSize().width, maxwidth);
            } // for row
            TableColumn column = columnModel.getColumn(col);
            column.setPreferredWidth(maxwidth);
        } // for col 
    }
    
    public void setAutoResizeColWidth(JTable table) { //, DefaultTableModel model
        table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
//        table.setModel(model);

        int margin = 5;

        for (int i = 0; i < table.getColumnCount(); i++) {
            int                     vColIndex = i;
            DefaultTableColumnModel colModel  = (DefaultTableColumnModel) table.getColumnModel();
            TableColumn             col       = colModel.getColumn(vColIndex);
            int                     width     = 0;

            // Get width of column header
            TableCellRenderer renderer = col.getHeaderRenderer();

            if (renderer == null) {
                renderer = table.getTableHeader().getDefaultRenderer();
            }

            Component comp = renderer.getTableCellRendererComponent(table, col.getHeaderValue(), false, false, 0, 0);

            width = comp.getPreferredSize().width;

            // Get maximum width of column data
            for (int r = 0; r < table.getRowCount(); r++) {
                renderer = table.getCellRenderer(r, vColIndex);
                comp     = renderer.getTableCellRendererComponent(table, table.getValueAt(r, vColIndex), false, false,
                        r, vColIndex);
                width = Math.max(width, comp.getPreferredSize().width);
            }

            // Add margin
            width += 2 * margin;

            // Set the width
            col.setPreferredWidth(width);
        }

        ((DefaultTableCellRenderer) table.getTableHeader().getDefaultRenderer()).setHorizontalAlignment(
            SwingConstants.LEFT);

        // table.setAutoCreateRowSorter(true);
        table.getTableHeader().setReorderingAllowed(false);
//        return table;
    }
    public void setAutoResizeColWidth(JXTable table) { //, DefaultTableModel model
        table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
//        table.setModel(model);

        int margin = 5;

        for (int i = 0; i < table.getColumnCount(); i++) {
            int                     vColIndex = i;
            DefaultTableColumnModel colModel  = (DefaultTableColumnModel) table.getColumnModel();
            TableColumn             col       = colModel.getColumn(vColIndex);
            int                     width     = 0;

            // Get width of column header
            TableCellRenderer renderer = col.getHeaderRenderer();

            if (renderer == null) {
                renderer = table.getTableHeader().getDefaultRenderer();
            }

            Component comp = renderer.getTableCellRendererComponent(table, col.getHeaderValue(), false, false, 0, 0);

            width = comp.getPreferredSize().width;

            // Get maximum width of column data
            for (int r = 0; r < table.getRowCount(); r++) {
                renderer = table.getCellRenderer(r, vColIndex);
                comp     = renderer.getTableCellRendererComponent(table, table.getValueAt(r, vColIndex), false, false,
                        r, vColIndex);
                width = Math.max(width, comp.getPreferredSize().width);
            }

            // Add margin
            width += 2 * margin;

            // Set the width
            col.setPreferredWidth(width);
        }

        ((DefaultTableCellRenderer) table.getTableHeader().getDefaultRenderer()).setHorizontalAlignment(
            SwingConstants.LEFT);

        // table.setAutoCreateRowSorter(true);
        table.getTableHeader().setReorderingAllowed(false);
//        return table;
    }
}
