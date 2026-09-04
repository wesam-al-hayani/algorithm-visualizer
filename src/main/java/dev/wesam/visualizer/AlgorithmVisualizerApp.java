package dev.wesam.visualizer;

import dev.wesam.visualizer.algorithms.MazeAlgorithms;
import dev.wesam.visualizer.algorithms.SortAlgorithms;
import dev.wesam.visualizer.catalog.AlgorithmCatalog;
import dev.wesam.visualizer.catalog.AlgorithmDemo;
import dev.wesam.visualizer.model.AlgorithmRun;
import dev.wesam.visualizer.model.AlgorithmStep;
import dev.wesam.visualizer.ui.AlgorithmHistory;
import dev.wesam.visualizer.ui.EditHistory;
import dev.wesam.visualizer.ui.GridEditor;
import dev.wesam.visualizer.ui.InputFeedback;
import dev.wesam.visualizer.ui.InputGenerator;
import dev.wesam.visualizer.ui.PlaybackController;
import dev.wesam.visualizer.ui.PseudocodeView;
import dev.wesam.visualizer.ui.ResultExporter;
import dev.wesam.visualizer.ui.TreeLabInput;
import dev.wesam.visualizer.ui.VisualizationCanvas;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.EnumMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import java.util.Set;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Separator;
import javafx.scene.control.Slider;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputControl;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.Tooltip;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Duration;

public final class AlgorithmVisualizerApp extends Application {
  private final List<AlgorithmDemo> catalog = AlgorithmCatalog.create();
  private final ListView<String> categories = new ListView<>();
  private final TextField algorithmSearch = new TextField();
  private final ListView<AlgorithmDemo> searchResults = new ListView<>();
  private final ListView<AlgorithmDemo> favorites = new ListView<>();
  private final ListView<AlgorithmDemo> recentlyViewed = new ListView<>();
  private final ComboBox<AlgorithmDemo> algorithms = new ComboBox<>();
  private final TextField input = new TextField();
  private final Label inputHint = new Label();
  private final Label explanation = new Label();
  private final PseudocodeView pseudocode = new PseudocodeView();
  private final FlowPane statistics = new FlowPane(8, 8);
  private final Label operation = new Label("Ready");
  private final Label result = new Label("");
  private final Label progress = new Label("Step 0 / 0");
  private final Slider speed = new Slider(.25, 4, 1);
  private final VisualizationCanvas visualization = new VisualizationCanvas();
  private final Button start = new Button("Start"),
      pause = new Button("Pause"),
      resume = new Button("Resume"),
      stepButton = new Button("Step"),
      reset = new Button("Reset"),
      generate = new Button("Generate");
  private final ToggleButton favorite = new ToggleButton("☆ Favorite"),
      lightTheme = new ToggleButton("☀ Light");
  private final Button focusView = new Button("Focus View"),
      exportText = new Button("Export TXT"),
      exportCsv = new Button("Export CSV"),
      about = new Button("About");
  private final ToggleButton drawWalls = new ToggleButton("Draw Walls"),
      eraseWalls = new ToggleButton("Erase Walls"),
      setGridStart = new ToggleButton("Set Start"),
      setGridTarget = new ToggleButton("Set Target");
  private final Button clearPath = new Button("Clear Path"),
      clearGrid = new Button("Clear Grid"),
      maze = new Button("Generate Instantly");
  private final ComboBox<MazeAlgorithms.Method> mazeGenerator = new ComboBox<>();
  private final VBox gridTools =
      new VBox(
          7,
          new HBox(8, drawWalls, eraseWalls, setGridStart, setGridTarget, clearPath, clearGrid),
          new HBox(8, new Label("Maze"), mazeGenerator, maze));
  private final ComboBox<SortAlgorithms.Kind> compareFirst = new ComboBox<>(),
      compareSecond = new ComboBox<>();
  private final HBox compareTools =
      new HBox(8, new Label("Compare"), compareFirst, new Label("vs"), compareSecond);
  private final FlowPane raceTools = new FlowPane(8, 6);
  private final VBox sortingTools = new VBox(7, compareTools, raceTools);
  private final EnumMap<SortAlgorithms.Kind, CheckBox> raceChoices =
      new EnumMap<>(SortAlgorithms.Kind.class);
  private final TextField treeKey = new TextField();
  private final Button treeInsert = new Button("Insert"),
      treeSearch = new Button("Search"),
      treeDelete = new Button("Delete"),
      treeClear = new Button("Clear"),
      treeRandom = new Button("Random Tree");
  private final HBox treeTools =
      new HBox(8, treeKey, treeInsert, treeSearch, treeDelete, treeClear, treeRandom);
  private final PlaybackController playback = new PlaybackController();
  private final AlgorithmHistory history = new AlgorithmHistory();
  private final EditHistory editHistory = new EditHistory();
  private BorderPane root;
  private Region navigation;
  private SplitPane workspaceSplit;
  private ScrollPane informationScroll;
  private Timeline timeline;
  private boolean updatingFavorite;
  private boolean updatingSortingTools;

