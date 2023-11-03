package sysc4806.project.controllers;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import sysc4806.project.models.ApplicationUser;
import sysc4806.project.models.UserDetails;
import sysc4806.project.repositories.ApplicationUserRepository;

import static org.springframework.security.web.context.HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY;

@Controller
public class LoginController {
    @Autowired
    private ApplicationUserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @GetMapping(path = "/login")
    public String login() {
        return "login";
    }

    @PostMapping(path = "/loginHandler")
    public String logUserIn(HttpServletRequest req, @RequestParam(name = "email") String email, @RequestParam(name = "password") String password) {
        ApplicationUser user = userRepository.findApplicationUserByEmail(email);
        if (user != null && passwordEncoder.matches(password, user.getPassword())) {
            UserDetails userDetails = new UserDetails(
                    user.getEmail(),
                    user.getPassword(),
                    SecurityContextHolder.getContext().getAuthentication().getAuthorities(),
                    user.getId()
            );

            SecurityContext sc = SecurityContextHolder.getContext();
            sc.setAuthentication(new UsernamePasswordAuthenticationToken(userDetails, password, userDetails.getAuthorities()));
            HttpSession session = req.getSession(true);
            session.setAttribute(SPRING_SECURITY_CONTEXT_KEY, sc);
            return "redirect:/home";
        }
        return "redirect:/login?error";
    }

}
