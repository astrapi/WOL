package com.pacoportela.android.wol;

import android.os.StrictMode;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.view.menu.ListMenuItemView;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.math.BigInteger;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

public class WakeOnLan extends AppCompatActivity implements AdapterView.OnItemClickListener {
    ArrayList<String[]> listaDatos = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wake_on_lan);

	if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }

    }

    public void setItemEnLista(View view){
        EditText nombre = (EditText)findViewById(R.id.editTextNombre);
        EditText mac = (EditText)findViewById(R.id.editTextMac);

        String[] nuevoDato = {nombre.getText().toString(), mac.getText().toString()};

        ListView lista = (ListView)findViewById(R.id.listViewEquipos);
        listaDatos.add(nuevoDato);

        AdaptadorListaEquipos adaptadorListaEquipos = new AdaptadorListaEquipos
                (this, R.layout.layout_elemento_lista, R.id.textViewListaEquipo, listaDatos);
        lista.setAdapter(adaptadorListaEquipos);
        lista.setOnItemClickListener(this);
    }

    public void despertarOrdenador(int posicion){
        String[] datos = listaDatos.get(posicion);
        String macString = datos[1];
        byte[] mac = new byte[6];
        int i = 0;
        StringTokenizer st = new StringTokenizer(macString, ".");
        while(st.hasMoreTokens()){
            mac[i++] = getByte(st.nextToken());
        }
        for(int j = 0; j < mac.length; j++){
            Log.i("WOL", String.valueOf(mac[j]));
        }
        byte[] buffer = new byte[102];
        byte contador;
        for (contador = 0; contador < 6; contador++) {
            buffer[contador] = (byte) 0xFF;
        }
        for (contador = 6; contador < 102; contador += 6) {
            System.arraycopy(mac, 0, buffer, contador, 6);
        }
        try {
            DatagramSocket udp = new DatagramSocket();
            udp.setBroadcast(true);
            udp.send(new DatagramPacket(buffer, 102, InetAddress.getByName("255.255.255.255"), 1976));
        } catch (java.net.SocketException se) {
            System.out.println(se.toString());

        } catch (java.net.UnknownHostException ue) {
            System.out.println(ue.toString());

        } catch (java.io.IOException ie) {
            System.out.println(ie.toString());

        }
    }

    private byte getByte(String dato){
        short s = Short.parseShort(dato, 16);
        byte b = (byte)s;
        return b;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int posicion, long id) {
        despertarOrdenador(posicion);
    }
}
