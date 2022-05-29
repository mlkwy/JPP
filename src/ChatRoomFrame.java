import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.net.Socket;
import java.util.ArrayList;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.border.Border;

public class ChatRoomFrame extends JFrame{
    private static final long serialVersionUID = 1L;

    Socket socket;
    String name;
    Boolean is_leader;
    static ArrayList<String> client_list;

    SendToServer write_thread;

    JPanel panel0;
    JPanel panel1;
    JPanel panel2;
    Container c;
    JLabel header;
    JTextArea message_area;
    JScrollPane scroll;
    JTextArea textfield;
    JButton send_button;
    JButton mute_button;

    ChatRoomFrame(){
        this.socket=Client.socket;
        this.name=Client.name;
        this.is_leader=Client.is_leader;
        ChatRoomFrame.client_list=Client.client_list;


        setTitle("Chat Room");
        setResizable(false);
        setLayout(null);



        panel0 = new JPanel();
        panel0.setLayout(null);
        panel0.setPreferredSize(new Dimension(400, 50));


        header = new JLabel(this.name);
        header.setFont(new Font("Arial", Font.BOLD, 20));
        header.setBounds(10, 0, 400, 50);



        panel1 = new JPanel();
        panel1.setLayout(null);
        panel1.setPreferredSize(new Dimension(400, 500));



        message_area = new JTextArea();
        message_area.setEditable(false);
        message_area.setBounds(10, 0, 380, 490);
        scroll = new JScrollPane(message_area, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scroll.setPreferredSize(new Dimension(390, 490));
        scroll.setBounds(10, 0, 390, 490);


        panel2 = new JPanel();
        panel2.setLayout(null);
        panel2.setPreferredSize(new Dimension(400, 150));

        send_button = new JButton("Send");
        send_button.setOpaque(true);;

        mute_button = new JButton("M/U");
        mute_button.setOpaque(true);


        textfield = new JTextArea();
        textfield.setBounds(10, 0, 330, 50);
        send_button.setBounds(345, 0, 50, 50);
        mute_button.setBounds(345, 0, 50, 50);


        send_button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String message = textfield.getText();
                if(!message.equals(""))
                {
                    message = "Message`" + name + "`" + message;
                    new SendToServer(socket, message).start();
                    textfield.setText("");
                }
                textfield.requestFocusInWindow();
            }
        });

        mute_button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String message = textfield.getText();

                if(!message.equals("") && !message.equals(name) && Client.client_list.contains(message.trim()))
                {
                    System.out.println("mute");
                    message_area.append("You muted / unmuted " + message + ".\n");
                    message = "Muted`" + message;
                    new SendToServer(socket, message).start();
                    textfield.setText("");
                }
                textfield.requestFocusInWindow();
            }
        });


        panel0.add(header);
        //panel1.add(message_area);
        panel1.add(scroll);
        panel2.add(textfield);
        panel2.add(send_button);
        if(is_leader)
            panel2.add(mute_button);

        Border border = BorderFactory.createLineBorder(Color.GRAY);
        message_area.setBorder(BorderFactory.createCompoundBorder(border, BorderFactory.createEmptyBorder(0, 0, 0, 0)));
        textfield.setBorder(BorderFactory.createCompoundBorder(border, BorderFactory.createEmptyBorder(0, 0, 0, 0)));

        panel0.setBounds(0, 0, 400, 50);
        panel1.setBounds(0, 50, 400, 500);
        panel2.setBounds(0, 550, 400, 50);

        add(panel0);
        add(panel1);
        add(panel2);

        pack();
        setVisible(true);
        setSize(new Dimension(425, 640));
        this.addWindowListener(new WindowAdapter(){
            public void windowClosing(WindowEvent e) {
                System.exit(0);
            }
        });
        textfield.requestFocusInWindow();
    }

    public void alterLayout()
    {
        if(is_leader){
            panel2.remove(mute_button);
            textfield.setBounds(10, 0, 330, 50);
            send_button.setBounds(345, 0, 50, 50);
            textfield.requestFocusInWindow();

            Border border = BorderFactory.createLineBorder(Color.GRAY);
            message_area.setBorder(BorderFactory.createCompoundBorder(border, BorderFactory.createEmptyBorder(0, 0, 0, 0)));
            textfield.setBorder(BorderFactory.createCompoundBorder(border, BorderFactory.createEmptyBorder(0, 0, 0, 0)));

            header.setText(name);
            header.setForeground(Color.BLACK);
        }

        else {
            panel2.add(textfield);
            textfield.setBounds(10, 0, 275, 50);
            send_button.setBounds(292, 0, 50, 50);
            panel2.add(mute_button);
            textfield.requestFocusInWindow();

            header.setText(name+" (Leader)");
            header.setForeground(Color.BLUE);

            Border border = BorderFactory.createLineBorder(Color.BLUE);
            message_area.setBorder(BorderFactory.createCompoundBorder(border, BorderFactory.createEmptyBorder(0, 0, 0, 0)));
            textfield.setBorder(BorderFactory.createCompoundBorder(border, BorderFactory.createEmptyBorder(0, 0, 0, 0)));
        }

        is_leader = !is_leader;
        panel2.validate();
    }
}


