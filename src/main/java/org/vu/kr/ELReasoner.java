package org.vu.kr;

import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.*;

import java.io.File;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

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
            subsumerSet.add(cls);
            subsumerSet.add(owlThing);
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
        } while (changed);
    }

    private boolean applyTransitivityRule() {
        boolean changed = false;
        for (OWLClass cls : subsumers.keySet()) {
            Set<OWLClass> currentSubsumers = new HashSet<>(subsumers.get(cls));
            for (OWLClass subsumer : currentSubsumers) {
                Set<OWLClass> indirectSubsumers = subsumers.get(subsumer);
                if (indirectSubsumers != null) {
                    changed |= subsumers.get(cls).addAll(indirectSubsumers);
                }
            }
        }
        return changed;
    }

    private boolean applyConjunctionRule() {
        boolean changed = false;
        for (OWLAxiom axiom : ontology.getAxioms()) {
            if (axiom instanceof OWLSubClassOfAxiom) {
                OWLSubClassOfAxiom subClassAxiom = (OWLSubClassOfAxiom) axiom;
                if (subClassAxiom.getSuperClass() instanceof OWLObjectIntersectionOf) {
                    OWLClass subClass = subClassAxiom.getSubClass().asOWLClass();
                    OWLObjectIntersectionOf intersection = (OWLObjectIntersectionOf) subClassAxiom.getSuperClass();
                    for (OWLClassExpression conjunct : intersection.getOperands()) {
                        if (conjunct instanceof OWLClass) {
                            changed |= subsumers.get(subClass).add((OWLClass) conjunct);
                        }
                    }
                }
            }
        }
        return changed;
    }

    private boolean applyExistentialRule() {
        boolean changed = false;
        for (OWLAxiom axiom : ontology.getAxioms()) {
            if (axiom instanceof OWLSubClassOfAxiom) {
                OWLSubClassOfAxiom subClassAxiom = (OWLSubClassOfAxiom) axiom;
                if (subClassAxiom.getSubClass() instanceof OWLClass &&
                        subClassAxiom.getSuperClass() instanceof OWLObjectSomeValuesFrom) {
                    OWLClass subClass = (OWLClass) subClassAxiom.getSubClass();
                    OWLObjectSomeValuesFrom existential = (OWLObjectSomeValuesFrom) subClassAxiom.getSuperClass();
                    if (existential.getFiller() instanceof OWLClass) {
                        OWLClass filler = (OWLClass) existential.getFiller();
                        changed |= subsumers.get(subClass).addAll(subsumers.get(filler));
                    }
                }
            }
        }
        return changed;
    }

    public Set<OWLClass> getSubsumers(OWLClass cls) {
        return new HashSet<>(subsumers.getOrDefault(cls, new HashSet<>()));
    }

    // Main method for testing
    public static void main(String[] args) {
        try {
            // Replace with your ontology file path
            String ontologyFile = "path/to/your/ontology.owl";
            ELReasoner reasoner = new ELReasoner(ontologyFile);

            // Compute all subsumption relationships
            reasoner.computeSubsumers();

            // Get and print subsumers for a specific class
            OWLDataFactory factory = OWLManager.createOWLOntologyManager().getOWLDataFactory();
            OWLClass testClass = factory.getOWLClass(IRI.create("http://example.org/ontology#YourClassName"));

            Set<OWLClass> subsumers = reasoner.getSubsumers(testClass);
            System.out.println("Subsumers:");
            for (OWLClass subsumer : subsumers) {
                System.out.println(subsumer.getIRI().getShortForm());
            }

        } catch (OWLOntologyCreationException e) {
            e.printStackTrace();
        }
    }
}