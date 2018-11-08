package ricardopazdemiquel.com.imotos;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import static android.content.Context.MODE_PRIVATE;

public class Elejir_tipo_siete extends Fragment implements View.OnClickListener {

    private Button siete;
    private Button siete_maravilla;
    private Button siete_super;
    private Button siete_togo;
    private Activity activity;
    double longitudeGPS, latitudeGPS;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_elejir_tipo_siete, container, false);

        longitudeGPS = getActivity().getIntent().getDoubleExtra("lng",0);
        latitudeGPS = getActivity().getIntent().getDoubleExtra("lat",0);

        siete = view.findViewById(R.id.btn_siete);
        siete_maravilla = view.findViewById(R.id.btn_sieteMaravilla);
        siete_super = view.findViewById(R.id.btn_superSiete);
        siete_togo = view.findViewById(R.id.btn_togo);

        siete.setOnClickListener(this);
        siete_maravilla.setOnClickListener(this);
        siete_super.setOnClickListener(this);
        siete_togo.setOnClickListener(this);

        return view;
    }

    /*@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_elejir_tipo_siete);

        longitudeGPS = getIntent().getDoubleExtra("lng",0);
        latitudeGPS = getIntent().getDoubleExtra("lat",0);

        siete = findViewById(R.id.btn_siete);
        siete_maravilla = findViewById(R.id.btn_sieteMaravilla);
        siete_super = findViewById(R.id.btn_superSiete);
        siete_togo = findViewById(R.id.btn_togo);

        siete.setOnClickListener(this);
        siete_maravilla.setOnClickListener(this);
        siete_super.setOnClickListener(this);
        siete_togo.setOnClickListener(this);
    }*/

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_siete:
                    Intent intent = new Intent(getActivity(), PedirSieteMap.class);
                    intent.putExtra("lng", longitudeGPS);
                    intent.putExtra("lat", latitudeGPS);
                    intent.putExtra("tipo", 1);
                    startActivity(intent);
                break;
            case R.id.btn_superSiete:
                Intent intent1 = new Intent(getActivity(), PedirSieteMap.class);
                    intent1.putExtra("lng", longitudeGPS);
                    intent1.putExtra("lat", latitudeGPS);
                    intent1.putExtra("tipo", 4);
                    startActivity(intent1);

                break;
            case R.id.btn_sieteMaravilla:
                JSONObject obj = getUsr_log();
                try {
                    if(obj.getString("sexo").equals("Mujer")){
                        Intent intent2 = new Intent(getActivity(), PedirSieteMap.class);
                        intent2.putExtra("lng", longitudeGPS);
                        intent2.putExtra("lat", latitudeGPS);
                        intent2.putExtra("tipo", 3);
                        startActivity(intent2);
                    }else{
                        Toast.makeText(getActivity(),"Usted no puede tener esta opción.", Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                break;
            case R.id.btn_togo:
                Intent intent_togo = new Intent(getActivity(), PedirSieteTogo.class);
                    intent_togo.putExtra("lng", longitudeGPS);
                    intent_togo.putExtra("lat", latitudeGPS);
                    intent_togo.putExtra("tipo", 2);
                    startActivity(intent_togo);
                break;
        }
    }

    public JSONObject getUsr_log() {
        SharedPreferences preferencias = getActivity().getSharedPreferences("myPref", MODE_PRIVATE);
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
}