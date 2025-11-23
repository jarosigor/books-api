package com.morotech.bookApi.controller;


import com.morotech.bookApi.api.BooksApi;
import com.morotech.bookApi.model.BookDetails;
import com.morotech.bookApi.model.BookSearchResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
public class BooksController implements BooksApi {

    @Override
    public ResponseEntity<BookDetails> getBookDetails(Integer bookId) {
        return null;
    }

    @Override
    public ResponseEntity<BookSearchResponse> searchBooks(String title, Integer page, Integer size) {
        return null;
    }
}
