import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.util.Comparator;
import java.util.Iterator;

public class DomainTally
{

	private int k;
	private int nProcessed;
	private JHUHashMap<String, Integer> hashmap;
	private MinPQ<DomainCount> topDomain;

	public DomainTally(String strK)
	{
		try
		{
			k = Integer.parseInt(strK);
		}
		catch(NumberFormatException e)
		{
			k = 0;
		}

		if(k <= 0)
		{
			throw new IllegalArgumentException("k > 0");
		}

		// System.out.println("*k = " + k);

		hashmap = new MyJHUHashMap<String, Integer>();
		nProcessed = 0;
		topDomain = new MinPQ<DomainTally.DomainCount>(DomainCountGreaterComparator);
	}

	private String parseUrl(String url) throws MalformedURLException
	{
		// remove protocol
		int index = url.indexOf("://");
		if(index != -1)
		{
			url = url.substring(index + 3);
		}

		// remove port
		index = url.indexOf(":");
		if(index != -1)
		{
			url = url.substring(0, index);
		}
		else
		{
			// remove path
			index = url.indexOf("/");
			if(index != -1)
			{
				url = url.substring(0, index);
			}
		}

		// remove all except second & top level domain
		index = url.lastIndexOf('.');
		// TODO top level 도메인 체크 ??
		if(index != -1)
		{
			// System.out.println(url);
			index = url.substring(0, index).lastIndexOf('.');

			if(index != -1)
			{
				url = url.substring(index + 1);
			}
		}

		// lower case 변환
		return url.toLowerCase();
	}

	private void doReport()
	{
		System.out.println(String.format("\nREPORT: After collecting %d URLs total, the top %d domains are:", nProcessed, k));

		// print top k url;
		// TODO 오름차 순으로 출력 ??
		MinPQ<DomainCount> topDomain_new = new MinPQ<DomainTally.DomainCount>(DomainCountGreaterComparator);
		while(!topDomain.isEmpty())
		{
			DomainCount dc = topDomain.delMin();
			System.out.println(dc.domain + " : " + dc.count);
			topDomain_new.insert(dc);
		}

		topDomain = topDomain_new;
		System.out.println("");
	}

	// 가장 많이 카운트된 k 개를 update 한다.
	private void updateTopDomain(String domain, Integer freq)
	{

		Iterator<DomainCount> iter = topDomain.iterator();
		while(iter.hasNext())
		{
			DomainCount dc = iter.next();
			// 기존에 있었으면 count 만 업데이트 하고 리턴
			if(dc.domain.equalsIgnoreCase(domain))
			{
				dc.count = freq;
				return;
			}
		}

		// 새로운 top k domain
		topDomain.insert(new DomainCount(domain, freq));

		// 항상 k 개만 유지
		if(topDomain.size() > k)
		{
			topDomain.delMin();
		}
	}

	// 읽은 도메인을 카운팅 한다.
	private void processReadDomain(String domain)
	{
		Integer freq = hashmap.get(domain);
		if(freq == null)
		{
			freq = 1;
		}
		else
		{
			freq++;
		}
		hashmap.put(domain, freq);

		updateTopDomain(domain, freq);

		nProcessed++;
	}

	// url 파일 처리
	public void processFile(String infile)
	{
		System.out.println("Processing the file %s..." + infile);

		String thisLine = null;
		try
		{
			BufferedReader br = new BufferedReader(new FileReader(infile));
			while((thisLine = br.readLine()) != null)
			{

				String url = thisLine.trim();

				if(url.equalsIgnoreCase("report"))
				{
					doReport();
					continue;
				}

				String domain = parseUrl(url);
				// System.out.println(domain);
				processReadDomain(domain);
			}
			br.close();
		}
		catch(IOException e)
		{
			e.printStackTrace();
			throw new IllegalArgumentException("can not open file!");
		}
	}

	// 키보드 입력을 받아 처리
	public void doInteractiveStage() throws IOException
	{
		System.out.println("You can enter an additional URL to process, 'report' to see the top 2, or 'quit'.");

		InputStreamReader in = new InputStreamReader(System.in);
		BufferedReader keyboard = new BufferedReader(in);

		while(true)
		{
			System.out.print("What now (a url, 'report', or 'quit')? ");
			String line = keyboard.readLine().trim();

			if(line.equalsIgnoreCase("quit"))
			{
				System.out.println("\nGoodbye.");
				break;
			}

			if(line.equalsIgnoreCase("report"))
			{
				doReport();
			}

			// url process
			String domain = parseUrl(line);
			// System.out.println(domain);
			processReadDomain(domain);

			System.out.println("");
		}
	}

	private static class DomainCount
	{
		String domain;
		int count;

		public DomainCount(String domain, int count)
		{
			this.domain = domain;
			this.count = count;
		}
	}

	// count 오름차순으로 정렬을 위한 Comparator
	private static Comparator<DomainCount> DomainCountGreaterComparator = new Comparator<DomainCount>()
	{
		public int compare(DomainCount d1, DomainCount d2)
		{
			if(d1 == null || d2 == null)
			{
				return -1;
			}

			int diff = d1.count - d2.count;
			if(diff == 0) return 1; // count 가 같을 경우 기존 값이 유지되도록 1을 리턴

			return diff;
		}
	};

	public static void main(String args[]) throws IOException
	{
		if(args.length != 2)
		{
			throw new IllegalArgumentException("need 2 argument!");
		}

		DomainTally domainTally = new DomainTally(args[0]);
		domainTally.processFile(args[1]);

		System.out.println("File processing complete, beginning interactive stage.");
		System.out.println("\n-=-=-=-=-=-=-\n");

		domainTally.doInteractiveStage();
	}

}