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

        System.out.println("Connected: " + socket);

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
            String port;


            switch (cmd) {
                case "set" -> {
                    port = parts[1];
                    if (!server.getLangTransMap().isEmpty()) {
                        Map.Entry<String, Map<String, String>> entry = server.getLangTransMap().entrySet().iterator().next();

                        StringBuilder sb = new StringBuilder();

                        sb.append(entry.getKey()).append(",");

                        for(Map.Entry<String, String> trans : entry.getValue().entrySet()) {
                            sb.append(trans.getKey()).append("=").append(trans.getValue()).append(" ");
                        }

                        Socket langServer = new Socket();
                        langServer.connect(new InetSocketAddress("localhost", Integer.parseInt(port)));
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
                    port = parts[2];

                    server.getLangServerMap().put(language, new Socket("localhost", Integer.parseInt(port)));

                    server.getLangTransMap().remove(language);
                }

                case "get" -> {
                    text = parts[1];
                    language = parts[2];
                    port = parts[3];

                    boolean canTranslate = server.getLangServerMap().entrySet().stream().anyMatch(e -> e.getKey().equals(language));

                    if (canTranslate) {
                        Socket langServer = server.getLangServerMap().get(language);
                        PrintWriter output = new PrintWriter(new OutputStreamWriter(langServer.getOutputStream()), true);

                        System.out.println("SENDING: get," + text + ',' + language + ',' + port);
                        output.println("get," + text + ',' + language + ',' + port);

                        langServer.close();
                        output.close();
                    } else {
                        Socket client = new Socket("localhost", Integer.parseInt(port));
                        PrintWriter output = new PrintWriter(new OutputStreamWriter(client.getOutputStream()), true);

                        System.out.println("SENDING: err,no such language in the database");
                        output.println("err,no such language in the database");

                        client.close();
                        output.close();
                    }
                }

                default -> {
                    output.println("err,unknown cmd");
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

