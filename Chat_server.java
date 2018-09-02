package chat_server;
import java.io.*;
import java.net.*;
import java.util.*;
import java.text.SimpleDateFormat;

public class Chat_server extends javax.swing.JFrame 
{   //Declarations
    ArrayList clientOutputStreams;
    ArrayList<String> users;
    public static Date date = new Date();
    public static SimpleDateFormat format = new SimpleDateFormat ("MM/dd/yy, h:mm a, ");
    public Socket socket1;
    String red;
    PrintWriter client_writer;
   
    //Run
    public class ServerStart implements Runnable 
    {
        @Override
        public void run() 
        {
            clientOutputStreams = new ArrayList();//Array list to store connected users
            users = new ArrayList();  
            try 
            {
                ServerSocket serverSock = new ServerSocket(4009);
                while (true) 
                {
                    Socket clientSock = serverSock.accept();
                    client_writer = new PrintWriter(clientSock.getOutputStream());
                    clientOutputStreams.add(client_writer);
                    Thread listener = new Thread(new ClientHandler(clientSock, client_writer));
                    listener.start();//Start the thered
                    chat_print.append(format.format(new Date())+"Incoming Connection \n");
                }
            }
            catch (Exception ex)
            {
                chat_print.append(format.format(new Date())+"Error making a connection \n");
            }
        }
    }
    
   //Thread
   public class ClientHandler implements Runnable	
   {
       BufferedReader client_buffer;
       PrintWriter client;
       public ClientHandler(Socket clientSocket, PrintWriter user) 
       {
            client = user;
            try 
            {
                socket1 = clientSocket;
                InputStreamReader client_reader = new InputStreamReader(socket1.getInputStream());
                client_buffer = new BufferedReader(client_reader);
            }
            catch (Exception ex) 
            {
                chat_print.append(format.format(new Date())+"Unexpected error... \n");
            }

       }
    
    //Method to send about the disconnected user to other connected users.
    void remove_user (String name)
    {
        broadcast(name + ": is now offline :Chat");
    }
    
    //Method to broacast the news about usage of reduntant name
    public void reduntant_name()
    {
        red = "reduntant";
        broadcast(red +": :");
    }
      
       @Override
       public void run() 
       {
            String message, connect = "Connect", disconnect = "Disconnect", chat = "Chat" ;
            String[] split_data;
            String file1= "file";
            try 
            {
                while ((message = client_buffer.readLine()) != null) 
                {
                    split_data = message.split(":");//Split the incoming data at occurances of ":"
                    if ((users.indexOf(split_data[0]) != -1)&&(split_data[2].equals(connect)))//Check is incomimg uer name is already in use
                    {
                        reduntant_name();
                    }
                    else
                    {
                        chat_print.append(format.format(new Date())+"Received connection from : " + split_data[0] + "\n");
                        if (split_data[2].equals(connect)) //check in incomimg information is about a new user connection
                        {
                            broadcast((split_data[0] + ":" + split_data[1] + " Available :" + chat));
                            Add_user(split_data[0]);
                        } 
                        else if (split_data[2].equals(disconnect)) //check if incoming information is about a discnnecting user.If yes, delete the user and brodcast the news
                        {
                            broadcast((split_data[0] + ": has disconnected. Unavailable " + ":" + chat));
                            remove_user(split_data[0]);
                            Delete_user(split_data[0]);
                        
                        } 
                        else if (split_data[2].equals(chat))//Check if a user wants to chat and send texts to chat room. If yes broadcast the message
                        {
                            broadcast(message);
                        } 
                        else if (split_data[2].equals(file1))// Check if incoming information is about a new file transfer. If yes, receive the file and brodcaste it
                        {
                            chat_print.append(format.format(new Date())+"Receiving file from : " + split_data[0]);
                            receive_file(split_data[0],client_buffer);  
                            chat_print.setCaretPosition(chat_print.getDocument().getLength());
                        }
                        else
                        {
                            chat_print.append(format.format(new Date())+"Invalid User entry \n");
                        }
                    }
                }
            } 
            catch (Exception ex) 
            {   
                chat_print.append(format.format(new Date())+"Lost a connection. \n");
                ex.printStackTrace();
                clientOutputStreams.remove(client);
            } 
	} 
    }

   //Method to receive the incomig file.
   public void receive_file(String user,BufferedReader client_buffer)
   {
       try 
       {
            FileOutputStream fos = new FileOutputStream("testfile.txt");
            byte[] buffer = new byte[4096];
            int filesize = 15123; // Send file size in separate msg
            int read = 0;
            int totalRead = 0;
            int remaining = filesize;
            String fileCon = null;
            broadcast(user+":Received file:File");//brodcaste the message that a file has been received
            while((fileCon=client_buffer.readLine())!=null)
            {
                if(fileCon.equals("end"))
                    break;
                totalRead += read;
                remaining -= read;
                chat_print.append("\n"+ fileCon);
                fos.write(buffer, 0, read);
                brodcast_file(fileCon);//brodcaste the incomimg file.
            }
            client_writer.println("null");
            chat_print.setCaretPosition(chat_print.getDocument().getLength());
            fos.close();
	}
       catch (Exception ex)
       {}
   }
   
   //Method to brodcaste the incoming file.
   public void brodcast_file(String fileCon)
   {
        Iterator iterator1 = clientOutputStreams.iterator();//to broadcaste to all users
        try 
        {
            while (iterator1.hasNext()) 
            {
            PrintWriter client_writer = (PrintWriter) iterator1.next();
            client_writer.println(fileCon);
            client_writer.flush();
            } 
        }
        catch (Exception ex) 
        {
            chat_print.append(format.format(new Date())+"Error in broadcasting. \n");
        }
   }
   
