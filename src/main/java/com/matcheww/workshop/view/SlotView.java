package com.matcheww.workshop.view;

import com.matcheww.workshop.model.Item;
import com.matcheww.workshop.model.ItemStack;
import com.matcheww.workshop.model.Slot;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;

/**
 * View layer. The one visual primitive every container view is built from:
 * renders a single Slot's contents (icon + quantity, or nothing if empty).
 *
 * Belongs in the View layer: it only reads its bound Slot to render, via
 * read-only methods (isEmpty()/getItemStack()) - it never calls addItem(),
 * removeItem(), or clear(). Mouse events are attached to this node using
 * plain JavaFX Node methods, but the handlers themselves are supplied by
 * DragAndDropController when it registers this view - the logic behind a
 * press/drag/release lives entirely in the Controller, not here.
 *
 * Does NOT belong in the Controller layer: refresh() only re-reads and
 * re-draws; it never decides whether a move is valid.
 */
public class SlotView extends StackPane {

    private static final double SIZE = 40;

    private final Slot slot;
    private final Region iconRegion;
    private final Label iconLabel;
    private final Label quantityLabel;

    public SlotView(Slot slot) {
        this.slot = slot;
        getStyleClass().add("slot");
        setPrefSize(SIZE, SIZE);
        setMinSize(SIZE, SIZE);
        setMaxSize(SIZE, SIZE);

        iconRegion = new Region();
        iconRegion.getStyleClass().add("slot-icon");
        iconRegion.setPrefSize(SIZE - 10, SIZE - 10);
        iconRegion.setMaxSize(SIZE - 10, SIZE - 10);

        iconLabel = new Label();
        iconLabel.getStyleClass().add("slot-icon-label");

        quantityLabel = new Label();
        quantityLabel.getStyleClass().add("slot-quantity-label");
        StackPane.setAlignment(quantityLabel, Pos.BOTTOM_RIGHT);

        getChildren().addAll(iconRegion, iconLabel, quantityLabel);
        refresh();
    }

    public Slot getSlot() {
        return slot;
    }

    /** Re-reads the bound Slot and redraws. Called by a Controller after any Model mutation. */
    public void refresh() {
        if (slot.isEmpty()) {
            iconRegion.setVisible(false);
            iconLabel.setText("");
            quantityLabel.setText("");
            return;
        }

        ItemStack itemStack = slot.getItemStack();
        Item item = itemStack.getItem();

        iconRegion.setVisible(true);
        iconRegion.setStyle("-fx-background-color: " + ItemIconFactory.colorHexFor(item) + ";");
        iconLabel.setText(ItemIconFactory.abbreviationFor(item));

        int quantity = itemStack.getQuantity();
        quantityLabel.setText(quantity > 1 ? String.valueOf(quantity) : "");
    }
}