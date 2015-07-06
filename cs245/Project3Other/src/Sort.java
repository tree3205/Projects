import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedList;


public class Sort implements SortInterface{
	
	public Sort() {
		
	}

	public void display(Comparable[] array) {
		for (int i = 0; i < array.length; i++) {
			System.out.print(array[i] + " ");
		}
		System.out.println();
	}
	
	public void display(int[] array) {
		for (int i = 0; i < array.length; i++) {
			System.out.print(array[i] + " ");
		}
		System.out.println();
	}
	
	public void displayLL(LinkedList<LLNode> list) {
		Iterator<LLNode> itr = list.iterator();
		while ( list.element().elem() != null && itr.hasNext()) {
			System.out.print(itr.next().elem() + " ");
		}
		System.out.println();
	}
	
	public Comparable[] getData(Comparable[] array, int lowindex, int highindex) {
		int size = highindex - lowindex + 1;
		if (size <= 0 )
			System.out.println("Empty array.");
		Comparable[] data = new Comparable[size];
		return data = Arrays.copyOfRange(array, lowindex, highindex+1);
	}
	
	public int[] getData(int[] array, int lowindex, int highindex) {
		int size = highindex - lowindex + 1;
		if (size <= 0 )
			System.out.println("Empty array.");
		int[] data = new int[size];
		return data = Arrays.copyOfRange(array, lowindex, highindex+1);
	}
	
	@Override
	public void insertionSort(Comparable[] array, int lowindex, int highindex,
			boolean reversed) {
		System.out.println("Original array: ");
		display(array);
		Comparable[] data = getData(array, lowindex, highindex);
		System.out.println("Data in range: ");
		display(data);	
		System.out.println("insertionSort: ");
		
		int i, j;
		Comparable current;
		
		for (i = 1; i < data.length; i++) {
			current = data[i];
			if (reversed == true) {
				for (j = i - 1; j >= 0 && data[j].compareTo(current) < 0; j--) {
					data[j + 1] = data[j];
					data[j] = current; 
				}
			}
			else if (reversed == false) {
				for (j = i - 1; j >= 0 && data[j].compareTo(current) > 0; j--) {
					data[j + 1] = data[j]; 
				    data[j] = current;
				}
			}
		}
		display(data);	
		System.out.println("==============");
	}

	@Override
	public void selectionSort(Comparable[] array, int lowindex, int highindex,
			boolean reversed) {
		System.out.println("Original array: ");
		display(array);
		Comparable[] data = getData(array, lowindex, highindex);
		System.out.println("Data in range: ");
		display(data);	
		System.out.println("selectionSort: ");
		
		int i,j;
		int select;
		Comparable tmp;
		
		for(i = 0; i < data.length; i++) {
			select = i;
			for (j = i + 1; j < data.length; j++) {
				if  (reversed == true) {
					if (data[select].compareTo(data[j]) < 0) {
						select = j;
					}
				}
				else if (reversed == false){
					if (data[select].compareTo(data[j]) > 0) {
						select = j;
					}
				}	
			}
			tmp = data[i];
			data[i] = data[select];
			data[select] = tmp;
		}
		display(data);	
		System.out.println("==============");
	}

	@Override
	public void shellSort(Comparable[] array, int lowindex, int highindex, boolean reversed) {
		System.out.println("Original array: ");
		display(array);
		Comparable[] data = getData(array, lowindex, highindex);
		System.out.println("Data in range:");
		display(data);	
		System.out.println("shellSort: ");
		
		int n = data.length;
		int increment, i, j;
		Comparable tmp;
		for (double k = (int) (Math.log(n) / Math.log(2)); k > 0; k--) {
			increment = (int) (Math.pow(2, k) - 1);
			for ( i = increment; i < n; i++) {
				tmp = data[i];
				if (reversed == true) {
					for (j = i - increment; j >= 0 && data[j].compareTo(tmp) < 0; j = j - increment) {
						data[j + increment] = data[j];
					}
					data[j + increment] = tmp;
				}
				else if (reversed == false) {
					for (j = i - increment; j >= 0 && data[j].compareTo(tmp) > 0; j = j - increment) {
						data[j + increment] = data[j];
					}
					data[j + increment] = tmp;
				}
			}	
		}
		display(data);	
		System.out.println("==============");
	}
		
