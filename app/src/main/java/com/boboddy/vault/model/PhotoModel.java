package com.boboddy.vault.model;

/**
 * Created by nick on 3/6/15.
 */
public class PhotoModel {

    private long _id;
    private String filepath;
//    private byte[] data;

    public long get_id() {
        return _id;
    }

    public void set_id(long _id) {
        this._id = _id;
    }

    public String getFilepath() {
        return filepath;
    }

    public void setFilepath(String filepath) {
        this.filepath = filepath;
    }

    public String toString() {
        return filepath;
    }

}
