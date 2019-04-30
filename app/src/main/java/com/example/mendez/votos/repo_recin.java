package com.example.mendez.votos;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
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

import java.io.File;
import java.io.FileOutputStream;
import java.util.HashMap;
import java.util.Map;

import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

public class repo_recin extends AppCompatActivity implements View.OnClickListener {
    ProgressDialog loading;
    EditText recin;
    Button crerep;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_repo_recin);
        recin=(EditText)findViewById(R.id.editTextrecin);
        recin.setOnClickListener(this);
        crerep=(Button)findViewById(R.id.btncr);
        crerep.setOnClickListener(this);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

    }

    public void tipo(){
        final CharSequence[] opcion={"Urbano","Rural"};
        final AlertDialog.Builder alertOpcion=new AlertDialog.Builder(repo_recin.this);
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
        final AlertDialog.Builder alertOpcion=new AlertDialog.Builder(repo_recin.this);
        alertOpcion.setTitle("Seleccione recinto");
        alertOpcion.setItems(opcion, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                recin.setText(opcion[i].toString());
            }
        });
        alertOpcion.show();
    }
    public void recinrur(){
        final CharSequence[] opcion={"Palmar Chico","Yaguacua","Aguairenda","Purisima","Crevaux","Dormini","Sunchal","Sanandita","Sachapera","Pajoso","Tierras Nuevas","Caiza","Carceleta","Campo Grande","San Isidro","Bagual","Cañon oculto","Villa el Carmen","La Grampa","San francisco del Inti","Timboy Chaco"};
        final AlertDialog.Builder alertOpcion=new AlertDialog.Builder(repo_recin.this);
        alertOpcion.setTitle("Seleccione recinto");
        alertOpcion.setItems(opcion, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                recin.setText(opcion[i].toString());
            }
        });
        alertOpcion.show();
    }
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.editTextrecin:
                tipo();
                break;
            case R.id.btncr:

                if (!TextUtils.isEmpty(recin.getText().toString())){
                    repo(recin.getText().toString());
                }else{
                    Toast.makeText(getBaseContext(),"Escoja uun recinto",Toast.LENGTH_SHORT).show();
                }

                break;
        }
    }
    public void repo(String rec) {

        Log.e("gua","entro guarproducto");
        Log.e("ver","recinto "+rec);


        HashMap<String, String> map = new HashMap<>();// Mapeo previo

        map.put("recinto", rec);



        JSONObject jobject = new JSONObject(map);

        // Actualizar datos en el servidor

       loading = ProgressDialog.show(repo_recin.this,"Subiendo...","Espere por favor...",false,false);

        VolleySingleton.getInstance(getBaseContext()).addToRequestQueue(
                new JsonObjectRequest(
                        Request.Method.POST,
                        Constantes.REPO_VO,
                        jobject,
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                //loading.dismiss();
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

                    //Toast.makeText(getBaseContext(), mensaje, Toast.LENGTH_LONG).show();
                    loading.dismiss();
                    if (vopd(mensaje,recin.getText().toString())){
                        verpdf(recin.getText().toString());
                    }else{
                        Toast.makeText(getBaseContext(),"No se pudo obtener el reporte",Toast.LENGTH_SHORT).show();
                    }

                    break;

                case "2":
                    // Mostrar mensaje
                    loading.dismiss();
                    Toast.makeText(getBaseContext(), mensaje, Toast.LENGTH_LONG).show();

                    break;
            }
        } catch (JSONException e) {
            e.printStackTrace();
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
                path = FileProvider.getUriForFile(getBaseContext(), getApplicationContext().getPackageName() + ".provider", pdfFile);
            } else{
                path = Uri.fromFile(pdfFile);
            }
            Intent intent = new Intent(Intent.ACTION_VIEW);
            //define el tipo de archivo
            intent.setDataAndType(path,"application/pdf");
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);//Inicia pdf viewer
            if(intent.resolveActivity(getPackageManager()) != null) {
                //Toast.makeText(context, "No tiene aplicacion para ver PDF", Toast.LENGTH_SHORT).show();
                startActivity(Intent.createChooser(intent, "Abrir archivo en"));
            } else{
                Toast.makeText(getBaseContext(), "No tiene aplicacion para ver PDF", Toast.LENGTH_SHORT).show();
            }

        } else {
            Toast.makeText(getBaseContext(), "No existe archivo! ", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home: //hago un case por si en un futuro agrego mas opciones
                Log.i("ActionBar", "Atrás!");
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
