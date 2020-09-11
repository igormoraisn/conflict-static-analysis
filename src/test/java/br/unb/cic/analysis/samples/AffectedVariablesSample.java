package br.unb.cic.analysis.samples;

public class AffectedVariablesSample {
    public static void main(String[] args) {
        int a, b, x, y, w, z, c;
        a = 1; // in {} out {}
        x = 1; // source in {} out{x}
        y = x + a; // in{x} out {x,y}
        b = a++; // in{x,y} out{x,y}
        z = x + 3; // in{x,y} out{x,y,z}
        w = y + 5; // sink in{x,y,z} out{x,y,z,w}
        a = b++; // mantem
        y = 0; // out_changed_variables {x,y,z,w} out_propagated_variables {x,z,w} kill{y}
        c = y; // in {x,z,w} out_changed_variables {x,y,z,w} out_propagated_variables {x,z,w}
        c = w + 1;
    }
}