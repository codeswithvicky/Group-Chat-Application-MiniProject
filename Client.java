import java.io.*;
import java.net.*;
import java.util.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.text.*;

public class Client implements KeyListener
{

	private JTextArea msgArea;      
  	private JTextField sendArea;         
  	private JTextField loginPort;             
   	private JTextField loginName;            
   	private JFrame loginFrame;               
   	private JFrame chatFrame;              
    private int port;
	
   	private BufferedReader reader;      
   	private PrintWriter writer;         
   	private Socket socket;                
   	private String IPAddress="192.168.43.160";               
   	private String userName;           
	final static String secretKey = "private";
	EncryDecry encryDecry=new EncryDecry();
	SimpleDateFormat time=new SimpleDateFormat("hh:mm aa");
   	
	public static void main(String[] args)
	{
	      	Client c= new Client();
      		c.loginForm();
   	}
   
  
   	public void loginForm()
	{

      		loginFrame = new JFrame("LOGIN");
      		JPanel loginPanel  = new JPanel();
      		loginPanel.setLayout(new GridLayout(0, 2));
			loginPanel.setBackground(new Color(152,251,152));
      		
      		//for formatting the panels
      		JPanel container = new JPanel();
      		container.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
      		container.setLayout(new BorderLayout());
			container.setBackground(new Color(152,251,152));
      
      		//set the properties of the welcome label
      		JLabel userlogin = new JLabel("USER LOGIN", SwingConstants.CENTER);
      		userlogin.setFont(new Font("Cambria", Font.BOLD, 26));
      		userlogin.setForeground(Color.red);
      		loginFrame.add(userlogin, BorderLayout.NORTH);
      
      		//the ip label & field set up and add
      		JLabel IPLabel = new JLabel("Enter HOST Port Address:");
			IPLabel.setFont(new Font("Cambria",Font.BOLD,20));
      		loginPanel.add(IPLabel);
 
      		loginPort = new JTextField(20);
      		loginPanel.add(loginPort);
      
      		//set up chat name label & field
      		JLabel name = new JLabel("Enter User Name:");	
			name.setFont(new Font("Cambria",Font.BOLD,20));
      		loginPanel.add(name);
      
      		loginName = new JTextField(20);
      		loginPanel.add(loginName);
      
      		//add the chatPanel to container for formatting
      		container.add(loginPanel);
      		loginFrame.add(container, BorderLayout.CENTER);
      
      		//create a ok button and listener
      		JButton connect = new JButton("CONNECT");
			connect.setBackground(new Color(135,206,250));
      		connect.addActionListener(new OKListener());
      		//create a panel so button doesn't take up entire south field
      		JPanel southPanel = new JPanel();
      		southPanel.setLayout(new FlowLayout());
			southPanel.setBackground(new Color(152,251,152));
      		southPanel.add(connect);
      		loginFrame.add(southPanel, BorderLayout.SOUTH);
      
      		//set properties of the loginFrame
			loginFrame.setSize(500,400);
      		loginFrame.setResizable(false);
      		loginFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
      
      		//set it to the center of the screen by getting screen size
      		loginFrame.setLocationRelativeTo(null);
      
      		loginFrame.pack();
      		loginFrame.setVisible(true); 
   	}
   
  
	public void chatGUI()
	{
      		//set up the loginFrame and panels
      		chatFrame = new JFrame("GROUP-CHAT");
      		JPanel chatPanel = new JPanel();
      		chatPanel.setLayout(new FlowLayout());
      
      		//add a menu
      		menu();
      
      		//add the title and center
      		JLabel title = new JLabel("USER:"+userName+"             GROUP CHAT", SwingConstants.LEFT);
      		title.setFont(new Font("Congenial black", Font.BOLD, 25));
      		title.setForeground(Color.red);
      		
      		chatFrame.add(title,BorderLayout.NORTH);
      		//set up the recieved messages
      		msgArea = new JTextArea(15,50);
      		msgArea.setFont(new Font("Serif", Font.PLAIN, 18));
			msgArea.setBackground(new Color(247,233,201));
      		msgArea.setLineWrap(true);
      		msgArea.setWrapStyleWord(true);
      		msgArea.setEditable(false);
      
      		//allow the recieved messages to be scrollable
      		JScrollPane scroll = new JScrollPane(msgArea);
      		scroll.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
      		chatFrame.add(scroll, BorderLayout.CENTER);
      
      		//set up the field for sending text
      		sendArea = new JTextField(55);
			sendArea.addKeyListener(this);
      		chatPanel.add(sendArea);
      
      		//add a send button and a listener
      		JButton send = new JButton(new ImageIcon("send.png")); 
			send.setOpaque(false);
			send.setFocusPainted(false);
			send.setContentAreaFilled(false); 
			send.setBorder(BorderFactory.createEmptyBorder());
      		send.addActionListener(new SendListener());
      		chatPanel.add(send);
      
		
      		//connect to the server
      		connect();
      
     		//add to the chatPanel loginFrame and set properties
			chatFrame.add(chatPanel, BorderLayout.SOUTH);
      		chatFrame.setResizable(false);
      		chatFrame.setLocationRelativeTo(null);
      		chatFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
      		chatFrame.pack();
      		chatFrame.setVisible(true);
      		
  	} 
	public void keyPressed(KeyEvent ke)
	{
	
	}
	public void keyReleased(KeyEvent ke)
	{
		if(ke.getKeyCode()==10)
		{
			try
			{
            		//add chat name in front of the text entered by the user
					String s=time.format(new Date());
            		if(!(sendArea.getText().equals("")))
			{
            			String sendMsg = userName + ": " + sendArea.getText()+"   ["+s+"]";
				String encryptedmsg = encryDecry.encrypt(sendMsg,secretKey);
            			writer.println(encryptedmsg);                              //send the message to the server
            			writer.flush();	 //flushes
			}                                       
            
            
         	}
			catch(Exception ex)
			{
            		//pushes an user friendly disconnect message to the client
            		msgArea.append("\nClient is not connected. Please try restarting the client.\n");
           
           			//pushes the scroll area to show the most recent text from others
           			msgArea.setCaretPosition(msgArea.getDocument().getLength());
         
			}//end catch
         
         	//clears the send text field and prepares it for recieving most user input
         	sendArea.setText("");
         	sendArea.requestFocus();

		}
	}
	public void keyTyped(KeyEvent ke)
	{
	
	}
  
