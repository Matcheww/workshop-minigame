package com.matcheww.workshop.model;

/**
 * A single inventory slot. Holds zero or one {@link ItemStack}.
 * Slots are owned by exactly one {@link Container} (Hotbar, CraftingTable,
 * or Furnace); the container is responsible for creating and holding them.
 */
public class Slot {
    private ItemStack itemStack;
    private final int slotIndex;
 
    // constructor for containers empty by default
    public Slot(int slotIndex) {
        this.slotIndex = slotIndex;
        this.itemStack = null;
    }
 
    public int getSlotIndex() {
        return slotIndex;
    }
 
    public boolean isEmpty() {
        return itemStack == null;
    }
 
    public boolean isFull() {
        return !isEmpty() && itemStack.isFull();
    }
 
    public ItemStack getItemStack() {
        return itemStack;
    }
 
    /** Directly sets the stack held by this slot (used by containers when moving stacks). */
    public void setItemStack(ItemStack itemStack) {
        this.itemStack = itemStack;
    }
 
    /**
     * Attempts to add {@code quantityToAdd} of {@code item} into this slot,
     * merging with the existing stack if the item type matches.
     * @return true if the entire quantity was placed
     */
    public boolean addItem(Item item, int quantityToAdd) {
        if (item == null || quantityToAdd <= 0) {
            return false;
        }
 
        if (isEmpty()) {
            int amountToPlace = Math.min(quantityToAdd, item.getMaxStackSize());
            itemStack = new ItemStack(item, amountToPlace);
            return quantityToAdd <= item.getMaxStackSize();
        }
 
        if (!itemStack.getItem().equals(item)) {
            return false;
        }
 
        int leftover = itemStack.addQuantity(quantityToAdd);
        return leftover == 0;
    }
 
    /**
     * Removes up to {@code quantityToRemove} from this slot's stack.
     * @return the removed stack, or null if the slot was empty
     */
    public ItemStack removeItem(int quantityToRemove) {
        if (isEmpty() || quantityToRemove <= 0) {
            return null;
        }
 
        int availableToRemove = itemStack.getQuantity();
        
        if (quantityToRemove >= availableToRemove) {
            ItemStack removed = itemStack;
            itemStack = null;
            return removed;
        }

        return itemStack.split(quantityToRemove);
    }
 
    public void clear() {
        itemStack = null;
    }
}

