package com.pacoportela.android.wol;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import java.util.StringTokenizer;

public class WakeOnLan extends AppCompatActivity implements AdapterView.OnItemClickListener,
        AdapterView.OnItemLongClickListener {
    ArrayList<String[]> listaDatos = new ArrayList<>();
    final String wol = "WOL";
    final static String PREFERENCIAS = "preferenciasWol";
    ListView lista;
    boolean modificando = false;
    int posicion = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wake_on_lan);

        Log.i(wol, "Entra en onCreate().");

        lista = (ListView)findViewById(R.id.listViewEquipos);
        lista.setOnItemClickListener(this);
        lista.setOnItemLongClickListener(this);
        cargarSharedPreferences();
    }

    protected void onPause(){
        Log.i(wol, "Entra en onPause().");

        super.onPause();
        guardarSharedPreferences();
        Log.i(wol, "Guarda preferencias.");
    }

    public void urkell(View view){
        Log.i(wol, "Muestra a Steve Urkel ;-))");
        LayoutInflater li = getLayoutInflater();
        View layout = li.inflate(R.layout.layout_urkel,null);
        Toast t = new Toast(getApplicationContext());
        t.setView(layout);
        t.setDuration(Toast.LENGTH_LONG);
        t.show();

    }

    private void guardarSharedPreferences(){
        SharedPreferences preferences = getSharedPreferences(PREFERENCIAS, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor =  preferences.edit();
        Set set = new HashSet<>();
        for(int i = 0; i < listaDatos.size(); i++){
            set.add(listaDatos.get(i)[0]+"-"+listaDatos.get(i)[1]);
        }
        editor.putStringSet("nombres", set);
        editor.apply();
    }

    private void cargarSharedPreferences(){
        SharedPreferences preferences = getSharedPreferences(PREFERENCIAS, Context.MODE_PRIVATE);
        Set<String> nombres = preferences.getStringSet("nombres", null);
        if(nombres != null) {
            for (String nombre : nombres) {
                StringTokenizer st = new StringTokenizer(nombre, "-");
                while (st.hasMoreTokens()) {
                    String[] dato = new String[2];
                    dato[0] = st.nextToken();
                    dato[1] = st.nextToken();
                    listaDatos.add(dato);
                }
            }
            Log.i(wol, "Carga preferencias.");
        }

            AdaptadorListaEquipos adaptadorListaEquipos = new AdaptadorListaEquipos
                (this, R.layout.layout_elemento_lista, R.id.textViewListaEquipo, listaDatos);
        lista.setAdapter(adaptadorListaEquipos);
    }

    public void setItemEnLista(View view){
        EditText nombre = (EditText)findViewById(R.id.editTextNombre);
        EditText mac = (EditText)findViewById(R.id.editTextMac);
        if(nombre.length() == 0 || mac.length() == 0){
            mensajeToast("No se puede dejar ningún dato vacio");
            nombre.requestFocus();
            return;
        }
        String[] nuevoDato = {nombre.getText().toString(), mac.getText().toString()};
        if(modificando){
            listaDatos.set(posicion, nuevoDato);
            Button boton = (Button)findViewById(R.id.boton);
            boton.setText(R.string.texto_boton_anadir);
            modificando = false;
            Log.i(wol, "Modificado un equipo de la lista.");
        }
        else{
            listaDatos.add(nuevoDato);
            Log.i(wol, "Añadido un nuevo equipo a la lista.");
        }

        AdaptadorListaEquipos adaptadorListaEquipos = new AdaptadorListaEquipos
                (this, R.layout.layout_elemento_lista, R.id.textViewListaEquipo, listaDatos);
        lista.setAdapter(adaptadorListaEquipos);
    }

    @RequiresApi(api = Build.VERSION_CODES.CUPCAKE)
    public void despertarOrdenador(int posicion){
        Log.i(wol, "Entra en despertarOrdenador()");
        new TareaDespertarOrdenador().execute(Integer.valueOf(posicion));
    }

    private byte getByte(String dato){
        byte b = 0x00;
        try{
            short s = Short.parseShort(dato, 16);
            b = (byte)s;
        }
        catch (NumberFormatException ne){
            Log.i(wol, ne.toString());
        }
        return b;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int posicion, long id) {
        this.posicion = posicion;
        despertarOrdenador(posicion);
    }

    private void modificar_borrarDatos(final int posicion){
        AlertDialog.Builder constructor = new AlertDialog.Builder(this);
        constructor.setMessage("¿Desea borra o modificar?").setCancelable(false).
                setPositiveButton("BORRAR", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        borrarEquipo(posicion, dialog);
                    }
                });
        constructor.setNegativeButton("MODIFICAR", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                modificarEquipo(posicion, dialog);
            }
        });
        AlertDialog dialog = constructor.create();
        dialog.show();
    }

    private void borrarEquipo(int posicion, DialogInterface dialogo){
        listaDatos.remove(posicion);
        lista.removeViews(posicion, 1);
        Log.i(wol, "Borrado un equipo de la lista");
        dialogo.cancel();
    }

    private void modificarEquipo(int posicion, DialogInterface dialogo){
        modificando = true;
        String nombre = listaDatos.get(posicion)[0];
        String mac = listaDatos.get(posicion)[1];
        Button boton = (Button)findViewById(R.id.boton);
        boton.setText(R.string.texto_boton_modificar);
        EditText et_nombre = (EditText) findViewById(R.id.editTextNombre);
        et_nombre.setText(nombre);
        EditText et_mac = (EditText) findViewById(R.id.editTextMac);
        et_mac.setText(mac);
        et_nombre.requestFocus();
        dialogo.cancel();
    }

    private void mensajeToast(String mensaje){
        Toast toast = Toast.makeText(this, mensaje, Toast.LENGTH_LONG);
        toast.show();
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
        modificar_borrarDatos(position);
        return false;
    }

    private class TareaDespertarOrdenador extends AsyncTask<Integer, Integer, String>{

        @Override
        protected String doInBackground(Integer... posicion) {
            Log.i(wol, "Crea clase AsyncTask TareaDespertarOrdenador");
            int pos = posicion[0].intValue();
            String[] datos = listaDatos.get(pos);
            String macString = datos[1];
            byte[] mac = new byte[6];
            int i = 0;
            StringTokenizer st = new StringTokenizer(macString, ".");
            while(st.hasMoreTokens()){
                mac[i++] = getByte(st.nextToken());
            }

            Log.i(wol, "MAC del equipo a despertar: " + macString);

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
                udp.send(new DatagramPacket
                        (buffer, 102, InetAddress.getByName("255.255.255.255"), 1976));
            } catch (java.net.SocketException se) {
                System.out.println(se.toString());

            } catch (java.net.UnknownHostException ue) {
                System.out.println(ue.toString());

            } catch (java.io.IOException ie) {
                System.out.println(ie.toString());
            }
            return "Enviado datagrama magic packet";
        }

        @Override
        protected void onPostExecute(String mensaje){
            Log.i(wol, mensaje);
            mensajeToast(mensaje);
        }
    }
}
