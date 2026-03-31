import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Problem 2: E-commerce Flash Sale Inventory Manager
 *
 * Uses HashMap for O(1) stock lookup, synchronized operations
 * for thread safety, and LinkedHashMap for FIFO waiting list.
 */
public class Problem2 {

    /** Maps productId -> stock count. */
    private HashMap<String, Integer> stockMap;

    /** Waiting list: maps userId -> productId (FIFO order preserved). */
    private LinkedHashMap<Integer, String> waitingList;

    /** Tracks position in waiting list per product. */
    private HashMap<String, Integer> waitingListCount;

    public Problem2() {
        stockMap = new HashMap<>();
        waitingList = new LinkedHashMap<>();
        waitingListCount = new HashMap<>();
    }

    /**
     * Adds a product with initial stock.
     *
     * @param productId product identifier
     * @param stock initial stock count
     */
    public void addProduct(String productId, int stock) {
        stockMap.put(productId, stock);
        waitingListCount.put(productId, 0);
    }

    /**
     * Checks current stock for a product.
     *
     * @param productId product identifier
     * @return available stock, or -1 if product not found
     */
    public synchronized int checkStock(String productId) {
        return stockMap.getOrDefault(productId, -1);
    }

    /**
     * Attempts to purchase a product. If out of stock,
     * adds the user to a waiting list.
     *
     * @param productId product to purchase
     * @param userId buyer's user ID
     * @return result message
     */
    public synchronized String purchaseItem(String productId, int userId) {
        if (!stockMap.containsKey(productId)) {
            return "Product not found: " + productId;
        }

        int currentStock = stockMap.get(productId);

        if (currentStock > 0) {
            stockMap.put(productId, currentStock - 1);
            return "Success, " + (currentStock - 1) + " units remaining";
        } else {
            waitingList.put(userId, productId);
            int position = waitingListCount.get(productId) + 1;
            waitingListCount.put(productId, position);
            return "Added to waiting list, position #" + position;
        }
    }

    /**
     * Restocks a product and fulfills waiting list orders.
     *
     * @param productId product to restock
     * @param quantity units to add
     */
    public synchronized void restock(String productId, int quantity) {
        int currentStock = stockMap.getOrDefault(productId, 0);
        stockMap.put(productId, currentStock + quantity);
        System.out.println("Restocked " + productId + " with " + quantity + " units.");
    }

    /**
     * Returns the current waiting list size for a product.
     *
     * @param productId product identifier
     * @return number of users waiting
     */
    public int getWaitingListSize(String productId) {
        return waitingListCount.getOrDefault(productId, 0);
    }

    public static void main(String[] args) {
        Problem2 manager = new Problem2();

        // Add product with 100 units
        manager.addProduct("IPHONE15_256GB", 100);

        // Check stock
        System.out.println("checkStock(\"IPHONE15_256GB\") → " + manager.checkStock("IPHONE15_256GB") + " units available");

        // Purchase items
        System.out.println("purchaseItem(userId=12345) → " + manager.purchaseItem("IPHONE15_256GB", 12345));
        System.out.println("purchaseItem(userId=67890) → " + manager.purchaseItem("IPHONE15_256GB", 67890));

        // Simulate buying remaining stock
        for (int i = 1; i <= 98; i++) {
            manager.purchaseItem("IPHONE15_256GB", 100000 + i);
        }

        System.out.println("Stock after 100 purchases: " + manager.checkStock("IPHONE15_256GB"));

        // Next purchase goes to waiting list
        System.out.println("purchaseItem(userId=99999) → " + manager.purchaseItem("IPHONE15_256GB", 99999));

        // Concurrent simulation with threads
        System.out.println("\n--- Concurrent Purchase Simulation ---");
        manager.addProduct("PS5_DIGITAL", 5);

        Thread[] threads = new Thread[10];
        for (int i = 0; i < 10; i++) {
            final int userId = 200 + i;
            threads[i] = new Thread(() -> {
                String result = manager.purchaseItem("PS5_DIGITAL", userId);
                System.out.println("User " + userId + ": " + result);
            });
        }

        for (Thread t : threads) t.start();
        for (Thread t : threads) {
            try { t.join(); } catch (InterruptedException e) { }
        }

        System.out.println("PS5 remaining stock: " + manager.checkStock("PS5_DIGITAL"));
        System.out.println("PS5 waiting list size: " + manager.getWaitingListSize("PS5_DIGITAL"));
    }
}
