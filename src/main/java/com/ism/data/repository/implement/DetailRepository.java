package com.ism.data.repository.implement;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.ArrayList;
import java.sql.Timestamp;

import com.ism.core.database.IDatabase;
import com.ism.core.repository.implement.Repository;
import com.ism.data.entities.Article;
import com.ism.data.entities.Detail;
import com.ism.data.entities.Dette;
import com.ism.data.repository.IDetailRepository;

public class DetailRepository extends Repository<Detail> implements IDetailRepository {
    public DetailRepository(IDatabase database) {
        super(database, Detail.class, "details");
    }

    @Override
    public String generateInsertSQL() {
        return """
                    INSERT INTO details (
                        qte, prix_vente, article_id, dette_id, created_at, updated_at
                    ) VALUES (
                        ?, ?, ?, ?, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
                    )
                """;
    }

    @Override
    public String generateUpdateSQL() {
        return """
                    UPDATE details
                    SET qte = ?,
                        prix_vente = ?,
                        article_id = ?,
                        dette_id = ?,
                        updated_at = ?
                    WHERE id = ?
                """;
    }

    @Override
    public Detail selectBy(Long id) throws SQLException {
        Detail detail = null;
        String sql = getSelectBy(tableName, id);
        try {
            database.getConnection();
            database.initPreparedStatement(sql);
            ResultSet rs = database.ps().executeQuery();
            if (rs.next()) {
                detail = this.convertToObject(rs);
            }
            rs.close();
        } finally {
            database.closeConnection();
        }
        return detail;
    }

    @Override
    public void update(Detail entity) throws SQLException {
        String sql = generateUpdateSQL();
        try {
            database.getConnection();
            database.initPreparedStatement(sql);
            // Setting parameters for the update statement
            database.ps().setInt(1, entity.getQte());
            database.ps().setDouble(2, entity.getPrixVente());
            database.ps().setLong(3, entity.getArticle().getId());
            database.ps().setLong(4, entity.getDette().getId());
            database.ps().setTimestamp(5, Timestamp.valueOf(entity.getUpdatedAt()));
            database.ps().setLong(6, entity.getId());

            database.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            database.closeConnection();
        }
    }

    @Override
    public boolean insert(Detail entity) throws SQLException {
        String sql = generateInsertSQL();
        try {
            database.getConnection();
            database.initPreparedStatement(sql);
            // Setting parameters for the update statement
            database.ps().setInt(1, entity.getQte());
            database.ps().setDouble(2, entity.getPrixVente());
            database.ps().setLong(3, entity.getArticle().getId());
            database.ps().setLong(4, entity.getDette().getId());

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
    public List<Detail> selectAll() throws SQLException {
        List<Detail> details = new ArrayList<>();
        String sql = getSelectAll(tableName);
        try {
            database.getConnection();
            database.initPreparedStatement(sql);
            ResultSet rs = database.ps().executeQuery();
            while (rs.next()) {
                details.add(this.convertToObject(rs));
            }
            rs.close();
        } finally {
            database.closeConnection();
        }
        return details;
    }

    private Detail convertToObject(ResultSet rs) {
        try {
            // Récupération de user_id, et vérification de sa validité en mode Lazy
            Article article = null;
            Dette dette = null;
            Long articleId = rs.getLong("article_id");
            if (!rs.wasNull()) {
                article = new Article();
                article.setId(articleId);
            }
            Long demandeDetteId = rs.getLong("demande_dette_id");
            if (!rs.wasNull()) {
                dette = new Dette();
                dette.setId(demandeDetteId);
            }
            return new Detail(
                    rs.getLong("id"),
                    rs.getTimestamp("created_at").toLocalDateTime(),
                    rs.getTimestamp("updated_at").toLocalDateTime(),
                    rs.getInt("qte"),
                    rs.getDouble("prix_vente"),
                    article,
                    dette);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
}
