package com.creativewidgetworks.goldparser.simple3.rulehandlers;

import com.creativewidgetworks.goldparser.engine.Reduction;
import com.creativewidgetworks.goldparser.parser.GOLDParser;
import com.creativewidgetworks.goldparser.parser.ProcessRule;
import com.creativewidgetworks.goldparser.parser.Variable;
import com.creativewidgetworks.goldparser.simple3.Simple3;

@ProcessRule(rule="<Value> ::= ( <Expression> )")

/**
 * Rule handler for the parenthesis rule.
 *
 * @author Ralph Iden (http://www.creativewidgetworks.com)
 * @version 5.0.0 
 */
public class Parenthesis extends Reduction {

    Reduction innerReduction;
    
    public Parenthesis(GOLDParser parser) {
        Reduction reduction = parser.getCurrentReduction();
        if (reduction != null) {
            if (reduction.size() == 3) {
                innerReduction = parser.getCurrentReduction().get(1).asReduction();
            } else {
                parser.raiseParserException(Simple3.formatMessage("error.param_count", "3", String.valueOf(reduction.size())));
            }
        } else {
            parser.raiseParserException(Simple3.formatMessage("error.no_reduction"));
        }           
    }

    @Override
    public Variable getValue() {
        return innerReduction.getValue();
    }
    
}
