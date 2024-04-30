package com.susarne.ihave2.models;

public class GetUserRespondDto {
    private String userName;
    private String password;
    private String name;
    private String emailAddress;

    private String photoAlbumId;



    public GetUserRespondDto(String userName, String password, String name, String emailAddress, String photoAlbumId) {
        this.userName = userName;
        this.password = password;
        this.name = name;
        this.emailAddress = emailAddress;
        this.photoAlbumId = photoAlbumId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmailAddress() {
        return emailAddress;
    }

    public void setEmailAddress(String emailAddress) {
        this.emailAddress = emailAddress;
    }

    public String getPhotoAlbumId() {
        return photoAlbumId;
    }

    public void setPhotoAlbumId(String photoAlbumId) {
        this.photoAlbumId = photoAlbumId;
    }
}
