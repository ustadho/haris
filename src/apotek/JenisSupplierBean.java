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
public class JenisSupplierBean {
    private String kode_jenis;
    private String nama_jenis;
    private int top;
    
    static String sID="";
    static String sTgl="";
    private Connection conn;

    private String kode_jenis_supp;
    
    /** Creates a new instance of UnitBean*/
    public JenisSupplierBean() {
    }
    
    public void setKodeJenis(String sId){
        this.kode_jenis=sId;
    }
    
    public void setNamaJenis(String sNama){
        this.nama_jenis=sNama;
    }
    
    public String getKodeSupp(){
        return kode_jenis;
    }
    
    public String getNamaSup(){
        return nama_jenis;
    }
    
    public Connection getConn() {
        return conn;
    }
    
    public void setConn(Connection conn) {
        this.conn = conn;
    }
    
    public boolean Add() throws SQLException{
        SimpleDateFormat sd = new SimpleDateFormat("yyyy-MM-dd");
        boolean st=false;
            String sQry="select fn_phar_new_jenis_supp('" + kode_jenis +"','"+nama_jenis+"')";
        
            Statement stm = conn.createStatement();
            
            System.out.println(sQry);
            ResultSet rsTr = stm.executeQuery(sQry);
            if (rsTr.next()){
                kode_jenis=rsTr.getString(1).trim();
            }
            rsTr.close();
            stm.close();
            st=true;
        return st;
        }
    
    public int Edit(String sKode)throws SQLException{
        String sUpdate= "update phar_jenis_supplier set kode_jenis_supp='"+kode_jenis+"',jenis_supplier='"+nama_jenis+"' " +
                        "where kode_jenis_supp='"+sKode+"'";
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
        sDel="DELETE FROM phar_jenis_supplier  WHERE kode_jenis_supp='"+sKode+"'";
        Statement stm = conn.createStatement();
        
        System.out.println(sDel);
        stm.executeUpdate(sDel);
        
        stm.close();
            
        return sDel;
    }
   
   public String[] getFieldName() {
        String kueri="select kode_jenis_supp, jenis_supplier as Jenis_Supplier from phar_jenis_supplier Order by kode_jenis_supp";
        
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

