package com.ism.controllers.admin;

import java.io.IOException;

import com.ism.controllers.IApplication;

import javafx.event.ActionEvent;

public interface IApplicationAdmin extends IApplication {
    void loadGestionClient(ActionEvent e) throws IOException;
    void loadGestionUser(ActionEvent e) throws IOException;
    void loadListAllUser(ActionEvent e) throws IOException;
    void loadGestionArticle(ActionEvent e) throws IOException;
    void loadArchiveDette(ActionEvent e) throws IOException;
}
