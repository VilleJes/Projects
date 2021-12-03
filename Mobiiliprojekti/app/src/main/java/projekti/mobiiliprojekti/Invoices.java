package projekti.mobiiliprojekti;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Invoices {
    private String orderID;
    private String vuokraaja;
    private String vuokranAntaja;
    private ArrayList<String> paivamaarat;
    private int hinta;
    private String otsikko;
    private String osoite;
    private String vuokraajaID;
    private String numero;
    private String sahkoposti;
    private String asiakasNro;
    private String asiakasSposti;
    private String mokkiID;
    private String mokkiImage;

    public Invoices() {

    }


    public Invoices(String messagePushID, String vuokraOtsikko, String vuokraOsoite, String mID, String mImage, String uid, String aNro, String aSposti, String vuokraaja, String vuokraID, String posti, String nro, ArrayList<String> paivat, int summa) {
        this.setOrderID(messagePushID);
        this.setOtsikko(vuokraOtsikko);
        this.setOsoite(vuokraOsoite);
        this.setMokkiID(mID);
        this.setMokkiImage(mImage);
        this.setVuokraaja(uid);
        this.setAsiakasNro(aNro);
        this.setAsiakasSposti(aSposti);
        this.setVuokranAntaja(vuokraaja);
        this.setVuokraajaID(vuokraID);
        this.setSahkoposti(posti);
        this.setNumero(nro);
        this.setPaivamaarat(paivat);
        this.setHinta(summa);
    }

    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("orderID", getOrderID());
        result.put("otsikko", getOtsikko());
        result.put("osoite",getOsoite());
        result.put("mokkiID",getMokkiID());
        result.put("mokkikuva",getMokkiImage());
        result.put("asiakas", getVuokraaja());
        result.put("asiakkaan nro",getAsiakasNro());
        result.put("asiakkaan sposti",getAsiakasSposti());
        result.put("vuokranantaja", getVuokranAntaja());
        result.put("vuokranantajaID",getVuokraajaID());
        result.put("vuokranantajan numero",getNumero());
        result.put("vuokranantajan sposti",getSahkoposti());
        result.put("paivamaarat", getPaivamaarat());
        result.put("hinta", getHinta());
        return result;
    }

    public String getMokkiImage() { return mokkiImage; }
    public void setMokkiImage(String mImage) { this.mokkiImage = mImage; }
    public String getMokkiID() { return mokkiID; }
    public void setMokkiID(String mID) {
        this.mokkiID = mID;
    }
    public String getAsiakasNro() { return asiakasNro; }
    public void setAsiakasNro(String aNro) {
        this.asiakasNro = aNro;
    }
    public String getAsiakasSposti() { return asiakasSposti; }
    public void setAsiakasSposti(String aPosti) {
        this.asiakasSposti = aPosti;
    }
    public String getNumero() {
        return numero;
    }
    public void setNumero(String nro) {
        this.numero = nro;
    }
    public String getSahkoposti() {
        return sahkoposti;
    }
    public void setSahkoposti(String sposti) {
        this.sahkoposti = sposti;
    }

    public String getVuokraajaID() {
        return vuokraajaID;
    }
    public void setVuokraajaID(String vuokraID) {
        this.vuokraajaID = vuokraID;
    }
    public String getOrderID() {
        return orderID;
    }

    public String getOsoite() {
        return osoite;
    }

    public void setOsoite(String vuokraOsoite) {
        this.osoite = vuokraOsoite;
    }

    public void setOrderID(String orderID) {
        this.orderID = orderID;
    }

    public String getVuokraaja() {
        return vuokraaja;
    }

    public void setVuokraaja(String vuokraaja) {
        this.vuokraaja = vuokraaja;
    }

    public String getVuokranAntaja() {
        return vuokranAntaja;
    }

    public void setVuokranAntaja(String vuokranAntaja) {
        this.vuokranAntaja = vuokranAntaja;
    }

    public ArrayList<String> getPaivamaarat() {
        return paivamaarat;
    }

    public void setPaivamaarat(ArrayList<String> paivamaarat) {
        this.paivamaarat = paivamaarat;
    }

    public int getHinta() {
        return hinta;
    }

    public void setHinta(int hinta) {
        this.hinta = hinta;
    }

    public String getOtsikko() {
        return otsikko;
    }

    public void setOtsikko(String otsikko) {
        this.otsikko = otsikko;
    }
}
