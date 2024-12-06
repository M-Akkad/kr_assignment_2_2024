//package org.vu.kr;
//
//import org.semanticweb.owlapi.apibinding.OWLManager;
//import org.semanticweb.owlapi.model.*;
//
//import java.io.File;
//import java.util.HashMap;
//import java.util.HashSet;
//import java.util.Map;
//import java.util.Set;
//
//public class ELReasoner {
//    final OWLOntology ontology;
//    private final Map<OWLClass, Set<OWLClass>> subsumers;
//    private final OWLDataFactory factory;
//
//    public ELReasoner(String ontologyFile) throws OWLOntologyCreationException {
//        OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
//        this.ontology = manager.loadOntologyFromOntologyDocument(new File(ontologyFile));
//        this.factory = manager.getOWLDataFactory();
//        this.subsumers = new HashMap<>();
//        initializeSubsumers();
//    }
//
//    private void initializeSubsumers() {
//        OWLClass owlThing = factory.getOWLThing();
//        for (OWLClass cls : ontology.getClassesInSignature()) {
//            Set<OWLClass> subsumerSet = new HashSet<>();
//            subsumerSet.add(cls);
//            subsumerSet.add(owlThing);
//            subsumers.put(cls, subsumerSet);
//        }
//    }
//
//    public void computeSubsumers() {
//        boolean changed;
//        do {
//            changed = false;
//            changed |= applyTransitivityRule();
//            changed |= applyConjunctionRule();
//            changed |= applyExistentialRule();
//        } while (changed);
//    }
//
//    private boolean applyTransitivityRule() {
//        boolean changed = false;
//        for (OWLClass cls : subsumers.keySet()) {
//            Set<OWLClass> currentSubsumers = new HashSet<>(subsumers.get(cls));
//            for (OWLClass subsumer : currentSubsumers) {
//                Set<OWLClass> indirectSubsumers = subsumers.get(subsumer);
//                if (indirectSubsumers != null) {
////                    System.out.println("Applying Transitivity Rule: Adding subsumers of " + subsumer + " to " + cls);
//                    changed |= subsumers.get(cls).addAll(indirectSubsumers);
//                }
//            }
//        }
//        return changed;
//    }
//
//    private boolean applyConjunctionRule() {
//        boolean changed = false;
//        for (OWLAxiom axiom : ontology.getAxioms()) {
//            if (axiom instanceof OWLSubClassOfAxiom) {
//                OWLSubClassOfAxiom subClassAxiom = (OWLSubClassOfAxiom) axiom;
//                if (subClassAxiom.getSuperClass() instanceof OWLObjectIntersectionOf) {
//                    OWLClass subClass = subClassAxiom.getSubClass().asOWLClass();
//                    OWLObjectIntersectionOf intersection = (OWLObjectIntersectionOf) subClassAxiom.getSuperClass();
//                    for (OWLClassExpression conjunct : intersection.getOperands()) {
//                        if (conjunct instanceof OWLClass) {
////                            System.out.println("Applying Conjunction Rule: " + subClass + " ⊑ " + conjunct);
//                            changed |= subsumers.get(subClass).add((OWLClass) conjunct);
//                        }
//                    }
//                }
//            }
//        }
//        return changed;
//    }
//
//    private boolean applyExistentialRule() {
//        boolean changed = false;
//        for (OWLAxiom axiom : ontology.getAxioms()) {
//            if (axiom instanceof OWLSubClassOfAxiom) {
//                OWLSubClassOfAxiom subClassAxiom = (OWLSubClassOfAxiom) axiom;
//                if (subClassAxiom.getSubClass() instanceof OWLClass &&
//                        subClassAxiom.getSuperClass() instanceof OWLObjectSomeValuesFrom) {
//                    OWLClass subClass = (OWLClass) subClassAxiom.getSubClass();
//                    OWLObjectSomeValuesFrom existential = (OWLObjectSomeValuesFrom) subClassAxiom.getSuperClass();
//                    if (existential.getFiller() instanceof OWLClass) {
//                        OWLClass filler = (OWLClass) existential.getFiller();
////                        System.out.println("Applying Existential Rule: " + subClass + " ⊑ ∃" + existential.getProperty() + "." + existential.getFiller());
//                        changed |= subsumers.get(subClass).addAll(subsumers.get(filler));
//                    }
//                }
//            }
//        }
//        return changed;
//    }
//
//    public Set<OWLClass> getSubsumers(OWLClass cls) {
//        return new HashSet<>(subsumers.getOrDefault(cls, new HashSet<>()));
//    }
//
//    // Main method for testing
//    public static void main(String[] args) {
//        try {
//            // Replace with your ontology file path
//            String ontologyFile = "path/to/your/ontology.owl";
//            ELReasoner reasoner = new ELReasoner(ontologyFile);
//
//            // Compute all subsumption relationships
//            reasoner.computeSubsumers();
//
//            // Get and print subsumers for a specific class
//            OWLDataFactory factory = OWLManager.createOWLOntologyManager().getOWLDataFactory();
//            OWLClass testClass = factory.getOWLClass(IRI.create("http://example.org/ontology#YourClassName"));
//
//            Set<OWLClass> subsumers = reasoner.getSubsumers(testClass);
//            System.out.println("Subsumers:");
//            for (OWLClass subsumer : subsumers) {
//                System.out.println(subsumer.getIRI().getShortForm());
//            }
//
//        } catch (OWLOntologyCreationException e) {
//            e.printStackTrace();
//        }
//    }
//}


