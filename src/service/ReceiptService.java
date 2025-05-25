package service;

import dao.FileStorage;
import model.Receipt;

import java.util.ArrayList;
import java.util.Optional;
import java.util.function.Predicate;

/**
 * Услуга за управление на касови бележки.
 */
public class ReceiptService implements DataService<Receipt, Integer> {


    static {
        FileStorage.registerTypeWithCustomDir(Receipt.class, "data/receipts", true);
    }

    /**
     * Създава нова касова бележка.
     *
     * @param entity Касовата бележка за създаване.
     * @return Създадената касова бележка.
     * @throws IllegalArgumentException ако данните за касовата бележка са невалидни.
     */
    @Override
    public Receipt createEntity(Receipt entity) {
        validateEntity(entity);
        FileStorage.addObject(entity);
        return entity;
    }

    /**
     * Актуализира съществуваща касова бележка.
     *
     * @param entity Касовата бележка с актуализираните данни.
     * @return Актуализираната касова бележка.
     * @throws IllegalArgumentException ако данните за касовата бележка са невалидни или ако касова бележка с такова ID не съществува.
     */
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

    /**
     * Намира касова бележка по нейното ID.
     *
     * @param integer ID на касовата бележка.
     * @return Optional, съдържащ касовата бележка, ако е намерена, в противен случай празен Optional.
     */
    @Override
    public Optional<Receipt> findEntityById(Integer integer) {
        return FileStorage.findObjectById(Receipt.class, integer);
    }

    /**
     * Връща списък с всички касови бележки.
     *
     * @return Списък с всички касови бележки.
     */
    @Override
    public ArrayList<Receipt> getAllEntities() {
        return FileStorage.getCollection(Receipt.class);
    }

    /**
     * Намира касова бележка по зададен филтър (предикат).
     *
     * @param filter Предикатът, по който се търси.
     * @return Optional, съдържащ първата намерена касова бележка, отговаряща на филтъра, в противен случай празен Optional.
     */
    @Override
    public Optional<Receipt> findEntityByFilter(Predicate<Receipt> filter) {
        return FileStorage.getCollection(Receipt.class)
                .stream()
                .filter(filter)
                .findFirst();
    }

    /**
     * Намира всички касови бележки, отговарящи на зададен филтър (предикат).
     *
     * @param filter Предикатът, по който се търси.
     * @return Списък с касови бележки, отговарящи на филтъра.
     */
    @Override
    public ArrayList<Receipt> findEntitiesByFilter(Predicate<Receipt> filter) {
        return (ArrayList<Receipt>) FileStorage.getCollection(Receipt.class)
                .stream()
                .filter(filter)
                .toList();
    }

    /**
     * Валидира данните на касова бележка.
     *
     * @param entity Касовата бележка за валидиране.
     * @throws IllegalArgumentException ако някоя от данните е невалидна (null, отрицателно ID, липсващ клиент, касиер, дата, отрицателна цена или празен списък с продукти).
     */
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
        if (entity.getDateTime() == null) {
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
