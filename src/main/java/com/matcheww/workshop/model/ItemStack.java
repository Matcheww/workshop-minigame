package com.matcheww.workshop.model;

/**
 * Couples an {@link Item} with a quantity. Kept separate from {@link Slot}
 * so that "what item" and "how many" can be manipulated (merge, split, move)
 * independently of any particular inventory slot.
 */
public class ItemStack {
    private final Item item; // single reference to the item
    private int quantity;
 
    public ItemStack(Item item, int quantity) {
        if (item == null) {
            throw new IllegalArgumentException("item cannot be null");
        }
        if (quantity < 1 || quantity > item.getMaxStackSize()) {
            throw new IllegalArgumentException(
                    "quantity must be between 1 and " + item.getMaxStackSize() + " for " + item.getName());
        }
        this.item = item;
        this.quantity = quantity;
    }
 
    public Item getItem() {
        return item;
    }
 
    public int getQuantity() {
        return quantity;
    }
 
    public boolean isFull() {
        return quantity >= item.getMaxStackSize();
    }
 
    public int getRemainingCapacity() {
        return item.getMaxStackSize() - quantity;
    }
 
    public boolean canMergeWith(ItemStack otherItemStack) {
        return otherItemStack != null && otherItemStack.item.equals(this.item) && !isFull();
    }
 
    /**
     * Adds up to {@code quantityToAdd} to this stack, capped by the item's max stack size.
     * @return the leftover amount that did NOT fit (0 if everything fit)
     */
    public int addQuantity(int quantityToAdd) {
        if (quantityToAdd < 0) {
            throw new IllegalArgumentException("amount cannot be negative");
        }

        int spaceLeft = getRemainingCapacity();
        int added = Math.min(quantityToAdd, spaceLeft);
        quantity += added;

        return quantityToAdd - added;
    }
 
    /** @return true if the removal succeeded (enough quantity was available) */
    public boolean removeQuantity(int quantityToRemove) {
        if (quantityToRemove < 0) {
            throw new IllegalArgumentException("amount cannot be negative");
        }

        if (quantityToRemove >= quantity) {
            return false;
        }

        quantity -= quantityToRemove;

        return true;
    }
 
    /** Splits off a new stack of {@code quantityToSplit}, shrinking this stack in place. */
    public ItemStack split(int quantityToSplit) {
        if (quantityToSplit <= 0 || quantityToSplit >= quantity) {
            throw new IllegalArgumentException("invalid split amount: " + quantityToSplit);
        }

        quantity -= quantityToSplit;

        return new ItemStack(item, quantityToSplit);
    }
 
    @Override
    public String toString() {
        return item.getName() + " x" + quantity;
    }
}
