package be.kuleuven.distributedsystems.cloud;

import be.kuleuven.distributedsystems.cloud.entities.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.config.HypermediaWebClientConfigurer;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Component
public class Model {

    private HashMap<String,List<Booking>> bookings = new HashMap<>();

    @Autowired
    WebClient.Builder webClientBuilder;
    Map<String,String> company_apiKEYS = new HashMap<>();
           // .put("reliabletheatrecompany","wCIoTqec6vGJijW2meeqSokanZuqOL");



    public List<Show> getShows() {
        String apiKKey = "wCIoTqec6vGJijW2meeqSokanZuqOL";
        String company = "https://reliabletheatrecompany.com/";
        Collection<Show> shows = webClientBuilder
                .baseUrl(company)
                .build()
                .get()
                .uri(uriBuilder -> uriBuilder
                        .pathSegment("shows")
                        .queryParam("key",apiKKey)
                        .build())
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<CollectionModel<Show>>() {})
                .block()
                .getContent();

        return new ArrayList<>(shows);
    }

    public Show getShow(String company, UUID showId) {
        String apiKKey = "wCIoTqec6vGJijW2meeqSokanZuqOL";
        //System.out.println("company:" + company);
        try{
            Show show = webClientBuilder
                    .baseUrl("https://"+company)
                    .build()
                    .get()
                    .uri(uriBuilder -> uriBuilder
                            .pathSegment(String.format("shows/%s",showId.toString()))
                            .queryParam("key",apiKKey)
                            .build())
                    .retrieve()
                    .bodyToMono(new ParameterizedTypeReference<Show>() {})
                    .block();
            return show;
        }catch (Exception e){
            System.err.println(e.getMessage() + String.format("getShow: Company:%s UUID:%s",company,showId.toString()));
        }
        return null;
    }

    public List<LocalDateTime> getShowTimes(String company, UUID showId) {
        String apiKKey = "wCIoTqec6vGJijW2meeqSokanZuqOL";
        try{
            Collection<LocalDateTime> showTimes = webClientBuilder
                    .baseUrl("https://"+company)
                    .build()
                    .get()
                    .uri(uriBuilder -> uriBuilder
                            .pathSegment(String.format("shows/%s/times",showId.toString()))
                            .queryParam("key",apiKKey)
                            .build())
                    .retrieve()
                    .bodyToMono(new ParameterizedTypeReference<CollectionModel<LocalDateTime>>() {})
                    .block()
                    .getContent();

            //System.out.println(showTimes);
            return new ArrayList<>(showTimes);
        }catch (Exception e){
            System.err.println(e.getMessage() + String.format("getShowTimes: Company:%s UUID:%s",company,showId.toString()));
        }
        return null;
    }

    public List<Seat> getAvailableSeats(String company, UUID showId, LocalDateTime time) {
        String apiKKey = "wCIoTqec6vGJijW2meeqSokanZuqOL";
        try{
            Collection<Seat> showAvailableSeats = webClientBuilder
                    .baseUrl("https://"+company)
                    .build()
                    .get()
                    .uri(uriBuilder -> uriBuilder
                            .pathSegment("shows/"+ showId.toString() +"/seats" )
                            .queryParam("time",time.toString())
                            .queryParam("available",true)
                            .queryParam("key",apiKKey)
                            .build())
                    .retrieve()
                    .bodyToMono(new ParameterizedTypeReference<CollectionModel<Seat>>() {})
                    .block()
                    .getContent();

            return new ArrayList<>(showAvailableSeats);
        }catch (Exception e){
            System.err.println(e.getMessage() + String.format(
                    "showAvailableSeats: Company:%s UUID:%s time:%s",company,showId.toString(),time.toString())
            );
        }
        return null;
    }

    public Seat getSeat(String company, UUID showId, UUID seatId) {
        String apiKKey = "wCIoTqec6vGJijW2meeqSokanZuqOL";
        try{
            Seat seat = webClientBuilder
                    .baseUrl("https://"+company)
                    .build()
                    .get()
                    .uri(uriBuilder -> uriBuilder
                            .pathSegment(String.format("shows/%s/seats/%s",showId,seatId))
                            .queryParam("key",apiKKey)
                            .build())
                    .retrieve()
                    .bodyToMono(new ParameterizedTypeReference<Seat>() {})
                    .block();

            return seat;
        }catch (Exception e){
            System.err.println(e.getMessage() + String.format(
                    "getSeat: Company:%s UUIDshow:%s UUIDseat:%s",company,showId,seatId)
            );
        }
        return null;
    }

    public Ticket getTicket(String company, UUID showId, UUID seatId) {
        String apiKKey = "wCIoTqec6vGJijW2meeqSokanZuqOL";
        try{
            Ticket ticket = webClientBuilder
                    .baseUrl("https://"+company)
                    .build()
                    .get()
                    .uri(uriBuilder -> uriBuilder
                            .pathSegment(String.format("shows/%s/seats/%s/ticket",showId,seatId))
                            .queryParam("key",apiKKey)
                            .build())
                    .retrieve()
                    .bodyToMono(new ParameterizedTypeReference<Ticket>() {})
                    .block();

            return ticket;
        }catch (Exception e){
            System.err.println(e.getMessage() + String.format(
                    "getTicket: Company:%s UUIDshow:%s UUIDseat:%s",company,showId,seatId)
            );
        }
        return null;
    }

    public List<Booking> getBookings(String customer) {
        return new ArrayList<Booking>(bookings.get(customer));
    }

    public List<Booking> getAllBookings() {
        ArrayList<Booking> allBookings = new ArrayList<Booking>();
        for (List<Booking> bookingsOfCustomer : bookings.values()) {
            allBookings.addAll(bookingsOfCustomer);
        }
        return allBookings;
    }

    public Set<String> getBestCustomers() {
        // TODO: return the best customer (highest number of tickets, return all of them if multiple customers have an equal amount)
        Set<String> bestCustomer = new HashSet<>();
        int highestNumTickets = 0;
        int numOfTickets;
        for (String customer:bookings.keySet()) {
            numOfTickets = 0;
            for (Booking booking: bookings.get(customer)) {
                numOfTickets += booking.getTickets().size();
            }
            if (highestNumTickets < numOfTickets) {
                bestCustomer = new HashSet<String>();
                bestCustomer.add(customer);
                highestNumTickets = numOfTickets;
            } else if (highestNumTickets == numOfTickets){
                bestCustomer.add(customer);
            }
        }
        return bestCustomer;
    }

    //reserve all seats for the given quotes
    public void confirmQuotes(List<Quote> quotes, String customer) {
        String apiKKey = "wCIoTqec6vGJijW2meeqSokanZuqOL";
        List<Ticket> tickets = new ArrayList<>();
        for (Quote quote : quotes) {
            try {
                Ticket ticket = webClientBuilder
                        .baseUrl("https://" + quote.getCompany())
                        .build()
                        .put()
                        .uri(uriBuilder -> uriBuilder
                                .pathSegment(String.format("shows/%s/seats/%s/ticket", quote.getShowId(), quote.getSeatId()))
                                .queryParam("customer","{customer}")
                                .queryParam("key","{key}")
                                .build(customer,apiKKey))
                        .retrieve()
                        .bodyToMono(new ParameterizedTypeReference<Ticket>() {
                        })
                        .block();
                tickets.add(ticket);
            } catch (Exception e) {
                System.err.println(e.getMessage() + String.format(
                        "  error message: confirmQuotes: Company:%s UUIDshow:%s UUIDseat:%s", quote.getCompany(), quote.getShowId(), quote.getSeatId())
                );
            }
        }

        Booking newBooking = new Booking(UUID.randomUUID(),LocalDateTime.now(),tickets,customer);
        if (bookings.get(customer) == null) {
            bookings.put(customer, new ArrayList<>());
        }
        bookings.get(customer).add(newBooking);
    }
}
