package uk.gov.dwp.uc.pairtest;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.TestPropertySource;
import uk.gov.dwp.uc.pairtest.domain.TicketTypeRequest;
import uk.gov.dwp.uc.pairtest.domain.TicketTypeRequest.Type;
import uk.gov.dwp.uc.pairtest.exception.InvalidPurchaseException;
import uk.gov.dwp.uc.pairtest.services.BookTicketAndReserveSeat;
import uk.gov.dwp.uc.pairtest.services.TicketProcessor;
import uk.gov.dwp.uc.pairtest.services.TicketServiceImpl;
import uk.gov.dwp.uc.pairtest.utils.TicketAndAccountsValidations;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.ArgumentMatchers.any;

@SpringBootTest
@TestPropertySource(locations = "classpath:application-test.properties")
public class CinemaTicketsApplicationTests {

    @MockBean
    private TicketProcessor ticketProcessor;

    @MockBean
    private BookTicketAndReserveSeat bookTicketAndReserveSeat;

    @MockBean
    private TicketAndAccountsValidations ticketAndAccountsValidations;

    @Autowired
    private TicketServiceImpl ticketService;

    @Autowired
    private ExecutorService executorService;

    @TestConfiguration
    static class TestConfig {
        @Bean
        public ExecutorService executorService() {
            // Use a single-threaded executor for predictable test execution
            return Executors.newSingleThreadExecutor();
        }
    }

    @Test
    void testPropertiesAreLoadedCorrectly() {
        when(ticketAndAccountsValidations.getMaxTicket()).thenReturn(25);
        when(ticketAndAccountsValidations.getMinTicket()).thenReturn(1);

        assertEquals(25, ticketAndAccountsValidations.getMaxTicket());
        assertTrue(ticketAndAccountsValidations.getMaxTicket() > 0, "MAX_TICKET should be positive");
    }

    @Test
    public void testPurchaseTickets_ValidInput() {
        TicketTypeRequest adultTickets = new TicketTypeRequest(Type.ADULT, 2);
        TicketTypeRequest childTickets = new TicketTypeRequest(Type.CHILD, 1);
        TicketTypeRequest infantTickets = new TicketTypeRequest(Type.INFANT, 1);

        assertDoesNotThrow(() -> ticketService.purchaseTickets(12345L, adultTickets, childTickets, infantTickets));
    }

    @Test
    void testValidTicketTypeRequest() {
        TicketTypeRequest request = new TicketTypeRequest(TicketTypeRequest.Type.ADULT, 2);
        assertEquals(TicketTypeRequest.Type.ADULT, request.getTicketType());
        assertEquals(2, request.getNoOfTickets());
    }

    @Test
    public void testPurchaseTickets_InvalidAccountId() throws ExecutionException, InterruptedException {

        TicketTypeRequest adultTickets = new TicketTypeRequest(Type.ADULT, 2);

        doThrow(new InvalidPurchaseException("Invalid account ID."))
                .when(ticketAndAccountsValidations).validateAccountId(any());

        try {

            ticketService.purchaseTickets(-1L, adultTickets);

        } catch (Exception e) {

            assertTrue(e instanceof InvalidPurchaseException);
            assertEquals("Invalid account ID.", e.getMessage());
        }
    }

    @Test
    public void testPurchaseTickets_NoAdultTicket() throws InterruptedException, ExecutionException {

        TicketTypeRequest childTickets = new TicketTypeRequest(Type.CHILD, 2);

        doThrow(new InvalidPurchaseException("Child or Infant tickets cannot be purchased without an Adult."))
                .when(ticketAndAccountsValidations).validateTicketPurchase(any());

        ticketService.purchaseTickets(12345L, childTickets);

        verify(bookTicketAndReserveSeat, never()).makePaymentAndReserveSeats(any(), any());
    }

    @Test
    public void testPurchaseTickets_TooManyTickets() {

        TicketTypeRequest adultTickets = new TicketTypeRequest(Type.ADULT, 26);

        doThrow(new InvalidPurchaseException("Cannot purchase more than 25 tickets."))
                .when(ticketAndAccountsValidations).validateTicketPurchase(any());

        ticketService.purchaseTickets(12345L, adultTickets);

        verify(bookTicketAndReserveSeat, never()).makePaymentAndReserveSeats(any(), any());
    }

    @Test
    public void testPurchaseTickets_InfantsExceedAdults() {

        TicketTypeRequest adultTickets = new TicketTypeRequest(Type.ADULT, 1);
        TicketTypeRequest infantTickets = new TicketTypeRequest(Type.INFANT, 2);

        doThrow(new InvalidPurchaseException("Number of INFANT tickets cannot exceed number of ADULT tickets"))
                .when(ticketAndAccountsValidations).validateTicketPurchase(any());

        ticketService.purchaseTickets(12345L, adultTickets, infantTickets);

        verify(bookTicketAndReserveSeat, never()).makePaymentAndReserveSeats(any(), any());
    }

}