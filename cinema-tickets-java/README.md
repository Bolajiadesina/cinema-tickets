## Java Solution for Cinema Tickets Purchase Service

This project implements the DWP cinema tickets purchase services coding exercise in Java.
Overview

This project is implemented in Java (Java 21+) and uses Maven as the build tool.
The solution processes ticket purchase requests, ensuring all business rules are validated, and correctly integrates with external services for payment and seat reservation.
Features

  > ####   Validates ticket purchase requests:
     >-   Account ID must be valid (> 0)
     >-  Maximum of 25 tickets per purchase
        Adult: £25
        Child: £15
        Infant: £0 (Free)
    >- Calculates seat reservations:
        Adult and Child tickets reserve seats
        Child and Infant tickets require at least one Adult ticket
        Infants sit on an adult's lap
    >- Integrates with:
        TicketPaymentService- handles processing of ticket payments
        SeatReservationService - manages reservation of seats for ticket holders

> #### Design Approach

> #### The solution follows separation of concerns:
>
>- TicketAndAccountsValidations - handles validation rules
>- TicketProcessor - the engine that build the ticket process (totalTickets, totalSeats, totalPrice, adultTickets, childTickets, infantTickets)
>- TicketServiceImpl - handles the calling of different logics

> #### Assumptions

    Accounts with ID > 0 are valid and have sufficient funds
    External services always succeed once invoked
    Infant tickets require at least one adult ticket (no one-to-one ratio enforced)

> #### Testing

> #### The project uses JUnit 5 and Mockito.

> #### Test coverage includes:

 >-   Validation rules
>-    Price calculation
  >-  Seat reservation logic
  >-  Service interaction and ordering

Prerequisites

    Java 21
    Maven 3.x.

To run tests:

`mvn test`