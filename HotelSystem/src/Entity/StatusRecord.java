package Entity;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class StatusRecord {

    private String status;
    private String staffId;
    private LocalDateTime timestamp;

    private static final DateTimeFormatter FORMATTER =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public StatusRecord(String status, String staffId) {
        this.status = status;
        this.staffId = staffId;
        this.timestamp = LocalDateTime.now();
    }

    public String getStatus() { return status; }
    public String getStaffId() { return staffId; }
    public LocalDateTime getTimestamp() { return timestamp; }

    @Override
    public String toString() {
        if (staffId == null || staffId.isEmpty()) {
            return "[" + timestamp.format(FORMATTER) + "] " + status;
        }
        return "[" + timestamp.format(FORMATTER) + "] " + status + " (by " + staffId + ")";
    }
}