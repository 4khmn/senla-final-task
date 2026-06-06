package com.project.velo.repository;

import com.project.velo.entity.Favorite;
import com.project.velo.entity.FavoriteId;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class FavoriteRepository extends BaseRepository<Favorite, FavoriteId> {

    protected FavoriteRepository() {
        super(Favorite.class);
    }

    public List<Favorite> getAllByUser(String username, int page, int size){
        return entityManager.createQuery("SELECT f FROM Favorite f WHERE f.user.username = :username ORDER BY f.createdAt DESC", Favorite.class)
                .setParameter("username", username)
                .setFirstResult(page * size)
                .setMaxResults(size)
                .getResultList();
    }

    public long countByUser(String username) {
        return entityManager.createQuery("SELECT COUNT(f) FROM Favorite f WHERE f.user.username = :username", Long.class)
                .setParameter("username", username)
                .getSingleResult();
    }

    public boolean existsById(FavoriteId favoriteId) {
        Long count = entityManager.createQuery(
                        "SELECT COUNT(*) FROM Favorite f WHERE f.id = :favoriteId", Long.class)
                .setParameter("favoriteId", favoriteId)
                .getSingleResult();
        return count>0;
    }
}
