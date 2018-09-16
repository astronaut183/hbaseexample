package com.imooc.bigdata.hos.common;

import java.util.List;

public class ObjectListResult {

    private String bucket;
    private String maxKey;
    private String minKey;
    private String mextMarker;
    private int maxKeyNumber;
    private int objectCount;
    private String listId;
    private List<HosObjectSummary> objectSummaries;

    public String getBucket() {
        return bucket;
    }

    public void setBucket(String bucket) {
        this.bucket = bucket;
    }

    public String getMaxKey() {
        return maxKey;
    }

    public void setMaxKey(String maxKey) {
        this.maxKey = maxKey;
    }

    public String getMinKey() {
        return minKey;
    }

    public void setMinKey(String minKey) {
        this.minKey = minKey;
    }

    public String getMextMarker() {
        return mextMarker;
    }

    public void setMextMarker(String mextMarker) {
        this.mextMarker = mextMarker;
    }

    public int getMaxKeyNumber() {
        return maxKeyNumber;
    }

    public void setMaxKeyNumber(int maxKeyNumber) {
        this.maxKeyNumber = maxKeyNumber;
    }

    public int getObjectCount() {
        return objectCount;
    }

    public void setObjectCount(int objectCount) {
        this.objectCount = objectCount;
    }

    public String getListId() {
        return listId;
    }

    public void setListId(String listId) {
        this.listId = listId;
    }

    public List<HosObjectSummary> getObjectSummaries() {
        return objectSummaries;
    }

    public void setObjectSummaries(List<HosObjectSummary> objectSummaries) {
        this.objectSummaries = objectSummaries;
    }
}
