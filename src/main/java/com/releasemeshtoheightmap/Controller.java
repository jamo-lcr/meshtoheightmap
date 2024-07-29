package com.releasemeshtoheightmap;
import java.io.File;


import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.application.Platform;


public class Controller  {
    @FXML
    private Button btnStartCalculation;
    @FXML
    private Button btnLoad;
    @FXML
    private Button btnSelect;
    @FXML
    private Button btnSaveAsNew;
    @FXML
    private Button btnLoadMesh;
    @FXML
    private TextField tfField1;
    @FXML
    private TextField tfField2;
    @FXML
    private TextField tfField3;
    @FXML
    private TextField tfField4;
    @FXML
    private TextField tfField5;
    @FXML
    private TextField tfField6;
    @FXML
    private TextField tfField7;
    @FXML
    private TextField tfField8;
    @FXML
    private CheckBox checkBox1;
    @FXML
    private CheckBox checkBox2;
    @FXML
    private CheckBox checkBox3;
    @FXML
    private Text text2;
    @FXML
    private ProgressBar ProgressBar;

    private Stage mainWindow;


    public void setMainWindow(Stage mainWindow) {
        this.mainWindow = mainWindow;
    }

    @FXML
    public void initialize() {
        // satrtsettings
        checkBox2.setVisible(false);
        checkBox3.setVisible(false);
        text2.setVisible(false);
        tfField3.setText("100");
        tfField4.setText("100");

        checkBox1.selectedProperty().addListener((obs, wasSelected, isNowSelected) -> {
            CheckBox[] box = { checkBox2, checkBox3 };
            updateCheckBoxVisibility(checkBox1, box);
            updateTextVisibility(checkBox1, text2);
        });
        checkBox2.selectedProperty().addListener((obs, wasSelected, isNowSelected) -> {
            if (checkBox2.isSelected() == true) {
                checkBox3.setSelected(false);
            }
        });
        checkBox3.selectedProperty().addListener((obs, wasSelected, isNowSelected) -> {
            if (checkBox3.isSelected() == true) {
                checkBox2.setSelected(false);
            }
        });
        btnLoad.setOnAction(event -> {
            String filePath = openFileChooser("*.set");
            if (filePath != null) {
                System.out.println("Selected file path: " + filePath);
                tfField1.setText(filePath);
                LoadSettingsfile(filePath);
                // Handle the file path as needed
            } else {
                System.out.println("File selection canceled.");
            }
        });
        btnSelect.setOnAction(event -> {
            String filePath = openFolderChooser()+"\\";
            if (filePath != null) {
                tfField5.setText(filePath);

                // Handle the file path as needed
            } else {
                System.out.println("File selection canceled.");
            }
        });
        btnLoadMesh.setOnAction(event -> {
            String filePath = openFileChooser("*.obj");
            if (filePath != null) {
                tfField2.setText(filePath);
                Checkobj(filePath);
                // Handle the file path as needed
            } else {
                System.out.println("File selection canceled.");
            }
        });
        btnSaveAsNew.setOnAction(event -> {
            String filePath = openSaveFileChooser("*.set");
            if (filePath != null) {
                tfField1.setText(filePath);
                System.out.println("File will be saved to: " + filePath);
                Savesettingsfile(filePath);
                // Handle file saving, e.g., write data to file
            } else {
                System.out.println("File saving canceled.");
            }
        });
        btnStartCalculation.setOnAction(event -> {
            int mode = 0;
            if (checkBox1.isSelected() == true) {
                mode = 1;
            }
            if (checkBox2.isSelected() == true) {
                mode = 2;
            }
            if (checkBox3.isSelected() == true) {
                mode = 3;
            }
            final int calcmode = mode;
            Thread thread = new Thread(() -> BetaApp.startcalculation(getcurrentsettings(), calcmode));
            thread.start();
            try {
                // Wait for the thread to finish
                thread.join();
                
                // Code to execute after the thread has finished
                System.out.println("Thread has finished. Executing subsequent code.");
                ProgressBar.setProgress(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        });
        
        restrictToIntegerInput(tfField3);
        restrictToIntegerInput(tfField4);
        restrictToVector3Input(tfField6);
        restrictToVector3Input(tfField7);
        restrictToVector3Input(tfField8);


        

    }


    private void updateCheckBoxVisibility(CheckBox parent, CheckBox[] checkBoxes) {
        // Example of how to toggle visibility based on checkBox1 state
        boolean isCheckBoxSelected = parent.isSelected();
        for (int i = 0; i < checkBoxes.length; i++) {
            checkBoxes[i].setVisible(isCheckBoxSelected);
            checkBoxes[i].setManaged(isCheckBoxSelected);
        }
    }

    private void updateTextVisibility(CheckBox parent, Text text) {
        boolean isCheckBoxSelected = parent.isSelected();
        text.setVisible(isCheckBoxSelected);

    }

    private String openFileChooser(String Filetype) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open Settings File");

        // Optionally, set extension filters
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("SelectedFiletype", Filetype),
                new FileChooser.ExtensionFilter("All Files", "*.*"));
        File currentDirectory = new File(System.getProperty("user.dir"));
        fileChooser.setInitialDirectory(currentDirectory);
        // Show the open file dialog
        File selectedFile = fileChooser.showOpenDialog(mainWindow);

        if (selectedFile != null) {
            System.out.println("File selected: " + selectedFile.getAbsolutePath());
            return selectedFile.getAbsolutePath();
            // Handle the file (e.g., read content, display, etc.)
        } else {
            System.out.println("File selection canceled.");
        }
        return null;
    }

