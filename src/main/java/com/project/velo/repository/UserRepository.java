package com.project.velo.repository;

import com.project.velo.entity.User;
import com.project.velo.entity.enums.Role;
import jakarta.persistence.criteria.*;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
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
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<User> cq = cb.createQuery(User.class);
        Root<User> root = cq.from(User.class);

        root.fetch("profile", JoinType.LEFT);

        cq.where(buildPredicates(cb, root, enabled, role));

        cq.orderBy(cb.desc(root.get("id")));

        return entityManager.createQuery(cq)
                .setFirstResult(page * size)
                .setMaxResults(size)
                .getResultList();
    }

    public long countFiltered(Boolean enabled, Role role) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Long> cq = cb.createQuery(Long.class);
        Root<User> root = cq.from(User.class);
        cq.select(cb.count(root));
        cq.where(buildPredicates(cb, root, enabled, role));

        return entityManager.createQuery(cq).getSingleResult();
    }


    public boolean existsByUsernameAndEnabledTrue(String username) {
        return entityManager.createQuery(
                        "SELECT COUNT(u) > 0 FROM User u WHERE u.username = :username AND u.enabled = true",
                        Boolean.class)
                .setParameter("username", username)
                .getSingleResult();
    }

    private Predicate[] buildPredicates(CriteriaBuilder cb, Root<User> root, Boolean enabled, Role role) {
        List<Predicate> predicates = new ArrayList<>();

        if (enabled != null) {
            predicates.add(cb.equal(root.get("enabled"), enabled));
        }

        if (role != null) {
            predicates.add(cb.equal(root.get("role"), role));
        }

        return predicates.toArray(new Predicate[0]);
    }
}
