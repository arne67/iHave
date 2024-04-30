package com.susarne.ihave2.models.GooglePhotos;

import java.util.List;

public class BatchCreateRespondBody {

    private List<NewMediaItemResult> newMediaItemResults;

    public List<NewMediaItemResult> getNewMediaItemResults() {
        return newMediaItemResults;
    }

    public void setNewMediaItemResults(List<NewMediaItemResult> newMediaItemResults) {
        this.newMediaItemResults = newMediaItemResults;
    }
}
