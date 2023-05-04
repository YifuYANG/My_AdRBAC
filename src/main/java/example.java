import org.wso2.balana.Balana;
import org.wso2.balana.finder.impl.FileBasedPolicyFinderModule;

import java.io.File;
import java.io.IOException;

public class example {

    private static Balana balana;

    private static void initBalana() {
        try{
            // using file based policy repository. so set the policy location as system property
            String policyLocation = (new File(".")).getCanonicalPath() + File.separator + "resources";
            System.setProperty(FileBasedPolicyFinderModule.POLICY_DIR_PROPERTY, policyLocation);
        } catch (IOException e) {
            System.err.println("Can not locate policy repository");
        }
        // create default instance of Balana
        balana = Balana.getInstance();
    }
}
