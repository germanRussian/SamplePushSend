package com.posco.samplepushsend;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    EditText editText;
    TextView textView;

    private static String CHANNEL_ID2 = "channel2";
    private static String CHANNEL_NAME2 = "Channel2";
    NotificationManager manager;

    static RequestQueue requestQueue;
    static String regId = "dCjMTKOAQumpJHvBtC_eLP:APA91bGLQWw8GQl7WAK7A6N5sTMK0xw5uSOkjTCykn_aSQSb6gsxRXJ5bhKdt6uzzGD_VoO2Gj1fC4vOX5DX83u0inX39xIaULpaFnjnLBMREDIB_1NkrMrltHkJUlDdKbRKGZz1d-Xx";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        editText = findViewById(R.id.editText);
        textView = findViewById(R.id.textView);

        Button button = findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String input = editText.getText().toString();
                send(input);

            }
        });

        if (requestQueue == null) {
            requestQueue = Volley.newRequestQueue(getApplicationContext());
        }

    }

    public void showNoti2() {
        manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        NotificationCompat.Builder builder = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            manager.createNotificationChannel(new NotificationChannel(
                    CHANNEL_ID2, CHANNEL_NAME2, NotificationManager.IMPORTANCE_DEFAULT
            ));

            builder = new NotificationCompat.Builder(this, CHANNEL_ID2);
        } else {
            builder = new NotificationCompat.Builder(this);
        }

        Intent intent = new Intent(this, MainActivity.class);


        PendingIntent pendingIntent = PendingIntent.getActivity(this, 101, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        builder.setContentTitle("content");
        builder.setContentText("알림 메시지입니다.");
        builder.setSmallIcon(android.R.drawable.ic_menu_view);


        builder.setAutoCancel(true);

        builder.setContentIntent(pendingIntent);

        Notification noti = builder.build();

        manager.notify(2, noti);

        NotificationCompat.BigTextStyle style = new NotificationCompat.BigTextStyle();
        style.bigText("많은 글자들입니다");
        style.setBigContentTitle("제목입니다");
        style.setSummaryText("요약 글입니다");

        NotificationCompat.Builder builder2 = new NotificationCompat.Builder(this, "channel3")
                .setContentTitle("알림 제목")
                .setContentText("알림 내용")
                .setSmallIcon(android.R.drawable.ic_menu_send)
                .setStyle(style);

    }


    public void send(String input) {
        JSONObject requestData = new JSONObject();

        try {
            requestData.put("priority", "high");

            JSONObject dataObj = new JSONObject();

            dataObj.put("contents", input);
            requestData.put("data", dataObj);
            JSONArray idArray = new JSONArray();

            idArray.put(0, regId);
            requestData.put("registration_ids", idArray);
        } catch (Exception e) {
            e.printStackTrace();
        }
        sendData(requestData, new SendResponseListener() {

            @Override
            public void onRequestCompleted() {
                println("onRequestCompleted() 호출됨.");
            }

            @Override
            public void onRequestStarted() {
                println("onRequestStarted() 호출됨.");
            }

            @Override
            public void onRequestWithError(VolleyError error) {
                println("onRequestWithError() 호출됨.");
            }
        });
    }


    public interface SendResponseListener {
        public void onRequestStarted();
        public void onRequestCompleted();
        public void onRequestWithError(VolleyError error);
    }

    public void sendData(JSONObject requestData, final SendResponseListener listener) {
        JsonObjectRequest request = new JsonObjectRequest(

                Request.Method.POST, "https://fcm.googleapis.com/fcm/send", requestData, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                listener.onRequestCompleted();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                listener.onRequestWithError(error);
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                return params;
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<String, String>();
                headers.put("Authorization", "key=AAAAg3MRt3A:APA91bEcJ3dn3JMGlxeFstPzP8yo67p87ekbdW5CqxXsfnkxkhi7RxD8ciZLn6zNl9k_qCGxMGhy8zwDlg6TK2IFeVGh1OhsLt72PQ4jjmajWRdftw11Bbqqm-dmfuiumiyj7Dx65uh4");
                return headers;
            }

            @Override
            public String getBodyContentType() {
                return "application/json";
            }
        };

        request.setShouldCache(false);
        listener.onRequestStarted();
        requestQueue.add(request);
    }


    public void println(String data) {
        textView.append(data + "\n");
    }
}
