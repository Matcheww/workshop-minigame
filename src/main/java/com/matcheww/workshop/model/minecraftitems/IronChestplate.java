package com.matcheww.workshop.model.minecraftitems;

import com.matcheww.workshop.model.Item;

public class IronChestplate extends Item {

    public IronChestplate() {
        super("iron_chestplate", "Iron Chestplate");
    }

    @Override
    public String getDescription() {
        return "Protects the player's torso.";
    }
}