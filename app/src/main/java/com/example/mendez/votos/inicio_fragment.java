package com.example.mendez.votos;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TextView;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link inicio_fragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 */
public class inicio_fragment extends Fragment {

    private OnFragmentInteractionListener mListener;
    tabla_votos tabla;
    TextView avi;
    public inicio_fragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view=inflater.inflate(R.layout.inicio_fragment, container, false);
        tabla = new tabla_votos(getActivity(), (TableLayout)view.findViewById(R.id.tabla));
        tabla.agregarCabecera(R.array.cabecera_tabla);
        avi=(TextView)view.findViewById(R.id.avi);
        cargarTabla();
        return view;
    }

    public void cargarTabla() {
        BD base = new BD(getActivity(), "baseVotos", null, 1);
        SQLiteDatabase bd = base.getWritableDatabase();
        Cursor fila = bd.rawQuery(
                "select * from votos", null);
        int i=0;
        if (fila.moveToFirst()) {
            do {
                ArrayList<String> elementos = new ArrayList<String>();
                elementos.add(Integer.toString(i+1));
                elementos.add(fila.getString(0));
                elementos.add(fila.getString(1));
                elementos.add(fila.getString(2));
                elementos.add(fila.getString(3));
                elementos.add(fila.getString(4));
                elementos.add(fila.getString(5));
                i++;
                tabla.agregarFilaTabla(elementos);
                avi.setVisibility(View.GONE);
            } while (fila.moveToNext());

        }else{
            avi.setVisibility(View.VISIBLE);
        }

    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
