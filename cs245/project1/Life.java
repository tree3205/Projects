import galles.ColumnIterator;
import galles.ElemIterator;
import galles.MatrixElem;
import galles.RowIterator;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;

public class Life {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		MySparseArray currentGeneration = new MySparseArray(0);

		try {
			BufferedReader in = new BufferedReader(new FileReader(args[0]));
			BufferedWriter out = new BufferedWriter(new FileWriter(args[1]));
			int round = Integer.valueOf(args[2]);
			
			String line;
			while ((line = in.readLine()) != null) {
				String[] data = line.split(",");
				int row = Integer.valueOf(data[0]);
				int col = Integer.valueOf(data[1]);
				currentGeneration.setValue(row, col, 1);
			}
			
			RowIterator r;
			MySparseArray neighborsMap;
			MySparseArray nextGeneration;
			ElemIterator elmItr;
			while (round > 0) {
				neighborsMap = new MySparseArray(0);
				r = currentGeneration.iterateRows();
				while (r.hasNext()) {
					elmItr = r.next();
					while (elmItr.hasNext()) {
						MatrixElem me = elmItr.next();
						int row = me.rowIndex();
						int col = me.columnIndex();
						for (int tmpRow = row - 1; tmpRow <= row + 1; tmpRow++) {
							for (int tmpCol = col - 1; tmpCol <= col + 1; tmpCol++) {
								if (tmpRow > 0 && tmpCol > 0 && (tmpRow != row || tmpCol != col)) {
									neighborsMap.setValue(tmpRow, tmpCol, (Integer) neighborsMap.elementAt(tmpRow, tmpCol) + 1);
								}
							}
						}
					}
				}
				
				nextGeneration = new MySparseArray(0);
				r = neighborsMap.iterateRows();
				while (r.hasNext()) {
					elmItr = r.next();
					while (elmItr.hasNext()) {
						MatrixElem me = elmItr.next();
						int row = me.rowIndex();
						int col = me.columnIndex();
						int val = (Integer) me.value();
						if (val == 2 && currentGeneration.elementAt(row, col).equals(new Integer(1))) {
							nextGeneration.setValue(row, col, 1);
						}
						if (val == 3) {
							nextGeneration.setValue(row, col, 1);
						}
					}
				}
				
				currentGeneration = nextGeneration;
				round--;
			}

			ColumnIterator c = currentGeneration.iterateColumns();
			while (c.hasNext()) {
				elmItr = c.next();
				while (elmItr.hasNext()) {
					MatrixElem me = elmItr.next();
					int row = me.rowIndex();
					int col = me.columnIndex();
					out.write(row + "," + col + "\n");
				}
			}
			
			in.close();
			out.close();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
