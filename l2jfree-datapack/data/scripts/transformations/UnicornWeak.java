package transformations;

import com.l2jfree.gameserver.instancemanager.TransformationManager;
import com.l2jfree.gameserver.model.L2DefaultTransformation;
import com.l2jfree.gameserver.model.actor.instance.L2PcInstance;

/**
 * Description: <br>
 * This will handle the transformation, giving the skills, and removing them, when the player logs out and is transformed these skills
 * do not save. 
 * When the player logs back in, there will be a call from the enterworld packet that will add all their skills.
 * The enterworld packet will transform a player.
 * 
 * @author durgus
 *
 */
public class UnicornWeak extends L2DefaultTransformation
{
	public UnicornWeak()
	{
		// id, colRadius, colHeight
		super(206, 8.0, 25.5);
	}

	public void transformedSkills(L2PcInstance player)
	{
		// Horn of Doom
		addSkill(player, 563, 2);
		// Gravity Control
		addSkill(player, 564, 2);
		// Horn Assault
		addSkill(player, 565, 2);
		// Light of Heal
		addSkill(player, 567, 2);
	}

	public void removeSkills(L2PcInstance player)
	{
		// Horn of Doom
		removeSkill(player, 563);
		// Gravity Control
		removeSkill(player, 564);
		// Horn Assault
		removeSkill(player, 565);
		// Light of Heal
		removeSkill(player, 567);
	}

	public static void main(String[] args)
	{
		TransformationManager.getInstance().registerTransformation(new UnicornWeak());
	}
}
