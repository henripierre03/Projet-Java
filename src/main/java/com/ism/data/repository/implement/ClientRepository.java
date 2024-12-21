package com.ism.data.repository.implement;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.stream.Collectors;
import java.util.Collections;
import java.sql.Timestamp;
import java.util.ArrayList;

import com.ism.core.database.IDatabase;
import com.ism.core.repository.implement.Repository;
import com.ism.data.entities.Client;
import com.ism.data.entities.DemandeDette;
import com.ism.data.entities.Detail;
import com.ism.data.entities.Dette;
import com.ism.data.entities.Paiement;
import com.ism.data.entities.User;
import com.ism.data.enums.EtatDemandeDette;
import com.ism.data.enums.EtatDette;
import com.ism.data.repository.IClientRepository;

public class ClientRepository extends Repository<Client> implements IClientRepository {
    private static final String CREATED_AT = "created_at";
    private static final String UPDATED_AT = "updated_at";

    public ClientRepository(IDatabase database) {
        super(database, Client.class, "clients");
    }

    @Override
    public String generateInsertSQL() {
        return """
                    INSERT INTO clients (
                        surname, tel, address, cumul_montant_du, status, user_id, created_at, updated_at
                    ) VALUES (
                        ?, ?, ?, ?, ?, ?, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
                    )
                """;
    }

    @Override
    public String generateUpdateSQL() {
        return """
                    UPDATE clients
                    SET surname = ?,
                        tel = ?,
                        address = ?,
                        cumul_montant_du = ?,
                        status = ?,
                        user_id = ?,
                        updated_at = ?
                    WHERE id = ?
                """;
    }

