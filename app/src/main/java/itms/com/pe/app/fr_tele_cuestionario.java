package itms.com.pe.app;


import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import cn.pedant.SweetAlert.SweetAlertDialog;
import cz.msebera.android.httpclient.Header;


/**
 * A simple {@link Fragment} subclass.
 */
public class fr_tele_cuestionario extends Fragment {

    View v;

    TextView lblGenerales,lblRepiratorios,lblDigestivos,lblMuscoesqueleticos,lblUrinarios,lblPiel,lblPresionArterial,lblOtros;
    LinearLayout layoutGenerales,layoutRespiratorios,layoutDigestivos,layoutMusculoesqueleticos,layoutUrinarios,layoutPiel,layoutPresionArterial,layoutOtros;
    Boolean visible = false;
    CheckBox gen1,gen2,gen3,gen4,gen5,gen6,gen7,gen8;
    CheckBox resp1,resp2,resp3,resp4,resp5,resp6,resp7;
    CheckBox dig1,dig2,dig3,dig4,dig5,dig6,dig7;
    CheckBox musc1,musc2,musc3,musc4;
    CheckBox uri1,uri2,uri3;
    CheckBox piel1,piel2,piel3,piel4,piel5,piel6;
    CheckBox presion1,presion2;
    EditText editOtros, cuestDias,cuestHoras;
    Button btnAceptar;

    ProgressDialog prgBarra;

    ArrayList<CheckBox> boxes = new ArrayList<CheckBox>();
    Global gl = new Global();

    SweetAlertDialog pDialog = null;



    public fr_tele_cuestionario() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        v = inflater.inflate(R.layout.fragment_fr_tele_cuestionario, container, false);

        layoutGenerales = (LinearLayout)v.findViewById(R.id.layoutGenerales);
        layoutRespiratorios = (LinearLayout)v.findViewById(R.id.layoutRespiratorios);
        layoutDigestivos = (LinearLayout)v.findViewById(R.id.layoutDigestivos);
        layoutMusculoesqueleticos = (LinearLayout)v.findViewById(R.id.layoutMusculoesqueleticos);
        layoutUrinarios = (LinearLayout)v.findViewById(R.id.layoutUrinarios);
        layoutPiel = (LinearLayout)v.findViewById(R.id.layoutPiel);
        layoutPresionArterial = (LinearLayout)v.findViewById(R.id.layoutPresionArterial);
        layoutOtros = (LinearLayout)v.findViewById(R.id.layoutOtros);

        lblGenerales = (TextView)v.findViewById(R.id.txtGenerales);
        lblRepiratorios = (TextView)v.findViewById(R.id.txtRespiratorios);
        lblDigestivos = (TextView)v.findViewById(R.id.txtDisgestivos);
        lblMuscoesqueleticos = (TextView)v.findViewById(R.id.txtMuscoesqueleticos);
        lblUrinarios = (TextView)v.findViewById(R.id.txtUrinarios);
        lblPiel = (TextView)v.findViewById(R.id.txtPiel);
        lblPresionArterial = (TextView)v.findViewById(R.id.txtPresionArterial);
        lblOtros = (TextView)v.findViewById(R.id.txtOtros);
        cuestDias = (EditText)v.findViewById(R.id.txtCuestDias);
        cuestHoras = (EditText)v.findViewById(R.id.txtCuestHoras);

