package com.example.phontacts;

import android.os.Parcel;
import android.os.Parcelable;

public class ContactItem implements Parcelable {
private int imgSrc;
private String cName;
private String cNumber;

    public ContactItem(int imgSrc, String cName, String cNumber) {
        this.imgSrc = imgSrc;
        this.cName = cName;
        this.cNumber = cNumber;
    }

    protected ContactItem(Parcel in) {
        imgSrc = in.readInt();
        cName = in.readString();
        cNumber = in.readString();
    }

    public static final Creator<ContactItem> CREATOR = new Creator<ContactItem>() {
        @Override
        public ContactItem createFromParcel(Parcel in) {
            return new ContactItem(in);
        }

        @Override
        public ContactItem[] newArray(int size) {
            return new ContactItem[size];
        }
    };

    public int getImgSrc() {
        return imgSrc;
    }

    public void setImgSrc(int imgSrc) {
        this.imgSrc = imgSrc;
    }

    public String getcName() {
        return cName;
    }

    public void setcName(String cName) {
        this.cName = cName;
    }

    public String getcNumber() {
        return cNumber;
    }

    public void setcNumber(String cNumber) {
        this.cNumber = cNumber;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(imgSrc);
        dest.writeString(cName);
        dest.writeString(cNumber);
    }
}
