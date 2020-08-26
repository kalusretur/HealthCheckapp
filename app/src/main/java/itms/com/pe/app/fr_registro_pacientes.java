package itms.com.pe.app;


import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import cz.msebera.android.httpclient.Header;


/**
 * A simple {@link Fragment} subclass.
 */
public class fr_registro_pacientes extends Fragment {

    View v;
    Button btnIngresarPaciente;
    MenuOpciones menu = new MenuOpciones();

    /*-------VARIABLES PARA INSERTAR NUEVOS PACIENTES---------*/
    Spinner parentesco,genero,tipodoc;
    ProgressDialog prgDialog;

    login lg = new login();
    Global gb = new Global();
    final Calendar c = Calendar.getInstance();

    int year = c.get(Calendar.YEAR);
    int month = c.get(Calendar.MONTH);
    int day = c.get(Calendar.DAY_OF_MONTH);

    /*Datos del Form*/
    EditText nombres, apellido_pat,apellido_mat, celphone, fecNaci, numero_doc;


    public fr_registro_pacientes() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        getActivity().setTitle("Registro de Pacientes");
        v = inflater.inflate(R.layout.fragment_fr_registro_pacientes, container, false);

        /*---------------------------*/
        nombres= (EditText)v.findViewById(R.id.txtNombreFpaci);
        apellido_pat= (EditText)v.findViewById(R.id.txtApellidoFpaci_pat);
        apellido_mat= (EditText)v.findViewById(R.id.txtApellidoFpaci_mat);
        celphone= (EditText)v.findViewById(R.id.txtCelularFpaci);
        fecNaci = (EditText)v.findViewById(R.id.txtFecNac);
        numero_doc= (EditText)v.findViewById(R.id.txtNumDocFpaci);
        btnIngresarPaciente = (Button)v.findViewById(R.id.btnIngresarPaciente);


        // Instanciamos el progress dialog object
        prgDialog = new ProgressDialog(getContext());
        prgDialog.setMessage("Guardando Paciente");
        prgDialog.setCancelable(false);

        llenarSpParentesco();
        llenarSpGenero();
        llenarSpTipoDoc();


        fecNaci.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerDialog dpDialog = new DatePickerDialog(getContext(),android.R.style.Theme_Holo_Light_Dialog_MinWidth, datePickerListener, year, month, day);
                dpDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                DatePicker datePicker = dpDialog.getDatePicker();
                dpDialog.setTitle("Fecha de Nacimiento");

                Calendar calendar = Calendar.getInstance();//get the current day
                datePicker.setMaxDate(calendar.getTimeInMillis());
                dpDialog.show();
            }
        });




        // Inflate the layout for this fragment


        btnIngresarPaciente.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(parentesco.getSelectedItemId()==0){
                    alertaVerificacion("Debe seleccionar el parentesco");
                }
                else if(genero.getSelectedItemId()==0){
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
                    //menu.navigationView.getMenu().clear();
                    //menu.navigationView.getMenu().getItem(3).setVisible(false);
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
                        params.put("paci_paci_tip_doc", tipoDocumento());
                        params.put("paci_rut", numero_doc.getText().toString());
                        params.put("paci_site", lg.CountryCode);
                        params.put("paci_city", lg.City);
                        params.put("paci_activo", "1");
                        crearPaciente(params);
                        prgDialog.dismiss();/*
                        Toast.makeText(getContext(), "Se guardo el Paciente.", Toast.LENGTH_SHORT).show();
                        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                        fragmentManager.beginTransaction().replace(R.id.contenedor, new fr_servicios()).commit();*/

                    } catch (ParseException e) {
                        e.printStackTrace();
                        prgDialog.dismiss();
                        Toast.makeText(getContext(), "Ocurrió un error al guardar el Paciente.", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        return v;
    }


    void llenarSpParentesco(){

        String [] array_parentesco = getResources().getStringArray(R.array.Parentesco);
        //String [] values = {"AC-CARRERAS TECNICAS","AD-PROGRAMA DE ADELANTO","DC-DIPLOMADOS CIBERTEC"};
        parentesco = (Spinner) v.findViewById(R.id.sp_parentesco);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, array_parentesco);
        adapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
        parentesco.setAdapter(adapter);

    }

    void llenarSpGenero(){

        String [] array_genero = getResources().getStringArray(R.array.Genero);
        //String [] values = {"AC-CARRERAS TECNICAS","AD-PROGRAMA DE ADELANTO","DC-DIPLOMADOS CIBERTEC"};
        genero = (Spinner) v.findViewById(R.id.sp_genero);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, array_genero);
        adapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
        genero.setAdapter(adapter);

    }

    void llenarSpTipoDoc(){

        String [] array_tipodoc = getResources().getStringArray(R.array.TipoDocumento);
        //String [] values = {"AC-CARRERAS TECNICAS","AD-PROGRAMA DE ADELANTO","DC-DIPLOMADOS CIBERTEC"};
        tipodoc = (Spinner) v.findViewById(R.id.sp_tipdoc);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, array_tipodoc);
        adapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
        tipodoc.setAdapter(adapter);

    }

    /*-------------*/
    protected Dialog onCreateDialog(int id) {
        //DatePickerDialog dpDialog = new DatePickerDialog(this, datePickerListener, year, month, day);
        DatePickerDialog dpDialog = new DatePickerDialog(getContext(),android.R.style.Theme_Holo_Light_Dialog_MinWidth, datePickerListener, year, month, day);
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

    String tipoDocumento(){
        int posicion = (int) tipodoc.getSelectedItem();
        switch (posicion){
            case 1:
                return "01";
            case 2:
                return "07";
            case 3:
                return "04";
        }
        return "E";
    }


    public void crearPaciente(RequestParams params){
        AsyncHttpClient client = new AsyncHttpClient();
        client.post("http://"+gb.WebService+"/insertPaci", params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                System.out.println(statusCode);
                if(statusCode==201){
                    prgDialog.dismiss();
                    FragmentManager fragmentManager=getActivity().getSupportFragmentManager();
                    fragmentManager.beginTransaction().replace(R.id.contenedor, new fr_servicios()).commit();
                    Toast.makeText(getContext(), "Se guardo el Paciente.", Toast.LENGTH_SHORT).show();
                }
                else{
                    prgDialog.dismiss();
                    Toast.makeText(getContext(), "Ocurrio un error", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                System.out.println(statusCode);
                prgDialog.dismiss();
                Toast.makeText(getContext(), "Ocurrio un error", Toast.LENGTH_SHORT).show();
            }
        });
    }


    void alertaVerificacion(String text){
        android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(getContext());
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
