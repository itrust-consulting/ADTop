package adtop;

import lu.itrust.adtop.tools.Pack;

public class AttackDefenceTreeTest {
	Pack pack;

	/**
	@Before
	public void initGenerationAttackDefenceTreeTest() {
		pack = new Pack();
	}

	@Test
	public void GenerationAttackDefenceTreeTest() {
		try {
			// import attack tree
			pack.getAt().importFromXML(getClass().getResource("/ATree.xml").getPath());
			pack.getAt().generateListValues();
		
			// evaluate-it
			pack.getAt().evaluateAT();
		
			// import measures
			pack.getMeasures().importFromJSONFile(getClass().getResource("/extractTS.json").getPath());
			// import Association matrix
			pack.getMatrix().importMatrixFromXLS("C:/Users/ersagun/Desktop/val/Association matrix_full_countermeasures (duplicates).xls", pack);
			pack.setAdt(new AttackDefenceTree(pack.getAt()));
			pack.getAdt().generateFromAT(pack);

			/**
			 * //generate attack defence tree if (!pack.getAdt().isEmpty()) {
			 * try { checkCorrespondanceScenario(); } catch
			 * (CheckCorrespondanceScenarioException e1) { //showWarning(
			 * "Attack-Defence Tree generation",e1.getMessage());
			 * 
			 * } //generateAndReplaceTree(pack.getAdt());
			 * 
			 * } else //showInformation("Generation Attack-Defence Tree", //
			 * "Attack-defence tree cannot be generated. Please import an attack tree and an association matrix."
			 * );
			 * 
			 *
		
			
			// evalute adt
			pack.getAdt().getRoot().evaluateADT();
			pack.getAdt().evaluateRoot();
			pack.getAdt().setEvaluated(true);
		
		} catch (Exception e) {
			// showWarning("Import association matrix problem", e.getMessage());
			//e.printStackTrace();
		}
		assertTrue(0.0046910957031250046==pack.getAdt().getRoot().getSuccessProbability());
	}
	**/

}
