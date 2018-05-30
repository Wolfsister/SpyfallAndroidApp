package com.chenesseau.denis.spyfall;

import android.Manifest;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Map;

//TODO: Ajouter lieux depuis activity location view : faire en utilisant tools ?

public class MainActivity extends AppCompatActivity {

    ArrayList<Player> dataPlayers;
    ListView listView;
    Button addNewPlayer;
    TextView twRoleGameMaster;
    private static PlayerAdapter adapter;

    private static String roleGameMasterMessage = "You haven't launched a party yet.";
    private static String notClickedRoleGameMasterMessage = "Click to see your role ! (don't forget to launch a game first ;) )";


    private static final int PERMISSION_REQUEST_CODE = 1;

    private static final int MY_PERMISSIONS_REQUEST_READ_CONTACTS = 1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Location.populateLocations();

        twRoleGameMaster = (TextView) findViewById(R.id.twGameMasterRole);
        twRoleGameMaster.setText(notClickedRoleGameMasterMessage);

        twRoleGameMaster.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                TextView twRoleGameMaster = (TextView) v.findViewById(R.id.twGameMasterRole);
                String oldText = twRoleGameMaster.getText().toString();

                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    // Pressed
                    twRoleGameMaster.setText(MainActivity.roleGameMasterMessage);

                } else if (event.getAction() == MotionEvent.ACTION_UP) {
                    // Released
                    twRoleGameMaster.setText(MainActivity.notClickedRoleGameMasterMessage);
                }
                return true;

            }
        });

        listView = (ListView) findViewById(R.id.listPlayers);

        dataPlayers = new ArrayList<>();
        dataPlayers.add(new Player("Me", ""));

        Button viewLocationButton = (Button) findViewById(R.id.viewLocationsButton);
        viewLocationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                ArrayList<String> locationArrayList = Location.LOCATIONS;
                Intent intentListLocations = new Intent(MainActivity.this, LocationsList.class);
                intentListLocations.putExtra("Locations_list", locationArrayList);

                startActivity(intentListLocations);


            }
        });


        Button buttonStartTheGame = (Button) findViewById(R.id.buttonStartGame);
        buttonStartTheGame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendRoles();
            }
        });

        Button buttonStartTheGameSpys = (Button) findViewById(R.id.buttonStartGameSpys);
        buttonStartTheGameSpys.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendRolesSpy();
            }
        });

        Button buttonStartTheGameDifferentLocations = (Button) findViewById(R.id.buttonStartOnlyLocations);
        buttonStartTheGameDifferentLocations.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendLocationToEverybody();
            }
        });


        // permission to send SMS

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {

            if (checkSelfPermission(Manifest.permission.SEND_SMS)
                    == PackageManager.PERMISSION_DENIED) {

                Log.d("permission", "permission denied to SEND_SMS - requesting it");
                String[] permissions = {Manifest.permission.SEND_SMS};

                requestPermissions(permissions, PERMISSION_REQUEST_CODE);

            }

            if (checkSelfPermission(Manifest.permission.READ_CONTACTS)
                    == PackageManager.PERMISSION_DENIED) {

                Log.d("permission", "permission denied to READ_CONTACTS - requesting it");
                String[] permissions = {Manifest.permission.READ_CONTACTS};

                requestPermissions(permissions, PERMISSION_REQUEST_CODE);

            }
        }

        adapter = new PlayerAdapter(dataPlayers, getApplicationContext());

        listView.setAdapter(adapter);

        addNewPlayer = (Button) findViewById(R.id.addPlayerButton);

        addNewPlayer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                AlertDialog.Builder mBuilder = new AlertDialog.Builder(MainActivity.this);
                View mView = getLayoutInflater().inflate(R.layout.dialog_add_player, null);
                final EditText nameNewPlayer = (EditText) mView.findViewById(R.id.nameAddPlayerEt);
                final EditText phoneNewPlayer = (EditText) mView.findViewById(R.id.phoneAddPlayerEt);
                Button buttonAddPlayerDialog = (Button) mView.findViewById(R.id.buttonAddPlayerOnDialog);
                Button selectFromContacts = (Button) mView.findViewById(R.id.addFromContactsPhone);

                mBuilder.setView(mView);
                final AlertDialog dialog = mBuilder.create();
                dialog.show();

                selectFromContacts.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent i = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
                        startActivityForResult(i, 1001);
                        dialog.dismiss();
                    }

                });

                buttonAddPlayerDialog.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String nameNewPlayerString = nameNewPlayer.getText().toString();
                        String phoneNewPlayerString = phoneNewPlayer.getText().toString();

                        if ((!nameNewPlayerString.isEmpty() && !phoneNewPlayerString.isEmpty()) || nameNewPlayerString.trim().toLowerCase().equals("me") ) {
                            Toast.makeText(MainActivity.this, "Player added", Toast.LENGTH_SHORT).show();

                            if( nameNewPlayerString.trim().toLowerCase().equals("me")) {
                                addPlayer("Me", "");
                            } else {
                                addPlayer(nameNewPlayerString, phoneNewPlayerString);
                            }
                            dialog.dismiss();


                        } else {
                            Toast.makeText(MainActivity.this, "Please fill all the fields", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch(requestCode) {
            case 1001:

                if (resultCode == Activity.RESULT_OK) {
                    if (ContextCompat.checkSelfPermission(this,
                            Manifest.permission.READ_CONTACTS)
                            == PackageManager.PERMISSION_GRANTED) {

                        Cursor cursor = null;

                        try {
                            String contactName = null ;
                            String name = null;
                            Uri uri = data.getData();
                            cursor = getContentResolver().query(uri, null, null, null, null);
                            cursor.moveToFirst();
                            int phoneIndex =cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME);
                            contactName = cursor.getString(phoneIndex);
                            contactName = contactName.split(" ")[0]; // Get only the first name of contact.

                            Cursor contactCursor = getContentResolver().query(uri,
                                    new String[] { ContactsContract.Contacts._ID }, null, null,
                                    null);
                            String id = null;
                            if (contactCursor.moveToFirst()) {
                                id = contactCursor.getString(contactCursor
                                        .getColumnIndex(ContactsContract.Contacts._ID));
                            }
                            contactCursor.close();
                            String phoneNumber = null;
                            Cursor phoneCursor = getContentResolver().query(
                                    ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                                    new String[] { ContactsContract.CommonDataKinds.Phone.NUMBER },
                                    ContactsContract.CommonDataKinds.Phone.CONTACT_ID + "= ? ",
                                    new String[] { id }, null);
                            if (phoneCursor.moveToFirst()) {
                                phoneNumber = phoneCursor
                                        .getString(phoneCursor
                                                .getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                            }
                            phoneCursor.close();

                            Log.e("MainActivity", "Contact fetched");
                            Log.e("MainActivity", contactName);
                            Log.e("MainActivity", phoneNumber);


                            if (!phoneNumber.isEmpty() && !contactName.isEmpty()) {
                                addPlayer(contactName, phoneNumber);
                            } else {
                                Toast.makeText(MainActivity.this, "The player couldn't be added. Check you contact's infos.", Toast.LENGTH_LONG).show();
                            }

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        break;
                    }
                } else {
                    Log.e("MainActivity", "Failed to pick contact");
                }

            default:
                break;

        }
    }

    protected void addPlayer(String name, String phoneNo) {

        phoneNo = phoneNo.replaceAll("\\s+", ""); //remove spaces in phone no
        Player newPlayer = new Player(name, phoneNo);

        //TODO dit que cela ne contient pas alors que si => Utiliser equals, qui compare les valeurs

        if (!dataPlayers.contains(newPlayer)) {

            dataPlayers.add(newPlayer);
            Log.d("AddPlayer", "Player added");
            Log.d("ListPlayers", dataPlayers.toString());
            adapter.notifyDataSetChanged();
        } else {
            Toast.makeText(getApplicationContext(), "Player already in the game",
                    Toast.LENGTH_LONG).show();
        }
    }

    protected void sendSMS(Map<String, String> rolesPerContact) {


        Log.i("rolespercontact", rolesPerContact.toString());
        int cmp = 0;
        for (Map.Entry<String, String> entry : rolesPerContact.entrySet()) {
            cmp++;
            Log.i("cmp", Integer.toString(cmp));
            String phoneNumber = entry.getKey();
            String textMessage = entry.getValue();

            Log.i("phoneNumber", phoneNumber);

            SmsManager smsManager = SmsManager.getDefault();

            try {

                if(!phoneNumber.equals("Me")) {
                    smsManager.sendTextMessage(phoneNumber, null, textMessage, null, null);
                    Log.i("Message", textMessage);
                    Log.i("MessageSent", "Message sent");
                } else {

                    this.roleGameMasterMessage = textMessage;
                    Log.i("Message", textMessage);

                    twRoleGameMaster.setText(notClickedRoleGameMasterMessage);

                }
            } catch (Exception ex) {
                Log.d("ExceptionSending", ex.getMessage().toString());
                ex.printStackTrace();
            }

        }
    }

    protected void sendRoles() {

        Map<String, String> rolesPerContact = Location.setRolesPerContact(dataPlayers);
        sendSMS(rolesPerContact);

    }

    protected void sendRolesSpy() {

        Map<String, String> rolesPerContact = Location.setSpyRoleContact(dataPlayers);
        sendSMS(rolesPerContact);
    }

    protected void sendLocationToEverybody() {

        Map<String, String> rolesPerContact = Location.setDifferentLocationPerContact(dataPlayers);
        sendSMS(rolesPerContact);
    }
}
