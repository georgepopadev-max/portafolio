package com.incident.assistant.service;

import com.incident.assistant.model.Document;
import com.incident.assistant.rag.RetrievalService;
import com.incident.assistant.repository.DocumentRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Service;

@Service
public class DocumentService {

    private final DocumentRepository documentRepository;
    private final RetrievalService retrievalService;

    public DocumentService(DocumentRepository documentRepository, RetrievalService retrievalService) {
        this.documentRepository = documentRepository;
        this.retrievalService = retrievalService;
    }

    @PostConstruct
    public void initializeKnowledgeBase() {
        loadRunbooks();
        loadPastIncidents();
        loadArchitectureDocs();
    }

    private void loadRunbooks() {
        Document billingRunbook = new Document(
            "runbook",
            "billing-service-504",
            "Billing Service 504 Timeout Troubleshooting",
            "504 Gateway Timeout Troubleshooting Guide for Billing Service\n\n" +
            "Symptoms:\n- 504 Gateway Timeout errors on /api/billing endpoints\n" +
            "- Increased latency in payment processing\n" +
            "- Connection pool exhaustion errors\n\n" +
            "Step 1: Check HikariCP Connection Pool\n" +
            "The billing service uses HikariCP with max pool size of 20.\n" +
            "Under high load, connections can exhaust. Check metrics:\n" +
            "  - hikaricp.connections.active\n" +
            "  - hikaricp.connections.idle\n" +
            "  - hikaricp.connections.pending\n\n" +
            "Step 2: Check PostgreSQL Active Connections\n" +
            "Run: SELECT count(*) FROM pg_stat_activity WHERE datname = 'billing_db'\n" +
            "If approaching max_connections (100), identify blocking queries.\n\n" +
            "Step 3: Check for Memory Pressure\n" +
            "High memory usage can cause slow processing leading to timeouts.\n" +
            "Review pod memory limits and current usage.\n\n" +
            "Step 4: Restart if Necessary\n" +
            "As a temporary measure, restart billing-service pods:\n" +
            "  kubectl rollout restart deployment/billing-service -n production\n\n" +
            "Resolution: Root cause was connection pool exhaustion due to a memory leak in the invoice processor."
        );

        Document dbRunbook = new Document(
            "runbook",
            "database-connection-issues",
            "Database Connection Issue Resolution",
            "Database Connection Troubleshooting Runbook\n\n" +
            "Common Causes:\n" +
            "1. Connection pool exhaustion (HikariCP)\n" +
            "2. Long-running queries blocking connections\n" +
            "3. Database server overload\n" +
            "4. Network connectivity issues\n\n" +
            "Diagnostic Commands:\n" +
            "1. Check active connections:\n" +
            "   SELECT pid, state, query, query_start\n" +
            "   FROM pg_stat_activity WHERE datname = 'main_db'\n\n" +
            "2. Identify blocking queries:\n" +
            "   SELECT * FROM pg_stat_activity\n" +
            "   WHERE state = 'active' AND query_start < NOW() - INTERVAL '5 minutes'\n\n" +
            "3. Check connection limits:\n" +
            "   SHOW max_connections\n\n" +
            "4. Check slow queries:\n" +
            "   SELECT * FROM pg_stat_statements\n" +
            "   ORDER BY mean_exec_time DESC LIMIT 10\n\n" +
            "Fixes:\n" +
            "- Kill blocking queries: SELECT pg_terminate_backend(pid)\n" +
            "- Increase pool size if underutilized\n" +
            "- Add connection pooler (PgBouncer)\n" +
            "- Optimize slow queries with indexes"
        );

        Document apiTimeoutRunbook = new Document(
            "runbook",
            "api-timeout-procedures",
            "API Timeout Resolution Procedures",
            "API Timeout Troubleshooting Procedures\n\n" +
            "Timeout Types:\n" +
            "1. 504 Gateway Timeout - upstream service too slow\n" +
            "2. 503 Service Unavailable - service overloaded\n" +
            "3. 502 Bad Gateway - upstream returned invalid response\n\n" +
            "Investigation Steps:\n" +
            "1. Identify affected endpoint and upstream service\n" +
            "2. Check upstream service health\n" +
            "3. Review connection pool metrics\n" +
            "4. Check for recent deployments or traffic spikes\n\n" +
            "Common Patterns:\n" +
            "- 504 + high CPU = computational bottleneck\n" +
            "- 504 + high memory = memory pressure, GC thrashing\n" +
            "- 504 + connection errors = downstream dependency issue\n\n" +
            "Escalation Criteria:\n" +
            "- Timeout duration > 5 minutes\n" +
            "- Multiple services affected\n" +
            "- Customer-facing impact confirmed"
        );

        documentRepository.save(billingRunbook);
        documentRepository.save(dbRunbook);
        documentRepository.save(apiTimeoutRunbook);
    }

