package service;

import dao.FileStorage;
import model.Store;

import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

public class StoreService implements DataService<Store, Integer> {

    @Override
    public Store createEntity(Store entity) {
        validateEntity(entity);
        FileStorage.addObject(entity);
        return entity;
    }

    @Override
    public Store updateEntity(Store entity) {
        validateEntity(entity);

        boolean updated = FileStorage.updateObject(
                entity, s -> s.getId() == entity.getId());

        if (!updated) {
            throw new IllegalArgumentException(
                    "Магазин с ID " + entity.getId() + " не съществува");
        }

        return entity;
    }

    @Override
    public Optional<Store> findEntityById(Integer integer) {
        return FileStorage.findObject(Store.class, s -> s.getId() == integer);
    }

    @Override
    public List<Store> findAllEntities() {
        return FileStorage.getCollection(Store.class);
    }

    @Override
    public List<Store> findEntityByFilter(Predicate<Store> filter) {
        return FileStorage.getCollection(Store.class)
                .stream()
                .filter(filter)
                .toList();
    }

    @Override
    public void printEntity(Store entity) {
    }

    @Override
    public void validateEntity(Store entity) {
        if (entity == null) {
            throw new IllegalArgumentException("Магазинът не може да бъде null");
        }
        if (entity.getId() <= 0) {
            throw new IllegalArgumentException("Невалиден ID на магазина");
        }
        if (entity.getName() == null || entity.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("Името на магазин с ID " + entity.getId() + " не може да бъде null или празно");
        }
        if(entity.getCashiersIds() == null) {
            throw new IllegalArgumentException("Списъкът с ID на касиерите за магазин с ID " + entity.getId() + " не може да бъде null");
        }
        if (entity.getReceiptsIds() == null) {
            throw new IllegalArgumentException("Списъкът с ID на издадените касови бележки за магазин с ID " + entity.getId() + " не може да бъде null");
        }
        if (entity.getProductsInStock() == null) {
            throw new IllegalArgumentException("Списъкът с продукти в наличност не може да бъде null");
        }
        if (entity.getProductsSold() == null) {
            throw new IllegalArgumentException("Списъкът с продадени продукти не може да бъде null");
        }
    }
}
