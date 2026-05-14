package GUI;

import model.*;
import DataBase.DB; 
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.collections.FXCollections;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.sql.*;
import java.util.concurrent.atomic.AtomicReference;
import javafx.application.Platform;

public class GUI extends Application {

    Train train = new Train("Express");
    TableView<Ticket> tableView = new TableView<>();
    Label totalTicketsLabel = new Label("Tickets Count: 0");

    private void applyCSS(Scene scene) {
        try {
            String css = getClass().getResource("style.css").toExternalForm();
            scene.getStylesheets().add(css);
        } catch (Exception e) {
            System.out.println("⚠️ CSS Load Warning: style.css not found.");
        }
    }

    // ميثود لإظهار تنبيهات مخصصة للمستخدم
    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    @Override
    public void start(Stage primaryStage) {
        
        // 1. Main Menu
        Label mainTitle = new Label("Train Booking System");
        mainTitle.getStyleClass().add("header-label");

        Button btnNormal = new Button("Book Normal Ticket");
        Button btnFirstClass = new Button("Book First Class");
        Button btnSearch = new Button("Search Ticket");
        Button btnShowAll = new Button("Show All Tickets");
        Button btnChat = new Button("💬 Open Chat");
        Button btnExit = new Button("Exit");

        VBox mainLayout = new VBox(15, mainTitle, btnNormal, btnFirstClass, btnSearch, btnShowAll, btnChat, btnExit);
        mainLayout.setAlignment(Pos.CENTER);
        mainLayout.setPadding(new Insets(20));
        mainLayout.setId("mainPane");

        for (Button b : new Button[]{btnNormal, btnFirstClass, btnSearch, btnShowAll, btnExit}) {
            b.setMaxWidth(250);
            b.getStyleClass().add(b == btnExit ? "button-danger" : "button-primary");
        }

        Scene mainScene = new Scene(mainLayout, 400, 500);
        applyCSS(mainScene);
        primaryStage.setScene(mainScene);
        primaryStage.setTitle("Main Menu");
        primaryStage.show();

        // 2. Search Logic with Custom Handling
        Stage searchStage = new Stage();
        TextField searchIdField = new TextField();
        searchIdField.setPromptText("Enter Passenger ID");
        Button btnSearchTicket = new Button("Search Now");
        Label lblSearchMsg = new Label();
        Button btnSearchBack = new Button("← Back");

        VBox searchPane = new VBox(15, new Label("Search by Passenger ID"), searchIdField, btnSearchTicket, lblSearchMsg, btnSearchBack);
        searchPane.setAlignment(Pos.CENTER);
        searchPane.setPadding(new Insets(20));
        searchPane.setId("mainPane");
        searchStage.setScene(new Scene(searchPane, 350, 350));
        applyCSS(searchStage.getScene());

        btnChat.setMaxWidth(250);
        btnChat.getStyleClass().add("button-primary");

        btnSearchTicket.setOnAction(e -> {
            try {
                String input = searchIdField.getText().trim();
                if(input.isEmpty()) throw new Exception("Please enter an ID to search.");
                
                int id = Integer.parseInt(input);
                loadTicketsFromDB(); 
                Ticket found = train.searchTicketById(id);
                
                if (found != null) {
                    lblSearchMsg.setText("✅ Found!\nPrice: " + found.calculatePrice() + "\nDate: " + found.getDate());
                    lblSearchMsg.setStyle("-fx-text-fill: green;");
                } else {
                    lblSearchMsg.setText("❌ ID: " + id + " Not Found");
                    lblSearchMsg.setStyle("-fx-text-fill: red;");
                }
            } catch (NumberFormatException ex) {
                lblSearchMsg.setText("⚠️ Error: ID must be numeric!");
            } catch (Exception ex) {
                lblSearchMsg.setText("⚠️ " + ex.getMessage());
            }
        });

        // 3. Table & Delete Logic
        TableColumn<Ticket, Integer> colId = new TableColumn<>("ID");
        colId.setCellValueFactory(new PropertyValueFactory<>("ticketId"));
        TableColumn<Ticket, Double> colPrice = new TableColumn<>("Price");
        colPrice.setCellValueFactory(new PropertyValueFactory<>("price"));
        TableColumn<Ticket, String> colDate = new TableColumn<>("Date");
        colDate.setCellValueFactory(new PropertyValueFactory<>("date"));

        tableView.getColumns().setAll(colId, colPrice, colDate);

        Button btnDelete = new Button("Delete Selected");
        btnDelete.getStyleClass().add("button-danger");
        Button btnTableBack = new Button("← Back");

        VBox tableLayout = new VBox(15, totalTicketsLabel, tableView, btnDelete, btnTableBack);
        tableLayout.setPadding(new Insets(20)); 
        tableLayout.setAlignment(Pos.CENTER); 
        tableLayout.setId("mainPane");
        
        Stage tableStage = new Stage();
        tableStage.setScene(new Scene(tableLayout, 500, 600));
        applyCSS(tableStage.getScene());

        btnShowAll.setOnAction(e -> {
            loadTicketsFromDB();
            tableView.setItems(FXCollections.observableArrayList(train.getTickets()));
            totalTicketsLabel.setText("Total Tickets: " + train.getTickets().size());
            tableStage.show();
        });
        btnChat.setOnAction(e -> {
    ChoiceDialog<String> dialog = new ChoiceDialog<>("Admin (Server)", "Admin (Server)", "Student (Client)");
            dialog.setTitle("Open Chat");
            dialog.setHeaderText("Select your role");
            dialog.setContentText("Role:");
            dialog.showAndWait().ifPresent(choice -> openChatWindow(choice.startsWith("Admin")));
        });

        btnDelete.setOnAction(e -> {
            Ticket selected = tableView.getSelectionModel().getSelectedItem();
            if (selected == null) {
                showAlert(Alert.AlertType.WARNING, "Selection Error", "Please select a row to delete!");
            } else {
                deleteTicketFromDB(selected);
            }
        });

        // Navigation Actions
        btnSearch.setOnAction(e -> {
            lblSearchMsg.setText("");
            searchIdField.clear();
            searchStage.show(); 
        });
        btnNormal.setOnAction(e -> openBookingWindow("Normal Ticket", false));
        btnFirstClass.setOnAction(e -> openBookingWindow("First Class Ticket", true));
        btnExit.setOnAction(e -> primaryStage.close());
        btnTableBack.setOnAction(e -> tableStage.close());
        btnSearchBack.setOnAction(e -> searchStage.close());
    }

