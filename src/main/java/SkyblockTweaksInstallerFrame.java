/*
 * Copyright (C) 2025 MisterCheezeCake
 *
 * This file is part of SkyblockTweaks.
 *
 * SkyblockTweaks is free software: you can redistribute it
 * and/or modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation, either
 * version 3 of the License, or (at your option) any later version.
 *
 * SkyblockTweaks is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with SkyblockTweaks. If not, see <https://www.gnu.org/licenses/>.
 */
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.WindowConstants;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.util.Locale;
import java.util.Objects;
import java.util.jar.JarFile;
import java.util.zip.ZipEntry;

/**
 * An adapted version of SkyHanni's version of the SkyblockAddons Installer Frame.
 * Contains changes to work with Fabric mods. It's also slightly bigger.
 * @author Biscuit, MisterCheezeCake
 */
public class SkyblockTweaksInstallerFrame extends JFrame implements ActionListener, MouseListener {
    private static final int TOTAL_HEIGHT = 459;
    private static final int TOTAL_WIDTH = 429;
    private JLabel logo = null;
    private JLabel versionInfo = null;
    private JLabel labelFolder = null;
    private JPanel panelCenter = null;
    private JPanel panelBottom = null;
    private JPanel totalContentPane = null;
    private JTextArea descriptionText = null;
    private JTextArea fabricDescriptionText = null;
    private JTextField textFieldFolderLocation = null;
    private JButton buttonChooseFolder = null;
    private JButton buttonInstall = null;
    private JButton buttonOpenFolder = null;
    private JButton buttonClose = null;
    private int x = 0;
    private int y = 0;

    private int w = TOTAL_WIDTH;
    private int h;
    private int margin;

