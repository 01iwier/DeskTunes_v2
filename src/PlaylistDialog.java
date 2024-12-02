import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;

public class PlaylistDialog extends JDialog {
    private PlayerGUI playerGUI;
    private ArrayList<String> songPaths;

    public PlaylistDialog(PlayerGUI playerGUI) {
        this.playerGUI = playerGUI;
        this.songPaths = new ArrayList<>();
        setTitle("Create Playlist");
        setSize(500, 500);
        setResizable(false);
        getContentPane().setBackground(PlayerGUI.FRAME_COLOR);
        setLayout(null);
        setModal(true);
        setLocationRelativeTo(playerGUI);

        addDialogComponents();
    }

    private void addDialogComponents() {
        JPanel songContainer = new JPanel();
        songContainer.setLayout(new BoxLayout(songContainer, BoxLayout.Y_AXIS));
        songContainer.setBounds((int)(getWidth() * 0.025), 10, (int)(getWidth() * 0.9), (int)(getHeight() * 0.75));
        add(songContainer);

        JButton addSongButton = new JButton("Add");
        addSongButton.setBounds(60, (int)(getHeight() * 0.8), 100, 25);
        addSongButton.setFont(new Font("JetBrains Mono", Font.BOLD, 14));
        addSongButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFileChooser chooser = new JFileChooser();
                chooser.setFileFilter(new FileNameExtensionFilter("MP3", "mp3"));
                chooser.setCurrentDirectory(new File("assets/mp3"));
                int result = chooser.showOpenDialog(PlaylistDialog.this);
                File file = chooser.getSelectedFile();
                if (result == JFileChooser.APPROVE_OPTION && file != null) {
                    JLabel filePathLabel = new JLabel(file.getPath());
                    filePathLabel.setFont(new Font("JetBrains Mono", Font.BOLD, 12));
                    filePathLabel.setBorder(BorderFactory.createLineBorder(Color.black));
                    songPaths.add(filePathLabel.getText());
                    songContainer.add(filePathLabel);
                    songContainer.revalidate();
                }
            }
        });
        add(addSongButton);

        JButton savePlaylistButton = new JButton("Save");
        savePlaylistButton.setBounds(215, (int)(getHeight() * 0.8), 100, 25);
        savePlaylistButton.setFont(new Font("JetBrains Mono", Font.BOLD, 14));
        savePlaylistButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    JFileChooser chooser = new JFileChooser();
                    chooser.setCurrentDirectory(new File("assets/mp3"));
                    int result = chooser.showSaveDialog(PlaylistDialog.this);

                    if (result == JFileChooser.APPROVE_OPTION) {
                        File file = chooser.getSelectedFile();
                        if (!file.getName().substring(file.getName().length() - 4).equalsIgnoreCase(".txt")) {
                            file = new File(file.getAbsolutePath() + ".txt");
                            file.createNewFile();
                        }
                        FileWriter fileWriter = new FileWriter(file);
                        BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
                        for (String songPath : songPaths) {
                            bufferedWriter.write(songPath + "\n");
                        }
                        bufferedWriter.close();
                        JOptionPane.showMessageDialog(PlaylistDialog.this, "Playlist Saved");
                        PlaylistDialog.this.dispose();
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }

            }
        });
        add(savePlaylistButton);
    }
}
