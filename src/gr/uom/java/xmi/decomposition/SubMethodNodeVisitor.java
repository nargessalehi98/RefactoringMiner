package gr.uom.java.xmi.decomposition;

import lombok.Builder;
import lombok.Getter;
import lombok.Singular;
import org.eclipse.jdt.core.dom.*;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeNode;
import java.util.*;
import java.util.regex.Pattern;

public class SubMethodNodeVisitor extends ASTVisitor {
	static final Pattern METHOD_INVOCATION_PATTERN = Pattern.compile("!(\\w|\\.)*@\\w*");
	static final Pattern METHOD_SIGNATURE_PATTERN = Pattern.compile("(public|protected|private|static|\\s) +[\\w<>\\[\\]]+\\s+(\\w+) *\\([^)]*\\) *(\\{?|[^;])");
	private final CompilationUnit cu;
	private final String filePath;
	@Getter private List<String> variables = new ArrayList<>();
	@Getter private List<String> types = new ArrayList<>();
	@Getter private Map<String, List<OperationInvocation>> methodInvocationMap = new LinkedHashMap<>();
	@Getter private List<VariableDeclaration> variableDeclarations = new ArrayList<>();
	@Getter private List<AnonymousClassDeclarationObject> anonymousClassDeclarations = new ArrayList<>();
	@Getter private List<String> stringLiterals = new ArrayList<>();
	@Getter private List<String> numberLiterals = new ArrayList<>();
	@Getter private List<String> nullLiterals = new ArrayList<>();
	@Getter private List<String> booleanLiterals = new ArrayList<>();
	@Getter private List<String> typeLiterals = new ArrayList<>();
	@Getter private Map<String, List<ObjectCreation>> creationMap = new LinkedHashMap<>();
	@Getter private List<String> infixExpressions = new ArrayList<>();
	@Getter private List<String> infixOperators = new ArrayList<>();
	@Getter private List<String> arrayAccesses = new ArrayList<>();
	@Getter private List<String> prefixExpressions = new ArrayList<>();
	@Getter private List<String> postfixExpressions = new ArrayList<>();
	@Getter private List<String> arguments = new ArrayList<>();
	@Getter private List<TernaryOperatorExpression> ternaryOperatorExpressions = new ArrayList<>();
	@Getter private List<LambdaExpressionObject> lambdas = new ArrayList<>();
	private Set<ASTNode> builderPatternChains = new LinkedHashSet<>();
	private DefaultMutableTreeNode root = new DefaultMutableTreeNode();
	private DefaultMutableTreeNode current = root;

	@Builder
	public SubMethodNodeVisitor(CompilationUnit cu, String filePath,
								@Singular List<String> variables,
								@Singular List<String> types,
								Map<String, List<OperationInvocation>> methodInvocationMap,
								@Singular List<VariableDeclaration> variableDeclarations,
								@Singular List<AnonymousClassDeclarationObject> anonymousClassDeclarations,
								@Singular List<String> stringLiterals,
								@Singular List<String> numberLiterals,
								@Singular List<String> nullLiterals,
								@Singular List<String> booleanLiterals,
								@Singular List<String> typeLiterals,
								Map<String, List<ObjectCreation>> creationMap,
								@Singular List<String> infixExpressions,
								@Singular List<String> infixOperators,
								@Singular List<String> arrayAccesses,
								@Singular List<String> prefixExpressions,
								@Singular List<String> postfixExpressions,
								@Singular List<String> arguments,
								@Singular List<TernaryOperatorExpression> ternaryOperatorExpressions,
								@Singular List<LambdaExpressionObject> lambdas,
								Set<ASTNode> builderPatternChains,
								DefaultMutableTreeNode root) {
		this.cu = cu;
		this.filePath = filePath;
		this.variables = variables;
		this.types = types;
		this.methodInvocationMap = methodInvocationMap == null ? new LinkedHashMap<>() : methodInvocationMap;
		this.variableDeclarations = variableDeclarations;
		this.anonymousClassDeclarations = anonymousClassDeclarations;
		this.stringLiterals = stringLiterals;
		this.numberLiterals = numberLiterals;
		this.nullLiterals = nullLiterals;
		this.booleanLiterals = booleanLiterals;
		this.typeLiterals = typeLiterals;
		this.creationMap = creationMap == null ? new LinkedHashMap<>() : creationMap;
		this.infixExpressions = infixExpressions;
		this.infixOperators = infixOperators;
		this.arrayAccesses = arrayAccesses;
		this.prefixExpressions = prefixExpressions;
		this.postfixExpressions = postfixExpressions;
		this.arguments = arguments;
		this.ternaryOperatorExpressions = ternaryOperatorExpressions;
		this.lambdas = lambdas;
		this.builderPatternChains = builderPatternChains == null ? new LinkedHashSet<>() : builderPatternChains;
		this.root = root == null ? new DefaultMutableTreeNode() : root;
		this.current = this.root;
	}

