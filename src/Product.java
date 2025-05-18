public class Product {
    private int id;
    private String name;
    private double unitPurchasePrice;
    private ProductCategory category;

    public Product(int id, String name, double unitPurchasePrice, ProductCategory category) {
        this.id = id;
        this.name = name;
        this.unitPurchasePrice = unitPurchasePrice;
        this.category = category;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getUnitPurchasePrice() {
        return unitPurchasePrice;
    }

    public void setUnitPurchasePrice(double unitPurchasePrice) {
        this.unitPurchasePrice = unitPurchasePrice;
    }

    public ProductCategory getCategory() {
        return category;
    }

    public void setCategory(ProductCategory category) {
        this.category = category;
    }
}
