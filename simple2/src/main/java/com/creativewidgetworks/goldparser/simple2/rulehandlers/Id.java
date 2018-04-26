package com.creativewidgetworks.goldparser.simple2.rulehandlers;

import com.creativewidgetworks.goldparser.engine.Reduction;
import com.creativewidgetworks.goldparser.parser.GOLDParser;
import com.creativewidgetworks.goldparser.parser.ProcessRule;
import com.creativewidgetworks.goldparser.parser.Variable;
import com.creativewidgetworks.goldparser.simple2.Simple2;

@ProcessRule(rule="<Value> ::= Id")

/**
 * Rule handler for the assign the value of a Variable rule.
 *
 * @author Ralph Iden (http://www.creativewidgetworks.com)
 * @version 5.0.0 
 */
public class Id extends Reduction {
    private GOLDParser theParser;
    private String variableName;

    public Id(GOLDParser parser) {
        theParser = parser;
        Reduction reduction = parser.getCurrentReduction();
        if (reduction != null) {
            if (reduction.size() == 1) {
                variableName = reduction.get(0).asString();
            } else {
                parser.raiseParserException(Simple2.formatMessage("error.param_count", "1", String.valueOf(reduction.size())));
            }
        } else {
            parser.raiseParserException(Simple2.formatMessage("error.no_reduction"));
        }        
    }

    public String getVariableName() {
        return variableName;
    }
    
    @Override
    public Variable getValue() {
        Variable var = theParser.getProgramVariable(variableName);
        return var == null ? new Variable("") : var;
    }
    
    @Override
    public String toString() {
        return variableName + "=" + getValue();
    }
}