package org.vu.kr;

import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.*;

import java.io.File;
import java.util.*;

public class ELReasoner {
    final OWLOntology ontology;
    private final Map<OWLClass, Set<OWLClass>> subsumers;
    private final OWLDataFactory factory;

    public ELReasoner(String ontologyFile) throws OWLOntologyCreationException {
        OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
        this.ontology = manager.loadOntologyFromOntologyDocument(new File(ontologyFile));
        this.factory = manager.getOWLDataFactory();
        this.subsumers = new HashMap<>();
        initializeSubsumers();
    }

    private void initializeSubsumers() {
        OWLClass owlThing = factory.getOWLThing();
        for (OWLClass cls : ontology.getClassesInSignature()) {
            Set<OWLClass> subsumerSet = new HashSet<>();
            subsumerSet.add(cls); // Each class subsumes itself
            subsumerSet.add(owlThing); // Each class is subsumed by Thing
            subsumers.put(cls, subsumerSet);
        }
    }

    public void computeSubsumers() {
        boolean changed;
        do {
            changed = false;
            changed |= applyTransitivityRule();
            changed |= applyConjunctionRule();
            changed |= applyExistentialRule();
            changed |= applyEquivalentClassesRule();
        } while (changed);
        // Call handleDisjointClasses after all other rules
        handleDisjointClasses();
    }

    private boolean applyTransitivityRule() {
        boolean changed = false;
        for (OWLClass cls : subsumers.keySet()) {
            Set<OWLClass> currentSubsumers = new HashSet<>(subsumers.get(cls));
            for (OWLClass subsumer : currentSubsumers) {
                Set<OWLClass> indirectSubsumers = subsumers.get(subsumer);
                if (indirectSubsumers != null) {
                    boolean added = subsumers.get(cls).addAll(indirectSubsumers);
                    if (added) {
                        System.out.println("Transitivity: Updated subsumers for " + cls);
                        changed = true;
                    }
                }
            }
        }
        return changed;
    }

    private boolean applyConjunctionRule() {
        boolean changed = false;
        for (OWLAxiom axiom : ontology.getAxioms(AxiomType.SUBCLASS_OF)) {
            if (axiom instanceof OWLSubClassOfAxiom) {
                OWLSubClassOfAxiom subClassAxiom = (OWLSubClassOfAxiom) axiom;
                if (subClassAxiom.getSuperClass() instanceof OWLObjectIntersectionOf) {
                    OWLClass subClass = subClassAxiom.getSubClass().asOWLClass();
                    OWLObjectIntersectionOf intersection = (OWLObjectIntersectionOf) subClassAxiom.getSuperClass();
                    for (OWLClassExpression conjunct : intersection.getOperands()) {
                        if (conjunct instanceof OWLClass) {
                            boolean added = subsumers.get(subClass).add((OWLClass) conjunct);
                            if (added) {
                                System.out.println("Conjunction: " + subClass + " subsumes " + conjunct);
                                changed = true;
                            }
                        }
                    }
                }
            }
        }
        return changed;
    }

