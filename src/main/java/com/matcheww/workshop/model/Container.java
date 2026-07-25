package com.matcheww.workshop.model;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Abstract base class for anything made up of {@link Slot}s
 * (Hotbar, CraftingTable, Furnace). Owns its slots via composition -
 * a slot never outlives the container that created it.
 */
public abstract class Container {
    protected final List<Slot> slots;
    protected final int capacity;

    protected Container(int slotCount) {
        if (slotCount < 1) {
            throw new IllegalArgumentException("capacity must be at least 1");
        }

        this.capacity = slotCount;
        this.slots = new ArrayList<>(slotCount);

        for (int i = 0; i < capacity; i++) {
            slots.add(new Slot(i));
        }
    }

    public Slot getSlot(int index) {
        if (index < 0 || index >= slots.size()) {
            throw new IndexOutOfBoundsException("Invalid slot index: " + index);
        }

        return slots.get(index);
    }

    // return a copy only
    public List<Slot> getSlots() {
        return Collections.unmodifiableList(slots);
    }

    public int getCapacity() {
        return capacity;
    }

    public boolean isFull() {
        return slots.stream().allMatch(Slot::isFull);
    }
 
    public boolean isEmpty() {
        return slots.stream().allMatch(Slot::isEmpty);
    }
}
