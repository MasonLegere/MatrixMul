import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

import org.jfree.data.xy.XYSeries;

import Jama.Matrix;

public class MatrixMul {
	
	public static void main(String[] args) throws InterruptedException {
		
		int[] numThreads; 	// numThreads[i] <- number of threads to use for ith multiplication
		int[] dimA, dimB;	// dim_[0] <- row_dim, dim_[1] <- col_dim
		String line;  		// buffer for user input
		Scanner scanner = new Scanner(System.in);
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
		
	    // Creates randomly populated entries with values in (0,1)
	    A = Matrix.random(dimA[0], dimA[1]);
	    B = Matrix.random(dimB[0], dimB[1]);
		
	    // Performs matrix mul. using the number of threads specified in the entries numThreads 
		XYSeries times = runTest(numThreads, A, B);
		LinePlot plot = new LinePlot(times);
		plot.setVisible(true); 
		
		// Performs unit test to ensure correctness of the product
		
		/*
		 * 	unitTest();
		 * 	System.out.println("Unit Test Complete"); 
		 * */

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
	
	/*
	 * Unit Test Outline: 
	 * 
	 * - Computes the produce A*B using m threads. 
	 * - The sizes of both A and B are varied over possible ranges for defined matrix multiplication.
	 * - Product compared with pre-built Matrix method times(Matrix) included in the Jama library. 
	 * */
	public static void unitTest() throws InterruptedException {
		
		Matrix A; 		// A <- i by j matrix 
		Matrix B; 		// B <- j by k matrix 
		int m; 			// m <- number of threads
		
		for (int i = 1; i < 500; i+= 10) {
			for (int j = 1; j < 500; j+= 10) {
				for (int k = 1; k < 500; k+= 10) {	
					
					A = Matrix.random(i,j);
					B = Matrix.random(j,k);
					
					for (m = 1; m < 600; m+=50) {
						assert(matrixMul(A,B,m).equals(A.times(B)));
					}
					
				}
			}
		}
		
	}
	
	
	/*
	 * Computes the matrix product A*B using a specified number of threads. 
	 * 
	 * **(1)**
	 * 		The minimum is taken between the rounded step-size and the difference between the 
	 * 		height and the current row number being performed. Rounding ensures that on average
	 * 		the threads will have a shared workload. The minimum between the two values is to 
	 * 		account for case where the number of threads does not evenly divide the matrix height.
	 * 
	 * Everything else in the method if self-explanatory. 
	 * */
	public static Matrix matrixMul(Matrix A, Matrix B, int numThreads) throws InterruptedException {
		
		int startingRow = 0; 	// startingRow  <- row that a thread starts on
		int numRows = 0;		// numRows 		<- number of rows a thread is responsible to compute
		
		// The height of matrix A, stored as a float so integer division is not done in **(1)**
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
			numRows = (int) Math.min(Math.round(height/numThreads), height - i*(numRows)); // **(1)**
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
