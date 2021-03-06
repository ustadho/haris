/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.klinik.dao;

import apotek.Main;
import com.klinik.model.Pasien;
import com.klinik.model.Usia;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import main.MainForm;

/**
 *
 * @author cak-ust
 */
public class PasienDao {
    public String simpan(Pasien p){
        String norm=null;
        try {
            if(p.getNorm()==null){

                String SQL_INSERT="INSERT INTO rm_pasien(\n" +
                        "            norm, nama, jenis_kelamin, tempat_lahir, tgl_lahir, alamat_domisili, \n" +
                        "            telepon, hp, nama_keluarga, telp_keluarga, user_ins, title)\n" +
                        "    VALUES (fn_get_new_norm('"+p.getNama().substring(0, 1)+"'), ?, ?, ?, ?, ?, \n" + //5
                        "            ?, ?, ?, ?, ?, ?) returning norm;";
                PreparedStatement ps=Main.conn.prepareStatement(SQL_INSERT);
                ps.setString(1, p.getNama());
                ps.setString(2, p.getJenisKelamin());
                ps.setString(3, p.getTempatLahir());
                ps.setDate(4, new java.sql.Date(p.getTanggalLahir().getTime()));
                ps.setString(5, p.getAlamatDomisili());
                ps.setString(6, p.getTelepon());
                ps.setString(7, p.getHp());
                ps.setString(8, p.getNamaKeluarga());
                ps.setString(9, p.getTeleponKeluarga());
                ps.setString(10, MainForm.sUserName);
                ps.setString(11, p.getTitle());
                ResultSet rs=ps.executeQuery();
                if(rs.next()){
                    norm=rs.getString(1);
                }
                ps.close();
            }else{
                String SQL_UPDATE="UPDATE rm_pasien\n" +
                            "   SET nama=?, jenis_kelamin=?, tempat_lahir=?, tgl_lahir=?, \n" + //4
                            "       alamat_domisili=?, telepon=?, hp=?, nama_keluarga=?, telp_keluarga=?, \n" + //9
                            "       time_upd=now(), user_upd=?, title=?\n" +
                            " WHERE norm=?";
                PreparedStatement ps=Main.conn.prepareStatement(SQL_UPDATE);
                ps.setString(1, p.getNama());
                ps.setString(2, p.getJenisKelamin());
                ps.setString(3, p.getTempatLahir());
                ps.setDate(4, new java.sql.Date(p.getTanggalLahir().getTime()));
                ps.setString(5, p.getAlamatDomisili());
                ps.setString(6, p.getTelepon());
                ps.setString(7, p.getHp());
                ps.setString(8, p.getNamaKeluarga());
                ps.setString(9, p.getTeleponKeluarga());
                ps.setString(10, MainForm.sUserName);
                ps.setString(11, p.getTitle());
                ps.setString(12, p.getNorm());
                ps.executeUpdate();
                ps.close();
            }
        } catch (SQLException ex) {
            Logger.getLogger(PasienDao.class.getName()).log(Level.SEVERE, null, ex);
        }
        return norm;
    }
    
    public String getNewNorm(){
        String norm="";
        try {
            ResultSet rs=Main.conn.createStatement().executeQuery("select fn_get_new_norm()");
            if(rs.next()){
                
            }
        } catch (SQLException ex) {
            Logger.getLogger(PasienDao.class.getName()).log(Level.SEVERE, null, ex);
        }
        return norm;
    }
    
    public Pasien getPasien(String norm){
        Pasien px=null;
        try {
            PreparedStatement ps=Main.conn.prepareStatement("select * from rm_pasien where norm=?");
            ps.setString(1, norm);
            ResultSet rs=ps.executeQuery();
            if(rs.next()){
                px=new Pasien();
                px.setNorm(norm);
                px.setNama(rs.getString("nama"));
                px.setJenisKelamin(rs.getString("jenis_kelamin"));
                px.setTempatLahir(rs.getString("tempat_lahir"));
                px.setTanggalLahir(rs.getDate("tgl_lahir"));
                px.setAlamatDomisili(rs.getString("alamat_domisili"));
                px.setTelepon(rs.getString("telepon"));
                px.setHp(rs.getString("hp"));
                px.setNamaKeluarga(rs.getString("nama_keluarga"));
                px.setTeleponKeluarga(rs.getString("telp_keluarga"));
            }
            rs.close();
            ps.close();
        } catch (SQLException ex) {
            Logger.getLogger(PasienDao.class.getName()).log(Level.SEVERE, null, ex);
        }
        return px;
    }
    
    public Usia getUsia(String tglSampai, String tglLahir){
        String sql="select tahun, bulan, hari from fn_usia('"+tglSampai+"'::date, '"+tglLahir+"'::date) as "
                + "(tahun double precision, bulan double precision, hari double precision)";
        Usia usia=new Usia();
        try {
            ResultSet rs=MainForm.conn.createStatement().executeQuery(sql);
            if(rs.next()){
                usia.setTahun(rs.getDouble("tahun"));
                usia.setBulan(rs.getDouble("bulan"));
                usia.setHari(rs.getDouble("hari"));
            }
            rs.close();
        } catch (SQLException ex) {
            Logger.getLogger(PasienDao.class.getName()).log(Level.SEVERE, null, ex);
        }
        return usia;
    }
    
    public Date getTglLahirFromUsia(String tglSampai, Integer th, Integer bl, Integer hr){
        String sql="select fn_tgl_lahir('"+tglSampai+"'::date, "+th+", "+bl+", "+hr+")";
        Date tgl=new Date();
        try {
            ResultSet rs=MainForm.conn.createStatement().executeQuery(sql);
            if(rs.next()){
                tgl=rs.getDate(1);
            }
            rs.close();
        } catch (SQLException ex) {
            Logger.getLogger(PasienDao.class.getName()).log(Level.SEVERE, null, ex);
        }
        return tgl;
    }
    
}
