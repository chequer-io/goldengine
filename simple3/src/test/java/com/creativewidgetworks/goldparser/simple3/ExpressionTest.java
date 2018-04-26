package com.creativewidgetworks.goldparser.simple3;

import java.math.BigDecimal;
import java.sql.Timestamp;

import org.junit.Test;

import com.creativewidgetworks.goldparser.engine.ParserException;
import com.creativewidgetworks.goldparser.engine.Token;
import com.creativewidgetworks.goldparser.parser.Variable;
import com.creativewidgetworks.goldparser.simple3.rulehandlers.Expression;

public class ExpressionTest extends GOLDParserTestCase {

    /*----------------------------------------------------------------------------*/
    /* Test object directly 
    /*----------------------------------------------------------------------------*/   

    @Test
    public void testExpression_no_reductions() throws Exception {
        GOLDParserForTesting parser = new GOLDParserForTesting();
        try {
            new Expression(parser);
            fail("Expected ParserException");
        } catch (ParserException pe) {
            assertEquals("wrong msg", "Invalid state, stack empty.", pe.getMessage());
        }
    }    

    /*----------------------------------------------------------------------------*/

    @Test
    public void testExpression_parameters() throws Exception {
        GOLDParserForTesting parser = new GOLDParserForTesting();

        // Too few
        try {
            parser.makeReductionAndPush(new Token("1"), new Token("*"));
            new Expression(parser).execute();
            fail("Expected ParserException");
        } catch (ParserException pe) {
            assertEquals("wrong msg", "Wrong number of parameters, expected 3 but got 2.", pe.getMessage());
        }

        parser.clear();
        
        // Too many
        try {
            parser.makeReductionAndPush(new Token("1"), new Token("+"), new Token("+"), new Token("1"));
            new Expression(parser).execute();
            fail("Expected ParserException");
        } catch (ParserException pe) {
            assertEquals("wrong msg", "Wrong number of parameters, expected 3 but got 4.", pe.getMessage());
        }
        
        parser.clear();
        
        // Just right
        parser.makeReductionAndPush(
            new Token("6"), 
            new Token("/"), 
            new Token("2"));
        new Expression(parser).execute();
    }    
    
    /*----------------------------------------------------------------------------*/
    
    @Test
    public void testExpression_invalid_operator() throws Exception {
        GOLDParserForTesting parser = new GOLDParserForTesting();

        // Valid operators
        String[] operators = {"==", "<>", "<", "<=", ">", ">=", "+", "-", "*", "/", "&"};
        for (String operator : operators) {
            parser.makeReductionAndPush(new Token("1"), new Token(operator), new Token("2"));
            new Expression(parser).execute();            
        }
        
        // Invalid operator
        try {
            parser.makeReductionAndPush(new Token("1"), new Token("!"), new Token("2"));
            new Expression(parser).execute();
            fail("Expected ParserException");
        } catch (ParserException pe) {
            assertEquals("wrong msg", "Invalid operator. Expected one of the following: == <> < <= > >= + - * / & but got \"!\".", pe.getMessage());
        }
    }    
    
    /*----------------------------------------------------------------------------*/

    @Test
    public void testExpression_add() throws Exception {
        GOLDParserForTesting parser = new GOLDParserForTesting();

        parser.makeReductionAndPush(
            new Token(makeNumberLiteralForTest("12")),        
            new Token("+"),
            new Token(makeNumberLiteralForTest("2")));        

        Expression expression = new Expression(parser);
        expression.execute();
        assertEquals("wrong result", new BigDecimal("14"), expression.getValue().asNumber());
    }

    /*----------------------------------------------------------------------------*/
    
    @Test
    public void testExpression_subtract() throws Exception {
        GOLDParserForTesting parser = new GOLDParserForTesting();
        
        parser.makeReductionAndPush(
                new Token(makeNumberLiteralForTest("12")),        
                new Token("-"),
                new Token(makeNumberLiteralForTest("2")));        
        
        Expression expression = new Expression(parser);
        expression.execute();
        assertEquals("wrong result", new BigDecimal("10"), expression.getValue().asNumber());
    }

    /*----------------------------------------------------------------------------*/
    
