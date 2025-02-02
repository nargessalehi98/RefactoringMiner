package gr.uom.java.xmi.diff;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.refactoringminer.api.RefactoringType;

import gr.uom.java.xmi.UMLAttribute;

public class MoveAndRenameAttributeRefactoring extends MoveAttributeRefactoring {
	private final Set<CandidateAttributeRefactoring> attributeRenames;
	
	public MoveAndRenameAttributeRefactoring(UMLAttribute originalAttribute, UMLAttribute movedAttribute,
			Set<CandidateAttributeRefactoring> attributeRenames) {
		super(originalAttribute, movedAttribute);
		this.attributeRenames = attributeRenames;
	}

	public Set<CandidateAttributeRefactoring> getAttributeRenames() {
		return attributeRenames;
	}

	public String toString() {
        return getName() + "\t" +
                originalAttribute.toQualifiedString() +
                " renamed to " +
                movedAttribute.toQualifiedString() +
                " and moved from class " +
                getSourceClassName() +
                " to class " +
                getTargetClassName();
	}

	public String getName() {
		return this.getRefactoringType().getDisplayName();
	}

	public RefactoringType getRefactoringType() {
		return RefactoringType.MOVE_RENAME_ATTRIBUTE;
	}

	@Override
	public List<CodeRange> rightSide() {
		List<CodeRange> ranges = new ArrayList<>();
		ranges.add(movedAttribute.codeRange()
				.setDescription("moved and renamed attribute declaration")
				.setCodeElement(movedAttribute.toString()));
		return ranges;
	}
}
