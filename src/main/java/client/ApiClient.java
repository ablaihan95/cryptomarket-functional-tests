package client;

import static io.restassured.RestAssured.given;
import static org.apache.http.HttpStatus.SC_OK;

import io.restassured.RestAssured;
import io.restassured.filter.log.LogDetail;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import models.MarketDataModel;
import models.OrderModel;

public class ApiClient {

    public ApiClient() {
        RestAssured.baseURI = "http://94.130.158.237:43587";
    }

    public OrderModel getOrderById(String id) {
        return getResponseOrderById(id)
                .then()
                .log().ifValidationFails(LogDetail.ALL)
                .statusCode(SC_OK)
                .extract().as(OrderModel.class);
    }

    public Response getResponseOrderById(String id) {
        return given()
                .queryParam("id", id)
                .log().all()
                .get("/api/order")
                .then()
                .log().all()
                .extract().response();
    }

    public Response deleteOrderById(String id) {
        return given()
                .queryParam("id", id)
                .delete("/api/order")
                .then()
                .statusCode(200)
                .extract().response();
    }

    public Response deleteAllOrders() {
        return given()
                .get("/api/order/clean")
                .then()
                .statusCode(SC_OK)
                .extract().response();
    }

    public Response postOrders(OrderModel orderModel) {
        String side = orderModel.getSide();
        if (!(side == null)) {
            side = orderModel.getSide().substring(0, 1).toUpperCase() + orderModel.getSide().substring(1);
        }
        return given()
                .contentType(ContentType.JSON)
                .body(orderModel.toBuilder()
                        .side(side)
                        .build())
                .log().all()
                .post("/api/order/create")
                .then()
                .log().all()
                .extract().response();
    }

    public MarketDataModel getMarketData() {
        return given().log().all()
                .get("/api/marketdata")
                .then()
                .log().all()
                .statusCode(SC_OK)
                .extract().response().as(MarketDataModel.class);
    }

}
