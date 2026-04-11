import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Problem 7: Autocomplete System for Search Engine
 *
 * Uses HashMap for query frequency storage and prefix-based
 * matching to return top suggestions efficiently.
 */
public class Problem7 {

    /** Maps query string -> search frequency. */
    private HashMap<String, Integer> queryFrequency;

    /** Cache for popular prefix results. */
    private HashMap<String, List<String>> prefixCache;

    public Problem7() {
        queryFrequency = new HashMap<>();
        prefixCache = new HashMap<>();
    }

    /**
     * Records a search query, incrementing its frequency.
     *
     * @param query the search query
     */
    public void recordSearch(String query) {
        String normalized = query.toLowerCase().trim();
        queryFrequency.put(normalized, queryFrequency.getOrDefault(normalized, 0) + 1);
        // Invalidate cache entries that this query matches
        for (int i = 1; i <= normalized.length(); i++) {
            prefixCache.remove(normalized.substring(0, i));
        }
    }

    /**
     * Updates frequency for a specific query.
     *
     * @param query the query to update
     * @return the new frequency
     */
    public int updateFrequency(String query) {
        String normalized = query.toLowerCase().trim();
        int newFreq = queryFrequency.getOrDefault(normalized, 0) + 1;
        queryFrequency.put(normalized, newFreq);
        return newFreq;
    }

    /**
     * Returns top N suggestions for a given prefix.
     *
     * @param prefix the search prefix
     * @param n max suggestions
     * @return list of suggestions with frequencies
     */
    public List<String> search(String prefix, int n) {
        String normalizedPrefix = prefix.toLowerCase().trim();

        // Check cache first
        if (prefixCache.containsKey(normalizedPrefix)) {
            return prefixCache.get(normalizedPrefix);
        }

        // Find all matching queries
        List<Map.Entry<String, Integer>> matches = new ArrayList<>();
        for (Map.Entry<String, Integer> entry : queryFrequency.entrySet()) {
            if (entry.getKey().startsWith(normalizedPrefix)) {
                matches.add(entry);
            }
        }

        // Sort by frequency descending
        matches.sort((a, b) -> b.getValue() - a.getValue());

        // Build result list
        List<String> results = new ArrayList<>();
        for (int i = 0; i < Math.min(n, matches.size()); i++) {
            Map.Entry<String, Integer> entry = matches.get(i);
            results.add("\"" + entry.getKey() + "\" (" + entry.getValue() + " searches)");
        }

        // Cache the result
        prefixCache.put(normalizedPrefix, results);

        return results;
    }

    /**
     * Returns the total number of unique queries stored.
     *
     * @return count of unique queries
     */
    public int getTotalQueries() {
        return queryFrequency.size();
    }

    /**
     * Returns memory estimate for stored queries.
     *
     * @return estimated characters stored
     */
    public long getMemoryEstimate() {
        long totalChars = 0;
        for (String key : queryFrequency.keySet()) {
            totalChars += key.length();
        }
        return totalChars;
    }

    public static void main(String[] args) {
        Problem7 autocomplete = new Problem7();

        // Populate with search data
        String[] queries = {
            "java tutorial", "java tutorial", "java tutorial",
            "javascript basics", "javascript basics",
            "java download", "java download",
            "java 21 features",
            "java spring boot", "java spring boot", "java spring boot", "java spring boot",
            "javascript frameworks",
            "python tutorial", "python tutorial", "python tutorial", "python tutorial",
            "python machine learning",
            "java collections", "java collections",
        };

        for (String q : queries) {
            autocomplete.recordSearch(q);
        }

        // Simulate higher frequencies
        for (int i = 0; i < 1234564; i++) autocomplete.updateFrequency("java tutorial");
        for (int i = 0; i < 987652; i++) autocomplete.updateFrequency("javascript basics");
        for (int i = 0; i < 456787; i++) autocomplete.updateFrequency("java download");

        // Search with prefix
        System.out.println("search(\"jav\") ->");
        List<String> results = autocomplete.search("jav", 5);
        for (int i = 0; i < results.size(); i++) {
            System.out.println("  " + (i + 1) + ". " + results.get(i));
        }

        // Another prefix
        System.out.println("\nsearch(\"py\") ->");
        results = autocomplete.search("py", 5);
        for (int i = 0; i < results.size(); i++) {
            System.out.println("  " + (i + 1) + ". " + results.get(i));
        }

        // Update trending query
        System.out.println("\nupdateFrequency(\"java 21 features\") -> Frequency: " + autocomplete.updateFrequency("java 21 features"));
        System.out.println("updateFrequency(\"java 21 features\") -> Frequency: " + autocomplete.updateFrequency("java 21 features"));

        // Stats
        System.out.println("\nTotal unique queries: " + autocomplete.getTotalQueries());
        System.out.println("Estimated memory (chars): " + autocomplete.getMemoryEstimate());
    }
}
