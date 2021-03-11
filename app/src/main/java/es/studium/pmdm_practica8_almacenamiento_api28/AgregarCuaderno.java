package es.studium.pmdm_practica8_almacenamiento_api28;

import androidx.appcompat.app.AppCompatActivity;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedWriter;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

public class AgregarCuaderno extends AppCompatActivity {
    String servidor= "192.168.1.79";
    AltaRemota alta;
    private Button btnAgregarCuaderno, btnCancelarNuevoCuaderno;
    private EditText etCuaderno;
    TextView txtNombre;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_agregar_cuaderno);

        //Instanciar vistas
        etCuaderno = findViewById(R.id.txtCuaderno);
        btnAgregarCuaderno = findViewById(R.id.btnAgregarCuaderno);
        btnCancelarNuevoCuaderno = findViewById(R.id.btnCancelarNuevoCuaderno);

        //Agregar listener al boton guardar
        btnAgregarCuaderno.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Resetear errores a ambos
                etCuaderno.setError(null);
                String texto = etCuaderno.getText().toString();
                if ("".equals(texto)) {
                    etCuaderno.setError("Escribe el nombre del cuaderno");
                    etCuaderno.requestFocus();
                    return;
                }
                Toast.makeText(AgregarCuaderno.this, "Alta datos...",
                        Toast.LENGTH_SHORT).show();
                txtNombre = findViewById(R.id.txtCuaderno);
                alta = new AltaRemota(txtNombre.getText().toString());
                alta.execute();
                txtNombre.setFocusable(false);
//                finish();
            }
        });
        // El de cancelar simplemente cierra la actividad
        btnCancelarNuevoCuaderno.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
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
            Toast.makeText(AgregarCuaderno.this, "Alta..."+this.nombreCuaderno,
                    Toast.LENGTH_SHORT).show();
        }
        protected String doInBackground(Void... argumentos)
        {
            try {
                // Crear la URL de conexión al API
                URL url = new
                        URL("http://"+servidor+"/ApiRest/cuadernos.php");
                // Crear la conexión HTTP
                HttpURLConnection myConnection = (HttpURLConnection)
                        url.openConnection();
                // Establecer método de comunicación.
                myConnection.setRequestMethod("POST");
                // Conexión exitosa
                String response = "";
                HashMap<String, String> postDataParams = new
                        HashMap<String, String>();
                postDataParams.put("nombreCuaderno",
                        this.nombreCuaderno);
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
            txtNombre = findViewById(R.id.cuaderno);
            txtNombre.setText(nombreCuaderno);
            Toast.makeText(AgregarCuaderno.this, "Alta Correcta...",
                    Toast.LENGTH_SHORT).show();
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