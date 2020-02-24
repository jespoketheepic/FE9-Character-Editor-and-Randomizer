package contents.gui;

import contents.datastructures.*;
import contents.datastructures.interfaces.Inventory;
import contents.datastructures.interfaces.SystemCmpContents;
import contents.datastructures.inventory.Item;
import contents.datastructures.inventory.Weapon;
import contents.io.*;
import contents.datastructures.feClass.BaseFEClass;
import contents.datastructures.feClass.LaguzFEClass;
import contents.datastructures.feClass.PromotedFEClass;
import contents.datastructures.interfaces.FEClass;
import contents.gui.components.NamedObjectCellFactory;
import contents.gui.components.QuickLongTooltip;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableArray;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Group;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Text;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import contents.math.ByteMath;
import contents.math.Randomize;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;

public class RootControler {

    private static final int[][] enemyGrowthOptions = new int[][]{{1,2,3,4},{1,2},{0,1,2,3,4,5,6,7}};

    @SuppressWarnings("Duplicates")
    public void initialize() {
        // Assign Tooltips
        Tooltip gctoolTooltip = new QuickLongTooltip("Use GC-Tool to extract a Fire Emblem: Path of Radiance.ISO file");
        Tooltip.install(pickRootFolderLabel, gctoolTooltip);
        Tooltip.install(pickRootFolderSupportLabel, gctoolTooltip);
        Tooltip applyTooltip = new QuickLongTooltip("Apply your changes to the individual character before moving on.\n" +
                "Use Save & Quit to save all your applied changes to the relevant files.\n" +
                "When you are done, use GCRebuilder to rebuild an ISO from the edited files.");
        Tooltip.install(applyButtonBox, applyTooltip);
        Tooltip.install(weaponRankSupportLabel, new QuickLongTooltip("Note that what weapons you can use is dictated by the class, not the weapon rank."));
        Tooltip.install(skillsSupportLabel, new QuickLongTooltip("A unit can currently not start with more skills than they have in the Vanilla game.\n" +
                "Skills you probably shouldn't mess with are marked with CAPITAL LETTERS"));
        Tooltip.install(animSupportLabel, new QuickLongTooltip("Model 1 is used when unpromoted/untransformed, and Model 2 is used when promoted/transformed.\n" +
                "Some characters work fine without any specifically assigned Model, and some ONLY work no Model assigned.\n" +
                "Position is given as (X horizontal from left, Y vertical from top) coordinates from the origin of the map, which may lie outside the playable area.\n" +
                "Note this editor has no safety in place to ensure the position coordinates are valid."));
        Tooltip.install(globalSupportLabel, new QuickLongTooltip("Global changes are applied when you Save and Quit."));
        Tooltip.install(randomizeSupportLabel, new QuickLongTooltip("The randomizing is still pretty weird. Notes:\n" +
                "Two logs are created in the directory containing your root folder. One with everthing changed, and one for avoiding spoilers with only the growths\n" +
                "Ike is guaranteed to be a flyer so he can recruit Marcia and Jill.\n" +
                "The last weapon in each character's inventory is replaced with an Iron weapon they can use.\n" +
                "The Vague Katti is saved from becoming an Iron weapon by putting it in Tormod's inventory."));
        Tooltip.install(ch1HouseSupportLabel, new QuickLongTooltip("The item in the first house in ch 1.\n" +
                " Limited to items whose IID can fit in the space left by the original \"IID_STEELSWORD\"."));

        // Set custom cellfactory to comboboxes
        NamedObjectCellFactory.applyToComboBox(characterComboBox);
        NamedObjectCellFactory.applyToComboBox(classComboBox);
        NamedObjectCellFactory.applyToComboBox(skill1ComboBox);
        NamedObjectCellFactory.applyToComboBox(skill2ComboBox);
        NamedObjectCellFactory.applyToComboBox(skill3ComboBox);
        NamedObjectCellFactory.applyToComboBox(MPID_ComboBox);
        NamedObjectCellFactory.applyToComboBox(portraitComboBox);
        NamedObjectCellFactory.applyToComboBox(animation1ComboBox);
        NamedObjectCellFactory.applyToComboBox(animation2ComboBox);
        NamedObjectCellFactory.applyToComboBox(weapon1ComboBox);
        NamedObjectCellFactory.applyToComboBox(weapon2ComboBox);
        NamedObjectCellFactory.applyToComboBox(weapon3ComboBox);
        NamedObjectCellFactory.applyToComboBox(weapon4ComboBox);
        NamedObjectCellFactory.applyToComboBox(item1ComboBox);
        NamedObjectCellFactory.applyToComboBox(item2ComboBox);
        NamedObjectCellFactory.applyToComboBox(item3ComboBox);
        NamedObjectCellFactory.applyToComboBox(item4ComboBox);
        NamedObjectCellFactory.applyToComboBox(ch1ItemComboBox);

        // Radio buttons
        enemyGrowthTypeRadioGroup = new ToggleGroup();
        enemyGrowthRadioSMSS.setToggleGroup(enemyGrowthTypeRadioGroup);
        enemyGrowthRadioSM.setToggleGroup(enemyGrowthTypeRadioGroup);
        enemyGrowthRadioAll.setToggleGroup(enemyGrowthTypeRadioGroup);
        enemyGrowthRadioSMSS.setUserData(0);
        enemyGrowthRadioSM.setUserData(1);
        enemyGrowthRadioAll.setUserData(2);
        enemyGrowthRadioSMSS.setSelected(true);

        // Spinners
        basesSpinners = List.of(hpSpinner, strSpinner, magSpinner, sklSpinner, spdSpinner, lckSpinner, defSpinner, resSpinner);
        growthsSpinners = List.of(hpGrowthSpinner, strGrowthSpinner, magGrowthSpinner, sklGrowthSpinner, spdGrowthSpinner, lckGrowthSpinner, defGrowthSpinner, resGrowthSpinner);
        weaponRankSpinners = List.of(swordSpinner, lanceSpinner, axeSpinner, bowSpinner, fireSpinner, thunderSpinner, windSpinner, staffSpinner);
        setValueFactories();
    }

