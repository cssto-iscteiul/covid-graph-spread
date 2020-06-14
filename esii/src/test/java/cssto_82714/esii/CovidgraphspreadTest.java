package cssto_82714.esii;

import java.io.IOException;
import java.util.List;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.errors.MissingObjectException;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevObject;
import org.eclipse.jgit.revwalk.RevWalk;

import junit.framework.TestCase;

public class CovidgraphspreadTest extends TestCase {

	protected void setUp() throws Exception {
		super.setUp();
	}


	public void testGetHyperlinkOfFileFromCommit() {
		
		Covidgraphspread.openRepository();

		String link = "";
		try {

			List<Ref> call = Covidgraphspread.git.tagList().call();
			RevWalk walk = new RevWalk(Covidgraphspread.git.getRepository());
			RevCommit commit;
			try {
				commit = (RevCommit)walk.parseAny(call.get(0).getObjectId());
				link = Covidgraphspread.getHyperlinkOfFileFromCommit(commit);
			} catch (MissingObjectException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}	
		} catch (GitAPIException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		assertNotNull("Hyperlink", link);

	}



	public void testAddInfoToHTML() throws GitAPIException {
		List<Ref> call = Covidgraphspread.git.tagList().call();
		RevWalk walk = new RevWalk(Covidgraphspread.git.getRepository());
		RevCommit commit = null;
		String tag = "tag";

		int tableSize = Covidgraphspread.doc.select("table").select("tr").size();

		try {
			commit = (RevCommit)walk.parseAny(call.get(0).getObjectId());
		} catch (MissingObjectException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		Covidgraphspread.addInfoToHTML(commit, tag);

		assertEquals("Checking if rows were added", tableSize +1, Covidgraphspread.doc.select("table").select("tr").size());

	}



	public void testCreateTable() {
		Covidgraphspread.createTable();
		assertEquals("Didnt create table", 0, Covidgraphspread.doc.select("t01").size());
	}

	public void testOpenRepository() { 

		
		Git git = Covidgraphspread.git;

		assertNotNull("Git repository", git);

	}
	
	public void testFindFileInCommit() { 

		Repository repository = Covidgraphspread.git.getRepository();
		
		try {
			List<Ref> call = Covidgraphspread.git.tagList().call();

			for (Ref ref : call) {
				String tag = ref.getName();
				RevWalk walk = new RevWalk(repository);
				try {
					RevObject object = walk.parseAny(ref.getObjectId());

					if (object instanceof RevCommit) {
						Covidgraphspread.findFileInCommit(ref.getObjectId(), tag);
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

		assertNotNull("Checking if table was updated", Covidgraphspread.doc.select("table"));
		

	}
	
	public void testGetFilesFromTags() {
		
		Covidgraphspread.getFilesFromTags();
		
		assertNotNull("Checking if table was updated", Covidgraphspread.doc.select("table"));
		
	}



}
