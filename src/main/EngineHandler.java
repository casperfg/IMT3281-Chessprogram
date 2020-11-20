package main;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Closeable;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.security.CodeSource;
import java.security.ProtectionDomain;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;

// https://stackoverflow.com/questions/600146/run-exe-which-is-packaged-inside-jar-file
// https://github.com/rahular/chess-misc/blob/master/JavaStockfish/src/com/rahul/stockfish/Stockfish.java
public class EngineHandler {
    public int eloRating;
    public int thinkTime = 1000; // ms
    private Process engine;
    private BufferedReader processReader;
    private OutputStreamWriter processWriter;
    private engineWorker worker; // workThread that lets engine wait and calculate
    private static String PATH = "stockfishWin.exe"; // path for window
    private static final String PATHmac = "stockfish"; // path max

    public EngineHandler() { // start the engine
        startEngine();
    }

    public EngineHandler(int elo, int thkTime) { // start the engine
        eloRating = elo;
        thinkTime = thkTime; // with time and elo.
        startEngine();
    }

    public void getBest(Chessboard cboard) { // get best move (start thread)
        worker = new engineWorker(processReader, processWriter, thinkTime, cboard); // start new thread
        worker.start(); // make new Workerthread and start it
    }

    public String checkWorker() { // check if the workerthread is finished
        if (worker != null) {
            if (worker.done) { // worker is done calculating
                worker.done = false; // reset done
                return worker.Best;
            }
        }
        return "-1"; // still not done
    }

    public boolean checkMate() {
        return worker.playerMated;
    }
    public String jarFuckery(String Path)throws URISyntaxException,
            ZipException,
            IOException {
        final URI uri;
        final URI exe; // makes a copy of the .exe file that is in the jar
        uri = getJarURI(); // puts it on the computer. makes sure it is executable
        URI getFileRet = getFile(uri, Path); // and uses the path to that file.
        File exeFile = new File(getFileRet); // deletes the file when the program exits.
        exeFile.setExecutable(true);
        return getFileRet.getPath();
    }

    private void startEngine() { // works for both windows and max
        String os = System.getProperty("os.name"); // get operatingsystem name
        if (!os.contains("Windows")) { // mac functionality
            PATH = PATHmac; // set path to the mac path
        }
        try {
            engine = Runtime.getRuntime().exec(jarFuckery(PATH)); // execute exe
            // make reader / writer stream
            processReader = new BufferedReader(new InputStreamReader(engine.getInputStream()));
            processWriter = new OutputStreamWriter(engine.getOutputStream());
        } catch (Exception e) {
            e.printStackTrace();
        }
        // set the option that makes elo possible
        sendCommand("setoption name UCI_LimitStrength value true");
        setElo(eloRating);
    }

    private void sendCommand(String cmd) { // send command to worker.
        worker = new engineWorker(processReader, processWriter);
        worker.sendCommand(cmd);
    }

    public void stopEngine() { // stop the engine
        try {
            processReader.close(); // close reader/writer
            processWriter.close();
        } catch (IOException ignored) {
        }
        worker = null;
    }

    public void setElo(int Elo) { // set elo of engine
        sendCommand("setoption name UCI_Elo value " + Elo);
    }

    // from stackoverflow
    private static URI getJarURI()
            throws URISyntaxException
    {
        final ProtectionDomain domain;
        final CodeSource       source;
        final URL              url;
        final URI              uri;

        domain = Main.class.getProtectionDomain();
        source = domain.getCodeSource();
        url    = source.getLocation();
        uri    = url.toURI();

        return (uri);
    }

    private static URI getFile(final URI    where,
                               final String fileName)
            throws ZipException,
            IOException
    {
        final File location;
        final URI  fileURI;

        location = new File(where);

        // not in a JAR, just return the path on disk
        if(location.isDirectory())
        {
            fileURI = URI.create(where.toString() + fileName);
        }
        else
        {
            final ZipFile zipFile;

            zipFile = new ZipFile(location);

            try
            {
                fileURI = extract(zipFile, fileName);
            }
            finally
            {
                zipFile.close();
            }
        }

        return (fileURI);
    }

    private static URI extract(final ZipFile zipFile,
                               final String  fileName)
            throws IOException
    {
        final File         tempFile;
        final ZipEntry     entry;
        final InputStream  zipStream;
        OutputStream       fileStream;

        tempFile = File.createTempFile(fileName, Long.toString(System.currentTimeMillis()));
        tempFile.deleteOnExit();
        entry    = zipFile.getEntry(fileName);

        if(entry == null)
        {
            throw new FileNotFoundException("cannot find file: " + fileName + " in archive: " + zipFile.getName());
        }

        zipStream  = zipFile.getInputStream(entry);
        fileStream = null;

        try
        {
            final byte[] buf;
            int          i;

            fileStream = new FileOutputStream(tempFile);
            buf        = new byte[1024];
            i          = 0;

            while((i = zipStream.read(buf)) != -1)
            {
                fileStream.write(buf, 0, i);
            }
        }
        finally
        {
            close(zipStream);
            close(fileStream);
        }

        return (tempFile.toURI());
    }

    private static void close(final Closeable stream)
    {
        if(stream != null)
        {
            try
            {
                stream.close();
            }
            catch(final IOException ex)
            {
                ex.printStackTrace();
            }
        }
    }
}
