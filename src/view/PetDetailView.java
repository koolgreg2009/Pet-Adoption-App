package view;

import java.awt.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.net.URL;
import javax.swing.*;

import dto.PetDTO;
import interface_adapter.display_pets.DisplayPetsController;
import interface_adapter.get_breed.GetBreedController;
import interface_adapter.pet_bio.PetBioState;
import interface_adapter.pet_bio.PetBioViewModel;

/**
 * The {@code PetDetailView} class represents a detailed view of a pet's information.
 * It displays various details about a pet including name, breed, age, species, bio, location, contact details, and an image.
 * This class also handles updating the pet details when the state changes and provides functionality to view the pet's breed and navigate back.
 */
public class PetDetailView extends JPanel implements PropertyChangeListener {
    private static final long serialVersionUID = 1L;
    public static final String viewName = "Pet Bio";
    private final DisplayPetsController displayPetsController;
    private final GetBreedController getBreedController;

    private javax.swing.JTextField ageText;
    private javax.swing.JButton backButton;
    private javax.swing.JButton breedButton;
    private javax.swing.JTextField breedText;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JTextField nameText;
    private javax.swing.JTextField speciesesText;
    private JLabel imageLabel;
    private JLabel jLabelBio;
    private JTextArea bioText;
    private JLabel jLabelLocation;
    private JTextField locationText;
    private JLabel jLabelEmail;
    private JTextField emailText;
    private JLabel jLabelPhoneNum;
    private JTextField phoneNumText;
    private JLabel jLabelActivityLevel;
    private JTextField activityLevelText;
    private JScrollPane bioScrollPane;

    private String viewUser;
    private int imageRequest;

    /**
     * Constructs a {@code PetDetailView} with the specified {@code PetBioViewModel}, {@code DisplayPetsController}, and {@code GetBreedController}.
     *
     * @param petDetailViewModel the {@code PetBioViewModel} that provides the pet's details and manages state changes.
     * @param displayPetsController the {@code DisplayPetsController} used to navigate back to the pet listings.
     * @param getBreedController the {@code GetBreedController} used to retrieve breed information.
     */
    public PetDetailView(PetBioViewModel petDetailViewModel, DisplayPetsController displayPetsController, GetBreedController getBreedController) {
        initComponents();
        this.displayPetsController = displayPetsController;
        this.getBreedController = getBreedController;
        petDetailViewModel.addPropertyChangeListener(this);
    }

