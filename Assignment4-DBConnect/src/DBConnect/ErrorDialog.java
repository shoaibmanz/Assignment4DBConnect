package DBConnect;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;


public class ErrorDialog {
	
	@FXML
	private Label displayLabel;
	
	@FXML
	private Button okButton;
	
	void setDisplayText(String text) {
		this.displayLabel.setText(text);
	}
	
	public void OKPressed() {
		
		Stage window = (Stage)okButton.getScene().getWindow();
		
		window.close();
	}
}
