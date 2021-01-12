package ai.qiwu.com.xiaoduhome.common.temple.play;

import ai.qiwu.com.xiaoduhome.pojo.ActorWithWord;
import ai.qiwu.com.xiaoduhome.pojo.dpl.Event;
import ai.qiwu.com.xiaoduhome.pojo.playAside.PlayPageData;
import ai.qiwu.com.xiaoduhome.service.ScheduleServiceNew;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * @author 苗权威
 * @dateTime 19-9-2 上午10:21
 */
public class PlayUtils {

    private static final PlayPojo PLAY_HINT = buildHints();
    private static final PlayPojo PLAY_BACK_IMG = buildLunchBackgroundImg();
    private static final PlayPojo PLAY_FLOWER = buildFlower();
//    private static final PlayPojo PLAY_LOGO = buildLogoImage();
    private static final PlayPojo PLAY_COLLECT = buildCollect();
//    private static final PlayPojo PLAY_UN_COLLECT = buildUnCollect();

    private static final PlayPojo PLAY_LIST_ITEM = buildContent();

    public static PlayDocument getPlayDocument(String headerName, List<ActorWithWord> aws,
                                               String audio, String botAccount) {
        PlayDocument document = new PlayDocument();
        document.setDuration(110000);
        PlayMainTemplate mainTemplate = new PlayMainTemplate();
        document.setMainTemplate(mainTemplate);

        List<PlayPageData> dataList = new ArrayList<>();
        for (ActorWithWord aw: aws) {
            PlayPageData data = new PlayPageData(aw.getActor(), ScheduleServiceNew.getAsideImgUrl(botAccount, aw.getActor()),
                    aw.getText().replaceAll("<br>","\n"));
            dataList.add(data);
        }

        mainTemplate.setItems(Arrays.asList(PLAY_BACK_IMG, buildLunchHead(correctName(headerName)),
                buildConList(audio, dataList), PLAY_COLLECT, PLAY_FLOWER, PLAY_HINT));
        return document;
    }

    private static PlayPojo buildConList(String audioSrc, List<PlayPageData> data) {
        PlayPojo con = new PlayPojo();
        con.setType("Container");
        con.setMarginTop("50dp");
        con.setHeight("450dp");
        con.setWidth("100%");
        con.setPaddingLeft("45dp");
        con.setPaddingRight("45dp");
        con.setPaddingBottom("100dp");
//        con.setAlignItems("center");
//        con.setJustifyContent("center");

        PlayPojo list = new PlayPojo();
        list.setType("List");
        list.setHeight("100%");
        list.setWidth("100%");
        list.setDirection("vertical");
        list.setMarginBottom("40dp");
//        list.setComponentId("playScroll");
//        Event event = new Event("ScrollToIndex", "playScroll");
//        event.setIndex(data.size());
//        event.setAlign("last");
//        list.setOnLoaded(Collections.singletonList(event));

//        List<Event> events = new ArrayList<>();
//        for (PlayPageData ppd: data) {
//            Event event = new Event("Scroll", "playScroll");
//            event.setDistance("30dp");
//            events.add(event);
//        }
//        list.setOnLoaded(events);
        list.setData(data);
        list.setItems(Collections.singletonList(PLAY_LIST_ITEM));

        PlayPojo audio = new PlayPojo();
        audio.setType("Audio");
        audio.setComponentId("audioCom");
        audio.setLooping("false");
        audio.setAutoplay("true");
        audio.setSrc(audioSrc);
        audio.setOnEnd(Collections.singletonList(new Event("SendEvent","audio")));

        con.setItems(Arrays.asList(list, audio));
        return con;
    }

    private static PlayPojo buildContent() {
        PlayPojo con = new PlayPojo();
        con.setType("Container");
        con.setHeight("100%");
        con.setWidth("100%");
        con.setMarginBottom("35dp");
        con.setFlexDirection("column");

        PlayPojo asideCon = new PlayPojo();
        asideCon.setType("Container");
        asideCon.setFlexDirection("row");
        asideCon.setAlignItems("center");
        asideCon.setItems(Arrays.asList(buildAsideImageINList(), buildAsideText()));

        PlayPojo text = new PlayPojo();
        text.setType("Text");
        text.setComponentId("text");
        text.setFontSize("28dp");
        text.setFontStyle("normal");
        text.setMarginTop("13dp");
        text.setText("${data.text}");

        con.setItems(Arrays.asList(asideCon, text));
        return con;
    }

