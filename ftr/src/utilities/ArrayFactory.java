package utilities;

import java.util.Vector;

public class ArrayFactory {
	
	//private double [] vector;
	//private double  value;
	//private int columns;
	//private int rows;
	
	// Empty constructor
	public ArrayFactory(){}
	
	//
	// Create a matrix repeating a vector by rows
	//
	public double [][] repMat(double [] vector, int rows, int columns){
		double [][] matrix = new double[rows][columns];
		for (int r = 0; r < rows; r++){
			for (int c = 0; c < columns; c++){
				matrix [r][c] = vector[r];	
			}
		}
		return matrix;
	}
	
	//
	// Create a matrix repeating a vector by rows
	//
	public double [][] repMat(double [] vector,  int rows){
		double [][] matrix = new double[rows][vector.length];
		for (int r = 0; r < rows; r++){
			matrix[r] = vector;
		}
		return matrix;
	}
	
	//
	// Create a vector repeating a value
	//
	public double [] repVec(double value, int length){
		double [] vector = new double[length];
		for (int i = 0; i < length; i++){
			vector [i] = value;	
		}
		return vector;
	}
	
	//
	// Creating a vector from a matrix
	//
	public double [] mat2vec(double [][] matrix, int nrows, int ncols){
		double [] vector = new double[nrows * ncols];
		int k = 0;
		while (k < nrows * ncols) {
			for (int i = 0; i < nrows; i++){
				for (int j = 0; j < ncols; j++){
					vector[k] = matrix[i][j];
					k  = k + 1;
				}
			}
		}
		return vector;
	}
	
	//
    // Función para crear un vector a partir de una matriz de Strings
	//
    public String [] mat2vec(String [][] matrix, int nrows, int ncols){
 		String [] vector = new String[nrows * ncols];
 		int k = 0;
 		while (k < nrows * ncols) {
 			for (int i = 0; i < nrows; i++){
 				for (int j = 0; j < ncols; j++){
 					vector[k] = matrix[i][j];
 					k  = k + 1;
 				}
 			}
 		}
 		return vector;
 	}
	
	// Creating a matrix from a vector
	public double[][] vec2mat(double[] vector, int nrows, int ncols){
		double[][] matrix = new double[nrows][ncols];
		int k = 0;
		while (k < nrows * ncols) {
			for (int i = 0; i < nrows; i++){
				for (int j = 0; j < ncols; j++){
					matrix[i][j] = vector[k];
					k  = k + 1;
				}
			}
		}
		return matrix;
	}
	
	//
	// Función para crear un vector tipo String[] a partir de un Vector
	//
	public String [] vector2vec(Vector<String[]> vector)
	//public String [] vector2vec(Vector<String[]> vector, int nrows, int ncols)
	{
		String [] vec = new String[vector.size() * vector.get(0).length];
		int k = 0;
		while (k < vector.size() * vector.get(0).length) {
			//System.out.println("vector filas\t"+vector.size()+ "columnas\t"+vector.get(0).length);
			for (int i = 0; i < vector.size(); i++){
				//System.out.println("i"+i);
				for (int j = 0; j < vector.get(0).length; j++){
					vec[k] = vector.get(i)[j];
					k  = k + 1;
					//System.out.println("j"+j);
				}
			}
		}
		return vec;
	}
	
	//
	// Función para clacular el producto de un escalar por una matriz
	//
	public double [][] productEscalarMatrix(double [][] matriz, double escalar){
	    double [][] matrizResultado=new double[matriz.length][matriz[0].length];
	    for(int i=0;i<matriz.length;i++)
	        for(int j=0;j<matriz[i].length;j++)
	            matrizResultado[i][j]=matriz[i][j]*escalar; // multiplica cada valor de la matriz por el valor escalar.
	    return matrizResultado;
	}
	
	//
	// Función para clacular el producto de un escalar por un vector
	//
	public double [] productEscalarVector(double [] vector, double escalar){
	    double [] vectorResultado=new double[vector.length];
	    for(int i=0; i<vector.length;i++)
	    	vectorResultado[i]=vector[i]*escalar; // multiplica cada valor del vector por el valor escalar.
	    return vectorResultado;
	}
}