    private void openBookingWindow(String title, boolean isFirst) {
        Stage s = new Stage();
        GridPane grid = new GridPane();
        grid.setPadding(new Insets(20));
        grid.setVgap(10);
        grid.setHgap(10);
        grid.setAlignment(Pos.CENTER);
        grid.setId("mainPane");

        TextField nameF = new TextField(),
                ageF = new TextField(),
                idF = new TextField(), 
                priceF = new TextField(),
                dateF = new TextField();
        dateF.setPromptText("YYYY/MM/DD"); 

        Button confirm = new Button("Confirm Booking");
        Label msg = new Label();

        grid.add(new Label(title), 0, 0, 2, 1);
        grid.add(new Label("Name:"), 0, 1); grid.add(nameF, 1, 1);
        grid.add(new Label("Age:"), 0, 2); grid.add(ageF, 1, 2);
        grid.add(new Label("ID:"), 0, 3); grid.add(idF, 1, 3);
        grid.add(new Label("Base Price:"), 0, 4); grid.add(priceF, 1, 4);
        grid.add(new Label("Date:"), 0, 5); grid.add(dateF, 1, 5);
        grid.add(confirm, 0, 6, 2, 1); grid.add(msg, 0, 7, 2, 1);

        confirm.setOnAction(e -> {
            try (Connection conn = DB.DBConnection()) {
                // 1. فحص الحقول الفارغة
                if(nameF.getText().trim().isEmpty()) throw new Exception("Please enter the Name.");
                if(ageF.getText().trim().isEmpty()) throw new Exception("Please enter the Age.");
                if(idF.getText().trim().isEmpty()) throw new Exception("Please enter the Passenger ID.");
                if(priceF.getText().trim().isEmpty()) throw new Exception("Please enter the Base Price.");
                if(dateF.getText().trim().isEmpty()) throw new Exception("Please enter the Date.");

                // 2. فحص صحة الأرقام (مع رسالة لكل نوع)
                int age, id; double price;
                try { age = Integer.parseInt(ageF.getText().trim()); } 
                catch (NumberFormatException ex) { throw new Exception("Invalid Age! Please enter a number."); }

                try { id = Integer.parseInt(idF.getText().trim()); } 
                catch (NumberFormatException ex) { throw new Exception("Invalid ID! Please enter a number."); }

                try { price = Double.parseDouble(priceF.getText().trim()); } 
                catch (NumberFormatException ex) { throw new Exception("Invalid Price! Use numbers (e.g. 100.0)."); }

                // 3. فحص صيغة التاريخ (Regex)
                String dateStr = dateF.getText().trim();
                if (!dateStr.matches("\\d{4}/\\d{2}/\\d{2}")) {
                    throw new Exception("Date must be in format YYYY/MM/DD (e.g. 2025/12/30)");
                }

                // 4. تنفيذ العملية
                Ticket t = isFirst ? new FirstClassTicket(id, price, dateStr) : new NormalTicket(id, price, dateStr);

                String sql = "INSERT INTO BOOKING_SYSTEM (NAME, AGE, PASSENGER_ID, BASE_PRICE, BOOKING_DATE, TICKET_TYPE) VALUES (?, ?, ?, ?, TO_DATE(?, 'YYYY/MM/DD'), ?)";
                PreparedStatement ps = conn.prepareStatement(sql);
                ps.setString(1, nameF.getText().trim());
                ps.setInt(2, age);
                ps.setInt(3, id);
                ps.setDouble(4, t.calculatePrice());
                ps.setString(5, dateStr);
                ps.setString(6, isFirst ? "FirstClass" : "Normal");
                
                ps.executeUpdate();
                msg.setText("✅ Booked Successfully!");
                msg.setStyle("-fx-text-fill: green;");
                showAlert(Alert.AlertType.INFORMATION, "Success", "Ticket for ID " + id + " has been saved!");
                
            } catch (SQLException ex) {
                if (ex.getErrorCode() == 1) { // Unique constraint violation
                    showAlert(Alert.AlertType.ERROR, "DB Error", "Passenger ID already exists in the system!");
                } else {
                    showAlert(Alert.AlertType.ERROR, "Database Error", ex.getMessage());
                }
            } catch (Exception ex) {
                msg.setText("❌ " + ex.getMessage());
                msg.setStyle("-fx-text-fill: red;");
                showAlert(Alert.AlertType.WARNING, "Input Validation", ex.getMessage());
            }
        });

        s.setScene(new Scene(grid, 400, 500));
        applyCSS(s.getScene());
        s.show();
    }