	public static class SubMethodNodeVisitorBuilder {}

	SubMethodNodeVisitor(CompilationUnit cu, String filePath) {
		this.cu = cu;
		this.filePath = filePath;
	}

	public boolean visit(ArrayAccess node) {
		arrayAccesses.add(node.toString());
		if(current.getUserObject() != null) {
			AnonymousClassDeclarationObject anonymous = (AnonymousClassDeclarationObject)current.getUserObject();
			anonymous.getArrayAccesses().add(node.toString());
		}
		return super.visit(node);
	}

	public boolean visit(PrefixExpression node) {
		prefixExpressions.add(node.toString());
		if(current.getUserObject() != null) {
			AnonymousClassDeclarationObject anonymous = (AnonymousClassDeclarationObject)current.getUserObject();
			anonymous.getPrefixExpressions().add(node.toString());
		}
		return super.visit(node);
	}

	public boolean visit(PostfixExpression node) {
		postfixExpressions.add(node.toString());
		if(current.getUserObject() != null) {
			AnonymousClassDeclarationObject anonymous = (AnonymousClassDeclarationObject)current.getUserObject();
			anonymous.getPostfixExpressions().add(node.toString());
		}
		return super.visit(node);
	}

	public boolean visit(ConditionalExpression node) {
		TernaryOperatorExpression ternary = new TernaryOperatorExpression(cu, filePath, node);
		ternaryOperatorExpressions.add(ternary);
		if(current.getUserObject() != null) {
			AnonymousClassDeclarationObject anonymous = (AnonymousClassDeclarationObject)current.getUserObject();
			anonymous.getTernaryOperatorExpressions().add(ternary);
		}
		return super.visit(node);
	}

	public boolean visit(InfixExpression node) {
		infixExpressions.add(node.toString());
		infixOperators.add(node.getOperator().toString());
		if(current.getUserObject() != null) {
			AnonymousClassDeclarationObject anonymous = (AnonymousClassDeclarationObject)current.getUserObject();
			anonymous.getInfixExpressions().add(node.toString());
			anonymous.getInfixOperators().add(node.getOperator().toString());
		}
		return super.visit(node);
	}

	public boolean visit(ClassInstanceCreation node) {
		List<Expression> arguments = node.arguments();
		for(Expression argument : arguments) {
			processArgument(argument);
		}
		ObjectCreation creation = new ObjectCreation(cu, filePath, node);
		String nodeAsString = node.toString();
		if(creationMap.containsKey(nodeAsString)) {
			creationMap.get(nodeAsString).add(creation);
		}
		else {
			List<ObjectCreation> list = new ArrayList<>();
			list.add(creation);
			creationMap.put(nodeAsString, list);
		}
		if(current.getUserObject() != null) {
			AnonymousClassDeclarationObject anonymous = (AnonymousClassDeclarationObject)current.getUserObject();
			Map<String, List<ObjectCreation>> anonymousCreationMap = anonymous.getCreationMap();
			if(anonymousCreationMap.containsKey(nodeAsString)) {
				anonymousCreationMap.get(nodeAsString).add(creation);
			}
			else {
				List<ObjectCreation> list = new ArrayList<>();
				list.add(creation);
				anonymousCreationMap.put(nodeAsString, list);
			}
		}
		return super.visit(node);
	}

