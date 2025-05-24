package dao;

import java.io.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Клас за съхранение на обекти във файлове, организирани по тип (подобно на таблици в база данни)
 * с автоматично генерирани имена на файлове
 */
public class FileStorage {
    private static final String DATA_DIR = "data/";
    private static final String FILE_EXTENSION = ".dat";
    private static final Map<Class<?>, String> TYPE_TO_FILENAME = new HashMap<>();
    private static final Map<Class<?>, List<?>> CACHED_COLLECTIONS = new ConcurrentHashMap<>();

    /**
     * Регистрира тип обект с име на файл за съхранение.
     * Ако не регистрирате типа, ще се използва автоматично генерирано име.
     */
    public static <T> void registerType(Class<T> type, String fileName) {
        TYPE_TO_FILENAME.put(type, fileName);
    }

    /**
     * Зарежда всички регистрирани типове данни от файлове
     */
    public static void loadAllData() {
        for (Class<?> type : TYPE_TO_FILENAME.keySet()) {
            loadCollection(type);
        }
    }

    /**
     * Запазва всички регистрирани типове данни във файлове
     */
    public static void saveAllData() {
        for (Class<?> type : CACHED_COLLECTIONS.keySet()) {
            saveCollection(type);
        }
    }

    /**
     * Зарежда колекция от обекти от даден тип
     */
    @SuppressWarnings("unchecked")
    public static <T> List<T> getCollection(Class<T> type) {
        if (!CACHED_COLLECTIONS.containsKey(type)) {
            loadCollection(type);
        }
        return (List<T>) CACHED_COLLECTIONS.getOrDefault(type, new ArrayList<>());
    }

    /**
     * Добавя нов обект към колекцията и го запазва
     */
    public static <T> void addObject(T object) {
        Class<?> type = object.getClass();
        List<Object> collection = (List<Object>) CACHED_COLLECTIONS.computeIfAbsent(
                type, k -> new ArrayList<>());
        collection.add(object);
        saveCollection(type);
    }

    /**
     * Актуализира обект в колекцията
     * @param object Обектът, който трябва да бъде актуализиран
     * @param matcher Функция, която определя дали обектът съвпада с търсения елемент
     */
    public static <T> boolean updateObject(T object, MatcherFunction<T> matcher) {
        Class<?> type = object.getClass();
        List<T> collection = getCollection((Class<T>) type);

        for (int i = 0; i < collection.size(); i++) {
            if (matcher.matches(collection.get(i))) {
                collection.set(i, object);
                saveCollection(type);
                return true;
            }
        }
        return false;
    }

    /**
     * Изтрива обект от колекцията
     * @param type Типът на колекцията
     * @param matcher Функция, която определя дали обектът трябва да бъде изтрит
     */
    public static <T> boolean removeObject(Class<T> type, MatcherFunction<T> matcher) {
        List<T> collection = getCollection(type);
        boolean removed = collection.removeIf(obj -> matcher.matches(obj));
        if (removed) {
            saveCollection(type);
        }
        return removed;
    }

    /**
     * Търси обект в колекцията
     * @param type Типът на колекцията
     * @param matcher Функция, която определя критериите за търсене
     */
    public static <T> Optional<T> findObject(Class<T> type, MatcherFunction<T> matcher) {
        List<T> collection = getCollection(type);
        return collection.stream()
                .filter(obj -> matcher.matches(obj))
                .findFirst();
    }

    /**
     * Намира всички обекти, отговарящи на критериите
     * @param type Типът на колекцията
     * @param matcher Функция, която определя критериите за търсене
     */
    public static <T> List<T> findAllObjects(Class<T> type, MatcherFunction<T> matcher) {
        List<T> collection = getCollection(type);
        return collection.stream()
                .filter(obj -> matcher.matches(obj))
                .collect(ArrayList::new, ArrayList::add, ArrayList::addAll);
    }

    /**
     * Връща името на файла за даден тип
     */
    private static <T> String getFileNameForType(Class<T> type) {
        // Първо проверяваме дали има изрично регистрирано име
        String fileName = TYPE_TO_FILENAME.get(type);
        if (fileName != null) {
            return fileName;
        }

        // Ако няма, създаваме име от името на класа
        // Запазваме PascalCase формата (CashDesk -> CashDesk.dat)
        return type.getSimpleName() + FILE_EXTENSION;
    }

    // Приватни помощни методи


    @SuppressWarnings("unchecked")
    private static <T> void loadCollection(Class<T> type) {
        String fileName = getFileNameForType(type);
        File file = new File(DATA_DIR + fileName);
        List<T> collection = new ArrayList<>();

        if (file.exists()) {
            try (ObjectInputStream ois = new ObjectInputStream(
                    new FileInputStream(file))) {
                collection = (List<T>) ois.readObject();
            } catch (IOException | ClassNotFoundException e) {
                System.err.println("Грешка при четене на колекция " + type.getSimpleName() +
                        ": " + e.getMessage());
            }
        }

        CACHED_COLLECTIONS.put(type, collection);
    }

    private static <T> void saveCollection(Class<T> type) {
        List<T> collection = (List<T>) CACHED_COLLECTIONS.get(type);
        if (collection == null) {
            return;
        }

        String fileName = getFileNameForType(type);

        try {
            File dir = new File(DATA_DIR);
            if (!dir.exists()) {
                dir.mkdirs();
            }

            try (ObjectOutputStream oos = new ObjectOutputStream(
                    new FileOutputStream(DATA_DIR + fileName))) {
                oos.writeObject(collection);
            }
        } catch (IOException e) {
            System.err.println("Грешка при запис на колекция " + type.getSimpleName() +
                    ": " + e.getMessage());
        }
    }

    @FunctionalInterface
    public interface MatcherFunction<T> {
        boolean matches(T object);
    }
}