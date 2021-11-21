package be.kuleuven.distributedsystems.cloud;

import be.kuleuven.distributedsystems.cloud.entities.Booking;
import com.google.protobuf.ByteString;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;

@RestController
public class Worker {

    private HashMap<String, List<Booking>> bookings = new HashMap<>();

    //Not sure we need ths one (not in the slides)
    @PostMapping
    String receiveMessageFromPushSub(@RequestBody String body) {
        //answer with 2xx HTTP Status code to acknowledge

        // TODO: parse the body
        //String data = ByteString data.toStringUtf8();


        //do the booking
        /*
         Booking newBooking = new Booking(UUID.randomUUID(),LocalDateTime.now(),tickets,customer);
        if (bookings.get(customer) == null) {
            bookings.put(customer, new ArrayList<>());
        }
        bookings.get(customer).add(newBooking);
         */
        return body;
    }

    @PostMapping("/subscription")
    public void subscription(@RequestBody String body) {
        //answer with 2xx HTTP Status code to acknowledge
        // TODO: parse the body?

    }
}
