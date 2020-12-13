package socialnetwork.controller;

import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import socialnetwork.domain.Prietenie;
import socialnetwork.domain.Utilizator;
import socialnetwork.service.Service;
import socialnetwork.service.ServiceException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class UserFriendshipsController implements EventHandler<ActionEvent>{
    private static Scene scene;
    Service service;
    Stage stage;
    private Long id_user;
    private Utilizator utilizator;

    TableView<Prietenie> tableFriendships;
    ObservableList<Prietenie> friendships= FXCollections.observableArrayList();

    TableColumn<Prietenie,Long> id1;
    TableColumn<Prietenie,Long> id2;
    TableColumn<Prietenie, LocalDateTime> data;
    TableColumn<Prietenie,String> status;

    Button acceptFriendship;
    Button rejectFriendship;
    Button cancelFriendship;
    Button back;

    public UserFriendshipsController(Service service, Stage stage, Long id_user, Utilizator utilizator) {
        this.service = service;
        this.stage = stage;
        this.id_user = id_user;
        this.utilizator = utilizator;
    }

    public static Scene getScene() {
        return scene;
    }

    public void init(){
        initFriendships();
        initTableFriendships();
        stage.setTitle("Cereri de prietenie");

        acceptFriendship=new Button();
        acceptFriendship.setText("Accepta prietenie");
        acceptFriendship.setOnAction(this);

        rejectFriendship=new Button();
        rejectFriendship.setText("Refuza prietenie");
        rejectFriendship.setOnAction(this);

        cancelFriendship=new Button();
        cancelFriendship.setText("Retragere cerere de prietenie");
        cancelFriendship.setOnAction(this);

        back=new Button();
        back.setText("Back");
        back.setOnAction(this);

        VBox layout=new VBox();
        layout.setAlignment(Pos.CENTER);
        layout.setSpacing(40);
        layout.getChildren().add(tableFriendships);
        layout.getChildren().add(acceptFriendship);
        layout.getChildren().add(rejectFriendship);
        layout.getChildren().add(cancelFriendship);
        layout.getChildren().add(back);

        scene = new Scene(layout);
        stage.setScene(scene);
        stage.show();
    }

    public void initFriendships(){
        Iterable<Prietenie> prietenii=service.getAllFriendships();
        List<Prietenie> list= StreamSupport.stream(prietenii.spliterator(),false)
                .filter(x->x.getId().getLeft().equals(id_user) || x.getId().getRight().equals(id_user))
                .collect(Collectors.toList());
        friendships.setAll(list);
    }


    public void initTableFriendships(){
        tableFriendships=new TableView<>();

        id1=new TableColumn<>("ID1");
        id2=new TableColumn<>("ID2");
        data=new TableColumn<>("Date");
        status=new TableColumn<>("Status");

        tableFriendships.getColumns().addAll(id1,id2,status,data);
        tableFriendships.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        id1.setCellValueFactory(param -> new ReadOnlyObjectWrapper<Long>(param.getValue().getId().getLeft()));
        id2.setCellValueFactory(param -> new ReadOnlyObjectWrapper<Long>(param.getValue().getId().getRight()));
        data.setCellValueFactory(new PropertyValueFactory<Prietenie,LocalDateTime>("date"));
        status.setCellValueFactory(new PropertyValueFactory<Prietenie,String>("status"));
        tableFriendships.setItems(friendships);
    }

    public void handleAcceptFriendship(){
        Prietenie selected=tableFriendships.getSelectionModel().getSelectedItem();
        if(selected!=null){
            if(!selected.getId().getRight().equals(id_user)){
                MessageAlert.showErrorMessage(null,"Prietenie trimisa de dumneavoastra");
            }
            else{
                try {
                    service.accept_rejectFriendRequest(selected.getId().getLeft(), id_user, "accepted");
                    MessageAlert.showMessage(null, Alert.AlertType.INFORMATION, "Accepta prietenie", "Prietenie acceptata");
                    initFriendships();
                    tableFriendships.setItems(friendships);
                }catch (ServiceException e) {
                    MessageAlert.showErrorMessage(null,e.getMessage());
                }
            }
        }
        else{
            MessageAlert.showErrorMessage(null,"Trebuie selectata o prietenie");
        }
    }

    public void handleRejectFriendship(){
        Prietenie selected=tableFriendships.getSelectionModel().getSelectedItem();
        if(selected!=null){
            if(!selected.getId().getRight().equals(id_user)){
                MessageAlert.showErrorMessage(null,"Prietenie trimisa de dumneavoastra");
            }
            else{
                try {
                    service.accept_rejectFriendRequest(selected.getId().getLeft(), id_user, "rejected");
                    MessageAlert.showMessage(null, Alert.AlertType.INFORMATION, "Refuza prietenie", "Prietenie refuzata");
                    initFriendships();
                    tableFriendships.setItems(friendships);
                }catch (ServiceException e) {
                    MessageAlert.showErrorMessage(null,e.getMessage());
                }
            }
        }
        else{
            MessageAlert.showErrorMessage(null,"Trebuie selectata o prietenie");
        }
    }

    public void handleCancelFriendship(){
        Prietenie selected=tableFriendships.getSelectionModel().getSelectedItem();
        if(selected!=null){
            if(!selected.getId().getLeft().equals(id_user)){
                Utilizator u=service.findUtilizatorVersion2(selected.getId().getRight());
                MessageAlert.showErrorMessage(null,"Prietenie trimisa de "+u.getFirstName()+" "+u.getLastName());
            }
            else{
                if(!selected.getStatus().equals("pending")){
                    MessageAlert.showErrorMessage(null,"Prietenie existenta. Status: "+selected.getStatus());
                }
                else {
                    try {
                        service.removePrietenie(selected);
                        MessageAlert.showMessage(null, Alert.AlertType.INFORMATION, "Retrage prietenie", "Prietenie retrasa");
                        initFriendships();
                        tableFriendships.setItems(friendships);
                    } catch (ServiceException e) {
                        MessageAlert.showErrorMessage(null, e.getMessage());
                    }
                }
            }
        }
        else{
            MessageAlert.showErrorMessage(null,"Trebuie selectata o prietenie");
        }
    }

    @Override
    public void handle(ActionEvent event) {
        if(event.getSource()==back){
            stage.setScene(UserController.getScene());
            stage.setTitle(utilizator.getFirstName()+" "+utilizator.getLastName());
        }
        if(event.getSource()==acceptFriendship){
            handleAcceptFriendship();
        }
        if(event.getSource()==rejectFriendship){
            handleRejectFriendship();
        }
        if(event.getSource()==cancelFriendship){
            handleCancelFriendship();
        }
    }
}
