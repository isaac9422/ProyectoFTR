package market;

import java.awt.Color;
import java.awt.Font;

import org.jfree.chart.JFreeChart;
import org.jfree.chart.annotations.XYImageAnnotation;
import org.jfree.chart.annotations.XYTextAnnotation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import utilities.Global;

public class PlotFlows{
	
	// parámetros
	JFreeChart chart;
	Dispatch dispatch;
	double[][] cordXY;
	XYSeriesCollection dataset;
	
	// costructor vacío
	public PlotFlows(){}
	
	// constructor: 
	public PlotFlows(Dispatch dispatch, JFreeChart chart, XYSeriesCollection dataset, double[][] cordXY){
		this.dispatch = dispatch;
		this.chart = chart;
		this.dataset = dataset;
		this.cordXY = cordXY;
	}
	
	public void run(){
		
		XYSeries series = new XYSeries("Flows",false, true);
		series.add(0.0, 1.0);
		this.dataset.removeAllSeries();
		this.dataset.addSeries(series);
		XYPlot plot = this.chart.getXYPlot(); 
		plot.clearAnnotations();
		XYImageAnnotation xyImage = new XYImageAnnotation(0.5,0.5,Global.fondo);
		plot.addAnnotation(xyImage);
		for (int i = 0; i < Global.nLines; i++){
			XYTextAnnotation xy = new XYTextAnnotation(String.valueOf(Global.decimalFormatter.format(this.dispatch.getFlows()[i][0])),this.cordXY[0][i]/22,this.cordXY[1][i]/150000);
			xy.setBackgroundPaint(Color.white);
			xy.setFont(Global.labelFont);
			plot.addAnnotation(xy);
		}
	}
}
