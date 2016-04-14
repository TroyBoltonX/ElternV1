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
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by e-Miracle workers.
 */

public class ViewPastReminderElderActivity extends Activity implements AdapterView.OnItemSelectedListener{

    private Appointment currentAppointment = new Appointment();
    // private TextView titleText;
    //private TextView descriptionText;
    //private TextView dateText;
    // private TextView timeText;
    private TextView dateTimeText;
    private EditText titleEditText;
    private EditText descriptionEditText;
    private String date = "";
    private String time = "";
    private Date remDate;
    public static final int SMS_REMINDER_REQUEST = 11;
    private String frequency;
    private CheckBox completedBox;
    String newDateString;
    String newTimeString;

    private int year, day, month, hour, minute;
    static final int TIME_DIALOG_ID = 1;
    static final int DATE_DIALOG_ID = 0;

    private StringBuilder setDate;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_past_reminder_elder);
        Intent i = getIntent();
        currentAppointment = i.getParcelableExtra("Appointment");
        // titleText = (TextView)findViewById(R.id.titleTextView);
        //descriptionText = (TextView)findViewById(R.id.descriptionTextView);
        // dateText = (TextView)findViewById(R.id.dateTextView);
        //timeText = (TextView)findViewById(R.id.timeTextView);
        dateTimeText = (TextView)findViewById(R.id.dateTimeText);
        titleEditText = (EditText) findViewById(R.id.titleEditText);
        descriptionEditText = (EditText) findViewById(R.id.descriptionEditText);
        completedBox = (CheckBox) findViewById(R.id.doneCheckBox);

        //show if reminder is completed on checkbox
        completedBox.setChecked(currentAppointment.getCompleted());


        // titleText.setText(currentAppointment.getTitle());
        // descriptionText.setText(currentAppointment.getDescription());
        titleEditText.setText(currentAppointment.getTitle());
        descriptionEditText.setText(currentAppointment.getDescription());
        titleEditText.setEnabled(false);
        descriptionEditText.setEnabled(false);

        if(currentAppointment.getCompleted() == true)
        {
            completedBox.setEnabled(false);
        }


/*
        //spinner to show the drop down menu for frequency
        Spinner dropdown = (Spinner)findViewById(R.id.repeatSpinner);
        String[] itemsFrequency = new String[]{"Only Once", "Daily", "Weekly", "Monthly"};
        ArrayAdapter<String> adapter2 = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, itemsFrequency);
        dropdown.setAdapter(adapter2);
        dropdown.setOnItemSelectedListener(this);

        titleEditText.setEnabled(false);
        descriptionEditText.setEnabled(false);
        dropdown.setEnabled(false);

        //set the selected value for spinner
        frequency = currentAppointment.getFrequency();
        if (!frequency.equals(null)) {
            int spinnerPosition = adapter2.getPosition(frequency);
            dropdown.setSelection(spinnerPosition);
        }
*/
        //dateText.setText(currentAppointment.getDate());
        // timeText.setText(currentAppointment.getTime());

        date = currentAppointment.getDate();
        time = currentAppointment.getTime();
        //formated date
        String formattedRemDate = "";

        //put date into date object
        String dateString2 = date+" "+time+":"+"00";
        try {
            remDate = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss").parse(dateString2);
            SimpleDateFormat sdf = new SimpleDateFormat("EEE dd-MMM-yyyy\nhh:mm aa");
            formattedRemDate = sdf.format(remDate);
        }
        catch (Exception e){

        }

        dateTimeText.setText(formattedRemDate);


    /*    String dateTime = "Date: "+ currentAppointment.getDate()+" Time: "+currentAppointment.getTime();
        //dateTimeText.setText(dateTime);

        //set the date in the correct format
        String month2 = currentAppointment.getDate().substring(3, 5);

        //set the month in the correct format
        String monthString = "";
        if (month2.equals("01")) {
            monthString = "Jan";
        }
        if (month2.equals("02")) {
            monthString = "Feb";
        }
        if (month2.equals("03")) {
            monthString = "Mar";
        }
        if (month2.equals("04")) {
            monthString = "Apr";
        }
        if (month2.equals("05")) {
            monthString = "May";
        }
        if (month2.equals("06")) {
            monthString = "Jun";
        }
        if (month2.equals("07")) {
            monthString = "Jul";
        }
        if (month2.equals("08")) {
            monthString = "Aug";
        }
        if (month2.equals("09")) {
            monthString = "Sep";
        }
        if (month2.equals("10")) {
            monthString = "Oct";
        }
        if (month2.equals("11")) {
            monthString = "Nov";
        }
        if (month2.equals("12")) {
            monthString = "Dec";
        }

        String dateString = "Date: "+ currentAppointment.getDate().substring(0, 3) + monthString + currentAppointment.getDate().substring(5, 10)+"\nTime: "+currentAppointment.getTime();;

        dateTimeText.setText(dateString);


        final Calendar calendar = Calendar.getInstance();
        year = calendar.get(Calendar.YEAR);
        month = calendar.get(Calendar.MONTH);
        day = calendar.get(Calendar.DAY_OF_MONTH);
        hour = calendar.get(Calendar.HOUR_OF_DAY);
        minute = calendar.get(Calendar.MINUTE);
*/
   //     ImageButton imButton = (ImageButton) findViewById(R.id.imageButton);
