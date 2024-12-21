package com.ism.core.repository.implement;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.PersistenceException;

import com.ism.core.factory.implement.YamlFactory;
import com.ism.core.repository.IRepository;
import com.ism.core.services.IYamlService;

public abstract class Repository<T> implements IRepository<T> {
    private final Class<T> type;
    private static EntityManagerFactory emf;
    
    protected Repository(Class<T> type) {
        this.type = type;
        initializeEntityManagerFactory();
    }

    private void initializeEntityManagerFactory() {
        if (emf == null || !emf.isOpen()) {
            IYamlService yamlService = YamlFactory.createInstance();
            String persistenceUnit = yamlService.yamlToMap().get("persistence").toString();
            emf = Persistence.createEntityManagerFactory(persistenceUnit);
        }
    }

    protected EntityManager getEntityManager() {
        if (emf == null || !emf.isOpen()) {
            initializeEntityManagerFactory();
        }
        return emf.createEntityManager();
    }

    @Override
    public T insert(T data) {
        EntityManager em = getEntityManager();
        try {
            em.getTransaction().begin();
            em.persist(data);
            em.flush();
            em.getTransaction().commit();
            return data;
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            System.err.println("Échec de l'insertion : " + e.getMessage());
            return null;
        } finally {
            if (em != null && em.isOpen()) {
                em.close();
            }
        }
    }

    @Override
    public List<T> selectAll() {
        EntityManager em = getEntityManager();
        try {
            String query = String.format("SELECT u FROM %s u ORDER BY id ASC", type.getName());
            return em.createQuery(query, type).getResultList();
        } catch (Exception e) {
            System.err.println("Échec de la récupération des données : " + e.getMessage());
            return null;
        } finally {
            if (em != null && em.isOpen()) {
                em.close();
            }
        }
    }

    @Override
    public T selectBy(Long id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(type, id);
        } catch (Exception e) {
            System.err.println("Échec de la récupération par ID : " + e.getMessage());
            return null;
        } finally {
            if (em != null && em.isOpen()) {
                em.close();
            }
        }
    }

    @Override
    public void update(T entity) {
        EntityManager em = getEntityManager();
        try {
            em.getTransaction().begin();
            em.merge(entity);
            em.getTransaction().commit();
        } catch (PersistenceException e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            System.err.println("Échec de la mise à jour : " + e.getMessage());
        } finally {
            if (em != null && em.isOpen()) {
                em.close();
            }
        }
    }

    @Override
    public int size() {
        EntityManager em = getEntityManager();
        try {
            String query = String.format("SELECT COUNT(u) FROM %s u", type.getName());
            return ((Long) em.createQuery(query).getSingleResult()).intValue();
        } catch (Exception e) {
            System.err.println("Échec de la récupération de la taille : " + e.getMessage());
            return 0;
        } finally {
            if (em != null && em.isOpen()) {
                em.close();
            }
        }
    }

    public static void closeEntityManagerFactory() {
        if (emf != null && emf.isOpen()) {
            emf.close();
        }
    }
}