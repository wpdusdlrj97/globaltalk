package com.example.globaltalk;

public class InChatData {

    private String chat_email;
    private String chat_profile_image;
    private String chat_profile_name;
    private String chat_content;
    private String chat_wdate;

    private String my_email;




    public String getchat_email() {
        return chat_email;
    }

    public String getchat_profile_image() {
        return chat_profile_image;
    }

    public String getchat_profile_name() {
        return chat_profile_name;
    }


    public String getchat_content() {
        return chat_content;
    }

    public String getchat_wdate() {
        return chat_wdate;
    }


    public String getmy_email() {
        return my_email;
    }




    public void setchat_email(String chat_email) {
        this.chat_email = chat_email;
    }

    public void setchat_profile_image(String chat_profile_image) {
        this.chat_profile_image = chat_profile_image;
    }

    public void setchat_profile_name(String chat_profile_name) {
        this.chat_profile_name = chat_profile_name;
    }

    public void setchat_content(String chat_content) {
        this.chat_content = chat_content;
    }

    public void setchat_wdate(String chat_wdate) {
        this.chat_wdate = chat_wdate;
    }


    public void setmy_email(String my_email) {
        this.my_email = my_email;
    }



}
