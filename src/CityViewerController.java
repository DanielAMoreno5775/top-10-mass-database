import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.paint.Color;
import java.util.Iterator;


public class CityViewerController {
    //create variables for the different buttons, text fields, labels, and combination boxes
    @FXML
    private ComboBox<City> cityCombo;
    @FXML
    private Label fileReadMsg;
    @FXML
    private TextField filenameInput;
    @FXML
    private ComboBox<State> stateCombo;
    @FXML
    private Label distanceLabel;
    @FXML
    private Label timeLbl;
    @FXML
    private Label boxOfZipcodeNum;
    @FXML
    private Label numCitiesLbl;

    //create local data fields
    private OrderedAddOnce<State> stateList = new OrderedAddOnce <>();
    
    //read in the data from the file based on the user's input
    @FXML
    void $readFileButton(ActionEvent event) {
        String fileName = filenameInput.getText();
        File dataFile = null;
        Scanner inputFileData = null;
        String word = "";
        int numOfLinesRead = 0;
        
        //empty out all of the lists so that the list won't contain multiple copies even if you open the file multiple times
        distanceLabel.setText("");
        timeLbl.setText("");
        fileReadMsg.setText("");
        boxOfZipcodeNum.setText("");
        stateList = new OrderedAddOnce <>();
        stateCombo.getSelectionModel().clearSelection();
        stateCombo.setValue(null);
        stateCombo.getItems().clear();
        stateCombo.getItems().removeAll();
        stateCombo.setVisibleRowCount(1);
        cityCombo.getSelectionModel().clearSelection();
        cityCombo.setValue(null);
        cityCombo.getItems().clear();
        cityCombo.getItems().removeAll();
        cityCombo.setVisibleRowCount(1);
        
        try {
            //ensure that the user input something
            if (fileName.equals("")) {
                throw new FileNotFoundException("You didn't input anything");
            }
            
            dataFile = new File(fileName);
            //prevent issues involving the file
            if (!(dataFile.exists()) || !(dataFile.canRead()) || (dataFile.isDirectory())) {
                throw new FileNotFoundException("File doesn't exist, can't be read, or is a directory");
            }
            else {
                //read the file
                inputFileData = new Scanner (dataFile);
                while (inputFileData.hasNextLine()) {
                    //store the next line
                    String line = inputFileData.nextLine();
                    Scanner lineScanner = new Scanner(line);
                    lineScanner.useDelimiter(",");
                    numOfLinesRead += 1;
                    //ignore first line of column names
                    if (numOfLinesRead > 1) {
                        //split up the words within the line
                        int zip = lineScanner.nextInt();
                        String cityName = lineScanner.next();
                        String stateName = lineScanner.next();
                        double latitude = lineScanner.nextDouble();
                        double longitude = lineScanner.nextDouble();
                        int timezone = lineScanner.nextInt();
                        String daylightStr = lineScanner.next();
                        boolean yesDaylight = false;
                        if (daylightStr.charAt(0) == '1') {
                            yesDaylight = true;
                        }
                        //store everything in a city and throw it in the group
                        City tempCity = new City(cityName,latitude,longitude,timezone,yesDaylight);
                        State tempState = stateList.addOnce(new State(stateName));
                        tempState.addCity(tempCity, zip);
                    }
                }
                inputFileData.close();
                //set the message color to a shade of green that I can see
                fileReadMsg.setTextFill(Color.web("#4BB543"));
                fileReadMsg.setText("The cities have been read");
                //acquire and store the list of all city names in the combination box
                stateCombo.setVisibleRowCount(10);
                Iterator<State> stateIter = stateList.iterator();
                int numOfZipcodes = 0;
                while (stateIter.hasNext()) {
                    State nextState = stateIter.next();
                    stateCombo.getItems().add(nextState);
                    numOfZipcodes += nextState.getNumOfZipcodes();
                }
                //display the number of unique zip codes within the file
                boxOfZipcodeNum.setTextFill(Color.web("#4BB543"));
                boxOfZipcodeNum.setText(String.valueOf(numOfZipcodes) + " Zip Codes");
            }
        }
        //can be thrown by inputFileData = new Scanner (dataFile);
        catch (FileNotFoundException e){
            cityCombo.setVisibleRowCount(1);
            stateCombo.setVisibleRowCount(1);
            System.out.println("Scanner error opening the file " + fileName);
            System.out.println(e.getMessage());
            //set the message color to a more friendly red
            fileReadMsg.setTextFill(Color.web("#FF003C"));
            fileReadMsg.setText("The file was unsuccessfully read");
        } 
        //can be thrown by dataFile.exists() or dataFile.canRead()
        catch (SecurityException e) {
            cityCombo.setVisibleRowCount(1);
            stateCombo.setVisibleRowCount(1);
            System.out.println("File error opening the file " + fileName);
            System.out.println(e.getMessage());
            //set the message color to a more friendly red
            fileReadMsg.setTextFill(Color.web("#FF003C"));
            fileReadMsg.setText("The file was unsuccessfully read");
        }
    }
    
    @FXML
    void $readCityComboBox(ActionEvent event) {
        if (cityCombo.getValue() != null) {
            distanceLabel.setText(String.valueOf(cityCombo.getValue().distanceToFortWorth()) + " Miles");
        } else {
            distanceLabel.setText("");
        }
    }

    @FXML
    void $readStateComboBox(ActionEvent event) {
        //reset the city combo box
        cityCombo.getSelectionModel().clearSelection();
        cityCombo.setValue(null);
        cityCombo.getItems().clear();
        cityCombo.getItems().removeAll();
        cityCombo.setVisibleRowCount(1);
        numCitiesLbl.setText("");
        timeLbl.setText("");
        
        //fill the city combo box
        if (stateCombo.getValue() != null) {
           //create an iterator object to iterate through it
           Iterator<City> cityIter = stateCombo.getValue().getCityList().iterator();
           City[] listOfBiggestCities = new City[10];
           int numOfFoundCities = 0;
           
           //alter the size of the dropdown depending on the number of cities for that state
           int numOfCities = stateCombo.getValue().getNumOfCities();
           numCitiesLbl.setTextFill(Color.web("#483D8B"));
           numCitiesLbl.setText(String.valueOf(numOfCities) + " Cities");
           if (numOfCities < 10 && numOfCities > 0) {
               cityCombo.setVisibleRowCount(numOfCities);
           } else if (numOfCities <= 0 ) {
               cityCombo.setVisibleRowCount(1);
           } else {
               cityCombo.setVisibleRowCount(10);
           }
           
           //find 10 biggest cities based on number of zipcodes, populate the combo box with them, and display the time taken to do so
           long startTime = System.nanoTime();
           while (cityIter.hasNext()) {
                City tempCity = cityIter.next();
                if (numOfFoundCities < 10) {
                    listOfBiggestCities[numOfFoundCities] = tempCity;
                    numOfFoundCities++;
                } else {
                    for (int i = 0; i < 10; i++) {
                        if (listOfBiggestCities[i].getNumOfZipcodes() < tempCity.getNumOfZipcodes()) {
                            listOfBiggestCities[i] = tempCity;
                            break;
                        }
                    }
                }
           }
           long endTime = System.nanoTime();
           for (City city : listOfBiggestCities) {
                cityCombo.getItems().add(city);
           }
           String formattedTime = String.format("%,d NanoSeconds",(endTime - startTime));
           timeLbl.setTextFill(Color.web("#483D8B"));
           timeLbl.setText(formattedTime);
        }
    }
}
