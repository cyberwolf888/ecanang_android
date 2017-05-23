package com.electronic.canang.models;


public class Transaksi {
    public String id_transaksi;
    public String canang_id;
    public String user_id;
    public String telp;
    public String address;
    public String total;
    public String img_bukti;
    public String status;
    public String created_at;
    public String label_status;
    public String nama_paket;

    public Transaksi(String id_transaksi,String canang_id,String user_id,String telp,String address,String total,String img_bukti,String status,String created_at,String label_status,String nama_paket)
    {
        this.id_transaksi = id_transaksi;
        this.canang_id = canang_id;
        this.user_id = user_id;
        this.telp = telp;
        this.address = address;
        this.total = total;
        this.img_bukti = img_bukti;
        this.status = status;
        this.created_at = created_at;
        this.label_status = label_status;
        this.nama_paket = nama_paket;
    }
}
