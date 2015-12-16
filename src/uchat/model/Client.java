package uchat.model;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Chat client class, must also be created for both the one hosting and those who join.
 * Like server getMessages() must be called to retrieve connection/error messages, 
 * as well as the chat communications. 
 * Use a timer, like with the server. 
 * Additionally, messages must be sent from the UI to be streamed to the server with sendMessage().
 * 
 * @author David Lerner
 * @version 1.0
 */
public class Client implements Runnable
{
  private final Object moniter = new Object();
  public String name;
  public ArrayList<User> users;
  private Socket socket = null;
  private Thread thread = null;
  private ObjectInputStream streamIn = null;
  private ObjectOutputStream streamOut = null;
  private ClientThread client = null;
  private String id;
  private BufferedImage pic;
  private ArrayList<Message> messageBuffer;
  private Message fromUI;
  private boolean hasLog;

    /**
     * Constructor for the Client class. Threads are automatically started at construction.
     * 
     * @param serverName the IP address of the server
     * @param serverPort the application's port number
     * @param name the user's name
     * @param pic the user's image (not supported)
     */
    public Client(String serverName, int serverPort, String name,
                BufferedImage pic) {
    this.name = name;
    this.pic = pic;
    messageBuffer = new ArrayList<>();
    id = "";
    users = new ArrayList<>();
    hasLog = false;
    addMessage(new Message("Establishing connection. Please wait ...",
        Message.messageType.ALERT));
    try {
      socket = new Socket(serverName, serverPort);
      id = socket.toString();
      fromUI = new Message(name, socket.getLocalAddress().toString(), Message.messageType.CONNECT);
      addMessage(new Message("Connected: " + socket,
          Message.messageType.ALERT));
      start();
    } catch (UnknownHostException uhe) {
      addMessage(new Message("Host unknown: " + uhe.getMessage(),
          Message.messageType.ERROR));
    } catch (IOException ioe) {
      addMessage(new Message("Unexpected exception: " + ioe.getMessage(),
          Message.messageType.ERROR));
    }
  }

  protected synchronized void addMessage(Message msg) {
    messageBuffer.add(msg);
  }

  /**
   * Used to obtain all the connection/error/chat messages received by the client.
   *
   * @return a list of Message objects
   */
  public synchronized ArrayList<Message> getMessages() {
    ArrayList<Message> temp = messageBuffer;
    messageBuffer = new ArrayList<>();
    return temp;
  }

  @Override
  public void run() {
    while (thread != null) {
      try {
        synchronized (moniter) {
          if (hasLog)
            moniter.wait();
        }
        Message msg = getMessageFromUI();
        if (msg != null) {
          if (msg.getType() == Message.messageType.CONNECT)
            hasLog = true;
          streamOut.writeObject(msg);
          streamOut.flush();
        }
      } catch (IOException ioe) {
        addMessage(new Message("Sending error: " + ioe.getMessage(),
            Message.messageType.ERROR));
        end();
      } catch (InterruptedException ie) {
        Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ie);
      }
    }
  }

  private synchronized Message getMessageFromUI() {
    Message temp = fromUI;
    fromUI = null;
    return temp;
  }

  /**
   * Used by the UI to send chat messages.
   *
   * @param text The text message to be sent to the chat server
   * @param img  An accompanying image (not supported -nothing will be sent)
   */
  public void sendMessage(String text, BufferedImage img) {
    synchronized (this) {
      fromUI = new Message(name, text, id, pic, img);
      fromUI = new Message(name, text, id, null, null); //comment out this line when object sending is implemented
    }
    synchronized (moniter) {
      moniter.notify();
    }
  }

  protected void handle(Message msg) {
    if (msg.getType() == Message.messageType.EXIT) {
      end();
    } else {
      if (msg.getId().equals(id))
        addMessage(new Message("(You) " + msg.getName(), msg.getText(),
            msg.getId(), msg.getPicture(), msg.getImage()));
      else
        addMessage(msg);
    }
  }

  public void start() throws IOException {
    streamOut = new ObjectOutputStream(socket.getOutputStream());
    if (thread == null) {
      client = new ClientThread(this, socket);
      thread = new Thread(this);
      thread.start();
    }
  }

  protected void end() {
    if (thread != null) {
      thread = null;
    }
    try {
      if (streamIn != null)
        streamIn.close();
      if (streamOut != null)
        streamOut.close();
      if (socket != null)
        socket.close();
    } catch (IOException ioe) {
      addMessage(new Message("Error closing: " + ioe.getMessage(),
          Message.messageType.ERROR));
    }
    client.close();
    client.end();
  }

    /**
     * Gets the IP address and port number of the this client as a unique ID.
     * 
     * @return a string containing the IP address and port number of the socket this client uses 
     */
    public String getID() {
    return id;
  }

}
