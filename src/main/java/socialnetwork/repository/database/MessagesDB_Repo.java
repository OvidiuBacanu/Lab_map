package socialnetwork.repository.database;

import org.postgresql.util.PSQLException;
import socialnetwork.domain.Message;
import socialnetwork.domain.Prietenie;
import socialnetwork.domain.Utilizator;
import socialnetwork.domain.validators.Validator;
import socialnetwork.repository.Repository;

import java.math.BigDecimal;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.*;

public class MessagesDB_Repo implements Repository<Long,Message> {
    private String url;
    private String username;
    private String password;
    private Validator<Message> validator;

    public MessagesDB_Repo(String url, String username, String password, Validator<Message> validator) {
        this.url = url;
        this.username = username;
        this.password = password;
        this.validator = validator;
    }

    @Override
    public Message findOne(Long aLong) {
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement("SELECT * from messages WHERE id=?"))
        {
            statement.setBigDecimal(1, BigDecimal.valueOf(aLong));
            ResultSet resultSet = statement.executeQuery();

            while(resultSet.next()) {
                Long id = resultSet.getLong("id");
                Long from1 = resultSet.getLong("from1");
                String to1 = resultSet.getString("to1");
                String message1 = resultSet.getString("message1");
                Timestamp date1 = resultSet.getTimestamp("date1");
                Long reply = resultSet.getLong("reply");

                Utilizator user_from = new Utilizator();
                List<Utilizator> list_to = new ArrayList<>();
                List<String> list_to_String = Arrays.asList(to1.split(","));

                for (Utilizator u : findAllUsers()) {
                    if (u.getId().equals(from1))
                        user_from = new Utilizator(u);
                    for (String idString : list_to_String) {
                        Long idLong = Long.parseLong(idString);
                        if (u.getId().equals(idLong))
                            list_to.add(u);
                    }
                }

                if (reply == null) {
                    Message message = new Message(user_from, list_to, message1);
                    message.setId(id);
                    return message;

                } else {
                    Message replyMessage = findOne(reply);
                    Message message = new Message(user_from, list_to, message1, replyMessage);
                    message.setId(id);
                    return message;
                }
            }
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public Iterable<Message> findAll() {
        Set<Message> messages = new HashSet<>();
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement("SELECT * from messages");
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                Long id = resultSet.getLong("id");
                Long from1 = resultSet.getLong("from1");
                String to1 = resultSet.getString("to1");
                String message1= resultSet.getString("message1");
                Timestamp date1=resultSet.getTimestamp("date1");
                Long reply=resultSet.getLong("reply");

                Utilizator user_from=new Utilizator();
                List<Utilizator> list_to=new ArrayList<>();
                List<String> list_to_String= Arrays.asList(to1.split(","));


                for(Utilizator u:findAllUsers()) {
                    if (u.getId().equals(from1))
                        user_from = new Utilizator(u);
                    for(String idString:list_to_String) {
                        Long idLong=Long.parseLong(idString);
                        if (u.getId().equals(idLong))
                            list_to.add(u);
                    }
                }

                if(reply==null) {
                    Message message = new Message(user_from, list_to, message1);
                    message.setId(id);
                    messages.add(message);
                }
                else{
                    Message replyMessage=findOne(reply);
                    Message message = new Message(user_from, list_to, message1,replyMessage);
                    message.setId(id);
                    messages.add(message);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return messages;
    }

    @Override
    public Message save(Message entity) {
        if (entity==null)
            throw new IllegalArgumentException("entity must be not null");
        validator.validate(entity);
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement("INSERT INTO messages(from1,to1,message1,date1,reply) VALUES (?,?,?,?,?)"))
        {
            statement.setBigDecimal(1,BigDecimal.valueOf(entity.getFrom().getId()));
            statement.setString(2,String.valueOf(entity.getToAsString()));
            statement.setString(3,String.valueOf(entity.getMessage()));
            statement.setTimestamp(4,Timestamp.valueOf(entity.getData()));
            if(entity.getReply()!=null)
                statement.setBigDecimal(5,BigDecimal.valueOf(entity.getReply().getId()));
            else
                statement.setBigDecimal(5,null);
            try {
                ResultSet resultSet = statement.executeQuery();
                statement.executeUpdate();
            }
            catch (PSQLException e){};
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public Message delete(Long aLong) {
        return null;
    }

    @Override
    public Message update(Message entity) {
        return null;
    }

    public Iterable<Utilizator> findAllUsers() {
        Set<Utilizator> users = new HashSet<>();
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement("SELECT * from users");
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                Long id = resultSet.getLong("id");
                String firstName = resultSet.getString("first_name");
                String lastName = resultSet.getString("last_name");

                Utilizator utilizator = new Utilizator(firstName, lastName);
                utilizator.setId(id);
                users.add(utilizator);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return users;
    }
}
