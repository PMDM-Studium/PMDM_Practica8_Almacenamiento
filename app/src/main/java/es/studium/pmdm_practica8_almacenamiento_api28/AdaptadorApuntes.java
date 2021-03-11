package es.studium.pmdm_practica8_almacenamiento_api28;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class AdaptadorApuntes extends BaseAdapter {
    private Context context;
    private ArrayList<Apuntes> listaApuntes;
    public AdaptadorApuntes(Context context,ArrayList<Apuntes> listaApuntes){
        this.context=context;
        this.listaApuntes=listaApuntes;
    }
    @Override
    public int getCount() {
        return listaApuntes.size();
    }

    @Override
    public Object getItem(int position) {
        return listaApuntes.get(position);
    }

    @Override
    public long getItemId(int position) {
        return listaApuntes.get(position).getIdApunte();
    }

    @Override
    public View getView(int position, View view, ViewGroup viewGroup) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
        view=inflater.inflate(R.layout.lista_apuntes, null);
        TextView textoApunte = view.findViewById(R.id.textoApunte);
        textoApunte.setText(listaApuntes.get(position).getTextoApunte());
        TextView fechaApunte = view.findViewById(R.id.fechaApunte);
        fechaApunte.setText(listaApuntes.get(position).getFechaApunte());
        return view;
    }
}
