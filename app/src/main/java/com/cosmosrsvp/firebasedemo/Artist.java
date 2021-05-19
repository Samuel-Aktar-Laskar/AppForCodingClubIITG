package com.cosmosrsvp.firebasedemo;

import android.graphics.Bitmap;

public class Artist {
    public String contactno,country,fullname,profession,email,path;
    public Bitmap pro;

    public Artist(String contactno, String country, String fullname, String profession, String email, String path, Bitmap pro) {
        this.contactno = contactno;
        this.country = country;
        this.fullname = fullname;
        this.profession = profession;
        this.email = email;
        this.path = path;
        this.pro = pro;
    }

    public Bitmap getPro() {
        return pro;
    }

    public void setPro(Bitmap pro) {
        this.pro = pro;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public Artist(String contactno, String country, String fullname, String profession, String email, String path) {
        this.contactno = contactno;
        this.country = country;
        this.fullname = fullname;
        this.profession = profession;
        this.email = email;
        this.path = path;
    }

    public Artist() {
    }

    public Artist(String contactno, String country, String fullname, String profession, String email) {
        this.contactno = contactno;
        this.country = country;
        this.fullname = fullname;
        this.profession = profession;
        this.email = email;
    }

    public String getContactno() {
        return contactno;
    }

    public void setContactno(String contactno) {
        this.contactno = contactno;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getFullname() {
        return fullname;
    }

    public void setFullname(String fullname) {
        this.fullname = fullname;
    }

    public String getProfession() {
        return profession;
    }

    public void setProfession(String profession) {
        this.profession = profession;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
