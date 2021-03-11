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
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class AgregarApunte extends AppCompatActivity {
//    int idFK=ApuntesActivity.idFK;
//    String idCuadernoFK = String.valueOf(idFK);
    String servidor= "192.168.1.79";
    AltaRemota alta;
    private Button btnAgregarApunte, btnCancelarNuevoApunte;
    private EditText etFechaApunte,etTexto;
    TextView txtFechaApunte, txtNombre;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_agregar_apunte);

        //Instanciar vistas
        etFechaApunte = findViewById(R.id.txtFechaApunte);
        etTexto = findViewById(R.id.txtApunte);
        btnAgregarApunte = findViewById(R.id.btnAgregarApunte);
        btnCancelarNuevoApunte = findViewById(R.id.btnCancelarNuevoApunte);

        //Agregar listener al boton guardar
        btnAgregarApunte.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Resetear errores a ambos
                etFechaApunte.setError(null);
                etTexto.setError(null);
                String fecha = etFechaApunte.getText().toString(),
                        texto= etTexto.getText().toString();
                if ("".equals(fecha)) {
                    etFechaApunte.setError("Escribe la fecha del Apunte");
                    etFechaApunte.requestFocus();
                    return;
                }
                if ("".equals(texto)){
                    etTexto.setError("Escribe el texto del Apunte");
                    return;
                }
                Toast.makeText(AgregarApunte.this, "Alta datos...", Toast.LENGTH_SHORT).show();
                txtNombre = findViewById(R.id.txtCuaderno);
                txtFechaApunte = findViewById(R.id.txtFechaApunte);
//                alta = new AltaRemota(txtFechaApunte.getText().toString(),
//                        txtNombre.getText().toString(), idCuadernoFK);
//                alta.execute();
                txtNombre.setFocusable(false);
            }
        });
        // El de cancelar simplemente cierra la actividad
        btnCancelarNuevoApunte.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
    private class AltaRemota extends AsyncTask<Void, Void, String>
    {
        // Atributos
        String fechaApunte, textoApunte, idCuadernoFK;
        // Constructor
        public AltaRemota(String fechaApunte, String txtoApunte, String idCuadernoFK)
        {
            this.fechaApunte = fechaApunte;
            this.textoApunte = txtoApunte;
            this.idCuadernoFK = idCuadernoFK;
        }
        // Inspectoras
        protected void onPreExecute()
        {
            Toast.makeText(AgregarApunte.this, "Alta..."+this.textoApunte, Toast.LENGTH_SHORT).show();
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
            txtFechaApunte = findViewById(R.id.txtFechaApunte);
            txtFechaApunte.setText(fechaApunte);
            txtNombre = findViewById(R.id.txtApunte);
            txtNombre.setText(textoApunte);
            Toast.makeText(AgregarApunte.this, "Alta Correcta...",
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