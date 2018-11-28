package ricardopazdemiquel.com.imotos;

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

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Hashtable;

import ricardopazdemiquel.com.imotos.clienteHTTP.HttpConnection;
import ricardopazdemiquel.com.imotos.clienteHTTP.MethodType;
import ricardopazdemiquel.com.imotos.clienteHTTP.StandarRequestConfiguration;
import ricardopazdemiquel.com.imotos.utiles.Token;

public class Preferencias extends AppCompatActivity implements View.OnClickListener{


    private LinearLayout liner_ver_perfil;
    private LinearLayout liner_sign_out;
    private TextView text_nombre;
    private TextView text_apellidos;
    private com.mikhaellopez.circularimageview.CircularImageView img_photo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preferencias);

        Toolbar toolbar = findViewById(R.id.toolbar3);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_left_arrow);

        liner_ver_perfil = findViewById(R.id.liner_ver_perfil);
        liner_sign_out = findViewById(R.id.liner_sign_out);
        text_nombre = findViewById(R.id.text_nombre);
        text_apellidos = findViewById(R.id.text_apellidos);
        img_photo = findViewById(R.id.img_photo);
        liner_sign_out.setOnClickListener(this);
        liner_ver_perfil.setOnClickListener(this);

        final JSONObject usr_log = getUsr_log();
        if (usr_log != null) {
            try {
                String nombre = usr_log.getString("nombre");
                String apellido_pa = usr_log.getString("apellido_pa");
                String apellido_ma = usr_log.getString("apellido_ma");
                text_nombre.setText(nombre);
                text_apellidos.setText(apellido_pa+" "+apellido_ma);
                if(usr_log.getString("foto_perfil").length()>0){
                    new AsyncTaskLoadImage(img_photo).execute(getString(R.string.url_foto)+usr_log.getString("foto_perfil"));
                }
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
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.liner_sign_out:
                JSONObject usr = getUsr_log();
                SharedPreferences preferencias = getSharedPreferences("myPref",MODE_PRIVATE);
                SharedPreferences.Editor editor = preferencias.edit();
                editor.putString("usr_log", "");
                editor.commit();
                try {
                    new Desconectarse(usr.getInt("id"),Token.currentToken).execute();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                Intent intent = new Intent(Preferencias.this, LoginSocial.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);

                break;
            case R.id.liner_ver_perfil:
                Intent intent2 = new Intent(Preferencias.this, Perfil_ClienteFragment.class);
                startActivity(intent2);
                finish();
                break;
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

    private class Desconectarse extends AsyncTask<Void, String, String> {
        private int  id  ;
        private String nombre ;
        public Desconectarse(int id ,String nombre ) {
            this.id= id;
            this.nombre= nombre;

        }
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(Void... params) {
            Hashtable<String,String> param = new Hashtable<>();
            param.put("evento","desconectarse");
            param.put("id",id+"");
            param.put("token",nombre);
            String respuesta = HttpConnection.sendRequest(new StandarRequestConfiguration(getString(R.string.url_servlet_index), MethodType.POST, param));
            return respuesta;
        }

        @Override
        protected void onPostExecute(String pacientes) {
            super.onPostExecute(pacientes);

        }
        @Override
        protected void onProgressUpdate(String... values) {
            super.onProgressUpdate(values);
        }
    }

}
