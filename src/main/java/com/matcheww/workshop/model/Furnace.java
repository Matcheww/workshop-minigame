package main.java.com.matcheww.workshop.model;

/**
 * A furnace with 3 fixed slots: input, fuel, output. {@code tick()} is
 * designed to be driven by a background thread/scheduler (the
 * concurrency piece of this project) so smelting progresses in real
 * time without the model knowing anything about threads itself.
 * Mutating methods are synchronized since the tick thread and the
 * UI/controller thread will both touch this state.
 */
public class Furnace extends Container {
    public static final int SIZE = 3;
    private static final int INPUT_INDEX = 0;
    private static final int FUEL_INDEX = 1;
    private static final int OUTPUT_INDEX = 2;
    private static final int DEFAULT_FUEL_TICKS = 200;

    private SmeltingRecipe activeRecipe;
    private int smeltProgress;
    private int fuelRemaining;
    private volatile boolean isSmelting;

    public Furnace() {
        super(SIZE);
    }

    public Slot getInputSlot() {
        return getSlot(INPUT_INDEX);
    }

    public Slot getFuelSlot() {
        return getSlot(FUEL_INDEX);
    }

    public Slot getOutputSlot() {
        return getSlot(OUTPUT_INDEX);
    }

    public void setActiveRecipe(SmeltingRecipe recipe) {
        this.activeRecipe = recipe;
    }

    public boolean canSmelt() {
        Slot input = getInputSlot();
        Slot fuel = getFuelSlot();
        if (input.isEmpty() || fuel.isEmpty() || activeRecipe == null) {
            return false;
        }
        return activeRecipe.getInputItem().equals(input.getItemStack().getItem());
    }

    public synchronized void startSmelting() {
        if (isSmelting || !canSmelt()) {
            return;
        }
        isSmelting = true;
        smeltProgress = 0;
        fuelRemaining = DEFAULT_FUEL_TICKS;
        getFuelSlot().removeItem(1);
    }

    /** Advances the smelt by one tick; intended to be called periodically by a scheduler thread. */
    public synchronized void tick() {
        if (!isSmelting) {
            return;
        }

        if (fuelRemaining <= 0) {
            isSmelting = false;
            return;
        }

        fuelRemaining--;
        smeltProgress++;

        if (activeRecipe != null && smeltProgress >= activeRecipe.getSmeltTime()) {
            completeSmelting();
        }
    }

    private void completeSmelting() {
        getInputSlot().removeItem(1);
        ItemStack output = activeRecipe.getOutput();
        getOutputSlot().addItem(output.getItem(), output.getQuantity());
        smeltProgress = 0;
        isSmelting = canSmelt();
        if (isSmelting) {
            fuelRemaining = DEFAULT_FUEL_TICKS;
            getFuelSlot().removeItem(1);
        }
    }

    public int getSmeltProgress() {
        return smeltProgress;
    }

    public int getFuelRemaining() {
        return fuelRemaining;
    }

    public boolean isSmelting() {
        return isSmelting;
    }
}
