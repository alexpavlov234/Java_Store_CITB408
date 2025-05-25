package service;

import dao.FileStorage;
import model.Cashier;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;

import java.util.ArrayList;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mockStatic;

public class CashierServiceTest {

    private CashierService cashierService;
    private Cashier testCashier;

    @BeforeEach
    public void setUp() {
        cashierService = new CashierService();
        testCashier = new Cashier("Test Cashier", 1000.0);
        testCashier.setId(1);
    }

    @Test
    public void testCreateEntity_NullCashier_ThrowsException() {
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> cashierService.createEntity(null)
        );

        assertEquals("Касиерът не може да бъде null", exception.getMessage());
    }

    @Test
    public void testUpdateEntity_ValidCashier_Success() {
        try (MockedStatic<FileStorage> mockedFileStorage = mockStatic(FileStorage.class)) {
            mockedFileStorage.when(() -> FileStorage.updateObject(eq(testCashier), any())).thenReturn(true);

            Cashier result = cashierService.updateEntity(testCashier);

            assertNotNull(result);
            assertEquals(testCashier.getId(), result.getId());
            assertEquals(testCashier.getName(), result.getName());
            assertEquals(testCashier.getSalary(), result.getSalary());
        }
    }

    @Test
    public void testUpdateEntity_NonExistingCashier_ThrowsException() {
        try (MockedStatic<FileStorage> mockedFileStorage = mockStatic(FileStorage.class)) {
            mockedFileStorage.when(() -> FileStorage.updateObject(eq(testCashier), any())).thenReturn(false);

            IllegalArgumentException exception = assertThrows(
                    IllegalArgumentException.class,
                    () -> cashierService.updateEntity(testCashier)
            );

            assertEquals("Касиер с ID " + testCashier.getId() + " не съществува", exception.getMessage());
        }
    }

    @Test
    public void testFindEntityById_ExistingId_ReturnsCashier() {
        try (MockedStatic<FileStorage> mockedFileStorage = mockStatic(FileStorage.class)) {
            mockedFileStorage.when(() -> FileStorage.findObjectById(Cashier.class, 1)).thenReturn(Optional.of(testCashier));

            Optional<Cashier> result = cashierService.findEntityById(1);

            assertTrue(result.isPresent());
            assertEquals(testCashier.getId(), result.get().getId());
            assertEquals(testCashier.getName(), result.get().getName());
        }
    }

    @Test
    public void testFindEntityById_NonExistingId_ReturnsEmptyOptional() {
        try (MockedStatic<FileStorage> mockedFileStorage = mockStatic(FileStorage.class)) {
            mockedFileStorage.when(() -> FileStorage.findObjectById(Cashier.class, 999)).thenReturn(Optional.empty());

            Optional<Cashier> result = cashierService.findEntityById(999);

            assertFalse(result.isPresent());
        }
    }

    @Test
    public void testGetAllEntities_ReturnsAllCashiers() {
        try (MockedStatic<FileStorage> mockedFileStorage = mockStatic(FileStorage.class)) {
            ArrayList<Cashier> cashiers = new ArrayList<>();
            cashiers.add(testCashier);

            Cashier anotherCashier = new Cashier("Another Cashier", 1500.0);
            anotherCashier.setId(2);
            cashiers.add(anotherCashier);

            mockedFileStorage.when(() -> FileStorage.getCollection(Cashier.class)).thenReturn(cashiers);

            ArrayList<Cashier> result = cashierService.getAllEntities();

            assertEquals(2, result.size());
            assertEquals(testCashier.getId(), result.get(0).getId());
            assertEquals(anotherCashier.getId(), result.get(1).getId());
        }
    }

    @Test
    public void testCalculateTotalSalaries_ReturnsSumOfSalaries() {
        try (MockedStatic<FileStorage> mockedFileStorage = mockStatic(FileStorage.class)) {
            ArrayList<Cashier> cashiers = new ArrayList<>();
            cashiers.add(testCashier); // Salary: 1000.0

            Cashier anotherCashier = new Cashier("Another Cashier", 1500.0);
            anotherCashier.setId(2);
            cashiers.add(anotherCashier); // Salary: 1500.0

            mockedFileStorage.when(() -> FileStorage.getCollection(Cashier.class)).thenReturn(cashiers);

            double result = cashierService.calculateTotalSalaries();

            assertEquals(2500.0, result, 0.001);
        }
    }

    @Test
    public void testValidateEntity_InvalidId_ThrowsException() {
        testCashier.setId(-1);

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> cashierService.validateEntity(testCashier)
        );

        assertEquals("Невалиден ID на касиер", exception.getMessage());
    }

    @Test
    public void testValidateEntity_EmptyName_ThrowsException() {
        testCashier.setName("");

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> cashierService.validateEntity(testCashier)
        );

        assertEquals("Името на касиер с ID " + testCashier.getId() + " не може да бъде null или празно", exception.getMessage());
    }

    @Test
    public void testValidateEntity_NullName_ThrowsException() {
        testCashier.setName(null);

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> cashierService.validateEntity(testCashier)
        );

        assertEquals("Името на касиер с ID " + testCashier.getId() + " не може да бъде null или празно", exception.getMessage());
    }

    @Test
    public void testValidateEntity_NegativeSalary_ThrowsException() {
        testCashier.setSalary(-100.0);

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> cashierService.validateEntity(testCashier)
        );

        assertEquals("Заплатата на касиер с ID " + testCashier.getId() + " не може да бъде отрицателна", exception.getMessage());
    }
}
