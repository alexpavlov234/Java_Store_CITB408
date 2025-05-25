package service;

import dao.FileStorage;
import model.Client;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;

import java.util.ArrayList;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mockStatic;

public class ClientServiceTest {

    private ClientService clientService;
    private Client testClient;

    @BeforeEach
    public void setUp() {
        clientService = new ClientService();
        testClient = new Client("Test Client", 500.0);
        testClient.setId(1);
    }

    @Test
    public void testCreateEntity_NullClient_ThrowsException() {
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> clientService.createEntity(null)
        );

        assertEquals("Клиентът не може да бъде null", exception.getMessage());
    }

    @Test
    public void testUpdateEntity_ValidClient_Success() {
        try (MockedStatic<FileStorage> mockedFileStorage = mockStatic(FileStorage.class)) {
            mockedFileStorage.when(() -> FileStorage.updateObject(eq(testClient), any())).thenReturn(true);

            Client result = clientService.updateEntity(testClient);

            assertNotNull(result);
            assertEquals(testClient.getId(), result.getId());
            assertEquals(testClient.getName(), result.getName());
            assertEquals(testClient.getBalance(), result.getBalance());
        }
    }

    @Test
    public void testUpdateEntity_NonExistingClient_ThrowsException() {
        try (MockedStatic<FileStorage> mockedFileStorage = mockStatic(FileStorage.class)) {
            mockedFileStorage.when(() -> FileStorage.updateObject(eq(testClient), any())).thenReturn(false);

            IllegalArgumentException exception = assertThrows(
                    IllegalArgumentException.class,
                    () -> clientService.updateEntity(testClient)
            );

            assertEquals("Клиент с ID " + testClient.getId() + " не съществува", exception.getMessage());
        }
    }

    @Test
    public void testFindEntityById_ExistingId_ReturnsClient() {
        try (MockedStatic<FileStorage> mockedFileStorage = mockStatic(FileStorage.class)) {
            mockedFileStorage.when(() -> FileStorage.findObjectById(Client.class, 1)).thenReturn(Optional.of(testClient));

            Optional<Client> result = clientService.findEntityById(1);

            assertTrue(result.isPresent());
            assertEquals(testClient.getId(), result.get().getId());
            assertEquals(testClient.getName(), result.get().getName());
        }
    }

    @Test
    public void testFindEntityById_NonExistingId_ReturnsEmptyOptional() {
        try (MockedStatic<FileStorage> mockedFileStorage = mockStatic(FileStorage.class)) {
            mockedFileStorage.when(() -> FileStorage.findObjectById(Client.class, 999)).thenReturn(Optional.empty());

            Optional<Client> result = clientService.findEntityById(999);

            assertFalse(result.isPresent());
        }
    }

    @Test
    public void testGetAllEntities_ReturnsAllClients() {
        try (MockedStatic<FileStorage> mockedFileStorage = mockStatic(FileStorage.class)) {
            ArrayList<Client> clients = new ArrayList<>();
            clients.add(testClient);

            Client anotherClient = new Client("Another Client", 1000.0);
            anotherClient.setId(2);
            clients.add(anotherClient);

            mockedFileStorage.when(() -> FileStorage.getCollection(Client.class)).thenReturn(clients);

            ArrayList<Client> result = clientService.getAllEntities();

            assertEquals(2, result.size());
            assertEquals(testClient.getId(), result.get(0).getId());
            assertEquals(anotherClient.getId(), result.get(1).getId());
        }
    }

    @Test
    public void testValidateEntity_InvalidId_ThrowsException() {
        testClient.setId(-1);

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> clientService.validateEntity(testClient)
        );

        assertEquals("Невалиден ID на клиент", exception.getMessage());
    }

    @Test
    public void testValidateEntity_EmptyName_ThrowsException() {
        testClient.setName("");

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> clientService.validateEntity(testClient)
        );

        assertEquals("Името на клиент с ID " + testClient.getId() + " не може да бъде null или празно", exception.getMessage());
    }

    @Test
    public void testValidateEntity_NullName_ThrowsException() {
        testClient.setName(null);

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> clientService.validateEntity(testClient)
        );

        assertEquals("Името на клиент с ID " + testClient.getId() + " не може да бъде null или празно", exception.getMessage());
    }

    @Test
    public void testValidateEntity_NegativeBalance_ThrowsException() {
        testClient.setBalance(-100.0);

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> clientService.validateEntity(testClient)
        );

        assertEquals("Балансът на клиент с ID " + testClient.getId() + " не може да бъде отрицателен", exception.getMessage());
    }
}
