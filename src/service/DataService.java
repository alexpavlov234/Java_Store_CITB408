package service;

import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

/**
 * Общ интерфейс за услуги (services), които управляват данни
 * @param <T> Тип на обектите, с които работи услугата
 * @param <ID> Тип на идентификатора на обектите
 */
public interface DataService<T, ID> {
    
    /**
     * Създава нов обект
     * @param entity обектът, който ще бъде създаден
     * @return създаденият обект
     */
    T createEntity(T entity);
    
    /**
     * Актуализира съществуващ обект
     * @param entity обектът, който ще бъде актуализиран
     * @return актуализираният обект
     */
    T updateEntity(T entity);
    

    /**
     * Намира обект по идентификатор
     * @param id идентификаторът на обекта
     * @return обектът, ако е намерен, или празен Optional в противен случай
     */
    Optional<T> findEntityById(ID id);
    
    /**
     * Намира всички обекти
     * @return списък с всички обекти
     */
    List<T> findAllEntities();
    
    /**
     * Намира всички обекти, които отговарят на определено условие
     * @param filter условието, на което трябва да отговарят обектите
     * @return списък с обекти, отговарящи на условието
     */
    List<T> findEntityByFilter(Predicate<T> filter);

    /**
     * Отпечатва информация за обект в конзолата
     * @param entity обектът, който трябва да бъде отпечатан
     */
    void printEntity(T entity);

    /**
     * Валидация на обект
     * @param entity обектът, който трябва да бъде валидиран
     * @throws IllegalArgumentException ако обектът не е валиден
     */
    void validateEntity(T entity) throws IllegalArgumentException;
}
