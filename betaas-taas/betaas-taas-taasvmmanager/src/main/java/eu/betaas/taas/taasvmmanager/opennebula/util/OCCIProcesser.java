/**

Copyright 2014 ATOS SPAIN S.A.

Licensed under the Apache License, Version 2.0 (the License);
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.

Authors :
Francisco Javier Nieto. Atos Research and Innovation, Atos SPAIN SA
@email francisco.nieto@atos.net 
Sergio García Villalonga. Atos Research and Innovation, Atos SPAIN SA
@email sergio.garciavillalonga@atos.net
**/

package eu.betaas.taas.taasvmmanager.opennebula.util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.StringReader;
import java.util.Iterator;
import java.util.List;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.jdom.output.XMLOutputter;
import org.jdom.xpath.XPath;

public class OCCIProcesser {
	public String tags2UpperCase(String payload) throws JDOMException, IOException {
		SAXBuilder builder = new SAXBuilder();
		builder.setValidation(false);
		builder.setIgnoringElementContentWhitespace(true);
		Document xmldoc = builder.build(new StringReader(payload));
		
		XPath xpath = XPath.newInstance("//*");
        List result = xpath.selectNodes(xmldoc);
        Iterator it = result.iterator();
		while(it.hasNext()){
			Element element = (Element)it.next();
			element.setName(element.getName().toUpperCase());
			element.setNamespace(null);
		}
		
		XMLOutputter outputter = new XMLOutputter();
		ByteArrayOutputStream parsed = new ByteArrayOutputStream();
		outputter.output(xmldoc, parsed);
		
		return parsed.toString();
	}
	
	public String tags2LowerCase(String payload) throws JDOMException, IOException{
		SAXBuilder builder = new SAXBuilder();
		builder.setValidation(false);
		builder.setIgnoringElementContentWhitespace(true);
		Document xmldoc = builder.build(new StringReader(payload));
		
		XPath xpath = XPath.newInstance("//*");
        List result = xpath.selectNodes(xmldoc);
        Iterator it = result.iterator();
		while(it.hasNext()){
			Element element = (Element)it.next();
			element.setName(element.getName().toLowerCase());
			element.setNamespace(null);
		}
		
		XMLOutputter outputter = new XMLOutputter();
		ByteArrayOutputStream parsed = new ByteArrayOutputStream();
		outputter.output(xmldoc, parsed);
		
		return parsed.toString();
	}
	
	public String getSaveAsPayload (String original) throws JDOMException, IOException{
		SAXBuilder builder = new SAXBuilder();
		builder.setValidation(false);
		builder.setIgnoringElementContentWhitespace(true);
		Document xmldoc = builder.build(new StringReader(original));
		Document ret    = builder.build(new StringReader(original));
		
		((Element)ret.getContent(0)).removeContent();
		
		XPath xpath = XPath.newInstance("//DISK[SAVE_AS]");
        List result = xpath.selectNodes(xmldoc);
        Iterator it = result.iterator();
		while(it.hasNext()){
			Element element = (Element)it.next();
			element.setNamespace(null);
			ret.getRootElement().addContent((Element)element.clone());
		}
		
		XMLOutputter outputter = new XMLOutputter();
		ByteArrayOutputStream parsed = new ByteArrayOutputStream();
		outputter.output(ret, parsed);
		
		return parsed.toString();
	}
	
	public String getIdFromHref (String href) {
		String uriComponents[] = href.split("/");
		
		return uriComponents[uriComponents.length - 1];
	}
}
