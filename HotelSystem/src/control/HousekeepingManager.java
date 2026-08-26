package control;

import entity.Room;
import entity.HousekeepingLog;
import entity.StatusRecord;
import entity.RoomStatus;
import adt.LinkedStack;
import adt.ArrayList;

public class HousekeepingManager {

    private static final RoomStatus[] SEQUENCE = {
        RoomStatus.DIRTY, RoomStatus.CLEANING_IN_PROGRESS,
        RoomStatus.INSPECTED, RoomStatus.CLEAN
    };

    private HousekeepingLog[] logs;
    private int logCount;
    private String lastError;

    public HousekeepingManager() {
        logs = new HousekeepingLog[10];
        logCount = 0;
        lastError = "";
    }

    public String getLastError() { return lastError; }

    private void ensureCapacity() {
        if (logCount == logs.length) {
            HousekeepingLog[] bigger = new HousekeepingLog[logs.length * 2];
            for (int i = 0; i < logs.length; i++) bigger[i] = logs[i];
            logs = bigger;
        }
    }

    private int indexOfStatus(RoomStatus status) {
        for (int i = 0; i < SEQUENCE.length; i++) {
            if (SEQUENCE[i] == status) return i;
        }
        return -1;
    }

    private HousekeepingLog findLog(String roomNo) {
        for (int i = 0; i < logCount; i++) {
            if (logs[i].getRoom().getRoomNo().equalsIgnoreCase(roomNo)) return logs[i];
        }
        return null;
    }
    
    public HousekeepingManager(ArrayList<Room> rooms) {
        logs = new HousekeepingLog[10];
        logCount = 0;
        lastError = "";
        for (int i = 0; i < rooms.size(); i++) {
            registerRoom(rooms.get(i));
        }
    }
    public boolean roomExists(String roomNo) { return findLog(roomNo) != null; }

    public String getCurrentStatus(String roomNo) {
        HousekeepingLog log = findLog(roomNo);
        return (log == null) ? null : log.getRoom().getCurrentStatus().toString();
    }

    public String getNextStatus(String roomNo) {
        HousekeepingLog log = findLog(roomNo);
        if (log == null) return null;
        int currentIndex = indexOfStatus(log.getRoom().getCurrentStatus());
        if (currentIndex == -1 || currentIndex == SEQUENCE.length - 1) return null;
        return SEQUENCE[currentIndex + 1].toString();
    }

    public static String[] getValidStatuses() {
        String[] statuses = new String[SEQUENCE.length];
        for (int i = 0; i < SEQUENCE.length; i++) statuses[i] = SEQUENCE[i].toString();
        return statuses;
    }

    public void registerRoom(Room room) {
        ensureCapacity();
        logs[logCount++] = new HousekeepingLog(room);
    }

    public boolean updateStatus(String roomNo, String newStatus, String staffId) {
        HousekeepingLog log = findLog(roomNo);
        if (log == null) {
            lastError = "Room '" + roomNo + "' was not found in the system.";
            return false;
        }

        RoomStatus currentStatus = log.getRoom().getCurrentStatus();
        RoomStatus requestedStatus;
        try {
            requestedStatus = RoomStatus.fromDisplayName(newStatus);
        } catch (IllegalArgumentException exception) {
            requestedStatus = null;
        }
        int currentIndex = indexOfStatus(currentStatus);
        int newIndex = requestedStatus == null ? -1 : indexOfStatus(requestedStatus);

        if (newIndex == -1 || newIndex != currentIndex + 1) {
            lastError = "Cannot change status from '" + currentStatus + "' to '" + newStatus + "'.";
            return false;
        }

        log.getHistory().push(new StatusRecord(requestedStatus, staffId));
        log.getRoom().setCurrentStatus(requestedStatus);
        return true;
    }