    /**
     * Initializes the UI components and layout of the {@code PetDetailView}.
     * This method sets up labels, text fields, buttons, and the layout of the panel.
     */
    @SuppressWarnings("unchecked")
    private void initComponents() {
        jLabel1 = new javax.swing.JLabel();
        nameText = new javax.swing.JTextField();
        breedButton = new JButton();
        breedText = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        ageText = new javax.swing.JTextField();
        jLabel5 = new javax.swing.JLabel();
        speciesesText = new javax.swing.JTextField();
        backButton = new javax.swing.JButton();
        jLabelBio = new JLabel();
        bioText = new JTextArea();
        jLabelLocation = new JLabel();
        locationText = new javax.swing.JTextField();
        jLabelPhoneNum = new JLabel();
        phoneNumText = new javax.swing.JTextField();
        jLabelEmail = new JLabel();
        emailText = new javax.swing.JTextField();
        jLabelActivityLevel = new JLabel();
        activityLevelText = new javax.swing.JTextField();

        jLabel1.setText("Name:");

        nameText.setEditable(false);
        nameText.setBackground(Color.white);
        nameText.setBorder(null);

        imageLabel = new JLabel();
        imageLabel.setHorizontalAlignment(SwingConstants.CENTER);
        imageLabel.setBorder(BorderFactory.createTitledBorder("Pet Image"));

        imageLabel.setHorizontalAlignment(SwingConstants.CENTER);
        imageLabel.setVerticalAlignment(SwingConstants.CENTER);
        breedText.setEditable(false);
        breedText.setBackground(Color.white);
        breedText.setBorder(null);
        breedButton.setText("Breed:");
        breedButton.setBorderPainted(false);
        breedButton.setBorder(null);
        breedButton.setContentAreaFilled(false);
        breedButton.setFocusPainted(false);
        breedButton.setOpaque(false);
        breedButton.setForeground(nameText.getForeground()); // Match the text color
        breedButton.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                breedButtonActionPerformed(evt);
            }
        });

        jLabel3.setText("Age:");

        ageText.setEditable(false);
        ageText.setBackground(Color.white);
        ageText.setBorder(null);

        jLabel5.setText("Species:");

        speciesesText.setEditable(false);
        speciesesText.setBackground(Color.white);
        speciesesText.setBorder(null);

        jLabelBio.setText("Bio:");

        bioText.setEditable(false);
        bioText.setBackground(Color.white);
        bioText.setBorder(null);
        bioText.setLineWrap(true);
        bioText.setWrapStyleWord(true);
        JScrollPane bioScrollPane = new JScrollPane(bioText);
        bioScrollPane.setBorder(null);

        jLabelLocation.setText("Location:");
        locationText.setEditable(false);
        locationText.setBackground(Color.white);
        locationText.setBorder(null);

        jLabelPhoneNum.setText("Phone Number:");
        phoneNumText.setEditable(false);
        phoneNumText.setBackground(Color.white);
        phoneNumText.setBorder(null);

        jLabelEmail.setText("Email:");
        emailText.setEditable(false);
        emailText.setBackground(Color.white);
        emailText.setBorder(null);

        jLabelActivityLevel.setText("Activity Level:");
        activityLevelText.setEditable(false);
        activityLevelText.setBackground(Color.white);
        activityLevelText.setBorder(null);

        backButton.setText("<Back");
        backButton.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                backButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(layout.createSequentialGroup()
                                .addContainerGap()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                        .addComponent(backButton)
                                        .addGroup(layout.createSequentialGroup()
                                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                        .addComponent(jLabel1)
                                                        .addComponent(breedButton)
                                                        .addComponent(jLabel3)
                                                        .addComponent(jLabel5)
                                                        .addComponent(jLabelLocation)
                                                        .addComponent(jLabelBio)
                                                        .addComponent(jLabelEmail)
                                                        .addComponent(jLabelPhoneNum)
                                                        .addComponent(jLabelActivityLevel))
                                                .addGap(38, 38, 38)
                                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                                        .addComponent(nameText)
                                                        .addComponent(breedText)
                                                        .addComponent(ageText)
                                                        .addComponent(speciesesText, javax.swing.GroupLayout.DEFAULT_SIZE, 298, Short.MAX_VALUE)
                                                        .addComponent(locationText)
                                                        .addComponent(bioScrollPane, javax.swing.GroupLayout.DEFAULT_SIZE, 800, Short.MAX_VALUE)
                                                        .addComponent(emailText)
                                                        .addComponent(phoneNumText)
                                                        .addComponent(activityLevelText))))
                                .addContainerGap(18, Short.MAX_VALUE))
                        .addComponent(imageLabel, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 400, Short.MAX_VALUE)
        );

        layout.setVerticalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(layout.createSequentialGroup()
                                .addContainerGap()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(jLabel1)
                                        .addComponent(nameText, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(breedButton)
                                        .addComponent(breedText, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGap(12, 12, 12)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(jLabel3)
                                        .addComponent(ageText, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addComponent(jLabel5)
                                        .addComponent(speciesesText, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(jLabelLocation)
                                        .addComponent(locationText, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addComponent(jLabelBio)
                                        .addComponent(bioScrollPane, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(jLabelActivityLevel)
                                        .addComponent(activityLevelText, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(jLabelEmail)
                                        .addComponent(emailText, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(jLabelPhoneNum)
                                        .addComponent(phoneNumText, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(imageLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(backButton)
                                .addContainerGap(95, Short.MAX_VALUE))
        );
    }

    /**
     * Handles the action event when the back button is clicked.
     * This method triggers the {@code DisplayPetsController} to execute and navigate back to the pet listings.
     *
     * @param evt the action event triggered by clicking the back button.
     */
    private void backButtonActionPerformed(java.awt.event.ActionEvent evt) {
        displayPetsController.execute(viewUser);
    }

    /**
     * Handles the action event when the breed button is clicked.
     * This method triggers the {@code GetBreedController} to execute and retrieve information about the pet's breed.
     *
     * @param evt the action event triggered by clicking the breed button.
     */
    private void breedButtonActionPerformed(java.awt.event.ActionEvent evt) {
        getBreedController.execute(breedText.getText());
    }

    /**
     * Listens for property changes in the {@code PetBioViewModel}.
     * When a property change event occurs, this method updates the pet details displayed on the panel
     * and adjusts the pet image according to its URL.
     *
     * @param evt the property change event containing the updated {@code PetBioState}.
     */
    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if ("state".equals(evt.getPropertyName()) && evt.getNewValue() instanceof PetBioState) {
            PetBioState state = (PetBioState) evt.getNewValue();
            this.viewUser = state.getViewUser();
            PetDTO pet = state.getPet();
            nameText.setText(pet.getName());
            breedText.setText(pet.getBreed());
            ageText.setText("" + pet.getPetAge() + " year(s) old");
            speciesesText.setText(pet.getSpecies());
            bioText.setText(pet.getBio());
            locationText.setText(pet.getLocation());
            emailText.setText(pet.getEmail());
            phoneNumText.setText(pet.getPhoneNum());
            activityLevelText.setText(pet.getActivityLevel());

            final int request = ++imageRequest;
            imageLabel.setText("Loading image...");
            imageLabel.setIcon(null);
            new SwingWorker<ImageIcon, Void>() {
                @Override protected ImageIcon doInBackground() throws Exception {
                    if (pet.getImgUrl() == null || pet.getImgUrl().isBlank()) return null;
                    java.net.URLConnection connection = new URL(pet.getImgUrl()).openConnection();
                    connection.setConnectTimeout(5000);
                    connection.setReadTimeout(5000);
                    try (java.io.InputStream input = connection.getInputStream()) {
                        java.awt.image.BufferedImage image = javax.imageio.ImageIO.read(input);
                        return image == null ? null : new ImageIcon(image);
                    }
                }
                @Override protected void done() {
                    if (request != imageRequest) return;
                    try {
                        ImageIcon icon = get();
                        if (icon == null) throw new IllegalStateException();
                        double ratio = Math.min(400.0 / icon.getIconWidth(), 300.0 / icon.getIconHeight());
                        imageLabel.setIcon(new ImageIcon(icon.getImage().getScaledInstance(
                                Math.max(1, (int) (icon.getIconWidth() * ratio)),
                                Math.max(1, (int) (icon.getIconHeight() * ratio)), Image.SCALE_SMOOTH)));
                        imageLabel.setText("");
                    } catch (Exception e) {
                        imageLabel.setText("Image not available");
                    }
                }
            }.execute();
        }

        if ("notification".equals(evt.getPropertyName())){
            PetBioState state = (PetBioState) evt.getNewValue();
            JOptionPane.showMessageDialog(this, state.getNotification());
        }
    }
}
