package uk.gov.dwp.uc.pairtest;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.util.ReflectionTestUtils;

import uk.gov.dwp.uc.pairtest.domain.TicketSummary;
import uk.gov.dwp.uc.pairtest.domain.TicketTypeRequest;
import uk.gov.dwp.uc.pairtest.domain.TicketTypeRequest.Type;
import uk.gov.dwp.uc.pairtest.exception.InvalidCustomerUserTypeException;
import uk.gov.dwp.uc.pairtest.exception.TicketCountException;
import uk.gov.dwp.uc.pairtest.services.BookTicketAndReserveSeat;
import uk.gov.dwp.uc.pairtest.services.TicketProcessor;
import uk.gov.dwp.uc.pairtest.services.TicketServiceImpl;
import uk.gov.dwp.uc.pairtest.utils.TicketAndAccountsValidations;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.concurrent.ExecutorService;

public class TicketServiceImplTests {
    Logger logger= LoggerFactory.getLogger(TicketServiceImplTests.class);

    
    private TicketServiceImpl ticketService;
    private ExecutorService executorService;

    @BeforeEach
    void setUp() {
        
        TicketProcessor ticketProcessor = mock(TicketProcessor.class);
        BookTicketAndReserveSeat bookTicketAndReserveSeat = mock(BookTicketAndReserveSeat.class);
        TicketAndAccountsValidations ticketAndAccountsValidations = mock(TicketAndAccountsValidations.class);

        ticketService = new TicketServiceImpl(ticketProcessor, bookTicketAndReserveSeat, ticketAndAccountsValidations, executorService);

        ReflectionTestUtils.setField(ticketService, "minTicket", 1);
        ReflectionTestUtils.setField(ticketService, "maxTicket", 25);
    }

   

    @Test
    void testValidTicketTypeRequest() {
        assertDoesNotThrow(() -> ticketService.createTicketRequest(TicketTypeRequest.Type.ADULT, 1));
    }

    @Test
    void testNullTypeThrowsInvalidCustomerUserTypeException() {
        Exception exception = assertThrows(InvalidCustomerUserTypeException.class, () -> {
            ticketService.createTicketRequest(null, 2);
        });
        assertEquals("Type cannot be null", exception.getMessage());
    }

    @Test
    void testZeroTicketsThrowsTicketCountException() {
        Exception exception = assertThrows(TicketCountException.class, () -> {
            ticketService.createTicketRequest(TicketTypeRequest.Type.ADULT, 0);
        });
        assertEquals("Number of tickets must be between 1 and 25", exception.getMessage());
    }

    @Test
    void testNegativeTicketsThrowsTicketCountException() {
        Exception exception = assertThrows(TicketCountException.class, () -> {
            ticketService.createTicketRequest(TicketTypeRequest.Type.ADULT, -1);
        });
        assertEquals("Number of tickets must be between 1 and 25", exception.getMessage());
    }

    @Test
    void testTooManyTicketsThrowsTicketCountException() {
        Exception exception = assertThrows(TicketCountException.class, () -> {
            ticketService.createTicketRequest(TicketTypeRequest.Type.ADULT, 26);
        });
        assertEquals("Number of tickets must be between 1 and 25", exception.getMessage());
    }

    
   

   
}