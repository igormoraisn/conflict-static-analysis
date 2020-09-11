package br.unb.cic.analysis.df;

import br.unb.cic.analysis.AbstractMergeConflictDefinition;
import br.unb.cic.analysis.model.Pair;
import soot.Body;
import soot.Local;
import soot.Unit;
import soot.toolkits.scalar.ArraySparseSet;
import soot.toolkits.scalar.FlowSet;

public class AffectedVariablesFromSourceAnalysis extends TwoVariablesAnalysis {

    public AffectedVariablesFromSourceAnalysis(Body methodBody, AbstractMergeConflictDefinition definition) {
        super(methodBody, definition);
    }

    @Override
    protected void flowThrough(Pair<FlowSet<DataFlowAbstraction>, FlowSet<DataFlowAbstraction>> in, Unit u, Pair<FlowSet<DataFlowAbstraction>, FlowSet<DataFlowAbstraction>> out) {
        FlowSet<DataFlowAbstraction> inChanged = in.getFirst();
        FlowSet<DataFlowAbstraction> inPropagated = in.getSecond();
        FlowSet<DataFlowAbstraction> outChanged = out.getFirst();
        FlowSet<DataFlowAbstraction> outPropagated = out.getSecond();

        FlowSet<DataFlowAbstraction> temp = new ArraySparseSet<>();
        FlowSet<DataFlowAbstraction> killSet = new ArraySparseSet<>();
        inChanged.difference(killSet, temp);
        temp.union(gen(u, inChanged), outChanged);
        this.analysisData.outChanged = outChanged.clone();

        FlowSet<DataFlowAbstraction> tempPropagated = new ArraySparseSet<>();

        FlowSet<DataFlowAbstraction> killSetPropagated = new ArraySparseSet<>();
        FlowSet<Local> mustKill = kill(u);

        for (DataFlowAbstraction item : inPropagated) {
            for (Local local : mustKill) {
                if (local.getName().compareTo(item.getLocal().getName()) == 0) {
                    killSetPropagated.add(item);
                }
            }
        }
        inPropagated.difference(killSetPropagated, tempPropagated);
        tempPropagated.union(gen(u, inPropagated), outPropagated);
        this.analysisData.outPropagated = outPropagated.clone();
    }

    protected FlowSet<DataFlowAbstraction> gen(Unit u, FlowSet<DataFlowAbstraction> in) {
        FlowSet<DataFlowAbstraction> res = new ArraySparseSet<>();
        if (isSourceStatement(u)) {
            for (Local local : getDefVariables(u)) {
                res.add(new DataFlowAbstraction(local, findSourceStatement(u)));
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

    protected FlowSet<Local> kill(Unit u) {
        FlowSet<Local> res = new ArraySparseSet<>();
        for (Local local : getDefVariables(u)) {
            Local temp = (Local) local.clone();
            temp.setName(local.getName().split("#")[0]);
            res.add(temp);
        }
        return res;
    }
}
