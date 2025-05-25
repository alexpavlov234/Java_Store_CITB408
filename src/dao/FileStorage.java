package dao;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import java.io.*;
import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.time.LocalDateTime;
import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Клас за съхранение на обекти във файлове, организирани по тип, записани в JSON формат в текстови файлове.
 * с автоматично генерирани имена на файлове
 */
public class FileStorage {
    private static final String DATA_DIR = "data/";
    private static final String FILE_EXTENSION = ".txt";
    private static final Map<Class<?>, String> TYPE_TO_FILENAME = new HashMap<>();
    private static final Map<Class<?>, String> TYPE_TO_CUSTOM_DIR = new HashMap<>();
    private static final Map<Class<?>, Boolean> TYPE_TO_SEPARATE_FILES = new HashMap<>();
    private static final Map<Class<?>, List<?>> CACHED_COLLECTIONS = new ConcurrentHashMap<>();
    private static final Map<Class<?>, AtomicLong> idCounters = new ConcurrentHashMap<>();

    // Gson инстанция за сериализация и десериализация на обекти - използва се за конвертиране на обекти в JSON и обратно
    private static final Gson gson = new GsonBuilder()
            .setPrettyPrinting()
            .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
            .registerTypeAdapter(LocalDate.class, new LocalDateAdapter())
            .create();

    /**
     * Регистрира името на файл за съхранение на обекти от даден тип.
     * Ако не регистрирате типа, ще се използва автоматично генерирано име.
     */
    public static <T> void registerType(Class<T> type, String fileName) {
        TYPE_TO_FILENAME.put(type, fileName);
    }

    /**
     * Регистрира за даден тип обекти, че трябва да се съхраняват в персонализирана директория и като отделни файлове.
     */
    public static <T> void registerTypeWithCustomDir(Class<T> type, String customDir, boolean separateFiles) {
        TYPE_TO_CUSTOM_DIR.put(type, customDir);
        TYPE_TO_SEPARATE_FILES.put(type, separateFiles);
    }

    /**
     * Зарежда данните за всички регистирани обекти,
     * Обхожда всички типове, регистрирани с имена на файлове и с персонализирани директории,
     * като гарантира, че всеки тип се зарежда само веднъж.
     */
    public static void loadAllData() {
        // Зарежда колекции от типове, които имат персонализирани имена на файлове
        for (Class<?> type : TYPE_TO_FILENAME.keySet()) {
            loadCollection(type);
        }
        // Зарежда колекции от типове, които имат персонализирани директории
        for (Class<?> type : TYPE_TO_CUSTOM_DIR.keySet()) {
            // Проверява дали типът вече е зареден
            if (!TYPE_TO_FILENAME.containsKey(type)) {
                loadCollection(type);
            }
        }
    }

    /**
     * Запазва всички регистрирани типове данни във файлове от съответните им кешове.
     */
    public static void saveAllData() {
        for (Class<?> type : CACHED_COLLECTIONS.keySet()) {
            saveCollection(type);
        }
    }

    /**
     * Зарежда колекция от обекти от даден тип, ако не е заредена в кеша.
     */
    @SuppressWarnings("unchecked")
    public static <T> ArrayList<T> getCollection(Class<T> type) {
        if (!CACHED_COLLECTIONS.containsKey(type)) {
            loadCollection(type);
        }
        return (ArrayList<T>) CACHED_COLLECTIONS.getOrDefault(type, new ArrayList<>());
    }

    /**
     * Добавя нов обект към колекцията и го запазва - като в файл, така и в кеша.
     * Ако обектът има поле "id" и то е нула или null, ще му бъде зададен автоматично генериран идентификатор.
     *
     * @param object Обектът, който трябва да бъде добавен
     * @param <T> Типът на обекта
     */
    public static <T> void addObject(T object) {
        Class<?> type = object.getClass();
        try {
            Field idField = type.getDeclaredField("id");
            idField.setAccessible(true);
            Object value = idField.get(object);

            List<T> collection = getCollection((Class<T>) type);

            if (value != null && value instanceof Number && ((Number) value).longValue() != 0L) {
                long objectId = ((Number) value).longValue();
                for (T item : collection) {
                    Object itemId = idField.get(item);
                    if (itemId instanceof Number && ((Number) itemId).longValue() == objectId) {
                        throw new IllegalArgumentException("Object with ID " + objectId + " already exists");
                    }
                }
            } else {
                long maxId = 0;
                try {
                    for (T item : collection) {
                        Object itemId = idField.get(item);
                        if (itemId instanceof Number) {
                            long id = ((Number) itemId).longValue();
                            if (id > maxId) {
                                maxId = id;
                            }
                        }
                    }
                } catch (IllegalAccessException e) {
                    // Ignore errors
                }

                long newId = maxId + 1;
                AtomicLong counter = idCounters.computeIfAbsent(type, k -> new AtomicLong(newId));

                if (idField.getType().equals(int.class) || idField.getType().equals(Integer.class)) {
                    idField.set(object, (int) newId);
                } else if (idField.getType().equals(long.class) || idField.getType().equals(Long.class)) {
                    idField.set(object, newId);
                }
            }

            collection.add(object);
            CACHED_COLLECTIONS.put(type, collection);
            saveCollection(type);

            // If the type is configured to save as separate files, also save individual file
            if (Boolean.TRUE.equals(TYPE_TO_SEPARATE_FILES.get(type))) {
                saveIndividualObject(object);
            }

        } catch (NoSuchFieldException | IllegalAccessException e) {
            List<Object> collection = (List<Object>) CACHED_COLLECTIONS.computeIfAbsent(
                    type, k -> new ArrayList<>());
            collection.add(object);
            saveCollection(type);
        }
    }