  @Override
  public void start(Stage stage) {
    root = new BorderPane();
    root.getStyleClass().add("app-root");
    root.setTop(header());
    navigation = sidebar();
    root.setLeft(navigation);
    root.setCenter(workspace());
    wireActions();
    categories.getSelectionModel().selectFirst();
    Scene scene = new Scene(root, 1280, 820);
    scene.getStylesheets().add(getClass().getResource("/styles.css").toExternalForm());
    installKeyboardShortcuts(scene);
    stage.setTitle("Algorithm Lab — Interactive Algorithms & Data Structures");
    stage.setMinWidth(980);
    stage.setMinHeight(680);
    stage.setScene(scene);
    stage.show();
  }

  private Region header() {
    Label mark = new Label("AL");
    mark.getStyleClass().add("brand-mark");
    VBox titles =
        new VBox(new Label("ALGORITHM LAB"), new Label("Interactive Algorithms & Data Structures"));
    titles.getStyleClass().add("brand-title");
    Region spacer = new Region();
    HBox.setHgrow(spacer, Priority.ALWAYS);
    HBox box =
        new HBox(10, mark, titles, spacer, lightTheme, focusView, exportText, exportCsv, about);
    box.getStyleClass().add("header");
    return box;
  }

  private Region sidebar() {
    LinkedHashSet<String> names = new LinkedHashSet<>();
    catalog.forEach(d -> names.add(d.category()));
    categories.getItems().setAll(names);
    algorithmSearch.setPromptText("Search algorithms…");
    algorithmSearch.setAccessibleText("Search algorithms by name or category");
    searchResults.setMaxHeight(170);
    searchResults.setManaged(false);
    searchResults.setVisible(false);
    favorites.setPrefHeight(96);
    recentlyViewed.setPrefHeight(118);
    categories.setPrefWidth(270);
    categories.setPrefHeight(290);
    categories.getStyleClass().add("category-list");
    categories.setAccessibleText("Algorithm categories");
    favorites.setAccessibleText("Favorite algorithms");
    recentlyViewed.setAccessibleText("Recently viewed algorithms");
    Label searchLabel = eyebrow("FIND AN ALGORITHM");
    Label categoryLabel = eyebrow("CATEGORIES");
    Label favoriteLabel = eyebrow("FAVORITES");
    Label recentLabel = eyebrow("RECENTLY VIEWED");
    VBox box =
        new VBox(
            9,
            searchLabel,
            algorithmSearch,
            searchResults,
            categoryLabel,
            categories,
            favoriteLabel,
            favorites,
            recentLabel,
            recentlyViewed);
    box.setPadding(new Insets(22, 14, 20, 20));
    box.getStyleClass().add("sidebar");
    VBox.setVgrow(categories, Priority.ALWAYS);
    return box;
  }

