import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class PlayerGUI extends JFrame {
    public static final Color FRAME_COLOR = Color.DARK_GRAY;
    public static final Color TEXT_COLOR = Color.GRAY;

    public PlayerGUI() {
        super("DeskTunes");
        setSize(400, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(true);
        setLayout(null);
        getContentPane().setBackground(FRAME_COLOR);
        addGuiComponents();
    }

    private void addGuiComponents() {
        //add toolbar
        addToolbar();

        //add song image
        ImageIcon originalIcon = new ImageIcon("assets/record.png");
        Image scaledImage = originalIcon.getImage().getScaledInstance(225, 225, Image.SCALE_SMOOTH);
        ImageIcon scaledIcon = new ImageIcon(scaledImage);
        JLabel songImage = new JLabel(scaledIcon);
        songImage.setBounds(200 - 112, 50, 225, 225);
        add(songImage);

        //add song title
        JLabel songTitle = new JLabel("Song Title", JLabel.CENTER);
        songTitle.setBounds(0, 310, getWidth() - 10, 40);
        songTitle.setFont(new Font("Jetbrains Mono", Font.BOLD, 32));
        songTitle.setForeground(TEXT_COLOR);
        songTitle.setHorizontalAlignment(SwingConstants.CENTER);
        add(songTitle);

        //add song artist
        JLabel songArtist = new JLabel("Song Artist", JLabel.CENTER);
        songArtist.setBounds(0, 360, getWidth() - 10, 30);
        songArtist.setFont(new Font("Jetbrains Mono", Font.BOLD, 24));
        songArtist.setForeground(TEXT_COLOR);
        songArtist.setHorizontalAlignment(SwingConstants.CENTER);
        add(songArtist);

        //add progress slider
        JSlider playbackSlider = new JSlider(JSlider.HORIZONTAL, 0, 100, 0);
        playbackSlider.setBounds(getWidth()/2 - 300/2, 410, 300, 30);
        playbackSlider.setBackground(null);
        add(playbackSlider);

        addPlaybackButtons();
    }

    private void addToolbar() {
        JToolBar toolbar = new JToolBar();
        toolbar.setBounds(0, 0, getWidth(), 20);
        toolbar.setFloatable(false);

        JMenuBar menuBar = new JMenuBar();
        toolbar.add(menuBar);

        JMenu songMenu = new JMenu("Song");
        menuBar.add(songMenu);

        JMenuItem loadSong = new JMenuItem("Load Song");
        songMenu.add(loadSong);

        JMenu playlistMenu = new JMenu("Playlist");
        menuBar.add(playlistMenu);

        JMenuItem createPlaylist = new JMenuItem("Create Playlist");
        playlistMenu.add(createPlaylist);

        JMenuItem loadPlaylist = new JMenuItem("Load Playlist");
        playlistMenu.add(loadPlaylist);



        add(toolbar);
    }

    private void addPlaybackButtons() {
        JPanel playbackButtons = new JPanel();
        playbackButtons.setBounds(0, 450, getWidth() - 10, 80);
        playbackButtons.setBackground(null);

        JButton prevButton = new JButton(loadImage("assets/prev.png"));
        prevButton.setBorderPainted(false);
        prevButton.setBackground(null);
        playbackButtons.add(prevButton);

        JButton playButton = new JButton(loadImage("assets/play.png"));
        playButton.setBorderPainted(false);
        playButton.setBackground(null);
        playbackButtons.add(playButton);

        JButton pauseButton = new JButton(loadImage("assets/pause.png"));
        pauseButton.setBorderPainted(false);
        pauseButton.setBackground(null);
        playbackButtons.add(pauseButton);

        JButton nextButton = new JButton(loadImage("assets/next.png"));
        nextButton.setBorderPainted(false);
        nextButton.setBackground(null);
        playbackButtons.add(nextButton);

        add(playbackButtons);
    }

    private ImageIcon loadImage(String path) {
        try {
            BufferedImage image = ImageIO.read(new File(path));
            return new ImageIcon(image);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

}













