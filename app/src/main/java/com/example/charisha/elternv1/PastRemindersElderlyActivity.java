package com.example.charisha.elternv1;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

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
 * SHow list of reminders for elderly
 */

public class PastRemindersElderlyActivity extends Activity {
    private ListView appointmentList;
    public static ArrayList<Appointment> appointments;
    private AppointmentOldAdapterElder adapter;
    private Appointment appointment;
    public static Appointment tempAppointment = new Appointment();
    private List<ParseObject> AList;
    private TextView noRemindersTV;
    public static final int CURRENT_REMINDER_REQUEST = 10;
    public static final int EDIT_REMINDER_REQUEST = 6;
    public static final int ADD_REMINDER_REQUEST = 5;
    private ProgressBar progBar;
    private AppointmentOldAdapterElder adapter2;
    private Date remDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_past_reminders_elderly);
        appointmentList = (ListView) findViewById(R.id.listView);
        appointments = new ArrayList<Appointment>();
        noRemindersTV = (TextView) findViewById(R.id.noRemTV);
        progBar = (ProgressBar) findViewById(R.id.progressBar2);
        progBar.setVisibility(View.VISIBLE);

        //retrieve all appointments information from server, save information to dish object and add them to ArrayList dishes one by one,
        //showing on appointmentList.
        ParseQuery<ParseObject> query = ParseQuery.getQuery("Appointment");
        appointments = new ArrayList<Appointment>();

        //check if network is available
        if (isNetworkConnected()) {

            query.whereEqualTo("toWho", MainActivity.userName);
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
                            if(isPastReminder(tempAppointment) == true) {
                                appointments.add(tempAppointment);
                            }
                            tempAppointment = new Appointment();
                        }
                        adapter = new AppointmentOldAdapterElder(PastRemindersElderlyActivity.this, appointments);
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

        //By clicking the item, it will change to ViewDIshActivity, specific dish information will be shown on new page.
        appointmentList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                appointment = (Appointment) appointmentList.getAdapter().getItem(i);

                // Return the object to the MainActivity and close this activity
                Intent intent = new Intent(PastRemindersElderlyActivity.this, ViewPastReminderElderActivity.class);

                intent.putExtra("Appointment", appointment);
                startActivityForResult(intent, EDIT_REMINDER_REQUEST);
                // RemindersElderlyActivity.this.finish();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_past_reminders_elderly, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        // if (id == R.id.action_settings) {
        //     return true;
        // }

        switch(item.getItemId())
        {
            //go to add reminder screen for elderly
            case R.id.current:
                Intent intent = new Intent(PastRemindersElderlyActivity.this, RemindersElderlyActivity.class);
                startActivityForResult(intent,CURRENT_REMINDER_REQUEST);
                finish();
                break;
            //go to add reminder screen for elderly
         /*   case R.id.add:
                Intent intent2 = new Intent(PastRemindersElderlyActivity.this, AddRemindersElderlyActivity.class);
                startActivityForResult(intent2, ADD_REMINDER_REQUEST);
                finish();
                break;
                */
            //refesh view
            case R.id.refresh:
                refreshListView();
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
        // final ArrayList<Appointment> appointments2 = new ArrayList<Appointment>();
        appointments.clear();
        progBar.setVisibility(View.VISIBLE);
        noRemindersTV.setText("");

        //check if network is available
        if (isNetworkConnected() == true) {

            query.whereEqualTo("toWho", MainActivity.userName);
            query.findInBackground(new FindCallback<ParseObject>() {
                @Override
                public void done(List<ParseObject> appointmentlist, ParseException e) {

                    if (appointmentlist.size() > 0) {
                        for (int i = 0; i < appointmentlist.size(); i++) {
                            tempAppointment.setTitle(appointmentlist.get(i).getString("title"));
                            tempAppointment.setDescription(appointmentlist.get(i).getString("description"));
                            tempAppointment.setDate(appointmentlist.get(i).getString("date"));
                            tempAppointment.setTime(appointmentlist.get(i).getString("time"));

                            tempAppointment.setToWho(appointmentlist.get(i).getString("toWho"));
                            tempAppointment.setFromWho(appointmentlist.get(i).getString("fromWho"));
                            tempAppointment.setFrequency(appointmentlist.get(i).getString("frequency"));
                            tempAppointment.setCompleted(appointmentlist.get(i).getBoolean("completed"));
                            //check if reminder is in the past
                            if(isPastReminder(tempAppointment) == true) {
                                appointments.add(tempAppointment);
                            }
                            tempAppointment = new Appointment();
                        }

                        adapter2 = new AppointmentOldAdapterElder(PastRemindersElderlyActivity.this, appointments);
                        appointmentList.setAdapter(adapter2);

                        //check if there are reminders
                        if (appointmentList.getAdapter().isEmpty()) {
                            noRemindersTV.setText("no reminders");
                            progBar.setVisibility(View.GONE);

                        } else {
                            noRemindersTV.setText("");
                            progBar.setVisibility(View.GONE);
                        }

                    } else {
                        //Log.d("dish", "Error: " + e.getMessage());
                        noRemindersTV.setText("no reminders");
                        progBar.setVisibility(View.GONE);
                    }

                    progBar.setVisibility(View.GONE);
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

    private boolean isPastReminder(Appointment rem)
    {
        Appointment reminder = rem;

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
}
