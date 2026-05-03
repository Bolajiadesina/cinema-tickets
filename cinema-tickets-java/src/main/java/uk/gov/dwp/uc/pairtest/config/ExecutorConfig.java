
package uk.gov.dwp.uc.pairtest.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import thirdparty.paymentgateway.TicketPaymentService;
import thirdparty.seatbooking.SeatReservationService;
import uk.gov.dwp.uc.pairtest.services.TicketProcessor;

@Configuration
public class ExecutorConfig {

    @Bean
    public TicketPaymentService ticketPaymentService() {
        return new thirdparty.paymentgateway.TicketPaymentServiceImpl();
    }

    @Bean
    public SeatReservationService seatReservationService() {
        return new thirdparty.seatbooking.SeatReservationServiceImpl();
    }

    @Bean
    public TicketProcessor ticketProcessor() {
        return new TicketProcessor();
    }

   
}