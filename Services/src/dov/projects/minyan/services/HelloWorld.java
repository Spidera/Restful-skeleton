package dov.projects.minyan.services;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * User: dovlaviati
 * Date: 5/25/15, at 5:18 PM
 */
@Controller
@RequestMapping("/")
public class HelloWorld {

    @RequestMapping(method = RequestMethod.GET)
    public String printWelcome(ModelMap modelMap) {
        modelMap.addAttribute("message", "YO!");
        return "index";
    }
}