    @Test
    public void testExpression_multiply() throws Exception {
        GOLDParserForTesting parser = new GOLDParserForTesting();
        
        parser.makeReductionAndPush(
                new Token(makeNumberLiteralForTest("12")),        
                new Token("*"),
                new Token(makeNumberLiteralForTest("2")));        
        
        Expression expression = new Expression(parser);
        expression.execute();
        assertEquals("wrong result", new BigDecimal("24"), expression.getValue().asNumber());
    }

    /*----------------------------------------------------------------------------*/
    
    @Test
    public void testExpression_divide() throws Exception {
        GOLDParserForTesting parser = new GOLDParserForTesting();
        
        parser.makeReductionAndPush(
                new Token(makeNumberLiteralForTest("12")),        
                new Token("/"),
                new Token(makeNumberLiteralForTest("2")));        
        
        Expression expression = new Expression(parser);
        expression.execute();
        assertEquals("wrong result", new BigDecimal("6"), expression.getValue().asNumber());

        parser.clear();
        
        parser.makeReductionAndPush(
                new Token(makeNumberLiteralForTest("1")),        
                new Token("/"),
                new Token(makeNumberLiteralForTest("3")));        
        
        expression = new Expression(parser);
        expression.execute();
        assertEquals("wrong result", new BigDecimal("0.33333"), expression.getValue().asNumber());
    }

    /*----------------------------------------------------------------------------*/
    
    @Test
    public void testExpression_concatentate() throws Exception {
        GOLDParserForTesting parser = new GOLDParserForTesting();
        
        parser.makeReductionAndPush(
                new Token(makeStringLiteralForTest("AB")),        
                new Token("&"),
                new Token(makeStringLiteralForTest("C")));        
        
        Expression expression = new Expression(parser);
        expression.execute();
        assertEquals("wrong result", "ABC", expression.getValue().asString());
        
        parser.clear();
        
        parser.makeReductionAndPush(
                new Token(makeStringLiteralForTest("AB")),        
                new Token("+"),
                new Token(makeNumberLiteralForTest("1")));        
        
        expression = new Expression(parser);
        expression.execute();
        assertEquals("wrong result", "AB1", expression.getValue().asString());
    }

    /*----------------------------------------------------------------------------*/
    /* Test object indirectly using the engine 
    /*----------------------------------------------------------------------------*/
    
    @Test
    public void testExpression_compare_booleans() throws Exception {
        GOLDParserForTesting parser = new GOLDParserForTesting();

        String sourceCode =
            "b1 = 1 == 1 \r\n" +
            "b2 = 1 == 2 \r\n" +
            "print b1 == b1 \r\n" +
            "print b1 == b2 \r\n" +
            "print b1 <> b2 \r\n" +
            "print b1 <> b1 \r\n" +
            "\r\n";

        String[] actual = executeProgram(parser, sourceCode);
        validateLines(makeExpected("true", "false", "true", "false"), actual);
    }

    /*----------------------------------------------------------------------------*/
    
    @Test
    public void testExpression_compare_numbers() throws Exception {
        GOLDParserForTesting parser = new GOLDParserForTesting();
        
        String sourceCode =
            "n1 = 4    \r\n" +
            "n2 = 5    \r\n" +
            "n3 = 5.0  \r\n" +
            "n4 = 6    \r\n" +
            "print n1 < n2  \r\n" +
            "print n2 < n1  \r\n" +
            "print n1 <= n2 \r\n" +
            "print n2 <= n1 \r\n" +
            "print n1 == n2 \r\n" +
            "print n2 == n3 \r\n" +
            "print n1 <> n2 \r\n" +
            "print n1 <> n1 \r\n" +
            "print n3 > n1  \r\n" +
            "print n1 > n3  \r\n" +
            "print n3 >= n2 \r\n" +
            "print n1 >= n2 \r\n" +
            "\r\n";
        
        String[] actual = executeProgram(parser, sourceCode);
        validateLines(makeExpected(
            "true", 
            "false",
            "true", 
            "false",
            "false", 
            "true", 
            "true", 
            "false", 
            "true", 
            "false",
            "true",
            "false"), actual);
    }

    /*----------------------------------------------------------------------------*/

