package dao;

import jakarta.persistence.*;
import Model.User;
import org.mindrot.jbcrypt.BCrypt;

public class UserDao implements IUser {
    private final EntityManagerFactory emf;

    public UserDao(EntityManagerFactory emf) {
        this.emf = emf;
    }

    public User findByEmail(String email) {
        EntityManager em = emf.createEntityManager();
        try {
            return em.createQuery("SELECT u FROM User u WHERE u.email = :email", User.class)
                    .setParameter("email", email)
                    .getSingleResult();
        } catch (NoResultException e) {
            return null;
        } finally {
            em.close();
        }
    }

    public boolean registerUser(String name, String email, String password) {
        EntityManager em = emf.createEntityManager();
        try {
            long count = em.createQuery("SELECT COUNT(u) FROM User u WHERE u.email = :email", Long.class)
                    .setParameter("email", email)
                    .getSingleResult();

            if (count > 0) return false;

            em.getTransaction().begin();
            String hashedPassword = BCrypt.hashpw(password, BCrypt.gensalt());

            User user = new User();
            user.setUsername(name);
            user.setEmail(email);
            user.setPassword(hashedPassword);

            em.persist(user);
            em.getTransaction().commit();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            if (em.getTransaction().isActive()) em.getTransaction().rollback();
            return false;
        } finally {
            em.close();
        }
    }

    public boolean authenticateUser(String email, String password) {
        User user = findByEmail(email);
        return user != null && BCrypt.checkpw(password, user.getPassword());
    }

    public boolean deleteUser(String email) {
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();
            User user = em.createQuery("SELECT u FROM User u WHERE u.email = :email", User.class)
                    .setParameter("email", email)
                    .getSingleResult();
            em.remove(user);
            em.getTransaction().commit();
            return true;
        } catch (NoResultException e) {
            return false;
        } catch (Exception e) {
            e.printStackTrace();
            if (em.getTransaction().isActive()) em.getTransaction().rollback();
            return false;
        } finally {
            em.close();
        }
    }

    public boolean updateUser(User updatedUser) {
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();
            User user = em.find(User.class, updatedUser.getId());
            if (user == null) return false;

            user.setUsername(updatedUser.getUsername());
            user.setEmail(updatedUser.getEmail());

            if (updatedUser.getPassword() != null && !updatedUser.getPassword().isEmpty()) {
                String hashedPassword = BCrypt.hashpw(updatedUser.getPassword(), BCrypt.gensalt());
                user.setPassword(hashedPassword);
            }

            em.merge(user);
            em.getTransaction().commit();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            if (em.getTransaction().isActive()) em.getTransaction().rollback();
            return false;
        } finally {
            em.close();
        }
    }
}
