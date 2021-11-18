package be.kuleuven.distributedsystems.cloud;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class Worker {

    @PostMapping
    String receiveMessageFromPushSub(@RequestBody String body) {
        //answer with 2xx HTTP Status code to acknowledge
        // TODO: parse the body? and respond
        return body;
    }
}
