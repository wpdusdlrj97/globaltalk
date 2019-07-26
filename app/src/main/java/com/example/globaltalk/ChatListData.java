package com.example.globaltalk;

public class ChatListData {
    private String member_email;
    private String member_image;
    private String member_name;

    private String chatroom_no;
    private String chatroom_image;
    private String userlist;



    public String getMember_email() {
        return member_email;
    }

    public String getMember_image() {
        return member_image;
    }

    public String getMember_name() {
        return member_name;
    }

    public String getchatroom_no() {
        return chatroom_no;
    }

    public String getchatroom_image() {
        return chatroom_image;
    }

    public String getuserlist() {
        return userlist;
    }



    public void setchatroom_no(String chatroom_no) {
        this.chatroom_no = chatroom_no;
    }

    public void setchatroom_image(String chatroom_image) {
        this.chatroom_image = chatroom_image;
    }

    public void setuserlist(String userlist) {
        this.userlist = userlist;
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

}
