/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package main;

/**
 *
 * @author root
 */
public class MenuAuth {
    private boolean read=false, insert=false, update=false, delete=false, print=false, koreksi=false;

    public boolean canRead(){
        return read;
    }

    public boolean canInsert(){
        return insert;
    }

    public boolean canUpdate(){
        return update;
    }

    public boolean canDelete(){
        return delete;
    }

    public boolean canPrint(){
        return print;
    }

    public boolean canKoreksi(){
        return koreksi;
    }

    public void setRead(boolean b){
        this.read=b;
    }

    public void setInsert(boolean b){
        this.insert=b;
    }

    public void setUpdate(boolean b){
        this.update=b;
    }

    public void setDelete(boolean b){
        this.delete=b;
    }

    public void setPrint(boolean b){
        this.print=b;
    }

    public void setKoreksi(boolean b){
        this.koreksi=b;
    }


}
