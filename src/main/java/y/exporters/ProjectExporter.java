package y.exporters;

import y.em.Project;

interface ProjectExporter {
	public Project importProject(String filename) throws Exception;
}
