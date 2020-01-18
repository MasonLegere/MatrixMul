import java.util.ArrayList;
import java.util.List;

import Jama.Matrix;

public class MatrixMul {
	
	public static void main(String[] args) throws InterruptedException {
		Matrix A = Matrix.random(3,2);
		Matrix B = Matrix.random(2,5); 
		
		Matrix C1 = matrixMul(A,B,2); 
		Matrix C2 = A.times(B);
		
		C1.print(1,2);
		C2.print(1,2);
		
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
					
		for (int i = 0; i < Math.min(numThreads, height); i++) {
			numRows = (int) Math.min(Math.round(height/numThreads), height - i*(numRows));
			MatrixThread m = new MatrixThread(A,B,startingRow,numRows);
			Thread t = new Thread(m);
			t.start();
			threadList.add(t);
			startingRow += numRows;
		}
		
		for (Thread t : threadList) {
			t.join();
		}
		
		return MatrixThread.getProduct();
		
	}
	
	
	
	

}
