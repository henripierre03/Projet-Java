module com.ism {
    requires javafx.fxml;
    requires javafx.graphics;
    requires transitive javafx.controls;
    requires transitive javafx.base;
    
    requires java.sql;
    requires org.postgresql.jdbc; // Pour PostgreSQL
    requires org.hibernate.orm.core; // Pour Hibernate Core (v5.6.1)
    requires lombok; // Pour Lombok (annotations de génération de code)
    requires org.yaml.snakeyaml;
    requires java.persistence; // Pour jBCrypt (chiffrement de mots de passe)
    requires org.controlsfx.controls;
    requires mysql.connector.j; // Pour MySQL JDBC
    requires hibernate.entitymanager; // Pour Hibernate EntityManager (v5.6.1)
    requires jbcrypt;

    exports com.ism;
    exports com.ism.core.config.security;
    exports com.ism.core.factory;
    exports com.ism.controllers;
    exports com.ism.data.entities;
    exports com.ism.core.helper;
    exports com.ism.services;
    exports com.ism.services.implement;
    exports com.ism.data.repository;
    exports com.ism.data.repository.implement;
    exports com.ism.data.enums;
    exports com.ism.controllers.implement;
    exports com.ism.controllers.admin.implement;
    exports com.ism.controllers.store.implement;
    exports com.ism.controllers.client.implement;
    
    opens com.ism.core.helper to org.controlsfx.controls, javafx.controls, javafx.fxml;
    opens com.ism.data.entities to org.hibernate.orm.core;
    opens com.ism.core.factory to javafx.fxml;
    opens com.ism.controllers to javafx.fxml;
    opens com.ism.services to com.ism.core.repository;
    opens com.ism.core.config.security to javafx.fxml, com.ism.services;
    opens com.ism.controllers.implement to javafx.fxml;
    opens com.ism.controllers.admin.implement to javafx.fxml;
    opens com.ism.controllers.store.implement to javafx.fxml;
    opens com.ism.controllers.client.implement to javafx.fxml;
}
