import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Problem 10: Multi-Level Cache System with Hash Tables
 *
 * Implements L1 (in-memory LRU), L2 (SSD-backed), and L3 (database)
 * cache levels with promotion, eviction, and hit ratio tracking.
 */
public class Problem10 {

    /** Represents cached video data. */
    static class VideoData {
        String videoId;
        String title;
        int sizeMB;

        VideoData(String videoId, String title, int sizeMB) {
            this.videoId = videoId;
            this.title = title;
            this.sizeMB = sizeMB;
        }
    }

    /** L1 Cache: in-memory LRU (access-order LinkedHashMap). */
    private LinkedHashMap<String, VideoData> l1Cache;
    private int l1MaxSize;

    /** L2 Cache: simulated SSD-backed storage. */
    private LinkedHashMap<String, VideoData> l2Cache;
    private int l2MaxSize;

    /** L3: Simulated database (all videos). */
    private HashMap<String, VideoData> database;

    /** Access count per video for promotion decisions. */
    private HashMap<String, Integer> accessCounts;

    /** Promotion threshold: promote from L2 to L1 after this many accesses. */
    private int promotionThreshold;

    /** Statistics. */
    private int l1Hits, l1Misses;
    private int l2Hits, l2Misses;
    private int l3Hits, l3Misses;
    private long totalLookupTimeNs;
    private int totalLookups;

    public Problem10(int l1MaxSize, int l2MaxSize, int promotionThreshold) {
        this.l1MaxSize = l1MaxSize;
        this.l2MaxSize = l2MaxSize;
        this.promotionThreshold = promotionThreshold;

        // Access-order LinkedHashMap for LRU
        this.l1Cache = new LinkedHashMap<>(l1MaxSize, 0.75f, true);
        this.l2Cache = new LinkedHashMap<>(l2MaxSize, 0.75f, true);
        this.database = new HashMap<>();
        this.accessCounts = new HashMap<>();
    }

    /**
     * Adds a video to the database (L3).
     *
     * @param video the video data
     */
    public void addToDatabase(VideoData video) {
        database.put(video.videoId, video);
    }

    /**
     * Retrieves a video, checking L1 -> L2 -> L3 in order.
     * Promotes videos between levels based on access patterns.
     *
     * @param videoId the video to retrieve
     * @return result message with cache level and timing
     */
    public String getVideo(String videoId) {
        long startTime = System.nanoTime();
        totalLookups++;

        // Track access count
        int accessCount = accessCounts.getOrDefault(videoId, 0) + 1;
        accessCounts.put(videoId, accessCount);

        // Check L1
        if (l1Cache.containsKey(videoId)) {
            l1Hits++;
            long elapsed = System.nanoTime() - startTime;
            totalLookupTimeNs += elapsed;
            return "L1 Cache HIT (" + formatTime(elapsed) + ")";
        }
        l1Misses++;

        // Simulate L1 lookup delay
        simulateDelay(500000); // 0.5ms

        // Check L2
        if (l2Cache.containsKey(videoId)) {
            l2Hits++;
            VideoData video = l2Cache.get(videoId);

            // Promote to L1 if access count exceeds threshold
            if (accessCount >= promotionThreshold) {
                addToL1(videoId, video);
                long elapsed = System.nanoTime() - startTime;
                totalLookupTimeNs += elapsed;
                return "L2 Cache HIT (" + formatTime(elapsed) + ") -> Promoted to L1";
            }

            long elapsed = System.nanoTime() - startTime;
            totalLookupTimeNs += elapsed;
            return "L2 Cache HIT (" + formatTime(elapsed) + ")";
        }
        l2Misses++;

        // Simulate L2 lookup delay
        simulateDelay(5000000); // 5ms

        // Check L3 (database)
        if (database.containsKey(videoId)) {
            l3Hits++;
            VideoData video = database.get(videoId);

            // Add to L2
            addToL2(videoId, video);

            // Simulate database delay
            simulateDelay(150000000); // 150ms

            long elapsed = System.nanoTime() - startTime;
            totalLookupTimeNs += elapsed;
            return "L1 MISS -> L2 MISS -> L3 Database HIT (" + formatTime(elapsed) + ") -> Added to L2 (access count: " + accessCount + ")";
        }
        l3Misses++;

        long elapsed = System.nanoTime() - startTime;
        totalLookupTimeNs += elapsed;
        return "NOT FOUND in any level";
    }

