package cssto_82714.esii;


import java.io.File;
import java.io.IOException;
import java.util.*;

import org.apache.commons.io.FileUtils;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.api.errors.InvalidRemoteException;
import org.eclipse.jgit.api.errors.TransportException;
import org.eclipse.jgit.errors.CorruptObjectException;
import org.eclipse.jgit.errors.IncorrectObjectTypeException;
import org.eclipse.jgit.errors.MissingObjectException;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevObject;
import org.eclipse.jgit.revwalk.RevTree;
import org.eclipse.jgit.revwalk.RevWalk;
import org.eclipse.jgit.treewalk.TreeWalk;
import org.eclipse.jgit.treewalk.filter.PathFilter;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;


/**
 * @author Catarina Teodoro
 *
 */
public class Covidgraphspread 
{


	public static Git git;
	public static Document doc;

	public static void main( String[] args ) throws InvalidRemoteException, TransportException, GitAPIException, IOException
	{

		createTable();
		openRepository();
		getFilesFromTags();

		System.out.println(cgi_lib.Header());
		Hashtable form_data = cgi_lib.ReadParse(System.in);
		System.out.println(doc.select("table"));
		System.out.println(doc.select("style"));
		System.out.println(cgi_lib.HtmlBot());

	}

	/**
	 * Lists all tags from repository and searches for the files associated to each tag
	 */
	public static void getFilesFromTags() {
		Repository repository = git.getRepository();
		try {

			List<Ref> call = git.tagList().call();

			for (Ref ref : call) {
				String tag = ref.getName();
				RevWalk walk = new RevWalk(repository);
				try {
					RevObject object = walk.parseAny(ref.getObjectId());

					if (object instanceof RevCommit) {
						findFileInCommit(ref.getObjectId(), tag);
					}
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

		} catch (GitAPIException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	/**
	 * 
	 * Given a commitId and the tag associated searches for the file covid19spreading.rdf
	 * If the file is found the information is added to the html table
	 * 
	 * @param commitId
	 * @param tag
	 * @throws MissingObjectException
	 * @throws IncorrectObjectTypeException
	 * @throws CorruptObjectException
	 * @throws IOException
	 */
	public static void findFileInCommit(ObjectId commitId, String tag) throws MissingObjectException, IncorrectObjectTypeException, CorruptObjectException, IOException {
		Repository repository = git.getRepository();

		try(RevWalk revWalk = new RevWalk(repository)){
			RevCommit commit = revWalk.parseCommit(commitId);

			RevTree tree = commit.getTree();

			try(TreeWalk treeWalk = new TreeWalk(repository)){
				treeWalk.addTree(tree);
				treeWalk.setRecursive(true);
				treeWalk.setFilter(PathFilter.create("covid19spreading.rdf"));
				if(!treeWalk.next()) {
					throw new IllegalStateException("Didnt find file");
				}

				String[] tags = tag.split("/");
				String finalTag = tags[2];
				addInfoToHTML(commit, finalTag);
			}
			revWalk.dispose();
		}
	}

	/**
	 * 
	 * Given a commit creates the visual data link for the covid19spreading.rdf file
	 * 
	 * @param commit
	 * @return
	 */
	public static String getHyperlinkOfFileFromCommit(RevCommit commit) {
		String hyperlink = "http://www.visualdataweb.de/webvowl/#iri=https://raw.githubusercontent.com/vbasto-iscte/ESII1920/" + commit.getName() + "/covid19spreading.rdf";
		return hyperlink;
	}

	/**
	 * Creates an html table and adds style to it
	 */
	public static void createTable() {
		doc = Jsoup.parse("<html></html>");
		doc.body().addClass("body-styles-cls");
		doc.body().appendElement("div");
		doc.body().appendElement("table").attr("id", "t01");
		doc.body().appendElement("style");
		Element table = doc.select("table").get(0);
		table.append("<tr></tr>");
		Element headersRow = table.select("tr").get(0);
		headersRow.append("<th>File timestamp</th>");
		headersRow.append("<th>File name</th>");
		headersRow.append("<th>File tag</th>");
		headersRow.append("<th>Tag Description</th>");
		headersRow.append("<th>Spread Visualization Link</th>");

		Element style = doc.select("style").get(0);
		style.append("table, th, td {border: 1px solid grey; border-collapse: collapse;}");
		style.append("th, td {\r\n" + 
				"  padding: 15px;\r\n" + 
				"}");
		style.append("th {\r\n" + 
				"  text-align: center;\r\n" + 
				"}");
		style.append("table {\r\n" + 
				"  border-spacing: 5px;\r\n" + 
				"}");
	}

	/**
	 * 
	 * Given a commit and it's tag adds all the information to the html table
	 * 
	 * @param commit
	 * @param tag
	 */
	public static void addInfoToHTML(RevCommit commit, String tag) {

		Date time = commit.getAuthorIdent().getWhen();
		String description = commit.getFullMessage();

		String[] rowData = new String[5];
		rowData[0] = time.toString();
		rowData[1] = "covid19spreading.rdf";
		rowData[2] = tag;
		rowData[3] = description;
		rowData[4] = "<a href='" + getHyperlinkOfFileFromCommit(commit) + "'>Link</a>";


		Element table = doc.select("table").get(0);
		Element newRow = table.append("<tr></tr>");
		int newRowIndex = table.select("tr").size() - 1;
		for(int i =0; i < rowData.length; i++) {
			table.select("tr").get(newRowIndex).append("<td>" + rowData[i] + "</td>");
		}
	}

	/**
	 * Accesses the git repository to export covid19spreading.rdf files
	 */
	public static void openRepository() {

		File f = new File("./ESII1920");
		if (f.exists() && f.isDirectory()) {
			try {
				FileUtils.cleanDirectory(f);
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}
		try {
			git = Git.cloneRepository()
					.setURI("https://github.com/vbasto-iscte/ESII1920")
					.call();
		} catch (GitAPIException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
}
