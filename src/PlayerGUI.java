import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Hashtable;

public class PlayerGUI extends JFrame {
    public static final Color FRAME_COLOR = Color.DARK_GRAY;
    public static final Color TEXT_COLOR = Color.GRAY;

    private MusicPlayer musicPlayer;
    private JFileChooser jFileChooser;

    private JLabel songTitle, songArtist;
    private JSlider progressSlider;
    private JPanel playbackButtons;


    public PlayerGUI() {
        super("DeskTunes");
        setSize(400, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);
        setLayout(null);
        getContentPane().setBackground(FRAME_COLOR);

        musicPlayer = new MusicPlayer(this);

        jFileChooser = new JFileChooser();
        jFileChooser.setCurrentDirectory(new File("assets/mp3"));
        jFileChooser.setFileFilter(new FileNameExtensionFilter("MP3", "mp3"));

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
        songTitle = new JLabel("Song Title", JLabel.CENTER);
        songTitle.setBounds(0, 310, getWidth() - 10, 40);
        songTitle.setFont(new Font("Jetbrains Mono", Font.BOLD, 28));
        songTitle.setForeground(TEXT_COLOR);
        songTitle.setHorizontalAlignment(SwingConstants.CENTER);
        add(songTitle);

        //add song artist
        songArtist = new JLabel("Song Artist", JLabel.CENTER);
        songArtist.setBounds(0, 360, getWidth() - 10, 30);
        songArtist.setFont(new Font("Jetbrains Mono", Font.BOLD, 20));
        songArtist.setForeground(TEXT_COLOR);
        songArtist.setHorizontalAlignment(SwingConstants.CENTER);
        add(songArtist);

        //add progress slider
        progressSlider = new JSlider(JSlider.HORIZONTAL, 0, 100, 0);
        progressSlider.setBounds(getWidth()/2 - 300/2, 410, 300, 30);
        progressSlider.setBackground(null);
        progressSlider.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                musicPlayer.pauseSong();
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                JSlider source = (JSlider) e.getSource();
                int frame = source.getValue();
                musicPlayer.setCurrentFrame(frame);
                musicPlayer.setCurrentTimeInMs((int) (frame / ((2.08) * musicPlayer.getCurrentSong().getFrameRatePerMs())));
                musicPlayer.playCurrentSong();
                enablePause();
            }
        });
        add(progressSlider);

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
        loadSong.addActionListener(_ -> {
            int result = jFileChooser.showOpenDialog(PlayerGUI.this);
            File selectedFile = jFileChooser.getSelectedFile();
            if (result == JFileChooser.APPROVE_OPTION && selectedFile != null) {
                Song song = new Song(selectedFile.getPath());
                musicPlayer.loadSong(song);
                updateInfo(song);
                updateProgressSlider(song);
                enablePause();
            }
        });
        songMenu.add(loadSong);

        JMenu playlistMenu = new JMenu("Playlist");
        menuBar.add(playlistMenu);

        JMenuItem createPlaylist = new JMenuItem("Create Playlist");
        createPlaylist.addActionListener(_ -> new PlaylistDialog(PlayerGUI.this).setVisible(true));
        playlistMenu.add(createPlaylist);

        JMenuItem loadPlaylist = new JMenuItem("Load Playlist");
        loadPlaylist.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFileChooser chooser = new JFileChooser();
                chooser.setFileFilter(new FileNameExtensionFilter("Playlist", "txt"));
                chooser.setCurrentDirectory(new File("assets/mp3"));
                int result = chooser.showOpenDialog(PlayerGUI.this);
                File selectedFile = chooser.getSelectedFile();
                if (result == JFileChooser.APPROVE_OPTION && selectedFile != null) {
                    musicPlayer.stopSong();
                    musicPlayer.loadPlaylist(selectedFile);
                }
            }
        });
        playlistMenu.add(loadPlaylist);

        add(toolbar);
    }

    private void addPlaybackButtons() {
        playbackButtons = new JPanel();
        playbackButtons.setBounds(0, 450, getWidth() - 10, 80);
        playbackButtons.setBackground(null);

        JButton prevButton = new JButton(loadImage("assets/prev.png"));
        prevButton.setBorderPainted(false);
        prevButton.setBackground(null);
        prevButton.addActionListener(_ -> {
            musicPlayer.prevSong();
        });
        playbackButtons.add(prevButton);

        JButton playButton = new JButton(loadImage("assets/play.png"));
        playButton.setBorderPainted(false);
        playButton.setBackground(null);
        playButton.addActionListener(_ -> {
            enablePause();
            musicPlayer.playCurrentSong();
        });
        playbackButtons.add(playButton);

        JButton pauseButton = new JButton(loadImage("assets/pause.png"));
        pauseButton.setBorderPainted(false);
        pauseButton.setBackground(null);
        pauseButton.setVisible(false);
        pauseButton.addActionListener(_ -> {
            enablePlay();
            musicPlayer.pauseSong();
        });
        playbackButtons.add(pauseButton);

        JButton nextButton = new JButton(loadImage("assets/next.png"));
        nextButton.setBorderPainted(false);
        nextButton.setBackground(null);
        nextButton.addActionListener(_ -> {
            musicPlayer.nextSong();
        });
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

    public void updateInfo(Song song) {
        songTitle.setText(song.getSongTitle());
        songArtist.setText(song.getSongArtist());
    }

    public void setProgressSlider(int frame) {
        progressSlider.setValue(frame);
    }

    public void updateProgressSlider(Song song) {
        progressSlider.setMaximum(song.getMp3File().getFrameCount());
        Hashtable<Integer, JLabel> labelTable = new Hashtable<>();
        JLabel labelBegin = new JLabel("00:00");
        labelBegin.setFont(new Font("Jetbrains Mono", Font.PLAIN, 18));
        labelBegin.setForeground(TEXT_COLOR);

        JLabel labelEnd = new JLabel(song.getSongLength());
        labelEnd.setFont(new Font("Jetbrains Mono", Font.PLAIN, 18));
        labelEnd.setForeground(TEXT_COLOR);

        labelTable.put(0, labelBegin);
        labelTable.put(song.getMp3File().getFrameCount(), labelEnd);

        progressSlider.setLabelTable(labelTable);
        progressSlider.setPaintLabels(true);
    }

    public void enablePause() {
        JButton playButton = (JButton) playbackButtons.getComponent(1);
        JButton pauseButton = (JButton) playbackButtons.getComponent(2);
        playButton.setVisible(false);
        pauseButton.setVisible(true);
        playButton.setEnabled(false);
        pauseButton.setEnabled(true);
    }

    public void enablePlay() {
        JButton playButton = (JButton) playbackButtons.getComponent(1);
        JButton pauseButton = (JButton) playbackButtons.getComponent(2);
        playButton.setVisible(true);
        pauseButton.setVisible(false);
        playButton.setEnabled(true);
        pauseButton.setEnabled(false);
    }

}