	public boolean visit(ArrayCreation node) {
		ObjectCreation creation = new ObjectCreation(cu, filePath, node);
		String nodeAsString = node.toString();
		if(creationMap.containsKey(nodeAsString)) {
			creationMap.get(nodeAsString).add(creation);
		}
		else {
			List<ObjectCreation> list = new ArrayList<>();
			list.add(creation);
			creationMap.put(nodeAsString, list);
		}
		if(current.getUserObject() != null) {
			AnonymousClassDeclarationObject anonymous = (AnonymousClassDeclarationObject)current.getUserObject();
			Map<String, List<ObjectCreation>> anonymousCreationMap = anonymous.getCreationMap();
			if(anonymousCreationMap.containsKey(nodeAsString)) {
				anonymousCreationMap.get(nodeAsString).add(creation);
			}
			else {
				List<ObjectCreation> list = new ArrayList<>();
				list.add(creation);
				anonymousCreationMap.put(nodeAsString, list);
			}
		}
		ArrayInitializer initializer = node.getInitializer();
		if(initializer != null) {
			if(initializer.expressions().size() > 10) {
				return false;
			}
		}
		return super.visit(node);
	}

	public boolean visit(VariableDeclarationFragment node) {
		if(!(node.getParent() instanceof LambdaExpression)) {
			VariableDeclaration variableDeclaration = new VariableDeclaration(cu, filePath, node);
			variableDeclarations.add(variableDeclaration);
			if(current.getUserObject() != null) {
				AnonymousClassDeclarationObject anonymous = (AnonymousClassDeclarationObject)current.getUserObject();
				anonymous.getVariableDeclarations().add(variableDeclaration);
			}
		}
		return super.visit(node);
	}

	public boolean visit(SingleVariableDeclaration node) {
		VariableDeclaration variableDeclaration = new VariableDeclaration(cu, filePath, node);
		variableDeclarations.add(variableDeclaration);
		if(current.getUserObject() != null) {
			AnonymousClassDeclarationObject anonymous = (AnonymousClassDeclarationObject)current.getUserObject();
			anonymous.getVariableDeclarations().add(variableDeclaration);
		}
		return super.visit(node);
	}

	public boolean visit(AnonymousClassDeclaration node) {
		DefaultMutableTreeNode childNode = insertNode(node);
		AnonymousClassDeclarationObject childAnonymous = (AnonymousClassDeclarationObject)childNode.getUserObject();
		if(current.getUserObject() != null) {
			AnonymousClassDeclarationObject currentAnonymous = (AnonymousClassDeclarationObject)current.getUserObject();
			currentAnonymous.getAnonymousClassDeclarations().add(childAnonymous);
		}
		anonymousClassDeclarations.add(childAnonymous);
		this.current = childNode;
		for(ASTNode parent : builderPatternChains) {
			if(isParent(node, parent)) {
				return false;
			}
		}
		return super.visit(node);
	}

	public void endVisit(AnonymousClassDeclaration node) {
		DefaultMutableTreeNode parentNode = deleteNode(node);
		for(ASTNode parent : builderPatternChains) {
			if(isParent(node, parent) || isParent(parent, node)) {
				removeAnonymousData();
				break;
			}
		}
		this.current = parentNode;
	}

	private void removeAnonymousData() {
		if(current.getUserObject() != null) {
			AnonymousClassDeclarationObject anonymous = (AnonymousClassDeclarationObject)current.getUserObject();
			this.variables.removeAll(anonymous.getVariables());
			this.types.removeAll(anonymous.getTypes());
			for(String key : anonymous.getMethodInvocationMap().keySet()) {
				this.methodInvocationMap.remove(key, anonymous.getMethodInvocationMap().get(key));
			}
			for(String key : anonymous.getCreationMap().keySet()) {
				this.creationMap.remove(key, anonymous.getCreationMap().get(key));
			}
			this.variableDeclarations.removeAll(anonymous.getVariableDeclarations());
			this.stringLiterals.removeAll(anonymous.getStringLiterals());
			this.booleanLiterals.removeAll(anonymous.getBooleanLiterals());
			this.typeLiterals.removeAll(anonymous.getTypeLiterals());
			this.numberLiterals.removeAll(anonymous.getNumberLiterals());
			this.infixExpressions.removeAll(anonymous.getInfixExpressions());
			this.infixOperators.removeAll(anonymous.getInfixOperators());
			this.arguments.removeAll(anonymous.getArguments());
			this.ternaryOperatorExpressions.removeAll(anonymous.getTernaryOperatorExpressions());
			this.anonymousClassDeclarations.removeAll(anonymous.getAnonymousClassDeclarations());
			this.lambdas.removeAll(anonymous.getLambdas());
		}
	}

