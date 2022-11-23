package com.ejavier.dm;

import java.io.File;
import java.io.FileInputStream;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Scanner;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class DataMaskingMain {
	
	public static void main(String arg[]) {
		
		DataMaskingMain dmMain = new DataMaskingMain();
		dmMain.process();
		
	}
	
	private void process() {
		
		HashMap<String, Object> rwacXML = loadXML();
		
		//Read through the data file
		FileInputStream inputStream = null;
		Scanner sc = null;
		int ctr=0;
		boolean firstline = true;
		try {
			
			String filename = (String)rwacXML.get("filename");
			String output = (String)rwacXML.get("output");
			
			RandomAccessFile stream = new RandomAccessFile(output, "rw");
			FileChannel channel = stream.getChannel();
			
			ArrayList fieldList = (ArrayList)rwacXML.get("fields");
			inputStream = new FileInputStream(filename);
			sc = new Scanner(inputStream, "UTF-8");
			
			ArrayList maskedList = new ArrayList();
			
			//Loop through the file
			while (sc.hasNext()) {
				
				
				//Read file by line
				String line = sc.nextLine();
				System.out.println(line);
				String tmp = "";
				
				if(firstline == true) {
					firstline = false;
				}else {
					tmp+="\n";
				}
				
				if(line!=null && line.length() > 20) {
				
				//loop through the fields
				ArrayList fieldsRow = new ArrayList();
				Iterator itr = fieldList.iterator();
				while (itr.hasNext()) {
					DmField dmField = (DmField)itr.next();
					String fieldValue = line.substring(dmField.getStart(), dmField.getEnd());
					
					//System.out.println(dmField.getFieldname() + "-->" +dmField.isStatic());
					boolean isStatic = dmField.isStatic();
					//Masking Logic Here!
					if(isStatic) {
						String replacement = dmField.getReplacement();
						if(replacement!=null && !replacement.isEmpty()) {
							int padSize = dmField.getLength() - replacement.length(); 	
							fieldValue = pad(padSize, replacement, dmField.isNumeric());
						}
					}else {
						
						String className=dmField.getReplacement();
						
						Class c = Class.forName(className);
						
						MaskingLogicInterface mli = (MaskingLogicInterface) c.getConstructor().newInstance();
						
						System.out.println("mli " + mli.doProcess(fieldList, dmField, line));
						
						fieldValue = mli.doProcess(fieldList, dmField, line);
						
					}
					
					fieldsRow.add(fieldValue);
					
				}
				
				maskedList.add(fieldsRow);
				
				//write to file
			   
			   for(int ctr1=0; ctr1<fieldsRow.size(); ctr1++) {
				   tmp+=fieldsRow.get(ctr1);
			   }
			  
			   
			   
				}else { tmp += line;} //line length > 20
				
				byte[] strBytes = tmp.getBytes();
				ByteBuffer buffer = ByteBuffer.allocate(strBytes.length);
				buffer.put(strBytes);
				buffer.flip();
				channel.write(buffer);
				
				tmp = "";
				
			}
			
			logMaskedList(maskedList);
			stream.close();
			channel.close();
			
			if (sc.ioException() != null) {
				throw sc.ioException();
			}
					
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			
		}
		
	}
	
	private void logMaskedList(ArrayList maskedList) {
		
		for(int ctr=0; ctr<maskedList.size(); ctr++) {
			ArrayList fieldsList = (ArrayList)maskedList.get(ctr);
			
			for (int ctr1=0; ctr1<fieldsList.size(); ctr1++) {
				System.out.print(fieldsList.get(ctr1));
			}
			System.out.println("");
		}
	}
	
	private String pad(int size, String value, boolean isNumeric) {
		String output = "";
		String pad = "";
		
		for (int ctr=0; ctr<size; ctr++) {
			if (isNumeric) 
				pad+="0";
			else
				pad+=" ";
		}
		if (isNumeric) 
			output=pad+value;
		else
			output=value+pad;

		return output;
		
	}
	
    private HashMap<String, Object> loadXML() {
		
    	/** Change this **/
		File xmlFile = new File("/Users/ejavier/eclipse-workspace/dm/src/resources/rwac.xml");

		
		HashMap<String,Object> output = new HashMap<String,Object>(); 
		ArrayList<DmField> fieldsList = new ArrayList<DmField>();
		
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();

		int strPosition = 0;
		
		try {
			 
			dbf.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);
			DocumentBuilder db = dbf.newDocumentBuilder();
			Document doc = db.parse(xmlFile);
			
			doc.getDocumentElement().normalize();
			
			NodeList filenameNode = doc.getElementsByTagName("filename");
			String filename = filenameNode.item(0).getTextContent();
			output.put("filename", filename);
			NodeList outputNode = doc.getElementsByTagName("output");
			String outputFile = outputNode.item(0).getTextContent();
			output.put("output", outputFile);
			
			NodeList list = doc.getElementsByTagName("field");
			
			for (int ctr = 0; ctr < list.getLength(); ctr++) {
				
				Node node = list.item(ctr);
				
				if(node.getNodeType() == Node.ELEMENT_NODE) {
					
					Element element = (Element) node;
					
					String fieldName = element.getAttribute("name");
					String fieldLength = element.getAttribute("length");
					String fieldIsStatic = element.getAttribute("isStatic");
					String fieldIsNumeric = element.getAttribute("isNumeric");
					String fieldReplacement = element.getAttribute("replacement");
					
					
					DmField df = new DmField();
					df.setFieldname(fieldName);
					df.setLength(Integer.valueOf(fieldLength));
					df.setStatic(Boolean.valueOf(fieldIsStatic));
					df.setNumeric(Boolean.valueOf(fieldIsNumeric));
					df.setReplacement(fieldReplacement);
					df.setStart(strPosition);
					df.setEnd(strPosition += df.getLength());
					
					fieldsList.add(df);
					
				}
				
			}
			
			output.put("fields", fieldsList);
			
			
		} catch (Exception e) {
				e.printStackTrace();
		}
		return output;
		
	}
	
	
	

	

}


