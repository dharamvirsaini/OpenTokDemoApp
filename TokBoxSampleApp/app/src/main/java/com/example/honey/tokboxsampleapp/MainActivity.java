package com.example.honey.tokboxsampleapp;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.opentok.OpenTok;
import com.opentok.android.Connection;
import com.opentok.android.OpentokError;
import com.opentok.android.Publisher;
import com.opentok.android.PublisherKit;
import com.opentok.android.Session;
import com.opentok.android.Stream;
import com.opentok.android.Subscriber;
import com.opentok.android.SubscriberKit;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;

public class MainActivity extends AppCompatActivity implements Session.SessionListener,
        Publisher.PublisherListener, Subscriber.SubscriberListener,
        Subscriber.VideoListener, Session.ArchiveListener, Session.SignalListener {

    public static final int API_KEY = 45644842;
   // public static final String SECRET_KEY = "77a2b6bc6c63ceb507e64e86b5f854d1642048a0";

    private static  String SESSION_ID;
    private static Session session1;
   // public static final String TOKEN = "T1==cGFydG5lcl9pZD00NTY0NDg0MiZzaWc9ZDlkYzljODM5NTMyOTk0NzVjZjUzYmNkOTMxMzYwNGE4OWU4Mzc3OTpzZXNzaW9uX2lkPTFfTVg0ME5UWTBORGcwTW41LU1UUTNNamN6TURRek56a3hOSDVvYWxCb05ISkVSMHRJVEUxMmVFeDJTR2wzUTAxWVFWSi1mZyZjcmVhdGVfdGltZT0xNDcyNzMwNDcwJm5vbmNlPTAuMTg3MDk1MDU2NzUzNjA1NiZyb2xlPXB1Ymxpc2hlciZleHBpcmVfdGltZT0xNDczMzM1MjY5";
    public static final String LOGTAG = "MainActivity.class";

    private LinearLayout publisherView;
    private LinearLayout.LayoutParams publisherParams;
    private LinearLayout subscriberView;
    private LinearLayout.LayoutParams subscriberParams;
    private static String mArchiveID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.i(LOGTAG, "call to onCreate");
        super.onCreate(savedInstanceState);


        setContentView(R.layout.activity_main);
        LinearLayout parentLayout = (LinearLayout) findViewById(R.id.mainParent);

        subscriberView = new LinearLayout(this);
        subscriberParams = new LinearLayout.LayoutParams
                (LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        subscriberParams.weight = 0.5f;
        subscriberView.setLayoutParams(subscriberParams);

        Button startArchiving = (Button)findViewById(R.id.start);

        startArchiving.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        postJSONObjectArchive("http://192.168.43.27:8080/OpenTokServerAPI/rest/session/startArchive/" + SESSION_ID, null, "start");
                    }
                }).start();


            }
        });

        Button stopArchiving = (Button)findViewById(R.id.stop);

        stopArchiving.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        postJSONObjectArchive("http://192.168.43.27:8080/OpenTokServerAPI/rest/session/stopArchive/" + mArchiveID, null, "stop");
                    }
                }).start();


            }
        });

        publisherView = new LinearLayout(this);
        publisherParams = new LinearLayout.LayoutParams
                (LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        publisherParams.weight = 0.5f;
        publisherView.setLayoutParams(publisherParams);


        parentLayout.setWeightSum(1f);
        parentLayout.addView(publisherView);
        parentLayout.addView(subscriberView);

        new Thread(new Runnable() {
            @Override
            public void run() {
               // try {
                    final JSONObject jsonObject = new JSONObject();
                 JSONObject response = getSessionParams("http://192.168.43.27:8080/OpenTokServerAPI/rest/session", jsonObject);

            try {
                    SESSION_ID = response.getString("Session");
                    session1 = new Session(MainActivity.this, Integer.toString(API_KEY), SESSION_ID);
                    session1.setSessionListener(MainActivity.this);
                    session1.setArchiveListener(MainActivity.this);
                    session1.setSignalListener(MainActivity.this);
                   session1.connect(response.getString("token"));
                } catch (JSONException e) {
                   e.printStackTrace();
                }
            }
        }).start();




    }

    @Override
    public void onConnected(Session session) {
        Log.i(LOGTAG, "call to onConnected of the SessionListener");
        Publisher publisher = new Publisher(MainActivity.this);
        publisher.setPublisherListener(this);
       // publisher.getCapturer().startCapture();
        //publisher.setPublisherVideoType(PublisherKit.PublisherKitVideoType.PublisherKitVideoTypeScreen);
        publisherView.addView(publisher.getView(), publisherParams);
        session.publish(publisher);
        session1.sendSignal("", "Hello, Signaling!");
    }

    @Override
    public void onStreamReceived(Session session, Stream stream) {
        Log.i(LOGTAG, "call to onStreamReceived");
        Subscriber subscriber = new Subscriber(MainActivity.this, stream);
        subscriber.setVideoListener(this);
        session.subscribe(subscriber);
        subscriberView.addView(subscriber.getView(), subscriberParams);
    }

    @Override
    public void onDisconnected(Session session) {
        Log.i(LOGTAG, "call to onDisconnected of the SessionListener");
    }

    @Override
    public void onStreamDropped(Session session, Stream stream) {
        Log.i(LOGTAG, "call to onStreamDropped of the SessionListener");
    }

    @Override
    public void onError(Session session, OpentokError error) {
        Log.i(LOGTAG, "SessionListener error: " + error.getMessage());
    }

    @Override
    public void onStreamCreated(PublisherKit publisher, Stream stream) {

        Log.i(LOGTAG, "call to onStreamCreated of the PublisherListener");
    }

    @Override
    public void onStreamDestroyed(PublisherKit publisher, Stream stream) {
        Log.i(LOGTAG, "call to onStreamDestroyed of the PublisherListener");
    }

    @Override
    public void onError(PublisherKit publisher, OpentokError error) {
        Log.i(LOGTAG, "PublisherListener error: " + error.getMessage());
    }

    @Override
    public void onConnected(SubscriberKit subscriber) {
        Log.i(LOGTAG, "call to onConnected of the SubscriberListener");
    }

    @Override
    public void onDisconnected(SubscriberKit subscriber) {
        Log.i(LOGTAG, "call to onDisconnected of the SubscriberListener");
    }

    @Override
    public void onError(SubscriberKit subscriber, OpentokError error) {
        Log.i(LOGTAG, "SubscriberListener error: " + error.getMessage());
    }

    @Override
    public void onVideoDataReceived(SubscriberKit subscriber) {
        Log.i(LOGTAG, "call to onVideoDataReceived of the VideoListener");
    }

    @Override
    public void onVideoDisabled(SubscriberKit subscriber, java.lang.String reason) {
        Log.i(LOGTAG, "call to onVideoDisabled of the VideoListener");
    }

    @Override
    public void onVideoEnabled(SubscriberKit subscriber, java.lang.String reason) {
        Log.i(LOGTAG, "call to onVideoEnabled of the VideoListener");
    }

    @Override
    public void onVideoDisableWarning(SubscriberKit subscriber) {
        Log.i(LOGTAG, "call to onVideoDisableWarning of the VideoListener");
    }

    @Override
    public void onVideoDisableWarningLifted(SubscriberKit subscriber) {
        Log.i(LOGTAG, "call to onVideoDisableWarning of the VideoListener");
    }

    public static JSONObject postJSONObjectArchive(String completeUrl, JSONObject jsonObject, String type)
    {
        DataOutputStream dataOutputStream;
        InputStream is;
        JSONObject jsonObject1= null;
        String result;

        try{

            URL url = new URL(completeUrl);
            HttpURLConnection httpURLConnection=(HttpURLConnection)url.openConnection();
            httpURLConnection.setRequestMethod("GET");
            httpURLConnection.setConnectTimeout(15000);
            httpURLConnection.setReadTimeout(15000);
            httpURLConnection.setRequestProperty("Content-Type", "application/json");
            httpURLConnection.setRequestProperty("X-Requested-With", "XMLHttpRequest");

            BufferedReader br = new BufferedReader(new InputStreamReader(httpURLConnection.getInputStream(),"UTF-8"));

            String line = null;
            StringBuilder sb = new StringBuilder();

            while ((line = br.readLine()) != null) {
                sb.append(line);
            }

            br.close();
            result = sb.toString();
            System.out.println("response is " + result);

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return jsonObject1;
    }

    public static JSONObject getSessionParams(String completeUrl, JSONObject jsonObject)
    {
        DataOutputStream dataOutputStream;
        InputStream is;
        JSONObject jsonObject1= null;
        String result;

        try{
            URL url = new URL(completeUrl);
            HttpURLConnection httpURLConnection=(HttpURLConnection)url.openConnection();
            httpURLConnection.setRequestMethod("GET");
            httpURLConnection.setConnectTimeout(15000);
            httpURLConnection.setReadTimeout(15000);
            httpURLConnection.setRequestProperty("Content-Type", "application/json");
            httpURLConnection.setRequestProperty("X-Requested-With", "XMLHttpRequest");
            BufferedReader br = new BufferedReader(new InputStreamReader(httpURLConnection.getInputStream(),"UTF-8"));

            String line = null;
            StringBuilder sb = new StringBuilder();

            while ((line = br.readLine()) != null) {
                sb.append(line);
            }

            br.close();
            result = sb.toString();

            jsonObject1 = new JSONObject(result);
            System.out.println("response is " + result);

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return jsonObject1;
    }


    @Override
    public void onArchiveStarted(Session session, String s, String s1) {
        System.out.println("Archive Id is " + s);
    }

    @Override
    public void onArchiveStopped(Session session, String s) {

    }

    @Override
    public void onSignalReceived(Session session, String s, String s1, Connection connection) {
        Toast toast = Toast.makeText(this, s1, Toast.LENGTH_LONG);
        toast.show();
    }
}
