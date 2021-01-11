package xiaoduhome.common.temple.recommand;


import ai.qiwu.com.xiaoduhome.common.Constants;
import ai.qiwu.com.xiaoduhome.common.temple.TempleUtils;
import ai.qiwu.com.xiaoduhome.pojo.data.ProjectData;
import ai.qiwu.com.xiaoduhome.pojo.dpl.Event;
import ai.qiwu.com.xiaoduhome.service.ScheduleServiceNew;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.CollectionUtils;

import java.io.File;
import java.util.*;

import static ai.qiwu.com.xiaoduhome.common.Constants.RE_RA_CA_PAGE_SIZE;

/**
 * @author 苗权威
 * @dateTime 19-9-5 上午11:25
 */
@Slf4j
public class RecommendUtil {

    private static final RecommandPojo RECOM_BACK_IMG = buildRecomendBackgroundImg();
    private static final RecommandPojo RECOM_HEAD = buildRecommendHead();
    private static final RecommandPojo RANK_HEAD = buildRankHead();
    private static final RecommandPojo FUNNY_HEAD = buildFunnyHead();
    private static final RecommandPojo RECOM_FOOTER = buildRecommendFooter();
    private static final RecommandPojo RANK_CATEGORY_FOOTER = buildRankCategoryFooter();
    private static final RecommandPojo FUNNY_FOOTER = buildFunnyFooter();
    private static final RecommandPojo RECOM_LIST_CON = buildImgFrmTxtContainer();
    private static final RecommandPojo FUNNY_LIST_CON = buildImgFrmTxtContainerFunny();

    private static volatile RecommandDocument rankDocument;

    public static RecommandDocument getRecommendDocument(int num, String channelId) {
        RecommandDocument document = new RecommandDocument();
        document.setDuration(120000);
        RecommandMainTemplate mainTemplate = new RecommandMainTemplate();
        document.setMainTemplate(mainTemplate);

        RecommandPojo scrollView = new RecommandPojo();
        scrollView.setType("ScrollView");
        scrollView.setWidth("100%");
        scrollView.setMarginBottom("140dp");
        scrollView.setDirection("vertical");
        List<RecommandPojo> scrollList = new ArrayList<>();
        scrollView.setItems(scrollList);

        List<ProjectData> bots = ScheduleServiceNew.getTheChannelAllProjectData(channelId);
        int bound = bots.size();
        StringBuilder botIds = new StringBuilder();
        int begin = num*RE_RA_CA_PAGE_SIZE;

        int end;
        if (begin >= bound) return null;
        bound = begin+RE_RA_CA_PAGE_SIZE <= bound ? begin+RE_RA_CA_PAGE_SIZE : bound;

        int count = 0;
        while (begin < bound) {
            end = begin+4 <= bound ? begin+4 : bound;
            scrollList.add(buildRecommendList(getListData(count,begin, end, bots, botIds)));
            count++;
            begin = end;
        }
        mainTemplate.getItems().addAll(Arrays.asList(RECOM_BACK_IMG,RECOM_HEAD,scrollView,RECOM_FOOTER));

        botIds.deleteCharAt(botIds.length()-1);
        String idToken = Base64.getEncoder().encodeToString(botIds.toString().getBytes());
        document.setRecommendIdList(idToken);
        return document;
    }

    public static RecommandDocument getCategory(int num, String category) {
        RecommandDocument document = new RecommandDocument();
        document.setDuration(120000);
        RecommandMainTemplate mainTemplate = new RecommandMainTemplate();
        document.setMainTemplate(mainTemplate);

        RecommandPojo scrollView = new RecommandPojo();
        scrollView.setType("ScrollView");
        scrollView.setWidth("100%");
        scrollView.setMarginBottom("140dp");
        scrollView.setDirection("vertical");
        scrollView.setComponentId("udScroll");
        List<RecommandPojo> scrollList = new ArrayList<>();
        scrollView.setItems(scrollList);

        List<ProjectData> dataList = ScheduleServiceNew.category2Bots.get(category);

        if (CollectionUtils.isEmpty(dataList)) return null;
        int bound = dataList.size();
        int begin = num*RE_RA_CA_PAGE_SIZE;
        int end;
        if (begin >= bound) return null;
        bound = begin+RE_RA_CA_PAGE_SIZE <= bound ? begin+RE_RA_CA_PAGE_SIZE : bound;

        //StringBuilder botIds = new StringBuilder();

        int count = 0;
        while (begin < bound) {
            end = begin+4 <= bound ? begin+4 : bound;
            scrollList.add(buildRecommendList(getListDataNoIds(count, begin, end, dataList)));
            count++;
            begin = end;
        }
        int pageSize = dataList.size() / RE_RA_CA_PAGE_SIZE + (dataList.size()%RE_RA_CA_PAGE_SIZE == 0 ? 0:1);
        mainTemplate.getItems().addAll(Arrays.asList(RECOM_BACK_IMG,buildRecommendCollectHead(category),
                scrollView,buildPageButton(num+1, pageSize),RANK_CATEGORY_FOOTER));

//        botIds.deleteCharAt(botIds.length()-1);
//        String idToken = Base64.getEncoder().encodeToString(botIds.toString().getBytes());
//        document.setRecommendIdList(idToken);
        return document;
    }

