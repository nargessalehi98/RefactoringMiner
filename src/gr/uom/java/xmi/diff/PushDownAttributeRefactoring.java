package gr.uom.java.xmi.diff;

import gr.uom.java.xmi.UMLAttribute;
import org.refactoringminer.api.RefactoringType;

import java.util.ArrayList;
import java.util.List;

public class PushDownAttributeRefactoring extends MoveAttributeRefactoring {

	public PushDownAttributeRefactoring(UMLAttribute originalAttribute, UMLAttribute movedAttribute) {
		super(originalAttribute, movedAttribute);
	}

	public String toString() {
		return getName() + "\t" +
				getOriginalAttribute().toQualifiedString() +
				" from class " +
				getSourceClassName() +
				" to " +
				getMovedAttribute().toQualifiedString() +
				" from class " +
				getTargetClassName();
	}

	public RefactoringType getRefactoringType() {
		return RefactoringType.PUSH_DOWN_ATTRIBUTE;
	}

	@Override
	public List<CodeRange> rightSide() {
		List<CodeRange> ranges = new ArrayList<>();
		ranges.add(movedAttribute.codeRange()
				.setDescription("pushed down attribute declaration")
				.setCodeElement(movedAttribute.toString()));
		return ranges;
	}
}