    /////////////////
    // FXML Fields //
    /////////////////

    // Root
    @FXML
    private AnchorPane root;
    @FXML
    private Button browseForRootFolderButton;
    @FXML
    private TextField rootFolderTextField;
    @FXML
    private Label pickRootFolderLabel;
    @FXML
    private Label pickRootFolderSupportLabel;
    @FXML
    private ProgressIndicator loadingIndicator;
    @FXML
    private Label loadingLabel;

    // EditorPane outer
    @FXML
    private SplitPane editorPane;
    @FXML
    private ComboBox<CharacterEntry> characterComboBox;
    @FXML
    private Button applyButton;
    @FXML
    private Button saveButton;
    @FXML
    private AnchorPane applyButtonBox;

    // Bases
    @FXML
    private Spinner<Integer> hpSpinner;
    @FXML
    private Spinner<Integer> strSpinner;
    @FXML
    private Spinner<Integer> magSpinner;
    @FXML
    private Spinner<Integer> sklSpinner;
    @FXML
    private Spinner<Integer> spdSpinner;
    @FXML
    private Spinner<Integer> lckSpinner;
    @FXML
    private Spinner<Integer> defSpinner;
    @FXML
    private Spinner<Integer> resSpinner;

    // Growth
    @FXML
    private Spinner<Integer> hpGrowthSpinner;
    @FXML
    private Spinner<Integer> strGrowthSpinner;
    @FXML
    private Spinner<Integer> magGrowthSpinner;
    @FXML
    private Spinner<Integer> sklGrowthSpinner;
    @FXML
    private Spinner<Integer> spdGrowthSpinner;
    @FXML
    private Spinner<Integer> lckGrowthSpinner;
    @FXML
    private Spinner<Integer> defGrowthSpinner;
    @FXML
    private Spinner<Integer> resGrowthSpinner;

    // Weapon Ranks
    @FXML
    private Label weaponRankSupportLabel;
    @FXML
    private Spinner<Character> swordSpinner;
    @FXML
    private Spinner<Character> lanceSpinner;
    @FXML
    private Spinner<Character> axeSpinner;
    @FXML
    private Spinner<Character> bowSpinner;
    @FXML
    private Spinner<Character> fireSpinner;
    @FXML
    private Spinner<Character> thunderSpinner;
    @FXML
    private Spinner<Character> windSpinner;
    @FXML
    private Spinner<Character> staffSpinner;

    // Misc
    @FXML
    private Spinner<Integer> levelSpinner;
    @FXML
    private ComboBox<FEClass> classComboBox;
    @FXML
    private ComboBox<Animate> animation1ComboBox;
    @FXML
    private ComboBox<Animate> animation2ComboBox;
    @FXML
    private Label animSupportLabel;
    @FXML
    private ComboBox<Portrait> portraitComboBox;
    @FXML
    private ComboBox<MPID> MPID_ComboBox;
    @FXML
    private Spinner<Integer> buildSpinner;
    @FXML
    private Spinner<Integer> weightSpinner;
    @FXML
    private Spinner<Integer> startXSpinner;
    @FXML
    private Spinner<Integer> startYSpinner;
    @FXML
    private Spinner<Integer> finalXSpinner;
    @FXML
    private Spinner<Integer> finalYSpinner;

    // Skills
    @FXML
    private Label skillsSupportLabel;
    @FXML
    private ComboBox<Skill> skill1ComboBox;
    @FXML
    private ComboBox<Skill> skill2ComboBox;
    @FXML
    private ComboBox<Skill> skill3ComboBox;
    
    // Weapons and Items
    @FXML
    private AnchorPane weaponsAndItemsBox;
    @FXML
    private ComboBox<Weapon> weapon1ComboBox;
    @FXML
    private ComboBox<Weapon> weapon2ComboBox;
    @FXML
    private ComboBox<Weapon> weapon3ComboBox;
    @FXML
    private ComboBox<Weapon> weapon4ComboBox;
    @FXML
    private ComboBox<Item> item1ComboBox;
    @FXML
    private ComboBox<Item> item2ComboBox;
    @FXML
    private ComboBox<Item> item3ComboBox;
    @FXML
    private ComboBox<Item> item4ComboBox;

    // Global
    @FXML
    private Label globalSupportLabel;
    @FXML
    private Spinner<Integer> enemyGrowthSpinner;
    @FXML
    private RadioButton enemyGrowthRadioSMSS;
    @FXML
    private RadioButton enemyGrowthRadioSM;
    @FXML
    private RadioButton enemyGrowthRadioAll;

    // Randomize
    @FXML
    private Label randomizeSupportLabel;
    @FXML
    private CheckBox basesCheck;
    @FXML
    private CheckBox growthsCheck;
    @FXML
    private CheckBox classCheck;
    @FXML
    private CheckBox skillsCheck;
    @FXML
    private Button randomizeSaveAndQuitButton;

    // Don't Randomize
    @FXML
    private Label ch1HouseSupportLabel;
    @FXML
    private ComboBox<Inventory> ch1ItemComboBox;
    @FXML
    private Button dontRandomizeSaveAndQuitButton;


