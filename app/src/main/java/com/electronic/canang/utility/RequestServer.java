package com.electronic.canang.utility;


public class RequestServer {
    private String server_ip = "192.168.1.1";
    private String server_url = "/ecanang/api/";
    private String img_url = "/ecanang/assets/img/paket/";

    public String getServer_url(){
        return "http://"+this.server_ip+this.server_url;
    }
    public String getImg_url(){
        return "http://"+this.server_ip+this.img_url;
    }

}
