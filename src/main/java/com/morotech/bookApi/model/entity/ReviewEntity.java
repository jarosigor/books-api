package com.morotech.bookApi.model.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@NoArgsConstructor
@Table(name = "book_reviews")
public class ReviewEntity {
    @Id
    @Getter(AccessLevel.NONE)
    @Setter(AccessLevel.NONE)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "uuid", nullable = false, unique = true, updatable = false)
    private UUID uuid = UUID.randomUUID();

    @Column(name = "book_id", nullable = false, updatable = false)
    private Integer bookId;

    @Min(1)
    @Max(5)
    @Column(nullable = false)
    private Integer rating;

    @Size(min = 10, max = 1000)
    @Column(nullable = false, length = 2000)
    private String review;

    public ReviewEntity(Integer bookId, Integer rating, String review) {
        this.bookId = bookId;
        this.rating = rating;
        this.review = review;
    }
}
