package tikape.runko.domain;

public class Avaus {
    private Integer id;
    private Alue alue;
    private String otsikko;
    private String avaus;
    private String aloitettu;
    private String aloittaja;
//    private List<Vastaus> vastaukset; // pitäisikö tässä olla lista avauksen vastauksista?
    
    //Konstruktorit
    
    public Avaus() {
    }

    public Avaus(Integer id, Alue alue, String otsikko, String avaus, String aloitettu, String aloittaja) {
        this.id = id;
        this.alue = alue;
        this.otsikko = otsikko;
        this.avaus = avaus;
        this.aloitettu = aloitettu;
        this.aloittaja = aloittaja;
    }

    public Avaus(Alue alue, String otsikko, String avaus, String aloittaja) {
        this(null, alue, otsikko, avaus, null, aloittaja);
    }
    
    //Getterit

    public Integer getId() {
        return id;
    }

    public Alue getAlue() {
        return alue;
    }

    public String getOtsikko() {
        return otsikko;
    }

    public String getAvaus() {
        return avaus;
    }

    public String getAloitettu() {
        return aloitettu;
    }

    public String getAloittaja() {
        return aloittaja;
    }

    //Setterit

    public void setId(Integer id) {
        this.id = id;
    }

    public void setAlue(Alue alue) {
        this.alue = alue;
    }

    public void setOtsikko(String otsikko) {
        this.otsikko = otsikko;
    }

    public void setAvaus(String avaus) {
        this.avaus = avaus;
    }

    public void setAloitettu(String aloitettu) {
        this.aloitettu = aloitettu;
    }

    public void setAloittaja(String aloittaja) {
        this.aloittaja = aloittaja;
    }
}