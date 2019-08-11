package com.example.ym.fix_it;

public class Comments {


    public String comment,date,time,fullname,profileimage;

    public Comments()
    {

    }

    public Comments(String comment, String date, String time, String fullname, String profileimage) {
        this.comment = comment;
        this.date = date;
        this.time = time;
        this.fullname = fullname;
        this.profileimage = profileimage;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getFullname() {
        return fullname;
    }

    public void setFullname(String fullname) {
        this.fullname = fullname;
    }

    public String getProfileimage() {
        return profileimage;
    }

    public void setProfileimage(String profileimage) {
        this.profileimage = profileimage;
    }
}