    ////////////
    // Fields //
    ////////////

    private Stage stage;
    private String rootDirectory;
    private boolean decompressed;
    private LinkedHashMap<String, CharacterEntry> characterEntryMap;
    private Map<String, DisposEntry> disposEntryMap;
    private Map<Integer, Skill> skillsMap;
    private Map<Integer, MPID> mpidMap;
    private Map<Integer, Portrait> portraitsMap;
    private Map<Integer, Animate> animationMap;
    private Map<Integer, FEClass> feClassMap;

    private List<Inventory> inventoryList;
    private Map<String, Weapon> weaponsMap;
    private Map<String, Item> itemsMap;

    private Map<Integer, BaseFEClass> baseFEClassMap;
    private Map<Integer, PromotedFEClass> promotedFEClassMap;
    private Map<Integer, LaguzFEClass> laguzFEClassMap;

    private CharacterEntry currentlySelectedCharacter;
    private DisposEntry currentlySelectedDispos;
    private int oldEnemyGrowth;
    private int oldEnemyGrowthType;

    private List<Spinner<Integer>> basesSpinners;
    private List<Spinner<Integer>> growthsSpinners;
    private List<Spinner<Character>> weaponRankSpinners;

    private ToggleGroup enemyGrowthTypeRadioGroup;

    private DisposIO disposIO;

    //////////////////
    // FXML Methods //
    //////////////////

