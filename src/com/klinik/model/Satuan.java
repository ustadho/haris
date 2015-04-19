/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.klinik.model;

/**
 *
 * @author cak-ust
 */
public class Satuan {
    private String kode;
    private String nama;

    public Satuan(String string, String string0) {
        this.kode=string;
        this.nama=string0;
    }

    public Satuan() {

    }

    public String getKode() {
        return kode;
    }

    public void setKode(String kode) {
        this.kode = kode;
    }

    public String getNama() {
        return nama;
    }

    public void setNama(String nama) {
        this.nama = nama;
    }
    
    
}
