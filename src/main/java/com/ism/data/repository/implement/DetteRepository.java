package com.ism.data.repository.implement;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.ArrayList;
import java.util.stream.Collectors;
import java.util.Collections;
import java.sql.Timestamp;
import java.sql.Types;

import com.ism.core.database.IDatabase;
import com.ism.core.repository.implement.Repository;
import com.ism.data.entities.*;
import com.ism.data.enums.EtatDette;
import com.ism.data.repository.IDetteRepository;

public class DetteRepository extends Repository<Dette> implements IDetteRepository {
    private static final String CREATED_AT = "created_at";
    private static final String UPDATED_AT = "updated_at";

    public DetteRepository(IDatabase database) {
        super(database, Dette.class, "dettes");
    }

    @Override
    public String generateInsertSQL() {
        return """
                INSERT INTO dettes (
                    montant_total, montant_verser, status, etat, client_id, demande_dette_id, created_at, updated_at
                ) VALUES (
                    ?, ?, ?, ?, ?, ?, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
                )
                """;
    }

    @Override
    public String generateUpdateSQL() {
        return """
                UPDATE dettes
                SET montant_total = ?,
                    montant_verser = ?,
                    status = ?,
                    etat = ?,
                    client_id = ?,
                    demande_dette_id = ?,
                    updated_at = ?
                WHERE id = ?
                """;
    }