	private DefaultMutableTreeNode deleteNode(AnonymousClassDeclaration childAnonymous) {
		Enumeration<TreeNode> enumeration = root.postorderEnumeration();
		DefaultMutableTreeNode childNode = findNode(childAnonymous);
		DefaultMutableTreeNode parentNode = findParent(childAnonymous, enumeration);
		assert childNode != null;
		parentNode.remove(childNode);
		AnonymousClassDeclarationObject childAnonymousObject = (AnonymousClassDeclarationObject)childNode.getUserObject();
		childAnonymousObject.setAstNode(null);
		return parentNode;
	}

	private DefaultMutableTreeNode insertNode(AnonymousClassDeclaration childAnonymous) {
		Enumeration<TreeNode> enumeration = root.postorderEnumeration();
		AnonymousClassDeclarationObject anonymousObject = new AnonymousClassDeclarationObject(cu, filePath, childAnonymous);
		DefaultMutableTreeNode childNode = new DefaultMutableTreeNode(anonymousObject);

		DefaultMutableTreeNode parentNode = findParent(childAnonymous, enumeration);
		parentNode.add(childNode);
		return childNode;
	}

	private DefaultMutableTreeNode findNode(AnonymousClassDeclaration anonymous) {
		Enumeration<TreeNode> enumeration = root.postorderEnumeration();

		while(enumeration.hasMoreElements()) {
			DefaultMutableTreeNode currentNode = (DefaultMutableTreeNode)enumeration.nextElement();
			AnonymousClassDeclarationObject currentAnonymous = (AnonymousClassDeclarationObject)currentNode.getUserObject();
			if(currentAnonymous != null && currentAnonymous.getAstNode().equals(anonymous)) {
				return currentNode;
			}
		}
		return null;
	}

	private DefaultMutableTreeNode findParent(AnonymousClassDeclaration childAnonymous, Enumeration enumeration) {
		while(enumeration.hasMoreElements()) {
			DefaultMutableTreeNode currentNode = (DefaultMutableTreeNode)enumeration.nextElement();
			AnonymousClassDeclarationObject currentAnonymous = (AnonymousClassDeclarationObject)currentNode.getUserObject();
			if(currentAnonymous != null && isParent(childAnonymous, currentAnonymous.getAstNode())) {
				return currentNode;
			}
		}
		return root;
	}

	private boolean isParent(ASTNode child, ASTNode parent) {
		ASTNode current = child;
		while(current.getParent() != null) {
			if(current.getParent().equals(parent))
				return true;
			current = current.getParent();
		}
		return false;
	}

	public boolean visit(StringLiteral node) {
		stringLiterals.add(node.toString());
		if(current.getUserObject() != null) {
			AnonymousClassDeclarationObject anonymous = (AnonymousClassDeclarationObject)current.getUserObject();
			anonymous.getStringLiterals().add(node.toString());
		}
		return super.visit(node);
	}

	public boolean visit(NumberLiteral node) {
		numberLiterals.add(node.toString());
		if(current.getUserObject() != null) {
			AnonymousClassDeclarationObject anonymous = (AnonymousClassDeclarationObject)current.getUserObject();
			anonymous.getNumberLiterals().add(node.toString());
		}
		return super.visit(node);
	}

	public boolean visit(NullLiteral node) {
		nullLiterals.add(node.toString());
		if(current.getUserObject() != null) {
			AnonymousClassDeclarationObject anonymous = (AnonymousClassDeclarationObject)current.getUserObject();
			anonymous.getNullLiterals().add(node.toString());
		}
		return super.visit(node);
	}

