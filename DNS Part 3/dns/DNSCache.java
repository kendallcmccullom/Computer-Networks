package dns;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.HashMap;
/**
 * Class representing a cache of stored DNS records.
 * @version 1.0
 */
public class DNSCache {
	private ArrayList<DNSRecord> results;
	private ArrayList<DNSRecord> initiate;
	private String name;

	public DNSCache() {
		results = new ArrayList<DNSRecord>();
	}

	public void addCache(DNSMessage val) {
		initiate = val.getAnswers();
		int end = initiate.size();
		for (int i = 0; i < end; i++) {
			DNSRecord curr = initiate.get(i);
			results.add(curr);	
		}
		if (!val.getQuestionType().equals("A")) {
			return;
		}
	}

	public ArrayList<DNSRecord> getRecords(String name, String type, String rclass) {
		var matches = new ArrayList<DNSRecord>();
		if(results.size() == 0){
			return matches;
		}
		for (var record : results) {
			if (record.getName().equals(name) && record.getTypeStr().equals(type)
					&& record.getClassStr().equals(rclass)) {
				matches.add(record);
					}
		}
		return matches;
	}
	public void remove() {
		Iterator<DNSRecord> iter = results.iterator();
		while(iter.hasNext()) {
			DNSRecord answer = iter.next();
			int ttl = answer.getTTL();
			Instant time = answer.getTimeStamp();
			Instant checking = time.plusSeconds(ttl);
			int value = Instant.now().compareTo(checking);
			if (value > 0) {
				iter.remove();
			}

		}
	}
}




