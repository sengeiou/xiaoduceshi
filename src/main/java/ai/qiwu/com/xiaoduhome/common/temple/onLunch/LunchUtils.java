package ai.qiwu.com.xiaoduhome.common.temple.onLunch;

import ai.qiwu.com.xiaoduhome.common.temple.TempleUtils;
import ai.qiwu.com.xiaoduhome.pojo.data.ProjectData;
import ai.qiwu.com.xiaoduhome.pojo.dpl.Event;
import ai.qiwu.com.xiaoduhome.service.ScheduleServiceNew;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * @author 苗权威
 * @dateTime 19-9-5 下午4:48
 */
@Slf4j
public class LunchUtils {

    private static final LunchPojo LUNCH_LIST_CON = buildImgFrmTxtContainer();
    private static final LunchPojo LUNCH_LIST_CATEGORY_CON = buildCategoryList();
//    private static final LunchPojo LUNCH_HEAD = buildLunchHead();
    private static final LunchPojo LUNCH_BACK_IMG = buildLunchBackgroundImg();
    private static final LunchPojo FOOTER = buildHomeFooter();
    private static final LunchPojo CATEGORY_FOOTER = buildCategoryFooter();
//    private static final LunchPojo LUNCH_LOGO_IMG = buildLogoImage();
//    private static final LunchPojo LUNCH_HSITORY_IMG = buildHistoryImg();
//    private static final LunchPojo LUNCH_COLLECT_IMG = buildCollectImg();
    private volatile static LunchDocument lunchDocument = buildLunchNewDocument(0);
    private volatile static LunchDocument lunchDocument_part = buildLunchNewDocument(1);
    private volatile static LunchDocument categoryDocument = buildCategoryDocument();


    private static LunchDocument buildLunchNewDocument(int part) {
        LunchDocument document = new LunchDocument();
        document.setDuration(240000);
        LunchMainTemplate mainTemplate = new LunchMainTemplate();
        document.setMainTemplate(mainTemplate);

        List<ProjectData> dataList = ScheduleServiceNew.getTheChannelAllProjectData("jiaoyou-audio-test");
        int size = dataList.size();
        int rows = size/4 + (size%4 == 0?0:1);
        int i, bound;
        boolean all;
        if ((all = part == 0)) {
            i = 0;
            bound = rows;
        } else {
            i = 0;
            bound = 1;
        }
        List<LunchPojo> list = new ArrayList<>();
        LunchPojo rowCon;
        int begin, end;
        while (i < bound) {
            begin = i*4;
            end = begin+4 > size ? size : begin+4;
            rowCon = buildLunchList(TempleUtils.buildDataList(dataList.subList(begin, end),begin+1));
            list.add(rowCon);
            i++;
        }
        LunchPojo scroll;
        if (all) {
            scroll = buildLunchScroll();
            scroll.setItems(list);
            mainTemplate.getItems().addAll(Arrays.asList(LUNCH_BACK_IMG,scroll,FOOTER));
        } else {
            scroll = buildLunchPartContainer();
            scroll.setItems(list);
            mainTemplate.getItems().addAll(Arrays.asList(LUNCH_BACK_IMG,scroll));
        }

//        mainTemplate.getItems().addAll(Arrays.asList(LUNCH_BACK_IMG,LUNCH_HEAD,LUNCH_LOGO_IMG,LUNCH_HSITORY_IMG,LUNCH_COLLECT_IMG,scroll));
//        mainTemplate.getItems().addAll(Arrays.asList(LUNCH_BACK_IMG,LUNCH_HEAD,scroll,FOOTER));
        return document;
    }

//    public static LunchDocument buildLunchDocument() {
//        LunchDocument document = new LunchDocument();
//        document.setDuration(240000);
//        LunchMainTemplate mainTemplate = new LunchMainTemplate();
//        document.setMainTemplate(mainTemplate);
//
////        LunchPojo conRecommend = buildLunchList(TempleUtils.buildDataList(ScheduleService.getRecommendList(),1));
////        conRecommend.setFirstItem(Collections.singletonList(TempleUtils.getFrameRecommand()));
////        conRecommend.setMarginTop("8dp");
////
////        LunchPojo conRanking = buildLunchList(TempleUtils.buildDataList(ScheduleService.getRankingList(), 9));
////        conRanking.setFirstItem(Collections.singletonList(TempleUtils.getFrameRakking()));
//
//        LunchPojo conCategory = buildLunchCategoryList(TempleUtils.buildCategoryDataList(0));
//        conCategory.setFirstItem(Collections.singletonList(TempleUtils.getFrameCategory()));
//
//        LunchPojo scroll = buildLunchScroll();
////        scroll.setItems(Arrays.asList(conRecommend,conRanking,conCategory));
//
////        mainTemplate.getItems().addAll(Arrays.asList(LUNCH_BACK_IMG,LUNCH_HEAD,LUNCH_LOGO_IMG,LUNCH_HSITORY_IMG,LUNCH_COLLECT_IMG,scroll));
//        return document;
//    }

