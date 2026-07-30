package com.matcheww.workshop;

/**
 * Plain entry point - not part of the Model, View, or Controller layers.
 * Its only job is to hand off to GameApplication.launch(...). Keeping this
 * separate from the actual javafx.application.Application subclass avoids
 * "JavaFX runtime components are missing" errors that some launch paths
 * (double-clicking a packaged jar, running via `java -jar`) throw when the
 * class on the command line extends Application directly.
 */
public class Main {

    public static void main(String[] args) {
        GameApplication.launch(GameApplication.class, args);
    }
}