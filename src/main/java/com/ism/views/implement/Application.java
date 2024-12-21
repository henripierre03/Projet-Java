package com.ism.views.implement;

import com.ism.core.helper.Helper;
import com.ism.data.entities.User;
import com.ism.views.IApplication;

public abstract class Application implements IApplication {
    protected static final String MSG_CHOICE = "Choisissez une option : ";
    protected static final String MSG_EXIT = "Merci d'avoir utiliser notre application, au revoir !";
    protected static final String MSG_CLIENT = "Aucun client n'a été enregistré.";
    protected static final String MSG_ACCOUNT = "Compte créer avec succès !";
    protected static final String MSG_ERROR = "Erreur, choix invalide.";
    protected static final String MSG_FILTER = "Filtrer par: ";

    @Override
    public boolean isEmpty(int size, String msg) {
        if (size == 0) {
            System.out.println(msg);
            return true;
        }
        return false;
    }

    @Override
    public void msgWelcome(User user) {
        System.out.println("Bienvenue " + Helper.capitalize(user.getLogin()) + "!");
    }

    @Override
    public void msgSuccess() {
        msgSuccess("Ajouté avec succès !");
    }

    @Override
    public void msgSuccess(String msg) {
        System.out.println(msg);
    }

    @Override
    public void motif(char c) {
        motif(String.valueOf(c), 64);
    }

    @Override
    public void motif(String letter, int nbr) {
        System.out.println(String.valueOf(letter).repeat(nbr));
    }

    @Override
    public boolean isDigit(String number) {
        return number.matches("\\d");
    }

    @Override
    public boolean isInteger(String number) {
        try {
            Integer.parseInt(number);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    @Override
    public boolean isDecimal(String number) {
        try {
            Double.parseDouble(number);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    // Mettre en place les entrer pour continuer par exemple:
    // "Appuyez sur une touche pour continuer..."
}
