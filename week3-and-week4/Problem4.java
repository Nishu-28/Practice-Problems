import java.util.Arrays;
import java.util.Random;

public class Problem4 {

    static class Asset {
        String symbol;
        double returnRate;
        double volatility;
        int originalIndex;

        Asset(String symbol, double returnRate, double volatility) {
            this.symbol = symbol;
            this.returnRate = returnRate;
            this.volatility = volatility;
        }

        @Override
        public String toString() {
            return symbol + ":" + returnRate + "%";
        }
    }

    static void mergeSortByReturn(Asset[] arr, int l, int r) {
        if (l >= r) return;
        int m = (l + r) >>> 1;
        mergeSortByReturn(arr, l, m);
        mergeSortByReturn(arr, m + 1, r);
        mergeStable(arr, l, m, r);
    }

    static void mergeStable(Asset[] arr, int l, int m, int r) {
        Asset[] tmp = new Asset[r - l + 1];
        int i = l, j = m + 1, k = 0;
        while (i <= m && j <= r) {
            if (arr[i].returnRate <= arr[j].returnRate) tmp[k++] = arr[i++];
            else tmp[k++] = arr[j++];
        }
        while (i <= m) tmp[k++] = arr[i++];
        while (j <= r) tmp[k++] = arr[j++];
        System.arraycopy(tmp, 0, arr, l, tmp.length);
    }

    static final Random RNG = new Random(42);

    static void quickSortDesc(Asset[] arr, int lo, int hi) {
        if (lo >= hi) return;
        int pivotIdx = medianOfThree(arr, lo, hi);
        Asset tmp = arr[pivotIdx]; arr[pivotIdx] = arr[hi]; arr[hi] = tmp;
        int p = partitionDesc(arr, lo, hi);
        quickSortDesc(arr, lo, p - 1);
        quickSortDesc(arr, p + 1, hi);
    }

    static int medianOfThree(Asset[] arr, int lo, int hi) {
        int mid = (lo + hi) >>> 1;
        double a = arr[lo].returnRate, b = arr[mid].returnRate, c = arr[hi].returnRate;
        if ((a >= b && a <= c) || (a <= b && a >= c)) return lo;
        if ((b >= a && b <= c) || (b <= a && b >= c)) return mid;
        return hi;
    }

    static int partitionDesc(Asset[] arr, int lo, int hi) {
        Asset pivot = arr[hi];
        int i = lo - 1;
        for (int j = lo; j < hi; j++) {
            if (compareReturnDescVolAsc(arr[j], pivot) < 0) {
                i++;
                Asset t = arr[i]; arr[i] = arr[j]; arr[j] = t;
            }
        }
        Asset t = arr[i + 1]; arr[i + 1] = arr[hi]; arr[hi] = t;
        return i + 1;
    }

    static int compareReturnDescVolAsc(Asset a, Asset b) {
        if (a.returnRate != b.returnRate) return Double.compare(b.returnRate, a.returnRate);
        return Double.compare(a.volatility, b.volatility);
    }

    public static void main(String[] args) {
        Asset[] input = {
                new Asset("AAPL", 12.0, 0.18),
                new Asset("TSLA", 8.0, 0.35),
                new Asset("GOOG", 15.0, 0.22)
        };

        Asset[] merge = input.clone();
        mergeSortByReturn(merge, 0, merge.length - 1);
        System.out.println("Merge: " + Arrays.toString(merge));

        Asset[] quick = input.clone();
        quickSortDesc(quick, 0, quick.length - 1);
        System.out.println("Quick (desc): " + Arrays.toString(quick));
    }
}
