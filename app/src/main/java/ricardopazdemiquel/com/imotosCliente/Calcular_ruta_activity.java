package ricardopazdemiquel.com.imotosCliente;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.BottomSheetBehavior;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import ricardopazdemiquel.com.imotosCliente.Dialog.Confirmar_viaje_Dialog;
import ricardopazdemiquel.com.imotosCliente.Dialog.Confirmar_viaje_Dialog2;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceBuffer;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutionException;

import ricardopazdemiquel.com.imotosCliente.clienteHTTP.HttpConnection;
import ricardopazdemiquel.com.imotosCliente.clienteHTTP.MethodType;
import ricardopazdemiquel.com.imotosCliente.clienteHTTP.StandarRequestConfiguration;
import ricardopazdemiquel.com.imotosCliente.utiles.Contexto;
import ricardopazdemiquel.com.imotosCliente.utiles.DirectionsJSONParser;
import ricardopazdemiquel.com.imotosCliente.utiles.Token;

public class Calcular_ruta_activity extends AppCompatActivity implements View.OnClickListener, View.OnFocusChangeListener, GoogleApiClient.OnConnectionFailedListener,GoogleApiClient.ConnectionCallbacks {

    MapView mMapView;
    private GoogleMap googleMap;
    private boolean entroLocation=false;
    private static final String LOG_TAG = "MainActivity";
    private static final int GOOGLE_API_CLIENT_ID = 0;
    private AutoCompleteTextView selected;
    private TextView monto;
    private TextView tv_cantidad;
    private Button btn_confirmar;

    private LinearLayout ll_ubic;
    private LinearLayout linear_confirm;
    private LinearLayout linearLayoutTogo;
    private ConstraintLayout layoutButon;
    private LatLng inicio;
    private LatLng fin;
    private GoogleApiClient mGoogleApiClient;
    private PlaceArrayAdapter mPlaceArrayAdapter;
    private int tipo_pago;
    private BottomSheetBehavior bottomSheetBehavior;
    private static final LatLngBounds BOUNDS_MOUNTAIN_VIEW = new LatLngBounds(
            new LatLng(37.398160, -122.180831), new LatLng(37.430610, -121.9720));

    JSONObject usr_log;
    //inicializamos los botones para pedir siete togo y el tipo de carrera
    private RadioButton radio_efectivo;
    private RadioButton radio_credito;
    // inicializamos los iconos de confirmar carrera
    private ImageView icono2 ;
    double mont;
    boolean aeropuerto = false;

    JSONObject obj ;
    private int tipo_carrera;
    double longitudeGPS;
    double latitudeGPS;
    double latinicio;
    double lnginicio;
    double latfinal;
    double lngfinal;
    String mensaje;