        /*Intanciar los checkbox*/
        gen1 = (CheckBox)v.findViewById(R.id.gen1);
        gen2 = (CheckBox)v.findViewById(R.id.gen2);
        gen3 = (CheckBox)v.findViewById(R.id.gen3);
        gen4 = (CheckBox)v.findViewById(R.id.gen4);
        gen5 = (CheckBox)v.findViewById(R.id.gen5);
        gen6 = (CheckBox)v.findViewById(R.id.gen6);
        gen7 = (CheckBox)v.findViewById(R.id.gen7);
        gen8 = (CheckBox)v.findViewById(R.id.gen8);
        resp1 = (CheckBox)v.findViewById(R.id.resp1);
        resp2 = (CheckBox)v.findViewById(R.id.resp2);
        resp3 = (CheckBox)v.findViewById(R.id.resp3);
        resp4 = (CheckBox)v.findViewById(R.id.resp4);
        resp5 = (CheckBox)v.findViewById(R.id.resp5);
        resp6 = (CheckBox)v.findViewById(R.id.resp6);
        resp7 = (CheckBox)v.findViewById(R.id.resp7);
        dig1 = (CheckBox)v.findViewById(R.id.dig1);
        dig2 = (CheckBox)v.findViewById(R.id.dig2);
        dig3 = (CheckBox)v.findViewById(R.id.dig3);
        dig4 = (CheckBox)v.findViewById(R.id.dig4);
        dig5 = (CheckBox)v.findViewById(R.id.dig5);
        dig6 = (CheckBox)v.findViewById(R.id.dig6);
        dig7 = (CheckBox)v.findViewById(R.id.dig7);
        musc1 = (CheckBox)v.findViewById(R.id.musc1);
        musc2 = (CheckBox)v.findViewById(R.id.musc2);
        musc3 = (CheckBox)v.findViewById(R.id.musc3);
        musc4 = (CheckBox)v.findViewById(R.id.musc4);
        uri1 = (CheckBox)v.findViewById(R.id.uri1);
        uri2 = (CheckBox)v.findViewById(R.id.uri2);
        uri3 = (CheckBox)v.findViewById(R.id.uri3);
        piel1 = (CheckBox)v.findViewById(R.id.piel1);
        piel2 = (CheckBox)v.findViewById(R.id.piel2);
        piel3 = (CheckBox)v.findViewById(R.id.piel3);
        piel4 = (CheckBox)v.findViewById(R.id.piel4);
        piel5 = (CheckBox)v.findViewById(R.id.piel5);
        piel6 = (CheckBox)v.findViewById(R.id.piel6);
        presion1 = (CheckBox)v.findViewById(R.id.presion1);
        presion2 = (CheckBox)v.findViewById(R.id.presion2);
        editOtros = (EditText)v.findViewById(R.id.editOtros);

        btnAceptar = (Button)v.findViewById(R.id.btnAceptar);
        prgBarra = new ProgressDialog(getContext(), AlertDialog.THEME_HOLO_LIGHT);
        prgBarra.setTitle("Cargado");
        prgBarra.setMessage("Cargando...");












