package com.dxnnx.soytec.mainactivity;

import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.EditText;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import dxnnx.com.soytec.Global;
import dxnnx.com.soytec.R;

/**
 * Created by DXNNX on 9/18/17.
 */

public class mensajeria extends AppCompatActivity {
    RecyclerView rv;
    EditText mensaje;
    String Usuario;
    boolean admin = false;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activitymensajes);
        rv = (RecyclerView)findViewById(R.id.rvmensajes);
        mensaje = (EditText)findViewById(R.id.mensajePosible);
        rv.setHasFixedSize(true);
        LinearLayoutManager llm = new LinearLayoutManager(this);
        rv.setLayoutManager(llm);
        if(getIntent().getBooleanExtra("admin",false)){
            Usuario =getIntent().getStringExtra("user");
            admin = true;
        }else{
            Usuario = Global.user;
            admin = false;
        }
        try {
            genCardViews(new AsyncCV().execute(Usuario).get());
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
    }

    public  void enviar(View v) throws ExecutionException, InterruptedException {
        if(mensaje.getText().toString().length()!=0){
            new enviarAsync().execute(Usuario,mensaje.getText().toString(),(Usuario.equals(Global.user))?"0":"1");
            genCardViews((new AsyncCV().execute(Usuario,(Usuario.equals(Global.user))?"1":"0").get()));
            mensaje.setText("");
        }
    }

    public void genCardViews(String query)
    {
        try {
        List<Mensaje> mensajes= new ArrayList<>();
        JSONArray jsonarray = null;
            jsonarray = new JSONArray(query);
            for (int i = 0; i < jsonarray.length(); i++) {
                JSONObject jsonobject = jsonarray.getJSONObject(i);
                mensajes.add(new Mensaje(jsonobject.getString("mensaje"),jsonobject.getInt("envia")));

            }
            RVAdapter_Mensaje adapter = new RVAdapter_Mensaje(mensajes,this);
            rv.setAdapter(adapter);
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    public class AsyncCV extends AsyncTask<String, String, String>
    {
        HttpURLConnection conn;
        URL url = null;
        int estado = 0;
        AsyncCV(){}
        AsyncCV(int estado){
            this.estado = estado;
        }
        @Override
        protected String doInBackground(String... params) {
            try {
                Uri.Builder builder = new Uri.Builder()
                        .appendQueryParameter("user", params[0]);
                url = new URL("http://"+ Global.ip+":8888/mensajes.php?"+builder.build().getEncodedQuery());

            } catch (MalformedURLException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                return "exception";
            }
            try {
                // Setup HttpURLConnection class to send and receive data from php and mysql
                conn = (HttpURLConnection)url.openConnection();
                conn.setReadTimeout(Global.READ_TIMEOUT);
                conn.setConnectTimeout(Global.CONNECTION_TIMEOUT);
                conn.setRequestMethod("GET");

                // setDoInput and setDoOutput method depict handling of both send and receive
                conn.setDoInput(true);
                conn.setDoOutput(true);


                // Open connection for sending data
                OutputStream os = conn.getOutputStream();
                BufferedWriter writer = new BufferedWriter(
                        new OutputStreamWriter(os, "UTF-8"));

                os.close();
                conn.connect();

            } catch (IOException e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
                return "exception";
            }

            try {

                int response_code = conn.getResponseCode();

                // Check if successful connection made
                if (response_code == HttpURLConnection.HTTP_OK) {

                    // Read data sent from server
                    InputStream input = conn.getInputStream();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(input));
                    StringBuilder result = new StringBuilder();
                    String line;

                    while ((line = reader.readLine()) != null) {
                        result.append(line);
                    }


                    return POST(result.toString());

                }else{

                    return("unsuccessful");
                }

            } catch (IOException e) {
                e.printStackTrace();
                return "exception";
            } finally {
                conn.disconnect();
            }


        }
        private String POST(String Response){
            return Response;
        }
        @Override
        protected void onPostExecute(String result) {


        }

    }

    public class enviarAsync extends AsyncTask<String, String, String>
    {
        HttpURLConnection conn;
        URL url = null;
        @Override
        protected String doInBackground(String... params) {
            try {
                Uri.Builder builder = new Uri.Builder()
                        .appendQueryParameter("user", params[0])
                        .appendQueryParameter("mensaje", params[1])
                        .appendQueryParameter("envia", params[2]);
                url = new URL("http://"+ Global.ip+":8888/enviarmensaje.php?"+builder.build().getEncodedQuery());

            } catch (MalformedURLException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                return "exception";
            }
            try {
                // Setup HttpURLConnection class to send and receive data from php and mysql
                conn = (HttpURLConnection)url.openConnection();
                conn.setReadTimeout(Global.READ_TIMEOUT);
                conn.setConnectTimeout(Global.CONNECTION_TIMEOUT);
                conn.setRequestMethod("GET");

                // setDoInput and setDoOutput method depict handling of both send and receive
                conn.setDoInput(true);
                conn.setDoOutput(true);


                // Open connection for sending data
                OutputStream os = conn.getOutputStream();
                BufferedWriter writer = new BufferedWriter(
                        new OutputStreamWriter(os, "UTF-8"));

                os.close();
                conn.connect();

            } catch (IOException e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
                return "exception";
            }

            try {

                int response_code = conn.getResponseCode();

                // Check if successful connection made
                if (response_code == HttpURLConnection.HTTP_OK) {

                    // Read data sent from server
                    InputStream input = conn.getInputStream();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(input));
                    StringBuilder result = new StringBuilder();
                    String line;

                    while ((line = reader.readLine()) != null) {
                        result.append(line);
                    }


                    return POST(result.toString());

                }else{

                    return("unsuccessful");
                }

            } catch (IOException e) {
                e.printStackTrace();
                return "exception";
            } finally {
                conn.disconnect();
            }


        }
        private String POST(String Response){
            return Response;
        }
        @Override
        protected void onPostExecute(String result) {


        }

    }
}