    public Calcular_ruta_activity() {
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calcular_ruta);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar2);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_left_arrow);

        usr_log = getUsr_log();
        if (usr_log == null) {
            Intent intent = new Intent(Calcular_ruta_activity.this, LoginCliente.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        }
        monto = findViewById(R.id.tv_monto);
        radio_efectivo = findViewById(R.id.radio_efectivo);
        radio_credito = findViewById(R.id.radio_credito);
        radio_efectivo.setOnClickListener(this);
        radio_credito.setOnClickListener(this);
        btn_confirmar= findViewById(R.id.btn_confirmar);
        btn_confirmar.setOnClickListener(this);
        icono2 = findViewById(R.id.icono2);

        String str = getIntent().getStringExtra("JSON");

        try {
            obj = new JSONObject(str);
            tipo_carrera = obj.getInt("tipo");
            latinicio = obj.getDouble("latinicio");
            lnginicio = obj.getDouble("lnginicio");
            latfinal = obj.getDouble("latfinal");
            lngfinal = obj.getDouble("lngfinal");
            longitudeGPS = obj.getDouble("lng");
            latitudeGPS = obj.getDouble("lat");
            if(tipo_carrera == 2){
                mensaje = obj.getString("mensaje");
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

       /* float[] results = new float[1];
            Location.distanceBetween(
                  latinicio,
                    lnginicio,
                    -17.6481,
                    -63.1404,
                    results);
            if( results[0]<1000){
                //punto 1 ae
                aeropuerto = true;
            }

        float[] results2 = new float[1];
        Location.distanceBetween(
                latfinal,
                lngfinal,
                -17.6481,
                -63.1404,
                results2);
        if( results2[0]<1000){
            //punto 1 ae
            aeropuerto = true;
        }
        if(aeropuerto){
            android.app.FragmentManager fragmentManager = getFragmentManager();
            new Aeropuerto_viaje_Dialog().show(fragmentManager, "Dialog");
        }*/

        cargarTipo();
        mGoogleApiClient = new GoogleApiClient.Builder(Calcular_ruta_activity.this)
                .addApi(Places.GEO_DATA_API)
                .enableAutoManage(this, GOOGLE_API_CLIENT_ID, this)
                .addConnectionCallbacks(this)
                .build();

        mPlaceArrayAdapter = new PlaceArrayAdapter(this, android.R.layout.simple_list_item_1,
                BOUNDS_MOUNTAIN_VIEW, null);

        mMapView = findViewById(R.id.mapviewPedirSiete);
        mMapView.onCreate(savedInstanceState);
        mMapView.onResume();

        MapsInitializer.initialize(this.getApplicationContext());
        mMapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(final GoogleMap mMap) {
                googleMap = mMap;
                CameraUpdate cu = CameraUpdateFactory.newLatLngZoom(new LatLng(latitudeGPS, longitudeGPS), 14);
                googleMap.animateCamera(cu);
                LatLng latLng1 = new LatLng(latinicio,lnginicio);
                LatLng latLng2 = new LatLng(latfinal,lngfinal);
                calculando_ruta( tipo_carrera,latLng1 ,latLng2);
                //mMap.setMapStyle(new MapStyleOptions(getResources().getString(R.string.style_map)));
            }
        });

        if (mMapView != null &&
                mMapView.findViewById(Integer.parseInt("1")) != null) {
            ImageView locationButton = (ImageView) ((View) mMapView.findViewById(Integer.parseInt("1")).getParent()).findViewById(Integer.parseInt("2"));
            RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams)
                    locationButton.getLayoutParams();
            layoutParams.addRule(RelativeLayout.ALIGN_PARENT_TOP, 0);
            layoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE);
            layoutParams.setMargins(0, 0, 30, 600);
            locationButton.setImageResource(R.drawable.ic_mapposition_foreground);

        }
        mMapView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                InputMethodManager imm = (InputMethodManager)getSystemService(Context.
                        INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
                return true;
            }
        });
    }

    // Opcion para ir atras sin reiniciar el la actividad anterior de nuevo
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onBackPressed() {
        finish();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }


    public void ok_pedir_viaje() throws JSONException {
        Intent inte = new Intent(Calcular_ruta_activity.this, PidiendoSiete.class);
        inte.putExtra("latInicio", inicio.latitude + "");
        inte.putExtra("lngInicio", inicio.longitude + "");
        inte.putExtra("latFin", fin.latitude + "");
        inte.putExtra("lngFin", fin.longitude + "");
        inte.putExtra("token", Token.currentToken);
        inte.putExtra("id_usr", usr_log.getInt("id") + "");
        inte.putExtra("tipo", tipo_carrera + "");
        inte.putExtra("tipo_pago", tipo_pago + "");
        if(tipo_carrera == 2){
            inte.putExtra("mensaje", mensaje + "");
        }
        startActivity(inte);
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_confirmar:
                Confimar_viaje();
                break;
        }
    }


    private AdapterView.OnItemClickListener mAutocompleteClickListener
            = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            final PlaceArrayAdapter.PlaceAutocomplete item = mPlaceArrayAdapter.getItem(position);
            final String placeId = String.valueOf(item.placeId);
            Log.i(LOG_TAG, "Selected: " + item.description);
            PendingResult<PlaceBuffer> placeResult = Places.GeoDataApi
                    .getPlaceById(mGoogleApiClient, placeId);
            placeResult.setResultCallback(mUpdatePlaceDetailsCallback);
            Log.i(LOG_TAG, "Fetching details for ID: " + item.placeId);
        }
    };

    private ResultCallback<PlaceBuffer> mUpdatePlaceDetailsCallback
            = new ResultCallback<PlaceBuffer>() {
        @Override
        public void onResult(PlaceBuffer places) {
            if (!places.getStatus().isSuccess()) {
                Log.e(LOG_TAG, "Place query did not complete. Error: " +
                        places.getStatus().toString());
                return;
            }
            // Selecting the first object buffer.
            final Place place = places.get(0);
            CharSequence attributions = places.getAttributions();
            CameraUpdate cu = CameraUpdateFactory.newLatLngZoom(place.getLatLng(), 18);
            googleMap.animateCamera(cu);

        }
    };

    private String getCompleteAddressString(double LATITUDE, double LONGITUDE) {
        String strAdd = "";
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocation(LATITUDE, LONGITUDE, 1);
            if (addresses != null) {
                Address returnedAddress = addresses.get(0);
                //StringBuilder strReturnedAddress = new StringBuilder("");

                strAdd=returnedAddress.getThoroughfare();
                if(strAdd==null )
                strAdd=returnedAddress.getFeatureName();

                //  Log.w("My Current loction addr", strReturnedAddress.toString());
            } else {
                Log.w("My Current loction addr", "No Address returned!");
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.w("My Current loction addr", "Canont get Address!");
        }
        return strAdd;
    }
    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        if(hasFocus){
            selected=(AutoCompleteTextView) v;

        }
    }

    public void cargarTipo(){
        switch (tipo_carrera){
            case 1:
                setTitle("iMotos");
                break;
            case 2:
                setTitle("iMotos");
                break;
        }
    }
    private String obtenerDireccionesURL(LatLng origin, LatLng dest){

        String str_origin = "origin="+origin.latitude+","+origin.longitude;

        String str_dest = "destination="+dest.latitude+","+dest.longitude;

        String key = "key="+getString(R.string.apikey);

        String parameters = str_origin+"&"+str_dest;

        String output = "json";

        String url = "https://maps.googleapis.com/maps/api/directions/"+output+"?"+parameters+"&"+key;

        return url;
    }

    public JSONObject getUsr_log() {
        SharedPreferences preferencias = getSharedPreferences("myPref", MODE_PRIVATE);
        String usr = preferencias.getString("usr_log", "");
        if (usr.length() <= 0) {
            return null;
        } else {
            try {
                JSONObject usr_log = new JSONObject(usr);
                return usr_log;
            } catch (JSONException e) {
                e.printStackTrace();
                return null;
            }
        }
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        mPlaceArrayAdapter.setGoogleApiClient(mGoogleApiClient);
        Log.i(LOG_TAG, "Google Places API connected.");
    }

    @Override
    public void onConnectionSuspended(int i) {
        mPlaceArrayAdapter.setGoogleApiClient(null);
        Log.e(LOG_TAG, "Google Places API connection suspended.");
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.e(LOG_TAG, "Google Places API connection failed with error code: "
                + connectionResult.getErrorCode());

        Toast.makeText(this,
                "Google Places API connection failed with error code:" +
                        connectionResult.getErrorCode(),
                Toast.LENGTH_LONG).show();
    }


    private class DownloadTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... url) {

            String data = "";

            try{
                data = downloadUrl(url[0]);
            }catch(Exception e){
                Log.d("ERROR AL OBTENER INFO D",e.toString());
            }
            return data;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            ParserTask parserTask = new ParserTask();

            parserTask.execute(result);
        }
    }

    private class ParserTask extends AsyncTask<String, Integer, List<List<HashMap<String,String>>> > {

        @Override
        protected List<List<HashMap<String, String>>> doInBackground(String... jsonData) {

            JSONObject jObject;
            List<List<HashMap<String, String>>> routes = null;

            try{
                jObject = new JSONObject(jsonData[0]);
                DirectionsJSONParser parser = new DirectionsJSONParser();

                routes = parser.parse(jObject);
            }catch(Exception e){
                e.printStackTrace();
            }
            return routes;
        }

        @Override
        protected void onPostExecute(List<List<HashMap<String, String>>> result) {
            ArrayList<LatLng> points = null;
            PolylineOptions lineOptions = null;
            MarkerOptions markerOptions = new MarkerOptions();

            for(int i=0;i<result.size();i++){
                points = new ArrayList<LatLng>();
                lineOptions = new PolylineOptions();

                List<HashMap<String, String>> path = result.get(i);

                for(int j=0;j<path.size();j++){
                    HashMap<String,String> point = path.get(j);

                    double lat = Double.parseDouble(point.get("lat"));
                    double lng = Double.parseDouble(point.get("lng"));
                    LatLng position = new LatLng(lat, lng);

                    points.add(position);
                }

                //polylineOption
                lineOptions.addAll(points);
                lineOptions.width(5);
                lineOptions.color(Color.rgb(0,0,0));
            }
            if(lineOptions!=null) {
                googleMap.addPolyline(lineOptions);

                int size = points.size() - 1;
                float[] results = new float[1];
                float sum = 0;

                for(int i = 0; i < size; i++){
                    Location.distanceBetween(
                            points.get(i).latitude,
                            points.get(i).longitude,
                            points.get(i+1).latitude,
                            points.get(i+1).longitude,
                            results);
                    sum += results[0];
                }

                try {
                    String resp = new validar_precio(tipo_carrera).execute().get();
                    if(resp==null){
                        Toast.makeText(Calcular_ruta_activity.this,"Error al conectarse con el servidor.", Toast.LENGTH_SHORT).show();
                    }else{
                        JSONObject object = new JSONObject(resp);
                        if(object != null){
                            double costo_metro = object.getDouble("costo_metro");
                            double costo_minuto = object.getDouble("costo_minuto");
                            double costo_basico = object.getDouble("costo_basico");
                            mont = costo_basico + (costo_metro * sum ) + ((sum/500)*costo_minuto);
                            if(aeropuerto){
                                mont+=50;
                            }
                        }else {
                            return;
                        }
                        int montoaux = (int) mont;
                        if(aeropuerto == true){
                            monto.setText("BS. " +(montoaux-1)+" - "+(montoaux+1));
                        }else{
                            monto.setText("BS. " +(montoaux-1)+" - "+(montoaux+1));
                        }

                    }

                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                LatLngBounds.Builder builder = new LatLngBounds.Builder();
                builder.include(points.get(0));
                builder.include(points.get(points.size()-1));
                LatLngBounds bounds=builder.build();
                CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds,100);
                googleMap.moveCamera(cu);
                //sum = metros
            }
        }
    }

    private String downloadUrl(String strUrl) throws IOException {
        String data = "";
        InputStream iStream = null;
        HttpURLConnection urlConnection = null;
        try{
            URL url = new URL(strUrl);

            // Creamos una conexion http
            urlConnection = (HttpURLConnection) url.openConnection();

            // Conectamos
            urlConnection.connect();

            // Leemos desde URL
            iStream = urlConnection.getInputStream();

            BufferedReader br = new BufferedReader(new InputStreamReader(iStream));

            StringBuffer sb = new StringBuffer();

            String line = "";
            while( ( line = br.readLine()) != null){
                sb.append(line);
            }

            data = sb.toString();

            br.close();

        }catch(Exception e){
            Log.d("Exception", e.toString());
        }finally{
            iStream.close();
            urlConnection.disconnect();
        }
        return data;
    }


    private void Confimar_viaje() {
        try {
            String id = usr_log.getString("id");
            String resp =new User_getPerfil(id).execute().get();
            if(resp==null){
                Toast.makeText(Calcular_ruta_activity.this,"Error al conectarse con el servidor.", Toast.LENGTH_SHORT).show();
            }else{
                android.app.FragmentManager fragmentManager = getFragmentManager();
                if (!resp.isEmpty()){
                    JSONObject usr = new JSONObject(resp);
                    if(usr.getString("exito").equals("si")){
                        double credito = usr.getDouble("creditos");
                        boolean acept = true;
                        if(radio_credito.isChecked() == true){
                            tipo_pago=2;
                            if(credito < mont){
                                new Confirmar_viaje_Dialog2().show(fragmentManager, "Dialog");
                                acept=false;
                            }
                        }
                        else {
                            tipo_pago=1;
                            if(credito < 0){
                                new Confirmar_viaje_Dialog(tipo_carrera,credito).show(fragmentManager, "Dialog");
                                //esta en deuda , aler se cobrara el monto + viej
                                acept=false;
                            }
                        }
                        if(acept){
                            ok_pedir_viaje();
                        }
                    }
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
    }

    public void calculando_ruta(int tipo , LatLng latlng1 , LatLng latlng2 ){
        selected=null;
        inicio=latlng1;
        fin=latlng2;
        String url = obtenerDireccionesURL(latlng1,latlng2);
        googleMap.addMarker(new MarkerOptions().position(latlng1).title("INICIO").icon(Inicio_bitmapDescriptorFromVector(this , R.drawable.asetmar)));
        googleMap.addMarker(new MarkerOptions().position(latlng2).title("FIN").icon(Fin_bitmapDescriptorFromVector(this, R.drawable.asetmar)));
        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        builder.include(latlng1);
        builder.include(latlng2);
        LatLngBounds bounds=builder.build();
        int width = getResources().getDisplayMetrics().widthPixels;
        int height = getResources().getDisplayMetrics().heightPixels;
        CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds,width,height,100);

        googleMap.moveCamera(cu);
        Calcular_ruta_activity.DownloadTask downloadTask= new Calcular_ruta_activity.DownloadTask();
        downloadTask.execute(url);
        //linear_confirm.setVisibility(View.VISIBLE);
        mostraConfirmar_icon(tipo);
    }

    private BitmapDescriptor Inicio_bitmapDescriptorFromVector(Context context, @DrawableRes int vectorDrawableResourceId) {
        Drawable background = ContextCompat.getDrawable(context, R.drawable.ic_icon_pointer_map);
        background.setBounds(0, 0, background.getIntrinsicWidth(), background.getIntrinsicHeight());
        Drawable vectorDrawable = ContextCompat.getDrawable(context, vectorDrawableResourceId);
        vectorDrawable.setBounds(40, 20, vectorDrawable.getIntrinsicWidth() + 40, vectorDrawable.getIntrinsicHeight() + 20);
        Bitmap bitmap = Bitmap.createBitmap(background.getIntrinsicWidth(), background.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        background.draw(canvas);
        vectorDrawable.draw(canvas);
        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }

    private BitmapDescriptor Fin_bitmapDescriptorFromVector(Context context, @DrawableRes int vectorDrawableResourceId) {
        Drawable background = ContextCompat.getDrawable(context, R.drawable.ic_icon_pointer_map2);
        background.setBounds(0, 0, background.getIntrinsicWidth(), background.getIntrinsicHeight());
        Drawable vectorDrawable = ContextCompat.getDrawable(context, vectorDrawableResourceId);
        vectorDrawable.setBounds(40, 20, vectorDrawable.getIntrinsicWidth() + 40, vectorDrawable.getIntrinsicHeight() + 20);
        Bitmap bitmap = Bitmap.createBitmap(background.getIntrinsicWidth(), background.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        background.draw(canvas);
        vectorDrawable.draw(canvas);
        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }

    private void mostraConfirmar_icon(int valor){
        switch (valor){
            case 1:
                icono2.setBackground(getApplication().getResources().getDrawable(R.drawable.ic_icon_imoto));
                break;
            case 2:
                icono2.setBackground(getApplication().getResources().getDrawable(R.drawable.ic_icon_imoto));
                break;
        }
    }

    public class validar_precio extends AsyncTask<Void, String, String> {
        private int id;
        public validar_precio(int id ){
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
            parametros.put("evento", "get_costo");
            parametros.put("id",id+"");
            String respuesta = HttpConnection.sendRequest(new StandarRequestConfiguration(getString(R.string.url_servlet_admin), MethodType.POST, parametros));
            return respuesta;
        }
        @Override
        protected void onPostExecute(String resp) {
            super.onPostExecute(resp);
        }
    }

    public class User_getPerfil extends AsyncTask<Void, String, String> {

        private final String id;
        User_getPerfil(String id_usr) {
            id = id_usr;
        }

        @Override
        protected String doInBackground(Void... params) {
            Hashtable<String, String> parametros = new Hashtable<>();
            parametros.put("evento", "get_usuario");
            parametros.put("id",id);
            String respuesta ="";
            try {
                respuesta = HttpConnection.sendRequest(new StandarRequestConfiguration(getString(R.string.url_servlet_index), MethodType.POST, parametros));
            } catch (Exception ex) {
                Log.e(Contexto.APP_TAG, "Hubo un error al conectarse al servidor.");
            }
            return respuesta;
        }
        @Override
        protected void onPostExecute(final String success) {
            super.onPostExecute(success);
            if(success==null){
                Toast.makeText(Calcular_ruta_activity.this,"Error al conectarse con el servidor.", Toast.LENGTH_SHORT).show();
            }else{
                if (!success.isEmpty()){
                    try {
                        JSONObject usr = new JSONObject(success);
                        if(usr.getString("exito").equals("si")) {
                            SharedPreferences preferencias = getSharedPreferences("myPref", MODE_PRIVATE);
                            SharedPreferences.Editor editor = preferencias.edit();
                            editor.putString("usr_log", usr.toString());
                            editor.commit();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }

        }
    }

}
