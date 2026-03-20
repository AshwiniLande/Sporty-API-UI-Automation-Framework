package utils;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;
import java.util.concurrent.locks.ReentrantReadWriteLock;


    public final class ConfigManager {

        private static final Properties prop = new Properties();

        /** Lock to ensure thread-safe read/write access */
        private static final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();

        /** Static initializer block - Executes only once when the class is loaded, not on every call */
        static {

            lock.writeLock().lock();

            try {
                   String env = System.getProperty("env", "qa");
                   String path = System.getProperty("user.dir") + "/src/test/resources/" + env + ".properties";

                   /** Auto resource management to prevents memory leaks */
                   try (FileInputStream stream = new FileInputStream(path)) {
                          prop.load(stream);
                   } catch (IOException e) {

                    /** Convert checked exception into runtime to avoid propagation and method signature pollution */
                    throw new RuntimeException("Failed to load configuration file: " + path, e);
                }

            } finally {
                lock.writeLock().unlock();
            }
        }

        private ConfigManager() {}

        /** Returns value from properties file for the given key */
        public static String get(String key) {

            lock.readLock().lock();

            try {
                return prop.getProperty(key);
            } finally {
                lock.readLock().unlock();
            }
        }
    }

