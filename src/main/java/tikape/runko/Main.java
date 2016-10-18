package tikape.runko;

import java.util.HashMap;
import spark.ModelAndView;
import static spark.Spark.*;
import spark.template.thymeleaf.ThymeleafTemplateEngine;
import tikape.runko.database.Database;
import tikape.runko.database.KeskustelualueDao;
import tikape.runko.database.KeskustelunavausDao;
import tikape.runko.database.VastausDao;
import tikape.runko.domain.Keskustelualue;
import tikape.runko.domain.Keskustelunavaus;
import tikape.runko.domain.Vastaus;

public class Main {

    public static void main(String[] args) throws Exception {
        Database database = new Database("jdbc:sqlite:foorumi.db");
        
        KeskustelualueDao keskustelualuedao = new KeskustelualueDao(database);
        KeskustelunavausDao keskustelunavausdao = new KeskustelunavausDao(database);
        VastausDao vastausdao = new VastausDao(database);
        
        //Pyyntö juuriosoitteeseen listaa kaikki keskustelualueet
        get("/", (req, res) -> {
            HashMap map = new HashMap<>();
            
            map.put("alueet", keskustelualuedao.findAll());
            return new ModelAndView(map, "index");
        }, new ThymeleafTemplateEngine());

        //Keskustelualueen lisääminen
        post("/", (req, res) -> {
            keskustelualuedao.create(new Keskustelualue(req.queryParams("aihealue"), req.queryParams("perustaja")));
            
            res.redirect("/");
            return "";
        });
        
        //Keskustelualuekohtaiset sivut
        get("/:id", (req, res) -> {
            HashMap map = new HashMap<>();
            Integer id = Integer.parseInt(req.params(":id"));

            map.put("alue", keskustelualuedao.findOne(id));
            map.put("avaukset", keskustelunavausdao.findAll(id));
            return new ModelAndView(map, "alue");
        }, new ThymeleafTemplateEngine());
        
        //Keskustelualueen lisääminen
        post("/:id", (req, res) -> {
            Integer id = Integer.parseInt(req.params(":id"));
            keskustelunavausdao.create(new Keskustelunavaus(keskustelualuedao.findOne(id), req.queryParams("otsikko"), req.queryParams("avaus"), req.queryParams("aloittaja")));
            
            res.redirect("/" + id);
            return "";
        });
        
        //Keskustelunavauskohtaiset sivut
        get("/:id/:idd", (req, res) -> {
            HashMap map = new HashMap<>();
            Integer id = Integer.parseInt(req.params(":id"));
            Integer idd = Integer.parseInt(req.params(":idd"));

            map.put("alue", keskustelualuedao.findOne(id));
            map.put("avaus", keskustelunavausdao.findOne(idd));
            map.put("viestit", vastausdao.findAll(idd));
            return new ModelAndView(map, "avaus");
        }, new ThymeleafTemplateEngine());
        
        //Vastauksen lisääminen
        post("/:id/:idd", (req, res) -> {
            Integer id = Integer.parseInt(req.params(":id"));
            Integer idd = Integer.parseInt(req.params(":idd"));
            vastausdao.create(new Vastaus(keskustelunavausdao.findOne(idd), req.queryParams("teksti"), req.queryParams("kirjoittaja")));
            
            res.redirect("/" + id + "/" + idd);
            return "";
        });
    }
}