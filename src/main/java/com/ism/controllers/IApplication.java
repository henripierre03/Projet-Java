package com.ism.controllers;

import java.io.IOException;

import com.ism.data.entities.User;

import javafx.event.ActionEvent;

public interface IApplication {
    int menu();
    void run(User user);
    void msgSuccess();
    void msgSuccess(String msg);
    boolean isEmpty(int size, String msg);
    void motif(char letter);
    void motif(String letter, int nbr);
    boolean isDigit(String number);
    boolean isDecimal(String number);
    boolean isInteger(String number);
    void msgWelcome(User user);
    void logout(ActionEvent e) throws IOException;
}
