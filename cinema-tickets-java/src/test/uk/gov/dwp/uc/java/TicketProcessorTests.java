package uk.gov.dwp.uc.pairtest;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.TestPropertySource;

import uk.gov.dwp.uc.pairtest.domain.TicketSummary;
import uk.gov.dwp.uc.pairtest.domain.TicketTypeRequest;
import uk.gov.dwp.uc.pairtest.domain.TicketTypeRequest.Type;
import uk.gov.dwp.uc.pairtest.services.TicketProcessor;

@SpringBootTest
@TestPropertySource(locations = "classpath:application-test.properties")
public class TicketProcessorTests {

    @Autowired
    private TicketProcessor ticketProcessor;
     @TestConfiguration
    static class TestConfig {
        @Bean
        public ExecutorService executorService() {
            return Executors.newSingleThreadExecutor();
        }
    }

    @Test
    public void testProcessTickets_CalculatesCorrectCostAndSeats() {
        // GIVEN: A set of ticket requests for 2 Adults, 1 Child, and 1 Infant.
        TicketTypeRequest adult = new TicketTypeRequest(Type.ADULT, 2);
        TicketTypeRequest child = new TicketTypeRequest(Type.CHILD, 1);
        TicketTypeRequest infant = new TicketTypeRequest(Type.INFANT, 1);

        // WHEN: The TicketProcessor processes the requests.
        TicketSummary summary = ticketProcessor.processTickets(adult, child, infant);

        // THEN: Verify the cost and seat count are correct based on the rules
        // (2 Adult * $25) + (1 Child * $15) = $65
        // (2 Adult seats) + (1 Child seat) = 3 total seats
        assertEquals(4, summary.getTotalTickets());
        assertEquals(3, summary.getTotalSeats()); // Infants do not get a seat.
        assertEquals(65, summary.getTotalPrice());
        assertEquals(2, summary.getAdultTickets());
        assertEquals(1, summary.getChildTickets());
        assertEquals(1, summary.getInfantTickets());
    }

    @Test
    public void testProcessTickets_OnlyAdults() {
        // GIVEN: A request for only 3 Adult tickets.
        TicketTypeRequest adult = new TicketTypeRequest(Type.ADULT, 3);

        // WHEN: The TicketProcessor processes the request.
        TicketSummary summary = ticketProcessor.processTickets(adult);

        // THEN: Verify the cost and seat count are correct.
        assertEquals(3, summary.getTotalTickets());
        assertEquals(3, summary.getTotalSeats());
        assertEquals(75, summary.getTotalPrice()); // 3 Adults * $25 = $75
        assertEquals(3, summary.getAdultTickets());
        assertEquals(0, summary.getChildTickets());
        assertEquals(0, summary.getInfantTickets());
    }

    @Test
    public void testProcessTickets_OnlyInfants() {
       // GIVEN: A request for only 2 Infant tickets.
        TicketTypeRequest infant = new TicketTypeRequest(Type.INFANT, 2);

        // WHEN: The TicketProcessor processes the request.
        TicketSummary summary = ticketProcessor.processTickets(infant);

        // THEN: Verify the cost and seat count are correct.
        assertEquals(2, summary.getTotalTickets());
        assertEquals(0, summary.getTotalSeats()); // Infants do not get seats.
        assertEquals(0, summary.getTotalPrice());
        assertEquals(0, summary.getAdultTickets());
        assertEquals(0, summary.getChildTickets());
        assertEquals(2, summary.getInfantTickets());
    }
}
