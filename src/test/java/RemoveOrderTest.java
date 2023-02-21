import static org.apache.http.HttpStatus.SC_NOT_FOUND;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import client.ApiClient;
import java.util.Random;
import models.OrderModel;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class RemoveOrderTest {
    private final ApiClient apiClient = new ApiClient();
    private OrderModel defaultOrder;
    private OrderModel order2;

    @BeforeMethod
    public void setUp() {
        defaultOrder = apiClient.postOrders(OrderModel.builder()
                .id(String.valueOf(new Random().nextInt(9999)))
                .price("100")
                .quantity("12")
                .side("Buy")
                .build()).as(OrderModel.class);

        order2 = apiClient.postOrders(defaultOrder.toBuilder()
                .id(String.valueOf(new Random().nextInt(9999)))
                .side("Sell")
                .build()).as(OrderModel.class);

    }

    @AfterClass(alwaysRun = true)
    public void tearDown() {
        apiClient.deleteAllOrders();
    }

    @Test(priority = 20)
    public void shouldDeleteAllOrdersBuyId() {
        String message = apiClient.deleteAllOrders().jsonPath().get("message").toString();
        assertThat(message)
                .isEqualTo("Order book is clean.");
        assertThat(apiClient.getResponseOrderById(defaultOrder.getId()).statusCode()).isEqualTo(SC_NOT_FOUND);
        assertThat(apiClient.getResponseOrderById(order2.getId()).statusCode()).isEqualTo(SC_NOT_FOUND);
    }

    @Test
    public void shouldDeleteOrdersBuyId() {
        apiClient.deleteOrderById(order2.getId());

        assertThat(apiClient.getResponseOrderById(order2.getId()).statusCode()).isEqualTo(SC_NOT_FOUND);
        //todo uncomment after bug getById is fixed
//        assertThat(apiClient.getResponseOrderById(defaultOrder.getId()).statusCode()).isEqualTo(SC_OK);
    }
}