    /**
     * Актуализира обект в колекцията
     *
     * @param object  Обектът, който трябва да бъде актуализиран
     * @param matcher Функция, която определя дали обектът съвпада с търсения елемент
     * @return true ако обектът е актуализиран, false ако не е намерен
     */
    public static <T> boolean updateObject(T object, MatcherFunction<T> matcher) {
        Class<?> type = object.getClass();
        List<T> collection = getCollection((Class<T>) type);

        for (int i = 0; i < collection.size(); i++) {
            if (matcher.matches(collection.get(i))) {
                collection.set(i, object);
                saveCollection(type);

                // If the type is configured to save as separate files, also update individual file
                if (Boolean.TRUE.equals(TYPE_TO_SEPARATE_FILES.get(type))) {
                    saveIndividualObject(object);
                }

                return true;
            }
        }
        return false;
    }

    /**
     * Изтрива обект от колекцията
     *
     * @param type    Типът на колекцията
     * @param matcher Функция, която определя дали обектът трябва да бъде изтрит
     */
    public static <T> boolean removeObject(Class<T> type, MatcherFunction<T> matcher) {
        List<T> collection = getCollection(type);

        // If using separate files, find and delete the individual file
        if (Boolean.TRUE.equals(TYPE_TO_SEPARATE_FILES.get(type))) {
            for (T obj : collection) {
                if (matcher.matches(obj)) {
                    deleteIndividualObject(obj);
                    break;
                }
            }
        }

        boolean removed = collection.removeIf(obj -> matcher.matches(obj));
        if (removed) {
            saveCollection(type);
        }
        return removed;
    }

    /**
     * Търси обект в колекцията
     *
     * @param type    Типът на колекцията
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
     *
     * @param type    Типът на колекцията
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
        String fileName = TYPE_TO_FILENAME.get(type);
        if (fileName != null) {
            return fileName;
        }
        return type.getSimpleName() + FILE_EXTENSION;
    }

    /**
     * Връща директорията за
     */
    private static <T> String getDirectoryForType(Class<T> type) {
        String customDir = TYPE_TO_CUSTOM_DIR.get(type);
        if (customDir != null) {
            return customDir.endsWith("/") ? customDir : customDir + "/";
        }
        return DATA_DIR;
    }

    /**
     * Saves an individual object to its own file
     */
    private static <T> void saveIndividualObject(T object) {
        Class<?> type = object.getClass();
        String dir = getDirectoryForType(type);

        try {
            File dirFile = new File(dir);
            if (!dirFile.exists()) {
                dirFile.mkdirs();
            }

            // Get object ID for filename
            Field idField = type.getDeclaredField("id");
            idField.setAccessible(true);
            Object id = idField.get(object);

            String fileName = type.getSimpleName() + "_" + id + FILE_EXTENSION;
            try (Writer writer = new FileWriter(dir + fileName)) {
                gson.toJson(object, writer);
            }
        } catch (IOException | NoSuchFieldException | IllegalAccessException e) {
            System.err.println("Error saving individual object " + type.getSimpleName() +
                    ": " + e.getMessage());
        }
    }

    /**
     * Deletes an individual object file
     */
    private static <T> void deleteIndividualObject(T object) {
        Class<?> type = object.getClass();
        String dir = getDirectoryForType(type);

        try {
            Field idField = type.getDeclaredField("id");
            idField.setAccessible(true);
            Object id = idField.get(object);

            String fileName = type.getSimpleName() + "_" + id + FILE_EXTENSION;
            File file = new File(dir + fileName);
            if (file.exists()) {
                file.delete();
            }
        } catch (NoSuchFieldException | IllegalAccessException e) {
            System.err.println("Error deleting individual object file: " + e.getMessage());
        }
    }