    public boolean rollbackStatus(String roomNo) {
        HousekeepingLog log = findLog(roomNo);
        if (log == null) {
            lastError = "Room '" + roomNo + "' was not found in the system.";
            return false;
        }

        if (log.getHistory().size() <= 1) {
            lastError = "Room '" + roomNo + "' has no earlier status to roll back to.";
            return false;
        }

        log.getHistory().pop();
        StatusRecord previous = log.getHistory().peek();
        log.getRoom().setCurrentStatus(previous.getStatus());
        log.incrementRollbackCount();
        return true;
    }
    
    public boolean startNewCycle(String roomNo, String staffId) {
    HousekeepingLog log = findLog(roomNo);
    if (log == null) {
        lastError = "Room '" + roomNo + "' was not found in the system.";
        return false;
    }

    RoomStatus currentStatus = log.getRoom().getCurrentStatus();
    if (currentStatus != RoomStatus.CLEAN) {
        lastError = "Room '" + roomNo + "' must be 'Clean' before starting a new cycle (currently '" + currentStatus + "').";
        return false;
    }

    log.getHistory().push(new StatusRecord(RoomStatus.DIRTY, staffId));
    log.getRoom().setCurrentStatus(RoomStatus.DIRTY);
    return true;
}
    
    public boolean printHistory(String roomNo) {
        HousekeepingLog log = findLog(roomNo);
        if (log == null) {
            lastError = "Room '" + roomNo + "' was not found in the system.";
            return false;
        }

        System.out.println("Status history for Room " + roomNo + " (most recent first):");
        LinkedStack<StatusRecord> original = log.getHistory();
        LinkedStack<StatusRecord> temp = new LinkedStack<>();

        while (!original.isEmpty()) {
            StatusRecord r = original.pop();
            System.out.println("  " + r);
            temp.push(r);
        }
        while (!temp.isEmpty()) {
            original.push(temp.pop());
        }
        return true;
    }

    // ---------- reports ----------

    public void generateRoomStatusReport(String statusFilter) {
        System.out.println("\n==================================================");
        System.out.println("           ROOM STATUS SUMMARY");
        System.out.println("==================================================");

        HousekeepingLog[] filtered = new HousekeepingLog[logCount];
        int fCount = 0;
        for (int i = 0; i < logCount; i++) {
            RoomStatus status = logs[i].getRoom().getCurrentStatus();
            if (statusFilter.equalsIgnoreCase("ALL") || status.toString().equalsIgnoreCase(statusFilter)) {
                filtered[fCount++] = logs[i];
            }
        }

        // insertion sort by room number (ascending)
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
            System.out.println("[i] No rooms match status '" + statusFilter + "'.");
            return;
        }

        System.out.printf("%-4s %-8s %-10s %-22s%n", "No.", "Room", "Type", "Status");
        System.out.println("--------------------------------------------------");
        for (int i = 0; i < fCount; i++) {
            Room r = filtered[i].getRoom();
            System.out.printf("%-4d %-8s %-10s %-22s%n", i + 1, r.getRoomNo(), r.getRoomType(), r.getCurrentStatus());
        }
        System.out.println("--------------------------------------------------");
        System.out.println("Total: " + fCount + " room(s)");
    }

    public void generateRollbackFrequencyReport() {
        System.out.println("\n==================================================");
        System.out.println("           ROLLBACK FREQUENCY REPORT");
        System.out.println("==================================================");

        HousekeepingLog[] filtered = new HousekeepingLog[logCount];
        int fCount = 0;
        for (int i = 0; i < logCount; i++) {
            if (logs[i].getRollbackCount() > 0) {
                filtered[fCount++] = logs[i];
            }
        }

        // insertion sort by rollbackCount descending
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

        System.out.printf("%-6s %-8s %-10s%n", "Rank", "Room", "Rollbacks");
        System.out.println("--------------------------------------------------");
        for (int i = 0; i < fCount; i++) {
            HousekeepingLog log = filtered[i];
            System.out.printf("%-6s %-8s %-10d%n", "#" + (i + 1), log.getRoom().getRoomNo(), log.getRollbackCount());
        }
    }
}