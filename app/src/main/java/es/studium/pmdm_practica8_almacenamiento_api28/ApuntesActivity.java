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
         import android.widget.Button;
         import android.widget.ListView;
         import android.widget.Toast;

         import com.google.android.material.floatingactionbutton.FloatingActionButton;

         import org.json.JSONArray;
         import org.json.JSONObject;

         import java.io.BufferedReader;
         import java.io.InputStream;
         import java.io.InputStreamReader;
         import java.io.UnsupportedEncodingException;
         import java.net.HttpURLConnection;
         import java.net.URI;
         import java.net.URL;
         import java.net.URLEncoder;
         import java.util.ArrayList;
         import java.util.Date;

public class ApuntesActivity extends AppCompatActivity {
    int idFK = MainActivity.idSeleccionado;
    ListView listaApuntes;
    String servidor = "192.168.1.79";
    // Atributos
    JSONArray result;
    JSONObject jsonobject;
    String idApunte ="";
    String fechaApunte ="";
    String textoApunte = "";
    String idCuadernoFK ="";
    ConsultaRemota acceso;
    BajaRemota baja;
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
                Toast.makeText(ApuntesActivity.this, "Largo "+position+" - "+id, Toast.LENGTH_SHORT).show();

                AlertDialog dialog = new AlertDialog.Builder(ApuntesActivity.this).setPositiveButton("Sí", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Toast.makeText(ApuntesActivity.this, "Eliminando datos...",
                                Toast.LENGTH_SHORT).show();
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
                Toast.makeText(ApuntesActivity.this, position+" - "+id, Toast.LENGTH_SHORT).show();
            }
        });
        fabAgregarApunte.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent( ApuntesActivity.this, AgregarApunte.class);
                startActivity(intent);
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
            Toast.makeText(ApuntesActivity.this, "Eliminando...",
                    Toast.LENGTH_SHORT).show();
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
            Toast.makeText(ApuntesActivity.this, "Actualizando datos...",
                    Toast.LENGTH_SHORT).show();
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
//    private class ModificacionRemota extends AsyncTask<Void, Void, String>
//    {
//        // Atributos
//        String idApunte;
//        String nombreCuaderno;
//        // Constructor
//        public ModificacionRemota(String id,String nombre)
//        {
//            this.idCuaderno = id;
//            this.nombreCuaderno = nombre;
//        }
//        // Inspectores
//        protected void onPreExecute()
//        {
//            Toast.makeText(MainActivity.this, "Modificando...",
//                    Toast.LENGTH_SHORT).show();
//        }
//        protected String doInBackground(Void... voids)
//        {
//            try
//            {
//                String response = "";
//                Uri uri = new Uri.Builder()
//                        .scheme("http")
//                        .authority(servidor)
//                        .path("/ApiRest/cuadernos.php")
//                        .appendQueryParameter("idCuaderno", this.idCuaderno)
//                        .appendQueryParameter("nombreCuaderno",
//                                this.nombreCuaderno)
//                        .build();
//                // Create connection
//                URL url = new URL(uri.toString());
//                HttpURLConnection connection = (HttpURLConnection)
//                        url.openConnection();
//                connection.setReadTimeout(15000);
//                connection.setConnectTimeout(15000);
//                connection.setRequestMethod("PUT");
//                connection.setDoInput(true);
//                connection.setDoOutput(true);
//                int responseCode=connection.getResponseCode();
//                if (responseCode == HttpsURLConnection.HTTP_OK)
//                {
//                    String line;
//                    BufferedReader br=new BufferedReader(new
//                            InputStreamReader(connection.getInputStream()));
//                    while ((line=br.readLine()) != null)
//                    {
//                        response+=line;
//                    }
//                }
//                else
//                {
//                    response="";
//                }
//                connection.getResponseCode();
//                if (connection.getResponseCode() == 200)
//                {
//                    // Success
//                    Log.println(Log.ASSERT,"Resultado", "Registro modificado:"+response);
//                    connection.disconnect();
//                }
//                else
//                {
//                    // Error handling code goes here
//                    Log.println(Log.ASSERT,"Error", "Error");
//                }
//            }
//            catch(Exception e)
//            {
//                Log.println(Log.ASSERT,"Excepción", e.getMessage());
//            }
//            return null;
//        }
//        protected void onPostExecute(String mensaje)
//        {
//            Toast.makeText(MainActivity.this, "Actualizando datos...",
//                    Toast.LENGTH_SHORT).show();
//        }
//    }
}
