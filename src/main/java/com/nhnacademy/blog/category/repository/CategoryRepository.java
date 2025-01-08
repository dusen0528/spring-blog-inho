package com.nhnacademy.blog.category.repository;

import com.nhnacademy.blog.category.domain.Category;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * TODO#7 - CategoryRepository 구현
 */
public interface CategoryRepository extends JpaRepository<Category, Long> {

}