    private boolean applyExistentialRule() {
        boolean changed = false;
        for (OWLAxiom axiom : ontology.getAxioms(AxiomType.SUBCLASS_OF)) {
            if (axiom instanceof OWLSubClassOfAxiom) {
                OWLSubClassOfAxiom subClassAxiom = (OWLSubClassOfAxiom) axiom;
                if (subClassAxiom.getSubClass() instanceof OWLClass &&
                        subClassAxiom.getSuperClass() instanceof OWLObjectSomeValuesFrom) {
                    OWLClass subClass = (OWLClass) subClassAxiom.getSubClass();
                    OWLObjectSomeValuesFrom existential = (OWLObjectSomeValuesFrom) subClassAxiom.getSuperClass();
                    if (existential.getFiller() instanceof OWLClass) {
                        OWLClass filler = (OWLClass) existential.getFiller();
                        boolean added = subsumers.get(subClass).addAll(subsumers.get(filler));
                        if (added) {
                            System.out.println("Existential: " + subClass + " subsumes " + filler);
                            changed = true;
                        }
                    }
                }
            }
        }
        return changed;
    }

    private boolean applyEquivalentClassesRule() {
        boolean changed = false;
        for (OWLAxiom axiom : ontology.getAxioms(AxiomType.EQUIVALENT_CLASSES)) {
            if (axiom instanceof OWLEquivalentClassesAxiom) {
                OWLEquivalentClassesAxiom eqAxiom = (OWLEquivalentClassesAxiom) axiom;
                Set<OWLClass> equivalentClasses = eqAxiom.getNamedClasses();
                for (OWLClass cls : equivalentClasses) {
                    for (OWLClass equivalent : equivalentClasses) {
                        if (!cls.equals(equivalent)) {
                            boolean added = subsumers.get(cls).addAll(subsumers.get(equivalent));
                            if (added) {
                                System.out.println("Equivalence: " + cls + " is equivalent to " + equivalent);
                                changed = true;
                            }
                        }
                    }
                }
            }
        }
        return changed;
    }

    /**
     * Method to handle DisjointClasses axioms.
     * Detects individuals that belong to multiple disjoint classes and reports inconsistencies.
     */
    private void handleDisjointClasses() {
        System.out.println("Checking for disjoint class inconsistencies...");
        for (OWLDisjointClassesAxiom disjointAxiom : ontology.getAxioms(AxiomType.DISJOINT_CLASSES)) {
            Set<OWLClass> disjointClasses = disjointAxiom.getClassesInSignature();
            List<OWLClass> disjointList = new ArrayList<>(disjointClasses);

            for (int i = 0; i < disjointList.size(); i++) {
                for (int j = i + 1; j < disjointList.size(); j++) {
                    OWLClass classA = disjointList.get(i);
                    OWLClass classB = disjointList.get(j);

                    // Retrieve instances of the disjoint classes
                    Set<OWLNamedIndividual> instancesA = getInstances(classA);
                    Set<OWLNamedIndividual> instancesB = getInstances(classB);

                    // Check for common instances
                    Set<OWLNamedIndividual> commonInstances = new HashSet<>(instancesA);
                    commonInstances.retainAll(instancesB);

                    if (!commonInstances.isEmpty()) {
                        System.out.println("Inconsistency detected! Individuals belong to disjoint classes:");
                        for (OWLNamedIndividual individual : commonInstances) {
                            System.out.println(" - Individual: " + individual + " in " + classA + " and " + classB);
                        }
                    }
                }
            }
        }
    }

    /**
     * Retrieve instances of a given OWL class.
     */
    private Set<OWLNamedIndividual> getInstances(OWLClass owlClass) {
        Set<OWLNamedIndividual> instances = new HashSet<>();
        for (OWLAxiom axiom : ontology.getAxioms(AxiomType.CLASS_ASSERTION)) {
            if (axiom instanceof OWLClassAssertionAxiom) {
                OWLClassAssertionAxiom classAssertion = (OWLClassAssertionAxiom) axiom;
                if (classAssertion.getClassExpression().equals(owlClass)) {
                    instances.add(classAssertion.getIndividual().asOWLNamedIndividual());
                }
            }
        }
        return instances;
    }

    public Set<OWLClass> getSubsumers(OWLClass cls) {
        return new HashSet<>(subsumers.getOrDefault(cls, new HashSet<>()));
    }
}


