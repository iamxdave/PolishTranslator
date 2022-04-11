package zad1;

import java.io.*;
import java.net.InetSocketAddress;
import java.net.Socket;


public class LServerHandler implements Runnable {
    private final Socket socket;
    private final LangServer server;
    private final BufferedReader input;
    private final PrintWriter output;

    public LServerHandler(Socket socket, LangServer server) throws IOException {

        this.socket = socket;
        this.server = server;

        System.out.println("\nConnected: " + socket.getPort());

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

                    language = parts[1];
                    text = parts[2];

                    String[] translations = text.split(" ");

                    for (String trans : translations) {
                        String[] t = trans.split("=");
                        server.getTransMap().put(t[0], t[1]);
                    }

                    server.setLanguage(language);
                    System.out.println("ADDED: " + language + '=' + server.getTransMap());

                    System.out.println("SENDING: setack," + server.getServer().getLocalPort());

                    Socket mainServer = new Socket();
                    mainServer.connect(new InetSocketAddress("localhost", 6600));

                    PrintWriter mainServerOutput = new PrintWriter(new OutputStreamWriter(mainServer.getOutputStream()), true);

                    mainServerOutput.println("setack," + language + ',' + server.getServer().getLocalPort());

                    mainServer.close();
                    mainServerOutput.close();
                }

                case "get" -> {
                    text = parts[1];
                    language = parts[2];
                    port = parts[3];

                    text = text.replace(';', ',');

                    Socket client = new Socket();
                    client.connect(new InetSocketAddress("localhost", Integer.parseInt(port)));
                    PrintWriter output = new PrintWriter(new OutputStreamWriter(client.getOutputStream()), true);

                    if(!language.equals(server.getLanguage()))
                        output.println("err,Incorrect language");



                    boolean canTranslate = true;
                    StringBuilder sb = new StringBuilder();


                    for (String word : text.split(" ")) {
                        canTranslate = server.getTransMap().entrySet().stream().anyMatch(e -> e.getKey().equals(word));

                        if (canTranslate) {
                            sb.append(server.getTransMap().get(word)).append(" ");
                        } else {
                            System.out.println("SENDING: err,No such word in the database");
                            output.println("err,No such word in the database");
                            break;
                        }
                    }

                    if(canTranslate) {
                        System.out.println("SENDING: get," + sb);
                        output.println("get," + sb);
                    }


                    client.close();
                    output.close();
                }

                default -> output.println("err,unknown cmd");
            }

            socket.close();
            input.close();
            output.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
