package model;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;

public class Store implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private String name;
    private List<Product> productsInStock;
    private List<Product> productsSold;
    private List<Cashier> cashiers;

    private Map<ProductCategory, Double> markupPercentages;

    private int daysBeforeExpirationThreshold;
    private double discountPercentNearExpiration;

    public double getProductFinalPrice(Product product) {
        double markupPercentage = markupPercentages.get(product.getCategory());

        double productFinalPrice = product.getUnitPurchasePrice() + (product.getUnitPurchasePrice() * markupPercentage/100);

        if(isProductExpirationDiscountable(product)){
            return productFinalPrice - (productFinalPrice * discountPercentNearExpiration/100);
        } else {
            return productFinalPrice;
        }
    }

    public boolean isProductExpirationDiscountable(Product product){
        if(daysBeforeExpirationThreshold >= ChronoUnit.DAYS.between(LocalDate.now(), product.getExpirationDate())){
            return true;
        } else {
            return false;
        }
    }


}
