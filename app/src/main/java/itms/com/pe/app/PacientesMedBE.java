package itms.com.pe.app;

/**
 * Created by Sistemas on 7/09/2017.
 */

public class PacientesMedBE {
    private String paciente;
    private int procedimiento_id;
    private String procedimiento;
    private int info_id;
    private int exa_id;
    private String clie_id;
    private String fecha_examen;

    public PacientesMedBE(){}

    public String getPaciente() {
        return paciente;
    }

    public void setPaciente(String paciente) {
        this.paciente = paciente;
    }

    public int getProcedimiento_id() {
        return procedimiento_id;
    }

    public void setProcedimiento_id(int procedimiento_id) {
        this.procedimiento_id = procedimiento_id;
    }

    public String getProcedimiento() {
        return procedimiento;
    }

    public void setProcedimiento(String procedimiento) {
        this.procedimiento = procedimiento;
    }

    public int getInfo_id() {
        return info_id;
    }

    public void setInfo_id(int info_id) {
        this.info_id = info_id;
    }

    public int getExa_id() {
        return exa_id;
    }

    public void setExa_id(int exa_id) {
        this.exa_id = exa_id;
    }

    public String getClie_id() {
        return clie_id;
    }

    public void setClie_id(String clie_id) {
        this.clie_id = clie_id;
    }

    public String getFecha_examen() {
        return fecha_examen;
    }

    public void setFecha_examen(String fecha_examen) {
        this.fecha_examen = fecha_examen;
    }
}
