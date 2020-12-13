package socialnetwork.controller;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import socialnetwork.service.Service;

public class LoginController implements EventHandler<ActionEvent> {
    private static Scene scene;
    Service service;
    private TextField id1;
    private Button login;
    private Button cancel;
    Stage stage;
    UserController userController;

    public LoginController(Service service,Stage stage) {
        this.service = service;
        this.stage=stage;
    }

    public static Scene getScene() {
        return scene;
    }

    public void init(){
        stage.setTitle("Login");
        id1 =new TextField();
        Label text_id=new Label("ID");

        login=new Button();
        login.setText("Login");
        login.setOnAction(this);

        cancel=new Button();
        cancel.setText("Cancel");
        cancel.setOnAction(this);

        VBox layout=new VBox();
        layout.setAlignment(Pos.CENTER);
        layout.setSpacing(40);

        HBox buttonLayout=new HBox();
        buttonLayout.getChildren().add(login);
        buttonLayout.getChildren().add(cancel);
        buttonLayout.setSpacing(40);

        HBox textLayout=new HBox();
        textLayout.getChildren().add(text_id);
        textLayout.getChildren().add(id1);

        layout.getChildren().add(textLayout);
        layout.getChildren().add(buttonLayout);

        scene = new Scene(layout);
        stage.setScene(scene);
        stage.show();
    }

    @Override
    public void handle(ActionEvent event) {
        if(event.getSource()==cancel)
            stage.close();
        if(event.getSource()==login) {
            userController = new UserController(service, stage, Long.parseLong(id1.getText()));
            userController.init();
            id1.setText("");
        }
    }
}


