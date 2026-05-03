package uk.gov.dwp.uc.pairtest.services;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import thirdparty.paymentgateway.TicketPaymentService;
import thirdparty.seatbooking.SeatReservationService;
import uk.gov.dwp.uc.pairtest.domain.TicketSummary;

@Service
public class BookTicketAndReserveSeat {
    Logger logger = LoggerFactory.getLogger(BookTicketAndReserveSeat.class);

    private final ExecutorService executorService;
    private final TicketPaymentService ticketPaymentService;
    private final SeatReservationService seatReservationService;

    public BookTicketAndReserveSeat(
            ExecutorService executorService,
            TicketPaymentService ticketPaymentService,
            SeatReservationService seatReservationService) {
        this.executorService = executorService;
        this.ticketPaymentService = ticketPaymentService;
        this.seatReservationService = seatReservationService;
    }

    public void makePaymentAndReserveSeats(Long accountId, TicketSummary summary) {

        Future<?> paymentFuture = executorService.submit(() -> {
            try {
                ticketPaymentService.makePayment(accountId, summary.getTotalPrice());
            } catch (Exception e) {
                throw new RuntimeException("Payment failed: " + e.getMessage());
            }
        });

        Future<?> reservationFuture = executorService.submit(() -> {
            try {
                seatReservationService.reserveSeat(accountId, summary.getAdultTickets() + summary.getChildTickets());
            } catch (Exception e) {
                throw new RuntimeException("Seat reservation failed: " + e.getMessage());
            }
        });
    }

}
