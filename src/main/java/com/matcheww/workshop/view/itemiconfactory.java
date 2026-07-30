package com.matcheww.workshop.view;

import com.matcheww.workshop.model.Item;
import javafx.scene.paint.Color;

/**
 * View-layer helper that derives a placeholder visual (a color and a short
 * abbreviation) for any Item, using only the Item base class's own public
 * API - no instanceof chains against concrete subclasses like OakLog or
 * CoalOre, and no image assets required.
 *
 * Because it reads only getItemId()/getName(), it automatically supports
 * every future Item subclass with zero changes here, matching the Model's
 * original "easily extensible" design goal. Belongs in the View layer: it
 * only decides how something looks, never what it is.
 */
final class ItemIconFactory {

    private ItemIconFactory() {
    }

    static String colorHexFor(Item item) {
        int hash = Math.abs(item.getItemId().hashCode());
        double hue = hash % 360;
        Color color = Color.hsb(hue, 0.55, 0.80);
        return String.format("#%02X%02X%02X",
                Math.round(color.getRed() * 255),
                Math.round(color.getGreen() * 255),
                Math.round(color.getBlue() * 255));
    }

    static String abbreviationFor(Item item) {
        String[] words = item.getName().trim().split("\\s+");
        StringBuilder abbreviation = new StringBuilder();
        for (String word : words) {
            if (!word.isEmpty()) {
                abbreviation.append(Character.toUpperCase(word.charAt(0)));
            }
            if (abbreviation.length() == 2) {
                break;
            }
        }
        return abbreviation.toString();
    }
}