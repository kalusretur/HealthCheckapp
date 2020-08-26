package itms.com.pe.app;

public class SeasonsBE {

    private String id;
    private String name;
    private String direccion;
    private String latitud;
    private String longitud;
    private int stock;

    public SeasonsBE(){

    }

    public SeasonsBE(String id, String name, String direccion, String latitud, String longitud, int stock) {
        this.id = id;
        this.name = name;
        this.direccion = direccion;
        this.latitud = latitud;
        this.longitud = longitud;
        this.stock = stock;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public String getLatitud() {
        return latitud;
    }

    public void setLatitud(String latitud) {
        this.latitud = latitud;
    }

    public String getLongitud() {
        return longitud;
    }

    public void setLongitud(String longitud) {
        this.longitud = longitud;
    }

    public int getStock() {
        return stock;
    }

    public void setStock(int stock) {
        this.stock = stock;
    }
}
