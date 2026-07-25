package com.matcheww.workshop.model;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class CraftingRecipe extends Recipe {
    private final List<List<Item>> pattern;

    public CraftingRecipe(Item[][] pattern, ItemStack output) {
        super(output);
        this.pattern = deepCopy(pattern);
    }

    /** Copies the caller's array into an unmodifiable structure so external
     *  mutation of the original array can never change this recipe afterward. */
    private static List<List<Item>> deepCopy(Item[][] source) {
        List<List<Item>> copy = new ArrayList<>();

        for (Item[] row : source) {
            List<Item> rowCopy = new ArrayList<>();
            Collections.addAll(rowCopy, row);
            copy.add(Collections.unmodifiableList(rowCopy));
        }

        return Collections.unmodifiableList(copy); // also lock the outterpart of 2d arraylist
    }

    @Override
    public boolean matches(Item[][] input) {
        if (input == null || input.length != pattern.size()) {
            return false;
        }

        for (int row = 0; row < pattern.size(); row++) {
            List<Item> expectedRow = pattern.get(row);
            if (input[row].length != expectedRow.size()) {
                return false;
            }

            for (int col = 0; col < expectedRow.size(); col++) {
                Item expected = expectedRow.get(col);
                Item actual = input[row][col];
                
                /*
                answers the qs: "do these two slots' items not match?"
                - we only want to enter if items do not match
                Case 1: expected == null, actual == null -> NO, both slots match.
                Case 2: expected == null, actual != null -> YES, the recipe expects an empty slot.
                Case 3: expected != null, actual == expected -> NO, both slots contain the same item.
                Case 4: expected != null, actual == null or different item -> YES, the expected item is missing or incorrect.
                 */
                if (expected == null ? actual != null : !expected.equals(actual)) {
                    return false;
                }
            }
        }
        
        return true;
    }

    @Override
    public String toString() {
        return "CraftingRecipe{pattern=" + pattern + ", output=" + output + "}";
    }
}