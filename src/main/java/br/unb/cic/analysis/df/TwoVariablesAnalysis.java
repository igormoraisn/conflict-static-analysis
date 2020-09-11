package br.unb.cic.analysis.df;

import br.unb.cic.analysis.AbstractAnalysis;
import br.unb.cic.analysis.AbstractMergeConflictDefinition;
import br.unb.cic.analysis.model.Conflict;
import br.unb.cic.analysis.model.Pair;
import br.unb.cic.analysis.model.Statement;
import soot.Body;
import soot.Local;
import soot.Unit;
import soot.ValueBox;
import soot.jimple.internal.JArrayRef;
import soot.jimple.internal.JInstanceFieldRef;
import soot.toolkits.graph.ExceptionalUnitGraph;
import soot.toolkits.scalar.ArraySparseSet;
import soot.toolkits.scalar.FlowSet;
import soot.toolkits.scalar.ForwardFlowAnalysis;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class TwoVariablesAnalysis extends ForwardFlowAnalysis<Unit, Pair<FlowSet<DataFlowAbstraction>, FlowSet<DataFlowAbstraction>>> implements AbstractAnalysis {

    protected Body methodBody;
    protected AbstractMergeConflictDefinition definition;
    protected AnalysisData analysisData;

    /**
     * Constructor of the TwoVariablesAnalysis class.
     *
     * According to the SOOT architecture, the constructor for a
     * flow analysis must receive as an argument a graph, set up
     * essential information and call the doAnalysis method of the
     * super class.
     */
    public TwoVariablesAnalysis(Body methodBody, AbstractMergeConflictDefinition definition) {
        super(new ExceptionalUnitGraph(methodBody));
        this.methodBody = methodBody;
        this.definition = definition;
        this.analysisData = new AnalysisData();
        definition.loadSourceStatements();
        definition.loadSinkStatements();
        doAnalysis();
    }

    @Override
    protected Pair<FlowSet<DataFlowAbstraction>, FlowSet<DataFlowAbstraction>> newInitialFlow() {
        return new Pair(new ArraySparseSet<>(), new ArraySparseSet<>());
    }

    @Override
    protected void merge(Pair<FlowSet<DataFlowAbstraction>, FlowSet<DataFlowAbstraction>> flowSetFlowSetPair, Pair<FlowSet<DataFlowAbstraction>, FlowSet<DataFlowAbstraction>> a1, Pair<FlowSet<DataFlowAbstraction>, FlowSet<DataFlowAbstraction>> a2) {

    }

    @Override
    protected void copy(Pair<FlowSet<DataFlowAbstraction>, FlowSet<DataFlowAbstraction>> flowSetFlowSetPair, Pair<FlowSet<DataFlowAbstraction>, FlowSet<DataFlowAbstraction>> a1) {

    }

    @Override
    protected void flowThrough(Pair<FlowSet<DataFlowAbstraction>, FlowSet<DataFlowAbstraction>> in, Unit u, Pair<FlowSet<DataFlowAbstraction>, FlowSet<DataFlowAbstraction>> out) {
    }

    @Override
    public void clear() {
    }

    @Override
    public Set<Conflict> getConflicts() {
        return null;
    }

    protected boolean isSourceStatement(Unit d) {
        return definition.getSourceStatements().stream().map(s -> s.getUnit()).collect(Collectors.toList()).contains(d);
    }

    protected boolean isSinkStatement(Unit d) {
        return definition.getSinkStatements().stream().map(s -> s.getUnit()).collect(Collectors.toList()).contains(d);
    }

    protected List<Local> getUseVariables(Unit u) {
        return u.getUseBoxes().stream()
                .map(box -> box.getValue())
                .filter(v -> v instanceof Local)
                .map(v -> (Local)v)
                .collect(Collectors.toList());
    }

    protected List<Local> getDefVariables(Unit u) {
        List<Local> localDefs = new ArrayList<>();
        for (ValueBox v : u.getDefBoxes()) {
            if (v.getValue() instanceof Local) {
                localDefs.add((Local) v.getValue());
            } else if (v.getValue() instanceof JArrayRef) {
                JArrayRef ref = (JArrayRef) v.getValue();
                localDefs.add((Local) ref.getBaseBox().getValue());
            }
            else if (v.getValue() instanceof JInstanceFieldRef) {
                JInstanceFieldRef ref = (JInstanceFieldRef) v.getValue();
                localDefs.add((Local) ref.getBaseBox().getValue());
            }
        }
        return localDefs;
    }

    protected boolean isLeftStatement(Unit u) {
        return isSourceStatement(u);
    }

    protected boolean isRightStatement(Unit u) {
        return isSinkStatement(u);
    }

    protected Statement findRightStatement(Unit u) {
        return findSinkStatement(u);
    }

    protected Statement findLeftStatement(Unit u) {
        return findSourceStatement(u);
    }

    protected Statement findSourceStatement(Unit d) {
        return definition.getSourceStatements().stream().filter(s -> s.getUnit().equals(d)).
                findFirst().get();
    }

    protected Statement findSinkStatement(Unit d) {
        return definition.getSinkStatements().stream().filter(s -> s.getUnit().equals(d)).
                findFirst().get();
    }

    protected Statement findStatement(Unit d) {
        return Statement.builder()
                .setClass(methodBody.getMethod().getDeclaringClass())
                .setMethod(methodBody.getMethod())
                .setType(Statement.Type.SOURCE)
                .setUnit(d)
                .setSourceCodeLineNumber(d.getJavaSourceStartLineNumber()).build();
    }
}