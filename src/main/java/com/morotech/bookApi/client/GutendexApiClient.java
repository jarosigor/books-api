package com.morotech.bookApi.client;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.morotech.bookApi.exception.GutendexApiException;
import com.morotech.bookApi.exception.InvalidPageException;
import com.morotech.bookApi.exception.ObjectMapperException;
import com.morotech.bookApi.model.dto.GutendexApiResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;
import org.springframework.web.util.UriComponentsBuilder;


@RequiredArgsConstructor
@Component
@Slf4j
public class GutendexApiClient {

    private final RestClient gutendexRestClient;
    private final ObjectMapper objectMapper;

    @Cacheable("gutendexTitleSearch")
    public GutendexApiResponse searchBooksByTitle(String title, int page) {
        log.info("Searching Gutendex for title {}", title);
        try {
            var uri = UriComponentsBuilder.fromPath("/books/")
                    .queryParam("search", title)
                    .queryParam("page", page)
                    .encode()
                    .toUriString();
            var gutendexApiResponseJson = gutendexRestClient.get()
                    .uri(uri)
                    .retrieve()
                    .onStatus(status -> status == HttpStatus.NOT_FOUND,
                            ((request, response) -> { throw new InvalidPageException("Page number may be bigger than available pages."); }))
                    .body(String.class);
            if (gutendexApiResponseJson == null || gutendexApiResponseJson.isEmpty()) {
                return null;
            } else {
                log.info("Gutendex API call successful");
                return objectMapper.readValue(gutendexApiResponseJson, GutendexApiResponse.class);
            }
        } catch (RestClientException e) {
            log.error("Failed to call Gutendex API", e);
            throw new GutendexApiException(String.format("Error while searching books by title %s: %s", title, e.getMessage()));
        } catch (JsonProcessingException e) {
            log.error("Failed to parse Gutendex API response", e);
            throw new ObjectMapperException(String.format("Error while processing API response: %s", e.getMessage()));
        }
    }

    // To be adjusted
    public String getBookById(int bookId) {
        log.info("Fetching book details from Gutendex for book ID {}", bookId);
        try {
            var uri = UriComponentsBuilder.fromPath("/books/{bookId}")
                    .buildAndExpand(bookId)
                    .encode()
                    .toUriString();
            return gutendexRestClient.get()
                    .uri(uri)
                    .retrieve()
                    .body(String.class);
        } catch (RestClientException e) {
            throw new GutendexApiException(String.format("Error while fetching book details for book ID %d: %s", bookId, e.getMessage()));
        }
    }
}
