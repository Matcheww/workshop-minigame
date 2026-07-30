package com.matcheww.workshop.model.minecraftitems;

import com.matcheww.workshop.model.Item;

public class IronIngot extends Item {
    
    public IronIngot() {
        super("iron_ingot", "Iron Ingot");
    }

    @Override
    public String getDescription() {
        return "Make iron tools.";
    }
}
