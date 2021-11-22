package gr.uom.java.xmi.diff;

import gr.uom.java.xmi.UMLAttribute;
import gr.uom.java.xmi.UMLOperation;
import gr.uom.java.xmi.decomposition.*;
import lombok.AccessLevel;
import lombok.Getter;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Detects expecting exception test encoding migration (from JUnit 3 to Rule-based JUnit 4)
 * JUnit3 relies on try-catch containing an Assert.fail call to expect thrown exceptions
 * JUnit4 usually relies on @Rule ExpectedException, a Single Member annotated field, which provides an expect method
 */
public class TryWithFailToExpectedExceptionRuleDetection {
    private final UMLOperation operationBefore;
    private final UMLOperation operationAfter;
    private final List<UMLAttribute> addedAttributes;
    private final List<CompositeStatementObject> removedCompositeStmts;
    private final List<StatementObject> addedStmts;
    private @Getter(AccessLevel.PACKAGE) List<TryStatementObject> tryStatements;
    private @Getter(AccessLevel.PACKAGE) List<String> capturedExceptions;
    private @Getter(AccessLevel.PACKAGE) List<OperationInvocation> assertFailInvocationsFound;
    private @Getter(AccessLevel.PACKAGE) UMLAttribute expectedExceptionFieldDeclaration;
    private @Getter(AccessLevel.PACKAGE) List<OperationInvocation> expectInvocations;

    public TryWithFailToExpectedExceptionRuleDetection(UMLOperationBodyMapper mapper, UMLClassBaseDiff classDiff) {
        this(mapper,classDiff.addedAttributes);
    }

    public TryWithFailToExpectedExceptionRuleDetection(UMLOperationBodyMapper mapper, List<UMLAttribute> addedAttributes) {
        this.operationBefore = mapper.getOperation1();
        this.operationAfter = mapper.getOperation2();
        this.addedAttributes = addedAttributes;
        removedCompositeStmts = mapper.getNonMappedInnerNodesT1();
        addedStmts = mapper.getNonMappedLeavesT2();
    }

    public TryWithFailToExpectedExceptionRuleRefactoring check() {
        try {
            if (checkFromTryWithFail() && checkToExpectedException()) {
                return createRefactoring();
            }
            return null;
        }
        catch (NoSuchElementException exception) {
            return null;
        }
    }

    private TryWithFailToExpectedExceptionRuleRefactoring createRefactoring() {
        // TODO: Filter data relevant to refactoring
        return new TryWithFailToExpectedExceptionRuleRefactoring(operationBefore, operationAfter, tryStatements, assertFailInvocationsFound, capturedExceptions, expectInvocations, expectedExceptionFieldDeclaration);
    }

    private boolean checkFromTryWithFail() {
        tryStatements = filterTryStatement(removedCompositeStmts).collect(Collectors.toList());
        capturedExceptions = tryStatements.stream()
                .filter(stmt -> detectAssertFailInvocationAtTheEndOf(stmt).findAny().isPresent())
                .flatMap(TryWithFailToExpectedExceptionRuleDetection::detectCatchExceptions)
                .collect(Collectors.toList());
        assertFailInvocationsFound = tryStatements.stream()
                .flatMap(TryWithFailToExpectedExceptionRuleDetection::detectAssertFailInvocationAtTheEndOf)
                .collect(Collectors.toList());
        return assertFailInvocationsFound.size() > 0;
    }

    private boolean checkToExpectedException() {
        expectedExceptionFieldDeclaration = addedAttributes.stream()
                .filter(field -> field.getType().getClassType().equals("ExpectedException"))
                .findAny()
                .orElseThrow();
        expectInvocations = detectAddedExpectInvocations(addedStmts,capturedExceptions, expectedExceptionFieldDeclaration)
                .collect(Collectors.toList());
        return expectInvocations.size() > 0;
    }

    private static Stream<OperationInvocation> detectAddedExpectInvocations(List<StatementObject> addedStmts, List<String> capturedExceptions, UMLAttribute expectedExceptionRuleFieldDeclaration) {
        return extractMethodInvocationsStream(addedStmts)
                .filter(invocation -> isExpectedExceptionExpectInvocation(capturedExceptions, invocation))
                .filter(expectInvocation -> expectedExceptionRuleFieldDeclaration.getName().equals(expectInvocation.getExpression()))
                .filter(expectInvocation -> expectInvocation.getArguments().size() == 1)
                .filter(expectInvocation -> capturedExceptions.contains(expectInvocation.getArguments().get(0).replace(".class", "")));
    }

    private static boolean isExpectedExceptionExpectInvocation(List<String> candidateExceptions, OperationInvocation invocation) {
        return invocation.getMethodName().equals("expect") && isAnyArgumentPassedTo(candidateExceptions, invocation);
    }

    private static boolean isAnyArgumentPassedTo(List<String> arguments, OperationInvocation invocation) {
        return arguments.contains(invocation.getArguments().get(0).replace(".class",""));
    }

    private static Stream<OperationInvocation> extractMethodInvocationsStream(List<StatementObject> addedStmts) {
        return addedStmts.stream().flatMap(st -> st.getMethodInvocationMap().values().stream().flatMap(Collection::stream));
    }

    private static Stream<OperationInvocation> detectAssertFailInvocationAtTheEndOf(TryStatementObject tryStatement) {
        var lastStatement = tryStatement.getStatements().get(tryStatement.getStatements().size() - 1);
        var operationInvocationsInLastStatement = new ArrayList<>(lastStatement.getMethodInvocationMap().values()).get(0);
        return operationInvocationsInLastStatement.stream().filter(invocation -> invocation.getExpression().equals("Assert") && invocation.getMethodName().equals("fail"));
    }

    private static Stream<String> detectCatchExceptions(TryStatementObject tryStatement) {
        return tryStatement.getCatchClauses().stream()
                .flatMap(clause -> clause.getVariableDeclarations().stream()
                        .map(variable -> variable.getType().getClassType()))
                .filter(classType -> classType.endsWith("Exception"));
    }

    private static Stream<TryStatementObject> filterTryStatement(List<CompositeStatementObject> stmts) {
        return stmts.stream()
                .filter(st->st instanceof TryStatementObject)
                .map(st -> (TryStatementObject)st);
    }
}
