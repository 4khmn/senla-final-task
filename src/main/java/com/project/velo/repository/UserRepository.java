package com.project.velo.repository;

import com.project.velo.entity.User;
import com.project.velo.entity.enums.Role;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class UserRepository extends BaseRepository<User, Long>{

    protected UserRepository() {
        super(User.class);
    }

    public Optional<User> findByUsername(String username) {
        return entityManager.createQuery(
                        "SELECT u FROM User u WHERE u.username = :username", User.class)
                .setParameter("username", username)
                .getResultList()
                .stream()
                .findFirst();
    }

    public boolean existsByUsername(String username) {
        Long count = entityManager.createQuery(
                        "SELECT COUNT(*) FROM User u WHERE u.username = :username", Long.class)
                .setParameter("username", username)
                .getSingleResult();
        return count>0;
    }

    public boolean existsByEmail(String email) {
        Long count = entityManager.createQuery(
                        "SELECT COUNT(*) FROM User u WHERE u.email = :email", Long.class)
                .setParameter("email", email)
                .getSingleResult();
        return count>0;
    }

    public List<User> findAllFiltered(Boolean enabled, Role role, int page, int size) {
        StringBuilder hql = new StringBuilder("SELECT u FROM User u JOIN FETCH u.profile WHERE 1=1");

        if (enabled != null) hql.append(" AND u.enabled = :enabled");
        if (role != null) hql.append(" AND u.role = :role");

        hql.append(" ORDER BY u.id DESC");

        var query = entityManager.createQuery(hql.toString(), User.class);

        if (enabled != null) query.setParameter("enabled", enabled);
        if (role != null) query.setParameter("role", role);

        return query.setFirstResult(page * size)
                .setMaxResults(size)
                .getResultList();
    }

    public long countFiltered(Boolean enabled, Role role) {
        StringBuilder hql = new StringBuilder("SELECT COUNT(u) FROM User u WHERE 1=1");

        if (enabled != null) hql.append(" AND u.enabled = :enabled");
        if (role != null) hql.append(" AND u.role = :role");

        var query = entityManager.createQuery(hql.toString(), Long.class);

        if (enabled != null) query.setParameter("enabled", enabled);
        if (role != null) query.setParameter("role", role.name());

        return query.getSingleResult();
    }

    public boolean existsByUsernameAndEnabledTrue(String username) {
        return entityManager.createQuery(
                        "SELECT COUNT(u) > 0 FROM User u WHERE u.username = :username AND u.enabled = true",
                        Boolean.class)
                .setParameter("username", username)
                .getSingleResult();
    }

}
