package main.java.com.matcheww.workshop.model;

public class CoalOre extends Item {

    public CoalOre() {
        super("coal_ore", "Coal Ore");
    }

    @Override
    public String getDescription() {
        return "Ore containing coal. Can be used as furnace fuel.";
    }
}
