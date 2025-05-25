package service;

import dao.FileStorage;
import model.Product;
import model.ProductCategory;
import model.Receipt;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mockStatic;

public class ReceiptServiceTest {

    private ReceiptService receiptService;
    private Receipt testReceipt;
    private Map<Product, Integer> purchasedProducts;

    @BeforeEach
    public void setUp() {
        receiptService = new ReceiptService();

        Product testProduct = new Product("Test Product", 10.0, ProductCategory.FOOD, LocalDate.now().plusDays(30));
        testProduct.setId(1);
        testProduct.setUnitSalePrice(15.0);

        purchasedProducts = new HashMap<>();
        purchasedProducts.put(testProduct, 2);

        testReceipt = new Receipt(1, 1, LocalDateTime.now(), purchasedProducts);
        testReceipt.setId(1);
    }

    @Test
    public void testCreateEntity_NullReceipt_ThrowsException() {
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> receiptService.createEntity(null)
        );

        assertEquals("Разписката не може да бъде null", exception.getMessage());
    }

    @Test
    public void testUpdateEntity_ValidReceipt_Success() {
        try (MockedStatic<FileStorage> mockedFileStorage = mockStatic(FileStorage.class)) {
            mockedFileStorage.when(() -> FileStorage.updateObject(eq(testReceipt), any())).thenReturn(true);

            Receipt result = receiptService.updateEntity(testReceipt);

            assertNotNull(result);
            assertEquals(testReceipt.getId(), result.getId());
            assertEquals(testReceipt.getClient(), result.getClient());
            assertEquals(testReceipt.getCashier(), result.getCashier());
            assertEquals(testReceipt.getDateTime(), result.getDateTime());
            assertEquals(testReceipt.getTotalPrice(), result.getTotalPrice());
            assertEquals(testReceipt.getPurchasedProducts(), result.getPurchasedProducts());
        }
    }

    @Test
    public void testUpdateEntity_NonExistingReceipt_ThrowsException() {
        try (MockedStatic<FileStorage> mockedFileStorage = mockStatic(FileStorage.class)) {
            mockedFileStorage.when(() -> FileStorage.updateObject(eq(testReceipt), any())).thenReturn(false);

            IllegalArgumentException exception = assertThrows(
                    IllegalArgumentException.class,
                    () -> receiptService.updateEntity(testReceipt)
            );

            assertEquals("Разписка с ID " + testReceipt.getId() + " не съществува", exception.getMessage());
        }
    }

    @Test
    public void testFindEntityById_ExistingId_ReturnsReceipt() {
        try (MockedStatic<FileStorage> mockedFileStorage = mockStatic(FileStorage.class)) {
            mockedFileStorage.when(() -> FileStorage.findObjectById(Receipt.class, 1)).thenReturn(Optional.of(testReceipt));

            Optional<Receipt> result = receiptService.findEntityById(1);

            assertTrue(result.isPresent());
            assertEquals(testReceipt.getId(), result.get().getId());
            assertEquals(testReceipt.getClient(), result.get().getClient());
            assertEquals(testReceipt.getCashier(), result.get().getCashier());
        }
    }

    @Test
    public void testFindEntityById_NonExistingId_ReturnsEmptyOptional() {
        try (MockedStatic<FileStorage> mockedFileStorage = mockStatic(FileStorage.class)) {
            mockedFileStorage.when(() -> FileStorage.findObjectById(Receipt.class, 999)).thenReturn(Optional.empty());

            Optional<Receipt> result = receiptService.findEntityById(999);

            assertFalse(result.isPresent());
        }
    }

    @Test
    public void testGetAllEntities_ReturnsAllReceipts() {
        try (MockedStatic<FileStorage> mockedFileStorage = mockStatic(FileStorage.class)) {
            ArrayList<Receipt> receipts = new ArrayList<>();
            receipts.add(testReceipt);

            Product anotherProduct = new Product("Another Product", 20.0, ProductCategory.NON_FOOD, LocalDate.now().plusDays(60));
            anotherProduct.setId(2);
            anotherProduct.setUnitSalePrice(30.0);

            Map<Product, Integer> anotherPurchasedProducts = new HashMap<>();
            anotherPurchasedProducts.put(anotherProduct, 3);

            Receipt anotherReceipt = new Receipt(2, 2, LocalDateTime.now(), anotherPurchasedProducts);
            anotherReceipt.setId(2);
            receipts.add(anotherReceipt);

            mockedFileStorage.when(() -> FileStorage.getCollection(Receipt.class)).thenReturn(receipts);

            ArrayList<Receipt> result = receiptService.getAllEntities();

            assertEquals(2, result.size());
            assertEquals(testReceipt.getId(), result.get(0).getId());
            assertEquals(anotherReceipt.getId(), result.get(1).getId());
        }
    }

    @Test
    public void testValidateEntity_InvalidId_ThrowsException() {
        testReceipt.setId(-1);

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> receiptService.validateEntity(testReceipt)
        );

        assertEquals("Невалиден ID на разписка", exception.getMessage());
    }

    @Test
    public void testValidateEntity_InvalidClientId_ThrowsException() {
        testReceipt.setClient(-1);

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> receiptService.validateEntity(testReceipt)
        );

        assertEquals("Разписката с ID " + testReceipt.getId() + " трябва да има клиент", exception.getMessage());
    }

    @Test
    public void testValidateEntity_InvalidCashierId_ThrowsException() {
        testReceipt.setCashier(-1);

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> receiptService.validateEntity(testReceipt)
        );

        assertEquals("Разписката с ID " + testReceipt.getId() + " трябва да има назначен касиер", exception.getMessage());
    }

    @Test
    public void testValidateEntity_NullDateTime_ThrowsException() {
        testReceipt.setDateTime(null);

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> receiptService.validateEntity(testReceipt)
        );

        assertEquals("Разписката с ID " + testReceipt.getId() + " трябва да има дата и час", exception.getMessage());
    }

    @Test
    public void testValidateEntity_NegativeTotalPrice_ThrowsException() {
        testReceipt = new Receipt(1, 1, LocalDateTime.now(), new HashMap<>());
        testReceipt.setId(1);

        try {
            java.lang.reflect.Field field = Receipt.class.getDeclaredField("totalPrice");
            field.setAccessible(true);
            field.set(testReceipt, -10.0);
        } catch (Exception e) {
            fail("Failed to set totalPrice using reflection: " + e.getMessage());
        }

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> receiptService.validateEntity(testReceipt)
        );

        assertEquals("Разписката с ID " + testReceipt.getId() + " трябва да има положителна цена", exception.getMessage());
    }

    @Test
    public void testValidateEntity_EmptyPurchasedProducts_ThrowsException() {
        testReceipt.setPurchasedProducts(new HashMap<>());

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> receiptService.validateEntity(testReceipt)
        );

        assertEquals("Разписката с ID " + testReceipt.getId() + " трябва да има поне един закупен продукт", exception.getMessage());
    }

    @Test
    public void testValidateEntity_NullPurchasedProducts_ThrowsException() {
        testReceipt.setPurchasedProducts(null);

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> receiptService.validateEntity(testReceipt)
        );

        assertEquals("Разписката с ID " + testReceipt.getId() + " трябва да има поне един закупен продукт", exception.getMessage());
    }
}
