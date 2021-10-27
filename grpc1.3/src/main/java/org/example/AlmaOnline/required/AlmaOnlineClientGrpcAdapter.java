package org.example.AlmaOnline.required;

import com.google.common.util.concurrent.ListenableFuture;
import com.google.protobuf.Empty;
import com.google.protobuf.Timestamp;
import org.example.AlmaOnline.provided.client.*;
import org.example.AlmaOnline.provided.client.DineInOrderQuote;
import org.example.AlmaOnline.server.*;

import java.util.*;

// AlmaOnlineClientGrpcAdapter provides your own implementation of the AlmaOnlineClientAdapter
public class AlmaOnlineClientGrpcAdapter implements AlmaOnlineClientAdapter {
    // getRestaurants should retrieve the information on all the available restaurants.
    @Override
    public List<RestaurantInfo> getRestaurants(AlmaOnlineGrpc.AlmaOnlineBlockingStub stub) {
        //get the response
        Empty request = Empty.newBuilder().build();
        var restaurantsResponse  = stub.getRestaurants(request);
        List<RestaurantInfoResponse> restaurantsInfoslistResponse = restaurantsResponse.getRestaurantsInfosList();
        //create Building logic objects from Proto objects
        List<RestaurantInfo> restaurantInfos = new ArrayList<>();
        for (int i = 0; i < restaurantsInfoslistResponse.size(); i++) {
            RestaurantInfoResponse restInfoResp = RestaurantInfoResponse.newBuilder().build();

            restaurantInfos.add(
                    new RestaurantInfo(
                            restaurantsInfoslistResponse.get(i).getId()
                            ,restaurantsInfoslistResponse.get(i).getName()
                    )
            );
        }
        return restaurantInfos;
    }

    // getMenu should return the menu of a given restaurant
    @Override
    public MenuInfo getMenu(AlmaOnlineGrpc.AlmaOnlineBlockingStub stub, String restaurantId) {
        //request and get response
        var requestBuilder = RestaurantId.newBuilder().setId(restaurantId);
        var menuResponse = stub.getMenu(requestBuilder.build());
        //creat businnes logic object from response
        Map<String,Double> items = new HashMap<>();
        for (ItemResponse itemResponse: menuResponse.getItemsList()) {
            items.put(itemResponse.getName(),itemResponse.getPrice());
        }
        return new MenuInfo(items);
    }

    @Override
    public ListenableFuture<?> createDineInOrder(AlmaOnlineGrpc.AlmaOnlineFutureStub stub, DineInOrderQuote order) {
        var request = DineInOrderRequest.newBuilder()
                .setRestaurantId(order.getRestaurantId()).setOrderId(order.getOrderId()).setCustomer(order.getCustomer());
        Timestamp timestamp = Timestamp.newBuilder().setSeconds(1000*order.getReservationDate().getTime()).build();
        request.setReservationDate(timestamp);
        request.addAllItems(order.getItems());

        return stub.createDineInOrder(request.build());
    }


    // createDeliveryOrder should create the given delivery order at the AlmaOnline server
    @Override
    public ListenableFuture<?> createDeliveryOrder(AlmaOnlineGrpc.AlmaOnlineFutureStub stub, DeliveryOrder order) {
        DeliveryOrderRequest.Builder request = DeliveryOrderRequest.newBuilder()
                .setRestaurantId(order.getRestaurantId()).setOrderId(order.getOrderId())
                .setCustomer(order.getCustomer()).setDeliveryAddress(order.getDeliveryAddress());
        request.addAllItems(order.getItems());

        return stub.createDeliveryOrder(request.build());
    }

    // getOrder should retrieve the order information at the AlmaOnline server given the restaurant the order is
    // placed at and the id of the order.
    @Override
    public BaseOrderInfo getOrder(AlmaOnlineGrpc.AlmaOnlineBlockingStub stub, String restaurantId, String orderId) {
        GetOrderRequest getOrderRequest = GetOrderRequest.newBuilder().setRestaurantId(restaurantId).setOrderId(orderId).build();
        var response = stub.getOrder(getOrderRequest);

        if(response.hasDeliveryOrderResponse()){
            DeliveryOrderResponse deliveryOrderResponse = response.getDeliveryOrderResponse();
            List <ItemInfo> itemInfoList = new ArrayList<>();
            for (ItemResponse itemResponse:deliveryOrderResponse.getItemsList()) {
                itemInfoList.add(new ItemInfo(itemResponse.getName(), itemResponse.getPrice()));
            }
            Date createDate = new Date(deliveryOrderResponse.getCreateDate().getSeconds()/1000);

            return new DeliveryOrderInfo(deliveryOrderResponse.getCustomer(),createDate,itemInfoList, deliveryOrderResponse.getDeliveryAddress());

        } else if (response.hasDineInOrderResponse()) {
            DineInOrderResponse dineInOrderResponse = response.getDineInOrderResponse();
            List <ItemInfo> itemInfoList = new ArrayList<>();
            for (ItemResponse itemResponse:dineInOrderResponse.getItemsList()) {
                itemInfoList.add(new ItemInfo(itemResponse.getName(), itemResponse.getPrice()));
            }
            Date createDate = new Date(dineInOrderResponse.getCreateDate().getSeconds()/1000);
            Date reservationDate = new Date(dineInOrderResponse.getReservationDate().getSeconds()/1000);

            return new DineInOrderInfo(dineInOrderResponse.getCustomer(),createDate,itemInfoList, reservationDate);
        }else{
            System.out.printf("No order with restaurantID:%s OrderID:%s\n%n",restaurantId,orderId);
            return null;
        }
    }

    // getScript returns the script the application will run during testing.
    // You can leave the default implementation, as it will test most of the functionality.
    // Alternatively, you can provide your own implementation to test your own edge-cases.
    @Override
    public AppScript getScript() {
        return AlmaOnlineClientAdapter.super.getScript();
    }
}
