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
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.junit.Before;
import org.junit.Test;

import java.nio.file.Paths;

/**
 * [概 要]
 * [环 境] JAVA 1.8
 *
 * @author 六哥
 * @date 2018/7/18
 */
public class IndexingTest {
	private String ids[] = {"1", "2", "3"};
	private String citys[] = {"qingdao", "nanjing", "shanghai"};
	private String descs[] = {
			"Qingdao is a beautiful city.",
			"Nanjing is a city of culture.",
			"Shanghai is a bustling city."
	};

	private Directory dir;

	@Before
	public void setUp() throws Exception {
		dir = FSDirectory.open(Paths.get("/Users/maxiaohu/Desktop/lucene/lucene02"));
		IndexWriter writer = getWriter();
		for (int i = 0; i < ids.length; i++) {
			Document doc = new Document();
			doc.add(new StringField("id", ids[i], Field.Store.YES));
			doc.add(new StringField("city", citys[i], Field.Store.YES));
			doc.add(new TextField("desc", descs[i], Field.Store.NO));
			writer.addDocument(doc); // 添加文档
		}
		writer.close();
	}

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
	 * 测试写了几个文档
	 *
	 * @throws Exception Exception
	 */
	@Test
	public void testIndexWriter() throws Exception {
		IndexWriter writer = getWriter();
		System.out.println("写入了" + writer.numDocs() + "个文档");
		writer.close();
	}

	/**
	 * 测试读取文档
	 *
	 * @throws Exception Exception
	 */
	@Test
	public void testIndexReader() throws Exception {
		IndexReader reader = DirectoryReader.open(dir);
		System.out.println("最大文档数：" + reader.maxDoc());
		System.out.println("实际文档数：" + reader.numDocs());
		reader.close();
	}

	/**
	 * 测试删除 在合并前
	 *
	 * @throws Exception Exception
	 */
	@Test
	public void testDeleteBeforeMerge() throws Exception {
		IndexWriter writer = getWriter();
		System.out.println("删除前：" + writer.numDocs());
		writer.deleteDocuments(new Term("id", "1"));
		writer.commit();
		System.out.println("writer.maxDoc()：" + writer.maxDoc());
		System.out.println("writer.numDocs()：" + writer.numDocs());
		writer.close();
	}

	/**
	 * 测试删除 在合并后
	 *
	 * @throws Exception Exception
	 */
	@Test
	public void testDeleteAfterMerge() throws Exception {
		IndexWriter writer = getWriter();
		System.out.println("删除前：" + writer.numDocs());
		writer.deleteDocuments(new Term("id", "1"));
		writer.forceMergeDeletes(); // 强制删除
		writer.commit();
		System.out.println("writer.maxDoc()：" + writer.maxDoc());
		System.out.println("writer.numDocs()：" + writer.numDocs());
		writer.close();
	}

	/**
	 * 测试更新
	 *
	 * @throws Exception Exception
	 */
	@Test
	public void testUpdate() throws Exception {
		IndexWriter writer = getWriter();
		Document doc = new Document();
		doc.add(new StringField("id", "1", Field.Store.YES));
		doc.add(new StringField("city", "qingdao", Field.Store.YES));
		doc.add(new TextField("desc", "dsss is a city.", Field.Store.NO));
		writer.updateDocument(new Term("id", "1"), doc);
		writer.close();
	}
}
