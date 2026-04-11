import java.util.ArrayList;
import java.util.List;

public class Problem1 {

    static class Transaction {
        String id;
        double fee;
        String timestamp;

        Transaction(String id, double fee, String timestamp) {
            this.id = id;
            this.fee = fee;
            this.timestamp = timestamp;
        }

        @Override
        public String toString() {
            return id + ":" + fee + "@" + timestamp;
        }
    }

    static int[] bubbleSortByFee(ArrayList<Transaction> txs) {
        int n = txs.size();
        int passes = 0, swaps = 0;
        for (int i = 0; i < n - 1; i++) {
            passes++;
            boolean swapped = false;
            for (int j = 0; j < n - 1 - i; j++) {
                if (txs.get(j).fee > txs.get(j + 1).fee) {
                    Transaction tmp = txs.get(j);
                    txs.set(j, txs.get(j + 1));
                    txs.set(j + 1, tmp);
                    swaps++;
                    swapped = true;
                }
            }
            if (!swapped) break;
        }
        return new int[] { passes, swaps };
    }

    static void insertionSortByFeeAndTimestamp(ArrayList<Transaction> txs) {
        for (int i = 1; i < txs.size(); i++) {
            Transaction key = txs.get(i);
            int j = i - 1;
            while (j >= 0 && compareFeeTs(txs.get(j), key) > 0) {
                txs.set(j + 1, txs.get(j));
                j--;
            }
            txs.set(j + 1, key);
        }
    }

    static int compareFeeTs(Transaction a, Transaction b) {
        if (a.fee != b.fee) return Double.compare(a.fee, b.fee);
        return a.timestamp.compareTo(b.timestamp);
    }

    static List<Transaction> flagHighFeeOutliers(ArrayList<Transaction> txs) {
        List<Transaction> outliers = new ArrayList<>();
        for (Transaction t : txs) {
            if (t.fee > 50.0) outliers.add(t);
        }
        return outliers;
    }

    public static void main(String[] args) {
        ArrayList<Transaction> txs = new ArrayList<>();
        txs.add(new Transaction("id1", 10.5, "10:00"));
        txs.add(new Transaction("id2", 25.0, "09:30"));
        txs.add(new Transaction("id3", 5.0, "10:15"));

        ArrayList<Transaction> forBubble = new ArrayList<>(txs);
        int[] stats = bubbleSortByFee(forBubble);
        System.out.println("BubbleSort (fees): " + forBubble
                + " // " + stats[0] + " passes, " + stats[1] + " swaps");

        ArrayList<Transaction> forInsertion = new ArrayList<>(txs);
        insertionSortByFeeAndTimestamp(forInsertion);
        System.out.println("InsertionSort (fee+ts): " + forInsertion);

        List<Transaction> outliers = flagHighFeeOutliers(forBubble);
        System.out.println("High-fee outliers: " + (outliers.isEmpty() ? "none" : outliers));
    }
}
