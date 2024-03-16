package com.example.ihave2.models.GooglePhotos;

import java.util.List;

public class BatchCreateRequestBody {
    public String albumId;
    public List<NewMediaItem> newMediaItems;

    public String getAlbumId() {
        return albumId;
    }

    public void setAlbumId(String albumId) {
        this.albumId = albumId;
    }

    public List<NewMediaItem> getNewMediaItems() {
        return newMediaItems;
    }

    public void setNewMediaItems(List<NewMediaItem> newMediaItems) {
        this.newMediaItems = newMediaItems;
    }
}
