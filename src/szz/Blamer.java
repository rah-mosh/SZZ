package szz;

/**
 * Created by usi on 11/29/16.
 */

import com.gitblit.models.PathModel;
import com.gitblit.models.PathModel.PathChangeModel;
import org.eclipse.jgit.api.BlameCommand;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.blame.BlameResult;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevTree;
import org.eclipse.jgit.treewalk.*;
import java.io.ByteArrayOutputStream;
import org.eclipse.jgit.treewalk.filter.PathFilter;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.ObjectLoader;
import java.io.IOException;
import java.io.ByteArrayInputStream;
import org.apache.commons.io.IOUtils;

import java.util.List;
import java.util.Set;
import java.util.HashSet;


public class Blamer
{
    private PathChangeModel path;
    private Repository repository;

    public Blamer(PathModel.PathChangeModel path, Repository repository) {
        this.path=path;
        this.repository=repository;
    }

    public PathChangeModel getPath() {
        return  path;
    }

    public Repository getRepository() {
        return repository;
    }
    public int countFiles(RevCommit rCommit, String path) throws java.io.IOException{
        RevTree tree = rCommit.getTree();
        try (TreeWalk treeWalk = new TreeWalk(repository.getGitRepository())) {
            treeWalk.addTree(tree);
            treeWalk.setRecursive(true);
            treeWalk.setFilter(PathFilter.create(path));
            if (!treeWalk.next()) {
                throw new IllegalStateException("Did not find expected file");

            }

            ObjectId objectId = treeWalk.getObjectId(0);
            ObjectLoader loader = repository.getGitRepository().open(objectId);

            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            loader.copyTo(stream);


            return IOUtils.readLines(new ByteArrayInputStream(stream.toByteArray())).size();
        }
    }



    public BlameResult blameGeneration(Commit commit) throws IOException, GitAPIException {

        BlameCommand bCommand= new BlameCommand(repository.getGitRepository());
        bCommand.setStartCommit(commit.getGitCommit());
        bCommand.setFilePath(path.path);
        BlameResult blameRes = bCommand.call();
//        Set<RevCommit> listCommiter = new HashSet<>();
//        int lines = countFiles(commit.getGitCommit(),path.path);
//        System.out.println(lines);
//        for (int i = 0; i < lines; i++) {
//            RevCommit bCommit = blameRes.getSourceCommit(i);
//            listCommiter.add(bCommit);
//
////            System.out.println("blame commiter: "+bCommit.getCommitterIdent().getName()+ "blame commit:  " + bCommit.getName());
////            RawText  rText= blameRes.getResultContents();
////            rText.getLineDelimiter();
////            for (int j=0; i<rText.size(); j++){
////            System.out.println(rText.getString(j));
//            }
//        listCommiter.forEach(commiter->System.out.println(commiter));
//        return listCommiter;
        return blameRes;

    }
}
