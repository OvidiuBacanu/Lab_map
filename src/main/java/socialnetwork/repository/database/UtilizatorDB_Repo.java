package socialnetwork.repository.database;

import org.postgresql.util.PSQLException;
import socialnetwork.domain.Prietenie;
import socialnetwork.domain.Tuple;
import socialnetwork.domain.Utilizator;
import socialnetwork.domain.validators.Validator;
import socialnetwork.repository.Repository;

import java.math.BigDecimal;
import java.sql.*;
import java.util.*;

public class UtilizatorDB_Repo implements Repository<Long, Utilizator> {
    private String url;
    private String username;
    private String password;
    private Validator<Utilizator> validator;

    public UtilizatorDB_Repo(String url, String username, String password, Validator<Utilizator> validator) {
        this.url = url;
        this.username = username;
        this.password = password;
        this.validator = validator;
    }

    @Override
    public Utilizator findOne(Long aLong) {
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement("SELECT * from users WHERE id=?"))
            {
                statement.setBigDecimal(1, BigDecimal.valueOf(aLong));
                ResultSet resultSet = statement.executeQuery();

                while (resultSet.next()) {
                    Long id = resultSet.getLong("id");
                    String firstName = resultSet.getString("first_name");
                    String lastName = resultSet.getString("last_name");

                    Utilizator utilizator = new Utilizator(firstName, lastName);
                    utilizator.setId(id);
                    return utilizator;
                }
            }
        catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public Iterable<Utilizator> findAll() {
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

                for(Prietenie p:findAllFriendships()) {
                    if (p.getStatus() != null) {
                        if (p.getStatus().equals("accepted")) {
                            if (p.getId().getRight().equals(id)) {
                                utilizator.addFriend(findOne(p.getId().getLeft()));
                            } else {
                                if ((p.getId().getLeft().equals(id))) {
                                    utilizator.addFriend(findOne(p.getId().getRight()));
                                }
                            }
                        }
                    }
                }
                users.add(utilizator);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return users;
    }

    @Override
    public Utilizator save(Utilizator entity) {
        if (entity==null)
            throw new IllegalArgumentException("entity must be not null");
        validator.validate(entity);
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement("INSERT INTO users(first_name,last_name) VALUES (?,?)"))
        {
            statement.setString(1,entity.getFirstName());
            statement.setString(2,entity.getLastName());
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
    public Utilizator delete(Long aLong) {
        if(aLong==null)
            throw new IllegalArgumentException("id must be not null");
        Utilizator u=findOne(aLong);
        if(u==null)
            return u;
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement("DELETE FROM users WHERE id=?"))
        {
            statement.setBigDecimal(1, BigDecimal.valueOf(aLong));
            try {

                ResultSet resultSet = statement.executeQuery();
                statement.executeUpdate();
            }
            catch (PSQLException e){};
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
        return u;
    }

    @Override
    public Utilizator update(Utilizator entity) {
        return null;
    }

    public Iterable<Prietenie> findAllFriendships() {
        Set<Prietenie> friends = new HashSet<>();
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement("SELECT * from prietenii");
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                Long id1 = resultSet.getLong("id1");
                Long id2 = resultSet.getLong("id2");
                String status=resultSet.getString("status");

                Prietenie p=new Prietenie();
                Tuple x=new Tuple(id1,id2);
                p.setId(x);
                p.setStatus(status);
                friends.add(p);
            }
            return friends;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return friends;
    }

    public Iterable<Utilizator>reloadFriendships(){
        Set<Utilizator> users = new HashSet<>();
        for(Utilizator u:findAll()){
            Long id=u.getId();
            for(Prietenie p:findAllFriendships()){
                if(p.getId().getLeft().equals(id))
                    u.addFriend(findOne(p.getId().getRight()));
                else
                    if(p.getId().getRight().equals(id))
                        u.addFriend(findOne(p.getId().getLeft()));
            }
            users.add(u);
        }
        return users;
    }
}
