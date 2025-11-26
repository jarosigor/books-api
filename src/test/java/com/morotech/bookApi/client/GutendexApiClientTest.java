package com.morotech.bookApi.client;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.morotech.bookApi.exception.GutendexApiException;
import com.morotech.bookApi.exception.InvalidPageException;
import com.morotech.bookApi.exception.ObjectMapperException;
import com.morotech.bookApi.model.dto.GutendexApiResponse;
import com.morotech.bookApi.model.dto.GutendexBook;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.client.RestClient;

import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Gutendex API Client Tests")
class GutendexApiClientTest {

    @Mock
    RestClient gutendexRestClient;

    @Mock
    RestClient.RequestHeadersUriSpec requestHeadersUriSpec;

    @Mock
    RestClient.ResponseSpec responseSpec;

    ObjectMapper objectMapper = new ObjectMapper();

    GutendexApiClient client;

    GutendexApiResponse mockResponse;
    GutendexBook mockBook;
    String mockResponseJson;
    String mockBookJson;

    @BeforeEach
    void setup() throws Exception {
        client = new GutendexApiClient(gutendexRestClient, objectMapper);

        mockBook = new GutendexBook();
        mockBook.setId(1);
        mockBook.setTitle("Oliver Twist");

        mockResponse = new GutendexApiResponse();
        mockResponse.setCount(1);
        mockResponse.setResults(List.of(mockBook));
        mockResponse.setNext("");

        mockResponseJson = objectMapper.writeValueAsString(mockResponse);
        mockBookJson = objectMapper.writeValueAsString(mockBook);
    }

    @Test
    @DisplayName("Should search books by title successfully")
    void searchBooksByTitle_Success() throws Exception {
        // Given
        when(gutendexRestClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri(anyString())).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.onStatus(any(), any())).thenReturn(responseSpec);
        when(responseSpec.body(String.class)).thenReturn(mockResponseJson);

        // When
        var result = client.searchBooksByTitle("oliver", 1);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getCount()).isEqualTo(1);
        assertThat(result.getResults()).hasSize(1);
        assertThat(result.getResults().getFirst().getTitle()).isEqualTo("Oliver Twist");
    }

    @Test
    @DisplayName("Should return null when API returns empty response")
    void searchBooksByTitle_EmptyResponse() {
        // Given
        when(gutendexRestClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri(anyString())).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.onStatus(any(), any())).thenReturn(responseSpec);
        when(responseSpec.body(String.class)).thenReturn("");

        // When
        var result = client.searchBooksByTitle("nonexistent", 1);

        // Then
        assertThat(result).isNull();
    }

    @Test
    @DisplayName("Should throw InvalidPageException when page not found")
    void searchBooksByTitle_InvalidPage() {
        // Given
        when(gutendexRestClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri(anyString())).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.onStatus(any(), any())).thenAnswer(invocation -> {
            throw new InvalidPageException("Page number may be bigger than available pages.");
        });

        assertThatThrownBy(() -> client.searchBooksByTitle("test", 999))
                .isInstanceOf(InvalidPageException.class)
                .hasMessageContaining("Page number may be bigger than available pages");
    }

    @Test
    @DisplayName("Should throw ObjectMapperException on invalid JSON")
    void searchBooksByTitle_InvalidJson() {
        // Given
        when(gutendexRestClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri(anyString())).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.onStatus(any(), any())).thenReturn(responseSpec);
        when(responseSpec.body(String.class)).thenReturn("invalid json");

        // When/Then
        assertThatThrownBy(() -> client.searchBooksByTitle("test", 1))
                .isInstanceOf(ObjectMapperException.class)
                .hasMessageContaining("Error while processing API response");
    }

    @Test
    @DisplayName("Should get book by ID successfully")
    void getBookById_Success() {
        // Given
        when(gutendexRestClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri(anyString())).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.body(String.class)).thenReturn(mockBookJson);

        // When
        var result = client.getBookById(1);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1);
        assertThat(result.getTitle()).isEqualTo("Oliver Twist");
    }

    @Test
    @DisplayName("Should return null when book not found")
    void getBookById_NotFound() {
        // Given
        when(gutendexRestClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri(anyString())).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.body(String.class)).thenReturn(null);

        // When
        var result = client.getBookById(999);

        // Then
        assertThat(result).isNull();
    }
}
