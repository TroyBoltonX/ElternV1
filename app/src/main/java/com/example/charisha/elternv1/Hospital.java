package com.example.charisha.elternv1;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by e-Miracle workers.
 */
public class Hospital implements Parcelable {

    private String hospitalName;
    private String addressLine1;
    private String addressLine2;
    private double hospitalLatitude;
    private double hospitalLongitude;

    public String getHospitalName() {
        return hospitalName;
    }

    public void setHospitalName(String hospitalName) {
        this.hospitalName = hospitalName;
    }

    public String getAddressLine1() {
        return addressLine1;
    }

    public void setAddressLine1(String addressLine1) {
        this.addressLine1 = addressLine1;
    }

    public String getAddressLine2() {
        return addressLine2;
    }

    public void setAddressLine2(String addressLine2) {
        this.addressLine2 = addressLine2;
    }

    public double getHospitalLatitude() {
        return hospitalLatitude;
    }

    public void setHospitalLatitude(double hospitalLatitude) {
        this.hospitalLatitude = hospitalLatitude;
    }

    public double getHospitalLongitude() {
        return hospitalLongitude;
    }

    public void setHospitalLongitude(double hospitalLongitude) {
        this.hospitalLongitude = hospitalLongitude;
    }

    public Hospital(String hospitalName, String addressLine1, String addressLine2, double hospitalLatitude, double hospitalLongitude) {
        this.hospitalName = hospitalName;
        this.addressLine1 = addressLine1;
        this.addressLine2 = addressLine2;
        this.hospitalLatitude = hospitalLatitude;
        this.hospitalLongitude = hospitalLongitude;
    }

    protected Hospital(Parcel input) {
        this.hospitalName = input.readString();
        this.addressLine1 = input.readString();
        this.addressLine2 = input.readString();
        this.hospitalLatitude = input.readDouble();
        this.hospitalLongitude = input.readDouble();
    }

    public static final Creator<Hospital> CREATOR = new Creator<Hospital>() {
        @Override
        public Hospital createFromParcel(Parcel in) {
            return new Hospital(in);
        }

        @Override
        public Hospital[] newArray(int size) {
            return new Hospital[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(hospitalName);
        dest.writeString(addressLine1);
        dest.writeString(addressLine2);
        dest.writeDouble(hospitalLatitude);
        dest.writeDouble(hospitalLongitude);
    }
}

