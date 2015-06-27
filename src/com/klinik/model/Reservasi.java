/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.klinik.model;

import java.util.Date;

/**
 *
 * @author faheem
 */
public class Reservasi {
    private Integer id;
    Pasien pasien;
    private String userIns;
    private Date timeIns;
    private Boolean batal;
    private String userBatal;
    private Date timeBatal;
    private String status; //'F --> Finised, W -> Waiting, C --> Cancel';
    private String kodeDokter;
    private Date tanggal;
    private String title;
    
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Pasien getPasien() {
        return pasien;
    }

    public void setPasien(Pasien px) {
        this.pasien = px;
    }

    public String getUserIns() {
        return userIns;
    }

    public void setUserIns(String userIns) {
        this.userIns = userIns;
    }

    public Date getTimeIns() {
        return timeIns;
    }

    public void setTimeIns(Date timeIns) {
        this.timeIns = timeIns;
    }

    public Boolean isBatal() {
        return batal;
    }

    public void setBatal(Boolean batal) {
        this.batal = batal;
    }

    public String getUserBatal() {
        return userBatal;
    }

    public void setUserBatal(String userBatal) {
        this.userBatal = userBatal;
    }

    public Date getTimeBatal() {
        return timeBatal;
    }

    public void setTimeBatal(Date timeBatal) {
        this.timeBatal = timeBatal;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getKodeDokter() {
        return kodeDokter;
    }

    public void setKodeDokter(String kodeDokter) {
        this.kodeDokter = kodeDokter;
    }

    public Date getTanggal() {
        return tanggal;
    }

    public void setTanggal(Date tanggal) {
        this.tanggal = tanggal;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
    
    
}
