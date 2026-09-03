package dev.wesam.visualizer;

import dev.wesam.visualizer.catalog.AlgorithmCatalog;
import dev.wesam.visualizer.catalog.AlgorithmDemo;
import dev.wesam.visualizer.model.AlgorithmRun;
import dev.wesam.visualizer.model.AlgorithmStep;
import dev.wesam.visualizer.ui.VisualizationCanvas;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Separator;
import javafx.scene.control.Slider;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Random;

public final class AlgorithmVisualizerApp extends Application {
    private final List<AlgorithmDemo> catalog=AlgorithmCatalog.create();
    private final ListView<String> categories=new ListView<>();
    private final ComboBox<AlgorithmDemo> algorithms=new ComboBox<>();
    private final TextField input=new TextField();
    private final Label inputHint=new Label();
    private final Label explanation=new Label();
    private final TextArea pseudocode=new TextArea();
    private final FlowPane statistics=new FlowPane(8,8);
    private final Label operation=new Label("Ready");
    private final Label result=new Label("");
    private final Label progress=new Label("Step 0 / 0");
    private final Slider speed=new Slider(.25,4,1);
    private final VisualizationCanvas visualization=new VisualizationCanvas();
    private final Button start=new Button("Start"),pause=new Button("Pause"),resume=new Button("Resume"),stepButton=new Button("Step"),reset=new Button("Reset"),generate=new Button("Generate");
    private AlgorithmRun run;
    private int frame;
    private Timeline timeline;

    @Override public void start(Stage stage){
        BorderPane root=new BorderPane();root.getStyleClass().add("app-root");root.setTop(header());root.setLeft(sidebar());root.setCenter(workspace());
        wireActions();categories.getSelectionModel().selectFirst();
        Scene scene=new Scene(root,1280,820);scene.getStylesheets().add(getClass().getResource("/styles.css").toExternalForm());
        stage.setTitle("Algorithm & Data Structure Visualizer");stage.setMinWidth(980);stage.setMinHeight(680);stage.setScene(scene);stage.show();
    }

    private Region header(){Label mark=new Label("AV");mark.getStyleClass().add("brand-mark");VBox titles=new VBox(new Label("ALGORITHM LAB"),new Label("Algorithm & Data Structure Visualizer"));titles.getStyleClass().add("brand-title");Region spacer=new Region();HBox.setHgrow(spacer,Priority.ALWAYS);Label course=new Label("DAP1 · DAP2 · Efficient Algorithms");course.getStyleClass().add("course-pill");HBox box=new HBox(14,mark,titles,spacer,course);box.getStyleClass().add("header");return box;}
    private Region sidebar(){LinkedHashSet<String>names=new LinkedHashSet<>();catalog.forEach(d->names.add(d.category()));categories.getItems().setAll(names);categories.setPrefWidth(235);categories.getStyleClass().add("category-list");Label label=new Label("CATEGORIES");label.getStyleClass().add("eyebrow");VBox box=new VBox(12,label,categories);box.setPadding(new Insets(22,14,20,20));box.getStyleClass().add("sidebar");VBox.setVgrow(categories,Priority.ALWAYS);return box;}
    private Region workspace(){
        algorithms.setMaxWidth(Double.MAX_VALUE);HBox.setHgrow(algorithms,Priority.ALWAYS);Label choose=new Label("Algorithm");choose.getStyleClass().add("field-label");HBox selector=new HBox(12,choose,algorithms);selector.setAlignment(javafx.geometry.Pos.CENTER_LEFT);
        input.setPromptText("Input");HBox.setHgrow(input,Priority.ALWAYS);inputHint.getStyleClass().add("hint");VBox inputBox=new VBox(6,new HBox(10,input,generate),inputHint);
        HBox controls=new HBox(8,start,pause,resume,stepButton,reset,new Separator(Orientation.VERTICAL),new Label("Speed"),speed);controls.setAlignment(javafx.geometry.Pos.CENTER_LEFT);speed.setPrefWidth(130);speed.setShowTickLabels(true);
        VBox top=new VBox(12,selector,inputBox,controls);top.getStyleClass().add("control-card");
        VBox visualCard=new VBox(10,operation,visualization,new HBox(12,progress,result));visualCard.getStyleClass().add("visual-card");VBox.setVgrow(visualization,Priority.ALWAYS);operation.getStyleClass().add("operation");result.getStyleClass().add("result");HBox.setHgrow(result,Priority.ALWAYS);
        VBox info=informationPanel();ScrollPane scroll=new ScrollPane(info);scroll.setFitToWidth(true);scroll.getStyleClass().add("info-scroll");
        SplitPane split=new SplitPane(visualCard,scroll);split.setDividerPositions(.72);VBox.setVgrow(split,Priority.ALWAYS);
        VBox body=new VBox(14,top,split);body.setPadding(new Insets(20));return body;
    }
    private VBox informationPanel(){Label aboutTitle=new Label("HOW IT WORKS");aboutTitle.getStyleClass().add("eyebrow");explanation.setWrapText(true);Label pseudoTitle=new Label("PSEUDOCODE");pseudoTitle.getStyleClass().add("eyebrow");pseudocode.setEditable(false);pseudocode.setWrapText(false);pseudocode.setPrefRowCount(8);Label statsTitle=new Label("LIVE STATISTICS");statsTitle.getStyleClass().add("eyebrow");Label legend=new Label("● active    ● frontier / alternate    ● complete");legend.getStyleClass().add("legend");VBox info=new VBox(10,aboutTitle,explanation,new Separator(),pseudoTitle,pseudocode,new Separator(),statsTitle,statistics,new Separator(),legend);info.setPadding(new Insets(18));info.getStyleClass().add("info-panel");return info;}

