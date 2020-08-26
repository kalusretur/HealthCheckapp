package itms.com.pe.app;


import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.Typeface;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

//import com.google.android.gms.location.LocationListener;
import android.location.LocationListener;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStates;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;

import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.zxing.WriterException;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import androidmads.library.qrgenearator.QRGContents;
import androidmads.library.qrgenearator.QRGEncoder;
import cn.pedant.SweetAlert.SweetAlertDialog;
import cz.msebera.android.httpclient.Header;

import static android.content.Context.WINDOW_SERVICE;


/**
 * A simple {@link Fragment} subclass.
 */
public class fr_servicios extends Fragment implements GoogleApiClient.OnConnectionFailedListener, GoogleApiClient.ConnectionCallbacks {

    //private static final int MY_PERMISSIONS_REQUEST_ACCESS_COARSE_LOCATION = 1;

    View v;
    private SupportMapFragment mSupportMapFragment;
    Global gl = new Global();
    login lg = new login();

    Button btn_ekg, btn_mapa, btn_holter, btn_espiro, btn_fondoOjo, btn_teleconsulta, btn_Service, btn_ServiceError, btn_laboratorio;
    TextView tv_ekg, tv_ekg2, tv_mapa, tv_holter, tv_espiro, tv_espiro2, tv_fondojo, tv_fondojo2, tv_teleconsulta, tv_teleconsulta2, tv_lab1, tv_lab2;
    EditText txtDireccion;
    TextView tv_CargaDirecion;
    LinearLayout menu_01, menu_02;
    TextView siguiente, anterior;

    ArrayList<CostoServicioBE> listaPreciosServicio = new ArrayList<>();
    ProgressDialog prgDialog;
    ProgressBar prgBar;
    ProgressDialog prgDialogPrecios;
    /*---------GPS--------------*/
    Double valLatitud = 0.0;
    Double valLongitud = 0.0;
    public LocationManager mlocManager;
    Boolean reloajMapa = false;

    /*Valor Activar boton Solictud*/
    Boolean cargaInicial = false;
    int tipoExasolicitud = 0;
    Boolean existenciaCord = false;

    SweetAlertDialog pDialog = null;
    SweetAlertDialog pDialogOK = null;

    ProgressDialog prgConsultaDisponibilidad;
    private GoogleApiClient playServices;


    Marker markerOptions;
    MarkerOptions markerOptionsFull = new MarkerOptions();

    List<SeasonsBE> listaMarker = new ArrayList<SeasonsBE>();

    ImageView QR_Image;
    QRGEncoder qrgEncoder;
    Bitmap bitmap;
    Dialog myDialog;


    public fr_servicios() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        getActivity().setTitle("Nuestros Servicios");
        llenarArray();

        // Inflate the layout for this fragment
        v = inflater.inflate(R.layout.fragment_fr_servicios, container, false);
        instanciarVariables();
        QR_Image = (ImageView)v.findViewById(R.id.QR_Image);




