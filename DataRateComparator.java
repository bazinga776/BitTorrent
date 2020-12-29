import java.util.Comparator;

public class DataRateComparator implements Comparator<PeerInfo> {

	private boolean isConstructor ;

	public DataRateComparator() {
		this.isConstructor = true;
	}

	public DataRateComparator(boolean isConstructor) {
		this.isConstructor = isConstructor;
	}

	public int compare(PeerInfo pInfo1, PeerInfo pInfo2) {
		if (pInfo1 == null && pInfo2 == null)
			return 0;

		if (pInfo1 == null)
			return 1;

		if (pInfo2 == null)
			return -1;

		if (pInfo1 instanceof Comparable) {
			if (isConstructor) {
				return pInfo1.compareTo(pInfo2);
			} else {
				return pInfo2.compareTo(pInfo1);
			}
		} 
		else {
			if (isConstructor) {
				return pInfo1.toString().compareTo(pInfo2.toString());
			} else {
				return pInfo2.toString().compareTo(pInfo1.toString());
			}
		}
	}

}
