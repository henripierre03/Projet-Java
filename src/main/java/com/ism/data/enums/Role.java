package com.ism.data.enums;

public enum Role {
    ADMIN, BOUTIQUIER, CLIENT;

    public static Role getRole(String role) {
        for (Role r : Role.values()) {
            if (r.name().equalsIgnoreCase(role)) {
                return r;
            }
        }
        return null;
    }
}
