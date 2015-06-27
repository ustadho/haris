/*
 * printNota.java
 *
 * Created on November 10, 2005, 4:28 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */
package apotek;

import main.MainForm;
import java.io.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import javax.print.*;

/**
 *
 * @author root
 */
public class printPenjualanU220 {

    NumberFormat nf = new DecimalFormat("###,###,###");
    NumberFormat nf1 = new DecimalFormat("###,###,###.##");
    NumberFormat nf_int = new DecimalFormat("###.##");
    private Connection conn;
    private String noPenjualan;
    private SimpleDateFormat clockFormat;
    private String head;
    private String prnUlang = "", sUser = "";
    private PrintService service;
    private double discBill, fPPN, kembali;

    /**
     * Creates a new instance of printNota
     */
    public printPenjualanU220(Connection newCon, String newNota, String newUser, PrintService service) {
        conn = newCon;
        noPenjualan = newNota;
        sUser = newUser;
        try {
            printFile(saveToTmpFile(), service);
            //saveToTmpFile();
        } catch (IOException e) {
            System.out.println("IO Err " + e.getMessage());
        } catch (SQLException se) {
            System.out.println("SQL Err " + se.getMessage());
        }
    }

    private File saveToTmpFile() throws IOException, SQLException {

//        File a= new File("/home/faheem/");
//        File temp = File.createTempFile("Penjualan", ".txt",a);

        File temp = File.createTempFile("Penjualan", ".txt");
//         Delete temp file when program exits.
        temp.deleteOnExit();

        // Write to temp file
        BufferedWriter out = new BufferedWriter(new FileWriter(temp));

        out.write(resetPrn());

        out.write(cpi20());
        out.write(padString(MainForm.sNamaUsaha, 32));
        out.newLine();
        out.write(padString(MainForm.sAlamat, 38));
        out.newLine();
        out.write(padString(MainForm.sTelp, 45));
        out.newLine();
        //out.newLine();        

        out.write(italic());
        out.write(cpi15());
        out.write(padString("", 15) + padString("KWITANSI", 15));        //6
        out.newLine(); //7
        out.newLine(); //8
        out.write(cancelItalic());
        String r1 = "", r2 = "";

        //         1         2         3         4         5         6         7         8         9         10       11        12        13        14
        //12345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890

        String sSqlDet = "select * from fn_penjualan_print_kwt('" + noPenjualan + "') as (tgl_print text, jam_print text, no_invoice varchar, no_resep varchar, \n"
                + "tgl_resep text, nama_dokter varchar, nama_pasien varchar, no_r varchar, cara_pembuatan varchar, kode_barang varchar, \n"
                + "nama_barang varchar, qty_jual double precision, harga_jual double precision, qty_r double precision, sub_total double precision, \n"
                + "jenis_pembayaran varchar, total numeric, discount_bill double precision, bayar numeric, keterangan varchar, user_trx varchar)";
        System.out.println(sSqlDet);
        Statement stDet = conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
        ResultSet rsDet = stDet.executeQuery(sSqlDet);

        String nama = "";
        String alamat = "";
        String telp = "", keterangan = "", tanggal = "";
        String kasirs = "";
        String no_r = "", no_r1 = "";
        String ketR = "", sCara_pembuatan = "", sCara_pembuatan1 = "";
        int i = 1;
        String no = "";
        double totalBill = 0, bayar = 0;

        if (rsDet.next()) {
            nama = rsDet.getString("nama_pasien");
            keterangan = rsDet.getString("keterangan");
            tanggal = rsDet.getString("tgl_resep");
            kasirs = rsDet.getString("user_trx");
            totalBill = rsDet.getDouble("total");
            discBill = rsDet.getDouble("discount_bill");
            bayar = rsDet.getDouble("bayar");

        }
        out.write(padString("No. Nota", 10) + padString(":", 2) + padString(noPenjualan, 13) + padString(" # " + kasirs, 10)); //97
        out.newLine();
        out.write(padString("Pasien", 10) + padString(":", 2) + padString(nama, 26)); //97
        out.newLine();
        out.write(copyString("-", 40)); //11        s
        out.newLine();  //12        
        //out.write(padString("|",2)+padString("No",3)+padString("|",2)+padString("Nama Barang",15)+padString("|",2)+padString("Qty",5)+padString("|",2)+padString("Harga",8)+padString("|",1));
        out.write(padString("|", 3) + padString("Nama Barang", 21) + padString("|", 2) + padString("Qty", 4) + "|" + padString("Harga", 8) + padString("|", 1));
        out.newLine(); //14
        out.write(copyString("-", 40)); //15
        out.newLine(); //16
        rsDet.beforeFirst();
        int baris = 1;
        NumberFormat df = new DecimalFormat("###,###,###");
        float TotBayar = 0, fDiscountItem = 0;
        while (rsDet.next()) {
            sCara_pembuatan = rsDet.getString("cara_pembuatan").trim();
            no_r = rsDet.getString("no_r");
            r1 = rsDet.getString("no_r");
            no_r = no_r.substring(2, no_r.length());
            if ((r1.substring(0, 1).equalsIgnoreCase("R") || (r2.length() > 0 && r2.substring(0, 1).equalsIgnoreCase("R")))
                    && !r1.equalsIgnoreCase(r2)) {
                out.newLine();
            }
            if (sCara_pembuatan.equalsIgnoreCase("N")) {
                //out.write(padString("  ",3)+padString(rsDet.getString("nama_barang").trim(),19)+padString(" ",1)+rataKanan(nf_int.format(rs.getFloat("qty_jual")),4)+rataKanan(nf.format(rs.getDouble("jumlah")),16));
                out.write(padString("  ", 3) + padString(rsDet.getString("nama_barang").trim(), 21)
                        + padString(" ", 1) + rataKanan(rsDet.getString("qty_jual"), 5)
                        + padString(" ", 1) + rataKanan(df.format(rsDet.getDouble("sub_total")), 8));
//                jumlah+=rs.getDouble("jumlah");
            } else {
                if (!no_r.equalsIgnoreCase(no_r1)) {
                    out.newLine();
                    out.write(padString("R/", 3) + padString(" ", 21)
                            + padString(" ", 1) + rataKanan(nf_int.format(rsDet.getFloat("qty_r")), 5)
                            + rataKanan(nf.format(rsDet.getDouble("sub_total")), 9));
                    out.newLine();
                    out.write(padString("  ", 3) + padString(rsDet.getString("nama_barang").trim(), 21)
                            + padString(" ", 1) + rataKanan(nf_int.format(rsDet.getFloat("qty_jual")), 5));
//                    jumlah+=rs.getDouble("jumlah");
                } else {
                    out.write(padString("  ", 3) + padString(rsDet.getString("nama_barang").trim(), 21)
                            + padString(" ", 1) + rataKanan(nf_int.format(rsDet.getFloat("qty_jual")), 5));
                }
            }
            out.newLine();
            no_r1 = no_r;
            r2 = r1;
            sCara_pembuatan1 = sCara_pembuatan;
        }

        out.write(copyString("-", 40));
        out.newLine();
        out.write(padString("Sub Total", 10) + padString("", 16) + rataKanan(df.format(totalBill), 13));
        out.newLine();
        out.write(padString("Discount", 10) + padString("", 16) + rataKanan(df.format(discBill), 13));
        out.newLine();
//        float ppn=(TotBayar-discBill)*(fPPN/100);
        //out.write(padString("",1)+padString("PPN",17)+padString("(+)",5)+rataKanan(df.format(ppn),13)+padString("",2));

//        Statement sTotal=conn.createStatement();
//        ResultSet rsTotal = sTotal.executeQuery("select * from penjualan_bayar where no_penjualan='"+ noPenjualan +"'");
//        float dBayar =0;
//        if(rsTotal.next()){
//            dBayar = rsTotal.getFloat("bayar");
//        }
//        rsTotal.close();
//        sTotal.close();

        out.write(padString("BAYAR", 10) + padString("", 16) + rataKanan(df.format(bayar), 13));
        out.newLine();
        //out.write(padString("",1)+copyString("-",25));
        //out.newLine();
//        float tTotal=TotBayar-(discBill);
        //tTotal=tTotal+(tTotal*(fPPN/100));        
        kembali = bayar - totalBill;
        out.write(padString("Kembali", 10) + padString("", 16) + rataKanan(df.format(kembali), 13));
        out.newLine();
        Statement stBilang = conn.createStatement();
        ResultSet rsBilang = stBilang.executeQuery("select uang(" + df.format(totalBill).replace(",", "") + "::bigint) as terbilang");
        String terbilang = "";
        if (rsBilang.next()) {
            terbilang = rsBilang.getString(1).toLowerCase();
        }
        rsBilang.close();
        stBilang.close();

//        terbilang = "# "+terbilang+" Rupiah #";
//        out.write(italic());
//        out.write(padString(terbilang,100));
//        out.write(cancelItalic());
//        out.newLine();

        String tanggalN = "";
        Statement stTanggal = conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
        ResultSet rsTanggal = stTanggal.executeQuery("select to_char(now(), 'dd-MM-yyyy hh24:MI')");
        if (rsTanggal.next()) {
            //SimpleDateFormat sda = new SimpleDateFormat("dd-MM-yyyy HH:MM");
            tanggalN = rsTanggal.getString(1);
        }
        rsTanggal.close();
        stTanggal.close();


        out.newLine();
        out.write(padString("", 23) + padString(tanggalN, 16));
        //out.newLine();    
        //out.write(padString("",18)+padString("Kasir : "+sUser,20));
        /*out.write(padString("",18)+padString("Kasir",20));        
         out.newLine();
         out.newLine();
         out.newLine();
         out.write(padString("",18)+padString(sUser,10));
         */
        out.newLine();
        out.newLine();
        out.write(padString(".: Terima Kasih Semoga Lekas Sembuh :.", 40));
        out.newLine();
        out.write(printCutPaper());
        out.write(drawKick());
        out.close();
        return temp.getCanonicalFile();
    }