    private static PlayPojo buildAsideText() {
        PlayPojo text = new PlayPojo();
        text.setType("Text");
        text.setText("${data.asideText}");
        text.setComponentId("asideText");
        text.setHeight("50dp");
        text.setMarginLeft("20dp");
        return text;
    }

    private static PlayPojo buildAsideImageINList() {
        PlayPojo frame = new PlayPojo();
        frame.setType("Frame");
        frame.setWidth("60dp");
        frame.setHeight("60dp");
        frame.setBorderRadius("50dp");

        PlayPojo image = new PlayPojo();
        image.setType("Image");
        image.setComponentId("asideImg");
        image.setSrc("${data.asideImg}");
        frame.setWidth("60dp");
        image.setHeight("60dp");
        image.setScaleType("fitCenter");

        frame.setItems(Collections.singletonList(image));
        return frame;
    }

    public static PlayDocument getPlayDocument(String headerName, String aside, String asideImg,
                                               String audio, String text) {
        PlayDocument document = new PlayDocument();
        document.setDuration(180000);
        PlayMainTemplate mainTemplate = new PlayMainTemplate();
        document.setMainTemplate(mainTemplate);
        PlayPojo collectComponent = PLAY_COLLECT;
        //if (!collected) collectComponent = PLAY_UN_COLLECT;
        mainTemplate.setItems(Arrays.asList(PLAY_BACK_IMG, buildLunchHead(headerName),
                buildAside(aside), buildAsideImage(asideImg), buildWholeContainer(audio, text)
                ,collectComponent, PLAY_FLOWER, PLAY_HINT));
        return document;
    }

    private static PlayPojo buildFlower() {
        PlayPojo backGroundImg = new PlayPojo();
        backGroundImg.setType("Image");
        backGroundImg.setPosition("absolute");
        backGroundImg.setRight("100dp");
        backGroundImg.setTop("20dp");
        backGroundImg.setHeight("24dp");
        backGroundImg.setWidth("24dp");
        backGroundImg.setSrc("https://didi-gz25.heyqiwu.cn/duai/image/flower.png");
        backGroundImg.setOnClick(Collections.singletonList(new Event("SendEvent", "playButton")));
        backGroundImg.setScaleType("fitCenter");

        return backGroundImg;
    }

    private static PlayPojo buildCollect() {
        PlayPojo backGroundImg = new PlayPojo();
        backGroundImg.setType("Image");
        backGroundImg.setPosition("absolute");
        backGroundImg.setRight("30dp");
        backGroundImg.setTop("20dp");
        backGroundImg.setHeight("24dp");
        backGroundImg.setWidth("24dp");
        backGroundImg.setComponentId("playCollect");
        backGroundImg.setSrc("https://didi-gz25.heyqiwu.cn/duai/image/collec.png");
        backGroundImg.setOnClick(Collections.singletonList(new Event("SendEvent", "playButton")));
        backGroundImg.setScaleType("fitCenter");

        return backGroundImg;
    }

    private static PlayPojo buildUnCollect() {
        PlayPojo backGroundImg = new PlayPojo();
        backGroundImg.setType("Image");
        backGroundImg.setPosition("absolute");
        backGroundImg.setRight("30dp");
        backGroundImg.setTop("20dp");
        backGroundImg.setHeight("50dp");
        backGroundImg.setWidth("50dp");
        backGroundImg.setComponentId("playCollect");
        backGroundImg.setSrc("https://didi-gz25.heyqiwu.cn/duai/image/uncollec.png");
        backGroundImg.setOnClick(Collections.singletonList(new Event("SendEvent", "playButton")));
        backGroundImg.setScaleType("fitCenter");

        return backGroundImg;
    }

