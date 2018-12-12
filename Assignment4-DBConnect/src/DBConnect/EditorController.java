package DBConnect;

import java.io.IOException;
import java.net.URL;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLType;
import java.sql.Types;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Iterator;
import java.util.Optional;
import java.util.ResourceBundle;

import javax.sql.rowset.CachedRowSet;
import javax.sql.rowset.RowSetFactory;
import javax.sql.rowset.RowSetProvider;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Callback;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.TableColumn.CellDataFeatures;
import javafx.scene.control.TableColumn.CellEditEvent;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.layout.FlowPane;

public class EditorController implements Initializable {

	
	enum ItemType { Table, Column, Database };
	private class ItemInfo {
		
		
		private String item;
		private ItemType type;
		
		public ItemInfo(String item, ItemType type) {
			this.item = item;
			this.type = type;
		}
		
		@Override
		public String toString() {
			return this.item;
		}
		
		public ItemType Type() {
			return this.type;
		}
	}
	
	@FXML
	private TableView editorTableView;

	@FXML
	private TreeView<ItemInfo> metadataTreeView;

	@FXML
	private TextArea queryTextArea;

	@FXML
	private FlowPane inputFlowPane;
	
	@FXML
	private String tableName;
	
	@Override
	public void initialize(URL url, ResourceBundle bundle) {
		
		metadataTreeView.setRoot(this.makeMetadataTree());
	}
	
	
	
	private CachedRowSet rowset;
	
	public void addButtonClicked() {
		
		if (tableName.equals(""))
			return;
		
		ObservableList<Node> children = inputFlowPane.getChildren();
		
		String insertQuery = "Insert Into " + tableName + " Values (";
		
		for (int i = 0; i < children.size(); ++i) {
			
			TextField field = (TextField)children.get(i);
			
			insertQuery += "\"" + field.getText() + "\"";
			
			if (i < children.size() - 1) {
				insertQuery += ", ";
			}
		}
		
		insertQuery += ");";
		
		
		try {
			
			DatabaseController.executeQuery(insertQuery);
					
			//rowset.moveToInsertRow();
			
			ObservableList newRow = FXCollections.observableArrayList();
			
			for (int i = 1; i <= rowset.getMetaData().getColumnCount(); ++i) {
				
				TextField field = (TextField)children.get(i - 1);
				
				System.out.println(field.getText());
				//rowset.updateString(i, field.getText());
				newRow.add(field.getText());
			}
			//rowset.insertRow();		
			//rowset.moveToCurrentRow();
			
			editorTableView.getItems().add(newRow);
			editorTableView.refresh();	
			
			rowset = this.buildRowSet("SELECT * from " + tableName + " LIMIT 1000;");
		} catch (SQLException e) {
			
			Alert alert = new Alert(AlertType.ERROR, e.getMessage(), ButtonType.OK);
			alert.showAndWait();
		}
	}
	
	public void searchButtonClicked() throws SQLException {
		
		if (tableName.equals(""))
			return;
		
		ObservableList<Node> children = inputFlowPane.getChildren();
		
		
		for (int i = 1; i <= rowset.getMetaData().getColumnCount(); ++i) {
			
			TextField field = (TextField)children.get(i - 1);
			
			System.out.println(field.getText());
		
		}
	}
	
	
	public void removeButtonClicked() throws SQLException {
		
		MultipleSelectionModel msm = editorTableView.getSelectionModel();
		
		ObservableList<Integer> selected = msm.getSelectedIndices();
		
		for (int i = 0; i < selected.size(); ++i) {
			
			int index = selected.get(i).intValue();
			editorTableView.getItems().remove(index);
			
			rowset.absolute(index + 1);
			rowset.deleteRow();
			rowset.moveToCurrentRow();
		}
		
		editorTableView.refresh();
	}
	
	
	public void commitButtonClicked() {
		
		try {
			Alert alert = new Alert(AlertType.CONFIRMATION, "Are you sure you wish to commit these changes?", ButtonType.OK, ButtonType.CANCEL);
			
			Optional<ButtonType> result = alert.showAndWait();
			
			if(!result.isPresent() || result.get() == ButtonType.CANCEL)
				return;
			else if(result.get() == ButtonType.OK)
				DatabaseController.acceptChanges(rowset);
			
			alert = new Alert(AlertType.INFORMATION, "Changes successfully commited.", ButtonType.OK);
			alert.show();
			
		} catch (SQLException e) {
			
			Alert alert = new Alert(AlertType.ERROR, "Commit failed: " + e.getMessage(), ButtonType.CLOSE);
			alert.showAndWait();
		}
	}
	
