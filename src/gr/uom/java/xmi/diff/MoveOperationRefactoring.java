package gr.uom.java.xmi.diff;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.refactoringminer.api.Refactoring;
import org.refactoringminer.api.RefactoringType;

import gr.uom.java.xmi.UMLOperation;
import gr.uom.java.xmi.decomposition.UMLOperationBodyMapper;
import gr.uom.java.xmi.decomposition.replacement.Replacement;

public class MoveOperationRefactoring implements Refactoring {
	protected final UMLOperation originalOperation;
	protected final UMLOperation movedOperation;
	private final Set<Replacement> replacements;
	private UMLOperationBodyMapper bodyMapper;

	public MoveOperationRefactoring(UMLOperationBodyMapper bodyMapper) {
		this.bodyMapper = bodyMapper;
		this.originalOperation = bodyMapper.getOperation1();
		this.movedOperation = bodyMapper.getOperation2();
		this.replacements = bodyMapper.getReplacements();
	}

	public MoveOperationRefactoring(UMLOperation originalOperation, UMLOperation movedOperation) {
		this.originalOperation = originalOperation;
		this.movedOperation = movedOperation;
		this.replacements = new LinkedHashSet<>();
	}

	public String toString() {
		return getName() + "\t" +
				originalOperation +
				" from class " +
				originalOperation.getClassName() +
				" to " +
				movedOperation +
				" from class " +
				movedOperation.getClassName();
	}

	public String getName() {
		return this.getRefactoringType().getDisplayName();
	}

	public RefactoringType getRefactoringType() {
		if(!originalOperation.getName().equals(movedOperation.getName())) {
			return RefactoringType.MOVE_AND_RENAME_OPERATION;
		}
		return RefactoringType.MOVE_OPERATION;
	}

	public UMLOperationBodyMapper getBodyMapper() {
		return bodyMapper;
	}

	public UMLOperation getOriginalOperation() {
		return originalOperation;
	}

	public UMLOperation getMovedOperation() {
		return movedOperation;
	}

	public Set<Replacement> getReplacements() {
		return replacements;
	}

	/**
	 * @return the code range of the source method in the <b>parent</b> commit
	 */
	public CodeRange getSourceOperationCodeRangeBeforeMove() {
		return originalOperation.codeRange();
	}

	/**
	 * @return the code range of the target method in the <b>child</b> commit
	 */
	public CodeRange getTargetOperationCodeRangeAfterMove() {
		return movedOperation.codeRange();
	}

	public boolean compatibleWith(MoveAttributeRefactoring ref) {
		if(ref.getMovedAttribute().getClassName().equals(this.movedOperation.getClassName()) &&
				ref.getOriginalAttribute().getClassName().equals(this.originalOperation.getClassName())) {
			List<String> originalOperationVariables = this.originalOperation.getAllVariables();
			List<String> movedOperationVariables = this.movedOperation.getAllVariables();
			return originalOperationVariables.contains(ref.getOriginalAttribute().getName()) &&
					movedOperationVariables.contains(ref.getMovedAttribute().getName());
		}
		return false;
	}

	public Set<ImmutablePair<String, String>> getInvolvedClassesBeforeRefactoring() {
		Set<ImmutablePair<String, String>> pairs = new LinkedHashSet<>();
		pairs.add(new ImmutablePair<>(getOriginalOperation().getLocationInfo().getFilePath(), getOriginalOperation().getClassName()));
		return pairs;
	}

	public Set<ImmutablePair<String, String>> getInvolvedClassesAfterRefactoring() {
		Set<ImmutablePair<String, String>> pairs = new LinkedHashSet<>();
		pairs.add(new ImmutablePair<>(getMovedOperation().getLocationInfo().getFilePath(), getMovedOperation().getClassName()));
		return pairs;
	}

	@Override
	public List<CodeRange> leftSide() {
		List<CodeRange> ranges = new ArrayList<>();
		ranges.add(originalOperation.codeRange()
				.setDescription("original method declaration")
				.setCodeElement(originalOperation.toString()));
		return ranges;
	}

	@Override
	public List<CodeRange> rightSide() {
		List<CodeRange> ranges = new ArrayList<>();
		ranges.add(movedOperation.codeRange()
				.setDescription("moved method declaration")
				.setCodeElement(movedOperation.toString()));
		return ranges;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((movedOperation == null) ? 0 : movedOperation.hashCode());
		result = prime * result + ((movedOperation == null) ? 0 : movedOperation.getLocationInfo().hashCode());
		result = prime * result + ((originalOperation == null) ? 0 : originalOperation.hashCode());
		result = prime * result + ((originalOperation == null) ? 0 : originalOperation.getLocationInfo().hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		MoveOperationRefactoring other = (MoveOperationRefactoring) obj;
		if (movedOperation == null) {
			if (other.movedOperation != null)
				return false;
		} else if (!movedOperation.equals(other.movedOperation)) {
			return false;
		} else if(!movedOperation.getLocationInfo().equals(other.movedOperation.getLocationInfo())) {
			return false;
		}
		if (originalOperation == null) {
			return other.originalOperation == null;
		} else if (!originalOperation.equals(other.originalOperation)) {
			return false;
		} else return originalOperation.getLocationInfo().equals(other.originalOperation.getLocationInfo());
	}
}
