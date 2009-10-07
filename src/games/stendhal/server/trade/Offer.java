package games.stendhal.server.trade;


import games.stendhal.server.entity.item.Item;
import marauroa.common.game.RPClass;
import marauroa.common.game.RPObject;
import marauroa.common.game.Definition.Type;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

public class Offer extends RPObject {
	
	public static final String OFFER_RPCLASS_NAME = "offer";

	private final Item item;
	
	private final Integer price;

	private final String offererName;
	
	public static void generateRPClass() {
		final RPClass offerRPClass = new RPClass(OFFER_RPCLASS_NAME);
		offerRPClass.isA("entity");
		offerRPClass.addAttribute("price", Type.INT);
		offerRPClass.addAttribute("offererName", Type.STRING);
		offerRPClass.addAttribute("class", Type.STRING);
		offerRPClass.addRPSlot("item", 1);
	}
	
	/**
	 * @param item
	 */
	public Offer(final Item item, final Integer price, final String offererName) {
		super();
		setRPClass("offer");
		this.addSlot("item");
		this.getSlot("item").add(item);
		this.item = item;
		this.put("price", price.intValue());
		this.price = price;
		this.put("offererName", offererName);
		this.offererName = offererName;
		store();
	}
	
	public Offer(final RPObject object) {
		this((Item) object.getSlot("item").getFirst(), Integer.valueOf(object.getInt("price")), object.get("offererName"));
	}


	public final Item getItem() {
		return item;
	}



	public final Integer getPrice() {
		return price;
	}



	public final String getOffererName() {
		return offererName;
	}

	
	@Override
	public int hashCode() {
		return HashCodeBuilder.reflectionHashCode(this);
	}

	@Override
	public boolean equals(final Object obj) {
		return EqualsBuilder.reflectionEquals(this, obj, false,
				Offer.class);
	}
	
}
