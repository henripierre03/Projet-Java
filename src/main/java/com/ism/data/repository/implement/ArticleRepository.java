package com.ism.data.repository.implement;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.ArrayList;
import java.sql.Timestamp;

import com.ism.core.database.IDatabase;
import com.ism.core.repository.implement.Repository;
import com.ism.data.entities.Article;
import com.ism.data.repository.IArticleRepository;

public class ArticleRepository extends Repository<Article> implements IArticleRepository {
    public ArticleRepository(IDatabase database) {
        super(database, Article.class, "articles");
    }

    @Override
    public String generateInsertSQL() {
        return """
                INSERT INTO articles (
                    libelle, prix, qte_stock, created_at, updated_at
                ) VALUES (
                    ?, ?, ?, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
                )
                RETURNING id;
                """;
    }

    @Override
    public String generateUpdateSQL() {
        return """
                UPDATE articles
                SET libelle = ?,
                    prix = ?,
                    qte_stock = ?,
                    updated_at = ?
                WHERE id = ?
                """;
    }

    @Override
    public void update(Article entity) throws SQLException {
        String sql = generateUpdateSQL();
        try {
            database.getConnection();
            database.initPreparedStatement(sql);
            // Setting parameters for the update statement
            database.ps().setString(1, entity.getLibelle());
            database.ps().setDouble(2, entity.getPrix());
            database.ps().setInt(3, entity.getQteStock());
            database.ps().setTimestamp(4, Timestamp.valueOf(entity.getUpdatedAt()));
            database.ps().setLong(5, entity.getId());
            
            database.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            database.closeConnection();
        }
    }

    @Override
    public List<Article> selectAllAvailable() {
        try {
            return selectAll().stream()
                    .filter(article -> article.getQteStock() != 0)
                    .collect(Collectors.toList());
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return Collections.emptyList();
    }

    @Override
    public List<Article> selectAll() throws SQLException {
        List<Article> articles = new ArrayList<>();
        String sql = getSelectAll(tableName);
        try {
            database.getConnection();
            database.initPreparedStatement(sql);
            ResultSet rs = database.ps().executeQuery();
            while (rs.next()) {
                articles.add(this.convertToObject(rs));
            }
            rs.close();
        } finally {
            database.closeConnection();
        }
        return articles;
    }

    @Override
    public boolean insert(Article entity) {
        String sql = generateInsertSQL();
        try {
            database.getConnection();
            database.initPreparedStatement(sql);
            database.ps().setString(1, entity.getLibelle());
            database.ps().setDouble(2, entity.getPrix());
            database.ps().setInt(3, entity.getQteStock());
            
            database.executeUpdate();

            ResultSet rs = database.ps().getGeneratedKeys();
            if (rs.next()) {
                entity.setId(rs.getLong(1));
            }
            rs.close();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public Article selectBy(Long id) throws SQLException {
        Article article = null;
        String sql = getSelectBy(tableName, id);
        try {
            database.getConnection();
            database.initPreparedStatement(sql);
            
            ResultSet rs = database.ps().executeQuery();
            if (rs.next()) {
                article = this.convertToObject(rs);
            }
            rs.close();
        } finally {
            database.closeConnection();
        }
        return article;
    }

    Article convertToObject(ResultSet rs) {
        try {
            return new Article(
                    rs.getLong("id"),
                    rs.getString("libelle"),
                    rs.getDouble("prix"),
                    rs.getInt("qte_stock"),
                    rs.getTimestamp("created_at").toLocalDateTime(),
                    rs.getTimestamp("updated_at").toLocalDateTime());
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
}
