package socialnetwork;

import socialnetwork.config.ApplicationContext;
import socialnetwork.domain.Message;
import socialnetwork.domain.Prietenie;
import socialnetwork.domain.Tuple;
import socialnetwork.domain.Utilizator;
import socialnetwork.domain.validators.*;
import socialnetwork.grafuri.Graf;
import socialnetwork.repository.Repository;
import socialnetwork.repository.database.MessagesDB_Repo;
import socialnetwork.repository.database.PrieteniiDB_Repo;
import socialnetwork.repository.database.UtilizatorDB_Repo;
import socialnetwork.repository.file.PrietenieFile;
import socialnetwork.repository.file.UtilizatorFile;
import socialnetwork.service.Service;
import socialnetwork.service.UtilizatorService;
import socialnetwork.ui.UI;
import socialnetwork.ui.UI_Admin;
import socialnetwork.ui.UI_Utilizator;

import java.util.ArrayList;
import java.util.List;

public class MainConsole {
    public static void main(String[] args) {
        //String fileName=ApplicationContext.getPROPERTIES().getProperty("data.socialnetwork.users");
        String fileName="data/users.csv";
        Repository<Long,Utilizator> userFileRepository = new UtilizatorFile(fileName
                , new UtilizatorValidator());

        //userFileRepository.findAll().forEach(System.out::println);

        String fileName2="data/prietenii.csv";
        Repository<Tuple<Long,Long>, Prietenie> friendshipFileRepository=new PrietenieFile(fileName2, new PrietenieValidator());

        /*final String url = ApplicationContext.getPROPERTIES().getProperty("database.socialnetwork.url");
        final String username= ApplicationContext.getPROPERTIES().getProperty("databse.socialnetwork.username");
        final String pasword= ApplicationContext.getPROPERTIES().getProperty("database.socialnetwork.pasword");*/

        final String url = "jdbc:postgresql://localhost:5432/socialnetwork";
        final String username= "postgres";
        final String pasword= "amuitatparola07";

        UtilizatorDB_Repo utilizatorDB_repo=new UtilizatorDB_Repo(url,username,pasword,new UtilizatorValidator());
        PrieteniiDB_Repo prieteniiDB_repo=new PrieteniiDB_Repo(url,username,pasword,new PrietenieValidator());
        MessagesDB_Repo messagesDB_repo=new MessagesDB_Repo(url,username,pasword,new MessageValidator());

        Service service=new Service(utilizatorDB_repo,prieteniiDB_repo,messagesDB_repo,new Graf());
        //Service service=new Service(userFileRepository,friendshipFileRepository,new Graf());
        UI_Admin ui_admin=new UI_Admin(service);
        UI_Utilizator ui_utilizator=new UI_Utilizator(service);
        UI ui=new UI(ui_admin,ui_utilizator);
        ui.run();

       /* Validator<Message> val=new MessageValidator();
        Utilizator u1=service.findUtilizator(1L);
        List<Utilizator> list=new ArrayList<>();
        String mesaj="";
        list.add(u1);

        Message message=new Message(u1,list,mesaj);
        try{
            service.addNewMessage(message);
        }catch (ValidationException e){
            System.out.println(e.getMessage());
        }*/
    }
}


