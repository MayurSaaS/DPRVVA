package com.vvautotest.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Site {
    public int id;
    public String code;
    public String name;
    public String lat;
    public String lng;
    public String contractorName;
    public String packageNo;
    public String roadNo;
    public String roadName;
    public String stabLength;
    public String stabWidth;
    public String stabDepth;
    public String noofLanes;
    public String cumVolumeWork;
    public String geoFance;
    public String geoFenceWeb;
    public String geoFenceApp;

}
