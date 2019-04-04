import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonPropertyOrder({"uri", "requestCount", "sendBytesCount", "receivedBytesCount"})
public class CsvRow {
    @JsonProperty("uri")
    private String uri;

    @JsonProperty("requestCount")
    private Integer requestCount;

    @JsonProperty("sendBytesCount")
    private Integer sendBytesCount;

    @JsonProperty("receivedBytesCount")
    private Integer receivedBytesCount;

    public CsvRow(String uri, int requestCount, int sendBytesCount, int receivedBytesCount){
        this.uri = uri;
        this.requestCount = requestCount;
        this.sendBytesCount = sendBytesCount;
        this.receivedBytesCount = receivedBytesCount;
    }

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public int getRequestCount() {
        return requestCount;
    }

    public void setRequestCount(int requestCount) {
        this.requestCount = requestCount;
    }

    public int getSendBytesCount() {
        return sendBytesCount;
    }

    public void setSendBytesCount(int sendBytesCount) {
        this.sendBytesCount = sendBytesCount;
    }

    public int getReceivedBytesCount() {
        return receivedBytesCount;
    }

    public void setReceivedBytesCount(int receivedBytesCount) {
        this.receivedBytesCount = receivedBytesCount;
    }
}
