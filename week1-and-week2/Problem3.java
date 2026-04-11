import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Problem 3: DNS Cache with TTL (Time To Live)
 *
 * Uses HashMap with custom DNSEntry for TTL-based expiration,
 * LinkedHashMap for LRU eviction, and cache hit/miss tracking.
 */
public class Problem3 {

    /** Represents a cached DNS entry. */
    static class DNSEntry {
        String domain;
        String ipAddress;
        long timestamp;
        long expiryTime;

        DNSEntry(String domain, String ipAddress, long ttlSeconds) {
            this.domain = domain;
            this.ipAddress = ipAddress;
            this.timestamp = System.currentTimeMillis();
            this.expiryTime = this.timestamp + (ttlSeconds * 1000);
        }

        boolean isExpired() {
            return System.currentTimeMillis() > expiryTime;
        }
    }

    /** LRU cache with access-order LinkedHashMap. */
    private LinkedHashMap<String, DNSEntry> cache;

    /** Maximum cache capacity. */
    private int maxSize;

    /** Simulated upstream DNS mappings. */
    private HashMap<String, String> upstreamDns;

    /** Default TTL in seconds. */
    private long defaultTtl;

    /** Cache statistics. */
    private int hits;
    private int misses;
    private int expired;

    public Problem3(int maxSize, long defaultTtl) {
        this.maxSize = maxSize;
        this.defaultTtl = defaultTtl;
        this.hits = 0;
        this.misses = 0;
        this.expired = 0;

        // Access-order LinkedHashMap for LRU
        this.cache = new LinkedHashMap<>(maxSize, 0.75f, true);

        // Simulated upstream DNS
        this.upstreamDns = new HashMap<>();
        upstreamDns.put("google.com", "172.217.14.206");
        upstreamDns.put("facebook.com", "157.240.1.35");
        upstreamDns.put("amazon.com", "176.32.103.205");
        upstreamDns.put("github.com", "140.82.121.4");
        upstreamDns.put("netflix.com", "54.237.226.164");
    }

    /**
     * Resolves a domain name to an IP address.
     * Checks cache first, falls back to upstream DNS.
     *
     * @param domain the domain to resolve
     * @return resolution result message
     */
    public String resolve(String domain) {
        // Check cache
        if (cache.containsKey(domain)) {
            DNSEntry entry = cache.get(domain);

            if (entry.isExpired()) {
                cache.remove(domain);
                expired++;
                // Query upstream
                String ip = queryUpstream(domain);
                if (ip != null) {
                    addToCache(domain, ip);
                    return "Cache EXPIRED -> Query upstream -> " + ip;
                }
                return "Cache EXPIRED -> Upstream MISS -> Domain not found";
            }

            hits++;
            long lookupTime = System.nanoTime() % 10;
            return "Cache HIT -> " + entry.ipAddress + " (retrieved in 0." + lookupTime + "ms)";
        }

        // Cache miss
        misses++;
        String ip = queryUpstream(domain);
        if (ip != null) {
            addToCache(domain, ip);
            return "Cache MISS -> Query upstream -> " + ip + " (TTL: " + defaultTtl + "s)";
        }
        return "Cache MISS -> Upstream MISS -> Domain not found";
    }

    /**
     * Queries the simulated upstream DNS.
     *
     * @param domain the domain to look up
     * @return IP address or null
     */
    private String queryUpstream(String domain) {
        return upstreamDns.get(domain);
    }

    /**
     * Adds an entry to the cache with LRU eviction.
     *
     * @param domain the domain name
     * @param ip the IP address
     */
    private void addToCache(String domain, String ip) {
        if (cache.size() >= maxSize) {
            // Remove eldest (LRU) entry
            Iterator<String> it = cache.keySet().iterator();
            if (it.hasNext()) {
                it.next();
                it.remove();
            }
        }
        cache.put(domain, new DNSEntry(domain, ip, defaultTtl));
    }

    /**
     * Removes all expired entries from the cache.
     *
     * @return number of entries cleaned
     */
    public int cleanExpiredEntries() {
        int cleaned = 0;
        Iterator<Map.Entry<String, DNSEntry>> it = cache.entrySet().iterator();
        while (it.hasNext()) {
            if (it.next().getValue().isExpired()) {
                it.remove();
                cleaned++;
            }
        }
        return cleaned;
    }

    /**
     * Returns cache statistics.
     *
     * @return formatted stats string
     */
    public String getCacheStats() {
        int total = hits + misses + expired;
        double hitRate = total > 0 ? (hits * 100.0 / total) : 0;
        return String.format("Hit Rate: %.1f%%, Misses: %d, Expired: %d, Cache Size: %d/%d",
                hitRate, misses, expired, cache.size(), maxSize);
    }

    public static void main(String[] args) {
        Problem3 dnsCache = new Problem3(3, 2); // max 3 entries, 2 second TTL

        // First lookup - cache miss
        System.out.println("resolve(\"google.com\") -> " + dnsCache.resolve("google.com"));

        // Second lookup - cache hit
        System.out.println("resolve(\"google.com\") -> " + dnsCache.resolve("google.com"));

        // More lookups
        System.out.println("resolve(\"facebook.com\") -> " + dnsCache.resolve("facebook.com"));
        System.out.println("resolve(\"amazon.com\") -> " + dnsCache.resolve("amazon.com"));

        // This should trigger LRU eviction (cache size = 3)
        System.out.println("resolve(\"github.com\") -> " + dnsCache.resolve("github.com"));

        // Stats
        System.out.println("\ngetCacheStats() -> " + dnsCache.getCacheStats());

        // Wait for TTL to expire
        System.out.println("\nWaiting for TTL expiration (2 seconds)...");
        try { Thread.sleep(2100); } catch (InterruptedException e) { }

        // Should show expired
        System.out.println("resolve(\"github.com\") -> " + dnsCache.resolve("github.com"));
        System.out.println("\ngetCacheStats() -> " + dnsCache.getCacheStats());
    }
}
