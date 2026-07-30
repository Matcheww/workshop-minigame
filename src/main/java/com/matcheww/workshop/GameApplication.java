package com.matcheww.workshop;

import com.matcheww.workshop.controller.CraftingController;
import com.matcheww.workshop.controller.FurnaceController;
import com.matcheww.workshop.controller.GameController;
import com.matcheww.workshop.view.GameView;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * JavaFX bootstrap / composition root. Intentionally outside all three MVC
 * layers:
 *
 * - It is NOT the Controller layer: beyond the one-time wiring below, it
 *   contains no application flow, performs no validation, and never calls
 *   a Model method once the window is shown.
 * - It is NOT the View layer: it renders nothing itself.
 *
 * Its responsibility is wiring: construct GameController (the Models),
 * build GameView on top of it, then construct the feature-specific
 * controllers (CraftingController, FurnaceController) around the views
 * GameView just built, and connect DragAndDropController's extension
 * points to them. This last part only happens here because it is the one
 * place that has both the Models and the already-built Views available at
 * the same time.
 */
public class GameApplication extends Application {

    private static final double WINDOW_WIDTH = 960;
    private static final double WINDOW_HEIGHT = 680;

    private FurnaceController furnaceController;

    @Override
    public void start(Stage primaryStage) {
        GameController gameController = new GameController();
        GameView gameView = new GameView(gameController);

        CraftingController craftingController = new CraftingController(
                gameController.getCraftingTable(),
                gameController.getRecipeBank(),
                gameView.getCraftingTableView());

        furnaceController = new FurnaceController(
                gameController.getFurnace(),
                gameController.getRecipeBank(),
                gameView.getFurnaceView());

        gameController.getDragAndDropController().registerCraftingOutput(
                gameView.getCraftingTableView().getOutputSlotView(),
                gameController.getCraftingTable()::craft,
                craftingController::onGridChanged);

        gameController.getDragAndDropController().addMoveListener(craftingController::onGridChanged);
        gameController.getDragAndDropController().addMoveListener(furnaceController::onInputChanged);

        Scene scene = new Scene(gameView, WINDOW_WIDTH, WINDOW_HEIGHT);
        scene.getStylesheets().add(getClass().getResource("/css/minecraft.css").toExternalForm());

        primaryStage.setTitle("Workshop Minigame");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    @Override
    public void stop() {
        if (furnaceController != null) {
            furnaceController.shutdown();
        }
    }
}