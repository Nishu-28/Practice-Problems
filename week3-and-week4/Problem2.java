import java.util.Arrays;

public class Problem2 {

    static class Client {
        String id;
        int riskScore;
        double accountBalance;

        Client(String id, int riskScore, double accountBalance) {
            this.id = id;
            this.riskScore = riskScore;
            this.accountBalance = accountBalance;
        }

        @Override
        public String toString() {
            return id + ":" + riskScore;
        }
    }

    static int bubbleSortAsc(Client[] clients) {
        int swaps = 0;
        int n = clients.length;
        for (int i = 0; i < n - 1; i++) {
            boolean swapped = false;
            for (int j = 0; j < n - 1 - i; j++) {
                if (clients[j].riskScore > clients[j + 1].riskScore) {
                    Client tmp = clients[j];
                    clients[j] = clients[j + 1];
                    clients[j + 1] = tmp;
                    swaps++;
                    swapped = true;
                }
            }
            if (!swapped) break;
        }
        return swaps;
    }

    static void insertionSortDesc(Client[] clients) {
        for (int i = 1; i < clients.length; i++) {
            Client key = clients[i];
            int j = i - 1;
            while (j >= 0 && compareDesc(clients[j], key) > 0) {
                clients[j + 1] = clients[j];
                j--;
            }
            clients[j + 1] = key;
        }
    }

    static int compareDesc(Client a, Client b) {
        if (a.riskScore != b.riskScore) return Integer.compare(b.riskScore, a.riskScore);
        return Double.compare(b.accountBalance, a.accountBalance);
    }

    static Client[] topNRisks(Client[] sortedDesc, int n) {
        return Arrays.copyOf(sortedDesc, Math.min(n, sortedDesc.length));
    }

    public static void main(String[] args) {
        Client[] input = {
                new Client("clientC", 80, 50000),
                new Client("clientA", 20, 12000),
                new Client("clientB", 50, 30000)
        };

        Client[] asc = input.clone();
        int swaps = bubbleSortAsc(asc);
        System.out.println("Bubble (asc): " + Arrays.toString(asc) + " // Swaps: " + swaps);

        Client[] desc = input.clone();
        insertionSortDesc(desc);
        System.out.println("Insertion (desc): " + Arrays.toString(desc));

        Client[] top3 = topNRisks(desc, 3);
        System.out.println("Top 3 risks: " + Arrays.toString(top3));
    }
}
