/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package apotek.dao;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import main.Setting;

/**
 *
 * @author cak-ust
 */
public class SettingDao {
    private Connection conn;
    public void setConn(Connection c){
        this.conn=c;
    }
    
    public Setting getSetting(){
        Setting s=new Setting();
        try {
            ResultSet rs;
            rs = conn.createStatement().executeQuery("select coalesce(round_up,0) as round_up, "
                    + "coalesce(nama_klinik,'') as nama_klinik, coalesce(alamat,'') as alamat, coalesce(telepon,'') as telepon, "
                    + "coalesce(apoteker,'') as apoteker, coalesce(sip_apoteker,'') as sip_apoteker "
                    + "from m_setting");
            if(rs.next()){
                s.setRoundUp(rs.getDouble("round_up"));
                s.setNamaKlinik(rs.getString("nama_klinik"));
                s.setAlamat(rs.getString("alamat"));
                s.setTelepon(rs.getString("telepon"));
                s.setApoteker(rs.getString("apoteker"));
                s.setSipApoteker(rs.getString("sip_apoteker"));
            }
            rs.close();
        } catch (SQLException ex) {
            Logger.getLogger(SettingDao.class.getName()).log(Level.SEVERE, null, ex);
        }
        return s;
    }
}
