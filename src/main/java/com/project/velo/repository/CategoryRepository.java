package com.project.velo.repository;

import com.project.velo.entity.Category;
import org.springframework.stereotype.Repository;

@Repository
public class CategoryRepository extends BaseRepository<Category, Long> {

    protected CategoryRepository() {
        super(Category.class);
    }
}
