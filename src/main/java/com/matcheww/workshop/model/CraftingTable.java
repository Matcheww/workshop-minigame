package main.java.com.matcheww.workshop.model;

/**
 * The 3x3 crafting grid plus its output slot. Delegates "what does
 * this pattern produce" to a {@link CraftingRecipe} rather than
 * hard-coding recipe logic here.
 */
public class CraftingTable extends Container {
    public static final int ROWS = 3;
    public static final int COLS = 3;

    private final Slot outputSlot;
    private CraftingRecipe activeRecipe;

    public CraftingTable() {
        super(ROWS * COLS);
        this.outputSlot = new Slot(-1);
    }

    public Slot getOutputSlot() {
        return outputSlot;
    }

    /** Snapshots the current grid contents as a 3x3 item pattern (nulls for empty slots). */
    public Item[][] getPattern() {
        Item[][] pattern = new Item[ROWS][COLS];
        for (int row = 0; row < ROWS; row++) {
            for (int col = 0; col < COLS; col++) {
                Slot slot = slots.get(row * COLS + col);
                pattern[row][col] = slot.isEmpty() ? null : slot.getItemStack().getItem();
            }
        }
        return pattern;
    }

    public boolean matchesRecipe(CraftingRecipe recipe) {
        return recipe != null && recipe.matches(getPattern());
    }

    /** Called by the controller once a matching recipe has been set. */
    public void setActiveRecipe(CraftingRecipe recipe) {
        this.activeRecipe = recipe;
    }

    /**
     * Consumes one item from every occupied grid slot and returns the
     * recipe's output. Returns null if no recipe currently matches.
     */
    public ItemStack craft() {
        if (activeRecipe == null || !matchesRecipe(activeRecipe)) {
            return null;
        }
        for (Slot slot : slots) {
            slot.removeItem(1);
        }
        return activeRecipe.getOutput();
    }

    public void clearGrid() {
        for (Slot slot : slots) {
            slot.clear();
        }
        outputSlot.clear();
        activeRecipe = null;
    }
}
