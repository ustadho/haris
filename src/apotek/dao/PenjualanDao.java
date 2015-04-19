/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package apotek.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import main.MainForm;

/**
 *
 * @author cak-ust
 */
public class PenjualanDao {
    public int jmlRecord(){
        int i=0;
        try {
            ResultSet rs=MainForm.conn.createStatement().executeQuery("select count(*) from penjualan");
            rs.next();
            i=rs.getInt(1);
            rs.close();
        } catch (SQLException ex) {
            Logger.getLogger(PenjualanDao.class.getName()).log(Level.SEVERE, null, ex);
        }
        return i;
    }
}
