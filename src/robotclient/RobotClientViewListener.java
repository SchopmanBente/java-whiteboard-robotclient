package robotclient;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class RobotClientViewListener extends WindowAdapter {

	private RobotClient client;
	private RobotClientView clientView;

	/*
	 * Constructor van de RobotClientViewListener
	 * 
	 * @Param: RobotClient client
	 * 
	 * @Param: RobotClientView view
	 */
	public RobotClientViewListener(RobotClient client, RobotClientView view) {
		this.client = client;
		this.clientView = view;

	}

	/*
	 * Als de Window gesloten wordt en er is een client aanwezig, verstuur dan
	 * een StopMessage
	 * 
	 * @Param: WindowEvent e
	 * 
	 * @return: void, er wordt een StopMessage verstuurd als er een connectie is
	 * met de server
	 */
	@Override
	public void windowClosing(WindowEvent e) {
		if (this.client != null) {
			client.sendStopMessage();
		}
	}

}
