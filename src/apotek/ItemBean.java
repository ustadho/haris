/*
 * ItemBean.java
 *
 * Created on December 7, 2006, 11:31 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package apotek;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 *
 * @author root
 */
public class ItemBean {
    private String kode;
    private String nama;
    private String patent;
    private String uom;
    private String ket;
    private float min;
    private float max;
    private String satuan_besar;
    private boolean discontinued;
    private String barcode;
    private boolean automatic;
    private boolean consignment;
    private String group_id;
    private String jenis_id;
    private String bentuk_id;
    private String site;
    private String location;
    private String base_price_type;
    
    //variabel untuk supplier-barang
    private float convertion ;
    private String old_kode_supp;
    private String kode_supplier;
    private float price;
    private float disc;
    private float bonus;
    private float vat;
    
    private Connection conn;

    private String kode_sama;

    private float berat;

    private float base_price;

    private boolean showInInvoice;

    private boolean status_pakai;

    private int default_brows;

    private int konversi;

    private float harga_besar_resep;

    private float harga_besar_non_resep;

    private String satuan_kecil;

    private float harga_kecil_resep;

    private float harga_kecil_non_resep;

    private String gudang;

    private String kode_jenis;

    private String keterangan;

    private String sKodeAsal;
    private boolean isDispensing;
    /** Creates a new instance of ItemBean */
    public ItemBean() {
    
    }
    
    public void setConn(Connection nCon){
        this.conn=nCon;
    }
    
    public void setNama(String s){
        nama=s;
    }
    
    public void setDefaultBrows(int i){
        setDefault_brows(i);
    }
    
    public void setKode(String sKode){
        this.kode=sKode;
    }
    
    public void setStatusPakai(boolean b){
        setStatus_pakai(b);
    }
    
    public void setPatentName(String sPatent){
        this.patent=sPatent;
    }
    public void setUom(String sUom){
        this.uom=sUom;
    }
    public void setKeterangan(String sKet){
        this.ket=sKet;
        keterangan=sKet;
    }
    public void setMin(float fMin){
        this.min=fMin;
    }
    public void setMax(float fMax){
        this.max=fMax;
    }
    public void setSatuanBesar(String sMilik){
        this.satuan_besar=sMilik;
    }
    public void setDiscontinued(boolean bDisc){
        this.discontinued=bDisc;
    }
    public void setBarcode(String sBarcode){
        this.barcode=sBarcode;
    }
    public void setAoutomatic(boolean bAouto){
        this.automatic=bAouto;
    }
    public void setConsignment(boolean sConsignment){
        this.consignment=sConsignment;
    }
    public void setGroupID(String sGroup){
        this.group_id=sGroup;
    }
    public void setJenisID(String sJenis){
        this.jenis_id=sJenis;
    }
    public void setBentukID(String sBentuk){
        this.bentuk_id=sBentuk;
    }
    public void setSiteID(String sSite){
        this.site=sSite;
    }
    public void setLocation(String sLoc){
        this.location=sLoc;
    }
    public void setBasePrice(String sBprice){
        this.base_price_type=sBprice;
    }
    
    public void setKodeSama(String nKode){
        setKode_sama(nKode);
    }
    
    public void setBerat(float nBerat){
        berat=nBerat;
    }
    
    public void setBasePrice(float fBase){
        base_price=fBase;
    }
    
    //set untuk supplir - barang
    public void setSupplier(String sKdSupp){
        this.kode_supplier=sKdSupp;
    }
    public void setOldSupplier(String sKdSupp){
        this.old_kode_supp=sKdSupp;
    }
    public void setKonversi(float konv){
        this.convertion=konv;
    }
    public void setPrice(float fPrice){
        this.price=fPrice;
    }
    public void setDisc(float fDisc){
        this.disc=fDisc;
    }
    public void setBonus(float fBonus){
        this.bonus=fBonus;
    }
    public void setVat(float fVat){
        this.vat=fVat;
    }
    
    public String udfGetNewKode(){
        return this.kode;
    }
    
    public boolean AddItem() throws SQLException{
        boolean st=false;
            String sQry="select fn_new_barang('" + kode +"','" +nama+"'," +
                        ""+status_pakai+","+default_brows+","+min+","+max+",'"+satuan_besar+"'," + 
                        ""+convertion+","+harga_besar_resep+","+harga_besar_non_resep+"," +
                        "'"+satuan_kecil+"',"+harga_kecil_resep+","+harga_kecil_non_resep+",'"+location+"','"+barcode+"'," +
                        "'"+kode_sama+"', '"+kode_jenis+"', '"+getKeterangan()+"',"+base_price+")";
        
            Statement stm = conn.createStatement();
            
            System.out.println(sQry);
            ResultSet rsTr = stm.executeQuery(sQry);
            if (rsTr.next()){
                kode=rsTr.getString(1).trim();
                setKode(rsTr.getString(1).trim());
            }
            rsTr.close();
            stm.close();
            st=true;
        return st;
        }
    public int EditItem(){
        String sUpdate="";
        int i=0;
            
        sUpdate="update barang Set " +
                        "item_name='"+nama+"', " +
                        "discontinued="+status_pakai+", " +
                        "default_brows="+default_brows+", " +
                        "min_stock="+min+"," +
                        "max_stock="+max+","+
                        "satuan_besar='"+satuan_besar+"', "+
                        "konversi="+convertion+", " + 
                        "harga_besar_resep="+harga_besar_resep+", " +
                        "harga_besar_non_resep="+harga_besar_non_resep+", " +
                        "satuan_kecil='"+satuan_kecil+"', " +
                        "harga_kecil_resep="+harga_kecil_resep+", " +
                        "harga_kecil_non_resep="+harga_kecil_non_resep+"," +
                        "kode_lokasi='"+location+"', "+
                        "barcode='"+barcode+"', " +
                        "kode_barang_sama='"+kode_sama+"', " +
                        "kode_jenis='"+kode_jenis+"'," +
                        "keterangan='"+keterangan+"'," +
                        "is_dispensing="+isDispensing+" " + 
                        "where item_code='"+sKodeAsal+"'";
        try{
            conn.setAutoCommit(false);
            System.out.println(sUpdate);
            Statement stm = conn.createStatement();
            i=stm.executeUpdate(sUpdate);
            stm.close();
         }catch(SQLException se){System.out.println(se.getMessage()); }
       return i;
         
    }
    
