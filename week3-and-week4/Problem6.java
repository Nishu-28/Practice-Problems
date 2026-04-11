import java.util.Arrays;

public class Problem6 {

    static int[] linearSearch(int[] bands, int target) {
        int comps = 0;
        for (int i = 0; i < bands.length; i++) {
            comps++;
            if (bands[i] == target) return new int[] { i, comps };
        }
        return new int[] { -1, comps };
    }

    static int[] binaryInsertionPoint(int[] sortedBands, int target) {
        int lo = 0, hi = sortedBands.length, comps = 0;
        while (lo < hi) {
            comps++;
            int mid = (lo + hi) >>> 1;
            if (sortedBands[mid] < target) lo = mid + 1;
            else hi = mid;
        }
        return new int[] { lo, comps };
    }

    static int[] floorAndCeiling(int[] sortedBands, int target) {
        int lo = 0, hi = sortedBands.length - 1, comps = 0;
        int floorVal = Integer.MIN_VALUE, ceilVal = Integer.MAX_VALUE;
        while (lo <= hi) {
            comps++;
            int mid = (lo + hi) >>> 1;
            if (sortedBands[mid] == target) {
                return new int[] { sortedBands[mid], sortedBands[mid], comps };
            } else if (sortedBands[mid] < target) {
                floorVal = sortedBands[mid];
                lo = mid + 1;
            } else {
                ceilVal = sortedBands[mid];
                hi = mid - 1;
            }
        }
        return new int[] { floorVal, ceilVal, comps };
    }

    public static void main(String[] args) {
        int[] sorted = { 10, 25, 50, 100 };
        int target = 30;

        int[] lin = linearSearch(sorted, target);
        System.out.println("Sorted risks: " + Arrays.toString(sorted));
        System.out.println("Linear: threshold=" + target
                + (lin[0] == -1 ? " -> not found" : " -> index " + lin[0])
                + " (" + lin[1] + " comps)");

        int[] ins = binaryInsertionPoint(sorted, target);
        System.out.println("Binary insertion point: " + ins[0] + " (" + ins[1] + " comps)");

        int[] fc = floorAndCeiling(sorted, target);
        System.out.println("Binary floor(" + target + "): " + fc[0]
                + ", ceiling: " + fc[1] + " (" + fc[2] + " comps)");
    }
}
