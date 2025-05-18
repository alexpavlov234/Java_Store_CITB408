public class CashDesk {
    private int id;
    private Cashier cashier;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Cashier getCashier() {
        return cashier;
    }

    public void setCashier(Cashier cashier) {
        this.cashier = cashier;
    }

    public CashDesk(int id, Cashier cashier) {
        this.id = id;
        this.cashier = cashier;
    }
}
