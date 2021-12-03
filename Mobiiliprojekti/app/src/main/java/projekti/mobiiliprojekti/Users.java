package projekti.mobiiliprojekti;

public class Users {
    private String name;
    private String image;
    private String numero;
    private String sahkoposti;

    public Users() {

    }

    public Users(String sName, String sImage, String sNumero, String sSahkoposti) {
        this.setName(sName);
        this.setImage(sImage);
        this.setNumero(sNumero);
        this.setSahkoposti(sSahkoposti);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getNumero() {
        return numero;
    }

    public void setNumero(String numero) {
        this.numero = numero;
    }

    public String getSahkoposti() {
        return sahkoposti;
    }

    public void setSahkoposti(String sahkoposti) {
        this.sahkoposti = sahkoposti;
    }
}
