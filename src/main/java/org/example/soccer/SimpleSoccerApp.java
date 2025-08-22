package org.example.soccer;

import javafx.application.Application;
import javafx.geometry.*;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.collections.*;
import javafx.util.StringConverter;


public class SimpleSoccerApp extends Application {
    private League league;
    private TableView<Team> leagueTable;
    private TableView<Player> playersTable;
    private ListView<String> resultsListView;
    private ListView<String> eventsListView;
    private ObservableList<Player> allPlayers;
    private boolean allMatchesPlayed = false; // Track if all matches have been played

    @Override
    public void start(Stage primaryStage) {
        league = new League("Premier League");

        primaryStage.setTitle("⚽ Soccer League Manager");
        primaryStage.setMinWidth(850);
        primaryStage.setMinHeight(750);

        // Create main container with gradient background
        VBox root = new VBox(15);
        root.setPadding(new Insets(20));
        root.setStyle("-fx-background: linear-gradient(to bottom, #1e3c72, #2a5298);" +
                "-fx-background-color: linear-gradient(to bottom, #1e3c72, #2a5298);");

        // Enhanced title with shadow effect
        Label title = new Label("⚽ SOCCER LEAGUE MANAGER");
        title.setStyle("-fx-font-size: 32px; " +
                "-fx-font-weight: bold; " +
                "-fx-text-fill: white; " +
                "-fx-alignment: center; " +
                "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.8), 5, 0, 2, 2);");
        title.setMaxWidth(Double.MAX_VALUE);

        // Styled container for team creation
        VBox teamSection = createStyledSection();
        Label teamSectionTitle = createSectionTitle("🏆 Team Management");

        // Team creation controls
        HBox teamCreation = new HBox(10);
        teamCreation.setAlignment(Pos.CENTER);
        teamCreation.setPadding(new Insets(10));

        TextField teamNameField = new TextField();
        teamNameField.setPromptText("Enter team name...");
        teamNameField.setPrefWidth(200);
        teamNameField.setStyle(getTextFieldStyle());

        Button addTeamBtn = createStyledButton("Add Team", "#4CAF50");
        Button loadSampleBtn = createStyledButton("Load Sample Teams", "#4CAF50");

        addTeamBtn.setOnAction(e -> addTeam(teamNameField));
        loadSampleBtn.setOnAction(e -> loadSampleData());

        Label teamLabel = new Label("Team Name:");
        teamLabel.setStyle("-fx-text-fill: white; -fx-font-weight: bold;");

        teamCreation.getChildren().addAll(teamLabel, teamNameField, addTeamBtn, loadSampleBtn);

        // Team removal section (now includes both remove and clear all buttons)
        HBox teamManagement = new HBox(10);
        teamManagement.setAlignment(Pos.CENTER);
        teamManagement.setPadding(new Insets(10));

        ComboBox<Team> teamSelector = new ComboBox<>();
        teamSelector.setItems(league.getTeams());
        teamSelector.setStyle(getComboBoxStyle());
        teamSelector.setConverter(new StringConverter<Team>() {
            @Override
            public String toString(Team team) {
                return team != null ? team.getName() : "";
            }

            @Override
            public Team fromString(String string) {
                return league.getTeams().stream()
                        .filter(t -> t.getName().equals(string))
                        .findFirst().orElse(null);
            }
        });
        teamSelector.setPromptText("Select team to remove...");

        Button removeTeamBtn = createStyledButton("Remove Selected", "#F44336");
        Button clearAllBtn = createStyledButton("Clear All", "#FF5722");

        removeTeamBtn.setOnAction(e -> removeSelectedTeam(teamSelector));
        clearAllBtn.setOnAction(e -> clearAllTeams());

        Label removeLabel = new Label("Remove Team:");
        removeLabel.setStyle("-fx-text-fill: white; -fx-font-weight: bold;");

        teamManagement.getChildren().addAll(removeLabel, teamSelector, removeTeamBtn, clearAllBtn);

        teamSection.getChildren().addAll(teamSectionTitle, teamCreation, teamManagement);

        // Match controls section
        VBox controlsSection = createStyledSection();
        Label controlsTitle = createSectionTitle("⚽ Match Controls");

        HBox controls = new HBox(15);
        controls.setAlignment(Pos.CENTER);
        controls.setPadding(new Insets(15));

        Button nextMatchBtn = createStyledButton("Play Next Match", "linear-gradient(to bottom, #FF9800, #FFC107)");
        Button allMatchesBtn = createStyledButton("Play All Matches", "linear-gradient(to bottom, #FF9800, #FFC107)");
        Button resetBtn = createStyledButton("Reset Season", "linear-gradient(to bottom, #FF9800, #FFC107)");

        nextMatchBtn.setOnAction(e -> playNextMatch());
        allMatchesBtn.setOnAction(e -> playAllMatches());
        resetBtn.setOnAction(e -> resetSeason());

        controls.getChildren().addAll(nextMatchBtn, allMatchesBtn, resetBtn);
        controlsSection.getChildren().addAll(controlsTitle, controls);

        // Enhanced TabPane
        TabPane tabPane = new TabPane();
        tabPane.setStyle("-fx-background-color: rgba(255,255,255,0.95); " +
                "-fx-background-radius: 10; " +
                "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.3), 8, 0, 0, 2);");
        tabPane.setPrefHeight(350);
        tabPane.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);

