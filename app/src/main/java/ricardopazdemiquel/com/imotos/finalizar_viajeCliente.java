package ricardopazdemiquel.com.imotos;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RatingBar;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Hashtable;

import ricardopazdemiquel.com.imotos.clienteHTTP.HttpConnection;
import ricardopazdemiquel.com.imotos.clienteHTTP.MethodType;
import ricardopazdemiquel.com.imotos.clienteHTTP.StandarRequestConfiguration;
import ricardopazdemiquel.com.imotos.utiles.Contexto;

public class finalizar_viajeCliente extends AppCompatActivity implements View.OnClickListener {

    // private Button nombre;

    private RatingBar ratingBar;
    //private int id_carrera;
    private JSONObject carrera;
    Fragment fragment_1 = null;
    //Fragment fragment_2  = null;
    private float ratings;
    private int frag;
    private Button btn_enviar_mensaje;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_finalizar_viajecliente);

        String res = getIntent().getStringExtra("carrera");
        //id_carrera = Integer.parseInt(getIntent().getStringExtra("id_carrera"));
        try {
            carrera = new JSONObject(res);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        btn_enviar_mensaje = findViewById(R.id.btn_enviar_mensaje);
        btn_enviar_mensaje.setOnClickListener(this);

        ratingBar = findViewById(R.id.ratingBar);
        fragment_1 = new FinalizarViajeFragment_1();
        //fragment_2= new FinalizarViajeFragment_2();
        getSupportFragmentManager().beginTransaction().add(R.id.contenedorFragment, fragment_1).commit();

        ratingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                ratings = rating;
                /*if(frag == 0){
                    frag = getSupportFragmentManager().beginTransaction().add(R.id.contenedorFragment,fragment_2).commit();
                }*/
            }
        });
    }

    public JSONObject get_carrera() {
        return carrera;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_enviar_mensaje:
                try {
                    new Finalizo(carrera.getInt("id"), ratings, "", false, false, false);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                break;
        }
    }

    //asyncTask Finalizo carrera
    private class Finalizo extends AsyncTask<Void, String, String> {

        private ProgressDialog progreso;
        private int id_carrera;
        private float calificacion;
        private String mensaje;
        boolean amable;
        boolean auto_limpio;
        boolean buena_ruta;

        public Finalizo(int id_carrera, float finalizo, String mensaje, boolean amable, boolean auto_limpio, boolean buena_ruta) {
            this.id_carrera = id_carrera;
            this.calificacion = finalizo;
            this.amable = amable;
            this.auto_limpio = auto_limpio;
            this.buena_ruta = buena_ruta;
            this.mensaje = mensaje;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progreso = new ProgressDialog(finalizar_viajeCliente.this);
            progreso.setIndeterminate(true);
            progreso.setTitle("Esperando Respuesta");
            progreso.setCancelable(false);
            progreso.show();
        }

        @Override
        protected String doInBackground(Void... params) {
            publishProgress("por favor espere...");
            Hashtable<String, String> parametros = new Hashtable<>();
            parametros.put("evento", "finalizo_carrera_cliente");
            parametros.put("id_carrera", id_carrera + "");
            parametros.put("calificacion", calificacion + "");
            parametros.put("amable", amable + "");
            parametros.put("auto_limpio", auto_limpio + "");
            parametros.put("buena_ruta", buena_ruta + "");
            parametros.put("mensaje", mensaje + "");
            String respuesta = HttpConnection.sendRequest(new StandarRequestConfiguration(getString(R.string.url_servlet_index), MethodType.POST, parametros));
            return respuesta;
        }

        @Override
        protected void onPostExecute(String resp) {
            super.onPostExecute(resp);
            progreso.dismiss();
            if (resp != null) {
                if (resp.equals("falso")) {
                    Log.e(Contexto.APP_TAG, "Hubo un error al conectarse al servidor.");
                    return;
                } else {
                    //new MapCarrera.buscar_carrera().execute();
                    Intent intent = new Intent(finalizar_viajeCliente.this, PedirSieteMap.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    finish();
                }
            }
        }

        @Override
        protected void onProgressUpdate(String... values) {
            super.onProgressUpdate(values);

        }
    }
}
