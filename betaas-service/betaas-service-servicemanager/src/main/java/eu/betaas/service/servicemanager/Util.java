/*
Copyright 2014-2015 Intecs Spa

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

 Unless required by applicable law or agreed to in writing, software
 distributed under the License is distributed on an "AS IS" BASIS,
 WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 See the License for the specific language governing permissions and
 limitations under the License.
*/

package eu.betaas.service.servicemanager;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * It contains some utility functions
 * @author Intecs
 */
public class Util {

	/**
	 * Loads a file from the specified address
	 * @param fileName name of the file to load
	 * @return the file content
	 * @throws Exception
	 */
	public static String loadFile(String fileName) throws Exception {
		InputStream is = null;
		String s, res;
		boolean start;

		res = "";
		try {
			if (fileName.startsWith("http://")) {
				is = new URL(fileName).openStream();
			} else {
				is = new FileInputStream(fileName); 
			}
			BufferedReader reader = new BufferedReader(new InputStreamReader(is));

			start = true;
			while ((s = reader.readLine()) != null) {
				res += (start ? "":"\n") + s;
				start = false;
			}

		} finally {
			try {
				is.close();
			} catch (IOException ioe) {}
		}

		return res;
	}


	/**
	 * Retrieve the text content of a specified tag
	 * @param nl the node list where to search for the tag
	 * @param path the path in the form nodeName1/nodeName2/.../nodeNameN.
	 *        At each level the first occurrence of the node matching the path 
	 *        is considered (i.e. [0] as Xpath)
	 * @return the text content or null if the path cannot be matched
	 */
	public static String getContent(NodeList nl, String path) {
		if ((nl == null) || (path == null)) return null;
		if (nl.getLength() == 0) return null;
		String[] names = path.split("/");
		if (names.length == 0) return null;

		return getContent(nl, names, 0);
	}
	


	/**
	 * Get the text at specified path starting from the specified node
	 * @param n
	 * @param path
	 * @return
	 */
	public static String getContent(Node n, String path) {
		if (n == null) return null;
		return getContent(n.getChildNodes(), path);
	}
	
	
	/**
	 * Get the list of nodes matching the given path in the given node list
	 * @param nl
	 * @param path
	 * @return
	 */
	public static ArrayList<Node> getNodeList(NodeList nl, String path) {
		if ((nl == null) || (path == null)) return null;
		if (nl.getLength() == 0) return null;
		String[] names = path.split("/");
		if (names.length == 0) return null;
		
		return getNodeList(nl, names, 0);
	}

	
	/**
	 * Search for the content specified by the tag names array starting from
	 * the specified position 
	 * @param nl
	 * @param names
	 * @param pos
	 * @return
	 */
	private static String getContent(NodeList nl, String[] names, int pos) {
		if ((nl == null) || (names.length-pos == 0)) return null;
		if (nl.getLength() == 0) return null;
		
		for (int i=0; i < nl.getLength(); i++) {
			if (nl.item(i).getNodeName().compareTo(names[pos]) == 0) {

				if (names.length-pos == 1) {
					return nl.item(i).getTextContent();
				} else {
					return getContent(nl.item(i).getChildNodes(), names, pos+1);
				}
			}
		}
		return null;
	}

	
	/**
	 * Get the list of nodes matching the given array of tag names in the given node list
	 * @param nl
	 * @param names
	 * @param pos
	 * @return
	 */
	private static ArrayList<Node> getNodeList(NodeList nl, String[] names, int pos) {
		if ((nl == null) || (names.length-pos == 0)) return null;
		if (nl.getLength() == 0) return null;
		
		ArrayList<Node> result = null;
		
		if (names.length-pos == 1) result = new ArrayList<Node>();
		
		for (int i=0; i < nl.getLength(); i++) {
			if (nl.item(i).getNodeName().compareTo(names[pos]) == 0) {

				if (names.length-pos == 1) {
					result.add(nl.item(i));
				} else {
					return getNodeList(nl.item(i).getChildNodes(), names, pos+1);
				}
			}
		}
		return result;
	}
		
}
