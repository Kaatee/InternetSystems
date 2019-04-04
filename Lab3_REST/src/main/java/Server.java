import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

@Path("/test")
public class Server {
    public static final String EXAMPLE = "Udalo sie!";

    @GET
    @Produces("text/plain")
    public String getStudents(){
        return EXAMPLE;
    }
}