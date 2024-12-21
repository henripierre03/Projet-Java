package com.ism.data.repository.implement;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.ArrayList;
import java.sql.Timestamp;

import com.ism.core.database.IDatabase;
import com.ism.core.repository.implement.Repository;
import com.ism.data.entities.Article;
import com.ism.data.entities.DemandeArticle;
import com.ism.data.entities.DemandeDette;
import com.ism.data.repository.IDemandeArticleRepository;

public class DemandeArticleRepository extends Repository<DemandeArticle> implements IDemandeArticleRepository {
    public DemandeArticleRepository(IDatabase database) {
        super(database, DemandeArticle.class, "demande_articles");
    }

    @Override
    public String generateInsertSQL() {
        return """
                    INSERT INTO demande_articles (
                        qte_article, article_id, demande_dette_id, created_at, updated_at
                    ) VALUES (
                        ?, ?, ?, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
                    )
                """;
    }

    @Override
    public String generateUpdateSQL() {
        return """
                    UPDATE demande_articles
                    SET qte_article = ?,
                        article_id = ?,
                        demande_dette_id = ?,
                        updated_at = ?
                    WHERE id = ?
                """;
    }

    @Override
    public DemandeArticle selectBy(Long id) throws SQLException {
        DemandeArticle demandeArticle = null;
        String sql = getSelectBy(tableName, id);
        try {
            database.getConnection();
            database.initPreparedStatement(sql);
            ResultSet rs = database.ps().executeQuery();
            if (rs.next()) {
                demandeArticle = this.convertToObject(rs);
            }
            rs.close();
        } finally {
            database.closeConnection();
        }
        return demandeArticle;
    }

    @Override
    public void update(DemandeArticle entity) throws SQLException {
        String sql = generateUpdateSQL();
        try {
            database.getConnection();
            database.initPreparedStatement(sql);
            // Setting parameters for the update statement
            database.ps().setInt(1, entity.getQteArticle());
            database.ps().setLong(2, entity.getArticle().getId());
            database.ps().setLong(3, entity.getDemandeDette().getId());
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
    public boolean insert(DemandeArticle entity) throws SQLException {
        String sql = generateInsertSQL();
        try {
            database.getConnection();
            database.initPreparedStatement(sql);
            // Setting parameters for the update statement
            database.ps().setInt(1, entity.getQteArticle());
            database.ps().setLong(2, entity.getArticle().getId());
            database.ps().setLong(3, entity.getDemandeDette().getId());

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
    public List<DemandeArticle> selectAll() throws SQLException {
        List<DemandeArticle> demandeArticles = new ArrayList<>();
        String sql = getSelectAll(tableName);
        try {
            database.getConnection();
            database.initPreparedStatement(sql);
            ResultSet rs = database.ps().executeQuery();
            while (rs.next()) {
                demandeArticles.add(this.convertToObject(rs));
            }
            rs.close();
        } finally {
            database.closeConnection();
        }
        return demandeArticles;
    }

    private DemandeArticle convertToObject(ResultSet rs) {
        try {
            // Récupération de article_id, demande_dette_id, et vérification de leurs validités en mode Lazy
            Article article = null;
            DemandeDette demandeDette = null;
            Long articleId = rs.getLong("article_id");
            if (!rs.wasNull()) {
                article = new Article();
                article.setId(articleId);
            }
            Long demandeDetteId = rs.getLong("demande_dette_id");
            if (!rs.wasNull()) {
                demandeDette = new DemandeDette();
                demandeDette.setId(demandeDetteId);
            }
            return new DemandeArticle(
                    rs.getLong("id"),
                    rs.getTimestamp("created_at").toLocalDateTime(),
                    rs.getTimestamp("updated_at").toLocalDateTime(),
                    rs.getInt("qte_article"),
                    article,
                    demandeDette);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
}