    @SuppressWarnings("unchecked")
    private static <T> void loadCollection(Class<T> type) {
        String dir = getDirectoryForType(type);
        String fileName = getFileNameForType(type);
        File file = new File(dir + fileName);
        List<T> collection = new ArrayList<>();

        // If using separate files, load from individual files
        if (Boolean.TRUE.equals(TYPE_TO_SEPARATE_FILES.get(type))) {
            File directory = new File(dir);
            if (directory.exists() && directory.isDirectory()) {
                File[] files = directory.listFiles((d, name) ->
                        name.startsWith(type.getSimpleName() + "_") && name.endsWith(FILE_EXTENSION));

                if (files != null) {
                    for (File individualFile : files) {
                        try (Reader reader = new FileReader(individualFile)) {
                            T obj = gson.fromJson(reader, type);
                            if (obj != null) {
                                collection.add(obj);
                            }
                        } catch (IOException e) {
                            System.err.println("Error reading individual file " + individualFile.getName() +
                                    ": " + e.getMessage());
                        }
                    }
                }
            }
        }
        // Otherwise, load from the collection file
        else if (file.exists()) {
            try (Reader reader = new FileReader(file)) {
                Type listType = TypeToken.getParameterized(ArrayList.class, type).getType();
                collection = gson.fromJson(reader, listType);
                if (collection == null) {
                    collection = new ArrayList<>();
                }
            } catch (IOException e) {
                System.err.println("Error reading collection " + type.getSimpleName() +
                        ": " + e.getMessage());
            }
        }

        CACHED_COLLECTIONS.put(type, collection);
    }

    /**
     * Запазва колекцията от обекти в съответния файл или файлове и в кеша.
     * Ако е регистрирано, че трябва да се използват отделни файлове за всеки обект,
     * ще запише всеки обект в отделен файл.
     * В противен случай ще запише цялата колекция в един файл.
     */
    private static <T> void saveCollection(Class<T> type) {
        List<T> collection = (List<T>) CACHED_COLLECTIONS.get(type);
        if (collection == null) {
            return;
        }

        // If configured to use separate files, save each object individually
        if (Boolean.TRUE.equals(TYPE_TO_SEPARATE_FILES.get(type))) {
            String dir = getDirectoryForType(type);
            File dirFile = new File(dir);
            if (!dirFile.exists()) {
                dirFile.mkdirs();
            }

            for (T object : collection) {
                saveIndividualObject(object);
            }
        }
        // Otherwise save as a collection file
        else {
            String dir = getDirectoryForType(type);
            String fileName = getFileNameForType(type);

            try {
                File dirFile = new File(dir);
                if (!dirFile.exists()) {
                    dirFile.mkdirs();
                }

                try (Writer writer = new FileWriter(dir + fileName)) {
                    gson.toJson(collection, writer);
                }
            } catch (IOException e) {
                System.err.println("Error saving collection " + type.getSimpleName() +
                        ": " + e.getMessage());
            }
        }
    }

    /**
     * Адаптер за сериализация и десериализация на LocalDateTime
     * в JSON формат, използвани от Gson.
     */
    private static class LocalDateTimeAdapter implements com.google.gson.JsonSerializer<LocalDateTime>,
            com.google.gson.JsonDeserializer<LocalDateTime> {
        @Override
        public com.google.gson.JsonElement serialize(LocalDateTime src, java.lang.reflect.Type typeOfSrc,
                                                     com.google.gson.JsonSerializationContext context) {
            return new com.google.gson.JsonPrimitive(src.toString());
        }

        @Override
        public LocalDateTime deserialize(com.google.gson.JsonElement json, java.lang.reflect.Type typeOfT,
                                         com.google.gson.JsonDeserializationContext context) throws com.google.gson.JsonParseException {
            return LocalDateTime.parse(json.getAsString());
        }
    }

    /**
     * Адаптер за сериализация и десериализация на LocalDate
     * в JSON формат, използвани от Gson.
     */
    private static class LocalDateAdapter implements com.google.gson.JsonSerializer<LocalDate>,
            com.google.gson.JsonDeserializer<LocalDate> {
        @Override
        public com.google.gson.JsonElement serialize(LocalDate src, java.lang.reflect.Type typeOfSrc,
                                                     com.google.gson.JsonSerializationContext context) {
            return new com.google.gson.JsonPrimitive(src.toString());
        }

        @Override
        public LocalDate deserialize(com.google.gson.JsonElement json, java.lang.reflect.Type typeOfT,
                                     com.google.gson.JsonDeserializationContext context) throws com.google.gson.JsonParseException {
            return LocalDate.parse(json.getAsString());
        }
    }

    /**
     * Функционален интерфейс за съвпадение на обекти.
     * Използва се за филтриране на обекти в колекции.
     *
     * @param <T> Типът на обекта, който се проверява
     */
    @FunctionalInterface
    public interface MatcherFunction<T> {
        boolean matches(T object);
    }
}
