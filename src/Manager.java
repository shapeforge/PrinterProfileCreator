import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.CheckBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.WindowEvent;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.Observable;
import java.util.ResourceBundle;
import java.nio.file.StandardCopyOption.*;

public class Manager implements Initializable {
    @FXML
    VBox Installed;
    @FXML
    VBox Saved;



    String appdata ;
    File instalDirectory;
    File saveDirectory;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        appdata = System.getenv("APPDATA");
        if(appdata == null)
        appdata = appdata == null ? "./" : appdata;
        instalDirectory = new File(appdata+"\\IceSL\\icesl-printers\\fff");
        saveDirectory = new File(appdata+"\\IceSL\\icesl-printers_saved\\fff");
        if(!saveDirectory.exists()) {
            new File(appdata + "\\IceSL\\icesl-printers_saved").mkdirs();
            new File(appdata + "\\IceSL\\icesl-printers_saved\\fff").mkdirs();
        }
        load();
    }


    @FXML
    void save(){

        for (Object o : Installed.getChildren()){
            if(o.getClass() == CheckBox.class){
                CheckBox ch = (CheckBox)o;
                if(ch.isSelected()){
                    try {
                        Files.move(new File(appdata + "\\IceSL\\icesl-printers\\fff\\"+ch.getText()).toPath(), new File(appdata + "\\IceSL\\icesl-printers_saved\\fff\\"+ch.getText()).toPath(), StandardCopyOption.REPLACE_EXISTING);
                    } catch (IOException e) {
                        System.out.println("Allready saved printer" );
                    }
                }

            }
        }


        load();
    }

    @FXML
    void reload(){

        for (Object o : Saved.getChildren()){
            if(o.getClass() == CheckBox.class){
                CheckBox ch = (CheckBox)o;
                if(ch.isSelected()){
                    try {
                        Files.move(new File(appdata + "\\IceSL\\icesl-printers_saved\\fff\\"+ch.getText()).toPath(), new File(appdata + "\\IceSL\\icesl-printers\\fff\\"+ch.getText()).toPath(), StandardCopyOption.REPLACE_EXISTING);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

            }
        }


        load();
    }

    @FXML
    void add(){
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/Generator.fxml"));
            Parent root1 = (Parent) fxmlLoader.load();
            Stage stage = new Stage();
            stage.setScene(new Scene(root1));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    void load(){
        Installed.getChildren().remove(0, Installed.getChildren().size());
        Saved.getChildren().remove(0, Saved.getChildren().size());
        File[] fList = instalDirectory.listFiles();
        if(fList != null)
            for (File file : fList) {
                if (file.isDirectory()) {
                    CheckBox ch = new CheckBox();
                    ch.setText(file.getName());
                    Installed.getChildren().add(ch);
                }
            }

        fList = saveDirectory.listFiles();
        if(fList != null)
            for (File file : fList) {
                if (file.isDirectory()) {
                    CheckBox ch = new CheckBox();
                    ch.setText(file.getName());
                    Saved.getChildren().add(ch);
                }
            }
    }
}
