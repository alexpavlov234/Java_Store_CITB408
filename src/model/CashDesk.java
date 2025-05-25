package model;

import java.io.Serial;
import java.io.Serializable;

public class CashDesk implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private int id;
    private int cashierId;

    public CashDesk(int id, int cashierId) {
        this.id = id;
        this.cashierId = cashierId;
    }

    public CashDesk() {

    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getCashier() {
        return cashierId;
    }

    public void setCashier(int cashierId) {
        this.cashierId = cashierId;
    }
}
