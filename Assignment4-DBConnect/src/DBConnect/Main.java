package DBConnect;

import javafx.application.*;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;


public class Main extends Application {
	
	@Override
	public void start(Stage stage) throws Exception {
		
		Parent root = FXMLLoader.load(getClass().getResource("LoginView.fxml"));
		
		// load the login view in the start
		stage.setScene(new Scene(root, 400, 300));
		stage.setTitle("DB Connect");
		stage.show();
	}
	
	public static void main(String[] args) {
		
		launch(args);
	}

}
