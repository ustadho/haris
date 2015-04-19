/*
 * phar_PRBean.java
 *
 * Created on December 13, 2006, 10:27 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package pembelian;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author root
 */
public class phar_GoodReceiptBean {
    private String ReleaseDate,sUserId,sUserName,Supplier,Remark,locationID,KodeGudang,expirate_date;
    private String no_po,no_gr,no_inv_do_sj,Kode_Barang,kode_supplier,UOM,Flag_Trx,dDate,No_retur_order,jenis_retur;
    private Double Kurs,Price,faktor;
    private Float Discount;
    private int Qty;
    private String shipping;

    private float hDiscount;

    private float hTax;

    private String userTerima;

    private String no_batch;

    private java.lang.Float PPN;
    private Integer TOP;
    private Connection conn;
    private String sTglJthTempo;
    private String sKeterangan;
    private float biayaKirim=0, biayaMaterai=0;
  
    
    /** Creates a new instance of phar_PRBean */
    public phar_GoodReceiptBean() {
    }
    
    public void setConn(Connection newConn){
        conn=newConn;
    }
    
    public void setUserId(String sid){
        sUserId=sid;
    }
    
    public void setNoBAtch(String sBatch){
        no_batch=sBatch;
    }
    
    public void setUserName(String sName){
        sUserName=sName;
    }
            
    public void setReleaseDate(String sTglRel){
        this.ReleaseDate=sTglRel;
    }
    
    public void setdDate(String sTgl){
        this.dDate=sTgl;
    }
     
    public void setExpiredDate(String sTgl){
        this.expirate_date=sTgl;
    }
     
    public void setSupplier(String sSupp){
        Supplier=sSupp;
    }
    
    public void setKode_Barang(String sKode){
        Kode_Barang=sKode;
    }
    
    public void setKode_Supplier(String sKode){
        kode_supplier=sKode;
    }
    
    public void setUOM(String sUOM){
        UOM=sUOM;
    }
    
    public void setNoPO(String snopo){
        no_po=snopo;
    }
    
    public void setNo_inv_do_sj(String snopo){
        no_inv_do_sj=snopo;
    }
    
    public void setKodeGudang(String sSite){
        KodeGudang=sSite;
    }
    
    public void setLocationId(String sLocation){
        locationID=sLocation;
    }
    
    public void setRemark(String sRemark){
        Remark=sRemark;
        sKeterangan=sRemark;
    }
    
        
    public void setKurs(Double dKurs){
        Kurs=dKurs;
    }
    
    public void setFaktor(Double dfakt){
        faktor=dfakt;
    }
        
    public void setJenisRetur(String sRet){
        jenis_retur=sRet;
    }
    
    public void setNo_retur_order(String snoRet){
        No_retur_order=snoRet;
    }
    
    public void setPrice(Double dPrice){
        Price=dPrice;
    }
    
    public void setQty(int iQty){
        Qty=iQty;
    }
    
    public void setFlag_Trx(String sFlag){
        Flag_Trx=sFlag;
    }
    
    public void setDiscount(Float fDisc){
        Discount=fDisc;
    }
    
    public void setPPN(Float fPPN){
        PPN=fPPN;
    }
    
    
    public void setTOP(Integer iTop){
        TOP=iTop;
    }
    
    
    
    public String add_Good_Receipt() throws SQLException{
        String sSql="select fn_get_kode_gr('"+ReleaseDate+"')";
        no_gr="";
        
        
        Statement stat=conn.createStatement(ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_UPDATABLE);
        ResultSet rs=stat.executeQuery(sSql);
        if (rs.next()){
            no_gr=rs.getString(1);
        }

        rs=stat.executeQuery("select * from good_receipt limit 0");
        rs.moveToInsertRow();
        rs.updateString("no_penerimaan", no_gr);
        rs.updateString("kode_supplier", kode_supplier);
        //rs.updateDate("tanggal", java.sql.Timestamp.valueOf(ReleaseDate));
        rs.updateString("no_sj_supplier", no_inv_do_sj);
        rs.updateString("shipping", getShipping());
        rs.updateFloat("discount", Discount);
        rs.updateFloat("tax", PPN);
        rs.updateString("user_terima", userTerima);
        rs.updateString("keterangan", sKeterangan);
        rs.updateString("user_trx", sUserName);
        rs.updateString("kode_gudang", KodeGudang);
        rs.updateDate("tgl_jatuh_tempo", java.sql.Date.valueOf(sTglJthTempo));
        rs.updateFloat("biaya_lain", biayaKirim);
        rs.updateFloat("biaya_materai", biayaMaterai);
        rs.insertRow();
        
        stat.close();
        rs.close();
        return no_gr;
    }
    
    public void add_Good_Receipt_detail(String sNoGR) throws SQLException{
//        if (Discount>0 && Discount<=100){
//            Discount=(Qty.floatValue()*Price.floatValue())*(Discount/100);
//        }
//        if (PPN>0 && PPN<=100){
//            if (Discount>0 && Discount<=100){
//                PPN=((Qty.floatValue()*Price.floatValue())-Discount)*(PPN/100);
//            }else{PPN=((Qty.floatValue()*Price.floatValue())-Discount)*(PPN/100);}
//        }
        String sSql="";
        Statement stat=conn.createStatement();
        sSql="insert into good_receipt_detail(no_penerimaan, no_po, item_code,jumlah,harga,discount, tax, no_batch, exp_date) " +
             "values('"+sNoGR+"', '"+no_po+"', " +
             "'"+Kode_Barang+"',"+Qty+","+Price+","+Discount+","+PPN+",'"+no_batch+"', case when '"+expirate_date+"'='' then null else '"+expirate_date+"' end )";
        System.out.println(sSql);
        stat.executeUpdate(sSql);
        stat.close();
    }
    
