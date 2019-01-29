import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;


public class GeneratorController implements Initializable {

    @FXML
    TextField PrinterName;
    @FXML
    TextField BSX;
    @FXML
    TextField BSY;
    @FXML
    TextField BSZ;
    @FXML
    TextField ZHMIN;
    @FXML
    TextField ZHMAX;
    @FXML
    TextField FD;
    @FXML
    TextField ND;
    @FXML
    TextField ET;
    @FXML
    TextField ETMIN;
    @FXML
    TextField ETMAX;
    @FXML
    TextField BT;
    @FXML
    TextField BTMIN;
    @FXML
    TextField BTMAX;
    @FXML
    TextField PS;
    @FXML
    TextField PSMIN;
    @FXML
    TextField PSMAX;
    @FXML
    TextField FLS;
    @FXML
    TextField FLSMIN;
    @FXML
    TextField FLSMAX;
    @FXML
    TextField PERS;
    @FXML
    TextField PERSMIN;
    @FXML
    TextField PERSMAX;
    @FXML
    TextArea Header;
    @FXML
    TextArea Footer;
    @FXML
    TextField PathField;
    @FXML
    Label userMessage;


    @FXML
    public void Generate(){
        checkemptyFields();

        String path = PathField.getText();
        path = path + PrinterName.getText();

        boolean success = (new File(path)).mkdirs();
        if (!success) {
            print("Folder path is not valid");
            return;
        }
        (new File(path+ "\\profiles")).mkdirs();
        (new File(path+ "\\materials")).mkdirs();
        String features = "version = 2 \n\n"+
                "bed_size_x_mm = " + BSX.getText() + "\n" +
                "bed_size_y_mm = " + BSY.getText() + "\n" +
                "bed_size_z_mm = " + BSZ.getText() + "\n" +
                "\n" +
                "nozzle_diameter_mm = " + ND.getText() +
                "\n\nextruder_count = 1\n\nz_offset   = 0.0\n\npriming_mm_per_sec = 10 \n" +
                "z_layer_height_mm_min = " + ZHMIN.getText()+ "\n" +
                "z_layer_height_mm_max = " + ZHMAX.getText()+ "\n\n" +
                "print_speed_mm_per_sec_min = " + PSMIN.getText() + "\n" +
                "print_speed_mm_per_sec_max = " +PSMAX.getText() + "\n\n" +
                "bed_temp_degree_c = " + BT.getText() +"\n"+
                "bed_temp_degree_c_min = " + BTMIN.getText() + "\n"+
                "bed_temp_degree_c_max = " + BTMAX.getText() + "\n\n" +
                "perimeter_print_speed_mm_per_sec_min = "+PERSMIN.getText() +"\n" +
                "perimeter_print_speed_mm_per_sec_max = "+PERSMAX.getText() +"\n\n" +
                "first_layer_print_speed_mm_per_sec = " +FLS.getText() + "\n" +
                "first_layer_print_speed_mm_per_sec_min =" +FLSMIN.getText() + "\n"+
                "first_layer_print_speed_mm_per_sec_max =" +FLSMAX.getText() + "\n\n" +
                "for i=0,63,1 do" + "\n" +
                "  _G['filament_diameter_mm_'..i] =" + FD.getText() + "\n" +
                "  _G['filament_priming_mm_'..i] = 4.0\n" +
                "  _G['extruder_temp_degree_c_' ..i] =" + ET.getText() + "\n" +
                "  _G['extruder_temp_degree_c_'..i..'_min'] =" +ETMIN.getText() + "\n" +
                "  _G['extruder_temp_degree_c_'..i..'_max'] =" +ETMAX.getText() + "\n" +
                "  _G['extruder_mix_count_'..i] = 1\n"+
                "end\n"
                ;


        float layermax = Float.parseFloat(ZHMAX.getText());
        float layermin = Float.parseFloat(ZHMIN.getText());

        float layer = layermax > 2*layermin? layermax/2.0f : layermin;

        String medium = "name_en = \"Standard quality\"\n" +
                "name_es = \"Estàndar calidad\"\n" +
                "name_fr = \"Qualité standard\"\n" +
                "\n" +
                "z_layer_height_mm = "+layer+"\n" +
                "\n" +
                "print_speed_mm_per_sec="+PS.getText()+"\n" +
                "first_layer_print_speed_mm_per_sec="+FLS.getText()+"\n" +
                "perimeter_print_speed_mm_per_sec="+PERS.getText()+"\n" +
                "travel_speed_mm_per_sec=80\n" +
                "priming_mm_per_sec=10\n" +
                "\n" +
                "add_raft=false\n" +
                "raft_spacing=1.0\n" +
                "\n" +
                "gen_supports=true\n" +
                "support_extruder=0\n" +
                "\n" +
                "add_brim=true\n" +
                "brim_distance_to_print=1.0\n" +
                "brim_num_contours=3\n" +
                "\n" +
                "extruder_0=0\n" +
                "num_shells_0=3\n" +
                "cover_thickness_mm_0=2\n" +
                "print_perimeter_0=true\n" +
                "infill_percentage_0=20\n" +
                "flow_multiplier_0=1.0\n" +
                "speed_multiplier_0=1.0\n" +
                "bed_temp_degree_c = " +BT.getText() + "\n" +
                "\n" +
                "process_thin_features=false\n";

        String header = Header.getText();
        String footer = Footer.getText();



        write(path+"\\features.lua", features);
        write(path+"\\footer.gcode", footer);
        write(path+"\\header.gcode", header);
        write(path+"\\printer.lua", printer);
        write(path+"\\materials\\abs.lua", ABS);
        write(path+"\\materials\\pla.lua", PLA);
        write(path+"\\materials\\tpu.lua", TPU);
        write(path+"\\materials\\petg.lua", PETG);
        write(path+"\\profiles\\standard.lua",medium );

        print("Profile successfully generated");
    }

