package com.ejavier.dm;

import java.util.ArrayList;

public class LastNameMaskingLogicImpl implements MaskingLogicInterface{
	
	public String doProcess(ArrayList fieldList, DmField dmfield, String line){
		
		String output = "NAME2";

		
		for (int ctr=0; ctr<fieldList.size(); ctr++) {
			DmField dmField = (DmField)fieldList.get(ctr);
			String cif = dmField.getFieldname();
			
			if(cif.equalsIgnoreCase("CIF")) {
				int start = dmField.getStart();
				int end = dmField.getEnd();
				String strCif = line.substring(start, end);
				int intCif = Integer.parseInt(strCif);
				output+=intCif;
			}
			
		}
		
		
		return output;
	}
	

}
