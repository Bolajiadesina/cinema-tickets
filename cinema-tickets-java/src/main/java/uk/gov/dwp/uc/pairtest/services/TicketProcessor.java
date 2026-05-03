package uk.gov.dwp.uc.pairtest.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import uk.gov.dwp.uc.pairtest.domain.TicketSummary;
import uk.gov.dwp.uc.pairtest.domain.TicketTypeRequest;

@Component
public class TicketProcessor {
    Logger logger= LoggerFactory.getLogger(TicketProcessor.class);

    @Value("${CHILD_TICKET_PRICE}")
    private int childTicketPrice;
    @Value("${ADULT_TICKET_PRICE}")
    private int adultTicketPrice;

     

 
    public TicketSummary processTickets(TicketTypeRequest... ticketTypeRequests) {

        logger.info("Processing ticket requests...");

        logger.debug("Child Ticket Price: {}", childTicketPrice);
        logger.debug("Adult Ticket Price: {}", adultTicketPrice);
        var totalTickets = 0;
        var totalSeats = 0;
        var totalPrice = 0;
        var infantTickets = 0;
        var adultTickets = 0;
        var childTickets = 0;

        for (TicketTypeRequest request : ticketTypeRequests) {
            int numberOfTickets = request.getNoOfTickets();
            totalTickets += numberOfTickets;

            switch (request.getTicketType()) {
                case ADULT:
                    totalPrice += numberOfTickets * adultTicketPrice;
                    totalSeats += numberOfTickets;
                    adultTickets += numberOfTickets;
                    break;
                case CHILD:
                    totalPrice += numberOfTickets * childTicketPrice;
                    totalSeats += numberOfTickets;
                    childTickets += numberOfTickets;
                    break;
                case INFANT:
                    // Infants do not pay and are not allocated a seat.
                    infantTickets += numberOfTickets;
                    break;
            }
        }
        return new TicketSummary(totalTickets, totalSeats, totalPrice, adultTickets, childTickets, infantTickets);
    }
    
}
