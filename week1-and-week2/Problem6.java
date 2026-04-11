import java.util.HashMap;

/**
 * Problem 6: Distributed Rate Limiter for API Gateway
 *
 * Uses HashMap to track per-client token buckets.
 * Implements token bucket algorithm with time-based refill.
 */
public class Problem6 {

    /** Represents a token bucket for a single client. */
    static class TokenBucket {
        int tokens;
        long lastRefillTime;
        int maxTokens;
        int refillRate; // tokens per refill interval
        long refillIntervalMs; // interval in milliseconds

        TokenBucket(int maxTokens, int refillRate, long refillIntervalMs) {
            this.maxTokens = maxTokens;
            this.tokens = maxTokens;
            this.refillRate = refillRate;
            this.refillIntervalMs = refillIntervalMs;
            this.lastRefillTime = System.currentTimeMillis();
        }

        /** Refills tokens based on elapsed time. */
        void refill() {
            long now = System.currentTimeMillis();
            long elapsed = now - lastRefillTime;
            long intervals = elapsed / refillIntervalMs;

            if (intervals > 0) {
                tokens = Math.min(maxTokens, tokens + (int)(intervals * refillRate));
                lastRefillTime = now;
            }
        }

        /** Attempts to consume a token. Returns true if allowed. */
        boolean tryConsume() {
            refill();
            if (tokens > 0) {
                tokens--;
                return true;
            }
            return false;
        }

        /** Returns seconds until next refill. */
        long getRetryAfterSeconds() {
            long now = System.currentTimeMillis();
            long nextRefill = lastRefillTime + refillIntervalMs;
            long remaining = Math.max(0, nextRefill - now);
            return remaining / 1000;
        }
    }

    /** Maps clientId -> TokenBucket. */
    private HashMap<String, TokenBucket> clientBuckets;

    /** Default rate limit settings. */
    private int maxTokens;
    private int refillRate;
    private long refillIntervalMs;

    public Problem6(int maxTokens, int refillRate, long refillIntervalMs) {
        this.clientBuckets = new HashMap<>();
        this.maxTokens = maxTokens;
        this.refillRate = refillRate;
        this.refillIntervalMs = refillIntervalMs;
    }

    /**
     * Checks rate limit for a client. Creates a new bucket
     * if the client is seen for the first time.
     *
     * @param clientId the client's API key or IP
     * @return result message (Allowed or Denied)
     */
    public synchronized String checkRateLimit(String clientId) {
        TokenBucket bucket = clientBuckets.computeIfAbsent(clientId,
                k -> new TokenBucket(maxTokens, refillRate, refillIntervalMs));

        if (bucket.tryConsume()) {
            return "Allowed (" + bucket.tokens + " requests remaining)";
        } else {
            return "Denied (0 requests remaining, retry after " + bucket.getRetryAfterSeconds() + "s)";
        }
    }

    /**
     * Returns the rate limit status for a client.
     *
     * @param clientId the client's API key or IP
     * @return formatted status string
     */
    public String getRateLimitStatus(String clientId) {
        TokenBucket bucket = clientBuckets.get(clientId);
        if (bucket == null) {
            return "{client not found}";
        }
        bucket.refill();
        int used = maxTokens - bucket.tokens;
        return "{used: " + used + ", limit: " + maxTokens + ", remaining: " + bucket.tokens + "}";
    }

    /**
     * Returns the total number of tracked clients.
     *
     * @return number of active clients
     */
    public int getActiveClients() {
        return clientBuckets.size();
    }

    public static void main(String[] args) {
        // 10 requests max, refill 10 per 5 seconds
        Problem6 rateLimiter = new Problem6(10, 10, 5000);

        String clientId = "abc123";

        // Make requests until denied
        for (int i = 0; i < 12; i++) {
            String result = rateLimiter.checkRateLimit(clientId);
            System.out.println("checkRateLimit(\"" + clientId + "\") -> " + result);
        }

        // Check status
        System.out.println("\ngetRateLimitStatus(\"" + clientId + "\") -> " + rateLimiter.getRateLimitStatus(clientId));

        // Multiple clients
        System.out.println("\n--- Multiple Clients ---");
        rateLimiter.checkRateLimit("client_A");
        rateLimiter.checkRateLimit("client_B");
        rateLimiter.checkRateLimit("client_C");
        System.out.println("Active clients: " + rateLimiter.getActiveClients());

        // Concurrent access simulation
        System.out.println("\n--- Concurrent Rate Limiting ---");
        Problem6 concurrentLimiter = new Problem6(5, 5, 5000);

        Thread[] threads = new Thread[8];
        for (int i = 0; i < 8; i++) {
            final int idx = i;
            threads[i] = new Thread(() -> {
                String result = concurrentLimiter.checkRateLimit("shared_client");
                System.out.println("Thread-" + idx + ": " + result);
            });
        }
        for (Thread t : threads) t.start();
        for (Thread t : threads) {
            try { t.join(); } catch (InterruptedException e) { }
        }
    }
}
