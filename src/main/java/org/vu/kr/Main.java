//package org.vu.kr;
//
//import org.semanticweb.owlapi.model.OWLClass;
//import org.semanticweb.owlapi.model.OWLOntologyCreationException;
//
//import java.util.Set;
//
//public class Main {
//    public static void main(String[] args) {
//        try {
//            // Check if an argument is provided
//            if (args.length < 1) {
//                System.err.println("Please provide the path to the ontology file as an argument.");
//                return;
//            }
//
//            // Get the ontology file path from the command-line arguments
//            String ontologyFile = args[0];
//            System.out.println("Loading ontology from file: " + ontologyFile);
//
//            // Initialize the reasoner with the given ontology file
//            ELReasoner reasoner = new ELReasoner(ontologyFile);
//
//            // Get all classes in the ontology
//            Set<OWLClass> allClasses = reasoner.ontology.getClassesInSignature();
//            System.out.println("Classes in ontology:");
//            for (OWLClass cls : allClasses) {
//                System.out.println("- " + cls.getIRI().getShortForm());
//            }
//
//            // Compute subsumption relationships
//            reasoner.computeSubsumers();
//
//            // Create a class to query
//            OWLClass queryClass = allClasses.iterator().next(); // Use the first class as the query
//            System.out.println("\nQuerying subsumers for: " + queryClass.getIRI().getShortForm());
//
//            // Get and print subsumers
//            Set<OWLClass> subsumers = reasoner.getSubsumers(queryClass);
//            if (subsumers.isEmpty()) {
//                System.out.println("No subsumers found (except the class itself and Thing).");
//            } else {
//                System.out.println("Subsumers:");
//                for (OWLClass subsumer : subsumers) {
//                    if (!subsumer.equals(queryClass) && !subsumer.isOWLThing()) {
//                        System.out.println("- " + subsumer.getIRI().getShortForm());
//                    }
//                }
//            }
//
//            System.out.println("\nOntology statistics:");
//            System.out.println("Total classes: " + allClasses.size());
//            System.out.println("Total axioms: " + reasoner.ontology.getAxiomCount());
//
//        } catch (OWLOntologyCreationException e) {
//            System.err.println("Error loading ontology: " + e.getMessage());
//            e.printStackTrace();
//        } catch (Exception e) {
//            System.err.println("Unexpected error: " + e.getMessage());
//            e.printStackTrace();
//        }
//    }
//}


package org.vu.kr;

import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;

import java.util.Set;

public class Main {
    public static void main(String[] args) {
        try {
            if (args.length < 1) {
                System.err.println("Please provide the path to the ontology file as an argument.");
                return;
            }

            String ontologyFile = args[0];
            System.out.println("Loading ontology from file: " + ontologyFile);

            ELReasoner reasoner = new ELReasoner(ontologyFile);

            Set<OWLClass> allClasses = reasoner.ontology.getClassesInSignature();
            System.out.println("Classes in ontology:");
            for (OWLClass cls : allClasses) {
                System.out.println("- " + cls.getIRI().getShortForm());
            }

            reasoner.computeSubsumers();

            OWLClass queryClass = allClasses.iterator().next(); // Query first class
            System.out.println("\nQuerying subsumers for: " + queryClass.getIRI().getShortForm());

            Set<OWLClass> subsumers = reasoner.getSubsumers(queryClass);
            if (subsumers.isEmpty()) {
                System.out.println("No subsumers found (except the class itself and Thing).");
            } else {
                System.out.println("Subsumers:");
                for (OWLClass subsumer : subsumers) {
                    if (!subsumer.equals(queryClass) && !subsumer.isOWLThing()) {
                        System.out.println("- " + subsumer.getIRI().getShortForm());
                    }
                }
            }

            System.out.println("\nOntology statistics:");
            System.out.println("Total classes: " + allClasses.size());
            System.out.println("Total axioms: " + reasoner.ontology.getAxiomCount());

        } catch (OWLOntologyCreationException e) {
            System.err.println("Error loading ontology: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("Unexpected error: " + e.getMessage());
        }
    }
}
