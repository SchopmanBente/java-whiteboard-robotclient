package robotclient;

import java.awt.Color;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Observable;
import java.util.Random;

import javax.swing.Timer;

import shared.messages.Message;
import shared.messages.client.InitialMessage;
import shared.messages.client.StopMessage;
import shared.model.User;

public class RobotClient extends Observable {
	private User user;
	private String name;
	private Socket socket;
	private ObjectOutputStream writer;
	private Timer timer;

	/*
	 * Constructor voor de WhiteboardClient
	 * 
	 * @param String address
	 * 
	 * @param int port
	 * 
	 * @param String name
	 * 
	 * @param String cmd
	 * 
	 * @param Color color
	 */
	public RobotClient(String address, int port, String name, String cmd, Color color) {
		this.user = createUser(name, cmd, color);
		;
		this.setName(name);
		setUpNetworking(address, port);

		System.out.println("Sending Initialmessage");

		sendMessage(new InitialMessage(this.getUser()));

		System.out.println("Adding incomingreader");

		new IncomingReader(socket, this);

		timer = new Timer(10000, new TimingDrawingController(this));
		timer.start();

		setChanged();
		notifyObservers(this);
	}

	/*
	 * Methode om een user aan te maken
	 * 
	 * @param String name
	 * 
	 * @param String cmd
	 * 
	 * @param Color color
	 * 
	 * @return User
	 */
	private User createUser(String name, String cmd, Color color) {
		User newUser = new User(name, color);
		if (cmd == "CHOOSE") {
			newUser = new User(name, color);
		}
		if (cmd == "RANDOM") {
			newUser = new User(name, generateColor());
		}
		return newUser;
	}

	/*
	 * Methode om random een kleur te genereren waarbij elke waarde(r,g,b)
	 * tussen de 0 en 255 ligt
	 * 
	 * @param nothing
	 * 
	 * @return Color
	 */
	private Color generateColor() {
		Random random = new Random();
		int lowerBound = 0;
		int higherBound = 255;
		int r = random.nextInt(higherBound - lowerBound) + lowerBound;
		int g = random.nextInt(higherBound - lowerBound) + lowerBound;
		int b = random.nextInt(higherBound - lowerBound) + lowerBound;
		return new Color(r, g, b);
	}

	/*
	 * Methode om een verbinding met de server op te zetten
	 * 
	 * @param String address
	 * 
	 * @param int port
	 * 
	 * @return void, geslaagd networkconnectie of niet
	 */
	public void setUpNetworking(String address, int port) {
		try {
			socket = new Socket(address, port);
			writer = new ObjectOutputStream(socket.getOutputStream());
			System.out.println("network connection established");
		} catch (IOException ex) {
			ex.printStackTrace();
		}
	}

	/*
	 * Methode om als een client een Message te versturen
	 * 
	 * @param Message message
	 * 
	 * @return void, message verstuurd naar de server
	 */
	public void sendMessage(Message message) {
		try {
			writer.writeObject(message);
			writer.flush();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	/*
	 * Methode om een inkomend bericht te weergeven in de RobotClientView
	 * 
	 * @param Message message
	 * 
	 * @return void, WhiteboardClientView op de hoogte van de wijzigingen
	 */
	public void addIncoming(Message message) {
		System.out.println("Message incoming");
		setChanged();
		notifyObservers(message);
	}

	public String getName() {
		return name;
	}

	public User getUser() {
		return this.user;
	}

	public Color getColor() {
		// TODO Auto-generated method stub
		return this.getUser().getColor();
	}

	/*
	 * Methode om een StopMessage te verturen naar de Server
	 * 
	 * @param nothing
	 * 
	 * @return void, User afgemeld bij de server
	 */
	public void sendStopMessage() {
		// Timer wordt gestopt om te voorkomen dat de robot berichten blijft
		// sturen
		timer.stop();
		StopMessage message = new StopMessage(this.getUser());
		sendMessage(message);
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setUser(User user) {
		this.user = user;
	}

}
