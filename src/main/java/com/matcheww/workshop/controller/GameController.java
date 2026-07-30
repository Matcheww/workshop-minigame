package com.matcheww.workshop.controller;

import com.matcheww.workshop.model.CraftingRecipe;
import com.matcheww.workshop.model.CraftingTable;
import com.matcheww.workshop.model.Furnace;
import com.matcheww.workshop.model.Hotbar;
import com.matcheww.workshop.model.Inventory;
import com.matcheww.workshop.model.Item;
import com.matcheww.workshop.model.ItemContainer;
import com.matcheww.workshop.model.ItemStack;
import com.matcheww.workshop.model.RecipeBank;
import com.matcheww.workshop.model.SmeltingRecipe;
import com.matcheww.workshop.model.Slot;
import com.matcheww.workshop.model.minecraftitems.Coal;
import com.matcheww.workshop.model.minecraftitems.IronIngot;
import com.matcheww.workshop.model.minecraftitems.IronOre;
import com.matcheww.workshop.model.minecraftitems.OakLog;
import com.matcheww.workshop.model.minecraftitems.Stick;
import com.matcheww.workshop.model.minecraftitems.WoodenPlanks;
import com.matcheww.workshop.model.minecraftitems.IronHelmet;
import com.matcheww.workshop.model.minecraftitems.IronChestplate;
import com.matcheww.workshop.model.minecraftitems.IronLeggings;
import com.matcheww.workshop.model.minecraftitems.IronBoots;

import java.util.List;
import java.util.Random;
import java.util.function.Supplier;

/**
 * Controller layer. This is the single composition point for every Model
 * instance the game needs. Later, more focused controllers (drag-and-drop,
 * crafting, furnace) will read from the instances exposed here rather than
 * constructing their own.
 *
 * Belongs in the Controller layer, not the Model layer: it contains no
 * business rules of its own - it only holds references and, starting in
 * later steps, delegates every mutation to the Model classes themselves.
 *
 * Does NOT belong in the View layer: it never imports javafx.scene.* and
 * never renders anything. Views read from the Model objects exposed here,
 * but this class does not decide how they are drawn.
 */
public class GameController {

    /**
     * Every currently known Item subclass, as factories rather than shared
     * instances - each slot gets its own fresh Item, consistent with how
     * the rest of the app already constructs items. Adding a new Item
     * subclass later only means adding one line here, not touching the
     * random-fill logic itself.
     */
    private static final List<Supplier<Item>> ITEM_FACTORIES = List.of(
            OakLog::new,
            WoodenPlanks::new,
            Stick::new,
            Coal::new,
            IronOre::new,
            IronIngot::new,
            IronHelmet::new,
            IronChestplate::new,
            IronLeggings::new,
            IronBoots::new
    );

    private static final int MAX_RANDOM_QUANTITY = 20;

    private final Hotbar hotbar;
    private final Inventory inventory;
    private final ItemContainer itemContainer;
    private final CraftingTable craftingTable;
    private final Furnace furnace;
    private final RecipeBank recipeBank;
    private final DragAndDropController dragAndDropController;
    private final Random random = new Random();

    public GameController() {
        this.hotbar = new Hotbar();
        this.inventory = new Inventory();
        this.itemContainer = new ItemContainer();
        this.craftingTable = new CraftingTable();
        this.furnace = new Furnace();
        this.recipeBank = new RecipeBank();
        this.dragAndDropController = new DragAndDropController();
        populateItemContainer();
        seedRecipes();
    }

    /**
     * Fills every slot in the item container with a random item type and a
     * random quantity - the pool of items the player can drag into their
     * inventory, craft, or smelt. This is initial game setup (deciding what
     * the item pool starts with), not a persistent game rule, so it lives
     * here rather than in the Model.
     */
    private void populateItemContainer() {
        for (Slot slot : itemContainer.getSlots()) {
            Item item = ITEM_FACTORIES.get(random.nextInt(ITEM_FACTORIES.size())).get();
            int quantity = 1 + random.nextInt(Math.min(MAX_RANDOM_QUANTITY, item.getMaxStackSize()));
            slot.addItem(item, quantity);
        }
    }

    /**
     * DEMO DATA ONLY. Proves the crafting/smelting pipeline end-to-end
     * using only the Item subclasses that already exist in the Model.
     */
    private void seedRecipes() {
        Item[][] stickPattern = {
                {new OakLog(), null, null},
                {null, null, null},
                {null, null, null}
        };
        
        recipeBank.registerCraftingRecipe(new CraftingRecipe(stickPattern, new ItemStack(new WoodenPlanks(), 4)));
        recipeBank.registerSmeltingRecipe(new SmeltingRecipe(new IronOre(), new ItemStack(new IronIngot(), 1), 10));
    }

    public Hotbar getHotbar() {
        return hotbar;
    }

    public Inventory getInventory() {
        return inventory;
    }

    public ItemContainer getItemContainer() {
        return itemContainer;
    }

    public CraftingTable getCraftingTable() {
        return craftingTable;
    }

    public Furnace getFurnace() {
        return furnace;
    }

    public RecipeBank getRecipeBank() {
        return recipeBank;
    }

    public DragAndDropController getDragAndDropController() {
        return dragAndDropController;
    }
}