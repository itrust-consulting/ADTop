package lu.itrust.adtop.tools;

import java.io.IOException;
import java.util.List;

import lu.itrust.adtop.model.dl.DefenceLibrary;
import lu.itrust.adtop.model.matrix.AssociationMatrix;
import lu.itrust.adtop.model.measure.MeasureContainer;
import lu.itrust.adtop.model.tree.AttackDefenceTree;
import lu.itrust.adtop.model.tree.AttackDefenceTreeGenerator;
import lu.itrust.adtop.tools.API.ConnectorAPIImpl;

/**
 * Pack contain one instance of all variables
 * 
 * @author ersagun
 *
 */

public class Pack {

	/**
	 * Attack tree imported from ADT file
	 */
	// private AttackTree attackTree ;

	/**
	 * Attack-Defence tree imported from ADT file
	 */
	private AttackDefenceTree attackDefenceTree;

	private AttackDefenceTree unTouchedDefenceTree;

	/**
	 * Measures imported from JSON file
	 */
	private MeasureContainer measureContainer;

	/**
	 * Defence-Library imported from DL file
	 */
	private DefenceLibrary defenceLibrary;

	/**
	 * Defence-Library imported from DL file
	 */
	private AssociationMatrix associationMatrix;

	private AttackDefenceTreeGenerator attackDefenceTreeGenerator;

	private ConnectorAPIImpl connectorAPIImpl;

	private double initialProbability = 0;

	public AttackDefenceTreeGenerator getAttackDefenceTreeGenerator() {
		return attackDefenceTreeGenerator;
	}

	public void setAttackDefenceTreeGenerator(AttackDefenceTreeGenerator attackDefenceTreeGenerator) {
		this.attackDefenceTreeGenerator = attackDefenceTreeGenerator;
	}

	@Override
	public String toString() {
		return "Pack [measureContainer=" + measureContainer + ", defenceLibrary=" + defenceLibrary + ", associationMatrix=" + associationMatrix + ", attackDefenceTree="
				+ attackDefenceTree + "]";
	}

	public Pack() {

		/**
		 * Attributs declared on the Controller
		 */

		/**
		 * Attack tree imported from ADT file
		 */
		// attackTree = new AttackTree();

		/**
		 * Attack-Defence tree imported from ADT file
		 */
		attackDefenceTree = new AttackDefenceTree();

		/**
		 * Measures imported from JSON file
		 */
		measureContainer = new MeasureContainer();

		/**
		 * Defence-Library imported from DL file
		 */
		defenceLibrary = new DefenceLibrary();

		/**
		 * Defence-Library imported from DL file
		 */
		associationMatrix = new AssociationMatrix();

	}

	public void initializeAttackDefenceTreeGenerator(int option, int nbTabs, List<Double> l) throws IOException {
		if(this.attackDefenceTreeGenerator!=null)
			this.attackDefenceTreeGenerator.clear();
		this.attackDefenceTreeGenerator = new AttackDefenceTreeGenerator(this, option, nbTabs, l);
	}

	/** GETTERS AND SETTERS **/

	public AttackDefenceTree getAttackDefenceTree() {
		return attackDefenceTree;
	}

	public void setAttackDefenceTree(AttackDefenceTree adt) {
		this.attackDefenceTree = adt;
	}

	public MeasureContainer getMeasureContainer() {
		return measureContainer;
	}

	public void setMeasureContainer(MeasureContainer measures) {
		this.measureContainer = measures;
	}

	public DefenceLibrary getDefenceLibrary() {
		return defenceLibrary;
	}

	public void setDefenceLibrary(DefenceLibrary defenceLibrary) {
		this.defenceLibrary = defenceLibrary;
	}

	public AssociationMatrix getAssociationMatrix() {
		return associationMatrix;
	}

	public void setAssociationMatrix(AssociationMatrix matrix) {
		this.associationMatrix = matrix;
	}

	public ConnectorAPIImpl getConnectorAPI() {
		return connectorAPIImpl;
	}

	public void setConnectorAPI(ConnectorAPIImpl connectorAPIImpl) {
		this.connectorAPIImpl = connectorAPIImpl;
	}

	/**
	 * @return the copy of unTouchedDefenceTree
	 */
	public AttackDefenceTree getUnTouchedDefenceTree() {
		return unTouchedDefenceTree == null ? null : unTouchedDefenceTree.clone();
	}

	/**
	 * @param unTouchedDefenceTree
	 *            the unTouchedDefenceTree to set
	 */
	public void setUnTouchedDefenceTree(AttackDefenceTree unTouchedDefenceTree) {
		if (this.unTouchedDefenceTree != null)
			this.unTouchedDefenceTree.clear();
		this.unTouchedDefenceTree = unTouchedDefenceTree;
	}

	public double getALE() {
		return this.initialProbability * this.measureContainer.getImpact();
	}

	public void updateIntialProbability() {
		if (this.attackDefenceTree == null || this.attackDefenceTree.getRoot() == null)
			return;
		setInitialProbability(this.attackDefenceTree.getRoot().getSuccessValue());
	}

	/**
	 * @return the initialProbability
	 */
	public double getInitialProbability() {
		return initialProbability;
	}

	/**
	 * @param initialProbability
	 *            the initialProbability to set
	 */
	protected void setInitialProbability(double initialProbability) {
		this.initialProbability = initialProbability;
	}

}
