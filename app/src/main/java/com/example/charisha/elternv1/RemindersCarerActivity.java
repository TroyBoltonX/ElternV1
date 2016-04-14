package com.example.charisha.elternv1;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.SaveCallback;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Created by e-Miracle workers.
 * The list of reminders for carer
 */

public class RemindersCarerActivity extends Activity {
    private ListView appointmentList;
    private AppointmentAdapterCarer adapter;
    private AppointmentAdapterCarer adapter2;
    public static ArrayList<Appointment> appointments;
    // private AppointmentAdapterElder adapter;
    private Appointment appointment;
    public static Appointment tempAppointment = new Appointment();
    private List<ParseObject> AList;
    private TextView noRemindersTV;
    public static final int ADD_REMINDER_REQUEST = 5;
    public static final int EDIT_REMINDER_REQUEST = 6;
    public static final int PAST_REMINDER_REQUEST = 8;
    public static final int CURRENT_REMINDER_REQUEST = 9;

    private ProgressBar progBar;
    private Date remDate;
    Appointment repeatAppointment;
    String newId = "";
    Appointment rem ;
    boolean downloaded = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reminders_carer);

        appointmentList = (ListView) findViewById(R.id.foodListView);
        noRemindersTV = (TextView) findViewById(R.id.noRemTV);
        progBar = (ProgressBar) findViewById(R.id.progressBar);
        progBar.setVisibility(View.VISIBLE);

        //retrieve all appointments information from server, save information to dish object and add them to ArrayList dishes one by one,
        //showing on appointmentList.
        appointments = new ArrayList<Appointment>();
        ParseQuery<ParseObject> query = ParseQuery.getQuery("Appointment");

        //check if network is available
        if (isNetworkConnected()) {

            query.whereEqualTo("fromWho", MainActivity.userName);
            query.findInBackground(new FindCallback<ParseObject>() {
                @Override
                public void done(List<ParseObject> appointmentlist, ParseException e) {

                    if (appointmentlist.size() > 0) {
                        for (int i = 0; i < appointmentlist.size(); i++) {
                            tempAppointment.setTitle(appointmentlist.get(i).getString("title"));
                            tempAppointment.setDescription(appointmentlist.get(i).getString("description"));
                            tempAppointment.setDate(appointmentlist.get(i).getString("date"));
                            tempAppointment.setTime(appointmentlist.get(i).getString("time"));
                            tempAppointment.setId(appointmentlist.get(i).getObjectId());

                            tempAppointment.setToWho(appointmentlist.get(i).getString("toWho"));
                            tempAppointment.setFromWho(appointmentlist.get(i).getString("fromWho"));
                            tempAppointment.setFrequency(appointmentlist.get(i).getString("frequency"));
                            tempAppointment.setCompleted(appointmentlist.get(i).getBoolean("completed"));

                           //check if reminder is not a past reminder
                            if(isPastReminder(tempAppointment) == false)
                            {
                                appointments.add(tempAppointment);
                            }
                            //if it is a past remidner check if it is a repeating remider
                            else
                            {
                                //check if reminder is a repeating reminder
                                if (!tempAppointment.getFrequency().equals("Only Once"))
                                {
                                    //set the repeated reminder
                                    repeatAppointment = setRepeatAppointment(tempAppointment);
                                    appointments.add(repeatAppointment);
                               }
                            }
                            tempAppointment = new Appointment();
                        }
                        adapter = new AppointmentAdapterCarer(RemindersCarerActivity.this, appointments);
                        //adapter = new AppointmentAdapterElder(RemindersCarerActivity.this, appointments);
                        appointmentList.setAdapter(adapter);

                        //check if there are reminders
                        if (appointmentList.getAdapter().isEmpty()) {
                            noRemindersTV.setText("No Reminders");
                            progBar.setVisibility(View.GONE);
                        } else {
                            noRemindersTV.setText("");
                            progBar.setVisibility(View.GONE);
                        }

                    } else {
                        //Log.d("dish", "Error: " + e.getMessage());
                        progBar.setVisibility(View.GONE);
                        noRemindersTV.setText("No Reminders");
                    }
                }
            });
        }
        else
        {
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



        appointmentList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                appointment = (Appointment) appointmentList.getAdapter().getItem(i);

                // Return the object to the MainActivity and close this activity
                Intent intent = new Intent(view.getContext(), EditReminderCarerActivity.class);

                intent.putExtra("Appointment", appointment);
                startActivityForResult(intent, EDIT_REMINDER_REQUEST);
                //   RemindersCarerActivity.this.finish();
            }
        });


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_reminders_carer, menu);
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
        //     return true;
        //  }

        switch(item.getItemId())
        {
          //go to past remuinders screen for carer
            case R.id.refresh:
                /* Intent intent3 = new Intent(RemindersCarerActivity.this, RemindersCarerActivity.class);
                startActivityForResult(intent3, CURRENT_REMINDER_REQUEST);
                finish();
                break;
                */
                refreshListView();
                break;

            //go to add reminder screen for carer
            case R.id.add:
                Intent intent = new Intent(RemindersCarerActivity.this, AddRemindersCarerActivity.class);
                startActivityForResult(intent, ADD_REMINDER_REQUEST);
                break;

            //go to past remuinders screen for carer
            case R.id.past:
                Intent intent2 = new Intent(RemindersCarerActivity.this, PastRemindersCarerActivity.class);
                startActivityForResult(intent2, PAST_REMINDER_REQUEST);
                finish();
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    // We override this method when we are expecting a result from an
// activity we have called.
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //triggered after the add reminder activity
        if (requestCode == ADD_REMINDER_REQUEST )
        {
            if (resultCode == RESULT_OK)
            {
                //call method to refresh listview with the newly added reminder
                refreshListView();
                Toast.makeText(this, "Reminder saved", Toast.LENGTH_SHORT).show();
            }
        }

        if (requestCode == EDIT_REMINDER_REQUEST)
        {
            if (resultCode == RESULT_OK)
            {
                //call method to refresh listview with the newly added reminder
                refreshListView();
                // Toast.makeText(this, "Reminder saved", Toast.LENGTH_SHORT).show();
            }
        }
    }

    /*
    This method will retrieve all the reminder object in the database,
    add it to the listReminders arraylist and update the ListView screen
     */

    private void refreshListView() {

        //retrieve all appointments information from server, save information to dish object and add them to ArrayList dishes one by one,
        //showing on appointmentList.
        ParseQuery<ParseObject> query = ParseQuery.getQuery("Appointment");
        //clear arraylist
        appointments.clear();
        noRemindersTV.setText("");
        progBar.setVisibility(View.VISIBLE);

        //check if network is available
        if (isNetworkConnected() == true) {

            query.whereEqualTo("fromWho", MainActivity.userName);
            query.findInBackground(new FindCallback<ParseObject>() {
                @Override
                public void done(List<ParseObject> appointmentlist, ParseException e) {

                    if (appointmentlist.size() > 0) {
                        for (int i = 0; i < appointmentlist.size(); i++) {
                            tempAppointment.setTitle(appointmentlist.get(i).getString("title"));
                            tempAppointment.setDescription(appointmentlist.get(i).getString("description"));
                            tempAppointment.setDate(appointmentlist.get(i).getString("date"));
                            tempAppointment.setTime(appointmentlist.get(i).getString("time"));
                            tempAppointment.setId(appointmentlist.get(i).getObjectId());

                            tempAppointment.setToWho(appointmentlist.get(i).getString("toWho"));
                            tempAppointment.setFromWho(appointmentlist.get(i).getString("fromWho"));
                            tempAppointment.setFrequency(appointmentlist.get(i).getString("frequency"));
                            tempAppointment.setCompleted(appointmentlist.get(i).getBoolean("completed"));
                            if (isPastReminder(tempAppointment) == false) {
                                appointments.add(tempAppointment);
                            }

                            //if it is a past remidner check if it is a repeating remider
                            else
                            {
                                //check if reminder is a repeating reminder
                                if (!tempAppointment.getFrequency().equals("Only Once"))
                                {
                                    //set the repeated reminder
                                    repeatAppointment = setRepeatAppointment(tempAppointment);
                                    appointments.add(repeatAppointment);
                                }
                            }

                            tempAppointment = new Appointment();
                        }
                        adapter2 = new AppointmentAdapterCarer(RemindersCarerActivity.this, appointments);
                        appointmentList.setAdapter(adapter2);

                        //indicate to user if no remidners are present in the adapter
                        if (appointmentList.getAdapter().isEmpty()) {
                            noRemindersTV.setText("no reminders");
                            progBar.setVisibility(View.GONE);
                        } else {
                            noRemindersTV.setText("");
                            progBar.setVisibility(View.GONE);
                        }
                    } else {
                        //Log.d("dish", "Error: " + e.getMessage());
                        progBar.setVisibility(View.GONE);
                        noRemindersTV.setText("No Reminders");
                    }
                }
            });
            progBar.setVisibility(View.GONE);
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
    Method to check to the reminder is a past reminder
    compares today's date with the reminder date
     */

    private boolean isPastReminder(Appointment remi)
    {
        Appointment reminder = remi;

        //put date into date object
        String dateString = reminder.getDate()+" "+reminder.getTime()+":"+"00";
        try {
            remDate = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss").parse(dateString);
        }
        catch (Exception e){

        }

        Calendar c = Calendar.getInstance();
        //int secondsT = c.get(Calendar.MILLISECOND);
        //long timeInMillisSinceEpoch = remDate.getTime();
        Date todayDate = c.getTime();

        //if today is after reminder date
        if(todayDate.compareTo(remDate)>=0){
            //System.out.println("Date1 is after Date2");

            return true;
        }

        return false;
    }

    public Appointment setRepeatAppointment(Appointment reminder)
    {
        rem = reminder;
        int frequency = 0;
        Appointment previousRem = reminder;

        Calendar c = Calendar.getInstance();
        Date todayDate = c.getTime();

        //put date into date object
        String dateString = rem.getDate()+" "+rem.getTime()+":"+"00";

        try {
            remDate = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss").parse(dateString);
        }
        catch (Exception e)
        {

        }

        rem.setDateTime(remDate);

       // System.out.println("today " + todayDate);
       // System.out.println("rem date " + remDate);

        int day = Integer.parseInt(rem.getDate().substring(0, 2));
        int month = Integer.parseInt(rem.getDate().substring(3, 5));
        //correct month
        month = month -1;
        //corrected day
        day = day-1;

        int year = Integer.parseInt(rem.getDate().substring(6, 10));

        int min = Integer.parseInt(rem.getTime().substring(3, 5));
        int hour = Integer.parseInt(rem.getTime().substring(0, 2));
        int sec = 00;

        Calendar cal2 = Calendar.getInstance();
        cal2.set(Calendar.DAY_OF_MONTH, day);
        cal2.set(Calendar.MONTH, month);
        cal2.set(Calendar.YEAR, year);
        cal2.set(Calendar.MINUTE, min);
        cal2.set(Calendar.HOUR, hour);
        cal2.set(Calendar.SECOND, sec);

        while(todayDate.compareTo(cal2.getTime())>0)
        {
            if (rem.getFrequency().equals("Daily"))
            {
                cal2.add(Calendar.DAY_OF_MONTH, 1);
                System.out.println("frequency 1");
            }
            if (rem.getFrequency().equals("Weekly"))
            {
                cal2.add(Calendar.DAY_OF_MONTH, 7);
                System.out.println("frequency 2");
            }
            if (rem.getFrequency().equals("Monthly"))
            {
                cal2.add(Calendar.MONTH, 1);
                System.out.println("frequency 3");
            }
        }
//check the frequency of previous object to only once
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
        String newRemDate = sdf.format(cal2.getTime());
        rem.setDate(newRemDate);
System.out.println("got here!!! " + previousRem.getTitle() + " " + previousRem.getDescription() + " " + previousRem.getDate()
+" "+ previousRem.getTime() + " " + previousRem.getToWho() + " " + previousRem.getfromWho());

        //Retrieve the object from server and change its information.
        ParseQuery<ParseObject> query = ParseQuery.getQuery("Appointment");
        query.whereEqualTo("objectId", previousRem.getId());
        query.getFirstInBackground(new GetCallback<ParseObject>() {
            public void done(ParseObject object, ParseException e) {
                if (object == null) {
                    Log.d("score", "The getFirst request failed.");

                } else {
                    //change the value of the attribute to what the staff input, if the EditText is empty, maintain the original value.
                    //  if (titleEditText.getText().toString().isEmpty()==false)
                    object.put("frequency", "Only Once");
                    object.saveInBackground();
                }
            }
        });

  //add new reminder to database
        //save repeat appoitment to database
        final ParseObject newAppointment = new ParseObject("Appointment");
        newAppointment.put("title", rem.getTitle());
        newAppointment.put("date", rem.getDate());
        newAppointment.put("time", rem.getTime());
        newAppointment.put("description", rem.getDescription());
        newAppointment.put("toWho", rem.getToWho());
        newAppointment.put("fromWho", rem.getfromWho());
        newAppointment.put("frequency", rem.getFrequency());

        // newAppointment.saveInBackground();

       newAppointment.saveInBackground(new SaveCallback() {
            public void done(ParseException e) {
                if (e == null) {
                    // Saved successfully.
                    newId = newAppointment.getObjectId();
                    rem.setId(newId);
                    System.out.println("repeat object ID " + newId);
                   // return rem;
                   // downloaded = true;
                } else {
                    // The save failed.
                }
            }
        });
        //reset completed to not done
        rem.setCompleted(false);
        return rem;
    }
}
