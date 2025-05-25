package service;

import dao.FileStorage;
import model.CashDesk;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;

import java.util.ArrayList;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mockStatic;

public class CashDeskServiceTest {

    private CashDeskService cashDeskService;
    private CashDesk testCashDesk;

    @BeforeEach
    public void setUp() {
        cashDeskService = new CashDeskService();
        testCashDesk = new CashDesk(1, 1);
        testCashDesk.setId(1);
    }

    @Test
    public void testCreateEntity_NullCashDesk_ThrowsException() {
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> cashDeskService.createEntity(null)
        );

        assertEquals("Касиерът не може да бъде null", exception.getMessage());
    }

    @Test
    public void testUpdateEntity_ValidCashDesk_Success() {
        try (MockedStatic<FileStorage> mockedFileStorage = mockStatic(FileStorage.class)) {
            mockedFileStorage.when(() -> FileStorage.updateObject(eq(testCashDesk), any())).thenReturn(true);

            CashDesk result = cashDeskService.updateEntity(testCashDesk);

            assertNotNull(result);
            assertEquals(testCashDesk.getId(), result.getId());
            mockedFileStorage.verify(() -> FileStorage.updateObject(eq(testCashDesk), any()));
        }
    }

    @Test
    public void testUpdateEntity_NonExistingCashDesk_ThrowsException() {
        try (MockedStatic<FileStorage> mockedFileStorage = mockStatic(FileStorage.class)) {
            mockedFileStorage.when(() -> FileStorage.updateObject(eq(testCashDesk), any())).thenReturn(false);

            IllegalArgumentException exception = assertThrows(
                    IllegalArgumentException.class,
                    () -> cashDeskService.updateEntity(testCashDesk)
            );

            assertEquals("Каса с ID " + testCashDesk.getId() + " не съществува", exception.getMessage());
        }
    }

    @Test
    public void testFindEntityById_ExistingId_ReturnsCashDesk() {
        try (MockedStatic<FileStorage> mockedFileStorage = mockStatic(FileStorage.class)) {
            mockedFileStorage.when(() -> FileStorage.findObjectById(CashDesk.class, 1)).thenReturn(Optional.of(testCashDesk));

            Optional<CashDesk> result = cashDeskService.findEntityById(1);

            assertTrue(result.isPresent());
            assertEquals(testCashDesk.getId(), result.get().getId());
        }
    }

    @Test
    public void testFindEntityById_NonExistingId_ReturnsEmptyOptional() {
        try (MockedStatic<FileStorage> mockedFileStorage = mockStatic(FileStorage.class)) {
            mockedFileStorage.when(() -> FileStorage.findObjectById(CashDesk.class, 999)).thenReturn(Optional.empty());

            Optional<CashDesk> result = cashDeskService.findEntityById(999);

            assertFalse(result.isPresent());
        }
    }

    @Test
    public void testGetAllEntities_ReturnsAllCashDesks() {
        try (MockedStatic<FileStorage> mockedFileStorage = mockStatic(FileStorage.class)) {
            ArrayList<CashDesk> cashDesks = new ArrayList<>();
            cashDesks.add(testCashDesk);

            CashDesk anotherCashDesk = new CashDesk(2, 2);
            anotherCashDesk.setId(2);
            cashDesks.add(anotherCashDesk);

            mockedFileStorage.when(() -> FileStorage.getCollection(CashDesk.class)).thenReturn(cashDesks);

            ArrayList<CashDesk> result = cashDeskService.getAllEntities();

            assertEquals(2, result.size());
            assertEquals(testCashDesk.getId(), result.get(0).getId());
            assertEquals(anotherCashDesk.getId(), result.get(1).getId());
        }
    }

    @Test
    public void testValidateEntity_InvalidId_ThrowsException() {
        testCashDesk.setId(-1);

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> cashDeskService.validateEntity(testCashDesk)
        );

        assertEquals("Невалиден ID на каса", exception.getMessage());
    }

    @Test
    public void testValidateEntity_InvalidStoreId_ThrowsException() {
        testCashDesk.setStore(-1);

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> cashDeskService.validateEntity(testCashDesk)
        );

        assertEquals("Касата с ID " + testCashDesk.getId() + " трябва да бъде свързана с магазин", exception.getMessage());
    }

    @Test
    public void testValidateEntity_InvalidCashierId_ThrowsException() {
        testCashDesk.setCashier(-1);

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> cashDeskService.validateEntity(testCashDesk)
        );

        assertEquals("Касата с ID " + testCashDesk.getId() + " трябва да има назначен касиер", exception.getMessage());
    }
}
