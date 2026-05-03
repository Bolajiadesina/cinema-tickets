package uk.gov.dwp.uc.pairtest.services;


import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

import org.slf4j.Logger;

import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import uk.gov.dwp.uc.pairtest.domain.ResponseFormat;
import uk.gov.dwp.uc.pairtest.domain.TicketSummary;
import uk.gov.dwp.uc.pairtest.domain.TicketTypeRequest;
import uk.gov.dwp.uc.pairtest.exception.InvalidCustomerUserTypeException;
import uk.gov.dwp.uc.pairtest.exception.InvalidPurchaseException;
import uk.gov.dwp.uc.pairtest.exception.TicketCountException;
import uk.gov.dwp.uc.pairtest.utils.TicketAndAccountsValidations;

@Service
public class TicketServiceImpl implements TicketService {

    Logger logger = LoggerFactory.getLogger(TicketServiceImpl.class);

    @Value("${MIN_TICKET}")
    private int minTicket;
    @Value("${MAX_TICKET}")
    private int maxTicket;

    private  final TicketProcessor ticketProcessor;
    private final BookTicketAndReserveSeat bookTicketAndReserveSeat;
    private final TicketAndAccountsValidations ticketAndAccountsValidations;
    private TicketSummary ticketSummary = null;
    private final ExecutorService executorService;

    public TicketServiceImpl(
            TicketProcessor ticketProcessor,
            BookTicketAndReserveSeat bookTicketAndReserveSeat,
            TicketAndAccountsValidations ticketAndAccountsValidations,
            ExecutorService executorService) {

        this.ticketProcessor = ticketProcessor;
        this.bookTicketAndReserveSeat = bookTicketAndReserveSeat;
        this.ticketAndAccountsValidations = ticketAndAccountsValidations;
        this.executorService = executorService;
    }

    public TicketTypeRequest createTicketRequest(TicketTypeRequest.Type type, int noOfTickets) {
        if (type == null) {
            throw new InvalidCustomerUserTypeException("Type cannot be null");
        }
        if (noOfTickets < minTicket || noOfTickets > maxTicket) {
            throw new TicketCountException(
                    ResponseFormat.INVALID_TICKET_COUNT.responseMessage(minTicket, maxTicket));
        }
        
        return new TicketTypeRequest(type, noOfTickets);
    }

    @Override
    public void purchaseTickets(Long accountId, TicketTypeRequest... ticketTypeRequests)
            throws InvalidPurchaseException {

        Future<?> reservationFuture = executorService.submit(() -> {
        
                ticketAndAccountsValidations.validateAccountId(accountId);
                ticketSummary = ticketProcessor.processTickets(ticketTypeRequests);
                ticketAndAccountsValidations.validateTicketPurchase(ticketSummary);
                bookTicketAndReserveSeat.makePaymentAndReserveSeats(accountId, ticketSummary);
           
        });
    }
}
