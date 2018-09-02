package chat_client;
import java.util.*;
import java.net.*;
import java.io.*;
import java.text.SimpleDateFormat;
import javax.swing.JFileChooser;

public class Chat_client extends javax.swing.JFrame 
{
    //Declarations
    String user_name, server_address = "localhost"; //Host IP 
    int port_number = 4009;
    Boolean Connection_status = false; //To check if the current thread is connected
    public static Date date = new Date();
    public static SimpleDateFormat format = new SimpleDateFormat ("MM/dd/yy, h:mm a, ");
    public File file;
    Thread incoming_reader; 
    ArrayList<String> users_list = new ArrayList();
    Socket socket;
    BufferedReader read_buffer;
    PrintWriter write_buffer;
    
    
    //The function which invokes the thread start
    public void thread_listen() 
    {
        if(incoming_reader == null)
        {
            incoming_reader = new Thread(new incoming_reader());
            incoming_reader.start();
        }
    }
    
    //The function to add the connected user to the user lisr
    public void add_user(String data) 
    {
        users_list.add(data);
    }
    
    //The function print the connected users on screen
    public void users_write() 
    {
        String[] list_temp = new String[(users_list.size())];
        users_list.toArray(list_temp);
        for (String token:list_temp) 
        {
            text_screen.append("                                                                                 "+token + "\n");
        }
    }
    
    //Function to send server that the client is disconnecting.
    public void disconnect_send() 
    {
        String exit1 = (user_name + ": :Disconnect");
        try
        {
            write_buffer.println(exit1); 
            write_buffer.flush(); 
        } 
        catch (Exception ex) 
        {
            text_screen.append(format.format(new Date())+"Not Disconnected. Try again.\n");
        }
    }
    
    //Function to print on a client's screnn that it has been disconnected
    public void Disconnected() 
    {
        try 
        {
            text_screen.append(format.format(new Date())+"Disconnected.\n");
            socket.close();
        }   catch(Exception ex) 
        {
            text_screen.append(format.format(new Date())+"Failed to disconnect. \n");
        }
        Connection_status = false;
        username_box.setEditable(true);
    }
    
    //Initilaise the GUi
    public Chat_client() 
    {
        initComponents();      
    }
    
    // Function Remove a user if disconnected
    public void userRemove(String data) 
    {
         text_screen.append(data + " is now offline.\n");
    }
    //Thread run
    public class incoming_reader implements Runnable
    {
        @Override
        public void run() 
        {
            while(incoming_reader !=null)
            {
                String[] split_data;
                String stream, connected_status = "Sending Connected Users", connect = "Connect", disconnect = "disconnected", chat = "Chat";
                String red = "reduntant", file = "File", stop = "Stop";
                try
                {
                    while ((stream = read_buffer.readLine()) !=null) 
                    {
                        text_screen.setCaretPosition(text_screen.getDocument().getLength());
                        split_data = stream.split(":");//Splits incomimg data at occurances of ":"
                        if(split_data[0].equals(red))//Checks if server told the name entered is already in use
                        {
                            text_screen.append(format.format(new Date())+" Illegal User  Entry. Disconnect and Reconnect. User :"+ user_name +" : Please exit and connect again \n");
                        }
                        else
                        {
                            if (split_data[2].equals(chat)) // Checks if incoming data is a chat. If yes, displays
                            {
                                text_screen.append(format.format(new Date())+split_data[0] + ": " + split_data[1] + "\n");
                                text_screen.setCaretPosition(text_screen.getDocument().getLength());
                            } 
                            else if (split_data[2].equals(connect))//Check if incoming data is about anew connection occured in the chat room. If yes, add the new user to user list
                            {
                                add_user(split_data[0]);
                            }   
                            else if (split_data[2].equals(connected_status)) // If connected, displays all the connected users.
                            {
                                text_screen.append(format.format(new Date())+"Currently Available Users : \n");
                                users_write();
                                users_list.clear();
                            }
                            else if (split_data[2].equals(disconnect)) // check if incomimng data is about a user disconnecting. If yes, remove the user from the list
                            {
                                userRemove(split_data[0]);
                            } 
                             else if (split_data[2].equals(stop)) //Check if server is stopping. If yes, exits.
                            {
                                Thread.sleep(600);
                                Disconnected(); 
                                System.exit(0); 
                            } 
                            else if (split_data[2].equals(file))//Check if incoming data is a file transfer. If yes, displays the file.
                            {
                                text_screen.append(format.format(new Date())+"Received file from : "+split_data[0]+"\n");
                                text_screen.setCaretPosition(text_screen.getDocument().getLength());
                                text_screen.append(format.format(new Date())+"Displaying Content from file sent by  : "+split_data[0]+"\n");
                                while ((stream = read_buffer.readLine()) !=null)
                                {
                                    text_screen.append(stream + "\n");//Displays the file
                                }
                            }
                        }
                    }
                }
                catch(Exception ex) 
                { }
            }
        }
    }
    
