package com.ism.core.helper;

public abstract class Helper {
    public void dump(Object object) {
        System.out.println(object);
    }

    public void die() {
        return;
    }

    public void dd(Object object) {
        dump(object);
        die();
    }

    /**
     * Création de la fonction capitalize qui prends comme paramètre {@code String}
     * et retourne le mots avec premier lettre en majuscule
     *
     * @param input {@code String}
     * 
     * @return {@code String}
     */
    public static String capitalize(String input) {
        if (input == null || input.isEmpty()) {
            return input;
        }
        return input.substring(0, 1).toUpperCase() + input.substring(1).toLowerCase();
    }
}
