import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.Stage;
import java.io.*;
import java.net.Socket;

public class Client extends Application {
    PrintWriter pw;
    public String nomClient;

    public static void main(String[] args) {
        launch(args);
    }
    @Override
    public void start(Stage stage) throws Exception {


        stage.setTitle("Chat app");
        BorderPane borderPane = new BorderPane();
        borderPane.setStyle("-fx-background-color: white;"+"-fx-font-weight: bold;");

        Label title = new Label("Chatter");
        title.setAlignment(Pos.CENTER);
        title.setStyle("-fx-font-family: Corbel;"+"-fx-font-size: 24px;"+"-fx-text-fill: #c49489;");

        Label name = new Label("NAME");
        name.setAlignment(Pos.CENTER);
        name.setStyle("-fx-font-family: Corbel;"+"-fx-font-size: 15px;"+"-fx-text-fill: #733d8a;");
        TextField clientName = new TextField();
        clientName.setPrefSize(80, 25);
        Label id = new Label("ID");
        id.setAlignment(Pos.CENTER);
        id.setStyle("-fx-font-family: Corbel;"+"-fx-font-size: 15px;"+"-fx-text-fill: #733d8a;");
        TextField clientId = new TextField();
        clientId.setPrefSize(50, 25);
        Button buttonConnect = new Button("Connect");
        buttonConnect.setStyle("-fx-background-color: rgb(166, 163, 247);"+"-fx-background-radius: 5px;");

        HBox hBox = new HBox();
        hBox.setSpacing(10);
        hBox.setPadding(new Insets(5));
        hBox.getChildren().addAll(name,clientName,id,clientId,buttonConnect);
        hBox.setAlignment(Pos.CENTER);
        VBox vBox5 = new VBox();
        vBox5.setAlignment(Pos.CENTER);
        vBox5.getChildren().addAll(title,hBox);
        vBox5.setSpacing(10);
        borderPane.setTop(vBox5);

        VBox messages = new VBox();
        messages.setPrefSize(350,400);
        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setPrefSize(300,400);
        scrollPane.setFitToWidth(true);
        scrollPane.setLayoutX(30);
        scrollPane.setLayoutY(70);
        scrollPane.setContent(messages);
        borderPane.setCenter(scrollPane);

        Button buttonMsg = new Button("Send");
        buttonMsg.setPadding(new Insets(5));
        buttonMsg.setStyle("-fx-background-color: rgb(179, 227, 245);"+"-fx-background-radius: 5px;");

        TextField textFieldMsg = new TextField();
        textFieldMsg.setPromptText("Your Message");
        textFieldMsg.setPrefSize(200, 34);
        textFieldMsg.setStyle("-fx-background-color: #c9c9c9;"+"-fx-background-radius: 30px;");

        Button receiversButton = new Button("ok");
        receiversButton.setPadding(new Insets(5));
        receiversButton.setStyle("-fx-background-color: #af2ae8;"+"-fx-background-radius: 5px;");

        Button editReceiversButton = new Button("edit");
        editReceiversButton.setPadding(new Insets(5));
        editReceiversButton.setStyle("-fx-background-color: rgb(19, 211, 25);"+"-fx-background-radius: 5px;");

        TextField textFieldReceivers = new TextField();
        textFieldReceivers.setPromptText("receivers IDS");
        textFieldReceivers.setPrefSize(100, 34);
        textFieldReceivers.setStyle("-fx-background-color: #c9c9c9;"+"-fx-background-radius: 30px;");

        HBox hBox1 = new HBox();
        hBox1.setSpacing(10);
        hBox1.setPadding(new Insets(10));
        hBox1.getChildren().addAll(textFieldMsg,buttonMsg,textFieldReceivers,receiversButton);
        borderPane.setBottom(hBox1);

        Scene scene = new Scene(borderPane,440, 480);
        stage.setScene(scene);
        stage.show();

        receiversButton.setOnAction((actionEvent) -> {
            hBox1.getChildren().remove(receiversButton);
            hBox1.getChildren().add(editReceiversButton);
            textFieldReceivers.setDisable(true);
        });

        editReceiversButton.setOnAction((actionEvent) -> {
            hBox1.getChildren().remove(editReceiversButton);
            hBox1.getChildren().add(receiversButton);
            textFieldReceivers.setDisable(false);
        });

        buttonConnect.setOnAction((actionEvent) -> {
            try {
                Socket socket = new Socket("localhost",3333);
                InputStream is = socket.getInputStream();
                InputStreamReader isr = new InputStreamReader(is);
                BufferedReader br = new BufferedReader(isr);
                OutputStream os = socket.getOutputStream();
                pw = new PrintWriter(os,true);
                pw.println(clientId.getText()+ ","+clientName.getText());
                nomClient=(String) clientName.getText();


                new Thread(() -> {
                    while (true) {
                        try {
                            String response = br.readLine();
                            HBox hBox2 = new HBox();
                            hBox2.setAlignment(Pos.CENTER_LEFT);
                            hBox2.setPadding(new Insets(5, 5, 5, 10));

                            Text text = new Text(response);
                            TextFlow textFlow = new TextFlow(text);

                            textFlow.setStyle(
                                    "-fx-background-color: rgb(233, 233, 235);" +
                                            "-fx-background-radius: 20px;");

                            textFlow.setPadding(new Insets(5, 10, 5, 10));
                            hBox2.getChildren().add(textFlow);

                            Platform.runLater(new Runnable() {
                                @Override
                                public void run() {
                                    messages.getChildren().add(hBox2);
                                }
                            });
                        }catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    }
                }).start();

            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            hBox.setVisible(false);
        });

        buttonMsg.setOnAction((event) -> {
            String messageToServer = textFieldMsg.getText();
            if(!textFieldReceivers.getText().isEmpty()){
                messageToServer = textFieldReceivers.getText()+"=>"+textFieldMsg.getText();
            }
            String messageToSend =textFieldMsg.getText();
            if (!messageToSend.isEmpty()) {
                HBox hBox3 = new HBox();
                hBox3.setAlignment(Pos.CENTER_RIGHT);

                hBox3.setPadding(new Insets(5, 5, 5, 10));
                Text text = new Text(messageToSend);
                TextFlow textFlow = new TextFlow(text);
                textFlow.setStyle(
                        "-fx-color: rgb(239, 242, 255);" +
                                "-fx-background-color: rgb(15, 125, 242);" +
                                "-fx-background-radius: 20px;");

                textFlow.setPadding(new Insets(5, 10, 5, 10));
                text.setFill(Color.color(0.934, 0.925, 0.996));

                hBox3.getChildren().add(textFlow);
                messages.getChildren().add(hBox3);

                pw.println(nomClient+" :"+messageToServer);
                textFieldMsg.clear();
            }
        });
    }
}