package xiaoduhome.common.temple;

import ai.qiwu.com.xiaoduhome.common.Constants;
import ai.qiwu.com.xiaoduhome.common.temple.funny.FunnyPojo;
import ai.qiwu.com.xiaoduhome.common.temple.onLunch.LunchBotData;
import ai.qiwu.com.xiaoduhome.common.temple.playUpdate.PlayUpdateDocument;
import ai.qiwu.com.xiaoduhome.common.temple.playUpdate.PlayUpdatePojo;
import ai.qiwu.com.xiaoduhome.pojo.data.ProjectData;
import ai.qiwu.com.xiaoduhome.pojo.dpl.Item;
import ai.qiwu.com.xiaoduhome.pojo.dpl.MyDocument;
import ai.qiwu.com.xiaoduhome.service.ScheduleServiceNew;
import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import static ai.qiwu.com.xiaoduhome.common.Constants.IMG_CUSTOM_PREFIX;

/**
 * @author 苗权威
 * @dateTime 19-8-24 下午2:03
 */
@Slf4j
//@Component
public class TempleUtils {

    public static PlayUpdateDocument getPlayUpdate(String audio, String text) {
        PlayUpdateDocument document = JSON.parseObject(getDPLTemple("static/json/playUpdate.json"), PlayUpdateDocument.class);
        List<PlayUpdatePojo> whole = document.getMainTemplate().getItems().get(0).getItems();
        whole.get(0).getItems().get(0).setText(text);
        whole.get(1).setSrc(audio);
        return document;
    }

    private static MyDocument buildCollectTemple() {
        return JSON.parseObject(getDPLTemple("static/json/collect.json"), MyDocument.class);
    }

    public static MyDocument getCollectIcon(String componentId, String imgSrc, String txt) {
        MyDocument collect = buildCollectTemple();
        Item container = collect.getMainTemplate().getItems().get(0);
        container.setComponentId(componentId);
        container.getOnClick().get(0).setComponentId(componentId);
        Item image = container.getItems().get(0);
        image.setSrc(imgSrc);
        Item text = container.getItems().get(1);
        text.setText(txt);
        return collect;
    }

    public static List<LunchBotData> buildDataList(List<ProjectData> list, int begin) {
        ArrayList<LunchBotData> dataList = new ArrayList<>();
        for (ProjectData projectInfo : list) {
            String productName = correctName(projectInfo.getName());
            String imageId = new StringBuilder(Constants.PRE_PRODUCT_IMG).append(projectInfo.getName()).toString();
            LunchBotData data = new LunchBotData(projectInfo.getBannerImgUrl(), imageId,productName);
            data.setNum(begin++);
            dataList.add(data);
        }
        return dataList;
    }

    public static List<LunchBotData> buildCategoryDataList(int row) {
        ArrayList<LunchBotData> dataList = new ArrayList<>();
        int begin = row*4;
        String name, imgId, imgSrc;
        for (int i = begin; i < begin+4; i++) {
            name = ScheduleServiceNew.CATEGORY.get(i);
            imgSrc =  new StringBuilder(IMG_CUSTOM_PREFIX)
                    .append(ScheduleServiceNew.CATEGORY_CHARAC[i]).append(".png").toString();
            imgId = Constants.DPL_COMPONENT_ID.PRE_CATEGORY +ScheduleServiceNew.CATEGORY.get(i);
            LunchBotData data = new LunchBotData(imgSrc, imgId, name);
            data.setNum(i+1);
            dataList.add(data);
        }
        return dataList;
    }

    public static String correctName(String name) {
        int brace = name.indexOf("{");
        if (brace != -1) name = name.substring(0, brace);
        return name;
    }

    private static String getDPLTemple(String path) {
        String temple = null;
        try {
            temple = getJsonFromName(path);
        } catch (IOException e) {
            log.error("没有获取到json字符串:{}", e.toString());
        }
        return temple;
    }

    private static String getJsonFromName(String filename) throws IOException {
        InputStream inputStream = Thread.currentThread().getContextClassLoader().getResourceAsStream(filename);
        InputStreamReader reader = new InputStreamReader(inputStream);
        char[] buffer = new char[512];
        int ln;
        StringBuilder builder = new StringBuilder();
        while ((ln = reader.read(buffer)) != -1) {
            builder.append(buffer, 0, ln);
        }
        return builder.toString();
    }

    private static FunnyPojo getFunnyPageItemPojo() {
        FunnyPojo pojo = new FunnyPojo();
        pojo.setType("Container");
        pojo.setFlexDirection("row");
        pojo.setWidth("100%");
        pojo.setHeight("100%");
        pojo.setMarginTop("40dp");

        FunnyPojo image = new FunnyPojo();
        image.setSrc("${data.imgSrc}");
        image.setType("Image");
        image.setMarginTop("30dp");
        image.setHeight("400dp");
        image.setWidth("400dp");
        image.setMarginLeft("25dp");
        image.setScaleType("fitXY");
        image.setBorderRadius("18dp");

        FunnyPojo textContainer = new FunnyPojo();
        textContainer.setType("Container");
        textContainer.setWidth("500dp");
        textContainer.setHeight("100%");
        textContainer.setMarginLeft("20dp");

        FunnyPojo text = new FunnyPojo();
        text.setType("Text");
        text.setText("${data.text}");
        text.setFontSize("25dp");
        text.setLineHeight("60dp");
        text.setMarginTop("20dp");
        text.setMarginLeft("10dp");

        List<FunnyPojo> textContainerItems = new ArrayList<>();
        textContainerItems.add(text);
        textContainer.setItems(textContainerItems);

        List<FunnyPojo> pojoItems = new ArrayList<>();
        pojoItems.add(image);
        pojoItems.add(textContainer);

        pojo.setItems(pojoItems);
        return pojo;
    }
}
