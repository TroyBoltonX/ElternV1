package com.example.charisha.elternv1;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Vibrator;
import android.view.WindowManager;
import android.widget.Toast;

import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import me.tatarka.support.job.JobParameters;
import me.tatarka.support.job.JobService;
import me.tatarka.support.os.PersistableBundle;

/**
 * Created by e-Miracle workers.
 */

public class ReminderAlertService extends JobService {

    private String userName;
    private boolean fireNotice = false;
    private static final int VIBRITION = 500;

    @Override
    public boolean onStartJob(JobParameters jobParameters) {

        PersistableBundle persistableBundle = jobParameters.getExtras();
        userName = persistableBundle.getString(MainElderScreenActivity.USER_NAME);

        List<ParseObject> reminderList = popupAlertUpdate(userName);
        showAlert(reminderList);
        return false;
    }

    private List<ParseObject> popupAlertUpdate(String elderlyName) {

        List<ParseObject> scheduledAppointment = new ArrayList<>();
        String eventDate, eventTime;

        ParseQuery<ParseObject> queryAppointment = ParseQuery.getQuery("Appointment");
        queryAppointment.whereEqualTo("toWho", elderlyName);

        try {
            List<ParseObject> appointmentList;
            appointmentList = queryAppointment.find();

            if (!appointmentList.isEmpty()) {
                for (ParseObject appointment : appointmentList) {

                    eventDate = appointment.getString("date");
                    eventTime = appointment.getString("time");

                    if (compareDateTime(eventDate, eventTime))
                        scheduledAppointment.add(appointment);

                    System.out.println("appointment.getString(\"title\")  " + appointment.getString("title"));
                }
            } else {
                Toast.makeText(getApplicationContext(), "No Appointment in the database", Toast.LENGTH_SHORT).show();
            }
        } catch (com.parse.ParseException pe) {
            pe.printStackTrace();
        }
        return scheduledAppointment;
    }

    public boolean compareDateTime(String eventDate, String eventTime) {

        String currentDateAndTime;
        Date formatCurrentDateAndTime;
        Calendar calendarAddHour;
        Calendar calendarAddMinute;
        Calendar calendarMinusMinute;
        Calendar calendarMiddleTime;
        String hourAddedTime, upTime, downTime;
        String appointmentDateAndTime;
        Date upMinute, downMinute, appointmentMinute;

        try {
            SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy HH:mm", java.util.Locale.getDefault());
            currentDateAndTime = formatter.format(new Date());

            formatCurrentDateAndTime = formatter.parse(currentDateAndTime);
            calendarAddHour = Calendar.getInstance();
            calendarAddHour.setTime(formatCurrentDateAndTime);
            calendarAddHour.add(Calendar.HOUR, 1); //3

            // testing print to console for testing
            hourAddedTime = String.valueOf(calendarAddHour.getTime()).substring(10, 16);
            System.out.println("hourAddedTime                " + hourAddedTime);

            // Up Time
            calendarAddHour.add(Calendar.MINUTE, 5);
            upTime = String.valueOf(formatter.format(calendarAddHour.getTime()));

            // testing
            System.out.println("Up Time               " + upTime);


            upMinute = formatter.parse(upTime);
            calendarAddMinute = Calendar.getInstance();
            calendarAddMinute.setTime(upMinute);

            // Down Time
            calendarAddHour.add(Calendar.MINUTE, -10);
            downTime = String.valueOf(formatter.format(calendarAddHour.getTime()));

            // testing
            System.out.println("Down Time               " + downTime);

            downMinute = formatter.parse(downTime);
            calendarMinusMinute = Calendar.getInstance();
            calendarMinusMinute.setTime(downMinute);

            // Appointment Time
            appointmentDateAndTime = eventDate + eventTime;
            Date middleTime = formatter.parse(appointmentDateAndTime);

            calendarMiddleTime = Calendar.getInstance();
            calendarMiddleTime.setTime(middleTime);

            appointmentMinute = calendarMiddleTime.getTime();

            //test
            System.out.println("appointmentMinute             " + appointmentMinute);
            System.out.println("calendarMinusMinute.getTime() " + calendarMinusMinute.getTime());
            System.out.println("calendarAddMinute.getTime()   " + calendarAddMinute.getTime());

            System.out.println("Really : really : " + (appointmentMinute.after(calendarMinusMinute.getTime()) && appointmentMinute.before(calendarAddMinute.getTime())));

            if (appointmentMinute.after(calendarMinusMinute.getTime()) && appointmentMinute.before(calendarAddMinute.getTime())) {
                fireNotice = true;
                System.out.println("Appointment Time fire notice     " + fireNotice);
            } else {
                fireNotice = false;
            }
        }  catch (java.text.ParseException e) {
            e.printStackTrace();
        }

        System.out.println("return value + " + fireNotice);
        return fireNotice;
    }

