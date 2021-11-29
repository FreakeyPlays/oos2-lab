package acamo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import de.saring.leafletmap.LatLong;
import de.saring.leafletmap.LeafletMapView;
import de.saring.leafletmap.MapConfig;
import de.saring.leafletmap.MapLayer;
import de.saring.leafletmap.Marker;
import de.saring.leafletmap.ScaleControlConfig;
import de.saring.leafletmap.ZoomControlConfig;
import de.saring.leafletmap.events.MapClickEventListener;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Worker;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import jsonstream.PlaneDataServer;
import messer.*;
import observer.*;
import senser.Senser;

public class Acamo extends Application implements Observer<BasicAircraft> {
    // Plane Data variables
    private static double latitude = 48.7433425;
    private static double longitude = 9.3201122;
    private static int dist = 50;
    private static boolean haveConnection = true;
    private ActiveAircrafts activeAircrafts;
    private PlaneDataServer server;

    // JavaFX Table
    private TableView<BasicAircraft> table = new TableView<BasicAircraft>();
    private ObservableList<BasicAircraft> aircraftList = FXCollections.observableArrayList();
    private ArrayList<Label> valueLabel = new ArrayList<Label>();
    private int selectedTableRow = -1;

    // Map
    private LeafletMapView map = new LeafletMapView();
    private Marker homeMarker;
    private HashMap<String, Marker> aircraftHashMap = new HashMap<String, Marker>();
    private CompletableFuture<Worker.State> loadingState;
    private TextField inputLong;
    private TextField inputLat;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        String urlString = "https://opensky-network.org/api/states/all";

        if (haveConnection)
            server = new PlaneDataServer(urlString, latitude, longitude, 50);
        else
            server = new PlaneDataServer(latitude, longitude, 50);

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
                try {
                    if (selectedTableRow >= 0) {
                        table.getSelectionModel().select(selectedTableRow);
                        BasicAircraft bac = table.getSelectionModel().getSelectedItem();
                        ArrayList<Object> values = BasicAircraft.getAttributesValues(bac);
                        for (int i = 0; i < values.size(); i++) {
                            valueLabel.get(i).setText(values.get(i).toString());
                        }
                    }
                } catch (NullPointerException e) {
                }

