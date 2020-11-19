package main;

import java.io.*;
import java.util.Properties;

public class Config {

    private final String DEFAULT_PATH = "/default.properties";
    private final String CONFIG_PATH = "config.properties";

    public Properties props;


    public Config() {
        loadDefaultProps();
        loadProps();
    }

    private void loadDefaultProps() {
        Properties defaultProps = new Properties();
        try {
            InputStream in = getClass().getResourceAsStream(DEFAULT_PATH);
            defaultProps.load(in);
            in.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        props = new Properties(defaultProps);
    }

    private void loadProps() {
        File f = new File(CONFIG_PATH);

        try {
            if (f.createNewFile()) {
                System.out.println("File created: " + f.getName());
            } else {
                System.out.println("Found " + CONFIG_PATH);
            }

            FileInputStream in = new FileInputStream(f);
            props.load(in);
            in.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void saveProps() {
        try {
            FileOutputStream out = new FileOutputStream(CONFIG_PATH);
            props.store(out, "Custom properties");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
