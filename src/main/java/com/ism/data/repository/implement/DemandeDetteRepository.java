package com.ism.data.repository.implement;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.ArrayList;
import java.sql.Timestamp;
import java.sql.Types;

import com.ism.core.database.IDatabase;
import com.ism.core.repository.implement.Repository;
import com.ism.data.entities.Client;
import com.ism.data.entities.DemandeArticle;
import com.ism.data.entities.DemandeDette;
import com.ism.data.entities.Dette;
import com.ism.data.enums.EtatDemandeDette;
import com.ism.data.repository.IDemandeDetteRepository;

public class DemandeDetteRepository extends Repository<DemandeDette> implements IDemandeDetteRepository {
    private static final String CREATED_AT = "created_at";
    private static final String UPDATED_AT = "updated_at";

    public DemandeDetteRepository(IDatabase database) {
        super(database, DemandeDette.class, "demande_dettes");
    }

    @Override
    public String generateInsertSQL() {
        return """
                    INSERT INTO demande_dettes (
                        montant_total, etat, dette_id, client_id, created_at, updated_at
                    ) VALUES (
                        ?, ?, ?, ?, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
                    )
                """;
    }

    @Override
    public String generateUpdateSQL() {
        return """
                    UPDATE demande_dettes
                    SET montant_total = ?,
                        etat = ?,
                        dette_id = ?,
                        client_id = ?,
                        updated_at = ?
                    WHERE id = ?
                """;
    }

    @Override
    public DemandeDette selectBy(Long id) throws SQLException {
        DemandeDette demandeDette = null;
        String sql = getSelectBy(tableName, id);
        try {
            database.getConnection();
            database.initPreparedStatement(sql);
            ResultSet rs = database.ps().executeQuery();
            if (rs.next()) {
                demandeDette = this.convertToObject(rs, false);
                // Load related entities only after basic object is created
                if (demandeDette != null) {
                    demandeDette.setDemandeArticles(fetchDemandeArticles(id));
                }
            }
            rs.close();
        } finally {
            database.closeConnection();
        }
        return demandeDette;
    }
    @Override
    public void update(DemandeDette entity) throws SQLException {
        String sql = generateUpdateSQL();
        try {
            database.getConnection();
            database.initPreparedStatement(sql);
            // Setting parameters for the update statement
            database.ps().setDouble(1, entity.getMontantTotal());
            database.ps().setString(2, entity.getEtat().name());
            if (entity.getDette() != null) {
                database.ps().setLong(3, entity.getDette().getId());
            } else {
                database.ps().setNull(3, Types.INTEGER);
            }
            database.ps().setLong(4, entity.getClient().getId());
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
    public boolean insert(DemandeDette entity) throws SQLException {
        String sql = generateInsertSQL();
        try {
            database.getConnection();
            database.initPreparedStatement(sql);
            // Setting parameters for the update statement
            database.ps().setDouble(1, entity.getMontantTotal());
            database.ps().setString(2, entity.getEtat().name());
            if (entity.getDette() != null) {
                database.ps().setLong(3, entity.getDette().getId());
            } else {
                database.ps().setNull(3, Types.INTEGER);
            }
            database.ps().setLong(4, entity.getClient().getId());

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
    public List<DemandeDette> selectAll() throws SQLException {
        List<DemandeDette> demandeDettes = new ArrayList<>();
        String sql = getSelectAll(tableName);
        try {
            database.getConnection();
            database.initPreparedStatement(sql);
            ResultSet rs = database.ps().executeQuery();
            while (rs.next()) {
                DemandeDette demandeDette = this.convertToObject(rs, true);
                if (demandeDette != null) {
                    demandeDette.setDemandeArticles(fetchDemandeArticles(demandeDette.getId()));
                    demandeDettes.add(demandeDette);
                }
            }
            rs.close();
        } finally {
            database.closeConnection();
        }
        return demandeDettes;
    }

    private DemandeDette convertToObject(ResultSet rs, boolean loadRelated) throws SQLException {
        try {
            Long detteId = rs.getLong("dette_id");
            Dette dette = null;
            if (!rs.wasNull()) {
                dette = new Dette();
                dette.setId(detteId);
            }

            Long clientId = rs.getLong("client_id");
            Client client = null;
            if (!rs.wasNull()) {
                if (loadRelated) {
                    client = new ClientRepository(database).selectBy(clientId);
                } else {
                    client = new Client();
                    client.setId(clientId);
                }
            }
            return new DemandeDette(
                rs.getLong("id"),
                rs.getTimestamp(CREATED_AT).toLocalDateTime(),
                rs.getTimestamp(UPDATED_AT).toLocalDateTime(),
                rs.getDouble("montant_total"),
                EtatDemandeDette.valueOf(rs.getString("etat")),
                dette,
                client
            );
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    private List<DemandeArticle> fetchDemandeArticles(Long demandeDetteId) throws SQLException {
        List<DemandeArticle> demandeArticles = new ArrayList<>();
        String sql = "SELECT da.*, a.* FROM demande_articles da LEFT JOIN articles a ON da.article_id = a.id WHERE da.demande_dette_id = ?";
        
        try {
            database.getConnection();
            database.initPreparedStatement(sql);
            database.ps().setLong(1, demandeDetteId);
            ResultSet rs = database.ps().executeQuery();
            
            while (rs.next()) {
                DemandeArticle demandeArticle = new DemandeArticle();
                demandeArticle.setId(rs.getLong("id"));
                demandeArticle.setQteArticle(rs.getInt("qte_article"));
                demandeArticle.setCreatedAt(rs.getTimestamp(CREATED_AT).toLocalDateTime());
                demandeArticle.setUpdatedAt(rs.getTimestamp(UPDATED_AT).toLocalDateTime());
                
                // Set the article directly from the join result
                demandeArticle.setArticle(new ArticleRepository(database).convertToObject(rs));
                
                // Set a lightweight reference to parent DemandeDette
                DemandeDette parentDemandeDette = new DemandeDette();
                parentDemandeDette.setId(demandeDetteId);
                demandeArticle.setDemandeDette(parentDemandeDette);
                
                demandeArticles.add(demandeArticle);
            }
            rs.close();
        } finally {
            database.closeConnection();
        }
        return demandeArticles;
    }
}
