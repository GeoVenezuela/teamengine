package com.occamlab.te;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Iterator;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

public class ListSuites {
  public static Collection getSuites(ArrayList sources) throws Exception {
    LinkedHashMap suites = new LinkedHashMap();
    DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
    dbf.setNamespaceAware(true);
    DocumentBuilder db = dbf.newDocumentBuilder();
    System.setProperty("org.apache.xerces.xni.parser.XMLParserConfiguration","org.apache.xerces.parsers.XIncludeParserConfiguration");
    Iterator it = sources.iterator();
    while (it.hasNext()) {
      File f = (File)it.next();
      File files[];
      if (f.isDirectory()) {
        files = f.listFiles();
      } else {
        files = new File[1];
        files[0] = f;
      }
      for (int i = 0; i < files.length; i++ ) {
        String path = files[i].getAbsolutePath().toLowerCase(); 
        if (path.indexOf(".ctl") > 0 || path.indexOf(".xml") > 0) {
          Document doc = db.parse(files[i]);
          NodeList suiteElements = doc.getElementsByTagNameNS(Test.CTL_NS, "suite");
          for (int j = 0; j < suiteElements.getLength(); j++ ) {
            Suite suite = new Suite((Element)suiteElements.item(j));
            suites.put(suite.getKey(), suite);
          }
        }
      }
    }
    return suites.values();
  }
  
  public static void main(String[] args) throws Exception {
    ArrayList sources = new ArrayList();
    String cmd = "java com.occamlab.te.ListSessions";

    for (int i = 0; i < args.length; i++) {
      if (args[i].startsWith("-cmd=")) {
        cmd = args[i].substring(5);
      } else if (args[i].startsWith("-source=")) {
        sources.add(new File(args[i].substring(8)));
      }
    }
    
    if (sources.size() == 0) {
      System.out.println("Syntax:");
      System.out.println(cmd + " -source={ctlfile|dir} [-source={ctlfile|dir}] ...");
      return;
    }

    Iterator it = getSuites(sources).iterator();
    while (it.hasNext()) {
      Suite suite = (Suite)it.next();
      System.out.print("Suite " + suite.getPrefix() + ":" + suite.getLocalName());
      System.out.println(" (" + suite.getKey() + ")");
      System.out.println(suite.getTitle());
      String desc = suite.getDescription();
      if (desc != null) {
        System.out.println(desc);
      }
      String link = suite.getLink();
      if (link != null) {
        System.out.println("See " + link);
      }
      System.out.println();
    }
  }
}
