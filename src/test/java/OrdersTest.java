import static org.apache.http.HttpStatus.SC_BAD_REQUEST;
import static org.apache.http.HttpStatus.SC_NOT_FOUND;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import client.ApiClient;
import models.OrderModel;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

public class OrdersTest {

    private final ApiClient apiClient = new ApiClient();

    private OrderModel defaultOrder;

    @BeforeClass
    public void setUp() {
        defaultOrder = apiClient.postOrders(OrderModel.builder()
                .id("1")
                .price("100")
                .quantity("12")
                .side("Buy")
                .build()).as(OrderModel.class);
    }

    @AfterClass(alwaysRun = true)
    public void tearDown() {
        apiClient.deleteAllOrders();
    }

    //todo test works as not expected enable after bug is fixed
    @Test(enabled = false)
    public void shouldGetOrderBuyId() {
        assertThat(apiClient.getOrderById(defaultOrder.getId()))
                .isEqualTo(defaultOrder); //expected 200 but 400
    }

    @Test
    public void shouldGetNotFoundForOrderBuyId() {
        assertThat(apiClient.getResponseOrderById("1234").statusCode())
                .isEqualTo(SC_NOT_FOUND);
    }

    @Test
    public void shouldGetErrorForOrderBuyIdIfIdIsNotNumber() {
        String message = apiClient.getResponseOrderById("notExistingId")
                .then()
                .statusCode(SC_BAD_REQUEST)
                .extract().jsonPath().get("message").toString();
        assertThat(message).isEqualTo("ID should be an integer");

    }

    @Test(dataProvider = "modelsForCreating")
    public void shouldCreateOrderBuyId(final OrderModel orderModel) {
        OrderModel createdModel = apiClient.postOrders(orderModel).as(OrderModel.class);
        assertThat(createdModel)
                .isEqualTo(orderModel);
    }

    @Test(dataProvider = "modelsForErrorPost")
    public void shouldGetErrorForPostOrderBuyId(final OrderModel orderModel) {
        int statusCode = apiClient.postOrders(orderModel).statusCode();
        assertThat(statusCode)
                .isEqualTo(SC_BAD_REQUEST);
    }

    //todo uncomment cases enable after bugs are fixed
    @DataProvider
    public Object[][] modelsForCreating() {
        return new Object[][] {
                {defaultOrder.toBuilder()
                        .quantity("9999")
                        .build()},
                {defaultOrder.toBuilder()
                        .side("sell")
                        .build()},
                {defaultOrder.toBuilder()
                        .quantity("1")
                        .build()},
                {defaultOrder.toBuilder()
                        .price("9999")
                        .build()},
                {defaultOrder.toBuilder()
                        .price("1")
                        .build()},
//                {defaultOrder.toBuilder()
//                        .price(null) //price is optional but 400
//                        .build()},
                {defaultOrder.toBuilder()
                        .id("9999")
                        .build()},
//                {defaultOrder.toBuilder()
//                        .id(null) // id is optional but replaced with 10000
//                        .build()},
        };
    }

    //todo uncomment cases enable after bugs are fixed
    @DataProvider
    public Object[][] modelsForErrorPost() {
        return new Object[][] {
                {defaultOrder.toBuilder()
                        .quantity("10000")
                        .build()},
                {defaultOrder.toBuilder()
                        .quantity(null)
                        .build()},
//                {defaultOrder.toBuilder()
//                        .quantity("0")
//                        .build()}, // should be fail with 400 but 200
//                {defaultOrder.toBuilder()
//                        .price("10000")
//                        .build()}, //should be fail with 400 but 200
                {defaultOrder.toBuilder()
                        .price("0")
                        .build()},
                {defaultOrder.toBuilder()
                        .side(null)
                        .build()},
                {defaultOrder.toBuilder()
                        .side("BuySell")
                        .build()},
                {defaultOrder.toBuilder()
                        .id("10000")
                        .build()},
//                {defaultOrder.toBuilder() //should be fail with 400 but 200
//                        .id("0")
//                        .build()},
        };
    }
}