	public boolean visit(BooleanLiteral node) {
		booleanLiterals.add(node.toString());
		if(current.getUserObject() != null) {
			AnonymousClassDeclarationObject anonymous = (AnonymousClassDeclarationObject)current.getUserObject();
			anonymous.getBooleanLiterals().add(node.toString());
		}
		return super.visit(node);
	}

	public boolean visit(TypeLiteral node) {
		typeLiterals.add(node.toString());
		if(current.getUserObject() != null) {
			AnonymousClassDeclarationObject anonymous = (AnonymousClassDeclarationObject)current.getUserObject();
			anonymous.getTypeLiterals().add(node.toString());
		}
		return super.visit(node);
	}

	public boolean visit(ThisExpression node) {
		if(!(node.getParent() instanceof FieldAccess)) {
			variables.add(node.toString());
			if(current.getUserObject() != null) {
				AnonymousClassDeclarationObject anonymous = (AnonymousClassDeclarationObject)current.getUserObject();
				anonymous.getVariables().add(node.toString());
			}
		}
		return super.visit(node);
	}

	public boolean visit(SimpleName node) {
		if(node.getParent() instanceof FieldAccess && ((FieldAccess)node.getParent()).getExpression() instanceof ThisExpression) {
			FieldAccess fieldAccess = (FieldAccess)node.getParent();
			variables.add(fieldAccess.toString());
			if(current.getUserObject() != null) {
				AnonymousClassDeclarationObject anonymous = (AnonymousClassDeclarationObject)current.getUserObject();
				anonymous.getVariables().add(fieldAccess.toString());
			}
		}
		else if (!SimpleNameVisitorUtils.shouldSkip(node)) {
			variables.add(node.getIdentifier());
			if(current.getUserObject() != null) {
				AnonymousClassDeclarationObject anonymous = (AnonymousClassDeclarationObject)current.getUserObject();
				anonymous.getVariables().add(node.getIdentifier());
			}
		}
		return super.visit(node);
	}

	public boolean visit(ArrayType node) {
		types.add(node.toString());
		if(current.getUserObject() != null) {
			AnonymousClassDeclarationObject anonymous = (AnonymousClassDeclarationObject)current.getUserObject();
			anonymous.getTypes().add(node.toString());
		}
		return false;
	}

	public boolean visit(ParameterizedType node) {
		types.add(node.toString());
		if(current.getUserObject() != null) {
			AnonymousClassDeclarationObject anonymous = (AnonymousClassDeclarationObject)current.getUserObject();
			anonymous.getTypes().add(node.toString());
		}
		return false;
	}

	public boolean visit(WildcardType node) {
		types.add(node.toString());
		if(current.getUserObject() != null) {
			AnonymousClassDeclarationObject anonymous = (AnonymousClassDeclarationObject)current.getUserObject();
			anonymous.getTypes().add(node.toString());
		}
		return false;
	}

	public boolean visit(QualifiedType node) {
		types.add(node.toString());
		if(current.getUserObject() != null) {
			AnonymousClassDeclarationObject anonymous = (AnonymousClassDeclarationObject)current.getUserObject();
			anonymous.getTypes().add(node.toString());
		}
		return false;
	}

	public boolean visit(PrimitiveType node) {
		types.add(node.toString());
		if(current.getUserObject() != null) {
			AnonymousClassDeclarationObject anonymous = (AnonymousClassDeclarationObject)current.getUserObject();
			anonymous.getTypes().add(node.toString());
		}
		return false;
	}

	public boolean visit(SimpleType node) {
		Name name = node.getName();
		types.add(name.getFullyQualifiedName());
		if(current.getUserObject() != null) {
			AnonymousClassDeclarationObject anonymous = (AnonymousClassDeclarationObject)current.getUserObject();
			anonymous.getTypes().add(name.getFullyQualifiedName());
		}
		return false;
	}