/*
        //Pick time's click event listener
        imButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                showDialog(DATE_DIALOG_ID);
            }
        });
        */
/*
        //Pick time's click event listener
        dateTimeText.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                showDialog(DATE_DIALOG_ID);
            }
        });
*/
    }

    /*
 listener for the drop down menu (spinner) to get user type
  */
    public void onItemSelected(AdapterView<?> parent, View view,
                               int pos, long id) {

        //retrive the user's selection of frequence
        frequency = parent.getItemAtPosition(pos).toString();
    }

    /*
    what to do if nothing is chosen on drop down menu
     */
    public void onNothingSelected(AdapterView<?> parent) {
        // Another interface callback
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_edit_reminder_elder, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
/*
        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }
*/
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        //make keyboard disappear
        InputMethodManager imm = (InputMethodManager)getSystemService(Context.
                INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        return true;
    }

    public void editReminder(View v) {

        //check if network is available
        if (isNetworkConnected()) {


            //check if date input is after today
            if (checkDate() == true) {
                //check if any changes have been made to reminder and ask user whether to save changes
                if (!currentAppointment.getCompleted() == completedBox.isChecked()) {
                    AlertDialog.Builder builder2 = new AlertDialog.Builder(ViewPastReminderElderActivity.this);
                    builder2.setTitle("Edit appointment?");
                    builder2.setMessage("Are you sure you want to save changes to this reminder?");
                    builder2.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {


                                //Retrieve the object from server and change its information.
                                ParseQuery<ParseObject> query = ParseQuery.getQuery("Appointment");
                                //query.whereEqualTo("title", currentAppointment.getTitle());
                                //query.whereEqualTo("description", currentAppointment.getDescription());
                                //query.whereEqualTo("date", currentAppointment.getDate());
                                //query.whereEqualTo("time", currentAppointment.getTime());
                                //query.whereEqualTo("toWho", currentAppointment.getToWho());
                                //query.whereEqualTo("fromWho", currentAppointment.getfromWho());
                                query.whereEqualTo("objectId", currentAppointment.getId());
                                query.getFirstInBackground(new GetCallback<ParseObject>() {
                                    public void done(ParseObject object, ParseException e) {
                                        if (object == null) {
                                            Log.d("score", "The getFirst request failed.");

                                        } else {
                                            //change the value of the attribute to what the staff input, if the EditText is empty, maintain the original value.
                                            //  if (titleEditText.getText().toString().isEmpty()==false)
                                           // object.put("title", titleEditText.getText().toString());

                                            //  if (date.equals("") == false)
                                           // object.put("date", date);
                                            //  if (time.equals("") == false)
                                          //  object.put("time", time);
                                            // if (descriptionEditText.getText().toString().isEmpty()==false)
                                           // object.put("description", descriptionEditText.getText().toString());
                                           // object.put("frequency", frequency);
                                            object.put("completed", completedBox.isChecked());
                                            object.saveInBackground();

                                            Intent intent = new Intent();
                                            setResult(RESULT_OK, intent);
                                            finish();
                                        }
                                    }
                                });

                            Toast.makeText(getBaseContext(), "Reminder edited",
                                    Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent();
                            setResult(RESULT_OK, intent);
                            finish();
                        }
                    });
                    //if user choses not to save changes to appointment
                    builder2.setNegativeButton("No", new
                            DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    dialogInterface.cancel();
                                    Intent intent = new Intent();
                                    setResult(RESULT_OK, intent);
                                    finish();
                                }
                            });
                    builder2.create().show();
                } else {
                    //if no changes made to appointment
                    Intent intent = new Intent();
                    setResult(RESULT_OK, intent);
                    finish();
                }

            } else {
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
        } else {
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
        dateTimeText.setText(
                setDate
                        .append("\nTime: ")
                        .append(format(hour)).append(":")
                        .append(format(minute)));
        time = setDate.toString().substring(24);

        //set month
        String monthString;
        switch (month) {
            case 0:
                monthString = "Jan";
                break;
            case 1:
                monthString = "Feb";
                break;
            case 2:
                monthString = "Mar";
                break;
            case 3:
                monthString = "Apr";
                break;
            case 4:
                monthString = "May";
                break;
            case 5:
                monthString = "Jun";
                break;
            case 6:
                monthString = "Jul";
                break;
            case 7:
                monthString = "Aug";
                break;
            case 8:
                monthString = "Sep";
                break;
            case 9:
                monthString = "Oct";
                break;
            case 10:
                monthString = "Nov";
                break;
            case 11:
                monthString = "Dec";
                break;
            default:
                monthString = "Invalid month";
                break;
        }
        //correct day, month, hour and minute formats
        String dayString = String.valueOf(day);
        String hourString = String.valueOf(hour);
        String minuteString = String.valueOf(minute);

        if(String.valueOf(day).length()<2)
        {
            dayString = "0".concat(dayString);
        }

        if(String.valueOf(minute).length()<2)
        {
            minuteString = "0".concat(minuteString);
        }

        if(String.valueOf(hour).length()<2)
        {
            hourString = "0".concat(hourString);
        }

        newDateString = dayString + "-" + monthString + "-" + year;
        newTimeString = minuteString + ":" + hourString;

        String dateString = "Date: " + dayString + "-" + monthString + "-"+ year + "\nTime: " +hourString+ ":" +minuteString;
        dateTimeText.setText(dateString);
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

    //check if reminder date is valid
    private boolean checkDate()
    {/*
        //check if date changed
        if (currentAppointment.getDate().equals(date) && currentAppointment.getTime().equals(time))
        {
            return true;
        }

        else{
            //correct month
            int correctedMonth = month + 1;

            //put date into date object
            String dateString = year + "-" + correctedMonth + "-" + day + " " + hour + ":" + minute + ":" + "00";

            try {
                remDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(dateString);
            } catch (Exception e) {

            }

            Calendar c = Calendar.getInstance();
            //int secondsT = c.get(Calendar.MILLISECOND);
            //long timeInMillisSinceEpoch = remDate.getTime();
            Date todayDate = c.getTime();

            if (todayDate.compareTo(remDate) > 0) {
                //System.out.println("Date1 is after Date2");
                return false;
            }
        }
*/
        return true;

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

    /*
    Executed after an activity
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //triggered after the add reminder activity
        if (requestCode == SMS_REMINDER_REQUEST) {
            if (resultCode == RESULT_OK) {

                Intent intent = new Intent();
                setResult(RESULT_OK, intent);
                finish();

            }
        }
    }


    /*
 Method executed when sms button is pressed
  */
/*    public void smsReminder(View v){

        System.out.println("got here!!!!!!");
        //check if network is available
        if (isNetworkConnected() == true) {


            //check if date input is after today
            if (checkDate() == true) {
                //check if any changes have been made to reminder and ask user whether to save changes
                if(!currentAppointment.getCompleted() == completedBox.isChecked())
                {

                    AlertDialog.Builder builder2 = new AlertDialog.Builder(ViewPastReminderElderActivity.this);
                    builder2.setTitle("Edit appointment?");
                    builder2.setMessage("Are you sure you want to save changes to this reminder?");
                    builder2.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {

                            //check if changes have been made to appointment
                            if (titleEditText.getText().toString().equals("") || titleEditText.getText().toString().isEmpty()
                                    || descriptionEditText.getText().toString().equals("") || descriptionEditText.getText().toString().isEmpty()) {
                                // Use the AlertDialog Builder utility to create the dialog
                                AlertDialog.Builder builder = new AlertDialog.Builder(ViewPastReminderElderActivity.this);
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

                                //Retrieve the object from server and change its information.
                                ParseQuery<ParseObject> query = ParseQuery.getQuery("Appointment");
                                // query.whereEqualTo("title", currentAppointment.getTitle());
                                // query.whereEqualTo("description", currentAppointment.getDescription());
                                // query.whereEqualTo("date", currentAppointment.getDate());
                                // query.whereEqualTo("time", currentAppointment.getTime());
                                //query.whereEqualTo("toWho", currentAppointment.getToWho());
                                //query.whereEqualTo("fromWho", currentAppointment.getfromWho());
                                query.whereEqualTo("objectId", currentAppointment.getId());
                                query.getFirstInBackground(new GetCallback<ParseObject>() {
                                    public void done(ParseObject object, ParseException e) {
                                        if (object == null) {
                                            Log.d("score", "The getFirst request failed.");

                                        } else {
                                            //change the value of the attribute to what the staff input, if the EditText is empty, maintain the original value.
                                            //  if (titleEditText.getText().toString().isEmpty()==false)
                                            object.put("title", titleEditText.getText().toString());

                                            //  if (date.equals("") == false)
                                            object.put("date", date);
                                            //  if (time.equals("") == false)
                                            object.put("time", time);
                                            // if (descriptionEditText.getText().toString().isEmpty()==false)
                                            object.put("description", descriptionEditText.getText().toString());
                                            object.saveInBackground();

                                            //if changes have been made to appointment and use chooses to save changes
                                            Intent intent = new Intent();
                                            Appointment newAppointment = new Appointment();
                                            newAppointment = currentAppointment;
                                            newAppointment.setDate(date);
                                            newAppointment.setTime(time);
                                            newAppointment.setTitle(titleEditText.getText().toString());
                                            newAppointment.setDescription(descriptionEditText.getText().toString());

                                            Intent intent2 = new Intent(ViewPastReminderElderActivity.this, SmsElderlyActivity.class);
                                            intent2.putExtra("Appointment", currentAppointment);
                                            startActivityForResult(intent, SMS_REMINDER_REQUEST);
                                            finish();
                                        }
                                    }
                                });

                            }


                        }
                    });
                    //if changes have been made to appointment and uses choses not to save appointment
                    builder2.setNegativeButton("No", new
                            DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    Intent intent = new Intent(ViewPastReminderElderActivity.this, SmsElderlyActivity.class);
                                    intent.putExtra("Appointment", currentAppointment);
                                    startActivityForResult(intent, SMS_REMINDER_REQUEST);
                                    finish();
                                }
                            });
                    builder2.create().show();
                } else {
                    //if no changes have been made to appointment
                    Intent intent = new Intent(ViewPastReminderElderActivity.this, SmsElderlyActivity.class);
                    intent.putExtra("Appointment", currentAppointment);
                    startActivityForResult(intent, SMS_REMINDER_REQUEST);
                    finish();
                }

            } else {
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
        } else {
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
    */
}
