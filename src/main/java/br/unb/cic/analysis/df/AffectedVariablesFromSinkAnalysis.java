package br.unb.cic.analysis.df;

import br.unb.cic.analysis.AbstractMergeConflictDefinition;
import soot.Body;
import soot.Local;
import soot.Unit;
import soot.toolkits.scalar.ArraySparseSet;
import soot.toolkits.scalar.FlowSet;

public class AffectedVariablesFromSinkAnalysis extends AffectedVariablesFromSourceAnalysis {

    public AffectedVariablesFromSinkAnalysis(Body methodBody, AbstractMergeConflictDefinition definition) {
        super(methodBody, definition);
    }

    @Override
    protected FlowSet<DataFlowAbstraction> gen(Unit u, FlowSet<DataFlowAbstraction> in) {
        FlowSet<DataFlowAbstraction> res = new ArraySparseSet<>();
        if (isSinkStatement(u)) {
            for (Local local : getDefVariables(u)) {
                res.add(new DataFlowAbstraction(local, findSinkStatement(u)));
            }
        } else {
            for (Local local : getUseVariables(u)) {
                for (DataFlowAbstraction dataFlow : in) {
                    if (local.getName().compareTo(dataFlow.getLocal().getName()) == 0) {
                        for(Local defVariable: getDefVariables(u)) {
                            Local temp = (Local) defVariable.clone();
                            temp.setName(temp.getName().split("#")[0]);
                            res.add(new DataFlowAbstraction(temp, findStatement(u)));
                        }
                    }
                }
            }
        }
        return res;
    }
}
