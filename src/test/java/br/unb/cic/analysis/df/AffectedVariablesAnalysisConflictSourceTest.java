package br.unb.cic.analysis.df;

import br.unb.cic.analysis.AbstractMergeConflictDefinition;
import br.unb.cic.analysis.SootWrapper;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import soot.*;

import java.util.*;

public class AffectedVariablesAnalysisConflictSourceTest {

    private AffectedVariablesFromSourceAnalysis analysis;

    @Before
    public void configure() {
        G.reset();
        Collector.instance().clear();

        AbstractMergeConflictDefinition definition = new AbstractMergeConflictDefinition() {
            @Override
            protected Map<String, List<Integer>> sourceDefinitions() {
                Map<String, List<Integer>> res = new HashMap<>();
                List<Integer> lines = new ArrayList<>();
                lines.add(7);      //source
                res.put("br.unb.cic.analysis.samples.AffectedVariablesSample", lines);
                return res;
            }

            @Override
            protected Map<String, List<Integer>> sinkDefinitions() {
                Map<String, List<Integer>> res = new HashMap<>();
                List<Integer> lines = new ArrayList<>();
                lines.add(11);      //sink
                res.put("br.unb.cic.analysis.samples.AffectedVariablesSample", lines);
                return res;
            }
        };

        PackManager.v().getPack("jtp").add(
                new Transform("jtp.oneConflict", new BodyTransformer() {
                    @Override
                    protected void internalTransform(Body body, String phaseName, Map<String, String> options) {
                        analysis = new AffectedVariablesFromSourceAnalysis(body, definition);
                    }
                }));
        String cp = "target/test-classes";
        String targetClass = "br.unb.cic.analysis.samples.AffectedVariablesSample";

        PhaseOptions.v().setPhaseOption("jb", "use-original-names:true");
        SootWrapper.builder().withClassPath(cp).addClass(targetClass).build().execute();
    }

    @Test
    public void testAffectedVariablesAnalysisWithOutChanged() {
        List<String> outChanged = analysis.analysisData.getOutChangedLocals();
        List<String> variables = Arrays.asList("x", "y", "w", "z");
        Assert.assertTrue(variables.containsAll(outChanged));
    }

    @Test
    public void testAffectedVariablesAnalysisWithOutPropagated() {
        List<String> outPropagated = analysis.analysisData.getOutPropagatedLocals();
        List<String> variables = Arrays.asList("x", "w", "z");
        Assert.assertTrue(variables.containsAll(outPropagated));
    }
}
