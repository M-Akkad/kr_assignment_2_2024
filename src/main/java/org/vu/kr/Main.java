package org.vu.kr;

import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.parameters.Imports;

import java.util.Set;

public class Main {
    public static void main(String[] args) {
        try {
            if (args.length < 2) {
                System.err.println("Usage: PROGRAMM_NAME ONTOLOGY_FILE CLASS_NAME");
                return;
            }

            String ontologyFilePath = args[0];
            String className = args[1];

            System.out.println("Loading ontology from file: " + ontologyFilePath);

            ELReasoner reasoner = new ELReasoner(ontologyFilePath);

            // Compute all subsumers
            reasoner.computeSubsumers();

            // Find the specific class by its IRI short form
            OWLClass queryClass = null;
            for (OWLClass cls : reasoner.ontology.getClassesInSignature(Imports.INCLUDED)) {
                if (cls.getIRI().getShortForm().equals(className)) {
                    queryClass = cls;
                    break;
                }
            }

            if (queryClass == null) {
                System.err.println("Class not found: " + className);
                return;
            }

            // Query and print the subsumers for the given class
            System.out.println("\nQuerying subsumers for: " + className);
            Set<OWLClass> subsumers = reasoner.getSubsumers(queryClass);

            if (subsumers.isEmpty()) {
                System.out.println("No subsumers found for: " + className);
            } else {
                System.out.println("Subsumers:");
                for (OWLClass subsumer : subsumers) {
                    System.out.println("  - " + reasoner.getClassLabel(subsumer));
                }
            }

        } catch (OWLOntologyCreationException e) {
            System.err.println("Error loading ontology: " + e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
