package dxnnx.com.soytec;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;


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

/**
 * Created by DXNNX on 9/7/17.
 */

public class Registro extends Activity {
    private EditText etEmail;
    private EditText etPassword;
    private EditText etName;
    private EditText etID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.registro);
        etEmail = (EditText) findViewById(R.id.correo);
        etPassword = (EditText) findViewById(R.id.pass);
        etName = (EditText) findViewById(R.id.nombre);
        etID = (EditText) findViewById(R.id.Carnet);
    }
    public void Registrar(View v){

        //RequestFuture<StringRequest> futuro = RequestFuture.newFuture();
        final String email = etEmail.getText().toString();
        final String password = etPassword.getText().toString();
        final String name = etName.getText().toString();
        final String id = etID.getText().toString();
        if(email.contains("@itcr.ac.cr")) {
            new AsyncRegistro().execute(email,password,name,id);
        }


    }
    public void salirRegistro(){
        Context context = this;
        Intent intent = new Intent(this ,Login.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }


private class AsyncRegistro extends AsyncTask<String, String, String>
{
    HttpURLConnection conn;
    URL url = null;


    @Override
    protected String doInBackground(String... params) {
        try {

            // Enter URL address where your php file resides
            Uri.Builder builder = new Uri.Builder()
                    .appendQueryParameter("user", params[0])
                    .appendQueryParameter("pass", params[1])
                    .appendQueryParameter("nombre", params[2])
                    .appendQueryParameter("carnet", params[3]);
            System.out.println(builder.build().getEncodedQuery());
            url = new URL("http://"+ Global.ip+":8888/registrar.php?"+builder.build().getEncodedQuery());

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


                return result.toString();

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

    @Override
    protected void onPostExecute(String result) {
        try{
            JSONObject json= new JSONObject(result);
            if(json.has("ERROR")){
                Dialogos.Alerta("Error",json.getString("ERROR"),Registro.this);
            }else{
                json.getString("respuesta");
                Dialogos.Alerta("Enhorabuena",json.getString("respuesta"),Registro.this);
                salirRegistro();

            }
        }catch (Exception e){e.printStackTrace();}


    }

    }
}
