package ricardopazdemiquel.com.imotosCliente.Adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import ricardopazdemiquel.com.imotosCliente.PedirSieteMap;
import ricardopazdemiquel.com.imotosCliente.R;
import ricardopazdemiquel.com.imotosCliente.viewHolder;

public class AdaptadorSieteEstandar extends RecyclerView.Adapter<viewHolder> {

    private JSONArray listaCanchas;
    private Context contexto;
    private PedirSieteMap pm;


    public AdaptadorSieteEstandar(JSONArray listaCanchas, Context contexto, PedirSieteMap pm) {
        this.listaCanchas = listaCanchas;
        this.contexto = contexto;
        this.pm =pm;
    }


    @NonNull
    @Override
    public viewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.layout_item_siete_estandar, parent, false);
        viewHolder holder = new viewHolder(view);

        return holder;
    }

    @Override
    public void onBindViewHolder(final viewHolder holder, int position) {
        try {
            //holder.itemView.setOnLongClickListener(this);
            JSONObject obj =listaCanchas.getJSONObject(position);
            //holder.btn_reservar.setImageResource(R.drawable.background_siete_camioneta);
            //String a = obj.getString("nombre");
            holder.btn_reservar.setImageResource(Integer.parseInt(obj.getString("nombre")));
            holder.btn_reservar.setTag(obj.getInt("id"));
            holder.btn_reservar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                        int id = (int) view.getTag();
                        pm.CalcularRuta(id);
                }
            });
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return listaCanchas.length();
    }
}
