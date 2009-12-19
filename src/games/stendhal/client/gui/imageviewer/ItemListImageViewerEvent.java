package games.stendhal.client.gui.imageviewer;

import java.awt.Dimension;
import java.net.URL;

import javax.swing.JLabel;

import marauroa.common.game.RPEvent;
import marauroa.common.game.RPObject;
import marauroa.common.game.RPSlot;

/**
 * Opens a styled internal frame displaying an item list
 * 
 * @author hendrik
 */
public class ItemListImageViewerEvent extends ViewPanel {
	private static final long serialVersionUID = -6114543463410539585L;

	private RPEvent event;

	/**
	 * creates a new ItemListImageViewerEvent
	 *
	 * @param event event
	 */
	public ItemListImageViewerEvent(RPEvent event) {
		this.event = event;
	}

	/**
	 * shows the window
	 */
	public void view() {
		new ImageViewWindow(event.get("title"), this);
	}


	@Override
	public void prepareView(final Dimension maxSize) {
		// only display when not null
		StringBuilder html = new StringBuilder();
		html.append("<html><body style=\"color: #FFFFFF\"><table border=\"1\">");
		html.append("<tr><th>Item</th><th>Price</th><th>Description</th></tr>");

		RPSlot slot = event.getSlot("content");
		for (RPObject item : slot) {
			createRow(html, item);
		}

		
		html.append("</table></body></html>");

		final JLabel imageLabel = new JLabel(html.toString());

		add(imageLabel);

		setVisible(true);
	}

	/**
	 * creates an item row
	 *
	 * @param html  out buffer to append to
	 * @param item  RPObject representing an item to display
	 */
	private void createRow(StringBuilder html, RPObject item) {
		URL url = getItemImageURL(item);
		String price = getFormatedPrice(item);

		html.append("<tr><td>");
		html.append("<img src=\"" + url.toString() + "\">");
		html.append("</td><td>");
		html.append(price);
		html.append("</td><td>");
		html.append(item.get("description"));
		html.append("</td></tr>");
	}

	/**
	 * formats the price depending on its sign.
	 *
	 * @param RPObject representing an item to display 
	 * @return html code to display price
	 */
	private String getFormatedPrice(RPObject item) {
		String price = "";
		if (item.has("price")) {
			int priceInt = item.getInt("price");
			price = "<span style=\"color: ";
			if (priceInt < 0) {
				price = price + "#F00\">";
			} else {
				price = price + "#0F0\">";
			}
			price = price + Math.abs(priceInt) + "</span>";
		}
		return price;
	}

	private URL getItemImageURL(RPObject item) {
		String itemName = item.get("class") + "/" + item.get("subclass");
		String imagePath = "/data/sprites/items/" + itemName + ".png";
		URL url = this.getClass().getResource(imagePath);
		return url;
	}
}
