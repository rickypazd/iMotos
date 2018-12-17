package ricardopazdemiquel.com.imotosCliente;

import android.content.BroadcastReceiver;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Hashtable;

import ricardopazdemiquel.com.imotosCliente.clienteHTTP.HttpConnection;
import ricardopazdemiquel.com.imotosCliente.clienteHTTP.MethodType;
import ricardopazdemiquel.com.imotosCliente.clienteHTTP.StandarRequestConfiguration;
import ricardopazdemiquel.com.imotosCliente.utiles.Contexto;
import ricardopazdemiquel.com.imotosCliente.utiles.Token;

public class PidiendoSiete extends AppCompatActivity {
    private String latInicio;
    private String lngInicio;
    private String latFin;
    private String lngFin;
    private String token;
    private String id_usr;
    private String tipoCarrera;
    private String tipo_pago;
    private String mensaje;
    private static final int iMotos_mensaje = 2;
    private int tipo;
    private BroadcastReceiver broadcastReceiverConfirmoCarrera;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(ricardopazdemiquel.com.imotos.R.layout.activity_pidiendo_siete);

        Intent intent= getIntent();

        tipoCarrera = intent.getStringExtra("tipo");
        tipo = Integer.valueOf(tipoCarrera);
        if(tipo == iMotos_mensaje){
            latFin=intent.getStringExtra("latFin");
            lngFin=intent.getStringExtra("lngFin");
            latInicio=intent.getStringExtra("latInicio");
            lngInicio=intent.getStringExtra("lngInicio");
            token=intent.getStringExtra("token");
            id_usr=intent.getStringExtra("id_usr");
            mensaje = intent.getStringExtra("mensaje");
            tipo_pago = intent.getStringExtra("tipo_pago");
        }else{
            latInicio=intent.getStringExtra("latInicio");
            lngInicio=intent.getStringExtra("lngInicio");
            latFin=intent.getStringExtra("latFin");
            lngFin=intent.getStringExtra("lngFin");
            token=intent.getStringExtra("token");
            id_usr=intent.getStringExtra("id_usr");
            tipo_pago = intent.getStringExtra("tipo_pago");
        }
        new Get_ActualizarToken(id_usr).execute();
    }

    @Override
    public void onBackPressed() {
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    public class Get_ActualizarToken extends AsyncTask<Void, String, String> {
        private String id;
        public Get_ActualizarToken(String id){
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
            parametros.put("evento", "actualizar_token");
            parametros.put("id_usr",id);
            parametros.put("token", Token.currentToken);
            String respuesta = HttpConnection.sendRequest(new StandarRequestConfiguration(getString(ricardopazdemiquel.com.imotos.R.string.url_servlet_admin), MethodType.POST, parametros));
            return respuesta;
        }
        @Override
        protected void onPostExecute(String resp) {
            super.onPostExecute(resp);
            if(tipo == iMotos_mensaje){
                new buscar_carrera_togo().execute();
            }else{
                new buscar_carrera().execute();
            }
        }
        @Override
        protected void onProgressUpdate(String... values) {
            super.onProgressUpdate(values);
        }
    }

    private class buscar_carrera extends AsyncTask<Void, String, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(Void... params) {

            Hashtable<String,String> param = new Hashtable<>();
            param.put("evento","buscar_carrera");
            param.put("latInicio",latInicio);
            param.put("lngInicio",lngInicio);
            param.put("latFin",latFin);
            param.put("lngFin",lngFin);
            param.put("token", token);
            param.put("id",id_usr);
            param.put("tipo",tipoCarrera);
            param.put("tipo_pago",tipo_pago);

            String respuesta = HttpConnection.sendRequest(new StandarRequestConfiguration(getString(ricardopazdemiquel.com.imotos.R.string.url_servlet_index), MethodType.POST, param));
            return respuesta;
        }

        @Override
        protected void onPostExecute(String Resp) {
            super.onPostExecute(Resp);
            if(Resp == null){
                Toast.makeText(PidiendoSiete.this,"Hubo un error al conectarse al servidor.", Toast.LENGTH_SHORT).show();
                Log.e(Contexto.APP_TAG, "Hubo un error al conectarse al servidor.");
            }else if (Resp.equals("falso")) {
                Toast.makeText(PidiendoSiete.this,"No encontramos conductores cerca.", Toast.LENGTH_SHORT).show();
                finish();
            }else{
                try {
                    JSONObject obj = new JSONObject(Resp);
                    Intent inte = new Intent(PidiendoSiete.this,EsperandoConductor.class);
                    inte.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    inte.putExtra("obj_carrera",obj.toString());
                    startActivity(inte);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }

        @Override
        protected void onProgressUpdate(String... values) {
            super.onProgressUpdate(values);

        }
    }

    private class buscar_carrera_togo extends AsyncTask<Void, String, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(Void... params) {

            Hashtable<String,String> param = new Hashtable<>();
            param.put("evento","buscar_carrera");
            param.put("latFin",latFin);
            param.put("lngFin",lngFin);
            param.put("latInicio",latInicio);
            param.put("lngInicio",lngInicio);
            param.put("token", token);
            param.put("id",id_usr);
            param.put("tipo",tipoCarrera);
            param.put("tipo_pago",tipo_pago);
            param.put("productos_str" , mensaje);
            String respuesta = HttpConnection.sendRequest(new StandarRequestConfiguration(getString(ricardopazdemiquel.com.imotos.R.string.url_servlet_index), MethodType.POST, param));
            return respuesta;
        }

        @Override
        protected void onPostExecute(String Resp) {
            super.onPostExecute(Resp);
            if(Resp == null){
                Toast.makeText(PidiendoSiete.this,"Hubo un error al conectarse al servidor.", Toast.LENGTH_SHORT).show();
                Log.e(Contexto.APP_TAG, "Hubo un error al conectarse al servidor.");
                finish();
            }else if (Resp.equals("falso")) {
                Toast.makeText(PidiendoSiete.this,"Error al obtener datos.", Toast.LENGTH_SHORT).show();
                finish();
            }else{
                try {
                    JSONObject obj = new JSONObject(Resp);
                    Intent inte = new Intent(PidiendoSiete.this,EsperandoConductor.class);
                    inte.putExtra("obj_carrera",obj.toString());
                    startActivity(inte);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }

        @Override
        protected void onProgressUpdate(String... values) {
            super.onProgressUpdate(values);

        }
    }


}
