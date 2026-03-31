/**
 * Problem 8: Parking Lot Management with Open Addressing
 *
 * Array-based hash table using linear probing for collision
 * resolution. Tracks entry/exit times for billing and
 * generates occupancy statistics.
 */
public class Problem8 {

    /** Spot status constants. */
    static final int EMPTY = 0;
    static final int OCCUPIED = 1;
    static final int DELETED = 2;

    /** Represents a parked vehicle entry. */
    static class ParkingEntry {
        String licensePlate;
        long entryTime;
        int status;

        ParkingEntry() {
            this.status = EMPTY;
        }
    }

    /** Array-based hash table for parking spots. */
    private ParkingEntry[] spots;

    /** Total number of spots. */
    private int capacity;

    /** Current occupancy count. */
    private int occupiedCount;

    /** Statistics tracking. */
    private int totalProbes;
    private int totalParkOperations;
    private double totalRevenue;
    private int[] hourlyOccupancy; // 24 hours

    /** Rate per hour. */
    private double ratePerHour;

    public Problem8(int capacity, double ratePerHour) {
        this.capacity = capacity;
        this.spots = new ParkingEntry[capacity];
        for (int i = 0; i < capacity; i++) {
            spots[i] = new ParkingEntry();
        }
        this.occupiedCount = 0;
        this.totalProbes = 0;
        this.totalParkOperations = 0;
        this.totalRevenue = 0;
        this.ratePerHour = ratePerHour;
        this.hourlyOccupancy = new int[24];
    }

    /**
     * Hash function: maps license plate to preferred spot.
     *
     * @param licensePlate the vehicle's license plate
     * @return preferred spot index
     */
    private int hash(String licensePlate) {
        int hash = 0;
        for (char c : licensePlate.toCharArray()) {
            hash = (hash * 31 + c) % capacity;
        }
        return Math.abs(hash);
    }

    /**
     * Parks a vehicle using linear probing.
     *
     * @param licensePlate the vehicle's license plate
     * @return result message with spot number and probe count
     */
    public String parkVehicle(String licensePlate) {
        if (occupiedCount >= capacity) {
            return "Parking lot is full. Cannot park " + licensePlate;
        }

        int preferredSpot = hash(licensePlate);
        int probes = 0;
        int spot = preferredSpot;

        StringBuilder probeTrace = new StringBuilder();

        while (spots[spot].status == OCCUPIED) {
            if (probes > 0 || spot != preferredSpot) {
                probeTrace.append("Spot #").append(spot).append("... occupied... ");
            }
            probes++;
            spot = (spot + 1) % capacity; // Linear probing
        }

        spots[spot].licensePlate = licensePlate;
        spots[spot].entryTime = System.currentTimeMillis();
        spots[spot].status = OCCUPIED;
        occupiedCount++;
        totalProbes += probes;
        totalParkOperations++;

        // Track hourly occupancy
        int hour = (int)((System.currentTimeMillis() / 3600000) % 24);
        hourlyOccupancy[hour] = Math.max(hourlyOccupancy[hour], occupiedCount);

        String result;
        if (probes == 0) {
            result = "Assigned spot #" + spot + " (" + probes + " probes)";
        } else {
            result = probeTrace + "Spot #" + spot + " (" + probes + " probes)";
        }
        return result;
    }

    /**
     * Removes a vehicle and calculates parking fee.
     *
     * @param licensePlate the vehicle's license plate
     * @return result message with duration and fee
     */
    public String exitVehicle(String licensePlate) {
        int preferredSpot = hash(licensePlate);
        int spot = preferredSpot;
        int probes = 0;

        while (probes < capacity) {
            if (spots[spot].status == EMPTY) {
                return "Vehicle " + licensePlate + " not found.";
            }
            if (spots[spot].status == OCCUPIED && licensePlate.equals(spots[spot].licensePlate)) {
                long duration = System.currentTimeMillis() - spots[spot].entryTime;
                double hours = Math.max(duration / 3600000.0, 0.25); // minimum 15 min
                double fee = Math.round(hours * ratePerHour * 100.0) / 100.0;
                totalRevenue += fee;

                spots[spot].status = DELETED;
                spots[spot].licensePlate = null;
                occupiedCount--;

                long minutes = (duration / 60000) % 60;
                long hrs = duration / 3600000;
                return String.format("Spot #%d freed, Duration: %dh %dm, Fee: $%.2f", spot, hrs, minutes, fee);
            }
            probes++;
            spot = (spot + 1) % capacity;
        }
        return "Vehicle " + licensePlate + " not found.";
    }

    /**
     * Returns parking statistics.
     *
     * @return formatted statistics string
     */
    public String getStatistics() {
        double occupancyRate = (occupiedCount * 100.0) / capacity;
        double avgProbes = totalParkOperations > 0 ? (double) totalProbes / totalParkOperations : 0;
        return String.format("Occupancy: %.0f%%, Avg Probes: %.1f, Total Revenue: $%.2f",
                occupancyRate, avgProbes, totalRevenue);
    }

    public static void main(String[] args) {
        Problem8 parkingLot = new Problem8(500, 5.50);

        // Park vehicles (demonstrating collision and probing)
        System.out.println("parkVehicle(\"ABC-1234\") -> " + parkingLot.parkVehicle("ABC-1234"));
        System.out.println("parkVehicle(\"ABC-1235\") -> " + parkingLot.parkVehicle("ABC-1235"));
        System.out.println("parkVehicle(\"XYZ-9999\") -> " + parkingLot.parkVehicle("XYZ-9999"));
        System.out.println("parkVehicle(\"DEF-5678\") -> " + parkingLot.parkVehicle("DEF-5678"));
        System.out.println("parkVehicle(\"GHI-1111\") -> " + parkingLot.parkVehicle("GHI-1111"));

        // Exit a vehicle
        System.out.println("\nexitVehicle(\"ABC-1234\") -> " + parkingLot.exitVehicle("ABC-1234"));

        // Park more to fill spots
        System.out.println("\n--- Filling parking lot ---");
        for (int i = 0; i < 200; i++) {
            parkingLot.parkVehicle("AUTO-" + String.format("%04d", i));
        }

        // Statistics
        System.out.println("\ngetStatistics() -> " + parkingLot.getStatistics());

        // Try to find non-existent vehicle
        System.out.println("\nexitVehicle(\"ZZZ-0000\") -> " + parkingLot.exitVehicle("ZZZ-0000"));
    }
}
