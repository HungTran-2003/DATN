package haui.doan.ticket_booking.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;


@Service
public class EmailService {

    @Autowired
    private JavaMailSender emailSender;

    public void sendOtpEmail(String toEmail, String otp) throws MessagingException {
        // Tạo email
        MimeMessage message = emailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true);
        
        // Cài đặt thông tin email
        helper.setFrom("kamimono10@gmail.com");
        helper.setTo(toEmail);
        helper.setSubject("Verification Email for CineX");
        helper.setText(generateOtpEmailContent(otp)); // Nội dung email (HTML có thể dùng true)

        // Gửi email
        emailSender.send(message);
    }

    public String generateOtpEmailContent(String otp) {
        // Tạo nội dung email với mã OTP
        String emailContent = 
            "CineX,\n\n" +
            "Mã OTP của bạn là:" + otp + "\n\n" +
            "Vui lòng nhập mã này trong vòng 1 phút để hoàn tất quá trình xác thực.\n\n" +
            "Nếu bạn không yêu cầu mã OTP này, vui lòng bỏ qua email này.\n\n" +
            "Cảm ơn bạn đã sử dụng dịch vụ của chúng tôi!\n\n" +
            "Trân trọng,\n" +
            "CineX Company";

        return emailContent;
    }
}

