package com.morotech.bookApi.repository;

import com.morotech.bookApi.model.entity.ReviewEntity;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReviewRepository extends JpaRepository<ReviewEntity, Long> {
    List<ReviewEntity> findAllByBookId(Integer bookId);
}
