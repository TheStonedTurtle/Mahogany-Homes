package thestonedturtle.mahoganyhomes;

import com.google.common.collect.ImmutableMap;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Set;

import static thestonedturtle.mahoganyhomes.HotspotType.*;

public enum HomeHotspot {

	// East Ardy
	JESS(new ObjectHotspot(40171, B2),
			new ObjectHotspot(40172, B2),
			new ObjectHotspot(40173, B2),
			new ObjectHotspot(40174, B2),
			new ObjectHotspot(40175, B3),
			new ObjectHotspot(40176, B3),
			new ObjectHotspot(40177, RP),
			new ObjectHotspot(40299, SB)),
	NOELLA(new ObjectHotspot(40156, B2),
			new ObjectHotspot(40157, B2),
			new ObjectHotspot(40158, RP),
			new ObjectHotspot(40159, RP),
			new ObjectHotspot(40160, B2),
			new ObjectHotspot(40161, B3),
			new ObjectHotspot(40162, B3),
			new ObjectHotspot(40163, RP)),
	ROSS(new ObjectHotspot(40164, SB),
			new ObjectHotspot(40165, B2),
			new ObjectHotspot(40166, B2),
			new ObjectHotspot(40167, B3),
			new ObjectHotspot(40168, RP),
			new ObjectHotspot(40169, B2),
			new ObjectHotspot(40170, RP)),

	// Falador
	LARRY(new ObjectHotspot(40297, SB),
			new ObjectHotspot(40095, B2),
			new ObjectHotspot(40096, B2),
			new ObjectHotspot(40097, B3),
			new ObjectHotspot(40298, RP),
			new ObjectHotspot(40098, B3),
			new ObjectHotspot(40099, RP)),
	NORMAN(new ObjectHotspot(40296, SB),
			new ObjectHotspot(40089, RP),
			new ObjectHotspot(40090, B3),
			new ObjectHotspot(40091, B3),
			new ObjectHotspot(40092, B2),
			new ObjectHotspot(40093, B2),
			new ObjectHotspot(40094, B2)),
	TAU(new ObjectHotspot(40083, SB),
			new ObjectHotspot(40084, B3),
			new ObjectHotspot(40085, B3),
			new ObjectHotspot(40086, B2),
			new ObjectHotspot(40087, B2),
			new ObjectHotspot(40088, B2),
			new ObjectHotspot(40295, RP)),

	// Hosidius
	BARBARA(new ObjectHotspot(40011, RP),
			new ObjectHotspot(40293, SB),
			new ObjectHotspot(40012, B3),
			new ObjectHotspot(40294, B2),
			new ObjectHotspot(40013, B2),
			new ObjectHotspot(40014, B1),
			new ObjectHotspot(40015, B1)),
	LEELA(new ObjectHotspot(40007, B2),
			new ObjectHotspot(40008, B2),
			new ObjectHotspot(40290, SB),
			new ObjectHotspot(40291, B3),
			new ObjectHotspot(40009, B3),
			new ObjectHotspot(40010, RP),
			new ObjectHotspot(40292, B2)),
	MARIAH(new ObjectHotspot(40002, B3),
			new ObjectHotspot(40287, SB),
			new ObjectHotspot(40003, B2),
			new ObjectHotspot(40288, B2),
			new ObjectHotspot(40004, B2),
			new ObjectHotspot(40005, B2),
			new ObjectHotspot(40006, B2),
			new ObjectHotspot(40289, RP)),


	// Varrock
	BOB(new ObjectHotspot(39981, B4),
			new ObjectHotspot(39982, RP),
			new ObjectHotspot(39983, B2),
			new ObjectHotspot(39984, B2),
			new ObjectHotspot(39985, B2),
			new ObjectHotspot(39986, B2),
			new ObjectHotspot(39987, B2),
			new ObjectHotspot(39988, B2)),
	JEFF(new ObjectHotspot(39989, B3),
			new ObjectHotspot(39990, B2),
			new ObjectHotspot(39991, B2),
			new ObjectHotspot(39992, B3),
			new ObjectHotspot(39993, B2),
			new ObjectHotspot(39994, B2),
			new ObjectHotspot(39995, RP),
			new ObjectHotspot(39996, B1)),
	SARAH(new ObjectHotspot(39997, B3),
			new ObjectHotspot(39998, B2),
			new ObjectHotspot(39999, B2),
			new ObjectHotspot(40000, B2),
			new ObjectHotspot(40286, SB),
			new ObjectHotspot(40001, B2));


	@AllArgsConstructor
	@Getter
	private static class ObjectHotspot {
		int objectId;
		HotspotType hotspotType;
	}

	final ImmutableMap<Integer, ObjectHotspot> varBitToObjectHotspotMap;

	HomeHotspot(ObjectHotspot... objectHotspots) {
		final ImmutableMap.Builder<Integer, ObjectHotspot> objects = new ImmutableMap.Builder<>();
		int varBit = 10554;
		for (final ObjectHotspot objectHotspot : objectHotspots) {
			objects.put(varBit++, objectHotspot);
		}
		varBitToObjectHotspotMap = objects.build();
	}

	public ObjectHotspot getObjectHotspot(int varbit) {
		return varBitToObjectHotspotMap.get(varbit);
	}

	public RequiredMaterials getRequiredMaterials(Set<Integer> varbits) {
		int numOfPlanks = 0;
		int numOfSteelBars = 0;
		for (int varbit : varbits) {
			ObjectHotspot objectHotspot = getObjectHotspot(varbit);
			if (objectHotspot == null) return null;
			Material material = objectHotspot.getHotspotType().getMaterial();
			int numOfMaterial = objectHotspot.getHotspotType().getNumOfMaterial();
			if (Material.PLANK.equals(material)) numOfPlanks += numOfMaterial;
			else if (Material.STEEL_BAR.equals(material)) numOfSteelBars += numOfMaterial;
		}
		return new RequiredMaterials(numOfPlanks, numOfPlanks, numOfSteelBars, numOfSteelBars);
	}

}
