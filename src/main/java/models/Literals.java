package models;

import common.Printer;
import org.semanticweb.owlapi.model.OWLAxiom;
import sun.awt.image.ImageWatched;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Literals {

    private Set<OWLAxiom> owlAxioms;

    public Literals() {
        this.owlAxioms = new HashSet<>();
    }

    public Literals(Set<OWLAxiom> owlAxioms) {
        this.owlAxioms = owlAxioms;
    }

    public Set<OWLAxiom> getOwlAxioms() {
        return owlAxioms;
    }

    public void addLiterals(Literals literals) {
        this.owlAxioms.addAll(literals.getOwlAxioms());
    }

    @Override
    public String toString() {
        StringBuilder result = new StringBuilder();
        for (OWLAxiom owlAxiom : owlAxioms) {
            result.append(Printer.print(owlAxiom)).append(";");
        }
        return result.toString();
    }

    public void removeLiterals(List<OWLAxiom> literals){
        owlAxioms.removeAll(literals);
    }

    public void removeLiteral(OWLAxiom literal) {owlAxioms.remove(literal); }

    public void addLiterals(List<OWLAxiom> literals){
        owlAxioms.addAll(literals);
    }

    public boolean contains(OWLAxiom axiom) { return owlAxioms.contains(axiom); }
}
