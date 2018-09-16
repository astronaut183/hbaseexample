package com.imooc.bigdata.hos.common;

import okhttp3.Response;

import java.io.IOException;
import java.io.InputStream;

public class HosObject {

    private ObjectMetaData metaData;
    private InputStream content;
    private Response response;

    public HosObject() {
    }

    public HosObject(Response response) {
        this.response = response;
    }

    public ObjectMetaData getMetaData() {
        return metaData;
    }

    public void setMetaData(ObjectMetaData metaData) {
        this.metaData = metaData;
    }

    public InputStream getContent() {
        return content;
    }

    public void setContent(InputStream content) {
        this.content = content;
    }

    public Response getResponse() {
        return response;
    }

    public void setResponse(Response response) {
        this.response = response;
    }

    public void close() {
        try {
            if (content != null) {
                this.content.close();
            }
            if (response != null) {
                this.response.close();
            }
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }

}
