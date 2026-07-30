package com.matcheww.workshop.view;

import com.matcheww.workshop.model.Item;
import com.matcheww.workshop.model.ItemStack;
import javafx.geometry.Point2D;
import javafx.scene.control.Label;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;

/**
 * View layer. A purely cosmetic floating icon shown while an item is
 * mid-drag; reuses the same icon style as SlotView via ItemIconFactory.
 *
 * Belongs in the View layer: it renders one ItemStack, nothing more, and
 * holds no reference to any Slot or Container. Mouse-transparent so it
 * never interferes with DragAndDropController's own drop hit-testing.
 *
 * Does NOT belong in the Controller layer: it has no idea when a drag
 * starts, updates, or ends - DragAndDropController tells it exactly what
 * to show and where, via showFor()/moveTo()/hide().
 */
public class DragGhostView extends StackPane {

    private static final double SIZE = 32;

    private final Region iconRegion;
    private final Label iconLabel;

    public DragGhostView() {
        setMouseTransparent(true);
        setPickOnBounds(false);
        setVisible(false);
        getStyleClass().add("drag-ghost");

        iconRegion = new Region();
        iconRegion.getStyleClass().add("slot-icon");
        iconRegion.setPrefSize(SIZE, SIZE);
        iconRegion.setMaxSize(SIZE, SIZE);

        iconLabel = new Label();
        iconLabel.getStyleClass().add("slot-icon-label");

        getChildren().addAll(iconRegion, iconLabel);
    }

    public void showFor(ItemStack itemStack, double sceneX, double sceneY) {
        Item item = itemStack.getItem();
        iconRegion.setStyle("-fx-background-color: " + ItemIconFactory.colorHexFor(item) + ";");
        iconLabel.setText(ItemIconFactory.abbreviationFor(item));
        moveTo(sceneX, sceneY);
        setVisible(true);
    }

    public void moveTo(double sceneX, double sceneY) {
        Point2D local = getParent().sceneToLocal(sceneX, sceneY);
        setLayoutX(local.getX() - SIZE / 2);
        setLayoutY(local.getY() - SIZE / 2);
    }

    public void hide() {
        setVisible(false);
    }
}