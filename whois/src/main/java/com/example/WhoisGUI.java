package com.example;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.EventQueue;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.HeadlessException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.UnknownHostException;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingWorker;
import javax.swing.WindowConstants;

public class WhoisGUI extends JFrame {
    private JTextField searchString = new JTextField(30);
    private JTextArea names = new JTextArea(15, 80);
    private JButton findButton = new JButton("Find");
    private ButtonGroup searchIn = new ButtonGroup();
    private ButtonGroup searchFor = new ButtonGroup();
    private JCheckBox exactMatch = new JCheckBox("Exact Match", true);
    private JTextField chosenServer = new JTextField();
    private Whois server;

    public WhoisGUI(Whois server) throws HeadlessException {
        super("Whois");
        this.server = server;
        Container pane = getContentPane();

        Font f = new Font("Monospaced", Font.PLAIN, 12);
        names.setFont(f);
        names.setEditable(false);

        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new GridLayout(1, 1, 10, 10));
        JScrollPane jsp = new JScrollPane(names);
        centerPanel.add(jsp);
        pane.add("Center", centerPanel);

        //不希望南边和北边的按钮占据整个区域
        //所以在那里添加Panel，
        //并在Panel中使用FlowLayout
        JPanel northPanel = new JPanel();
        JPanel northPanelTop = new JPanel();
        northPanelTop.setLayout(new FlowLayout(FlowLayout.LEFT));
        northPanelTop.add(new JLabel("Whois: "));
        northPanelTop.add("North", searchString);
        northPanelTop.add(exactMatch);
        northPanelTop.add(findButton);
        northPanel.setLayout(new BorderLayout(2, 1));
        northPanel.add("North", northPanelTop);
        JPanel northPanelBottom = new JPanel();
        northPanelBottom.setLayout(new GridLayout(1, 3, 5, 5));
        northPanelBottom.add(initRecordType());
        northPanelBottom.add(initSearchFields());
        northPanelBottom.add(initServerChoice());
        northPanel.add("Center", northPanelBottom);

        pane.add("North", northPanel);

        ActionListener al = new LookupNames();
        findButton.addActionListener(al);
        searchString.addActionListener(al);
    }

    private JPanel initRecordType() {
        JPanel p = new JPanel();
        p.setLayout(new GridLayout(6, 2, 5, 2));
        p.add(new JLabel("Search for:"));
        p.add(new JLabel(""));

        JRadioButton any = new JRadioButton("Any", true);
        any.setActionCommand("Any");
        searchFor.add(any);
        p.add(any);

        p.add(this.makeSearchForRadioButton("Network"));
        p.add(this.makeSearchForRadioButton("Person"));
        p.add(this.makeSearchForRadioButton("Host"));
        p.add(this.makeSearchForRadioButton("Domain"));
        p.add(this.makeSearchForRadioButton("Organization"));
        p.add(this.makeSearchForRadioButton("Group"));
        p.add(this.makeSearchForRadioButton("Gateway"));
        p.add(this.makeSearchForRadioButton("ASN"));

        return p;
    }

    private JRadioButton makeSearchForRadioButton(String label) {
        JRadioButton button = new JRadioButton(label, false);
        button.setActionCommand(label);
        searchFor.add(button);
        return button;
    }

    private JPanel initSearchFields() {
        JPanel p = new JPanel();
        p.setLayout(new GridLayout(6, 1, 5, 2));
        p.add(new JLabel("Search In: "));

        JRadioButton all = new JRadioButton("All", true);
        all.setActionCommand("All");
        searchIn.add(all);
        p.add(all);

        p.add(this.makeSearchInRadioButton("Name"));
        p.add(this.makeSearchInRadioButton("Mailbox"));
        p.add(this.makeSearchInRadioButton("Handle"));

        return p;
    }

    private JRadioButton makeSearchInRadioButton(String label) {
        JRadioButton button = new JRadioButton(label, false);
        button.setActionCommand(label);
        searchIn.add(button);
        return button;
    }

    private JPanel initServerChoice() {
        final JPanel p = new JPanel();
        p.setLayout(new GridLayout(6, 1, 5, 2));
        p.add(new JLabel("Search At: "));
        chosenServer.setText(server.getHost().getHostName());
        p.add(chosenServer);

        chosenServer.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    server = new Whois(chosenServer.getText());
                } catch (UnknownHostException e1) {
                    JOptionPane.showMessageDialog(p,
                            e1.getMessage(),
                            "Alter",
                            JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        return p;
    }

    private static class FrameShower implements Runnable {
        private Frame frame;
        public FrameShower(WhoisGUI frame) {
            this.frame = frame;
        }

        @Override
        public void run() {
            frame.setVisible(true);
        }
    }

    private class LookupNames implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            names.setText("");
            SwingWorker<String, Object> worker = new Lookup();
            worker.execute();
        }
    }

    private class Lookup extends SwingWorker<String, Object> {
        @Override
        protected String doInBackground() throws Exception {
            Whois.SearchIn group = Whois.SearchIn.ALL;
            Whois.SearchFor category = Whois.SearchFor.ANY;

            String searchForLabel = searchFor.getSelection().getActionCommand();
            String searchInLabel = searchIn.getSelection().getActionCommand();

            if (searchInLabel.equals(Whois.SearchIn.NAME.label)) {
                group = Whois.SearchIn.NAME;
            } else if (searchInLabel.equals(Whois.SearchIn.MAILBOX.label)) {
                group = Whois.SearchIn.MAILBOX;
            } else if (searchInLabel.equals(Whois.SearchIn.HANDLE.label)) {
                group = Whois.SearchIn.HANDLE;
            }
            if (searchFor.equals(Whois.SearchFor.NETWORK.label)) {
                category = Whois.SearchFor.NETWORK;
            } else if (searchForLabel.equals(Whois.SearchFor.PERSON.label)) {
                category = Whois.SearchFor.PERSON;
            } else if (searchForLabel.equals(Whois.SearchFor.HOST.label)) {
                category = Whois.SearchFor.HOST;
            } else if (searchForLabel.equals(Whois.SearchFor.DOMAIN.label)) {
                category = Whois.SearchFor.DOMAIN;
            } else if (searchForLabel.equals(Whois.SearchFor.ORGANIZATION.label)) {
                category = Whois.SearchFor.ORGANIZATION;
            } else if (searchForLabel.equals(Whois.SearchFor.GROUP.label)) {
                category = Whois.SearchFor.GROUP;
            } else if (searchForLabel.equals(Whois.SearchFor.GATEWAY.label)) {
                category = Whois.SearchFor.GATEWAY;
            } else if (searchForLabel.equals(Whois.SearchFor.ASN.label)) {
                category = Whois.SearchFor.ASN;
            }

            server.setHost(chosenServer.getText());

            return server.lookUpNames(searchString.getText(),
                    category,
                    group,
                    exactMatch.isSelected());
        }

        @Override
        protected void done() {
            try {
                names.setText(get());
            } catch (Exception e) {
                JOptionPane.showMessageDialog(WhoisGUI.this,
                        e.getMessage(),
                        "Lookup Failed",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    public static void main(String[] args) {
        try {
            Whois server = new Whois();
            WhoisGUI a = new WhoisGUI(server);
            a.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
            a.pack();
            EventQueue.invokeLater(new FrameShower(a));
        } catch (UnknownHostException e) {
            JOptionPane.showMessageDialog(null,
                    "Could not locate default host " + Whois.DEFAULT_HOST,
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }
}
