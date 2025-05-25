package service;

import dao.FileStorage;
import model.Product;
import model.ProductCategory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mockStatic;

public class ProductServiceTest {

    private ProductService productService;
    private Product testProduct;

    @BeforeEach
    public void setUp() {
        productService = new ProductService();
        testProduct = new Product("Test Product", 10.0, ProductCategory.FOOD, LocalDate.now().plusDays(30));
        testProduct.setId(1);
        testProduct.setUnitSalePrice(15.0);
    }

    @Test
    public void testCreateEntity_NullProduct_ThrowsException() {
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> productService.createEntity(null)
        );

        assertEquals("Продуктът не може да бъде null", exception.getMessage());
    }

    @Test
    public void testUpdateEntity_ValidProduct_Success() {
        try (MockedStatic<FileStorage> mockedFileStorage = mockStatic(FileStorage.class)) {
            mockedFileStorage.when(() -> FileStorage.updateObject(eq(testProduct), any())).thenReturn(true);

            Product result = productService.updateEntity(testProduct);

            assertNotNull(result);
            assertEquals(testProduct.getId(), result.getId());
            assertEquals(testProduct.getName(), result.getName());
            assertEquals(testProduct.getUnitPurchasePrice(), result.getUnitPurchasePrice());
            assertEquals(testProduct.getUnitSalePrice(), result.getUnitSalePrice());
            assertEquals(testProduct.getCategory(), result.getCategory());
            assertEquals(testProduct.getExpirationDate(), result.getExpirationDate());
        }
    }

    @Test
    public void testUpdateEntity_NonExistingProduct_ThrowsException() {
        try (MockedStatic<FileStorage> mockedFileStorage = mockStatic(FileStorage.class)) {
            mockedFileStorage.when(() -> FileStorage.updateObject(eq(testProduct), any())).thenReturn(false);

            IllegalArgumentException exception = assertThrows(
                    IllegalArgumentException.class,
                    () -> productService.updateEntity(testProduct)
            );

            assertEquals("Продукт с ID " + testProduct.getId() + " не съществува", exception.getMessage());
        }
    }

    @Test
    public void testFindEntityById_ExistingId_ReturnsProduct() {
        try (MockedStatic<FileStorage> mockedFileStorage = mockStatic(FileStorage.class)) {
            mockedFileStorage.when(() -> FileStorage.findObjectById(Product.class, 1)).thenReturn(Optional.of(testProduct));

            Optional<Product> result = productService.findEntityById(1);

            assertTrue(result.isPresent());
            assertEquals(testProduct.getId(), result.get().getId());
            assertEquals(testProduct.getName(), result.get().getName());
        }
    }

    @Test
    public void testFindEntityById_NonExistingId_ReturnsEmptyOptional() {
        try (MockedStatic<FileStorage> mockedFileStorage = mockStatic(FileStorage.class)) {
            mockedFileStorage.when(() -> FileStorage.findObjectById(Product.class, 999)).thenReturn(Optional.empty());

            Optional<Product> result = productService.findEntityById(999);

            assertFalse(result.isPresent());
        }
    }

    @Test
    public void testGetAllEntities_ReturnsAllProducts() {
        try (MockedStatic<FileStorage> mockedFileStorage = mockStatic(FileStorage.class)) {
            ArrayList<Product> products = new ArrayList<>();
            products.add(testProduct);

            Product anotherProduct = new Product("Another Product", 20.0, ProductCategory.NON_FOOD, LocalDate.now().plusDays(60));
            anotherProduct.setId(2);
            anotherProduct.setUnitSalePrice(30.0);
            products.add(anotherProduct);

            mockedFileStorage.when(() -> FileStorage.getCollection(Product.class)).thenReturn(products);

            ArrayList<Product> result = productService.getAllEntities();

            assertEquals(2, result.size());
            assertEquals(testProduct.getId(), result.get(0).getId());
            assertEquals(anotherProduct.getId(), result.get(1).getId());
        }
    }

    @Test
    public void testValidateEntity_InvalidId_ThrowsException() {
        testProduct.setId(-1);

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> productService.validateEntity(testProduct)
        );

        assertEquals("ID на продукта трябва да бъде положително число", exception.getMessage());
    }

    @Test
    public void testValidateEntity_EmptyName_ThrowsException() {
        testProduct.setName("");

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> productService.validateEntity(testProduct)
        );

        assertEquals("Името на продукт с ID " + testProduct.getId() + " не може да бъде null или празно", exception.getMessage());
    }

    @Test
    public void testValidateEntity_NullName_ThrowsException() {
        testProduct.setName(null);

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> productService.validateEntity(testProduct)
        );

        assertEquals("Името на продукт с ID " + testProduct.getId() + " не може да бъде null или празно", exception.getMessage());
    }

    @Test
    public void testValidateEntity_NegativePurchasePrice_ThrowsException() {
        testProduct.setUnitPurchasePrice(-10.0);

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> productService.validateEntity(testProduct)
        );

        assertEquals("Покупната цена на продукт с ID " + testProduct.getId() + " трябва да бъде положително число", exception.getMessage());
    }

    @Test
    public void testValidateEntity_NegativeSalePrice_ThrowsException() {
        testProduct.setUnitSalePrice(-15.0);

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> productService.validateEntity(testProduct)
        );

        assertEquals("Продажната цена на продукт с ID " + testProduct.getId() + " трябва да бъде положително число", exception.getMessage());
    }

    @Test
    public void testValidateEntity_NullCategory_ThrowsException() {
        testProduct.setCategory(null);

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> productService.validateEntity(testProduct)
        );

        assertEquals("Категорията на продукт с ID " + testProduct.getId() + " не може да бъде null", exception.getMessage());
    }

    @Test
    public void testValidateEntity_NullExpirationDate_ThrowsException() {
        testProduct.setExpirationDate(null);

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> productService.validateEntity(testProduct)
        );

        assertEquals("Срокът на годност на продукт с ID " + testProduct.getId() + " не може да бъде null", exception.getMessage());
    }
}
