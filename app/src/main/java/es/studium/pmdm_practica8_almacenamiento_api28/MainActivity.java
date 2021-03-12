package es.studium.pmdm_practica8_almacenamiento_api28;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONArray;
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

public class MainActivity extends AppCompatActivity {
    ListView listaCuadernos;
    String servidor = "192.168.1.79";
    // Atributos
    JSONArray result;
    JSONObject jsonobject;
    String idCuaderno="";
    String nombreCuaderno= "";
    TextView txtNombre;
    ConsultaRemota acceso;
    AltaRemota alta;
    BajaRemota baja;
    ArrayList<Cuadernos> arrayListCuadernos;
    AdaptadorCuadernos adaptadorCuadernos;
    private FloatingActionButton fabAgregarCuaderno;
    public static int idSeleccionado;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        listaCuadernos = findViewById(R.id.listacuadernosContainer);
        fabAgregarCuaderno = findViewById(R.id.floatingActionButton);
        arrayListCuadernos = new ArrayList<>();
        acceso = new ConsultaRemota();
        acceso.execute();

        //Ponemos primero el click largo para que no afecte al corto.
        listaCuadernos.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                AlertDialog dialog = new AlertDialog.Builder(MainActivity.this).setPositiveButton("Sí", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Toast.makeText(MainActivity.this, "Eliminando datos...", Toast.LENGTH_SHORT).show();
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
                        .setMessage("¿Esta seguro de querer eliminar el cuaderno?")
                        .create();
                dialog.show();
                return true;
            }
        });
        listaCuadernos.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                idSeleccionado= (int) id;
                Intent intent = new Intent( MainActivity.this, ApuntesActivity.class);
                startActivity(intent);
            }
        });
        fabAgregarCuaderno.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // Create an alert builder
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setTitle("Agregar Cuaderno");

                // set the custom layout
                final View customLayout = getLayoutInflater().inflate(R.layout.dialogo_agregar_cuaderno, null);
                builder.setView(customLayout);

                // add a button
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {
                        // send data from the AlertDialog to the Activity
                        txtNombre = customLayout.findViewById(R.id.txtNombreCuaderno);

                        Toast.makeText(MainActivity.this, "Alta datos...", Toast.LENGTH_SHORT).show();
                        alta = new AltaRemota(txtNombre.getText().toString());
                        alta.execute();
                        txtNombre.setFocusable(false);
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
    }

    private class ConsultaRemota extends AsyncTask<Void, Void, String>
    {
        // Constructor
        public ConsultaRemota()
        {}
        // Inspectores
        protected void onPreExecute()
        {
            if(!arrayListCuadernos.isEmpty()) {
                arrayListCuadernos.clear();
            }
                adaptadorCuadernos= new AdaptadorCuadernos(MainActivity.this, arrayListCuadernos);
                Toast.makeText(MainActivity.this, "Obteniendo datos...", Toast.LENGTH_SHORT).show();
        }
        protected String doInBackground(Void... argumentos)
        {
            if(!arrayListCuadernos.isEmpty()) {
                arrayListCuadernos.clear();
            }
                try {
                    // Crear la URL de conexión al API
                    URL url = new
                            URL("http://" + servidor + "/ApiRest/cuadernos.php");
                    // Crear la conexión HTTP
                    HttpURLConnection myConnection = (HttpURLConnection) url.openConnection();
                    // Establecer método de comunicación. Por defecto GET.
                    myConnection.setRequestMethod("GET");
                    if (myConnection.getResponseCode() == 200) {
                        // Conexión exitosa
                        // Creamos Stream para la lectura de datos desde el servidor
                        InputStream responseBody = myConnection.getInputStream();
                        InputStreamReader responseBodyReader = new InputStreamReader(responseBody, "UTF-8");
                        // Creamos Buffer de lectura
                        BufferedReader bR = new BufferedReader(responseBodyReader);
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
                            idCuaderno = jsonobject.getString("idCuaderno");
                            nombreCuaderno = jsonobject.getString("nombreCuaderno");
                            Cuadernos cuadernos = new Cuadernos(Integer.parseInt(idCuaderno), nombreCuaderno);
                            arrayListCuadernos.add(cuadernos);
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
            listaCuadernos.setAdapter(adaptadorCuadernos);
        }
    }

    private class BajaRemota extends AsyncTask<Void, Void, String>
    {
        // Atributos
        String idCuaderno;
        // Constructor
        public BajaRemota(String id)
        {
            this.idCuaderno = id;
        }
        // Inspectores
        protected void onPreExecute()
        {
            Toast.makeText(MainActivity.this, "Eliminando...", Toast.LENGTH_SHORT).show();
        }
        @Override
        protected String doInBackground(Void... voids)
        {
            try
            {
                // Crear la URL de conexión al API
                URI baseUri = new
                        URI("http://"+servidor+"/ApiRest/cuadernos.php");
                String[] parametros = {"id",this.idCuaderno};
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
            Toast.makeText(MainActivity.this, "Actualizando datos...", Toast.LENGTH_SHORT).show();
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

    private class AltaRemota extends AsyncTask<Void, Void, String>
    {
        // Atributos
        String nombreCuaderno;
        // Constructor
        public AltaRemota(String nombre)
        {
            this.nombreCuaderno = nombre;
        }
        // Inspectoras
        protected void onPreExecute()
        {
            Toast.makeText(MainActivity.this, "Alta..."+this.nombreCuaderno, Toast.LENGTH_SHORT).show();
        }
        protected String doInBackground(Void... argumentos)
        {
            try {
                // Crear la URL de conexión al API
                URL url = new URL("http://"+servidor+"/ApiRest/cuadernos.php");
                // Crear la conexión HTTP
                HttpURLConnection myConnection = (HttpURLConnection) url.openConnection();
                // Establecer método de comunicación.
                myConnection.setRequestMethod("POST");
                // Conexión exitosa
                String response = "";
                HashMap<String, String> postDataParams = new HashMap<String, String>();
                postDataParams.put("nombreCuaderno", this.nombreCuaderno);
                myConnection.setDoInput(true);
                myConnection.setDoOutput(true);
                OutputStream os = myConnection.getOutputStream();
                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
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
            Toast.makeText(MainActivity.this, "Alta Correcta...", Toast.LENGTH_SHORT).show();
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
