package service;

import dao.FileStorage;
import model.Cashier;

import java.util.ArrayList;
import java.util.Optional;
import java.util.function.Predicate;

public class CashierService implements DataService<Cashier, Integer> {


    @Override
    public Cashier createEntity(Cashier entity) {
        validateEntity(entity);
        FileStorage.addObject(entity);
        return entity;
    }

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


    @Override
    public Optional<Cashier> findEntityById(Integer integer) {
        return FileStorage.findObject(Cashier.class, c -> c.getId() == integer);
    }

    @Override
    public ArrayList<Cashier> findAllEntities() {
        return FileStorage.getCollection(Cashier.class);
    }

    @Override
    public Optional<Cashier> findEntityByFilter(Predicate<Cashier> filter) {
        return FileStorage.getCollection(Cashier.class)
                .stream()
                .filter(filter).findFirst();
    }

    @Override
    public ArrayList<Cashier> findEntitiesByFilter(Predicate<Cashier> filter){
        return (ArrayList<Cashier>) FileStorage.getCollection(Cashier.class)
                .stream()
                .filter(filter).toList();
    }

    // Допълнителни специфични методи за касиери могат да бъдат добавени тук

    /**
     * Изчислява сумата на заплатите на всички касиери
     *
     * @return Сумата на заплатите на всички касиери
     */
    public double calculateTotalSalaries() {
        return findAllEntities().stream()
                .mapToDouble(Cashier::getSalary)
                .sum();
    }

    @Override
    public void printEntity(Cashier entity) {
        if (entity == null) {
            System.out.println("Касиерът е null");
            return;
        }
        System.out.println("Касиер: " + entity.getName() + ", Заплата: " + entity.getSalary());
    }

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
