package com.example.charisha.elternv1;

import android.os.Parcel;
import android.os.Parcelable;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by e-Miracle workers.
 */

public class Appointment implements Parcelable {
    private String title;
    private String description;
    private String date;
    private String time;
    private String toWho;
    private String fromWho;
    private Date dateTime;
    private String frequency;
    private String id;
    private boolean completed;
    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
        public Appointment createFromParcel(Parcel in) {

            return new Appointment(in);
        }

        public Appointment[] newArray(int size) {

            return new Appointment[size];
        }
    };

    public Appointment() {
        this.id = "a";
        this.title = "b";
        this.description = "c";
        this.date = "d";
        this.time = "e";
        this.toWho = "f";
        this.fromWho = "g";
        this.frequency = "h";
        this.completed = false;
    }

    public Appointment(Parcel in) {
        this.id = in.readString();
        this.title = in.readString();
        this.description = in.readString();
        this.date = in.readString();
        this.time = in.readString();
        this.toWho = in.readString();
        this.fromWho = in.readString();
        this.frequency = in.readString();
        this.completed = (in.readInt() == 0) ? false : true;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFrequency() {
        return frequency;
    }

    public void setFrequency(String frequency) {
        this.frequency = frequency;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getToWho() {
        return toWho;
    }

    public void setToWho(String toWho) {
        this.toWho = toWho;
    }

    public String getfromWho() {
        return fromWho;
    }

    public void setFromWho(String fromWho) {
        this.fromWho = fromWho;
    }

    public Boolean getCompleted() {
        return completed;
    }

    public void setCompleted(Boolean completed) {
        this.completed = completed;
    }

    public Date getDateTime()
    {
        Date newdate = null;
        SimpleDateFormat dateformat2 = new SimpleDateFormat("dd-M-yyyy hh:mm:ss");

        String strdate2 = getDate() + " " + getTime() + ":00";

        try {
            newdate = dateformat2.parse(strdate2);
            System.out.println(newdate);
        }

        catch (ParseException e) {
            e.printStackTrace();
        }

        return newdate;
    }

    public void setDateTime(Date date)
    {
     dateTime = date;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(id);
        parcel.writeString(title);
        parcel.writeString(description);
        parcel.writeString(date);
        parcel.writeString(time);
        parcel.writeString(toWho);
        parcel.writeString(fromWho);
        parcel.writeString(frequency);
        parcel.writeInt(completed ? 1 : 0);
    }
}
