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
            // За да работят касовите бележки с Map<Product, Integer>
            .enableComplexMapKeySerialization()
            .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
            .registerTypeAdapter(LocalDate.class, new LocalDateAdapter())
            .create();

    /**
     * Регистрира даден тип обект с персонализирано име на файл.
     * Ако не регистрирате типа, ще се използва автоматично генерирано име.
     */
    public static <T> void registerType(Class<T> type, String fileName) {
        TYPE_TO_FILENAME.put(type, fileName);
    }

    /**
     * Регистрира даден тип обект с персонализирана директория за съхранение и с възможност за използване на отделни файлове за всеки обект.
     */
    public static <T> void registerTypeWithCustomDir(Class<T> type, String customDir, boolean separateFiles) {
        TYPE_TO_CUSTOM_DIR.put(type, customDir);
        TYPE_TO_SEPARATE_FILES.put(type, separateFiles);
    }


    /**
     * Зарежда колекция от обекти от даден тип, ако не е заредена в кеша, я зарежда от файл.
     *
     * @param type Типът на обектите, които трябва да бъдат заредени
     * @return Колекция от обекти от дадения тип
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
     * Методът работи с обекти, които имат поле "id" от тип int или long.
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
                        throw new IllegalArgumentException("Обект с ID " + objectId + " вече съществува в колекцията.");
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
                    throw new RuntimeException("Не може да се достъпи полето 'id' на обекта", e);
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

            if (Boolean.TRUE.equals(TYPE_TO_SEPARATE_FILES.get(type))) {
                saveIndividualObjectAsSeparateFile(object);
            }

        } catch (Exception е) {
            throw new RuntimeException("Неуспешно записване на обект от тип " + type.getSimpleName(), е);
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

                if (Boolean.TRUE.equals(TYPE_TO_SEPARATE_FILES.get(type))) {
                    saveIndividualObjectAsSeparateFile(object);
                }

                return true;
            }
        }
        return false;
    }


    /**
     * Търси обект в колекцията
     *
     * @param type    Типът на колекцията
     * @param id     Идентификатор на обекта, който трябва да бъде намерен - може да бъде Integer или Long
     */
    public static <T> Optional<T> findObjectById(Class<T> type, Object id) {
        List<T> collection = getCollection(type);
        for (T object : collection) {
            try {
                Field idField = type.getDeclaredField("id");
                idField.setAccessible(true);
                Object objectId = idField.get(object);
                if (objectId != null && objectId.equals(id)) {
                    return Optional.of(object);
                }
            } catch (NoSuchFieldException | IllegalAccessException e) {
                System.err.println("Error accessing 'id' field in " + type.getSimpleName() + ": " + e.getMessage());
            }
        }
        return Optional.empty();
    }

    /**
     * Връща името на файла за даден тип oбект, ако е регистрирано такова.
     * Ако не е регистрирано, ще се използва автоматично генерирано име на файла
     *
     * @param type Типът на обекта, за който се търси име на файл
     * @return Името на файла за дадения тип обект
     */
    private static <T> String getFileNameForType(Class<T> type) {
        String fileName = TYPE_TO_FILENAME.get(type);
        if (fileName != null) {
            return fileName;
        }
        return type.getSimpleName() + FILE_EXTENSION;
    }

    /**
     * Връща директорията за съхранение на обекти от даден тип, ако е регистрирана персонализирана директория.
     *
     * @param type Типът на обекта, за който се търси директория
     * @return Директорията за съхранение на обекти от дадения тип
     */
    private static <T> String getDirectoryForType(Class<T> type) {
        String customDir = TYPE_TO_CUSTOM_DIR.get(type);
        if (customDir != null) {
            return customDir.endsWith("/") ? customDir : customDir + "/";
        }
        return DATA_DIR;
    }

    /**
     * Запазва индивидуален обект в отделен файл, като използва името на класа и ID-то на обекта за име на файла.
     *
     * @param object Обектът, който трябва да бъде запазен
     * @param <T> Типът на обекта
     */
    private static <T> void saveIndividualObjectAsSeparateFile(T object) {
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
     * Зарежда колекция от обекти от файл или от отделни файлове.
     * Ако колекцията вече е заредена в кеша, няма да се зарежда отново.
     *
     * @param type Типът на обектите, които трябва да бъдат заредени
     */
    @SuppressWarnings("unchecked")
    private static <T> void loadCollection(Class<T> type) {
        String dir = getDirectoryForType(type);
        String fileName = getFileNameForType(type);
        File file = new File(dir + fileName);
        List<T> collection = new ArrayList<>();

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
                            throw new RuntimeException("Грешка при четене на файл " + individualFile.getName() +
                                    ": " + e.getMessage(), e);
                        }
                    }
                }
            }
        }
        else if (file.exists()) {
            try (Reader reader = new FileReader(file)) {
                Type listType = TypeToken.getParameterized(ArrayList.class, type).getType();
                collection = gson.fromJson(reader, listType);
                if (collection == null) {
                    collection = new ArrayList<>();
                }
            } catch (IOException e) {
                throw new RuntimeException("Грешка при четене на файл " + fileName +
                        ": " + e.getMessage(), e);
            }
        }

        CACHED_COLLECTIONS.put(type, collection);
    }

    /**
     * Запазва колекцията от обекти в съответния файл или файлове.
     * Ако е конфигурирано да се използват отделни файлове за всеки обект, ще ги запише поотделно.
     * В противен случай ще запише цялата колекция в един файл.
     *
     * @param type Типът на обектите, които трябва да бъдат запазени
     * @param <T>  Типът на обекта
     */
    private static <T> void saveCollection(Class<T> type) {
        List<T> collection = (List<T>) CACHED_COLLECTIONS.get(type);
        if (collection == null) {
            return;
        }

        if (Boolean.TRUE.equals(TYPE_TO_SEPARATE_FILES.get(type))) {
            String dir = getDirectoryForType(type);
            File dirFile = new File(dir);
            if (!dirFile.exists()) {
                dirFile.mkdirs();
            }

            for (T object : collection) {
                saveIndividualObjectAsSeparateFile(object);
            }
        }
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
     * Връща пълния път до файла за даден обект, като използва типа и ID-то на обекта.
     *
     * @param object Обектът, за който се търси пътя до файла
     * @return Пълният път до файла, където е съхранен обектът
     */
    public static <T> String getFilePathForObject(T object) {
        Class<?> type = object.getClass();
        String dir = getDirectoryForType(type);

        try {
            Field idField = type.getDeclaredField("id");
            idField.setAccessible(true);
            Object id = idField.get(object);
            if (id == null) {
                throw new IllegalArgumentException("Обектът няма зададен ID.");
            }

            String fileName = type.getSimpleName() + "_" + id + FILE_EXTENSION;
            return dir + fileName;
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException("Грешка при получаване на пътя до файла за обект от тип " + type.getSimpleName(), e);
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