	public boolean visit(MethodInvocation node) {
		List<Expression> arguments = node.arguments();
		for(Expression argument : arguments) {
			processArgument(argument);
		}
		String methodInvocation;
		if(METHOD_INVOCATION_PATTERN.matcher(node.toString()).matches()) {
			methodInvocation = processMethodInvocation(node);
		}
		else {
			methodInvocation = node.toString();
		}
		if(methodInvocationMap.isEmpty() && node.getExpression() instanceof MethodInvocation &&
				!(node.getName().getIdentifier().equals("length") && node.arguments().size() == 0)) {
			builderPatternChains.add(node);
		}
		for(String key : methodInvocationMap.keySet()) {
			List<OperationInvocation> invocations = methodInvocationMap.get(key);
			OperationInvocation invocation = invocations.get(0);
			if(key.startsWith(methodInvocation) && invocation.numberOfSubExpressions() > 0 &&
					!(invocation.getName().equals("length") && invocation.getArguments().size() == 0)) {
				builderPatternChains.add(node);
			}
		}
		OperationInvocation invocation = new OperationInvocation(cu, filePath, node);
		visitInvocation(methodInvocation, invocation);
		return super.visit(node);
	}

	static String processMethodInvocation(MethodInvocation node) {
		return node.getName().getIdentifier() + formatArguments(node.arguments());
	}

	static String processClassInstanceCreation(ClassInstanceCreation node) {
		return "new" + " " + node.getType().toString() + formatArguments(node.arguments());
	}

	private static String formatArguments(List<Expression> arguments) {
		StringBuilder sb = new StringBuilder();
		sb.append("(");
		if(arguments.size() > 0) {
			for(int i=0; i<arguments.size()-1; i++)
				sb.append(arguments.get(i).toString()).append(", ");
			sb.append(arguments.get(arguments.size()-1).toString());
		}
		sb.append(")");
		return sb.toString();
	}

	public boolean visit(SuperMethodInvocation node) {
		visitInvocation(node.arguments(), node.toString(), new OperationInvocation(cu, filePath, node));
		return super.visit(node);
	}

	public boolean visit(ConstructorInvocation node) {
		visitInvocation(node.arguments(), node.toString(), new OperationInvocation(cu, filePath, node));
		return super.visit(node);
	}

	public boolean visit(SuperConstructorInvocation node) {
		visitInvocation(node.arguments(), node.toString(), new OperationInvocation(cu, filePath, node));
		return super.visit(node);
	}

	private void visitInvocation(List<Expression> arguments, String node, OperationInvocation invocation) {
		for(Expression argument : arguments) {
			processArgument(argument);
		}
		visitInvocation(node, invocation);
	}

	private void visitInvocation(String node, OperationInvocation invocation) {
		if(methodInvocationMap.containsKey(node)) {
			methodInvocationMap.get(node).add(invocation);
		}
		else {
			List<OperationInvocation> list = new ArrayList<>();
			list.add(invocation);
			methodInvocationMap.put(node, list);
		}
		if(current.getUserObject() != null) {
			AnonymousClassDeclarationObject anonymous = (AnonymousClassDeclarationObject)current.getUserObject();
			Map<String, List<OperationInvocation>> anonymousMethodInvocationMap = anonymous.getMethodInvocationMap();
			if(anonymousMethodInvocationMap.containsKey(node)) {
				anonymousMethodInvocationMap.get(node).add(invocation);
			}
			else {
				List<OperationInvocation> list = new ArrayList<>();
				list.add(invocation);
				anonymousMethodInvocationMap.put(node, list);
			}
		}
	}

	private void processArgument(Expression argument) {
		if(argument instanceof SuperMethodInvocation ||
				argument instanceof Name ||
				argument instanceof StringLiteral ||
				argument instanceof BooleanLiteral ||
				argument instanceof NumberLiteral ||
				(argument instanceof FieldAccess && ((FieldAccess)argument).getExpression() instanceof ThisExpression) ||
				(argument instanceof ArrayAccess && invalidArrayAccess((ArrayAccess)argument)) ||
				(argument instanceof InfixExpression && invalidInfix((InfixExpression)argument)))
			return;
		this.arguments.add(argument.toString());
		if(current.getUserObject() != null) {
			var anonymous = (AnonymousClassDeclarationObject) current.getUserObject();
			anonymous.getArguments().add(argument.toString());
		}
	}

