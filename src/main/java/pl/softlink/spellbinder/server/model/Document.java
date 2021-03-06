package pl.softlink.spellbinder.server.model;

import pl.softlink.spellbinder.global.security.Security;
import pl.softlink.spellbinder.server.JDBC;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.LinkedList;
import java.util.Random;

public class Document extends Model {

    private Integer id = null;
    private String content = "";
    private String name = "";

    public Document(String content, int id, String name) {
        this.content = content;
        this.id = id;
        this.name = name;
    }

    public Document(String name) {
        this.name = name;
    }

    public Integer getId() {
        return id;
    }

    public void save() {

        String sql = "`document` SET";
        sql += " `content`=?";
        sql += ", `name`=?";

        if (isNew()) {
            sql = "INSERT INTO " + sql;
        } else {
            sql = "UPDATE " + sql + " where `id`=?";
        }

        try {
            PreparedStatement preparedStatement = JDBC.getConnection().prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            preparedStatement.setString(1, this.content);
            preparedStatement.setString(2, this.name);

            if (!isNew()) {
                preparedStatement.setInt(3, this.id);
            }

            preparedStatement.executeUpdate();

            if (isNew()) {
                id = JDBC.getGeneratedKey(preparedStatement);
            }

        } catch (SQLException e) {
            throw new RuntimeException("sql error with statement: " + sql, e);
        }
    }

    public static Document findById(int id) {
        String sql = "SELECT * FROM `document` WHERE `id`=? LIMIT 1";

        try {
            PreparedStatement preparedStatement = JDBC.getConnection().prepareStatement(sql);
            preparedStatement.setInt(1, id);
            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next() == false) {
                return null;
            }

            Document document = new Document(resultSet.getString("content"), resultSet.getInt("id"), resultSet.getString("name"));
            return document;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public boolean addUser(User user) {

        String sql = "insert into `user_document` SET `user_id`=?, `document_id`=?";
        try {
            PreparedStatement preparedStatement = JDBC.getConnection().prepareStatement(sql);
            preparedStatement.setInt(1, user.getId());
            preparedStatement.setInt(2, this.id);
            preparedStatement.executeUpdate();
            return true;
        } catch (SQLException e) {
            return false;
        }
    }

    public String getName() {
        return name;
    }

    public static LinkedList<Document> getListForUser(User user) {
        LinkedList<Document> documentList = new LinkedList<>();
        String sql = "SELECT * FROM `document` `d` JOIN `user_document` `ud` ON `ud`.`document_id`=`d`.`id` WHERE `ud`.`user_id`=?";
        try {
            PreparedStatement preparedStatement = JDBC.getConnection().prepareStatement(sql);
            preparedStatement.setInt(1, user.getId());
            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next() != false) {
                Document document = new Document(resultSet.getString("content"), resultSet.getInt("id"), resultSet.getString("name"));
                documentList.add(document);
            }

        } catch (SQLException e) {
            throw new RuntimeException("sql error with statement: " + sql, e);
        }

        return documentList;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Document setName(String name) {
        this.name = name;
        return this;
    }
}
