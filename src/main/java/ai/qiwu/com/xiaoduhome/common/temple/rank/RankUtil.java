package ai.qiwu.com.xiaoduhome.common.temple.rank;

import ai.qiwu.com.xiaoduhome.pojo.dpl.Event;
import lombok.extern.slf4j.Slf4j;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * @author 苗权威
 * @dateTime 19-9-2 下午9:04
 */
@Slf4j
public class RankUtil {

//    private static final RankingPojo RANK_FOOTER = buildRankFooter();
//
//    private static final RankingPojo RANK_BACKGROUND_IMG = buildRankBackgroundImg();
//
//    private static final RankingPojo RANK_HEAD = buildRankHead();
//
//    private static final List<RankingPojo> RANK_CONTAINER_LIST = buildImgTextContainer();
//
//    private static RankingDocument RANKING_DOCUMENT;
//
//    public static RankingDocument getRankDocument() {
//        if (RANKING_DOCUMENT != null) return RANKING_DOCUMENT;
//        RANKING_DOCUMENT = new RankingDocument();
//        RankingMainTemplate mainTemplate = new RankingMainTemplate();
//        RANKING_DOCUMENT.setMainTemplate(mainTemplate);
//
//        List<RankBotInfo> dataFirst = new ArrayList<>();
//        List<RankBotInfo> dataSec = new ArrayList<>();
//        StringBuilder botIds = new StringBuilder();
//        int i = 0;
//        for (GameProjectTb bot : ScheduleService.getRankingList()) {
//            botIds.append(bot.getBotAccount()).append(File.separator);
//            String imgSrc = new StringBuilder(Constants.IMG_PREFIX).append(bot.getBannerImgUrl()).toString();
//            String imageId = new StringBuilder(Constants.PRE_PRODUCT_IMG).append(bot.getName()).toString();
//            RankBotInfo rankBotInfo = new RankBotInfo(imgSrc, imageId, correctName(bot.getName()));
//            if (i < 4) {
//                dataFirst.add(rankBotInfo);
//            }else if (i < 8){
//                dataSec.add(rankBotInfo);
//            }else {
//                break;
//            }
//            i++;
//        }
//        if (botIds.length() != 0) {
//            botIds.deleteCharAt(botIds.length()-1);
//            String idToken = Base64.getEncoder().encodeToString(botIds.toString().getBytes());
//            RANKING_DOCUMENT.setRankBotIds(idToken);
//        }
//        buildMainItem(dataFirst,dataSec, mainTemplate.getItems());
//        return RANKING_DOCUMENT;
//    }
//
//    public static RankingDocument updateRankDocument(int num) {
//        RankingDocument document = new RankingDocument();
//        RankingMainTemplate mainTemplate = new RankingMainTemplate();
//        document.setMainTemplate(mainTemplate);
//        GameProjectTbRepository repository = BaseHolder.getBean("gameProjectTbRepository");
//        List<GameProjectTb> bots;
//        try {
//            bots = repository.postRankBot(num*8);
//        } catch (Exception e) {
//            log.error("查询后续排行榜作品信息出错");
//            return null;
//        }
//        if (bots == null || bots.size() == 0) return null;
//        StringBuilder botIds = new StringBuilder();
//        List<RankBotInfo> dataFirst = new ArrayList<>();
//        List<RankBotInfo> dataSec = new ArrayList<>();
//        int i = 1;
//        for (GameProjectTb bot: bots) {
//            botIds.append(bot.getBotAccount()).append(File.separator);
//            String imgSrc = new StringBuilder(Constants.IMG_PREFIX).append(bot.getBannerImgUrl()).toString();
//            String imageId = new StringBuilder(Constants.PRE_PRODUCT_IMG).append(bot.getName()).toString();
//            RankBotInfo rankBotInfo = new RankBotInfo(imgSrc, imageId, correctName(bot.getName()));
//            rankBotInfo.setNum(i);
//            if (i < 4) {
//                dataFirst.add(rankBotInfo);
//            } else if (i == 4) {
//                dataFirst.add(rankBotInfo);
//            } else if (i < 9) {
//                dataSec.add(rankBotInfo);
//            } else {
//                break;
//            }
//            i++;
//        }
//        if (botIds.length() != 0) {
//            botIds.deleteCharAt(botIds.length()-1);
//            String idToken = Base64.getEncoder().encodeToString(botIds.toString().getBytes());
//            document.setRankBotIds(idToken);
//        }
//        buildMainItem(dataFirst, dataSec, mainTemplate.getItems());
//        return document;
//    }

//    private static void buildMainItem(List<RankBotInfo> dataFirst, List<RankBotInfo> dataSec, List<RankingPojo> items) {
//        RankingPojo main = new RankingPojo();
//        main.setType("ScrollView");
//        main.setHeight("100%");
//        main.setWidth("100%");
//
//        RankingPojo list = new RankingPojo();
//        list.setType("List");
//        list.setWidth("100%");
//        list.setDirection("horizontal");
//        list.setMarginLeft("15dp");
//        list.setData(dataFirst);
//        list.setItems(RANK_CONTAINER_LIST);
//
//        RankingPojo list2 = new RankingPojo();
//        list2.setType("List");
//        list2.setWidth("100%");
//        list2.setDirection("horizontal");
//        list2.setMarginLeft("15dp");
//        list2.setData(dataSec);
//        list2.setItems(RANK_CONTAINER_LIST);
//
//        main.setItems(Arrays.asList(list, list2));
//
//        items.add(RANK_BACKGROUND_IMG);
//        items.add(RANK_HEAD);
//        items.add(main);
//        items.add(RANK_FOOTER);
//    }

