package entity;

import java.time.LocalDateTime;

public class StatusRecord {
    private RoomStatus status;
    private String staffId;
    private LocalDateTime timestamp;

    public StatusRecord() {
        this.status = null;
        this.staffId = "";
        this.timestamp = LocalDateTime.now();
    }

    public StatusRecord(RoomStatus status, String staffId) {
        this.status = status;
        this.staffId = staffId;
        this.timestamp = LocalDateTime.now();
    }

    public RoomStatus getStatus() { return status; }
    public String getStaffId() { return staffId; }
    public LocalDateTime getTimestamp() { return timestamp; }

    @Override
    public String toString() {
        return "StatusRecord{" +
                "status='" + status + '\'' +
                ", staffId='" + staffId + '\'' +
                ", timestamp=" + timestamp +
                '}';
    }
}
