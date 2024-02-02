package lk.ijse.controller;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXScrollPane;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import lk.ijse.emoji.Emoji;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.net.URL;
import java.nio.file.Files;
import java.util.ResourceBundle;

public class ClientFormController implements Runnable, Initializable {
    private DataInputStream dataInputStream;
    private DataOutputStream dataOutputStream;
    private Socket socket;
    private String clientName = "Client";
    @FXML
    private AnchorPane apane;

    @FXML
    private AnchorPane emojiPane;


    @FXML
    private TextField txtMassegeTypingBar;
    @FXML
    private Label lblClientName;

    @FXML
    private ScrollPane scrollPane;
    @FXML
    private VBox txtVBox;

    @FXML
    void btnImageOnAction(ActionEvent event) {
        FileChooser fileChooser=new FileChooser();
        fileChooser.setTitle("Images");
        fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("Image Files" , "*.png","*.jpg"));
        File file=fileChooser.showOpenDialog(txtMassegeTypingBar.getScene().getWindow());
        if (file!=null){
            try {
                byte [] bytes= Files.readAllBytes(file.toPath());
                sendImages(clientName,bytes);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    private void sendImages(String clientName, byte[] bytes) {
        try {
            dataOutputStream.writeUTF("/02");
            dataOutputStream.flush();
            dataOutputStream.writeUTF(clientName);
            dataOutputStream.flush();
            dataOutputStream.writeInt(bytes.length);
            dataOutputStream.flush();
            dataOutputStream.write(bytes);
            dataOutputStream.flush();

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @FXML
    void btnSendOnAction(ActionEvent event) {
        String massege=txtMassegeTypingBar.getText();
        if(massege!=null){
            try {
                dataOutputStream.writeUTF("/01");
                dataOutputStream.flush();
                dataOutputStream.writeUTF(clientName+" : "+massege);
                dataOutputStream.flush();
                System.out.println(massege);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

    }

    public void setClientName(String name) {
        clientName = name;
    }

    @Override
    public void run() {
        try {
            socket=new Socket("localhost",5600 );
            dataInputStream=new DataInputStream(new BufferedInputStream(socket.getInputStream()));
            dataOutputStream=new DataOutputStream(socket.getOutputStream());
            String massege;
            while ((massege=dataInputStream.readUTF())!=null){


              if (massege.equals("/01")){
                  massege=dataInputStream.readUTF();
                  String[] split = massege.split(" : ");
                  HBox hBox=new HBox();
                  Label label=new Label();
                  label.setFont(Font.font(25));


                  if(massege.startsWith(clientName)){
                      label.setText(split[1]);
                      hBox.setAlignment(Pos.CENTER_RIGHT);

                  }
                  else {
                      label.setText(massege);
                      hBox.setAlignment(Pos.CENTER_LEFT);
                  }
                  hBox.getChildren().add(label);

                  Platform.runLater(()->{
                      txtVBox.getChildren().add(hBox);
                  });
              }
              else if (massege.equals("/02")){
                  String name=dataInputStream.readUTF();
                  byte[] bytes=new byte[dataInputStream.readInt()];
                  dataInputStream.readFully(bytes);

                  HBox hBox=new HBox();
                  Label label=new Label(name+" : ");
                  label.setFont(Font.font(25));


                  ByteArrayInputStream imageArray = new ByteArrayInputStream(bytes);
                  Image image=new Image(imageArray);  ////////////////
                  ImageView imageView=new ImageView(image);
                  imageView.setFitWidth(100);
                  imageView.setFitHeight(100);


                  if(name.equals(clientName)){
                      hBox.getChildren().add(imageView);
                      hBox.setAlignment(Pos.CENTER_RIGHT);
                  }
                  else {
                      hBox.getChildren().add(label);
                      hBox.getChildren().add(imageView);
                      hBox.setAlignment(Pos.CENTER_LEFT);
                  }

                  Platform.runLater(()->{
                      txtVBox.getChildren().add(hBox);
                  });


                  }
              }


        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }
    @FXML
    void btnEmojiOnAction(ActionEvent actionEventevent) {
        Emoji emoji = new Emoji();
        VBox vBox =new VBox(emoji);
        vBox.setPrefSize(300,200);
        vBox.setLayoutX(200);
        vBox.setLayoutY(75);
        vBox.setStyle("-fx-font-size: 25");

        apane.getChildren().add(vBox);
        emoji.setVisible(true);

        emoji.getListView().setOnMouseClicked(event -> {
            String selectedEmoji = emoji.getListView().getSelectionModel().getSelectedItem();
            if (selectedEmoji != null) {
                txtMassegeTypingBar.setText(txtMassegeTypingBar.getText()+selectedEmoji);
            }
            emoji.setVisible(false);
        });
    }



    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        lblClientName.setText(clientName);
        new Thread(()->{
            this.run();
        }).start();

    }
    @FXML
    void btnCloseOnAction(ActionEvent event) {
        System.exit(0);
    }

    @FXML
    void btnMinimizeOnAction(ActionEvent event) {
        Stage stage = ( Stage) ((JFXButton)event.getSource ()).getScene ().getWindow ();
        stage.setIconified ( true );


    }



}
