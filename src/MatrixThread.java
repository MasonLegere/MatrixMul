import Jama.Matrix;

public class MatrixThread implements Runnable{

private Matrix A, B, C;
private int startingRow, numRows;

	public MatrixThread(Matrix A, Matrix B, Matrix C, int startingRow, int numRows) {
		this.A = A;
		this.B = B;
		this.C = C;
		this.startingRow = startingRow;
		this.numRows = numRows;
	}

	@Override
	public void run() {	
		for (int i = startingRow; i < numRows + startingRow; i++) {
			for (int j = 0; j < B.getColumnDimension(); j++) {
				C.set(i, j, dotProduct(i,j));
			}
		}
	}
	
	/*
	 * Computes the dot product between row (i) of matrix A 
	 * and column (j) of matrix B. This operation is defined as it has already be
	 * checked to make sure that the operation A*B is well-defined.
	 * */
	private double dotProduct(int i, int j) {
		double sum = 0;
		for (int k = 0; k < A.getColumnDimension(); k++) {
			sum += A.get(i, k)*B.get(k, j);
		}
		return sum;
	}

}
