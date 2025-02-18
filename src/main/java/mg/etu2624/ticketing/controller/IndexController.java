package mg.etu2624.ticketing.controller;

import mg.itu.prom16.annotation.controller.Controller;
import mg.itu.prom16.annotation.mapping.Url;
import mg.itu.prom16.annotation.response.ResponseBody;

@Controller
public class IndexController {
    
    @Url("/")
    public String index() {
        return "index";	
    }
    
}