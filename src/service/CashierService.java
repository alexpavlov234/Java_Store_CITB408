package service;

import dao.FileStorage;
import model.Cashier;

import java.util.ArrayList;
import java.util.Optional;
import java.util.function.Predicate;

/**
 * Услуга за управление на касиери.
 */
public class CashierService implements DataService<Cashier, Integer> {


    /**
     * Създава нов касиер.
     *
     * @param entity Касиерът за създаване.
     * @return Създаденият касиер.
     * @throws IllegalArgumentException ако данните за касиера са невалидни.
     */
    @Override
    public Cashier createEntity(Cashier entity) {
        validateEntity(entity);
        FileStorage.addObject(entity);
        return entity;
    }

    /**
     * Актуализира съществуващ касиер.
     *
     * @param entity Касиерът с актуализираните данни.
     * @return Актуализираният касиер.
     * @throws IllegalArgumentException ако данните за касиера са невалидни или ако касиер с такова ID не съществува.
     */
    @Override
    public Cashier updateEntity(Cashier entity) {
        validateEntity(entity);

        boolean updated = FileStorage.updateObject(
                entity, c -> c.getId() == entity.getId());

        if (!updated) {
            throw new IllegalArgumentException(
                    "Касиер с ID " + entity.getId() + " не съществува");
        }

        return entity;
    }


    /**
     * Намира касиер по неговото ID.
     *
     * @param integer ID на касиера.
     * @return Optional, съдържащ касиера, ако е намерен, в противен случай празен Optional.
     */
    @Override
    public Optional<Cashier> findEntityById(Integer integer) {
        return FileStorage.findObjectById(Cashier.class, integer);
    }

    /**
     * Връща списък с всички касиери.
     *
     * @return Списък с всички касиери.
     */
    @Override
    public ArrayList<Cashier> getAllEntities() {
        return FileStorage.getCollection(Cashier.class);
    }

    /**
     * Намира касиер по зададен филтър (предикат).
     *
     * @param filter Предикатът, по който се търси.
     * @return Optional, съдържащ първия намерен касиер, отговарящ на филтъра, в противен случай празен Optional.
     */
    @Override
    public Optional<Cashier> findEntityByFilter(Predicate<Cashier> filter) {
        return FileStorage.getCollection(Cashier.class)
                .stream()
                .filter(filter).findFirst();
    }

    /**
     * Намира всички касиери, отговарящи на зададен филтър (предикат).
     *
     * @param filter Предикатът, по който се търси.
     * @return Списък с касиери, отговарящи на филтъра.
     */
    @Override
    public ArrayList<Cashier> findEntitiesByFilter(Predicate<Cashier> filter) {
        return (ArrayList<Cashier>) FileStorage.getCollection(Cashier.class)
                .stream()
                .filter(filter).toList();
    }

    // Допълнителни специфични методи за касиери могат да бъдат добавени тук

    /**
     * Изчислява сумата на заплатите на всички касиери.
     *
     * @return Сумата на заплатите на всички касиери.
     */
    public double calculateTotalSalaries() {
        return getAllEntities().stream()
                .mapToDouble(Cashier::getSalary)
                .sum();
    }


    /**
     * Валидира данните на касиер.
     *
     * @param cashier Касиерът за валидиране.
     * @throws IllegalArgumentException ако някоя от данните е невалидна (null, отрицателно ID, празно име, отрицателна заплата).
     */
    @Override
    public void validateEntity(Cashier cashier) {
        if (cashier == null) {
            throw new IllegalArgumentException("Касиерът не може да бъде null");
        }

        if (cashier.getId() < 0) {
            throw new IllegalArgumentException("Невалиден ID на касиер");
        }

        if (cashier.getName() == null || cashier.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("Името на касиер с ID " + cashier.getId() + " не може да бъде null или празно");
        }

        if (cashier.getSalary() < 0) {
            throw new IllegalArgumentException("Заплатата на касиер с ID " + cashier.getId() + " не може да бъде отрицателна");
        }

    }
}
