package client;

public class Pairs {	
    private Double percentage;
    private String classPredicted;
    private int classPredictedValue;
    private static java.text.DecimalFormat sf = new java.text.DecimalFormat("0.##E0");
	
    public Pairs(Double pPercentage, int pClassPredictedValue, String pClassPredicted) {
    	percentage = pPercentage;
    	classPredictedValue = pClassPredictedValue;
    	classPredicted = pClassPredicted;
    }    
    public Double getPercentage() {
    	return percentage;
    }
    
    public int getClassPredictedValue() {
    	return classPredictedValue;
    }
    
    public String getResult() { 	
    	return classPredicted + ":"  + sf.format(percentage);
    }
    
}
