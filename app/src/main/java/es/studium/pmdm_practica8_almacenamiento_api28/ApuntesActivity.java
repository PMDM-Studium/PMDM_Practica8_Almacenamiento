package es.studium.pmdm_practica8_almacenamiento_api28;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;

public class ApuntesActivity extends AppCompatActivity {
    int idFK = MainActivity.idSeleccionado;
    int idSelect;
    String idFKString = String.valueOf(idFK);
    ListView listaApuntes;
    String servidor = "192.168.1.79";
    // Atributos
    JSONArray result;
    JSONObject jsonobject;
    String idApunte ="";
    String fechaApunte ="";
    String textoApunte = "";
    String idCuadernoFK ="";
    EditText txtFechaApunte, txtNombre;
    ConsultaRemota acceso;
    AltaRemota alta;
    BajaRemota baja;
    ModificacionRemota modifica;
    ArrayList<Apuntes> arrayListApuntes;
    AdaptadorApuntes adaptadorApuntes;
    private FloatingActionButton fabAgregarApunte;
    Button btnAtras;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_apuntes);

        listaApuntes = findViewById(R.id.listaapuntesContainer);
        fabAgregarApunte = findViewById(R.id.floatingActionButtonapuntes);
        btnAtras = findViewById(R.id.btnAtras);

        arrayListApuntes = new ArrayList<>();
        acceso = new ConsultaRemota();
        acceso.execute();



        //Ponemos primero el click largo para que no afecte al corto.
        listaApuntes.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                AlertDialog dialog = new AlertDialog.Builder(ApuntesActivity.this).setPositiveButton("Sí", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Toast.makeText(ApuntesActivity.this, "Eliminando datos...", Toast.LENGTH_SHORT).show();
                        baja = new BajaRemota(id+"");
                        baja.execute();
                        acceso = new ConsultaRemota();
                        acceso.execute();
                    }
                }).setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }).setTitle("Confirmar")
                        .setMessage("¿Esta seguro de querer eliminar el Apunte?")
                        .create();
                dialog.show();
                return true;
            }
        });
        listaApuntes.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
               //Un click
                // Create an alert builder
                AlertDialog.Builder builder = new AlertDialog.Builder(ApuntesActivity.this);
                builder.setTitle("Name");

                // set the custom layout
                final View customLayout = getLayoutInflater().inflate(R.layout.dialogo_modificar_apunte, null);
                builder.setView(customLayout);
                try {
                    jsonobject = result.getJSONObject((int) id);
                    idApunte = jsonobject.getString("idApunte");
                    fechaApunte = jsonobject.getString("fechaApunte");
                    textoApunte = jsonobject.getString("textoApunte");
                    idCuadernoFK = jsonobject.getString("idCuadernoFK");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                //Cargamos el EditText
                txtFechaApunte = customLayout.findViewById(R.id.dlgFechaApunte);
                txtNombre = customLayout.findViewById(R.id.dlgTextoApunte);
                //Establecemos los valores
                txtFechaApunte.setText(fechaApunte);
                txtNombre.setText(textoApunte);
                //Volvemos a cargar
                txtFechaApunte = customLayout.findViewById(R.id.dlgFechaApunte);
                txtNombre = customLayout.findViewById(R.id.dlgTextoApunte);
                // add a button
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {
                        // send data from the AlertDialog to the Activity
                        Toast.makeText(ApuntesActivity.this, "Alta datos...", Toast.LENGTH_SHORT).show();

                        modifica = new ModificacionRemota(String.valueOf(id), txtFechaApunte.getText().toString(), txtNombre.getText().toString(), idFKString);
                        modifica.execute();
                    }
                });
                builder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });
                // create and show the alert dialog
                builder.show();
                acceso = new ConsultaRemota();
                acceso.execute();
            }
        });
        fabAgregarApunte.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // Create an alert builder
                AlertDialog.Builder builder = new AlertDialog.Builder(ApuntesActivity.this);
                builder.setTitle("Agregar Apunte");

                // set the custom layout
                final View customLayout = getLayoutInflater().inflate(R.layout.dialogo_agregar_apunte, null);
                builder.setView(customLayout);

                // add a button
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {
                        // send data from the AlertDialog to the Activity

                        txtFechaApunte = customLayout.findViewById(R.id.dlgFechaApunte);
                        txtNombre = customLayout.findViewById(R.id.dlgTextoApunte);

                        Toast.makeText(ApuntesActivity.this, "Alta datos...", Toast.LENGTH_SHORT).show();
                        alta = new AltaRemota(txtFechaApunte.getText().toString(), txtNombre.getText().toString(), idFKString);
                        alta.execute();
                    }
                });
                builder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });
                // create and show the alert dialog
                builder.show();
            }
        });
        btnAtras.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private class ConsultaRemota extends AsyncTask<Void, Void, String>
    {
        // Constructor
        public ConsultaRemota()
        {}
        // Inspectores
        protected void onPreExecute()
        {
            if(!arrayListApuntes.isEmpty()) {
                arrayListApuntes.clear();
            }
            adaptadorApuntes = new AdaptadorApuntes(ApuntesActivity.this, arrayListApuntes);
            Toast.makeText(ApuntesActivity.this, "Obteniendo datos...", Toast.LENGTH_SHORT).show();


        }
        protected String doInBackground(Void... argumentos)
        {
            if(!arrayListApuntes.isEmpty()) {
                arrayListApuntes.clear();
            }
            try {
                // Crear la URL de conexión al API
                URL url = new
                        URL("http://" + servidor + "/ApiRest/apuntes.php?idCuaderno="+idFK);
                // Crear la conexión HTTP
                HttpURLConnection myConnection = (HttpURLConnection)
                        url.openConnection();
                // Establecer método de comunicación. Por defecto GET.
                myConnection.setRequestMethod("GET");
                if (myConnection.getResponseCode() == 200) {
                    // Conexión exitosa
                    // Creamos Stream para la lectura de datos desde el servidor
                    InputStream responseBody = myConnection.getInputStream();
                    InputStreamReader responseBodyReader =
                            new InputStreamReader(responseBody, "UTF-8");
                    // Creamos Buffer de lectura
                    BufferedReader bR = new
                            BufferedReader(responseBodyReader);
                    String line = "";
                    StringBuilder responseStrBuilder = new StringBuilder();
                    // Leemos el flujo de entrada
                    while ((line = bR.readLine()) != null) {
                        responseStrBuilder.append(line);
                    }
                    // Parseamos respuesta en formato JSON
                    result = new JSONArray(responseStrBuilder.toString());
                    // Nos quedamos solamente con la primera
                    //arrayListCuadernos.clear();

                    for (int i = 0; i < result.length(); i++) {
                        jsonobject = result.getJSONObject(i);
                        // Sacamos dato a dato obtenido
                        idApunte = jsonobject.getString("idApunte");
                        fechaApunte = jsonobject.getString("fechaApunte");
                        textoApunte = jsonobject.getString("textoApunte");
                        idCuadernoFK = jsonobject.getString("idCuadernoFK");
                        Apuntes apuntes = new Apuntes(Integer.parseInt(idApunte), fechaApunte, textoApunte, Integer.parseInt(idCuadernoFK));
                        arrayListApuntes.add(apuntes);
                    }
                    responseBody.close();
                    responseBodyReader.close();
                    myConnection.disconnect();
                } else {
                    // Error en la conexión
                    Log.println(Log.ERROR, "Error", "¡Conexión fallida!");
                }
            } catch (Exception e) {
                Log.println(Log.ERROR, "Error", "¡Conexión fallida!");
            }
            return (null);
        }
        protected void onPostExecute(String mensaje)
        {
            listaApuntes.setAdapter(adaptadorApuntes);


        }
    }

    private class BajaRemota extends AsyncTask<Void, Void, String>
    {
        // Atributos
        String idApunte;
        // Constructor
        public BajaRemota(String id)
        {
            this.idApunte = id;
        }
        // Inspectores
        protected void onPreExecute()
        {
            Toast.makeText(ApuntesActivity.this, "Eliminando...", Toast.LENGTH_SHORT).show();
        }
        @Override
        protected String doInBackground(Void... voids)
        {
            try
            {
                // Crear la URL de conexión al API
                URI baseUri = new
                        URI("http://"+servidor+"/ApiRest/apuntes.php");
                String[] parametros = {"id",this.idApunte};
                URI uri = applyParameters(baseUri, parametros);
                // Create connection
                HttpURLConnection myConnection = (HttpURLConnection)
                        uri.toURL().openConnection();
                // Establecer método. Por defecto GET.
                myConnection.setRequestMethod("DELETE");
                if (myConnection.getResponseCode() == 200)
                {
                    // Success
                    Log.println(Log.ASSERT,"Resultado", "Registro borrado");
                    myConnection.disconnect();
                }
                else
                {
                    // Error handling code goes here
                    Log.println(Log.ASSERT,"Error", "Error");
                }
            }
            catch(Exception e)
            {
                Log.println(Log.ASSERT,"Excepción", e.getMessage());
            }
            return null;
        }
        protected void onPostExecute(String mensaje)
        {
            Toast.makeText(ApuntesActivity.this, "Actualizando datos...", Toast.LENGTH_SHORT).show();
        }
        URI applyParameters(URI uri, String[] urlParameters)
        {
            StringBuilder query = new StringBuilder();
            boolean first = true;
            for (int i = 0; i < urlParameters.length; i += 2)
            {
                if (first)
                {
                    first = false;
                }
                else
                {
                    query.append("&");
                }
                try
                {
                    query.append(urlParameters[i]).append("=")
                            .append(URLEncoder.encode(urlParameters[i + 1],
                                    "UTF-8"));
                }
                catch (UnsupportedEncodingException ex)
                {
                    /* As URLEncoder are always correct, this exception
                     * should never be thrown. */
                    throw new RuntimeException(ex);
                }
            }
            try
            {
                return new URI(uri.getScheme(), uri.getAuthority(),
                        uri.getPath(), query.toString(), null);
            }
            catch (Exception ex)
            {
                /* As baseUri and query are correct, this exception
                 * should never be thrown. */
                throw new RuntimeException(ex);
            }
        }
    }
    private class ModificacionRemota extends AsyncTask<Void, Void, String>
    {
        // Atributos
        String idApunte, fechaApunte, textoApunte, idCuadernoFK;
        // Constructor
        public ModificacionRemota(String idApunte,String fechaApunte, String textoApunte, String idCuadernoFK)
        {
            this.idApunte = idApunte;
            this.fechaApunte = fechaApunte;
            this.textoApunte = textoApunte;
            this.idCuadernoFK = idCuadernoFK;
        }
        // Inspectores
        protected void onPreExecute()
        {
            Toast.makeText(ApuntesActivity.this, "Modificando...", Toast.LENGTH_SHORT).show();
        }
        protected String doInBackground(Void... voids)
        {
            try
            {
                String response = "";
                Uri uri = new Uri.Builder()
                        .scheme("http")
                        .authority(servidor)
                        .path("/ApiRest/apuntes.php")
                        .appendQueryParameter("idApunte", this.idApunte)
                        .appendQueryParameter("fechaApunte", this.fechaApunte)
                        .appendQueryParameter("textoApunte", this.textoApunte)
                        .appendQueryParameter("idCuadernoFK", this.idCuadernoFK)
                        .build();
                // Create connection
                URL url = new URL(uri.toString());
                HttpURLConnection connection = (HttpURLConnection)
                        url.openConnection();
                connection.setReadTimeout(15000);
                connection.setConnectTimeout(15000);
                connection.setRequestMethod("PUT");
                connection.setDoInput(true);
                connection.setDoOutput(true);
                int responseCode=connection.getResponseCode();
                if (responseCode == HttpsURLConnection.HTTP_OK)
                {
                    String line;
                    BufferedReader br=new BufferedReader(new
                            InputStreamReader(connection.getInputStream()));
                    while ((line=br.readLine()) != null)
                    {
                        response+=line;
                    }
                }
                else
                {
                    response="";
                }
                connection.getResponseCode();
                if (connection.getResponseCode() == 200)
                {
                    // Success
                    Log.println(Log.ASSERT,"Resultado", "Registro modificado:"+response);
                    connection.disconnect();
                }
                else
                {
                    // Error handling code goes here
                    Log.println(Log.ASSERT,"Error", "Error");
                }
            }
            catch(Exception e)
            {
                Log.println(Log.ASSERT,"Excepción", e.getMessage());
            }
            return null;
        }
        protected void onPostExecute(String mensaje)
        {
            Toast.makeText(ApuntesActivity.this, "Actualizando datos...", Toast.LENGTH_SHORT).show();
            acceso = new ConsultaRemota();
            acceso.execute();
        }
    }
    private class AltaRemota extends AsyncTask<Void, Void, String>
    {
        // Atributos
        String fechaApunte, textoApunte, idCuadernoFK;
        // Constructor
        public AltaRemota(String fechaApunte, String textoApunte, String idCuadernoFK)
        {
            this.fechaApunte = fechaApunte;
            this.textoApunte = textoApunte;
            this.idCuadernoFK = idCuadernoFK;
        }
        // Inspectoras
        protected void onPreExecute()
        {
            Toast.makeText(ApuntesActivity.this, "Alta..."+this.textoApunte, Toast.LENGTH_SHORT).show();
        }
        protected String doInBackground(Void... argumentos)
        {
            try {
                // Crear la URL de conexión al API
                URL url = new
                        URL("http://"+servidor+"/ApiRest/apuntes.php");
                // Crear la conexión HTTP
                HttpURLConnection myConnection = (HttpURLConnection)
                        url.openConnection();
                // Establecer método de comunicación.
                myConnection.setRequestMethod("POST");
                // Conexión exitosa
                String response = "";
                HashMap<String, String> postDataParams = new
                        HashMap<String, String>();
                postDataParams.put("fechaApunte",
                        this.fechaApunte);
                postDataParams.put("textoApunte",
                        this.textoApunte);
                postDataParams.put("idCuadernoFK",
                        this.idCuadernoFK);
                myConnection.setDoInput(true);
                myConnection.setDoOutput(true);
                OutputStream os = myConnection.getOutputStream();
                BufferedWriter writer = new BufferedWriter(new
                        OutputStreamWriter(os, "UTF-8"));
                writer.write(getPostDataString(postDataParams));
                writer.flush();
                writer.close();
                os.close();
                myConnection.getResponseCode();
                if (myConnection.getResponseCode() == 200)
                {
                    // Success
                    myConnection.disconnect();
                }
                else {
                    // Error handling code goes here
                    Log.println(Log.ASSERT, "Error", "Error");
                }
            }
            catch(Exception e)
            {
                Log.println(Log.ASSERT,"Excepción", e.getMessage());
            }
            return (null);
        }
        protected void onPostExecute(String mensaje)
        {
            // Actualizamos los cuadros de texto
            Toast.makeText(ApuntesActivity.this, "Alta Correcta...", Toast.LENGTH_SHORT).show();
            acceso = new ConsultaRemota();
            acceso.execute();
        }
        private String getPostDataString(HashMap<String, String> params)
                throws UnsupportedEncodingException
        {
            StringBuilder result = new StringBuilder();
            boolean first = true;
            for(Map.Entry<String, String> entry : params.entrySet())
            {
                if (first)
                {
                    first = false;
                }
                else
                {
                    result.append("&");
                }
                result.append(URLEncoder.encode(entry.getKey(), "UTF-8"));
                result.append("=");
                result.append(URLEncoder.encode(entry.getValue(), "UTF-8"));
            }
            return result.toString();
        }
    }
}