        // League Table Tab
        Tab leagueTab = new Tab("🏆 League Table");
        leagueTab.setClosable(false);

        leagueTable = new TableView<>();
        leagueTable.setItems(league.getTeams());
        leagueTable.setStyle(getTableStyle());

        setupLeagueTableColumns();
        leagueTab.setContent(leagueTable);

        // Players Tab
        Tab playersTab = new Tab("👥 Top Scorers");
        playersTab.setClosable(false);

        playersTable = new TableView<>();
        playersTable.setStyle(getTableStyle());
        allPlayers = FXCollections.observableArrayList();
        playersTable.setItems(allPlayers);

        setupPlayersTableColumns();
        playersTab.setContent(playersTable);

        // Events Tab
        Tab eventsTab = new Tab("📋 Match Events");
        eventsTab.setClosable(false);

        eventsListView = new ListView<>();
        eventsListView.setStyle(getListViewStyle());
        eventsTab.setContent(eventsListView);

        tabPane.getTabs().addAll(leagueTab, playersTab, eventsTab);

        // Apply additional styling programmatically as fallback
        tabPane.getTabs().forEach(tab -> {
            tab.setStyle(
                    "-fx-font-size: 16px;" +
                            "-fx-font-weight: bold;" +
                            "-fx-text-base-color: gray;" //+   // text color
                    // "-fx-background-color: lightgray;" // optional background
            );
        });

        // Match Results section
        VBox resultsSection = createStyledSection();
        Label resultsLabel = createSectionTitle("📊 Match Results");

        resultsListView = new ListView<>();
        resultsListView.setPrefHeight(150);
        resultsListView.setStyle(getListViewStyle());

        resultsSection.getChildren().addAll(resultsLabel, resultsListView);

        root.getChildren().addAll(title, teamSection, controlsSection, tabPane, resultsSection);

        Scene scene = new Scene(root, 900, 810);

        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private VBox createStyledSection() {
        VBox section = new VBox(10);
        section.setStyle("-fx-background-color: rgba(255,255,255,0.1); " +
                "-fx-background-radius: 15; " +
                "-fx-padding: 15; " +
                "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.3), 5, 0, 0, 2);");
        return section;
    }

    private Label createSectionTitle(String text) {
        Label title = new Label(text);
        title.setStyle("-fx-font-size: 18px; " +
                "-fx-font-weight: bold; " +
                "-fx-text-fill: white; " +
                "-fx-alignment: center;");
        title.setMaxWidth(Double.MAX_VALUE);
        return title;
    }

    private Button createStyledButton(String text, String color) {
        Button button = new Button(text);
        button.setStyle("-fx-background-color: " + color + "; " +
                "-fx-text-fill: white; " +
                "-fx-font-weight: bold; " +
                "-fx-font-size: 12px; " +
                "-fx-background-radius: 25; " +
                "-fx-padding: 10 20 10 20; " +
                "-fx-cursor: hand; " +
                "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.3), 3, 0, 0, 1);");

        button.setOnMouseEntered(e -> {
            button.setStyle(button.getStyle() + "-fx-scale-x: 1.05; -fx-scale-y: 1.05;");
        });

        button.setOnMouseExited(e -> {
            button.setStyle(button.getStyle().replace("-fx-scale-x: 1.05; -fx-scale-y: 1.05;", ""));
        });

        return button;
    }

