package com.ism.controllers.implement;

import java.util.List;
import java.util.Scanner;

import com.ism.controllers.IView;

public abstract class ImpView<T> implements IView<T> {
    protected static Scanner scanner;

    public static void setScanner(Scanner scanner) {
        ImpView.scanner = scanner;
    }

    @Override
    public void afficher(List<T> list) {
        list.forEach(System.out::println);
    }
    
    protected boolean isInteger(String number) {
        try {
            Integer.parseInt(number);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    protected boolean isDecimal(String number) {
        try {
            Double.parseDouble(number);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    @Override
    public void motif(String letter) {
        motif(letter, 64);
    }

    @Override
    public void motif(String letter, int nbr) {
        System.out.println(String.valueOf(letter).repeat(nbr));
    }

    public boolean checkEmail(String email) {
        // Regex pour un email valide
        String emailRegex = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$";
        if (email.matches(emailRegex)) {
            return true;
        }
        return false;
    }

    public boolean checkLogin(String login) {
        // Regex pour un login valide
        String loginRegex = "^[a-zA-Z0-9_-]{5,}$";
        if (login.matches(loginRegex)) {
            return true;
        }
        return false;
    }
}
