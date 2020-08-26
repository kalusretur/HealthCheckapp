package itms.com.pe.app;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.DialogInterface;
import android.graphics.Color;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import cn.pedant.SweetAlert.SweetAlertDialog;
import cz.msebera.android.httpclient.Header;
import ir.mirrajabi.searchdialog.SimpleSearchDialogCompat;
import ir.mirrajabi.searchdialog.core.BaseSearchDialogCompat;
import ir.mirrajabi.searchdialog.core.SearchResultListener;
import ir.mirrajabi.searchdialog.core.Searchable;
import itms.com.pe.app.Culqi.Card;
import itms.com.pe.app.Culqi.Token;
import itms.com.pe.app.Culqi.TokenCallback;

import static itms.com.pe.app.R.color.material_blue_grey_950;

public class confirmacion_servicio extends AppCompatActivity {

    Toolbar toolbar;
    login lg = new login();
    Button btnProcesar;
    TextView paciente, formapago, precio, especialidad, motivo;
    Switch switchMarcapaso;
    Global gl = new Global();
    MenuOpciones mn = new MenuOpciones();
    EditText txtTarjeta;
    ProgressDialog prgBarra;

    /*Seleccion Paciente*/
    Boolean selecPaciente=false;
    Boolean selecFormaPago=false;
    Boolean selecEspecialidad=false;

    private int i = -1;
    SweetAlertDialog pDialog = null;
    SweetAlertDialog pDialogOK = null;
    SweetAlertDialog pDialogOK2 = null;


    /*Confirmto tipo de seleccion de pago*/
    int tipoPago= 0; //1 es tajeta Online y 2 efectivo
    /*
    String[] value = new String[]{
            "Transferencia Bancaria",
            "Tarjeta (POS)",
            "Efectivo"
    };
    */

    String[] value = new String[]{
            "Tarjeta (Online)",
            "Efectivo",
            "Transferencia Bancaria"
    };

    String[] valueTeleconsulta = new String[]{
            "Tarjeta (Online)"
    };
    /*Culqui*/
    String pk_public=null,pk_private=null;

