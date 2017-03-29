package com.pacoportela.android.wol;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.view.menu.ListMenuItemView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class WakeOnLan extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wake_on_lan);
    }

    public void setItemEnLista(View view){
        EditText nombre = (EditText)findViewById(R.id.editTextNombre);
        EditText mac = (EditText)findViewById(R.id.editTextMac);
        String[] nuevoDato = {nombre.getText().toString(), mac.getText().toString()};
        ListView lista = (ListView)findViewById(R.id.listViewEquipos);

        ArrayAdapter<String[]> arrayAdapter = (ArrayAdapter<String[]>) lista.getAdapter();
        if(arrayAdapter == null){
            final ArrayList<String[]> listaDatos = new ArrayList<>();
            listaDatos.add(nuevoDato);
            arrayAdapter = new ArrayAdapter<String[]>(
                    this,
                    android.R.layout.simple_list_item_2,
                    android.R.id.text1,
                    listaDatos) {

                @NonNull
                @Override
                public View getView(int position, View convertView, @NonNull ViewGroup parent) {

                    // Must always return just a View.
                    View view = super.getView(position, convertView, parent);

                    // If you look at the android.R.layout.simple_list_item_2 source, you'll see
                    // it's a TwoLineListItem with 2 TextViews - text1 and text2.
                    //TwoLineListItem listItem = (TwoLineListItem) view;
                    String[] entry = listaDatos.get(position);
                    TextView text1 = (TextView) view.findViewById(android.R.id.text1);
                    TextView text2 = (TextView) view.findViewById(android.R.id.text2);
                    text1.setText(entry[0]);
                    text2.setText(entry[1]);
                    return view;
                }
            };
        }
        else {
            arrayAdapter.insert(nuevoDato, arrayAdapter.getCount());
        }
        lista.setAdapter(arrayAdapter);
    }
}
