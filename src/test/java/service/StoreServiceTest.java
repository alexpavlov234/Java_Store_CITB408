package service;

import dao.FileStorage;
import model.ProductCategory;
import model.Store;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

public class StoreServiceTest {

    private StoreService storeService;
    private Store testStore;

    @BeforeEach
    public void setUp() {
        storeService = new StoreService();

        Map<ProductCategory, Double> markupPercentages = new HashMap<>();
        markupPercentages.put(ProductCategory.FOOD, 20.0);
        markupPercentages.put(ProductCategory.NON_FOOD, 30.0);

        testStore = new Store("Тестов магазин", markupPercentages, 5, 15.0);
        testStore.setId(1);
    }


    @Test
    public void testCreateEntity_NullStore_ThrowsException() {
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> storeService.createEntity(null)
        );

        assertEquals("Магазинът не може да бъде null", exception.getMessage());
    }

    @Test
    public void testUpdateEntity_ValidStore_Success() {
        try (MockedStatic<FileStorage> mockedFileStorage = mockStatic(FileStorage.class)) {

            mockedFileStorage.when(() -> FileStorage.updateObject(eq(testStore), any())).thenReturn(true);


            Store result = storeService.updateEntity(testStore);


            assertNotNull(result);
            assertEquals(testStore.getId(), result.getId());
            assertEquals(testStore.getName(), result.getName());
            mockedFileStorage.verify(() -> FileStorage.updateObject(eq(testStore), any()));
        }
    }

    @Test
    public void testUpdateEntity_NonExistingStore_ThrowsException() {
        try (MockedStatic<FileStorage> mockedFileStorage = mockStatic(FileStorage.class)) {

            mockedFileStorage.when(() -> FileStorage.updateObject(eq(testStore), any())).thenReturn(false);

            IllegalArgumentException exception = assertThrows(
                    IllegalArgumentException.class,
                    () -> storeService.updateEntity(testStore)
            );

            assertEquals("Магазин с ID " + testStore.getId() + " не съществува", exception.getMessage());
        }
    }

    @Test
    public void testFindEntityById_ExistingId_ReturnsStore() {
        try (MockedStatic<FileStorage> mockedFileStorage = mockStatic(FileStorage.class)) {

            mockedFileStorage.when(() -> FileStorage.findObjectById(Store.class, 1)).thenReturn(Optional.of(testStore));


            Optional<Store> result = storeService.findEntityById(1);


            assertTrue(result.isPresent());
            assertEquals(testStore.getId(), result.get().getId());
            assertEquals(testStore.getName(), result.get().getName());
        }
    }

    @Test
    public void testFindEntityById_NonExistingId_ReturnsEmptyOptional() {
        try (MockedStatic<FileStorage> mockedFileStorage = mockStatic(FileStorage.class)) {
            mockedFileStorage.when(() -> FileStorage.findObjectById(Store.class, 999)).thenReturn(Optional.empty());

            Optional<Store> result = storeService.findEntityById(999);

            assertFalse(result.isPresent());
        }
    }

    @Test
    public void testGetAllEntities_ReturnsAllStores() {
        try (MockedStatic<FileStorage> mockedFileStorage = mockStatic(FileStorage.class)) {
            ArrayList<Store> stores = new ArrayList<>();
            stores.add(testStore);

            Store anotherStore = new Store("Друг магазин", new HashMap<>(), 3, 10.0);
            anotherStore.setId(2);
            stores.add(anotherStore);

            mockedFileStorage.when(() -> FileStorage.getCollection(Store.class)).thenReturn(stores);

            ArrayList<Store> result = storeService.getAllEntities();

            assertEquals(2, result.size());
            assertEquals(testStore.getId(), result.get(0).getId());
            assertEquals(anotherStore.getId(), result.get(1).getId());
        }
    }

    @Test
    public void testValidateEntity_InvalidId_ThrowsException() {
        testStore.setId(-1);

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> storeService.validateEntity(testStore)
        );

        assertEquals("Невалиден ID на магазина", exception.getMessage());
    }

    @Test
    public void testValidateEntity_EmptyName_ThrowsException() {
        testStore.setId(1);
        testStore = spy(testStore);
        when(testStore.getName()).thenReturn("");

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> storeService.validateEntity(testStore)
        );

        assertEquals("Името на магазин с ID " + testStore.getId() + " не може да бъде null или празно", exception.getMessage());
    }
}

