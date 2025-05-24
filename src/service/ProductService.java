package service;

import dao.FileStorage;
import model.Product;

import java.util.ArrayList;
import java.util.Optional;
import java.util.function.Predicate;

public class ProductService implements DataService<Product, Integer> {

    @Override
    public Product createEntity(Product entity) {
        validateEntity(entity);
        FileStorage.addObject(entity);
        return entity;
    }

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

    @Override
    public Optional<Product> findEntityById(Integer integer) {
        return FileStorage.findObject(Product.class, p -> p.getId() == integer);
    }

    @Override
    public ArrayList<Product> findAllEntities() {
        return FileStorage.getCollection(Product.class);
    }

    @Override
    public ArrayList<Product> findEntityByFilter(Predicate<Product> filter) {
        return (ArrayList<Product>) FileStorage.getCollection(Product.class).stream()
                .filter(filter)
                .toList();
    }

    @Override
    public void printEntity(Product entity) {

    }

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
