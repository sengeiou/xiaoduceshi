package xiaoduhome.common.temple;

import ai.qiwu.com.xiaoduhome.pojo.dpl.Event;
import ai.qiwu.com.xiaoduhome.pojo.pageData.HistoryData;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * @author 苗权威
 * @dateTime 19-9-25 下午3:38
 */
public class CollectHistoryUtils {

    private static final CollectHistoryPagePojo MAIN_CON = buildMainContainer();
    private static final CollectHistoryPagePojo BACK_IMG = buildLunchBackgroundImg();
    private static final CollectHistoryPagePojo COLLECT_HEAD = buildLunchHead();
    private static final CollectHistoryPagePojo HISTORY_HEAD = buildHistoryHead();
    private static final CollectHistoryPagePojo FOOTER = buildFooter();

    public static CollectHistoryDocument getDocument(List<HistoryData> datas, boolean collect,
                                                     int index, int num) {
        CollectHistoryDocument document = new CollectHistoryDocument();
        document.setDuration(120000);
        CollectHistoryMainTemplate mainTemplate = new CollectHistoryMainTemplate();
        document.setMainTemplate(mainTemplate);

        CollectHistoryPagePojo allContainer = new CollectHistoryPagePojo();
        allContainer.setType("Container");
        allContainer.setWidth("100%");
        allContainer.setPaddingRight("15dp");
        allContainer.setPaddingLeft("15dp");
        allContainer.setPaddingBottom("130dp");

        CollectHistoryPagePojo list = new CollectHistoryPagePojo();
        list.setType("List");
        list.setHeight("100%");
        list.setWidth("100%");
        list.setDirection("vertical");
        list.setComponentId("udScroll");
        list.setItems(Collections.singletonList(MAIN_CON));
        list.setData(datas);

        allContainer.setItems(Collections.singletonList(list));

        CollectHistoryPagePojo head;
        if (collect) head = COLLECT_HEAD;
        else head = HISTORY_HEAD;
        mainTemplate.getItems().addAll(Arrays.asList(BACK_IMG, head, allContainer, FOOTER, buildPageButton(index, num)));
        return document;
    }

    public static CollectHistoryPagePojo buildAudioComponent(String src) {
        CollectHistoryPagePojo audio = new CollectHistoryPagePojo();
        audio.setType("Audio");
        audio.setComponentId("audioCom");
//        audio.setLooping("false");
//        audio.setAutoplay("true");
        audio.setSrc(src);
        audio.setOnEnd(Collections.singletonList(new Event("SendEvent","audio")));
        return audio;
    }

    private static CollectHistoryPagePojo buildMainContainer() {
        CollectHistoryPagePojo main = new CollectHistoryPagePojo();
        main.setType("Container");
        main.setHeight("100dp");
        main.setWidth("100%");
        main.setMarginBottom("20dp");
        main.setFlexDirection("row");
//        main.setJustifyContent("center");
//        main.setAlignItems("center");
        main.setComponentId("${data.botAccountId}");
        main.setOnClick(Collections.singletonList(new Event("SendEvent", "${data.botAccountId}")));

        // num
        CollectHistoryPagePojo numContainer = new CollectHistoryPagePojo();
        numContainer.setType("Container");
        numContainer.setWidth("100dp");
        numContainer.setJustifyContent("center");
        numContainer.setAlignItems("center");
        numContainer.setMarginTop("7dp");
        CollectHistoryPagePojo num = new CollectHistoryPagePojo();
        num.setType("Text");
        num.setFontSize("40dp");
//        num.setMarginLeft("40dp");
//        num.setMarginTop("7dp");
        num.setText("${data.collectPageLeftNum}");
        numContainer.setItems(Collections.singletonList(num));

        // image
        CollectHistoryPagePojo image = new CollectHistoryPagePojo();
        image.setType("Image");
        image.setWidth("80dp");
        image.setHeight("80dp");
        image.setBorderRadius("10dp");
        image.setMarginLeft("20dp");
        image.setSrc("${data.bannerImgUrl}");

        // name,intro container
        CollectHistoryPagePojo nameIntroCon = new CollectHistoryPagePojo();
        nameIntroCon.setType("Container");
        nameIntroCon.setHeight("75dp");
        nameIntroCon.setWidth("50%");
        nameIntroCon.setMarginLeft("20dp");
        nameIntroCon.setFlexDirection("column");

        CollectHistoryPagePojo name = new CollectHistoryPagePojo();
        name.setType("Text");
        name.setFontSize("30dp");
        name.setText("${data.name}");

        CollectHistoryPagePojo intro = new CollectHistoryPagePojo();
        intro.setType("Text");
        intro.setFontSize("20dp");
        intro.setMarginTop("8dp");
        intro.setText("${data.intro}");
        intro.setTextOverflow("ellipsis");
        intro.setMaxLines(1);

        nameIntroCon.setItems(Arrays.asList(name, intro));

        // author
        CollectHistoryPagePojo author = new CollectHistoryPagePojo();
        author.setType("Text");
        author.setFontSize("25dp");
        author.setMarginTop("20dp");
        author.setText("${data.authorName}");
        author.setMarginLeft("100dp");
        author.setTextOverflow("ellipsis");

//        // divide
//        CollectHistoryPagePojo divide = new CollectHistoryPagePojo();
//        divide.setType("Frame");
//        divide.setPosition("absolute");
//        divide.setMarginTop("90dp");
//        divide.setWidth("100%");
//        divide.setBorderColor("rgba(255,255,255,0.3)");
//        divide.setBorderWidth("0.5dp");

        main.setItems(Arrays.asList(numContainer,image, nameIntroCon, author));
        return main;
    }

