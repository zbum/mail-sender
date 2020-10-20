package kr.co.manty;

import org.apache.commons.cli.*;

import javax.mail.Authenticator;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;
import java.util.logging.ConsoleHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;


public class MailSender {
    public static void main(String[] args) throws Exception{
        Options options = new Options();
        Option fileOption = new Option("f", "file", true, "eml file path");
        fileOption.setRequired(true);
        options.addOption(fileOption);

        Option senderOption = new Option("u", "user", true, "user email address");
        senderOption.setRequired(true);
        options.addOption(senderOption);

        Option passwordOption = new Option("p", "password", true, "sender password");
        passwordOption.setRequired(true);
        options.addOption(passwordOption);

        Option receiverOption = new Option("r", "receiver", true, "receiver email address");
        receiverOption.setRequired(true);
        options.addOption(receiverOption);

        Option hostOption = new Option("h", "host", true, "smtp host");
        hostOption.setRequired(true);
        options.addOption(hostOption);

        CommandLineParser parser = new DefaultParser();
        HelpFormatter helpFormatter = new HelpFormatter();
        CommandLine cmd;

        try {
            cmd = parser.parse(options, args);
        }catch (ParseException pe) {
            System.out.println(pe.getMessage());
            helpFormatter.printHelp("MailSender", options);

            System.exit(1);
            return;
        }

        Path emlPath = Paths.get(cmd.getOptionValue("f"));

        Logger logger = Logger.getLogger("com.sun.mail.smtp");
        logger.setLevel(Level.INFO);
        ConsoleHandler handler = new ConsoleHandler();
        handler.setFormatter(new SimpleFormatter());
        handler.setLevel(Level.INFO);
        logger.addHandler(handler);


        Properties properties = new Properties();
        properties.put("mail.smtp.auth", "true");
        properties.setProperty("mail.smtp.host", cmd.getOptionValue("h"));
        properties.put("mail.smtp.socketFactory.port", 465);
        properties.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
        properties.put("mail.smtp.socketFactory.fallback", "false");

        Session session = Session.getDefaultInstance(properties, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(cmd.getOptionValue("u"), cmd.getOptionValue("p"));
            }
        });

        MimeMessage message = new MimeMessage(session, Files.newInputStream(emlPath));

        Transport.send(message, InternetAddress.parse(cmd.getOptionValue("r")));

    }
}