  private Region workspace() {
    algorithms.setMaxWidth(Double.MAX_VALUE);
    algorithms.setAccessibleText("Algorithm selection");
    HBox.setHgrow(algorithms, Priority.ALWAYS);
    Label choose = new Label("Algorithm");
    choose.getStyleClass().add("field-label");
    HBox selector = new HBox(12, choose, algorithms, favorite);
    selector.setAlignment(javafx.geometry.Pos.CENTER_LEFT);
    input.setPromptText("Input");
    input.setAccessibleText("Algorithm input");
    HBox.setHgrow(input, Priority.ALWAYS);
    inputHint.getStyleClass().add("hint");
    VBox inputBox = new VBox(6, new HBox(10, input, generate), inputHint);
    HBox controls =
        new HBox(
            8,
            start,
            pause,
            resume,
            stepButton,
            reset,
            new Separator(Orientation.VERTICAL),
            new Label("Speed"),
            speed);
    controls.setAlignment(javafx.geometry.Pos.CENTER_LEFT);
    speed.setPrefWidth(130);
    speed.setShowTickLabels(true);
    gridTools.setManaged(false);
    gridTools.setVisible(false);
    mazeGenerator.getItems().setAll(MazeAlgorithms.Method.values());
    mazeGenerator.getSelectionModel().select(MazeAlgorithms.Method.RECURSIVE_BACKTRACKING);
    mazeGenerator.setAccessibleText("Maze generation algorithm");
    initializeSortingTools();
    sortingTools.setManaged(false);
    sortingTools.setVisible(false);
    treeKey.setPromptText("Tree key");
    treeKey.setAccessibleText("Tree Lab key");
    treeKey.setPrefWidth(110);
    treeTools.setManaged(false);
    treeTools.setVisible(false);
    treeTools.getStyleClass().add("tree-tools");
    VBox top = new VBox(12, selector, inputBox, gridTools, sortingTools, treeTools, controls);
    top.getStyleClass().add("control-card");
    VBox visualCard = new VBox(10, operation, visualization, new HBox(12, progress, result));
    visualCard.getStyleClass().add("visual-card");
    VBox.setVgrow(visualization, Priority.ALWAYS);
    operation.getStyleClass().add("operation");
    result.getStyleClass().add("result");
    HBox.setHgrow(result, Priority.ALWAYS);
    VBox info = informationPanel();
    informationScroll = new ScrollPane(info);
    informationScroll.setFitToWidth(true);
    informationScroll.getStyleClass().add("info-scroll");
    workspaceSplit = new SplitPane(visualCard, informationScroll);
    workspaceSplit.setDividerPositions(.72);
    VBox.setVgrow(workspaceSplit, Priority.ALWAYS);
    VBox body = new VBox(14, top, workspaceSplit);
    body.setPadding(new Insets(20));
    return body;
  }

  private VBox informationPanel() {
    Label aboutTitle = new Label("HOW IT WORKS");
    aboutTitle.getStyleClass().add("eyebrow");
    explanation.setWrapText(true);
    Label pseudoTitle = new Label("PSEUDOCODE");
    pseudoTitle.getStyleClass().add("eyebrow");
    pseudocode.setPrefHeight(190);
    Label statsTitle = new Label("LIVE STATISTICS");
    statsTitle.getStyleClass().add("eyebrow");
    Label legend = new Label("● active    ● frontier / alternate    ● complete");
    legend.getStyleClass().add("legend");
    VBox info =
        new VBox(
            10,
            aboutTitle,
            explanation,
            new Separator(),
            pseudoTitle,
            pseudocode,
            new Separator(),
            statsTitle,
            statistics,
            new Separator(),
            legend);
    info.setPadding(new Insets(18));
    info.getStyleClass().add("info-panel");
    return info;
  }

