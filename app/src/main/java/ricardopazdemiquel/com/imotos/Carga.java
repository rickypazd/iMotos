package ricardopazdemiquel.com.imotos;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import com.google.firebase.iid.FirebaseInstanceId;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Hashtable;
import java.util.concurrent.ExecutionException;

import ricardopazdemiquel.com.imotos.clienteHTTP.HttpConnection;
import ricardopazdemiquel.com.imotos.clienteHTTP.MethodType;
import ricardopazdemiquel.com.imotos.clienteHTTP.StandarRequestConfiguration;
import ricardopazdemiquel.com.imotos.utiles.Contexto;
import ricardopazdemiquel.com.imotos.utiles.NetworkStateChangeReceiver;
import ricardopazdemiquel.com.imotos.utiles.Token;

public class Carga extends AppCompatActivity {

    private Intent intenError;
    private boolean isfirst;
    private JSONObject usr_log;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_carga);

        Token.currentToken = FirebaseInstanceId.getInstance().getToken();

        if (isConnectedToInternet(this)) {

            //Intent intent= new Intent(Carga.this,Sin_conexion.class);
            //startActivity(intent);
            /////
            /// if ress{cant:8
            ///         arr:[{
            ///                 },
            ///              {
            ///                 }]
            ///             }
            ///cant == 0 sigue
            // cant >0 && menor que 11 actualiza del arr
            //camt >10  pregunta para actualizar y carga la ventana

        }
        usr_log = getUsr_log();
        try {
            if (usr_log != null) {
                new ValidarSession(usr_log.getInt("id"), Token.currentToken).execute().get();
                new Get_validarCarrera(usr_log.getInt("id")).execute();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
    }

    public void ejecutar() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (usr_log != null) {
                    try {
                        if (usr_log.getInt("id_rol") == 4) {
                            Intent intent = new Intent(Carga.this, PedirSieteMap.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                            intent.putExtra("lng", 0);
                            intent.putExtra("lat", 0);
                            intent.putExtra("tipo", 1);
                            startActivity(intent);
                            finish();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    Intent intent = new Intent(Carga.this, LoginSocial.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    finish();
                }
            }
        }, 2000);

    }

    @Override
    protected void onRestart() {
        super.onRestart();
        this.recreate();
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

    private boolean isConnectedToInternet(Context context) {
        try {
            if (context != null) {
                ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(CONNECTIVITY_SERVICE);
                NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
                return networkInfo != null && networkInfo.isConnected();
            }
            return false;
        } catch (Exception e) {
            Log.e(NetworkStateChangeReceiver.class.getName(), e.getMessage());
            return false;
        }
    }


    public class ValidarSession extends AsyncTask<Void, String, String> {
        private int id;
        private String token;

        public ValidarSession(int id, String token) {
            this.id = id;
            this.token = token;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(Void... params) {
            Hashtable<String, String> parametros = new Hashtable<>();
            parametros.put("evento", "validar_token");
            parametros.put("id_usr", id + "");
            parametros.put("token", token + "");
            String respuesta = HttpConnection.sendRequest(new StandarRequestConfiguration(getString(R.string.url_servlet_index), MethodType.POST, parametros));
            return respuesta;
        }

        @Override
        protected void onPostExecute(String resp) {
            super.onPostExecute(resp);
            if (resp == null) {
                Log.e(Contexto.APP_TAG, "Error al conectarse con el servidor.");
                Toast.makeText(Carga.this, "Error al conectarse con el servidor.", Toast.LENGTH_SHORT).show();
            }else if (resp.isEmpty()) {
                Log.e(Contexto.APP_TAG, "Hubo un error al obtener datos.");
                Toast.makeText(Carga.this, "Hubo un error al obtener datos.", Toast.LENGTH_SHORT).show();
            }else if (resp.contains("falso")) {
                Log.e(Contexto.APP_TAG, "Hubo un error al obtener datos.");
                Toast.makeText(Carga.this, "Hubo un error al obtener datos.", Toast.LENGTH_SHORT).show();
            }else{
                try {
                    JSONArray obj = new JSONArray(resp);
                    if (obj.length() <= 0) {
                        Toast.makeText(Carga.this, "No se encontro la sesión, por favor vuelva a iniciar.", Toast.LENGTH_LONG).show();
                        SharedPreferences preferencias = getSharedPreferences("myPref", MODE_PRIVATE);
                        SharedPreferences.Editor editor = preferencias.edit();
                        editor.putString("usr_log", "");
                        usr_log = null;
                    }
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


    public class Get_validarCarrera extends AsyncTask<Void, String, String> {
        private int id;

        public Get_validarCarrera(int id) {
            this.id = id;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(Void... params) {
            Hashtable<String, String> parametros = new Hashtable<>();
            parametros.put("evento", "get_carrera_cliente");
            parametros.put("id_usr", id + "");
            String respuesta = HttpConnection.sendRequest(new StandarRequestConfiguration(getString(R.string.url_servlet_index), MethodType.POST, parametros));
            return respuesta;
        }

        @Override
        protected void onPostExecute(String resp) {
            super.onPostExecute(resp);
            if (resp == null) {
                Log.e(Contexto.APP_TAG, "Hubo un error al conectarse al servidor.");
                Toast.makeText(Carga.this, "Hubo un error al conectarse al servidor.", Toast.LENGTH_SHORT).show();
                finish();
            } else if (resp.isEmpty()) {
                Log.e(Contexto.APP_TAG, "Hubo un error al obtener datos.");
                Toast.makeText(Carga.this, "Hubo un error al obtener datos.", Toast.LENGTH_SHORT).show();
                finish();
            } else if (resp.contains("falso")) {
                Log.e(Contexto.APP_TAG, "Hubo un error al obtener datos.");
                Toast.makeText(Carga.this, "Hubo un error al obtener datos.", Toast.LENGTH_SHORT).show();
            } else {
                try {
                    JSONObject obj = new JSONObject(resp);
                    if (obj.getBoolean("exito")) {
                        if (obj.getInt("id_tipo") == 2) {//togo
                            Intent intent = new Intent(Carga.this, EsperandoConductor.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                            intent.putExtra("obj_carrera", obj.toString());
                            startActivity(intent);
                        } else {
                            Intent intent = new Intent(Carga.this, EsperandoConductor.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                            intent.putExtra("obj_carrera", obj.toString());
                            startActivity(intent);
                        }
                    } else {
                        SharedPreferences preferencias = getSharedPreferences("myPref", MODE_PRIVATE);
                        SharedPreferences.Editor editor = preferencias.edit();
                        editor.putString("chat_carrera", new JSONArray().toString());
                        editor.commit();
                        ejecutar();
                    }
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