    public static RecommandDocument getRankDocumentFirstPage(String channelId) {
        if (rankDocument != null) return rankDocument;
        RecommandDocument document = new RecommandDocument();
        document.setDuration(120000);
        RecommandMainTemplate mainTemplate = new RecommandMainTemplate();
        document.setMainTemplate(mainTemplate);

        RecommandPojo scrollView = new RecommandPojo();
        scrollView.setType("ScrollView");
        scrollView.setWidth("100%");
        scrollView.setMarginBottom("140dp");
        scrollView.setDirection("vertical");
        scrollView.setComponentId("udScroll");
        List<RecommandPojo> scrollList = new ArrayList<>();
        scrollView.setItems(scrollList);

        RecommandPojo rowContainer = new RecommandPojo();
        rowContainer.setType("Container");
        rowContainer.setFlexDirection("row");
        rowContainer.setMarginLeft("20dp");
        rowContainer.setMarginBottom("55dp");

        StringBuilder botIds = new StringBuilder();
        List<ProjectData> list = ScheduleServiceNew.getTheChannelProjectsByWatch(channelId);
        //log.info("RANK_ORDER_ID_LIST size = "+list.size());
        ProjectData first = list.get(0);
        botIds.append(first.getBotAccount()).append(File.separator);
        ProjectData second = list.get(1);
        botIds.append(second.getBotAccount()).append(File.separator);
        ProjectData third = list.get(2);
        botIds.append(third.getBotAccount()).append(File.separator);
        ProjectData forth = list.get(3);
        botIds.append(forth.getBotAccount()).append(File.separator);
        RecommandPojo pojoFirst = buildImgFrmTxtContainerFirstRow(first.getBannerImgUrl(), Constants.PRE_PRODUCT_IMG + first.getName(),
                TempleUtils.correctName(first.getName()), "first.png");
        RecommandPojo pojoSe = buildImgFrmTxtContainerFirstRow(second.getBannerImgUrl(), Constants.PRE_PRODUCT_IMG + second.getName(),
                TempleUtils.correctName(second.getName()), "second.png");
        RecommandPojo pojoThird = buildImgFrmTxtContainerFirstRow(third.getBannerImgUrl(), Constants.PRE_PRODUCT_IMG + third.getName(),
                TempleUtils.correctName(third.getName()), "third.png");
        RecommandPojo pojoFourth = buildImgFrmTxtContainerFourth(forth.getBannerImgUrl(), Constants.PRE_PRODUCT_IMG + forth.getName(),
                TempleUtils.correctName(forth.getName()));
        rowContainer.setItems(Arrays.asList(pojoFirst, pojoSe, pojoThird, pojoFourth));

        scrollList.add(rowContainer);

        scrollList.add(buildRecommendList(getListData(1, 4, 8, list, botIds)));
        scrollList.add(buildRecommendList(getListData(2, 8, 12, list, botIds)));
        //scrollList.add(buildRecommendList(getListData(3, 12, 16, list, botIds)));

        mainTemplate.getItems().addAll(Arrays.asList(RECOM_BACK_IMG,RANK_HEAD,scrollView,buildPageButton(1,
                list.size()/RE_RA_CA_PAGE_SIZE + (list.size()%RE_RA_CA_PAGE_SIZE==0?0:1)),RANK_CATEGORY_FOOTER));

        botIds.deleteCharAt(botIds.length()-1);
        String idToken = Base64.getEncoder().encodeToString(botIds.toString().getBytes());
        document.setRecommendIdList(idToken);
        rankDocument = document;
        return document;
    }

