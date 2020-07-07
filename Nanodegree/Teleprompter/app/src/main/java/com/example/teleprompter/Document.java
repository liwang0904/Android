package com.example.teleprompter;

import android.content.ContentUris;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;

import com.google.firebase.database.Exclude;

public class Document implements Parcelable {
    private String title;
    private String text;
    private int id = -1;
    private int priority = -1;

    @Exclude
    private String userId;
    @Exclude
    private boolean isNew;

    private String cloudId;
    private boolean isPing;

    public Document() {
        isNew = false;
        isPing = false;
    }

    public Document(Cursor data) {
        id = data.getInt(data.getColumnIndex(Contract.Entry._ID));
        title = data.getString(data.getColumnIndex(Contract.Entry.COLUMN_TITLE));
        text = data.getString(data.getColumnIndex(Contract.Entry.COLUMN_TEXT));
        priority = data.getInt(data.getColumnIndex(Contract.Entry.COLUMN_PRIORITY));
        cloudId = data.getString(data.getColumnIndex(Contract.Entry.COLUMN_CLOUD_ID));
        isPing = false;
        isNew = false;
        userId = data.getString(data.getColumnIndex(Contract.Entry.COLUMN_USERNAME));
    }

    @Exclude
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    public String getCloudId() {
        return cloudId;
    }

    public void setCloudId(String cloudId) {
        this.cloudId = cloudId;
    }

    public boolean isPing() {
        return isPing;
    }

    public void setPing(boolean ping) {
        isPing = ping;
    }

    @Exclude
    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    @Exclude
    public boolean isNew() {
        return isNew;
    }

    public void setNew(boolean aNew) {
        isNew = aNew;
    }

    @Exclude
    public Uri getUri() {
        return ContentUris.withAppendedId(Contract.Entry.CONTENT_URI, id);
    }

    @Exclude
    public ContentValues getContentValues() {
        ContentValues contentValues = new ContentValues();
        contentValues.put(Contract.Entry.COLUMN_TEXT, text);
        contentValues.put(Contract.Entry.COLUMN_TITLE, title);

        if (userId != null) contentValues.put(Contract.Entry.COLUMN_USERNAME, userId);
        if (cloudId != null) contentValues.put(Contract.Entry.COLUMN_CLOUD_ID, cloudId);
        if (priority != -1) contentValues.put(Contract.Entry.COLUMN_PRIORITY, priority);

        return contentValues;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(cloudId);
        dest.writeString(title);
        dest.writeString(text);
        dest.writeInt(priority);
        dest.writeString(userId);
        dest.writeByte((byte) (isNew ? 1 : 0));
        dest.writeByte((byte) (isPing ? 1 : 0));
    }

    protected Document(Parcel in) {
        id = in.readInt();
        cloudId = in.readString();
        title = in.readString();
        text = in.readString();
        priority = in.readInt();
        userId = in.readString();
        isNew = in.readByte() != 0;
        isPing = in.readByte() != 0;
    }

    public static final Creator<Document> CREATOR = new Creator<Document>() {
        @Override
        public Document createFromParcel(Parcel source) {
            return new Document(source);
        }

        @Override
        public Document[] newArray(int size) {
            return new Document[size];
        }
    };
}