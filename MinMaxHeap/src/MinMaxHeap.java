/*
 * 배열을 이용, MinMaxHeap을 구현하는 과제입니다. 아래에 insert, max, min, 
 * deleteMin, deleteMax 메소드들을 구현하십시오. 
 * 필요한 만큼의 private 메소드들을 추가 하셔도 좋습니다. 
 *
 * MinMaxHeap은 generalization binary heap으로 
 * min heap과 max heap 의 특성을 모두 갖게되어 
 * 홀수층에는 min heap이, 짝수층에는 max heap의 특성을 갖게됩니다. 
 * 
 * 배열에서 
 * arr[x]의 왼쪽 아이는 arr[2x]
 * arr[x]의 오른쪽 아이는 arr[2x+1] 
 * arr[x]의 부모는 arr[x/2] 의 인덱스를 가지고 있습니다.
 * 
 * 또한 arr[x]는 
 *  floor(log_2(x)) 층에 위치하고 있습니다. (사진 참고)
 * 
 * min은 heap에서 가장 작은 수를 찾는 메소드로, 이 경우 root, 즉 arr[1]에 
 * 위치하게 됩니다. 
 * max는 root의 두 아이들 (왼쪽, 오른쪽) 중에 하나입니다. 
 * insert, deleteMin, 그리고 deleteMax 메소드들은 전부 O(log_2(N))의 
 * 런타임을 갖게 됩니다. 
 * 
 * 필요하다면 테스터 클래스를 만들어 메인 메소드를 작성해서 인풋을 테스트 해보셔도 좋습니다.
 * 테스터 클래스에는 Java에 built in 되어 있는 Array 클래스를 사용 할 수 있습니다. 
 * ex) Arrays.toString(arr)  
 * 
 * 
 * 
 * 아래 링크에 나오는 설명을 참고하면 도움이 될 듯 합니다. 
 * http://www.akira.ruc.dk/~keld/teaching/algoritmedesign_f03/Artikler/02/Atkinson86.pdf
 * 
 */

public class MinMaxHeap
{
	private int currentSize;
	private int[] arr;

	public MinMaxHeap(int capacity)
	{
		if(capacity < 1)
		{
			throw new IllegalArgumentException("capacity must be >= 1");
		}

		arr = new int[capacity + 1];
		currentSize = 0;
	}

	public boolean isFull()
	{
		return currentSize == arr.length - 1;
	}

	public boolean isEmpty()
	{
		return currentSize == 0;
	}

	// MinMaxHeap 의 특성에 맞게 숫자를 집어넣어야 합니다.
	public void insert(int x)
	{// PRE: The heap is not full
		if(isFull())
		{
			throw new IllegalStateException("internal error");
		}

		int currentNode = ++currentSize;
		int level = (int) (Math.log(currentNode) / Math.log(2));

		// parrent & grandparrent index
		int pa = currentNode / 2;
		int grandPa = pa / 2;

		// 가장 밑의 레벨 중 min max level을 선택해 공간을 만든다.
		if(currentNode > 1 && (level % 2 == 0 && arr[pa] < x || level % 2 != 0 && arr[pa] > x))
		{
			arr[currentNode] = arr[pa];
			currentNode = pa;
			pa = grandPa;
			level--;
		}

		// 마지막 레벨에서 부터 데이터를 삽입할 index 를 찾는다.
		while(currentNode != 1)
		{
			grandPa = pa / 2;
			
			// 현재 위치가 삽입될 위치면 break
			if(level < 2 && x >= arr[pa])
			{
				break;
			}
			else if(level % 2 == 0 && x >= arr[grandPa] || level % 2 != 0 && x <= arr[grandPa])
			{
				break;
			}
			
			// 두 레벨 위로 올라간다.
			arr[currentNode] = arr[grandPa];
			currentNode = grandPa;
			level = level - 2;
		}

		// 찾은 위치에 데이터 삽입
		arr[currentNode] = x;

		// 배열에 데이터가 다 찼으면 배열을 늘려준다.
		if(currentSize == arr.length - 1)
		{
			resize();
		}
	}

	// 가장 작은 수를 찾아 리턴합니다.
	public int min()
	{ // PRE: The heap is not empty
		assert !isEmpty();

		// 루트값 리턴 
		return arr[1];
	}

	// 가장 큰 수를 찾아 리턴합니다.
	public int max()
	{ // PRE: The heap is not empty
		assert !isEmpty();

		if(currentSize > 2)
		{
			// 두번째 레벨중 큰값 리턴  
			return (arr[3] < arr[2]) ? arr[2] : arr[3];
		}

		// 마지막 값이 max
		return arr[currentSize];
	}

