package ricardopazdemiquel.com.imotosCliente;

import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;
import java.util.Locale;

public class FinalizarViajeFragment_1 extends Fragment implements View.OnClickListener {

    private static final String TAG ="fragment_explorar";
    private JSONObject carrera;

    private TextView textNombre;
    private TextView textplaca;
    private TextView textInicio;
    private TextView textFin;
    private Button btn_enviar_mensaje;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_finalizar_viaje_fragment_1, container, false);

        textNombre = view.findViewById(R.id.text_nombre);
        textplaca = view.findViewById(R.id.text_placa);
        textInicio = view.findViewById(R.id.text_inicio);
        textFin = view.findViewById(R.id.text_direccion_final);
        btn_enviar_mensaje = view.findViewById(R.id.btn_enviar_mensaje);
        btn_enviar_mensaje.setOnClickListener(this);

        carrera=((finalizar_viajeCliente)getActivity()).get_carrera();
        cargar();
        return view;
    }


    private void cargar(){
        try {
            String nombre  = carrera.getString("nombre");
            String apellidoP  = carrera.getString("apellido_pa");
            String apellidoM  = carrera.getString("apellido_ma");
            String placa  = carrera.getString("placa");
            String telefono  = carrera.getString("telefono");
            double lat_i= carrera.getDouble("latinicial");
            double lat_f  = carrera.getDouble("latfinal");
            double lng_i = carrera.getDouble("lnginicial");
            double lng_f = carrera.getDouble("lngfinal");
            String inicial = get_localizacion(lat_i , lng_i).replaceAll("\n","");
            String finales =  get_localizacion(lat_f , lng_f).replaceAll("\n","");


            /*switch (tipo) {
                case (EFECTIVO):
                    tipo_pago.setText("Efectivo");
                    break;
                case (CREDITO):
                    tipo_pago.setText("Credito");
                    break;
            }*/

            /*switch (tipo_carrera) {
                case 1:
                    text_tipo_carrera.setText("Siete Estandar");
                    break;
                case 2:
                    tipo_pago.setText("Siete To go");
                    break;
                case 3:
                    tipo_pago.setText("Siete Maravilla");
                    break;
                case 4:
                    tipo_pago.setText("Super Siete");
                    break;
                case 5:
                    tipo_pago.setText("Siete 4x4");
                    break;
                case 6:
                    tipo_pago.setText("Siete Camioneta");
                    break;
                case 7:
                    tipo_pago.setText("Siete 3 filas");
                    break;
            }*/



            textNombre.setText(nombre+" "+apellidoP+" "+apellidoM);
            textplaca.setText(placa + " ° " + telefono);
            textInicio.setText(inicial);
            textFin.setText(finales);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private String get_localizacion(double LATITUDE, double LONGITUDE) {
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

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_enviar_mensaje:
                ((finalizar_viajeCliente)getActivity()).finalizo_carrera();
                break;
        }
    }
}