    public static RecommandDocument getRankOtherPageDocument(int num, String channelId) {
        RecommandDocument document = new RecommandDocument();
        document.setDuration(120000);
        RecommandMainTemplate mainTemplate = new RecommandMainTemplate();
        document.setMainTemplate(mainTemplate);

        RecommandPojo scrollView = new RecommandPojo();
        scrollView.setType("ScrollView");
        scrollView.setWidth("100%");
        scrollView.setMarginBottom("140dp");
        scrollView.setDirection("vertical");
        List<RecommandPojo> scrollList = new ArrayList<>();
        scrollView.setItems(scrollList);

        List<ProjectData> list = ScheduleServiceNew.getTheChannelProjectsByWatch(channelId);
        int bound = list.size();
        int begin = num*RE_RA_CA_PAGE_SIZE;
        int end;
        if (begin >= bound) return null;
        bound = begin+RE_RA_CA_PAGE_SIZE <= bound ? begin+RE_RA_CA_PAGE_SIZE : bound;

        StringBuilder botIds = new StringBuilder();
        int row = 0;
        while (begin < bound) {
            end = begin+4 <= bound ? begin+4 : bound;
            scrollList.add(buildRecommendList(getListData(row, begin, end, list, botIds)));
            row++;
            begin = end;
        }
        mainTemplate.getItems().addAll(Arrays.asList(RECOM_BACK_IMG,RANK_HEAD,scrollView,
                buildPageButton(num+1, list.size()/RE_RA_CA_PAGE_SIZE + (list.size()%RE_RA_CA_PAGE_SIZE==0?0:1)),RANK_CATEGORY_FOOTER));

        botIds.deleteCharAt(botIds.length()-1);
        String idToken = Base64.getEncoder().encodeToString(botIds.toString().getBytes());
        document.setRecommendIdList(idToken);
        return document;
    }

    public static RecommandPojo buildAudioComponent(String src) {
        RecommandPojo audio = new RecommandPojo();
        audio.setType("Audio");
        audio.setComponentId("audioCom");
//        audio.setLooping("false");
//        audio.setAutoplay("true");
        audio.setSrc(src);
        audio.setOnEnd(Collections.singletonList(new Event("SendEvent","audio")));
        return audio;
    }

    public static RecommandDocument getFunnyDocument(List<ProjectData> bots, String audio) {
        RecommandDocument document = new RecommandDocument();
        document.setDuration(240000);
        RecommandMainTemplate mainTemplate = new RecommandMainTemplate();
        document.setMainTemplate(mainTemplate);

        List<RecommandBotInfo> data = new ArrayList<>();
        StringBuilder botIds = new StringBuilder();
        int num = 0;
        for (ProjectData bot: bots) {
            String imageId = Constants.PRE_PRODUCT_IMG + bot.getName();
            RecommandBotInfo detail = new RecommandBotInfo(bot.getBannerImgUrl(),imageId, TempleUtils.correctName(bot.getName()));
            detail.setNum(++num);
            data.add(detail);
            botIds.append(bot.getBotAccount()).append(File.separator);
        }
        mainTemplate.getItems().addAll(Arrays.asList(RECOM_BACK_IMG, FUNNY_HEAD, buildFunnyList(data), buildFunnyAudio(audio),FUNNY_FOOTER));
        botIds.deleteCharAt(botIds.length()-1);
        String idToken = Base64.getEncoder().encodeToString(botIds.toString().getBytes());
        document.setRecommendIdList(idToken);
        return document;
    }

    private static RecommandPojo buildFunnyAudio(String audioSrc) {
        RecommandPojo audio = new RecommandPojo();
        audio.setType("Audio");
        audio.setComponentId("audioCom");
        audio.setSrc(audioSrc);
        audio.setLooping("false");
        audio.setAutoplay("true");
        return audio;
    }

    private static RecommandPojo buildFunnyList(List<RecommandBotInfo> data) {
//        RecommandPojo con = new RecommandPojo();
//        con.setType("Container");
//        con.setHeight("100%");
//        con.setWidth("100%");
//        con.setAlignItems("center");

        RecommandPojo list = new RecommandPojo();
        list.setType("List");
        list.setDirection("horizontal");
        list.setMarginTop("65dp");
        list.setMarginLeft("25dp");
        list.setData(data);
        list.setItems(Collections.singletonList(FUNNY_LIST_CON));

//        con.setItems(Collections.singletonList(list));

        return list;
    }

