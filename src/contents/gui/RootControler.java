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
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
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
        Tooltip.install(animSupportLabel, new QuickLongTooltip("Anim 1 is used when unpromoted/untransformed, and Anim2 is used when promoted/transformed.\n" +
                "Some characters work fine without any Anim, and some ONLY work no Anim."));
        Tooltip.install(globalSupportLabel, new QuickLongTooltip("Global changes are applied when you Save and Quit."));
        Tooltip.install(randomizeSupportLabel, new QuickLongTooltip("The randomizing is still pretty weird. Notes:\n" +
                "Two logs are created in the directory containing your root folder. One with everthing changed, and one for avoiding spoilers with only the growths\n" +
                "Ike is guaranteed to be a flyer so he can recruit Marcia and Jill.\n" +
                "The last weapon in each character's inventory is replaced with an Iron weapon they can use.\n" +
                "The Vague Katti is saved from becoming an Iron weapon by putting it in Tormod's inventory."));

        // Set custom cellfactory to comboboxes
        NamedObjectCellFactory.applyToComboBox(characterComboBox);
        NamedObjectCellFactory.applyToComboBox(classComboBox);
        NamedObjectCellFactory.applyToComboBox(skill1ComboBox);
        NamedObjectCellFactory.applyToComboBox(skill2ComboBox);
        NamedObjectCellFactory.applyToComboBox(skill3ComboBox);
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
    private Spinner<Integer> buildSpinner;
    @FXML
    private Spinner<Integer> weightSpinner;

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
    private Button randomizeSaveAndQuitButton;


    ////////////
    // Fields //
    ////////////

    private Stage stage;
    private String rootDirectory;
    private boolean decompressed;
    private LinkedHashMap<String, CharacterEntry> characterEntryMap;
    private Map<String, DisposEntry> disposEntryMap;
    private Map<Integer, Skill> skillsMap;
    private Map<Integer, Portrait> portraitsMap;
    private Map<Integer, Animate> animationMap;
    private Map<Integer, FEClass> feClassMap;
    private Map<String, Weapon> weaponsMap;
    private Map<String, Item> itemsMap;
    private Map<Integer, BaseFEClass> baseFEClassMap;
    private Map<Integer, PromotedFEClass> promotedFEClassMap;
    private Map<Integer, LaguzFEClass> laguzFEClassMap;

    private CharacterEntry currentlySelectedCharacter;
    private DisposEntry currentlySelectedDispos;
    private int oldEnemyGrowth;

    private List<Spinner<Integer>> basesSpinners;
    private List<Spinner<Integer>> growthsSpinners;
    private List<Spinner<Character>> weaponRankSpinners;

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
                        List<Inventory> inventoryList = ExternalFileMapmaker.readInventoryFile();
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

                    // ComboBox contents
                    skill1ComboBox.setItems(FXCollections.observableArrayList(skillsMap.values()).sorted(Comparator.comparing(Skill::getDisplayName)));
                    skill2ComboBox.setItems(FXCollections.observableArrayList(skillsMap.values()).sorted(Comparator.comparing(Skill::getDisplayName)));
                    skill3ComboBox.setItems(FXCollections.observableArrayList(skillsMap.values()).sorted(Comparator.comparing(Skill::getDisplayName)));
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
                        oldEnemyGrowth = SystemIO.readGrowthIncrease(rootDirectory);
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
    }

    @FXML
    private void saveChanges(){

        // Apply enemy growths
        if (enemyGrowthSpinner.getValue() != oldEnemyGrowth){
            for (CharacterEntry entry : characterEntryMap.values()){
                // We only load disposentries for playable characters, so they can be used to filter for enemies
                if (disposEntryMap.get(entry.getPID_name()) == null){
                    byte[] growths = entry.getGrowths();

                    // We apply the increase to each growth
                    for (int i = 0; i < 8; i++){
                        growths[i] = (byte)(growths[i] - oldEnemyGrowth + enemyGrowthSpinner.getValue());
                    }

                    // Write the new growth value to the file so we can find it again next time.
                    try {
                        SystemIO.writeGrowthIncrease(rootDirectory, enemyGrowthSpinner.getValue());
                    } catch (IOException e){
                        e.printStackTrace();
                        throw new RuntimeException("The enemy growth.");
                    }
                }
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
                // Ike special case
                if(entry.getPID_name().equals("PID_IKE")){
                    Map<Integer, FEClass> ikeClasses = new HashMap<>();
                    ikeClasses.put(0x00016B57, feClassMap.get(0x00016B57)); // Dracoknight
                    ikeClasses.put(0x00016D7D, feClassMap.get(0x00016D7D)); // Pegasusknight
                    ikeClasses.put(0x00016DA9, feClassMap.get(0x00016DA9)); // Ranger, for the prev class stuff

                    // This is indiscribably bad coding style, but oh god please let it just be temporary :( // TODO: Fix this monster
                    while(ByteMath.bytesToInt(entry.getClass_pointer()) == 0x00016DA9) {
                        Randomize.randomizeClass(entry, disposEntryMap.get(entry.getPID_name()), ikeClasses, weaponsMap);
                    }
                } else {
                    // Everyone else
                    Randomize.randomizeClass(entry, disposEntryMap.get(entry.getPID_name()), feClassMap, weaponsMap);
                }

                // Log class
                try {
                    fullLog.write("Class: " + feClassMap.get(ByteMath.bytesToInt(entry.getClass_pointer())).getDisplayName() + System.lineSeparator());
                } catch (IOException e){
                    e.printStackTrace();
                    System.out.println("Log failed a write. Whatever.");
                }

                // Save the Vague Katti
                disposEntryMap.get("PID_TOPUCK").setWeapon1_IID_name("IID_WATOU");
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
                        sb.setCharAt(7*i + 3, String.valueOf((bases[i]/10) % 10).charAt(0));
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
                        sb.setCharAt(7*i + 2, String.valueOf((growths[i]/100) % 10).charAt(0));
                    }

                    // Write it
                    fullLog.write("Growths: " + sb.toString() + System.lineSeparator());
                    growthsLog.write("Growths: " + sb.toString() + System.lineSeparator());
                } catch (IOException e){
                    e.printStackTrace();
                    System.out.println("Log failed a write. Whatever.");
                }
            }
        });

        fullLog.close();
        growthsLog.close();
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
        levelSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 20));

        // Build and Weight
        buildSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(-20, 20));
        weightSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(-50, 50));

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
