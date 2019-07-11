package com.example.globaltalk;


public class CommentData {

    private String comment_id;
    private String board_id;
    private String member_email;
    private String member_image;
    private String member_name;
    private String member_content;
    private String member_time;
    private String login_email;

    public String getComment_id() {
        return comment_id;
    }

    public String getBoard_id() {
        return board_id;
    }

    public String getMember_email() {
        return member_email;
    }

    public String getMember_image() {
        return member_image;
    }

    public String getMember_name() {
        return member_name;
    }

    public String getMember_content() {
        return member_content;
    }

    public String getMember_time() {
        return member_time;
    }

    public String getLogin_email() {
        return login_email;
    }



    public void setComment_id(String comment_id) {
        this.comment_id = comment_id;
    }

    public void setBoard_id(String board_id) {
        this.board_id = board_id;
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

    public void setMember_content(String member_content) {
        this.member_content = member_content;
    }

    public void setMember_time(String member_time) {
        this.member_time = member_time;
    }

    public void setLogin_email(String login_email) {
        this.login_email = login_email;
    }
}
