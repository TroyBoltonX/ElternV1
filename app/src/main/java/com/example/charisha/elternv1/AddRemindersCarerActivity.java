package com.example.charisha.elternv1;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Created by e-Miracle workers.
 */

public class AddRemindersCarerActivity extends Activity implements AdapterView.OnItemSelectedListener{

    private EditText titleText;
    private EditText descriptionText;
    private Spinner dropdown;
    private String date;
    private String time;
    private String toWho;
    private TextView textDateTime;
    private int year, day, month, hour, minute;
    static final int TIME_DIALOG_ID = 1;
    static final int DATE_DIALOG_ID = 0;
    private ArrayList<String> items = new ArrayList<String>();
    private Date remDate;
    private String frequency;
    private Boolean completed = false;
    String newDateString;
    String newTimeString;

    public static ArrayList<Appointment> appointments;

    public static Appointment tempAppointment = new Appointment();

    private StringBuilder setDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_reminders_carer);

        dropdown = (Spinner)findViewById(R.id.spinner);
        titleText = (EditText) findViewById(R.id.elderReminderTitleET);
        descriptionText = (EditText) findViewById(R.id.elderReminderDescriptionET);
        textDateTime = (TextView)findViewById(R.id.dateTimeText);

        //spinner to show the drop down menu for frequency
        Spinner dropdown2 = (Spinner)findViewById(R.id.repeatSpinner);
        String[] itemsFrequency = new String[]{"Only Once", "Daily", "Weekly", "Monthly"};
        ArrayAdapter<String> adapter2 = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, itemsFrequency);
        dropdown2.setAdapter(adapter2);
        dropdown2.setOnItemSelectedListener(this);

        //check if network is available
        if (isNetworkConnected()) {

            ParseQuery<ParseObject> query = ParseQuery.getQuery("ElderlyCarer");

            query.whereEqualTo("carerID", MainActivity.userName);
            query.findInBackground(new FindCallback<ParseObject>() {
                @Override
                public void done(List<ParseObject> list, ParseException e) {

                    if (list.size() > 0) {
                        for (int i = 0; i < list.size(); i++) {
                            String id = (String) list.get(i).getString("elderlyID").toString();
                            items.add(id);
                        }

                    } else {
                        //Log.d("dish", "Error: " + e.getMessage());
                    }

                    if (items.size() == 0)
                    {
                        // Use the AlertDialog Builder utility to create the dialog
                        AlertDialog.Builder builder = new AlertDialog.Builder(AddRemindersCarerActivity.this);
// Set message and title for dialog (we can chain methods here!)
                        builder.setTitle("No Elder Added!").setMessage("You don't have an elder added. An elder has to be added " +
                                "to set appointments. Please " +
                                "go to main screen and tap the 'Manage Elder' tab to add an elder");
// Add a button to the dialog with an event handler for clicks (presses)
                        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                                finish();
                            }
                        });
// Set the dialog to not be distrubed by the application
                        builder.setCancelable(false);
// Create dialog and display it to the user
                        builder.create().show();
                    }
                    setAdapter();
                }
            });

            final Calendar calendar = Calendar.getInstance();
            year = calendar.get(Calendar.YEAR);
            month = calendar.get(Calendar.MONTH);
            day = calendar.get(Calendar.DAY_OF_MONTH);
            hour = calendar.get(Calendar.HOUR_OF_DAY);
            minute = calendar.get(Calendar.MINUTE);
            date = "";
            time = "";

            ImageButton imButton = (ImageButton) findViewById(R.id.imageButton);

            imButton.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    showDialog(DATE_DIALOG_ID);
                }
            });
        }
        else{
            // Use the AlertDialog Builder utility to create the dialog
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
// Set message and title for dialog (we can chain methods here!)
            builder.setTitle("No connection!").setMessage("Check network connection");
// Add a button to the dialog with an event handler for clicks (presses)
            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
// Set the dialog to not be distrubed by the application
            builder.setCancelable(false);
// Create dialog and display it to the user
            builder.create().show();
        }
    }

    public void setAdapter(){

        System.out.println("items**" + items.size());
        ArrayAdapter<String> adapter2 = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, items);
        adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        dropdown.setAdapter(adapter2);
        dropdown.setOnItemSelectedListener(this);
    }

    public void onItemSelected(AdapterView<?> parent, View view,
                               int pos, long id) {

        switch(parent.getId()){
            case R.id.spinner:
                //retrieve the user's selection of usertype
                toWho = parent.getItemAtPosition(pos).toString();
                break;
            case R.id.repeatSpinner:
                //retrive the user's selection of frequence
                frequency = parent.getItemAtPosition(pos).toString();
                break;
        }
    }
    public void onNothingSelected(AdapterView<?> parent) {
        // Another interface callback
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_add_reminders_carer, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        //   if (id == R.id.action_settings) {
        //       return true;
        //   }

        return super.onOptionsItemSelected(item);
    }

    //make keyboard disappear
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        //make keyboard disappear
        InputMethodManager imm = (InputMethodManager)getSystemService(Context.
                INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        return true;
    }
    /*
Method executed when add reminders button is pressed
Reminder would be added to the database
 */
    public void addReminderCarer(View v)
    {
        //check if network is available
        if (isNetworkConnected()) {

// check values in edit text for Reminder information are not empty
            if (titleText.getText().toString().isEmpty() || titleText.getText().toString().equals("") ||
                    descriptionText.getText().toString().isEmpty() || descriptionText.getText().toString().equals("") ||
                    date.isEmpty() || date.equals("") || time.equals("") ||
                    time.isEmpty()) {

                // Use the AlertDialog Builder utility to create the dialog
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
// Set message and title for dialog (we can chain methods here!)
                builder.setTitle("Required Fields!").setMessage("Please enter all fields");
// Add a button to the dialog with an event handler for clicks (presses)
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
// Set the dialog to not be distrubed by the application
                builder.setCancelable(false);
// Create dialog and display it to the user
                builder.create().show();

            } else {

                //check if date input is after today
                if(checkDate() == true) {

                    ParseObject newAppointment = new ParseObject("Appointment");
                    newAppointment.put("title", titleText.getText().toString());
                    newAppointment.put("date", date);
                    newAppointment.put("time", time);
                    newAppointment.put("description", descriptionText.getText().toString());
                    newAppointment.put("toWho", toWho);
                    newAppointment.put("fromWho", MainActivity.userName);
                    newAppointment.put("frequency", frequency);
                    newAppointment.put("completed", completed);

                    newAppointment.saveInBackground();
                    titleText.setText("");
                    descriptionText.setText("");
                    textDateTime.setText("Tap to choose date and time");

                    //  notnulText.setText("add Reminder successfully");
                    Intent intent = new Intent();
                    setResult(RESULT_OK, intent);
                    finish();
                }
                else
                {
                    // Use the AlertDialog Builder utility to create the dialog
                    AlertDialog.Builder builder = new AlertDialog.Builder(this);
// Set message and title for dialog (we can chain methods here!)
                    builder.setTitle("Check date and time!").setMessage("Date and time cannot be in the past");
// Add a button to the dialog with an event handler for clicks (presses)
                    builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
// Set the dialog to not be distrubed by the application
                    builder.setCancelable(false);
// Create dialog and display it to the user
                    builder.create().show();
                }
            }
        }
        else{
            // Use the AlertDialog Builder utility to create the dialog
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
// Set message and title for dialog (we can chain methods here!)
            builder.setTitle("No connection!").setMessage("Check network connection");
// Add a button to the dialog with an event handler for clicks (presses)
            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
// Set the dialog to not be distrubed by the application
            builder.setCancelable(false);
// Create dialog and display it to the user
            builder.create().show();
        }
    }

    private void updateDate() {
//        textDateTime.setText(
//                new StringBuilder()
//                        // Month is 0 based so add 1
//                        .append("Date: ")
//                        .append(day).append("-")
//                        .append(month + 1).append("-")
//                        .append(year).append(" "));

        setDate = new StringBuilder()
                .append("Date: ")
                .append(format(day)).append("-")
                .append(format(month + 1)).append("-")
                .append(year).append(" ");
        date = setDate.toString().substring(6);
        showDialog(TIME_DIALOG_ID);
    }

    //-------------------------------------------update time---//
    public void updateTime() {
        textDateTime.setText(
                setDate
                        .append("\nTime: ")
                        .append(format(hour)).append(":")
                        .append(format(minute)));

        time = setDate.toString().substring(24);

        //correct month
        int correctedMonth = month + 1;

        //formated date
        String formattedRemDate = "";

        //put date into date object
        String dateString2 = year+"-"+correctedMonth+"-"+day+" "+hour+":"+minute+":"+"00";
        try {
            remDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(dateString2);
            SimpleDateFormat sdf = new SimpleDateFormat("EEE dd-MMM-yyyy\nhh:mm aa");
            formattedRemDate = sdf.format(remDate);
        }
        catch (Exception e){

        }


        //String dateString = "Date: " + dayString + "-" + monthString + "-"+ year + "\nTime: " +hourString+ ":" +minuteString;
        textDateTime.setText(formattedRemDate);
    }

    private static String format(int c) {
        if (c >= 10)
            return String.valueOf(c);
        else
            return "0" + String.valueOf(c);
    }

    //Datepicker dialog generation

    private DatePickerDialog.OnDateSetListener mDateSetListener =
            new DatePickerDialog.OnDateSetListener() {

                public void onDateSet(DatePicker view, int yearOf,
                                      int monthOfYear, int dayOfMonth) {

                    year = yearOf;
                    month = monthOfYear;
                    day = dayOfMonth;
                    updateDate();
                }
            };

    // Timepicker dialog generation
    private TimePickerDialog.OnTimeSetListener mTimeSetListener =
            new TimePickerDialog.OnTimeSetListener() {
                public void onTimeSet(TimePicker view, int hourOfDay, int minuteOfHour) {
                    hour = hourOfDay;
                    minute = minuteOfHour;
                    updateTime();
                }
            };

    @Override
    protected Dialog onCreateDialog(int id) {
        switch (id) {
            case DATE_DIALOG_ID:
                return new DatePickerDialog(this,
                        mDateSetListener,
                        year, month, day);

            case TIME_DIALOG_ID:
                return new TimePickerDialog(this,
                        mTimeSetListener, hour, minute, false);
        }

        return null;
    }

    /*
    Check network connection
     */
    private boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo ni = cm.getActiveNetworkInfo();
        if (ni == null) {
            // There are no active networks.
            return false;
        } else
            return true;
    }

    //check if reminder date is valid
    private boolean checkDate()
    {
        //correct month
        int correctedMonth = month + 1;

        //put date into date object
        String dateString = year+"-"+correctedMonth+"-"+day+" "+hour+":"+minute+":"+"00";
        try {
            remDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(dateString);
        }
        catch (Exception e){

        }

        Calendar c = Calendar.getInstance();
        //int secondsT = c.get(Calendar.MILLISECOND);
        //long timeInMillisSinceEpoch = remDate.getTime();
        Date todayDate = c.getTime();
        System.out.println("today +++ " + todayDate.toString());
        System.out.println("reminder +++ " + remDate.toString());
        System.out.println("rem month" + month);

        //if today is after reminder date
        if(todayDate.compareTo(remDate)>=0){
            //System.out.println("Date1 is after Date2");
            return false;
        }

        return true;
    }
}
