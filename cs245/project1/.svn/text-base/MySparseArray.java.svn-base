import galles.ColumnIterator;
import galles.ElemIterator;
import galles.MatrixElem;
import galles.RowIterator;
import galles.SparseArray;

public class MySparseArray implements SparseArray {

	private Object defaultValue;
	private IndexLink rowIndexHead;
	private IndexLink colIndexHead;

	public MySparseArray(Object defaultValue) {
		this.defaultValue = defaultValue;
		this.rowIndexHead = new IndexLink(true);
		this.colIndexHead = new IndexLink(false);
	}

	@Override
	public Object defaultValue() {
		return defaultValue;
	}

	@Override
	public RowIterator iterateRows() {
		return new MyRowIterator();
	}

	@Override
	public ColumnIterator iterateColumns() {
		return new MyColumnIterator();
	}

	@Override
	public Object elementAt(int row, int col) {
		IndexLink tmp = rowIndexHead;
		while (tmp.next != null && tmp.next.index != col) {
			tmp = tmp.next;
		}
		if (tmp.next != null) {
			ElemLink etmp = tmp.next.headElem;
			while (etmp.nextRow != null && etmp.nextRow.rowIndex != row) {
				etmp = etmp.nextRow;
			}
			if (etmp.nextRow != null) {
				return etmp.nextRow.value;
			}
		}
		
		return defaultValue;
	}

	@Override
	public void setValue(int row, int col, Object value) {
		ElemLink elem = new ElemLink(row, col, value);
		IndexLink tmp;
		if (value.equals(defaultValue)) {
			/* remove the ElemLink if existed */
			
			/* maintain rowIndexLinkedList */
			tmp = rowIndexHead;
			while (tmp.next != null && tmp.next.index != col) {
				tmp = tmp.next;
			}
			if (tmp.next != null) {
				tmp.next.removeElem(elem);
				if (tmp.next.headElem.nextRow == null) {
					tmp.next = tmp.next.next;
				}
			}
			
			/* maintain colIndexLinkedList */
			tmp = colIndexHead;
			while (tmp.next != null && tmp.next.index != row) {
				tmp = tmp.next;
			}
			if (tmp.next != null) {
				tmp.next.removeElem(elem);
				if (tmp.next.headElem.nextCol == null) {
					tmp.next = tmp.next.next;
				}
			}

		} else { 
			/* add new ElemLink */
			
			/* maintain rowIndexLinkedList */
			tmp = rowIndexHead;
			while (tmp.next != null && tmp.next.index < col) {
				tmp = tmp.next;
			}
			if (tmp.next == null) {
				IndexLink newRowIndex = new IndexLink(true, col, null);
				/* add new ElemLink to newRowIndex */
				newRowIndex.addElem(elem);
				/* end */
				tmp.setNext(newRowIndex);
			}
			else if (tmp.next.index == col) {
				/* add new ElemLink to tmp.next */
				tmp.next.addElem(elem);
				/* end */
			}
			else { // CASE: tmp.next.index > col
				IndexLink newRowIndex = new IndexLink(true, col, tmp.next);
				/* add new ElemLink to newRowIndex */
				newRowIndex.addElem(elem);
				/* end */
				tmp.setNext(newRowIndex);
			}
			
			/* maintain colIndexLinkedList */
			tmp = colIndexHead;
			while (tmp.next != null && tmp.next.index < row) {
				tmp = tmp.next;
			}
			if (tmp.next == null) {
				IndexLink newColIndex = new IndexLink(false, row, null);
				/* add new ElemLink to newColIndex */
				newColIndex.addElem(elem);
				/* end */
				tmp.setNext(newColIndex);
			}
			else if (tmp.next.index == row) {
				/* add new ElemLink to tmp.next */
				tmp.next.addElem(elem);
				/* end */
			}
			else { // CASE: tmp.next.index > col
				IndexLink newColIndex = new IndexLink(false, row, tmp.next);
				/* add new ElemLink to newColIndex */
				newColIndex.addElem(elem);
				/* end */
				tmp.setNext(newColIndex);
			}
		}
	}
	
	private class IndexLink {
		private boolean isRowIndex;
		private int index;
		private IndexLink next;
		private ElemLink headElem;

		IndexLink(boolean isRowIndex, int index, IndexLink next) {
			this.isRowIndex = isRowIndex;
			this.index = index;
			this.headElem = new ElemLink();
			this.next = next;
		}
		
		IndexLink(IndexLink next) {
			this.next = next;
		}
		
		IndexLink(boolean isRowIndex) {
			this.isRowIndex = isRowIndex;
		}

		IndexLink next() {
			return next;
		}

		ElemLink headElem() {
			return headElem;
		}

		void setNext(IndexLink next) {
			this.next = next;
		}

		void addElem(ElemLink elem) {
			ElemLink tmp = headElem;
			if (isRowIndex) {
				while (tmp.nextRow != null && tmp.nextRow.rowIndex < elem.rowIndex) {
					tmp = tmp.nextRow;
				}
				if (tmp.nextRow == null) {
					tmp.setNextRow(elem);
				}
				else if (tmp.nextRow.rowIndex == elem.rowIndex) {
					tmp.nextRow.value = elem.value;
				}
				else {
					elem.setNextRow(tmp.nextRow);
					tmp.setNextRow(elem);
				}
			}
			else {
				while (tmp.nextCol != null && tmp.nextCol.colIndex < elem.colIndex) {
					tmp = tmp.nextCol;
				}
				if (tmp.nextCol == null) {
					tmp.setNextCol(elem);
				}
				else if (tmp.nextCol.colIndex == elem.colIndex) {
					tmp.nextCol.value = elem.value;
				}
				else {
					elem.setNextCol(tmp.nextCol);
					tmp.setNextCol(elem);
				}
			}
		}
		