    public boolean addItemSupp() throws SQLException{
        boolean st=false;
            String sQry="select fn_new_item_supp('" + kode +"','" +kode_supplier+"'," +
                        "'"+uom+"','"+convertion+"',"+price+","+disc+",'"+bonus+"',"+vat+")";
        
            Statement stm = conn.createStatement();
            
            System.out.println(sQry);
            ResultSet rsTr = stm.executeQuery(sQry);
            if (rsTr.next()){
                kode=rsTr.getString(1).trim();
            }
            rsTr.close();
            stm.close();
            st=true;
        return st;
        }
    
    public boolean setDefaultBasePrice()throws SQLException{
        int hasil;
        boolean st=false;
        
        Statement stm=conn.createStatement();
        hasil =stm.executeUpdate("Update barang set base_price="+base_price+" where item_code='"+kode+"'");
        
        st =hasil==0 ?false: true;
        return st;
    }
    
    public int EditItemSupp(){
        String sUpdate="";
        int i=0;
            
        sUpdate="update supplier_barang Set " +
                        "kode_barang='"+kode+"'," +
                        "kode_supplier='"+kode_supplier+"', " +
                        "uom_alt='"+uom+"', " +
                        "konversi="+convertion+", " +
                        "price="+price+"," +
                        "disc="+disc+","+
                        "bonus="+bonus+","+
                        "vat="+vat+" "+
                        "where kode_barang='"+kode+"' and kode_supplier='"+old_kode_supp+"' ";
        try{
            conn.setAutoCommit(false);
            System.out.println(sUpdate);
            Statement stm = conn.createStatement();
            i=stm.executeUpdate(sUpdate);
            stm.close();
         }catch(SQLException se){System.out.println(se.getMessage()); }
       return i;
    }
    
    public int DeleteItem(String sKode)throws SQLException{
        int i=0, i1=0, i2=0;
        
        String sDel="";
        String sDelIKelas="";
        String sDelISupp="";
        
        sDelIKelas="Delete from barang where item_code='"+sKode+"'";
//        sDelISupp="Delete from phar_supplier_barang where kode_barang='"+sKode+"'";
//        sDel="Delete from phar_item where kode_barang='"+sKode+"'";
//        
        conn.setAutoCommit(false);
        System.out.println(sDelIKelas+"; "+ sDelISupp+"; "+ sDel);
        Statement stm = conn.createStatement();
        i=stm.executeUpdate(sDelIKelas+"; "+ sDelISupp+"; "+ sDel);
        
        stm.close();
        
        return i;
        
    }
    
    public int DeleteItemSupp(String sKode,String sSupp)throws SQLException{
        int i=0 ;
        
        String sDel="";
        
        sDel="Delete from supplier_barang where kode_barang='"+sKode+"' and kode_supplier='"+sSupp+"'";
        
        conn.setAutoCommit(false);
        System.out.println(sDel);
        Statement stm = conn.createStatement();
        i=stm.executeUpdate(sDel);
        
        stm.close();
        
        return i;
        
    }

    void setIsDispensing(boolean selected) {
        isDispensing=selected;
    }

    void setShowInInvoice(boolean b) {
        showInInvoice=b;
    }

    public void setKode_sama(String kode_sama) {
        this.kode_sama = kode_sama;
    }

    public void setStatus_pakai(boolean status_pakai) {
        this.status_pakai = status_pakai;
    }

    public void setDefault_brows(int default_brows) {
        this.default_brows = default_brows;
    }

    public void setKonversi(int konversi) {
        this.konversi = konversi;
    }

    public void setHarga_besar_resep(float harga_besar_resep) {
        this.harga_besar_resep = harga_besar_resep;
    }

    public void setHarga_besar_non_resep(float harga_besar_non_resep) {
        this.harga_besar_non_resep = harga_besar_non_resep;
    }

    public void setSatuan_kecil(String satuan_kecil) {
        this.satuan_kecil = satuan_kecil;
    }

    public void setHarga_kecil_resep(float harga_kecil_resep) {
        this.harga_kecil_resep = harga_kecil_resep;
    }

    public void setHarga_kecil_non_resep(float harga_kecil_non_resep) {
        this.harga_kecil_non_resep = harga_kecil_non_resep;
    }

    public void setGudang(String gudang) {
        this.gudang = gudang;
    }

    public void setKode_jenis(String kode_jenis) {
        this.kode_jenis = kode_jenis;
    }

    public String getKeterangan() {
        return ket;
        
    }

    String getKode() {
        return kode;
    }

    void setKodeAsal(String string) {
        this.sKodeAsal = string;
    }
    
}
