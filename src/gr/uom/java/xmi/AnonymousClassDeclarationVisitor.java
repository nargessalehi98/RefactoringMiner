package gr.uom.java.xmi;

import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.AnonymousClassDeclaration;

import java.util.LinkedHashSet;
import java.util.Set;

public class AnonymousClassDeclarationVisitor extends ASTVisitor {

	private final Set<AnonymousClassDeclaration> anonymousClassDeclarations = new LinkedHashSet<>();
	public boolean visit(AnonymousClassDeclaration node) {
		anonymousClassDeclarations.add(node);
		return super.visit(node);
	}

	public Set<AnonymousClassDeclaration> getAnonymousClassDeclarations() {
		return anonymousClassDeclarations;
	}
}
