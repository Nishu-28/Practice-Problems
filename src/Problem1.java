import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Problem 1: Social Media Username Availability Checker
 *
 * Uses HashMap for O(1) username lookup, frequency tracking
 * of attempted usernames, and suggestion generation.
 */
public class Problem1 {

    /** Maps username -> userId for registered users. */
    private HashMap<String, Integer> registeredUsers;

    /** Tracks how many times each username was attempted. */
    private HashMap<String, Integer> attemptFrequency;

    /** Auto-incrementing user ID counter. */
    private int nextUserId;

    public Problem1() {
        registeredUsers = new HashMap<>();
        attemptFrequency = new HashMap<>();
        nextUserId = 1;
    }

    /**
     * Checks if a username is available.
     * Tracks each attempt in the frequency map.
     *
     * @param username the username to check
     * @return true if available, false if taken
     */
    public boolean checkAvailability(String username) {
        attemptFrequency.put(username, attemptFrequency.getOrDefault(username, 0) + 1);
        return !registeredUsers.containsKey(username);
    }

    /**
     * Registers a username if available.
     *
     * @param username the username to register
     * @return the assigned userId, or -1 if taken
     */
    public int register(String username) {
        if (registeredUsers.containsKey(username)) {
            return -1;
        }
        int userId = nextUserId++;
        registeredUsers.put(username, userId);
        return userId;
    }

    /**
     * Suggests alternative usernames by appending numbers
     * and inserting separators.
     *
     * @param username the taken username
     * @return list of available alternatives
     */
    public List<String> suggestAlternatives(String username) {
        List<String> suggestions = new ArrayList<>();

        // Append numbers
        for (int i = 1; suggestions.size() < 5; i++) {
            String candidate = username + i;
            if (!registeredUsers.containsKey(candidate)) {
                suggestions.add(candidate);
            }
        }

        // Try dot separator variant
        if (username.contains("_")) {
            String dotVariant = username.replace('_', '.');
            if (!registeredUsers.containsKey(dotVariant)) {
                suggestions.add(dotVariant);
            }
        }

        // Try underscore separator variant
        if (username.contains(".")) {
            String underscoreVariant = username.replace('.', '_');
            if (!registeredUsers.containsKey(underscoreVariant)) {
                suggestions.add(underscoreVariant);
            }
        }

        return suggestions;
    }

    /**
     * Returns the most attempted username.
     *
     * @return the username with the highest attempt count
     */
    public String getMostAttempted() {
        String mostAttempted = null;
        int maxAttempts = 0;

        for (Map.Entry<String, Integer> entry : attemptFrequency.entrySet()) {
            if (entry.getValue() > maxAttempts) {
                maxAttempts = entry.getValue();
                mostAttempted = entry.getKey();
            }
        }
        return mostAttempted;
    }

    /**
     * Returns the attempt count for the most attempted username.
     *
     * @return the highest attempt count
     */
    public int getMostAttemptedCount() {
        int maxAttempts = 0;
        for (int count : attemptFrequency.values()) {
            if (count > maxAttempts) {
                maxAttempts = count;
            }
        }
        return maxAttempts;
    }

    public static void main(String[] args) {
        Problem1 checker = new Problem1();

        // Register some users
        checker.register("john_doe");
        checker.register("admin");
        checker.register("jane_smith");

        // Check availability
        System.out.println("checkAvailability(\"john_doe\") → " + !checker.checkAvailability("john_doe") + " (already taken)");
        System.out.println("checkAvailability(\"jane_smith\") → " + checker.checkAvailability("new_user") + " (available)");

        // Simulate multiple attempts on "admin"
        for (int i = 0; i < 10542; i++) {
            checker.checkAvailability("admin");
        }

        // Suggest alternatives
        List<String> suggestions = checker.suggestAlternatives("john_doe");
        System.out.println("suggestAlternatives(\"john_doe\") → " + suggestions);

        // Most attempted
        System.out.println("getMostAttempted() → \"" + checker.getMostAttempted()
                + "\" (" + checker.getMostAttemptedCount() + " attempts)");
    }
}
