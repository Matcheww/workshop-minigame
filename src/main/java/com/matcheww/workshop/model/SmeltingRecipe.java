package main.java.com.matcheww.workshop.model;

public class SmeltingRecipe extends Recipe {
    private final Item inputItem;
    private final int smeltTimeSeconds;

    public SmeltingRecipe(Item inputItem, ItemStack output, int smeltTimeSeconds) {
        super(output);
        if (inputItem == null) {
            throw new IllegalArgumentException("inputItem cannot be null");
        }
        if (smeltTimeSeconds < 1) {
            throw new IllegalArgumentException("smeltTimeSeconds must be at least 1");
        }
        this.inputItem = inputItem;
        this.smeltTimeSeconds = smeltTimeSeconds;
    }

    public Item getInputItem() {
        return inputItem;
    }

    public int getSmeltTime() {
        return smeltTimeSeconds;
    }

    /** A smelting "pattern" is just a single item in the furnace's input slot, at [0][0]. */
    @Override
    public boolean matches(Item[][] input) {
        if (input == null || input.length == 0 || input[0].length == 0) {
            return false;
        }

        Item candidate = input[0][0];
        
        return candidate != null && candidate.equals(inputItem);
    }
}