    @FXML
    private void browseForRootFolder(ActionEvent event) {

        // Get the window we are in
        stage = (Stage) browseForRootFolderButton.getScene().getWindow();
        // Make a DirectoryChooser
        DirectoryChooser chooser = new DirectoryChooser();
        chooser.setTitle("Browse for FE9 root folder");
        // Set initial directory to the working directory
        chooser.setInitialDirectory(new File(System.getProperty("user.dir")));
        // Get the selected directory, if null just return
        File fileChoice = chooser.showDialog(stage);
        if(fileChoice == null)
        {
            return;
        }
        rootDirectory = fileChoice.getPath();
        rootFolderTextField.setText(rootDirectory);
        rootFolderTextField.end();

        // Probe a bit whether this directory indeed contains the files we need
        if (!new File(rootDirectory + "\\system.cmp").isFile()) {
            // Alert missing system.cmp
            directoryProblemAlert("system.cmp missing");
            editorPane.setDisable(true);
            return;
        } else if (!new File(rootDirectory + "\\zmap\\always\\dispos.cmp").isFile()) {
            // Alert missing zmap contents
            directoryProblemAlert("zmap contents missing");
            return;
        }

        ///////////////////////////////////////////////////////////////////////////////////////////////////////////////
        // If we have a valid root folder, decompress what we need
        rootFolderTextField.setDisable(true);
        browseForRootFolderButton.setDisable(true);

        // Decompression of dispos files has been moved into disposIO
        Thread decompressThread = new Thread(()->{
            try {
                loadingLockdown(true);
                disposIO = new DisposIO(rootDirectory);
                disposEntryMap = disposIO.collectDisposEntries();
                Compressor.decompressLZ77(rootDirectory + File.separator + "system.cmp");
                decompressed = true;

                // Now that the files are decompressed, we need to recompress when the user tries to exit.
                root.getScene().getWindow().setOnCloseRequest(ev -> {
                    Thread compressThread = new Thread(()->{
                        try {
                            if(decompressed) {
                                Platform.runLater(()-> loadingLockdown(true));
                                Compressor.compressLZ77(rootDirectory + File.separator + "system.cmp");
                                Compressor.compressLZ77_disposFiles(disposIO.getDisposFileList());
                                Platform.runLater(()-> loadingLockdown(false));
                            }
                        } catch (IOException | InterruptedException e) {
                            problemAlert("Error encountered while re-compressing files", "Compressor error", e.getMessage());
                        }
                        Platform.exit();
                    });

                    compressThread.setDaemon(false);
                    compressThread.start();
                    ev.consume();
                });

                // Read in the character entries:
                Platform.runLater(()->{
                    try {
                        characterEntryMap = CharacterEntryIO.collectCharacterEntries(rootDirectory);
                    } catch (IOException e) {
                        directoryProblemAlert("Error encountered while reading data", e.getMessage());
                        editorPane.setDisable(true);
                        return;
                    }

                    // Read external files
                    try{
                        skillsMap = ExternalFileMapmaker.readSkillFile();
                    } catch (IOException e){
                        e.printStackTrace();
                        throw new RuntimeException("Exception reading Skills.txt");
                    }
                    try{
                        mpidMap = ExternalFileMapmaker.readMPIDFile();
                    } catch (IOException e){
                        e.printStackTrace();
                        throw new RuntimeException("Exception reading MPID.txt");
                    }
                    try{
                        portraitsMap = ExternalFileMapmaker.readPortraitFile();
                    } catch (IOException e){
                        e.printStackTrace();
                        throw new RuntimeException("Exception reading Portraits.txt");
                    }
                    try{
                        animationMap = ExternalFileMapmaker.readAnimationsFile();
                    } catch (IOException e){
                        e.printStackTrace();
                        throw new RuntimeException("Exception reading Animations.txt");
                    }
                    try{
                        inventoryList = ExternalFileMapmaker.readInventoryFile();
                        weaponsMap = inventoryList.stream().filter(entry -> entry instanceof Weapon).map(entry -> (Weapon) entry).collect(Collectors.toMap(Weapon::getKey, entry -> entry));
                        itemsMap = inventoryList.stream().filter(entry -> entry instanceof Item).map(entry -> (Item) entry).collect(Collectors.toMap(Item::getKey, entry -> entry));
                    } catch (IOException e){
                        e.printStackTrace();
                        throw new RuntimeException("Exception reading Items.txt");
                    }
                    try{
                        baseFEClassMap = new HashMap<>();
                        promotedFEClassMap = new HashMap<>();
                        laguzFEClassMap = new HashMap<>();
                        feClassMap = ExternalFileMapmaker.readFEClassFile(baseFEClassMap, promotedFEClassMap, laguzFEClassMap);
                    } catch (IOException e){
                        e.printStackTrace();
                        throw new RuntimeException("Exception reading Class.txt");
                    }
                    try {
                        // The chapter 1 house
                        ObservableList<Inventory> observeInventory = FXCollections.observableArrayList(inventoryList).filtered(entry -> entry.getIID_name().length() <= 0x0E).sorted(Comparator.comparing(Inventory::getName));
                        if (observeInventory.isEmpty()){
                            throw new RuntimeException("No items to put in house?");
                        }
                        ch1ItemComboBox.setItems(observeInventory);
                        String houseContents = new String(SystemIO.readCh1HouseItem(rootDirectory));
                        ch1ItemComboBox.setValue(observeInventory.filtered(entry -> entry.getIID_name().equals(houseContents)).get(0));
                    } catch (IOException e){
                        throw new RuntimeException(e);
                    }


                    // ComboBox contents
                    skill1ComboBox.setItems(FXCollections.observableArrayList(skillsMap.values()).sorted(Comparator.comparing(Skill::getDisplayName)));
                    skill2ComboBox.setItems(FXCollections.observableArrayList(skillsMap.values()).sorted(Comparator.comparing(Skill::getDisplayName)));
                    skill3ComboBox.setItems(FXCollections.observableArrayList(skillsMap.values()).sorted(Comparator.comparing(Skill::getDisplayName)));
                    MPID_ComboBox.setItems(FXCollections.observableArrayList(mpidMap.values()).sorted(Comparator.comparing(MPID::getDisplayName)));
                    portraitComboBox.setItems(FXCollections.observableArrayList(portraitsMap.values()).sorted(Comparator.comparing(Portrait::getDisplayName)));
                    classComboBox.setItems(FXCollections.observableArrayList(feClassMap.values()).sorted(Comparator.comparing(FEClass::getDisplayName)));
                    animation1ComboBox.setItems(FXCollections.observableArrayList(animationMap.values()).sorted(Comparator.comparing(Animate::getDisplayName)));
                    animation2ComboBox.setItems(FXCollections.observableArrayList(animationMap.values()).sorted(Comparator.comparing(Animate::getDisplayName)));
                    weapon1ComboBox.setItems(FXCollections.observableArrayList(weaponsMap.values()).sorted(Comparator.comparing(Weapon::getDisplayName)));
                    weapon2ComboBox.setItems(FXCollections.observableArrayList(weaponsMap.values()).sorted(Comparator.comparing(Weapon::getDisplayName)));
                    weapon3ComboBox.setItems(FXCollections.observableArrayList(weaponsMap.values()).sorted(Comparator.comparing(Weapon::getDisplayName)));
                    weapon4ComboBox.setItems(FXCollections.observableArrayList(weaponsMap.values()).sorted(Comparator.comparing(Weapon::getDisplayName)));
                    item1ComboBox.setItems(FXCollections.observableArrayList(itemsMap.values()).sorted(Comparator.comparing(Item::getDisplayName)));
                    item2ComboBox.setItems(FXCollections.observableArrayList(itemsMap.values()).sorted(Comparator.comparing(Item::getDisplayName)));
                    item3ComboBox.setItems(FXCollections.observableArrayList(itemsMap.values()).sorted(Comparator.comparing(Item::getDisplayName)));
                    item4ComboBox.setItems(FXCollections.observableArrayList(itemsMap.values()).sorted(Comparator.comparing(Item::getDisplayName)));

                    // Fill the characters combobox
                    characterComboBox.setItems(FXCollections.observableArrayList(characterEntryMap.values()));
                    characterComboBox.setValue(characterComboBox.getItems().get(0));
                    characterPicked();

                    // Global (Enemy growth)
                    try {
                        int[] buffer = SystemIO.readGrowthIncrease(rootDirectory);
                        // Growth increase type
                        oldEnemyGrowthType = buffer[0];
                        switch (oldEnemyGrowthType){
                            case 0: // SMSS
                                enemyGrowthRadioSMSS.fire();
                                break;
                            case 1: // SM
                                enemyGrowthRadioSM.fire();
                                break;
                            case 2: // All
                                enemyGrowthRadioAll.fire();
                                break;
                        }
                        oldEnemyGrowth = buffer[1];
                        enemyGrowthSpinner.getValueFactory().setValue(oldEnemyGrowth);
                    } catch (IOException e){
                        e.printStackTrace();
                    }

                    // If we have successfully read in the data, enable the editor portion
                    loadingLockdown(false);
                });
            } catch (IOException | InterruptedException e){
                e.printStackTrace();
                throw new RuntimeException("Failed decompression");
            }
        });

        decompressThread.setDaemon(false);
        decompressThread.start();
    }

