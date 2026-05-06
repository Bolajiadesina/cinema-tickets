package uk.gov.dwp.uc.pairtest.domain;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;


import uk.gov.dwp.uc.pairtest.domain.TicketTypeRequest.Type;
import uk.gov.dwp.uc.pairtest.services.TicketProcessor;


@DisplayName("Ticket process engine Test")
public class TicketProcessorTests {

    
    private TicketProcessor ticketProcessor;
     @BeforeEach
     void setup() throws Exception {
        ticketProcessor = new TicketProcessor();
        // Set ticket prices using reflection since @Value doesn't work in tests
        java.lang.reflect.Field childPriceField = TicketProcessor.class.getDeclaredField("childTicketPrice");
        childPriceField.setAccessible(true);
        childPriceField.setInt(ticketProcessor, 15);
        
        java.lang.reflect.Field adultPriceField = TicketProcessor.class.getDeclaredField("adultTicketPrice");
        adultPriceField.setAccessible(true);
        adultPriceField.setInt(ticketProcessor, 25);
    }  
    

    @Test
     void testProcessTickets_CalculatesCorrectCostAndSeats() {
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
        assertEquals(3, summary.getTotalSeats()); 
        assertEquals(65, summary.getTotalPrice());
        assertEquals(2, summary.getAdultTickets());
        assertEquals(1, summary.getChildTickets());
        assertEquals(1, summary.getInfantTickets());
    }

    @Test
     void testProcessTickets_OnlyAdults() {
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
     void testProcessTickets_OnlyInfants() {
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
