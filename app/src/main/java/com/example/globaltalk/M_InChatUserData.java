package com.example.globaltalk;

public class M_InChatUserData {

    private String chat_email;
    private String chat_profile_image;
    private String chat_profile_name;
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


    public void setmy_email(String my_email) {
        this.my_email = my_email;
    }



}
