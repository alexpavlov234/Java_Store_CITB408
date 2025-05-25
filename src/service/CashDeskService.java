package service;

import dao.FileStorage;
import model.CashDesk;

import java.util.ArrayList;
import java.util.Optional;
import java.util.function.Predicate;

/**
 * Услуга за управление на касови апарати.
 */
public class CashDeskService implements DataService<CashDesk, Integer> {

    /**
     * Създава нов касов апарат.
     *
     * @param entity Касовият апарат за създаване.
     * @return Създаденият касов апарат.
     * @throws IllegalArgumentException ако данните за касовия апарат са невалидни.
     */
    @Override
    public CashDesk createEntity(CashDesk entity) {
        validateEntity(entity);
        FileStorage.addObject(entity);
        return entity;
    }

    /**
     * Актуализира съществуващ касов апарат.
     *
     * @param entity Касовият апарат с актуализираните данни.
     * @return Актуализираният касов апарат.
     * @throws IllegalArgumentException ако данните за касовия апарат са невалидни или ако касов апарат с такова ID не съществува.
     */
    @Override
    public CashDesk updateEntity(CashDesk entity) {
        validateEntity(entity);

        boolean updated = FileStorage.updateObject(
                entity, c -> c.getId() == entity.getId());

        if (!updated) {
            throw new IllegalArgumentException(
                    "Каса с ID " + entity.getId() + " не съществува");
        }

        return entity;
    }

    /**
     * Намира касов апарат по неговото ID.
     *
     * @param integer ID на касовия апарат.
     * @return Optional, съдържащ касовия апарат, ако е намерен, в противен случай празен Optional.
     */
    @Override
    public Optional<CashDesk> findEntityById(Integer integer) {
        return FileStorage.findObjectById(CashDesk.class, integer);
    }

    /**
     * Връща списък с всички касови апарати.
     *
     * @return Списък с всички касови апарати.
     */
    @Override
    public ArrayList<CashDesk> getAllEntities() {
        return FileStorage.getCollection(CashDesk.class);
    }

    /**
     * Намира касов апарат по зададен филтър (предикат).
     *
     * @param filter Предикатът, по който се търси.
     * @return Optional, съдържащ първия намерен касов апарат, отговарящ на филтъра, в противен случай празен Optional.
     */
    @Override
    public Optional<CashDesk> findEntityByFilter(Predicate<CashDesk> filter) {
        return FileStorage.getCollection(CashDesk.class)
                .stream()
                .filter(filter).findFirst();
    }

    /**
     * Намира всички касови апарати, отговарящи на зададен филтър (предикат).
     *
     * @param filter Предикатът, по който се търси.
     * @return Списък с касови апарати, отговарящи на филтъра.
     */
    @Override
    public ArrayList<CashDesk> findEntitiesByFilter(Predicate<CashDesk> filter) {
        return (ArrayList<CashDesk>) FileStorage.getCollection(CashDesk.class)
                .stream()
                .filter(filter).toList();
    }

    /**
     * Валидира данните на касов апарат.
     *
     * @param cashDesk Касовият апарат за валидиране.
     * @throws IllegalArgumentException ако някоя от данните е невалидна (null, отрицателно ID, липсващ магазин или касиер).
     */
    @Override
    public void validateEntity(CashDesk cashDesk) {
        if (cashDesk == null) {
            throw new IllegalArgumentException("Касиерът не може да бъде null");
        }
        if (cashDesk.getId() < 0) {
            throw new IllegalArgumentException("Невалиден ID на каса");
        }
        if (cashDesk.getStore() < 0) {
            throw new IllegalArgumentException("Касата с ID " + cashDesk.getId() + " трябва да бъде свързана с магазин");
        }
        if (cashDesk.getCashier() < 0) {
            throw new IllegalArgumentException("Касата с ID " + cashDesk.getId() + " трябва да има назначен касиер");
        }
    }
}