    private static RecommandPojo buildImgFrmTxtContainerFunny() {
        RecommandPojo container = new RecommandPojo();
        container.setType("Container");
        container.setWidth("275dp");
        container.setMarginLeft("30dp");

        //image
        RecommandPojo image = new RecommandPojo();
        image.setType("Image");
        image.setWidth("230dp");
        image.setHeight("230dp");
        image.setMarginLeft("7dp");
        image.setMarginTop("7dp");
        image.setPosition("absolute");
        image.setScaleType("fitXY");
        image.setBorderRadius("22dp");
        image.setSrc("${data.imageSrc}");
        image.setOnClick(Collections.singletonList(new Event("SendEvent", "${data.imageId}")));

        //frame
        RecommandPojo frame = new RecommandPojo();
        frame.setType("Frame");
        frame.setWidth("35dp");
        frame.setHeight("35dp");
        frame.setBackgroundColor("rgb(30,144,255)");
        frame.setBorderRadius("20dp");

        RecommandPojo frmCon = new RecommandPojo();
        frmCon.setType("Container");
        frmCon.setWidth("100%");
        frmCon.setHeight("100%");
        frmCon.setAlignItems("center");
        frmCon.setJustifyContent("center");

        RecommandPojo frmText = new RecommandPojo();
        frmText.setType("Text");
        frmText.setText("${data.num}");
        frmText.setFontSize("22dp");

        frmCon.setItems(Collections.singletonList(frmText));
        frame.setItems(Collections.singletonList(frmCon));

        //text
        RecommandPojo text = new RecommandPojo();
        text.setType("Text");
        text.setFontSize("33dp");
        text.setMarginTop("200dp");
        text.setMarginLeft("7dp");
        text.setText("${data.imageName}");

        container.setItems(Arrays.asList(image,frame,text));
        return container;
    }

    private static RecommandPojo buildImgFrmTxtContainerFirstRow(String imgSrc, String imgId, String productName, String rankImgSrc) {
        RecommandPojo container = new RecommandPojo();
        container.setType("Container");
        container.setWidth("220dp");
        container.setMarginLeft("15dp");

        //image
        RecommandPojo image = new RecommandPojo();
        image.setType("Image");
        image.setWidth("190dp");
        image.setHeight("190dp");
        image.setMarginLeft("7dp");
        image.setMarginTop("7dp");
        image.setPosition("absolute");
        image.setScaleType("fitXY");
        image.setBorderRadius("15dp");
        image.setSrc(imgSrc);
        image.setOnClick(Collections.singletonList(new Event("SendEvent", imgId)));

        //frame
        RecommandPojo rankImg = new RecommandPojo();
        rankImg.setType("Image");
        rankImg.setScaleType("fitXY");
        rankImg.setWidth("45dp");
        rankImg.setHeight("60dp");
        rankImg.setSrc(new StringBuilder("https://didi-gz5.jiaoyou365.com/duai/image/").append(rankImgSrc).toString());

        //text
        RecommandPojo text = new RecommandPojo();
        text.setType("Text");
        text.setFontSize("25dp");
        text.setMarginTop("145dp");
        text.setMarginLeft("7dp");
        text.setText(productName);

        container.setItems(Arrays.asList(image,rankImg,text));
        return container;
    }

    private static RecommandPojo buildImgFrmTxtContainerFourth(String imgSrc, String imgId, String productName) {
        RecommandPojo container = new RecommandPojo();
        container.setType("Container");
        container.setWidth("220dp");
        container.setMarginLeft("15dp");

        //image
        RecommandPojo image = new RecommandPojo();
        image.setType("Image");
        image.setWidth("190dp");
        image.setHeight("190dp");
        image.setMarginLeft("7dp");
        image.setMarginTop("7dp");
        image.setPosition("absolute");
        image.setScaleType("fitXY");
        image.setBorderRadius("15dp");
        image.setSrc(imgSrc);
        image.setOnClick(Collections.singletonList(new Event("SendEvent", imgId)));

        //frame
        RecommandPojo frame = new RecommandPojo();
        frame.setType("Frame");
        frame.setWidth("35dp");
        frame.setHeight("35dp");
        frame.setBackgroundColor("rgb(30,144,255)");
        frame.setBorderRadius("20dp");

        RecommandPojo frmCon = new RecommandPojo();
        frmCon.setType("Container");
        frmCon.setWidth("100%");
        frmCon.setHeight("100%");
        frmCon.setAlignItems("center");

        RecommandPojo frmText = new RecommandPojo();
        frmText.setType("Text");
        frmText.setText("4");
        frmText.setFontSize("22dp");

        frmCon.setItems(Collections.singletonList(frmText));
        frame.setItems(Collections.singletonList(frmCon));

        //text
        RecommandPojo text = new RecommandPojo();
        text.setType("Text");
        text.setFontSize("25dp");
        text.setMarginTop("165dp");
        text.setMarginLeft("7dp");
        text.setText(productName);

        container.setItems(Arrays.asList(image,frame,text));
        return container;
    }

