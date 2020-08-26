package itms.com.pe.app;

/**
 * Created by Sistemas on 21/08/2017.
 */

public class AtencionesBE {

    private String paciente;
    private String fecha;
    private String procedimiento;
    private String estadoExamen;
    private int valEstadoExamen;
    private int id_especialidad_ta;
    private int paci_id;
    private String direccion;
    private String distrito;
    private String fecha_nacimiento;
    private int paci_edad;
    private int cita_id;
    private int info_id_ta;
    private int proc_id;
    private int exa_id;
    private int info_id;

    public AtencionesBE(){}

    public String getPaciente() {
        return paciente;
    }

    public void setPaciente(String paciente) {
        this.paciente = paciente;
    }

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }

    public String getProcedimiento() {
        return procedimiento;
    }

    public void setProcedimiento(String procedimiento) {
        this.procedimiento = procedimiento;
    }

    public String getEstadoExamen() {
        return estadoExamen;
    }

    public void setEstadoExamen(String estadoExamen) {
        this.estadoExamen = estadoExamen;
    }

    public int getValEstadoExamen() {
        return valEstadoExamen;
    }

    public void setValEstadoExamen(int valEstadoExamen) {
        this.valEstadoExamen = valEstadoExamen;
    }

    public int getId_especialidad_ta() {
        return id_especialidad_ta;
    }

    public void setId_especialidad_ta(int id_especialidad_ta) {
        this.id_especialidad_ta = id_especialidad_ta;
    }

    public int getPaci_id() {
        return paci_id;
    }

    public void setPaci_id(int paci_id) {
        this.paci_id = paci_id;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public String getDistrito() {
        return distrito;
    }

    public void setDistrito(String distrito) {
        this.distrito = distrito;
    }

    public String getFecha_nacimiento() {
        return fecha_nacimiento;
    }

    public void setFecha_nacimiento(String fecha_nacimiento) {
        this.fecha_nacimiento = fecha_nacimiento;
    }

    public int getPaci_edad() {
        return paci_edad;
    }

    public void setPaci_edad(int paci_edad) {
        this.paci_edad = paci_edad;
    }

    public int getCita_id() {
        return cita_id;
    }

    public void setCita_id(int cita_id) {
        this.cita_id = cita_id;
    }

    public int getInfo_id_ta() {
        return info_id_ta;
    }

    public void setInfo_id_ta(int info_id_ta) {
        this.info_id_ta = info_id_ta;
    }

    public int getProc_id() {
        return proc_id;
    }

    public void setProc_id(int proc_id) {
        this.proc_id = proc_id;
    }

    public int getExa_id() {
        return exa_id;
    }

    public void setExa_id(int exa_id) {
        this.exa_id = exa_id;
    }

    public int getInfo_id() {
        return info_id;
    }

    public void setInfo_id(int info_id) {
        this.info_id = info_id;
    }
}
