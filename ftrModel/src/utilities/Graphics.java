package utilities;

import java.awt.Color;
import java.awt.Dimension;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;

import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.xy.XYDataset;

public class Graphics extends JFrame
{	
	private static final long serialVersionUID = 1L;
	
	// elementos d ela interfaz	
	private XYDataset dataset; 	// conjunto de datos
	private JFreeChart chart; 	// gráfico
	//private Dimension dimension; // dimensión del gráfico
	
	// constructor vacío 
	public Graphics() {}
		
		
	// constructor con el título de la pantalla 
	public Graphics(String title) {
        super(title);
    }
    
	
	// constructor para una gráfica tipo StepChart
	public Graphics(String title, XYDataset dataset, JFreeChart chart, String typePlot, Dimension dimension) 
	{
        super(title);
 		
 		// tipo de gráfico
 		if(typePlot == "step") stepChartTheme(chart);
        else if(typePlot == "line") lineChartTheme(chart);
        else if(typePlot == "flow") flowTheme(chart);
        ChartPanel chartPanel = new ChartPanel(chart);
        chartPanel.setPreferredSize(dimension);
  		setContentPane(chartPanel);
    }
	
	// constructor para una gráfica tipo StepChart
	public Graphics(String title, JTable table, Dimension dimension) 
	{
        super(title);
        JScrollPane  panel = new JScrollPane (table);
 		panel.setPreferredSize(dimension);
  		setContentPane(panel);
    }
		
	// set methods
	public void setDataset(XYDataset dataset){this.dataset =  dataset; }
	public void setChart(JFreeChart chart){this.chart =  chart; }
	
	//
	//formato de un gráfico tipo StepChart
	//
	private void stepChartTheme(JFreeChart chart) {
        chart.setBackgroundPaint(Color.white); 		// chart background        
        XYPlot plot = chart.getXYPlot(); 		// se obtiene una referencia a la gráfica para futuras modificaciones
        plot.setBackgroundPaint(Color.white);		// background del área del gráfico
        plot.setDomainGridlinePaint(Color.black); 	// color cuadricula eje x
        plot.setRangeGridlinePaint(Color.black);	// color cuadricula eje y
        
        // change the auto tick unit selection to integer units only...
        NumberAxis rangeAxis = (NumberAxis) plot.getRangeAxis();
        rangeAxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());	       
	}
	
	//
	// Formato de un gráfico tipo LineChart
	//
	private void lineChartTheme(JFreeChart chart) {
        chart.setBackgroundPaint(Color.white); // chart background        
        final XYPlot plot = chart.getXYPlot(); // Se obtiene una referencia a la gráfica para futuras modificaciones
        plot.setBackgroundPaint(Color.white);
        plot.setDomainGridlinePaint(Color.black);
        plot.setRangeGridlinePaint(Color.black);
        
        // permite cambiar la presentación de las lineas que representan las series de tiempo
        final XYLineAndShapeRenderer renderer = new XYLineAndShapeRenderer();
        //renderer.setSeriesLinesVisible(0, false);
        renderer.setSeriesShapesVisible(0, true);
        plot.setRenderer(renderer);

        // change the auto tick unit selection to integer units only...
        final NumberAxis rangeAxis = (NumberAxis) plot.getRangeAxis();
        rangeAxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());
        NumberAxis domaindAxis = (NumberAxis) plot.getDomainAxis();
        domaindAxis.setRange(1, 24);
	}
	
	//
	// Formato de un gráfico tipo LineChart
	//
	private void flowTheme(JFreeChart chart) {
        chart.setBackgroundPaint(Color.white); // chart background        
        XYPlot plot = chart.getXYPlot(); // Se obtiene una referencia a la gráfica para futuras modificaciones
        plot.setBackgroundPaint(Color.white);
        plot.setDomainGridlinePaint(Color.black);
        plot.setRangeGridlinePaint(Color.black);
        
        plot.setDomainCrosshairVisible(true);
        plot.setRangeCrosshairVisible(true);

        
        // permite cambiar la presentación de las lineas que representan las series de tiempo
        XYLineAndShapeRenderer renderer = new XYLineAndShapeRenderer();
        renderer.setBaseShapesVisible(true);
        renderer.setBaseShapesFilled(true);
        renderer.setSeriesPaint(0, Color.GREEN);
        renderer.setSeriesShapesVisible(0, true);
        plot.setRenderer(renderer);

        // change the auto tick unit selection to integer units only...
        NumberAxis rangeAxis = (NumberAxis) plot.getRangeAxis();
        rangeAxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());
        rangeAxis.setVisible(false);
        NumberAxis domaindAxis = (NumberAxis) plot.getDomainAxis();
        domaindAxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());
        domaindAxis.setVisible(false);	       
	}
}
