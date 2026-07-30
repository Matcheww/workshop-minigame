package com.matcheww.workshop.view;

import com.matcheww.workshop.controller.GameController;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;

/**
 * View layer. Top-level layout shell for the whole window: item container
 * on top, crafting table + furnace in the center row, hotbar on the
 * bottom, an inventory panel that floats centered when opened, an
 * always-on-top inventory toggle button, and a transparent overlay layer
 * above everything used only to host the drag ghost while an item is
 * mid-drag.
 *
 * Belongs in the View layer: it only arranges JavaFX nodes. It holds a
 * reference to GameController purely so it can read the Model objects
 * needed to construct the real container views below - it never calls a
 * mutating Model method itself.
 *
 * Does NOT belong in the Controller layer: the inventory-toggle button
 * only flips this class's own visibility state (see onInventoryToggleRequested);
 * it never touches a Slot or Container.
 *
 * The toggle button is added as its own top-level StackPane child, above
 * the inventory panel, specifically so it stays clickable even while the
 * inventory panel is open and visually covering everything beneath it -
 * otherwise there would be no way to close the panel once it was opened.
 *
 * Exposes getCraftingTableView()/getFurnaceView() so GameApplication can
 * construct CraftingController/FurnaceController around them after this
 * view is built - this class does not construct those controllers itself,
 * since doing so is not a View responsibility.
 */
public class GameView extends StackPane {

    private final GameController gameController;
    private final InventoryView inventoryView;
    private final CraftingTableView craftingTableView;
    private final FurnaceView furnaceView;

    private boolean inventoryOpen = false;

    public GameView(GameController gameController) {
        this.gameController = gameController;
        getStyleClass().add("game-root");

        craftingTableView = new CraftingTableView(gameController.getCraftingTable(), gameController.getDragAndDropController());
        furnaceView = new FurnaceView(gameController.getFurnace(), gameController.getDragAndDropController());

        BorderPane content = new BorderPane();
        content.setTop(buildItemContainerRegion());
        content.setCenter(buildCraftingFurnaceRow());
        content.setBottom(buildHotbarRegion());

        inventoryView = new InventoryView(gameController.getInventory(), gameController.getDragAndDropController());
        StackPane.setAlignment(inventoryView, Pos.CENTER);

        Node toggleCorner = buildInventoryToggleCorner();
        StackPane.setAlignment(toggleCorner, Pos.TOP_RIGHT);

        Pane overlay = new Pane();
        overlay.setMouseTransparent(true);
        overlay.setPickOnBounds(false);
        gameController.getDragAndDropController().attachOverlay(overlay);

        getChildren().addAll(content, inventoryView, toggleCorner, overlay);
    }

    public CraftingTableView getCraftingTableView() {
        return craftingTableView;
    }

    public FurnaceView getFurnaceView() {
        return furnaceView;
    }

    private Node buildItemContainerRegion() {
        ItemContainerView itemContainerView =
                new ItemContainerView(gameController.getItemContainer(), gameController.getDragAndDropController());
        itemContainerView.setPadding(new Insets(12));
        BorderPane.setAlignment(itemContainerView, Pos.CENTER);
        return itemContainerView;
    }

    private Node buildCraftingFurnaceRow() {
        HBox row = new HBox(24, craftingTableView, furnaceView);
        row.getStyleClass().add("crafting-furnace-row");
        row.setAlignment(Pos.CENTER);
        row.setPadding(new Insets(12));
        return row;
    }

    private Node buildHotbarRegion() {
        HotbarView hotbarView = new HotbarView(gameController.getHotbar(), gameController.getDragAndDropController());
        hotbarView.setPadding(new Insets(12));
        BorderPane.setAlignment(hotbarView, Pos.CENTER);
        return hotbarView;
    }

    private Node buildInventoryToggleCorner() {
        Button toggleInventory = new Button("Inventory");
        toggleInventory.getStyleClass().add("inventory-toggle-button");
        toggleInventory.setOnAction(event -> onInventoryToggleRequested());

        StackPane corner = new StackPane(toggleInventory);
        corner.setPadding(new Insets(12));
        corner.setPickOnBounds(false);
        corner.setMaxSize(Region.USE_PREF_SIZE, Region.USE_PREF_SIZE);
        return corner;
    }

    /**
     * Toggles the inventory panel's visibility directly, rather than
     * forwarding to GameController. This is a deliberate MVC-boundary
     * judgment call: opening/closing the inventory never reads or writes
     * a Slot or Container, so there is no business logic or application
     * flow to hand off - routing a boolean flip through a Controller here
     * would be ceremony, not architecture.
     */
    private void onInventoryToggleRequested() {
        inventoryOpen = !inventoryOpen;
        if (inventoryOpen) {
            inventoryView.show();
        } else {
            inventoryView.hide();
        }
    }
}