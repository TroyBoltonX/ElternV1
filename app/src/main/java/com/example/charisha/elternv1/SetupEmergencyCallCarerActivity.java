package com.example.charisha.elternv1;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by e-Miracle workers.
 * The class to set up emergency call for carer
 */

public class SetupEmergencyCallCarerActivity extends Activity implements AdapterView.OnItemSelectedListener{
    private ArrayList<String> items = new ArrayList<String>();
    private Spinner dropdown;
    private EditText phoneNum;
    private String elderUsername;
    //indicates if elder already has a emergency number assigned
    private Boolean hasEmNum;
    private String num;
    public static final int PICK_CONTACT = 12;
    private String contactNum;
    private String numberString;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup_emergency_call_carer);

        dropdown = (Spinner)findViewById(R.id.spinner);
        phoneNum = (EditText)findViewById(R.id.phoneET);

        Button button = (Button)findViewById(R.id.pickcontact);

        //onclikck lister for contact button
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
                startActivityForResult(intent, PICK_CONTACT);
            }
        });

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

                    if (items.size() == 0) {
                        // Use the AlertDialog Builder utility to create the dialog
                        AlertDialog.Builder builder = new AlertDialog.Builder(SetupEmergencyCallCarerActivity.this);
// Set message and title for dialog (we can chain methods here!)
                        builder.setTitle("No Elder Added!").setMessage("You don't have an elder added. An elder has to be added " +
                                "to set appointments. Please add an elder");
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

        //retrieve the user's selection of usertype
        elderUsername = parent.getItemAtPosition(pos).toString();

    }
    public void onNothingSelected(AdapterView<?> parent) {
        // Another interface callback
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_setup_emergency_call_carer, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
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
    Method executed when setup button is pressed
     */
    public void setEmergencyNum(View v) {
        numberString = phoneNum.getText().toString().replaceAll("\\s+", "");

        num = "";
        //check if network is available
        if (isNetworkConnected()) {

            //retrieve elder user from database
            // if (MainActivity.userType.equals("elderly")) {
            ParseQuery<ParseObject> query = ParseQuery.getQuery("Elderly");
            query.whereEqualTo("userName", elderUsername);
            query.getFirstInBackground(new GetCallback<ParseObject>() {
                public void done(ParseObject object, ParseException e) {
                    if (object == null) {

                    } else {
                        //check if number has already been assigned
                        //if a number has already been assigned
                        if (object.has("emergency"))
                        {
                            AlertDialog.Builder builder2 = new AlertDialog.Builder(SetupEmergencyCallCarerActivity.this);
                            builder2.setTitle("Emegency number exists!");
                            builder2.setMessage("Do you wish to replace the exisiting emegency number for this elder?");
                            builder2.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {

                                    //retrieve elder object
                                    ParseQuery<ParseObject> query2 = ParseQuery.getQuery("Elderly");
                                    query2.whereEqualTo("userName", elderUsername);
                                    query2.getFirstInBackground(new GetCallback<ParseObject>() {
                                        public void done(ParseObject object, ParseException e) {
                                            if (object == null) {
                                                Log.d("score", "The getFirst request failed.");

                                            } else {
                                                //check if number is numeric
                                                if (isNumeric(numberString))

                                                {
                                                    //add emergency number
                                                    // String answer1 = object.get("answer").toString();
                                                    if (numberString.isEmpty() == false &&
                                                            numberString.length() > 2 &&
                                                            numberString.length() < 30) {
                                                        object.put("emergency", numberString);

                                                        object.saveInBackground();
                                                        Toast.makeText(getBaseContext(), "Emergency number saved",
                                                                Toast.LENGTH_SHORT).show();
                                                        finish();

                                                    } else {

                                                        // Use the AlertDialog Builder utility to create the dialog
                                                        AlertDialog.Builder builder = new AlertDialog.Builder(SetupEmergencyCallCarerActivity.this);
                                                        // Set message and title for dialog (we can chain methods here!)
                                                        builder.setTitle("Invalid phone number").setMessage("Please check number");
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
                                                    AlertDialog.Builder builder = new AlertDialog.Builder(SetupEmergencyCallCarerActivity.this);
                                                    // Set message and title for dialog (we can chain methods here!)
                                                    builder.setTitle("Invalid phone number").setMessage("Please check number");
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
                                    });
                                }
                            });
                            //if changes have been made to appointment and uses choses not to save appointment
                            builder2.setNegativeButton("No", new
                                    DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            finish();
                                        }
                                    });
                            builder2.create().show();
                        }

                        //if number has not already been assigned
                        else
                        {
                            //retrieve elder object
                            ParseQuery<ParseObject> query2 = ParseQuery.getQuery("Elderly");
                            query2.whereEqualTo("userName", elderUsername);
                            query2.getFirstInBackground(new GetCallback<ParseObject>() {
                                public void done(ParseObject object, ParseException e) {
                                    if (object == null) {
                                        Log.d("score", "The getFirst request failed.");

                                    } else {
                                        //check if number is numeric
                                        if (isNumeric(numberString))
                                        {
                                            //add emergency number
                                            // String answer1 = object.get("answer").toString();
                                            if (numberString.isEmpty() == false &&
                                                  numberString.length() > 2 &&
                                                  numberString.length() < 30) {
                                                object.put("emergency", numberString);

                                                object.saveInBackground();
                                                Toast.makeText(getBaseContext(), "Emergency number saved",
                                                        Toast.LENGTH_SHORT).show();
                                                finish();
                                            } else {

                                                // Use the AlertDialog Builder utility to create the dialog
                                                AlertDialog.Builder builder = new AlertDialog.Builder(SetupEmergencyCallCarerActivity.this);
                                                // Set message and title for dialog (we can chain methods here!)
                                                builder.setTitle("Invalid phone number").setMessage("Please check number");
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
                                            AlertDialog.Builder builder = new AlertDialog.Builder(SetupEmergencyCallCarerActivity.this);
                                            // Set message and title for dialog (we can chain methods here!)
                                            builder.setTitle("Invalid phone number").setMessage("Please check number");
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
                            });
                        }
                    }
                }
            });
        }
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

                            phoneNum.setText(contactNum);
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
