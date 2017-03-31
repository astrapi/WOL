package com.pacoportela.android.wol;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by Francisco Portela Henche on 30/03/17.
 */

public class AdaptadorListaEquipos extends ArrayAdapter {
    Context contexto;
    ArrayList<String[]> listaDatos;

    public AdaptadorListaEquipos
            (Context contexto, int recursoLayout, int textViewId, ArrayList<String[]> listaDatos){
        super(contexto, recursoLayout, textViewId, listaDatos);
        this.contexto = contexto;
        this.listaDatos = listaDatos;
    }

    public View getView(int posicion, View convertView, ViewGroup parent){
        View view = null;
        if(convertView == null){
            LayoutInflater inflador =
                    (LayoutInflater)contexto.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflador.inflate(R.layout.layout_elemento_lista, null);
        }
        else {
            view = convertView;
        }
        TextView equipo = (TextView)view.findViewById(R.id.textViewListaEquipo);
        TextView mac = (TextView)view.findViewById(R.id.textViewListaMac);
        equipo.setText(listaDatos.get(posicion)[0]);
        mac.setText(listaDatos.get(posicion)[1]);
        return view;
    }
}