    private static PlayPojo buildCollectFrame() {
        PlayPojo collectCon = new PlayPojo();
        collectCon.setType("Container");
        collectCon.setLeft("300dp");
        collectCon.setTop("380dp");
        collectCon.setPosition("absolute");

        PlayPojo frame = new PlayPojo();
        frame.setType("Frame");
        frame.setWidth("120dp");
        frame.setHeight("120dp");
        frame.setPaddingTop("20dp");
        frame.setBorderRadius("60dp");
        frame.setBackgroundColor("rgba(0,0,0,0.4)");

        PlayPojo backGroundImg = new PlayPojo();
        backGroundImg.setType("Image");
        backGroundImg.setComponentId("playCollect");
        backGroundImg.setMarginLeft("35dp");
        backGroundImg.setSrc("https://didi-gz25.heyqiwu.cn/duai/image/collec.png");
        backGroundImg.setHeight("50dp");
        backGroundImg.setWidth("50dp");
        backGroundImg.setScaleType("fitCenter");
        backGroundImg.setOnClick(Collections.singletonList(new Event("SendEvent", "playButton")));

//        PlayPojo text = new PlayPojo();
//        text.setType("Text");
//        text.setMarginLeft("22dp");
//        text.setText("收藏作品");
//        text.setFontSize("20dp");

        frame.setItems(Collections.singletonList(backGroundImg));
        collectCon.setItems(Collections.singletonList(frame));
        return collectCon;
    }

    private static PlayPojo buildUnCollectFrame() {
        PlayPojo collectCon = new PlayPojo();
        collectCon.setType("Container");
        collectCon.setLeft("300dp");
        collectCon.setTop("380dp");
        collectCon.setPosition("absolute");

        PlayPojo frame = new PlayPojo();
        frame.setType("Frame");
        frame.setWidth("120dp");
        frame.setHeight("120dp");
        frame.setPaddingTop("20dp");
        frame.setBorderRadius("60dp");
        frame.setBackgroundColor("rgba(0,0,0,0.4)");

        PlayPojo backGroundImg = new PlayPojo();
        backGroundImg.setType("Image");
        backGroundImg.setComponentId("playCollect");
        backGroundImg.setMarginLeft("35dp");
        backGroundImg.setSrc("https://didi-gz25.heyqiwu.cn/duai/image/uncollec.png");
        backGroundImg.setHeight("50dp");
        backGroundImg.setWidth("50dp");
        backGroundImg.setScaleType("fitCenter");
        backGroundImg.setOnClick(Collections.singletonList(new Event("SendEvent", "playButton")));

//        PlayPojo text = new PlayPojo();
//        text.setType("Text");
//        text.setMarginLeft("22dp");
//        text.setText("取消收藏");
//        text.setFontSize("20dp");

        frame.setItems(Collections.singletonList(backGroundImg));
        collectCon.setItems(Collections.singletonList(frame));
        return collectCon;
    }

    private static PlayPojo buildFlowerFrame() {
        PlayPojo flowerCon = new PlayPojo();
        flowerCon.setType("Container");
        flowerCon.setLeft("600dp");
        flowerCon.setTop("380dp");
        flowerCon.setPosition("absolute");

        PlayPojo frame = new PlayPojo();
        frame.setType("Frame");
        frame.setWidth("120dp");
        frame.setHeight("120dp");
        frame.setPaddingTop("20dp");
        frame.setBorderRadius("60dp");
        frame.setBackgroundColor("rgba(0,0,0,0.4)");

        PlayPojo backGroundImg = new PlayPojo();
        backGroundImg.setType("Image");
        backGroundImg.setMarginLeft("35dp");
        backGroundImg.setSrc("https://didi-gz25.heyqiwu.cn/duai/image/flower.png");
        backGroundImg.setHeight("50dp");
        backGroundImg.setWidth("50dp");
        backGroundImg.setScaleType("fitCenter");
        backGroundImg.setOnClick(Collections.singletonList(new Event("SendEvent", "playButton")));

//        PlayPojo text = new PlayPojo();
//        text.setType("Text");
//        text.setMarginLeft("22dp");
//        text.setText("鲜花打赏");
//        text.setFontSize("20dp");

        frame.setItems(Collections.singletonList(backGroundImg));
        flowerCon.setItems(Collections.singletonList(frame));
        return flowerCon;
    }

     private static PlayPojo buildWholeContainer(String audioSrc, String textContent) {
        PlayPojo whole = new PlayPojo();
        whole.setType("Container");
        whole.setHeight("100%");
        whole.setWidth("100%");
        whole.setComponentId("whole");
        whole.setTop("165dp");

        PlayPojo textContainer = new PlayPojo();
        textContainer.setType("Container");
        //textContainer.setPadding("45dp");
        textContainer.setPosition("absolute");
        textContainer.setPaddingLeft("45dp");
        textContainer.setPaddingRight("45dp");
//        textContainer.setAlignItems("center");
//        textContainer.setJustifyContent("center");

        PlayPojo text = new PlayPojo();
        text.setType("Text");
        text.setComponentId("text");
        text.setFontSize("28dp");
        text.setFontStyle("normal");
        text.setPaddingBottom("20dp");
        text.setText(textContent);
        textContainer.setItems(Collections.singletonList(text));

        PlayPojo audio = new PlayPojo();
        audio.setType("Audio");
        audio.setComponentId("audioCom");
        audio.setLooping("false");
        audio.setAutoplay("true");
        audio.setSrc(audioSrc);
        Event onEnd = new Event("SendEvent","audio");
        audio.setOnEnd(Collections.singletonList(onEnd));

        whole.setItems(Arrays.asList(textContainer, audio));
        return whole;
    }

