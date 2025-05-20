package haui.doan.ticket_booking.controller;

import org.springframework.web.bind.annotation.RestController;

import haui.doan.ticket_booking.service.OtpService;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;



@RestController
@RequestMapping("/api/otp")
public class OtpContronller {
    @Autowired
    private OtpService otpService; 
    
    @PostMapping("")
    public ResponseEntity<String> sendOtp(@RequestBody String email) {
        otpService.generateAndSendOtp(email);
        return ResponseEntity.ok("OTP sent");
    }

    
    @PostMapping("/verify-otp")
    public ResponseEntity<Boolean> verifyOtp(@RequestBody Map<String, String> request) {
        String email = request.get("email");
        String otp = request.get("otp");

        boolean isValid = otpService.verifyOtp(email, otp);
        if (isValid) {
            return ResponseEntity.ok(true);
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(false);
        }
    }
}