   //Method to broadcaste the message 
    public void broadcast(String message) 
    {
	Iterator iterator1 = clientOutputStreams.iterator();//to brodcaste to all users
        int flag =0;
        try 
        {
            while (iterator1.hasNext()) 
            {
                flag++;
                PrintWriter client_writer = (PrintWriter) iterator1.next();
		client_writer.println(message);//write message to the client
		chat_print.append(format.format(new Date())+"Sending to user " +flag +" -> " + message +"\n");
                client_writer.flush();
                chat_print.setCaretPosition(chat_print.getDocument().getLength());
            } 
        }
        catch (Exception ex) 
        {
            chat_print.append(format.format(new Date())+"Error in broadcasting. \n");
        }     
    }
    
    //Method to delete the disconnected user
    public void Delete_user (String User_name) 
    {
        String message, add = ": :Connect", connected_status = "Server: :Sending Connected Users";
        users.remove(User_name);
        String[] tempList = new String[(users.size())];
        users.toArray(tempList);
        for (String token:tempList) 
        {
            message = (token + add);
            broadcast(message);
        }
        broadcast(connected_status);
    }
 
 //Method to add the new user to the user list and broadcaste the new connection
  public void Add_user (String User_name) 
    {
        String message, add = ": :Connect", connected_status = "Server: :Sending Connected Users", name = User_name;
        chat_print.append(format.format(new Date())+ " Adding User " + name + "\n");
        users.add(name);
        chat_print.append(format.format(new Date())+"Added user " + name + "\n");
        String[] tempList = new String[(users.size())];
        users.toArray(tempList);
        for (String token:tempList) 
        {
            message = (token + add);
            broadcast(message);
        }
        broadcast(connected_status);
    }
      
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jScrollPane1 = new javax.swing.JScrollPane();
        chat_print = new javax.swing.JTextArea();
        start_server_button = new javax.swing.JButton();
        stop_server_button = new javax.swing.JButton();
        connected_users_botton = new javax.swing.JButton();
        clear_button = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Chat - Server's frame");
        setForeground(java.awt.Color.yellow);
        setName("server"); // NOI18N
        setResizable(false);

        chat_print.setBackground(new java.awt.Color(255, 204, 204));
        chat_print.setColumns(20);
        chat_print.setForeground(new java.awt.Color(51, 0, 102));
        chat_print.setLineWrap(true);
        chat_print.setRows(5);
        jScrollPane1.setViewportView(chat_print);

        start_server_button.setText("START SERVER");
        start_server_button.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                start_server_on_click(evt);
            }
        });

        stop_server_button.setText("STOP SERVER");
        stop_server_button.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                stop_server_on_click(evt);
            }
        });

        connected_users_botton.setText("CONNECTED USERS");
        connected_users_botton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                display_users_on_click(evt);
            }
        });

        clear_button.setText("CLEAR SCREEN");
        clear_button.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                clear_screen_on_click(evt);
            }
        });

        jLabel1.setText("SERVER WINDOW");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jScrollPane1)
                        .addContainerGap())
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(start_server_button, javax.swing.GroupLayout.PREFERRED_SIZE, 131, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(stop_server_button, javax.swing.GroupLayout.PREFERRED_SIZE, 137, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addContainerGap())
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel1)
                        .addGap(129, 129, 129)
                        .addComponent(clear_button, javax.swing.GroupLayout.DEFAULT_SIZE, 108, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(connected_users_botton)
                        .addGap(20, 20, 20))))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(clear_button, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(connected_users_botton))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 477, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(start_server_button)
                    .addComponent(stop_server_button))
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void stop_server_on_click(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_stop_server_on_click
        try 
        {
            broadcast("Server:is stopping and all users will be disconnected :Chat"); // broadcaste the server is stopping
            broadcast("Server:is stopping and all users will be disconnected :Stop");
            chat_print.append(format.format(new Date())+"Server stopping... \n");
            socket1.close();
            Thread.sleep(600);
            System.exit(0);//close server
        } 
        catch(Exception ex) 
        {
            ex.printStackTrace();
        }
    }//GEN-LAST:event_stop_server_on_click

    private void start_server_on_click(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_start_server_on_click
        Thread starter = new Thread(new ServerStart());// start the server on click
        starter.start();
        chat_print.append(format.format(new Date())+"Server started. \n");
    }//GEN-LAST:event_start_server_on_click

    private void display_users_on_click(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_display_users_on_click
        chat_print.append(format.format(new Date())+"Online users : \n");
        for (String current_user : users)//display connected users
        {
            chat_print.append("                                                                "+current_user);
            chat_print.append("\n");
        }    
    }//GEN-LAST:event_display_users_on_click

    private void clear_screen_on_click(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_clear_screen_on_click
        chat_print.setText("");//CLeat screen
    }//GEN-LAST:event_clear_screen_on_click
 
    //Initialise GUI
    public Chat_server() 
    {
        initComponents();
    }
    
    public static void main(String args[]) 
    {
        java.awt.EventQueue.invokeLater(new Runnable() //Invoke GUI
        {
            @Override
            public void run() 
            {
                new Chat_server().setVisible(true);//Set visible
            }
        }
        );
    }
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTextArea chat_print;
    private javax.swing.JButton clear_button;
    private javax.swing.JButton connected_users_botton;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JButton start_server_button;
    private javax.swing.JButton stop_server_button;
    // End of variables declaration//GEN-END:variables
}
