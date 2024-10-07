package uk.gov.dwp.uc.pairtest;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import org.junit.Before;
import org.junit.Test;

import thirdparty.paymentgateway.TicketPaymentService;
import thirdparty.seatbooking.SeatReservationService;
import uk.gov.dwp.uc.pairtest.TicketServiceImpl;
import uk.gov.dwp.uc.pairtest.domain.TicketTypeRequest;
import uk.gov.dwp.uc.pairtest.exception.InvalidPurchaseException;
import static org.junit.jupiter.api.Assertions.*;

class CinemaTicketsApplicationTests {

	@Test
	void contextLoads() {
	}



	private TicketPaymentService ticketPaymentService;
    private SeatReservationService seatReservationService;
    private TicketServiceImpl ticketService;

    @Before
    public void setup() {
        ticketPaymentService = mock(TicketPaymentService.class);
        seatReservationService = mock(SeatReservationService.class);
        ticketService = new TicketServiceImpl(ticketPaymentService, seatReservationService);
    }

    @Test
    public void testValidTicketPurchase() {
        TicketTypeRequest adultRequest = new TicketTypeRequest(TicketTypeRequest.Type.ADULT, 2);
        TicketTypeRequest childRequest = new TicketTypeRequest(TicketTypeRequest.Type.CHILD, 1);
        TicketTypeRequest infantRequest = new TicketTypeRequest(TicketTypeRequest.Type.INFANT, 1);

        ticketService.purchaseTickets(1L, adultRequest, childRequest, infantRequest);

        verify(ticketPaymentService).makePayment(1L, 65);  // 2 adults * £25 + 1 child * £15 = £65
        verify(seatReservationService).reserveSeat(1L, 3);  // 2 adults + 1 child = 3 seats
    }

    @Test(expected = InvalidPurchaseException.class)
    public void testInvalidTicketPurchase_NoAdultTicket() {
        TicketTypeRequest childRequest = new TicketTypeRequest(TicketTypeRequest.Type.CHILD, 1);
        ticketService.purchaseTickets(1L, childRequest);
    }

    @Test
    public void shouldProcessValidPurchase() {
        TicketTypeRequest adultTicket = new TicketTypeRequest(TicketTypeRequest.Type.ADULT, 2);
        TicketTypeRequest childTicket = new TicketTypeRequest(TicketTypeRequest.Type.CHILD, 3);

        ticketService.purchaseTickets(1L, adultTicket, childTicket);

        verify(ticketPaymentService).makePayment(1L, 95);  // 2 * 25 + 3 * 15 = 95
        verify(seatReservationService).reserveSeat(1L, 5);  // 5 seats to reserve
    }

    @Test
    public void shouldProcessValidPurchaseWithInfant() {
        TicketTypeRequest adultTicket = new TicketTypeRequest(TicketTypeRequest.Type.ADULT, 1);
        TicketTypeRequest childTicket = new TicketTypeRequest(TicketTypeRequest.Type.CHILD, 1);
        TicketTypeRequest infantTicket = new TicketTypeRequest(TicketTypeRequest.Type.INFANT, 1);

        ticketService.purchaseTickets(1L, adultTicket, childTicket, infantTicket);

        verify(ticketPaymentService).makePayment(1L, 40);  // 1 * 25 + 1 * 15 = 40
        verify(seatReservationService).reserveSeat(1L, 2);  // 2 seats (1 adult, 1 child)
    }

    @Test
    public void shouldThrowExceptionForTooManyTickets() {
        TicketTypeRequest adultTicket = new TicketTypeRequest(TicketTypeRequest.Type.ADULT, 26);

        assertThrows(InvalidPurchaseException.class, () -> ticketService.purchaseTickets(1L, adultTicket));
    }

    @Test
    public void shouldThrowExceptionForChildTicketWithoutAdult() {
        TicketTypeRequest childTicket = new TicketTypeRequest(TicketTypeRequest.Type.CHILD, 2);

        assertThrows(InvalidPurchaseException.class, () -> ticketService.purchaseTickets(1L, childTicket));
    }

    @Test
    public void shouldThrowExceptionForInfantTicketWithoutAdult() {
        TicketTypeRequest infantTicket = new TicketTypeRequest(TicketTypeRequest.Type.INFANT, 1);

        assertThrows(InvalidPurchaseException.class, () -> ticketService.purchaseTickets(1L, infantTicket));
    }

    @Test
    public void shouldProcessValidMaximumTicketPurchase() {
        TicketTypeRequest adultTicket = new TicketTypeRequest(TicketTypeRequest.Type.ADULT, 25);

        ticketService.purchaseTickets(1L, adultTicket);

        verify(ticketPaymentService).makePayment(1L, 625);  // 25 * 25 = 625
        verify(seatReservationService).reserveSeat(1L, 25);  // 25 seats
    }

    @Test
    public void shouldThrowExceptionForInvalidAccountId() {
        TicketTypeRequest adultTicket = new TicketTypeRequest(TicketTypeRequest.Type.ADULT, 1);

        assertThrows(InvalidPurchaseException.class, () -> ticketService.purchaseTickets(0L, adultTicket));
    }
}
