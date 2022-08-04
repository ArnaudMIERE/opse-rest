package fr.sedoo.openopse.rest.domain.etc;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.tools.ant.DefaultLogger;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.ProjectHelper;
import org.eclipse.jgit.api.CloneCommand;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.PullCommand;
import org.eclipse.jgit.transport.CredentialsProvider;
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;

import fr.sedoo.openopse.rest.config.ApplicationConfig;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class TBLEtcWrapper {

	@Autowired
	private ApplicationConfig config;

	@Autowired
	private ConfigurableApplicationContext ctx;

	@PostConstruct
	public void initializePythonContext() {
		
		File scriptFolder = new File(config.getScriptFolderName());
		if (scriptFolder.exists()) {
			try {
				FileUtils.deleteDirectory(scriptFolder);
				
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		log.info("Python script folder: "+scriptFolder.getAbsolutePath());
		scriptFolder.mkdirs();
		try {
			initGit(scriptFolder);
		} catch (Exception e) {
			log.error("An error has occured while preparing script folder: "+ExceptionUtils.getStackTrace(e));
			ctx.close();
		}

		File envFolder = new File(config.getVirtualEnvironmentFolderName());
		if (! envFolder.exists()) {
			envFolder.mkdirs(); 
			try {
				log.info("Initializing Virtual env");
				initVirtualEnvironnement();
				log.info("Virtual env initialisation completed");
			} catch (Exception e) {
				log.error("An exception has occured while installing Virtual env: "+ExceptionUtils.getStackTrace(e));
				ctx.close();
			}
		}

		
	}

	private void initGit(File scriptFolder) throws Exception {
		ShellWrapper.deleteScript(config.getConvertMneToPngLauncherScript(), scriptFolder);
		ShellWrapper.deleteScript(config.getSpipLauncherScript(), scriptFolder);
		log.info("Cloning python script repo");
		CloneCommand cloneCommand = Git.cloneRepository();
		cloneCommand.setURI("https://github.com/evrard1012/OpenOPSE.git");
		cloneCommand.setCredentialsProvider( getCredentialProvider());
		cloneCommand.setDirectory(scriptFolder);
		
	    try (Git git = cloneCommand.call()) {
	    	log.info("Cloning completed");
	    }
	    catch (Exception e) {
			log.error("An error has occured while cloning Python repository: "+ExceptionUtils.getStackTrace(e));
			ctx.close();
		}
	    
	   addExecutableRightToLaunchers(scriptFolder);
		
		
		
		File antBuildFile = new File(scriptFolder, "build.xml");
		
		if (antBuildFile.exists()) {
			log.info("Executiong post-pull tasks");
			Project project=new Project();
			ProjectHelper helper = ProjectHelper.getProjectHelper();
			helper.parse(project, antBuildFile);
			project.addBuildListener(getDefaultLogger());
			project.setBaseDir(scriptFolder);
			String defaultTarget = project.getDefaultTarget();
			try {
				project.executeTarget(defaultTarget);
				log.info("Post-pull tasks executed successfully");
			} catch (Exception e) {
				log.error("An error has occured while executing post-pull tasks: "+ExceptionUtils.getStackTrace(e));
				throw e;
			}
			
			
			
		} else {
			log.error("No ant build file");
		}
		
	}
	
	 private void addExecutableRightToLaunchers(File scriptFolder) throws IOException {
		 File neoNarvalLauncherShell = ShellWrapper.copyScript(config.getConvertMneToPngLauncherScript(), scriptFolder);
			ShellWrapper.setExecutable(neoNarvalLauncherShell);
			
			File spipLauncherShell = ShellWrapper.copyScript(config.getSpipLauncherScript(), scriptFolder);
			ShellWrapper.setExecutable(spipLauncherShell);
		
	}

	private static DefaultLogger getDefaultLogger(){
	        DefaultLogger consoleLogger=new DefaultLogger();
	        consoleLogger.setErrorPrintStream(System.err);
	        consoleLogger.setOutputPrintStream(System.out);
	        consoleLogger.setMessageOutputLevel(Project.MSG_VERBOSE);
	        return consoleLogger;
	    }

	private CredentialsProvider getCredentialProvider() {
		return new UsernamePasswordCredentialsProvider( "ArnaudMIERE", "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" ) ;
	}
	
	public void generateThumbnails(String src, String dest, String filename) throws IOException, InterruptedException {
		src = config.getOpenOpseFolderName()+"uuid/tiff/MNE/"+filename;
		File directoryInit = new File (src);
		dest = config.getOpenOpseFolderName()+"uuid/thumbnails/tiff/MNE/"+filename;
		
		ProcessBuilder processBuilder = new ProcessBuilder(config.getScriptFolderName());
		processBuilder.directory(directoryInit);
		Process process = processBuilder.start();
		int exitCode = process.waitFor();
		log.info("No errors should be detected", 0, exitCode);
	}

	

	private Map<String, String> getProperties() {
		Map<String, String> properties = new HashMap<>();
		properties.put("PYENV", config.getVirtualEnvironmentFolderName());
		return properties;
	}
	
	public String updateScripts() throws Exception {
		File scriptFolder = new File(config.getScriptFolderName());
		if (! scriptFolder.exists()) {
			initGit(scriptFolder); 
			return "Script updated";
		} else {
			log.info("Pulling python script repo");
			try (Git git = Git.open(scriptFolder)) {
				PullCommand pull = git.pull();
				pull.setCredentialsProvider( getCredentialProvider());
				pull.call();
				addExecutableRightToLaunchers(scriptFolder);
				return "Scripts updated";
			} catch (Exception e) {
				log.error("An error has occured while pulling Python repository: "+ExceptionUtils.getStackTrace(e));
				throw new Exception("An error has occured while updating scripts");
			}
		} 
	}

	public String updateVirtualEnvironnement() throws Exception {
		File envFolder = new File(config.getVirtualEnvironmentFolderName());
		if (envFolder.exists()) {
			try {
				FileUtils.deleteDirectory(envFolder);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		envFolder.mkdirs();
		initVirtualEnvironnement();
		return "Environnement updated";
	}
	
	private void initVirtualEnvironnement() throws Exception {
		File installShell = null;
		File scriptFolder = new File(config.getScriptFolderName());
		if (!scriptFolder.exists()) {
			scriptFolder.mkdirs();
		}
		try {
			installShell = ShellWrapper.copyScript(config.getInstallScript(), scriptFolder);
			ShellWrapper.setExecutable(installShell);

			ExecutionResult executionResult = new ExecutionResult();
			log.info("Executing install.sh");
			int result = ShellWrapper.executeShell(installShell, getProperties(), null, executionResult);

			if (result != 0){
				throw new Exception("An error has occured while installing ETC: "+executionResult.getContent());
			} else {
				log.info(executionResult.getContent());
				log.info("install.sh completed");
			}
		} catch (Exception e) {
			log.error("An error has occured while initializing/updating Python context: "+ExceptionUtils.getStackTrace(e));
			throw e;
		}
	}
}
