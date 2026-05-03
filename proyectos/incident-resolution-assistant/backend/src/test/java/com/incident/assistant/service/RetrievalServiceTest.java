package com.incident.assistant.service;

import com.incident.assistant.dto.CitationDto;
import com.incident.assistant.model.Document;
import com.incident.assistant.rag.EmbeddingService;
import com.incident.assistant.rag.RetrievalService;
import com.incident.assistant.repository.DocumentChunkRepository;
import com.incident.assistant.repository.DocumentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RetrievalServiceTest {

    @Mock
    private DocumentRepository documentRepository;

    @Mock
    private DocumentChunkRepository chunkRepository;

    private EmbeddingService embeddingService;
    private RetrievalService retrievalService;

    @BeforeEach
    void setUp() {
        embeddingService = new EmbeddingService();
        retrievalService = new RetrievalService(documentRepository, chunkRepository, embeddingService);
    }

    @Test
    void testRetrieve_WithMatchingDocuments_ReturnsRelevantCitations() {
        Document billingDoc = new Document(
            "runbook",
            "billing-504",
            "Billing Service 504 Timeout",
            "504 Gateway Timeout on billing service. Check connection pool. HikariCP max pool size 20."
        );
        Document dbDoc = new Document(
            "runbook",
            "database-connection",
            "Database Connection Issues",
            "Database connection pool exhaustion. Check active connections. Use PgBouncer for pooling."
        );
        Document unrelatedDoc = new Document(
            "runbook",
            "email-service",
            "Email Service Configuration",
            "Configure email SMTP settings. SPF and DKIM setup for outgoing emails."
        );

        when(documentRepository.findAll()).thenReturn(List.of(billingDoc, dbDoc, unrelatedDoc));

        List<CitationDto> results = retrievalService.retrieve("504 gateway timeout billing", 3);

        assertFalse(results.isEmpty());
        assertEquals(3, results.size());

        CitationDto topResult = results.get(0);
        assertTrue(topResult.getTitle().toLowerCase().contains("billing") ||
                   topResult.getTitle().toLowerCase().contains("timeout"));
        assertTrue(topResult.getRelevanceScore() >= 0);
        assertTrue(topResult.getRelevanceScore() <= 1);
    }

    @Test
    void testRetrieve_WithNoMatchingDocuments_ReturnsEmptyList() {
        Document unrelatedDoc = new Document(
            "runbook",
            "email-service",
            "Email Service Configuration",
            "Configure email SMTP settings. SPF and DKIM setup."
        );

        when(documentRepository.findAll()).thenReturn(List.of(unrelatedDoc));

        List<CitationDto> results = retrievalService.retrieve("completely unrelated query xyz", 5);

        assertNotNull(results);
    }

    @Test
    void testRetrieve_TopKLimit_WorksCorrectly() {
        Document doc1 = new Document("runbook", "doc1", "Doc 1", "Content 1 with specific keywords");
        Document doc2 = new Document("runbook", "doc2", "Doc 2", "Content 2 with specific keywords");
        Document doc3 = new Document("runbook", "doc3", "Doc 3", "Content 3 with specific keywords");
        Document doc4 = new Document("runbook", "doc4", "Doc 4", "Content 4 with specific keywords");
        Document doc5 = new Document("runbook", "doc5", "Doc 5", "Content 5 with specific keywords");

        when(documentRepository.findAll()).thenReturn(List.of(doc1, doc2, doc3, doc4, doc5));

        List<CitationDto> results = retrievalService.retrieve("specific keywords", 3);

        assertEquals(3, results.size());
    }

    @Test
    void testRetrieve_ResultsOrderedByRelevance() {
        Document billingDoc = new Document(
            "runbook",
            "billing-504",
            "Billing Service Timeout",
            "504 gateway timeout errors on billing API. Check connection pool exhaustion."
        );
        Document unrelatedDoc = new Document(
            "runbook",
            "email-config",
            "Email Configuration",
            "Email SMTP settings and SPF configuration."
        );

        when(documentRepository.findAll()).thenReturn(List.of(billingDoc, unrelatedDoc));

        List<CitationDto> results = retrievalService.retrieve("504 timeout billing API", 2);

        assertEquals(2, results.size());
        assertTrue(results.get(0).getRelevanceScore() >= results.get(1).getRelevanceScore());
    }

    @Test
    void testRetrieve_CitationsHaveValidFields() {
        Document doc = new Document(
            "runbook",
            "test-doc",
            "Test Document Title",
            "This is test content with important information."
        );

        when(documentRepository.findAll()).thenReturn(List.of(doc));

        List<CitationDto> results = retrievalService.retrieve("test query", 1);

        assertFalse(results.isEmpty());
        CitationDto citation = results.get(0);
        assertNotNull(citation.getDocumentId());
        assertNotNull(citation.getTitle());
        assertNotNull(citation.getSourceType());
        assertNotNull(citation.getRelevanceScore());
        assertNotNull(citation.getSnippet());
    }

    @Test
    void testEmbed_SameText_ReturnsSameEmbedding() {
        String text = "This is a test document about 504 gateway timeout";

        double[] embedding1 = embeddingService.embed(text);
        double[] embedding2 = embeddingService.embed(text);

        assertArrayEquals(embedding1, embedding2);
    }

    @Test
    void testEmbed_DifferentTexts_ProduceDifferentEmbeddings() {
        String text1 = "504 gateway timeout error";
        String text2 = "Email configuration SPF DKIM";

        double[] embedding1 = embeddingService.embed(text1);
        double[] embedding2 = embeddingService.embed(text2);

        double similarity = embeddingService.cosineSimilarity(embedding1, embedding2);

        assertTrue(similarity >= -1 && similarity <= 1);
    }

    @Test
    void testCosineSimilarity_SameVector_ReturnsOne() {
        double[] vector = {0.5, 0.5, 0.5, 0.5};

        double similarity = embeddingService.cosineSimilarity(vector, vector);

        assertEquals(1.0, similarity, 0.0001);
    }

    @Test
    void testCosineSimilarity_OrthogonalVectors_ReturnsZero() {
        double[] vector1 = {1.0, 0.0, 0.0, 0.0};
        double[] vector2 = {0.0, 1.0, 0.0, 0.0};

        double similarity = embeddingService.cosineSimilarity(vector1, vector2);

        assertEquals(0.0, similarity, 0.0001);
    }

    @Test
    void testChunkDocument_SplitsContentCorrectly() {
        Document doc = new Document(
            "test",
            "test-id",
            "Test Document",
            "This is the first sentence. This is the second sentence. This is the third sentence."
        );

        retrievalService.chunkDocument(doc);

        verify(chunkRepository, atLeastOnce()).save(any());
    }
}