    private void loadTicketsFromDB() {
        train.getTickets().clear();
        try (Connection conn = DB.DBConnection()) {
            if (conn == null) throw new SQLException("Database connection failed!");
            String query = "SELECT PASSENGER_ID, BASE_PRICE, TO_CHAR(BOOKING_DATE, 'YYYY/MM/DD') AS \"D_DATE\" FROM BOOKING_SYSTEM";
            ResultSet rs = conn.createStatement().executeQuery(query);
            while (rs.next()) {
                train.addTicket(new NormalTicket(rs.getInt("PASSENGER_ID"), rs.getDouble("BASE_PRICE"), rs.getString("D_DATE")));
            }
        } catch (SQLException ex) {
            showAlert(Alert.AlertType.ERROR, "Load Error", ex.getMessage());
        }
    }

    private void deleteTicketFromDB(Ticket t) {
        try (Connection conn = DB.DBConnection()) {
            String sql = "DELETE FROM BOOKING_SYSTEM WHERE PASSENGER_ID = ?";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, t.getTicketId());
            if (ps.executeUpdate() > 0) {
                train.getTickets().remove(t);
                tableView.setItems(FXCollections.observableArrayList(train.getTickets()));
                totalTicketsLabel.setText("Total Tickets: " + train.getTickets().size());
                showAlert(Alert.AlertType.INFORMATION, "Deleted", "Ticket ID " + t.getTicketId() + " removed successfully.");
            }
        } catch (SQLException ex) {
            showAlert(Alert.AlertType.ERROR, "Delete Failed", ex.getMessage());
        }
    }

    //chatapp
    private void openChatWindow(boolean isAdmin) {
    String role = isAdmin ? "Admin" : "Student";
    String otherRole = isAdmin ? "Student" : "Admin";

    Stage chatStage = new Stage();
    chatStage.setTitle("Chat — " + role);

    TextArea chatArea = new TextArea();
    chatArea.setEditable(false);
    chatArea.setWrapText(true);
    VBox.setVgrow(chatArea, Priority.ALWAYS);

    TextField msgField = new TextField();
    msgField.setPromptText("Type a message...");
    Button sendBtn = new Button("Send ➤");
    sendBtn.setDisable(true); // enabled after connect
    sendBtn.getStyleClass().add("button-primary");

    HBox inputRow = new HBox(8, msgField, sendBtn);
    HBox.setHgrow(msgField, Priority.ALWAYS);

    VBox layout = new VBox(10, chatArea, inputRow);
    layout.setPadding(new Insets(15));
    layout.setId("mainPane");

    Scene scene = new Scene(layout, 460, 420);
    applyCSS(scene);
    chatStage.setScene(scene);
    chatStage.show();

    AtomicReference<PrintWriter> outRef    = new AtomicReference<>();
    AtomicReference<Socket>      socketRef = new AtomicReference<>();

    // Send on button click or Enter key
    Runnable doSend = () -> {
        PrintWriter out = outRef.get();
        if (out == null) return;
        String msg = msgField.getText().trim();
        if (msg.isEmpty()) return;
        out.println(msg);
        chatArea.appendText(role + ": " + msg + "\n");
        msgField.clear();
    };
    sendBtn.setOnAction(e -> doSend.run());
    msgField.setOnAction(e -> doSend.run());

    // Close socket when window closes
    chatStage.setOnCloseRequest(e -> {
        try {
            Socket s = socketRef.get();
            if (s != null && !s.isClosed()) s.close();
        } catch (Exception ignored) {}
    });

    // Network runs on daemon thread — keeps JavaFX UI alive
    Thread netThread = new Thread(() -> {
        try {
            Socket socket;
            if (isAdmin) {
                Platform.runLater(() -> chatArea.appendText("[System] Waiting for Student on port 5000...\n"));
                ServerSocket ss = new ServerSocket(5000);
                socket = ss.accept();
                ss.close(); // accept one connection only
                Platform.runLater(() -> {
                    chatArea.appendText("[System] Student connected!\n");
                    sendBtn.setDisable(false);
                    msgField.requestFocus();
                });
            } else {
                Platform.runLater(() -> chatArea.appendText("[System] Connecting to Admin...\n"));
                socket = new Socket("localhost", 5000);
                Platform.runLater(() -> {
                    chatArea.appendText("[System] Connected!\n");
                    sendBtn.setDisable(false);
                    msgField.requestFocus();
                });
            }

            socketRef.set(socket);
            PrintWriter    out = new PrintWriter(socket.getOutputStream(), true);
            BufferedReader in  = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            outRef.set(out);

            // Read loop — updates UI via Platform.runLater
            String line;
            while ((line = in.readLine()) != null) {
                final String msg = line;
                Platform.runLater(() -> chatArea.appendText(otherRole + ": " + msg + "\n"));
            }

            Platform.runLater(() -> chatArea.appendText("[System] Connection closed.\n"));
            socket.close();

        } catch (SocketException e) {
            Platform.runLater(() -> chatArea.appendText("[System] Disconnected.\n"));
        } catch (Exception e) {
            Platform.runLater(() -> showAlert(Alert.AlertType.ERROR, "Chat Error", e.getMessage()));
        }
    });
        netThread.setDaemon(true);
        netThread.start();
        }

    public static void main(String[] args) { launch(args); }
}