package cs601.webmail.managers;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryparser.classic.MultiFieldQueryParser;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.RAMDirectory;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class SearchService {
    public static Connection c;
    public static RAMDirectory directory;

    public static RAMDirectory getDir() throws SQLException, IOException, ClassNotFoundException {
        if (directory == null) {
            directory = new RAMDirectory();
        }
        return directory;
    }

    public static void createSearchData() throws IOException, SQLException, ClassNotFoundException {
        IndexWriter writer = new IndexWriter(directory, new IndexWriterConfig(new StandardAnalyzer()));
        c = new DBManager().getConnection();
        PreparedStatement find = c.prepareStatement("SELECT * FROM Mails WHERE mailType != \"empty\";");
        ResultSet rs = find.executeQuery();

        while (rs.next()) {
            Document doc = new Document();
            int mailID = rs.getInt("mailID");
            int userID = rs.getInt("userID");
            String sender = rs.getString("sender");
            String recipient = rs.getString("recipient");
            String cc = rs.getString("cc");
            String bcc = rs.getString("bcc");
            String subject = rs.getString("subject");
            String content = rs.getString("content");
            String date = rs.getString("emailDate");
            String mailType = rs.getString("mailType");
            String uidl = rs.getString("uidl");
            String ifRead = rs.getString("read");
            int folderID = rs.getInt("folderID");
            doc.add(new StringField("mailID", String.valueOf(mailID), Field.Store.YES));
            doc.add(new StringField("userID", String.valueOf(userID), Field.Store.YES));
            doc.add(new TextField("from", sender, Field.Store.YES));
            doc.add(new TextField("to", recipient, Field.Store.YES));
            doc.add(new TextField("cc", cc, Field.Store.YES));
            doc.add(new TextField("bcc", bcc, Field.Store.YES));
            doc.add(new TextField("subject", subject, Field.Store.YES));
            doc.add(new TextField("content", content, Field.Store.YES));
            doc.add(new StringField("date", date, Field.Store.YES));
            doc.add(new StringField("mailType", mailType, Field.Store.YES));
            doc.add(new StringField("uidl", uidl, Field.Store.YES));
            doc.add(new StringField("ifRead", ifRead, Field.Store.YES));
            doc.add(new StringField("folderID", String.valueOf(folderID), Field.Store.YES));
            writer.addDocument(doc);
        }
        find.close();
        if (writer != null) {
            writer.close();
        }
    }

    // upadat when new data come in
    public void updateData(ArrayList<Email> mailList) throws IOException {
        IndexWriter writer = new IndexWriter(directory, new IndexWriterConfig(new StandardAnalyzer()));
        for (Email mail: mailList) {
            Document doc = new Document();
            int mailID = mail.getMailID();
            int userID = mail.getUserID();
            String sender = mail.getSender();
            String recipient = mail.getReceiver();
            String cc = mail.getCC();
            String bcc = mail.getBcc();
            String subject = mail.getSubject();
            String content = mail.getContent();
            String date = mail.getDate();
            String mailType = mail.getType();
            String uidl = mail.getUIDL();
            String ifRead = mail.getIfRead();
            int folderID = mail.getFolderID();
            doc.add(new StringField("mailID", String.valueOf(mailID), Field.Store.YES));
            doc.add(new StringField("userID", String.valueOf(userID), Field.Store.YES));
            doc.add(new TextField("from", sender, Field.Store.YES));
            doc.add(new TextField("to", recipient, Field.Store.YES));
            doc.add(new TextField("cc", cc, Field.Store.YES));
            doc.add(new TextField("bcc", bcc, Field.Store.YES));
            doc.add(new TextField("subject", subject, Field.Store.YES));
            doc.add(new TextField("content", content, Field.Store.YES));
            doc.add(new StringField("date", date, Field.Store.YES));
            doc.add(new StringField("mailType", String.valueOf(mailType), Field.Store.YES));
            doc.add(new StringField("uidl", uidl, Field.Store.YES));
            doc.add(new StringField("ifRead", ifRead, Field.Store.YES));
            doc.add(new StringField("folderID", String.valueOf(folderID), Field.Store.YES));
            writer.addDocument(doc);
        }
        if (writer != null) {
            writer.close();
        }
    }

    public void deleteData(int mailID) throws SQLException, ClassNotFoundException,
            IOException, ParseException {
        IndexWriter writer = new IndexWriter(directory, new IndexWriterConfig(new StandardAnalyzer()));
        QueryParser searchQuery = new QueryParser("mailID", new StandardAnalyzer());
        Query query = searchQuery.parse(String.valueOf(mailID));
        writer.deleteDocuments(query);
        writer.commit();
        if (writer != null) {
            writer.close();
        }
    }

    public void clear(ArrayList<Integer> clearedMailList) throws ParseException, IOException {
        IndexWriter writer = new IndexWriter(directory, new IndexWriterConfig(new StandardAnalyzer()));
        for (Integer mailID: clearedMailList) {
            QueryParser searchQuery = new QueryParser("mailID", new StandardAnalyzer());
            Query query = searchQuery.parse(String.valueOf(mailID));
            writer.deleteDocuments(query);
            writer.commit();
        }
        if (writer != null) {
            writer.close();
        }
    }

    public ArrayList<Email> search(String[] mySearch, int userID) throws ParseException, IOException {
        ArrayList<Email> results = new ArrayList<>();

        DirectoryReader iReader = DirectoryReader.open(directory);
        IndexSearcher isearcher = new IndexSearcher(iReader);

        // String[] query, String[] field, analyzor
        Query searchQuery = MultiFieldQueryParser.parse(new String[]{mySearch[1], String.valueOf(userID)},
                new String[]{mySearch[0], "userID"}, new BooleanClause.Occur[]{BooleanClause.Occur.MUST,
                        BooleanClause.Occur.MUST},
                new StandardAnalyzer());
        TopDocs hits = isearcher.search(searchQuery, 10000);
        System.out.println("Find totoal num: " + hits.totalHits);
        for (int i = 0; i < hits.totalHits; i++) {
            int doc = hits.scoreDocs[i].doc;
            Document mydoc = isearcher.doc(doc);
            int mailID = Integer.valueOf(mydoc.get("mailID"));
            String sender = mydoc.get("from");
            String recipient = mydoc.get("to");
            String cc = mydoc.get("cc");
            String bcc = mydoc.get("bcc");
            String subject = mydoc.get("subject");
            String content = mydoc.get("content");
            String date = mydoc.get("date");
            String mailType = mydoc.get("mailType");
            String uidl = mydoc.get("uidl");
            String ifRead = mydoc.get("read");
            int folderID = Integer.valueOf(mydoc.get("folderID"));
            Email mail = new Email(mailID, userID, sender, recipient, cc, bcc, subject, content, date,
                    mailType, uidl, ifRead, folderID, null);
            results.add(mail);
        }
        if(iReader != null) {
            iReader.close();
        }
        return results;
    }

    public static void main(String [] args) {
        int userID = 1;
//        String[] search = new String[]{"to", "yxu66mail"};
        String[] search = new String[]{"from", "treexy1230"};
        SearchService searchService = new SearchService();

        try {
            getDir();
            searchService.createSearchData();
            ArrayList<Email> results = searchService.search(search, userID);
            System.out.println("!!!!!");
            System.out.println(results.size());
            for(Email e: results) {
                System.out.println(e);
                System.out.println("=================");
            }
        } catch (IOException e) {
            ErrorManager.instance().error(e);
        } catch (SQLException e) {
            ErrorManager.instance().error(e);
        } catch (ClassNotFoundException e) {
            ErrorManager.instance().error(e);
        } catch (ParseException e) {
            ErrorManager.instance().error(e);
        }
    }
}