    //Function to filter text files
    class MyCustomFilter extends javax.swing.filechooser.FileFilter 
    {
        @Override
        public boolean accept(File file) 
        {
            // Allow only directories, or files with ".txt" extension
            return file.isDirectory() || file.getAbsolutePath().endsWith(".txt");
        }
        @Override
        public String getDescription() 
        {
            // This description will be displayed in the dialog
            return "Text documents (*.txt)";
        }
    } 
  
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        fileChooser = new javax.swing.JFileChooser();
        title = new javax.swing.JLabel();
        username_box = new javax.swing.JTextField();
        connect_button = new javax.swing.JButton();
        disconnect_button = new javax.swing.JButton();
        anonymous_login = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        text_screen = new javax.swing.JTextArea();
        enter_text = new javax.swing.JTextField();
        send_button = new javax.swing.JButton();
        username_add = new javax.swing.JLabel();
        jButton1 = new javax.swing.JButton();
        send_file = new javax.swing.JButton();
        jMenuBar1 = new javax.swing.JMenuBar();
        jMenu1 = new javax.swing.JMenu();
        OPEN = new javax.swing.JMenuItem();
        Exit = new javax.swing.JMenuItem();

        fileChooser.setDialogTitle("This is my open dialog box");
        fileChooser.setFileFilter(new MyCustomFilter());

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Chat - Client's frame");
        setName("client"); // NOI18N
        setResizable(false);

        title.setText("CLIENT WINDOW");

