package edu.uci.ics.peiot.dataconnector.wifi;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.concurrent.TimeUnit;

public class StreamReceiver {
    private String inPipeFileName;

    private static InputStream pipeIn;
    private static InputStreamReader streamReader;
    private static BufferedReader bufferReader;


    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    public StreamReceiver(String inPipeFileName){

    }

    private void createPipeReader(String pipeNameStr) throws Exception{
        File pipeInFile = new File(pipeNameStr);
        pipeIn = new FileInputStream(pipeInFile);
        streamReader = new InputStreamReader(pipeIn);
        bufferReader = new BufferedReader(streamReader);
    }

    public void connect(){
        String pipeName = inPipeFileName;
        boolean connected = false;
        do {
            try {
                TimeUnit.SECONDS.sleep(1);
                createPipeReader(pipeName);
                connected = true;
            } catch (Exception e) {
                logger.error("Cannot Open Data Source Pipe File");
            }
        } while (!connected);
    }

    public String readLine(){
        String line = null;
        try {
            line = bufferReader.readLine();
            //logger.debug("Data Receiver Get Data: " + line);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return line;
    }

}