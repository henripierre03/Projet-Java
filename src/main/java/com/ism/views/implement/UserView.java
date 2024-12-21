package com.ism.views.implement;

import java.util.List;

import com.ism.core.helper.PasswordUtils;
import com.ism.data.entities.Client;
import com.ism.data.entities.User;
import com.ism.data.enums.Role;
import com.ism.services.IUserService;
import com.ism.views.IUserView;

public class UserView extends ImpView<User> implements IUserView {
    private IUserService userService;
    private static final String MSG_EMAIL = "Entrez votre adresse email : ";
    private static final String MSG_LOGIN = "Entrez votre login : ";
    private static final String MSG_PASSWORD = "Entrez votre mot de passe : ";

    public UserView(IUserService userService) {
        this.userService = userService;
    }

    @Override
    public User accountCustomer(IUserService userService, Client client) {
        User user = new User();
        user.setEmail(checkEmail());
        // Vérification Email unique
        if (userService.findBy(userService.findAll(), user) != null) {
            System.out.println("Erreur, l'email est déjà utilisé.");
            return null;
        }
        user.setLogin(checkLogin());
        // Vérification Login unique
        if (userService.findBy(userService.findAll(), user) != null) {
            System.out.println("Erreur, le login est déjà utilisé.");
            return null;
        }
        System.out.print(MSG_PASSWORD);
        user.setPassword(PasswordUtils.hashPassword(scanner.nextLine()));
        user.setStatus(true);
        user.setRole(Role.CLIENT);
        user.setClient(client);
        return user;
    }

    @Override
    public User saisir(IUserService userService) {
        User user = new User();
        user.setEmail(checkEmail());
        // Vérification Email unique
        if (userService.findBy(userService.findAll(), user) != null) {
            System.out.println("Erreur, l'email est déjà utilisé.");
            return null;
        }
        user.setLogin(checkLogin());
        // Vérification Login unique
        if (userService.findBy(userService.findAll(), user) != null) {
            System.out.println("Erreur, le login est déjà utilisé.");
            return null;
        }
        System.out.print(MSG_PASSWORD);
        user.setPassword(PasswordUtils.hashPassword(scanner.nextLine()));
        user.setRole(Role.values()[typeRole() - 1]);
        user.setStatus(true);
        return user;
    }

    private String checkLogin() {
        String login;
        // Regex pour un login valide
        String loginRegex = "^[a-zA-Z0-9_-]{5,}$";
        do {
            System.out.print(MSG_LOGIN);
            login = scanner.nextLine();
            if (login.isBlank()) {
                System.out.println("Le login ne peut pas être vide.");
            }
            // Vérifie si le login correspond au format attendu
            if (!login.matches(loginRegex)) {
                System.out.println("Format incorrect. Veuillez entrer un login valide (minimum 5 caractères, sans espace, sans accent, sans caractères spéciaux).");
            }
        } while (login.isBlank() || !login.matches(loginRegex));
        return login.trim();
    }

    private String checkEmail() {
        String email;
        // Regex pour un email valide
        String emailRegex = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$";
        do {
            System.out.print(MSG_EMAIL);
            email = scanner.nextLine();
            if (email.isBlank()) {
                System.out.println("L'email ne peut pas être vide.");
            }
            // Vérifie si l'email correspond au format attendu
            if (!email.matches(emailRegex)) {
                System.out.println("Format incorrect. Veuillez entrer un email valide (par exemple : exemple@domaine.com).");
            }
        } while (email.isBlank() || !email.matches(emailRegex));
        return email.trim();
    }
    

    @Override
    public int typeRole() {
        String choix;
        do {
            System.out.println("(1)- " + Role.values()[0].name());
            System.out.println("(2)- " + Role.values()[1].name());
            System.out.print("Choisissez un type d'utilisateur : ");
            choix = scanner.nextLine();
            if (!choix.equals("1") && !choix.equals("2")) {
                System.out.println("Erreur, choix invalide. Veuillez entrer 1 ou 2.");
            }
        } while (!choix.equals("1") && !choix.equals("2"));
        return Integer.parseInt(choix);
    }

    @Override
    public User getObject(List<User> users) {
        User user;
        String choix;
        this.afficher(users);
        do {
            user = new User();
            System.out.print("Choisissez une user par son id: ");
            choix = scanner.nextLine();
            if (isInteger(choix)) {
                user.setId(Long.parseLong(choix));
                user = userService.findBy(users, user);
            } else {
                continue;
            }
            if (user == null) {
                System.out.println("Erreur, l'user n'existe pas.");
            }
        } while (user == null);
        return user;
    }

    @Override
    public User saisir() {
        // Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'saisir'");
    }
}
