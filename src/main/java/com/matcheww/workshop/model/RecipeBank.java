package com.matcheww.workshop.model;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Owns every known recipe in the game and answers "does anything match
 * this input?" Exists so that CraftingTable and Furnace never need to
 * hold or loop over the full recipe list themselves - they just ask.
 */
public class RecipeBank {
    private final List<CraftingRecipe> craftingRecipes;
    private final List<SmeltingRecipe> smeltingRecipes;

    public RecipeBank() {
        craftingRecipes = new ArrayList<>();
        smeltingRecipes = new ArrayList<>();
    }

    public void registerCraftingRecipe(CraftingRecipe recipe) {
        if (recipe != null) {
            craftingRecipes.add(recipe);
        }
    }

    public void registerSmeltingRecipe(SmeltingRecipe recipe) {
        if (recipe != null) {
            smeltingRecipes.add(recipe);
        }
    }

    /** @return the first CraftingRecipe whose pattern matches, or null if none do */
    public CraftingRecipe findMatchingCraftingRecipe(Item[][] pattern) {
        for (CraftingRecipe recipe : craftingRecipes) {
            if (recipe.matches(pattern)) {
                return recipe;
            }
        }

        return null;
    }

    /** @return the first SmeltingRecipe that accepts this input item, or null if none do */
    public SmeltingRecipe findMatchingSmeltingRecipe(Item inputItem) {
        Item[][] singleCellInput = { { inputItem } };

        for (SmeltingRecipe recipe : smeltingRecipes) {
            if (recipe.matches(singleCellInput)) {
                return recipe;
            }
        }
        
        return null;
    }

    public List<CraftingRecipe> getCraftingRecipes() {
        return Collections.unmodifiableList(craftingRecipes);
    }

    public List<SmeltingRecipe> getSmeltingRecipes() {
        return Collections.unmodifiableList(smeltingRecipes);
    }
}