package ricardopazdemiquel.com.imotos;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Hashtable;

import ricardopazdemiquel.com.imotos.clienteHTTP.HttpConnection;
import ricardopazdemiquel.com.imotos.clienteHTTP.MethodType;
import ricardopazdemiquel.com.imotos.clienteHTTP.StandarRequestConfiguration;
import ricardopazdemiquel.com.imotos.utiles.Contexto;

public class Perfil_ClienteFragment extends AppCompatActivity implements View.OnClickListener{

    private static final String TAG ="fragment_explorar";

    private TextView textNombre;
    private TextView textApellido;
    private TextView textTelefono;
    private TextView textEmail;
    private TextView textcredito;
    private com.mikhaellopez.circularimageview.CircularImageView img_photo;


    private LinearLayout Liner_nombre;
    private LinearLayout Liner_apellido;
    private LinearLayout Liner_telefono;
    private LinearLayout Liner_correo;

    @Override
    protected void onCreate(Bundle onSaveInstanceState) {
        super.onCreate(onSaveInstanceState);
        setContentView(R.layout.fragment_perfil_cliente);

        Toolbar toolbar = findViewById(R.id.toolbar3);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_left_arrow);

        textNombre = findViewById(R.id.text_nombreCliente);
        textApellido = findViewById(R.id.text_apellidoCliente);
        textTelefono = findViewById(R.id.text_numero_telefono);
        textEmail = findViewById(R.id.text_email_cliente);
        img_photo = findViewById(R.id.img_photo);

        textcredito = findViewById(R.id.creditos);

        Liner_nombre = findViewById(R.id.Liner_nombre);
        Liner_apellido = findViewById(R.id.Liner_apellido);
        Liner_telefono = findViewById(R.id.Liner_telefono);
        Liner_correo = findViewById(R.id.Liner_correo);

        Liner_nombre.setOnClickListener(this);
        Liner_apellido.setOnClickListener(this);
        Liner_telefono.setOnClickListener(this);
        Liner_correo.setOnClickListener(this);


        final JSONObject usr_log = getUsr_log();
        if (usr_log != null) {
            try {
                new User_getPerfil(usr_log.getString("id")).execute();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
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
        final JSONObject usr_log = getUsr_log();
        if (usr_log != null) {
            try {
                new User_getPerfil(usr_log.getString("id")).execute();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onClick(View view) {
        Intent intent = new Intent(Perfil_ClienteFragment.this , Editar_perfil_Activity.class);
        final JSONObject usr_log = getUsr_log();
        switch (view.getId()) {
            case R.id.Liner_nombre:
                if (usr_log != null) {
                    try {
                        String nombre = usr_log.getString("nombre");
                        intent.putExtra("nombre", nombre);
                        intent.putExtra("tipo", "nombre_usuario");
                        startActivity(intent);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    finish();
                }
                break;
            case R.id.Liner_apellido:
                if (usr_log != null) {
                    try {
                        String apellido_pa = usr_log.getString("apellido_pa");
                        String apellido_ma = usr_log.getString("apellido_ma");
                        intent.putExtra("apellido_pa", apellido_pa);
                        intent.putExtra("apellido_ma", apellido_ma);
                        intent.putExtra("tipo", "apellido_usuario");
                        startActivity(intent);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    finish();
                }
                break;
            case R.id.Liner_telefono:
                if (usr_log != null) {
                    try {
                        String telefono = usr_log.getString("telefono");
                        intent.putExtra("telefono", telefono);
                        intent.putExtra("tipo", "telefono_usuario");
                        startActivity(intent);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    finish();
                }
                break;
            case R.id.Liner_correo:
                if (usr_log != null) {
                    try {
                        String correo = usr_log.getString("correo");
                        intent.putExtra("correo", correo);
                        intent.putExtra("tipo", "correo_usuario");
                        startActivity(intent);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    finish();
                }
                break;

        }
    }

    private void cargarUsuario(){
        final JSONObject usr_log = getUsr_log();
        if (usr_log != null) {
            try {
                String nombre = usr_log.getString("nombre");
                String apellido_pa = usr_log.getString("apellido_pa");
                String apellido_ma = usr_log.getString("apellido_ma");
                String telefono= usr_log.getString("telefono");
                String correo = usr_log.getString("correo");
                String credito = usr_log.getString("creditos");
                Double valor = Double.valueOf(credito);
                textNombre.setText(nombre);
                textApellido.setText(apellido_pa+" "+apellido_ma);
                textTelefono.setText("+591 "+telefono);
                textEmail.setText(correo);
                textcredito.setText(String.format("%.2f",valor));
                if(usr_log.getString("foto_perfil").length()>0){
                    new AsyncTaskLoadImage(img_photo).execute(getString(R.string.url_foto)+usr_log.getString("foto_perfil"));
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }else{
            finish();
        }
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


    public class User_getPerfil extends AsyncTask<Void, String, String> {

        private ProgressDialog progreso;
        private final String id;
        User_getPerfil(String id_usr) {
            id = id_usr;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progreso = new ProgressDialog(Perfil_ClienteFragment.this);
            progreso.setIndeterminate(true);
            progreso.setTitle("Esperando Respuesta");
            progreso.setCancelable(false);
            progreso.show();
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
            progreso.dismiss();
            if(success == null ){
                Toast.makeText(Perfil_ClienteFragment.this,"Hubo un error al conectarse al servidor.", Toast.LENGTH_SHORT).show();
                Log.e(Contexto.APP_TAG, "Hubo un error al conectarse al servidor.");
            }else if (!success.isEmpty()){
                try {
                    JSONObject usr = new JSONObject(success);
                    if(usr.getString("exito").equals("si")){
                        SharedPreferences preferencias = getSharedPreferences("myPref",MODE_PRIVATE);
                        SharedPreferences.Editor editor = preferencias.edit();
                        editor.putString("usr_log", usr.toString());
                        editor.commit();
                        cargarUsuario();
                    }else{
                        Toast.makeText(Perfil_ClienteFragment.this,"Error al obtener Datos", Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }else{
                Toast.makeText(Perfil_ClienteFragment.this,"Error al obtener Datos", Toast.LENGTH_SHORT).show();
            }
        }
        @Override
        protected void onProgressUpdate(String... values) {
            super.onProgressUpdate(values);

        }
    }

    public class AsyncTaskLoadImage  extends AsyncTask<String, String, Bitmap> {
        private final static String TAG = "AsyncTaskLoadImage";
        private ImageView imageView;
        public AsyncTaskLoadImage(ImageView imageView) {
            this.imageView = imageView;
        }
        @Override
        protected Bitmap doInBackground(String... params) {
            Bitmap bitmap = null;
            try {
                URL url = new URL(params[0]);
                bitmap = BitmapFactory.decodeStream((InputStream)url.getContent());
            } catch (IOException e) {
                Log.e(TAG, e.getMessage());
            }
            return bitmap;
        }
        @Override
        protected void onPostExecute(Bitmap bitmap) {
            imageView.setImageBitmap(bitmap);
        }
    }


}
