package DBConnect;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;

public class LoginController implements Initializable  {

	@FXML
	private ComboBox<String> databaseCBox;
	
	@FXML
	private Button connectButton;
	
	@FXML
	private TextField dbnameTextField;
	
	@FXML
	private TextField usernameTextField;
	
	@FXML
	private TextField hostIPTextField;
	
	@FXML
	private TextField portTextField;
	
	@FXML
	private PasswordField passwordTextField;
	
	@FXML
	private ProgressIndicator connectionProgressIndicator;
	
	@FXML
	public void loginClicked() {
		
		System.out.println("Button clicked");
		connectionProgressIndicator.setVisible(true);
		
		String hostname = hostIPTextField.getText();
		
		String port = portTextField.getText();
				
		String dbname = dbnameTextField.getText();
		
		String username = usernameTextField.getText();
		String password = passwordTextField.getText();
		
		String selectedDB = databaseCBox.getSelectionModel().getSelectedItem();
		SupportedDB dbType = SupportedDB.valueOf(selectedDB);
		
		try {
			
			DatabaseController.MakeConnection(dbType, dbname, hostname, port, username, password);	
			
			Stage stage = (Stage)connectButton.getScene().getWindow();
			
			Parent root = FXMLLoader.load(getClass().getResource("EditorView.fxml"));
			
			stage.setScene(new Scene(root, 800, 600));

			System.out.println("Connection made");
		}
		catch (Exception e) {
			
			Alert alert = new Alert(AlertType.ERROR, e.getMessage(), ButtonType.CLOSE);
			alert.showAndWait();
			
		} finally {
			connectionProgressIndicator.setVisible(false);	
		}		
	}


	@Override
	public void initialize(URL url, ResourceBundle bundle) {
		// called on window creation
		
		
		SupportedDB[] vals = SupportedDB.values();
		
		for (SupportedDB val : vals) {
			databaseCBox.getItems().add(val.toString());
		}
		databaseCBox.setValue(vals[0].toString());
	}
	
/*	private void ShowError(String errorMsg) {
		
		try {
			Stage stage = new Stage();
			stage.setTitle("Error");
			stage.initModality(Modality.APPLICATION_MODAL);
			
			FXMLLoader loader = new FXMLLoader(getClass().getResource("ErrorDialog.fxml"));
			
			Scene scene = new Scene(loader.load(), 200, 200);
			stage.setScene(scene);
			
			
			ErrorDialog controller = loader.<ErrorDialog>getController();
			
			controller.setDisplayText(errorMsg);
			
			stage.show();
			
		} catch (IOException e) {

			e.printStackTrace();
		}
	}*/
}