                for (BasicAircraft bac : activeAircrafts.values()) {
                    int direction = (int) bac.getTrak();
                    LatLong coord = new LatLong(bac.getCoordinate().getLatitude(), bac.getCoordinate().getLongitude());
                    String icao = bac.getIcao();
                    String icon = "plane" + String.format("%02d", direction / 15);
                    if (aircraftHashMap.containsKey(icao)) {
                        Marker plane = aircraftHashMap.get(icao);
                        plane.move(coord);
                        plane.changeIcon(icon);
                    } else {
                        loadingState.whenComplete((state, throwable) -> {
                            Marker plane = new Marker(coord, icao, icon, 0);
                            map.addMarker(plane);
                            aircraftHashMap.put(icao, plane);
                        });
                    }
                }
            }
        });
    }

    private void javafx(Stage primaStage) {

        // Map
        Label mapTitle = createTitle("Map", new Insets(0, 0, 20, 0), 24);
        List<MapLayer> config = new LinkedList<>();
        config.add(MapLayer.OPENSTREETMAP);
        loadingState = map.displayMap(new MapConfig(config, new ZoomControlConfig(),
                new ScaleControlConfig(), new LatLong(latitude, longitude)));

        loadingState.whenComplete((State, Throwable) -> {
            // map.onMapMove(new MapMoveEventListener() { Frage???

            // @Override
            // public void onMapMove(LatLong arg0) {
            // map.panTo(arg0);
            // }

            // });

            map.onMapClick(new MapClickEventListener() {

                @Override
                public void onMapClick(LatLong coord) {
                    inputLat.setText(String.valueOf(coord.getLatitude()));
                    inputLong.setText(String.valueOf(coord.getLongitude()));
                    switchLocation(coord.getLatitude(), coord.getLongitude(), dist);
                }

            });

            homeMarker = new Marker(new LatLong(latitude, longitude), "Home", "Home", 1);
            map.addCustomMarker("Home", "icons/basestationlarge.png");
            map.addMarker(homeMarker);

            for (int i = 0; i <= 24; i++) {
                String n = String.format("%02d", i);
                map.addCustomMarker("plane" + n, "icons/plane" + n + ".png");
            }
        });

        for (BasicAircraft bac : activeAircrafts.values()) {
            int direction = (int) bac.getTrak();
            LatLong coord = new LatLong(bac.getCoordinate().getLatitude(), bac.getCoordinate().getLongitude());
            String icao = bac.getIcao();
            String icon = "plane" + String.format("%02d", direction / 15);
            loadingState.whenComplete((state, throwable) -> {
                Marker plane = new Marker(coord, icao, icon, 0);
                map.addCustomMarker(icon, "icons/" + icon + ".png");
                map.addMarker(plane);
                aircraftHashMap.put(icao, plane);
            });
        }

        map.setMaxWidth(500);
        map.setMaxHeight(400);

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

        // Data Pane
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

        // Input and Button for switching location
        Label latLabel = createTitle("Latitude:", new Insets(5, 0, 5, 0), 14);
        inputLat = new TextField();
        inputLat.setText(String.valueOf(latitude));

        Label lngLabel = createTitle("Longitude:", new Insets(5, 0, 5, 0), 14);
        inputLong = new TextField();
        inputLong.setText(String.valueOf(longitude));

        Button submit = new Button("Submit");
        submit.setOnAction(e -> {
            switchLocation(Double.parseDouble(inputLat.getText()), Double.parseDouble(inputLong.getText()), dist);
        });

        // Container for all Panes
        HBox container = new HBox();
        VBox mapBox = new VBox();
        VBox tableBox = new VBox();
        VBox selectedBox = new VBox();

        mapBox.setPadding(new Insets(0, 10, 0, 0));
        mapBox.getChildren().addAll(mapTitle, map, latLabel, inputLat, lngLabel, inputLong, submit);

        tableBox.setPadding(new Insets(0, 10, 0, 10));
        tableBox.getChildren().addAll(tabelTitle, table);

        selectedBox.setPadding(new Insets(0, 0, 0, 10));
        selectedBox.getChildren().addAll(selectedTitle, selectedAircraftPane);

        container.setPadding(new Insets(20, 20, 20, 20));
        container.getChildren().addAll(mapBox, tableBox, selectedBox);

        // Eventhandler: selected row
        rowSelectedEvent();

        Scene scene = new Scene(container, 1310, 600);
        primaStage.setOnCloseRequest(e -> System.exit(0));
        primaStage.setScene(scene);
        primaStage.setTitle("Acamo");
        primaStage.show();
    }

    public void rowSelectedEvent() {
        table.setOnMouseClicked(new EventHandler<MouseEvent>() {

            public void handle(MouseEvent e) {
                try {

                    selectedTableRow = table.getSelectionModel().getSelectedIndex();

                    BasicAircraft bac = table.getSelectionModel().getSelectedItem();
                    ArrayList<Object> values = BasicAircraft.getAttributesValues(bac); // ERROR
                    for (int i = 0; i < values.size(); i++) {
                        valueLabel.get(i).setText(values.get(i).toString());
                    }
                } catch (NullPointerException ex) {

                }
            }
        });
    }

    public Label createTitle(String s, Insets i, int f) {
        Label label = new Label(s);
        label.setPadding(i);
        label.setFont(new Font(f));

        return label;
    }

    public void switchLocation(double lat, double lng, int dist) {
        map.panTo(new LatLong(lat, lng));
        homeMarker.move(new LatLong(lat, lng));
        activeAircrafts.clear();
        aircraftList.clear();
        for (Marker mark : aircraftHashMap.values()) {
            map.removeMarker(mark);
        }
        server.resetLocation(lat, lng, dist);
    }
}
