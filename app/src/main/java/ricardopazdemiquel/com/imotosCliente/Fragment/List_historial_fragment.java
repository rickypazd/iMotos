package ricardopazdemiquel.com.imotosCliente.Fragment;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import ricardopazdemiquel.com.imotosCliente.Adapter.Adapter_historial;
import ricardopazdemiquel.com.imotosCliente.PedirSieteMap;
import ricardopazdemiquel.com.imotos.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Hashtable;
import java.util.List;
import java.util.Locale;

import ricardopazdemiquel.com.imotosCliente.clienteHTTP.HttpConnection;
import ricardopazdemiquel.com.imotosCliente.clienteHTTP.MethodType;
import ricardopazdemiquel.com.imotosCliente.clienteHTTP.StandarRequestConfiguration;
import ricardopazdemiquel.com.imotosCliente.utiles.Contexto;

public class List_historial_fragment extends Fragment implements View.OnClickListener{

    private static final String TAG ="fragment_explorar";
    private ListView lv;
    private ImageView close_fragment;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_historial, container, false);

        lv = view.findViewById(R.id.lista_historial);
        close_fragment = view.findViewById(R.id.close_fragment);
        close_fragment.setOnClickListener(this);

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                try {
                    JSONObject obj = new JSONObject(view.getTag().toString());
                    Double latFin = obj.getDouble("latfinal");
                    Double lngFin = obj.getDouble("lngfinal");
                    ((PedirSieteMap)getActivity()).Verificar_tipo_siete(10,getCompleteAddressString(latFin,lngFin),latFin, lngFin);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

        new get_historial().execute();

        lv.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(event.getAction()== MotionEvent.ACTION_DOWN){
                    ((PedirSieteMap)getActivity()).close();
                }else if(event.getAction()== MotionEvent.ACTION_UP){
                    ((PedirSieteMap)getActivity()).open();
                }else if(event.getAction()== MotionEvent.ACTION_CANCEL){
                    ((PedirSieteMap)getActivity()).open();
                }
                return false;
            }
        });

        return view;
    }


    private String getCompleteAddressString(double LATITUDE, double LONGITUDE) {
        String strAdd = "";
        Geocoder geocoder = new Geocoder(getActivity(), Locale.getDefault());
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

    public JSONObject getUsr_log() {
        SharedPreferences preferencias = getActivity().getSharedPreferences("myPref", Context.MODE_PRIVATE);
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
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.close_fragment:
                ((PedirSieteMap)getActivity()).close_behavior();
                break;
        }
    }

    public class get_historial extends AsyncTask<Void, String, String> {

        String id_usr;
        {
            try {
                id_usr = getUsr_log().getString("id");
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        private ProgressDialog progreso;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progreso = new ProgressDialog(getActivity());
            progreso.setIndeterminate(true);
            progreso.setTitle("Esperando Respuesta");
            progreso.setCancelable(false);
            progreso.show();
        }
        @Override
        protected String doInBackground(Void... params) {
            publishProgress("por favor espere...");
            Hashtable<String,String> param = new Hashtable<>();
            param.put("evento","get_historial_ubic");
            param.put("id", id_usr);
            String respuesta ="";
            try {
                respuesta = HttpConnection.sendRequest(new StandarRequestConfiguration(getString(R.string.url_servlet_index), MethodType.POST, param));
            } catch (Exception e) {
                Log.e(Contexto.APP_TAG, "Hubo un error al conectarse al servidor.");
            }
            return respuesta;
        }
        @Override
        protected void onPostExecute(String resp) {
            super.onPostExecute(resp);
            progreso.dismiss();
            if(resp == null){
                Toast.makeText(getActivity(),"Error al conectarse con el servidor.", Toast.LENGTH_SHORT).show();
                Log.e(Contexto.APP_TAG, "Hubo un error al conectarse al servidor.");
            }else if(resp.isEmpty()){
                Toast.makeText(getActivity(),"Error al obtener datos", Toast.LENGTH_SHORT).show();
            }else {
                try {
                    JSONArray array  = new JSONArray(resp);
                    Adapter_historial adapter = new Adapter_historial(getActivity(),array);
                    lv.setAdapter(adapter);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
        @Override
        protected void onProgressUpdate(String... values) {
            super.onProgressUpdate(values);
            progreso.setMessage(values[0]);
        }
    }

}
