/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package apotek.dao;

import com.klinik.model.Barang;
import com.klinik.model.BarangPaket;
import com.klinik.model.SuppBarang;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import main.MainForm;

/**
 *
 * @author faheem
 */
public class ItemDao {

    public double getSaldo(String item, String gudang) {
        double saldo = 0;
        try {
            ResultSet rs = MainForm.conn.createStatement().executeQuery("select * from kartu_stock  where item_code='" + item + "' and kode_gudang='" + gudang + "' "
                    + "order by serial_no desc limit 1");
            if (rs.next()) {
                saldo = rs.getDouble("saldo");
            }
            rs.close();
        } catch (SQLException ex) {
            Logger.getLogger(ItemDao.class.getName()).log(Level.SEVERE, null, ex);
        }

        return saldo;
    }

    public Barang getBarangByKode(String kode) {
        try {
            Barang barang = null;
            ResultSet rs = MainForm.conn.createStatement().executeQuery("select *, "
                    + "round_up(base_price*(1+margin/100), " + MainForm.setting.getRoundUp() + ") as harga_jual from barang where item_code='" + kode + "'");
            if (rs.next()) {
                barang = new Barang();
                barang.setItemCode(rs.getString("item_code"));
                barang.setItemName(rs.getString("item_name"));
                barang.setNamaPaten(rs.getString("nama_paten"));
                barang.setNamaGenerik(rs.getString("nama_generik"));
                barang.setDosis(rs.getDouble("dosis"));
                barang.setKeterangan(rs.getString("keterangan"));
                barang.setSatuanKecil(rs.getString("satuan_kecil"));
                barang.setKodeJenis(rs.getString("kode_jenis"));
                barang.setBentukId(rs.getString("bentuk_id"));
                barang.setGroupId(rs.getString("group_id"));
                barang.setManufakturId(rs.getString("manufaktur_id"));
                barang.setMin(rs.getInt("min"));
                barang.setMax(rs.getInt("max"));
                barang.setHpp(rs.getDouble("hpp"));
                barang.setBasePrice(rs.getDouble("base_price"));
                barang.setMargin(rs.getDouble("margin"));
                barang.setDiscontinued(rs.getBoolean("discontinued"));
                barang.setSuppDefault(rs.getString("supp_default"));
                barang.setPrAutomatic(rs.getBoolean("pr_automatic"));
                barang.setConsignment(rs.getBoolean("consignment"));
                barang.setIndikasi(rs.getString("indikasi"));
                barang.setDiskonBox(rs.getDouble("diskon_box"));
                barang.setStock(rs.getDouble("stock"));
                barang.setCetakDiFaktur(rs.getBoolean("cetak_di_faktur"));
                barang.setUserIns(rs.getString("user_ins"));
                barang.setTimeIns(rs.getDate("time_ins"));

                barang.setUserIns(rs.getString("user_upd"));
                barang.setTimeIns(rs.getDate("time_upd"));
                barang.setOn_order(rs.getDouble("on_order"));
                barang.setHargaJual(rs.getDouble("harga_jual"));
                barang.setListPaket(getListPaket(kode));
            }
            rs.close();
            return barang;

        } catch (SQLException ex) {
            Logger.getLogger(ItemDao.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public Double getHpp(String item) {
        double hpp = 0;
        try {
            ResultSet rs = MainForm.conn.createStatement().executeQuery("select hpp from kartu_stock where item_code='" + item + "' "
                    + "order by serial_no desc limit 1");
            if (rs.next()) {
                hpp = rs.getDouble("hpp");
            }
            rs.close();
        } catch (SQLException ex) {
            Logger.getLogger(ItemDao.class.getName()).log(Level.SEVERE, null, ex);
        }

        return hpp;
    }

    public SuppBarang getSuppBarang(String item, String sup) {
        SuppBarang x = new SuppBarang();
        try {
            ResultSet rs;
            rs = MainForm.conn.createStatement().executeQuery("SELECT uom_alt, convertion, price, disc, \n"
                    + "       disc_rp, bonus, vat, user_ins, time_ins, user_upd, time_upd, \n"
                    + "       priority, min_order, prosentase, is_disc_rp, is_tax_rp\n"
                    + "  FROM supplier_barang "
                    + "where kode_barang='"+item+"' and kode_supplier='"+sup+"';");
            if(rs.next()){
                x.setKodeBarang(item);
                x.setKodeBarang(item);
                x.setUomAlt(rs.getString("uom_alt"));
                x.setConvertion(rs.getInt("convertion"));
                x.setPrice(rs.getDouble("price"));
                x.setDisc(rs.getDouble("disc"));
                x.setVat(rs.getDouble("vat"));
                x.setPriority(rs.getInt("priority"));
                
            }
            rs.close();
        } catch (SQLException ex) {
            Logger.getLogger(ItemDao.class.getName()).log(Level.SEVERE, null, ex);
        }
        return x;
    }
    
    public List<BarangPaket> getListPaket(String kode){
        List<BarangPaket> list=new ArrayList<BarangPaket>();
        try {
            ResultSet rs=MainForm.conn.createStatement().executeQuery("select * from barang_paket where kode_paket='"+kode+"'");
            while(rs.next()){
                BarangPaket b=new BarangPaket(rs.getString("kode_paket"), rs.getString("item_code"), rs.getInt("jumlah"));
                list.add(b);
            }
            rs.close();
        } catch (SQLException ex) {
            Logger.getLogger(ItemDao.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return list;
    }
    
    public void clearItemPaket(String kode){
        try {
            PreparedStatement ps=MainForm.conn.prepareStatement("delete from barang_paket where kode_paket=?");
            ps.setString(1, kode);
            ps.executeUpdate();
        } catch (SQLException ex) {
            Logger.getLogger(ItemDao.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public void simpanItemPaket(List<BarangPaket> list){
        try {
            PreparedStatement ps=MainForm.conn.prepareStatement("INSERT INTO barang_paket(\n" +
                    "            kode_paket, item_code, jumlah)\n" +
                    "    VALUES (?, ?, ?);");
            for(BarangPaket x: list){
                ps.setString(1, x.getKodePaket());
                ps.setString(2, x.getItemCode());
                ps.setDouble(3, x.getJumlah());
                ps.addBatch();
            }
            ps.executeBatch();
        } catch (SQLException ex) {
            Logger.getLogger(ItemDao.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }
    
    
}