    private static RankingPojo buildRankFooter() {
        RankingPojo footerCon = new RankingPojo();
        footerCon.setType("Container");
        footerCon.setPosition("absolute");
        footerCon.setWidth("100%");
        footerCon.setLeft("0");
        footerCon.setBottom("0");

        RankingPojo footer = new RankingPojo();
        footer.setType("Footer");
        footer.setHints(Arrays.asList("打开第一个","下一页","上一页","返回"));

        footerCon.setItems(Collections.singletonList(footer));

        return footerCon;
    }

    private static RankingPojo buildRankBackgroundImg() {
        RankingPojo backGroundImg = new RankingPojo();
        backGroundImg.setType("Image");
        backGroundImg.setPosition("absolute");
        backGroundImg.setTop("0dp");
        backGroundImg.setLeft("0dp");
        backGroundImg.setSrc("https://didi-gz5.jiaoyou365.com/duai/image/backend.jpg");
        backGroundImg.setHeight("100%");
        backGroundImg.setWidth("100%");
        backGroundImg.setScaleType("fitXY");

        return backGroundImg;
    }

    private static RankingPojo buildRankHead() {
        RankingPojo head = new RankingPojo();
        head.setType("Header");
        head.setHeaderTitle("排行榜");
        head.setHasBackIcon(false);

        return head;
    }

    private static List<RankingPojo> buildImgTextContainer() {
        RankingPojo container = new RankingPojo();
        container.setType("Container");
        container.setWidth("220dp");
        container.setMarginBottom("35dp");
        container.setMarginLeft("15dp");

        RankingPojo image = new RankingPojo();
        image.setType("Image");
        image.setSrc("${data.imageSrc}");
        image.setHeight("170dp");
        image.setWidth("170dp");
        image.setMarginLeft("7dp");
        image.setMarginTop("7dp");
        image.setPosition("absolute");
        image.setScaleType("fitXY");
        image.setBorderRadius("10dp");

        Event onClickEvent = new Event("SendEvent", "${data.imageId}");
        image.setOnClick(Collections.singletonList(onClickEvent));

        RankingPojo frame = new RankingPojo();
        frame.setType("Frame");
        frame.setWidth("40dp");
        frame.setHeight("40dp");
        frame.setBackgroundColor("rgba(0,0,0,0.9)");
        frame.setBorderRadius("12dp");

        RankingPojo textContainer = new RankingPojo();
        textContainer.setWidth("100%");
        textContainer.setHeight("100%");
        textContainer.setAlignItems("center");

        RankingPojo numText = new RankingPojo();
        numText.setText("${data.num}");
        numText.setFontSize("25dp");

        textContainer.setItems(Collections.singletonList(numText));
        frame.setItems(Collections.singletonList(textContainer));

        RankingPojo text = new RankingPojo();
        text.setType("Text");
        text.setFontSize("25dp");
        text.setMarginTop("140dp");
        text.setMarginLeft("7dp");
        text.setText("${data.imageName}");

        container.setItems(Arrays.asList(image, frame, text));
        return Collections.singletonList(container);
    }

    private static String correctName(String name) {
        int brace = name.indexOf("{");
        if (brace != -1) name = name.substring(0, brace);
        return name;
    }
}
