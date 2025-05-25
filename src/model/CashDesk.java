package model;

import java.io.Serial;
import java.io.Serializable;

/**
 * Представлява касов апарат в магазин.
 */
public class CashDesk implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private int id;
    private int storeId;
    private int cashierId;

    /**
     * Конструктор за създаване на касов апарат с ID на магазин и ID на касиер.
     *
     * @param storeId   ID на магазина.
     * @param cashierId ID на касиера.
     */
    public CashDesk(int storeId, int cashierId) {
        this.storeId = storeId;
        this.cashierId = cashierId;
    }

    /**
     * Връща ID на касовия апарат.
     *
     * @return ID на касовия апарат.
     */
    public int getId() {
        return id;
    }

    /**
     * Задава ID на касовия апарат.
     *
     * @param id Ново ID на касовия апарат.
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * Връща ID на касиера, асоцииран с касовия апарат.
     *
     * @return ID на касиера.
     */
    public int getCashier() {
        return cashierId;
    }

    /**
     * Задава ID на касиера, асоцииран с касовия апарат.
     *
     * @param cashierId Ново ID на касиера.
     */
    public void setCashier(int cashierId) {
        this.cashierId = cashierId;
    }

    /**
     * Връща ID на магазина, към който принадлежи касовият апарат.
     *
     * @return ID на магазина.
     */
    public int getStore() {
        return storeId;
    }

    /**
     * Задава ID на магазина, към който принадлежи касовият апарат.
     *
     * @param storeId Ново ID на магазина.
     */
    public void setStore(int storeId) {
        this.storeId = storeId;
    }
}