    private void loadPastIncidents() {
        Document incident1 = new Document(
            "incident",
            "INC-4582",
            "Billing API 504 errors during payment batch - RESOLVED",
            "Incident #4582 - Billing API Timeouts\n\n" +
            "Date: 2024-11-15\n" +
            "Duration: 47 minutes\n" +
            "Severity: P1\n\n" +
            "Description:\n" +
            "Multiple customers reported 504 Gateway Timeout errors when attempting to process payments through the billing API.\n\n" +
            "Impact:\n" +
            "- ~200 payment transactions failed\n" +
            "- Customer complaints in support queue\n" +
            "- Payment batch processing delayed by 2 hours\n\n" +
            "Root Cause:\n" +
            "Memory leak in invoice processor component caused gradual memory growth.\n" +
            "Under high load during batch processing, pods reached memory limits,\n" +
            "causing increased GC activity and slow response times leading to upstream timeouts.\n\n" +
            "Resolution:\n" +
            "1. Identified memory leak via heap dump analysis\n" +
            "2. Restarted billing-service pods to restore normal operation\n" +
            "3. Deployed hotfix for invoice processor memory leak\n" +
            "4. Implemented memory-based auto-scaling\n\n" +
            "Prevention:\n" +
            "- Added memory monitoring alerts\n" +
            "- Implemented quarterly pod restarts\n" +
            "- Enhanced load testing for batch processing"
        );

        Document incident2 = new Document(
            "incident",
            "INC-4451",
            "Database connection pool exhaustion - RESOLVED",
            "Incident #4451 - Database Connection Pool Exhaustion\n\n" +
            "Date: 2024-10-28\n" +
            "Duration: 1 hour 23 minutes\n" +
            "Severity: P1\n\n" +
            "Description:\n" +
            "API services started returning 503 errors due to database connection pool exhaustion.\n\n" +
            "Impact:\n" +
            "- All API endpoints returning errors\n" +
            "- Complete service degradation\n" +
            "- 100% of active users affected\n\n" +
            "Root Cause:\n" +
            "A deployed database migration query ran without proper index hints,\n" +
            "causing sequential scans on a 10M+ row table. Long-running queries\n" +
            "held connections open, exhausting the pool.\n\n" +
            "Resolution:\n" +
            "1. Terminated long-running queries blocking connection release\n" +
            "2. Added index on migration query columns\n" +
            "3. Implemented query timeout limits\n" +
            "4. Set up PgBouncer connection pooler\n\n" +
            "Prevention:\n" +
            "- All migrations require DBA review\n" +
            "- Query performance testing in staging\n" +
            "- Connection pool monitoring alerts"
        );

        Document incident3 = new Document(
            "incident",
            "INC-4390",
            "Grid processor memory leak - RESOLVED",
            "Incident #4390 - Grid Processor Memory Leak\n\n" +
            "Date: 2024-10-15\n" +
            "Duration: 3 hours 15 minutes\n" +
            "Severity: P2\n\n" +
            "Description:\n" +
            "Grid processor service showed gradual memory growth over 72 hours.\n" +
            "Pods eventually hit memory limits and were OOMKilled.\n\n\n" +
            "Impact:\n" +
            "- Grid processing jobs failing intermittently\n" +
            "- Delayed data synchronization\n" +
            "- Required manual pod restarts every few hours\n\n" +
            "Root Cause:\n" +
            "Map accumulation issue in the data aggregation module. References\n" +
            "weren't being released, causing gradual memory growth.\n\n" +
            "Resolution:\n" +
            "1. Identified leak via heap profiling\n" +
            "2. Fixed map reference management\n" +
            "3. Deployed fix and monitored memory stability\n" +
            "4. All pods recovered after natural restart\n\n" +
            "Prevention:\n" +
            "- Added memory profiling to CI/CD pipeline\n" +
            "- Set up memory usage dashboards\n" +
            "- Configured automatic alerting at 80% memory usage"
        );

        documentRepository.save(incident1);
        documentRepository.save(incident2);
        documentRepository.save(incident3);
    }

