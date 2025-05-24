package service;

import dao.FileStorage;
import model.CashDesk;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

public class CashDeskService implements DataService<CashDesk, Integer> {

    @Override
    public CashDesk createEntity(CashDesk entity) {
        validateEntity(entity);
        FileStorage.addObject(entity);
        return entity;
    }

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

    @Override
    public Optional<CashDesk> findEntityById(Integer integer) {
        return FileStorage.findObject(CashDesk.class, c -> c.getId() == integer);
    }

    @Override
    public ArrayList<CashDesk> findAllEntities() {
        return FileStorage.getCollection(CashDesk.class);
    }

    @Override
    public ArrayList<CashDesk> findEntityByFilter(Predicate<CashDesk> filter) {
        return (ArrayList<CashDesk>) FileStorage.getCollection(CashDesk.class)
                .stream()
                .filter(filter)
                .toList();
    }

    @Override
    public void printEntity(CashDesk entity) {

    }

    @Override
    public void validateEntity(CashDesk cashDesk) {
        if (cashDesk == null) {
            throw new IllegalArgumentException("Касиерът не може да бъде null");
        }
        if (cashDesk.getId() < 0) {
            throw new IllegalArgumentException("Невалиден ID на каса");
        }
        if (cashDesk.getCashier() < 0) {
            throw new IllegalArgumentException("Касата с ID " + cashDesk.getId() + " трябва да има назначен касиер");
        }
    }
}
