package ricardopazdemiquel.com.imotos.Adapter;

import android.content.Context;
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

public class Adapter_historial extends BaseAdapter {

    private Context contexto;
    private JSONArray array = new JSONArray();
    MenuItem item;

    public Adapter_historial(Context contexto, JSONArray array) {
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
            view = LayoutInflater.from(contexto).inflate(R.layout.layout_item_historial , viewGroup, false);
        }

        /*view.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if( MotionEvent.ACTION_DOWN);
                view.setBackgroundColor(Color.parseColor("#EEEEEE"));
                return false;
            }
        });*/
        TextView text_ubicacion = view.findViewById(R.id.text_ubicacion);

        try {
            JSONObject obj =  array.getJSONObject(i);
            Double latFin = obj.getDouble("latfinal");
            Double lngFin = obj.getDouble("lngfinal");
            String ubicacion = getCompleteAddressString(latFin,lngFin);
            text_ubicacion.setText(ubicacion.replaceAll("\n",""));
            view.setTag(obj.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return view;
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

}
