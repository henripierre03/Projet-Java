package com.ism.views.implement;

import java.util.List;
import java.util.Scanner;

import com.ism.views.IView;

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
}
