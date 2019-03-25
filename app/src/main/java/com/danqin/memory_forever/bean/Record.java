package com.danqin.memory_forever.bean;

import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;
import java.util.List;

public class Record implements Parcelable {

    //文字内容
    private String content;
    //视频地址
    private String videoUrl;
    //图片地址
    private List<String> imgUrls;
    //图片Uri
    private List<Uri> uris;

    //列表图片小图
    private String smallImgUrl;

    private int day;

    private int month;

    public Record(){

    }

    protected Record(Parcel in) {
        content = in.readString();
        videoUrl = in.readString();
        imgUrls = in.createStringArrayList();
        uris = in.createTypedArrayList(Uri.CREATOR);
        smallImgUrl = in.readString();
        day = in.readInt();
        month = in.readInt();
    }

    public static final Creator<Record> CREATOR = new Creator<Record>() {
        @Override
        public Record createFromParcel(Parcel in) {
            return new Record(in);
        }

        @Override
        public Record[] newArray(int size) {
            return new Record[size];
        }
    };

    public List<Uri> getUris() {
        return uris;
    }

    public void setUris(List<Uri> uris) {
        this.uris = uris;
    }

    public String getSmallImgUrl() {
        return smallImgUrl;
    }

    public void setSmallImgUrl(String smallImgUrl) {
        this.smallImgUrl = smallImgUrl;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getVideoUrl() {
        return videoUrl;
    }

    public void setVideoUrl(String videoUrl) {
        this.videoUrl = videoUrl;
    }

    public List<String> getImgUrls() {
        return imgUrls;
    }

    public void setImgUrls(List<String> imgUrls) {
        this.imgUrls = imgUrls;
    }

    public int getDay() {
        return day;
    }

    public void setDay(int day) {
        this.day = day;
    }

    public int getMonth() {
        return month;
    }

    public void setMonth(int month) {
        this.month = month;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(content);
        parcel.writeString(videoUrl);
        parcel.writeStringList(imgUrls);
        parcel.writeTypedList(uris);
        parcel.writeString(smallImgUrl);
        parcel.writeInt(day);
        parcel.writeInt(month);
    }


}