    private String getTextFieldStyle() {
        return "-fx-background-color: rgba(255,255,255,0.9); " +
                "-fx-background-radius: 20; " +
                "-fx-padding: 8 15 8 15; " +
                "-fx-font-size: 12px; " +
                "-fx-border-radius: 20; " +
                "-fx-border-color: rgba(255,255,255,0.3); " +
                "-fx-border-width: 2;";
    }

    private String getComboBoxStyle() {
        return "-fx-background-color: rgba(255,255,255,0.9); " +
                "-fx-background-radius: 20; " +
                "-fx-padding: 5 15 5 15;";
    }

    private String getTableStyle() {
        return "-fx-background-color: rgba(255,255,255,0.95); " +
                "-fx-background-radius: 10; " +
                "-fx-border-radius: 10; " +
                "-fx-border-color: rgba(0,0,0,0.1); " +
                "-fx-border-width: 1;";
    }

    private String getListViewStyle() {
        return "-fx-background-color: rgba(255,255,255,0.95); " +
                "-fx-background-radius: 10; " +
                "-fx-border-radius: 10; " +
                "-fx-border-color: rgba(0,0,0,0.1); " +
                "-fx-border-width: 1; " +
                "-fx-font-family: 'Consolas', monospace;";
    }

    private void setupLeagueTableColumns() {
        TableColumn<Team, String> nameCol = new TableColumn<>("Team");
        nameCol.setCellValueFactory(new PropertyValueFactory<>("name"));
        nameCol.setPrefWidth(150);
        nameCol.setStyle("-fx-font-weight: bold;");

        TableColumn<Team, Integer> pointsCol = new TableColumn<>("Points");
        pointsCol.setCellValueFactory(new PropertyValueFactory<>("points"));
        pointsCol.setPrefWidth(80);
        pointsCol.setStyle("-fx-alignment: CENTER; -fx-font-weight: bold; -fx-text-fill: #4CAF50;");

        TableColumn<Team, Integer> winsCol = new TableColumn<>("Wins");
        winsCol.setCellValueFactory(new PropertyValueFactory<>("wins"));
        winsCol.setPrefWidth(60);
        winsCol.setStyle("-fx-alignment: CENTER;");

        TableColumn<Team, Integer> drawsCol = new TableColumn<>("Draws");
        drawsCol.setCellValueFactory(new PropertyValueFactory<>("draws"));
        drawsCol.setPrefWidth(60);
        drawsCol.setStyle("-fx-alignment: CENTER;");

        TableColumn<Team, Integer> lossesCol = new TableColumn<>("Losses");
        lossesCol.setCellValueFactory(new PropertyValueFactory<>("losses"));
        lossesCol.setPrefWidth(60);
        lossesCol.setStyle("-fx-alignment: CENTER;");

        TableColumn<Team, Integer> goalDiffCol = new TableColumn<>("Goal Diff");
        goalDiffCol.setCellValueFactory(cellData ->
                new javafx.beans.property.SimpleIntegerProperty(cellData.getValue().getGoalDifference()).asObject());
        goalDiffCol.setPrefWidth(80);
        goalDiffCol.setStyle("-fx-alignment: CENTER; -fx-font-weight: bold;");

        leagueTable.getColumns().addAll(nameCol, pointsCol, winsCol, drawsCol, lossesCol, goalDiffCol);
    }

    private void setupPlayersTableColumns() {
        TableColumn<Player, String> playerNameCol = new TableColumn<>("Player");
        playerNameCol.setCellValueFactory(new PropertyValueFactory<>("name"));
        playerNameCol.setPrefWidth(150);
        playerNameCol.setStyle("-fx-font-weight: bold;");

        TableColumn<Player, String> playerTeamCol = new TableColumn<>("Team");
        playerTeamCol.setCellValueFactory(cellData -> {
            Player player = cellData.getValue();
            for (Team team : league.getTeams()) {
                if (team.getPlayers().contains(player)) {
                    return new javafx.beans.property.SimpleStringProperty(team.getName());
                }
            }
            return new javafx.beans.property.SimpleStringProperty("Unknown");
        });
        playerTeamCol.setPrefWidth(120);

        TableColumn<Player, String> positionCol = new TableColumn<>("Position");
        positionCol.setCellValueFactory(new PropertyValueFactory<>("position"));
        positionCol.setPrefWidth(100);

        TableColumn<Player, Integer> goalsCol = new TableColumn<>("Goals");
        goalsCol.setCellValueFactory(new PropertyValueFactory<>("goalsScored"));
        goalsCol.setPrefWidth(80);
        goalsCol.setStyle("-fx-alignment: CENTER; -fx-font-weight: bold; -fx-text-fill: #FF5722;");

        TableColumn<Player, Integer> gamesCol = new TableColumn<>("Games");
        gamesCol.setCellValueFactory(new PropertyValueFactory<>("gamesPlayed"));
        gamesCol.setPrefWidth(80);
        gamesCol.setStyle("-fx-alignment: CENTER;");

        playersTable.getColumns().addAll(playerNameCol, playerTeamCol, positionCol, goalsCol, gamesCol);
    }

