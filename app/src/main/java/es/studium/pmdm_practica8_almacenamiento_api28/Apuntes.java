package es.studium.pmdm_practica8_almacenamiento_api28;

public class Apuntes {
        private int idApunte, idCuadernoFK;
        private String fechaApunte, textoApunte;

        public Apuntes (String fechaApunte, String textoApunte, int idCuadernoFK){
            this.fechaApunte = fechaApunte;
            this.textoApunte = textoApunte;
            this.idCuadernoFK=idCuadernoFK;
        }
    public Apuntes (int idApunte, String fechaApunte, String textoApunte, int idCuadernoFK){
            this.idApunte=idApunte;
            this.fechaApunte = fechaApunte;
            this.textoApunte = textoApunte;
        this.idCuadernoFK=idCuadernoFK;
        }

    public int getIdApunte() {
        return idApunte;
    }

    public void setIdApunte(int idApunte) {
        this.idApunte = idApunte;
    }

    public int getIdCuadernoFK() {
        return idCuadernoFK;
    }

    public void setIdCuadernoFK(int idCuadernoFK) {
        this.idCuadernoFK = idCuadernoFK;
    }

    public String getFechaApunte() {
        return fechaApunte;
    }

    public void setFechaApunte(String fechaApunte) {
        this.fechaApunte = fechaApunte;
    }

    public String getTextoApunte() {
        return textoApunte;
    }

    public void setTextoApunte(String textoApunte) {
        this.textoApunte = textoApunte;
    }

    @Override
    public String toString() {
        return "Apuntes{" +
                "idApunte=" + idApunte +
                ", idCuadernoFK=" + idCuadernoFK +
                ", fechaApunte='" + fechaApunte + '\'' +
                ", textoApunte='" + textoApunte + '\'' +
                '}';
    }
}