    /**
     * Invalidates a video across all cache levels.
     *
     * @param videoId the video to invalidate
     */
    public void invalidate(String videoId) {
        l1Cache.remove(videoId);
        l2Cache.remove(videoId);
        accessCounts.remove(videoId);
    }

    /** Adds to L1 with LRU eviction. */
    private void addToL1(String videoId, VideoData video) {
        if (l1Cache.size() >= l1MaxSize) {
            // Evict LRU entry
            String eldest = l1Cache.keySet().iterator().next();
            l1Cache.remove(eldest);
        }
        l1Cache.put(videoId, video);
    }

    /** Adds to L2 with LRU eviction. */
    private void addToL2(String videoId, VideoData video) {
        if (l2Cache.size() >= l2MaxSize) {
            String eldest = l2Cache.keySet().iterator().next();
            l2Cache.remove(eldest);
        }
        l2Cache.put(videoId, video);
    }

    /** Simulates delay in nanoseconds. */
    private void simulateDelay(long nanos) {
        long start = System.nanoTime();
        while (System.nanoTime() - start < nanos) {
            // busy wait
        }
    }

    /** Formats nanoseconds to readable time. */
    private String formatTime(long nanos) {
        double ms = nanos / 1_000_000.0;
        return String.format("%.1fms", ms);
    }

    /**
     * Returns comprehensive cache statistics.
     *
     * @return formatted statistics
     */
    public String getStatistics() {
        int l1Total = l1Hits + l1Misses;
        int l2Total = l2Hits + l2Misses;
        int l3Total = l3Hits + l3Misses;

        double l1HitRate = l1Total > 0 ? (l1Hits * 100.0 / l1Total) : 0;
        double l2HitRate = l2Total > 0 ? (l2Hits * 100.0 / l2Total) : 0;
        double l3HitRate = l3Total > 0 ? (l3Hits * 100.0 / l3Total) : 0;

        int overallHits = l1Hits + l2Hits + l3Hits;
        double overallHitRate = totalLookups > 0 ? (overallHits * 100.0 / totalLookups) : 0;
        double avgTime = totalLookups > 0 ? (totalLookupTimeNs / 1_000_000.0 / totalLookups) : 0;

        return String.format(
                "L1: Hit Rate %.0f%%, Size: %d/%d\n" +
                "  L2: Hit Rate %.0f%%, Size: %d/%d\n" +
                "  L3: Hit Rate %.0f%%\n" +
                "  Overall: Hit Rate %.0f%%, Avg Time: %.1fms",
                l1HitRate, l1Cache.size(), l1MaxSize,
                l2HitRate, l2Cache.size(), l2MaxSize,
                l3HitRate,
                overallHitRate, avgTime);
    }

    public static void main(String[] args) {
        Problem10 cache = new Problem10(3, 5, 2); // L1: 3, L2: 5, promote after 2 accesses

        // Populate database
        cache.addToDatabase(new VideoData("video_123", "Breaking News Live", 500));
        cache.addToDatabase(new VideoData("video_456", "Cat Compilation", 200));
        cache.addToDatabase(new VideoData("video_789", "Tutorial Java", 350));
        cache.addToDatabase(new VideoData("video_999", "Rare Documentary", 800));
        cache.addToDatabase(new VideoData("video_001", "Music Video", 150));

        // First access - all go to L3
        System.out.println("getVideo(\"video_123\") -> " + cache.getVideo("video_123"));
        System.out.println();

        // Second access - should be L2 hit, and promoted to L1 (access count = 2 >= threshold)
        System.out.println("getVideo(\"video_123\") [2nd] -> " + cache.getVideo("video_123"));
        System.out.println();

        // Third access - should be L1 hit
        System.out.println("getVideo(\"video_123\") [3rd] -> " + cache.getVideo("video_123"));
        System.out.println();

        // New video - L3
        System.out.println("getVideo(\"video_999\") -> " + cache.getVideo("video_999"));
        System.out.println();

        // Non-existent video
        System.out.println("getVideo(\"video_000\") -> " + cache.getVideo("video_000"));
        System.out.println();

        // Cache invalidation
        cache.invalidate("video_123");
        System.out.println("After invalidate(\"video_123\"):");
        System.out.println("getVideo(\"video_123\") -> " + cache.getVideo("video_123"));
        System.out.println();

        // Statistics
        System.out.println("getStatistics() ->");
        System.out.println("  " + cache.getStatistics());
    }
}