    @FXML
    private void characterPicked() {
        currentlySelectedCharacter = characterComboBox.getValue();
        currentlySelectedDispos = disposEntryMap.get(currentlySelectedCharacter.getPID_name());

        // Base stats
        byte[] bases = currentlySelectedCharacter.getBases();
        int i = 0;
        for(Spinner<Integer> spinner : basesSpinners){
            spinner.getValueFactory().setValue((int)bases[i]);
            i++;
        }

        // Growths
        byte[] growths = currentlySelectedCharacter.getGrowths();
        i = 0;
        for(Spinner<Integer> spinner : growthsSpinners){
            spinner.getValueFactory().setValue(ByteMath.unsignedByteToInt(growths[i]));
            i++;
        }

        // Weapon ranks
        char[] weaponRanks = new String(currentlySelectedCharacter.getWeaponRanks(), StandardCharsets.UTF_8).toCharArray();
        i = 0;
        for(Spinner<Character> spinner : weaponRankSpinners){
            spinner.getValueFactory().setValue(weaponRanks[i]);
            i++;
        }

        // Level
        levelSpinner.getValueFactory().setValue(ByteMath.unsignedByteToInt(currentlySelectedCharacter.getLevel()));

        // Build and Weight
        buildSpinner.getValueFactory().setValue((int)currentlySelectedCharacter.getBuild());
        weightSpinner.getValueFactory().setValue((int)currentlySelectedCharacter.getWeight());

        // Skills
        skill1ComboBox.setValue(skillsMap.get(ByteMath.bytesToInt(currentlySelectedCharacter.getSkill1_pointer())));
        disableComboBoxOnNullID(skill1ComboBox, "(none)");
        skill2ComboBox.setValue(skillsMap.get(ByteMath.bytesToInt(currentlySelectedCharacter.getSkill2_pointer())));
        disableComboBoxOnNullID(skill2ComboBox, "(none)");
        skill3ComboBox.setValue(skillsMap.get(ByteMath.bytesToInt(currentlySelectedCharacter.getSkill3_pointer())));
        disableComboBoxOnNullID(skill3ComboBox, "(none)");

        // MPID
        MPID_ComboBox.setValue(mpidMap.get(ByteMath.bytesToInt(currentlySelectedCharacter.getMPID_pointer())));

        // Portrait
        portraitComboBox.setValue(portraitsMap.get(ByteMath.bytesToInt(currentlySelectedCharacter.getPortrait_pointer())));

        // Class and animation
        classComboBox.setValue(feClassMap.get(ByteMath.bytesToInt(currentlySelectedCharacter.getClass_pointer())));
        animation1ComboBox.setValue(animationMap.get(ByteMath.bytesToInt(currentlySelectedCharacter.getAnimation1_pointer())));
        animation2ComboBox.setValue(animationMap.get(ByteMath.bytesToInt(currentlySelectedCharacter.getAnimation2_pointer())));

        // Weapons and Items
        // Disabled on entries with no Dispos
        if(currentlySelectedDispos == null){
            weaponsAndItemsBox.setDisable(true);
        } else {
            weaponsAndItemsBox.setDisable(false);
            weapon1ComboBox.setValue(weaponsMap.get(currentlySelectedDispos.getWeapon1_IID_name()));
            disableComboBoxOnNullID(weapon1ComboBox, "IID_NULL");
            weapon2ComboBox.setValue(weaponsMap.get(currentlySelectedDispos.getWeapon2_IID_name()));
            disableComboBoxOnNullID(weapon2ComboBox, "IID_NULL");
            weapon3ComboBox.setValue(weaponsMap.get(currentlySelectedDispos.getWeapon3_IID_name()));
            disableComboBoxOnNullID(weapon3ComboBox, "IID_NULL");
            weapon4ComboBox.setValue(weaponsMap.get(currentlySelectedDispos.getWeapon4_IID_name()));
            disableComboBoxOnNullID(weapon4ComboBox, "IID_NULL");
            item1ComboBox.setValue(itemsMap.get(currentlySelectedDispos.getItem1_IID_name()));
            disableComboBoxOnNullID(item1ComboBox, "IID_NULL");
            item2ComboBox.setValue(itemsMap.get(currentlySelectedDispos.getItem2_IID_name()));
            disableComboBoxOnNullID(item2ComboBox, "IID_NULL");
            item3ComboBox.setValue(itemsMap.get(currentlySelectedDispos.getItem3_IID_name()));
            disableComboBoxOnNullID(item3ComboBox, "IID_NULL");
            item4ComboBox.setValue(itemsMap.get(currentlySelectedDispos.getItem4_IID_name()));
            disableComboBoxOnNullID(item4ComboBox, "IID_NULL");
            // Location
            byte[] coordinates = currentlySelectedDispos.getCoordinates();
            startXSpinner.getValueFactory().setValue((int)coordinates[0]);
            startYSpinner.getValueFactory().setValue((int)coordinates[1]);
            finalXSpinner.getValueFactory().setValue((int)coordinates[2]);
            finalYSpinner.getValueFactory().setValue((int)coordinates[3]);
        }
    }

    // When a class is picked, set animations to this class' defaults
    @FXML
    private void classPicked(){
        animation1ComboBox.setValue(animationMap.get(ByteMath.bytesToInt(classComboBox.getValue().getAID1_pointer())));
        animation2ComboBox.setValue(animationMap.get(ByteMath.bytesToInt(classComboBox.getValue().getAID2_pointer())));
    }

