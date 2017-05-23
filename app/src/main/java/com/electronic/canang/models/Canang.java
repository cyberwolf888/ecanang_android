package com.electronic.canang.models;

public class Canang {
    public String id_canang;
    public String nama_paket;
    public String image;
    public String harga;
    public String keterangan;
    public String status;

    public Canang(String id_canang, String nama_paket, String image, String harga, String keterangan, String status)
    {
        this.id_canang = id_canang;
        this.nama_paket = nama_paket;
        this.image = image;
        this.harga = harga;
        this.keterangan = keterangan;
        this.status = status;
    }
}
