package com.vvautotest.utils;

public class ServerConfig {

    public static boolean isStaging = true;

    public static final String DOMAIN_URL = "http://api.vvaspl.com:500/";

    //Staging
    public static final String STAGING_URL = DOMAIN_URL + "test/api/";
    //Live
    public static final String LIVE_URL = DOMAIN_URL + "test/api/";

    public static final String GALLERY_URL = DOMAIN_URL + "test/Gallery/";
    public static final String My_Document_URL = DOMAIN_URL + "test/Documents/";
    public static final String PDF_URL = DOMAIN_URL + "test/PDFView/";
    //Base URL
    public static final String BASE_URL = isStaging ? STAGING_URL : LIVE_URL;

    public static final String Login_URL = BASE_URL + "User/Login";
    public static final String Site_URL = BASE_URL + "Master/SiteList";
    public static final String Users_URL = BASE_URL + "Master/UserList";
    public static final String Menus_URL = BASE_URL + "User/MenuDetails";
    public static final String Action_Details_URL = BASE_URL + "User/ActionDetails";
    public static final String Forgot_Password_URL = BASE_URL + "User/ForgetPassword";
    public static final String Load_Users_By_Site_URL = BASE_URL + "User/SiteWiseUsersList";
    public static final String CeateFolder = BASE_URL + "Transaction/CreateFolder";
    public static final String Photos_URL =  GALLERY_URL;
    public static final String Image_Details_URL =  BASE_URL + "Transaction/GetImageDetails";;
    public static final String Categories_URL =  BASE_URL + "Master/CategoryList";;
    public static final String Image_Upload_URL =  BASE_URL + "Transaction/UploadImageDetails";;
    public static final String Image_Upload_Document_URL =  BASE_URL + "Transaction/UploadFile";;
    public static final String Image_Upload_Multiple_Document_URL =  BASE_URL + "Transaction/UploadFiles";;
    public static final String PDF_Upload_URL =  BASE_URL + "Transaction/UploadPDFDetails";;
    public static final String PDF_Upload_Video_URL =  BASE_URL + "Transaction/UploadVideo";;



}
