package haui.doan.ticket_booking.service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import haui.doan.ticket_booking.model.OtpData;

@Service
public class OtpService {

    private final Map<String, OtpData> otpStorage = new ConcurrentHashMap<>();
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

    @Autowired
    private EmailService mailService; // Giả sử bạn đã có một dịch vụ gửi email

    public void generateAndSendOtp(String email) {
        String otp = String.format("%04d", new Random().nextInt(9999));

        OtpData otpData = new OtpData(otp, LocalDateTime.now());
        otpStorage.put(email, otpData);

        // Tự động xóa OTP sau 1 phút
        scheduler.schedule(() -> otpStorage.remove(email), 5, TimeUnit.MINUTES);

        // Gửi email ở đây (gọi mailService.sendOtpMail(email, otp);)
        try {
            mailService.sendOtpEmail(email, otp);
        } catch (Exception e) {
            e.printStackTrace(); // Xử lý lỗi gửi email nếu cần
        }
    }

    public boolean verifyOtp(String email, String inputOtp) {
        OtpData otpData = otpStorage.get(email);
        System.out.println("OTP data: " + otpData.getOtp());

        // Kiểm tra hạn sử dụng OTP (phòng trường hợp scheduler chưa kịp xóa)
        if (Duration.between(otpData.getCreatedAt(), LocalDateTime.now()).toMinutes() >= 1) {
            otpStorage.remove(email);
            return false; // Hết hạn
        }

        boolean isValid = otpData.getOtp().equals(inputOtp);
        if (isValid) {
            otpStorage.remove(email); // Dùng xong thì xoá
        }

        return isValid;
    }
}

