/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.klinik.dao;

import com.klinik.model.DiskonEvent;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import main.MainForm;

/**
 *
 * @author cak-ust
 */
public class DiskonEventDao {

    public Integer simpan(DiskonEvent e) {
        Integer id = null;

        try {
            if (id == null) {
                PreparedStatement ps = MainForm.conn.prepareStatement("INSERT INTO event_diskon(\n"
                        + "            tipe_tarif, tipe_diskon, tgl_mulai, tgl_sampai, aktif, item_code, \n"
                        + "            min_tot_bill, diskon_persen, diskon_rp, keterangan)\n"
                        + "    VALUES (?, ?, ?, ?, ?, ?, ?, \n"
                        + "            ?, ?, ?) returning id;");
                ps.setString(1, e.getTipeTarif());
                ps.setString(2, e.getTipeDiskon());
                ps.setDate(3, new java.sql.Date(e.getTglMulai().getTime()));
                ps.setDate(4, new java.sql.Date(e.getTglSampai().getTime()));
                ps.setBoolean(5, e.getAktif());
                ps.setString(6, e.getItemCode());
                ps.setDouble(7, e.getMinTotBill());
                ps.setDouble(8, e.getDiskonPersen());
                ps.setDouble(9, e.getDiskonRp());
                ps.setString(10, e.getKeterangan());
                
                ps.executeQuery();
                ResultSet rs=ps.getResultSet();
                if(rs.next()){
                    id=rs.getInt(1);
                }
                rs.close();
                ps.close();
            }
        } catch (SQLException ex) {
            Logger.getLogger(DiskonEventDao.class.getName()).log(Level.SEVERE, null, ex);
        }
        return id;
    }
}
