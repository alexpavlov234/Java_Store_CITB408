package model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class CashierTest {

    @Test
    void getId() {
        Cashier cashier = new Cashier("Test Name", 1000.0);
        cashier.setId(1);
        assertEquals(1, cashier.getId());
    }

    @Test
    void setId() {
        Cashier cashier = new Cashier("Test Name", 1000.0);
        cashier.setId(2);
        assertEquals(2, cashier.getId());
    }

    @Test
    void getName() {
        Cashier cashier = new Cashier("Alice", 1500.0);
        assertEquals("Alice", cashier.getName());
    }

    @Test
    void setName() {
        Cashier cashier = new Cashier("Test Name", 1000.0);
        cashier.setName("Bob");
        assertEquals("Bob", cashier.getName());
    }

    @Test
    void getSalary() {
        Cashier cashier = new Cashier("Charlie", 2000.0);
        assertEquals(2000.0, cashier.getSalary(), 0.001);
    }

    @Test
    void setSalary() {
        Cashier cashier = new Cashier("Test Name", 1000.0);
        cashier.setSalary(2500.0);
        assertEquals(2500.0, cashier.getSalary(), 0.001);
    }

    @Test
    void constructorTest() {
        Cashier cashier = new Cashier("David", 3000.0);
        assertEquals("David", cashier.getName());
        assertEquals(3000.0, cashier.getSalary(), 0.001);
        assertEquals(0, cashier.getId());
    }
}