	@Override
	public void bucketSort(int[] array, int lowindex, int highindex,
			boolean reversed) {
		System.out.println("Original array: ");
		display(array);
		int[] data = getData(array, lowindex, highindex);
		System.out.println("Data in range: ");
		display(data);
		
		int i, j, k;
		int min, max;
		long num = data.length / 2;
		min = data[0];
		max = min;
		for (i = 1; i < data.length; i++) {
			if (data[i] < min ) {
				min = data[i];
			}
			else if (data[i] > max) {
				max = data[i];
			}
		}
		
		int range = (int) ((max / num) + 1);
		LinkedList[] bucket = new LinkedList[(int) num];
		for (i = 0; i < num; i++) {
			bucket[i] = new LinkedList();
		}
		
		for (i = 0; i < data.length; i++) {
			int hash = data[i] / range;
			bucket[hash].add(data[i]);
		}
		
		//for (i = 0; i < bucket.length;i++) {
			//System.out.println(bucket[i]);
		//}
		
		for (i = 0; i < bucket.length; i++) {
			for ( j = 1; j < bucket[i].size(); j++) {
				LinkedList tmpList = bucket[i];
				int tmp =  (Integer) tmpList.get(j);
				if (reversed == false) {
					for ( k = j - 1; k >= 0 && ((Comparable) tmpList.get(k)).compareTo(tmp) > 0; k--) {
						tmpList.set(k + 1, tmpList.get(k));
						tmpList.set(k, tmp);
					}	
				}
				else if (reversed == true) {
					for ( k = j - 1; k >= 0 && ((Comparable) tmpList.get(k)).compareTo(tmp) < 0; k--) {
						tmpList.set(k + 1, tmpList.get(k));
						tmpList.set(k, tmp);
					}	
				}
			}
		}
		
		if (reversed == false) {
			System.out.println("bucketSort: ");
			for ( i = 0; i < bucket.length; i++) {
				for ( j = 0; j < bucket[i].size(); j++) {
					System.out.print(bucket[i].get(j) + " ");
				}
			}
			System.out.println();
			System.out.println("=============");
		}
		else if (reversed = true) {
			System.out.println("bucket(reversed): ");
			for ( i = bucket.length - 1; i >= 0; i--) {
				for ( j = 0; j < bucket[i].size(); j++) {
					System.out.print(bucket[i].get(j) + " ");
				}
			}
			System.out.println();
			System.out.println("=============");
		}		
	}

	@Override
	public void heapSort(Comparable[] array, int lowindex, int highindex,
			boolean reversed) {
		System.out.println("Original array: ");
		display(array);
		Comparable[] data = getData(array, lowindex, highindex);
		System.out.println("Data in range: ");
		display(data);
		
		// need to work
	}

	
		/**int i, max;
		Comparable[] heapSort = new Comparable[highindex - lowindex + 1];
		MaxHeap maxHeap = new MaxHeap(highindex - lowindex + 2);
		for(i = lowindex; i <= highindex; i++) {
			maxHeap.insert((Integer) array[i]);
		}
		System.out.println("MaxHeap: ");
		maxHeap.print();
		if (reversed == true) {
			System.out.println("heapSort:(reversed) ");
			for (i = 0; i < heapSort.length; i++) {
				heapSort[i] = maxHeap.removemax();
			}
		}
		else if (reversed == false) {
			System.out.println("heapSort: ");
			for ( i = heapSort.length - 1; i >= 0; i--) {
				heapSort[i] = maxHeap.removemax();
			}
		}
		display(heapSort);
		System.out.println("===========");
	}*/
		
		

	@Override
	public void radixSort(int[] array, int lowindex, int highindex,
			boolean reversed) {
		int base, digit, i, j, rtok;
		base = highindex - lowindex + 1;
		int[] count = new int[base];
		 for ( digit = 0; digit <= 5; digit++) {
		 }
		
	}

	@Override
	public void mergeSort(Comparable[] array, int lowindex, int highindex,
			boolean reversed) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void quickSort(Comparable[] array, int lowindex, int highindex,
			boolean reversed) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public LLNode mergeSortLL(LLNode list, boolean reversed) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public LLNode insertionSortLL(LLNode list, boolean reversed) {
			
		return list;
	}

	@Override
	public void optimziedQuickSort(Comparable[] array, int lowindex,
			int highindex, boolean reversed) {
		
		
	}

	
}
