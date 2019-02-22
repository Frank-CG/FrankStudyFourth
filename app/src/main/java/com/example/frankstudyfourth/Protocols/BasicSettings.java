package com.example.frankstudyfourth.Protocols;

public final class BasicSettings {
    public static final String urlBaseHome = "http://parlvucloud.parl.gc.ca/";

    public static String getUrlBaseParlvuAPI(String language){
        return urlBaseHome + "/Harmony/" + language + "/api/data/";
    }

    public static String getUrlBaseParlvuStream(String language){
        return urlBaseHome + "/Harmony/" + language + "/api/PowerBrowserData/";
    }

}
