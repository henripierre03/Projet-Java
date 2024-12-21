package com.ism.data.repository.implement;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.ArrayList;
import java.sql.Timestamp;

import com.ism.core.database.IDatabase;
import com.ism.core.repository.implement.Repository;
import com.ism.data.entities.Dette;
import com.ism.data.entities.Paiement;
import com.ism.data.repository.IPaiementRepository;

public class PaiementRepository extends Repository<Paiement> implements IPaiementRepository {
    public PaiementRepository(IDatabase database) {
        super(database, Paiement.class, "paiements");
    }

    @Override
    public String generateInsertSQL() {
        return """
                    INSERT INTO paiements (
                        montant_paye, dette_id, created_at, updated_at
                    ) VALUES (
                        ?, ?, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
                    )
                """;
    }

    @Override
    public String generateUpdateSQL() {
        return """
                    UPDATE paiements SET
                        montant_paye = ?,
                        dette_id = ?,
                        updated_at = ?
                    WHERE id = ?
                """;
    }

    @Override
    public Paiement selectBy(Long id) throws SQLException {
        Paiement paiement = null;
        String sql = getSelectBy(tableName, id);
        try {
            database.getConnection();
            database.initPreparedStatement(sql);
            ResultSet rs = database.ps().executeQuery();
            if (rs.next()) {
                paiement = this.convertToObject(rs);
            }
            rs.close();
        } finally {
            database.closeConnection();
        }
        return paiement;
    }

    @Override
    public void update(Paiement entity) throws SQLException {
        String sql = generateUpdateSQL();
        try {
            database.getConnection();
            database.initPreparedStatement(sql);
            // Setting parameters for the update statement
            database.ps().setDouble(1, entity.getMontantPaye());
            database.ps().setLong(2, entity.getDette().getId());
            database.ps().setTimestamp(3, Timestamp.valueOf(entity.getUpdatedAt()));
            database.ps().setLong(4, entity.getId());

            database.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            database.closeConnection();
        }
    }

    @Override
    public boolean insert(Paiement entity) throws SQLException {
        String sql = generateInsertSQL();
        try {
            database.getConnection();
            database.initPreparedStatement(sql);
            // Setting parameters for the update statement
            database.ps().setDouble(1, entity.getMontantPaye());
            database.ps().setLong(2, entity.getDette().getId());

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
    public List<Paiement> selectAll() throws SQLException {
        List<Paiement> paiements = new ArrayList<>();
        String sql = getSelectAll(tableName);
        try {
            database.getConnection();
            database.initPreparedStatement(sql);
            ResultSet rs = database.ps().executeQuery();
            while (rs.next()) {
                paiements.add(this.convertToObject(rs));
            }
            rs.close();
        } finally {
            database.closeConnection();
        }
        return paiements;
    }

    private Paiement convertToObject(ResultSet rs) {
        try {
            Dette dette = null;
            Long detteId = rs.getLong("dette_id");
            if (!rs.wasNull()) {
                dette = new DetteRepository(database).selectBy(detteId);
            }
            return new Paiement(
                    rs.getLong("id"),
                    rs.getTimestamp("created_at").toLocalDateTime(),
                    rs.getTimestamp("updated_at").toLocalDateTime(),
                    rs.getDouble("montant_paye"),
                    dette);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
