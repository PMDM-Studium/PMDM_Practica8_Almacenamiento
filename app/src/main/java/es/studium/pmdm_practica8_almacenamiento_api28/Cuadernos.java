package es.studium.pmdm_practica8_almacenamiento_api28;

public class Cuadernos {
    private int idCuaderno;
    private String nombreCuaderno;

    public Cuadernos (String nombreCuaderno){
        this.nombreCuaderno=nombreCuaderno;
    }
    public Cuadernos (int idCuaderno, String nombreCuaderno){
        this.idCuaderno=idCuaderno;
        this.nombreCuaderno=nombreCuaderno;
    }
    public int getIdCuaderno() {
        return idCuaderno;
    }

    public void setIdCuaderno(int idCuaderno) {
        this.idCuaderno = idCuaderno;
    }

    public String getNombreCuaderno() {
        return nombreCuaderno;
    }

    public void setNombreCuaderno(String nombreCuaderno) {
        this.nombreCuaderno = nombreCuaderno;
    }
    @Override
    public String toString() {
        return "Cuadernos{" +
                "idCuaderno=" + idCuaderno +
                ", nombreCuaderno='" + nombreCuaderno + '\'' +
                '}';
    }
}