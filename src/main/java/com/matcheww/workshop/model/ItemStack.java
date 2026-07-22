package main.java.com.matcheww.workshop.model;

/**
 * Couples an {@link Item} with a quantity. Kept separate from {@link Slot}
 * so that "what item" and "how many" can be manipulated (merged, split,
 * moved) independently of any particular inventory slot.
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
 
    public boolean isEmpty() {
        return quantity <= 0;
    }
 
    public boolean isFull() {
        return quantity >= item.getMaxStackSize();
    }
 
    public int getRemainingCapacity() {
        return item.getMaxStackSize() - quantity;
    }
 
    public boolean canMergeWith(ItemStack other) {
        return other != null && other.item.equals(this.item) && !isFull();
    }
 
    /**
     * Adds up to {@code amount} to this stack, capped by the item's max stack size.
     * @return the leftover amount that did NOT fit (0 if everything fit)
     */
    public int addQuantity(int amount) {
        if (amount < 0) {
            throw new IllegalArgumentException("amount cannot be negative");
        }
        int spaceLeft = getRemainingCapacity();
        int added = Math.min(amount, spaceLeft);
        quantity += added;
        return amount - added;
    }
 
    /** @return true if the removal succeeded (enough quantity was available) */
    public boolean removeQuantity(int amount) {
        if (amount < 0) {
            throw new IllegalArgumentException("amount cannot be negative");
        }
        if (amount > quantity) {
            return false;
        }
        quantity -= amount;
        return true;
    }
 
    /** Splits off a new stack of {@code amount}, shrinking this stack in place. */
    public ItemStack split(int amount) {
        if (amount <= 0 || amount >= quantity) {
            throw new IllegalArgumentException("invalid split amount: " + amount);
        }
        quantity -= amount;
        return new ItemStack(item, amount);
    }
 
    @Override
    public String toString() {
        return item.getName() + " x" + quantity;
    }
}
