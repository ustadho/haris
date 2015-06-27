/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.klinik.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 *
 * @author faheem
 */
public class Barang {
    private String itemCode;
    private String barcode;
    private String itemName;
    private String namaPaten;
    private String namaGenerik;
    private double dosis;
    private String keterangan;
    private String satuanKecil;
    private String kodeJenis;
    private String bentukId;
    private String groupId;
    private String manufakturId;
    private Integer min;
    private Integer max;
    private double hpp;
    private double basePrice;
    private double margin;
    private boolean discontinued;
    private String suppDefault;
    private boolean prAutomatic;
    private boolean consignment;
    private String indikasi;
    private double diskonBox;
    private double stock;
    private boolean cetakDiFaktur;
    private String userIns;
    private Date timeIns;
    private String userUpd;
    private Date timeUpd;
    private double on_order ;
    private double hargaKlinik;
    private double hargaReseller;
    private List<BarangPaket> listPaket=new ArrayList<BarangPaket>();

    public String getItemCode() {
        return itemCode;
    }

    public void setItemCode(String itemCode) {
        this.itemCode = itemCode;
    }

    public String getBarcode() {
        return barcode;
    }

    public void setBarcode(String barcode) {
        this.barcode = barcode;
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public String getNamaPaten() {
        return namaPaten;
    }

    public void setNamaPaten(String namaPaten) {
        this.namaPaten = namaPaten;
    }

    public String getNamaGenerik() {
        return namaGenerik;
    }

    public void setNamaGenerik(String namaGenerik) {
        this.namaGenerik = namaGenerik;
    }

    public double getDosis() {
        return dosis;
    }

    public void setDosis(double dosis) {
        this.dosis = dosis;
    }

    public String getKeterangan() {
        return keterangan;
    }

    public void setKeterangan(String keterangan) {
        this.keterangan = keterangan;
    }

    public String getSatuanKecil() {
        return satuanKecil;
    }

    public void setSatuanKecil(String satuanKecil) {
        this.satuanKecil = satuanKecil;
    }

    public String getKodeJenis() {
        return kodeJenis;
    }

    public void setKodeJenis(String kodeJenis) {
        this.kodeJenis = kodeJenis;
    }

    public String getBentukId() {
        return bentukId;
    }

    public void setBentukId(String bentukId) {
        this.bentukId = bentukId;
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public String getManufakturId() {
        return manufakturId;
    }

    public void setManufakturId(String manufakturId) {
        this.manufakturId = manufakturId;
    }

    public Integer getMin() {
        return min;
    }

    public void setMin(Integer min) {
        this.min = min;
    }

    public Integer getMax() {
        return max;
    }

    public void setMax(Integer max) {
        this.max = max;
    }

    public double getHpp() {
        return hpp;
    }

    public void setHpp(double hpp) {
        this.hpp = hpp;
    }

    public double getBasePrice() {
        return basePrice;
    }

    public void setBasePrice(double basePrice) {
        this.basePrice = basePrice;
    }

    public double getMargin() {
        return margin;
    }

    public void setMargin(double margin) {
        this.margin = margin;
    }

    public boolean isDiscontinued() {
        return discontinued;
    }

    public void setDiscontinued(boolean discontinued) {
        this.discontinued = discontinued;
    }

    public String getSuppDefault() {
        return suppDefault;
    }

    public void setSuppDefault(String suppDefault) {
        this.suppDefault = suppDefault;
    }

    public boolean isPrAutomatic() {
        return prAutomatic;
    }

    public void setPrAutomatic(boolean prAutomatic) {
        this.prAutomatic = prAutomatic;
    }

    public boolean isConsignment() {
        return consignment;
    }

    public void setConsignment(boolean consignment) {
        this.consignment = consignment;
    }

    public String getIndikasi() {
        return indikasi;
    }

    public void setIndikasi(String indikasi) {
        this.indikasi = indikasi;
    }

    public double getDiskonBox() {
        return diskonBox;
    }

    public void setDiskonBox(double diskonBox) {
        this.diskonBox = diskonBox;
    }

    public double getStock() {
        return stock;
    }

    public void setStock(double stock) {
        this.stock = stock;
    }

    public boolean isCetakDiFaktur() {
        return cetakDiFaktur;
    }

    public void setCetakDiFaktur(boolean cetakDiFaktur) {
        this.cetakDiFaktur = cetakDiFaktur;
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

    public String getUserUpd() {
        return userUpd;
    }

    public void setUserUpd(String userUpd) {
        this.userUpd = userUpd;
    }

    public Date getTimeUpd() {
        return timeUpd;
    }

    public void setTimeUpd(Date timeUpd) {
        this.timeUpd = timeUpd;
    }

    public double getOn_order() {
        return on_order;
    }

    public void setOn_order(double on_order) {
        this.on_order = on_order;
    }

    public double getHargaKlinik() {
        return hargaKlinik;
    }

    public void setHargaKlinik(double hargaKlinik) {
        this.hargaKlinik = hargaKlinik;
    }

    public double getHargaReseller() {
        return hargaReseller;
    }

    public void setHargaReseller(double hargaReseller) {
        this.hargaReseller = hargaReseller;
    }

    public List<BarangPaket> getListPaket() {
        return listPaket;
    }

    public void setListPaket(List<BarangPaket> listPaket) {
        this.listPaket = listPaket;
    }

    
}
