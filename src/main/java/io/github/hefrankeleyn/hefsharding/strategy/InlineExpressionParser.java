package io.github.hefrankeleyn.hefsharding.strategy;

import com.google.common.base.Joiner;
import com.google.common.base.Strings;
import com.google.common.collect.Maps;
import groovy.lang.Closure;
import groovy.lang.GroovyShell;
import groovy.lang.Script;

import java.util.Map;
import java.util.Objects;

/**
 * @Date 2024/8/20
 * @Author lifei
 */
public class InlineExpressionParser {
    private String inlineExpression;
    private static final GroovyShell SHELL = new GroovyShell();
    private static final Map<String, Script> SCRIPTS = Maps.newHashMap();

    public InlineExpressionParser(String inlineExpression) {
        this.inlineExpression = inlineExpression;
    }

    public static String handlePlaceHolder(String expression) {
        if (Objects.isNull(expression)) {
            return null;
        }
        if (expression.contains("$->{")) {
            return expression.replaceAll("\\$->\\{", "\\$\\{");
        }
        return expression;
    }

    public String getInlineExpression() {
        return inlineExpression;
    }

    public void setInlineExpression(String inlineExpression) {
        this.inlineExpression = inlineExpression;
    }

    public Closure<?> evaluateClosure() {
        String closureExpression = Strings.lenientFormat("{it->\"%s\"}", inlineExpression);
        return (Closure<?>) evaluate(closureExpression);
    }

    private Object evaluate(String closureExpression) {
        Script script = null;
        if (SCRIPTS.containsKey(closureExpression)) {
            script = SCRIPTS.get(closureExpression);
        } else {
            script = SHELL.parse(closureExpression);
            SCRIPTS.put(closureExpression, script);
        }
        return script.run();
    }
}
