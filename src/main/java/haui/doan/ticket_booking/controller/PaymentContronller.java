package haui.doan.ticket_booking.controller;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Date;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import haui.doan.ticket_booking.DTO.CreateLinkPaymentDTO;
import haui.doan.ticket_booking.controller.user.BookingController;
import haui.doan.ticket_booking.model.Booking;
import haui.doan.ticket_booking.model.Payment;
import haui.doan.ticket_booking.repository.PaymentRepository;
import haui.doan.ticket_booking.service.BookingService;
import vn.payos.PayOS;
import vn.payos.type.CheckoutResponseData;
import vn.payos.type.ItemData;
import vn.payos.type.PaymentData;
import vn.payos.type.PaymentLinkData;
import vn.payos.type.Webhook;
import vn.payos.type.WebhookData;

@RestController
@RequestMapping("/payment")
public class PaymentContronller {

    private final PayOS payOS;

    @Autowired
    private BookingService bookingService;

    @Autowired
    private BookingController bookingController;

    @Autowired
    private PaymentRepository paymentRepository;

    public PaymentContronller(PayOS payOS) {
        super();
        this.payOS = payOS;
    }

    @PostMapping("/create")
    public ObjectNode createPaymentLink(@RequestBody CreateLinkPaymentDTO RequestBody) {
        ObjectMapper objectMapper = new ObjectMapper();
        ObjectNode response = objectMapper.createObjectNode();
        try {
            final String productName = RequestBody.getProductName();
            final String description = RequestBody.getDescription();
            final String returnUrl = RequestBody.getReturnUrl();
            final String cancelUrl = RequestBody.getCancelUrl();
            final int price = RequestBody.getPrice();
            final long orderCode = RequestBody.getOrderCode();

            Long currentTimeSeconds =  (System.currentTimeMillis() / 1000);
            Long expiredAt = currentTimeSeconds + 600;

            ItemData item = ItemData.builder().name(productName).price(price).quantity(1).build();

            PaymentData paymentData = PaymentData.builder().orderCode(orderCode).description(description).amount(price)
                    .item(item).returnUrl(returnUrl).cancelUrl(cancelUrl).expiredAt(expiredAt).build();

            CheckoutResponseData data = payOS.createPaymentLink(paymentData);

            response.put("error", 0);
            response.put("message", "success");
            response.set("data", objectMapper.valueToTree(data));
            return response;

        } catch (Exception e) {
            e.printStackTrace();
            response.put("error", -1);
            response.put("message", e.toString());
            response.set("data", null);
            return response;

        }
    }

    @GetMapping(path = "/{orderId}")
    public ObjectNode getOrderById(@PathVariable("orderId") long orderId) {
        ObjectMapper objectMapper = new ObjectMapper();
        ObjectNode response = objectMapper.createObjectNode();

        try {
            PaymentLinkData order = payOS.getPaymentLinkInformation(orderId);

            response.set("data", objectMapper.valueToTree(order));
            response.put("error", 0);
            response.put("message", "ok");
            return response;
        } catch (Exception e) {
            e.printStackTrace();
            response.put("error", -1);
            response.put("message", e.getMessage());
            response.set("data", null);
            return response;
        }

    }

    @PutMapping(path = "/{orderId}")
    public ObjectNode cancelOrder(@PathVariable("orderId") int orderId) {
        ObjectMapper objectMapper = new ObjectMapper();
        ObjectNode response = objectMapper.createObjectNode();
        try {
            PaymentLinkData order = payOS.cancelPaymentLink(orderId, null);
            bookingService.deleteBooking(orderId);
            bookingController.getBookingPending().remove(Integer.valueOf(orderId));
            response.set("data", objectMapper.valueToTree(order));
            response.put("error", 0);
            response.put("message", "ok");
            return response;
        } catch (Exception e) {
            e.printStackTrace();
            response.put("error", -1);
            response.put("message", e.getMessage());
            response.set("data", null);
            return response;
        }
    }

    @PostMapping(path = "/confirm-webhook")
    public ObjectNode confirmWebhook(@RequestBody Map<String, String> requestBody) {
        ObjectMapper objectMapper = new ObjectMapper();
        ObjectNode response = objectMapper.createObjectNode();
        try {
            String str = payOS.confirmWebhook(requestBody.get("webhookUrl"));
            response.set("data", objectMapper.valueToTree(str));
            response.put("error", 0);
            response.put("message", "ok");
            return response;
        } catch (Exception e) {
            e.printStackTrace();
            response.put("error", -1);
            response.put("message", e.getMessage());
            response.set("data", null);
            return response;
        }
    }

    @PostMapping(path = "/payos_transfer_handler")
    public ObjectNode payosTransferHandler(@RequestBody ObjectNode body)
        throws JsonProcessingException, IllegalArgumentException {
        ObjectMapper objectMapper = new ObjectMapper();
        ObjectNode response = objectMapper.createObjectNode();
        Webhook webhookBody = objectMapper.treeToValue(body, Webhook.class);

        try {
        // Init Response
            response.put("error", 0);
            response.put("message", "Webhook delivered");
            response.set("data", null);

            WebhookData data = payOS.verifyPaymentWebhookData(webhookBody);
            Long bookingId = data.getOrderCode();
            String status = data.getDesc();
            if (status.equals("success")) {
                Booking booking = bookingService.getBookingById(bookingId.intValue());
                booking.setPaymentStatus(Booking.PaymentStatus.PAID);
                bookingService.updateBooking(booking);

                Payment payment = new Payment();
                payment.setAmount(BigDecimal.valueOf(data.getAmount()));
                payment.setBooking(booking);
                payment.setPaymentMethod("PayOs");
                payment.setPaymentStatus(Payment.PaymentStatus.PAID);
                payment.setPaymentTime(new Timestamp(System.currentTimeMillis()));
                payment.setTransactionId(data.getReference());

                paymentRepository.save(payment);

                bookingController.getBookingPending().remove(Integer.valueOf(booking.getBookingId()));
            }
            return response;
        } catch (Exception e) {
            e.printStackTrace();
            response.put("error", -1);
            response.put("message", e.getMessage());
            response.set("data", null);
            return response;
        }
    }

}
