package work.hang;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.junit.Test;

import java.nio.file.Paths;

/**
 * [概 要]
 * [环 境] JAVA 1.8
 *
 * @author 六哥
 * @date 2018/7/20
 */
public class IndexingTest2 {
	private String ids[] = {"1", "2", "3", "4"};
	private String authors[] = {"Jack", "Marry", "John", "Json"};
	private String positions[] = {"accounting", "technician", "salesperson", "boss"};
	private String titles[] = {"Java is a good language.", "Java is a cross platform language", "Java powerful", "You should learn java"};
	private String contents[] = {
			"If possible, use the same JRE major version at both index and search time.",
			"When upgrading to a different JRE major version, consider re-indexing. ",
			"Different JRE major versions may implement different versions of Unicode,",
			"For example: with Java 1.4, `LetterTokenizer` will split around the character U+02C6,"
	};

	private Directory dir;

	/**
	 * 获取IndexWriter实例
	 *
	 * @return writer
	 * @throws Exception Exception
	 */
	private IndexWriter getWriter() throws Exception {
		Analyzer analyzer = new StandardAnalyzer(); // 标准分词器
		IndexWriterConfig iwc = new IndexWriterConfig(analyzer);
		return new IndexWriter(dir, iwc);
	}

	/**
	 * 生成索引
	 *
	 * @throws Exception Exception
	 */
	@Test
	public void index() throws Exception {
		dir = FSDirectory.open(Paths.get("/Users/maxiaohu/Desktop/lucene/lucene03"));
		IndexWriter writer = getWriter();
		for (int i = 0; i < ids.length; i++) {
			Document doc = new Document();
			doc.add(new StringField("id", ids[i], Field.Store.YES));
			doc.add(new StringField("author", authors[i], Field.Store.YES));
			doc.add(new StringField("position", positions[i], Field.Store.YES));
			doc.add(new TextField("title", titles[i], Field.Store.YES));
			doc.add(new TextField("content", contents[i], Field.Store.NO));
			writer.addDocument(doc); // 添加文档
		}
		writer.close();
	}

	/**
	 * 查询
	 *
	 * @throws Exception Exception
	 */
	@Test
	public void search() throws Exception {
		dir = FSDirectory.open(Paths.get("/Users/maxiaohu/Desktop/lucene/lucene03"));
		IndexReader indexReader = DirectoryReader.open(dir);
		IndexSearcher indexSearcher = new IndexSearcher(indexReader);
		String searchField = "title";
		String str = "java";
		Term term = new Term(searchField, str);
		Query query = new TermQuery(term);
		TopDocs hits = indexSearcher.search(query, 10);
		System.out.println("匹配 '" + str + "'，总共查询到" + hits.totalHits + "个文档");
		for (ScoreDoc scoreDoc : hits.scoreDocs) {
			Document doc = indexSearcher.doc(scoreDoc.doc);
			System.out.println(doc.get("title"));
		}
		indexReader.close();
	}
}
