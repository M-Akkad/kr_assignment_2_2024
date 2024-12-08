package org.vu.kr;

import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.*;
import org.semanticweb.owlapi.model.parameters.Imports;

import java.io.File;
import java.util.*;

public class ELReasoner {
    final OWLOntology ontology;
    private final Map<OWLClass, Set<OWLClass>> subsumers;
    private final OWLDataFactory factory;

    public ELReasoner(String ontologyFilePath) throws OWLOntologyCreationException {
        OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
        File ontologyFile = new File(ontologyFilePath);

        if (!ontologyFile.exists()) {
            throw new IllegalArgumentException("Ontology file not found: " + ontologyFilePath);
        }

        this.ontology = manager.loadOntologyFromOntologyDocument(ontologyFile);
        this.factory = manager.getOWLDataFactory();
        this.subsumers = new HashMap<>();

        initializeSubsumers();
    }

    private void initializeSubsumers() {
        OWLClass owlThing = factory.getOWLThing();
        for (OWLClass cls : ontology.getClassesInSignature(Imports.INCLUDED)) {
            if (!cls.isAnonymous()) {
                Set<OWLClass> subsumerSet = new HashSet<>();
                subsumerSet.add(cls);
                subsumerSet.add(owlThing);
                subsumers.put(cls, subsumerSet);
            }
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
        for (OWLAxiom axiom : ontology.getAxioms(Imports.INCLUDED)) {
            if (axiom instanceof OWLSubClassOfAxiom) {
                OWLSubClassOfAxiom subClassAxiom = (OWLSubClassOfAxiom) axiom;
                OWLClassExpression subClassExpression = subClassAxiom.getSubClass();
                OWLClassExpression superClassExpression = subClassAxiom.getSuperClass();

                if (subClassExpression instanceof OWLClass) {
                    OWLClass subClass = (OWLClass) subClassExpression;

                    // Handle OWLObjectIntersectionOf
                    if (superClassExpression instanceof OWLObjectIntersectionOf) {
                        OWLObjectIntersectionOf intersection = (OWLObjectIntersectionOf) superClassExpression;
                        for (OWLClassExpression operand : intersection.getOperands()) {
                            if (operand instanceof OWLClass) {
                                changed |= subsumers.get(subClass).add((OWLClass) operand);
                            }
                        }
                    }

                    // Handle OWLObjectUnionOf
                    if (superClassExpression instanceof OWLObjectUnionOf) {
                        OWLObjectUnionOf union = (OWLObjectUnionOf) superClassExpression;
                        for (OWLClassExpression operand : union.getOperands()) {
                            if (operand instanceof OWLClass) {
                                changed |= subsumers.get(subClass).add((OWLClass) operand);
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
        for (OWLAxiom axiom : ontology.getAxioms(Imports.INCLUDED)) {
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
}
