import java.util.Arrays;

public class Problem3 {

    static class Trade {
        String id;
        int volume;

        Trade(String id, int volume) {
            this.id = id;
            this.volume = volume;
        }

        @Override
        public String toString() {
            return id + ":" + volume;
        }
    }

    static void mergeSortAsc(Trade[] arr, int l, int r) {
        if (l >= r) return;
        int m = (l + r) >>> 1;
        mergeSortAsc(arr, l, m);
        mergeSortAsc(arr, m + 1, r);
        merge(arr, l, m, r);
    }

    static void merge(Trade[] arr, int l, int m, int r) {
        Trade[] tmp = new Trade[r - l + 1];
        int i = l, j = m + 1, k = 0;
        while (i <= m && j <= r) {
            if (arr[i].volume <= arr[j].volume) tmp[k++] = arr[i++];
            else tmp[k++] = arr[j++];
        }
        while (i <= m) tmp[k++] = arr[i++];
        while (j <= r) tmp[k++] = arr[j++];
        System.arraycopy(tmp, 0, arr, l, tmp.length);
    }

    static void quickSortDesc(Trade[] arr, int lo, int hi) {
        if (lo >= hi) return;
        int p = partitionDesc(arr, lo, hi);
        quickSortDesc(arr, lo, p - 1);
        quickSortDesc(arr, p + 1, hi);
    }

    static int partitionDesc(Trade[] arr, int lo, int hi) {
        int pivot = arr[hi].volume;
        int i = lo - 1;
        for (int j = lo; j < hi; j++) {
            if (arr[j].volume > pivot) {
                i++;
                Trade t = arr[i]; arr[i] = arr[j]; arr[j] = t;
            }
        }
        Trade t = arr[i + 1]; arr[i + 1] = arr[hi]; arr[hi] = t;
        return i + 1;
    }

    static Trade[] mergeTwoSorted(Trade[] a, Trade[] b) {
        Trade[] out = new Trade[a.length + b.length];
        int i = 0, j = 0, k = 0;
        while (i < a.length && j < b.length) {
            if (a[i].volume <= b[j].volume) out[k++] = a[i++];
            else out[k++] = b[j++];
        }
        while (i < a.length) out[k++] = a[i++];
        while (j < b.length) out[k++] = b[j++];
        return out;
    }

    static long totalVolume(Trade[] trades) {
        long total = 0;
        for (Trade t : trades) total += t.volume;
        return total;
    }

    public static void main(String[] args) {
        Trade[] input = {
                new Trade("trade3", 500),
                new Trade("trade1", 100),
                new Trade("trade2", 300)
        };

        Trade[] merged = input.clone();
        mergeSortAsc(merged, 0, merged.length - 1);
        System.out.println("MergeSort: " + Arrays.toString(merged) + " // Stable");

        Trade[] quick = input.clone();
        quickSortDesc(quick, 0, quick.length - 1);
        System.out.println("QuickSort (desc): " + Arrays.toString(quick));

        Trade[] morning = { new Trade("m1", 100), new Trade("m2", 400) };
        Trade[] afternoon = { new Trade("a1", 150), new Trade("a2", 250) };
        Trade[] combined = mergeTwoSorted(morning, afternoon);
        System.out.println("Merged morning+afternoon total: " + totalVolume(combined));
    }
}