  private void wireActions() {
    categories
        .getSelectionModel()
        .selectedItemProperty()
        .addListener(
            (o, old, value) -> {
              stopPlayback();
              algorithms
                  .getItems()
                  .setAll(catalog.stream().filter(d -> d.category().equals(value)).toList());
              algorithms.getSelectionModel().selectFirst();
            });
    algorithmSearch
        .textProperty()
        .addListener((observable, oldValue, query) -> updateSearchResults(query));
    searchResults
        .getSelectionModel()
        .selectedItemProperty()
        .addListener((observable, oldValue, demo) -> navigateTo(demo));
    favorites
        .getSelectionModel()
        .selectedItemProperty()
        .addListener(
            (observable, oldValue, demo) -> {
              if (demo != null) Platform.runLater(() -> navigateTo(demo));
            });
    recentlyViewed
        .getSelectionModel()
        .selectedItemProperty()
        .addListener(
            (observable, oldValue, demo) -> {
              if (demo != null) Platform.runLater(() -> navigateTo(demo));
            });
    algorithms
        .valueProperty()
        .addListener(
            (o, old, demo) -> {
              stopPlayback();
              playback.reset();
              editHistory.clear();
              visualization.show(null);
              updateButtons();
              boolean isGrid = demo != null && demo.name().startsWith("Grid ");
              boolean isTreeLab = demo != null && TreeLabInput.supports(demo.name());
              boolean isSortingTool =
                  demo != null
                      && (demo.name().equals("Sorting Compare Mode")
                          || demo.name().equals("Sorting Race"));
              gridTools.setManaged(isGrid);
              gridTools.setVisible(isGrid);
              sortingTools.setManaged(isSortingTool);
              sortingTools.setVisible(isSortingTool);
              treeTools.setManaged(isTreeLab);
              treeTools.setVisible(isTreeLab);
              if (demo != null) {
                history.recordViewed(demo);
                updatingFavorite = true;
                favorite.setSelected(history.isFavorite(demo));
                updateFavoriteLabel();
                updatingFavorite = false;
                refreshHistoryLists();
                input.setText(demo.defaultInput());
                if (isSortingTool) configureSortingTools(demo.name());
                inputHint.setText(demo.inputHint());
                explanation.setText(
                    demo.explanation()
                        + "\n\nTime: "
                        + demo.timeComplexity()
                        + "\nSpace: "
                        + demo.spaceComplexity());
                pseudocode.showCode(demo.pseudocode(), -1);
                operation.setText(demo.name());
                result.setText("");
                progress.setText("Step 0 / 0");
                statistics.getChildren().clear();
                if (isGrid) previewGrid();
                if (isTreeLab) showTreeLab();
              }
            });
    favorite.setOnAction(
        event -> {
          if (updatingFavorite || algorithms.getValue() == null) return;
          history.setFavorite(algorithms.getValue(), favorite.isSelected());
          updateFavoriteLabel();
          refreshHistoryLists();
        });
    lightTheme.setOnAction(event -> updateTheme());
    focusView.setOnAction(event -> updateFocusMode());
    about.setOnAction(event -> showAbout());
    exportText.setOnAction(event -> exportCurrent("txt"));
    exportCsv.setOnAction(event -> exportCurrent("csv"));
    input
        .textProperty()
        .addListener(
            (observable, oldValue, newValue) -> {
              if (playback.hasRun()) {
                stopPlayback();
                playback.reset();
                visualization.show(null);
                operation.setText("Input changed — press Start to run again");
                progress.setText("Step 0 / 0");
                result.setText("");
                statistics.getChildren().clear();
                updateButtons();
              }
            });
    start.setOnAction(e -> startRun());
    pause.setOnAction(
        e -> {
          if (timeline != null) timeline.pause();
          playback.pause();
          updateButtons();
        });
    resume.setOnAction(
        e -> {
          if (timeline != null) {
            playback.resume();
            timeline.play();
            updateButtons();
          }
        });
    stepButton.setOnAction(
        e -> {
          if (!playback.hasRun() && !prepare()) return;
          if (timeline != null) timeline.pause();
          playback.pause();
          advance();
          updateButtons();
        });
    reset.setOnAction(e -> reset());
    generate.setOnAction(e -> randomize());
    speed
        .valueProperty()
        .addListener(
            (o, a, b) -> {
              if (timeline != null) timeline.setRate(b.doubleValue());
            });
    updateButtons();
    ToggleGroup editMode = new ToggleGroup();
    drawWalls.setToggleGroup(editMode);
    eraseWalls.setToggleGroup(editMode);
    setGridStart.setToggleGroup(editMode);
    setGridTarget.setToggleGroup(editMode);
    drawWalls.setSelected(true);
    visualization.setOnGridCellClicked(this::editGridCell);
    clearPath.setOnAction(
        e -> {
          reset();
          previewGrid();
        });
    clearGrid.setOnAction(
        e -> {
          makeClearGrid();
          previewGrid();
        });
    maze.setOnAction(
        e -> {
          makeMaze();
          previewGrid();
        });
    compareFirst.setOnAction(event -> updateCompareSelection());
    compareSecond.setOnAction(event -> updateCompareSelection());
    raceChoices
        .values()
        .forEach(choice -> choice.setOnAction(event -> updateRaceSelection(choice)));
    treeInsert.setOnAction(e -> performTreeOperation(TreeLabInput.Operation.INSERT));
    treeSearch.setOnAction(e -> performTreeOperation(TreeLabInput.Operation.SEARCH));
    treeDelete.setOnAction(e -> performTreeOperation(TreeLabInput.Operation.DELETE));
    treeClear.setOnAction(e -> clearTreeLab());
    treeRandom.setOnAction(
        e -> {
          input.setText(TreeLabInput.randomTree(new Random(), 10));
          showTreeLab();
        });
    treeKey.setOnAction(e -> performTreeOperation(TreeLabInput.Operation.INSERT));
  }

