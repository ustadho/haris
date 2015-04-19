/*
 * TarifPeriksaBean.java
 *
 * Created on November 13, 2006, 8:44 AM
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
public class Phar_TarifBarangBean {
    private String JenisId,LokasiId,SiteId,KodeMilik,Keterangan,GroupId,BentukId;
    private String nmPeriksa,base_price_type;
    private String KodeBarang;
    private String NamaBarang,NamaPatent,UOM,barcode;
    private String kelas;
    private Boolean DisContinued,Automatic,Consignment;
    private float BasePrice,Min,Max;
    private Connection conn;
    private String sQry="select * from fn_phar_show_item() " +
                 "as(kode_barang varchar,nama_barang varchar,patent_name varchar,uom_kecil varchar," +
                 "keterangan varchar,min float4,max float4,kode_milik varchar,unit varchar,discontinued bool," +
                 "barcode varchar,automatic bool,consignment bool,group_id varchar,group_name varchar,jenis_id varchar," +
                 "jenis_barang varchar,bentuk_id varchar,bentuk_name varchar,site_id varchar,location_id varchar," +
                 "location varchar,base_price numeric) ";
    
    private String sQry1="select kode_barang,nama_barang,uom_kecil,base_price from phar_item ";
    
    /**
     * Creates a new instance of TarifPeriksaBean
     */
    public Phar_TarifBarangBean() {
    }
    
    
    public String getQryShort(){
        return sQry1;
    }
    
    public void setKodeBarang(String sKode){
        this.KodeBarang=sKode;
    }
    
    public void setNamaBarang(String sNamaBarang){
        this.NamaBarang=sNamaBarang;
    }
    
    public void setNamaPatent(String sNamaPatent){
        this.NamaPatent=sNamaPatent;
    }
    
    public void setJenisId(String sId){
        this.JenisId=sId;
    }
    
    public void setUOM(String sUOM){
        this.UOM=sUOM;
    }
    
    public void setMin(float fMin){
        this.Min=fMin;
    }
    
    public void setMax(float fMax){
        this.Max=fMax;
    }
    
    public void setKodeMilik(String sKode){
        this.KodeMilik=sKode;
    }
    
    public void setIdLokasi(String sLokasi){
        this.LokasiId=sLokasi;
    }
    
    public void setSiteId(String sIdSite){
        this.SiteId=sIdSite;
    }
    
    public void setBentukId(String sBentukId){
        this.BentukId=sBentukId;
    }
    
    public void setIdGroup(String sIdGroup){
        this.GroupId=sIdGroup;
    }
    
    public void setBarCode(String sBarCode){
        this.barcode=sBarCode;
    }
    
    public void setKeterangan(String sKet){
        this.Keterangan=sKet;
    }

    public void setbase_price_type(String sbase_price_type){
        this.base_price_type=sbase_price_type;
    }
    
    
    public void setAutomatic(Boolean sAutomatic){
        this.Automatic=sAutomatic;
    }
    
    public void setConsignment(Boolean sConsignment){
        this.Consignment=sConsignment;
    }
    
    public void setDisContinued(Boolean sDisContinued){
        this.DisContinued=sDisContinued;
    }
    
    public void setBasePrice(Float fBasePrice){
        this.BasePrice=fBasePrice;
    }
    
    
    public Connection getConn() {
        return conn;
    }
    
    public void setConn(Connection conn) {
        this.conn = conn;
    }
    
    public String getQryFilter(String sCol, String sOpr,String sValue){
        //String sQry="select kode_periksa, nama_pemeriksaan,  FROM rad_jenis_periksa ";
        String sFilter;
        if(sCol.equalsIgnoreCase("ALL")){
//            String sFtr=sOpr=="Like" ? "'%"+ sValue.toUpperCase() + "%' " : " '"+ sValue.toUpperCase() + "' ";
//            sFilter ="WHERE upper(kode_barang||nama_barang||patent_name||uom_kecil||" +
//                 "keterangan||min||max||kode_milik||unit||discontinued||" +
//                 "barcode||automatic||consignment||group_id||group_name||jenis_id||" +
//                 "jenis_barang||bentuk_id||bentuk_name||site_id||location_id||" +
//                 "location||base_price) like "+sFtr.toUpperCase();
            sFilter="";
        }
        else{
            String sFtr=sOpr=="Like" ? "'%"+ sValue + "%' " : " '"+ sValue + "' ";
            sFilter ="WHERE upper("+sCol+ ") " + sOpr +sFtr.toUpperCase();
                }
            
        sQry = sQry + sFilter+" order by 1";
        return sQry;
    }
    
    public String getQryFilterShort(String sCol, String sOpr,String sValue){
        //String sQry="select kode_periksa, nama_pemeriksaan,  FROM rad_jenis_periksa ";
        String sFilter;
        if(sCol.equalsIgnoreCase("ALL")){
            sFilter="";
        }
        else{
            String sFtr=sOpr=="Like" ? "'%"+ sValue + "%' " : " '"+ sValue + "' ";
            sFilter ="WHERE upper("+sCol+ ") " + sOpr +sFtr.toUpperCase();
                }
            
        sQry1 = sQry1 + sFilter+" order by 1";
        return sQry1;
    }
    
    public String getQrySearch(String sValue){
        sQry=sQry+"Where upper(kode_barang||nama_barang||patent_name||uom_kecil||" +
                 "keterangan||min||max||kode_milik||unit||discontinued||" +
                 "barcode||automatic||consignment||group_id||group_name||jenis_id||" +
                 "jenis_barang||bentuk_id||bentuk_name||site_id||location_id||" +
                 "location||base_price) like '%"+sValue+"%'";
        return sQry;
    }
    
    public String getQrySearchShort(String sValue){
        sQry1=sQry1+"Where upper(kode_barang||nama_barang||uom_kecil||base_price) like upper('%"+sValue+"%')";
        return sQry1;
    }
        
    public boolean Add() throws SQLException{
//        String sQry="select  fn_rad_new_nama_periksa('" + idPeriksa +"','" +nmPeriksa+"','"+singkatan+"','"+kode_jenis+"')";
//        
//        SimpleDateFormat sd = new SimpleDateFormat("yyyy-MM-dd");
        boolean st=false;
//        
//            Statement stm = conn.createStatement();
//            System.out.println(sQry);
//            ResultSet rsTr = stm.executeQuery(sQry);
//            if (rsTr.next()){
//                idPeriksa=rsTr.getString(1).trim();
//            }
//            rsTr.close();
//            stm.close();
            st=true;
        return st;
        }
    
      
    public void Edit(String KodeBarange)throws SQLException{
        String sUpdate= "UPDATE phar_item " +
                        "set nama_barang ='"+ NamaBarang+"', " +
                        "    patent_name  ='"+ NamaPatent   +"'," +
                        "    uom_kecil='"+UOM+"'," +
                        "    keterangan='"+Keterangan+"',"+
                        "    min="+Min+","+
                        "    max="+Max+","+
                        "    kode_milik='"+KodeMilik+"',"+
                        "    barcode='"+barcode+"',"+
                        "    group_id='"+GroupId+"',"+
                        "    jenis_id='"+JenisId+"',"+
                        "    bentuk_id='"+BentukId+"',"+
                        "    site_id='"+SiteId+"',"+
                        "    location_id='"+LokasiId+"',"+
                        "    base_price_type='"+base_price_type+"',"+
                        "    base_price="+BasePrice+","+
                        "    user_upd=current_user,"+
                        "    time_upd=now() "+
                        "where kode_barang='"+ KodeBarange+"'";
            //System.out.println(sUpdate);
            Statement stm = conn.createStatement();
            stm.executeUpdate(sUpdate);
            stm.close();
    }
    
    public void Edit1(String KodeBarange)throws SQLException{
        String sUpdate= "UPDATE phar_item " +
                        "set base_price="+BasePrice+","+
                        "    user_upd=current_user,"+
                        "    time_upd=now() "+
                        "where kode_barang='"+ KodeBarange+"'";
            //System.out.println(sUpdate);
            Statement stm = conn.createStatement();
            stm.executeUpdate(sUpdate);
            stm.close();
    }
        
    public String Delete(String kodeBarange) throws SQLException{
        String txt="";
        conn.setAutoCommit(false);
        Statement stm = conn.createStatement();
        stm.executeUpdate("DELETE FROM phar_item " +
                          "WHERE kode_barang='"+kodeBarange+"'");
        stm.close();
            
        return txt;
    }
   
    public String[] getFieldName() {
        String kueri=sQry;
        
        String[] myCol={};
        try{
            Statement st=conn.createStatement();
            ResultSet rs=st.executeQuery(kueri);
            int jmlKolom=rs.getMetaData().getColumnCount();
            
            myCol=new String[jmlKolom];
            for(int i=0;i<jmlKolom;i++){
                myCol[i]=rs.getMetaData().getColumnName(i+1);
            }
            rs.close();
            st.close();
            
        }catch(SQLException se){System.out.println(se.getMessage());}
        return myCol;
    }
    
        public String[] getFieldNameShort() {
        String kueri=sQry1;
        String[] myCol={};
        try{
            Statement st=conn.createStatement();
            ResultSet rs=st.executeQuery(kueri);
            int jmlKolom=rs.getMetaData().getColumnCount();
            
            myCol=new String[jmlKolom];
            for(int i=0;i<jmlKolom;i++){
                myCol[i]=rs.getMetaData().getColumnName(i+1);
            }
            rs.close();
            st.close();
            
        }catch(SQLException se){System.out.println(se.getMessage());}
        return myCol;
    }
}


