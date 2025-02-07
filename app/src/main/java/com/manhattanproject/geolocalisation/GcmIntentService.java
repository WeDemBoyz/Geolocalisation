package com.manhattanproject.geolocalisation;

import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

public class GcmIntentService extends IntentService {
    public static final int NOTIFICATION_ID = 1;
    private NotificationManager mNotificationManager;
    NotificationCompat.Builder builder;

    public GcmIntentService() {
        super("GcmIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Bundle extras = intent.getExtras();
        GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(this);
        // The getMessageType() intent parameter must be the intent you received
        // in your BroadcastReceiver.
        String messageType = gcm.getMessageType(intent);
        String TAG = "TAG";

        if (!extras.isEmpty()) {  // has effect of unparcelling Bundle
            /*
             * Filter messages based on message type. Since it is likely that GCM
             * will be extended in the future with new message types, just ignore
             * any message types you're not interested in, or that you don't
             * recognize.
             */
            if (GoogleCloudMessaging.
                    MESSAGE_TYPE_SEND_ERROR.equals(messageType)) {
                sendNotification0("Send error: " + extras.toString());
            } else if (GoogleCloudMessaging.
                    MESSAGE_TYPE_DELETED.equals(messageType)) {
                sendNotification0("Deleted messages on server: " +
                        extras.toString());
                // If it's a regular GCM message, do some work.
            } else if (GoogleCloudMessaging.
                    MESSAGE_TYPE_MESSAGE.equals(messageType)) {
                if  (extras.containsKey("type")) {
                    if (extras.getString("type").equalsIgnoreCase("0")) {
                        registerDemand(extras.getString("pseudo"));
                        sendNotification0(extras.getString("message"));
                    } else if (extras.getString("type").equalsIgnoreCase("1")) {
                        sendNotification1(extras.getString("message"));
                        DataBase db = new DataBase(getApplicationContext(), "base de donne", null, 4);
                        Lieu l = new Lieu(-1, Categorie_lieu.valueOf(extras.getString("cat")), extras.getString("des"), extras.getString("descr"), false, new LatLng(Double.parseDouble(extras.getString("positionx")), Double.parseDouble(extras.getString("positiony"))), true);
                        db.ajoutLieu(l);
                    } else if (extras.getString("type").equalsIgnoreCase("2")) {
                        sendNotification2(extras.getString("message"));
                        System.out.println("rout");
                    }
                }
            }
            Log.i(TAG, "Completed work @ " + SystemClock.elapsedRealtime());
            // Post notification of received message.
            if  (extras.containsKey("type")) {
                if (extras.getString("type").equalsIgnoreCase("0")) {
                    //Incrémenter le compteur de demandes non lues
                    SharedPreferences settings = getSharedPreferences("DemandesNonLues", Context.MODE_PRIVATE);
                    int nb = settings.getInt("nb", 0);
                    SharedPreferences.Editor edit = settings.edit();
                    edit.putInt("nb", nb + 1);
                    edit.apply();
                }
            }
            //affichage test
            Log.i(TAG, "Received: " + extras.toString());
        }
        // Release the wake lock provided by the WakefulBroadcastReceiver.
        GcmBroadcastReceiver.completeWakefulIntent(intent);
    }


    private void registerDemand(String pseudo){
        int ami = -1;
        int user = -1;
        final String[] params={"selectUser.php","pseudo",pseudo};
        Requete r = new Requete();
        r.execute(params);
        try {
            r.get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        String response=r.getResult();
        try{
            JSONArray jArray = new JSONArray(response);
            System.out.println("Donnée de la réponse : "+jArray);
            JSONObject json_data = jArray.getJSONObject(0);
            ami=json_data.getInt("id");
        }catch(JSONException e){
            Log.e("log_tag", "Error parsing data " + e.toString());
        }
        Utilisateur courant = new Utilisateur();
        courant.recup(getApplicationContext());
        final String[] p={"selectUser.php","pseudo",courant.getPseudo()};
        r = new Requete();
        r.execute(p);
        try {
            r.get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        response=r.getResult();
        try{
            JSONArray jArray = new JSONArray(response);
            System.out.println("Donnée de la réponse : "+jArray);
            JSONObject json_data = jArray.getJSONObject(0);
            user=json_data.getInt("id");
        }catch(JSONException e){
            Log.e("log_tag", "Error parsing data " + e.toString());
        }
        System.out.println("ami : "+ami+"   user : "+user);
        final String[] pa={"newDemand.php","iduser",Integer.toString(user),"idami",Integer.toString(ami)};
        r = new Requete();
        r.execute(pa);
    }
    // Put the message into a notification and post it.
    // This is just one simple example of what you might choose to do with
    // a GCM message.
    private void sendNotification0(String msg) {
        mNotificationManager = (NotificationManager)
                this.getSystemService(Context.NOTIFICATION_SERVICE);

        PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
                new Intent(this, Activity_list_utilisateur.class), 0);

        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.drawable.ic_launcher)
                        .setContentTitle("Manhattan Project")
                        .setAutoCancel(true)
                        .setStyle(new NotificationCompat.BigTextStyle()
                                .bigText(msg))
                        .setContentText(msg);

        mBuilder.setContentIntent(contentIntent);
        mNotificationManager.notify(NOTIFICATION_ID, mBuilder.build());
    }

    private void sendNotification1(String msg) {
        mNotificationManager = (NotificationManager)
                this.getSystemService(Context.NOTIFICATION_SERVICE);

        PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
                new Intent(this, Activity_Demandes_Lieux.class), 0);

        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.drawable.ic_launcher)
                        .setContentTitle("Manhattan Project")
                        .setAutoCancel(true)
                        .setStyle(new NotificationCompat.BigTextStyle()
                                .bigText(msg))
                        .setContentText(msg);

        mBuilder.setContentIntent(contentIntent);
        mNotificationManager.notify(NOTIFICATION_ID, mBuilder.build());
    }


    private void sendNotification2(String msg) {
        mNotificationManager = (NotificationManager)
                this.getSystemService(Context.NOTIFICATION_SERVICE);

        PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
                new Intent(this, AcceptRefuseLocalisation.class), 0);

        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.drawable.ic_launcher)
                        .setContentTitle("Manhattan Project")
                        .setAutoCancel(true)
                        .setStyle(new NotificationCompat.BigTextStyle()
                                .bigText(msg))
                        .setContentText(msg);

        mBuilder.setContentIntent(contentIntent);
        mNotificationManager.notify(NOTIFICATION_ID, mBuilder.build());
    }
}