    @SuppressWarnings("Duplicates")
    @FXML
    private void applyChanges(){

        // Bases
        currentlySelectedCharacter.setBases(new byte[]{
                (byte)(hpSpinner.getValue() & 0xFF),
                (byte)(strSpinner.getValue() & 0xFF),
                (byte)(magSpinner.getValue() & 0xFF),
                (byte)(sklSpinner.getValue() & 0xFF),
                (byte)(spdSpinner.getValue() & 0xFF),
                (byte)(lckSpinner.getValue() & 0xFF),
                (byte)(defSpinner.getValue() & 0xFF),
                (byte)(resSpinner.getValue() & 0xFF)});

        // Growths
        currentlySelectedCharacter.setGrowths(new byte[]{
                (byte)(hpGrowthSpinner.getValue() & 0xFF),
                (byte)(strGrowthSpinner.getValue() & 0xFF),
                (byte)(magGrowthSpinner.getValue() & 0xFF),
                (byte)(sklGrowthSpinner.getValue() & 0xFF),
                (byte)(spdGrowthSpinner.getValue() & 0xFF),
                (byte)(lckGrowthSpinner.getValue() & 0xFF),
                (byte)(defGrowthSpinner.getValue() & 0xFF),
                (byte)(resGrowthSpinner.getValue() & 0xFF)});

        // Misc
        currentlySelectedCharacter.setLevel((byte)(levelSpinner.getValue() & 0xFF));
        currentlySelectedCharacter.setBuild((byte)(buildSpinner.getValue() & 0xFF));
        currentlySelectedCharacter.setWeight((byte)(weightSpinner.getValue() & 0xFF));
        currentlySelectedCharacter.setClass_pointer(classComboBox.getValue().getJID_pointer());
        currentlySelectedCharacter.setAnimation1_pointer(animation1ComboBox.getValue().getAID_pointer());
        currentlySelectedCharacter.setAnimation2_pointer(animation2ComboBox.getValue().getAID_pointer());
        currentlySelectedCharacter.setPortrait_pointer(portraitComboBox.getValue().getFID_pointer());
        currentlySelectedCharacter.setMPID_pointer(MPID_ComboBox.getValue().getID_pointer());

        // Skills
        //bubbleUpComboBoxValues(skillsMap.get(0), skill1ComboBox, skill2ComboBox, skill3ComboBox);
        currentlySelectedCharacter.setSkill1_pointer(skill1ComboBox.getValue().getSID_pointer());
        currentlySelectedCharacter.setSkill2_pointer(skill2ComboBox.getValue().getSID_pointer());
        currentlySelectedCharacter.setSkill3_pointer(skill3ComboBox.getValue().getSID_pointer());

        // Weapon ranks
        currentlySelectedCharacter.setWeaponRanks(new String(new char[]{
                swordSpinner.getValue(),
                lanceSpinner.getValue(),
                axeSpinner.getValue(),
                bowSpinner.getValue(),
                fireSpinner.getValue(),
                thunderSpinner.getValue(),
                windSpinner.getValue(),
                staffSpinner.getValue(),
                '-', 0x00}
                ).getBytes(StandardCharsets.UTF_8));

        // Dispos
        currentlySelectedDispos.setJID_name(classComboBox.getValue().getJID_name());
        //bubbleUpComboBoxValues(weaponsMap.get("IID_NULL"), weapon1ComboBox, weapon2ComboBox, weapon3ComboBox, weapon4ComboBox);
        currentlySelectedDispos.setWeapon1_IID_name(weapon1ComboBox.getValue().getIID_name());
        currentlySelectedDispos.setWeapon2_IID_name(weapon2ComboBox.getValue().getIID_name());
        currentlySelectedDispos.setWeapon3_IID_name(weapon3ComboBox.getValue().getIID_name());
        currentlySelectedDispos.setWeapon4_IID_name(weapon4ComboBox.getValue().getIID_name());
        //bubbleUpComboBoxValues(itemsMap.get("IID_NULL"), item1ComboBox, item2ComboBox, item3ComboBox, item4ComboBox);
        currentlySelectedDispos.setItem1_IID_name(item1ComboBox.getValue().getIID_name());
        currentlySelectedDispos.setItem2_IID_name(item2ComboBox.getValue().getIID_name());
        currentlySelectedDispos.setItem3_IID_name(item3ComboBox.getValue().getIID_name());
        currentlySelectedDispos.setItem4_IID_name(item4ComboBox.getValue().getIID_name());

        // Position
        currentlySelectedDispos.setCoordinates(startXSpinner.getValue(), startYSpinner.getValue(),finalXSpinner.getValue(),finalYSpinner.getValue());
    }

    @FXML
    private void saveChanges(){
        // Apply enemy growths
        if (enemyGrowthSpinner.getValue() != oldEnemyGrowth || (Integer) enemyGrowthTypeRadioGroup.getSelectedToggle().getUserData() != oldEnemyGrowthType){
            try {
                ArrayList<ClassGrowths> classGrowthsList;

                if (enemyGrowthSpinner.getValue() == 0){
                    classGrowthsList = SystemIO.resetClassGrowths();
                    oldEnemyGrowth = 0;
                } else {
                    classGrowthsList = SystemIO.readClassGrowths(rootDirectory);
                }

                for (ClassGrowths classGrowths : classGrowthsList) {

                    // Undo old changes
                    switch (oldEnemyGrowthType) {
                        case 0: // SMSS
                            classGrowths.massEdit_StrMagSklSpd(-oldEnemyGrowth);
                            break;
                        case 1: // SM
                            classGrowths.massEdit_StrMag(-oldEnemyGrowth);
                            break;
                        case 2: // All
                            classGrowths.massEdit_All(-oldEnemyGrowth);
                            break;
                    }

                    // Apply the new
                    switch ((Integer) enemyGrowthTypeRadioGroup.getSelectedToggle().getUserData()) {
                        case 0: // SMSS
                            classGrowths.massEdit_StrMagSklSpd(enemyGrowthSpinner.getValue());
                            break;
                        case 1: // SM
                            classGrowths.massEdit_StrMag(enemyGrowthSpinner.getValue());
                            break;
                        case 2: // All
                            classGrowths.massEdit_All(enemyGrowthSpinner.getValue());
                            break;
                    }
                }

                // Write the new growth value to the file so we can find it again next time.
                if (enemyGrowthTypeRadioGroup.getSelectedToggle().getUserData() instanceof Integer){
                    SystemIO.writeGrowthIncrease(rootDirectory, (Integer) enemyGrowthTypeRadioGroup.getSelectedToggle().getUserData(), enemyGrowthSpinner.getValue());
                } else {
                    throw new RuntimeException("Radiobuttons messed up?");
                }

                // Apply the new class growths
                SystemIO.writeClassGrowths(rootDirectory, classGrowthsList);

            } catch (IOException e){
                e.printStackTrace();
                throw new RuntimeException("The enemy growth.");
            }
        }

        // Write in the character entries:
        try {
            CharacterEntryIO.writeCharacterEntries(characterEntryMap, rootDirectory);
            disposIO.writeDisposEntries();
        } catch (IOException e) {
            problemAlert("Error encountered while writing data", "Write error", e.getMessage());
            return;
        }

        stage.fireEvent(new WindowEvent(stage, WindowEvent.WINDOW_CLOSE_REQUEST));

    }

