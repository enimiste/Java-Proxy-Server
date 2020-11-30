import com.beust.jcommander.DynamicParameter;
import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import static java.util.Collections.singletonMap;

public class Main {

    // Main method for the program
    public static void main(String[] args) {
        Args cmd = new Args();
        JCommander jcmd = JCommander.newBuilder()
                .addObject(cmd)
                .build();
        try {

            jcmd.parse(args);
            // Create an instance of Proxy and begin listening for connections
            Proxy myProxy = new Proxy(cmd.portNumber);
            cmd.urlOverrideByUrl
                    .forEach(Proxy::addUrlOverrides);
            cmd.urlOverrideByFile
                    .entrySet()
                    .stream()
                    .flatMap(e -> singletonMap(e.getKey(), new File(e.getValue())).entrySet().stream())
                    .forEach(e -> Proxy.addCachedPage(e.getKey(), e.getValue()));
            myProxy.listen();
        } catch (Exception e) {
            jcmd.usage();
            System.err.println(e.getMessage());
        }
    }

    static class Args {
        @Parameter(names = {"-p", "--port"},
                required = true,
                description = "Port number on which the proxy will listen")
        int portNumber;
        @DynamicParameter(names = {"-D"},
                description = "Add urls to be overridden. -DrequestedUrl=overridingUrl")
        Map<String, String> urlOverrideByUrl = new HashMap<>();
        @DynamicParameter(names = {"-F"},
                description = "Add urls to be overridden by static files. -FrequestedUrl=pathToOverrideFile")
        Map<String, String> urlOverrideByFile = new HashMap<>();
    }
}
