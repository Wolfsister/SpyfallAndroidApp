package com.chenesseau.denis.spyfall;

import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Denis on 31/07/2017.
 */


public class Location {


    public static ArrayList<String> LOCATIONS = new ArrayList<>();


    public static void populateLocations() {
        LOCATIONS.add("Théâtre");
        LOCATIONS.add("Restaurant");
        LOCATIONS.add("Plage");
        LOCATIONS.add("Ecole");
        LOCATIONS.add("Supermarché");
        LOCATIONS.add("Garage Automobile");
        LOCATIONS.add("Cirque");
        LOCATIONS.add("Hôpital");
        LOCATIONS.add("Croisades");
        LOCATIONS.add("Train");
        LOCATIONS.add("Banque");
        LOCATIONS.add("Avion");
        LOCATIONS.add("Commissariat");
        LOCATIONS.add("Bateau Pirate");
        LOCATIONS.add("Studio de Cinéma");
        LOCATIONS.add("Base Militaire");
        LOCATIONS.add("Sous-Marin");
        LOCATIONS.add("Paquebot");
        LOCATIONS.add("Pôle Nord");
        LOCATIONS.add("Hôtel");
        LOCATIONS.add("Fête entreprise");
        LOCATIONS.add("Spa");
        LOCATIONS.add("Laboratoire");
        LOCATIONS.add("Ambassade");
        LOCATIONS.add("Station Spatiale");
        LOCATIONS.add("Casino");
        LOCATIONS.add("Cathédrale");
        LOCATIONS.add("Marché");

    }

    public static ArrayList<String> PICKED_LOCATIONS = new ArrayList<>();

    public static String getRandomLocation() {

        //pass array with already picked location

        // ex ; array[] : alreadyPickedLocation = [ "Avion", "Train"]
        boolean firstTry = true;
        String chosenLocation = "";
        while (firstTry || PICKED_LOCATIONS.contains(chosenLocation)) {

            int randomIndex = (int) Math.floor(Math.random() * LOCATIONS.size());
            chosenLocation = LOCATIONS.get(randomIndex);
            firstTry = false;
        }


        PICKED_LOCATIONS.add(chosenLocation);
        return chosenLocation;

    }

    public static boolean addCustomLocation(String locationName) {

        boolean locationAdded = false;

        if (LOCATIONS.contains(locationName)) {
            LOCATIONS.add(locationName);
            locationAdded = true;

        }

        return locationAdded;
        // faire toast pour prévenir utilisateur si pas ajouté

    }

    // toutes méthodes ici, mais pas forcément à laisser ici

    // -------------------------------- DANS MAIN

    private ArrayList<String> phoneNumbersList = new ArrayList<String>();
    ;

// return une map avec le numéro de téléphone est le contenu du sms
    //Attention, unicité de la key sur map
    public static Map<String, String> setRolesPerContact(ArrayList<Player> listPlayers) {

        Map<String, String> mapNumbersRoles = new HashMap<String, String>();
        int nbPlayers = listPlayers.size();
        Log.i("nbPlayer", Integer.toString(nbPlayers));
        int spyIndex = (int) Math.floor(Math.random()*nbPlayers);
        Log.i("Spy index", Integer.toString(spyIndex));
        String location = Location.getRandomLocation();

        for (int i = 0; i < nbPlayers; i++) {

            Log.d("i in setRoles", Integer.toString(i));
            Player player = listPlayers.get(i);
            String playerName = player.getName();
            String playerPhoneNumber = player.getPhoneNumber();
            Log.d("setRole", playerName);
            if (playerName.trim().toLowerCase().equals("me")) {
                Log.d("inParticularCase", "Yes");
                playerPhoneNumber = "Me";
            }
            Log.d("setRole", playerPhoneNumber);

            Log.d("Player", player.toString());

            if (i == spyIndex) {
                mapNumbersRoles.put(playerPhoneNumber, playerName + ", tu es espion, découvre où tu es !");
            } else {
                mapNumbersRoles.put(playerPhoneNumber, playerName + ", vous êtes ici : " + location +". Démasquez l'espion !");
            }
            Log.d("Map Roles", mapNumbersRoles.toString());
        }

        return mapNumbersRoles;
    }
//
//    public void sendSMS(Map<String, String> numbersAndRoles) {
//
//        try {
//            SmsManager smsManager = SmsManager.getDefault();
//
//            for (Map.Entry<String, String> info : numbersAndRoles.entrySet()) {
//                smsManager.sendTextMessage(info.getKey(), null, info.getValue(), null, null);
//
//            }
//
//            Toast.makeText(getApplicationContext(), "Messages Sent",
//                    Toast.LENGTH_LONG).show();
//
//
//        } catch (Exception ex) {
//            Toast.makeText(getApplicationContext(), ex.getMessage().toString(),
//                    Toast.LENGTH_LONG).show();
//            ex.printStackTrace();
//        }
//
//    }


}




