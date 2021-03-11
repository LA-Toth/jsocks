package me.laszloattilatoth.jsocks;

import me.laszloattilatoth.jsocks.util.Logging;
import org.apache.commons.cli.*;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Main {
    public static void main(String[] args) {
        Logger logger = Logger.getGlobal();
        System.setProperty("java.util.logging.SimpleFormatter.format", Logging.LOG_FORMAT);
        Logger.getLogger("").setLevel(Level.FINEST);
        Logger.getLogger("").getHandlers()[0].setLevel(Level.FINEST);

        Options options = new Options();
        String host = "127.0.0.1";
        int port = 1080;

        options.addOption("b", "bind", true, "Bind address (IPv4)");
        options.addOption("p", "port", true, "Port number");
        options.addOption(Option.builder("h").longOpt("help").desc("Print help").build());

        CommandLineParser parser = new DefaultParser();
        CommandLine cmd = null;
        try {
            cmd = parser.parse(options, args);
        } catch (ParseException e) {
            logger.severe("Unable to process arguments; error='" + e.getMessage() + "'");
            System.exit(1);
        }

        if (cmd.hasOption('h') || cmd.hasOption('f')) {
            String header = "A SOCKS proxy";
            String footer = "\nPoC";

            HelpFormatter formatter = new HelpFormatter();
            formatter.printHelp("jsocks", header, options, footer, true);
            System.exit(0);
        }

        if (cmd.hasOption('b'))
            host = cmd.getOptionValue('b');

        if (cmd.hasOption('p')) {
            try {
                port = Integer.parseInt(cmd.getOptionValue('p'));
                if (port < 1 || port > 65535) {
                    logger.severe("The specified port is not in 1..65535 range");
                    System.exit(1);
                }
            } catch (NumberFormatException e) {
                logger.severe("Specified port number is not an integer");
                System.exit(1);
            }
        }

        InetAddress address = null;
        try {
            address = InetAddress.getByName(host);
        } catch (UnknownHostException e) {
            Logging.logException(logger, e, Level.SEVERE, false);
            System.exit(1);
        }

        try {
            JSocks ssh = new JSocks(address, port);

            System.exit(ssh.run());
        } catch (Throwable e) {
            Logging.logThrowableWithBacktrace(Logger.getGlobal(), e, Level.INFO);
        }
    }
}