    private String copyString(String sTeks, int panjang) {
        String newString = "";
        for (int y = 1; y <= panjang; y++) {
            newString = sTeks + newString;
        }
        return newString;
    }

    private String rataKanan(String sTeks, int panjang) {
        String newText;

        newText = space(panjang - sTeks.length()) + sTeks;

        return newText;
    }

    private String padString(String sTeks, int panjang) {
        String newText;
        String jmSpace = "";
        if (sTeks.length() > panjang) {
            newText = sTeks.trim().substring(0, panjang);
        } else {
            newText = sTeks.trim();
        }


        for (int i = 0; i < (panjang - sTeks.trim().length()); i++) {
            newText = newText + " ";
        }

        return newText;
    }

    private String potongStringBaru(String sTeks, int panjang) {
        String newText;
        String jmSpace = "";
        if (sTeks.length() > panjang) {
            newText = sTeks.trim().substring(0, panjang);
        } else {
            newText = sTeks.trim();
        }

        for (int i = 0; i < (panjang - sTeks.trim().length()); i++) {
            newText = newText + " ";
        }

        String aText = "";
        String TextBaru = "";
        char[] a = newText.toCharArray();
        for (int i = 0; i < newText.trim().length(); i++) {
            if (String.valueOf(a[i]).toString().equalsIgnoreCase(" ")) {
                if (sTeks.trim().length() <= panjang) {
                    TextBaru = newText;
                } else {
                    TextBaru = aText + a[i] + a[i + 1];
                }
            }
            aText = aText + a[i];
        }

        for (int i = 0; i < (panjang - TextBaru.trim().length()); i++) {
            TextBaru = TextBaru + " ";
        }

        return TextBaru;
    }