    @Test
    public void testExpression_compare_strings() throws Exception {
        GOLDParserForTesting parser = new GOLDParserForTesting();
        
        String sourceCode =
            "s1 = 'alpha' \r\n" +
            "s2 = 'beta'  \r\n" +
            "s3 = 'gamma' \r\n" +
            "s4 = 'BETA'  \r\n" +
            "print s1 < s2  \r\n" + 
            "print s2 < s1  \r\n" + 
            "print s1 <= s2 \r\n" + 
            "print s1 <= s1 \r\n" + 
            "print s2 <= s1 \r\n" + 
            "print s1 == s1 \r\n" + 
            "print s1 == s2 \r\n" +
            "print s2 <> s4 \r\n" + 
            "print s2 <> s2 \r\n" +
            "print s3 > s4  \r\n" + 
            "print s4 > s3  \r\n" + 
            "print s3 >= s2 \r\n" + 
            "print s3 >= s3 \r\n" + 
            "print s2 >= s3 \r\n" + 
            "\r\n";
        
        String[] actual = executeProgram(parser, sourceCode);
        validateLines(makeExpected(
            "true", "false",          // <
            "true", "true", "false",  // <=
            "true", "false",          // ==
            "true", "false",          // <>
            "true", "false",          // >
            "true", "true", "false"   // >=
            ), actual);
    }
 
    /*----------------------------------------------------------------------------*/

    @Test
    public void testExpression_compare_timestamps() throws Exception {
        GOLDParserForTesting parser = new GOLDParserForTesting();
        
        // Since Simple3 doesn't support setting Timestamp values,
        // we'll set the test values outside of the parsing stage
        parser.setProgramVariable("ts1", new Variable(new Timestamp(100000)));
        parser.setProgramVariable("ts2", new Variable(new Timestamp(200000)));
        parser.setProgramVariable("ts3", new Variable(new Timestamp(200000)));
        parser.setProgramVariable("ts4", new Variable(new Timestamp(300000)));
        
        String sourceCode =
            "print ts1 < ts2  \r\n" +
            "print ts2 < ts1  \r\n" +
            "print ts1 <= ts2 \r\n" +
            "print ts2 <= ts1 \r\n" +
            "print ts1 == ts2 \r\n" +
            "print ts2 == ts3 \r\n" +
            "print ts2 <> ts4 \r\n" +
            "print ts2 <> ts2 \r\n" +
            "print ts3 > ts4  \r\n" +
            "print ts4 > ts3  \r\n" +
            "print ts3 >= ts2 \r\n" +
            "print ts3 >= ts4 \r\n" +
            "\r\n";
        
        String[] actual = executeProgram(parser, sourceCode);
        validateLines(makeExpected(
            "true", 
            "false", 
            "true", 
            "false", 
            "false", 
            "true", 
            "true", 
            "false", 
            "false",
            "true",
            "true",
            "false"), actual);
    }

    /*----------------------------------------------------------------------------*/

    private void validateTypeMismatchException(GOLDParserForTesting parser, String source) {
        parser.getErrorMessages().clear();
        executeProgram(parser, source);
        assertEquals("should have errors", "Type mismatch error.", parser.getErrorMessage());
    }

    private void validateNoTypeMismatchException(GOLDParserForTesting parser, String source) {
        parser.getErrorMessages().clear();
        executeProgram(parser, source);
        assertEquals("should not have errors", "", parser.getErrorMessage());
    }
    
    @Test
    public void testTypeMismatch() throws Exception {
        GOLDParserForTesting parser = new GOLDParserForTesting();
        
        parser.setProgramVariable("b1", new Variable(Boolean.TRUE));        
        parser.setProgramVariable("n1", new Variable(BigDecimal.TEN));
        parser.setProgramVariable("s1", new Variable("Hello"));
        parser.setProgramVariable("ts1", new Variable(new Timestamp(100000)));
        
        validateTypeMismatchException(parser, "print b1 == n1");
        validateNoTypeMismatchException(parser, "print b1 == s1");
        validateTypeMismatchException(parser, "print b1 == ts1");
        
        validateTypeMismatchException(parser, "print n1 == b1");
        validateNoTypeMismatchException(parser, "print n1 == s1");
        validateTypeMismatchException(parser, "print n1 == ts1");

        validateNoTypeMismatchException(parser, "print s1 == b1");
        validateNoTypeMismatchException(parser, "print s1 == n1");
        validateNoTypeMismatchException(parser, "print s1 == ts1");

        validateTypeMismatchException(parser, "print ts1 == b1");
        validateTypeMismatchException(parser, "print ts1 == n1");
        validateNoTypeMismatchException(parser, "print ts1 == s1");
    }
}
