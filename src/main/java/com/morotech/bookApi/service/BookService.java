package com.morotech.bookApi.service;

import com.morotech.bookApi.client.GutendexApiClient;
import com.morotech.bookApi.exception.InvalidPageException;
import com.morotech.bookApi.exception.ResourceNotFoundException;
import com.morotech.bookApi.mapper.GutendexResponseMapper;
import com.morotech.bookApi.model.BookDetailsResponse;
import com.morotech.bookApi.model.BookSearchResponse;
import com.morotech.bookApi.model.dto.GutendexApiResponse;
import com.morotech.bookApi.model.dto.GutendexBook;
import com.morotech.bookApi.model.entity.ReviewEntity;
import com.morotech.bookApi.repository.ReviewRepository;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class BookService {

    private final GutendexApiClient gutendexApiClient;
    private final GutendexResponseMapper gutendexResponseMapper;
    private final ReviewRepository reviewRepository;

    public BookDetailsResponse getBookDetails(Integer bookId) {
        var bookDetails = gutendexApiClient.getBookById(bookId);

        if (bookDetails == null) {
            log.error("Book with ID {} not found", bookId);
            throw new ResourceNotFoundException(String.format("Book with ID: %d ,not found.", bookId));
        }

        var reviewEntityList = reviewRepository.findAllByBookId(bookId);
        var avgRating = reviewEntityList.stream()
                .mapToInt(ReviewEntity::getRating)
                .average()
                .orElse(0.0);
        var reviews = reviewEntityList.stream()
                .map(ReviewEntity::getReview)
                .toList();

        return gutendexResponseMapper.toBookDetailsResponse(bookDetails, reviews, avgRating);
    }

    public BookSearchResponse searchBooks(String title, Integer page, Integer size) {
        if (page < 1) {
            log.error("Invalid page number");
            throw new InvalidPageException("Page number must be greater than or equal to 1.");
        }

        var allByTitleFilter = fetchAllByTitleFilter(title);
        var paginatedContent = paginateList(allByTitleFilter, page, size);

        return gutendexResponseMapper.toBookSearchResponse(paginatedContent, page, size, allByTitleFilter.size());
    }

    private <T> List<T> paginateList(List<T> list, int page, int size) {
        int fromIndex = (page - 1) * size;
        int toIndex = Math.min(fromIndex + size, list.size());

        if (fromIndex >= list.size()) {
            return List.of();
        }

        return list.subList(fromIndex, toIndex);
    }

    private List<GutendexBook> fetchAllByTitleFilter(String titleQuery) {
        String q = titleQuery.toLowerCase();
        List<GutendexBook> filtered = new ArrayList<>();

        int gutendexPage = 1;
        boolean morePages = true;

        while (morePages) {
            var response = gutendexApiClient.searchBooksByTitle(titleQuery, gutendexPage);

            if (response == null || response.getResults().isEmpty()) {
                break;
            }

            // filter only by title
            filtered.addAll(
                    response.getResults().stream()
                            .filter(b -> b.getTitle().toLowerCase().contains(q))
                            .toList()
            );

            if (isFetchComplete(response)) {
                morePages = false;
            } else {
                gutendexPage++;
            }
        }

        return filtered;
    }

    private boolean isFetchComplete(GutendexApiResponse response) {
        return response.getNext() == null || response.getNext().isEmpty();
    }
}
