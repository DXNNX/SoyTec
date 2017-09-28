package com.dxnnx.soytec.mainactivity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.InputStream;

import dxnnx.com.soytec.Global;
import dxnnx.com.soytec.R;

/**
 * Created by DXNNX on 9/18/17.
 */

public class full_post extends AppCompatActivity {
    public TextView id;
    TextView ID;
    TextView nombre;
    TextView fecha;
    TextView descripcion;
    ImageView foto;
    ImageView favorito;
    TextView ubicacion;
    TextView categoria;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.popup_fullpost);
        ID = (TextView)findViewById(R.id.idtxt);
        nombre= (TextView)findViewById(R.id.nombretxt);
        fecha = (TextView)findViewById(R.id.fechatxt);
        descripcion= (TextView)findViewById(R.id.descripciontxt);
        foto = (ImageView)findViewById(R.id.fotoimg);
        favorito = (ImageView)findViewById(R.id.favorito);
        ubicacion = (TextView)findViewById(R.id.ubicaciontxt);
        categoria = (TextView)findViewById(R.id.categoriatxt);

        Evento eventos = Global.evento;
            nombre.setText(eventos.nombre);
            ID.setText("id: "+eventos.id);
            fecha.setText("Fecha: "+eventos.fecha);
            descripcion.setText(eventos.descripcion);
            ubicacion.setText("Lugar: "+eventos.ubicacion);
            categoria.setText("Categoria: "+eventos.categoria);
        if(eventos.estado==1){
            favorito.setImageResource(R.mipmap.fav);
        }else{
            favorito.setImageResource(R.mipmap.unfav);
        }
        String myUrl = "http://"+ Global.ip+":8888/pic/"+eventos.foto;
        full_post.DownloadImageWithURLTask downloadTask = new full_post.DownloadImageWithURLTask(foto);
        downloadTask.execute(myUrl);

    }
    public void manageFavs(View v){
        Evento Eventos = Global.evento;
        if(Eventos.getEstado() == 0){
            Eventos.estado = 1;
            new MainActivity.AsyncFavorito().execute(Global.user,Eventos.getId(),1+"");
            ImageView imgFav = (ImageView) findViewById(v.getId());
            imgFav.setImageResource(R.mipmap.fav);
        }else{
            Eventos.estado = 0;
            new MainActivity.AsyncFavorito().execute(Global.user,Eventos.getId(),3+"");
            ImageView imgFav = (ImageView) findViewById(v.getId());
            imgFav.setImageResource(R.mipmap.unfav);
        }

    }



    private class DownloadImageWithURLTask extends AsyncTask<String, Void, Bitmap> {
        ImageView bmImage;
        public DownloadImageWithURLTask(ImageView bmImage) {
            this.bmImage = bmImage;
        }

        protected Bitmap doInBackground(String... urls) {
            String pathToFile = urls[0];
            Bitmap bitmap = null;
            try {
                InputStream in = new java.net.URL(pathToFile).openStream();
                bitmap = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return bitmap;
        }
        protected void onPostExecute(Bitmap result) {
            bmImage.setImageBitmap(result);
        }
    }


}
