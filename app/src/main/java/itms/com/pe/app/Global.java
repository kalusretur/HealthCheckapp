package itms.com.pe.app;

import android.util.Log;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;

/**
 * Created by MEDIPRO on 17/03/2017.
 */

public class Global {

    //public static String WebService="192.168.3.54:8080/pe";
    public static String WebService="telemedicina.pitperu.com.pe:8080/appitms";
    /*Variable para el Nombre del Paciente en el navigation*/
    public static String paciente_log_nombre_all="";

    /*Variable para Teleasistencia verifico si acepto los terminos*/
    public static int acepto_terminos_ta=0;

    /*Servicios ITMS*/
    public static String    servicioDesc;
    public static int    servicioPrecio;
    public static int       servicioID;


    /*Variables CITA*/
    public static String distrito_usr;
    public static String direccion_usr;
    public static String cord_latitud_usr;
    public static String cord_longitud_usr;
    public static int paci_id_usr;
    public static int marca_paso_usr;
    public static String especialidad_ta;

    /*Variables para TC*/
    public static int val_tipo_ta=25;
    public static int val_especialidad_ta;
    public static int val_motivo_ta=124;
    public static int val_estado_ta=0;
    public static int val_paci_id_ta;
    public static String val_direccion_ta;
    public static String val_distrito_ta;
    public static int val_edad_ta;
    public static int val_cita_id_ta;
    public static int val_info_id_ta;

    /*Variables Domicilab*/
    public static String dom_procedimientos;

    /*Array Lsit Anhos*/
    public static ArrayList<Integer> anhos = new ArrayList<Integer>();

    /*Variable para ver Informes - PIT MEDICO*/
    public static String clie_id_oracle=null;

    /*Variable para capturar el Celular del Paciente de la Cita*/
    public static String cel_paci_cita = " ";

    /*Metodo Log*/
    public void registroLogs(RequestParams params){
        AsyncHttpClient client = new AsyncHttpClient();
        client.post("http://" + WebService + "/insertLogs", params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                if(statusCode==201){
                    System.out.println("Se ingreso el Log, con éxito!");
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                System.out.println("Error al registrar el Log!");
            }
        });
    }

    /*Envio Correo a ITMS*/
    public void sendMailITMS(RequestParams params){
        AsyncHttpClient client = new AsyncHttpClient();
        client.post("http://" + WebService + "/citaMailX", params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                if(statusCode==200){
                    System.out.println("Se envio el mail.");
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                System.out.println("Ocurrió un error al enviar el mail.");
            }
        });
    }

    /*Envio Correo al Cliente*/
    public void senMailCliente(RequestParams params){
        AsyncHttpClient client = new AsyncHttpClient();
        client.post("http://" + WebService + "/citaMailClienteX", params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                if(statusCode==200){
                    System.out.println("Se envio el mail.");
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                System.out.println("Ocurrió un error al enviar el mail.");
            }
        });
    }




    /*Otros métodos*/

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
