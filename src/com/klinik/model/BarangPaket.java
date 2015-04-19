/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.klinik.model;

/**
 *
 * @author cak-ust
 */
public class BarangPaket {
    private String kodePaket;
    private String itemCode;
    private Integer jumlah;

    public BarangPaket() {
    }

    
    public BarangPaket(String kodePaket, String itemCode, Integer jumlah) {
        this.kodePaket = kodePaket;
        this.itemCode = itemCode;
        this.jumlah = jumlah;
    }

    
    public String getKodePaket() {
        return kodePaket;
    }

    public void setKodePaket(String kodePaket) {
        this.kodePaket = kodePaket;
    }

    public String getItemCode() {
        return itemCode;
    }

    public void setItemCode(String itemCode) {
        this.itemCode = itemCode;
    }

    public Integer getJumlah() {
        return jumlah;
    }

    public void setJumlah(Integer jumlah) {
        this.jumlah = jumlah;
    }
    
    
}