    @FXML
    private void randomizeSaveAndQuit() throws IOException{
        // Setup
        Randomize.updateAvailableSkills(skillsMap.values());
        SystemIO.eliminateBlessedArmor(rootDirectory);
        SystemIO.paladins(rootDirectory);
        SystemIO.ch1ItemScriptStuff(rootDirectory);
        SystemIO.promoteRangerAndTheif(rootDirectory);
        SystemIO.laguzRoyalsPreventDepromote(rootDirectory);

        // Logging
        BufferedWriter fullLog = new BufferedWriter(new FileWriter(rootDirectory + File.separator + "../" + File.separator + "FE9 Randomized Full Log.txt"));
        BufferedWriter growthsLog = new BufferedWriter(new FileWriter(rootDirectory + File.separator + "../" + File.separator + "FE9 Randomized Growths Only Log.txt"));

        // For each characterentry of a playable
        characterEntryMap.values().stream().filter(entry -> disposEntryMap.get(entry.getPID_name()) != null).forEach(entry -> {
            // Log name
            try {
                fullLog.write(entry.getDisplayName() + ":" + System.lineSeparator());
                growthsLog.write(entry.getDisplayName() + ":" + System.lineSeparator());
            } catch (IOException e){
                e.printStackTrace();
                System.out.println("Log failed a write. Whatever.");
            }

            // Class. Must be first because the others base decisions on it.
            if (classCheck.isSelected()){
                Randomize.randomizeClass(entry, disposEntryMap.get(entry.getPID_name()), feClassMap, weaponsMap);

                // Log class
                try {
                    fullLog.write("Class: " + feClassMap.get(ByteMath.bytesToInt(entry.getClass_pointer())).getDisplayName() + System.lineSeparator());
                } catch (IOException e){
                    e.printStackTrace();
                    System.out.println("Log failed a write. Whatever.");
                }
            }

            // Stat labels, if either type of stats is random
            if(basesCheck.isSelected() || growthsCheck.isSelected()){
                try{
                    fullLog.write("         [  HP ][ Str ][ Mag ][ Skl ][ Spd ][ Lck ][ Def ][ Res ]" + System.lineSeparator());

                    if (growthsCheck.isSelected()){
                        growthsLog.write("         [  HP ][ Str ][ Mag ][ Skl ][ Spd ][ Lck ][ Def ][ Res ]" + System.lineSeparator());
                    }
                } catch (IOException e){
                    e.printStackTrace();
                    System.out.println("Log failed a write. Whatever.");
                }
            }

            // Bases
            if (basesCheck.isSelected()){
                Randomize.randomizeBases(entry, weaponsMap, feClassMap);

                // Log bases
                try {
                    byte [] bases = entry.getBases();
                    // Boxes to align everything
                    StringBuilder sb = new StringBuilder("[     ][     ][     ][     ][     ][     ][     ][     ]");

                    // Fill the boxes with growths by the digit
                    for(int i = 0; i < 8; i++){
                        sb.setCharAt(7*i + 4, String.valueOf(bases[i] % 10).charAt(0));
                        sb.setCharAt(7*i + 3, (String.valueOf((bases[i]/10) % 10).charAt(0) == 0) ? ' ' : String.valueOf((bases[i]/10) % 10).charAt(0));
                    }

                    // Write it
                    fullLog.write("Bases:   " + sb.toString() + System.lineSeparator());
                } catch (IOException e){
                    e.printStackTrace();
                    System.out.println("Log failed a write. Whatever.");
                }
            }

            // Growths
            if (growthsCheck.isSelected()){
                Randomize.randomizeGrowths(entry, weaponsMap, feClassMap);

                // Log growths
                try {
                    byte [] growths = entry.getGrowths();
                    // Boxes to align everything
                    StringBuilder sb = new StringBuilder("[    %][    %][    %][    %][    %][    %][    %][    %]");

                    // Fill the boxes with growths by the digit
                    for(int i = 0; i < 8; i++){
                        sb.setCharAt(7*i + 4, String.valueOf(growths[i] % 10).charAt(0));
                        sb.setCharAt(7*i + 3, String.valueOf((growths[i]/10) % 10).charAt(0));
                        sb.setCharAt(7*i + 2, (String.valueOf((growths[i]/100) % 10).charAt(0) == 0) ? ' ' : String.valueOf((growths[i]/100) % 10).charAt(0));
                    }

                    // Write it
                    fullLog.write("Growths: " + sb.toString() + System.lineSeparator());
                    growthsLog.write("Growths: " + sb.toString() + System.lineSeparator());
                } catch (IOException e){
                    e.printStackTrace();
                    System.out.println("Log failed a write. Whatever.");
                }
            }

            // Skills
            if(skillsCheck.isSelected()){
                Randomize.randomizeSkills(entry);

                // Log Skills
                try {
                    fullLog.write("Skills: ");
                    if (ByteMath.bytesToInt(entry.getSkill1_pointer()) != 0) {
                        fullLog.write(skillsMap.get(ByteMath.bytesToInt(entry.getSkill1_pointer())).getDisplayName());
                    }

                    if (ByteMath.bytesToInt(entry.getSkill2_pointer()) != 0) {
                        fullLog.write(skillsMap.get(ByteMath.bytesToInt(entry.getSkill2_pointer())).getDisplayName());
                    }

                    if (ByteMath.bytesToInt(entry.getSkill3_pointer()) != 0) {
                        fullLog.write(skillsMap.get(ByteMath.bytesToInt(entry.getSkill3_pointer())).getDisplayName());
                    }

                    fullLog.write(System.lineSeparator());
                } catch (IOException e){
                    e.printStackTrace();
                    System.out.println("Log failed a write. Whatever.");
                }
            }

            try {
                fullLog.write(System.lineSeparator());
            } catch (IOException e){
                e.printStackTrace();
                System.out.println("Log failed a write. Whatever.");
            }

        });

        // Misc things that are not by character
        // Ch 1 house
        SystemIO.writeCh1HouseItem(rootDirectory, "IID_STEELLANCE");
        // Save the Vague Katti
        disposEntryMap.get("PID_TOPUCK").setWeapon1_IID_name("IID_WATOU");
        // Move Marcia
        disposEntryMap.get("PID_MARCIA").setCoordinates(23, 2, 23, 9);
        // Move Jill
        disposEntryMap.get("PID_JILL").setCoordinates(14, 13, 15, 11);

        fullLog.close();
        growthsLog.close();
        saveChanges();
    }

