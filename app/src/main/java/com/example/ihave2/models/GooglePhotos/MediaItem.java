package com.example.ihave2.models.GooglePhotos;


import com.google.gson.annotations.SerializedName;

public class MediaItem {

    @SerializedName("id")
    private String id;
    @SerializedName("description")
    private String description;
    @SerializedName("productUrl")
    private String productUrl; //link til præsentation af foto via Google Photo Applikation
    @SerializedName("baseUrl")
    private String baseUrl;  //link til selve fotofilen i cloud (expire efter et stykke tid)
    @SerializedName("mimeType")
    private String mimeType;
    @SerializedName("mediaMetadata")
    private MediaMetadata mediaMetadata;
    @SerializedName("filename")
    private String filename;

    private class MediaMetadata {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getProductUrl() {
        return productUrl;
    }

    public void setProductUrl(String productUrl) {
        this.productUrl = productUrl;
    }

    public String getBaseUrl() {
        return baseUrl;
    }

    public void setBaseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    public String getMimeType() {
        return mimeType;
    }

    public void setMimeType(String mimeType) {
        this.mimeType = mimeType;
    }

    public MediaMetadata getMediaMetadata() {
        return mediaMetadata;
    }

    public void setMediaMetadata(MediaMetadata mediaMetadata) {
        this.mediaMetadata = mediaMetadata;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }
}
