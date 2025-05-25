package service;

import dao.FileStorage;
import model.Product;

import java.util.ArrayList;
import java.util.Optional;
import java.util.function.Predicate;

/**
 * Услуга за управление на продукти.
 */
public class ProductService implements DataService<Product, Integer> {

    /**
     * Създава нов продукт.
     *
     * @param entity Продуктът за създаване.
     * @return Създаденият продукт.
     * @throws IllegalArgumentException ако данните за продукта са невалидни.
     */
    @Override
    public Product createEntity(Product entity) {
        validateEntity(entity);
        FileStorage.addObject(entity);
        return entity;
    }

    /**
     * Актуализира съществуващ продукт.
     *
     * @param entity Продуктът с актуализираните данни.
     * @return Актуализираният продукт.
     * @throws IllegalArgumentException ако данните за продукта са невалидни или ако продукт с такова ID не съществува.
     */
    @Override
    public Product updateEntity(Product entity) {
        validateEntity(entity);

        boolean updated = FileStorage.updateObject(
                entity, p -> p.getId() == entity.getId());

        if (!updated) {
            throw new IllegalArgumentException(
                    "Продукт с ID " + entity.getId() + " не съществува");
        }

        return entity;
    }

    /**
     * Намира продукт по неговото ID.
     *
     * @param integer ID на продукта.
     * @return Optional, съдържащ продукта, ако е намерен, в противен случай празен Optional.
     */
    @Override
    public Optional<Product> findEntityById(Integer integer) {
        return FileStorage.findObjectById(Product.class, integer);
    }

    /**
     * Връща списък с всички продукти.
     *
     * @return Списък с всички продукти.
     */
    @Override
    public ArrayList<Product> getAllEntities() {
        return FileStorage.getCollection(Product.class);
    }

    /**
     * Намира продукт по зададен филтър (предикат).
     *
     * @param filter Предикатът, по който се търси.
     * @return Optional, съдържащ първия намерен продукт, отговарящ на филтъра, в противен случай празен Optional.
     */
    @Override
    public Optional<Product> findEntityByFilter(Predicate<Product> filter) {
        return FileStorage.getCollection(Product.class).stream()
                .filter(filter)
                .findFirst();
    }

    /**
     * Намира всички продукти, отговарящи на зададен филтър (предикат).
     *
     * @param filter Предикатът, по който се търси.
     * @return Списък с продукти, отговарящи на филтъра.
     */
    @Override
    public ArrayList<Product> findEntitiesByFilter(Predicate<Product> filter) {
        return (ArrayList<Product>) FileStorage.getCollection(Product.class).stream()
                .filter(filter)
                .toList();
    }

    /**
     * Валидира данните на продукт.
     *
     * @param product Продуктът за валидиране.
     * @throws IllegalArgumentException ако някоя от данните е невалидна (null, отрицателно ID, празно име, отрицателна цена, липсваща категория или срок на годност).
     */
    @Override
    public void validateEntity(Product product) {
        if (product == null) {
            throw new IllegalArgumentException("Продуктът не може да бъде null");
        }
        if (product.getId() < 0) {
            throw new IllegalArgumentException("ID на продукта трябва да бъде положително число");
        }
        if (product.getName() == null || product.getName().isEmpty()) {
            throw new IllegalArgumentException("Името на продукт с ID " + product.getId() + " не може да бъде null или празно");
        }

        if (product.getUnitPurchasePrice() < 0) {
            throw new IllegalArgumentException("Покупната цена на продукт с ID " + product.getId() + " трябва да бъде положително число");
        }

        if (product.getUnitSalePrice() < 0) {
            throw new IllegalArgumentException("Продажната цена на продукт с ID " + product.getId() + " трябва да бъде положително число");
        }

        if (product.getCategory() == null) {
            throw new IllegalArgumentException("Категорията на продукт с ID " + product.getId() + " не може да бъде null");
        }

        if (product.getExpirationDate() == null) {
            throw new IllegalArgumentException("Срокът на годност на продукт с ID " + product.getId() + " не може да бъде null");
        }

    }
}
