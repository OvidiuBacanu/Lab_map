package socialnetwork.controller;

import javafx.beans.property.ReadOnlyObjectWrapper;
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
import javafx.util.Callback;
import javafx.util.StringConverter;
import socialnetwork.domain.Message;
import socialnetwork.domain.Prietenie;
import socialnetwork.domain.Utilizator;
import socialnetwork.domain.validators.ValidationException;
import socialnetwork.service.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;


public class UserMessagesController implements EventHandler<ActionEvent> {
    Service service;
    Stage stage;
    private Long id_user;
    private Utilizator utilizator;

    Button back;
    Button send;
    Button reply;
    Button removeToUser;

    TableView<Message> tableMessages;
    ObservableList<Message> messages= FXCollections.observableArrayList();
    ObservableList<Utilizator> users=FXCollections.observableArrayList();
    ObservableList<Utilizator> users_to=FXCollections.observableArrayList();
    List<Utilizator> users_to_list=new ArrayList<>();

    TableColumn<Message,String> from;
    TableColumn<Message,String> to;
    TableColumn<Message,LocalDateTime> data;
    TableColumn<Message,String> mesaj;

    TextField mesaj_text_field;
    TextField raspuns_text_field;
    ComboBox<Utilizator> utilizatorComboBox;
    ListView<Utilizator> utilizatorListView;

    public UserMessagesController(Service service, Stage stage, Long id_user, Utilizator utilizator) {
        this.service = service;
        this.stage = stage;
        this.id_user = id_user;
        this.utilizator = utilizator;
    }

    public void init(){
        initMessages();
        initTableMessages();
        initUsers();
        initComboBoxUtilizatori();

        Label to_text;
        Label mesaj_text;
        Label raspuns_text;
        to_text=new Label("To");
        mesaj_text=new Label("Mesaj");
        raspuns_text=new Label("Raspuns");

        back=new Button();
        back.setText("Back");
        back.setOnAction(this);

        send=new Button();
        send.setText("Trimite mesaj nou");
        send.setOnAction(this);

        reply=new Button();
        reply.setText("Raspunde");
        reply.setOnAction(this);

        removeToUser=new Button();
        removeToUser.setText("Sterge destinatar");
        removeToUser.setOnAction(this);

        mesaj_text_field=new TextField();
        raspuns_text_field=new TextField();

        utilizatorListView=new ListView<>();
        utilizatorListView.setCellFactory(new Callback<ListView<Utilizator>, ListCell<Utilizator>>() {
            @Override
            public ListCell<Utilizator> call(ListView<Utilizator> param) {
                ListCell<Utilizator> cell=new ListCell<Utilizator>(){

                    @Override
                    protected void updateItem(Utilizator item,boolean empty){
                        super.updateItem(item,empty);
                        if(item!=null){
                            setText(item.getFirstName()+" "+item.getLastName());
                        }
                        else{
                            setText("");
                        }
                    }
                };
                return cell;
            }
        });

        HBox to_layout=new HBox();
        to_layout.setAlignment(Pos.CENTER);
        to_layout.getChildren().add(to_text);
        to_layout.getChildren().add(utilizatorComboBox);

        VBox layout=new VBox();
        layout.setSpacing(10);
        layout.getChildren().add(to_layout);
        layout.getChildren().add(utilizatorListView);
        layout.getChildren().add(removeToUser);
        layout.getChildren().add(mesaj_text);
        layout.getChildren().add(mesaj_text_field);
        layout.getChildren().add(send);

        VBox table_layout=new VBox();
        table_layout.setAlignment(Pos.CENTER);
        table_layout.setSpacing(25);
        table_layout.getChildren().add(tableMessages);
        table_layout.getChildren().add(raspuns_text);
        table_layout.getChildren().add(raspuns_text_field);
        table_layout.getChildren().add(reply);

        HBox main_layout=new HBox();
        main_layout.setSpacing(40);
        main_layout.getChildren().add(layout);
        main_layout.getChildren().add(table_layout);

        VBox main_main_layout=new VBox();
        main_main_layout.setAlignment(Pos.CENTER);
        main_main_layout.setSpacing(30);
        main_main_layout.getChildren().add(main_layout);
        main_main_layout.getChildren().add(back);

        Scene scene = new Scene(main_main_layout);
        stage.setTitle("Mesaje");
        stage.setScene(scene);
        stage.show();
    }

