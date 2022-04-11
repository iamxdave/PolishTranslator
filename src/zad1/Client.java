package zad1;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.*;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;

public class Client extends JFrame{

    JTextArea inputText = new JTextArea();
    JTextField inputLang = new JTextField();
    JTextArea outputText = new JTextArea();
    Button translateButton = new Button();
    Button changeDataButton = new Button();



    public Client() {
        setDisplay(400, 700, "Client");
        setGUI();
        pack();
    }

    public void setDisplay(int width, int height, String title) {
        setPreferredSize(new Dimension(width, height));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setTitle(title);

        setLocationRelativeTo(null);
        setResizable(false);
        setVisible(true);
    }


    public void setGUI() {
        JPanel mainPanel = new JPanel(new BorderLayout());

        setTextArea(inputText);
        setTextArea(outputText);
        outputText.setEditable(false);

        JPanel centerPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));

        centerPanel.add(new JLabel("Language:"));
        centerPanel.add(inputLang);
        centerPanel.add(translateButton);
        centerPanel.add(changeDataButton);

        inputLang.setPreferredSize(new Dimension(40,20));

        setButtons(translateButton, "TRANSLATE");
        setButtons(changeDataButton, "CHANGE DATA");

        translateButton.addActionListener(this::translate);
        changeDataButton.addActionListener(this::changeData);


        Panel panelNorth = new Panel(new BorderLayout(10,10));
        setTextPanels(panelNorth, inputText, "PUT YOUR TEXT HERE");
        
        Panel panelSouth = new Panel(new BorderLayout(10,10));
        setTextPanels(panelSouth, outputText, "RESULT IN EXPECTED LANGUAGE");



        mainPanel.add(panelNorth, BorderLayout.NORTH);
        mainPanel.add(centerPanel, BorderLayout.CENTER);
        mainPanel.add(panelSouth, BorderLayout.SOUTH);

        add(mainPanel);

    }

    public void setTextArea(JTextArea area) {
        area.setFont(new Font("Arial", Font.PLAIN, 20));
        area.setBorder(BorderFactory.createEmptyBorder(50, 50, 50, 50));
        area.setMinimumSize(new Dimension(300, 250));
        area.setLineWrap(true);
    }

    public void setButtons(Button button, String text) {
        button.setLabel(text);
        button.setFocusable(false);
        button.setPreferredSize(new Dimension(100, 70));
        button.setBackground(Color.GRAY);
        button.setForeground(Color.WHITE);
    }
    
    public void setTextPanels(Panel panel, JTextArea area, String title) {
        JScrollPane scrollPane = new JScrollPane (area, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setPreferredSize(new Dimension(250, 230));

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Arial", Font.PLAIN, 20));
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        titleLabel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        titleLabel.setBackground(Color.DARK_GRAY);
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setOpaque(true);

        panel.add(titleLabel, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);
    }

    public void translate(ActionEvent e) {
        Socket client = new Socket();


        try {
            ServerSocket responseServer = new ServerSocket(0);
            client.connect(new InetSocketAddress("localhost", 6600));
            PrintWriter output = new PrintWriter(new OutputStreamWriter(client.getOutputStream()), true);

            String text = inputText.getText()
                    .replace('\n', ' ')
                    .replace(',', ';');

            output.println("get," + text + ',' + inputLang.getText() + ',' + responseServer.getLocalPort());

            Socket respondingServer = responseServer.accept();


            BufferedReader input = new BufferedReader(new InputStreamReader(respondingServer.getInputStream()));

            String response = input.readLine();

            String[] parts = response.split(",");

            String cmd = parts[0];
            text = parts[1];

            switch (cmd) {
                case "get" -> {
                    outputText.setText(text);
                }

                case "err" -> {
                    JOptionPane.showMessageDialog(new JFrame(), text, "Error Message", JOptionPane.ERROR_MESSAGE);
                }
            }

        } catch (IOException ex) {
            ex.printStackTrace();
        }

        repaint();
    }



    public void changeData(ActionEvent e) {
        inputText.setText("");
        inputLang.setText("");
        outputText.setText("");

        repaint();
    }
}
