package com.bookstore.Controller;
/*
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/auth")
public class AuthController {

    // Login oldal megjelenítése
    @GetMapping("/login")
    public String showLoginPage() {
        return "login"; // login.html
    }

    // Login feldolgozása
    @PostMapping("/login")
    public String loginUser(@RequestParam String username,
                            @RequestParam String password,
                            Model model) {

        // TODO: hitelesítés logika
        if (authenticate(username, password)) {
            return "redirect:/home";
        } else {
            model.addAttribute("error", "Hibás felhasználónév vagy jelszó");
            return "login";
        }
    }

    // Regisztrációs oldal megjelenítése
    @GetMapping("/register")
    public String showRegisterPage(Model model) {
        model.addAttribute("user", new Object()); // később User DTO
        return "register"; // register.html
    }

    // Regisztráció feldolgozása
    @PostMapping("/register")
    public String registerUser(@ModelAttribute("user") Object user,
                               Model model) {

        // TODO: validáció + mentés
        boolean success = register(user);

        if (success) {
            return "redirect:/auth/login";
        } else {
            model.addAttribute("error", "Sikertelen regisztráció");
            return "register";
        }
    }

    // --- Segédfüggvények (stubok) ---

    private boolean authenticate(String username, String password) {
        // TODO: implementáld (pl. service hívás)
        return true;
    }

    private boolean register(Object user) {
        // TODO: implementáld (pl. adatbázis mentés)
        return true;
    }
}*/