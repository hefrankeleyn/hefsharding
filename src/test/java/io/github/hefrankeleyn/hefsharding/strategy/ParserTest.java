package io.github.hefrankeleyn.hefsharding.strategy;

import groovy.lang.Closure;
import org.junit.Test;

/**
 * @Date 2024/8/21
 * @Author lifei
 */
public class ParserTest {

    @Test
    public void evaluateTest() {
        String inlineExpression = "user$->{id % 2}";
        String expression = InlineExpressionParser.handlePlaceHolder(inlineExpression);
        InlineExpressionParser parser = new InlineExpressionParser(expression);
        Closure<?> closure = parser.evaluateClosure();
        closure.setProperty("id", 5);
        String result = closure.call().toString();
        System.out.println(result);
    }

}
