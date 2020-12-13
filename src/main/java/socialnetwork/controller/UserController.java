package socialnetwork.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import socialnetwork.domain.Utilizator;
import socialnetwork.observer.Observer;
import socialnetwork.service.Service;
import socialnetwork.service.ServiceException;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class UserController implements EventHandler<ActionEvent>, Observer {
    private static Scene scene;
    Service service;
    Stage stage;
    private Long id_user;
    private Utilizator utilizator;

    TableView<Utilizator> tableUsers;
    TableView<Utilizator> tableFriends;

    ObservableList<Utilizator> users=FXCollections.observableArrayList();
    ObservableList<Utilizator> friends=FXCollections.observableArrayList();

    TableColumn<Utilizator,String> firstName;
    TableColumn<Utilizator,String> lastName;

    TableColumn<Utilizator,String> firstName_f;
    TableColumn<Utilizator,String> lastName_f;

    private Button addFriend;
    private Button removeFriend;
    private Button back;
    private Button seeFriendships;
    private Button seeMessages;

    UserFriendshipsController userFriendshipsController;
    UserMessagesController userMessagesController;

    public UserController(Service service, Stage stage, Long id_user) {
        this.service = service;
        this.stage = stage;
        this.id_user = id_user;
        utilizator=service.findUtilizator(id_user);
        userFriendshipsController= new UserFriendshipsController(service,stage,id_user,utilizator);
        userMessagesController=new UserMessagesController(service,stage,id_user,utilizator);
    }

    public static Scene getScene() {
        return scene;
    }

    public void initTableUsers(){
        tableUsers=new TableView<>();

        firstName=new TableColumn<>("First Name");
        lastName=new TableColumn<>("Last Name");
        tableUsers.getColumns().addAll(firstName,lastName);
        tableUsers.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        firstName.setCellValueFactory(new PropertyValueFactory<Utilizator,String>("firstName"));
        lastName.setCellValueFactory(new PropertyValueFactory<Utilizator,String>("lastName"));
        tableUsers.setItems(users);
    }

    public void initTableFriends(){
        tableFriends=new TableView<>();

        firstName_f=new TableColumn<>("First Name");
        lastName_f=new TableColumn<>("Last Name");
        tableFriends.getColumns().addAll(firstName_f,lastName_f);
        tableFriends.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        firstName_f.setCellValueFactory(new PropertyValueFactory<Utilizator,String>("firstName"));
        lastName_f.setCellValueFactory(new PropertyValueFactory<Utilizator,String>("lastName"));
        tableFriends.setItems(friends);
    }

    public void init(){
        initUsers();
        initFriends();
        initTableUsers();
        initTableFriends();
        service.addObserver(this);

        stage.setTitle(utilizator.getFirstName()+" "+utilizator.getLastName());

        Label text_users=new Label("USERS");
        Label text_friends=new Label("FRIENDS");

        addFriend=new Button();
        addFriend.setText("Adauga prieten");
        addFriend.setOnAction(this);

        removeFriend=new Button();
        removeFriend.setText("Sterge prieten");
        removeFriend.setOnAction(this);

        back=new Button();
        back.setText("Back");
        back.setOnAction(this);

        seeFriendships=new Button();
        seeFriendships.setText("Vezi prietenii");
        seeFriendships.setOnAction(this);

        seeMessages=new Button();
        seeMessages.setText("Vezi mesaje");
        seeMessages.setOnAction(this);

        VBox users_layout=new VBox();
        users_layout.setAlignment(Pos.CENTER);
        users_layout.getChildren().addAll(text_users,tableUsers);
        users_layout.getChildren().add(addFriend);

        VBox friends_layout=new VBox();
        friends_layout.setAlignment(Pos.CENTER);
        friends_layout.getChildren().addAll(text_friends,tableFriends);
        friends_layout.getChildren().add(removeFriend);

        HBox layout=new HBox();
        layout.setSpacing(40);
        layout.getChildren().addAll(users_layout,friends_layout);

        VBox main_layout=new VBox();
        main_layout.setAlignment(Pos.CENTER);
        main_layout.setSpacing(40);
        main_layout.getChildren().add(layout);
        main_layout.getChildren().add(seeFriendships);
        main_layout.getChildren().add(seeMessages);
        main_layout.getChildren().add(back);

        scene = new Scene(main_layout);
        stage.setScene(scene);
        stage.show();
    }

    private void initUsers() {
        Iterable<Utilizator> utilizatori=service.getAllUsers();
        List<Utilizator> list= StreamSupport.stream(utilizatori.spliterator(),false)
                .filter(x->!utilizator.suntPrieteni(x) && x.getId()!=id_user)
                .collect(Collectors.toList());
        users.setAll(list);
    }

    private void initFriends(){
        Iterable<Utilizator> prieteni=service.getAllUsers();
        List<Utilizator> list= StreamSupport.stream(prieteni.spliterator(),false)
                .filter(x->utilizator.suntPrieteni(x))
                .collect(Collectors.toList());
        friends.setAll(list);
    }

    public void handleAddFriend(){
        Utilizator selected=tableUsers.getSelectionModel().getSelectedItem();
        if(selected!=null){
            Long id_selected=selected.getId();
            try{
                service.sendFriendRequest(id_user,id_selected);
                MessageAlert.showMessage(null, Alert.AlertType.INFORMATION,"Adauga prieten","Cerere trimisa");
            }catch(ServiceException e){
                MessageAlert.showErrorMessage(null,e.getMessage());
            }
        }
        else{
            MessageAlert.showErrorMessage(null,"Trebuie selectat un utilizator");
        }
    }

    public void handleRemoveFriend(){
        Utilizator selected=tableFriends.getSelectionModel().getSelectedItem();
        if(selected!=null){
            Long id_selected=selected.getId();
            service.removeFriendVersion2(id_user,id_selected);
            utilizator.deleteFriend(selected);

            initFriends();
            tableFriends.setItems(friends);
            initUsers();
            tableUsers.setItems(users);

            MessageAlert.showMessage(null, Alert.AlertType.INFORMATION,"Sterge prieten","Prietenie stearsa");
        }
        else{
            MessageAlert.showErrorMessage(null,"Trebuie selectat un utilizator");
        }
    }
    public void reload(){
        utilizator= service.findUtilizator(id_user);
        initFriends();
        tableFriends.setItems(friends);
        initUsers();
        tableUsers.setItems(users);
    }

    @Override
    public void handle(ActionEvent event) {
        if(event.getSource()==addFriend) {
            handleAddFriend();
        }
        if(event.getSource()==removeFriend) {
            handleRemoveFriend();
        }
        if(event.getSource()==seeFriendships){
            userFriendshipsController.init();
        }
        if(event.getSource()==seeMessages){
            userMessagesController.init();
        }
        if(event.getSource()==back){
            stage.setScene(LoginController.getScene());
            stage.setTitle("Login");
        }
    }

    @Override
    public void update() {
        reload();
    }
}
