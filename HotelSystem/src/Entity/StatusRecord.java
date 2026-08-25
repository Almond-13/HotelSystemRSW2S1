package entity;

import java.time.LocalDateTime;

public class StatusRecord {
    private String status;
    private String staffId;
    private LocalDateTime timestamp;

    public StatusRecord() {
        this.status = "";
        this.staffId = "";
        this.timestamp = LocalDateTime.now();
    }

    public StatusRecord(String status, String staffId) {
        this.status = status;
        this.staffId = staffId;
        this.timestamp = LocalDateTime.now();
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getStaffId() {
        return staffId;
    }

    public void setStaffId(String staffId) {
        this.staffId = staffId;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    @Override
    public String toString() {
        return "StatusRecord{" +
                "status='" + status + '\'' +
                ", staffId='" + staffId + '\'' +
                ", timestamp=" + timestamp +
                '}';
    }
}
