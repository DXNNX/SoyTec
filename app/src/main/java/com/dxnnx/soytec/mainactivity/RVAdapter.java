package com.dxnnx.soytec.mainactivity;


import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.support.v7.widget.*;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.InputStream;
import java.util.List;

import dxnnx.com.soytec.Global;
import dxnnx.com.soytec.R;

/**
 * Created by DXNNX on 9/8/17.
 */

public class RVAdapter  extends RecyclerView.Adapter<RVAdapter.EventViewHolder>{

    List<Evento> eventos;
    boolean revision = false;
    RVAdapter(List<Evento> eventos){
        this.eventos= eventos;
    }
    RVAdapter(List<Evento> eventos,boolean revision){
        this.eventos= eventos;this.revision=revision;
    }
    @Override
    public int getItemCount() {
        return eventos.size();
    }

    @Override
    public EventViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.card_view, viewGroup, false);
        return new EventViewHolder(v);
    }

    @Override
    public void onBindViewHolder(EventViewHolder eventViewHolder, int i) {
        eventViewHolder.nombre.setText(eventos.get(i).nombre);
        eventViewHolder.ID.setText(eventos.get(i).id);
        eventViewHolder.fecha.setText(eventos.get(i).fecha);
        eventViewHolder.descripcion.setText(eventos.get(i).descripcion);
        eventViewHolder.ubicacion.setText(eventos.get(i).ubicacion);
        eventViewHolder.categoria.setText(eventos.get(i).categoria);
        eventViewHolder.favorito.setId(i);
        eventViewHolder.foto.setTag(i);
        if(eventos.get(i).estado==1){
            eventViewHolder.favorito.setImageResource(R.mipmap.fav);
        }else{
            eventViewHolder.favorito.setImageResource(R.mipmap.unfav);
        }
        if(revision){
            eventViewHolder.favorito.setVisibility(View.GONE);
            eventViewHolder.checked.setVisibility(View.VISIBLE);
        }

        if(!Global.usuarioTipo.equals("admin")){
            eventViewHolder.delete.setVisibility(View.INVISIBLE);
            eventViewHolder.update.setVisibility(View.INVISIBLE);
        }
        eventViewHolder.checked.setTag(i);
        eventViewHolder.delete.setTag(i);
        eventViewHolder.update.setTag(i);
        String myUrl = "http://"+ Global.ip+":8888/pic/"+eventos.get(i).foto;
        DownloadImageWithURLTask downloadTask = new DownloadImageWithURLTask(eventViewHolder.foto);
        downloadTask.execute(myUrl);
    }
    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    public static class EventViewHolder extends RecyclerView.ViewHolder {

        CardView cv;
        TextView ID;
        TextView nombre;
        TextView fecha;
        TextView descripcion;
        ImageView foto;
        ImageView favorito;
        TextView ubicacion;
        TextView categoria;
        ImageButton update;
        ImageButton delete;
        ImageButton checked;
        EventViewHolder(View itemView) {
            super(itemView);
            cv = (CardView)itemView.findViewById(R.id.cv);

            ID = (TextView)itemView.findViewById(R.id.idtxt);
            nombre= (TextView)itemView.findViewById(R.id.nombretxt);
            fecha = (TextView)itemView.findViewById(R.id.fechatxt);
            descripcion= (TextView)itemView.findViewById(R.id.descripciontxt);

            foto = (ImageView)itemView.findViewById(R.id.fotoimg);
            update= (ImageButton)itemView.findViewById(R.id.updateImage);
            delete= (ImageButton)itemView.findViewById(R.id.borrarImg);
            checked= (ImageButton)itemView.findViewById(R.id.checkedBtn);

            favorito = (ImageView)itemView.findViewById(R.id.favorito);
            ubicacion = (TextView)itemView.findViewById(R.id.ubicaciontxt);
            categoria = (TextView)itemView.findViewById(R.id.categoriatxt);


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