    private static LunchDocument buildCategoryDocument() {
        LunchDocument document = new LunchDocument();
        document.setDuration(90000);
        LunchMainTemplate mainTemplate = new LunchMainTemplate();
        document.setMainTemplate(mainTemplate);

        LunchPojo firstRow = buildLunchCategoryList(TempleUtils.buildCategoryDataList(0));
        LunchPojo secondRow = buildLunchCategoryList(TempleUtils.buildCategoryDataList(1));
        LunchPojo scroll = buildLunchScroll();
        scroll.setMarginTop("240dp");
        scroll.setItems(Arrays.asList(firstRow,secondRow));

        mainTemplate.getItems().addAll(Arrays.asList(LUNCH_BACK_IMG,scroll,CATEGORY_FOOTER));
        return document;
    }

    public static LunchPojo buildAudioComponent(String src) {
        LunchPojo audio = new LunchPojo();
        audio.setType("Audio");
        audio.setComponentId("audioCom");
//        audio.setLooping("false");
//        audio.setAutoplay("true");
        audio.setSrc(src);
        audio.setOnEnd(Collections.singletonList(new Event("SendEvent","audio")));
        return audio;
    }

    private static LunchPojo buildLunchScroll() {
        LunchPojo scroll = new LunchPojo();
        scroll.setType("ScrollView");
        scroll.setDirection("vertical");
        scroll.setMarginTop("180dp");
        scroll.setWidth("100%");
        scroll.setComponentId("udScroll");
        scroll.setPaddingBottom("20dp");
//        scroll.setMarginBottom("10dp");
        return scroll;
    }

    private static LunchPojo buildLunchPartContainer() {
        LunchPojo container = new LunchPojo();
        container.setType("Container");
        container.setMarginTop("180dp");
        container.setWidth("100%");
//        scroll.setMarginBottom("10dp");
        return container;
    }

    private static LunchPojo buildLunchCategoryList(List<LunchBotData> data) {
        LunchPojo container = new LunchPojo();
        //container.setMarginTop("25dp");
        container.setType("Container");
        container.setWidth("100%");
        container.setMarginBottom("15dp");
//        container.setAlignItems("center");

        LunchPojo list = new LunchPojo();
        list.setType("List");
        list.setHeight("280dp");
        list.setMarginLeft("44dp");
        list.setDirection("horizontal");
        list.setData(data);
        list.setItems(Collections.singletonList(LUNCH_LIST_CATEGORY_CON));

        container.setItems(Collections.singletonList(list));

        return container;
    }

    private static LunchPojo buildLunchList(List<LunchBotData> data) {
        LunchPojo container = new LunchPojo();
        container.setType("Container");
        container.setMarginBottom("45dp");
        container.setAlignItems("center");
        container.setWidth("100%");

        LunchPojo list = new LunchPojo();
        list.setType("List");
        list.setMarginLeft("20dp");
//        list.setMarginRight("15dp");
        list.setDirection("horizontal");
        list.setData(data);
        list.setItems(Collections.singletonList(LUNCH_LIST_CON));

        container.setItems(Collections.singletonList(list));

        return container;
    }

    private static LunchPojo buildCategoryList() {
        LunchPojo category = buildImgFrmTxtContainer();
        category.getItems().get(0).setWidth("170dp");
        category.getItems().get(0).setHeight("170dp");
        category.getItems().get(0).setMarginLeft("1dp");
        category.getItems().get(1).setMarginLeft("15dp");
        category.getItems().get(2).setMarginTop("148dp");
        category.getItems().get(2).setMarginLeft("61dp");
        return category;
    }

    private static LunchPojo buildImgFrmTxtContainer() {
        LunchPojo container = new LunchPojo();
        container.setType("Container");
        container.setMarginTop("15dp");
        container.setWidth("200dp");
        container.setMarginRight("33dp");

        //image
        LunchPojo image = new LunchPojo();
        image.setType("Image");
        image.setWidth("200dp");
        image.setHeight("200dp");
        image.setMarginLeft("7dp");
        image.setMarginTop("7dp");
        image.setPosition("absolute");
        image.setScaleType("fitXY");
        image.setBorderRadius("22dp");
        image.setSrc("${data.imageSrc}");
        image.setOnClick(Collections.singletonList(new Event("SendEvent", "${data.imageId}")));

        //frame
        LunchPojo frame = new LunchPojo();
        frame.setType("Frame");
        frame.setWidth("35dp");
        frame.setHeight("35dp");
        frame.setBackgroundColor("rgb(30,144,255)");
        frame.setBorderRadius("20dp");

        LunchPojo frmCon = new LunchPojo();
        frmCon.setType("Container");
        frmCon.setWidth("100%");
        frmCon.setHeight("100%");
        frmCon.setAlignItems("center");
        frmCon.setJustifyContent("center");

        LunchPojo frmText = new LunchPojo();
        frmText.setType("Text");
        frmText.setText("${data.num}");
        frmText.setFontSize("22dp");

        frmCon.setItems(Collections.singletonList(frmText));
        frame.setItems(Collections.singletonList(frmCon));

        //text
        LunchPojo text = new LunchPojo();
        text.setType("Text");
        text.setFontSize("24dp");
        text.setMarginTop("178dp");
        text.setMarginLeft("7dp");
        text.setText("${data.imageName}");

        List<LunchPojo> list = new ArrayList<>();
        list.add(image);
        list.add(frame);
        list.add(text);
        container.setItems(list);
        return container;
    }

