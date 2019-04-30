package com.example.mendez.votos;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.support.v4.content.FileProvider;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.mendez.votos.web.VolleySingleton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;

import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link repo_fragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 */
public class repo_fragment extends Fragment implements View.OnClickListener {

    private OnFragmentInteractionListener mListener;
    Button repo_rec,repo_to;
    private final int MIS_PERMISOS = 100;
    public repo_fragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view=inflater.inflate(R.layout.repo_fragment, container, false);

        repo_rec=(Button)view.findViewById(R.id.repo_rec);
        repo_rec.setOnClickListener(this);
        repo_to=(Button)view.findViewById(R.id.repo_to);
        repo_to.setOnClickListener(this);
        validaPermisos();
        solicitaPermisosVersionesSuperiores();
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
            case R.id.repo_rec:

                startActivity(new Intent(getActivity(),repo_recin.class));

                break;
            case R.id.repo_to:

                repo_tot();

                break;
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode==MIS_PERMISOS){
            if(grantResults.length==2 && grantResults[0]== PackageManager.PERMISSION_GRANTED && grantResults[1]==PackageManager.PERMISSION_GRANTED){//el dos representa los 2 permisos
                Toast.makeText(getActivity(),"Permisos aceptados",Toast.LENGTH_SHORT);
                //btnf.setEnabled(true);
            }
        }else{
            solicitarPermisosManual();
        }
    }
    private void solicitarPermisosManual() {
        final CharSequence[] opciones={"si","no"};
        final AlertDialog.Builder alertOpciones=new AlertDialog.Builder(getActivity());//estamos en fragment
        alertOpciones.setTitle("¿Desea configurar los permisos de forma manual?");
        alertOpciones.setItems(opciones, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (opciones[i].equals("si")){
                    Intent intent=new Intent();
                    intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                    Uri uri=Uri.fromParts("package",getActivity().getPackageName(),null);
                    intent.setData(uri);
                    startActivity(intent);
                }else{
                    Toast.makeText(getActivity(),"Los permisos no fueron aceptados",Toast.LENGTH_SHORT).show();
                    dialogInterface.dismiss();
                }
            }
        });
        alertOpciones.show();
    }
    private boolean solicitaPermisosVersionesSuperiores() {
        if (Build.VERSION.SDK_INT<Build.VERSION_CODES.M){//validamos si estamos en android menor a 6 para no buscar los permisos
            return true;
        }

        //validamos si los permisos ya fueron aceptados
        if((getActivity().checkSelfPermission(WRITE_EXTERNAL_STORAGE)== PackageManager.PERMISSION_GRANTED)){
            return true;
        }


        if ((shouldShowRequestPermissionRationale(WRITE_EXTERNAL_STORAGE))){
            cargarDialogoRecomendacion();
        }else{
            requestPermissions(new String[]{WRITE_EXTERNAL_STORAGE}, MIS_PERMISOS);
        }

        return false;//implementamos el que procesa el evento dependiendo de lo que se defina aqui
    }
    private void cargarDialogoRecomendacion() {
        AlertDialog.Builder dialogo=new AlertDialog.Builder(getActivity());
        dialogo.setTitle("Permisos Desactivados");
        dialogo.setMessage("Debe aceptar los permisos para el correcto funcionamiento de la App");

        dialogo.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                requestPermissions(new String[]{WRITE_EXTERNAL_STORAGE},100);
            }
        });
        dialogo.show();
    }
    private boolean validaPermisos() {

        if(Build.VERSION.SDK_INT< Build.VERSION_CODES.M){
            return true;
        }

        if((getActivity().checkSelfPermission(WRITE_EXTERNAL_STORAGE)== PackageManager.PERMISSION_GRANTED)){
            return true;
        }

        if(
                (shouldShowRequestPermissionRationale(WRITE_EXTERNAL_STORAGE))){
            cargarDialogoRecomendacion();
        }else{
            requestPermissions(new String[]{WRITE_EXTERNAL_STORAGE},100);
        }

        return false;
    }

    public void repo_tot() {
        String newURL = Constantes.REPO_TO;
        Log.e("ver","url111VENTAS DET-> "+newURL);
        final ProgressDialog loading = ProgressDialog.show(getActivity(),"Cargando...","Espere por favor...",false,false);

        VolleySingleton.getInstance(getActivity()).
                addToRequestQueue(
                        new JsonObjectRequest(
                                Request.Method.GET,
                                newURL,
                                new Response.Listener<JSONObject>() {

                                    @Override
                                    public void onResponse(JSONObject response) {
                                        // Procesar la respuesta Json
                                        loading.dismiss();
                                        procesarRespuesta2(response);
                                        Log.e("ver","manda-procesar-respuesta -");
                                    }
                                },
                                new Response.ErrorListener() {
                                    @Override
                                    public void onErrorResponse(VolleyError error) {
                                        loading.dismiss();
                                        Log.e("ver","Error Volley NOPUTO -");



                                    }
                                }
                        )
                );
    }

    /**
     * Interpreta los resultados de la respuesta y así
     * realizar las operaciones correspondientes
     *
     * @param response Objeto Json con la respuesta
     */
    private void procesarRespuesta2(JSONObject response) {
        Log.e("ver","entra-procesar-respuesta -");
        try {
            // Obtener atributo "estado"
            String estado = response.getString("estado");

            switch (estado) {
                case "1": // EXITO

                    String mensajes = response.getString("mensaje");
                    Log.e("ver","entra-caso1 -"+mensajes);

                    Log.e("ver","TAMAÑO"+mensajes.length());
                    if (vopd(mensajes,"Total")){
                        verpdf("Total");
                    }else{
                        Toast.makeText(getActivity(),"No se pudo obtener el reporte",Toast.LENGTH_SHORT).show();
                    }

                    break;
                case "2": // FALLIDO

                    String mensaje2 = response.getString("mensaje");
                    Log.e("ver","entra-caso2 -"+mensaje2);

                    break;
            }

        } catch (JSONException e) {

        }

    }

    public boolean vopd(String as,String id){
        Log.e("pdf",as);
        File dir;
        File file;
        try {
            String path = Environment.getExternalStorageDirectory().getPath();
            dir = new File(path + "/Reportes");
            if (!dir.exists())
                dir.mkdir();
            file = new File(dir, "Reporte_" + id + ".pdf");
            if (!file.exists()) {
                file.createNewFile();
            }

            //final File dwldsPath = new File(DOWNLOADS_FOLDER + fileName + ".pdf");
            byte[] pdfAsBytes = Base64.decode(as, 0);
            FileOutputStream os;
            os = new FileOutputStream(file, false);
            os.write(pdfAsBytes);
            os.flush();
            os.close();
        }catch(Exception e) {
            Log.e("Error: ", e.getMessage());
            return false;
        }
        return true;
    }
    public void verpdf(String nombre){
        String s=String.valueOf(Environment.getExternalStorageDirectory()+"/Reportes/Reporte_"+nombre+".pdf" );
        Uri path;
        File pdfFile = new File(s);//File path
        if (pdfFile.exists()){ //Revisa si el archivo existe!
            if (Build.VERSION.SDK_INT >=  Build.VERSION_CODES.N) {
                path = FileProvider.getUriForFile(getActivity(), getActivity().getPackageName() + ".provider", pdfFile);
            } else{
                path = Uri.fromFile(pdfFile);
            }
            Intent intent = new Intent(Intent.ACTION_VIEW);
            //define el tipo de archivo
            intent.setDataAndType(path,"application/pdf");
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);//Inicia pdf viewer
            if(intent.resolveActivity(getActivity().getPackageManager()) != null) {
                //Toast.makeText(context, "No tiene aplicacion para ver PDF", Toast.LENGTH_SHORT).show();
                startActivity(Intent.createChooser(intent, "Abrir archivo en"));
            } else{
                Toast.makeText(getActivity(), "No tiene aplicacion para ver PDF", Toast.LENGTH_SHORT).show();
            }

        } else {
            Toast.makeText(getActivity(), "No existe archivo! ", Toast.LENGTH_SHORT).show();
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
