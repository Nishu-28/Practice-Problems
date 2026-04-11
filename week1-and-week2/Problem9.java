import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Problem 9: Two-Sum Problem Variants for Financial Transactions
 *
 * Uses HashMap for O(1) complement lookup to find transaction pairs
 * summing to a target, detect duplicates, and solve K-Sum.
 */
public class Problem9 {

    /** Represents a financial transaction. */
    static class Transaction {
        int id;
        double amount;
        String merchant;
        String time;
        String account;

        Transaction(int id, double amount, String merchant, String time, String account) {
            this.id = id;
            this.amount = amount;
            this.merchant = merchant;
            this.time = time;
            this.account = account;
        }

        public String toString() {
            return "id:" + id;
        }
    }

    /**
     * Classic Two-Sum: finds pairs of transactions that sum to target.
     *
     * @param transactions list of transactions
     * @param target target sum
     * @return list of matching pairs
     */
    public static List<String> findTwoSum(List<Transaction> transactions, double target) {
        List<String> results = new ArrayList<>();
        HashMap<Double, Transaction> complementMap = new HashMap<>();

        for (Transaction t : transactions) {
            double complement = target - t.amount;

            if (complementMap.containsKey(t.amount)) {
                Transaction other = complementMap.get(t.amount);
                results.add("(" + other + ", " + t + ") // " + other.amount + " + " + t.amount);
            }

            complementMap.put(complement, t);
        }

        return results;
    }

    /**
     * Two-Sum with time window: finds pairs within a specified time window.
     *
     * @param transactions list of transactions
     * @param target target sum
     * @param windowMinutes time window in minutes
     * @return list of matching pairs within the window
     */
    public static List<String> findTwoSumWithTimeWindow(List<Transaction> transactions, double target, int windowMinutes) {
        List<String> results = new ArrayList<>();

        // Parse time to minutes for comparison
        HashMap<Double, List<Transaction>> complementMap = new HashMap<>();

        for (Transaction t : transactions) {
            int tMinutes = parseTimeToMinutes(t.time);
            double complement = target - t.amount;

            if (complementMap.containsKey(t.amount)) {
                for (Transaction other : complementMap.get(t.amount)) {
                    int otherMinutes = parseTimeToMinutes(other.time);
                    if (Math.abs(tMinutes - otherMinutes) <= windowMinutes) {
                        results.add("(" + other + ", " + t + ") // " + other.amount + " + " + t.amount
                                + " within " + Math.abs(tMinutes - otherMinutes) + " min");
                    }
                }
            }

            complementMap.computeIfAbsent(complement, k -> new ArrayList<>()).add(t);
        }

        return results;
    }

    /**
     * Detects duplicate transactions: same amount, same merchant, different accounts.
     *
     * @param transactions list of transactions
     * @return list of detected duplicates
     */
    public static List<String> detectDuplicates(List<Transaction> transactions) {
        List<String> results = new ArrayList<>();

        // Key: "amount|merchant" -> list of transactions
        HashMap<String, List<Transaction>> groupMap = new HashMap<>();

        for (Transaction t : transactions) {
            String key = t.amount + "|" + t.merchant;
            groupMap.computeIfAbsent(key, k -> new ArrayList<>()).add(t);
        }

        for (Map.Entry<String, List<Transaction>> entry : groupMap.entrySet()) {
            List<Transaction> group = entry.getValue();
            if (group.size() > 1) {
                // Check for different accounts
                HashMap<String, List<Transaction>> byAccount = new HashMap<>();
                for (Transaction t : group) {
                    byAccount.computeIfAbsent(t.account, k -> new ArrayList<>()).add(t);
                }
                if (byAccount.size() > 1) {
                    StringBuilder sb = new StringBuilder();
                    sb.append("{amount:").append(group.get(0).amount);
                    sb.append(", merchant:\"").append(group.get(0).merchant).append("\"");
                    sb.append(", accounts:").append(byAccount.keySet()).append("}");
                    results.add(sb.toString());
                }
            }
        }

        return results;
    }

    /**
     * K-Sum: finds K transactions that sum to target using recursive approach.
     *
     * @param transactions list of transactions
     * @param k number of transactions
     * @param target target sum
     * @return list of matching K-tuples
     */
    public static List<String> findKSum(List<Transaction> transactions, int k, double target) {
        List<String> results = new ArrayList<>();
        List<Transaction> current = new ArrayList<>();
        findKSumHelper(transactions, k, target, 0, current, results);
        return results;
    }

    private static void findKSumHelper(List<Transaction> transactions, int k, double target,
                                        int startIndex, List<Transaction> current, List<String> results) {
        if (k == 2) {
            // Base case: two-sum with HashMap
            HashMap<Double, Transaction> complementMap = new HashMap<>();
            for (int i = startIndex; i < transactions.size(); i++) {
                Transaction t = transactions.get(i);
                double complement = target - t.amount;

                if (complementMap.containsKey(t.amount)) {
                    List<Transaction> combo = new ArrayList<>(current);
                    combo.add(complementMap.get(t.amount));
                    combo.add(t);

                    StringBuilder sb = new StringBuilder("(");
                    double sum = 0;
                    for (int j = 0; j < combo.size(); j++) {
                        if (j > 0) sb.append(", ");
                        sb.append(combo.get(j));
                        sum += combo.get(j).amount;
                    }
                    sb.append(") // ");
                    for (int j = 0; j < combo.size(); j++) {
                        if (j > 0) sb.append("+");
                        sb.append((int) combo.get(j).amount);
                    }
                    results.add(sb.toString());
                }
                complementMap.put(complement, t);
            }
            return;
        }

        for (int i = startIndex; i <= transactions.size() - k; i++) {
            current.add(transactions.get(i));
            findKSumHelper(transactions, k - 1, target - transactions.get(i).amount,
                    i + 1, current, results);
            current.remove(current.size() - 1);
        }
    }

    /** Parses "HH:MM" to total minutes. */
    private static int parseTimeToMinutes(String time) {
        String[] parts = time.split(":");
        return Integer.parseInt(parts[0]) * 60 + Integer.parseInt(parts[1]);
    }

    public static void main(String[] args) {
        List<Transaction> transactions = new ArrayList<>();
        transactions.add(new Transaction(1, 500, "Store A", "10:00", "acc1"));
        transactions.add(new Transaction(2, 300, "Store B", "10:15", "acc1"));
        transactions.add(new Transaction(3, 200, "Store C", "10:30", "acc1"));
        transactions.add(new Transaction(4, 500, "Store A", "10:45", "acc2")); // duplicate
        transactions.add(new Transaction(5, 150, "Store D", "11:30", "acc1"));
        transactions.add(new Transaction(6, 350, "Store E", "10:20", "acc1"));

        // Classic Two-Sum
        System.out.println("findTwoSum(target=500) ->");
        for (String result : findTwoSum(transactions, 500)) {
            System.out.println("  " + result);
        }

        // Two-Sum with time window (60 minutes)
        System.out.println("\nfindTwoSumWithTimeWindow(target=500, window=60min) ->");
        for (String result : findTwoSumWithTimeWindow(transactions, 500, 60)) {
            System.out.println("  " + result);
        }

        // Detect duplicates
        System.out.println("\ndetectDuplicates() ->");
        for (String result : detectDuplicates(transactions)) {
            System.out.println("  " + result);
        }

        // K-Sum (3 transactions summing to 1000)
        System.out.println("\nfindKSum(k=3, target=1000) ->");
        for (String result : findKSum(transactions, 3, 1000)) {
            System.out.println("  " + result);
        }
    }
}
