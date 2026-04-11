import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Problem 5: Real-Time Analytics Dashboard for Website Traffic
 *
 * Uses multiple HashMaps for page views, unique visitors,
 * and traffic source tracking. Supports top-N page queries.
 */
public class Problem5 {

    /** Represents a page view event. */
    static class PageViewEvent {
        String url;
        String userId;
        String source;

        PageViewEvent(String url, String userId, String source) {
            this.url = url;
            this.userId = userId;
            this.source = source;
        }
    }

    /** Maps pageUrl -> total visit count. */
    private HashMap<String, Integer> pageViews;

    /** Maps pageUrl -> set of unique user IDs. */
    private HashMap<String, Set<String>> uniqueVisitors;

    /** Maps traffic source -> count. */
    private HashMap<String, Integer> trafficSources;

    /** Total events processed. */
    private int totalEvents;

    public Problem5() {
        pageViews = new HashMap<>();
        uniqueVisitors = new HashMap<>();
        trafficSources = new HashMap<>();
        totalEvents = 0;
    }

    /**
     * Processes a single page view event.
     *
     * @param event the page view event
     */
    public void processEvent(PageViewEvent event) {
        totalEvents++;

        // Update page view count
        pageViews.put(event.url, pageViews.getOrDefault(event.url, 0) + 1);

        // Track unique visitors
        uniqueVisitors.computeIfAbsent(event.url, k -> new HashSet<>()).add(event.userId);

        // Track traffic source
        trafficSources.put(event.source, trafficSources.getOrDefault(event.source, 0) + 1);
    }

    /**
     * Returns top N most visited pages.
     *
     * @param n number of top pages to return
     * @return list of formatted page stats
     */
    public List<String> getTopPages(int n) {
        // Sort pages by visit count
        List<Map.Entry<String, Integer>> sorted = new ArrayList<>(pageViews.entrySet());
        sorted.sort((a, b) -> b.getValue() - a.getValue());

        List<String> result = new ArrayList<>();
        for (int i = 0; i < Math.min(n, sorted.size()); i++) {
            Map.Entry<String, Integer> entry = sorted.get(i);
            String url = entry.getKey();
            int views = entry.getValue();
            int unique = uniqueVisitors.containsKey(url) ? uniqueVisitors.get(url).size() : 0;
            result.add(String.format("%d. %s - %d views (%d unique)", i + 1, url, views, unique));
        }
        return result;
    }

    /**
     * Returns traffic source distribution as percentages.
     *
     * @return list of formatted source stats
     */
    public List<String> getTrafficSourceStats() {
        List<Map.Entry<String, Integer>> sorted = new ArrayList<>(trafficSources.entrySet());
        sorted.sort((a, b) -> b.getValue() - a.getValue());

        List<String> result = new ArrayList<>();
        for (Map.Entry<String, Integer> entry : sorted) {
            double percentage = (entry.getValue() * 100.0) / totalEvents;
            result.add(String.format("%s: %.0f%%", entry.getKey(), percentage));
        }
        return result;
    }

    /**
     * Generates a full dashboard summary.
     */
    public void getDashboard() {
        System.out.println("  Top Pages:");
        for (String page : getTopPages(10)) {
            System.out.println("    " + page);
        }

        System.out.println("  Traffic Sources:");
        for (String source : getTrafficSourceStats()) {
            System.out.println("    " + source);
        }
    }

    public static void main(String[] args) {
        Problem5 analytics = new Problem5();

        // Simulate page view events
        String[][] events = {
            {"/article/breaking-news", "user_123", "google"},
            {"/article/breaking-news", "user_456", "facebook"},
            {"/article/breaking-news", "user_789", "google"},
            {"/article/breaking-news", "user_101", "direct"},
            {"/article/breaking-news", "user_123", "google"}, // repeat visitor
            {"/sports/championship", "user_201", "google"},
            {"/sports/championship", "user_202", "direct"},
            {"/sports/championship", "user_203", "facebook"},
            {"/sports/championship", "user_204", "direct"},
            {"/tech/new-phone", "user_301", "google"},
            {"/tech/new-phone", "user_302", "other"},
            {"/weather/forecast", "user_401", "direct"},
        };

        for (String[] e : events) {
            analytics.processEvent(new PageViewEvent(e[0], e[1], e[2]));
        }

        // Simulate higher volume for breaking news
        for (int i = 0; i < 15000; i++) {
            analytics.processEvent(new PageViewEvent("/article/breaking-news", "user_" + (1000 + i), "google"));
        }
        for (int i = 0; i < 12000; i++) {
            analytics.processEvent(new PageViewEvent("/sports/championship", "user_" + (20000 + i), "direct"));
        }

        System.out.println("processEvent({url: \"/article/breaking-news\", userId: \"user_123\", source: \"google\"})");
        System.out.println("processEvent({url: \"/article/breaking-news\", userId: \"user_456\", source: \"facebook\"})");
        System.out.println("...");
        System.out.println();
        System.out.println("getDashboard() ->");
        analytics.getDashboard();
    }
}
