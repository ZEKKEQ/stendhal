package games.stendhal.server.entity.npc.fsm;

import static games.stendhal.server.entity.npc.ConversationStates.ATTENDING;
import static games.stendhal.server.entity.npc.ConversationStates.IDLE;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static utilities.SpeakerNPCTestHelper.getReply;
import games.stendhal.server.entity.Entity;
import games.stendhal.server.entity.npc.ChatAction;
import games.stendhal.server.entity.npc.ChatCondition;
import games.stendhal.server.entity.npc.ConversationStates;
import games.stendhal.server.entity.npc.EventRaiser;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.npc.parser.Sentence;
import games.stendhal.server.entity.player.Player;
import marauroa.common.Log4J;

import org.junit.BeforeClass;
import org.junit.Test;

import utilities.PlayerTestHelper;

public class EngineTest {

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		Log4J.init();
		PlayerTestHelper.generatePlayerRPClasses();
		PlayerTestHelper.generateNPCRPClasses();
	}


	/**
	 * Tests for engine.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void testEngine() {
		new Engine(null);
	}

	/**
	 * Tests for addSingleStringEmptyCondition.
	 */
	@Test
	public void testAddSingleStringEmptyCondition() {
		final Engine en = new Engine(new SpeakerNPC("bob"));
		ConversationStates state = IDLE;
		
		final String triggers = "boo";

		final ConversationStates nextState = ConversationStates.ATTENDING;
		final String reply = "huch";
		final ChatAction action = new ChatAction() {
			public void fire(final Player player, final Sentence sentence, final EventRaiser npc) {
				assertEquals("boo", sentence.getTriggerExpression().getNormalized());
			}
		};
		en.add(state, triggers, null, nextState, reply, action);
		final Player pete = PlayerTestHelper.createPlayer("player");
		en.step(pete, "boo");
		assertEquals(nextState, en.getCurrentState());
	}
	
	/**
	 * Tests for addBothActionsNull.
	 */
	@Test
	public void testaddBothActionsNull() throws Exception {
		final Engine en = new Engine(new SpeakerNPC("bob"));
		assertTrue(en.getTransitions().isEmpty());
		en.add(IDLE, null, null, null, IDLE, null, null);
		assertThat(en.getTransitions().size(), is(1));
		en.add(IDLE, null, null, null, IDLE, null, null);
		assertThat(en.getTransitions().size(), is(1));
	}
	
	/**
	 * Tests for addExistingActionNull.
	 */
	@Test
	public void testaddExistingActionNull() throws Exception {
		final Engine en = new Engine(new SpeakerNPC("bob"));
		
		en.add(IDLE, null, null, null, IDLE, null, null);
		assertThat(en.getTransitions().size(), is(1));
		en.add(IDLE, null, null, null, IDLE, null, new ChatAction() {

			public void fire(final Player player, final Sentence sentence, final EventRaiser npc) {
				// empty method
			}
		});
		assertThat(en.getTransitions().size(), is(2));
		
	}
	
	/**
	 * Tests for addnewNullAction.
	 */
	@Test
	public void testaddnewNullAction() throws Exception {
		final Engine en = new Engine(new SpeakerNPC("bob"));
		
		
		en.add(IDLE, null, null, null, IDLE, null, new ChatAction() {

			public void fire(final Player player, final Sentence sentence, final EventRaiser npc) {
				// empty method
			}
		});
		assertThat(en.getTransitions().size(), is(1));
		en.add(IDLE, null, null, null, IDLE, null, null);
		
		assertThat(en.getTransitions().size(), is(2));
		
	}
	
	/**
	 * Tests for addSameAction.
	 */
	@Test
	public void testaddSameAction() throws Exception {
		final Engine en = new Engine(new SpeakerNPC("bob"));
		ChatAction chatAction = new ChatAction() {

			public void fire(final Player player, final Sentence sentence, final EventRaiser npc) {
				// empty method
			}
		};
		en.add(IDLE, null, null, null, IDLE, null, chatAction);
		assertThat(en.getTransitions().size(), is(1));
		
		en.add(IDLE, null, null, null, IDLE, null, chatAction);
		assertThat(en.getTransitions().size(), is(1));
		
	}
	
	/**
	 * Tests for addNotSameAction.
	 */
	@Test
	public void testaddNotSameAction() throws Exception {
		final Engine en = new Engine(new SpeakerNPC("bob"));
		ChatAction chatAction1 = new ChatAction() {

			public void fire(final Player player, final Sentence sentence, final EventRaiser npc) {
				// empty method
			}
		};
		ChatAction chatAction2 = new ChatAction() {

			public void fire(final Player player, final Sentence sentence, final EventRaiser npc) {
				// empty method
			}
		};
		en.add(IDLE, null, null, null, IDLE, null, chatAction1);
		assertThat(en.getTransitions().size(), is(1));
		
		en.add(IDLE, null, null, null, IDLE, null, chatAction2);
		assertThat(en.getTransitions().size(), is(2));
		
	}
	

	/**
	 * Tests for addSingleStringValidCondition.
	 */
	@Test
	public void testAddSingleStringValidCondition() {
		final SpeakerNPC bob = new SpeakerNPC("bob");

		final Engine en = new Engine(bob);
		ConversationStates state = IDLE;
		ConversationStates nextState = ATTENDING;
		
		final String triggers = "boo";

		final ChatCondition cc = new ChatCondition() {
			public boolean fire(final Player player, final Sentence sentence, final Entity npc) {
				assertEquals(triggers, sentence.getTriggerExpression().getNormalized());
				return true;
			}
		};

		
		final String reply = "huch";
		final ChatAction action = new ChatAction() {
			public void fire(final Player player, final Sentence sentence, final EventRaiser npc) {
				assertEquals(triggers, sentence.getTriggerExpression().getNormalized());
			}
		};
		en.add(state, triggers, cc, nextState, reply, action);
		final Player pete = PlayerTestHelper.createPlayer("player");
		en.step(pete, triggers);
		assertEquals(nextState, en.getCurrentState());
		assertEquals(reply, getReply(bob));
	}

}
