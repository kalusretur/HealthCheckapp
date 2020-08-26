package itms.com.pe.app;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.view.ContextThemeWrapper;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import cz.msebera.android.httpclient.Header;
import ir.mirrajabi.searchdialog.SimpleSearchDialogCompat;
import ir.mirrajabi.searchdialog.core.BaseSearchDialogCompat;
import ir.mirrajabi.searchdialog.core.SearchResultListener;
import ir.mirrajabi.searchdialog.core.Searchable;

import static android.R.color.primary_text_dark_nodisable;
import static itms.com.pe.app.R.color.material_blue_grey_950;

public class servicios_Domicilab extends AppCompatActivity {

    Global gl = new Global();
    ArrayList<DomicilabBE> listaDomicilab = new ArrayList<>();
    Button btnProcedimientos, btnComfirmacion;
    Toolbar toolbar;
    ArrayList<DomicilabBE> listax = new ArrayList<>();;
    ArrayList<DomicilabBE> listaSolicitadaCliente  = new ArrayList<>();
    AdapterProcedimientosDomicilab adapterProcedimientosDomicilab;
    /**
     * Variables del Servicio
     */
    Double contadorMostrar;
    int contadorPagar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_servicios__domicilab);
        InicializarVariabler();

        toolbar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        btnProcedimientos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cargarProcedimientosDomicilab();
            }
        });

        btnComfirmacion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertaConfirmacion(listaSolicitadaCliente.size(),calculadoraDomicilab());
            }
        });
    }


    /**
     * Otros Métodos
     */
    private void InicializarVariabler(){
        btnProcedimientos   = (Button) findViewById(R.id.btnProcedimientos);
        btnComfirmacion     = (Button) findViewById(R.id.btn_domicilab_confirmacion);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
    }
    String  calculadoraDomicilab(){
        String mostrarEtiqueta="";
        contadorMostrar = 0.00;
        contadorPagar   = 0;

        for (int i=0; i<listaSolicitadaCliente.size(); i++){
            contadorMostrar = contadorMostrar + Double.parseDouble(listaSolicitadaCliente.get(i).getEtiqueta_proce());
            contadorPagar = contadorPagar + listaSolicitadaCliente.get(i).getPrecio_proce();
        }
        mostrarEtiqueta = integerFormat(contadorMostrar);
        return mostrarEtiqueta;

        /*System.err.println("TOTAL ES : "+ contadorMostrar);
        System.err.println("TOTAL PAGAR : "+ contadorPagar);
        System.out.println(integerFormat(contadorMostrar));*/
    }

    String procedimientosInsert(ArrayList<DomicilabBE> lista){
        String procedimientos="";
        for(int i=0; lista.size()>i; i++){
            DomicilabBE domicilab = new DomicilabBE();
            domicilab.setCod_proce(lista.get(i).getCod_proce());
            procedimientos=procedimientos+domicilab.getCod_proce()+",";
        }
        return procedimientos.substring(0, procedimientos.length()-1);
    }

    void alertaConfirmacion(int cantidad, String total){
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this, android.R.style.Theme_Holo_Light_Dialog_MinWidth);
        builder.setMessage("Número de Procedimientos : "+cantidad+" \nCosto Total :  "+total+"")
                .setCancelable(false)
                .setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        finish();
                        gl.servicioDesc = calculadoraDomicilab();
                        gl.servicioPrecio = contadorPagar;
                        gl.dom_procedimientos = procedimientosInsert(listaSolicitadaCliente);
                        Intent intent = new Intent(servicios_Domicilab.this, confirmacion_servicio.class);
                        startActivity(intent);
                    }
                })
                .setNegativeButton("Corregir", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });

        //Creating dialog box
        android.app.AlertDialog alert = builder.create();
        //Setting the title manually
        alert.setTitle("Confirmación");
        alert.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        alert.show();
    }

    private void cargarProcedimientosDomicilab(){
        AsyncHttpClient client = new AsyncHttpClient();
        client.post("http://"+gl.WebService+"/integracion/listServiciosDomicilab", new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                String responseStr = null;
                JSONArray jsonArray = null;
                listaDomicilab.clear();
                try {
                    responseStr = new String(responseBody, "UTF-8");
                    jsonArray = new JSONArray(responseStr);
                    //System.err.println(jsonArray);

                    if(jsonArray.length()>0){
                        for (int i=0; i<jsonArray.length(); i++){

                            JSONObject jsonObject = jsonArray.getJSONObject(i);
                            DomicilabBE objDomicilab = new DomicilabBE();
                            objDomicilab.setCod_proce(jsonObject.optString("cod_proce"));
                            objDomicilab.setNom_proce(jsonObject.optString("nom_proce"));
                            objDomicilab.setEtiqueta_proce(jsonObject.optString("etiqueta_proce"));
                            objDomicilab.setPrecio_proce(jsonObject.optInt("precio_proce"));
                            objDomicilab.setEstado_proce(jsonObject.optInt("estado_proce"));
                            listaDomicilab.add(objDomicilab);
                        }
                        alertaProcedimientosDomiciLab(listaDomicilab);
                    }else{

                    }
                }catch (Exception e){
                    e.printStackTrace();
                }

            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                //System.out.println("Error Code :" + statusCode);
                Toast.makeText(servicios_Domicilab.this, "Ocurrio un error ", Toast.LENGTH_SHORT).show();
            }
        });
    }

    void alertaProcedimientosDomiciLab(final ArrayList lista){
        new SimpleSearchDialogCompat(servicios_Domicilab.this, "Busqueda", "What are you looking for...?",
                null, lista, new SearchResultListener<Searchable>() {
            @Override
            public void onSelected(BaseSearchDialogCompat baseSearchDialogCompat, Searchable searchable, int i) {
                Toast.makeText(servicios_Domicilab.this, ""+searchable.getTitle(), Toast.LENGTH_SHORT).show();
                baseSearchDialogCompat.dismiss();
                for (DomicilabBE paDomicilabBE : listaDomicilab){
                    if(paDomicilabBE.getNom_proce().equals(searchable.getTitle())){
                        //System.err.println(paDomicilabBE.getPrecio_proce());
                        addProcedimientos(paDomicilabBE);
                        activarButonConfirmacion(listaSolicitadaCliente);
                    }
                }
            }
        }).show();
    }

    void addProcedimientos(DomicilabBE objeto){
        adapterProcedimientosDomicilab = new AdapterProcedimientosDomicilab(servicios_Domicilab.this, listaSolicitadaCliente);
        ListView listView = (ListView) findViewById(R.id.lista_procedimientos_domicilab);
        adapterProcedimientosDomicilab.add(objeto);
        adapterProcedimientosDomicilab.notifyDataSetChanged();
        listView.setAdapter(adapterProcedimientosDomicilab);
    }

    void activarButonConfirmacion(ArrayList<DomicilabBE> lista){
        if(lista.size()>0){
            btnComfirmacion.setEnabled(true);
            btnComfirmacion.setBackgroundColor(getResources().getColor(material_blue_grey_950));
        }else{
            btnComfirmacion.setEnabled(false);
            btnComfirmacion.setBackgroundColor(Color.parseColor("#D7D7D7"));
        }

    }

    String integerFormat(Double i) {
        DecimalFormat df = new DecimalFormat("S/ #.00");
        String s = df.format(i);
        return s;

    }

    /**
     * Clase ArrrayAdapter
     */
    public class  AdapterProcedimientosDomicilab extends ArrayAdapter<DomicilabBE>{
        public AdapterProcedimientosDomicilab(Context context, ArrayList<DomicilabBE> listaProcedimiestosSelecionados){
            super(context, 0, listaProcedimiestosSelecionados);
        }

        @Override
        public int getCount() {
            return super.getCount();
        }

        @Nullable
        @Override
        public DomicilabBE getItem(int position) {
            return super.getItem(position);
        }

        @Override
        public long getItemId(int position) {
            return super.getItemId(position);
        }

        @Override
        public int getPosition(@Nullable DomicilabBE item) {
            return super.getPosition(item);
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            final DomicilabBE domi = getItem(position);
            if(convertView == null){
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.layout_adapter_domicilab, parent, false);
            }
            TextView tw_procedimiento = (TextView)convertView.findViewById(R.id.tw_domi_proc);
            TextView tw_domi_costo = (TextView)convertView.findViewById(R.id.tw_domi_costo);
            Button btn_remove = (Button)convertView.findViewById(R.id.btnDeleteDomicilab);

            tw_procedimiento.setText(domi.getNom_proce());
            tw_domi_costo.setText("S/ "+domi.getEtiqueta_proce());

            btn_remove.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    listaSolicitadaCliente.remove(getPosition(domi));
                    activarButonConfirmacion(listaSolicitadaCliente);
                    adapterProcedimientosDomicilab.notifyDataSetChanged();
                }
            });
            return convertView;
        }
    }
}
