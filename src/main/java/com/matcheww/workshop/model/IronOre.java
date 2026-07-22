package main.java.com.matcheww.workshop.model;

public class IronOre extends Item {

    public IronOre() {
        super("iron_ore", "Iron Ore");
    }

    @Override
    public String getDescription() {
        return "Raw iron ore. Smelt it in a furnace to produce iron.";
    }
}
