package com.morotech.bookApi.service;

import com.morotech.bookApi.client.GutendexApiClient;
import com.morotech.bookApi.exception.InvalidPageException;
import com.morotech.bookApi.exception.ResourceNotFoundException;
import com.morotech.bookApi.mapper.GutendexResponseMapper;
import com.morotech.bookApi.model.dto.GutendexApiResponse;
import com.morotech.bookApi.model.dto.GutendexAuthor;
import com.morotech.bookApi.model.dto.GutendexBook;
import com.morotech.bookApi.model.entity.ReviewEntity;
import com.morotech.bookApi.repository.ReviewRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;

import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BookServiceTest {

    @Mock
    GutendexApiClient api;

    GutendexResponseMapper mapper = new GutendexResponseMapper();

    @Mock
    ReviewRepository reviewRepository;

    BookService service;

    // Test data
    List<GutendexBook> mockTestBooks;
    GutendexBook mockBook1;
    GutendexBook mockBook2;
    GutendexBook mockBook3;
    GutendexApiResponse mockApiResponse1;
    GutendexApiResponse mockApiResponse2;
    GutendexAuthor mockAuthor1;
    GutendexAuthor mockAuthor2;

    @BeforeEach
    void setup() {
        this.service = new BookService(api, mapper, reviewRepository);

        mockAuthor1 = new GutendexAuthor();
        mockAuthor1.setName("Charles Dickens");
        mockAuthor2 = new GutendexAuthor();
        mockAuthor2.setName("John Smith");

        mockBook1 = new GutendexBook();
        mockBook1.setId(1);
        mockBook1.setAuthors(List.of(mockAuthor1));
        mockBook1.setLanguages(List.of("English"));
        mockBook1.setDownloadCount(2);
        mockBook1.setTitle("Oliver Twist");

        mockBook2 = new GutendexBook();
        mockBook2.setId(2);
        mockBook2.setAuthors(List.of(mockAuthor2));
        mockBook2.setLanguages(List.of("English"));
        mockBook2.setDownloadCount(5);
        mockBook2.setTitle("Oliver Cromwell Biography");

        mockBook3 = new GutendexBook();
        mockBook3.setId(3);
        mockBook3.setAuthors(List.of(mockAuthor1, mockAuthor2));
        mockBook3.setLanguages(List.of("English", "French"));
        mockBook3.setDownloadCount(8);
        mockBook3.setTitle("Great Expectations");

        mockTestBooks = List.of(mockBook1, mockBook2, mockBook3);

        mockApiResponse1 = new GutendexApiResponse();
        mockApiResponse1.setResults(mockTestBooks);
        mockApiResponse1.setCount(3);
        mockApiResponse1.setNext("");
        mockApiResponse2 = new GutendexApiResponse();
        mockApiResponse2.setResults(List.of(mockBook2));
        mockApiResponse2.setCount(1);
        mockApiResponse2.setNext("");
    }

    @Test
    @DisplayName("Test getBookDetails - success scenario")
    void getBookDetails_success() {
        var bookId = mockBook3.getId();
        var reviews = List.of(
                new ReviewEntity(bookId, 5, "Nice one"),
                new ReviewEntity(bookId, 3, "Average read")
        );

        when(api.getBookById(bookId)).thenReturn(mockBook3);
        when(reviewRepository.findAllByBookId(bookId)).thenReturn(reviews);

        var result = service.getBookDetails(bookId);

        assertThat(result).isNotNull();
        assertThat(result.getBookId()).isEqualTo(bookId);
        assertThat(result.getTitle()).isEqualTo("Great Expectations");
        assertThat(result.getReviews()).isEqualTo(reviews.stream().map(ReviewEntity::getReview).toList());
        assertThat(result.getAverageRating()).isEqualTo(4.0);
    }

    @Test
    @DisplayName("Test getBookDetails - book not found scenario")
    void getBookDetails_notFound() {
        when(api.getBookById(99)).thenReturn(null);

        assertThatThrownBy(() -> service.getBookDetails(99))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    @DisplayName("Test searchBooks - invalid page number")
    void searchBooks_invalidPage() {
        assertThatThrownBy(() -> service.searchBooks("oliver", 0, 10))
                .isInstanceOf(InvalidPageException.class);
    }

    @Test
    @DisplayName("Test searchBooks - single page filter works")
    void searchBooks_singlePageFilterWorks() {
        when(api.searchBooksByTitle("oliver", 1)).thenReturn(mockApiResponse1);

        var result = service.searchBooks("oliver", 1, 1);

        assertThat(result).isNotNull();
        assertThat(result.getTotalPages()).isEqualTo(2);
        assertThat(result.getContent().size()).isEqualTo(1);
        assertThat(result.getContent().getFirst().getTitle()).isEqualTo("Oliver Twist");
    }

}
