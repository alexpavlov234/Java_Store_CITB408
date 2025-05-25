package model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ClientTest {

    @Test
    void getId() {
        Client client = new Client("Test Client", 100.0);
        client.setId(1);
        assertEquals(1, client.getId());
    }

    @Test
    void setId() {
        Client client = new Client("Test Client", 100.0);
        client.setId(2);
        assertEquals(2, client.getId());
    }

    @Test
    void getName() {
        Client client = new Client("Alice", 150.0);
        assertEquals("Alice", client.getName());
    }

    @Test
    void setName() {
        Client client = new Client("Test Client", 100.0);
        client.setName("Bob");
        assertEquals("Bob", client.getName());
    }

    @Test
    void getBalance() {
        Client client = new Client("Charlie", 200.50);
        assertEquals(200.50, client.getBalance(), 0.001);
    }

    @Test
    void setBalance() {
        Client client = new Client("Test Client", 100.0);
        client.setBalance(250.75);
        assertEquals(250.75, client.getBalance(), 0.001);
    }

    @Test
    void constructorTest() {
        Client client = new Client("David", 500.0);
        assertEquals("David", client.getName());
        assertEquals(500.0, client.getBalance(), 0.001);
        assertEquals(0, client.getId());
    }
}

