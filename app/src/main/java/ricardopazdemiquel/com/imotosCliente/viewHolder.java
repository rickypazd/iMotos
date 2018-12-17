package ricardopazdemiquel.com.imotosCliente;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;

public class viewHolder extends RecyclerView.ViewHolder {


    public ImageView btn_reservar;
    public viewHolder(View v) {

        super(v);

         btn_reservar= v.findViewById(ricardopazdemiquel.com.imotos.R.id.btn_reservar);

    }
}
