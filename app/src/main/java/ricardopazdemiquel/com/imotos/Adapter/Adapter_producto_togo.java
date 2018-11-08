package ricardopazdemiquel.com.imotos.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import ricardopazdemiquel.com.imotos.R;

public class Adapter_producto_togo extends BaseAdapter {

    private Context contexto;
    private JSONArray array = new JSONArray();
    MenuItem item;

    public Adapter_producto_togo(Context contexto, JSONArray array) {
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
            view = LayoutInflater.from(contexto).inflate(R.layout.layout_item_producto_togo, viewGroup, false);
        }

        /*view.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if( MotionEvent.ACTION_DOWN);
                view.setBackgroundColor(Color.parseColor("#EEEEEE"));
                return false;
            }
        });*/

        TextView text_producto = view.findViewById(R.id.text_producto);
        TextView text_cantidad = view.findViewById(R.id.text_cantidad);
        TextView text_descripcion = view.findViewById(R.id.text_descripcion);
        try {
            JSONObject obj =  array.getJSONObject(i);
            text_producto.setText(obj.getString("producto"));
            text_cantidad.setText(obj.getString("cantidad"));
            text_descripcion.setText(obj.getString("descripcion"));
            view.setTag(obj.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return view;
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
