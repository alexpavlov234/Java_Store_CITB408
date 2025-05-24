import java.util.List;

public class Client {
    private int id;
    private String name;
    private double balance;
    private List<Product> products;

    public Client(int id, String name, double balance, List<Product> products) {
        this.id = id;
        this.name = name;
        this.balance = balance;
        this.products = products;
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

    public double getBalance() {
        return balance;
    }

    public void setBalance(double balance) {
        this.balance = balance;
    }

    public List<Product> getProducts() {
        return products;
    }

    public void setProducts(List<Product> products) {
        this.products = products;
    }
}
