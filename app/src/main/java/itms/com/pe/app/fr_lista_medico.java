package itms.com.pe.app;


import android.*;
import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
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
import java.util.Calendar;

import cn.pedant.SweetAlert.SweetAlertDialog;
import cz.msebera.android.httpclient.Header;
import itms.com.pe.app.Funciones.PDFTools;


/**
 * A simple {@link Fragment} subclass.
 */
public class fr_lista_medico extends Fragment {

    View v;
    ProgressDialog progressDialog;
    Global gl = new Global();
    login lg = new login();
    SweetAlertDialog dialogPermiso = null;
    SweetAlertDialog dialogDescarga = null;


    public fr_lista_medico() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        getActivity().setTitle("Mis Pacientes");
        v = inflater.inflate(R.layout.fragment_fr_lista_medico, container, false);
        progressDialog = new ProgressDialog(getContext());
        progressDialog.setMessage("Cargando...");
        progressDialog.setCancelable(false);
        onDestroy();
        RequestParams params = new RequestParams();
        params.put("clie_id",gl.clie_id_oracle);
        listarExamenes(params);
        return v;
    }


    /*----------METODO REST--------------*/

    public void listarExamenes(RequestParams params){
        final int DEFAULT_TIMEOUT = 30 * 1000;
        progressDialog.show();
        AsyncHttpClient client = new AsyncHttpClient();
        client.setConnectTimeout(DEFAULT_TIMEOUT);
        client.setResponseTimeout(DEFAULT_TIMEOUT);
        client.post("http://"+gl.WebService+"/atencionesMed", params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                String responseStr = null;
                JSONArray jsonArray = null;

                try {
                    responseStr = new String(responseBody, "UTF-8");
                    jsonArray = new JSONArray(responseStr);
                    //System.out.println(jsonArray);

                    if(jsonArray.length()>0){
                        if(getActivity()!=null){
                            progressDialog.dismiss();
                            ArrayList<PacientesMedBE> array_atenciones = new ArrayList<>();
                            AdapterAtencionesMED adapterAtencionesMED = new AdapterAtencionesMED(getActivity(), array_atenciones);
                            ListView listView = (ListView) v.findViewById(R.id.lista_atenciones);
                            for(int i=0; i<jsonArray.length(); i++){
                                JSONObject jsonObject = jsonArray.getJSONObject(i);
                                PacientesMedBE paci = new PacientesMedBE();
                                paci.setPaciente(jsonObject.optString("paciente"));
                                paci.setProcedimiento_id(jsonObject.optInt("procedimiento_id"));
                                paci.setProcedimiento(jsonObject.optString("procedimiento"));
                                paci.setInfo_id(jsonObject.optInt("info_id"));
                                paci.setExa_id(jsonObject.optInt("exa_id"));
                                paci.setClie_id(jsonObject.optString("clie_id"));
                                paci.setFecha_examen(jsonObject.optString("fecha_examen"));
                                adapterAtencionesMED.add(paci);
                            }
                            listView.setAdapter(adapterAtencionesMED);
                        }
                    }else{
                        progressDialog.dismiss();
                        FragmentManager fragmentManager=getActivity().getSupportFragmentManager();
                        fragmentManager.beginTransaction().replace(R.id.contenedor, new fr_sin_atenciones()).commit();
                    }

                }catch (Exception e){
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                progressDialog.dismiss();
                if(getActivity()!=null){
                    Toast.makeText(getContext(), "Ocurrio un error al cargar los Pacientes", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void onDestroy(){
        super.onDestroy();
        if ( progressDialog!=null && progressDialog.isShowing() ){
            progressDialog.cancel();
        }
    }


    /*Otra clase*/

    public class AdapterAtencionesMED extends ArrayAdapter<PacientesMedBE>{
        Global gl = new Global();
        PDFTools pdf = new PDFTools();

        public AdapterAtencionesMED(Context context, ArrayList<PacientesMedBE> listaAtenciones){
            super(context, 0, listaAtenciones);
        }

        @Override
        public int getCount() {
            return super.getCount();
        }

        @Nullable
        @Override
        public PacientesMedBE getItem(int position) {
            return super.getItem(position);
        }

        @Override
        public long getItemId(int position) {
            return super.getItemId(position);
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            final PacientesMedBE aten = getItem(position);
            if(convertView == null){
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.layout_adaptermed, parent, false);
            }
            TextView paciente = (TextView)convertView.findViewById(R.id.tv_pacienteMED);
            TextView fecha = (TextView)convertView.findViewById(R.id.tv_fechaMED);
            TextView procedimiento = (TextView)convertView.findViewById(R.id.tv_procedimientoMED);
            TextView estado = (TextView)convertView.findViewById(R.id.tv_estadoMED);
            Button informeDetails = (Button)convertView.findViewById(R.id.btnInformeDetailsMED);

            //Muestro los objetos
            paciente.setText(aten.getPaciente());
            fecha.setText(aten.getFecha_examen());
            procedimiento.setText(aten.getProcedimiento());
            estado.setText("Informado");


            informeDetails.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //System.out.println("Tipo exa" + aten.getClie_id());
                    //DescargarExamenAndOpen(aten.getProcedimiento_id(), aten.getExa_id(), aten.getInfo_id(), 0);
                    if (ActivityCompat.checkSelfPermission(getActivity(), android.Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), android.Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                        requestPermissions(new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE},
                                1000);

                    }else{
                        try {
                            DescargarExamenAndOpen(aten.getProcedimiento_id(), aten.getExa_id(), aten.getInfo_id(), 0);
                        }catch (Exception e){
                            e.printStackTrace();
                            AlertaErrorDescarga();
                        }
                    }
                }

            });



            return convertView;
        }


        /*METODO PARA DESCARGAR LOS EXAMENES*/
        void DescargarExamenAndOpen(int tipo_exa, int exa_id, int info_id, int id_info_id_ta){
            switch (tipo_exa){
                case 1: /*Espirometria*/
                    try {
                        pdf.showPDFUrl(getContext(),"http://www.pitperu.com.pe/esp/informe.php?examen="+exa_id+"");
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                    break;
                case 2: /*Electrocargiograma*/
                    try {
                        pdf.showPDFUrl(getContext(),"http://www.pitperu.com.pe/ecg/informeECG-PDF.php?examen="+exa_id+"");
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                    break;
                case 3: /*MAPA*/
                    RequestParams params = new RequestParams();
                    params.put("exa_id", exa_id);
                    creoTMPexaMAPA(params, info_id);
                    break;
                case 4: /*Holter*/
                    try {
                        pdf.showPDFUrl(getContext(),"http://www.pitperu.com.pe/Holter/down.php?examen="+info_id+"");
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                    break;
                case 5: /*Fondo de Ojo*/
                    try {
                        pdf.showPDFUrl(getContext(),"http://www.pitperu.com.pe/fo/informewebFO.php?info_id="+info_id+"");
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                    break;
                case 6: /*Audiometría*/
                    try {
                        pdf.showPDFUrl(getContext(),"http://www.pitperu.com.pe/audiometria/informewebaudio.php?info_id="+info_id+"");
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                    break;
                case 7: /*Ergometría*/
                    try {
                        pdf.showPDFUrl(getContext(),"http://www.pitperu.com.pe/pe/down.php?examen="+info_id+"");
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                    break;
                case 8: /*Papanicolao*/
                    try {
                        pdf.showPDFUrl(getContext(),"http://www.pitperu.com.pe/pap/informeweb.php?examen="+exa_id+"");
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                    break;
                case 9: /*TeleAsistencia*/
                    try {
                        pdf.showPDFUrl(getContext(),"http://telemedicina.pitperu.com.pe/clinet/teleasistencia/receta_data.php?inf_id="+id_info_id_ta+"");
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                    break;
                case 10: /*Imágenes*/
                    try {
                        pdf.showPDFUrl(getContext(),"http://www.pitperu.com.pe/ris/informewebRIS.php?info_id="+info_id+"");
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                    break;
            }
        }

        void creoTMPexaMAPA(RequestParams params, final int folio){
            AsyncHttpClient client2 = new AsyncHttpClient();
            client2.get("http://www.pitperu.com.pe/mapa/perfil_rep.php", params, new AsyncHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                    try {
                        pdf.showPDFUrl(getContext(),"http://www.pitperu.com.pe/mapa/pdf_dump.php?informe="+folio+"");
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {

                }
            });
        }

    }



    /*Respuesta del Permiso*/
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == 1000) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                //System.out.println("ACEPTE ");
                try {

                }catch (Exception e){
                    e.printStackTrace();
                }
                return;
            }else{
                //System.out.println("NO ACEPTE ");
                AlertaDebeBrindarPermiso();
            }
        }
    }

    void AlertaDebeBrindarPermiso(){

        dialogPermiso = new SweetAlertDialog(getActivity(), SweetAlertDialog.PROGRESS_TYPE);
        dialogPermiso.show();
        dialogPermiso.setCancelable(false);
        dialogPermiso.setTitleText("Acceso no concedido!")
                .setContentText("Debe brindar el acceso \n para poder descargar \n los Exámenes.")
                .setConfirmText("Aceptar")
                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sweetAlertDialog) {
                        try {
                            dialogPermiso.dismiss();
                        }
                        catch (Exception e){
                            e.printStackTrace();
                        }
                    }
                })
                .changeAlertType(SweetAlertDialog.WARNING_TYPE);
    }

    void AlertaErrorDescarga(){

        dialogDescarga = new SweetAlertDialog(getActivity(), SweetAlertDialog.PROGRESS_TYPE);
        dialogDescarga.show();
        dialogDescarga.setCancelable(false);
        dialogDescarga.setTitleText("Error al Descargar")
                .setContentText("Porfavor Vuelva a intentar.")
                .setConfirmText("Aceptar")
                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sweetAlertDialog) {
                        try {
                            dialogDescarga.dismiss();
                        }
                        catch (Exception e){
                            e.printStackTrace();
                        }
                    }
                })
                .changeAlertType(SweetAlertDialog.ERROR_TYPE);
    }



}
