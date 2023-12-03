//Students: Yousef Elsonbaty (A00138), Mariam Hussein (A00039) & Amr Osman (A00112)

package application;

import java.awt.Desktop;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Scanner;

import javafx.animation.Animation;
import javafx.animation.AnimationTimer;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DateCell;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.ListView;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.text.Text;
import javafx.scene.web.WebView;
import javafx.stage.Stage;
import javafx.util.Duration;

public class Controllers {
    
    @FXML
    private Button btnDelete;

    @FXML
    private Button btnEdit;

    @FXML
    private Button btnExport;

    @FXML
    private Button btnNew;

    @FXML
    private Button btnSave;

    @FXML
    private Button btnUndo;

    @FXML
    private BarChart<String, Number> UIChart;

    @FXML
    private Text apiPlace;

    @FXML
    private TableView<Item> dataTable;
    
    @FXML
    private TableColumn<Item, String> nameCol;

    @FXML
    private TableColumn<Item, LocalDate> dateCol;
    
    @FXML
    private TableColumn<Item, String> locationCol;

    @FXML
    private TableColumn<Item, String> statusCol;
    
    @FXML
    private TableColumn<Item, String> descriptionCol;
    
    @FXML
    private TextField tfItemName;

    @FXML
    private DatePicker dateLostFound;

    @FXML
    private ComboBox<String> dropdownLocations;

    @FXML
    private ComboBox<String> dropdownStatus;
    
    @FXML
    private TextArea txtDescription;

    @FXML
    private ListView<String> locationsList;

    @FXML
    private MenuBar menu;
    
    @FXML
    private MenuItem helpItem;
    
    @FXML
    private MenuItem quitItem;
    
    @FXML
    private Text txtDateTime;

    @FXML
    private TextArea txtItem;
    
    @FXML
    private Text txtMessage;

    @FXML
    private Hyperlink urlAUBH;
    
	//Initialize data types for different situations
	ObservableList<Item> initialData = FXCollections.observableArrayList();
	ObservableList<Item> backupData = FXCollections.observableArrayList();
	
	int BAL, BBL, BCL, BDL, AUDL, LIBL, GYML, STCL, WCL;
	int BAF, BBF, BCF, BDF, AUDF, LIBF, GYMF, STCF, WCF;
    
    @FXML
    void DateLostFoundAction(ActionEvent event) {
    	dateLostFound.getValue();
    }

    @FXML
    void HelpAction(ActionEvent event) throws MalformedURLException {
    	Stage stage = new Stage();
    	String location =
                new File(
                        System.getProperty("user.dir") + File.separator + "README.txt"
                ).toURI().toURL().toExternalForm();

        WebView webView = new WebView();
        webView.getEngine().load(location);

        Scene scene = new Scene(webView);
        stage.setTitle("Help");
        stage.setScene(scene);
        stage.show();
    }

    @FXML
	void QuitAction(ActionEvent event) {
    	Platform.exit();	
	}
    
    @FXML
    void NewButtonAction(ActionEvent event) {
    	btnNew.setDisable(true);
    	tfItemName.setDisable(false);
    	dateLostFound.setDisable(false);
    	dropdownLocations.setDisable(false);
    	dropdownStatus.setDisable(false);
    	txtDescription.setDisable(false);
    	tfItemName.clear();
    	txtDescription.clear();
    }

