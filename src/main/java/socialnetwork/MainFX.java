package socialnetwork;

import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import socialnetwork.controller.LoginController;
import socialnetwork.domain.Prietenie;
import socialnetwork.domain.Tuple;
import socialnetwork.domain.Utilizator;
import socialnetwork.domain.validators.MessageValidator;
import socialnetwork.domain.validators.PrietenieValidator;
import socialnetwork.domain.validators.UtilizatorValidator;
import socialnetwork.grafuri.Graf;
import socialnetwork.repository.Repository;
import socialnetwork.repository.database.MessagesDB_Repo;
import socialnetwork.repository.database.PrieteniiDB_Repo;
import socialnetwork.repository.database.UtilizatorDB_Repo;
import socialnetwork.repository.file.PrietenieFile;
import socialnetwork.repository.file.UtilizatorFile;
import socialnetwork.service.Service;

import java.awt.*;

public class MainFX extends Application {

    @Override
    public void start(Stage stage) {
        final String url = "jdbc:postgresql://localhost:5432/socialnetwork";
        final String username= "postgres";
        final String pasword= "amuitatparola07";

        UtilizatorDB_Repo utilizatorDB_repo=new UtilizatorDB_Repo(url,username,pasword,new UtilizatorValidator());
        PrieteniiDB_Repo prieteniiDB_repo=new PrieteniiDB_Repo(url,username,pasword,new PrietenieValidator());
        MessagesDB_Repo messagesDB_repo=new MessagesDB_Repo(url,username,pasword,new MessageValidator());

        Service service=new Service(utilizatorDB_repo,prieteniiDB_repo,messagesDB_repo,new Graf());
        LoginController loginController=new LoginController(service,stage);
        loginController.init();
    }

    public static void main(String[] args) {
        launch();
    }

}