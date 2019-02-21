package com.example.frankstudyfourth.Protocols;

public final class BasicSettings {
    public static final String urlBaseHome = "http://parlvu.parl.gc.ca/";

    public static String getUrlBaseParlvuAPI(String language){
        return urlBaseHome + "/XRender/" + language + "/api/data/";
    }

    public static String getUrlBaseParlvuStream(String language){
        return urlBaseHome + "/XRender/" + language + "/api/PowerBrowserData/";
    }

}