		void removeElem(ElemLink elem) {
			ElemLink tmp = headElem;
			if (isRowIndex) {
				while (tmp.nextRow != null && tmp.nextRow.rowIndex != elem.rowIndex) {
					tmp = tmp.nextRow;
				}
				if (tmp.nextRow != null) {
					tmp.nextRow = tmp.nextRow.nextRow;
				}
			}
			else {
				while (tmp.nextCol != null && tmp.nextCol.colIndex != elem.colIndex) {
					tmp = tmp.nextCol;
				}
				if (tmp.nextCol != null) {
					tmp.nextCol = tmp.nextCol.nextCol;
				}
			}
		}
		
		MyElemIterator getElemIterator() {
			return new MyElemIterator();
		}
		
		private class MyElemIterator extends ElemIterator {
			
			private boolean iteratingRow;
			private boolean iteratingCol;
			private ElemLink current;
			
			public MyElemIterator() {
				this.iteratingRow = !isRowIndex;
				this.iteratingCol = isRowIndex;
				current = headElem;
			}

			@Override
			public boolean iteratingRow() {
				return iteratingRow;
			}

			@Override
			public boolean iteratingCol() {
				return iteratingCol;
			}

			@Override
			public int nonIteratingIndex() {
				if (iteratingRow) {
					return current.rowIndex();
				}
				else {
					return current.columnIndex();
				}
			}

			@Override
			public MatrixElem next() {
				if (iteratingRow) {
					current = current.nextCol();
					return current;
				}
				else {
					current = current.nextRow();
					return current;
				}
			}

			@Override
			public boolean hasNext() {
				if (iteratingRow) {
					return current != null && current.nextCol != null;
				}
				else {
					return current != null && current.nextRow != null;
				}
			}
			
		}
		
	}

	private class ElemLink implements MatrixElem {

		private int rowIndex;
		private int colIndex;
		private Object value;
		private ElemLink nextRow;
		private ElemLink nextCol;

		ElemLink(int rowIndex, int columnIndex, Object value) {
			this.rowIndex = rowIndex;
			this.colIndex = columnIndex;
			this.value = value;
			this.nextRow = null;
			this.nextCol = null;
		}
		
		ElemLink() {}
		

		public void setNextRow(ElemLink nextRow) {
			this.nextRow = nextRow;
		}

		public void setNextCol(ElemLink nextCol) {
			this.nextCol = nextCol;
		}

		@Override
		public int rowIndex() {
			return rowIndex;
		}

		@Override
		public int columnIndex() {
			return colIndex;
		}

		@Override
		public Object value() {
			return value;
		}
		
		public ElemLink nextRow() {
			return nextRow;
		}
		
		public ElemLink nextCol() {
			return nextCol;
		}

	}
	
	private class MyRowIterator extends RowIterator {
		
		private IndexLink current;
		
		public MyRowIterator() {
			this.current = rowIndexHead;
		}

		@Override
		public ElemIterator next() {
			current = current.next();
			return current.getElemIterator(); // need fix
		}

		@Override
		public boolean hasNext() {
			return current != null && current.next != null;
		}
		
	}
	
	private class MyColumnIterator extends ColumnIterator {
		
		private IndexLink current;
		
		public MyColumnIterator() {
			this.current = colIndexHead;
		}

		@Override
		public ElemIterator next() {
			current = current.next();
			return current.getElemIterator();
		}

		@Override
		public boolean hasNext() {
			return current != null && current.next != null;
		}
		
	}

	public static void main(String[] args) {
		MySparseArray s = new MySparseArray(0);
		s.setValue(2, 4, 5);
		s.setValue(10, 4, 3);
		s.setValue(10, 15, 2);
		s.setValue(30, 35, 1);
		s.setValue(10, 15, 0);
		
		RowIterator r = s.iterateRows();
		while (r.hasNext())
		{
		   ElemIterator elmItr = r.next();
		   while (elmItr.hasNext())
		   {
		      MatrixElem me = elmItr.next();
		      System.out.print("row:" + me.rowIndex() + 
		                       " col:" + me.columnIndex() + 
		                       " val:" + me.value() + " ");
		      System.out.println();
		   }
		}
		
		/*IndexLink tmp;
		ElemLink etmp;
		tmp = a.rowIndexHead;
		while (tmp.next != null) {
			tmp = tmp.next;
			etmp = tmp.headElem;
			while (etmp.nextRow != null) {
				etmp = etmp.nextRow;
				System.out.println(etmp.rowIndex() + ", " + etmp.columnIndex() + ", " + etmp.value());
			}
		}
		System.out.println();
		tmp = a.colIndexHead;
		while (tmp.next != null) {
			tmp = tmp.next;
			etmp = tmp.headElem;
			while (etmp.nextCol != null) {
				etmp = etmp.nextCol;
				System.out.println(etmp.rowIndex() + ", " + etmp.columnIndex() + ", " + etmp.value());
			}
		}*/
	}
}
