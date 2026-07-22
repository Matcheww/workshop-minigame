package main.java.com.matcheww.workshop.model;

/**
 * Abstract base class for every item type in the game.
 * Holds only the properties common to ALL items so that new item
 * subclasses stay lightweight and easy to add.
 */
public abstract class Item {
    private final String itemId;
    private final String name;
    private final int maxStackSize;
 
    protected Item(String itemId, String name, int maxStackSize) {
        if (itemId == null || itemId.isBlank()) {
            throw new IllegalArgumentException("itemId cannot be blank");
        }
        if (maxStackSize < 1) {
            throw new IllegalArgumentException("maxStackSize must be at least 1");
        }
        this.itemId = itemId;
        this.name = name;
        this.maxStackSize = maxStackSize;
    }
 
    /** Convenience constructor for the common case of a standard 64-stack item. */
    protected Item(String itemId, String name) {
        this(itemId, name, 64);
    }
 
    public String getItemId() {
        return itemId;
    }
 
    public String getName() {
        return name;
    }
 
    public int getMaxStackSize() {
        return maxStackSize;
    }
 
    /** Short flavor/tooltip text; every concrete item must define its own. */
    public abstract String getDescription();
 
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Item)) return false;
        
        return itemId.equals(((Item) o).itemId);
    }
 
    @Override
    public int hashCode() {
        return itemId.hashCode();
    }
 
    @Override
    public String toString() {
        return name;
    }
}
