package utilities;

import market.Dispatch;

import org.jfree.chart.JFreeChart;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import utilities.Global;

public class PlotSpotPrices implements Runnable{

	//
	Dispatch dispatch;
	XYSeriesCollection dataset;
	
	
	// constructor vacío
	public PlotSpotPrices(){}
	
	// constructor: despacho, dataset, JFreeChar, GraphicsStepChart
	public PlotSpotPrices(Dispatch dispatch, XYSeriesCollection dataset){
		this.dispatch = dispatch;
		this.dataset = dataset;		
	}
	
	@Override
	public void run() {
		
		this.dataset.removeAllSeries();
        
		XYSeries series = new XYSeries("Spot price",false, true);
		for (int h = 0; h < 24; h++)
		{
			series.add(h+1,this.dispatch.getNodalPrices()[0][h]);
		}
        this.dataset.addSeries(series);
	}
}