    private static RecommandPojo buildPageButton(int curIndex, int num) {
        RecommandPojo fCon = new RecommandPojo();
        fCon.setType("Container");
        fCon.setTop("20dp");
        fCon.setRight("15dp");
        fCon.setPosition("absolute");

        RecommandPojo frame = new RecommandPojo();
        frame.setType("Frame");
        frame.setBorderRadius("40dp");
        frame.setBackgroundColor("rgba(0,0,0,0.5)");

        RecommandPojo sCon = new RecommandPojo();
        sCon.setType("Container");
        sCon.setWidth("130dp");
        sCon.setHeight("35dp");
        sCon.setAlignItems("center");
        sCon.setJustifyContent("center");

        RecommandPojo text = new RecommandPojo();
        text.setType("Text");
        text.setFontSize("22dp");
        if (num == 0) text.setText("第"+curIndex+"页");
        else text.setText(curIndex+"/"+num);

        sCon.setItems(Collections.singletonList(text));
        frame.setItems(Collections.singletonList(sCon));
        fCon.setItems(Collections.singletonList(frame));
        return fCon;
    }

    private static List<RecommandBotInfo> getListData(int row,int begin, int end, List<ProjectData> bots, StringBuilder botIds) {
        List<RecommandBotInfo> data = new ArrayList<>();
//        int n = 1+row*4;
        end = end > bots.size() ? bots.size() : end;
        for (int i = begin; i < end; i++) {
            ProjectData bot = bots.get(i);
            botIds.append(bot.getBotAccount()).append(File.separator);
            String imageId = new StringBuilder(Constants.PRE_PRODUCT_IMG).append(bot.getName()).toString();
            RecommandBotInfo detail = new RecommandBotInfo(bot.getBannerImgUrl(),imageId, TempleUtils.correctName(bot.getName()));
            detail.setNum(i+1);
            data.add(detail);
        }
        return data;
    }

    private static List<RecommandBotInfo> getListDataNoIds(int row,int begin, int end, List<ProjectData> bots) {
        List<RecommandBotInfo> data = new ArrayList<>();
        int n = 1+row*4;
        for (int i = begin; i < end; i++) {
            ProjectData bot = bots.get(i);
            String imageId = new StringBuilder(Constants.PRE_PRODUCT_IMG).append(bot.getName()).toString();
            RecommandBotInfo detail = new RecommandBotInfo(bot.getBannerImgUrl(),imageId, bot.getName());
            detail.setNum(i-begin+n);
            data.add(detail);
        }
        return data;
    }

    private static RecommandPojo buildRecommendList(List<RecommandBotInfo> data) {
        RecommandPojo list = new RecommandPojo();
        list.setType("List");
        list.setWidth("100%");
        list.setHeight("240dp");
        list.setMarginLeft("20dp");
        list.setMarginBottom("55dp");
        list.setDirection("horizontal");
        list.setData(data);
        list.setItems(Collections.singletonList(RECOM_LIST_CON));

        return list;
    }

