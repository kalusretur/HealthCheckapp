package itms.com.pe.app;


import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.util.UUID;

import cz.msebera.android.httpclient.Header;
import itms.com.pe.app.Funciones.validarEmail;

public class Registro extends AppCompatActivity {

    Button btnRegistrar;
    ProgressDialog prgDialog;
    Toolbar toolbar;
    TextView terminos;
    EditText correo,phone,contraseña1, contraseña2;
    CheckBox checkBox;

    private Global gb = new Global();
    private validarEmail validaMAIL = new validarEmail();
    private Encryptor encryptor = new Encryptor();



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        btnRegistrar = (Button) findViewById(R.id.btnRegistroRG);
        terminos = (TextView) findViewById(R.id.condicionesRG);
        /*--REgistro--*/

        correo = (EditText) findViewById(R.id.txtCorreoRG);
        phone = (EditText) findViewById(R.id.txtCelularRG);
        contraseña1 = (EditText) findViewById(R.id.txtPassRG);
        contraseña2 = (EditText) findViewById(R.id.txtPass2RG);
        checkBox = (CheckBox) findViewById(R.id.chkTerminosRG);

        /*Creo el prgDialog*/
        prgDialog = new ProgressDialog(this);
        prgDialog.setMessage("Validando Datos");
        prgDialog.setCancelable(false);


        /*Boton de Return*/