	private boolean hasEdited = false;
	
	private CachedRowSet buildRowSet(String query) throws SQLException {
		
		ResultSet rs = DatabaseController.fetchQuery(query);
		
		// creating a cached row set from the result set
		RowSetFactory factory = RowSetProvider.newFactory();
		CachedRowSet rowset = factory.createCachedRowSet();
		rowset.populate(rs);
		
		return rowset;
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void buildTable() {
		
		// clearing flow pane
		inputFlowPane.getChildren().clear();
		
		// clear table view items and columns
		editorTableView.getItems().clear();
		editorTableView.getColumns().clear();
		
		ObservableList<ObservableList> data = FXCollections.observableArrayList();
		try {
			// CHANGE THIS LATER AUHNFAINFOADNF
			String SQLQuery = "SELECT * from " + tableName + " LIMIT 1000;";

			queryTextArea.setText(SQLQuery);
			
			rowset = buildRowSet(SQLQuery);
			
			for (int i = 0; i < rowset.getMetaData().getColumnCount(); i++) {
				
				final int j = i;
				
				String colName = rowset.getMetaData().getColumnName(i + 1);
				TableColumn col = new TableColumn(colName);
				
				// creating the text field for input
				TextField inputField = new TextField();
				inputField.setPromptText(colName);
				inputField.setPadding(new Insets(10, 10, 10, 10));
			
				
				inputFlowPane.getChildren().add(inputField);
				
				int type = rowset.getMetaData().getColumnType(i + 1);
				
				col.setEditable(true);
				col.setCellValueFactory( new Callback<CellDataFeatures<ObservableList, String>, ObservableValue<String>>() {
							
					public ObservableValue<String> call(CellDataFeatures<ObservableList, String> param) {
								
						return new SimpleStringProperty(param.getValue().get(j).toString());
					}
							
				});
				
				col.setCellFactory(TextFieldTableCell.forTableColumn());

				col.setOnEditCommit(new EventHandler<CellEditEvent>() {

					@Override
					public void handle(CellEditEvent t) {
						
						int rowIndex = t.getTablePosition().getRow();
						
						ObservableList source = (ObservableList)t.getTableView().getItems().get(rowIndex);
						int colIndex = source.indexOf(t.getOldValue());
						
						try {
							EditorController.toObject((String)t.getNewValue(), type);
							
							rowset.absolute(rowIndex + 1);
							rowset.updateString(colIndex + 1, (String)t.getNewValue());
							
							rowset.updateRow();
							source.set(colIndex, t.getNewValue());
							hasEdited = true;
						} 
						catch (NumberFormatException | ParseException e) {
							Alert alert = new Alert(AlertType.ERROR, "Type mismatch: " + e.getMessage(), ButtonType.CLOSE);
							alert.showAndWait();
						}
						catch (SQLException e) {
							Alert alert = new Alert(AlertType.ERROR, e.getMessage(), ButtonType.CLOSE);
							alert.showAndWait();
						}
						
						editorTableView.refresh();
					}
				});

				col.setCellFactory( TextFieldTableCell.<ObservableList>forTableColumn());
				
				
				editorTableView.getColumns().addAll(col);
				System.out.println("Column [" + i + "] ");
			}

			while (rowset.next()) {

				ObservableList<String> row = FXCollections.observableArrayList();
				
				for (int i = 1; i <= rowset.getMetaData().getColumnCount(); i++) {

					row.add(rowset.getString(i));
				}
				data.add(row);
				
				System.out.println("Row " + row + " ");
			}
			
			editorTableView.setItems(data);
			editorTableView.refresh();
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private static Object toObject(String value, int type) throws ParseException {
		
		Object result = null;

		switch (type) {
		
			case Types.CHAR:
			case Types.VARCHAR:
			case Types.LONGVARCHAR:
				result = value;
				break;
				
			case Types.BIT:
				result = Boolean.parseBoolean(value);
				break;
	
			case Types.TINYINT:
				result = Byte.parseByte(value);
				break;
	
			case Types.SMALLINT:
				result = Short.parseShort(value);
				break;
	
			case Types.INTEGER:
				result = Integer.parseInt(value);
				break;
	
			case Types.BIGINT:
				result = Long.parseLong(value);
				break;
	
			case Types.REAL:
			case Types.FLOAT:
				result = Float.parseFloat(value);
				break;
	
			case Types.DOUBLE:
				result = Double.parseDouble(value);
				break;
	
			case Types.BINARY:
			case Types.VARBINARY:
			case Types.LONGVARBINARY:
				result = value.getBytes();
				break;
	
			case Types.DATE:
				result = new SimpleDateFormat("dd MMM yyyy").parse(value);
				break;
	
			case Types.TIME:
				result = new SimpleDateFormat("hh:mm:ss").parse(value);
				break;
	
			case Types.TIMESTAMP:
				result = new SimpleDateFormat("hh:mm:ss dd MMM yyyy").parse(value);
				break;
		}
		return result;
	}
	

	private TreeItem<ItemInfo> makeMetadataTree() {
		
		try {
			DatabaseMetaData data = DatabaseController.getMetadata();
			
			String schemaName = DatabaseController.getCatalog();
			TreeItem<ItemInfo> root = new TreeItem<ItemInfo>(new ItemInfo(schemaName, ItemType.Database));

			String[] types = { "TABLE" };

			ResultSet tables = data.getTables(schemaName, null, "%", types);

			while (tables.next()) {
				
				
				String tableName = tables.getString(3);
				
				TreeItem<ItemInfo> tableItem = new TreeItem<ItemInfo>(new ItemInfo(tableName, ItemType.Table));
				
				ResultSet columns = data.getColumns(null, null, tableName, null);

				while (columns.next()) {
					
					// get the column name and type
					String type = columns.getString("TYPE_NAME");
					String name = columns.getString("COLUMN_NAME");
					
		 			TreeItem<ItemInfo> columnItem = new TreeItem<ItemInfo>(new ItemInfo(name + ": " + type, ItemType.Column));
					tableItem.getChildren().add(columnItem);
				}
			
				root.getChildren().add(tableItem);
			}
			metadataTreeView.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<TreeItem<ItemInfo>>() {

				@Override
				public void changed(ObservableValue<? extends TreeItem<ItemInfo>> observable, TreeItem<ItemInfo> oldValue, TreeItem<ItemInfo> newValue) {

					ItemInfo selected = newValue.getValue();
							
					if (selected.Type().equals(ItemType.Table)) {
						tableName = selected.item;
						buildTable();
					}
							
				}
			});
						
			return root;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return null;
	}
}

//private Class<?> toClass(int type) {
//	
//    Class<?> result = Object.class;
//
//    switch (type) {
//        case Types.CHAR:
//        case Types.VARCHAR:
//        case Types.LONGVARCHAR:
//            result = String.class;
//            break;
//
//        case Types.NUMERIC:
//        case Types.DECIMAL:
//            result = java.math.BigDecimal.class;
//            break;
//
//        case Types.BIT:
//            result = Boolean.class;
//            break;
//
//        case Types.TINYINT:
//            result = Byte.class;
//            break;
//
//        case Types.SMALLINT:
//            result = Short.class;
//            break;
//
//        case Types.INTEGER:
//            result = Integer.class;
//            break;
//
//        case Types.BIGINT:
//            result = Long.class;
//            break;
//
//        case Types.REAL:
//        case Types.FLOAT:
//            result = Float.class;
//            break;
//
//        case Types.DOUBLE:
//            result = Double.class;
//            break;
//
//        case Types.BINARY:
//        case Types.VARBINARY:
//        case Types.LONGVARBINARY:
//            result = Byte[].class;
//            break;
//
//        case Types.DATE:
//            result = java.sql.Date.class;
//            break;
//
//        case Types.TIME:
//            result = java.sql.Time.class;
//            break;
//
//        case Types.TIMESTAMP:
//            result = java.sql.Timestamp.class;
//            break;
//    }
//
//    return result;
//}