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

/**
 *
 * @author root
 */
public class phar_PRBean {
    private String LokasiId,SiteId,ReleaseDate,NeededDate,sUserId,sUserName,acc1,acc2;
    private String no_pr,RequestBy,LocationId,Kode_Barang,kode_supplier,UOM,Flag_Trx,dDate;
    private Double Qty;
    private Boolean Automatic;
    private Connection conn;
    /** Creates a new instance of phar_PRBean */
    public phar_PRBean() {
    }
    
    public void setConn(Connection newConn){
        conn=newConn;
    }
    
    public void setUserId(String sid){
        sUserId=sid;
    }
    
    public void setUserName(String sName){
        sUserName=sName;
    }
            
    public void setLocationId(String sLokasi){
        this.LocationId=sLokasi;
    }
    
    public void setSiteId(String sIdSite){
        this.SiteId=sIdSite;
    }
    
    public void setReleaseDate(String sTglRel){
        this.ReleaseDate=sTglRel;
    }
    
    public void setNeededDate(String sTglNeed){
        this.NeededDate=sTglNeed;
    }    
    
    public void setRequestedBy(String sReqBy){
        RequestBy=sReqBy;
    }
    
    public void setAutomatic(Boolean auto){
        Automatic=auto;
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
    
    public void setQty(Double iQty){
        Qty=iQty;
    }
    
    public void setFlag_Trx(String sFlag){
        Flag_Trx=sFlag;
    }
    
    public void setAcc_level_1(String sAcc){
        acc1=sAcc;
    }
    public void setAcc_level_2(String sAcc){
        acc2=sAcc;
    }
    
    public void setdDate(String sTgl){
        this.dDate=sTgl;
    }
    
    public String add_PR() throws SQLException{
        String sSql="select fn_phar_get_no_pr('"+sUserId+"') as no_pr";
        no_pr="";
        Statement stat=conn.createStatement();
        ResultSet rs=stat.executeQuery(sSql);
        if (rs.next()){
            no_pr=rs.getString("no_pr");
        }

        sSql="insert into phar_pr(no_pr,site_id,release_date,requested_by,need_date,automatic) values('"+no_pr+"'," +
             "'"+SiteId+"','"+ReleaseDate+"','"+RequestBy+"','"+NeededDate+"',"+Automatic+")";
        stat.executeUpdate(sSql);
        stat.close();
        rs.close();
        return no_pr;
    }
    
    public void add_PR_detail(String sNoPR) throws SQLException{
        String sSql="";
        Statement stat=conn.createStatement();
        sSql="insert into phar_pr_detail(no_pr,kode_barang,jumlah,uom,kode_supp,location_id) values('"+sNoPR+"'," +
             "'"+Kode_Barang+"',"+Qty+",'"+UOM+"','"+kode_supplier+"','"+LocationId+"')";
        stat.executeUpdate(sSql);
        stat.close();
    }
    
    public void Koreksi_PR(String snoPR) throws SQLException{
        Statement stat=conn.createStatement();
        String sSql="update phar_pr set flag_tr='K' where no_pr='"+snoPR+"'";
        stat.executeUpdate(sSql);
        
        sSql="select fn_phar_get_no_pr('"+sUserId+"') as no_pr";
        String no_pr_new="";
        ResultSet rs=stat.executeQuery(sSql);
        if (rs.next()){
            no_pr_new=rs.getString("no_pr");
        }
        rs.close();
        sSql="insert into phar_pr(no_pr,site_id,release_date,requested_by,rutin," +
             "automatic,need_date,flag_tr,keterangan) " +
             "select '"+no_pr_new.trim()+"',site_id,release_date,requested_by,rutin," +
             "automatic,need_date,'K','Koreksi dari no_PR="+snoPR.trim()+"' " +
             "from phar_pr where no_pr='"+snoPR.trim()+"'";
        stat.executeUpdate(sSql);
        sSql="insert into phar_pr_detail(no_pr,kode_barang,jumlah,uom,kode_supp,no_po,location_id) " +
             "select '"+no_pr_new.trim()+"',kode_barang,-jumlah,uom,kode_supp,no_po,location_id " +
             "from phar_pr_detail where no_pr='"+snoPR.trim()+"'";
        stat.executeUpdate(sSql);
        stat.close();
        
    }
    
public void Update_phar_pr_ACC(String sno_pr) throws SQLException{
    String sSql="";
    Statement stat=conn.createStatement();
    sSql="update phar_pr set acc_level_1='"+acc1+"',acc_level_2='"+acc2+"'," +
         "user_upd='"+sUserName+"',time_upd='"+dDate+"' " +
         "where no_pr='"+sno_pr+"'";
    stat.executeUpdate(sSql);
    stat.close();
}
    
}
