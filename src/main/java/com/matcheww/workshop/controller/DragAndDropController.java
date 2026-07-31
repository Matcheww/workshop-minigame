package com.matcheww.workshop.controller;

import com.matcheww.workshop.model.Item;
import com.matcheww.workshop.model.ItemStack;
import com.matcheww.workshop.model.Slot;
import com.matcheww.workshop.view.DragGhostView;
import com.matcheww.workshop.view.SlotView;
import javafx.geometry.Bounds;
import javafx.scene.Scene;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

/**
 * Controller layer. Implements Minecraft Java Edition's click-based
 * inventory interaction model: a single left-click picks a stack up onto
 * the cursor (it keeps following the mouse even with no button held,
 * until the next click places it), a press-drag-release across several
 * slots evenly distributes the held stack among them, and a right-click
 * drops exactly one item at a time.
 *
 * Belongs in the Controller layer: it decides what counts as a valid
 * placement, invokes the mutating Slot methods that actually move
 * ItemStacks, and updates the affected SlotViews afterward.
 *
 * Does NOT belong in the Model layer: it contains no rules about what a
 * Slot or Item fundamentally is, only how a player's clicks translate
 * into calls on the existing Slot API. Does NOT belong in the View
 * layer: it never constructs a JavaFX Region/Label or sets a style
 * itself - all rendering decisions stay inside SlotView/DragGhostView's
 * own methods.
 *
 * Architecture note: earlier this app used per-SlotView press/drag/
 * release handlers, which worked for a single continuous "move from A to
 * B" gesture. That approach cannot express Minecraft's actual model,
 * where pickup and placement are two separate clicks and the held stack
 * must keep following the cursor with no button held in between. So
 * input is handled globally instead, via Scene-level event filters plus
 * the same bounds-based hit-testing (findSlotViewAt) this class already
 * used for drop targets - register() now only builds the list of known
 * SlotViews for that hit-testing, it no longer wires per-node handlers.
 */
public class DragAndDropController {

    private final List<SlotView> registeredSlotViews = new ArrayList<>();
    private final DragGhostView dragGhost = new DragGhostView();
    private final List<Runnable> moveListeners = new ArrayList<>();
    private final Map<SlotView, Supplier<ItemStack>> craftingOutputActions = new HashMap<>();
    private final Map<SlotView, Runnable> craftingOutputCallbacks = new HashMap<>();

    /** The stack currently riding the cursor, or null if the cursor is empty. */
    private ItemStack heldStack;

    /** Unique slots visited since the current mouse button went down, in visit order. */
    private final List<SlotView> currentPath = new ArrayList<>();

    /** Mounts this controller's drag ghost into the given overlay layer. */
    public void attachOverlay(Pane overlay) {
        overlay.getChildren().add(dragGhost);
    }

    /**
     * Hooks this controller into the Scene's mouse events. Must be called
     * once, after the Scene exists - typically right after GameApplication
     * constructs it, since global filters (rather than per-node handlers)
     * are what let the held stack keep following the cursor over empty
     * background, not just over registered slots.
     */
    public void attachInputHandling(Scene scene) {
        scene.addEventFilter(MouseEvent.MOUSE_PRESSED, this::onPressed);
        scene.addEventFilter(MouseEvent.MOUSE_DRAGGED, event -> onCursorMoved(event, true));
        scene.addEventFilter(MouseEvent.MOUSE_MOVED, event -> onCursorMoved(event, false));
        scene.addEventFilter(MouseEvent.MOUSE_RELEASED, this::onReleased);
    }

    /** Registers a SlotView so it can be found by hit-testing. Called once per SlotView, typically by ContainerView. */
    public void register(SlotView slotView) {
        registeredSlotViews.add(slotView);
    }

    /** Notified after every successful pickup/placement anywhere in the app. Cheap re-checks (e.g. "does the grid still match a recipe?") are expected on the other end. */
    public void addMoveListener(Runnable listener) {
        moveListeners.add(listener);
    }

    /**
     * Registers the crafting output slot's special pickup behavior: rather
     * than removing whatever ItemStack is sitting in the slot (which is
     * only ever a preview written by CraftingController, not real consumed
     * state), clicking this slot with an empty cursor calls craftAction
     * (craftingTable::craft) to actually consume the grid, and afterCollect
     * (craftingController::onGridChanged) to refresh the preview afterward.
     * This slot is not a valid placement target - clicking it while
     * already holding a stack, or including it in a distribution drag,
     * does nothing.
     */
    public void registerCraftingOutput(SlotView outputView, Supplier<ItemStack> craftAction, Runnable afterCollect) {
        registeredSlotViews.add(outputView);
        craftingOutputActions.put(outputView, craftAction);
        craftingOutputCallbacks.put(outputView, afterCollect);
    }

    private void onPressed(MouseEvent event) {
        if (event.getButton() == MouseButton.SECONDARY) {
            handleRightClick(event);
            return;
        }
        if (event.getButton() != MouseButton.PRIMARY) {
            return;
        }
        currentPath.clear();

        SlotView pressedSlot = findSlotViewAt(event.getSceneX(), event.getSceneY());
        if (pressedSlot == null) {
            return;
        }

        if (heldStack == null) {
            if (craftingOutputActions.containsKey(pressedSlot)) {
                pickUpCraftingOutput(pressedSlot, event);
            } else if (!pressedSlot.getSlot().isEmpty()) {
                pickUpFromSlot(pressedSlot, event);
            }
        } else {
            addToPathIfEligible(pressedSlot);
        }
    }

