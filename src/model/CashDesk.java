package model;

import java.io.Serial;
import java.io.Serializable;

public class CashDesk implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private int id;
    private int storeId;
    private int cashierId;

    public CashDesk(int storeId, int cashierId) {
        this.storeId = storeId;
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

    public int getStore() {
        return storeId;
    }

    public void setStore(int storeId) {
        this.storeId = storeId;
    }
}