    @Override
    public List<Client> selectAllActifs() {
        try {
            return selectAll().stream()
                    .filter(Client::isStatus)
                    .collect(Collectors.toList());
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return Collections.emptyList();
    }

    @Override
    public List<Client> selectAllCustomerAvailable() {
        try {
            return selectAll().stream()
                    .filter(cl -> cl.getUser() == null)
                    .collect(Collectors.toList());
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return Collections.emptyList();
    }

    @Override
    public Client selectBy(Long id) throws SQLException {
        Client client = null;
        String sql = getSelectBy(tableName, id);
        try {
            database.getConnection();
            database.initPreparedStatement(sql);
            ResultSet rs = database.ps().executeQuery();
            if (rs.next()) {
                client = this.convertToObject(rs);
            }
            rs.close();
        } finally {
            database.closeConnection();
        }
        return client;
    }

    @Override
    public void update(Client entity) throws SQLException {
        String sql = generateUpdateSQL();
        try {
            database.getConnection();
            database.initPreparedStatement(sql);
            // Setting parameters for the update statement
            database.ps().setString(1, entity.getSurname());
            database.ps().setString(2, entity.getTel());
            database.ps().setString(3, entity.getAddress());
            database.ps().setDouble(4, entity.getCumulMontantDu());
            database.ps().setBoolean(5, entity.isStatus());
            if (entity.getUser() != null) {
                database.ps().setLong(6, entity.getUser().getId());
            } else {
                database.ps().setNull(6, java.sql.Types.INTEGER);
            }
            database.ps().setTimestamp(7, Timestamp.valueOf(entity.getUpdatedAt()));
            database.ps().setLong(8, entity.getId());

            database.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            database.closeConnection();
        }
    }

    @Override
    public boolean insert(Client entity) throws SQLException {
        String sql = generateInsertSQL();
        try {
            database.getConnection();
            database.initPreparedStatement(sql);
            // Setting parameters for the update statement
            database.ps().setString(1, entity.getSurname());
            database.ps().setString(2, entity.getTel());
            database.ps().setString(3, entity.getAddress());
            database.ps().setDouble(4, entity.getCumulMontantDu());
            database.ps().setBoolean(5, entity.isStatus());
            if (entity.getUser() != null) {
                database.ps().setLong(6, entity.getUser().getId());
            } else {
                database.ps().setNull(6, java.sql.Types.INTEGER);
            }

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
    public List<Client> selectAll() throws SQLException {
        List<Client> clients = new ArrayList<>();
        String sql = getSelectAll(tableName);
        try {
            database.getConnection();
            database.initPreparedStatement(sql);
            ResultSet rs = database.ps().executeQuery();
            while (rs.next()) {
                clients.add(this.convertToObject(rs));
            }
            rs.close();
        } finally {
            database.closeConnection();
        }
        return clients;
    }

    private Client convertToObject(ResultSet rs) {
        try {
            // Récupération de user_id, et vérification de sa validité en mode Lazy
            Long userId = rs.getLong("user_id");
            User user = null;
            if (!rs.wasNull()) {
                user = new User();
                user.setId(userId);
            }

            // Création de l'instance de Client
            Client client = new Client(
                    rs.getLong("id"),
                    rs.getTimestamp(CREATED_AT).toLocalDateTime(),
                    rs.getTimestamp(UPDATED_AT).toLocalDateTime(),
                    rs.getString("surname"),
                    rs.getString("tel"),
                    rs.getString("address"),
                    rs.getDouble("cumul_montant_du"),
                    rs.getBoolean("status"),
                    user // User
            );

            // Récupération de la liste de demandeDettes pour ce client
            client.setDemandeDettes(fetchDemandeDettes(client.getId()));

            // Récupération de la liste de dettes pour ce client
            client.setDettes(fetchDettes(client.getId()));

            return client;

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    // Méthode pour récupérer les demandeDettes associées à un client
    private List<DemandeDette> fetchDemandeDettes(Long clientId) {
        List<DemandeDette> demandeDettes = new ArrayList<>();
        String sql = "SELECT * FROM demande_dettes WHERE client_id = ?";
        try {
            database.getConnection();
            database.initPreparedStatement(sql);
            database.ps().setLong(1, clientId);
            ResultSet rs = database.ps().executeQuery();
            while (rs.next()) {
                DemandeDette demandeDette = new DemandeDette();
                demandeDette.setId(rs.getLong("id"));
                demandeDette.setMontantTotal(rs.getDouble("montant_total"));
                demandeDette.setClient(null);
                demandeDette.setDette(null);
                demandeDette.setEtat(EtatDemandeDette.valueOf(rs.getString("etat")));
                demandeDette.setCreatedAt(rs.getTimestamp(CREATED_AT).toLocalDateTime());
                demandeDette.setUpdatedAt(rs.getTimestamp(UPDATED_AT).toLocalDateTime());
                demandeDettes.add(demandeDette);
            }
            rs.close();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                database.closeConnection();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return demandeDettes;
    }

    // Méthode pour récupérer les dettes associées à un client
    private List<Dette> fetchDettes(Long clientId) {
        List<Dette> dettes = new ArrayList<>();
        String sql = "SELECT * FROM dettes WHERE client_id = ?";
        try {
            database.getConnection();
            database.initPreparedStatement(sql);
            database.ps().setLong(1, clientId);
            ResultSet rs = database.ps().executeQuery();
            while (rs.next()) {
                Dette dette = new Dette();
                dette.setId(rs.getLong("id"));
                dette.setClient(null);
                dette.setMontantTotal(rs.getDouble("montant_total"));
                dette.setMontantVerser(rs.getDouble("montant_verser"));
                dette.setStatus(rs.getBoolean("status"));
                dette.setEtat(EtatDette.valueOf(rs.getString("etat"))); // Enum type
                dette.setDemandeDette(null);
                dette.setPaiements(fetchPaiements(dette.getId()));
                dette.setDetails(fetchDetails(dette.getId()));
                dette.setCreatedAt(rs.getTimestamp(CREATED_AT).toLocalDateTime());
                dette.setUpdatedAt(rs.getTimestamp(UPDATED_AT).toLocalDateTime());
                dettes.add(dette);
            }
            rs.close();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                database.closeConnection();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return dettes;
    }

    private List<Paiement> fetchPaiements(Long detteId) {
        List<Paiement> paiements = new ArrayList<>();
        String sql = "SELECT * FROM paiements WHERE dette_id = ?";
        try {
            database.getConnection();
            database.initPreparedStatement(sql);
            database.ps().setLong(1, detteId);
            ResultSet rs = database.ps().executeQuery();
            while (rs.next()) {
                Paiement paiement = new Paiement();
                paiement.setId(rs.getLong("id"));
                paiement.setMontantPaye(rs.getDouble("montant_paye"));
                paiement.setDette(null);
                paiement.setCreatedAt(rs.getTimestamp(CREATED_AT).toLocalDateTime());
                paiement.setUpdatedAt(rs.getTimestamp(UPDATED_AT).toLocalDateTime());
                paiements.add(paiement);
            }
            rs.close();
        } catch (Exception e) {
            e.printStackTrace();
        } 
        finally {
            try {
                database.closeConnection();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return paiements;
    }

    private List<Detail> fetchDetails(Long detteId) {
        List<Detail> details = new ArrayList<>();
        String sql = "SELECT * FROM details WHERE dette_id = ?";
        try {
            database.getConnection();
            database.initPreparedStatement(sql);
            database.ps().setLong(1, detteId);
            ResultSet rs = database.ps().executeQuery();
            while (rs.next()) {
                Detail detail = new Detail();
                detail.setId(rs.getLong("id"));
                detail.setPrixVente(rs.getDouble("prix_vente"));
                detail.setDette(null);
                detail.setQte(rs.getInt("qte"));
                detail.setArticle(new ArticleRepository(database).selectBy(rs.getLong("article_id")));
                detail.setCreatedAt(rs.getTimestamp(CREATED_AT).toLocalDateTime());
                detail.setUpdatedAt(rs.getTimestamp(UPDATED_AT).toLocalDateTime());
                details.add(detail);
            }
            rs.close();
        } catch (Exception e) {
            e.printStackTrace();
        } 
        finally {
            try {
                database.closeConnection();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return details;
    }

}