    @FXML
    void SaveButtonAction(ActionEvent event) {
    	//All locations get displayed
    	locationsList.getSelectionModel().selectFirst();
	    
	    //Adding New Record
    	Item item = new Item(tfItemName.getText(),dateLostFound.getValue(),dropdownLocations.getValue().toString(),dropdownStatus.getValue().toString(),txtDescription.getText());
    	dataTable.getItems().addAll(item);
    	txtMessage.setText("Item is saved.");
    	tfItemName.clear();
    	txtDescription.clear();
    	txtItem.setText(null);
    	tfItemName.setDisable(true);
    	dateLostFound.setDisable(true);
    	dropdownLocations.setDisable(true);
    	dropdownStatus.setDisable(true);
    	txtDescription.setDisable(true);
    	btnNew.setDisable(false);
    	
    	//Setting up the bar chart
    	XYChart.Series<String, Number> series1 = new XYChart.Series<>();
        series1.setName("Items lost");       
    	
    	XYChart.Series<String, Number> series2 = new XYChart.Series<>();
        series2.setName("Items found");
        
        UIChart.setAnimated(false);
        
        UIChart.getData().add(series1);
        UIChart.getData().add(series2);

    	BAL = BBL = BCL = BDL = AUDL = LIBL = GYML = STCL = WCL = 0;
    	BAF = BBF = BCF = BDF = AUDF = LIBF = GYMF = STCF = WCF = 0;
    	
    	for(int i = 0; i < initialData.size(); i++) {
     		if (initialData.get(i).getItemLocation().toString() == "Building A" && initialData.get(i).getItemStatus().toString() == "Lost") 
     			BAL += 1;
     		else if (initialData.get(i).getItemLocation().toString() == "Building B" && initialData.get(i).getItemStatus().toString() == "Lost") 
     			BBL += 1;
     		else if (initialData.get(i).getItemLocation().toString() == "Building C" && initialData.get(i).getItemStatus().toString() == "Lost") 
     			BCL += 1;
     		else if (initialData.get(i).getItemLocation().toString() == "Building D" && initialData.get(i).getItemStatus().toString() == "Lost") 
     			BDL += 1;
     		else if (initialData.get(i).getItemLocation().toString() == "Auditorium" && initialData.get(i).getItemStatus().toString() == "Lost") 
     			AUDL += 1;
     		else if (initialData.get(i).getItemLocation().toString() == "Library" && initialData.get(i).getItemStatus().toString() == "Lost") 
     			LIBL += 1;
     	    else if (initialData.get(i).getItemLocation().toString() == "Sports Center" && initialData.get(i).getItemStatus().toString() == "Lost") 
     	    	GYML += 1;
     	    else if (initialData.get(i).getItemLocation().toString() == "Student Commons" && initialData.get(i).getItemStatus().toString() == "Lost") 
     		   	STCL += 1;
     	    else if (initialData.get(i).getItemLocation().toString() == "Welcome Center" && initialData.get(i).getItemStatus().toString() == "Lost") 	
     		   	WCL += 1;
     	}
    	
    	for(int i = 0; i < initialData.size(); i++) {
     		if (initialData.get(i).getItemLocation().toString() == "Building A" && initialData.get(i).getItemStatus().toString() == "Found") 
     			BAF += 1;
     		else if (initialData.get(i).getItemLocation().toString() == "Building B" && initialData.get(i).getItemStatus().toString() == "Found") 
     			BBF += 1;
     		else if (initialData.get(i).getItemLocation().toString() == "Building C" && initialData.get(i).getItemStatus().toString() == "Found") 
     			BCF += 1;
     		else if (initialData.get(i).getItemLocation().toString() == "Building D" && initialData.get(i).getItemStatus().toString() == "Found") 
     			BDF += 1;
     		else if (initialData.get(i).getItemLocation().toString() == "Auditorium" && initialData.get(i).getItemStatus().toString() == "Found") 
     			AUDF += 1;
     		else if (initialData.get(i).getItemLocation().toString() == "Library" && initialData.get(i).getItemStatus().toString() == "Found") 
     			LIBF += 1;
     	    else if (initialData.get(i).getItemLocation().toString() == "Sports Center" && initialData.get(i).getItemStatus().toString() == "Found") 
     	    	GYMF += 1;
     	    else if (initialData.get(i).getItemLocation().toString() == "Student Commons" && initialData.get(i).getItemStatus().toString() == "Found") 
     	    	STCF += 1;
     	    else if (initialData.get(i).getItemLocation().toString() == "Welcome Center" && initialData.get(i).getItemStatus().toString() == "Found") 
     	    	WCF += 1;
     	}
	 	
	 	UIChart.getData().clear();
    	
	 	series1.getData().add(new XYChart.Data<String, Number>("Building A", BAL));
	 	series2.getData().add(new XYChart.Data<String, Number>("Building A", BAF));
	 	series1.getData().add(new XYChart.Data<String, Number>("Building B", BBL));
	 	series2.getData().add(new XYChart.Data<String, Number>("Building B", BBF));
	 	series1.getData().add(new XYChart.Data<String, Number>("Building C", BCL));
	 	series2.getData().add(new XYChart.Data<String, Number>("Building C", BCF));
	 	series1.getData().add(new XYChart.Data<String, Number>("Building D", BDL));
	 	series2.getData().add(new XYChart.Data<String, Number>("Building D", BDF));
	 	series1.getData().add(new XYChart.Data<String, Number>("Auditorium", AUDL));
	 	series2.getData().add(new XYChart.Data<String, Number>("Auditorium", AUDF));
	 	series1.getData().add(new XYChart.Data<String, Number>("Library", LIBL));
	 	series2.getData().add(new XYChart.Data<String, Number>("Library", LIBF));
	 	series1.getData().add(new XYChart.Data<String, Number>("Sports Center", GYML));
	 	series2.getData().add(new XYChart.Data<String, Number>("Sports Center", GYMF));
	 	series1.getData().add(new XYChart.Data<String, Number>("Student Commons", STCL));
	 	series2.getData().add(new XYChart.Data<String, Number>("Student Commons", STCF));
	 	series1.getData().add(new XYChart.Data<String, Number>("Welcome Center", WCL));
	 	series2.getData().add(new XYChart.Data<String, Number>("Welcome Center", WCF));
	 	
	 	UIChart.getData().add(series1);
        UIChart.getData().add(series2);
    }
    
