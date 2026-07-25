package main.java.com.matcheww.workshop.model;
import java.util.Arrays;

public class CraftingRecipe extends Recipe {
    private final Item[][] pattern;

    public CraftingRecipe(Item[][] pattern, ItemStack output) {
        super(output);
        this.pattern = pattern;
    }

    @Override
    public boolean matches(Item[][] input) {
        if (input == null || input.length != pattern.length) {
            return false;
        }

        for (int row = 0; row < pattern.length; row++) {
            if (input[row].length != pattern[row].length) {
                return false;
            }

            for (int col = 0; col < pattern[row].length; col++) {
                Item expected = pattern[row][col];
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
        return "CraftingRecipe{pattern=" + Arrays.deepToString(pattern) + ", output=" + output + "}";
    }
}
