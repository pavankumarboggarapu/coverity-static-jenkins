import java.io.*;
import javax.servlet.http.HttpServletRequest;

public class CommandInjection {
    public static Process runCmd(HttpServletRequest request) throws IOException {
        //String filename = request.getParameter("filename");
        String filename = System.console().readLine();
        String[] cmdList = new String[]{"cat" + filename};
        ProcessBuilder builder = new ProcessBuilder(cmdList);
        Process process = builder.start();
        return(process);
    }
}
