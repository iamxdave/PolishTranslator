package zad1;

import javax.swing.*;
import java.io.*;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;

public class ClientHandler {


    public void setText(JTextArea inputText, JTextField inputLang, JTextArea outputText) {
        Socket client = new Socket();

        if (!outputText.getText().isEmpty()) {
            JOptionPane.showMessageDialog(new JFrame(), "Change data to translate again", "Error Message", JOptionPane.INFORMATION_MESSAGE);
        } else if (inputText.getText().isEmpty()) {
            JOptionPane.showMessageDialog(new JFrame(), "Enter text to translate", "Error Message", JOptionPane.INFORMATION_MESSAGE);
        } else if (inputLang.getText().isEmpty()) {
            JOptionPane.showMessageDialog(new JFrame(), "Enter language to be translated into", "Error Message", JOptionPane.INFORMATION_MESSAGE);
        } else {

            try {
                ServerSocket responseServer = new ServerSocket(0);
                client.connect(new InetSocketAddress("localhost", 6600));
                PrintWriter output = new PrintWriter(new OutputStreamWriter(client.getOutputStream()), true);
                BufferedReader input = new BufferedReader(new InputStreamReader(client.getInputStream()));

                String text = inputText.getText()
                        .replace('\n', ' ')
                        .replace(',', ';');

                output.println("get," + text + ',' + inputLang.getText() + ',' + responseServer.getLocalPort());


                String response = input.readLine();
                String[] parts = response.split(",");

                String cmd = parts[0];
                text = parts[1];

                switch (cmd) {
                    case "getack" -> {
                        Socket respondingServer = responseServer.accept();
                        BufferedReader inputServer = new BufferedReader(new InputStreamReader(respondingServer.getInputStream()));

                        boolean empty = false;
                        String responseFromServer = inputServer.readLine();

                        parts = responseFromServer.split(",");
                        cmd = parts[0];
                        text = parts[1];

                        switch (cmd) {
                            case "get" -> outputText.setText(text);

                            case "err" -> JOptionPane.showMessageDialog(new JFrame(), text, "Error Message From LangServer", JOptionPane.ERROR_MESSAGE);
                        }
                    }

                    case "err" -> JOptionPane.showMessageDialog(new JFrame(), text, "Error Message From MainServer", JOptionPane.ERROR_MESSAGE);
                }



            } catch (IOException ex) {
                ex.printStackTrace();
            }

        }
    }
}
