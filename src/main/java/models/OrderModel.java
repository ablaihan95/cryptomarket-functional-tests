package models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@Builder(toBuilder = true)
public class OrderModel {
    private String id;
    private String price;
    private String quantity;
    private String side;


    @Override
    public String toString() {
        return "OrderModel{" +
                "id='" + id + '\'' +
                ", price='" + price + '\'' +
                ", quantity='" + quantity + '\'' +
                ", side='" + side+ '\'' +
                '}';
    }
}
