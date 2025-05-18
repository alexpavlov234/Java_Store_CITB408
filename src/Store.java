import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;

public class Store {
    private String name;
    private List<Product> products;
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
