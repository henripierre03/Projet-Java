// Méthode Eager Load pour tous récupérés
Long clientId = rs.getLong("client_id");
Long demandeDetteId = rs.getLong("demande_dette_id");
Client client = null;
DemandeDette demandeDette = null;
if (!rs.wasNull()) {
client = new ClientRepository(database).selectBy(clientId);
demandeDette = new DemandeDetteRepository(database).selectBy(demandeDetteId);
}
