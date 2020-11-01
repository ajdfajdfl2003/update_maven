package tw.idv.angus;

import tw.idv.angus.docker.ContainerConfig;
import tw.idv.angus.docker.DockerClient;
import tw.idv.angus.docker.exception.MyDockerClientException;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Instant;
import java.util.Arrays;
import java.util.List;

public class RunDocker {
    private static final String dependencyPackage = "gson-version";
    private static final String dependencyPackageVersion = "2.8.1";
    private static final String repoName = "target_of_update_maven";
    private static final String workDir = "/opt/git/";
    private static final String m2 = workDir + ".m2";
    private static final DockerClient dockerClient = new DockerClient();

    public static void main(String[] args) throws MyDockerClientException {
        runGitClone();

        runMaven(Arrays.asList("mvn", "versions:set-property", "-Dproperty=" + dependencyPackage, "-DnewVersion=" + dependencyPackageVersion));
        runMaven(Arrays.asList("mvn", "-U", "compile", "test"));

        runGit(Arrays.asList("add", "."));
        runGit(Arrays.asList("commit", "-m", "update " + dependencyPackage + " to " + dependencyPackageVersion));
        runGit(Arrays.asList("push", "origin", "develop:develop"));
    }

    private static void runGit(List<String> commands) throws MyDockerClientException {
        dockerClient.pullImage("alpine/git", "latest");
        ContainerConfig config = new ContainerConfig("alpine/git:latest")
                .withContainerName("git-" + Instant.now().toEpochMilli())
                .withHostName("git")
                .withVolume(workDir + ":" + workDir)
                .withVolume(System.getProperty("user.home") + ":/root")
//                .withAutoRemove(true)
                .withWorkDir(workDir + repoName)
                .withCommands(commands);
        dockerClient.run(config);

        checkDir(workDir + repoName);
    }

    private static void runGitClone() throws MyDockerClientException {
        dockerClient.pullImage("alpine/git", "latest");
        ContainerConfig config = new ContainerConfig("alpine/git:latest")
                .withContainerName("git-" + Instant.now().toEpochMilli())
                .withHostName("git")
                .withVolume(workDir + ":" + workDir)
                .withVolume(System.getProperty("user.home") + "/:/root/")
//                .withAutoRemove(true)
                .withCommands(Arrays.asList("clone", "-b", "develop", "git@github.com:ajdfajdfl2003/" + repoName + ".git", workDir + repoName));
        dockerClient.run(config);

        checkDir(workDir + repoName);
    }

    private static void runMaven(List<String> commands) throws MyDockerClientException {
        checkDir(m2);
        dockerClient.pullImage("maven", "3.6.3-jdk-8");
        ContainerConfig config = new ContainerConfig("maven:3.6.3-jdk-8")
                .withContainerName("maven-versionUpdate" + Instant.now().toEpochMilli())
                .withHostName("maven")
                .withVolume(m2 + ":/root/.m2")
                .withVolume(workDir + ":" + workDir)
                .withWorkDir(workDir + repoName)
                .withAutoRemove(true)
                .withCommands(commands);
        dockerClient.run(config);
    }

    private static void checkDir(String location) throws MyDockerClientException {
        Path path = Paths.get(location);
        if (!Files.exists(path)) throw new MyDockerClientException(location + " not found");
    }
}