    public void initTableMessages(){
        tableMessages=new TableView<>();

        from=new TableColumn<>("From");
        to=new TableColumn<>("To");
        data=new TableColumn<>("Date");
        mesaj=new TableColumn<>("Mesaj");

        tableMessages.getColumns().addAll(from,to,mesaj,data);

        from.setCellValueFactory(param -> new ReadOnlyObjectWrapper<String>(param.getValue().getFromAsString()));
        to.setCellValueFactory(param -> new ReadOnlyObjectWrapper<String>(param.getValue().getToAsString_NameVersion()));
        mesaj.setCellValueFactory(new PropertyValueFactory<Message,String>("message"));
        data.setCellValueFactory(new PropertyValueFactory<Message,LocalDateTime>("data"));
        tableMessages.setItems(messages);
    }

    public void initMessages(){
        Iterable<Message> mesaje=service.getAllMessages();
        List<Message> list= StreamSupport.stream(mesaje.spliterator(),false)
                .filter(x->x.getFrom().getId().equals(id_user) || x.idInListTo(id_user))
                .collect(Collectors.toList());
        messages.setAll(list);
    }

    public void initUsers(){
        Iterable<Utilizator> utilizatori=service.getAllUsers();
        List<Utilizator> list= StreamSupport.stream(utilizatori.spliterator(),false)
                .filter(x-> !x.getId().equals(id_user))
                .collect(Collectors.toList());
        users.setAll(list);
    }

    public void initComboBoxUtilizatori(){
        utilizatorComboBox=new ComboBox<>();
        Callback<ListView<Utilizator>, ListCell<Utilizator>> cellFactory=new Callback<ListView<Utilizator>, ListCell<Utilizator>>(){
            @Override
            public ListCell<Utilizator> call(ListView<Utilizator> param) {
                return new ListCell<Utilizator>(){
                    @Override
                    protected void updateItem(Utilizator item,boolean empty){
                        super.updateItem(item,empty);
                        if(item==null || empty){
                            setGraphic(null);
                        }else{
                            setText(item.getFirstName()+" "+item.getLastName());
                        }
                    }
                };
            }
        };
        utilizatorComboBox.setCellFactory(cellFactory);
        utilizatorComboBox.setButtonCell(cellFactory.call(null));
        utilizatorComboBox.setItems(users);
        utilizatorComboBox.setOnAction(this);
    }

    public void handleSelectComboBox(){
        if(!users_to_list.contains(utilizatorComboBox.getValue())) {
            users_to_list.add(utilizatorComboBox.getValue());
            users_to.setAll(users_to_list);
            utilizatorListView.setItems(users_to);
        }
        else{
            MessageAlert.showErrorMessage(null,"Utilizator deja selectat");
            utilizatorListView.getSelectionModel().select(utilizatorComboBox.getValue());
        }
    }

    public void handleRemoveToUser(){
        Utilizator u=utilizatorListView.getSelectionModel().getSelectedItem();
        if(u!=null){
            users_to_list.remove(u);
            users_to.setAll(users_to_list);
            utilizatorListView.setItems(users_to);
        }
        else{
            MessageAlert.showErrorMessage(null,"Trebuie selectat un utilizator");
        }
    }

    public void handleSendNewMessage(){
        String text=mesaj_text_field.getText();
        try{
            Message message=new Message(utilizator,users_to_list,text);
            service.addNewMessage(message);
            initMessages();
            tableMessages.setItems(messages);
            MessageAlert.showMessage(null, Alert.AlertType.INFORMATION, "Trimitere mesaj", "Mesaj trimis");
            mesaj_text_field.setText("");
        }catch (ValidationException e){
            MessageAlert.showErrorMessage(null,e.getMessage());
        }
    }

    public void handleReplyToMessage(){
        Message selected=tableMessages.getSelectionModel().getSelectedItem();
        if(selected!=null){
            try {
                String text = raspuns_text_field.getText();
                List<Utilizator> list=new ArrayList<>();
                list.add(selected.getFrom());
                Message message = new Message(utilizator, list, text, selected);
                service.addNewMessage(message);
                initMessages();
                tableMessages.setItems(messages);
                MessageAlert.showMessage(null, Alert.AlertType.INFORMATION, "Trimitere raspuns", "Raspuns trimis");
                raspuns_text_field.setText("");
            } catch (ValidationException e) {
                MessageAlert.showErrorMessage(null, e.getMessage());
            }
        }
        else{
            MessageAlert.showErrorMessage(null,"Trebuie selectat un mesaj");
        }
    }

    @Override
    public void handle(ActionEvent event) {
        if (event.getSource() == back) {
            stage.setScene(UserController.getScene());
            stage.setTitle(utilizator.getFirstName() + " " + utilizator.getLastName());
            users_to.removeAll();
        }
        if(event.getSource()==utilizatorComboBox){
            handleSelectComboBox();
        }
        if(event.getSource()==removeToUser){
            handleRemoveToUser();
        }
        if(event.getSource()==send){
            handleSendNewMessage();
        }
        if(event.getSource()==reply){
            handleReplyToMessage();
        }
    }
}
