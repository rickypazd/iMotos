package ricardopazdemiquel.com.imotosCliente.Adapter;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Geocoder;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;
import java.util.Locale;

import ricardopazdemiquel.com.imotos.R;

public class Adapter_favoritos extends BaseAdapter {

    private Context contexto;
    private JSONArray array = new JSONArray();
    Double latFin ;
    Double lngFin ;
    MenuItem item;

    public Adapter_favoritos(Context contexto, JSONArray array) {
        this.contexto = contexto;
        this.array = array;
    }

    public JSONArray getArray(){
        return array;
    }
    @Override
    public int getCount() {
        return array.length();
    }

    @Override
    public JSONObject getItem(int i) {
        try {
            return array.getJSONObject(i);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }


    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        if (view == null) {
            view = LayoutInflater.from(contexto).inflate(R.layout.layout_item_favoritos , viewGroup, false);
        }

        /*view.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if( MotionEvent.ACTION_DOWN);
                view.setBackgroundColor(Color.parseColor("#EEEEEE"));
                return false;
            }
        });*/

        TextView text_nombre = view.findViewById(R.id.text_nombre);
        TextView text_ubicacion = view.findViewById(R.id.text_ubicacion);

        try {
            JSONObject obj =  array.getJSONObject(i);

            if(obj.getString("nombre_favorito").equals("Aeropuerto")){
                text_nombre.setText(obj.getString("nombre_favorito"));
                latFin = obj.getDouble("latFin");
                lngFin = obj.getDouble("lngFin");
                String ubicacion = get_localizacion(latFin,lngFin);
                text_ubicacion.setText(ubicacion.replaceAll("\n",""));
                Drawable img = contexto.getResources().getDrawable(R.drawable.ic_icon_avion);
                img.setBounds(0, 0, (int)text_nombre.getTextSize()+5, (int)text_nombre.getTextSize()+5);
                text_nombre.setCompoundDrawables(img, null, null, null);
                view.setTag(obj.toString());
            }else{
                latFin = obj.getDouble("latFin");
                lngFin = obj.getDouble("lngFin");
                String ubicacion = get_localizacion(latFin,lngFin);
                text_nombre.setText(obj.getString("nombre_favorito"));
                text_ubicacion.setText(ubicacion.replaceAll("\n",""));
                view.setTag(obj.toString());
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        /*view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                view.getTag().toString();
                try {
                    JSONObject obj = new JSONObject(view.getTag().toString());
                    latFin = obj.getDouble("latFin");
                    lngFin = obj.getDouble("lngFin");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                ((PedirSieteMap)contexto).addpositionFavorito(latFin, lngFin);
            }
        });*/
        return view;
    }

    private String get_localizacion(double LATITUDE, double LONGITUDE) {
        String strAdd = "";
        Geocoder geocoder = new Geocoder(contexto, Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocation(LATITUDE, LONGITUDE, 1);
            if (addresses != null) {
                Address returnedAddress = addresses.get(0);
                StringBuilder strReturnedAddress = new StringBuilder("");

                for (int i = 0; i <= returnedAddress.getMaxAddressLineIndex(); i++) {
                    strReturnedAddress.append(returnedAddress.getAddressLine(i)).append("\n");
                }
                strAdd = strReturnedAddress.toString();
                Log.w("My Current loction addr", strReturnedAddress.toString());
            } else {
                Log.w("My Current loction addr", "No Address returned!");
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.w("My Current loction addr", "Canont get Address!");
        }
        return strAdd;
    }


    private String getCompleteAddressString(double LATITUDE, double LONGITUDE) {
        String strAdd = "";
        Geocoder geocoder = new Geocoder(contexto, Locale.getDefault());
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

    public void addItem(JSONObject obj){
        if(array!=null){
            array.put(obj);
        }
    }

    public void removeiten(int pos){
        if(array!=null){
            array.remove(pos);
        }
    }

    public void updateItem(JSONObject obj , int pos){
        if(array!=null){
            try {
                array.put(pos,obj);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

}
