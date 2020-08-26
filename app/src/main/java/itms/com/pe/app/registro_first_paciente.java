package itms.com.pe.app;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import cz.msebera.android.httpclient.Header;

public class registro_first_paciente extends AppCompatActivity {

    Spinner parentesco,genero,tipodoc;

    Toolbar toolbar;
    Button btnRegistroUsuario;
    ProgressDialog prgDialog;

    login lg = new login();
    Global gb = new Global();
    final Calendar c = Calendar.getInstance();

    int year = c.get(Calendar.YEAR);
    int month = c.get(Calendar.MONTH);
    int day = c.get(Calendar.DAY_OF_MONTH);



    /*Datos del Form*/
    EditText nombres, apellido_pat,apellido_mat, celphone, fecNaci, numero_doc;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro_first_paciente);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        btnRegistroUsuario = (Button)findViewById(R.id.btnIngresarPaciente);
        /**/
        nombres= (EditText)findViewById(R.id.txtNombreFpaci);
        apellido_pat= (EditText)findViewById(R.id.txtApellidoFpaci_pat);
        apellido_mat= (EditText)findViewById(R.id.txtApellidoFpaci_mat);
        celphone= (EditText)findViewById(R.id.txtCelularFpaci);
        fecNaci = (EditText)findViewById(R.id.txtFecNac);
        numero_doc= (EditText)findViewById(R.id.txtNumDocFpaci);

        // Instanciamos el progress dialog object
        prgDialog = new ProgressDialog(this);
        prgDialog.setMessage("Guardando Paciente");
        prgDialog.setCancelable(false);

        llenarSpParentesco();
        llenarSpGenero();
        llenarSpTipoDoc();
        toolbar.setTitleTextColor(Color.WHITE);
        toolbar.setTitle("Registro de Paciente");
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                confirmacionSalir();
            }
        });






        fecNaci.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                showDialog(0);
                return true;
            }
        });

        btnRegistroUsuario.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(genero.getSelectedItemId()==0){
                    alertaVerificacion("Debe seleccionar el género");
                }else if(apellido_pat.getText().length()<1){
                    alertaVerificacion("Ingrese apellido paterno");
                }else if(apellido_mat.getText().length()<1){
                    alertaVerificacion("Ingrese apellido materno");
                }else if(nombres.getText().length()<1){
                    alertaVerificacion("Ingrese nombre");
                }else if(celphone.getText().length()<1){
                    alertaVerificacion("Ingrese un número de celular");
                }else if(fecNaci.getText().length()<1){
                    alertaVerificacion("Debe seleccionar su fecha de nacimiento");
                }else if(tipodoc.getSelectedItemId()==0){
                    alertaVerificacion("Debe seleccionar el tipo de documento");
                }else if(numero_doc.getText().length()<1){
                    alertaVerificacion("Ingrese el número de documento");
                }else {

                    prgDialog.show();
                    //Toast.makeText(getApplicationContext(), lg.cod_USR, Toast.LENGTH_SHORT).show();

                    SimpleDateFormat fromUser = new SimpleDateFormat("dd/MM/yyyy");
                    SimpleDateFormat sdffEC = new SimpleDateFormat("yyyy-MM-dd");
                    String fechaInsert = null;

                    try {
                        fechaInsert = sdffEC.format(fromUser.parse(fecNaci.getText().toString().trim()));
                        RequestParams params = new RequestParams();
                        params.put("cod_usr", lg.cod_USR);
                        params.put("paci_parentesco", parentesco.getSelectedItemId());
                        params.put("paci_genero", obtenerGenero());
                        params.put("paci_nom", nombres.getText().toString());
                        params.put("paci_ape_pat", apellido_pat.getText().toString());
                        params.put("paci_ape_mat", apellido_mat.getText().toString());
                        params.put("paci_phone", celphone.getText().toString());
                        params.put("paci_fec_nac", fechaInsert);
                        params.put("paci_paci_tip_doc", tipodoc.getSelectedItemId());
                        params.put("paci_rut", numero_doc.getText().toString());
                        params.put("paci_site", lg.CountryCode);
                        params.put("paci_city", lg.City);
                        params.put("paci_activo", "1");
                        crearPaciente(params);
                    } catch (ParseException e) {
                        e.printStackTrace();
                        prgDialog.dismiss();
                        Toast.makeText(getApplicationContext(), "Ocurrió un error al guardar el Paciente.", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        /**/

    }

    void llenarSpParentesco(){

        String [] array_parentesco = getResources().getStringArray(R.array.ParentescoFirst);
        //String [] values = {"AC-CARRERAS TECNICAS","AD-PROGRAMA DE ADELANTO","DC-DIPLOMADOS CIBERTEC"};
        parentesco = (Spinner) findViewById(R.id.sp_parentesco);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, array_parentesco);
        adapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
        parentesco.setAdapter(adapter);

    }

    void llenarSpGenero(){

        String [] array_genero = getResources().getStringArray(R.array.Genero);
        //String [] values = {"AC-CARRERAS TECNICAS","AD-PROGRAMA DE ADELANTO","DC-DIPLOMADOS CIBERTEC"};
        genero = (Spinner) findViewById(R.id.sp_genero);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, array_genero);
        adapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
        genero.setAdapter(adapter);

    }

    void llenarSpTipoDoc(){

        String [] array_tipodoc = getResources().getStringArray(R.array.TipoDocumento);
        //String [] values = {"AC-CARRERAS TECNICAS","AD-PROGRAMA DE ADELANTO","DC-DIPLOMADOS CIBERTEC"};
        tipodoc = (Spinner) findViewById(R.id.sp_tipdoc);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, array_tipodoc);
        adapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
        tipodoc.setAdapter(adapter);

    }

    /*-------------*/
    protected Dialog onCreateDialog(int id) {
        //DatePickerDialog dpDialog = new DatePickerDialog(this, datePickerListener, year, month, day);
        DatePickerDialog dpDialog = new DatePickerDialog(this,android.R.style.Theme_Holo_Light_Dialog_MinWidth, datePickerListener, year, month, day);
        dpDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        DatePicker datePicker = dpDialog.getDatePicker();

        Calendar calendar = Calendar.getInstance();//get the current day
        datePicker.setMaxDate(calendar.getTimeInMillis());
        return dpDialog;
    }


    private DatePickerDialog.OnDateSetListener datePickerListener = new DatePickerDialog.OnDateSetListener() {
        public void onDateSet(DatePicker view, int selectedYear,
                              int selectedMonth, int selectedDay) {
            day = selectedDay;
            month = selectedMonth;
            year = selectedYear;
            fecNaci.setText(selectedDay + "/" + (selectedMonth + 1) + "/"
                    + selectedYear);
        }
    };

    String obtenerGenero(){
        int posicion = (int) genero.getSelectedItemId();
        switch (posicion){
            case 1:
                return "M";
            case 2:
                return "F";
        }
        return "E";
    };



    void confirmacionSalir(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this, android.R.style.Theme_Holo_Light_Dialog_MinWidth);
        builder.setMessage("¿Desea cerrar la aplicación?")
                .setCancelable(false)
                .setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        finish();
                        //onBackPressed();
                    }
                })
                .setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        //  Action for 'NO' Button
                        dialog.cancel();
                    }
                });

        //Creating dialog box
        AlertDialog alert = builder.create();
        //Setting the title manually
        alert.setTitle("Alerta");
        alert.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        alert.show();
    }




    public void crearPaciente(RequestParams params){
        AsyncHttpClient client = new AsyncHttpClient();
        client.post("http://"+gb.WebService+"/insertPaci", params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                System.out.println(statusCode);
                String responseStr = null;
                JSONObject jsonObject = null;

                try {
                    responseStr = new String(responseBody, "UTF-8");
                    jsonObject = new JSONObject(responseStr);

                    if(statusCode==201){
                        String paci = jsonObject.optString("paciente_login");
                        gb.paciente_log_nombre_all=paci;
                        prgDialog.dismiss();
                        Intent intent = new Intent(registro_first_paciente.this, MenuOpciones.class);
                        startActivity(intent);
                        Toast.makeText(getApplicationContext(), "Se guardo el Paciente.", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                    else{
                        prgDialog.dismiss();
                        Toast.makeText(getApplicationContext(), "Ocurrio un error", Toast.LENGTH_SHORT).show();
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                System.out.println(statusCode);
                prgDialog.dismiss();
                Toast.makeText(getApplicationContext(), "Ocurrio un error con el Servidor", Toast.LENGTH_SHORT).show();
            }
        });
    }


    void alertaVerificacion(String text){
        android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(registro_first_paciente.this);
        builder.setTitle("Atención");
        builder.setMessage(text).setPositiveButton("ACEPTAR", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        android.support.v7.app.AlertDialog alert = builder.create();
        alert.show();
    }
}