    private static RecommandPojo buildImgFrmTxtContainer() {
        RecommandPojo container = new RecommandPojo();
        container.setType("Container");
        container.setWidth("220dp");
        container.setMarginLeft("15dp");

        //image
        RecommandPojo image = new RecommandPojo();
        image.setType("Image");
        image.setWidth("190dp");
        image.setHeight("190dp");
        image.setMarginLeft("7dp");
        image.setMarginTop("7dp");
        image.setPosition("absolute");
        image.setScaleType("fitXY");
        image.setBorderRadius("15dp");
        image.setSrc("${data.imageSrc}");
        image.setOnClick(Collections.singletonList(new Event("SendEvent", "${data.imageId}")));

        //frame
        RecommandPojo frame = new RecommandPojo();
        frame.setType("Frame");
        frame.setWidth("35dp");
        frame.setHeight("35dp");
        frame.setBackgroundColor("rgb(30,144,255)");
        frame.setBorderRadius("20dp");

        RecommandPojo frmCon = new RecommandPojo();
        frmCon.setType("Container");
        frmCon.setWidth("100%");
        frmCon.setHeight("100%");
        frmCon.setAlignItems("center");

        RecommandPojo frmText = new RecommandPojo();
        frmText.setType("Text");
        frmText.setText("${data.num}");
        frmText.setFontSize("22dp");

        frmCon.setItems(Collections.singletonList(frmText));
        frame.setItems(Collections.singletonList(frmCon));

        //text
        RecommandPojo text = new RecommandPojo();
        text.setType("Text");
        text.setFontSize("25dp");
        text.setMarginTop("165dp");
        text.setMarginLeft("7dp");
        text.setText("${data.imageName}");

        container.setItems(Arrays.asList(image,frame,text));
        return container;
    }

    private static RecommandPojo buildRecommendFooter() {
        RecommandPojo footerCon = new RecommandPojo();
        footerCon.setType("Container");
        footerCon.setPosition("absolute");
        footerCon.setWidth("100%");
        footerCon.setLeft("0");
        footerCon.setBottom("0");

        RecommandPojo footer = new RecommandPojo();
        footer.setType("Footer");
        footer.setHints(Arrays.asList("打开第一个","下一页","上一页","返回"));

        footerCon.setItems(Collections.singletonList(footer));

        return footerCon;
    }

    private static RecommandPojo buildRankCategoryFooter() {
        RecommandPojo footerCon = new RecommandPojo();
        footerCon.setType("Container");
        footerCon.setPosition("absolute");
        footerCon.setWidth("100%");
        footerCon.setLeft("0");
        footerCon.setBottom("0");

        RecommandPojo footer = new RecommandPojo();
        footer.setType("Footer");
        footer.setHints(Arrays.asList("打开第一个","下一页","上一页","跳到第三页","返回"));

        footerCon.setItems(Collections.singletonList(footer));

        return footerCon;
    }

    private static RecommandPojo buildFunnyFooter() {
        RecommandPojo footerCon = new RecommandPojo();
        footerCon.setType("Container");
        footerCon.setPosition("absolute");
        footerCon.setWidth("100%");
        footerCon.setLeft("0");
        footerCon.setBottom("0");

        RecommandPojo footer = new RecommandPojo();
        footer.setType("Footer");
        footer.setHints(Arrays.asList("打开第一个","返回"));

        footerCon.setItems(Collections.singletonList(footer));

        return footerCon;
    }

    private static RecommandPojo buildRecomendBackgroundImg() {
        RecommandPojo backGroundImg = new RecommandPojo();
        backGroundImg.setType("Image");
        backGroundImg.setPosition("absolute");
        backGroundImg.setTop("0dp");
        backGroundImg.setLeft("0dp");
        backGroundImg.setSrc("https://didi-gz5.jiaoyou365.com/duai/image/backNew.jpg");
        backGroundImg.setHeight("100%");
        backGroundImg.setWidth("100%");
        backGroundImg.setScaleType("fitXY");

        return backGroundImg;
    }

    private static RecommandPojo buildRecommendHead() {
        RecommandPojo head = new RecommandPojo();
        head.setType("Header");
        head.setHeaderTitle("推荐页");
        head.setHasBackIcon(false);

        return head;
    }

    private static RecommandPojo buildRankHead() {
        RecommandPojo head = new RecommandPojo();
        head.setType("Header");
        head.setHeaderTitle("排行榜");
        head.setHasBackIcon(false);

        return head;
    }

    private static RecommandPojo buildFunnyHead() {
        RecommandPojo head = new RecommandPojo();
        head.setType("Header");
        head.setHeaderTitle("精品推荐");
        head.setHasBackIcon(false);

        return head;
    }

    private static RecommandPojo buildRecommendCollectHead(String category) {
        RecommandPojo head = new RecommandPojo();
        head.setType("Header");
        head.setHeaderTitle(category);
        head.setHasBackIcon(false);

        return head;
    }

//    public static RecommandDocument getRankDocument() {
//        return rankDocument;
//    }
//
    public static void setRankDocument(RecommandDocument rankDocument) {
        RecommendUtil.rankDocument = rankDocument;
    }
}
