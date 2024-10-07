package uk.gov.dwp.uc.pairtest;

import thirdparty.paymentgateway.TicketPaymentService;
import thirdparty.seatbooking.SeatReservationService;
import uk.gov.dwp.uc.pairtest.exception.InvalidPurchaseException;

public class TicketServiceImpl  implements TicketService {
    
     private final TicketPaymentService ticketPaymentService;
    private final SeatReservationService seatReservationService;


    public TicketServiceImpl(TicketPaymentService ticketPaymentService, SeatReservationService seatReservationService) {
        this.ticketPaymentService = ticketPaymentService;
        this.seatReservationService = seatReservationService;



        @Override
        public void purchaseTickets(Long accountId, TicketTypeRequest... ticketTypeRequests) throws InvalidPurchaseException {
    
            validateAccount(accountId);
            TicketSummary ticketSummary = processTickets(ticketTypeRequests);
            validateTicketPurchase(ticketSummary);
            makePaymentAndReserveSeats(accountId, ticketSummary);
        }
    
        private void validateAccount(Long accountId) {
            if (accountId == null || accountId <= 0) {
                throw new InvalidPurchaseException("Invalid account ID.");
            }
        }


        private TicketSummary processTickets(TicketTypeRequest[] ticketRequests) {
            int totalTickets = 0;
            int adultTickets = 0;
            int childTickets = 0;
            int infantTickets = 0;
            int totalAmountToPay = 0;
    
            for (TicketTypeRequest request : ticketRequests) {
                int numberOfTickets = request.getNoOfTickets();
                totalTickets += numberOfTickets;
    
                switch (request.getTicketType()) {
                    case ADULT:
                        adultTickets += numberOfTickets;
                        totalAmountToPay += numberOfTickets * 25;
                        break;
                    case CHILD:
                        childTickets += numberOfTickets;
                        totalAmountToPay += numberOfTickets * 15;
                        break;
                    case INFANT:
                        infantTickets += numberOfTickets;
                        break;
                }
            }
    
            return new TicketSummary(totalTickets, adultTickets, childTickets, infantTickets, totalAmountToPay);
        }
    
    }
}
