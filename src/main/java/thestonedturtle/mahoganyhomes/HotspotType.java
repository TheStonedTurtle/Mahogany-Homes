package thestonedturtle.mahoganyhomes;

import lombok.AllArgsConstructor;
import lombok.Getter;

import static thestonedturtle.mahoganyhomes.Material.PLANK;
import static thestonedturtle.mahoganyhomes.Material.STEEL_BAR;

@AllArgsConstructor
@Getter
public enum HotspotType {

	//Remove & Build Furniture (1 plank)
	B1(PLANK, 1),
	//Remove & Build Furniture (2 plank)
	B2(PLANK, 2),
	//Remove & Build Furniture (3 plank)
	B3(PLANK, 3),
	//Remove & Build Furniture (4 plank)
	B4(PLANK, 4),
	//Repair Furniture (1 plank)
	RP(PLANK, 1),
	//Repair Furniture (1 steel bar)
	SB(STEEL_BAR, 1);

	private final Material material;
	private final int numOfMaterial;

}
