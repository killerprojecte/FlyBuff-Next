package org.fastmcmirror.flybuff.api;

public class BuffCallback {
    private String reason = "";
    private boolean hasReason = false;
    private boolean status = true;

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public boolean isHasReason() {
        return hasReason;
    }

    public void setHasReason(boolean hasReason) {
        this.hasReason = hasReason;
    }

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }
}
