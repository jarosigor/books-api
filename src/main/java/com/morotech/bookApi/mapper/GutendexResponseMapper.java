package com.morotech.bookApi.mapper;

import com.morotech.bookApi.model.Author;
import com.morotech.bookApi.model.BookDetailsResponse;
import com.morotech.bookApi.model.BookSearchResponse;
import com.morotech.bookApi.model.BookSearchResult;
import com.morotech.bookApi.model.dto.GutendexAuthor;
import com.morotech.bookApi.model.dto.GutendexBook;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
public class GutendexResponseMapper {

    public BookSearchResponse toBookSearchResponse(List<GutendexBook> source, int page, int size, int totalElements) {
        List<BookSearchResult> content = source.stream()
                .map(this::toBookSearchResult)
                .toList();

        int totalPages = (int) Math.ceil(totalElements / (double) size);

        return new BookSearchResponse(content, page, size, totalElements, totalPages);
    }

    private BookSearchResult toBookSearchResult(GutendexBook book) {
        List<Author> authors = book.getAuthors() == null ? List.of()
                : book.getAuthors().stream().map(this::toAuthor).toList();

        return new BookSearchResult(
                book.getId(),
                book.getTitle(),
                authors,
                book.getLanguages(),
                book.getDownloadCount()
        );
    }

    private Author toAuthor(GutendexAuthor author) {
        return new Author()
                .name(author.getName())
                .birthYear(author.getBirthYear())
                .deathYear(author.getDeathYear());
    }

    public BookDetailsResponse toBookDetailsResponse(GutendexBook book, List<String> reviews, double avgRating) {
        List<Author> authors = book.getAuthors() == null ? List.of()
                : book.getAuthors().stream().map(this::toAuthor).toList();

        return new BookDetailsResponse()
                .bookId(book.getId())
                .title(book.getTitle())
                .authors(authors)
                .languages(book.getLanguages())
                .downloadCount(book.getDownloadCount())
                .averageRating(avgRating)
                .reviews(reviews);
    }
}
