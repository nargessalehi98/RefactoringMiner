package gr.uom.java.xmi.decomposition;

import gr.uom.java.xmi.LocationInfo;
import org.eclipse.jdt.core.dom.CompilationUnit;

import java.util.ArrayList;
import java.util.List;

public class VariableScope {
	private final String filePath;
	private final int startOffset;
	private final int endOffset;
	private final int startLine;
	private int startColumn;
	private final int endLine;
	private int endColumn;
	private final List<AbstractCodeFragment> statementsInScope = new ArrayList<>();
	private final List<AbstractCodeFragment> statementsInScopeUsingVariable = new ArrayList<>();
	
	public VariableScope(CompilationUnit cu, String filePath, int startOffset, int endOffset) {
		//ASTNode parent = node.getParent();
		this.filePath = filePath;
		this.startOffset = startOffset;
		this.endOffset = endOffset;
		//this.startOffset = node.getStartPosition();
		//this.endOffset = parent.getStartPosition() + parent.getLength();
		
		//lines are 1-based
		this.startLine = cu.getLineNumber(startOffset);
		this.endLine = cu.getLineNumber(endOffset);
		//columns are 0-based
		this.startColumn = cu.getColumnNumber(startOffset);
		//convert to 1-based
		if(this.startColumn > 0) {
			this.startColumn += 1;
		}
		this.endColumn = cu.getColumnNumber(endOffset);
		//convert to 1-based
		if(this.endColumn > 0) {
			this.endColumn += 1;
		}
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + endColumn;
		result = prime * result + endLine;
		result = prime * result + endOffset;
		result = prime * result + ((filePath == null) ? 0 : filePath.hashCode());
		result = prime * result + startColumn;
		result = prime * result + startLine;
		result = prime * result + startOffset;
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
		VariableScope other = (VariableScope) obj;
		if (endColumn != other.endColumn)
			return false;
		if (endLine != other.endLine)
			return false;
		if (endOffset != other.endOffset)
			return false;
		if (filePath == null) {
			if (other.filePath != null)
				return false;
		} else if (!filePath.equals(other.filePath))
			return false;
		if (startColumn != other.startColumn)
			return false;
		if (startLine != other.startLine)
			return false;
		return startOffset == other.startOffset;
	}

	public String toString() {
		return startLine + ":" + startColumn +
				"-" +
				endLine + ":" + endColumn;
	}

	public void addStatement(AbstractCodeFragment statement) {
		this.statementsInScope.add(statement);
	}

	public void addStatementUsingVariable(AbstractCodeFragment statement) {
		this.statementsInScopeUsingVariable.add(statement);
	}

	public List<AbstractCodeFragment> getStatementsInScope() {
		return statementsInScope;
	}

	public List<AbstractCodeFragment> getStatementsInScopeUsingVariable() {
		return statementsInScopeUsingVariable;
	}

	public boolean subsumes(LocationInfo other) {
		return this.filePath.equals(other.getFilePath()) &&
				this.startOffset <= other.getStartOffset() &&
				this.endOffset >= other.getEndOffset();
	}
}
