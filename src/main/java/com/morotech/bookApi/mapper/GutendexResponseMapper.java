package com.morotech.bookApi.mapper;

import com.morotech.bookApi.model.Author;
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

    private BookSearchResult toBookSearchResult(GutendexBook b) {
        List<Author> authors = b.getAuthors() == null ? List.of()
                : b.getAuthors().stream().map(this::toAuthor).toList();

        return new BookSearchResult(
                b.getId(),
                b.getTitle(),
                authors,
                b.getLanguages(),
                b.getDownloadCount()
        );
    }

    private Author toAuthor(GutendexAuthor a) {
        return new Author()
                .name(a.getName())
                .birthYear(a.getBirthYear())
                .deathYear(a.getDeathYear());
    }
}