    private static CollectHistoryPagePojo buildPageButton(int curIndex, int num) {
        CollectHistoryPagePojo fCon = new CollectHistoryPagePojo();
        fCon.setType("Container");
        fCon.setTop("20dp");
        fCon.setRight("15dp");
        fCon.setPosition("absolute");

        CollectHistoryPagePojo frame = new CollectHistoryPagePojo();
        frame.setType("Frame");
        frame.setBorderRadius("40dp");
        frame.setBackgroundColor("rgba(0,0,0,0.5)");

        CollectHistoryPagePojo sCon = new CollectHistoryPagePojo();
        sCon.setType("Container");
        sCon.setWidth("130dp");
        sCon.setHeight("35dp");
        sCon.setAlignItems("center");
        sCon.setJustifyContent("center");

        CollectHistoryPagePojo text = new CollectHistoryPagePojo();
        text.setType("Text");
        text.setFontSize("22dp");
        if (num == 0) text.setText("第"+curIndex+"页");
        else text.setText(curIndex+"/"+num);

        sCon.setItems(Collections.singletonList(text));
        frame.setItems(Collections.singletonList(sCon));
        fCon.setItems(Collections.singletonList(frame));
        return fCon;
    }

    private static CollectHistoryPagePojo buildFooter() {
        CollectHistoryPagePojo footerCon = new CollectHistoryPagePojo();
        footerCon.setType("Container");
        footerCon.setPosition("absolute");
        footerCon.setWidth("100%");
        footerCon.setLeft("0");
        footerCon.setBottom("0");

        CollectHistoryPagePojo footer = new CollectHistoryPagePojo();
        footer.setType("Footer");
        footer.setHints(Arrays.asList("打开第一个","下一页","上一页","返回","跳到第三页"));
        footerCon.setItems(Collections.singletonList(footer));
        return footerCon;
    }

    private static CollectHistoryPagePojo buildLunchBackgroundImg() {
        CollectHistoryPagePojo backGroundImg = new CollectHistoryPagePojo();
        backGroundImg.setType("Image");
        backGroundImg.setPosition("absolute");
        backGroundImg.setTop("0dp");
        backGroundImg.setLeft("0dp");
        backGroundImg.setSrc("https://didi-gz5.jiaoyou365.com/duai/image/backNew.jpg");
        backGroundImg.setHeight("100%");
        backGroundImg.setScaleType("fitXY");
        backGroundImg.setWidth("100%");
        return backGroundImg;
    }

    private static CollectHistoryPagePojo buildLunchHead() {
        CollectHistoryPagePojo head = new CollectHistoryPagePojo();
        head.setType("Header");
        head.setHeaderTitle("收藏");
        head.setHasBackIcon(false);
        return head;
    }

    private static CollectHistoryPagePojo buildHistoryHead() {
        CollectHistoryPagePojo head = new CollectHistoryPagePojo();
        head.setType("Header");
        head.setHeaderTitle("历史记录");
        head.setHasBackIcon(false);
        return head;
    }
}