	// 가장 작은 수를 삭제하고, 공백이 생긴 배열을 재조정합니다.
	public int deleteMin()
	{ // PRE: The heap is not empty
		assert !isEmpty();

		int minElement = arr[1]; // 가장작은수
		int currentNode = 1;
		int level = 0;
		int grandChild = 4;

		int lastElement = arr[currentSize--];

		while(grandChild <= currentSize)
		{
			int min = grandChild;
			
			// 현재 노드 와 child 노드 에서 가장 작은값 index 선택
			for(int i = 0; grandChild + i <= currentSize && i < 3; i++)
			{
				if(arr[grandChild + i] < arr[min])
				{
					min = grandChild + i;
				}
			}
			
			grandChild = min;
			if(lastElement <= arr[grandChild])
			{
				break;
			}
			
			// 두 단계 아래로 내려간다.(작은 값을 위로 올린다.)
			level += 2;
			arr[currentNode] = arr[grandChild];
			currentNode = grandChild;
			grandChild *= 4;
		}

		int child = currentNode * 2;
		if(child <= currentSize)
		{
			// 더 작은 자식값 index 선택 
			if(child + 1 <= currentSize && arr[child] > arr[child + 1])
			{
				child++;
			}
			
			// child 인덱스의 값이 마지각 값보다 크면 현재노드에 마지막 값을 저장하고 최소값 리턴. 
			if(arr[child] > lastElement)
			{
				arr[currentNode] = lastElement;
				return minElement;
			}
			else
			{
				arr[currentNode] = arr[child];
				currentNode = child;
				level++;
			}
		}

		int pa = currentNode / 2;
		if(level % 2 != 0)
		{
			pa /= 2;
		}
		
		if(arr[pa] < lastElement)
		{
			arr[currentNode] = arr[pa];
			currentNode = pa;
			level--;
		}

		while(level % 2 != 0 && level > 2)
		{
			pa = currentNode / 2;
			int grandPa = pa / 2;
			if(lastElement <= arr[grandPa])
			{
				break;
			}
			
			// 두 단계 위로 올라간다.(큰 값을 아래로 내린다.)
			arr[currentNode] = arr[grandPa];
			currentNode = grandPa;
			level = level - 2;
		}
		arr[currentNode] = lastElement;

		return minElement;
	}

	// 가장 큰 수를 삭제하고, 공백이 생긴 배열을 재조정합니다.
	public int deleteMax()
	{ // PRE: The heap is not empty
		assert !isEmpty();

		int maxElement;
		int currentNode;

		if(currentSize > 2)
		{
			// 가장 큰 값 & index 설정
			if(arr[3] < arr[2])
			{
				maxElement = arr[2];
				currentNode = 2;
			}
			else
			{
				maxElement = arr[3];
				currentNode = 3;
			}
		}
		else
		{
			// 마지막 값이 max
			return arr[currentSize--];
		}

		int lastElement = arr[currentSize--];

		int level = 1;
		int grandChild = currentNode * 4;

		while(grandChild <= currentSize)
		{
			int max = grandChild;
			
			// 현재 노드 와 child 노드 에서 가장 작은값 index 선택
			for(int i = 0; grandChild + i <= currentSize && i < 3; i++)
			{
				if(arr[grandChild + i] > arr[max])
				{
					max = grandChild + i;
				}
			}
			
			grandChild = max;
			if(lastElement >= arr[grandChild])
			{
				break;
			}
			
			// 두 단계 아래로 내려간다.(큰값을 위로 올린다.)
			level += 2;
			arr[currentNode] = arr[grandChild];
			currentNode = grandChild;
			grandChild *= 4;
		}

		int child = currentNode * 2;
		if(child <= currentSize)
		{
			// 더 큰 자식값 index 선택
			if(child + 1 <= currentSize && arr[child] < arr[child + 1])
			{
				child++;
			}
			
			// child 인덱스의 값이 마지각 값보다 작으면 현재노드에 마지막 값을 저장하고 최대값 리턴.
			if(arr[child] < lastElement)
			{
				arr[currentNode] = lastElement;
				return maxElement;
			}
			else
			{
				arr[currentNode] = arr[child];
				currentNode = child;
				level++;
			}
		}
		
		int pa = currentNode / 2;
		if(arr[pa] > lastElement)
		{
			arr[currentNode] = arr[pa];
			currentNode = pa;
			level--;
		}

		while(level % 2 == 0 && level > 2)
		{
			pa = currentNode / 2;
			int grandPa = pa / 2;
			if(lastElement >= arr[grandPa])
			{
				break;
			}
			
			// 두 단계 위로 올라간다.(작은 값을 아래로 내린다.)
			arr[currentNode] = arr[grandPa];
			currentNode = grandPa;
			level = level - 2;
		}
		arr[currentNode] = lastElement;
		return maxElement;
	}

	// 테스트를 위해 정의함.
	@Override
	public String toString()
	{
		StringBuilder s = new StringBuilder();
		s.append("The " + currentSize + " elements are [ ");
		if(currentSize > 0)
		{
			s.append(arr[1]);
			for(int i = 2; i <= currentSize; i++)
			{
				s.append(", " + arr[i]);
			}
		}
		s.append(" ]");
		return new String(s);
	}

	// Private methods go here

	// arr 의 2배크기 배열을 만든 후 기존 값을 복사한다.
	private void resize()
	{
		int[] old = arr;
		arr = new int[arr.length * 2];
		for(int i = 0; i < old.length; i++)
		{
			arr[i] = old[i];
		}
	}
}
