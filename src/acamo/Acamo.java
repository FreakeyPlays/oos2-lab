package acamo;

import java.util.ArrayList;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import jsonstream.PlaneDataServer;
import messer.BasicAircraft;
import messer.Messer;
import observer.Observable;
import observer.Observer;
import senser.Senser;

public class Acamo extends Application implements Observer<BasicAircraft> {
    // Plane Data variables
    private static double latitude = 48.7433425;
    private static double longitude = 9.3201122;
    private static boolean haveConnection = true;
    private ActiveAircrafts activeAircrafts;

    // JavaFX Table
    private TableView<BasicAircraft> table = new TableView<BasicAircraft>();
    private ObservableList<BasicAircraft> aircraftList = FXCollections.observableArrayList();
    ArrayList<Label> valueLabel = new ArrayList<Label>();
    private int selectedTableRow = -1;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        String urlString = "https://opensky-network.org/api/states/all";
        PlaneDataServer server;

        if (haveConnection)
            server = new PlaneDataServer(urlString, latitude, longitude, 150);
        else
            server = new PlaneDataServer(latitude, longitude, 100);

        Senser senser = new Senser(server);
        new Thread(server).start();
        new Thread(senser).start();

        Messer messer = new Messer();
        senser.addObserver(messer);
        new Thread(messer).start();

        activeAircrafts = new ActiveAircrafts();
        messer.addObserver(activeAircrafts);
        messer.addObserver(this);

        javafx(primaryStage);
    }

    @Override
    public void update(Observable<BasicAircraft> observable, BasicAircraft newValue) {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                aircraftList.clear();
                aircraftList.addAll(activeAircrafts.values());
                if (selectedTableRow >= 0) {
                    table.getSelectionModel().select(selectedTableRow);
                    BasicAircraft bac = table.getSelectionModel().getSelectedItem();
                    ArrayList<Object> values = BasicAircraft.getAttributesValues(bac);
                    for (int i = 0; i < values.size(); i++) {
                        valueLabel.get(i).setText(values.get(i).toString());
                    }
                }
            }
        });
    }

    private void javafx(Stage primaStage) {

        // Table
        Label tabelTitle = createTitle("Active Aircrafts", new Insets(0, 0, 20, 0), 24);
        ArrayList<String> tableAtributes = BasicAircraft.getAttributesNames();
        for (String attribute : tableAtributes) {
            TableColumn<BasicAircraft, String> tableHeader = new TableColumn<BasicAircraft, String>(attribute);
            tableHeader.setCellValueFactory(new PropertyValueFactory<BasicAircraft, String>(attribute));
            table.getColumns().add(tableHeader);
        }
        table.setItems(aircraftList);
        table.setEditable(false);

        GridPane selectedAircraftPane = new GridPane();
        selectedAircraftPane.setHgap(20);
        Label selectedTitle = createTitle("Selected Aircraft", new Insets(0, 0, 20, 0), 24);

        // crating Labels and saving in Array
        String[] labelData = { "icao:", "operator:", "posTime:", "corordinate:", "speed:", "trak:" };
        ArrayList<Label> nameLabel = new ArrayList<Label>();
        for (int i = 0; i < labelData.length; i++) {
            nameLabel.add(new Label(labelData[i]));
            selectedAircraftPane.add(nameLabel.get(i), 0, i);

            valueLabel.add(new Label(""));
            selectedAircraftPane.add(valueLabel.get(i), 1, i);
        }

        // Container for all Panes
        HBox container = new HBox();
        VBox tableBox = new VBox();
        VBox selectedBox = new VBox();

        tableBox.setPadding(new Insets(0, 10, 0, 0));
        tableBox.getChildren().addAll(tabelTitle, table);

        selectedBox.setPadding(new Insets(0, 0, 0, 10));
        selectedBox.getChildren().addAll(selectedTitle, selectedAircraftPane);

        container.setPadding(new Insets(20, 20, 20, 20));
        container.getChildren().addAll(tableBox, selectedBox);

        // Eventhandler: selected row
        table.setOnMouseClicked(new EventHandler<MouseEvent>() {
            public void handle(MouseEvent e) {
                selectedTableRow = table.getSelectionModel().getSelectedIndex();

                BasicAircraft bac = table.getSelectionModel().getSelectedItem();
                ArrayList<Object> values = BasicAircraft.getAttributesValues(bac); // ERROR
                for (int i = 0; i < values.size(); i++) {
                    valueLabel.get(i).setText(values.get(i).toString());
                }
            }
        });

        Scene scene = new Scene(container, 800, 500);
        primaStage.setOnCloseRequest(e -> System.exit(0));
        primaStage.setScene(scene);
        primaStage.setTitle("Acamo");
        primaStage.show();
    }

    public Label createTitle(String s, Insets i, int f) {
        Label label = new Label(s);
        label.setPadding(i);
        label.setFont(new Font(f));

        return label;
    }
}