        /*-Programo en los botones-*/
        siguiente.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                menu_01.setVisibility(View.GONE);
                menu_02.setVisibility(View.VISIBLE);
                btnTELECONSULTA();
            }
        });

        anterior.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                menu_01.setVisibility(View.VISIBLE);
                menu_02.setVisibility(View.GONE);
                btnEKG();
            }
        });


        btn_Service.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*Intent intent = new Intent(getActivity(), confirmacion_servicio.class);
                startActivity(intent);*/
                //btnCallService();
                validarHorario();

            }
        });


        btn_ekg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*Tipo de Examen 2*/
                //btnEKG();
                String inputValue = "ABL658";

                if (inputValue.length() > 0) {
                    WindowManager manager = (WindowManager) getActivity().getSystemService(WINDOW_SERVICE);
                    Display display = manager.getDefaultDisplay();
                    Point point = new Point();
                    display.getSize(point);
                    int width = point.x;
                    int height = point.y;
                    int smallerDimension = width < height ? width : height;
                    smallerDimension = smallerDimension * 3 / 4;

                    qrgEncoder = new QRGEncoder(
                            inputValue, null,
                            QRGContents.Type.TEXT,
                            smallerDimension);
                    try {
                        bitmap = qrgEncoder.encodeAsBitmap();
                        //QR_Image.setImageBitmap(bitmap);
                        show_alert(bitmap);
                    } catch (WriterException e) {
                        //Log.v(TAG, e.toString());
                        e.printStackTrace();
                    }
                } else {
                    //edtValue.setError("Required");
                    System.err.println("Error papi");
                }


            }
        });

        btn_mapa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*Tipo de Examen 3 */
                //btnMAPA();
                //show_alert();
            }
        });

        btn_holter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*Tipo de Examen 4 */
                btnHOLTER();
            }
        });

        btn_espiro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*Tipo de Examen 1 */
                btnESPIRO();
            }
        });

        btn_fondoOjo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*Tipo de Examen 5 */
                btnFONDOJO();
            }
        });

        btn_teleconsulta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*Tipo de Examen 9 */
                btnTELECONSULTA();

                /*Inicio Fragment Cuestionario
                FragmentManager fragmentManager=getActivity().getSupportFragmentManager();
                fragmentManager.beginTransaction().replace(R.id.contenedor, new fr_tele_cuestionario()).commit();
                */
            }
        });

        btn_laboratorio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                btnLABORATORIO();
            }
        });


        mSupportMapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.mapwhere);
        if (mSupportMapFragment == null) {
            FragmentManager fragmentManager = getFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            mSupportMapFragment = SupportMapFragment.newInstance();
            fragmentTransaction.replace(R.id.mapwhere, mSupportMapFragment).commit();
        }
        //prgDialog.show();
        //locationStart();

        /*Comentando 20-09-2017
        if(ValidaGPS()==false){
            displayAlertDialog();
        }else{
            cargarMapa();
        }*/
        if (getLocationMode(getContext()) != 3) {
            SolicitarPermisosGPS();
        } else {
            cargarMapa();
        }

        return v;
    }
    void show_alert(Bitmap bitmap){
        AlertDialog.Builder alertadd = new AlertDialog.Builder(getActivity());
        LayoutInflater factory = LayoutInflater.from(getActivity());
        final View view = factory.inflate(R.layout.layout_alert_image, null);
        ImageView imageView = (ImageView)view.findViewById(R.id.QR_Image_Dialog);
        imageView.setImageBitmap(bitmap);
        alertadd.setView(view);
        alertadd.setCancelable(false);
        alertadd.setNeutralButton("Aceptar", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dlg, int sumthin) {
            }
        });

        alertadd.show();
    }

    void instanciarVariables() {
        /*Instancio mis variables*/

        btn_ekg = (Button) v.findViewById(R.id.btn_ekg);
        btn_mapa = (Button) v.findViewById(R.id.btn_mapa);
        btn_holter = (Button) v.findViewById(R.id.btn_holter);
        btn_espiro = (Button) v.findViewById(R.id.btn_espiro);
        btn_fondoOjo = (Button) v.findViewById(R.id.btn_fondoOjo);
        btn_teleconsulta = (Button) v.findViewById(R.id.btn_teleconsulta);
        btn_laboratorio = (Button) v.findViewById(R.id.btn_domicilab);

        btn_Service = (Button) v.findViewById(R.id.btnSol_Service);
        btn_ServiceError = (Button) v.findViewById(R.id.btnSol_ServiceError);

        tv_ekg = (TextView) v.findViewById(R.id.tv_ekg);
        tv_ekg2 = (TextView) v.findViewById(R.id.tv_ekg2);
        tv_mapa = (TextView) v.findViewById(R.id.tv_mapa);
        tv_holter = (TextView) v.findViewById(R.id.tv_holter);
        tv_espiro = (TextView) v.findViewById(R.id.tv_espiro);
        tv_espiro2 = (TextView) v.findViewById(R.id.tv_espiro2);
        tv_fondojo = (TextView) v.findViewById(R.id.tv_fondoOjo);
        tv_fondojo2 = (TextView) v.findViewById(R.id.tv_fondoOjo2);
        tv_CargaDirecion = (TextView) v.findViewById(R.id.tv_cargaDirecion);
        tv_lab1 = (TextView) v.findViewById(R.id.tv_lab1);
        tv_lab2 = (TextView) v.findViewById(R.id.tv_lab2);

        tv_teleconsulta = (TextView) v.findViewById(R.id.tv_teleconsulta);
        tv_teleconsulta2 = (TextView) v.findViewById(R.id.tv_teleconsulta2);


        txtDireccion = (EditText) v.findViewById(R.id.txtDireccion);

        prgDialog = new ProgressDialog(getContext());
        prgDialog.setMessage("Cargando Coordenadas");
        prgDialog.setCancelable(false);

        prgDialogPrecios = new ProgressDialog(getContext());
        prgDialogPrecios.setMessage("Cargando...");
        prgDialogPrecios.setCancelable(false);

        prgBar = (ProgressBar) v.findViewById(R.id.progress_bar);
        gl.acepto_terminos_ta = 0;

        prgConsultaDisponibilidad = new ProgressDialog(getActivity(), android.app.AlertDialog.THEME_HOLO_LIGHT);
        prgConsultaDisponibilidad.setTitle("Alerta");
        prgConsultaDisponibilidad.setMessage("Obteniendo Información.");
        prgConsultaDisponibilidad.setCancelable(false);

        menu_01 = (LinearLayout) v.findViewById(R.id.menu01);
        menu_02 = (LinearLayout) v.findViewById(R.id.menu02);

        siguiente = (TextView) v.findViewById(R.id.siguiente);
        anterior = (TextView) v.findViewById(R.id.anterior);



         /*---CARGOLOS PRECIOS--
        RequestParams carga = new RequestParams();
        carga.put("val_table", "3");
        CargarPrecios(carga);*/
    }

    /*-----METODO REST---*/
    void ObtenerDireccion(RequestParams params) {
        btn_Service.setVisibility(View.GONE);
        showProgreBar();
        AsyncHttpClient client = new AsyncHttpClient();
        client.get("http://maps.googleapis.com/maps/api/geocode/json", params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {

                String responseStr = null;
                String responseStr2 = null;
                String responseStr3 = null;
                JSONArray jsonArray = null;
                JSONArray jsonArray2 = null;
                JSONArray jsonArray3 = null;

                try {
                    //System.out.println(responseStr);
                    /*First Array*/
                    responseStr = new String(responseBody, "UTF-8");
                    JSONObject jsonObject = new JSONObject(responseStr);
                    jsonArray = jsonObject.getJSONArray("results");
                    JSONObject datos = jsonArray.getJSONObject(0);
                    //String av = datos.getString("address_components");
                    //System.out.println(datos);

                    /*Second array*/
                    responseStr2 = new String(String.valueOf(datos));
                    JSONObject jsonObject2 = new JSONObject(responseStr2);
                    jsonArray2 = jsonObject2.getJSONArray("address_components");
                    JSONObject datos2 = jsonArray2.getJSONObject(0);
                    JSONObject datos3 = jsonArray2.getJSONObject(1);
                    JSONObject datos4 = jsonArray2.getJSONObject(2);
                    String numero = datos2.optString("long_name");
                    String Direccion = datos3.optString("short_name");
                    //gl.distrito_usr = datos4.getString("long_name");
                    /*Tree Array*/
                    JSONObject array3 = jsonArray.getJSONObject(1);
                    responseStr3 = new String(String.valueOf(array3));
                    JSONObject dat3 = new JSONObject(responseStr3);
                    jsonArray3 = dat3.getJSONArray("address_components");
                    JSONObject value01 = jsonArray3.getJSONObject(1);
                    gl.distrito_usr = value01.optString("long_name");
                    //System.out.println("Distrito :" +datos4.getString("long_name"));
                    //System.out.println(Direccion + " " + numero);
                    hideProgreBar();

                    //btn_Service.setVisibility(View.VISIBLE);

                    txtDireccion.setText("");
                    txtDireccion.setText(Direccion + " " + numero);
                    txtDireccion.setSelection(txtDireccion.getText().length());
                    existenciaCord = true;
                    if (cargaInicial == false) {
                        btnEKG();
                        cargaInicial = true;
                    }
                    btn_ServiceError.setVisibility(View.GONE);
                } catch (Exception e) {
                    btn_Service.setVisibility(View.GONE);
                    e.printStackTrace();
                    showProgreBarError();
                    //cargaInicial=false;
                    showButtonErrorCordenadas();
                    existenciaCord = false;
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                Log.d("Error", "Error");
                showProgreBarError();
                existenciaCord = false;
            }
        });
    }

    void ObtenerDirecionConAPI(Double latitud, Double longitud) {
        btn_Service.setVisibility(View.GONE);
        showProgreBar();
        try {
            Geocoder geocoder = new Geocoder(getActivity(), Locale.getDefault());
            List<Address> list = geocoder.getFromLocation(latitud, longitud, 1);
            if (!list.isEmpty()) {
                Address DirCalle = list.get(0);
                String direccion = DirCalle.getThoroughfare() + " " + DirCalle.getFeatureName();
                gl.distrito_usr = DirCalle.getLocality();
                hideProgreBar();
                /*-------*/
                //btn_Service.setVisibility(View.VISIBLE);

                txtDireccion.setText("");
                txtDireccion.setText(direccion);
                txtDireccion.setSelection(txtDireccion.getText().length());
                existenciaCord = true;
                if (cargaInicial == false) {
                    btnEKG();
                    cargaInicial = true;
                }
                btn_ServiceError.setVisibility(View.GONE);
            } else {
                btn_Service.setVisibility(View.GONE);
                showProgreBarError();
                showButtonErrorCordenadas();
                existenciaCord = false;
            }

        } catch (Exception e) {
            e.printStackTrace();
            Log.d("Error", "Error");
            showProgreBarError();
            existenciaCord = false;
        }
    }

    public void CargarPrecios(RequestParams params2) {
        AsyncHttpClient client = new AsyncHttpClient();
        client.get("http://" + gl.WebService + "/listPrecioServicios", params2, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {

                String responseStr = null;
                JSONArray jsonArray = null;


                try {
                    responseStr = new String(responseBody, "UTF-8");
                    jsonArray = new JSONArray(responseStr);

                    System.out.println(jsonArray);

                    if (jsonArray.length() > 0) {

                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject jsonObject = jsonArray.getJSONObject(i);
                            CostoServicioBE objServicio = new CostoServicioBE();
                            objServicio.setNombre(jsonObject.optString("nombre"));
                            objServicio.setTipo_exa(jsonObject.optInt("tipo_exa"));
                            objServicio.setPrecio(jsonObject.optInt("precio"));
                            listaPreciosServicio.add(objServicio);
                        }

                    } else {

                    }


                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }


            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                System.out.println("Error Code :" + statusCode);
                Toast.makeText(getContext(), "Ocurrio un error ", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void CargarPreciosSend(RequestParams params2) {
        AsyncHttpClient client = new AsyncHttpClient();
        client.get("http://" + gl.WebService + "/listPrecioServicios", params2, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {

                String responseStr = null;
                JSONArray jsonArray = null;


                try {
                    responseStr = new String(responseBody, "UTF-8");
                    jsonArray = new JSONArray(responseStr);

                    //System.out.println(jsonArray);

                    if (jsonArray.length() > 0) {

                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject jsonObject = jsonArray.getJSONObject(i);
                            CostoServicioBE objServicio = new CostoServicioBE();
                            objServicio.setNombre(jsonObject.optString("nombre"));
                            objServicio.setTipo_exa(jsonObject.optInt("tipo_exa"));
                            objServicio.setPrecio(jsonObject.optInt("precio"));
                            listaPreciosServicio.add(objServicio);
                        }
                        for (int i = 0; i < listaPreciosServicio.size(); i++) {
                            if (listaPreciosServicio.get(i).getTipo_exa() == tipoExasolicitud) {
                                //System.out.println(listaPreciosServicio.get(i).getNombre());
                                gl.servicioDesc = listaPreciosServicio.get(i).getNombre();
                                gl.servicioID = listaPreciosServicio.get(i).getTipo_exa();
                                gl.servicioPrecio = listaPreciosServicio.get(i).getPrecio();
                            }
                        }
                        //Toast.makeText(getContext(), gl.servicioPrecio.toString(), Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(getActivity(), confirmacion_servicio.class);
                        startActivity(intent);

                    } else {

                    }


                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }


            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                System.out.println("Error Code :" + statusCode);
                Toast.makeText(getContext(), "Ocurrio un error ", Toast.LENGTH_SHORT).show();
            }
        });
    }

    /*OTROS METODOS FUERA DE CLASE*/
    private void locationStart() {
        try {
            mlocManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);


            Localizacion Local = new Localizacion();
            Local.setFragment_mapa(this);

            final boolean gpsEnabled = mlocManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
            if (!gpsEnabled) {
                Intent settingsIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(settingsIntent);
                System.out.println("Aca primero");
            }
            if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION,}, 1000);
                System.out.println("Aca Segundo");
                return;
            }
            mlocManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, (LocationListener) Local);
            mlocManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, (LocationListener) Local);

            System.out.println("Localizacion agregada");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == 1000) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                System.out.println("ACEPTE ");
                prgDialog.show();
                locationStart();
                return;
            } else {
                System.out.println("NO ACEPTE ");
            }
        }
    }

    /**
     * Metodo del Gestor de GPS
     * @param connectionResult
     */
    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    /**
     * Metodo del Gestor de GPS
     * @param bundle
     */
    @Override
    public void onConnected(@Nullable Bundle bundle) {

    }

    /**
     * Metodo del Gestor GPS
     * @param i
     */
    @Override
    public void onConnectionSuspended(int i) {

    }


    /* Aqui empieza la Clase Localizacion */
    public class Localizacion implements LocationListener {
        fr_servicios fragment_mapa;

        public fr_servicios getFragment_mapa() {
            return fragment_mapa;
        }

        public void setFragment_mapa(fr_servicios fragment_mapa) {
            this.fragment_mapa = fragment_mapa;
        }

        @Override
        public void onLocationChanged(Location loc) {
            // Este metodo se ejecuta cada vez que el GPS recibe nuevas coordenadas
            // debido a la deteccion de un cambio de ubicacion

            loc.getLatitude();
            loc.getLongitude();

            valLatitud = loc.getLatitude();
            valLongitud = loc.getLongitude();
            //System.out.println("Latitud  :" +valLatitud);
            //System.out.println("Longitud :" +valLongitud);

            if (valLatitud != 0.0 && valLongitud != 0.0 && reloajMapa == false) {
                reloajMapa = true;
                if (mSupportMapFragment != null) {
                    mSupportMapFragment.getMapAsync(new OnMapReadyCallback() {
                        @Override
                        public void onMapReady(GoogleMap googleMap) {

                            googleMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                                @Override
                                public boolean onMarkerClick(Marker marker) {

                                    for (int i = 0; i < listaMarker.size(); i++) {
                                        if (marker.getTitle().equals(listaMarker.get(i).getName())) {
                                            System.err.println(listaMarker.get(i).getName() + "  Stock : " + listaMarker.get(i).getStock());
                                            if (listaMarker.get(i).getStock() > 0) {
                                                System.out.println("Hay Stpck");
                                            } else {
                                                pDialog = new SweetAlertDialog(getActivity(), SweetAlertDialog.PROGRESS_TYPE)
                                                        .setTitleText("Cargando");
                                                pDialog.show();
                                                pDialog.setCancelable(false);
                                                pDialog.setTitleText("Horario no Disponible")
                                                        .setContentText("Horario de atención \n L-V 8:30 a.m. a 07:30 p.m. \n No domingos y feriados.")
                                                        .setConfirmText("Aceptar")
                                                        .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                                            @Override
                                                            public void onClick(SweetAlertDialog sweetAlertDialog) {
                                                                try {
                                                                    pDialog.dismiss();
                                                                } catch (Exception e) {
                                                                    e.printStackTrace();
                                                                }
                                                            }
                                                        })
                                                        .changeAlertType(SweetAlertDialog.WARNING_TYPE);
                                            }
                                        }
                                    }

                                    /*if(marker.getTitle().equals("U Lima")){
                                        System.err.println("Estamos en la U de Lima Concha le vale");
                                    }if(marker.getTitle().equals("Juan de Arona")){
                                        System.err.println("Estacion Juan de Arona");
                                    }*/
                                    return false;
                                }
                            });

                            googleMap.getUiSettings().setAllGesturesEnabled(true);
                            googleMap.setMyLocationEnabled(true);
                            try {
                                int permissionCheck = ContextCompat.checkSelfPermission(getContext(),Manifest.permission.ACCESS_COARSE_LOCATION);
                            }
                            catch (Exception e){
                                e.printStackTrace();
                            }


                            LatLng marker_latlng = new LatLng(valLatitud, valLongitud); // MAKE THIS WHATEVER YOU WANT
                            CameraPosition cameraPosition = new CameraPosition.Builder().target(marker_latlng).zoom(15.0f).build();
                            CameraUpdate cameraUpdate = CameraUpdateFactory.newCameraPosition(cameraPosition);
                            googleMap.moveCamera(cameraUpdate);
                            googleMap.getUiSettings().setZoomControlsEnabled(true);
                            //Posicion del zoom y GPS
                            googleMap.setPadding(0,200,0,200);

                            googleMap.setOnCameraChangeListener(new GoogleMap.OnCameraChangeListener() {
                                @Override
                                public void onCameraChange(CameraPosition cameraPosition) {
                                    String ubicacion = String.valueOf(cameraPosition.target.latitude)+","+ String.valueOf(cameraPosition.target.longitude);
                                    gl.cord_latitud_usr = cameraPosition.target.latitude+"";
                                    gl.cord_longitud_usr = cameraPosition.target.longitude+"";
                                    //System.out.println("Ubicacion CAM" +ubicacion);
                                    /*Comentado 14-09-2017 - Por problemas con Android 5.0
                                    RequestParams params = new RequestParams();
                                    params.put("latlng",ubicacion);
                                    params.put("sensor", true);
                                    ObtenerDireccion(params);
                                    */
                                    ObtenerDirecionConAPI(cameraPosition.target.latitude,cameraPosition.target.longitude);
                                }
                            });

                            googleMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
                                @Override
                                public void onMapClick(LatLng latLng) {
                                    //-12.128863, -77.000470
                                    System.err.println("Gaaaaaaaaaaaaaaaaaa");


                                }
                            });
                            Double latitude2=-12.128863;
                            Double longitude2=-77.000470;

                            Double latitude=37.421998333333335;
                            Double longitude=-122.08400000000002;

                            LatLng lating = new LatLng(latitude,longitude);
                            LatLng lating2 = new LatLng(latitude2,longitude2);

                            //googleMap.setOnMarkerClickListener(this);



                            createMarkers(googleMap);
                        }
                    });
                    try {
                        prgDialog.dismiss();
                    }catch (Exception e){
                        e.printStackTrace();
                    }

                }

            }else {
                stop();
            }
        }



        @Override
        public void onProviderDisabled(String provider) {
            // Este metodo se ejecuta cuando el GPS es desactivado
            System.out.println("GPS Desactivado");
        }

        @Override
        public void onProviderEnabled(String provider) {
            // Este metodo se ejecuta cuando el GPS es activado
            System.out.println("GPS Activado");
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
            switch (status) {
                case LocationProvider.AVAILABLE:
                    Log.d("debug", "LocationProvider.AVAILABLE");
                    break;
                case LocationProvider.OUT_OF_SERVICE:
                    Log.d("debug", "LocationProvider.OUT_OF_SERVICE");
                    break;
                case LocationProvider.TEMPORARILY_UNAVAILABLE:
                    Log.d("debug", "LocationProvider.TEMPORARILY_UNAVAILABLE");
                    break;
            }
        }
    }

    /*-------------------*/

    void llenarArray(){
        listaMarker.add(new SeasonsBE("E001","Miraflores","Av Jose Larco 235, Miraflores 15074","-12.120134", "-77.028952", 0));
        listaMarker.add(new SeasonsBE("E002","Domingo Orue","Av. Paseo de la República 5226, Miraflores 15047","-12.108703", "-77.026108", 5));
        listaMarker.add(new SeasonsBE("E003","Javier Prado","Av. Paseo de la República, Lince 15046","-12.090201", "-77.023738", 3));
        listaMarker.add(new SeasonsBE("E004","Juan de Arona","Av Juan de Arona 151, San Isidro 15046","-12.096776", "-77.032093", 10));
        listaMarker.add(new SeasonsBE("E005","U Lima","Avenida Manuel Olguín, Cercado de Lima 15023","-12.085267", "-76.973099", 4));
    }

    void createMarkers(GoogleMap googleMap){
        for(int i=0; i<listaMarker.size(); i++){
            Marker nameMarker2 = googleMap.addMarker(new MarkerOptions()
                    .position(new LatLng(Double.parseDouble(listaMarker.get(i).getLatitud()),Double.parseDouble(listaMarker.get(i).getLongitud())))
                    .title(listaMarker.get(i).getName())
                    .snippet("Stock : "+listaMarker.get(i).getStock()+"")
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.globoapp)));
        }
    }
    void createMarke(Marker name, GoogleMap googleMap){
        // name = googleMap.addMarker();
        //Ubicacion Google
        // 37.422
        // -122.084


    }


    /**OTROS METODOS VOID**/
    void ActivarCampos(TextView txt ,String txtString, Button btn, String btnString){
        txt.setText(txtString);

        btn.setText(btnString);
        if(existenciaCord==true){
            btn.setVisibility(View.GONE);
        }else{
            btn.setVisibility(View.GONE);
        }
    }
    public boolean ValidaGPS(){
        LocationManager locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
        boolean statusOfGPS = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        return statusOfGPS;
    }
    void LimpiarBotones(){
        tv_ekg.setText("");
        tv_ekg2.setText("");
        tv_mapa.setText("");
        tv_holter.setText("");
        tv_teleconsulta.setText("");
        tv_teleconsulta2.setText("");
        tv_espiro.setText("");
        tv_fondojo.setText("");
        tv_espiro2.setText("");
        tv_fondojo2.setText("");
        tv_lab1.setText("");
        tv_lab2.setText("");
    }
    void ObtenerDatosporExamen(int exa_id){
        for (int i=0; i<listaPreciosServicio.size(); i++){
            if(listaPreciosServicio.get(i).getTipo_exa()==exa_id){
                System.out.println(listaPreciosServicio.get(i).getNombre());
                gl.servicioDesc = listaPreciosServicio.get(i).getNombre();
                gl.servicioID   = listaPreciosServicio.get(i).getTipo_exa();
                gl.servicioPrecio = listaPreciosServicio.get(i).getPrecio();
            }
        }
    }
    void stop(){
        Localizacion Local = new Localizacion();
        mlocManager.removeUpdates(Local);
    }
    void callNext(){
        cargarMapa();
    }
    void cargarMapa(){
        if (ActivityCompat.checkSelfPermission((Activity)getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    1000);
        } else {
            prgDialog.show();
            locationStart();
        }
    }
    void cargarMapaBK(){
        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    1000);
        } else {
            prgDialog.show();
            locationStart();
        }
    }
    public void displayAlertDialog() {
        AlertDialog.Builder alert = new AlertDialog.Builder(getActivity());
        alert.setTitle("Servicio de Ubicación Inhabilitados");
        alert.setMessage("Es necesario activar el GPS para hacer uso de la Aplicación");
        alert.setPositiveButton("OK", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                cargarMapa();
            }
        });
        AlertDialog dialog = alert.create();
        dialog.setCancelable(false);
        dialog.show();
    }
    void hideProgreBar(){
        prgBar.setVisibility(View.GONE);
        tv_CargaDirecion.setVisibility(View.GONE);
        txtDireccion.setVisibility(View.VISIBLE);
    }
    void showProgreBar(){
        prgBar.setVisibility(View.VISIBLE);
        tv_CargaDirecion.setVisibility(View.VISIBLE);
        txtDireccion.setVisibility(View.GONE);
    }
    void showProgreBarError(){
        prgBar.setVisibility(View.GONE);
        tv_CargaDirecion.setVisibility(View.GONE);
        txtDireccion.setVisibility(View.VISIBLE);
        txtDireccion.setText("Ocurrio un error al obtener la dirección");
        txtDireccion.setSelection(txtDireccion.getText().length());
    }
    void btnEKG(){
        LimpiarBotones();
        ActivarCampos(tv_ekg,"Electro", btn_Service, "    Solicitar Electrocardiograma    ");
        tv_ekg2.setText("Cardiograma");
        tipoExasolicitud = 2;
        ObtenerDatosporExamen(tipoExasolicitud);
        gl.acepto_terminos_ta=0;
    }
    void btnMAPA(){
        LimpiarBotones();
        ActivarCampos(tv_mapa, "Mapa", btn_Service, "    Solicitar Mapa    ");
        tipoExasolicitud = 3;
        ObtenerDatosporExamen(tipoExasolicitud);
        gl.acepto_terminos_ta=0;
    }
    void btnHOLTER(){
        LimpiarBotones();
        ActivarCampos(tv_holter, "Holter",btn_Service, "    Solicitar Holter    ");
        tipoExasolicitud = 4;
        ObtenerDatosporExamen(tipoExasolicitud);
        gl.acepto_terminos_ta=0;
    }
    void btnESPIRO(){
        LimpiarBotones();
        ActivarCampos(tv_espiro, "Espiro", btn_Service, "    Solicitar Espirometria    ");
        tv_espiro2.setText("metria");
        tipoExasolicitud = 1;
        ObtenerDatosporExamen(tipoExasolicitud);
        gl.acepto_terminos_ta=0;
    }
    void btnFONDOJO(){
        LimpiarBotones();
        ActivarCampos(tv_fondojo, "Fondo", btn_Service, "    Solicitar Fondo de Ojo    ");
        tv_fondojo2.setText("de Ojo");;
        tipoExasolicitud = 5;
        ObtenerDatosporExamen(tipoExasolicitud);
        gl.acepto_terminos_ta=0;
    }
    void btnTELECONSULTA(){
        LimpiarBotones();
        ActivarCampos(tv_teleconsulta, "Tele", btn_Service, "    Solicitar Tele Asistencia    ");
        tv_teleconsulta2.setText("Asistencia");
        tipoExasolicitud = 9;
        ObtenerDatosporExamen(tipoExasolicitud);
        /*
        if(gl.acepto_terminos_ta==0){
            alertaTeleasistencia();
            LimpiarBotones();
            ActivarCampos(tv_teleconsulta, "Tele", btn_Service, "    Solicitar Tele Asistencia    ");
            tv_teleconsulta2.setText("Asistencia");
            tipoExasolicitud = 9;
            ObtenerDatosporExamen(tipoExasolicitud);
        }else{
            LimpiarBotones();
            ActivarCampos(tv_teleconsulta, "Tele", btn_Service, "    Solicitar Tele Asistencia    ");
            tv_teleconsulta2.setText("Asistencia");
            tipoExasolicitud = 9;
            ObtenerDatosporExamen(tipoExasolicitud);
        }*/
                /*Configuracion Avaya

        Intent launchIntent = getActivity().getPackageManager().getLaunchIntentForPackage("com.radvision.ctm.android.client");
        if (launchIntent != null) {
            //startActivity(launchIntent);//null pointer check in case package name was not found
            Intent cliente= new Intent(Intent.ACTION_VIEW, Uri.parse("http://videoavaya.convexusmedia.pe/scopia?ID=6051&autojoin"));
            startActivity(cliente);
        }
        else{
            try {
                Intent intent= new Intent(Intent.ACTION_VIEW,Uri.parse("market://details?id=com.radvision.ctm.android.client"));
                startActivity(intent);
            }catch (Exception e){
                e.printStackTrace();
            }
        }*/
    }

    void btnLABORATORIO(){
        LimpiarBotones();
        ActivarCampos(tv_lab1, "Laboratorio", btn_Service, "    Solicitar Laboratorio    ");
        tipoExasolicitud = 15;
        ObtenerDatosporExamen(tipoExasolicitud);
        gl.acepto_terminos_ta=0;
        gl.servicioID=tipoExasolicitud;
    }
    void showButtonErrorCordenadas(){
        btn_ServiceError.setVisibility(View.VISIBLE);
        btn_ServiceError.setText("   No se obtubo la dirección   ");
    }

    void alertaTeleasistencia(){
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        TextView msgTitle = new TextView(getActivity());
        msgTitle.setText("Términos y Condiciones");
        msgTitle.setTextColor(Color.BLACK);
        msgTitle.setPadding(10, 15, 15, 10);
        msgTitle.setTypeface(null, Typeface.BOLD);
        msgTitle.setTextSize(17);
        msgTitle.setGravity(Gravity.CENTER_HORIZONTAL);
        //builder.setView(msgTitle);
        //builder.setTitle("Términos y Condiciones");
        builder.setMessage(condiciones());
        builder.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                gl.acepto_terminos_ta=1;
                dialogInterface.cancel();
                btnCallService();

            }
        });
        builder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.cancel();
            }
        });
        AlertDialog alert = builder.create();
        alert.setCustomTitle(msgTitle);
        alert.setCancelable(false);
        alert.show();
    }

    void alertTwoButtons() {
        new AlertDialog.Builder(getContext())
                .setTitle("Two Buttons")
                .setMessage("Do you think Mike is awesome?")
                .setIcon(R.drawable.candado)
                .setPositiveButton("YES",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        })
                .setNegativeButton("NO", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                }).show();
    }

    /*---------*/
    String condiciones(){
        return "NO UTILICE ESTE APLICATIVO EN CASO DE EMERGENCIAS MÉDICAS.\n\nEl uso de este sitio web, así como de los servicios y las herramientas provistas a través del mismo, está condicionado a la aceptación por parte de Usted de los términos de uso descritos a continuación. Por favor, lea atentamente los siguientes términos de uso antes de utilizar este sitio. Debe entender que las interacciones con los servicios de consulta médica online (a través de una plataforma tecnológica) son un complemento del manejo médico que pueda usted tener con su médico de cabecera. Las asesorías médicas ofrecidas por TELEMEDICINA DE PERU S.A. por este medio no pretenden ni están en capacidad de sustituir la evaluación física regular de salud que pueda realizar con su médico de cabecera o el especialista que controla su salud usualmente." +
                "\n\n Glosario de términos \n\n La Empresa: \n\n Es la compañía que ha diseñado e implementado la plataforma tecnológica denominada CheckHealth, que facilita la prestación del servicio de telemedicina brindado por TELEMEDICINA DE PERU S.A. \n\n Es una persona jurídica dedicada principalmente a la prestación de servicios de salud utilizando medios tecnológicos actuales. \n\n  " +
                "\n\n Profesional de la salud:\n\n Es un médico que trabaja bajo las instrucciones y supervisión de TELEMEDICINA DE PERU S.A. y quien está legalmente facultado para ejercer la medicina, ha cumplido con todas las obligaciones que la ley le impone para ejercer la medicina en la República del Perú y no se encuentra legalmente impedido en el ejercicio de su profesión.\n\n" +
                "El Usuario:\n\n Es toda persona que luego de registrarse, ingresa a recibir los Servicios. \n\n El Servicio:\n\n Es la prestación mediante la cual TELEMEDICINA DE PERU S.A. pone a disposición de “El Usuario”, una plataforma tecnológica que le permite al “Profesional de la salud” adscrito a TELEMEDICINA DE PERU S.A. prestar servicios de telemedicina. \n\n Teleasistencia médica:\n\n A los efectos de este contrato, se entiende como Teleasistencia la prestación de servicios médicos, tales como, consulta, diagnóstico, monitoreo o tratamiento de pacientes a distancia, a través del uso combinado de información y comunicación, para el intercambio de información confiable en el diagnóstico, tratamiento y prevención de enfermedades, con el objetivo de mantener o mejorar la salud de los usuarios del servicio.\n\n" +
                "PROVEEDORES DISPONIBLES\n\n Cuando el “Usuario” solicita una consulta para obtener la asesoría de un “Profesional de la salud”, TELEMEDICINA DE PERU S.A. le ofrecerá la opción de consultar con cualquier profesional médico que se encuentre disponible para atenderlo. Para su tranquilidad, TELEMEDICINA DE PERU S.A. certifica que los profesionales de la salud que prestan El Servicio cumplen con los requisitos de ley que son necesarios para proveer los servicios que ofrecen. Sin embargo, Usted es el único responsable de decidir si desea acceder a una consulta con un determinado “Profesional de la salud”. Cuando Usted solicita una consulta, está aceptando consultar específicamente a un “Profesional de la salud” y por lo tanto está dando inicio a una relación directa médico (“Profesional de la salud”) - paciente (“Usuario”), y Usted expresamente asume la responsabilidad de tener la consulta a través de esta plataforma tecnológica.\n\n " +
                "REGISTRO DE USUARIO\n\n Para tener acceso a los servicios en línea, Usted deberá registrarse como “Usuario” desde la República del Perú y deberá crear una cuenta personal, suministrando la información que se solicita. Con la excepción de las cuentas de sus “dependientes”, Usted acuerda no crear más de una cuenta de usuario, ni crear una cuenta para ninguna otra persona, sino ha sido autorizado de manera expresa y escrita. Asimismo, Usted se compromete a proveer información correcta, verdadera y actualizada al momento de registrarse o cuando realice la actualización de su ficha de información personal. En caso de proveer información falsa o incorrecta o de registrarse desde un país donde la empresa no tenga presencia, nos reservamos el derecho de suspender su cuenta y negarle el acceso al uso del sistema.\n\n" +
                "ACCESO:\n\n Al registrarse, deberá designar un correo electrónico (usuario) y una clave de acceso. Estos datos son personales e intransferibles y usted es responsable de mantener la seguridad de su cuenta, cuidando en todo momento la seguridad y confidencialidad de sus claves de acceso. Usted entiende y acepta que en ningún caso podrá facilitar el acceso de terceros a esta plataforma tecnológica mediante el uso de su nombre de usuario y contraseña. En tal sentido, Usted asume la responsabilidad y los daños y perjuicios que puedan derivarse del uso no autorizado de su cuenta. En caso que la seguridad de su cuenta se vea comprometida, deberá notificar a TELEMEDICINA DE PERU S.A. de manera inmediata, al correo siguiente: info@itms.com.pe.\n\n " +
                "PRIVACIDAD\n\n Toda información que relacione su identidad con las condiciones físicas y mentales presentes, pasadas o futuras y su historia médica, es considerada información de carácter privado y tratada como tal. TELEMEDICINA DE PERU S.A. garantiza la confidencialidad del acto médico (“Profesional de la salud”) que exige la Ley peruana. Como parte de su proceso operativo TELEMEDICINA DE PERU S.A. registra su información personal y se obligan a no venderla, licenciarla o compartirla fuera de TELEMEDICINA DE PERU S.A. excepto: (i) si Usted autoriza expresamente a hacerlo, (ii) solo con el fin de proporcionar nuestros productos o servicios o aquellos de nuestras entidades asociadas, (iii) cuando sea requerido o permitido por la Ley. A estos efectos, se considera como información personal, los nombres y apellidos, la edad, el correo electrónico y los teléfonos de contacto del Afiliado o Usuario y sus dependientes. TELEMEDICINA DE PERU S.A. se reserva el derecho de utilizar data anónima proveniente de informaciones generadas y almacenadas durante el proceso de interacción entre El Usuario y el “Profesional de la salud” con el fin de proveer información estadística.\n\n " +
                "PROHIBICIÓN DE USO INDEBIDO\n\n Usted se obliga a no utilizar la aplicación (app) para ninguna finalidad que sea ilegal o esté prohibida por estos términos de uso y las leyes vigentes.\n\n ACEPTACIÓN Y AUTORIZACIÓN\n\n AL ACEPTAR estos términos usted manifiesta que ha leído con detenimiento este documento, se obliga a cumplir con lo aquí estipulado, acepta expresamente todas las disposiciones en él contenidas y cumple con otorgar la autorización correspondiente en favor de TELEMEDICINA DE PERU S.A. por el uso de esta plataforma para los procedimientos y/o consultas médicas que obtenga a través de la misma. Si Usted decide no aceptar los Términos de Uso, le pedimos que por favor no utilice los servicios prestados por esta aplicacion, pues Usted no está autorizado a utilizarlo. El Usuario entiende que la aceptación de los términos aquí contenidos, es equivalente a un contrato escrito y con este protocolo se da cumplimiento expreso a la legislación nacional vigente.";
    }

    void btnCallService(){
        System.out.println("x : "+tipoExasolicitud);
        switch (tipoExasolicitud){
            case 9:
                RequestParams params = new RequestParams();
                params.put("cod_usr", lg.cod_USR);
                validarTAproceso(params);
                break;
            case 15:
                Intent intent = new Intent(getActivity(), servicios_Domicilab.class);
                startActivity(intent);
                gl.direccion_usr=txtDireccion.getText().toString();
                break;
            default:
                RequestParams carga = new RequestParams();
                carga.put("val_table", "3");
                CargarPreciosSend(carga);
                gl.direccion_usr=txtDireccion.getText().toString();
                break;
        }
        /*
        if(tipoExasolicitud==9){
            //validarHorarioMedico();
            RequestParams params = new RequestParams();
            params.put("cod_usr", lg.cod_USR);
            validarTAproceso(params);
        }else{
            RequestParams carga = new RequestParams();
            carga.put("val_table", "3");
            CargarPreciosSend(carga);
            gl.direccion_usr=txtDireccion.getText().toString();
        }*/
    }


    /*------------------------*/
    /*Valido Horario de Atención*/
    public void validarHorario(){
        prgConsultaDisponibilidad.show();
        AsyncHttpClient client = new AsyncHttpClient();
        client.post("http://" + gl.WebService + "/validaDisponibilidad", new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                //System.out.println("Succes : " + statusCode);
                prgConsultaDisponibilidad.dismiss();
                btnCallService();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                prgConsultaDisponibilidad.dismiss();
                //System.out.println("onFailure : " + statusCode);
                pDialog = new SweetAlertDialog(getActivity(), SweetAlertDialog.PROGRESS_TYPE)
                        .setTitleText("Cargando");
                pDialog.show();
                pDialog.setCancelable(false);
                pDialog.setTitleText("Horario no Disponible")
                        .setContentText("Horario de atención \n L-V 8:30 a.m. a 07:30 p.m. \n No domingos y feriados.")
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
        });
    }

    /*----------------------------------*/
    //Validad Disponibilidad del Médico para TA
    public void validarHorarioMedico(){
        AsyncHttpClient client = new AsyncHttpClient();
        client.post("http://" + gl.WebService + "/validaDisponibilidadMedico", new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                //System.out.println("Succes : " + statusCode);
                if(gl.acepto_terminos_ta==1){
                    RequestParams carga = new RequestParams();
                    carga.put("val_table", "3");
                    CargarPreciosSend(carga);
                    gl.direccion_usr=txtDireccion.getText().toString();
                }else{
                    //Toast.makeText(getContext(), "Debe aceptar los terminos.", Toast.LENGTH_SHORT).show();
                    alertaTeleasistencia();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                //System.out.println("onFailure : " + statusCode);
                pDialog = new SweetAlertDialog(getActivity(), SweetAlertDialog.PROGRESS_TYPE)
                        .setTitleText("Cargando");
                pDialog.show();
                pDialog.setCancelable(false);
                pDialog.setTitleText("TeleAsistencia")
                        .setContentText("El servicio no está disponible \n en estos momentos")
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
        });
    }


    /*Valido que no tenga una TA en proceso*/
    void validarTAproceso(RequestParams params){
        AsyncHttpClient client = new AsyncHttpClient();
        client.post("http://" + gl.WebService + "/validoTAenproceso", params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                String responseStr = null;
                JSONObject jsonobject = null;
                try {
                    responseStr = new String(responseBody, "UTF-8");
                    jsonobject = new JSONObject(responseStr);
                    int tipo_procedimiento = jsonobject.optInt("tipo_pro");

                    if(tipo_procedimiento==9){
                        alertaExisteTAenProceso();
                    }else{
                        validarHorarioMedico();
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                System.out.println("Error al verificar TA StatusCode : " + statusCode);
            }
        });
    }

    void alertaExisteTAenProceso(){
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Alerta");
        builder.setMessage("Ya existe una Teleasistencia en proceso, por favor dirigete al menú Mis Atenciones.").setPositiveButton("ACEPTAR", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        AlertDialog alert = builder.create();
        alert.setCancelable(false);
        alert.show();
    }

    /*Actualización 20-09-2017*/

    /**
     * Obtengo el valor del GPS y Metodo
     * @param context
     * @return
     */
    public int getLocationMode(Context context)
    {
        try {
            return Settings.Secure.getInt(getContext().getContentResolver(), Settings.Secure.LOCATION_MODE);
        } catch (Settings.SettingNotFoundException e) {
            e.printStackTrace();
        }

        return 0;
    }

    /**
     * Metodo para Cambiar el Metodo de GPS
     * @param mGoogleApiClient
     * @param activity
     */
    public static void locationChecker(GoogleApiClient mGoogleApiClient, final Activity activity) {
        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(30 * 1000);
        locationRequest.setFastestInterval(5 * 1000);
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(locationRequest);
        builder.setAlwaysShow(true);
        PendingResult<LocationSettingsResult> result =
                LocationServices.SettingsApi.checkLocationSettings(mGoogleApiClient, builder.build());
        result.setResultCallback(new ResultCallback<LocationSettingsResult>() {
            @Override
            public void onResult(LocationSettingsResult result) {
                final Status status = result.getStatus();
                final LocationSettingsStates state = result.getLocationSettingsStates();
                System.out.println("STADO CODE " + status.getStatus());
                switch (status.getStatusCode()) {

                    case LocationSettingsStatusCodes.SUCCESS:
                        // All location settings are satisfied. The client can initialize location
                        // requests here.
                        System.out.println("PRIMERO");
                        break;
                    case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                        // Location settings are not satisfied. But could be fixed by showing the user
                        // a dialog.
                        try {
                            // Show the dialog by calling startResolutionForResult(),
                            // and check the result in onActivityResult().
                            status.startResolutionForResult(
                                    activity, 1000);
                            System.out.println("SEGUNDO");
                        } catch (IntentSender.SendIntentException e) {
                            // Ignore the error.
                            System.out.println("ATCH");
                        }
                        break;
                    case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                        // Location settings are not satisfied. However, we have no way to fix the
                        // settings so we won't show the dialog.
                        System.out.println("ULTIMO");
                        break;
                }
            }
        });
    }

    void SolicitarPermisosGPS(){
        playServices = new GoogleApiClient
                .Builder(getActivity())
                .enableAutoManage(getActivity(), 34992, this)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();

        locationChecker(playServices, getActivity());

    }

    @Override
    public void onPause() {
        super.onPause();
        try {
            if(playServices!=null){
                playServices.stopAutoManage(getActivity());
                playServices.disconnect();
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        try {
            if(playServices!=null){
                playServices.stopAutoManage(getActivity());
                playServices.disconnect();
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }


}