  private void startRun() {
    if (!prepare()) return;
    if (playback.totalSteps() == 0) {
      operation.setText("Completed without visual steps");
      result.setText(playback.result());
      updateButtons();
      return;
    }
    playback.start();
    timeline = new Timeline(new KeyFrame(Duration.millis(760), e -> advance()));
    timeline.setCycleCount(Timeline.INDEFINITE);
    timeline.setRate(speed.getValue());
    timeline.play();
    updateButtons();
  }

  private boolean prepare() {
    stopPlayback();
    try {
      AlgorithmDemo demo = algorithms.getValue();
      if (demo == null) return false;
      AlgorithmRun run = demo.runner().apply(input.getText());
      playback.load(run);
      result.setText("");
      progress.setText("Step 0 / " + playback.totalSteps());
      return true;
    } catch (Exception exception) {
      showInputError(exception);
      return false;
    }
  }

  private void performTreeOperation(TreeLabInput.Operation operationType) {
    try {
      int key = Integer.parseInt(treeKey.getText().trim());
      input.setText(TreeLabInput.append(input.getText(), operationType, key));
      treeKey.clear();
      showTreeLab();
    } catch (Exception exception) {
      showInputError(exception);
    }
  }

  private void clearTreeLab() {
    input.clear();
    reset();
    operation.setText(algorithms.getValue().name() + " cleared");
    result.setText("Empty tree");
    treeKey.requestFocus();
  }

  private void showTreeLab() {
    stopPlayback();
    playback.reset();
    if (input.getText().isBlank()) {
      visualization.show(null);
      statistics.getChildren().clear();
      progress.setText("Persistent tree · 0 operations");
      updateButtons();
      return;
    }
    try {
      AlgorithmRun run = algorithms.getValue().runner().apply(input.getText());
      AlgorithmStep current = run.steps().get(run.steps().size() - 1);
      visualization.show(current);
      operation.setText(current.message());
      pseudocode.showCode(current.pseudocode(), current.activeLine());
      showStatistics(current);
      progress.setText("Persistent tree · " + run.steps().size() + " operations");
      result.setText(run.result());
      updateButtons();
    } catch (Exception exception) {
      showInputError(exception);
    }
  }

  private void showInputError(Exception exception) {
    Alert alert = new Alert(Alert.AlertType.ERROR);
    alert.setHeaderText("Invalid input");
    AlgorithmDemo demo = algorithms.getValue();
    alert.setContentText(
        demo == null
            ? "Choose an algorithm before entering input."
            : InputFeedback.message(exception, demo));
    alert.showAndWait();
  }

  private void exportCurrent(String extension) {
    AlgorithmDemo demo = algorithms.getValue();
    AlgorithmStep current = visualization.currentStep();
    if (demo == null || current == null) {
      Alert alert = new Alert(Alert.AlertType.INFORMATION);
      alert.setHeaderText("Nothing to export yet");
      alert.setContentText("Run or step through an algorithm before exporting its current state.");
      alert.showAndWait();
      return;
    }
    FileChooser chooser = new FileChooser();
    chooser.setTitle("Export Algorithm Lab result");
    chooser.setInitialFileName(ResultExporter.suggestedBaseName(demo) + "." + extension);
    chooser
        .getExtensionFilters()
        .add(
            new FileChooser.ExtensionFilter(
                extension.equals("csv") ? "CSV data (*.csv)" : "Text report (*.txt)",
                "*." + extension));
    java.io.File destination = chooser.showSaveDialog(root.getScene().getWindow());
    if (destination == null) return;
    String finalResult = result.getText().isBlank() ? playback.result() : result.getText();
    String content =
        extension.equals("csv")
            ? ResultExporter.csv(demo, input.getText(), current, finalResult)
            : ResultExporter.text(demo, input.getText(), current, finalResult);
    try {
      Files.writeString(destination.toPath(), content, StandardCharsets.UTF_8);
      result.setText("Exported " + destination.getName());
    } catch (java.io.IOException exception) {
      showInputError(
          new IllegalArgumentException("Could not save the export: " + exception.getMessage()));
    }
  }

