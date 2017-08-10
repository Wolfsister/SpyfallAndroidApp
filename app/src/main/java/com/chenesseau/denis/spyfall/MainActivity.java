package com.chenesseau.denis.spyfall;

import android.Manifest;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Map;

//TODO: si numéro non rentré sur new Player, player = celui qui a le téléphone.
//TODO: if name == "me", on affiche le role dans app


//TODO: Ajouter lieux depuis activity location view : faire en utilisant tools ?



public class MainActivity extends AppCompatActivity {

    ArrayList<Player> dataPlayers;
    ListView listView;
    Button addNewPlayer;
    TextView twRoleGameMaster;
    private static PlayerAdapter adapter;

    private static final int PERMISSION_REQUEST_CODE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Location.populateLocations();

        twRoleGameMaster = (TextView) findViewById(R.id.twGameMasterRole);

        listView = (ListView) findViewById(R.id.listPlayers);

        dataPlayers = new ArrayList<>();
        dataPlayers.add(new Player("Oleg", "0659409883"));
        dataPlayers.add(new Player("Me", ""));
        dataPlayers.add(new Player("Denis", "0699733478"));
        dataPlayers.add(new Player("Paul", "0659043628"));

//        dataPlayers.add(new Player("Maman", "0699733478"));

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


        // permission to send SMS

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {

            if (checkSelfPermission(Manifest.permission.SEND_SMS)
                    == PackageManager.PERMISSION_DENIED) {

                Log.d("permission", "permission denied to SEND_SMS - requesting it");
                String[] permissions = {Manifest.permission.SEND_SMS};

                requestPermissions(permissions, PERMISSION_REQUEST_CODE);

            }
        }


//        sendRoles();

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

                mBuilder.setView(mView);
                final AlertDialog dialog = mBuilder.create();
                dialog.show();

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


    protected void addPlayer(String name, String phoneNo) {

        phoneNo = phoneNo.replaceAll("\\s+", ""); //remove spaces in phone no
        Player newPlayer = new Player(name, phoneNo);

        //TODO dit que cela ne contient pas alors que si => Utiliser equals, qui compare les valeurs

        if (!dataPlayers.contains(newPlayer)) {

            dataPlayers.add(newPlayer);
        } else {
            Toast.makeText(getApplicationContext(), "Player already in the game",
                    Toast.LENGTH_LONG).show();
        }
    }

    protected void sendRoles() {

//        if (dataPlayers.size()< 3) {
//            Toast.makeText(this, "You must add more players first !", Toast.LENGTH_SHORT).show();
//        }
        Map<String, String> rolesPerContact = Location.setRolesPerContact(dataPlayers);

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
//                Toast.makeText(getApplicationContext(), "Message Sent",
//                        Toast.LENGTH_LONG).show();
                    Log.i("Message", textMessage);
                    Log.i("MessageSent", "Message sent");
                } else {
                    twRoleGameMaster.setText(textMessage);

                }
            } catch (Exception ex) {
//                Toast.makeText(getApplicationContext(), ex.getMessage().toString(),
//                        Toast.LENGTH_LONG).show();
                ex.printStackTrace();
            }

        }

    }

}
