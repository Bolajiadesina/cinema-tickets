package uk.gov.dwp.uc.pairtest.domain;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.gov.dwp.uc.pairtest.exception.InvalidCustomerUserTypeException;
import uk.gov.dwp.uc.pairtest.exception.TicketCountException;
import uk.gov.dwp.uc.pairtest.services.BookTicketAndReserveSeat;
import uk.gov.dwp.uc.pairtest.services.TicketProcessor;
import uk.gov.dwp.uc.pairtest.services.TicketServiceImpl;
import uk.gov.dwp.uc.pairtest.utils.TicketAndAccountsValidations;
import uk.gov.dwp.uc.pairtest.domain.TicketTypeRequest;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.RETURNS_MOCKS;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import java.lang.reflect.Field;

@DisplayName("Ticket Service Implementation Test")
public class TicketServiceImplTests {
    Logger logger = LoggerFactory.getLogger(TicketServiceImplTests.class);

    private TicketServiceImpl ticketService;
    private TicketProcessor ticketProcessor;

    @BeforeEach
    void setUp() throws Exception {
        ticketProcessor = mock(TicketProcessor.class, RETURNS_MOCKS);
        BookTicketAndReserveSeat bookTicketAndReserveSeat = mock(BookTicketAndReserveSeat.class, RETURNS_MOCKS);
        TicketAndAccountsValidations ticketAndAccountsValidations = new TicketAndAccountsValidations();

        ticketService = new TicketServiceImpl(ticketProcessor, bookTicketAndReserveSeat, ticketAndAccountsValidations);

        // Set minTicket and maxTicket using reflection since @Value doesn't work in unit tests
        Field minTicketField = TicketServiceImpl.class.getDeclaredField("minTicket");
        minTicketField.setAccessible(true);
        minTicketField.set(ticketService, 1);

        Field maxTicketField = TicketServiceImpl.class.getDeclaredField("maxTicket");
        maxTicketField.setAccessible(true);
        maxTicketField.set(ticketService, 25);

        Field emptyStringField = TicketServiceImpl.class.getDeclaredField("emptyString");
        emptyStringField.setAccessible(true);
        emptyStringField.set(ticketService, "Ticket Type cannot be null");
    }

    @Test
    void testValidTicketTypeRequest() {
        assertDoesNotThrow(() -> ticketService.createTicketRequest(TicketTypeRequest.Type.ADULT, 1));
    }

    @Test
    void testNullTypeThrowsInvalidCustomerUserTypeException() {
        assertThrows(InvalidCustomerUserTypeException.class, () -> ticketService.createTicketRequest(null, 2));
    }

    @Test
    void testZeroTicketsThrowsTicketCountException() {
        assertThrows(TicketCountException.class,
                () -> ticketService.createTicketRequest(TicketTypeRequest.Type.ADULT, 0));
    }

    @Test
    void testNegativeTicketsThrowsTicketCountException() {
        assertThrows(TicketCountException.class,
                () -> ticketService.createTicketRequest(TicketTypeRequest.Type.ADULT, -1));
    }

    @Test
    void testTooManyTicketsThrowsTicketCountException() {
        assertThrows(TicketCountException.class,
                () -> ticketService.createTicketRequest(TicketTypeRequest.Type.ADULT, 26));
    }

}