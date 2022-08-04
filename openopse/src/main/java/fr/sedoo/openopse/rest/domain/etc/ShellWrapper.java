package fr.sedoo.openopse.rest.domain.etc;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.attribute.PosixFilePermission;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringJoiner;

import org.apache.commons.io.FileUtils;
import org.springframework.core.io.ClassPathResource;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ShellWrapper {
	
	public static int executeShell(File shell, Map<String, String> properties, List<String> parameters, ExecutionResult executionResult) throws Exception {
		ProcessBuilder builder = new ProcessBuilder();


		List<String> commandLine = new ArrayList<>();
		commandLine.add(shell.getAbsolutePath());
		if (parameters != null) {
			commandLine.addAll(parameters);
		}
		builder.command(commandLine);
		Map<String, String> env = builder.environment();
		builder.directory(shell.getParentFile());
		if (properties != null) {
			env.putAll(properties);
		}

		Process process = builder.start();

		final BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));

		StringJoiner sj = new StringJoiner(System.getProperty("line.separator"));
		reader.lines().iterator().forEachRemaining(sj::add);
		String result = sj.toString();
		int exitCode = process.waitFor();
		executionResult.setContent(result);
		process.destroy();

		return exitCode;
	}
	
	public static File setExecutable(File file) throws IOException {
		Set<PosixFilePermission> perms = new HashSet<>();
		perms.add(PosixFilePermission.OWNER_READ);
		perms.add(PosixFilePermission.OWNER_WRITE);
		perms.add(PosixFilePermission.OWNER_EXECUTE);

		Files.setPosixFilePermissions(file.toPath(), perms);
		return file;

	}
	
	public static File copyScript(String fileName, File tgtFolder) throws IOException {
		ClassPathResource classPathResource = new ClassPathResource(fileName);
		if (!tgtFolder.exists()) {
			tgtFolder.mkdirs();	
		}
		File tgtFile = new File(tgtFolder, classPathResource.getFilename());
		if (tgtFile.exists()) {
			tgtFile.delete();
		}
		FileUtils.copyInputStreamToFile(classPathResource.getInputStream(), tgtFile);
		return tgtFile;
	}
	
	public static void deleteScript(String fileName, File tgtFolder) throws IOException {
		ClassPathResource classPathResource = new ClassPathResource(fileName);
		File tgtFile = new File(tgtFolder, classPathResource.getFilename());
		if (tgtFile.exists()) {
			tgtFile.delete();
		}
	}

}
