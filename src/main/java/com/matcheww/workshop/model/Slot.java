package main.java.com.matcheww.workshop.model;

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

    // constructor for containers with randomized items for the player to choose from
    public Slot(int slotIndex, ItemStack itemStack) {
        this.slotIndex = slotIndex;
        this.itemStack = itemStack;
    }
 
    public int getSlotIndex() {
        return slotIndex;
    }
 
    public boolean isEmpty() {
        return itemStack == null || itemStack.isEmpty();
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
     * Attempts to add {@code quantity} of {@code item} into this slot,
     * merging with the existing stack if the item type matches.
     * @return true if the entire quantity was placed
     */
    public boolean addItem(Item item, int quantity) {
        if (item == null || quantity <= 0) {
            return false;
        }
 
        if (isEmpty()) {
            int amountToPlace = Math.min(quantity, item.getMaxStackSize());
            itemStack = new ItemStack(item, amountToPlace);
            return quantity <= item.getMaxStackSize();
        }
 
        if (!itemStack.getItem().equals(item)) {
            return false;
        }
 
        int leftover = itemStack.addQuantity(quantity);
        return leftover == 0;
    }
 
    /**
     * Removes up to {@code quantity} from this slot's stack.
     * @return the removed stack, or null if the slot was empty
     */
    public ItemStack removeItem(int quantity) {
        if (isEmpty() || quantity <= 0) {
            return null;
        }
 
        int available = itemStack.getQuantity();
        if (quantity >= available) {
            ItemStack removed = itemStack;
            itemStack = null;
            return removed;
        }
        return itemStack.split(quantity);
    }
 
    public void clear() {
        itemStack = null;
    }
}