    @FXML
    void EditButtonAction(ActionEvent event) {
    	//All locations get displayed
    	locationsList.getSelectionModel().selectFirst();
    	
    	Item item = dataTable.getSelectionModel().getSelectedItem();
    	tfItemName.setDisable(false);
    	dateLostFound.setDisable(false);
    	dropdownLocations.setDisable(false);
    	dropdownStatus.setDisable(false);
    	txtDescription.setDisable(false);
    	tfItemName.setText(item.getItemName());
    	dateLostFound.setValue(item.getItemDate());
    	dropdownLocations.setValue(item.getItemLocation());
    	dropdownStatus.setValue(item.getItemStatus());
    	txtDescription.setText(item.getItemDescription());
    	dataTable.getItems().remove(item);
    	btnNew.setDisable(true);
    }

    @FXML
    void ExportButtonAction(ActionEvent event) throws IOException {
        Writer writer = null;
        try {
            File file = new File("LostFoundItems.csv");
            writer = new BufferedWriter(new FileWriter(file));
            
            //Write the header into the CSV file
        	String header = nameCol.getText() + "," + dateCol.getText() + "," + locationCol.getText() + "," + statusCol.getText() + "," +descriptionCol.getText() +  "\n"; 
            writer.write(header);
            
            //Write each row into the CSV file
            for (Item item : initialData) {
                String text = item.getItemName() + "," + item.getItemDate() + "," + item.getItemLocation() + "," + item.getItemStatus() + "," + item.getItemDescription() + "\n";
                writer.write(text);
            }
            
            //Notify user that the export function is done
            txtMessage.setText("Data has been exported to CSV file.");
            
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        finally {
        	writer.flush();
            writer.close();
        } 
    }
    
    @FXML
    void DeleteButtonAction(ActionEvent event) {
    	//All locations get displayed
    	locationsList.getSelectionModel().selectFirst();
    	
    	//Enable the user to select multiple rows to delete
		dataTable.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
		ObservableList<Item> selectedRows = dataTable.getSelectionModel().getSelectedItems();
		ArrayList<Item> rows = new ArrayList<>(selectedRows);
		backupData.addAll(rows);
		rows.forEach(row -> dataTable.getItems().remove(row));
		txtMessage.setText("Item(s) is (are) deleted.");
		txtItem.setText(null);
		btnUndo.setDisable(false);
		
    	//Setting up the bar chart
    	XYChart.Series<String, Number> series1 = new XYChart.Series<>();
        series1.setName("Items lost");       
    	
    	XYChart.Series<String, Number> series2 = new XYChart.Series<>();
        series2.setName("Items found");
        
        UIChart.setAnimated(false);
        
        UIChart.getData().add(series1);
        UIChart.getData().add(series2);
        
    	BAL = BBL = BCL = BDL = AUDL = LIBL = GYML = STCL = WCL = 0;
    	BAF = BBF = BCF = BDF = AUDF = LIBF = GYMF = STCF = WCF = 0;
    	
    	for(int i = 0; i < initialData.size(); i++) {
     		if (initialData.get(i).getItemLocation().toString() == "Building A" && initialData.get(i).getItemStatus().toString() == "Lost") 
     			BAL = -(BAL -= 1);
     		else if (initialData.get(i).getItemLocation().toString() == "Building B" && initialData.get(i).getItemStatus().toString() == "Lost") 
     			BBL = -(BBL -= 1);
     		else if (initialData.get(i).getItemLocation().toString() == "Building C" && initialData.get(i).getItemStatus().toString() == "Lost") 
     			BCL = -(BCL -= 1);
     		else if (initialData.get(i).getItemLocation().toString() == "Building D" && initialData.get(i).getItemStatus().toString() == "Lost") 
     			BDL = -(BDL -= 1);
     		else if (initialData.get(i).getItemLocation().toString() == "Auditorium" && initialData.get(i).getItemStatus().toString() == "Lost") 
     			AUDL = -(AUDL -= 1);
     		else if (initialData.get(i).getItemLocation().toString() == "Library" && initialData.get(i).getItemStatus().toString() == "Lost") 
     			LIBL = -(LIBL -= 1);
     	    else if (initialData.get(i).getItemLocation().toString() == "Sports Center" && initialData.get(i).getItemStatus().toString() == "Lost") 
     	    	GYML = -(GYML -= 1);
     	    else if (initialData.get(i).getItemLocation().toString() == "Student Commons" && initialData.get(i).getItemStatus().toString() == "Lost") 
     	    	STCL = -(STCL -= 1);
     	    else if (initialData.get(i).getItemLocation().toString() == "Welcome Center" && initialData.get(i).getItemStatus().toString() == "Lost") 	
     	    	WCL = -(WCL -= 1);
     	}
    	
    	for(int i = 0; i < initialData.size(); i++) {
     		if (initialData.get(i).getItemLocation().toString() == "Building A" && initialData.get(i).getItemStatus().toString() == "Found") 
     			BAF = -(BAF -= 1);
     		else if (initialData.get(i).getItemLocation().toString() == "Building B" && initialData.get(i).getItemStatus().toString() == "Found") 
     			BBF = -(BBF -= 1);
     		else if (initialData.get(i).getItemLocation().toString() == "Building C" && initialData.get(i).getItemStatus().toString() == "Found") 
     			BCF = -(BCF -= 1);
     		else if (initialData.get(i).getItemLocation().toString() == "Building D" && initialData.get(i).getItemStatus().toString() == "Found") 
     			BDF = -(BDF -= 1);
     		else if (initialData.get(i).getItemLocation().toString() == "Auditorium" && initialData.get(i).getItemStatus().toString() == "Found") 
     			AUDF = -(AUDF -= 1);
     		else if (initialData.get(i).getItemLocation().toString() == "Library" && initialData.get(i).getItemStatus().toString() == "Found") 
     			LIBF = -(LIBF -= 1);
     	    else if (initialData.get(i).getItemLocation().toString() == "Sports Center" && initialData.get(i).getItemStatus().toString() == "Found") 
     	    	GYMF = -(GYMF -= 1);
     	    else if (initialData.get(i).getItemLocation().toString() == "Student Commons" && initialData.get(i).getItemStatus().toString() == "Found") 
     	    	STCF = -(STCF -= 1);
     	    else if (initialData.get(i).getItemLocation().toString() == "Welcome Center" && initialData.get(i).getItemStatus().toString() == "Found") 
     	    	WCF = -(WCF -= 1);
     	}
	 	
	 	UIChart.getData().clear();
    	
	 	series1.getData().add(new XYChart.Data<String, Number>("Building A", BAL));
	 	series2.getData().add(new XYChart.Data<String, Number>("Building A", BAF));
	 	series1.getData().add(new XYChart.Data<String, Number>("Building B", BBL));
	 	series2.getData().add(new XYChart.Data<String, Number>("Building B", BBF));
	 	series1.getData().add(new XYChart.Data<String, Number>("Building C", BCL));
	 	series2.getData().add(new XYChart.Data<String, Number>("Building C", BCF));
	 	series1.getData().add(new XYChart.Data<String, Number>("Building D", BDL));
	 	series2.getData().add(new XYChart.Data<String, Number>("Building D", BDF));
	 	series1.getData().add(new XYChart.Data<String, Number>("Auditorium", AUDL));
	 	series2.getData().add(new XYChart.Data<String, Number>("Auditorium", AUDF));
	 	series1.getData().add(new XYChart.Data<String, Number>("Library", LIBL));
	 	series2.getData().add(new XYChart.Data<String, Number>("Library", LIBF));
	 	series1.getData().add(new XYChart.Data<String, Number>("Sports Center", GYML));
	 	series2.getData().add(new XYChart.Data<String, Number>("Sports Center", GYMF));
	 	series1.getData().add(new XYChart.Data<String, Number>("Student Commons", STCL));
	 	series2.getData().add(new XYChart.Data<String, Number>("Student Commons", STCF));
	 	series1.getData().add(new XYChart.Data<String, Number>("Welcome Center", WCL));
	 	series2.getData().add(new XYChart.Data<String, Number>("Welcome Center", WCF));
	 	
	 	UIChart.getData().add(series1);
        UIChart.getData().add(series2);
	}

    @FXML
    void UndoButtonAction(ActionEvent event) {
    	//All locations get displayed
    	locationsList.getSelectionModel().selectFirst();
    	
	    dataTable.getItems().addAll(backupData);
	    backupData.clear();
	    txtMessage.setText("Item(s) is (are) restored.");
	    btnUndo.setDisable(true);
	    
    	//Setting up the bar chart
    	XYChart.Series<String, Number> series1 = new XYChart.Series<>();
        series1.setName("Items lost");       
    	
    	XYChart.Series<String, Number> series2 = new XYChart.Series<>();
        series2.setName("Items found");
        
        UIChart.setAnimated(false);
        
        UIChart.getData().add(series1);
        UIChart.getData().add(series2);
        
    	BAL = BBL = BCL = BDL = AUDL = LIBL = GYML = STCL = WCL = 0;
    	BAF = BBF = BCF = BDF = AUDF = LIBF = GYMF = STCF = WCF = 0;
    	
    	for(int i = 0; i < initialData.size(); i++) {
     		if (initialData.get(i).getItemLocation().toString() == "Building A" && initialData.get(i).getItemStatus().toString() == "Lost") 
     			BAL += 1;
     		else if (initialData.get(i).getItemLocation().toString() == "Building B" && initialData.get(i).getItemStatus().toString() == "Lost") 
     			BBL += 1;
     		else if (initialData.get(i).getItemLocation().toString() == "Building C" && initialData.get(i).getItemStatus().toString() == "Lost") 
     			BCL += 1;
     		else if (initialData.get(i).getItemLocation().toString() == "Building D" && initialData.get(i).getItemStatus().toString() == "Lost") 
     			BDL += 1;
     		else if (initialData.get(i).getItemLocation().toString() == "Auditorium" && initialData.get(i).getItemStatus().toString() == "Lost") 
     			AUDL += 1;
     		else if (initialData.get(i).getItemLocation().toString() == "Library" && initialData.get(i).getItemStatus().toString() == "Lost") 
     			LIBL += 1;
     	    else if (initialData.get(i).getItemLocation().toString() == "Sports Center" && initialData.get(i).getItemStatus().toString() == "Lost") 
     	    	GYML += 1;
     	    else if (initialData.get(i).getItemLocation().toString() == "Student Commons" && initialData.get(i).getItemStatus().toString() == "Lost") 
     		   	STCL += 1;
     	    else if (initialData.get(i).getItemLocation().toString() == "Welcome Center" && initialData.get(i).getItemStatus().toString() == "Lost") 	
     		   	WCL += 1;
     	}
    	
    	for(int i = 0; i < initialData.size(); i++) {
     		if (initialData.get(i).getItemLocation().toString() == "Building A" && initialData.get(i).getItemStatus().toString() == "Found") 
     			BAF += 1;
     		else if (initialData.get(i).getItemLocation().toString() == "Building B" && initialData.get(i).getItemStatus().toString() == "Found") 
     			BBF += 1;
     		else if (initialData.get(i).getItemLocation().toString() == "Building C" && initialData.get(i).getItemStatus().toString() == "Found") 
     			BCF += 1;
     		else if (initialData.get(i).getItemLocation().toString() == "Building D" && initialData.get(i).getItemStatus().toString() == "Found") 
     			BDF += 1;
     		else if (initialData.get(i).getItemLocation().toString() == "Auditorium" && initialData.get(i).getItemStatus().toString() == "Found") 
     			AUDF += 1;
     		else if (initialData.get(i).getItemLocation().toString() == "Library" && initialData.get(i).getItemStatus().toString() == "Found") 
     			LIBF += 1;
     	    else if (initialData.get(i).getItemLocation().toString() == "Sports Center" && initialData.get(i).getItemStatus().toString() == "Found") 
     	    	GYMF += 1;
     	    else if (initialData.get(i).getItemLocation().toString() == "Student Commons" && initialData.get(i).getItemStatus().toString() == "Found") 
     	    	STCF += 1;
     	    else if (initialData.get(i).getItemLocation().toString() == "Welcome Center" && initialData.get(i).getItemStatus().toString() == "Found") 
     	    	WCF += 1;
     	}
	 	
	 	UIChart.getData().clear();
    	
	 	series1.getData().add(new XYChart.Data<String, Number>("Building A", BAL));
	 	series2.getData().add(new XYChart.Data<String, Number>("Building A", BAF));
	 	series1.getData().add(new XYChart.Data<String, Number>("Building B", BBL));
	 	series2.getData().add(new XYChart.Data<String, Number>("Building B", BBF));
	 	series1.getData().add(new XYChart.Data<String, Number>("Building C", BCL));
	 	series2.getData().add(new XYChart.Data<String, Number>("Building C", BCF));
	 	series1.getData().add(new XYChart.Data<String, Number>("Building D", BDL));
	 	series2.getData().add(new XYChart.Data<String, Number>("Building D", BDF));
	 	series1.getData().add(new XYChart.Data<String, Number>("Auditorium", AUDL));
	 	series2.getData().add(new XYChart.Data<String, Number>("Auditorium", AUDF));
	 	series1.getData().add(new XYChart.Data<String, Number>("Library", LIBL));
	 	series2.getData().add(new XYChart.Data<String, Number>("Library", LIBF));
	 	series1.getData().add(new XYChart.Data<String, Number>("Sports Center", GYML));
	 	series2.getData().add(new XYChart.Data<String, Number>("Sports Center", GYMF));
	 	series1.getData().add(new XYChart.Data<String, Number>("Student Commons", STCL));
	 	series2.getData().add(new XYChart.Data<String, Number>("Student Commons", STCF));
	 	series1.getData().add(new XYChart.Data<String, Number>("Welcome Center", WCL));
	 	series2.getData().add(new XYChart.Data<String, Number>("Welcome Center", WCF));
	 	
	 	UIChart.getData().add(series1);
        UIChart.getData().add(series2);
    }

    @FXML
    void urlAUBHAction(ActionEvent event) throws URISyntaxException, IOException {
    	Desktop.getDesktop().browse(new URI("https://aubh.edu.bh/"));
    }

    @FXML
	void initialize() {
    	//Disable fields
    	tfItemName.setDisable(true);
    	dateLostFound.setDisable(true);
    	dropdownLocations.setDisable(true);
    	dropdownStatus.setDisable(true);
    	txtDescription.setDisable(true);
    	
		//Set text notifications to null
    	txtItem.setText(null);
    	
    	//Disable / Enable Save button
    	btnSave.disableProperty().bind(
    			tfItemName.textProperty().isEmpty()
    			.or(txtDescription.textProperty().isEmpty())
    	        .or(dropdownLocations.valueProperty().isNull())
    	        .or(dropdownStatus.valueProperty().isNull())
    	        .or(dateLostFound.valueProperty().isNull()));
    	
    	//Disable / Enable Export button
    	btnExport.disableProperty().bind(Bindings.size(initialData).isEqualTo(0));
    	
    	//Disable / Enable Delete button
    	btnDelete.disableProperty().bind(dataTable.getSelectionModel().selectedItemProperty().isNull());
    	
    	//Disable / Enable Edit button
    	btnEdit.disableProperty().bind(dataTable.getSelectionModel().selectedItemProperty().isNull());
    	
    	//Disable Undo button
    	btnUndo.setDisable(true);
    	
    	//Clock for system notification text
	    Timeline clock = new Timeline(new KeyFrame(Duration.ZERO, e -> 
	    {        
        	txtMessage.setText("Please refer to the instructions for any assistance.");
	    }),
	         new KeyFrame(Duration.seconds(10))
	    );
	    clock.setCycleCount(Animation.INDEFINITE);
	    clock.play();
    	
        //Values in combo boxes and list view
    	String statuses[] = { "Lost", "Found" };
    	String locations[] = { "Building A", "Building B", "Building C", "Building D", "Auditorium", "Library", "Sports Center", "Student Commons", "Welcome Center" };
    	String list[] = { "All Buildings", "Building A", "Building B", "Building C", "Building D", "Auditorium", "Library", "Sports Center", "Student Commons", "Welcome Center" };
    	
    	//Populate combo boxes and list view
    	dropdownLocations.getItems().addAll(locations);
    	dropdownStatus.getItems().addAll(statuses);
    	locationsList.getItems().addAll(list);
    	
	    //Set values to table
    	nameCol.setCellValueFactory(new PropertyValueFactory<Item, String>("itemName"));
    	dateCol.setCellValueFactory(new PropertyValueFactory<Item, LocalDate>("itemDate"));
	    locationCol.setCellValueFactory(new PropertyValueFactory<Item, String>("itemLocation"));
	    statusCol.setCellValueFactory(new PropertyValueFactory<Item, String>("itemStatus"));
	    descriptionCol.setCellValueFactory(new PropertyValueFactory<Item, String>("itemDescription"));
	    dataTable.setItems(initialData);
    	
    	//Disable future dates on the datepicker
    	dateLostFound.setDayCellFactory(param -> new DateCell() {
            @Override
            public void updateItem(LocalDate date, boolean empty) {
                super.updateItem(date, empty);
                setDisable(empty || date.compareTo(LocalDate.now()) > 0 );
            }
        });
    	
    	//Item description settings
    	dataTable.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<Item>() {
			@Override
			public void changed(ObservableValue<? extends Item> val0, Item val1, Item val2) {
				txtItem.setText("Name: "+ initialData.get(dataTable.getSelectionModel().getSelectedIndex()).getItemName().toString() + "\r\n" +
						"Date Lost/Found: "+ initialData.get(dataTable.getSelectionModel().getSelectedIndex()).getItemDate().toString() + "\r\n" +
						"Location: "+ initialData.get(dataTable.getSelectionModel().getSelectedIndex()).getItemLocation().toString() + "\r\n" +
						"Status: "+ initialData.get(dataTable.getSelectionModel().getSelectedIndex()).getItemStatus().toString() + "\r\n" +
						"Description: "+ initialData.get(dataTable.getSelectionModel().getSelectedIndex()).getItemDescription().toString());
				
			}});
    	
    	//Filtering the table with listview
	    locationsList.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<String>() {
			@Override
			public void changed(ObservableValue<? extends String> var0, String var1, String var2){
				txtItem.setText(null);
				dataTable.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
				if(locationsList.getSelectionModel().getSelectedItem().toString() != "All Buildings" && !initialData.isEmpty())
				{
					FilteredList<Item> filteredData = new FilteredList<Item>(initialData, e -> e.getItemLocation().toString() == locationsList.getSelectionModel().getSelectedItem().toString());
					dataTable.setItems(filteredData);
					
					//Description Area for filtered data
					 dataTable.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<Item>() {
						 @Override
							public void changed(ObservableValue<? extends Item> arg0, Item arg1, Item arg2) {
							 txtItem.setText("Name: "+ filteredData.get(dataTable.getSelectionModel().getSelectedIndex()).getItemName().toString() + "\r\n" +
										"Date Lost/Found: "+ filteredData.get(dataTable.getSelectionModel().getSelectedIndex()).getItemDate().toString() + "\r\n" +
										"Location: "+ filteredData.get(dataTable.getSelectionModel().getSelectedIndex()).getItemLocation().toString() + "\r\n" +
										"Status: "+ filteredData.get(dataTable.getSelectionModel().getSelectedIndex()).getItemStatus().toString() + "\r\n" +
										"Description: "+ filteredData.get(dataTable.getSelectionModel().getSelectedIndex()).getItemDescription().toString());
							}});
				}
				else 
					dataTable.setItems(initialData);
			}});
    	
    	//Set current date & time
    	AnimationTimer timer = new AnimationTimer() {
    	    @Override
    	    public void handle(long now) {
    	    	txtDateTime.setText(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
    	    }
    	};
    	timer.start();
    	
    	//Set bar chart
    	UIChart.setTitle("Lost & Found items in AUBH per location"); 
    	CategoryAxis xAxis = new CategoryAxis();   

    	xAxis.setLabel("Locations in AUBH");
    	
    	NumberAxis yAxis = new NumberAxis(); 
    	yAxis.setLabel("Number of listed items");
    	
    	//API configuration
    	try {
    		URL url = new URL("https://api.weatherbit.io/v2.0/current?lat=26&lon=50.33&key=6bd766fd2ddd4822bf78e7d71909a1bf&include=minutely");
    		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
    			conn.setRequestMethod("GET");
    			conn.connect();
    		
    		//Check if connection is made
    		int responseCode = conn.getResponseCode();
    		if (responseCode != 200) {
    			throw new RuntimeException("HttpResponseCode: " + responseCode);
    		} else {
    			StringBuilder informationSystem = new StringBuilder();
    			Scanner scanner = new Scanner(url.openStream());
    			while (scanner.hasNext()) {
    				informationSystem.append(scanner.nextLine());
    			}

    		String informationSystem1 = informationSystem.substring(0);
    		int x = informationSystem1.indexOf("temp\":")+6;
    		int y = informationSystem1.lastIndexOf(",\"aqi\"");
    		String informationSystem2 = informationSystem1.substring(x, y);
    		apiPlace.setText("Current temperature in Bahrain: " + informationSystem2 + "°C" );
    		}
    	}catch(Exception e){
    		e.printStackTrace();
    	}
    }
}
