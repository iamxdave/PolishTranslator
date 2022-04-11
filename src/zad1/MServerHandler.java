package zad1;

import java.io.*;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.Map;

public class MServerHandler implements Runnable {

    private Socket socket;
    private MainServer server;
    private BufferedReader input;
    private PrintWriter output;

    public MServerHandler(Socket socket, MainServer server) throws IOException {

        this.socket = socket;
        this.server = server;

        System.out.println("Connected: " + socket.getPort());

        input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        output = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()), true);
    }

    @Override
    public void run() {

        try {
            String response = input.readLine();

            System.out.println("GOT MSG: " + response);

            String[] parts = response.split(",");

            String cmd = parts[0];
            String text;
            String language;
            String adress;


            switch (cmd) {
                case "set" -> {
                    adress = parts[1];
                    if (!server.getLangTransMap().isEmpty()) {
                        Map.Entry<String, Map<String, String>> entry = server.getLangTransMap().entrySet().iterator().next();

                        StringBuilder sb = new StringBuilder();

                        sb.append(entry.getKey()).append(",");

                        for(Map.Entry<String, String> trans : entry.getValue().entrySet()) {
                            sb.append(trans.getKey()).append("=").append(trans.getValue()).append(" ");
                        }

                        Socket langServer = new Socket();
                        langServer.connect(new InetSocketAddress("localhost", Integer.parseInt(adress)));
                        PrintWriter langServerOutput = new PrintWriter(new OutputStreamWriter(langServer.getOutputStream()), true);

                        System.out.println("SENDING: set," + sb);
                        langServerOutput.println("set," + sb);

                        langServer.close();
                        langServerOutput.close();

                    } else {
                        System.out.println("SENDING: err, no available languages to set");
                        output.println("err,no available languages to set");
                    }
                }

                case "setack" -> {
                    language = parts[1];
                    adress = parts[2];

                    server.getLangServerMap().put(language, Integer.parseInt(adress));

                    server.getLangTransMap().remove(language);
                }

                case "get" -> {
                    text = parts[1];
                    language = parts[2];
                    adress = parts[3];

                    boolean canTranslate = server.getLangServerMap().entrySet().stream().anyMatch(e -> e.getKey().equals(language));

                    if (canTranslate) {
                        Socket langServer = new Socket("localhost", server.getLangServerMap().get(language));
                        PrintWriter output = new PrintWriter(new OutputStreamWriter(langServer.getOutputStream()), true);

                        System.out.println("SENDING: get," + text + ',' + language + ',' + adress);
                        output.println("get," + text + ',' + language + ',' + adress);

                        langServer.close();
                        output.close();
                    } else {
                        Socket client = new Socket("localhost", Integer.parseInt(adress));
                        PrintWriter output = new PrintWriter(new OutputStreamWriter(client.getOutputStream()), true);

                        System.out.println("SENDING: err,No such language in the database");
                        output.println("err,No such language in the database");

                        client.close();
                        output.close();
                    }
                }

                default -> {
                    output.println("err,Unknown cmd");
                }
            }

            socket.close();
            input.close();
            output.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