    private static LunchPojo buildLunchBackgroundImg() {
        LunchPojo backGroundImg = new LunchPojo();
        backGroundImg.setType("Image");
        backGroundImg.setPosition("absolute");
        backGroundImg.setTop("0dp");
        backGroundImg.setLeft("0dp");
//        backGroundImg.setSrc("https://didi-gz5.jiaoyou365.com/duai/image/backNew.jpg");
        backGroundImg.setSrc("https://hw-gz25.heyqiwu.cn/duai/image/headImg.jpg");
        backGroundImg.setHeight("100%");
        backGroundImg.setScaleType("fitXY");
        backGroundImg.setWidth("100%");

        return backGroundImg;
    }

    private static LunchPojo buildLunchHead() {
//        LunchPojo head = new LunchPojo();
//        head.setType("Header");
//        //head.setHeaderTitle("交游天下");
//        head.setHasBackIcon(false);
//        return head;
        LunchPojo head = new LunchPojo();
        head.setType("Image");
        head.setPosition("absolute");
        head.setTop("0dp");
        head.setLeft("0dp");
        head.setWidth("100%");
        head.setHeight("100%");
        head.setScaleType("centerCrop");
        head.setSrc("https://hw-gz25.heyqiwu.cn/duai/image/heaImg.png");
        return head;
    }

    private static LunchPojo buildHomeFooter() {
        LunchPojo footerCon = new LunchPojo();
        footerCon.setType("Container");
        footerCon.setPosition("absolute");
        footerCon.setWidth("100%");
        footerCon.setLeft("0");
        footerCon.setBottom("0");

        LunchPojo footer = new LunchPojo();
        footer.setType("Footer");
        footer.setHints(Arrays.asList("打开第一个","打开排行榜","打开分类","退出","打开历史记录","打开收藏","推荐作品", "登陆"));

        footerCon.setItems(Collections.singletonList(footer));

        return footerCon;
    }

    private static LunchPojo buildCategoryFooter() {
        LunchPojo footerCon = new LunchPojo();
        footerCon.setType("Container");
        footerCon.setPosition("absolute");
        footerCon.setWidth("100%");
        footerCon.setLeft("0");
        footerCon.setBottom("0");

        LunchPojo footer = new LunchPojo();
        footer.setType("Footer");
        footer.setHints(Arrays.asList("打开第一个","返回"));

        footerCon.setItems(Collections.singletonList(footer));

        return footerCon;
    }

    private static LunchPojo buildLogoImage() {
        LunchPojo logoCon = new LunchPojo();
        logoCon.setType("Container");
        logoCon.setPaddingTop("5dp");
        logoCon.setPosition("absolute");
        logoCon.setHeight("78dp");
        logoCon.setTop("10dp");
        logoCon.setLeft("390dp");
        logoCon.setPaddingBottom("10dp");

        LunchPojo logo = new LunchPojo();
        logo.setType("Image");
        logo.setSrc("https://hw-gz25.heyqiwu.cn/duai/image/logo.png");
        logo.setWidth("200dp");
        logo.setHeight("58dp");
        logoCon.setItems(Collections.singletonList(logo));
        return logoCon;
    }

    private static LunchPojo buildHistoryImg() {
        LunchPojo logo = new LunchPojo();
        logo.setType("Image");
        logo.setPosition("absolute");
        logo.setSrc("https://hw-gz25.heyqiwu.cn/duai/image/history.png");
        logo.setWidth("24dp");
        logo.setHeight("24dp");
        logo.setScaleType("centerInside");
        logo.setTop("20dp");
        logo.setRight("30dp");
        logo.setOnClick(Collections.singletonList(new Event("SendEvent", "lunchHistory")));
        return logo;
    }

    private static LunchPojo buildCollectImg() {
        LunchPojo logo = new LunchPojo();
        logo.setType("Image");
        logo.setPosition("absolute");
        logo.setSrc("https://hw-gz25.heyqiwu.cn/duai/image/collec.png");
        logo.setWidth("24dp");
        logo.setHeight("24dp");
        logo.setScaleType("centerInside");
        logo.setTop("20dp");
        logo.setRight("100dp");
        logo.setOnClick(Collections.singletonList(new Event("SendEvent", "lunchCollect")));
        return logo;
    }

    public static LunchDocument getLunchDocument_part() {
        return lunchDocument_part;
    }

    public static LunchDocument getLunchDocument() {
        return lunchDocument;
    }

    public static void setLunchDocument(LunchDocument lunchDocument) {
        LunchUtils.lunchDocument = lunchDocument;
    }

    public static LunchDocument getCategoryDocument() {
        return categoryDocument;
    }

    public static void setCategoryDocument(LunchDocument categoryDocument) {
        LunchUtils.categoryDocument = categoryDocument;
    }
}