        toolbar.setTitleTextColor(Color.WHITE);
        toolbar.setTitle("Registro de Usuario");
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // back button pressed
                onBackPressed();
            }
        });

        /*Boton de Registrar*/


        btnRegistrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(correo.getText().length()<1){
                    AlertDialog.Builder builder = new AlertDialog.Builder(Registro.this);
                    builder.setTitle("Atención");
                    builder.setMessage("Ingrese un correo electrónico.").setPositiveButton("ACEPTAR", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    });
                    AlertDialog alert = builder.create();
                    alert.show();
                }
                else if(validaMAIL.validateEmail(correo.getText().toString())==false){
                    AlertDialog.Builder builder = new AlertDialog.Builder(Registro.this);
                    builder.setTitle("Atención");
                    builder.setMessage("Ingrese un correo electrónico Válido.").setPositiveButton("ACEPTAR", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    });
                    AlertDialog alert = builder.create();
                    alert.show();

                }
                else if(phone.getText().length()<1){
                    AlertDialog.Builder builder = new AlertDialog.Builder(Registro.this);
                    builder.setTitle("Atención");
                    builder.setMessage("Ingrese su número de celular").setPositiveButton("ACEPTAR", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    });
                    AlertDialog alert = builder.create();
                    alert.show();
                }
                else if(contraseña1.getText().length()<1){
                    AlertDialog.Builder builder = new AlertDialog.Builder(Registro.this);
                    builder.setTitle("Atención");
                    builder.setMessage("Ingrese una contraseña.").setPositiveButton("ACEPTAR", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    });
                    AlertDialog alert = builder.create();
                    alert.show();
                }
                else  if(contraseña2.getText().length()<1){
                    AlertDialog.Builder builder = new AlertDialog.Builder(Registro.this);
                    builder.setTitle("Atención");
                    builder.setMessage("Repita su contraseña.").setPositiveButton("ACEPTAR", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    });
                    AlertDialog alert = builder.create();
                    alert.show();
                }
                else if((contraseña1.getText().toString()).equals(contraseña2.getText().toString())){

                    if(checkBox.isChecked()){
                        /*--Realizo el registro del paciente--*/

                        String IME = id(getBaseContext());
                        Log.d("Click", IME);

                        //Toast.makeText(getApplicationContext(), IME, Toast.LENGTH_SHORT).show();

                        RequestParams params = new RequestParams();
                        params.put("email", correo.getText().toString());
                        validarEmail(params);



                    }else{
                            Toast.makeText(getApplicationContext(), "Debe aceptar los terminos y condiciones.", Toast.LENGTH_SHORT).show();
                    }
                }else{

                    AlertDialog.Builder builder = new AlertDialog.Builder(Registro.this);
                    builder.setTitle("Atención");
                    builder.setMessage("Las contraseñas ingresadas no coninciden.").setPositiveButton("ACEPTAR", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    });
                    AlertDialog alert = builder.create();
                    alert.show();
                }


            }
        });

        /*Terminos y condiciones*/
        terminos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(Registro.this);
                TextView msgTitle = new TextView(Registro.this);
                msgTitle.setText("Términos y Condiciones");
                msgTitle.setTextColor(Color.BLACK);
                msgTitle.setPadding(10, 15, 15, 10);
                msgTitle.setTypeface(null, Typeface.BOLD);
                msgTitle.setTextSize(17);
                msgTitle.setGravity(Gravity.CENTER_HORIZONTAL);
                //builder.setView(msgTitle);
                //builder.setTitle("Términos y Condiciones");
                /*builder.setMessage("Acepto los Términos y Condiciones y brindo mi consentimiento para el tratamiento de mis datos de caracter personal proporcionados en el presente formulario" +
                        " de inscripción al equipo de Telemedicina de Perú S.A, para que sean analizados, procesados y transferidos de acuerdo a su Política de Privacidad y Protección de Datos Personales, " +
                        "de tal manera que puedan brindarme todos los servicios que ofrecen de manera directa o a través de terceros." +
                        "\n\n" +
                        "Si tienes alguna duda o sugerencia puede escribirnos a  info@itms.com.pe y con gusto responderemos a tus dudas o sugerencias. Si Piensas en Salud piensa en Telemedicina de Perú S.A.");*/
                builder.setMessage("Acepto los Términos y Condiciones y brindo mi consentimiento para el tratamiento de mis datos de caracter personal proporcionados en el presente formulario.");
                AlertDialog alert = builder.create();
                alert.setCustomTitle(msgTitle);
                alert.show();
            }
        });

    }


    /*-Metodo validar Exitencioa de mail-*/
    public void validarEmail(RequestParams params){
        prgDialog.show();
        AsyncHttpClient client = new AsyncHttpClient();
        client.get("http://"+gb.WebService+"/validarDatos", params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                String responseStr = null;
                JSONObject jsonobject = null;

                try {
                    responseStr = new String(responseBody, "UTF-8");
                    jsonobject = new JSONObject(responseStr);
                    String emailverf = jsonobject.optString("usr_CORREO");

                    if (emailverf!="null"){
                        prgDialog.dismiss();
                        Log.d("Validar Mail : " , "El correo Existe");
                        AlertDialog.Builder builder = new AlertDialog.Builder(Registro.this);
                        builder.setTitle("Alerta");
                        builder.setMessage("El correo electrónico ya está registrado.").setPositiveButton("ACEPTAR", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        });
                        AlertDialog alert = builder.create();
                        alert.show();
                    }else{
                        //prgDialog.dismiss();
                        //Log.d("Error : " , "El correo no Existe");
                        String IMEI = id(getBaseContext());
                        RequestParams params = new RequestParams();
                        String claveMD5 = contraseña1.getText().toString();
                        params.put("mail", correo.getText().toString());
                        params.put("password", getMD5(claveMD5));
                        params.put("phone", phone.getText().toString());
                        params.put("imei", IMEI);
                        crearUsuario(params);
                    }

                }catch (JSONException e){
                    e.printStackTrace();
                }catch (UnsupportedEncodingException e){
                    e.printStackTrace();
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                prgDialog.dismiss();
                if(statusCode==0){
                    Toast.makeText(getApplicationContext(), "Ocurrio un error con el servidor", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }


    /*Metodo identificador Único*/

    private static String uniqueID = null;
    private static final String PREF_UNIQUE_ID = "PREF_UNIQUE_ID";

    public synchronized static String id(Context context) {
        if (uniqueID == null) {
            SharedPreferences sharedPrefs = context.getSharedPreferences(
                    PREF_UNIQUE_ID, Context.MODE_PRIVATE);
            uniqueID = sharedPrefs.getString(PREF_UNIQUE_ID, null);
            if (uniqueID == null) {
                uniqueID = UUID.randomUUID().toString();
                SharedPreferences.Editor editor = sharedPrefs.edit();
                editor.putString(PREF_UNIQUE_ID, uniqueID);
                editor.commit();
            }
        }
        return uniqueID;
    }

    public void sendingMail(RequestParams params){
        AsyncHttpClient client = new AsyncHttpClient();
        client.get("http://www.pitperu.com.pe/app/correo_bienvenida.php", params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                Log.d("Exito : ", "Se envio el correo con exito.");
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                Log.d("Error envio : ", "Error en el envio de correo.");

            }
        });
    }


    public void crearUsuario(RequestParams params){
        AsyncHttpClient client = new AsyncHttpClient();
        client.post("http://"+gb.WebService+"/registroAccount", params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                if(statusCode==201){
                    prgDialog.dismiss();
                    Log.d("Mensaje  : ", " Se ingreso el Log con Éxito ");
                    String mailSend = null;
                    try {
                        String key = "21AE50A80ITMS1O4"; //llave
                        String iv = "1957533836ITMSAB";
                        mailSend = encryptor.encrypt(key,iv, correo.getText().toString());
                    } catch (Exception e) {
                        e.printStackTrace();
                        Log.d("Error : ", "  Al encripar");
                    }

                    RequestParams mail = new RequestParams();
                    mail.put("c", correo.getText().toString());
                    mail.put("lnk", mailSend);
                    sendingMail(mail);

                        /*Creo la alerta*/
                    AlertDialog.Builder builder = new AlertDialog.Builder(Registro.this);
                    builder.setTitle("Alerta");
                    builder.setMessage("Por favor revise su correo electrónico para verificar su cuenta.").setPositiveButton("ACEPTAR", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            onBackPressed();
                        }
                    });
                    AlertDialog alert = builder.create();
                    alert.setCancelable(false);
                    alert.show();
                }else{
                    prgDialog.dismiss();
                    Toast.makeText(getApplicationContext(), "Ocurrio un error al registrar la Cuenta", Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                //Log.d("Mensaje  : ", " Ocurrio un error al ingresar el Log ");
                prgDialog.dismiss();
                Toast.makeText(getApplicationContext(), "Ocurrio un error con el Servidor", Toast.LENGTH_SHORT).show();
            }
        });
    }

    /*Otros metodos*/

    public String getMD5(String cadena) throws Exception {

        MessageDigest md = MessageDigest.getInstance("MD5");
        byte[] b = md.digest(cadena.getBytes());

        int size = b.length;
        StringBuilder h = new StringBuilder(size);
        for (int i = 0; i < size; i++) {

            int u = b[i] & 255;

            if (u < 16)
            {
                h.append("0").append(Integer.toHexString(u));
            }
            else
            {
                h.append(Integer.toHexString(u));
            }
        }
        return h.toString();
    }

}