    private void checkemptyFields(){
        if(PrinterName.getText().length() == 0) PrinterName.setText("nonameprinter");
        if(BSX.getText().length() == 0) BSX.setText("0");
        if(BSY.getText().length() == 0) BSY.setText("0");
        if(BSZ.getText().length() == 0) BSZ.setText("0");
        if(ZHMIN.getText().length() == 0) ZHMIN.setText("0");
        if(ZHMAX.getText().length() == 0) ZHMAX.setText("0");
        if(ET.getText().length() == 0) ET.setText("0");
        if(ETMAX.getText().length() == 0) ETMAX.setText("0");
        if(ETMIN.getText().length() == 0) ETMIN.setText("0");
        if(BT.getText().length() == 0) BT.setText("0");
        if(BTMIN.getText().length() == 0) BTMIN.setText("0");
        if(BTMAX.getText().length() == 0) BTMAX.setText("0");
        if(FD.getText().length() == 0) FD.setText("0");
        if(ND.getText().length() == 0) ND.setText("0");
        if(PS.getText().length() == 0) PS.setText("0");
        if(PSMIN.getText().length() == 0) PSMIN.setText("0");
        if(PSMAX.getText().length() == 0) PSMAX.setText("0");
        if(PERS.getText().length() == 0) PERS.setText("0");
        if(PERSMIN.getText().length() == 0) PERSMIN.setText("0");
        if(PERSMAX.getText().length() == 0) PERSMAX.setText("0");
        if(FLS.getText().length() == 0) FLS.setText("0");
        if(FLSMIN.getText().length() == 0) FLSMIN.setText("0");
        if(FLSMAX.getText().length() == 0) FLSMAX.setText("0");
    }


