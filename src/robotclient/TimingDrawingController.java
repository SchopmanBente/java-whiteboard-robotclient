package robotclient;

import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import shared.messages.client.DrawingMessage;
import shared.model.TextDrawing;

public class TimingDrawingController extends MouseAdapter implements ActionListener, DrawingController {

	private RobotClient client;
	private RobotClientView clientView;
	private Random random;
	private List<String> texts = new ArrayList<String>();

	/*
	 * Constructor voor TimingDrawingController
	 * 
	 * @param RobotClient client
	 */
	public TimingDrawingController(RobotClient client) {
		this.client = client;
		this.random = new Random();
		this.texts.add("TEST");
		this.texts.add("ROBOT TEST");
		this.texts.add("ROBOT TEXT");
	}

	/*
	 * Methode om een DrawingMessage met het type drawing Text te versturen naar
	 * de server
	 * 
	 * @param MouseEvent e
	 * 
	 * @return void, verzonden DrawingMessage
	 */
	@Override
	public void actionPerformed(ActionEvent arg0) {
		// Bepaal de random positie
		int x = random.nextInt(750);
		int y = random.nextInt(750);
		Point location = new Point(x, y);
		// Bepaal welke tekst wordt geplaatst
		int textIndex = random.nextInt(this.texts.size()) - 1;
		if (textIndex < 0) {
			textIndex = 0;
		}
		String text = this.texts.get(textIndex);
		// Maak en verstuur de Drawing
		TextDrawing drawing = new TextDrawing(location, text);
		System.out.println("Sending a DrawingMessage with Drawing: Text");
		DrawingMessage textMessage = new DrawingMessage(client.getUser(), drawing);
		client.sendMessage(textMessage);

	}

}