        /*Metodos*/
        lblGenerales.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*
                if(layoutGenerales.getVisibility() == View.GONE){
                    layoutGenerales.setVisibility(View.VISIBLE);
                }else{
                    layoutGenerales.setVisibility(View.GONE);
                }*/
                MostrarCuestionarioOpcion(layoutGenerales);

            }
        });
        lblRepiratorios.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MostrarCuestionarioOpcion(layoutRespiratorios);
            }
        });

        lblDigestivos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MostrarCuestionarioOpcion(layoutDigestivos);
            }
        });
        lblMuscoesqueleticos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MostrarCuestionarioOpcion(layoutMusculoesqueleticos);
            }
        });
        lblUrinarios.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MostrarCuestionarioOpcion(layoutUrinarios);
            }
        });
        lblPiel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MostrarCuestionarioOpcion(layoutPiel);
            }
        });
        lblPresionArterial.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MostrarCuestionarioOpcion(layoutPresionArterial);
            }
        });
        lblOtros.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MostrarCuestionarioOpcion(layoutOtros);
            }
        });

        btnAceptar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*
                System.out.println("Gnerales :" + valueSistomasGenerales());
                System.out.println("Respiratorios :" + valuesSintomasRespiratorios());
                System.out.println("Digestivos :" + valuesSintomasDigestivos());
                System.out.println("Muscoesqueleticos :" + valuesSintomasMuscoesqueleticos());
                System.out.println("Urinarios :" + valuesSintomasUrinarios());
                System.out.println("Piel :" + valuesSintomasPiel());
                System.out.println("Presion :" + valuesSintomasPresionArterial());*/
                RequestParams params = new RequestParams();
                params.put("inf_tipo", gl.val_tipo_ta);
                params.put("inf_tipo_especialidad", gl.val_especialidad_ta);
                params.put("inf_motivo", gl.val_motivo_ta);
                params.put("inf_estado", gl.val_estado_ta);
                params.put("inf_edad", gl.val_edad_ta);
                params.put("pac_id", gl.val_paci_id_ta);
                params.put("inf_direccion_entrega", gl.val_direccion_ta);
                params.put("inf_distrito_entrega", gl.val_distrito_ta);
                params.put("cue_dias", getCuestionarioDias());
                params.put("cue_horas", getCuestionarioHoras());
                params.put("p1",valueSistomasGenerales());
                params.put("p2",valuesSintomasRespiratorios());
                params.put("p3",valuesSintomasDigestivos());
                params.put("p4",valuesSintomasMuscoesqueleticos());
                params.put("p5",valuesSintomasUrinarios());
                params.put("p6",valuesSintomasPiel());
                params.put("p7",valuesSintomasPresionArterial());
                params.put("p8",editOtros.getText().toString().trim());
                params.put("cita_id_para_ta", gl.val_cita_id_ta);
                insertarTeleconsultar(params);

                /*System.out.println(gl.val_tipo_ta);
                System.out.println(gl.val_especialidad_ta);
                System.out.println(gl.val_motivo_ta);
                System.out.println(gl.val_estado_ta);
                System.out.println(gl.val_paci_id_ta);
                System.out.println(gl.val_direccion_ta);
                System.out.println(gl.val_distrito_ta);
                System.out.println(cuestDias.getText().toString().trim());
                System.out.println(cuestHoras.getText().toString().trim());
                System.out.println(valueSistomasGenerales());
                System.out.println(valuesSintomasRespiratorios());
                System.out.println(valuesSintomasDigestivos());
                System.out.println(valuesSintomasMuscoesqueleticos());
                System.out.println(valuesSintomasUrinarios());
                System.out.println(valuesSintomasPiel());
                System.out.println(valuesSintomasPresionArterial());
                System.out.println(editOtros.getText().toString().trim());
                System.out.println(gl.val_cita_id_ta);

                System.out.println("dias  " + getCuestionarioDias());
                System.out.println("Horas  " + getCuestionarioHoras());*/
            }
        });

        return v;
    }

    //Metodos fuera

    void LimpiarCuestionario(){

        try {
            layoutGenerales.setVisibility(View.GONE);
            layoutRespiratorios.setVisibility(View.GONE);
            layoutDigestivos.setVisibility(View.GONE);
            layoutMusculoesqueleticos.setVisibility(View.GONE);
            layoutUrinarios.setVisibility(View.GONE);
            layoutPiel.setVisibility(View.GONE);
            layoutPresionArterial.setVisibility(View.GONE);
            layoutOtros.setVisibility(View.GONE);

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    void MostrarCuestionarioOpcion(LinearLayout vista){
        if(vista.getVisibility() == View.GONE ){
            vista.setVisibility(View.VISIBLE);
        }else{
            vista.setVisibility(View.GONE);
        }
    }

    String valueSistomasGenerales(){
        String a = "0";
        String b = "0";
        String c = "0";
        String d = "0";
        String e = "0";
        String f = "0";
        String g = "0";
        String h = "0";
        if(gen1.isChecked()==true) a="1"; else a="0";
        if(gen2.isChecked()==true) b="1"; else b="0";
        if(gen3.isChecked()==true) c="1"; else c="0";
        if(gen4.isChecked()==true) d="1"; else d="0";
        if(gen5.isChecked()==true) e="1"; else e="0";
        if(gen6.isChecked()==true) f="1"; else f="0";
        if(gen7.isChecked()==true) g="1"; else g="0";
        if(gen8.isChecked()==true) h="1"; else h="0";
        return a+b+c+d+e+f+g+h;
    }
    String valuesSintomasRespiratorios(){
        String a = "0";
        String b = "0";
        String c = "0";
        String d = "0";
        String e = "0";
        String f = "0";
        String g = "0";
        if(resp1.isChecked()==true) a="1"; else a="0";
        if(resp2.isChecked()==true) b="1"; else b="0";
        if(resp3.isChecked()==true) c="1"; else c="0";
        if(resp4.isChecked()==true) d="1"; else d="0";
        if(resp5.isChecked()==true) e="1"; else e="0";
        if(resp6.isChecked()==true) f="1"; else f="0";
        if(resp7.isChecked()==true) g="1"; else g="0";
        return a+b+c+d+e+f+g;
    }
    String valuesSintomasDigestivos(){
        String a = "0";
        String b = "0";
        String c = "0";
        String d = "0";
        String e = "0";
        String f = "0";
        String g = "0";
        if(dig1.isChecked()==true) a="1"; else a="0";
        if(dig2.isChecked()==true) b="1"; else b="0";
        if(dig3.isChecked()==true) c="1"; else c="0";
        if(dig4.isChecked()==true) d="1"; else d="0";
        if(dig5.isChecked()==true) e="1"; else e="0";
        if(dig6.isChecked()==true) f="1"; else f="0";
        if(dig7.isChecked()==true) g="1"; else g="0";
        return a+b+c+d+e+f+g;
    }
    String valuesSintomasMuscoesqueleticos(){
        String a = "0";
        String b = "0";
        String c = "0";
        String d = "0";
        if(musc1.isChecked()==true) a="1"; else a="0";
        if(musc2.isChecked()==true) b="1"; else b="0";
        if(musc3.isChecked()==true) c="1"; else c="0";
        if(musc4.isChecked()==true) d="1"; else d="0";
        return a+b+c+d;
    }
    String valuesSintomasUrinarios(){
        String a = "0";
        String b = "0";
        String c = "0";
        if(uri1.isChecked()==true) a="1"; else a="0";
        if(uri2.isChecked()==true) b="1"; else b="0";
        if(uri3.isChecked()==true) c="1"; else c="0";
        return a+b+c;
    }
    String valuesSintomasPiel(){
        String a = "0";
        String b = "0";
        String c = "0";
        String d = "0";
        String e = "0";
        String f = "0";
        if(piel1.isChecked()==true) a="1"; else a="0";
        if(piel2.isChecked()==true) b="1"; else b="0";
        if(piel3.isChecked()==true) c="1"; else c="0";
        if(piel4.isChecked()==true) d="1"; else d="0";
        if(piel5.isChecked()==true) e="1"; else e="0";
        if(piel6.isChecked()==true) f="1"; else f="0";
        return a+b+c+d+e+f;
    }
    String valuesSintomasPresionArterial(){
        String a = "0";
        String b = "0";
        if(presion1.isChecked()==true) a="1"; else a="0";
        if(presion2.isChecked()==true) b="1"; else b="0";
        return a+b;
    }

    /*Métodos REST*/

    void insertarTeleconsultar(final RequestParams params){
        prgBarra.show();
        AsyncHttpClient client = new AsyncHttpClient();
        client.post("http://" + gl.WebService + "/inserTeleconsulta", params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                String responseStr = null;
                JSONObject jsonobject = null;

                if(statusCode==201){
                    try {
                        responseStr = new String(responseBody, "UTF-8");
                        jsonobject = new JSONObject(responseStr);
                        //System.out.println(responseStr);
                        gl.val_info_id_ta = jsonobject.optInt("tele_at_id");
                        //prgBarra.dismiss();
                        //Inicio Fragment Cuestionario
                        RequestParams params1 = new RequestParams();
                        params1.put("cita_id_ta", gl.val_cita_id_ta);
                        updateCitaTeleconsultInsertada(params1);
                    }catch (Exception e){
                        e.printStackTrace();
                    }

                }else{
                    alertaErrorRegistro();
                    System.out.println("Todo MAL");
                    prgBarra.dismiss();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                alertaErrorRegistro();
                System.out.println("Todo MAL X2");
                prgBarra.dismiss();
            }
        });
    }

    void updateCitaTeleconsultInsertada(RequestParams params){
        AsyncHttpClient client = new AsyncHttpClient();
        client.post("http://" + gl.WebService + "/updateCitaTA", params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                System.out.println(statusCode);
                if(statusCode==200){
                    prgBarra.dismiss();
                    FragmentManager fragmentManager=getActivity().getSupportFragmentManager();
                    fragmentManager.beginTransaction().replace(R.id.contenedor, new fr_esperando_ta()).commit();
                }else{
                    //alertaErrorRegistro();

                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                //System.out.println(statusCode);
                //System.out.println("Ocurrio un error2");
                //alertaErrorRegistro();
            }
        });
    }

    /*------------------------------------------
    public void listarExamenes(RequestParams params){
        //progressDialog.show();
        AsyncHttpClient client = new AsyncHttpClient();
        client.post("http://"+gl.WebService+"/atencionesPac", params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                String responseStr = null;
                JSONArray jsonArray = null;

                try {
                    responseStr = new String(responseBody, "UTF-8");
                    jsonArray = new JSONArray(responseStr);
                    System.out.println(jsonArray);

                    if(jsonArray.length()>0){
                        if(getActivity()!=null){
                            //progressDialog.dismiss();
                            ArrayList<AtencionesBE> array_atenciones = new ArrayList<>();
                            AdapterAtenciones adapterAtenciones = new AdapterAtenciones(getActivity(), array_atenciones);
                            ListView listView = (ListView) v.findViewById(R.id.lista_atenciones);
                            for(int i=0; i<jsonArray.length(); i++){
                                JSONObject jsonObject = jsonArray.getJSONObject(i);
                                AtencionesBE ate = new AtencionesBE();
                                ate.setPaciente(jsonObject.optString("paciente"));
                                ate.setFecha(jsonObject.optString("fecha"));
                                ate.setProcedimiento(jsonObject.optString("procedimiento"));
                                ate.setEstadoExamen(jsonObject.optString("estadoExamen"));
                                ate.setValEstadoExamen(jsonObject.optInt("valEstadoExamen"));
                                ate.setId_especialidad_ta(jsonObject.optInt("id_especialidad_ta"));
                                ate.setPaci_id(jsonObject.optInt("paci_id"));
                                ate.setDireccion(jsonObject.optString("direccion"));
                                ate.setDistrito(jsonObject.optString("distrito"));
                                ate.setFecha_nacimiento(jsonObject.optString("fecha_nacimiento"));
                                ate.setPaci_edad(jsonObject.optInt("paci_edad"));
                                ate.setCita_id(jsonObject.optInt("cita_id"));
                                ate.setInfo_id_ta(jsonObject.optInt("info_id_ta"));
                                adapterAtenciones.add(ate);
                            }
                            listView.setAdapter(adapterAtenciones);
                        }
                    }else{
                        //progressDialog.dismiss();
                    }

                }catch (Exception e){
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                //progressDialog.dismiss();
                Toast.makeText(getContext(), "Ocurrio un error al cargar los Pacientes", Toast.LENGTH_SHORT).show();
            }
        });
    }
    */

    void alertaErrorRegistro(){
        pDialog = new SweetAlertDialog(getActivity(), SweetAlertDialog.PROGRESS_TYPE)
                .setTitleText("Cargando");
        pDialog.show();
        pDialog.setCancelable(false);
        pDialog.setTitleText("Se presentó un error")
                .setContentText("Vuelve a intentar")
                .setConfirmText("Aceptar")
                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sweetAlertDialog) {
                        try {
                            pDialog.dismiss();
                        }
                        catch (Exception e){
                            e.printStackTrace();
                        }
                    }
                })
                .changeAlertType(SweetAlertDialog.WARNING_TYPE);
    }

    int getCuestionarioDias(){
        try {
            if(cuestDias.getText().toString().trim().length()<1){
                return 0;
            }
            else
                return Integer.parseInt(cuestDias.getText().toString().trim());
        }catch (Exception e){
            e.printStackTrace();
        }
        return 0;
    }
    int getCuestionarioHoras(){
        try {
            if(cuestHoras.getText().toString().trim().length()<1){
                return 0;
            }else
                return Integer.parseInt(cuestHoras.getText().toString().trim());
        }catch (Exception e){
            e.printStackTrace();
        }
        return 0;
    }

}

