package itms.com.pe.app;

/**
 * Created by Sistemas on 22/08/2017.
 */

public class EspecialidadTABE {

    private int id_procedimiento;
    private String des_procedimiento;
    private String prec_procedimiento;
    private int monto_culqui;
    private String  text_dialog;

    public EspecialidadTABE(){}

    public int getId_procedimiento() {
        return id_procedimiento;
    }

    public void setId_procedimiento(int id_procedimiento) {
        this.id_procedimiento = id_procedimiento;
    }

    public String getDes_procedimiento() {
        return des_procedimiento;
    }

    public void setDes_procedimiento(String des_procedimiento) {
        this.des_procedimiento = des_procedimiento;
    }

    public String getPrec_procedimiento() {
        return prec_procedimiento;
    }

    public void setPrec_procedimiento(String prec_procedimiento) {
        this.prec_procedimiento = prec_procedimiento;
    }

    public int getMonto_culqui() {
        return monto_culqui;
    }

    public void setMonto_culqui(int monto_culqui) {
        this.monto_culqui = monto_culqui;
    }

    public String getText_dialog() {
        return text_dialog;
    }

    public void setText_dialog(String text_dialog) {
        this.text_dialog = text_dialog;
    }
}
