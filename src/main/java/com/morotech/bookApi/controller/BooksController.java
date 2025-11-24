package com.morotech.bookApi.controller;


import com.morotech.bookApi.api.BooksApi;
import com.morotech.bookApi.model.BookDetails;
import com.morotech.bookApi.model.BookSearchResponse;
import com.morotech.bookApi.service.BooksService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class BooksController implements BooksApi {

    private final BooksService booksService;

    @Override
    public ResponseEntity<BookDetails> getBookDetails(Integer bookId) {
        return ResponseEntity.ok(booksService.getBookDetails(bookId));
    }

    @Override
    public ResponseEntity<BookSearchResponse> searchBooks(String title, Integer page, Integer size) {
        return ResponseEntity.ok(booksService.searchBooks(title, page, size));
    }
}
