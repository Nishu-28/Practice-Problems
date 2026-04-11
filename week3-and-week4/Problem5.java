import java.util.Arrays;

public class Problem5 {

    static class SearchResult {
        int index;
        int comparisons;
        int count;

        SearchResult(int index, int comparisons, int count) {
            this.index = index;
            this.comparisons = comparisons;
            this.count = count;
        }
    }

    static SearchResult linearFirst(String[] logs, String target) {
        int comps = 0;
        for (int i = 0; i < logs.length; i++) {
            comps++;
            if (logs[i].equals(target)) return new SearchResult(i, comps, -1);
        }
        return new SearchResult(-1, comps, -1);
    }

    static SearchResult linearLast(String[] logs, String target) {
        int comps = 0;
        int last = -1;
        for (int i = 0; i < logs.length; i++) {
            comps++;
            if (logs[i].equals(target)) last = i;
        }
        return new SearchResult(last, comps, -1);
    }

    static SearchResult binarySearch(String[] sortedLogs, String target) {
        int lo = 0, hi = sortedLogs.length - 1, comps = 0;
        int foundAt = -1;
        while (lo <= hi) {
            comps++;
            int mid = (lo + hi) >>> 1;
            int cmp = sortedLogs[mid].compareTo(target);
            if (cmp == 0) { foundAt = mid; break; }
            if (cmp < 0) lo = mid + 1;
            else hi = mid - 1;
        }
        if (foundAt == -1) return new SearchResult(-1, comps, 0);

        int first = foundAt, last = foundAt;
        while (first > 0 && sortedLogs[first - 1].equals(target)) first--;
        while (last < sortedLogs.length - 1 && sortedLogs[last + 1].equals(target)) last++;
        return new SearchResult(first, comps, last - first + 1);
    }

    public static void main(String[] args) {
        String[] logs = { "accB", "accA", "accB", "accC" };
        System.out.println("Logs (unsorted): " + Arrays.toString(logs));

        SearchResult lr = linearFirst(logs, "accB");
        System.out.println("Linear first accB: index " + lr.index
                + " (" + lr.comparisons + " comparisons)");

        String[] sorted = logs.clone();
        Arrays.sort(sorted);
        System.out.println("Sorted logs: " + Arrays.toString(sorted));

        SearchResult br = binarySearch(sorted, "accB");
        System.out.println("Binary accB: index " + br.index
                + " (" + br.comparisons + " comparisons), count=" + br.count);
    }
}
