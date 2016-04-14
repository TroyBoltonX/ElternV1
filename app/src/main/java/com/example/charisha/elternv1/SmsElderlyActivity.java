package com.example.charisha.elternv1;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.telephony.gsm.SmsManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by e-Miracle workers.
 * Class for SMS-ing of a reminder (elder)
 */

public class SmsElderlyActivity extends Activity {

    private Appointment currentAppointment;
    Button btnSendSMS;
    EditText txtPhoneNo;
    EditText txtMessage;
    public static final int PICK_CONTACT = 12;
    private String contactNum;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sms_elderly);
        btnSendSMS = (Button) findViewById(R.id.btnSendSMS);
        txtPhoneNo = (EditText) findViewById(R.id.txtPhoneNo);
        txtMessage = (EditText) findViewById(R.id.txtMessage);

        //get the selected appointment
        Intent i = getIntent();
        currentAppointment = i.getParcelableExtra("Appointment");

        //formated date
        String formattedRemDate = "";
        //formatted time
        String formattedTime = "";

        //put date into date object
        String dateString2 = currentAppointment.getDate()+" "+currentAppointment.getTime()+":"+"00";
        try {
            Date remDate = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss").parse(dateString2);
            SimpleDateFormat sdf = new SimpleDateFormat("EEE, dd-MMM-yyy");
            //time format
            SimpleDateFormat sdf2 = new SimpleDateFormat("hh:mm aa");
            //format date
            formattedRemDate = sdf.format(remDate);
            //format time
            formattedTime = sdf2.format(remDate);
        }
        catch (Exception e){

        }

        //create message
        StringBuilder sb = new StringBuilder();
        sb.append("Reminder from " + currentAppointment.getfromWho() + "." + "\r\n "+
                "Title " + currentAppointment.getTitle() + "." + "\r\n "+
                " Description: " + currentAppointment.getDescription() + "."  + "\r\n "+
                " Date: " + formattedRemDate+ "."  +  "\r\n "+
                " Time :" + formattedTime+ "."  + "\r\n "+
                " For : " + currentAppointment.getToWho());

        // String message = "test";
        String message = sb.toString();
        txtMessage.setText(message);

        Button button = (Button)findViewById(R.id.pickcontact);

        //onclikck lister for contact button
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
                startActivityForResult(intent, PICK_CONTACT);
            }
        });

        //set on click listener for message button
        btnSendSMS.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                String phoneNo = txtPhoneNo.getText().toString().replaceAll("\\s+","");
                String message = txtMessage.getText().toString();
                if (phoneNo.length() > 0 && message.length() > 0 && isNumeric(phoneNo))
                    sendSMS(phoneNo, message);
                else
                    Toast.makeText(getBaseContext(),
                            "Please enter a valid phone number and a message.",
                            Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_sms_elderly, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        ////noinspection SimplifiableIfStatement
        // if (id == R.id.action_settings) {
        ///     return true;
        // }

        return super.onOptionsItemSelected(item);
    }

    //---sends an SMS message to another device---
    private void sendSMS(String phoneNumber, String message)
    {
        //PendingIntent pi = PendingIntent.getActivity(this, 0,
        //        new Intent(this, EditReminderElderActivity.class), 0);
        SmsManager sms = SmsManager.getDefault();
        sms.sendTextMessage(phoneNumber, null, message, null, null);
        Toast.makeText(getBaseContext(),
                "Sms sent",
                Toast.LENGTH_SHORT).show();
        Intent intent = new Intent();
        setResult(RESULT_OK, intent);
        finish();
    }

    @Override public void onActivityResult(int reqCode, int resultCode, Intent data) {
        super.onActivityResult(reqCode, resultCode, data);

        //pick contact from contact list
        switch (reqCode) {
            case (PICK_CONTACT):
                if (resultCode == Activity.RESULT_OK) {
                    Uri contactData = data.getData();
                    Cursor c = managedQuery(contactData, null, null, null, null);
                    if (c.moveToFirst()) {
                        String id = c.getString(c.getColumnIndexOrThrow(ContactsContract.Contacts._ID));

                        String hasPhone =
                                c.getString(c.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER));

                        if (hasPhone.equalsIgnoreCase("1")) {
                            Cursor phones = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,
                                    ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = " + id, null, null);
                            phones.moveToFirst();
                            contactNum = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                            // Toast.makeText(getApplicationContext(), cNumber, Toast.LENGTH_SHORT).show();
                            // setCn(cNumber);

                            txtPhoneNo.setText(contactNum);
                        }
                    }
                }
        }
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

    //check if String is a number
    public static boolean isNumeric(String str)
    {
        try
        {
            double d = Double.parseDouble(str);
        }
        catch(NumberFormatException nfe)
        {
            return false;
        }
        return true;
    }
}
