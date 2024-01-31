package lk.ijse.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import lk.ijse.Launcher;

import java.io.IOException;

public class LoginFormController {

    @FXML
    private TextField txtUserName;

    @FXML
    void btnLoginOnAction(ActionEvent event) throws IOException {
        if (!txtUserName.getText().isEmpty() && txtUserName.getText().matches("[A-Za-z0-9]+")){
            FXMLLoader fxmlLoader = new FXMLLoader(Launcher.class.getResource("/view/ClientForm.fxml"));

            ClientFormController controller = new ClientFormController();
            controller.setClientName(txtUserName.getText());
            fxmlLoader.setControllerFactory(c -> controller);

            Stage primaryStage = new Stage();
            primaryStage.setTitle(txtUserName.getText());
            primaryStage.setScene(new Scene(fxmlLoader.load()));
            primaryStage.show();

            txtUserName.clear();
        }else {
            new Alert(Alert.AlertType.ERROR, "Please Enter Your Name! ").show();
        }


    }

}
