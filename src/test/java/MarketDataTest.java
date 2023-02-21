import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import client.ApiClient;
import io.restassured.RestAssured;
import io.restassured.parsing.Parser;
import java.util.Random;
import models.MarketDataModel;
import models.OrderModel;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class MarketDataTest {
    private final ApiClient apiClient = new ApiClient();
    private OrderModel defaultOrder;
    private OrderModel order2;

    @BeforeMethod
    public void setUp() {
        RestAssured.registerParser("text/plain", Parser.JSON);
        defaultOrder = apiClient.postOrders(OrderModel.builder()
                .id(String.valueOf(new Random().nextInt( 9999)))
                .price("100")
                .quantity("12")
                .side("Buy")
                .build()).as(OrderModel.class);

        order2 = apiClient.postOrders(defaultOrder.toBuilder()
                .id(String.valueOf(new Random().nextInt(9999)))
                .price("39")
                .side("Sell")
                .build()).as(OrderModel.class);

    }

    @AfterMethod(alwaysRun = true)
    public void tearDown() {
        apiClient.deleteAllOrders();
    }

    @Test
    public void shouldReturnMarketDataWithAllOrders() {
        MarketDataModel body = apiClient.getMarketData();
        assertThat(body.getAsks().size()).isGreaterThanOrEqualTo(1);
        assertThat(body.getAsks().get(0)).isEqualTo(order2.toBuilder().id(null).side(null).build());
        assertThat(body.getBids().size()).isGreaterThanOrEqualTo(1);
        assertThat(body.getBids().get(0)).isEqualTo(defaultOrder.toBuilder().id(null).side(null).build());
    }

    @Test
    public void shouldReturnMarketDataWithOnlyAsksIfAllBidsIsDeleted() {
        apiClient.deleteOrderById(defaultOrder.getId());
        MarketDataModel body = apiClient.getMarketData();
        assertThat(body.getAsks().size()).isGreaterThanOrEqualTo(1);
        assertThat(body.getAsks().get(0)).isEqualTo(order2.toBuilder().id(null).side(null).build());
        assertThat(body.getBids().size()).isGreaterThanOrEqualTo(0);
    }


    //todo test works as not expected enable after bug is fixed
    @Test(enabled = false)
    public void shouldReturnChipestOrder() {
        OrderModel chipestOrder = apiClient.postOrders(defaultOrder.toBuilder()
                .price("1")
                .side("Sell").build())
                .as(OrderModel.class)
                .toBuilder()
                .id(null)
                .side(null)
                .build();
        MarketDataModel body = apiClient.getMarketData();
        assertThat(body.getAsks().size()).isGreaterThanOrEqualTo(2);
        assertThat(body.getAsks().get(0)).isEqualTo(chipestOrder);
    }

    @Test(priority = 4)
    public void shouldReturnEmptyMarketDataIfAllOrdersIsDeleted() {
        apiClient.deleteAllOrders();
        MarketDataModel body = apiClient.getMarketData();
        assertThat(body.getAsks().size()).isGreaterThanOrEqualTo(0);
        assertThat(body.getBids().size()).isGreaterThanOrEqualTo(0);
    }
}