    /**
     * Right-click while holding a stack drops exactly one item into a
     * valid slot under the cursor - empty, or already holding the same
     * item with room to spare, matching Minecraft's "place one at a time"
     * behavior. A self-contained action, not part of the drag-path
     * machinery above: it fires once per right-click and never touches
     * currentPath. Does nothing if the cursor is empty, no slot is under
     * the cursor, the slot is not a valid target, or the slot is the
     * crafting output preview (not a real placement target).
     */
    private void handleRightClick(MouseEvent event) {
        if (heldStack == null) {
            return;
        }

        SlotView target = findSlotViewAt(event.getSceneX(), event.getSceneY());
        if (target == null || craftingOutputActions.containsKey(target)) {
            return;
        }

        Slot slot = target.getSlot();
        if (!slotAcceptsItem(slot, heldStack.getItem())) {
            return;
        }

        int before = quantityOf(slot);
        slot.addItem(heldStack.getItem(), 1);
        int absorbed = quantityOf(slot) - before;
        if (absorbed <= 0) {
            return;
        }
        target.refresh();

        int remaining = heldStack.getQuantity() - absorbed;
        if (remaining > 0) {
            heldStack = new ItemStack(heldStack.getItem(), remaining);
            dragGhost.showFor(heldStack, event.getSceneX(), event.getSceneY());
        } else {
            heldStack = null;
            dragGhost.hide();
        }
        notifyMoveListeners();
    }

    private void onCursorMoved(MouseEvent event, boolean buttonHeld) {
        if (heldStack == null) {
            return;
        }
        dragGhost.moveTo(event.getSceneX(), event.getSceneY());

        if (buttonHeld) {
            SlotView current = findSlotViewAt(event.getSceneX(), event.getSceneY());
            if (current != null) {
                addToPathIfEligible(current);
            }
        }
    }

    private void onReleased(MouseEvent event) {
        if (event.getButton() != MouseButton.PRIMARY) {
            return;
        }
        if (heldStack != null && !currentPath.isEmpty()) {
            distributeAcrossPath();
        }
        currentPath.clear();
    }

    private void addToPathIfEligible(SlotView slotView) {
        if (!craftingOutputActions.containsKey(slotView) && !currentPath.contains(slotView)) {
            currentPath.add(slotView);
        }
    }

    private void pickUpFromSlot(SlotView slotView, MouseEvent event) {
        Slot slot = slotView.getSlot();
        heldStack = slot.removeItem(slot.getItemStack().getQuantity());
        slotView.refresh();
        dragGhost.showFor(heldStack, event.getSceneX(), event.getSceneY());
        notifyMoveListeners();
    }

    private void pickUpCraftingOutput(SlotView outputView, MouseEvent event) {
        Slot previewSlot = outputView.getSlot();
        if (previewSlot.isEmpty()) {
            return;
        }

        ItemStack crafted = craftingOutputActions.get(outputView).get();
        if (crafted == null) {
            return;
        }

        // A fresh copy - craft() returns the recipe's own output ItemStack
        // reference directly, so holding that reference on the cursor
        // would let placing/merging it later mutate the recipe's
        // permanent template.
        heldStack = new ItemStack(crafted.getItem(), crafted.getQuantity());
        dragGhost.showFor(heldStack, event.getSceneX(), event.getSceneY());

        craftingOutputCallbacks.get(outputView).run();
    }

    /**
     * Places the held stack into a single valid slot (path of size 1), or
     * evenly distributes it across every valid slot visited during a drag
     * (path of size 2+). Skips incompatible or full slots entirely rather
     * than letting them absorb a partial share. Any amount that could not
     * be placed - because a target's real capacity fell short of its
     * fair share, or because no valid slot existed at all - stays on the
     * cursor. Total placed + total remaining on the cursor always equals
     * the amount held before this call: nothing is lost or duplicated.
     */
    private void distributeAcrossPath() {
        List<SlotView> validViews = new ArrayList<>();
        for (SlotView view : currentPath) {
            if (slotAcceptsItem(view.getSlot(), heldStack.getItem())) {
                validViews.add(view);
            }
        }

        if (validViews.isEmpty()) {
            return;
        }

        int totalHeld = heldStack.getQuantity();
        int slotCount = validViews.size();
        int baseShare = totalHeld / slotCount;
        int remainder = totalHeld % slotCount;

        int totalPlaced = 0;
        for (int i = 0; i < validViews.size(); i++) {
            int share = baseShare + (i < remainder ? 1 : 0);
            if (share <= 0) {
                continue;
            }
            Slot target = validViews.get(i).getSlot();
            int before = quantityOf(target);
            target.addItem(heldStack.getItem(), share);
            totalPlaced += quantityOf(target) - before;
            validViews.get(i).refresh();
        }

        int remaining = totalHeld - totalPlaced;
        if (remaining > 0) {
            heldStack = new ItemStack(heldStack.getItem(), remaining);
        } else {
            heldStack = null;
            dragGhost.hide();
        }
        notifyMoveListeners();
    }

    private boolean slotAcceptsItem(Slot slot, Item item) {
        if (slot.isEmpty()) {
            return true;
        }
        ItemStack existing = slot.getItemStack();
        return existing.getItem().equals(item) && existing.getRemainingCapacity() > 0;
    }

    private SlotView findSlotViewAt(double sceneX, double sceneY) {
        for (SlotView candidate : registeredSlotViews) {
            Bounds boundsInScene = candidate.localToScene(candidate.getBoundsInLocal());
            if (boundsInScene != null && boundsInScene.contains(sceneX, sceneY)) {
                return candidate;
            }
        }
        return null;
    }

    private void notifyMoveListeners() {
        for (Runnable listener : moveListeners) {
            listener.run();
        }
    }

    private int quantityOf(Slot slot) {
        return slot.isEmpty() ? 0 : slot.getItemStack().getQuantity();
    }
}