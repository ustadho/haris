/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.klinik.dao;

import apotek.Main;
import com.klinik.model.Pasien;
import com.klinik.model.Reservasi;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import main.MainForm;
import org.jdesktop.swingx.JXDatePicker;

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
        String SQL_UPD="UPDATE rm_reservasi set batal=true, user_batal=?, time_batal=now(), status='4' "
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
    
    public Reservasi findOne(Integer id){
        Reservasi rv=null;
        String SQL="SELECT id, tanggal, kode_dokter, norm, nama, jenis_kelamin, tempat_lahir, \n" +
                    "       tgl_lahir, alamat_domisili, telepon, hp, nama_keluarga, telp_keluarga,"
                    + "case status when '1' then 'Reservasi' "
                    + "                 when '2' then 'Registrasi' "
                    + "                 when '3' then 'Selesai' "
                    + "                 when '4' then 'Batal' "
                    + "else '' end as status, coalesce(title,'') as title\n" +
                    "FROM rm_reservasi where id="+id;
        try {
            ResultSet rs=Main.conn.createStatement().executeQuery(SQL);
            if(rs.next()){
                Pasien px=new Pasien();
                px.setNorm(rs.getString("norm"));
                px.setNama(rs.getString("nama"));
                px.setJenisKelamin(rs.getString("jenis_kelamin"));
                px.setTempatLahir(rs.getString("tempat_lahir"));
                px.setTanggalLahir(rs.getDate("tgl_lahir"));
                px.setAlamatDomisili(rs.getString("alamat_domisili"));
                px.setTelepon(rs.getString("telepon"));
                px.setHp(rs.getString("hp"));
                px.setNamaKeluarga(rs.getString("nama_keluarga"));
                px.setTeleponKeluarga(rs.getString("telp_keluarga"));
                
                rv=new Reservasi();
                rv.setId(id);
                rv.setKodeDokter(rs.getString("kode_dokter"));
                rv.setPasien(px);
                rv.setTanggal(rs.getDate("tanggal"));
                rv.setStatus(rs.getString("status"));
                rv.setTitle(rs.getString("title"));
            }
            rs.close();
        } catch (SQLException ex) {
            Logger.getLogger(ReservasiDao.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return rv;
    }

    public void updateStatus(Integer id, String st, String norm) {
        String SQL="update rm_reservasi set status=?, norm=? where id=?";
        try {
            PreparedStatement ps=Main.conn.prepareStatement(SQL);
            ps.setString(1, st);
            ps.setString(2, norm);
            ps.setInt(3, id);
            ps.executeUpdate();
        } catch (SQLException ex) {
            Logger.getLogger(ReservasiDao.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
}
