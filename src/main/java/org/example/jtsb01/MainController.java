package org.example.jtsb01;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class MainController {

    private static final Logger logger = LoggerFactory.getLogger(MainController.class);

    @GetMapping("/")
    public String index() {
        return "redirect:/question/list";
    }

    @GetMapping("/home")
    public String index(@AuthenticationPrincipal OAuth2User user, Model model) {
        if(user != null) {
            model.addAttribute("name", user.getAttributes().get("name"));
            model.addAttribute("email", user.getAttributes().get("email"));
            logger.info("user.getAttributes() = {}", user.getAttributes());
        }
        return "redirect:/question/list";
    }
}