  private void advance() {
    var next = playback.advanceOne();
    if (next.isEmpty()) {
      finish();
      return;
    }
    AlgorithmStep current = next.orElseThrow();
    visualization.show(current);
    operation.setText(current.message());
    pseudocode.showCode(current.pseudocode(), current.activeLine());
    showStatistics(current);
    progress.setText("Step " + playback.position() + " / " + playback.totalSteps());
    if (playback.state() == PlaybackController.State.FINISHED) finish();
  }

  private void finish() {
    stopPlayback();
    if (playback.hasRun()) result.setText(playback.result());
    operation.setText("Complete");
    updateButtons();
  }

  private void reset() {
    stopPlayback();
    playback.reset();
    visualization.show(null);
    result.setText("");
    operation.setText(algorithms.getValue() == null ? "Ready" : algorithms.getValue().name());
    progress.setText("Step 0 / 0");
    statistics.getChildren().clear();
    updateButtons();
  }

  private void stopPlayback() {
    if (timeline != null) {
      timeline.stop();
      timeline = null;
    }
  }

  private void updateButtons() {
    boolean running = playback.state() == PlaybackController.State.RUNNING;
    boolean paused = playback.state() == PlaybackController.State.PAUSED;
    pause.setDisable(!running);
    resume.setDisable(!paused || !playback.hasRemainingSteps());
    stepButton.setDisable(running);
    start.setDisable(running);
  }

  private void showStatistics(AlgorithmStep current) {
    statistics.getChildren().clear();
    current
        .statistics()
        .forEach(
            (name, value) -> {
              VBox card = new VBox(new Label(name.toUpperCase()), new Label(format(value)));
              card.getStyleClass().add("stat-card");
              statistics.getChildren().add(card);
            });
    if (!current.details().isBlank()) {
      Label details = new Label(current.details().replaceAll("\\ncolumns=\\d+", ""));
      details.setWrapText(true);
      details.getStyleClass().add("details");
      statistics.getChildren().add(details);
    }
  }

  private String format(Number n) {
    return n instanceof Double || n instanceof Float
        ? String.format("%.2f", n.doubleValue())
        : Long.toString(n.longValue());
  }

  private void randomize() {
    AlgorithmDemo demo = algorithms.getValue();
    if (demo == null) return;
    String generated = InputGenerator.generate(demo, input.getText(), new Random());
    if (demo.name().startsWith("Grid ")) applyEdit(generated);
    else input.setText(generated);
    reset();
    if (demo.name().startsWith("Grid ")) previewGrid();
    if (TreeLabInput.supports(demo.name())) showTreeLab();
  }

  private void previewGrid() {
    try {
      AlgorithmRun preview = algorithms.getValue().runner().apply(input.getText());
      if (!preview.steps().isEmpty()) visualization.show(preview.steps().get(0));
    } catch (Exception ignored) {
    }
  }

  private void editGridCell(int row, int column) {
    if (!gridTools.isVisible()) return;
    GridEditor.Mode mode =
        setGridStart.isSelected()
            ? GridEditor.Mode.SET_START
            : setGridTarget.isSelected()
                ? GridEditor.Mode.SET_TARGET
                : eraseWalls.isSelected()
                    ? GridEditor.Mode.ERASE_WALLS
                    : GridEditor.Mode.DRAW_WALLS;
    applyEdit(GridEditor.edit(input.getText(), row, column, mode));
    reset();
    previewGrid();
  }

  private void makeClearGrid() {
    applyEdit(GridEditor.clear(input.getText()));
    reset();
  }

