package itms.com.pe.app;


import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONObject;

import java.net.URL;
import java.util.Timer;
import java.util.TimerTask;

import cz.msebera.android.httpclient.Header;

import static itms.com.pe.app.R.color.material_blue_grey_950;


/**
 * A simple {@link Fragment} subclass.
 */
public class fr_esperando_ta extends Fragment {

    View v;
    Global gl = new Global();
    TextView tw_message;
    Button btn_videoconferencia;

    /*Timer*/
    int count= 100;
    Timer t;

    /*Timer VideoConferencia*/
    int med_acepto=0;
    int estado_cuestionario;


    /*Alerta Medico Acepto Solicitud*/
    Boolean muestroAlerta=false;


    public fr_esperando_ta() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        v = inflater.inflate(R.layout.fragment_fr_esperando_ta, container, false);
        tw_message = (TextView) v.findViewById(R.id.tv_message);
        btn_videoconferencia = (Button) v.findViewById(R.id.btnTeleAsistencia);
        tw_message.setSelected(true);

        t = new Timer();
        t.scheduleAtFixedRate(timer , 0 , 5000);


        /*Buttton Videoconferencia*/
        btn_videoconferencia.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //System.out.println("Valor Global : " + gl.val_info_id_ta);
                //tarea1 = new PruebaAsyn();
                //tarea1.execute();
                avayaAsyn open = new avayaAsyn();
                open.execute();
            }
        });

        return v;
    }

    public void colaAtencion(RequestParams params) {
        AsyncHttpClient client = new AsyncHttpClient();
        client.post("http://" + gl.WebService + "/conferenciaAvaya", params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                String responseStr = null;
                JSONObject jsonobject = null;

                try {
                    responseStr = new String(responseBody, "UTF-8");
                    jsonobject = new JSONObject(responseStr);
                    System.out.println(responseStr);

                    String val_fecha_ini_aten = jsonobject.optString("inf_fecha_ini_atencion");
                    estado_cuestionario = jsonobject.optInt("cue_estado");
                    if(val_fecha_ini_aten!="null"){
                        //med_acepto=1;
                        muestroAlertayActivoBoton();
                        muestroAlerta = true;
                    }if(estado_cuestionario==3){
                        System.out.println("Estado en el else if " + estado_cuestionario);
                        cambiarVista();
                    }
                    else{
                        med_acepto=0;
                    }

                }catch (Exception e){
                    e.printStackTrace();
                }

            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                med_acepto=0;
            }
        });
    }


    TimerTask timer= new TimerTask(){

        @Override
        public void run() {

            try {
                getActivity().runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        /*
                        count--;
                        if(count >= 0) {
                            //texto_count.setText(Integer.toString(count));
                            System.out.println(count);
                        }*/
                        if(med_acepto==0){
                            RequestParams params = new RequestParams();
                            params.put("inf_id", gl.val_info_id_ta);
                            colaAtencion(params);
                            System.out.println("Esperando...." + med_acepto);
                            //cambiarVistar();
                        }
                    }
                });
                if(estado_cuestionario==3){
                    System.out.println("FIN  :  " +med_acepto);
                    t.cancel();
                }
                /*
                if (count <= 0) {
                    t.cancel();
                }*/
            }catch (Exception e){
                e.printStackTrace();
                t.cancel();
            }
        }

    };


    /*Método abrir Avaya*/
    void openAvaya(){
        AsyncHttpClient client = new AsyncHttpClient();
        client.get("http://videoavaya.convexusmedia.pe/scopia?ID=6050&autojoin", new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                System.out.println("Open avaya!!");
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {

            }
        });
    }


    /*----------------METODO ASYNCRONO------------------*/

    public class PruebaAsyn extends AsyncTask<Void, Double, Boolean>{

        @Override
        protected Boolean doInBackground(Void... voids) {
            publishProgress();
            try {
                //openAvaya();
                Intent cliente= new Intent(Intent.ACTION_VIEW, Uri.parse("http://videoavaya.convexusmedia.pe/scopia?ID=6050&autojoin"));
                startActivity(cliente);
            }catch (Exception e){
                e.printStackTrace();
            }

            return true;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            if(aBoolean)
                Toast.makeText(getContext(), "TAREA FINALIZADA", Toast.LENGTH_SHORT).show();
        }

        @Override
        protected void onProgressUpdate(Double... values) {
            super.onProgressUpdate(values);
        }

        @Override
        protected void onCancelled(Boolean aBoolean) {
            super.onCancelled(aBoolean);
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
        }
    }


    public void displayAlertDialog() {
        AlertDialog.Builder alert = new AlertDialog.Builder(getActivity(), android.app.AlertDialog.THEME_HOLO_LIGHT);
        //alert.setTitle("Servicio de Ubicación Inhabilitados");
        alert.setMessage("El medico ha aceptado su solicitud. Por favor presione el boton de Video Conferencia.");
        alert.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        AlertDialog dialog = alert.create();
        dialog.setCancelable(false);
        dialog.show();
    }

    /*---------------------------------------*/
    final class avayaAsyn extends AsyncTask<URL, Integer, Void>{
        @Override
        protected Void doInBackground(URL... urls) {
            Intent launchIntent = getActivity().getPackageManager().getLaunchIntentForPackage("com.radvision.ctm.android.client");
            if (launchIntent != null) {
                //startActivity(launchIntent);//null pointer check in case package name was not found
                Intent cliente= new Intent(Intent.ACTION_VIEW, Uri.parse("http://videoavaya.convexusmedia.pe/scopia?ID=6050&autojoin"));
                startActivity(cliente);
            }
            else{
                try {
                    Intent intent= new Intent(Intent.ACTION_VIEW,Uri.parse("market://details?id=com.radvision.ctm.android.client"));
                    startActivity(intent);
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
            return null;
        }
    }


    void cambiarVista(){
        FragmentManager fragmentManager=getActivity().getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.contenedor, new fr_mis_atenciones()).commit();
    }

    void muestroAlertayActivoBoton(){
        if(muestroAlerta==false){
            tw_message.setText("El medico ha aceptado su solicitud. Por favor presione el boton de Video Conferencia.");
            displayAlertDialog();
            btn_videoconferencia.setEnabled(true);
            btn_videoconferencia.setBackgroundColor(getResources().getColor(material_blue_grey_950));
        }
    }


}