    public SkyblockTweaksInstallerFrame() {
        try {
            setName("SkyblockTweaksInstallerFrame");
            setTitle("Skyblock Tweaks Installer");
            setResizable(false);
            setSize(TOTAL_WIDTH, TOTAL_HEIGHT);
            setContentPane(getPanelContentPane());

            getButtonFolder().addActionListener(this);
            getButtonInstall().addActionListener(this);
            getButtonOpenFolder().addActionListener(this);
            getButtonClose().addActionListener(this);
            getFabricTextArea().addMouseListener(this);

            pack();
            setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

            getFieldFolder().setText(getModsFolder().getPath());
            getButtonInstall().setEnabled(true);
            getButtonInstall().requestFocus();
        } catch (Exception ex) {
            showErrorPopup(ex);
        }
    }

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            SkyblockTweaksInstallerFrame frame = new SkyblockTweaksInstallerFrame();
            frame.centerFrame(frame);
            frame.setVisible(true);

        } catch (Exception ex) {
            showErrorPopup(ex);
        }
    }

    private static String getStacktraceText(Throwable ex) {
        StringWriter stringWriter = new StringWriter();
        ex.printStackTrace(new PrintWriter(stringWriter));
        return stringWriter.toString().replace("\t", "  ");
    }

    private static void showErrorPopup(Throwable ex) {
        ex.printStackTrace();

        JTextArea textArea = new JTextArea(getStacktraceText(ex));
        textArea.setEditable(false);
        Font currentFont = textArea.getFont();
        Font newFont = new Font(Font.MONOSPACED, currentFont.getStyle(), currentFont.getSize());
        textArea.setFont(newFont);

        JScrollPane errorScrollPane = new JScrollPane(textArea);
        errorScrollPane.setPreferredSize(new Dimension(600, 400));
        JOptionPane.showMessageDialog(null, errorScrollPane, "Error", JOptionPane.ERROR_MESSAGE);
    }

    private JPanel getPanelContentPane() {
        if (totalContentPane == null) {
            try {
                totalContentPane = new JPanel();
                totalContentPane.setName("PanelContentPane");
                totalContentPane.setLayout(new BorderLayout(5, 5));
                totalContentPane.setPreferredSize(new Dimension(TOTAL_WIDTH, TOTAL_HEIGHT));
                totalContentPane.add(getPanelCenter(), "Center");
                totalContentPane.add(getPanelBottom(), "South");
            } catch (Throwable ivjExc) {
                showErrorPopup(ivjExc);
            }
        }
        return totalContentPane;
    }

    private JPanel getPanelCenter() {
        if (panelCenter == null) {
            try {
                (panelCenter = new JPanel()).setName("PanelCenter");
                panelCenter.setLayout(null);
                panelCenter.add(getPictureLabel(), getPictureLabel().getName());
                panelCenter.add(getVersionInfo(), getVersionInfo().getName());
                panelCenter.add(getTextArea(), getTextArea().getName());
                panelCenter.add(getFabricTextArea(), getFabricTextArea().getName());
                panelCenter.add(getLabelFolder(), getLabelFolder().getName());
                panelCenter.add(getFieldFolder(), getFieldFolder().getName());
                panelCenter.add(getButtonFolder(), getButtonFolder().getName());
            } catch (Throwable ivjExc) {
                showErrorPopup(ivjExc);
            }
        }
        return panelCenter;
    }

    private JLabel getPictureLabel() {
        if (logo == null) {
            try {
                h = w / 2;
                margin = 5;

                BufferedImage myPicture = ImageIO.read(Objects.requireNonNull(getClass()
                        .getClassLoader()
                        .getResourceAsStream("assets/skyblocktweaks/icon.png"), "Logo not found."));
                Image scaled = myPicture.getScaledInstance(256, 256, Image.SCALE_SMOOTH);
                logo = new JLabel(new ImageIcon(scaled));
                logo.setName("Logo");
                logo.setBounds(x + margin, y + margin, w - margin * 2, h - margin);
                logo.setFont(new Font(Font.DIALOG, Font.BOLD, 18));
                logo.setHorizontalAlignment(SwingConstants.CENTER);
                logo.setPreferredSize(new Dimension(h * 742 / 537, h));

                y += h;
            } catch (Throwable ivjExc) {
                showErrorPopup(ivjExc);
            }
        }
        return logo;
    }

    private JLabel getVersionInfo() {
        if (versionInfo == null) {
            try {
                h = 25;

                versionInfo = new JLabel();
                versionInfo.setName("LabelMcVersion");
                versionInfo.setBounds(x, y, w, h);
                versionInfo.setFont(new Font(Font.DIALOG, Font.BOLD, 14));
                versionInfo.setHorizontalAlignment(SwingConstants.CENTER);
                versionInfo.setPreferredSize(new Dimension(w, h));
                versionInfo.setText("Skyblock Tweaks by MisterCheezeCake, Installer by Biscuit");

                y += h;
            } catch (Throwable ivjExc) {
                showErrorPopup(ivjExc);
            }
        }
        return versionInfo;
    }

    private JTextArea getTextArea() {
        if (descriptionText == null) {
            try {
                h = 60;
                margin = 10;

                descriptionText = new JTextArea();
                descriptionText.setName("TextArea");
                setStandardFormatting(descriptionText);
                descriptionText.setText(
                        "This installer will copy Skyblock Tweaks into your fabric mods folder for you, and replace any old versions that already exist. " +
                                "Close this if you prefer to do this yourself!");
                descriptionText.setWrapStyleWord(true);

                y += h;
            } catch (Throwable ivjExc) {
                showErrorPopup(ivjExc);
            }
        }
        return descriptionText;
    }

    private void setStandardFormatting(JTextArea descriptionText) {
        descriptionText.setBounds(x + margin, y + margin, w - margin * 2, h - margin);
        descriptionText.setEditable(false);
        descriptionText.setHighlighter(null);
        descriptionText.setEnabled(true);
        descriptionText.setFont(new Font(Font.DIALOG, Font.PLAIN, 12));
        descriptionText.setLineWrap(true);
        descriptionText.setOpaque(false);
        descriptionText.setPreferredSize(new Dimension(w - margin * 2, h - margin));
    }

    private JTextArea getFabricTextArea() {
        if (fabricDescriptionText == null) {
            try {
                h = 55;
                margin = 10;

                fabricDescriptionText = new JTextArea();
                fabricDescriptionText.setName("TextAreaFabric");
                setStandardFormatting(fabricDescriptionText);
                fabricDescriptionText.setText(
                        "However, you still need to install Fabric Loader and other dependencies in order to be able to run this mod. Click here for more information and download links.");
                fabricDescriptionText.setForeground(Color.BLUE.darker());
                fabricDescriptionText.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
                fabricDescriptionText.setWrapStyleWord(true);

                y += h;
            } catch (Throwable ivjExc) {
                showErrorPopup(ivjExc);
            }
        }
        return fabricDescriptionText;
    }

    private JLabel getLabelFolder() {
        if (labelFolder == null) {
            h = 16;
            w = 65;

            x += 10; // Padding

            try {
                labelFolder = new JLabel();
                labelFolder.setName("LabelFolder");
                labelFolder.setBounds(x, y + 2, w, h);
                labelFolder.setPreferredSize(new Dimension(w, h));
                labelFolder.setText("Mods Folder");
            } catch (Throwable ivjExc) {
                showErrorPopup(ivjExc);
            }

            x += w;
        }
        return labelFolder;
    }

    private JTextField getFieldFolder() {
        if (textFieldFolderLocation == null) {
            h = 20;
            w = 287;

            try {
                textFieldFolderLocation = new JTextField();
                textFieldFolderLocation.setName("FieldFolder");
                textFieldFolderLocation.setBounds(x, y, w, h);
                textFieldFolderLocation.setEditable(false);
                textFieldFolderLocation.setPreferredSize(new Dimension(w, h));
            } catch (Throwable ivjExc) {
                showErrorPopup(ivjExc);
            }

            x += w;
        }
        return textFieldFolderLocation;
    }

    private JButton getButtonFolder() {
        if (buttonChooseFolder == null) {
            h = 20;
            w = 25;

            x += 10; // Padding

            try {
                BufferedImage myPicture = ImageIO.read(Objects.requireNonNull(getClass()
                        .getClassLoader()
                        .getResourceAsStream("assets/skyblocktweaks/folder.png"), "Folder icon not found."));
                Image scaled = myPicture.getScaledInstance(w - 8, h - 6, Image.SCALE_SMOOTH);
                buttonChooseFolder = new JButton(new ImageIcon(scaled));
                buttonChooseFolder.setName("ButtonFolder");
                buttonChooseFolder.setBounds(x, y, w, h);
                buttonChooseFolder.setPreferredSize(new Dimension(w, h));
            } catch (Throwable ivjExc) {
                showErrorPopup(ivjExc);
            }
        }
        return buttonChooseFolder;
    }

    private JPanel getPanelBottom() {
        if (panelBottom == null) {
            try {
                panelBottom = new JPanel();
                panelBottom.setName("PanelBottom");
                panelBottom.setLayout(new FlowLayout(FlowLayout.CENTER, 15, 10));
                panelBottom.setPreferredSize(new Dimension(390, 55));
                panelBottom.add(getButtonInstall(), getButtonInstall().getName());
                panelBottom.add(getButtonOpenFolder(), getButtonOpenFolder().getName());
                panelBottom.add(getButtonClose(), getButtonClose().getName());
            } catch (Throwable ivjExc) {
                showErrorPopup(ivjExc);
            }
        }
        return panelBottom;
    }

    private JButton getButtonInstall() {
        if (buttonInstall == null) {
            w = 110;
            h = 26;

            try {
                buttonInstall = new JButton();
                buttonInstall.setName("ButtonInstall");
                buttonInstall.setPreferredSize(new Dimension(w, h));
                buttonInstall.setText("Install");
            } catch (Throwable ivjExc) {
                showErrorPopup(ivjExc);
            }
        }
        return buttonInstall;
    }

    private JButton getButtonOpenFolder() {
        if (buttonOpenFolder == null) {
            w = 140;
            h = 26;

            try {
                buttonOpenFolder = new JButton();
                buttonOpenFolder.setName("ButtonOpenFolder");
                buttonOpenFolder.setPreferredSize(new Dimension(w, h));
                buttonOpenFolder.setText("Open Mods Folder");
            } catch (Throwable ivjExc) {
                showErrorPopup(ivjExc);
            }
        }
        return buttonOpenFolder;
    }

    private JButton getButtonClose() {
        if (buttonClose == null) {
            w = 110;
            h = 26;

            try {
                (buttonClose = new JButton()).setName("ButtonClose");
                buttonClose.setPreferredSize(new Dimension(w, h));
                buttonClose.setText("Cancel");
            } catch (Throwable ivjExc) {
                showErrorPopup(ivjExc);
            }
        }
        return buttonClose;
    }

    public void onFolderSelect() {
        File currentDirectory = new File(getFieldFolder().getText());

        JFileChooser jFileChooser = new JFileChooser(currentDirectory);
        jFileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        jFileChooser.setAcceptAllFileFilterUsed(false);
        if (jFileChooser.showOpenDialog(this) == 0) {
            File newDirectory = jFileChooser.getSelectedFile();
            getFieldFolder().setText(newDirectory.getPath());
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == getButtonClose()) {
            dispose();
            System.exit(0);
        }
        if (e.getSource() == getButtonFolder()) {
            onFolderSelect();
        }
        if (e.getSource() == getButtonInstall()) {
            onInstall();
        }
        if (e.getSource() == getButtonOpenFolder()) {
            onOpenFolder();
        }
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        if (e.getSource() == getFabricTextArea()) {
            try {
                Desktop.getDesktop().browse(new URI(
                        "https://github.com/MisterCheezeCake/SkyblockTweaks/wiki/Download-Guide"));
            } catch (IOException | URISyntaxException ex) {
                showErrorPopup(ex);
            }
        }
    }

    public void onInstall() {
        try {
            File modsFolder = new File(getFieldFolder().getText());
            if (!modsFolder.exists()) {
                showErrorMessage("Folder not found: " + modsFolder.getPath());
                return;
            }
            if (!modsFolder.isDirectory()) {
                showErrorMessage("Not a folder: " + modsFolder.getPath());
                return;
            }
            tryInstall(modsFolder);
        } catch (Exception e) {
            showErrorPopup(e);
        }
    }

    private void tryInstall(File modsFolder) {
        File thisFile = getThisFile();

        if (thisFile != null) {
            // Subfolders no longer exist!
            boolean deletingFailure = false;
            if (modsFolder.isDirectory()) { // Delete in this current folder.
                boolean failed = findSkyblockTweaksAndDelete(modsFolder.listFiles());
                if (failed) deletingFailure = true;
            }
            if (deletingFailure) return;

            if (thisFile.isDirectory()) {
                showErrorMessage("This file is a directory... Are we in a development environment?");
                return;
            }

            try {
                Files.copy(thisFile.toPath(), new File(modsFolder, thisFile.getName()).toPath());
            } catch (Exception ex) {
                showErrorPopup(ex);
                return;
            }

            showMessage("Skyblock Tweaks has been successfully installed into your mods folder.");
            dispose();
            System.exit(0);
        }
    }

    private boolean findSkyblockTweaksAndDelete(File[] files) {
        if (files == null) return false;

        for (File file : files) {
            if (!file.isDirectory() && file.getPath().endsWith(".jar")) {
                try {
                    JarFile jarFile = new JarFile(file);
                    ZipEntry sbtMixinFile = jarFile.getEntry("skyblocktweaks.mixins.json");
                    // We can safely assume that a mod is SBT if it has a file named skyblocktweaks.mixins.json
                    // 1.8 mods got the mod id from the mcmod.info file, but since we are dealing with FMJs and I
                    // really don't want to deal with getting a json parser here, we use this
                    if (sbtMixinFile != null) {
                            jarFile.close();
                            try {
                                boolean deleted = file.delete();
                                if (!deleted) {
                                    throw new Exception();
                                }
                            } catch (Exception ex) {
                                ex.printStackTrace();
                                showErrorMessage("Was not able to delete the other Skyblock Tweaks files found in your mods folder!" +
                                        System.lineSeparator() +
                                        "Please make sure that your minecraft is currently closed and try again, or feel" +
                                        System.lineSeparator() +
                                        "free to open your mods folder and delete those files manually.");
                                return true;
                            }
                            continue;
                    }
                    jarFile.close();
                } catch (Exception ex) {
                    // Just don't check the file I guess, move on to the next...
                }
            }
        }
        return false;
    }

    public void onOpenFolder() {
        try {
            Desktop.getDesktop().open(getModsFolder());
        } catch (Exception e) {
            showErrorPopup(e);
        }
    }

    public File getModsFolder() {
        String userHome = System.getProperty("user.home", ".");

        File modsFolder = getFile(userHome, "minecraft/mods");
        if (!modsFolder.exists() && !modsFolder.mkdirs()) {
            throw new RuntimeException("The working directory could not be created: " + modsFolder);
        }
        return modsFolder;
    }

    public File getFile(String userHome, String minecraftPath) {
        File workingDirectory;
        switch (getOperatingSystem()) {
            case LINUX:
            case SOLARIS: {
                workingDirectory = new File(userHome, '.' + minecraftPath + '/');
                break;
            }
            case WINDOWS: {
                String applicationData = System.getenv("APPDATA");
                if (applicationData != null) {
                    workingDirectory = new File(applicationData, "." + minecraftPath + '/');
                    break;
                }
                workingDirectory = new File(userHome, '.' + minecraftPath + '/');
                break;
            }
            case MACOS: {
                workingDirectory = new File(userHome, "Library/Application Support/" + minecraftPath);
                break;
            }
            default: {
                workingDirectory = new File(userHome, minecraftPath + '/');
                break;
            }
        }
        return workingDirectory;
    }

    public OperatingSystem getOperatingSystem() {
        String osName = System.getProperty("os.name").toLowerCase(Locale.US);
        if (osName.contains("win")) {
            return OperatingSystem.WINDOWS;

        } else if (osName.contains("mac")) {
            return OperatingSystem.MACOS;

        } else if (osName.contains("solaris") || osName.contains("sunos")) {

            return OperatingSystem.SOLARIS;
        } else if (osName.contains("linux") || osName.contains("unix")) {

            return OperatingSystem.LINUX;
        }
        return OperatingSystem.UNKNOWN;
    }

    public void centerFrame(JFrame frame) {
        Rectangle rectangle = frame.getBounds();
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        Rectangle screenRectangle = new Rectangle(0, 0, screenSize.width, screenSize.height);

        int newX = screenRectangle.x + (screenRectangle.width - rectangle.width) / 2;
        int newY = screenRectangle.y + (screenRectangle.height - rectangle.height) / 2;

        if (newX < 0) newX = 0;
        if (newY < 0) newY = 0;

        frame.setBounds(newX, newY, rectangle.width, rectangle.height);
    }

    public void showMessage(String message) {
        JOptionPane.showMessageDialog(null, message, "Skyblock Tweaks", JOptionPane.INFORMATION_MESSAGE);
    }

    public void showErrorMessage(String message) {
        JOptionPane.showMessageDialog(null, message, "Skyblock Tweaks - Error", JOptionPane.ERROR_MESSAGE);
    }

    private File getThisFile() {
        try {
            return new File(SkyblockTweaksInstallerFrame.class
                    .getProtectionDomain()
                    .getCodeSource()
                    .getLocation()
                    .toURI());
        } catch (URISyntaxException ex) {
            showErrorPopup(ex);
        }
        return null;
    }

    @Override public void mousePressed(MouseEvent e) {}

    @Override public void mouseReleased(MouseEvent e) {}

    @Override public void mouseEntered(MouseEvent e) {}

    @Override public void mouseExited(MouseEvent e) {}

    public enum OperatingSystem {
        LINUX,
        SOLARIS,
        WINDOWS,
        MACOS,
        UNKNOWN
    }
}