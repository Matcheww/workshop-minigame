package com.matcheww.workshop.controller;

import com.matcheww.workshop.model.CraftingRecipe;
import com.matcheww.workshop.model.CraftingTable;
import com.matcheww.workshop.model.ItemStack;
import com.matcheww.workshop.model.RecipeBank;
import com.matcheww.workshop.model.Slot;
import com.matcheww.workshop.view.CraftingTableView;

/**
 * Controller layer. After any change to the crafting grid, checks
 * RecipeBank for a match and keeps CraftingTable's active recipe and
 * output-slot preview in sync with it.
 *
 * Belongs in the Controller layer: it decides what "the grid currently
 * matches recipe X" means and pushes that decision into both the Model
 * (setActiveRecipe) and the View (the output SlotView's preview).
 *
 * Does NOT belong in the Model layer: RecipeBank/CraftingTable already
 * expose everything needed (getPattern, findMatchingCraftingRecipe,
 * setActiveRecipe) - this class only calls them in the right order, it
 * adds no new game rules. Does NOT belong in the View layer: it never
 * constructs a JavaFX node itself, only calls refresh()/refreshAll() on
 * views that already exist.
 */
public class CraftingController {

    private final CraftingTable craftingTable;
    private final RecipeBank recipeBank;
    private final CraftingTableView craftingTableView;

    public CraftingController(CraftingTable craftingTable, RecipeBank recipeBank, CraftingTableView craftingTableView) {
        this.craftingTable = craftingTable;
        this.recipeBank = recipeBank;
        this.craftingTableView = craftingTableView;
    }

    /**
     * Re-checks the grid against every known recipe and updates the output
     * preview accordingly. Called after any drag that could have touched
     * the grid, and again after a craft is collected (the grid may still
     * match the same recipe if it held enough ingredients for more than
     * one craft).
     */
    public void onGridChanged() {
        CraftingRecipe match = recipeBank.findMatchingCraftingRecipe(craftingTable.getPattern());
        craftingTable.setActiveRecipe(match);

        Slot outputSlot = craftingTable.getOutputSlot();
        if (match != null) {
            ItemStack recipeOutput = match.getOutput();
            // A fresh copy - never store the recipe's own output ItemStack
            // reference directly, or collecting/merging it later would
            // mutate the recipe's permanent template.
            outputSlot.setItemStack(new ItemStack(recipeOutput.getItem(), recipeOutput.getQuantity()));
        } else {
            outputSlot.clear();
        }

        craftingTableView.getGridView().refreshAll();
        craftingTableView.getOutputSlotView().refresh();
    }
}