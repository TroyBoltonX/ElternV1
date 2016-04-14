package com.example.charisha.elternv1;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
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
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

/**
 * Created by e-Miracle workers.
 * Class for user registration
 */

public class RegisterActivity extends Activity implements AdapterView.OnItemSelectedListener{

    String userId;
    String password;
    String userType;
    String passwordQuestion;
    String passwordAnswer;
    private EditText userNameET;
    private EditText passwordET;
    private EditText questionET;
    private EditText answerET;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        //spinner to show the drop down menu for user type where user choses if ther are registering a parent elder
        Spinner dropdown = (Spinner)findViewById(R.id.spinnerUserType);
        String[] items = new String[]{"Elderly", "Carer"};
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, items);
        dropdown.setAdapter(adapter);
        dropdown.setOnItemSelectedListener(this);

        userNameET = (EditText) findViewById(R.id.userNameET);
        passwordET = (EditText) findViewById(R.id.passwordET);
        questionET = (EditText) findViewById(R.id.questionET);
        answerET = (EditText) findViewById(R.id.answerET);
    }

    /*
    listener for the drop down menu (spinner) to get user type
     */
    public void onItemSelected(AdapterView<?> parent, View view,
                               int pos, long id) {

        //retrive the user's selection of usertype
        userType = parent.getItemAtPosition(pos).toString();
    }

    /*
    what to do if nothing is chosen on drop down menu
     */
    public void onNothingSelected(AdapterView<?> parent) {
        // Another interface callback
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        //make keyboard disappear
        InputMethodManager imm = (InputMethodManager)getSystemService(Context.
                INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_register, menu);
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
        //  }

        return super.onOptionsItemSelected(item);
    }

    /*
    Method that runs when register button is pressed
    Saves user registration data to database
     */
    public void saveUserToDatabase(View v) {

        //check if network is available
        if (isNetworkConnected()) {

            //ensure user has entered a all fields
            if (userNameET.getText().toString().isEmpty() || userNameET.getText().toString().equals("") ||
                    passwordET.getText().toString().isEmpty() || passwordET.getText().toString().equals("") ||
                    questionET.getText().toString().isEmpty() || questionET.getText().toString().equals("") ||
                    answerET.getText().toString().isEmpty() || answerET.getText().toString().equals("")) {

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
                //check if the username is atleast 6 characters long
                if (userNameET.getText().toString().length() < 6 || userNameET.getText().toString().length() > 20) {
                    // Use the AlertDialog Builder utility to create the dialog
                    AlertDialog.Builder builder = new AlertDialog.Builder(this);
// Set message and title for dialog (we can chain methods here!)
                    builder.setTitle("Invalid input!").setMessage("Username should be 6 - 20 characters long");
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
                    //check if the password is atleast 6 characters long
                    if (passwordET.getText().toString().length() < 6 || passwordET.getText().toString().length() > 20) {
                        // Use the AlertDialog Builder utility to create the dialog
                        AlertDialog.Builder builder = new AlertDialog.Builder(this);
// Set message and title for dialog (we can chain methods here!)
                        builder.setTitle("Invalid input!").setMessage("Password should 6 - 20 characters long");
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

                        ParseQuery<ParseObject> query = ParseQuery.getQuery("Elderly");
                        query.whereEqualTo("userName", userNameET.getText().toString());
                        query.getFirstInBackground(new GetCallback<ParseObject>() {
                            public void done(ParseObject object, ParseException e) {

                                if (object == null) {
                                    ParseQuery<ParseObject> query = ParseQuery.getQuery("Carer");
                                    query.whereEqualTo("userName", userNameET.getText().toString());
                                    query.getFirstInBackground(new GetCallback<ParseObject>() {
                                        public void done(ParseObject object, ParseException e) {
                                            if (object == null) {
                                                if (userType.equals("Elderly")) {
                                                    ParseObject elderly = new ParseObject("Elderly");
                                                    elderly.put("userName", userNameET.getText().toString());
                                                    // elderly.put("password", passwordET.getText().toString());
                                                    elderly.put("question", questionET.getText().toString());
                                                    elderly.put("answer", answerET.getText().toString());
                                                    elderly.put("userType", userType);

                                                    // Encrypt Password
                                                    String password = passwordET.getText().toString();

                                                    Encryption encryption = new Encryption();
                                                    String hashPassword = encryption.hashPassword(password);

                                                    elderly.put("password", hashPassword);

                                                    elderly.saveInBackground();
                                                    Toast.makeText(getBaseContext(), "User Account Created",
                                                            Toast.LENGTH_SHORT).show();
                                                    finish();
                                                } else {
                                                    //change attribute value of the object to the value input by user.
                                                    ParseObject carer = new ParseObject("Carer");
                                                    carer.put("userName", userNameET.getText().toString());
                                                    // carer.put("password", passwordET.getText().toString());
                                                    carer.put("question", questionET.getText().toString());
                                                    carer.put("answer", answerET.getText().toString());
                                                    carer.put("userType", userType);

                                                    // Encrypt Password
                                                    String password = passwordET.getText().toString();

                                                    Encryption encryption = new Encryption();
                                                    String hashPassword = encryption.hashPassword(password);

                                                    carer.put("password", hashPassword);

                                                    carer.saveInBackground();
                                                    Toast.makeText(getBaseContext(), "User Account Created",
                                                            Toast.LENGTH_SHORT).show();
                                                    finish();
                                                }
                                            } else {
                                                //pop up to hint name is already registered
                                                AlertDialog.Builder builder = new AlertDialog.Builder(RegisterActivity.this);
                                                // Set message and title for dialog (we can chain methods here!)
                                                builder.setTitle("Username already exists!").setMessage("Please enter a different username");
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
                                    });
                                } else {
                                    //pop up to hint name is already registered
                                    //pop up to hint name is already registered
                                    AlertDialog.Builder builder = new AlertDialog.Builder(RegisterActivity.this);
                                    // Set message and title for dialog (we can chain methods here!)
                                    builder.setTitle("Username already exists!").setMessage("Please enter a different username");
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
                        });
                    }
                }
            }
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
    }

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
}
