package itms.com.pe.app;


import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
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
import itms.com.pe.app.Funciones.PDFTools;


/**
 * A simple {@link Fragment} subclass.
 */
public class fr_mis_atenciones extends Fragment {

    View v;
    ProgressDialog progressDialog;
    Global gl = new Global();
    login lg = new login();

    SweetAlertDialog dialogPermiso = null;
    SweetAlertDialog dialogDescarga = null;




    public fr_mis_atenciones() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        getActivity().setTitle("Mis Atenciones");
        v = inflater.inflate(R.layout.fragment_fr_mis_atenciones, container, false);
        progressDialog = new ProgressDialog(getContext());
        progressDialog.setMessage("Cargando...");
        progressDialog.setCancelable(false);
        onDestroy();
        RequestParams params = new RequestParams();
        params.put("cod_usr",lg.cod_USR);
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
        client.post("http://"+gl.WebService+"/atencionesPac", params, new AsyncHttpResponseHandler() {
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
                                ate.setProc_id(jsonObject.optInt("proc_id"));
                                ate.setExa_id(jsonObject.optInt("exa_id"));
                                ate.setInfo_id(jsonObject.optInt("info_id"));
                                adapterAtenciones.add(ate);
                            }
                            listView.setAdapter(adapterAtenciones);
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


    /*Clase Arrayadaoter*/
    public class AdapterAtenciones extends ArrayAdapter<AtencionesBE> {
        Global gl = new Global();
        PDFTools pdf = new PDFTools();
        int numero_cita = 0;

        public AdapterAtenciones(Context context, ArrayList<AtencionesBE> listaAtenciones){
            super(context, 0, listaAtenciones);
        }

        @Override
        public int getCount() {
            return super.getCount();
        }

        @Nullable
        @Override
        public AtencionesBE getItem(int position) {
            return super.getItem(position);
        }

        @Override
        public long getItemId(int position) {
            return super.getItemId(position);
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            final AtencionesBE aten = getItem(position);
            if(convertView == null){
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.layout_adapter, parent, false);
            }
            TextView paciente = (TextView)convertView.findViewById(R.id.tv_paciente);
            TextView fecha = (TextView)convertView.findViewById(R.id.tv_fecha);
            TextView procedimiento = (TextView)convertView.findViewById(R.id.tv_procedimiento);
            TextView estado = (TextView)convertView.findViewById(R.id.tv_estado);
            Button irTeleconsulta = (Button)convertView.findViewById(R.id.btnIrTA);
            Button informeDetails = (Button)convertView.findViewById(R.id.btnInformeDetails);

            //Muestro los objetos
            paciente.setText(aten.getPaciente());
            fecha.setText(aten.getFecha());
            procedimiento.setText(aten.getProcedimiento());
            estado.setText(aten.getEstadoExamen());

            System.out.println("estado examen "+aten.getValEstadoExamen() );

            if(aten.getValEstadoExamen()==2){
                informeDetails.setVisibility(View.VISIBLE);
                irTeleconsulta.setVisibility(View.GONE);
            }else if(aten.getValEstadoExamen()==1||aten.getValEstadoExamen()==99){
                irTeleconsulta.setVisibility(View.VISIBLE);
                informeDetails.setVisibility(View.GONE);
            }else{
                informeDetails.setVisibility(View.GONE);
                irTeleconsulta.setVisibility(View.GONE);
            }

            informeDetails.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //System.out.println("Tipo exa" + aten.getProc_id());
                    //DescargarExamenAndOpen(aten.getProc_id(), aten.getExa_id(), aten.getInfo_id(), aten.getInfo_id_ta());
                    if (ActivityCompat.checkSelfPermission(getActivity(), android.Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), android.Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                        requestPermissions(new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE},
                                1000);

                    }else{
                        try {
                            DescargarExamenAndOpen(aten.getProc_id(), aten.getExa_id(), aten.getInfo_id(), aten.getInfo_id_ta());
                        }catch (Exception e){
                            e.printStackTrace();
                            AlertaErrorDescarga();
                        }
                    }
                }
            });

            irTeleconsulta.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                /*
                if(getContext() instanceof FragmentActivity){
                    /*FragmentActivity activity = (new FragmentActivity());
                    FragmentTransaction t = activity.getSupportFragmentManager().beginTransaction();
                    FragmentManager fragmentManager=getActivity().getSupportFragmentManager();*/

                    //}*/
                    Intent launchIntent = getActivity().getPackageManager().getLaunchIntentForPackage("com.radvision.ctm.android.client");
                    if(launchIntent != null){
                        gl.val_especialidad_ta  =aten.getId_especialidad_ta();
                        gl.val_paci_id_ta       =aten.getPaci_id();
                        gl.val_direccion_ta     =aten.getDireccion();
                        gl.val_distrito_ta      =aten.getDistrito();
                        gl.val_edad_ta          =aten.getPaci_edad();
                        gl.val_cita_id_ta       =aten.getCita_id();
                        gl.val_info_id_ta       =aten.getInfo_id_ta();
                        //System.out.println("Valor a insertar"+ gl.val_especialidad_ta);

                        if(aten.getValEstadoExamen()==1){
                            try {
                                FragmentManager fragmentManager = ((FragmentActivity) getContext()).getSupportFragmentManager();
                                fragmentManager.beginTransaction().replace(R.id.contenedor, new fr_tele_cuestionario()).commit();
                            }catch (Exception e){
                                e.printStackTrace();
                            }
                        }else{
                            try {
                                FragmentManager fragmentManager = ((FragmentActivity) getContext()).getSupportFragmentManager();
                                fragmentManager.beginTransaction().replace(R.id.contenedor, new fr_esperando_ta()).commit();
                            }catch (Exception e){
                                e.printStackTrace();
                            }
                        }
                    }else{
                        displayAlertDialogAvaya();
                    }




                }
            });

            return convertView;
        }

        public void OpenOther(){
            FragmentManager fragmentManager = ((FragmentActivity) getContext()).getSupportFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.contenedor, new fr_esperando_ta()).commit();
        }

        /*----------*/
        void alertaPrueba(){
            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
            builder.setTitle("Alerta");
            builder.setMessage("El info_id es : " + numero_cita).setPositiveButton("ACEPTAR", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    try {
                        pdf.showPDFUrl(getContext(),"http://telemedicina.pitperu.com.pe/clinet/teleasistencia/receta_data.php?inf_id="+numero_cita+"");
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }
            });
            AlertDialog alert = builder.create();
            alert.setCancelable(false);
            alert.show();
        }

        /*METODO PARA DESCARGAR LOS EXAMENES*/
        void DescargarExamenAndOpen(int tipo_exa, int exa_id, int info_id, int id_info_id_ta){
            switch (tipo_exa){
                case 1: /*Espirometria*/
                    //System.out.println("Soy Espirometria");
                    try {
                        pdf.showPDFUrl(getContext(),"http://www.pitperu.com.pe/esp/informe.php?examen="+exa_id+"");
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                    break;
                case 2: /*Electrocargiograma*/
                    //System.out.println("Soy Electrocargiograma");
                    try {
                        pdf.showPDFUrl(getContext(),"http://www.pitperu.com.pe/ecg/informeECG-PDF.php?examen="+exa_id+"");
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                    break;
                case 3: /*MAPA*/
                    //System.out.println("Soy MAPA");
                    RequestParams params = new RequestParams();
                    params.put("exa_id", exa_id);
                    creoTMPexaMAPA(params, info_id);
                    break;
                case 4: /*Holter*/
                    //System.out.println("Soy Holter");
                    try {
                        pdf.showPDFUrl(getContext(),"http://www.pitperu.com.pe/Holter/down.php?examen="+info_id+"");
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                    break;
                case 5: /*Fondo de Ojo*/
                    //ystem.out.println("Soy Fondo de Ojo");
                    try {
                        pdf.showPDFUrl(getContext(),"http://www.pitperu.com.pe/fo/informewebFO.php?info_id="+info_id+"");
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                    break;
                case 9: /*TeleAsistencia*/
                    //System.out.println("Soy TeleAsistencia");
                    try {
                        pdf.showPDFUrl(getContext(),"http://telemedicina.pitperu.com.pe/clinet/teleasistencia/receta_data.php?inf_id="+id_info_id_ta+"");
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

    /**
     * Alerta para enviarlo a PlayStore y descargar Avaya
     */

    public void displayAlertDialogAvaya() {
        AlertDialog.Builder alert = new AlertDialog.Builder(getActivity(), android.app.AlertDialog.THEME_HOLO_LIGHT);
        //alert.setTitle("Servicio de Ubicación Inhabilitados");
        alert.setMessage("Para iniciar una atención deberá descargar el sotfware de video conferencia");
        alert.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                try {
                    Intent intent= new Intent(Intent.ACTION_VIEW,Uri.parse("market://details?id=com.radvision.ctm.android.client"));
                    startActivity(intent);
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        });
        AlertDialog dialog = alert.create();
        dialog.setCancelable(false);
        dialog.show();
    }








}
