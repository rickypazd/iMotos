package ricardopazdemiquel.com.imotosCliente.Dialog;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import ricardopazdemiquel.com.imotosCliente.Inicio_viaje_togo;
import ricardopazdemiquel.com.imotos.R;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Edson on 02/12/2017.
 */

@SuppressLint("ValidFragment")
public class Cancelar_viaje_togo_Dialog extends DialogFragment implements View.OnClickListener {

    private Button btn_cancelar;
    private Button btn_confirmar_cancelacion;
    private TextView text_mesaje;

    public static String APP_TAG = "registro";

    private static final String TAG = Cancelar_viaje_togo_Dialog.class.getSimpleName();
    private JSONObject obj;

    @SuppressLint("ValidFragment")
    public Cancelar_viaje_togo_Dialog(JSONObject json_cancelarViaje) {
        this.obj=json_cancelarViaje;
    }


    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        return createLoginDialogo();
    }

    public AlertDialog createLoginDialogo() {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        //AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), R.style.DialogFragmanetstyle);
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View v = inflater.inflate(R.layout.dialog_cancelar_viaje_togo, null);
        builder.setView(v);

        btn_confirmar_cancelacion = v.findViewById(R.id.btn_cancelar);
        btn_cancelar = v.findViewById(R.id.btn_confirmar_cancelacion);
        text_mesaje = v.findViewById(R.id.text_mensaje);

        btn_cancelar.setOnClickListener(this);
        btn_confirmar_cancelacion.setOnClickListener(this);


        try {
            boolean tipo  = obj.getBoolean("cobro");
            int monto = obj.getInt("total");
            if(tipo){
                text_mesaje.setText("Se te cobrará Bs. "+monto +" por la cancelación.");
            }else{
                text_mesaje.setText("Todavía puedes cancelar este viaje sin costo.");
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }


        return builder.create();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_cancelar:
                    ((Inicio_viaje_togo)getActivity()).confirmar();
                    dismiss();
                break;
            case R.id.btn_confirmar_cancelacion:
                dismiss();
                break;
        }
    }

 }


