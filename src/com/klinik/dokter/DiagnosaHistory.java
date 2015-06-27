package com.klinik.dokter;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * DiagnosaHistory.java
 *
 * Created on Apr 28, 2010, 3:47:26 PM
 */


import java.awt.Cursor;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;
import javax.swing.text.html.HTMLDocument;
import javax.swing.text.html.HTMLFrameHyperlinkEvent;
import main.MainForm;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.util.JRLoader;
import net.sf.jasperreports.view.JasperViewer;

/**
 *
 * @author ustadho
 */
public class DiagnosaHistory extends javax.swing.JFrame {
    private Connection conn;
    private String sNorm="";
    private String sNoReg="";

    /** Creates new form DiagnosaHistory */
//    public DiagnosaHistory(java.awt.Frame parent, boolean modal) {
//        super(parent, modal);
//        initComponents();
//    }

    public DiagnosaHistory() {
        initComponents();
    }

    public void setNoReg(String s){
        sNoReg=s;
    }
    
    public void setConn(Connection con){
        this.conn=con;
    }
    
    public void setNorm(String s){
        this.sNorm=s;
    }

    public void udfLoadDiagnosa(){
        String sHtml="<html><body> ";
        
        try {
            ResultSet rsD;
            ResultSet rs;
            System.out.println("Norm:"+sNorm);
            rs = conn.createStatement().executeQuery(
                    " select ps.norm, coalesce(ps.nama ||coalesce(' ('||ps.title||')', ''),'') as pasien, "
                    + "to_char(ps.tgl_lahir, 'dd/MM/yyyy') as tgl_lahir, age(current_date , ps.tgl_lahir) as usia "
                    + "from rm_pasien ps where ps.norm='"+sNorm+"'");
            if(rs.next()){
                lblNorm.setText(rs.getString("norm"));
                lblPasien.setText(rs.getString("pasien"));
                lblTglLahir.setText(rs.getString("tgl_lahir"));
                lblUsia.setText(rs.getString("usia"));
            }
            String sDet="select r.no_reg, to_char(r.tanggal, 'dd/MM/yyyy') as tanggal, to_char(r.time_ins, 'hh24:MI') as jam, "
                        + "coalesce(dg.kode_diagnosa,'') as kode_diagnosa,"
                        + "coalesce(dg.ket_diagnosa,'') as ket_diagnosa, coalesce(dg.s_ket, '') as s_ket,"
                        + "coalesce(dg.o_ket, '') as o_ket, coalesce(dg.a_ket ,'') as a_ket, coalesce(dg.p_ket ,'') as p_ket, "
                        + "coalesce(dg.catatan_lain,'') as catatan_lain, coalesce(dg.user_ins,'') as user_ins "
                        + "from rm_reg r "
                        + "left join rm_reg_diagnosa dg on dg.no_reg=r.no_reg "
                        + "where r.norm='"+sNorm+"' "
                        + "order by r.time_ins desc";
                
            System.out.println(sDet);
                
            sHtml="";
            String sEdit="";
            rs.close();
            rs= conn.createStatement().executeQuery(sDet);
            while(rs.next()){
                //sHtml+="<p><a href="+rs.getString(1)+" >"+ rs.getString("datetime_ins")+ " "+ rs.getString("catatan")+"</a></p>";
//                sEdit=sNoReg.equalsIgnoreCase(rs.getString("no_reg")) && rs.getString("user_ins").equalsIgnoreCase(MainForm.sUserName)?
                sEdit=sNoReg.equalsIgnoreCase(rs.getString("no_reg"))?
                        "<b> <a href=edit-"+rs.getString(1)+" >Edit</b>": "";
                
                sHtml+="<p><font face=\"Tahoma\" size=\"3\" color=\"blue\"><u>"
                        + "No. Registrasi : <b>"+ rs.getString("no_reg")+"</b> &nbsp;&nbsp;&nbsp; "+ 
                        rs.getString("tanggal")+ " &nbsp; - &nbsp;"+ rs.getString("jam")+ "</u></font>&nbsp; &nbsp; <font face=\"Tahoma\" size=\"3\" color=\"green\">"
                        + sEdit+" &nbsp;&nbsp;<b> <a href=print-"+rs.getString(1)+" >Print</b></font></p>";

                //sHtml+="<UL>";
                sHtml+="<font face=\"Courier New\" size=\"3\" color=\"green\"> "
                        + "Kode Diag. Utama :"+ rs.getString("kode_diagnosa")+"<br>"
                        + "Ket. Diag. Utama :"+ rs.getString("ket_diagnosa")+" </font>"
                        + "<font face=\"Tahoma\" size=\"3\" color=\"black\"> "
                        + "<LI>&nbsp;    <b>S :</b> "+rs.getString("s_ket")+"</LI>"
                        + "<LI>&nbsp;    <b>O :</b> "+rs.getString("o_ket")+"</LI>"
                        + "<LI>&nbsp;    <b>A :</b> "+rs.getString("a_ket")+"</LI>"
                        + "<LI>&nbsp;    <b>P :</b> - "+rs.getString("p_ket").replace("\n", "<br>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; - ") +"</LI>"
                        + "<br><u>Catatan    :</u> "+rs.getString("catatan_lain")+" "
                        
                        + "</font>";
                sHtml+="</UL>";
            }

            sHtml+="</body></html>";
            
            rs.close();
            jEditorPane1.setEditable( false );
            jEditorPane1.addHyperlinkListener(createHyperLinkListener());
            jEditorPane1.getDocument().putProperty( "Ignore-Charset", "true" );  // this line makes no difference either way
            jEditorPane1.setContentType( "text/html" );
            jEditorPane1.setText( sHtml );
            jEditorPane1.setCaretPosition(0);
            
        } catch (SQLException ex) {
            Logger.getLogger(DiagnosaHistory.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    private void udfPreviewDiagnosa(String sNoReg){
        try {
            this.setCursor(new Cursor(Cursor.WAIT_CURSOR));
            HashMap reportParam = new HashMap();
            JasperReport jasperReport=null;
            reportParam.put("norm", lblNorm.getText());
            reportParam.put("no_reg", sNoReg);
            jasperReport = (JasperReport) JRLoader.loadObject(getClass().getResourceAsStream("Reports/DiagnosaPx.jasper"));
            JasperPrint print = JasperFillManager.fillReport(jasperReport,reportParam,conn);

            if(print.getPages().isEmpty()){
                JOptionPane.showMessageDialog(this, "Report tidak ditermukan!");
                return;
            }
            print.setOrientation(jasperReport.getOrientationValue());
            JasperViewer.viewReport(print,false);

            this.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
        } catch (JRException ex) {
            Logger.getLogger(DiagnosaHistory.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public HyperlinkListener createHyperLinkListener() {
 	return new HyperlinkListener() {
 	    public void hyperlinkUpdate(HyperlinkEvent e) {
                if (e.getEventType() == HyperlinkEvent.EventType.ACTIVATED) {
                    System.err.println(e.getDescription());
 		
                    if (e instanceof HTMLFrameHyperlinkEvent) {
                        
 			((HTMLDocument)jEditorPane1.getDocument()).processHTMLFrameHyperlinkEvent(
 			    (HTMLFrameHyperlinkEvent)e);
 		    } else {
 			//try {
 			    //jEditorPane1.setPage(e.getURL());
                            //JOptionPane.showMessageDialog(DiagnosaHistory.this, "ID Diagnosa "+e.getDescription());
                        if(e.getDescription().indexOf("edit")>=0){
                            DiagnosaEdit d2=new DiagnosaEdit();
                            d2.setHistoryForm(DiagnosaHistory.this);
                            d2.setConn(conn);
                            d2.setNoReg(e.getDescription().replace("edit-", ""));
                            d2.setVisible(true);
                        }else if(e.getDescription().indexOf("print")>=0){
                            udfPreviewDiagnosa(e.getDescription().replace("print-", ""));
                        }else if(e.getDescription().indexOf("delete")>=0){
                            String sID=e.getDescription().substring(e.getDescription().indexOf("-")+1, e.getDescription().length());

                            if(JOptionPane.showConfirmDialog(DiagnosaHistory.this, "Anda yakin untuk menghapus diagnosa ini?"+sID, "Confirm", JOptionPane.YES_NO_OPTION)==JOptionPane.YES_OPTION){
                                try{
                                    System.out.println("select fn_rmo_diagnosa_delete('"+sID+"', '"+MainForm.sUserName+"')");
                                    ResultSet rs=conn.createStatement().executeQuery("select fn_rmo_diagnosa_delete('"+sID+"', '"+MainForm.sUserName+"')");

                                    if(rs.next()){
                                        JOptionPane.showMessageDialog(DiagnosaHistory.this, "Delete diagnosa sukses!");
                                        udfLoadDiagnosa();
                                    }
                                }catch(SQLException se){

                                }
                            }
                        }

// 			} catch (IOException ioe) {
// 			    System.out.println("IOE: " + ioe);
// 			}
 		    }
 		}
 	    }
 	};
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
        jEditorPane1 = new javax.swing.JEditorPane();
        jPanel5 = new javax.swing.JPanel();
        jLabel8 = new javax.swing.JLabel();
        lblNorm = new javax.swing.JLabel();
        lblPasien = new javax.swing.JLabel();
        jLabel12 = new javax.swing.JLabel();
        jLabel14 = new javax.swing.JLabel();
        lblTglLahir = new javax.swing.JLabel();
        jLabel15 = new javax.swing.JLabel();
        lblUsia = new javax.swing.JLabel();
        btnRefresh = new javax.swing.JButton();
        btnClose = new javax.swing.JButton();
        btnPrint = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Histori Diagnosa");
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowOpened(java.awt.event.WindowEvent evt) {
                formWindowOpened(evt);
            }
        });

        jScrollPane1.setViewportView(jEditorPane1);

        jPanel5.setBorder(javax.swing.BorderFactory.createTitledBorder("Detail Pasien"));
        jPanel5.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel8.setText("Pasien");
        jPanel5.add(jLabel8, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 55, 80, 20));

        lblNorm.setBackground(new java.awt.Color(255, 255, 255));
        lblNorm.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        lblNorm.setOpaque(true);
        jPanel5.add(lblNorm, new org.netbeans.lib.awtextra.AbsoluteConstraints(90, 30, 100, 20));

        lblPasien.setBackground(new java.awt.Color(255, 255, 255));
        lblPasien.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        lblPasien.setOpaque(true);
        jPanel5.add(lblPasien, new org.netbeans.lib.awtextra.AbsoluteConstraints(90, 55, 340, 20));

        jLabel12.setText("NORM");
        jPanel5.add(jLabel12, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 30, 80, 20));

        jLabel14.setText("Tgl. Lahir :");
        jPanel5.add(jLabel14, new org.netbeans.lib.awtextra.AbsoluteConstraints(250, 30, 90, 20));

        lblTglLahir.setBackground(new java.awt.Color(255, 255, 255));
        lblTglLahir.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        lblTglLahir.setOpaque(true);
        jPanel5.add(lblTglLahir, new org.netbeans.lib.awtextra.AbsoluteConstraints(340, 30, 90, 20));

        jLabel15.setText("Usia :");
        jPanel5.add(jLabel15, new org.netbeans.lib.awtextra.AbsoluteConstraints(450, 30, 40, 20));

        lblUsia.setBackground(new java.awt.Color(255, 255, 255));
        lblUsia.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        lblUsia.setOpaque(true);
        jPanel5.add(lblUsia, new org.netbeans.lib.awtextra.AbsoluteConstraints(490, 30, 130, 20));

        btnRefresh.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/small/refresh.png"))); // NOI18N
        btnRefresh.setText("Refresh");
        btnRefresh.setMargin(new java.awt.Insets(2, 2, 2, 2));
        btnRefresh.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnRefreshActionPerformed(evt);
            }
        });

