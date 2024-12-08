package org.vu.kr;

import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.parameters.Imports;

public class Main {
    public static void main(String[] args) {
        try {
            String ontologyFilePath = "ontologies/amino-acid.amino-acid-ontology.2.owl.xml";

            ELReasoner reasoner = new ELReasoner(ontologyFilePath);

            // Compute all subsumers
            reasoner.computeSubsumers();

            // Print classes and their subsumers
            for (OWLClass cls : reasoner.ontology.getClassesInSignature(Imports.INCLUDED)) {
                System.out.println("Class: " + cls.getIRI().getShortForm());
                System.out.println("Subsumers:");
                for (OWLClass subsumer : reasoner.getSubsumers(cls)) {
                    System.out.println("  - " + subsumer.getIRI().getShortForm());
                }
                System.out.println();
            }

        } catch (OWLOntologyCreationException e) {
            System.err.println("Error loading ontology: " + e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