    public void add_Good_Receipt_detail_update(String sNoGR) throws SQLException{
        String sSql="";
        Statement stat=conn.createStatement();
//        if (Discount>0 && Discount<=100){
//            Discount=(Qty.floatValue()*Price.floatValue())*(Discount/100);
//        }
//        if (PPN>0 && PPN<=100){
//            if (Discount>0 && Discount<=100){
//                PPN=((Qty.floatValue()*Price.floatValue())-Discount)*(PPN/100);
//            }else{PPN=((Qty.floatValue()*Price.floatValue())-Discount)*(PPN/100);}
//        }
        
        sSql="insert into phar_good_receipt_detail(good_receipt_id,kode_barang,jumlah,harga,discount,ppn,expired_date) " +
             "select good_receipt_id,kode_barang,-jumlah,harga,discount,ppn,expired_date from phar_good_receipt_detail where good_receipt_id='"+sNoGR+"' and kode_barang='"+Kode_Barang+"'";
        stat.executeUpdate(sSql);
        sSql="insert into phar_good_receipt_detail(good_receipt_id,kode_barang,jumlah,harga,discount,ppn,expired_date) values('"+sNoGR+"'," +
             "'"+Kode_Barang+"',"+Qty+","+Price+","+Discount+","+PPN+",'"+expirate_date+"')";
        stat.executeUpdate(sSql);
        stat.close();
    }
    
 public void Update_phar_Good_Receipt_4koreksi(String sno_gr) throws SQLException{
    String sSql="";
    Statement stat=conn.createStatement();
    sSql="update phar_good_receipt set site_id='"+KodeGudang+"',location_id='"+locationID+"',remarks='"+Remark+"',no_inv_do_sj='"+no_inv_do_sj+"'," +
         "user_upd='"+sUserName+"',time_upd=now() " +
         "where no_po='"+sno_gr+"'";
    
    stat.executeUpdate(sSql);
    //System.out.println(sSql);
    stat.close();
}
 
     
 public String add_Good_return() throws SQLException{
        String sSql="select fn_phar_get_no_return2supp('"+sUserId+"') as no_gr";
        no_gr="";
        Statement stat=conn.createStatement();
        ResultSet rs=stat.executeQuery(sSql);
        if (rs.next()){
            no_gr=rs.getString("no_gr");
        }

        sSql="insert into phar_return_to_supplier(no_return_supp,tanggal,kode_supplier,site_id,location_id,no_retur_order,jenis_retur) " +
             "values('"+no_gr+"',now(),'"+kode_supplier+"','"+KodeGudang+"','"+locationID+"','"+No_retur_order+"','"+jenis_retur+"')";
        stat.executeUpdate(sSql);
        stat.close();
        rs.close();
        return no_gr;
    }
    
  public void add_Good_return_detail(String sNoGR) throws SQLException{
        String sSql="";
        Statement stat=conn.createStatement();
        sSql="insert into phar_return_to_supplier_detail(no_return_supp,kode_barang,qty,price,uom,factor,expired_date) values('"+sNoGR+"'," +
             "'"+Kode_Barang+"',"+Qty+","+Price+",'"+UOM+"',"+faktor+",'"+expirate_date+"')";
     //   System.out.println(sSql);
        stat.executeUpdate(sSql);
        stat.close();
    }
  
  
  public void setPO_closed(String sNo_PO) throws SQLException{
        String sSql="";
        Statement stat=conn.createStatement();
        sSql="update phar_po set closed=true where no_po='"+sNo_PO.trim()+"'";
        stat.executeUpdate(sSql);
        stat.close();
  }

    public String getShipping() {
        return shipping;
    }

    public void setShipping(String shipping) {
        this.shipping = shipping;
    }

    public void setHDiscount(float hDiscount) {
        this.hDiscount = hDiscount;
    }

    public void setHTax(float hTax) {
        this.hTax = hTax;
    }

    public void setUserTerima(String userTerima) {
        this.userTerima = userTerima;
    }

    boolean koreksiPenerimaan(String text) {
        boolean b=true;
        try {
            Statement st = conn.createStatement();
            ResultSet rs = st.executeQuery("select fn_koreksi_gr('" + text + "')");
            
            if(rs.next()){
                b=true;
            }else{
                b=false;
            }
            
        } catch (SQLException ex) {
            Logger.getLogger(phar_GoodReceiptBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return b;
    }

    void setBiayaKirim(float udfGetFloat) {
        biayaKirim=udfGetFloat;
    }

    void setBiayaMaterai(float udfGetFloat) {
        biayaMaterai=udfGetFloat;
    }

    void setTglJthTempo(String sTglJthTempo) {
        this.sTglJthTempo=sTglJthTempo;
    }
  
}
