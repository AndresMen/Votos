package com.example.mendez.votos;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.mendez.votos.web.VolleySingleton;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link votos_fragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 */
public class votos_fragment extends Fragment implements View.OnClickListener {

    private OnFragmentInteractionListener mListener;

    EditText rec,mes,si,no,nulo,blanco;
    Button gua;
    BD base;
    SQLiteDatabase bd;
    public votos_fragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view=inflater.inflate(R.layout.votos_fragment, container, false);
        rec=(EditText)view.findViewById(R.id.editTextrec);
        rec.setOnClickListener(this);
        mes=(EditText)view.findViewById(R.id.editTextmesa);
        mes.setOnClickListener(this);
        si=(EditText)view.findViewById(R.id.edtsi);
        no=(EditText)view.findViewById(R.id.edtno);
        nulo=(EditText)view.findViewById(R.id.edtnulo);
        blanco=(EditText)view.findViewById(R.id.edtbla);
        gua=(Button)view.findViewById(R.id.env);
        gua.setOnClickListener(this);
        base = new BD(getActivity(),
                "baseVotos", null, 1);
        bd = base.getWritableDatabase();
        return view;
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

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.env:

                if (!TextUtils.isEmpty(rec.getText().toString())&!TextUtils.isEmpty(mes.getText().toString())&!TextUtils.isEmpty(si.getText().toString())&!TextUtils.isEmpty(no.getText().toString())&!TextUtils.isEmpty(nulo.getText().toString())&!TextUtils.isEmpty(blanco.getText().toString())){
                    guardarp(rec.getText().toString(),mes.getText().toString(),si.getText().toString(),no.getText().toString(),nulo.getText().toString(),blanco.getText().toString());
                }else {
                    Toast.makeText(getActivity(),"Rellene todos los campos",Toast.LENGTH_SHORT).show();
                }

                break;
            case R.id.editTextrec:
               tipo();
                break;
            case R.id.editTextmesa:
                mesa();
                break;
        }
    }
    public void tipo(){
        final CharSequence[] opcion={"Urbano","Rural"};
        final AlertDialog.Builder alertOpcion=new AlertDialog.Builder(getContext());
        alertOpcion.setTitle("Seleccione lugar");
        alertOpcion.setItems(opcion, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if(opcion[i].toString().equals("Urbano")){
                    recinciu();
                }else{
                    if (opcion[i].toString().equals("Rural")){
                        recinrur();
                    }
                }
            }
        });
        alertOpcion.show();
    }
    public void recinciu(){
        final CharSequence[] opcion={"U.E. Club de Leones","U.E. Estenssoro","U.E. Ferroviaria","U.E. Liceo","U.E. N. Paz Galarza","U.E. German Bush","U.E. Yacuiba","U.E. Oliverio P.","U.E. Belgrano","U.E. Simon Bolivar I","U.E. J. Azurduy","U.E. Sucre","U.E. Heroes del Chaco","U.E. Simon Bolivar II"};
        final AlertDialog.Builder alertOpcion=new AlertDialog.Builder(getActivity());
        alertOpcion.setTitle("Seleccione recinto");
        alertOpcion.setItems(opcion, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                rec.setText(opcion[i].toString());
            }
        });
        alertOpcion.show();
    }
    public void recinrur(){
        final CharSequence[] opcion={"Palmar Chico","Yaguacua","Aguairenda","Purisima","Crevaux","Dormini","Sunchal","Sanandita","Sachapera","Pajoso","Tierras Nuevas","Caiza","Carceleta","Campo Grande","San Isidro","Bagual","Cañon oculto","Villa el Carmen","La Grampa","San francisco del Inti","Timboy Chaco"};
        final AlertDialog.Builder alertOpcion=new AlertDialog.Builder(getActivity());
        alertOpcion.setTitle("Seleccione recinto");
        alertOpcion.setItems(opcion, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                rec.setText(opcion[i].toString());
            }
        });
        alertOpcion.show();
    }
    public void mesa(){
        final CharSequence[] opcion={"1","2","3","4","5","6","7","8","9","10","11","12","13","14","15","16","17","18","19","20","21","22","23","24"};
        final AlertDialog.Builder alertOpcion=new AlertDialog.Builder(getActivity());
        alertOpcion.setTitle("Seleccione mesa");
        alertOpcion.setItems(opcion, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                mes.setText(opcion[i].toString());
            }
        });
        alertOpcion.show();
    }
    public void guardarp(String rec,String me,String si,String no,String nul,String blan) {

        Log.e("gua","entro guarproducto");
        Log.e("ver","recinto "+rec);
        Log.e("ver","mesa "+me);
        Log.e("ver","si "+si);
        Log.e("ver","no "+no);
        Log.e("ver","nulo "+nul);
        Log.e("ver","blanco "+blan);


        HashMap<String, String> map = new HashMap<>();// Mapeo previo

        map.put("recinto", rec);
        map.put("mesa", me);
        map.put("si", si);
        map.put("no", no);
        map.put("nulo", nul);
        map.put("blanco", blan);



        JSONObject jobject = new JSONObject(map);

        // Actualizar datos en el servidor

        final ProgressDialog loading = ProgressDialog.show(getActivity(),"Subiendo...","Espere por favor...",false,false);

        VolleySingleton.getInstance(getActivity()).addToRequestQueue(
                new JsonObjectRequest(
                        Request.Method.POST,
                        Constantes.IN_VO,
                        jobject,
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                loading.dismiss();
                                procesarrespuesta(response);
                                Log.e("pinche","mnada a procesar respuesta");
                            }
                        },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                Log.e( "Error Volley: " ,error.getMessage());
                                loading.dismiss();

                                //finish();
                            }
                        }

                ) {
                    @Override
                    public Map<String, String> getHeaders() {
                        Map<String, String> headers = new HashMap<String, String>();
                        headers.put("Content-Type", "application/json; charset=utf-8");
                        headers.put("Accept", "application/json");
                        return headers;
                    }

                    @Override
                    public String getBodyContentType() {
                        return "application/json; charset=utf-8" + getParamsEncoding();
                    }
                }
        );

    }

    private void procesarrespuesta(JSONObject response) {
        try {
            String estado = response.getString("estado");
            // Obtener mensaje
            String mensaje = response.getString("mensaje");
            switch (estado) {
                case "1":
                    // Mostrar mensaje

                        ContentValues registro = new ContentValues();
                        registro.put("recinto",rec.getText().toString());
                        registro.put("mesa",mes.getText().toString());
                        registro.put("vsi",si.getText().toString());
                        registro.put("vno",no.getText().toString());
                        registro.put("vnulo",nulo.getText().toString());
                        registro.put("vblanco",blanco.getText().toString());
                        bd.insert("votos", null, registro);

                    Toast.makeText(getActivity(), mensaje, Toast.LENGTH_LONG).show();

                    mes.setText("");
                    si.setText("");
                    no.setText("");
                    nulo.setText("");
                    blanco.setText("");
                    break;

                case "2":
                    // Mostrar mensaje
                    Toast.makeText(getActivity(), mensaje, Toast.LENGTH_LONG).show();

                    break;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

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
