package lu.itrust.adtop.model.tree;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import lu.itrust.adtop.exception.ADException;
import lu.itrust.adtop.model.measure.Measure;
import lu.itrust.adtop.model.measure.Standard;
import lu.itrust.adtop.tools.Pack;

public class AttackDefenceTreeGenerator {

	private AttackDefenceTree attackDefenceTreeInitial;

	private List<AttackDefenceTree> attackDefenceTrees;
	private double impact;
	private Map<String, Map<Measure, List<String>>> defenceAttacks;
	private List<Double> maxSucMaxCost;

	public final static Comparator<? super AttackDefenceTree> COMPARATOR = (o1, o2) -> {
		return Double.compare(o1.getRosiValue(), o2.getRosiValue());
	};

	public List<Double> getMaxSucMaxCost() {
		return maxSucMaxCost;
	}

	public void setMaxSucMaxCost(ArrayList<Double> maxSucMaxCost) {
		this.maxSucMaxCost = maxSucMaxCost;
	}

	// Example : A,AB,ABC,AC,BC...
	private List<List<String>> allCombinaisons;

	public AttackDefenceTreeGenerator(Pack p, int option, int nbTabs, List<Double> l) throws IOException {
		this.attackDefenceTreeInitial = p.getUnTouchedDefenceTree();
		this.maxSucMaxCost = l;
		this.attackDefenceTrees = new LinkedList<AttackDefenceTree>();
		this.impact = p.getMeasureContainer().getImpact();
		this.defenceAttacks = p.getAssociationMatrix().getMatrixMeasureAttack();
		this.allCombinaisons = findAllCombination(p.getAssociationMatrix().getAllMeasures());
		try {
			if (option == 0) {
				generateAllAttackDefenceTreesPrime(p, nbTabs);
			} else if (option == 1) {
				generateAllAttackDefenceTrees(p, nbTabs);
			}
		} catch (ADException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public AttackDefenceTreeGenerator() {

	}

	public void printAllCombinaisons() {
		allCombinaisons.stream().forEach(list -> System.out.println(list.toString()));
	}

	public void printForDefenceAttacks() {
		for (Entry<String, Map<Measure, List<String>>> entry : defenceAttacks.entrySet()) {
			System.out.println("Measure : " + entry.getValue().toString());
			for (Entry<Measure, List<String>> entry2 : entry.getValue().entrySet()) {
				System.out.print("Attacks : " + entry2.getValue().toString() + " ");
			}
		}
	}

	public void generateAllAttackDefenceTrees(Pack p, int nbTabs) {

		Boolean maxCost = false;
		Boolean maxSucP = false;
		if (!this.maxSucMaxCost.isEmpty() && this.maxSucMaxCost.size() >= 2) {
			if (this.maxSucMaxCost.get(0) > 0)
				maxCost = true;

			if (this.maxSucMaxCost.get(1) > 0)
				maxSucP = true;
		}

		double min = 0;

		AttackDefenceTree adtempty = p.getUnTouchedDefenceTree();

		if (!adtempty.measures.isEmpty()) {
			adtempty.generateAttackDefenceValues();
			adtempty.evaluateAttackDefenceTree();
			adtempty.calculSetRosiValue(p);
			attackDefenceTrees.add(adtempty);
		}

		Iterator<List<String>> iterator = allCombinaisons.iterator();
		while (iterator.hasNext()) {
			List<String> oneCombination = iterator.next();
			// eval with cost
			if (maxCost) {
				if (this.maxSucMaxCost.get(0) >= totalCostForOneCombinaison(oneCombination, p))
					computeForTree(oneCombination, p, min, maxSucP, nbTabs);
			} else
				computeForTree(oneCombination, p, min, maxSucP, nbTabs);
			iterator.remove();
		}

	}

	public void computeForTree(List<String> oneCombination, Pack p, double min, boolean maxSucP, int nbTabs) {
		Map<Measure, List<String>> positionsForMeasure = forAttacksFindAllPositions(oneCombination, p);
		List<Map<Measure, String>> allCombi = new ArrayList<>();
		allCombi.addAll(calculateRecursively(positionsForMeasure));

		for (Map<Measure, String> combi : allCombi) {

			AttackDefenceTree adt = p.getUnTouchedDefenceTree();

			adt.generateAttackDefenceValues();

			for (Entry<Measure, String> aCombis : combi.entrySet()) {
				adt.addDefenceToAttackDefenceTree(aCombis.getKey(), aCombis.getValue(), p);
				adt.generateAttackDefenceValues();
				adt.evaluateAttackDefenceTree();
				adt.calculSetRosiValue(p);
				if (min == 0) {
					min = adt.getRoot().getSuccessValue();
				} else if (min > adt.getRoot().getSuccessValue()) {
					min = adt.getRoot().getSuccessValue();
				}
			}

			if (maxSucP) {
				if (adt.getRoot().getSuccessValue() <= this.maxSucMaxCost.get(1)) {
					addToTree(adt, nbTabs);
				}
			} else {
				addToTree(adt, nbTabs);
			}
		}

		attackDefenceTrees.sort(COMPARATOR);
	}

	public void addToTree(AttackDefenceTree adt, int nbTabs) {
		if (attackDefenceTrees.isEmpty()) {
			attackDefenceTrees.add(adt);
		} else {
			if (attackDefenceTrees.size() < nbTabs) {
				attackDefenceTrees.add(adt);
			} else {

				attackDefenceTrees.sort(COMPARATOR);

				if (adt.getRosiValue() < attackDefenceTrees.get(attackDefenceTrees.size() - 1).getRosiValue()) {
					attackDefenceTrees.remove(attackDefenceTrees.size() - 1).destroy();
					attackDefenceTrees.add(adt);
				}

			}
		}
	}

	public void generateAllAttackDefenceTreesPrime(Pack p, int nbTabs) throws IOException {
		Boolean maxCost = !this.maxSucMaxCost.isEmpty() ? this.maxSucMaxCost.get(0) > 0 : false;
		Boolean maxSucP = this.maxSucMaxCost.size() > 1 ? this.maxSucMaxCost.get(1) > 0 : false;
		AttackDefenceTree adtempty = p.getUnTouchedDefenceTree();
		if (!adtempty.measures.isEmpty()) {
			adtempty.generateAttackDefenceValues();
			adtempty.evaluateAttackDefenceTree();
			adtempty.calculSetRosiValue(p);
			attackDefenceTrees.add(adtempty);
		}
		Iterator<List<String>> iterator = allCombinaisons.iterator();
		while (iterator.hasNext()) {
			List<String> oneCombination = iterator.next();
			if (maxCost) {
				if (this.maxSucMaxCost.get(0) >= totalCostForOneCombinaison(oneCombination, p))
					computeForTreePrime(oneCombination, p, maxSucP);
			} else
				computeForTreePrime(oneCombination, p, maxSucP);
			iterator.remove();
		}
		attackDefenceTrees.sort(COMPARATOR);

	}

	public void computeForTreePrime(List<String> oneCombination, Pack p, boolean maxSucP) {
		Map<Measure, List<String>> positionsForMeasure = forAttacksFindAllPositions(oneCombination, p);
		AttackDefenceTree adt = p.getUnTouchedDefenceTree();
		adt.generateAttackDefenceValues();
		int setNumber = adt.getSetNumber();
		p.getAttackDefenceTree().setSetNumber(setNumber);
		for (Entry<Measure, List<String>> oneCombi : positionsForMeasure.entrySet()) {
			for (String attackToTake : oneCombi.getValue())
				adt.addDefenceToAttackDefenceTree(oneCombi.getKey(), attackToTake, p);
		}
		adt.generateAttackDefenceValues();
		adt.evaluateAttackDefenceTree();
		adt.calculSetRosiValue(p);
		if (maxSucP) {
			if (adt.getRoot().getSuccessValue() <= this.maxSucMaxCost.get(1))
				attackDefenceTrees.add(adt);
		} else
			attackDefenceTrees.add(adt);
	}

	public double totalCostForOneCombinaison(List<String> measureNames, Pack p) {
		Standard standard = p.getMeasureContainer().getStandards().get(0);
		return measureNames.stream().mapToDouble(name -> standard.getMeasureCostWithName(name)).sum();
	}

	public List<Map<Measure, String>> calculateRecursively(Map<Measure, List<String>> listAttackMeasure) {
		// trivial
		if (listAttackMeasure.isEmpty()) {
			List<Map<Measure, String>> emptyList = new ArrayList<>();
			emptyList.add(new HashMap<Measure, String>());
			return emptyList;
		}

		Measure firstElement = null;
		List<String> firstElementList = null;
		for (Entry<Measure, List<String>> firstElement1 : listAttackMeasure.entrySet()) {
			firstElement = firstElement1.getKey();
			firstElementList = firstElement1.getValue();
		}
		List<String> currentMeasurePlaces = listAttackMeasure.get(firstElement);

		Map<Measure, List<String>> listAttackMeasureCloned = new HashMap<>();
		listAttackMeasureCloned.putAll(listAttackMeasure);
		listAttackMeasureCloned.remove(firstElement, firstElementList);
		List<Map<Measure, String>> l = calculateRecursively(listAttackMeasureCloned);
		List<Map<Measure, String>> listToReturn = new ArrayList<>();
		for (String attackPlace : currentMeasurePlaces) {
			for (Map<Measure, String> eachCombinaison : l) {
				Map<Measure, String> allCombinaisons = new HashMap<>();
				allCombinaisons.put(firstElement, attackPlace);
				allCombinaisons.putAll(eachCombinaison);
				listToReturn.add(allCombinaisons);
			}

		}
		return listToReturn;
	}

	public static Map<Measure, List<String>> forAttacksFindAllPositions(Collection<String> measures, Pack p) {
		return measures.stream().filter(measure -> p.getAssociationMatrix().getMatrixMeasureAttack().containsKey(measure))
				.flatMap(measure -> p.getAssociationMatrix().getMatrixMeasureAttack().get(measure).entrySet().stream()).collect(Collectors.toMap(Entry::getKey, Entry::getValue));
	}

	public AttackDefenceTree getAttackDefenceTreeInitial() {
		return attackDefenceTreeInitial;
	}

	public void setAttackDefenceTreeInitial(AttackDefenceTree attackDefenceTreeInitial) {
		this.attackDefenceTreeInitial = attackDefenceTreeInitial;
	}

	public double getImpact() {
		return impact;
	}

	public void setImpact(double impact) {
		this.impact = impact;
	}

	public Map<String, Map<Measure, List<String>>> getDefenceAttacks() {
		return defenceAttacks;
	}

	public void setDefenceAttacks(Map<String, Map<Measure, List<String>>> defenceAttacks) {
		this.defenceAttacks = defenceAttacks;
	}

	public List<List<String>> getAllCombinaisons() {
		return allCombinaisons;
	}

	public void setAllCombinaisons(List<List<String>> allCombinaisons) {
		this.allCombinaisons = allCombinaisons;
	}

	public static List<List<String>> findAllCombination(List<String> l) {
		List<List<String>> powerSet = new LinkedList<List<String>>();

		for (int i = 1; i <= l.size(); i++)
			powerSet.addAll(combination(l, i));

		return powerSet;
	}

	public static <T> List<List<T>> combination(List<T> values, int size) {

		if (0 == size) {
			return Collections.singletonList(Collections.<T>emptyList());
		}

		if (values.isEmpty()) {
			return Collections.emptyList();
		}

		List<List<T>> combination = new LinkedList<List<T>>();

		T actual = values.iterator().next();

		List<T> subSet = new LinkedList<T>(values);
		subSet.remove(actual);

		List<List<T>> subSetCombination = combination(subSet, size - 1);

		for (List<T> set : subSetCombination) {

			List<T> newSet = new LinkedList<T>(set);
			newSet.add(0, actual);

			combination.add(newSet);
		}

		combination.addAll(combination(subSet, size));

		return combination;
	}

	public static void combinationForPositions(ArrayList<Integer> A, int k, ArrayList<ArrayList<Integer>> list, int sizeMax) {
		if (k == sizeMax - 1) {
			A.set(k, 0);
			toString(A);
			list.add(A);

			A.set(k, 1);
			toString(A);
			list.add(A);
			return;
		}

		A.set(k, 0);
		combinationForPositions(A, k + 1, list, sizeMax);
		A.set(k, 1);
		combinationForPositions(A, k + 1, list, sizeMax);
	}

	public static int getMaxSize(Map<String, List<String>> defencesAttacks) {
		int size = 0;
		for (Entry<String, List<String>> entry : defencesAttacks.entrySet()) {
			List<String> attacksForDefences = entry.getValue();
			if (size < attacksForDefences.size()) {
				size = attacksForDefences.size();
			}
		}
		return size;
	}

	public static List<String> getOneRank(Map<String, List<String>> defencesAttacks, int i) {
		List<String> toReturn = new ArrayList<String>();
		for (Entry<String, List<String>> entry : defencesAttacks.entrySet()) {
			String defence = entry.getKey();
			List<String> attacksForDefences = entry.getValue();
			if (attacksForDefences.get(i) != null) {
				toReturn.add(defence + "$*$" + attacksForDefences.get(i));
			}
		}
		return toReturn;
	}

	public static <E> void toString(ArrayList<E> A) {
		for (int i = 0; i < A.size(); i++) {
			System.out.print(A.get(i) + " ");
		}
		System.out.println("\n");
	}

	public List<AttackDefenceTree> getAttackDefenceTrees() {
		return attackDefenceTrees;
	}

	public void setAttackDefenceTrees(List<AttackDefenceTree> attackDefenceTrees) {
		this.attackDefenceTrees = attackDefenceTrees;
	}

	public void clear() {
		attackDefenceTreeInitial.clear();
		attackDefenceTrees.forEach(AttackDefenceTree::clear);
		attackDefenceTrees.clear();
		defenceAttacks = null;
		maxSucMaxCost.clear();
	}

}