    private void loadArchitectureDocs() {
        Document billingArch = new Document(
            "architecture",
            "billing-service-arch",
            "Billing Service Architecture",
            "Billing Service Architecture Documentation\n\n" +
            "Service Overview:\n" +
            "The billing service is a Spring Boot application that handles payment processing,\n" +
            "invoice generation, and subscription management.\n\n" +
            "Database Connection:\n" +
            "- Uses HikariCP connection pool\n" +
            "- Max pool size: 20 connections\n" +
            "- Connection timeout: 30 seconds\n" +
            "- Idle timeout: 10 minutes\n" +
            "- Max lifetime: 30 minutes per connection\n\n" +
            "Dependencies:\n" +
            "- PostgreSQL 15 (primary database)\n" +
            "- Redis (session cache)\n" +
            "- External payment gateway API\n\n" +
            "Scaling Configuration:\n" +
            "- Min pods: 2\n" +
            "- Max pods: 10\n" +
            "- CPU threshold: 70%\n" +
            "- Memory threshold: 80%\n\n" +
            "Error Handling Patterns:\n" +
            "- Circuit breaker for external API calls\n" +
            "- Retry with exponential backoff (max 3 attempts)\n" +
            "- Fallback to cached data for read operations\n" +
            "- Dead letter queue for failed payments\n\n" +
            "Monitoring:\n" +
            "- Key metrics: response time, error rate, payment volume\n" +
            "- Alert thresholds: p99 latency > 2s, error rate > 1%\n" +
            "- Dashboards: Grafana billing dashboard"
        );

        Document connectionPool = new Document(
            "architecture",
            "connection-pool-config",
            "Connection Pool Configuration",
            "HikariCP Connection Pool Configuration Guide\n\n" +
            "Configuration Parameters:\n" +
            "poolName: billing-pool\n" +
            "maximumPoolSize: 20\n" +
            "minimumIdle: 5\n" +
            "connectionTimeout: 30000\n" +
            "idleTimeout: 600000\n" +
            "maxLifetime: 1800000\n\n" +
            "Tuning Guidelines:\n" +
            "1. maximumPoolSize: Set based on database max_connections / number of service instances\n" +
            "2. minimumIdle: Keep some connections always ready for low-traffic periods\n" +
            "3. connectionTimeout: Should exceed your longest query execution time\n" +
            "4. idleTimeout: Shorter for high-turnover pools, longer for stable loads\n" +
            "5. maxLifetime: Shorter than database server connection timeout\n\n" +
            "Monitoring Metrics:\n" +
            "- connections.active: Current active connections\n" +
            "- connections.idle: Current idle connections\n" +
            "- connections.pending: Threads waiting for connection\n" +
            "- connections.timeout: Connection acquisition timeouts\n\n" +
            "Troubleshooting:\n" +
            "- High pending count: Increase pool size or check for connection leaks\n" +
            "- High connection timeout: Database server under heavy load\n" +
            "- Connections not released: Check for unclosed statements/connections"
        );

        Document errorHandling = new Document(
            "architecture",
            "error-handling-patterns",
            "Error Handling Patterns",
            "Service Error Handling Patterns and Best Practices\n\n" +
            "Timeout Handling:\n" +
            "1. Connect timeout: 5 seconds\n" +
            "2. Read timeout: 30 seconds\n" +
            "3. Total request timeout: 60 seconds\n\n" +
            "Retry Strategy:\n" +
            "- Max attempts: 3\n" +
            "- Backoff: Exponential (1s, 2s, 4s)\n" +
            "- Jitter: Random 0-500ms\n" +
            "- Retryable errors: 5xx, network errors, timeouts\n" +
            "- Non-retryable: 4xx client errors\n\n" +
            "Circuit Breaker:\n" +
            "- Failure threshold: 50% in 10 seconds\n" +
            "- Recovery timeout: 30 seconds\n" +
            "- Half-open requests: 3\n\n" +
            "Error Codes:\n" +
            "- 504: Gateway timeout - upstream service slow\n" +
            "- 503: Service unavailable - overloaded or down\n" +
            "- 502: Bad gateway - upstream returned invalid response\n" +
            "- 500: Internal server error - application error\n" +
            "- 429: Too many requests - rate limited\n\n" +
            "Logging Requirements:\n" +
            "- Log correlation IDs for request tracing\n" +
            "- Include error context (stack traces, request IDs)\n" +
            "- Log at WARN for retries, ERROR for failures\n" +
            "- Never log sensitive data (tokens, passwords)"
        );

        documentRepository.save(billingArch);
        documentRepository.save(connectionPool);
        documentRepository.save(errorHandling);
    }
}