  private void makeMaze() {
    MazeAlgorithms.Method method = mazeGenerator.getValue();
    if (method == null) method = MazeAlgorithms.Method.RECURSIVE_BACKTRACKING;
    applyEdit(MazeAlgorithms.generate(method, input.getText(), new Random()).grid());
    reset();
  }

  private void initializeSortingTools() {
    compareFirst.getItems().setAll(SortAlgorithms.Kind.values());
    compareSecond.getItems().setAll(SortAlgorithms.Kind.values());
    compareFirst.setAccessibleText("First sorting algorithm");
    compareSecond.setAccessibleText("Second sorting algorithm");
    for (SortAlgorithms.Kind kind : SortAlgorithms.Kind.values()) {
      CheckBox choice = new CheckBox(sortName(kind));
      raceChoices.put(kind, choice);
      raceTools.getChildren().add(choice);
    }
    sortingTools.getStyleClass().add("comparison-tools");
  }

  private void configureSortingTools(String demoName) {
    updatingSortingTools = true;
    boolean compare = demoName.equals("Sorting Compare Mode");
    compareTools.setManaged(compare);
    compareTools.setVisible(compare);
    raceTools.setManaged(!compare);
    raceTools.setVisible(!compare);
    if (compare) {
      compareFirst.setValue(SortAlgorithms.Kind.MERGE);
      compareSecond.setValue(SortAlgorithms.Kind.QUICK);
    } else {
      Set<SortAlgorithms.Kind> defaults =
          Set.of(
              SortAlgorithms.Kind.BUBBLE,
              SortAlgorithms.Kind.INSERTION,
              SortAlgorithms.Kind.MERGE,
              SortAlgorithms.Kind.QUICK,
              SortAlgorithms.Kind.HEAP);
      raceChoices.forEach((kind, choice) -> choice.setSelected(defaults.contains(kind)));
    }
    updatingSortingTools = false;
  }

  private void updateCompareSelection() {
    if (updatingSortingTools || compareFirst.getValue() == null || compareSecond.getValue() == null)
      return;
    if (compareFirst.getValue() == compareSecond.getValue()) {
      operation.setText("Choose two distinct sorting algorithms");
      return;
    }
    replaceSortingSelection(compareFirst.getValue().name() + "," + compareSecond.getValue().name());
  }

  private void updateRaceSelection(CheckBox changed) {
    if (updatingSortingTools) return;
    List<SortAlgorithms.Kind> selected =
        raceChoices.entrySet().stream()
            .filter(entry -> entry.getValue().isSelected())
            .map(java.util.Map.Entry::getKey)
            .toList();
    if (selected.size() > 6) {
      updatingSortingTools = true;
      changed.setSelected(false);
      updatingSortingTools = false;
      operation.setText("Sorting Race supports at most six algorithms");
      return;
    }
    if (selected.size() < 2) {
      operation.setText("Choose at least two algorithms for Sorting Race");
      return;
    }
    replaceSortingSelection(
        selected.stream().map(Enum::name).collect(java.util.stream.Collectors.joining(",")));
  }

  private void replaceSortingSelection(String selection) {
    String current = input.getText();
    String array =
        current.contains(";") ? current.substring(current.indexOf(';') + 1).trim() : current;
    input.setText(selection + " ; " + array);
    reset();
  }

  private static String sortName(SortAlgorithms.Kind kind) {
    String[] words = kind.name().toLowerCase(Locale.ROOT).split("_");
    StringBuilder name = new StringBuilder();
    for (String word : words) {
      if (!name.isEmpty()) name.append(' ');
      name.append(Character.toUpperCase(word.charAt(0))).append(word.substring(1));
    }
    return name.toString();
  }

  private Label eyebrow(String text) {
    Label label = new Label(text);
    label.getStyleClass().add("eyebrow");
    return label;
  }

  private void updateSearchResults(String query) {
    String normalized = query == null ? "" : query.trim().toLowerCase();
    boolean searching = !normalized.isEmpty();
    searchResults.setManaged(searching);
    searchResults.setVisible(searching);
    if (!searching) {
      searchResults.getItems().clear();
      return;
    }
    searchResults
        .getItems()
        .setAll(
            catalog.stream()
                .filter(
                    demo ->
                        demo.name().toLowerCase().contains(normalized)
                            || demo.category().toLowerCase().contains(normalized)
                            || demo.explanation().toLowerCase().contains(normalized))
                .toList());
  }

