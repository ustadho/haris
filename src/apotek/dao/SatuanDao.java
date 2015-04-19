package apotek.dao;

import com.klinik.model.Satuan;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import main.MainForm;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author faheem
 */
public class SatuanDao {
    public List<Satuan> findAll(){
        List<Satuan> hasil=new ArrayList<Satuan>();
        
        try{
            ResultSet rs;
            rs = MainForm.conn.createStatement().executeQuery("select * from uom order by uom_desc ");
            while(rs.next()){
                hasil.add(new Satuan(rs.getString("uom"), rs.getString("uom_desc")));
            }
        }catch(SQLException se){
            System.out.println("error SatuanDao.findAll: \n"+se.getMessage());
        }
        return hasil;
    }

    public Satuan findById(String kode) {
        Satuan s=new Satuan();
        try {
            
            ResultSet rs=MainForm.conn.createStatement().executeQuery("select uom, coalesce(uom_desc,'') as uom_desc "
                    + "from uom where uom='"+kode+"'");
            if(rs.next()){
                s.setKode(rs.getString("uom"));
                s.setNama(rs.getString("uom_desc"));
            }
            rs.close();
        } catch (SQLException ex) {
            Logger.getLogger(SatuanDao.class.getName()).log(Level.SEVERE, null, ex);
        }
        return s;
    }
}
