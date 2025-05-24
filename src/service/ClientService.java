package service;

import dao.FileStorage;
import model.Client;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

public class ClientService implements DataService<Client, Integer> {
    @Override
    public Client createEntity(Client entity) {
        validateEntity(entity);
        FileStorage.addObject(entity);
        return entity; // Return the created entity
    }

    @Override
    public Client updateEntity(Client entity) {
        validateEntity(entity);

        boolean updated = FileStorage.updateObject(
                entity, c -> c.getId() == entity.getId());

        if (!updated) {
            throw new IllegalArgumentException(
                    "Клиент с ID " + entity.getId() + " не съществува");
        }

        return entity;
    }

    @Override
    public Optional<Client> findEntityById(Integer integer) {
        return FileStorage.findObject(Client.class, c -> c.getId() == integer);
    }

    @Override
    public ArrayList<Client> findAllEntities() {
        return FileStorage.getCollection(Client.class);
    }

    @Override
    public ArrayList<Client> findEntityByFilter(Predicate<Client> filter) {
        return (ArrayList<Client>) FileStorage.getCollection(Client.class)
                .stream()
                .filter(filter)
                .toList();
    }

    @Override
    public void printEntity(Client entity) {

    }

    @Override
    public void validateEntity(Client entity) throws IllegalArgumentException {
        if (entity == null) {
            throw new IllegalArgumentException("Клиентът не може да бъде null");
        }
        if ( entity.getId() < 0) {
            throw new IllegalArgumentException("Невалиден ID на клиент");
        }
        if (entity.getName() == null || entity.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("Името на клиент с ID " + entity.getId() + " не може да бъде null или празно");
        }
        if(entity.getBalance() < 0) {
            throw new IllegalArgumentException("Балансът на клиент с ID " + entity.getId() + " не може да бъде отрицателен");
        }
    }
}
