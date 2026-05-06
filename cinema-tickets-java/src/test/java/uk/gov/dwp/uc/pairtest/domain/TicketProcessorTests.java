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
       
        java.lang.reflect.Field childPriceField = TicketProcessor.class.getDeclaredField("childTicketPrice");
        childPriceField.setAccessible(true);
        childPriceField.setInt(ticketProcessor, 15);
        
        java.lang.reflect.Field adultPriceField = TicketProcessor.class.getDeclaredField("adultTicketPrice");
        adultPriceField.setAccessible(true);
        adultPriceField.setInt(ticketProcessor, 25);
    }  
    

    @Test
    @DisplayName("Should process tickets and calculate correct cost and seats")
     void testProcessTickets_CalculatesCorrectCostAndSeats() {
   
        TicketTypeRequest adult = new TicketTypeRequest(Type.ADULT, 2);
        TicketTypeRequest child = new TicketTypeRequest(Type.CHILD, 1);
        TicketTypeRequest infant = new TicketTypeRequest(Type.INFANT, 1);

     
        TicketSummary summary = ticketProcessor.processTickets(adult, child, infant);

       
        assertEquals(4, summary.getTotalTickets());
        assertEquals(3, summary.getTotalSeats()); 
        assertEquals(65, summary.getTotalPrice());
        assertEquals(2, summary.getAdultTickets());
        assertEquals(1, summary.getChildTickets());
        assertEquals(1, summary.getInfantTickets());
    }

    @Test
    @DisplayName("Should process only adult tickets correctly")
     void testProcessTickets_OnlyAdults() {
       
        TicketTypeRequest adult = new TicketTypeRequest(Type.ADULT, 3);

      
        TicketSummary summary = ticketProcessor.processTickets(adult);

        
        assertEquals(3, summary.getTotalTickets());
        assertEquals(3, summary.getTotalSeats());
        assertEquals(75, summary.getTotalPrice()); // 3 Adults * $25 = $75
        assertEquals(3, summary.getAdultTickets());
        assertEquals(0, summary.getChildTickets());
        assertEquals(0, summary.getInfantTickets());
    }

    @Test
    @DisplayName("Should process only infant tickets correctly")
     void testProcessTickets_OnlyInfants() {
      
        TicketTypeRequest infant = new TicketTypeRequest(Type.INFANT, 2);

       
        TicketSummary summary = ticketProcessor.processTickets(infant);

        
        assertEquals(2, summary.getTotalTickets());
        assertEquals(0, summary.getTotalSeats()); // Infants do not get seats.
        assertEquals(0, summary.getTotalPrice());
        assertEquals(0, summary.getAdultTickets());
        assertEquals(0, summary.getChildTickets());
        assertEquals(2, summary.getInfantTickets());
    }
}
