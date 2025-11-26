package com.morotech.bookApi.controller;


import com.morotech.bookApi.api.BooksApi;
import com.morotech.bookApi.model.BookDetailsResponse;
import com.morotech.bookApi.model.BookSearchResponse;
import com.morotech.bookApi.service.BookService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class BookController implements BooksApi {

    private final BookService bookService;

    @Override
    public ResponseEntity<BookDetailsResponse> getBookDetails(Integer bookId) {
        return ResponseEntity.ok(bookService.getBookDetails(bookId));
    }

    @Override
    public ResponseEntity<BookSearchResponse> searchBooks(String title, Integer page, Integer size) {
        return ResponseEntity.ok(bookService.searchBooks(title, page, size));
    }
}
