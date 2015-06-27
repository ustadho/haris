/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.klinik.dao;

import apotek.Main;
import com.klinik.model.Registrasi;
import com.klinik.model.StatusUpdate;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import main.MainForm;

/**
 *
 * @author faheem
 */
public class RegistrasiDao {
    public StatusUpdate save(Registrasi r){
        StatusUpdate st=new StatusUpdate();
        
        String noReg=null;
        if(r.getNoReg()==null || r.getNoReg().equalsIgnoreCase("")){
            String SQL="INSERT INTO rm_reg(\n" +
                        "            no_reg, kode_dokter, norm, tanggal, diagnosa_masuk, alergi, berat_badan, \n" +//5
                        "            mag, time_ins, user_ins, id_reservasi)\n" +
                        "    VALUES (fn_get_no_reg(), ?, ?, current_date, ?, ?, ?, \n" +
                        "            ?, now(), ?, ?) returning no_reg;";
            System.out.println(SQL);
            try {
                PreparedStatement ps=Main.conn.prepareStatement(SQL);

                ps.setString(1, r.getKodeDokter());
                ps.setString(2, r.getNorm());
                ps.setString(3, r.getDiagnosaMasuk());
                ps.setString(4, r.getAlergi());
                ps.setBigDecimal(5, r.getBeratBadan());
                ps.setBoolean(6, r.isMag());
                ps.setString(7, MainForm.sUserName);
                ps.setInt(8, r.getIdReservasi());
                ps.executeQuery();
                ResultSet rs=ps.getResultSet();
                if(rs.next()){
                    noReg=rs.getString("no_reg");
                    st.setNo(noReg);
                    st.setSukses(true);
                }
            } catch (SQLException ex) {
                Logger.getLogger(RegistrasiDao.class.getName()).log(Level.SEVERE, null, ex);
                System.out.println("NetEx: "+ex.getNextException());
            }
            
        }
        return st;
    }
}
