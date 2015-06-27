/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.klinik.model;

import java.util.Date;

/**
 *
 * @author cak-ust
 */
public class DiskonEvent {
    private Integer id;
    private String tipeTarif;
    private String tipeDiskon;
    private Date tglMulai;
    private Date tglSampai;
    private Boolean aktif;
    private String itemCode;
    private Double minTotBill;
    private Double diskonPersen;
    private Double diskonRp;
    private String keterangan;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getTipeTarif() {
        return tipeTarif;
    }

    public void setTipeTarif(String tipeTarif) {
        this.tipeTarif = tipeTarif;
    }

    public String getTipeDiskon() {
        return tipeDiskon;
    }

    public void setTipeDiskon(String tipeDiskon) {
        this.tipeDiskon = tipeDiskon;
    }

    public Date getTglMulai() {
        return tglMulai;
    }

    public void setTglMulai(Date tglMulai) {
        this.tglMulai = tglMulai;
    }

    public Date getTglSampai() {
        return tglSampai;
    }

    public void setTglSampai(Date tglSampai) {
        this.tglSampai = tglSampai;
    }

    public Boolean getAktif() {
        return aktif;
    }

    public void setAktif(Boolean aktif) {
        this.aktif = aktif;
    }

    public String getItemCode() {
        return itemCode;
    }

    public void setItemCode(String itemCode) {
        this.itemCode = itemCode;
    }

    public Double getMinTotBill() {
        return minTotBill;
    }

    public void setMinTotBill(Double minTotBill) {
        this.minTotBill = minTotBill;
    }

    public Double getDiskonPersen() {
        return diskonPersen;
    }

    public void setDiskonPersen(Double diskonPersen) {
        this.diskonPersen = diskonPersen;
    }

    public Double getDiskonRp() {
        return diskonRp;
    }

    public void setDiskonRp(Double diskonRp) {
        this.diskonRp = diskonRp;
    }

    public String getKeterangan() {
        return keterangan;
    }

    public void setKeterangan(String keterangan) {
        this.keterangan = keterangan;
    }
    
    
}
