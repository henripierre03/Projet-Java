package com.ism.core.helper;

import org.mindrot.jbcrypt.BCrypt;

public abstract class PasswordUtils {
    private PasswordUtils() {
    }

    // Hacher un mot de passe
    public static String hashPassword(String password) {
        return BCrypt.hashpw(password, BCrypt.gensalt());
    }

    // Vérifier un mot de passe par rapport à un hash
    public static boolean checkPassword(String password, String hashedPassword) {
        return BCrypt.checkpw(password, hashedPassword);
    }
}
