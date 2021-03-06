package tikape.runko.domain;

public class Alue {
    private Integer id;
    private String aihealue;
    private String kuvaus;
    private String perustettu;
    private String perustaja;
//    private List<Keskustelunavaus> avaukset; // pitäisikö tässä olla lista alueen avauksista?

    // Konstruktorit:
    
    public Alue() {
    }

    public Alue(Integer id, String aihealue, String kuvaus, String perustettu, String perustaja) {
        this.id = id;
        this.aihealue = aihealue;
        this.kuvaus = kuvaus;
        this.perustettu = perustettu;
        this.perustaja = perustaja;
    }

    public Alue(String aihealue, String perustaja) {
        this(null, aihealue, null, null, perustaja);
    }

    // Getterit:
    
    public Integer getId() {
        return id;
    }

    public String getAihealue() {
        return aihealue;
    }

    public String getKuvaus() {
        return kuvaus;
    }
    
    public String getPerustettu() {
        return perustettu;
    }

    public String getPerustaja() {
        return perustaja;
    }

    // Setterit:

    public void setId(Integer id) {
        this.id = id;
    }

    public void setAihealue(String aihealue) {
        this.aihealue = aihealue;
    }

    public void setKuvaus(String kuvaus) {
        this.kuvaus = kuvaus;
    }

    public void setPerustettu(String perustettu) {
        this.perustettu = perustettu;
    }

    public void setPerustaja(String perustaja) {
        this.perustaja = perustaja;
    }
}