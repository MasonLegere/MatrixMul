import Jama.Matrix;

public class MatrixThread implements Runnable{

private Matrix A, B;
private static Matrix C;
private int startingRow, numRows;

	public MatrixThread(Matrix A, Matrix B, int startingRow, int numRows) {
		this.A = A;
		this.B = B;
		this.C = new Matrix(A.getRowDimension(),B.getColumnDimension());
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
	
	public static Matrix getProduct() {
		return C;
	}
	
	private double dotProduct(int row, int col) {
		double sum = 0;
		for (int i = 0; i < A.getColumnDimension(); i++) {
			sum += A.get(row, i)*B.get(i, col);
		}
		return sum;
	}

}