    // Rest of the methods remain the same but with enhanced alert styling
    private void clearAllTeams() {
        Alert confirm = createStyledAlert(Alert.AlertType.CONFIRMATION, "Clear All Teams", "Remove All Teams",
                "This will remove all teams and reset the league. Are you sure?");

        confirm.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                league.getTeams().clear();
                league.getGames().clear();
                league.getAllEvents().clear();
                resultsListView.getItems().clear();
                eventsListView.getItems().clear();
                allMatchesPlayed = false; // Reset the flag
                updatePlayerTable();
                showAlert("Teams Cleared", "All teams have been removed from the league.");
            }
        });
    }

    private void removeSelectedTeam(ComboBox<Team> teamSelector) {
        Team selectedTeam = teamSelector.getValue();
        if (selectedTeam != null) {
            Alert confirm = createStyledAlert(Alert.AlertType.CONFIRMATION, "Remove Team",
                    "Remove " + selectedTeam.getName(), "Are you sure you want to remove this team?");

            confirm.showAndWait().ifPresent(response -> {
                if (response == ButtonType.OK) {
                    league.removeTeam(selectedTeam);
                    teamSelector.setValue(null);

                    if (league.getTeams().size() >= 2) {
                        league.generateMatches();
                    } else {
                        league.getGames().clear();
                    }

                    resultsListView.getItems().clear();
                    allMatchesPlayed = false; // Reset the flag when team structure changes
                    updatePlayerTable();
                    showAlert("Team Removed", selectedTeam.getName() + " has been removed from the league.");
                }
            });
        } else {
            showAlert("No Selection", "Please select a team to remove.");
        }
    }

    private void addTeam(TextField teamNameField) {
        String teamName = teamNameField.getText().trim();
        if (!teamName.isEmpty()) {
            Team newTeam = new Team(teamName);
            newTeam.addPlayer(new Player("Player 1", "Forward"));
            newTeam.addPlayer(new Player("Player 2", "Midfielder"));
            newTeam.addPlayer(new Player("Player 3", "Defender"));

            league.addTeam(newTeam);
            teamNameField.clear();

            if (league.getTeams().size() >= 2) {
                league.generateMatches();
            }

            allMatchesPlayed = false; // Reset the flag when new team is added
            updatePlayerTable();
            showAlert("Team Added", teamName + " has been added to the league!");
        } else {
            showAlert("Error", "Please enter a team name.");
        }
    }

    private void loadSampleData() {
        String[] teamNames = {"Arsenal", "Chelsea", "Liverpool", "Man City", "Man United", "Tottenham"};

        for (String name : teamNames) {
            Team team = new Team(name);
            team.addPlayer(new Player("Player 1", "Forward"));
            team.addPlayer(new Player("Player 2", "Midfielder"));
            team.addPlayer(new Player("Player 3", "Defender"));
            league.addTeam(team);
        }

        league.generateMatches();
        allMatchesPlayed = false; // Reset the flag when loading new data
        updatePlayerTable();
    }

    private void playNextMatch() {
        Game game = league.simulateNextMatch();
        if (game != null) {
            resultsListView.getItems().add(game.getResult());
            updatePlayerTable();
            updateEventsDisplay();
            if (league.isSeasonComplete()) {
                allMatchesPlayed = true; // Set flag when season is complete
                showAlert("Season Complete!", "Champion: " + league.getTeams().get(0).getName());
            }
        } else {
            showAlert("No Matches", "All matches have been played!");
        }
    }

    private void playAllMatches() {
        // Check if all matches have already been played
        if (allMatchesPlayed && league.isSeasonComplete()) {
            String champion = "";
            if (!league.getTeams().isEmpty()) {
                champion = league.getTeams().get(0).getName();
            }
            showAlert("Season Already Complete!",
                    "All matches have already been played!\n\nThe champion is: " + champion);
            return;
        }

        // Play all matches
        league.simulateAllMatches();
        resultsListView.getItems().clear();
        for (Game game : league.getGames()) {
            resultsListView.getItems().add(game.getResult());
        }
        updatePlayerTable();
        updateEventsDisplay();
        allMatchesPlayed = true; // Set the flag after playing all matches

        String champion = "";
        if (!league.getTeams().isEmpty()) {
            champion = league.getTeams().get(0).getName();
        }
        showAlert("Season Complete!", "Champion: " + champion);
    }

    private void resetSeason() {
        // First call the league's reset method
        league.resetSeason();

        // Force reset all team statistics to ensure goal difference becomes 0
        for (Team team : league.getTeams()) {
            // Reset all team statistics using the correct property methods
            team.pointsProperty().set(0);
            team.winsProperty().set(0);
            team.drawsProperty().set(0);
            team.lossesProperty().set(0);
            team.goalsForProperty().set(0);          // This resets goals scored
            team.goalsAgainstProperty().set(0);      // This resets goals conceded
            team.gamesPlayedProperty().set(0);


        }

        // Regenerate matches and clear displays
        league.generateMatches();
        resultsListView.getItems().clear();
        eventsListView.getItems().clear();
        allMatchesPlayed = false; // Reset the flag when season is reset
        updatePlayerTable();

        // Force refresh all UI components to show updated values
        leagueTable.refresh();
        playersTable.refresh();

        // Sort the table to ensure proper display
        leagueTable.sort();
    }

    private void updateEventsDisplay() {
        eventsListView.getItems().clear();

        for (Game game : league.getGames()) {
            if (game.isFinished() && !game.getEvents().isEmpty()) {
                eventsListView.getItems().add("");
                eventsListView.getItems().add("MATCH: " + game.getResult());
                eventsListView.getItems().add("═══════════════════════════════");

                for (GameEvent event : game.getEvents()) {
                    String eventText = "";
                    if (event instanceof Goal) {
                        eventText = String.format("  %d' ⚽ GOAL! %s (%s)",
                                event.getMinute(),
                                event.getPlayer().getName(),
                                event.getTeam().getName());
                    } else if (event instanceof YellowCard) {
                        eventText = String.format("  %d' 🟨 Yellow Card: %s (%s)",
                                event.getMinute(),
                                event.getPlayer().getName(),
                                event.getTeam().getName());
                    } else if (event instanceof Kickoff) {
                        eventText = String.format("  %d' ⚽ Kickoff: %s",
                                event.getMinute(),
                                event.getTeam().getName());
                    } else if (event instanceof Possession) {
                        eventText = String.format("  %d' ⚡ Possession: %s (%s)",
                                event.getMinute(),
                                event.getPlayer().getName(),
                                event.getTeam().getName());
                    }
                    eventsListView.getItems().add(eventText);
                }
                eventsListView.getItems().add("");
            }
        }

        if (eventsListView.getItems().isEmpty()) {
            eventsListView.getItems().add("No matches played yet. Click 'Play Next Match' to start!");
        }
    }

    private void updatePlayerTable() {
        allPlayers.clear();

        for (Team team : league.getTeams()) {
            allPlayers.addAll(team.getPlayers());
        }

        allPlayers.sort((p1, p2) -> Integer.compare(p2.getGoalsScored(), p1.getGoalsScored()));
    }

    private Alert createStyledAlert(Alert.AlertType type, String title, String header, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);

        // Style the alert dialog with white background and bigger font
        DialogPane dialogPane = alert.getDialogPane();
        dialogPane.setStyle("-fx-background-color: white; " +
                "-fx-text-fill: black; " +
                "-fx-font-size: 16px; " +
                "-fx-font-weight: bold; " +
                "-fx-border-color: #1e3c72; " +
                "-fx-border-width: 3; " +
                "-fx-border-radius: 10; " +
                "-fx-background-radius: 10;");

        // Style the header
        Label headerLabel = (Label) dialogPane.lookup(".header-panel .label");
        if (headerLabel != null) {
            headerLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #1e3c72;");
        }

        // Style the content
        Label contentLabel = (Label) dialogPane.lookup(".content");
        if (contentLabel != null) {
            contentLabel.setStyle("-fx-font-size: 16px; -fx-text-fill: black; -fx-wrap-text: true;");
        }

        return alert;
    }

    private void showAlert(String title, String message) {
        Alert alert = createStyledAlert(Alert.AlertType.INFORMATION, title, null, message);
        alert.showAndWait();
    }

    public static void main(String[] args) {
        launch(args);
    }
}