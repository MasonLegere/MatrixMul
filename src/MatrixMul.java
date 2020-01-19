import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

import org.jfree.data.xy.XYSeries;

import Jama.Matrix;

public class MatrixMul {
	
	public static void main(String[] args) throws InterruptedException {
		
		/*
		 * UNIT TEST METHOD CALL
		 * 
		 * unitTest();
		 */
		int[] numThreads, dimA, dimB;
		Scanner scanner = new Scanner(System.in);
		String line;
		Matrix A,B;
		
		System.out.println("This program will perform the computation A*B = C"); 
		System.out.println("Enter the desired size of matrix A in the form \"m,n\" where m and n are positive integers");
		line = scanner.nextLine(); 
		dimA = Arrays.stream(line.split(",")).mapToInt(Integer::parseInt).toArray();
		
		System.out.println("Enter the desired size of matrix B in the form \"m,k\" where m and k are positive integers");
		line = scanner.nextLine(); 
		dimB = Arrays.stream(line.split(",")).mapToInt(Integer::parseInt).toArray();
		
		System.out.println("Enter a comma delimited list of postive integers to specifiy the number of threads to be used.");
		line = scanner.nextLine();
	    numThreads = Arrays.stream(line.split(",")).mapToInt(Integer::parseInt).toArray();
		
	    A = Matrix.random(dimA[0], dimA[1]);
	    B = Matrix.random(dimB[0], dimB[1]);
		
		XYSeries times = runTest(numThreads, A, B);
		LinePlot plot = new LinePlot(times);
		plot.setVisible(true);
		
	}
	
	public static XYSeries runTest(int[] numThreads, Matrix A, Matrix B) throws InterruptedException {
		XYSeries times = new XYSeries("Data"); 
		long initialTime, finalTime, timeElapsed;
		
		for (int i : numThreads) {
			
			
			initialTime = System.currentTimeMillis();
			matrixMul(A, B, i);
			finalTime = System.currentTimeMillis();
			timeElapsed = finalTime - initialTime;
			times.add(i,timeElapsed);
			
		}
	
		return times;
	}
	
	public static void unitTest() throws InterruptedException {
		
		Matrix A; 		// A <- i by j matrix 
		Matrix B; 		// B <- j by k matrix 
		int m; 			// m <- number of threads
		
		for (int i = 1; i < 500; i+= 10) {
			for (int j = 1; j < 500; j+= 10) {
				for (int k = 1; k < 500; k+= 10) {	
					
					A = Matrix.random(i,j);
					B = Matrix.random(j,k);
					
					for (m = 1; m < 600; m+=25) {
						assert(matrixMul(A,B,m).equals(A.times(B)));
					}
					
				}
			}
		}
		
	}
	
	public static Matrix matrixMul(Matrix A, Matrix B, int numThreads) throws InterruptedException {
		
		int startingRow = 0, numRows = 0;
		float height = A.getRowDimension(); 
		
		List<Thread> threadList = new ArrayList<Thread>(); 
		
		if (A.getColumnDimension() != B.getRowDimension()) {
			throw new IllegalArgumentException("Matrix Multiplication Undefined: Inner Dimensions Must Agree.");
		}
		else if (numThreads < 1) {
			throw new IllegalArgumentException("Number of Threads Must be an Integer Larger Than Zero.");
		}
			
		Matrix C = new Matrix(A.getRowDimension(),B.getColumnDimension());
		
		for (int i = 0; i < Math.min(numThreads, height); i++) {
			numRows = (int) Math.min(Math.round(height/numThreads), height - i*(numRows));
			MatrixThread m = new MatrixThread(A, B, C, startingRow, numRows);
			Thread t = new Thread(m);
			t.start();
			threadList.add(t);
			startingRow += numRows;
		}
		
		for (Thread t : threadList) {
			t.join();
		}
		
		return C;
		
	}
	
	
	
	

}