    //Integer[] icons = new Integer[] {R.drawable.tarjeta, R.drawable.pos, R.drawable.efectivo};
    Integer[] icons = new Integer[] {R.drawable.tarjeta, R.drawable.efectivo, R.drawable.efectivo};
    ArrayList<PacientesBE> listarPacientes = new ArrayList<>();
    ArrayList<EspecialidadTABE> listaEspecialidades = new ArrayList<>();
    /*Lista Procedimientos DomiciLab*/
    ArrayList<DomicilabBE> listaDomicilab = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirmacion_servicio);

        /*Instancio Variables*/
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        prgBarra = new ProgressDialog(this, AlertDialog.THEME_HOLO_LIGHT);
        prgBarra.setTitle("Cargado");
        prgBarra.setMessage("Cargando...");
        //sp_costo = (Spinner) findViewById(R.id.sp_costo);
        btnProcesar = (Button) findViewById(R.id.btn_confirmacion);
        paciente = (TextView) findViewById(R.id.txtPaciente);
        formapago = (TextView) findViewById(R.id.txtFormaPago);
        precio = (TextView) findViewById(R.id.txtPrecio);
        switchMarcapaso = (Switch) findViewById(R.id.switch_marcapaso);
        txtTarjeta = (EditText) findViewById(R.id.txtNumerotarjeta) ;
        especialidad = (TextView) findViewById(R.id.txtEspecialidad);
        motivo = (TextView) findViewById(R.id.txtMotivo);

        precio.setText("Costo del Servicio "+gl.servicioDesc);

        toolbar.setTitleTextColor(Color.WHITE);
        toolbar.setTitle("Confirmación");
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });



        btnProcesar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MethodregistroCita();

            }
        });

        paciente.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //OlddialogPrecios();
                //System.out.println(listarPacientes.get(1).getPACIENTE());
                dialogPacientes();
            }
        });

        formapago.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(gl.servicioID==9){
                    dialogPrecios(valueTeleconsulta);
                }else{
                    dialogPrecios(value);
                }
                //dialogPrecios(value);

            }
        });

        especialidad.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialogEspecilidadTA();
            }
        });



        /*Cargo Pacientes*/
        RequestParams params = new RequestParams();
        params.put("cod_usr",lg.cod_USR);
        CargarPacientes(params);
        CargarEspecialidad();

        if(gl.servicioID==4){
            switchMarcapaso.setVisibility(View.VISIBLE);
        }else if(gl.servicioID==9){
            especialidad.setVisibility(View.VISIBLE);
            motivo.setVisibility(View.VISIBLE);
            precio.setVisibility(View.GONE);
        }
    }



    /*Otros métodos*/
    void OlddialogPrecios(){
        AlertDialog.Builder alertdialogbuilder = new AlertDialog.Builder(confirmacion_servicio.this, R.style.YourDialogStyle);
        alertdialogbuilder.setTitle(" Seleccione el método de Pago ");
        alertdialogbuilder.setItems(value, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String selectedText = Arrays.asList(value).get(which);
                System.out.println(selectedText);
            }
        });

        AlertDialog dialog = alertdialogbuilder.create();
        dialog.show();
        dialog.getWindow().setBackgroundDrawableResource(R.color.BLANCO);
    }
    void dialogPacientes(){
        AlertDialog.Builder dialog= new AlertDialog.Builder(this, R.style.YourDialogStyle);
        dialog.setTitle(" Seleccione un Paciente");
        final List<String> lstPacientes = new ArrayList<>();
        final List<Integer> lstCod_paci = new ArrayList<>();
        for(int i=0; i<listarPacientes.size(); i++){
            lstPacientes.add(listarPacientes.get(i).getPACIENTE());
        }
        final CharSequence[] allPacientes = lstPacientes.toArray(new String[lstPacientes.size()]);
        dialog.setItems(allPacientes, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String valueSelect = allPacientes[which].toString();
                paciente.setText(valueSelect);
                paciente.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.downico2, 0);

                for (PacientesBE pac : listarPacientes){
                    if(pac.getPACIENTE().equals(valueSelect)){
                        selecPaciente=true;
                        activoButtonConfirmacion(gl.servicioID);
                        gl.paci_id_usr = pac.getPACI_ID();
                        gl.cel_paci_cita = pac.getPACI_PHONE();
                        //System.out.println(pac.getPACI_ID());
                    }
                }

            }
        });
        AlertDialog dialog2 = dialog.create();
        dialog2.show();
        dialog2.getWindow().setBackgroundDrawableResource(R.color.BLANCO);

    }
    void dialogEspecilidadTA(){
        AlertDialog.Builder dialog= new AlertDialog.Builder(this, R.style.YourDialogStyle);
        dialog.setTitle(" Seleccione una Especialidad");
        final List<String> lstDialogEspecialidad  = new ArrayList<>();
        //final List<Integer> lstCodigoEspecialidad = new ArrayList<>();
        for(int i=0; i<listaEspecialidades.size(); i++){
            lstDialogEspecialidad.add(listaEspecialidades.get(i).getText_dialog());
            //lstCodigoEspecialidad.add(listaEspecialidades.get(i).getId_procedimiento());
        }
        final CharSequence[] allEspecialidad = lstDialogEspecialidad.toArray(new String[lstDialogEspecialidad.size()]);
        dialog.setItems(allEspecialidad, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String valueSelect = allEspecialidad[which].toString();
                especialidad.setText(valueSelect);
                especialidad.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.downico2, 0);

                for (EspecialidadTABE espe : listaEspecialidades){
                    if(espe.getText_dialog().equals(valueSelect)){
                        selecEspecialidad=true;
                        activoButtonConfirmacion(gl.servicioID);
                        gl.servicioDesc = espe.getPrec_procedimiento();
                        gl.servicioPrecio = espe.getMonto_culqui();
                        gl.especialidad_ta = espe.getId_procedimiento()+"";
                        //gl.paci_id_usr = pac.getPACI_ID();
                        //System.out.println(pac.getPACI_ID());
                        /*System.out.println(espe.getId_procedimiento());
                        System.out.println(espe.getDes_procedimiento());
                        System.out.println(espe.getPrec_procedimiento());
                        System.out.println(espe.getMonto_culqui());
                        System.out.println(espe.getText_dialog());*/
                    }
                }

            }
        });
        AlertDialog dialog2 = dialog.create();
        dialog2.show();
        dialog2.getWindow().setBackgroundDrawableResource(R.color.BLANCO);

    }
    void dialogPrecios(final String[] valJDialog){
        ListAdapter adapter = new AdapterFormaPago(this, valJDialog, icons);
        AlertDialog.Builder alertdialogbuilder = new AlertDialog.Builder(confirmacion_servicio.this, R.style.YourDialogStyle);
        alertdialogbuilder.setTitle("Seleccione el método de Pago ");
        alertdialogbuilder.setAdapter(adapter, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String selectedText = Arrays.asList(valJDialog).get(which);

                /*if(selectedText.equals("Transferencia Bancaria")){
                    formapago.setText("Transferencia Bancaria");
                    formapago.setCompoundDrawablesWithIntrinsicBounds(
                            R.drawable.tarjeta2, 0, R.drawable.downico2, 0);
                    selecFormaPago=true;
                    activoButtonConfirmacion(selecPaciente,selecFormaPago);
                }*/
                if(selectedText.equals("Tarjeta (Online)")){
                    formapago.setText("Tarjeta (Online)");
                    formapago.setCompoundDrawablesWithIntrinsicBounds(
                            R.drawable.tarjeta2, 0, R.drawable.downico2, 0);
                    selecFormaPago=true;
                    activoButtonConfirmacion(gl.servicioID);
                    tipoPago=1;
                    //System.out.println("SOY TARJETA ONLINE");
                }

                /*
                if(selectedText.equals("Tarjeta (POS)")){
                    formapago.setText("Tarjeta (POS)");
                    formapago.setCompoundDrawablesWithIntrinsicBounds(
                            R.drawable.pos2, 0, R.drawable.downico2, 0);
                }*/

                if(selectedText.equals("Efectivo")){
                    formapago.setText("Efectivo");
                    formapago.setCompoundDrawablesWithIntrinsicBounds(
                            R.drawable.efectivo2, 0, R.drawable.downico2, 0);
                    selecFormaPago=true;
                    activoButtonConfirmacion(gl.servicioID);
                    tipoPago=2;
                    //System.out.println("SOY EFECTIVO");
                }
                if(selectedText.equals("Transferencia Bancaria")){
                    formapago.setText("Transferencia Bancaria");
                    formapago.setCompoundDrawablesWithIntrinsicBounds(
                            R.drawable.efectivo2, 0, R.drawable.downico2, 0);
                    selecFormaPago=true;
                    activoButtonConfirmacion(gl.servicioID);
                    tipoPago=3;
                    //System.out.println("SOY EFECTIVO");
                }
            }
        });
        AlertDialog dialog = alertdialogbuilder.create();
        dialog.show();
        dialog.getWindow().setBackgroundDrawableResource(R.color.BLANCO);
    }
    void llenarSpParentesco(){

        String [] array_parentesco = getResources().getStringArray(R.array.Parentesco);
        //String [] values = {"AC-CARRERAS TECNICAS","AD-PROGRAMA DE ADELANTO","DC-DIPLOMADOS CIBERTEC"};
        ArrayAdapter<PacientesBE> adapter = new ArrayAdapter<PacientesBE>(this, R.layout.spinner, listarPacientes);
        //adapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
        //sp_costo.setAdapter(adapter);



    }
    void llenarSPinner(){
        ArrayAdapter<String> SpinerAdapter;
        String[] arrayItems = {"Strawberry","Chocolate","Vanilla"};
        final int[] actualValues={10,20,30};

        SpinerAdapter = new ArrayAdapter<String>(this,
                R.layout.spinner, arrayItems);
        //sp_costo.setAdapter(SpinerAdapter);

        /*sp_costo.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> arg0, View arg1,
                                       int arg2, long arg3) {
                int thePrice=actualValues[ arg2];
                System.out.println(thePrice);

            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
                // TODO Auto-generated method stub

            }
        });*/
    }
    public void CargarPacientes(RequestParams params){
        AsyncHttpClient client = new AsyncHttpClient();
        client.get("http://"+gl.WebService+"/listaPacientes", params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {

                String responseStr = null;
                JSONArray jsonArray = null;

                try {
                    responseStr = new String(responseBody, "UTF-8");
                    jsonArray = new JSONArray(responseStr);

                    if(jsonArray.length()>0){

                        for(int i=0; i<jsonArray.length(); i++){
                            JSONObject jsonObject = jsonArray.getJSONObject(i);
                            PacientesBE objPaci = new PacientesBE();
                            objPaci.setPACI_ID(jsonObject.optInt("paci_ID"));
                            objPaci.setPACIENTE(jsonObject.optString("paciente"));
                            objPaci.setCOD_USR(jsonObject.optInt("cod_USR"));
                            objPaci.setPACI_PHONE(jsonObject.optString("paci_PHONE"));
                            listarPacientes.add(objPaci);
                        }

                    }else{

                    }


                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }



            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                Toast.makeText(getApplicationContext(), "Error al Cargar los Pacientes.", Toast.LENGTH_SHORT).show();
            }
        });

    }

    public void CargarEspecialidad(){
        AsyncHttpClient client = new AsyncHttpClient();
        client.post("http://"+gl.WebService+"/especialidadTA", new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {

                String responseStr = null;
                JSONArray jsonArray = null;

                try {
                    responseStr = new String(responseBody, "UTF-8");
                    jsonArray = new JSONArray(responseStr);

                    if(jsonArray.length()>0){

                        for(int i=0; i<jsonArray.length(); i++){
                            JSONObject jsonObject = jsonArray.getJSONObject(i);
                            EspecialidadTABE objEspeci = new EspecialidadTABE();
                            objEspeci.setId_procedimiento(jsonObject.optInt("id_procedimiento"));
                            objEspeci.setDes_procedimiento(jsonObject.optString("des_procedimiento"));
                            objEspeci.setPrec_procedimiento(jsonObject.optString("prec_procedimiento"));
                            objEspeci.setMonto_culqui(jsonObject.optInt("monto_culqui"));
                            objEspeci.setText_dialog(jsonObject.optString("text_dialog"));
                            listaEspecialidades.add(objEspeci);
                        }

                    }else{

                    }


                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }



            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                Toast.makeText(getApplicationContext(), "Ocurrió un error.", Toast.LENGTH_SHORT).show();
            }
        });

    }

    void activoButtonConfirmacion(int value){
        if(value==9){
            if(selecPaciente.equals(true)&&selecFormaPago.equals(true)&&selecEspecialidad.equals(true)){
                btnProcesar.setEnabled(true);
                btnProcesar.setBackgroundColor(getResources().getColor(material_blue_grey_950));
            }else{
                btnProcesar.setEnabled(false);
            }
        }else{
            if(selecPaciente.equals(true)&&selecFormaPago.equals(true)){
                btnProcesar.setEnabled(true);
                btnProcesar.setBackgroundColor(getResources().getColor(material_blue_grey_950));
            }
            else {
                btnProcesar.setEnabled(false);
            }
        }
    }
    void lanza(){
        AlertDialog.Builder builder = new AlertDialog.Builder(confirmacion_servicio.this, R.style.Pasarella);
        LayoutInflater inflater = getLayoutInflater();
        TextView title = new TextView(this);
        title.setText("Confirmación de Pago");
        title.setGravity(Gravity.CENTER);
        title.setTextColor(Color.BLACK);
        title.setPadding(10, 10, 10, 10);
        title.setTextSize(20);
        builder.setCustomTitle(title);

        View dialog_layout = inflater.inflate(R.layout.form_tarjeta,null);
        final EditText numtarjeta = (EditText) dialog_layout.findViewById(R.id.txtNumerotarjeta);
        numtarjeta.addTextChangedListener(new TextWatcher() {
            private static final char space = ' ';
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                int pos = 0;
                while (true) {
                    if (pos >= s.length()) break;
                    if (space == s.charAt(pos) && (((pos + 1) % 5) != 0 || pos + 1 == s.length())) {
                        s.delete(pos, pos + 1);
                    } else {
                        pos++;
                    }
                }

                // Insert char where needed.
                pos = 4;
                while (true) {
                    if (pos >= s.length()) break;
                    final char c = s.charAt(pos);
                    // Only if its a digit where there should be a space we insert a space
                    if ("0123456789".indexOf(c) >= 0) {
                        s.insert(pos, "" + space);
                    }
                    pos += 5;
                }

            }
        });
        final Spinner spmes  = (Spinner) dialog_layout.findViewById(R.id.sp_mess);
        final Spinner spAnho = (Spinner) dialog_layout.findViewById(R.id.spAnho);
        final EditText cvvtarjeta = (EditText) dialog_layout.findViewById(R.id.txtCvvtarjeta);
        final Button btnAceptar = (Button) dialog_layout.findViewById(R.id.btnAceptaPago);
        final TextView condiciones = (TextView) dialog_layout.findViewById(R.id.tw_condiciones);
        final ImageView visa = (ImageView) dialog_layout.findViewById(R.id.img_visa);
        final ImageView mc = (ImageView) dialog_layout.findViewById(R.id.img_mc);
        final ImageView diners = (ImageView) dialog_layout.findViewById(R.id.img_diners);
        final ImageView amex = (ImageView) dialog_layout.findViewById(R.id.img_amex);
        if(gl.servicioID!=9){
            condiciones.setVisibility(View.VISIBLE);
        }else{
            condiciones.setVisibility(View.GONE);
        }

        btnAceptar.setText("Pagar " + gl.servicioDesc);

        /*Lleno Spinner Mes*/
        String [] array_mes = getResources().getStringArray(R.array.mesPsarella);
        spmes.setPopupBackgroundResource(R.color.BLANCO);
        //spmes.getBackground().setColorFilter(getResources().getColor(R.color.AMARILLO), PorterDuff.Mode.SRC_ATOP);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, array_mes);
        adapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
        spmes.setAdapter(adapter);

        /*Lleno Spinner Anho*/
        String [] array_anho = getResources().getStringArray(R.array.anhoPasarella);
        spAnho.setPopupBackgroundResource(R.color.BLANCO);
        ArrayAdapter<String> adapter2 = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, array_anho);
        adapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
        spAnho.setAdapter(adapter2);


        builder.setView(dialog_layout);
        final AlertDialog dialog = builder.create();

        btnAceptar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //prgBarra.show();
                //Oculto el alert
                dialog.dismiss();
                //Muestro el progrese Dialog - Library
                pDialog = new SweetAlertDialog(confirmacion_servicio.this, SweetAlertDialog.PROGRESS_TYPE)
                        .setTitleText("Cargando");
                pDialog.show();
                pDialog.setCancelable(false);
                System.out.println(" CONFIRMACION EXITOSA!");
                String cardAll = numtarjeta.getText().toString().trim();
                String numeroTarjeta = cardAll.replaceAll(" ","");
                String cvvTarjeta = cvvtarjeta.getText().toString().trim();
                int mesTarjeta = Integer.parseInt(spmes.getSelectedItem().toString().trim());
                int anhoTarjeta = Integer.parseInt(spAnho.getSelectedItem().toString().trim());

                /*System.out.println(numeroTarjeta);
                System.out.println(cvvTarjeta);
                System.out.println(mesTarjeta);
                System.out.println(anhoTarjeta);*/
                //Card card = new Card(numtarjeta.getText().toString().trim(),cvvtarjeta.getText().toString().trim(), Integer.parseInt(spmes.getSelectedItem().toString().trim()), Integer.parseInt(spAnho.getSelectedItem().toString().trim()),"soporte@itms.com.pe");
                Card card = new Card(numeroTarjeta,cvvTarjeta,mesTarjeta,anhoTarjeta,lg.email_usr);
                Token token = new Token(pk_public);
                token.createToken(getApplicationContext(), card, new TokenCallback() {
                    @Override
                    public void onSuccess(JSONObject token) throws JSONException {
                        String token_generated = token.get("id").toString();
                        System.out.println(token_generated);
                        /*Llamo al servio para el cargo*/
                        RequestParams params = new RequestParams();
                        params.put("charge_amount",gl.servicioPrecio);
                        params.put("mail", lg.email_usr);
                        params.put("token_id", token_generated);
                        /*Parametros para la cita*/
                        params.put("codigo",lg.cod_USR);
                        params.put("paci",gl.paci_id_usr);
                        params.put("tipproc",gl.servicioID);
                        params.put("tippag","1");
                        params.put("montoproc",gl.servicioDesc);
                        params.put("marcapaso",gl.marca_paso_usr);
                        params.put("distrito",gl.distrito_usr);
                        params.put("direccion",gl.direccion_usr);
                        params.put("latitud",gl.cord_latitud_usr);
                        params.put("longitud",gl.cord_longitud_usr);
                        params.put("estpago","1");
                        params.put("espta",gl.especialidad_ta);
                        params.put("estado_cita", estadoCita());
                        charge_service(params);
                        /*-----*/
                        //prgBarra.dismiss();
                        //dialog.dismiss();
                        //finish();
                    }

                    @Override
                    public void onError(Exception error) {
                        //System.out.println("Error PAPU");
                        //prgBarra.dismiss();
                        //dialog.dismiss();

                        pDialog.setTitleText("Ocurrio un Error")
                                .setConfirmText("Aceptar")
                                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                    @Override
                                    public void onClick(SweetAlertDialog sweetAlertDialog) {
                                        try {
                                            pDialog.dismiss();
                                            finish();
                                        }
                                        catch (Exception e){
                                            e.printStackTrace();
                                        }
                                    }
                                })
                                .changeAlertType(SweetAlertDialog.ERROR_TYPE);
                    }
                });

            }
        });




        dialog.getWindow().setBackgroundDrawableResource(R.color.BLANCO);
        dialog.show();
    }

    void lanzaOLDDD(){
        AlertDialog.Builder builder = new AlertDialog.Builder(confirmacion_servicio.this);
        LayoutInflater inflater = getLayoutInflater();

        View dialog_layout = inflater.inflate(R.layout.form_tarjeta,null);
        final EditText txtun = (EditText) dialog_layout.findViewById(R.id.txtNumerotarjeta);
        txtun.addTextChangedListener(new TextWatcher() {
            private static final char space = ' ';
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                int pos = 0;
                while (true) {
                    if (pos >= s.length()) break;
                    if (space == s.charAt(pos) && (((pos + 1) % 5) != 0 || pos + 1 == s.length())) {
                        s.delete(pos, pos + 1);
                    } else {
                        pos++;
                    }
                }

                // Insert char where needed.
                pos = 4;
                while (true) {
                    if (pos >= s.length()) break;
                    final char c = s.charAt(pos);
                    // Only if its a digit where there should be a space we insert a space
                    if ("0123456789".indexOf(c) >= 0) {
                        s.insert(pos, "" + space);
                    }
                    pos += 5;
                }

            }
        });
        /*final EditText txtps = (EditText) dialog_layout.findViewById(R.id.txtMestarjeta);
        final EditText txtpg = (EditText) dialog_layout.findViewById(R.id.txtAnhotarjeta);*/
        final EditText txtpl = (EditText) dialog_layout.findViewById(R.id.txtCvvtarjeta);
        builder.setView(dialog_layout)

                .setPositiveButton("LogIn", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        /*

                        String uname =txtun.getText().toString();
                        String pass = txtps.getText().toString();
                        String txtpg = txtps.getText().toString();
                        String txtpl = txtps.getText().toString();

                        if (uname.equals("admin") && pass.equals("1234")) {

                            Toast.makeText(confirmacion_servicio.this, "Login Success", Toast.LENGTH_SHORT).show();
                            dialogInterface.dismiss();
                        }else {
                            Toast.makeText(confirmacion_servicio.this, "Login Failed", Toast.LENGTH_SHORT).show();

                        }

                        */
                    }
                })
                .setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                });

        AlertDialog dialog = builder.create();
        dialog.show();
        dialog.getWindow().setBackgroundDrawableResource(R.color.wallet_holo_blue_light);
    }

    void diaalogLibrary(){
        final SweetAlertDialog pDialog = new SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE)
                .setTitleText("Cargando");
        pDialog.show();
    }

    void validarMarcapaso(){
        if(switchMarcapaso.isChecked()==true){
            gl.marca_paso_usr=1;
        }else if(switchMarcapaso.isChecked()==false){
            gl.marca_paso_usr=0;
        }else{
            gl.marca_paso_usr=0;
        }
    }


    /*Métodos REST*/
    public void obtnerKeyCulqui(RequestParams params){
        prgBarra.show();
        AsyncHttpClient client = new AsyncHttpClient();
        client.addHeader("Autorizacion","ITMSperu");
        client.post("http://" + gl.WebService + "/obtenerKEY", params, new AsyncHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                String responseStr = null;
                JSONObject jsonobject = null;

                try{
                    responseStr = new String(responseBody, "UTF-8");
                    jsonobject = new JSONObject(responseStr);
                    String valida = jsonobject.getString("pk_public");
                    if(valida!="null"){
                        pk_public = jsonobject.optString("pk_public");
                        pk_private = jsonobject.optString("pk_private");
                        prgBarra.dismiss();
                        lanza();
                    }else{
                        Toast.makeText(getApplicationContext(), "Ocurrio un error.", Toast.LENGTH_SHORT).show();
                        prgBarra.dismiss();
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }
                //Log.d("Mensaje  : ", " Exito gg");
                //System.out.println(statusCode);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {

                    Log.d("Mensaje  : ", " Error gg");
                    System.out.println(statusCode);
                    prgBarra.dismiss();
                    Toast.makeText(getApplicationContext(), "Ocurrio un error.", Toast.LENGTH_SHORT).show();

            }
        });
    }

    public void charge_service(RequestParams params){
        final int DEFAULT_TIMEOUT = 30 * 1000;
        AsyncHttpClient client = new AsyncHttpClient();
        client.setConnectTimeout(DEFAULT_TIMEOUT);
        client.setResponseTimeout(DEFAULT_TIMEOUT);
        client.post("http://" + gl.WebService + "/venta_app", params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                //System.out.println("Status Code :   "+statusCode);
                gl.especialidad_ta=null;
                String responseStr = null;
                JSONObject jsonobject = null;
                //String cod_pago_culqui = null;

                try {
                    if(statusCode==201){
                        responseStr = new String(responseBody, "UTF-8");
                        jsonobject = new JSONObject(responseStr);
                        //cod_pago_culqui = jsonobject.optString("id");

                        RequestParams params1 = new RequestParams();
                        params1.put("accion","Venta Exitosa");
                        params1.put("cod_usr",lg.cod_USR);
                        params1.put("cod_pago",jsonobject.optString("id"));
                        gl.registroLogs(params1);

                        RequestParams mail = new RequestParams();
                        mail.put("paciente", jsonobject.optString("paciente"));
                        mail.put("service",  jsonobject.optString("procedimiento"));
                        mail.put("mont_proc",  jsonobject.optString("monto_proc"));
                        mail.put("distrito", jsonobject.optString("distrito"));
                        mail.put("direccion", jsonobject.optString("direccion"));
                        mail.put("fecha",    jsonobject.optString("fecha"));
                        mail.put("tippago",  jsonobject.optString("tipopago"));
                        mail.put("estpago",  jsonobject.optString("estadopago"));
                        mail.put("cita_id",  jsonobject.optString("cita_id"));
                        mail.put("mail_paci", gl.cel_paci_cita);
                        gl.sendMailITMS(mail);

                        RequestParams params2 = new RequestParams();
                        params2.put("mailcliente", lg.email_usr);
                        params2.put("paciente", jsonobject.optString("paciente"));
                        params2.put("service",  jsonobject.optString("procedimiento"));
                        params2.put("mont_proc",  jsonobject.optString("monto_proc"));
                        params2.put("direccion", jsonobject.optString("direccion"));
                        params2.put("fecha",    jsonobject.optString("fecha"));
                        params2.put("tippago",  jsonobject.optString("tipopago"));
                        params2.put("estpago",  jsonobject.optString("estadopago"));
                        gl.senMailCliente(params2);
                        pDialog.setTitleText("¡Se realizó el pago con éxito!")
                                .setConfirmText("Aceptar")
                                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                    @Override
                                    public void onClick(SweetAlertDialog sweetAlertDialog) {
                                        try {
                                            pDialog.dismiss();
                                            if(gl.servicioID==9){
                                                alertaRedireccionarUsuario();
                                            }else{
                                                finish();
                                            }
                                        }catch (Exception e){
                                            e.printStackTrace();
                                        }
                                    }
                                })
                                .changeAlertType(SweetAlertDialog.SUCCESS_TYPE);
                    }else{
                        System.out.println("Status Code :   "+statusCode);
                        pDialog.setTitleText("¡Ocurrió un error 01")
                                .setConfirmText("Aceptar")
                                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                    @Override
                                    public void onClick(SweetAlertDialog sweetAlertDialog) {
                                        try {
                                            pDialog.dismiss();
                                            finish();
                                        }catch (Exception e){
                                            e.printStackTrace();
                                        }
                                    }
                                })
                                .changeAlertType(SweetAlertDialog.ERROR_TYPE);
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                //System.out.println("Status Code :   "+statusCode);
                gl.especialidad_ta=null;
                pDialog.setTitleText("¡ERROR!")
                        .setConfirmText("Aceptar")
                        .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                            @Override
                            public void onClick(SweetAlertDialog sweetAlertDialog) {
                                try {
                                    pDialog.dismiss();
                                    finish();
                                }catch (Exception e){
                                    e.printStackTrace();
                                }
                            }
                        })
                        .changeAlertType(SweetAlertDialog.ERROR_TYPE);
            }
        });
    }

    public void registroCita(RequestParams params){
        pDialog = new SweetAlertDialog(confirmacion_servicio.this, SweetAlertDialog.PROGRESS_TYPE)
                .setTitleText("Cargando");
        pDialog.show();
        pDialog.setCancelable(false);
        AsyncHttpClient client = new AsyncHttpClient();
        client.post("http://" + gl.WebService + "/insertCitaDB", params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                //System.out.println("Status Code :   "+statusCode);
                String responseStr = null;
                JSONObject jsonObject = null;

                gl.especialidad_ta=null;
                try {
                    if(statusCode==201){
                        responseStr = new String(responseBody, "UTF-8");
                        jsonObject = new JSONObject(responseStr);
                        RequestParams params1 = new RequestParams();
                        params1.put("paciente", jsonObject.optString("paciente"));
                        params1.put("service",  jsonObject.optString("procedimiento"));
                        params1.put("mont_proc",  jsonObject.optString("monto_proc"));
                        params1.put("distrito", jsonObject.optString("distrito"));
                        params1.put("direccion",jsonObject.optString("direccion"));
                        params1.put("fecha",    jsonObject.optString("fecha"));
                        params1.put("tippago",  jsonObject.optString("tipopago"));
                        params1.put("estpago",  jsonObject.optString("estadopago"));
                        params1.put("cita_id",  jsonObject.optInt("cita_id"));
                        params1.put("mail_paci", gl.cel_paci_cita);
                        gl.sendMailITMS(params1);

                        RequestParams params2 = new RequestParams();
                        params2.put("mailcliente", lg.email_usr);
                        params2.put("paciente", jsonObject.optString("paciente"));
                        params2.put("service",  jsonObject.optString("procedimiento"));
                        params2.put("mont_proc",  jsonObject.optString("monto_proc"));
                        params2.put("direccion", jsonObject.optString("direccion"));
                        params2.put("fecha",    jsonObject.optString("fecha"));
                        params2.put("tippago",  jsonObject.optString("tipopago"));
                        params2.put("estpago",  jsonObject.optString("estadopago"));
                        gl.senMailCliente(params2);

                        /**
                         * Inserto el RQ_Domicilan
                         */
                        callRQ_Domicilab(jsonObject.optInt("cod_proc"),jsonObject.optInt("cita_id"));

                        pDialog.setTitleText("¡Se registró la cita con éxito!")
                                .setConfirmText("Aceptar")
                                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                    @Override
                                    public void onClick(SweetAlertDialog sweetAlertDialog) {
                                        try{
                                            gl.especialidad_ta=null;
                                            pDialog.dismiss();
                                            //finish();
                                            alertaEfectivoUsuario();
                                        }catch (Exception e){
                                            e.printStackTrace();
                                        }
                                    }
                                })
                                .changeAlertType(SweetAlertDialog.SUCCESS_TYPE);
                    }else{
                        pDialog.setTitleText("¡Ocurrió un error al efectuar el pago!")
                                .setConfirmText("Aceptar")
                                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                    @Override
                                    public void onClick(SweetAlertDialog sweetAlertDialog) {
                                        try {
                                            gl.especialidad_ta=null;
                                            pDialog.dismiss();
                                            finish();
                                        }catch (Exception e){
                                            e.printStackTrace();
                                        }
                                    }
                                })
                                .changeAlertType(SweetAlertDialog.ERROR_TYPE);
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                System.out.println("Code: "+statusCode);
                gl.especialidad_ta=null;
                pDialog.setTitleText("¡Ocurrió un error!")
                        .setContentText("Al registrar la cita \n vuelva a intentar")
                        .setConfirmText("Aceptar")
                        .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                            @Override
                            public void onClick(SweetAlertDialog sweetAlertDialog) {
                                try {
                                    pDialog.dismiss();
                                    finish();
                                }catch (Exception e){
                                    e.printStackTrace();
                                }
                            }
                        })
                        .changeAlertType(SweetAlertDialog.ERROR_TYPE);
            }
        });
    }

    void MethodregistroCita(){
        //System.out.println("aaa");
        //lanza();
        validarMarcapaso();
        if(tipoPago==1){
                    /*Tipo de pago con Tarjeta de Credito*/
            RequestParams params = new RequestParams();
            params.put("key", "10");
            obtnerKeyCulqui(params);
        }else if(tipoPago==2){
                    /*Tipo de pago con Efectivo*/
            RequestParams params = new RequestParams();
            params.put("codigo",lg.cod_USR);
            params.put("paci",gl.paci_id_usr);
            params.put("tipproc",gl.servicioID);
            params.put("tippag","2");
            params.put("montoproc",gl.servicioDesc);
            params.put("marcapaso",gl.marca_paso_usr);
            params.put("distrito",gl.distrito_usr);
            params.put("direccion",gl.direccion_usr);
            params.put("latitud",gl.cord_latitud_usr);
            params.put("longitud",gl.cord_longitud_usr);
            params.put("estpago","0");
            params.put("espta",gl.especialidad_ta);
            params.put("estado_cita", estadoCita());
            registroCita(params);/*
            System.err.println("codigo"+lg.cod_USR);
            System.err.println("paci"+gl.paci_id_usr);
            System.err.println("tipproc"+gl.servicioID);
            System.err.println("tippag"+"2");
            System.err.println("montoproc"+gl.servicioDesc);
            System.err.println("marcapaso"+gl.marca_paso_usr);
            System.err.println("distrito"+gl.distrito_usr);
            System.err.println("direccion"+gl.direccion_usr);
            System.err.println("latitud"+gl.cord_latitud_usr);
            System.err.println("longitud"+gl.cord_longitud_usr);
            System.err.println("estpago"+"0");
            System.err.println("espta"+gl.especialidad_ta);
            System.err.println("estado_cita"+ estadoCita());*/
        }else  if(tipoPago==3){
                    /*Tipo de pago con Transferencia Bancaria*/
            RequestParams params = new RequestParams();
            params.put("codigo",lg.cod_USR);
            params.put("paci",gl.paci_id_usr);
            params.put("tipproc",gl.servicioID);
            params.put("tippag","3");
            params.put("montoproc",gl.servicioDesc);
            params.put("marcapaso",gl.marca_paso_usr);
            params.put("distrito",gl.distrito_usr);
            params.put("direccion",gl.direccion_usr);
            params.put("latitud",gl.cord_latitud_usr);
            params.put("longitud",gl.cord_longitud_usr);
            params.put("estpago","0");
            params.put("espta",gl.especialidad_ta);
            params.put("estado_cita", estadoCita());
            registroCita(params);
        }else{
            System.out.println("Ninguna acccion!");
        }
    }

    /*Método Confirma Estado si es TA o Procedimiento*/
    int estadoCita(){
        if(gl.servicioID==9){
            return 1;
        }else{
            return 0;
        }
    }

    /*private Activity getActivity() {
        Context context = getContext();
        while (context instanceof ContextWrapper) {
            if (context instanceof Activity) {
                return (Activity)context;
            }
            context = ((ContextWrapper)context).getBaseContext();
        }
        return null;
    }*/

    void alertaRedireccionarUsuario(){
        pDialogOK = new SweetAlertDialog(confirmacion_servicio.this, SweetAlertDialog.PROGRESS_TYPE)
                .setTitleText("Cargando");
        pDialogOK.show();
        pDialogOK.setCancelable(false);
        pDialogOK.setTitleText("¡Se registro la Cita con Exito!")
                .setContentText("Por favor seleccione \n en el Menu Principal \n la opción Mis Atenciones.")
                .setConfirmText("Aceptar")
                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sweetAlertDialog) {
                        try{
                            pDialogOK.dismiss();
                            finish();
                        }catch (Exception e){
                            e.printStackTrace();
                        }
                    }
                })
                .changeAlertType(SweetAlertDialog.SUCCESS_TYPE);
    }

    void alertaEfectivoUsuario(){
        pDialogOK2 = new SweetAlertDialog(confirmacion_servicio.this, SweetAlertDialog.PROGRESS_TYPE)
                .setTitleText("Cargando");
        pDialogOK2.show();
        pDialogOK2.setCancelable(false);
        pDialogOK2.setTitleText("¡En breve nos contactaremos!")
                .setContentText("Para coordinar el servicio solicitado.")
                .setConfirmText("Aceptar")
                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sweetAlertDialog) {
                        try{
                            pDialogOK2.dismiss();
                            finish();
                        }catch (Exception e){
                            e.printStackTrace();
                        }
                    }
                })
                .changeAlertType(SweetAlertDialog.SUCCESS_TYPE);
    }


    void opacarTipoTarjeta(ImageView img1, ImageView img2, ImageView img3, ImageView img4){
        img1.setImageAlpha(125);
        img2.setImageAlpha(125);
        img3.setImageAlpha(125);
        img4.setImageAlpha(125);
    }

    String proc_ta_domicilab(){
        String retorno=null;
        if(gl.servicioID==15){
            retorno = gl.dom_procedimientos;
        }else{
            retorno = gl.val_especialidad_ta+"";
        }
        return retorno;

    }

    void callRQ_Domicilab(int cod_pro, int cita_id){
        if(cod_pro==15){
            RequestParams params = new RequestParams();
            params.put("cita_id", cita_id);
            params.put("procds", gl.dom_procedimientos);
            params.put("paci_id", gl.paci_id_usr);
            insertRQ_Domicilab(params);
        }else{
            System.out.println("NADA!");
        }
    }

    void insertRQ_Domicilab(RequestParams params){
        AsyncHttpClient client = new AsyncHttpClient();
        client.post("http://" + gl.WebService + "/integracion/insertRQDomicilab", params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                String responseStr = null;
                JSONObject jsonObject = null;

                if(statusCode==201){
                    try{
                        /*responseStr = new String(responseBody, "UTF-8");
                        jsonObject = new JSONObject(responseStr);
                        System.err.println(jsonObject.optString("cod_ord_ser"));
                        System.err.println(jsonObject.optString("matriz_send"));*/
                        //Lllamo al metodo para consumir el ws de domicilab
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                    //System.out.println(responseBody);
                }else{
                    System.err.println("E, insert RQ_Domicilab");
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                System.err.println("E, insert RQ_Domicilab");
            }
        });
    }





}
