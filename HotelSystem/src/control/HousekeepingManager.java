//Author : Lee Wei Zhong
package control;

import entity.Room;
import entity.HousekeepingLog;
import entity.StatusRecord;
import adt.LinkedStack;
import adt.ArrayList;
import java.time.format.DateTimeFormatter;

public class HousekeepingManager {

    private static final String[] SEQUENCE = {
        "Dirty", "Cleaning In Progress", "Inspected", "Clean"
    };

    private HousekeepingLog[] logs;
    private int logCount;
    private String lastError;

    public HousekeepingManager() {
        logs = new HousekeepingLog[10];
        logCount = 0;
        lastError = "";
    }

    // Build Logs From Shared Room List (RoomDAO)
    public HousekeepingManager(ArrayList<Room> rooms) {
        logs = new HousekeepingLog[10];
        logCount = 0;
        lastError = "";
        for (int i = 0; i < rooms.size(); i++) {
            registerRoom(rooms.get(i));
        }
    }

    public String getLastError() { return lastError; }

    // Ensure Log Capacity
    private void ensureCapacity() {
        if (logCount == logs.length) {
            HousekeepingLog[] bigger = new HousekeepingLog[logs.length * 2];
            for (int i = 0; i < logs.length; i++) bigger[i] = logs[i];
            logs = bigger;
        }
    }

    // Get Index Of Status In Sequence
    private int indexOfStatus(String status) {
        for (int i = 0; i < SEQUENCE.length; i++) {
            if (SEQUENCE[i].equalsIgnoreCase(status)) return i;
        }
        return -1;
    }

    // Find Room's Housekeeping Log
    private HousekeepingLog findLog(String roomNo) {
        for (int i = 0; i < logCount; i++) {
            if (logs[i].getRoom().getRoomNo().equalsIgnoreCase(roomNo)) return logs[i];
        }
        return null;
    }

    public boolean roomExists(String roomNo) { return findLog(roomNo) != null; }

    public String getCurrentStatus(String roomNo) {
        HousekeepingLog log = findLog(roomNo);
        return (log == null) ? null : log.getRoom().getCurrentStatus();
    }

    // Get Next Status In Sequence (null if already at final stage)
    public String getNextStatus(String roomNo) {
        HousekeepingLog log = findLog(roomNo);
        if (log == null) return null;
        int currentIndex = indexOfStatus(log.getRoom().getCurrentStatus());
        if (currentIndex == -1 || currentIndex == SEQUENCE.length - 1) return null;
        return SEQUENCE[currentIndex + 1];
    }

    public static String[] getValidStatuses() { return SEQUENCE.clone(); }

    // Register Room Into Housekeeping Log
    public void registerRoom(Room room) {
        ensureCapacity();
        logs[logCount++] = new HousekeepingLog(room);
    }

    // Update Room Status
    public boolean updateStatus(String roomNo, String newStatus, String staffId) {
        HousekeepingLog log = findLog(roomNo);
        if (log == null) {
            lastError = "Room '" + roomNo + "' was not found in the system.";
            return false;
        }

        // Validate New Status Follows Current Status In Sequence
        String currentStatus = log.getRoom().getCurrentStatus();
        int currentIndex = indexOfStatus(currentStatus);
        int newIndex = indexOfStatus(newStatus);

        if (newIndex == -1 || newIndex != currentIndex + 1) {
            lastError = "Cannot change status from '" + currentStatus + "' to '" + newStatus + "'.";
            return false;
        }

        // Push New Status Onto History And Update Room
        log.getHistory().push(new StatusRecord(newStatus, staffId));
        log.getRoom().setCurrentStatus(newStatus);
        log.getRoom().updateOStatus();
        return true;
    }

    // Record Checkout (Room Becomes Dirty For New Cleaning Cycle)
    public void recordCheckoutDirty(String roomNo, String staffId) {
        HousekeepingLog log = findLog(roomNo);
        if (log != null) {
            log.getHistory().push(new StatusRecord("Dirty", staffId));
            log.getRoom().setCurrentStatus("Dirty");
        }
    }

    // Preview Previous Status Without Modifying History
    public String getPreviousStatus(String roomNo) {
        HousekeepingLog log = findLog(roomNo);
        if (log == null || log.getHistory().size() <= 1) return null;
        StatusRecord top = log.getHistory().pop();
        StatusRecord previous = log.getHistory().peek();
        log.getHistory().push(top);
        return previous.getStatus();
    }

    // Rollback Status
    public boolean rollbackStatus(String roomNo) {
        HousekeepingLog log = findLog(roomNo);
        if (log == null) {
            lastError = "Room '" + roomNo + "' was not found in the system.";
            return false;
        }

        // Block Rollback At Dirty (Start Of Cycle, Nothing Earlier To Restore)
        String currentStatus = log.getRoom().getCurrentStatus();
        if (currentStatus.equalsIgnoreCase("Dirty")) {
            lastError = "Room '" + roomNo + "' is at 'Dirty' - the start of this cycle, nothing to roll back to.";
            return false;
        }

        if (log.getHistory().size() <= 1) {
            lastError = "Room '" + roomNo + "' has no earlier status to roll back to.";
            return false;
        }

        // Pop Current Status And Restore Previous One
        log.getHistory().pop();
        StatusRecord previous = log.getHistory().peek();
        log.getRoom().setCurrentStatus(previous.getStatus());
        log.incrementRollbackCount();
        return true;
    }

