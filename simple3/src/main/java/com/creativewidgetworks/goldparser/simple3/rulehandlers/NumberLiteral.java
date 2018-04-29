package com.creativewidgetworks.goldparser.simple3.rulehandlers;

import java.math.BigDecimal;

import com.creativewidgetworks.goldparser.engine.ParserException;
import com.creativewidgetworks.goldparser.engine.Reduction;
import com.creativewidgetworks.goldparser.parser.GOLDParser;
import com.creativewidgetworks.goldparser.parser.ProcessRule;
import com.creativewidgetworks.goldparser.parser.Variable;
import com.creativewidgetworks.goldparser.simple3.Simple3;

@ProcessRule(rule="<Value> ::= NumberLiteral")

/**
 * Rule handler for the number literal rule.
 *
 * @author Ralph Iden (http://www.creativewidgetworks.com)
 * @version 5.0.0 
 */
public class NumberLiteral extends Reduction {

    public NumberLiteral(GOLDParser parser) throws ParserException  {
        String literal = parser.getCurrentReduction() == null ? "<null>" : parser.getCurrentReduction().get(0).asString();
        try {
            setValue(new Variable(new BigDecimal(literal)));
        } catch (NumberFormatException e) {
            parser.raiseParserException(Simple3.formatMessage("error.token_nan", literal));
        }
    }

}
