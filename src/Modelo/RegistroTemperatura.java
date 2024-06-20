package Modelo;

public class RegistroTemperatura {
    private String dni;
    private double temperatura;

    public RegistroTemperatura() {
    }

    public RegistroTemperatura(String dni, double temperatura) {
        this.dni = dni;
        this.temperatura = temperatura;
    }

    public String getDni() {
        return dni;
    }

    public void setDni(String dni) {
        this.dni = dni;
    }

    public double getTemperatura() {
        return temperatura;
    }

    public void setTemperatura(double temperatura) {
        this.temperatura = temperatura;
    }

    @Override
    public String toString() {
        return "RegistroTemperatura{" +
                "dni='" + dni + '\'' +
                ", temperatura=" + temperatura +
                '}';
    }
}
