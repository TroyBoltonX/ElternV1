package com.example.charisha.elternv1;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

/**
 * Created by charisha on 17/09/2015.
 */
public class AppointmentOldAdapterCarer extends BaseAdapter {
    private Context context;
    private ArrayList<Appointment> appointments;

    public AppointmentOldAdapterCarer(Context context, ArrayList<Appointment> appointments) {
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

    public ArrayList<Appointment> getAppointments() {
        return appointments;
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
            view = inflater.inflate(R.layout.list_appointment_old_item_carer, null);

            TextView topicText = (TextView) view.findViewById(R.id.titleTextView);
            topicText.setText(appointments.get(i).getTitle());
            // TextView dateText = (TextView) view.findViewById(R.id.dateTextView);
            // dateText.setText("Date: " + dateString);
            // TextView timeText = (TextView) view.findViewById(R.id.timeTextView);
            // timeText.setText("Time: " + appointments.get(i).getTime());
            // TextView elderText = (TextView) view.findViewById(R.id.elderTextView);
            // elderText.setText(" " + appointments.get(i).getToWho());

            //to diplay the image
            ImageView completedImage = (ImageView) view.findViewById(R.id.completedImage);

            if (!appointments.get(i).getCompleted()) {
                //completedImage.setImageResource(R.drawable.completestamp);
                topicText.setTextColor(Color.RED);
            } else {
                //completedImage.setImageResource(R.drawable.notdonee);
//            completedImage.setMaxHeight(0);
                //   completedImage.setMaxWidth(0);
            }
            return view;
        }
        return view;
    }
}
