package core;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.text.Collator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import jxl.Workbook;
import jxl.format.UnderlineStyle;
import jxl.write.Label;
import jxl.write.VerticalAlignment;
import jxl.write.WritableCellFormat;
import jxl.write.WritableFont;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;

import org.directwebremoting.io.FileTransfer;

public class Parser {
	private static final int BUFFER_SIZE = 1024;

	public void parse(String content) {
		Pattern pattern = Pattern
				.compile("<a href=\"https://book.douban.com/subject/(.*?)/\" title=\"(.*?)\"[\\s\\S]*?<div class=\"pub\">([\\s\\S]*?)</div>[\\s\\S]*?<span class=\"rating_nums\">(.*?)</span>[\\s\\S]*?\\((.*?)������");
		Matcher m = pattern.matcher(content); //ƥ����Ҫ������
		List<Book> bs = new ArrayList<Book>();
		while (m.find()) { //�ҵ�����
			if (Integer.parseInt(m.group(1)) > 1000) { //Ҫ��������������1000
				String[] t_dat = m.group(3).trim().split("/");
				if (t_dat.length > 4) {
					Book b = new Book();
					b.setBook_name(m.group(2));
					b.setPeople_number(m.group(1));
					b.setScore(m.group(4));
					b.setAuthor(t_dat[1]);
					b.setPress(t_dat[2]);
					b.setDate(t_dat[3]);
					b.setPrice(t_dat[4]);
					bs.add(b);
				}
			}
		}
		Collections.sort(bs, new Comparator<Book>() { //�Է������������ɸߵ���
			public int compare(Book o1, Book o2) {
				Collator collator = Collator.getInstance(Locale.CHINA);
				if (collator.compare(o1.getScore(), o2.getScore()) > 0) {
					return -1;
				} else if (collator.compare(o1.getScore(), o2.getScore()) == 0) {
					return 0;
				} else {
					return 1;
				}
			}
		});
		List<Book> newList=bs.subList(0, 40); //����ǰ40������ߵ�
		try {
			this.createData(newList);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	/* �����ӱ�����EXCEL */
	public FileTransfer createData(List<Book> bs) throws Exception {
		List<String[]> datas = new ArrayList<String[]>(); // �����������ݵļ��ϣ��������������ݣ�
		// ��������
		String[] fieldNames = { "���", "����", "����", "��������", "����", "������", "��������",
				"�۸�" };
		datas.add(fieldNames);
		// ��ȡ����
		int i=1;
		for (Book b : bs) {
			String[] data = new String[fieldNames.length];
			data[0] = String.valueOf(i);
			data[1] = b.getBook_name();
			data[2] = b.getScore();
			data[3] = b.getPeople_number();
			data[4] = b.getAuthor();
			data[5] = b.getPress();
			data[6] = b.getDate();
			data[7] = b.getPrice();
			datas.add(data);
			i++;
		}
		return this.createExcel(datas);
	}

	/* �Զ�����浼�����ܣ�����Excel�ķ��� */
	@SuppressWarnings("deprecation")
	public FileTransfer createExcel(List<String[]> datas) throws Exception {
		try {
			long start = System.currentTimeMillis();
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			String fileName = null;
			fileName = String.valueOf(new Date().getTime());
			// �����ͷ fieldNameMap fieldNames
			String[] fieldNames = datas.get(0);
			// ���嵼����ʱ�ļ�
			String filePath = "D://" + File.separator + fileName + ".xls";
			File excelFile = new File(filePath);
			WritableWorkbook wwb = Workbook.createWorkbook(excelFile);
			try {
				excelFile.createNewFile();
				// ����EXCEL����
				WritableSheet ws = wwb.createSheet(fileName, 0);
				ws.getSettings().setDefaultColumnWidth(16);// ��Ԫ����
				WritableFont headerWF = new WritableFont(WritableFont.ARIAL,
						10, WritableFont.NO_BOLD, false,
						UnderlineStyle.NO_UNDERLINE, jxl.format.Colour.RED);

				WritableCellFormat headerWCFF = new WritableCellFormat(headerWF);
				headerWCFF.setWrap(true);// �Զ�����

				WritableCellFormat wcff = new WritableCellFormat();
				wcff.setWrap(true);// �Զ�����
				wcff.setVerticalAlignment(VerticalAlignment.TOP);

				// �����ͷ
				for (int i = 0; i < fieldNames.length; i++) {
					String fieldCnName = fieldNames[i];
					Label label = new Label(i, 0, fieldCnName, headerWCFF);
					ws.addCell(label);
				}
				// ��д����
				int row = 1;
				for (int i = 1; i < datas.size(); i++) {
					String[] data = (String[]) datas.get(i);
					int col = 0;
					for (int j = 0; j < data.length; j++) {
						Label label = new Label(col, row, data[j]);
						ws.addCell(label);
						col++;
					}
					row++;
				}
				wwb.write();
			} catch (Exception ex) {
				ex.printStackTrace();
				throw new Exception("����EXCEL����");
			} finally {
				wwb.close();
			}
			// ����
			if (excelFile.exists()) {
				FileInputStream fis = new FileInputStream(excelFile);
				byte buf[] = new byte[BUFFER_SIZE];
				int backSize = 0;
				if (fis.available() > 0) {
					while ((backSize = fis.read(buf)) > -1) {
						bos.write(buf, 0, backSize);
					}
				}
				bos.flush();
				fis.close();
			} else {
				throw new Exception("����EXCEL�����ļ������ڣ�");
			}

			long end = System.currentTimeMillis();
			// ����������޷���������
			fileName = fileName + ".xls";
			fileName = new String(fileName.getBytes("GBK"), "ISO8859-1");
			return new FileTransfer(fileName, "xls", bos.toByteArray());
		} catch (Throwable e) {
			e.printStackTrace();
			throw new Exception(e);
		}
	}
}
