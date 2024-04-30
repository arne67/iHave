package com.susarne.ihave2.models.GooglePhotos;

public class Album {

    public String id;
    public String title;
    public String productUrl;
    public String isWriteable;
    public String mediaItemsCount;
    public String coverPhotoBaseUrl;
    public String coverPhotoMediaItemId;

    /*
        @SerializedName("id")
        @Expose
        private String id;
        @SerializedName("title")
        @Expose
        private String title;
        @SerializedName("productUrl")
        @Expose
        private String productUrl;
        @SerializedName("isWriteable")
        @Expose
        private String isWriteable;
        @SerializedName("mediaItemsCount")
        @Expose
        private String mediaItemsCount;
        @SerializedName("coverPhotoBaseUrl")
        @Expose
        private String coverPhotoBaseUrl;
        @SerializedName("coverPhotoMediaItemId")
        @Expose
        private String coverPhotoMediaItemId;
        */

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getProductUrl() {
            return productUrl;
        }

        public void setProductUrl(String productUrl) {
            this.productUrl = productUrl;
        }

        public String getIsWriteable() {
            return isWriteable;
        }

        public void setIsWriteable(String isWriteable) {
            this.isWriteable = isWriteable;
        }

        public String getMediaItemsCount() {
            return mediaItemsCount;
        }

        public void setMediaItemsCount(String mediaItemsCount) {
            this.mediaItemsCount = mediaItemsCount;
        }

        public String getCoverPhotoBaseUrl() {
            return coverPhotoBaseUrl;
        }

        public void setCoverPhotoBaseUrl(String coverPhotoBaseUrl) {
            this.coverPhotoBaseUrl = coverPhotoBaseUrl;
        }

        public String getCoverPhotoMediaItemId() {
            return coverPhotoMediaItemId;
        }

        public void setCoverPhotoMediaItemId(String coverPhotoMediaItemId) {
            this.coverPhotoMediaItemId = coverPhotoMediaItemId;
        }

}