	public boolean visit(QualifiedName node) {
		Name qualifier = node.getQualifier();
		if(Character.isUpperCase(qualifier.getFullyQualifiedName().charAt(0))) {
			types.add(qualifier.getFullyQualifiedName());
			if(current.getUserObject() != null) {
				AnonymousClassDeclarationObject anonymous = (AnonymousClassDeclarationObject)current.getUserObject();
				anonymous.getTypes().add(qualifier.getFullyQualifiedName());
			}
			variables.add(node.toString());
			if(current.getUserObject() != null) {
				AnonymousClassDeclarationObject anonymous = (AnonymousClassDeclarationObject)current.getUserObject();
				anonymous.getVariables().add(node.toString());
			}
		}
		else if(qualifier instanceof SimpleName && !(node.getParent() instanceof QualifiedName)) {
			if(node.getName().getIdentifier().equals("length")) {
				variables.add(node.toString());
				if(current.getUserObject() != null) {
					AnonymousClassDeclarationObject anonymous = (AnonymousClassDeclarationObject)current.getUserObject();
					anonymous.getVariables().add(node.toString());
				}
			}
			else {
				String qualifierIdentifier = ((SimpleName)qualifier).getIdentifier();
				MethodDeclaration parentMethodDeclaration = findParentMethodDeclaration(node);
				if(parentMethodDeclaration != null) {
					boolean qualifierIsParameter = false;
					List<SingleVariableDeclaration> parameters = parentMethodDeclaration.parameters();
					for(SingleVariableDeclaration parameter : parameters) {
						if(parameter.getName().getIdentifier().equals(qualifierIdentifier)) {
							qualifierIsParameter = true;
							break;
						}
					}
					if(qualifierIsParameter) {
						variables.add(node.toString());
						if(current.getUserObject() != null) {
							AnonymousClassDeclarationObject anonymous = (AnonymousClassDeclarationObject)current.getUserObject();
							anonymous.getVariables().add(node.toString());
						}
					}
				}
				EnhancedForStatement enhancedFor = findParentEnhancedForStatement(node);
				if(enhancedFor != null) {
					if(enhancedFor.getParameter().getName().getIdentifier().equals(qualifierIdentifier)) {
						variables.add(node.toString());
						if(current.getUserObject() != null) {
							AnonymousClassDeclarationObject anonymous = (AnonymousClassDeclarationObject)current.getUserObject();
							anonymous.getVariables().add(node.toString());
						}
					}
				}
			}
		}
		return super.visit(node);
	}

	private EnhancedForStatement findParentEnhancedForStatement(ASTNode node) {
		ASTNode parent = node.getParent();
		while(parent != null) {
			if(parent instanceof EnhancedForStatement) {
				return (EnhancedForStatement)parent;
			}
			parent = parent.getParent();
		}
		return null;
	}

	private MethodDeclaration findParentMethodDeclaration(ASTNode node) {
		ASTNode parent = node.getParent();
		while(parent != null) {
			if(parent instanceof MethodDeclaration) {
				return (MethodDeclaration)parent;
			}
			parent = parent.getParent();
		}
		return null;
	}

	public boolean visit(CastExpression node) {
		Expression castExpression = node.getExpression();
		if(castExpression instanceof SimpleName) {
			variables.add(node.toString());
			if(current.getUserObject() != null) {
				AnonymousClassDeclarationObject anonymous = (AnonymousClassDeclarationObject)current.getUserObject();
				anonymous.getVariables().add(node.toString());
			}
		}
		return super.visit(node);
	}

	public boolean visit(LambdaExpression node) {
		LambdaExpressionObject lambda = new LambdaExpressionObject(cu, filePath, node);
		lambdas.add(lambda);
		if(current.getUserObject() != null) {
			AnonymousClassDeclarationObject anonymous = (AnonymousClassDeclarationObject)current.getUserObject();
			anonymous.getLambdas().add(lambda);
		}
		return false;
	}

	private static boolean invalidArrayAccess(ArrayAccess e) {
		return e.getArray() instanceof SimpleName && simpleNameOrNumberLiteral(e.getIndex());
	}

	private static boolean invalidInfix(InfixExpression e) {
		return simpleNameOrNumberLiteral(e.getLeftOperand()) && simpleNameOrNumberLiteral(e.getRightOperand());
	}

	private static boolean simpleNameOrNumberLiteral(Expression e) {
		return e instanceof SimpleName || e instanceof NumberLiteral;
	}
}
