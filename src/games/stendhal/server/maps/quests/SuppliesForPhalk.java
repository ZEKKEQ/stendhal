package games.stendhal.server.maps.quests;

import games.stendhal.server.core.engine.SingletonRepository;
import games.stendhal.server.entity.item.Item;
import games.stendhal.server.entity.npc.ChatAction;
import games.stendhal.server.entity.npc.ConversationPhrases;
import games.stendhal.server.entity.npc.ConversationStates;
import games.stendhal.server.entity.npc.EventRaiser;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.npc.action.DropInfostringItemAction;
import games.stendhal.server.entity.npc.action.DropItemAction;
import games.stendhal.server.entity.npc.action.EquipItemAction;
import games.stendhal.server.entity.npc.action.IncreaseXPAction;
import games.stendhal.server.entity.npc.action.MultipleActions;
import games.stendhal.server.entity.npc.action.SetQuestAction;
import games.stendhal.server.entity.npc.action.SetQuestAndModifyKarmaAction;
import games.stendhal.server.entity.npc.condition.AndCondition;
import games.stendhal.server.entity.npc.condition.NotCondition;
import games.stendhal.server.entity.npc.condition.PlayerHasInfostringItemWithHimCondition;
import games.stendhal.server.entity.npc.condition.PlayerHasItemWithHimCondition;
import games.stendhal.server.entity.npc.condition.QuestCompletedCondition;
import games.stendhal.server.entity.npc.condition.QuestInStateCondition;
import games.stendhal.server.entity.npc.condition.QuestNotStartedCondition;
import games.stendhal.server.entity.npc.parser.Sentence;
import games.stendhal.server.entity.player.Player;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

