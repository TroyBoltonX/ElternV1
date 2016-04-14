package com.example.charisha.elternv1;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;

/**
 * Created by e-Miracle workers.
 */

public class AppointmentAdapterElder extends BaseAdapter {
    private Context context;
    private ArrayList<Appointment> appointments;

    public AppointmentAdapterElder(Context context, ArrayList<Appointment> appointments) {
        this.context = context;
        this.appointments = appointments;

        //sort reminders according to date and time
        Collections.sort(appointments, new Comparator<Appointment>() {
            public int compare(Appointment o1, Appointment o2) {
                if (o1.getDate() == null || o2.getDate() == null)
                    return 0;
                return o1.getDateTime().compareTo(o2.getDateTime());
            }
        });
    }

    @Override
    public int getCount() {
        return appointments.size();
    }

    @Override
    public Appointment getItem(int i) {
        return appointments.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {

        if (view == null) {
            LayoutInflater inflater = (LayoutInflater)
                    context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.list_appointment_item_elder, null);

            //formated date
            String formattedRemDate = "";
            //formatted time
            String formattedTime = "";
            Date remDate;

            //put date into date object
            String dateString2 = appointments.get(i).getDate() + " " + appointments.get(i).getTime() + ":" + "00";
            try {
                remDate = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss").parse(dateString2);
                //date format
                SimpleDateFormat sdf = new SimpleDateFormat("EEE, dd-MMM");
                //time format
                SimpleDateFormat sdf2 = new SimpleDateFormat("hh:mm aa");
                //format date
                formattedRemDate = sdf.format(remDate);
                //format time
                formattedTime = sdf2.format(remDate);

            } catch (Exception e) {

            }

            TextView topicText = (TextView) view.findViewById(R.id.titleTextView);
            topicText.setText(appointments.get(i).getTitle());
            TextView dateText = (TextView) view.findViewById(R.id.dateTextView);
            dateText.setText("Date: " + formattedRemDate);
            TextView timeText = (TextView) view.findViewById(R.id.timeTextView);
            timeText.setText("Time: " + formattedTime);


            //to diplay the image
            ImageView completedImage = (ImageView) view.findViewById(R.id.completedImage);

            if (appointments.get(i).getCompleted()) {
                // dateText.setVisibility(View.GONE);
                //timeText.setVisibility(View.GONE);
                completedImage.setImageResource(R.drawable.tick);
                //  topicText.setTextColor(Color.parseColor("#006400"));
            } else {
                // completedImage.setImageResource(R.drawable.notdonee);

            }

            return view;
        }
        return view;
    }
}
