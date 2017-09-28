package com.dxnnx.soytec.mainactivity;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import dxnnx.com.soytec.R;

/**
 * Created by DXNNX on 9/18/17.
 */

public class RVAdapter_Mensaje extends RecyclerView.Adapter<RVAdapter_Mensaje.EventViewHolder>{

        List<Mensaje> mensajes;
        Context context;

    RVAdapter_Mensaje(List<Mensaje> mensajes,Context context){
            this.mensajes= mensajes;
            this.context= context;
        }
        @Override
        public int getItemCount() {
            return mensajes.size();
        }

        @Override
        public EventViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            View v = LayoutInflater.from(viewGroup.getContext())
                    .inflate(R.layout.cv_mensajes, viewGroup, false);
            return new com.dxnnx.soytec.mainactivity.RVAdapter_Mensaje.EventViewHolder(v);
        }

        @Override
        public void onBindViewHolder(EventViewHolder eventViewHolder, int i) {

            eventViewHolder.mensajeIndividual.setText(mensajes.get(i).mensaje);
            if(mensajes.get(i).envia==0){
                eventViewHolder.cv.setBackgroundColor(Color.LTGRAY);
            }else{
                eventViewHolder.cv.setBackgroundColor(Color.CYAN);
            }
        }
        @Override
        public void onAttachedToRecyclerView(RecyclerView recyclerView) {
            super.onAttachedToRecyclerView(recyclerView);
        }

        public static class EventViewHolder extends RecyclerView.ViewHolder {

            CardView cv;
            int ID;
            TextView mensajeIndividual;


            EventViewHolder(View itemView) {
                super(itemView);
                cv = (CardView)itemView.findViewById(R.id.cv_mensajes);

               mensajeIndividual = (TextView) itemView.findViewById(R.id.mensajetxt);


            }
        }
}