    private String space(int iSpc) {
        String s = "";
        for (int i = 1; i <= iSpc; i++) {
            s = s + " ";
        }
        return s;
    }

    private String tengah(String sStr) {
        String s = "";
        int iTengah = (80 - sStr.length()) / 2;
        s = s + space(iTengah) + sStr;
        return s;
    }

    private String printCutPaper() {
        String str;
        str = String.valueOf((char) 29) + String.valueOf((char) 'V') + String.valueOf((char) 66) + String.valueOf((char) 0);

//    str = String.valueOf((char)29) +String.valueOf((char)'i');
        return str;
    }

    private String drawKick() {
        String str;
        str = String.valueOf((char) 27) + String.valueOf((char) 112) + String.valueOf((char) 0) + String.valueOf((char) 60) + String.valueOf((char) 120);
        return str;
    }

    private void printFile(File fileToPrint, PrintService service) {
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
        } catch (PrintException e) {
            System.out.println(e.getMessage());
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    private String resetPrn() {
        String str;
        str = String.valueOf((char) 27) + String.valueOf((char) 64);
        return str;
    }

    private String draft() {
        String str;
        str = String.valueOf((char) 27) + String.valueOf((char) 48);
        return str;
    }

    private String LQ() {
        String str;
        str = String.valueOf((char) 27) + String.valueOf((char) 49);
        return str;
    }

    private String bold() {
        String str;
        str = String.valueOf((char) 27) + String.valueOf((char) 69);
        return str;
    }

    private String cancelBold() {
        String str;
        str = String.valueOf((char) 27) + String.valueOf((char) 70);
        return str;
    }

    private String italic() {
        String str;
        str = String.valueOf((char) 27) + String.valueOf((char) 52);
        return str;
    }

    private String cancelItalic() {
        String str;
        str = String.valueOf((char) 27) + String.valueOf((char) 53);
        return str;
    }

    private String underLine() {
        String str;
        str = String.valueOf((char) 27) + String.valueOf((char) 45) + String.valueOf((char) 49);
        return str;
    }

    private String cancelUnderLine() {
        String str;
        str = String.valueOf((char) 27) + String.valueOf((char) 45) + String.valueOf((char) 48);
        return str;
    }

    private String cpi10() {
        String str;
        str = String.valueOf((char) 27) + String.valueOf((char) 80);
        return str;
    }

    private String cpi12() {
        String str;
        str = String.valueOf((char) 27) + String.valueOf((char) 77);
        return str;
    }

    private String cpi15() {
        String str;
        str = String.valueOf((char) 27) + String.valueOf((char) 103);
        return str;
    }

    private String cpi17() {
        String str;
        str = String.valueOf((char) 27) + String.valueOf((char) 80) + String.valueOf((char) 15);
        return str;
    }

    private String cpi20() {
        String str;
        str = String.valueOf((char) 27) + String.valueOf((char) 77) + String.valueOf((char) 15);
        return str;
    }

    
    private String condenced() {
        String str;
        str = String.valueOf((char) 27) + String.valueOf((char) 15);
        return str;
    }

    private String condensed_24() {
        String str;
        str = String.valueOf((char) 15);
        return str;
    }

    private String cancelCondenced() {
        String str;
        str = String.valueOf((char) 18);
        return str;
    }

    private String loadFront() {
        String str;
        str = String.valueOf((char) 27) + String.valueOf((char) 25) + String.valueOf((char) 70);
        return str;
    }

    private String DoubleStrike() {
        String str;
        str = String.valueOf((char) 27) + String.valueOf((char) 71);
        return str;

    }

    private String CancelDoubleStrike() {
        String str;
        str = String.valueOf((char) 27) + String.valueOf((char) 72);
        return str;

    }

    private String PitchPoint10() {
        String str;
        str = String.valueOf((char) 27) + String.valueOf((char) 88) + String.valueOf((char) 36 + String.valueOf((char) 0)) + String.valueOf((char) 21);
        return str;

    }

    private String NoPitchPoint() {
        String str;
        str = String.valueOf((char) 27) + String.valueOf((char) 88) + String.valueOf((char) 0);
        return str;

    }

    private String Space_1_per_36() {
        String str;
        str = String.valueOf((char) 27) + String.valueOf((char) 51) + String.valueOf((char) 5);
        return str;

    }

    private String Space_1_per_72() {
        String str;
        str = String.valueOf((char) 27) + String.valueOf((char) 51) + String.valueOf((char) 45);
        return str;

    }

    public void setConn(Connection con) {
        conn = con;
    }

    public void setHead(String head) {
        this.head = head;
    }

    public void setPrnUlang(String prnUlang) {
        this.prnUlang = prnUlang;
    }

    public void setService(PrintService service) {
        this.service = service;
    }

    public void print() {
        try {
            printFile(saveToTmpFile(), service);
        } catch (IOException e) {
            System.out.println("IO Err " + e.getMessage());
        } catch (SQLException se) {
            System.out.println("SQL Err " + se.getMessage());
        }
    }

    public static void main(String[] args) {


        Connection conn;
        String url = "jdbc:postgresql://localhost/apotek";
        try {
            Class.forName("org.postgresql.Driver");
        } catch (ClassNotFoundException ce) {
            System.out.println(ce.getMessage());
        }
        DocFlavor flavor = DocFlavor.SERVICE_FORMATTED.PRINTABLE;
        PrintService[] services = PrintServiceLookup.lookupPrintServices(flavor, null);

        try {
            conn = DriverManager.getConnection(url, "postgres", "bismillah");
            printPenjualanU220 pn = new printPenjualanU220(conn, "1310190007", "Ustadho", null);
        } catch (SQLException se) {
            System.out.println(se.getMessage());
        }
        Integer i = 0;
        Integer j = 0;
        Integer k = i + j;
        System.out.print(k);





    }
}