package com.covera.config;

import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

public class DatabaseConfig {
    private static final String PERSISTENCE_UNIT_NAME = "ecommercePU";
    private static EntityManagerFactory factory;

    public static void connectDB() {
        factory = Persistence.createEntityManagerFactory(PERSISTENCE_UNIT_NAME);
        System.out.println("✅ Database connected successfully!");
    }

    public static EntityManagerFactory getFactory() {
        return factory;
    }
}