  private void navigateTo(AlgorithmDemo demo) {
    if (demo == null) return;
    categories.getSelectionModel().select(demo.category());
    algorithms.getSelectionModel().select(demo);
  }

  private void refreshHistoryLists() {
    favorites.getItems().setAll(history.favorites(catalog));
    recentlyViewed.getItems().setAll(history.recentlyViewed(catalog));
  }

  private void updateFavoriteLabel() {
    favorite.setText(favorite.isSelected() ? "★ Favorite" : "☆ Favorite");
  }

  private void updateTheme() {
    root.getStyleClass().remove("light-theme");
    if (lightTheme.isSelected()) root.getStyleClass().add("light-theme");
    lightTheme.setText(lightTheme.isSelected() ? "☾ Dark" : "☀ Light");
  }

  private void updateFocusMode() {
    if (focusView.getText().equals("Focus View")) {
      root.setLeft(null);
      workspaceSplit.getItems().remove(informationScroll);
      focusView.setText("Exit Focus");
    } else {
      root.setLeft(navigation);
      if (!workspaceSplit.getItems().contains(informationScroll))
        workspaceSplit.getItems().add(informationScroll);
      workspaceSplit.setDividerPositions(.72);
      focusView.setText("Focus View");
    }
  }

  private void showAbout() {
    Alert dialog = new Alert(Alert.AlertType.INFORMATION);
    dialog.setTitle("About Algorithm Lab");
    dialog.setHeaderText("Algorithm Lab 2.0.0");
    dialog.setContentText(
        "Interactive Algorithms & Data Structures\n\n"
            + "Java "
            + System.getProperty("java.version")
            + " · JavaFX "
            + System.getProperty("javafx.version", "21")
            + "\n\nAn independent educational project inspired by computer science coursework.");
    dialog.showAndWait();
  }

  private void installKeyboardShortcuts(Scene scene) {
    start.setTooltip(new Tooltip("Start playback (Space)"));
    pause.setTooltip(new Tooltip("Pause playback (Space)"));
    stepButton.setTooltip(new Tooltip("Advance one logical step (Right Arrow)"));
    reset.setTooltip(new Tooltip("Reset (R)"));
    generate.setTooltip(new Tooltip("Generate input (G)"));
    scene.addEventFilter(KeyEvent.KEY_PRESSED, this::handleShortcut);
  }

  private void handleShortcut(KeyEvent event) {
    if (event.isShortcutDown() && event.getCode() == KeyCode.F) {
      algorithmSearch.requestFocus();
      algorithmSearch.selectAll();
      event.consume();
      return;
    }
    if (event.isShortcutDown() && event.getCode() == KeyCode.Z) {
      if (event.isShiftDown()) redoEdit();
      else undoEdit();
      event.consume();
      return;
    }
    if (event.getTarget() instanceof TextInputControl) return;
    if (event.getCode() == KeyCode.SPACE) {
      if (playback.state() == PlaybackController.State.RUNNING) pause.fire();
      else if (playback.state() == PlaybackController.State.PAUSED) resume.fire();
      else start.fire();
    } else if (event.getCode() == KeyCode.RIGHT) stepButton.fire();
    else if (event.getCode() == KeyCode.R) reset.fire();
    else if (event.getCode() == KeyCode.G) generate.fire();
    else return;
    event.consume();
  }

  private void applyEdit(String replacement) {
    input.setText(editHistory.apply(input.getText(), replacement));
  }

  private void undoEdit() {
    if (!isEditableGraphOrGrid()) return;
    editHistory.undo(input.getText()).ifPresent(this::restoreEdit);
  }

  private void redoEdit() {
    if (!isEditableGraphOrGrid()) return;
    editHistory.redo(input.getText()).ifPresent(this::restoreEdit);
  }

  private boolean isEditableGraphOrGrid() {
    AlgorithmDemo demo = algorithms.getValue();
    return demo != null && demo.category().equals("Graph Algorithms");
  }

  private void restoreEdit(String value) {
    input.setText(value);
    reset();
    if (algorithms.getValue().name().startsWith("Grid ")) previewGrid();
  }

  public static void main(String[] args) {
    launch(args);
  }
}
