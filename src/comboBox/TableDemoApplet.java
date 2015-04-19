package comboBox;

import java.awt.Container;
import java.awt.Dimension;
import javax.swing.*;

public class TableDemoApplet extends JApplet {
    
    public TableDemoApplet() {
        createGUI(getContentPane());
    }
    
    private static void createGUI(Container contentPane) {
        Object[][] rowData = new String[][] { {"98-43", "AraAra! SL"},
                                              {"81-31", "Aragones Transports SA"},
                                              {"12-72", "Rocca SL"},
                                              {"99-10", "Rodriguez e Hijos SA"},
                                              {"00-65", "Rimbau Motors SL"} };
        JTable table = new JTable(rowData, new String[] {"Part No", "Provider"});
        
        JComboBox companyComboBox = new JComboBox(new Object[] {"AraAra! SL", "Aragones Transports SA", "Rocca SL", "Rodriguez e Hijos SA", "Rimbau Motors SL"});
        companyComboBox.setEditable(true);
        //new S15WorkingBackspace(companyComboBox);

        // setup the ComboBoxCellEditor, DefaultCellEditor won't work!
        table.getColumnModel().getColumn(1).setCellEditor(new ComboBoxCellEditor(companyComboBox));
        
        table.setPreferredScrollableViewportSize(new Dimension(400, 100));
        JScrollPane scrollPane = new JScrollPane(table);
        
        contentPane.setLayout(new java.awt.FlowLayout());
        contentPane.add(scrollPane);
        contentPane.add(new JTextField("HALLO!"));
    }
    
    public static void main(String[] args) {
        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                JFrame frame = new JFrame();
                frame.setDefaultCloseOperation(3);
                createGUI(frame.getContentPane());
                frame.pack(); frame.setVisible(true);
            }
        });
    }   
}