    public void showAlert(List<ParseObject> reminderList) {

        AlertDialog.Builder alertDialogBuilder;
        AlertDialog alertDialog;

        for (ParseObject reminder: reminderList) {

            final String reminderId = reminder.getObjectId();

            System.out.println("reminderId       " + reminder.getObjectId());

            alertDialogBuilder = new AlertDialog.Builder(ReminderAlertService.this)
                    .setTitle(reminder.getString("title"))
                    .setMessage("From: " + reminder.getString("fromWho") +
                            "\nTo: " + reminder.getString("toWho") +
                            "\nReminder: " + reminder.getString("description"))
                    .setIcon(R.drawable.ic_dialog_alert)
                    .setCancelable(false)
                    .setPositiveButton(R.string.alert_dismiss, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
//                        ParseQuery<ParseObject> markComplete = ParseQuery.getQuery("Appointment");
//                        markComplete.whereEqualTo("objectId", reminderId);
//
//                        markComplete.getFirstInBackground(new GetCallback<ParseObject>() {
//                            public void done(ParseObject object, ParseException e) {
//                                if (e == null) {
//
//                                    object.put("completed", true);
//                                    object.saveInBackground();
//                                } else {
//                                    Log.d("Warning: ", e.getMessage());
//                                }
//                            }
//                        });
                        dialog.dismiss();
                    }
                });

//                .setPositiveButton(R.string.alert_ok, new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        ParseQuery<ParseObject> markComplete = ParseQuery.getQuery("Appointment");
//                        markComplete.whereEqualTo("objectId", reminderId);
//
//                        markComplete.getFirstInBackground(new GetCallback<ParseObject>() {
//                            public void done(ParseObject object, ParseException e) {
//                                if (e == null) {
//
//                                    object.put("completed", true);
//                                    object.saveInBackground();
//                                } else {
//                                    Log.d("Warning: ", e.getMessage());
//                                }
//                            }
//                        });
//                        dialog.dismiss();
//                    }
//                });


//                    .setNegativeButton(R.string.alert_dismiss, new DialogInterface.OnClickListener() {
//                        @Override
//                        public void onClick(DialogInterface dialog, int which) {
//                            dialog.dismiss();
//                        }
//                    })
//                    .setCancelable(true)
//                    .create();

//            alertDialog = new AlertDialog.Builder(ReminderAlertService.this)
//                    .setTitle(reminder.getString("title"))
//                    .setMessage("From: " + reminder.getString("fromWho") +
//                            "\nTo: " + reminder.getString("toWho") +
//                            "\nReminder: " + reminder.getString("description"))
//                    .setIcon(R.drawable.ic_dialog_alert)
//                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
//                @Override
//                public void onClick(DialogInterface dialog, int which) {
//                    dialog.dismiss();
//                }
//            });
//            // Set the dialog to not be distrubed by the application
//            builder.setCancelable(false);
//            // Create dialog and display it to the user
//            builder.create().show();

            Vibrator vibrator;
            vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
            vibrator.vibrate(VIBRITION);

            try {
                Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
                Ringtone ringtone = RingtoneManager.getRingtone(getApplicationContext(), notification);
                ringtone.play();
            } catch (Exception e) {
                e.printStackTrace();
            }
            alertDialog = alertDialogBuilder.create();
            alertDialog.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
            alertDialog.show();
        }
    }

    @Override
    public boolean onStopJob(JobParameters jobParameters) {
        return false;
    }
}