    private void write(String path, String footer) {
        try {
            FileOutputStream file = new FileOutputStream(path);
            file.write(footer.getBytes());
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void print(String s){
        userMessage.setText(s);
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {

        String appdata = System.getenv("APPDATA");

        if(appdata == null) print("IceSL Folder not found please fill the \"...\"");
        appdata = appdata == null ? "..." : appdata;

        PathField.setText(appdata+"\\IceSL\\icesl-printers\\fff\\");

        PrinterName.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue,
                                String newValue) {
                if (!newValue.matches("^[a-zA-Z0-9_]*")) {
                    PrinterName.setText(oldValue);
                }
            }
        });

        BSX.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue,
                                String newValue) {
                if (!newValue.matches("\\d*")) {
                    BSX.setText(newValue.replaceAll("[^\\d]", ""));
                }
            }
        });



        BSY.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue,
                                String newValue) {
                if (!newValue.matches("\\d*")) {
                    BSY.setText(newValue.replaceAll("[^\\d]", ""));
                }
            }
        });

        BSZ.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue,
                                String newValue) {
                if (!newValue.matches("\\d*")) {
                    BSY.setText(newValue.replaceAll("[^\\d]", ""));
                }
            }
        });

        ET.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue,
                                String newValue) {
                if (!newValue.matches("\\d*")) {
                    ET.setText(newValue.replaceAll("[^\\d]", ""));
                }
            }
        });

        ETMIN.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue,
                                String newValue) {
                if (!newValue.matches("\\d*")) {
                    ETMIN.setText(newValue.replaceAll("[^\\d]", ""));
                }
            }
        });

        ETMAX.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue,
                                String newValue) {
                if (!newValue.matches("\\d*")) {
                    ETMAX.setText(newValue.replaceAll("[^\\d]", ""));
                }
            }
        });

        BT.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue,
                                String newValue) {
                if (!newValue.matches("\\d*")) {
                    ET.setText(newValue.replaceAll("[^\\d]", ""));
                }
            }
        });

        BTMIN.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue,
                                String newValue) {
                if (!newValue.matches("\\d*")) {
                    ETMIN.setText(newValue.replaceAll("[^\\d]", ""));
                }
            }
        });

        BTMAX.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue,
                                String newValue) {
                if (!newValue.matches("\\d*")) {
                    ETMAX.setText(newValue.replaceAll("[^\\d]", ""));
                }
            }
        });


        PS.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue,
                                String newValue) {
                if (!newValue.matches("\\d*")) {
                    PS.setText(newValue.replaceAll("[^\\d]", ""));
                }
            }
        });

        PSMIN.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue,
                                String newValue) {
                if (!newValue.matches("\\d*")) {
                    PSMIN.setText(newValue.replaceAll("[^\\d]", ""));
                }
            }
        });

        PSMAX.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue,
                                String newValue) {
                if (!newValue.matches("\\d*")) {
                    PSMAX.setText(newValue.replaceAll("[^\\d]", ""));
                }
            }
        });


        FLS.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue,
                                String newValue) {
                if (!newValue.matches("\\d*")) {
                    FLS.setText(newValue.replaceAll("[^\\d]", ""));
                }
            }
        });

        FLSMIN.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue,
                                String newValue) {
                if (!newValue.matches("\\d*")) {
                    FLSMIN.setText(newValue.replaceAll("[^\\d]", ""));
                }
            }
        });

        FLSMAX.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue,
                                String newValue) {
                if (!newValue.matches("\\d*")) {
                    FLSMAX.setText(newValue.replaceAll("[^\\d]", ""));
                }
            }
        });

        PERS.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue,
                                String newValue) {
                if (!newValue.matches("\\d*")) {
                    PERS.setText(newValue.replaceAll("[^\\d]", ""));
                }
            }
        });

        PERSMIN.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue,
                                String newValue) {
                if (!newValue.matches("\\d*")) {
                    PERSMIN.setText(newValue.replaceAll("[^\\d]", ""));
                }
            }
        });

        PERSMAX.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue,
                                String newValue) {
                if (!newValue.matches("\\d*")) {
                    PERSMIN.setText(newValue.replaceAll("[^\\d]", ""));
                }
            }
        });

        ZHMAX.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue,
                                String newValue) {
                if (!newValue.matches("[+-]?(([0-9]+([.][0-9]*)?)|[.][0-9]+)") && newValue.length() >0) {
                    ZHMAX.setText(oldValue);
                }
            }
        });

        ZHMIN.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue,
                                String newValue) {
                if (!newValue.matches("[+-]?(([0-9]+([.][0-9]*)?)|[.][0-9]+)") && newValue.length() >0) {
                    ZHMIN.setText(oldValue);
                }
            }
        });

        FD.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue,
                                String newValue) {
                if (!newValue.matches("[+-]?(([0-9]+([.][0-9]*)?)|[.][0-9]+)") && newValue.length() >0) {
                    FD.setText(oldValue);
                }
            }
        });

        ND.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue,
                                String newValue) {
                if (!newValue.matches("[+-]?(([0-9]+([.][0-9]*)?)|[.][0-9]+)") && newValue.length() >0) {
                    ND.setText(oldValue);
                }
            }
        });
    }

    String ABS = "name_en = \"ABS\"\n" +
            "name_es = \"ABS\"\n" +
            "name_fr = \"ABS\"\n" +
            "\n" +
            "extruder_temp_degree_c_0 = 230\n" +
            "filament_diameter_mm_0 = 1.75\n" +
            "filament_priming_mm_0 = 3.0\n" +
            "\n" +
            "bed_temp_degree_c = 100";

    String PLA = "name_en = \"PLA\"\n" +
            "name_es = \"PLA\"\n" +
            "name_fr = \"PLA\"\n" +
            "\n" +
            "extruder_temp_degree_c_0 = 210\n" +
            "filament_diameter_mm_0 = 1.75\n" +
            "filament_priming_mm_0 = 3.0\n" +
            "\n" +
            "bed_temp_degree_c = 65\n";

    String PETG = "name_en = \"PETG\"\n" +
            "name_es = \"PETG\"\n" +
            "name_fr = \"PETG\"\n" +
            "\n" +
            "extruder_temp_degree_c_0 = 235\n" +
            "filament_diameter_mm_0 = 1.75\n" +
            "filament_priming_mm_0 = 3.0\n" +
            "\n" +
            "bed_temp_degree_c = 70";

    String TPU = "name_en = \"TPU\"\n" +
            "name_es = \"TPU\"\n" +
            "name_fr = \"TPU\"\n" +
            "\n" +
            "extruder_temp_degree_c_0 = 235\n" +
            "filament_diameter_mm_0 = 1.75\n" +
            "filament_priming_mm_0 = 3.0\n" +
            "\n" +
            "bed_temp_degree_c = 70\n";

    String printer = "-- Generic reprap\n" +
            "\n" +
            "version = 2\n" +
            "\n" +
            "function comment(text)\n" +
            "  output('; ' .. text)\n" +
            "end\n" +
            "\n" +
            "extruder_e = 0\n" +
            "extruder_e_restart = 0\n" +
            "\n" +
            "function header()\n" +
            "  h = file('header.gcode')\n" +
            "  h = h:gsub( '<TOOLTEMP>', extruder_temp_degree_c[extruders[0]] )\n" +
            "  h = h:gsub( '<HBPTEMP>', bed_temp_degree_c )\n" +
            "  output(h)\n" +
            "end\n" +
            "\n" +
            "function footer()\n" +
            "  output(file('footer.gcode'))\n" +
            "end\n" +
            "\n" +
            "function layer_start(zheight)\n" +
            "  comment('<layer>')\n" +
            "  output('G1 Z' .. f(zheight))\n" +
            "end\n" +
            "\n" +
            "function layer_stop()\n" +
            "  extruder_e_restart = extruder_e\n" +
            "  output('G92 E0')\n" +
            "  comment('</layer>')\n" +
            "end\n" +
            "\n" +
            "function retract(extruder,e)\n" +
            "  len   = filament_priming_mm[extruder]\n" +
            "  speed = priming_mm_per_sec * 60;\n" +
            "  letter = 'E'\n" +
            "  output('G1 F' .. speed .. ' ' .. letter .. f(e - len - extruder_e_restart))\n" +
            "  extruder_e = e - len\n" +
            "  return e - len\n" +
            "end\n" +
            "\n" +
            "function prime(extruder,e)\n" +
            "  len   = filament_priming_mm[extruder]\n" +
            "  speed = priming_mm_per_sec * 60;\n" +
            "  letter = 'E'\n" +
            "  output('G1 F' .. speed .. ' ' .. letter .. f(e + len - extruder_e_restart))\n" +
            "  extruder_e = e + len\n" +
            "  return e + len\n" +
            "end\n" +
            "\n" +
            "current_extruder = 0\n" +
            "current_frate = 0\n" +
            "\n" +
            "function select_extruder(extruder)\n" +
            "end\n" +
            "\n" +
            "function swap_extruder(from,to,x,y,z)\n" +
            "end\n" +
            "\n" +
            "function move_xyz(x,y,z)\n" +
            "  output('G1 X' .. f(x) .. ' Y' .. f(y) .. ' Z' .. f(z+z_offset))\n" +
            "end\n" +
            "\n" +
            "function move_xyze(x,y,z,e)\n" +
            "  extruder_e = e\n" +
            "  letter = 'E'\n" +
            "  output('G1 X' .. f(x) .. ' Y' .. f(y) .. ' Z' .. f(z+z_offset) .. ' F' .. current_frate .. ' ' .. letter .. f(e - extruder_e_restart))\n" +
            "end\n" +
            "\n" +
            "function move_e(e)\n" +
            "  extruder_e = e\n" +
            "  letter = 'E'\n" +
            "  output('G1 ' .. letter .. f(e - extruder_e_restart))\n" +
            "end\n" +
            "\n" +
            "function set_feedrate(feedrate)\n" +
            "  output('G1 F' .. feedrate)\n" +
            "  current_frate = feedrate\n" +
            "end\n" +
            "\n" +
            "function extruder_start()\n" +
            "end\n" +
            "\n" +
            "function extruder_stop()\n" +
            "end\n" +
            "\n" +
            "function progress(percent)\n" +
            "end\n" +
            "\n" +
            "function set_extruder_temperature(extruder,temperature)\n" +
            "  output('M104 S' .. temperature .. ' T' .. extruder)\n" +
            "end\n" +
            "\n" +
            "current_fan_speed = -1\n" +
            "function set_fan_speed(speed)\n" +
            "  if speed ~= current_fan_speed then\n" +
            "    output('M106 S'.. math.floor(255 * speed/100))\n" +
            "    current_fan_speed = speed\n" +
            "  end\n" +
            "end\n";

}
