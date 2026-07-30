package com.matcheww.workshop.controller;

import com.matcheww.workshop.model.Furnace;
import com.matcheww.workshop.model.Item;
import com.matcheww.workshop.model.RecipeBank;
import com.matcheww.workshop.model.SmeltingRecipe;
import com.matcheww.workshop.view.FurnaceView;
import javafx.application.Platform;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Controller layer. After any change to the furnace's input slot, checks
 * RecipeBank for a smelting match and starts smelting when fuel is also
 * present. Also owns the background tick loop that drives smelting
 * forward over real time.
 *
 * Belongs in the Controller layer: it decides when smelting should start
 * and is the one place that marshals the tick thread's results onto the
 * JavaFX Application Thread.
 *
 * Does NOT belong in the Model layer: Furnace.tick() already contains all
 * the actual smelting rules and is already synchronized for exactly this
 * kind of cross-thread access - this class only decides when to call it.
 * Does NOT belong in the View layer: FurnaceView is only ever told plain
 * ints (updateProgress) or asked to refresh() - this class never touches
 * a JavaFX node directly.
 *
 * activeRecipe is cached here, not read from Furnace, because Furnace has
 * no public getter for it - this class is the one that decided the match
 * in the first place, so it already knows. It is volatile because it is
 * written on the JavaFX Application Thread (via onInputChanged, triggered
 * by a drag) and read on the background tick thread.
 */
public class FurnaceController {

    private static final long TICK_PERIOD_MILLIS = 1000;

    private final Furnace furnace;
    private final RecipeBank recipeBank;
    private final FurnaceView furnaceView;
    private final ScheduledExecutorService scheduler;

    private volatile SmeltingRecipe activeRecipe;

    public FurnaceController(Furnace furnace, RecipeBank recipeBank, FurnaceView furnaceView) {
        this.furnace = furnace;
        this.recipeBank = recipeBank;
        this.furnaceView = furnaceView;
        this.scheduler = Executors.newSingleThreadScheduledExecutor(FurnaceController::newDaemonThread);
        scheduler.scheduleAtFixedRate(this::tick, TICK_PERIOD_MILLIS, TICK_PERIOD_MILLIS, TimeUnit.MILLISECONDS);
    }

    /** Called after any drag that could have touched the input slot. */
    public void onInputChanged() {
        if (furnace.getInputSlot().isEmpty()) {
            activeRecipe = null;
        } else {
            Item inputItem = furnace.getInputSlot().getItemStack().getItem();
            activeRecipe = recipeBank.findMatchingSmeltingRecipe(inputItem);
        }
        furnace.setActiveRecipe(activeRecipe);

        if (furnace.canSmelt()) {
            furnace.startSmelting();
        }
    }

    /** Runs on the background scheduler thread - mutates the (already thread-safe) Model, then marshals the refresh back onto the JavaFX thread. */
    private void tick() {
        furnace.tick();

        SmeltingRecipe recipeAtTickTime = activeRecipe;
        int smeltTime = recipeAtTickTime != null ? recipeAtTickTime.getSmeltTime() : 1;

        Platform.runLater(() -> {
            furnaceView.updateProgress(furnace.getSmeltProgress(), smeltTime);
            furnaceView.refreshSlots();
        });
    }

    private static Thread newDaemonThread(Runnable runnable) {
        Thread thread = new Thread(runnable, "furnace-tick");
        thread.setDaemon(true);
        return thread;
    }

    /** Stops the background tick loop. Called from GameApplication.stop(). */
    public void shutdown() {
        scheduler.shutdownNow();
    }
}