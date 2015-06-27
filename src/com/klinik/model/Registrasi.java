/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.klinik.model;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Date;
import sun.security.util.BigInt;

/**
 *
 * @author cak-ust
 */
public class Registrasi {
    private String noReg;
    private String kodeDokter;
    private String norm;
    private Date tanggal;
    private String diagnosaMasuk;
    private String alergi;
    private BigDecimal beratBadan;
    private Boolean mag;
    private Timestamp timeIns;
    private String userIns;
    private Timestamp timeUpd;
    private String userUpd;
    private Integer idReservasi;

    public String getNoReg() {
        return noReg;
    }

    public void setNoReg(String noReg) {
        this.noReg = noReg;
    }

    public String getKodeDokter() {
        return kodeDokter;
    }

    public void setKodeDokter(String kodeDokter) {
        this.kodeDokter = kodeDokter;
    }

    public String getNorm() {
        return norm;
    }

    public void setNorm(String norm) {
        this.norm = norm;
    }

    public Date getTanggal() {
        return tanggal;
    }

    public void setTanggal(Date tanggal) {
        this.tanggal = tanggal;
    }

    public String getDiagnosaMasuk() {
        return diagnosaMasuk;
    }

    public void setDiagnosaMasuk(String diagnosaMasuk) {
        this.diagnosaMasuk = diagnosaMasuk;
    }

    public String getAlergi() {
        return alergi;
    }

    public void setAlergi(String alergi) {
        this.alergi = alergi;
    }

    public BigDecimal getBeratBadan() {
        return beratBadan;
    }

    public void setBeratBadan(BigDecimal beratBadan) {
        this.beratBadan = beratBadan;
    }

    public Boolean isMag() {
        return mag;
    }

    public void setMag(Boolean mag) {
        this.mag = mag;
    }

    public Timestamp getTimeIns() {
        return timeIns;
    }

    public void setTimeIns(Timestamp timeIns) {
        this.timeIns = timeIns;
    }

    public String getUserIns() {
        return userIns;
    }

    public void setUserIns(String userIns) {
        this.userIns = userIns;
    }

    public Timestamp getTimeUpd() {
        return timeUpd;
    }

    public void setTimeUpd(Timestamp timeUpd) {
        this.timeUpd = timeUpd;
    }

    public String getUserUpd() {
        return userUpd;
    }

    public void setUserUpd(String userUpd) {
        this.userUpd = userUpd;
    }

    public Integer getIdReservasi() {
        return idReservasi;
    }

    public void setIdReservasi(Integer idReservasi) {
        this.idReservasi = idReservasi;
    }

    
}
