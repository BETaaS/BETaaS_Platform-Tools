//Copyright 2014-2015 Tecnalia.
//
//Licensed under the Apache License, Version 2.0 (the "License");
//you may not use this file except in compliance with the License.
//You may obtain a copy of the License at
//
//       http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

// BETaaS - Building the Environment for the Things as a Service
//
// Component: Context Manager, TaaS Module
// Responsible: Tecnalia
package eu.betaas.taas.contextmanager.onto.classesExt.semantic.data.skos;

import com.hp.hpl.jena.rdf.model.ResourceFactory;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.Property;

public class Skos {

    public static Property semanticRelation = ResourceFactory.createProperty("http://www.w3.org/2004/02/skos/core#"+"semanticRelation");
    public static Property broader = ResourceFactory.createProperty("http://www.w3.org/2004/02/skos/core#"+"broader");
    public static Property narrower = ResourceFactory.createProperty("http://www.w3.org/2004/02/skos/core#"+"narrower");
    public static Property related = ResourceFactory.createProperty("http://www.w3.org/2004/02/skos/core#"+"related");
    public static Property broaderGeneric = ResourceFactory.createProperty("http://www.w3.org/2004/02/skos/core#"+"broaderGeneric");
    public static Property broaderInstantive = ResourceFactory.createProperty("http://www.w3.org/2004/02/skos/core#"+"broaderInstantive");
    public static Property broaderPartitive = ResourceFactory.createProperty("http://www.w3.org/2004/02/skos/core#"+"broaderPartitive");
    public static Property narrowerGeneric = ResourceFactory.createProperty("http://www.w3.org/2004/02/skos/core#"+"narrowerGeneric");
    public static Property narrowerInstantive = ResourceFactory.createProperty("http://www.w3.org/2004/02/skos/core#"+"narrowerInstantive");
    public static Property narrowerPartitive = ResourceFactory.createProperty("http://www.w3.org/2004/02/skos/core#"+"narrowerPartitive");
    public static Property relatedPartOf = ResourceFactory.createProperty("http://www.w3.org/2004/02/skos/core#"+"relatedPartOf");
    public static Property relatedHasPart = ResourceFactory.createProperty("http://www.w3.org/2004/02/skos/core#"+"relatedHasPart");

    public static Property externalID = ResourceFactory.createProperty("http://www.w3.org/2004/02/skos/core#"+"externalID");
    public static Property prefLabel = ResourceFactory.createProperty("http://www.w3.org/2004/02/skos/core#"+"prefLabel");
    public static Property altLabel = ResourceFactory.createProperty("http://www.w3.org/2004/02/skos/core#"+"altLabel");
    public static Property prefSymbol = ResourceFactory.createProperty("http://www.w3.org/2004/02/skos/core#"+"prefSymbol");
    public static Property altSymbol = ResourceFactory.createProperty("http://www.w3.org/2004/02/skos/core#"+"altSymbol");
    public static Property scopeNote = ResourceFactory.createProperty("http://www.w3.org/2004/02/skos/core#"+"scopeNote");
    public static Property definition = ResourceFactory.createProperty("http://www.w3.org/2004/02/skos/core#"+"definition");
    public static Property example = ResourceFactory.createProperty("http://www.w3.org/2004/02/skos/core#"+"example");

    public static Property inScheme = ResourceFactory.createProperty("http://www.w3.org/2004/02/skos/core#"+"inScheme");

    public static Resource Concept = ResourceFactory.createResource("http://www.w3.org/2004/02/skos/core#"+"Concept");
    public static Resource TopConcept = ResourceFactory.createResource("http://www.w3.org/2004/02/skos/core#"+"TopConcept");
    public static Resource ConceptScheme = ResourceFactory.createResource("http://www.w3.org/2004/02/skos/core#"+"ConceptScheme");

    public static Resource Array = ResourceFactory.createResource("http://www.w3.org/2004/02/skos/core#"+"Array");

    public static Property array = ResourceFactory.createProperty("http://www.w3.org/2004/02/skos/core#"+"array");
    public static Property members = ResourceFactory.createProperty("http://www.w3.org/2004/02/skos/core#"+"members");
    public static Property ordered = ResourceFactory.createProperty("http://www.w3.org/2004/02/skos/core#"+"ordered");

}
