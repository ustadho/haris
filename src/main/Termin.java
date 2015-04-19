/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package main;

/**
 *
 * @author root
 */
public class Termin {
    private int hariDiskon=0, jtTempo=0;
    private double diskon=0;
    private String keterangan="";

    public int getHariDiskon(){
        return hariDiskon;
    }

    public double getDiskon(){
        return diskon;
    }

    public int getJtTempo(){
        return jtTempo;
    }

    public String getKeterangan(){
        return keterangan;
    }
    public void setHariDiskon(int i){
        this.hariDiskon=i;
    }

    public void setDiskon(double d){
        this.diskon=d;
    }

    public void setJtTempo(int i){
        this.jtTempo=i;
    }

    public void setKeterangan(String s){
        this.keterangan=s;
    }

    
}
