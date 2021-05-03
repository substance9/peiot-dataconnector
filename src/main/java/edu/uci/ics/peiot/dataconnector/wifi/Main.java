package edu.uci.ics.peiot.dataconnector.wifi;

import edu.uci.ics.peiot.dataconnector.wifi.model.RawConnectionEvent;
import net.sourceforge.argparse4j.ArgumentParsers;
import net.sourceforge.argparse4j.inf.ArgumentParser;
import net.sourceforge.argparse4j.inf.ArgumentParserException;
import net.sourceforge.argparse4j.inf.Namespace;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Properties;



public class Main {
    private static final Logger logger = LoggerFactory.getLogger(Main.class);

    public static void main( String[] args ){

        System.out.println( "PE-IoT Data Connector Initiating:" );

        Properties prop = readConfig(args);

        //Instantiate sender
        RawEventSender sender = new RawEventSender(Integer.parseInt(prop.getProperty("port")));
        sender.init();

        if(prop.getProperty("mode").equals("prod")){
            StreamReceiver streamReceiver = new StreamReceiver(prop.getProperty("prod_in_pipe_path"));
            streamReceiver.connect();
            UCIWifiParser parser = new UCIWifiParser();
            RawConnectionEvent rawEvt;
            while(true){
                String evtStr = streamReceiver.readLine();
                if(evtStr.length() == 0){
                    continue;
                }
                if (evtStr == null) {
                    logger.error("Empty Event String, skip");
                    continue;
                }
                try{
                    rawEvt = parser.parse(evtStr);
                }catch(Exception e){
                    e.printStackTrace();
                    logger.error("Unable to parse the event string: " + evtStr + " , skip");
                    continue;
                }

                if (rawEvt == null){
                    logger.error("Unable to parse the event string: " + evtStr + " , skip");
                    continue;
                }
                sender.send(rawEvt);
            }
        }
        else if (prop.getProperty("mode").equals("exp")){
            HistoricalDataLoader dataLoader = new HistoricalDataLoader(prop.getProperty("exp_data_directory"),
                                                                        Integer.parseInt(prop.getProperty("numofdata")));
            ArrayList<RawConnectionEvent> hisWifiDataArray = dataLoader.loadDataToArray();

            DataSendingRateController sendController = new DataSendingRateController(hisWifiDataArray,
                                                                                    sender,
                                                                                    prop.getProperty("workload"),
                                                                                    Integer.parseInt(prop.getProperty("rate")));

            sendController.startSending();
        }

        // //Start the replayer to read and send data
//        TxProcessor txProcessor = new TxProcessor(tQueue,
//                                            Integer.parseInt(prop.getProperty("mpl")),
//                                            Integer.parseInt(prop.getProperty("coordinator_db_port")),
//                                            Integer.parseInt(prop.getProperty("num_agents")),
//                                            Integer.parseInt(prop.getProperty("agent_ports_starts")),
//                                            prop.getProperty("result.output_dir"));
//
//        txProcessor.connectToAgents();
//        Thread txSenderThread = new Thread(txProcessor);
//
//        txSenderThread.start();


        // //reaping all threads, ending the experiment


        // ** Code for getting the execution time
        //long expEndTime = System.currentTimeMillis();
        //System.out.println("Experiment took " + String.valueOf(expEndTime-expStartTime) + "ms to finish");
    }

    private static Properties readConfig( String[] args ){
        // - First, read from the property file in the resource directory. Load the values into Properties Class
        // - Second, read from the command line arguments and overwrite corresponding values
        Properties prop = null;
        try (InputStream input = Main.class.getClassLoader().getResourceAsStream("data-source.properties")) {

            prop = new Properties();

            // load a properties file
            prop.load(input);

        } catch (IOException ex) {
            ex.printStackTrace();
        }

        ArgumentParser parser = ArgumentParsers.newFor("Main").build().defaultHelp(true)
                .description("PE-IoT WiFi Data Connector");

        //Example argument statements.
        parser.addArgument("-m", "--mode").choices("prod","exp")
				.setDefault("exp").help("Work mode, production: streaming data, exp:historical data");
        parser.addArgument("-p", "--port")
                .setDefault("7735").help("Port number for the downstream program to connect and get processed data");
        parser.addArgument("-w", "--workload").choices("static","variant")
                .setDefault("static").help("ONLY for exp mode, the ingestion workload pattern");
        parser.addArgument("-r", "--rate")
                .setDefault("150").help("ONLY for exp mode, the ingestion rate of workload, in events/second");
        parser.addArgument("-n", "--numofdata")
                .setDefault("100000").help("ONLY for exp mode, the maximum amount of data to be ingested");

        Namespace ns = null;

        try {
            ns = parser.parseArgs(args);
        } catch (ArgumentParserException e) {
            parser.handleError(e);
            System.exit(1);
        }

        if (args.length >= 2){
            //read config from command line args
            prop.setProperty("mode", ns.get("mode"));
            prop.setProperty("port", ns.get("port"));
            prop.setProperty("workload", ns.get("workload"));
            prop.setProperty("rate", ns.get("rate"));
            prop.setProperty("numofdata", ns.get("numofdata"));
        }

        //if (ns.get("mode").equals("exp")) {
            // ** Result output code
//        String resultDir = prop.getProperty("result.output_path")
//                                                                +"errID_"+prop.getProperty("simulated_error_id")
//                                                                +"|transID_"+prop.getProperty("error_transaction_id")
//                                                                +"|expID_"+prop.getProperty("experiment_id");
//        prop.setProperty("result.output_dir", resultDir);
//        System.out.println("--result.output_dir:\t"+resultDir);

//        System.out.println( "Experiment Parameters:" );
            // get the property value and print it out
//            System.out.println("--replayer.inputs_directory:\t"+prop.getProperty("replayer.inputs_directory"));
//            System.out.println("--replayer.concurrency:\t\t"+prop.getProperty("replayer.concurrency"));
//            System.out.println("--replayer.experiment_duration:\t"+prop.getProperty("replayer.experiment_duration"));
//            System.out.println("--simulator.policy:\t\t"+prop.getProperty("simulator.policy"));
        //}

        return prop;
    }
}