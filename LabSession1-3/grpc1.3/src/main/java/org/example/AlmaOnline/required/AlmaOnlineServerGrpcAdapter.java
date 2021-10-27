package org.example.AlmaOnline.required;


import com.google.protobuf.Empty;
import io.grpc.stub.StreamObserver;
import org.example.AlmaOnline.provided.server.AlmaOnlineServerAdapter;
import org.example.AlmaOnline.provided.service.*;
import org.example.AlmaOnline.server.*;
import org.example.AlmaOnline.provided.service.exceptions.OrderException;
import org.example.AlmaOnline.provided.service.DineInOrderQuote;

import com.google.protobuf.Timestamp;
import java.util.*;

// AlmaOnlineServerGrpcAdapter implements the grpc-server side of the application.
// The implementation should not contain any additional business logic, only implement
// the code here that is required to couple your IDL definitions to the provided business logic.
public class AlmaOnlineServerGrpcAdapter extends AlmaOnlineGrpc.AlmaOnlineImplBase implements AlmaOnlineServerAdapter {

    // the service field contains the AlmaOnline service that the server will
    // call during testing.
    private final AlmaOnlineService service;

    public AlmaOnlineServerGrpcAdapter() {
        this.service = this.getInitializer().initialize();
    }

    @Override
    public void getRestaurants(Empty request, StreamObserver<RestaurantsResponse> responseObserver) {
        List<Restaurant> restaurants = new ArrayList<>(service.getRestaurants());
        var BuilderResponse = RestaurantsResponse.newBuilder();

        for (Restaurant restaurant : restaurants) {
            var BuilderRestaurantInfoResponse = RestaurantInfoResponse.newBuilder();
            var restaurantInfoResponse = BuilderRestaurantInfoResponse
                    .setId(restaurant.getId()).setName(restaurant.getName()).build();
          BuilderResponse.addRestaurantsInfos(restaurantInfoResponse);

        }
        //do something with responseObserver
        responseObserver.onNext(BuilderResponse.build());
        responseObserver.onCompleted();
    }

    @Override
    public void getMenu(RestaurantId request, StreamObserver<MenuResponse> responseObserver) {
        String restaurantId = request.getId();
        Optional<Menu> menu = service.getRestaurantMenu(restaurantId);
        var response = MenuResponse.newBuilder();
        if (menu.isPresent()){
            for (Item item: menu.get().getItems()) {
                var itemResponseBuilder = ItemResponse.newBuilder()
                        .setName(item.getName()).setPrice(item.getPrice());
                response.addItems(itemResponseBuilder.build());
            }
        }
        responseObserver.onNext(response.build());
        responseObserver.onCompleted();
    }

    @Override
    public void createDineInOrder(DineInOrderRequest request, StreamObserver<Empty> responseObserver) {

        DineInOrderQuote dineInOrderQuote = new DineInOrderQuote(
                request.getOrderId(),
                new Date(),
                request.getCustomer(), request.getItemsList(),
                new Date(request.getReservationDate().getSeconds()/1000)
                );
        try{
            service.createDineInOrder(request.getRestaurantId(),dineInOrderQuote);
        }catch (OrderException e){
            System.out.println("Order Exception:");
        }


        var response = Empty.newBuilder().build();
        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    @Override
    public void createDeliveryOrder(DeliveryOrderRequest request, StreamObserver<Empty> responseObserver) {
        DeliveryOrderQuote deliveryOrderQuote = new DeliveryOrderQuote(
                request.getOrderId(),
                new Date(),
                request.getCustomer(), request.getItemsList(),
                request.getDeliveryAddress()
        );
        try{
            service.createDeliveryOrder(request.getRestaurantId(),deliveryOrderQuote);
        }catch (OrderException e){
            System.out.println("Order Exception:");
        }


        var response = Empty.newBuilder().build();
        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    @Override
    public void getOrder(GetOrderRequest request, StreamObserver<GetOrderResponse> responseObserver) {

        Optional<Order> orderOptionnal = service.getOrder(request.getRestaurantId(), request.getOrderId());
        if (orderOptionnal.isPresent()){
            Order order = orderOptionnal.get();

            GetOrderResponse.Builder responseBuilder = GetOrderResponse.newBuilder();
            if (order instanceof DineInOrder){
                DineInOrder dineInOrder = (DineInOrder) order;
                DineInOrderResponse.Builder dineInOrderResponseBuilder = DineInOrderResponse.newBuilder().setCustomer(order.getCustomer())
                        .setCreateDate(Timestamp.newBuilder().setSeconds(order.getCreationDate().getTime()*1000))
                        .setReservationDate(Timestamp.newBuilder().setSeconds(dineInOrder.getReservationDate().getTime()*1000));
                for (Item item:order.getItems()) {
                    dineInOrderResponseBuilder.addItems(ItemResponse.newBuilder().setName(item.getName()).setPrice(item.getPrice()).build());
                }
                responseBuilder.setDineInOrderResponse(dineInOrderResponseBuilder.build());
            }else if(order instanceof DeliveryOrder){
                DeliveryOrder deliveryOrder = (DeliveryOrder) order;
                DeliveryOrderResponse.Builder deliveryOrderResponse = DeliveryOrderResponse.newBuilder().setCustomer(order.getCustomer())
                        .setCreateDate(Timestamp.newBuilder().setSeconds(order.getCreationDate().getTime()*1000))
                        .setDeliveryAddress(deliveryOrder.getDeliveryAddress());
                for (Item item:order.getItems()) {
                    deliveryOrderResponse.addItems(ItemResponse.newBuilder().setName(item.getName()).setPrice(item.getPrice()).build());
                }
                responseBuilder.setDeliveryOrderResponse(deliveryOrderResponse.build());
            }
            responseObserver.onNext(responseBuilder.build());
            responseObserver.onCompleted();

        }else{
            System.out.printf("No order with restaurantID:%s orderID:%s%n",request.getRestaurantId(), request.getOrderId());
            GetOrderResponse.Builder response = GetOrderResponse.newBuilder();
            responseObserver.onNext(response.build());
            responseObserver.onCompleted();
        }

    }


}
