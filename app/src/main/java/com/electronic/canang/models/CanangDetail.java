package com.electronic.canang.models;


public class CanangDetail {
    public String id_canang_detail;
    public String id_canang;
    public String label;
    public String qty;

    public CanangDetail(String id_canang_detail, String id_canang, String label, String qty)
    {
        this.id_canang_detail = id_canang_detail;
        this.id_canang = id_canang;
        this.label = label;
        this.qty = qty;
    }
}
