package socialnetwork.repository.database;

import org.postgresql.util.PSQLException;
import socialnetwork.domain.Prietenie;
import socialnetwork.domain.Tuple;
import socialnetwork.domain.Utilizator;
import socialnetwork.domain.validators.ValidationException;
import socialnetwork.domain.validators.Validator;
import socialnetwork.repository.Repository;

import java.math.BigDecimal;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

public class PrieteniiDB_Repo implements Repository<Tuple<Long,Long>, Prietenie> {
    private String url;
    private String username;
    private String password;
    private Validator<Prietenie> validator;

    public PrieteniiDB_Repo(String url, String username, String password, Validator<Prietenie> validator) {
        this.url = url;
        this.username = username;
        this.password = password;
        this.validator = validator;
    }

    @Override
    public Prietenie findOne(Tuple<Long, Long> tuple) {
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement("SELECT * from prietenii WHERE id1=? and id2=?"))
        {
            statement.setBigDecimal(1, BigDecimal.valueOf(tuple.getLeft()));
            statement.setBigDecimal(2, BigDecimal.valueOf(tuple.getRight()));
            ResultSet resultSet = statement.executeQuery();

            while(resultSet.next()) {
                Long id1 = resultSet.getLong("id1");
                Long id2 = resultSet.getLong("id2");
                String status=resultSet.getString("status");
                Prietenie p = new Prietenie();
                Tuple x = new Tuple(id1, id2);
                p.setStatus(status);
                p.setId(x);
                return p;
            }
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public Iterable<Prietenie> findAll() {
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

    @Override
    public Prietenie save(Prietenie entity) {
        if (entity==null)
            throw new IllegalArgumentException("entity must be not null");
        validator.validate(entity);
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement("INSERT INTO prietenii(id1,id2,status,data_prietenie) VALUES (?,?,?,?)"))
        {

            statement.setBigDecimal(1,BigDecimal.valueOf(entity.getId().getLeft()));
            statement.setBigDecimal(2,BigDecimal.valueOf(entity.getId().getRight()));
            statement.setString(3,String.valueOf(entity.getStatus()));
            statement.setTimestamp(4,Timestamp.valueOf(entity.getDate()));
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
    public Prietenie delete(Tuple<Long, Long> tuple) {
        if(tuple==null)
            throw new IllegalArgumentException("id must be not null");
        Prietenie p=findOne(tuple);
        if(p==null)
           return p;
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement("DELETE FROM prietenii WHERE id1=? and id2=?"))
        {
            statement.setBigDecimal(1,BigDecimal.valueOf(tuple.getLeft()));
            statement.setBigDecimal(2,BigDecimal.valueOf(tuple.getRight()));
            try {
                ResultSet resultSet = statement.executeQuery();
                statement.executeUpdate();
            }catch (PSQLException e){};
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
        return p;
    }


    @Override
    public Prietenie update(Prietenie entity) {
        if(entity==null)
            throw new IllegalArgumentException();
        validator.validate(entity);
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement("UPDATE prietenii SET status=?,data_prietenie=? WHERE id1=? and id2=?"))
        {
            statement.setString(1,String.valueOf(entity.getStatus()));
            statement.setTimestamp(2,Timestamp.valueOf(entity.getDate()));
            statement.setBigDecimal(3,BigDecimal.valueOf(entity.getId().getLeft()));
            statement.setBigDecimal(4,BigDecimal.valueOf(entity.getId().getRight()));
            try {
                ResultSet resultSet = statement.executeQuery();
                statement.executeUpdate();
                return null;
            }catch (PSQLException e){};
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
        return entity;
    }
}
