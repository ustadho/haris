package apotek;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import javax.swing.text.MaskFormatter;

/**
 *
 * @author root
 */
public class SupplierBean {
    private String kode_supplier;
    private String nama_supplier;
    private String alamat;
    private String kode_kota;
    private String telp;
    private String contact_person;
    private String hp;
    private String keterangan;
    private int top;
    private boolean bActive;
    
    private String sQry="select * from rm_unit ";
    
    public static String sID="";
    static String sTgl="";
    private Connection conn;

    private String kode_jenis_supp;
    
    /** Creates a new instance of UnitBean*/
    public SupplierBean() {
    }
    
    public void setKodeSupp(String sId){
        this.kode_supplier=sId;
    }
    
    public void setNamaSup(String sNama){
        this.nama_supplier=sNama;
    }
    
    public void setAlamat(String spv){
        this.alamat=spv;
    }
    
    public void setKodeKota(String sKota){
        this.kode_kota=sKota;
    }
    
    public void setTelepon(String sTelp){
        this.telp=sTelp;
    }
    
    public void setContactPerson(String sCp){
        this.contact_person=sCp;
    }
    
    public void setHp(String sHp){
        this.hp=sHp;
    }
    
    public void setKeterangan(String sKet){
        this.keterangan=sKet;
    }
    
    public void setTop(int iTop){
        this.top=iTop;
    }
    
    public void setKodeJenis(String sJenis){
        this.kode_jenis_supp=sJenis;
    }
    
    public void setActive(Boolean bActive){
        this.bActive=bActive;
    }
    
    public String getKodeSupp(){
        return kode_supplier;
    }
    
    public String getNamaSup(){
        return nama_supplier;
    }
    
    public String getAlamat(){
        return alamat;
    }
    
    public String getKodeKota(){
        return kode_kota;
    }
    
    public String getTelepon(){
        return telp;
    }
    
    public String getContactPerson(){
        return contact_person;
    }
    
    public String getHp(){
        return hp;
    }
    
    public String getKeterangan(){
        return keterangan;
    }
    
    public int getTop(){
        return top;
    }    
    
    public String getKodeJenis(){
        return kode_jenis_supp;
    }    
    
    public Boolean getActive(){
        return bActive;
    }  
    
    public Connection getConn() {
        return conn;
    }
    
    public void setConn(Connection conn) {
        this.conn = conn;
    }
    
    public String getNewCode(String s){
        Statement st;
        String sNew="";
        try {
            st = conn.createStatement();
            ResultSet rs=st.executeQuery("select fn_get_kode_supp('"+s+"')");
            
            if(rs.next())
                sNew=rs.getString(1);
            
            rs.close();
            st.close();
            
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return sNew;
    }
    
    public boolean Add() throws SQLException{
        SimpleDateFormat sd = new SimpleDateFormat("yyyy-MM-dd");
        boolean st=false;
            String sQry="select fn_phar_new_supp('" + kode_supplier +"','" +nama_supplier+"','"+alamat+"','"+kode_kota+"','"+telp+"'," +
                        "'"+contact_person+"','"+hp+"','"+keterangan+"',"+top+",'"+kode_jenis_supp+"',"+bActive+")";
        
            Statement stm = conn.createStatement();
            
            System.out.println(sQry);
            ResultSet rsTr = stm.executeQuery(sQry);
            if (rsTr.next()){
                kode_supplier=rsTr.getString(1).trim();
            }
            rsTr.close();
            stm.close();
            st=true;
        return st;
        }
    
    public int Edit(String idUnit)throws SQLException{
        String sUpdate= "Update phar_supplier set " +
                        "kode_supplier='"+kode_supplier+"', " +
                        "nama_supplier='"+nama_supplier+"' , " +
                        "alamat= '"+alamat+"', " +
                        "kode_kota='"+kode_kota+"', " +
                        "telp='"+telp+"', " +
                        "contact_person='"+contact_person+"', " +
                        "hp='"+hp+"', " +
                        "keterangan='"+keterangan+"', " +
                        "top="+top+", " +
                        "kode_jenis_supp='"+kode_jenis_supp+"' " +
                        " Where kode_supplier='"+kode_supplier+"'";
        
        int i=0;
            conn.setAutoCommit(false);
            System.out.println(sUpdate);
            Statement stm = conn.createStatement();
            i=stm.executeUpdate(sUpdate);
            stm.close();
        return i;
    }
        
    public String Delete(String sKode) throws SQLException{
        String sDel="";
        conn.setAutoCommit(false);
        sDel="DELETE FROM phar_supplier  WHERE kode_supplier='"+sKode+"'";
        Statement stm = conn.createStatement();
        
        System.out.println(sDel);
        stm.executeUpdate(sDel);
        
        stm.close();
            
        return sDel;
    }
   
   public String getQryFilter(String sCol, String sOpr,String sValue){
        //String sQry="select kode_periksa, nama_pemeriksaan,  FROM rad_jenis_periksa ";
        String sFilter;
        if(sCol.equalsIgnoreCase("ALL")){
            String sFtr=sOpr=="Like" ? "'%"+ sValue.toUpperCase() + "%' " : " '"+ sValue.toUpperCase() + "' ";
            sFilter ="WHERE upper(kode_periksa||nama_pemeriksaan||singkatan||jenis_periksa) like "+sFtr.toUpperCase();
        }
        else{
            String sFtr=sOpr=="Like" ? "'%"+ sValue + "%' " : " '"+ sValue + "' ";
            sFilter ="WHERE upper("+sCol+ ") " + sOpr +sFtr.toUpperCase();
                }
            
        sQry = sQry + sFilter+" order by 1";
        return sQry;
    }
    
    public String getQrySearch(String sValue){
        sQry=sQry+"Where upper(kode_periksa||nama_pemeriksaan||singkatan||jenis_periksa) like '%"+sValue+"%'";
        return sQry;
    }
    
    public String[] getFieldName() {
        String kueri="select kode_supplier, nama_supplier, alamat, nama_kota, telp, contact_person, hp, keterangan ,top " + 
                     "from phar_supplier s inner join rm_kota k on k.kode_kota=s.kode_kota where kode_supplier='XX'";
        
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
