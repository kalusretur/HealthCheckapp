package itms.com.pe.app;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
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
import java.util.Locale;
import java.util.UUID;

import cn.pedant.SweetAlert.SweetAlertDialog;
import cz.msebera.android.httpclient.Header;

public class login extends AppCompatActivity {

    TextView twCreate;
    Button btnIngresar;
    EditText correo, clave;
    CheckBox chk_record;
    ProgressDialog prgDialog;
    private Global gb = new Global();
    /**/


    public static String cod_USR =null;

    public static int rol_id;
    private String  versionActualApp = "1721.1";

    /*Insert DB*/
    public static String CountryCode;
    public static String City;

    /*Correo Variable Global*/
    public static  String email_usr;

    /*Guardar Preferencias*/
    public static SharedPreferences preferences = null;
    SharedPreferences.Editor  editor;

    /*Alerta Version */
    SweetAlertDialog AlertVersion = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //
        twCreate = (TextView)findViewById(R.id.twCreate);
        btnIngresar = (Button)findViewById(R.id.btnIngresar);
        correo = (EditText)findViewById(R.id.txtUsuario);
        clave  = (EditText)findViewById(R.id.txtClave);
        chk_record = (CheckBox)findViewById(R.id.chk_record);
        /*INTANCIO LOS DATOS A GUARDAR*/
        preferences = getSharedPreferences("datos", Context.MODE_PRIVATE);
        editor= preferences.edit();

        // Instanciamos el progress dialog object
        prgDialog = new ProgressDialog(this);
        prgDialog.setMessage("Validando Credenciales");
        //set cancelable as False
        prgDialog.setCancelable(false);
        chk_record.setChecked(preferences.getBoolean("Chekeed", Boolean.parseBoolean("")));
        if(chk_record.isChecked()){
            correo.setText(preferences.getString("usuario",""));
            correo.setSelection(correo.getText().length());
            clave.setText(preferences.getString("password",""));
        }

        gb.paciente_log_nombre_all="";

        /*Programo el REGISTRO*/

        twCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(login.this, Registro.class);
                startActivity(intent);
                Log.d("Eso","Click");
            }
        });

        /*Programo el boton INGRESAR*/

        btnIngresar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                finish();
                Intent intent = new Intent(login.this, MenuOpciones.class);
                startActivity(intent);
            }
        });


        RequestParams params = new RequestParams();
        String IMEI = id(getBaseContext());
        params.put("imei", IMEI);
        validaPhone(params);

    }

    public void validaLoginApp(RequestParams params){
        prgDialog.show();
        AsyncHttpClient client = new AsyncHttpClient();
        client.post("http://"+gb.WebService+"/validarLoginAPP", params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                String responseStr = null;
                JSONObject jsonObject = null;

                try {
                    responseStr = new String(responseBody, "UTF-8");
                    jsonObject = new JSONObject(responseStr);
                    String correo_login = jsonObject.optString("usr_CORREO");

                    if(correo_login!="null"){
                        int activo = jsonObject.getInt("usr_ACTIVO");
                        email_usr= correo_login;
                            /*verifico si el usuario esta activo y entro al menu*/
                        if(activo==1){
                            prgDialog.dismiss();
                            guardarLogin();
                            /*Ingreso el usuarioLogueado*/
                            cod_USR = jsonObject.optString("cod_USR");
                            rol_id = jsonObject.optInt("usr_ROL");
                            //System.out.println("Mi rol es  : " + rol_id);
                            gb.paciente_log_nombre_all = jsonObject.optString("usr_TITULAR");
                            gb.clie_id_oracle = jsonObject.optString("clie_ID");
                            /*Ingreso en variables Pais y Ciudad*/
                            getContry();

                            /*vaido si existe paciente*/
                            RequestParams params = new RequestParams();
                            params.put("cod_p", cod_USR);
                            validaPaci(params);

                        }else{
                            prgDialog.dismiss();
                            AlertDialog.Builder builder = new AlertDialog.Builder(login.this);
                            builder.setTitle("Alerta");
                            builder.setMessage("El correo ingresado aun no esta activo, favor de revisar su correo electrónico.").setPositiveButton("ACEPTAR", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                }
                            });
                            AlertDialog alert = builder.create();
                            alert.show();
                        }


                    }else{
                        prgDialog.dismiss();
                        Log.d("Print", "No Hay valores");
                        Toast.makeText(getApplicationContext(), "Usuario y Constraseña incorrecto", Toast.LENGTH_SHORT).show();
                    }
                }catch (Exception e){
                    e.printStackTrace();
                    prgDialog.dismiss();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                prgDialog.dismiss();

            }
        });

    }



    public void validaPhone(RequestParams params){
        AsyncHttpClient client = new AsyncHttpClient();
        client.get("http://"+gb.WebService+"/validarDatos", params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                String responseStr = null;
                JSONObject jsonobject = null;
                String IMEI = id(getBaseContext());

                try {

                    responseStr = new String(responseBody, "UTF-8");
                    jsonobject = new JSONObject(responseStr);
                    String valPhone = jsonobject.getString("usr_CORREO");
                    Log.d("Printer ", IMEI);

                    if(valPhone!="null"){
                        twCreate.setVisibility(View.GONE);
                        Log.d("IMEI", IMEI);

                    }
                    else{
                        Log.d("IMEI", "Emei no existe :   "+IMEI);
                    }

                }catch (JSONException e) {
                    e.printStackTrace();
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
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

    public boolean isOnline() {
        ConnectivityManager cm =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }


    /*--LLamam a una WS para obtener Pais y Ciudad--*/

    public void getContry(){
        AsyncHttpClient client = new AsyncHttpClient();
        client.get("http://ip-api.com/json", new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                String responseStr = null;
                JSONObject jsonobject = null;
                try {
                    if(statusCode==200){

                        responseStr = new String(responseBody, "UTF-8");
                        jsonobject = new JSONObject(responseStr);

                        CountryCode = jsonobject.optString("countryCode");
                        City = jsonobject.optString("city");

                    }else{
                        Log.d("Error : " , "Error al obtener Country");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                if(statusCode!=200){
                    Log.d("Error getContry: " , error.getMessage());
                }
            }
        });
    }

    public void validaPaci(final RequestParams params){
        AsyncHttpClient client = new AsyncHttpClient();
        client.get("http://"+gb.WebService+"/validarExistePaci", params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                try {
                    String code = new String(responseBody, "UTF-8");
                    //System.out.println(code.getBytes());
                    int numPaci = Integer.parseInt(code);
                    if (numPaci>0){
                        /*Abro la ventana de Menu*/
                        RequestParams logs = new RequestParams();
                        logs.put("accion", "Logueo APP");
                        logs.put("cod_usr", cod_USR);
                        gb.registroLogs(logs);
                        finish();
                        Intent intent = new Intent(login.this, MenuOpciones.class);
                        startActivity(intent);

                    }
                    else{
                        /*Abro la ventana de Menu*/
                        finish();
                        Intent intent = new Intent(login.this, registro_first_paciente.class);
                        startActivity(intent);
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }


            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {

            }
        });

    }


    void saveLogin(){
        editor.putString("usuario",correo.getText().toString());
        editor.putString("password",clave.getText().toString());
        editor.putBoolean("Chekeed",chk_record.isChecked());
        editor.commit();
        finish();
    }

    void guardarLogin(){
        if(chk_record.isChecked()==true){
            saveLogin();
            Log.d("Usario xx22", preferences.getString("usuario",""));
        }else{
            editor.putString("usuario","");
            editor.putString("password","");
            editor.putBoolean("Chekeed", false);
            editor.commit();
            finish();
        }
    }


    /**--------Validar Version-----*/
    void validarVersionAPP(){
        AsyncHttpClient client = new AsyncHttpClient();
        client.post("http://"+gb.WebService+"/versionApp", new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                String responseStr = null;
                JSONObject jsonobject = null;

                try {
                    responseStr = new String(responseBody, "UTF-8");
                    jsonobject = new JSONObject(responseStr);
                    String version = jsonobject.optString("version_actual");

                    if(version.equals(versionActualApp)){
                        //System.out.println("Version Correcta");
                        datosLogin();
                    }else{
                        methodAlertaVersionDesfasada();
                        //System.out.println("Version desfasada");
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }


            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                System.out.println("Error al validadr version App");
            }
        });
    }


    void datosLogin(){
        String correoIn = correo.getText().toString();
        String claveIn = null;
        try {
            claveIn = gb.getMD5(clave.getText().toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
        RequestParams params = new RequestParams();
        params.put("email",correoIn);
        params.put("passwd",claveIn);
        validaLoginApp(params);
    }

    void methodAlertaVersionDesfasada(){
        AlertVersion = new SweetAlertDialog(login.this, SweetAlertDialog.PROGRESS_TYPE)
                .setTitleText("Cargando");
        AlertVersion.show();
        AlertVersion.setCancelable(false);
        AlertVersion.setTitleText("Version Actual Desfasada")
                .setContentText("Actualizar a la ultima version \n para poder utilizar nuestra Aplicacion.")
                .setConfirmText("Aceptar")
                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sweetAlertDialog) {
                        try {
                            Intent intent= new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=itms.com.pe.app"));
                            startActivity(intent);
                            AlertVersion.dismiss();
                            finish();
                        }
                        catch (Exception e){
                            e.printStackTrace();
                        }
                    }
                })
                .changeAlertType(SweetAlertDialog.ERROR_TYPE);
    }




}
