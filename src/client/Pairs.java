package client;

public class Pairs {	
    private Double percentage;
    private String classPredicted;
    private static java.text.DecimalFormat sf = new java.text.DecimalFormat("0.##E0");
	
    public Pairs(Double pPercentage, String pClassPredicted) {
    	percentage = pPercentage;
    	classPredicted = pClassPredicted;
    }    
    public Double getPercentage() {
    	return percentage;
    }
    
    public String getResult() { 	
    	return classPredicted + ":"  + sf.format(percentage);
    }
    
}
