package dxnnx.com.soytec;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;


import com.dxnnx.soytec.mainactivity.MainActivity;

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


import org.json.JSONException;
import org.json.JSONObject;

import java.util.concurrent.ExecutionException;


public class Login extends Activity {
    private EditText etEmail;
    private EditText etPassword;
    private TextView lblError;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        etEmail = (EditText) findViewById(R.id.email);
        etPassword = (EditText) findViewById(R.id.pass);
        lblError = (TextView) findViewById(R.id.errorLogin);


    }

    public void login(View v) throws JSONException, ExecutionException, InterruptedException {

        //RequestFuture<StringRequest> futuro = RequestFuture.newFuture();
        // Get text from email and passord field
        final String email = etEmail.getText().toString();
        final String password = etPassword.getText().toString();


        // Initialize  AsyncLogin() class with email and password
        String result = new AsyncLogin().execute(email,password).get();//.execute(email,password);
        JSONObject json= new JSONObject(result);
        if(json.getInt("login") == 1){

            Global.user =  etEmail.getText().toString();
            lblError.setText("");
            Global.usuarioTipo = json.getString("tipo");

            GoMain();
        }else{
            lblError.setText("Usuario/Contraseña invalidos");
        }
    }
    public void regClic(View v){
        Intent myIntent = new Intent(this, Registro.class);
        this.startActivity(myIntent);

    }
    public void GoMain(){

        Intent myIntent = new Intent(Login.this, MainActivity.class);
        this.startActivity(myIntent);
    }






private class AsyncLogin extends AsyncTask<String, String, String>
{
    HttpURLConnection conn;
    URL url = null;

    @Override
    protected String doInBackground(String... params) {
        try {

            // Enter URL address where your php file resides
            Uri.Builder builder = new Uri.Builder()
                    .appendQueryParameter("user", params[0])
                    .appendQueryParameter("pass", params[1]);
            url = new URL("http://"+ Global.ip+":8888/login.php?"+builder.build().getEncodedQuery());

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

