package com.matcheww.workshop.model;

/**
 * Abstract base for anything that turns an input pattern into an
 * output {@link ItemStack}. Keeps "what produces what" logic out of
 * the container classes (CraftingTable / Furnace).
 */
public abstract class Recipe {
    protected final ItemStack output;

    protected Recipe(ItemStack output) {
        if (output == null) {
            throw new IllegalArgumentException("output cannot be null");
        }
        this.output = output;
    }

    public ItemStack getOutput() {
        return output;
    }

    /** @return true if the given input pattern satisfies this recipe */
    public abstract boolean matches(Item[][] input);
}