        btnClose.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/small/CANCEL.PNG"))); // NOI18N
        btnClose.setText("Close");
        btnClose.setMargin(new java.awt.Insets(2, 2, 2, 2));
        btnClose.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCloseActionPerformed(evt);
            }
        });

        btnPrint.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/small/PRINTER.PNG"))); // NOI18N
        btnPrint.setText("Print All");
        btnPrint.setMargin(new java.awt.Insets(2, 2, 2, 2));
        btnPrint.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnPrintActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel5, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 820, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(btnPrint, javax.swing.GroupLayout.PREFERRED_SIZE, 90, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(42, 42, 42)
                        .addComponent(btnRefresh, javax.swing.GroupLayout.PREFERRED_SIZE, 90, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, 0)
                        .addComponent(btnClose, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel5, javax.swing.GroupLayout.PREFERRED_SIZE, 85, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 488, Short.MAX_VALUE)
                .addGap(10, 10, 10)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(btnRefresh)
                        .addComponent(btnPrint))
                    .addComponent(btnClose))
                .addContainerGap())
        );

        setSize(new java.awt.Dimension(848, 669));
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void formWindowOpened(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowOpened
        udfLoadDiagnosa();
    }//GEN-LAST:event_formWindowOpened

    private void btnRefreshActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnRefreshActionPerformed
        udfLoadDiagnosa();
}//GEN-LAST:event_btnRefreshActionPerformed

    private void btnCloseActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCloseActionPerformed
        this.dispose();
}//GEN-LAST:event_btnCloseActionPerformed

    private void btnPrintActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnPrintActionPerformed
        udfPreviewDiagnosa("");
    }//GEN-LAST:event_btnPrintActionPerformed

    /**
    * @param args the command line arguments
    */
//    public static void main(String args[]) {
//        java.awt.EventQueue.invokeLater(new Runnable() {
//            public void run() {
//                DiagnosaHistory dialog = new DiagnosaHistory(new javax.swing.JFrame(), true);
//                dialog.addWindowListener(new java.awt.event.WindowAdapter() {
//                    public void windowClosing(java.awt.event.WindowEvent e) {
//                        System.exit(0);
//                    }
//                });
//                dialog.setVisible(true);
//            }
//        });
//    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnClose;
    private javax.swing.JButton btnPrint;
    private javax.swing.JButton btnRefresh;
    private javax.swing.JEditorPane jEditorPane1;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JLabel lblNorm;
    private javax.swing.JLabel lblPasien;
    private javax.swing.JLabel lblTglLahir;
    private javax.swing.JLabel lblUsia;
    // End of variables declaration//GEN-END:variables

}
