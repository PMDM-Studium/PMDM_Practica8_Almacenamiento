package es.studium.pmdm_practica8_almacenamiento_api28;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class AdaptadorCuadernos extends BaseAdapter {
    private Context context;
    private ArrayList<Cuadernos> listaCuadernos;
    public AdaptadorCuadernos(Context context,ArrayList<Cuadernos> listaCuadernos){
        this.context=context;
        this.listaCuadernos=listaCuadernos;
    }
    @Override
    public int getCount() {
        return listaCuadernos.size();
    }

    @Override
    public Object getItem(int position) {
        return listaCuadernos.get(position);
    }

    @Override
    public long getItemId(int position) {
        return listaCuadernos.get(position).getIdCuaderno();
    }

    @Override
    public View getView(int position, View view, ViewGroup viewGroup) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
        view=inflater.inflate(R.layout.lista_cuadernos, null);
        TextView nombreCuaderno = view.findViewById(R.id.nombreCuaderno);
        nombreCuaderno.setText(listaCuadernos.get(position).getNombreCuaderno());
        return view;
    }
}
