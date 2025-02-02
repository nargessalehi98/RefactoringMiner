package gr.uom.java.xmi.diff;

public class TestOperationDiffMother {
    static String createExampleClassCode() {  // TODO: define as a non-static
        return "package ca.concordia.victor.exception;\n" +
                "\n" +
                "class ExampleClass {\n" +
                "    private int exampleField;\n" +
                "\n" +
                "    ExampleClass(int exampleField) {\n" +
                "        this.exampleField = exampleField;\n" +
                "    }\n" +
                "\n" +
                "    void exampleMethod(int guessingField) {\n" +
                "        if (guessingField != exampleField) {\n" +
                "            throw new IllegalArgumentException(\"Wrong guess!\");\n" + // TODO: better explanation
                "        }\n" +
                "    }\n" +
                "}\n";
    }

    static String createExampleClassTestCode(String exampleTestMethod) {
        return "package ca.concordia.victor.exception;\n" +
                "\n" +
                "import org.junit.Assert;\n" +
                "import org.junit.Before;\n" +
                "import org.junit.Rule;\n" +
                "import org.junit.Test;\n" +
                "import org.junit.rules.ExpectedException;\n" +
                "public class ExampleClassTest {\n" +
                "    private ExampleClass exampleObj;\n" +
                "    @Before\n" +
                "    public void setUp() {\n" +
                "        exampleObj = new ExampleClass(42);\n" +
                "    }\n" +
                exampleTestMethod + "\n" +
                "}";
    }

    static String createExampleTestMethod_TryCatchVersion() {
        return "    @Test\n" +
                "    public void testExampleMethod_WrongGuess() {\n" + // TODO: don't include error message in func name
                "        try {\n" +
                "            exampleObj.exampleMethod(0);\n" +
                "            Assert.fail(\"IllegalArgumentException expected\");\n" + // TODO: put it here 
                "        } catch (IllegalArgumentException ignored) {}\n" +
                "    }";
    }

    static String createExampleTestMethod_InlineVersion() {
        return "    @Test(expected = IllegalArgumentException.class)\n" +
                "    public void testExampleMethod_WrongGuess() {\n" +
                "        exampleObj.exampleMethod(0);\n" +
                "    }";
    }

    static String createExampleTestMethod_RuleVersion() {
        return "    @Rule public ExpectedException thrown = ExpectedException.none();\n" +
                "    @Test\n" +
                "    public void testExampleMethod_WrongGuess() {\n" +
                "        thrown.expect(IllegalArgumentException.class);\n" +
                "        exampleObj.exampleMethod(0);\n" +
                "    }";
    }

    static String createExampleTestMethod_AssertVersion() {
        return "    @Test\n" +
                "    public void testExampleMethod_WrongGuess() {\n" +
                "        Assert.assertThrows(IllegalArgumentException.class, () -> {\n" +
                "            exampleObj.exampleMethod(0);\n" +
                "        });\n" +
                "    }";
    }

    static String createExampleTestMethod_CorrectGuess() {
        return "    @Test\n" +
                "    public void testExampleMethod_CorrectGuess() {\n" +
                "        exampleObj.exampleMethod(42);\n" +
                "    }";
    }
}
