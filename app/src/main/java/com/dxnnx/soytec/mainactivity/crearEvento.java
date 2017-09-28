package com.dxnnx.soytec.mainactivity;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;

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
import java.text.Normalizer;
import java.util.regex.Pattern;

import dxnnx.com.soytec.Global;
import dxnnx.com.soytec.R;

/**
 * Created by DXNNX on 9/18/17.
 */

public class crearEvento extends AppCompatActivity {
    private EditText nombre;
    private EditText descripcion;
    private EditText ubicacion;
    private EditText categoria;
    private EditText foto;
    private DatePicker fecha;
    private boolean update;
    private boolean admin;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.crear_evento);
        nombre = (EditText) findViewById(R.id.nombreEvento);
        descripcion = (EditText) findViewById(R.id.descripcionEvento);
        ubicacion= (EditText) findViewById(R.id.ubicacionEvento);
        categoria= (EditText) findViewById(R.id.categoriaEvento);
        foto= (EditText) findViewById(R.id.fotoEvento);
        fecha= (DatePicker) findViewById(R.id.datePicker);
        update = getIntent().getBooleanExtra("update",false);
        admin = getIntent().getBooleanExtra("admin",false);
        if(update && !admin){
            Evento e = Global.evento;
            nombre.setText(e.nombre);
            descripcion.setText(e.descripcion);
            ubicacion.setText(e.ubicacion);
            categoria.setText(e.categoria);
            foto.setText(e.foto);
            fecha.updateDate(Integer.parseInt(e.fecha.split("-")[0]),Integer.parseInt(e.fecha.split("-")[1])-1,Integer.parseInt(e.fecha.split("-")[2]));
            Button btn = (Button) findViewById(R.id.enviar);
            btn.setText("Enviar Evento");
        }
        if(admin){Button btn = (Button) findViewById(R.id.enviar);
            btn.setText("Enviar Evento");
        }
    }




    public void enviarEvento(View v){
        String id = deAccent(nombre.getText().toString());
        String name = deAccent(nombre.getText().toString());
        String description = deAccent(descripcion.getText().toString());
        String category = deAccent(categoria.getText().toString());
        String location = deAccent(ubicacion.getText().toString());
        String Picture = deAccent(foto.getText().toString());
        String date= fecha.getYear()+"-"+(fecha.getMonth()+1)+"-"+fecha.getDayOfMonth();
        id = id.toLowerCase().replaceAll(" ","");
        if(update){
            id = Global.evento.id;
        }
        new AsyncCrearEvento().execute(id,name,date,description,Picture,location,category,String.valueOf((update | admin)));
        finish();
    }

    public String deAccent(String str) {
        String nfdNormalizedString = Normalizer.normalize(str, Normalizer.Form.NFD);
        Pattern pattern = Pattern.compile("\\p{InCombiningDiacriticalMarks}+");
        return pattern.matcher(nfdNormalizedString).replaceAll("");
    }


    private class AsyncCrearEvento extends AsyncTask<String, String, String>
    {
        HttpURLConnection conn;
        URL url = null;

        @Override
        protected String doInBackground(String... params) {
            try {

                // Enter URL address where your php file resides
                Uri.Builder builder = new Uri.Builder()
                        .appendQueryParameter("id", params[0])
                        .appendQueryParameter("nombre", params[1])
                        .appendQueryParameter("fecha", params[2])
                        .appendQueryParameter("descripcion", params[3])
                        .appendQueryParameter("foto", params[4])
                        .appendQueryParameter("ubicacion", params[5])
                        .appendQueryParameter("categoria", params[6]);
                if(Boolean.valueOf(params[7])){
                    builder.appendQueryParameter("tipo", "1");
                }else {
                    builder.appendQueryParameter("tipo", "2");
                }
                url = new URL("http://"+ Global.ip+":8888/crearevento.php?"+builder.build().getEncodedQuery());

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

        }

    }
}