    private static PlayPojo buildLunchBackgroundImg() {
        PlayPojo backGroundImg = new PlayPojo();
        backGroundImg.setType("Image");
        backGroundImg.setPosition("absolute");
        backGroundImg.setTop("0dp");
        backGroundImg.setLeft("0dp");
        backGroundImg.setSrc("https://didi-gz25.heyqiwu.cn/duai/image/backNew.jpg");
        backGroundImg.setHeight("100%");
        backGroundImg.setWidth("100%");
        backGroundImg.setScaleType("fitXY");

        return backGroundImg;
    }

    private static PlayPojo buildLunchHead(String headName) {
        PlayPojo head = new PlayPojo();
        head.setType("Header");
        head.setHeaderTitle(headName);
        head.setHasBackIcon(false);

        return head;
    }

    private static PlayPojo buildLogoImage() {
        PlayPojo logo = new PlayPojo();
        logo.setType("Image");
        logo.setPosition("absolute");
        logo.setSrc("https://didi-gz25.heyqiwu.cn/duai/image/logo.png");
        logo.setWidth("200dp");
        logo.setHeight("58dp");
        logo.setTop("20dp");
        logo.setLeft("10dp");
        return logo;
    }

    private static PlayPojo buildAside(String aside) {
        PlayPojo asideContainer = new PlayPojo();
        asideContainer.setType("Container");
        asideContainer.setPosition("absolute");
        asideContainer.setLeft("130dp");
        asideContainer.setTop("180dp");

//        PlayPojo frame = new PlayPojo();
//        frame.setType("Frame");
//        frame.setPaddingLeft("50dp");
//        frame.setPaddingRight("15dp");
//        frame.setPaddingTop("15dp");
//        frame.setBorderRadius("10dp");
//        frame.setBackgroundColor("rgba(0,0,0,0.4)");

        PlayPojo text = new PlayPojo();
        text.setType("Text");
        text.setText(aside);
        text.setComponentId("asideText");
        text.setHeight("50dp");

//        frame.setItems(Collections.singletonList(text));
//        asideContainer.setItems(Collections.singletonList(frame));
        asideContainer.setItems(Collections.singletonList(text));
        return asideContainer;
    }

    private static PlayPojo buildAsideImage(String src) {
        PlayPojo asideContainer = new PlayPojo();
        asideContainer.setType("Container");
        asideContainer.setPosition("absolute");
        asideContainer.setLeft("45dp");
        asideContainer.setTop("167dp");

        PlayPojo frame = new PlayPojo();
        frame.setType("Frame");
        frame.setWidth("60dp");
        frame.setHeight("60dp");
        frame.setBorderRadius("50dp");

        PlayPojo image = new PlayPojo();
        image.setType("Image");
        image.setComponentId("asideImg");
        image.setSrc(src);
        frame.setWidth("60dp");
        image.setHeight("60dp");
        image.setScaleType("fitCenter");

        frame.setItems(Collections.singletonList(image));
        asideContainer.setItems(Collections.singletonList(frame));
        return asideContainer;
    }

    private static PlayPojo buildHints() {
        PlayPojo footerCon = new PlayPojo();
        footerCon.setType("Container");
        footerCon.setPosition("absolute");
        footerCon.setWidth("100%");
        footerCon.setLeft("0");
        footerCon.setBottom("0");

        PlayPojo footer = new PlayPojo();
        footer.setType("Footer");
        footer.setHints(Arrays.asList("我要送花", "收藏", "退出作品", "重新开始"));

        footerCon.setItems(Collections.singletonList(footer));

        return footerCon;
    }

    private static String correctName(String name) {
        int brace = name.indexOf("{");
        if (brace != -1) name = name.substring(0, brace);
        return name;
    }
}