  	private void menu()
	{
      
      		JMenuBar menuBar = new JMenuBar();
      		chatFrame.setJMenuBar(menuBar);
      	
      		//set up menu
      		JMenu fileMenu = new JMenu("File");
      		JMenuItem EXIT = new JMenuItem("Exit");
      		fileMenu.add(EXIT);
      		menuBar.add(fileMenu);
      
      		EXIT.addActionListener(new ExitListener());
  	}
  
  
  
  	private void connect()
	{
    		//keeps going until a connection goes through
    		while(true)
			{
					try
					{ 
						//connect to address
						socket = new Socket(IPAddress, port);
            			//if the connection goes through aka not null, do below
            			if(socket != null)
						{
            
               				//set up the ability to send and write messages sent/recieved  
               				InputStreamReader in = new InputStreamReader(socket.getInputStream());
               				reader = new BufferedReader(in);
               				writer = new PrintWriter(socket.getOutputStream());
					
							writer.println(userName);
							writer.flush();
               				//prints out a user friendly message to the client saying you have connected
               				msgArea.append("You have connected to the Chat Room\n---------------------------------------------------\n");
               				String s="\t\t***"+userName+" HAS JOINED IN THE CHAT***";
               				//set up a thread
							writer.println(encryDecry.encrypt(s,secretKey));
							writer.flush();
               				Thread t = new Thread(new Receiving());
               				t.start();
							
               				t.sleep(1000);
               				//breaks out of loop
               				break;
            			}
              
            
					}
					catch(IOException ie)
					{
						//prints a user friendly message stating failure to connect
						msgArea.append("\nClient did not connect to the Chat Room. Please try again\n");
					}
					catch(InterruptedException e)
					{
						//prints a user friendly message stating failure to connect
						msgArea.append("\nClient did not connect to the Chat Room. Please try again\n");
					}
     		}//end while loop
	}//end connect function
  
 
  	public class ExitListener implements ActionListener
	{
      	public void actionPerformed(ActionEvent ae)
		{
			String sendMsg="\t\t***"+userName+" HAS LEFT THE CHAT***";
			writer.println(encryDecry.encrypt(sendMsg,secretKey));
			writer.flush();
         	System.exit(1);                           //exits the program when clicked
      	}
  	}
 	
	public class SendListener implements ActionListener
	{
      	public void actionPerformed(ActionEvent ae)
		{
     		try
			{
            	if(!(sendArea.getText().equals("")))
			{
		
            	//add chat name in front of the text entered by the user
				String s=time.format(new Date());
            	String sendMsg = userName + ": " + sendArea.getText()+"   ["+s+"]";
				String encryptedmsg = encryDecry.encrypt(sendMsg,secretKey);
            	writer.println(encryptedmsg);                              //send the message to the server
            	writer.flush();                                        //flushes
			}
         	}
			catch(Exception ex)
			{
            	//pushes an user friendly disconnect message to the client
            	msgArea.append("\nClient is not connected. Please try restarting the client.\n");
            
				//pushes the scroll area to show the most recent text from others
            	msgArea.setCaretPosition(msgArea.getDocument().getLength());
         
			}//end catch
         
         		//clears the send text field and prepares it for recieving most user input
         	sendArea.setText("");
         	sendArea.requestFocus();
         
      	}//end actionPerformed

   	}//end inner class SendListener
   
	public class OKListener implements ActionListener
	{
      	public void actionPerformed(ActionEvent ae)
		{
           		//save the entered values into appropiate attributes
           		port = Integer.parseInt(loginPort.getText());
           		userName = loginName.getText();
           
           		loginFrame.dispose(); //after above values are saved, the initial gui loginFrame is disposed
            
            		//call second client for actual chatting
            	chatGUI();
      	}//end actionperformed
		
   	}//end inner class OKListener
   
   	public class Receiving extends JFrame implements Runnable
	{
      		@Override
      		public void run()
			{
         		String msg;                         //for saving the message that gets sent to the client
         
         		try
				{
					String message="";
					EncryDecry ed=new EncryDecry();
					while((msg=reader.readLine())!=null)
					{
						message=ed.decrypt(msg,secretKey);
               				//pushes the scroll area to show the most recent text from others 
						msgArea.append(message+"\n");
						msgArea.setCaretPosition(msgArea.getDocument().getLength());
			
					}
					
         		}
				catch(Exception ex)
				{
            			//pushes an user friendly disconnect message to the client
            			msgArea.append("\nClient is not connected. Please try restarting the client.\n"); 
						JOptionPane.showMessageDialog(this,"SERVER TERMINATED THE CHAT","INFO",JOptionPane.WARNING_MESSAGE);
						chatFrame.dispose();
						System.exit(1);
            
            	}//end catch
         
      		}//end run
      
	}//end inner class Receiving
   
}//end Chatclient