/**
 * QUEST: Supplies For Phalk
 *
 * PARTICIPANTS:
 * <ul>
 * <li>Phalk, the dwarf guarding Semos mines</li>
 * <li>Wrvil, a kobold weapon trader in Wo'fol</li>
 * <li>Mrotho, in Ados barracks</li>
 * </ul>
 *
 * STEPS:
 * <ul>
 * <li>1. Phalk asks for some food and drink</li>
 * <li>2. Once you brought him the food and drink, Phalk asks you to collect his clothes from Wrvil and Mrotho</li>
 * <li>3. Wrvil gives you Phalk's special dwarf cloak but you must pay for it in arrows</li>
 * <li>4. Mrotho gives you Phalk's special golden armor but you must pay for it in gold bars</li>
 * <li>5. Phalk accepts only the special items from Wrvil and Mrotho with his name on</li>
 * </ul>
 *
 * REWARD:
 * <ul>
 * <li>Dwarvish armor</li>
 * <li>5000 XP in total</li>
 * <li>Karma</li>
 * </ul>
 *
 * REPETITIONS:
 * <ul>
 * <li>Not repeatable.</li>
 * </ul>
 */
 
 public class SuppliesForPhalk extends AbstractQuest {
 
 	private static final String QUEST_SLOT = "supplies_for_phalk";
 	
 
 	@Override
	public String getSlotName() {
		return QUEST_SLOT;
	}
	private void askForFood() {
		final SpeakerNPC npc = npcs.get("Phalk");	
		
		npc.add(ConversationStates.ATTENDING,
				ConversationPhrases.QUEST_MESSAGES, 
				new QuestNotStartedCondition(QUEST_SLOT),
				ConversationStates.QUEST_OFFERED, 
				"I've been here a long time, and I can not leave this place. Could you bring me some food?",
				null);
							
		npc.add(ConversationStates.ATTENDING,
				ConversationPhrases.QUEST_MESSAGES, 
				new QuestCompletedCondition(QUEST_SLOT),
				ConversationStates.ATTENDING,
				"Thanks for getting me the food and clothes. I think I can stand here warning people for some months longer now.",
				null);
		

		npc.add(ConversationStates.QUEST_OFFERED,
				ConversationPhrases.YES_MESSAGES, null,
				ConversationStates.ATTENDING,
				"Oh, great! I am really hungry and thirsty. 3 #sandwiches, 3 bottles of #beer and 3 glasses of #wine should be enough. Please bring it to me and say #food!",
				new SetQuestAndModifyKarmaAction(QUEST_SLOT, "start", 5.0));

		// Player says no, they've lost karma.
		npc.add(ConversationStates.QUEST_OFFERED,
				ConversationPhrases.NO_MESSAGES, null, ConversationStates.IDLE,
				"Oh, thats not nice... but ok. Maybe the next visitor can help me.",
				new SetQuestAndModifyKarmaAction(QUEST_SLOT, "rejected", -10.0));
		
		npc.addReply("beer", "In an INN of course!");
		npc.addReply("wine", "In an INN of course!");
		npc.addReply(Arrays.asList("sandwiches", "sandwich"), "Come on, ask in a bakery!");
	}
	
	private void receiveFood() {
	final SpeakerNPC npc = npcs.get("Phalk");	
	
		npc.add(ConversationStates.ATTENDING, "food",
				new QuestInStateCondition(QUEST_SLOT, "start"),
				ConversationStates.QUEST_ITEM_QUESTION,
				"Do you have 3 sandwiches, 3 bottles of beer and 3 glasses of wine?",
				null);
		
		final List<ChatAction> actions = new LinkedList<ChatAction>();
		actions.add(new IncreaseXPAction(600));
		actions.add(new DropItemAction("sandwich",3));
		actions.add(new DropItemAction("beer",3));
		actions.add(new DropItemAction("wine",3));
		// the extra parts in the quest state are for wrvil and mrotho not to give them cloaks and armor twice
		actions.add(new SetQuestAndModifyKarmaAction(QUEST_SLOT, "clothes;none;none", 2.0));		
		
		npc.add(ConversationStates.QUEST_ITEM_QUESTION, ConversationPhrases.YES_MESSAGES,
				new AndCondition(
						new QuestInStateCondition(QUEST_SLOT, "start"),
						new PlayerHasItemWithHimCondition("sandwich",3),
						new PlayerHasItemWithHimCondition("beer",3),
						new PlayerHasItemWithHimCondition("wine",3)),
				ConversationStates.ATTENDING, 
				"Yay, thank you!!! There is another thing you could do for me: my clothes are old and dirty and I need a new #cloak and a new #armor. Please bring them to me and say #clothes.",
				new MultipleActions(actions)
		);
		
		
		npc.add(ConversationStates.QUEST_ITEM_QUESTION, ConversationPhrases.YES_MESSAGES,
				new AndCondition(new QuestInStateCondition(QUEST_SLOT, "start"),
				new NotCondition(
						new AndCondition(
								new PlayerHasItemWithHimCondition("sandwich",3),
								new PlayerHasItemWithHimCondition("beer",3),
								new PlayerHasItemWithHimCondition("wine",3)))),
				ConversationStates.ATTENDING, 
				"I've been around a long time and what's more I am really hungry. You can't trick me.",
				null);
		
		npc.add(ConversationStates.QUEST_ITEM_QUESTION, 
				ConversationPhrases.NO_MESSAGES,
				new QuestInStateCondition(QUEST_SLOT, "start"), 
				ConversationStates.ATTENDING,
				"Pff! Then go away! But be sure, you will not get a reward if you don't bring me the items!", 
				null);
		
		npc.add(ConversationStates.ATTENDING,
				ConversationPhrases.QUEST_MESSAGES, 
				new QuestInStateCondition(QUEST_SLOT, "start"),
				ConversationStates.ATTENDING,
				"I already asked you to bring me some #food!",
				null);
		
		npc.add(ConversationStates.ATTENDING, "cloak",
				new QuestInStateCondition(QUEST_SLOT, 0, "clothes") ,
				ConversationStates.ATTENDING, 
				"I know Wrvil (he lives in Wofol) has a new cloak for me. Just tell him my name.",
				null);
		
		npc.add(ConversationStates.ATTENDING, "armor",
				new QuestInStateCondition(QUEST_SLOT, 0, "clothes") ,
				ConversationStates.ATTENDING, 
				"Mrotho (he lives in Ados) told me he will look for a golden armor for me. Just tell him my name.",
				null);
		
	}
	
	private void getCloak() {
	final SpeakerNPC npc = npcs.get("Wrvil");
	
		npc.add(ConversationStates.ATTENDING, "Phalk",
				new AndCondition(new QuestInStateCondition(QUEST_SLOT, 0, "clothes"),new QuestInStateCondition(QUEST_SLOT, 1, "none")) ,
				ConversationStates.ATTENDING, 
				"Aaah, his cloak... yes, it is ready. But I am still waiting for the #payment!",
				null);
		
		npc.add(ConversationStates.ATTENDING, "payment",
				new AndCondition(new QuestInStateCondition(QUEST_SLOT, 0, "clothes"),new QuestInStateCondition(QUEST_SLOT, 1, "none")),
				ConversationStates.QUEST_ITEM_QUESTION, 
				"Oh yes! it costs 20 steel arrows. Our victims don't bring them back ;) Do you have them?",
				null);	
		
		npc.add(ConversationStates.QUEST_ITEM_QUESTION, 
				ConversationPhrases.NO_MESSAGES,
				new AndCondition(new QuestInStateCondition(QUEST_SLOT, 0, "clothes"),new QuestInStateCondition(QUEST_SLOT, 1, "none")), 
				ConversationStates.ATTENDING,
				"So I can not give you the cloak! First the payment!", 
				null);
		
		final List<ChatAction> actions = new LinkedList<ChatAction>();
		actions.add(new IncreaseXPAction(200));
		actions.add(new DropItemAction("steel arrow",20));
		actions.add(new ChatAction() {
			public void fire(final Player player, final Sentence sentence, final EventRaiser npc) {
				final Item cloak = SingletonRepository.getEntityManager().getItem("dwarf cloak");
				cloak.setInfoString("Phalk");
				cloak.setDescription("You see a brand new dwarf cloak, with the name 'Phalk' sewn into the label by Wrvil.");
				// remember the description
				cloak.setPersistent(true);
				cloak.setBoundTo(player.getName());
				player.equipOrPutOnGround(cloak);
			};
		});
		// the extra parts in the quest state are for wrvil and mrotho not to give them cloaks and armor twice
		actions.add(new SetQuestAction(QUEST_SLOT, 1, "cloak"));
		
		npc.add(ConversationStates.QUEST_ITEM_QUESTION, ConversationPhrases.YES_MESSAGES,
				new AndCondition(
						new AndCondition(new QuestInStateCondition(QUEST_SLOT, 0, "clothes"),new QuestInStateCondition(QUEST_SLOT, 1, "none")),
						new PlayerHasItemWithHimCondition("steel arrow",20)),
				ConversationStates.ATTENDING, 
				"Ok, here you are.",
				new MultipleActions(actions)
		);
		
		
		npc.add(ConversationStates.QUEST_ITEM_QUESTION, ConversationPhrases.YES_MESSAGES,
				new AndCondition(new QuestInStateCondition(QUEST_SLOT, 0, "clothes"),new QuestInStateCondition(QUEST_SLOT, 1, "none"),
				new NotCondition(new PlayerHasItemWithHimCondition("steel arrow",20))),
				ConversationStates.ATTENDING, 
				"Your type are all liars, aren't they? Come back when you have the payment.",
				null);

	}
	
	private void getArmor() {
		final SpeakerNPC npc = npcs.get("Mrotho");
		
		npc.add(ConversationStates.ATTENDING, "Phalk",
				new AndCondition(new QuestInStateCondition(QUEST_SLOT, 0, "clothes"),new QuestInStateCondition(QUEST_SLOT, 2, "none")) ,
				ConversationStates.QUEST_ITEM_QUESTION, 
				"Ooops, his armor...wait.. where is it.. aah here it is. Did he give you the #payment for me too?",
				null);
		
		npc.add(ConversationStates.QUEST_ITEM_QUESTION, "payment",
				new AndCondition(new QuestInStateCondition(QUEST_SLOT, 0, "clothes"),new QuestInStateCondition(QUEST_SLOT, 2, "none")),
				ConversationStates.QUEST_ITEM_QUESTION, 
				"Well.. the armor will cost 20 gold bars. Do you have them?",
				null);	
		
		// incase player goes on to ask something else, accept payment from attending too.
		npc.add(ConversationStates.ATTENDING, "payment",
				new AndCondition(new QuestInStateCondition(QUEST_SLOT, 0, "clothes"),new QuestInStateCondition(QUEST_SLOT, 2, "none")),
				ConversationStates.QUEST_ITEM_QUESTION, 
				"The armor will cost 20 gold bars. Do you have them?",
				null);	
		
		npc.add(ConversationStates.QUEST_ITEM_QUESTION, 
				ConversationPhrases.NO_MESSAGES,
				new AndCondition(new QuestInStateCondition(QUEST_SLOT, 0, "clothes"),new QuestInStateCondition(QUEST_SLOT, 2, "none")), 
				ConversationStates.ATTENDING,
				"Bah! I will not give you the armor without payment!", 
				null);
		
		final List<ChatAction> actions = new LinkedList<ChatAction>();
		actions.add(new IncreaseXPAction(200));
		actions.add(new DropItemAction("gold bar",20));
		actions.add(new ChatAction() {
			public void fire(final Player player, final Sentence sentence, final EventRaiser npc) {
				final Item armor = SingletonRepository.getEntityManager().getItem("golden armor");
				armor.setInfoString("Phalk");
				armor.setDescription("You see a shining golden armor, with the name 'Phalk' inscribed inside.");
				// remember the description
				armor.setPersistent(true);
				armor.setBoundTo(player.getName());
				player.equipOrPutOnGround(armor);
			};
		});
		// the extra parts in the quest state are for wrvil and mrotho not to give them cloaks and armor twice
		actions.add(new SetQuestAction(QUEST_SLOT, 2, "armor"));
		
		npc.add(ConversationStates.QUEST_ITEM_QUESTION, ConversationPhrases.YES_MESSAGES,
				new AndCondition(
						new AndCondition(new QuestInStateCondition(QUEST_SLOT, 0, "clothes"),new QuestInStateCondition(QUEST_SLOT, 2, "none")),
						new PlayerHasItemWithHimCondition("gold bar",20)),
				ConversationStates.ATTENDING, 
				"Ok, here you are.",
				new MultipleActions(actions)
		);
		
		npc.add(ConversationStates.QUEST_ITEM_QUESTION, ConversationPhrases.YES_MESSAGES,
				new AndCondition(new QuestInStateCondition(QUEST_SLOT, 0, "clothes"),new QuestInStateCondition(QUEST_SLOT, 2, "none"),
				new NotCondition(new PlayerHasItemWithHimCondition("gold bar",20))),
				ConversationStates.ATTENDING, 
				"Army disciplinary actions are pretty serious, so don't lie to me.",
				null);
		
	}
		
	
	private void receiveClothes() {
	final SpeakerNPC npc = npcs.get("Phalk");	
	
		final List<ChatAction> actions = new LinkedList<ChatAction>();
		actions.add(new IncreaseXPAction(4000));
		actions.add(new DropInfostringItemAction("golden armor","Phalk"));
		actions.add(new DropInfostringItemAction("dwarf cloak","Phalk"));
		actions.add(new SetQuestAndModifyKarmaAction(QUEST_SLOT, "done", 5.0));	
		actions.add(new EquipItemAction("dwarvish armor", 1, true));
		
		npc.add(ConversationStates.ATTENDING, "clothes",
				new AndCondition(new QuestInStateCondition(QUEST_SLOT, 0, "clothes"),
				new PlayerHasInfostringItemWithHimCondition("golden armor","Phalk"),
				new PlayerHasInfostringItemWithHimCondition("dwarf cloak","Phalk")),
				ConversationStates.ATTENDING, 
				"Oh yeah! Thank you so much! Payment?? Erm... *cough* I will give you my old armor as a reward.",
				new MultipleActions(actions));

		npc.add(ConversationStates.ATTENDING, "clothes",
				new AndCondition(new QuestInStateCondition(QUEST_SLOT, 0, "clothes"),
				new NotCondition(
						new AndCondition(
								new PlayerHasInfostringItemWithHimCondition("golden armor","Phalk"),
								new PlayerHasInfostringItemWithHimCondition("dwarf cloak","Phalk")))),
				ConversationStates.ATTENDING, 
				"Hm, I want the special golden #armor from Mrotho and the dwarf #cloak from Wrvil. Tell them my name and they will give you what they made me.",
				null);
		
		npc.add(ConversationStates.ATTENDING,
				ConversationPhrases.QUEST_MESSAGES, 
				new QuestInStateCondition(QUEST_SLOT, 0, "clothes"),
				ConversationStates.ATTENDING,
				"I am waiting for you to bring me new #clothes from Wrvil and Mrotho.",
				null);
	}
	
	@Override
	public void addToWorld() {
		super.addToWorld();

		askForFood();
		receiveFood();
		getCloak();
		getArmor();
		receiveClothes();
	}
	@Override
	public String getName() {
		return "SuppliesForPhalk";
	}
	
	@Override
	public int getMinLevel() {
		return 30;
	}
}
 
