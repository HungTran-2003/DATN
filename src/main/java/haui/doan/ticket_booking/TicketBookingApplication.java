package haui.doan.ticket_booking;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;

import vn.payos.PayOS;

import org.springframework.beans.factory.annotation.Value;

@SpringBootApplication
@EnableScheduling
public class TicketBookingApplication {

	@Value("${CLIENT_ID}")
	private String clientId;

	@Value("${PAYOS_API_KEY}")
	private String apiKey;

	@Value("${PAYOS_CHECKSUM}")
	private String checksumKey;

	@Bean
	public PayOS payOS() {
		return new PayOS(clientId, apiKey, checksumKey);
	}

	public static void main(String[] args) {
		SpringApplication.run(TicketBookingApplication.class, args);
	}

}
