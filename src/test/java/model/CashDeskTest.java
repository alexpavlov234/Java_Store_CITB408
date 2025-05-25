package model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class CashDeskTest {

    @Test
    void getId() {
        CashDesk cashDesk = new CashDesk(1, 101);
        cashDesk.setId(123);
        assertEquals(123, cashDesk.getId());
    }

    @Test
    void setId() {
        CashDesk cashDesk = new CashDesk(1, 101);
        cashDesk.setId(456);
        assertEquals(456, cashDesk.getId());
    }

    @Test
    void getCashier() {
        CashDesk cashDesk = new CashDesk(1, 101);
        assertEquals(101, cashDesk.getCashier());
    }

    @Test
    void setCashier() {
        CashDesk cashDesk = new CashDesk(1, 101);
        cashDesk.setCashier(202);
        assertEquals(202, cashDesk.getCashier());
    }

    @Test
    void getStore() {
        CashDesk cashDesk = new CashDesk(1, 101);
        assertEquals(1, cashDesk.getStore());
    }

    @Test
    void setStore() {
        CashDesk cashDesk = new CashDesk(1, 101);
        cashDesk.setStore(2);
        assertEquals(2, cashDesk.getStore());
    }

    @Test
    void constructorTest() {
        CashDesk cashDesk = new CashDesk(5, 505);
        assertEquals(5, cashDesk.getStore());
        assertEquals(505, cashDesk.getCashier());
        assertEquals(0, cashDesk.getId());
    }
}

