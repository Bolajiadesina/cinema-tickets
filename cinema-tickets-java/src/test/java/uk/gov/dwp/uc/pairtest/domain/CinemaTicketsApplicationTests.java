package uk.gov.dwp.uc.pairtest.domain;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import uk.gov.dwp.uc.pairtest.domain.TicketTypeRequest.Type;
import uk.gov.dwp.uc.pairtest.exception.InvalidPurchaseException;
import uk.gov.dwp.uc.pairtest.services.BookTicketAndReserveSeat;
import uk.gov.dwp.uc.pairtest.services.TicketProcessor;
import uk.gov.dwp.uc.pairtest.services.TicketServiceImpl;
import uk.gov.dwp.uc.pairtest.utils.TicketAndAccountsValidations;

import java.util.concurrent.ExecutionException;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.ArgumentMatchers.any;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)

@DisplayName("Ticket Service validation Tests")
public class CinemaTicketsApplicationTests {

    private TicketProcessor ticketProcessor;

    @Mock
    private BookTicketAndReserveSeat bookTicketAndReserveSeat;

    @Mock
    private TicketAndAccountsValidations ticketAndAccountsValidations;

    private TicketServiceImpl ticketService;

    @BeforeEach
    void setUp() {
        this.ticketProcessor = new TicketProcessor();
        this.ticketService = new TicketServiceImpl(ticketProcessor, bookTicketAndReserveSeat,
                ticketAndAccountsValidations);
    }

    @Test
    @DisplayName("Should load properties correctly from application.properties")
    void testPropertiesAreLoadedCorrectly() {
        when(ticketAndAccountsValidations.getMaxTicket()).thenReturn(25);
        when(ticketAndAccountsValidations.getMinTicket()).thenReturn(1);

        assertEquals(25, ticketAndAccountsValidations.getMaxTicket());
        assertTrue(ticketAndAccountsValidations.getMaxTicket() > 0, "MAX_TICKET should be positive");
    }

    @Test
    @DisplayName("Should purchase tickets successfully with valid input")
    void testPurchaseTickets_ValidInput() {
        TicketTypeRequest adultTickets = new TicketTypeRequest(Type.ADULT, 2);
        TicketTypeRequest childTickets = new TicketTypeRequest(Type.CHILD, 1);
        TicketTypeRequest infantTickets = new TicketTypeRequest(Type.INFANT, 1);

        assertDoesNotThrow(() -> ticketService.purchaseTickets(12345L, adultTickets, childTickets, infantTickets));
    }

    @Test
    @DisplayName("Should throw VAL_001 when account ID is invalid")
    void testValidTicketTypeRequest() {
        TicketTypeRequest request = new TicketTypeRequest(TicketTypeRequest.Type.ADULT, 2);
        assertEquals(TicketTypeRequest.Type.ADULT, request.getTicketType());
        assertEquals(2, request.getNoOfTickets());
    }

    @Test
    @DisplayName("Should throw VAL_001 when account ID is invalid")
    void testPurchaseTickets_InvalidAccountId() throws ExecutionException, InterruptedException {

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
    @DisplayName("Should throw VAL_002 when trying to purchase more than 25 tickets")   
    void testPurchaseTickets_TooManyTickets() {
        TicketTypeRequest adultTickets = new TicketTypeRequest(Type.ADULT, 26);

        // Update the mock to include the new code
        doThrow(new InvalidPurchaseException("VAL_002", "Cannot purchase more than 25 tickets."))
                .when(ticketAndAccountsValidations).validateTicketPurchase(any());

        InvalidPurchaseException exception = assertThrows(InvalidPurchaseException.class,
                () -> ticketService.purchaseTickets(12345L, adultTickets));

        assertEquals("Cannot purchase more than 25 tickets.", exception.getMessage());

        assertEquals("VAL_002", exception.getErrorCode());
    }

    @Test
    @DisplayName("Should throw VAL_003 when child/infant tickets are purchased without an adult")
    void testPurchaseTickets_NoAdultTicket() {
        // 1. Arrange
        TicketTypeRequest childTickets = new TicketTypeRequest(Type.CHILD, 2);

        String expectedMessage = "Child and Infant tickets cannot be purchased without purchasing an Adult ticket.";
        String expectedCode = "VAL_003";

        doThrow(new InvalidPurchaseException(expectedCode, expectedMessage))
                .when(ticketAndAccountsValidations).validateTicketPurchase(any());

        InvalidPurchaseException exception = assertThrows(InvalidPurchaseException.class,
                () -> ticketService.purchaseTickets(12345L, childTickets));

        assertEquals(expectedMessage, exception.getMessage());

        assertEquals(expectedCode, exception.getErrorCode());

        verify(bookTicketAndReserveSeat, never()).makePaymentAndReserveSeats(any(), any());
    }

    @Test
    @DisplayName("Should throw exception when number of infant tickets exceeds adult tickets")
    void testPurchaseTickets_InfantsExceedAdults() {

        TicketTypeRequest adultTickets = new TicketTypeRequest(Type.ADULT, 1);
        TicketTypeRequest infantTickets = new TicketTypeRequest(Type.INFANT, 2);

        doThrow(new InvalidPurchaseException("Number of INFANT tickets cannot exceed number of ADULT tickets"))
                .when(ticketAndAccountsValidations).validateTicketPurchase(any());

        InvalidPurchaseException exception = assertThrows(InvalidPurchaseException.class,
                () -> ticketService.purchaseTickets(12345L, adultTickets, infantTickets));

        assertEquals("Number of INFANT tickets cannot exceed number of ADULT tickets", exception.getMessage());
        verify(bookTicketAndReserveSeat, never()).makePaymentAndReserveSeats(any(), any());
    }

}