package parser;

import application.Application;
import application.ExitCode;
import common.Configuration;
import common.DLSyntax;
import common.Prefixes;
import models.Abducibles;
import org.semanticweb.owlapi.model.*;
import reasoner.Loader;

import java.util.HashSet;
import java.util.Set;
import java.util.logging.Logger;

public class AbduciblesParser {

    private Logger logger = Logger.getLogger(ObservationParser.class.getSimpleName());
    private Loader loader;

    public AbduciblesParser(Loader loader) {
        this.loader = loader;
    }

    public Abducibles parse(){

        Set<OWLClass> classes = new HashSet<>();
        Set<OWLNamedIndividual> individuals = new HashSet<>();
        Set<OWLObjectProperty> roles = new HashSet<>();

        for (String abd: Configuration.ABDUCIBLES){
            String[] prefix_obj;

            if (!abd.contains(DLSyntax.DELIMITER_ONTOLOGY)){
                prefix_obj = abd.split(DLSyntax.DELIMITER_ASSERTION);
                if (!Prefixes.prefixes.containsKey(prefix_obj[0])){
                    System.err.println("Prefix " + prefix_obj[0] + " in abducible -a " + abd + " is unknown, define the prefix with -p parameter.");
                    Application.finish(ExitCode.ERROR);
                }
                abd = Prefixes.prefixes.get(prefix_obj[0]).concat(prefix_obj[1]);
            }

            prefix_obj = abd.split(DLSyntax.DELIMITER_ONTOLOGY);
            if (Character.isUpperCase(prefix_obj[1].charAt(0))){
                classes.add(create_class(abd));
            }
            else if (prefix_obj[1].endsWith("()")){
                roles.add(create_role(abd.substring(0, abd.length() - 2)));
            }
            else{
                individuals.add(create_individual(abd));
            }
        }
        if (classes.isEmpty()){ //&& roles.isEmpty()){
            return new Abducibles(loader);
        }
        Set<OWLNamedIndividual> observation_inds = loader.getObservation().getOwlAxiom().getIndividualsInSignature();
        for (OWLNamedIndividual ind: observation_inds){
            if (!individuals.contains(ind)) {
                individuals.add(ind);
            }
        }
        return new Abducibles(loader, individuals, classes, roles);
    }

    private OWLClass create_class(String abd){
        return loader.getDataFactory().getOWLClass(IRI.create(abd));
    }

    private OWLNamedIndividual create_individual(String abd){
        return loader.getDataFactory().getOWLNamedIndividual(IRI.create(abd));
    }

    private OWLObjectProperty create_role(String abd){
        return loader.getDataFactory().getOWLObjectProperty(IRI.create(abd));
    }
}
