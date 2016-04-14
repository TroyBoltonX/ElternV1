package com.example.charisha.elternv1;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by e-Miracle workers.
 */
public class Toilet implements Parcelable {

    private String toiletName;
    private String toiletAddress;
    private String toiletTown;
    private String accessibleMale;
    private String accessibleFemale;
    private double toiletLatitude;
    private double toiletLongitude;

    public Toilet(String toiletName, String toiletAddress, String toiletTown, String accessibleMale, String accessibleFemale, double toiletLatitude, double toiletLongitude) {
        this.toiletName = toiletName;
        this.toiletAddress = toiletAddress;
        this.toiletTown = toiletTown;
        this.accessibleMale = accessibleMale;
        this.accessibleFemale = accessibleFemale;
        this.toiletLatitude = toiletLatitude;
        this.toiletLongitude = toiletLongitude;
    }

    public String getToiletName() {
        return toiletName;
    }

    public void setToiletName(String toiletName) {
        this.toiletName = toiletName;
    }

    public String getToiletAddress() {
        return toiletAddress;
    }

    public void setToiletAddress(String toiletAddress) {
        this.toiletAddress = toiletAddress;
    }

    public String getToiletTown() {
        return toiletTown;
    }

    public void setToiletTown(String toiletTown) {
        this.toiletTown = toiletTown;
    }

    public String getAccessibleMale() {
        return accessibleMale;
    }

    public void setAccessibleMale(String accessibleMale) {
        this.accessibleMale = accessibleMale;
    }

    public String getAccessibleFemale() {
        return accessibleFemale;
    }

    public void setAccessibleFemale(String accessibleFemale) {
        this.accessibleFemale = accessibleFemale;
    }

    public double getToiletLatitude() {
        return toiletLatitude;
    }

    public void setToiletLatitude(double toiletLatitude) {
        this.toiletLatitude = toiletLatitude;
    }

    public double getToiletLongitude() {
        return toiletLongitude;
    }

    public void setToiletLongitude(double toiletLongitude) {
        this.toiletLongitude = toiletLongitude;
    }

    protected Toilet(Parcel input) {
        this.toiletName = input.readString();
        this.toiletAddress = input.readString();
        this.toiletTown = input.readString();
        this.accessibleMale = input.readString();
        this.accessibleFemale = input.readString();
        this.toiletLatitude = input.readDouble();
        this.toiletLongitude = input.readDouble();
    }

    public static final Creator<Toilet> CREATOR = new Creator<Toilet>() {
        @Override
        public Toilet createFromParcel(Parcel input) {
            return new Toilet(input);
        }

        @Override
        public Toilet[] newArray(int size) {
            return new Toilet[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(toiletName);
        dest.writeString(toiletAddress);
        dest.writeString(toiletTown);
        dest.writeString(accessibleMale);
        dest.writeString(accessibleFemale);
        dest.writeDouble(toiletLatitude);
        dest.writeDouble(toiletLongitude);
    }
}
