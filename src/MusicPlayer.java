import javazoom.jl.player.advanced.AdvancedPlayer;
import javazoom.jl.player.advanced.PlaybackEvent;
import javazoom.jl.player.advanced.PlaybackListener;

import java.io.*;
import java.util.ArrayList;

public class MusicPlayer extends PlaybackListener {
    private static final Object playSignal = new Object();
    private boolean isPaused, isFinished, pressedPrev, pressedNext;
    private ArrayList<Song> playlist;
    private int currentPlaylistIndex;

    private Song currentSong;
    public Song getCurrentSong() {
        return currentSong;
    }

    private PlayerGUI playerGui;
    private AdvancedPlayer advancedPlayer;

    private int currentFrame;
    public void setCurrentFrame(int frame) {
        currentFrame = frame;
    }

    private int currentTimeInMs;
    public void setCurrentTimeInMs(int timeInMs) {
        currentTimeInMs = timeInMs;
    }

    public MusicPlayer(PlayerGUI gui) {
        this.playerGui = gui;
    }

    public void loadSong(Song song) {
        currentSong = song;
        playlist = null;

        if (!isFinished) {
            stopSong();
        }

        if (currentSong != null) {
            currentFrame = 0;
            currentTimeInMs = 0;
            playerGui.setProgressSlider(0);
            playCurrentSong();
        }
    }

    public void loadPlaylist(File playlistFile) {
        playlist = new ArrayList<>();
        try {
            FileReader fr = new FileReader(playlistFile);
            BufferedReader br = new BufferedReader(fr);
            String songPath;
            while ((songPath = br.readLine()) != null) {
                Song song = new Song(songPath);
                playlist.add(song);
            }

            if (playlist.size() > 0) {
                playerGui.setProgressSlider(0);
                currentTimeInMs = 0;
                currentSong = playlist.get(0);
                currentFrame = 0;
                playerGui.enablePlay();
                playerGui.updateInfo(currentSong);
                playerGui.updateProgressSlider(currentSong);
                playCurrentSong();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void pauseSong() {
        if (advancedPlayer != null) {
            isPaused = true;
            stopSong();
        }
    }

    public void stopSong() {
        if (advancedPlayer != null) {
            advancedPlayer.stop();
            advancedPlayer.close();
            advancedPlayer = null;
        }
    }

    public void nextSong() {
        if (playlist == null || (currentPlaylistIndex + 1) >= playlist.size()) return;
        pressedNext = true;
        if (!isFinished) {
            stopSong();
        }
        currentPlaylistIndex++;
        currentSong = playlist.get(currentPlaylistIndex);
        currentFrame = 0;
        currentTimeInMs = 0;
        playerGui.enablePause();
        playerGui.updateInfo(currentSong);
        playerGui.updateProgressSlider(currentSong);

        playCurrentSong();
    }

    public void prevSong() {
        if (playlist == null || (currentPlaylistIndex - 1) < 0) return;
        pressedPrev = true;
        stopSong();
        currentPlaylistIndex--;
        currentSong = playlist.get(currentPlaylistIndex);
        currentFrame = 0;
        currentTimeInMs = 0;
        playerGui.enablePause();
        playerGui.updateInfo(currentSong);
        playerGui.updateProgressSlider(currentSong);

        playCurrentSong();
    }

    public void playCurrentSong() {
        if (currentSong == null) return;

        try {
            FileInputStream fileInputStream = new FileInputStream(currentSong.getFilePath());
            BufferedInputStream bufferedInputStream = new BufferedInputStream(fileInputStream);

            advancedPlayer = new AdvancedPlayer(bufferedInputStream);
            advancedPlayer.setPlayBackListener(this);

            startMusicThread();
            startProgressSliderThread();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void startMusicThread() {
        new Thread(() -> {
            try {
                if (isPaused) {
                    synchronized (playSignal) {
                        isPaused = false;
                        playSignal.notify();
                    }
                    advancedPlayer.play(currentFrame, Integer.MAX_VALUE);
                } else {
                    advancedPlayer.play();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

    private void startProgressSliderThread() {
        new Thread(() -> {
            if (isPaused) {
                try {
                    synchronized (playSignal) {
                        playSignal.wait();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            while (!isPaused && !isFinished && !pressedPrev && !pressedNext) {
                try {
                    currentTimeInMs++;
                    int calculatedFrame = (int) ((double) currentTimeInMs * 2.08 * currentSong.getFrameRatePerMs());
                    playerGui.setProgressSlider(calculatedFrame);
                    Thread.sleep(1);
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        }).start();
    }

    @Override
    public void playbackStarted(PlaybackEvent evt) {
        isFinished = false;
        pressedPrev = false;
        pressedNext = false;
    }

    @Override
    public void playbackFinished(PlaybackEvent evt) {
        if (isPaused) {
            currentFrame += (int) ((double) evt.getFrame() * currentSong.getFrameRatePerMs());
        } else {
            if (pressedNext || pressedPrev) return;
            isFinished = true;
            if (playlist == null) {
                playerGui.enablePlay();
            } else {
                if (currentPlaylistIndex == playlist.size() - 1) {
                    playerGui.enablePlay();
                } else {
                    nextSong();
                }
            }
        }
    }
}
