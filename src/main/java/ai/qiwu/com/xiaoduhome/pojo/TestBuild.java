package ai.qiwu.com.xiaoduhome.pojo;

import com.csvreader.CsvReader;
import com.csvreader.CsvWriter;
import lombok.*;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.csv.CSVRecord;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.DigestUtils;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author 苗权威
 * @dateTime 19-9-16 上午11:35
 */
@Builder
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class TestBuild {
    private Integer id;
    private String name;

    public static String unicodeToString(String unicode) {
        StringBuffer string = new StringBuffer();
        String[] hex = unicode.split("\\\\u");
        for (int i = 1; i < hex.length; i++) {
            int data = Integer.parseInt(hex[i], 16);// 转换出每一个代码点
            string.append((char) data);// 追加成string
        }
        return string.toString();
    }

    public static String decodeUnicode2(String dataStr) {
        int start = 0;
        int end = 0;
        final StringBuffer buffer = new StringBuffer();
        while (start > -1) {
            end = dataStr.indexOf("\\u", start + 2);
            String charStr = null;
            if (end == -1) {
                charStr = dataStr.substring(start + 2, dataStr.length());
            } else {
                charStr = dataStr.substring(start + 2, end);
            }
            char letter = (char) Integer.parseInt(charStr, 16);
            buffer.append(new Character(letter).toString());
            start = end;
        }
        return buffer.toString();
    }

    public static String unicodetoString(String unicode) {
        if (unicode == null || "".equals(unicode)) {
            return null;
        }
        StringBuilder sb = new StringBuilder();
        int i = -1;
        int pos = 0;
        while ((i = unicode.indexOf("\\u", pos)) != -1) {
            sb.append(unicode, pos, i);
            if (i + 5 < unicode.length()) {
                pos = i + 6;
                sb.append((char) Integer.parseInt(unicode.substring(i + 2, i + 6), 16));
            }
        }
        sb.append(unicode.substring(pos));
        return sb.toString();
    }

    public static String decodeUnicode(String str) {
        Charset set = Charset.forName("UTF-16");
        Pattern p = Pattern.compile("\\\\u([0-9a-fA-F]{4})");
        Matcher m = p.matcher( str );
        int start = 0 ;
        int start2 = 0 ;
        StringBuffer sb = new StringBuffer();
        while( m.find( start ) ) {
            start2 = m.start() ;
            if( start2 > start ){
                String seg = str.substring(start, start2) ;
                sb.append( seg );
            }
            String code = m.group( 1 );
            int i = Integer.valueOf( code , 16 );
            byte[] bb = new byte[ 4 ] ;
            bb[ 0 ] = (byte) ((i >> 8) & 0xFF );
            bb[ 1 ] = (byte) ( i & 0xFF ) ;
            ByteBuffer b = ByteBuffer.wrap(bb);
            sb.append( String.valueOf( set.decode(b) ).trim() );
            start = m.end() ;
        }
        start2 = str.length() ;
        if( start2 > start ){
            String seg = str.substring(start, start2) ;
            sb.append( seg );
        }
        return sb.toString() ;
    }

    static void csvUtil(String targetFile, String src, String srcModify, String v2) throws IOException {
        CsvWriter csvWriter = new CsvWriter(targetFile, ',', Charset.forName("UTF-8"));
        CsvReader srcReader = new CsvReader(src, ',',Charset.forName("UTF-8"));
        CsvReader srcModifyReader = new CsvReader(srcModify, ',',Charset.forName("UTF-8"));
        CsvReader v2Reader = new CsvReader(v2, ',',Charset.forName("UTF-8"));

        srcReader.readHeaders();
        srcModifyReader.readHeaders();
        v2Reader.readHeaders();

        String[] headers = {"内部代号", "句子", "是否已上传音频文件", "音频文件名"};
        csvWriter.writeRecord(headers);
        HashMap<String, String> srcModifyId2File = new HashMap<>();
        while (srcModifyReader.readRecord()) {
            String[] arr = srcModifyReader.getRawRecord().split(",");
            if (arr.length != 4) continue;
            String iden = new StringBuilder(arr[0]).append(arr[1]).toString();
            srcModifyId2File.put(iden, arr[3]);
        }
        //System.out.println(srcModifyId2File);
        while (srcReader.readRecord() && v2Reader.readRecord()) {
            String iden = new StringBuilder(srcReader.get(0)).append(srcReader.get(1)).toString();
            String filename = srcModifyId2File.get(iden);
            String[] arr = new String[4];
            arr[0] = v2Reader.get(0);
            arr[1] = v2Reader.get(1);
            arr[2] = v2Reader.get(2);
            arr[3] = filename;
            csvWriter.writeRecord(arr);
        }
        csvWriter.close();
    }

    static void csvUtilByCommon(String targetFile, String src, String srcModify, String v2) throws IOException {
        CSVFormat format = CSVFormat.DEFAULT.withHeader("内部代号", "句子", "是否已上传音频文件", "音频文件名");
        try (CSVParser srcParser = CSVFormat.EXCEL.parse(new BufferedReader(new InputStreamReader(new FileInputStream(new File(src)), Charset.forName("UTF-8"))));
             CSVParser srcModifyParser = CSVFormat.EXCEL.parse(new BufferedReader(new InputStreamReader(new FileInputStream(new File(srcModify)), Charset.forName("UTF-8"))));
             CSVParser v2Parser = CSVFormat.EXCEL.parse(new BufferedReader(new InputStreamReader(new FileInputStream(new File(v2)), Charset.forName("UTF-8"))));
             CSVPrinter csvPrinter = new CSVPrinter(new OutputStreamWriter(new FileOutputStream(targetFile), Charset.forName("UTF-8")),format)){
            HashMap<String, String> srcModifyId2File = new HashMap<>();
            int i = 0;
            for (CSVRecord record: srcModifyParser) {
                if (i++ == 0) {
                    System.out.println(record.get(0));
                    continue;
                }
                if (StringUtils.isBlank(record.get(3))) continue;
                String iden = new StringBuilder(record.get(0)).append(record.get(1)).toString();
                srcModifyId2File.put(iden, record.get(3));
            }
            int j = 0;
            Iterator<CSVRecord> iterator = srcParser.iterator();
            for (CSVRecord record: v2Parser) {
                CSVRecord srcRecord;
                if (iterator.hasNext()) {
                    srcRecord = iterator.next();
                }else {
                    break;
                }
                if (j++ == 0) {
                    System.out.println("v2:"+record.get(0));
                    System.out.println("src:"+srcRecord.get(0));
                    continue;
                }
                String iden = new StringBuilder(srcRecord.get(0)).append(srcRecord.get(1)).toString();
                String filename = srcModifyId2File.get(iden);
                csvPrinter.printRecord(record.get(0), record.get(1), record.get(2), filename);
            }
        }
    }

    static void check(String src, String srcModify, String check) throws IOException {
        CsvReader srcReader = new CsvReader(src, ',',Charset.forName("UTF-8"));
        CsvReader srcModifyReader = new CsvReader(srcModify, ',',Charset.forName("UTF-8"));
        CsvReader checkReader = new CsvReader(check, ',',Charset.forName("UTF-8"));
        srcReader.readHeaders();
        srcModifyReader.readHeaders();
        checkReader.readHeaders();

        HashMap<String, String[]> srcModifyId2File = new HashMap<>();
        while (srcModifyReader.readRecord()) {
            String[] arr = srcModifyReader.getRawRecord().split(",");
            if (arr.length != 4) continue;
            String iden = new StringBuilder(arr[0]).append(arr[1]).toString();
            srcModifyId2File.put(iden, arr);
        }

        boolean srcOver = false;
        boolean checkOver = false;
        while ((srcOver = srcReader.readRecord())) {
            String id = srcReader.get(0);
            String iden = new StringBuilder(id).append(srcReader.get(1)).toString();
            String[] info = srcModifyId2File.get(iden);
            checkOver = checkReader.readRecord();
            if (info == null ) {
                checkReader.getRawRecord();
                continue;
            }
            String rawRecord = checkReader.getRawRecord();
            if (rawRecord == null) {
                System.out.println("check is null but info is not:"+Arrays.toString(info));
                continue;
            }
            try {
                String[] checkArr = rawRecord.split(",");
                if (info.length != checkArr.length) System.out.println(id+":长度不同,src:"+Arrays.toString(info)+";check:"+Arrays.toString(checkArr));
                for (int i = 1; i < info.length; i++) {
                    if (i == 2) continue;
                    if (!info[i].equals(checkArr[i])) {
                        System.out.println(id+":字段不通,第"+(i+1));
                    }
                }
            }catch (Exception e) {
                System.out.println("2333333:====="+id);
            }
        }
        if (srcOver != srcReader.readRecord()) System.out.println("大小不一致");
    }

    public static String buildMP3Filename(String filename) {
        String file = DigestUtils.md5DigestAsHex(filename.getBytes(StandardCharsets.UTF_8));
        file = file+".mp3";
        return file;
    }

    public static boolean doesFileExist(String filePath) {
        File file = new File(filePath);
        if (file.exists()) {
            //log.info("文件:{},大小为:{}", filePath, file.length());
            if (file.length() == 0) {
                if (file.delete()) {
                    System.out.println("大小为0的文件, 删除成功:"+filePath);
                } else {
                    System.out.println("大小为0的文件, 删除成功:"+filePath);
                }
                return false;
            }
            return true;
        }
        return false;
    }

    private static void test4() throws IOException {
        String target = "/home/yf01/下载/猎狗-人工配音关系对于表.csv";
        CsvReader reader = new CsvReader(new BufferedReader(new InputStreamReader(new FileInputStream(target),Charset.forName("UTF-8"))), ',');
        HashMap<String, String> idText = new HashMap<>();
        int i = 0;
        while (reader.readRecord()) {
            System.out.println(reader.get(1));
            System.out.println(reader.get(1).length());
        }
        reader.close();

        System.out.println("///////////////////////////");
        BufferedReader readers = new BufferedReader(new InputStreamReader(new FileInputStream(target), Charset.forName("UTF-8")));
        readers.readLine();
        String row;
        int j = 0;
        while ((row = readers.readLine()) != null) {
            j++;
            String[] arr = row.split(",");
            System.out.println(Arrays.toString(arr));
        }
        System.out.println("jjjj:"+j);
        readers.close();

        System.out.println("========================");
        CSVFormat format = CSVFormat.DEFAULT.withHeader("内部代号", "句子", "是否已上传音频文件", "音频文件名");
        CSVPrinter csvPrinter = new CSVPrinter(new OutputStreamWriter(new FileOutputStream("/home/yf01/下载/tts/mqw.csv"), Charset.forName("UTF-8")),format);

        BufferedReader readerCo = new BufferedReader(new InputStreamReader(new FileInputStream(target), Charset.forName("UTF-8")));
        CSVParser parser = CSVFormat.EXCEL.withHeader("内部代号", "句子", "是否已上传音频文件", "音频文件名").parse(readerCo);
        List<CSVRecord> list = parser.getRecords();
        System.out.println(list.get(0).get(0));
//        int q = 0;
//        for (CSVRecord record: list) {
//            if (q++ == 0) {
//                System.out.println(record.get(0));
//                continue;
//            }
//            csvPrinter.printRecord(record.get(0),record.get(1),record.get(2),record.get(3));
//        }
        readerCo.close();
        //csvPrinter.flush();
        //csvPrinter.close();

        BufferedReader readerCoNoHead = new BufferedReader(new InputStreamReader(new FileInputStream(target), Charset.forName("UTF-8")));
        CSVParser parser2 = CSVFormat.EXCEL.parse(readerCoNoHead);
        System.out.println(parser2.iterator().next().get(0));
        readerCoNoHead.close();
    }

    static boolean test5(String target1, String target2) {
        try (CSVParser t1Parser = CSVFormat.EXCEL.parse(new BufferedReader(new InputStreamReader(new FileInputStream(new File(target1)), Charset.forName("UTF-8"))));
             CSVParser t2Parser = CSVFormat.EXCEL.parse(new BufferedReader(new InputStreamReader(new FileInputStream(new File(target2)), Charset.forName("UTF-8"))))){
                 Iterator<CSVRecord> t2RecordIterator = t2Parser.iterator();
                 for (CSVRecord t1Record: t1Parser){
                     CSVRecord t2Record;
                     if (t2RecordIterator.hasNext()) t2Record = t2RecordIterator.next();
                     else return false;
                     if (!t1Record.toString().equals(t2Record.toString())) {
                         System.out.println(t2Record.get(0));
                     }
                 }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return true;
    }

    static boolean test6(String target, String v2) {
        try (CSVParser t1Parser = CSVFormat.EXCEL.parse(new BufferedReader(new InputStreamReader(new FileInputStream(new File(target)), Charset.forName("UTF-8"))));
             CSVParser t2Parser = CSVFormat.EXCEL.parse(new BufferedReader(new InputStreamReader(new FileInputStream(new File(v2)), Charset.forName("UTF-8"))))){
            Iterator<CSVRecord> t2RecordIterator = t2Parser.iterator();
            for (CSVRecord t1Record: t1Parser){
                CSVRecord t2Record;
                if (t2RecordIterator.hasNext()) t2Record = t2RecordIterator.next();
                else return false;
                if (!t1Record.get(1).equals(t2Record.get(1))) {
                    System.out.println(t2Record.get(0));
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return true;
    }

    static void test7() {
        String regex = "[/].+?[/]}";
        String s = "/home/yf01/下载/xiaorong2.csv";
        Pattern pattern = Pattern.compile(regex);
        CSVFormat format = CSVFormat.DEFAULT.withHeader("内部代号", "句子", "是否已上传音频文件", "音频文件名");
        try (CSVParser t1Parser = CSVFormat.EXCEL.parse(new BufferedReader(new InputStreamReader(new FileInputStream(new File(s)), Charset.forName("UTF-8"))));
             CSVPrinter csvPrinter = new CSVPrinter(new OutputStreamWriter(new FileOutputStream("/home/yf01/下载/tts/xiaorongs.csv"), Charset.forName("UTF-8")),format)){
            int i = 0;
            for (CSVRecord record: t1Parser){
                if (i++ == 0) continue;
                String text = record.get(1);
                Matcher matcher = pattern.matcher(text);
                List<String> vari = new ArrayList<>();
                while (matcher.find()) {
                    String variableText = matcher.group(1);
                    if (StringUtils.isNotBlank(variableText)) vari.add(variableText);
                }
                if (vari.size() != 0) {
                    String[] arr = pattern.split(regex);

                }
                csvPrinter.printRecord(record.get(0),record.get(1),record.get(2),record.get(3));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) throws IOException {
        String target = "/home/yf01/下载/tts/齐齐/target.csv";
        String target1 = "/home/yf01/下载/tts/target.csv";
        String target2 = "/home/yf01/下载/tts/target2.csv";
        String src = "/home/yf01/下载/齐齐-人工配音关系对于表(2).csv";
        String srcModify = "/home/yf01/下载/qiOld.csv";
        String v2 = "/home/yf01/下载/qiNew.csv";
        //csvUtil(target1, src, srcModify, v2);

        //check(src, srcModify, target);
        //test4();
        String tomTarget = "/home/yf01/下载/tts/tom.csv";
        String tomSrc = "/home/yf01/下载/汤姆OldFresh.csv";
        String tomSrcModify = "/home/yf01/下载/tomFilled.csv";
        String tomV2 = "/home/yf01/下载/汤姆NewFresh.csv";
        csvUtilByCommon(tomTarget, tomSrc, tomSrcModify, tomV2);
        //System.out.println(test5(target1, target2));
        //System.out.println(test6(target2, v2));


    }
}
