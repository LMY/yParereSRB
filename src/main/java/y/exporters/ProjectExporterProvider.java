package y.exporters;

import y.em.Project;

public class ProjectExporterProvider {
	
	private static ProjectExporter getExporter(String filename) {
		if (filename.toLowerCase().endsWith("prj") || filename.toLowerCase().endsWith("cem"))
			return new ProjectExporterPRJ();
		else
			return new ProjectExporterYEM();
	}
	
	
	public static Project importProject(String filename) throws Exception {
		final ProjectExporter exp = getExporter(filename);
		
		Project p = exp.importProject(filename);
		// if couldn't read, fall back to all possible exporters
		if (p == null || p.getSites() == null || p.getSites().length == 0)
			p = new ProjectExporterPRJ().importProject(filename);
		if (p == null || p.getSites() == null || p.getSites().length == 0)
			p = new ProjectExporterYEM().importProject(filename);
		
		if (p == null || p.getSites() == null || p.getSites().length == 0)	// warn if nothing imported
			throw new Exception("Il file \""+filename+"\" non sembra un file di progetto valido!");
		
		return p;
	}
}
