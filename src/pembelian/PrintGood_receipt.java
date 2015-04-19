/*
 * PrintKwtUM.java
 *
 * Created on November 14, 2006, 11:48 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package pembelian;

import java.awt.print.PrinterJob;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.MalformedURLException;
import java.io.*;
import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import javax.print.*;
import javax.print.attribute.*;
import javax.print.attribute.standard.*;
import javax.print.event.*;
import javax.swing.JOptionPane;
/**
 *
 * @author root
 */
public class PrintGood_receipt {
    private Connection conn;
    private String no_GR;
    private SimpleDateFormat clockFormat;
    private String nama_unit="",username="";
    private Boolean okCpy;
    /** Creates a new instance of PrintKwtUM */
    public PrintGood_receipt(Connection newCon, String newNoGR,Boolean okCopy,String userLogin,PrintService service) {
        conn=newCon;
//        nama_unit=namaUnit;
        no_GR=newNoGR;
        okCpy=okCopy;
        username=userLogin;
        printFile(saveToTmpFile(),service);
    }
    
    private File saveToTmpFile() {
    try{
        //File temp = File.createTempFile("kwt", ".tmp");
    
        File fileT = new File("C:/Temp/Joss/");        
        File temp = File.createTempFile("GRnota", ".tmp",fileT);
        
        // Delete temp file when program exits.
       // temp.deleteOnExit();
    
        // Write to temp file
        BufferedWriter out = new BufferedWriter(new FileWriter(temp));

    // no_GR = D061114139001    
    //String sSqlHead = "select * from fn_cetak_nota_tanggungan('"+no_GR+"') as (no_GR VARCHAR, no_reg VARCHAR, nama_pasien VARCHAR, alamat_domisili VARCHAR, kota_domisili VARCHAR, jenis_bayar VARCHAR, user_tr VARCHAR, tgl VARCHAR, nama VARCHAR, nama_poli VARCHAR, corporate VARCHAR, alamat VARCHAR, kota_corporate VARCHAR, telp VARCHAR, fax VARCHAR, norm VARCHAR, ket VARCHAR)";
    
    
    String Supplier,alamat,kota,term,tanggal,user_receipt,kodeSupplier,tglnow="";    
    
    NumberFormat nf     = new DecimalFormat("###,###,###.##");
    NumberFormat nf_int = new DecimalFormat("#.#");
    
        int printCount=1;int noConter=0;
        String sNoPo="";
        Statement stTgl=conn.createStatement();
        ResultSet rsTgl=stTgl.executeQuery("select to_char(now()::date,'dd/MM/yyyy') as tglnow,substring(now()::time,1,8)as jamnow");
        if (rsTgl.next()){
            tglnow=rsTgl.getString("tglnow")+"-"+rsTgl.getString("jamnow");
        }
        rsTgl.close();
        stTgl.close();
        printCount++;
        
        out.write(resetPrn());
                
        out.write(draft());
  //      out.write(bold());
  //      out.write(italic());
   //     out.write(cpi15());
      
      //  out.write("Siloam Hospitals Surabaya");
        if (okCpy){
            out.write(cancelUnderLine());
            out.write(space(20)+"Copy "+printCount);
        }
        //Cancel Italic
        out.newLine();
        out.write(cancelItalic());
        out.write(cancelUnderLine());
        out.write("");
        out.write("");
         
        out.write(cancelUnderLine());
//        out.write(cancelCondenced());
//        out.write(cpi15());
        out.write(draft());
        out.newLine();
        
//        out.write(space(29));
//        out.write(underLine());
//        out.write(bold());
        String headkwt="G o o d  R e c e i p t";
        out.write("Apotik JOSS PRIMA");
        out.write(bold());
        out.write(underLine());
        out.write(space(8));
        out.write(headkwt.toUpperCase());
        out.write(cancelBold());
        out.write(cancelUnderLine());
        out.newLine();
        out.write("Jl. Sunan Drajat 155A Telp. (0322) 324557 "+padString("",13));
        out.newLine();
        out.write("Lamongan");
        out.newLine();
        out.newLine();
        
        String sSql="select * from fn_gr_print_nota('"+no_GR+"')as(tanggal text,jam text,kode_supplier varchar," +
                "nama_supplier varchar,alamat varchar,kota varchar,remarks varchar,gr_id varchar, user_receipt varchar," +
                "no_po varchar,kode_barang varchar,nama_barang varchar,uom varchar,qty varchar,price numeric,discount float8," +
                "vat float4,total float8, jml_asal numeric, hrg_asal float8 )";
                
        System.out.println(sSql);
        Statement st = conn.createStatement();
        ResultSet rs = st.executeQuery(sSql);
        int i = 1;
        int qty=0;
        float disc=0;
        float tax=0;
        float total=0;
        float totitem=0;
        float discitem=0;
        float taxitem=0;
        String jumlah;
        String no;
        while(rs.next()){
            kodeSupplier     = rs.getString("kode_supplier");
            Supplier         = rs.getString("nama_supplier");
            alamat           = rs.getString("alamat");
            kota             = rs.getString("kota");
            tanggal          = rs.getString("tanggal")+" - "+rs.getString("jam");
            user_receipt     = rs.getString("user_receipt");
            
            clockFormat = new SimpleDateFormat("dd/MM/yyyy");
                         //         1         2         3         4         5         6         7         8         9         10       11        12        13        14    
                         //12345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890
            if ((i%11)==1){
                out.write(padString("Supplier : "+Supplier.trim()+"("+kodeSupplier+")",45)      +"Delivery      : "+ no_GR);
                out.newLine();
                out.write(padString(alamat,45)                                                  +"Receipt Date  : "+tanggal);         
                out.newLine();
                out.write(padString(kota,45)                                                    +"Receipt By    : "+user_receipt);
                out.newLine();        
                out.write(padString("",45)                                                      +"Print Date    : "+tglnow);
                out.newLine();        
                out.write(padString("Remarks : "+rs.getString("remarks"),45)                    +"Print By      : "+username);
                out.newLine();
//                out.write(bold());
                        //         1         2         3         4         5         6         7         8         9         10       11        12        13        14    
                        //12345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890
                /*
                 *ASLINE TEKO PAK DWI                                                 
                out.write("-------------------------------------------------------------------------------");
                out.newLine();
                out.write("|No|     Nama Barang      | Qty |  UOM   |   Harga  | Disc.|Tax %|     Jumlah  |");  
                out.newLine();
                out.write("-------------------------------------------------------------------------------");
                 */
                        //         1         2         3         4         5         6         7         8         9         10       11        12        13        14    
                        //12345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890
                out.write("---------------------------------------------------------------------------------");
                out.newLine();
              //out.write("|No|   Nama Barang    | Qty |  UOM  |  Harga | Disc.|Tax %|   Jumlah  | No. PO  ");  
                out.write("|No|   Nama Barang    |   Satuan    |  Harga | Disc.|Tax %|   Jumlah  | No. PO  |"); 
                out.newLine();
                out.write("---------------------------------------------------------------------------------");
            }    
            out.newLine();
            if(i<9){
              no=" "+i+".";
            }else{
              no=""+i+".";
            }
            /*
             -----ASLINE TEKO PAK DWI
             out.write(padString("|",1)+padString(no,2)+padString("|",1)+padString(rs.getString("nama_Barang"),22)+padString("|",1)+rataKanan(rs.getString("qty"),5)+padString("|",1)+
                    padString(rs.getString("uom"),8)+padString("|",1)+rataKanan(nf.format(rs.getFloat("price")),10)+
                    padString("|",1)+rataKanan(rs.getString("discount"),6)+padString("|",1)+rataKanan(rs.getString("vat"),5)+padString("|",1)+rataKanan(nf.format(rs.getFloat("total")),13)+padString("|",1));  
            */
            out.write(padString("|",1)+padString(no,2)+padString("|",1)+padString(rs.getString("nama_Barang"),18)+padString("|",1)+
                    padString(rs.getString("qty"),13)+padString("|",1)+             //padString(rs.getString("uom"),7)+padString("|",1)+
                    rataKanan(nf.format(rs.getFloat("price")),8)+
                    padString("|",1)+rataKanan(rs.getString("discount"),6)+padString("|",1)+rataKanan(rs.getString("vat"),5)+
                    padString("|",1)+rataKanan(nf.format(rs.getFloat("total")),11)+padString("|",1)+
                    padString(rs.getString("no_po"),9)+padString("|",1));  
            
            qty += rs.getInt("jml_asal");
            totitem=(rs.getInt("jml_asal")*rs.getFloat("hrg_asal"));
//            discitem=totitem*(rs.getFloat("discount")/100);
//            taxitem=discitem*(rs.getFloat("vat")/100);
            discitem=rs.getFloat("discount");
            taxitem=rs.getFloat("vat");
            disc+=discitem;
            tax+=taxitem;
            total+=(totitem-discitem)+taxitem;
            i++;
        }
    //    total=((total)-disc)+tax;
        out.newLine();
        for (int jj=0;jj<11-i;jj++){
            /*out.write(padString("|",1)+padString("",2)+padString("|",1)+padString("",22)+padString("|",1)+padString("",5)+padString("|",1)+
                    padString("",8)+padString("|",1)+padString(" ",10)+padString("|",1)+padString("",6)+padString("|",1)+padString("",5)+
                    padString("|",1)+padString("",13)+padString("|",1));
            */
            out.write(padString("|",1)+padString("",2)+padString("|",1)+padString("",18)+padString("|",1)+
                    rataKanan("",13)+padString("|",1)+ //padString("",7)+padString("|",1)+
                    rataKanan("",8)+
                    padString("|",1)+rataKanan("",6)+padString("|",1)+rataKanan("",5)+
                    padString("|",1)+rataKanan("",11)+padString("|",1)+padString("",9)+padString("|",1));
            
             out.newLine();
        }
        out.write("---------------------------------------------------------------------------------");
        out.newLine();
        //out.write(padString("",48)+padString("Total Bayar : Rp",21)+ padString(nf.format(TotBayar),10));
        out.write("   "+padString("Penerima      Gudang        Kontroler",46)+padString("Total          ",15)+rataKanan(nf.format(total),15));
        out.newLine();
        out.write(padString("",49)+padString("Discount    (-)",15)+rataKanan(nf.format(disc),15));
        out.newLine();
        out.write(padString("",49)                                       +padString("PPN         (+)",15)+rataKanan(nf.format(tax),15));
        out.newLine();
        out.write(padString("",49)                                       +rataKanan("==============================",30));
        out.newLine();
        out.write(padString("",49)                                       +padString("Netto          ",15)+rataKanan(nf.format((total-disc)+tax),15));
        out.newLine();
        out.write(padString("(           ) (            ) (            )",49)                         +rataKanan("==============================",30));
        out.newLine();
        rs.close();
        st.close();
        
        out.newLine();
        out.write(cancelBold());
        i=i+6;
        out.close();   
        
        return temp.getCanonicalFile();
    }catch(IOException io){
        System.err.println(io.getMessage());
    }catch(SQLException se){
        System.err.println(se.getMessage());
    }
     return null;
}  

private String rataKanan(String sTeks,int panjang){
    String newText;
    
    newText=space(panjang-sTeks.length())+sTeks;
            
    return newText;
}

private String padString(String sTeks,int panjang){
    String newText;   
    String jmSpace="";
    if (sTeks.length()>panjang){
        newText=sTeks.trim().substring(0, panjang);
    }else{newText=sTeks.trim();}
    
    
    for(int i=0;i<(panjang-sTeks.trim().length());i++){
        newText=newText+" ";
    }
    
    return newText;
}

private String space(int iSpc){
    String s="";
    for(int i=1;i<=iSpc;i++){
        s=s+" ";
    }
    return s;
}

private String tengah(String sStr){
    String s="";
    int iTengah = (80-sStr.length())/2;
    s=s+space(iTengah)+sStr;
    return s;
}


private void printFile(File fileToPrint, PrintService service){
//int yesNo = JOptionPane.showConfirmDialog(this,"Siapkan Printer",JOptionPane.YES_NO_OPTION,JOptionPane.QUESTION_MESSAGE);
//if(yesNo == JOptionPane.YES_OPTION){
try {
// Open the text file
FileInputStream fs = new FileInputStream(fileToPrint);

// Find the default service
DocFlavor flavor = DocFlavor.INPUT_STREAM.AUTOSENSE;
//PrintService service = PrintServiceLookup.lookupDefaultPrintService();

// Create the print job
DocPrintJob job = service.createPrintJob();
Doc doc = new SimpleDoc(fs, flavor, null);

// Monitor print job events
// See "Determining When a Print Job Has Finished"
// for the implementation of PrintJobWatcher
// PrintJobWatcher pjDone = new PrintJobWatcher(job);

// Print it
job.print(doc, null);

// Wait for the print job to be done
// pjDone.waitForDone();

// It is now safe to close the input stream
fs.close();
} catch (PrintException e) {System.out.println(e.getMessage());
} catch (IOException e) {System.out.println(e.getMessage());
}   
}


public void setUser(String sUser){
    username=sUser;
}

private String resetPrn(){
    String str;
    str = String.valueOf((char)27) + String.valueOf((char)64);
    return str;    
}

private String draft(){
    String str;
    str = String.valueOf((char)27) + String.valueOf((char)48);
    return str;        
}

private String LQ(){
    String str;
    str = String.valueOf((char)27) + String.valueOf((char)49);
    return str;        
}

private String bold(){
    String str;
    str = String.valueOf((char)27) + String.valueOf((char)69);
    return str;        
}

private String cancelBold(){
    String str;
    str = String.valueOf((char)27) + String.valueOf((char)70);
    return str;        
}


private String italic(){
    String str;
    str = String.valueOf((char)27) + String.valueOf((char)52);
    return str;        
}

private String cancelItalic(){
    String str;
    str = String.valueOf((char)27) + String.valueOf((char)53);
    return str;        
}


private String underLine(){
    String str;
    str = String.valueOf((char)27) + String.valueOf((char)45) + String.valueOf((char)49);
    return str;        
}

private String cancelUnderLine(){
    String str;
    str = String.valueOf((char)27) + String.valueOf((char)45) + String.valueOf((char)48);
    return str;        
}


private String cpi10(){
    String str;
    str = String.valueOf((char)27) + String.valueOf((char)80);
    return str;        
}


private String cpi12(){
    String str;
    str = String.valueOf((char)27) + String.valueOf((char)77);
    return str;        
}

private String cpi15(){
    String str;
    str = String.valueOf((char)27) + String.valueOf((char)103);
    return str;        
}

private String condenced(){
    String str;
    str = String.valueOf((char)27) + String.valueOf((char)15);
    return str;        
}

private String cancelCondenced(){
    String str;
    str =  String.valueOf((char)18);
    return str;        
}

private String loadFront(){
    String str;
    str =  String.valueOf((char)27) + String.valueOf((char)25) + String.valueOf((char)70);
    return str;        
}

private String DoubleStrike(){
    String str;
    str =  String.valueOf((char)27) + String.valueOf((char)71) ;
    return str;        
    
}

private String CancelDoubleStrike(){
    String str;
    str =  String.valueOf((char)27) + String.valueOf((char)72) ;
    return str;        
    
}


public static void main(String[] args) {
        
       Connection  conn;
        String url = "jdbc:postgresql://localhost/joss";
        try{
            Class.forName("org.postgresql.Driver");    
        } catch(ClassNotFoundException ce) {
            System.out.println(ce.getMessage());
        }
           
            PrinterJob job = PrinterJob.getPrinterJob();
            DocFlavor flavor = DocFlavor.SERVICE_FORMATTED.PRINTABLE;
            PrintRequestAttributeSet aset = new HashPrintRequestAttributeSet();
            PrintServiceAttributeSet pset = new HashPrintServiceAttributeSet();
            PrintService[] services = PrintServiceLookup.lookupPrintServices(flavor, null);
            int i=0;
//            for(i=0;i<services.length;i++){
//                if(services[i].getName().equalsIgnoreCase("printer")){
//                    break;
//                }
//            }
           
        try {
            conn = DriverManager.getConnection(url,"joss","123");           
            PrintGood_receipt pn = new PrintGood_receipt(conn,"071229014",false,"DWikk",services[1]);
        } catch(SQLException se) {
            System.out.println(se.getMessage());
        }
        
       
        
}
    
}
