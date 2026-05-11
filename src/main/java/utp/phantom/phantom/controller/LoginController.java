package utp.phantom.phantom.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class LoginController {

    @GetMapping("/login")
    public String login(@RequestParam(required = false) String error) {
        if (error != null) {
            return "redirect:/?loginError=true";
        }
        return "redirect:/";
    }
}