package zad1;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

public class Client extends JFrame{

    JTextArea inputText = new JTextArea();
    JTextField inputLang = new JTextField();
    JTextArea outputText = new JTextArea();
    Button translateButton = new Button();
    Button changeDataButton = new Button();



    public void setDisplay(int width, int height, String title) {
        setPreferredSize(new Dimension(width, height));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setTitle(title);

        pack();

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

        pack();
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
        ClientHandler ch = new ClientHandler();
        ch.setText(inputText, inputLang, outputText);
        repaint();
    }



    public void changeData(ActionEvent e) {
        inputText.setText("");
        inputLang.setText("");
        outputText.setText("");

        repaint();
    }
}
