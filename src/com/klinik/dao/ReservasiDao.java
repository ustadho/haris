/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.klinik.dao;

import apotek.Main;
import com.klinik.model.Reservasi;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import main.MainForm;

/**
 *
 * @author faheem
 */
public class ReservasiDao {
    public Integer save(Reservasi x){
        Integer id=null;
        try{
            if(x.getId()==null){
                String SQL_INSERT="INSERT INTO rm_reservasi(\n" +
                                "   norm, nama, jenis_kelamin, tempat_lahir, tgl_lahir, alamat_domisili, \n" +
                                "   telepon, hp, nama_keluarga, telp_keluarga, user_ins, \n" +//7
                                "   status, kode_dokter, tanggal)\n" +//12
                                "VALUES (?, ?, ?, ?, ?, ?, \n" + //6
                                "   ?, ?, ?, ?, ?, ?, ?, ?) returning id;"; //14
                PreparedStatement ps=Main.conn.prepareStatement(SQL_INSERT);
                if(x.getPasien().getNorm()!=null){
                    ps.setString(1, x.getPasien().getNorm());
                }else{
                    ps.setNull(1, java.sql.Types.VARCHAR);
                }
                ps.setString(2, x.getPasien().getNama());
                ps.setString(3, x.getPasien().getJenisKelamin());
                ps.setString(4, x.getPasien().getTempatLahir());
                ps.setDate(5, new java.sql.Date(x.getPasien().getTanggalLahir().getTime()));
                ps.setString(6, x.getPasien().getAlamatDomisili());
                ps.setString(7, x.getPasien().getTelepon());
                ps.setString(8, x.getPasien().getHp());
                ps.setString(9, x.getPasien().getNamaKeluarga());
                ps.setString(10, x.getPasien().getTeleponKeluarga());
                ps.setString(11, MainForm.sUserName);
                ps.setString(12, x.getStatus());
                ps.setString(13, x.getKodeDokter());
                ps.setDate(14, new java.sql.Date(x.getTanggal().getTime()));
                ps.executeQuery();
                ResultSet rs=ps.getResultSet();
                if(rs.next()){
                    id=rs.getInt("id");
                }
                rs.close();
            }else{
                String SQL_UPD="UPDATE rm_reservasi\n" +
                "   SET norm=?, nama=?, jenis_kelamin=?, tempat_lahir=?, tgl_lahir=?, \n" + //5
                "       alamat_domisili=?, telepon=?, hp=?, nama_keluarga=?, telp_keluarga=?, \n" + //10
                "       \n" +
                "       status=?\n" +
                " WHERE id=?";
                PreparedStatement ps=Main.conn.prepareStatement(SQL_UPD);
                ps.setString(1, x.getPasien().getNorm());
                ps.setString(2, x.getPasien().getNama());
                ps.setString(3, x.getPasien().getJenisKelamin());
                ps.setString(4, x.getPasien().getTempatLahir());
                ps.setDate(5, new java.sql.Date(x.getPasien().getTanggalLahir().getTime()));
                ps.setString(6, x.getPasien().getAlamatDomisili());
                ps.setString(7, x.getPasien().getTelepon());
                ps.setString(8, x.getPasien().getHp());
                ps.setString(9, x.getPasien().getNamaKeluarga());
                ps.setString(10, x.getPasien().getTeleponKeluarga());
                ps.setString(11, x.getStatus());
                ps.setInt(12, x.getId());
                ps.executeUpdate();
                ps.close();
            }
        } catch (SQLException ex) {
            Logger.getLogger(PasienDao.class.getName()).log(Level.SEVERE, null, ex);
        }
        return id;
    }
    
    public void batal(Integer id){
        String SQL_UPD="UPDATE rm_reservasi set batal=true, user_batal=?, time_batal=now(), status='C' "
                + "where id=?";
        try {
            PreparedStatement ps=Main.conn.prepareStatement(SQL_UPD);
            ps.setString(1, MainForm.sUserName);
            ps.setInt(2, id);
            
            ps.executeUpdate();
            ps.close();
        } catch (SQLException ex) {
            Logger.getLogger(ReservasiDao.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public void ubahDokter(Integer id, String kodeDokter, Date tanggal){
        String SQL_UPD="UPDATE rm_reservasi set kode_dokter=?, tanggal=?, user_upd=?, time_upd=now() "
                + "where id=?";
        try {
            PreparedStatement ps=Main.conn.prepareStatement(SQL_UPD);
            ps.setString(1, kodeDokter);
            ps.setDate(2, new java.sql.Date(tanggal.getTime()));
            ps.setString(3, MainForm.sUserName);
            ps.setInt(4, id);
            ps.executeUpdate();
            
            ps.close();
        } catch (SQLException ex) {
            Logger.getLogger(ReservasiDao.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public boolean cekNormExists(String kodeDokter, Date tanggal, String norm){
        boolean st=false;
        String SQL="select norm from rm_reservasi where kode_dokter=? and tanggal=? and norm=?";
        try {
            PreparedStatement ps=Main.conn.prepareStatement(SQL);
            ps.setString(1, kodeDokter);
            ps.setDate(2, new java.sql.Date(tanggal.getTime()));
            ps.setString(3, norm);
            if(ps.executeQuery().next()){
                ps.close();
                return true;
            }
            ps.close();
        } catch (SQLException ex) {
            Logger.getLogger(ReservasiDao.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }
        return st;
    }
}
