package com.vvautotest.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.ArrayList;
import java.util.Date;

@JsonIgnoreProperties(ignoreUnknown = true)
public class User {
    public String userID;
    public String loginID;
    public String userType;
    public String fName;
    public String mName;
    public String lName;
    public String designation;
    public String contactNo;
    public String email;
    public Date dob;
    public String gender;
    public ArrayList<Site> sites;
    public Result result;
}
