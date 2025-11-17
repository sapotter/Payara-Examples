package fish.payara.examples.testcontainers;

import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.utility.DockerImageName;
import org.testcontainers.utility.MountableFile;

public class PayaraMicroContainer extends GenericContainer<PayaraMicroContainer> {
    
    private static final int DEFAULT_PORT = 8080;
    private static final String DEFAULT_CONTEXT_PATH = "/";
    protected static final String CONTEXT = "ObservabilityTool";
    
    public PayaraMicroContainer(DockerImageName dockerImageName) {
        super(dockerImageName);
        withExposedPorts(DEFAULT_PORT);
        waitingFor(Wait.forLogMessage(".*Payara Micro .* ready.*\\n", 1));

    }
    
    public PayaraMicroContainer withDeploymentPath(String warPath) {
        withCopyFileToContainer(
            MountableFile.forHostPath(warPath), 
            "/opt/payara/deployments/application.war"
        );
        return this;
    }
    
    public String getApplicationUrl() {
        return String.format(
            "http://%s:%d%s",
            getHost(),
            getMappedPort(DEFAULT_PORT),
            DEFAULT_CONTEXT_PATH
        );
    }
}