        username_box.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                text_username(evt);
            }
        });

        connect_button.setText("CONNECT");
        connect_button.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                connect_user(evt);
            }
        });

        disconnect_button.setText("DISCONNECT");
        disconnect_button.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                disconnect_user(evt);
            }
        });

        anonymous_login.setText("ANONYMOUS LOGIN");
        anonymous_login.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                anonymous_user(evt);
            }
        });

        text_screen.setBackground(new java.awt.Color(255, 204, 204));
        text_screen.setColumns(20);
        text_screen.setRows(5);
        jScrollPane1.setViewportView(text_screen);

        enter_text.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                enter_chat(evt);
            }
        });

        send_button.setText("SEND");
        send_button.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                send_text(evt);
            }
        });

        username_add.setText("USERNAME : ");

        jButton1.setText("CLEAR SCREEN");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                clear_screen(evt);
            }
        });

        send_file.setText("SEND FILE");
        send_file.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                send_fileActionPerformed(evt);
            }
        });

        jMenu1.setText("SELECT TEXT FILE TO SEND");

        OPEN.setText("OPEN");
        OPEN.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                OPENActionPerformed(evt);
            }
        });
        jMenu1.add(OPEN);

        Exit.setText("EXIT");
        Exit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ExitActionPerformed(evt);
            }
        });
        jMenu1.add(Exit);

        jMenuBar1.add(jMenu1);

        setJMenuBar(jMenuBar1);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(30, 30, 30)
                .addComponent(enter_text, javax.swing.GroupLayout.PREFERRED_SIZE, 450, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(send_button, javax.swing.GroupLayout.DEFAULT_SIZE, 114, Short.MAX_VALUE)
                    .addComponent(jButton1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(0, 0, Short.MAX_VALUE))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(username_add)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(username_box, javax.swing.GroupLayout.PREFERRED_SIZE, 163, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(anonymous_login))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(connect_button, javax.swing.GroupLayout.PREFERRED_SIZE, 99, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(disconnect_button, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(21, 21, 21))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(title, javax.swing.GroupLayout.PREFERRED_SIZE, 140, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(38, 38, 38)
                        .addComponent(send_file)
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 668, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(title, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(send_file))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 413, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(disconnect_button)
                        .addComponent(connect_button, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(username_add, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(username_box, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addComponent(anonymous_login, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(15, 15, 15)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(enter_text, javax.swing.GroupLayout.PREFERRED_SIZE, 52, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(send_button)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jButton1)))
                .addContainerGap(13, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void connect_user(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_connect_user
        thread_listen();//Invokes thread
        if (Connection_status == false) 
        {
            user_name = username_box.getText();
            username_box.setEditable(false);
            try 
            {
                socket = new Socket(server_address, port_number);
                InputStreamReader streamreader = new InputStreamReader(socket.getInputStream());
                read_buffer = new BufferedReader(streamreader);
                write_buffer = new PrintWriter(socket.getOutputStream());
                write_buffer.println(user_name + ":has connected.:Connect");//Sends server about the new user connection
                write_buffer.flush(); 
                Connection_status = true; 
            } 
            catch (Exception ex) 
            {
                text_screen.append(format.format(new Date())+"Cannot Connect! Try Again. \n");
                username_box.setEditable(true);
            }
        } 
        else if (Connection_status == true) 
        {
            text_screen.append(format.format(new Date())+"You are already connected. \n");//Dsiplays if already connected
        }
    }//GEN-LAST:event_connect_user

    private void disconnect_user(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_disconnect_user
        disconnect_send();
        Disconnected();
    }//GEN-LAST:event_disconnect_user

    private void anonymous_user(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_anonymous_user
        username_box.setText("");
        if (Connection_status == false) 
        {
            String anon="anon"; //Gives a name for the anonymous user
            Random generator = new Random(); 
            int i = generator.nextInt(999) + 1;
            String is=String.valueOf(i);
            anon=anon.concat(is);
            user_name=anon;   
            username_box.setText(anon);
            username_box.setEditable(false);
            try 
            {
                socket = new Socket(server_address, port_number);
                InputStreamReader streamreader = new InputStreamReader(socket.getInputStream());
                read_buffer = new BufferedReader(streamreader);
                write_buffer = new PrintWriter(socket.getOutputStream());
                write_buffer.println(anon + ":has connected.:Connect"); // Sends the user name to server
                write_buffer.flush(); 
                Connection_status = true; 
            } 
            catch (Exception ex) 
            {
                text_screen.append(format.format(new Date())+"Cannot Connect! Try Again. \n");
                username_box.setEditable(true);
            }
            thread_listen();//Invokes thread
        } 
        else if (Connection_status == true) 
            {
                text_screen.append(format.format(new Date())+"You are already connected. \n");//Displays if connection is active
            }
    }//GEN-LAST:event_anonymous_user

    private void send_text(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_send_text
        String nothing = "";
        if ((enter_text.getText()).equals(nothing)) // Waits for user to enter data 
        {
            enter_text.setText("");
            enter_text.requestFocus();
        } 
        else 
        {
            try 
            {
               write_buffer.println(user_name + ":" + enter_text.getText() + ":" + "Chat");// Sends the chat text entered by the user to server
               write_buffer.flush(); // flushes the buffer
            } 
            catch (Exception ex) 
            {
                text_screen.append(format.format(new Date())+"Message was not sent. \n");
            }
            enter_text.setText("");
            enter_text.requestFocus();
        }
        enter_text.setText("");
        enter_text.requestFocus();
    }//GEN-LAST:event_send_text

    private void text_username(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_text_username

    }//GEN-LAST:event_text_username

    private void clear_screen(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_clear_screen
        text_screen.setText("");       
    }//GEN-LAST:event_clear_screen

    private void enter_chat(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_enter_chat
        
    }//GEN-LAST:event_enter_chat

    private void OPENActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_OPENActionPerformed
        int returnVal = fileChooser.showOpenDialog(this);
        try 
        {
            if (returnVal == JFileChooser.APPROVE_OPTION) //Reads the file via file chooser
            {   
                file = fileChooser.getSelectedFile();
                text_screen.read( new FileReader( file.getAbsolutePath() ), null );    	
            } 
            else 
            {
                System.out.println("File access cancelled by user.");
            }
        }
        catch (Exception ex)
        {}
        enter_text.setText("");
        enter_text.requestFocus();
    }//GEN-LAST:event_OPENActionPerformed

    private void ExitActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ExitActionPerformed
        System.exit(0);      
    }//GEN-LAST:event_ExitActionPerformed

    private void send_fileActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_send_fileActionPerformed
        try
        {
            write_buffer = new PrintWriter(socket.getOutputStream(),true);
            write_buffer.println(user_name + ":is sending a file.:file"); //sends the information about file transfer to server
            write_buffer.flush(); 
            FileInputStream fis = new FileInputStream(file); //Opens file stream
            byte[] buffer = new byte[4096];
            String fileCon = "";
            while (fis.read(buffer) > 0)  // Receives the file
            {
                fileCon+=new String(buffer);
            }
            write_buffer.println(fileCon);
            write_buffer.println("end"); // Determines the end of the file
            fis.close(); // Close file stream
            text_screen.append(format.format(new Date())+"Sent file \n"); // Displays fil has been sent
            text_screen.setCaretPosition(text_screen.getDocument().getLength());
        }
        catch (Exception ex)
        {}	
    }//GEN-LAST:event_send_fileActionPerformed

    public static void main(String args[]) 
    {
        java.awt.EventQueue.invokeLater(new Runnable() //Invoke GUI
        {
            @Override
            public void run() 
            {
                new Chat_client().setVisible(true);// Make Visible
            }
        }
        );
    }
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JMenuItem Exit;
    private javax.swing.JMenuItem OPEN;
    private javax.swing.JButton anonymous_login;
    private javax.swing.JButton connect_button;
    private javax.swing.JButton disconnect_button;
    private javax.swing.JTextField enter_text;
    private javax.swing.JFileChooser fileChooser;
    private javax.swing.JButton jButton1;
    private javax.swing.JMenu jMenu1;
    private javax.swing.JMenuBar jMenuBar1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JButton send_button;
    private javax.swing.JButton send_file;
    private javax.swing.JTextArea text_screen;
    private javax.swing.JLabel title;
    private javax.swing.JLabel username_add;
    private javax.swing.JTextField username_box;
    // End of variables declaration//GEN-END:variables
}
