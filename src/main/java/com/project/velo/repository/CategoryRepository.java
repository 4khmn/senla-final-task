package com.project.velo.repository;

import com.project.velo.entity.Category;
import org.springframework.stereotype.Repository;

@Repository
public class CategoryRepository extends BaseRepository<Category, Long> {

    protected CategoryRepository() {
        super(Category.class);
    }

    public boolean existsByName(String name){
        Long count = entityManager.createQuery(
                        "SELECT COUNT(*) FROM Category c WHERE c.name = :name", Long.class)
                .setParameter("name", name)
                .getSingleResult();
        return count>0;
    }
}
