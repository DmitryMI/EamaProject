package com.example.smarthouse.backend.discovery;

import android.os.Parcel;
import android.os.Parcelable;

public class Discovery implements Parcelable {
    public final static String DiscoveryAction = "DISCOVERY_ACTION";
    public final static String IntentPayloadName = "DISCOVERY_PAYLOAD";

    private final boolean isLan;
    private final String lanUrl;
    private final String wanUrl;

    private Discovery(Parcel in) {
        isLan = in.readInt() == 1;
        lanUrl = in.readString();
        wanUrl = in.readString();
    }

    public Discovery(boolean isLan, String lanUrl, String wanUrl) {
        this.isLan = isLan;
        this.lanUrl = lanUrl;
        this.wanUrl = wanUrl;
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

    public String getLanUrl() {
        return lanUrl;
    }
    public String getWanUrl(){return wanUrl;}

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        int isLanInt = isLan ? 1 : 0;
        parcel.writeInt(isLanInt);
        parcel.writeString(lanUrl);
        parcel.writeString(wanUrl);
    }
}
