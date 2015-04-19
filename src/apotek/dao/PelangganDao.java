/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package apotek.dao;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author faheem
 */
public class PelangganDao {
    private Connection conn;
    public void setConn(Connection con){
        this.conn=con;
    }
    
    private String getNewKodePelanggan() throws SQLException{
        String kode="";
            ResultSet rs=conn.createStatement().executeQuery("select fn_get_new_kode_pelanggan()");
        if(rs.next()){
            kode=rs.getString(1);
        }
        rs.close();
        return kode;
    }
    
    public String simpanPelanggan(String kode, String nama, Date tglLahir, 
            String alamat, String kodeKota, 
            String telepon, String hp){
        try {
            conn.setAutoCommit(false);
            ResultSet rs=conn.createStatement(ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_UPDATABLE)
                    .executeQuery("select * from pelanggan where kode_pelanggan='"+kode+"'");
            boolean isNew=false;
            if(!rs.next()){
                isNew=true;
                kode=getNewKodePelanggan();
                rs.moveToInsertRow();
            }
            rs.updateString("kode_pelanggan", kode);
            rs.updateString("nama_pelanggan", nama);
            
            rs.updateDate("tgl_lahir", tglLahir==null? null: new java.sql.Date(tglLahir.getTime()));
            rs.updateString("alamat", alamat);
            rs.updateString("kode_kota", kodeKota);
            rs.updateString("telepon", telepon);
            rs.updateString("hp", hp);
            if(isNew){
                rs.insertRow();
            }else{
                rs.updateRow();
            }
            
            conn.setAutoCommit(true);
        } catch (SQLException ex) {
            try {
                conn.rollback();
                conn.setAutoCommit(true);
            } catch (SQLException ex1) {
                System.out.println("Error: "+ ex.getMessage()+"\n"+ex.getNextException());
                Logger.getLogger(PelangganDao.class.getName()).log(Level.SEVERE, null, ex1);
            }
            Logger.getLogger(PelangganDao.class.getName()).log(Level.SEVERE, null, ex);
        }
        return kode;
    }
}
