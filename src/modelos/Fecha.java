package modelos;

public class Fecha {
    private int año;
    private int mes;
    private int día;
    private int hora;
    private int minuto;

    public Fecha(int año, int mes, int día, int hora, int minuto) {
        this.año = año;
        this.mes = mes;
        this.día = día;
        this.hora = hora;
        this.minuto = minuto;
    }

    public int getAño() { return año; }
    public int getMes() { return mes; }
    public int getDía() { return día; }
    public int getHora() { return hora; }
    public int getMinuto() { return minuto; }

    @Override
    public String toString() {
        return String.format("%04d-%02d-%02d %02d:%02d", año, mes, día, hora, minuto);
    }
}