    @FXML
    private void dontRandomizeSaveAndQuit() throws IOException{
        SystemIO.eliminateBlessedArmor(rootDirectory);
        SystemIO.ch1ItemScriptStuff(rootDirectory);
        SystemIO.promoteRangerAndTheif(rootDirectory);
        SystemIO.laguzRoyalsPreventDepromote(rootDirectory);

        // Ch 1 house
        SystemIO.writeCh1HouseItem(rootDirectory, ch1ItemComboBox.getValue());
        // Move Marcia
        disposEntryMap.get("PID_MARCIA").setCoordinates(23, 2, 23, 9);
        // Move Jill
        disposEntryMap.get("PID_JILL").setCoordinates(14, 13, 15, 11);

        saveChanges();
    }


    /////////////////////
    // Support Methods //
    /////////////////////

    // Gives a warning about the supplied directory
    private void directoryProblemAlert(String headerText){
        directoryProblemAlert(headerText, "Are you sure this is the root directory extracted from a Fire Emblem: Path of Radiance ISO file?");
    }

    private void directoryProblemAlert(String headerText, String warning){
        problemAlert(headerText, "Directory problem", warning);
    }

    private void problemAlert(String headerText, String title, String warning){
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(title);
        alert.setHeaderText(headerText);
        Text warningText = new Text(warning);
        warningText.setWrappingWidth(300);
        alert.getDialogPane().setContent(warningText);
        alert.showAndWait();
    }

    //
    private void setValueFactories(){

        // Base stats
        for(Spinner<Integer> spinner : basesSpinners){
            spinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(-30, 80));
        }

        // Growths
        for(Spinner<Integer> spinner : growthsSpinners){
            spinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 255,0,5));
        }

        // Weapon Ranks
        for(Spinner<Character> spinner : weaponRankSpinners){
            spinner.setValueFactory(new SpinnerValueFactory.ListSpinnerValueFactory<>(FXCollections.observableArrayList(List.of('-', 'E', 'D', 'C', 'B', 'A', 'S'))));
        }

        // Level
        levelSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 20));

        // Build and Weight
        buildSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(-20, 20));
        weightSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(-50, 50));

        // Location
        startXSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 255));
        startYSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 255));
        finalXSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 255));
        finalYSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 255));

        // Global
        enemyGrowthSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 100, 0, 5));
    }

    // Takes in comboboxes and their (none) value, and moves the (none) values to the back of the line
    // varargs are to be given in prioritized order from highest to lowest
//    private <T> void bubbleUpComboBoxValues(T nullValue, ComboBox<T> ... args){
//        // Loop from the lowest priority arg to the highest one
//        for(int i = args.length - 1; i > 0; i--){
//            // If the lower priority box has a proper value, and the next one up doesn't, swap them.
//            if(( args[i-1].getValue() == nullValue )&&( args[i].getValue() != nullValue )){
//                // Swap
//                args[i-1].setValue(args[i].getValue());
//                args[i].setValue(nullValue);
//            }
//        }
//    }

    private void loadingLockdown(boolean b){
        editorPane.setDisable(b);
        loadingIndicator.setDisable(!b);
        loadingIndicator.setVisible(b);
        loadingLabel.setDisable(!b);
        loadingLabel.setVisible(b);
    }

    // Disable boxes whose value is currently nothing. For anything hardcoded so we cant get more than vanilla, like items and skills
    private <T extends SystemCmpContents> void disableComboBoxOnNullID(ComboBox<T> box, String ... nullIDs){
        box.setDisable(false);

        for (String nullID : nullIDs){
            if (box.getValue().getID_name().equals(nullID)) {
                box.setDisable(true);
            }
        }
    }

}
