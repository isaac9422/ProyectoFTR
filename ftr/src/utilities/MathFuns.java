package utilities;

import java.util.List;

public class MathFuns {
	
	//----------------------------------------------------------------------------------
	// calcular la media de un arreglo
	//----------------------------------------------------------------------------------
	public static double Mean(double[] array){
		double sum = 0.0;
		for(int i = 0; i < array.length; i++){
			sum = sum + array[i];
		}	
		return sum/array.length;
	}
	
	//----------------------------------------------------------------------------------
	// funci�n para determinar si un n�mero est� en un vector
	//----------------------------------------------------------------------------------
	public static boolean IsIn(int value, int[] array){
		boolean isIn = false;
		for(int i = 0; i < array.length; i++){
			if(value == array[i]){
				isIn = true;
			}
		}
		return isIn;	
	}
	
	//----------------------------------------------------------------------------------
	// funci�n para determinar el m�nimo de un vector
	//----------------------------------------------------------------------------------
	public static double Min(double[] array){
		double min = array[0];
		for(int i = 0; i < array.length; i++){
			if(array[i] < min){
				min = array[i];
			}
		}
		return min;	
	}
	
	//----------------------------------------------------------------------------------
	// funci�n para determinar el m�ximo de un vector
	//----------------------------------------------------------------------------------
	public static double Max(double[] array){
		double max = array[0];
		for(int i = 0; i < array.length; i++){
			if(array[i] > max){
				max = array[i];
			}
		}
		return max;	
	}
	
	//----------------------------------------------------------------------------------
	// funci�n para transponer una matriz
	//----------------------------------------------------------------------------------
	public static double[][] Transposed(double[][] array){
		double[][] transposed = new double[array[0].length][array.length];
		for (int i=0; i< array.length;i++){
			for(int j=0; j< array[0].length;j++){
				transposed[j][i]= array[i][j];
			}
		}
		return transposed;
	}
	
	//----------------------------------------------------------------------------------
	// funci�n para realizar una suma acumulativa por columnas
	//----------------------------------------------------------------------------------
	public double[] SumByColumns(double[][] array){
		double[] sumByColumns = new double[array[0].length];
		for (int i=0; i< array.length;i++){
			for(int j=0; j< array[0].length;j++){
				sumByColumns[j]= sumByColumns[j] + array[i][j];
			}
		}
		return sumByColumns;
	}
	
	//----------------------------------------------------------------------------------
	// funci�n para determinar el m�nimo de un vector que sea mayor que cero
	//----------------------------------------------------------------------------------
	public static double MinNonZero(double[] array){
		double min = Max(array);
		for(int i = 0; i < array.length; i++)
		{
			if(array[i] > Global.ftrOperatorMinimumPrice)
			{
				if(array[i] < min){
					min = array[i];
				}
			}
		}
		return min;	
	}
}