    private String openFolderChooser() {
        DirectoryChooser directoryChooser = new DirectoryChooser();
        directoryChooser.setTitle("Select Folder");

        // Optionally, set initial directory
        File currentDirectory = new File(System.getProperty("user.dir"));
        directoryChooser.setInitialDirectory(currentDirectory);

        // Show the directory chooser dialog
        File selectedDirectory = directoryChooser.showDialog(mainWindow);

        if (selectedDirectory != null) {
            System.out.println("Folder selected: " + selectedDirectory.getAbsolutePath());
            return selectedDirectory.getAbsolutePath();
        } else {
            System.out.println("Folder selection canceled.");
            return null;
        }
    }

    private String openSaveFileChooser(String Filetype) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save File");

        // Optionally, set initial file name and extension filters
        fileChooser.setInitialFileName("Untitled");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("SelectedFiletype", Filetype),
                new FileChooser.ExtensionFilter("All Files", "*.*"));

        // Set initial directory to the current working directory
        File currentDirectory = new File(System.getProperty("user.dir"));
        fileChooser.setInitialDirectory(currentDirectory);

        // Show the save file dialog
        File file = fileChooser.showSaveDialog(mainWindow);

        if (file != null) {
            return file.getAbsolutePath(); // Return the absolute path
        } else {
            return null; // Return null if the user cancels
        }
    }

    private void restrictToIntegerInput(TextField textField) {
        textField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d*")) {
                textField.setText(newValue.replaceAll("[^\\d]", ""));
            }
        });
    }

    private void restrictToFloatInput(TextField textField) {
        textField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("^-?\\d*(\\.\\d*)?$")) {
                textField.setText(oldValue);
            }
        });
    }

    private void restrictToVector3Input(TextField textField) {
        textField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("^(-?\\d*(\\.\\d+)?\\s+){2}-?\\d*(\\.\\d+)?$")) {
                textField.setText(oldValue);
            }
        });
    }

    private void Savesettingsfile(String path) {
        Settings.Savesettings(path, getcurrentsettings());

    }

    private Settings getcurrentsettings() {
        Settings settings = new Settings(getFile(tfField2.getText()), tfField5.getText(),
                Integer.parseInt(tfField3.getText()), Integer.parseInt(tfField4.getText()), checkBox2.isSelected(),
                Vector3.parse(tfField6.getText()), Vector3.parse(tfField8.getText()),
                Vector3.parse(tfField7.getText()));
        return settings;
    }

    private void LoadSettingsfile(String path) {
        Settings settings = Settings.Loadsettingsfromfile(path);
        Loadcurrentsettings(settings);

    }

    private void Loadcurrentsettings(Settings settings) {
        tfField2.setText(settings.MeshFile.getAbsolutePath());
        tfField3.setText("" + settings.TexturesizeX);
        tfField4.setText("" + settings.TexturesizeY);
        tfField6.setText(settings.scale.getString());
        tfField7.setText(settings.position.getString());
        tfField8.setText(settings.rotation.getString());
        tfField5.setText("" + settings.OutputfilePath);
    }

    private void Checkobj(String path) {

    }

    private File getFile(String filePath) {
        File file = new File(filePath);

        if (file.exists() && file.isFile()) {
            return file;
        } else {
            System.err.println("File does not exist or is not a file: " + filePath);
            return null;
        }
    }

    public void updateProgressBar() {
        Platform.runLater(() -> {
            int max = BetaApp.maxProgress;
            int current = BetaApp.progress;
            if (max > 0) {
                double progress = (double) current / max;
                ProgressBar.setProgress(progress);
            } else {
                ProgressBar.setProgress(0);
            }
        });
    }
}