    private void wireActions(){
        categories.getSelectionModel().selectedItemProperty().addListener((o,old,value)->{stopPlayback();algorithms.getItems().setAll(catalog.stream().filter(d->d.category().equals(value)).toList());algorithms.getSelectionModel().selectFirst();});
        algorithms.valueProperty().addListener((o,old,demo)->{stopPlayback();run=null;frame=0;visualization.show(null);updateButtons(false,false);if(demo!=null){input.setText(demo.defaultInput());inputHint.setText(demo.inputHint());explanation.setText(demo.explanation()+"\n\nTime: "+demo.timeComplexity()+"\nSpace: "+demo.spaceComplexity());pseudocode.setText(demo.pseudocode());operation.setText(demo.name());result.setText("");progress.setText("Step 0 / 0");statistics.getChildren().clear();}});
        start.setOnAction(e->startRun());pause.setOnAction(e->{if(timeline!=null)timeline.pause();updateButtons(false,true);});resume.setOnAction(e->{if(timeline!=null){timeline.play();updateButtons(true,false);}});stepButton.setOnAction(e->{if(run==null)prepare();if(timeline!=null)timeline.pause();advance();updateButtons(false,true);});reset.setOnAction(e->reset());generate.setOnAction(e->randomize());speed.valueProperty().addListener((o,a,b)->{if(timeline!=null)timeline.setRate(b.doubleValue());});
        updateButtons(false,false);
    }
    private void startRun(){if(!prepare())return;if(run.steps().isEmpty()){operation.setText("Completed without visual steps");result.setText(run.result());return;}timeline=new Timeline(new KeyFrame(Duration.millis(760),e->advance()));timeline.setCycleCount(Timeline.INDEFINITE);timeline.setRate(speed.getValue());timeline.play();updateButtons(true,false);}
    private boolean prepare(){stopPlayback();try{AlgorithmDemo demo=algorithms.getValue();if(demo==null)return false;run=demo.runner().apply(input.getText());frame=0;result.setText("");progress.setText("Step 0 / "+run.steps().size());return true;}catch(Exception exception){Alert alert=new Alert(Alert.AlertType.ERROR);alert.setHeaderText("Invalid input");alert.setContentText(exception.getMessage()==null?exception.toString():exception.getMessage());alert.showAndWait();return false;}}
    private void advance(){if(run==null||frame>=run.steps().size()){finish();return;}AlgorithmStep current=run.steps().get(frame++);visualization.show(current);operation.setText(current.message());pseudocode.setText(markLine(current.pseudocode(),current.activeLine()));showStatistics(current);progress.setText("Step "+frame+" / "+run.steps().size());if(frame>=run.steps().size())finish();}
    private void finish(){stopPlayback();if(run!=null)result.setText(run.result());operation.setText("Complete");updateButtons(false,false);}
    private void reset(){stopPlayback();run=null;frame=0;visualization.show(null);result.setText("");operation.setText(algorithms.getValue()==null?"Ready":algorithms.getValue().name());progress.setText("Step 0 / 0");statistics.getChildren().clear();updateButtons(false,false);}
    private void stopPlayback(){if(timeline!=null){timeline.stop();timeline=null;}}
    private void updateButtons(boolean running,boolean paused){pause.setDisable(!running);resume.setDisable(!paused||run==null||frame>=run.steps().size());stepButton.setDisable(running);start.setDisable(running);}
    private void showStatistics(AlgorithmStep current){statistics.getChildren().clear();current.statistics().forEach((name,value)->{VBox card=new VBox(new Label(name.toUpperCase()),new Label(format(value)));card.getStyleClass().add("stat-card");statistics.getChildren().add(card);});if(!current.details().isBlank()){Label details=new Label(current.details().replaceAll("\\ncolumns=\\d+", ""));details.setWrapText(true);details.getStyleClass().add("details");statistics.getChildren().add(details);}}
    private String format(Number n){return n instanceof Double||n instanceof Float?String.format("%.2f",n.doubleValue()):Long.toString(n.longValue());}
    private String markLine(String text,int active){if(text==null||text.isBlank())return algorithms.getValue().pseudocode();String[]lines=text.split("\\R");StringBuilder out=new StringBuilder();for(int i=0;i<lines.length;i++)out.append(i==active?"▶  ":"   ").append(lines[i]).append('\n');return out.toString();}
    private void randomize(){AlgorithmDemo demo=algorithms.getValue();if(demo==null)return;Random random=new Random();String hint=demo.inputHint().toLowerCase();if(hint.contains("values")||hint.contains("integers")||hint.contains("priorities")||hint.equals("keys to insert")){List<Integer>v=new ArrayList<>();for(int i=0;i<10;i++)v.add(random.nextInt(91)-20);input.setText(v.toString().replace("[","").replace("]",""));}else input.setText(demo.defaultInput());reset();}
    public static void main(String[]args){launch(args);}
}
