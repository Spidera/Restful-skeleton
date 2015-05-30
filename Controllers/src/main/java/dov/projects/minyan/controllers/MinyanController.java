package dov.projects.minyan.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
@RequestMapping("/Minyan")
public class MinyanController {

    @RequestMapping(value = "/", method = RequestMethod.GET)
    public String createNewMinyan(ModelMap model) {
        return "createNewMinyan";
    }
}
