package com.example.teleprompter;

import android.os.Parcel;
import android.os.Parcelable;

public class Spec implements Parcelable {
    private int scrollSpeed;
    private int fontSize;
    private String title;
    private String content;
    private int backgroundColor;
    private int fontColor;

    public Spec() {
    }

    public int getScrollSpeed() {
        return scrollSpeed;
    }

    public void setScrollSpeed(int scrollSpeed) {
        this.scrollSpeed = scrollSpeed;
    }

    public int getFontSize() {
        return fontSize;
    }

    public void setFontSize(int fontSize) {
        this.fontSize = fontSize;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public int getBackgroundColor() {
        return backgroundColor;
    }

    public void setBackgroundColor(int backgroundColor) {
        this.backgroundColor = backgroundColor;
    }

    public int getFontColor() {
        return fontColor;
    }

    public void setFontColor(int fontColor) {
        this.fontColor = fontColor;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(scrollSpeed);
        dest.writeInt(fontSize);
        dest.writeString(title);
        dest.writeString(content);
        dest.writeInt(backgroundColor);
        dest.writeInt(fontColor);
    }

    protected Spec(Parcel in) {
        scrollSpeed = in.readInt();
        fontSize = in.readInt();
        title = in.readString();
        content = in.readString();
        backgroundColor = in.readInt();
        fontColor = in.readInt();
    }

    public static final Creator<Spec> CREATOR = new Creator<Spec>() {
        @Override
        public Spec createFromParcel(Parcel source) {
            return new Spec(source);
        }

        @Override
        public Spec[] newArray(int size) {
            return new Spec[size];
        }
    };
}