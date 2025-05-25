package service;

import dao.FileStorage;
import model.Receipt;

import java.util.ArrayList;
import java.util.Optional;
import java.util.function.Predicate;

public class ReceiptService implements DataService<Receipt, Integer> {


    static {
        FileStorage.registerTypeWithCustomDir(Receipt.class, "data/receipts", true);
    }

    @Override
    public Receipt createEntity(Receipt entity) {
        validateEntity(entity);
        FileStorage.addObject(entity);
        return entity;
    }

    @Override
    public Receipt updateEntity(Receipt entity) {
        validateEntity(entity);

        boolean updated = FileStorage.updateObject(
                entity, r -> r.getId() == entity.getId());

        if (!updated) {
            throw new IllegalArgumentException(
                    "Разписка с ID " + entity.getId() + " не съществува");
        }

        return entity;
    }

    @Override
    public Optional<Receipt> findEntityById(Integer integer) {
        return FileStorage.findObjectById(Receipt.class, integer);
    }

    @Override
    public ArrayList<Receipt> getAllEntities() {
        return FileStorage.getCollection(Receipt.class);
    }

    @Override
    public Optional<Receipt> findEntityByFilter(Predicate<Receipt> filter) {
        return FileStorage.getCollection(Receipt.class)
                .stream()
                .filter(filter)
                .findFirst();
    }

    @Override
    public ArrayList<Receipt> findEntitiesByFilter(Predicate<Receipt> filter) {
        return (ArrayList<Receipt>) FileStorage.getCollection(Receipt.class)
                .stream()
                .filter(filter)
                .toList();
    }

    @Override
    public void printEntity(Receipt entity) {


    }

    @Override
    public void validateEntity(Receipt entity) throws IllegalArgumentException {
        if (entity == null) {
            throw new IllegalArgumentException("Разписката не може да бъде null");
        }
        if (entity.getId() < 0) {
            throw new IllegalArgumentException("Невалиден ID на разписка");
        }
        if (entity.getClient() < 0) {
            throw new IllegalArgumentException("Разписката с ID " + entity.getId() + " трябва да има клиент");
        }
        if (entity.getCashier() < 0) {
            throw new IllegalArgumentException("Разписката с ID " + entity.getId() + " трябва да има назначен касиер");
        }
        if(entity.getDateTime() == null) {
            throw new IllegalArgumentException("Разписката с ID " + entity.getId() + " трябва да има дата и час");
        }
        if (entity.getTotalPrice() < 0) {
            throw new IllegalArgumentException("Разписката с ID " + entity.getId() + " трябва да има положителна цена");
        }
        if (entity.getPurchasedProducts() == null || entity.getPurchasedProducts().isEmpty()) {
            throw new IllegalArgumentException("Разписката с ID " + entity.getId() + " трябва да има поне един закупен продукт");
        }
    }
}
