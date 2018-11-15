package ricardopazdemiquel.com.imotos;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Hashtable;

import ricardopazdemiquel.com.imotos.clienteHTTP.HttpConnection;
import ricardopazdemiquel.com.imotos.clienteHTTP.MethodType;
import ricardopazdemiquel.com.imotos.clienteHTTP.StandarRequestConfiguration;
import ricardopazdemiquel.com.imotos.utiles.Contexto;


public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private BroadcastReceiver broadcastReceiver;
    private BroadcastReceiver broadcastReceiverMessage;

    private LinearLayout btn_nav_formaspago;
    private LinearLayout btn_nav_miperfil;
    private LinearLayout btn_nav_misviajes;
    private LinearLayout btn_nav_preferencias;
    private JSONObject usr_log;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Intent inten = new Intent(MainActivity.this,PedirSieteMap.class);
        startActivity(inten);
        finish();

        ImageView fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
                if (drawer.isDrawerOpen(GravityCompat.START)) {
                    drawer.closeDrawer(GravityCompat.START);
                } else{
                    drawer.openDrawer(GravityCompat.START);
                }
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, null, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);

        View header = navigationView.inflateHeaderView(R.layout.nav_header_main);
        btn_nav_formaspago=header.findViewById(R.id.btn_nav_formaspago);
        btn_nav_miperfil=header.findViewById(R.id.btn_nav_miperfil);
        btn_nav_misviajes=header.findViewById(R.id.btn_nav_misviajes);
        btn_nav_preferencias=header.findViewById(R.id.btn_nav_preferencias);

        btn_nav_formaspago.setOnClickListener(this);
        btn_nav_miperfil.setOnClickListener(this);
        btn_nav_misviajes.setOnClickListener(this);
        btn_nav_preferencias.setOnClickListener(this);

        if(getUsr_log()!=null){
            if(!runtime_permissions()){
                seleccionarFragmento("Mapa");
            }
        }
        try {
            new Get_validarCarrera(usr_log.getInt("id")).execute();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        //  showDirections(-17.89,-63.1408,-17.6,-63.1408);
    }

    @Override
    protected void onResume() {
        super.onResume();

    }


    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


         // Json : obtiene el id del usuario si es que ya estuvo registrado en la aplicacion o aiga iniciado sesion
         public JSONObject getUsr_log() {
             SharedPreferences preferencias = getSharedPreferences("myPref",MODE_PRIVATE);
             String usr = preferencias.getString("usr_log", "");
             if (usr.length()<=0) {
                 Intent intent = new Intent(MainActivity.this,LoginCliente.class);
                 intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                 startActivity(intent);
                 return null;
             }else{
                 try {
                     usr_log=new JSONObject(usr);
                     return usr_log;
                 } catch (JSONException e) {
                     e.printStackTrace();
                     return null;
                 }
             }
         }

    private void seleccionarFragmento(String fragmento) {
        Fragment fragmentoGenerico = null;
        FragmentManager fragmentManager = getSupportFragmentManager();
        Object obj = -1;
        switch (fragmento) {
            case "Mapa":
                fragmentoGenerico= new Elejir_tipo_siete();
                break;
        }
            fragmentManager.beginTransaction().replace(R.id.contenmain, fragmentoGenerico).commit();
        if (fragmentoGenerico != null) {

        }
    }

    private boolean runtime_permissions() {
        if(Build.VERSION.SDK_INT >= 23 && ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED){      requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},100);
            return true;
        }
        return false;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == 100){
            if( grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED){

                seleccionarFragmento("Mapa");
            }else {
                runtime_permissions();
            }
        }
    }

    public void showDirections(double lat, double lng, double lat1, double lng1) {
        final Intent intent = new
                Intent(Intent.ACTION_VIEW, Uri.parse("http://maps.google.com/maps?" +
                "saddr=" + lat + "," + lng + "&daddr=" + lat1 + "," +
                lng1));
        intent.setClassName("com.google.android.apps.maps", "com.google.android.maps.MapsActivity");
        startActivity(intent);
    }

         @Override
         public void onClick(View v) {
            Intent intent;
            int id=v.getId();
            switch (id){
                 case R.id.btn_nav_formaspago:
                     intent = new Intent(MainActivity.this , Transaccion_cliente_Activity.class);
                     startActivity(intent);
                     break;
                 case R.id.btn_nav_miperfil:
                     intent =  new Intent(MainActivity.this , Perfil_ClienteFragment.class);
                     startActivity(intent);
                     break;
                 case R.id.btn_nav_misviajes:
                     intent =  new Intent(MainActivity.this , MisViajes_Cliente_Activity.class);
                     startActivity(intent);
                     break;
                 case R.id.btn_nav_preferencias:
                     intent =  new Intent(MainActivity.this , Preferencias.class);
                     startActivity(intent);
                     break;
             }

             DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
             drawer.closeDrawer(GravityCompat.START);
         }



    public class Get_validarCarrera extends AsyncTask<Void, String, String> {
         private int id;
         public Get_validarCarrera(int id){
             this.id=id;
         }

         @Override
         protected void onPreExecute()
         {
             super.onPreExecute();
         }
         @Override
         protected String doInBackground(Void... params) {
             Hashtable<String, String> parametros = new Hashtable<>();
             parametros.put("evento", "get_carrera_cliente");
             parametros.put("id_usr",id+"");
             String respuesta = HttpConnection.sendRequest(new StandarRequestConfiguration(getString(R.string.url_servlet_index), MethodType.POST, parametros));
             return respuesta;
         }
         @Override
         protected void onPostExecute(String resp) {
             super.onPostExecute(resp);
             if(resp==null){
                 Log.e(Contexto.APP_TAG, "Hubo un error al conectarse al servidor.");
             }else{
                 if (resp.contains("falso")) {
                     Log.e(Contexto.APP_TAG, "Hubo un error al conectarse al servidor.");
                 } else {
                     try {
                         JSONObject obj = new JSONObject(resp);
                         if(obj.getBoolean("exito")) {
                             if(obj.getInt("id_tipo")==2){//togo
                                 Intent intent = new Intent(MainActivity.this, Inicio_viaje_togo.class);
                                 intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                 intent.putExtra("obj_carrera", obj.toString());
                                 startActivity(intent);
                             }else{
                                 Intent intent = new Intent(MainActivity.this, EsperandoConductor.class);
                                 intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                 intent.putExtra("obj_carrera", obj.toString());
                                 startActivity(intent);
                             }

                         }else{

                             SharedPreferences preferencias = getSharedPreferences("myPref",MODE_PRIVATE);
                             SharedPreferences.Editor editor = preferencias.edit();
                             editor.putString("chat_carrera", new JSONArray().toString());
                             editor.commit();
                         }
                     } catch (JSONException e) {
                         e.printStackTrace();
                     }
                 }
             }

         }
         @Override
         protected void onProgressUpdate(String... values) {
             super.onProgressUpdate(values);

         }
    }

}