    // Manually Start New Cleaning Cycle (Testing / Standalone Use)
    public boolean startNewCycle(String roomNo, String staffId) {
        HousekeepingLog log = findLog(roomNo);
        if (log == null) {
            lastError = "Room '" + roomNo + "' was not found in the system.";
            return false;
        }

        String currentStatus = log.getRoom().getCurrentStatus();
        if (!currentStatus.equalsIgnoreCase("Clean")) {
            lastError = "Room '" + roomNo + "' must be 'Clean' before starting a new cycle (currently '" + currentStatus + "').";
            return false;
        }

        log.getHistory().push(new StatusRecord("Dirty", staffId));
        log.getRoom().setCurrentStatus("Dirty");
        return true;
    }

    // Print Room Status History (Most Recent First, Non-Destructive)
    public boolean printHistory(String roomNo) {
        HousekeepingLog log = findLog(roomNo);
        if (log == null) {
            lastError = "Room '" + roomNo + "' was not found in the system.";
            return false;
        }

        System.out.println("\n==================================================");
        System.out.println("      STATUS HISTORY - ROOM " + roomNo);
        System.out.println("==================================================");

        System.out.printf("%-22s %-15s %-20s%n", "Status", "By", "Timestamp");
        System.out.println("--------------------------------------------------");

        LinkedStack<StatusRecord> original = log.getHistory();
        LinkedStack<StatusRecord> temp = new LinkedStack<>();

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        while (!original.isEmpty()) {
            StatusRecord r = original.pop();
            String formattedTime = r.getTimestamp().format(formatter);
            System.out.printf("%-22s %-15s %-20s%n", r.getStatus(), r.getStaffId(), formattedTime);
            temp.push(r);
        }
        while (!temp.isEmpty()) {
            original.push(temp.pop());
        }

        System.out.println("--------------------------------------------------");
        return true;
    }

    // Report: Room Status Summary (Filtered By Status And Room Type)
    public void generateRoomStatusReport(String statusFilter, String typeFilter) {
        System.out.println("\n==================================================");
        System.out.println("           ROOM STATUS SUMMARY");
        System.out.println("==================================================");

        // Search: Filter Rooms Matching Status AND Room Type
        HousekeepingLog[] filtered = new HousekeepingLog[logCount];
        int fCount = 0;
        for (int i = 0; i < logCount; i++) {
            String status = logs[i].getRoom().getCurrentStatus();
            String type = logs[i].getRoom().getRoomType();
            boolean statusMatch = statusFilter.equalsIgnoreCase("ALL") || status.equalsIgnoreCase(statusFilter);
            boolean typeMatch = typeFilter.equalsIgnoreCase("ALL") || type.equalsIgnoreCase(typeFilter);
            if (statusMatch && typeMatch) {
                filtered[fCount++] = logs[i];
            }
        }

        // Sort: Insertion Sort By Room Number (Ascending)
        for (int i = 1; i < fCount; i++) {
            HousekeepingLog key = filtered[i];
            String keyRoom = key.getRoom().getRoomNo();
            int j = i - 1;
            while (j >= 0 && filtered[j].getRoom().getRoomNo().compareTo(keyRoom) > 0) {
                filtered[j + 1] = filtered[j];
                j--;
            }
            filtered[j + 1] = key;
        }

        if (fCount == 0) {
            System.out.println("[i] No rooms match status '" + statusFilter + "' and type '" + typeFilter + "'.");
            return;
        }

        // Print Report Table
        System.out.printf("%-4s %-8s %-10s %-22s%n", "No.", "Room", "Type", "Status");
        System.out.println("--------------------------------------------------");
        for (int i = 0; i < fCount; i++) {
            Room r = filtered[i].getRoom();
            System.out.printf("%-4d %-8s %-10s %-22s%n", i + 1, r.getRoomNo(), r.getRoomType(), r.getCurrentStatus());
        }
        System.out.println("--------------------------------------------------");
        System.out.println("Total: " + fCount + " room(s)");
    }

    // Get Distinct Room Types (For Validating Report Filter Input)
    public String[] getDistinctRoomTypes() {
        String[] types = new String[logCount];
        int count = 0;
        for (int i = 0; i < logCount; i++) {
            String type = logs[i].getRoom().getRoomType();
            boolean found = false;
            for (int j = 0; j < count; j++) {
                if (types[j].equalsIgnoreCase(type)) {
                    found = true;
                    break;
                }
            }
            if (!found) {
                types[count++] = type;
            }
        }
        String[] result = new String[count];
        for (int i = 0; i < count; i++) result[i] = types[i];
        return result;
    }

    // Report: Rollback Frequency
    public void generateRollbackFrequencyReport() {
        System.out.println("\n==================================================");
        System.out.println("           ROLLBACK FREQUENCY REPORT");
        System.out.println("==================================================");

        // Search: Filter Rooms With At Least One Rollback
        HousekeepingLog[] filtered = new HousekeepingLog[logCount];
        int fCount = 0;
        for (int i = 0; i < logCount; i++) {
            if (logs[i].getRollbackCount() > 0) {
                filtered[fCount++] = logs[i];
            }
        }

        // Sort: Insertion Sort By Rollback Count (Descending)
        for (int i = 1; i < fCount; i++) {
            HousekeepingLog key = filtered[i];
            int keyCount = key.getRollbackCount();
            int j = i - 1;
            while (j >= 0 && filtered[j].getRollbackCount() < keyCount) {
                filtered[j + 1] = filtered[j];
                j--;
            }
            filtered[j + 1] = key;
        }

        if (fCount == 0) {
            System.out.println("[i] No rollbacks recorded.");
            return;
        }

        // Print Report Table
        System.out.printf("%-6s %-8s %-10s%n", "Rank", "Room", "Rollbacks");
        System.out.println("--------------------------------------------------");
        for (int i = 0; i < fCount; i++) {
            HousekeepingLog log = filtered[i];
            System.out.printf("%-6s %-8s %-10d%n", "#" + (i + 1), log.getRoom().getRoomNo(), log.getRollbackCount());
        }
    }
}