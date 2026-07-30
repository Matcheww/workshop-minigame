package com.matcheww.workshop.model.minecraftitems;

import com.matcheww.workshop.model.Item;

public class IronBoots extends Item {

    public IronBoots() {
        super("iron_boots", "Iron Boots");
    }

    @Override
    public String getDescription() {
        return "Protects the player's feet.";
    }
}