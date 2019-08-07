package com.example.globaltalk;

import java.util.List;

public class InviteData {
    private String member_email;
    private String member_image;
    private String member_name;
    private String InviteList;

    private int invite_checkbox;



    public String getMember_email() {
        return member_email;
    }

    public String getMember_image() {
        return member_image;
    }

    public String getMember_name() {
        return member_name;
    }

    public String getInviteList() {
        return InviteList;
    }



    public int getinvite_checkbox() {
        return invite_checkbox;
    }






    public void setMember_email(String member_email) {
        this.member_email = member_email;
    }

    public void setMember_image(String member_image) {
        this.member_image = member_image;
    }

    public void setMember_name(String member_name) {
        this.member_name = member_name;
    }

    public void setInviteList(String InviteList) {
        this.InviteList = InviteList;
    }


    public void setinvite_checkbox(int invite_checkbox) {
        this.invite_checkbox = invite_checkbox;
    }



}