    @Override
    public List<Dette> selectAllSoldes() {
        try {
            database.getConnection();
            return selectAll().stream()
                    .filter(dette -> dette.getMontantRestant() == 0)
                    .collect(Collectors.toList());
        } catch (SQLException e) {
            e.printStackTrace();
            return Collections.emptyList();
        } finally {
            try {
                database.closeConnection();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public List<Dette> selectAllNonSoldes() {
        try {
            database.getConnection();
            return selectAll().stream()
                    .filter(dette -> (dette.getClient() != null && dette.getClient().getCumulMontantDu() != 0.0))
                    .collect(Collectors.toList());
        } catch (SQLException e) {
            e.printStackTrace();
            return Collections.emptyList();
        } finally {
            try {
                database.closeConnection();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public Dette selectBy(Long id) throws SQLException {
        Dette dette = null;
        String sql = getSelectBy(tableName, id);
        
        try {
            database.getConnection();
            database.initPreparedStatement(sql);

            ResultSet rs = database.ps().executeQuery();
            if (rs.next()) {
                dette = convertToObject(rs);
            }
        } finally {
            database.closeConnection();
        }
        return dette;
    }

    @Override
    public void update(Dette entity) throws SQLException {
        String sql = generateUpdateSQL();
        
        try {
            database.getConnection();
            database.initPreparedStatement(sql);
            
            database.ps().setDouble(1, entity.getMontantTotal());
            database.ps().setDouble(2, entity.getMontantVerser());
            database.ps().setBoolean(3, entity.isStatus());
            database.ps().setString(4, entity.getEtat().name());
            database.ps().setLong(5, entity.getClient().getId());
            
            if (entity.getDemandeDette() != null) {
                database.ps().setLong(6, entity.getDemandeDette().getId());
            } else {
                database.ps().setNull(6, Types.INTEGER);
            }
            
            database.ps().setTimestamp(7, Timestamp.valueOf(entity.getUpdatedAt()));
            database.ps().setLong(8, entity.getId());

            database.ps().executeUpdate();
        } finally {
            database.closeConnection();
        }
    }

    @Override
    public boolean insert(Dette entity) throws SQLException {
        String sql = generateInsertSQL();
        
        try {
            database.getConnection();
            database.initPreparedStatement(sql);
            
            database.ps().setDouble(1, entity.getMontantTotal());
            database.ps().setDouble(2, entity.getMontantVerser());
            database.ps().setBoolean(3, entity.isStatus());
            database.ps().setString(4, entity.getEtat().name());
            database.ps().setLong(5, entity.getClient().getId());
            
            if (entity.getDemandeDette() != null) {
                database.ps().setLong(6, entity.getDemandeDette().getId());
            } else {
                database.ps().setNull(6, Types.INTEGER);
            }

            int affectedRows = database.ps().executeUpdate();
            if (affectedRows == 0) {
                return false;
            }

            ResultSet rs = database.ps().getGeneratedKeys();
            if (rs.next()) {
                entity.setId(rs.getLong(1));
                return true;
            }
            return false;
        } finally {
            database.closeConnection();
        }
    }

    @Override
    public List<Dette> selectAll() throws SQLException {
        List<Dette> dettes = new ArrayList<>();
        String sql = getSelectAll(tableName);
        
        try {
            database.getConnection();
            database.initPreparedStatement(sql);
            ResultSet rs = database.ps().executeQuery();
            while (rs.next()) {
                dettes.add(convertToObject(rs));
            }
        } finally {
            database.closeConnection();
        }
        return dettes;
    }

    private Dette convertToObject(ResultSet rs) throws SQLException {
        Client client = null;
        DemandeDette demandeDette = null;
        
        Long clientId = rs.getLong("client_id");
        if (!rs.wasNull()) {
            ClientRepository clientRepo = new ClientRepository(database);
            client = clientRepo.selectBy(clientId);
        }

        Long demandeDetteId = rs.getLong("demande_dette_id");
        if (!rs.wasNull()) {
            demandeDette = new DemandeDette();
            demandeDette.setId(demandeDetteId);
        }

        Dette dette = new Dette(
            rs.getLong("id"),
            rs.getTimestamp(CREATED_AT).toLocalDateTime(),
            rs.getTimestamp(UPDATED_AT).toLocalDateTime(),
            rs.getDouble("montant_total"),
            rs.getDouble("montant_verser"),
            rs.getBoolean("status"),
            EtatDette.valueOf(rs.getString("etat")),
            client,
            demandeDette
        );

        dette.setPaiements(fetchPaiements("dette_id", dette.getId()));
        dette.setDetails(fetchDetails("dette_id", dette.getId()));

        return dette;
    }

    private List<Paiement> fetchPaiements(String key, Long id) {
        List<Paiement> paiements = new ArrayList<>();
        String sql = String.format("SELECT * FROM paiements WHERE %s = ?", key);
        
        try {
            database.getConnection();
            database.initPreparedStatement(sql);
            database.ps().setLong(1, id);
            database.ps().executeQuery();
            ResultSet rs = database.ps().getGeneratedKeys();
            while (rs.next()) {
                Paiement paiement = new Paiement();
                paiement.setId(rs.getLong("id"));
                paiement.setMontantPaye(rs.getDouble("montant_paye"));
                paiement.setDette(null);
                paiement.setCreatedAt(rs.getTimestamp(CREATED_AT).toLocalDateTime());
                paiement.setUpdatedAt(rs.getTimestamp(UPDATED_AT).toLocalDateTime());
                paiements.add(paiement);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                database.closeConnection();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return paiements;
    }

    private List<Detail> fetchDetails(String key, Long id) {
        List<Detail> details = new ArrayList<>();
        String sql = String.format("SELECT * FROM details WHERE %s = ?", key);
        
        try {
            database.getConnection();
            database.initPreparedStatement(sql);
            database.ps().setLong(1, id);
            database.ps().executeQuery();
            ResultSet rs = database.ps().getGeneratedKeys();
            while (rs.next()) {
                Detail detail = new Detail();
                detail.setId(rs.getLong("id"));
                detail.setPrixVente(rs.getDouble("prix_vente"));
                detail.setDette(null);
                detail.setQte(rs.getInt("qte"));
                
                ArticleRepository articleRepo = new ArticleRepository(database);
                detail.setArticle(articleRepo.selectBy(rs.getLong("article_id")));
                
                detail.setCreatedAt(rs.getTimestamp(CREATED_AT).toLocalDateTime());
                detail.setUpdatedAt(rs.getTimestamp(UPDATED_AT).toLocalDateTime());
                details.add(detail);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                database.closeConnection();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return details;
    }
}