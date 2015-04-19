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
            rs = conn.createStatement().executeQuery("select * from m_setting");
            if(rs.next()){
                s.setRoundUp(rs.getDouble("round_up"));
            }
            rs.close();
        } catch (SQLException ex) {
            Logger.getLogger(SettingDao.class.getName()).log(Level.SEVERE, null, ex);
        }
        return s;
    }
}
