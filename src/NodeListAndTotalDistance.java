import java.util.ArrayList;
import java.util.List;

public class NodeListAndTotalDistance {
    public List<Integer> nodeList = new ArrayList<>();
    public int totalDistance;

    public List<Integer> getNodeList() {
        return nodeList;
    }

    public void setNodeList(List<Integer> nodeList) {
        this.nodeList = nodeList;
    }

    public int getTotalDistance() {
        return totalDistance;
    }

    public void setTotalDistance(int totalDistance) {
        this.totalDistance = totalDistance;
    }

    @Override
    public String toString() {
        List<Integer> modifiedNodeList = new ArrayList<>();
        for (Integer value : nodeList) {
            modifiedNodeList.add(value + 1);
        }
        return modifiedNodeList + "\r\n" + totalDistance;
    }
}
