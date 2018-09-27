package robotclient;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;

import shared.messages.server.UsersMessage;
import shared.messages.server.WhiteboardMessage;
import shared.model.User;

public class RobotClientView extends JFrame implements Observer {
	/**
	 * 
	 */
	private static final long serialVersionUID = -953418612274248107L;
	private JLabel whiteboardLabel = new JLabel();
	private JPanel whiteboardPanel = new JPanel();
	public RobotClient client;
	private JTabbedPane tabs = new JTabbedPane();
	private SettingsPanel settingsPanel;
	private JPanel userPanel = new JPanel();
	private UserPanel userListPanel = new UserPanel();
	private TimingDrawingController mouseController;
	private RobotClientViewListener windowListener;

	/*
	 * De main-methode die afhankelijk van de args-length een RobotClientView
	 * met een connectie met de server genereert of een RobotClientView zonder
	 * connectie met de server
	 * 
	 * @param String[] args
	 * 
	 * @return Een RobotClientView
	 */
	public static void main(String[] args) {
		if (args.length == 3) {
			String address = args[0];
			if(address == "localhost"){
				address = "127.0.0.1";
			}
			int port = Integer.parseInt(args[1]);
			String name = args[2];
			new RobotClientView(address, port, name);
		} else {
			new RobotClientView();
		}

	}

	/*
	 * Constructor die wordt aangeroepen als er een address, port en name
	 * beschikbaar zijn
	 * 
	 * @param address
	 * 
	 * @param int port
	 * 
	 * @param String name
	 * 
	 * @return RobotClientView waarbij een random kleur wordt toegewezen aan de
	 * gebruiker
	 */
	public RobotClientView(String address, int port, String name) {
		String cmd = "RANDOM";
		Color color = new Color(0, 0, 0);
		this.client = new RobotClient(address, port, name, cmd, color);
		this.client.addObserver(this);
		createGUI();

	}

	/*
	 * Constructor die wordt aangeroepen als er niets wordt aangegeven
	 * 
	 * @param nothing
	 * 
	 * @return RobotClientView waarbij de gebruiker alles kan instellen
	 */
	public RobotClientView() {
		createGUI();
	}

	/*
	 * methode die wordt aangeroepen als de gebruiker de instellingen heeft
	 * verzonden en deze aan de eisen voldoen. Hierdoor kan er een connectie met
	 * de server worden opgezet
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
	 * 
	 * @return void, maar het bereikte resultaat is dat de WhiteboardClientView
	 * aangepast is en waaraan een Client verbonden is
	 */
	public void onChange(String address, int port, String name, String cmd, Color color) {
		this.client = new RobotClient(address, port, name, cmd, color);
		this.client.addObserver(this);
		updateGUI();
		System.out.println("The settings are adjusted.");
	}

	/*
	 * Methode die wordt aangeroepen als de RobotClient een message binnenkrijgt
	 * en alle observers hierover inlicht. Deze methode kijkt naar het type
	 * message om de juiste manier van afhandelen te regelen.
	 * 
	 * @param Observable
	 * 
	 * @param object
	 * 
	 * @return void, afhandeling naar het type message
	 */
	@Override
	public void update(Observable arg0, Object object) {

		System.out.println("Retrieving message..");

		// Whiteboard en Users messages afhandelen
		if (object instanceof WhiteboardMessage) {
			System.out.println("WhiteboardMessage found");
			WhiteboardMessage message = (WhiteboardMessage) object;
			whiteboardLabel.setIcon(new ImageIcon(message.getImage()));
		} else if (object instanceof UsersMessage) {
			System.out.println("UsersMessage found");
			UsersMessage message = (UsersMessage) object;
			showUsers(message);
		}
	}

	/*
	 * Methode om een GUI te maken voor de RobotClientView
	 * 
	 * @param nothing
	 * 
	 * @return void
	 */
	private void createGUI() {
		setViewTitle();

		// Panel om het Whiteboard in te tonen
		whiteboardLabel.setPreferredSize(new Dimension(600, 800));
		whiteboardPanel = new JPanel();
		whiteboardPanel.add(whiteboardLabel, BorderLayout.CENTER);

		/*
		 * Een JTabbedPane wordt aangemaakt om een userPanel en settingsPanel
		 * aan toe te voegen
		 */
		tabs = new JTabbedPane();
		userPanel = new JPanel();
		userPanel.add(userListPanel);
		settingsPanel = new SettingsPanel(this);
		tabs.addTab("Settings", settingsPanel);
		tabs.addTab("Users", userPanel);
		mouseController = new TimingDrawingController(client);
		whiteboardPanel.addMouseListener(mouseController);

		add(whiteboardPanel, BorderLayout.CENTER);
		add(tabs, BorderLayout.EAST);
		windowListener = new RobotClientViewListener(client, this);
		addWindowListener(windowListener);

		pack();
		setVisible(true);
	}

	/*
	 * Methode om de RobotClientView te updaten zodra er een connectie is
	 * gemaakt
	 * 
	 * @param nothing
	 * 
	 * @return nothing
	 */
	private void updateGUI() {
		setViewTitle();
		System.out.println(this.client);

		whiteboardPanel.removeAll();
		whiteboardLabel.setPreferredSize(new Dimension(600, 800));
		whiteboardPanel.add(whiteboardLabel, BorderLayout.CENTER);
		whiteboardPanel.revalidate();
		whiteboardPanel.repaint();

		whiteboardPanel.removeMouseListener(mouseController);
		mouseController = new TimingDrawingController(this.client);
		whiteboardPanel.addMouseListener(mouseController);

		windowListener = new RobotClientViewListener(this.client, this);
		addWindowListener(windowListener);

	}

	/*
	 * Methode om de titel van de clientapplicatie in te stellen
	 * 
	 * @param void
	 * 
	 * @return void
	 */
	private void setViewTitle() {
		if (client == null) {
			setTitle("Robotclient");
		} else {
			setTitle("Robotclient " + client.getUser().getName());
		}
	}

	/*
	 * Toont de users in het userListPanel
	 * 
	 * @param UsersMessage message
	 * 
	 * @return void, met een bijgewerkt userListPanel
	 */
	private void showUsers(UsersMessage message) {
		List<User> users = message.getUsers();
		System.out.println("Retrieved UsersMessage");
		userPanel.remove(userListPanel);
		userListPanel = new UserPanel(users);
		userPanel.add(userListPanel, BorderLayout.NORTH);
		userPanel.revalidate();
		userPanel.repaint();
	}

}
