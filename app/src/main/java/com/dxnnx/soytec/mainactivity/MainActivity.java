package com.dxnnx.soytec.mainactivity;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

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

public class MainActivity extends AppCompatActivity {
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private TextView mTextMessage;
    private EditText mBuscar;
    private Spinner mSpinner;
    public RecyclerView rv;
    public boolean admin = false;
    ImageButton chkbtn;
    public List<Evento> Eventos;
    public List<String> mCategorias = new ArrayList<String>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        rv = (RecyclerView)findViewById(R.id.rv);
        mSpinner = (Spinner)findViewById(R.id.spinner);
        chkbtn = (ImageButton) findViewById(R.id.checkBtn);
        if(Global.usuarioTipo.equals("admin")) {
            admin = true;
            chkbtn.setVisibility(View.VISIBLE);
        }else{
            chkbtn.setVisibility(View.GONE);
        }
        buscadorSetListener();
        homeClic(null);
    }

    public void buscadorSetListener(){
        mBuscar= (EditText) findViewById(R.id.buscar);
        mBuscar.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                boolean handled = false;
                if (actionId == EditorInfo.IME_ACTION_SEND) {
                    buscarAction();
                    handled = true;
                }
                return handled;
            }
        });

    }

    public void buscarAction(){

        try {

            genCardViews((new AsyncCV(6).execute(mBuscar.getText().toString()).get()));
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
    }

    public void manageFavs(View v){
        try{
        if(Eventos.get(v.getId()).getEstado() == 0){
            Eventos.get(v.getId()).estado = 1;
            new AsyncFavorito().execute(Global.user,Eventos.get(v.getId()).getId(),1+"");
            ImageView imgFav = (ImageView) findViewById(v.getId());
            imgFav.setImageResource(R.mipmap.fav);
        }else{
            Eventos.get(v.getId()).estado = 0;
            new AsyncFavorito().execute(Global.user,Eventos.get(v.getId()).getId(),3+"");
            ImageView imgFav = (ImageView) findViewById(v.getId());
            imgFav.setImageResource(R.mipmap.unfav);
        }
        }catch(IndexOutOfBoundsException e){

        }
    }

    public void getTags(View v){
        mSpinner.setVisibility(View.VISIBLE);
        String query = null;
        try {
            query = (new AsyncCV(5).execute().get());
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        JSONArray jsonarray = null;
        try {
            jsonarray = new JSONArray(query);
            mCategorias = new ArrayList<String>();
            for (int i = 0; i < jsonarray.length(); i++) {
                JSONObject jsonobject = jsonarray.getJSONObject(i);
                String temp = jsonarray.getString(i).split("\":\"")[1];
                mCategorias.add(temp.substring(0,temp.length()-2));
            }
            ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, mCategorias);
            dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            mSpinner.setAdapter(dataAdapter);
            mSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int pos,
                                           long id) {
                    try {
                        genCardViews(new AsyncCV(4).execute(mCategorias.get(pos)).get());
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    } catch (ExecutionException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> parentView) {
                    // your code here
                }

            });
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void postCompleto(View v){
        Global.evento = Eventos.get(Integer.parseInt(v.getTag().toString()));
        Intent intent = new Intent(MainActivity.this, full_post.class);
        startActivity(intent);
    }

    public void deletePost(View v){
        String idEventoBorrado = Eventos.get(Integer.parseInt(v.getTag().toString())).getId();
        new AsyncDelete().execute(idEventoBorrado,"1");
        homeClic(null);

    }

    public void revisarSolicitudes(View v) throws ExecutionException, InterruptedException {
        Global.revision = true;
        genCardViews(new AsyncCV(7).execute().get());
        Global.revision = false;
    }

    public void checked(View v){
            String idEventoRevisado = Eventos.get(Integer.parseInt(v.getTag().toString())).getId();
            new AsyncChecked().execute(idEventoRevisado);
        try {
            revisarSolicitudes(null);
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

    public void updatePost(View v){
        Global.evento = Eventos.get(Integer.parseInt(v.getTag().toString()));
        Context context = this;
        Intent intent = new Intent(this ,crearEvento.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra("update",true);
        context.startActivity(intent);
        homeClic(null);
    }

    public void getTop5(View v){

        try {
            genCardViews((new AsyncCV(3).execute().get()));
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
    }

    public void getFavs(View v){
        try {
            genCardViews((new AsyncCV(2).execute().get()));
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
    }


    public void homeClic(View v){

        try {
            genCardViews((new AsyncCV().execute().get()));
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
    }

    public void solicitudEvento(View v){
        Context context = this;
        Intent intent = new Intent(this ,crearEvento.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        if(admin){
            intent.putExtra("admin",true);
        }else{
            intent.putExtra("admin",false);
        }
        context.startActivity(intent);
    }

    public void verMensajeria(View v){
        if(Global.usuarioTipo.equals("admin")){
            Context context = this;
            Intent intent = new Intent(this ,listaChats.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        } else{
            Context context = this;
            Intent intent = new Intent(this ,mensajeria.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.putExtra("admin",false);
            context.startActivity(intent);
        }
    }

    public void genCardViews(String query)
    {
        Eventos = new ArrayList<>();
        JSONArray jsonarray = null;
        try {
            jsonarray = new JSONArray(query);

        for (int i = 0; i < jsonarray.length(); i++) {
            //Evento(String id, String nombre, String fecha,String descripcion, String foto,String Ubicacion)
            JSONObject jsonobject = jsonarray.getJSONObject(i);
            Eventos.add(new Evento(jsonobject.getString("id"),
                    jsonobject.getString("nombre"),
                    jsonobject.getString("fecha"),
                    jsonobject.getString("descripcion"),
                    jsonobject.getString("foto"),
                    jsonobject.getString("ubicacion"),
                    (!Global.revision)?jsonobject.getInt("fav"):0,
                    jsonobject.getString("categoria")));

        }
            rv = (RecyclerView)findViewById(R.id.rv);
            rv.setHasFixedSize(true);
            LinearLayoutManager llm = new LinearLayoutManager(this);
            rv.setLayoutManager(llm);
            RVAdapter adapter = new RVAdapter(Eventos,Global.revision);
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
                        .appendQueryParameter("user", Global.user);
                if(estado != 0){
                    builder = builder.appendQueryParameter("tipo",estado+"");
                }
                if(estado == 4){
                    builder = builder.appendQueryParameter("categoria",params[0]);
                }
                if(estado == 6){
                    builder = builder.appendQueryParameter("busqueda",params[0]);
                }
                url = new URL("http://"+ Global.ip+":8888/eventos.php?"+builder.build().getEncodedQuery());

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



    static class AsyncFavorito extends AsyncTask<String, String, String>
    {
        HttpURLConnection conn;
        URL url = null;


        @Override
        protected String doInBackground(String... params) {
            try {

                // Enter URL address where your php file resides
                Uri.Builder builder = new Uri.Builder()
                        .appendQueryParameter("user", params[0])
                        .appendQueryParameter("id", params[1])
                        .appendQueryParameter("tipo", params[2]);
                url = new URL("http://"+ Global.ip+":8888/favorito.php?"+builder.build().getEncodedQuery());

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

    static class AsyncDelete extends AsyncTask<String, String, String>
    {
        HttpURLConnection conn;
        URL url = null;


        @Override
        protected String doInBackground(String... params) {
            try {

                // Enter URL address where your php file resides
                Uri.Builder builder = new Uri.Builder()
                        .appendQueryParameter("id", params[0])
                        .appendQueryParameter("tipo", params[1]);
                url = new URL("http://"+ Global.ip+":8888/borrar.php?"+builder.build().getEncodedQuery());

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


    private class AsyncChecked extends AsyncTask<String, String, String>
    {
        HttpURLConnection conn;
        URL url = null;

        @Override
        protected String doInBackground(String... params) {
            try {

                // Enter URL address where your php file resides
                Uri.Builder builder = new Uri.Builder()
                        .appendQueryParameter("id", params[0])
                        .appendQueryParameter("tipo", "3");
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
