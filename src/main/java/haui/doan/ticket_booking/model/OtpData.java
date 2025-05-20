package haui.doan.ticket_booking.model;

import java.time.LocalDateTime;

public class OtpData {
    private String otp;
    private LocalDateTime createdAt;

    public OtpData(String otp, LocalDateTime createdAt) {
        this.otp = otp;
        this.createdAt = createdAt;
    }

    public String getOtp() {
        return otp;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}

