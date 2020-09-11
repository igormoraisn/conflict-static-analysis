package br.unb.cic.analysis.df;

import soot.toolkits.scalar.FlowSet;

import java.util.ArrayList;
import java.util.List;

public class AnalysisData {
    protected FlowSet<DataFlowAbstraction> outChanged;
    protected FlowSet<DataFlowAbstraction> outPropagated;

    public List<String> getOutChangedLocals() {
        List<String> locals = new ArrayList<>();
        for(DataFlowAbstraction data: outChanged) {
            locals.add(data.getLocal().toString());
        }
        return locals;
    }

    public List<String> getOutPropagatedLocals() {
        List<String> locals = new ArrayList<>();
        for(DataFlowAbstraction data: outPropagated) {
            locals.add(data.getLocal().toString());
        }
        return locals;
    }
}
