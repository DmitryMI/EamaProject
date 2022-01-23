package com.example.smarthouse.backend.discovery;

import android.os.Parcel;
import android.os.Parcelable;

public class Discovery implements Parcelable {
    public final static String DiscoveryAction = "DISCOVERY_ACTION";
    public final static String IntentPayloadName = "DISCOVERY_PAYLOAD";

    private final boolean isLan;
    private final String url;

    private Discovery(Parcel in) {
        isLan = in.readInt() == 1;
        url = in.readString();
    }

    public Discovery(boolean isLan, String url) {
        this.isLan = isLan;
        this.url = url;
    }

    public static final Creator<Discovery> CREATOR = new Creator<Discovery>() {
        @Override
        public Discovery createFromParcel(Parcel in) {
            return new Discovery(in);
        }

        @Override
        public Discovery[] newArray(int size) {
            return new Discovery[size];
        }
    };

    public boolean isLan() {
        return isLan;
    }

    public String getUrl() {
        return url;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        int isLanInt = isLan ? 1 : 0;
        parcel.writeInt(isLanInt);
        parcel.writeString(url);
    }
}
