package com.morotech.bookApi.service;

import com.morotech.bookApi.client.GutendexApiClient;
import com.morotech.bookApi.exception.InvalidPageException;
import com.morotech.bookApi.mapper.GutendexResponseMapper;
import com.morotech.bookApi.model.BookDetails;
import com.morotech.bookApi.model.BookSearchResponse;
import com.morotech.bookApi.model.dto.GutendexApiResponse;
import com.morotech.bookApi.model.dto.GutendexBook;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class BooksService {

    private final GutendexApiClient gutendexApiClient;
    private final GutendexResponseMapper gutendexResponseMapper;

    public BookDetails getBookDetails(Integer bookId) {
        return null;
    }

    public BookSearchResponse searchBooks(String title, Integer page, Integer size) {
        if (page < 1) {
            throw new InvalidPageException("Page number must be greater than or equal to 1.");
        }

        var allByTitleFilter = fetchAllByTitleFilter(title);
        int totalElements = allByTitleFilter.size();
        int fromIndex = (page - 1) * size;
        int toIndex = Math.min(fromIndex + size, totalElements);

        List<GutendexBook> content = fromIndex >= totalElements ? List.of() : allByTitleFilter.subList(fromIndex, toIndex);

        return gutendexResponseMapper.toBookSearchResponse(content, page, size, totalElements);
    }

    public List<GutendexBook> fetchAllByTitleFilter(String titleQuery) {
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
